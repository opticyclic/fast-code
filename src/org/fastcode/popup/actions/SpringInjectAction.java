/*
 * Fast Code Plugin for Eclipse
 *
 * Copyright (C) 2008  Gautam Dev
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA.
 *
 * Plugin Home Page: http://fast-code.sourceforge.net/
 */

package org.fastcode.popup.actions;

import static org.fastcode.common.FastCodeConstants.DOT_CHAR;
import static org.fastcode.common.FastCodeConstants.KEYWORD_TO_CLASS;
import static org.fastcode.common.FastCodeConstants.KEYWORD_TO_INSTANCE;
import static org.fastcode.util.CreateSimilarDescriptor.getCreateSimilarDescriptor;
import static org.fastcode.util.SourceUtil.getFolderFromPath;
import static org.fastcode.util.StringUtil.createDefaultInstance;
import static org.fastcode.util.StringUtil.replacePlaceHolder;

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IObjectActionDelegate;
import org.fastcode.util.CreateSimilarDescriptor;
import org.fastcode.util.CreateSimilarDescriptorConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class SpringInjectAction extends DepencyInjectAction implements IObjectActionDelegate {

	/**
	 * Constructor for Action.
	 */
	public SpringInjectAction() {
		super();
	}

	/**
	 *
	 * @param typesToInject
	 * @param targetUnit
	 * @throws Exception
	 *
	 */
	@Override
	public void doAfterInjectDependency(final IType[] typesToInject, final ICompilationUnit targetUnit) throws Exception {
		addSpringDepencyInXml(typesToInject, targetUnit);
	}

	/**
	 * @param typeToInject
	 * @param targetUnit
	 *
	 */
	@Override
	public boolean checkIfEligibleForDependency(final IType typeToInject, final ICompilationUnit targetUnit) {

		final CreateSimilarDescriptor descriptor = getCreateSimilarDescriptor(typeToInject);

		if (descriptor == null) {
			return false;
		}

		final String fromPattern = descriptor.getFromPattern();

		String toName = descriptor.getToPattern();

		try {
			toName = replacePlaceHolder(fromPattern, typeToInject.getFullyQualifiedName(), descriptor.getToPattern());
		} catch (final Exception ex1) {
			toName = null;
		}

		if (toName == null) {
			return false;
		}

		final int pos = toName.lastIndexOf(DOT_CHAR);

		if (pos < 0) {
			return false;
		} else {
			final String packge = toName.substring(0, pos);
			//packge += '.' + descriptor.getImplSubPackage();
			if (!targetUnit.findPrimaryType().getPackageFragment().getElementName().equals(packge)) {
				return false;
			}
		}

		return true;
	}

	/**
	 *
	 * @param typesToInject
	 * @param targetUnit
	 * @throws Exception
	 */
	public void addSpringDepencyInXml(final IType[] typesToInject, final ICompilationUnit targetUnit) throws Exception {
		CreateSimilarDescriptorConfig config = null;
		final CreateSimilarDescriptor descriptor = getCreateSimilarDescriptor(typesToInject[0]);

		for (final CreateSimilarDescriptorConfig conf : descriptor.getDescriptorConfigParts()) {
			if (conf.getConfigType().equals("spring")) {
				config = conf;
				break;
			}
		}

		if (config == null) {
			return;
		}

		final String path = config.getConfigLocation();
		final IFolder folder = getFolderFromPath(targetUnit.findPrimaryType().getJavaProject().getProject(), path);
		String configFileName = config.getConfigFileName();
		//configFileName = replaceTokens(config.getConfigStartPattern(), typesToInject[0].getFullyQualifiedName(), configFileName);
		configFileName = replacePlaceHolder(configFileName, KEYWORD_TO_INSTANCE, createDefaultInstance(targetUnit.findPrimaryType()
				.getSuperInterfaceNames()[0]));
		configFileName = replacePlaceHolder(configFileName, KEYWORD_TO_CLASS, targetUnit.findPrimaryType().getSuperInterfaceNames()[0]);
		final IFile file = folder.getFile(configFileName);

		if (file != null && file.exists()) {
			this.fastCodeConsole.writeToConsole("Found file according to configuration : " + configFileName);
			if (updateSpringFile(file, typesToInject, targetUnit)) {
				return;
			}
		}

		this.fastCodeConsole.writeToConsole("Did not find file according to configuration : " + configFileName);
		this.fastCodeConsole.writeToConsole("Scanning folder for match : " + folder.getFullPath().toString());

		for (final IResource resource : folder.members()) {
			if (resource.getType() == IResource.FILE) {
				if (resource.getName().endsWith(".xml")) {
					if (updateSpringFile((IFile) resource, typesToInject, targetUnit)) {
						return;
					}
				}
			}
		}

		throw new Exception("Unable to update spring configuration file either because no "
				+ "configuration file was found or there was something wrong with the file.");
	}

	/**
	 *
	 * @param file
	 * @param typesToInject
	 * @param targetUnit
	 * @return
	 * @throws Exception
	 */
	private boolean updateSpringFile(final IFile file, final IType[] typesToInject, final ICompilationUnit targetUnit) throws Exception {
		InputStream input = file.getContents();

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		final DocumentBuilder docBuilder = factory.newDocumentBuilder();

		final Document document = docBuilder.parse(input);
		final NodeList parentNodeList = document.getElementsByTagName("bean");

		if (parentNodeList.getLength() == 0) {
			return false;
			//throw new Exception("No bean element was found in file : " + file.getName());
		}

		this.fastCodeConsole.writeToConsole("Found bean tag in file : " + file.getName());

		boolean fileModified = false;
		boolean beanFound = false;

		for (int i = 0; i < parentNodeList.getLength(); i++) {
			final Node node = parentNodeList.item(i);
			if (!node.hasAttributes() || node.getAttributes().getNamedItem("class") == null) {
				continue;
			}
			final String classAttr = node.getAttributes().getNamedItem("class").getTextContent();
			if (!classAttr.equals(targetUnit.findPrimaryType().getFullyQualifiedName())) {
				continue;
			}

			beanFound = true;

			for (final IType type : typesToInject) {
				final NodeList nodeList = node.getChildNodes();
				final Element elem = document.createElement("property");
				elem.setAttribute("name", createDefaultInstance(type.getElementName()));
				elem.setAttribute("ref", createDefaultInstance(type.getElementName()));
				boolean propFound = false;
				for (int j = 0; j < nodeList.getLength(); j++) {
					final Node node1 = nodeList.item(j);
					if (node1.getNodeName().equals("property")) {
						if (node1.getAttributes().getNamedItem("name").getTextContent()
								.equals(createDefaultInstance(type.getElementName()))) {
							propFound = true;
							break;
						}
					}
				}

				if (!propFound) {
					fileModified = true;
					node.appendChild(elem);
				}
			}
		}

		if (!beanFound) {
			return false;
		}

		if (!fileModified) {
			return true;
		}

		final StringWriter stringOut = new StringWriter();
		final OutputFormat format = new OutputFormat(document);

		final XMLSerializer serializer = new XMLSerializer(stringOut, format);
		serializer.serialize(document);
		final String buffer = stringOut.toString();
		stringOut.close();
		input.close();
		if (file.exists()) {
			file.delete(false, null);
		}
		try {
			input = new StringBufferInputStream(buffer);
			file.create(input, false, null);
		} catch (final Exception ex) {
			ex.printStackTrace();
			// Do nothing
		} finally {
			if (input != null) {
				input.close();
			}
		}
		return true;
	}
}

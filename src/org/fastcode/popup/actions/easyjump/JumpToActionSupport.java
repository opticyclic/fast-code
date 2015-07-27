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

package org.fastcode.popup.actions.easyjump;

import static org.fastcode.common.FastCodeConstants.ANY_CLASS;
import static org.fastcode.common.FastCodeConstants.ANY_PACKAGE;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_CLASS_NAME_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_PACKAGE_PATTERN;
import static org.fastcode.util.SourceUtil.getElementFromProject;
import static org.fastcode.util.SourceUtil.getResourceFromWorkspace;
import static org.fastcode.util.SourceUtil.getTypeFromWorkspace;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.replacePlaceHolder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.fastcode.AbstractActionSupport;
import org.fastcode.Activator;
import org.fastcode.mapping.MappingDefinition;
import org.fastcode.mapping.MappingDefinitionList;

/**
 * @author Gautam
 *
 */
public abstract class JumpToActionSupport extends AbstractActionSupport {

	/**
	 *
	 * @param compUnit
	 */
	public void run(final ICompilationUnit compUnit) {
		String errorMessage = null;
		final Shell shell = new Shell();

		final String fCompUnitName = compUnit.findPrimaryType().getFullyQualifiedName();
		final String match = findMatch(fCompUnitName);
		if (isEmpty(match)) {
			MessageDialog.openError(shell, "Error", "Cannot find target object for the type selected");
			return;
		}
		IResource resource = null;
		try {
			if (match.endsWith(".xml")) {
				resource = (IResource) getElementFromProject(compUnit.getJavaProject(), match, ".jar");

				if (resource == null || !resource.exists()) {
					resource = getResourceFromWorkspace(match);
				}
				if (resource == null || !resource.exists()) {
					errorMessage = "Could not find the target " + match;
				} else {
					final IFile file = (IFile) resource;
					final IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(file.getName());
					this.page.openEditor(new FileEditorInput(file), desc.getId());
				}
				return;
			} else {
				final IType type = getTypeFromWorkspace(match, ".jar");
				if (type == null) {
					errorMessage = "Could not find the target " + match;
				} else {
					final IEditorPart javaEditor = JavaUI.openInEditor(type.getCompilationUnit());
					JavaUI.revealInEditor(javaEditor, (IJavaElement) type.getCompilationUnit());
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
			errorMessage = e.getMessage();
		}
		if (errorMessage != null) {
			MessageDialog.openError(shell, "Error", "There was some problem : " + errorMessage);
		}
	}

	/**
	 *
	 * @param fCompUnitName
	 * @return
	 */
	protected String findMatch(final String fCompUnitName) {
		//return getCompUnitMap().get(fCompUnitName);
		final MappingDefinitionList mappingDefinitionList = MappingDefinitionList.getInstance();

		final String category = getCategory();

		for (final MappingDefinition mappingDefinition : mappingDefinitionList.getMappingDefinitions()) {
			if (!mappingDefinition.getCategory().equals(category)) {
				continue;
			}
			String source = mappingDefinition.getSource();

			source = replacePlaceHolder(source, ANY_PACKAGE,
					Activator.getDefault().getPluginPreferences().getString(P_GLOBAL_PACKAGE_PATTERN));
			source = replacePlaceHolder(source, ANY_CLASS,
					Activator.getDefault().getPluginPreferences().getString(P_GLOBAL_CLASS_NAME_PATTERN));

			final Pattern p = Pattern.compile(source);
			final Matcher m = p.matcher(fCompUnitName);
			if (!m.matches()) {
				continue;
			}
			String value = mappingDefinition.getDestination();
			for (int i = 1; i <= m.groupCount(); i++) {
				value = value.replaceAll("\\$\\{" + i + "\\}", m.group(i));
			}
			return value;
		}
		return null;
	}

	@Override
	protected boolean doesModify() {
		return false;
	}

	@Override
	protected boolean doesRequireMoreInfoFromUser() {
		return false;
	}

	@Override
	protected IType[] getTypesFromUser(final String title, final String description) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement, final IType[] types) throws Exception {
		// TODO Auto-generated method stub

	}

	protected abstract String getCategory();

}

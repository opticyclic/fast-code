package org.fastcode.templates.util;

import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.DOUBLE_QUOTES;
import static org.fastcode.common.FastCodeConstants.EQUAL;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.fastcode.Activator;
import org.fastcode.templates.contentassist.ElementProposal;
import org.fastcode.templates.contentassist.FunctionProposal;
import org.fastcode.util.FastCodeUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ContentAssistUtil {

	final static Map<String, ArrayList<String>>	eTypeFunctionMap	= new HashMap<String, ArrayList<String>>();

	public static Map<String, ArrayList<ElementProposal>> getReferenceProposals(final String fileName) {

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		InputStream inputStream = null;

		final Map<String, ArrayList<ElementProposal>> eProposalMap = new HashMap<String, ArrayList<ElementProposal>>();
		ArrayList<ElementProposal> eProposalList = null;
		ArrayList<String> efunctionsList = null;

		try {
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + fileName), false);
			final Document document = docBuilder.parse(inputStream);
			final NodeList elementsList = document.getElementsByTagName("element-proposal");

			final int size = elementsList.getLength();
			for (int i = 0; i < size; i++) {
				final Node elementNode = elementsList.item(i);
				final NamedNodeMap elementattributes = elementNode.getAttributes();
				final Node proposal = elementattributes.getNamedItem("proposal");
				final Node prefix = elementattributes.getNamedItem("prefix");
				final Node desc = elementattributes.getNamedItem("description");
				final Node offset = elementattributes.getNamedItem("offset");
				final Node basic = elementattributes.getNamedItem("basic");

				efunctionsList = new ArrayList<String>();
				eProposalList = new ArrayList<ElementProposal>();
				ArrayList<ElementProposal> functionProposal = new ArrayList<ElementProposal>();
				ArrayList<String> functionsList = new ArrayList<String>();
				final ElementProposal elementProposal = new ElementProposal(proposal.getNodeValue(), prefix.getNodeValue(),
						desc.getNodeValue(), Integer.parseInt(offset.getNodeValue()));
				eProposalList.add(elementProposal);

				Node functionNode = elementNode.getFirstChild();

				while (functionNode != null) {

					if (functionNode.getNodeType() == Node.ELEMENT_NODE) {

						functionProposal = createFunctionProposal(functionNode, elementProposal, eProposalList);

						functionsList = createFunctionsList(null, functionNode, efunctionsList);

					}
					functionNode = functionNode.getNextSibling();

				}

				eProposalMap.put(proposal.getNodeValue(), functionProposal);
				eTypeFunctionMap.put(proposal.getNodeValue(), functionsList);

			}

		} catch (final Exception ex) {
			ex.printStackTrace();

		} finally {

			FastCodeUtil.closeInputStream(inputStream);
		}

		return eProposalMap;
	}

	public static void parseElements() {

	}

	public static ArrayList<String> getTypefunctionmap(final String type) {
		if (eTypeFunctionMap.isEmpty()) {
			getReferenceProposals("reference-proposal.xml");
		}

		return eTypeFunctionMap.get(type);
	}

	private static ArrayList<String> createFunctionsList(final String parentProposal, final Node functionNode,
			final ArrayList<String> efunctionsList) {

		final NamedNodeMap functionAttributes = functionNode.getAttributes();
		final Node fproposal = functionAttributes.getNamedItem("proposal");
		final Node fbasic = functionAttributes.getNamedItem("basic");

		if (fbasic.getNodeValue().equals("false")) {

			efunctionsList.add(parentProposal != null ? parentProposal + DOT + fproposal.getNodeValue() : fproposal.getNodeValue());
			Node innerFuncNode = functionNode.getFirstChild();
			while (innerFuncNode != null) {
				if (innerFuncNode.getNodeType() == Node.ELEMENT_NODE) {
					createFunctionsList(fproposal.getNodeValue(), innerFuncNode, efunctionsList);
				}
				innerFuncNode = innerFuncNode.getNextSibling();
			}
		}
		return efunctionsList;

	}

	private static ArrayList<ElementProposal> createFunctionProposal(final Node functionNode, final ElementProposal elementProposal,
			final ArrayList<ElementProposal> eProposalList) {

		final NamedNodeMap functionAttributes = functionNode.getAttributes();
		final Node fproposal = functionAttributes.getNamedItem("proposal");
		final Node fprefix = functionAttributes.getNamedItem("prefix");
		final Node fdesc = functionAttributes.getNamedItem("description");
		final Node foffset = functionAttributes.getNamedItem("offset");
		final Node fbasic = functionAttributes.getNamedItem("basic");

		String proposalName;
		if (fbasic.getNodeValue().equals("true")) {
			proposalName = fproposal.getNodeValue();
		} else {
			proposalName = elementProposal.getProposal() + DOT + fproposal.getNodeValue();
		}

		final FunctionProposal functionProposal = new FunctionProposal(proposalName, fprefix.getNodeValue(), fdesc.getNodeValue(),
				Integer.parseInt(foffset.getNodeValue()));
		functionProposal.addPredecessor(elementProposal);

		eProposalList.add(functionProposal);

		Node innerFuncNode = functionNode.getFirstChild();
		while (innerFuncNode != null) {
			if (innerFuncNode.getNodeType() == Node.ELEMENT_NODE) {
				createFunctionProposal(innerFuncNode, functionProposal, eProposalList);
			}
			innerFuncNode = innerFuncNode.getNextSibling();
		}

		return eProposalList;
	}

	private static ArrayList<ElementProposal> createFuncProposal(final Node functionNode, final ElementProposal elementProposal,
			final ArrayList<ElementProposal> eProposalList) {

		final NamedNodeMap functionAttributes = functionNode.getAttributes();
		final Node fproposal = functionAttributes.getNamedItem("proposal");
		final Node fprefix = functionAttributes.getNamedItem("prefix");
		final Node fdesc = functionAttributes.getNamedItem("description");
		final Node foffset = functionAttributes.getNamedItem("offset");
		final Node fbasic = functionAttributes.getNamedItem("basic");

		final FunctionProposal functionProposal = new FunctionProposal(elementProposal.getProposal() + DOT + fproposal.getNodeValue(),
				fprefix.getNodeValue(), fdesc.getNodeValue(), Integer.parseInt(foffset.getNodeValue()));
		functionProposal.addPredecessor(elementProposal);

		eProposalList.add(functionProposal);

		Node innerFuncNode = functionNode.getFirstChild();
		while (innerFuncNode != null) {
			if (innerFuncNode.getNodeType() == Node.ELEMENT_NODE) {
				createFuncProposal(innerFuncNode, functionProposal, eProposalList);
			}
			innerFuncNode = innerFuncNode.getNextSibling();
		}
		return eProposalList;
	}

	public static ArrayList<ElementProposal> getBasicProposals(final String fileName) {

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		InputStream inputStream = null;
		final ArrayList<ElementProposal> eProposalList = new ArrayList<ElementProposal>();

		try {
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + fileName), false);
			final Document document = docBuilder.parse(inputStream);
			final NodeList elementsList = document.getElementsByTagName("element-proposal");

			final int size = elementsList.getLength();
			for (int i = 0; i < size; i++) {
				final Node elementNode = elementsList.item(i);
				final NamedNodeMap elementattributes = elementNode.getAttributes();
				final Node proposal = elementattributes.getNamedItem("proposal");
				final Node prefix = elementattributes.getNamedItem("prefix");
				final Node desc = elementattributes.getNamedItem("description");
				final Node offset = elementattributes.getNamedItem("offset");
				final Node basic = elementattributes.getNamedItem("basic");
				final String basicProposal = basic.getNodeValue();
				if (basicProposal.equals("true")) {
					final ElementProposal elementProposal = new ElementProposal(proposal.getNodeValue(), prefix.getNodeValue(),
							desc.getNodeValue(), Integer.parseInt(offset.getNodeValue()));
					eProposalList.add(elementProposal);

					Node functionNode = elementNode.getFirstChild();

					while (functionNode != null) {

						if (functionNode.getNodeType() == Node.ELEMENT_NODE) {

							createFuncProposal(functionNode, elementProposal, eProposalList);

						}
						functionNode = functionNode.getNextSibling();

					}

				}

			}

		} catch (final Exception ex) {
			ex.printStackTrace();

		} finally {

			FastCodeUtil.closeInputStream(inputStream);
		}
		return eProposalList;

	}

	public static boolean isWithinQuotes(final IDocument document, final int offset) {

		boolean leftQuoteFound = false;
		boolean rightQuoteFound = false;
		try {
			for (int i = offset; i > 0; i--) {
				if (document.get(i, 1).equals(EQUAL)) {
					break;
				}
				if (document.get(i, 1).equals(DOUBLE_QUOTES)) {
					leftQuoteFound = true;
					break;
				}
			}
			for (int i = offset; i <= document.getLength(); i++) {
				if (document.get(i, 1).equals(RIGHT_PAREN)) {
					break;
				}
				if (document.get(i, 1).equals(DOUBLE_QUOTES)) {
					rightQuoteFound = true;
					break;
				}
			}

			return leftQuoteFound && rightQuoteFound;

		} catch (final BadLocationException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		return false;
	}

}

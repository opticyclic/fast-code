package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.TAB;
import static org.fastcode.common.FastCodeConstants.TRUE_STR;
import static org.fastcode.util.FastCodeUtil.closeInputStream;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.w3c.dom.Node.ELEMENT_NODE;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.ide.ResourceUtil;
import org.fastcode.Activator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UnitTestReturnFormatUtil {

	/**
	 * @param appliesTo
	 * @param returnFormat
	 * @return
	 */
	public Map<String, Map<String, List<UnitTestReturnFormatOption>>> readFromResultFormatFile(final String appliesTo,
			final String returnFormat) {
		InputStream inputStream = null;
		final String unitTestResultFormatFile = "unit-test-result-format.xml";
		try {
			inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + unitTestResultFormatFile),
					false); // ResourceUtil.class.getClassLoader().getResourceAsStream(unitTestResultFormatFile);

			if (inputStream == null) {
				throw new IllegalArgumentException("Invalid file " + unitTestResultFormatFile);
			}
			return getUnitTestReturnFormatSettings(inputStream, appliesTo, returnFormat);

		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (final IOException ex) {
					ex.printStackTrace();
				}

			}
		}
		return null;
	}

	/**
	 *
	 * @param returnFormat
	 * @param appliesTo2
	 * @param store
	 * @param templateStream
	 * @return
	 * @return
	 * @throws Exception
	 */
	private Map<String, Map<String, List<UnitTestReturnFormatOption>>> getUnitTestReturnFormatSettings(
			final InputStream unitTestReturnFormatStream, final String appliesTo, final String returnFormat) throws Exception {

		final UnitTestReturnFormatSettings unitTestResultFormatSettings = UnitTestReturnFormatSettings.getInstance();

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			final Document document = docBuilder.parse(unitTestReturnFormatStream);
			final NodeList parentNodeList = document.getElementsByTagName("result-formats");
			final NodeList resultFormatNodeList = document.getElementsByTagName("result-format");

			for (int k = 0; k <= resultFormatNodeList.getLength(); k++) {
				Node node = resultFormatNodeList.item(k);
				while (node != null) {
					if (node.getNodeType() != ELEMENT_NODE) {
						node = node.getNextSibling();
						continue;
					}
					if ("result-format".equals(node.getNodeName())) {
						final NamedNodeMap attributes = node.getAttributes();
						if (attributes == null || attributes.getLength() == 0) {
							continue;
						}
						// final String appliesTo = null;
						for (int i = 0; i < attributes.getLength(); i++) {
							if ("applies-to".equalsIgnoreCase(attributes.item(i).getNodeName())) {
								if (attributes.item(i).getNodeValue().equals(appliesTo)) {
									final Map<String, List<UnitTestReturnFormatOption>> returnTypeOptionMap = makeReturnFormatSettings(
											node, appliesTo, returnFormat);
									final Map<String, List<UnitTestReturnFormatOption>> returnTypeOptionMapCache = unitTestResultFormatSettings.resultFormatMap
											.get(appliesTo);
									if (returnTypeOptionMapCache == null) {
										unitTestResultFormatSettings.resultFormatMap.put(appliesTo, returnTypeOptionMap);
									} else {
										if (returnTypeOptionMap.containsKey(returnFormat)) {
											returnTypeOptionMapCache.put(returnFormat, returnTypeOptionMap.get(returnFormat));
										}
										unitTestResultFormatSettings.resultFormatMap.put(appliesTo, returnTypeOptionMapCache);

									}

								}
							}
						}
					}
					node = node.getNextSibling();
				}

			}

			System.out.println(unitTestResultFormatSettings.resultFormatMap);
			// unitTestResultFormatSettings.resultFormatMap = resultFormatMap;
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage(), ex);
		} finally {
			// FastCodeUtil.closeInputStream(entityStream);
		}
		return unitTestResultFormatSettings.resultFormatMap;
	}

	/**
	 *
	 * @param returnFormat
	 * @param node
	 * @param templateName
	 * @return
	 */
	private Map<String, List<UnitTestReturnFormatOption>> makeReturnFormatSettings(final Node returnFormatNode, final String appliesTo,
			final String returnFormat) {
		// String returnType = null;

		final Map<String, List<UnitTestReturnFormatOption>> returnTypeOptionMap = new LinkedHashMap<String, List<UnitTestReturnFormatOption>>();

		Node node = returnFormatNode.getFirstChild();

		while (node != null) {
			if (node.getNodeType() != ELEMENT_NODE) {
				node = node.getNextSibling();
				continue;
			}

			if ("format".equals(node.getNodeName())) {
				final NamedNodeMap attributes = node.getAttributes();
				if (attributes == null || attributes.getLength() == 0) {
					continue;
				}

				for (int i = 0; i < attributes.getLength(); i++) {
					if ("return-type".equalsIgnoreCase(attributes.item(i).getNodeName())) {
						final String returnTypeAttributeValue = attributes.item(i).getNodeValue();
						if (returnTypeAttributeValue.equals(returnFormat)) {
							returnTypeOptionMap.put(returnFormat, makeFormatOptionSettings(node, returnFormat));
						} else {
							if (returnTypeAttributeValue.contains(ASTERISK)) {
								final Pattern pattrn = Pattern.compile(returnTypeAttributeValue.replace(ASTERISK, ".*"));
								final Matcher matcher = pattrn.matcher(returnFormat);
								if (matcher.matches()) {
									returnTypeOptionMap.put(returnFormat, makeFormatOptionSettings(node, returnFormat));
								}
							}
						}
					}
				}
			}
			node = node.getNextSibling();
		}

		return returnTypeOptionMap;
	}

	/**
	 * @param formatNode
	 * @param returnType
	 * @return
	 */
	private List<UnitTestReturnFormatOption> makeFormatOptionSettings(final Node formatNode, final String returnType) {
		final List<UnitTestReturnFormatOption> formatOptionsList = new ArrayList<UnitTestReturnFormatOption>();
		UnitTestReturnFormatOption unitTestReturnFormatOption = null;

		Node node = formatNode.getFirstChild();

		while (node != null) {
			if (node.getNodeType() != ELEMENT_NODE) {
				node = node.getNextSibling();
				continue;
			}

			if ("options".equals(node.getNodeName())) {
				final NodeList optionNodeList = node.getChildNodes();
				System.out.println(optionNodeList.getLength());
				for (int i = 0; i <= optionNodeList.getLength(); i++) {
					final Node optionNode = optionNodeList.item(i);
					if (optionNode != null) {
						final NamedNodeMap attributes = optionNode.getAttributes();
						if (attributes == null || attributes.getLength() == 0) {
							continue;
						}

						String optionName = null;
						boolean requireValue = false;
						String valueType = null;
						String methodBody;

						for (int j = 0; j < attributes.getLength(); j++) {
							if ("name".equalsIgnoreCase(attributes.item(j).getNodeName())) {
								optionName = attributes.item(j).getNodeValue();
							} else if ("require-value".equalsIgnoreCase(attributes.item(j).getNodeName())) {
								requireValue = attributes.item(j).getNodeValue().equalsIgnoreCase(TRUE_STR);
							} else if ("value-type".equalsIgnoreCase(attributes.item(j).getNodeName())) {
								valueType = attributes.item(j).getNodeValue();
							}
						}
						methodBody = optionNode.getTextContent();

						unitTestReturnFormatOption = new UnitTestReturnFormatOption(optionName, valueType, requireValue, methodBody);
						formatOptionsList.add(unitTestReturnFormatOption);
					}
				}
			}
			node = node.getNextSibling();
		}

		return formatOptionsList;
	}

	/**
	 * @param inputStream
	 * @return
	 */
	public static String getAllUnitTestResultFormat(final InputStream inputStream) {
		final StringBuffer resultFormatsBuffer = new StringBuffer();
		final String TAB2 = TAB + TAB;

		final UnitTestReturnFormatSettings unitTestResultFormatSettings = UnitTestReturnFormatSettings.getInstance();
		resultFormatsBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		resultFormatsBuffer.append("<result-formats>\n");

		final Map<String, Map<String, List<UnitTestReturnFormatOption>>> resultFormatMap = unitTestResultFormatSettings
				.getResultFormatMap();
		for (final Entry<String, Map<String, List<UnitTestReturnFormatOption>>> returnFormat : resultFormatMap.entrySet()) {
			resultFormatsBuffer.append(TAB + "<result-format applies-to=\"" + returnFormat.getKey() + "\">\n");
			for (final Entry<String, List<UnitTestReturnFormatOption>> formatMap : returnFormat.getValue().entrySet()) {
				resultFormatsBuffer.append(TAB2 + "<format return-type=\"" + formatMap.getKey() + "\">\n");
				resultFormatsBuffer.append(TAB2 + TAB + "<options>\n");
				for (final UnitTestReturnFormatOption formatOption : formatMap.getValue().toArray(new UnitTestReturnFormatOption[0])) {
					resultFormatsBuffer.append(TAB2 + TAB2 + "<option name=\"" + formatOption.getName() + "\"");
					if (!isEmpty(formatOption.getValueType())) {
						resultFormatsBuffer.append(" require-value=\"" + formatOption.getRequireValue() + "\"");
						resultFormatsBuffer.append(" value-type=\"" + formatOption.getValueType() + "\"");
					}
					resultFormatsBuffer.append("\">\n");
					resultFormatsBuffer.append(TAB + "<![CDATA[\n");
					resultFormatsBuffer.append(TAB2 + formatOption.getMethodBody() + "\n");
					resultFormatsBuffer.append(TAB + "]]>\n");
					resultFormatsBuffer.append("</option>");
				}
				resultFormatsBuffer.append("</options>");
			}
			resultFormatsBuffer.append("</result-format>");
		}
		resultFormatsBuffer.append("</result-formats>");
		return resultFormatsBuffer.toString();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		InputStream inputStream = null;
		final String unitTestResultFormatFile = "unit-test-result-format.xml";
		try {
			inputStream = ResourceUtil.class.getClassLoader().getResourceAsStream(unitTestResultFormatFile);
			if (inputStream == null) {
				throw new IllegalArgumentException("Invalid file " + unitTestResultFormatFile);
			}

			// getUnitTestReturnFormatSettings(inputStream);
			final UnitTestReturnFormatUtil formatUtil = new UnitTestReturnFormatUtil();
			System.out.println(formatUtil.getUnitTestReturnFormatSettings(inputStream, "JUNIT_TYPE_4", "int"));

		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

	}

	/**
	 * @param returnType
	 * @return
	 */
	public Map<String, String> getParentFromXml(final String returnType) {
		InputStream inputStream = null;
		String parentInterface = null;
		final String parentChildRelationshipFile = "parent-child.xml";
		try {
			inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + parentChildRelationshipFile),
					false); // ResourceUtil.class.getClassLoader().getResourceAsStream(unitTestResultFormatFile);

			if (inputStream == null) {
				throw new IllegalArgumentException("Invalid file " + parentChildRelationshipFile);
			}
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			final Document document = docBuilder.parse(inputStream);
			final NodeList parentNodeList = document.getElementsByTagName("parent");

			int nodeCount = -1;
			for (int i = 0; i < parentNodeList.getLength(); i++) {
				final NodeList node = parentNodeList.item(i).getChildNodes();

				for (int j = 0; j < node.getLength(); j++) {
					if (node.item(j).getTextContent().endsWith(returnType)) {
						nodeCount = i;
						break;
					}
					if (nodeCount > -1) {
						break;
					}

				}

			}
			if (nodeCount > -1) {
				final NamedNodeMap attr = parentNodeList.item(nodeCount).getAttributes();
				for (int i = 0; i < attr.getLength(); i++) {
					if ("name".equals(attr.item(i).getNodeName())) {
						parentInterface = attr.item(i).getNodeValue();

					}

				}

			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {
			closeInputStream(inputStream);
		}
		final UnitTestReturnFormatSettings unitTestResultFormatSettings = UnitTestReturnFormatSettings.getInstance();
		final String parentCache = unitTestResultFormatSettings.getParentMap().get(returnType);
		if (parentCache == null) {
			if (parentInterface != null) {
				unitTestResultFormatSettings.parentMap.put(returnType, parentInterface);
			}
		} else {
			unitTestResultFormatSettings.parentMap.put(returnType, parentCache);
		}

		return unitTestResultFormatSettings.getParentMap();
	}
}

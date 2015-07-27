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
package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.ATTRIBUTE_ALLOWED_VALUES;
import static org.fastcode.common.FastCodeConstants.ATTRIBUTE_DEPENDSON;
import static org.fastcode.common.FastCodeConstants.ATTRIBUTE_ENABLED;
import static org.fastcode.common.FastCodeConstants.ATTRIBUTE_LABEL;
import static org.fastcode.common.FastCodeConstants.ATTRIBUTE_MAX;
import static org.fastcode.common.FastCodeConstants.ATTRIBUTE_MIN;
import static org.fastcode.common.FastCodeConstants.ATTRIBUTE_PATTERN;
import static org.fastcode.common.FastCodeConstants.ATTRIBUTE_REQUIRED;
import static org.fastcode.common.FastCodeConstants.ATTRIBUTE_TYPE;
import static org.fastcode.common.FastCodeConstants.ATTRIBUTE_VALUE;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.COLON_CHAR;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DEFAULT_TEMPLATE_VARIATION_FIELD;
import static org.fastcode.common.FastCodeConstants.DEFAULT_TEMPLATE_VARIATION_FIELD_NAME;
import static org.fastcode.common.FastCodeConstants.DEFAULT_TEMPLATE_VARIATION_FIELD_VALUE;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.DOT_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.EQUAL;
import static org.fastcode.common.FastCodeConstants.EXCLUDE_FIELDS_FROM_SNIPPETS;
import static org.fastcode.common.FastCodeConstants.HYPHEN;
import static org.fastcode.common.FastCodeConstants.INT;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN_CHAR;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PROJECT;
import static org.fastcode.common.FastCodeConstants.QUOTE_STR;
import static org.fastcode.common.FastCodeConstants.QUOTE_STR_CHAR;
import static org.fastcode.common.FastCodeConstants.RIGHT_CURL;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN_CHAR;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.SPACE_CHAR;
import static org.fastcode.common.FastCodeConstants.STRING_CONSTANT;
import static org.fastcode.common.FastCodeConstants.TAB_CHAR;
import static org.fastcode.common.FastCodeConstants.TEMPLATE_TAG_PREFIX;
import static org.fastcode.common.FastCodeConstants.TODAY;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.common.FastCodeConstants.XML_END;
import static org.fastcode.common.FastCodeConstants.XML_START;
import static org.fastcode.common.FastCodeConstants.ZERO_STRING;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.SourceUtil.getFQNameFromFieldTypeName;
import static org.fastcode.util.SourceUtil.isNativeType;
import static org.w3c.dom.Node.CDATA_SECTION_NODE;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jface.text.ITextSelection;
import org.fastcode.common.FastCodeAdditionalParams;
import org.fastcode.common.FastCodeConstants.RETURN_TYPES;
import org.fastcode.common.FastCodeConstants.TemplateTag;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.Pair;
import org.fastcode.common.XmlElement;
import org.fastcode.exception.FastCodeException;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.templates.util.FcTagAttributes;
import org.fastcode.templates.util.TagAttributeList;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * @author Gautam Dev
 *
 */
public class StringUtil {

	private static Map<String, String> specialTokens = new HashMap<String, String>();
	private static final String[] EXCLUDE_GLOBAL_PROPERTIES = { DEFAULT_TEMPLATE_VARIATION_FIELD, DEFAULT_TEMPLATE_VARIATION_FIELD_NAME,
			DEFAULT_TEMPLATE_VARIATION_FIELD_VALUE, EXCLUDE_FIELDS_FROM_SNIPPETS };
	public static final String[] JAVA_RESERVED_WORDS = { "abstract", "continue", "for", "new", "switch", "assert", "default", "goto",
			"package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw",
			"byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int",
			"short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const",
			"float", "native", "super", "while", "true", "false" };
	static {
		specialTokens.put("\\\"", "\"");
		specialTokens.put("\\n", "\n");
		specialTokens.put("\\t", "\t");
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	public static String createDefaultInstance(final String name) {

		String simpleName = name;
		if (isEmpty(simpleName)) {
			return simpleName;
		}

		if (name.contains("$")) {
			simpleName = name.substring(name.lastIndexOf("$") + 1);
			return simpleName.toLowerCase();
		}
		final int offset = simpleName.lastIndexOf(DOT);
		if (offset != -1) {
			simpleName = simpleName.substring(offset + 1);
		}

		return simpleName.length() > 1 ? Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1) : simpleName.toLowerCase();
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	public static String createEmbeddedInstance(final String name) {
		if (isEmpty(name)) {
			return name;
		}
		return name.length() > 1 ? Character.toUpperCase(name.charAt(0)) + name.substring(1) : name.toUpperCase();
	}

	/**
	 *
	 * @param snppt
	 * @return
	 */
	public static String replaceSpecialChars(final String snppt) {
		String snippet = snppt.replace("\\n", "\n");
		snippet = snippet.replace("\\t", "\t");
		snippet = snippet.replace("\\r", "\r");
		return snippet;
	}

	/**
	 *
	 * @param name
	 * @param names
	 * @return
	 */
	public static boolean isStringInArray(final String name, final String[] names) {
		if (isEmpty(name) || names == null) {
			return false;
		}
		for (final String n : names) {
			if (name.equals(n)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param name
	 * @param names
	 * @return
	 */
	public static boolean isJavaInArray(final String name, final String[] names) {
		if (isEmpty(name) || names == null) {
			return false;
		}
		for (final String n : names) {
			if (n.endsWith(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param objectA
	 * @param objectB
	 * @return
	 */
	public static boolean isObjectsEquals(final Object objectA, final Object objectB) {
		return objectA == objectB || objectA != null && objectA.equals(objectB);
	}

	/**
	 *
	 * @param arrayA
	 * @param arrayB
	 * @return
	 */
	public static boolean isArrayEquals(final String[] arrayA, final String[] arrayB) {
		if (arrayA == null && arrayB == null) {
			return true;
		} else if (arrayA == null || arrayB == null) {
			return false;
		} else if (arrayA.length != arrayB.length) {
			return false;
		} else {
			for (int i = 0; i < arrayA.length; i++) {
				if (arrayA != null && !arrayA[i].trim().equals(arrayB[i])) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 *
	 * @param snippet
	 * @return
	 */
	public static String formatSnippet(final String snippet) {
		final String[] lines = snippet.split(NEWLINE);
		final StringBuilder retStr = new StringBuilder();
		final Pattern pattern = Pattern.compile("(\\s+)(\\S+.*)");
		String whtSpc = null;
		for (final String line : lines) {
			if (isEmpty(line)) {
				continue;
			}
			final Matcher matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				retStr.append(line.trim());
				retStr.append(NEWLINE);
				continue;
			}
			String remainder = line;
			// retStr.append(whtSpc == null ? EMPTY_STR : NEWLINE);

			whtSpc = whtSpc == null ? matcher.group(1) : whtSpc;
			/*
			 * if (whtSpc == null) { whtSpc = matcher.group(1); }
			 */
			if (line.startsWith(whtSpc)) {
				// remainder = line.substring(whtSpc.length()).trim();
				remainder = line.replaceFirst(whtSpc, EMPTY_STR);
			}
			retStr.append(remainder);
			retStr.append(NEWLINE);
		}
		return retStr.toString();
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	public static boolean isEmpty(final String name) {
		return name == null || name.trim().equals(EMPTY_STR);
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	public static String covertToRegex(final String value) {
		return value.replaceAll("\\.", "\\\\.");
	}

	/**
	 *
	 * @param arg
	 * @param placeHolder
	 * @param value
	 * @return
	 */
	public static String replacePlaceHolder(final String arg, final String placeHolder, final String value) {
		return arg.replace(makePlaceHolder(placeHolder), value);
	}

	/**
	 *
	 * This will blank out certain portion. E.g extends ${super_class} will be
	 * blanked if no super_class is present. It will look ahead and gobble up
	 * all the while spaces upto the next keyword.
	 *
	 * @param arg
	 * @param keyWord
	 * @param placeHolder
	 * @param nextKeyWord
	 * @return
	 */
	public static String replacePlaceHolderWithBlank(final String arg, final String keyWord, final String placeHolder,
			final String nextKeyWord) {
		String complPlaceHolder = makePlaceHolder(placeHolder);
		String extraSpaces = EMPTY_STR;
		final int off = arg.indexOf(complPlaceHolder);
		final int end = arg.indexOf(nextKeyWord, off + complPlaceHolder.length()); // find
																					// the
																					// next
																					// place
																					// holder
		if (off != -1 && end != -1) {
			final String tmp = arg.substring(off + complPlaceHolder.length(), end);
			if (tmp != null && tmp.trim().equals(EMPTY_STR)) {
				complPlaceHolder += tmp;
				extraSpaces = tmp;
			}
		}
		complPlaceHolder = "\\$\\{" + placeHolder + "\\}";
		complPlaceHolder += extraSpaces;
		return keyWord == null ? arg.replaceFirst(complPlaceHolder, EMPTY_STR) : arg.replaceFirst(keyWord + "\\s+" + complPlaceHolder,
				EMPTY_STR);
	}

	/**
	 *
	 * @param arg
	 * @param placeHolder
	 * @return
	 */
	public static boolean containsPlaceHolder(final String arg, final String placeHolder) {
		final Pattern p = Pattern.compile(".*" + "\\$\\{" + placeHolder + "\\}" + ".*", Pattern.DOTALL);
		final Matcher m = p.matcher(arg);
		return m.matches();
	}

	/**
	 *
	 * @param arg
	 * @return
	 */
	public static boolean containsAnyPlaceHolder(final String arg) {
		final Pattern p = Pattern.compile(".*" + "\\$\\{[a-zA-Z0-9_.()]+\\}" + ".*", Pattern.DOTALL);
		final Matcher m = p.matcher(arg);
		final boolean b = m.matches();
		return b;

	}

	/**
	 *
	 * @param name
	 * @param namePattern
	 * @return
	 */
	public static boolean doesMatchPattern(final String name, final String namePattern) {

		final String pattern = namePattern.contains(ASTERISK) ? namePattern.replace(ASTERISK, ".*") : namePattern;
		final Pattern p = Pattern.compile(pattern);
		final Matcher m = p.matcher(name);
		return m.matches();
	}

	/**
	 *
	 * @author Gautam
	 * @param name
	 * @param namePatterns
	 * @return
	 */
	public static boolean doesMatchPatterns(final String name, final String namePatterns) {
		final String[] pArr = namePatterns.split("\\s+");
		boolean match = false;
		for (final String p : pArr) {
			if (doesMatchPattern(name, p)) {
				match = true;
				break;
			}
		}
		return match;
	}

	/**
	 *
	 * @param buffer
	 * @return
	 */
	public static boolean isFirstLetterUpperCase(final String buffer) {
		if (isEmpty(buffer)) {
			return false;
		}
		return Character.isUpperCase(buffer.charAt(0));
	}

	/**
	 *
	 * @param buffer
	 * @return
	 */
	public static boolean isAllLettersUpperCase(final String buffer) {
		if (isEmpty(buffer)) {
			return false;
		}
		// return buffer.matches(ALL_LETTER_UPPER_CASE_PATTERN);
		return buffer.toUpperCase().equals(buffer);
	}

	/**
	 *
	 * @param pattern
	 * @param buffer
	 * @param placeHolders
	 * @throws Exception
	 */
	public static void parseTokens(final String pattern, final String buffer, final Map<String, Object> placeHolders) throws Exception {
		final Pattern p = Pattern.compile(pattern);
		final Matcher m = p.matcher(buffer);
		if (!m.matches()) {
			throw new Exception(buffer + " does not match the pattern " + pattern);
		}
		for (int i = 1; i <= m.groupCount(); i++) {
			placeHolders.put(EMPTY_STR + i, m.group(i));
		}
	}

	/**
	 * This method with parse inBuffer for regex in specified in pattern and
	 * replace token (${1}, ${2}, etc) in the target.
	 *
	 * @param pattern
	 * @param inBuffer
	 * @param target
	 * @return
	 * @throws Exception
	 */
	public static String replaceTokens(final String pattern, final String inBuffer, final String target,
			final Map<String, String> placeHolders) throws Exception {
		final Pattern p = Pattern.compile(pattern);
		final Matcher m = p.matcher(inBuffer);
		if (!m.matches()) {
			throw new Exception(inBuffer + " does not match the pattern " + pattern);
		}
		String retBuff = target;
		for (int i = 1; i <= m.groupCount(); i++) {
			placeHolders.put(EMPTY_STR + i, m.group(i));
			retBuff = replacePlaceHolder(retBuff, EMPTY_STR + i, m.group(i));
		}

		return retBuff;
	}

	/**
	 *
	 * @param pattern
	 * @param placeHolders
	 * @return
	 * @throws Exception
	 */
	public static String replacePlaceHolders(final String pattern, final Map<String, Object> placeHolders) throws Exception {
		String retBuff = pattern;
		for (final Entry<String, Object> entry : placeHolders.entrySet()) {
			final Object val = entry.getValue();
			if (val instanceof String) {
				retBuff = replacePlaceHolder(retBuff, entry.getKey(), EMPTY_STR + val);
			}
		}
		if (containsPlaceHolder(retBuff, "input")) {
			getGlobalSettings(placeHolders);
			retBuff = evaluateByVelocity(retBuff, placeHolders);
		}
		return retBuff;
	}

	/**
	 * This method finds a match ignoring multiple white spaces between words.
	 * "I am here" will match "I     am    here", but not "I   a  m   here"
	 *
	 * @param container
	 * @param buffer
	 */
	public static String findMatch(final String container, final String buffer) {
		int start = container.indexOf(buffer);
		// First find the exact match
		if (start > -1) {
			return container.substring(start, start + buffer.length());
		}
		final String containerTrm = container.replaceAll("\\s+", SPACE);
		final String bufferTrm = buffer.replaceAll("\\s+", SPACE);
		start = container.indexOf(bufferTrm);
		if (start == -1) {
			return null;
		}
		return containerTrm.substring(start, start + bufferTrm.length());
	}

	/**
	 * Changes to camel case. It will change ProductType to product_type.
	 *
	 * @param buffer
	 * @param separator
	 * @return
	 */
	public static String changeToCamelCase(final String buffer, final char separator) {
		final StringBuffer retBuffer = new StringBuffer();
		boolean firstUpperCase = true;

		for (int i = 0; i < buffer.length(); i++) {
			final char c = buffer.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i > 0 && Character.isLetterOrDigit(buffer.charAt(i - 1)) && firstUpperCase) {
					retBuffer.append(EMPTY_STR + separator + Character.toLowerCase(c));
				} else {
					retBuffer.append(EMPTY_STR + Character.toLowerCase(c));
				}
				firstUpperCase = false;
			} else {
				firstUpperCase = true;
				retBuffer.append(c);
			}
		}
		return retBuffer.toString();
	}

	/**
	 * Changes to reverse camel case. It will change PRODUCT_TYPE to
	 * ProductType.
	 *
	 * @param buffer
	 * @param separator
	 * @return
	 */
	public static String reverseCamelCase(final String buffer, final char separator) {
		final StringBuffer retBuffer = new StringBuffer();
		boolean firstUpperCase = true;

		for (int i = 0; i < buffer.length(); i++) {
			final char c = buffer.charAt(i);
			if (c == separator) {
				firstUpperCase = true;
			} else {
				if (i == 0 || Character.isLetterOrDigit(c) && firstUpperCase) {
					retBuffer.append(EMPTY_STR + Character.toUpperCase(c));
				} else {
					retBuffer.append(EMPTY_STR + Character.toLowerCase(c));
				}
				firstUpperCase = false;
			}
		}
		return retBuffer.toString();
	}

	/**
	 *
	 */

	public static void getGlobalSettings(final Map<String, Object> placeHolders) {
		final GlobalSettings globalSettings = getInstance();

		for (final Object key : globalSettings.getProperties().keySet()) {
			if (!isStringInArray((String) key, EXCLUDE_GLOBAL_PROPERTIES)) {
				placeHolders.put((String) key, globalSettings.getPropertyValue((String) key, EMPTY_STR));
			}
		}

		placeHolders.put("user", globalSettings.getUser());
		final SimpleDateFormat dateFormat = new SimpleDateFormat(globalSettings.getDateFormat());
		final String currDate = dateFormat.format(new Date());
		placeHolders.put(TODAY, currDate);

	}

	/**
	 *
	 * @param template
	 * @param placeHolders
	 * @return
	 * @throws Exception
	 */
	public static String evaluateByVelocity(final String template, final Map<String, ?>... placeHolders) throws Exception {

		final StringWriter writer = new StringWriter();

		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(VelocityUtil.class.getClassLoader());
			Velocity.init();
			final VelocityContext context = new VelocityContext();
			// context.put("esc", new EscapeTool());
			if (placeHolders != null) {
				for (final Map plh : placeHolders) {
					copyIntoContext(context, plh);
				}
			}
			Velocity.evaluate(context, writer, "LOG", template);
			return writer.getBuffer().toString();
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex);
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
			writer.close();
		}
	}

	/**
	 *
	 * @param context
	 * @param placeHolders
	 */
	private static void copyIntoContext(final VelocityContext context, final Map<String, ?> placeHolders) {
		for (final Entry<String, ?> entry : placeHolders.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * It will find the pattern in the container for xml documents. rootTag is
	 * the start tag where we should start looking, e.g in we should start
	 * looking from <struts-config>/<action-mappings> for action-mappings. For
	 * sping configuration files it would be just <beans>.
	 *
	 * @param pattern
	 * @param container
	 * @param rootTag
	 * @return
	 * @throws Exception
	 */
	public static boolean containsXmlStructure(final String pattern, final String rootTag, final String container) throws Exception {
		final XmlElement xmlElement = parseXml(pattern);
		return containsXmlStructure(xmlElement, rootTag, container);
	}

	/**
	 *
	 * @param xmlElement
	 * @param rootTag
	 * @param container
	 * @return
	 * @throws Exception
	 */
	public static boolean containsXmlStructure(final XmlElement xmlElement, final String rootTag, final String container) throws Exception {
		final Document document = getXmlDocument(container);

		// First get series of tags.
		final String[] tagsArr = cleanStringArray(rootTag.split("<|>|>\\s*<"));

		final Node startNode = findNode(document, tagsArr);
		if (startNode == null) {
			return false;
		}

		final NodeList childNodes = startNode.getChildNodes();
		if (childNodes == null || childNodes.getLength() == 0) {
			return false;
		}

		for (int i = 0; i < childNodes.getLength(); i++) {
			final Node childNode = childNodes.item(i);
			if (!childNode.getNodeName().equals(xmlElement.getTagName())) {
				continue;
			}
			if (checkAllAttributesMatches(childNode, xmlElement)) {
				return true;
			}
			// if (found && numAttributes == xmlElement.getAttributes().size())
			// {
		}
		return false;
	}

	/**
	 *
	 * @param nodeName
	 * @param attrName
	 * @param container
	 * @return
	 * @throws Exception
	 */
	public static String[] findAttributes(final String nodeName, final String attrName, final String container) throws Exception {
		final Document document = getXmlDocument(container);
		final NodeList nodes = document.getElementsByTagName(nodeName);

		final List<String> attrVals = new ArrayList<String>();
		for (int i = 0; i < nodes.getLength(); i++) {
			final Node node = nodes.item(i);
			final String attrVal = getAttributeValue(node, attrName);
			if (attrVal != null) {
				attrVals.add(attrVal);
			}
		}
		return attrVals.isEmpty() ? new String[0] : attrVals.toArray(new String[0]);
	}

	/**
	 *
	 * @param item
	 * @param attrName
	 * @return
	 */
	private static String getAttributeValue(final Node node, final String attrName) {
		final NamedNodeMap attributes = node.getAttributes();

		for (int j = 0; j < attributes.getLength(); j++) {
			final Node attr = attributes.item(j);
			if (attrName.equals(attr.getNodeName())) {
				return attr.getNodeValue();
			}
		}
		return null;
	}

	/**
	 *
	 * @param nodeName
	 * @param attrName
	 * @param attrVal
	 * @return
	 * @throws Exception
	 */
	public static int findNodePosition(final String rootNode, final String nodeName, final String container, final String attrName,
			final String attrVal) throws Exception {
		final Document document = getXmlDocument(container);
		final NodeList rootNd = document.getElementsByTagName(rootNode);
		if (rootNd == null || rootNd.getLength() == 0) {
			return -1;
		}
		final NodeList nodes = rootNd.item(0).getChildNodes();
		int count = 0;
		for (int i = 0; i < nodes.getLength(); i++) {
			final Node node = nodes.item(i);
			if (node.getNodeType() == ELEMENT_NODE && node.getNodeName().equals(nodeName)) {
				if (attrVal.equals(getAttributeValue(node, attrName))) {
					return count;
				}
				count++;
			}
		}
		return -1;
	}

	/**
	 *
	 * @param container
	 * @return
	 * @throws SAXException
	 */
	public static Document getXmlDocument(final String container) throws Exception {
		final InputStream inputStream = new ByteArrayInputStream(container.getBytes());

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		final DocumentBuilder docBuilder = factory.newDocumentBuilder();

		return docBuilder.parse(inputStream);
	}

	/**
	 *
	 * @param startNode
	 * @param tags
	 * @return
	 */
	public static Node findNode(final Node startNode, final String[] tags) {
		Node retNode = startNode;
		boolean found = false;
		for (String tag : tags) {
			found = false;
			// NodeList root =
			// document.getElementsByTagName(tagsArr[tagsArr.length - 1]);
			tag = tag.trim();

			final int start = tag.indexOf(SPACE);
			if (start > 0) { // if it has attributes
				tag = tag.substring(0, start);
			}
			final NodeList rootList = startNode.getChildNodes();
			if (rootList == null || rootList.getLength() == 0) {
				return null;
			}
			for (int i = 0; i < rootList.getLength(); i++) {
				retNode = rootList.item(i);
				if (retNode.getNodeType() == ELEMENT_NODE && retNode.getNodeName().equals(tag)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return null;
			}
		}
		return retNode;
	}

	/**
	 *
	 * @param node
	 * @param xmlElement
	 * @return
	 */
	public static boolean checkAllAttributesMatches(final Node node, final XmlElement xmlElement) {
		final NamedNodeMap attributes = node.getAttributes();
		if (attributes.getLength() != xmlElement.getAttributes().size()) {
			// continue;
		}

		for (int j = 0; j < attributes.getLength(); j++) {
			final Node attr = attributes.item(j);
			final String attrName = attr.getNodeName();
			final String attrValue = attr.getNodeValue();
			if (attrValue == null || attrValue.equals("default")) {
				continue;
			}
			boolean attributeMatched = false;
			for (final Pair<String, String> pair : xmlElement.getAttributes()) {
				if (pair.getLeft().equals(attrName) && pair.getRight().equals(attrValue)) {
					attributeMatched = true;
					break;
				}
			}
			if (!attributeMatched) {
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 * @param xmlStr
	 * @return
	 * @throws Exception
	 */
	public static String formatXmlWithCDATA(final String xmlStr) throws Exception {
		final InputStream inputStream = new ByteArrayInputStream(xmlStr.getBytes());
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder docBuilder = factory.newDocumentBuilder();
		final Document document = docBuilder.parse(inputStream);
		return formatXmlNode(document.getFirstChild());
	}

	/**
	 *
	 * @param xmlStr
	 * @return
	 * @throws Exception
	 */
	public static String formatXml(final String xmlStr) throws Exception {
		// Source xmlInput = new StreamSource(new StringReader(xmlStr));
		// StringWriter stringWriter = new StringWriter();
		// StreamResult xmlOutput = new StreamResult(stringWriter);
		// Transformer transformer =
		// TransformerFactory.newInstance().newTransformer();
		// transformer.setOutputProperty(OutputKeys.INDENT, "Yes");
		// transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount",
		// String.valueOf(2));
		// transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS,
		// "query");
		// transformer.transform(xmlInput, xmlOutput);

		final InputStream inputStream = new ByteArrayInputStream(xmlStr.getBytes());
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder docBuilder = factory.newDocumentBuilder();
		final Document document = docBuilder.parse(inputStream);

		final OutputFormat format = new OutputFormat(document);
		format.setOmitXMLDeclaration(true);
		format.setLineWidth(65);
		format.setIndenting(true);
		format.setIndent(2);
		final Writer out = new StringWriter();
		final XMLSerializer serializer = new XMLSerializer(out, format);
		serializer.serialize(document);
		return out.toString();
	}

	/**
	 *
	 * @param xmlStr
	 * @return
	 */
	public static boolean isValidXml(final String xmlStr) {
		final InputStream inputStream = new ByteArrayInputStream(xmlStr.getBytes());
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = factory.newDocumentBuilder();
			final Document document = docBuilder.parse(inputStream);
		} catch (final Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 *
	 * @param xmlStr
	 * @return
	 * @throws Exception
	 */
	public static String formatXmlNode(final Node xmlNode) throws Exception {
		final StringBuilder xmlStr = new StringBuilder();
		final String paddingSpaces = "    ";

		if (xmlNode.getNodeType() == CDATA_SECTION_NODE) {
			return "<![CDATA[\n" + paddingSpaces + xmlNode.getNodeValue().trim() + "\n]]>";
		} else if (xmlNode.getNodeType() == TEXT_NODE) {
			return paddingSpaces + xmlNode.getTextContent().trim();
		}
		final StringBuilder attrs = new StringBuilder();

		final NamedNodeMap attributes = xmlNode.getAttributes();

		if (attributes != null) {
			for (int i = 0; i < attributes.getLength(); i++) {
				final Node attr = attributes.item(i);
				attrs.append(attr.getNodeName() + "=" + "\"" + attr.getNodeValue() + "\"" + SPACE);
			}
		}
		attrs.trimToSize();
		if (!EMPTY_STR.equals(attrs.toString().trim())) {
			xmlStr.append(paddingSpaces + "<" + xmlNode.getNodeName() + SPACE + attrs.toString() + ">" + NEWLINE);
		} else {
			xmlStr.append(paddingSpaces + "<" + xmlNode.getNodeName() + ">" + NEWLINE);
		}

		final NodeList childNodes = xmlNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			final short nodeType = childNodes.item(i).getNodeType();
			if (nodeType != ELEMENT_NODE && nodeType != CDATA_SECTION_NODE && nodeType != TEXT_NODE) {
				continue;
			}
			final String chldXmlStr = formatXmlNode(childNodes.item(i));
			final String[] lines = chldXmlStr.split(NEWLINE);
			for (final String line : lines) {
				if (!isEmpty(line)) {
					xmlStr.append(paddingSpaces + line + NEWLINE);
				}
			}
		}
		xmlStr.append(paddingSpaces + "</" + xmlNode.getNodeName() + ">" + NEWLINE);
		xmlStr.trimToSize();
		return xmlStr.toString();
	}

	/**
	 *
	 * @param content
	 * @return
	 */
	public static String format(final String content, final String spacesBeforeCursor) {
		final StringBuilder formatString = new StringBuilder();
		final String[] lines = content.split(NEWLINE);

		for (final String line : lines) {
			if (isEmpty(spacesBeforeCursor)) {
				formatString.append((EMPTY_STR.equals(formatString.toString()) ? EMPTY_STR : spacesBeforeCursor) + line.trim() + NEWLINE);
			} else {
				formatString.append(line.trim() + NEWLINE);
			}
		}
		return formatString.toString();
	}

	/**
	 * This method will replace the complete line containing a tag with empty
	 * string. This will make the whole line disappear.
	 *
	 * @param body
	 * @param tag
	 * @return
	 * @throws Exception
	 */
	public static String resetLineWithTag(final String body, final String tag) throws Exception {
		final String fulltag = makePlaceHolder(tag);
		final int off = body.indexOf(fulltag);
		if (off < 0) {
			return body;
		}
		int start = body.lastIndexOf(NEWLINE, off);
		final int end = body.indexOf(NEWLINE, off);
		if (start < 0) {
			start = 0;
		}
		if (end != -1) {
			final String repStr = body.substring(start + 1, end + 1);
			return body.replace(repStr, EMPTY_STR);
		}
		return body;
	}

	/**
	 * Removes empty string from the array and return a string array
	 *
	 * @param args
	 * @return
	 */
	public static String[] cleanStringArray(final String[] args) {
		final List<String> retStr = new ArrayList<String>();
		if (args == null) {
			return retStr.toArray(new String[0]);
		}
		for (final String str : args) {
			if (str != null && !str.trim().equals(EMPTY_STR)) {
				retStr.add(str.trim());
			}
		}
		return retStr.toArray(new String[0]);
	}

	/**
	 *
	 * @param xmlSource
	 * @return
	 * @throws Exception
	 */
	public static XmlElement parseXml(final String xmlSource) throws Exception {
		final InputStream input = new StringBufferInputStream(xmlSource);
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		final DocumentBuilder docBuilder = factory.newDocumentBuilder();

		final Document document = docBuilder.parse(input);

		final String tagName = document.getDocumentElement().getTagName();
		final XmlElement xmlElement = new XmlElement(tagName);
		final NamedNodeMap attributes = document.getDocumentElement().getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			final Node item = attributes.item(i);
			final String nodeName = item.getNodeName();
			final String nodeValue = item.getNodeValue();
			xmlElement.addAttribute(nodeName, nodeValue);
		}
		return xmlElement;
	}

	/**
	 *
	 * @param buffer
	 * @return
	 */
	public static String singularize(final String buffer) {
		String singular = null;
		if (buffer == null) {
			return singular;
		}
		final int length = buffer.length();
		if (buffer.endsWith("ies")) {
			singular = buffer.substring(0, length - 3);
		} else if (buffer.endsWith("es")) {
			singular = buffer.substring(0, length - 2);
		} else if (buffer.endsWith("s")) {
			singular = buffer.substring(0, length - 1);
		}
		return singular;
	}

	/**
	 *
	 * @param buffer
	 * @param separator
	 * @return
	 */
	public static String changeToPlural(final String buffer) {
		String plural = null;
		if (buffer == null) {
			return plural;
		}
		if (buffer.endsWith("y")) {
			plural = buffer.replaceAll("y$", "ies");
		} else if (buffer.endsWith("s")) {
			plural = buffer.replaceAll("s$", "es");
		} else {
			plural = buffer + "s";
		}
		return plural;
	}

	/**
	 *
	 * @param dateFormat
	 * @return
	 */
	public static String computeDate(final String dateFormat) {
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		return simpleDateFormat.format(new Date());
	}

	/**
	 * This method will parse parameterized types e.g. List<String>,
	 * List<List<String>>, Map<String, Date>, Map<String, List<Date>>,
	 * List<List<Map<String, List<Date>>>>, Map<Map<String, List<String>>,
	 * Map<String, Date>>> etc.
	 *
	 * @param type
	 * @return
	 */
	/*
	 * public static FastCodeType parseType(final String type) {
	 *
	 * final int i = type.indexOf('<'); if (i < 0) { return new
	 * FastCodeType(type); } final int j = type.lastIndexOf('>'); if (j < 0) {
	 * return new FastCodeType(type); } final FastCodeType fastCodeType = new
	 * FastCodeType(type); final String params = type.substring(i + 1, j); final
	 * String[] paramsArr = parseParamString(params);
	 *
	 * for (final String param : paramsArr) { if (param.contains("<")) {
	 * fastCodeType.addParameters(parseType(param.trim())); } else {
	 * fastCodeType.addParameters(new FastCodeType(type)); } } return
	 * fastCodeType; }
	 */
	/**
	 * This method will parse parameterized types e.g. List<String>,
	 * List<List<String>>, Map<String, Date>, Map<String, List<Date>>,
	 * List<List<Map<String, List<Date>>>>, Map<Map<String, List<String>>,
	 * Map<String, Date>>> etc.
	 *
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static FastCodeType parseType(String returnType, final ICompilationUnit compilationUnit) throws Exception {
		boolean isArray = false;
		FastCodeType fastCodeType = null;
		if (returnType.indexOf("[") > -1) {
			returnType = returnType.substring(0, returnType.indexOf("["));
			isArray = true;
		}
		if (isNativeType(returnType) || compilationUnit == null) {
			fastCodeType = new FastCodeType(returnType);
			if (isArray) {
				fastCodeType.setName(fastCodeType.getName() + "[]");
			}
			return fastCodeType;
		}
		if (!returnType.contains(DOT)) {
			returnType = getFQNameFromFieldTypeName(returnType, compilationUnit);
		}
		final IJavaProject javaProject = compilationUnit != null ? compilationUnit.getJavaProject() : null;
		IType type = javaProject.findType(returnType);
		final int i = returnType.indexOf(XML_START);
		if (i < 0) {
			fastCodeType = new FastCodeType(returnType);
			if (isArray) {
				fastCodeType.setName(fastCodeType.getName() + "[]");
			}
			return fastCodeType;
		}
		final int j = returnType.lastIndexOf(XML_END);
		if (j < 0) {
			fastCodeType = new FastCodeType(returnType);
			if (isArray) {
				fastCodeType.setName(fastCodeType.getName() + "[]");
			}
			return fastCodeType;
		}
		type = javaProject.findType(getFQNameFromFieldTypeName(returnType.substring(0, i), compilationUnit));
		final FastCodeType codeType = new FastCodeType(type);
		final String params = returnType.substring(i + 1, j);
		final String[] paramsArr = parseParamString(params);

		for (final String param : paramsArr) {
			if (param.contains(XML_START)) {
				codeType.addParameters(parseType(param.trim(), compilationUnit));
			} else {
				codeType.addParameters(new FastCodeType(getFQNameFromFieldTypeName(param, compilationUnit)));
			}
		}
		return codeType;
	}

	/**
	 *
	 * @param fastCodeType
	 * @return
	 */
	public static String flattenType(final FastCodeType fastCodeType, final boolean considerFQName) {
		String retString = fastCodeType.getName();
		if (considerFQName) {
			retString = fastCodeType.getFullyQualifiedName();
		}
		final boolean hasParms = fastCodeType.getParameters() != null && !fastCodeType.getParameters().isEmpty();
		final StringBuilder params = new StringBuilder();
		for (final FastCodeType codeType : fastCodeType.getParameters()) {
			params.append(flattenType(codeType, considerFQName));
			if (fastCodeType.getParameters().indexOf(codeType) < fastCodeType.getParameters().size() - 1) {
				params.append(COMMA);
			}
		}
		if (hasParms) {
			retString += "<" + params.toString() + ">";
		}
		return retString;
	}

	/**
	 *
	 * @param selection
	 * @param source
	 * @return
	 * @throws Exception
	 */
	public static boolean isOnEmpltyLine(final ITextSelection selection, final String source) throws Exception {
		if (selection.getLength() > 0) {
			return false;
		}

		final int startLine = selection.getStartLine();
		final int end = selection.getEndLine();
		if (end > startLine) {
			return false;
		}

		// int endOfCurrentLine = source.indexOf(NEWLINE,
		// selection.getOffset());
		// int endOfCurrentLine = source.indexOf(LINEFEED,
		// selection.getOffset());

		int i = 0;
		String currLine = EMPTY_STR;
		for (final String line : source.split(NEWLINE)) {
			if (i++ == startLine) {
				currLine = line;
				break;
			}
		}

		return currLine.trim().equals(EMPTY_STR);
		// if (!source.substring(selection.getOffset(),
		// endOfCurrentLine).trim().equals(EMPTY_STR)) {
		// return false;
		// }
		//
		// int beginOfCurrentLine = source.lastIndexOf(NEWLINE,
		// selection.getOffset());
		// if (!source.substring(beginOfCurrentLine,
		// selection.getOffset()).trim().equals(EMPTY_STR)) {
		// return false;
		// }
		//
		// return true;
	}

	/**
	 *
	 * @param strArr
	 * @param placeHolders
	 * @return
	 * @throws Exception
	 */
	public static String[] replacePlaceHolders(final String[] strArr, final Map<String, Object> placeHolders) throws Exception {
		if (strArr == null || strArr.length == 0) {
			return strArr;
		}
		for (int i = 0; i < strArr.length; i++) {
			if (strArr[i] != null) {
				strArr[i] = replacePlaceHolders(strArr[i], placeHolders);
			}
		}
		return strArr;
	}

	/**
	 * @param params
	 */
	private static String[] parseParamString(final String params) {
		final char[] parArr = params.toCharArray();
		final List<String> paramsList = new ArrayList<String>();

		int count = 0, start = 0, index = 0;
		for (final char c : parArr) {
			if (c == '<') {
				count++;
			} else if (c == '>') {
				count--;
			}

			if (c == COMMA.charAt(0) && count == 0) {
				final int length = index - start;
				final char[] buf = new char[length];
				System.arraycopy(parArr, start, buf, 0, length);
				paramsList.add(new String(buf));
				start = index + 1;
			}
			index++;
		}
		final int length = index - start;
		if (length > 0) {
			final char[] buf = new char[length];
			System.arraycopy(parArr, start, buf, 0, length);
			paramsList.add(new String(buf));
		}

		return paramsList.toArray(new String[0]);
	}

	/**
	 *
	 * @param arg
	 * @return
	 */
	public static String makeWord(final String arg) {
		if (isEmpty(arg)) {
			return arg;
		}
		final String arg1 = arg.replaceAll(UNDERSCORE, SPACE);
		final String[] words = arg1.split(SPACE);
		final StringBuilder word = new StringBuilder();
		for (final String w : words) {
			if (isEmpty(w)) {
				continue;
			}
			word.append(w.substring(0, 1).toUpperCase() + w.substring(1).toLowerCase()).append(SPACE);
		}
		return word.toString().trim();
	}

	/**
	 *
	 * @param methodSrc
	 * @return
	 */
	public static String parseMethodName(final String methodSrc) {
		/*final Pattern pattern = Pattern.compile(
				".*(public|protected|private|private static|public static|protected static)\\s+.* \\s*([a-z0-9_]*)\\s*\\([a-z0-9_,\\[\\] ]*\\).*\\{.*\\}\\s*", Pattern.CASE_INSENSITIVE
						| Pattern.DOTALL | Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(methodSrc);
		if (!matcher.matches()) {
			return null;
		}
		System.out.println(matcher.group(0));
		System.out.println(matcher.group(1));
		return matcher.group(2);*/
		final String classPrefix = "public class myclass {\n";
		final String fullClass = classPrefix + methodSrc + "}";
		final ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(fullClass.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		final IJavaElement tree = cu.getJavaElement();
		final FastCodeVisitor methodVisitor = new FastCodeVisitor(cu);
		cu.accept(methodVisitor);
		for (final MethodDeclaration method : methodVisitor.getMethods()) {
			System.out.print("Method name: " + method.getName() + " Return type: " + method.getReturnType2());
			return method.getName().toString();
		}

		return null;
	}

	/**
	 *
	 * @param classSrc
	 * @return
	 */
	public static String parseClassName(final String classSrc) {
		final Pattern pattern = Pattern
				.compile(
						".*public\\s*([abstract]*)\\s*(class|interface)\\s*([a-z0-9_]*)\\s*([extends|implements]*)\\s*([a-z0-9_<>]*)\\s*([extends|implements]*)\\s*([a-z0-9_,\\s<>]*)\\s*\\{.*\\}\\s*",
						Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(classSrc);
		if (!matcher.matches()) {
			return null;
		}
		return matcher.group(3);
	}

	/**
	 *
	 * @param fieldSrc
	 * @return
	 */
	public static String parseFieldName(final String fieldSrc) {
		/*final Pattern pattern = Pattern.compile(".*\\s*[()a-z0-9_,\"=]*\\s*private\\s+(\\w*)\\s+([a-z_$][\\w$]*);",
				Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
		final Matcher matcher = pattern.matcher(fieldSrc);
		System.out.println(matcher.groupCount());

		if (!matcher.matches()) {
			return null;
		}
		return matcher.group(2);*/
		final String classPrefix = "public class myclass {\n";
		final String fullClass = classPrefix + fieldSrc + "}";
		final ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(fullClass.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		//final IJavaElement tree =
		cu.accept(new ASTVisitor() {

		}); //getJavaElement();
		final FastCodeVisitor fieldVisitor = new FastCodeVisitor(cu);
		cu.accept(fieldVisitor);
		for (final FieldDeclaration field : fieldVisitor.getFields()) {
			System.out.println(((VariableDeclarationFragment) field.fragments().get(0)).getName().toString());
			return ((VariableDeclarationFragment) field.fragments().get(0)).getName().toString();
			/*System.out.print("Method name: " + field.getName());
			return field.getName().toString();*/
		}

		return null;
	}

	/**
	 *
	 * @param arg
	 * @return
	 */
	public static String makePlaceHolder(final String arg) {
		return "${" + arg + "}";
	}

	/**
	 *
	 * @param value
	 * @param choices
	 * @return
	 */
	public static int findInStringArray(final String choice, final String[] choices) {
		if (choice == null || choices == null || choices.length == 0) {
			return -1;
		}
		for (int i = 0; i < choices.length; i++) {
			if (choice.equals(choices[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param value
	 * @return
	 */
	public static String changeFirstLetterToUpperCase(final String value) {
		return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
	}

	/**
	 * @param value
	 * @return
	 */
	public static String changeFirstLetterToLowerCase(final String value) {
		return value.substring(0, 1).toLowerCase() + value.substring(1);
	}

	/**
	 * @param fullyQualifiedClassName
	 * @return
	 */
	public static String getClsNmeFromFQClsNme(final String fullyQualifiedClassName) {
		return fullyQualifiedClassName.substring(fullyQualifiedClassName.lastIndexOf(DOT) + 1, fullyQualifiedClassName.length());
	}

	/**
	 * @param varName
	 * @return
	 */
	public static boolean isValidVariableName(final String varName) {
		return varName.matches("[a-zA-Z][a-zA-Z0-9_]*");
	}

	/**
	 * @param varName
	 * @return
	 */
	public static boolean isJavaReservedWord(final String varName) {
		for (final String resWord : JAVA_RESERVED_WORDS) {
			if (varName.equals(resWord)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param varName
	 * @return
	 */
	public static boolean isValidPackageName(final String varName) {
		return varName.matches("[a-zA-Z0-9_.]*");
	}

	/**
	 *
	 * @param templateTag
	 * @return
	 */
	public static String getTemplateTagEnd(final TemplateTag templateTag) {
		return "</" + TEMPLATE_TAG_PREFIX + COLON + templateTag.name().toLowerCase() + XML_END;
	}

	/**
	 *
	 * @param templateTag
	 * @return
	 */
	public static String getTemplateTagStart(final TemplateTag templateTag) {
		//System.out.println(XML_START + TEMPLATE_TAG_PREFIX + COLON + templateTag.name().toLowerCase());
		return XML_START + TEMPLATE_TAG_PREFIX + COLON + templateTag.name().toLowerCase();
	}

	/**
	 *
	 * @param tag
	 * @param attrs
	 * @return
	 */
	public static Map<String, String> getAttributes(final String tag) {
		final Map<String, String> attributes = new HashMap<String, String>();
		//		final String modTag = tag.replaceAll("\\s*" + EQUAL + "\\s*", EQUAL).replaceAll(
		//				QUOTE_STR + "\\s*" + "([a-zA-Z_.]+)" + "\\s*" + QUOTE_STR, QUOTE_STR + "$1" + QUOTE_STR);
		//		final String pattern = "\\s+(?=(?:(?:[^\"]*\"){2})*[^\"]*$)";
		//		//final String[] attrArr = modTag.split("\\s+");
		//		final String[] attrArr1 = modTag.split(pattern, -1);

		final char[] charArray = tag.toCharArray();

		final StringBuilder attrNameBuilder = new StringBuilder();
		final StringBuilder attrValueBuilder = new StringBuilder();

		boolean attrFound = false, attrValStartFound = false, attrNameEndFound = false, equalSignFound = false;

		for (final char ch : charArray) {
			if (!attrFound && Character.isWhitespace(ch)) {
				continue;
			}
			attrFound = true;
			if (ch == EQUAL.charAt(0) && !attrValStartFound && !equalSignFound) {
				equalSignFound = true;
				continue;
			}

			if (!equalSignFound && !attrNameEndFound && Character.isWhitespace(ch)) {
				attrNameEndFound = true;
			} else if (!equalSignFound && !attrNameEndFound) {
				attrNameBuilder.append(ch);
			} else if (!equalSignFound && attrNameEndFound && !Character.isWhitespace(ch)) {
				throw new RuntimeException("Illegal character " + ch + " for attribute " + attrNameBuilder.toString());
			} else if (ch != QUOTE_STR && attrValStartFound) {
				attrValueBuilder.append(ch);
			} else if (ch == QUOTE_STR && equalSignFound && !attrValStartFound) {
				attrValStartFound = true;
			} else if (!Character.isWhitespace(ch) && equalSignFound && !attrValStartFound) {
				throw new RuntimeException("Illegal character " + ch + " for attribute " + attrNameBuilder.toString());
			} else if (ch == QUOTE_STR && attrValStartFound) { // end of an attribute
				attrFound = attrValStartFound = equalSignFound = attrNameEndFound = false;
				attributes.put(attrNameBuilder.toString().trim(), attrValueBuilder.toString());
				attrNameBuilder.setLength(0);
				attrValueBuilder.setLength(0);
			}
		}

		//		for (final String nameValue : attrArr1) {
		//			if (isEmpty(nameValue)) {
		//				continue;
		//			}
		//			final String[] attrComp = nameValue.split(EQUAL);
		//			final String attrName = attrComp[0].trim();
		//			final String attrValue = attrComp[1].trim().replaceAll("^" + QUOTE_STR, EMPTY_STR).replaceAll(QUOTE_STR + "$", EMPTY_STR);
		//			attributes.put(attrName, attrValue);
		//		}

		return attributes;
	}

	/**
	 *
	 * @param tag
	 * @param attrs
	 * @return
	 */
	public static Map<String, String> getAttributes2(final String tag) {
		final Map<String, String> attributes = new HashMap<String, String>();

		final char[] charArray = tag.toCharArray();

		boolean attriNameStart = false;
		final boolean attriValueEnd;
		final boolean attriValueStart;
		boolean attriNameEnd = false;
		//StringBuilder
		for (final char c : charArray) {

			if (Character.isLetterOrDigit(c) && !attriNameStart && !attriNameEnd) {
				attriNameStart = true;
				//in a strinbbuilder add attr name
			} else if (c == '=' && attriNameStart) {
				attriNameEnd = true;
				final boolean attriValue = true;

				//check for " and set value start...then append the values to another string builder untill next " is reached...k..yes sir...k sir...yes sir..i can handle
			}
		}
		final String modTag = tag.replaceAll("\\s*" + EQUAL + "\\s*", EQUAL).replaceAll(
				QUOTE_STR + "\\s*" + "([a-zA-Z_.]+)" + "\\s*" + QUOTE_STR, QUOTE_STR + "$1" + QUOTE_STR);

		final String[] attrArr = modTag.split("\\s+");

		for (final String nameValue : attrArr) {
			if (isEmpty(nameValue)) {
				continue;
			}
			final String[] attrComp = nameValue.split(EQUAL);
			final String attrName = attrComp[0].trim();
			final String attrValue = attrComp[1].trim().replaceAll("^" + QUOTE_STR, EMPTY_STR).replaceAll(QUOTE_STR + "$", EMPTY_STR);
			attributes.put(attrName, attrValue);
		}

		return attributes;
	}

	/**
	 * @param snippet
	 * @return
	 */
	public static String formatJson(final String snippet) {
		boolean firstLine = true;
		final StringBuilder jsonSnippet = new StringBuilder(EMPTY_STR);
		final String[] snippetArr = snippet.split(NEWLINE);
		String tabStr = EMPTY_STR;

		for (final String snip : snippetArr) {

			if (snip.trim().contains(LEFT_CURL)) {
				if (firstLine) {
					jsonSnippet.append(LEFT_CURL).append(NEWLINE);
					firstLine = false;
				} else {
					// jsonSnippet.append(formatJson(snip))
				}
			} else if (snip.trim().contains(RIGHT_CURL)) {
				tabStr = tabStr.replaceFirst(tabStr, EMPTY_STR);
				// jsonSnippet.append();
			} else {
				jsonSnippet.append(snip.trim()).append(NEWLINE);
			}
			jsonSnippet.append(snip.trim()).append(NEWLINE).append(tabStr);
		}

		// jsonSnippet.append((EMPTY_STR.equals(jsonSnippet.toString()) ?
		// EMPTY_STR : "") + "" + NEWLINE);
		return jsonSnippet.toString();
	}

	/**
	 * @param fileName
	 * @return
	 */
	private static String readFile(final String fileName) {

		final File file = new File(fileName);

		char[] buffer = null;

		try {
			final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

			buffer = new char[(int) file.length()];

			int i = 0;
			int c = bufferedReader.read();

			while (c != -1) {
				buffer[i++] = (char) c;
				c = bufferedReader.read();
			}
		} catch (final FileNotFoundException e) {
		} catch (final IOException e) {
		}

		return new String(buffer);
	}

	public static boolean isEmpty(final Collection<?> c) {
		return c == null || c.isEmpty();
	}

	/**
	 * @param m
	 * @return
	 */
	public static boolean isEmpty(final Map<?, ?> m) {
		return m == null || m.isEmpty();
	}

	/**
	 * @param additonalParameters
	 * @return
	 * @throws FastCodeException
	 */
	public static List<FastCodeAdditionalParams> parseAdditonalParam(final String additonalParameters) throws FastCodeException {
		boolean spaceFound = false;
		boolean attriListStart = false;
		boolean paramTypeFound = false;
		/*final boolean attriValue = false;
		final boolean attriValueEnd = false;
		final boolean commaFound = false;*/
		final boolean attriValueStrt = false;
		boolean createObj = false;
		boolean colonFound = false;

		FastCodeAdditionalParams additionalParams = null;
		final StringBuilder paramVar = new StringBuilder();
		final StringBuilder paramType = new StringBuilder();
		/*final StringBuilder attribute_1 = new StringBuilder();
		final StringBuilder value_1 = new StringBuilder();*/
		Map<String, String> allAttributes = new HashMap<String, String>();
		final List<FastCodeAdditionalParams> additionalParamsList = new ArrayList<FastCodeAdditionalParams>();
		final List<String> paramList = new ArrayList<String>();
		final StringBuilder attributes = new StringBuilder();

		for (final char ch : additonalParameters.toCharArray()) {
			/*if (!spaceFound && !paramTypeFound && !attriListStart && !attriValue && !attriValueStrt && !attriValueEnd && !commaFound) {
				System.out.println("i am here");
			}*/
			if (spaceFound && !attriListStart) {
				//System.out.println("i am here");
				spaceFound = false;
				createObj = true;
				paramTypeFound = false;
			}
			if (Character.isWhitespace(ch) && !attriListStart) { // && !attriValueStrt) {
				spaceFound = true;
				//paramTypeFound = false;
				//createObj = true;
				continue;
			} else if (ch == ':') {
				paramTypeFound = true;
				colonFound = true;
				continue;
			} else if (ch == LEFT_PAREN_CHAR) {
				attriListStart = true;
				paramTypeFound = false;
				//createObj = false;
				continue;
			}/* else if (ch == '=') {
				attriValueStrt = true;
				}*/
			/*else if (ch == '=') {
				attriValue = true;
				if (commaFound) {
					commaFound = false;
				}
				continue;
			}*//*else if (ch == '\"') {
				if (attriValueStrt) {
					attriValueEnd = true;
					attriValueStrt = false;
				} else {
					attriValueStrt = true;
				}
				continue;
				}*//* else if (ch == COMMA_CHAR && attriValueEnd) {
					commaFound = true;
					attriValue = attriValueEnd = false;
					allAttributes.put(attribute_1.toString(), value_1.toString());
					value_1.delete(0, value_1.length());
					attribute_1.delete(0, attribute_1.length());
					continue;
					} */else if (ch == RIGHT_PAREN_CHAR) {
				if (attriListStart) {
					try {
						allAttributes = getAttributes(attributes.toString());
					} catch (final Exception ex) {
						throw new FastCodeException("Additional parameter " + HYPHEN + SPACE + paramVar.toString().trim() + SPACE + HYPHEN
								+ SPACE + ex.getMessage());
					}
				}
				paramTypeFound = false;
				attriListStart = false;
				/*attriValue = false;
				attriValueStrt = false;
				attriValueEnd = false;
				commaFound = false;*/
				spaceFound = false;
				/*allAttributes.put(attribute_1.toString(), value_1.toString());
				value_1.delete(0, value_1.length());
				attribute_1.delete(0, attribute_1.length());*/
				//createObj = true; //old one
				attributes.delete(0, attributes.length());
				continue;
			}

			if (attriListStart) {
				attributes.append(ch);
				continue;
			}
			if (paramTypeFound) { // && !spaceFound && !leftParaFound) {
				paramType.append(ch);
				continue;
			}

			/*if ((attriValue || attriValueStrt) && !attriValueEnd) {
				value_1.append(ch);
				continue;
			}
			if ((attriListStart || commaFound) && !attriValue) {
				attribute_1.append(ch);
				continue;
			}*/

			if (!isEmpty(paramVar.toString().trim()) && createObj) {
				validateParamValueAndType(paramVar, paramType, paramList, colonFound);
				for (final Entry<String, String> attribute : allAttributes.entrySet()) {
					if ((attribute.getKey().equals(ATTRIBUTE_MIN) || attribute.getKey().equals(ATTRIBUTE_MAX))
							&& !(paramType.toString().equalsIgnoreCase(INT) || paramType.toString().equalsIgnoreCase(
									RETURN_TYPES.INTRANGE.getValue()))) {
						throw new FastCodeException("Min or Max attribute can be used only with - int or intRange - parameter");
					}
					if (attribute.getKey().equals(ATTRIBUTE_TYPE)
							&& !paramType.toString().equalsIgnoreCase(RETURN_TYPES.LOCALVAR.getValue())) {
						throw new FastCodeException("type attribute can be used only with localvar parameter");
					}

					if (!(attribute.getKey().equals(ATTRIBUTE_REQUIRED) || attribute.getKey().equals(ATTRIBUTE_PATTERN)
							|| attribute.getKey().equals(PLACEHOLDER_PROJECT) || attribute.getKey().equals(ATTRIBUTE_LABEL)
							|| attribute.getKey().equals(ATTRIBUTE_ALLOWED_VALUES) || attribute.getKey().equals(ATTRIBUTE_VALUE)
							|| attribute.getKey().equals(ATTRIBUTE_ENABLED) || attribute.getKey().equals(ATTRIBUTE_MAX)
							|| attribute.getKey().equals(ATTRIBUTE_MIN) || attribute.getKey().equals(ATTRIBUTE_TYPE) || attribute.getKey()
							.equals(ATTRIBUTE_DEPENDSON))) {
						throw new FastCodeException("Attribute - " + attribute.getKey() + ", for parameter - " + paramVar
								+ ", must be one of required/pattern/allowed_values/value/project/label/enabled");
					}
				}

				if (allAttributes.containsKey(ATTRIBUTE_MAX) && allAttributes.containsKey(ATTRIBUTE_MIN)) {
					if (!(Integer.parseInt(allAttributes.get(ATTRIBUTE_MIN)) <= Integer.parseInt(allAttributes.get(ATTRIBUTE_MAX)))) {
						throw new FastCodeException("Min must have value less than or equal to Max.");
					}
				}
				final String allowedValues = allAttributes.get(ATTRIBUTE_ALLOWED_VALUES) == null ? EMPTY_STR : allAttributes
						.get(ATTRIBUTE_ALLOWED_VALUES);
				final String value = allAttributes.get(ATTRIBUTE_VALUE) == null ? EMPTY_STR : allAttributes.get(ATTRIBUTE_VALUE);
				if (!isEmpty(value) && !isEmpty(allowedValues)) {
					boolean valuePresent = false;
					for (final String allowedValue : allowedValues.split(SPACE)) {
						if (value.equals(allowedValue)) {
							valuePresent = true;
							break;
						}
					}
					if (!valuePresent) {
						throw new FastCodeException("Additional Parameter - " + paramVar.toString()
								+ ", value attribute must be one of the allowed Values.");
					}
				}
				additionalParams = new FastCodeAdditionalParams(paramVar.toString().trim(),
						colonFound ? RETURN_TYPES.getReturnType(paramType.toString().trim()) : RETURN_TYPES.getReturnType(STRING_CONSTANT),
						value, allAttributes.get(ATTRIBUTE_REQUIRED) == null ? Boolean.toString(true)
								: allAttributes.get(ATTRIBUTE_REQUIRED), allAttributes.get(ATTRIBUTE_PATTERN) == null ? EMPTY_STR
								: allAttributes.get(ATTRIBUTE_PATTERN), allAttributes.get(PLACEHOLDER_PROJECT) == null ? EMPTY_STR
								: allAttributes.get(PLACEHOLDER_PROJECT), allowedValues,
						allAttributes.get(ATTRIBUTE_LABEL) == null ? EMPTY_STR : allAttributes.get(ATTRIBUTE_LABEL),
						allAttributes.get(ATTRIBUTE_ENABLED) == null ? Boolean.toString(true) : allAttributes.get(ATTRIBUTE_ENABLED),
						allAttributes.get(ATTRIBUTE_MIN) == null ? ZERO_STRING : allAttributes.get(ATTRIBUTE_MIN),
						allAttributes.get(ATTRIBUTE_MAX) == null ? ZERO_STRING : allAttributes.get(ATTRIBUTE_MAX),
						allAttributes.get(ATTRIBUTE_TYPE) == null ? EMPTY_STR : allAttributes.get(ATTRIBUTE_TYPE),
						allAttributes.get(ATTRIBUTE_DEPENDSON) == null ? EMPTY_STR : allAttributes.get(ATTRIBUTE_DEPENDSON));
				additionalParamsList.add(additionalParams);
				createObj = false;
				colonFound = false;
				paramVar.delete(0, paramVar.length());
				paramType.delete(0, paramType.length());
				allAttributes.clear();
			}
			paramVar.append(ch);
		}

		validateParamValueAndType(paramVar, paramType, paramList, colonFound);
		for (final Entry<String, String> attribute : allAttributes.entrySet()) {

			if ((attribute.getKey().equals(ATTRIBUTE_MIN) || attribute.getKey().equals(ATTRIBUTE_MAX))
					&& !(paramType.toString().equalsIgnoreCase(INT) || paramType.toString().equalsIgnoreCase(
							RETURN_TYPES.INTRANGE.getValue()))) {
				throw new FastCodeException("Min or Max attribute can be used only with - int or intRange - parameter");
			}
			if (attribute.getKey().equals(ATTRIBUTE_TYPE) && !paramType.toString().equalsIgnoreCase(RETURN_TYPES.LOCALVAR.getValue())) {
				throw new FastCodeException("type attribute can be used only with localvar parameter");
			}
			if (!(attribute.getKey().equals(ATTRIBUTE_REQUIRED) || attribute.getKey().equals(ATTRIBUTE_PATTERN)
					|| attribute.getKey().equals(PLACEHOLDER_PROJECT) || attribute.getKey().equals(ATTRIBUTE_LABEL)
					|| attribute.getKey().equals(ATTRIBUTE_ALLOWED_VALUES) || attribute.getKey().equals(ATTRIBUTE_VALUE)
					|| attribute.getKey().equals(ATTRIBUTE_ENABLED) || attribute.getKey().equals(ATTRIBUTE_MAX)
					|| attribute.getKey().equals(ATTRIBUTE_MIN) || attribute.getKey().equals(ATTRIBUTE_TYPE) || attribute.getKey().equals(
					ATTRIBUTE_DEPENDSON))) {
				throw new FastCodeException("Attribute - " + attribute.getKey() + ", for parameter - " + paramVar
						+ ", must be one of required/pattern/allowed_values/value/project/label");
			}
		}
		if (allAttributes.containsKey(ATTRIBUTE_MAX) && allAttributes.containsKey(ATTRIBUTE_MIN)) {
			if (!(Integer.parseInt(allAttributes.get(ATTRIBUTE_MIN)) <= Integer.parseInt(allAttributes.get(ATTRIBUTE_MAX)))) {
				throw new FastCodeException("Min must have value less than or equal to Max.");
			}
		}
		final String allowedValues = allAttributes.get(ATTRIBUTE_ALLOWED_VALUES) == null ? EMPTY_STR : allAttributes
				.get(ATTRIBUTE_ALLOWED_VALUES);
		final String value = allAttributes.get(ATTRIBUTE_VALUE) == null ? EMPTY_STR : allAttributes.get(ATTRIBUTE_VALUE);
		if (!isEmpty(value) && !isEmpty(allowedValues)) {
			boolean valuePresent = false;
			for (final String allowedValue : allowedValues.split(SPACE)) {
				if (value.equals(allowedValue)) {
					valuePresent = true;
					break;
				}
			}
			if (!valuePresent) {
				throw new FastCodeException("Additional Parameter - " + paramVar.toString()
						+ ", value - attribute must, be one of the allowed Values.");
			}
		}
		additionalParams = new FastCodeAdditionalParams(paramVar.toString().trim(), colonFound ? RETURN_TYPES.getReturnType(paramType
				.toString().trim()) : RETURN_TYPES.getReturnType(STRING_CONSTANT), value,
				allAttributes.get(ATTRIBUTE_REQUIRED) == null ? Boolean.toString(true) : allAttributes.get(ATTRIBUTE_REQUIRED),
				allAttributes.get(ATTRIBUTE_PATTERN) == null ? EMPTY_STR : allAttributes.get(ATTRIBUTE_PATTERN),
				allAttributes.get(PLACEHOLDER_PROJECT) == null ? EMPTY_STR : allAttributes.get(PLACEHOLDER_PROJECT), allowedValues,
				allAttributes.get(ATTRIBUTE_LABEL) == null ? EMPTY_STR : allAttributes.get(ATTRIBUTE_LABEL),
				allAttributes.get(ATTRIBUTE_ENABLED) == null ? Boolean.toString(true) : allAttributes.get(ATTRIBUTE_ENABLED),
				allAttributes.get(ATTRIBUTE_MIN) == null ? ZERO_STRING : allAttributes.get(ATTRIBUTE_MIN),
				allAttributes.get(ATTRIBUTE_MAX) == null ? ZERO_STRING : allAttributes.get(ATTRIBUTE_MAX),
				allAttributes.get(ATTRIBUTE_TYPE) == null ? EMPTY_STR : allAttributes.get(ATTRIBUTE_TYPE),
				allAttributes.get(ATTRIBUTE_DEPENDSON) == null ? EMPTY_STR : allAttributes.get(ATTRIBUTE_DEPENDSON));
		additionalParamsList.add(additionalParams);

		/*System.out.println(paramVar.toString());
		System.out.println(paramType.toString());*/
		/*System.out.println("attri " + attribute_1.toString());
		System.out.println("val " + value_1.toString());*/
		//System.out.println(allAttributes);
		return additionalParamsList;

	}

	/**
	 * @param paramVar
	 * @param paramType
	 * @param paramList
	 * @param colonFound
	 * @throws FastCodeException
	 */
	public static void validateParamValueAndType(final StringBuilder paramVar, final StringBuilder paramType, final List<String> paramList,
			final boolean colonFound) throws FastCodeException {
		if (paramList.contains(paramVar.toString().trim())) {
			throw new FastCodeException("Duplicate Additional Parameter name " + paramVar.toString().trim());
		}

		final RETURN_TYPES type = RETURN_TYPES.getReturnType(paramType.toString().trim());
		if (colonFound && type == null) {
			throw new FastCodeException(
					"Invalid type "
							+ paramType.toString().trim()
							+ " for parameter "
							+ paramVar.toString().trim()
							+ "\nAdditional Parameter type can be only class/file/package/folder/project/javaProject/localvar/boolean/interface/enumeration.");
		}
		paramList.add(paramVar.toString().trim());
	}

	/**
	 * @param input
	 * @return
	 */
	public static boolean isValidTableOrColumnName(final String input) {
		//return input.matches("[a-zA-Z\d][\w#@]{0,127}$");
		return input.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
		//return input.matches("^(\\w*\\d*[^\\s+][^!@#$%^&*:|])$");
	}

	public static List<TagAttributeList> parseFCTag(final String templateBody) {
		final List<TagAttributeList> tagTypeAttributeList = new ArrayList<TagAttributeList>();
		int lineNo = 1;
		final String[] tempBodySplit = templateBody.split(NEWLINE);

		for (final String lineContent : tempBodySplit) {

			if (!lineContent.trim().contains("<fc:")) {
				lineNo++;
				continue;
			}
			String tagName = EMPTY_STR;
			String attriName = EMPTY_STR;
			final List<FcTagAttributes> attributesList = new ArrayList<FcTagAttributes>();
			boolean attriStart = false;
			boolean tagNameStart = false;
			boolean quoteStart = false;
			final boolean quoteEnd = false;
			boolean angle_left = false;
			String tagStart = EMPTY_STR;
			int colNo = -1;

			for (final char ch : lineContent.toCharArray()) {
				colNo++;

				if (ch == '<') {
					angle_left = true;
					tagStart = tagStart + ch;
					continue;
				}

				if (angle_left && ch == 'f' || ch == 'c') {
					if (!(tagNameStart || attriStart)) {
						if (!quoteStart) {

							tagStart = tagStart + ch;
							continue;
						}
					}
				}

				if (ch == QUOTE_STR_CHAR) {
					if (quoteStart) {
						quoteStart = false;
						attriStart = true;
					} else {
						quoteStart = true;
					}
					continue;
				}

				if (quoteStart) {
					continue;
				}

				if (ch == '>' || ch == '/') {
					tagTypeAttributeList.add(new TagAttributeList(tagName, attributesList));
					break;
				}

				if (tagStart.equals("<fc") && ch == COLON_CHAR) {
					tagNameStart = true;
					continue;
				}

				if (ch == SPACE_CHAR && (tagNameStart || attriStart)) {
					attriStart = true;
					tagNameStart = false;
					//colNoFound = true;
					continue;
				}

				if (ch == '=') {
					attributesList.add(new FcTagAttributes(attriName, lineNo, colNo - attriName.length()));
					attriStart = false;
					//colNoFound = false;
					attriName = EMPTY_STR;
					continue;
				}

				if (tagNameStart) {
					tagName = tagName + ch;
				}
				if (attriStart) {
					attriName = attriName + ch;
				}

			}
			lineNo++;
		}
		return tagTypeAttributeList;

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		final String container = "<beans> \n" + "<bean    id=\"aaa\"   ref=\"bbb\">\n" + "</bean>\n" + "</beans> \n";
		final String buffer = "<bean id=\"aaa\"                      ref=\"bbb\">\n" + "</bean>\n";

		final String con = "<named-query name=\"aaaa\">\n" + "<city>Dallas</city>" + "<query>\n" + "<![CDATA[\n" + "asasasasasasasa\n"
				+ "]]>\n" + "</query>" + "</named-query>";
		final Pattern parmPattern = Pattern.compile("([A-Za-z]+)\\s*<\\s*([A-Za-z ,]+)\\s*>\\s*");
		// Matcher matcher = parmPattern.matcher("List<List<String>>");
		final Matcher matcher = parmPattern.matcher("List < String >");

		if (matcher.matches()) {
			// System.out.println("matched ");
		}

		// FastCodeType fastCodeType =
		// parseType("List < Map < String  ,  Date > >");
		// final FastCodeType fastCodeType = parseType("List");
		// FastCodeType fastCodeType = parseType("List< List < String > >");
		// System.out.println("Type " + fastCodeType.getType());
		// System.out.println("Type " +
		// fastCodeType.getParams().get(0).getType());
		// System.out.println("Type " +
		// fastCodeType.getParams().get(0).getParams().get(0).getType());
		// System.out.println("Type " +
		// fastCodeType.getParams().get(0).getParams().get(1).getType());

		// String[] params = parseParamString("Map<List<String>, Date>");
		// String[] params = parseParamString("List<String>, Date");
		// System.out.println("length " + params.length);
		// System.out.println("params[0] " + params[0]);
		// System.out.println("params[1] " + params[1]);

		// System.out.println(findMatch(container, buffer));

		// System.out.println(covertToRegex("[a-z.]*"));
		// System.out.println(changeToCamelCase("itemService", '-'));

		// System.out.println(isAllLettersUpperCase("ONE"));
		// System.out.println(changeToCamelCase("CategoryDAO", '-'));

		final String imp = "com.abc.test.AAAAAA";
		final String clssName = imp.substring(imp.lastIndexOf(DOT_CHAR) + 1);
		final String pkg = imp.substring(0, imp.lastIndexOf(DOT_CHAR));

		String str = "<bean id=\"aaaa\" class=\"ddddd\">" + "#foreach ($field in ${fields}) <prop name=\"${field.name}\"/> #end"
				+ "</bean>";
		try {
			final XmlElement element = parseXml(str);
			// System.out.println(" " + element.getTagName());
			// System.out.println(" " +
			// element.getAttributes().get(0).getLeft());
			// System.out.println(" " +
			// element.getAttributes().get(0).getRight());
			// System.out.println(" " +
			// element.getAttributes().get(1).getLeft());
			// System.out.println(" " +
			// element.getAttributes().get(1).getRight());
		} catch (final Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		final String tag = "<struts-config><action-mappings>";
		// String tag = "<struts-config>";
		// System.out.println(" " + tag);
		// String[] tags = tag.split(">\\s*<|<|>");
		final String[] tags = tag.split("<|>");
		// String[] tags = tag.split("\\<[a-z,A-Z,0-9]*\\>");
		// System.out.println("tags.length " + tags.length);
		for (final String tag1 : tags) {
			if (!tag1.trim().equals(EMPTY_STR)) {
				// System.out.println("tag " + tag1);
			}
		}

		// System.out.println(" " + reverseCamelCase("product_type", '_'));
		// System.out.println("clssName " + clssName);
		// System.out.println("pkg " + pkg);
		str = "${class_header}\n" + "package ${package_name};\n" + "${class_annotations}\n"
				+ "public ${class_modifier} ${class_type} ${class_name} extends ${super_class} implements ${interfaces} {\n" + "}\n";
		try {
			// System.out.println("str " + str);
			// System.out.println("str " + resetLineWithTag(str,
			// "class_annotations"));
			// System.out.println("str " + replacePlaceHolderWithBlank(str,
			// "implements", "interfaces", "{"));
			// System.out.println("str " + replacePlaceHolderWithBlank(str,
			// "extends", "super_class", "implements"));
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		str = "</aaaa></bbbb>";
		final String[] strArr = str.split("</|>");
		// System.out.println("1 " + strArr[0]);
		// System.out.println("2 " + strArr[1]);
		// System.out.println("3 " + strArr[2]);
		// System.out.println("4 " + strArr[3]);

		// System.out.println("tag " + containsPlaceHolder("${tag}", "tag"));
		// System.out.println(formatXmlWithCDATA(con));

		// final String typeName = "aaa[]";
		// System.out.println("str = " +
		// (typeName.replaceAll("\\[[\\[ \\]]*\\]", EMPTY_STR)));
		// System.out.println(replaceSpecialChars("aaa\\nbbbb\\tcccc\\naaaaa"));

		// final StringBuilder annoAttrs = new StringBuilder("aaaa");
		// annoAttrs.insert(0, LEFT_PAREN).append(RIGHT_PAREN);
		// // System.out.println(" annoAttrs " + annoAttrs);
		//
		// final String key = "from_fields";
		// System.out.println(key.substring(0, key.length() - 1));
		//
		// final int key1 = 1;
		// switch (key1) {
		// case 1:
		// int ii = 7;
		// break;
		// case 2 :
		// ii = 0;
		// System.out.println("ii " + ii);
		// default:
		// break;
		// }

		/*final String method =
			 "private void static main121_op(String[] args) throws Exception {\n  {djfkdsjf} }";
		System.out.println(parseMethodName(method));*/

		// System.out.println(containsAnyPlaceHolder("${var.max}"));
		// System.out.println((reverseCamelCase("prod_tpy",'_')));
		// System.out.println((reverseCamelCase("prod_tpy",'_')).substring(0,1).toLowerCase()+(reverseCamelCase("prod_tpy",'_')).substring(1));

		final String fieldSrc = "\t\t\t\t@Column(name = \"ipaddress\",nullable=false,length = 10)\n private String ipaddress;"; //"\t\t\t\t@Column \n private String ipaddress;"; // final String
		/*final String fieldname = parseFieldName(fieldSrc);
		System.out.println(fieldname);*/

		/*
		 * final String setMethod =
		 * "public void setIpaddress(String ipaddress){\n\t this.ipaddress=ipaddress;\n}"
		 * ; System.out.println(parseMethodName(setMethod));
		 */
		// System.out.println(isValidVariableName("krish_10"));
		/*
		 * System.out.println(containsAnyPlaceHolder(
		 * "if (this == ${class_instance}) {"));
		 *
		 * final String snippet = "if (this == ${class_instance}) {" +
		 * "return true;"+ "}"+ "if (${class_instance} == null) {"+
		 * "return false;"+ "}" +
		 * "if (!(${class_instance} instanceof Sample)) {"+ "return false;" +
		 * "}"+ "Sample otherSample = (Sample) ${class_instance};"+ "return"+
		 *
		 * "return new EqualsBuilder().append(this.firstName, otherSample.firstName).append(this.lastName, otherSample.lastName).append(this.doorNumber, otherSample.doorNumber).append(this.address, otherSample.address).isEquals()"
		 * ; System.out.println(containsAnyPlaceHolder(snippet));
		 */
		/*System.out.println(containsAnyPlaceHolder("public interface ${name}${suffix}{\\n}"));

		final Map<String, String> attributes = getAttributes("name = \"  aaa  \"   target = \"  gfgf  \"");
		System.out.println(attributes);*/

		//getAttributes("title=\"no default constructor\"");
		//getAttributes(" name=\"create${class.name}\" target=\"${targetClass.fullyQualifiedName}\"");
		//getAttributes(" node=\"bean\" parent=\"beans\" target=\"\"");
		/* final Map<String, String> attributes = getAttributes("dir  =   \"TestFC/src/xml\" name=\"applicationContext-${class.defaultInstance}.xml\"");
		 System.out.println("attributes " + attributes);
		 final Map<String, String> attributes2 = getAttributes(" name=\"equals\"");
		 System.out.println(attributes2);
		 getAttributes(" dir=\"TestFC/src/jsp\" name=\"TestJsonK1.js\"");
		 getAttributes(" target=\"${targetClass.fullyQualifiedName}\" name=\"${fieldName}\"");
		 getAttributes(" name=\"${class.defaultInstance}\"");
		 getAttributes(" name=\"${class.name}Impl\" package=\"${class.fastCodePackage.packageName}.impl\" project=\"${class.fastCodePackage.javaProject}\"");
		 getAttributes(" dir=\"${folder.getFullPath().toString()}\" name=\"${fileName}\"");
		 getAttributes(" type=\"class\" name=\"${fileName}\" package=\"${package}\" project=\"${project}\"");
		 getAttributes(" type=\"interface\" name=\"${fileName}\" package=\"${package}\" project=\"${project}\"");
		 */

		//				parseAdditonalParam("myName:class (value=\"mytext\")");//, required=\"false\") myStr myFldr:folder");//, required=\"false\")");
		//				parseAdditonalParam("myName:class(value=\"mytext\" , required=\"false\")");
		//parseAdditonalParam("myName:class (value=\"my text\"  required=\"false\") myFldr:folder");
		//				parseAdditonalParam("myName:class (value=\"mytext\" required=\"true\")");
		//		parseAdditonalParam("myName:class (value=\"my text\",required=\"true\")");
		//		parseAdditonalParam("myName:class (value=\"my text\", required=\"true\")");
		//		parseAdditonalParam("myName:class (value=\"my text\",required=\"true\")");
		//		parseAdditonalParam("myName:class (value=\"mytext\" , required=\"true\") myStr");
		//		parseAdditonalParam("myName:class (value=\"mytext\") myStr"); --
		//		parseAdditonalParam("myName:class (value=\"mytext\" , required=\"true\") myStr myFldr:folder");
		//		parseAdditonalParam("myName:class (value=\"mytext\") myStr myFldr:folder");
		//		parseAdditonalParam("myName:class myStr");
		//		parseAdditonalParam("myName:class (value=\"mytext\" , required=\"true\") myFldr:folder");
		//parseAdditonalParam("myName:class (value=\"mytext\", required=false) myFldr:folder");
		//		parseAdditonalParam("myName:class myFldr:folder");
		//		parsesAdditonalParam("myName:class (value=\"my,text\", required=\"true\")");
		//		parseAdditonalParam("myName:class (value=\"my , text\", required=\"true\")");
		//		parseAdditonalParam("myName:class (value=\"my,text\" , required=\"false\") myFldr:folder");
		//parseAdditonalParam("folder:folder fileName(value=\"applicationContext-${class.defaultInstance}.xml\") myName(value=\"Vo\", allowed_values=\"Krish Dao Vo\")");
		//parseAdditonalParam("targetClass:class generateComment:boolean(value=\"true\", label=\"Generate Comment\")");
		//parseAdditonalParam("targetClass:class(label=\"Target Class\" pattern=\"*DAO\" project=\"TestFC\") fieldName value(allowed_values=\"Krish Bajju Vidu\") staticImport:boolean(label=\"Do Static Import\" value=\"true\") testFile:file (pattern=\"*File\" project=\"TestFC\" required=\"false\") myPak:package");
		//getAttributes("label=\"Target Class\"  pattern=\"*DAO\" project=\"TestFC\"");
		//getAttributes("label=\"Generate Comment\" value=true"); //wont wrk
		//getAttributes("targetClass:class(label=\"Target Class\" pattern=\"*DAO\" project=\"TestFC\") fieldName value(allowed_values=\"Krish Bajju Vidu\") staticImport:boolean(label=\"Do Static Import\" value=true) testFile:file (pattern=\"*File\" project=\"TestFC\" required=false) myPak:package"); //wont wrk
		//parseAdditonalParam("targetClass:class staticImport:boolean methodName(value=\"create${class.name}\")");
		//parseAdditonalParam("targetClass:class(label=\"Target Class\" pattern=\"*DAO\" project=\"TestFC\") fieldName value(allowed_values=\"Krish Bajju Vidu\") staticImport:boolean(label=\"Do Static Import\" value=\"true\") testFile:file (pattern=\"*File\" project=\"TestFC\" required=\"false\") myPak:package ");
		//final String method1 = "///* " +
		//	  "/*" +
		//  "/*@return" +
		//			  "/*//"+
		//			"public int getVar2 ()  {" +
		//			"int getVar2 =  testOfCVDialog.getVar2();" +
		//			"return getVar2;" +
		//			"}";
		/*final String method2 =
				"public Strng int getVar2 ()  {" +
				"int getVar2 =  testOfCVDialog.getVar2();" +
				"return getVar2;" +
				"}";*/
		//	final String abc = "/**/n*/n* @return/n*/ /n public int getVar2 ()  {int getVar2 =  testOfCVDialog.getVar2();return getVar2;}";
		//	System.out.println("method name -- " + parseMethodName(abc));
		final String testStr = "            <fc:file dir=\"${scripts.fullPath}/${module.toLowerCase()}\" name=\"${module}.js\">";
		parseFCTag(testStr);

	}

	/**
	 * get the number of tabs in the beginning of the string
	 * @param lineContent
	 * @param noOfTab
	 * @return
	 */
	public static int getNoOfTabs(final String lineContent) {
		int noOfTab = 0;
		for (final char ch : lineContent.toCharArray()) {
			if (ch == TAB_CHAR) {
				noOfTab++;
				continue;
			}
			if (Character.isLetterOrDigit(ch)) {
				break;
			}
		}
		return noOfTab;
	}

}

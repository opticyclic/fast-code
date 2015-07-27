package org.fastcode.templates.velocity.contentassist;

import static org.fastcode.common.FastCodeConstants.DOUBLE_QUOTES;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.EQUAL;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.OPTIONAL;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.TAB;
import static org.fastcode.common.FastCodeConstants.TRUE_STR;
import static org.fastcode.util.StringUtil.isEmpty;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;
import org.fastcode.Activator;
import org.fastcode.templates.contentassist.TemplateProposal;
import org.fastcode.templates.util.FcTagAttributes;
import org.fastcode.templates.util.TagAttributeList;
import org.fastcode.util.FastCodeUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FastCodeKeywordsManager {

	private static SortedSet<String>				FC_START_DIRECTIVES		= new TreeSet<String>();
	private static SortedSet<String>				FC_END_DIRECTIVES		= new TreeSet<String>();

	private static SortedSet<String>				COLOR_DIRECTIVES		= new TreeSet<String>();

	private static Map<String, ArrayList<String>>	requiredAttributesMap	= new TreeMap<String, ArrayList<String>>();
	private static Map<String, ArrayList<String>>	optionalAttributesMap	= new TreeMap<String, ArrayList<String>>();
	private static Map<String, ArrayList<String>>	oldAttributesMap	= new TreeMap<String, ArrayList<String>>();

	private static Map<String, String>				defaultDirectives		= new TreeMap<String, String>();

	private static Map<String, String>				signatureMap			= new TreeMap<String, String>();

	static {
		getFCTagsMapping();
	}

	/**
	 * Get all directives.
	 *
	 * @return the directives
	 */
	public static String[] getDirectives() {
		return COLOR_DIRECTIVES.toArray(new String[COLOR_DIRECTIVES.size()]);
	}

	/**
	 * Checks if is multi line directive.
	 *
	 * @param directive
	 *            the directive
	 *
	 * @return true, if is multi line directive
	 */
	/*
	 * public static boolean isMultiLineDirective(String directive) { return
	 * MULTI_LINE_DIRECTIVES.contains(directive); }
	 */

	/**
	 * Checks if the given directive has parenthesis.
	 *
	 * @param directive
	 *            the directive
	 *
	 * @return true, if has parenthesis
	 */
	/*
	 * public static boolean hasParenthesis(String directive) { return
	 * PARANTHESIS_DIRECTIVES.contains(directive); }
	 */
	/**
	 * Gets the completion proposals for the given element.
	 *
	 * @param element
	 *            the element
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @param spaceToPad
	 * @param proposalsToSkip
	 * @param fcTagType
	 *
	 * @return the completion proposals
	 */
	public static List<ICompletionProposal> getCompletionProposals(final String partition, final String element, final int offset,
			final int length, final String spaceToPad, final String fcTagType, final List<String> proposalsToSkip) {
		final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

		if (!IDocument.DEFAULT_CONTENT_TYPE.equals(partition)) {
			return proposals;
		}
		Iterator<String> iter = null;
		if (element.startsWith("<f")) {
			iter = FC_START_DIRECTIVES.iterator();
			while (iter.hasNext()) {
				final String proposal = iter.next();
				String proposalString = EMPTY_STR;
				String reqdAttr = EMPTY_STR;
				String optionalAttr = EMPTY_STR;
				String signature = EMPTY_STR;
				String propString = EMPTY_STR;

				if (proposal.toLowerCase().startsWith(element.substring(0, element.length()).toLowerCase())) {
					final int cursorOffset = proposal.length();

					if (hasRequiredAttribute(proposal.substring(4, proposal.length() - 1))) {

						proposalString = proposal.substring(0, proposal.length() - 1);
						for (final String attrs : requiredAttributesMap.get(proposal.substring(4, proposal.length() - 1))) {
							if (attrs != EMPTY_STR) {
								reqdAttr += SPACE + attrs + EQUAL + DOUBLE_QUOTES + DOUBLE_QUOTES;
							}
						}

						if (hasSignatureMap(proposal.substring(4, proposal.length() - 1))) {
							signature = signatureMap.get(proposal.substring(4, proposal.length() - 1));

						}

					}
					if (hasOptionalAttribute(proposal.substring(4, proposal.length() - 1))) {
						proposalString = proposal.substring(0, proposal.length() - 1);
						for (final String attrs : optionalAttributesMap.get(proposal.substring(4, proposal.length() - 1))) {
							if (attrs != EMPTY_STR) {
								if (attrs.endsWith(OPTIONAL)) {
									optionalAttr += SPACE + attrs + EQUAL + DOUBLE_QUOTES + TRUE_STR + DOUBLE_QUOTES;
								} else {
									optionalAttr += SPACE + attrs + EQUAL + DOUBLE_QUOTES + DOUBLE_QUOTES;
								}
							}
						}

					}
					if (!optionalAttr.equals(EMPTY_STR)) {

						propString = proposalString + reqdAttr + optionalAttr + ">";
						proposals.add(createProposal(proposal, propString, offset, length, cursorOffset, spaceToPad));
					}

					if (!signature.equals(EMPTY_STR)) {

						propString = proposalString + reqdAttr + ">" + NEWLINE + TAB + signature;
						proposals.add(createProposal(proposal, propString, offset, length, cursorOffset, spaceToPad));

					}
					propString = proposalString + reqdAttr + ">";
					proposals.add(createProposal(proposal, propString, offset, length, cursorOffset, spaceToPad));

				}

			}
		} else if (element.startsWith("</f")) {

			iter = FC_END_DIRECTIVES.iterator();
			while (iter.hasNext()) {
				String proposal = iter.next();
				if (proposal.toLowerCase().startsWith(element.substring(2, element.length()).toLowerCase())) {

					proposal = "</" + proposal + ">";
					final int cursorOffset = proposal.length();
					final String displayString = proposal;
					proposals.add(createTemplateProposal(proposal, displayString, offset, length, cursorOffset));

				}

			}
		} else if (element.startsWith(SPACE)) {

			final int cursorOffset = fcTagType.length();
			if (requiredAttributesMap.containsKey(fcTagType)) {
				for (final String attrs : requiredAttributesMap.get(fcTagType)) {
					String reqdAttr = EMPTY_STR;
					if (attrs != EMPTY_STR) {
						boolean skipProp = false;
						for (final String propToSkip : proposalsToSkip.toArray(new String[0])) {
							if (attrs.trim().startsWith(propToSkip)) {
								skipProp = true;
								break;
							}
						}

						if (skipProp) {
							continue;
						}
						reqdAttr += SPACE + attrs + EQUAL + DOUBLE_QUOTES + DOUBLE_QUOTES;
						final String displayString = attrs;
						proposals.add(createTemplateProposal(reqdAttr, displayString, offset, length, cursorOffset));
					}
				}
			}

			if (optionalAttributesMap.containsKey(fcTagType)) {
				for (final String attrs : optionalAttributesMap.get(fcTagType)) {
					String optionalAttr = EMPTY_STR;
					if (attrs != EMPTY_STR) {
						boolean skipProp = false;
						for (final String propToSkip : proposalsToSkip.toArray(new String[0])) {
							if (attrs.trim().startsWith(propToSkip)) {
								skipProp = true;
								break;
							}
						}

						if (skipProp) {
							continue;
						}
						if (attrs.endsWith(OPTIONAL)) {
							optionalAttr += SPACE + attrs + EQUAL + DOUBLE_QUOTES + TRUE_STR + DOUBLE_QUOTES;
						} else {
							optionalAttr += SPACE + attrs + EQUAL + DOUBLE_QUOTES + DOUBLE_QUOTES;
						}

						final String displayString = attrs;
						proposals.add(createTemplateProposal(optionalAttr, displayString, offset, length, cursorOffset));
					}
				}
			}
		}

		return proposals;

	}

	private static boolean hasSignatureMap(final String proposal) {
		return signatureMap.containsKey(proposal);
	}

	private static ICompletionProposal createProposal(final String proposal, String proposalString, final int offset, final int length,
			int cursorOffset, final String spaceToPad) {

		String endDirective = EMPTY_STR;
		if (hasEndProposal(proposal)) {
			/*String spaceToPadStr = EMPTY_STR;
			for (int i = 0; i < spaceToPad; i++) {
				spaceToPadStr += SPACE;
			}*/
			endDirective = NEWLINE + spaceToPad + "</fc:" + proposal.substring(4, proposal.length() - 1) + ">";
			proposalString += endDirective;
			cursorOffset = proposalString.length() - 1;
		} else {

			proposalString = proposalString.substring(0, proposalString.length() - 1) + "/>";
			cursorOffset = proposalString.length() - 1;
		}
		final String displayString = proposalString.substring(0, proposalString.length() - endDirective.length());
		proposalString += EMPTY_STR;
		cursorOffset += 1;

		return createTemplateProposal(proposalString, displayString, offset, length, cursorOffset);

	}

	private static boolean hasOptionalAttribute(final String proposal) {
		return optionalAttributesMap.containsKey(proposal);
	}

	private static boolean hasRequiredAttribute(final String proposal) {
		return requiredAttributesMap.containsKey(proposal);
	}

	/**
	 * Checks if the given directive has end tags
	 *
	 * @param directive
	 *            the directive
	 *
	 * @return true, if has end tag
	 */
	private static boolean hasEndProposal(final String directive) {

		return FC_END_DIRECTIVES.contains(directive.substring(1, directive.length() - 1));
	}

	/**
	 * Creates a template proposal.
	 *
	 * @param proposal
	 *            the proposal
	 * @param displayString
	 *            the display string
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @param cursorOffset
	 *            the cursor offset
	 *
	 * @return the template proposal
	 */
	private static ICompletionProposal createTemplateProposal(final String proposal, final String displayString, final int offset,
			final int length, final int cursorOffset) {
		return new TemplateProposal(proposal, displayString, offset, length, new Point(offset + cursorOffset, 0));
	}

	public static void getFCTagsMapping() {

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		InputStream inputStream = null;

		try {
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/fctags.xml"), false);
			final Document document = docBuilder.parse(inputStream);
			final NodeList fctagList = document.getElementsByTagName("tag");

			final int size = fctagList.getLength();
			for (int i = 0; i < size; i++) {
				final Node tagNode = fctagList.item(i);
				final NamedNodeMap tagAttributes = tagNode.getAttributes();
				final Node nameNode = tagAttributes.getNamedItem("name");
				final String tagName = nameNode.getNodeValue();
				FC_START_DIRECTIVES.add("<fc:" + tagName + ">");
				// FC_END_DIRECTIVES.add("</fc:" + tagName + ">");

				final Node endNode = tagAttributes.getNamedItem("endtag");
				final boolean hasEndTag = Boolean.valueOf(endNode.getNodeValue());
				FC_END_DIRECTIVES.add(hasEndTag ? "fc:" + tagName : "");
				COLOR_DIRECTIVES.add("fc:" + tagName);

				if (tagNode.getTextContent().trim() != "") {
					signatureMap.put(tagName, tagNode.getTextContent().trim());
				}

				final Node reqdAttrNode = tagAttributes.getNamedItem("required");
				final String requiredAttibutes = reqdAttrNode.getNodeValue();
				requiredAttributesMap.put(tagName, new ArrayList<String>(Arrays.asList(requiredAttibutes.split(","))));

				final Node optionalAttrNode = tagAttributes.getNamedItem("optional");
				final String optionalAttibutes = optionalAttrNode.getNodeValue();
				optionalAttributesMap.put(tagName, new ArrayList<String>(Arrays.asList(optionalAttibutes.split(","))));

				final Node oldAttrNode = tagAttributes.getNamedItem("old");
				final String oldAttibutes = oldAttrNode.getNodeValue();
				oldAttributesMap.put(tagName, new ArrayList<String>(Arrays.asList(oldAttibutes.split(","))));

			}

		} catch (final Exception ex) {
			ex.printStackTrace();

		} finally {

			FastCodeUtil.closeInputStream(inputStream);
		}

	}

	public static List<FcTagAttributes> validateAttriutes(final List<TagAttributeList> tagAttriLsit) {
		final List<FcTagAttributes> inValidAttributes = new ArrayList<FcTagAttributes>();
		for (final TagAttributeList fcTagAttributes : tagAttriLsit) {
			final List<String> allAttributesForTag = new ArrayList<String>(requiredAttributesMap.get(fcTagAttributes.getTagName()));
			allAttributesForTag.addAll(optionalAttributesMap.get(fcTagAttributes.getTagName()));
			allAttributesForTag.addAll(oldAttributesMap.get(fcTagAttributes.getTagName()));
			for (final FcTagAttributes attri : fcTagAttributes.getAttributesList()) {
				if (allAttributesForTag.contains(attri.getVarName().trim())) {
					continue;
				}
				inValidAttributes.add(attri);
			}
			 /*allAttributesForTag = optionalAttributesMap.get(fcTagAttributes.getKey());
				for (final FcTagAttributes attri : fcTagAttributes.getValue()) {
					if (allAttributesForTag.contains(attri.getVarName().trim())) {
						continue;
					}
					inValidAttributes.add(attri);
				}*/
		}

		return inValidAttributes;
	}

	public static List<FcTagAttributes> getabsentReqdAttriutes(final List<TagAttributeList> tagAttriList) {
		final List<FcTagAttributes> absentReqdAttributes = new ArrayList<FcTagAttributes>();
		for (final TagAttributeList fcTagAttributes : tagAttriList) {
			int lineNo = 0;
			int colNo = 0;
			for (final String reqdAttri : requiredAttributesMap.get(fcTagAttributes.getTagName())) {
				if (isEmpty(reqdAttri)) {
					continue;
				}
				boolean found = false;
				for (final FcTagAttributes attribute : fcTagAttributes.getAttributesList()) {
					lineNo = attribute.getVarLineNo();
					colNo = attribute.getVarCol();
					if (attribute.getVarName().equals(reqdAttri)) {
						found = true;
						break;
					}
					//found = false;
				}
				if (!found) {
					absentReqdAttributes.add(new FcTagAttributes(reqdAttri, lineNo, colNo));
				}
			}
		}

		return absentReqdAttributes;
	}
}

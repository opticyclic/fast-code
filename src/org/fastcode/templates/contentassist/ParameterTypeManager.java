package org.fastcode.templates.contentassist;

import static org.fastcode.common.FastCodeConstants.ATTRIBUTE_ALLOWED_VALUES;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN;
import static org.fastcode.common.FastCodeConstants.ATTRIBUTE_PATTERN;
import static org.fastcode.common.FastCodeConstants.ATTRIBUTE_REQUIRED;
import static org.fastcode.common.FastCodeConstants.ATTRIBUTE_VALUE;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PROJECT;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.EQUAL;
import static org.fastcode.common.FastCodeConstants.DOUBLE_QUOTES;
import static org.fastcode.common.FastCodeConstants.STRING_INSTANCE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;
import org.fastcode.util.VelocityUtil;

public class ParameterTypeManager {

	private static SortedSet<String> PARAM_TYPES = new TreeSet<String>();
	private static Map<String, ArrayList<String>> ATTRIBUTES_MAP = new HashMap<String, ArrayList<String>>();

	static {
		PARAM_TYPES.add("class");
		PARAM_TYPES.add("javaProject");
		PARAM_TYPES.add("localvar");
		PARAM_TYPES.add("file");
		PARAM_TYPES.add("package");
		PARAM_TYPES.add("folder");
		PARAM_TYPES.add("project");
		PARAM_TYPES.add("boolean");
		PARAM_TYPES.add("enum");
		PARAM_TYPES.add("interface");
		PARAM_TYPES.add("string");
		PARAM_TYPES.add("int");
		PARAM_TYPES.add("intRange");

	};

	static {
		ATTRIBUTES_MAP.putAll(VelocityUtil.getInstance().getAdditionalParameterAttributeMap(PARAM_TYPES));
		/*
		 * ATTRIBUTES.add(PLACEHOLDER_PROJECT);
		 * ATTRIBUTES.add(ATTRIBUTE_REQUIRED);
		 * ATTRIBUTES.add(ATTRIBUTE_ALLOWED_VALUES);
		 * ATTRIBUTES.add(ATTRIBUTE_VALUE); ATTRIBUTES.add(ATTRIBUTE_PATTERN);
		 */
	}

	public static List<ICompletionProposal> getCompletionProposals(final String element, final int offset, final int length,
			final String paramType, final List<String> proposalsToSkip) {
		final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

		if (element.startsWith(COLON)) {
			final Iterator<String> iter = PARAM_TYPES.iterator();
			while (iter.hasNext()) {
				String proposal = COLON + iter.next();

				if (proposal.toLowerCase().startsWith(element.toLowerCase())) {
					int cursorOffset = proposal.length();
					final String displayString = proposal.substring(1);
					proposal += SPACE;
					cursorOffset += 1;
					proposals.add(createTemplateProposal(proposal, displayString, offset, length, cursorOffset));
				}
			}
		} else if (element.startsWith(LEFT_PAREN)) {
			final ArrayList<String > attriList = ATTRIBUTES_MAP.get(paramType);
			Iterator<String> iter = null;
			if (attriList != null) {
				 iter = ATTRIBUTES_MAP.get(paramType).iterator();
			} else {
				iter = ATTRIBUTES_MAP.get(STRING_INSTANCE).iterator();
			}
			while (iter.hasNext()) {
				String proposal = LEFT_PAREN + iter.next();
				if (proposal.toLowerCase().startsWith(element.toLowerCase())) {
					int cursorOffset = proposal.length();
					final String displayString = proposal.substring(1);
					if (proposal.contains(EQUAL)) {
						proposal += SPACE ;
						cursorOffset += 1;
					} else {
						proposal += EQUAL + DOUBLE_QUOTES + SPACE + DOUBLE_QUOTES + SPACE;
						cursorOffset += 4;
					}

					proposals.add(createTemplateProposal(proposal, displayString, offset, length, cursorOffset));
				}
			}
		} else if (element.startsWith(SPACE)) {
			final ArrayList<String > attriList = ATTRIBUTES_MAP.get(paramType);
			Iterator<String> iter = null;
			if (attriList != null) {
				 iter = ATTRIBUTES_MAP.get(paramType).iterator();
			} else {
				iter = ATTRIBUTES_MAP.get(STRING_INSTANCE).iterator();
			}
			while (iter.hasNext()) {
				String proposal = SPACE + iter.next();
				boolean skipProp = false;
				for (final String propToSkip : proposalsToSkip.toArray(new String[0])) {
					if (proposal.trim().startsWith(propToSkip)) {
						skipProp = true;
						break;
					}
				}

				if (skipProp) {
					continue;
				}
				if (proposal.toLowerCase().startsWith(element.toLowerCase())) {
					int cursorOffset = proposal.length();
					final String displayString = proposal.substring(1);
					if (proposal.contains(EQUAL)) {
						proposal += SPACE ;
						cursorOffset += 1;
					} else {
						proposal += EQUAL + DOUBLE_QUOTES + SPACE + DOUBLE_QUOTES + SPACE;
						cursorOffset += 4;
					}
					proposals.add(createTemplateProposal(proposal, displayString, offset, length, cursorOffset));
				}
			}
		}

		return proposals;
	}

	private static TemplateProposal createTemplateProposal(final String proposal, final String displayString, final int offset,
			final int length, final int cursorOffset) {
		return new TemplateProposal(proposal, displayString, offset, length, new Point(offset + cursorOffset, 0));
	}

	/**
	 * Get all directives.
	 *
	 * @return the directives
	 */
	public static String[] getDirectives() {
		return PARAM_TYPES.toArray(new String[PARAM_TYPES.size()]);
	}

}

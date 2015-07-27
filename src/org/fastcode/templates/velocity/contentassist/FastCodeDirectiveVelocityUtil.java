package org.fastcode.templates.velocity.contentassist;

import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DOLLAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL;
import static org.fastcode.common.FastCodeConstants.RIGHT_CURL;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;
import static org.fastcode.util.StringUtil.isEmpty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;
import org.fastcode.common.FastCodeConstants.FIRST_TEMPLATE;
import org.fastcode.common.FastCodeConstants.SECOND_TEMPLATE;
import org.fastcode.templates.contentassist.ElementProposal;
import org.fastcode.templates.contentassist.TemplateProposal;
import org.fastcode.templates.util.VariablesUtil;
import org.fastcode.util.VelocityUtil;
import static org.fastcode.common.FastCodeConstants.DB_FOREACH_VARS;
import static org.fastcode.util.FastCodeUtil.getEmptyArrayForNull;

/**
 * Manager for Velocity directives.
 */
public class FastCodeDirectiveVelocityUtil {
	private static Set<String>	SINGLE_LINE_DIRECTIVES	= new HashSet<String>();
	private static Set<String>	MULTI_LINE_DIRECTIVES	= new HashSet<String>();
	private static Set<String>	ALL_DIRECTIVES			= new TreeSet<String>();	// sorted
	private static Set<String>	PARANTHESIS_DIRECTIVES	= new HashSet<String>();
	private static Set<String>	END_DIRECTIVES			= new HashSet<String>();

	static {
		SINGLE_LINE_DIRECTIVES.add("#set");
		SINGLE_LINE_DIRECTIVES.add("#stop");
		SINGLE_LINE_DIRECTIVES.add("#parse");
		SINGLE_LINE_DIRECTIVES.add("#include");

		MULTI_LINE_DIRECTIVES.add("#if");
		MULTI_LINE_DIRECTIVES.add("#if-else");
		MULTI_LINE_DIRECTIVES.add("#if-else-if");
		MULTI_LINE_DIRECTIVES.add("#macro");
		MULTI_LINE_DIRECTIVES.add("#foreach");

		ALL_DIRECTIVES.add("#end");
		ALL_DIRECTIVES.add("#else");
		ALL_DIRECTIVES.add("#elseif");
		ALL_DIRECTIVES.addAll(SINGLE_LINE_DIRECTIVES);
		ALL_DIRECTIVES.addAll(MULTI_LINE_DIRECTIVES);

		PARANTHESIS_DIRECTIVES.add("#set");
		PARANTHESIS_DIRECTIVES.add("#if");
		PARANTHESIS_DIRECTIVES.add("#elseif");
		PARANTHESIS_DIRECTIVES.add("#foreach");
		PARANTHESIS_DIRECTIVES.add("#include");
		PARANTHESIS_DIRECTIVES.add("#parse");
		PARANTHESIS_DIRECTIVES.add("#macro");
		//PARANTHESIS_DIRECTIVES.add("#if else");

		END_DIRECTIVES.add("#if");
		END_DIRECTIVES.add("#elseif");
		END_DIRECTIVES.add("#foreach");
		//END_DIRECTIVES.add("#if else");
	}

	/**
	 * Get all directives.
	 *
	 * @return the directives
	 */
	public static String[] getDirectives() {
		return ALL_DIRECTIVES.toArray(new String[ALL_DIRECTIVES.size()]);
	}

	/**
	 * Checks if is multi line directive.
	 *
	 * @param directive the directive
	 *
	 * @return true, if is multi line directive
	 */
	public static boolean isMultiLineDirective(final String directive) {
		return MULTI_LINE_DIRECTIVES.contains(directive);
	}

	/**
	 * Checks if the given directive has parenthesis.
	 *
	 * @param directive the directive
	 *
	 * @return true, if has parenthesis
	 */
	public static boolean hasParenthesis(final String directive) {
		return PARANTHESIS_DIRECTIVES.contains(directive);
	}

	/**
	 * Checks if the given directive has end tags.
	 *
	 * @param directive
	 *            the directive
	 *
	 * @return true, if has end tag
	 */
	private static boolean hasEndProposal(final String directive) {
		return END_DIRECTIVES.contains(directive);
	}

	/**
	 * Gets the completion proposals for the given element.
	 *
	 * @param element the element
	 * @param offset the offset
	 * @param length the length
	 *
	 * @return the completion proposals
	 */
	public static List<ICompletionProposal> getCompletionProposals(final String element, final int offset, final int length,
			final FIRST_TEMPLATE firstTemplateItem, final SECOND_TEMPLATE secondTemplateItem, final String templatePrefix) {
		final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

		if (element.startsWith("#")) {
			final Iterator<String> iter = ALL_DIRECTIVES.iterator();
			while (iter.hasNext()) {
				String proposal = iter.next();
				if (proposal.toLowerCase().startsWith(element.toLowerCase())) {
					int cursorOffset = proposal.length();
					String displayString = proposal;
					if (hasParenthesis(proposal)) {
						proposal += "()";
						cursorOffset = proposal.length();
					}
					if (hasEndProposal(proposal.substring(0, proposal.length() - 2))) {
						proposal += "\n#end";
						cursorOffset = proposal.length();
					}

					boolean propAdded = false;
					if (proposal.equals("#if-else-if")) {
						final String proposalString = "#if () \n#elseif () \n#else \n#end";
						displayString = proposal;
						cursorOffset = proposalString.length();
						proposals.add(createTemplateProposal(proposalString, displayString, offset, length, cursorOffset));
						propAdded = true;
					}

					if (proposal.equals("#if-else")) {
						final String proposalString = "#if () \n#else \n#end";
						displayString = proposal;
						cursorOffset = proposalString.length();
						proposals.add(createTemplateProposal(proposalString, displayString, offset, length, cursorOffset));
						propAdded = true;
					}

					if (!propAdded) {
						proposals.add(createTemplateProposal(proposal, displayString, offset, length, cursorOffset));
					}


					if (proposal.startsWith("#foreach")) {
						if (secondTemplateItem != null && !secondTemplateItem.equals(SECOND_TEMPLATE.none)) {
							final String first_part = proposal.substring(0, 9);
							final String last_part = proposal.substring(9, proposal.length());
							if (secondTemplateItem.equals(SECOND_TEMPLATE.custom) || secondTemplateItem.equals(SECOND_TEMPLATE.both)) {

								String middle_part = DOLLAR + SECOND_TEMPLATE.field.getValue() + SPACE + "in" + SPACE + DOLLAR + LEFT_CURL
										+ VariablesUtil.getPlural(SECOND_TEMPLATE.field.getValue()) + RIGHT_CURL;
								String proposalString = first_part + middle_part + last_part;
								displayString = proposalString;
								cursorOffset = proposalString.length();
								proposals.add(createTemplateProposal(proposalString, displayString, offset, length, cursorOffset));

								middle_part = DOLLAR + SECOND_TEMPLATE.method.getValue() + SPACE + "in" + SPACE + DOLLAR + LEFT_CURL
										+ VariablesUtil.getPlural(SECOND_TEMPLATE.method.getValue()) + RIGHT_CURL;
								proposalString = first_part + middle_part + last_part;
								displayString = proposalString;
								cursorOffset = proposalString.length();
								proposals.add(createTemplateProposal(proposalString, displayString, offset, length, cursorOffset));
							} else if (secondTemplateItem.equals(SECOND_TEMPLATE.data)) {
								final String middle_part = DOLLAR + secondTemplateItem.getValue() + SPACE + "in" + SPACE + DOLLAR + LEFT_CURL
										+ VariablesUtil.getInstance().getFilePlaceholderValue() + RIGHT_CURL;
								final String proposalString = first_part + middle_part + last_part;
								displayString = proposalString;
								cursorOffset = proposalString.length();
								proposals.add(createTemplateProposal(proposalString, displayString, offset, length, cursorOffset));
							} else {
								
								final String middle_part = DOLLAR + secondTemplateItem.getValue() + SPACE + "in" + SPACE + DOLLAR + LEFT_CURL
										+ VariablesUtil.getPlural(secondTemplateItem.getValue()) + RIGHT_CURL;
								final String proposalString = first_part + middle_part + last_part;
								displayString = proposalString;
								cursorOffset = proposalString.length();
								proposals.add(createTemplateProposal(proposalString, displayString, offset, length, cursorOffset));
							}
						}
						if (!isEmpty(templatePrefix) && templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX)) {
							final String first_part = proposal.substring(0, 9);
							final String last_part = proposal.substring(9, proposal.length());
							for (final String var : getEmptyArrayForNull(VelocityUtil.getInstance().getPropertyValue(DB_FOREACH_VARS, EMPTY_STR).split(COMMA))) {
								final String middle_part = DOLLAR + var.substring(0, var.length()-1) + SPACE + "in" + SPACE + DOLLAR + LEFT_CURL
										+ var + RIGHT_CURL;
								final String proposalString = first_part + middle_part + last_part;
								displayString = proposalString;
								cursorOffset = proposalString.length();
								proposals.add(createTemplateProposal(proposalString, displayString, offset, length, cursorOffset));
							}
						}
					}
				}
			}
		} else if (element.startsWith("=\"#")) {
			String proposal = firstTemplateItem.getValue();
			int cursorOffset = 0;
			String displayString = null;
			if (!proposal.endsWith(FIRST_TEMPLATE.None.getValue()) && proposal.toLowerCase().startsWith(element.substring(3).toLowerCase())) {
				cursorOffset = proposal.length();
				displayString = proposal;
				proposal = "=\"#" + proposal;
				cursorOffset = proposal.length();
				proposals.add(createTemplateProposal(proposal, displayString, offset, length, cursorOffset));
			}

			proposal = secondTemplateItem.getValue();
			cursorOffset = 0;
			displayString = null;
			if (!(proposal.endsWith(SECOND_TEMPLATE.none.getValue()) || proposal.endsWith(SECOND_TEMPLATE.field.getValue()))
					&& proposal.toLowerCase().startsWith(element.substring(3).toLowerCase())) {
				cursorOffset = proposal.length();
				displayString = proposal;
				proposal = "=\"#" + proposal;
				cursorOffset = proposal.length();
				proposals.add(createTemplateProposal(proposal, displayString, offset, length, cursorOffset));
			}

			final VariablesUtil vutil = VariablesUtil.getInstance();

			final Iterator<ElementProposal> iter1 = vutil.getAddlParamList().iterator();

			while (iter1.hasNext()) {
				final ElementProposal propertyProposal = iter1.next();

				if (element.length() == 0 || propertyProposal.getProposal().toLowerCase().startsWith(element.substring(3).toLowerCase())) {
					proposal = "=\"#" + propertyProposal.getProposal();
					proposals.add(createTemplateProposal(proposal, propertyProposal.getDisplayString(), offset, length, cursorOffset));
				}

			}

		}

		return proposals;
	}

	/**
	 * Creates a template proposal.
	 *
	 * @param proposal the proposal
	 * @param displayString the display string
	 * @param offset the offset
	 * @param length the length
	 * @param cursorOffset the cursor offset
	 *
	 * @return the template proposal
	 */
	private static ICompletionProposal createTemplateProposal(final String proposal, final String displayString, final int offset,
			final int length, final int cursorOffset) {
		return new TemplateProposal(proposal, displayString, offset, length, new Point(offset + cursorOffset, 0));
	}
}

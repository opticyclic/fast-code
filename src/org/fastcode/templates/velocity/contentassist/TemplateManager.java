package org.fastcode.templates.velocity.contentassist;

import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.FC_LOCAL_VAL_LIST;
import static org.fastcode.common.FastCodeConstants.HYPHEN;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.preferences.PreferenceConstants.P_FILE_TEMPLATE_PLACHOLDER_NAME;
import static org.fastcode.util.StringUtil.isEmpty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeConstants.FIRST_TEMPLATE;
import org.fastcode.common.FastCodeConstants.SECOND_TEMPLATE;
import org.fastcode.templates.contentassist.ElementProposal;
import org.fastcode.templates.contentassist.FunctionProposal;
import org.fastcode.templates.contentassist.TemplateProposal;
import org.fastcode.templates.util.ContentAssistUtil;
import org.fastcode.templates.util.FastCodeLocalVariables;
import org.fastcode.templates.util.ForLoopVariable;
import org.fastcode.templates.util.SetVariable;
import org.fastcode.templates.util.VariablesUtil;
import org.fastcode.util.VelocityUtil;

/**
 * Manager for Velocity references.
 */
public class TemplateManager {

	private static final Map<String, ArrayList<ElementProposal>>	REFERENCE_PROPOSALS_MAP	= ContentAssistUtil
																									.getReferenceProposals("reference-proposal.xml");
	private static List<ElementProposal>							REFERENCE_PROPOSALS		= new ArrayList<ElementProposal>();
	private static final List<ElementProposal>						BASE_PROPERTIES			= ContentAssistUtil
																									.getBasicProposals("reference-proposal.xml");
	private static final List<ElementProposal>						DEFAULT_PROPERTIES		= ContentAssistUtil
																									.getBasicProposals("default-reference-proposal.xml");

	/**
	 * Gets the completion proposals for the given element.
	 *
	 * @param element
	 *            the element
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @param templateItemsMap
	 *            the properties
	 * @param propertiesOnly
	 *            true, if properties only should be suggested
	 * @param templateBody
	 * @param currentLine
	 * @param atEOF
	 *
	 * @return the list of proposals
	 */
	public static List<ICompletionProposal> getCompletionProposals(String element, final int offset, final int length,
			final Map<FIRST_TEMPLATE, SECOND_TEMPLATE> templateItemsMap, final boolean propertiesOnly,
			final FIRST_TEMPLATE firstTemplateItem, final String templateBody, final int currentLine, final boolean atEOF) { //, final String textUptoOffset
		final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

		boolean silent = false;
		if (element.startsWith("$!{")) {
			silent = true;
			element = element.substring(3);
		} else if (element.startsWith("$!")) {
			silent = true;
			element = element.substring(2);
		} else if (element.startsWith("${")) {
			element = element.substring(2);
		} else if (element.startsWith("$")) {
			element = element.substring(1);
		}

		else {
			return proposals;
		}

		if (element.indexOf('.') == -1) {
			if (firstTemplateItem != null) {
				return getElementProposals(element, offset, length, silent, templateItemsMap, propertiesOnly, firstTemplateItem,
						templateBody, currentLine, atEOF); //, textUptoOffset
			} else {
				return getElementProposals(element, offset, length, silent, templateItemsMap, propertiesOnly);
			}
		} else if (!propertiesOnly) {
			// return getFunctionProposals(element, offset, length, silent);
		}

		return proposals;
	}

	/**
	 * Gets the description for the given element.
	 *
	 * @param element
	 *            the element
	 *
	 * @return the element description
	 */
	public static String getElementDescription(final String element, final Map<String, String> properties) {
		ElementProposal referenceProposal = getMatchingProposal(element, new ArrayList<ElementProposal>(REFERENCE_PROPOSALS));
		if (referenceProposal != null) {
			return referenceProposal.getDescription();
		}

		referenceProposal = getMatchingProposal(element, BASE_PROPERTIES);
		if (referenceProposal != null) {
			return referenceProposal.getDescription();
		}

		// try to get property
		return properties.get(element);
	}

	/**
	 * Gets the element proposals for the given element.
	 *
	 * @param element
	 *            the element
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @param silent
	 *            true for silent references ($!)
	 * @param propertiesOnly
	 *            true, if properties only should be suggested
	 * @param templateBody
	 * @param currentLine
	 *            line number of the current cursor position
	 * @param atEOF
	 *
	 * @return list of element proposals
	 */
	private static List<ICompletionProposal> getElementProposals(final String element, final int offset, final int length,
			final boolean silent, final Map<FIRST_TEMPLATE, SECOND_TEMPLATE> templateItemsMap, final boolean propertiesOnly,
			final FIRST_TEMPLATE firstTemplateItem, final String templateBody, final int currentLine, final boolean atEOF) { //, final String textUptoOffset
		final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

		/*final VelocityUtil veloUtil = VelocityUtil.getInstance();
		List<String> setVarlist = null;
		try {
			setVarlist = veloUtil.getListofSetVariables(textUptoOffset);
			System.out.println(setVarlist);
			for (final String var : setVarlist) {
				REFERENCE_PROPOSALS.add(new ElementProposal(var, var, ""));
			}
			setVarlist.clear();
		} catch (final Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}*/
		//System.out.println(setVarlist);
		final SECOND_TEMPLATE secondTempalteItem = templateItemsMap.get(firstTemplateItem);
		if (!firstTemplateItem.equals(FIRST_TEMPLATE.None)) {
			if (!firstTemplateItem.equals(FIRST_TEMPLATE.Enumeration)) {
				REFERENCE_PROPOSALS.addAll(REFERENCE_PROPOSALS_MAP.get(firstTemplateItem.getValue()));
			}

			final ArrayList<ElementProposal> elementProposals = REFERENCE_PROPOSALS_MAP.get(secondTempalteItem.getValue() + "s");
			if (elementProposals != null) {
				REFERENCE_PROPOSALS.addAll(elementProposals);
			} else {
				if (secondTempalteItem.equals(SECOND_TEMPLATE.both) || secondTempalteItem.equals(SECOND_TEMPLATE.custom)) {
					REFERENCE_PROPOSALS.addAll(REFERENCE_PROPOSALS_MAP.get("field"));
					REFERENCE_PROPOSALS.addAll(REFERENCE_PROPOSALS_MAP.get("method"));
				}
				if (secondTempalteItem.equals(SECOND_TEMPLATE.data)) {
					final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
					final String dataFilePlaceHolder = store.getString(P_FILE_TEMPLATE_PLACHOLDER_NAME);
					REFERENCE_PROPOSALS.add(new ElementProposal(dataFilePlaceHolder, dataFilePlaceHolder, ""));
				}
				if (secondTempalteItem.equals(SECOND_TEMPLATE.property)) {
					REFERENCE_PROPOSALS.addAll(REFERENCE_PROPOSALS_MAP.get(SECOND_TEMPLATE.property));
				}
			}

		}

		Iterator<ElementProposal> iter = REFERENCE_PROPOSALS.iterator();

		while (iter.hasNext()) {

			final ElementProposal propertyProposal = iter.next();

			if (element.length() == 0 || propertyProposal.getProposal().toLowerCase().startsWith(element.toLowerCase())) {
				proposals.add(createTemplateProposal(propertyProposal, offset, length, silent));
			}

		}

		iter = DEFAULT_PROPERTIES.iterator();

		while (iter.hasNext()) {
			final ElementProposal propertyProposal = iter.next();

			if (element.length() == 0 || propertyProposal.getProposal().toLowerCase().startsWith(element.toLowerCase())) {
				proposals.add(createTemplateProposal(propertyProposal, offset, length, silent));
			}

		}

		final VariablesUtil vutil = VariablesUtil.getInstance();

		iter = vutil.getVariablesList().iterator();

		while (iter.hasNext()) {
			final ElementProposal propertyProposal = iter.next();

			if (element.length() == 0 || propertyProposal.getProposal().toLowerCase().startsWith(element.toLowerCase())) {
				proposals.add(createTemplateProposal(propertyProposal, offset, length, silent));
			}

		}

		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		//final boolean validateVariables = false;
		final VelocityUtil velocityUtil = VelocityUtil.getInstance();
		final boolean showErrorMessage = true;
		final Map<String, Object> allVariablesMap = velocityUtil.getVariablesFromTemplateBody(templateBody, firstTemplateItem.getValue(),
				secondTempalteItem.getValue(), vutil.getAdditnlParamList(), EMPTY_STR, EMPTY_STR, EMPTY_STR, preferenceStore, false, atEOF, null, showErrorMessage);
		/*final List<String> validVariables = (List<String>) allVariablesMap.get(VALID_VARIABLES);
		final List<String> localVariables = (List<String>) allVariablesMap.get(LOCAL_VARIABLES);
		final List<String> setVariables = (List<String>) allVariablesMap.get(SET_VARIABLES);*/
		/*System.out.println(offset);
		System.out.println(length);*/
		boolean forLoopVarInScope = false;
		boolean setVarInScope = false;
		String forLoopLocalVar = EMPTY_STR;
		final List<FastCodeLocalVariables> fastCodeAllLocalVariables = (List<FastCodeLocalVariables>) allVariablesMap.get(FC_LOCAL_VAL_LIST);
		for (final FastCodeLocalVariables fastCodeLocalVariables : fastCodeAllLocalVariables) {
			if (fastCodeLocalVariables instanceof ForLoopVariable) {
				if (currentLine >= fastCodeLocalVariables.getScopeStartLine() && currentLine <= fastCodeLocalVariables.getScopeEndLine()) {
					forLoopVarInScope = true;
					forLoopLocalVar = fastCodeLocalVariables.getVarName();
					//add proposal for this for loop variable
					final ArrayList<String> functions = ContentAssistUtil.getTypefunctionmap(secondTempalteItem.getValue());
					ElementProposal proposal;
					if (functions != null) {
						for (final String function : functions) {
							proposal = new ElementProposal(forLoopLocalVar + DOT + function, forLoopLocalVar, SPACE
									+ HYPHEN
									+ SPACE
									+ (function.contains(DOT) ? function.substring(0, function.indexOf(DOT)) + SPACE
											+ function.substring(function.indexOf(DOT) + 1, function.length()) : function));
							proposals.add(createTemplateProposal(proposal, offset, length, silent));
						}
					}
				}
			} else if (fastCodeLocalVariables instanceof SetVariable) {
				if (currentLine >= fastCodeLocalVariables.getScopeStartLine()) {
					setVarInScope = true;
					final ElementProposal proposal = new ElementProposal(fastCodeLocalVariables.getVarName(), /*fastCodeLocalVariables.getVarName()*/ "", fastCodeLocalVariables.getVarName());
					proposals.add(createTemplateProposal(proposal, offset, length, silent));
				}
			}
		}
		REFERENCE_PROPOSALS.clear();
		forLoopLocalVar = EMPTY_STR;
		return proposals;
	}

	private static List<ICompletionProposal> getElementProposals(final String element, final int offset, final int length,
			final boolean silent, final Map<FIRST_TEMPLATE, SECOND_TEMPLATE> templateItemsMap, final boolean propertiesOnly) {
		final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

		//		Iterator<ElementProposal> iter = BASE_PROPERTIES.iterator();
		//		while (iter.hasNext()) {
		//			final ElementProposal propertyProposal = iter.next();
		//
		//			if (element.length() == 0 || propertyProposal.getProposal().startsWith(element)) {
		//				proposals.add(createTemplateProposal(propertyProposal, offset, length, silent));
		//			}
		//
		//		}

		final Iterator<ElementProposal> iter = DEFAULT_PROPERTIES.iterator();

		while (iter.hasNext()) {
			final ElementProposal propertyProposal = iter.next();

			if (element.length() == 0 || propertyProposal.getProposal().toLowerCase().startsWith(element.toLowerCase())) {
				proposals.add(createTemplateProposal(propertyProposal, offset, length, silent));
			}

		}

		final VariablesUtil vutil = VariablesUtil.getInstance();

		final Iterator<ElementProposal> iter1 = vutil.getVariablesList().iterator();

		while (iter1.hasNext()) {
			final ElementProposal propertyProposal = iter1.next();

			if (element.length() == 0 || propertyProposal.getProposal().toLowerCase().startsWith(element.toLowerCase())) {
				proposals.add(createTemplateProposal(propertyProposal, offset, length, silent));
			}

		}

		return proposals;
	}

	/**
	 * Gets the function proposals for the given element.
	 *
	 * @param element
	 *            the element
	 * @param offset
	 *            the offset
	 * @param length
	 *            the length
	 * @param silent
	 *            true for silent references ($!)
	 *
	 * @return list of function proposals
	 */
	private static List<ICompletionProposal> getFunctionProposals(final String element, final int offset, final int length,
			final boolean silent) {
		final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

		final String[] tokens = element.split("\\.");
		// if (tokens.length == 0 ||
		// !tokens[0].equals(VelocityTemplateManager.KEY_ELEMENT)) {
		// return proposals;
		// }

		final int level = element.endsWith(".") ? tokens.length : tokens.length - 1;
		final Set<FunctionProposal> functionProposals = getValidFunctionProposals(getMatchingProposal(tokens[level - 1],
				new ArrayList<ElementProposal>(REFERENCE_PROPOSALS)));

		for (int i = 1; i < level - 1; ++i) {
			final ElementProposal rp = getMatchingProposal(tokens[i], new ArrayList<ElementProposal>(REFERENCE_PROPOSALS));
			if (rp == null) {
				return proposals; // invalid reference -> empty proposals
			} else if (!rp.isMultiple()) {
				functionProposals.remove(rp); // alread used
			}
		}

		final String functionStart = element.endsWith(".") ? "" : tokens[tokens.length - 1];

		final Iterator<FunctionProposal> iter = functionProposals.iterator();
		while (iter.hasNext()) {
			final FunctionProposal function = iter.next();
			if (functionStart.length() == 0 || function.getProposal().startsWith(functionStart)) {
				final String elementStart = element.substring(0, element.lastIndexOf('.'));
				proposals.add(createTemplateProposal(elementStart + ".", function, offset, length, silent));
			}
		}

		return proposals;
	}

	/**
	 * Gets the valid function proposals for the given predecessor.
	 *
	 * @param predecessor
	 *            the predecessor
	 *
	 * @return the valid function proposals
	 */
	private static Set<FunctionProposal> getValidFunctionProposals(final ElementProposal predecessor) {
		final Set<FunctionProposal> functionProposals = new LinkedHashSet<FunctionProposal>();

		if (predecessor == null) {
			return functionProposals;
		}

		final Iterator<ElementProposal> iter = REFERENCE_PROPOSALS.iterator();
		while (iter.hasNext()) {
			final ElementProposal fp = iter.next();
			if (fp instanceof FunctionProposal && ((FunctionProposal) fp).isPredecessor(predecessor)) {
				functionProposals.add((FunctionProposal) fp);
			}
		}

		return functionProposals;
	}

	/**
	 * Gets the matching proposals for the given token.
	 *
	 * @param token
	 *            the token
	 * @param proposals
	 *            list of possible proposals
	 *
	 * @return the matching proposal
	 */
	private static ElementProposal getMatchingProposal(final String token, final List<ElementProposal> proposals) {
		ElementProposal referenceProposal = null;

		final Iterator<ElementProposal> iter = proposals.iterator();
		while (iter.hasNext()) {
			final ElementProposal fp = iter.next();
			if (fp.matches(token)) {
				referenceProposal = fp;
				break;
			}
		}

		return referenceProposal;
	}

	private static String createReferenceString(final String element, final boolean silent) {
		return (silent ? "$!{" : "${") + element + "}";
	}

	private static TemplateProposal createTemplateProposal(final ElementProposal referenceProposal, final int offset, final int length,
			final boolean silent) {
		return createTemplateProposal("", referenceProposal, offset, length, silent);
	}

	private static TemplateProposal createTemplateProposal(final String prefix, final ElementProposal referenceProposal, final int offset,
			final int length, final boolean silent) {

		final String proposal = createReferenceString(prefix + referenceProposal.getProposal(), silent);

		return new TemplateProposal(proposal, referenceProposal.getDisplayString(), offset, length, new Point(offset + proposal.length()
				+ referenceProposal.getSelectionOffset(), referenceProposal.getSelectionLength()));
	}
}

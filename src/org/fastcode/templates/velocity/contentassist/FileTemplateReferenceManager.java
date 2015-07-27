package org.fastcode.templates.velocity.contentassist;


	import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;
import org.fastcode.templates.contentassist.ElementProposal;
import org.fastcode.templates.contentassist.FunctionProposal;
import org.fastcode.templates.contentassist.TemplateProposal;
import org.fastcode.templates.util.ContentAssistUtil;
import org.fastcode.templates.util.VariablesUtil;

	/**
	 * Manager for Velocity references.
	 */
	public class FileTemplateReferenceManager {

		private static final Map<String,ArrayList<ElementProposal>> REFERENCE_PROPOSALS_MAP = ContentAssistUtil.getReferenceProposals("file-reference-proposal.xml");
		private static final List<ElementProposal>	BASE_PROPERTIES		= ContentAssistUtil.getBasicProposals("file-reference-proposal.xml");
		private static final List<ElementProposal>	DEFAULT_PROPERTIES		= ContentAssistUtil.getBasicProposals("default-reference-proposal.xml");

		/**
		 * Gets the completion proposals for the given element.
		 *
		 * @param element
		 *            the element
		 * @param offset
		 *            the offset
		 * @param length
		 *            the length
		 * @param properties
		 *            the properties
		 * @param propertiesOnly
		 *            true, if properties only should be suggested
		 *
		 * @return the list of proposals
		 */
		public static List<ICompletionProposal> getCompletionProposals(String element, final int offset, final int length,
				final Map<String, String> properties, final boolean propertiesOnly) {
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

					return getElementProposals(element, offset, length, silent, properties, propertiesOnly);

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
			ElementProposal referenceProposal = getMatchingProposal(element, new ArrayList<ElementProposal>(REFERENCE_PROPOSALS_MAP.get(0)));
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


		private static List<ICompletionProposal> getElementProposals(final String element, final int offset, final int length,
				final boolean silent, final Map<String, String> properties, final boolean propertiesOnly) {
			final List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

			Iterator<ElementProposal> iter = BASE_PROPERTIES.iterator();
			while (iter.hasNext()) {
				final ElementProposal propertyProposal = iter.next();

				if (element.length() == 0 || propertyProposal.getProposal().startsWith(element)) {
					proposals.add(createTemplateProposal(propertyProposal, offset, length, silent));
				}

			}

			iter = DEFAULT_PROPERTIES.iterator();
			while (iter.hasNext()) {
				final ElementProposal propertyProposal = iter.next();

				if (element.length() == 0 || propertyProposal.getProposal().startsWith(element)) {
					proposals.add(createTemplateProposal(propertyProposal, offset, length, silent));
				}

			}

			final VariablesUtil vutil = VariablesUtil.getInstance();

			final Iterator<ElementProposal> iter1 = vutil.getVariablesList().iterator();

			while (iter1.hasNext()) {
				final ElementProposal propertyProposal = iter1.next();

				if (element.length() == 0 || propertyProposal.getProposal().startsWith(element)) {
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
			final Set<FunctionProposal> functionProposals = getValidFunctionProposals(getMatchingProposal(tokens[level - 1],new ArrayList<ElementProposal>(REFERENCE_PROPOSALS_MAP.get(0))));

			for (int i = 1; i < level - 1; ++i) {
				final ElementProposal rp = getMatchingProposal(tokens[i], new ArrayList<ElementProposal>(REFERENCE_PROPOSALS_MAP.get(0)));
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

			final Iterator<ElementProposal> iter = REFERENCE_PROPOSALS_MAP.get(0).iterator();
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

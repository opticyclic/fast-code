package org.fastcode.templates.velocity.contentassist;

import static org.fastcode.common.FastCodeConstants.DOUBLE_SLASH_CHAR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.fastcode.FastCodeColorManager;
import org.fastcode.templates.contentassist.AbstractTemplateContentAssistant;
import org.fastcode.templates.rules.FastCodeTemplateCodeScanner;
import org.fastcode.templates.rules.IRulesStrategy;
import org.fastcode.templates.velocity.rules.TemplateRulesStartStrategie;

	/**
	 * Content assistant for Velocity references.
	 */


	/**
	 * Content assistant for Velocity references.
	 */

	public class FileTemplateContentAssist  extends AbstractTemplateContentAssistant {
		private final Map<String, String> properties;
		private final boolean propertiesOnly;
		private final ITokenScanner referenceScanner;



		/**
		 * Instantiates a new reference content assistant.
		 *
		 * @param propertiesOnly true, if properties only should be suggested
		 * @param properties the properties
		 */
		public FileTemplateContentAssist(final Map<String, String> properties, final boolean propertiesOnly) {
			super(new String[] {"$"}, propertiesOnly ? new char[] {'$'} : new char[] {'$', '.'});

			this.properties = properties;
			this.propertiesOnly = propertiesOnly;


			final IRulesStrategy[] ruleStrategies = {
					new TemplateRulesStartStrategie()};
			this.referenceScanner = new FastCodeTemplateCodeScanner(ruleStrategies);
		}

		/*
		 * @see org.fastcode.templates.contentassist.AbstractTemplateContentAssistant#getCompletionProposals(org.eclipse.jface.text.IDocument, int, int)
		 */
		@Override
		public List<ICompletionProposal> getCompletionProposals(final IDocument document, final int offset, final int length, final String spaceToPad) {
			try {
				final String element = getElement(document, offset, length);

				if (element != null) {
					return FileTemplateReferenceManager.getCompletionProposals(element, offset,	length, this.properties, this.propertiesOnly);
				}
			} catch (final Exception e) {e.printStackTrace();}

			return new ArrayList<ICompletionProposal>();
		}

		private String getElement(final IDocument document, final int offset, final int length)
													throws BadLocationException {
			if (length <= 0 || offset > 0 &&
					document.getChar(offset - 1) == DOUBLE_SLASH_CHAR) { // escaped reference
				return null;
			}

			final String element = document.get(offset, length);
			if (element.startsWith("$") && isValidReference(document, offset, length)) {
				return element;
			}
			return null;
		}

		private boolean isValidReference(final IDocument document, final int offset, final int length) {
			this.referenceScanner.setRange(document, offset, length);

			final IToken token = this.referenceScanner.nextToken();

			return  this.referenceScanner.getTokenOffset() == offset &&
					this.referenceScanner.getTokenLength() == length &&
					token.equals(FastCodeColorManager.getToken("VARIABLE"));
		}





}

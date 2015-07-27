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
import org.fastcode.common.FastCodeConstants.FIRST_TEMPLATE;
import org.fastcode.common.FastCodeConstants.SECOND_TEMPLATE;
import org.fastcode.templates.contentassist.AbstractTemplateContentAssistant;
import org.fastcode.templates.rules.FastCodeTemplateCodeScanner;
import org.fastcode.templates.rules.IRulesStrategy;
import org.fastcode.templates.velocity.rules.TemplateRulesStartStrategie;


/**
 * Content assistant for Velocity references.
 */
public class TemplateContentAssistant extends AbstractTemplateContentAssistant {
	private final Map<FIRST_TEMPLATE, SECOND_TEMPLATE>	templateItemsMap;
	private final boolean								propertiesOnly;
	private final ITokenScanner							referenceScanner;
	private final FIRST_TEMPLATE						firstTemplateItem;

	/**
	 * Instantiates a new reference content assistant.
	 *
	 * @param propertiesOnly true, if properties only should be suggested
	 * @param templateItemsMap the properties
	 */
	public TemplateContentAssistant(final Map<FIRST_TEMPLATE, SECOND_TEMPLATE> templateItemsMap, final boolean propertiesOnly) {
		super(new String[] { "$" }, propertiesOnly ? new char[] { '$' } : new char[] { '$', '.' });

		this.templateItemsMap = templateItemsMap;
		this.propertiesOnly = propertiesOnly;
		this.firstTemplateItem = templateItemsMap != null ? (FIRST_TEMPLATE) templateItemsMap.keySet().toArray()[0] : null;

		final IRulesStrategy[] ruleStrategies = { new TemplateRulesStartStrategie() };
		this.referenceScanner = new FastCodeTemplateCodeScanner(ruleStrategies);
	}

	/*
	 * @see org.fastcode.templates.contentassist.AbstractTemplateContentAssistant#getCompletionProposals(org.eclipse.jface.text.IDocument, int, int)
	 */
	@Override
	public List<ICompletionProposal> getCompletionProposals(final IDocument document, final int offset, final int length, final String spaceToPad) {
		try {
			final String templateBody = document.get(0, document.getLength());
			final String element = getElement(document, offset, length);
			if (element != null) {
				System.out.println(document.getLength() + " -- " + offset);
				/*final String text = document.get(0, offset == document.getLength() -1 ? offset : document.getLength()); //offset); //document.getLength());
				System.out.println(text);*/
				System.out.println("if--" + document.getLineOfOffset(document.getLength()));
				//System.out.println("else--" + document.getLineOfOffset(offset + 1));

				/*the below code is used to get the current line number of the cursor
				 * document.getLineOfOffset -- starts from 0, so adding 1 after all calculation
				 * offset is the number of chars before the $ sign, excluding $
				 * control comes here as soon as we type $, so cursor will be in front of dollar
				 */
				final int currLine = offset + 1 == document.getLength() ? document.getLineOfOffset(document.getLength()) + 1 : document.getLineOfOffset(offset + 1) + 1;
				System.out.println("currentline--" + currLine);
				final boolean atEOF = offset + 1 == document.getLength(); //this is needed so that velocity engine does not error out, when we are parsing template body, while coding the template
				return TemplateManager.getCompletionProposals(element, offset, length, this.templateItemsMap, this.propertiesOnly,
						this.firstTemplateItem, templateBody, currLine, atEOF); //, text
			}
		} catch (final Exception e) {/* ignore */
			e.printStackTrace();
		}

		return new ArrayList<ICompletionProposal>();
	}

	private String getElement(final IDocument document, final int offset, final int length) throws BadLocationException {
		if (length <= 0 || offset > 0 && document.getChar(offset - 1) == DOUBLE_SLASH_CHAR) { // escaped reference
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

		return this.referenceScanner.getTokenOffset() == offset && this.referenceScanner.getTokenLength() == length
				&& token.equals(FastCodeColorManager.getToken("VARIABLE"));
	}
}

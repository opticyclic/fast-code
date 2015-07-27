package org.fastcode.templates.contentassist;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.TAB;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.replaceSpecialChars;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.fastcode.util.FastCodeUtil;

/**
 * Template assist processor.
 */
public class TemplateAssistProcessor implements IContentAssistProcessor {
	private ITemplateContentAssistant[]	assistants;

	public TemplateAssistProcessor() {
		//super();
	}

	/**
	 * Instantiates a new template assist processor.
	 *
	 * @param assistants the assistants
	 */
	public TemplateAssistProcessor(final ITemplateContentAssistant[] assistants) {
		this.assistants = assistants;
	}

	public ITemplateContentAssistant[] getAssistants() {
		return this.assistants;
	}

	public void setAssistants(final ITemplateContentAssistant[] assistants) {
		this.assistants = assistants;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeCompletionProposals(org.eclipse.jface.text.ITextViewer, int)
	 */
	@Override
	public ICompletionProposal[] computeCompletionProposals(final ITextViewer viewer, final int documentOffset) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		final IDocument document = viewer.getDocument();

		try {
			final int pos = documentOffset == document.getLength() ? documentOffset - 1 : documentOffset;
			final IRegion region = document.getLineInformationOfOffset(pos);

			int startPosition = -1;
			ITemplateContentAssistant currentAssistant = null;
			for (int i = 0; i < this.assistants.length; ++i) {
				final int start = this.assistants[i].getStartPosition(document, region, documentOffset);
				if (start >= startPosition) {
					startPosition = start;
					currentAssistant = this.assistants[i];
				}
			}

			if (currentAssistant == null) {
				return new ICompletionProposal[0];
			}

			final int length = documentOffset - startPosition;
			/*System.out.println("region.getOffset()-" + region.getOffset());
			System.out.println("length-" + length);
			System.out.println("documentOffset-" + documentOffset);
			System.out.println("document.getLength()-" + document.getLength());
			System.out.println("startPosition-" + startPosition);*/
			if (currentAssistant instanceof FCTagContentAssist) {
				final String textBeforeContent = document.get(region.getOffset(), documentOffset - region.getOffset() - length);
				//System.out.println(textBeforeContent);
				final String textAfterContent = getContentUptoNewLine(document.get(documentOffset,  document.getLength() - documentOffset));
				//System.out.println(textAfterContent);
				final String element = document.get(startPosition, length);
				//fc tags should be there only in new line..not between any other code/text
				//3rd Dec '14 -- however, new code to show fc tag attributes by space is added, so including condition for space.
				if ((!isEmpty(textBeforeContent) || !isEmpty(textAfterContent)) && !element.equals(SPACE)) {
					System.out.println("im inside this...will exit");
					return new ICompletionProposal[0];
				}
			}

			final String startLineStr = document.get(region.getOffset(), documentOffset - region.getOffset());
			proposals = currentAssistant.getCompletionProposals(document, startPosition, length, startLineStr.substring(0, startLineStr.length() - length));
		} catch (final Exception e) {/* ignore */
			System.out.println(e);
		}

		return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	private String getContentUptoNewLine(final String textUpToEndOfDoc) {
		final StringBuilder charUptoEOL = new StringBuilder(EMPTY_STR);
		for (final char ch : textUpToEndOfDoc.toCharArray()) {
			if (ch == NEWLINE.charAt(0)) {
				break;
			}
			charUptoEOL.append(ch);

		}
		return charUptoEOL.toString();
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getCompletionProposalAutoActivationCharacters()
	 */
	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		int length = 0;
		for (int i = 0; i < this.assistants.length; ++i) {
			length += this.assistants[i].getActivationCharacters().length;
		}

		int index = 0;
		final char[] activationCharacters = new char[length];
		for (int i = 0; i < this.assistants.length; ++i) {
			final char[] chars = this.assistants[i].getActivationCharacters();
			for (int j = 0; j < chars.length; ++j) {
				activationCharacters[index++] = chars[j];
			}
		}

		return activationCharacters;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#computeContextInformation(org.eclipse.jface.text.ITextViewer, int)
	 */
	@Override
	public IContextInformation[] computeContextInformation(final ITextViewer viewer, final int offset) {
		return null;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationAutoActivationCharacters()
	 */
	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getContextInformationValidator()
	 */
	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.IContentAssistProcessor#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return null;
	}
}

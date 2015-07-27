package org.fastcode.templates.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class FCTagAssistProcessor implements IContentAssistProcessor {
	private ITemplateContentAssistant[]	assistants;

	public FCTagAssistProcessor() {
		//super();
	}

	/**
	 * Instantiates a new template assist processor.
	 *
	 * @param assistants the assistants
	 */
	public FCTagAssistProcessor(final ITemplateContentAssistant[] assistants) {
		this.assistants = assistants;
	}

	public ITemplateContentAssistant[] getAssistants() {
		return this.assistants;
	}

	public void setAssistants(final ITemplateContentAssistant[] assistants) {
		this.assistants = assistants;
	}


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
			final String startLineStr = document.get(region.getOffset(), documentOffset - region.getOffset());
			proposals = currentAssistant.getCompletionProposals(document, startPosition, length, startLineStr.substring(0, startLineStr.length() - length));
		} catch (final Exception e) {/* ignore */
			System.out.println(e.getMessage());
		}

		return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

	@Override
	public IContextInformation[] computeContextInformation(final ITextViewer arg0, final int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		int length = 0;
		for (int i = 0; this.assistants != null && i < this.assistants.length; ++i) {
			length += this.assistants[i].getActivationCharacters().length;
		}

		int index = 0;
		final char[] activationCharacters = new char[length];
		for (int i = 0; this.assistants != null && i < this.assistants.length; ++i) {
			final char[] chars = this.assistants[i].getActivationCharacters();
			for (int j = 0; j < chars.length; ++j) {
				activationCharacters[index++] = chars[j];
			}
		}

		return activationCharacters;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}

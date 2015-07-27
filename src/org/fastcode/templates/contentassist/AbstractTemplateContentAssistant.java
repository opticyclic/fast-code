package org.fastcode.templates.contentassist;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * Abstract base class for template content assistants.
 */
public abstract class AbstractTemplateContentAssistant implements ITemplateContentAssistant {

	private final String[]	startSequences;
	private final char[]	activationCharacters;

	/**
	 * Instantiates a new abstract template content assistant.
	 *
	 * @param startSequences the start sequences
	 * @param activationCharacters the activation characters
	 */
	public AbstractTemplateContentAssistant(final String[] startSequences, final char[] activationCharacters) {
		this.startSequences = startSequences;
		this.activationCharacters = activationCharacters;
	}

	@Override
	public abstract List<ICompletionProposal> getCompletionProposals(IDocument document, int offset, int length, String  spaceToPad);

	@Override
	public char[] getActivationCharacters() {
		return this.activationCharacters;
	}

	@Override
	public int getStartPosition(final IDocument document, final IRegion region, final int offset) throws BadLocationException {
		if (offset <= region.getOffset() || offset > region.getOffset() + region.getLength()) {
			return -1;
		}

		int startIndex = -1;
		int endIndex = -1;

		final String line = document.get(region.getOffset(), offset - region.getOffset());
		//System.out.println(line.length());
		for (int i = 0; i < this.startSequences.length; ++i) {
			final int index = line.lastIndexOf(this.startSequences[i]);
			if (index < 0) {
				continue;
			}

			if (startIndex < 0) {
				startIndex = index;
				endIndex = startIndex + this.startSequences[i].length();
				continue;
			}

			// find the leftmost ending start sequence.
			// in case of equality get the one, that starts first
			final int currentEndIndex = index + this.startSequences[i].length();
			if (currentEndIndex == endIndex && index < startIndex || currentEndIndex > endIndex) {
				startIndex = index;
				endIndex = startIndex + this.startSequences[i].length();
			}
		}

		return startIndex >= 0 ? region.getOffset() + startIndex : -1;
	}
}

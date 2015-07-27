package org.fastcode.templates.contentassist;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * Interface for template content assistants.
 */
public interface ITemplateContentAssistant {

	/**
	 * Gets the start position.
	 *
	 * @param document the document
	 * @param region the region
	 * @param offset the offset
	 *
	 * @return the start position
	 *
	 * @throws BadLocationException the bad location exception
	 */
	public int getStartPosition(IDocument document, IRegion region, int offset) throws BadLocationException;

	/**
	 * Gets the activation characters.
	 *
	 * @return the activation characters
	 */
	public char[] getActivationCharacters();

	/**
	 * Gets the completion proposals.
	 *
	 * @param document the document
	 * @param offset the offset
	 * @param length the length
	 * @param spaceToPad
	 *
	 * @return list of completion proposals
	 */
	public List<ICompletionProposal> getCompletionProposals(IDocument document, int offset, int length, String spaceToPad);
}

package org.fastcode.templates.contentassist;

import static org.fastcode.common.FastCodeConstants.RIGHT_CURL;
import static org.fastcode.common.FastCodeConstants.RIGHT_CURL_CHAR;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN_CHAR;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

/**
 * Template proposal class.
 */
public class TemplateProposal implements ICompletionProposal {

	private final String	element;
	private final String	displayString;
	private final int		offset;
	private int				length;
	private final Point		selection;

	/**
	 * Creates a template element proposal.
	 *
	 * @param displayString the display string
	 * @param element the element
	 * @param length the length to replace
	 * @param offset the offset to replace
	 * @param selection the selection
	 */
	public TemplateProposal(final String element, final String displayString, final int offset, final int length, final Point selection) {
		this.element = element;
		this.displayString = displayString;
		this.offset = offset;
		this.length = length;
		this.selection = selection;
	}

	/*
	 * @see ICompletionProposal#apply(IDocument)
	 */
	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#apply(org.eclipse.jface.text.IDocument)
	 */
	@Override
	public void apply(final IDocument document) {

		try {
			final int idx = this.offset + this.length;
			if (idx < document.getLength()
					&& (this.element.endsWith(RIGHT_CURL) && document.getChar(idx) == RIGHT_CURL_CHAR || this.element.endsWith(RIGHT_PAREN)
							&& document.getChar(idx) == RIGHT_PAREN_CHAR)) {
				++this.length;
			}

			document.replace(this.offset, this.length, this.element);
		} catch (final BadLocationException e) {/* ignore */
		}
	}

	/*
	 * @see ICompletionProposal#getDisplayString()
	 */
	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getDisplayString()
	 */
	@Override
	public String getDisplayString() {
		return this.displayString;
	}

	/*
	 * @see ICompletionProposal#getSelection(IDocument)
	 */
	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getSelection(org.eclipse.jface.text.IDocument)
	 */
	@Override
	public Point getSelection(final IDocument document) {
		return this.selection;
	}

	/*
	 * @see ICompletionProposal#getAdditionalProposalInfo()
	 */
	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getAdditionalProposalInfo()
	 */
	@Override
	public String getAdditionalProposalInfo() {
		return null;
	}

	/*
	 * @see ICompletionProposal#getImage()
	 */
	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getImage()
	 */
	@Override
	public Image getImage() {
		return null;
	}

	/*
	 * @see ICompletionProposal#getContextInformation()
	 */
	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposal#getContextInformation()
	 */
	@Override
	public IContextInformation getContextInformation() {
		return null;
	}
}

package org.fastcode.templates.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.fastcode.templates.rules.TagDetector;

/**
 * Content assistant for annotation tags.
 */
public class TagContentAssistant extends AbstractTemplateContentAssistant {
	private static TagDetector	tagDetector	= new TagDetector();

	/**
	 * Instantiates a new annotation tag content assistant.
	 */
	public TagContentAssistant() {
		super(new String[] { "{@", "@" }, new char[] { '@' });
	}

	/*
	 * @see org.fastcode.templates.contentassist.AbstractTemplateContentAssistant#getCompletionProposals(org.eclipse.jface.text.IDocument, int, int)
	 */
	@Override
	public List<ICompletionProposal> getCompletionProposals(final IDocument document, final int offset, final int length, final String spaceToPad) {
		try {
			final String element = getElement(document, offset, length);
			if (element != null) {
				return TagManager.getCompletionProposals(element, offset, length);
			}
		} catch (final Exception e) {/* ignore */
		}

		return new ArrayList<ICompletionProposal>();
	}

	private String getElement(final IDocument document, final int offset, final int length) throws BadLocationException {
		if (length <= 0) {
			return null;
		}

		final String element = document.get(offset, length);
		if (element.startsWith("{@") && isValidElement(element.substring(1)) || element.startsWith("@") && isValidElement(element)) {
			return element;
		}
		return null;
	}

	private boolean isValidElement(final String element) {
		final char[] chars = element.toCharArray();
		if (chars.length == 0 || !tagDetector.isWordStart(chars[0])) {
			return false;
		}

		for (int i = 1; i < chars.length; ++i) {
			if (!tagDetector.isWordPart(chars[i])) {
				return false;
			}
		}

		return true;
	}
}

package org.fastcode.templates.velocity.contentassist;

import static org.fastcode.common.FastCodeConstants.DOUBLE_SLASH_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_QUOTE_STR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.SPACE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.fastcode.common.FastCodeConstants.FIRST_TEMPLATE;
import org.fastcode.common.FastCodeConstants.SECOND_TEMPLATE;
import org.fastcode.common.FastCodeConstants.TARGET;
import org.fastcode.templates.contentassist.AbstractTemplateContentAssistant;
import org.fastcode.templates.velocity.rules.DirectiveDetector;
import static org.fastcode.common.FastCodeConstants.FC_TAG_START;

/**
 * Content assistant for Velocity directives.
 */
public class DirectiveContentAssistant extends AbstractTemplateContentAssistant {
	private final DirectiveDetector	directiveDetector	= new DirectiveDetector();
	private SECOND_TEMPLATE			secondTemplateItem	= null;
	private FIRST_TEMPLATE			firstTemplateItem	= null;
	String							templatePrefix		= EMPTY_STR;

	/**
	 * The Constructor.
	 */
	public DirectiveContentAssistant(final FIRST_TEMPLATE firstTemplate, final SECOND_TEMPLATE secondTemplate, final String templatePrefix) {
		super(new String[] { "#", "=\"#" }, new char[] { '#' });
		this.secondTemplateItem = secondTemplate;
		this.firstTemplateItem = firstTemplate;
		this.templatePrefix = templatePrefix;
	}

	/**
	 * The Constructor.
	 * @param second_TEMPLATE
	 * @param first_TEMPLATE
	 */
	public DirectiveContentAssistant(final String templatePrefix) {
		super(new String[] { "#", "=\"#" }, new char[] { '#' });
		this.templatePrefix = templatePrefix;
	}

	/*
	 * @see org.fastcode.templates.contentassist.AbstractTemplateContentAssistant#getCompletionProposals(org.eclipse.jface.text.IDocument, int, int)
	 */
	@Override
	public List<ICompletionProposal> getCompletionProposals(final IDocument document, final int offset, final int length,
			final String spaceToPad) {
		try {
			final String element = getElement(document, offset, length);
			if (element != null) {
				return FastCodeDirectiveVelocityUtil.getCompletionProposals(element, offset, length, this.firstTemplateItem, this.secondTemplateItem,
						this.templatePrefix);
			}
		} catch (final Exception e) {/* ignore */
			System.out.println(e.getMessage());
		}

		return new ArrayList<ICompletionProposal>();
	}

	private String getElement(final IDocument document, final int offset, final int length) throws BadLocationException {
		if (length <= 0 || offset > 0 && document.getChar(offset - 1) == DOUBLE_SLASH_CHAR) { // escaped directive
			return null;
		}

		final String element = document.get(offset, length);
		//if (element.startsWith("#") || element.startsWith("=\"#") && isTargetElement(document, offset, length) && isValidElement(element)) {
		if (element.startsWith("#") && isValidElement(element) || element.startsWith("=\"#") && isTargetAttribute(document, offset, length)) {
			return element;
		}
		return null;
	}

	private boolean isTargetAttribute(final IDocument document, final int offset, final int length) throws BadLocationException {
	final IRegion region = document.getLineInformationOfOffset(offset);
		/*System.out.println(region.toString());
		System.out.println(length);
		System.out.println(offset);*/
		final int pos = getAttributPos(document, region, offset);

		final String textBeforeContent = document.get(region.getOffset(), pos - region.getOffset());
		if (textBeforeContent.contains(FC_TAG_START)) {
			final String attibute = document.get(pos, offset - pos).trim();
			if (attibute.equalsIgnoreCase(TARGET.target.getValue()) || attibute.equalsIgnoreCase(TARGET.clas.getValue())
					|| attibute.equalsIgnoreCase(TARGET.file.getValue()) || attibute.equalsIgnoreCase(TARGET.packag.getValue())
					|| attibute.equalsIgnoreCase(TARGET.folder.getValue())) {
				return true;
			}
		}

		return false;
	}

	private int getAttributPos(final IDocument document, final IRegion region, final int offset) throws BadLocationException {
		int attriStartPos = 0;
		for (int i = offset; i > region.getOffset(); i--) {
			if (document.get(i, 1).equals(SPACE)) {
				attriStartPos = i;
				break;
			}
		}
		return attriStartPos;
	}

	private boolean isValidElement(final String element) {
		final char[] chars = element.toCharArray();
		if (chars.length == 0 || !this.directiveDetector.isWordStart(chars[0])) {
			return false;
		}

		for (int i = 1; i < chars.length; ++i) {
			if (!this.directiveDetector.isWordPart(chars[i])) {
				return false;
			}
		}

		return true;
	}
}


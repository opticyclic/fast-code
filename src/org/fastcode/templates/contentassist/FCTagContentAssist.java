package org.fastcode.templates.contentassist;

import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.EQUAL;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.SPACE_CHAR;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.fastcode.templates.velocity.contentassist.FastCodeKeywordsManager;
import org.fastcode.templates.velocity.rules.FastCodeKeywordDetector;
import org.fastcode.util.FastCodeUtil;

public class FCTagContentAssist extends AbstractTemplateContentAssistant {

	private static FastCodeKeywordDetector	tagDetector	= new FastCodeKeywordDetector();

	/**
	 * Instantiates a new fckeyword content assistant.
	 */

	public FCTagContentAssist() {
		super(new String[] { "<f", "</f", SPACE }, new char[] { 'f', SPACE_CHAR }); //(start sequence[], activation character[])
	}

	/* (non-Javadoc)
	 * @see org.fastcode.templates.contentassist.AbstractTemplateContentAssistant#getCompletionProposals(org.eclipse.jface.text.IDocument, int, int)
	 */
	@Override
	public List<ICompletionProposal> getCompletionProposals(final IDocument document, final int offset, final int length,
			final String spaceToPad) {
		try {
			final String element = getElement(document, offset, length);
			if (element != null) {
				final String partition = FastCodeUtil.getPartition(document, offset);
				String fcTagType = null;
				List<String> proposalsToSkip = null;
				if (element.equals(SPACE)) {
					fcTagType = getFCTagTypeType(document, offset, length);
					proposalsToSkip = getProposalsToSkip(document, offset, length);
				}
				return FastCodeKeywordsManager.getCompletionProposals(partition, element, offset, length, spaceToPad, fcTagType,
						proposalsToSkip);
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
		if (element.startsWith("<f") && isValidElement(element) || element.startsWith("</f") && isValidElement(element)
				|| element.startsWith(SPACE) && isWithinFCTag(document, offset) /*&& !isWithinQuotes(document, offset)*/
				&& isValidElement(element)) {
			return element;
		}
		return null;
	}

	private boolean isWithinFCTag(final IDocument document, final int offset) {
		try {
			//System.out.println(document.getLength());

			for (int i = offset; i > 3; i--) {
				//System.out.println(document.get(i - 4, 4));
				if (document.get(i - 4, 4).equals("<fc:") || document.get(i - 4, 4).equals("/fc:")) {
					/*for (int j = i; j < offset; j++) {
						if (document.get(j, 1).equals(")")) {
							return false;
						}
					}*/
					return true;

				}

			}

		} catch (final BadLocationException ex) {
			ex.printStackTrace();
		}

		return false;
	}

	private boolean isValidElement(final String element) {
		final char[] chars = element.toCharArray();

		if (chars.length != 0 || tagDetector.isWordStart(chars[1])) {
			return true;
		}
		if (chars.length == 3) {
			if (tagDetector.isWordStart(chars[2])) {
				return true;
			}

		}

		for (int i = 1; i < chars.length; ++i) {
			if (!tagDetector.isWordPart(chars[i])) {
				return false;
			}
		}
		return false;

	}

	private List<String> getProposalsToSkip(final IDocument document, final int offset, final int length) throws BadLocationException {
		if (length <= 0) {
			return null;
		}
		boolean colonFound = false;
		int attriPos = 0;
		final List<String> attriList = new ArrayList<String>();
		String attriName = EMPTY_STR;
		boolean equalFound = false;
		final boolean spaceFound = false;
		final boolean firstSpaceFound = false;
		for (int i = offset; i > 0; i--) {
			if (document.get(i, 1).equals(COLON)) {
				colonFound = true;
				break;
			}
			if (document.get(i, 1).equals(SPACE)) {
				attriPos = i;
			}
		}

		for (int i = attriPos + 1; i < offset; i++) {
			if (document.get(i, 1).equals(SPACE)) {
				equalFound = false;
			}

			if (equalFound) {
				continue;
			}

			if (document.get(i, 1).equals(EQUAL)) {
				attriList.add(attriName.trim());
				attriName = EMPTY_STR;
				equalFound = true;
				continue;
			}

			attriName = attriName + document.get(i, 1);
		}

		return attriList;
	}

	private String getFCTagTypeType(final IDocument document, final int offset, final int length) throws BadLocationException {
		if (length <= 0) {
			return null;
		}
		String paramType = EMPTY_STR;
		final boolean leftParanFound = false;
		boolean colonFond = false;
		boolean spaceBeforeParamFound = false;
		int spacePos = 0;

		for (int i = offset; i > 0; i--) {
			if (document.get(i, 1).equals(SPACE)) {
				spacePos = i;
				spaceBeforeParamFound = true;
				paramType = EMPTY_STR;
			}

			if (spaceBeforeParamFound) {
				if (document.get(i, 1).equals(COLON)) {
					colonFond = true;
					return new StringBuilder(paramType).reverse().toString().trim();
				}
				paramType = paramType + document.get(i, 1);

			}
		}
		return null;
		/*String paramType = EMPTY_STR;
		for (int i = offset; i > 0; i--) {
			if (document.get(i, 1).equals(COLON) || document.get(i, 1).equals(SPACE)) {
				for (int j = i + 1; j <= offset; j++) {
					if (document.get(j, 1).equals("(") || document.get(j, 1).equals(SPACE)) {
						return paramType;
					} else {
						paramType = paramType + document.get(j, 1);
					}
				}

			}
		}
		return document.get(0, offset); //return null;
		*/}

}

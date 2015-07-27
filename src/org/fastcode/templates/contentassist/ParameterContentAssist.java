package org.fastcode.templates.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.impl.xb.xmlschema.SpaceAttribute.Space;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.fastcode.templates.rules.ColonDetector;

import static org.fastcode.common.FastCodeConstants.DOUBLE_QUOTES;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.SPACE_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.EQUAL;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.templates.util.ContentAssistUtil.isWithinQuotes;

public class ParameterContentAssist extends AbstractTemplateContentAssistant {
	private static ColonDetector	colonDetector	= new ColonDetector();

	/**
	 * Instantiates a new annotation tag content assistant.
	 */
	public ParameterContentAssist() {
		super(new String[] { ":", "(", SPACE }, new char[] { ':', '(', SPACE_CHAR });

	}

	/*
	 * @see org.fastcode.templates.contentassist.AbstractTemplateContentAssistant#getCompletionProposals(org.eclipse.jface.text.IDocument, int, int)
	 */
	@Override
	public List<ICompletionProposal> getCompletionProposals(final IDocument document, final int offset, final int length, final String spaceToPad) {
		try {
			final String element = getElement(document, offset, length);
			final String paramType = getParamType(document, offset, length);
			final List<String> proposalsToSkip = getProposalsToSkip(document, offset, length);
			if (element != null) {
				return ParameterTypeManager.getCompletionProposals(element, offset, length, paramType, proposalsToSkip);
			}
		} catch (final Exception e) {/* ignore */
		}

		return new ArrayList<ICompletionProposal>();
	}

	private List<String> getProposalsToSkip(final IDocument document, final int offset, final int length) throws BadLocationException {
		if (length <= 0) {
			return null;
		}
		boolean leftParanFound = false;
		int parnPos = 0;
		final List<String> attriList = new ArrayList<String>();
		String attriName = EMPTY_STR;
		boolean equalFound = false;
		final boolean spaceFound = false;
		for (int i = offset; i > 0; i--) {
			if (document.get(i, 1).equals(LEFT_PAREN)) {
				leftParanFound = true;
				parnPos = i;
				break;
			}
		}

		for (int i = parnPos + 1; i < offset; i++) {
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

	private String getParamType(final IDocument document, final int offset, final int length) throws BadLocationException {
		if (length <= 0) {
			return null;
		}
		String paramType = EMPTY_STR;
		boolean leftParanFound = false;
		boolean colonFond = false;
		boolean spaceBeforeParamFound = false;

		for (int i = offset; i > 0; i--) {
			if (document.get(i, 1).equals(LEFT_PAREN)) {
				leftParanFound = true;
				continue;
			}

			if (leftParanFound) {
				if (document.get(i, 1).equals(COLON)) {
					colonFond = true;
				}

				if (document.get(i, 1).equals(SPACE)) {
					if (!isEmpty(paramType)) {
						spaceBeforeParamFound = true;
					}
				}

				if (colonFond || spaceBeforeParamFound) {
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

	private String getElement(final IDocument document, final int offset, final int length) throws BadLocationException {
		if (length <= 0) {
			return null;
		}

		final String element = document.get(offset, length);
		/*if (element.startsWith(":") || element.startsWith("(") && getParamType(document,  offset, length)!=null || element.startsWith(SPACE) && isWithinBraces(document, offset)
				&& isValidElement(element)) {
			return element;
		}*/
		if (element.startsWith(":") || element.startsWith("(") || element.startsWith(SPACE) && !isWithinQuotes(document, offset)
				&& isWithinBraces(document, offset) && isValidElement(element)) {
			return element;
		}
		return null;
	}

	private boolean isWithinBraces(final IDocument document, final int offset) {

		try {
			for (int i = offset; i > 0; i--) {
				System.out.println("doc length" + document.getLength());

				System.out.println(document.get(i, 1));
				if (document.get(i - 4, 4).equals(EQUAL + DOUBLE_QUOTES + DOUBLE_QUOTES)) {
					System.out.println("finally found the codition");
					System.out.println(document.get(i - 4, 4));
				}
				if (document.get(i - 4, 4).equals(EQUAL + DOUBLE_QUOTES + DOUBLE_QUOTES) || document.get(i, 1).equals("(")) {
					for (int j = i; j < offset; j++) {
						if (document.get(j, 1).equals(")")) {
							return false;
						}
					}
					return true;

				}
			}

		} catch (final BadLocationException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		return false;
	}



	private boolean isValidElement(final String element) {
		final char[] chars = element.toCharArray();

		if (chars.length == 0 || !colonDetector.isWordStart(chars[0])) {
			return false;
		}

		for (int i = 1; i < chars.length; ++i) {
			if (!colonDetector.isWordPart(chars[i])) {
				return false;
			}
		}

		return true;
	}

}

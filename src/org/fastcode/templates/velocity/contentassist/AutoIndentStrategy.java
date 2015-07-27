package org.fastcode.templates.velocity.contentassist;

import static org.fastcode.common.FastCodeConstants.ASTERISK_CHAR;
import static org.fastcode.common.FastCodeConstants.ASTERISK_WITH_SINGLE_SPACE;
import static org.fastcode.common.FastCodeConstants.ASTERISK_WITH_SPACE;
import static org.fastcode.common.FastCodeConstants.DOUBLE_QUOTES_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH_CHAR;
import static org.fastcode.common.FastCodeConstants.HASH_CHAR;
import static org.fastcode.common.FastCodeConstants.LEFT_BRACKET;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL_CHAR;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN_CHAR;
import static org.fastcode.common.FastCodeConstants.MULTILINE_COMMENT_END_TAG;
import static org.fastcode.common.FastCodeConstants.RIGHT_BRACKET;
import static org.fastcode.common.FastCodeConstants.RIGHT_CURL_CHAR;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN_CHAR;
import static org.fastcode.common.FastCodeConstants.SINGLE_QUOTE;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.TAB_CHAR;
import static org.fastcode.common.FastCodeConstants.VELOCITY_COMMENT_END_TAG;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.fastcode.templates.rules.FastCodeTemplatePartitions;
import org.fastcode.util.FastCodeUtil;

/**
 * Strategy for auto indention in Velocity templates.
 */
public class AutoIndentStrategy implements IAutoEditStrategy {

	/*
	 * @see
	 * org.eclipse.jface.text.IAutoEditStrategy#customizeDocumentCommand(org
	 * .eclipse.jface.text.IDocument, org.eclipse.jface.text.DocumentCommand)
	 */

	@Override
	public void customizeDocumentCommand(final IDocument doc, final DocumentCommand cmd) {
		if (doc.getLength() == 0 || cmd.offset == -1 || cmd.length > 0 || cmd.text == null) {
			return;
		}

		final String[] lineDelimiters = doc.getLegalLineDelimiters();
		final int index = TextUtilities.endsWith(lineDelimiters, cmd.text);
		if (index > -1 && lineDelimiters[index].equals(cmd.text)) {
			indentAfterNewLine(doc, cmd);
		}
	}

	private void indentAfterNewLine(final IDocument doc, final DocumentCommand cmd) {
		try {
			final String partition = FastCodeUtil.getPartition(doc, cmd.offset);

			// find start of line
			final IRegion info = doc.getLineInformation(doc.getLineOfOffset(cmd.offset));
			final int lineOffset = info.getOffset();
			final int lineLength = info.getLength();

			// line empty or comment closed -> do nothing
			final String line = doc.get(lineOffset, lineLength).trim();
			if (line.length() == 0 || line.endsWith(MULTILINE_COMMENT_END_TAG)) {
				return;
			}

			if (line.length() == 0 || line.endsWith(VELOCITY_COMMENT_END_TAG)) {
				return;
			}

			// find white spaces
			final int eow = findEndOfWhiteSpace(doc, lineOffset, cmd.offset);

			String indention = EMPTY_STR;
			final StringBuilder buf = new StringBuilder(cmd.text);
			if (eow > lineOffset) {
				indention = doc.get(lineOffset, eow - lineOffset);
				buf.append(indention);
			}

			// inside brackets or quotes -> no prefix
			if (isInsideBrackets(doc, lineOffset, cmd.offset)) {
				return;
			}

			// check type of partition
			if (partition.equals(FastCodeTemplatePartitions.MULTI_LINE_COMMENT)) {
				// Velocity multi line comment partition
				handleMultiLineCommentPartition(doc, eow, buf);
			} else if (partition.equals(FastCodeTemplatePartitions.SINGLE_LINE_COMMENT)) {
				// Velocity single line comment partition
				handleSingleLineCommentPartition(doc, indention, buf);
			} else {
				// default partition ( + Velocity directives)
				handleDefaultPartition(doc, cmd, info, eow, indention, buf);
			}

			cmd.text = buf.toString();
		} catch (final BadLocationException excp) {/* stop work */
		}
	}

	private void handleMultiLineCommentPartition(final IDocument doc, final int eow, final StringBuilder buf) throws BadLocationException {
		if (doc.getChar(eow) == HASH_CHAR) {
			buf.append(ASTERISK_WITH_SPACE); // first line after start of
												// comment
		} else {
			buf.append(ASTERISK_WITH_SINGLE_SPACE); // indention already set
		}
	}

	private void handleSingleLineCommentPartition(final IDocument doc, final String indention, final StringBuilder buf)
			throws BadLocationException {
		if (indention.length() == 0) {
			buf.append(ASTERISK_WITH_SPACE); // first line after comment
		} else {
			buf.append(ASTERISK_WITH_SINGLE_SPACE); // indention already set
		}
	}

	private void handleDefaultPartition(final IDocument doc, final DocumentCommand cmd, final IRegion lineInfo, final int eow,
			final String indention, final StringBuilder buf) throws BadLocationException {
		final char ch = doc.getChar(eow);
		switch (ch) {

		}
		if (ch == FORWARD_SLASH_CHAR && eow + 1 < doc.getLength()
				&& (doc.getChar(eow + 1) == ASTERISK_CHAR || doc.getChar(eow + 1) == FORWARD_SLASH_CHAR)) {

			addNewLine(doc, cmd, eow, buf, indention, EMPTY_STR/*ASTERISK_WITH_SPACE*/, MULTILINE_COMMENT_END_TAG);

		} else if (ch == HASH_CHAR && eow + 1 < doc.getLength()
				&& (doc.getChar(eow + 1) == ASTERISK_CHAR || doc.getChar(eow + 1) == HASH_CHAR)) {

			addNewLine(doc, cmd, eow, buf, indention, EMPTY_STR, VELOCITY_COMMENT_END_TAG);

		} else if (ch == ASTERISK_CHAR) {
			buf.append(ASTERISK_WITH_SINGLE_SPACE);
		}

	}

	private void addNewLine(final IDocument doc, final DocumentCommand cmd, final int eow, final StringBuilder buf, final String indention,
			final String appendString, final String commentEndTag) {
		// start of Java multi/single line comment
		try {
			if (doc.getChar(eow + 1) == ASTERISK_CHAR) {
				// multi line comment
				buf.append(appendString);
				cmd.caretOffset = cmd.offset + buf.length();
				cmd.shiftsCaret = false;
				buf.append(doc.getLegalLineDelimiters()[0] + indention + SPACE + commentEndTag);
			} else {
				// single line comment
				buf.append(EMPTY_STR);
			}
		} catch (final BadLocationException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

	}

	private int findEndOfWhiteSpace(final IDocument document, int offset, final int end) throws BadLocationException {
		while (offset < end) {
			final char ch = document.getChar(offset);
			if (ch != EMPTY_CHAR && ch != TAB_CHAR) {
				return offset;
			}
			++offset;
		}
		return end;
	}

	private boolean isInsideBrackets(final IDocument doc, final int start, final int end) throws BadLocationException {
		int cntLeftParenthesis = 0;
		int cntRightParenthesis = 0;
		int cntLeftBrace = 0;
		int cntRightBrace = 0;
		int cntLeftBracket = 0;
		int cntRightBracket = 0;
		int cntSingleQuote = 0;
		int cntDoubleQuote = 0;

		for (int i = start; i < end; ++i) {
			final char ch = doc.getChar(i);

			if (ch == LEFT_PAREN_CHAR) {
				++cntLeftParenthesis;
			} else if (ch == LEFT_CURL_CHAR) {
				++cntLeftBrace;
			} else if (ch == LEFT_BRACKET) {
				++cntLeftBracket;
			} else if (ch == RIGHT_PAREN_CHAR) {
				++cntRightParenthesis;
			} else if (ch == RIGHT_CURL_CHAR) {
				++cntRightBrace;
			} else if (ch == RIGHT_BRACKET) {
				++cntRightBracket;
			} else if (ch == DOUBLE_QUOTES_CHAR) {
				++cntDoubleQuote;
			} else if (ch == SINGLE_QUOTE) {
				++cntSingleQuote;
			}
		}

		return cntLeftParenthesis > cntRightParenthesis || cntLeftBrace > cntRightBrace || cntLeftBracket > cntRightBracket
				|| cntDoubleQuote % 2 == 1 || cntSingleQuote % 2 == 1;
	}
}

package org.fastcode.templates.contentassist;

import static org.fastcode.common.FastCodeConstants.DOUBLE_QUOTES_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.LEFT_BRACKET;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL_CHAR;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN_CHAR;
import static org.fastcode.common.FastCodeConstants.RIGHT_BRACKET;
import static org.fastcode.common.FastCodeConstants.RIGHT_CURL_CHAR;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN_CHAR;
import static org.fastcode.common.FastCodeConstants.SINGLE_QUOTES_CHAR;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * Automatic closing of brackets and quotes.
 */
public class AutoCloseStrategy implements IAutoEditStrategy {

	/*
	 * @see org.eclipse.jface.text.IAutoEditStrategy#customizeDocumentCommand(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.DocumentCommand)
	 */

	@Override
	public void customizeDocumentCommand(final IDocument document, final DocumentCommand command) {
		if (document.getLength() == 0 || command.offset == -1 || command.length > 0 || command.text == null || command.text.length() != 1) {
			return;
		}

		final char ch = command.text.charAt(0);
		if (ch == LEFT_PAREN_CHAR) {
			setClosingChar(command, RIGHT_PAREN_CHAR);
		} else if (ch == LEFT_CURL_CHAR) {
			setClosingChar(command, RIGHT_CURL_CHAR);
		} else if (ch == LEFT_BRACKET) {
			setClosingChar(command, RIGHT_BRACKET);
		} else if (ch == RIGHT_PAREN_CHAR) {
			checkClosingChar(document, command, LEFT_PAREN_CHAR, RIGHT_PAREN_CHAR);
		} else if (ch == RIGHT_CURL_CHAR) {
			checkClosingChar(document, command, LEFT_CURL_CHAR, RIGHT_CURL_CHAR);
		} else if (ch == RIGHT_BRACKET) {
			checkClosingChar(document, command, LEFT_BRACKET, RIGHT_BRACKET);
		} else if (ch == DOUBLE_QUOTES_CHAR) {
			checkQuoteChar(document, command, DOUBLE_QUOTES_CHAR);
		} else if (ch == SINGLE_QUOTES_CHAR) {
			checkQuoteChar(document, command, SINGLE_QUOTES_CHAR);
		}
	}

	private void setClosingChar(final DocumentCommand command, final char closingChar) {
		command.text += closingChar;
		command.caretOffset = command.offset + 1;
		command.shiftsCaret = false;
	}

	private void checkQuoteChar(final IDocument document, final DocumentCommand command, final char quoteChar) {
		try {
			if (command.offset == document.getLength()) { // cursor at eof
				if (countQuotes(document, command, quoteChar) % 2 == 0) {
					// set closing quote
					setClosingChar(command, quoteChar);
				}
				return;
			}

			final char ch = document.getChar(command.offset);
			if (ch == quoteChar) {
				// next char is already quote
				command.caretOffset = command.offset + 1; // already there -> shift caret
				command.shiftsCaret = false;
				command.text = EMPTY_STR;
			} else if (countQuotes(document, command, quoteChar) % 2 == 0) {
				// set closing quote
				setClosingChar(command, quoteChar);
			}
		} catch (final BadLocationException e) {/* stop work */
		}
	}

	private void checkClosingChar(final IDocument document, final DocumentCommand command, final char openingChar, final char closingChar) {
		if (command.offset == document.getLength()) {
			return; // cursor at eof -> nothing to do
		}

		try {
			final char ch = document.getChar(command.offset);
			if (ch == closingChar) {
				// next char is already closing char
				// -> check, if it's already set by auto closing
				if (!needsClosingChar(document, command, openingChar, closingChar)) {
					command.caretOffset = command.offset + 1; // already there -> shift caret
					command.shiftsCaret = false;
					command.text = EMPTY_STR;
				}
			}
		} catch (final BadLocationException e) {/* stop work */
		}
	}

	private boolean needsClosingChar(final IDocument document, final DocumentCommand command, final char openingChar, final char closingChar)
			throws BadLocationException {

		int countOpening = 0;
		int countClosing = 0;

		final IRegion info = document.getLineInformationOfOffset(command.offset);

		final int start = info.getOffset();
		final int end = command.offset;

		for (int i = start; i <= end; ++i) {
			final char ch = document.getChar(i);
			if (ch == openingChar) {
				++countOpening;
			} else if (ch == closingChar) {
				++countClosing;
			}
		}

		return countOpening > countClosing;
	}

	private int countQuotes(final IDocument document, final DocumentCommand command, final char quoteChar) throws BadLocationException {
		int count = 0;

		final IRegion info = document.getLineInformationOfOffset(command.offset);

		final int start = info.getOffset();
		final int end = command.offset;

		for (int i = start; i < end; ++i) {
			final char ch = document.getChar(i);
			if (ch == quoteChar) {
				++count;
			}
		}

		return count;
	}
}

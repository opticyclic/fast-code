package org.fastcode.templates.rules;

import static org.fastcode.common.FastCodeConstants.COLON_CHAR;
import static org.fastcode.common.FastCodeConstants.SPACE_CHAR;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.fastcode.FastCodeColorManager;
import org.fastcode.templates.contentassist.ParameterContentAssist;

/**
 * Rule for valid Velocity Template variable .
 */
public class AdditionalParameterRule extends SingleLineRule {

	private final String	startSequence;

	/**
	 *
	 * @param start
	 *            the start
	 * @param token
	 *            the token
	 */
	public AdditionalParameterRule(final String start, final IToken token) {
		this(start, token, false);
	}

	/**
	 * Instantiates a new reference rule.
	 *
	 * @param startSequence
	 *            the start sequence
	 * @param token
	 *            the token
	 * @param ruleForContentAssist
	 *            true allows incomplete references for ContentAssist
	 */
	public AdditionalParameterRule(final String startSequence, final IToken token, final boolean ruleForContentAssist) {
		super(startSequence, null, token);
		this.startSequence = startSequence;
	}

	public AdditionalParameterRule(final String startSequence, final String endSequence, final IToken token) {
		super(startSequence, endSequence, token);
		this.startSequence = startSequence;

	}

	/*
	 * @see
	 * org.eclipse.jface.text.rules.PatternRule#endSequenceDetected(org.eclipse
	 * .jface.text.rules.ICharacterScanner)
	 */
	@Override
	protected boolean endSequenceDetected(final ICharacterScanner scanner) {

		setToDefault();
		Boolean endOfTag = false;

		while (true) {
			final int i = scanner.read();
			final char ch = (char) i;

			endOfTag = checkEndOfToken(ch, scanner);

			if (endOfTag != null) {
				break;
			}

		}

		return endOfTag.booleanValue();
	}

	private boolean checkEndOfToken(final char ch, final ICharacterScanner scanner) {
		if (ch == SPACE_CHAR || ch == '(') {

			return true;
		}
		return false;
	}

	/**
	 *
	 * @param ch
	 * @return
	 */
	/*
	 * private Boolean checkForEndOfToken(final char ch, final ICharacterScanner
	 * scanner) {
	 *
	 * final boolean withinString = this.lastQuoteChar != null &&
	 * (this.lastQuoteChar == SINGLE_QUOTE || this.lastQuoteChar ==
	 * QUOTE_STR_CHAR);
	 *
	 * switch (ch) {
	 *
	 * case RIGHT_CURL_CHAR: if (withinString) { return null; }
	 * this.numBraceStack--; if (this.numBraceStack < 0) { return false; }
	 * return this.numBraceStack == 0 ? isCompleteToken() : null; case
	 * LEFT_CURL_CHAR: if (!withinString) { this.numBraceStack++; } return null;
	 * case SINGLE_QUOTE: case QUOTE_STR_CHAR: if (this.lastQuoteChar == null) {
	 * this.lastQuoteChar = ch; } if (this.lastQuoteChar == ch) { if
	 * (this.lastChar == BACK_SLASH.charAt(0)) { return null; }
	 * this.lastQuoteChar = null; // string is closed. } return null; case
	 * SPACE_CHAR:
	 *
	 * case TAB_CHAR: if (withinString) { return null; } scanner.unread();
	 * return isCompleteToken(); case LEFT_PAREN_CHAR: if (!withinString) {
	 * this.numParenthesisStack++; } return null; case RIGHT_PAREN_CHAR: if
	 * (withinString) { return null; } this.numParenthesisStack--; if
	 * (this.numParenthesisStack < 0) { scanner.unread(); } return
	 * this.ruleForContentAssist || this.numParenthesisStack < 0 ?
	 * isCompleteToken() : null; case COMMA_CHAR: return withinString ? null :
	 * false; case (char) ICharacterScanner.EOF: case LINEFEED_CHAR: case
	 * NEW_LINE_CHAR: scanner.unread(); return isCompleteToken(); case DOT_CHAR:
	 * if (withinString) { return null; } else if (this.ruleForContentAssist) {
	 * return isCompleteToken(); } else { return null; } default: if
	 * (this.lastChar == null && ch != '{' && !Character.isLetterOrDigit(ch)) {
	 *
	 * return false; } break; } return null; }
	 */

	@Override
	protected IToken doEvaluate(final ICharacterScanner scanner, final boolean resume) {

		if (resume) {

			if (endSequenceDetected(scanner)) {
				return this.fToken;
			}

		} else {
			final int c = scanner.read();

			if (c == this.fStartSequence[0]) {
				if (sequenceDetected(scanner, this.fStartSequence, false)) {
					if (endSequenceDetected(scanner)) {
						if (isParameterType(scanner)) {
							scanner.unread();
							return this.fToken;
						}

						return FastCodeColorManager.getToken("NORMAL");
					} else {

						// if (isNumber(scanner)) {
						// return FastCodeColorManager.getToken("NORMAL");
						// } else {
						scanner.unread();
						new ParameterContentAssist();
						return FastCodeColorManager.getToken("NORMAL");
						// }

					}
				}
			}
		}

		scanner.unread();
		return Token.UNDEFINED;

	}

	private boolean isNumber(final ICharacterScanner scanner) {
		final char n = (char) scanner.read();
		return Character.isDigit(n);
	}

	private boolean isParameterType(final ICharacterScanner scanner) {

		unread(scanner, this.startSequence.length() + 1);

		if ((char) scanner.read() != COLON_CHAR) {
			read(scanner, this.startSequence.length());

			return false;
		} else {

			read(scanner, this.startSequence.length());
			return true;
		}

	}

	private void unread(final ICharacterScanner scanner, int length) {

		while (length >= 0) {

			scanner.unread();
			length--;

		}

	}

	private void read(final ICharacterScanner scanner, int length) {

		while (length >= 0) {

			scanner.read();
			length--;

		}
	}

	/**
	 *
	 */
	private void setToDefault() {
		// this.variableBuilder.setLength(0);
		// this.lastChar = this.lastQuoteChar = null;
		// this.numBraceStack = this.numParenthesisStack = 0;
	}

	/**
	 * Checks for complete variable
	 *
	 * @return true, if variable is valid
	 */
	// private boolean isCompleteToken() {
	// final boolean ret = this.ruleForContentAssist ? this.numBraceStack >= 0 :
	// this.lastQuoteChar == null && this.numBraceStack == 0
	// && this.numParenthesisStack <= 0;
	// return ret;
	// }
}

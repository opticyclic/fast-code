package org.fastcode.templates.velocity.rules;

import static org.fastcode.common.FastCodeConstants.BACK_SLASH;
import static org.fastcode.common.FastCodeConstants.COMMA_CHAR;
import static org.fastcode.common.FastCodeConstants.DOT_CHAR;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL_CHAR;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN_CHAR;
import static org.fastcode.common.FastCodeConstants.LINEFEED_CHAR;
import static org.fastcode.common.FastCodeConstants.NEW_LINE_CHAR;
import static org.fastcode.common.FastCodeConstants.QUOTE_STR_CHAR;
import static org.fastcode.common.FastCodeConstants.RIGHT_CURL_CHAR;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN_CHAR;
import static org.fastcode.common.FastCodeConstants.SINGLE_QUOTE;
import static org.fastcode.common.FastCodeConstants.SPACE_CHAR;
import static org.fastcode.common.FastCodeConstants.TAB_CHAR;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;

/**
 * Rule for valid Velocity Template variable .
 */
public class TemplateVariableRule extends SingleLineRule {

	private int					numParenthesisStack		= 0;
	private int					numBraceStack			= 0;

	private Character			lastQuoteChar;
	private Character			lastChar;

	private boolean				ruleForContentAssist	= false;

	private final StringBuilder	variableBuilder			= new StringBuilder();

	/**
	 *
	 * @param start the start
	 * @param token the token
	 */
	public TemplateVariableRule(final String start, final IToken token) {
		this(start, token, false);
	}

	/**
	 * Instantiates a new reference rule.
	 *
	 * @param startSequence the start sequence
	 * @param token the token
	 * @param ruleForContentAssist true allows incomplete references for ContentAssist
	 */
	public TemplateVariableRule(final String startSequence, final IToken token, final boolean ruleForContentAssist) {
		super(startSequence, null, token);
		this.ruleForContentAssist = ruleForContentAssist;
	}

	/*
	 * @see org.eclipse.jface.text.rules.PatternRule#endSequenceDetected(org.eclipse.jface.text.rules.ICharacterScanner)
	 */
	@Override
	protected boolean endSequenceDetected(final ICharacterScanner scanner) {

		setToDefault();
		Boolean endOfTag = false;
		while (true) {
			final int i = scanner.read();
			final char ch = (char) i;

			endOfTag = checkForEndOfToken(ch, scanner);
			if (endOfTag != null) {
				this.variableBuilder.append(ch);
				break;
			}

			this.variableBuilder.append(ch);
			this.lastChar = ch;
		}
		if (!endOfTag) {
			//unread(scanner,this.variableBuilder.length());
		}

		//System.out.println(this.ruleForContentAssist + " - EOT " + this.variableBuilder.toString() + " " + endOfTag);

		return endOfTag.booleanValue();
	}

	/**
	 *
	 * @param ch
	 * @return
	 */
	private Boolean checkForEndOfToken(final char ch, final ICharacterScanner scanner) {

		final boolean withinString = this.lastQuoteChar != null
				&& (this.lastQuoteChar == SINGLE_QUOTE || this.lastQuoteChar == QUOTE_STR_CHAR);

		switch (ch) {

		case RIGHT_CURL_CHAR:
			if (withinString) {
				return null;
			}
			this.numBraceStack--;
			if (this.numBraceStack < 0) {
				return false;
			}
			return this.numBraceStack == 0 ? isCompleteToken() : null;
		case LEFT_CURL_CHAR:
			if (!withinString) {
				this.numBraceStack++;
			}
			return null;
		case SINGLE_QUOTE:
		case QUOTE_STR_CHAR:
			if (this.lastQuoteChar == null) {
				this.lastQuoteChar = ch;
			}
			if (this.lastQuoteChar == ch) {
				if (this.lastChar == BACK_SLASH.charAt(0)) {
					return null;
				}
				this.lastQuoteChar = null; // string is closed.
			}
			return null;
		case SPACE_CHAR:

		case TAB_CHAR:
			if (withinString) {
				return null;
			}
			scanner.unread();
			return isCompleteToken();
		case LEFT_PAREN_CHAR:
			if (!withinString) {
				this.numParenthesisStack++;
			}
			return null;
		case RIGHT_PAREN_CHAR:
			if (withinString) {
				return null;
			}
			this.numParenthesisStack--;
			if (this.numParenthesisStack < 0) {
				scanner.unread();
			}
			return this.ruleForContentAssist || this.numParenthesisStack < 0 ? isCompleteToken() : null;
		case COMMA_CHAR:
			return !withinString ? null : true;
		case (char) ICharacterScanner.EOF:
		case LINEFEED_CHAR:
		case NEW_LINE_CHAR:
			scanner.unread();
			return isCompleteToken();
		case DOT_CHAR:
			if (withinString) {
				return null;
			} else if (this.ruleForContentAssist) {
				return isCompleteToken();
			} else {
				return null;
			}
		default:
			if (this.lastChar == null && ch != '{' && !Character.isLetterOrDigit(ch)) {

				return false;
			}
			break;
		}
		return null;
	}

	/**
	 *
	 */
	private void setToDefault() {
		this.variableBuilder.setLength(0);
		this.lastChar = this.lastQuoteChar = null;
		this.numBraceStack = this.numParenthesisStack = 0;
	}

	/**
	 * Checks for complete variable
	 *
	 * @return true, if variable is valid
	 */
	private boolean isCompleteToken() {
		final boolean ret = this.ruleForContentAssist ? this.numBraceStack >= 0 : this.lastQuoteChar == null && this.numBraceStack == 0
				&& this.numParenthesisStack <= 0;
		return ret;
	}
}

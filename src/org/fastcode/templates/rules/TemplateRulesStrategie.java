package org.fastcode.templates.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.fastcode.FastCodeColorManager;
import org.fastcode.templates.util.ResettableScanner;

/**
 * Provides general template scanning rules.
 */
public class TemplateRulesStrategie implements IRulesStrategy {

	/*
	 * @see org.fastcode.templates.rules.IRulesStrategy#createRules()
	 */

	@Override
	public List<IRule> createRules() {
		final List<IRule> rules = new ArrayList<IRule>();

		// Add rule for parsed strings
		rules.add(new StringRule("\"", "\"", FastCodeColorManager.getToken("STRING")));

		// Add rule for unparsed strings
		rules.add(new StringRule("\'", "\'", FastCodeColorManager.getToken("STRING")));

		// Add rule for escaped references
		rules.add(new StringRule("\\", "$", FastCodeColorManager.getToken("ESCAPES")));

		// Add rule for escaped directives
		rules.add(new StringRule("\\", "#", FastCodeColorManager.getToken("ESCAPES")));

		// Add rule for hmtl markups
		rules.add(new MarkupRule(FastCodeColorManager.getToken("MARKUP")));

		// Add rules for operators
		rules.add(new OperatorRule(FastCodeColorManager.getToken("OPERATOR")));

		rules.add(new ColonRule(FastCodeColorManager.getToken("COLON")));

		// Add rules for numbers
		//rules.add(new NumberRule(FastCodeColorManager.getToken("NUMBER")));

		// Add rules for tags
		rules.add(new WordRule(new TagDetector(), FastCodeColorManager.getToken("TAG")));

		return rules;
	}

	// ----------------------------------------------------
	// inner classes
	// ----------------------------------------------------

	/**
	 * Rule for string detection.
	 */
	public static class StringRule extends SingleLineRule {
		private final String			endSequence;
		private final ResettableScanner	rScanner	= new ResettableScanner();

		/**
		 * Instantiates a new string rule.
		 *
		 * @param startSequence
		 *            the start sequence
		 * @param endSequence
		 *            the end sequence
		 * @param token
		 *            the success token
		 */
		public StringRule(final String startSequence, final String endSequence, final IToken token) {
			super(startSequence, endSequence, token);
			this.endSequence = endSequence;
		}

		/*
		 * @see
		 * org.eclipse.jface.text.rules.PatternRule#evaluate(org.eclipse.jface
		 * .text.rules.ICharacterScanner)
		 */

		@Override
		public IToken evaluate(final ICharacterScanner scanner) {
			this.rScanner.setScanner(scanner);
			IToken result = super.evaluate(this.rScanner);
			if (result != getSuccessToken() || !validate((FastCodeTemplateCodeScanner) scanner)) {
				result = Token.UNDEFINED;
				this.rScanner.reset();
			}
			return result;
		}

		/**
		 * Validate existing end sequence.
		 *
		 * @return true, if validate
		 */
		public boolean validate(final FastCodeTemplateCodeScanner scanner) {
			try {
				final String token = scanner.getDocument().get(scanner.getTokenOffset(), scanner.getTokenLength());
				return token.endsWith(this.endSequence);
			} catch (final BadLocationException e) {/* ignore */
			}

			return false;
		}
	}

	/**
	 * Rule for html markup detection.
	 */
	private static class MarkupRule extends SingleLineRule {
		private final ResettableScanner	rScanner	= new ResettableScanner();

		public MarkupRule(final IToken token) {
			super("<", ">", token);
		}

		/*
		 * @see PatternRule#evaluate(ICharacterScanner)
		 */

		@Override
		public IToken evaluate(final ICharacterScanner scanner) {
			this.rScanner.setScanner(scanner);
			IToken result = super.evaluate(this.rScanner);
			if (result != getSuccessToken() || !validate((FastCodeTemplateCodeScanner) scanner)) {
				result = Token.UNDEFINED;
				this.rScanner.reset();
			}
			return result;
		}

		private boolean validate(final FastCodeTemplateCodeScanner scanner) {
			try {
				final String token = scanner.getDocument().get(scanner.getTokenOffset(), scanner.getTokenLength()) + ".";

				int offset = 0;
				char character = token.charAt(++offset);

				if (character == '/') {
					character = token.charAt(++offset);
				}

				while (Character.isWhitespace(character)) {
					character = token.charAt(++offset);
				}

				while (Character.isLetter(character)) {
					character = token.charAt(++offset);
				}

				while (Character.isWhitespace(character)) {
					character = token.charAt(++offset);
				}

				if (offset >= 2 && token.charAt(offset) == this.fEndSequence[0]) {
					return true;
				}

			} catch (final BadLocationException exception) {/* ignore */
			}

			return false;
		}
	}

	/**
	 * Rule for operator detection.
	 */
	public static class OperatorRule implements IRule {
		// omit comment chars '/' and '*'
		private static final char[]	OPERATORS	= { '(', ')', '[', ']', '=', '>', '<', '+', '-', '!', ',', '|', '&', '%' };

		private final IToken		token;

		/**
		 * Creates a new operator rule.
		 *
		 * @param token
		 *            Token to use for this rule
		 */
		public OperatorRule(final IToken token) {
			this.token = token;
		}

		/*
		 * @see
		 * org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text
		 * .rules.ICharacterScanner)
		 */

		@Override
		public IToken evaluate(final ICharacterScanner scanner) {
			int character = scanner.read();
			if (isOperator((char) character)) {
				do {
					character = scanner.read();
				} while (isOperator((char) character));
				scanner.unread();
				return this.token;
			} else {
				scanner.unread();
				return Token.UNDEFINED;
			}
		}

		/**
		 * Checks, if this character is an operator character.
		 *
		 * @param character
		 *            Character to determine whether it is an operator character
		 * @return <code>true</code>, if the character is an operator
		 */
		private boolean isOperator(final char character) {
			for (int index = 0; index < OPERATORS.length; index++) {
				if (OPERATORS[index] == character) {
					return true;
				}
			}
			return false;
		}
	}

	private static class ColonRule implements IRule {
		// omit comment chars '/' and '*'
		private static final char	colon	= ':';

		private final IToken		token;

		/**
		 * Creates a new operator rule.
		 *
		 * @param token
		 *            Token to use for this rule
		 */
		public ColonRule(final IToken token) {
			this.token = token;
		}

		/* @see
		 * org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text
		 * .rules.ICharacterScanner)
		*/

		@Override
		public IToken evaluate(final ICharacterScanner scanner) {
			final int character = scanner.read();

			if ((char) character == colon) {
				//				do {
				//					character = scanner.read();
				//				} while ((char) character == colon);
				//				scanner.unread();
				return this.token;
			} else {
				scanner.unread();
				return Token.UNDEFINED;
			}
		}

		/*  Checks, if this character is an operator character.

		  @param character
		             Character to determine whether it is an operator character
		  @return <code>true</code>, if the character is an operator
		  */

	}

	/**
	 * Rule for number detection.
	 */
	private static class NumberRule implements IRule {
		private final IToken	token;

		/**
		 * Creates a new number rule.
		 *
		 * @param token
		 *            Token to use for this rule
		 */
		public NumberRule(final IToken token) {
			this.token = token;
		}

		/*
		 * @see
		 * org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text
		 * .rules.ICharacterScanner)
		 */

		@Override
		public IToken evaluate(final ICharacterScanner scanner) {
			int character = scanner.read();
			if (Character.isDigit((char) character)) {
				do {
					character = scanner.read();
				} while (Character.isDigit((char) character));
				scanner.unread();
				return this.token;
			} else {
				scanner.unread();
				return Token.UNDEFINED;

			}
		}
	}
}

package org.fastcode.templates.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.Token;
import org.fastcode.FastCodeColorManager;
import org.fastcode.templates.contentassist.ParameterTypeManager;
import org.fastcode.templates.rules.TemplateRulesStrategie.OperatorRule;
import org.fastcode.templates.rules.TemplateRulesStrategie.StringRule;

/**
 * Strategie for Velocity reference rules.
 */

public class ParameterTypeRulesStrategie implements IRulesStrategy {

	/*
	 * @see org.fastcode.templates.rules.IRulesStrategy#createRules()
	 */

	@Override
	public List<IRule> createRules() {
		final List<IRule> rules = new ArrayList<IRule>();

		final IToken token = FastCodeColorManager.getToken("DEFAULT");
		final String[] directives = ParameterTypeManager.getDirectives();
		for (int i = directives.length - 1; i >= 0; i--) {
			rules.add(new AdditionalParameterRule(directives[i], token));

		}
		rules.add(new NumberRule(FastCodeColorManager.getToken("NORMAL")));
		rules.add(new ColonRule(FastCodeColorManager.getToken("COLON")));
		rules.add(new StringRule("\"", "\"", FastCodeColorManager.getToken("STRING")));
		rules.add(new StringRule("\'", "\'", FastCodeColorManager.getToken("STRING")));
		rules.add(new OperatorRule(FastCodeColorManager.getToken("PARAM_OPERATORS")));

		return rules;
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

}

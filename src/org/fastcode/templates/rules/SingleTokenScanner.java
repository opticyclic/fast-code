package org.fastcode.templates.rules;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.RuleBasedScanner;

/**
 * Dummy scanner without rules. Deliveres only one token.
 */
public class SingleTokenScanner extends RuleBasedScanner {

	/**
	 * Instantiates a new single token scanner.
	 *
	 * @param token the default token
	 */
	public SingleTokenScanner(final IToken token) {
		super();
		setDefaultReturnToken(token);
	}
}

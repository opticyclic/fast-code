package org.fastcode.templates.velocity.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WordRule;
import org.fastcode.FastCodeColorManager;
import org.fastcode.templates.rules.IRulesStrategy;
import org.fastcode.templates.velocity.contentassist.FastCodeDirectiveVelocityUtil;

/**
 * Strategie for Velocity directive rules.
 */
public class DirectiveRulesStrategie implements IRulesStrategy {

	/*
	 * @see org.fastcode.templates.rules.IRulesStrategy#createRules()
	 */
	@Override
	public List<IRule> createRules() {
		final List<IRule> rules = new ArrayList<IRule>();

		// Add word rule for directives
		final IToken token = FastCodeColorManager.getToken("DIRECTIVE");
		final WordRule wordRule = new WordRule(new DirectiveDetector(), FastCodeColorManager.getToken("DEFAULT"));
		final String[] directives = FastCodeDirectiveVelocityUtil.getDirectives();
		for (int i = directives.length - 1; i >= 0; i--) {
			wordRule.addWord(directives[i], token);

		}
		rules.add(wordRule);

		return rules;
	}
}

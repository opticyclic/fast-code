package org.fastcode.templates.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WordRule;
import org.fastcode.FastCodeColorManager;
import org.fastcode.templates.velocity.contentassist.FastCodeKeywordsManager;
import org.fastcode.templates.velocity.rules.FastCodeKeywordDetector;

public class FastCodeKeywordRulesStrategie implements IRulesStrategy {

	/*
	 * @see org.fastcode.templates.rules.IRulesStrategy#createRules()
	 */
	@Override
	public List<IRule> createRules() {
		final List<IRule> rules = new ArrayList<IRule>();

		// Add word rule for directives
		final IToken token = FastCodeColorManager.getToken("FCKEYS");
		final WordRule wordRule = new WordRule(new FastCodeKeywordDetector(), FastCodeColorManager.getToken("NORMAL"));
		final String[] directives = FastCodeKeywordsManager.getDirectives();
		for (int i = directives.length - 1; i >= 0; i--) {
			wordRule.addWord(directives[i], token);
		}
		rules.add(wordRule);

		return rules;
	}
}

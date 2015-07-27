package org.fastcode.templates.rules;

import static org.fastcode.util.StringUtil.JAVA_RESERVED_WORDS;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.WordRule;
import org.fastcode.FastCodeColorManager;
import org.fastcode.templates.velocity.contentassist.FastCodeKeywordsManager;
import org.fastcode.templates.velocity.rules.FastCodeJavaKeywordDetector;
import org.fastcode.templates.velocity.rules.FastCodeKeywordDetector;

public class FastCodeJavaKeywordRulesStrategie implements IRulesStrategy {


	/*
	 * @see org.fastcode.templates.rules.IRulesStrategy#createRules()
	 */
	@Override
	public List<IRule> createRules() {
		final List<IRule> rules = new ArrayList<IRule>();

		// Add word rule for java reserved words
		final IToken token = FastCodeColorManager.getToken("JAVA_KEYWORD");
		final WordRule wordRule = new WordRule(new FastCodeJavaKeywordDetector(), FastCodeColorManager.getToken("NORMAL"));
		for (int i = JAVA_RESERVED_WORDS.length - 1; i >= 0; i--) {
			wordRule.addWord(JAVA_RESERVED_WORDS[i], token);

		}
		rules.add(wordRule);

		return rules;
	}
}

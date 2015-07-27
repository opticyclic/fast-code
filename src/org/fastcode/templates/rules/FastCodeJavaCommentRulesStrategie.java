package org.fastcode.templates.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.WordRule;
import org.fastcode.FastCodeColorManager;
import org.fastcode.templates.velocity.rules.FastCodeJavaCommentDetector;

public class FastCodeJavaCommentRulesStrategie implements IRulesStrategy {

	/*
	 * @see org.fastcode.templates.rules.IRulesStrategy#createRules()
	 */
	@Override
	public List<IRule> createRules() {
		final List<IRule> rules = new ArrayList<IRule>();
		final IToken token = FastCodeColorManager.getToken("COMMENT");

		final WordRule wordRule = new WordRule(new FastCodeJavaCommentDetector(), FastCodeColorManager.getToken("NORMAL"));
			/*wordRule.addWord("/*", token);
			wordRule.addWord("//", token);*/
		//rules.add(wordRule);

		rules.add(new EndOfLineRule("//", token));
		rules.add(new MultiLineRule("/*", "*/", token, (char) 0, true));



		return rules;
	}

}

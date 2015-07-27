package org.fastcode.templates.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.fastcode.FastCodeColorManager;
import static org.fastcode.util.FastCodeUtil.getEmptyArrayForNull;

/**
 * Scanner for template code.
 */
public class FastCodeTemplateCodeScanner extends RuleBasedScanner {
	private final IRulesStrategy[]	ruleStrategies;

	/**
	 * Instantiates a new template code scanner.
	 *
	 * @param ruleStrategies the rule strategies
	 */
	public FastCodeTemplateCodeScanner(final IRulesStrategy[] ruleStrategies) {
		super();
		this.ruleStrategies = ruleStrategies;
		initializeRules();
	}

	/**
	 * Gets the document.
	 *
	 * @return the document
	 */
	public IDocument getDocument() {
		return this.fDocument;
	}

	/**
	 * Creates the rules by using the given rule strategies.
	 *
	 * @return list of rules
	 */
	private List<IRule> createRules() {
		final List<IRule> rules = new ArrayList<IRule>();

		// Add generic whitespace rule
		rules.add(new WhitespaceRule(new WhitespaceDetector()));

		// add rules from strategies
		for (int i = 0; i < getEmptyArrayForNull(this.ruleStrategies).length; ++i) {
			rules.addAll(this.ruleStrategies[i].createRules());
		}

		return rules;
	}

	/**
	 * Initialize the rules.
	 */
	private void initializeRules() {
		final List<IRule> rules = createRules();
		setRules(rules.toArray(new IRule[rules.size()]));

		setDefaultReturnToken(FastCodeColorManager.getToken("NORMAL"));
	}
}

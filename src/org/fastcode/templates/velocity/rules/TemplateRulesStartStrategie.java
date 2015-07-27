package org.fastcode.templates.velocity.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IRule;
import org.fastcode.FastCodeColorManager;
import org.fastcode.templates.rules.IRulesStrategy;

/**
 * Strategie for Velocity reference rules, where references
 * could be incomplete.
 */
public class TemplateRulesStartStrategie implements IRulesStrategy {

	/*
	 * @see org.fastcode.templates.rules.IRulesStrategy#createRules()
	 */
	@Override
	public List<IRule> createRules() {
		final List<IRule> rules = new ArrayList<IRule>();

		// Add rules for incomplete references
		rules.add(new TemplateVariableRule("$!", FastCodeColorManager.getToken("VARIABLE"), true));
		rules.add(new TemplateVariableRule("$", FastCodeColorManager.getToken("VARIABLE"), true));

		return rules;
	}
}

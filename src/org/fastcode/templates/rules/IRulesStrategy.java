package org.fastcode.templates.rules;

import java.util.List;

import org.eclipse.jface.text.rules.IRule;

/**
 * Interface for rule strategies.
 */
public interface IRulesStrategy {

	/**
	 * Create rules.
	 *
	 * @return list of rules
	 */
	public List<IRule> createRules();
}

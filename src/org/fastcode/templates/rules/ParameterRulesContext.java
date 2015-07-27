package org.fastcode.templates.rules;

import java.util.Map;

import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextHover;
import org.fastcode.templates.contentassist.AutoCloseStrategy;
import org.fastcode.templates.contentassist.ITemplateContentAssistant;
import org.fastcode.templates.contentassist.ParameterContentAssist;
/*import org.fastcode.templates.velocity.contentassist.DirectiveAndCommentStartStrategy;*/
import org.fastcode.templates.velocity.contentassist.TemplateTextHover;

public class ParameterRulesContext {

	public IRulesStrategy[] getParameterRuleStrategies() {
		final IRulesStrategy[] ruleStrategies = { new ParameterTypeRulesStrategie() };
		return ruleStrategies;
	}

	public ITextHover getTemplateTextHover(final Map<String, String> properties) {
		return new TemplateTextHover(null);
	}

	public ITemplateContentAssistant[] getTemplateContentAssistants(final Map<String, String> properties) {
		final ITemplateContentAssistant[] assistants = { new ParameterContentAssist() };
		return assistants;
	}

	public IAutoEditStrategy[] getTemplateAutoEditStrategies() {
		final IAutoEditStrategy[] autoEditStrategies = { new DefaultIndentLineAutoEditStrategy(), /*new DirectiveAndCommentStartStrategy(),*/
				new AutoCloseStrategy() };
		return autoEditStrategies;
	}

}

package org.fastcode.templates.rules;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;

import java.util.Map;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextHover;
import org.fastcode.common.FastCodeConstants.FIRST_TEMPLATE;
import org.fastcode.common.FastCodeConstants.SECOND_TEMPLATE;
import org.fastcode.templates.contentassist.AutoCloseStrategy;
import org.fastcode.templates.contentassist.FCTagContentAssist;
import org.fastcode.templates.contentassist.ITemplateContentAssistant;
import org.fastcode.templates.velocity.contentassist.AutoIndentStrategy;
import org.fastcode.templates.velocity.contentassist.DBTemplatePreferenceContentAssistant;
import org.fastcode.templates.velocity.contentassist.DirectiveContentAssistant;
import org.fastcode.templates.velocity.contentassist.FileTemplateContentAssist;
import org.fastcode.templates.velocity.contentassist.TemplateContentAssistant;
import org.fastcode.templates.velocity.contentassist.TemplateTextHover;
import org.fastcode.templates.velocity.rules.DirectiveRulesStrategie;
import org.fastcode.templates.velocity.rules.TemplateVariableRulesStrategie;

public class FastCodeRulesContext {

	public IRulesStrategy[] getTemplateRuleStrategies() {
		final IRulesStrategy[] ruleStrategies = { new TemplateVariableRulesStrategie(), new DirectiveRulesStrategie(),
				new TemplateRulesStrategie(), new FastCodeKeywordRulesStrategie() };
		return ruleStrategies;
	}

	public IRulesStrategy[] getFCTagRuleStrategies() {
		final IRulesStrategy[] ruleStrategies = { new TemplateVariableRulesStrategie(), new DirectiveRulesStrategie(),
				new TemplateRulesStrategie(), new FastCodeKeywordRulesStrategie() , new FastCodeJavaKeywordRulesStrategie() , new FastCodeJavaCommentRulesStrategie()};
		return ruleStrategies;
	}

	public ITextHover getTemplateTextHover(final Map<String, String> properties) {
		return new TemplateTextHover(null);
	}

	public ITemplateContentAssistant[] getDBTemplateContentAssistants(final Map<String, String> properties) {
		final ITemplateContentAssistant[] assistants = { new DBTemplatePreferenceContentAssistant(null, false),
				new DirectiveContentAssistant(P_DATABASE_TEMPLATE_PREFIX), new FCTagContentAssist(),

		};
		return assistants;
	}

	public ITemplateContentAssistant[] getFileTemplateContentAssistants(final Map<String, String> properties) {
		final ITemplateContentAssistant[] assistants = { new FileTemplateContentAssist(null, false),
				new DirectiveContentAssistant(EMPTY_STR), new FCTagContentAssist()

		};
		return assistants;
	}

	public IAutoEditStrategy[] getTemplateAutoEditStrategies() {
		final IAutoEditStrategy[] autoEditStrategies = { new AutoIndentStrategy() , /*new DefaultIndentLineAutoEditStrategy(),*/ /*new DirectiveAndCommentStartStrategy(),*/
		new AutoCloseStrategy() };
		return autoEditStrategies;
	}

	public ITemplateContentAssistant[] getFCTagContentAssistants(final Map<FIRST_TEMPLATE, SECOND_TEMPLATE> templateItemsMap) {

		final ITemplateContentAssistant[] assistants = {
				new TemplateContentAssistant(templateItemsMap, false),
				templateItemsMap != null && (SECOND_TEMPLATE) templateItemsMap.values().toArray()[0] != null ? new DirectiveContentAssistant(
						(FIRST_TEMPLATE) templateItemsMap.keySet().toArray()[0], (SECOND_TEMPLATE) templateItemsMap.values().toArray()[0],
						TEMPLATE) : new DirectiveContentAssistant(TEMPLATE)

		};
		return assistants;

	}

	public ITemplateContentAssistant[] getTemplateContentAssistants(final Map<FIRST_TEMPLATE, SECOND_TEMPLATE> templateItemsMap) {

		final ITemplateContentAssistant[] assistants = {
				new TemplateContentAssistant(templateItemsMap, false),
				templateItemsMap != null && (SECOND_TEMPLATE) templateItemsMap.values().toArray()[0] != null ? new DirectiveContentAssistant(
						(FIRST_TEMPLATE) templateItemsMap.keySet().toArray()[0], (SECOND_TEMPLATE) templateItemsMap.values().toArray()[0],
						TEMPLATE) : new DirectiveContentAssistant(TEMPLATE), new FCTagContentAssist()

		};
		return assistants;

	}

	/*public ITemplateContentAssistant[] getDefaultContentAssistants(final Map<String, String> properties) {
		final ITemplateContentAssistant[] assistants = { new DefaultTemplateContentAssistant(properties, false),new DirectiveContentAssistant(), new TagContentAssistant(), new FCTagContentAssist()

		};
		return assistants;
	}*/

}

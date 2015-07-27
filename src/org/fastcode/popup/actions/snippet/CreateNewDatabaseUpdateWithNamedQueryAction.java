package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_UPDATE_NAMED_QUERY;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;

public class CreateNewDatabaseUpdateWithNamedQueryAction extends CreateSqlGenericNamedQueryViewAction {

	public CreateNewDatabaseUpdateWithNamedQueryAction() {
		this.templatePrefix = P_DATABASE_TEMPLATE_PREFIX;
		this.templateType = DATABASE_TEMPLATE_UPDATE_NAMED_QUERY;
	}

	/*@Override
	public String getTemplateTypeForNamedQuery() {
		return P_ADDITIONAL_DATABASE_TEMPLATE_PREFIX + UNDERSCORE + "sql.update.format";
	}*/

	@Override
	protected String[] getFieldTypesForHql() {
		return null;
	}
}

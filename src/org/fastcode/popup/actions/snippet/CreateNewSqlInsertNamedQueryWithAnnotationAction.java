package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_INSERT_WITH_NAMED_QUERY_ANNOTATION;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;

public class CreateNewSqlInsertNamedQueryWithAnnotationAction extends CreateSqlGenericNamedQueryViewAction {

	/**
	 *
	 */
	public CreateNewSqlInsertNamedQueryWithAnnotationAction() {
		this.templatePrefix = P_DATABASE_TEMPLATE_PREFIX;
		this.templateType = DATABASE_TEMPLATE_INSERT_WITH_NAMED_QUERY_ANNOTATION;
	}

	/*@Override
	public String getTemplateTypeForNamedQuery() {
		return P_ADDITIONAL_DATABASE_TEMPLATE_PREFIX + UNDERSCORE + "sql.insert.format";
	}*/

	@Override
	protected String[] getFieldTypesForHql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean requireAnnotation() {
		return true;
	}

}

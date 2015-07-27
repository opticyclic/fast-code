package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_DELETE_WITH_NAMED_QUERY;

public class CreateHqlDeleteWithNamedQueryViewAction extends CreateHqlGenericViewAction {

	public CreateHqlDeleteWithNamedQueryViewAction() {
		this.templatePrefix = TEMPLATE;
		this.templateType = TEMPLATE_TYPE_CREATE_HQL_DELETE_WITH_NAMED_QUERY;
	}

	/*@Override
	public String getTemplateTypeForNamedQuery() {
		return P_ADDITIONAL_TEMPLATE_PREFIX + UNDERSCORE + "hql.delete.format";
	}*/

	@Override
	protected String[] getFieldTypesForHql() {
		return null;
	}

}

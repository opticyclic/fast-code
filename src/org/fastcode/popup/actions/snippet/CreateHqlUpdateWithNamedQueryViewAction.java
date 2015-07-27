package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_UPDATE_WITH_NAMED_QUERY;

public class CreateHqlUpdateWithNamedQueryViewAction extends CreateHqlGenericViewAction {

	/**
	 *
	 */
	public CreateHqlUpdateWithNamedQueryViewAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_HQL_UPDATE_WITH_NAMED_QUERY;
		this.templatePrefix = TEMPLATE;
	}

	/*@Override
	public String getTemplateTypeForNamedQuery() {
		return P_ADDITIONAL_TEMPLATE_PREFIX + UNDERSCORE + "hql.update.format";
	}*/

	@Override
	protected String[] getFieldTypesForHql() {
		// TODO Auto-generated method stub
		return null;
	}

}

package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_SELECT_NAMED_QUERY_WITH_ANNOTATION;

public class CreateHqlSelectNamedQueryWithAnnotationViewAction extends CreateHqlGenericViewAction {

	/**
	 *
	 */
	public CreateHqlSelectNamedQueryWithAnnotationViewAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_HQL_SELECT_NAMED_QUERY_WITH_ANNOTATION;
		this.templatePrefix = TEMPLATE;
	}

	/*@Override
	public String getTemplateTypeForNamedQuery() {
		return P_ADDITIONAL_TEMPLATE_PREFIX + UNDERSCORE + "hql.select.format";
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

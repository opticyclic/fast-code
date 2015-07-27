package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_DELETE_NAMED_QUERY_WITH_ANNOTATION;

public class CreateHqlDeleteNamedQueryWithAnnotationViewAction extends CreateHqlGenericViewAction {

	/**
	 *
	 */
	public CreateHqlDeleteNamedQueryWithAnnotationViewAction() {
		this.templatePrefix = TEMPLATE;
		this.templateType = TEMPLATE_TYPE_CREATE_HQL_DELETE_NAMED_QUERY_WITH_ANNOTATION;
	}

	/*@Override
	public String getTemplateTypeForNamedQuery() {
		return P_ADDITIONAL_TEMPLATE_PREFIX + UNDERSCORE + "hql.delete.format";
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

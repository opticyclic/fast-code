package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_UPDATE_NAMED_QUERY_WITH_ANNOTATION;

public class CreateHqlUpdateNamedQueryWithAnnotationViewAction extends CreateHqlGenericViewAction {

	/**
	 *
	 */
	public CreateHqlUpdateNamedQueryWithAnnotationViewAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_HQL_UPDATE_NAMED_QUERY_WITH_ANNOTATION;
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

	@Override
	protected boolean requireAnnotation() {
		return true;
	}

}

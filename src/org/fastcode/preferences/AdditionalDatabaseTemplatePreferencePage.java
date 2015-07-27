/**
 *
 */
package org.fastcode.preferences;

import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author ayngaran
 *
 */
public class AdditionalDatabaseTemplatePreferencePage extends AbstractTableTemplatePreferencePage implements IWorkbenchPreferencePage {

	/**
	 *
	 */
	public AdditionalDatabaseTemplatePreferencePage() {
		super();
		//this.templatePrefix = P_ADDITIONAL_DATABASE_TEMPLATE_PREFIX;
		setDescription("Additional Database Template preference");
	}

	/*@Override
	protected String getAllTemplatesPreferenceKey() {
		return P_ALL_ADDITIONAL_DATABASE_TEMPLATES;
	}*/

	@Override
	protected boolean isDetailedTemplate() {
		return false;
	}

	@Override
	protected boolean isShowAllowedFileExtension() {
		return false;
	}

}

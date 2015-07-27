/**
 * @author : Gautam

 * Created : 04/30/2010

 */

package org.fastcode.preferences;

import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_SERVICE_POJO_ID;

import org.eclipse.ui.IWorkbenchPreferencePage;

public class CreateNewServicePOJOPreferencePage extends CreateSimilarPreferencePage implements IWorkbenchPreferencePage {

	/**
	 *
	 */
	public CreateNewServicePOJOPreferencePage() {
		this.preferenceId = CREATE_NEW_PREFERENCE_SERVICE_POJO_ID;
		this.setDescription("Fast Code Create New Service Pojo Preference Page");
	}

	/**
	 *
	 */
	@Override
	protected boolean isForValueBeans() {
		return true;
	}

	/**
	 *
	 */
	@Override
	protected boolean isCreateNew() {
		return true;
	}
}
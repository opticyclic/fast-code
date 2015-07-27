/**
 * @author : Gautam

 * Created : 04/29/2010

 */

package org.fastcode.preferences;

import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_DAO_POJO_ID;

import org.eclipse.ui.IWorkbenchPreferencePage;

public class CreateNewDAOPOJOPreferencePage extends CreateSimilarPreferencePage implements IWorkbenchPreferencePage {

	/**
	 *
	 */
	public CreateNewDAOPOJOPreferencePage() {
		this.preferenceId = CREATE_NEW_PREFERENCE_DAO_POJO_ID;
		setDescription("Fast Code Create New DAO Pojo Preference Page");
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
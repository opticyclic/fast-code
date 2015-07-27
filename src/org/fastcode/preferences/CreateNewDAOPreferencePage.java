/**
 *
 */
package org.fastcode.preferences;

import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_DAO_ID;

import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Gautam
 *
 */
public class CreateNewDAOPreferencePage extends CreateSimilarPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Create_newDAO_preference_page
	 */
	public CreateNewDAOPreferencePage() {
		super();
		this.preferenceId = CREATE_NEW_PREFERENCE_DAO_ID;
		this.setDescription("Fast Code Create New DAO Preference Page");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.fastcode.preferences.CreateSimilarPreferencePage#isForValueBeans()
	 */
	@Override
	protected boolean isForValueBeans() {
		return false;
	}

	/**
	 *
	 */
	@Override
	protected boolean isCreateNew() {
		return true;
	}
}

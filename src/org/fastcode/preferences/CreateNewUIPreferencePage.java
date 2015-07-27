/**
 *
 */
package org.fastcode.preferences;

import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_UI_ID;

import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Gautam
 *
 */
public class CreateNewUIPreferencePage extends CreateSimilarPreferencePage implements IWorkbenchPreferencePage {

	/**
	 *
	 */
	public CreateNewUIPreferencePage() {
		super();
		this.preferenceId = CREATE_NEW_PREFERENCE_UI_ID;
		this.setDescription("Fast Code Create New UI Class Preference Page");
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

	@Override
	protected boolean isCreateNew() {
		return true;
	}

}

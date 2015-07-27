/**
 *
 */
package org.fastcode.preferences;

import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_SERVICE_ID;

import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Gautam
 *
 */
public class CreateNewServicePreferencePage extends CreateSimilarPreferencePage implements IWorkbenchPreferencePage {

	/**
	 *
	 */
	public CreateNewServicePreferencePage() {
		super();
		this.preferenceId = CREATE_NEW_PREFERENCE_SERVICE_ID;
		this.setDescription("Fast Code Create New Service Preference Page");
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

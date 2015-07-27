/**
 *
 */
package org.fastcode.popup.actions.createsimilar;

import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_UI_ID;

/**
 * @author Gautam
 *
 */
public class CreateSimilarNewUIViewAction extends CreateSimilarViewAction {

	public CreateSimilarNewUIViewAction() {
		super();
		this.createNew = true;
		this.preferenceId = CREATE_NEW_PREFERENCE_UI_ID;
	}
}

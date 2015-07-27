/**
 *
 */
package org.fastcode.popup.actions.createsimilar;

import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_SERVICE_ID;

/**
 * @author Gautam
 *
 */
public class CreateSimilarNewServiceViewAction extends CreateSimilarViewAction {

	/**
	 *
	 */
	public CreateSimilarNewServiceViewAction() {
		super();
		this.createNew = true;
		this.preferenceId = CREATE_NEW_PREFERENCE_SERVICE_ID;
	}
}

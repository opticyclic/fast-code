/**
 *
 */
package org.fastcode.popup.actions.createsimilar;

import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_DAO_ID;

/**
 * @author Gautam
 *
 */
public class CreateSimilarNewDaoViewAction extends CreateSimilarViewAction {

	/**
	 *
	 */
	public CreateSimilarNewDaoViewAction() {
		super();
		this.createNew = true;
		this.preferenceId = CREATE_NEW_PREFERENCE_DAO_ID;
	}
}

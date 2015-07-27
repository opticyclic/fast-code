/**
 * @author : Gautam

 * Created : 10/05/2010

 */

package org.fastcode.popup.actions.createsimilar;

import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_SERVICE_POJO_ID;

public class CreateSimilarNewServicePojoViewAction extends CreateSimilarViewAction {

	/**
	 *
	 */
	public CreateSimilarNewServicePojoViewAction() {
		super();
		this.createNew = true;
		this.preferenceId = CREATE_NEW_PREFERENCE_SERVICE_POJO_ID;
	}
}
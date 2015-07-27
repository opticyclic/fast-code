/**
 * @author : Gautam

 * Created : 04/27/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_NEW_INSTANCE_JSON;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateNewInstanceJsonViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/**
	 *
	 */
	public CreateNewInstanceJsonViewAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_NEW_INSTANCE_JSON;
		this.templatePrefix = TEMPLATE;
	}
}
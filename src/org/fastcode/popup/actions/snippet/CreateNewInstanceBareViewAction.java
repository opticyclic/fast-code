/**
 * @author : Gautam

 * Created : 04/27/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_NEW_INSTANCE_BARE;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateNewInstanceBareViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/**
	 *
	 */
	public CreateNewInstanceBareViewAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_NEW_INSTANCE_BARE;
		this.templatePrefix = TEMPLATE;
	}
}
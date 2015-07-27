/**
 * @author : Gautam

 * Created : 01/01/2011

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_RESOURCE_BUNDLE;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateNewResourceBundleViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/**
	 *
	 * This is Constructor for CreateNewResourceBundleViewAction
	 */
	public CreateNewResourceBundleViewAction() {
		this.templateType = TEMPLATE_TYPE_RESOURCE_BUNDLE;
		this.templatePrefix = TEMPLATE;
	}

	@Override
	protected boolean doContinueWithNoFields() {
		return false;
	}
}
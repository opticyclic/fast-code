/**
 *
 */
package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_NEW_INSTANCE;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Gautam
 *
 */
public class CreateNewInstanceViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {
	/**
	 *
	 */
	public CreateNewInstanceViewAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_NEW_INSTANCE;
		this.templatePrefix = TEMPLATE;
	}
}

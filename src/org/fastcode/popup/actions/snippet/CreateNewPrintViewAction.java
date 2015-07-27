/**
 *
 */
package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_PRINT_CLASS;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Gautam
 *
 */
public class CreateNewPrintViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/**
	 *
	 */
	public CreateNewPrintViewAction() {
		this.templateType = TEMPLATE_TYPE_PRINT_CLASS;
		this.templatePrefix = TEMPLATE;
	}

	/**
	 *
	 */
	@Override
	protected boolean doContinueWithNoFields() {
		return false;
	}
}

/**
 * @author : Gautam

 * Created : 11/16/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_SIMPLE_CLASS_SNIPPET;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateNewSimpleBeanViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/**
	 *
	 * This is Constructor for CreateNewSimpleBeanViewAction
	 */
	public CreateNewSimpleBeanViewAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_SIMPLE_CLASS_SNIPPET;
		this.templatePrefix = TEMPLATE;
	}

	@Override
	protected boolean isSingleSelection() {
		return true;
	}
}
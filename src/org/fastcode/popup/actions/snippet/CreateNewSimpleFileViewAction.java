/**
 * @author : Gautam

 * Created : 12/25/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_SIMPLE_FILE_SNIPPET;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateNewSimpleFileViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/**
	 *
	 * This is Constructor for CreateNewSimpleFileViewAction
	 */
	public CreateNewSimpleFileViewAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_SIMPLE_FILE_SNIPPET;
		this.templatePrefix = TEMPLATE;
	}
}
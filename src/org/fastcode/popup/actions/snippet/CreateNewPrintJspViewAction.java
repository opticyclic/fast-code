/**
 * @author : Gautam

 * Created : 04/27/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_INSTANCE_OF_CLASS_WEB;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateNewPrintJspViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/**
	 *
	 */
	public CreateNewPrintJspViewAction() {
		this.templateType = TEMPLATE_INSTANCE_OF_CLASS_WEB;
		this.templatePrefix = TEMPLATE;
		//this.description = "Print a variable Jsp";
	}

}
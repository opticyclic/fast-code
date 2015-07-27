/**
 * @author : Gautam

 * Created : 05/14/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_NEW_EQUALS_HASHCODE;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateEqualsHashCodeViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/**
	 *
	 */
	public CreateEqualsHashCodeViewAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_NEW_EQUALS_HASHCODE;
		this.templatePrefix = TEMPLATE;
	}

}
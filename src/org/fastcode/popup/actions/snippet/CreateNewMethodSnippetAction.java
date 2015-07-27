package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_METHOD_SNIPPET;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateNewMethodSnippetAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {
	/**
	 *
	 */
	public CreateNewMethodSnippetAction() {
		this.templateType = TEMPLATE_TYPE_METHOD_SNIPPET;
		this.templatePrefix = TEMPLATE;
	}
}

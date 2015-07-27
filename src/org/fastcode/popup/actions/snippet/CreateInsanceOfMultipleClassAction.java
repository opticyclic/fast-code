package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_NEW_INSTANCE_MULTIPLE_CLASS;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateInsanceOfMultipleClassAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {
	/**
	 *
	 */
	public CreateInsanceOfMultipleClassAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_NEW_INSTANCE_MULTIPLE_CLASS;
		this.templatePrefix = TEMPLATE;
	}

}

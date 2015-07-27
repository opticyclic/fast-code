package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_NEW_HASHCODE_AND_EQUALS_METHODS;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

;

public class CreateNewHashCodeAndEqualsViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	public CreateNewHashCodeAndEqualsViewAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_NEW_HASHCODE_AND_EQUALS_METHODS;
		this.templatePrefix = TEMPLATE;
	}

}

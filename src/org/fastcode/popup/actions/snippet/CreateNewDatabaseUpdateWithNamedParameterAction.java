package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_UPDATE_WITH_NAMED_PARAMETER;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class CreateNewDatabaseUpdateWithNamedParameterAction extends AbstractCreateNewDatabaseSnippetAction implements
		IEditorActionDelegate, IActionDelegate, IWorkbenchWindowActionDelegate {
	/**
	 *
	 */
	public CreateNewDatabaseUpdateWithNamedParameterAction() {
		this.templatePrefix = P_DATABASE_TEMPLATE_PREFIX;
		this.templateType = DATABASE_TEMPLATE_UPDATE_WITH_NAMED_PARAMETER;
	}
}

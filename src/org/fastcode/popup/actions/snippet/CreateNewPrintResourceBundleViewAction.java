/**
 * @author : Gautam

 * Created : 01/01/2011

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_PRINT_RESOURCE_BUNDLE;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.setting.TemplateSettings;

public class CreateNewPrintResourceBundleViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate,
		IActionDelegate, IWorkbenchWindowActionDelegate {

	/**
	 *
	 * This is Constructor for CreateNewPrintResourceBundleViewAction
	 */
	public CreateNewPrintResourceBundleViewAction() {
		this.templateType = TEMPLATE_TYPE_PRINT_RESOURCE_BUNDLE;
		this.templatePrefix = TEMPLATE;
	}

	/**
	 * @param templateSettings
	 * @param templateType
	 *
	 */
	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#isEmptyLineRequired(org.fastcode.setting.TemplateSettings, java.lang.String)
	 */
	@Override
	protected boolean isEmptyLineRequired(final TemplateSettings templateSettings, final String templateType) {
		return false;
	}
}
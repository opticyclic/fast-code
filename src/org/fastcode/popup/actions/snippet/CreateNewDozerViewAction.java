/**
 * @author : Gautam

 * Created : 05/04/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_NEW_DOZER_MAPPING;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.setting.TemplateSettings;

public class CreateNewDozerViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/**
	 *
	 */
	public CreateNewDozerViewAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_NEW_DOZER_MAPPING;
		this.templatePrefix = TEMPLATE;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#doShowEmbeddedFields(org.fastcode.setting.TemplateSettings, java.lang.String)
	 */
	@Override
	protected boolean doShowEmbeddedFields(final TemplateSettings templateSettings, final String templateType) {
		return true;
	}
}
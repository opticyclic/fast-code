/**
 * @author : Gautam

 * Created : 09/02/2010

 */

package org.fastcode.preferences;

import static org.fastcode.preferences.PreferenceConstants.P_ALL_COMMON_TEMPLATES;
import static org.fastcode.preferences.PreferenceConstants.P_COMMON_TEMPLATE_PREFIX;

import org.eclipse.ui.IWorkbenchPreferencePage;

public class CommonTemplatePreferencePage extends AbstractTableTemplatePreferencePage implements IWorkbenchPreferencePage {

	/**
	 *
	 */
	public CommonTemplatePreferencePage() {
		super();
		this.templatePrefix = P_COMMON_TEMPLATE_PREFIX;
		setDescription("Common Template preference");
	}

	@Override
	protected String getAllTemplatesPreferenceKey() {
		return P_ALL_COMMON_TEMPLATES;
	}

	@Override
	protected boolean isDetailedTemplate() {
		return false;
	}

	@Override
	protected boolean isShowAllowedFileExtension() {
		return false;
	}
}
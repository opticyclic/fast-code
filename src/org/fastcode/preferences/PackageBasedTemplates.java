package org.fastcode.preferences;

import static org.fastcode.preferences.PreferenceConstants.P_ALL_TEMPLATES;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;

import org.eclipse.ui.IWorkbenchPreferencePage;

public class PackageBasedTemplates extends AbstractTemplatePreferencePage implements IWorkbenchPreferencePage {

	/**
	 *
	 */
	public PackageBasedTemplates() {
		super();
		this.templatePrefix = TEMPLATE;
		setDescription("Templates preference");
	}

	/**
	 *
	 * @return
	 */
	@Override
	protected String getAllTemplatesPreferenceKey() {
		return P_ALL_TEMPLATES;
	}

	@Override
	protected boolean isDetailedTemplate() {
		return true;
	}

}
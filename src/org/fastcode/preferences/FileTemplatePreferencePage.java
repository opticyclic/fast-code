package org.fastcode.preferences;

import static org.fastcode.preferences.PreferenceConstants.P_ALL_TEMPLATES;

import org.eclipse.ui.IWorkbenchPreferencePage;

public class FileTemplatePreferencePage extends AbstractTableTemplatePreferencePage implements IWorkbenchPreferencePage {

	/**
	 *
	 * This is Constructor for FileTemplatePreferencePage
	 */
	public FileTemplatePreferencePage() {
		super();
		//this.templatePrefix = P_FILE_TEMPLATE_PREFIX;
		setDescription("File Template Preference");

	}

	@Override
	protected String getAllTemplatesPreferenceKey() {
		return P_ALL_TEMPLATES;//P_FILE_ALL_TEMPLATES;
	}

	/**
	 *
	 */
	@Override
	protected boolean isDetailedTemplate() {
		return false;
	}

	/**
	 *
	 */
	@Override
	protected boolean isShowAllowedFileExtension() {
		return true;
	}

}

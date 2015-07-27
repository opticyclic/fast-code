package org.fastcode.popup.actions.snippet;

import static org.fastcode.util.XMLUtil.importXML;

import org.fastcode.common.TemplateStore;
import org.fastcode.setting.TemplateSettings;
import org.fastcode.util.TemplateUtil;

public class CreateDatabaseImportSnippetAction extends CreateDatabaseImportExportSnippetViewAction {

	/**
	 *
	 * This is Constructor for CreateImportSnippetViewAction
	 */
	public CreateDatabaseImportSnippetAction() {

	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.CreateDatabaseImportExportSnippetViewAction#processTemplates(java.lang.String, java.lang.String)
	 */
	@Override
	protected void processTemplates(final String fileName, final String templatePreferenceName, final String folderName) throws Exception {
		//processXML(fileName, templatePreferenceName, folderName);
		importXML(fileName, templatePreferenceName, folderName);
		TemplateSettings.setReload(true);
		TemplateStore.getInstance().setReload(true);
		TemplateUtil.reloadTemplates();
	}

	@Override
	protected String getType() {
		return "Import";
	}
}

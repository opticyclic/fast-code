package org.fastcode.popup.actions.snippet;

import static org.fastcode.util.XMLUtil.exportXML;

public class CreateDatabaseExportSnippetAction extends CreateDatabaseImportExportSnippetViewAction {

	public CreateDatabaseExportSnippetAction() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.CreateDatabaseImportExportSnippetViewAction#processTemplates(java.lang.String, java.lang.String)
	 */
	@Override
	protected void processTemplates(final String fileName, final String templatePreferenceName, final String folderName) throws Exception {
		//processXML(fileName, templatePreferenceName, folderName);
		exportXML(fileName, templatePreferenceName, folderName);
	}

	/**
	 *
	 */
	@Override
	protected String getType() {
		return "Export";
	}

}

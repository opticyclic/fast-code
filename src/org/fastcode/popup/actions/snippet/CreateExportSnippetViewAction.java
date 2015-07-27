/**
 * @author : Gautam

 * Created : 10/18/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.util.XMLUtil.exportXML;

public class CreateExportSnippetViewAction extends CreateImportExportSnippetViewAction {

	/**
	 *
	 */
	public CreateExportSnippetViewAction() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.CreateImportExportSnippetViewAction#processTemplates(java.lang.String, java.lang.String)
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
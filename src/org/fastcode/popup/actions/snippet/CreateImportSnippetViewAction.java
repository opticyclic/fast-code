/**
 * @author : Gautam

 * Created : 12/13/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_PROPS;
import static org.fastcode.util.XMLUtil.importXML;

import org.fastcode.common.TemplateStore;
import org.fastcode.setting.TemplateSettings;
import org.fastcode.util.TemplateUtil;

public class CreateImportSnippetViewAction extends CreateImportExportSnippetViewAction {

	//private Properties	properties;

	/**
	 *
	 * This is Constructor for CreateImportSnippetViewAction
	 */
	public CreateImportSnippetViewAction() {

	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.CreateImportExportSnippetViewAction#processTemplates(java.lang.String, java.lang.String)
	 */
	@Override
	protected void processTemplates(final String fileName, final String templatePreferenceName, final String folderName) throws Exception {
		//processXML(fileName, templatePreferenceName, folderName);
		importXML(fileName, templatePreferenceName, folderName);
		if (!templatePreferenceName.equals(P_GLOBAL_PROPS)) {
			TemplateSettings.setReload(true);
			TemplateStore.getInstance().setReload(true);
			TemplateUtil.reloadTemplates();
		}
	}

	@Override
	protected String getType() {
		return "Import";
	}
}
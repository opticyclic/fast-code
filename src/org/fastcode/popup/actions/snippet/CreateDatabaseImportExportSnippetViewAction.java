package org.fastcode.popup.actions.snippet;

import static org.fastcode.common.FastCodeConstants.DB_TEMPLATES_FOLDER;
import static org.fastcode.util.TemplateUtil.findOrcreateTemplate;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.common.FastCodeConstants.TEMPLATE_PREFERENCE_NAME;

public class CreateDatabaseImportExportSnippetViewAction extends AbstractCreateNewDatabaseSnippetAction implements IActionDelegate,
		IEditorActionDelegate, IWorkbenchWindowActionDelegate {

	/*private static final int			ALL				= 0;
	public final Map<String, String>	templatesFiles	= new LinkedHashMap<String, String>();*/
	String	databaseTemplatesFiles	= "database-templates-config.xml";

	public CreateDatabaseImportExportSnippetViewAction() {

		//this.templatesFiles.put("database-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_DATABASE_TEMPLATES.name());
		/*this.templatesFiles.put("additional-database-templates-config.xml",
				TEMPLATE_PREFERENCE_NAME.ALL_ADDITIONAL_DATABASE_TEMPLATES.name());*/
	}

	/**
	 *
	 * @param action
	 * @throws Exception
	 */
	@Override
	public void runAction() throws Exception {

		try {
			processTemplates(this.databaseTemplatesFiles, TEMPLATE_PREFERENCE_NAME.ALL_DATABASE_TEMPLATES.name(), DB_TEMPLATES_FOLDER);
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception("There was some error in " + getType() + " templates : " + ex.getMessage());
		}
		/*MessageDialog.openInformation(new Shell(), "Success", getType() + " was successfully completed to Fast Code Eclipse Plugin/"
				+ DB_TEMPLATES_FOLDER + " folder.");*/

	}

	protected String getType() {
		return null;
	}

	/**
	 * @param fileName
	 * @param templatePreferenceName
	 * @throws Exception
	 */
	protected void processTemplates(final String fileName, final String templatePreferenceName, final String folderName) throws Exception {
		final IFile file = findOrcreateTemplate(fileName, folderName);
		if (!file.isSynchronized(0)) {
			throw new Exception(fileName + " is not Synchronized, please refresh and try again.");
		}
	}

}

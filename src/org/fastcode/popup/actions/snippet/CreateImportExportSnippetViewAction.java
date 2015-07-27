/**
 * @author : Gautam
 *
 * Created : 12/23/2010
 *
 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.common.FastCodeConstants.TEMPLATES_FOLDER;
import static org.fastcode.util.TemplateUtil.findOrcreateTemplate;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.common.FastCodeConstants.TEMPLATE_PREFERENCE_NAME;

public abstract class CreateImportExportSnippetViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate,
		IActionDelegate, IWorkbenchWindowActionDelegate {

	private static final int	ALL	= 0;

	//public final Map<String, String>	templatesFiles	= new LinkedHashMap<String, String>();

	/**
	 *
	 * This is Constructor for CreateImportExportSnippetViewAction
	 */
	public CreateImportExportSnippetViewAction() {
		//this.templatesFiles.put("templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_TEMPLATES.toString());
		//this.templatesFiles.put("additional-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_ADDITIONAL_TEMPLATES.toString());
		//this.templatesFiles.put("fast-code.properties", TEMPLATE_PREFERENCE_NAME.GLOBAL_PROPS.toString());
	}

	/**
	 *
	 * @param action
	 * @throws Exception
	 */
	@Override
	public void runAction() throws Exception {
		try {
			processTemplates("fast-code.properties", TEMPLATE_PREFERENCE_NAME.GLOBAL_PROPS.toString(), TEMPLATES_FOLDER);
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception("There was some error in " + getType() + " templates : " + ex.getMessage());
		}
		/*final String[] choices = new String[this.templatesFiles.size() + 2];
		choices[0] = "All";
		choices[1] = "None";

		int i = 0;
		for (final Entry<String, String> entry : this.templatesFiles.entrySet()) {
			choices[2 + i++] = entry.getKey();
		}

		final String choice = getChoiceFromMultipleValues(new Shell(), getType(), "Choose the file you would like to " + getType(), choices);
		if (choice == null || choice.equals("None")) {
			return;
		}

		final int result = findInStringArray(choice, choices);

		int count = 2;
		try {
			for (final Entry<String, String> entry : this.templatesFiles.entrySet()) {
				if (result == ALL || result == count++) {
					processTemplates(entry.getKey(), entry.getValue(), TEMPLATES_FOLDER);
				}
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception("There was some error in " + getType() + " templates : " + ex.getMessage());
		}
		MessageDialog.openInformation(new Shell(), "Success", getType() + " was successfully completed to Fast Code Eclipse Plugin/"
				+ TEMPLATES_FOLDER + " folder.");*/
	}

	protected abstract String getType();

	/**
	 * @param fileName
	 * @param templatePreferenceName
	 * @param folderName
	 * @throws Exception
	 */
	protected void processTemplates(final String fileName, final String templatePreferenceName, final String folderName) throws Exception {
		final IFile file = findOrcreateTemplate(fileName, folderName);
		if (!file.isSynchronized(ALL)) {
			throw new Exception(fileName + " is not Synchronized, please refresh and try again.");
		}
	}
}
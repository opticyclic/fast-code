/**
 * @author : Gautam

 * Created : 08/09/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_SELECT;
import static org.fastcode.util.SourceUtil.checkForErrors;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.fastcode.common.CreateSnippetData;
import org.fastcode.dialog.CreateSnippetDialog;
import org.fastcode.setting.TemplateSettings;
import org.fastcode.util.MessageUtil;

public class CreateHqlSelectViewAction extends CreateHqlGenericViewAction {

	/**
	 *
	 */
	public CreateHqlSelectViewAction() {
		this.templateType = TEMPLATE_TYPE_CREATE_HQL_SELECT;
		this.templatePrefix = TEMPLATE;
	}

	/*@Override
	public String getTemplateTypeForNamedQuery() {
		return P_ADDITIONAL_TEMPLATE_PREFIX + UNDERSCORE + "hql.select.format";
	}*/

	@Override
	protected String[] getFieldTypesForHql() {
		return new String[] { "selected_fields", "join_fields", "where_fields" };
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.fastcode.popup.actions.snippet.CreateHqlGenericViewAction#
	 * getCreateSnippetData(java.lang.String)
	 */
	@Override
	public CreateSnippetData getCreateSnippetData(final String fileName) throws Exception {
		final CreateSnippetData createSnippetData = new CreateSnippetData();
		final TemplateSettings templateSettings = getTemplateSettings(this.templateType);

		if (validateTemplateSetting(templateSettings, fileName)) {
			createSnippetData.setTemplatePrefix(this.templatePrefix);
			createSnippetData.setSnippetTypes(new String[] { this.templateType.replaceFirst("^" + this.templatePrefix + UNDERSCORE,
					EMPTY_STR) });
			// createSnippetData.setFromTempSettings(this.fromTemplateSetting);
			createSnippetData.setTemplateVariationField(getTemplateVariationField());
			createSnippetData.setClassNames(classNames);
			createSnippetData.setTemplateSettings(templateSettings);
			createSnippetData.setEditorpart(this.editorPart);
			IJavaProject project;
			final ICompilationUnit compUnit = getCompilationUnitFromEditor();
			if (compUnit == null) {
				final IFile file = (IFile) this.editorPart.getEditorInput().getAdapter(IFile.class);
				project = JavaCore.create(file.getProject());
			} else {
				project = compUnit.getJavaProject();
			}
			createSnippetData.setJavaProject(project);
			final CreateSnippetDialog createSnippetDialog = new CreateSnippetDialog(new Shell(), createSnippetData);
			if (createSnippetDialog.open() == Window.CANCEL) {
				return null;
			}
			final Shell parentShell = MessageUtil.getParentShell();
			final Shell shell = parentShell == null ? new Shell() : parentShell;
			if (checkForErrors(createSnippetData.getFromClass().getResource())
					&& MessageDialog.openQuestion(shell, "Error", "There seems to be some problems associated with "
							+ createSnippetData.getFromClass().getElementName()
							+ ". It is better to fix those problems and try again. Want to abort?")) {
				return null;
			}
			/*if (checkForErrors(createSnippetData.getToClass().getResource())
					&& MessageDialog.openQuestion(shell, "Error", "There seems to be some problems associated with "
							+ createSnippetData.getToClass().getElementName()
							+ ". It is better to fix those problems and try again. Want to abort?")) {
				return null;
			}*/

			return createSnippetData;
		}

		return null;
	}

}

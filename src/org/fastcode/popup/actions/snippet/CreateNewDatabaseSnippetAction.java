package org.fastcode.popup.actions.snippet;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_DELETE_NAMED_QUERY;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_DELETE_WITH_NAMED_QUERY_ANNOTATION;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_INSERT_NAMED_QUERY;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_INSERT_WITH_NAMED_QUERY_ANNOTATION;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_NAMED_QUERY;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_WITH_JOIN;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_WITH_JOIN_AND_MAPPER;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_WITH_NAMED_QUERY_ANNOTATION;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_UPDATE_NAMED_QUERY;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_UPDATE_WITH_NAMED_QUERY_ANNOTATION;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ALLOWED_FILE_NAMES;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ENABLE_TEMPLATE;
import static org.fastcode.preferences.PreferenceUtil.getTemplatePreferenceKey;
import static org.fastcode.setting.TemplateSettings.checkFilePattern;
import static org.fastcode.util.DatabaseUtil.getSchemaFromDb;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.makeWord;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.CreateSnippetData;
import org.fastcode.common.FastCodeSelectionDialog;
import org.fastcode.common.StringSelectionDialog;
import org.fastcode.dialog.CreateSnippetDialog;
import org.fastcode.preferences.DatabaseConnectionSettings;
import org.fastcode.setting.TemplateSettings;

public class CreateNewDatabaseSnippetAction extends AbstractCreateNewDatabaseSnippetAction implements IEditorActionDelegate,
		IActionDelegate, IWorkbenchWindowActionDelegate {
	private static Map<String, Class<? extends IActionDelegate>>	actionClassesMap	= new HashMap<String, Class<? extends IActionDelegate>>();
	private static final String[]									EXCLUDED_TEMPLATES	= {
			DATABASE_TEMPLATE_SELECT_WITH_NAMED_QUERY_ANNOTATION, DATABASE_TEMPLATE_INSERT_WITH_NAMED_QUERY_ANNOTATION,
			DATABASE_TEMPLATE_UPDATE_WITH_NAMED_QUERY_ANNOTATION, DATABASE_TEMPLATE_DELETE_WITH_NAMED_QUERY_ANNOTATION,
			DATABASE_TEMPLATE_INSERT_NAMED_QUERY, DATABASE_TEMPLATE_UPDATE_NAMED_QUERY, DATABASE_TEMPLATE_DELETE_NAMED_QUERY,
			DATABASE_TEMPLATE_SELECT_NAMED_QUERY, DATABASE_TEMPLATE_SELECT_WITH_JOIN, DATABASE_TEMPLATE_SELECT_WITH_JOIN_AND_MAPPER };
	static {
		actionClassesMap.put(DATABASE_TEMPLATE_INSERT_NAMED_QUERY, CreateNewDatabaseInsertWithNamedQueryAction.class);
		actionClassesMap.put(DATABASE_TEMPLATE_UPDATE_NAMED_QUERY, CreateNewDatabaseUpdateWithNamedQueryAction.class);
		actionClassesMap.put(DATABASE_TEMPLATE_DELETE_NAMED_QUERY, CreateNewDatabaseDeleteWithNamedQueryAction.class);
		actionClassesMap.put(DATABASE_TEMPLATE_SELECT_NAMED_QUERY, CreateNewDatabaseSelectWithNamedQueryAction.class);
	}

	public CreateNewDatabaseSnippetAction() {
		// this.dbTemplate = true;
		this.templatePrefix = P_DATABASE_TEMPLATE_PREFIX;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewDatabaseSnippetAction#runAction()
	 */
	@Override
	public void runAction() throws Exception {
		try {
			super.runAction();
			return;
		} catch (final SQLException e) {
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#getTemplateType()
	 */
	@Override
	protected String getTemplateType() {
		if (this.window != null) {
			this.editorPart = this.window.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		}
		final String fileName = this.editorPart == null ? EMPTY_STR : this.editorPart.getEditorInput().getName();
		String[] snippetTypes = TemplateSettings.getEnabledTemplateTypes(fileName, this.templatePrefix).toArray(new String[0]);
		snippetTypes = removeExcludedTemplates(snippetTypes, EXCLUDED_TEMPLATES, fileName);
		if (snippetTypes == null || snippetTypes.length == 0) {
			return null;
		}

		final Shell shell = this.editorPart == null ? new Shell() : this.editorPart.getSite().getShell();

		final List<String> snpTypes = new ArrayList<String>();
		for (final String snpTyp : snippetTypes) {
			final TemplateSettings templateSetting = super.getTemplateSettings(snpTyp);
			if (templateSetting != null) {
				snpTypes.add(snpTyp);
			}
		}
		snippetTypes = snpTypes.toArray(new String[0]);
		if (snippetTypes == null || snippetTypes.length == 0) {
			return null;
		}
		if (snippetTypes.length == 1) {
			return MessageDialog.openConfirm(shell, "Confirm", "Only one snippet type available for this file, it is "
					+ makeWord(snippetTypes[0]) + ". Continue?") ? snippetTypes[0] : null;
		}
		// below line added to check template loading issue
		// TemplateSettings.setReload(true);

		for (int i = 0; i < snippetTypes.length; i++) {
			snippetTypes[i] = snippetTypes[i].replaceFirst("^" + this.templatePrefix + UNDERSCORE, EMPTY_STR);
		}
		final FastCodeSelectionDialog snippetSelectionDialog = new StringSelectionDialog(shell, "Snippet Types",
				"Please select one of the snippet types", snippetTypes, false);
		if (snippetSelectionDialog.open() == Window.CANCEL) {
			return null;
		}

		this.templateType = this.templatePrefix + UNDERSCORE + (String) snippetSelectionDialog.getResult()[0];
		this.description = makeWord(this.templateType);
		return this.templateType;
	}

	/**
	 * @return
	 */
	protected String[] getTemplateTypes() {
		if (this.window != null) {
			this.editorPart = this.window.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		}
		final String fileName = this.editorPart == null ? EMPTY_STR : this.editorPart.getEditorInput().getName();
		String[] snippetTypes = TemplateSettings.getEnabledTemplateTypes(fileName, this.templatePrefix).toArray(new String[0]);
		snippetTypes = removeExcludedTemplates(snippetTypes, EXCLUDED_TEMPLATES, fileName);
		if (snippetTypes == null || snippetTypes.length == 0) {
			return null;
		}

		final Shell shell = this.editorPart == null ? new Shell() : this.editorPart.getSite().getShell();

		final List<String> snpTypes = new ArrayList<String>();
		for (final String snpTyp : snippetTypes) {
			final TemplateSettings templateSetting = super.getTemplateSettings(snpTyp);
			if (templateSetting != null) {
				snpTypes.add(snpTyp);
			}
		}
		snippetTypes = snpTypes.toArray(new String[0]);
		if (snippetTypes == null || snippetTypes.length == 0) {
			return null;
		}
		/*if (snippetTypes.length == 1) {
			return MessageDialog.openConfirm(shell, "Confirm", "Only one snippet type available for this file, it is " + makeWord(snippetTypes[0])
					+ ". Continue?") ? snippetTypes[0] : null;
		}*/
		// below line added to check template loading issue
		// TemplateSettings.setReload(true);

		for (int i = 0; i < snippetTypes.length; i++) {
			snippetTypes[i] = snippetTypes[i].replaceFirst("^" + this.templatePrefix + UNDERSCORE, EMPTY_STR);
		}
		/*		final FastCodeSelectionDialog snippetSelectionDialog = new StringSelectionDialog(shell, "Snippet Types", "Please select one of the snippet types",
						snippetTypes, false);
				if (snippetSelectionDialog.open() == Window.CANCEL) {
					return null;
				}

				this.templateType = this.templatePrefix + UNDERSCORE + (String) snippetSelectionDialog.getResult()[0];
				this.description = makeWord(this.templateType);*/
		return snippetTypes; //this.templateType;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewDatabaseSnippetAction#getCreateSnippetData(java.lang.String)
	 */
	@Override
	public CreateSnippetData getCreateSnippetData(final String fileName) throws Exception {
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		final CreateSnippetData createSnippetData = new CreateSnippetData();
		createSnippetData.setTemplatePrefix(this.templatePrefix);
		final String[] snippetTypes = getTemplateTypes();
		createSnippetData.setSnippetTypes(snippetTypes);
		// getTableFromDb(this.con);
		// createSnippetData.setTablesInDB(this.databaseCache.getDbTableListMap().get(databaseConnectionSettings.getTypesofDabases()));
		// createSnippetData.setTemplateSettings(this.templateSettings);
		IJavaProject project;
		final ICompilationUnit compUnit = getCompilationUnitFromEditor();
		if (compUnit == null) {
			final IFile file = (IFile) this.editorPart.getEditorInput().getAdapter(IFile.class);
			project = JavaCore.create(file.getProject());
		} else {
			project = compUnit.getJavaProject();
		}
		createSnippetData.setJavaProject(project);
		getSchemaFromDb(this.con, databaseConnectionSettings.getTypesofDabases());
		createSnippetData.setSchemasInDB(this.databaseCache.getDbSchemaListMap().get(databaseConnectionSettings.getTypesofDabases()));
		final CreateSnippetDialog createSnippetDialog = new CreateSnippetDialog(new Shell(), createSnippetData);
		if (createSnippetDialog.open() == Window.CANCEL) {
			if (this.con != null) {
				this.con.close();
			}
			return null;
		}
		return createSnippetData;
	}

	/**
	 *
	 * @param snippetTypes
	 * @param excludedTemplates
	 * @return
	 */
	private String[] removeExcludedTemplates(final String[] snippetTypes, final String[] excludedTemplates, final String fileName) {
		if (snippetTypes == null || snippetTypes.length == 0 || excludedTemplates == null || excludedTemplates.length == 0) {
			return snippetTypes;
		}

		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final List<String> excludeTempaltesForFileName = new ArrayList<String>();

		for (final String excludedTemplate : excludedTemplates) {
			final boolean enabled = preferenceStore.getBoolean(getTemplatePreferenceKey(excludedTemplate, P_TEMPLATE_ENABLE_TEMPLATE));
			if (!enabled) {
				continue;
			}
			final String allowedFileNames = preferenceStore.getString(getTemplatePreferenceKey(excludedTemplate,
					P_TEMPLATE_ALLOWED_FILE_NAMES));
			if (isEmpty(allowedFileNames)) {
				continue;
			} else {
				if (checkFilePattern(fileName, allowedFileNames.split("\\s+"))) {
					excludeTempaltesForFileName.add(excludedTemplate);
				}
			}
		}
		String[] retSnippets;
		if (snippetTypes.length < excludeTempaltesForFileName.size()) {
			retSnippets = new String[snippetTypes.length];
		} else {
			retSnippets = new String[snippetTypes.length - excludeTempaltesForFileName.size()];
			//final List<String> excludedTemplateList = Arrays.asList(excludedTemplates);
		}

		int i = 0;
		for (final String snippet : snippetTypes) {
			if (snippet != null && !excludeTempaltesForFileName.contains(snippet) && i < retSnippets.length) {
				retSnippets[i++] = snippet;
			}
		}
		return retSnippets;
	}
}

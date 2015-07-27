/**
 * @author : Gautam

 * Created : 05/12/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ALLOWED_FILE_NAMES;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ENABLE_TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_COPY_CLASS;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_COPY_CLASS_SINGLE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_DELETE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_DELETE_NAMED_QUERY_WITH_ANNOTATION;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_DELETE_WITH_NAMED_QUERY;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_SELECT;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_SELECT_NAMED_QUERY_WITH_ANNOTATION;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_SELECT_WITH_NAMED_QUERY;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_UPDATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_UPDATE_NAMED_QUERY_WITH_ANNOTATION;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_HQL_UPDATE_WITH_NAMED_QUERY;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_SIMPLE_CLASS_SNIPPET;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_CREATE_SIMPLE_FILE_SNIPPET;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_PRINT_CLASS;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_PRINT_RESOURCE_BUNDLE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_RESOURCE_BUNDLE;
import static org.fastcode.preferences.PreferenceUtil.getTemplatePreferenceKey;
import static org.fastcode.setting.TemplateSettings.checkFilePattern;
import static org.fastcode.util.SourceUtil.checkForErrors;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.makeWord;

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
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.Activator;
import org.fastcode.common.CreateSnippetData;
import org.fastcode.common.FastCodeSelectionDialog;
import org.fastcode.common.StringSelectionDialog;
import org.fastcode.dialog.CreateSnippetDialog;
import org.fastcode.setting.TemplateSettings;
import org.fastcode.util.MessageUtil;

public class CreateNewSnippetAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	private static Map<String, Class<? extends IActionDelegate>>	actionClassesMap	= new HashMap<String, Class<? extends IActionDelegate>>();

	private static final String[]									EXCLUDED_TEMPLATES	= { TEMPLATE_TYPE_CREATE_SIMPLE_CLASS_SNIPPET,
			TEMPLATE_TYPE_CREATE_SIMPLE_FILE_SNIPPET, TEMPLATE_TYPE_CREATE_HQL_SELECT_NAMED_QUERY_WITH_ANNOTATION,
			TEMPLATE_TYPE_CREATE_HQL_DELETE_NAMED_QUERY_WITH_ANNOTATION, TEMPLATE_TYPE_CREATE_HQL_UPDATE_NAMED_QUERY_WITH_ANNOTATION,
			TEMPLATE_TYPE_CREATE_HQL_DELETE_WITH_NAMED_QUERY, TEMPLATE_TYPE_CREATE_HQL_UPDATE_WITH_NAMED_QUERY,
			TEMPLATE_TYPE_CREATE_HQL_SELECT_WITH_NAMED_QUERY							};

	static {
		actionClassesMap.put(TEMPLATE_TYPE_CREATE_HQL_SELECT, CreateHqlSelectViewAction.class);
		actionClassesMap.put(TEMPLATE_TYPE_CREATE_HQL_UPDATE, CreateHqlUpdateViewAction.class);
		actionClassesMap.put(TEMPLATE_TYPE_CREATE_HQL_DELETE, CreateHqlDeleteViewAction.class);
		/*actionClassesMap.put(TEMPLATE_TYPE_CREATE_HQL_DELETE_WITH_NAMED_QUERY, CreateHqlDeleteWithNamedQueryViewAction.class);
		actionClassesMap.put(TEMPLATE_TYPE_CREATE_HQL_SELECT_WITH_NAMED_QUERY, CreateHqlSelectWithNamedQueryViewAction.class);
		actionClassesMap.put(TEMPLATE_TYPE_CREATE_HQL_UPDATE_WITH_NAMED_QUERY, CreateHqlUpdateWithNamedQueryViewAction.class);*/
		//		actionClassesMap.put(TEMPLATE_TYPE_CREATE_NEW_TOSTRING, CreateNewToStringViewAction.class);
		actionClassesMap.put(TEMPLATE_TYPE_COPY_CLASS, CreateNewCopyBeanViewAction.class);
		actionClassesMap.put(TEMPLATE_TYPE_COPY_CLASS_SINGLE, CreateNewCopyBeanSingleViewAction.class);
		//		actionClassesMap.put(TEMPLATE_TYPE_CREATE_NEW_DOZER_MAPPING, CreateNewDozerViewAction.class);
		//		actionClassesMap.put(TEMPLATE_TYPE_CREATE_NEW_DOZER_MAPPING_SINGLE, CreateNewDozerSingleViewAction.class);
		actionClassesMap.put(TEMPLATE_TYPE_PRINT_CLASS, CreateNewPrintViewAction.class);
		actionClassesMap.put(TEMPLATE_TYPE_PRINT_RESOURCE_BUNDLE, CreateNewPrintResourceBundleViewAction.class);
		actionClassesMap.put(TEMPLATE_TYPE_RESOURCE_BUNDLE, CreateNewResourceBundleViewAction.class);
		//		actionClassesMap.put(TEMPLATE_TYPE_CREATE_SIMPLE_FILE_SNIPPET, CreateNewSimpleFileViewAction.class);
		//		actionClassesMap.put(TEMPLATE_TYPE_CREATE_SIMPLE_CLASS_SNIPPET, CreateNewSimpleBeanViewAction.class);
		actionClassesMap.put(TEMPLATE_TYPE_PRINT_RESOURCE_BUNDLE, CreateNewPrintResourceBundleViewAction.class);
	}

	/**
	 *
	 */
	public CreateNewSnippetAction() {
		this.templatePrefix = TEMPLATE;
	}

	/**
	 * @param action
	 * @throws Exception
	 *
	 */
	@Override
	public void runAction() throws Exception {
		//final TemplateCache templateCache = TemplateCache.getInstance();

		final CreateSnippetData createSnippetData = getCreateSnippetData(EMPTY_STR);

		this.snippetSelection = true;

		if (createSnippetData == null || createSnippetData.getTemplateType() == null) {
			return;
		}

		if (!actionClassesMap.containsKey(createSnippetData.getTemplateType())) {
			super.createSnippetData = createSnippetData;
			super.runAction();
			return;
		}
		final Class<? extends IActionDelegate> clazz = actionClassesMap.get(createSnippetData.getTemplateType());
		try {
			final IActionDelegate instance = clazz.newInstance();
			if (this.window != null) {
				this.editorPart = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			}
			((AbstractCreateNewSnippetAction) instance).editorPart = this.editorPart;
			((AbstractCreateNewSnippetAction) instance).useLast = this.useLast;
			((AbstractCreateNewSnippetAction) instance).createSnippetData = createSnippetData;
			((AbstractCreateNewSnippetAction) instance).snippetSelection = true;

			((AbstractCreateNewSnippetAction) instance).runAction();
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage());
		} finally {
			this.snippetSelection = false;
		}
	}

	/**
	 *
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
	 *
	 * @param snippetTypes
	 * @param excludedTemplates
	 * @param fileName
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

		for (int i = 0; i < snippetTypes.length; i++) {
			snippetTypes[i] = snippetTypes[i].replaceFirst("^" + this.templatePrefix + UNDERSCORE, EMPTY_STR);
		}
		return snippetTypes;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#getCreateSnippetData(java.lang.String)
	 */
	@Override
	protected CreateSnippetData getCreateSnippetData(final String fileName) throws Exception {
		final CreateSnippetData createSnippetData = new CreateSnippetData();
		createSnippetData.setTemplatePrefix(this.templatePrefix);
		createSnippetData.setSnippetTypes(getTemplateTypes());
		//createSnippetData.setFromTempSettings(this.fromTemplateSetting);
		createSnippetData.setTemplateVariationField(getTemplateVariationField());
		createSnippetData.setClassNames(classNames);
		//createSnippetData.setEditorFileName(fileName);

		IJavaProject project;
		ICompilationUnit compUnit = null;
		if (this.editorPart != null) {
			createSnippetData.setEditorpart(this.editorPart);
			compUnit = getCompilationUnitFromEditor();
			if (compUnit == null) {
				final IFile file = (IFile) this.editorPart.getEditorInput().getAdapter(IFile.class);
				project = JavaCore.create(file.getProject());
			} else {
				project = compUnit.getJavaProject();
			}
			createSnippetData.setJavaProject(project);

			final ISelection selection = this.editorPart.getEditorSite().getSelectionProvider().getSelection();
			if (selection instanceof ITextSelection) {
				final String selectedText = ((ITextSelection) selection).getText().trim();
				if (!isEmpty(selectedText)) {
					createSnippetData.setSelectedText(selectedText);
				}
			}
		}
		final CreateSnippetDialog createSnippetDialog = new CreateSnippetDialog(new Shell(), createSnippetData);
		getLocalVariables(compUnit, createSnippetData);
		if (createSnippetDialog.open() == Window.CANCEL) {
			return null;
		}

		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;
		if (createSnippetData.getFromClass() != null
				&& checkForErrors(createSnippetData.getFromClass().getResource())
				&& MessageDialog.openQuestion(shell, "Error", "There seems to be some problems associated with "
						+ createSnippetData.getFromClass().getElementName()
						+ ". It is better to fix those problems and try again. Want to abort?")) {
			return null;
		}
		if (createSnippetData.getToClass() != null
				&& checkForErrors(createSnippetData.getToClass().getResource())
				&& MessageDialog.openQuestion(shell, "Error", "There seems to be some problems associated with "
						+ createSnippetData.getToClass().getElementName()
						+ ". It is better to fix those problems and try again. Want to abort?")) {
			return null;
		}

		return createSnippetData;
	}

}
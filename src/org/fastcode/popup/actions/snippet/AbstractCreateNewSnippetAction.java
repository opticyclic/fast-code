/**
 *
 */
package org.fastcode.popup.actions.snippet;

import static org.eclipse.jdt.core.Flags.isPrivate;
import static org.eclipse.jdt.core.Flags.isProtected;
import static org.eclipse.jdt.core.Flags.isPublic;
import static org.eclipse.jdt.core.IJavaElement.FIELD;
import static org.eclipse.jdt.core.IJavaElement.METHOD;
import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.eclipse.jface.dialogs.MessageDialog.openQuestion;
import static org.eclipse.jface.dialogs.MessageDialog.openWarning;
import static org.eclipse.jface.window.Window.CANCEL;
import static org.fastcode.common.FastCodeConstants.AUTO_CHECKIN;
import static org.fastcode.common.FastCodeConstants.CLASSES_SELECTED;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.COMMON_CLASS_SUFFIX;
import static org.fastcode.common.FastCodeConstants.DATABASE_NAME;
import static org.fastcode.common.FastCodeConstants.DEFAULT_CLASS_NAME_STR;
import static org.fastcode.common.FastCodeConstants.DEFAULT_TEMPLATE_VARIATION_FIELD;
import static org.fastcode.common.FastCodeConstants.DEFAULT_TEMPLATE_VARIATION_FIELD_NAME;
import static org.fastcode.common.FastCodeConstants.DEFAULT_TEMPLATE_VARIATION_FIELD_VALUE;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_CLASS_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_FILE_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_FOLDER_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_PACKAGE_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_PROJECT_STR;
import static org.fastcode.common.FastCodeConstants.EXIT_KEY;
import static org.fastcode.common.FastCodeConstants.FALSE_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.FILES_SELECTED;
import static org.fastcode.common.FastCodeConstants.FILE_TYPE_PROPERTY;
import static org.fastcode.common.FastCodeConstants.FILE_TYPE_XML;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.FROM_INSTANCE_STR;
import static org.fastcode.common.FastCodeConstants.FULL_CLASS_NAME_STR;
import static org.fastcode.common.FastCodeConstants.HQL_NAMED_QUERY_WITH_ANNOTATION_STR;
import static org.fastcode.common.FastCodeConstants.INSTANCE_STR;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;
import static org.fastcode.common.FastCodeConstants.JAVA_TABLE_NAME;
import static org.fastcode.common.FastCodeConstants.KEYWORD_FROM_FULL_CLASS;
import static org.fastcode.common.FastCodeConstants.KEYWORD_TO_FULL_CLASS;
import static org.fastcode.common.FastCodeConstants.NAMED_QUERY_ANNOTATION_STR;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.PACKAGE_NAME_STR;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_ENUM;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PACKAGE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PROJECT;
import static org.fastcode.common.FastCodeConstants.SCHEMA;
import static org.fastcode.common.FastCodeConstants.SELECTED_TEXT;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.STARTLINE_ENDLINE;
import static org.fastcode.common.FastCodeConstants.TABLE;
import static org.fastcode.common.FastCodeConstants.TEMPLATE_TYPE;
import static org.fastcode.common.FastCodeConstants.TO_INSTANCE_STR;
import static org.fastcode.common.FastCodeConstants.TRUE_STR;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.common.FastCodeConstants.UNDER_SCORE;
import static org.fastcode.common.FastCodeConstants._KEYWORD_FROM_TYPE;
import static org.fastcode.common.FastCodeConstants._KEYWORD_TO_TYPE;
import static org.fastcode.popup.actions.snippet.TemplateTagsProcessor.actionList;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_WITH_JOIN;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_WITH_JOIN_AND_MAPPER;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.P_EMBEDDED_FIELDS_EXCLUDE_PACKAGE;
import static org.fastcode.preferences.PreferenceConstants.P_EMBEDDED_FIELDS_INCLUDE_PACKAGE;
import static org.fastcode.preferences.PreferenceConstants.P_EMBEDDED_FIELDS_VIEW;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_STRICTLY_MAINTAIN_GETTER_SETTER;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATES_TO_ENABLE_POJO;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_CREATE_FILE_WITH_SELECTED_CONTENT;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_CREATE_IMPL;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_PRINT_FIELDS_OF_CLASS_WEB_SINGLE;
import static org.fastcode.util.FastCodeUtil.closeInputStream;
import static org.fastcode.util.SourceUtil.checkForErrors;
import static org.fastcode.util.SourceUtil.doesGetterSetterExist;
import static org.fastcode.util.SourceUtil.findTypeForImport;
import static org.fastcode.util.SourceUtil.getImagefromFCCacheMap;
import static org.fastcode.util.SourceUtil.getLocalVarFromCompUnit;
import static org.fastcode.util.SourceUtil.getPublicMethods;
import static org.fastcode.util.SourceUtil.getSelectedMembers;
import static org.fastcode.util.SourceUtil.populateFCCacheEntityImageMap;
import static org.fastcode.util.SourceUtil.refreshProject;
import static org.fastcode.util.StringUtil.changeFirstLetterToUpperCase;
import static org.fastcode.util.StringUtil.createDefaultInstance;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.isJavaInArray;
import static org.fastcode.util.StringUtil.isStringInArray;
import static org.fastcode.util.StringUtil.makeWord;
import static org.fastcode.util.StringUtil.parseAdditonalParam;
import static org.fastcode.util.StringUtil.parseType;
import static org.fastcode.util.StringUtil.reverseCamelCase;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.internal.ide.dialogs.OpenResourceDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.fastcode.Activator;
import org.fastcode.common.CreateSnippetData;
import org.fastcode.common.FastCodeAdditionalParams;
import org.fastcode.common.FastCodeConstants.EMBEDDED_FIELDS_VIEW;
import org.fastcode.common.FastCodeConstants.FIRST_TEMPLATE;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER;
import org.fastcode.common.FastCodeConstants.SECOND_TEMPLATE;
import org.fastcode.common.FastCodeDataBaseFieldDecorator;
import org.fastcode.common.FastCodeField;
import org.fastcode.common.FastCodeFile;
import org.fastcode.common.FastCodeFileSelectionDialog;
import org.fastcode.common.FastCodeFolder;
import org.fastcode.common.FastCodeMethod;
import org.fastcode.common.FastCodePackage;
import org.fastcode.common.FastCodeProject;
import org.fastcode.common.FastCodePropertySelectionDialog;
import org.fastcode.common.FastCodeReturn;
import org.fastcode.common.FastCodeSelectionDialog;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.FastCodeTypeSelectionDialog;
import org.fastcode.common.FastcodeSelectedText;
import org.fastcode.common.ReturnValuesData;
import org.fastcode.common.SnippetType;
import org.fastcode.common.VariableSelectionDialog;
import org.fastcode.dialog.CreateSnippetDialog;
import org.fastcode.dialog.ReturnValuesDialog;
import org.fastcode.dialog.TableUtil;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.setting.TemplateSettings;
import org.fastcode.util.FastCodeResourceChangeListener;
import org.fastcode.util.FileUtil;
import org.fastcode.util.MessageUtil;
import org.fastcode.util.SourceUtil;
import org.fastcode.util.StringUtil;

/**
 * @author Gautam
 *
 */
public abstract class AbstractCreateNewSnippetAction {

	protected IWorkbenchWindow					window;
	protected IEditorPart						editorPart;
	protected String							description;
	protected String							templateType;
	protected TemplateSettings					templateSettings;
	protected TemplateSettings					lastTemplateSettings;
	protected boolean							useLast;
	protected IType								lastType;
	protected IType								lastToType;
	protected String							errorMessage;
	// protected boolean dbTemplate;
	public boolean								isNamedQuery		= false;
	protected IType								hqlType;

	protected static Map<Integer, List<String>>	classNames			= new HashMap<Integer, List<String>>();
	// private static Map<Integer, List<String>> fieldNames = new
	// HashMap<Integer, List<String>>();
	protected String							templatePrefix;
	CreateSnippetData							createSnippetData;
	protected boolean							fromTemplateSetting	= false;
	protected boolean							snippetSelection	= false;

	static {
		final List<String> vals = new ArrayList<String>();
		vals.add(DEFAULT_CLASS_NAME_STR);
		classNames.put(1, vals);
		final List<String> vals1 = new ArrayList<String>();
		vals1.add("from_class");
		vals1.add("to_class");
		classNames.put(2, vals1);
		TableUtil.updateSQLFunctionsMapping();
	}

	/**
	 *
	 * @param action
	 */
	public void run(final IAction action) {
		this.errorMessage = null;
		final IRunnableWithProgress op = new IRunnableWithProgress() {

			@Override
			public void run(final IProgressMonitor monitor) {
				try {
					monitor.beginTask(action.getDescription(), 1);
					monitor.subTask("Doing " + action.getText());
					AbstractCreateNewSnippetAction.this.runAction();
				} catch (final Exception ex) {
					ex.printStackTrace();
					AbstractCreateNewSnippetAction.this.errorMessage = ex.getMessage();
				} finally {
					monitor.done();
				}
			}
		};
		final Shell shell = MessageUtil.getParentShell();
		try {
			new ProgressMonitorDialog(shell == null ? new Shell() : shell).run(false, false, op);
		} catch (final Exception ex) {
			ex.printStackTrace();
			this.errorMessage += SPACE + ex.getMessage();
		}
		if (this.errorMessage != null) {
			MessageUtil.showError(this.errorMessage, "Error");
		}
	}

	/**
	 * @throws Exception
	 */
	public void runAction() throws Exception {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(FastCodeResourceChangeListener.getInstance());
		if (this.window != null) {
			this.editorPart = this.window.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		}

		final Shell shell = this.editorPart == null || this.editorPart.getSite() == null ? new Shell() : this.editorPart.getSite()
				.getShell();

		if (this.editorPart == null) {
			openError(shell, "Error", "There is no file open in the editor.");
			return;
		}

		final String fileName = this.editorPart.getEditorInput().getName();

		if (!this.snippetSelection) {
			this.createSnippetData = getCreateSnippetData(fileName);
		}
		if (this.createSnippetData == null) {
			return;
		}

		this.templateType = this.createSnippetData.getTemplateType();

		this.description = this.templateType == null ? EMPTY_STR
				: makeWord(this.templateType.startsWith(this.templatePrefix + UNDERSCORE) ? this.templateType.substring(this.templatePrefix
						.length() + 1) : this.templateType);

		final TemplateCache templateCache = TemplateCache.getInstance();
		ICompilationUnit compilationUnit = null;
		try {
			final ISelection selection = this.editorPart.getEditorSite().getSelectionProvider().getSelection();

			if (!(selection instanceof ITextSelection)) {
				openError(null, "Error", "Unknow error occured. Please try again");
				return;
			}

			if (this.useLast) {
				this.templateType = this.createSnippetData.getTemplateType();
			}
			if (isEmpty(this.templateType)) {
				return;
			}
			this.templateSettings = this.createSnippetData.getTemplateSettings();

			if (this.useLast && templateCache.lastEditorInput != this.editorPart.getEditorInput()) {
				openError(shell, "Error", "Last action cannot be executed, editor has changed.");
				return;
			}

			final String[] allowedNames = this.templateSettings.getAllowedFileNames();
//			compilationUnit = isJavaInArray(JAVA_EXTENSION, allowedNames) ? getCompilationUnitFromEditor() : null;
			compilationUnit = getCompilationUnitFromEditor();
			if (compilationUnit == null && isJavaInArray(JAVA_EXTENSION, allowedNames) ) {
				throw new Exception("Fatal error : not a valid class.");
			}
			if (compilationUnit != null) {
				compilationUnit.becomeWorkingCopy(new NullProgressMonitor());
			}
			final IFile file = (IFile) this.editorPart.getEditorInput().getAdapter(IFile.class);
			final String source = FileUtil.getContentsFromFile(file);
			String spacesBeforeCursor = EMPTY_STR;
			if (isEmptyLineRequired(this.templateSettings, this.templateType)) {

				spacesBeforeCursor = getSpacesBeforeCursor((ITextSelection) selection, source);
			}

			final int numberOfClasses = this.templateSettings.getNumberRequiredItems();

			final IType pType = compilationUnit != null ? compilationUnit.findPrimaryType() : null;
			final Map<String, Object> placeHolders = new HashMap<String, Object>();

			final IFile editorFile = (IFile) this.editorPart.getEditorInput().getAdapter(IFile.class);
			placeHolders.put(ENCLOSING_FILE_STR, new FastCodeFile(editorFile));//new FastCodeFile(this.editorPart.getEditorInput().getName(), editorFile.getProjectRelativePath().toString()));

			final String projectRelativePath = editorFile.getProjectRelativePath().toString();
			final int segmentCount = editorFile.getProjectRelativePath().segmentCount();
			final String srcPath = segmentCount == 1 ? EMPTY_STR : projectRelativePath .substring(0, projectRelativePath.indexOf(editorFile.getProjectRelativePath().lastSegment()) - 1);

			final IFolder folder = isEmpty(srcPath) ? editorFile.getProject().getFolder(editorFile.getProject().getFullPath()) : editorFile.getProject().getFolder(srcPath);

			placeHolders.put(ENCLOSING_FOLDER_STR, new FastCodeFolder(folder));
			placeHolders.put(ENCLOSING_PROJECT_STR, new FastCodeFile(editorFile).getProject());

			if (pType != null) {
				placeHolders.put(ENCLOSING_CLASS_STR, new FastCodeType(pType));
				placeHolders.put(ENCLOSING_PACKAGE_STR, new FastCodeType(pType).getPackage());
				placeHolders.put(ENCLOSING_PROJECT_STR, new FastCodeProject(pType.getJavaProject().getProject()));
			}

			placeHolders.put(AUTO_CHECKIN, this.createSnippetData.isDoAutoCheckin());
			initializePlaceHolders(this.templateSettings, placeHolders);
			if (this.templatePrefix.equals(TEMPLATE) && this.createSnippetData.getSelectedProject() != null) {
				placeHolders.put(PLACEHOLDER_PROJECT, this.createSnippetData.getSelectedProject());
			}
			final GlobalSettings globalSettings = GlobalSettings.getInstance();
			final String commonSuffixes = globalSettings.getPropertyValue(COMMON_CLASS_SUFFIX.toUpperCase(), EMPTY_STR);
			placeHolders.put(COMMON_CLASS_SUFFIX, EMPTY_STR.equalsIgnoreCase(commonSuffixes) ? EMPTY_STR : commonSuffixes.split(COMMA));

			if (doExit(placeHolders)) {
				return;
			}
			if (!isEmpty(this.createSnippetData.getSelectedText())) {
				//if (!isEmpty(this.createSnippetData.getSelectedText()) && !this.templateType.equals(TEMPLATE_CREATE_FILE_WITH_SELECTED_CONTENT)) {
				//placeHolders.put(SELECTED_TEXT, this.createSnippetData.getSelectedText().trim());
				placeHolders.put(SELECTED_TEXT, new FastcodeSelectedText(this.createSnippetData.getSelectedText().trim()));
			}
			String tableName = null;

			if (this.templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX) && !this.templateType.equals(DATABASE_TEMPLATE_SELECT_WITH_JOIN)
					&& !this.templateType.equals(DATABASE_TEMPLATE_SELECT_WITH_JOIN_AND_MAPPER)) {
				if (this.isNamedQuery) {
					tableName = (String) placeHolders.get(TABLE);
				} else {
					tableName = this.createSnippetData.getTableSelected();
					placeHolders.put(TABLE, tableName);
					placeHolders.put(SCHEMA, this.createSnippetData.getSchemaSelected());
					placeHolders.put(DATABASE_NAME, this.createSnippetData.getSelectedDatabaseName());
					placeHolders.put(INSTANCE_STR, tableName.substring(0, 1).toLowerCase());
				}

				final String javaTableName = tableName.contains(UNDERSCORE) ? reverseCamelCase(tableName, UNDER_SCORE)
						: changeFirstLetterToUpperCase(tableName);
				placeHolders.put(JAVA_TABLE_NAME, javaTableName);
			}

			IType type = null;
			if (this.templatePrefix == TEMPLATE && requireClasses(placeHolders, this.templateSettings)) {
				if (this.useLast) {
					type = this.createSnippetData.getFromClass();
					addDefaultClassToPlaceHolders(type, placeHolders);
				} else if (numberOfClasses == 0) {
					type = pType;
					addDefaultClassToPlaceHolders(type, placeHolders);
				} else {
					if (!this.isNamedQuery) {
						if (this.createSnippetData.getFromClass() != null) {
							type = this.createSnippetData.getFromClass();
							addDefaultClassToPlaceHolders(type, placeHolders);
							placeHolders.put(_KEYWORD_FROM_TYPE, type); // :
																		// _KEYWORD_TO_TYPE,
																		// typeFromUser);
							if (numberOfClasses == 1) {
								if (type != null) {
									final FastCodeType fastCodeType = new FastCodeType(type);
									placeHolders.put(DEFAULT_CLASS_NAME_STR, fastCodeType);
								}
							} else if (numberOfClasses == 2) {
								if (type != null) {
									final FastCodeType fastCodeType = new FastCodeType(type);
									placeHolders.put("from_class", fastCodeType);
								}
								if (this.createSnippetData.getToClass() != null) {
									placeHolders.put(_KEYWORD_TO_TYPE, this.createSnippetData.getToClass());
									final FastCodeType fastCodeType = new FastCodeType(this.createSnippetData.getToClass());
									placeHolders.put("to_class", fastCodeType);
								}
							}
						}
					} else {
						if (!this.createSnippetData.isShowLocalVriable()) {
							type = this.hqlType;
						}
					}
				}
			}

			if (this.templatePrefix == TEMPLATE && this.templateSettings.getFirstTemplateItem() == FIRST_TEMPLATE.Enumeration) {
				type = this.createSnippetData.getEnumType();
				addImport(compilationUnit, type);
				placeHolders.put("enum", new FastCodeType(this.createSnippetData.getEnumType()));
				final String instanceName = this.createSnippetData.getInstanceName();
				if (instanceName != null) {
					placeHolders.put(INSTANCE_STR, instanceName);
				}
			}

			if (doExit(placeHolders)) {
				return;
			}

			// if (!this.dbTemplate) {
			if (this.templatePrefix.equals(TEMPLATE) && this.templateSettings.getFirstTemplateItem() == FIRST_TEMPLATE.Class
					&& requireFields(placeHolders, this.templateSettings)) {
				if (type == null && !this.createSnippetData.isShowLocalVriable()) {
					openError(shell, "Error", "No Class selected.");
					return;
				}
			}
			// }

			if (this.templatePrefix.equals(TEMPLATE) && requireFiles(placeHolders, this.templateSettings)) {
				// getFilesSelect(shell, placeHolders);
				if (!this.createSnippetData.getFastCodeFiles().isEmpty()) {
					placeHolders.put("file", this.createSnippetData.getFastCodeFiles().get(0));
					placeHolders.put("files", this.createSnippetData.getFastCodeFiles());
					if (this.templateSettings.getSecondTemplateItem() == SECOND_TEMPLATE.property) {
						/*if (this.templateType.equals(TEMPLATE_TYPE_PRINT_RESOURCE_BUNDLE)
								|| this.templateType.equals(TEMPLATE + UNDERSCORE + "VALUE")) {*/
						getFileElements(this.templateSettings, placeHolders, this.createSnippetData.getResourceFile());
					}
					if (this.templateSettings.getSecondTemplateItem() == SECOND_TEMPLATE.data) {
						final CreateNewFileSnippetAction createNewFileSnippetAction = new CreateNewFileSnippetAction(
								this.createSnippetData, placeHolders);
						createNewFileSnippetAction.runAction();
					}
					// loadItemsFromFile(placeHolders, this.templateSettings,
					// this.createSnippetData.getFastCodeFiles().get(0));
				}
			}
			if (doExit(placeHolders)) {
				return;
			}

			if (compilationUnit != null) {
				if (this.templatePrefix.equals(TEMPLATE) && requireClasses(placeHolders, this.templateSettings)
						|| this.templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX) && requireImport(placeHolders, this.templateSettings)) {
					// if (type != null) {
					addImport(compilationUnit, type);
					// }
				}
			}

			if (compilationUnit != null) {
				if (pType == null || !pType.exists() || !pType.isClass()) {
					openError(shell, "Error", "This cannot be done in current editor. Does not seem to be a class.");
					return;
				}
				// placeHolders.put(ENCLOSING_CLASS_STR,
				// compilationUnit.getElementName());
			}

			// IType type = this.useLast ? templateCache.lastType :
			// getTypeFromUser(templateSettings.getClassName());

			IType toType = null;
			if (this.templatePrefix.equals(TEMPLATE) && numberOfClasses > 1) {
				// placeHolders.put(KEYWORD_FROM_CLASS, new
				// FastCodeType(type.getFullyQualifiedName()));
				// placeHolders.put(KEYWORD_FROM_CLASS, type.getElementName());
				placeHolders.put(KEYWORD_FROM_FULL_CLASS, type.getFullyQualifiedName());
				placeHolders.put(FROM_INSTANCE_STR, this.createSnippetData.getInstanceName());

				// toType = this.useLast ? templateCache.lastToType :
				// getToTypeFromUser(this.templateSettings.getClassName());
				toType = this.useLast ? this.createSnippetData.getFromClass() : (IType) placeHolders.get(_KEYWORD_TO_TYPE);
				if (toType == null || toType.getFullyQualifiedName().equals(type.getFullyQualifiedName())) {
					openWarning(shell, "Warning", "Need to have valid to type.");
					return;
				}
				if (compilationUnit != null) {
					addImport(compilationUnit, toType);
				}
				// placeHolders.put(KEYWORD_TO_CLASS, new
				// FastCodeType(toType.getFullyQualifiedName()));
				// placeHolders.put(KEYWORD_TO_CLASS, toType.getElementName());
				placeHolders.put(KEYWORD_TO_FULL_CLASS, toType.getFullyQualifiedName());
				placeHolders.put(TO_INSTANCE_STR, this.createSnippetData.getToInstanceName());
			}

			Map<String, List<FastCodeField>> fieldSelection = new HashMap<String, List<FastCodeField>>();
			List<FastCodeReturn> localVariableSelection = new ArrayList<FastCodeReturn>();
			Map<String, List<FastCodeDataBaseFieldDecorator>> dbFieldSelection = new HashMap<String, List<FastCodeDataBaseFieldDecorator>>();
			if (this.templatePrefix.equals(TEMPLATE)
					&& (this.templateSettings.getFirstTemplateItem() == FIRST_TEMPLATE.Class || this.templateSettings
							.getFirstTemplateItem() == FIRST_TEMPLATE.Enumeration) && requireFields(placeHolders, this.templateSettings)) {
				if (type != null) {
					fieldSelection = this.getFields(placeHolders, type, toType);
				}
				if (this.createSnippetData.isShowLocalVriable()) {
					localVariableSelection = selectLocalVariables(compilationUnit);
					placeHolders.put("localvars", localVariableSelection);
				}
			} else if (this.templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX) && requireFields(placeHolders, this.templateSettings)
					&& !this.templateType.equals(DATABASE_TEMPLATE_SELECT_WITH_JOIN)
					&& !this.templateType.equals(DATABASE_TEMPLATE_SELECT_WITH_JOIN_AND_MAPPER)) {
				if (!placeHolders.containsKey("useExistingNamedQuery")) {
					placeHolders.put("use-alias-name", this.createSnippetData.isUseAliasName());
					dbFieldSelection = this.getFields(placeHolders);// this.getFields(tableName);
					placeHolders.put("group_by_fields", this.createSnippetData.getgroupByFieldSelectionMap());
					placeHolders.put("order_by_fields", this.createSnippetData.getorderByFieldSelectionMap());
					if (dbFieldSelection.isEmpty()) {
						return;
					}
					final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
					if (preferenceStore.getString(P_TEMPLATES_TO_ENABLE_POJO).contains(
							this.templateType.substring(this.templatePrefix.length() + 1))) {
						Map<String, String> fieldNameMethodMap = new HashMap<String, String>();
						if (this.createSnippetData.getiSelectPojoClassType() != null) {
							placeHolders.put("class", new FastCodeType(this.createSnippetData.getiSelectPojoClassType()));
							fieldNameMethodMap = checkSetMethodInPojoClass(dbFieldSelection,
									this.createSnippetData.getiSelectPojoClassType());
							if (fieldNameMethodMap != null) {
								placeHolders.put("nameMethodMap", fieldNameMethodMap);
								placeHolders.put("pojo_class_instance", createDefaultInstance(this.createSnippetData
										.getiSelectPojoClassType().getElementName()));
								addPojoClassImport(compilationUnit, this.createSnippetData.getiSelectPojoClassType(), placeHolders);
							} else {
								placeHolders.put("nameMethodMap", null);
							}
						}
					}
					for (final Entry<String, List<FastCodeDataBaseFieldDecorator>> entry : dbFieldSelection.entrySet()) {
						final String key = entry.getKey();
						placeHolders.put(key, entry.getValue());
					}
				}

				if (!this.isNamedQuery) {
					placeHolders.put("where_separator", this.createSnippetData.getWhereClauseSeparator());
				}
			} else if (this.templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX) && requireFields(placeHolders, this.templateSettings)
					&& this.templateType.equals(DATABASE_TEMPLATE_SELECT_WITH_JOIN)) {
				dbFieldSelection = getFieldsForJoin(placeHolders);
				if (dbFieldSelection.isEmpty()) {
					return;
				}
				for (final Entry<String, List<FastCodeDataBaseFieldDecorator>> entry : dbFieldSelection.entrySet()) {
					final String key = entry.getKey();
					placeHolders.put(key, entry.getValue());
				}
			}

			List<FastCodeMethod> fastCodeMethods = new ArrayList<FastCodeMethod>();
			if (this.templatePrefix.equals(TEMPLATE) && requireClasses(placeHolders, this.templateSettings)
					&& requireMethods(placeHolders, this.templateSettings)) {
				final IMethod[] methods = getPublicMethods(type);
				final IMember[] selectedMethods = getSelectedMembers(METHOD, methods, "create " + makeWord(this.templateType), true);
				if (selectedMethods == null || selectedMethods.length == 0) {
					return;
				}
				fastCodeMethods = new ArrayList<FastCodeMethod>();
				for (final IMember method : selectedMethods) {
					fastCodeMethods.add(new FastCodeMethod((IMethod) method));
				}
			}

			// Now combine fieldSelection and methodSelection into
			// memberSelection
			final Map<String, Object> memberSelection = new HashMap<String, Object>();
			memberSelection.put("methods", fastCodeMethods);
			if (!fastCodeMethods.isEmpty()) {
				placeHolders.put("method", fastCodeMethods.get(0));
				placeHolders.put("methods", fastCodeMethods);
			}
			for (final Entry<String, List<FastCodeField>> entry : fieldSelection.entrySet()) {
				final String key = entry.getKey();
				memberSelection.put(key, entry.getValue());
				if (entry.getValue().size() == 1) {// if(!entry.getValue().isEmpty()){
					placeHolders.put(StringUtil.singularize(key), entry.getValue().get(0));
				}
			}

			/*	if (this.templateSettings.getAdditionalParamaters() != null) {
					getAdditionalParameters(placeHolders, this.createSnippetData.getFromClass());
					if (doExit(placeHolders)) {
						return;
					}
				}*/

			if (this.templatePrefix.equals(TEMPLATE) && this.templateSettings.getFirstTemplateItem() == FIRST_TEMPLATE.Package) {
				placeHolders.put(PLACEHOLDER_PACKAGE, new FastCodePackage(this.createSnippetData.getPackageFragment())); // .getElementName());
				// placeHolders.put(PLACEHOLDER_PROJECT,
				// this.createSnippetData.getPackageFragment().getJavaProject().getElementName());

				if (this.templateSettings.getSecondTemplateItem() == SECOND_TEMPLATE.Class) {
					final FastCodeType[] fastCodeTypes = getClasses(placeHolders);
					if (doExit(placeHolders)) {
						return;
					}
					placeHolders.put(CLASSES_SELECTED, fastCodeTypes);
				}
			}

			if (this.templatePrefix.equals(TEMPLATE) && this.templateSettings.getFirstTemplateItem() == FIRST_TEMPLATE.Folder) {
				placeHolders.put("folder", new FastCodeFolder(this.createSnippetData.getFolder()));

				if (this.templateSettings.getSecondTemplateItem() == SECOND_TEMPLATE.file) {
					final FastCodeFile[] fastCodeFiles = getFiles(placeHolders);
					if (doExit(placeHolders)) {
						return;
					}
					placeHolders.put(FILES_SELECTED, fastCodeFiles);
				}
			}

			if (this.templateSettings.getAdditionalParamaters() != null) {
				getAdditionalParameters(placeHolders, this.createSnippetData.getFromClass());
				if (doExit(placeHolders)) {
					return;
				}
			}

			if (isEmpty(this.createSnippetData.getSelectedText()) && this.templateType.equals(TEMPLATE_CREATE_FILE_WITH_SELECTED_CONTENT)) {
				final ITextSelection selections = (ITextSelection) this.editorPart.getEditorSite().getSelectionProvider().getSelection();
				final ITextEditor editor = (ITextEditor) this.editorPart.getAdapter(ITextEditor.class);
				final IDocumentProvider documentProvider = editor.getDocumentProvider();
				final IDocument document = documentProvider.getDocument(editor.getEditorInput());
				final String lines = (String) placeHolders.get(STARTLINE_ENDLINE);
				final String[] stringArray = lines.split(" ");
				final StringBuffer sb = new StringBuffer();
				for (int x = 0; x < stringArray.length; x++) {
					final int startLine = Integer.parseInt(stringArray[x].substring(0, stringArray[x].indexOf('-')));
					final int endLine = Integer.parseInt(stringArray[x].substring(stringArray[x].indexOf('-') + 1));
					int i = 0;

					if (document.getNumberOfLines() < startLine) {
						openError(new Shell(), "Error", " Line number " + startLine + " does not exists in the file");
						return;
					}
					if (document.getNumberOfLines() < endLine) {
						openError(new Shell(), "Error", " Line number " + endLine + " does not exists in the file");
						return;
					}
					for (i = startLine - 1; i <= endLine - 1; i++) {
						if (document.getNumberOfLines() < i) {
							final int j = i + 1;
							openError(new Shell(), "Error", " Line number " + j + " does not exists in the file");
							return;
						}
						final IRegion region = document.getLineInformation(i);
						final String lineContent = document.get(region.getOffset(), region.getLength());
						sb.append(lineContent);
						sb.append(System.getProperty("line.separator"));
						placeHolders.put(SELECTED_TEXT, sb.toString().trim());
					}
				}
				final String finalSnippet = sb.toString().trim();
				if (highlightSnippet()) {

					final ITextSelection highlightSelection = new ITextSelection() {

						@Override
						public boolean isEmpty() {
							return false;
						}

						@Override
						public String getText() {
							return null;
						}

						@Override
						public int getStartLine() {
							return selections.getStartLine();
						}

						@Override
						public int getOffset() {
							return selections.getOffset();
						}

						@Override
						public int getLength() {
							return finalSnippet.length();
						}

						@Override
						public int getEndLine() {
							return selections.getEndLine() + finalSnippet.split(NEWLINE).length;
						}
					};
					this.editorPart.getEditorSite().getSelectionProvider().setSelection(highlightSelection);
				}
			}

			placeHolders.put(TEMPLATE_TYPE, this.templateType);
			createSnippet(placeHolders, memberSelection, spacesBeforeCursor);

			if (!this.useLast) {
				templateCache.templateType = this.templateType;
				templateCache.lastType = type;
				templateCache.lastToType = toType;
				templateCache.lastEditorInput = this.editorPart.getEditorInput();
				templateCache.lastTemplateSettings = this.templateSettings;
				templateCache.createSnippetData = this.createSnippetData;
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			if (actionList != null) {
				actionList.clear();
			}
			throw new Exception("There was some problem - " + ex.getMessage());
		} finally {
			if (compilationUnit != null) {
				compilationUnit.commitWorkingCopy(false, null);
				compilationUnit.discardWorkingCopy();
			}
			ResourcesPlugin.getWorkspace().addResourceChangeListener(FastCodeResourceChangeListener.getInstance());
		}
		//final IProject project = this.createSnippetData.getSelectedProject().getProject();
		if (this.createSnippetData.getSelectedProject() != null) {
			refreshProject(this.createSnippetData.getSelectedProject().getName());
		}
	}

	/**
	 * @param placeHolders
	 * @return
	 * @throws CoreException
	 */
	private FastCodeFile[] getFiles(final Map<String, Object> placeHolders) throws CoreException {
		/*
		 * final String path = this.createSnippetData.getFolder().toString();
		 * final String project = this.createSnippetData.getFolder().segment(0);
		 * final String srcPath = path.substring(project.length()+1); final
		 * IFolder folder =
		 * this.createSnippetData.getJavaProject().getProject().getFolder(new
		 * Path(srcPath));
		 */
		final IResource[] iResources = this.createSnippetData.getFolder().members();
		final List<FastCodeFile> fcFileList = new ArrayList<FastCodeFile>();
		final List<FastCodeFile> fcFilesSelected = new ArrayList<FastCodeFile>();
		for (final IResource res : iResources) {
			if (res instanceof IFile) {
				fcFileList.add(new FastCodeFile((IFile) res));//new FastCodeFile(res.getName(), res.getProjectRelativePath().toString()));
			}
		}
		final FastCodeFileSelectionDialog fastCodeTypeSelectionDialog = new FastCodeFileSelectionDialog(new Shell(), EMPTY_STR, EMPTY_STR,
				fcFileList.toArray(new FastCodeFile[0]), 0, true);
		final int res = fastCodeTypeSelectionDialog.open();
		if (fastCodeTypeSelectionDialog.getResult() != null && fastCodeTypeSelectionDialog.getResult().length > 0) {

			for (final Object fastCodeTypeObj : fastCodeTypeSelectionDialog.getResult()) {
				// typeFromUser = new
				// IType(((FastCodeType)fastCodeTypeObj).get);
				fcFilesSelected.add((FastCodeFile) fastCodeTypeObj);

			}
			return fcFilesSelected.toArray(new FastCodeFile[0]);
		}
		placeHolders.put(EXIT_KEY, true);
		return null;
	}

	/**
	 * @param placeHolders
	 * @return
	 * @throws Exception
	 */
	private FastCodeType[] getClasses(final Map<String, Object> placeHolders) throws Exception {
		final ICompilationUnit compilationUnit[] = this.createSnippetData.getPackageFragment().getCompilationUnits();
		final List<FastCodeType> fcTypeList = new ArrayList<FastCodeType>();
		final List<FastCodeType> fcTypeSelected = new ArrayList<FastCodeType>();
		for (final ICompilationUnit compUnit : compilationUnit) {
			if (compUnit.findPrimaryType() != null) {
				// added by mangala, for the template create_impl to show only
				// interfaces
				if (this.templateType.equals(TEMPLATE_CREATE_IMPL)) {
					if (compUnit.findPrimaryType().isInterface()) {
						fcTypeList.add(new FastCodeType(compUnit.findPrimaryType()));
					}
					continue;

				}
				/*	if (this.templateSettings.getSecondTemplateItem().equals(SECOND_TEMPLATE.field) && compUnit.findPrimaryType().isInterface()) {
						continue;
					}*/
				fcTypeList.add(new FastCodeType(compUnit.findPrimaryType()));
			}
		}
		final FastCodeTypeSelectionDialog fastCodeTypeSelectionDialog = new FastCodeTypeSelectionDialog(new Shell(), "Select Classes",
				"Select Classes for " + this.description, fcTypeList.toArray(new FastCodeType[0]), 0, true);
		final int res = fastCodeTypeSelectionDialog.open();
		if (fastCodeTypeSelectionDialog.getResult() != null && fastCodeTypeSelectionDialog.getResult().length > 0) {

			for (final Object fastCodeTypeObj : fastCodeTypeSelectionDialog.getResult()) {
				// typeFromUser = new
				// IType(((FastCodeType)fastCodeTypeObj).get);
				fcTypeSelected.add((FastCodeType) fastCodeTypeObj);

			}
			return fcTypeSelected.toArray(new FastCodeType[0]);
		}
		placeHolders.put(EXIT_KEY, true);
		return null;
	}

	/**
	 * @param compilationUnit
	 * @param selectedPojoClassType
	 * @param placeHolders
	 */
	protected void addPojoClassImport(final ICompilationUnit compilationUnit, final IType selectedPojoClassType,
			final Map<String, Object> placeHolders) {

	}

	/**
	 * @param dbFieldSelection
	 * @param getiSelectPojoClassType
	 * @return
	 * @throws Exception
	 */
	protected Map<String, String> checkSetMethodInPojoClass(final Map<String, List<FastCodeDataBaseFieldDecorator>> dbFieldSelection,
			final IType getiSelectPojoClassType) throws Exception {
		return null;
	}

	protected Map<String, List<FastCodeDataBaseFieldDecorator>> getFieldsForJoin(final Map<String, Object> placeHolders) throws Exception {
		return null;
	}

	/**
	 * @param placeHolders
	 * @param classSelected
	 * @throws Exception
	 */
	private void getAdditionalParameters(final Map<String, Object> placeHolders, final IType classSelected) throws Exception {
		final String addtnlParams = this.templateSettings.getAdditionalParamaters();
		if (isEmpty(addtnlParams)) {
			return;
		}
		final FastCodeType type = (FastCodeType) placeHolders.get("class");
		final String retType = EMPTY_STR;
		final String name = EMPTY_STR;
		final boolean getLocalVar = false;
		//		final List<FastCodeAdditionalParams> fcAdditnlParamList = new ArrayList<FastCodeAdditionalParams>();
		final String additionalParamFinal = evaluateByVelocity(addtnlParams, placeHolders);
		final List<FastCodeAdditionalParams> fcAdditnlParamList = parseAdditonalParam(additionalParamFinal);

		/*for (final String params : addtnlParams) {
			if (params.contains(COLON)) {
				final String parseParam[] = params.split(COLON);
				name = parseParam[1].equalsIgnoreCase(RETURN_TYPES.BOOLEAN.getValue()) ? parseParam[0] : parseParam[0] + SPACE + LEFT_PAREN
						+ parseParam[1] + RIGHT_PAREN;
				retType = parseParam[1];
				if (retType.equals(RETURN_TYPES.LOCALVAR.getValue())) {
					getLocalVar = true;
				}
			} else {
				name = params;
				retType = "String";
			}

			final FastCodeAdditionalParams additionalParams = new FastCodeAdditionalParams(name, RETURN_TYPES.getReturnType(retType),
					EMPTY_STR, EMPTY_STR);
			fcAdditnlParamList.add(additionalParams);
		}*/
		ReturnValuesData returnValuesData = new ReturnValuesData();
		final IJavaProject project;
		returnValuesData.setEditorPart(this.editorPart);
		final ICompilationUnit compUnit = getCompilationUnitFromEditor();
		/*if (compUnit == null) {
			final IFile file = (IFile) this.editorPart.getEditorInput().getAdapter(IFile.class);
			project = JavaCore.create(file.getProject());
		} else {
			project = compUnit.getJavaProject();
		}*/
		returnValuesData.setCompUnit(compUnit);
		returnValuesData.setJavaProject(this.createSnippetData.getSelectedProject().getJavaProject());
		returnValuesData.setFastCodeAdditionalParams(fcAdditnlParamList.toArray(new FastCodeAdditionalParams[0]));

		if (getLocalVar) {
			returnValuesData.setLocalVars(getLocalVarFromCompUnit(compUnit, this.editorPart));
		}

		// returnValuesData.setLabelText();
		// returnValuesData.setValueTypes();
		// returnValuesData.setDefaultValue(createDefaultInstance(type.getName()));
		returnValuesData.setShellTitle("Additional Parameters");
		returnValuesData.setUnitTest(false);
		final ReturnValuesDialog returnValuesDialog = new ReturnValuesDialog(new Shell(), returnValuesData);
		if (returnValuesDialog.open() == Window.CANCEL) {
			placeHolders.put(EXIT_KEY, true);
			return;
		}
		returnValuesData = returnValuesDialog.getReturnValuesData();

		/*final InputDialog inputDialog = new InputDialog(this.editorPart.getSite().getShell(), addtnlParams[0],
				"Enter prefix For Resource Bundle" + type.getName(), createDefaultInstance(type.getName()), new IInputValidator() {
					public String isValid(final String newText) {
						if (isEmpty(newText)) {
							return "instance cannot be empty.";
						}
						return null;
					}
				});*/

		for (final FastCodeAdditionalParams addtnlParam : fcAdditnlParamList.toArray(new FastCodeAdditionalParams[0])) {
			/*if (addtnlParam.contains(COLON)) {
				final String[] classFileDet = addtnlParam.split(COLON);
				placeHolders.put(classFileDet[0], returnValuesData.getReturnValuesMap().get(classFileDet[0]));
			}*/
			placeHolders.put(addtnlParam.getName(), returnValuesData.getReturnValuesMap().get(addtnlParam.getName()));
		}

	}

	/**
	 * @param placeHolders
	 * @return
	 * @throws Exception
	 */
	protected Map<String, List<FastCodeDataBaseFieldDecorator>> getFields(final Map<String, Object> placeHolders) throws Exception {
		return null;
	}

	/**
	 *
	 * @param placeHolders
	 * @param type
	 * @param toType
	 * @return
	 * @throws Exception
	 */
	protected Map<String, List<FastCodeField>> getFields(final Map<String, Object> placeHolders, final IType type, final IType toType)
			throws Exception {
		final Map<String, List<FastCodeField>> fieldSelection = new HashMap<String, List<FastCodeField>>();
		final FastCodeField[] fields = type.isEnum() ? getFastCodeFieldsFromEnum(type) : getRelevantFields(type);
		final String fldNamesProp = this.templateType + UNDERSCORE + "FIELD_NAMES";
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String fieldNames = globalSettings.getPropertyValue(fldNamesProp, "fields");

		for (final String fieldName : fieldNames.split(COMMA)) {
			final Map<String, List<FastCodeField>> fieldSelectionMap = getFieldSelection(type, toType, fields, placeHolders,
					fieldName.trim());
			if (fieldSelectionMap != null) {
				fieldSelection.putAll(fieldSelectionMap);
			}
		}
		return fieldSelection;
	}

	/**
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private FastCodeField[] getFastCodeFieldsFromEnum(final IType type) throws Exception {
		final IField[] fields = SourceUtil.getFieldsOfType(type);
		final List<FastCodeField> retFields = new ArrayList<FastCodeField>();

		if (fields == null || fields.length == 0) {
			return null;
		} else {
			for (final IField field : fields) {
				if (field.isEnumConstant()) {
					final FastCodeField fcField = new FastCodeField(field);
					retFields.add(fcField);
				}
			}
			return retFields.toArray(new FastCodeField[0]);
		}
	}

	/**
	 *
	 * @param placeHolders
	 * @return
	 */
	private boolean doExit(final Map<String, Object> placeHolders) {
		return (Boolean) (placeHolders.containsKey(EXIT_KEY) ? placeHolders.get(EXIT_KEY) : false);
	}

	/**
	 * @param shell
	 * @param placeHolders
	 */
	// not in use
	private void getFilesSelect(final Shell shell, final Map<String, Object> placeHolders) {
		OpenResourceDialog resourceDialog = null;
		FastCodeFileSelectionDialog fastCodeFileSelectionDialog = null;
		final List<FastCodeFile> fastCodeFiles = new ArrayList<FastCodeFile>();
		int res = 0;

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (!fastCodeCache.fileSet.isEmpty()) {
			fastCodeFileSelectionDialog = new FastCodeFileSelectionDialog(shell, "Open Files", "Select an item to open",
					fastCodeCache.fileSet.toArray(new FastCodeFile[0]), IResource.FILE, true);
			res = fastCodeFileSelectionDialog.open();
			if (res != CANCEL && fastCodeFileSelectionDialog.getResult() != null && fastCodeFileSelectionDialog.getResult().length > 0) {

				for (final Object fastCodeFile : fastCodeFileSelectionDialog.getResult()) {
					fastCodeFiles.add((FastCodeFile) fastCodeFile);
				}
			}
		}

		if (fastCodeCache.fileSet.isEmpty() || res == CANCEL) {
			resourceDialog = new OpenResourceDialog(shell, ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
			res = resourceDialog.open();
			if (res == CANCEL || resourceDialog.getResult() == null || resourceDialog.getResult().length == 0) {
				return;
			}
			final Object[] result = resourceDialog.getResult();

			for (final Object selFile : result) {
				if (selFile instanceof IFile) {
					final IFile file = (IFile) selFile;
					// final IContainer parent = file.getParent();//get flder
					// from file, and frm that get path
					// final IPackageFragment packageFragment =
					// (IPackageFragment)
					// parent.getAdapter(IPackageFragment.class);
					//final String path = file.getProjectRelativePath().toString();
					/*
					 * if (packageFragment != null && packageFragment.exists())
					 * { path = packageFragment.getPath().toString(); final
					 * IPackageFragmentRoot packageFragmentRoot =
					 * (IPackageFragmentRoot) packageFragment.getParent(); if
					 * (packageFragmentRoot != null &&
					 * packageFragmentRoot.exists()) { path =
					 * path.substring(packageFragmentRoot
					 * .getPath().toString().length()); } }
					 */
					final FastCodeFile fastCodeFile = new FastCodeFile(file);//new FastCodeFile(file.getName(), path);
					fastCodeFiles.add(fastCodeFile);
					if (!fastCodeCache.fileSet.contains(file)) {
						fastCodeCache.fileSet.add(file);
					}
				}
			}

		}
		if (fastCodeFiles.size() == 1) {
			placeHolders.put("file", fastCodeFiles.get(0));
		}
		placeHolders.put("files", fastCodeFiles);
	}

	/**
	 *
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private FastCodeField[] getRelevantFields(final IType type) throws Exception {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final IField[] fields = SourceUtil.getFieldsOfType(type);
		final String excludeFields = globalSettings.getPropertyValue("exclude.fields.from.snippets", EMPTY_STR);
		final boolean showChildFields = getBooleanPropertyValue(this.templateType + UNDERSCORE + "SHOW_CHILD_FIELDS", "true");

		final List<FastCodeField> retFields = new ArrayList<FastCodeField>();

		if (fields == null || fields.length == 0) {
			if (isEmpty(excludeFields)) {
				return null; // fields;
			}
		} else {
			for (final IField field : fields) {
				final FastCodeField fcField = new FastCodeField(field);
				if (checkGetterSetter(field)) {
					retFields.add(fcField);
					if (showChildFields) {
						getChildrenOfField(fcField, retFields);
					}
				}
			}
			return retFields.toArray(new FastCodeField[0]);
		}

		final String[] excludeFieldsArr = excludeFields.split(COMMA);
		for (int i = 0; i < excludeFieldsArr.length; i++) {
			excludeFieldsArr[i] = excludeFieldsArr[i].trim();
		}
		final String fileName = this.editorPart == null ? EMPTY_STR : this.editorPart.getEditorInput().getName();
		final int pos = fileName.lastIndexOf(DOT);
		String fileExtn = EMPTY_STR;
		if (pos > -1) {
			fileExtn = fileName.substring(pos + 1);
		}
		final String excludeFiles = globalSettings.getPropertyValue("exclude.fields.file.extensions", EMPTY_STR);
		final String[] excludeFilesArr = isEmpty(excludeFiles) ? new String[] {} : excludeFiles.split(COMMA);
		for (int i = 0; i < excludeFilesArr.length; i++) {
			excludeFilesArr[i] = excludeFilesArr[i].trim();
		}
		for (final IField field : fields) {
			if (!isStringInArray(fileExtn, excludeFilesArr)) {
				if (!isStringInArray(field.getElementName(), excludeFieldsArr)) {
					if (checkGetterSetter(field)) {
						retFields.add(new FastCodeField(field));
					}
				}
			}
		}
		return retFields.toArray(new FastCodeField[0]);
	}

	/**
	 * @param fcField
	 * @param retFields
	 * @return
	 * @throws Exception
	 */
	private List<FastCodeField> getChildrenOfField(final FastCodeField fcField, final List<FastCodeField> retFields) throws Exception {

		final List<FastCodeField> fastCodeFieldsList = new ArrayList<FastCodeField>();
		final IType fieldIType = getTypeForField(fcField);
		boolean getchild = false;
		if (fieldIType != null) {
			final String fieldPack = fieldIType.getPackageFragment().getElementName();
			final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
			final String includePackages = preferenceStore.getString(P_EMBEDDED_FIELDS_INCLUDE_PACKAGE);
			final String inclPack[] = includePackages != null ? includePackages.split(NEWLINE) : null;
			final String excludePackages = preferenceStore.getString(P_EMBEDDED_FIELDS_EXCLUDE_PACKAGE);
			final String exclPack[] = excludePackages != null ? excludePackages.split(NEWLINE) : null;

			if (inclPack != null) {
				for (final String incpk : inclPack) {
					if (fieldPack.startsWith(incpk.trim().contains(DOT) ? incpk.trim() : incpk.trim() + DOT)) {
						getchild = true;
						break;
					}
				}
			}

			if (exclPack != null) {
				for (final String excpk : exclPack) {
					if (fieldPack.startsWith(excpk.trim().contains(DOT) ? excpk.trim() : excpk.trim() + DOT)) {
						getchild = false;
						break;
					}
				}
			}

		}

		if (getchild) {
			if (inputHasChildren(fcField)) {
				final Object[] children = getChildrenForInput(fcField);
				if (children != null) {
					for (final Object child : children) {
						final FastCodeField childFCField = (FastCodeField) child;
						if (checkGetterSetter(childFCField.getField())) {
							retFields.add(childFCField);
							if (inputHasChildren(childFCField)) {
								getChildrenOfField(childFCField, retFields);
							}
						}
					}
				}
			}
		}

		return fastCodeFieldsList;
	}

	/**
	 * @param field
	 * @return
	 * @throws Exception
	 */
	private boolean checkGetterSetter(final IField field) throws Exception {
		final GETTER_SETTER getterSetterExist = doesGetterSetterExist(field);
		final GETTER_SETTER getterSetterRequired = this.templateSettings.getGetterSetterRequired();

		if (!(getterSetterExist == GETTER_SETTER.GETTER_SETTER_EXIST || getterSetterRequired == GETTER_SETTER.NONE || getterSetterExist == getterSetterRequired)) {
			final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
			if (preferenceStore.getBoolean(P_GLOBAL_STRICTLY_MAINTAIN_GETTER_SETTER)) {
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 * @param placeHolders
	 * @param templateSetting
	 * @return
	 */
	protected boolean requireClasses(final Map<String, Object> placeHolders, final TemplateSettings templateSetting) {
		return templateSetting.getFirstTemplateItem() == FIRST_TEMPLATE.Class;
	}

	/**
	 *
	 * @param placeHolders
	 * @param templateSetting
	 *
	 * @return
	 */
	protected boolean requireFields(final Map<String, Object> placeHolders, final TemplateSettings templateSetting) {
		if (this.templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX)) {

			final String templateVariationField = getTemplateVariationField();
			final Object templateVar = placeHolders.get(templateVariationField);
			if (templateVar != null) {
				if (templateVar instanceof String) {
					final String templateVariation = (String) templateVar;
					return getBooleanPropertyValue(this.templateType + DOT + templateVariation + UNDERSCORE + "REQUIRE_FIELD", "true");
				} else if (templateVar instanceof List) {
					return true;
				}
			} else {
				return getBooleanPropertyValue(this.templateType + UNDERSCORE + "REQUIRE_FIELD", "true");
			}
		}
		if (templateSetting.getSecondTemplateItem() == SECOND_TEMPLATE.field
				|| templateSetting.getSecondTemplateItem() == SECOND_TEMPLATE.both) {
			return true;
		}
		if (templateSetting.getSecondTemplateItem() == SECOND_TEMPLATE.custom) {
			final boolean requireField = getBooleanPropertyValue(this.templateType + UNDERSCORE + "REQUIRE_FIELD", "true");
			if (!requireField) {
				return requireField;
			}
			final String templateVariationField = getTemplateVariationField();
			final Object templateVar = placeHolders.get(templateVariationField);
			if (templateVar != null) {
				if (templateVar instanceof String) {
					final String templateVariation = (String) templateVar;
					return getBooleanPropertyValue(this.templateType + DOT + templateVariation + UNDERSCORE + "REQUIRE_FIELD", "true");
				} else if (templateVar instanceof List) {
					return true;
				}
			} else {
				return requireField;
			}
		}
		return false;
	}

	/**
	 *
	 * @param placeHolders
	 * @param templateSetting
	 *
	 * @return
	 */
	protected boolean requireMethods(final Map<String, Object> placeHolders, final TemplateSettings templateSetting) {
		if (templateSetting.getSecondTemplateItem() == SECOND_TEMPLATE.method
				|| templateSetting.getSecondTemplateItem() == SECOND_TEMPLATE.both) {
			return true;
		}
		if (templateSetting.getSecondTemplateItem() == SECOND_TEMPLATE.custom) {
			final boolean value = getBooleanPropertyValue(this.templateType + UNDERSCORE + "REQUIRE_METHOD", "false");
			if (value) {
				return value;
			}
			final String templateVariationField = getTemplateVariationField();
			final Object templateVar = placeHolders.get(templateVariationField);

			if (templateVar instanceof String) {
				final String templateVariation = (String) templateVar;
				return getBooleanPropertyValue(this.templateType + DOT + templateVariation + UNDERSCORE + "REQUIRE_METHOD", "false");
			} else if (templateVar instanceof List) {
				return false;
			}
		}
		return false;
	}

	/**
	 *
	 * @param placeHolders
	 * @param templateSetting
	 * @return
	 */
	protected boolean requireFiles(final Map<String, Object> placeHolders, final TemplateSettings templateSetting) {
		return templateSetting.getFirstTemplateItem() == FIRST_TEMPLATE.File;
	}

	/**
	 *
	 * @return
	 */
	protected SnippetType getSnippetType() {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String propertyValue = globalSettings.getPropertyValue(this.templateType + UNDERSCORE + "SNIPPET_TYPE", null);
		return SnippetType.getType(propertyValue);
	}

	/**
	 *
	 * @param templateType
	 * @param property
	 * @param defaultValue
	 * @return
	 */
	private boolean getBooleanPropertyValue(final String property, final String defaultValue) {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String value = globalSettings.getPropertyValue(property, defaultValue);
		return value.equals("true");
	}

	/**
	 *
	 * @param type
	 * @param placeHolders
	 * @throws Exception
	 */
	protected void addDefaultClassToPlaceHolders(final IType type, final Map<String, Object> placeHolders) throws Exception {
		if (type != null) {
			placeHolders.put(DEFAULT_CLASS_NAME_STR, new FastCodeType(type));
			placeHolders.put(FULL_CLASS_NAME_STR, type.getFullyQualifiedName());
			placeHolders.put(PACKAGE_NAME_STR, type.getPackageFragment().getElementName());
			final String instanceName = this.createSnippetData.getInstanceName();
			if (instanceName != null) {
				placeHolders.put(INSTANCE_STR, instanceName);
			}
		}
	}

	/**
	 *
	 * @param templateSettings
	 * @param placeHolders
	 * @throws Exception
	 */
	protected void initializePlaceHolders(final TemplateSettings templateSettings, final Map<String, Object> placeHolders) throws Exception {

		final String[] allTmpltVariations = this.templateSettings.getTemplateVariations();

		if (allTmpltVariations != null && allTmpltVariations.length > 0) {
			final String templateVariationField = getTemplateVariationField();
			if (this.createSnippetData.getVariationsSelected() == null) {
				placeHolders.put("_exit", true);
				return;
			}
			placeHolders.put(templateVariationField,
					this.templateSettings.isAllowMultipleVariation() ? Arrays.asList(this.createSnippetData.getVariationsSelected())
							: this.createSnippetData.getVariationsSelected()[0]);
		}
	}

	/**
	 *
	 * @param templateSettings
	 * @param placeHolders
	 * @param resourceFile
	 * @throws Exception
	 */
	protected void getFileElements(final TemplateSettings templateSettings, final Map<String, Object> placeHolders, final IFile resourceFile)
			throws Exception {
		final Shell shell = this.editorPart == null || this.editorPart.getSite() == null ? new Shell() : this.editorPart.getSite()
				.getShell();
		/*
		 * final OpenResourceDialog resourceDialog = new
		 * OpenResourceDialog(shell, ResourcesPlugin.getWorkspace().getRoot(),
		 * IResource.FILE); final int res = resourceDialog.open(); if (res ==
		 * CANCEL || resourceDialog.getResult() == null ||
		 * resourceDialog.getResult().length == 0) { return; }
		 *
		 * if (!(resourceDialog.getFirstResult() instanceof IFile)) { return; }
		 *
		 * final IFile resourceFile = (IFile) resourceDialog.getFirstResult();
		 */
		final InputStream contents = resourceFile.getContents();
		final Object[] fastCodeFileElements;

		final FileLoadingStrategy fileLoadingStrategy = getFileLoadingStrategy(resourceFile.getName());

		if (fileLoadingStrategy == null) {
			//			throw new Exception("Unable to get file type to load file");
			return;
		}
		try {
			fastCodeFileElements = fileLoadingStrategy.loadFileElementsFromInputStream(contents, null);
		} finally {
			closeInputStream(contents);
		}

		if (fastCodeFileElements == null) {
			MessageDialog.openWarning(shell, "Warning", "The file selected is empty . Exiting....");
			placeHolders.put("_exit", true);
			return;
		}

		final FastCodePropertySelectionDialog propertySelectionDialog = new FastCodePropertySelectionDialog(shell, EMPTY_STR, EMPTY_STR,
				fastCodeFileElements);
		final int retCode = propertySelectionDialog.open();
		if (retCode == CANCEL) {
			placeHolders.put("_exit", true);
			return;
		}

		final Object fastCodeFileElement = propertySelectionDialog.getFirstResult();
		placeHolders.put("property", fastCodeFileElement);
	}

	/**
	 *
	 * @param string
	 * @return
	 */
	private FileLoadingStrategy getFileLoadingStrategy(final String fileName) {

		if (fileName.endsWith(DOT + FILE_TYPE_PROPERTY)) {
			return new PropertiesFileLoadingStrategy();
		} else if (fileName.endsWith(DOT + FILE_TYPE_XML)) {
			return new XMLFileLoadingStrategy();
		}
		return null;
	}

	/**
	 * @param fastCodeFields
	 * @param type
	 * @param toType
	 * @param fieldsMap
	 * @param fields
	 * @throws Exception
	 */
	protected Map<String, List<FastCodeField>> getFieldSelection(final IType type, final IType toType, final FastCodeField[] fields,
			final Map<String, Object> placeHolders, final String fieldType) throws Exception {

		final List<FastCodeField> fastCodeFields = new ArrayList<FastCodeField>();
		final Map<String, List<FastCodeField>> fieldsMap = new HashMap<String, List<FastCodeField>>();

		final Shell shell = this.editorPart == null || this.editorPart.getSite() == null ? new Shell() : this.editorPart.getSite()
				.getShell();
		// if (doShowEmbeddedFields(this.templateSettings, this.templateType)) {
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final String showView = preferenceStore.getString(P_EMBEDDED_FIELDS_VIEW);
		final boolean showChildFields = getBooleanPropertyValue(this.templateType + UNDERSCORE + "SHOW_CHILD_FIELDS", "true");
		if (showChildFields && showView != null && showView.equals(EMBEDDED_FIELDS_VIEW.HIERARCHICAL_VIEW.getValue())) {
			final CheckedTreeSelectionDialog checkedTreeSelectionDialog = new CheckedTreeSelectionDialog(shell, new LabelProvider(),
					new TypeContentProvider());
			checkedTreeSelectionDialog.setTitle(makeWord(fieldType));
			checkedTreeSelectionDialog.setMessage("Select the " + makeWord(fieldType) + " from " + type.getFullyQualifiedName() + " for "
					+ this.description);
			checkedTreeSelectionDialog.setInput(type);

			final int result = checkedTreeSelectionDialog.open();
			if (result == CANCEL) {
				if (fieldType.equals("where_fields") && this.templateType.contains(TEMPLATE + UNDERSCORE + "HQL" + UNDERSCORE + "SELECT")) {
					fieldsMap.put(fieldType, fastCodeFields);
					return fieldsMap;
				} else {
					return null;
				}
			}
			if (checkedTreeSelectionDialog.getResult() != null) {
				for (final Object selection : checkedTreeSelectionDialog.getResult()) {
					fastCodeFields.add((FastCodeField) selection);
				}
			}
		} else {
			boolean multipleSelection = !isSingleSelection();
			if (this.templateType.equals(TEMPLATE_PRINT_FIELDS_OF_CLASS_WEB_SINGLE)) {
				multipleSelection = false;
			}
			final FastCodeField[] selectedMembers = getSelectedMembers(type, FIELD, fields, this.description, multipleSelection, fieldType);

			if ((selectedMembers == null || selectedMembers.length == 0) && !doContinueWithNoFields()) {
				return null;
			}

			if (selectedMembers != null && selectedMembers.length > 0) {
				for (final FastCodeField field : fields) {

					boolean found = false;
					for (final FastCodeField selField : selectedMembers) {
						if (field.equals(selField)) {
							found = true;
							break;
						}
					}
					if (!found) {
						continue;
					}

					// checkGetterSetter(field);
					final GETTER_SETTER getterSetterExist = doesGetterSetterExist(field.getField());
					final GETTER_SETTER getterSetterRequired = this.templateSettings.getGetterSetterRequired();

					if (!(getterSetterExist == GETTER_SETTER.GETTER_SETTER_EXIST || getterSetterRequired == GETTER_SETTER.NONE || getterSetterExist == getterSetterRequired)) {
						if (openQuestion(shell, "Warning", "Field " + field.getName() + " does not have required getter/setter, "
								+ "do you want to skip it?")) {
							continue;
						}
					}
					if (toType != null) {
						final IField toField = toType.getField(field.getName());
						if (toField == null || !toField.exists()) {
							openWarning(shell, "Warning", "Field " + field.getName() + " does not exist in the to class, "
									+ "it will be skipped.");
							continue;
						}
						final GETTER_SETTER getterSetterToExist = doesGetterSetterExist(toField);
						final GETTER_SETTER getterSetterToRequired = doesGetterSetterRequiredForToClass();
						if (!(getterSetterToExist == GETTER_SETTER.GETTER_SETTER_EXIST || getterSetterToRequired == GETTER_SETTER.NONE || getterSetterToExist == getterSetterToRequired)) {
							continue;
						}
					}
					fastCodeFields.add(field);
				}
			}
		}
		final String dataStructureProp = this.templateType + UNDERSCORE + "DATASTRUCTURE";
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String dataStructure = globalSettings.getPropertyValue(dataStructureProp, "flat");
		if (dataStructure.equals("hierarchical")) {
			populateChildFields(fastCodeFields);
		}
		fieldsMap.put(fieldType, fastCodeFields);
		return fieldsMap;
	}

	/**
	 *
	 * @param selection
	 * @param source
	 * @return
	 */
	private String getSpacesBeforeCursor(final ITextSelection selection, final String source) {
		final int beginOfCurrentLine = source.lastIndexOf(NEWLINE, selection.getOffset());
		final int startLine = selection.getStartLine();
		int i = 0;
		String currLine = EMPTY_STR;
		for (final String line : source.split(NEWLINE)) {
			if (i++ == startLine) {
				currLine = line;
				break;
			}
		}

		// System.out.println("beginOfCurrentLine " + beginOfCurrentLine);
		// System.out.println("selection.getOffset() " + selection.getOffset());
		if (source.length() < beginOfCurrentLine || source.length() < selection.getOffset()) { // this
																								// sounds
																								// crazy
																								// but
																								// kept
																								// for
																								// sanity
																								// check.
			return EMPTY_STR;
		}
		final String retStr = beginOfCurrentLine < selection.getOffset() - 1 ? source.substring(beginOfCurrentLine, selection.getOffset())
				: currLine;

		return retStr.replace(NEWLINE, EMPTY_STR);
	}

	/**
	 *
	 * @param templateSettings
	 *            TODO
	 * @param templateType
	 * @return
	 */
	protected boolean isEmptyLineRequired(final TemplateSettings templateSettings, final String templateType) {
		final SnippetType snippetType = getSnippetType();
		if (snippetType != null && snippetType != SnippetType.SNIPPET) {
			return false;
		}
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		return globalSettings.getPropertyValue(templateType + "_EMPTY_LINE_REQUIRED", "true").equalsIgnoreCase("true");
	}

	/**
	 *
	 * @return
	 */
	protected boolean isSingleSelection() {
		return false;
	}

	/**
	 *
	 * @param templateType2
	 * @param templateSettings2
	 * @return
	 */
	protected boolean doShowEmbeddedFields(final TemplateSettings templateSettings, final String templateType) {
		return false;
	}

	/**
	 *
	 * @return
	 */
	protected TemplateSettings getTemplateSettings(final String templateType) {
		if (isEmpty(templateType)) {
			return null;
		}
		return this.templateSettings = TemplateSettings.getTemplateSettings(templateType, this.templatePrefix);
	}

	/**
	 *
	 * @return
	 */
	protected String getTemplateType() {
		return this.templateType;
	}

	private void loadItemsFromFile(final Map<String, Object> placeHolders, final TemplateSettings templateSettings2,
			final FastCodeFile fastCodeFile) {

	}

	/**
	 *
	 * @param compilationUnit
	 * @param type
	 * @throws Exception
	 */
	protected void addImport(final ICompilationUnit compilationUnit, final IType type) throws Exception {
		if (type != null) {
			final IImportDeclaration imprt = compilationUnit.getImport(type.isEnum() ? type.getFullyQualifiedParameterizedName() : type
					.getFullyQualifiedName());
			final IType pType = compilationUnit.findPrimaryType();
			if (pType.equals(type)) {
				return;
			}
			if (imprt == null || !imprt.exists()) {
				compilationUnit.createImport(type.isEnum() ? type.getFullyQualifiedParameterizedName() : type.getFullyQualifiedName(),
						null, null);
			}
		}
	}

	/**
	 *
	 * @return
	 */
	protected String getTemplateVariationField() {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		if (FALSE_STR.equalsIgnoreCase(globalSettings.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD, TRUE_STR))) {
			// this.fromTemplateSetting = true;
			return this.templateSettings.getTemplateVariationField();
		} else {
			return globalSettings.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD_NAME, DEFAULT_TEMPLATE_VARIATION_FIELD_VALUE);
		}
	}

	/**
	 *
	 * @param snippet
	 * @param templateType
	 * @param placeHolders
	 * @param memberSelection
	 * @param spacesBeforeCursor
	 *
	 * @throws Exception
	 */
	protected void createSnippet(final Map<String, Object> placeHolders, final Map<String, Object> memberSelection,
			final String spacesBeforeCursor) throws Exception {
		final SnippetType snippetType = getSnippetType();
		final SnippetCreator snippetCreator = getSnippetCreator(snippetType);
		if (snippetCreator == null) {
			throw new Exception("Unknown Snippet Creator");
		}
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		if (this.createSnippetData.getTemplateBodyFromSnippetDialog() == null
				&& preferenceStore.getBoolean(P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES)) {
			this.createSnippetData
					.setTemplateBodyFromSnippetDialog(placeHolders.containsKey("ModifiedTemplateBody") ? (String) placeHolders
							.get("ModifiedTemplateBody") : this.templateSettings.getTemplateBody());
		} else if (!preferenceStore.getBoolean(P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES)) {
			this.createSnippetData.setTemplateBodyFromSnippetDialog(this.templateSettings.getTemplateBody());
		}
		if (this.templateType.endsWith(NAMED_QUERY_ANNOTATION_STR) || this.templateType.endsWith(HQL_NAMED_QUERY_WITH_ANNOTATION_STR)) {
			this.createSnippetData.setTemplateBodyFromSnippetDialog((String) placeHolders.get("ModifiedTemplateBody"));
		}
		final Object snippet = snippetCreator.createSnippet(this.editorPart, this.createSnippetData.getTemplateBodyFromSnippetDialog(),
				placeHolders, memberSelection, spacesBeforeCursor);
	}

	/**
	 *
	 * @param snippetType
	 * @return
	 */
	private SnippetCreator getSnippetCreator(final SnippetType snippetType) {
		switch (snippetType) {
		case SNIPPET:
			return new DefaultSnippetCreator();
		case METHOD:
			return new DefaultSnippetCreator();
		case FIELD:
			return new FieldSnippetCreator();
		case CLASS:
			return new ClassSnippetCreator();
		case POJO_METHOD:
			return new PojoMethodSnippetCreator();
		default:
			return null;
		}
	}

	/**
	 * @param snippet
	 *
	 * @throws Exception
	 */
	protected void createMethod(final String snippet) throws Exception {
		// System.out.println("snippet " + snippet);
		final ICompilationUnit compilationUnit = getCompilationUnitFromEditor();
		final IType type = compilationUnit.findPrimaryType();
		IMethod method;
		try {
			method = type.createMethod(snippet, null, false, null);
			if (method == null || !method.exists()) {
				throw new Exception("Unable to create method.");
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception("Unable to create mehtod, template may be wrong. " + ex.getMessage() + snippet, ex);
		}
		JavaUI.revealInEditor(this.editorPart, (IJavaElement) method);
	}

	/**
	 * @return
	 */
	protected ICompilationUnit getCompilationUnitFromEditor() {
		final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();
		final ICompilationUnit compilationUnit = manager.getWorkingCopy(this.editorPart.getEditorInput());
		return compilationUnit != null && compilationUnit.exists() ? compilationUnit : null;
	}

	/**
	 *
	 * @param action
	 * @param editorPart
	 */
	public void setActiveEditor(final IAction action, final IEditorPart editorPart) {
		this.editorPart = editorPart;
	}

	/**
	 *
	 * @param action
	 * @param selection
	 */
	public void selectionChanged(final IAction action, final ISelection selection) {

	}

	public void dispose() {

	}

	/**
	 *
	 * @param window
	 */
	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}

	/**
	 *
	 * @param type
	 * @return
	 * @throws Exception
	 */
	protected String getInstanceName(final IType type) throws Exception {
		return this.createSnippetData.getInstanceName();
	}

	/**
	 *
	 * @return
	 */
	protected boolean doContinueWithNoFields() {
		return true;
	}

	protected GETTER_SETTER doesGetterSetterRequiredForToClass() {
		return null;
	}

	/*
	 * protected Map<String, List<FastCodeDataBaseField>>
	 * getTableColumnsFromUser(final String fieldType) throws Exception { return
	 * null; }
	 */

	/**
	 * @author Gautam
	 *
	 */
	private class TypeContentProvider implements ITreeContentProvider {

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang
		 * .Object)
		 */

		@Override
		public Object[] getChildren(final Object input) {
			return getChildrenForInput(input);
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang
		 * .Object)
		 */

		@Override
		public Object getParent(final Object input) {
			return input instanceof IType ? null : ((FastCodeField) input).getParentField();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang
		 * .Object)
		 */

		@Override
		public boolean hasChildren(final Object input) {
			return inputHasChildren(input);
		}

		/**
		 *
		 * @param fastCodeField
		 * @return
		 */
		private IType getTypeForField(final FastCodeField fastCodeField) {
			final IField field = fastCodeField.getField();
			final IType declaringType = field.getDeclaringType();
			final ICompilationUnit compilationUnit = declaringType.getCompilationUnit();
			try {
				if (declaringType.isBinary() || declaringType.isInterface() || compilationUnit == null || !compilationUnit.exists()) {
					return null;
				}
				final FastCodeType parsedType = parseType(Signature.getSignatureSimpleName(field.getTypeSignature()),
						field.getCompilationUnit());
				// final IType fieldType = findTypeForImport(declaringType,
				// Signature.getSignatureSimpleName(field.getTypeSignature()),
				// declaringType);
				final IType fieldType = findTypeForImport(declaringType, parsedType.getName(), declaringType);
				if (fieldType == null || !fieldType.exists() || fieldType.isBinary() || fieldType.isInterface()) {
					return null;
				}
				final ICompilationUnit compilationUnitField = fieldType.getCompilationUnit();
				if (compilationUnitField == null || !compilationUnitField.exists()) {
					return null;
				}
				return fieldType;
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(
		 * java.lang.Object)
		 */

		@Override
		public Object[] getElements(final Object input) {
			return getChildren(input);
			/*
			 * final Object[] objects = getChildren(input); final Object[]
			 * elements = new Object[objects.length + 1]; elements[0] = input;
			 * int i = 1; for (final Object obj : objects) { elements[i++] =
			 * obj; } return elements;
			 */
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
		 */

		@Override
		public void dispose() {

		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse
		 * .jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */

		@Override
		public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) {

		}

	}

	/**
	 *
	 * @author Gautam
	 *
	 */
	private class LabelProvider implements ILabelProvider {

		private Image	image;

		@Override
		public Image getImage(final Object element) {
			try {
				if (element instanceof IType) {
					return null;
				} else if (element instanceof FastCodeField) {
					return getImageForField((FastCodeField) element);
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		public String getText(final Object input) {
			if (input instanceof IType) {
				return ((IType) input).getElementName();
			} else if (input instanceof FastCodeField) {
				return ((FastCodeField) input).getName();
			}
			return null;
		}

		/**
		 *
		 * @param field
		 * @return
		 * @throws Exception
		 */
		/*
		private Image getImageForField(final IField field) throws Exception {
		String imageName = null;

		if (isPrivate(field.getFlags())) {
			imageName = ISharedImages.IMG_FIELD_PRIVATE;
		} else if (isProtected(field.getFlags())) {
			imageName = ISharedImages.IMG_FIELD_PROTECTED;
		} else if (isPublic(field.getFlags())) {
			imageName = ISharedImages.IMG_FIELD_PUBLIC;
		}

		return getImage(imageName);
		}*/
		/**
		 *
		 * @param field
		 * @return
		 * @throws Exception
		 */
		private Image getImageForField(final FastCodeField fastCodeField) throws Exception {
			String imageName = null;
			final GlobalSettings globalSettings = GlobalSettings.getInstance();
			Image image = null;
			final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
			if (isPrivate(fastCodeField.getField().getFlags())) {
				if (fastCodeCache.getEntityImageMap().containsKey("field_private")) {
					return getImagefromFCCacheMap("field_private");
				}
				imageName = ISharedImages.IMG_FIELD_PRIVATE;
				image = getImage(imageName);//"int_obj.gif";
				populateFCCacheEntityImageMap("field_private", image);
			} else if (isProtected(fastCodeField.getField().getFlags())) {
				if (fastCodeCache.getEntityImageMap().containsKey("field_protected")) {
					return getImagefromFCCacheMap("field_protected");
				}
				imageName = ISharedImages.IMG_FIELD_PROTECTED;
				image = getImage(imageName);//"int_obj.gif";
				populateFCCacheEntityImageMap("field_protected", image);
			} else if (isPublic(fastCodeField.getField().getFlags())) {
				if (fastCodeCache.getEntityImageMap().containsKey("field_public")) {
					return getImagefromFCCacheMap("field_public");
				}
				imageName = ISharedImages.IMG_FIELD_PUBLIC;
				image = getImage(imageName);//"int_obj.gif";
				populateFCCacheEntityImageMap("field_public", image);
			} else if (fastCodeField.getField().isEnumConstant()) {
				if (fastCodeCache.getEntityImageMap().containsKey("field_enum")) {
					return getImagefromFCCacheMap("field_enum");
				}
				imageName = globalSettings.getPropertyValue(PLACEHOLDER_ENUM.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
				image = getImage(imageName);//"int_obj.gif";
				populateFCCacheEntityImageMap("field_enum", image);
			} else {
				if (fastCodeCache.getEntityImageMap().containsKey("field")) {
					return getImagefromFCCacheMap("field");
				}
				imageName = ISharedImages.IMG_FIELD_DEFAULT;
				image = getImage(imageName);//"int_obj.gif";
				populateFCCacheEntityImageMap("field", image);
			}
			return image;
		}

		/**
		 * Gets the image.
		 *
		 * @param imageName
		 *            the image name
		 * @return the image
		 */
		private Image getImage(String imageName) {
			URL url = null;
			if (imageName == null) {
				return null;
			}
			final Image image = PlatformUI.getWorkbench().getSharedImages().getImage(imageName);
			if (image != null && !image.isDisposed()) {
				// this.image = null;
				return image;
			}
			try {
				if (imageName.startsWith("org.eclipse.jdt.ui.")) {
					imageName = imageName.substring("org.eclipse.jdt.ui.".length());
				}
				url = new URL(Activator.getDefault().getDescriptor().getInstallURL(), "icons/" + imageName);
			} catch (final MalformedURLException ex) {
				ex.printStackTrace();
				return null;
			}
			final ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
			this.image = descriptor.createImage();
			return this.image;
		}

		@Override
		public void addListener(final ILabelProviderListener arg0) {
		}

		@Override
		public void dispose() {
			/*if (this.image != null && !this.image.isDisposed()) {
				this.image.dispose();
			}*/
		}

		@Override
		public boolean isLabelProperty(final Object arg0, final String arg1) {
			return false;
		}

		@Override
		public void removeListener(final ILabelProviderListener arg0) {

		}

	}

	/*
	 * public String getTableFromUser() throws Exception { return null; }
	 */

	/**
	 * @param placeHolders
	 * @param templateSetting
	 * @return
	 */
	protected boolean requireImport(final Map<String, Object> placeHolders, final TemplateSettings templateSetting) {
		final boolean requireField = getBooleanPropertyValue(this.templateType + UNDERSCORE + "REQUIRE_IMPORTS", "true");
		if (!requireField) {
			return requireField;
		}
		final String templateVariationField = getTemplateVariationField();
		final String templateVariation = (String) placeHolders.get(templateVariationField);
		return getBooleanPropertyValue(this.templateType + DOT + templateVariation + UNDERSCORE + "REQUIRE_IMPORTS", "true");
	}

	/**
	 * @return
	 */
	protected boolean requireAnnotation() {
		return false;
	}

	/**
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	protected CreateSnippetData getCreateSnippetData(final String fileName) throws Exception {
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
		return null;
	}

	/**
	 *
	 * @param templateSettings
	 * @param fileName
	 * @return
	 */
	protected boolean validateTemplateSetting(final TemplateSettings templateSettings, final String fileName) {
		if (this.templateSettings == null) {
			openError(new Shell(), "Error", "Template was not found.");
			return false;
		}
		if (!this.templateSettings.isEnabled()) {
			openError(new Shell(), "Error", "This snippet has been disabled. " + "You can enable by going to Windows -> Preferences -> "
					+ "Fast Code Preference -> Template Preference");
			return false;
		}

		final String[] allowedNames = this.templateSettings.getAllowedFileNames();

		if (allowedNames != null && allowedNames.length > 0 && !TemplateSettings.checkFilePattern(fileName, allowedNames)) {
			String allAllowedFileNames = EMPTY_STR;
			for (final String allowedName : allowedNames) {
				allAllowedFileNames += !allAllowedFileNames.equals(EMPTY_STR) ? FORWARD_SLASH + allowedName : allowedName;
			}
			openError(new Shell(), "Error", "This cannot be done in current editor. This can be done only in " + allAllowedFileNames
					+ " files.");
			return false;
		}
		return true;

	}

	/**
	 * @param input
	 * @return
	 */
	private Object[] getChildrenForInput(final Object input) {
		if (!(input instanceof IType || input instanceof FastCodeField)) {
			return null;
		}
		IType type = null;
		IField parField = null;
		FastCodeField parentField = null;
		final List<FastCodeField> fastCodeFields = new ArrayList<FastCodeField>();
		try {
			if (input instanceof IType) {
				type = (IType) input;
			} else if (input instanceof FastCodeField) {
				parField = ((FastCodeField) input).getField();
				// parentField = new FastCodeField(parField,
				// parField.getElementName(), null, ((FastCodeField)
				// input).getParentField());
				parentField = (FastCodeField) input;
				type = getTypeForField((FastCodeField) input);
			}
			final IField[] fields = type == null ? new IField[0] : type.getFields();
			for (final IField fld : fields) {
				if (!Flags.isStatic(fld.getFlags())) {
					final FastCodeField fastCodeField = new FastCodeField(fld, fld.getElementName(), null, parentField);
					fastCodeFields.add(fastCodeField);
				}
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return fastCodeFields.toArray(new FastCodeField[0]);
	}

	/**
	 *
	 * @param fastCodeField
	 * @return
	 */
	private IType getTypeForField(final FastCodeField fastCodeField) {
		final IField field = fastCodeField.getField();
		final IType declaringType = field.getDeclaringType();
		final ICompilationUnit compilationUnit = declaringType.getCompilationUnit();

		try {
			if (declaringType.isBinary() || declaringType.isInterface() || compilationUnit == null || !compilationUnit.exists()) {
				return null;
			}
			final FastCodeType parsedType = parseType(Signature.getSignatureSimpleName(field.getTypeSignature()),
					field.getCompilationUnit());
			// final IType fieldType = findTypeForImport(declaringType,
			// Signature.getSignatureSimpleName(field.getTypeSignature()),
			// declaringType);
			final IType fieldType = findTypeForImport(declaringType, parsedType.getName(), declaringType);
			if (fieldType == null || !fieldType.exists() || fieldType.isBinary() || fieldType.isInterface()) {
				return null;
			}
			/*
			 * final ICompilationUnit compilationUnitField =
			 * fieldType.getCompilationUnit(); if (compilationUnitField == null
			 * || !compilationUnitField.exists()) { return null; }
			 */
			final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
			final String includePackages = preferenceStore.getString(P_EMBEDDED_FIELDS_INCLUDE_PACKAGE);
			final String excludePackages = preferenceStore.getString(P_EMBEDDED_FIELDS_EXCLUDE_PACKAGE);
			final String inclPack[] = includePackages != null ? includePackages.split(NEWLINE) : new String[] {};
			final String exclPack[] = excludePackages != null ? excludePackages.split(NEWLINE) : new String[] {};
			final String fieldPack = fieldType.getPackageFragment().getElementName();
			boolean getchild = false;

			for (final String incpk : inclPack) {
				if (fieldPack.startsWith(incpk.trim().contains(DOT) ? incpk.trim() : incpk.trim() + DOT)) {
					getchild = true;
					break;
				}
			}
			for (final String excpk : exclPack) {
				if (fieldPack.startsWith(excpk.trim().contains(DOT) ? excpk.trim() : excpk.trim() + DOT)) {
					getchild = false;
					break;
				}
			}

			if (!getchild) {
				return null;
			}
			return fieldType;
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */

	/**
	 * @param input
	 * @return
	 */
	public boolean inputHasChildren(final Object input) {
		if (!(input instanceof IType || input instanceof FastCodeField)) {
			return false;
		}
		try {
			if (input instanceof IType) {
				return ((IType) input).getFields().length > 0;
			}
			final IType fieldType = getTypeForField((FastCodeField) input);
			return fieldType != null && fieldType.getFields() != null && fieldType.getFields().length > 0;
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 *
	 * @param fields
	 */
	private void populateChildFields(final List<FastCodeField> fields) {
		final List<FastCodeField> finalFields = new ArrayList<FastCodeField>();
		for (final FastCodeField field : fields) {
			final FastCodeField parentField = field.getParentField();
			List<FastCodeField> fieldList = parentField != null ? parentField.getChildFields() : finalFields;

			if (!fieldList.contains(field)) {
				fieldList.add(field);
			}

			FastCodeField pField = field;

			while (pField.getParentField() != null) {
				fieldList = pField.getParentField().getParentField() != null ? pField.getParentField().getParentField().getChildFields()
						: finalFields;

				if (!fieldList.contains(pField.getParentField())) {
					fieldList.add(pField.getParentField());
				}
				pField = pField.getParentField();
			}

		}
		fields.clear();
		fields.addAll(finalFields);

		return;

	}

	/**
	 * @param compilationUnit
	 * @return
	 * @throws Exception
	 */
	protected List<FastCodeReturn> selectLocalVariables(final ICompilationUnit compilationUnit) throws Exception {

		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;
		FastCodeReturn[] membersToWorkOn = {};
		final List<FastCodeReturn> variablesList = this.createSnippetData.getLocalVariables();
		membersToWorkOn = variablesList.toArray(membersToWorkOn);
		final List<FastCodeReturn> selectedFields = new ArrayList<FastCodeReturn>();
		FastCodeSelectionDialog selectionDialog = null;
		if (membersToWorkOn.length > 0) {
			selectionDialog = new VariableSelectionDialog(shell, "Variable Selection", "Select one or more " + "variable(s) for "
					+ this.description, membersToWorkOn, true);
			if (selectionDialog.open() == CANCEL) {
				return null;
			}

			for (final Object member : selectionDialog.getResult()) {
				selectedFields.add((FastCodeReturn) member);
			}

		}
		return selectedFields;
	}

	/**
	 * @param compilationUnit
	 * @param createSnippetData
	 * @throws Exception
	 */
	protected void getLocalVariables(final ICompilationUnit compilationUnit, final CreateSnippetData createSnippetData) throws Exception {
		createSnippetData.setLocalVariables(getLocalVarFromCompUnit(compilationUnit, this.editorPart));
	}

	/**
	 *
	 * @return
	 */
	private boolean highlightSnippet() {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		return globalSettings.getPropertyValue("TEMPLATE_HIGHLIGHT_SNIPPET", "true").equalsIgnoreCase("true");
	}
}

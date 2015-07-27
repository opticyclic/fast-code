/*
 * Fast Code Plugin for Eclipse
 *
 * Copyright (C) 2008  Gautam Dev
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA.
 *
 * Plugin Home Page: http://fast-code.sourceforge.net/
 */

package org.fastcode.popup.actions.easycreate;

import static org.eclipse.jdt.core.IJavaElement.FIELD;
import static org.eclipse.jdt.core.IJavaElement.METHOD;
import static org.eclipse.jdt.core.IJavaElement.TYPE;
import static org.eclipse.jdt.ui.JavaUI.createTypeDialog;
import static org.fastcode.common.FastCodeConstants.BUILDER_CLASS_NAME;
import static org.fastcode.common.FastCodeConstants.BUILDER_TYPE_VARIBLE;
import static org.fastcode.common.FastCodeConstants.CLASS_ANNOTATIONS_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_BODY_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_HEADER_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_IMPORTS_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_MODIFIER_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_NAME_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_TYPE_STR;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.EQUAL;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.FILE_SEPARATOR;
import static org.fastcode.common.FastCodeConstants.GLOBAL_ASK_TO_CREATE;
import static org.fastcode.common.FastCodeConstants.GLOBAL_NEVER_CREATE;
import static org.fastcode.common.FastCodeConstants.GROOVY_EXTENSION;
import static org.fastcode.common.FastCodeConstants.INITIATED;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN;
import static org.fastcode.common.FastCodeConstants.MESSAGE_DIALOG_RETURN_YES;
import static org.fastcode.common.FastCodeConstants.MODIFIER_PRIVATE;
import static org.fastcode.common.FastCodeConstants.MODIFIER_PUBLIC;
import static org.fastcode.common.FastCodeConstants.MY_NEW;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.PACKAGE_NAME_STR;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_CLASS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_FIELDS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_LOCALVARS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_MODIFY_FIELD;
import static org.fastcode.common.FastCodeConstants.RIGHT_CURL;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN;
import static org.fastcode.common.FastCodeConstants.SEMICOLON;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.STATIC;
import static org.fastcode.common.FastCodeConstants.SUPER;
import static org.fastcode.common.FastCodeConstants.TAB;
import static org.fastcode.preferences.PreferenceConstants.P_ASK_FOR_PARAMETERIZED_TYPE;
import static org.fastcode.preferences.PreferenceConstants.P_GETTER_CUSTOM_FORMAT;
import static org.fastcode.preferences.PreferenceConstants.P_GETTER_SETTER_FORMAT;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_PARAMETERIZED_TYPE_CHOICE;
import static org.fastcode.preferences.PreferenceConstants.P_SETTER_CUSTOM_FORMAT;
import static org.fastcode.preferences.PreferenceConstants.P_SETTER_VAR_PREFIX;
import static org.fastcode.util.FastCodeUtil.getEmptyListForNull;
import static org.fastcode.util.ImportUtil.createImport;
import static org.fastcode.util.MessageUtil.getChoiceFromMultipleValues;
import static org.fastcode.util.SourceUtil.createGetterSetters;
import static org.fastcode.util.SourceUtil.createGetterSettersWithCustomFormat;
import static org.fastcode.util.SourceUtil.createImportAsString;
import static org.fastcode.util.SourceUtil.createMethodSource;
import static org.fastcode.util.SourceUtil.doesFieldExistsInType;
import static org.fastcode.util.SourceUtil.getRepositoryServiceClass;
import static org.fastcode.util.SourceUtil.getSuperInterfacesType;
import static org.fastcode.util.SourceUtil.getTypeFromProject;
import static org.fastcode.util.SourceUtil.implementInterfaceMethods;
import static org.fastcode.util.SourceUtil.overrideBaseClassMethods;
import static org.fastcode.util.StringUtil.createEmbeddedInstance;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.findInStringArray;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.isJavaReservedWord;
import static org.fastcode.util.StringUtil.isOnEmpltyLine;
import static org.fastcode.util.StringUtil.isValidVariableName;
import static org.fastcode.util.StringUtil.makePlaceHolder;
import static org.fastcode.util.StringUtil.replacePlaceHolder;
import static org.fastcode.util.StringUtil.replacePlaceHolderWithBlank;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.fastcode.AbstractActionSupport;
import org.fastcode.Activator;
import org.fastcode.common.CreateVariableData;
import org.fastcode.common.FastCodeConstants.ACCESS_MODIFIER;
import org.fastcode.common.FastCodeConstants.CLASS_TYPE;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER_FORMAT;
import org.fastcode.common.FastCodeEntityHolder;
import org.fastcode.common.FastCodeField;
import org.fastcode.common.FastCodeMethod;
import org.fastcode.common.FastCodeReturn;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.Pair;
import org.fastcode.preferences.VersionControlPreferences;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.util.CreateSimilarDescriptorClass;
import org.fastcode.util.FastCodeContext;
import org.fastcode.util.FastCodeFileForCheckin;
import org.fastcode.util.FieldBuilderImpl;
import org.fastcode.util.MessageUtil;
import org.fastcode.util.RepositoryService;
import org.fastcode.util.SVNRepositoryService;
import org.fastcode.versioncontrol.FastCodeCheckinCache;

import static org.fastcode.util.VersionControlUtil.isPrjConfigured;
import static org.fastcode.util.VersionControlUtil.addOrUpdateFileStatusInCache;

/**
 * @author Gautam
 *
 */
public abstract class NewMemberCreateActionSupport extends AbstractActionSupport implements IEditorActionDelegate {

	private static Map<String, String>	typesMap			= new HashMap<String, String>();

	static {
		typesMap.put("java.util.List", "java.util.ArrayList");
		typesMap.put("java.util.Map", "java.util.HashMap");
		typesMap.put("java.util.Map", "java.util.TreeMap");
	}

	protected CreateVariableData		createVariableData;
	private final FieldBuilderImpl		fieldBuilderImpl	= new FieldBuilderImpl();
	// private final List<String> stringsList = new ArrayList<String>();
	IPreferenceStore					preferenceStore;
	boolean								prjShared			= false;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void runAction(ICompilationUnit compUnit, final IJavaElement javaElement) throws Exception {
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		//final Map<Object, Map<String, Object>> commitMessage = new HashMap<Object, Map<String,Object>>();
		String placeHolder = EMPTY_STR;
		FastCodeField origField = null;
		boolean createFileAlone = false;
		boolean prjConfigured = false;
		this.createVariableData = getCreateVariableData(compUnit);
		if (this.createVariableData == null) {
			return;
		}
		if (this.createVariableData.isVariableModifyAction()) {
			placeHolder = PLACEHOLDER_MODIFY_FIELD;
			origField = new FastCodeField(compUnit.findPrimaryType().getField(this.createVariableData.getModifiedVarOrigName()));
			deleteVariableInClass(compUnit);
		} else {
			placeHolder = PLACEHOLDER_FIELDS;
		}

		if (this.createVariableData.isCreateClassSimple() || this.createVariableData.isCreateClassDetailed()) {
			final String prjURI = this.createVariableData.getPackageFragment().getResource().getLocationURI().toString();
			final String path = prjURI.substring(prjURI.indexOf(COLON) + 1);
			final File newFileObj = new File(path + FILE_SEPARATOR + this.createVariableData.getClassName() + DOT
					+ this.createVariableData.getCompUnitType());
			final RepositoryService svnrepo = new SVNRepositoryService();
			this.prjShared = !isEmpty(this.createVariableData.getPackageFragment().getResource().getProject().getPersistentProperties());
			prjConfigured = !isEmpty(isPrjConfigured(this.createVariableData.getPackageFragment().getResource().getProject().getName()));
			if (this.autoCheckinEnabled && this.prjShared && prjConfigured) {
				final RepositoryService repositoryService = getRepositoryServiceClass();
				if (repositoryService.isFileInRepository(newFileObj)) { // && !MessageDialog.openQuestion(new Shell(), "File present in repository", "File already present in repository. Click yes to overwrite")) {
					createFileAlone = MessageDialog.openQuestion(new Shell(), "File present in repository",
							"File " + newFileObj.getName() + " already present in repository. Click yes to just create the file, No to return without any action.");
					if (!createFileAlone) {
						return;
					}
				}
			} else {
				createFileAlone = true;
			}
			//final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
			addOrUpdateFileStatusInCache(newFileObj);
			//checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(INITIATED, newFileObj.getAbsolutePath()));
			System.out.println( newFileObj.getAbsolutePath());
			compUnit = createClass(this.createVariableData);
			compUnit.becomeWorkingCopy(new NullProgressMonitor());
			final IEditorPart javaEditor = JavaUI.openInEditor(compUnit.findPrimaryType());
			JavaUI.revealInEditor(javaEditor, (IJavaElement) compUnit.findPrimaryType());

			//final File newFile = new File(compUnit.findPrimaryType().getResource().getLocationURI());
			if (!createFileAlone) {
				final IFile newFile = (IFile) compUnit.findPrimaryType().getResource(); //.getLocationURI());
				List<FastCodeEntityHolder> chngsForType = this.commitMessage.get(newFile);
				if (chngsForType == null) {
					chngsForType = new ArrayList<FastCodeEntityHolder>();
					chngsForType.add(new FastCodeEntityHolder(PLACEHOLDER_CLASS, new FastCodeType(compUnit.findPrimaryType())));
				}
				this.commitMessage.put(newFile, chngsForType);
			}
			this.editorPart = javaEditor;
		} else {
			this.prjShared = !isEmpty(compUnit.getResource().getProject().getPersistentProperties());
			prjConfigured = !isEmpty(isPrjConfigured(compUnit.getResource().getProject().getName()));
			if (!(this.autoCheckinEnabled && this.prjShared && prjConfigured)) {
				createFileAlone = true;
			}
			/*if (!this.autoCheckinEnabled || !this.prjShared || !prjConfigured) {
				createFileAlone = true;
			}*/
			final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
			addOrUpdateFileStatusInCache(new File(compUnit.findPrimaryType().getResource().getLocationURI()));
			//checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(INITIATED, new File(compUnit.findPrimaryType().getResource().getLocationURI()).getAbsolutePath()));
			System.out.println(new File(compUnit.findPrimaryType().getResource().getLocationURI()).getAbsolutePath());
		}
		IType type = compUnit.findPrimaryType();
		//final File file = new File(compUnit.findPrimaryType().getResource().getLocationURI());
		final IFile file = (IFile) compUnit.findPrimaryType().getResource();

		if (javaElement instanceof IMember) {
			final IMember member = (IMember) javaElement;
			final IType declaringType = member.getDeclaringType();
			type = declaringType != null && declaringType.exists() ? declaringType : compUnit.findPrimaryType();
		}

		IField field = null;
		IMethod method = null;
		//final IType leftType = null, rightType = null;

		final boolean simpleType = isSimpleType();
		/*if (!simpleType) {
			leftType = compUnit.getJavaProject().findType(pair.getLeft());
			rightType = compUnit.getJavaProject().findType(pair.getRight());

			this.addImport(compUnit, leftType);
			this.addImport(compUnit, rightType);
		}*/

		/*if (paramTypes != null) {
			for (final IType paramType : paramTypes) {
				this.addImport(compUnit, paramType);
			}
		}*/

		for (final String importType : getEmptyListForNull(this.createVariableData.getImportTypes())) {
			final IType imprtType = compUnit.getJavaProject().findType(importType);
			final IImportDeclaration importDeclaration = createImport(compUnit, imprtType, compUnit.getResource().getFileExtension());
			//addImport(compUnit, compUnit.getJavaProject().findType(importType));
		}

		String fieldName = null;

		String fieldSrc = null;
		//final String defaultFieldName;

		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		/*if (simpleType) {
			defaultFieldName = createDefaultInstance(paramTypes[0].getElementName());
		} else {
			defaultFieldName = paramTypes != null && paramTypes.length > 0 ? makeName(paramTypes[paramTypes.length - 1], leftType) : MY_NEW + leftType.getElementName();
			//			fieldNames[0] = fieldName;
			//			fieldSrc = createFieldSourceWithParameter(new Pair<IType, IType>(leftType, rightType), paramTypes, fieldName, Flags.AccPrivate);
		}
		final InputDialog inputDialog = new InputDialog(shell, "Create Field", "Enter a name or names (space separated) for the field", defaultFieldName,
				new IInputValidator() {
			public String isValid(final String newText) {
				if (isEmpty(newText)) {
					return "Input cannot be blank";
				}
				return null;
			}
		});

		final int retCode = inputDialog.open();
		final String[] fieldNames = inputDialog.getValue().split("\\s+");
		if (retCode == Window.CANCEL || fieldNames == null || fieldNames.length == 0) {
			return;
		}*/
		boolean customFormat = false;
		String setterCustomFormat = EMPTY_STR;
		String getterCustomFormat = EMPTY_STR;
		if (this.preferenceStore.getString(P_GETTER_SETTER_FORMAT).equals("custom")) {
			customFormat = true;
			setterCustomFormat = this.preferenceStore.getString(P_SETTER_CUSTOM_FORMAT);
			getterCustomFormat = this.preferenceStore.getString(P_GETTER_CUSTOM_FORMAT);
		}
		int elementType = javaElement == null ? TYPE : javaElement.getElementType();
		if (simpleType) {
			elementType = TYPE;
		} else {
			if (elementType == METHOD) {
				final String[] choices = { "Create Snippet", "Create Field" };

				final String choice = getChoiceFromMultipleValues(shell, "You are inside a method", "Would You Like To", choices);
				final int result = findInStringArray(choice, choices);
				if (result == 1) {
					elementType = TYPE;
				}
			}
		}
		final IJavaElement insertionPoint = this.createVariableData.getInsertionPoint();
		final String[] fieldNames = this.createVariableData.getFieldNames();
		switch (elementType) {
		case TYPE:
			/*
			 * if (checkIfFieldExists(type, fieldName)) { return; } field =
			 * type.createField(fieldSrc, null, false, null); break;
			 */
		case FIELD:
			//final GETTER_SETTER createGetterSetter = this.createVariableData.getGetterSetter(); //
			//final GETTER_SETTER createGetterSetter = this.getGetterSetterChoice(simpleType);
			if (this.createVariableData.getGetterSetter() == null) {
				this.createVariableData.setGetterSetter(getGetterSetterChoice(isSimpleType()));
			}
			if (this.createVariableData.isBuilderReqd()) {
				this.createVariableData.setGetterSetter(GETTER_SETTER.GETTER_EXIST);
				type = compUnit.findPrimaryType();
			}
			for (int i = 0; i < fieldNames.length; i++) {

				if (doesFieldExistsInType(type, fieldNames[i])) {
					MessageDialog.openWarning(shell, "Warning", "Field with name " + fieldNames[i] + " already exists.");
					continue;
				}

				final IJavaElement sibling = elementType == FIELD ? findNextElement(type, (IField) javaElement) : null;
				//if (simpleType) {
				fieldSrc = this.fieldBuilderImpl.buildFieldSource(null, this.createVariableData, fieldNames[i]);
				// fieldSrc = "private " + paramTypes[0].getElementName() +
				// SPACE + fieldNames[i] + ";" + NEWLINE;
				//	} else {
				/*fieldSrc = createFieldSourceWithParameter(new Pair<IType, IType>(leftType, rightType), paramTypes, fieldNames[i], createGetterSetter,
							Flags.AccPrivate);*/
				//}
				/*final File fileObj = new File(type.getResource().getLocationURI().toString());
				final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
				checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(INITIATED, fileObj.getAbsolutePath()));*/

				if (this.createVariableData.getStringInsertionPoint() != null) {
					if (this.createVariableData.getStringInsertionPoint().equals("First Member")) {
						int posOfField = 0;
						IJavaElement element = null;
						if (insertionPoint != null) {
							posOfField = type.getCompilationUnit().getSource().indexOf(insertionPoint.getElementName()) - 1;
							element = compUnit.getElementAt(posOfField);
						}

						field = type.createField(fieldSrc, element, false, null);

					} else if (this.createVariableData.getStringInsertionPoint().equals("Last Member")) {
						field = type.createField(fieldSrc, null, false, null);

					} else if (insertionPoint != null) {
						field = type.createField(fieldSrc, insertionPoint, false, null);

					}
				} else {

					field = type.createField(fieldSrc, sibling, false, null);

				}
				List<FastCodeMethod> methods = null;
				if (customFormat) {
					methods = createGetterSettersWithCustomFormat(type, field, this.createVariableData, setterCustomFormat,
							getterCustomFormat);
				} else {
					methods = createGetterSetters(type, field, this.createVariableData);
				}
				//createGetterSetters(type, field, paramTypes, createGetterSetter);
				fieldName = fieldNames[i];
				if (!createFileAlone) {
					FastCodeField fcField;
					if (this.createVariableData.isVariableModifyAction()) {
						fcField = origField;
					} else {
						fcField = new FastCodeField(field);
					}

					fcField.setGettersetter(GETTER_SETTER.getFormattedValue(this.createVariableData.getGetterSetter().getValue()));
					//				((List<Object>) fastCodeFieldList).add(fcField);
					fcField.setBuilderPattern(this.createVariableData.isBuilderReqd());
					List<FastCodeEntityHolder> chngesForType = this.commitMessage.get(file);
					if (chngesForType == null) {
						chngesForType = new ArrayList<FastCodeEntityHolder>();
						final List<Object> fastCodeFieldList = new ArrayList<Object>();
						fastCodeFieldList.add(fcField);
						chngesForType.add(new FastCodeEntityHolder(placeHolder, fastCodeFieldList));
					} else {
						boolean isNew = true;
						Object fastCodeFieldList = null;
						for (final FastCodeEntityHolder fcEntityHolder : chngesForType) {
							if (fcEntityHolder.getEntityName().equals(placeHolder)) {
								fastCodeFieldList = fcEntityHolder.getFastCodeEntity();
								isNew = false;
								break;
							}
						}

						if (isNew) {
							fastCodeFieldList = new ArrayList<Object>();
							((List<Object>) fastCodeFieldList).add(fcField);
							chngesForType.add(new FastCodeEntityHolder(placeHolder, fastCodeFieldList));
						} else {
							((List<Object>) fastCodeFieldList).add(fcField);
						}
					}
					this.commitMessage.put(file, chngesForType);
				}

				/*if (this.createVariableData.getGetterSetter() != null) {
					commitMessage.get(compUnit.findPrimaryType()).put(method, value)
				}*/
			}
			break;
		case METHOD:
			if (simpleType) {
				MessageDialog.openError(shell, "Error", "This feature does not work in a method. "
						+ "Please put the cursor on a field or the class name.");
				return;
			}
			fieldName = fieldNames[0];
			//fieldSrc = createFieldSourceWithParameter(new Pair<IType, IType>(leftType, rightType), paramTypes, fieldName, GETTER_SETTER.NONE, Flags.AccDefault);
			this.createVariableData.setAccessModifier(ACCESS_MODIFIER.DEFAULT);
			this.createVariableData.setGetterSetter(GETTER_SETTER.NONE);
			fieldSrc = this.fieldBuilderImpl.buildFieldSource(null, this.createVariableData, fieldNames[0]);
			method = (IMethod) javaElement;
			final ITextSelection selection = (ITextSelection) this.editorPart.getEditorSite().getSelectionProvider().getSelection();

			if (!isOnEmpltyLine(selection, method.getDeclaringType().getCompilationUnit().getSource())) {
				if (!MessageDialog.openConfirm(this.editorPart.getSite().getShell(), "Selection Error",
						"Your cursor may not be on an empty line. Yes to continue, No to bailout.")) {
					return;
				}
			}

			createNewTypedMemberInMethod(method, fieldSrc, selection.getOffset());

			if (this.autoCheckinEnabled && this.prjShared && prjConfigured) {
				final FastCodeReturn localVar = new FastCodeReturn(fieldName, null);
				List<FastCodeEntityHolder> chngesForType = this.commitMessage.get(file);
				if (chngesForType == null) {
					chngesForType = new ArrayList<FastCodeEntityHolder>();
					final List<Object> factCodeReturnList = new ArrayList<Object>();
					factCodeReturnList.add(localVar);
					chngesForType.add(new FastCodeEntityHolder(PLACEHOLDER_LOCALVARS, factCodeReturnList));
				} else {
					boolean isNew = true;
					Object factCodeReturnList = null;
					for (final FastCodeEntityHolder fcEntityHolder : chngesForType) {
						if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_LOCALVARS)) {
							factCodeReturnList = fcEntityHolder.getFastCodeEntity();
							isNew = false;
							break;
						}
					}

					if (isNew) {
						factCodeReturnList = new ArrayList<Object>();
						((List<Object>) factCodeReturnList).add(localVar);
						chngesForType.add(new FastCodeEntityHolder(PLACEHOLDER_LOCALVARS, factCodeReturnList));
					} else {
						((List<Object>) factCodeReturnList).add(localVar);
					}
				}
				this.commitMessage.put(file, chngesForType);
			}

			break;
		default:
			break;
		}

		if (field != null) {
			this.selection = new TextSelection(field.getNameRange().getOffset(), fieldName.equals(MY_NEW) ? MY_NEW.length()
					: fieldName.length());
		} else if (method != null) {
			final int start = ((ITextSelection) this.selection).getOffset() + fieldSrc.indexOf(fieldName);
			this.selection = new TextSelection(start, fieldName.equals(MY_NEW) ? MY_NEW.length() : fieldName.length());
		}
		if (this.selection != null) {
			this.editorPart.getEditorSite().getSelectionProvider().setSelection(this.selection);
		}
		if (this.createVariableData.isBuilderReqd()) {
			createBuilderMethods(type);
		}
		if (this.createVariableData.isCreateClassSimple() || this.createVariableData.isCreateClassDetailed()) {
			compUnit.commitWorkingCopy(false, null);
			compUnit.discardWorkingCopy();
			if (!this.commitMessage.isEmpty()) {
				final RepositoryService checkin = getRepositoryServiceClass();
				checkin.commitToRepository(this.commitMessage, false);
			}
		}
		this.createVariableData = null;
	}

	/**
	 *
	 * @param simpleType
	 * @param shell
	 * @param fieldNames
	 * @param i
	 * @return
	 */
	protected GETTER_SETTER getGetterSetterChoice(final boolean simpleType) {
		final String[] choices = simpleType ? new String[] { "Gettter/Setter", "None", "Gettter Only" } : new String[] { "Gettter/Adder",
				"None", "Gettter/Setter", "Gettter Only" };

		final String choice = getChoiceFromMultipleValues(new Shell(), "Gettter/Setter",
				"Would you like to create getter setter for fields you have created.", choices);
		final int result = findInStringArray(choice, choices);

		GETTER_SETTER createGetterSetter = null;

		switch (result) {
		case 0:
			createGetterSetter = simpleType ? GETTER_SETTER.GETTER_SETTER_EXIST : GETTER_SETTER.GETTER_ADDER_EXIST;
			break;
		case 2:
			createGetterSetter = simpleType ? GETTER_SETTER.GETTER_EXIST : GETTER_SETTER.GETTER_SETTER_EXIST;
			break;
		case 3:
			createGetterSetter = simpleType ? GETTER_SETTER.NONE : GETTER_SETTER.GETTER_EXIST;
			break;
		default:
			break;
		}
		return createGetterSetter;
	}

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement, final IType[] paramTypes) throws Exception {
		// Do nothing
	}

	/**
	 *
	 */
	@Override
	protected boolean doesModify() {
		return true;
	}

	//protected abstract CreateVariableData getCreateVariableData() throws Exception;
	/**
	 * @param compUnit
	 * @return
	 * @throws Exception
	 */
	protected CreateVariableData getCreateVariableData(final ICompilationUnit compUnit) throws Exception {
		final String fileName = this.editorPart.getEditorInput().getName();
		this.createVariableData.setCompUnitType(fileName.substring(fileName.lastIndexOf(DOT) + 1, fileName.length()));
		if (this.preferenceStore.contains(P_GETTER_SETTER_FORMAT)) {
			this.createVariableData.setGetterSetterFormat(GETTER_SETTER_FORMAT.getGetterSetterFormat(this.preferenceStore
					.getString(P_GETTER_SETTER_FORMAT)));
		}

		if (this.preferenceStore.contains(P_SETTER_VAR_PREFIX)) {
			this.createVariableData.setSetterVerPrefix(this.preferenceStore.getString(P_SETTER_VAR_PREFIX));
		}

		return this.createVariableData;
	}

	/**
	 *
	 */
	@Override
	protected boolean doesRequireMoreInfoFromUser() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.AbstractActionSupport#findSelectedJavaElement(org.eclipse.jdt.core.ICompilationUnit)
	 */
	@Override
	protected IJavaElement findSelectedJavaElement(final ICompilationUnit compUnit) throws Exception {
		if (this.selection instanceof ITextSelection) {
			final ITextSelection textSelection = (ITextSelection) this.selection;
			return compUnit.getElementAt(textSelection.getOffset());
		}
		return null;
	}

	/**
	 *
	 * @param type
	 * @param method
	 * @param fieldSrc
	 * @param cursorPos
	 * @throws Exception
	 */
	private IMethod createNewTypedMemberInMethod(final IMethod method, final String fieldSrc, final int cursorPos) throws Exception {

		final ITextEditor editor = (ITextEditor) this.editorPart;
		final IDocumentProvider documentProvider = editor.getDocumentProvider();
		final IDocument document = documentProvider.getDocument(editor.getEditorInput());
		document.replace(cursorPos, 0, fieldSrc);

		return method;
	}

	/**
	 *
	 * @param compUnit
	 * @param type
	 * @throws Exception
	 */
	protected void addImport(final ICompilationUnit compUnit, final IType type) throws Exception {

		if (type == null || !type.exists()) {
			throw new Exception("type does not exit");
		}

		final String pkg = type.getPackageFragment().getElementName();
		if (pkg.equals("java.lang")) {
			return;
		}

		if (this.createVariableData.getCompUnitType().equals(GROOVY_EXTENSION) && pkg.equals("java.util")) {
			return;
		}

		IImportDeclaration imp = compUnit.getImport(pkg + ".*");

		if (imp == null || !imp.exists()) {
			imp = compUnit.getImport(type.getFullyQualifiedName());
			if (imp == null || !imp.exists()) {
				compUnit.createImport(type.getFullyQualifiedName(), null, null);
			}
		}
	}

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	@Override
	protected IType[] getTypesFromUser(final String title, final String description) throws Exception {

		if (!isSimpleType()) {
			final String parameterizedTypeChoice = Activator.getDefault().getPreferenceStore()
					.getString(P_GLOBAL_PARAMETERIZED_TYPE_CHOICE);

			boolean createParameterDialog = true;
			if (parameterizedTypeChoice.equals(GLOBAL_ASK_TO_CREATE)) {
				final MessageDialogWithToggle dialogWithToggle = MessageDialogWithToggle.openYesNoQuestion(this.editorPart.getSite()
						.getShell(), "Parameterized Type", "Would you like to create a parameterized type?", "Remember Decision", false,
						Activator.getDefault().getPreferenceStore(), P_ASK_FOR_PARAMETERIZED_TYPE);
				if (dialogWithToggle.getReturnCode() != MESSAGE_DIALOG_RETURN_YES) {
					createParameterDialog = false;
				}
			} else if (parameterizedTypeChoice.equals(GLOBAL_NEVER_CREATE)) {
				createParameterDialog = false;
			}

			if (!createParameterDialog) {
				return null;
			}
		}

		final SelectionDialog selectionDialog = createTypeDialog(this.editorPart.getSite().getShell(), null,
				SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ALL_TYPES, true, EMPTY_STR);
		selectionDialog.setTitle(title);
		selectionDialog.setMessage(title + " For " + description);

		if (selectionDialog.open() == Window.CANCEL) {
			return null;
		}

		if (selectionDialog.getResult() == null || selectionDialog.getResult().length == 0) {
			return null;
		}
		final IType[] paramArr = new IType[1];
		paramArr[0] = (IType) selectionDialog.getResult()[0];
		return paramArr;
	}

	/**
	 *
	 * @param description
	 * @param title
	 * @return
	 * @throws Exception
	 */
	protected IType openTypeDialog(final String title, final String description) throws Exception {
		final SelectionDialog selectionDialog = createTypeDialog(this.editorPart.getSite().getShell(), null,
				SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ALL_TYPES, true, EMPTY_STR);
		selectionDialog.setTitle(title);
		selectionDialog.setMessage(title + " For " + description);

		if (selectionDialog.open() == Window.CANCEL || selectionDialog.getResult() == null || selectionDialog.getResult().length == 0) {
			return null;
		}

		return (IType) selectionDialog.getResult()[0];
	}

	/**
	 * @param defaultFieldName
	 * @throws Exception
	 */
	protected void openInputDialog(final String defaultFieldName) throws Exception {
		final InputDialog inputDialog = new InputDialog(new Shell(), "Create Field",
				"Enter a name or names (space separated) for the field", defaultFieldName, new IInputValidator() {

					//@Override
					@Override
					public String isValid(final String newText) {
						if (isEmpty(newText)) {
							return "Input cannot be blank";
						}
						for (final String text : newText.split("\\s+")) {
							if (isJavaReservedWord(text)) {
								return "Java Reserved word cannot be used as variable name";
							}
							if (!isValidVariableName(text)) {
								return "Special charecters cannot be used in variable name";
							}
						}
						return null;
					}
				});

		final int retCode = inputDialog.open();
		final String[] fieldNames = inputDialog.getValue().split("\\s+");
		if (retCode == Window.CANCEL || fieldNames == null || fieldNames.length == 0) {
			return;
		}

		this.createVariableData.setFieldNames(fieldNames);
		//return fieldNames;
	}

	protected boolean isSimpleType() {
		return false;
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(final IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		this.window = window;
	}

	/**
	 * @param type
	 */
	public void createBuilderMethods(final IType type) {

		try {
			IField field = null;
			String fieldSrc = null;
			final IType type1 = getTypeFromProject(this.createVariableData.getJavaProject(), type.getFullyQualifiedName());
			boolean builderExists = false;
			final IType[] content = type1.getTypes();
			for (int i = 0; i < content.length; i++) {
				if (content[i].getElementName().contains(BUILDER_CLASS_NAME)) {
					builderExists = true;
					break;
				}
			}
			IType newType = null;
			String methodSrc = EMPTY_STR;

			if (!builderExists) {
				newType = type.createType(MODIFIER_PUBLIC + SPACE + STATIC + SPACE + "class" + SPACE + BUILDER_CLASS_NAME + "{\n}", null,
						false, null);
				newType.createMethod(MODIFIER_PUBLIC + SPACE + BUILDER_CLASS_NAME + "(){\n}", null, false, null);
				methodSrc = createMethodSource("build", "return new " + type.getElementName() + "(this);", EMPTY_STR,
						type.getElementName(), null, false, true);
				newType.createMethod(methodSrc, null, false, null);

				methodSrc = MODIFIER_PRIVATE + SPACE + type.getElementName() + "(final " + BUILDER_CLASS_NAME + SPACE
						+ BUILDER_TYPE_VARIBLE + "){\n}";
				type.createMethod(methodSrc, newType, false, null);
			} else {
				newType = type.getType(BUILDER_CLASS_NAME);
			}
			final String[] fieldNames = this.createVariableData.getFieldNames();
			for (int i = 0; i < fieldNames.length; i++) {
				//final List<Pair<String, String>> methArgs = new ArrayList<Pair<String, String>>();
				final List<Pair<String, String>> methArgsFlds = new ArrayList<Pair<String, String>>();
				fieldSrc = this.fieldBuilderImpl.buildFieldSource(null, this.createVariableData, fieldNames[i]);
				field = newType.createField(fieldSrc, null, false, null);
				final String typeSignature = Signature.getSignatureSimpleName(field.getTypeSignature());
				final String source = "\t this." + field.getElementName() + SPACE + EQUAL + SPACE + BUILDER_TYPE_VARIBLE + DOT
						+ field.getElementName() + ";\n";
				final ITextEditor editor = (ITextEditor) this.editorPart;
				final IDocumentProvider documentProvider = editor.getDocumentProvider();
				final IDocument document = documentProvider.getDocument(editor.getEditorInput());
				final FindReplaceDocumentAdapter adapter = new FindReplaceDocumentAdapter(document);
				int offset = adapter.find(0, type.getElementName() + "\\(final " + BUILDER_CLASS_NAME + SPACE + BUILDER_TYPE_VARIBLE, true,
						true, true, false).getOffset();
				offset = adapter.find(offset, RIGHT_CURL, true, false, false, false).getOffset();
				document.replace(offset, 0, source);
				methArgsFlds.add(new Pair<String, String>(typeSignature, field.getElementName()));
				methodSrc = createMethodSource("with" + createEmbeddedInstance(field.getElementName()), "this." + field.getElementName()
						+ SPACE + EQUAL + SPACE + field.getElementName() + ";\n\treturn this;", "", newType.getElementName(), methArgsFlds,
						false, true);
				newType.createMethod(methodSrc, null, false, null);

			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @param compUnit
	 */
	public void deleteVariableInClass(final ICompilationUnit compUnit) {
		final String varName = this.createVariableData.getModifiedVarOrigName();
		final IField selectedField = compUnit.findPrimaryType().getField(varName);

		try {

			final FastCodeField fastCodeField = new FastCodeField(selectedField);
			final IType type1 = getTypeFromProject(compUnit.getJavaProject(), compUnit.findPrimaryType().getFullyQualifiedName());
			// IJavaElement[] content=compUnit.getChildren();
			final IMethod[] content = type1.getMethods();
			for (int i = 0; i < content.length; i++) {
				if (content[i].getElementName().equals(fastCodeField.getGetter())) {
					content[i].delete(true, null);
				}
				if (content[i].getElementName().equals(fastCodeField.getSetter())) {
					content[i].delete(true, null);
				}
				if (content[i].getElementName().equals("add" + selectedField.getElementName())) {
					content[i].delete(true, null);
				}

			}
			selectedField.delete(true, null);
		} catch (final Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * @param createVariableData
	 * @return
	 * @throws Exception
	 */
	public static ICompilationUnit createClass(final CreateVariableData createVariableData) throws Exception {
		final String className = createVariableData.getClassName();
		final IPackageFragment packageFragment = createVariableData.getPackageFragment();
		final boolean isDefaultConstReqd = createVariableData.isDefaultConsReqd();
		 String type = createVariableData.getCompUnitType();
			if(type.equals(EMPTY_STR)){
				if (packageFragment.getJavaProject().getProject().getNature("org.eclipse.jdt.groovy.core.groovyNature") == null) {
					type = JAVA_EXTENSION;
				} else {
					type = GROOVY_EXTENSION;
				}
				createVariableData.setCompUnitType(type);
			}
		final Map<String, Object> placeHolders = new HashMap<String, Object>();
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final List<IType> importTypes = new ArrayList<IType>();
		if (createVariableData.getiSelectBaseClassType() != null) {
			placeHolders.put("super_class", createVariableData.getiSelectBaseClassType().getElementName());
			importTypes.add(createVariableData.getiSelectBaseClassType());
		}
		final StringBuilder interfaces = new StringBuilder();
		for (final IType tmptype : createVariableData.getiInterfaceType()) {
			interfaces.append(tmptype.getElementName());
			importTypes.add(tmptype);
		}
		if (!isEmpty(interfaces.toString())) {
			placeHolders.put("interfaces", interfaces.toString());
		}
		final String importAsString = createImportAsString(importTypes);
		String classBody = globalSettings.getClassBody();
		placeHolders.put(CLASS_ANNOTATIONS_STR, EMPTY_STR);
		if (!placeHolders.containsKey("super_class")) {
			classBody = replacePlaceHolderWithBlank(classBody, "extends", "super_class", "implements");
		}
		if (!placeHolders.containsKey("interfaces")) {
			classBody = replacePlaceHolderWithBlank(classBody, "implements", "interfaces", "{");
		}
		if (!placeHolders.containsKey(CLASS_MODIFIER_STR)) {
			classBody = replacePlaceHolderWithBlank(classBody, null, CLASS_MODIFIER_STR, makePlaceHolder(CLASS_TYPE_STR));
		}
		placeHolders.put(PACKAGE_NAME_STR, packageFragment.getElementName());
		placeHolders.put(CLASS_NAME_STR, className);
		getGlobalSettings(placeHolders);
		final String classHeader = evaluateByVelocity(globalSettings.getClassHeader(), placeHolders);
		placeHolders.put(CLASS_HEADER_STR, classHeader);

		classBody = replacePlaceHolder(classBody, CLASS_IMPORTS_STR, importAsString);
		placeHolders.put(CLASS_BODY_STR, EMPTY_STR);
		if (isDefaultConstReqd) {
			final String insideclassBody = MODIFIER_PUBLIC + SPACE + className + LEFT_PAREN + RIGHT_PAREN + LEFT_CURL + NEWLINE + TAB
					+ SUPER + LEFT_PAREN + RIGHT_PAREN + SEMICOLON + NEWLINE + TAB + RIGHT_CURL;
			placeHolders.put(CLASS_BODY_STR, insideclassBody);
		}
		placeHolders.put(CLASS_TYPE_STR, CLASS_TYPE.CLASS.value().toLowerCase());

		getGlobalSettings(placeHolders);
		String source = evaluateByVelocity(classBody, placeHolders);
		if (type.equals(GROOVY_EXTENSION)) {
			source = source.replaceFirst(MODIFIER_PUBLIC, EMPTY_STR);
		}
		final ICompilationUnit compilationUnit = packageFragment.createCompilationUnit(className + DOT + type, source, false, null);
		for (final IType tmptype : createVariableData.getiInterfaceType()) {
			final FastCodeContext fastCodeContext = new FastCodeContext(tmptype);
			final CreateSimilarDescriptorClass createSimilarDescriptorClass = new CreateSimilarDescriptorClass.Builder().withClassType(
					CLASS_TYPE.CLASS).build();
			implementInterfaceMethods(tmptype, fastCodeContext, compilationUnit.findPrimaryType(), null, createSimilarDescriptorClass);
			final IType[] superInterfaceType = getSuperInterfacesType(tmptype);
			if (superInterfaceType != null) {
				for (final IType type1 : superInterfaceType) {
					if (type1 == null || !type1.exists()) {
						continue;
					}
					final FastCodeContext context = new FastCodeContext(type1);
					implementInterfaceMethods(type1, context, compilationUnit.findPrimaryType(), null, createSimilarDescriptorClass);
				}
			}
		}

		overrideBaseClassMethods(compilationUnit);
		return compilationUnit;
	}
}

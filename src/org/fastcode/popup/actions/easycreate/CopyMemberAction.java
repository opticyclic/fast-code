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

import static org.eclipse.jdt.core.Flags.AccStatic;
import static org.eclipse.jdt.core.Flags.isPrivate;
import static org.eclipse.jdt.core.Flags.isProtected;
import static org.eclipse.jdt.core.Flags.isPublic;
import static org.eclipse.jdt.core.Flags.isStatic;
import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.eclipse.jface.dialogs.MessageDialog.openWarning;
import static org.fastcode.common.FastCodeConstants.CLASS_MODIFIER_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_NAME_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_TYPE_STR;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.COPYOF;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.DOT_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.INITIATED;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN;
import static org.fastcode.common.FastCodeConstants.MESSAGE_DIALOG_RETURN_YES;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.NEW_IMPORT;
import static org.fastcode.common.FastCodeConstants.PACKAGE_NAME_STR;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_CLASS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_FIELDS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_METHODS;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.preferences.PreferenceConstants.P_ASK_FOR_COPY_METHOD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GETTER_SETTER_FORMAT;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_METHOD_ANNOTATIONS;
import static org.fastcode.preferences.PreferenceConstants.P_SETTER_VAR_PREFIX;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.ImportUtil.findImport;
import static org.fastcode.util.ImportUtil.findNextImportDeclaration;
import static org.fastcode.util.JUnitUtil.isJunitTest;
import static org.fastcode.util.SourceUtil.createGetterSetters;
import static org.fastcode.util.SourceUtil.createSimilar;
import static org.fastcode.util.SourceUtil.doesFieldExistsInType;
import static org.fastcode.util.SourceUtil.doesGetterSetterExist;
import static org.fastcode.util.SourceUtil.doesMethodExistsInType;
import static org.fastcode.util.SourceUtil.doesTypeExists;
import static org.fastcode.util.SourceUtil.getRepositoryServiceClass;
import static org.fastcode.util.StringUtil.computeDate;
import static org.fastcode.util.StringUtil.isAllLettersUpperCase;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.isFirstLetterUpperCase;
import static org.fastcode.util.StringUtil.replacePlaceHolder;
import static org.fastcode.util.VersionControlUtil.addOrUpdateFileStatusInCache;
import static org.fastcode.util.VersionControlUtil.isPrjConfigured;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.AbstractActionSupport;
import org.fastcode.Activator;
import org.fastcode.common.CreateVariableData;
import org.fastcode.common.FastCodeConstants.CREATE_OPTIONS_CHOICE;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER_FORMAT;
import org.fastcode.common.FastCodeEntityHolder;
import org.fastcode.common.FastCodeField;
import org.fastcode.common.FastCodeMethod;
import org.fastcode.common.FastCodeType;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.util.CreateSimilarDescriptor;
import org.fastcode.util.CreateSimilarDescriptorClass;
import org.fastcode.util.FastCodeContext;
import org.fastcode.util.FastCodeFileForCheckin;
import org.fastcode.util.RepositoryService;
import org.fastcode.versioncontrol.FastCodeCheckinCache;

/**
 * @author Gautam
 *
 */
public class CopyMemberAction extends AbstractActionSupport implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	protected boolean				allowMultiple	= false;
	protected CreateVariableData	createVariableData;
	IPreferenceStore				preferenceStore;

	@Override
	public void dispose() {
	}

	/**
	 * @param compUnit
	 * @param javaElement
	 *
	 * @throws Exception
	 */
	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement) throws Exception {
		/*this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		this.createVariableData = getCreateVariableData(compUnit);
		if (this.createVariableData == null) {
			return;
		}*/

		final IType primaryType = compUnit.findPrimaryType();
		if (javaElement instanceof IMember) {
			final IMember member = (IMember) javaElement;
			final IType declaringType = member.getDeclaringType();
			copySelectedMember(declaringType == null || !declaringType.exists() ? primaryType : declaringType, javaElement, null);
		} else if (javaElement != null) {
			copySelectedMember(primaryType, javaElement, null);
		}
	}

	/**
	 * @param compUnit
	 *
	 */
	@Override
	protected IJavaElement findSelectedJavaElement(final ICompilationUnit compUnit) throws Exception {
		IJavaElement element = null;
		if (this.selection instanceof ITextSelection) {
			element = compUnit.getElementAt(((ITextSelection) this.selection).getOffset());
			return element;
		}

		return element;
	}

	/**
	 * This method is used by both copying from existing member or creating new
	 * member.
	 *
	 * @param type
	 * @param newField
	 * @throws Exception
	 *
	 */
	protected void copySelectedMember(final IType type, final IJavaElement element, String newField) throws Exception {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();

		if (element == null) {
			openError(this.editorPart.getSite().getShell(), "Selection Error", "Please select some field and try again.");
			return;
		}

		switch (element.getElementType()) {
		case IJavaElement.TYPE:
			if (!isMemberNameSelected(type, (ITextSelection) this.selection)) {
				openError(this.editorPart.getSite().getShell(), "Selection Error",
						"Please select part or whole name of a method/field/type and try again.");
				return;
			}
			if (!element.equals(type)) {
				openError(this.editorPart.getSite().getShell(), "Selection Error",
						"Please select part or whole name of primary type and try again.");
				return;
			}
			final InputDialog typeInputDialog = new InputDialog(this.editorPart.getSite().getShell(), "New Name",
					"Enter a new name for class or names (space separated)", EMPTY_STR, null);
			if (typeInputDialog.open() == Window.CANCEL) {
				return;
			}
			final String newTypeName = typeInputDialog.getValue();

			final ICompilationUnit retCompUnit = copyType(type, newTypeName, (ITextSelection) this.selection);
			if (retCompUnit != null) {
				final IEditorPart javaEditor = JavaUI.openInEditor(retCompUnit);
				JavaUI.revealInEditor(javaEditor, (IJavaElement) retCompUnit.findPrimaryType());
			}
			break;
		case IJavaElement.IMPORT_DECLARATION:
			final IImportDeclaration importDeclaration = (IImportDeclaration) element;
			copyImport(type, importDeclaration, (ITextSelection) this.selection);
			break;
		case IJavaElement.FIELD:
			final IField field = (IField) element;
			if (!isMemberNameSelected(field, (ITextSelection) this.selection)) {
				openError(this.editorPart.getSite().getShell(), "Selection Error",
						"Please select part or whole name of a method/field and try again.");
				return;
			}

			//final boolean createGetterSetter = globalSettings.isGetterSetterForPrivateFields();

			if (isEmpty(newField) && (isPrivate(field.getFlags()) || isProtected(field.getFlags())) || this.allowMultiple
					|| doesGetterSetterExist(field) != GETTER_SETTER.NONE) {
				final InputDialog fieldInputDialog = new InputDialog(this.editorPart.getSite().getShell(), "New Name",
						"Enter a new name for field or names (space separated)", EMPTY_STR, null);
				if (fieldInputDialog.open() == Window.CANCEL) {
					return;
				}
				newField = fieldInputDialog.getValue();
			} else if (isEmpty(newField)) {
				newField = "copyOf";
			}

			copyField(type, field, newField, (ITextSelection) this.selection);
			break;
		case IJavaElement.METHOD:
			if (!isMemberNameSelected((IMethod) element, (ITextSelection) this.selection)) {
				MessageDialog.openError(this.editorPart.getSite().getShell(), "Selection Error",
						"Please select part or whole name of a method/field and try again.");
				return;
			}
			boolean copyBody = true;
			if (type.isClass()) {
				if (globalSettings.getCopyMethodBody() == CREATE_OPTIONS_CHOICE.NEVER_CREATE) {
					copyBody = false;
				} else if (globalSettings.getCopyMethodBody() == CREATE_OPTIONS_CHOICE.ASK_TO_CREATE) {
					final MessageDialogWithToggle dialogWithToggle = MessageDialogWithToggle.openYesNoQuestion(this.editorPart.getSite()
							.getShell(), "Copy Method Body", "Would you like to copy method's body as well?", "Remember Decision", false,
							Activator.getDefault().getPreferenceStore(), P_ASK_FOR_COPY_METHOD_BODY);
					if (dialogWithToggle.getReturnCode() != MESSAGE_DIALOG_RETURN_YES) {
						copyBody = false;
					}
				}
			}
			copyMethod(type, (IMethod) element, (ITextSelection) this.selection, copyBody);
			break;
		default:
			break;
		}
	}

	/**
	 * @param type
	 * @param field
	 * @param newField
	 * @param selection
	 * @throws Exception
	 *
	 */
	protected void copyField(final IType type, final IField field, final String newFieldPart, final ITextSelection selection)
			throws Exception {
		final String selectedText = selection.getText();

		final String[] newFieldPartArr = newFieldPart.split("\\s+");

		for (String newFldPart : newFieldPartArr) {
			newFldPart = modifyInput(field, selectedText, newFldPart);

			final String newFieldName = constructNewMemberName(field, newFldPart, selection);

			if (doesFieldExistsInType(type, newFieldName)) {
				showErrorAndSwitchToExistingElement(field, "Field already exists");
				return;
			}
		}

		IField lastField = field;

		for (String newFldPart : newFieldPartArr) {
			newFldPart = modifyInput(field, selectedText, newFldPart);

			final String newFieldName = constructNewMemberName(field, newFldPart, selection);

			final File newFileObj = new File(type.getResource().getLocationURI().toString());
			final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
			//checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(INITIATED, newFileObj.getAbsolutePath()));
			addOrUpdateFileStatusInCache(newFileObj);
			final IField newField = (IField) copyMemberAndSelect(type, field, lastField, newFieldName, newFldPart, selectedText, true);

			this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
			this.createVariableData = getCreateVariableData(null);
			if (this.createVariableData == null) {
				return;
			}
			final GETTER_SETTER getterSetter = doesGetterSetterExist(field);
			if (this.createVariableData.getGetterSetter() == null) {
				this.createVariableData.setGetterSetter(getterSetter);
			}
			this.createVariableData.setFieldParams(null);
			createGetterSetters(type, newField, this.createVariableData);
			lastField = newField;

			final FastCodeField fcField = new FastCodeField(newField);
			fcField.setGettersetter(GETTER_SETTER.getFormattedValue(this.createVariableData.getGetterSetter().getValue()));
			final boolean prjShared = !isEmpty(type.getResource().getProject().getPersistentProperties());
			final boolean prjConfigured = !isEmpty(isPrjConfigured(type.getResource().getProject().getName()));
			if (this.autoCheckinEnabled && prjShared && prjConfigured) {
				final IFile file = (IFile) type.getResource();
				List<FastCodeEntityHolder> chngesForType = this.commitMessage.get(file);
				if (chngesForType == null) {
					chngesForType = new ArrayList<FastCodeEntityHolder>();
					final List<Object> fastCodeFieldList = new ArrayList<Object>();
					fastCodeFieldList.add(fcField);
					chngesForType.add(new FastCodeEntityHolder(PLACEHOLDER_FIELDS, fastCodeFieldList));
				} else {
					boolean isNew = true;
					Object fastCodeFieldList = null;
					for (final FastCodeEntityHolder fcEntityHolder : chngesForType) {
						if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_FIELDS)) {
							fastCodeFieldList = fcEntityHolder.getFastCodeEntity();
							isNew = false;
							break;
						}
					}

					if (isNew) {
						fastCodeFieldList = new ArrayList<Object>();
						((List<Object>) fastCodeFieldList).add(fcField);
						chngesForType.add(new FastCodeEntityHolder(PLACEHOLDER_FIELDS, fastCodeFieldList));
					} else {
						((List<Object>) fastCodeFieldList).add(fcField);
					}
				}
				this.commitMessage.put(file, chngesForType);
			}
		}
	}

	/**
	 * @param member
	 * @param selectedText
	 * @param newPart
	 * @return
	 */
	private String modifyInput(final IMember member, final String selectedText, String newPart) {
		if (isAllLettersUpperCase(selectedText)) {
			if (selectedText.length() == 1 && !isAllLettersUpperCase(member.getElementName())) {
				newPart = newPart.substring(0, 1).toUpperCase() + newPart.substring(1);
			} else {
				newPart = newPart.toUpperCase();
			}
		} else if (isFirstLetterUpperCase(selectedText)) {
			newPart = newPart.substring(0, 1).toUpperCase() + newPart.substring(1);
		}
		return newPart;
	}

	/**
	 *
	 * @param type
	 * @param importDeclaration
	 * @param selection
	 * @throws Exception
	 */
	protected void copyImport(final IType type, final IImportDeclaration importDeclaration, final ITextSelection selection)
			throws Exception {
		if (!Flags.isStatic(importDeclaration.getFlags())) {
			MessageDialog.openError(this.editorPart.getSite().getShell(), "Static import",
					"Please use this feature only for static imports.");
			return;
		}
		final String newImportStr = findImport(type, importDeclaration, selection);
		final IImportDeclaration newImportDecl = type.getCompilationUnit().createImport(newImportStr,
				findNextImportDeclaration(type, importDeclaration), AccStatic, null);
		final int off = newImportDecl.getSourceRange().getOffset();
		int start = 0, len = 0;
		if (newImportStr.endsWith(NEW_IMPORT)) {
			start = newImportDecl.getElementName().indexOf(NEW_IMPORT);
			len = NEW_IMPORT.length();
		} else {
			final int tmp = importDeclaration.getElementName().lastIndexOf(DOT_CHAR);
			final String fieldImp = importDeclaration.getElementName().substring(tmp + 1);
			start = newImportDecl.getElementName().lastIndexOf(DOT_CHAR) + 1;
			len = newImportDecl.getElementName().length() - start;
			if (!fieldImp.equals(selection.getText())) {
				final int diff = fieldImp.length() - selection.getText().length();
				start += diff;
				len -= diff;

			}
		}
		final ISelection newSelection = new TextSelection(off + 14 + start, len);
		this.editorPart.getEditorSite().getSelectionProvider().setSelection(newSelection);
	}

	/**
	 *
	 * @param type
	 * @param method
	 * @param selectedText
	 * @throws Exception
	 */
	protected void copyMethod(final IType type, final IMethod method, final ITextSelection selection, final boolean copyBody)
			throws Exception {
		final String selectedText = selection.getText();
		final String newMethodName = constructNewMemberName(method, null, selection);
		if (doesMethodExistsInType(type, method, newMethodName)) {
			final IMethod newMethod = type.getMethod(newMethodName, method.getParameterTypes());
			showErrorAndSwitchToExistingElement(newMethod, "New method already exists");
			return;
		}

		final File newFileObj = new File(type.getResource().getLocationURI().toString());
		final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
		addOrUpdateFileStatusInCache(newFileObj);
		//checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(INITIATED, newFileObj.getAbsolutePath()));

		final IMethod newMethod = (IMethod) copyMemberAndSelect(type, method, method, newMethodName, null, selectedText, copyBody);
		final boolean prjShared = !isEmpty(type.getResource().getProject().getPersistentProperties());
		final boolean prjConfigured = !isEmpty(isPrjConfigured(type.getResource().getProject().getName()));
		if (this.autoCheckinEnabled && prjShared && prjConfigured) {
			final IFile file = (IFile) type.getResource();
			List<FastCodeEntityHolder> chngsForType = this.commitMessage.get(file);
			if (chngsForType == null) {
				chngsForType = new ArrayList<FastCodeEntityHolder>();
				final List<Object> fastCodeMethList = new ArrayList<Object>();
				fastCodeMethList.add(new FastCodeMethod(newMethod));
				chngsForType.add(new FastCodeEntityHolder(PLACEHOLDER_METHODS, fastCodeMethList));
			} else {
				boolean isNew = true;
				Object fastCodeMethList = null;
				for (final FastCodeEntityHolder fcEntityHolder : chngsForType) {
					if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_METHODS)) {
						fastCodeMethList = fcEntityHolder.getFastCodeEntity();
						isNew = false;
						break;
					}
				}

				if (isNew) {
					fastCodeMethList = new ArrayList<Object>();
					((List<Object>) fastCodeMethList).add(new FastCodeMethod(newMethod));
					chngsForType.add(new FastCodeEntityHolder(PLACEHOLDER_METHODS, fastCodeMethList));
				} else {
					((List<Object>) fastCodeMethList).add(new FastCodeMethod(newMethod));
				}
			}
			this.commitMessage.put(file, chngsForType);
		}
	}

	/**
	 *
	 * @param fromType
	 * @param newTypeName
	 * @param selection
	 * @throws Exception
	 */
	protected ICompilationUnit copyType(final IType fromType, final String newTypeName, final ITextSelection selection) throws Exception {
		final String selectedText = selection.getText();

		final String[] newTypePartArr = newTypeName.split("\\s+");
		ICompilationUnit compUnit = null;
		final IPackageFragment packageFragment = fromType.getPackageFragment();
		//final StringBuilder fileInRepo = new StringBuilder(EMPTY_STR);

		for (String newTypPart : newTypePartArr) {
			boolean createFileAlone = false;
			newTypPart = modifyInput(fromType, selectedText, newTypPart);

			final String newTypName = constructNewMemberName(fromType, newTypPart, selection);

			if (doesTypeExists(fromType, newTypName)) {
				openWarning(this.editorPart.getSite().getShell(), "Already Exists", "Class " + newTypName + " already exits");
				continue;
			}
			final CreateSimilarDescriptorClass createSimilarDescriptorClass = CreateSimilarDescriptorClass.createSimilarDescriptor(
					fromType, newTypName);
			final FastCodeContext fastCodeContext = new FastCodeContext(fromType);
			fastCodeContext.addToPlaceHolders(PACKAGE_NAME_STR, packageFragment.getElementName());
			fastCodeContext.addToPlaceHolders(CLASS_NAME_STR, newTypName);
			fastCodeContext.addToPlaceHolders(CLASS_MODIFIER_STR, createSimilarDescriptorClass.isFinalClass() ? "final" : EMPTY_STR);
			fastCodeContext.addToPlaceHolders(CLASS_TYPE_STR, createSimilarDescriptorClass.getClassType().value().toLowerCase());
			final CreateSimilarDescriptor createSimilarDescriptor = new CreateSimilarDescriptor.Builder()
					.withCreateSimilarDescriptorClasses(new CreateSimilarDescriptorClass[] { createSimilarDescriptorClass }).build();//new CreateSimilarDescriptor(null, false, null, null, null, false, false, false, null,false, createSimilarDescriptorClass);
			createSimilarDescriptor.numbersOfCreateSimilarDescriptorClasses(createSimilarDescriptorClass);
			final IMember[] members = {};

			final String prjURI = packageFragment.getResource().getLocationURI().toString();
			final String path = prjURI.substring(prjURI.indexOf(COLON) + 1);
			final File newFileObj = new File(path + FORWARD_SLASH + newTypName + DOT + JAVA_EXTENSION);
			final boolean prjShared = !isEmpty(packageFragment.getResource().getProject().getPersistentProperties());
			final boolean prjConfigured = !isEmpty(isPrjConfigured(packageFragment.getResource().getProject().getName()));
			if (this.autoCheckinEnabled && prjShared && prjConfigured) {
				final RepositoryService repositoryService = getRepositoryServiceClass();
				if (repositoryService.isFileInRepository(newFileObj)) { // && !MessageDialog.openQuestion(new Shell(), "File present in repository", "File already present in repository. Click yes to overwrite")) {
					/*fileInRepo.append(EMPTY_STR.equals(fileInRepo.toString()) ? "File(s)" + newTypName + COMMA : newTypName);
					continue;*/
					createFileAlone = MessageDialog.openQuestion(new Shell(), "File present in repository",
							"File " + newFileObj.getName() + " already present in repository. Click yes to just create the file, No to return without any action.");
					if (!createFileAlone) {
						continue;
					}
				}
			} else {
				createFileAlone = true;
			}

			final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
			addOrUpdateFileStatusInCache(newFileObj);
			//checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(INITIATED, newFileObj.getAbsolutePath()));

			createSimilar(fastCodeContext, members, createSimilarDescriptor, new NullProgressMonitor());
			compUnit = fastCodeContext.getCompilationUnitRegsistry(createSimilarDescriptorClass);
			if (!createFileAlone) {
				final IFile newFile = (IFile) compUnit.findPrimaryType().getResource();
				List<FastCodeEntityHolder> chngsForType = this.commitMessage.get(newFile);
				if (chngsForType == null) {
					chngsForType = new ArrayList<FastCodeEntityHolder>();
					chngsForType.add(new FastCodeEntityHolder(PLACEHOLDER_CLASS, new FastCodeType(compUnit.findPrimaryType())));
				}
				this.commitMessage.put(newFile, chngsForType);
			}
		}
		/*if (!EMPTY_STR.equals(fileInRepo.toString())) {
			fileInRepo.append("already persent in repository. The class(es) were not created. Please synchronise and try again.");
			MessageDialog.openWarning(new Shell(), "File(s) present in repository", fileInRepo.toString());
		}*/

		return compUnit;
	}

	/**
	 *
	 * @param type
	 * @param member
	 * @param newMemberName
	 * @param newFieldPart
	 * @param selectedText
	 * @param copyBody
	 * @return
	 * @throws Exception
	 */
	private IMember copyMemberAndSelect(final IType type, final IMember member, final IMember sibling, final String newMemberName,
			final String newFieldPart, final String selectedText, final boolean copyBody) throws Exception {
		final IJavaElement nextElement = findNextElement(type, sibling);
		IMember newMember = null;
		final GlobalSettings globalSettings = getInstance();
		if (member.getElementType() != IJavaElement.METHOD || copyBody) {
			member.copy(type, nextElement, newMemberName, false, null);
		} else { // Create a new method signature manually
			final IMethod method = (IMethod) member;
			int count = 0;
			final StringBuilder methodSrc = new StringBuilder();

			if (isJunitTest(type)) {
				final StringBuilder methAnnotations = new StringBuilder();
				final IPreferenceStore preferences = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
				final String methodAnnotations = preferences.getString(P_JUNIT_METHOD_ANNOTATIONS);
				if (methodAnnotations != null) {
					for (String methodAnnotation : methodAnnotations.split(NEWLINE)) {
						methodAnnotation = replacePlaceHolder(methodAnnotation, "user", globalSettings.getUser());
						final String currDate = computeDate(globalSettings.getDateFormat());
						methodAnnotation = replacePlaceHolder(methodAnnotation, "curr_date", currDate);
						methodAnnotation = replacePlaceHolder(methodAnnotation, "today", currDate);
						methAnnotations.append(methodAnnotation + NEWLINE);
					}
				}
				methodSrc.append(methAnnotations.toString());
			} else {
				for (final IAnnotation annotation : method.getAnnotations()) {
					methodSrc.append("@" + annotation.getSource() + NEWLINE);
				}
			}

			if (isPrivate(method.getFlags())) {
				methodSrc.append("private");
			} else if (isProtected(method.getFlags())) {
				methodSrc.append("protected");
			} else if (isPublic(method.getFlags())) {
				methodSrc.append("public");
			}

			if (isStatic(method.getFlags())) {
				methodSrc.append(SPACE + "static");
			}
			methodSrc.append(SPACE + Signature.getSignatureSimpleName(method.getReturnType()));

			methodSrc.append(SPACE + newMemberName);

			methodSrc.append(LEFT_PAREN);
			for (final String paramName : method.getParameterNames()) {
				final String paramType = method.getParameterTypes()[count];
				methodSrc.append(Signature.getSignatureSimpleName(paramType) + SPACE + paramName);
				if (count < method.getParameterNames().length - 1) {
					methodSrc.append(COMMA + SPACE);
				}
				count++;
			}
			methodSrc.append(") {\n");
			methodSrc.append("}\n");
			newMember = type.createMethod(methodSrc.toString(), nextElement, false, null);
		}

		if (newMember == null || !newMember.exists()) {
			if (member instanceof IField) {
				newMember = type.getField(newMemberName);
			} else if (member instanceof IMethod) {
				newMember = type.getMethod(newMemberName, ((IMethod) member).getParameterTypes());
			}
		}

		ITextSelection sel = null;

		if (member.getElementName().equals(selectedText)) {
			sel = new TextSelection(newMember.getNameRange().getOffset(), newMember.getNameRange().getLength());
		} else {
			final String textToSelect = isEmpty(newFieldPart) ? COPYOF : newFieldPart;
			sel = new TextSelection(newMember.getNameRange().getOffset() + newMember.getElementName().indexOf(textToSelect),
					textToSelect.length());
		}

		this.editorPart.getEditorSite().getSelectionProvider().setSelection(sel);

		return newMember;
	}

	@Override
	protected boolean canActOnClassesOnly() {
		return false;
	}

	/**
	 *
	 * @param parentUnit
	 * @param selection
	 * @return
	 * @throws Exception
	 */
	protected IField findFieldOnCursorPosition(final IType type, final ITextSelection selection) throws Exception {

		for (final IField field : type.getFields()) {
			final int offset1 = field.getSourceRange().getOffset();
			final int offset2 = field.getNameRange().getOffset();
			final int offset = selection.getOffset();
			if (offset >= offset1 && offset <= offset2) {
				return field;
			}
		}
		return null;
	}

	/**
	 * This method determines if the selection is the method name or part of the
	 * method name.
	 *
	 * @param member
	 * @param selection
	 * @return
	 * @throws Exception
	 */
	private boolean isMemberNameSelected(final IMember member, final ITextSelection selection) throws Exception {
		final int offset = member.getNameRange().getOffset();
		final int length = member.getNameRange().getLength();
		final int selOffset = selection.getOffset();
		final int selLength = selection.getLength();
		return selOffset >= offset && selOffset <= offset + length && selLength <= length - (selOffset - offset);
	}

	/**
	 *
	 * @param member
	 * @param newNamePart
	 * @param selectedText
	 * @return
	 * @throws Exception
	 */
	private String constructNewMemberName(final IMember member, final String newNamePart, final ITextSelection selection) throws Exception {
		String newFieldName = null;
		final String selectedText = selection.getText();
		final String elementName = member.getElementName();

		if (elementName.equals(selectedText)) { // if the whole name is
			// selected.
			newFieldName = isEmpty(newNamePart) ? COPYOF + member.getElementName() : newNamePart;
		} else if (elementName.indexOf(selectedText) == elementName.lastIndexOf(selectedText)) {
			// if the part of the name is selected, but it does not repeat in
			// the elementName
			newFieldName = member.getElementName().replace(selectedText, isEmpty(newNamePart) ? COPYOF : newNamePart);
		} else {
			// if the part of the name is selected, but it repeats in the
			// elementName, e.g GLOBAL_ACTION, and A is selected.
			int offset = selection.getOffset();
			offset -= member.getNameRange().getOffset();
			newFieldName = elementName.substring(0, offset) + (isEmpty(newNamePart) ? COPYOF : newNamePart)
					+ elementName.substring(offset + selection.getLength(), elementName.length());
		}

		return newFieldName;
	}

	@Override
	protected boolean doesModify() {
		return true;
	}

	@Override
	protected boolean doesRequireMoreInfoFromUser() {
		return false;
	}

	@Override
	protected IType[] getTypesFromUser(final String title, final String description) throws Exception {
		return null;
	}

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement, final IType[] types) throws Exception {
		return;
	}

	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}

	/**
	 * @param compUnit
	 * @return
	 * @throws Exception
	 */
	protected CreateVariableData getCreateVariableData(final ICompilationUnit compUnit) throws Exception {
		this.createVariableData = new CreateVariableData();
		if (this.preferenceStore.contains(P_GETTER_SETTER_FORMAT)) {
			this.createVariableData.setGetterSetterFormat(GETTER_SETTER_FORMAT.getGetterSetterFormat(this.preferenceStore
					.getString(P_GETTER_SETTER_FORMAT)));
		}

		if (this.preferenceStore.contains(P_SETTER_VAR_PREFIX)) {
			this.createVariableData.setSetterVerPrefix(this.preferenceStore.getString(P_SETTER_VAR_PREFIX));
		}

		return this.createVariableData;
	}
}

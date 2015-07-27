/**
 *
 */
package org.fastcode.util;

import static org.eclipse.jdt.core.Flags.AccStatic;
import static org.eclipse.jdt.core.Flags.isPublic;
import static org.eclipse.jdt.core.Flags.isStatic;
import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.DOT_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.GLOBAL_ALWAYS_CREATE;
import static org.fastcode.common.FastCodeConstants.GLOBAL_NEVER_CREATE;
import static org.fastcode.common.FastCodeConstants.HASH;
import static org.fastcode.common.FastCodeConstants.MESSAGE_DIALOG_RETURN_YES;
import static org.fastcode.common.FastCodeConstants.NEW_IMPORT;
import static org.fastcode.preferences.PreferenceConstants.P_ASK_FOR_STATIC_IMPORT;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_STATIC_IMPORT_TYPE_CHOICE;
import static org.fastcode.setting.GlobalSettings.getInstance;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeConstants.CREATE_OPTIONS_CHOICE;
import org.fastcode.setting.GlobalSettings;

/**
 * @author Gautam
 *
 */
public class ImportUtil {

	/**
	 *
	 * @param type
	 * @param targetType
	 * @param selectedMember
	 * @return
	 * @throws JavaModelException
	 */
	public static String findImport(final IType type, final IImportDeclaration selectedImport, final ITextSelection selection)
			throws Exception {
		String newImport = null;
		final String impName = selectedImport.getElementName();
		final String selectedText = selection.getText();
		selection.getOffset();
		final int off = impName.lastIndexOf(DOT_CHAR);
		final String importedMember = impName.substring(off + 1);

		// typeToImport =
		// type.getCompilationUnit().getResource().getProject().getWorkspace().impName.subSequence(0,
		// off);
		String unSelected = null;
		if (!importedMember.equals(selectedText)) {
			unSelected = importedMember.substring(0, importedMember.length() - selectedText.length());
		}
		boolean getNext = false;
		String fieldToImport = null;
		final IType typeToImport = type.getCompilationUnit().getJavaProject().findType(impName.substring(0, off));
		for (final IField field : typeToImport.getFields()) {
			final String fieldName = field.getElementName();

			final IImportDeclaration importDeclaration = type.getCompilationUnit().getImport(
					typeToImport.getFullyQualifiedName() + DOT + fieldName);
			if (getNext && (importDeclaration == null || !importDeclaration.exists())) {
				if (unSelected == null || fieldName.startsWith(unSelected)) {
					fieldToImport = fieldName;
					break;
				}
			}
			// boolean match = unSelected == null ?
			// fieldName.equals(importedMember) :
			// fieldName.startsWith(unSelected);
			if (getNext) {
				continue;
			}
			getNext = fieldName.equals(importedMember);
		}
		newImport = typeToImport.getFullyQualifiedName() + DOT + (fieldToImport != null ? fieldToImport : NEW_IMPORT);
		return newImport;
	}

	/**
	 *
	 * @return
	 */
	public static boolean doStaticImport() {
		final GlobalSettings globalSettings = getInstance();
		boolean creatStaticImport = true;

		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		final CREATE_OPTIONS_CHOICE staticImportTypeChoice = globalSettings.getCreateStaticImport();

		if (staticImportTypeChoice == CREATE_OPTIONS_CHOICE.NEVER_CREATE) {
			creatStaticImport = false;
		} else if (staticImportTypeChoice == CREATE_OPTIONS_CHOICE.ASK_TO_CREATE) {

			final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
			final MessageDialogWithToggle dialogWithToggle = MessageDialogWithToggle.openYesNoQuestion(shell, "Static Import",
					"This class contains one or more static members, Would you like to create a static import?", "Remember Decision",
					false, preferenceStore, P_ASK_FOR_STATIC_IMPORT);
			final boolean remDecs = dialogWithToggle.getToggleState();
			String value = null;
			if (dialogWithToggle.getReturnCode() != MESSAGE_DIALOG_RETURN_YES) {
				creatStaticImport = false;
				value = GLOBAL_NEVER_CREATE;
			} else {
				value = GLOBAL_ALWAYS_CREATE;
			}
			if (remDecs) {
				preferenceStore.setValue(P_GLOBAL_STATIC_IMPORT_TYPE_CHOICE, value);
			}
		}

		return creatStaticImport;
	}

	/**
	 *
	 * @param type
	 * @return
	 * @throws JavaModelException
	 */
	public static IMember[] retriveStaticMembers(final IType type) throws JavaModelException {

		if (type.getFields().length == 0 && type.getMethods().length == 0) {
			return null;
		}

		// IMember[] members = new IMember[type.getFields().length +
		// type.getMethods().length];
		final List<IMember> members = new ArrayList<IMember>();
		//int count = 0;
		if (type.isInterface()) {
			for (final IField field : type.getFields()) {
				members.add(field);
				//count++;
			}
		} else {
			for (final IField field : type.getFields()) {
				if (isStatic(field.getFlags()) && isPublic(field.getFlags())) {
					members.add(field);
					//count++;
				}
			}
		}
		/*if (type.isInterface()) { // interface cannot contain static methods, so
									// return here.
			return members.toArray(new IMember[0]);
		}*/

		for (final IMethod method : type.getMethods()) {
			if (isStatic(method.getFlags()) && isPublic(method.getFlags())) {
				members.add(method);
				//count++;
			}
		}
		return members.toArray(new IMember[0]);
	}

	/**
	 *
	 * @param type
	 * @param importDeclaration
	 * @return
	 * @throws Exception
	 */
	public static IImportDeclaration findNextImportDeclaration(final IType type, final IImportDeclaration importDeclaration)
			throws Exception {
		boolean found = false;
		for (final IImportDeclaration declaration : type.getCompilationUnit().getImports()) {
			if (found) {
				return declaration;
			}
			if (declaration.equals(importDeclaration)) {
				found = true;
			}
		}
		return null;
	}

	/**
	 *
	 * @param compUnit
	 * @param type
	 * @param fileExtension
	 *
	 * @return
	 * @throws Exception
	 */
	public static IImportDeclaration createImport(final ICompilationUnit compUnit, final IType type, final String fileExtension)
			throws Exception {

		if (type == null || !type.exists()) {
			throw new Exception("type does not exit");
		}

		final String pkg = type.getPackageFragment().getElementName();
		final String defaultPackageProperty = "DEFAULT_PACKAGES_" + fileExtension.toUpperCase();
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String defaultPackages[] = globalSettings.getPropertyValue(defaultPackageProperty, EMPTY_STR).split(COMMA);
		for (final String packge : defaultPackages) {
			if (packge.equals(pkg)) {
				return null;
			}
		}

		for (final IImportDeclaration importDeclaration : compUnit.getImports()) {
			final String importName = importDeclaration.getElementName();
			final int off = importName.lastIndexOf(DOT);
			if (type.getElementName().equals(importName.substring(off + 1))) {
				return importDeclaration;
			}
		}

		IImportDeclaration imp = compUnit.getImport(pkg + DOT + ASTERISK);
		if (imp != null && imp.exists()) {
			return imp;
		}

		imp = compUnit.getImport(type.getFullyQualifiedName());
		if (imp != null && imp.exists()) {
			return imp;
		}

		return compUnit.createImport(type.getFullyQualifiedName(), null, null);
	}

	/**
	 * @param classToImport
	 * @param compUnit
	 * @throws Exception
	 */
	public static void createImportOfMethodAndField(String classToImport, final ICompilationUnit compUnit) throws Exception {
		final String targetClass = classToImport.substring(0, classToImport.indexOf(HASH)).trim();
		final String memberName = classToImport.substring(classToImport.indexOf(HASH) + 1, classToImport.length()).trim();
		/*final IType targetClassType = compUnit.getJavaProject().findType(targetClass.trim());
		if (targetClassType == null || !targetClassType.exists()) {
			throw new Exception("Target class:   " + targetClass + "   specified in the XML is not found.Please choose another class");
		}*/
		classToImport = targetClass + DOT + memberName;
		compUnit.createImport(classToImport, null, AccStatic, null);
	}
}

package org.fastcode.popup.actions.processrules;

import static org.eclipse.jdt.ui.JavaUI.openInEditor;
import static org.eclipse.jdt.ui.JavaUI.revealInEditor;
import static org.eclipse.jface.dialogs.MessageDialog.openWarning;
import static org.fastcode.common.FastCodeConstants.ACTON_SELECTED_STR;
import static org.fastcode.common.FastCodeConstants.CREATE_SIMILAR_CLASSES_ACTION;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_INPUT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PACKAGE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PROJECT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_RESULT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_SOURCE;
import static org.fastcode.common.FastCodeConstants.SEMICOLON;
import static org.fastcode.util.SourceUtil.getJavaProject;
import static org.fastcode.util.SourceUtil.getPackageFragmentFromWorkspace;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.replaceSpecialChars;

import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.fastcode.common.FastCodeType;

public class CreateInterfaceAction extends FastCodeRuleProcessorAction {

	/**
	 * @param name
	 * @param placeHolders
	 * @param contextMap
	 * @throws Exception
	 */
	public void createInterface(final String name, final Map<String, Object> placeHolders, final Map<String, Object> contextMap)
			throws Exception {
		IPackageFragment interfacePackage = null;
		final IJavaProject javaProject = null;

		final String interfaceSnippet = evaluateByVelocity((String) placeHolders.get(PLACEHOLDER_SOURCE), placeHolders); // getValueForPlaceHolder((String)
																															// placeHolders.get(PLACEHOLDER_SOURCE),
																															// placeHolders);
																															// //
		if (isEmpty(interfaceSnippet)) {
			throw new Exception("Blank snippet, source may be invalid.");
		}

		final String interfaceSrc = replaceSpecialChars(interfaceSnippet);

		/*
		 * if(target != null){ classPackage =
		 * getPackageFragmentFromWorkspace(target); } else
		 */
		if (placeHolders.get(PLACEHOLDER_PACKAGE) != null) {
			final String packageName = getValueForPlaceHolder((String) placeHolders.get(PLACEHOLDER_PACKAGE), placeHolders);
			interfacePackage = getPackageFragmentFromWorkspace(packageName);
		} else if (placeHolders.get(PLACEHOLDER_PROJECT) != null) {
			interfacePackage = getPackageFromUser(getJavaProject((String) placeHolders.get(PLACEHOLDER_PROJECT)));
		}

		if (interfacePackage == null) {
			interfacePackage = getPackageFromUser(getWorkingJavaProject());
		}

		if (interfacePackage == null) {
			MessageDialog.openError(new Shell(), "Error", "Cannot proceed without package...exiting..");
			return;
		}

		final String packageDeclaration = "package " + interfacePackage.getElementName() + SEMICOLON + NEWLINE + NEWLINE;
		final String interfaceName = evaluateByVelocity(name, placeHolders); // getValueForPlaceHolder(name,
																				// placeHolders);
																				// //
		IType type = interfacePackage.getJavaProject().findType(interfacePackage.getElementName() + DOT + interfaceName);

		ICompilationUnit newInterface = null;
		if (type == null || !type.exists()) {
			newInterface = interfacePackage.createCompilationUnit(interfaceName + DOT + JAVA_EXTENSION, packageDeclaration + interfaceSrc,
					false, null);
			type = newInterface.findPrimaryType();
			if ((String) placeHolders.get(PLACEHOLDER_RESULT) != null) {
				placeHolders.put((String) placeHolders.get(PLACEHOLDER_RESULT), new FastCodeType(newInterface.findPrimaryType()
						.getFullyQualifiedName()));
				contextMap.put((String) placeHolders.get(PLACEHOLDER_RESULT), new FastCodeType(newInterface.findPrimaryType()
						.getFullyQualifiedName()));
			}

			this.fastCodeTypeSet.add(new FastCodeType(newInterface.findPrimaryType()));
		} else {
			openWarning(new Shell(), "Warning", "Interface, " + interfaceName + ", already exist. Will Skip and proceed.");
			if ((String) placeHolders.get(PLACEHOLDER_RESULT) != null) {
				placeHolders.put((String) placeHolders.get(PLACEHOLDER_RESULT), new FastCodeType(type));
				contextMap.put((String) placeHolders.get(PLACEHOLDER_RESULT), new FastCodeType(type));
			}
		}

		if (placeHolders.get(ACTON_SELECTED_STR) != CREATE_SIMILAR_CLASSES_ACTION) {
			final IEditorPart javaEditor = openInEditor(newInterface);
			revealInEditor(javaEditor, (IJavaElement) newInterface.findPrimaryType());
		}

		placeHolders.remove(PLACEHOLDER_SOURCE);
		placeHolders.remove(PLACEHOLDER_RESULT);
		placeHolders.remove(PLACEHOLDER_INPUT);

		/*
		 * if (!MessageDialog.openConfirm(new Shell(), "",
		 * "Do you want to create default methods?")) { return; }
		 *
		 * StringBuilder inBuiltMethod = new StringBuilder(); for(final String
		 * method : inbuiltMethods) { inBuiltMethod =
		 * EMPTY_STR.equals(inBuiltMethod.toString()) ?
		 * inBuiltMethod.append(getValueForPlaceHolder(method, placeHolders)) :
		 * inBuiltMethod.append(COMMA + getValueForPlaceHolder(method,
		 * placeHolders)); }
		 *
		 * final StringSelectionDialog selectionDialog = new
		 * StringSelectionDialog(new Shell(), "In build methods",
		 * "Choose methods ", inBuiltMethod.toString().split(COMMA), true); if
		 * (selectionDialog.open() == CANCEL) { return; }
		 *
		 * final Object[] methodsToCreate = selectionDialog.getResult();
		 *
		 * for (final Object method : methodsToCreate) { //final IMethod[]
		 * existingMethods = type.getMethods(); try { type.createMethod((String)
		 * method, null, false, null); } catch (final Exception ex) {
		 * MessageDialog.openError(new Shell(),"Error generating method",
		 * ex.getMessage() + "\nWill move on."); continue; } }
		 * placeHolders.put("defaultMethods", TRUE_STR);
		 */
	}

}

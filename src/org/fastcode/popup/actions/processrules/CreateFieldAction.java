package org.fastcode.popup.actions.processrules;

import static org.eclipse.jdt.ui.JavaUI.openInEditor;
import static org.eclipse.jdt.ui.JavaUI.revealInEditor;
import static org.eclipse.jface.window.Window.CANCEL;
import static org.fastcode.common.FastCodeConstants.ACTON_SELECTED_STR;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.CREATE_SIMILAR_CLASSES_ACTION;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.HASH_CHAR;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_INPUT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PROJECT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_RESULT;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_SNIPPET;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_SOURCE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_TARGET;
import static org.fastcode.common.FastCodeConstants.QUOTE_STR;
import static org.fastcode.common.FastCodeConstants.TAB;
import static org.fastcode.common.FastCodeConstants.TRUE_STR;
import static org.fastcode.util.SourceUtil.getJavaProject;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.replaceSpecialChars;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.fastCode.xsd.fastCodeRules.ActionsDocument.Actions.Action;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.fastcode.common.FastCodeField;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.StringSelectionDialog;

public class CreateFieldAction extends FastCodeRuleProcessorAction {

	private final Map<String, IMethod> methodMap = new HashMap<String, IMethod>();

	/**
	 * @param placeHolders
	 * @param contextMap
	 * @throws Exception
	 */
	public void createField(final Map<String, Object> placeHolders, final Map<String, Object> contextMap) throws Exception {
		if (((Action)placeHolders.get(ACTON_SELECTED_STR)).getName().equals("CREATE_STATIC_FIELD")) {
			final Object fieldObj = placeHolders.get("fieldType");
			if (fieldObj != null) {
				final FastCodeType fastCodeType = (FastCodeType) fieldObj;
				final String fieldType = fastCodeType.getName();

				if (fieldType.equals("String")) {
					final String value = (String) placeHolders.get("value");
					placeHolders.put("value", QUOTE_STR + value + QUOTE_STR);
				} /*else if (fieldType.equals("Char")) {

					}*/
			}
		}
		final String snppt = evaluateByVelocity((String) placeHolders.get(PLACEHOLDER_SOURCE), placeHolders); //getValueForPlaceHolder((String) placeHolders.get(PLACEHOLDER_SOURCE), placeHolders); //
		if (isEmpty(snppt)) {
			clearPlaceHolderMap(placeHolders);
			throw new Exception("Blank snippet, source may be invalid.");
		}
		final String fieldSrc = replaceSpecialChars(snppt);
		IJavaProject workingJavaProject = null;
		if (placeHolders.get(PLACEHOLDER_PROJECT) != null) {
			workingJavaProject = getJavaProject((String) placeHolders.get(PLACEHOLDER_PROJECT));
		}

		if (workingJavaProject == null || !workingJavaProject.exists()) {
			workingJavaProject = getWorkingJavaProject();
		}

		if (workingJavaProject == null || !workingJavaProject.exists()) {
			MessageDialog.openError(new Shell(), "Error", "Cannot proceed without working java project...exiting..");
			clearPlaceHolderMap(placeHolders);
			return;
		}

		/*if(!isEmpty(target) && workingJavaProject == null ) {
			workingJavaProject = getWorkingJavaProject();
		}*/

		IType type = null;
		IField field = null;

		final Object targetObj = placeHolders.get(PLACEHOLDER_TARGET);

		if (targetObj == null) {

		}
		String targetStr = null;
		FastCodeType targetFCT = null;

		if (placeHolders.get(PLACEHOLDER_SNIPPET) != null && TRUE_STR.equals(placeHolders.get(PLACEHOLDER_SNIPPET))) {
			String targetClassStr = null;
			String targetMethodStr = null;
			if (targetObj instanceof String) {
				targetStr = (String) targetObj;
				targetClassStr = targetStr.substring(0, targetStr.lastIndexOf(HASH_CHAR));
				targetMethodStr = targetStr.substring(targetStr.lastIndexOf(HASH_CHAR) + 1);
			}
			type = workingJavaProject.findType(targetClassStr);

			if (type == null) {
				type = getTypeFromUser("Target class specified in the XML is not found.Please choose another class.", "Select Class",
						"Select the Target class");
				if (type == null) {
					//show message??
					clearPlaceHolderMap(placeHolders);
					return;
				}
				placeHolders.put(PLACEHOLDER_TARGET, type.getFullyQualifiedName());
				placeHolders.put("targetClass", type.getFullyQualifiedName());
			}

			final IMethod[] methods = type.getMethods();
			IMethod methodSelected = null;
			StringBuilder methodsInType = new StringBuilder();
			final Map<String, IMethod> methodMap = new HashMap<String, IMethod>();

			if (methods != null) {
				int count = 0;
				for (final IMethod method : methods) {
					if (method.getElementName().endsWith(targetMethodStr)) {
						count++;
						methodSelected = method;
					}
					methodMap.put(method.getElementName(), method);
					methodsInType = EMPTY_STR.equals(methodsInType.toString()) ? methodsInType.append(method.getElementName())
							: methodsInType.append(COMMA + method.getElementName());
				}

				if (count == 0) {
					MessageDialog.openError(new Shell(), "Error",
							"Method " + targetMethodStr + " does not exist in the class " + type.getElementName()
									+ ". Please choose another method.");

					final StringSelectionDialog selectionDialog = new StringSelectionDialog(new Shell(), "Choose Method",
							"Choose a method", methodsInType.toString().split(COMMA), false);
					selectionDialog.setTitle("Select Method");
					selectionDialog.setMessage("Select the method");

					if (selectionDialog.open() == CANCEL) {
						clearPlaceHolderMap(placeHolders);
						return;
					}
					methodSelected = methodMap.get(selectionDialog.getResult()[0]);

				} else if (count > 1) {
					//show method selection dialog  methodSelected = selectionDialog.getSelection();
				}

				final String methodSource = methodSelected.getSource();
				final int openParanthesisPos = methodSource.indexOf("{");
				final StringBuilder methodBuilder = new StringBuilder();
				methodBuilder.append(methodSource.substring(0, openParanthesisPos));
				methodBuilder.append(LEFT_CURL + NEWLINE + TAB + fieldSrc + NEWLINE);
				methodBuilder.append(methodSource.substring(openParanthesisPos + 1, methodSource.length()));

				methodSelected.delete(false, null);
				final IMethod newMethod = type.createMethod(methodBuilder.toString(), null, false, null);

				if (newMethod == null || !newMethod.exists()) {
					//show error
				}
			}
		} else {
			if (targetObj instanceof String) {
				targetStr = (String) targetObj;
				type = workingJavaProject.findType(targetStr);
			} else if (targetObj instanceof FastCodeType) {
				targetFCT = (FastCodeType) targetObj;
				type = workingJavaProject.findType(targetFCT.getFullyQualifiedName());
			}
			//type = workingJavaProject.findType(targetFCT.getFullyQualifiedName());
			if (type == null) {
				type = getTypeFromUser("Target class specified in the XML is not found.Please choose another class.", "Select Class",
						"Select the Target class");
				if (type == null) {
					//show message??
					clearPlaceHolderMap(placeHolders);
					return;
				}
				placeHolders.put(PLACEHOLDER_TARGET, type.getFullyQualifiedName());
				placeHolders.put("targetClass", type.getFullyQualifiedName());
			}

			field = type.createField(fieldSrc, null, false, new NullProgressMonitor());
		}
		if ((String) placeHolders.get(PLACEHOLDER_RESULT) != null && field != null) {
			placeHolders.put((String) placeHolders.get(PLACEHOLDER_RESULT), new FastCodeField(field));
			contextMap.put((String) placeHolders.get(PLACEHOLDER_RESULT), new FastCodeField(field));
		}

		if (field != null) {
			this.fastCodeFieldSet.add(new FastCodeField(field));
		}

		if (placeHolders.get(ACTON_SELECTED_STR) != CREATE_SIMILAR_CLASSES_ACTION) {
			final IEditorPart javaEditor = openInEditor(type.getCompilationUnit());
			revealInEditor(javaEditor, (IJavaElement) type);
		}

		//placeHolders.put(PLACEHOLDER_CLASS, new FastCodeType(type.getFullyQualifiedName()));
		//doImport(type.getElementName(), placeHolders);
		clearPlaceHolderMap(placeHolders);
	}

	/**
	 * @param placeHolders
	 */
	private void clearPlaceHolderMap(final Map<String, Object> placeHolders) {
		placeHolders.remove(PLACEHOLDER_SOURCE);
		placeHolders.remove(PLACEHOLDER_RESULT);
		placeHolders.remove(PLACEHOLDER_INPUT);
		placeHolders.remove(PLACEHOLDER_TARGET);
	}

}

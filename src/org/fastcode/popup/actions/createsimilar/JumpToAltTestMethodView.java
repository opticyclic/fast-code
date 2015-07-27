/**
 *
 */
package org.fastcode.popup.actions.createsimilar;

import static org.eclipse.jdt.core.IJavaElement.METHOD;
import static org.eclipse.jdt.ui.JavaUI.revealInEditor;
import static org.eclipse.jface.dialogs.MessageDialog.openInformation;
import static org.fastcode.util.JUnitCreator.findTestMethods;
import static org.fastcode.util.JUnitUtil.findPossibleMethodForTest;
import static org.fastcode.util.JUnitUtil.isJunitTest;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.common.MethodSelectionDialog;
import org.fastcode.util.MessageUtil;

/**
 * @author Gautam
 *
 */
public class JumpToAltTestMethodView extends JumpToJUnitTestView implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement) throws Exception {

		if (!isJunitTest(compUnit.findPrimaryType())) {
			throw new Exception("This is not a Junit Test.");
		}

		final int elementType = javaElement == null ? 0 : javaElement.getElementType();
		if (elementType != METHOD) {
			throw new Exception("You did not select a method.");
		}

		final IMethod method = findPossibleMethodForTest((IMethod) javaElement);

		if (method == null) {
			return;
		}

		IMethod testMethod = null;

		final IMethod[] allExstTestMethods = findTestMethods(method.getCompilationUnit().findPrimaryType(), method, null);

		IMethod[] exstTestMethods = null;

		if (allExstTestMethods != null && allExstTestMethods.length > 2) {
			exstTestMethods = new IMethod[allExstTestMethods.length - 1];
		} else if (allExstTestMethods != null && allExstTestMethods.length == 2) {
			for (final IMethod altTestMethod : allExstTestMethods) {
				if (!altTestMethod.equals(javaElement)) {
					revealInEditor(this.editorPart, (IJavaElement) altTestMethod);
					return;
				}
			}
		} else {
			openInformation(null, "Warning", "No alternate test was found.");
			return;
		}

		int cnt = 0;
		for (final IMethod altTestMethod : allExstTestMethods) {
			if (!altTestMethod.equals(javaElement)) {
				exstTestMethods[cnt++] = altTestMethod;
			}
		}
		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		final MethodSelectionDialog elementListSelectionDialog = new MethodSelectionDialog(shell, "Select Test Method",
				"Multiple tests found for the method you selected, " + "please select one from the list below.", exstTestMethods, false);

		elementListSelectionDialog.open();
		if (elementListSelectionDialog.getResult() == null || elementListSelectionDialog.getResult().length == 0) {
			return;
		}
		testMethod = (IMethod) elementListSelectionDialog.getResult()[0];

		revealInEditor(this.editorPart, (IJavaElement) testMethod);
	}

}

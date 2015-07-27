/**
 *
 */
package org.fastcode.popup.actions.createsimilar;

import static org.eclipse.jdt.core.Flags.isPrivate;
import static org.eclipse.jdt.core.IJavaElement.METHOD;
import static org.eclipse.jdt.core.IJavaElement.TYPE;
import static org.eclipse.jface.dialogs.MessageDialog.openQuestion;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * @author Gautam
 *
 */
public class CreateJUnitTestView extends CreateJUnitTestActionSupport implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void runAction1(final ICompilationUnit compUnit, final IJavaElement javaElement) throws Exception {

		final IJavaElement methodSelected = findSelectedJavaElement(compUnit);
		if (methodSelected != null) {
			super.runAction(compUnit, methodSelected);
		}
	}

	/**
	 * @param compUnit
	 * @throws Exception
	 *
	 */
	@Override
	public IJavaElement findSelectedJavaElement(final ICompilationUnit compUnit) throws Exception {
		IMethod methodSelected = null;
		if (this.selection != null && this.selection instanceof ITextSelection) {
			final IJavaElement element = compUnit.getElementAt(((ITextSelection) this.selection).getOffset());
			if (element != null && element.getElementType() == TYPE) {
				return element;
			} else if (element != null && element.getElementType() == METHOD) {
				methodSelected = (IMethod) element;
			}
		}
		if (methodSelected != null && (methodSelected.isConstructor() || isPrivate(methodSelected.getFlags()))) {
			if (!openQuestion(this.editorPart.getSite().getShell(), "Warning",
					"Method you selected is either a constructor or a private method. Do you still want to continue?")) {
				return null;
			}
		}
		return methodSelected;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}

}

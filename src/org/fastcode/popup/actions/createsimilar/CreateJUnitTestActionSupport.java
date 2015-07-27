/**
 *
 */
package org.fastcode.popup.actions.createsimilar;

import static org.eclipse.jdt.ui.JavaUI.openInEditor;
import static org.eclipse.jdt.ui.JavaUI.revealInEditor;
import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.fastcode.common.FastCodeConstants.EXTENSION_OTHER;
import static org.fastcode.util.JUnitCreator.generateTest;
import static org.fastcode.util.JUnitUtil.isJunitEnabled;
import static org.fastcode.util.JUnitUtil.isJunitTest;
import static org.fastcode.util.SourceUtil.checkForErrors;
import static org.fastcode.util.SourceUtil.getRepositoryServiceClass;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.fastcode.AbstractActionSupport;
import org.fastcode.util.MessageUtil;
import org.fastcode.util.RepositoryService;

/**
 * @author Gautam
 *
 */
public abstract class CreateJUnitTestActionSupport extends AbstractActionSupport {

	protected IMember	testMember;
	protected String	errorMessage;

	/**
	 *
	 * @param compUnit
	 * @param method
	 * @throws Exception
	 */
	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement) throws Exception {
		if (javaElement == null || !(javaElement instanceof IMember)) {
			return;
		}
		final IMethod method = javaElement instanceof IMethod ? (IMethod) javaElement : null;
		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		if (checkForErrors(compUnit.getResource())
				&& MessageDialog.openQuestion(shell, "Error",
						"There seems to be some problems associated with " + compUnit.getElementName()
								+ ". It is better to fix those problems and try again. Want to abort?")) {
			return;
		}

		if (!isJunitEnabled(compUnit.findPrimaryType())) {
			openError(shell, "Error", "Junit has not been added as library.");
			return;
		}

		if (isJunitTest(compUnit.findPrimaryType())) {
			openError(shell, "Error", "This is already a Junit Test.");
			return;
		}

		final IType type = compUnit.findPrimaryType();
		final IMethod[] methods;

		/*if (method == null) {
			final List<IMethod> methodsList = new ArrayList<IMethod>();
			for (final IMethod meth : type.getMethods()) {
				if (meth.isConstructor() || isPrivate(meth.getFlags())) {
		//					if (!openQuestion(new Shell(), "Warning!", "It is not a public method, do you wish to continue?" +
		//																				"Press Yes to continue, No to bail out.")) {
		//					}
					continue;
				}
				methodsList.add(meth);
			}
			final IMember[] selectedMethods = methodsList.isEmpty() ? null : getSelectedMembers(IJavaElement.METHOD, methodsList.toArray(new IMethod[0]), "Unit Test", true);

			methods = (selectedMethods == null) || (selectedMethods.length == 0)  ? null : (IMethod[]) selectedMethods;
		} else {
			methods = new IMethod[1];
			methods[0] = method;
		}*/

		this.testMember = null;
		this.errorMessage = null;

		this.testMember = generateTest(type, this.commitMessage, method);

		if (this.errorMessage != null) {
			MessageDialog.openError(shell, "Error", "There was some problems in creating unit test " + this.errorMessage);
		}

		if (this.testMember == null || this.testMember != null && this.testMember.equals(method)) {
			return;
		}

		final IEditorPart javaEditor = openInEditor(this.testMember.getCompilationUnit());

		revealInEditor(javaEditor, (IJavaElement) this.testMember);
		if (this.testMember instanceof IMethod) {
			final IMethod testMethod = (IMethod) this.testMember;
			if (testMethod.getElementName().endsWith(EXTENSION_OTHER)) {
				final IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				final int offset = testMethod.getNameRange().getOffset();
				final ISelection selection = new TextSelection(offset + testMethod.getElementName().lastIndexOf(EXTENSION_OTHER),
						EXTENSION_OTHER.length());
				editorPart.getEditorSite().getSelectionProvider().setSelection(selection);
			}
		}
		if (this.autoCheckinEnabled) {
			if (!this.commitMessage.isEmpty()) {
				final RepositoryService checkin = getRepositoryServiceClass();
				checkin.commitToRepository(this.commitMessage, true);
			}
		}
	}

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement, final IType[] types) throws Exception {
		// Do Nothing
	}

	@Override
	protected boolean canActOnClassesOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected IType[] getTypesFromUser(final String title, final String description) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean doesModify() {
		return false;
	}

	@Override
	protected boolean doesRequireMoreInfoFromUser() {
		return false;
	}
}

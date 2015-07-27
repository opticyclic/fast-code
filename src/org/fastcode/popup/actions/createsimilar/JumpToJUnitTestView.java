/**
 *
 */
package org.fastcode.popup.actions.createsimilar;

import static org.eclipse.jdt.core.IJavaElement.METHOD;
import static org.eclipse.jdt.core.IJavaElement.TYPE;
import static org.eclipse.jdt.ui.JavaUI.openInEditor;
import static org.eclipse.jdt.ui.JavaUI.revealInEditor;
import static org.eclipse.jface.dialogs.MessageDialog.openQuestion;
import static org.fastcode.util.JUnitCreator.findTestMethods;
import static org.fastcode.util.JUnitCreator.generateTest;
import static org.fastcode.util.JUnitUtil.findTestUnit;
import static org.fastcode.util.JUnitUtil.isJunitTest;
import static org.fastcode.util.SourceUtil.findSuperInterfaceType;
import static org.fastcode.util.SourceUtil.getRepositoryServiceClass;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.AbstractActionSupport;
import org.fastcode.common.MethodSelectionDialog;
import org.fastcode.preferences.JunitPreferences;
import org.fastcode.util.JUnitUtil;
import org.fastcode.util.JunitPreferencesAndType;
import org.fastcode.util.RepositoryService;

/**
 * @author Gautam
 *
 */
public class JumpToJUnitTestView extends AbstractActionSupport implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/**
	 * @param compUnit
	 * @param javaElement
	 *
	 */
	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement) throws Exception {

		if (isJunitTest(compUnit.findPrimaryType())) {
			throw new Exception("This is already a Junit Test.");
		}

		final JunitPreferencesAndType junitPreferencesAndType = JUnitUtil.findJunitPreferences(compUnit.findPrimaryType());
		if (junitPreferencesAndType == null) {
			throw new Exception("This class you selected does not match any profile. "
					+ "Please configure it first by going to Window -> Preference -> Fast Code Pereference.");
		}

		final JunitPreferences junitPreferences = junitPreferencesAndType.getJunitPreferences();
		ICompilationUnit testUnit = findTestUnit(compUnit.findPrimaryType(), junitPreferences);

		IType typeToWorkOn = junitPreferencesAndType.getType();

		// find the interface if unit test is null
		if (testUnit == null && typeToWorkOn.isClass()) {
			final IType tmpType = findSuperInterfaceType(typeToWorkOn);
			if (tmpType != null) {
				final JunitPreferences junitPref = JunitPreferences.getInstance(tmpType);
				if (junitPref != null) {
					testUnit = findTestUnit(tmpType, junitPref);
					if (testUnit != null) {
						//						junitPreferences = junitPref;
						typeToWorkOn = tmpType;
					}
				}
			}
		}

		IMethod[] exstTestMethods = null;
		boolean newTestCreated = false;
		IMember testMethod = null;

		final int elementType = javaElement == null ? 0 : javaElement.getElementType();
		if (testUnit == null || !testUnit.exists()) {
			final boolean answer = openQuestion(new Shell(), "Warning", "Junit for the class you "
					+ "selected does not exist, Would you like to create one?");
			if (answer) {
				testMethod = generateTest(compUnit.findPrimaryType(), this.commitMessage, javaElement == null || elementType == TYPE ? null
						: (IMethod) javaElement);
				testUnit = testMethod.getCompilationUnit();
				newTestCreated = true;
			} else {
				return;
			}
		}

		if (elementType == METHOD && !newTestCreated) {
			exstTestMethods = findTestMethods(typeToWorkOn, (IMethod) javaElement, null);

			if (exstTestMethods == null || exstTestMethods.length == 0) {
				final boolean answer = openQuestion(new Shell(), "Warning", "Junit for the method you selected "
						+ "does not exist, Would you like to create one?");
				if (!answer) {
					return;
				}
				testMethod = generateTest(compUnit.findPrimaryType(), this.commitMessage, (IMethod) javaElement);

				if (this.autoCheckinEnabled) {
					if (!this.commitMessage.isEmpty()) {
						final RepositoryService checkin = getRepositoryServiceClass();
						checkin.commitToRepository(this.commitMessage, true);
					}
				}
			} else if (exstTestMethods.length == 1) {
				testMethod = exstTestMethods[0];
			} else {
				final MethodSelectionDialog elementListSelectionDialog = new MethodSelectionDialog(new Shell(), "Select Test Method",
						"Multiple tests found for the method you selected, " + "please select one from the list below.", exstTestMethods,
						false);

				elementListSelectionDialog.open();
				if (elementListSelectionDialog.getResult() == null || elementListSelectionDialog.getResult().length == 0) {
					return;
				}
				testMethod = (IMethod) elementListSelectionDialog.getResult()[0];
			}
		}

		if (testUnit != null && testUnit.exists()) {
			final IEditorPart javaEditor = openInEditor(testUnit);
			if (testMethod != null && testMethod.exists()) {
				revealInEditor(javaEditor, (IJavaElement) testMethod);
			} else {
				revealInEditor(javaEditor, (IJavaElement) testUnit.findPrimaryType());
			}
		}
	}

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement, final IType[] types) throws Exception {

	}

	@Override
	protected boolean canActOnClassesOnly() {
		return false;
	}

	@Override
	protected boolean doesModify() {
		return false;
	}

	@Override
	protected boolean doesRequireMoreInfoFromUser() {
		return false;
	}

	@Override
	protected IType[] getTypesFromUser(final String title, final String description) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}

package org.fastcode.popup.actions.createsimilar;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.UNIT_TEST_FOLDER;
import static org.fastcode.util.TemplateUtil.findOrcreateTemplate;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.AbstractActionSupport;

public abstract class UnitTestImportExportView extends AbstractActionSupport implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/*
	 * private static final int ALL = 0; public final Map<String, String>
	 * unitTestFiles = new LinkedHashMap<String, String>();
	 */

	String	unitTestFiles	= "UnitTestPreferences.xml";

	public UnitTestImportExportView() {
		// this.unitTestFiles.put("UnitTestPreferences.xml", "");
	}

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement, final IType[] types) throws Exception {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.fastcode.AbstractActionSupport#runAction(org.eclipse.jdt.core.
	 * ICompilationUnit, org.eclipse.jdt.core.IJavaElement)
	 */
	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement) throws Exception {
		processUnitTest(this.unitTestFiles, EMPTY_STR, UNIT_TEST_FOLDER);

		/*MessageDialog.openInformation(new Shell(), "Success", getType() + " was successfully completed to Fast Code Eclipse Plugin/"
				+ UNIT_TEST_FOLDER + " folder.");*/
	}

	protected abstract String getType();

	/**
	 * @param fileName
	 * @param templatePreferenceName
	 * @throws Exception
	 */
	protected void processUnitTest(final String fileName, final String templatePreferenceName, final String folderName) throws Exception {
		final IFile file = findOrcreateTemplate(fileName, folderName);
		if (file == null) {
			throw new Exception("Unknown Exception : Please try again.");
		}
		if (!file.isSynchronized(0)) {
			throw new Exception(fileName + " is not Synchronized, please refresh and try again.");
		}

	}

	@Override
	protected boolean requireJavaClass() {
		return false;
	}

	@Override
	protected IType[] getTypesFromUser(final String title, final String description) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean doesModify() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean doesRequireMoreInfoFromUser() {
		// TODO Auto-generated method stub
		return false;
	}

}

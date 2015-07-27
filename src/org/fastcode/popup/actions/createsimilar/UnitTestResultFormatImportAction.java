package org.fastcode.popup.actions.createsimilar;

import static org.fastcode.common.FastCodeConstants.UNIT_TEST_FOLDER;
import static org.fastcode.util.SourceUtil.checkForErrors;
import static org.fastcode.util.TemplateUtil.findOrcreateTemplate;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.AbstractActionSupport;
import org.fastcode.util.FastCodeUtil;
import org.fastcode.util.UnitTestReturnFormatSettings;

public class UnitTestResultFormatImportAction extends AbstractActionSupport implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	final String	unitTestResultFormatFile	= "unit-test-result-format.xml";

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement) throws Exception {

		final IFile file = findOrcreateTemplate(this.unitTestResultFormatFile, UNIT_TEST_FOLDER);

		if (file == null || !file.exists()) {
			throw new Exception("File " + this.unitTestResultFormatFile + " does not exist, please export and try again.");
		}

		if (checkForErrors(file)) {
			throw new Exception("File " + this.unitTestResultFormatFile + " has some errors, please fix them try again.");
		}

		final InputStream inputStream = file.getContents();
		if (inputStream == null || inputStream.available() == 0) {
			return;
		}
		try {
			if (inputStream.available() < 100) {
				throw new Exception("File " + this.unitTestResultFormatFile + " is too small. Please check the file and try again.");
			}

			//Activator.getDefault().getBundle()
			//final InputStream outputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + this.unitTestResultFormatFile), false);
			final UnitTestReturnFormatSettings unitTestReturnFormatSettings = UnitTestReturnFormatSettings.getInstance();
			unitTestReturnFormatSettings.getResultFormatMap().clear();

		} finally {
			FastCodeUtil.closeInputStream(inputStream);
		}

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

	@Override
	protected IType[] getTypesFromUser(final String title, final String description) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement, final IType[] types) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}

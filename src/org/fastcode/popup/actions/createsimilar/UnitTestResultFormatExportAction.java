package org.fastcode.popup.actions.createsimilar;

import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.UNIT_TEST_FOLDER;
import static org.fastcode.preferences.PreferenceConstants.P_EXPORT_SETTINGS;
import static org.fastcode.util.SourceUtil.backUpExistingExportFile;
import static org.fastcode.util.TemplateUtil.findOrcreateTemplate;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.AbstractActionSupport;
import org.fastcode.Activator;
import org.fastcode.common.FastCodeConstants.EXPORT_OPTIONS;
import org.fastcode.util.FastCodeUtil;

public class UnitTestResultFormatExportAction extends AbstractActionSupport implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	InputStream	inputStream					= null;
	String		unitTestResultFormatFile	= "unit-test-result-format.xml";

	/* (non-Javadoc)
	 * @see org.fastcode.AbstractActionSupport#runAction(org.eclipse.jdt.core.ICompilationUnit, org.eclipse.jdt.core.IJavaElement)
	 */
	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement) throws Exception {

		this.inputStream = FileLocator.openStream(Activator.getDefault().getBundle(),
				new Path("resources/" + this.unitTestResultFormatFile), false);
		boolean doExport = true;
		final IFile file = findOrcreateTemplate(this.unitTestResultFormatFile, UNIT_TEST_FOLDER);
		if (file == null) {
			throw new Exception("Unknown Exception : Please try again.");
		}

		if (!file.isSynchronized(0)) {
			throw new Exception(this.unitTestResultFormatFile + " is not Synchronized, please refresh and try again.");
		}

		try {
			if (file.exists()) {
				final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
				final String exportOption = preferenceStore.getString(P_EXPORT_SETTINGS);
				boolean backup = exportOption.equals(EXPORT_OPTIONS.BACKUP.getValue());
				if (exportOption.equals(EXPORT_OPTIONS.ASK_TO_OVERWRITE_OR_BACKUP.getValue())) {
					final MessageDialog exportMessageDialog = new MessageDialog(
							null,
							"Overwrite File",
							null,
							file.getName()
									+ " is already exported, Would you like to create a back up before it overwrite?\n If No, no back up of existing file will be created.",
							MessageDialog.QUESTION, new String[] { "Yes", "No" }, 0) {

						@Override
						protected void buttonPressed(final int buttonId) {
							setReturnCode(buttonId);
							close();

						}
					};

					exportMessageDialog.open();

					if (exportMessageDialog.getReturnCode() != -1) {
						if (exportMessageDialog.getReturnCode() == 0) {
							backup = true;
						}
					} else {
						doExport = false;
					}
				}

				if (backup) {
					backUpExistingExportFile(file, this.unitTestResultFormatFile, "resources" + FORWARD_SLASH + UNIT_TEST_FOLDER);
					file.setContents(this.inputStream, false, true, new NullProgressMonitor());
					/*final IFile newfile = findOrcreateTemplate(this.unitTestResultFormatFile, UNIT_TEST_FOLDER);
					if (newfile == null) {
						throw new Exception("Unknown Exception : Please try again.");
					}
					if (!newfile.isSynchronized(0)) {
						throw new Exception(this.unitTestResultFormatFile + " is not Synchronized, please refresh and try again.");
					}
					newfile.create(this.inputStream, false, new NullProgressMonitor());*/
				} else if (doExport) {
					file.setContents(this.inputStream, false, true, new NullProgressMonitor());
				}
			} else {
				file.create(this.inputStream, false, new NullProgressMonitor());

			}

			if (doExport) {
				MessageDialog.openInformation(new Shell(), "Success", "Export was successfully done to Fast Code Eclipse Plugin/"
						+ UNIT_TEST_FOLDER + " folder.");
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception("Template could not be saved " + ex.getMessage(), ex);
		} finally {
			FastCodeUtil.closeInputStream(this.inputStream);
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

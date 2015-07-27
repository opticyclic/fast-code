package org.fastcode.popup.actions.processrules;

import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.RULES_FOLDER;
import static org.fastcode.preferences.PreferenceConstants.P_EXPORT_SETTINGS;
import static org.fastcode.util.SourceUtil.backUpExistingExportFile;
import static org.fastcode.util.TemplateUtil.findOrcreateTemplate;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.Activator;
import org.fastcode.common.FastCodeConstants.EXPORT_OPTIONS;
import org.fastcode.util.FastCodeUtil;

public class FastCodeRuleExportAction implements IEditorActionDelegate, IActionDelegate, IWorkbenchWindowActionDelegate {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(final IWorkbenchWindow arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(final IAction arg0) {
		InputStream inputStream = null;
		boolean doExport = true;
		final String fastCodeRulesFile = "fast-code-rules.xml";
		try {
			inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + RULES_FOLDER + FORWARD_SLASH
					+ fastCodeRulesFile), false); //ResourceUtil.class.getClassLoader().getResourceAsStream(unitTestResultFormatFile);

			if (inputStream == null) {
				throw new IllegalArgumentException("Invalid file " + fastCodeRulesFile);
			}

			final IFile file = findOrcreateTemplate(fastCodeRulesFile, RULES_FOLDER);
			if (file == null) {
				throw new Exception("Unknown Exception : Please try again.");
			}
			if (!file.isSynchronized(0)) {
				throw new Exception(fastCodeRulesFile + " is not Synchronized, please refresh and try again.");
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
						backUpExistingExportFile(file, fastCodeRulesFile, "resources" + FORWARD_SLASH + RULES_FOLDER);
						file.setContents(inputStream, false, true, new NullProgressMonitor());
						/*final IFile newfile = findOrcreateTemplate(fastCodeRulesFile, RULES_FOLDER);
						if (newfile == null) {
							throw new Exception("Unknown Exception : Please try again.");
						}
						if (!newfile.isSynchronized(0)) {
							throw new Exception(fastCodeRulesFile + " is not Synchronized, please refresh and try again.");
						}

						newfile.create(inputStream, false, new NullProgressMonitor());*/
					} else if (doExport) {
						file.setContents(inputStream, false, true, new NullProgressMonitor());
					}

				} else {
					file.create(inputStream, false, new NullProgressMonitor());

				}
				if (doExport) {
					MessageDialog.openInformation(new Shell(), "Success", "Export was successfully done to Fast Code Eclipse Plugin/"
							+ RULES_FOLDER + " folder.");
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
				throw new Exception("File could not be saved " + ex.getMessage(), ex);
			} finally {
				FastCodeUtil.closeInputStream(inputStream);
			}

		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {
			FastCodeUtil.closeInputStream(inputStream);
		}
	}

	@Override
	public void selectionChanged(final IAction arg0, final ISelection arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setActiveEditor(final IAction arg0, final IEditorPart arg1) {
		// TODO Auto-generated method stub

	}

}

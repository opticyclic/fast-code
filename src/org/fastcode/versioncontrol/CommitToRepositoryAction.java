package org.fastcode.versioncontrol;

import static org.eclipse.jface.window.Window.CANCEL;
import static org.fastcode.common.FastCodeConstants.ADD_FILE;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.COMPLETED;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.MODIFIED_FILE;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.preferences.PreferenceConstants.P_TIME_GAP_BEFORE_CHECK_IN;
import static org.fastcode.util.SourceUtil.checkForErrors;
import static org.fastcode.util.SourceUtil.getRepositoryServiceClass;
import static org.fastcode.util.SourceUtil.isFileReferenced;
import static org.fastcode.util.SourceUtil.isFileSaved;
import static org.fastcode.util.SourceUtil.loadComments;
import static org.fastcode.util.SourceUtil.refreshProject;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.VersionControlUtil.getPreviousCommentsFromCache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeCheckInFileSelectionDialog;
import org.fastcode.common.FastCodeCheckinCommentsData;
import org.fastcode.dialog.FastCodeCheckinCommentsDialog;
import org.fastcode.exception.FastCodeRepositoryException;
import org.fastcode.preferences.VersionControlPreferences;
import org.fastcode.util.FastCodeFileForCheckin;
import org.fastcode.util.RepositoryService;

public class CommitToRepositoryAction implements IEditorActionDelegate, IActionDelegate, IWorkbenchWindowActionDelegate {

	protected IWorkbenchWindow	window;
	String						staleFile		= "-due to time lapse, to checkin.";
	String						noFile			= "-as the file does not exist on disk.";
	String						staleAndNoFile	= "-because of time lapse, and the file does not exist.";

	@Override
	public void run(final IAction arg0) {
		// TODO Auto-generated method stub
		final FastCodeCheckInFileSelectionDialog selectionDialog;
		try {
			final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();

			final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
			final VersionControlPreferences versionControlPreferences = VersionControlPreferences.getInstance();
			final List<FastCodeFileForCheckin> filseToCheckin = new ArrayList<FastCodeFileForCheckin>();
			final List<FastCodeFileForCheckin> filseToRemove = new ArrayList<FastCodeFileForCheckin>();
			final String timeLag = preferenceStore.getString(P_TIME_GAP_BEFORE_CHECK_IN);
			final StringBuilder filesRemovedFromCache = new StringBuilder(EMPTY_STR);
			boolean clean;

			if (!versionControlPreferences.isEnable()) {
				MessageDialog
						.openInformation(
								new Shell(),
								"Version Control not enabled",
								"Version control is not enabled. Please go to Windows->Preferences->Fast Code->Version Control and give the required details and try again.");
				return;
			}

			for (final FastCodeFileForCheckin fileForCheckin : checkinCache.filesToCheckIn) {
				if (fileForCheckin.getStatus() != COMPLETED) {
					continue;
				}
				final long timeDiff = System.currentTimeMillis() - Long.parseLong(fileForCheckin.getCachedTime());
				/*System.out.println(timeLag);
				System.out.println(timeDiff);
				System.out.println(System.currentTimeMillis());
				System.out.println(Long.parseLong(fileTime.getValue()));
				System.out.println(TimeUnit.SECONDS.convert(timeDiff, TimeUnit.MILLISECONDS));
				System.out.println(TimeUnit.SECONDS.convert(timeDiff, TimeUnit.MILLISECONDS) / 60);
				System.out.println(TimeUnit.MILLISECONDS.toMinutes(timeDiff));*/
				clean = TimeUnit.SECONDS.convert(timeDiff, TimeUnit.MILLISECONDS) / 60 <= Long.parseLong(timeLag);
				if (clean && fileForCheckin.getFile().exists()) { //if (TimeUnit.MILLISECONDS.toMinutes(timeDiff) <= Long.parseLong(timeLag) && fileTime.getKey().exists()) {
					filseToCheckin.add(fileForCheckin);
				} else {
					filseToRemove.add(fileForCheckin);
					if (!clean && !fileForCheckin.getFile().exists()) {
						filesRemovedFromCache.append(EMPTY_STR.equals(filesRemovedFromCache.toString()) ? fileForCheckin.getFile()
								.getAbsolutePath() + this.staleAndNoFile + COMMA : fileForCheckin.getFile().getAbsolutePath()
								+ this.staleAndNoFile);
					} else if (!clean) {
						filesRemovedFromCache.append(EMPTY_STR.equals(filesRemovedFromCache.toString()) ? fileForCheckin.getFile()
								.getAbsolutePath() + this.staleFile + COMMA : fileForCheckin.getFile().getAbsolutePath() + this.staleFile);
					} else if (!fileForCheckin.getFile().exists()) {
						filesRemovedFromCache.append(EMPTY_STR.equals(filesRemovedFromCache.toString()) ? fileForCheckin.getFile()
								.getAbsolutePath() + this.noFile + COMMA : fileForCheckin.getFile().getAbsolutePath() + this.noFile);
					}

				}
			}

			if (!isEmpty(filesRemovedFromCache.toString())) {
				for (final FastCodeFileForCheckin fileToRemove : filseToRemove.toArray(new FastCodeFileForCheckin[0])) {
					checkinCache.getFilesToCheckIn().remove(fileToRemove);
				}
				MessageDialog.openInformation(new Shell(), "Files removed from cache and not available for checking in", "The file(s) "
						+ filesRemovedFromCache.toString());
				//+ " have been removed from cache, either because the file does not exist on disk or time lapse to checkin.");
			}
			if (filseToCheckin.isEmpty()) {
				MessageDialog.openInformation(new Shell(), "No Files to check in",
						"There are no files in cache, that need to be checked in.");
				return;
			}
			selectionDialog = new FastCodeCheckInFileSelectionDialog(new Shell(), "Files to Commit", "Select files to commit",
					filseToCheckin.toArray(new FastCodeFileForCheckin[0]), 0, true);
			if (selectionDialog.open() == CANCEL) {
				return;
			}

			final Object[] filesToCommit = selectionDialog.getResult();
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final StringBuilder notSaved = new StringBuilder(EMPTY_STR);
			for (final Object selectedFile : filesToCommit) {
				if (!((FastCodeFileForCheckin) selectedFile).getFile().isDirectory()) {
					final IPath location = Path.fromOSString(((FastCodeFileForCheckin) selectedFile).getFileFullName());
					final IFile ifile = workspace.getRoot().getFileForLocation(location);
					final boolean fileReferenced = isFileReferenced(ifile, IJavaSearchConstants.TYPE);

					if (fileReferenced) {
						if (!MessageDialog.openQuestion(new Shell(), "File(s) check in",
								"Will check in only the selected file and not any dependencies. Do you want to proceed??")) {
							continue;
						}
					}
					if (!isFileSaved(ifile.getName(), ifile)) {
						notSaved.append(EMPTY_STR.equals(notSaved.toString()) ? ifile.getName() + "- not saved" + COMMA : ifile.getName()
								+ "- not saved.");
					}
					if (checkForErrors(ifile)) {
						notSaved.append(EMPTY_STR.equals(notSaved.toString()) ? ifile.getName() + "- has errors" + COMMA : ifile.getName()
								+ "- has errors.");
					}


				}
			}

			if (!isEmpty(notSaved.toString())) {
				MessageDialog.openError(new Shell(), "File(s) not saved or has error", "The file(s) " + notSaved.toString()
						+ "please take required action and try again.");
				return;
			}

			/*MessageDialog.openInformation(new Shell(), "Checking in files from cache",
					"These files are from cache, the comments might be stale");*/

			final List<String> prjToRefresh = new ArrayList<String>();

			final RepositoryService checkin = getRepositoryServiceClass();

			final IRunnableWithProgress op = new IRunnableWithProgress() {
				@Override
				public void run(final IProgressMonitor monitor) {
					try {
						for (final Object fcCheckinFile : filesToCommit) {
							monitor.setTaskName("Auto checkin");
							monitor.subTask("Checking in " + ((FastCodeFileForCheckin) fcCheckinFile).getFileFullName());

							final FastCodeCheckinCommentsData comboData = new FastCodeCheckinCommentsData();
							if (checkinCache.getCommentKeyDetail().isEmpty()) {
								loadComments();
							}
							comboData.setFinalComment(((FastCodeFileForCheckin) fcCheckinFile).getComments());
							final List<String> cmntsFromRepo = checkin.getPreviousComments(((FastCodeFileForCheckin) fcCheckinFile).getProject().getName());
							final List<String> cmntsFromCache = getPreviousCommentsFromCache(((FastCodeFileForCheckin) fcCheckinFile).getFile());

							comboData.setComntsFromCache(cmntsFromCache);
							comboData.setComntsFromRepo(cmntsFromRepo);
							final FastCodeCheckinCommentsDialog fastCodeCombo = new FastCodeCheckinCommentsDialog(new Shell(), comboData);
							if (fastCodeCombo.open() == Window.CANCEL) {
								return;
								/*comment = getCompilationUnitFromEditor() != null ? "Modified Class " + file.getName() + DOT : "Modified File "
										+ file.getName() + DOT;*/
							}
							String comment;
							if (comboData.isAddPrefixFooter()) {
								final Map<String, Object> placeHolder = new HashMap<String, Object>();
								getGlobalSettings(placeHolder);
								final String prefix = evaluateByVelocity(versionControlPreferences.getComntPrefix(), placeHolder);
								final String footer = evaluateByVelocity(versionControlPreferences.getComntFooter(), placeHolder);
								comment = prefix + NEWLINE + comboData.getFinalComment() + NEWLINE + footer;
								//					System.out.println("added prefix and footer" + comment);
							} else {
								comment = comboData.getFinalComment();
								//					System.out.println("no prefix and footer" + comment);
							}
							checkin.checkInFile(((FastCodeFileForCheckin) fcCheckinFile).getFile(),comment,
									((FastCodeFileForCheckin) fcCheckinFile).getProject());//, url);
							//checkinCache.getFileTimeMap().remove(file);
							prjToRefresh.add(((FastCodeFileForCheckin) fcCheckinFile).getProject().getName());
						}
					} catch (final FastCodeRepositoryException ex) {
						MessageDialog.openError(new Shell(), "Error", "Some error occured --" + ex.getMessage());
						ex.printStackTrace();
					} catch (final Exception ex) {
						MessageDialog.openError(new Shell(), "Error", "Some error occured --" + ex.getMessage());
						ex.printStackTrace();
					} finally {
						monitor.done();
					}
				}
			};

			final IWorkbench wb = PlatformUI.getWorkbench();
			final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			new ProgressMonitorDialog(new Shell()).run(false, false, op);

			for (final String projectName : prjToRefresh) {
				refreshProject(projectName);
			}

		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {

		}

	}

	@Override
	public void selectionChanged(final IAction arg0, final ISelection arg1) {
		// TODO Auto-generated method stub

	}

	public void setActivePart(final IAction arg0, final IWorkbenchPart arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void setActiveEditor(final IAction arg0, final IEditorPart arg1) {
		// TODO Auto-generated method stub
	}
}

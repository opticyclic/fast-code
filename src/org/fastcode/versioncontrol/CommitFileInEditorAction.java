package org.fastcode.versioncontrol;

import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.fastcode.common.FastCodeConstants.ADD_FILE;
import static org.fastcode.common.FastCodeConstants.CREATE_CLASS;
import static org.fastcode.common.FastCodeConstants.MODIFIED_FILE;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_FILE;
import static org.fastcode.util.SourceUtil.checkForErrors;
import static org.fastcode.util.SourceUtil.getRepositoryServiceClass;
import static org.fastcode.util.SourceUtil.isFileReferenced;
import static org.fastcode.util.SourceUtil.loadComments;
import static org.fastcode.util.SourceUtil.refreshProject;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.VersionControlUtil.getPreviousCommentsFromCache;
import static org.fastcode.util.VersionControlUtil.isPrjConfigured;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.common.FastCodeCheckinCommentsData;
import org.fastcode.common.FastCodeFile;
import org.fastcode.dialog.FastCodeCheckinCommentsDialog;
import org.fastcode.exception.FastCodeRepositoryException;
import org.fastcode.preferences.VersionControlPreferences;
import org.fastcode.util.RepositoryService;

public class CommitFileInEditorAction /*extends Action*/implements IEditorActionDelegate, IActionDelegate, IWorkbenchWindowActionDelegate {
	protected IWorkbenchWindow	window;
	protected IEditorPart		editorPart;

	/*CommitFileInEditorAction() {
		super();
		System.out.println("coming here");
		setEnabled(true);
		final IPreferenceStore prefs = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);

		setEnabled(prefs.getBoolean(P_ENABLE_AUTO_CHECKIN));

		prefs.addPropertyChangeListener(new IPropertyChangeListener() {

			public void propertyChange(final PropertyChangeEvent event) {
				 if (P_ENABLE_AUTO_CHECKIN.equals(event.getProperty()))
			       {
			         final IPreferenceStore prefs = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);

			         setEnabled(prefs.getBoolean(P_ENABLE_AUTO_CHECKIN));
			       }

			}

		});
	}*/

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		System.out.println("inside dispose");
	}

	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}

	@Override
	public void run(final IAction arg0) {
		final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();
		final VersionControlPreferences versionControlPreferences = VersionControlPreferences.getInstance();

		if (this.window != null) {
			this.editorPart = this.window.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		}

		if (this.editorPart == null) {
			openError(new Shell(), "Error", "There is no file open in the editor to check in.");
			return;
		}
		final IFile file = (IFile) this.editorPart.getEditorInput().getAdapter(IFile.class);

		if (this.editorPart.isDirty()) {
			MessageDialog.openError(new Shell(), "File not saved", "The file is not saved...please save and try again.");
			return;
		}

		try {
			if (checkForErrors(file)
					&& !MessageDialog.openQuestion(new Shell(), "Errors",
							"There are errors present in the file. Do you still want to check it in?")) {
				return;
			}

			if (!versionControlPreferences.isEnable()) {
				MessageDialog
						.openInformation(
								new Shell(),
								"Cannot Checkin",
								"Version control is not enabled.\nPlease go to Windows->Preferences->Fast Code->Version Control and give the required details and try again.");
				return;
			}

			if (isEmpty(file.getProject().getPersistentProperties())) {
				MessageDialog
						.openInformation(
								new Shell(),
								"Cannot Checkin",
								"Project "
										+ file.getProject().getName()
										+ " is not shared.\nPlease share the project, configure the details in Windows->Preferences->Fast Code->Version Control and try again.");
				return;
			}

			if (isEmpty(isPrjConfigured(file.getProject().getName()))) {
				MessageDialog
						.openInformation(
								new Shell(),
								"Cannot Checkin",
								"Project "
										+ file.getProject().getName()
										+ " is not configured.\nPlease configure the details in Windows->Preferences->Fast Code->Version Control and try again.");
				return;
			}

			/*final boolean prjShared = !isEmpty(file.getProject().getPersistentProperties());
			final boolean prjConfigured = !isEmpty(isPrjConfigured(file.getProject().getName()));
			if (!(versionControlPreferences.isEnable() && prjShared && prjConfigured)) {
				MessageDialog
						.openInformation(
								new Shell(),
								"Cannot Checkin",
								"Either version control is not enabled or, project "
										+ file.getProject().getName()
										+ " is not shared or configured.\nPlease go to Windows->Preferences->Fast Code->Version Control and give the required details and try again.");
				return;
			}*/

			final boolean fileReferenced = isFileReferenced(file, IJavaSearchConstants.TYPE);

			if (fileReferenced) {
				if (!MessageDialog.openQuestion(new Shell(), "File(s) check in",
						"Will check in only the selected file and not any dependencies. Do you want to proceed??")) {
					return;
				}
			}
			final RepositoryService checkin = getRepositoryServiceClass();
			final String comment;
			final Map<String, Object> placeHolder = new HashMap<String, Object>();
			getGlobalSettings(placeHolder);
			placeHolder.put(PLACEHOLDER_FILE, new FastCodeFile(file));
			final FastCodeCheckinCommentsData comboData = new FastCodeCheckinCommentsData();
			final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
			if (checkinCache.getCommentKeyDetail().isEmpty()) {
				loadComments();
			}
			if (checkin.isFileInRepository(new File(file.getLocationURI()))) {
				comboData.setFinalComment(evaluateByVelocity(checkinCache.getCommentKeyDetail().get(MODIFIED_FILE), placeHolder));
			} else {
				comboData.setFinalComment(evaluateByVelocity(checkinCache.getCommentKeyDetail().get(ADD_FILE), placeHolder));
			}
			final List<String> cmntsFromRepo = checkin.getPreviousComments(file.getProject().getName());
			final List<String> cmntsFromCache = getPreviousCommentsFromCache(new File(file.getLocationURI()));

			comboData.setComntsFromCache(cmntsFromCache);
			comboData.setComntsFromRepo(cmntsFromRepo);
			final FastCodeCheckinCommentsDialog fastCodeCombo = new FastCodeCheckinCommentsDialog(new Shell(), comboData);
			if (fastCodeCombo.open() == Window.CANCEL) {
				return;
				/*comment = getCompilationUnitFromEditor() != null ? "Modified Class " + file.getName() + DOT : "Modified File "
						+ file.getName() + DOT;*/
			}
			if (comboData.isAddPrefixFooter()) {

				final String prefix = evaluateByVelocity(versionControlPreferences.getComntPrefix(), placeHolder);
				final String footer = evaluateByVelocity(versionControlPreferences.getComntFooter(), placeHolder);
				comment = prefix + NEWLINE + comboData.getFinalComment() + NEWLINE + footer;
				//					System.out.println("added prefix and footer" + comment);
			} else {
				comment = comboData.getFinalComment();
				//					System.out.println("no prefix and footer" + comment);
			}

			//final RepositoryService checkin = getRepositoryServiceClass();

			final IRunnableWithProgress op = new IRunnableWithProgress() {
				@Override
				public void run(final IProgressMonitor monitor) {
					try {
						/*final String url = checkin.getRepositoryURL(file.getProject());
						if (!isEmpty(url)) {*/
						monitor.setTaskName("Auto checkin");
						monitor.subTask("Checking in " + file.getFullPath());
						checkin.checkInFile(new File(file.getLocationURI()), comment, file.getProject()); //, url);
						//}
					} catch (final FastCodeRepositoryException ex) {
						MessageDialog.openError(new Shell(), "Error", "Some error occured --" + ex.getMessage());
						//						ex.getCause().printStackTrace();
						ex.printStackTrace();
					} finally {
						monitor.done();
					}
				}
			};

			/*final IWorkbench wb = PlatformUI.getWorkbench();
			final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();*/
			new ProgressMonitorDialog(new Shell()).run(false, false, op);

			refreshProject(file.getProject().getName());
		} catch (final Exception ex) {
			MessageDialog.openError(new Shell(), "Error", "Some error occured --" + ex.getMessage());
			ex.printStackTrace();
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

	/**
	 * @return
	 */
	protected ICompilationUnit getCompilationUnitFromEditor() {
		final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();
		final ICompilationUnit compilationUnit = manager.getWorkingCopy(this.editorPart.getEditorInput());
		return compilationUnit != null && compilationUnit.exists() ? compilationUnit : null;
	}
}

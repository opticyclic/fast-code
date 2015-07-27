/**
 *
 */
package org.fastcode;

import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.eclipse.jface.dialogs.MessageDialog.openInformation;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FASTCODE;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;
import static org.fastcode.util.MessageUtil.showStatus;
import static org.fastcode.util.SourceUtil.getRepositoryServiceClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.texteditor.ITextEditor;
import org.fastcode.common.FastCodeEntityHolder;
import org.fastcode.preferences.VersionControlPreferences;
import org.fastcode.util.FastCodeResourceChangeListener;
import org.fastcode.util.MessageUtil;
import org.fastcode.util.RepositoryService;

/**
 * @author Gautam
 *
 */
public abstract class AbstractActionSupport {

	protected IWorkbenchWindow							window;
	protected IWorkbenchPage							page;
	protected IEditorPart								editorPart;
	//protected ICompilationUnit compUnit = null;
	protected ISelection								selection;
	private String										errorMessage;
	protected Map<Object, List<FastCodeEntityHolder>>	commitMessage	= new HashMap<Object, List<FastCodeEntityHolder>>();
	protected boolean									autoCheckinEnabled;

	/**
	 *
	 * @param action
	 */
	public void run(final IAction action) {
		this.errorMessage = null;
		final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		//ISourceModule unit = (ISourceModule) PHPStructuredEditor.getModelElement();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(FastCodeResourceChangeListener.getInstance());
		if (manager == null) {
			return;
		}
		if (this.window != null) {
			this.editorPart = this.window.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		} else {
			this.editorPart = null;
		}

		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		ICompilationUnit tmpcompUnit = null;
		if (this.editorPart != null) {
			tmpcompUnit = manager.getWorkingCopy(this.editorPart.getEditorInput());
		}
		final ICompilationUnit compUnit = tmpcompUnit;
		if (requireJavaClass() && (compUnit == null || !compUnit.exists())) {
			this.errorMessage = "This is not a java class, cannot continue.";
			if (this.editorPart.getEditorInput().getName().endsWith(JAVA_EXTENSION)) {
				this.errorMessage = "Some error occured. Please try again or submit a bug.";
			}
			openError(this.editorPart.getSite().getShell(), "Error", this.errorMessage);
			return;
		}
		boolean becomeWorkingCopy = false;
		try {
			if (requireJavaClass() && canActOnClassesOnly() && compUnit.findPrimaryType().isInterface()) {
				openError(this.editorPart.getSite().getShell(), "Error", "This action can not be applied to interfaces.");
				return;
			}

			if (doesModify()) {
				if (compUnit.isReadOnly() || compUnit.getUnderlyingResource().isReadOnly()) {
					openError(this.editorPart.getSite().getShell(), "Error", "Cannot continue, this is a read only class/interfaces.");
					return;
				}
				if (!compUnit.isWorkingCopy()) {
					becomeWorkingCopy = true;
					compUnit.becomeWorkingCopy(null);
				}
			}

			final VersionControlPreferences versionControlPreferences = VersionControlPreferences.getInstance();
			this.autoCheckinEnabled = versionControlPreferences.isEnable();

			final IRunnableWithProgress op = new IRunnableWithProgress() {
				@Override
				public void run(final IProgressMonitor monitor) {
					try {
						monitor.beginTask(action.getDescription(), 1);
						monitor.subTask("Doing " + action.getText());
						if (AbstractActionSupport.this.doesRequireMoreInfoFromUser()) {
							final IType[] types = AbstractActionSupport.this.getTypesFromUser("", "");
							AbstractActionSupport.this.runAction(compUnit, AbstractActionSupport.this.findSelectedJavaElement(compUnit),
									types);
						} else {
							AbstractActionSupport.this.runAction(compUnit,
									requireJavaClass() ? AbstractActionSupport.this.findSelectedJavaElement(compUnit) : null);
						}
						monitor.worked(1);
					} catch (final Exception ex) {
						ex.printStackTrace();
						AbstractActionSupport.this.errorMessage = ex.getMessage();
					} finally {
						monitor.done();
					}
				}
			};

			new ProgressMonitorDialog(shell).run(false, false, op);

			if (doesModify()) {
				compUnit.commitWorkingCopy(false, null);

				if (this.autoCheckinEnabled) {
					if (!this.commitMessage.isEmpty()) {
						final RepositoryService checkin = getRepositoryServiceClass();
						checkin.commitToRepository(this.commitMessage, false);
					}
				}
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			if (this.errorMessage != null) {
				this.errorMessage += ex.getMessage();
			} else {
				this.errorMessage = ex.getMessage();
			}
		} finally {
			try {
				if (becomeWorkingCopy) {
					compUnit.discardWorkingCopy();
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
				this.errorMessage = ex.getMessage();
			}
			this.commitMessage.clear();
			ResourcesPlugin.getWorkspace().addResourceChangeListener(FastCodeResourceChangeListener.getInstance());
		}

		if (this.errorMessage != null && !this.errorMessage.equals(EMPTY_STR)) {
			showStatus(shell, "Fast Code Plug-in", this.errorMessage);
		}
	}

	/**
	 *
	 * @param compUnit
	 * @param javaElement
	 * @param types
	 * @throws Exception
	 */
	protected abstract void runAction(ICompilationUnit compUnit, IJavaElement javaElement, IType[] types) throws Exception;

	/**
	 *
	 * @param compUnit
	 * @param javaElement
	 * @throws Exception
	 */
	protected abstract void runAction(ICompilationUnit compUnit, IJavaElement javaElement) throws Exception;

	/**
	 *
	 * @return
	 */
	protected boolean requireJavaClass() {
		return true;
	}

	/**
	 *
	 * @return
	 */
	protected boolean canActOnClassesOnly() {
		return true;
	}

	/**
	 *
	 * @param compUnit
	 * @return
	 * @throws Exception
	 */
	protected IJavaElement findSelectedJavaElement(final ICompilationUnit compUnit) throws Exception {

		final ISelection selection = ((ITextEditor) this.editorPart).getSelectionProvider().getSelection();
		if (selection instanceof ITextSelection) {
			final ITextSelection textSelection = (ITextSelection) selection;
			return compUnit.getElementAt(textSelection.getOffset());
		}
		return null;
	}

	protected abstract IType[] getTypesFromUser(String title, String description) throws Exception;

	protected String getInitialText() {
		return "";
	}

	protected abstract boolean doesModify();

	protected abstract boolean doesRequireMoreInfoFromUser();

	/**
	 *
	 * @param action
	 * @param targetEditor
	 */
	public void setActiveEditor(final IAction action, final IEditorPart targetEditor) {
		if (!action.getId().toUpperCase().startsWith(FASTCODE)) {
			return;
		}
		this.editorPart = targetEditor;
	}

	/**
	 *
	 * @param member
	 * @param error
	 * @throws JavaModelException
	 */
	protected void showErrorAndSwitchToExistingElement(final IMember member, final String error) throws JavaModelException {
		openInformation(this.editorPart.getSite().getShell(), "Already Exists", error);

		final ITextSelection sel = new TextSelection(member.getNameRange().getOffset(), member.getNameRange().getLength());

		this.editorPart.getEditorSite().getSelectionProvider().setSelection(sel);
	}

	/**
	 * Returns the next field from the cursor position, null if it is the last
	 * field.
	 *
	 * @param type
	 * @param member
	 *
	 * @return
	 * @throws JavaModelException
	 */
	protected IJavaElement findNextElement(final IType type, final IMember member) throws JavaModelException {
		IJavaElement nextJavaElement = null;

		boolean foundMatch = false;

		Object[] memberArray = null;
		if (member instanceof IField) {
			memberArray = type.getFields();
		} else if (member instanceof IMethod) {
			memberArray = type.getMethods();
		} else {
			return null;
		}

		for (final IMember membr : (IMember[]) memberArray) {
			if (membr.equals(member)) {
				foundMatch = true;
				continue;
			}
			if (foundMatch) {
				nextJavaElement = membr;
				break;
			}
		}
		/*
		 * if (foundMatch) { return nextJavaElement; }
		 *
		 * for (IMethod method : parentUnit.findPrimaryType().getMethods()) {
		 * //return first method return method; }
		 */
		return nextJavaElement;
	}

	/**
	 *
	 * @param action
	 * @param selection
	 */
	public void selectionChanged(final IAction action, final ISelection selection) {
		this.selection = selection;
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
		this.page = targetPart.getSite().getPage();
		this.window = this.page.getWorkbenchWindow();
	}

	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}
}

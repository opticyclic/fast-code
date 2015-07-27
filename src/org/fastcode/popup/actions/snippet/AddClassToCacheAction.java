package org.fastcode.popup.actions.snippet;

import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class AddClassToCacheAction implements IEditorActionDelegate, IActionDelegate, IWorkbenchWindowActionDelegate {

	protected IWorkbenchWindow	window;
	protected IWorkbenchPage	page;
	protected IEditorPart		editorPart;
	protected ISelection		selection;

	final FastCodeCache			fastCodeCache	= FastCodeCache.getInstance();

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	@Override
	public void run(final IAction action) {
		final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();
		if (manager == null) {
			return;
		}
		if (this.window != null) {
			this.editorPart = this.window.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		}

		final ICompilationUnit compUnit = manager.getWorkingCopy(this.editorPart.getEditorInput());

		if (compUnit != null && compUnit.exists()) {
			if (this.editorPart.getEditorInput().getName().endsWith(JAVA_EXTENSION)) { // groovy
																						// check
																						// needs
																						// to
																						// be
																						// added
				final IType type = compUnit.findPrimaryType();
				// final FastCodeType fastCodeType = new
				// FastCodeType(compUnit.findPrimaryType().getFullyQualifiedName());
				if (!this.fastCodeCache.typeSet.contains(type)) {
					this.fastCodeCache.typeSet.add(type);
				}
			}
		} else {
			final IFile file = (IFile) this.editorPart.getEditorInput().getAdapter(IFile.class);
			// final FastCodeFile fastCodeFile = new
			// FastCodeFile(compUnit.getElementName(), ((IFile)
			// compUnit.getResource()).getProjectRelativePath().toString());
			// IFile iFile=(IFile) compUnit.getResource();
			if (!this.fastCodeCache.fileSet.contains(file)) {
				this.fastCodeCache.fileSet.add(file);
			}
		}
	}

	public void runAction() throws Exception {

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setActiveEditor(final IAction action, final IEditorPart targetEditor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(final IWorkbenchWindow window) {
		this.window = window;

	}

}

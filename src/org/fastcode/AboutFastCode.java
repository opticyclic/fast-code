package org.fastcode;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.dialog.AboutFastCodeDialog;

public class AboutFastCode implements IEditorActionDelegate, IActionDelegate, IWorkbenchWindowActionDelegate {

	@Override
	public void dispose() {

	}

	@Override
	public void init(final IWorkbenchWindow arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run(final IAction arg0) {
		final AboutFastCodeDialog aboutFastCodeDialog = new AboutFastCodeDialog(new Shell());
		aboutFastCodeDialog.open();

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

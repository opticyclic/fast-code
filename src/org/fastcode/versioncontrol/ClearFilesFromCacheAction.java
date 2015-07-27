package org.fastcode.versioncontrol;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.preferences.VersionControlPreferences;

public class ClearFilesFromCacheAction implements IEditorActionDelegate, IActionDelegate, IWorkbenchWindowActionDelegate {

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
		final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
		final VersionControlPreferences versionControlPreferences = VersionControlPreferences.getInstance();
		if (checkinCache.getFilesToCheckIn().isEmpty()) {
			MessageDialog.openInformation(new Shell(), "Cache is Empty", "There is nothing to clear in the cache....");
		} else {
			if (MessageDialog.openConfirm(new Shell(), "Confirm Clear Cache", "Are you sure you want to clear the cache completely?")) {
				checkinCache.getFilesToCheckIn().clear();
			}
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

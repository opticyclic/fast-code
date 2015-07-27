package org.fastcode.common;

import org.eclipse.swt.widgets.Shell;

public class FastCodeFileSelectionDialog extends FastCodeSelectionDialog {

	/**
	 * @param parent
	 * @param title
	 * @param message
	 * @param elements
	 * @param type
	 * @param multipleSelection
	 */
	public FastCodeFileSelectionDialog(final Shell parent, final String title, final String message, final FastCodeFile[] elements, final int type,
			final boolean multipleSelection) {
		super(parent, title, message, elements, type, multipleSelection);

	}

}

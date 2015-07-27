package org.fastcode.common;

import java.io.File;

import org.eclipse.swt.widgets.Shell;

public class FileSelectionDialog extends FastCodeSelectionDialog {
	/**
	 * @param parent
	 * @param title
	 * @param message
	 * @param elements
	 * @param type
	 * @param multipleSelection
	 */
	public FileSelectionDialog(final Shell parent, final String title, final String message, final File[] elements, final int type,
			final boolean multipleSelection) {
		super(parent, title, message, elements, type, multipleSelection);

	}
 }

package org.fastcode.common;

import org.eclipse.swt.widgets.Shell;
import org.fastcode.util.FastCodeFileForCheckin;

public class FastCodeCheckInFileSelectionDialog extends FastCodeSelectionDialog {

	/**
	 * @param parent
	 * @param title
	 * @param message
	 * @param elements
	 * @param type
	 * @param multipleSelection
	 */
	public FastCodeCheckInFileSelectionDialog(final Shell parent, final String title, final String message, final FastCodeFileForCheckin[] elements, final int type,
			final boolean multipleSelection) {
		super(parent, title, message, elements, type, multipleSelection);

	}
 }

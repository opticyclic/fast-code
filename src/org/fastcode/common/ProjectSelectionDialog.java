package org.fastcode.common;

import org.eclipse.swt.widgets.Shell;

public class ProjectSelectionDialog extends FastCodeSelectionDialog {

	/**
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param elements
	 * @param type
	 */
	public ProjectSelectionDialog(final Shell parent, final String title, final String message, final Object[] elements,final int type) {
		super(parent, title, message, elements, type, false);
	}

}

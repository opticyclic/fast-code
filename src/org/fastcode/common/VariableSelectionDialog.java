package org.fastcode.common;

import org.eclipse.swt.widgets.Shell;

public class VariableSelectionDialog extends FastCodeSelectionDialog {

	/**
	 * @param parent
	 * @param title
	 * @param message
	 * @param elements
	 * @param type
	 * @param multipleSelection
	 */
	public VariableSelectionDialog(final Shell parent, final String title, final String message, final FastCodeReturn[] elements,
			final boolean multipleSelection) {
		super(parent, title, message, elements, 0, multipleSelection);

	}

}

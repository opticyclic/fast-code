/**
 *
 */
package org.fastcode.common;

import org.eclipse.swt.widgets.Shell;

/**
 * @author Gautam
 *
 */
public class StringSelectionDialog extends FastCodeSelectionDialog {

	/**
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param elements
	 * @param multipleSelection
	 */
	public StringSelectionDialog(final Shell parent, final String title, final String message, final String[] elements,
			final boolean multipleSelection) {
		super(parent, title, message, elements, 0, multipleSelection);
	}

}

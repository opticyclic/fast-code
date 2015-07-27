/**
 * @author : Gautam

 * Created : 09/27/2010

 */

package org.fastcode.common;

import org.eclipse.swt.widgets.Shell;

public class FastCodeMethodSelectionDialog extends FastCodeSelectionDialog {

	/**
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param methods
	 * @param type
	 * @param multipleSelection
	 */
	public FastCodeMethodSelectionDialog(final Shell parent, final String title, final String message, final FastCodeMethod[] methods,
			final boolean multipleSelection) {
		super(parent, title, message, methods, -1, multipleSelection);
	}

}
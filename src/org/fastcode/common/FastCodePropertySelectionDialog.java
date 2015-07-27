/**
 * @author : Gautam

 * Created : 01/02/2011

 */

package org.fastcode.common;

import org.eclipse.swt.widgets.Shell;

public class FastCodePropertySelectionDialog extends FastCodeSelectionDialog {

	/**
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param elements
	 */
	public FastCodePropertySelectionDialog(final Shell parent, final String title, final String message, final Object[] elements) {
		super(parent, title, message, elements, 0, false);
	}

}
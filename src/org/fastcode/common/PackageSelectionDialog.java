/**
 * @author : Gautam

 * Created : 01/03/2011

 */

package org.fastcode.common;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.swt.widgets.Shell;

public class PackageSelectionDialog extends FastCodeSelectionDialog {

	/**
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param elements
	 * @param type
	 */
	public PackageSelectionDialog(final Shell parent, final String title, final String message, final Object[] elements) {
		super(parent, title, message, elements, IJavaElement.PACKAGE_FRAGMENT, false);
	}

	/**
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param elements
	 * @param type
	 */
	public PackageSelectionDialog(final Shell parent, final String title, final String message, final Object[] elements, final String filter) {
		super(parent, title, message, elements, IJavaElement.PACKAGE_FRAGMENT, false, filter);
	}

}

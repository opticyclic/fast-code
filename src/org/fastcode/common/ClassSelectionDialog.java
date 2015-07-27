/**
 *
 */
package org.fastcode.common;

import static org.eclipse.jdt.core.IJavaElement.TYPE;

import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Gautam
 *
 */
public class ClassSelectionDialog extends FastCodeSelectionDialog {

	/**
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param types
	 * @param type
	 * @param multipleSelection
	 */
	public ClassSelectionDialog(final Shell parent, final String title, final String message, final IType[] types,
			final boolean multipleSelection) {

		super(parent, title, message, types, TYPE, multipleSelection);
	}

	/**
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param types
	 * @param multipleSelection
	 */
	public ClassSelectionDialog(final Shell parent, final String title, final String message, final FastCodeType[] types,
			final boolean multipleSelection) {
		super(parent, title, message, types, TYPE, multipleSelection);
	}
}

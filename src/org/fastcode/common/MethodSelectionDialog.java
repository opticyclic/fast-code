/**
 *
 */
package org.fastcode.common;

import static org.eclipse.jdt.core.IJavaElement.METHOD;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Gautam
 *
 */
public class MethodSelectionDialog extends FastCodeSelectionDialog {

	/**
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param methods
	 */
	public MethodSelectionDialog(Shell parent, String title, String message, IMethod[] methods, boolean multipleSelection) {
		super(parent, title, message, methods, METHOD, multipleSelection);
	}
}
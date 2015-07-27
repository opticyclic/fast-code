/**
 *
 */
package org.fastcode.util;

import static org.fastcode.util.StringUtil.isEmpty;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Gautam
 *
 */
public class MessageUtil {

	/**
	 *
	 * @param shell
	 * @param errorMessage
	 * @param title
	 */
	public static void showStatus(final Shell shell, final String title, final String errorMessage) {
		if (isEmpty(errorMessage)) {
			MessageDialog.openInformation(shell, title, "Action was executed.");
		} else {
			MessageDialog.openError(shell, title, "Action was not executed. " + errorMessage);
		}
	}

	/**
	 *
	 * @param errorMessage
	 * @param title
	 */
	public static void showError(final String errorMessage, final String title) {
		final Shell shell = new Shell();
		MessageDialog.openError(shell, title, errorMessage);
	}

	/**
	 *
	 * @param errorMessage
	 * @param title
	 */
	public static void showWarning(final String errorMessage, final String title) {
		final Shell shell = getParentShell();
		MessageDialog.openWarning(shell, title, errorMessage);
	}

	public static void showMessage(final String message, final String title) {
		final Shell shell = new Shell();
		MessageDialog.openInformation(shell, title, message);
	}

	/**
	 *
	 * @return
	 */
	public static Shell getParentShell() {
		try {
			final IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (editorPart != null) {
				return editorPart.getSite().getShell();
			}
			return null;
		} catch (final Exception ex) { // to handle NPE
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 *
	 * @param title
	 * @param message
	 * @param choices
	 * @return
	 */
	public static String getChoiceFromMultipleValues(final Shell parent, final String title, final String message, final String... choices) {
		final MessageDialog dialog = new MessageDialog(parent, title, null, message, MessageDialog.QUESTION, choices, 0);

		int result;
		try {
			result = dialog.open();
		} catch (final Exception ex) {
			ex.printStackTrace();
			return choices[0];
		}
		return result == SWT.DEFAULT ? null : choices[result];
	}

}

/**
 *
 */
package org.fastcode.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Gautam
 *
 */
public class MessageTest {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		final MessageDialog dialog = new MessageDialog(new Shell(), "Title", null, "Question", MessageDialog.QUESTION, new String[] {
				"Yes", "No", "Always", "Never" }, 0);
		/*
		 * MessageDialogWithToggle dialog = new MessageDialogWithToggle( new
		 * Shell(), "Title", null, "Decide", MessageDialog.QUESTION, new
		 * String[] {"Yes", "No", "Always", "Never"}, 0);
		 */
		dialog.open();
		System.out.println(dialog.getReturnCode());
	}

}

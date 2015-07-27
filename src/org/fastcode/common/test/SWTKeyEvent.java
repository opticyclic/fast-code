package org.fastcode.common.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SWTKeyEvent {
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("SWT KeyEvent Example");

		shell.setLayout(new FillLayout());

		final Button button = new Button(shell, SWT.CENTER);

		button.setText("Type Something");

		button.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				String string = "";

				//check click together?
				System.out.println(SWT.TRAVERSE_TAB_NEXT);
				System.out.println(SWT.TRAVERSE_TAB_PREVIOUS);
				System.out.println(SWT.TRAVERSE_RETURN);
				if ((e.stateMask & SWT.ALT) != 0) {
					string += "ALT - keyCode = " + e.keyCode;
				}
				if ((e.stateMask & SWT.CTRL) != 0) {
					string += "CTRL - keyCode = " + e.keyCode;
				}
				if ((e.stateMask & SWT.SHIFT) != 0) {
					string += "SHIFT - keyCode = " + e.keyCode;
				}

				if (e.keyCode == SWT.BS) {
					string += "BACKSPACE - keyCode = " + e.keyCode;
				}

				if (e.keyCode == SWT.ESC) {
					string += "ESCAPE - keyCode = " + e.keyCode;
				}

				if (e.keyCode == 9) {
					string += "TAB - keyCode = " + e.keyCode;
				}


				//check characters
				if (e.keyCode >= 97 && e.keyCode <= 122) {
					string += " " + e.character + " - keyCode = " + e.keyCode;
				}

				//check digit
				if (e.keyCode >= 48 && e.keyCode <= 57) {
					string += " " + e.character + " - keyCode = " + e.keyCode;
				}

				if (!string.equals("")) {
					System.out.println(string);
				}
			}
		});

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}

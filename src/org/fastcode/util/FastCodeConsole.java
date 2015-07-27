/**
 *
 */
package org.fastcode.util;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.fastcode.common.FastCodeColor;

/**
 * @author Gautam
 *
 */
public class FastCodeConsole {

	private static FastCodeConsole	console				= new FastCodeConsole();

	private MessageConsole			messageConsole;
	private IFile					file;

	private static final String		FAST_CODE_CONSOLE	= "Fast Code Console";
	// IHyperlink hyperlink = new FileLink();

	private int						offset				= 0;

	private FastCodeConsole() {
		final IConsoleManager conMan = ConsolePlugin.getDefault().getConsoleManager();
		final IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			if (FAST_CODE_CONSOLE.equals(existing[i].getName())) {
				this.messageConsole = (MessageConsole) existing[i];
				return;
			}
		}
		//no console found, so create a new one
		this.messageConsole = new MessageConsole(FAST_CODE_CONSOLE, null);
		conMan.addConsoles(new IConsole[] { this.messageConsole });
	}

	public static FastCodeConsole getInstance() {
		return console;
	}

	/**
	 *
	 * @param message
	 */
	public void writeToConsole(final String message) {
		this.writeToConsole(message, null, false);
	}

	/**
	 *
	 * @param message
	 */
	public void writeToConsoleWithLink(final String message, final IFile file) {
		this.writeToConsole(message, file, false);
	}

	/**
	 *
	 * @param message
	 */
	private void writeToConsole(final String message, final IFile file, final boolean isError) {
		final MessageConsoleStream out = this.messageConsole.newMessageStream();
		MessageConsoleStream errorOut = this.messageConsole.newMessageStream();
		if (isError) {
			// Color color = this.messageConsole.getBackground();
			final Color red = FastCodeColor.getErrorMsgColor();//new Color(Display.getCurrent(), 255, 0, 0);
			if (red != null && red.getDevice() != null) {
				out.setColor(red);
			}
		}

		try {
			if (file == null) {
				out.println(message);
			} else {
				out.print(message);
				errorOut = this.messageConsole.newMessageStream();
				//final Color blue = new Color(Display.getCurrent(), 0, 0, 255);
				errorOut.setColor(FastCodeColor.getBlueMsgColor());
				errorOut.println(file.getName());
				this.offset = this.messageConsole.getDocument().getLength();
				this.file = file;
				// this.messageConsole.addHyperlink(new FileLink(file,
				// "org.eclipse.jdt.ui.CompilationUnitEditor", 0, 1, 0),
				// this.offset, file.getName().length());
			}
			// this.offset += message.length();
		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (errorOut != null) {
					errorOut.close();
				}
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 *
	 */
	public void addLink() {
		try {
			this.messageConsole.addHyperlink(new FileLink(this.file, "org.eclipse.jdt.ui.CompilationUnitEditor", 0, 1, 0), this.offset,
					this.file.getName().length());
		} catch (final Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 *
	 * @param message
	 */
	public void writeErrorToConsole(final String message) {
		this.writeToConsole(message, null, true);
	}

	/**
	 * @return the messageConsole
	 */
	public MessageConsole getMessageConsole() {
		return this.messageConsole;
	}
}

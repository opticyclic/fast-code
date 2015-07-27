/**
 *
 */
package org.fastcode.util;

import org.eclipse.jface.text.rules.ICharacterScanner;

/**
 * @author Gautam
 *
 */
public class ScannerUtil {

	public static void unread(final ICharacterScanner scanner, final int numChars) {
		for (int i = 0; i < numChars; i++) {
			scanner.unread();
		}
	}
}

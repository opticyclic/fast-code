package org.fastcode.templates.rules;

import org.eclipse.jface.text.rules.IWordDetector;

public class ParameterTypeDetector implements IWordDetector {

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	@Override
	public boolean isWordStart(final char aChar) {
		return aChar == 'c' || aChar == 'j' || aChar == 'f' || aChar == 'p' || aChar == 'l' || aChar == 'b';
	}

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	@Override
	public boolean isWordPart(final char aChar) {
		return Character.isLetterOrDigit(aChar);
	}
}

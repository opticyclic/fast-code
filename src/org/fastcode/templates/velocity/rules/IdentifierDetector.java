package org.fastcode.templates.velocity.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * A Velocity identifier aware word detector.
 */
public class IdentifierDetector implements IWordDetector {

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	@Override
	public boolean isWordStart(final char aChar) {
		return Character.isLetter(aChar);
	}

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	@Override
	public boolean isWordPart(final char aChar) {
		return Character.isLetterOrDigit(aChar) || aChar == '-' || aChar == '_' || aChar == '.';
	}
}

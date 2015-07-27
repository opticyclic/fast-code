package org.fastcode.templates.velocity.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * A Velocity directive aware word detector.
 */
public class DirectiveDetector implements IWordDetector {

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	@Override
	public boolean isWordStart(final char aChar) {
		return aChar == '#';
	}

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	@Override
	public boolean isWordPart(final char aChar) {
		return Character.isLetterOrDigit(aChar) || aChar == '-' || aChar == '_';
	}
}

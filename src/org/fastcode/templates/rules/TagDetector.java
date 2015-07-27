package org.fastcode.templates.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Detector for Javadoc tags.
 */
public class TagDetector implements IWordDetector {

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	@Override
	public boolean isWordStart(final char aChar) {
		return aChar == '@';
	}

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	@Override
	public boolean isWordPart(final char aChar) {
		return Character.isLetterOrDigit(aChar) || aChar == '-' || aChar == '_';
	}
}

package org.fastcode.templates.rules;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

/**
 * A whitespace detector.
 */
public class WhitespaceDetector implements IWhitespaceDetector {

	/*
	 * @see org.eclipse.jface.text.rules.IWhitespaceDetector#isWhitespace(char)
	 */
	@Override
	public boolean isWhitespace(final char c) {
		return Character.isWhitespace(c);
	}
}

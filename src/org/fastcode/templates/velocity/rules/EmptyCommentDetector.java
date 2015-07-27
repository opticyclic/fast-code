package org.fastcode.templates.velocity.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Detector for empty Velocity comments.
 */
class EmptyCommentDetector implements IWordDetector {

	/*
	 * @see IWordDetector#isWordStart
	 */
	@Override
	public boolean isWordStart(final char c) {
		return c == '#';
	}

	/*
	 * @see IWordDetector#isWordPart
	 */
	@Override
	public boolean isWordPart(final char c) {
		return c == '*' || c == '#';
	}
}

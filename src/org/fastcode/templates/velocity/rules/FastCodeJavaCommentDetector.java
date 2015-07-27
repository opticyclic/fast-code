package org.fastcode.templates.velocity.rules;

import static org.fastcode.common.FastCodeConstants.NEW_LINE_CHAR;

import org.eclipse.jface.text.rules.IWordDetector;

public class FastCodeJavaCommentDetector implements IWordDetector {

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	@Override
	public boolean isWordStart(final char aChar) {
		return aChar == '/' ;
	}

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	@Override
	public boolean isWordPart(final char aChar) {
		//return Character.isLetterOrDigit(aChar) || aChar == '*' || aChar == '/';
		return true;
	}
}

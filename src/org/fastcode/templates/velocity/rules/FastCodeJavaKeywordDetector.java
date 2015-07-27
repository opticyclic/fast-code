package org.fastcode.templates.velocity.rules;

import org.eclipse.jface.text.rules.IWordDetector;

public class FastCodeJavaKeywordDetector implements IWordDetector {

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	@Override
	public boolean isWordStart(final char aChar) {
		return Character.isLetter(aChar);
		//return aChar == 'f' || aChar == 'b' || aChar == 'h' || aChar == 'j';
	}

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	@Override
	public boolean isWordPart(final char aChar) {
		return Character.isLetter(aChar);
		//return Character.isLetterOrDigit(aChar) || aChar == '<' || aChar == ':';
	}
}

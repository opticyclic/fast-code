package org.fastcode.templates.rules;

import org.eclipse.jface.text.rules.IWordDetector;
import static org.fastcode.common.FastCodeConstants.SPACE_CHAR;

public class ColonDetector implements IWordDetector {

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	@Override
	public boolean isWordStart(final char aChar) {
		return aChar == ':' || aChar == '(' || aChar == SPACE_CHAR;
	}

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	@Override
	public boolean isWordPart(final char aChar) {
		return Character.isLetterOrDigit(aChar) || aChar == SPACE_CHAR;
	}

}

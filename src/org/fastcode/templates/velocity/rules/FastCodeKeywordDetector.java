package org.fastcode.templates.velocity.rules;

import static org.fastcode.common.FastCodeConstants.SPACE_CHAR;

import org.eclipse.jface.text.rules.IWordDetector;

public class FastCodeKeywordDetector implements IWordDetector {

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
	 */
	@Override
	public boolean isWordStart(final char aChar) {
		/*if (aChar == 'f') {
			System.out.println("line 14 -- true;" + aChar);
		} else {
			System.out.println("line 16 -- false" + aChar);
		}*/
		return aChar == 'f'; //|| aChar == 'b' || aChar == 'h' || aChar == 'j';
	}

	/*
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
	 */
	@Override
	public boolean isWordPart(final char aChar) {
		//return Character.isLetterOrDigit(aChar);
		/*if (Character.isLetter(aChar) || aChar == '<' || aChar == ':' || aChar == SPACE_CHAR) {
			System.out.println("line 26 -- true->" + aChar);
		} else {
			System.out.println("line 28 -- false-->" + aChar);
		}*/
		//return Character.isLetter(aChar) || aChar == '<' || aChar == ':' || aChar == SPACE_CHAR || aChar == '=';

		return Character.isLetter(aChar) || aChar == '<' || aChar == ':';
	}
}

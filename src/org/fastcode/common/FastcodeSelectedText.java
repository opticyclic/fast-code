package org.fastcode.common;

import org.fastcode.util.StringUtil;

public class FastcodeSelectedText extends FastCodeEntity {

	private static String	selectedText;

	/**
	 * @param itext
	 * @throws Exception
	 */

	public FastcodeSelectedText(final String iText) {
		super();
		if (iText == null || StringUtil.isEmpty(iText)) {
			this.isEmpty = true;
			return;
		}
		this.selectedText = iText;

	}

	public String getSelectedText() {
		return this.selectedText;
	}

	public static String text() {
		return selectedText;
	}

	public static String replaceAll(String chartoreplace, String replacewithchar) {

		if (chartoreplace.equals("\\n")) {
			chartoreplace = "\r\n|\r|\n";
		}
		if (replacewithchar.equals("\\n")) {
			replacewithchar = "\r\n";
		}
		final String convertedText = selectedText.replaceAll(chartoreplace, replacewithchar);
		return convertedText;
	}
}

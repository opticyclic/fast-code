/**
 *
 */
package org.fastcode.common;

/**
 * @author Gautam
 *
 */
public enum SnippetType {
	SNIPPET, METHOD("method"), FIELD("field"), CLASS("class"), POJO_METHOD("pojo_method");

	private String	type;

	private SnippetType() {
		this(null);
	}

	/**
	 *
	 * @param type
	 */
	private SnippetType(final String type) {
		this.type = type;
	}

	/**
	 *
	 * @param type
	 * @return
	 */
	public static SnippetType getType(final String type) {
		if (type == null) {
			return SNIPPET;
		}
		for (final SnippetType snippetType : values()) {
			if (type.equalsIgnoreCase(snippetType.type)) {
				return snippetType;
			}
		}
		return null;
	}
}

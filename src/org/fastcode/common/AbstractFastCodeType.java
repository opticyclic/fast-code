package org.fastcode.common;

public class AbstractFastCodeType {

	private final String		name;
	private final FastCodeType	type;

	/**
	 * @param name
	 * @param type
	 */
	public AbstractFastCodeType(final String name, final FastCodeType type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public FastCodeType getType() {
		return this.type;
	}
}

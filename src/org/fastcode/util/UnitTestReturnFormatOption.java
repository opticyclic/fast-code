package org.fastcode.util;

public class UnitTestReturnFormatOption {

	private final String	name;
	private final String	valueType;
	private final boolean	requireValue;
	private final String	methodBody;

	/**
	 * @param name
	 * @param valueType
	 * @param requireValue
	 * @param methodBody
	 */
	public UnitTestReturnFormatOption(final String name, final String valueType, final boolean requireValue, final String methodBody) {
		super();
		this.name = name;
		this.valueType = valueType;
		this.requireValue = requireValue;
		this.methodBody = methodBody;
	}

	public String getName() {
		return this.name;
	}

	public String getValueType() {
		return this.valueType;
	}

	public boolean getRequireValue() {
		return this.requireValue;
	}

	public String getMethodBody() {
		return this.methodBody;
	}

}

package org.fastcode.common.test;

public class MockDBField {

	private final String	name;
	private final String	fullName;
	private final String	type;
	private final String	value;
	private final String	javaName;
	MockDBField				idField;
	private final boolean	nullable;
	private final int		size;
	private final String	tableName;
	private final String	javaTypeName;

	/**
	 * @param name
	 * @param fullName
	 */
	public MockDBField(final String name, final String fullName, final String type, final String value, final int size,
			final boolean nullable, final String javaName, final MockDBField idField, final String javaTypeName, final String tableName) {
		super();
		this.name = name;
		this.fullName = fullName;
		this.type = type;
		this.value = value;
		this.javaName = javaName;
		this.size = size;
		this.nullable = nullable;
		this.idField = idField;
		this.tableName = tableName;
		this.javaTypeName = javaTypeName;

	}

	public MockDBField(final String name, final String fullName, final String type, final String value, final int size,
			final boolean nullable, final String javaTypeName, final String tableName) {
		this(name, fullName, type, value, size, nullable, null, null, javaTypeName, tableName);
	}

	/**
	 *
	 * getter method for name
	 * @return
	 *
	 */
	public String getName() {
		return this.name;
	}

	/**
	 *
	 * getter method for fullName
	 * @return
	 *
	 */
	public String getFullName() {
		return this.fullName;
	}

	public String getType() {
		return this.type;
	}

	/**
	 *
	 * getter method for value
	 * @return
	 *
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 *
	 * getter method for javaName
	 * @return
	 *
	 */
	public String getJavaName() {
		return this.javaName;
	}

	/**
	 *
	 * getter method for nullable
	 * @return
	 *
	 */
	public boolean getNullable() {
		return this.nullable;
	}

	/**
	 *
	 * getter method for size
	 * @return
	 *
	 */
	public int getSize() {
		return this.size;
	}

	public MockDBField getIdField() {
		return this.idField;
	}

	public void setIdField(final MockDBField idField) {
		this.idField = idField;
	}

	public String getTableName() {
		return this.tableName;
	}

	public String getJavaTypeName() {
		return this.javaTypeName;
	}

}

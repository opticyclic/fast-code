package org.fastcode.common;

public class FastCodeDataBaseField extends AbstractFastCodeField {

	private final Boolean				nullable;
	protected int						size;
	private final FastCodeDataBaseField	id;
	private final String				javaName;
	private final String				tableName;
	private final String				javaTypeName;
	private final String				type;
	private String						alaisName;

	/**
	 * @param name
	 * @param type
	 * @param value
	 * @param size
	 * @param nullable
	 * @param javaName
	 * @param javaTypeName
	 * @param tableName
	 * @param id
	 */
	public FastCodeDataBaseField(final String name, final String type, final String value, final int size, final boolean nullable,
			final String javaName, final String javaTypeName, final String tableName, final FastCodeDataBaseField id) {
		super(name, value);
		this.nullable = nullable;
		this.id = id;
		this.size = size;
		this.javaName = javaName;
		this.tableName = tableName;
		this.javaTypeName = javaTypeName;
		this.type = type;
		this.alaisName = this.name;
	}

	/**
	 * @param name
	 * @param type
	 * @param value
	 * @param size
	 * @param nullable
	 */
	public FastCodeDataBaseField(final String name, final String type, final String value, final int size, final boolean nullable) {
		this(name, type, value, size, nullable, null, null, null, null);
	}

	public FastCodeDataBaseField(final String name, final String dataType, final String javaTypeName) {
		this(name, dataType, "0", 0, false, null, javaTypeName, null, null);
	}

	/**
	 *
	 * getter method for nullable
	 * @return
	 *
	 */
	public Boolean isNullable() {
		return this.nullable;
	}

	public FastCodeDataBaseField getId() {
		return this.id;
	}

	public int getSize() {
		return this.size;
	}

	public String getJavaName() {
		return this.javaName;
	}

	/*public String getInstance() {
		return this.tableName;
	}*/

	public String getTableName() {
		return this.tableName;
	}

	public String getJavaTypeName() {
		return this.javaTypeName;
	}

	public String getType() {
		return this.type;
	}

	public String getAlaisName() {
		return this.alaisName;
	}

	public void setAlaisName(final String alaisName) {
		this.alaisName = alaisName;
	}
}

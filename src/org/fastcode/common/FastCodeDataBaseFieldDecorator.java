package org.fastcode.common;

import org.fastcode.common.FastCodeConstants.Qualifier;
import org.fastcode.util.SQLFunctions;

public class FastCodeDataBaseFieldDecorator {

	private FastCodeDataBaseField	fastCodeDataBaseField;
	private Qualifier					whereQualifier;
	private SQLFunctions sqlFunction;

	public SQLFunctions getSQLFunction() {
		return this.sqlFunction;
	}

	public void setSQLFunction(final SQLFunctions func) {
		this.sqlFunction = func;
	}

	public FastCodeDataBaseField getFastCodeDataBaseField() {
		return this.fastCodeDataBaseField;
	}

	/**
	 * @param fastCodeDataBaseField
	 */
	public void setFastCodeDataBaseField(final FastCodeDataBaseField fastCodeDataBaseField) {
		this.fastCodeDataBaseField = fastCodeDataBaseField;
	}

	public Qualifier getWhereQualifier() {
		return this.whereQualifier;
	}

	public void setWhereQualifier(final Qualifier whereQualifier) {
		this.whereQualifier = whereQualifier;
	}

	public FastCodeDataBaseFieldDecorator(final FastCodeDataBaseField fastCodeDataBaseField, final Qualifier whereQualifier) {
		this.fastCodeDataBaseField = fastCodeDataBaseField;
		this.whereQualifier = whereQualifier;
	}

	public FastCodeDataBaseFieldDecorator() {
		// TODO Auto-generated constructor stub
	}



	public String getName() {
		return this.fastCodeDataBaseField.getName();
	}

	public String getType() {
		return this.fastCodeDataBaseField.getType();
	}

	public String getValue() {
		return this.fastCodeDataBaseField.getValue();
	}

	public FastCodeDataBaseField getId() {
		return this.fastCodeDataBaseField.getId();
	}

	public int getSize() {
		return this.fastCodeDataBaseField.getSize();
	}

	public String getJavaName() {
		return this.fastCodeDataBaseField.getJavaName();
	}

	public String getTableName() {
		return this.fastCodeDataBaseField.getTableName();
	}

	@Override
	public int hashCode() {
		return this.fastCodeDataBaseField.hashCode();
	}

	public Boolean isNullable() {
		return this.fastCodeDataBaseField.isNullable();
	}

	public String getJavaTypeName() {
		return this.fastCodeDataBaseField.getJavaTypeName();
	}

	/*public String toString() {
		return fastCodeDataBaseField.toString();
	}
*/
}

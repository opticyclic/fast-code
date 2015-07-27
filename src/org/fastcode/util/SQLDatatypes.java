package org.fastcode.util;

import java.util.ArrayList;
import java.util.Map;

public class SQLDatatypes {

	private String	type;
	private String	defaultvalue;
	private String	length;
	private String	precision;

	/**
	 * @param type
	 * @param defaultvalue
	 * @param length
	 * @param precision
	 */
	public SQLDatatypes(final String type, final String defaultvalue, final String length, final String precision) {

		this.type = type;
		this.defaultvalue = defaultvalue;
		this.length = length;
		this.precision = precision;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDefaultvalue() {
		return defaultvalue;
	}

	public void setDefaultvalue(String defaultvalue) {
		this.defaultvalue = defaultvalue;
	}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public static SQLDatatypes getInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	public static void setdataTypeFunctionsMap(Map<String, ArrayList<SQLDatatypes>> dataInfo) {
		// TODO Auto-generated method stub

	}
}

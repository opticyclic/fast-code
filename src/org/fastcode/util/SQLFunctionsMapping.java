package org.fastcode.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SQLFunctionsMapping {

	ArrayList<String>	databases	= new ArrayList<String>();

	public ArrayList<String> getDatabases() {
		return this.databases;
	}

	public void setDatabases(final ArrayList<String> databases) {
		this.databases = databases;
	}

	ArrayList<SQLFunctions>					functionsList			= new ArrayList<SQLFunctions>();
	Map<String, ArrayList<String>>			dbDataTypeMap			= new HashMap<String, ArrayList<String>>();
	Map<String, ArrayList<SQLFunctions>>	dataTypeFunctionsMap	= new HashMap<String, ArrayList<SQLFunctions>>();

	private static SQLFunctionsMapping		SQLFunctionsMapping		= new SQLFunctionsMapping();

	private SQLFunctionsMapping() {

	}

	public static SQLFunctionsMapping getInstance() {
		return SQLFunctionsMapping.SQLFunctionsMapping;
	}

	public ArrayList<SQLFunctions> getFunctionsList() {
		return this.functionsList;
	}

	public void setFunctionsList(final ArrayList<SQLFunctions> functionsList) {
		this.functionsList = functionsList;
	}

	public Map<String, ArrayList<String>> getDbDataTypeMap() {
		return this.dbDataTypeMap;
	}

	public void setDbDataTypeMap(final Map<String, ArrayList<String>> dbDataTypeMap) {
		this.dbDataTypeMap = dbDataTypeMap;
	}

	public Map<String, ArrayList<SQLFunctions>> getDataTypeFunctionsMap() {
		return this.dataTypeFunctionsMap;
	}

	public void setDataTypeFunctionsMap(final Map<String, ArrayList<SQLFunctions>> dataTypeFunctionsMap) {
		this.dataTypeFunctionsMap = dataTypeFunctionsMap;
	}

	public SQLFunctions getSQLFunction(final String fname) {

		for (final SQLFunctions func : this.functionsList) {
			if (fname.equals(func.getName().trim())) {
				return func;

			}
		}

		return null;
	}
}

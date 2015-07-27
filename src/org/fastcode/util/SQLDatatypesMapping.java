package org.fastcode.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SQLDatatypesMapping {

	private Map<String, Map<String, ArrayList<SQLDatatypes>>>	databaseDataTypeMap			= new HashMap<String, Map<String, ArrayList<SQLDatatypes>>>();

	private Map<String, SQLDatatypes>							dataTypeNameAndDetailsMap	= new HashMap<String, SQLDatatypes>();

	private static SQLDatatypesMapping							sqlDatatypesMapping			= new SQLDatatypesMapping();

	private SQLDatatypesMapping() {

	}

	public static SQLDatatypesMapping getInstance() {
		return sqlDatatypesMapping;
	}

	public Map<String, Map<String, ArrayList<SQLDatatypes>>> getDatabaseDataTypeMap() {
		return databaseDataTypeMap;
	}

	public void setDatabaseDataTypeMap(Map<String, Map<String, ArrayList<SQLDatatypes>>> databaseDataTypeMap) {
		this.databaseDataTypeMap = databaseDataTypeMap;
	}

	public Map<String, SQLDatatypes> getDataTypeNameAndDetailsMap() {
		return dataTypeNameAndDetailsMap;
	}

	public void setDataTypeNameAndDetailsMap(Map<String, SQLDatatypes> dataTypeNameAndDetailsMap) {
		this.dataTypeNameAndDetailsMap = dataTypeNameAndDetailsMap;
	}

}

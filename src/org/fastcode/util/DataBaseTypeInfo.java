package org.fastcode.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.fastcode.preferences.DatabaseConnectionSettings;

public class DataBaseTypeInfo {

	static String							databaseType;
	private ArrayList<String>				primaryKeyColumns	= new ArrayList<String>();
	static Map<String, ArrayList<String>>	primaryKeyMap		= new HashMap<String, ArrayList<String>>();

	private static DataBaseTypeInfo			dataBaseTypeInfo	= new DataBaseTypeInfo();

	private static boolean					reload;

	private DataBaseTypeInfo() {

		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		DataBaseTypeInfo.databaseType = databaseConnectionSettings.getTypesofDabases();
	}

	public static DataBaseTypeInfo getInstance() {
		if (reload) {
			reload = false;
			dataBaseTypeInfo = new DataBaseTypeInfo();

		}

		return DataBaseTypeInfo.dataBaseTypeInfo;
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public ArrayList<String> getPrimaryKeyColumns(final String tableNameWithSchema) {

		this.primaryKeyColumns = dataBaseTypeInfo.primaryKeyMap.get(tableNameWithSchema);
		return this.primaryKeyColumns;

	}

	public static void setReload(final boolean areload) {
		reload = areload;

	}
}

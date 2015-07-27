package org.fastcode.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fastcode.common.FastCodeDataBaseFieldDecorator;

public class DatabaseCache {

	Map<String, List<String>>							dbTableListMap				= new HashMap<String, List<String>>();
	Map<String, List<FastCodeDataBaseFieldDecorator>>	tableNameFieldDetailsMap	= new HashMap<String, List<FastCodeDataBaseFieldDecorator>>();
	Map<String, List<String>>							tableNameColumnListMap		= new HashMap<String, List<String>>();
	Map<String, List<String>>							notNullColumnListMap		= new HashMap<String, List<String>>();
	Map<String, List<String>>							dbSchemaListMap				= new HashMap<String, List<String>>();
	private static DatabaseCache						databaseCache				= new DatabaseCache();

	private DatabaseCache() {

	}

	/**
	 *
	 * @return
	 */
	public static DatabaseCache getInstance() {
		return databaseCache;
	}

	/**
	 * @return the dbTableListMap
	 */
	public Map<String, List<String>> getDbTableListMap() {
		return this.dbTableListMap;
	}

	/**
	 * @return the tableNameFieldDetailsMap
	 */
	public Map<String, List<FastCodeDataBaseFieldDecorator>> getTableNameFieldDetailsMap() {
		return this.tableNameFieldDetailsMap;
	}

	/**
	 * @return the tableNameColumnListMap
	 */
	public Map<String, List<String>> getTableNameColumnListMap() {
		return this.tableNameColumnListMap;
	}

	public Map<String, List<String>> getNotNullColumnListMap() {
		return this.notNullColumnListMap;
	}

	public Map<String, List<String>> getDbSchemaListMap() {
		return this.dbSchemaListMap;
	}
}

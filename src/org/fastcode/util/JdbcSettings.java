/*
 *
 */
package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.util.FileUtil.retrievePropertiesFromFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class JdbcSettings {
	final Map<String, String>		fieldTypeMethodMap	= new HashMap<String, String>();
	final Map<String, String>		fieldTypeImportMap	= new HashMap<String, String>();
	private static JdbcSettings		jdbcSettings		= new JdbcSettings();
	private static final String[]	PROPERTIES_FILE		= { "jdbc.properties", "datatypeconversion.properties" };

	private JdbcSettings() {

	}

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	public static JdbcSettings getInstance() throws Exception {
		jdbcSettings.readProperties();
		return jdbcSettings;

	}

	/**
	 * @throws Exception
	 *
	 */
	private void readProperties() throws Exception {
		populateDataMapPropertyFile("resources/" + PROPERTIES_FILE[0], this.fieldTypeMethodMap);
		populateDataMapPropertyFile("resources/" + PROPERTIES_FILE[1], this.fieldTypeImportMap);
	}

	/**
	 *
	 * @param propertyFile
	 * @param fieldTypeMap
	 * @throws Exception
	 */
	private void populateDataMapPropertyFile(final String propertyFile, final Map<String, String> fieldTypeMap) throws Exception {
		final Properties properties = retrievePropertiesFromFile(propertyFile);
		for (final Entry<Object, Object> entry : properties.entrySet()) {
			final String key = (String) entry.getKey();
			final String value = (String) entry.getValue();
			final String[] fieldTypes = value.split(COMMA);
			for (final String fieldType : fieldTypes) {
				fieldTypeMap.put(fieldType.trim(), key.trim());
			}
		}
	}

	public Map<String, String> getFieldTypeMethodMap() {
		return this.fieldTypeMethodMap;
	}

	public Map<String, String> getFieldTypeImportMap() {
		return this.fieldTypeImportMap;
	}
}

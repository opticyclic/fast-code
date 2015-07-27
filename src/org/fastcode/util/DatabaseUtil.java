package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.BIGINT;
import static org.fastcode.common.FastCodeConstants.BINARY;
import static org.fastcode.common.FastCodeConstants.BIT;
import static org.fastcode.common.FastCodeConstants.BOOLEAN;
import static org.fastcode.common.FastCodeConstants.BYTEA;
import static org.fastcode.common.FastCodeConstants.DATE;
import static org.fastcode.common.FastCodeConstants.DATETIME;
import static org.fastcode.common.FastCodeConstants.DECIMAL;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.DOUBLE;
import static org.fastcode.common.FastCodeConstants.DOUBLEPRECISION;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.FLOAT;
import static org.fastcode.common.FastCodeConstants.HSQLDB;
import static org.fastcode.common.FastCodeConstants.HSQLDB_ALL_COLUMN_DETAILS_SELECT;
import static org.fastcode.common.FastCodeConstants.HSQLDB_ALL_TABLES;
import static org.fastcode.common.FastCodeConstants.INT;
import static org.fastcode.common.FastCodeConstants.INTEGER;
import static org.fastcode.common.FastCodeConstants.LONGVARBINARY;
import static org.fastcode.common.FastCodeConstants.MYSQL;
import static org.fastcode.common.FastCodeConstants.MYSQL_ALL_COLUMN_DETAILS_SELECT_PART1;
import static org.fastcode.common.FastCodeConstants.MYSQL_ALL_COLUMN_DETAILS_SELECT_PART2;
import static org.fastcode.common.FastCodeConstants.MYSQL_ALL_DB_SELECT;
import static org.fastcode.common.FastCodeConstants.MYSQL_ALL_TABLES_SELECT;
import static org.fastcode.common.FastCodeConstants.MYSQL_PRIMARY_KEY_SELECT_FIRST_PART;
import static org.fastcode.common.FastCodeConstants.MYSQL_PRIMARY_KEY_SELECT_SECOND_PART;
import static org.fastcode.common.FastCodeConstants.N;
import static org.fastcode.common.FastCodeConstants.NO;
import static org.fastcode.common.FastCodeConstants.NUMBER;
import static org.fastcode.common.FastCodeConstants.NUMBER_OF_RECORDS;
import static org.fastcode.common.FastCodeConstants.NUMERIC;
import static org.fastcode.common.FastCodeConstants.ORACLE;
import static org.fastcode.common.FastCodeConstants.ORACLE_ALL_COLUMN_DETAILS_SELECT_PART1;
import static org.fastcode.common.FastCodeConstants.ORACLE_ALL_COLUMN_DETAILS_SELECT_PART2;
import static org.fastcode.common.FastCodeConstants.ORACLE_ALL_SCHEMA_SELECT;
import static org.fastcode.common.FastCodeConstants.ORACLE_PRIMARY_KEY_SELECT_FIRST_PART;
import static org.fastcode.common.FastCodeConstants.ORACLE_PRIMARY_KEY_SELECT_SECOND_PART;
import static org.fastcode.common.FastCodeConstants.ORACLE_TABLES_SELECT_FROM_SCHEMA;
import static org.fastcode.common.FastCodeConstants.POSTGRESQL;
import static org.fastcode.common.FastCodeConstants.POSTGRESQL_ALL_COLUMN_DETAILS_SELECT_PART1;
import static org.fastcode.common.FastCodeConstants.POSTGRESQL_ALL_COLUMN_DETAILS_SELECT_PART2;
import static org.fastcode.common.FastCodeConstants.POSTGRESQL_ALL_SCHEMA_SELECT;
import static org.fastcode.common.FastCodeConstants.POSTGRESQL_PRIMARY_KEY_SELECT_FIRST_PART;
import static org.fastcode.common.FastCodeConstants.POSTGRESQL_PRIMARY_KEY_SELECT_SECOND_PART;
import static org.fastcode.common.FastCodeConstants.POSTGRESQL_TABLES_SELECT_FROM_SCHEMA;
import static org.fastcode.common.FastCodeConstants.REAL;
import static org.fastcode.common.FastCodeConstants.SINGLE_QUOTATION_MARK;
import static org.fastcode.common.FastCodeConstants.SMALLINT;
import static org.fastcode.common.FastCodeConstants.SQLSERVER;
import static org.fastcode.common.FastCodeConstants.SYBASE;
import static org.fastcode.common.FastCodeConstants.SYBASE_ALL_COLUMN_DETAILS_SELECT;
import static org.fastcode.common.FastCodeConstants.SYBASE_ALL_TABLES;
import static org.fastcode.common.FastCodeConstants.TIME;
import static org.fastcode.common.FastCodeConstants.TIMESTAMP;
import static org.fastcode.common.FastCodeConstants.TIMESTAMP6;
import static org.fastcode.common.FastCodeConstants.TINYINT;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.common.FastCodeConstants.VARBINARY;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_CONN_DATA;
import static org.fastcode.preferences.PreferenceConstants.P_DBCONN_FIELD_DELIMITER;
import static org.fastcode.preferences.PreferenceConstants.P_DBCONN_RECORD_DELIMITER;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.reverseCamelCase;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.Activator;
import org.fastcode.common.DatabaseDetails;
import org.fastcode.common.FastCodeDataBaseField;
import org.fastcode.common.FastCodeDataBaseFieldDecorator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DatabaseUtil {

	/**
	 * @param con
	 * @throws Exception
	 */
	public static void getTableFromDb(final Connection con, final String schemaName, final String databaseType) throws Exception {
		final DatabaseCache databaseCache = DatabaseCache.getInstance();
		final List<String> tablesOfDb = new ArrayList<String>();
		ResultSet rs = null;
		Statement select = null;
		String[] tablesOfDatabase = null;
		//final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		if (!databaseCache.dbTableListMap.containsKey(schemaName)) {
			try {
				select = con.createStatement();
				if (databaseType.equals(MYSQL)) {
					rs = select.executeQuery(MYSQL_ALL_TABLES_SELECT + schemaName + SINGLE_QUOTATION_MARK);
				} else if (databaseType.equals(ORACLE)) {
					rs = select.executeQuery(ORACLE_TABLES_SELECT_FROM_SCHEMA + schemaName + SINGLE_QUOTATION_MARK);
				} else if (databaseType.equals(SQLSERVER)) {
					// rs=select.executeQuery(SQLSERVER_ALL_TABLES); nt yet
					// written
				} else if (databaseType.equals(HSQLDB)) {
					rs = select.executeQuery(HSQLDB_ALL_TABLES);
				} else if (databaseType.equals(SYBASE)) {
					rs = select.executeQuery(SYBASE_ALL_TABLES);
				} else if (databaseType.equals(POSTGRESQL)) {
					rs = select.executeQuery(POSTGRESQL_TABLES_SELECT_FROM_SCHEMA + schemaName + SINGLE_QUOTATION_MARK);
				}
				while (rs != null && rs.next()) {
					tablesOfDb.add(rs.getString(1));
				}
				databaseCache.dbTableListMap.put(schemaName, tablesOfDb);
			} catch (final Exception ex) {
				ex.printStackTrace();
				throw ex;

			} finally {
				if (rs != null) {
					rs.close();
				}
				if (select != null) {
					select.close();
				}
			}

		}

		tablesOfDatabase = databaseCache.dbTableListMap.get(schemaName).toArray(new String[0]);
	}

	/**
	 * @param tableName
	 * @param con
	 * @return
	 * @throws Exception
	 */
	public static String[] getTableColumnsFromDB(final String tableName, final Connection con, final String schemaName,
			final String databaseType) throws Exception {
		final DatabaseCache databaseCache = DatabaseCache.getInstance();
		if (!databaseCache.tableNameColumnListMap.containsKey(schemaName + DOT + tableName)) {
			populateColumnDetails(tableName, con, databaseCache, schemaName, databaseType);
		}
		// show dailog box with all the columns
		//final String[] columnList = this.databaseCache.tableNameColumnListMap.get(this.tableName).toArray(new String[0]);
		final String[] columnListDetail = databaseCache.tableNameColumnListMap.get(schemaName + DOT + tableName).toArray(new String[0]);

		return columnListDetail;

	}

	/**
	 * @param tableName
	 * @param con
	 * @param databaseCache
	 * @param databaseType
	 * @throws Exception
	 */
	private static void populateColumnDetails(final String tableName, final Connection con, final DatabaseCache databaseCache,
			final String schemaName, final String databaseType) throws Exception {
		final List<String> columnsList = new ArrayList<String>();
		final List<String> columnsListDetail = new ArrayList<String>();
		final List<String> notNullcolumnsList = new ArrayList<String>();
		ResultSet rs = null;
		ResultSet rsPk = null;
		Statement select = null;
		Statement selectPk = null;
		FastCodeDataBaseField fastCodeDataBasePrimaryKey = null;
		final List<FastCodeDataBaseFieldDecorator> tableColumnDetList = new ArrayList<FastCodeDataBaseFieldDecorator>();
		// String pkColName = null;
		final ArrayList<String> pkColNameList = new ArrayList<String>();
		//final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		final DataBaseTypeInfo databaseTypeInfo = DataBaseTypeInfo.getInstance();
		try {
			select = con.createStatement();
			selectPk = con.createStatement();
			if (databaseType.equals(MYSQL)) {
				rsPk = selectPk.executeQuery(MYSQL_PRIMARY_KEY_SELECT_FIRST_PART + tableName + MYSQL_PRIMARY_KEY_SELECT_SECOND_PART
						+ schemaName + SINGLE_QUOTATION_MARK);
				rs = select.executeQuery(MYSQL_ALL_COLUMN_DETAILS_SELECT_PART1 + tableName + MYSQL_ALL_COLUMN_DETAILS_SELECT_PART2
						+ schemaName + SINGLE_QUOTATION_MARK);
			} else if (databaseType.equals(ORACLE)) {
				rsPk = selectPk.executeQuery(ORACLE_PRIMARY_KEY_SELECT_FIRST_PART + tableName + ORACLE_PRIMARY_KEY_SELECT_SECOND_PART
						+ schemaName + SINGLE_QUOTATION_MARK);
				rs = select.executeQuery(ORACLE_ALL_COLUMN_DETAILS_SELECT_PART1 + tableName + SINGLE_QUOTATION_MARK
						+ ORACLE_ALL_COLUMN_DETAILS_SELECT_PART2 + schemaName + SINGLE_QUOTATION_MARK);

			} else if (databaseType.equals(SQLSERVER)) {
				// rs=select.executeQuery();...nt yet written
			} else if (databaseType.equals(HSQLDB)) {
				rs = select.executeQuery(HSQLDB_ALL_COLUMN_DETAILS_SELECT + tableName + SINGLE_QUOTATION_MARK);
			} else if (databaseType.equals(SYBASE)) {
				rs = select.executeQuery(SYBASE_ALL_COLUMN_DETAILS_SELECT + tableName + SINGLE_QUOTATION_MARK);
			} else if (databaseType.equals(POSTGRESQL)) {
				rsPk = selectPk.executeQuery(POSTGRESQL_PRIMARY_KEY_SELECT_FIRST_PART + schemaName + DOT + tableName
						+ POSTGRESQL_PRIMARY_KEY_SELECT_SECOND_PART);
				rs = select.executeQuery(POSTGRESQL_ALL_COLUMN_DETAILS_SELECT_PART1 + tableName
						+ POSTGRESQL_ALL_COLUMN_DETAILS_SELECT_PART2 + schemaName + SINGLE_QUOTATION_MARK);
			}
			if (rsPk != null) {
				while (rsPk.next()) {
					pkColNameList.add(rsPk.getString(1));
					notNullcolumnsList.add(rsPk.getString(1));
				}
				databaseTypeInfo.primaryKeyMap.put(schemaName + DOT + tableName, pkColNameList);
			}
			if (rs != null) {
				while (rs.next()) {

					columnsList.add(rs.getString(1));
					final String colName = rs.getString(1);
					String colType = rs.getString(2);
					final int colSize = rs.getInt(3);
					final boolean nullable = rs.getString(4).equalsIgnoreCase(NO) || rs.getString(4).equalsIgnoreCase(N) ? false : true;

					if (!nullable) {
						if (!pkColNameList.isEmpty()) {
							for (final String pkColName : pkColNameList) {
								if (pkColName != null && !pkColName.equals(colName)) {
									notNullcolumnsList.add(colName);
								}
							}
						} else {
							notNullcolumnsList.add(colName);
						}
					}
					/*	for (final String pkColName : pkColNameList) {
							if (!nullable && pkColName == null) {
								notNullcolumnsList.add(colName);
							} else if (!nullable && pkColName != null) {
								if (!pkColName.equals(colName)) {
									notNullcolumnsList.add(colName);
								}
							}
						}
					*/
					if (databaseType.equals(ORACLE)) {
						final int colDataPrecision = rs.getInt(5);
						final int colDataScale = rs.getInt(6);
						if (colDataPrecision >= 1 && colDataScale > 0) {
							colType = DECIMAL.toUpperCase(); // just for
																// displaying in
																// dailaog
																// box(in oracle
																// col data
																// types is in
																// uppser case)
						}
					}

					String defaultValue = "\"\"";

					final String javaColName = reverseCamelCase(colName, UNDERSCORE.charAt(0));

					if (colType.toLowerCase().equals(NUMBER) || colType.toLowerCase().equals(INT) || colType.toLowerCase().equals(INTEGER)
							|| colType.toLowerCase().equals(DOUBLE) || colType.toLowerCase().equals(FLOAT)
							|| colType.toLowerCase().equals(BIGINT) || colType.toLowerCase().equals(DOUBLEPRECISION)
							|| colType.toLowerCase().equals(REAL)) {
						defaultValue = "0";
					} else if (colType.toLowerCase().equals(DATE)) {
						defaultValue = "new Date(0000-00-00)";
					} else if (colType.toLowerCase().equals(DECIMAL) || colType.toLowerCase().equals(NUMERIC)) {
						defaultValue = "new BigDecimal(\"\")";
					} else if (colType.toLowerCase().equals(TIMESTAMP) || colType.toLowerCase().equals(TIMESTAMP6)
							|| colType.toLowerCase().equals(DATETIME) || colType.toLowerCase().indexOf("with") != -1
							&& colType.toLowerCase().substring(0, colType.toLowerCase().indexOf("with")).trim().equals(TIMESTAMP)) {
						defaultValue = "new Timestamp(00-00-00)";
					} else if (colType.toLowerCase().equals(TIME) || colType.toLowerCase().indexOf("with") != -1
							&& colType.toLowerCase().substring(0, colType.toLowerCase().indexOf("with")).trim().equals(TIME)) {
						defaultValue = "new Time(0)";
					} else if (colType.toLowerCase().equals(SMALLINT)) {
						defaultValue = "(short)0";
					} else if (colType.toLowerCase().equals(BINARY) || colType.toLowerCase().equals(LONGVARBINARY)
							|| colType.toLowerCase().equals(TINYINT) || colType.toLowerCase().equals(VARBINARY)
							|| colType.toLowerCase().equals(BYTEA)) {
						defaultValue = "(byte)0";
					} else if (colType.toLowerCase().equals(BIT) || colType.toLowerCase().equals(BOOLEAN)) {
						defaultValue = "true";
					}
					final String javaTypeName = getJavaTypeNameOfColType(colType.toLowerCase().trim());
					if (pkColNameList.isEmpty()) {
						tableColumnDetList.add(new FastCodeDataBaseFieldDecorator(new FastCodeDataBaseField(colName, colType, defaultValue,
								colSize, nullable, javaColName, javaTypeName, tableName, null), null));
						columnsListDetail.add(colName + "-       " + colType + ",    " + colSize + ",    "
								+ (nullable ? "Nullable" : "Not Nullable"));
					} else {
						if (isPrimaryKeyColumn(colName, pkColNameList)) {
							fastCodeDataBasePrimaryKey = new FastCodeDataBaseField(colName, colType, defaultValue, colSize, false);
							tableColumnDetList.add(0, new FastCodeDataBaseFieldDecorator(new FastCodeDataBaseField(colName, colType,
									defaultValue, colSize, nullable, javaColName, javaTypeName, tableName, fastCodeDataBasePrimaryKey),
									null));// call 1st constructr...
							columnsListDetail.add(colName + "-       " + "Primary Key" + ",    " + colType + ",   " + colSize + ",    "
									+ (nullable ? "Nullable" : "Not Nullable"));

						} else {
							tableColumnDetList.add(new FastCodeDataBaseFieldDecorator(new FastCodeDataBaseField(colName, colType,
									defaultValue, colSize, nullable, javaColName, javaTypeName, tableName, null), null));
							columnsListDetail.add(colName + "-       " + colType + ",    " + colSize + ",    "
									+ (nullable ? "Nullable" : "Not Nullable"));
						}
					}
				}
				databaseCache.notNullColumnListMap.put(schemaName + DOT + tableName, notNullcolumnsList);
				databaseCache.tableNameColumnListMap.put(schemaName + DOT + tableName, columnsListDetail);
				databaseCache.tableNameFieldDetailsMap.put(schemaName + DOT + tableName, tableColumnDetList);
			}
		} catch (final Exception ex) {
			throw new Exception(ex.getMessage(), ex);
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (select != null) {
				select.close();
			}
		}
	}

	/**
	 * @param columnType
	 * @return
	 * @throws Exception
	 */
	private static String getJavaTypeNameOfColType(final String columnType) throws Exception {
		final JdbcSettings jdbcSettings = JdbcSettings.getInstance();
		if (columnType != null) {
			if (jdbcSettings.getFieldTypeImportMap().containsKey(columnType.trim())) {
				return jdbcSettings.getFieldTypeImportMap().get(columnType.trim());
			}
		}
		return null;
	}

	/**
	 *
	 * @param colName
	 * @param pkColNameList
	 * @return
	 */
	private static boolean isPrimaryKeyColumn(final String colName, final ArrayList<String> pkColNameList) {
		for (final String columnName : pkColNameList) {

			if (colName.equals(columnName)) {
				return true;
			}

		}
		return false;
	}

	/**
	 * @param con
	 * @throws Exception
	 */
	public static void getSchemaFromDb(final Connection con, final String databaseType) throws Exception {
		final DatabaseCache databaseCache = DatabaseCache.getInstance();
		final List<String> schemasOfDb = new ArrayList<String>();
		ResultSet rs = null;
		Statement select = null;

		//final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		if (!databaseCache.dbSchemaListMap.containsKey(databaseType)) {
			try {
				select = con.createStatement();
				if (databaseType.equals(ORACLE)) {
					rs = select.executeQuery(ORACLE_ALL_SCHEMA_SELECT);
				} else if (databaseType.equals(POSTGRESQL)) {
					rs = select.executeQuery(POSTGRESQL_ALL_SCHEMA_SELECT);
				} else if (databaseType.equals(MYSQL)) {
					rs = select.executeQuery(MYSQL_ALL_DB_SELECT);
				}
				while (rs != null && rs.next()) {
					schemasOfDb.add(rs.getString(1));
				}
				databaseCache.dbSchemaListMap.put(databaseType, schemasOfDb);
			} catch (final Exception ex) {
				ex.printStackTrace();
				throw ex;

			} finally {
				if (rs != null) {
					rs.close();
				}
				if (select != null) {
					select.close();
				}
			}

		}

	}

	/**
	 * @return
	 */
	public static List<DatabaseDetails> loadConnectionsFromPreferenceStore() {

		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);

		final String RECORD_DELIMITER = preferenceStore.getString(P_DBCONN_RECORD_DELIMITER);
		final String FIELD_DELIMITER = preferenceStore.getString(P_DBCONN_FIELD_DELIMITER);
		final List<DatabaseDetails> connDataList = new ArrayList<DatabaseDetails>();
		final String dbConnRecords = preferenceStore.getString(P_DATABASE_CONN_DATA);
		if (!isEmpty(dbConnRecords)) {
			final String[] recordArray = dbConnRecords.split(RECORD_DELIMITER);
			for (final String record : recordArray) {
				if (!isEmpty(record)) {
					final String[] attrValues = record.split(FIELD_DELIMITER);
					final DatabaseDetails dataFromPref = new DatabaseDetails();
					if (attrValues.length < 7) {
						continue;
					}
					dataFromPref.setDatabaseType(attrValues[0]);
					dataFromPref.setDatabaseName(attrValues[1]);
					dataFromPref.setHostAddress(attrValues[2]);
					dataFromPref.setPort(Integer.parseInt(attrValues[3]));
					dataFromPref.setUserName(attrValues[4]);
					if (attrValues[5].equals(EMPTY_STR)) {
						dataFromPref.setPassword(EMPTY_STR);

					} else {
						dataFromPref.setPassword(attrValues[5]);
					}
					dataFromPref.setDefaultConn(Boolean.parseBoolean(attrValues[6]));
					if (attrValues.length == 9) {
						dataFromPref.setDriverClass(attrValues[7]);
						dataFromPref.setDriverPrj(attrValues[8]);
					}
					connDataList.add(dataFromPref);
				}

			}
		}

		return connDataList;
	}

	/**
	 * @param con
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public static int getNumberOfRecordsInTable(final Connection con, final String SchemaName, final String tableName) throws Exception {
		ResultSet rs = null;
		Statement select = null;

		try {
			select = con.createStatement();
			rs = select.executeQuery(NUMBER_OF_RECORDS + SchemaName + DOT + tableName);

			while (rs != null && rs.next()) {
				return rs.getInt(1);
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw ex;

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (select != null) {
				select.close();
			}
		}
		return 0;
	}

	/**
	 * 
	 */
	public static void parseSQLDatatypes() {

		final Map<String, Map<String, ArrayList<SQLDatatypes>>> databaseDataTypesMap = new HashMap<String, Map<String, ArrayList<SQLDatatypes>>>();
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		InputStream inputStream = null;
		final SQLDatatypesMapping sqldatatypesMapping = SQLDatatypesMapping.getInstance();
		try {
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/sql-datatypes.xml"), false);
			final Document document = docBuilder.parse(inputStream);
			final NodeList databaseList = document.getElementsByTagName("database");
			final int size = databaseList.getLength();
			for (int i = 0; i < size; i++) {
				final Node databaseNode = databaseList.item(i);
				final NamedNodeMap dbattributes = databaseNode.getAttributes();
				final Node dbAttrNode = dbattributes.getNamedItem("name");
				final String name = dbAttrNode.getNodeValue();

				final Map<String, ArrayList<SQLDatatypes>> groupDataTypeMap = new HashMap<String, ArrayList<SQLDatatypes>>();
				Node baseTypeNode = databaseNode.getFirstChild();

				while (baseTypeNode != null) {
					if (baseTypeNode.getNodeType() == Node.ELEMENT_NODE) {
						final NamedNodeMap baseTypeAttr = baseTypeNode.getAttributes();
						final Node groupTypeAttr = baseTypeAttr.getNamedItem("group");
						final String groupType = groupTypeAttr.getNodeValue();

						final List<SQLDatatypes> dataTypesDetailsList = new ArrayList<SQLDatatypes>();

						Node dataTypeDetails = baseTypeNode.getFirstChild();

						while (dataTypeDetails != null) {
							if (dataTypeDetails.getNodeType() == Node.ELEMENT_NODE) {
								final NamedNodeMap attributes = dataTypeDetails.getAttributes();
								Node type = attributes.getNamedItem("type");
								Node defaultValue = attributes.getNamedItem("default-value");
								Node length = attributes.getNamedItem("length");
								Node precision = attributes.getNamedItem("precision");
								SQLDatatypes sqlDatatypes = new SQLDatatypes(type.getNodeValue(), defaultValue.getNodeValue(),
										length.getNodeValue(), precision.getNodeValue());

								dataTypesDetailsList.add(sqlDatatypes);
								sqldatatypesMapping.getDataTypeNameAndDetailsMap().put(type.getNodeValue(), sqlDatatypes);
							}
							dataTypeDetails = dataTypeDetails.getNextSibling();
						}

						groupDataTypeMap.put(groupType, (ArrayList<SQLDatatypes>) dataTypesDetailsList);
					}
					baseTypeNode = baseTypeNode.getNextSibling();
				}
				databaseDataTypesMap.put(name, groupDataTypeMap);
			}

		} catch (final Exception ex) {
			ex.printStackTrace();

		} finally {

			sqldatatypesMapping.setDatabaseDataTypeMap(databaseDataTypesMap);
			FastCodeUtil.closeInputStream(inputStream);
		}

	}
}

package org.fastcode.dialog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.fastcode.Activator;
import org.fastcode.common.FastCodeDataBaseFieldDecorator;
import org.fastcode.util.DataBaseTypeInfo;
import org.fastcode.util.FastCodeUtil;
import org.fastcode.util.SQLFunctions;
import org.fastcode.util.SQLFunctionsMapping;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TableUtil {

	/*
	 * @param fastcodedatabasefielddecorator
	 * checks for the integer and decimal datatypes to modify the where qualifier checkboxes in wherefieldtabledialog
	 *
	 */
	public static boolean isIntegerDataType(final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator) {
		final String dataType = fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().getType().toLowerCase();
		if (dataType.equals("int") || dataType.equals("integer") || dataType.equals("decimal") || dataType.equals("double")
				|| dataType.equals("float") || dataType.equals("real") || dataType.equals("mediumint") || dataType.equals("time")
				|| dataType.equals("date") || dataType.equals("datatime") || dataType.equals("timestamp") || dataType.equals("year")
				|| dataType.equals("number")) {
			return true;

		}
		return false;

	}

	/*
	 * @param fastcodedatabasefielddecorator
	 * checks for the string datatypes to modify the where qualifier checkboxes in wherefieldtabledialog
	 *
	 */
	public static boolean isStringDataType(final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator) {
		final String dataType = fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().getType().toLowerCase();
		if (dataType.equals("varchar") || dataType.equals("text") || dataType.equals("character varying") || dataType.equals("varchar2")
				|| dataType.equals("char") || dataType.equals("character")) {
			return true;
		}
		return false;
	}

	/*
	 * @param fastcodedatabasefielddecorator list
	 * @param field name
	 * returns the fastcodedecorator object for the corresponding field
	 *
	 */

	public static FastCodeDataBaseFieldDecorator getFastCodeDataBaseFieldDecorator(final String fieldName,
			final List<FastCodeDataBaseFieldDecorator> fieldInfoForFirstTable,
			final List<FastCodeDataBaseFieldDecorator> fieldInfoForSecondTable,
			final List<FastCodeDataBaseFieldDecorator> fieldInfoForThirdTable) {

		FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator = null;
		fastCodeDataBaseFieldDecorator = getFastCodeDataBaseFieldDecorator(fieldName, fieldInfoForFirstTable);

		if (fastCodeDataBaseFieldDecorator != null) {
			return fastCodeDataBaseFieldDecorator;
		} else {

			fastCodeDataBaseFieldDecorator = getFastCodeDataBaseFieldDecorator(fieldName, fieldInfoForSecondTable);
			if (fastCodeDataBaseFieldDecorator != null) {
				return fastCodeDataBaseFieldDecorator;
			} else {
				fastCodeDataBaseFieldDecorator = getFastCodeDataBaseFieldDecorator(fieldName, fieldInfoForThirdTable);
				if (fastCodeDataBaseFieldDecorator != null) {
					return fastCodeDataBaseFieldDecorator;
				}

			}

		}

		return null;
	}

	/*
	 * @param fastcodedatabasefielddecorator list
	 * @param field name
	 * returns the fastcodedecorator object for the corresponding field
	 *
	 */

	public static FastCodeDataBaseFieldDecorator getFastCodeDataBaseFieldDecorator(String fieldName,
			final List<FastCodeDataBaseFieldDecorator> fieldInfo) {
		if (fieldName.contains(" *")) {
			fieldName = fieldName.substring(0, fieldName.length() - 2);
		}
		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : fieldInfo) {
			if (fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().getName().equals(fieldName)) {
				return fastCodeDataBaseFieldDecorator;
			}
			;
		}

		return null;
	}

	/*
	 * @param field name
	 * @param table name
	 * find the primarykey fields in a database table
	 *
	 */

	public static Boolean isPrimaryKey(final String fieldName, final String tableNameWithSchema) {

		final DataBaseTypeInfo dataBaseTypeInfo = DataBaseTypeInfo.getInstance();
		final ArrayList<String> primarykeyFields = dataBaseTypeInfo.getPrimaryKeyColumns(tableNameWithSchema);
		for (final String s : primarykeyFields) {
			if (s.equals(fieldName)) {
				return true;
			}
		}
		return false;
	}

	public static ArrayList<String> getFunctionsList(final String type) {

		final SQLFunctionsMapping sqlFuncMapping = SQLFunctionsMapping.getInstance();
		final ArrayList<String> funcList = new ArrayList<String>();
		final DataBaseTypeInfo dbInfo = DataBaseTypeInfo.getInstance();
		final String dbase = dbInfo.getDatabaseType();
		for (final String s : sqlFuncMapping.getDatabases()) {
			if (dbase.equals(s)) {
				final ArrayList<String> dataTypeList = sqlFuncMapping.getDbDataTypeMap().get(s);
				for (final String dt : dataTypeList) {
					if (dt.equalsIgnoreCase(type)) {
						for (final SQLFunctions dd : sqlFuncMapping.getDataTypeFunctionsMap().get(dt)) {
							funcList.add(dd.getName());
						}
						return funcList;
					}
				}
			}
		}

		return null;

	}

	public static void updateSQLFunctionsMapping() {
		final List<SQLFunctions> functionsList = new ArrayList<SQLFunctions>();
		final List<String> databases = new ArrayList<String>();
		final Map<String, ArrayList<SQLFunctions>> funcInfo = new HashMap<String, ArrayList<SQLFunctions>>();
		final Map<String, ArrayList<String>> databaseInfo = new HashMap<String, ArrayList<String>>();

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		InputStream inputStream = null;

		try {
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/sql-functions.xml"), false);
			final Document document = docBuilder.parse(inputStream);
			final NodeList databaseList = document.getElementsByTagName("database");

			final int size = databaseList.getLength();
			for (int i = 0; i < size; i++) {
				final Node databaseNode = databaseList.item(i);
				final NamedNodeMap dbattributes = databaseNode.getAttributes();
				final Node dbAttrNode = dbattributes.getNamedItem("name");
				final String name = dbAttrNode.getNodeValue();
				databases.add(name);
				final List<String> dataTypes = new ArrayList<String>();

				Node dataTypeNode = databaseNode.getFirstChild();
				while (dataTypeNode != null) {

					if (dataTypeNode.getNodeType() == Node.ELEMENT_NODE) {
						final NamedNodeMap dataTypeAttr = dataTypeNode.getAttributes();
						final Node typeAttr = dataTypeAttr.getNamedItem("type");
						final String dtype = typeAttr.getNodeValue();
						final ArrayList<String> datatypes = new ArrayList<String>();
						for (final String s : dtype.split("\\s+")) {
							datatypes.add(s);
						}
						dataTypes.addAll(datatypes);
						final List<SQLFunctions> functions = new ArrayList<SQLFunctions>();

						Node sqlfunc = dataTypeNode.getFirstChild();
						Node count = null;
						Node returnValue = null;
						Node insertAt = null;

						while (sqlfunc != null) {
							if (sqlfunc.getNodeType() == Node.ELEMENT_NODE) {
								final NamedNodeMap attributes = sqlfunc.getAttributes();
								count = attributes.getNamedItem("count");
								returnValue = attributes.getNamedItem("return-type");
								insertAt = attributes.getNamedItem("used-as");
								functions.add(new SQLFunctions(sqlfunc.getTextContent(), Integer.parseInt(count.getNodeValue()),
										returnValue.getNodeValue(), insertAt.getNodeValue()));
								functionsList.add(new SQLFunctions(sqlfunc.getTextContent(), Integer.parseInt(count.getNodeValue()),
										returnValue.getNodeValue(), insertAt.getNodeValue()));
							}
							sqlfunc = sqlfunc.getNextSibling();
						}
						for (final String s : datatypes) {
							funcInfo.put(s, (ArrayList<SQLFunctions>) functions);
						}

					}
					dataTypeNode = dataTypeNode.getNextSibling();

				}

				databaseInfo.put(name, (ArrayList<String>) dataTypes);

			}

		} catch (final Exception ex) {
			ex.printStackTrace();

		} finally {

			final SQLFunctionsMapping sqlFuncMapping = SQLFunctionsMapping.getInstance();
			sqlFuncMapping.setFunctionsList((ArrayList<SQLFunctions>) functionsList);
			sqlFuncMapping.setDataTypeFunctionsMap(funcInfo);
			sqlFuncMapping.setDbDataTypeMap(databaseInfo);
			sqlFuncMapping.setDatabases((ArrayList<String>) databases);
			FastCodeUtil.closeInputStream(inputStream);
		}

	}
}

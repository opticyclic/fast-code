package org.fastcode.popup.actions.snippet;

import static org.fastcode.common.FastCodeConstants.DATETIME;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.NUMBER;
import static org.fastcode.common.FastCodeConstants.SINGLE_QUOTATION_MARK;
import static org.fastcode.util.DatabaseUtil.getSchemaFromDb;
import static org.fastcode.util.DatabaseUtil.parseSQLDatatypes;
import static org.fastcode.util.FastCodeUtil.closeInputStream;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.replaceSpecialChars;
import static org.w3c.dom.Node.ELEMENT_NODE;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.AbstractActionSupport;
import org.fastcode.Activator;
import org.fastcode.common.CreateTableData;
import org.fastcode.common.DatabaseDetails;
import org.fastcode.dialog.CreateTableDialog;
import org.fastcode.preferences.DatabaseConnectionSettings;
import org.fastcode.util.ConnectToDatabase;
import org.fastcode.util.DatabaseCache;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CreateNewDatabaseTableSnippetAction extends AbstractActionSupport implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	protected CreateTableData	createTableData;
	ConnectToDatabase			connectToDatabase	= ConnectToDatabase.getInstance();
	Connection					con;
	final DatabaseCache			databaseCache		= DatabaseCache.getInstance();

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement) throws Exception {

		try {
			this.con = this.connectToDatabase.getConnection();
			if (this.con == null) {
				return;
			}

			this.createTableData = getCreateTableData();
			if (this.createTableData == null) {
				return;
			}
			if (this.con.isClosed()) {
				this.con = ConnectToDatabase.getCon();
			}
			processTableCreateOrUpdate();
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception("There was some problem - " + ex.getMessage());
		} finally {
			if (this.con != null) {
				this.connectToDatabase.closeConnection(this.con);
			}
		}
	}

	/**
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public void processTableCreateOrUpdate() throws Exception {
		PreparedStatement preparedStatement = null;
		final Map<String, Object> tablePlaceHolders = new HashMap<String, Object>();
		String query = null;
		tablePlaceHolders.put("schema", this.createTableData.getSchemaSelected());
		tablePlaceHolders.put("table", this.createTableData.getTableName());
		tablePlaceHolders.put("columnType", this.createTableData.getColumnTypeSelected().toUpperCase());
		tablePlaceHolders.put("columnNotNull", this.createTableData.getNotNull());
		tablePlaceHolders.put("columnSize",
				this.createTableData.getColumnTypeSize() == 0 ? EMPTY_STR : this.createTableData.getColumnTypeSize());
		tablePlaceHolders.put("columnSizeType", isEmpty(this.createTableData.getLenType()) ? EMPTY_STR : this.createTableData.getLenType());

		tablePlaceHolders.put("columnPrecisionAndScale", this.createTableData.getColumnTypePrecisionAndScale() == null ? EMPTY_STR
				: this.createTableData.getColumnTypePrecisionAndScale());
		tablePlaceHolders.put("columnNames", this.createTableData.getColumnNames());
		String defaultValue = EMPTY_STR;
		if (!isEmpty(this.createTableData.getDefaultValue())) {
			if (this.createTableData.getDataType().equalsIgnoreCase(NUMBER)
					|| this.createTableData.getDataType().equalsIgnoreCase(DATETIME)
					&& Character.isLetter(this.createTableData.getDefaultValue().trim().charAt(0))) {
				defaultValue = this.createTableData.getDefaultValue();
			} else {
				defaultValue = SINGLE_QUOTATION_MARK + this.createTableData.getDefaultValue().trim() + SINGLE_QUOTATION_MARK;
			}
		}
		tablePlaceHolders.put("defaultValue", defaultValue);
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		String dbType = databaseConnectionSettings.getTypesofDabases();
		if (this.createTableData.isGetNewConnection()) {
			this.connectToDatabase.closeConnection(this.con);
			this.con = this.connectToDatabase.getNewConnection(this.createTableData.getSelectedDatabaseName());
			final DatabaseDetails databaseDetails = databaseConnectionSettings.getConnMap().get(
					this.createTableData.getSelectedDatabaseName());
			dbType = databaseDetails.getDatabaseType();
		}
		if (this.createTableData.isCreateTableWithColumns()) {
			query = this.createTableData.getDbTypeTableWithColumnsQueryMap().get(dbType);
		} else if (this.createTableData.isAddColumnsToExistingTable()) {
			query = this.createTableData.getDbTypeAddColumnsToExistingTableMap().get(dbType);
		}
		try {
			final String snppt = evaluateByVelocity(query, tablePlaceHolders);
			if (isEmpty(snppt)) {
				throw new Exception("Blank snippet, please provide correct values in Table Dialog.");

			}
			final String snippet = replaceSpecialChars(snppt);
			preparedStatement = this.con.prepareStatement(snippet);
			preparedStatement.executeUpdate();
			String message = EMPTY_STR;
			if (this.createTableData.isAddColumnsToExistingTable()) {
				message = "Table -  " + this.createTableData.getTableName() + " - updated successfully.";
			} else {
				message = "Table - " + this.createTableData.getTableName() + " - created successfully.";
			}
			MessageDialog.openInformation(new Shell(), "Success", message);
		} catch (final Exception ex) {
			throw new Exception("There was some problem - " + ex.getMessage());
		} finally {
			/*if (this.con != null) {
				this.connectToDatabase.closeConnection(this.con);
			}*/
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
			} catch (final SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * @return
	 * @throws Exception
	 */
	protected CreateTableData getCreateTableData() throws Exception {
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();

		if (this.createTableData == null || !this.createTableData.isAddColumnsToExistingTable()) {
			this.createTableData = new CreateTableData();
			this.createTableData.setCreateTableWithColumns(true);
			this.createTableData.setAddColumnsToExistingTable(false);
		}
		getSchemaFromDb(this.con, databaseConnectionSettings.getTypesofDabases());
		this.createTableData.setSchemasInDB(this.databaseCache.getDbSchemaListMap().get(databaseConnectionSettings.getTypesofDabases()));

		//final DataBaseTypeInfo dataBaseTypeInfo = DataBaseTypeInfo.getInstance();
		//final SQLDatatypesMapping sqldatatypesMapping = SQLDatatypesMapping.getInstance();
		parseSQLDatatypes();

		/*Map<String, ArrayList<SQLDatatypes>> groupBaseTypeMap = sqldatatypesMapping.getDatabaseDataTypeMap().get(
				dataBaseTypeInfo.getDatabaseType());

		for (final SQLDatatypes columnType : groupBaseTypeMap.get("String")) {
			this.createTableData.getStringColumnTypesList().add(columnType.getType().toString());
		}
		for (final SQLDatatypes columnType : groupBaseTypeMap.get("Numeric")) {
			this.createTableData.getNumericColumnTypesList().add(columnType.getType().toString());
		}
		for (final SQLDatatypes columnType : groupBaseTypeMap.get("Datetime")) {
			this.createTableData.getDateTimeColumnTypesList().add(columnType.getType().toString());
		}
		for (final SQLDatatypes columnType : groupBaseTypeMap.get("Others")) {
			this.createTableData.getOthersColumnTypesList().add(columnType.getType().toString());
		}*/

		/*final Properties properties = retrievePropertiesFromFile("resources/datatypeconversion.properties");

		for (final Entry<Object, Object> entry : properties.entrySet()) {
			final String key = (String) entry.getKey();
			final String value = (String) entry.getValue();
			if (key.equals("java.lang.String")) {
				for (final String columnType : value.split(COMMA)) {
					this.createTableData.getStringColumnTypesList().add(columnType);
				}
			} else if (key.equals("java.lang.Integer") || key.equals("java.math.BigDecimal") || key.equals("java.lang.Double")
					|| key.equals("java.lang.Long") || key.equals("java.lang.Short") || key.equals("java.lang.Byte")
					|| key.equals("java.lang.Boolean") || key.equals("java.lang.Float")) {
				for (final String columnType : value.split(COMMA)) {
					this.createTableData.getNumericColumnTypesList().add(columnType);
				}

			} else if (key.equals("java.sql.Timestamp") || key.equals("java.sql.Date") || key.equals("java.sql.Time")) {
				for (final String columnType : value.split(COMMA)) {
					this.createTableData.getDateTimeColumnTypesList().add(columnType);
				}
			} else if (key.equals("java.sql.Blob") || key.equals("java.sql.Clob")) {
				for (final String columnType : value.split(COMMA)) {
					this.createTableData.getOthersColumnTypesList().add(columnType);
				}
			}
		}
		 */
		final InputStream databaseTableInputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path(
				"resources/database-table.xml"), false);
		parseDatabaseTableFile(databaseTableInputStream);
		final CreateTableDialogCallback createTableDialogCallback = new CreateTableDialogCallback() {

			@Override
			public void submitPressed(final CreateTableData createTableData) throws Exception {
				// handle data
				CreateNewDatabaseTableSnippetAction.this.createTableData = createTableData;
				try {
					processTableCreateOrUpdate();
					CreateNewDatabaseTableSnippetAction.this.createTableData.setGetNewConnection(false);
				} catch (final Exception ex) {
					throw ex;
				}
			}
		};
		final CreateTableDialog createTableDialog = new CreateTableDialog(new Shell(), this.createTableData, createTableDialogCallback);
		if (createTableDialog.open() == Window.CANCEL) {
			return null;
		}
		return this.createTableData;
	}

	/**
	 * @param databaseTableInputStream
	 */
	private void parseDatabaseTableFile(final InputStream databaseTableInputStream) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			final Document document = docBuilder.parse(databaseTableInputStream);
			final NodeList databaseList = document.getElementsByTagName("database");

			final int size = databaseList.getLength();
			for (int i = 0; i < size; i++) {
				final Node databaseNode = databaseList.item(i);
				final NamedNodeMap dbattributes = databaseNode.getAttributes();
				final Node dbAttrNode = dbattributes.getNamedItem("type");
				final String databaseType = dbAttrNode.getNodeValue();
				Node node = databaseNode.getFirstChild();
				while (node != null) {
					if (node.getNodeType() != ELEMENT_NODE) {
						node = node.getNextSibling();
						continue;
					}
					final String nodeName = node.getNodeName();
					final String nodeContent = node.getTextContent().trim();
					if (nodeName.equals("table-with-columns")) {
						this.createTableData.getDbTypeTableWithColumnsQueryMap().put(databaseType, nodeContent);
					} else if (nodeName.equals("add-columns")) {
						this.createTableData.getDbTypeAddColumnsToExistingTableMap().put(databaseType, nodeContent);
					}
					node = node.getNextSibling();
				}
			}

		} catch (final ParserConfigurationException ex) {
			ex.printStackTrace();
		} catch (final SAXException ex) {
			ex.printStackTrace();
		} catch (final IOException ex) {
			ex.printStackTrace();
		} finally {
			closeInputStream(databaseTableInputStream);
		}
	}

	/*	private void parseSqlDataTypes() {
			final List<SQLDatatypes> datatypesList = new ArrayList<SQLDatatypes>();
			final List<String> databases = new ArrayList<String>();
			final Map<String, ArrayList<SQLDatatypes>> funcInfo = new HashMap<String, ArrayList<SQLDatatypes>>();
			final Map<String, ArrayList<String>> databaseInfo = new HashMap<String, ArrayList<String>>();

			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			InputStream inputStream = null;

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
							final List<SQLDatatypes> functions = new ArrayList<SQLDatatypes>();

							Node sqlfunc = dataTypeNode.getFirstChild();
							Node datatype = null;
							Node defaultvalue = null;
							Node length = null;
							Node precision = null;

							while (sqlfunc != null) {
								if (sqlfunc.getNodeType() == Node.ELEMENT_NODE) {
									final NamedNodeMap attributes = sqlfunc.getAttributes();
									datatype = attributes.getNamedItem("data-type");
									defaultvalue = attributes.getNamedItem("default-value");
									length = attributes.getNamedItem("length");
									precision = attributes.getNamedItem("precision");
									functions.add(new SQLDatatypes(sqlfunc.getTextContent(), datatype.getNodeValue(), defaultvalue
											.getNodeValue(), length.getNodeValue(), precision.getNodeValue()));
									datatypesList.add(new SQLDatatypes(sqlfunc.getTextContent(), datatype.getNodeValue(), defaultvalue
											.getNodeValue(), length.getNodeValue(), precision.getNodeValue()));
								}
								sqlfunc = sqlfunc.getNextSibling();
							}
							for (final String s : datatypes) {
								funcInfo.put(s, (ArrayList<SQLDatatypes>) functions);
							}

						}
						dataTypeNode = dataTypeNode.getNextSibling();

					}

					databaseInfo.put(name, (ArrayList<String>) dataTypes);

				}

			} catch (final Exception ex) {
				ex.printStackTrace();

			} finally {

				SQLDatatypesMapping sqlFuncMapping = SQLDatatypesMapping.getInstance();
				sqlFuncMapping.setDatatypesList((ArrayList<SQLDatatypes>) datatypesList);
				//sqlFuncMapping.setdataTypeFunctionsMap(funcInfo);
				sqlFuncMapping.setDbDataTypeMap(databaseInfo);
				sqlFuncMapping.setDatabases((ArrayList<String>) databases);
				FastCodeUtil.closeInputStream(inputStream);
			}

		}*/

	@Override
	public void dispose() {
	}

	@Override
	protected void runAction(final ICompilationUnit compUnit, final IJavaElement javaElement, final IType[] types) throws Exception {
	}

	@Override
	protected IType[] getTypesFromUser(final String title, final String description) throws Exception {
		return null;
	}

	@Override
	protected boolean doesModify() {
		return false;
	}

	@Override
	protected boolean doesRequireMoreInfoFromUser() {
		return false;
	}

	@Override
	protected boolean requireJavaClass() {
		return false;
	}

	public interface CreateTableDialogCallback {
		void submitPressed(CreateTableData createTableData) throws Exception;
	}

}

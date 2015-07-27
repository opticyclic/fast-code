package org.fastcode.popup.actions.snippet;

import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DATABASE_NAME;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.SCHEMA;
import static org.fastcode.common.FastCodeConstants.TABLE;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_SIMPLE;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_SQL_QUERY;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_WITH_JOIN;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;
import static org.fastcode.util.DatabaseUtil.getSchemaFromDb;
import static org.fastcode.util.DatabaseUtil.getTableColumnsFromDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.common.CreateSnippetData;
import org.fastcode.common.FastCodeConstants.NUMBER_OF_JOIN_TABLES;
import org.fastcode.common.FastCodeDataBaseFieldDecorator;
import org.fastcode.dialog.CreateSnippetDialog;
import org.fastcode.dialog.SelectFieldsSelectionDialog;
import org.fastcode.dialog.SelectFieldsSelectionDialogForJoin;
import org.fastcode.dialog.WhereFieldTableDialog;
import org.fastcode.dialog.WhereFieldTableDialogForJoin;
import org.fastcode.preferences.DatabaseConnectionSettings;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.setting.TemplateSettings;
import org.fastcode.util.ConnectToDatabase;
import org.fastcode.util.DatabaseCache;
import org.fastcode.util.JdbcSettings;

public class AbstractCreateNewDatabaseSnippetAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate,
		IActionDelegate, IWorkbenchWindowActionDelegate {

	ConnectToDatabase										connectToDatabase	= ConnectToDatabase.getInstance();
	Connection												con;

	//final Map<String, List<FastCodeDataBaseField>>	dbFieldSelection	= new HashMap<String, List<FastCodeDataBaseField>>();
	final Map<String, List<FastCodeDataBaseFieldDecorator>>	dbFieldSelection	= new HashMap<String, List<FastCodeDataBaseFieldDecorator>>();

	final TemplateCache										templateCache		= TemplateCache.getInstance();
	final DatabaseCache										databaseCache		= DatabaseCache.getInstance();

	private Shell											shell				= null;
	Object													variation;
	String													templateVariationField;
	TableViewer												tableViewer;

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#runAction()
	 */
	@Override
	public void runAction() throws Exception {
		try {
			this.con = this.connectToDatabase.getConnection();
			if (this.con == null) {
				return;
			}

			this.shell = this.editorPart == null || this.editorPart.getSite() == null ? new Shell() : this.editorPart.getSite().getShell();

			super.runAction();

		} catch (final SQLException e) {
			e.printStackTrace();
			throw new Exception(e.getMessage(), e);
		} finally {
			if (this.dbFieldSelection != null) {
				this.dbFieldSelection.clear();
			}

			if (this.con != null) {
				this.connectToDatabase.closeConnection(this.con);
			}

		}

	}

	/**
	 * @param tableName
	 * @param fieldType
	 * @param schema
	 * @return
	 */
	public Map<String, List<FastCodeDataBaseFieldDecorator>> columnSelection(final String tableName, final String fieldType,
			final boolean isAliasNameRequired, final String schema) {
		List<FastCodeDataBaseFieldDecorator> selectedFields = new ArrayList<FastCodeDataBaseFieldDecorator>();
		boolean sqlFunctionsRequired;
		//Object[] userResult = null;

		if (fieldType.equals("fields")) {//||fieldType.equals("pojo_fields")) {
			sqlFunctionsRequired = this.templateType.equals(DATABASE_TEMPLATE_SELECT_SIMPLE)
					|| this.templateType.equals(DATABASE_TEMPLATE_SELECT_SQL_QUERY) ? true : false;
			final SelectFieldsSelectionDialog fieldsSelectionDialog = new SelectFieldsSelectionDialog(new Shell(), tableName,
					sqlFunctionsRequired, this.templateType, isAliasNameRequired, schema);
			if (fieldsSelectionDialog.open() == Window.CANCEL) {
				return Collections.EMPTY_MAP;
			}
			selectedFields = fieldsSelectionDialog.getSelectedFields();
			if (selectedFields == null || selectedFields.isEmpty()) {
				return Collections.EMPTY_MAP;
			}

			/*if (this.templateType.contains(P_DATABASE_TEMPLATE_PREFIX + UNDERSCORE + "INSERT")
					|| this.templateType.contains(P_DATABASE_TEMPLATE_PREFIX + UNDERSCORE + "UPDATE")) {
				final String[] notNullColNames = this.databaseCache.getNotNullColumnListMap().get(tableName).toArray(new String[0]);
				if (notNullColNames != null) {
					int count = 0;
					for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : selectedFields) {

						final String columnName = fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().getName(); //((String) res).substring(0, ((String) res).indexOf('-'));
						for (final String notNullColName : notNullColNames) {
							if (notNullColName.equals(columnName)) {
								count++;
							}
						}
					}
					if (count < notNullColNames.length) {
						MessageDialog.openWarning(this.shell, "Warning", "All Not Nullable fields should be selected");
						return columnSelection(tableName, fieldType, columnList);
					}
				}
			}*/

		} else {

			final WhereFieldTableDialog whereFieldSelectionDialog = new WhereFieldTableDialog(new Shell(), tableName, schema);
			if (whereFieldSelectionDialog.open() == Window.CANCEL) {
				return Collections.EMPTY_MAP;
			}
			selectedFields = whereFieldSelectionDialog.getSelectedFields();
			if (selectedFields == null || selectedFields.isEmpty()) {
				return Collections.EMPTY_MAP;
			}

		}

		final Map<String, List<FastCodeDataBaseFieldDecorator>> fieldsMap = new HashMap<String, List<FastCodeDataBaseFieldDecorator>>();
		fieldsMap.put(fieldType, selectedFields);

		return fieldsMap;
	}

	/**
	 * @param fieldType
	 * @param placeHolders
	 * @return
	 */
	private Map<String, List<FastCodeDataBaseFieldDecorator>> columnSelectionForJoin(final String fieldType,
			final Map<String, Object> placeHolders) {
		List<FastCodeDataBaseFieldDecorator> selectedFields = new ArrayList<FastCodeDataBaseFieldDecorator>();
		final String firstTable = placeHolders.get("First_Table").toString();
		final String secondTable = placeHolders.get("Second_Table").toString();
		final String schema1 = placeHolders.get("schema1").toString();
		final String schema2 = placeHolders.get("schema2").toString();
		String thirdTable = null;
		String schema3 = null;
		boolean isSQLFunctionsRequired;

		try {
			SelectFieldsSelectionDialogForJoin fieldSelectionDialog = null;
			if (fieldType.equals("fields")) {

				isSQLFunctionsRequired = this.templateType.equals(DATABASE_TEMPLATE_SELECT_WITH_JOIN) ? true : false;
				if (placeHolders.get("Number_Of_Join_Tables").equals(NUMBER_OF_JOIN_TABLES.TWO)) {
					fieldSelectionDialog = new SelectFieldsSelectionDialogForJoin(new Shell(), firstTable, secondTable,
							isSQLFunctionsRequired, (Boolean) placeHolders.get("use-alias-name"), schema1, schema2);
				} else if (placeHolders.get("Number_Of_Join_Tables").equals(NUMBER_OF_JOIN_TABLES.THREE)) {
					thirdTable = placeHolders.get("Third_Table").toString();
					schema3 = placeHolders.get("schema3").toString();
					fieldSelectionDialog = new SelectFieldsSelectionDialogForJoin(new Shell(), firstTable, secondTable, thirdTable,
							isSQLFunctionsRequired, (Boolean) placeHolders.get("use-alias-name"), schema1, schema2, schema3);
				}
				if (fieldSelectionDialog != null && fieldSelectionDialog.open() == Window.CANCEL) {
					return Collections.EMPTY_MAP;
				}
				selectedFields = fieldSelectionDialog.getSelectedFields();
				if (selectedFields == null || selectedFields.isEmpty()) {
					return Collections.EMPTY_MAP;
				}

			} else {
				WhereFieldTableDialogForJoin whereFieldSelectionDialog = null;

				if (placeHolders.get("Number_Of_Join_Tables").equals(NUMBER_OF_JOIN_TABLES.TWO)) {
					whereFieldSelectionDialog = new WhereFieldTableDialogForJoin(new Shell(), firstTable, secondTable, schema1, schema2);
				} else if (placeHolders.get("Number_Of_Join_Tables").equals(NUMBER_OF_JOIN_TABLES.THREE)) {
					thirdTable = placeHolders.get("Third_Table").toString();
					whereFieldSelectionDialog = new WhereFieldTableDialogForJoin(new Shell(), firstTable, secondTable, thirdTable, schema1,
							schema2, schema3);
				}
				if (whereFieldSelectionDialog != null && whereFieldSelectionDialog.open() == Window.CANCEL) {
					return Collections.EMPTY_MAP;
				}
				selectedFields = whereFieldSelectionDialog.getSelectedFields();
				if (selectedFields == null || selectedFields.isEmpty()) {
					return Collections.EMPTY_MAP;
				}

			}

		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		final Map<String, List<FastCodeDataBaseFieldDecorator>> fieldsMap = new HashMap<String, List<FastCodeDataBaseFieldDecorator>>();
		fieldsMap.put(fieldType, selectedFields);

		return fieldsMap;

	}

	/**
	 *
	 * @param fieldType
	 * @param columnList
	 * @return
	 */
	/*
	 * commented on 05/09/2012
	 *
	 * public Map<String, List<FastCodeDataBaseField>> columnSelection(final String tableName, final String fieldType, final String[] columnList) {
		final List<FastCodeDataBaseField> selectFields = new ArrayList<FastCodeDataBaseField>();

		final DatabaseFieldSelectionDialog databaseFieldSelectionDialog = new DatabaseFieldSelectionDialog(this.shell, "Columns in Table", "Choose "
				+ makeWord(fieldType), columnList, true);
		if (databaseFieldSelectionDialog.open() == Window.CANCEL) {
			return Collections.EMPTY_MAP;
		}

		final Object[] userResult = databaseFieldSelectionDialog.getResult();

		if (userResult == null || userResult.length == 0) {
			return Collections.EMPTY_MAP;
		}
		if (this.templateType.equals(DATABASE_TEMPLATE_INSERT_SIMPLE) || this.templateType.equals(DATABASE_TEMPLATE_INSERT_WITH_NAMED_PARAMETER)
				|| this.templateType.equals(DATABASE_TEMPLATE_INSERT_NAMED_QUERY)
				|| this.templateType.equals(DATABASE_TEMPLATE_INSERT_WITH_NAMED_QUERY_ANNOTATION)) {
			final String[] notNullColNames = this.databaseCache.getNotNullColumnListMap().get(tableName).toArray(new String[0]);
			if (notNullColNames != null) {
				int count = 0;
				for (final Object res : userResult) {
					final String columnName = ((String) res).substring(0, ((String) res).indexOf('-'));
					for (final String notNullColName : notNullColNames) {
						if (notNullColName.equals(columnName)) {
							count++;
						}
					}
				}
				if (count < notNullColNames.length) {
					MessageDialog.openWarning(this.shell, "Warning", "All Not Nullable fields should be selected");
					return columnSelection(tableName, fieldType, columnList);
				}
			}
		}
		// this.selectFields.clear();
		final List<FastCodeDataBaseField> fields = this.databaseCache.getTableNameFieldDetailsMap().get(tableName);

		for (final Object res : userResult) {
			final String columnName = ((String) res).substring(0, ((String) res).indexOf('-'));
			for (final FastCodeDataBaseField fastCodeDataBaseField : getEmptyListForNull(fields)) {
				//				final FastCodeDataBaseField fastCodeDataBaseField = iterator.next();
				if (fastCodeDataBaseField.getName().equals(columnName)) {
					selectFields.add(fastCodeDataBaseField);
				}
			}
		}

		final Map<String, List<FastCodeDataBaseField>> fieldsMap = new HashMap<String, List<FastCodeDataBaseField>>();
		fieldsMap.put(fieldType, selectFields);

		return fieldsMap;
	}

	private Map<String, List<FastCodeDataBaseField>> columnSelectionForJoin(final String fieldType, final String[] columnsForJoin,
			final Map<String, Object> placeHolders) {
		final List<FastCodeDataBaseField> selectFields = new ArrayList<FastCodeDataBaseField>();

		final DatabaseFieldSelectionDialog databaseFieldSelectionDialog = new DatabaseFieldSelectionDialog(this.shell, "Columns in Table", "Choose "
				+ makeWord(fieldType), columnsForJoin, true);
		if (databaseFieldSelectionDialog.open() == Window.CANCEL) {
			return Collections.EMPTY_MAP;
		}
		final Object[] userResult = databaseFieldSelectionDialog.getResult();

		if (userResult == null || userResult.length == 0) {
			return Collections.EMPTY_MAP;
		}
		String firstTable = placeHolders.get("First_Table").toString();
		String secondTable = placeHolders.get("Second_Table").toString();
		String thirdTable = null;
		if (placeHolders.get("Number_Of_Join_Tables").equals(NUMBER_OF_JOIN_TABLES.THREE)) {
			thirdTable = placeHolders.get("Third_Table").toString();
		}
		final List<FastCodeDataBaseField> firstTablefields = this.databaseCache.getTableNameFieldDetailsMap().get(firstTable);
		final List<FastCodeDataBaseField> secondTablefields = this.databaseCache.getTableNameFieldDetailsMap().get(secondTable);
		List<FastCodeDataBaseField> thirdTablefields = null;
		if (thirdTable != null) {
			thirdTablefields = this.databaseCache.getTableNameFieldDetailsMap().get(thirdTable);
		}

		for (final Object res : userResult) {
			final String tableName = ((String) res).substring(0, ((String) res).indexOf('-'));
			if (tableName.equals(firstTable)) {
				final String columnName = ((String) res).substring(((String) res).indexOf('-') + 1, ((String) res).lastIndexOf('-')).trim();//((String) res).substring(((String) res).indexOf("   "), ((String) res).lastIndexOf('-')).trim();
				for (final FastCodeDataBaseField fastCodeDataBaseField : getEmptyListForNull(firstTablefields)) {
					if (fastCodeDataBaseField.getName().equals(columnName)) {
						selectFields.add(fastCodeDataBaseField);
					}
				}
			}
			if (tableName.equals(secondTable)) {
				final String columnName = ((String) res).substring(((String) res).indexOf('-') + 1, ((String) res).lastIndexOf('-')).trim();//((String) res).substring(((String) res).indexOf('-'), ((String) res).indexOf('-'));
				for (final FastCodeDataBaseField fastCodeDataBaseField : getEmptyListForNull(secondTablefields)) {
					if (fastCodeDataBaseField.getName().equals(columnName)) {
						selectFields.add(fastCodeDataBaseField);
					}
				}
			}
			if (thirdTable != null) {
				if (tableName.equals(thirdTable)) {
					final String columnName = ((String) res).substring(((String) res).indexOf('-') + 1, ((String) res).lastIndexOf('-')).trim();
					for (final FastCodeDataBaseField fastCodeDataBaseField : getEmptyListForNull(thirdTablefields)) {
						if (fastCodeDataBaseField.getName().equals(columnName)) {
							selectFields.add(fastCodeDataBaseField);
						}
					}
				}
			}
		}
		final Map<String, List<FastCodeDataBaseField>> fieldsMap = new HashMap<String, List<FastCodeDataBaseField>>();
		fieldsMap.put(fieldType, selectFields);

		return fieldsMap;

	}*/

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#getFields(java.util.Map)
	 */
	@Override
	protected Map<String, List<FastCodeDataBaseFieldDecorator>> getFields(final Map<String, Object> placeHolders) throws Exception {

		final String tableName = (String) placeHolders.get(TABLE);
		final String schema = (String) placeHolders.get(SCHEMA);
		DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		String databaseType = databaseConnectionSettings.getConnMap().get(placeHolders.get(DATABASE_NAME)).getDatabaseType();
		if (this.con.isClosed()) {
			this.con = ConnectToDatabase.getCon();
		}
		final String[] columns = getTableColumnsFromDB(tableName, this.con, schema, databaseType);
		String fldNamesProp = null;
		final String templateVariationField = getTemplateVariationField();
		final Object templateVar = placeHolders.get(templateVariationField);
		if (templateVar != null) {
			if (templateVar instanceof String) {
				final String templateVariation = (String) templateVar;
				fldNamesProp = this.templateType + DOT + templateVariation + UNDERSCORE + "FIELD_NAMES";
			}
		} else {
			fldNamesProp = this.templateType + UNDERSCORE + "FIELD_NAMES";
		}
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String fieldNames = globalSettings.getPropertyValue(fldNamesProp, "fields");
		for (final String fieldName : fieldNames.split(COMMA)) {
			final Map<String, List<FastCodeDataBaseFieldDecorator>> fieldSelectionMap = columnSelection(tableName, fieldName.trim(),
					(Boolean) placeHolders.get("use-alias-name"), schema);
			if (fieldSelectionMap.isEmpty()) {

				if (fieldName.equals("fields")) {
					return Collections.EMPTY_MAP;
				} else {
					if (this.templateType.contains(P_DATABASE_TEMPLATE_PREFIX + UNDERSCORE + "UPDATE")) {
						return Collections.EMPTY_MAP;
					}
				}
			}

			if (!fieldSelectionMap.isEmpty()) {
				getMethodNameFromFields(fieldSelectionMap, placeHolders, fieldName);
			}
			this.dbFieldSelection.putAll(fieldSelectionMap);
		}

		return this.dbFieldSelection;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#getFieldsForJoin(java.util.Map)
	 */
	@Override
	protected Map<String, List<FastCodeDataBaseFieldDecorator>> getFieldsForJoin(final Map<String, Object> placeHolders) throws Exception {
		/*
		 * commented on 08/09/2012
		 * final List<String> columns = new ArrayList<String>();
		final String first_Table = placeHolders.get("First_Table").toString();
		final String second_Table = placeHolders.get("Second_Table").toString();
		final Object[] firstTableColumns = (Object[]) placeHolders.get("First_Table_Columns");
		final Object[] secondTableColumns = (Object[]) placeHolders.get("Second_Table_Columns");
		for (final Object column : firstTableColumns) {
			final String col = first_Table + "-   " + (String) column;
			columns.add(col);

		}
		for (final Object column : secondTableColumns) {
			final String col = second_Table + "-   " + (String) column;
			columns.add(col);

		}
		if (placeHolders.get("Number_Of_Join_Tables").equals(NUMBER_OF_JOIN_TABLES.THREE)) {
			final String third_Table = placeHolders.get("Third_Table").toString();
			final Object[] thirdTableColumns = (Object[]) placeHolders.get("Third_Table_Columns");
			for (Object column : thirdTableColumns) {
				String col = third_Table + "-   " + (String) column;
				columns.add(col);
			}

		}
		final String[] columnsForJoin = columns.toArray(new String[0]);*/
		final String fldNamesProp = this.templateType + UNDERSCORE + "FIELD_NAMES";
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String fieldNames = globalSettings.getPropertyValue(fldNamesProp, "fields");
		for (final String fieldName : fieldNames.split(COMMA)) {
			/*final Map<String, List<FastCodeDataBaseField>> fieldSelectionMap = columnSelectionForJoin(first_Table, second_Table, fieldName.trim(),
					columnsForJoin);*/
			final Map<String, List<FastCodeDataBaseFieldDecorator>> fieldSelectionMap = columnSelectionForJoin(fieldName.trim(),
					placeHolders);
			if (fieldSelectionMap.isEmpty()) {
				if (fieldName.equals("fields")) {
					return Collections.EMPTY_MAP;
				}
			}
			if (!fieldSelectionMap.isEmpty()) {
				getMethodNameFromFields(fieldSelectionMap, placeHolders, fieldName);
			}

			this.dbFieldSelection.putAll(fieldSelectionMap);
		}
		return this.dbFieldSelection;
	}

	/**
	 * @param fieldSelectionMap
	 * @param placeHolders
	 * @param fieldName
	 * @throws Exception
	 */
	private void getMethodNameFromFields(final Map<String, List<FastCodeDataBaseFieldDecorator>> fieldSelectionMap,
			final Map<String, Object> placeHolders, final String fieldName) throws Exception {
		final Map<String, String> setMethodFieldTypeMap = new HashMap<String, String>();
		final Map<String, String> getMethodFieldTypeMap = new HashMap<String, String>();
		final JdbcSettings jdbcSettings = JdbcSettings.getInstance();
		String setMethodName = null;
		for (final FastCodeDataBaseFieldDecorator fCDataBaseFieldDecorator : fieldSelectionMap.get(fieldName.trim())) {
			final String fieldType = fCDataBaseFieldDecorator.getType().toLowerCase().trim();
			if (jdbcSettings.getFieldTypeMethodMap().containsKey(fieldType)) {
				setMethodName = jdbcSettings.getFieldTypeMethodMap().get(fieldType);
			}

			setMethodFieldTypeMap.put(fCDataBaseFieldDecorator.getType().toLowerCase(), setMethodName);
			getMethodFieldTypeMap.put(fCDataBaseFieldDecorator.getType().toLowerCase(), setMethodName.replace("set", "get"));
		}

		if (fieldName.trim().equals("fields")) {
			placeHolders.put("MethodFieldMap", setMethodFieldTypeMap);
			placeHolders.put("GetMethodFieldMap", getMethodFieldTypeMap);
		} else if (fieldName.trim().equals("where_fields")) {
			placeHolders.put("MethodWhereFieldMap", setMethodFieldTypeMap);
		}
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#checkSetMethodInPojoClass(java.util.Map, org.eclipse.jdt.core.IType)
	 */
	@Override
	protected Map<String, String> checkSetMethodInPojoClass(final Map<String, List<FastCodeDataBaseFieldDecorator>> dbFieldSelection,
			final IType selectedPojoClassType) throws Exception {
		final Map<String, String> fieldsNameMethodMap = new HashMap<String, String>();
		List<FastCodeDataBaseFieldDecorator> selectFields = new ArrayList<FastCodeDataBaseFieldDecorator>();

		selectFields = dbFieldSelection.get("fields");
		final List<String> pojoClassSetMethods = new ArrayList<String>();
		for (final IMethod method : selectedPojoClassType.getMethods()) {
			if (method.getElementName().startsWith("set")) {
				pojoClassSetMethods.add(method.getElementName());
			}
		}
		int count = 0;
		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : selectFields) {
			boolean found = false;
			final String javaFieldName = fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().getJavaName();
			final String fieldName = fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().getName();
			for (final String methodName : pojoClassSetMethods) {
				final String pojoClassField = methodName.replaceFirst("set", EMPTY_STR).trim();
				if (javaFieldName.equalsIgnoreCase(pojoClassField)) {
					fieldsNameMethodMap.put(fieldName, methodName);
					count++;
					found = true;
					break;
				}
			}
			if (!found) {
				//	fieldsNameMethodMap.put(fieldName, EMPTY_STR);
				continue;
			}
		}

		if (count > 0) {
			return fieldsNameMethodMap;
		} else {
			return null;
		}
	}

	/**
	 *
	 * @param compilationUnit
	 * @param type
	 * @throws Exception
	 */
	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#addImport(org.eclipse.jdt.core.ICompilationUnit, org.eclipse.jdt.core.IType)
	 */
	@Override
	protected void addImport(final ICompilationUnit compilationUnit, final IType type) throws Exception {

		final String fldNamesProp = this.templateType + UNDERSCORE + "IMPORTS";
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String fieldNames = globalSettings.getPropertyValue(fldNamesProp, EMPTY_STR);
		if (fieldNames.equals(EMPTY_STR)) {
			return;
		}
		for (final String fieldName : fieldNames.split("\\s+")) {
			final String fldNme = fieldName.replaceAll(COMMA, EMPTY_STR).trim();
			final IImportDeclaration imprt = compilationUnit.getImport(fldNme);
			final IType pType = compilationUnit.findPrimaryType();
			/*
			 * if (pType.equals(type)) { return; }
			 */
			if (imprt == null || !imprt.exists()) {
				compilationUnit.createImport(fldNme, null, null);
			}
		}

	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#addPojoClassImport(org.eclipse.jdt.core.ICompilationUnit, org.eclipse.jdt.core.IType, java.util.Map)
	 */
	@Override
	protected void addPojoClassImport(final ICompilationUnit compilationUnit, final IType selectedPojoClassType,
			final Map<String, Object> placeHolders) {
		if (compilationUnit == null) {
			return;
		}
		if (placeHolders.get("pojo_class_instance") != null) {
			try {
				super.addImport(compilationUnit, selectedPojoClassType);
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(final IAction action, final ISelection selection) {
		super.selectionChanged(action, selection);
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#addDefaultClassToPlaceHolders(org.eclipse.jdt.core.IType, java.util.Map)
	 */
	@Override
	protected void addDefaultClassToPlaceHolders(final IType type, final Map<String, Object> placeHolders) throws Exception {

	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#requireAnnotation()
	 */
	@Override
	protected boolean requireAnnotation() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#getCreateSnippetData(java.lang.String)
	 */
	@Override
	protected CreateSnippetData getCreateSnippetData(final String fileName) throws Exception {
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		final CreateSnippetData createSnippetData = new CreateSnippetData();
		final TemplateSettings templateSettings = getTemplateSettings(this.templateType);

		if (validateTemplateSetting(templateSettings, fileName)) {
			createSnippetData.setTemplatePrefix(this.templatePrefix);
			final String[] snippetTypes = new String[] { this.templateType.replaceFirst("^" + this.templatePrefix + UNDERSCORE, EMPTY_STR) };
			createSnippetData.setSnippetTypes(snippetTypes);
			// getTableFromDb(this.con);
			// createSnippetData.setTablesInDB(this.databaseCache.getDbTableListMap().get(databaseConnectionSettings.getTypesofDabases()));
			createSnippetData.setTemplateSettings(templateSettings);
			/*
			  for (final String tableName : createSnippetData.getTablesInDB())
			  { getTableColumnsFromDB(tableName, this.con); }
			 */
			getSchemaFromDb(this.con, databaseConnectionSettings.getTypesofDabases());
			createSnippetData.setSchemasInDB(this.databaseCache.getDbSchemaListMap().get(databaseConnectionSettings.getTypesofDabases()));
			final CreateSnippetDialog createSnippetDialog = new CreateSnippetDialog(new Shell(), createSnippetData);
			if (createSnippetDialog.open() == Window.CANCEL) {
				if (this.con != null) {
					this.con.close();
				}
				return null;
			}
			return createSnippetData;
		}
		return null;
	}

}

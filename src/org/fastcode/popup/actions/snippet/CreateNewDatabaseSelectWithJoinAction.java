package org.fastcode.popup.actions.snippet;

import static org.fastcode.common.FastCodeConstants.DATABASE_NAME;
import static org.fastcode.common.FastCodeConstants.EXIT_KEY;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_WITH_JOIN;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;
import static org.fastcode.util.DatabaseUtil.getSchemaFromDb;

import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.common.CreateJoinData;
import org.fastcode.common.CreateSnippetData;
import org.fastcode.common.FastCodeConstants.NUMBER_OF_JOIN_TABLES;
import org.fastcode.common.FastCodeType;
import org.fastcode.dialog.CreateJoinDialog;
import org.fastcode.preferences.DatabaseConnectionSettings;
import org.fastcode.setting.TemplateSettings;

public class CreateNewDatabaseSelectWithJoinAction extends AbstractCreateNewDatabaseSnippetAction implements IEditorActionDelegate,
		IActionDelegate, IWorkbenchWindowActionDelegate {

	public CreateNewDatabaseSelectWithJoinAction() {

		this.templatePrefix = P_DATABASE_TEMPLATE_PREFIX;
		this.templateType = DATABASE_TEMPLATE_SELECT_WITH_JOIN;

	}

	/**
	 * @return
	 * @throws Exception
	 */
	protected CreateJoinData getCreateJoinData() throws Exception {
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		final CreateJoinData createJoinData = new CreateJoinData();

		/* getTableFromDb(this.con);
		createJoinData.setFirstTablesInDB(this.databaseCache.getDbTableListMap().get(databaseConnectionSettings.getTypesofDabases()));
		 createJoinData.setSecondTablesInDB(this.databaseCache.getDbTableListMap().get(databaseConnectionSettings.getTypesofDabases()));
		 createJoinData.setThirdTablesInDB(this.databaseCache.getDbTableListMap().get(databaseConnectionSettings.getTypesofDabases()));*/

		getSchemaFromDb(this.con, databaseConnectionSettings.getTypesofDabases());
		createJoinData.setSchemasInDB(this.databaseCache.getDbSchemaListMap().get(databaseConnectionSettings.getTypesofDabases()));
		createJoinData.setTemplateSettings(getTemplateSettings(this.templateType));
		/*for (final String tableName : createJoinData.getFirstTablesInDB()) {
			getTableColumnsFromDB(tableName, this.con);
		}*/

		final CreateJoinDialog createJoinDialog = new CreateJoinDialog(new Shell(), createJoinData);
		if (createJoinDialog.open() == Window.CANCEL) {
			return null;
		}
		return createJoinData;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewDatabaseSnippetAction#getCreateSnippetData(java.lang.String)
	 */
	@Override
	public CreateSnippetData getCreateSnippetData(final String fileName) throws Exception {
		final CreateSnippetData createSnippetData = new CreateSnippetData();
		createSnippetData.setTemplatePrefix(this.templatePrefix);
		createSnippetData.setTemplateType(this.templateType);
		createSnippetData.setTemplateSettings(getTemplateSettings(this.templateType));
		createSnippetData.setTemplateVariationField(getTemplateVariationField());
		createSnippetData.setVariationsSelected(new String[] { "join" });
		return createSnippetData;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#initializePlaceHolders(org.fastcode.setting.TemplateSettings, java.util.Map)
	 */
	@Override
	protected void initializePlaceHolders(final TemplateSettings templateSettings, final Map<String, Object> placeHolders) throws Exception {

		final CreateJoinData createJoinData = getCreateJoinData();
		if (createJoinData == null) {
			placeHolders.put(EXIT_KEY, Boolean.TRUE);
			return;
		}

		placeHolders.put("Number_Of_Join_Tables", createJoinData.getNumberOfJoinTables());
		placeHolders.put("First_Table", createJoinData.getFirstTableName());
		placeHolders.put("Second_Table", createJoinData.getSecondTableName());
		placeHolders.put("First_Table_Instance", createJoinData.getFirstTableInstanceName());
		placeHolders.put("Second_Table_Instance", createJoinData.getSecondTableInstanceName());
		placeHolders.put("First_Table_Join_Column", createJoinData.getSelectedTable1JoinColumn());
		placeHolders.put("Second_Table_Join_Column", createJoinData.getSelectedTable2JoinColumn());
		placeHolders.put("First_Table_Columns", createJoinData.getColumnsOfFirstTableInDB());
		placeHolders.put("Second_Table_Columns", createJoinData.getColumnsOfSecondTableInDB());
		placeHolders.put("JoinTypes", createJoinData.getJoinTypes().getValue());
		placeHolders.put("group_by_fields", createJoinData.getgroupByFieldSelectionMap());
		placeHolders.put("order_by_fields", createJoinData.getorderByFieldSelectionMap());
		placeHolders.put("use-alias-name", createJoinData.isUseAliasName());
		//placeHolders.put("where_qualifier", createJoinData.getWhereClauseQualifier().getValue());
		placeHolders.put("where_separator", createJoinData.getWhereClauseSeparator().getValue());
		placeHolders.put("schema1", createJoinData.getFirstSchemaSelected());
		placeHolders.put("schema2", createJoinData.getSecondSchemaSelected());
		placeHolders.put(DATABASE_NAME, createJoinData.getSelectedDatabaseName());

		if (createJoinData.getNumberOfJoinTables().equals(NUMBER_OF_JOIN_TABLES.THREE)) {
			placeHolders.put("Third_Table", createJoinData.getThirdTableName());
			placeHolders.put("Third_Table_Instance", createJoinData.getThirdTableInstanceName());
			placeHolders.put("Third_Table_Join_Column", createJoinData.getSelectedTable3JoinColumn());
			placeHolders.put("Copy_Of_Third_Table_Join_Column", createJoinData.getSelectedCopyOfTable3JoinColumn());
			placeHolders.put("Third_Table_Columns", createJoinData.getColumnsOfThirdTableInDB());
			placeHolders.put("schema3", createJoinData.getThirdSchemaSelected());

		}

		if (createJoinData.getModifiedTemplateBody() != null) {
			placeHolders.put("ModifiedTemplateBody", createJoinData.getModifiedTemplateBody());
		}
		if (createJoinData.getiSelectPojoClassType() != null) {
			placeHolders.put("class", new FastCodeType(createJoinData.getiSelectPojoClassType()));
		}

		super.initializePlaceHolders(templateSettings, placeHolders);
	}
}

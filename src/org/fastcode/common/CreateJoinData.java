package org.fastcode.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IType;
import org.fastcode.common.FastCodeConstants.JOIN_TYPES;
import org.fastcode.common.FastCodeConstants.NUMBER_OF_JOIN_TABLES;
import org.fastcode.common.FastCodeConstants.WHERE_CLAUSE_SEPARATOR;
import org.fastcode.setting.TemplateSettings;

public class CreateJoinData {
	//private WHERE_CLAUSE_QUALIFIER	whereClauseQualifier	= WHERE_CLAUSE_QUALIFIER.NONE;
	private WHERE_CLAUSE_SEPARATOR					whereClauseSeparator;
	private JOIN_TYPES								joinTypes;
	private List<String>							firstTablesInDB				= new ArrayList<String>();
	private List<String>							secondTablesInDB			= new ArrayList<String>();
	private List<String>							thirdTablesInDB				= new ArrayList<String>();
	private String[]								columnsOfFirstTableInDB;
	private String[]								columnsOfSecondTableInDB;
	private String[]								columnsOfThirdTableInDB;
	private String									firstTableName;
	private String									secondTableName;
	private String									thirdTableName;
	private String									firstTableInstanceName;
	private String									secondTableInstanceName;
	private String									thirdTableInstanceName;
	private String									selectedTable1JoinColumn;
	private String									selectedTable2JoinColumn;
	private String									selectedTable3JoinColumn;
	private String									selectedCopyOfTable3JoinColumn;
	private TemplateSettings						templateSettings;
	private String									modifiedTemplateBody;
	private NUMBER_OF_JOIN_TABLES					numberOfJoinTables;
	private List<FastCodeDataBaseFieldDecorator>	groupByFieldSelectionMap	= null;
	private List<FastCodeDataBaseFieldDecorator>	orderByFieldSelectionMap	= null;
	private IType									iSelectPojoClassType;
	private String									firstSchemaSelected;
	private List<String>							schemasInDB					= new ArrayList<String>();
	private String									secondSchemaSelected;

	private String									thirdSchemaSelected;
	private boolean									useAliasName;
	private String									selectedDatabaseName;

	/*
	 * public void setWhereClauseQualifier(final WHERE_CLAUSE_QUALIFIER
	 * whereClauseQualifier) { this.whereClauseQualifier = whereClauseQualifier;
	 * }
	 *
	 * public WHERE_CLAUSE_QUALIFIER getWhereClauseQualifier() { return
	 * this.whereClauseQualifier; }
	 */

	public void setWhereClauseSeparator(final WHERE_CLAUSE_SEPARATOR whereClauseSeparator) {
		this.whereClauseSeparator = whereClauseSeparator;
	}

	public WHERE_CLAUSE_SEPARATOR getWhereClauseSeparator() {
		return this.whereClauseSeparator;
	}

	public void setFirstTablesInDB(final List<String> firstTablesInDB) {
		this.firstTablesInDB = firstTablesInDB;
	}

	public List<String> getFirstTablesInDB() {
		return this.firstTablesInDB;
	}

	public void setSecondTablesInDB(final List<String> secondTablesInDB) {
		this.secondTablesInDB = secondTablesInDB;
	}

	public List<String> getSecondTablesInDB() {
		return this.secondTablesInDB;
	}

	public void setFirstTableInstanceName(final String firstTableInstanceName) {
		this.firstTableInstanceName = firstTableInstanceName;
	}

	public String getFirstTableInstanceName() {
		return this.firstTableInstanceName;
	}

	public void setSecondTableInstanceName(final String secondTableInstanceName) {
		this.secondTableInstanceName = secondTableInstanceName;
	}

	public String getSecondTableInstanceName() {
		return this.secondTableInstanceName;
	}

	public void setFirstTableName(final String firstTableName) {
		this.firstTableName = firstTableName;
	}

	public String getFirstTableName() {
		return this.firstTableName;
	}

	public void setSecondTableName(final String secondTableName) {
		this.secondTableName = secondTableName;
	}

	public String getSecondTableName() {
		return this.secondTableName;
	}

	public void setJoinTypes(final JOIN_TYPES joinTypes) {
		this.joinTypes = joinTypes;
	}

	public JOIN_TYPES getJoinTypes() {
		return this.joinTypes;
	}

	public void setColumnsOfFirstTableInDB(final String[] columnsOfFirstTableInDB) {
		this.columnsOfFirstTableInDB = columnsOfFirstTableInDB;
	}

	public String[] getColumnsOfFirstTableInDB() {
		return this.columnsOfFirstTableInDB;
	}

	public void setColumnsOfSecondTableInDB(final String[] columnsOfSecondTableInDB) {
		this.columnsOfSecondTableInDB = columnsOfSecondTableInDB;
	}

	public String[] getColumnsOfSecondTableInDB() {
		return this.columnsOfSecondTableInDB;
	}

	public void setSelectedTable1JoinColumn(final String selectedTable1JoinColumn) {
		this.selectedTable1JoinColumn = selectedTable1JoinColumn;
	}

	public String getSelectedTable1JoinColumn() {
		return this.selectedTable1JoinColumn;
	}

	public void setSelectedTable2JoinColumn(final String selectedTable2JoinColumn) {
		this.selectedTable2JoinColumn = selectedTable2JoinColumn;
	}

	public String getSelectedTable2JoinColumn() {
		return this.selectedTable2JoinColumn;
	}

	public void setModifiedTemplateBody(final String modifiedTemplateBody) {
		this.modifiedTemplateBody = modifiedTemplateBody;
	}

	public String getModifiedTemplateBody() {
		return this.modifiedTemplateBody;
	}

	public void setTemplateSettings(final TemplateSettings templateSettings) {
		this.templateSettings = templateSettings;
	}

	public TemplateSettings getTemplateSettings() {
		return this.templateSettings;
	}

	public void setThirdTablesInDB(final List<String> thirdTablesInDB) {
		this.thirdTablesInDB = thirdTablesInDB;
	}

	public List<String> getThirdTablesInDB() {
		return this.thirdTablesInDB;
	}

	public void setColumnsOfThirdTableInDB(final String[] columnsOfThirdTableInDB) {
		this.columnsOfThirdTableInDB = columnsOfThirdTableInDB;
	}

	public String[] getColumnsOfThirdTableInDB() {
		return this.columnsOfThirdTableInDB;
	}

	public void setThirdTableName(final String thirdTableName) {
		this.thirdTableName = thirdTableName;
	}

	public String getThirdTableName() {
		return this.thirdTableName;
	}

	public void setThirdTableInstanceName(final String thirdTableInstanceName) {
		this.thirdTableInstanceName = thirdTableInstanceName;
	}

	public String getThirdTableInstanceName() {
		return this.thirdTableInstanceName;
	}

	public void setSelectedTable3JoinColumn(final String selectedTable3JoinColumn) {
		this.selectedTable3JoinColumn = selectedTable3JoinColumn;
	}

	public String getSelectedTable3JoinColumn() {
		return this.selectedTable3JoinColumn;
	}

	public void setNumberOfJoinTables(final NUMBER_OF_JOIN_TABLES numberOfJoinTables) {
		this.numberOfJoinTables = numberOfJoinTables;
	}

	public NUMBER_OF_JOIN_TABLES getNumberOfJoinTables() {
		return this.numberOfJoinTables;
	}

	public void setSelectedCopyOfTable3JoinColumn(final String selectedCopyOfTable3JoinColumn) {
		this.selectedCopyOfTable3JoinColumn = selectedCopyOfTable3JoinColumn;
	}

	public String getSelectedCopyOfTable3JoinColumn() {
		return this.selectedCopyOfTable3JoinColumn;
	}

	public void setgroupByFieldSelectionMap(final List<FastCodeDataBaseFieldDecorator> groupByFieldSelectionMap) {
		this.groupByFieldSelectionMap = groupByFieldSelectionMap;
	}

	public void setorderByFieldSelectionMap(final List<FastCodeDataBaseFieldDecorator> orderByFieldSelectionMap) {
		this.orderByFieldSelectionMap = orderByFieldSelectionMap;
	}

	public List<FastCodeDataBaseFieldDecorator> getgroupByFieldSelectionMap() {
		return this.groupByFieldSelectionMap;
	}

	public List<FastCodeDataBaseFieldDecorator> getorderByFieldSelectionMap() {
		return this.orderByFieldSelectionMap;
	}

	public void setiSelectPojoClassType(final IType iSelectPojoClassType) {
		this.iSelectPojoClassType = iSelectPojoClassType;
	}

	public IType getiSelectPojoClassType() {
		return this.iSelectPojoClassType;
	}

	public String getFirstSchemaSelected() {
		return this.firstSchemaSelected;
	}

	public void setFirstSchemaSelected(final String firstSchemaSelected) {
		this.firstSchemaSelected = firstSchemaSelected;
	}

	public String getSecondSchemaSelected() {
		return this.secondSchemaSelected;
	}

	public void setSecondSchemaSelected(final String secondSchemaSelected) {
		this.secondSchemaSelected = secondSchemaSelected;
	}

	public String getThirdSchemaSelected() {
		return this.thirdSchemaSelected;
	}

	public void setThirdSchemaSelected(final String thirdSchemaSelected) {
		this.thirdSchemaSelected = thirdSchemaSelected;
	}

	public List<String> getSchemasInDB() {
		return this.schemasInDB;
	}

	public void setSchemasInDB(final List<String> schemasInDB) {
		this.schemasInDB = schemasInDB;
	}

	public boolean isUseAliasName() {
		return this.useAliasName;
	}

	public void setUseAliasName(final boolean useAliasName) {
		this.useAliasName = useAliasName;
	}

	/**
	 * @return the selectedDatabaseName
	 */
	public String getSelectedDatabaseName() {
		return selectedDatabaseName;
	}

	/**
	 * @param selectedDatabaseName the selectedDatabaseName to set
	 */
	public void setSelectedDatabaseName(String selectedDatabaseName) {
		this.selectedDatabaseName = selectedDatabaseName;
	}
}

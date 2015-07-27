package org.fastcode.common;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IType;
import org.fastcode.common.FastCodeConstants.QUERY_CHOICES;
import org.fastcode.common.FastCodeConstants.WHERE_CLAUSE_QUALIFIER;
import org.fastcode.common.FastCodeConstants.WHERE_CLAUSE_SEPARATOR;
import org.fastcode.setting.TemplateSettings;

public class CreateQueryData {
	private QUERY_CHOICES			queryChoices;
	private WHERE_CLAUSE_QUALIFIER	whereClauseQualifier	= WHERE_CLAUSE_QUALIFIER.NONE;
	private WHERE_CLAUSE_SEPARATOR	whereClauseSeparator	= WHERE_CLAUSE_SEPARATOR.NONE;
	private String[]				existingNamedQueries;
	private String					namedQueryFileName;
	private String					selectClassName;
	private IFile					iNamedqueryFile;
	private IType					iSelectClassType;
	private int						choice;
	private String					existingNamedQueryType;
	private String					newNamedQueryType;
	private List<String>			tablesInDB				= new ArrayList<String>();
	private String					namedQueryFileContent;
	private String					templatePrefix;
	private String					tableName;
	private String					templateType;
	private IType					iSelectPojoClassType;
	private TemplateSettings		templateSettings;
	private String					modifiedTemplateBody;
	private String					schemaSelected;
	private List<String>			schemasInDB				= new ArrayList<String>();
	private String					selectedDatabaseName;

	/**
	 * @param queryChoices
	 *            the queryChoices to set
	 */
	public void setQueryChoices(final QUERY_CHOICES queryChoices) {
		this.queryChoices = queryChoices;
	}

	/**
	 * @return the queryChoices
	 */
	public QUERY_CHOICES getQueryChoices() {
		return this.queryChoices;
	}

	/**
	 * @param whereClauseQualifier
	 *            the whereClauseQualifier to set
	 */

	public void setWhereClauseQualifier(final WHERE_CLAUSE_QUALIFIER whereClauseQualifier) {
		this.whereClauseQualifier = whereClauseQualifier;
	}

	/**
		* @return the whereClauseQualifier
		*/

	public WHERE_CLAUSE_QUALIFIER getWhereClauseQualifier() {
		return this.whereClauseQualifier;
	}

	/**
	 * @param whereClauseSeparator
	 *            the whereClauseSeparator to set
	 */
	public void setWhereClauseSeparator(final WHERE_CLAUSE_SEPARATOR whereClauseSeparator) {
		this.whereClauseSeparator = whereClauseSeparator;
	}

	/**
	 * @return the whereClauseSeparator
	 */
	public WHERE_CLAUSE_SEPARATOR getWhereClauseSeparator() {
		return this.whereClauseSeparator;
	}

	/**
	 * @param exiatingNamedQueries
	 *            the exiatingNamedQueries to set
	 */
	public void setExistingNamedQueries(final String[] existingNamedQueries) {
		this.existingNamedQueries = existingNamedQueries;
	}

	/**
	 * @return the exiatingNamedQueries
	 */
	public String[] getExistingNamedQueries() {
		return this.existingNamedQueries;
	}

	/**
	 * @param namedQueryFileName
	 *            the namedQueryFileName to set
	 */
	public void setNamedQueryFileName(final String namedQueryFileName) {
		this.namedQueryFileName = namedQueryFileName;
	}

	/**
	 * @return the namedQueryFileName
	 */
	public String getNamedQueryFileName() {
		return this.namedQueryFileName;
	}

	/**
	 * @param selectClassName
	 *            the selectClassName to set
	 */
	public void setSelectClassName(final String selectClassName) {
		this.selectClassName = selectClassName;
	}

	/**
	 * @return the selectClassName
	 */
	public String getSelectClassName() {
		return this.selectClassName;
	}

	/**
	 * @param iSelectClassType
	 *            the iSelectClassType to set
	 */
	public void setiSelectClassType(final IType iSelectClassType) {
		this.iSelectClassType = iSelectClassType;
	}

	/**
	 * @return the iSelectClassType
	 */
	public IType getiSelectClassType() {
		return this.iSelectClassType;
	}

	/**
	 * @param iNamedqueryFile
	 *            the iNamedqueryFile to set
	 */
	public void setiNamedqueryFile(final IFile iNamedqueryFile) {
		this.iNamedqueryFile = iNamedqueryFile;
	}

	/**
	 * @return the iNamedqueryFile
	 */
	public IFile getiNamedqueryFile() {
		return this.iNamedqueryFile;
	}

	/**
	 * @param choice
	 *            the choice to set
	 */
	public void setChoice(final int choice) {
		this.choice = choice;
	}

	/**
	 * @return the choice
	 */
	public int getChoice() {
		return this.choice;
	}

	/**
	 * @return the tablesInDB
	 */
	public List<String> getTablesInDB() {
		return this.tablesInDB;
	}

	/**
	 * @param newNamedQueryType
	 *            the newNamedQueryType to set
	 */
	public void setNewNamedQueryType(final String newNamedQueryType) {
		this.newNamedQueryType = newNamedQueryType;
	}

	/**
	 * @return the newNamedQueryType
	 */
	public String getNewNamedQueryType() {
		return this.newNamedQueryType;
	}

	/**
	 * @param existingNamedQueryType
	 *            the existingNamedQueryType to set
	 */
	public void setExistingNamedQueryType(final String existingNamedQueryType) {
		this.existingNamedQueryType = existingNamedQueryType;
	}

	/**
	 * @return the existingNamedQueryType
	 */
	public String getExistingNamedQueryType() {
		return this.existingNamedQueryType;
	}

	/**
	 * @param namedQueryFileContent
	 *            the namedQueryFileContent to set
	 */
	public void setNamedQueryFileContent(final String namedQueryFileContent) {
		this.namedQueryFileContent = namedQueryFileContent;
	}

	/**
	 * @return the namedQueryFileContent
	 */
	public String getNamedQueryFileContent() {
		return this.namedQueryFileContent;
	}

	public void setTablesInDB(final List<String> tablesInDB) {
		this.tablesInDB = tablesInDB;
	}

	/**
	 *
	 * getter method for templatePrefix
	 * @return
	 *
	 */
	public String getTemplatePrefix() {
		return this.templatePrefix;
	}

	/**
	 *
	 * setter method for templatePrefix
	 * @param templatePrefix
	 *
	 */
	public void setTemplatePrefix(final String templatePrefix) {
		this.templatePrefix = templatePrefix;
	}

	/**
	 *
	 * getter method for tableName
	 * @return
	 *
	 */
	public String getTableName() {
		return this.tableName;
	}

	/**
	 *
	 * setter method for tableName
	 * @param tableName
	 *
	 */
	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @param templateType the templateType to set
	 */
	public void setTemplateType(final String templateType) {
		this.templateType = templateType;
	}

	/**
	 * @return the templateType
	 */
	public String getTemplateType() {
		return this.templateType;
	}

	/**
	 * @param iSelectPojoClassType
	 *            the iSelectPojoClassType to set
	 */
	public void setiSelectPojoClassType(final IType iSelectPojoClassType) {
		this.iSelectPojoClassType = iSelectPojoClassType;
	}

	/**
	 * @return the iSelectPojoClassType
	 */
	public IType getiSelectPojoClassType() {
		return this.iSelectPojoClassType;
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

	public String getSchemaSelected() {
		return this.schemaSelected;
	}

	public void setSchemaSelected(final String schemaSelected) {
		this.schemaSelected = schemaSelected;
	}

	public List<String> getSchemasInDB() {
		return this.schemasInDB;
	}

	public void setSchemasInDB(final List<String> schemasInDB) {
		this.schemasInDB = schemasInDB;
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

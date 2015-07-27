package org.fastcode.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IEditorPart;
import org.fastcode.common.FastCodeConstants.WHERE_CLAUSE_SEPARATOR;
import org.fastcode.setting.TemplateSettings;

public class CreateSnippetData {

	private String[]								snippetTypes;
	private String[]								templateVariations;
	private String									templatePrefix;
	private String									templateType;
	private String									description;
	private List<String>							tablesInDB					= new ArrayList<String>();
	private String									tableSelected;
	private boolean									requiresClass;
	private boolean									requiresFile;
	private IType									classSelected;
	private List<FastCodeFile>						fastCodeFiles				= new ArrayList<FastCodeFile>();
	private TemplateSettings						templateSettings;
	// private boolean fromTempSettings;
	private String									templateVariationField;
	private String[]								variationsSelected;
	private Map<Integer, List<String>>				classNames					= new HashMap<Integer, List<String>>();
	private IType									fromClass;
	private IType									toClass;
	// private WHERE_CLAUSE_QUALIFIER whereClauseQualifier =
	// WHERE_CLAUSE_QUALIFIER.NONE;
	private WHERE_CLAUSE_SEPARATOR					whereClauseSeparator		= WHERE_CLAUSE_SEPARATOR.NONE;
	// private boolean defaultInstance;
	private String									instanceName;
	private String									toInstanceName;
	// private String editorFileName;
	private IFile									resourceFile;
	private String									templateBodyFromSnippetDialog;
	private IType									iSelectPojoClassType;
	private IJavaProject							javaProject;
	private boolean									requirePackage				= false;
	private boolean									requireFolder				= false;
	private IPackageFragment						packageFragment;
	private IFolder									folder;
	private boolean									showLocalVriable;
	private List<FastCodeReturn>					localVariables;

	private List<FastCodeDataBaseFieldDecorator>	groupByFieldSelectionMap	= null;
	private List<FastCodeDataBaseFieldDecorator>	orderByFieldSelectionMap	= null;
	private IType									enumType;
	private String									schemaSelected;
	private List<String>							schemasInDB					= new ArrayList<String>();
	private String									selectedText;
	private boolean									useAliasName;
	private IEditorPart								editorpart;
	private FastCodeProject							selectedProject;
	private String									selectedDatabaseName;
	private boolean									doAutoCheckin;

	/**
	 *
	 * getter method for snippetTypes
	 *
	 * @return
	 *
	 */
	public String[] getSnippetTypes() {
		return this.snippetTypes;
	}

	/**
	 *
	 * setter method for snippetTypes
	 *
	 * @param snippetTypes
	 *
	 */
	public void setSnippetTypes(final String[] snippetTypes) {
		this.snippetTypes = snippetTypes;
	}

	public String[] getTemplateVariations() {
		return this.templateVariations;
	}

	public void setTemplateVariations(final String[] templateVariations) {
		this.templateVariations = templateVariations;
	}

	/**
	 *
	 * getter method for templatePrefix
	 *
	 * @return
	 *
	 */
	public String getTemplatePrefix() {
		return this.templatePrefix;
	}

	/**
	 *
	 * setter method for templatePrefix
	 *
	 * @param templatePrefix
	 *
	 */
	public void setTemplatePrefix(final String templatePrefix) {
		this.templatePrefix = templatePrefix;
	}

	public String getTemplateType() {
		return this.templateType;
	}

	public void setTemplateType(final String templateType) {
		this.templateType = templateType;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public List<String> getTablesInDB() {
		return this.tablesInDB;
	}

	public void setTablesInDB(final List<String> tablesInDB) {
		this.tablesInDB = tablesInDB;
	}

	public String getTableSelected() {
		return this.tableSelected;
	}

	public void setTableSelected(final String tableSelected) {
		this.tableSelected = tableSelected;
	}

	public boolean isRequiresClass() {
		return this.requiresClass;
	}

	public void setRequiresClass(final boolean requiresClass) {
		this.requiresClass = requiresClass;
	}

	public boolean isRequiresFile() {
		return this.requiresFile;
	}

	public void setRequiresFile(final boolean requiresFile) {
		this.requiresFile = requiresFile;
	}

	public IType getClassSelected() {
		return this.classSelected;
	}

	public void setClassSelected(final IType classSelected) {
		this.classSelected = classSelected;
	}

	public TemplateSettings getTemplateSettings() {
		return this.templateSettings;
	}

	public void setTemplateSettings(final TemplateSettings templateSettings) {
		this.templateSettings = templateSettings;
	}

	/*
	 * public boolean isFromTempSettings() {return this.fromTempSettings;}
	 *
	 * public void setFromTempSettings(final boolean fromTempSettings)
	 * {this.fromTempSettings = fromTempSettings;}
	 */
	public String getTemplateVariationField() {
		return this.templateVariationField;
	}

	public void setTemplateVariationField(final String templateVariationField) {
		this.templateVariationField = templateVariationField;
	}

	public String[] getVariationsSelected() {
		return this.variationsSelected;
	}

	public void setVariationsSelected(final String[] variationsSelected) {
		this.variationsSelected = variationsSelected;
	}

	public Map<Integer, List<String>> getClassNames() {
		return this.classNames;
	}

	public void setClassNames(final Map<Integer, List<String>> classNames) {
		this.classNames = classNames;
	}

	public IType getFromClass() {
		return this.fromClass;
	}

	public void setFromClass(final IType fromClass) {
		this.fromClass = fromClass;
	}

	public IType getToClass() {
		return this.toClass;
	}

	public void setToClass(final IType toClass) {
		this.toClass = toClass;
	}

	public List<FastCodeFile> getFastCodeFiles() {
		return this.fastCodeFiles;
	}

	public void setFastCodeFiles(final List<FastCodeFile> fastCodeFiles) {
		this.fastCodeFiles = fastCodeFiles;
	}

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

	/*
	 * public boolean isDefaultInstance() {return this.defaultInstance;}
	 *
	 * public void setDefaultInstance(final boolean defaultInstance)
	 * {this.defaultInstance = defaultInstance;}
	 */

	public String getInstanceName() {
		return this.instanceName;
	}

	public void setInstanceName(final String instanceName) {
		this.instanceName = instanceName;
	}

	public String getToInstanceName() {
		return this.toInstanceName;
	}

	public void setToInstanceName(final String toInstanceName) {
		this.toInstanceName = toInstanceName;
	}

	public IFile getResourceFile() {
		return this.resourceFile;
	}

	public void setResourceFile(final IFile resourceFile) {
		this.resourceFile = resourceFile;
	}

	public void setTemplateBodyFromSnippetDialog(final String templateBodyFromSnippetDialog) {
		this.templateBodyFromSnippetDialog = templateBodyFromSnippetDialog;
	}

	public String getTemplateBodyFromSnippetDialog() {
		return this.templateBodyFromSnippetDialog;
	}

	public void setiSelectPojoClassType(final IType iSelectPojoClassType) {
		this.iSelectPojoClassType = iSelectPojoClassType;
	}

	public IType getiSelectPojoClassType() {
		return this.iSelectPojoClassType;
	}

	public IJavaProject getJavaProject() {
		return this.javaProject;
	}

	public void setJavaProject(final IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public boolean isRequirePackage() {
		return this.requirePackage;
	}

	public void setRequirePackage(final boolean requirePackage) {
		this.requirePackage = requirePackage;
	}

	public boolean isRequireFolder() {
		return this.requireFolder;
	}

	public void setRequireFolder(final boolean requireFolder) {
		this.requireFolder = requireFolder;
	}

	public IPackageFragment getPackageFragment() {
		return this.packageFragment;
	}

	public void setPackageFragment(final IPackageFragment packageFragment) {
		this.packageFragment = packageFragment;
	}

	public IFolder getFolder() {
		return this.folder;
	}

	public void setFolder(final IFolder folder) {
		this.folder = folder;
	}

	/*
	 * public String getEditorFileName() {return this.editorFileName;}
	 *
	 * public void setEditorFileName(final String editorFileName)
	 * {this.editorFileName = editorFileName;}
	 */

	public boolean isShowLocalVriable() {
		return this.showLocalVriable;
	}

	public void setShowLocalVriable(final boolean showLocalVriable) {
		this.showLocalVriable = showLocalVriable;
	}

	public List<FastCodeReturn> getLocalVariables() {
		return this.localVariables;
	}

	public void setLocalVariables(final List<FastCodeReturn> localVariables) {
		this.localVariables = localVariables;
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

	/**
	 *
	 * getter method for enumType
	 * @return
	 *
	 */
	public IType getEnumType() {
		return this.enumType;
	}

	/**
	 *
	 * setter method for enumType
	 * @param enumType
	 *
	 */
	public void setEnumType(final IType enumType) {
		this.enumType = enumType;
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

	public String getSelectedText() {
		return this.selectedText;
	}

	public void setSelectedText(final String selectedText) {
		this.selectedText = selectedText;
	}

	public boolean isUseAliasName() {
		return this.useAliasName;
	}

	public void setUseAliasName(final boolean useAliasName) {
		this.useAliasName = useAliasName;
	}

	public IEditorPart getEditorpart() {
		return this.editorpart;
	}

	public void setEditorpart(final IEditorPart editorpart) {
		this.editorpart = editorpart;
	}

	public FastCodeProject getSelectedProject() {
		return this.selectedProject;
	}

	public void setSelectedProject(final FastCodeProject selectedProject) {
		this.selectedProject = selectedProject;
	}

	/**
	 * @return the selectedDatabaseName
	 */
	public String getSelectedDatabaseName() {
		return this.selectedDatabaseName;
	}

	/**
	 * @param selectedDatabaseName the selectedDatabaseName to set
	 */
	public void setSelectedDatabaseName(final String selectedDatabaseName) {
		this.selectedDatabaseName = selectedDatabaseName;
	}

	/**
	 *
	 * getter method for doAutoCheckin
	 * @return
	 *
	 */
	public boolean isDoAutoCheckin() {
		return this.doAutoCheckin;
	}

	/**
	 *
	 * setter method for doAutoCheckin
	 * @param doAutoCheckin
	 *
	 */
	public void setDoAutoCheckin(final boolean doAutoCheckin) {
		this.doAutoCheckin = doAutoCheckin;
	}

}

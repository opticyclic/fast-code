/**
 *
 */
package org.fastcode.common;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.fastcode.common.FastCodeConstants.ACCESS_MODIFIER;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER_FORMAT;

public class CreateVariableData {
	private ACCESS_MODIFIER				accessModifier			= ACCESS_MODIFIER.PRIVATE;
	private GETTER_SETTER				getterSetter;
	/*
	 * private boolean multiline = true; private boolean needComment = true;
	 */
	private String[]					fieldNames;
	private String[]					fieldParams;
	private String[]					existingFields;
	private final List<String>			importTypes				= new ArrayList<String>();
	private String						fieldType;
	private boolean						isFinal;
	private boolean						isStatic;
	private boolean						isArray;
	private boolean						isList;
	private boolean						isSet;
	private boolean						isPrimitive;
	private boolean						isDefined;
	private boolean						isInitialized;
	private String						initialValue;
	private String						listType;
	private String						setType;
	private String						primitiveType;
	private String						setterVerPrefix;
	private IType						importCustomType;
	public final Map<String, String>	classFQNameMap			= new HashMap<String, String>();
	private boolean						isMap;
	private String						mapType;
	private IJavaProject				javaProject;
	private String						compUnitType			= EMPTY_STR;
	private GETTER_SETTER_FORMAT		getterSetterFormat;
	private boolean						hasLombokJar			= false;
	private boolean						isBuilderReqd;
	private List<IField>				iClassFields			= new ArrayList<IField>();
	private IField						insertionPoint;
	private String						stringInsertionPoint;
	private String[]					annotations;
	private boolean						useAnnotation			= false;
	private boolean						isVariableModifyAction	= false;
	private String						modifiedVarOrigName;
	private int							arrayDim;
	private IPackageFragment			packageFragment;
	private String						className;
	private boolean						isCreateClassSimple;
	private boolean						isDefaultConsReqd;
	private IType						iSelectBaseClassType;
	private List<IType>					iInterfaceType			= new ArrayList<IType>();
	private boolean						isCreateClassDetailed;
	private boolean						isInterface				= false;
	private boolean						isCreateFieldSimple;

	public CreateVariableData() {
		this.classFQNameMap.put("List", "java.util.List");
		this.classFQNameMap.put("ArrayList", "java.util.ArrayList");
		this.classFQNameMap.put("LinkedList", "java.util.LinkedList");
		this.classFQNameMap.put("Set", "java.util.Set");
		this.classFQNameMap.put("HashSet", "java.util.HashSet");
		this.classFQNameMap.put("LinkedHashSet", "java.util.LinkedHashSet");
		this.classFQNameMap.put("TreeSet", "java.util.TreeSet");
		this.classFQNameMap.put("Date", "java.util.Date");
		this.classFQNameMap.put("Map", "java.util.Map");
		this.classFQNameMap.put("HashMap", "java.util.HashMap");
		this.classFQNameMap.put("TreeMap", "java.util.TreeMap");

	}

	public ACCESS_MODIFIER getAccessModifier() {
		return this.accessModifier;
	}

	public void setAccessModifier(final ACCESS_MODIFIER accessModifier) {
		this.accessModifier = accessModifier;
	}

	public GETTER_SETTER getGetterSetter() {
		return this.getterSetter;
	}

	public void setGetterSetter(final GETTER_SETTER getterSetter) {
		this.getterSetter = getterSetter;
	}

	public boolean isFinal() {
		return this.isFinal;
	}

	public void setFinal(final boolean isFinal) {
		this.isFinal = isFinal;
	}

	public boolean isStatic() {
		return this.isStatic;
	}

	public void setStatic(final boolean isStatic) {
		this.isStatic = isStatic;
	}

	public boolean isArray() {
		return this.isArray;
	}

	public void setArray(final boolean isArray) {
		this.isArray = isArray;
	}

	public boolean isList() {
		return this.isList;
	}

	public void setList(final boolean isList) {
		this.isList = isList;
	}

	public boolean isSet() {
		return this.isSet;
	}

	public void setSet(final boolean isSet) {
		this.isSet = isSet;
	}

	public String[] getFieldNames() {
		return this.fieldNames;
	}

	public void setFieldNames(final String[] fieldNames) {
		this.fieldNames = fieldNames;
	}

	public String getFieldType() {
		return this.fieldType;
	}

	public void setFieldType(final String fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 *
	 * getter method for fieldParams
	 *
	 * @return
	 *
	 */
	public String[] getFieldParams() {
		return this.fieldParams;
	}

	/**
	 *
	 * setter method for fieldParams
	 *
	 * @param fieldParams
	 *
	 */
	public void setFieldParams(final String[] fieldParams) {
		this.fieldParams = fieldParams;
	}

	public boolean isInitialized() {
		return this.isInitialized;
	}

	public void setInitialized(final boolean initialize) {
		this.isInitialized = initialize;
	}

	public String getInitialValue() {
		return this.initialValue;
	}

	public void setInitialValue(final String initialVal) {
		this.initialValue = initialVal;
	}

	public boolean isPrimitive() {
		return this.isPrimitive;
	}

	public void setPrimitive(final boolean isPrimitive) {
		this.isPrimitive = isPrimitive;
	}

	public boolean isDefined() {
		return this.isDefined;
	}

	public void setDefined(final boolean isDefined) {
		this.isDefined = isDefined;
	}

	public String getListType() {
		return this.listType;
	}

	public void setListType(final String listType) {
		this.listType = listType;
	}

	public String getSetType() {
		return this.setType;
	}

	public void setSetType(final String setType) {
		this.setType = setType;
	}

	public void setSetterVerPrefix(final String setterVerPrefix) {
		this.setterVerPrefix = setterVerPrefix;
	}

	public String getSetterVerPrefix() {
		return this.setterVerPrefix;
	}

	/**
	 *
	 * getter method for importType
	 *
	 * @return
	 *
	 */
	public IType getImportCustomType() {
		return this.importCustomType;
	}

	/**
	 *
	 * setter method for importType
	 *
	 * @param typeSelected
	 *
	 */
	public void setImportCustomType(final IType typeSelected) {
		this.importCustomType = typeSelected;
	}

	/**
	 *
	 * getter method for importTypes
	 *
	 * @return
	 *
	 */
	public List<String> getImportTypes() {
		return this.importTypes;
	}

	/**
	 *
	 * add method for importTypes
	 *
	 * @param string
	 *
	 */
	public void addImportTypes(final String string) {
		this.importTypes.add(string);
	}

	public boolean isMap() {
		return this.isMap;
	}

	public void setMap(final boolean isMap) {
		this.isMap = isMap;
	}

	/**
	 *
	 * getter method for mapType
	 *
	 * @return
	 *
	 */
	public String getMapType() {
		return this.mapType;
	}

	/**
	 *
	 * setter method for mapType
	 *
	 * @param mapType
	 *
	 */
	public void setMapType(final String mapType) {
		this.mapType = mapType;
	}

	public void setExistingFields(final String[] existingFields) {
		this.existingFields = existingFields;
	}

	public String[] getExistingFields() {
		return this.existingFields;
	}

	public IJavaProject getJavaProject() {
		return this.javaProject;
	}

	public void setJavaProject(final IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	/**
	 *
	 * getter method for compUnitType
	 *
	 * @return
	 *
	 */
	public String getCompUnitType() {
		return this.compUnitType;
	}

	/**
	 *
	 * setter method for compUnitType
	 *
	 * @param compUnitType
	 *
	 */
	public void setCompUnitType(final String compUnitType) {
		this.compUnitType = compUnitType;
	}

	/**
	 *
	 * getter method for getterSetterFormat
	 *
	 * @return
	 *
	 */
	public GETTER_SETTER_FORMAT getGetterSetterFormat() {
		return this.getterSetterFormat;
	}

	/**
	 *
	 * setter method for getterSetterFormat
	 *
	 * @param getterSetterFormat
	 *
	 */
	public void setGetterSetterFormat(final GETTER_SETTER_FORMAT getterSetterFormat) {
		this.getterSetterFormat = getterSetterFormat;
	}

	public void clear() {
		this.importTypes.clear();
	}

	public boolean isHasLombokJar() {
		return this.hasLombokJar;
	}

	public void setHasLombokJar(final boolean hasLombokJar) {
		this.hasLombokJar = hasLombokJar;
	}

	public void setInsertionPoint(final IField insertionPoint) {
		this.insertionPoint = insertionPoint;
	}

	public IField getInsertionPoint() {
		return this.insertionPoint;
	}

	public void setiClassFields(final List<IField> iClassFields) {
		this.iClassFields = iClassFields;
	}

	public List<IField> getiClassFields() {
		return this.iClassFields;
	}

	public void setStringInsertionPoint(final String stringInsertionPoint) {
		this.stringInsertionPoint = stringInsertionPoint;
	}

	public String getStringInsertionPoint() {
		return this.stringInsertionPoint;
	}

	public void setAnnotations(final String[] annotations) {
		this.annotations = annotations;
	}

	public String[] getAnnotations() {
		return this.annotations;
	}

	public void setUseAnnotation(final boolean useAnnotation) {
		this.useAnnotation = useAnnotation;
	}

	public boolean isUseAnnotation() {
		return this.useAnnotation;
	}

	public boolean isBuilderReqd() {
		return this.isBuilderReqd;
	}

	public void setBuilderReqd(final boolean isBuilderReqd) {
		this.isBuilderReqd = isBuilderReqd;
	}

	public boolean isVariableModifyAction() {
		return this.isVariableModifyAction;
	}

	public void setVariableModifyAction(final boolean isVariableModifyAction) {
		this.isVariableModifyAction = isVariableModifyAction;
	}

	public String getModifiedVarOrigName() {
		return this.modifiedVarOrigName;
	}

	public void setModifiedVarOrigName(final String modifiedVarOrigName) {
		this.modifiedVarOrigName = modifiedVarOrigName;
	}

	public int getArrayDim() {
		return this.arrayDim;
	}

	public void setArrayDim(final int arrayDim) {
		this.arrayDim = arrayDim;
	}

	public IPackageFragment getPackageFragment() {
		return this.packageFragment;
	}

	public void setPackageFragment(final IPackageFragment packageFragment) {
		this.packageFragment = packageFragment;
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(final String className) {
		this.className = className;
	}

	public boolean isDefaultConsReqd() {
		return this.isDefaultConsReqd;
	}

	public void setDefaultConsReqd(final boolean isDefaultConsReqd) {
		this.isDefaultConsReqd = isDefaultConsReqd;
	}

	public IType getiSelectBaseClassType() {
		return this.iSelectBaseClassType;
	}

	public void setiSelectBaseClassType(final IType iSelectBaseClassType) {
		this.iSelectBaseClassType = iSelectBaseClassType;
	}

	public List<IType> getiInterfaceType() {
		return this.iInterfaceType;
	}

	public void setiInterfaceType(final List<IType> iInterfaceType) {
		this.iInterfaceType = iInterfaceType;
	}

	public boolean isCreateClassSimple() {
		return this.isCreateClassSimple;
	}

	public void setCreateClassSimple(final boolean isCreateClassSimple) {
		this.isCreateClassSimple = isCreateClassSimple;
	}

	public boolean isCreateClassDetailed() {
		return this.isCreateClassDetailed;
	}

	public void setCreateClassDetailed(final boolean isCreateClassDetailed) {
		this.isCreateClassDetailed = isCreateClassDetailed;
	}

	public boolean isInterface() {
		return this.isInterface;
	}

	public void setInterface(final boolean isInterface) {
		this.isInterface = isInterface;
	}

	public boolean isCreateFieldSimple() {
		return this.isCreateFieldSimple;
	}

	public void setCreateFieldSimple(final boolean isCreateFieldSimple) {
		this.isCreateFieldSimple = isCreateFieldSimple;
	}
}

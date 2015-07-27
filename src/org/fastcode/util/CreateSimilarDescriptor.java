/*
 * Fast Code Plugin for Eclipse
 *
 * Copyright (C) 2008  Gautam Dev
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA.
 *
 * Plugin Home Page: http://fast-code.sourceforge.net/
 */
package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.ANY_CLASS;
import static org.fastcode.common.FastCodeConstants.ANY_PACKAGE;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.METHOD_PATTERN_DEFAULT;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.NUM_MAX_CONFIGS;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_DAO_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_DAO_POJO_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_SERVICE_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_SERVICE_POJO_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_UI_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_PREFERENCE_POJO_SERVICE_UI_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID;
import static org.fastcode.preferences.PreferenceConstants.P_BREAK_DATE_FIELDS;
import static org.fastcode.preferences.PreferenceConstants.P_CLASS_ANNOTATIONS;
import static org.fastcode.preferences.PreferenceConstants.P_CLASS_IMPORTS;
import static org.fastcode.preferences.PreferenceConstants.P_CLASS_INSIDE_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_CLASS_TYPE;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_END_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_FILE_CONV_TYPES;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_FILE_NAME;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_HEADER_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_LOCALE;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_LOCATION;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_START_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_TYPE;
import static org.fastcode.preferences.PreferenceConstants.P_CONVERT_METHOD_PARAM;
import static org.fastcode.preferences.PreferenceConstants.P_CONVERT_METHOD_PARAM_FROM;
import static org.fastcode.preferences.PreferenceConstants.P_CONVERT_METHOD_PARAM_TO;
import static org.fastcode.preferences.PreferenceConstants.P_COPY_FIELDS;
import static org.fastcode.preferences.PreferenceConstants.P_COPY_METHODS;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_CONFIG;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_DEFAULT_CONSTRUCTOR;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_EQUALS_HASHCODE;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_FIELDS;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_FIELDS_NAME;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_INSTANCE_CONSTRUCTOR;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_METHOD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_TO_STRING;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_UNIT_TEST;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_WORKING_SET;
import static org.fastcode.preferences.PreferenceConstants.P_DIFFERENT_NAME;
import static org.fastcode.preferences.PreferenceConstants.P_EXCLUDE_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_FIELD_ANNOTATIONS;
import static org.fastcode.preferences.PreferenceConstants.P_FINAL_CLASS;
import static org.fastcode.preferences.PreferenceConstants.P_FROM_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_IMPLEMENT_INT;
import static org.fastcode.preferences.PreferenceConstants.P_IMPLEMENT_INTERFACES;
import static org.fastcode.preferences.PreferenceConstants.P_IMPL_SUB_PACKAGE;
import static org.fastcode.preferences.PreferenceConstants.P_INCLUDE_GETTER_SETTER_INSTACE_FROM;
import static org.fastcode.preferences.PreferenceConstants.P_INCLUDE_INSTACE_FROM;
import static org.fastcode.preferences.PreferenceConstants.P_INCLUDE_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_METHOD_ANNOTATIONS;
import static org.fastcode.preferences.PreferenceConstants.P_NEXT_DESCRIPTOR;
import static org.fastcode.preferences.PreferenceConstants.P_NUM_SIMILAR_CLASSES;
import static org.fastcode.preferences.PreferenceConstants.P_PACKAGE;
import static org.fastcode.preferences.PreferenceConstants.P_PROJECT;
import static org.fastcode.preferences.PreferenceConstants.P_SOURCE_PATH;
import static org.fastcode.preferences.PreferenceConstants.P_SUPER_CLASS;
import static org.fastcode.preferences.PreferenceConstants.P_TO_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_WORKING_SET_NAME;
import static org.fastcode.preferences.PreferenceUtil.getPreferenceStoreValueBoolean;
import static org.fastcode.preferences.PreferenceUtil.getPreferenceStoreValueInt;
import static org.fastcode.preferences.PreferenceUtil.getPreferenceStoreValueString;
import static org.fastcode.preferences.PreferenceUtil.preferenceMap;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.StringUtil.covertToRegex;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.replacePlaceHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.fastcode.common.FastCodeConstants.CLASS_TYPE;
import org.fastcode.common.FastCodeConstants.RELATION_TYPE;
import org.fastcode.common.FastCodeConstants.RETURN_TYPE;
import org.fastcode.setting.GlobalSettings;

/**
 * @author Gautam
 *
 */
public class CreateSimilarDescriptor {

	private static final Map<String, CreateSimilarDescriptor>	createSimilarDescriptorMap	= new HashMap<String, CreateSimilarDescriptor>();

	private final String										fromPattern;
	private final String										toPattern;
	// private String classBodyPattern;
	// private boolean createImpl;
	// private String implSubPackage;
	// private IType superType;
	// private IType[] implInterfaceTypes;
	// private boolean inclInstance;
	// private boolean inclGetterSetterForInstance;
	// private boolean copyMethod;
	// private boolean createMethodBody;
	// private String[] methodAnnotations;
	// private String[] classAnnotations;
	// private String[] fieldAnnotations;
	//
	// private boolean copyField;
	// private boolean breakDateFields;
	// private boolean createJunitTest;

	private boolean												copyField					= false;
	private boolean												copyMethod					= false;
	private boolean												breakDateFields				= false;

	private boolean												differentName				= false;
	private boolean												createWorkingSet			= false;

	private String												workingSetName				= null;

	// private boolean createReturnVariable;
	private boolean												createConfig				= false;
	private boolean												replaceName					= false;
	private String												methodPrefix				= null;
	private String												replacePart					= null;
	private String												replaceValue				= null;
	// private String returnVariableName;
	private String												includePattern				= null;
	private String												excludePattern				= null;

	private String												methodNamePattern			= null;

	private RETURN_TYPE											returnType					= RETURN_TYPE.RETURN_TYPE_PASS_THRU;

	private final int											numConfigs;

	private final CreateSimilarDescriptorClass[]				createSimilarDescriptorClasses;
	private CreateSimilarDescriptorConfig[]						descriptorConfigParts		= new CreateSimilarDescriptorConfig[NUM_MAX_CONFIGS];
	private CreateSimilarDescriptor								nextDescriptor				= null;
	private CreateSimilarDescriptorClass[]						createSimilarDescUserChoice;
	private int													noOfInputs;

	/**
	 *
	 * @param fromPattern
	 * @param createWorkingSet
	 * @param workingSetName
	 * @param includePattern
	 * @param excludePattern
	 * @param copyMethod
	 * @param breakDateFields
	 * @param methodNamePattern
	 * @param differentName
	 */
	/*public CreateSimilarDescriptor(final String fromPattern, final boolean createWorkingSet, final String workingSetName, final String includePattern,
			final String excludePattern, final boolean copyMethod, final boolean copyField, final boolean breakDateFields, final String methodNamePattern,
			final boolean differentName, final int numSimilarClasses) {
		this.fromPattern = fromPattern;
		this.createWorkingSet = createWorkingSet;
		this.workingSetName = workingSetName;
		this.includePattern = includePattern;
		this.excludePattern = excludePattern;
		this.copyMethod = copyMethod;
		this.copyField = copyField;
		this.breakDateFields = breakDateFields;
		this.methodNamePattern = methodNamePattern;
		this.differentName = differentName;
		this.createSimilarDescriptorClasses = new CreateSimilarDescriptorClass[numSimilarClasses];
	}*/

	/**
	 *
	 * @param fromPattern
	 * @param createWorkingSet
	 * @param workingSetName
	 * @param includePattern
	 * @param excludePattern
	 * @param createSimilarDescriptorClasses
	 *
	 * @param createSimilarDescriptorClasses
	 */
	/*public CreateSimilarDescriptor(final String fromPattern, final boolean createWorkingSet, final String workingSetName, final String includePattern,
			final String excludePattern, final boolean copyMethod, final boolean copyField, final boolean breakDateFields, final String methodNamePattern,
			final boolean differentName, final CreateSimilarDescriptorClass... createSimilarDescriptorClasses) {

		this(fromPattern, createWorkingSet, workingSetName, includePattern, excludePattern, copyMethod, copyField, breakDateFields, methodNamePattern,
				differentName, createSimilarDescriptorClasses == null ? 0 : createSimilarDescriptorClasses.length);
		if (createSimilarDescriptorClasses != null && createSimilarDescriptorClasses.length > 0) {
			this.createSimilarDescriptorClasses[0] = createSimilarDescriptorClasses[0];
			if (createSimilarDescriptorClasses.length > 1) {
				this.createSimilarDescriptorClasses[1] = createSimilarDescriptorClasses[1];
				this.createSimilarDescriptorClasses[1].setParentDescriptor(this.createSimilarDescriptorClasses[0]);
				this.createSimilarDescriptorClasses[0].getRelatedDescriptors().add(this.createSimilarDescriptorClasses[1]);
			}
		}
	}*/

	public void numbersOfCreateSimilarDescriptorClasses(final CreateSimilarDescriptorClass... createSimilarDescriptorClasses) {
		if (createSimilarDescriptorClasses != null && createSimilarDescriptorClasses.length > 0) {
			this.createSimilarDescriptorClasses[0] = createSimilarDescriptorClasses[0];
			if (createSimilarDescriptorClasses.length > 1) {
				this.createSimilarDescriptorClasses[1] = createSimilarDescriptorClasses[1];
				this.createSimilarDescriptorClasses[1].setParentDescriptor(this.createSimilarDescriptorClasses[0]);
				this.createSimilarDescriptorClasses[0].getRelatedDescriptors().add(this.createSimilarDescriptorClasses[1]);
			}
		}
	}

	public void numbersOfCreateSimilarDescUserChoiceClasses(final CreateSimilarDescriptorClass... createSimilarDescriptorClasses) {
		if (createSimilarDescriptorClasses != null && createSimilarDescriptorClasses.length > 0) {
			this.createSimilarDescUserChoice[0] = createSimilarDescriptorClasses[0];
			if (createSimilarDescriptorClasses.length > 1) {
				this.createSimilarDescUserChoice[1] = createSimilarDescriptorClasses[1];
				this.createSimilarDescUserChoice[1].setParentDescriptor(this.createSimilarDescUserChoice[0]);
				this.createSimilarDescUserChoice[0].getRelatedDescriptors().add(this.createSimilarDescUserChoice[1]);
			}
		}
	}

	/**
	 * @return the replacePart
	 */
	public String getReplacePart() {
		return this.replacePart;
	}

	/**
	 * @return the replaceValue
	 */
	public String getReplaceValue() {
		return this.replaceValue;
	}

	/**
	 *
	 * @param replacePart
	 * @param replaceValue
	 */
	public void createReplacePartAndValue(final String replacePart, final String replaceValue) {
		this.replacePart = replacePart;
		this.replaceValue = replaceValue;
	}

	/**
	 * @param fromType
	 * @param toType
	 * @param fromPattern
	 * @param toPattern
	 * @param createImpl
	 * @param implSubPackage
	 * @param superType
	 * @param inclInstance
	 * @param methodPrefix
	 * @param replacePart
	 * @param replaceValue
	 * @param createReturnVariable
	 * @param returnVariableName
	 *
	 */
	/*
	 * public CreateSimilarDescriptor( String fromPattern, String toPattern,
	 * boolean createImpl, String classBodyPattern, String implSubPackage, IType
	 * superType, IType[] implInterfaceTypes, boolean inclInstance, boolean
	 * inclGetterSetterForInstance, boolean copyMethod, boolean
	 * createMethodBody, boolean copyField, boolean breakDateFields, boolean
	 * createWorkingSet, String workingSetName, boolean createJunitTest,
	 * String[] classAnnotations, String[] fieldAnnotations, String[]
	 * methodAnnotations, String methodPrefix, String replacePart, String
	 * replaceValue, boolean createReturnVariable, String returnVariableName,
	 * String includePattern, String excludePattern) {
	 *
	 * this.fromPattern = fromPattern; this.toPattern = toPattern;
	 * this.createImpl = createImpl; this.classBodyPattern = classBodyPattern;
	 * this.implSubPackage = implSubPackage; this.superType = superType;
	 * this.implInterfaceTypes = implInterfaceTypes; this.inclInstance =
	 * inclInstance; this.inclGetterSetterForInstance =
	 * inclGetterSetterForInstance; this.copyMethod = copyMethod;
	 * this.createMethodBody = createMethodBody; this.copyField = copyField;
	 * this.breakDateFields = breakDateFields; this.createWorkingSet =
	 * createWorkingSet; this.workingSetName = workingSetName;
	 * this.createJunitTest = createJunitTest; this.classAnnotations =
	 * classAnnotations; this.fieldAnnotations = fieldAnnotations;
	 * this.methodAnnotations = methodAnnotations; this.methodPrefix =
	 * methodPrefix; this.replacePart = replacePart; this.replaceValue =
	 * replaceValue; this.createReturnVariable = createReturnVariable;
	 * this.returnVariableName = returnVariableName; this.includePattern =
	 * includePattern; this.excludePattern = excludePattern; }
	 */

	/**
	 *
	 * @param preferenceId
	 */
	public static boolean updateCreateSimilarDescriptor(final String preferenceId) {
		final Map<String, CreateSimilarDescriptor> descriptorMap = CreateSimilarDescriptor.getCreateSimilarDescriptorMap();
		final CreateSimilarDescriptor createSimilarDescriptor = descriptorMap.get(preferenceMap.get(preferenceId));
		if (createSimilarDescriptor != null) {
			descriptorMap.remove(preferenceMap.get(preferenceId));
		}
		try {
			descriptorMap.put(preferenceMap.get(preferenceId), makeCreateSimilarDescriptor(preferenceId));
		} catch (final Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 *
	 * @param preferenceId
	 * @return
	 * @throws Exception
	 */
	protected static CreateSimilarDescriptor makeCreateSimilarDescriptor(final String preferenceId) throws Exception {
		String fromPattern = getPreferenceStoreValueString(P_FROM_PATTERN, preferenceId);
		final GlobalSettings globalSettings = getInstance();

		fromPattern = replacePlaceHolder(fromPattern, ANY_PACKAGE, globalSettings.getPackagePattern());
		fromPattern = replacePlaceHolder(fromPattern, ANY_CLASS, globalSettings.getNameClassPattern());

		fromPattern = covertToRegex(fromPattern);
		final String toPattern = getPreferenceStoreValueString(P_TO_PATTERN, preferenceId);
		// String classBodyPattern =
		// getPreferenceStoreValueString(P_CLASS_BODY_PATTERN, preferenceId);

		// String classHeader = getPreferenceStoreValueString(P_CLASS_HEADER,
		// preferenceId);
		final String classInsideBody = getPreferenceStoreValueString(P_CLASS_INSIDE_BODY, preferenceId);

		final String subPkg = getPreferenceStoreValueString(P_IMPL_SUB_PACKAGE, preferenceId);

		final String superClass = getPreferenceStoreValueString(P_SUPER_CLASS, preferenceId);

		// List<IType> superTypes = new ArrayList<IType>();
		String[] superTypes = null;
		if (!isEmpty(superClass)) {
			superTypes = superClass.split(NEWLINE);
		}

		final Boolean createDefaultConstructor = getPreferenceStoreValueBoolean(P_CREATE_DEFAULT_CONSTRUCTOR, preferenceId);
		final Boolean createInstanceConstructor = getPreferenceStoreValueBoolean(P_CREATE_INSTANCE_CONSTRUCTOR, preferenceId);
		final Boolean includeInstaceFrom = getPreferenceStoreValueBoolean(P_INCLUDE_INSTACE_FROM, preferenceId);
		final Boolean includeGetterSetterInstaceFrom = getPreferenceStoreValueBoolean(P_INCLUDE_GETTER_SETTER_INSTACE_FROM, preferenceId);
		final Boolean copyMeth = getPreferenceStoreValueBoolean(P_COPY_METHODS, preferenceId);
		final Boolean finalClass = getPreferenceStoreValueBoolean(P_FINAL_CLASS, preferenceId);
		final Boolean createMethodBody = getPreferenceStoreValueBoolean(P_CREATE_METHOD_BODY, preferenceId);
		final Boolean copyField = getPreferenceStoreValueBoolean(P_COPY_FIELDS, preferenceId);
		final Boolean breakDateFields = getPreferenceStoreValueBoolean(P_BREAK_DATE_FIELDS, preferenceId);
		//final Boolean createReturnVariable = getPreferenceStoreValueBoolean(P_RETURN_VARIABLE, preferenceId);
		final Boolean createWorkingSet = getPreferenceStoreValueBoolean(P_CREATE_WORKING_SET, preferenceId);
		final Boolean createImpl = getPreferenceStoreValueBoolean(P_IMPLEMENT_INT, preferenceId);
		final Boolean createEqualsHashcode = getPreferenceStoreValueBoolean(P_CREATE_EQUALS_HASHCODE, preferenceId);
		final Boolean createToString = getPreferenceStoreValueBoolean(P_CREATE_TO_STRING, preferenceId);

		final String[] classAnnotations = getPreferenceValuesAsArray(P_CLASS_ANNOTATIONS, preferenceId);
		final String[] fieldAnnotations = getPreferenceValuesAsArray(P_FIELD_ANNOTATIONS, preferenceId);
		final String[] methodAnnotations = getPreferenceValuesAsArray(P_METHOD_ANNOTATIONS, preferenceId);
		;

		final String[] implementInterfaces = getPreferenceValuesAsArray(P_IMPLEMENT_INTERFACES, preferenceId);
		;
		final String[] classImports = getPreferenceValuesAsArray(P_CLASS_IMPORTS, preferenceId);
		;

		final String project = getPreferenceStoreValueString(P_PROJECT, preferenceId);
		final String sourcePath = getPreferenceStoreValueString(P_SOURCE_PATH, preferenceId);
		final String packge = getPreferenceStoreValueString(P_PACKAGE, preferenceId);

		//final String returnVariableName = getPreferenceStoreValueString(P_RETURN_VARIABLE_NAME, preferenceId);
		final String includePattern = getPreferenceStoreValueString(P_INCLUDE_PATTERN, preferenceId);
		final String excludePattern = getPreferenceStoreValueString(P_EXCLUDE_PATTERN, preferenceId);
		final String workingSetName = getPreferenceStoreValueString(P_WORKING_SET_NAME, preferenceId);
		final boolean createUnitTest = getPreferenceStoreValueBoolean(P_CREATE_UNIT_TEST, preferenceId);
		final boolean createFields = getPreferenceStoreValueBoolean(P_CREATE_FIELDS, preferenceId);
		final String createFieldName = getPreferenceStoreValueString(P_CREATE_FIELDS_NAME, preferenceId);
		final boolean differentName = getPreferenceStoreValueBoolean(P_DIFFERENT_NAME, preferenceId);

		final boolean convertMethodParam = getPreferenceStoreValueBoolean(P_CONVERT_METHOD_PARAM, preferenceId);
		String convertMethodParamFrom = null, convertMethodParamTo = null;
		if (convertMethodParam) {
			convertMethodParamFrom = getPreferenceStoreValueString(P_CONVERT_METHOD_PARAM_FROM, preferenceId);
			convertMethodParamTo = getPreferenceStoreValueString(P_CONVERT_METHOD_PARAM_TO, preferenceId);
			convertMethodParamFrom = replacePlaceHolder(convertMethodParamFrom, ANY_PACKAGE, globalSettings.getPackagePattern());
			convertMethodParamFrom = replacePlaceHolder(convertMethodParamFrom, ANY_CLASS, globalSettings.getNameClassPattern());
			convertMethodParamFrom = covertToRegex(convertMethodParamFrom);
		}

		final int numSimilarClasses = getPreferenceStoreValueInt(P_NUM_SIMILAR_CLASSES, preferenceId);

		final CreateSimilarDescriptor createSimilarDescriptor = new CreateSimilarDescriptor.Builder().withFromPattern(fromPattern)
				.withCreateWorkingSet(createWorkingSet).withWorkingSetName(workingSetName).withIncludePattern(includePattern)
				.withExcludePattern(excludePattern).withCopyMethod(copyMeth).withCopyField(copyField).withBreakDateFields(breakDateFields)
				.withMethodNamePattern(METHOD_PATTERN_DEFAULT).withDifferentName(differentName).withNumConfigs(numSimilarClasses)
				.withCreateSimilarDescriptorClasses(new CreateSimilarDescriptorClass[numSimilarClasses]).build();
		//new CreateSimilarDescriptor(fromPattern, createWorkingSet, workingSetName, includePattern,excludePattern, copyMeth, copyField, breakDateFields, METHOD_PATTERN_DEFAULT, differentName, numSimilarClasses);

		for (int numSimilarClass = 0; numSimilarClass < numSimilarClasses; numSimilarClass++) {
			final String classType = getPreferenceStoreValueString(P_CLASS_TYPE, preferenceId, numSimilarClass == 0 ? null
					: numSimilarClass);
			if (isEmpty(classType)) {
				throw new Exception("classType is empty for " + preferenceId);
			}
			final boolean isClass = CLASS_TYPE.getClassType(classType) == CLASS_TYPE.CLASS;
			CreateSimilarDescriptorClass createSimilarDescriptorClass = null;
			if (isClass && createImpl && numSimilarClass > 0) {
				createSimilarDescriptorClass = new CreateSimilarDescriptorClass.Builder().withClassType(CLASS_TYPE.CLASS)
						.withToPattern(toPattern).withRelationTypeToParent(RELATION_TYPE.RELATION_TYPE_IMPLEMENTS).withProject(project)
						.withSourcePath(sourcePath).withPackge(packge).withSubPackage(subPkg).withSuperTypes(superTypes)
						.withClassInsideBody(classInsideBody).withFinalClass(finalClass)
						.withCreateDefaultConstructor(createDefaultConstructor).withCreateInstanceConstructor(createInstanceConstructor)
						.withInclInstance(includeInstaceFrom).withCreateFields(createFields).withCreateFieldsName(createFieldName)
						.withInclGetterSetterForInstance(includeGetterSetterInstaceFrom).withImplementTypes(implementInterfaces)
						.withImportTypes(classImports).withClassAnnotations(classAnnotations).withFieldAnnotations(fieldAnnotations)
						.withCreateMethodBody(createMethodBody).withMethodAnnotations(methodAnnotations).withCreateUnitTest(createUnitTest)
						.withConvertMethodParam(convertMethodParam).withConvertMethodParamFrom(convertMethodParamFrom)
						.withConvertMethodParamTo(convertMethodParamTo).build();
				//new CreateSimilarDescriptorClass(CLASS_TYPE.CLASS, toPattern, RELATION_TYPE.RELATION_TYPE_IMPLEMENTS, project,sourcePath, packge, subPkg, superTypes, null, classInsideBody, finalClass, createDefaultConstructor, createInstanceConstructor,includeInstaceFrom, false, false, createFields, createFieldName, includeGetterSetterInstaceFrom, implementInterfaces, classImports,classAnnotations, fieldAnnotations, createMethodBody, methodAnnotations, createReturnVariable, returnVariableName, createUnitTest,convertMethodParam, convertMethodParamFrom, convertMethodParamTo);
			} else if (isClass) {
				createSimilarDescriptorClass = new CreateSimilarDescriptorClass.Builder().withClassType(CLASS_TYPE.CLASS)
						.withToPattern(toPattern).withProject(project).withSourcePath(sourcePath).withPackge(packge)
						.withSuperTypes(superTypes).withClassInsideBody(classInsideBody).withFinalClass(finalClass)
						.withCreateDefaultConstructor(createDefaultConstructor).withCreateInstanceConstructor(createInstanceConstructor)
						.withInclInstance(includeInstaceFrom).withCreateEqualsHashcode(createEqualsHashcode)
						.withCreateToString(createToString).withCreateFields(createFields).withCreateFieldsName(createFieldName)
						.withInclGetterSetterForInstance(includeGetterSetterInstaceFrom).withImplementTypes(implementInterfaces)
						.withImportTypes(classImports).withClassAnnotations(classAnnotations).withFieldAnnotations(fieldAnnotations)
						.withCreateMethodBody(createMethodBody).withMethodAnnotations(methodAnnotations).withCreateUnitTest(createUnitTest)
						.withConvertMethodParam(convertMethodParam).withConvertMethodParamFrom(convertMethodParamFrom)
						.withConvertMethodParamTo(convertMethodParamTo).build();
				//new CreateSimilarDescriptorClass(CLASS_TYPE.CLASS, toPattern, null, project, sourcePath, packge, null,superTypes, null, classInsideBody, finalClass, createDefaultConstructor, createInstanceConstructor, includeInstaceFrom,createEqualsHashcode, createToString, createFields, createFieldName, includeGetterSetterInstaceFrom, implementInterfaces, classImports,classAnnotations, fieldAnnotations, createMethodBody, methodAnnotations, createReturnVariable, returnVariableName, createUnitTest,convertMethodParam, convertMethodParamFrom, convertMethodParamTo);
			} else {
				if (convertMethodParam) {
					createSimilarDescriptorClass = new CreateSimilarDescriptorClass.Builder().withClassType(CLASS_TYPE.INTERFACE)
							.withToPattern(toPattern).withProject(project).withSourcePath(sourcePath).withPackge(packge)
							.withClassInsideBody(classInsideBody).withCreateUnitTest(createUnitTest)
							.withConvertMethodParam(convertMethodParam).withConvertMethodParamFrom(convertMethodParamFrom)
							.withConvertMethodParamTo(convertMethodParamTo).build();
					//new CreateSimilarDescriptorClass(CLASS_TYPE.INTERFACE, toPattern, project, sourcePath, packge, null,classInsideBody, createUnitTest, convertMethodParam, convertMethodParamFrom, convertMethodParamTo);
				} else {
					createSimilarDescriptorClass = new CreateSimilarDescriptorClass.Builder().withClassType(CLASS_TYPE.INTERFACE)
							.withToPattern(toPattern).withProject(project).withSourcePath(sourcePath).withPackge(packge)
							.withClassInsideBody(classInsideBody).withCreateUnitTest(createUnitTest).build();
					//new CreateSimilarDescriptorClass(CLASS_TYPE.INTERFACE, toPattern, project, sourcePath, packge, null,classInsideBody, createUnitTest);
				}
			}
			createSimilarDescriptor.createSimilarDescriptorClasses[numSimilarClass] = createSimilarDescriptorClass;
			if (numSimilarClass > 0) {
				createSimilarDescriptorClass.setParentDescriptor(createSimilarDescriptor.getCreateSimilarDescriptorClasses()[0]);
			}
		}

		final int maxConfigs = ConfigPattern.getInstance().getConfigs().length;
		for (int configCount = 0; configCount < maxConfigs; configCount++) {
			final Boolean createConfig = getPreferenceStoreValueBoolean(P_CREATE_CONFIG, preferenceId, configCount);
			if (!createConfig) {
				continue;
			}
			final String configType = getPreferenceStoreValueString(P_CONFIG_TYPE, preferenceId, configCount);
			final String configLocation = getPreferenceStoreValueString(P_CONFIG_LOCATION, preferenceId, configCount);
			final String configPattern = getPreferenceStoreValueString(P_CONFIG_PATTERN, preferenceId, configCount);
			final String configFileName = getPreferenceStoreValueString(P_CONFIG_FILE_NAME, preferenceId, configCount);
			final String configFileConvType = getPreferenceStoreValueString(P_CONFIG_FILE_CONV_TYPES, preferenceId, configCount);
			final String configLocale = getPreferenceStoreValueString(P_CONFIG_LOCALE, preferenceId, configCount);
			final String configHeaderPattern = getPreferenceStoreValueString(P_CONFIG_HEADER_PATTERN, preferenceId, configCount);
			final String configStartPattern = getPreferenceStoreValueString(P_CONFIG_START_PATTERN, preferenceId, configCount);
			final String configEndPattern = getPreferenceStoreValueString(P_CONFIG_END_PATTERN, preferenceId, configCount);

			final CreateSimilarDescriptorConfig config = new CreateSimilarDescriptorConfig(configType, configFileName, configFileConvType,
					configLocation, configLocale, configHeaderPattern, configStartPattern, configPattern, configEndPattern);
			createSimilarDescriptor.addConfigPart(config);

		}

		return createSimilarDescriptor;
	}

	/**
	 * @param preferenceId
	 * @return
	 */
	private static String[] getPreferenceValuesAsArray(final String preferenceItem, final String preferenceId) {
		final String prefs = getPreferenceStoreValueString(preferenceItem, preferenceId);
		String[] fieldAnnotations = null;
		if (!isEmpty(prefs)) {
			fieldAnnotations = prefs.split(NEWLINE);
		}
		return fieldAnnotations;
	}

	/**
	 *
	 * @return
	 */
	public static boolean refreshePreferenceDataFromStore() {
		if (!updateCreateSimilarDescriptor(CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID)) {
			return false;
		}
		if (!updateCreateSimilarDescriptor(CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID)) {
			return false;
		}

		if (!updateCreateSimilarDescriptor(CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID)) {
			return false;
		}

		if (!updateCreateSimilarDescriptor(CREATE_SIMILAR_PREFERENCE_POJO_SERVICE_UI_ID)) {
			return false;
		}

		if (!updateCreateSimilarDescriptor(CREATE_NEW_PREFERENCE_DAO_ID)) {
			return false;
		}

		if (!updateCreateSimilarDescriptor(CREATE_NEW_PREFERENCE_DAO_POJO_ID)) {
			return false;
		}

		if (!updateCreateSimilarDescriptor(CREATE_NEW_PREFERENCE_SERVICE_ID)) {
			return false;
		}

		if (!updateCreateSimilarDescriptor(CREATE_NEW_PREFERENCE_SERVICE_POJO_ID)) {
			return false;
		}

		if (!updateCreateSimilarDescriptor(CREATE_NEW_PREFERENCE_UI_ID)) {
			return false;
		}

		CreateSimilarDescriptor createSimilarDescriptor = createSimilarDescriptorMap.get(CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID);
		if (getPreferenceStoreValueBoolean(P_NEXT_DESCRIPTOR, CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID)) {
			createSimilarDescriptor.setNextDescriptor(createSimilarDescriptorMap.get(CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID));
		}

		createSimilarDescriptor = createSimilarDescriptorMap.get(CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID);
		if (getPreferenceStoreValueBoolean(P_NEXT_DESCRIPTOR, CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID)) {
			createSimilarDescriptor.setNextDescriptor(createSimilarDescriptorMap.get(CREATE_SIMILAR_PREFERENCE_POJO_SERVICE_UI_ID));
		}

		return updateCreateSimilarDescriptor(CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME);
	}

	/**
	 *
	 * @param type
	 * @return
	 */
	public static CreateSimilarDescriptor getCreateSimilarDescriptor(final IType type) {
		return getCreateSimilarDescriptor(type, false);
	}

	/**
	 *
	 * @param preferenceId
	 * @return
	 */
	public static CreateSimilarDescriptor getCreateSimilarDescriptor(final String preferenceId) {
		final Map<String, CreateSimilarDescriptor> descriptorMap = getCreateSimilarDescriptorMap();

		if (descriptorMap.isEmpty()) {
			refreshePreferenceDataFromStore();
		}
		return descriptorMap.get(preferenceMap.get(preferenceId));
	}

	/**
	 *
	 * It gets the CreateSimilarDescriptor from the type. The second argument is
	 * null for most of the cases. It is only needed for create similar with
	 * different name.
	 *
	 * @param type
	 * @param preferenceId
	 *
	 * @return
	 */
	public static CreateSimilarDescriptor getCreateSimilarDescriptor(final IType type, final boolean differentName) {
		final Map<String, CreateSimilarDescriptor> descriptorMap = getCreateSimilarDescriptorMap();

		if (descriptorMap.isEmpty()) {
			refreshePreferenceDataFromStore();
		}

		for (final CreateSimilarDescriptor createSimilarDescriptor : descriptorMap.values()) {
			// final CreateSimilarDescriptor createSimilarDescriptor =
			// descriptorMap.get(key);

			if (createSimilarDescriptor == null) {
				return null;
			}
			if (differentName) {
				if (createSimilarDescriptor.isDifferentName() && isValidType(type, createSimilarDescriptor.getFromPattern())) {
					return createSimilarDescriptor;
				}
			} else {
				if (!createSimilarDescriptor.isDifferentName() && isValidType(type, createSimilarDescriptor.getFromPattern())) {
					return createSimilarDescriptor;
				}
			}
		}

		return null;
	}

	/**
	 *
	 * @param type
	 * @param pattern
	 * @return
	 */
	private static boolean isValidType(final IType type, final String pattern) {
		String path = EMPTY_STR;
		if (pattern.startsWith(FORWARD_SLASH)) {
			final IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot) type.getPackageFragment().getParent();
			path = packageFragmentRoot.getPath().toString();
			path = path.substring(type.getJavaProject().getElementName().length() + 1);
			if (!pattern.startsWith(path)) {
				path += FORWARD_SLASH + type.getJavaProject().getElementName();
				if (!pattern.startsWith(path)) {
					return false;
				}
			}
		}

		if (!isEmpty(path)) {
			path += FORWARD_SLASH;
		}

		final Pattern pattrn = Pattern.compile(pattern);
		final Matcher m = pattrn.matcher(path + type.getFullyQualifiedName());

		return m.matches();
	}

	/**
	 * @return the createSimilarDescriptorMap
	 */
	public static Map<String, CreateSimilarDescriptor> getCreateSimilarDescriptorMap() {
		return createSimilarDescriptorMap;
	}

	/**
	 * @return the workingSetName
	 */
	public String getWorkingSetName() {
		return this.workingSetName;
	}

	/**
	 * @param workingSetName
	 *            the workingSetName to set
	 */
	public void setWorkingSetName(final String workingSetName) {
		this.workingSetName = workingSetName;
	}

	/**
	 * @return the fromPattern
	 */
	public String getFromPattern() {
		return this.fromPattern;
	}

	/**
	 * @return the toPattern
	 */
	public String getToPattern() {
		return this.toPattern;
	}

	/**
	 * @return the methodPrefix
	 */
	public String getMethodPrefix() {
		return this.methodPrefix;
	}

	/**
	 * @return the createWorkingSet
	 */
	public boolean isCreateWorkingSet() {
		return this.createWorkingSet;
	}

	/**
	 * @return the createConfig
	 */
	public boolean isCreateConfig() {
		return this.createConfig;
	}

	/**
	 * @return the nextDescriptor
	 */
	public CreateSimilarDescriptor getNextDescriptor() {
		return this.nextDescriptor;
	}

	/**
	 * @param nextDescriptor
	 *            the nextDescriptor to set
	 */
	public void setNextDescriptor(final CreateSimilarDescriptor nextDescriptor) {
		this.nextDescriptor = nextDescriptor;
	}

	/**
	 *
	 * @return
	 */
	public boolean isValid() {
		// if (!isEmpty(fromPattern) && isEmpty(toPattern)) {
		if (isEmpty(this.fromPattern)) {
			return false;
		}
		return true;
	}

	/**
	 *
	 * @return
	 */
	public String getIncludePattern() {
		return this.includePattern;
	}

	/**
	 *
	 * @return
	 */
	public String getExcludePattern() {
		return this.excludePattern;
	}

	/**
	 *
	 * @param i
	 * @return
	 */
	public CreateSimilarDescriptorConfig getConfigPart(final int i) {
		return i < this.descriptorConfigParts.length ? this.descriptorConfigParts[i] : null;
	}

	/**
	 *
	 * @param config
	 */
	public void addConfigPart(final CreateSimilarDescriptorConfig config) {
		for (int i = 0; i < this.descriptorConfigParts.length; i++) {
			if (this.descriptorConfigParts[i] == null) {
				this.descriptorConfigParts[i] = config;
				break;
			}
		}
	}

	/**
	 *
	 * @param i
	 */
	public void removeConfigPart(final int i) {
		if (i < this.descriptorConfigParts.length && this.descriptorConfigParts[i] != null) {
			this.descriptorConfigParts[i] = null;
		}
	}

	/**
	 * @return the descriptorConfigParts
	 */
	public CreateSimilarDescriptorConfig[] getDescriptorConfigParts() {
		return this.descriptorConfigParts;
	}

	/**
	 * @return the differentName
	 */
	public boolean isDifferentName() {
		return this.differentName;
	}

	/**
	 * @return the createSimilarDescriptorClasses
	 */
	public CreateSimilarDescriptorClass[] getCreateSimilarDescriptorClasses() {
		return this.createSimilarDescriptorClasses;
	}

	/**
	 * @return the replaceName
	 */
	public boolean isReplaceName() {
		return this.replaceName;
	}

	/**
	 * @return the copyField
	 */
	public boolean isCopyField() {
		return this.copyField;
	}

	/**
	 * @return the copyMethod
	 */
	public boolean isCopyMethod() {
		return this.copyMethod;
	}

	/**
	 * @return the breakDateFields
	 */
	public boolean isBreakDateFields() {
		return this.breakDateFields;
	}

	/**
	 * @return the returnType
	 */
	public RETURN_TYPE getReturnType() {
		return this.returnType;
	}

	/**
	 *
	 * @return
	 */
	public String getMethodNamePattern() {
		return this.methodNamePattern;
	}

	public CreateSimilarDescriptorClass[] getCreateSimilarDescUserChoice() {
		return this.createSimilarDescUserChoice;
	}

	public void setCreateSimilarDescUserChoice(final CreateSimilarDescriptorClass[] createSimilarDescUserChoice) {
		this.createSimilarDescUserChoice = createSimilarDescUserChoice;
	}

	public int getNoOfInputs() {
		return this.noOfInputs;
	}

	public void setNoOfInputs(final int noOfInputs) {
		this.noOfInputs = noOfInputs;
	}

	private CreateSimilarDescriptor(final Builder builder) {

		this.fromPattern = builder.fromPattern;

		this.toPattern = builder.toPattern;

		this.copyField = builder.copyField;

		this.copyMethod = builder.copyMethod;

		this.breakDateFields = builder.breakDateFields;

		this.differentName = builder.differentName;

		this.createWorkingSet = builder.createWorkingSet;

		this.workingSetName = builder.workingSetName;

		this.createConfig = builder.createConfig;

		this.replaceName = builder.replaceName;

		this.methodPrefix = builder.methodPrefix;

		this.replacePart = builder.replacePart;

		this.replaceValue = builder.replaceValue;

		this.includePattern = builder.includePattern;

		this.excludePattern = builder.excludePattern;

		this.methodNamePattern = builder.methodNamePattern;

		this.returnType = builder.returnType;

		this.numConfigs = builder.numConfigs;

		this.createSimilarDescriptorClasses = builder.createSimilarDescriptorClasses;

		this.descriptorConfigParts = builder.descriptorConfigParts;

		this.nextDescriptor = builder.nextDescriptor;

	}

	public static class Builder {

		private String							fromPattern;

		private String							toPattern;

		private boolean							copyField;

		private boolean							copyMethod;

		private boolean							breakDateFields;

		private boolean							differentName;

		private boolean							createWorkingSet;

		private String							workingSetName;

		private boolean							createConfig;

		private boolean							replaceName;

		private String							methodPrefix;

		private String							replacePart;

		private String							replaceValue;

		private String							includePattern;

		private String							excludePattern;

		private String							methodNamePattern;

		private RETURN_TYPE						returnType;

		private int								numConfigs;

		private CreateSimilarDescriptorClass[]	createSimilarDescriptorClasses;

		private CreateSimilarDescriptorConfig[]	descriptorConfigParts	= new CreateSimilarDescriptorConfig[NUM_MAX_CONFIGS];
		private CreateSimilarDescriptor			nextDescriptor;

		public Builder withFromPattern(final String fromPattern) {

			this.fromPattern = fromPattern;

			return this;

		}

		public Builder withToPattern(final String toPattern) {

			this.toPattern = toPattern;

			return this;

		}

		public Builder withCopyField(final boolean copyField) {

			this.copyField = copyField;

			return this;

		}

		public Builder withCopyMethod(final boolean copyMethod) {

			this.copyMethod = copyMethod;

			return this;

		}

		public Builder withBreakDateFields(final boolean breakDateFields) {

			this.breakDateFields = breakDateFields;

			return this;

		}

		public Builder withDifferentName(final boolean differentName) {

			this.differentName = differentName;

			return this;

		}

		public Builder withCreateWorkingSet(final boolean createWorkingSet) {

			this.createWorkingSet = createWorkingSet;

			return this;

		}

		public Builder withWorkingSetName(final String workingSetName) {

			this.workingSetName = workingSetName;

			return this;

		}

		public Builder withCreateConfig(final boolean createConfig) {

			this.createConfig = createConfig;

			return this;

		}

		public Builder withReplaceName(final boolean replaceName) {

			this.replaceName = replaceName;

			return this;

		}

		public Builder withMethodPrefix(final String methodPrefix) {

			this.methodPrefix = methodPrefix;

			return this;

		}

		public Builder withReplacePart(final String replacePart) {

			this.replacePart = replacePart;

			return this;

		}

		public Builder withReplaceValue(final String replaceValue) {

			this.replaceValue = replaceValue;

			return this;

		}

		public Builder withIncludePattern(final String includePattern) {

			this.includePattern = includePattern;

			return this;

		}

		public Builder withExcludePattern(final String excludePattern) {

			this.excludePattern = excludePattern;

			return this;

		}

		public Builder withMethodNamePattern(final String methodNamePattern) {

			this.methodNamePattern = methodNamePattern;

			return this;

		}

		public Builder withReturnType(final RETURN_TYPE returnType) {

			this.returnType = returnType;

			return this;

		}

		public Builder withNumConfigs(final int numConfigs) {

			this.numConfigs = numConfigs;

			return this;

		}

		public Builder withCreateSimilarDescriptorClasses(final CreateSimilarDescriptorClass[] createSimilarDescriptorClasses) {

			this.createSimilarDescriptorClasses = createSimilarDescriptorClasses;

			return this;

		}

		public Builder withDescriptorConfigParts(final CreateSimilarDescriptorConfig[] descriptorConfigParts) {

			this.descriptorConfigParts = descriptorConfigParts;

			return this;

		}

		public Builder withNextDescriptor(final CreateSimilarDescriptor nextDescriptor) {

			this.nextDescriptor = nextDescriptor;

			return this;

		}

		public CreateSimilarDescriptor build() {

			return new CreateSimilarDescriptor(this);

		}

	}

}

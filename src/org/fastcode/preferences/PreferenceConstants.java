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

package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.GLOBAL_ADD_TYPE_END;
import static org.fastcode.common.FastCodeConstants.GLOBAL_ALWAYS_CREATE;
import static org.fastcode.common.FastCodeConstants.GLOBAL_ASK_TO_CREATE;
import static org.fastcode.common.FastCodeConstants.GLOBAL_EXTENDS;
import static org.fastcode.common.FastCodeConstants.GLOBAL_IMPLEMENTS;
import static org.fastcode.common.FastCodeConstants.GLOBAL_MAKE_PLURAL;
import static org.fastcode.common.FastCodeConstants.GLOBAL_MAKE_PLURAL_ADD_TYPE_END;
import static org.fastcode.common.FastCodeConstants.GLOBAL_NEVER_CREATE;
import static org.fastcode.common.FastCodeConstants.GLOBAL_NO_RELATION;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;

import org.fastcode.common.FastCodeConstants.CLASS_TYPE;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

	public static final String		CREATE_SIMILAR_ACTION_DAO_SERVICE_ID							= "FastCode.CreateSimilarActionDaoService";

	public static final String		CREATE_SIMILAR_ACTION_SERVICE_UI_ID								= "FastCode.CreateSimilarActionServiceUI";

	public static final String		CREATE_SIMILAR_ACTION_WITH_DIFFERENT_NAME						= "FastCode.CreateSimilarActionWithDifferentName";

	public static final String		CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID						= "FastCode.preferences.CreateSimilarPreferenceDaoService";

	public static final String		CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID					= "FastCode.preferences.CreateSimilarPreferencePojoDaoService";

	public static final String		CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID							= "FastCode.preferences.CreateSimilarPreferenceServiceUI";

	public static final String		CREATE_SIMILAR_PREFERENCE_POJO_SERVICE_UI_ID					= "FastCode.preferences.CreateSimilarPreferencePojoServiceUI";

	public static final String		CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME						= "FastCode.preferences.CreateSimilarPreferenceDifferentName";

	public static final String		CREATE_NEW_PREFERENCE_DAO_ID									= "FastCode.preferences.CreateNewPreferenceDAO";

	public static final String		CREATE_NEW_PREFERENCE_DAO_POJO_ID								= "FastCode.preferences.CreateNewPreferencePojoDAO";

	public static final String		CREATE_NEW_PREFERENCE_SERVICE_ID								= "FastCode.preferences.CreateNewPreferenceService";

	public static final String		CREATE_NEW_PREFERENCE_SERVICE_POJO_ID							= "FastCode.preferences.CreateNewPreferencePojoService";

	public static final String		CREATE_NEW_PREFERENCE_UI_ID										= "FastCode.preferences.CreateNewPreferenceUI";

	public static final String		CREATE_NEW_PREFERENCE_UI_POJO_ID								= "FastCode.preferences.CreateNewPreferencePojoUI";

	public static final String		P_GLOBAL_PACKAGE_PATTERN										= "GLOBAL PACKAGE PATTERN";

	public static final String		P_GLOBAL_CLASS_NAME_PATTERN										= "GLOBAL CLASS NAME PATTERN";

	public static final String		P_GLOBAL_IMPL_SUFFIX											= "GLOBAL IMPL SUFFIX";

	public static final String		P_GLOBAL_IMPL_SUB_PACKAGE										= "GLOBAL IMPL SUB PACKAGE";

	public static final String		P_GLOBAL_SOURCE_PATH_JAVA										= "GLOBAL SOURCE PATH JAVA";

	public static final String		P_GLOBAL_SOURCE_PATH_TEST										= "GLOBAL SOURCE PATH TEST";

	public static final String		P_GLOBAL_SOURCE_PATH_RESOURCES									= "GLOBAL SOURCE PATH RESOURCES";

	public static final String		P_GLOBAL_USE_DEFAULT_FOR_PATH									= "GLOBAL USE DEFAULT FOR PATH";

	public static final String		P_GLOBAL_FINAL_MODIFIER_FOR_METHOD_ARGS							= "GLOBAL FINAL MODIFIER FOR METHOD ARGS";

	public static final String		P_GLOBAL_RELATED_CLASS											= "GLOBAL RELATED CLASS";

	//public static final String		P_GLOBAL_GETTER_SETTER_FOR_PRIVATE								= "GLOBAL GETTER SETTER FOR PRIVATE";

	public static final String		P_ASK_FOR_PARAMETERIZED_TYPE									= "GLOBAL ASK FOR PARAMETERIIZED TYPE";

	public static final String		P_ASK_FOR_STATIC_IMPORT											= "GLOBAL ASK FOR STATIC IMPORT";

	public static final String		P_ASK_FOR_COPY_METHOD_BODY										= "GLOBAL ASK FOR COPY METHOD BODY";

	public static final String		P_ASK_FOR_CONFIGURE_NOW											= "GLOBAL ASK FOR CONFIGURE NOW";

	public static final String		P_ASK_FOR_CONTINUE												= "GLOBAL ASK FOR CONTINUE";

	public static final String		P_GLOBAL_PARAMETERIZED_TYPE_CHOICE								= "GLOBAL PARAMETERIIZED TYPE CHOICE";

	public static final String		P_GLOBAL_PARAMETERIZED_NAME_STRATEGY							= "GLOBAL PARAMETERIIZED NAME STRATEGY";

	public static final String		P_GLOBAL_PARAMETERIZED_IGNORE_EXTENSIONS						= "GLOBAL PARAMETERIIZED IGNORE EXTENSIONS";

	public static final String		P_GLOBAL_TYPES_MAP												= "GLOBAL TYPES MAP";

	public static final String		P_GLOBAL_ANNOTATIONS_VALUES_MAP									= "GLOBAL ANNOTATIONS VALUES MAP";

	public static final String		P_GLOBAL_STATIC_IMPORT_TYPE_CHOICE								= "GLOBAL STATIC IMPORT CHOICE";

	public static final String		P_GLOBAL_COPY_METHOD_BODY										= "GLOBAL COPY METHOD BODY";

	public static final String		P_GLOBAL_PLACE_HOLDER_VALUES									= "GLOBAL PLACE HOLDER VALUES";

	public static final String		P_GLOBAL_ALWAYS_CREATE_PARAMETERIZED_TYPE						= "GLOBAL ALWAYS CREATE PARAMETERIZED TYPE";

	public static final String		P_GLOBAL_ALWAYS_CREATE_NON_PARAMETERIZED_TYPE					= "GLOBAL ALWAYS CREATE NON PARAMETERIZED TYPE";

	public static final String		P_GLOBAL_ASK_CREATE_PARAMETERIZED_TYPE							= "GLOBAL ASK CREATE PARAMETERIZED TYPE";

	public static final String		P_GLOBAL_ASK_GETTER_SETTER_ALWAYS								= "GLOBAL ASK GETTER SETTER ALWAYS";

	public static final String		P_GLOBAL_USER													= "GLOBAL USER";

	public static final String		P_GLOBAL_PROPS													= "GLOBAL_PROPS";

	public static final String		P_GLOBAL_DATE_FORMAT											= "GLOBAL DATE FORMAT";

	public static final String		P_GLOBAL_EQUALS_METHOD_BODY										= "GLOBAL EQUALS METHOD BODY";

	public static final String		P_GLOBAL_HASHCODE_METHOD_BODY									= "GLOBAL HASHCODE METHOD BODY";

	public static final String		P_GLOBAL_TOSTRING_METHOD_BODY									= "GLOBAL TOSTRING METHOD BODY";

	public static final String		P_AUTO_SAVE														= "AUTO SAVE";

	public static final String		P_SHOW_TIPS														= "SHOW TIPS";

	public static final String		P_BASE_TEST														= "Junit Base Type";

	public static final String		P_JUNIT_TYPE													= "Junit Type";

	public static final String		P_JUNIT_TEST_PROFILE											= "Junit Test Profile";

	public static final String		P_JUNIT_ALL_TEST_PROFILES										= "Junit All Test Profiles";

	public static final String		P_JUNIT_TEST_CLASS												= "Junit Test Class";

	public static final String		P_JUNIT_TEST_METHOD												= "Junit Test Method";

	public static final String		P_JUNIT_TYPE_3													= "Junit Type 3";

	public static final String		P_JUNIT_TYPE_4													= "Junit Type 4";

	public static final String		P_JUNIT_TYPE_TESTNG												= "Junit Type TESTNG";

	public static final String		P_JUNIT_TYPE_CUSTOM												= "Junit Type Custom";

	public static final String		P_TEST_DIR														= "testDirectory";

	public static final String		P_JUNIT_TEST_FORMAT												= "junitTestFormat";

	public static final String		P_JUNIT_TEST_ASK_FOR_ANOTHER_TEST								= "junitTestAskForAnotherTest";

	public static final String		P_JUNIT_TEST_ASK_FOR_METHOD_BODY								= "junitTestAskForMethodBody";

	public static final String		P_JUNIT_CREATE_METHOD_BODY										= "junitCreateMethodBody";

	public static final String		P_JUNIT_ALWAYS_CREATE_TRY_CATCH									= "junitAlwaysCreateTryCatch";

	public static final String		P_JUNIT_ALWAYS_CREATE_INSTANCE									= "junitAlwaysCreateInstance";

	public static final String		P_JUNIT_SHOW_ALL_PATHS											= "junitShowAllPaths";

	public static final String		P_JUNIT_CREATE_NEW_PROF											= "junitCreateNewProf";

	public static final String		P_JUNIT_DELETE_CURR_PROF										= "junitDeleteCurrProf";

	public static final String		P_JUNIT_PROFILE_NAME											= "junitProfileName";

	public static final String		P_JUNIT_PROFILE_PATTERN											= "junitProfilePattern";

	public static final String		P_JUNIT_EXCEPTION_BODY											= "junitExceptionBody";

	public static final String		P_JUNIT_NEGATIVE_BODY											= "junitNegativeBody";

	public static final String		P_JUNIT_METHOD_ANNOTATIONS										= "junitMethodAnnotations";

	public static final String		P_JUNIT_METHOD_COMMENT											= "junitMethodComment";

	public static final String		P_JUNIT_CLASS_ANNOTATIONS										= "junitClassAnnotations";

	public static final String		P_JUNIT_CLASS_IMPORTS											= "junitClassImports";

	public static final String		P_JUNIT_CLASS_INSIDE_BODY										= "junitClassInsideBody";

	public static final String		P_JUNIT_TEST_LOCATION											= "junitTestLocation";

	public static final String		P_JUNIT_TEST_PROJECT											= "junitTestProject";

	public static final String		P_JUNIT_OVERWRITE												= "junitOverwrite";

	public static final String		P_PROJECTS														= "PROJECTS";

	public static final String		CREATE_SIMILAR_DAO_SERVICE										= "CreateSimilarDaoService";

	public static final String		CREATE_SIMILAR_POJO_DAO_SERVICE									= "CreateSimilarPojoDaoService";

	public static final String		CREATE_SIMILAR_SERVICE_UI										= "CreateSimilarServiceUI";

	public static final String		CREATE_SIMILAR_POJO_SERVICE_UI									= "CreateSimilarPojoServiceUI";

	public static final String		CREATE_SIMILAR_DIFFERENT_NAME									= "CreateSimilarDifferentName";

	public static final String		CREATE_SIMILAR													= "CreateSimilar";

	public static final String		CREATE_NEW														= "CreateNew";

	public static final String		CREATE_NEW_DAO													= "CreateNewDao";

	public static final String		CREATE_NEW_DAO_POJO												= "CreateNewDaoPojo";

	public static final String		CREATE_NEW_SERVICE												= "CreateNewService";

	public static final String		CREATE_NEW_SERVICE_POJO											= "CreateNewServicePojo";

	public static final String		CREATE_NEW_UI													= "CreateNewUI";

	public static final String		CREATE_NEW_UI_POJO												= "CreateNewUIPojo";

	public static final String		P_PACKAGE														= CREATE_SIMILAR + "PACKAGE";

	public static final String		P_FROM_PATTERN													= CREATE_SIMILAR + "FROM_PATTERN";

	public static final String		P_SOURCE_PATH													= CREATE_SIMILAR + "SOURCE PATH";

	public static final String		P_TO_PATTERN													= CREATE_SIMILAR + "TO_PATTERN";

	public static final String		P_CLASS_BODY_PATTERN											= CREATE_SIMILAR + "CLASS BODY PATTERN";

	public static final String		P_CLASS_HEADER													= CREATE_SIMILAR
																											+ "CLASS HEADER PATTERN";

	public static final String		P_GLOBAL_CLASS_HEADER											= "GLOBAL CLASS BODY HEADER";

	public static final String		P_GLOBAL_CLASS_BODY												= "GLOBAL CLASS BODY";

	public static final String		P_GLOBAL_CLASS_METHOD_BODY										= "GLOBAL CLASS METHOD BODY";

	public static final String		P_GLOBAL_CLASS_FIELD_BODY										= "GLOBAL CLASS FIELD BODY";

	public static final String		P_GLOBAL_CONSTRUCTOR_BODY										= "GLOBAL CONSTRUCTOR BODY";

	public static final String		P_GLOBAL_INTERFACE_METHOD_BODY									= "GLOBAL INTERFACE METHOD BODY";

	public static final String		P_CLASS_INSIDE_BODY												= CREATE_SIMILAR + "CLASS INSIDE BODY";

	public static final String		P_CLASS_TYPE													= CREATE_SIMILAR + "CLASS TYPE";

	public static final String		P_CODE_BEFORE_CLASS_BODY										= CREATE_SIMILAR
																											+ "CODE BEFORE CLASS BODY";

	public static final String		P_CODE_WITHIN_CLASS_BODY										= CREATE_SIMILAR
																											+ "CODE WITHIN CLASS BODY";

	public static final String		P_IMPLEMENT_INT													= CREATE_SIMILAR + "IMPLEMENT INT";

	public static final String		P_CREATE_RELATED_CLASS											= CREATE_SIMILAR
																											+ "CREATE RELATED CLASS";

	public static final String		P_NUM_SIMILAR_CLASSES											= CREATE_SIMILAR
																											+ "NUM SIMILAR CLASSES";

	public static final String		P_IMPL_SUB_PACKAGE												= CREATE_SIMILAR
																											+ "IMPLEMENT SUB PACKAGE";

	public static final String		P_FINAL_CLASS													= CREATE_SIMILAR + "FINAL CLASS";

	public static final String		P_IMPLEMENT_SUPER_CLASS											= CREATE_SIMILAR
																											+ "IMPLEMENT SUPER CLASS";

	public static final String		P_IMPLEMENT_INTERFACES											= CREATE_SIMILAR
																											+ "IMPLEMENT INTERFACES";

	public static final String		P_DIFFERENT_NAME												= CREATE_SIMILAR + "DIFFERENT NAME";

	public static final String		P_CLASS_IMPORTS													= CREATE_SIMILAR + "CLASS IMPORTS";

	public static final String		P_SUPER_CLASS													= CREATE_SIMILAR + "SUPER CLASS";

	public static final String		P_INCLUDE_INSTACE_FROM											= CREATE_SIMILAR
																											+ "INCLUDE INSTACE FROM";

	public static final String		P_RELATION_CHOICE_TYPE											= CREATE_SIMILAR
																											+ "RELATION CHOICE TYPE";

	public static final String		P_CREATE_DEFAULT_CONSTRUCTOR									= CREATE_SIMILAR
																											+ "CREATE DEFAULT CONSTRUCTOR";

	public static final String		P_CREATE_INSTANCE_CONSTRUCTOR									= CREATE_SIMILAR
																											+ "CREATE INSTANCE CONSTRUCTOR";

	public static final String		P_INCLUDE_GETTER_SETTER_INSTACE_FROM							= CREATE_SIMILAR
																											+ "INCLUDE GETTER SETTER INSTACE FROM";

	public static final String		P_CREATE_FIELDS													= CREATE_SIMILAR + "CREATE FIELDS";

	public static final String		P_CREATE_FIELDS_NAME											= CREATE_SIMILAR + "CREATE FIELDS NAME";

	public static final String		P_CONVERT_METHOD_PARAM											= CREATE_SIMILAR
																											+ "CONVERT METHOD PARAM";

	public static final String		P_CONVERT_METHOD_PARAM_FROM										= CREATE_SIMILAR
																											+ "CONVERT METHOD PARAM FROM";

	public static final String		P_GLOBAL_CONVERT_METHOD_PARAM_PATTERN							= "CONVERT METHOD PARAM PATTERN";

	public static final String		P_CONVERT_METHOD_PARAM_TO										= CREATE_SIMILAR
																											+ "CONVERT METHOD PARAM TO";

	public static final String		P_COPY_METHODS													= CREATE_SIMILAR + "COPY METHODS";

	public static final String		P_CREATE_METHOD_BODY											= CREATE_SIMILAR + "CREATE METHOD BODY";

	public static final String		P_METHOD_ANNOTATIONS											= CREATE_SIMILAR
																											+ "METHODS ANNOTATIONS";

	public static final String		P_FIELD_ANNOTATIONS												= CREATE_SIMILAR + "FIELD ANNOTATIONS";

	public static final String		P_CLASS_ANNOTATIONS												= CREATE_SIMILAR + "CLASS ANNOTATIONS";

	public static final String		P_CREATE_EQUALS_HASHCODE										= CREATE_SIMILAR + "EQUALS HASHCODE";

	public static final String		P_CREATE_TO_STRING												= CREATE_SIMILAR + "TO STRING";

	public static final String		P_COPY_FIELDS													= CREATE_SIMILAR + "COPY FIELDS";

	public static final String		P_INCLUDE_PATTERN												= CREATE_SIMILAR + "INCLUDE PATTERN";

	public static final String		P_EXCLUDE_PATTERN												= CREATE_SIMILAR + "EXCLUDE PATTERN";

	public static final String		P_BREAK_DATE_FIELDS												= CREATE_SIMILAR + "BREAK DATE FIELDS";

	public static final String		P_CREATE_WORKING_SET											= CREATE_SIMILAR + "CREATE_WORKING_SET";

	public static final String		P_WORKING_SET_NAME												= CREATE_SIMILAR + "WORKING_SET_NAME";

	public static final String		P_CREATE_UNIT_TEST												= CREATE_SIMILAR + "CREATE JUNIT TEST";

	/*public static final String		P_RETURN_VARIABLE											= CREATE_SIMILAR + "RETURN VARIABLE";

	public static final String		P_RETURN_VARIABLE_NAME										= CREATE_SIMILAR + "RETURN VARIABLE NAME";
	*/
	public static final String		P_PROJECT														= CREATE_SIMILAR + "PROJECT";

	public static final String		P_NUM_CONFIG													= CREATE_SIMILAR + "NUM CONFIG";

	public static final String		P_CONFIG_ITEMS													= CREATE_SIMILAR + "CONFIG ITEMS";

	public static final String		P_RESTORE_CONFIG_ITEMS											= CREATE_SIMILAR
																											+ "RESTORE CONFIG ITEMS";

	public static final String		P_CREATE_CONFIG													= CREATE_SIMILAR + "CREATE CONFIG";

	public static final String		P_CONFIG_TYPE													= CREATE_SIMILAR + "P CONFIG TYPE";

	public static final String		P_CONFIG_FILE_CONV_TYPES										= CREATE_SIMILAR
																											+ "P CONFIG FILE CONV TYPES";

	public static final String		P_CONFIG_TYPE_SPRING											= CREATE_SIMILAR
																											+ "P CONFIG TYPE SPRING";

	public static final String		P_CONFIG_TYPE_DOZER												= CREATE_SIMILAR
																											+ "P CONFIG TYPE DOZER";

	public static final String		P_CONFIG_TYPE_TILES												= CREATE_SIMILAR
																											+ "P CONFIG TYPE TILES";

	public static final String		P_CONFIG_TYPE_STRUTS											= CREATE_SIMILAR
																											+ "P CONFIG TYPE STRUTS";

	public static final String		P_CONFIG_TYPE_VALIDATOR											= CREATE_SIMILAR
																											+ "P CONFIG TYPE VALIDATOR";

	public static final String		P_CONFIG_TYPE_RESOURCES											= CREATE_SIMILAR
																											+ "P CONFIG TYPE RESOURCES";

	public static final String		P_CONFIG_TYPE_CUSTOM											= CREATE_SIMILAR
																											+ "P CONFIG TYPE CUSTOM";

	public static final String		P_CONFIG_LOCATION												= CREATE_SIMILAR + "P CONFIG LOCATION";

	public static final String		P_CONFIG_FILE_NAME												= CREATE_SIMILAR + "P CONFIG FILE NAME";

	public static final String		P_CONFIG_LOCALE													= CREATE_SIMILAR + "P CONFIG LOCALE";

	public static final String		P_CONFIG_PATTERN												= CREATE_SIMILAR + "P CONFIG PATTERN";

	public static final String		P_REMOVE_CONFIG													= CREATE_SIMILAR + "P REMOVE CONFIG";

	public static final String		P_CONFIG_START_PATTERN											= CREATE_SIMILAR
																											+ "P CONFIG START PATTERN";

	public static final String		P_CONFIG_HEADER_PATTERN											= CREATE_SIMILAR
																											+ "P CONFIG HEADER PATTERN";

	public static final String		P_CONFIG_END_PATTERN											= CREATE_SIMILAR
																											+ "P CONFIG END PATTERN";

	public static final String		P_NEXT_DESCRIPTOR												= CREATE_SIMILAR + "P NEXT DESCRIPTOR";

	public static final String		TEMPLATE														= "TEMPLATE";

	public static final String		P_TEMPLATE_VARIATION											= "VARIATION";

	public static final String		P_TEMPLATE_ENABLE_TEMPLATE										= "ENABLE_TEMPLATE";

	public static final String		P_TEMPLATE_VARIATION_FIELD										= "VARIATION_FIELD";

	public static final String		P_TEMPLATE_DESCRIPTION											= "DESCRIPTION";

	public static final String		P_TEMPLATE_GETTER_SETTER										= "GETTER_SETTER";

	public static final String		P_TEMPLATE_ITEM_PATTERN											= "ITEM_PATTERN";

	public static final String		P_TEMPLATE_ALLOW_MULTIPLE_VARIATION								= "ALLOW_MULTIPLE_VARIATION";

	public static final String		P_TEMPLATE_ALLOWED_FILE_NAMES									= "ALLOWED_FILE_NAMES";

	public static final String		P_TEMPLATE_NUMBER_REQUIRED_ITEMS								= "NUMBER_REQUIRED_ITEMS";

	public static final String		P_TEMPLATE_BODY													= "TEMPLATE_BODY";

	public static final String		P_TEMPLATE_FILE													= "TEMPLATE_FILE";

	public static final String		P_ALL_TEMPLATES													= "ALL_TEMPLATES";

	public static final String		P_DATABASE_ALL_TEMPLATES										= "ALL_DATABASE_TEMPLATES";

	public static final String		P_ALL_FASTCODE_PROPERTIES										= "ALL_FASTCODE_PROPERTIES";

	public static final String		P_HQL_NAMED_QUERY_FILE_LOCATION									= "HQL_NAMED_QUERY_FILE_LOCATION";

	public static final String		P_ALL_COMMON_TEMPLATES											= "ALL_COMMON_TEMPLATES";

	public static final String		TEMPLATE_TYPE_CREATE_NEW_INSTANCE								= TEMPLATE + UNDERSCORE
																											+ "INSTANCE_OF_CLASS";

	public static final String		TEMPLATE_TYPE_CREATE_NEW_INSTANCE_JSON							= TEMPLATE + UNDERSCORE
																											+ "INSTANCE_OF_CLASS_JSON";

	public static final String		TEMPLATE_TYPE_CREATE_NEW_INSTANCE_BARE							= TEMPLATE + UNDERSCORE
																											+ "INSTANCE_OF_CLASS_BARE";

	public static final String		TEMPLATE_TYPE_CREATE_NEW_DOZER_MAPPING							= TEMPLATE + UNDERSCORE
																											+ "DOZER_MAPPING";

	public static final String		TEMPLATE_TYPE_CREATE_NEW_DOZER_MAPPING_SINGLE					= TEMPLATE + UNDERSCORE
																											+ "DOZER_MAPPING_SINGLE";

	public static final String		TEMPLATE_TYPE_CREATE_SIMPLE_CLASS_SNIPPET						= TEMPLATE + UNDERSCORE
																											+ "SIMPLE_CLASS_SNIPPET";

	public static final String		TEMPLATE_TYPE_CREATE_SIMPLE_FILE_SNIPPET						= TEMPLATE + UNDERSCORE
																											+ "SIMPLE_FILE_SNIPPET";

	public static final String		TEMPLATE_TYPE_CREATE_NEW_SPRING_BEAN							= TEMPLATE + UNDERSCORE + "SPRING_BEAN";

	public static final String		TEMPLATE_TYPE_CREATE_NEW_TOSTRING								= TEMPLATE + UNDERSCORE
																											+ "TOSTRING_METHOD";

	public static final String		TEMPLATE_TYPE_CREATE_HQL_SELECT									= TEMPLATE + UNDERSCORE + "HQL_SELECT";

	public static final String		TEMPLATE_TYPE_CREATE_HQL_SELECT_WITH_NAMED_QUERY				= TEMPLATE + UNDERSCORE
																											+ "HQL_SELECT_WITH_NAMED_QUERY";

	public static final String		TEMPLATE_TYPE_CREATE_HQL_SELECT_NAMED_QUERY_WITH_ANNOTATION		= TEMPLATE
																											+ UNDERSCORE
																											+ "HQL_SELECT_NAMED_QUERY_WITH_ANNOTATION";

	public static final String		TEMPLATE_TYPE_CREATE_HQL_UPDATE									= TEMPLATE + UNDERSCORE + "HQL_UPDATE";

	public static final String		TEMPLATE_TYPE_CREATE_HQL_UPDATE_WITH_NAMED_QUERY				= TEMPLATE + UNDERSCORE
																											+ "HQL_UPDATE_WITH_NAMED_QUERY";

	public static final String		TEMPLATE_TYPE_CREATE_HQL_UPDATE_NAMED_QUERY_WITH_ANNOTATION		= TEMPLATE
																											+ UNDERSCORE
																											+ "HQL_UPDATE_NAMED_QUERY_WITH_ANNOTATION";

	public static final String		TEMPLATE_TYPE_CREATE_HQL_DELETE									= TEMPLATE + UNDERSCORE + "HQL_DELETE";

	public static final String		TEMPLATE_TYPE_CREATE_HQL_DELETE_WITH_NAMED_QUERY				= TEMPLATE + UNDERSCORE
																											+ "HQL_DELETE_WITH_NAMED_QUERY";

	public static final String		TEMPLATE_TYPE_CREATE_HQL_DELETE_NAMED_QUERY_WITH_ANNOTATION		= TEMPLATE
																											+ UNDERSCORE
																											+ "HQL_DELETE_NAMED_QUERY_WITH_ANNOTATION";

	public static final String		TEMPLATE_TYPE_CREATE_NEW_EQUALS_HASHCODE						= TEMPLATE + UNDERSCORE
																											+ "EQUALS_HASHCODE_METHOD";

	public static final String		TEMPLATE_TYPE_CREATE_JSF_MANAGED_BEAN							= TEMPLATE + UNDERSCORE
																											+ "JSF_MANAGED_BEAN";

	public static final String		TEMPLATE_TYPE_CREATE_STRUTS_VALIDATION							= TEMPLATE + UNDERSCORE
																											+ "STRUTS_VALIDATION";

	public static final String		TEMPLATE_TYPE_CREATE_STRUTS_VALIDATION_SINGLE					= TEMPLATE + UNDERSCORE
																											+ "STRUTS_VALIDATION_SINGLE";

	public static final String		TEMPLATE_TYPE_PRINT_CLASS										= TEMPLATE + UNDERSCORE
																											+ "PRINT_FIELDS_OF_CLASS";

	public static final String		TEMPLATE_TYPE_PRINT_CLASS_JSP									= TEMPLATE + UNDERSCORE
																											+ "PRINT_FIELDS_OF_CLASS_JSP";

	public static final String		TEMPLATE_TYPE_PRINT_CLASS_JSP_SINGLE							= TEMPLATE
																											+ UNDERSCORE
																											+ "PRINT_FIELDS_OF_CLASS_JSP_SINGLE";

	public static final String		TEMPLATE_TYPE_COPY_CLASS										= TEMPLATE + UNDERSCORE
																											+ "COPY_CLASS_AS_METHOD";

	public static final String		TEMPLATE_TYPE_COPY_CLASS_SINGLE									= TEMPLATE + UNDERSCORE
																											+ "COPY_CLASS_SINGLE";

	public static final String		TEMPLATE_TYPE_RESOURCE_BUNDLE									= TEMPLATE + UNDERSCORE
																											+ "RESOURCE_BUNDLE";

	public static final String		TEMPLATE_TYPE_PRINT_RESOURCE_BUNDLE								= TEMPLATE + UNDERSCORE
																											+ "PRINT_RESOURCE_BUNDLE";

	public static final String[][]	CONFIG_TYPES													= { { "Spring", P_CONFIG_TYPE_SPRING },
			{ "Dozer", P_CONFIG_TYPE_DOZER }, { "Tiles", P_CONFIG_TYPE_TILES }, { "Struts", P_CONFIG_TYPE_STRUTS },
			{ "Validator", P_CONFIG_TYPE_VALIDATOR }, { "Resources", P_CONFIG_TYPE_RESOURCES }, { "Custom", P_CONFIG_TYPE_CUSTOM } };

	public static final String[][]	PARAMETERIZED_CHOICE_TYPES										= {
			{ "Always Create Parameterized Type", GLOBAL_ALWAYS_CREATE }, { "Ask To Create Parameterized Type", GLOBAL_ASK_TO_CREATE },
			{ "Never Create Parameterized Type", GLOBAL_NEVER_CREATE }								};

	public static final String[][]	CLASS_RELATION_CHOICE_TYPES										= {
			{ "Extends above class/interface", GLOBAL_EXTENDS }, { "Implements above interface", GLOBAL_IMPLEMENTS },
			{ "No Relation to above", GLOBAL_NO_RELATION }											};

	public static final String[][]	CLASS_CHOICE_TYPES												= {
			{ CLASS_TYPE.INTERFACE.value(), CLASS_TYPE.INTERFACE.value() }, { CLASS_TYPE.CLASS.value(), CLASS_TYPE.CLASS.value() } };

	public static final String[][]	PARAMETERIZED_NAME_CHOICE_TYPES									= {
			{ "Make It Plural", GLOBAL_MAKE_PLURAL }, { "Add Type to the End (fooList, fooMap, etc)", GLOBAL_ADD_TYPE_END },
			{ "Make It Plural then Add Type to the End", GLOBAL_MAKE_PLURAL_ADD_TYPE_END }			};

	public static final String[][]	STATIC_IMPORT_CHOICE_TYPES										= {
			{ "Always Create Static Import", GLOBAL_ALWAYS_CREATE }, { "Ask To Create Static Import", GLOBAL_ASK_TO_CREATE },
			{ "Never Create Static Import", GLOBAL_NEVER_CREATE }									};

	public static final String[][]	COPY_METHOD_CHOICE_TYPES										= {
			{ "Always Copy Method Body", GLOBAL_ALWAYS_CREATE }, { "Ask To Create Method Body", GLOBAL_ASK_TO_CREATE },
			{ "Never Create Method Body", GLOBAL_NEVER_CREATE }									};

	public static final String		P_DATABASE_TYPE													= "DATABASE TYPE";

	public static final String		P_DATABASE_NAME													= "DATABASE NAME";

	public static final String		P_HOST_ADDRESS													= "HOST ADDRESS";

	public static final String		P_PORT_NUMBER													= "PORT NUMBER";

	public static final String		P_USER_NAME														= "USER NAME";

	public static final String		P_PASSWORD														= "PASSWORD";

	public static final String		P_DB_PACKAGE_FOR_POJO_CLASS										= "DB_PACKAGE_FOR_POJO_CLASS";

	public static final String		P_WORKING_JAVA_PROJECT											= "WORKING_JAVA_PROJECT";

	public static final String		P_DATABASE_TEMPLATE_PREFIX										= "DATABASE_TEMPLATE";

	//public static final String		P_FILE_TEMPLATE_PREFIX											= "FILE_TEMPLATE";

	//public static final String		P_FILE_ALL_TEMPLATES											= "ALL_FILE_TEMPLATES";

	public static final String		P_SETTER_VAR_PREFIX												= "SETTER_VAR_PREFIX";

	//public static final String		P_ALL_ADDITIONAL_DATABASE_TEMPLATES								= "ALL_ADDITIONAL_DATABASE_TEMPLATES";

	public static final String		DATABASE_TEMPLATE_SELECT_NAMED_QUERY							= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "SELECT_WITH_NAMED_QUERY";

	public static final String		DATABASE_TEMPLATE_UPDATE_NAMED_QUERY							= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "UPDATE_WITH_NAMED_QUERY";

	public static final String		DATABASE_TEMPLATE_DELETE_NAMED_QUERY							= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "DELETE_WITH_NAMED_QUERY";

	public static final String		DATABASE_TEMPLATE_INSERT_NAMED_QUERY							= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "INSERT_WITH_NAMED_QUERY";

	public static final String		P_COMMON_TEMPLATE_PREFIX										= "COMMON_TEMPLATE";

	//public static final String		P_ADDITIONAL_DATABASE_TEMPLATE_PREFIX							= "ADDITIONAL_DATABASE_TEMPLATE";

	public static final String		DATABASE_TEMPLATE_POJO_CLASS									= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE + "POJO_CLASS";

	public static final String		DATABASE_TEMPLATE_ADD_FIELDS_TO_POJO_CLASS						= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "ADD_FIELDS_TO_POJO_CLASS";

	public static final String		DATABASE_TEMPLATE_INSERT_WITH_NAMED_PARAMETER					= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "INSERT_WITH_NAMED_PARAMETER";

	public static final String		DATABASE_TEMPLATE_INSERT_SIMPLE									= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE + "INSERT_SIMPLE";

	public static final String		DATABASE_TEMPLATE_DELETE_WITH_NAMED_PARAMETER					= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "DELETE_WITH_NAMED_PARAMETER";

	public static final String		DATABASE_TEMPLATE_DELETE_SIMPLE									= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE + "DELETE_SIMPLE";

	public static final String		DATABASE_TEMPLATE_UPDATE_WITH_NAMED_PARAMETER					= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "UPDATE_WITH_NAMED_PARAMETER";

	public static final String		DATABASE_TEMPLATE_UPDATE_SIMPLE									= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE + "UPDATE_SIMPLE";

	public static final String		DATABASE_TEMPLATE_SELECT_WITH_NAMED_PARAMETER					= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "SELECT_WITH_NAMED_PARAMETER";

	public static final String		DATABASE_TEMPLATE_SELECT_SIMPLE									= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE + "SELECT_SIMPLE";

	public static final String		DATABASE_TEMPLATE_SELECT_WITH_JOIN								= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "SELECT_WITH_JOIN";

	public static final String		DATABASE_TEMPLATE_SIMPLE_SNIPPET								= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE + "SIMPLE_SNIPPET";

	public static final String		P_GETTER_SETTER_FORMAT											= "GETTER_SETTER_FORMAT";

	public static final String		P_SINGLE_LINE													= "SINGLE_LINE";

	public static final String		P_MULTILINE_WITHOUT_COMMENT										= "MULTILINE_WITHOUT_COMMENT";

	public static final String		P_MULTILINE_WITH_COMMENT										= "MULTILINE_WITH_COMMENT";

	public static final String		DATABASE_TEMPLATE_SELECT_WITH_NAMED_QUERY_ANNOTATION			= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "SELECT_WITH_NAMED_QUERY_ANNOTATION";

	public static final String		DATABASE_TEMPLATE_UPDATE_WITH_NAMED_QUERY_ANNOTATION			= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "UPDATE_WITH_NAMED_QUERY_ANNOTATION";

	public static final String		DATABASE_TEMPLATE_DELETE_WITH_NAMED_QUERY_ANNOTATION			= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "DELETE_WITH_NAMED_QUERY_ANNOTATION";

	public static final String		DATABASE_TEMPLATE_INSERT_WITH_NAMED_QUERY_ANNOTATION			= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "INSERT_WITH_NAMED_QUERY_ANNOTATION";

	public static final String		P_TEMPLATE_SECOND_TEMPLATE_ITEM									= "SECOND_TEMPLATE_ITEM";

	public static final String		P_GLOBAL_STRICTLY_MAINTAIN_GETTER_SETTER						= "STRICTLY_MAINTAIN_GETTER_SETTER";

	public static final String		P_TEMPLATE_ADDITIONAL_PARAMETERS								= "ADDITIONAL_PARAMETERS";

	public static final String		DATABASE_TEMPLATE_SELECT_COUNT									= "DATABASE_TEMPLATE_SELECT_COUNT";

	public static final String		TEMPLATE_TYPE_METHOD_SNIPPET									= TEMPLATE + UNDERSCORE
																											+ "METHOD_SNIPPET";

	public static final String		P_DELIMITER_FOR_FILE_TEMPLATE									= "DELIMITER_FOR_FILE_TEMPLATE";

	public static final String		P_TEMPLATE_FIRST_TEMPLATE_ITEM									= "FIRST_TEMPLATE_ITEM";

	public static final String		P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES					= "ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES";

	public static final String		P_VARIABLE_ANNOTATION											= "VARIABLE_ANNOTATION";

	public static final String		P_JUNIT_FIELD_ANNOTATIONS										= "junitFieldAnnotations";

	public static final String		DATABASE_TEMPLATE_POJO_CLASS_WITHOUT_ANNOTATION					= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "POJO_CLASS_WITHOUT_ANNOTATION";
	public static final String		P_STATIC_MEMBERS_AND_TYPES										= "STATIC_MEMBERS_AND_TYPES";

	public static final String		P_EMBEDDED_FIELDS_PROJECT										= "EMBEDDED_FIELDS_PROJECT";

	public static final String		P_EMBEDDED_FIELDS_SRC_PATH										= "EMBEDDED_FIELDS_SRC_PATH";

	public static final String		P_EMBEDDED_FIELDS_INCLUDE_PACKAGE								= "EMBEDDED_FIELDS_INCLUDE_PACKAGE";

	public static final String		P_EMBEDDED_FIELDS_EXCLUDE_PACKAGE								= "EMBEDDED_FIELDS_EXCLUDE_PACKAGE";

	public static final String		P_EMBEDDED_FIELDS_VIEW											= "EMBEDDED_FIELDS_VIEW";

	public static final String		P_RADIO_GROUP_EDITOR_VALUE										= "RADIO_GROUP_EDITOR_VALUE";

	public static final String		P_POJO_BASE_CLASS												= "POJO_BASE_CLASS";

	public static final String		P_POJO_IMPLEMENT_INTERFACES										= "POJO_IMPLEMENT_INTERFACES";

	public static final String		P_GETTER_CUSTOM_FORMAT											= "GETTER_CUSTOM_FORMAT";

	public static final String		TEMPLATE_TYPE_CREATE_NEW_HASHCODE_AND_EQUALS_METHODS			= TEMPLATE + UNDERSCORE
																											+ "HASHCODE_AND_EQUALS_METHODS";

	public static final String		P_SETTER_CUSTOM_FORMAT											= "SETTER_CUSTOM_FORMAT";

	public static final String		P_GETTER_SETTER_POSITION										= "GETTER_SETTER_POSITION";

	public static final String		TEMPLATE_TYPE_CREATE_NEW_INSTANCE_MULTIPLE_CLASS				= TEMPLATE + UNDERSCORE
																											+ "INSTANCE_OF_MULTIPLE_CLASS";

	public static final String		DATABASE_TEMPLATE_ROWMAPPER										= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "ROWMAPPER_AS_CLASS";

	public static final String		DATABASE_TEMPLATE_ADD_FIELDS_TO_POJO_CLASS_WITHOUT_ANNOTATION	= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "ADD_FIELDS_TO_POJO_CLASS_WITHOUT_ANNOTATION";

	public static final String		DATABASE_TEMPLATE_SELECT_WITH_ROWMAPPER							= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "SELECT_WITH_ROWMAPPER_AS_METHOD";

	public static final String		DATABASE_TEMPLATE_POJO_INSTANCE_FROM_DB_FIELD					= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "POJO_INSTANCE_FROM_DB_FIELD";

	public static final String		DATABASE_TEMPLATE_SELECT_WITH_JOIN_AND_MAPPER					= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "SELECT_WITH_JOIN_ROWMAPPER_AS_METHOD";

	public static final String		TEMPLATE_CREATE_IMPL											= TEMPLATE + UNDERSCORE + "CREATE_IMPL";

	public static final String		TEMPLATE_COPY_CLASSES											= TEMPLATE + UNDERSCORE
																											+ "COPY_CLASSES";

	public static final String		TEMPLATE_SPRING_BEAN_FILE										= TEMPLATE + UNDERSCORE
																											+ "SPRING_BEAN_FILE";

	public static final String		TEMPLATE_INSTANCE_OF_CLASSES									= TEMPLATE + UNDERSCORE
																											+ "INSTANCE_OF_CLASSES";

	public static final String		TEMPLATE_INSTANCE_OF_GENERIC_DAO								= TEMPLATE + UNDERSCORE
																											+ "INSTANCE_OF_GENERIC_DAO";

	public static final String		TEMPLATE_INSTANCE_OF_CLASS_WEB									= TEMPLATE + UNDERSCORE
																											+ "INSTANCE_OF_CLASS_WEB";

	public static final String		P_REPOSITORY_URL												= "REPOSITORY_URL";

	public static final String		P_REPOSITORY_NAME												= "REPOSITORY_NAME";

	public static final String		P_CHECK_IN														= "CHECK_IN";

	public static final String		P_COMMENTS_PREFIX												= "COMMENTS_PREFIX";

	public static final String		P_COMMENTS_FOOTER												= "COMMENTS_FOOTER";

	public static final String		P_COMMENTS_TEXT													= "COMMENTS_TEXT";

	public static final String		P_REPOSITORY_USERNAME											= "REPOSITORY_USERNAME";

	public static final String		P_REPOSITORY_PASSWORD											= "REPOSITORY_PASSWORD";

	public static final String		P_EXPORT_SETTINGS												= "EXPORT_SETTINGS";

	public static final String[][]	EXPORT_SETTINGS_TYPES											= {
			{ "Overwrite without Backup", "overWrite" }, { "Back Up and OverWrite", "backUp" }, { "Ask User", "askToOverwriteOrBackup" } };

	public static final String		P_COMMENTS_DEFAULT_OR_USER_SUPPLIED								= "COMMENTS_DEFAULT_OR_USER_SUPPLIED";

	public static final String		P_TIME_GAP_BEFORE_CHECK_IN										= "TIME_GAP_BEFORE_AUTO_CHECK_IN";

	public static final String		TEMPLATE_PRINT_FIELDS_OF_CLASS_WEB_SINGLE						= TEMPLATE
																											+ UNDERSCORE
																											+ "PRINT_FIELDS_OF_CLASS_WEB_SINGLE";

	public static final String		TEMPLATE_CREATE_FILE_WITH_SELECTED_CONTENT						= TEMPLATE
																											+ UNDERSCORE
																											+ "CREATE_FILE_WITH_SELECTED_CONTENT";

	public static final String		DATABASE_TEMPLATE_INSERT_SQL_QUERY								= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "INSERT_SQL_QUERY";

	public static final String		DATABASE_TEMPLATE_UPDATE_SQL_QUERY								= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "UPDATE_SQL_QUERY";

	public static final String		DATABASE_TEMPLATE_DELETE_SQL_QUERY								= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "DELETE_SQL_QUERY";

	public static final String		DATABASE_TEMPLATE_SELECT_SQL_QUERY								= P_DATABASE_TEMPLATE_PREFIX
																											+ UNDERSCORE
																											+ "SELECT_SQL_QUERY";
	public static final String		P_TEMPLATES_TO_ENABLE_POJO										= "P_TEMPLATES_TO_ENABLE_POJO";

	public static final String		P_TEMPLATE_NAME													= "NAME";

	public static final String		P_ENABLE_AUTO_CHECKIN											= "enable_auto_checkin";

	public static final String		P_DEFAULT_TEMPLATES												= "DEFAULT_TEMPLATES";

	public static final String		P_REPOSITORY_PROJECT											= "repository_project";

	public static final String		DEFAULT_TEMPLATES												= "TEMPLATE";

	public static final String		DEFAULT_DATABASE_TEMPLATES										= "DATABASE_TEMPLATE";

	public static final String		DEFAULT_ADDITIONAL_TEMPLATES									= "ADDITIONAL_TEMPLATE";

	public static final String		DEFAULT_FILE_TEMPLATES											= "FILE_TEMPLATE";

	public static final String		P_FILE_TEMPLATE_PLACHOLDER_NAME									= "FILE_TEMPLATE_PLACHOLDER_NAME";

	//public static final String		FILE_TEMPLATE_SAMPLE_SNIPPET									= P_FILE_TEMPLATE_PREFIX + UNDERSCORE+ "SAMPLE_SNIPPET";

	public static final String		P_REPOSIROTY_PROJECT_URL_PAIR									= "project_url_pair";

	public static final String		P_REPOSITORY_BASE_LOCATION										= "repository_base_location";

	public static final String		P_ENABLE_TRACK_ECLIPSE_CHANGE									= "enable_track_eclipse_changes";

	public static final String		P_EXCLUDE_DIR													= "exclude_directories";

	public static final String		P_ALL_PREFIX													= "all_prefix";

	public static final String		TEMPLATE_TYPE_SAMPLE_FILE_TEMPLATE								= TEMPLATE + UNDERSCORE
																						+ "SAMPLE_FILE_TEMPLATE";
	public static final String		P_DBCONN_FIELD_DELIMITER										= "dbconn_field_delimiter";

	public static final String		P_DBCONN_RECORD_DELIMITER										= "dbconn_record_delimiter";

	public static final String		P_DATABASE_CONN_DATA											= "dbconn_data";
	
	public static final String		P_ALL_COMMON_VARIABLES											= "ALL_COMMON_VARIABLES";

}

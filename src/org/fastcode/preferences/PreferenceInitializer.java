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

import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.CLASS_ANNOTATIONS_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_BODY_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_HEADER_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_IMPORTS_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_MODIFIER_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_NAME_STR;
import static org.fastcode.common.FastCodeConstants.CLASS_TYPE_STR;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.CONVERSION_NONE;
import static org.fastcode.common.FastCodeConstants.DB_TEMPLATES_FOLDER;
import static org.fastcode.common.FastCodeConstants.EMPTY_QUOTE_STR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.FIELD_ANNOTATIONS_STR;
import static org.fastcode.common.FastCodeConstants.FIELD_CLASS_STR;
import static org.fastcode.common.FastCodeConstants.FIELD_MODIFIER_STR;
import static org.fastcode.common.FastCodeConstants.FIELD_NAME_STR;
import static org.fastcode.common.FastCodeConstants.GLOBAL_ALWAYS_CREATE;
import static org.fastcode.common.FastCodeConstants.GLOBAL_ASK_TO_CREATE;
import static org.fastcode.common.FastCodeConstants.GLOBAL_MAKE_PLURAL;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN;
import static org.fastcode.common.FastCodeConstants.METHOD_ANNOTATIONS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_ARGS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_BODY_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_COMMENTS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_EXCEPTIONS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_MODIFIER_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_NAME_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_RETURN_TYPE_STR;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.RIGHT_CURL;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN;
import static org.fastcode.common.FastCodeConstants.SEMICOLON;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.TAB;
import static org.fastcode.common.FastCodeConstants.TEMPLATES_FOLDER;
import static org.fastcode.common.FastCodeConstants.TRUE_STR;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.preferences.PreferenceConstants.*;
import static org.fastcode.preferences.PreferenceUtil.getPreferenceLabel;
import static org.fastcode.util.SourceUtil.getAllSourcePaths;
import static org.fastcode.util.StringUtil.makePlaceHolder;
import static org.fastcode.util.TemplateUtil.findOrcreateTemplate;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.Activator;
import org.fastcode.common.FastCodeConstants.CHECK_IN;
import org.fastcode.common.FastCodeConstants.CLASS_TYPE;
import org.fastcode.common.FastCodeConstants.EXPORT_OPTIONS;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.util.ConfigPattern;
import org.fastcode.util.CreateSimilarDescriptorConfig;
import org.fastcode.util.FastCodeUtil;
import org.fastcode.util.TemplateUtil;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	ConfigPattern							configPattern					= ConfigPattern.getInstance();
	public static final String				templateConfigFile				= "templates-config.xml";
	public static final String				commonTemplateConfigFile		= "common-templates-config.xml";
	public static final String				databaseTemplateConfigFile		= "database-templates-config.xml";
	//public static final String				fileTemplateConfigFile			= "file-templates-config.xml";
	//public static final String				additionalDatabaseTemplateConfigFile	= "additional-database-templates-config.xml";

	public static final String[]			createSimilarPreferenceArray	= { CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID,
			CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID, CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID,
			CREATE_SIMILAR_PREFERENCE_POJO_SERVICE_UI_ID, CREATE_NEW_PREFERENCE_DAO_ID, CREATE_NEW_PREFERENCE_DAO_POJO_ID,
			CREATE_NEW_PREFERENCE_SERVICE_ID, CREATE_NEW_PREFERENCE_SERVICE_POJO_ID, CREATE_NEW_PREFERENCE_UI_ID,
			CREATE_NEW_PREFERENCE_UI_POJO_ID								};

	public static final Map<String, String>	classTypesMap					= new HashMap<String, String>();

	static {
		classTypesMap.put(CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID, CLASS_TYPE.INTERFACE.value());
		classTypesMap.put(CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID, CLASS_TYPE.CLASS.value());

		classTypesMap.put(CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME, CLASS_TYPE.INTERFACE.value());

		classTypesMap.put(CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID, CLASS_TYPE.CLASS.value());
		classTypesMap.put(CREATE_SIMILAR_PREFERENCE_POJO_SERVICE_UI_ID, CLASS_TYPE.CLASS.value());

		classTypesMap.put(CREATE_NEW_PREFERENCE_DAO_ID, CLASS_TYPE.INTERFACE.value());
		classTypesMap.put(CREATE_NEW_PREFERENCE_SERVICE_ID, CLASS_TYPE.INTERFACE.value());
		classTypesMap.put(CREATE_NEW_PREFERENCE_UI_ID, CLASS_TYPE.CLASS.value());

		classTypesMap.put(CREATE_NEW_PREFERENCE_DAO_POJO_ID, CLASS_TYPE.CLASS.value());
		classTypesMap.put(CREATE_NEW_PREFERENCE_SERVICE_POJO_ID, CLASS_TYPE.CLASS.value());
		classTypesMap.put(CREATE_NEW_PREFERENCE_UI_POJO_ID, CLASS_TYPE.CLASS.value());
	}
	public static final Map<String, String>	propertiesDefalutValuesMap		= new HashMap<String, String>();

	static {
		propertiesDefalutValuesMap.put("default.template.variation.field", TRUE_STR);
		propertiesDefalutValuesMap.put("default.template.variation.field.name", "_template_variation");
		propertiesDefalutValuesMap.put("exclude.fields.file.extensions", EMPTY_STR);
		propertiesDefalutValuesMap.put("hql.variation.default", EMPTY_STR);
		propertiesDefalutValuesMap.put("named.query.file.location", EMPTY_STR);
		propertiesDefalutValuesMap.put("hql.node", "named-query");
		propertiesDefalutValuesMap.put("hql.rootnode", "entity-mappings");
	}

	public static Map<String, String>		annotationsMap					= new HashMap<String, String>();

	static {
		annotationsMap.put("org.springframework.beans.factory.annotation.Qualifier", "${1}(\"${field_full_class}\")");
		annotationsMap.put("org.springframework.stereotype.Service", "${1}(\"${to_full_class}\")");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {

		//		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		//		final IPreferencesService service = Platform.getPreferencesService();
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		//		final Preferences prefs = new InstanceScope().getNode(MY_PLUGIN_ID);
		store.setDefault(P_GLOBAL_PACKAGE_PATTERN, "[a-z.]+[a-z]+");
		store.setDefault(P_GLOBAL_CLASS_NAME_PATTERN, "[A-Z]+[a-z,A-Z]+");
		store.setDefault(P_GLOBAL_CLASS_BODY, makePlaceHolder(CLASS_HEADER_STR) + NEWLINE + "package ${package_name};\n"
				+ makePlaceHolder(CLASS_IMPORTS_STR) + NEWLINE + NEWLINE + makePlaceHolder(CLASS_ANNOTATIONS_STR) + NEWLINE + "public "
				+ makePlaceHolder(CLASS_MODIFIER_STR) + SPACE + makePlaceHolder(CLASS_TYPE_STR) + SPACE + makePlaceHolder(CLASS_NAME_STR)
				+ SPACE + "extends ${super_class} implements ${interfaces} {" + NEWLINE + NEWLINE + TAB + makePlaceHolder(CLASS_BODY_STR)
				+ NEWLINE + "}" + NEWLINE);

		store.setDefault(P_GLOBAL_CLASS_HEADER, "/**\n" + " * This class has been generated by Fast Code Eclipse Plugin \n"
				+ " * For more information please go to http://fast-code.sourceforge.net/\n" + " * @author : ${user}\n"
				+ " * Created : ${today}\n" + " */\n");

		final StringBuilder annotations = new StringBuilder();
		for (final String key : annotationsMap.keySet()) {
			annotations.append(key + COLON + annotationsMap.get(key) + NEWLINE);
		}
		store.setDefault(P_GLOBAL_ANNOTATIONS_VALUES_MAP, annotations.toString().trim());

		store.setDefault(P_GLOBAL_CLASS_METHOD_BODY, makePlaceHolder(METHOD_COMMENTS_STR) + NEWLINE
				+ makePlaceHolder(METHOD_ANNOTATIONS_STR) + NEWLINE + makePlaceHolder(METHOD_MODIFIER_STR) + SPACE
				+ makePlaceHolder(METHOD_RETURN_TYPE_STR) + SPACE + makePlaceHolder(METHOD_NAME_STR) + LEFT_PAREN
				+ makePlaceHolder(METHOD_ARGS_STR) + RIGHT_PAREN + " throws " + makePlaceHolder(METHOD_EXCEPTIONS_STR) + SPACE + LEFT_CURL
				+ NEWLINE + TAB + makePlaceHolder(METHOD_BODY_STR) + NEWLINE + RIGHT_CURL + NEWLINE);

		store.setDefault(P_GLOBAL_CONSTRUCTOR_BODY, "#foreach ($field in ${fields})" + NEWLINE + "this.${field.name} = ${field.name};"
				+ NEWLINE + "#end");

		store.setDefault(P_GLOBAL_INTERFACE_METHOD_BODY, makePlaceHolder(METHOD_COMMENTS_STR) + NEWLINE
				+ makePlaceHolder(METHOD_ANNOTATIONS_STR) + NEWLINE + makePlaceHolder(METHOD_MODIFIER_STR) + SPACE
				+ makePlaceHolder(METHOD_RETURN_TYPE_STR) + SPACE + makePlaceHolder(METHOD_NAME_STR) + LEFT_PAREN
				+ makePlaceHolder(METHOD_ARGS_STR) + RIGHT_PAREN + " throws " + makePlaceHolder(METHOD_EXCEPTIONS_STR) + SEMICOLON);

		store.setDefault(P_GLOBAL_CLASS_FIELD_BODY, makePlaceHolder(FIELD_ANNOTATIONS_STR) + NEWLINE + makePlaceHolder(FIELD_MODIFIER_STR)
				+ SPACE + makePlaceHolder(FIELD_CLASS_STR) + SPACE + makePlaceHolder(FIELD_NAME_STR) + SEMICOLON);

		store.setDefault(P_GLOBAL_SOURCE_PATH_JAVA, "/src/main/java /src");
		store.setDefault(P_GLOBAL_SOURCE_PATH_TEST, "/src/test/java /test");
		store.setDefault(P_GLOBAL_SOURCE_PATH_RESOURCES, "/src/main/resources /resources");

		store.setDefault(P_GLOBAL_EQUALS_METHOD_BODY, "if (this == ${class_instance}) {" + NEWLINE + "\t return true;" + NEWLINE + "}"
				+ NEWLINE + "if (${class_instance} == null) {" + NEWLINE + "\t return false;" + NEWLINE + "}" + NEWLINE
				+ "if (!(${class_instance} instanceof ${class_name})) {" + NEWLINE + "\t return false;" + NEWLINE + "}" + NEWLINE
				+ "${class_name} other${class_name} = (${class_name}) ${class_instance};" + NEWLINE + "return new EqualsBuilder()"
				+ "#foreach ($field in ${fields})" + ".append(this.${field.name}, other${class_name}.${field.name})" + "#end .isEquals();");
		store.setDefault(P_GLOBAL_HASHCODE_METHOD_BODY, "return new HashCodeBuilder(17, 37)" + "#foreach ($field in ${fields})"
				+ ".append(this.${field.name})" + "#end");

		store.setDefault(P_GLOBAL_TOSTRING_METHOD_BODY, "StringBuilder sb = new StringBuilder();" + NEWLINE + "sb"
				+ "#foreach ($field in ${fields})" + NEWLINE + ".append(\"${field.makeWord()} :  \" + this.${field.name})" + NEWLINE
				+ "#end" + NEWLINE + "return sb.toString();");

		store.setDefault(P_GLOBAL_USE_DEFAULT_FOR_PATH, true);
		store.setDefault(P_GLOBAL_FINAL_MODIFIER_FOR_METHOD_ARGS, true);

		store.setDefault(P_GLOBAL_PLACE_HOLDER_VALUES, "Following Place Holders can be Used in \n" + "Configurations and File Names\n"
				+ "(They are pretty self explanatory)\n" + "${user}, ${today}" + TAB + TAB + "${from_class}," + TAB + TAB
				+ "${to_class},\n" + "${from_instance}," + TAB + TAB + "${to_instance},\n" + "${to_full_class}," + TAB + TAB
				+ "${to_full_impl_class},\n" + "${to_impl_class}\n");
		store.setDefault(P_GLOBAL_PARAMETERIZED_TYPE_CHOICE, GLOBAL_ASK_TO_CREATE);
		store.setDefault(P_GLOBAL_PARAMETERIZED_NAME_STRATEGY, GLOBAL_MAKE_PLURAL);
		store.setDefault(P_GLOBAL_STATIC_IMPORT_TYPE_CHOICE, GLOBAL_ASK_TO_CREATE);

		store.setDefault(P_GLOBAL_CONVERT_METHOD_PARAM_PATTERN,
				"${converted_class} ${converted_name} = mapper.map(${original_name}, ${converted_class}.class)");

		store.setDefault(P_GLOBAL_IMPL_SUFFIX, "Impl");
		store.setDefault(P_GLOBAL_IMPL_SUB_PACKAGE, "impl");
		store.setDefault(P_GLOBAL_PARAMETERIZED_IGNORE_EXTENSIONS, "Vo VO Bean Pojo POJO DTO Dto");

		store.setDefault(P_TEST_DIR, EMPTY_STR);
		store.setDefault(P_BASE_TEST, EMPTY_STR);

		for (final String pref : classTypesMap.keySet()) {
			final String classType = classTypesMap.get(pref);
			store.setDefault(getPreferenceLabel(P_CLASS_TYPE, pref), classType);
			store.setDefault(getPreferenceLabel(P_NUM_SIMILAR_CLASSES, pref), classType.equals(CLASS_TYPE.INTERFACE.value()) ? 2 : 1);
			if (classType.equals(CLASS_TYPE.INTERFACE.value())) {
				store.setDefault(getPreferenceLabel(P_CLASS_TYPE, pref, 1), CLASS_TYPE.CLASS.value());
				store.setDefault(getPreferenceLabel(P_IMPL_SUB_PACKAGE, pref), "impl");
				store.setDefault(getPreferenceLabel(P_IMPLEMENT_INT, pref), true);
				//				store.setDefault(getPreferenceLabel(P_COPY_METHODS, pref),  true);
			}
		}
		store.setDefault(getPreferenceLabel(P_FROM_PATTERN, CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID),
				"(${ANY_PACKAGE}).dao.(${ANY_CLASS})Dao");
		store.setDefault(getPreferenceLabel(P_FROM_PATTERN, CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID),
				"(${ANY_PACKAGE}).service.(${ANY_CLASS})Service");
		store.setDefault(getPreferenceLabel(P_FROM_PATTERN, CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID),
				"(${ANY_PACKAGE}).dao.domain.(${ANY_CLASS})Vo");
		store.setDefault(getPreferenceLabel(P_FROM_PATTERN, CREATE_SIMILAR_PREFERENCE_POJO_SERVICE_UI_ID),
				"(${ANY_PACKAGE}).service.domain.(${ANY_CLASS})Bean");

		store.setDefault(getPreferenceLabel(P_FROM_PATTERN, CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME), "(${ANY_PACKAGE}).(${ANY_CLASS})DAO");
		store.setDefault(getPreferenceLabel(P_DIFFERENT_NAME, CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME), true);

		store.setDefault(getPreferenceLabel(P_TO_PATTERN, CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID), "${1}.service.${2}Service");
		store.setDefault(getPreferenceLabel(P_TO_PATTERN, CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID), "${1}.handler.${2}Handler");
		store.setDefault(getPreferenceLabel(P_TO_PATTERN, CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID), "${1}.service.domain.${2}Bean");
		store.setDefault(getPreferenceLabel(P_TO_PATTERN, CREATE_SIMILAR_PREFERENCE_POJO_SERVICE_UI_ID), "${1}.handler.form.${2}Form");

		store.setDefault(getPreferenceLabel(P_TO_PATTERN, CREATE_NEW_PREFERENCE_DAO_ID), "${input}Dao");
		store.setDefault(getPreferenceLabel(P_TO_PATTERN, CREATE_NEW_PREFERENCE_SERVICE_ID), "${input}Service");
		store.setDefault(getPreferenceLabel(P_TO_PATTERN, CREATE_NEW_PREFERENCE_UI_ID), "${input}Action");

		store.setDefault(getPreferenceLabel(P_TO_PATTERN, CREATE_NEW_PREFERENCE_DAO_POJO_ID), "${input}Dto");
		store.setDefault(getPreferenceLabel(P_TO_PATTERN, CREATE_NEW_PREFERENCE_SERVICE_POJO_ID), "${input}Vo");
		store.setDefault(getPreferenceLabel(P_TO_PATTERN, CREATE_NEW_PREFERENCE_UI_POJO_ID), "${input}Bean");

		store.setDefault(getPreferenceLabel(P_TO_PATTERN, CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME), "${1}.${input}DAO");
		store.setDefault(getPreferenceLabel(P_COPY_METHODS, CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID), true);
		store.setDefault(getPreferenceLabel(P_COPY_METHODS, CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME), true);
		//		store.setDefault(getPreferenceLabel(P_COPY_METHODS, CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID), true);
		store.setDefault(getPreferenceLabel(P_COPY_FIELDS, CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID), true);
		store.setDefault(getPreferenceLabel(P_COPY_FIELDS, CREATE_SIMILAR_PREFERENCE_POJO_SERVICE_UI_ID), true);
		//		store.setDefault(getPreferenceLabel(P_BREAK_DATE_FIELDS, CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID), false);
		//		store.setDefault(getPreferenceLabel(P_BREAK_DATE_FIELDS, CREATE_SIMILAR_PREFERENCE_POJO_SERVICE_UI_ID), false);

		store.setDefault(getPreferenceLabel(P_CREATE_METHOD_BODY, CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID), true);
		store.setDefault(getPreferenceLabel(P_CREATE_METHOD_BODY, CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID), true);

		store.setDefault(getPreferenceLabel(P_CONVERT_METHOD_PARAM_FROM, CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID),
				"(${ANY_PACKAGE}).dto.(${ANY_CLASS})Dto");
		store.setDefault(getPreferenceLabel(P_CONVERT_METHOD_PARAM_TO, CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID), "${1}.vo.${2}Vo");

		store.setDefault(getPreferenceLabel(P_CONVERT_METHOD_PARAM, CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME), true);
		store.setDefault(getPreferenceLabel(P_CONVERT_METHOD_PARAM_FROM, CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME),
				"(${ANY_PACKAGE}).dto.(${ANY_CLASS})Dto");
		store.setDefault(getPreferenceLabel(P_CONVERT_METHOD_PARAM_TO, CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME), "${1}.dto.${input}Dto");

		store.setDefault(getPreferenceLabel(P_CREATE_WORKING_SET, CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID), true);
		store.setDefault(getPreferenceLabel(P_CREATE_WORKING_SET, CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID), true);
		store.setDefault(getPreferenceLabel(P_CREATE_WORKING_SET, CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID), true);
		store.setDefault(getPreferenceLabel(P_CREATE_WORKING_SET, CREATE_SIMILAR_PREFERENCE_POJO_SERVICE_UI_ID), true);

		store.setDefault(getPreferenceLabel(P_WORKING_SET_NAME, CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID), "${2}WorkingSet");
		store.setDefault(getPreferenceLabel(P_WORKING_SET_NAME, CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID), "${2}WorkingSet");
		store.setDefault(getPreferenceLabel(P_WORKING_SET_NAME, CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID), "${2}WorkingSet");
		store.setDefault(getPreferenceLabel(P_WORKING_SET_NAME, CREATE_SIMILAR_PREFERENCE_POJO_SERVICE_UI_ID), "${2}WorkingSet");

		store.setDefault(getPreferenceLabel(P_INCLUDE_INSTACE_FROM, CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID), true);
		store.setDefault(getPreferenceLabel(P_INCLUDE_INSTACE_FROM, CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID), true);

		/*store.setDefault(getPreferenceLabel(P_RETURN_VARIABLE, CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID), true);
		store.setDefault(getPreferenceLabel(P_RETURN_VARIABLE, CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID), true);

		store.setDefault(getPreferenceLabel(P_RETURN_VARIABLE_NAME, CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID), "result");
		store.setDefault(getPreferenceLabel(P_RETURN_VARIABLE_NAME, CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID), "result");*/

		for (int configCount = 0; configCount < this.configPattern.getConfigs().length; configCount++) {
			final CreateSimilarDescriptorConfig config = this.configPattern.getConfigs()[configCount];
			for (int j = 0; j < createSimilarPreferenceArray.length; j++) {
				store.setDefault(getPreferenceLabel(P_CONFIG_TYPE, createSimilarPreferenceArray[j]) + configCount, config.getConfigType());
				if (config.getConfigFileName() != null) {
					store.setDefault(getPreferenceLabel(P_CONFIG_FILE_NAME, createSimilarPreferenceArray[j]) + configCount,
							config.getConfigFileName());
				}
				store.setDefault(getPreferenceLabel(P_CONFIG_FILE_CONV_TYPES, createSimilarPreferenceArray[j]) + configCount,
						CONVERSION_NONE);
				if (config.getConfigLocation() != null) {
					store.setDefault(getPreferenceLabel(P_CONFIG_LOCATION, createSimilarPreferenceArray[j]) + configCount,
							config.getConfigLocation());
				}
				if (config.getConfigLocale() != null) {
					store.setDefault(getPreferenceLabel(P_CONFIG_LOCALE, createSimilarPreferenceArray[j]) + configCount,
							config.getConfigLocale());
				}
				store.setDefault(getPreferenceLabel(P_CONFIG_HEADER_PATTERN, createSimilarPreferenceArray[j]) + configCount,
						config.getConfigHeaderPattern());
				store.setDefault(getPreferenceLabel(P_CONFIG_START_PATTERN, createSimilarPreferenceArray[j]) + configCount,
						config.getConfigStartPattern());
				store.setDefault(getPreferenceLabel(P_CONFIG_END_PATTERN, createSimilarPreferenceArray[j]) + configCount,
						config.getConfigEndPattern());
				store.setDefault(getPreferenceLabel(P_CONFIG_PATTERN, createSimilarPreferenceArray[j]) + configCount,
						config.getConfigPattern());
			}
			store.setDefault(getPreferenceLabel(P_CONFIG_LOCATION, CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME) + configCount,
					"/src/main/resources");
		}

		for (int j = 0; j < createSimilarPreferenceArray.length; j++) {
			store.setDefault(getPreferenceLabel(P_INCLUDE_PATTERN, createSimilarPreferenceArray[j]), ASTERISK);
			store.setDefault(getPreferenceLabel(P_CONFIG_ITEMS, createSimilarPreferenceArray[j]), EMPTY_STR);
			store.setDefault(getPreferenceLabel(P_REMOVE_CONFIG, createSimilarPreferenceArray[j]), false);
		}

		for (int j = 0; j < createSimilarPreferenceArray.length; j++) {
			store.setDefault(getPreferenceLabel(P_CONFIG_FILE_NAME, createSimilarPreferenceArray[j]), "application-context-${to_instance}");
		}
		store.setDefault(getPreferenceLabel(P_CONFIG_FILE_NAME, CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME),
				"application-context-${to_instance}");

		store.setDefault(getPreferenceLabel(P_CONFIG_PATTERN, CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID),
				"<bean id=\"${to_instance}\" class=\"${to_impl_class}\">\r\n\t<property name=\"${from_instance}\" ref=\"${from_instance}\" />\r\n</bean>");
		store.setDefault(getPreferenceLabel(P_CONFIG_PATTERN, CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID),
				"<bean id=\"${to_instance}\" class=\"${to_impl_class}\">\r\n\t<property name=\"${from_instance}\" ref=\"${from_instance}\" />\r\n</bean>");
		store.setDefault(getPreferenceLabel(P_CONFIG_PATTERN, CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME),
				"<bean id=\"${to_instance}\" class=\"${to_impl_class}\">\r\n\t<property name=\"${from_instance}\" ref=\"${from_instance}\" />\r\n</bean>");

		store.setDefault(P_JUNIT_OVERWRITE, false);
		store.setDefault(P_SHOW_TIPS, true);

		store.setDefault(P_JUNIT_TEST_FORMAT, "/** " + NEWLINE + "*\n" + "* This is a junit test for ${class_name}\n"
				+ "* This test was generated at ${today}\n" + "* @author ${user}\n" + "*/\n\n"
				+ "public class ${class_name}Test extends ${base_class} {\n" + "public ${class_name}Test  (){\n" +
				//"setPopulateProtectedVariables(true);\n" +
				"}\n" + "\n\n}\n");

		store.setDefault(P_JUNIT_TEST_CLASS, "${class_name}Test");
		store.setDefault(P_JUNIT_TEST_METHOD, "${method_name}");
		store.setDefault(P_JUNIT_TEST_PROFILE, "default");
		store.setDefault(P_JUNIT_PROFILE_NAME, "default");
		store.setDefault(P_JUNIT_PROFILE_PATTERN, "*");
		store.setDefault(P_JUNIT_CREATE_METHOD_BODY, true);

		store.setDefault(P_JUNIT_METHOD_COMMENT, "This is a ${test_type} test for ${method_name}.");

		if (!store.contains(P_JUNIT_TEST_LOCATION)) {
			final String[][] entryNamesAndValues = getAllSourcePaths("test");
			for (final String[] entry : entryNamesAndValues) {
				if (entry[0].toUpperCase().contains("TEST")) {
					store.setDefault(P_JUNIT_TEST_LOCATION, entry[0]);
					break;
				}
			}
		}

		store.setDefault(P_JUNIT_EXCEPTION_BODY, "${exception_variable}.printStackTrace();\n"
				+ "// System.out.println(${exception_type}.class.getName());\n"
				+ "fail(\"Test for ${method_name} failed \" + ${exception_variable}.getMessage());");

		store.setDefault(P_JUNIT_NEGATIVE_BODY, "try {\n" + TAB + "${method_invocation}\n"
				+ "} catch (${exception_type} ${exception_variable}) {\n" + TAB + "//${exception_variable}.printStackTrace();\n" + TAB
				+ "return;\n" + "}\n" + "fail(\"This test should have failed\");\n");

		store.setDefault(P_GLOBAL_USER, System.getProperty("user.name", EMPTY_STR));
		store.setDefault(P_GLOBAL_DATE_FORMAT, "MM/dd/yyyy hh:mm:ss");
		//store.setDefault(P_GLOBAL_GETTER_SETTER_FOR_PRIVATE, false);
		store.setDefault(P_AUTO_SAVE, true);
		store.setDefault(P_GLOBAL_COPY_METHOD_BODY, GLOBAL_ALWAYS_CREATE);

		store.setDefault(PreferenceConstants.P_DATABASE_TYPE, EMPTY_STR);
		store.setDefault(PreferenceConstants.P_DATABASE_NAME, EMPTY_STR);
		store.setDefault(PreferenceConstants.P_HOST_ADDRESS, EMPTY_STR);
		store.setDefault(PreferenceConstants.P_PORT_NUMBER, 0);
		store.setDefault(PreferenceConstants.P_USER_NAME, EMPTY_STR);
		store.setDefault(PreferenceConstants.P_PASSWORD, EMPTY_STR);

		//store.setDefault(P_DELIMITER_FOR_FILE_TEMPLATE, "\\t");

		final String getterCustomFormat = "#set ($value = " + EMPTY_QUOTE_STR + ")\n" + "#if(${field.name.length()}>1)\n"
				+ "#set($value=${field.name.substring(0,1).toUpperCase()} +${field.name.substring(1)})\n"
				+ "#else\n#set($value=${field.name.toUpperCase()})\n#end\n"
				+ "public ${field.type.name} get$value(){\n\treturn ${field.name};}";
		final String setterCustomFormat = "#set ($value = " + EMPTY_QUOTE_STR + ")\n#if(${field.name.length()}>1)"
				+ "#set($value=${field.name.substring(0,1).toUpperCase()} +${field.name.substring(1)})\n"
				+ "#else\n#set($value=${field.name.toUpperCase()})\n#end\n"
				+ "public void set$value(${field.type.name} ${field.name}){\n\tthis.${field.name}=${field.name};}";

		store.setDefault(P_GETTER_CUSTOM_FORMAT, getterCustomFormat);
		store.setDefault(P_SETTER_CUSTOM_FORMAT, setterCustomFormat);

		store.setDefault(P_EXPORT_SETTINGS, EXPORT_OPTIONS.ASK_TO_OVERWRITE_OR_BACKUP.getValue());
		store.setDefault(P_REPOSITORY_NAME, "SVN");
		store.setDefault(P_REPOSITORY_BASE_LOCATION, "svn://localhost");
		store.setDefault(P_ENABLE_AUTO_CHECKIN, false);
		store.setDefault(P_CHECK_IN, CHECK_IN.DONOT_CHECKIN.getValue());
		store.setDefault(P_TIME_GAP_BEFORE_CHECK_IN, "15");
		store.setDefault(P_TEMPLATES_TO_ENABLE_POJO,
				"SELECT_SIMPLE\nROWMAPPER_AS_CLASS\nSELECT_WITH_ROWMAPPER_AS_METHOD\nPOJO_INSTANCE_FROM_DB_FIELD\nGET_LIST_FROM_TABLE_AS_METHOD");

		store.setDefault(P_DBCONN_RECORD_DELIMITER, SEMICOLON);
		store.setDefault(P_DBCONN_FIELD_DELIMITER, SPACE);

		store.setDefault(P_FILE_TEMPLATE_PLACHOLDER_NAME, "records");
		initTemplates(store, TEMPLATES_FOLDER, templateConfigFile, TEMPLATE, P_ALL_TEMPLATES);
		initTemplates(store, TEMPLATES_FOLDER, commonTemplateConfigFile, P_COMMON_TEMPLATE_PREFIX, P_ALL_COMMON_TEMPLATES);
		initTemplates(store, DB_TEMPLATES_FOLDER, databaseTemplateConfigFile, P_DATABASE_TEMPLATE_PREFIX, P_DATABASE_ALL_TEMPLATES);
		//initTemplates(store, FILE_TEMPLATES_FOLDER, fileTemplateConfigFile, P_FILE_TEMPLATE_PREFIX, P_FILE_ALL_TEMPLATES);

		store.setDefault(P_ALL_COMMON_VARIABLES, "Common Variables" + TAB + TAB + "Descripton" + TAB + TAB + " \n" 
											   + "${user}"  + TAB + TAB + TAB + "user name who created the class/file" + TAB + TAB + " \n" 
											   + "${today}"  + TAB + TAB + TAB + "date/time creation of the class/file" + TAB + TAB + " \n" 
											   + "${to_class}"  + TAB + TAB +  "used to hold class which is needed in dozer mapping file or copy between two classes" + TAB + TAB + " \n" 
											   + "${from_class}"  + TAB + TAB + "used to hold class which is needed in dozer mapping file or copy between two classes" + TAB + TAB + " \n" 
											   + "${class.name}"   + TAB + TAB + "name of class selected" + TAB + TAB + " \n" 
											   + "${fields}"  + TAB + TAB + TAB + "list of fields selected where each element can be accessed as ${field}" + TAB + TAB + " \n" 
											   + "${Qt}"  + TAB + TAB + TAB + TAB + "used for Quotes" + TAB + TAB + " \n" 
						);		
		updateGlobalProps();

		//		System.out.println("In class PreferenceInitializer line 523");
	}

	/**
	 * This method takes the properties from fast-code.properties and puts them
	 * in PreferenceStore. However, it checks if non default value exists in
	 * PreferenceStore and if it does not, it will update the store. So if
	 * someone changes any property in fast-code.properties it will update the
	 * store with the new value. Eclipse needs to be restarted if any property
	 * in fast-code.properties is changed.
	 *
	 */
	private void updateGlobalProps() {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		//		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);

		final Enumeration<Object> keys = globalSettings.getProperties().keys();
		Object element = keys.nextElement();
		while (keys.hasMoreElements()) {
			final String prefElement = P_GLOBAL_PROPS + UNDERSCORE + element;
			final String defalutPropertyValue = propertiesDefalutValuesMap.containsKey(element) ? propertiesDefalutValuesMap.get(element)
					: EMPTY_STR;
			final String valueFromProperties = globalSettings.getProperties().getProperty((String) element, defalutPropertyValue);
			if (store.contains(prefElement)) {
				final String valueFromPreference = store.getString((String) element);
				if (!valueFromProperties.equals(defalutPropertyValue) && !valueFromPreference.equals(valueFromProperties)) {
					store.setValue(prefElement, valueFromProperties);
				}
			} else {
				store.setValue(prefElement, valueFromProperties);
			}
			element = keys.nextElement();
		}
	}

	/**
	 * @param store
	 * @param templateResource
	 * @param templatePrefix
	 * @param allTemplatesPreferenceKey
	 *
	 * @param store
	 */
	private void initTemplates(final IPreferenceStore store, final String folderName, final String templateResource,
			final String templatePrefix, final String allTemplatesPreferenceKey) {

		InputStream inputStream = null;

		try {

			final IFile file = findOrcreateTemplate(templateResource, folderName, false);

			if (file != null && !file.isSynchronized(0)) {
				throw new Exception(templateResource + " is not Synchronized, please refresh and try again.");
			}

			if (file != null && file.exists() && file.getContents() != null) {
				inputStream = file.getContents();
			} else {
				inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + templateResource), false);
			}
			TemplateUtil.updateTemplateStore(store, inputStream, templatePrefix, allTemplatesPreferenceKey, true);
			TemplateUtil.loadTemplates(store, templatePrefix, allTemplatesPreferenceKey);
		} catch (final Exception ex) {
			ex.printStackTrace();
			MessageDialog.openError(new Shell(), "Template Error ", ex.getMessage() + "....Please retry after making the changes");
			throw new RuntimeException("Error happened while saving Templates Preference" + ex.getMessage(), ex);
		} finally {
			FastCodeUtil.closeInputStream(inputStream);
		}
	}
}

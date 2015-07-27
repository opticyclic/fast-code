/**
 *
 */
package org.fastcode.setting;

import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.DEFAULT_IMPL_EXTENSION;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.GLOBAL_ALWAYS_CREATE;
import static org.fastcode.common.FastCodeConstants.GLOBAL_ASK_TO_CREATE;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.preferences.PreferenceConstants.P_EXPORT_SETTINGS;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_ANNOTATIONS_VALUES_MAP;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_CLASS_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_CLASS_FIELD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_CLASS_HEADER;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_CLASS_METHOD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_CLASS_NAME_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_CONSTRUCTOR_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_CONVERT_METHOD_PARAM_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_COPY_METHOD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_DATE_FORMAT;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_EQUALS_METHOD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_FINAL_MODIFIER_FOR_METHOD_ARGS;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_HASHCODE_METHOD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_IMPL_SUFFIX;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_INTERFACE_METHOD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_PACKAGE_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_PARAMETERIZED_IGNORE_EXTENSIONS;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_PARAMETERIZED_NAME_STRATEGY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_PROPS;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_RELATED_CLASS;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_SOURCE_PATH_JAVA;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_SOURCE_PATH_RESOURCES;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_SOURCE_PATH_TEST;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_STATIC_IMPORT_TYPE_CHOICE;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_TOSTRING_METHOD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_TYPES_MAP;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_USER;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_USE_DEFAULT_FOR_PATH;
import static org.fastcode.preferences.PreferenceConstants.P_SHOW_TIPS;
import static org.fastcode.preferences.PreferenceConstants.P_STATIC_MEMBERS_AND_TYPES;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.Activator;
import org.fastcode.common.FastCodeConstants.CREATE_OPTIONS_CHOICE;
import org.fastcode.common.FastCodeConstants.EXPORT_OPTIONS;

/**
 * @author Gautam
 *
 */
public class GlobalSettings {

	private static final String			LOOP_TEMPLATE_LIST			= "List<${converted_param_class}> ${converted_var_name} = new ArrayList<${converted_param_class}>();"
																			+ NEWLINE
																			+ "for (${original_param_class} ${original_param_name} : ${original_var_name}) { "
																			+ NEWLINE
																			+ "\t${conversion_code}"
																			+ NEWLINE
																			+ "\t${converted_var_name}.add(${converted_param_name});"
																			+ NEWLINE + "}";

	private static final String			LOOP_TEMPLATE_MAP			= "Map<${converted_param_key_class}, ${converted_param_value_class}> ${converted_var_name} = new HashMap<${converted_param_key_class}, ${converted_param_value_class}>();"
																			+ NEWLINE
																			+ "for (${original_param_key_class} ${original_param_name} : ${original_var_name}.keySet()) { "
																			+ NEWLINE
																			+ "\t${conversion_code}"
																			+ NEWLINE
																			+ "\t${converted_var_name}.add(${converted_param_name});"
																			+ NEWLINE + "}" + NEWLINE + "return ${converted_var_name}";

	private static GlobalSettings		globalSettings				= new GlobalSettings();

	private boolean						autoSave					= true;

	private boolean						showTips					= true;

	//private boolean						getterSetterForPrivateFields	= false;

	private boolean						useDefaultForPath			= true;

	private boolean						finalModifierForMethodArgs	= true;

	private CREATE_OPTIONS_CHOICE		copyMethodBody;

	private CREATE_OPTIONS_CHOICE		createStaticImport;

	private String						packagePattern;

	private String						classNamePattern;

	private String						implExtension				= DEFAULT_IMPL_EXTENSION;

	private String						sourcePathJava;

	private String						sourcePathTest;

	private String						sourcePathResources;

	private String						constructorBody;

	private String						convertMethodParamPattern;

	private String						user;

	private String						classHeader;

	private String						classBody;

	private String						classMethodBody;

	private String						equalsMethodBody;

	private String						hashcodeMethodBody;

	private String						toStringMethodBody;

	private String						interfaceMethodBody;

	private String						fieldBody;

	private String						templateForList				= LOOP_TEMPLATE_LIST;

	private String						templateForMap				= LOOP_TEMPLATE_MAP;

	private String						dateFormat;

	private String						parameterizedNameStrategy;

	private String						parameterizedIgnoreExtensions;

	private final Map<String, String>	annotationTypesMap			= new HashMap<String, String>();

	private final Map<String, String>	annotationValuesMap			= new HashMap<String, String>();

	private Properties					properties;

	private final Map<String, String>	relatedClassMap				= new HashMap<String, String>();

	private String						staticMembersAndTypes;

	private EXPORT_OPTIONS				exportSettings;

	private GlobalSettings() {

	}

	/**
	 *
	 * @return
	 */
	public static GlobalSettings getInstance() {
		//		final IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		final IPreferenceStore preferences = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);

		globalSettings.user = getPreferenceValueString(preferences, P_GLOBAL_USER);
		globalSettings.dateFormat = getPreferenceValueString(preferences, P_GLOBAL_DATE_FORMAT);
		globalSettings.implExtension = getPreferenceValueString(preferences, P_GLOBAL_IMPL_SUFFIX);

		//globalSettings.getterSetterForPrivateFields = preferences.getBoolean(P_GLOBAL_GETTER_SETTER_FOR_PRIVATE);
		//globalSettings.strictlyMaintGS = preferences.getBoolean(P_GLOBAL_STRICTLY_MAINTAIN_GETTER_SETTER);
		//globalSettings.setEnableTemplateBodyInDialogBoxes(preferences.getBoolean(P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES));
		globalSettings.showTips = preferences.getBoolean(P_SHOW_TIPS);

		final String copyMethodBody = preferences.getString(P_GLOBAL_COPY_METHOD_BODY);
		if (copyMethodBody.equals(GLOBAL_ALWAYS_CREATE)) {
			globalSettings.setCopyMethodBody(CREATE_OPTIONS_CHOICE.ALWAYS_CREATE);
		} else if (copyMethodBody.equals(GLOBAL_ASK_TO_CREATE)) {
			globalSettings.setCopyMethodBody(CREATE_OPTIONS_CHOICE.ASK_TO_CREATE);
		} else {
			globalSettings.setCopyMethodBody(CREATE_OPTIONS_CHOICE.NEVER_CREATE);
		}
		final String staticImportTypeChoice = preferences.getString(P_GLOBAL_STATIC_IMPORT_TYPE_CHOICE);
		if (staticImportTypeChoice.equals(GLOBAL_ALWAYS_CREATE)) {
			globalSettings.setCreateStaticImport(CREATE_OPTIONS_CHOICE.ALWAYS_CREATE);
		} else if (staticImportTypeChoice.equals(GLOBAL_ASK_TO_CREATE)) {
			globalSettings.setCreateStaticImport(CREATE_OPTIONS_CHOICE.ASK_TO_CREATE);
		} else {
			globalSettings.setCreateStaticImport(CREATE_OPTIONS_CHOICE.NEVER_CREATE);
		}

		globalSettings.packagePattern = getPreferenceValueString(preferences, P_GLOBAL_PACKAGE_PATTERN);
		globalSettings.classNamePattern = getPreferenceValueString(preferences, P_GLOBAL_CLASS_NAME_PATTERN);
		globalSettings.useDefaultForPath = preferences.getBoolean(P_GLOBAL_USE_DEFAULT_FOR_PATH);
		globalSettings.classHeader = getPreferenceValueString(preferences, P_GLOBAL_CLASS_HEADER);
		globalSettings.classBody = getPreferenceValueString(preferences, P_GLOBAL_CLASS_BODY);
		globalSettings.classMethodBody = getPreferenceValueString(preferences, P_GLOBAL_CLASS_METHOD_BODY);
		globalSettings.interfaceMethodBody = getPreferenceValueString(preferences, P_GLOBAL_INTERFACE_METHOD_BODY);
		globalSettings.fieldBody = getPreferenceValueString(preferences, P_GLOBAL_CLASS_FIELD_BODY);
		globalSettings.sourcePathJava = getPreferenceValueString(preferences, P_GLOBAL_SOURCE_PATH_JAVA);
		globalSettings.convertMethodParamPattern = getPreferenceValueString(preferences, P_GLOBAL_CONVERT_METHOD_PARAM_PATTERN);
		globalSettings.sourcePathTest = getPreferenceValueString(preferences, P_GLOBAL_SOURCE_PATH_TEST);
		globalSettings.sourcePathResources = getPreferenceValueString(preferences, P_GLOBAL_SOURCE_PATH_RESOURCES);
		globalSettings.parameterizedIgnoreExtensions = getPreferenceValueString(preferences, P_GLOBAL_PARAMETERIZED_IGNORE_EXTENSIONS);
		globalSettings.constructorBody = getPreferenceValueString(preferences, P_GLOBAL_CONSTRUCTOR_BODY);
		globalSettings.equalsMethodBody = getPreferenceValueString(preferences, P_GLOBAL_EQUALS_METHOD_BODY);
		globalSettings.hashcodeMethodBody = getPreferenceValueString(preferences, P_GLOBAL_HASHCODE_METHOD_BODY);
		globalSettings.toStringMethodBody = getPreferenceValueString(preferences, P_GLOBAL_TOSTRING_METHOD_BODY);
		globalSettings.finalModifierForMethodArgs = preferences.getBoolean(P_GLOBAL_FINAL_MODIFIER_FOR_METHOD_ARGS);

		final String annotationsValues = getPreferenceValueString(preferences, P_GLOBAL_ANNOTATIONS_VALUES_MAP);
		if (annotationsValues != null) {
			final String[] annotationsValuesArr = annotationsValues.split(NEWLINE);
			for (final String annot : annotationsValuesArr) {
				final String[] parts = annot.split(COLON);
				globalSettings.annotationValuesMap.put(parts[0], parts[1]);
			}
		}

		globalSettings.readProperties();

		final String parameterizedNameStrategy = preferences.getString(P_GLOBAL_PARAMETERIZED_NAME_STRATEGY);
		globalSettings.parameterizedNameStrategy = parameterizedNameStrategy;

		if (globalSettings.annotationTypesMap.isEmpty() && preferences.contains(P_GLOBAL_TYPES_MAP)) {
			final String fullTypesMap = preferences.getString(P_GLOBAL_TYPES_MAP);
			for (final String typeMap : fullTypesMap.split(NEWLINE)) {
				final String[] pair = typeMap.split(COLON);
				if (pair.length == 2) {
					globalSettings.annotationTypesMap.put(pair[0], pair[1]);
				}
			}
		}

		if (globalSettings.relatedClassMap.isEmpty() && preferences.contains(P_GLOBAL_RELATED_CLASS)) {
			final String relatedClasses = preferences.getString(P_GLOBAL_RELATED_CLASS);
			for (final String relatedClass : relatedClasses.split(NEWLINE)) {
				final String[] pair = relatedClass.split(COLON);
				if (pair.length == 2) {
					globalSettings.relatedClassMap.put(pair[0], pair[1]);
				}
			}
		}
		globalSettings.staticMembersAndTypes = preferences.getString(P_STATIC_MEMBERS_AND_TYPES);

		final String exportSettingsType = preferences.getString(P_EXPORT_SETTINGS);

		if (exportSettingsType.equals(EXPORT_OPTIONS.OVERWRITE.getValue())) {
			globalSettings.setExportSettings(EXPORT_OPTIONS.OVERWRITE);
		} else if (exportSettingsType.equals(EXPORT_OPTIONS.BACKUP.getValue())) {
			globalSettings.setExportSettings(EXPORT_OPTIONS.BACKUP);
		}
		return globalSettings;
	}

	/**
	 * @param preferenceStore
	 */
	private static String getPreferenceValueString(final IPreferenceStore preferenceStore, final String preference) {
		return preferenceStore.contains(preference) ? preferenceStore.getString(preference) : null;
	}

	/**
	 *
	 */
	public void updateAnnotationTypesMap() {
		final String fullTypesMap = createStringFromMap(globalSettings.annotationTypesMap);
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		store.setValue(P_GLOBAL_TYPES_MAP, fullTypesMap);
	}

	/**
	 *
	 */
	public void updateRelatedClassMap() {
		final String fullTypesMap = createStringFromMap(globalSettings.relatedClassMap);
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		store.setValue(P_GLOBAL_RELATED_CLASS, fullTypesMap);
	}

	/**
	 * @return
	 */
	private String createStringFromMap(final Map<String, String> varMap) {
		final StringBuilder fullTypesMap = new StringBuilder();

		for (final Entry<String, String> entry : varMap.entrySet()) {
			final String fullName = entry.getValue();
			fullTypesMap.append(entry.getKey() + COLON + fullName + NEWLINE);
		}
		return fullTypesMap.toString().trim();
	}

	/**
	 *
	 */
	private void readProperties() {
		InputStream input = null;
		final String propertiesFile = "fast-code.properties";

		try {
			input = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + propertiesFile), false);
			this.properties = new Properties();
			this.properties.load(input);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 *
	 * @param property
	 * @return
	 */
	public String getPropertyValue(final String property, final String defaultValue) {
		final String prefElement = P_GLOBAL_PROPS + UNDERSCORE + property;
		//		final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		if (store.contains(prefElement)) {
			return store.getString(prefElement);
		}
		return this.properties.getProperty(property, defaultValue);
	}

	/**
	 * @return the autoSave
	 */
	public boolean isAutoSave() {
		return this.autoSave;
	}

	/**
	 * @param autoSave the autoSave to set
	 */
	public void setAutoSave(final boolean autoSave) {
		this.autoSave = autoSave;
	}

	public String getImplExtension() {
		return this.implExtension;
	}

	public void setImplExtension(final String implExtension) {
		this.implExtension = implExtension;
	}

	/**
	 *
	 * @return
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 *
	 * @param user
	 */
	public void setUser(final String user) {
		this.user = user;
	}

	/**
	 *
	 * @return
	 */
	public String getDateFormat() {
		return this.dateFormat;
	}

	/**
	 *
	 * @param dateFormat
	 */
	public void setDateFormat(final String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 *
	 * @return
	 */
	public CREATE_OPTIONS_CHOICE getCopyMethodBody() {
		return this.copyMethodBody;
	}

	/**
	 *
	 * @param copyMethodBody
	 */
	public void setCopyMethodBody(final CREATE_OPTIONS_CHOICE copyMethodBody) {
		this.copyMethodBody = copyMethodBody;
	}

	/**
	 *
	 * @return
	 */
	public CREATE_OPTIONS_CHOICE getCreateStaticImport() {
		return this.createStaticImport;
	}

	/**
	 *
	 * @param createStaticImport
	 */
	public void setCreateStaticImport(final CREATE_OPTIONS_CHOICE createStaticImport) {
		this.createStaticImport = createStaticImport;
	}

	/**
	 * @return the parameterizedNameStrategy
	 */
	public String getParameterizedNameStrategy() {
		return this.parameterizedNameStrategy;
	}

	/**
	 * @param k the parameterizedNameStrategy to set
	 */
	public void setParameterizedNameStrategy(final String parameterizedNameStrategy) {
		this.parameterizedNameStrategy = parameterizedNameStrategy;
	}

	/**
	 *
	 * @return
	 */
	public String getParameterizedIgnoreExtensions() {
		return this.parameterizedIgnoreExtensions;
	}

	/**
	 *
	 * @param parameterizedIgnoreExtensions
	 */
	public void setParameterizedIgnoreExtensions(final String parameterizedIgnoreExtensions) {
		this.parameterizedIgnoreExtensions = parameterizedIgnoreExtensions;
	}

	/**
	 * @return the getterSetterForPrivateFields
	 */
	/*
	public boolean isGetterSetterForPrivateFields() {
	return this.getterSetterForPrivateFields;
	}*/

	/**
	 * @param getterSetterForPrivateFields the getterSetterForPrivateFields to set
	 */
	/*
	public void setGetterSetterForPrivateFields(final boolean getterSetterForPrivateFields) {
	this.getterSetterForPrivateFields = getterSetterForPrivateFields;
	}*/

	/**
	 * @return the annotationTypesMap
	 */
	public Map<String, String> getAnnotationTypesMap() {
		return this.annotationTypesMap;
	}

	/**
	 *
	 * @return
	 */
	public Map<String, String> getRelatedClassMap() {
		return this.relatedClassMap;
	}

	/**
	 *
	 * @return
	 */
	public boolean isShowTips() {
		return this.showTips;
	}

	/**
	 *
	 * @param showTips
	 */
	public void setShowTips(final boolean showTips) {
		this.showTips = showTips;
	}

	/**
	 *
	 * @return
	 */
	public String getClassBody() {
		return this.classBody;
	}

	/**
	 *
	 * @param classInsideBody
	 */
	public void setClassBody(final String classBody) {
		this.classBody = classBody;
	}

	/**
	 *
	 * @return
	 */
	public String getClassHeader() {
		return this.classHeader;
	}

	/**
	 *
	 * @param classHeader
	 */
	public void setClassHeader(final String classHeader) {
		this.classHeader = classHeader;
	}

	/**
	 *
	 * @return
	 */
	public String getFieldBody() {
		return this.fieldBody;
	}

	/**
	 *
	 * @param fieldBody
	 */
	public void setFieldBody(final String fieldBody) {
		this.fieldBody = fieldBody;
	}

	/**
	 *
	 * @return
	 */
	public String getClassMethodBody() {
		return this.classMethodBody;
	}

	/**
	 *
	 * @param classMethodBody
	 */
	public void setClassMethodBody(final String classMethodBody) {
		this.classMethodBody = classMethodBody;
	}

	/**
	 *
	 * @return
	 */
	public String getInterfaceMethodBody() {
		return this.interfaceMethodBody;
	}

	/**
	 *
	 * @param interfaceMethodBody
	 */
	public void setInterfaceMethodBody(final String interfaceMethodBody) {
		this.interfaceMethodBody = interfaceMethodBody;
	}

	/**
	 * @param useDefaultForPath the useDefaultForPath to set
	 */
	public void setUseDefaultForPath(final boolean useDefaultForPath) {
		this.useDefaultForPath = useDefaultForPath;
	}

	/**
	 * @return the useDefaultForPath
	 */
	public boolean isUseDefaultForPath() {
		return this.useDefaultForPath;
	}

	/**
	 * @param sourcePathJava the sourcePathJava to set
	 */
	public void setSourcePathJava(final String sourcePathJava) {
		this.sourcePathJava = sourcePathJava;
	}

	/**
	 * @return the sourcePathJava
	 */
	public String getSourcePathJava() {
		return this.sourcePathJava;
	}

	/**
	 *
	 * @return
	 */
	public String getSourcePathTest() {
		return this.sourcePathTest;
	}

	/**
	 *
	 * @param sourcePathTest
	 */
	public void setSourcePathTest(final String sourcePathTest) {
		this.sourcePathTest = sourcePathTest;
	}

	/**
	 * @param sourcePathResources the sourcePathResources to set
	 */
	public void setSourcePathResources(final String sourcePathResources) {
		this.sourcePathResources = sourcePathResources;
	}

	/**
	 * @return the sourcePathResources
	 */
	public String getSourcePathResources() {
		return this.sourcePathResources;
	}

	/**
	 *
	 * @return
	 */
	public String getConvertMethodParamPattern() {
		return this.convertMethodParamPattern;
	}

	/**
	 *
	 * @param convertMethodParamPattern
	 */
	public void setConvertMethodParamPattern(final String convertMethodParamPattern) {
		this.convertMethodParamPattern = convertMethodParamPattern;
	}

	/**
	 * @param packagePattern the packagePattern to set
	 */
	public void setPackagePattern(final String packagePattern) {
		this.packagePattern = packagePattern;
	}

	/**
	 * @return the packagePattern
	 */
	public String getPackagePattern() {
		return this.packagePattern;
	}

	/**
	 * @param classNamePattern the classNamePattern to set
	 */
	public void setNameClassPattern(final String classPattern) {
		this.classNamePattern = classPattern;
	}

	/**
	 * @return the classNamePattern
	 */
	public String getNameClassPattern() {
		return this.classNamePattern;
	}

	/**
	 *
	 * @return
	 */
	public boolean isFinalModifierForMethodArgs() {
		return this.finalModifierForMethodArgs;
	}

	/**
	 *
	 * @param finalModifierForMethodArgs
	 */
	public void setFinalModifierForMethodArgs(final boolean finalModifierForMethodArgs) {
		this.finalModifierForMethodArgs = finalModifierForMethodArgs;
	}

	/**
	 *
	 * @return
	 */
	public String getTemplateForList() {
		return this.templateForList;
	}

	/**
	 *
	 * @param templateForList
	 */
	public void setTemplateForList(final String templateForList) {
		this.templateForList = templateForList;
	}

	/**
	 *
	 * @return
	 */
	public String getTemplateForMap() {
		return this.templateForMap;
	}

	/**
	 *
	 * @param templateForMap
	 */
	public void setTemplateForMap(final String templateForMap) {
		this.templateForMap = templateForMap;
	}

	/**
	 * @param constructorBody the constructorBody to set
	 */
	public void setConstructorBody(final String constructorBody) {
		this.constructorBody = constructorBody;
	}

	/**
	 * @return the constructorBody
	 */
	public String getConstructorBody() {
		return this.constructorBody;
	}

	/**
	 *
	 * @return
	 */
	public String getEqualsMethodBody() {
		return this.equalsMethodBody;
	}

	/**
	 *
	 * @param equalsMethodBody
	 */
	public void setEqualsMethodBody(final String equalsMethodBody) {
		this.equalsMethodBody = equalsMethodBody;
	}

	/**
	 *
	 * @return
	 */
	public String getHashcodeMethodBody() {
		return this.hashcodeMethodBody;
	}

	/**
	 *
	 * @param hashcodeMethodBody
	 */
	public void setHashcodeMethodBody(final String hashcodeMethodBody) {
		this.hashcodeMethodBody = hashcodeMethodBody;
	}

	/**
	 *
	 * @return
	 */
	public String getToStringMethodBody() {
		return this.toStringMethodBody;
	}

	/**
	 *
	 * @param toStringMethodBody
	 */
	public void setToStringMethodBody(final String toStringMethodBody) {
		this.toStringMethodBody = toStringMethodBody;
	}

	/**
	 *
	 * @return
	 */
	public Map<String, String> getAnnotationValuesMap() {
		return this.annotationValuesMap;
	}

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return this.properties;
	}

	public void setStaticMembersAndTypes(final String staticMembersAndTypes) {
		this.staticMembersAndTypes = staticMembersAndTypes;
	}

	public String getStaticMembersAndTypes() {
		return this.staticMembersAndTypes;
	}

	public EXPORT_OPTIONS getExportSettings() {
		return this.exportSettings;
	}

	public void setExportSettings(final EXPORT_OPTIONS exportSettings) {
		this.exportSettings = exportSettings;
	}

}

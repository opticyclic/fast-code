/**
 * @author : Gautam

 * Created : 05/07/2010

 */

package org.fastcode.setting;

import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.DEFAULT_TEMPLATE_VARIATION_FIELD;
import static org.fastcode.common.FastCodeConstants.DEFAULT_TEMPLATE_VARIATION_FIELD_NAME;
import static org.fastcode.common.FastCodeConstants.DEFAULT_TEMPLATE_VARIATION_FIELD_VALUE;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FALSE_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.TRUE_STR;
import static org.fastcode.preferences.PreferenceConstants.P_ALL_TEMPLATES;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_ALL_TEMPLATES;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ADDITIONAL_PARAMETERS;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ALLOWED_FILE_NAMES;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ALLOW_MULTIPLE_VARIATION;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_DESCRIPTION;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ENABLE_TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_FIRST_TEMPLATE_ITEM;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_GETTER_SETTER;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ITEM_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_NUMBER_REQUIRED_ITEMS;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_SECOND_TEMPLATE_ITEM;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_VARIATION;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_VARIATION_FIELD;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceUtil.getTemplatePreferenceKey;
import static org.fastcode.util.StringUtil.isEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeConstants.FIRST_TEMPLATE;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER;
import org.fastcode.common.FastCodeConstants.SECOND_TEMPLATE;
import org.w3c.dom.Node;

public class TemplateSettings {

	private static Map<String, TemplateSettings>					templateSettingMap			= new HashMap<String, TemplateSettings>();
	private static Map<String, List<String>>						enabledTemplateTypes		= new LinkedHashMap<String, List<String>>();
	private final static Map<String, Map<String, List<String>>>		enabledTemplatesMap			= new HashMap<String, Map<String, List<String>>>();
	private final static Map<String, Map<String, TemplateSettings>>	prefixTemplateSettingMap	= new HashMap<String, Map<String, TemplateSettings>>();
	//private static String templatePrefix;

	private String[]												templateVariations;
	private String[]												allowedFileNames;
	private String													templateVariationField;
	private String													templateDescription;
	private String													itemPattern;
	private boolean													allowMultipleVariation;
	private GETTER_SETTER											getterSetterRequired;
	private boolean													enabled;
	private int														numberRequiredItems;
	private String													templateBody;
	private String													templateName;
	private FIRST_TEMPLATE											firstTemplateItem;
	private SECOND_TEMPLATE											secondTemplateItem;
	public static boolean											templateSave;
	private String													additionalParamaters;

	/**
	 * @param templateName
	 *
	 */
	private TemplateSettings(final String templateName) {
		loadFromPreferenceStore(templateName);
	}

	/**
	 * @param enabled
	 * @param templateName
	 * @param itemPattern
	 * @param templateVariations
	 * @param allowedFileNames
	 * @param templateVariationField
	 * @param templateDescription
	 * @param allowMultipleVariation
	 * @param getterSetterRequired
	 * @param numberRequiredItems
	 * @param templateBody
	 * @param firstTemplateItem
	 * @param secondTemplateItem
	 * @param additionalParameters
	 */
	public TemplateSettings(final boolean enabled, final String templateName, final String itemPattern, final String[] templateVariations,
			final String[] allowedFileNames, final String templateVariationField, final String templateDescription,
			final boolean allowMultipleVariation, final GETTER_SETTER getterSetterRequired, final int numberRequiredItems,
			final String templateBody, final FIRST_TEMPLATE firstTemplateItem, final SECOND_TEMPLATE secondTemplateItem,
			final String additionalParameters) {
		super();
		this.enabled = enabled;
		this.templateName = templateName;
		this.itemPattern = itemPattern;
		this.templateVariations = templateVariations;
		this.allowedFileNames = allowedFileNames;
		this.templateVariationField = templateVariationField;
		this.templateDescription = templateDescription;
		this.allowMultipleVariation = allowMultipleVariation;
		this.getterSetterRequired = getterSetterRequired;
		this.numberRequiredItems = numberRequiredItems;
		this.templateBody = templateBody;
		this.firstTemplateItem = firstTemplateItem;
		this.secondTemplateItem = secondTemplateItem;
		this.additionalParamaters = additionalParameters;
	}

	/**
	 * @param templateName
	 */
	private void loadFromPreferenceStore(final String templateName) {
		//		final IPreferenceStore preferences = Activator.getDefault().getPreferenceStore();
		//		final IEclipsePreferences preferences = new InstanceScope().getNode(FAST_CODE_PLUGIN_ID);
		final IPreferenceStore preferences = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		this.templateName = templateName;
		String pref = preferences.getString(getTemplatePreferenceKey(templateName, P_TEMPLATE_VARIATION));
		this.templateVariations = isEmpty(pref) ? null : pref.split("\\s+");
		final String templateVariationField = FALSE_STR.equalsIgnoreCase(globalSettings.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD,
				TRUE_STR)) ? preferences.getString(getTemplatePreferenceKey(templateName, P_TEMPLATE_VARIATION_FIELD)) : globalSettings
				.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD_NAME, DEFAULT_TEMPLATE_VARIATION_FIELD_VALUE);
		this.templateVariationField = templateVariationField;
		this.templateDescription = preferences.getString(getTemplatePreferenceKey(templateName, P_TEMPLATE_DESCRIPTION));
		this.itemPattern = preferences.getString(getTemplatePreferenceKey(templateName, P_TEMPLATE_ITEM_PATTERN));
		this.allowMultipleVariation = preferences.getBoolean(getTemplatePreferenceKey(templateName, P_TEMPLATE_ALLOW_MULTIPLE_VARIATION));
		this.firstTemplateItem = FIRST_TEMPLATE.getFirstTemplate(preferences.getString(getTemplatePreferenceKey(templateName,
				P_TEMPLATE_FIRST_TEMPLATE_ITEM)));
		this.secondTemplateItem = SECOND_TEMPLATE.getSecondTemplate(preferences.getString(getTemplatePreferenceKey(templateName,
				P_TEMPLATE_SECOND_TEMPLATE_ITEM)));
		this.getterSetterRequired = GETTER_SETTER.getGetterSetter(preferences.getString(getTemplatePreferenceKey(templateName,
				P_TEMPLATE_GETTER_SETTER)));
		this.enabled = preferences.getBoolean(getTemplatePreferenceKey(templateName, P_TEMPLATE_ENABLE_TEMPLATE));
		pref = preferences.getString(getTemplatePreferenceKey(templateName, P_TEMPLATE_ALLOWED_FILE_NAMES));
		this.allowedFileNames = isEmpty(pref) ? null : pref.split("\\s+");
		this.numberRequiredItems = preferences.getInt(getTemplatePreferenceKey(templateName, P_TEMPLATE_NUMBER_REQUIRED_ITEMS));
		this.templateBody = preferences.getString(getTemplatePreferenceKey(templateName, P_TEMPLATE_BODY));
		//this.methodFieldChoice = METHOD_FIELD.getMethodField(preferences.getString(getTemplatePreferenceKey(templateName, P_TEMPLATE_METHOD_FIELD_CHOICE)));
		//final String addtnlParams = preferences.getString(getTemplatePreferenceKey(templateName, P_TEMPLATE_ADDITIONAL_PARAMETERS));
		//this.additionalParamaters = isEmpty(addtnlParams) ? null : addtnlParams.split("\\s+");
		this.additionalParamaters = preferences.getString(getTemplatePreferenceKey(templateName, P_TEMPLATE_ADDITIONAL_PARAMETERS));
	}

	/**
	 * @param template
	 * @param templatePrefix
	 * @return
	 */
	public static TemplateSettings getTemplateSettings(final String template, final String templatePrefix) {

		final Map<String, List<String>> enabledTemplateTypesForPrefix = enabledTemplatesMap.get(templatePrefix);
		if (enabledTemplateTypesForPrefix != null) {
			if (!enabledTemplatesMap.get(templatePrefix).isEmpty() && !enabledTemplatesMap.get(templatePrefix).containsKey(template)) {
				return null;
			}
		}
		if (prefixTemplateSettingMap.get(templatePrefix) == null) {
			templateSettingMap = new HashMap<String, TemplateSettings>();
			prefixTemplateSettingMap.put(templatePrefix, templateSettingMap);
		}
		if (prefixTemplateSettingMap.get(templatePrefix) != null) {
			if (!prefixTemplateSettingMap.get(templatePrefix).containsKey(template)) {
				final TemplateSettings templateSettings = new TemplateSettings(template);
				if (isEmpty(templateSettings.getTemplateBody())) {
					return null;
				}
				prefixTemplateSettingMap.get(templatePrefix).put(template, templateSettings);
			}
		}
		return prefixTemplateSettingMap.get(templatePrefix).get(template);
	}

	/**
	 *
	 * @param node
	 * @param templateType
	 * @return
	 */
	public static TemplateSettings makeTemplateSettingFromXmlNode(final Node node) {
		return null;
	}

	/**
	 *
	 * @return
	 */
	public String getTemplateVariationField() {
		return this.templateVariationField;
	}

	/**
	 *
	 * @return
	 */
	public boolean isAllowMultipleVariation() {
		return this.allowMultipleVariation;
	}

	/**
	 * @return the templateVariations
	 */
	public String[] getTemplateVariations() {
		return this.templateVariations;
	}

	/**
	 *
	 * @return
	 */
	public String[] getAllowedFileNames() {
		return this.allowedFileNames;
	}

	/**
	 *
	 * @return
	 */
	public String getTemplateBody() {
		return this.templateBody;
	}

	public int getNumberRequiredItems() {
		return this.numberRequiredItems;
	}

	/**
	 *
	 * @return
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 *
	 * @return
	 */

	public String getItemPattern() {
		return this.itemPattern;
	}

	/**
	 * @param reload the reload to set
	 */
	public static void setReload(final boolean rload) {
		templateSettingMap.clear();
		enabledTemplateTypes.clear();
		enabledTemplatesMap.clear();
		prefixTemplateSettingMap.clear();
	}

	/**
	 * @return the getterSetterRequired
	 */
	public GETTER_SETTER getGetterSetterRequired() {
		return this.getterSetterRequired;
	}

	/**
	 *
	 * @return
	 */
	public String getTemplateDescription() {
		return this.templateDescription;
	}

	/**
	 * @param fileName
	 * @param templatePrefix
	 * @return
	 */
	public static List<String> getEnabledTemplateTypes(final String fileName, final String templatePrefix) {
		final List<String> enbTemplateTypes = new ArrayList<String>();
		// TemplateSettings.templatePrefix = templatePrefix;

		if (!enabledTemplatesMap.containsKey(templatePrefix)) {
			enabledTemplateTypes = new LinkedHashMap<String, List<String>>();
			enabledTemplateTypes.putAll(getAllEnabledTemplateTypes(templatePrefix));
			enabledTemplatesMap.put(templatePrefix, enabledTemplateTypes);

		}

		for (final Entry<String, List<String>> entry : enabledTemplatesMap.get(templatePrefix).entrySet()) {
			final List<String> fileNamePatterns = entry.getValue();
			if (fileNamePatterns == null || checkFilePattern(fileName, fileNamePatterns.toArray(new String[0]))) {
				enbTemplateTypes.add(entry.getKey());
			}
		}

		return enbTemplateTypes;
	}

	/*	*//**
			*
			* getter method for enabledTemplateTypes
			* @return
			*
			*/
	/*
	public static List<String> getEnabledTemplateTypes(final String fileName, final String templatesForDB) {
	final List<String> enbTemplateTypes = new ArrayList<String>();
	if (enabledTemplateTypes.isEmpty()) {
		enabledTemplateTypes.putAll(getAllEnabledTemplateTypes(templatesForDB));
	}

	for (final Entry<String, List<String>> entry : enabledTemplateTypes.entrySet()) {
		final List<String> fileExtensions = entry.getValue();
		if (fileExtensions == null || checkFileExtension(fileName, fileExtensions.toArray(new String[0]))) {
			enbTemplateTypes.add(entry.getKey());
		}
	}
	return enbTemplateTypes;
	}

	*//**
		*
		* @return
		*/
	/*
	public static Map<String, List<String>> getAllEnabledTemplateTypes() {
	final Map<String, List<String>> allEnabledTemplateTypes = new LinkedHashMap<String, List<String>>();
	//		final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
	final String allTemplates = preferenceStore.getString(P_ALL_TEMPLATES);
	final String[] templateTypes = allTemplates.split(COLON);
	for (final String templateType : templateTypes) {
		final boolean enabled = preferenceStore.getBoolean(getTemplatePreferenceKey(templateType, P_TEMPLATE_ENABLE_TEMPLATE));
		if (!enabled) {
			continue;
		}
		final String allowedFileExtensions = preferenceStore.getString(getTemplatePreferenceKey(templateType, P_TEMPLATE_ALLOWED_FILE_EXTENSIONS));
		if (isEmpty(allowedFileExtensions)) {
			allEnabledTemplateTypes.put(templateType, null);
		} else {
			allEnabledTemplateTypes.put(templateType, Arrays.asList(allowedFileExtensions.split("\\s+")));
		}
	}
	return allEnabledTemplateTypes;

	}
	*/

	/**
	 * @param templatePrefix
	 * @return
	 */
	public static Map<String, List<String>> getAllEnabledTemplateTypes(final String templatePrefix) {
		final Map<String, List<String>> allEnabledTemplateTypes = new LinkedHashMap<String, List<String>>();
		String templatesToGet = EMPTY_STR;
		//		final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);

		if (templatePrefix.equals(TEMPLATE)) {
			templatesToGet = P_ALL_TEMPLATES;
		} else if (templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX)) {
			templatesToGet = P_DATABASE_ALL_TEMPLATES;
		}
		final String allTemplates = preferenceStore.getString(templatesToGet);
		final String[] templateTypes = allTemplates.split(COLON);
		for (final String templateType : templateTypes) {
			final boolean enabled = preferenceStore.getBoolean(getTemplatePreferenceKey(templateType, P_TEMPLATE_ENABLE_TEMPLATE));
			if (!enabled) {
				continue;
			}
			final String allowedFileNames = preferenceStore
					.getString(getTemplatePreferenceKey(templateType, P_TEMPLATE_ALLOWED_FILE_NAMES));
			if (isEmpty(allowedFileNames)) {
				allEnabledTemplateTypes.put(templateType, null);
			} else {
				allEnabledTemplateTypes.put(templateType, Arrays.asList(allowedFileNames.split("\\s+")));
			}
		}
		return allEnabledTemplateTypes;

	}

	/**
	 *
	 * @param fileName
	 * @param allowedNamePatterns
	 * @return
	 */
	public static boolean checkFilePattern(final String fileName, final String[] allowedNamePatterns) {
		//		final String[] allowedExtensionArr = allowedExtension.split("\\s+");
		for (final String allowedNamePattern : allowedNamePatterns) {
			final Pattern pattern = Pattern.compile(allowedNamePattern.replace(ASTERISK, DOT + ASTERISK));
			final Matcher matcher = pattern.matcher(fileName);
			if (matcher.matches()) {
				return true;
			}
			/*if (fileName.endsWith("." + allowedNamePattern)) {
				return true;
			}*/
		}
		return false;
	}

	/**
	 *
	 * getter method for templateName
	 * @return
	 *
	 */
	public String getTemplateName() {
		return this.templateName;
	}

	public static boolean isTemplateSave() {
		return templateSave;
	}

	public static void setTemplateSave(final boolean templateSave) {
		TemplateSettings.templateSave = templateSave;
	}

	/**
	 * @return the methodFieldChoice
	 */
	public SECOND_TEMPLATE getSecondTemplateItem() {
		return this.secondTemplateItem;
	}

	/**
	 * @return the additionalParamaters
	 */
	public String getAdditionalParamaters() {
		return this.additionalParamaters;
	}

	public FIRST_TEMPLATE getFirstTemplateItem() {
		return this.firstTemplateItem;
	}

	/**
	 * @param newTemplates
	 * @param templatePrefix
	 */
	private static void addNewTemplatesToMap(final String[] newTemplates, final String templatePrefix) {

		final Map<String, List<String>> templateTypes = new LinkedHashMap<String, List<String>>();
		templateTypes.putAll(enabledTemplatesMap.get(templatePrefix));
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		for (final String template : newTemplates) {

			final String allowedFileNames = store.getString(getTemplatePreferenceKey(template, P_TEMPLATE_ALLOWED_FILE_NAMES));
			if (isEmpty(allowedFileNames)) {
				templateTypes.put(template, null);
			} else {
				templateTypes.put(template, Arrays.asList(allowedFileNames.split("\\s+")));
			}

		}

		enabledTemplatesMap.put(templatePrefix, templateTypes);
	}

	/**
	 * @param templatePrefix
	 * @return
	 */
	private static String[] getNewTemplates(final String templatePrefix) {
		String allTemplatesPreferenceKey = null;
		if (templatePrefix.equals(TEMPLATE)) {
			allTemplatesPreferenceKey = P_ALL_TEMPLATES;
		} else if (templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX)) {
			allTemplatesPreferenceKey = P_DATABASE_ALL_TEMPLATES;
		}

		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final String allTemplates = store.getString(allTemplatesPreferenceKey);
		final ArrayList<String> allTemplatesList = new ArrayList<String>();
		for (final String s : allTemplates.split(COLON)) {
			System.out.println("tem " + s);
			allTemplatesList.add(s);
		}

		for (final Entry<String, List<String>> entry : enabledTemplatesMap.get(templatePrefix).entrySet()) {
			if (allTemplatesList.contains(entry.getKey())) {
				allTemplatesList.remove(entry.getKey());
			}

		}

		return allTemplatesList.toArray(new String[0]);

	}
}

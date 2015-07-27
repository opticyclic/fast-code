/**
 *
 */
package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_DAO;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_DAO_POJO;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_DAO_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_DAO_POJO_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_SERVICE_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_SERVICE_POJO_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_UI_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_UI_POJO_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_SERVICE;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_SERVICE_POJO;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_UI;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_UI_POJO;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_DAO_SERVICE;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_DIFFERENT_NAME;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_POJO_DAO_SERVICE;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_POJO_SERVICE_UI;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_PREFERENCE_POJO_SERVICE_UI_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID;
import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_SERVICE_UI;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.util.MultiStringFieldEditor;

/**
 * @author Gautam
 *
 */
public class PreferenceUtil {

	public static final Map<String, String>	preferenceMap	= new HashMap<String, String>();

	static {
		preferenceMap.put(CREATE_SIMILAR_PREFERENCE_DAO_SERVICE_ID, CREATE_SIMILAR_DAO_SERVICE);
		preferenceMap.put(CREATE_SIMILAR_PREFERENCE_SERVICE_UI_ID, CREATE_SIMILAR_SERVICE_UI);
		preferenceMap.put(CREATE_SIMILAR_PREFERENCE_POJO_DAO_SERVICE_ID, CREATE_SIMILAR_POJO_DAO_SERVICE);
		preferenceMap.put(CREATE_SIMILAR_PREFERENCE_POJO_SERVICE_UI_ID, CREATE_SIMILAR_POJO_SERVICE_UI);

		preferenceMap.put(CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME, CREATE_SIMILAR_DIFFERENT_NAME);

		preferenceMap.put(CREATE_NEW_PREFERENCE_DAO_ID, CREATE_NEW_DAO);
		preferenceMap.put(CREATE_NEW_PREFERENCE_SERVICE_ID, CREATE_NEW_SERVICE);
		preferenceMap.put(CREATE_NEW_PREFERENCE_UI_ID, CREATE_NEW_UI);

		preferenceMap.put(CREATE_NEW_PREFERENCE_DAO_POJO_ID, CREATE_NEW_DAO_POJO);
		preferenceMap.put(CREATE_NEW_PREFERENCE_SERVICE_POJO_ID, CREATE_NEW_SERVICE_POJO);
		preferenceMap.put(CREATE_NEW_PREFERENCE_UI_POJO_ID, CREATE_NEW_UI_POJO);

	}

	/**
	 *
	 * @param name
	 * @param actionId
	 * @return
	 */
	public static String getPreferenceStoreValueString(final String name, final String actionId, final Integer count) {
		final String preferenceName = name.replaceFirst(CREATE_SIMILAR, preferenceMap.get(actionId))
				+ (count == null ? EMPTY_STR : EMPTY_STR + count);
		// final IPreferenceStore preferenceStore =
		// Activator.getDefault().getPreferenceStore();
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		return preferenceStore.contains(preferenceName) ? preferenceStore.getString(preferenceName) : preferenceStore
				.getDefaultString(preferenceName);
	}

	/**
	 *
	 * @param name
	 * @param actionId
	 * @return
	 */
	public static String getPreferenceStoreValueString(final String name, final String actionId) {
		return getPreferenceStoreValueString(name, actionId, null);
	}

	/**
	 *
	 * @param name
	 * @param actionId
	 * @return
	 */
	public static int getPreferenceStoreValueInt(final String name, final String actionId) {
		return getPreferenceStoreValueInt(name, actionId, null);
	}

	/**
	 *
	 * @param name
	 * @param actionId
	 * @param count
	 * @return
	 */
	private static int getPreferenceStoreValueInt(final String name, final String actionId, final Integer count) {
		final String preferenceName = name.replaceFirst(CREATE_SIMILAR, preferenceMap.get(actionId))
				+ (count != null ? EMPTY_STR + count : EMPTY_STR);
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		return store.getInt(preferenceName);
	}

	/**
	 *
	 * @param name
	 * @param actionId
	 * @return
	 */
	public static Boolean getPreferenceStoreValueBoolean(final String name, final String actionId, final Integer count) {
		final String preferenceName = name.replaceFirst(CREATE_SIMILAR, preferenceMap.get(actionId))
				+ (count != null ? EMPTY_STR + count : EMPTY_STR);
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		return store.getBoolean(preferenceName);
	}

	/**
	 *
	 * @param name
	 * @param actionId
	 * @return
	 */
	public static Boolean getPreferenceStoreValueBoolean(final String name, final String actionId) {
		return getPreferenceStoreValueBoolean(name, actionId, null);
	}

	/**
	 * @param prefVariable
	 * @param label
	 * @param composite
	 * @return
	 */
	public static StringFieldEditor createStringFieldEditor(final String prefVariable, final String label, final Composite composite) {
		return new StringFieldEditor(prefVariable, label, composite);
	}

	/**
	 * @param prefVariable
	 * @param label
	 * @param composite
	 * @return
	 */
	public static MultiStringFieldEditor createMultiStringFieldEditor(final String prefVariable, final String label,
			final Composite composite) {
		return new MultiStringFieldEditor(prefVariable, label, composite);
	}

	/**
	 *
	 * @param prefVariable
	 * @param label
	 * @param composite
	 * @return
	 */
	public static BooleanFieldEditor createBooleanFieldEditor(final String prefVariable, final String label, final Composite composite) {
		return new BooleanFieldEditor(prefVariable, label, composite);
	}

	/**
	 *
	 * @param prefVariable
	 * @param label
	 * @param composite
	 * @return
	 */
	public static RadioGroupFieldEditor createRadioGroupFieldEditor(final String prefVariable, final String label, final int length,
			final String[][] types, final Composite composite, final boolean useGroup) {
		return new RadioGroupFieldEditor(prefVariable, label, length, types, composite, useGroup);
	}

	/**
	 *
	 * @param name
	 * @param actionId
	 * @return
	 */
	public static String getPreferenceLabel(final String name, final String preferenceId) {
		return name.replaceFirst(CREATE_SIMILAR, preferenceMap.get(preferenceId));
	}

	/**
	 *
	 * @param name
	 * @param preferenceId
	 * @param count
	 * @return
	 */
	public static String getPreferenceLabel(final String name, final String preferenceId, final int count) {
		return name.replaceFirst(CREATE_SIMILAR, preferenceMap.get(preferenceId)) + (count > 0 ? EMPTY_STR + count : EMPTY_STR);
	}

	/**
	 *
	 * @param template
	 * @return
	 */
	public static String getTemplatePreferenceKey(final String templateName, final String templateItem) {
		// return templateName.replaceFirst(TEMPLATE, TEMPLATE + UNDERSCORE +
		// templateName) + UNDERSCORE + templateItem;
		return templateName + UNDERSCORE + templateItem;
	}
}

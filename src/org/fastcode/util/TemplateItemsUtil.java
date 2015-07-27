package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.fastcode.Activator;

public class TemplateItemsUtil {
	private static Properties			properties;
	private static TemplateItemsUtil	templateItemsUtil	= new TemplateItemsUtil();
	Map<String, ArrayList<String>>		placeHoldersMap		= new HashMap<String, ArrayList<String>>();

	/**
	 *
	 */
	public void readProperties() {
		InputStream input = null;
		final String propertiesFile = "template-items.properties";

		try {
			input = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + propertiesFile), false);
			properties = new Properties();
			properties.load(input);
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

		return properties.getProperty(property, defaultValue);
	}

	public static TemplateItemsUtil getInstance() {
		if (properties == null) {
			templateItemsUtil.readProperties();
		}
		return templateItemsUtil;
	}

	public ArrayList<String> getPlaceHoldersList(final String currentFirstTemplateItemValue, final String currentSecondTemplateItemValue) {

		final String key = currentFirstTemplateItemValue + UNDERSCORE + currentSecondTemplateItemValue;
		if (!this.placeHoldersMap.containsKey(key)) {
			final String builtInvariables = templateItemsUtil.getPropertyValue(key.toLowerCase(), EMPTY_STR);
			final ArrayList<String> builtInvariablesList = new ArrayList<String>();
			for (final String variable : builtInvariables.split(COMMA)) {
				builtInvariablesList.add(variable.trim());
			}
			this.placeHoldersMap.put(key, builtInvariablesList);
		}
		return this.placeHoldersMap.get(key);

	}

}

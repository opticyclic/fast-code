package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.util.FileUtil.retrievePropertiesFromFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DefaultTemplatesManager {

	private static Properties				properties			= null;
	private static DefaultTemplatesManager	templatemanager		= null;

	Map<String, ArrayList<String>>			defaultTemplatesMap	= new HashMap<String, ArrayList<String>>();

	public static DefaultTemplatesManager getInstance() {

		if (templatemanager == null) {
			templatemanager = new DefaultTemplatesManager();
			properties = new Properties();
			try {
				properties = retrievePropertiesFromFile("resources/default-templates.properties");
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		return templatemanager;

	}

	/*private static Properties readProperties() {

		final String propertiesFile = "default-templates.properties";

		try {
			final InputStream input = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + propertiesFile),
					false);

			properties.load(input);

		} catch (final Exception ex) {
			ex.printStackTrace();
		}

		return properties;
	}*/

	public ArrayList<String> getDefaultTemplates(final String templatePrefix) {

		if (!this.defaultTemplatesMap.containsKey(templatePrefix)) {

			readTemplatesFromProperties(templatePrefix);

		}

		return this.defaultTemplatesMap.get(templatePrefix);
	}

	private void readTemplatesFromProperties(final String templatePrefix) {
		final ArrayList<String> templatesList = new ArrayList<String>();
		final String[] templates = properties.getProperty(templatePrefix).split(COMMA);
		for (int i = 0; i < templates.length; i++) {
			templatesList.add(templates[i].trim());
		}
		this.defaultTemplatesMap.put(templatePrefix, templatesList);
	}

}

/**
 *
 */
package org.fastcode.setting;

/**
 * @author Gautam
 *
 */
public class TemplateSettingsContainer {

	private final TemplateSettings	templateSettings;
	private final TemplateSettings	exstTemplateSettings;

	/**
	 * @param templateSettings
	 * @param exstTemplateSettings
	 */
	public TemplateSettingsContainer(final TemplateSettings templateSettings, final TemplateSettings exstTemplateSettings) {
		super();
		this.templateSettings = templateSettings;
		this.exstTemplateSettings = exstTemplateSettings;
	}

	/**
	 *
	 * getter method for templateSettings
	 *
	 * @return
	 *
	 */
	public TemplateSettings getTemplateSettings() {
		return this.templateSettings;
	}

	/**
	 *
	 * getter method for exstTemplateSettings
	 *
	 * @return
	 *
	 */
	public TemplateSettings getExstTemplateSettings() {
		return this.exstTemplateSettings;
	}

}

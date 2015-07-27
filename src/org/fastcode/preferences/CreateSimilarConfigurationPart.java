/**
 *
 */
package org.fastcode.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Gautam
 *
 */
public class CreateSimilarConfigurationPart {

	private final RadioGroupFieldEditor[]	configTypeRadio;
	private final StringFieldEditor[]		configLocation;
	private final StringFieldEditor[]		configLocale;

	private final StringFieldEditor[]		configBodyPattern;
	private final StringFieldEditor[]		configStartPattern;
	private final StringFieldEditor[]		configHeaderPattern;
	private final StringFieldEditor[]		configEndPattern;

	private final StringFieldEditor[]		configFile;
	private final RadioGroupFieldEditor[]	configFileNameConversion;

	/**
	 *
	 * @param numMaxConfigs
	 */
	public CreateSimilarConfigurationPart(final int numMaxConfigs) {
		this.configTypeRadio = new RadioGroupFieldEditor[numMaxConfigs];
		this.configLocation = new StringFieldEditor[numMaxConfigs];
		this.configLocale = new StringFieldEditor[numMaxConfigs];
		this.configBodyPattern = new StringFieldEditor[numMaxConfigs];
		this.configHeaderPattern = new StringFieldEditor[numMaxConfigs];
		this.configStartPattern = new StringFieldEditor[numMaxConfigs];
		this.configEndPattern = new StringFieldEditor[numMaxConfigs];
		this.configFile = new StringFieldEditor[numMaxConfigs];
		this.configFileNameConversion = new RadioGroupFieldEditor[numMaxConfigs];
	}

	/**
	 *
	 * @param configCount
	 * @param enabled
	 * @param parent
	 */
	public void enableConfigParts(final int configCount, final boolean enabled, final Composite parent) {
		// configTypeRadio[configCount].setEnabled(enabled, parent);
		this.configLocation[configCount].setEnabled(enabled, parent);
		if (this.configLocale[configCount] != null) {
			this.configLocale[configCount].setEnabled(enabled, parent);
		}
		this.configBodyPattern[configCount].setEnabled(enabled, parent);
		this.configHeaderPattern[configCount].setEnabled(enabled, parent);
		this.configStartPattern[configCount].setEnabled(enabled, parent);
		this.configEndPattern[configCount].setEnabled(enabled, parent);
		this.configFile[configCount].setEnabled(enabled, parent);
		this.configFileNameConversion[configCount].setEnabled(enabled, parent);
	}

	/**
	 * @return the configTypeRadio
	 */
	public RadioGroupFieldEditor getConfigTypeRadio(final int index) {
		return this.configTypeRadio[index];
	}

	/**
	 * @param configTypeRadio
	 *            the configTypeRadio to set
	 */
	public void setConfigTypeRadio(final int index, final RadioGroupFieldEditor configTypeRadio) {
		if (index >= this.configTypeRadio.length) {
			MessageDialog.openError(new Shell(), "Error", "Array index out of bounds " + index + "  " + this.configTypeRadio.length);
			return;
		}
		this.configTypeRadio[index] = configTypeRadio;
	}

	/**
	 * @return the configLocation
	 */
	public StringFieldEditor getConfigLocation(final int index) {
		return this.configLocation[index];
	}

	/**
	 * @param configLocation
	 *            the configLocation to set
	 */
	public void setConfigLocation(final int index, final StringFieldEditor configLocation) {
		this.configLocation[index] = configLocation;
	}

	/**
	 * @return the configLocale
	 */
	public StringFieldEditor getConfigLocale(final int index) {
		return this.configLocale[index];
	}

	/**
	 * @param configLocale
	 *            the configLocale to set
	 */
	public void setConfigLocale(final int index, final StringFieldEditor configLocale) {
		this.configLocale[index] = configLocale;
	}

	/**
	 * @return the configBodyPattern
	 */
	public StringFieldEditor getConfigBodyPattern(final int index) {
		return this.configBodyPattern[index];
	}

	/**
	 * @param configBodyPattern
	 *            the configBodyPattern to set
	 */
	public void setConfigBodyPattern(final int index, final StringFieldEditor configBodyPattern) {
		this.configBodyPattern[index] = configBodyPattern;
	}

	/**
	 * @return the configStartPattern
	 */
	public StringFieldEditor getConfigStartPattern(final int index) {
		return this.configStartPattern[index];
	}

	/**
	 * @param configStartPattern
	 *            the configStartPattern to set
	 */
	public void setConfigStartPattern(final int index, final StringFieldEditor configStartPattern) {
		this.configStartPattern[index] = configStartPattern;
	}

	/**
	 * @return the configEndPattern
	 */
	public StringFieldEditor getConfigEndPattern(final int index) {
		return this.configEndPattern[index];
	}

	/**
	 * @param configEndPattern
	 *            the configEndPattern to set
	 */
	public void setConfigEndPattern(final int index, final StringFieldEditor configEndPattern) {
		this.configEndPattern[index] = configEndPattern;
	}

	/**
	 * @return the configFile
	 */
	public StringFieldEditor getConfigFile(final int index) {
		return this.configFile[index];
	}

	/**
	 * @param configFile
	 *            the configFile to set
	 */
	public void setConfigFile(final int index, final StringFieldEditor configFile) {
		this.configFile[index] = configFile;
	}

	/**
	 * @return the configFileNameConversion
	 */
	public RadioGroupFieldEditor getConfigFileNameConversion(final int index) {
		return this.configFileNameConversion[index];
	}

	/**
	 * @param configFileNameConversion
	 *            the configFileNameConversion to set
	 */
	public void setConfigFileNameConversion(final int index, final RadioGroupFieldEditor configFileNameConversion) {
		this.configFileNameConversion[index] = configFileNameConversion;
	}

	/**
	 * @return the configHeaderPattern
	 */
	public StringFieldEditor getConfigHeaderPattern(final int index) {
		return this.configHeaderPattern[index];
	}

	/**
	 * @param configHeaderPattern
	 *            the configHeaderPattern to set
	 */
	public void setConfigHeaderPattern(final int index, final StringFieldEditor configHeaderPattern) {
		this.configHeaderPattern[index] = configHeaderPattern;
	}
}

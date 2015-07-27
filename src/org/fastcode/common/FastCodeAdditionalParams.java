package org.fastcode.common;

import org.fastcode.common.FastCodeConstants.RETURN_TYPES;

public class FastCodeAdditionalParams {

	private String			name;
	private RETURN_TYPES	returnTypes;
	private String			defaultValue;
	private String			required;
	private String			pattern;
	private String			project;
	private String			allowedValues;
	private String			label;
	private String			enabled;
	private String			min;
	private String			max;
	private String			type;
	private String			dependsOn;

	public FastCodeAdditionalParams(final String name, final RETURN_TYPES returnTypes, final String defaultValue, final String required,
			final String enabled) {
		super();
		this.name = name;
		this.returnTypes = returnTypes;
		this.defaultValue = defaultValue;
		this.required = required;
		this.enabled = enabled;
	}

	public FastCodeAdditionalParams(final String name, final RETURN_TYPES returnTypes, final String defaultValue, final String required,
			final String pattern, final String project, final String allowedValues, final String label, final String enabled,
			final String min, final String max, final String type, final String dependsOn) {
		super();
		this.name = name;
		this.returnTypes = returnTypes;
		this.defaultValue = defaultValue;
		this.required = required;
		this.project = project;
		this.pattern = pattern;
		this.allowedValues = allowedValues;
		this.label = label;
		this.enabled = enabled;
		this.max = max;
		this.min = min;
		this.type = type;
		this.dependsOn = dependsOn;
	}

	public String getMin() {
		return this.min;
	}

	public String getMax() {
		return this.max;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public RETURN_TYPES getReturnTypes() {
		return this.returnTypes;
	}

	public void setReturnTypes(final RETURN_TYPES returnTypes) {
		this.returnTypes = returnTypes;
	}

	public String getDefaultValue() {
		return this.defaultValue;
	}

	public void setDefaultValue(final String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 *
	 * getter method for required
	 * @return
	 *
	 */
	public String getRequired() {
		return this.required;
	}

	/**
	 *
	 * setter method for required
	 * @param required
	 *
	 */
	public void setRequired(final String required) {
		this.required = required;
	}

	/**
	 *
	 * getter method for pattern
	 * @return
	 *
	 */
	public String getPattern() {
		return this.pattern;
	}

	/**
	 *
	 * setter method for pattern
	 * @param pattern
	 *
	 */
	public void setPattern(final String pattern) {
		this.pattern = pattern;
	}

	/**
	 *
	 * getter method for project
	 * @return
	 *
	 */
	public String getProject() {
		return this.project;
	}

	/**
	 *
	 * setter method for project
	 * @param project
	 *
	 */
	public void setProject(final String project) {
		this.project = project;
	}

	/**
	 *
	 * getter method for allowedValues
	 * @return
	 *
	 */
	public String getAllowedValues() {
		return this.allowedValues;
	}

	/**
	 *
	 * setter method for allowedValues
	 * @param allowedValues
	 *
	 */
	public void setAllowedValues(final String allowedValues) {
		this.allowedValues = allowedValues;
	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	/**
	 *
	 * getter method for enabled
	 * @return
	 *
	 */
	public String getEnabled() {
		return this.enabled;
	}

	/**
	 *
	 * setter method for enabled
	 * @param enabled
	 *
	 */
	public void setEnabled(final String enabled) {
		this.enabled = enabled;
	}

	/**
	 *
	 * getter method for type
	 * @return
	 *
	 */
	public String getType() {
		return this.type;
	}

	/**
	 *
	 * setter method for type
	 * @param type
	 *
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 *
	 * getter method for dependsOn
	 * @return
	 *
	 */
	public String getDependsOn() {
		return this.dependsOn;
	}

	/**
	 *
	 * setter method for dependsOn
	 * @param dependsOn
	 *
	 */
	public void setDependsOn(final String dependsOn) {
		this.dependsOn = dependsOn;
	}
}

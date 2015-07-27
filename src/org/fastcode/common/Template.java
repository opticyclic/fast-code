package org.fastcode.common;

public class Template implements Comparable<Template> {

	String	templateName;
	String	Description;
	String	templateVariation;
	String	allowedFileNames;
	String	templateBody;
	boolean	templateEnabled;
	boolean	templateDeleted;

	boolean	multipleVariationCheckbox;
	String	firstTemplateItem;
	String	secondTemplateItem;
	String	requiredClassItem;
	String	getterSetterItem;
	String	additionalParameters;
	String	itemPattern;

	public Template(final String templateName, final String description, final String templateVariation, final String allowedFileNames,
			final String templateBody, final boolean templateEnabled, final boolean isdeleted) {

		this.templateName = templateName;
		this.Description = description;
		this.templateVariation = templateVariation;
		this.allowedFileNames = allowedFileNames;

		this.templateBody = templateBody;
		this.templateEnabled = templateEnabled;
		this.templateDeleted = isdeleted;

	}

	public Template(final String templateName, final String description, final String templateVariation, final String allowedFileNames,
			final String templateBody, final boolean templateEnabled, final String firstTemplateItem, final String secondTemplateItem,
			final String requiredClassItem, final String getterSetterItem, final String additionalParameters,
			final boolean multipleVariationCheckbox, final String itemPattern, final boolean templateDeleted) {

		this.templateName = templateName;
		this.Description = description;
		this.templateVariation = templateVariation;
		this.allowedFileNames = allowedFileNames;

		this.templateBody = templateBody;
		this.templateEnabled = templateEnabled;
		this.templateDeleted = templateDeleted;
		this.firstTemplateItem = firstTemplateItem;
		this.secondTemplateItem = secondTemplateItem;
		this.requiredClassItem = requiredClassItem;
		this.getterSetterItem = getterSetterItem;
		this.additionalParameters = additionalParameters;
		this.multipleVariationCheckbox = multipleVariationCheckbox;
		this.itemPattern = itemPattern;
	}

	public String getItemPattern() {
		return this.itemPattern;
	}

	public void setItemPattern(final String itemPattern) {
		this.itemPattern = itemPattern;
	}

	public boolean isMultipleVariationCheckbox() {
		return this.multipleVariationCheckbox;
	}

	public void setMultipleVariationCheckbox(final boolean multipleVariationCheckbox) {
		this.multipleVariationCheckbox = multipleVariationCheckbox;
	}

	public boolean isTemplateDeleted() {
		return this.templateDeleted;
	}

	public void setTemplateDeleted(final boolean templateDeleted) {
		this.templateDeleted = templateDeleted;
	}

	public Template() {
		// TODO Auto-generated constructor stub
	}

	public boolean isTemplateEnabled() {
		return this.templateEnabled;
	}

	public void setTemplateEnabled(final boolean templateEnabled) {
		this.templateEnabled = templateEnabled;
	}

	public String getTemplateName() {
		return this.templateName;
	}

	public String getTemplateVariation() {
		return this.templateVariation;
	}

	public void setTemplateVariation(final String templateVariation) {
		this.templateVariation = templateVariation;
	}

	public String getAllowedFileNames() {
		return this.allowedFileNames;
	}

	public void setAllowedFileNames(final String allowedFileNames) {
		this.allowedFileNames = allowedFileNames;
	}

	public String getTemplateBody() {
		return this.templateBody;
	}

	public void setTemplateBody(final String templateBody) {
		this.templateBody = templateBody;
	}

	public void setTemplateName(final String templateName) {
		this.templateName = templateName;
	}

	public String getDescription() {
		return this.Description;
	}

	public void setDescription(final String description) {
		this.Description = description;
	}

	public String getFirstTemplateItem() {
		return this.firstTemplateItem;
	}

	public void setFirstTemplateItem(final String firstTemplateItem) {
		this.firstTemplateItem = firstTemplateItem;
	}

	public String getSecondTemplateItem() {
		return this.secondTemplateItem;
	}

	public void setSecondTemplateItem(final String secondTemplateItem) {
		this.secondTemplateItem = secondTemplateItem;
	}

	public String getRequiredClassItem() {
		return this.requiredClassItem;
	}

	public void setRequiredClassItem(final String requiredClassItem) {
		this.requiredClassItem = requiredClassItem;
	}

	public String getGetterSetterItem() {
		return this.getterSetterItem;
	}

	public void setGetterSetterItem(final String getterSetterItem) {
		this.getterSetterItem = getterSetterItem;
	}

	public String getAdditionalParameters() {
		return this.additionalParameters;
	}

	public void setAdditionalParameters(final String additionalParameters) {
		this.additionalParameters = additionalParameters;
	}

	@Override
	public int compareTo(final Template template) {
		return getTemplateName().compareToIgnoreCase(template.getTemplateName());
	}

	// @Override
	// public boolean equals(final Object object) {
	// if (this.templateName.equals(((Template) object).getTemplateName())) {
	// return true;
	//
	// } else {
	// return false;
	// }
	//
	// }

}

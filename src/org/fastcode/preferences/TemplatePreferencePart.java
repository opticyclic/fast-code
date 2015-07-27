/**
 *
 */
package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ADDITIONAL_PARAMETERS;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ALLOWED_FILE_NAMES;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ALLOW_MULTIPLE_VARIATION;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_DESCRIPTION;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ENABLE_TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_FIRST_TEMPLATE_ITEM;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_GETTER_SETTER;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ITEM_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_NAME;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_NUMBER_REQUIRED_ITEMS;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_SECOND_TEMPLATE_ITEM;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_VARIATION;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_VARIATION_FIELD;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceUtil.getTemplatePreferenceKey;
import static org.fastcode.util.TemplateUtil.makeTemplateLabel;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.fastcode.common.FastCodeConstants.FIELDS;
import org.fastcode.templates.viewer.TemplateFieldEditor;

/**
 * @author Gautam
 *
 */
public class TemplatePreferencePart {

	private static final String[][]	GETTER_SETTER_TYPES			= { { "Getter Setter Both", "gettersetter" }, { "Getter Only", "getter" },
			{ "Setter Only", "setter" }, { "None", "none" }	};
	private static final String[][]	NUMBER_OF_REQUIRED_ITMES	= { { "0", "0" }, { "1", "1" }, { "2", "2" } };
	private static final String[][]	SECOND_TEMPLATE_CHOICE		= { { "Field", "field" }, { "Method", "method" },
			{ "Method/Field", "both" }, { "Custom", "custom" }, { "Class", "class" }, { "File", "file" }, { "Property", "property" },
			{ "Data", "data" }, { "None", "none" }				};
	private static final String[][]	FIRST_TEMPLATE_CHOICE		= { { "Class", "class" }, { "File", "file" }, { "Package", "package" },
			{ "Folder", "folder" }, { "Enumeration", "enum" }, { "None", "none" } };
	final String					templateName;
	BooleanFieldEditor				enableTemplateCheckBox;
	StringFieldEditor				templateVariationField;
	StringFieldEditor				templateDescriptionField;
	StringFieldEditor				templateVariationFieldNameField;
	StringFieldEditor				itemPatternField;
	BooleanFieldEditor				allowMultipleVariationCheckBox;
	RadioGroupFieldEditor			getterSetterRadioButton;
	StringFieldEditor				templateAllowedFileNamesField;
	RadioGroupFieldEditor			numberOfRequiredClassesField;
	// MultiStringFieldEditor templateBodyField;
	TemplateFieldEditor				templateBodyField;
	FileFieldEditor					templateFileField;
	RadioGroupFieldEditor			secondTemplateRadioButton;
	RadioGroupFieldEditor			firstTemplateRadioButton;
	TemplateFieldEditor				additionalParametersField;

	final boolean					enabled;
	StringFieldEditor				templateNameField;

	/**
	 *
	 * @param template
	 */
	public TemplatePreferencePart(final String templateName) {
		this.templateName = templateName;
		final IEclipsePreferences preferences = new InstanceScope().getNode(FAST_CODE_PLUGIN_ID);
		// this.enabled =
		// Activator.getDefault().getPreferenceStore().getBoolean(getTemplatePreferenceKey(this.template,
		// P_TEMPLATE_ENABLE_TEMPLATE));
		this.enabled = preferences.getBoolean(getTemplatePreferenceKey(this.templateName, P_TEMPLATE_ENABLE_TEMPLATE), false);
	}

	/**
	 *
	 * @param parent
	 * @param additionalTemplate
	 * @return
	 */
	FieldEditor createEnableTemplateCheckBox(final Composite parent, final boolean additionalTemplate, final String templatePrefix) {
		return this.enableTemplateCheckBox = new BooleanFieldEditor(
				getTemplatePreferenceKey(this.templateName, P_TEMPLATE_ENABLE_TEMPLATE), makeTemplateLabel(this.templateName,
						templatePrefix), parent);
	}

	/**
	 *
	 * @param parent
	 */
	public FieldEditor createTemplateVariationField(final Composite parent) {
		return this.templateVariationField = new StringFieldEditor(getTemplatePreferenceKey(this.templateName, P_TEMPLATE_VARIATION),
				" Variations", parent);
	}

	public FieldEditor createTemplateDescriptionField(final Composite parent) {
		return this.templateDescriptionField = new StringFieldEditor(getTemplatePreferenceKey(this.templateName, P_TEMPLATE_DESCRIPTION),
				" Description", parent);
	}

	public FieldEditor createTemplateNameField(final Composite parent) {

		return this.templateNameField = new StringFieldEditor(getTemplatePreferenceKey(this.templateName, P_TEMPLATE_NAME),
				" Template Name", parent);

	}

	/**
	 *
	 * @param parent
	 * @return
	 */
	FieldEditor createItemPatternField(final Composite parent) {
		return this.itemPatternField = new StringFieldEditor(getTemplatePreferenceKey(this.templateName, P_TEMPLATE_ITEM_PATTERN),
				" First Template Item Pattern", parent);
	}

	/**
	 *
	 * @param parent
	 * @return
	 */
	FieldEditor createTemplateVariationFieldNameField(final Composite parent) {
		return this.templateVariationFieldNameField = new StringFieldEditor(getTemplatePreferenceKey(this.templateName,
				P_TEMPLATE_VARIATION_FIELD), " Variation Field", parent);
	}

	/**
	 *
	 * @param parent
	 */
	FieldEditor createAllowMultipleVariationCheckBox(final Composite parent) {
		return this.allowMultipleVariationCheckBox = new BooleanFieldEditor(getTemplatePreferenceKey(this.templateName,
				P_TEMPLATE_ALLOW_MULTIPLE_VARIATION),
				" Allow Multiple Variation                                                                                         ",
				parent);
	}

	/**
	 *
	 * @param parent
	 */
	FieldEditor createTemplateAllowedFileExtensionsField(final Composite parent) {
		return this.templateAllowedFileNamesField = new StringFieldEditor(getTemplatePreferenceKey(this.templateName,
				P_TEMPLATE_ALLOWED_FILE_NAMES), " Allowed File Names", parent);
	}

	/**
	 *
	 * @param parent
	 * @return
	 */
	FieldEditor createNumberOfRequiredClassesField(final Composite parent) {
		return this.numberOfRequiredClassesField = new RadioGroupFieldEditor(getTemplatePreferenceKey(this.templateName,
				P_TEMPLATE_NUMBER_REQUIRED_ITEMS), " Number Required Items", NUMBER_OF_REQUIRED_ITMES.length, NUMBER_OF_REQUIRED_ITMES,
				parent, true);

	}

	/**
	 *
	 * @param parent
	 * @return
	 */
	/*FieldEditor createTemplateBodyField(final Composite parent) {
		return this.templateBodyField = new MultiStringFieldEditor(getTemplatePreferenceKey(this.template, P_TEMPLATE_BODY), "Template Body", parent);
	}*/

	TemplateFieldEditor createTemplateBodyField(final Composite parent, final String templatePrefix) {
		final GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 2;
		if (templatePrefix.equals(TEMPLATE)) {
			gd.heightHint = 150;
		} else {
			gd.heightHint = 300;
		}
		gd.grabExcessVerticalSpace = true;
		this.templateBodyField = new TemplateFieldEditor(getTemplatePreferenceKey(this.templateName, P_TEMPLATE_BODY), " Template Body",
				parent, templatePrefix, FIELDS.TEMPLATE_BODY, SWT.MULTI);
		this.templateBodyField.getControl().setLayoutData(gd);

		final GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
		gd1.grabExcessHorizontalSpace = false;
		gd1.horizontalSpan = 2;
		this.templateBodyField.getLabelControl(parent).setLayoutData(gd1);
		return this.templateBodyField;

	}

	/**
	 *
	 * @param parent
	 * @return
	 */
	/*FieldEditor createTemplateFileField(final Composite parent) {
		return this.templateFileField = new FileFieldEditor(getTemplatePreferenceKey(this.template, P_TEMPLATE_FILE), "Load Template Body from File", true,
				parent);
	}*/

	/**
	 *
	 * @param parent
	 * @return
	 */
	FieldEditor createFirstTemplateRadioButton(final Composite parent) {
		return this.firstTemplateRadioButton = new RadioGroupFieldEditor(getTemplatePreferenceKey(this.templateName,
				P_TEMPLATE_FIRST_TEMPLATE_ITEM), "First Template Item", FIRST_TEMPLATE_CHOICE.length, FIRST_TEMPLATE_CHOICE, parent, true);

	}

	/**
	 *
	 * @param parent
	 * @return
	 */
	FieldEditor createSecondTemplateRadioButton(final Composite parent) {
		return this.secondTemplateRadioButton = new RadioGroupFieldEditor(getTemplatePreferenceKey(this.templateName,
				P_TEMPLATE_SECOND_TEMPLATE_ITEM), "Second Template Item", SECOND_TEMPLATE_CHOICE.length, SECOND_TEMPLATE_CHOICE, parent,
				true);

	}

	/**
	 *
	 * @param parent
	 * @return
	 */
	FieldEditor createGetterSetterRadioButton(final Composite parent) {
		return this.getterSetterRadioButton = new RadioGroupFieldEditor(getTemplatePreferenceKey(this.templateName,
				P_TEMPLATE_GETTER_SETTER), "Required Getter Setter", GETTER_SETTER_TYPES.length, GETTER_SETTER_TYPES, parent, true);
	}

	/**
	 *
	 * @param parent
	 * @return
	 */
	FieldEditor createAdditionalParametersField(final Composite parent, final String templatePrefix) {
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.minimumHeight = 15;
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalSpan = 2;
		this.additionalParametersField = new TemplateFieldEditor(getTemplatePreferenceKey(this.templateName,
				P_TEMPLATE_ADDITIONAL_PARAMETERS), " Additional Parameters", parent, templatePrefix, FIELDS.ADDITIONAL_PARAMETER, SWT.SINGLE);
		this.additionalParametersField.getControl().setLayoutData(gd);
		final GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
		gd1.minimumHeight = 15;
		gd1.grabExcessHorizontalSpace = false;
		gd1.horizontalSpan = 2;
		this.additionalParametersField.getLabelControl(parent).setLayoutData(gd1);
		return this.additionalParametersField;
	}

	/**
	 * @param parent
	 * @param additionalTemplate
	 * @param templatePrefix
	 * @return
	 */
	public BooleanFieldEditor createEnableTemplateCheckBoxForNewTemplate(final Composite parent, final boolean additionalTemplate,
			final String templatePrefix) {
		return this.enableTemplateCheckBox = new BooleanFieldEditor(
				getTemplatePreferenceKey(this.templateName, P_TEMPLATE_ENABLE_TEMPLATE),
				"Template name (Check to enable and include the Template)", parent);
	}
}

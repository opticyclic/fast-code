package org.fastcode.preferences;

import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.DB_TEMPLATES_FOLDER;
import static org.fastcode.common.FastCodeConstants.DEFAULT_TEMPLATE_VARIATION_FIELD;
import static org.fastcode.common.FastCodeConstants.FALSE_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.TEMPLATES_FOLDER;
import static org.fastcode.common.FastCodeConstants.TRUE_STR;
import static org.fastcode.preferences.PreferenceConstants.P_ALL_COMMON_TEMPLATES;
import static org.fastcode.preferences.PreferenceConstants.P_ALL_TEMPLATES;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_ALL_TEMPLATES;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ENABLE_TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_FIRST_TEMPLATE_ITEM;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_GETTER_SETTER;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_SECOND_TEMPLATE_ITEM;
import static org.fastcode.preferences.PreferenceUtil.getTemplatePreferenceKey;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.parseAdditonalParam;
import static org.fastcode.util.XMLUtil.exportXML;
import static org.fastcode.util.XMLUtil.importXML;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeConstants.FIRST_TEMPLATE;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER;
import org.fastcode.common.FastCodeConstants.SECOND_TEMPLATE;
import org.fastcode.common.FastCodeConstants.TEMPLATE_PREFERENCE_NAME;
import org.fastcode.exception.FastCodeException;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.setting.TemplateSettings;

public class AbstractTemplatePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	final String							allTemplates;
	final String[]							TEMPLATES_LIST;
	final boolean							commonTemplate;

	private final TemplatePreferencePart[]	templatePreferenceParts;
	protected String						templatePrefix;
	private String							currentValueOf1stTemplateItem	= null;
	private String							currentValueOf2ndTemplateItem	= null;
	private Button							importButton;
	private Button							exportButton;
	private boolean							pkgSelected;
	private boolean							fldrSelected;
	private boolean							classSelected;
	private boolean							fileSelected;
	private boolean							enumSelected;
	private String							prefValueOf1stTemplateItem;
	private String							prefValueOf2ndTempalteItem;

	/**
	 *
	 */
	public AbstractTemplatePreferencePage() {
		super(GRID);
		this.commonTemplate = P_ALL_COMMON_TEMPLATES.equals(getAllTemplatesPreferenceKey());
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		setPreferenceStore(store);
		this.allTemplates = store.getString(getAllTemplatesPreferenceKey());
		this.TEMPLATES_LIST = this.allTemplates.split(COLON);
		this.templatePreferenceParts = new TemplatePreferencePart[this.TEMPLATES_LIST.length];
		setDescription("Template preference");
		//System.out.println("In class TemplatePreferencePage line 62");
	}

	/**
	 *
	 */
	@Override
	public void createFieldEditors() {
		// IApplicationContext ctx = FastCodeContext
		// IRulesStrategy[] editRuleStrategies =
		// ctx.getTemplateRuleStrategies();
		int i = 0;
		for (final String template : this.TEMPLATES_LIST) {
			// System.out.println("template = " + template);
			if (isEmpty(template)) {

				continue;
			}
			final GlobalSettings globalSettings = GlobalSettings.getInstance();
			final TemplatePreferencePart templatePreferencePart = this.templatePreferenceParts[i++] = new TemplatePreferencePart(template);
			addField(templatePreferencePart.createEnableTemplateCheckBox(getFieldEditorParent(), this.commonTemplate, this.templatePrefix));
			addField(templatePreferencePart.createTemplateDescriptionField(getFieldEditorParent()));
			addField(templatePreferencePart.createTemplateVariationField(getFieldEditorParent()));
			if (FALSE_STR.equalsIgnoreCase(globalSettings.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD, TRUE_STR))) {
				addField(templatePreferencePart.createTemplateVariationFieldNameField(getFieldEditorParent()));
			}

			if (isShowAllowedFileExtension()) {
				addField(templatePreferencePart.createTemplateAllowedFileExtensionsField(getFieldEditorParent()));
			}
			if (isDetailedTemplate()) {
				// addField(templatePreferencePart.createClassNameField(getFieldEditorParent()));
				addField(templatePreferencePart.createAllowMultipleVariationCheckBox(getFieldEditorParent()));
				// templatePreferencePart.templateAllowedFileExtensionsField.setEmptyStringAllowed(false);
				addField(templatePreferencePart.createFirstTemplateRadioButton(getFieldEditorParent()));
				this.prefValueOf1stTemplateItem = getPreferenceStore().getString(
						getTemplatePreferenceKey(template, P_TEMPLATE_FIRST_TEMPLATE_ITEM));
				this.prefValueOf2ndTempalteItem = getPreferenceStore().getString(
						getTemplatePreferenceKey(template, P_TEMPLATE_SECOND_TEMPLATE_ITEM));
				addField(templatePreferencePart.createNumberOfRequiredClassesField(getFieldEditorParent()));
				addField(templatePreferencePart.createSecondTemplateRadioButton(getFieldEditorParent()));
				addField(templatePreferencePart.createGetterSetterRadioButton(getFieldEditorParent()));
			}

			addField(templatePreferencePart.createAdditionalParametersField(getFieldEditorParent(), this.templatePrefix));
			addField(templatePreferencePart.createTemplateBodyField(getFieldEditorParent(), this.templatePrefix));
			templatePreferencePart.templateBodyField.setEmptyStringAllowed(false);
			if (isDetailedTemplate()) {

				templatePreferencePart.templateBodyField.setEmptyStringAllowed(false);
			}
			// addField(templatePreferencePart.createTemplateFileField(getFieldEditorParent()));
			final boolean enabled = getPreferenceStore().getBoolean(getTemplatePreferenceKey(template, P_TEMPLATE_ENABLE_TEMPLATE));

			enableFields(templatePreferencePart, enabled);
			new Separator(SWT.SEPARATOR | SWT.HORIZONTAL).doFillIntoGrid(getFieldEditorParent(), 10, convertHeightInCharsToPixels(2));
		}
		createImportExportButtons(getFieldEditorParent());

	}

	/**
	 * @param parent
	 */
	private void createImportExportButtons(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);
		this.importButton = new Button(composite, SWT.PUSH);
		this.importButton.setText("Import");

		this.importButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

				try {

					if (getAllTemplatesPreferenceKey().equals(P_ALL_TEMPLATES)) {
						//processXML("templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_TEMPLATES.toString(), TEMPLATES_FOLDER);
						importXML("templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_TEMPLATES.toString(), TEMPLATES_FOLDER);
					} else if (getAllTemplatesPreferenceKey().equals(P_ALL_COMMON_TEMPLATES)) {
						//processXML("additional-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_ADDITIONAL_TEMPLATES.toString(),TEMPLATES_FOLDER);
						importXML("common-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_COMMON_TEMPLATES.toString(), TEMPLATES_FOLDER);
					} else if (getAllTemplatesPreferenceKey().equals(P_DATABASE_ALL_TEMPLATES)) {
						//processXML("database-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_DATABASE_TEMPLATES.name(),DB_TEMPLATES_FOLDER);
						importXML("database-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_DATABASE_TEMPLATES.name(),
								DB_TEMPLATES_FOLDER);
					}/* else if (getAllTemplatesPreferenceKey().equals(P_ALL_ADDITIONAL_DATABASE_TEMPLATES)) {
						//processXML("additional-database-templates-config.xml",TEMPLATE_PREFERENCE_NAME.ALL_ADDITIONAL_DATABASE_TEMPLATES.name(), DB_TEMPLATES_FOLDER);
						importXML("additional-database-templates-config.xml",
								TEMPLATE_PREFERENCE_NAME.ALL_ADDITIONAL_DATABASE_TEMPLATES.name(), DB_TEMPLATES_FOLDER);
						} else if (getAllTemplatesPreferenceKey().equals(P_FILE_ALL_TEMPLATES)) {
						//processXML("file-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_FILE_TEMPLATES.name(), FILE_TEMPLATES_FOLDER);
						importXML("file-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_FILE_TEMPLATES.name(), FILE_TEMPLATES_FOLDER);
						}
						*/
				} catch (final Exception ex) {
					try {
						throw new Exception("There was some error in Import templates : " + ex.getMessage());
					} catch (final Exception ex1) {
						ex1.printStackTrace();
					}
					ex.printStackTrace();
				} finally {
					MessageDialog.openInformation(new Shell(), "Success", "Import was successfully completed .");
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

		this.exportButton = new Button(composite, SWT.PUSH);
		this.exportButton.setText("Export");

		this.exportButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				try {
					if (getAllTemplatesPreferenceKey().equals(P_ALL_TEMPLATES)) {
						//processXML("templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_TEMPLATES.toString(), TEMPLATES_FOLDER);
						exportXML("templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_TEMPLATES.toString(), TEMPLATES_FOLDER);
					} else if (getAllTemplatesPreferenceKey().equals(P_ALL_COMMON_TEMPLATES)) {
						//processXML("additional-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_ADDITIONAL_TEMPLATES.toString(),TEMPLATES_FOLDER);
						exportXML("common-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_COMMON_TEMPLATES.toString(), TEMPLATES_FOLDER);
					} else if (getAllTemplatesPreferenceKey().equals(P_DATABASE_ALL_TEMPLATES)) {
						//processXML("database-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_DATABASE_TEMPLATES.name(),DB_TEMPLATES_FOLDER);
						exportXML("database-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_DATABASE_TEMPLATES.name(),
								DB_TEMPLATES_FOLDER);
					} /*else if (getAllTemplatesPreferenceKey().equals(P_ALL_ADDITIONAL_DATABASE_TEMPLATES)) {
						//processXML("additional-database-templates-config.xml",TEMPLATE_PREFERENCE_NAME.ALL_ADDITIONAL_DATABASE_TEMPLATES.name(), DB_TEMPLATES_FOLDER);
						exportXML("additional-database-templates-config.xml",
								TEMPLATE_PREFERENCE_NAME.ALL_ADDITIONAL_DATABASE_TEMPLATES.name(), DB_TEMPLATES_FOLDER);
						} else if (getAllTemplatesPreferenceKey().equals(P_FILE_ALL_TEMPLATES)) {
						//processXML("file-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_FILE_TEMPLATES.name(), FILE_TEMPLATES_FOLDER);
						exportXML("file-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_FILE_TEMPLATES.name(), FILE_TEMPLATES_FOLDER);
						}*/
				} catch (final Exception ex) {
					try {
						throw new Exception("There was some error in Export templates : " + ex.getMessage());
					} catch (final Exception ex1) {
						ex1.printStackTrace();
					}
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

	}

	@Override
	public boolean isValid() {
		return super.isValid();
	}

	@Override
	public boolean performOk() {
		if (!isEmpty(getErrorMessage())) {
			openError(getShell(), "Error", "There are errors present, cannot save.");
			return false;
		}
		final boolean status = super.performOk();
		TemplateSettings.setReload(status);
		// TemplateSettings.setTemplateSave(true);
		return status;
	}

	/**
	 * @param event
	 *
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		super.propertyChange(event);
		final Object source = event.getSource();
		final Object newValue = event.getNewValue();
		this.classSelected = false;
		this.fileSelected = false;
		this.pkgSelected = false;
		this.fldrSelected = false;
		this.enumSelected = false;
		final GlobalSettings globalSettings = GlobalSettings.getInstance();

		for (int i = 0; i < this.TEMPLATES_LIST.length; i++) {
			this.prefValueOf1stTemplateItem = getPreferenceStore().getString(
					getTemplatePreferenceKey(this.templatePreferenceParts[i].templateName, P_TEMPLATE_FIRST_TEMPLATE_ITEM));
			this.prefValueOf2ndTempalteItem = getPreferenceStore().getString(
					getTemplatePreferenceKey(this.templatePreferenceParts[i].templateName, P_TEMPLATE_SECOND_TEMPLATE_ITEM));
			if (source == this.templatePreferenceParts[i].enableTemplateCheckBox) {
				final boolean enbale = (Boolean) newValue;
				enableFields(this.templatePreferenceParts[i], enbale);
				break;
			} else if (source == this.templatePreferenceParts[i].allowMultipleVariationCheckBox) {
				this.templatePreferenceParts[i].templateVariationField.setEmptyStringAllowed(!(Boolean) newValue);
				if (FALSE_STR.equalsIgnoreCase(globalSettings.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD, TRUE_STR))) {
					this.templatePreferenceParts[i].templateVariationFieldNameField.setEmptyStringAllowed(!(Boolean) newValue);
				}
				break;
			} else if (source == this.templatePreferenceParts[i].templateVariationField) {
				if (FALSE_STR.equalsIgnoreCase(globalSettings.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD, TRUE_STR))) {
					this.templatePreferenceParts[i].templateVariationFieldNameField.setEmptyStringAllowed(isEmpty((String) newValue));
				}
				break;
			} else if (source == this.templatePreferenceParts[i].templateVariationFieldNameField) {
				final boolean isErr = ((String) newValue).endsWith(" ") || ((String) newValue).endsWith("\t");
				setErrorMessage(isErr ? "Variation Field cannot end with whitespace" : null);
				this.templatePreferenceParts[i].templateVariationField.setEmptyStringAllowed(isEmpty((String) newValue));
				break;
			} else if (source == this.templatePreferenceParts[i].firstTemplateRadioButton) {
				setErrorMessage(null);
				this.currentValueOf1stTemplateItem = (String) newValue;
				enableTemplateItems(this.templatePreferenceParts[i], newValue);

				validateSecondTemplateItem(this.currentValueOf2ndTemplateItem);
				break;
			} else if (source == this.templatePreferenceParts[i].secondTemplateRadioButton) {
				this.currentValueOf2ndTemplateItem = (String) newValue;

				if (newValue.equals(SECOND_TEMPLATE.field.getValue()) || newValue.equals(SECOND_TEMPLATE.both.getValue())
						|| newValue.equals(SECOND_TEMPLATE.custom.getValue())) {
					this.templatePreferenceParts[i].getterSetterRadioButton.setEnabled(true, getFieldEditorParent());
				} else if (newValue.equals(SECOND_TEMPLATE.method.getValue()) || newValue.equals(SECOND_TEMPLATE.none.getValue())) {
					getPreferenceStore().setValue(
							getTemplatePreferenceKey(this.templatePreferenceParts[i].templateName, P_TEMPLATE_GETTER_SETTER),
							GETTER_SETTER.NONE.getValue());
					this.templatePreferenceParts[i].getterSetterRadioButton.setEnabled(false, getFieldEditorParent());
				}
				if (isEmpty(this.currentValueOf1stTemplateItem)) {
					this.currentValueOf1stTemplateItem = this.prefValueOf1stTemplateItem;
				}
				this.classSelected = this.currentValueOf1stTemplateItem.equals(FIRST_TEMPLATE.Class.getValue());
				this.fileSelected = this.currentValueOf1stTemplateItem.equals(FIRST_TEMPLATE.File.getValue());
				this.pkgSelected = this.currentValueOf1stTemplateItem.equals(FIRST_TEMPLATE.Package.getValue());
				this.fldrSelected = this.currentValueOf1stTemplateItem.equals(FIRST_TEMPLATE.Folder.getValue());
				this.enumSelected = this.currentValueOf1stTemplateItem.equals(FIRST_TEMPLATE.Enumeration.getValue());
				validateSecondTemplateItem(newValue);
				break;
			} else if (source == this.templatePreferenceParts[i].additionalParametersField) {
				if (!isEmpty(newValue.toString())) {
					try {
						parseAdditonalParam(newValue.toString());
					} catch (final FastCodeException ex) {
						//throw new Exception(ex);
					}
					/*final String addtnlParams[] = newValue.toString().split("\\s+");
					final Map<String, String> addtnParamMap = new HashMap<String, String>();
					for (final String params : addtnlParams) {
						if (params.contains(COLON)) {
							final String parseParam[] = params.split(COLON);
							if (parseParam.length == 2) {
								final String type = parseParam[1].trim().toLowerCase();
								if (isEmpty(type)
										|| !(type.equalsIgnoreCase(FIRST_TEMPLATE.Class.getValue())
												|| type.equalsIgnoreCase(FIRST_TEMPLATE.File.getValue())
												|| type.equalsIgnoreCase(FIRST_TEMPLATE.Package.getValue())
												|| type.equalsIgnoreCase(FIRST_TEMPLATE.Folder.getValue())
												|| type.equalsIgnoreCase(RETURN_TYPES.JAVAPROJECT.getValue())
												|| type.equalsIgnoreCase(RETURN_TYPES.PROJECT.getValue())
												|| type.equalsIgnoreCase(RETURN_TYPES.LOCALVAR.getValue()) || type
													.equalsIgnoreCase(RETURN_TYPES.BOOLEAN.getValue()))) {
									setErrorMessage("Additional Parameter type can be only class/file/package/folder/project/javaProject/localvar/boolean.");
								} else {
									setErrorMessage(null);
								}
								if (addtnParamMap.containsKey(parseParam[0])) {
									setErrorMessage("Duplicate place holder name " + parseParam[0]);
								}
								addtnParamMap.put(parseParam[0], type);
							} else if (parseParam.length == 1) {
								setErrorMessage("Additional Parameter must have type class/file/package/folder/project/javaProject/localvar/boolean.");
							}
						}
					}*/
				}
			}
		}
	}

	/**
	 * @param templatePreferencePart
	 * @param enable
	 */
	private void enableFields(final TemplatePreferencePart templatePreferencePart, final boolean enable) {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();

		if (isDetailedTemplate()) {
			templatePreferencePart.templateAllowedFileNamesField.setEnabled(enable, getFieldEditorParent());
			templatePreferencePart.firstTemplateRadioButton.setEnabled(enable, getFieldEditorParent());
			this.prefValueOf1stTemplateItem = getPreferenceStore().getString(
					getTemplatePreferenceKey(templatePreferencePart.templateName, P_TEMPLATE_FIRST_TEMPLATE_ITEM));
			enableTemplateItems(templatePreferencePart, this.prefValueOf1stTemplateItem);
			if (!enable) {
				templatePreferencePart.secondTemplateRadioButton.setEnabled(enable, getFieldEditorParent());
				templatePreferencePart.getterSetterRadioButton.setEnabled(enable, getFieldEditorParent());
				templatePreferencePart.numberOfRequiredClassesField.setEnabled(enable, getFieldEditorParent());
			}
			templatePreferencePart.allowMultipleVariationCheckBox.setEnabled(enable, getFieldEditorParent());
		}
		templatePreferencePart.templateVariationField.setEnabled(enable, getFieldEditorParent());
		templatePreferencePart.templateDescriptionField.setEnabled(enable, getFieldEditorParent());
		if (FALSE_STR.equalsIgnoreCase(globalSettings.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD, TRUE_STR))) {
			templatePreferencePart.templateVariationFieldNameField.setEnabled(enable, getFieldEditorParent());
		}
		if (isShowAllowedFileExtension()) {
			templatePreferencePart.templateAllowedFileNamesField.setEnabled(enable, getFieldEditorParent());
		}
		templatePreferencePart.additionalParametersField.setEnabled(enable, getFieldEditorParent());
		templatePreferencePart.templateBodyField.setEnabled(enable, getFieldEditorParent());
		// templatePreferencePart.templateFileField.setEnabled(enable,
		// getFieldEditorParent());
	}

	/**
	 *
	 * @return
	 */
	protected String getAllTemplatesPreferenceKey() {
		return P_ALL_TEMPLATES;
	}

	protected boolean isDetailedTemplate() {
		return true;
	}

	@Override
	public void init(final IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	/**
	 *
	 */

	protected boolean isShowAllowedFileExtension() {
		return true;
	}

	/**
	 * @param templatePreferencePart
	 * @param newValue
	 */
	private void enableTemplateItems(final TemplatePreferencePart templatePreferencePart, final Object newValueOfFirstTemplateItem) {
		if (isEmpty(this.currentValueOf2ndTemplateItem)) {
			this.currentValueOf2ndTemplateItem = this.prefValueOf2ndTempalteItem;
		}
		if (newValueOfFirstTemplateItem.equals(FIRST_TEMPLATE.Class.getValue())) {
			this.classSelected = true;
			templatePreferencePart.secondTemplateRadioButton.setEnabled(true, getFieldEditorParent());
			if (this.currentValueOf2ndTemplateItem.equals(SECOND_TEMPLATE.field.getValue())
					|| this.currentValueOf2ndTemplateItem.equals(SECOND_TEMPLATE.both.getValue())
					|| this.currentValueOf2ndTemplateItem.equals(SECOND_TEMPLATE.custom.getValue())) {

				templatePreferencePart.getterSetterRadioButton.setEnabled(true, getFieldEditorParent());

			} else if (this.currentValueOf2ndTemplateItem.equals(SECOND_TEMPLATE.method.getValue())
					|| this.currentValueOf2ndTemplateItem.equals(SECOND_TEMPLATE.none.getValue())) {
				getPreferenceStore().setValue(getTemplatePreferenceKey(templatePreferencePart.templateName, P_TEMPLATE_GETTER_SETTER),
						GETTER_SETTER.NONE.getValue());
				templatePreferencePart.getterSetterRadioButton.setEnabled(false, getFieldEditorParent());
			}
			templatePreferencePart.numberOfRequiredClassesField.setEnabled(true, getFieldEditorParent());
		} else if (newValueOfFirstTemplateItem.equals(FIRST_TEMPLATE.File.getValue())) {
			this.fileSelected = true;
			getPreferenceStore().setValue(getTemplatePreferenceKey(templatePreferencePart.templateName, P_TEMPLATE_SECOND_TEMPLATE_ITEM),
					SECOND_TEMPLATE.none.getValue());
			templatePreferencePart.secondTemplateRadioButton.setEnabled(false, getFieldEditorParent());
			getPreferenceStore().setValue(getTemplatePreferenceKey(templatePreferencePart.templateName, P_TEMPLATE_GETTER_SETTER),
					GETTER_SETTER.NONE.getValue());
			templatePreferencePart.getterSetterRadioButton.setEnabled(false, getFieldEditorParent());
			templatePreferencePart.numberOfRequiredClassesField.setEnabled(false, getFieldEditorParent());
		} else if (newValueOfFirstTemplateItem.equals(FIRST_TEMPLATE.Package.getValue())
				|| newValueOfFirstTemplateItem.equals(FIRST_TEMPLATE.Folder.getValue())) {
			if (newValueOfFirstTemplateItem.equals(FIRST_TEMPLATE.Package.getValue())) {
				this.pkgSelected = true;
			} else if (newValueOfFirstTemplateItem.equals(FIRST_TEMPLATE.Folder.getValue())) {
				this.fldrSelected = true;
			}

			templatePreferencePart.secondTemplateRadioButton.setEnabled(true, getFieldEditorParent());
			templatePreferencePart.getterSetterRadioButton.setEnabled(false, getFieldEditorParent());
			templatePreferencePart.numberOfRequiredClassesField.setEnabled(false, getFieldEditorParent());
		} else if (newValueOfFirstTemplateItem.equals(FIRST_TEMPLATE.Enumeration.getValue())) {
			this.enumSelected = true;
			templatePreferencePart.secondTemplateRadioButton.setEnabled(true, getFieldEditorParent());
			if (this.currentValueOf2ndTemplateItem.equals(SECOND_TEMPLATE.field.getValue())) {
				templatePreferencePart.getterSetterRadioButton.setEnabled(true, getFieldEditorParent());
			} else if (this.currentValueOf2ndTemplateItem.equals(SECOND_TEMPLATE.none.getValue())) {
				getPreferenceStore().setValue(getTemplatePreferenceKey(templatePreferencePart.templateName, P_TEMPLATE_GETTER_SETTER),
						GETTER_SETTER.NONE.getValue());
				templatePreferencePart.getterSetterRadioButton.setEnabled(false, getFieldEditorParent());
			}
			templatePreferencePart.numberOfRequiredClassesField.setEnabled(true, getFieldEditorParent());
		} else if (newValueOfFirstTemplateItem.equals(FIRST_TEMPLATE.None.getValue())) {
			getPreferenceStore().setValue(getTemplatePreferenceKey(templatePreferencePart.templateName, P_TEMPLATE_SECOND_TEMPLATE_ITEM),
					SECOND_TEMPLATE.none.getValue());
			getPreferenceStore().setValue(getTemplatePreferenceKey(templatePreferencePart.templateName, P_TEMPLATE_GETTER_SETTER),
					GETTER_SETTER.NONE.getValue());
			templatePreferencePart.secondTemplateRadioButton.setEnabled(false, getFieldEditorParent());
			templatePreferencePart.getterSetterRadioButton.setEnabled(false, getFieldEditorParent());
			templatePreferencePart.numberOfRequiredClassesField.setEnabled(false, getFieldEditorParent());
		}
	}

	/**
	 * @param newValue
	 */
	private void validateSecondTemplateItem(final Object newValueOfSecondTemplateItem) {
		if (this.pkgSelected) {
			if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.field.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.both.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.custom.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.method.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.file.getValue())) {
				setErrorMessage("Please choose only Class or None in Second Template Item.");
			} else {
				setErrorMessage(null);
			}
		} else if (this.fldrSelected) {
			if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.field.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.both.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.custom.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.method.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.Class.getValue())) {
				setErrorMessage("Please choose only File or None in Second Template Item.");
			} else {
				setErrorMessage(null);
			}
		} else if (this.classSelected) {
			if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.Class.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.file.getValue())) {
				setErrorMessage("Please choose only Method, Field or Custom in Second Template Item.");
			} else {
				setErrorMessage(null);
			}

		} else if (this.fileSelected) {
			if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.Class.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.file.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.field.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.both.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.custom.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.method.getValue())) {
				setErrorMessage("Please choose only None in Second Template Item.");
			} else {
				setErrorMessage(null);
			}
		} else if (this.enumSelected) {
			if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.Class.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.file.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.both.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.custom.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.method.getValue())) {
				setErrorMessage("Please choose only Field or None in Second Template Item.");
			} else {
				setErrorMessage(null);
			}
		}
	}
}

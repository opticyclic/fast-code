package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.DEFAULT_TEMPLATE_VARIATION_FIELD;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FALSE_STR;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.TAB;
import static org.fastcode.common.FastCodeConstants.TRUE_STR;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.preferences.PreferenceConstants.P_ALL_TEMPLATES;
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
import static org.fastcode.preferences.PreferenceUtil.getTemplatePreferenceKey;
import static org.fastcode.util.FastCodeUtil.getEmptyListForNull;
import static org.fastcode.util.SourceUtil.showWarning;
import static org.fastcode.util.StringUtil.getNoOfTabs;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.parseAdditonalParam;
import static org.fastcode.util.StringUtil.parseFCTag;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fastcode.common.FastCodeAdditionalParams;
import org.fastcode.common.FastCodeColor;
import org.fastcode.common.FastCodeConstants.FIRST_TEMPLATE;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER;
import org.fastcode.common.FastCodeConstants.SECOND_TEMPLATE;
import org.fastcode.common.Template;
import org.fastcode.common.TemplateStore;
import org.fastcode.exception.FastCodeException;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.templates.util.FcTagAttributes;
import org.fastcode.templates.util.TagAttributeList;
import org.fastcode.templates.util.VariablesUtil;
import org.fastcode.templates.velocity.contentassist.FastCodeKeywordsManager;
import org.fastcode.templates.viewer.TemplateFieldEditor;
import org.fastcode.templates.viewer.TemplateFieldEditor.ErrorAnnotation;
import org.fastcode.util.DefaultTemplatesManager;
import org.fastcode.util.TemplateItemsUtil;
import org.fastcode.util.VelocityUtil;

public class OpenEditTemplateDialog extends FieldsPreferencePage {

	private final IPreferenceStore store;
	private final Template template;
	private final String templatePrefix;
	private final boolean isDetailedTemplate;
	private final boolean isShowAllowedFileExtension;
	private Composite parent;
	private TemplatePreferencePart templatePreferencePart;
	private Text errorMessageText;
	private String defaultMessage = null;
	private String currentFirstTemplateItemValue;
	private String currentSecondTemplateItemValue;
	private final boolean newTemplate;
	private StringFieldEditor descriptionField;
	private String templateName;
	private TemplateFieldEditor templateBodyField;
	private StringFieldEditor allowedFileNameExtensions;
	private FieldEditor allowMultipleVariation;
	private FieldEditor firstTemplateItem;
	private FieldEditor requiredClassField;
	private FieldEditor templateItemPatternField;
	private FieldEditor secondTemplatefield;
	private FieldEditor getterSetterField;
	private TemplateFieldEditor additionalParameterField;
	private FieldEditor templateVariationField;
	private FieldEditor variationFieldName;
	private String prefValueOf1stTemplateItem;
	private String prefValueOf2ndTempalteItem;
	private final String allTemplatesPreferenceKey;
	private StringFieldEditor templateNameField;
	protected boolean existingTemplateRenamed = false;
	private String additionalParamValue;
	private String sampleTemplateBody;
	private String warningMessage;
	private String errorMessage;
	private boolean fieldModified = false;
	private Shell shell;
	private final static Logger LOGGER = Logger.getLogger(OpenEditTemplateDialog.class.getName());
	String templateBodyLast = EMPTY_STR;

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		if (!this.allTemplatesPreferenceKey.equals(P_ALL_TEMPLATES)) {
			shell.setSize(800, 550);
		}
		shell.setText(this.newTemplate ? "New Template " : "Edit Template ");
		this.shell = shell;
	}

	@Override
	protected Control createDialogArea(final Composite ancestor) {
		this.parent = new Composite(ancestor, SWT.NONE);
		final GridLayout layout1 = new GridLayout();
		layout1.numColumns = 2;
		layout1.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout1.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout1.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout1.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		this.parent.setLayout(layout1);
		final GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		this.parent.setLayoutData(gd);
		createFieldEditors(this.parent);
		initialize();
		if (this.newTemplate) {
			setDefaultValuesToNewTemplate();
		}
		/*this.shell.addListener(SWT.Traverse, new Listener() {

			public void handleEvent(final Event event) {
				if (event.detail == SWT.TRAVERSE_ESCAPE) {
					if (OpenEditTemplateDialog.this.fieldModified) {
						final MessageDialog dialog = new MessageDialog(new Shell(), "Warning", null, "Changes made to the template has not been saved. Do you want to save the changes or proceed anyway?",
								Message6Dialog.WARNING, new String[] { "Save", "Proceed Anyway" }, 0) {

							@Override
							protected void buttonPressed(final int buttonId) {
								setReturnCode(buttonId);
								close();

							}
						};

						dialog.open();

						if (dialog.getReturnCode() == 0) {
							okPressed();
						} else {
							cancelPressed();
						}
					}
				}
			}
		});*/
		this.templateNameField.getTextControl(this.parent).addFocusListener(new FocusListener() {

			@Override
			public void focusGained(final FocusEvent e) {
				setErrorMessage(null);
				//				EnableTemplateFields(true);

			}

			@Override
			public void focusLost(final FocusEvent e) {
				if (OpenEditTemplateDialog.this.newTemplate) {
					final TemplateStore templateStore = TemplateStore.getInstance();
					for (final Template template : templateStore.getTemplatesList(OpenEditTemplateDialog.this.allTemplatesPreferenceKey)) {

						if (template.getTemplateName().equalsIgnoreCase(
								OpenEditTemplateDialog.this.templateNameField.getStringValue().trim())
								&& !template.isTemplateDeleted()) {
							setErrorMessage("Template Already exists");
							//							EnableTemplateFields(false);
							return;

						}
					}
				} else if (OpenEditTemplateDialog.this.defaultMessage == null) {
					if (!OpenEditTemplateDialog.this.templateNameField

					.getStringValue().equalsIgnoreCase(OpenEditTemplateDialog.this.template.getTemplateName())) {

						final TemplateStore templateStore = TemplateStore.getInstance();
						for (final Template template : templateStore
								.getTemplatesList(OpenEditTemplateDialog.this.allTemplatesPreferenceKey)) {

							if (template.getTemplateName().equalsIgnoreCase(
									OpenEditTemplateDialog.this.templateNameField.getStringValue().trim())
									&& !template.isTemplateDeleted()) {
								setErrorMessage("Template Already exists..You are overwriting an existing template");
								//								EnableTemplateFields(false);
								return;

							}

						}

					}
				}
			}
		});

		this.templateNameField.getTextControl(this.parent).addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				// TODO Auto-generated method stub

				if (OpenEditTemplateDialog.this.defaultMessage == null) {
					if (!OpenEditTemplateDialog.this.newTemplate) {
						final String templateName = OpenEditTemplateDialog.this.templatePrefix + UNDERSCORE
								+ OpenEditTemplateDialog.this.template.getTemplateName();

						for (final String defaultTemplate : DefaultTemplatesManager.getInstance().getDefaultTemplates(
								OpenEditTemplateDialog.this.templatePrefix)) {
							if (templateName.equals(defaultTemplate)) {
								MessageDialog.openError(null, "Overwrite Error", "Cannot rename default templates");
								OpenEditTemplateDialog.this.defaultMessage = "Cannot rename default templates";
								OpenEditTemplateDialog.this.templateNameField.setStringValue(OpenEditTemplateDialog.this.template
										.getTemplateName());
								break;
							}
						}
					}
				} else {
					OpenEditTemplateDialog.this.defaultMessage = null;
				}
			}

		});
		this.additionalParameterField.getControl().addFocusListener(new FocusListener() {

			@Override
			public void focusGained(final FocusEvent e) {

			}

			@Override
			public void focusLost(final FocusEvent e) {
				setAdditionalParamValues();
			}

		});
		enableFields(
				this.templatePreferencePart,
				getPreferenceStore().getBoolean(
						getTemplatePreferenceKey(
								this.templatePrefix + UNDERSCORE + (this.template != null ? this.template.getTemplateName() : "null"),
								P_TEMPLATE_ENABLE_TEMPLATE)));

		return ancestor;
	}

	/**
	 *
	 */
	private void setDefaultValuesToNewTemplate() {

		if (!getPreferenceStore()
				.getBoolean(getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_ENABLE_TEMPLATE))) {
			getPreferenceStore().setValue(getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_ENABLE_TEMPLATE),
					true);

		}

		this.templateNameField.getPreferenceStore().setToDefault(
				getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_NAME));
		if (this.isDetailedTemplate) {
			this.firstTemplateItem.getPreferenceStore().setValue(
					getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_FIRST_TEMPLATE_ITEM), "none");

			this.firstTemplateItem.load();
			this.allowMultipleVariation.getPreferenceStore().setToDefault(
					getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_ALLOW_MULTIPLE_VARIATION));
			this.templateItemPatternField.getPreferenceStore().setToDefault(
					getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_ITEM_PATTERN));
		}
		if (this.isShowAllowedFileExtension) {
			this.allowedFileNameExtensions.getPreferenceStore().setToDefault(
					getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_ALLOWED_FILE_NAMES));
			this.allowedFileNameExtensions.setStringValue(ASTERISK);
		}
		this.descriptionField.getPreferenceStore().setToDefault(
				getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_DESCRIPTION));
		/*if (this.allTemplatesPreferenceKey.equals(P_FILE_ALL_TEMPLATES)) {
			this.templateBodyField.getPreferenceStore().setValue(
					getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_BODY), this.sampleTemplateBody);
			this.templateBodyField.load();
		} else {*/
		this.templateBodyField.getPreferenceStore().setToDefault(
				getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_BODY));
		//	}

		this.additionalParameterField.getPreferenceStore().setToDefault(
				getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_ADDITIONAL_PARAMETERS));
		this.templateVariationField.getPreferenceStore().setToDefault(
				getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_VARIATION));
	}

	/**
	 * @param shell
	 * @param preferenceStore
	 * @param template
	 * @param templatePrefix
	 * @param allTemplatesPreferenceKey
	 * @param isDetailedTemplate
	 * @param isShowAllowedFileExtension
	 * @param isNewTemplate
	 */
	public OpenEditTemplateDialog(final Shell shell, final IPreferenceStore preferenceStore, final Template template,
			final String templatePrefix, final String allTemplatesPreferenceKey, final boolean isDetailedTemplate,
			final boolean isShowAllowedFileExtension, final boolean isNewTemplate) {
		super(shell);
		this.store = preferenceStore;
		setPreferenceStore(this.store);
		this.template = template;
		this.templatePrefix = templatePrefix;
		this.isDetailedTemplate = isDetailedTemplate;
		this.isShowAllowedFileExtension = isShowAllowedFileExtension;
		this.newTemplate = isNewTemplate;
		this.allTemplatesPreferenceKey = allTemplatesPreferenceKey;

	}

	/**
	 * @param shell
	 * @param preferenceStore
	 * @param template
	 * @param templatePrefix
	 * @param allTemplatesPreferenceKey
	 * @param isDetailedTemplate
	 * @param isShowAllowedFileExtension
	 * @param isNewTemplate
	 * @param templateBody
	 */
	public OpenEditTemplateDialog(final Shell shell, final IPreferenceStore preferenceStore, final Template template,
			final String templatePrefix, final String allTemplatesPreferenceKey, final boolean isDetailedTemplate,
			final boolean isShowAllowedFileExtension, final boolean isNewTemplate, final String templateBody) {
		super(shell);
		this.store = preferenceStore;
		setPreferenceStore(this.store);
		this.template = template;
		this.templatePrefix = templatePrefix;
		this.isDetailedTemplate = isDetailedTemplate;
		this.isShowAllowedFileExtension = isShowAllowedFileExtension;
		this.newTemplate = isNewTemplate;
		this.allTemplatesPreferenceKey = allTemplatesPreferenceKey;
		this.sampleTemplateBody = templateBody;

	}

	/**
	 * @param parent
	 */
	public void createFieldEditors(final Composite parent) {

		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		this.templatePreferencePart = new TemplatePreferencePart(this.templatePrefix + UNDERSCORE
				+ (this.template != null ? this.template.getTemplateName() : "null"));
		createErrorMessageText(parent);
		this.templateNameField = (StringFieldEditor) this.templatePreferencePart.createTemplateNameField(parent);
		addField(this.templateNameField);

		this.descriptionField = (StringFieldEditor) this.templatePreferencePart.createTemplateDescriptionField(parent);
		addField(this.descriptionField);
		if (FALSE_STR.equalsIgnoreCase(globalSettings.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD, TRUE_STR))) {
			this.variationFieldName = this.templatePreferencePart.createTemplateVariationFieldNameField(parent);
			addField(this.variationFieldName);
		}

		if (this.isShowAllowedFileExtension) {
			this.allowedFileNameExtensions = (StringFieldEditor) this.templatePreferencePart
					.createTemplateAllowedFileExtensionsField(parent);
			this.allowedFileNameExtensions.setEmptyStringAllowed(false);
			addField(this.allowedFileNameExtensions);
		}
		if (this.isDetailedTemplate) {
			this.allowMultipleVariation = this.templatePreferencePart.createAllowMultipleVariationCheckBox(parent);
			addField(this.allowMultipleVariation);
			new Label(parent, 0);
		}
		this.templateVariationField = this.templatePreferencePart.createTemplateVariationField(parent);
		addField(this.templateVariationField);
		if (this.isDetailedTemplate) {
			this.firstTemplateItem = this.templatePreferencePart.createFirstTemplateRadioButton(parent);
			addField(this.firstTemplateItem);

			this.requiredClassField = this.templatePreferencePart.createNumberOfRequiredClassesField(parent);
			addField(this.requiredClassField);

			this.secondTemplatefield = this.templatePreferencePart.createSecondTemplateRadioButton(parent);
			addField(this.secondTemplatefield);

			this.getterSetterField = this.templatePreferencePart.createGetterSetterRadioButton(parent);
			addField(this.getterSetterField);

			this.templateItemPatternField = this.templatePreferencePart.createItemPatternField(parent);
			addField(this.templateItemPatternField);
		}
		this.additionalParameterField = (TemplateFieldEditor) this.templatePreferencePart.createAdditionalParametersField(this.parent,
				this.templatePrefix);
		addField(this.additionalParameterField);
		this.templateBodyField = this.templatePreferencePart.createTemplateBodyField(this.parent, this.templatePrefix);
		addField(this.templateBodyField);
		this.templateBodyField.getControl().addFocusListener(new FocusListener() {

			@Override
			public void focusGained(final FocusEvent e) {
				setErrorMessage(null);
			}

			@Override
			public void focusLost(final FocusEvent e) {
				// TODO Auto-generated method stub
				setErrorMessage(null);
			}

		});

		this.templateBodyField.getControl().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(final KeyEvent arg0) {
				// TODO Auto-generated method stub
				setErrorMessage(null);
			}

			@Override
			public void keyPressed(final KeyEvent arg0) {
				//System.out.println("key pressed ->" + arg0.character);
				LOGGER.log(Level.FINE, "key pressed ->" + arg0.character);
				setErrorMessage(null);

			}
		});
		this.templateBodyField.getControl().addListener(SWT.CHANGED, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				//System.out.println(event.type);
				LOGGER.log(Level.FINE, "Event Type" + event.type);
				setErrorMessage(null);
			}
		});
	}

	/**
	 * @param value
	 */
	/*private void EnableTemplateFields(final boolean value) {
		this.descriptionField.setEnabled(value, this.parent);
		this.templateVariationField.setEnabled(value, this.parent);
		if (this.isShowAllowedFileExtension) {
			this.allowedFileNameExtensions.setEnabled(value, this.parent);
		}
		this.additionalParameterField.setEnabled(value, this.parent);
		this.templateBodyField.setEnabled(value, this.parent);
		if (this.isDetailedTemplate) {
			this.allowMultipleVariation.setEnabled(value, this.parent);
			this.templateItemPatternField.setEnabled(value, this.parent);
			this.firstTemplateItem.setEnabled(value, this.parent);

			enableTemplateItems(this.templatePreferencePart, this.currentFirstTemplateItemValue, false);
			this.secondTemplatefield.setEnabled(value, this.parent);
				this.getterSetterField.setEnabled(value, this.parent);
				this.requiredClassField.setEnabled(value, this.parent);

		}

	}*/

	/**
	 * @param templateSuffix
	 * @return
	 */
	private String getPreferenceName(final String templateSuffix) {
		return getTemplatePreferenceKey(this.templatePrefix + UNDERSCORE + this.templateNameField.getStringValue(), templateSuffix);
	}

	/**
	 *
	 */
	private void setNewTemplate() {

		final GlobalSettings globalSettings = GlobalSettings.getInstance();

		// this.enableTemplateCheckBox.setPreferenceName(getPreferenceName(P_TEMPLATE_ENABLE_TEMPLATE));
		getPreferenceStore().setValue(
				getTemplatePreferenceKey(this.templatePrefix + UNDERSCORE + this.templateNameField.getStringValue(),
						P_TEMPLATE_ENABLE_TEMPLATE), true);
		this.templateNameField.setPreferenceName(getPreferenceName(P_TEMPLATE_NAME));
		this.descriptionField.setPreferenceName(getPreferenceName(P_TEMPLATE_DESCRIPTION));
		if (this.isShowAllowedFileExtension) {
			this.allowedFileNameExtensions.setPreferenceName(getPreferenceName(P_TEMPLATE_ALLOWED_FILE_NAMES));
		}
		this.templateVariationField.setPreferenceName(getPreferenceName(P_TEMPLATE_VARIATION));

		if (FALSE_STR.equalsIgnoreCase(globalSettings.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD, TRUE_STR))) {
			this.variationFieldName.setPreferenceName(getTemplatePreferenceKey(
					this.templatePrefix + UNDERSCORE + this.templateNameField.getStringValue(), P_TEMPLATE_VARIATION_FIELD));
		}

		this.additionalParameterField.setPreferenceName(getPreferenceName(P_TEMPLATE_ADDITIONAL_PARAMETERS));
		this.templateBodyField.setPreferenceName(getPreferenceName(P_TEMPLATE_BODY));
		if (this.isDetailedTemplate) {
			this.allowMultipleVariation.setPreferenceName(getPreferenceName(P_TEMPLATE_ALLOW_MULTIPLE_VARIATION));
			this.firstTemplateItem.setPreferenceName(getPreferenceName(P_TEMPLATE_FIRST_TEMPLATE_ITEM));
			this.requiredClassField.setPreferenceName(getPreferenceName(P_TEMPLATE_NUMBER_REQUIRED_ITEMS));
			this.templateItemPatternField.setPreferenceName(getPreferenceName(P_TEMPLATE_ITEM_PATTERN));
			this.secondTemplatefield.setPreferenceName(getPreferenceName(P_TEMPLATE_SECOND_TEMPLATE_ITEM));
			this.getterSetterField.setPreferenceName(getPreferenceName(P_TEMPLATE_GETTER_SETTER));
		}

		setTemplateName(this.templateNameField.getStringValue());

	}

	@Override
	public void okPressed() {
		this.templateBodyField.removeAllAnnotations();

		if (this.templateNameField.getStringValue().trim().equals(EMPTY_STR)) {
			setErrorMessage("Template Name Field is blank.");
			return;
		} else if (this.isShowAllowedFileExtension && this.allowedFileNameExtensions.getStringValue().trim().equals(EMPTY_STR)) {

			setErrorMessage("Allowed file names Field is blank.");
			return;

		} else if (this.templateBodyField.getStringValue().trim().equals(EMPTY_STR)) {
			setErrorMessage("Template Body is blank.");
			return;
		}
		if (this.newTemplate) {
			setNewTemplate();
		} else if (!this.templateNameField.getStringValue().equals(this.template.getTemplateName())) {

			if (!OpenEditTemplateDialog.this.newTemplate) {
				final MessageDialog exportMessageDialog = new MessageDialog(
						null,
						"Template Name Renamed",
						null,
						"The name of the template has been changed.Click Yes to create a new Template with the new name or No to rename the existing template",
						MessageDialog.QUESTION, new String[] { "Yes", "No" }, 0) {

					@Override
					protected void buttonPressed(final int buttonId) {
						setReturnCode(buttonId);
						close();

					}
				};

				exportMessageDialog.open();

				if (exportMessageDialog.getReturnCode() == -1) {

					OpenEditTemplateDialog.this.templateNameField.setStringValue(getPreferenceStore().getString(
							getTemplatePreferenceKey(OpenEditTemplateDialog.this.templatePreferencePart.templateName, P_TEMPLATE_NAME)));
				} else if (exportMessageDialog.getReturnCode() == 0) {
					setNewTemplate();

				} else if (exportMessageDialog.getReturnCode() == 1) {
					setNewTemplate();
					this.existingTemplateRenamed = true;
				}

			}
		}
		if (this.allTemplatesPreferenceKey.equals(P_ALL_TEMPLATES)) {
			if (this.currentFirstTemplateItemValue.equals(SECOND_TEMPLATE.none.getValue())
					&& this.currentSecondTemplateItemValue.equals(SECOND_TEMPLATE.none.getValue())
					&& this.additionalParameterField.getStringValue().equals(EMPTY_STR)) {
				if (MessageDialog.openQuestion(null, "Additional Parameter is empty",
						"Additional Parameter is empty. Do you want to change the input for first and second template items?")) {
					return;
				}
			}
		}
		setSourceItems();
		setErrorMessage("Please hold while the template is being validated.....");
		try {
			RuntimeSingleton.parse(new StringReader(this.templateBodyField.getStringValue()), this.templateName);
		} catch (final ParseException pe) {
			pe.printStackTrace();
			if (pe.currentToken != null && pe.currentToken.next != null) {
				int lineNo = pe.currentToken.next.beginLine - 1;
				final int colNo = pe.currentToken.next.beginColumn - 1;
				int lineOffset = 0;
				int docLen = 0;
				try {
					final IRegion reg = this.templateBodyField.getDocument().getLineInformation(lineNo);
					docLen = this.templateBodyField.getDocument().getLineLength(lineNo);
					lineOffset = this.templateBodyField.getDocument().getLineOffset(lineNo);
					final int lineOfOffset = this.templateBodyField.getDocument().getLineOfOffset(lineNo);
					final String lineContent = this.templateBodyField.getDocument().get(reg.getOffset(), reg.getLength());
					/*System.out.println(reg);
					System.out.println(lineOffset);
					System.out.println(docLen);
					System.out.println("text-" + reg.toString());
					System.out.println("line content-" + this.templateBodyField.getDocument().get(lineOffset - docLen, docLen));
					System.out.println(lineOffset - docLen);
					System.out.println("content-" + lineContent);*/
					if (isEmpty(lineContent)) {
						lineOffset = this.templateBodyField.getDocument().getLineOffset(lineNo - 1);
						lineNo = lineNo - 1;

					}
				} catch (final BadLocationException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				final ErrorAnnotation errorAnnotation = this.templateBodyField.new ErrorAnnotation(lineNo, pe.getLocalizedMessage());
				this.templateBodyField.addAnnotation(errorAnnotation, new Position(lineOffset, 5));
				MessageDialog.openError(new Shell(), "Error", "Error in template body " + pe.getLocalizedMessage());
				return;
			}
		}

		final boolean validFCTags = validateFCTagAttributes();
		if (!validFCTags) {
			final String msg2 = ".\nIt is recommended to correct the errors. Do u want to proceed or stay here and correct the errors?";
			if (!showWarning("Template " + this.templateName + ", has errors in FC tags." + msg2, "Proceed Anyway", "Cancel")) {
				return;
			}
		}

		final VelocityUtil velocityUtil = VelocityUtil.getInstance();
		final boolean showErrorMessage = true;
		final boolean varvalidation = velocityUtil.validateVariablesAndMethods(this.templateBodyField.getStringValue(),
				this.currentFirstTemplateItemValue, this.currentSecondTemplateItemValue, this.additionalParamValue, this.templateName,
				this.templatePrefix, this.store, this.templateBodyField, showErrorMessage);
		velocityUtil.reset();

		/*final IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(final IProgressMonitor monitor) {
				try {
					try {
						RuntimeSingleton.parse(new StringReader(OpenEditTemplateDialog.this.templateBodyField.getStringValue()),
								OpenEditTemplateDialog.this.templateName);
					} catch (final ParseException pe) {
						pe.printStackTrace();
						if (pe.currentToken != null && pe.currentToken.next != null) {
							final int lineNo = pe.currentToken.next.beginLine - 1;
							final int colNo = pe.currentToken.next.beginColumn - 1;
							MessageDialog.openError(new Shell(), "Error", "Error in template body " + pe.getLocalizedMessage());
							return;
						}
					}

					final VelocityUtil velocityUtil = VelocityUtil.getInstance();
					OpenEditTemplateDialog.this.validVariables = velocityUtil.validateVariablesAndMethods(
							OpenEditTemplateDialog.this.templateBodyField.getStringValue(),
							OpenEditTemplateDialog.this.currentFirstTemplateItemValue,
							OpenEditTemplateDialog.this.currentSecondTemplateItemValue, OpenEditTemplateDialog.this.additionalParamValue,
							OpenEditTemplateDialog.this.templateName, OpenEditTemplateDialog.this.templatePrefix,
							OpenEditTemplateDialog.this.store);
					velocityUtil.reset();

				} finally {
					monitor.done();
				}
			}

		};

		final IWorkbench wb = PlatformUI.getWorkbench();
		final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		try {
			new ProgressMonitorDialog(new Shell()).run(false, false, op);
		} catch (final InvocationTargetException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} catch (final InterruptedException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}*/
		//		final boolean varvalidation = new VelocityUtil().validateVariablesAndMethods(this.templateBodyField.getStringValue(),
		//				this.currentFirstTemplateItemValue, this.currentSecondTemplateItemValue, this.additionalParamValue, this.templateName,
		//				this.templatePrefix, this.store);
		if (varvalidation) { //if (this.validVariables) {
			setErrorMessage(this.defaultMessage);
			super.okPressed();
		} else {
			return;
		}

	}

	/**
	 *
	 */
	public void setSourceItems() {
		if (isEmpty(this.currentFirstTemplateItemValue)) {
			this.currentFirstTemplateItemValue = this.prefValueOf1stTemplateItem;
		}
		if (isEmpty(this.currentSecondTemplateItemValue)) {
			this.currentSecondTemplateItemValue = this.prefValueOf2ndTempalteItem;
		}
		if (isEmpty(this.additionalParamValue)) {
			this.additionalParamValue = getPreferenceStore().getString(
					getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_ADDITIONAL_PARAMETERS));
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getNewValue().equals(event.getOldValue())) {
			return;
		}
		super.propertyChange(event);

		final Object source = event.getSource();
		final Object newValue = event.getNewValue();
		setErrorMessage(null);
		this.fieldModified = true;
		/*if (event.getNewValue().equals(event.getOldValue())) {
			return;
		}*/
		final GlobalSettings globalSettings = GlobalSettings.getInstance();

		this.prefValueOf1stTemplateItem = getPreferenceStore().getString(
				getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_FIRST_TEMPLATE_ITEM));
		this.prefValueOf2ndTempalteItem = getPreferenceStore().getString(
				getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_SECOND_TEMPLATE_ITEM));

		/* if (source == this.templatePreferencePart.enableTemplateCheckBox) {
		 final boolean enable = (Boolean) newValue;
		 enableFields(this.templatePreferencePart, enable);

		 } else*/
		if (source == this.templatePreferencePart.allowMultipleVariationCheckBox) {
			this.templatePreferencePart.templateVariationField.setEmptyStringAllowed(!(Boolean) newValue);
			if (FALSE_STR.equalsIgnoreCase(globalSettings.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD, TRUE_STR))) {
				this.templatePreferencePart.templateVariationFieldNameField.setEmptyStringAllowed(!(Boolean) newValue);
			}

		} else if (source == this.templatePreferencePart.templateVariationField) {
			if (FALSE_STR.equalsIgnoreCase(globalSettings.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD, TRUE_STR))) {
				this.templatePreferencePart.templateVariationFieldNameField.setEmptyStringAllowed(isEmpty((String) newValue));
			}

		} else if (source == this.templatePreferencePart.templateVariationFieldNameField) {
			final boolean isErr = ((String) newValue).endsWith(SPACE) || ((String) newValue).endsWith(TAB);
			setErrorMessage(isErr ? "Variation Field cannot end with whitespace" : null);
			this.templatePreferencePart.templateVariationField.setEmptyStringAllowed(isEmpty((String) newValue));

		} else if (source == this.templatePreferencePart.firstTemplateRadioButton) {
			//			setErrorMessage(null);
			this.currentFirstTemplateItemValue = (String) newValue;
			if (this.currentFirstTemplateItemValue.equals(SECOND_TEMPLATE.none.getValue())) {
				setWarningMessage("Warning: First Template Item is none");
			} else if (this.currentSecondTemplateItemValue.equals(SECOND_TEMPLATE.none.getValue())) {
				setWarningMessage("Warning: Second Template Item is none");
			} else {
				setWarningMessage(null);
			}
			enableTemplateItems(this.templatePreferencePart, newValue, false);

			validateAdditionalparameters(this.additionalParamValue);

			this.templateBodyField.updateSourceConfigurations(FIRST_TEMPLATE.getFirstTemplate(this.currentFirstTemplateItemValue),
					SECOND_TEMPLATE.getSecondTemplate(this.currentSecondTemplateItemValue));

		} else if (source == this.templatePreferencePart.secondTemplateRadioButton) {
			this.currentSecondTemplateItemValue = (String) newValue;
			if (this.currentFirstTemplateItemValue.equals(SECOND_TEMPLATE.none.getValue())) {
				setWarningMessage("Warning: First Template Item is none");
			} else if (this.currentSecondTemplateItemValue.equals(SECOND_TEMPLATE.none.getValue())) {
				setWarningMessage("Warning: Second Template Item is none");
			} else {
				setWarningMessage(null);
			}
			validateSecondTemplateItem(this.templatePreferencePart,
					this.currentFirstTemplateItemValue != null ? this.currentFirstTemplateItemValue : this.prefValueOf1stTemplateItem,
					newValue, false);

			validateAdditionalparameters(this.additionalParamValue);

			this.templateBodyField.updateSourceConfigurations(FIRST_TEMPLATE.getFirstTemplate(this.currentFirstTemplateItemValue),
					SECOND_TEMPLATE.getSecondTemplate(this.currentSecondTemplateItemValue));

		} else if (source == this.templatePreferencePart.additionalParametersField) {
			validateAdditionalparameters(newValue.toString());

		} else if (source == this.templateBodyField) {
			//System.out.println("template body changed");
			this.templateBodyField.removeAllAnnotations();
			final boolean showErrorMessage = false;
			setSourceItems();
			if (!this.templateBodyLast.trim().equals(this.templateBodyField.getStringValue().trim())) {
				final VelocityUtil velocityUtil = VelocityUtil.getInstance();
				final boolean varvalidation = velocityUtil.validateVariablesAndMethods(this.templateBodyField.getStringValue(),
						this.currentFirstTemplateItemValue, this.currentSecondTemplateItemValue, this.additionalParamValue, this.templateName,
						this.templatePrefix, this.store, this.templateBodyField, showErrorMessage);
				velocityUtil.reset();
				validateFCTagAttributes();
			}

			this.templateBodyLast = this.templateBodyField.getStringValue();
		}

	}

	private boolean validateFCTagAttributes() {
		boolean invalidAttri = false;
		boolean absentArrti = false;
		final List<TagAttributeList> tagAttriList = parseFCTag(this.templateBodyField.getStringValue());
		if (tagAttriList.isEmpty() || tagAttriList.size() < 1) {
			return true;
		}
		final List<FcTagAttributes> invalidAttributesList = FastCodeKeywordsManager.validateAttriutes(tagAttriList);
		if (!invalidAttributesList.isEmpty()) {
			markErrorAnnotation(invalidAttributesList, "Invalid Attribute", false);
			invalidAttri = true;
		}
		final List<FcTagAttributes> absentReqdAttributesList = FastCodeKeywordsManager.getabsentReqdAttriutes(tagAttriList);
		if (!absentReqdAttributesList.isEmpty()) {
			final boolean absentWord = true; //to mark word that is not der in the template body, send true, so that 1st few letters in the beginning of the line will be marked
			markErrorAnnotation(absentReqdAttributesList, "Require Attribute(s)", absentWord);
			absentArrti = true;
		}

		if (invalidAttri || absentArrti) {
			return false;
		}
		return true;
	}

	private void markErrorAnnotation(final List<FcTagAttributes> invalidAttributesList, final String errorMsg, final boolean absentWord) {
		for (final FcTagAttributes invalidAttributes : getEmptyListForNull(invalidAttributesList)) {

			if (this.templateBodyField != null) {
				int lineOffset = 0;
				int pos = 0;
				try {
					final IRegion reg = this.templateBodyField.getDocument().getLineInformation(
							invalidAttributes.getVarLineNo() > 0 ? invalidAttributes.getVarLineNo() - 1 : invalidAttributes.getVarLineNo());
					lineOffset = this.templateBodyField.getDocument().getLineOffset(
							invalidAttributes.getVarLineNo() > 0 ? invalidAttributes.getVarLineNo() - 1 : invalidAttributes.getVarLineNo());
					final String lineContent = this.templateBodyField.getDocument().get(reg.getOffset(), reg.getLength());
					/*System.out.println(lineContent);
					System.out.println(lineContent.indexOf(invalidVar.getVarName()));
					System.out.println(reg.toString());
					System.out.println(lineOffset);*/
					if (absentWord) {
						pos = lineOffset + getNoOfTabs(lineContent);
					} else {
						pos = lineOffset + invalidAttributes.getVarCol();// - getNoOfTabs(lineContent) * 7; //lineContent.indexOf(invalidAttributes.getVarName()); //(lineContent.indexOf(invalidAttributes.getVarName()) == -1 ? 0 : lineContent.indexOf(invalidAttributes.getVarName()));
					}
				} catch (final BadLocationException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				final ErrorAnnotation errorAnnotation = this.templateBodyField.new ErrorAnnotation(
						invalidAttributes.getVarLineNo() > 0 ? invalidAttributes.getVarLineNo() - 1 : invalidAttributes.getVarLineNo(),
						errorMsg + " '" + invalidAttributes.getVarName() + "'");

				this.templateBodyField.addAnnotation(errorAnnotation, new Position(pos, invalidAttributes.getVarName().length()));
			}
			//templateBodyField.getDocument().

		}
	}

	/**
	 * @param currentAdditionalparameterValue
	 */
	private void validateAdditionalparameters(final String currentAdditionalparameterValue) {
		if (!isEmpty(currentAdditionalparameterValue)) {
			List<String> placeHoldersList = new ArrayList<String>();
			placeHoldersList = TemplateItemsUtil.getInstance().getPlaceHoldersList(
					this.currentFirstTemplateItemValue == null ? this.prefValueOf1stTemplateItem : this.currentFirstTemplateItemValue,
					this.currentSecondTemplateItemValue == null ? this.prefValueOf2ndTempalteItem : this.currentSecondTemplateItemValue);
			try {
				final List<FastCodeAdditionalParams> fcAdditnlParamList = parseAdditonalParam(currentAdditionalparameterValue);
				for (final FastCodeAdditionalParams additnalParam : fcAdditnlParamList.toArray(new FastCodeAdditionalParams[0])) {
					if (placeHoldersList.contains(additnalParam.getName())) {
						setErrorMessage(additnalParam.getName() + " is a built in variable and cannot be used as additional parameter");
					}
					if (!isEmpty(additnalParam.getAllowedValues()) && !isEmpty(additnalParam.getDefaultValue())) {
						for (final String allowedValue : additnalParam.getAllowedValues().split(SPACE)) {
							if (!additnalParam.getDefaultValue().equals(allowedValue)) {
								setErrorMessage("Additional Parameter - " + additnalParam.getName()
										+ ", value attribute must be one of the allowed Values.");
							} else {
								setErrorMessage(this.defaultMessage);
								break;
							}
						}
					}
				}
			} catch (final FastCodeException fcExp) {
				//throw new Exception(ex);
				setErrorMessage(fcExp.getMessage());
			}

			/*final String addtnlParams[] = currentAdditionalparameterValue.split("\\s+");

			final Map<String, String> addtnParamMap = new HashMap<String, String>();
			List<String> placeHoldersList = new ArrayList<String>();
			for (final String params : addtnlParams) {

				if (params.contains(COLON)) {
					final String parseParam[] = params.split(COLON);

					if (parseParam.length == 2) {
						final String type = parseParam[1];

						if (isEmpty(type)
								|| !(type.equals(FIRST_TEMPLATE.Class.getValue()) || type.equals(FIRST_TEMPLATE.File.getValue())
										|| type.equals(FIRST_TEMPLATE.Package.getValue()) || type.equals(FIRST_TEMPLATE.Folder.getValue())
										|| type.equalsIgnoreCase(RETURN_TYPES.JAVAPROJECT.getValue())
										|| type.equalsIgnoreCase(RETURN_TYPES.LOCALVAR.getValue())
										|| type.equalsIgnoreCase(RETURN_TYPES.PROJECT.getValue()) || type
											.equalsIgnoreCase(RETURN_TYPES.BOOLEAN.getValue()) || type.equalsIgnoreCase(RETURN_TYPES.INTERFACE.getValue()) || type
											.equalsIgnoreCase(RETURN_TYPES.ENUMERATION.getValue()))) {
							setErrorMessage("Additional Parameter type can be only class/file/package/folder/project/javaProject/localvar/boolean/interface/enumeration.");
						}
						//						else {
						//							setErrorMessage(null);
						//						}

						placeHoldersList = TemplateItemsUtil.getInstance().getPlaceHoldersList(
								this.currentFirstTemplateItemValue == null ? this.prefValueOf1stTemplateItem
										: this.currentFirstTemplateItemValue,
								this.currentSecondTemplateItemValue == null ? this.prefValueOf2ndTempalteItem
										: this.currentSecondTemplateItemValue);
						if (placeHoldersList.contains(parseParam[0])) {
							setErrorMessage(parseParam[0] + " is a built in variable and cannot be used as additional parameter");
						}
						if (addtnParamMap.containsKey(parseParam[0])) {
							setErrorMessage("Duplicate Additional Parameter name " + parseParam[0]);
						}
						addtnParamMap.put(parseParam[0], type);
						} else if (parseParam.length == 1) {
							setErrorMessage("Additional Parameter must have type class/file/package/folder/project/javaProject/localvar/boolean.");
					}

				} else {
					placeHoldersList = TemplateItemsUtil.getInstance().getPlaceHoldersList(
							this.currentFirstTemplateItemValue == null ? this.prefValueOf1stTemplateItem
									: this.currentFirstTemplateItemValue,
							this.currentSecondTemplateItemValue == null ? this.prefValueOf2ndTempalteItem
									: this.currentSecondTemplateItemValue);
					if (placeHoldersList.contains(params)) {
						setErrorMessage(params + " is a built in variable and cannot be used as additional parameter");
					}
					if (addtnParamMap.containsKey(params)) {
						setErrorMessage("Duplicate Additional Parameter name " + params);
					}
					addtnParamMap.put(params, null);
				}

			}*/
			this.additionalParamValue = currentAdditionalparameterValue;
		}

	}

	/**
	 * @param templatePreferencePart
	 * @param currentFirstTemplateItemValue
	 * @param newValueOfSecondTemplateItem
	 * @param onLoad
	 */
	private void validateSecondTemplateItem(final TemplatePreferencePart templatePreferencePart,
			final String currentFirstTemplateItemValue, final Object newValueOfSecondTemplateItem, final boolean onLoad) {

		if (currentFirstTemplateItemValue.equals("package")) {
			if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.field.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.both.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.custom.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.method.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.file.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.property.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.data.getValue())) {
				setErrorMessage("Please choose only Class or None in Second Template Item.");
			} else {
				setErrorMessage(null);
			}
		} else if (currentFirstTemplateItemValue.equals("folder")) {

			if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.field.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.both.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.custom.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.method.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.Class.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.property.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.data.getValue())) {
				setErrorMessage("Please choose only File or None in Second Template Item.");
			} else {
				setErrorMessage(null);
			}
		} else if (currentFirstTemplateItemValue.equals("class")) {

			if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.Class.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.file.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.property.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.data.getValue())) {

				setErrorMessage("Please choose only Field, Method, Both or Custom in Second Template Item.");
			} else {
				if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.field.getValue())
						|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.both.getValue())
						|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.custom.getValue())) {

					templatePreferencePart.getterSetterRadioButton.setEnabled(true, this.parent);
				} else if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.method.getValue())
						|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.none.getValue())) {

					templatePreferencePart.getterSetterRadioButton.setEnabled(false, this.parent);

				}
				setErrorMessage(null);
			}
		}

		else if (currentFirstTemplateItemValue.equals("file")) {

			if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.Class.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.file.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.field.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.both.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.custom.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.method.getValue())) {
				setErrorMessage("Please choose data, property or None in Second Template Item.");
			} else {

				setErrorMessage(null);
				if (!onLoad && newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.property.getValue())) {
					MessageDialog.openWarning(null, "Second Template Item as property: warning",
							"This template is applicable only for property files ");
				}
			}
		} else if (currentFirstTemplateItemValue.equals("enum")) {

			if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.Class.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.file.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.both.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.custom.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.method.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.property.getValue())
					|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.data.getValue())) {

				templatePreferencePart.getterSetterRadioButton.setEnabled(false, this.parent);
				setErrorMessage("Please choose only Field or None in Second Template Item.");
			} else {
				if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.field.getValue())) {
					templatePreferencePart.getterSetterRadioButton.setEnabled(true, this.parent);
				} else if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.none.getValue())) {
					templatePreferencePart.getterSetterRadioButton.setEnabled(false, this.parent);
				}
				setErrorMessage(null);
			}
		} else if (currentFirstTemplateItemValue.equals("none")) {
			if (!onLoad) {
				if (newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.Class.getValue())
						|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.file.getValue())
						|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.both.getValue())
						|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.custom.getValue())
						|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.method.getValue())
						|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.property.getValue())
						|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.field.getValue())
						|| newValueOfSecondTemplateItem.equals(SECOND_TEMPLATE.data.getValue())) {
					setErrorMessage("Second Template item should be none");

				} else {
					setErrorMessage(null);
				}

			}
		}
		this.currentFirstTemplateItemValue = currentFirstTemplateItemValue;

	}

	/**
	 * @param templatePreferencePart
	 */
	private void refreshTemplateItems(final TemplatePreferencePart templatePreferencePart) {

		getPreferenceStore().setValue(getTemplatePreferenceKey(templatePreferencePart.templateName, P_TEMPLATE_NUMBER_REQUIRED_ITEMS), 0);
		templatePreferencePart.numberOfRequiredClassesField.load();
		getPreferenceStore().setValue(getTemplatePreferenceKey(templatePreferencePart.templateName, P_TEMPLATE_SECOND_TEMPLATE_ITEM),
				SECOND_TEMPLATE.none.getValue());
		templatePreferencePart.secondTemplateRadioButton.load();
		getPreferenceStore().setValue(getTemplatePreferenceKey(templatePreferencePart.templateName, P_TEMPLATE_GETTER_SETTER),
				GETTER_SETTER.NONE.getValue());
		templatePreferencePart.getterSetterRadioButton.load();

	}

	/**
	 * @param templatePreferencePart
	 * @param newValueOfFirstTemplateItem
	 * @param onLoad
	 */
	private void enableTemplateItems(final TemplatePreferencePart templatePreferencePart, final Object newValueOfFirstTemplateItem,
			final boolean onLoad) {
		if (isEmpty(this.currentSecondTemplateItemValue)) {
			this.currentSecondTemplateItemValue = this.prefValueOf2ndTempalteItem;
		}
		if (newValueOfFirstTemplateItem.equals(FIRST_TEMPLATE.Class.getValue())) {
			templatePreferencePart.secondTemplateRadioButton.setEnabled(true, this.parent);
			if (this.currentSecondTemplateItemValue.equals(SECOND_TEMPLATE.field.getValue())
					|| this.currentSecondTemplateItemValue.equals(SECOND_TEMPLATE.both.getValue())
					|| this.currentSecondTemplateItemValue.equals(SECOND_TEMPLATE.custom.getValue())) {

				templatePreferencePart.getterSetterRadioButton.setEnabled(true, this.parent);

			} else if (this.currentSecondTemplateItemValue.equals(SECOND_TEMPLATE.method.getValue())
					|| this.currentSecondTemplateItemValue.equals(SECOND_TEMPLATE.none.getValue())) {
				templatePreferencePart.getterSetterRadioButton.setEnabled(false, this.parent);
			}
			templatePreferencePart.numberOfRequiredClassesField.setEnabled(true, this.parent);
			getPreferenceStore().setValue(
					getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_NUMBER_REQUIRED_ITEMS), 1);
			templatePreferencePart.numberOfRequiredClassesField.load();
			templatePreferencePart.itemPatternField.setEnabled(true, this.parent);

		} else if (newValueOfFirstTemplateItem.equals(FIRST_TEMPLATE.File.getValue())) {

			templatePreferencePart.secondTemplateRadioButton.setEnabled(true, this.parent);
			templatePreferencePart.getterSetterRadioButton.setEnabled(false, this.parent);
			templatePreferencePart.numberOfRequiredClassesField.setEnabled(false, this.parent);
			getPreferenceStore().setValue(
					getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_NUMBER_REQUIRED_ITEMS), 1);
			templatePreferencePart.numberOfRequiredClassesField.load();
			templatePreferencePart.itemPatternField.setEnabled(true, this.parent);
			//templatePreferencePart.itemPatternField.setEnabled(true, this.parent);
			/*	this.currentSecondTemplateItemValue = SECOND_TEMPLATE.none.getValue();
					refreshTemplateItems(templatePreferencePart);*/

		} else if (newValueOfFirstTemplateItem.equals(FIRST_TEMPLATE.Package.getValue())
				|| newValueOfFirstTemplateItem.equals(FIRST_TEMPLATE.Folder.getValue())) {

			templatePreferencePart.secondTemplateRadioButton.setEnabled(true, this.parent);
			templatePreferencePart.getterSetterRadioButton.setEnabled(false, this.parent);
			templatePreferencePart.numberOfRequiredClassesField.setEnabled(false, this.parent);
			templatePreferencePart.itemPatternField.setEnabled(true, this.parent);
			getPreferenceStore().setValue(
					getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_NUMBER_REQUIRED_ITEMS), 1);
			templatePreferencePart.numberOfRequiredClassesField.load();
			getPreferenceStore().setValue(getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_GETTER_SETTER),
					GETTER_SETTER.NONE.getValue());
			templatePreferencePart.getterSetterRadioButton.load();

		} else if (newValueOfFirstTemplateItem.equals(FIRST_TEMPLATE.Enumeration.getValue())) {

			templatePreferencePart.secondTemplateRadioButton.setEnabled(true, this.parent);
			templatePreferencePart.itemPatternField.setEnabled(true, this.parent);
			if (this.currentSecondTemplateItemValue.equals(SECOND_TEMPLATE.field.getValue())) {
				templatePreferencePart.getterSetterRadioButton.setEnabled(true, this.parent);
			} else if (this.currentSecondTemplateItemValue.equals(SECOND_TEMPLATE.none.getValue())) {

				templatePreferencePart.getterSetterRadioButton.setEnabled(false, this.parent);
			}
			templatePreferencePart.numberOfRequiredClassesField.setEnabled(false, this.parent);
			getPreferenceStore().setValue(
					getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_NUMBER_REQUIRED_ITEMS), 1);
			templatePreferencePart.numberOfRequiredClassesField.load();
		} else if (newValueOfFirstTemplateItem.equals(FIRST_TEMPLATE.None.getValue())) {

			templatePreferencePart.secondTemplateRadioButton.setEnabled(false, this.parent);
			templatePreferencePart.getterSetterRadioButton.setEnabled(false, this.parent);
			templatePreferencePart.numberOfRequiredClassesField.setEnabled(false, this.parent);
			//			getPreferenceStore().setValue(
			//					getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_NUMBER_REQUIRED_ITEMS), 0);
			//			templatePreferencePart.numberOfRequiredClassesField.load();
			templatePreferencePart.itemPatternField.setEnabled(false, this.parent);
			//
			//			if (onLoad) {

			refreshTemplateItems(templatePreferencePart);
			this.currentSecondTemplateItemValue = SECOND_TEMPLATE.none.getValue();

			//			}
		}

		validateSecondTemplateItem(templatePreferencePart, (String) newValueOfFirstTemplateItem, this.currentSecondTemplateItemValue,
				onLoad);

	}

	/**
	 * @param parent
	 */
	private void createErrorMessageText(final Composite parent) {

		this.errorMessageText = new Text(parent, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
		//this.errorMessageText.setForeground(new Color(null, 255, 0, 0));
		this.errorMessageText.setForeground(FastCodeColor.getErrorMsgColor());
		this.errorMessageText.setLayoutData(new GridData(GridData.FILL_BOTH));
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.heightHint = 2 * this.errorMessageText.getLineHeight();
		this.errorMessageText.setLayoutData(gridData);
		gridData.horizontalSpan = 2;
		setErrorMessage(this.defaultMessage);

		//		new Label(parent, SWT.NULL);
	}

	/**
	 * @param errorMessage
	 */
	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
		String message = errorMessage != null ? errorMessage : EMPTY_STR;
		message = this.warningMessage != null ? message + EMPTY_STR + this.warningMessage : message;
		if (errorMessage != null) {
			this.errorMessageText.setForeground(FastCodeColor.getErrorMsgColor());//(new Color(null, 255, 0, 0));
		}

		if (this.errorMessageText != null && !this.errorMessageText.isDisposed()) {

			this.errorMessageText.setText(message);
			final boolean hasError = errorMessage != null || this.warningMessage != null;
			this.errorMessageText.setEnabled(hasError);
			this.errorMessageText.setVisible(hasError);
			this.errorMessageText.getParent().update();
			final Control button = getButton(IDialogConstants.OK_ID);
			if (button != null) {
				button.setEnabled(errorMessage == null);
			}
		}
	}

	/**
	 * @param warningMessage
	 */
	public void setWarningMessage(final String warningMessage) {
		this.warningMessage = warningMessage;
		String message = warningMessage != null ? warningMessage : EMPTY_STR;
		message = this.errorMessage != null ? this.errorMessage + EMPTY_STR + message : message;
		//this.errorMessageText.setForeground(new Color(null, 64, 96, 192));
		this.errorMessageText.setForeground(FastCodeColor.getWarningMsgPrefPageColor());
		if (this.errorMessageText != null && !this.errorMessageText.isDisposed()) {

			this.errorMessageText.setText(message);
			final boolean hasError = warningMessage != null || this.errorMessage != null;
			this.errorMessageText.setEnabled(hasError);
			this.errorMessageText.setVisible(hasError);
			this.errorMessageText.getParent().update();
			if (this.errorMessage != null) {
				this.errorMessageText.setForeground(FastCodeColor.getErrorMsgColor());//(new Color(null, 255, 0, 0));
				final Control button = getButton(IDialogConstants.OK_ID);
				if (button != null) {
					button.setEnabled(this.errorMessage == null);
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

		templatePreferencePart.templateNameField.setEnabled(enable, this.parent);

		if (this.isDetailedTemplate) {
			// templatePreferencePart.classNameField.setEnabled(enable,
			// parent);

			templatePreferencePart.firstTemplateRadioButton.setEnabled(enable, this.parent);
			this.prefValueOf1stTemplateItem = getPreferenceStore().getString(
					getTemplatePreferenceKey(templatePreferencePart.templateName, P_TEMPLATE_FIRST_TEMPLATE_ITEM));
			this.prefValueOf2ndTempalteItem = getPreferenceStore().getString(
					getTemplatePreferenceKey(this.templatePreferencePart.templateName, P_TEMPLATE_SECOND_TEMPLATE_ITEM));
			if (!enable) {
				templatePreferencePart.secondTemplateRadioButton.setEnabled(enable, this.parent);
				templatePreferencePart.getterSetterRadioButton.setEnabled(enable, this.parent);
				templatePreferencePart.numberOfRequiredClassesField.setEnabled(enable, this.parent);
				templatePreferencePart.itemPatternField.setEnabled(enable, this.parent);
			}

			enableTemplateItems(templatePreferencePart, this.prefValueOf1stTemplateItem, true);
			this.templateBodyField.updateSourceConfigurations(FIRST_TEMPLATE.getFirstTemplate(this.currentFirstTemplateItemValue),
					SECOND_TEMPLATE.getSecondTemplate(this.currentSecondTemplateItemValue));
			templatePreferencePart.allowMultipleVariationCheckBox.setEnabled(enable, this.parent);
			//			templatePreferencePart.itemPatternField.setEnabled(enable, this.parent);
		}
		templatePreferencePart.templateVariationField.setEnabled(enable, this.parent);
		templatePreferencePart.templateDescriptionField.setEnabled(enable, this.parent);
		if (FALSE_STR.equalsIgnoreCase(globalSettings.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD, TRUE_STR))) {
			templatePreferencePart.templateVariationFieldNameField.setEnabled(enable, this.parent);
		}
		if (this.isShowAllowedFileExtension) {
			templatePreferencePart.templateAllowedFileNamesField.setEnabled(enable, this.parent);
		}
		setAdditionalParamValues();
		templatePreferencePart.additionalParametersField.setEnabled(enable, this.parent);
		templatePreferencePart.templateBodyField.setEnabled(enable, this.parent);

	}

	public String getTemplateName() {
		return this.templateName;
	}

	public void setTemplateName(final String name) {
		this.templateName = name;
	}

	public boolean isExistingTemplateRenamed() {
		return this.existingTemplateRenamed;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void setAdditionalParamValues() {
		final VariablesUtil varUtil = VariablesUtil.getInstance();
		//final ArrayList<String> addlparams = new ArrayList<String>();
		final Map<String, String> addlparamsMap = new HashMap<String, String>();
		if (!isEmpty(this.additionalParameterField.getStringValue())) {
			List<FastCodeAdditionalParams> fcAdditnlParamList;
			try {
				fcAdditnlParamList = parseAdditonalParam(this.additionalParameterField.getStringValue());
				varUtil.setadditionalParamList(fcAdditnlParamList);
			} catch (final FastCodeException ex) {
				//throw new Exception(ex);
			}
			/*for (final String addlparam : this.additionalParameterField.getStringValue().split(SPACE)) {
				if (!addlparam.trim().equals(EMPTY_STR)) {
					if (addlparam.contains(COLON)) {
						addlparamsMap.put(addlparam.split(COLON)[0].trim(), addlparam.split(COLON)[1].trim());
						//addlparams.add(addlparam.split(COLON)[0].trim());
					} else {
						addlparamsMap.put(addlparam, null);
						//addlparams.add(addlparam);
					}
				}
			}
			varUtil.setadditionalParamList(addlparamsMap); //this line is moved out of for loop
			*/} else {
			varUtil.setadditionalParamList(null);
		}
		/*if (this.templateName.equals(TEMPLATE_TYPE_SAMPLE_FILE_TEMPLATE)) {//changed but dont know this will work or not
			varUtil.setFilePlaceHolderValue(getPreferenceStore().getString(P_FILE_TEMPLATE_PLACHOLDER_NAME));
		}*/

	}

	@Override
	public void cancelPressed() {
		if (this.fieldModified) {
			final MessageDialog dialog = new MessageDialog(new Shell(), "Warning", null,
					"Changes made to the template has not been saved. Do you want to save the changes or proceed anyway?",
					MessageDialog.WARNING, new String[] { "Proceed Anyway", "Cancel" }, 0) {
				@Override
				protected void buttonPressed(final int buttonId) {
					setReturnCode(buttonId);
					close();
				}
			};
			dialog.open();
			if (dialog.getReturnCode() == 0) {
				final VelocityUtil veloUtil = VelocityUtil.getInstance();
				veloUtil.clearLocalVarsList();
				super.cancelPressed();
			} else {
				return;
			}
		} else {
			final VelocityUtil veloUtil = VelocityUtil.getInstance();
			veloUtil.clearLocalVarsList();
			super.cancelPressed();
		}
	}
}

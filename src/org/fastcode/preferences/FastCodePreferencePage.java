/*
 * Fast Code Plugin for Eclipse
 *
 * Copyright (C) 2008  Gautam Dev
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA.
 *
 * Plugin Home Page: http://fast-code.sourceforge.net/
 */

package org.fastcode.preferences;

import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.preferences.PreferenceConstants.COPY_METHOD_CHOICE_TYPES;
import static org.fastcode.preferences.PreferenceConstants.EXPORT_SETTINGS_TYPES;
import static org.fastcode.preferences.PreferenceConstants.PARAMETERIZED_CHOICE_TYPES;
import static org.fastcode.preferences.PreferenceConstants.PARAMETERIZED_NAME_CHOICE_TYPES;
import static org.fastcode.preferences.PreferenceConstants.P_AUTO_SAVE;
import static org.fastcode.preferences.PreferenceConstants.P_EXPORT_SETTINGS;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_CLASS_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_CLASS_FIELD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_CLASS_HEADER;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_CLASS_METHOD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_CONVERT_METHOD_PARAM_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_COPY_METHOD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_DATE_FORMAT;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_FINAL_MODIFIER_FOR_METHOD_ARGS;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_IMPL_SUB_PACKAGE;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_IMPL_SUFFIX;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_INTERFACE_METHOD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_PARAMETERIZED_IGNORE_EXTENSIONS;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_PARAMETERIZED_NAME_STRATEGY;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_PARAMETERIZED_TYPE_CHOICE;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_PLACE_HOLDER_VALUES;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_SOURCE_PATH_JAVA;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_SOURCE_PATH_RESOURCES;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_SOURCE_PATH_TEST;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_STATIC_IMPORT_TYPE_CHOICE;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_USER;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_USE_DEFAULT_FOR_PATH;
import static org.fastcode.preferences.PreferenceConstants.P_SHOW_TIPS;
import static org.fastcode.preferences.PreferenceConstants.P_STATIC_MEMBERS_AND_TYPES;
import static org.fastcode.preferences.PreferenceConstants.STATIC_IMPORT_CHOICE_TYPES;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.MessageUtil.showError;
import static org.fastcode.util.SourceUtil.checkForJavaProjectInWorkspace;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeConstants.EXPORT_OPTIONS;
import org.fastcode.common.FastCodeConstants.FIELDS;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.templates.viewer.TemplateFieldEditor;
import org.fastcode.util.MultiStringFieldEditor;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 *
 * @author Gautam
 */

public class FastCodePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	//	private StringFieldEditor		packagePattern;
	//	private StringFieldEditor		classNamePattern;
	/*private StringFieldEditor		classBodyField;
	private StringFieldEditor		classHeaderField;
	private StringFieldEditor		classMethodBodyField;
	private StringFieldEditor		classFieldBodyField;
	private StringFieldEditor		interfaceMethodBodyField;*/
	private TemplateFieldEditor		classBodyField;
	private TemplateFieldEditor		classHeaderField;
	private TemplateFieldEditor		classMethodBodyField;
	private TemplateFieldEditor		classFieldBodyField;
	private TemplateFieldEditor		interfaceMethodBodyField;
	private StringFieldEditor		implSuffix;
	private StringFieldEditor		implSubPackage;
	//private StringFieldEditor		constructorBodyField;
	private StringFieldEditor		sourcePathJavaField;
	private StringFieldEditor		sourcePathTestField;
	private StringFieldEditor		sourcePathResourcesField;
	private BooleanFieldEditor		useDefaultForPathField;
	private StringFieldEditor		user;
	private StringFieldEditor		dateFormat;
	private MultiStringFieldEditor	placeHolderValues;
	//private StringFieldEditor		equalsMethodBodyField;
	//private StringFieldEditor		hashcodeMethodBodyField;
	//private StringFieldEditor		toStringMethodBodyField;
	//private BooleanFieldEditor		getterSetterForPrivate;
	private BooleanFieldEditor		finalModifierForMethodArgsCheckBox;
	private BooleanFieldEditor		showTipsCheckBox;
	private StringFieldEditor		parameterizedIgnoreExtensions;
	//private StringFieldEditor		convertMethodParamPatternField;
	private TemplateFieldEditor		convertMethodParamPatternField;
	private RadioGroupFieldEditor	parameterizedNameChoiceTypesRadio;
	private BooleanFieldEditor		autoSaveCheck;
	private boolean					errorShown	= false;
	private final String			defaultPlaceHolderValue;
	private ListEditor				staticMembersAndTypesList;
	private RadioGroupFieldEditor	exportSettingsRadio;

	/**
	 *
	 */
	public FastCodePreferencePage() {
		super(GRID);
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		setPreferenceStore(store);
		// setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Fast Code Preference Page the plugin");
		this.defaultPlaceHolderValue = getPreferenceStore().getDefaultString(P_GLOBAL_PLACE_HOLDER_VALUES);
		checkForJavaProjectInWorkspace();
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		//		this.packagePattern = new StringFieldEditor(P_GLOBAL_PACKAGE_PATTERN, "Package Name Pattern :", getFieldEditorParent());
		//		addField(this.packagePattern);
		//
		//		this.classNamePattern = new StringFieldEditor(P_GLOBAL_CLASS_NAME_PATTERN, "Class Name Pattern :", getFieldEditorParent());
		//		addField(this.classNamePattern);

		//this.classHeaderField = new MultiStringFieldEditor(P_GLOBAL_CLASS_HEADER, "Class Header :", getFieldEditorParent());
		this.classHeaderField = new TemplateFieldEditor(P_GLOBAL_CLASS_HEADER, "Class Header :", getFieldEditorParent(), TEMPLATE,
				FIELDS.TEMPLATE_BODY, SWT.MULTI);
		addField(this.classHeaderField);

		//this.classBodyField = new MultiStringFieldEditor(P_GLOBAL_CLASS_BODY, "Template For Class :", getFieldEditorParent());
		this.classBodyField = new TemplateFieldEditor(P_GLOBAL_CLASS_BODY, "Template For Class :", getFieldEditorParent(), TEMPLATE,
				FIELDS.TEMPLATE_BODY, SWT.MULTI);
		addField(this.classBodyField);

		//this.classFieldBodyField = new MultiStringFieldEditor(P_GLOBAL_CLASS_FIELD_BODY, "Field Body for Class:", getFieldEditorParent());
		this.classFieldBodyField = new TemplateFieldEditor(P_GLOBAL_CLASS_FIELD_BODY, "Field Body for Class:", getFieldEditorParent(),
				TEMPLATE, FIELDS.TEMPLATE_BODY, SWT.MULTI);
		addField(this.classFieldBodyField);

		// this.constructorBodyField = new
		// MultiStringFieldEditor(P_GLOBAL_CONSTRUCTOR_BODY,
		// "Constructor Body for Class:", getFieldEditorParent());
		// addField(this.constructorBodyField);

		/*this.classMethodBodyField = new MultiStringFieldEditor(P_GLOBAL_CLASS_METHOD_BODY, "Method Template for Class:",
				getFieldEditorParent());*/
		this.classMethodBodyField = new TemplateFieldEditor(P_GLOBAL_CLASS_METHOD_BODY, "Method Template for Class:",
				getFieldEditorParent(), TEMPLATE, FIELDS.TEMPLATE_BODY, SWT.MULTI);
		addField(this.classMethodBodyField);

		/*this.interfaceMethodBodyField = new MultiStringFieldEditor(P_GLOBAL_INTERFACE_METHOD_BODY, "Method Template for Interface:",
				getFieldEditorParent());*/
		this.interfaceMethodBodyField = new TemplateFieldEditor(P_GLOBAL_INTERFACE_METHOD_BODY, "Method Template for Interface:",
				getFieldEditorParent(), TEMPLATE, FIELDS.TEMPLATE_BODY, SWT.MULTI);
		addField(this.interfaceMethodBodyField);

		this.implSuffix = new StringFieldEditor(P_GLOBAL_IMPL_SUFFIX, "Append Suffix for Implemenation :", getFieldEditorParent());
		addField(this.implSuffix);

		this.implSubPackage = new StringFieldEditor(P_GLOBAL_IMPL_SUB_PACKAGE, "Global Implementation Sub Package :",
				getFieldEditorParent());
		addField(this.implSubPackage);

		this.useDefaultForPathField = new BooleanFieldEditor(P_GLOBAL_USE_DEFAULT_FOR_PATH,
				"Always use Default values for Path and disable choice for path fields:", getFieldEditorParent());
		addField(this.useDefaultForPathField);

		this.sourcePathJavaField = new StringFieldEditor(P_GLOBAL_SOURCE_PATH_JAVA, "Default Source Path For Java :",
				getFieldEditorParent());
		addField(this.sourcePathJavaField);

		this.sourcePathTestField = new StringFieldEditor(P_GLOBAL_SOURCE_PATH_TEST, "Default Source Path For Unit Test :",
				getFieldEditorParent());
		addField(this.sourcePathTestField);

		this.sourcePathResourcesField = new StringFieldEditor(P_GLOBAL_SOURCE_PATH_RESOURCES, "Default Source Path For Resources :",
				getFieldEditorParent());
		addField(this.sourcePathResourcesField);
		// String enable=this.
		// this.useDefaultForPathField.get
		final Boolean isDefaultChecked = getPreferenceStore().getBoolean(P_GLOBAL_USE_DEFAULT_FOR_PATH);
		this.sourcePathJavaField.setEnabled(isDefaultChecked, getFieldEditorParent());
		this.sourcePathTestField.setEnabled(isDefaultChecked, getFieldEditorParent());
		this.sourcePathResourcesField.setEnabled(isDefaultChecked, getFieldEditorParent());
		/*this.getterSetterForPrivate = new BooleanFieldEditor(P_GLOBAL_GETTER_SETTER_FOR_PRIVATE,
				"When creating fields, always create getter setter for private/protected fields :", getFieldEditorParent());
		addField(this.getterSetterForPrivate);*/

		// this.equalsMethodBodyField = new
		// MultiStringFieldEditor(P_GLOBAL_EQUALS_METHOD_BODY,
		// "Method Body for Equals:", getFieldEditorParent());
		// addField(this.equalsMethodBodyField);

		// this.hashcodeMethodBodyField = new
		// MultiStringFieldEditor(P_GLOBAL_HASHCODE_METHOD_BODY,
		// "Method Body for Hashcode:", getFieldEditorParent());
		// addField(this.hashcodeMethodBodyField);

		// this.toStringMethodBodyField = new
		// MultiStringFieldEditor(P_GLOBAL_TOSTRING_METHOD_BODY,
		// "Method Body for To String:", getFieldEditorParent());
		// addField(this.toStringMethodBodyField);

		this.finalModifierForMethodArgsCheckBox = new BooleanFieldEditor(P_GLOBAL_FINAL_MODIFIER_FOR_METHOD_ARGS,
				"Alway use final for method parameters wherever possible:", getFieldEditorParent());
		addField(this.finalModifierForMethodArgsCheckBox);

		final FieldEditor parameterizedTypeRadio = new RadioGroupFieldEditor(P_GLOBAL_PARAMETERIZED_TYPE_CHOICE,
				"When Creating List/Map/Set Types", PARAMETERIZED_CHOICE_TYPES.length, PARAMETERIZED_CHOICE_TYPES, getFieldEditorParent(),
				true);
		addField(parameterizedTypeRadio);

		this.parameterizedNameChoiceTypesRadio = new RadioGroupFieldEditor(P_GLOBAL_PARAMETERIZED_NAME_STRATEGY,
				"When Creating List/Map/Set Types", PARAMETERIZED_NAME_CHOICE_TYPES.length, PARAMETERIZED_NAME_CHOICE_TYPES,
				getFieldEditorParent(), true);
		addField(this.parameterizedNameChoiceTypesRadio);

		this.parameterizedIgnoreExtensions = new StringFieldEditor(P_GLOBAL_PARAMETERIZED_IGNORE_EXTENSIONS,
				"When Creating List/Map/Set Types ignore the following extensions", getFieldEditorParent());
		addField(this.parameterizedIgnoreExtensions);

		final FieldEditor staticImportTypesRadio = new RadioGroupFieldEditor(P_GLOBAL_STATIC_IMPORT_TYPE_CHOICE,
				"When Creating Import, If It Contains One or More Static Members", STATIC_IMPORT_CHOICE_TYPES.length,
				STATIC_IMPORT_CHOICE_TYPES, getFieldEditorParent(), true);
		addField(staticImportTypesRadio);

		final FieldEditor globalCopyMethodBodyTypesRadio = new RadioGroupFieldEditor(P_GLOBAL_COPY_METHOD_BODY, "When Copying a Method",
				COPY_METHOD_CHOICE_TYPES.length, COPY_METHOD_CHOICE_TYPES, getFieldEditorParent(), true);
		addField(globalCopyMethodBodyTypesRadio);

		this.showTipsCheckBox = new BooleanFieldEditor(P_SHOW_TIPS, "Show Tips :", getFieldEditorParent());
		addField(this.showTipsCheckBox);

		/*this.convertMethodParamPatternField = new MultiStringFieldEditor(P_GLOBAL_CONVERT_METHOD_PARAM_PATTERN,
				"Convert Method Param Pattern", getFieldEditorParent());*/
		this.convertMethodParamPatternField = new TemplateFieldEditor(P_GLOBAL_CONVERT_METHOD_PARAM_PATTERN,
				"Convert Method Param Pattern", getFieldEditorParent(), TEMPLATE, FIELDS.TEMPLATE_BODY, SWT.MULTI);
		addField(this.convertMethodParamPatternField);

		this.autoSaveCheck = new BooleanFieldEditor(P_AUTO_SAVE, "Automatically save files after every change :", getFieldEditorParent());
		this.autoSaveCheck.setEnabled(false, getFieldEditorParent());
		addField(this.autoSaveCheck);

		this.user = new StringFieldEditor(P_GLOBAL_USER, "User :", getFieldEditorParent());
		addField(this.user);

		this.dateFormat = new StringFieldEditor(P_GLOBAL_DATE_FORMAT, "Date Format :", getFieldEditorParent());
		addField(this.dateFormat);

		this.placeHolderValues = new MultiStringFieldEditor(P_GLOBAL_PLACE_HOLDER_VALUES, "Place Holders Used", true,
				getFieldEditorParent());
		// placeHolderValues.setEnabled(false, getFieldEditorParent());
		addField(this.placeHolderValues);

		this.staticMembersAndTypesList = new FastCodeListEditor(P_STATIC_MEMBERS_AND_TYPES, "Add New Member/Type For Static Import: ",
				getFieldEditorParent(), CONSIDER_CLASSES_AND_INTERFACES, null);

		addField(this.staticMembersAndTypesList);

		this.exportSettingsRadio = new RadioGroupFieldEditor(P_EXPORT_SETTINGS, "Export Settings:", EXPORT_SETTINGS_TYPES.length,
				EXPORT_SETTINGS_TYPES, getFieldEditorParent(), true);
		addField(this.exportSettingsRadio);
	}

	/**
	 * @param event
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		final Object newValue = event.getNewValue();
		//final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		if (event.getSource() == this.placeHolderValues) {
			if (!newValue.equals(event.getOldValue())) {
				if (!this.errorShown) {
					showError("This field can not be changed. All your changes will be reverted back.", "Prefrence Error");
					this.errorShown = true;
				}
				this.placeHolderValues.getTextControl(getFieldEditorParent()).setText(this.defaultPlaceHolderValue);
			}
		} /*else if (event.getSource() == this.getterSetterForPrivate) {
			if ((Boolean) newValue) {
				final MessageDialogWithToggle dialogWithToggle = MessageDialogWithToggle.openYesNoQuestion(getShell(), "Not Recommended",
						"It is not recommended that you always create getters/setters for field. " + "Would you still like to proceed?",
						"Remember Decision", false, store, P_GLOBAL_GETTER_SETTER_FOR_PRIVATE);
				if (dialogWithToggle.getReturnCode() != MESSAGE_DIALOG_RETURN_YES) {
					store.setValue(this.defaultPlaceHolderValue, false);
					this.getterSetterForPrivate.load();
				}
			}
			} */else if (event.getSource() == this.useDefaultForPathField) {
			this.sourcePathJavaField.setEnabled((Boolean) newValue, getFieldEditorParent());
			this.sourcePathTestField.setEnabled((Boolean) newValue, getFieldEditorParent());
			this.sourcePathResourcesField.setEnabled((Boolean) newValue, getFieldEditorParent());
		}
	}

	@Override
	public boolean performOk() {
		final boolean status = super.performOk();
		if (!status) {
			return status;
		}
		final GlobalSettings globalSettings = getInstance();
		globalSettings.setAutoSave(this.autoSaveCheck.getBooleanValue());
		globalSettings.setUser(this.user.getStringValue());
		globalSettings.setDateFormat(this.dateFormat.getStringValue());
		globalSettings.setParameterizedNameStrategy(getPreferenceStore().getString(P_GLOBAL_PARAMETERIZED_NAME_STRATEGY));
		globalSettings.setParameterizedIgnoreExtensions(this.parameterizedIgnoreExtensions.getStringValue());
		//globalSettings.setGetterSetterForPrivateFields(this.getterSetterForPrivate.getBooleanValue());
		globalSettings.setClassHeader(this.classHeaderField.getStringValue());
		globalSettings.setClassBody(this.classBodyField.getStringValue());
		globalSettings.setFieldBody(this.classFieldBodyField.getStringValue());
		globalSettings.setClassMethodBody(this.classMethodBodyField.getStringValue());
		globalSettings.setInterfaceMethodBody(this.interfaceMethodBodyField.getStringValue());
		globalSettings.setSourcePathJava(this.sourcePathJavaField.getStringValue());
		globalSettings.setSourcePathTest(this.sourcePathTestField.getStringValue());
		globalSettings.setSourcePathResources(this.sourcePathResourcesField.getStringValue());
		// globalSettings.setConstructorBody(this.constructorBodyField.getStringValue());
		globalSettings.setFinalModifierForMethodArgs(this.finalModifierForMethodArgsCheckBox.getBooleanValue());
		globalSettings.setConvertMethodParamPattern(this.convertMethodParamPatternField.getStringValue());
		// globalSettings.setEqualsMethodBody(this.equalsMethodBodyField.getStringValue());
		// globalSettings.setHashcodeMethodBody(this.hashcodeMethodBodyField.getStringValue());
		// globalSettings.setToStringMethodBody(this.toStringMethodBodyField.getStringValue());
		globalSettings.setUseDefaultForPath(this.useDefaultForPathField.getBooleanValue());
		globalSettings.setShowTips(this.showTipsCheckBox.getBooleanValue());
		globalSettings.setStaticMembersAndTypes(getPreferenceStore().getString(P_STATIC_MEMBERS_AND_TYPES));
		globalSettings.setExportSettings(EXPORT_OPTIONS.ASK_TO_OVERWRITE_OR_BACKUP);
		return status;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench workbench) {
	}

}

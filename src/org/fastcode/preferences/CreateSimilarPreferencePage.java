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

import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_ALL_TYPES;
import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_ANNOTATION_TYPES;
import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_INTERFACES;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.CONVERSION_CAMEL_CASE;
import static org.fastcode.common.FastCodeConstants.CONVERSION_CAMEL_CASE_HYPHEN;
import static org.fastcode.common.FastCodeConstants.CONVERSION_LOWER_CASE;
import static org.fastcode.common.FastCodeConstants.CONVERSION_NONE;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.MESSAGE_DIALOG_RETURN_YES;
import static org.fastcode.preferences.PreferenceConstants.CLASS_CHOICE_TYPES;
import static org.fastcode.preferences.PreferenceConstants.CREATE_NEW_PREFERENCE_SERVICE_ID;
import static org.fastcode.preferences.PreferenceConstants.P_BREAK_DATE_FIELDS;
import static org.fastcode.preferences.PreferenceConstants.P_CLASS_ANNOTATIONS;
import static org.fastcode.preferences.PreferenceConstants.P_CLASS_IMPORTS;
import static org.fastcode.preferences.PreferenceConstants.P_CLASS_INSIDE_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_CLASS_TYPE;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_END_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_FILE_CONV_TYPES;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_FILE_NAME;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_HEADER_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_ITEMS;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_LOCALE;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_LOCATION;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_START_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_CONFIG_TYPE;
import static org.fastcode.preferences.PreferenceConstants.P_CONVERT_METHOD_PARAM;
import static org.fastcode.preferences.PreferenceConstants.P_CONVERT_METHOD_PARAM_FROM;
import static org.fastcode.preferences.PreferenceConstants.P_CONVERT_METHOD_PARAM_TO;
import static org.fastcode.preferences.PreferenceConstants.P_COPY_FIELDS;
import static org.fastcode.preferences.PreferenceConstants.P_COPY_METHODS;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_CONFIG;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_DEFAULT_CONSTRUCTOR;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_FIELDS;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_FIELDS_NAME;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_INSTANCE_CONSTRUCTOR;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_METHOD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_UNIT_TEST;
import static org.fastcode.preferences.PreferenceConstants.P_CREATE_WORKING_SET;
import static org.fastcode.preferences.PreferenceConstants.P_EXCLUDE_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_FIELD_ANNOTATIONS;
import static org.fastcode.preferences.PreferenceConstants.P_FINAL_CLASS;
import static org.fastcode.preferences.PreferenceConstants.P_FROM_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_IMPLEMENT_INT;
import static org.fastcode.preferences.PreferenceConstants.P_IMPLEMENT_INTERFACES;
import static org.fastcode.preferences.PreferenceConstants.P_IMPL_SUB_PACKAGE;
import static org.fastcode.preferences.PreferenceConstants.P_INCLUDE_GETTER_SETTER_INSTACE_FROM;
import static org.fastcode.preferences.PreferenceConstants.P_INCLUDE_INSTACE_FROM;
import static org.fastcode.preferences.PreferenceConstants.P_INCLUDE_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_METHOD_ANNOTATIONS;
import static org.fastcode.preferences.PreferenceConstants.P_PACKAGE;
import static org.fastcode.preferences.PreferenceConstants.P_PROJECT;
import static org.fastcode.preferences.PreferenceConstants.P_REMOVE_CONFIG;
import static org.fastcode.preferences.PreferenceConstants.P_RESTORE_CONFIG_ITEMS;
import static org.fastcode.preferences.PreferenceConstants.P_SOURCE_PATH;
import static org.fastcode.preferences.PreferenceConstants.P_SUPER_CLASS;
import static org.fastcode.preferences.PreferenceConstants.P_TO_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_WORKING_SET_NAME;
import static org.fastcode.preferences.PreferenceUtil.createMultiStringFieldEditor;
import static org.fastcode.preferences.PreferenceUtil.createRadioGroupFieldEditor;
import static org.fastcode.preferences.PreferenceUtil.createStringFieldEditor;
import static org.fastcode.preferences.PreferenceUtil.getPreferenceLabel;
import static org.fastcode.preferences.PreferenceUtil.getPreferenceStoreValueString;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.CreateSimilarDescriptor.refreshePreferenceDataFromStore;
import static org.fastcode.util.MessageUtil.showError;
import static org.fastcode.util.MessageUtil.showWarning;
import static org.fastcode.util.SourceUtil.getAllProjects;
import static org.fastcode.util.SourceUtil.getAllSourcePathsInWorkspace;
import static org.fastcode.util.SourceUtil.getJavaProject;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.isStringInArray;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.Separator;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeConstants.CLASS_TYPE;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.util.ConfigPattern;
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
 */

public abstract class CreateSimilarPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	protected String					preferenceId;

	protected ComboFieldEditor			projectComboList;
	protected ComboFieldEditor			sourceComboList;

	protected FieldEditor				classTypeRadio;
	protected BooleanFieldEditor		createImplCheckBox;
	protected StringFieldEditor			implSubPkg;
	protected ListEditor				implSuperClass;
	protected StringButtonFieldEditor	packageFieldEditor;
	protected StringFieldEditor			fromPattern;
	protected StringFieldEditor			classBodyPattern;
	protected StringFieldEditor			classHeader;
	protected StringFieldEditor			classInsideBody;
	protected StringFieldEditor			toPattern;

	protected ListEditor				methodAnnotations;
	protected ListEditor				fieldAnnotations;
	protected ListEditor				classAnnotations;

	protected ListEditor				interfaces;
	protected ListEditor				classImports;

	protected BooleanFieldEditor		finalCheckBox;
	protected BooleanFieldEditor		copyMethodCheckBox;
	protected BooleanFieldEditor		createMethodBody;

	protected BooleanFieldEditor		createDefaultConstructor;
	protected BooleanFieldEditor		createInstanceConstructor;

	protected BooleanFieldEditor		copyFieldsCheckBox;
	protected BooleanFieldEditor		addionalFieldsCheckBox;
	protected StringFieldEditor			addionalFieldsNameField;

	/*protected BooleanFieldEditor		assignReturnCheckBox;
	protected StringFieldEditor			returnVariableName;*/

	protected BooleanFieldEditor		createWorkingSetCheckBox;
	protected BooleanFieldEditor		createUnitTestCheckBox;

	protected StringFieldEditor			workingSetName;

	protected BooleanFieldEditor		breakDateFieldsCheckBox;
	protected BooleanFieldEditor		inclInstanceCheckBox;
	protected BooleanFieldEditor		inclGetterSetterCheckBox;

	protected BooleanFieldEditor		convertMethodParamCheckBox;
	protected StringFieldEditor			convertMethodParamFromField;
	protected StringFieldEditor			convertMethodParamToField;

	protected StringFieldEditor			includePattern;
	protected StringFieldEditor			excludePattern;

	protected int						numConfigs						= 0;
	private final ConfigPattern			configPattern					= ConfigPattern.getInstance();
	protected int						numMaxConfigs					= this.configPattern.getConfigs().length;

	CreateSimilarConfigurationPart		createSimilarConfigurationPart	= new CreateSimilarConfigurationPart(this.numMaxConfigs);

	protected BooleanFieldEditor[]		createConfigCheckBox			= new BooleanFieldEditor[this.numMaxConfigs];

	protected BooleanFieldEditor		removeConfigCheckBox;

	private String						project;

	private String						sourcePath;

	/**
	 *
	 */
	public CreateSimilarPreferencePage() {
		super(GRID);
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		setPreferenceStore(store);
		// Activator.getDefault().getPreferenceStore();
		// this.project =
		// getPreferenceStore().getString(getPreferenceLabel(P_PROJECT,
		// preferenceId));
		// this.sourcePath =
		// getPreferenceStore().getString(getPreferenceLabel(P_SOURCE_PATH,
		// preferenceId));
		setDescription("Create similar preference page implementation");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		this.project = getPreferenceStore().getString(getPreferenceLabel(P_PROJECT, this.preferenceId));
		String configItems = null;
		final GlobalSettings globalSettings = getInstance();

		final String classType = getPreferenceStoreValueString(P_CLASS_TYPE, this.preferenceId);
		final boolean isClass = CLASS_TYPE.getClassType(classType) == CLASS_TYPE.CLASS;

		if (getPreferenceStore().contains(getPreferenceLabel(P_CONFIG_ITEMS, this.preferenceId))) {
			configItems = getPreferenceStore().getString(getPreferenceLabel(P_CONFIG_ITEMS, this.preferenceId));
		}

		String[] configItemArr = null;
		if (!isEmpty(configItems)) {
			configItemArr = configItems.split(COLON);
		}

		this.numConfigs = configItemArr == null ? this.numMaxConfigs : configItemArr.length;

		if (!isCreateNew()) {
			this.fromPattern = new StringFieldEditor(getPreferenceLabel(P_FROM_PATTERN, this.preferenceId), "From Clasees :", 60,
					getFieldEditorParent());
			this.fromPattern.setEmptyStringAllowed(false);
			addField(this.fromPattern);
		}

		if (!isClass) {
			new Separator(SWT.SEPARATOR | SWT.HORIZONTAL).doFillIntoGrid(getFieldEditorParent(), 10, convertHeightInCharsToPixels(2));
		}

		this.toPattern = new StringFieldEditor(getPreferenceLabel(P_TO_PATTERN, this.preferenceId), "To classes :", 40,
				getFieldEditorParent());
		this.toPattern.setEmptyStringAllowed(false);
		addField(this.toPattern);

		this.classTypeRadio = new RadioGroupFieldEditor(getPreferenceLabel(P_CLASS_TYPE, this.preferenceId), "This is a : ",
				CLASS_CHOICE_TYPES.length, CLASS_CHOICE_TYPES, getFieldEditorParent(), true);
		addField(this.classTypeRadio);
		this.classTypeRadio.setEnabled(false, getFieldEditorParent());

		final String[][] projects = getAllProjects();
		this.projectComboList = new ComboFieldEditor(getPreferenceLabel(P_PROJECT, this.preferenceId), "Project:", projects,
				getFieldEditorParent());
		addField(this.projectComboList);

		if (!globalSettings.isUseDefaultForPath()) {
			// String[][] sourcePaths =
			// getSourcePathsForProject(getPreferenceStore().getString(getPreferenceLabel(P_PROJECT,
			// preferenceId)));
			final String[][] sourcePaths = getAllSourcePathsInWorkspace();
			this.sourceComboList = new ComboFieldEditor(getPreferenceLabel(P_SOURCE_PATH, this.preferenceId), "Source Paths:", sourcePaths,
					getFieldEditorParent());
			addField(this.sourceComboList);
		}

		if (isCreateNew()) {
			this.packageFieldEditor = new StringButtonFieldEditor(getPreferenceLabel(P_PACKAGE, this.preferenceId), "Package :",
					getFieldEditorParent()) {
				@Override
				protected String changePressed() {
					try {
						final IJavaProject javaProject = getJavaProject(CreateSimilarPreferencePage.this.project);
						final SelectionDialog selectionDialog = JavaUI.createPackageDialog(getShell(), javaProject, 0, EMPTY_STR);
						final int ret = selectionDialog.open();
						if (ret == Window.CANCEL) {
							return null;
						}
						final IPackageFragment packageFragment = (IPackageFragment) selectionDialog.getResult()[0];
						return packageFragment.getElementName();
					} catch (final JavaModelException e) {
						e.printStackTrace();
					}
					return null;
				}
			};
			// final IJavaProject javaProject =
			// getJavaProject(CreateSimilarPreferencePage.this.project);
			// this.packageFieldEditor = new
			// FastCodeListEditor(getPreferenceLabel(P_PACKAGE,
			// this.preferenceId), "Package", getFieldEditorParent(),
			// IJavaElement.PACKAGE_FRAGMENT, javaProject);

			addField(this.packageFieldEditor);
			this.packageFieldEditor.setEnabled(!isEmpty(CreateSimilarPreferencePage.this.project), getFieldEditorParent());
		}

		// classBodyPattern = new
		// MultiStringFieldEditor(getPreferenceLabel(P_CLASS_BODY_PATTERN,
		// preferenceId), "To Class Pattern :", getFieldEditorParent());
		// addField(classBodyPattern);

		// new
		// AutoCompleteField(classBodyPattern.getTextControl(getFieldEditorParent()),
		// new TextContentAdapter(), keyWords);

		if (!isForValueBeans()) {
			if (!isClass) {
				this.createImplCheckBox = new BooleanFieldEditor(getPreferenceLabel(P_IMPLEMENT_INT, this.preferenceId),
						"Create an &Implementation of above interface :", getFieldEditorParent());
				addField(this.createImplCheckBox);

				this.implSubPkg = new StringFieldEditor(getPreferenceLabel(P_IMPL_SUB_PACKAGE, this.preferenceId),
						"Implementation &Sub Package :", 10, getFieldEditorParent());
				addField(this.implSubPkg);
				this.implSubPkg.setEnabled(getPreferenceStore().getBoolean(getPreferenceLabel(P_IMPLEMENT_INT, this.preferenceId)),
						getFieldEditorParent());
			}

			this.finalCheckBox = new BooleanFieldEditor(getPreferenceLabel(P_FINAL_CLASS, this.preferenceId), "Make this Class final :",
					getFieldEditorParent());
			addField(this.finalCheckBox);
			if (this.preferenceId.equals(CREATE_NEW_PREFERENCE_SERVICE_ID)) {
				this.finalCheckBox.setEnabled(false, getFieldEditorParent());
			}

			// this.classHeader = new
			// MultiStringFieldEditor(getPreferenceLabel(P_CLASS_HEADER,
			// preferenceId), "Code Before Class (Header) :",
			// getFieldEditorParent());
			// addField(this.classHeader);

			this.classInsideBody = new MultiStringFieldEditor(getPreferenceLabel(P_CLASS_INSIDE_BODY, this.preferenceId),
					"Code In the Class (&Body) :", getFieldEditorParent());
			addField(this.classInsideBody);

			this.implSuperClass = new FastCodeListEditor(getPreferenceLabel(P_SUPER_CLASS, this.preferenceId),
					"Implementation &Super Class :", getFieldEditorParent(), IJavaElementSearchConstants.CONSIDER_CLASSES, null);
			addField(this.implSuperClass);
			// this.implSuperClass.setEnabled(getPreferenceStore().getBoolean(getPreferenceLabel(P_IMPLEMENT_INTERFACES,
			// preferenceId)), getFieldEditorParent());
			//this.implSuperClass.setChangeButtonText(BUTTON_TEXT_BROWSE);

			this.interfaces = new FastCodeListEditor(getPreferenceLabel(P_IMPLEMENT_INTERFACES, this.preferenceId),
					"Interfaces To &Implement:", getFieldEditorParent(), CONSIDER_INTERFACES, null);
			addField(this.interfaces);

			this.classImports = new FastCodeListEditor(getPreferenceLabel(P_CLASS_IMPORTS, this.preferenceId), "Clases To I&mport:",
					getFieldEditorParent(), CONSIDER_ALL_TYPES, null);
			addField(this.classImports);

			this.inclInstanceCheckBox = new BooleanFieldEditor(getPreferenceLabel(P_INCLUDE_INSTACE_FROM, this.preferenceId),
					"Include an in&stance of the from class :", getFieldEditorParent());
			addField(this.inclInstanceCheckBox);
			this.inclInstanceCheckBox.setEnabled(!isCreateNew(), getFieldEditorParent());

			this.createDefaultConstructor = new BooleanFieldEditor(getPreferenceLabel(P_CREATE_DEFAULT_CONSTRUCTOR, this.preferenceId),
					"Create Default Co&nstructor :", getFieldEditorParent());
			addField(this.createDefaultConstructor);

			this.createInstanceConstructor = new BooleanFieldEditor(getPreferenceLabel(P_CREATE_INSTANCE_CONSTRUCTOR, this.preferenceId),
					"Create Instance Co&nstructor :", getFieldEditorParent());
			addField(this.createInstanceConstructor);

			this.inclGetterSetterCheckBox = new BooleanFieldEditor(getPreferenceLabel(P_INCLUDE_GETTER_SETTER_INSTACE_FROM,
					this.preferenceId), "Generate &getter/setters for private fields :", getFieldEditorParent());
			addField(this.inclGetterSetterCheckBox);

			this.copyMethodCheckBox = new BooleanFieldEditor(getPreferenceLabel(P_COPY_METHODS, this.preferenceId),
					"Copy &methods from the from Class", getFieldEditorParent());
			addField(this.copyMethodCheckBox);
			this.copyMethodCheckBox.setEnabled(!isCreateNew(), getFieldEditorParent());

			this.createMethodBody = new BooleanFieldEditor(getPreferenceLabel(P_CREATE_METHOD_BODY, this.preferenceId),
					"Create &method Body", getFieldEditorParent());
			addField(this.createMethodBody);
			this.createMethodBody.setEnabled(!isCreateNew(), getFieldEditorParent());

			this.addionalFieldsCheckBox = new BooleanFieldEditor(getPreferenceLabel(P_CREATE_FIELDS, this.preferenceId),
					"Create &Additional Fields", getFieldEditorParent());
			addField(this.addionalFieldsCheckBox);

			this.addionalFieldsNameField = new StringFieldEditor(getPreferenceLabel(P_CREATE_FIELDS_NAME, this.preferenceId),
					"Name/Pattern for &Additional Fields", getFieldEditorParent());
			addField(this.addionalFieldsNameField);

			this.includePattern = new StringFieldEditor(getPreferenceLabel(P_INCLUDE_PATTERN, this.preferenceId),
					"Include Methods/Fields:", 20, getFieldEditorParent());
			addField(this.includePattern);
			this.includePattern.setEnabled(!isCreateNew(), getFieldEditorParent());

			this.excludePattern = new StringFieldEditor(getPreferenceLabel(P_EXCLUDE_PATTERN, this.preferenceId),
					"Exclude Methods/Fields:", 20, getFieldEditorParent());
			addField(this.excludePattern);
			this.excludePattern.setEnabled(!isCreateNew(), getFieldEditorParent());

			this.classAnnotations = new FastCodeListEditor(getPreferenceLabel(P_CLASS_ANNOTATIONS, this.preferenceId),
					"Class &Annotations:", getFieldEditorParent(), CONSIDER_ANNOTATION_TYPES, null);
			addField(this.classAnnotations);

			this.fieldAnnotations = new FastCodeListEditor(getPreferenceLabel(P_FIELD_ANNOTATIONS, this.preferenceId),
					"Field &Annotations:", getFieldEditorParent(), CONSIDER_ANNOTATION_TYPES, null);
			addField(this.fieldAnnotations);

			this.methodAnnotations = new FastCodeListEditor(getPreferenceLabel(P_METHOD_ANNOTATIONS, this.preferenceId),
					"Method &Annotations:", getFieldEditorParent(), CONSIDER_ANNOTATION_TYPES, null);
			addField(this.methodAnnotations);
			/*
						this.assignReturnCheckBox = new BooleanFieldEditor(getPreferenceLabel(P_RETURN_VARIABLE, this.preferenceId), "Assign &return type to a variable :",
								getFieldEditorParent());
						addField(this.assignReturnCheckBox);
						this.assignReturnCheckBox.setEnabled(!isCreateNew(),getFieldEditorParent());

						this.returnVariableName = new StringFieldEditor(getPreferenceLabel(P_RETURN_VARIABLE_NAME, this.preferenceId), "&Return variable name :", 20,
								getFieldEditorParent());
						this.returnVariableName.setEmptyStringAllowed(!this.assignReturnCheckBox.getBooleanValue());
						addField(this.returnVariableName);
						this.returnVariableName.setEnabled(!isCreateNew(),getFieldEditorParent());*/

			this.convertMethodParamCheckBox = new BooleanFieldEditor(getPreferenceLabel(P_CONVERT_METHOD_PARAM, this.preferenceId),
					"Convert Method &Parameter:", getFieldEditorParent());
			addField(this.convertMethodParamCheckBox);
			this.convertMethodParamCheckBox.setEnabled(!isCreateNew(), getFieldEditorParent());

			this.convertMethodParamFromField = new StringFieldEditor(getPreferenceLabel(P_CONVERT_METHOD_PARAM_FROM, this.preferenceId),
					"Convert Method &Parameter From :", 50, getFieldEditorParent());
			addField(this.convertMethodParamFromField);
			this.convertMethodParamFromField.setEnabled(!isCreateNew(), getFieldEditorParent());

			this.convertMethodParamToField = new StringFieldEditor(getPreferenceLabel(P_CONVERT_METHOD_PARAM_TO, this.preferenceId),
					"Convert Method &Parameter To :", 50, getFieldEditorParent());
			addField(this.convertMethodParamToField);
			this.convertMethodParamToField.setEnabled(!isCreateNew(), getFieldEditorParent());

			this.createUnitTestCheckBox = new BooleanFieldEditor(getPreferenceLabel(P_CREATE_UNIT_TEST, this.preferenceId),
					"Create &Unit Test of the Target Class:", getFieldEditorParent());
			addField(this.createUnitTestCheckBox);
			this.createUnitTestCheckBox.setEnabled(false, getFieldEditorParent());
		} else {
			// classHeader = new
			// MultiStringFieldEditor(getPreferenceLabel(P_CLASS_HEADER,
			// preferenceId), "Code Before Class (Header) :",
			// getFieldEditorParent());
			// addField(classHeader);
			this.finalCheckBox = new BooleanFieldEditor(getPreferenceLabel(P_FINAL_CLASS, this.preferenceId), "Make this Class final :",
					getFieldEditorParent());
			addField(this.finalCheckBox);

			this.classInsideBody = new MultiStringFieldEditor(getPreferenceLabel(P_CLASS_INSIDE_BODY, this.preferenceId),
					"Code In the Class (&Body) :", getFieldEditorParent());
			addField(this.classInsideBody);

			this.implSuperClass = new FastCodeListEditor(getPreferenceLabel(P_SUPER_CLASS, this.preferenceId),
					"Implementation &Super Class :", getFieldEditorParent(), IJavaElementSearchConstants.CONSIDER_CLASSES, null);
			addField(this.implSuperClass);

			this.interfaces = new FastCodeListEditor(getPreferenceLabel(P_IMPLEMENT_INTERFACES, this.preferenceId),
					"Interfaces To &Implement:", getFieldEditorParent(), CONSIDER_INTERFACES, null);
			addField(this.interfaces);

			this.classImports = new FastCodeListEditor(getPreferenceLabel(P_CLASS_IMPORTS, this.preferenceId), "Clases To I&mport:",
					getFieldEditorParent(), CONSIDER_ALL_TYPES, null);
			addField(this.classImports);

			this.createDefaultConstructor = new BooleanFieldEditor(getPreferenceLabel(P_CREATE_DEFAULT_CONSTRUCTOR, this.preferenceId),
					"Create Default &Constructor :", getFieldEditorParent());
			addField(this.createDefaultConstructor);

			this.createInstanceConstructor = new BooleanFieldEditor(getPreferenceLabel(P_CREATE_INSTANCE_CONSTRUCTOR, this.preferenceId),
					"Create Instance &Constructor :", getFieldEditorParent());
			addField(this.createInstanceConstructor);

			this.copyFieldsCheckBox = new BooleanFieldEditor(getPreferenceLabel(P_COPY_FIELDS, this.preferenceId),
					"Copy &Fields from the from Class :", getFieldEditorParent());
			addField(this.copyFieldsCheckBox);
			this.copyFieldsCheckBox.setEnabled(!isCreateNew(), getFieldEditorParent());

			this.breakDateFieldsCheckBox = new BooleanFieldEditor(getPreferenceLabel(P_BREAK_DATE_FIELDS, this.preferenceId),
					"Break &Date Fields :", getFieldEditorParent());
			addField(this.breakDateFieldsCheckBox);
			this.breakDateFieldsCheckBox.setEnabled(!isCreateNew(), getFieldEditorParent());

			this.classAnnotations = new FastCodeListEditor(getPreferenceLabel(P_CLASS_ANNOTATIONS, this.preferenceId),
					"Class &Annotations:", getFieldEditorParent(), CONSIDER_ANNOTATION_TYPES, null);
			addField(this.classAnnotations);

			this.fieldAnnotations = new FastCodeListEditor(getPreferenceLabel(P_FIELD_ANNOTATIONS, this.preferenceId),
					"Field &Annotations:", getFieldEditorParent(), CONSIDER_ANNOTATION_TYPES, null);
			addField(this.fieldAnnotations);

			this.includePattern = new StringFieldEditor(getPreferenceLabel(P_INCLUDE_PATTERN, this.preferenceId),
					"Include Methods/Fields:", getFieldEditorParent());
			addField(this.includePattern);

			this.excludePattern = new StringFieldEditor(getPreferenceLabel(P_EXCLUDE_PATTERN, this.preferenceId),
					"Exclude Methods/Fields:", getFieldEditorParent());
			addField(this.excludePattern);
		}

		this.createWorkingSetCheckBox = new BooleanFieldEditor(getPreferenceLabel(P_CREATE_WORKING_SET, this.preferenceId),
				"Automatically Create a Working Set :", 30, getFieldEditorParent());
		addField(this.createWorkingSetCheckBox);

		this.workingSetName = new StringFieldEditor(getPreferenceLabel(P_WORKING_SET_NAME, this.preferenceId), "Working Set Name :", 30,
				getFieldEditorParent());
		addField(this.workingSetName);

		final String[][] configTypes = new String[this.configPattern.getConfigs().length][2];
		for (int k = 0; k < configTypes.length; k++) {
			configTypes[k][0] = configTypes[k][1] = this.configPattern.getConfigs()[k].getConfigType();
		}

		final String[][] configFileConvTypes = new String[4][2];
		configFileConvTypes[0][0] = configFileConvTypes[0][1] = CONVERSION_NONE;
		configFileConvTypes[1][0] = configFileConvTypes[1][1] = CONVERSION_LOWER_CASE;
		configFileConvTypes[2][0] = configFileConvTypes[2][1] = CONVERSION_CAMEL_CASE;
		configFileConvTypes[3][0] = configFileConvTypes[3][1] = CONVERSION_CAMEL_CASE_HYPHEN;

		// Create the configuration file items.

		for (int configCount = 0; configCount < this.numMaxConfigs; configCount++) {
			final String configType = this.configPattern.getConfigs()[configCount].getConfigType();
			if (!(isStringInArray(configType, configItemArr) || isStringInArray(configType,
					this.configPattern.getConfigTypes().get(this.preferenceId)))) {
				continue;
			}

			new Separator(SWT.SEPARATOR | SWT.HORIZONTAL).doFillIntoGrid(getFieldEditorParent(), 100, convertHeightInCharsToPixels(2));

			this.createConfigCheckBox[configCount] = new BooleanFieldEditor(getPreferenceLabel(P_CREATE_CONFIG, this.preferenceId)
					+ configCount, "Create configuration file :", getFieldEditorParent());

			addField(this.createConfigCheckBox[configCount]);

			final boolean enableConfigParts = getPreferenceStore().getBoolean(
					getPreferenceLabel(P_CREATE_CONFIG, this.preferenceId) + configCount);

			FieldEditor fieldEditor;

			fieldEditor = createRadioGroupFieldEditor(getPreferenceLabel(P_CONFIG_TYPE, this.preferenceId) + configCount,
					"Configuration Type", this.configPattern.getConfigs().length, configTypes, getFieldEditorParent(), true);
			this.createSimilarConfigurationPart.setConfigTypeRadio(configCount, (RadioGroupFieldEditor) fieldEditor);
			addField(fieldEditor);
			fieldEditor.setEnabled(false, getFieldEditorParent());

			fieldEditor = createStringFieldEditor(getPreferenceLabel(P_CONFIG_LOCATION, this.preferenceId) + configCount,
					"Configuration Location :", getFieldEditorParent());
			this.createSimilarConfigurationPart.setConfigLocation(configCount, (StringFieldEditor) fieldEditor);
			addField(fieldEditor);

			fieldEditor.setEnabled(enableConfigParts, getFieldEditorParent());
			// ((StringFieldEditor)fieldEditor).setEmptyStringAllowed(enableConfigParts
			// ? false : true);

			fieldEditor = createStringFieldEditor(getPreferenceLabel(P_CONFIG_FILE_NAME, this.preferenceId) + configCount,
					"Configuration File Name :", getFieldEditorParent());
			this.createSimilarConfigurationPart.setConfigFile(configCount, (StringFieldEditor) fieldEditor);

			// configFile[configCount] = new
			// StringFieldEditor(getPreferenceLabel(P_CONFIG_FILE_NAME,
			// preferenceId)+configCount, "Configuration File Name :",
			// getFieldEditorParent());
			addField(fieldEditor);
			fieldEditor.setEnabled(enableConfigParts, getFieldEditorParent());
			((StringFieldEditor) fieldEditor).setEmptyStringAllowed(enableConfigParts ? false : true);

			fieldEditor = new RadioGroupFieldEditor(getPreferenceLabel(P_CONFIG_FILE_CONV_TYPES, this.preferenceId) + configCount,
					"Convert the File Name :", configFileConvTypes.length, configFileConvTypes, getFieldEditorParent(), true);
			this.createSimilarConfigurationPart.setConfigFileNameConversion(configCount, (RadioGroupFieldEditor) fieldEditor);
			addField(fieldEditor);
			fieldEditor.setEnabled(enableConfigParts, getFieldEditorParent());

			if (!isEmpty(this.configPattern.getConfigs()[configCount].getConfigLocale())) {
				fieldEditor = new StringFieldEditor(getPreferenceLabel(P_CONFIG_LOCALE, this.preferenceId) + configCount,
						"Configuration Locale :", getFieldEditorParent());
				this.createSimilarConfigurationPart.setConfigLocale(configCount, (StringFieldEditor) fieldEditor);
				addField(fieldEditor);
				fieldEditor.setEnabled(enableConfigParts, getFieldEditorParent());
			}

			fieldEditor = createMultiStringFieldEditor(getPreferenceLabel(P_CONFIG_HEADER_PATTERN, this.preferenceId) + configCount,
					"Configuration Header :", getFieldEditorParent());
			this.createSimilarConfigurationPart.setConfigHeaderPattern(configCount, (MultiStringFieldEditor) fieldEditor);
			addField(fieldEditor);
			fieldEditor.setEnabled(enableConfigParts, getFieldEditorParent());

			fieldEditor = createMultiStringFieldEditor(getPreferenceLabel(P_CONFIG_START_PATTERN, this.preferenceId) + configCount,
					"Configuration Start :", getFieldEditorParent());
			this.createSimilarConfigurationPart.setConfigStartPattern(configCount, (MultiStringFieldEditor) fieldEditor);
			addField(fieldEditor);
			fieldEditor.setEnabled(enableConfigParts, getFieldEditorParent());

			fieldEditor = createStringFieldEditor(getPreferenceLabel(P_CONFIG_END_PATTERN, this.preferenceId) + configCount,
					"Configuration End :", getFieldEditorParent());
			this.createSimilarConfigurationPart.setConfigEndPattern(configCount, (StringFieldEditor) fieldEditor);
			addField(fieldEditor);
			fieldEditor.setEnabled(enableConfigParts, getFieldEditorParent());

			fieldEditor = createMultiStringFieldEditor(getPreferenceLabel(P_CONFIG_PATTERN, this.preferenceId) + configCount,
					"Configuration Pattern :", getFieldEditorParent());
			this.createSimilarConfigurationPart.setConfigBodyPattern(configCount, (MultiStringFieldEditor) fieldEditor);
			addField(fieldEditor);
			fieldEditor.setEnabled(enableConfigParts, getFieldEditorParent());
			((MultiStringFieldEditor) fieldEditor).setEmptyStringAllowed(enableConfigParts ? false : true);
		}

		this.removeConfigCheckBox = new BooleanFieldEditor(getPreferenceLabel(P_REMOVE_CONFIG, this.preferenceId),
				"Remove unused configurations :", BooleanFieldEditor.SEPARATE_LABEL, getFieldEditorParent());
		if (this.numConfigs > 1) {
			// removeConfig should be always off.
			addField(this.removeConfigCheckBox);
			getPreferenceStore().setValue(getPreferenceLabel(P_REMOVE_CONFIG, this.preferenceId), false);
			this.removeConfigCheckBox.load();
		}
		final Button restoreConfigButton = new Button(getFieldEditorParent(), SWT.PUSH);
		restoreConfigButton.setText("Restore configurations");
		if (this.numConfigs == this.numMaxConfigs) {
			restoreConfigButton.setEnabled(false);
		}

		restoreConfigButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				final MessageDialogWithToggle dialogWithToggle = MessageDialogWithToggle.openYesNoQuestion(
						CreateSimilarPreferencePage.this.getShell(), "Restore configurations",
						"Restore configurations, this will close the preference dialog, You have reopen it.", "Remember Decision", false,
						CreateSimilarPreferencePage.this.getPreferenceStore(),
						getPreferenceLabel(P_RESTORE_CONFIG_ITEMS, CreateSimilarPreferencePage.this.preferenceId));
				if (dialogWithToggle.getReturnCode() != MESSAGE_DIALOG_RETURN_YES) {
					return;
				}
				CreateSimilarPreferencePage.this.getPreferenceStore().setValue(
						getPreferenceLabel(P_CONFIG_ITEMS, CreateSimilarPreferencePage.this.preferenceId), "");
				CreateSimilarPreferencePage.this.getShell().close();
				// getShell().open();
			}
		});

	}

	@Override
	public boolean isValid() {
		boolean valid = super.isValid();
		if (!valid) {
			valid = this.fromPattern.isValid();
		}
		return valid;
	}

	@Override
	public boolean performOk() {
		if (!isCreateNew() && this.fromPattern.getStringValue().equals(EMPTY_STR)) {
			return false;
		}

		if (this.removeConfigCheckBox.getBooleanValue()) {
			int j = 0;
			final StringBuilder configItems = new StringBuilder();
			this.configPattern.getConfigs();
			for (j = 0; j < this.numMaxConfigs; j++) {
				final boolean createConfig = this.createConfigCheckBox[j].getBooleanValue();
				if (createConfig) {
					configItems.append(isEmpty(configItems.toString()) ? EMPTY_STR : COLON);
					configItems.append(this.configPattern.getConfigs()[j].getConfigType());
				}
			}
			getPreferenceStore().setValue(getPreferenceLabel(P_CONFIG_ITEMS, this.preferenceId), configItems.toString());
		}

		// if (!isEmpty(classBodyPattern.getStringValue()) &&
		// createImplCheckBox.getBooleanValue()) {
		// showWarning("Class body pattern is not empty and createImpl CheckBox on",
		// "Prefrence Warning");
		// }

		if (super.performOk()) {
			return refreshePreferenceDataFromStore();
		}
		return false;
	}

	/**
	 * @param event
	 *
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {

		final Object source = event.getSource();
		final Object newValue = event.getNewValue();

		if (source == this.createImplCheckBox) {
			final Boolean value = (Boolean) newValue;
			this.implSubPkg.setEnabled(value, getFieldEditorParent());
			this.finalCheckBox.setEnabled(value, getFieldEditorParent());
			this.implSuperClass.setEnabled(value, getFieldEditorParent());
			this.interfaces.setEnabled(value, getFieldEditorParent());
			this.classInsideBody.setEnabled(value, getFieldEditorParent());
			this.classAnnotations.setEnabled(value, getFieldEditorParent());
			this.classImports.setEnabled(value, getFieldEditorParent());
			this.createDefaultConstructor.setEnabled(value, getFieldEditorParent());
			this.createInstanceConstructor.setEnabled(value, getFieldEditorParent());
			this.addionalFieldsCheckBox.setEnabled(value, getFieldEditorParent());
			this.addionalFieldsNameField.setEnabled(value, getFieldEditorParent());
			this.inclGetterSetterCheckBox.setEnabled(value, getFieldEditorParent());
			enableMethodFields(value);
			if (!isCreateNew()) {
				this.inclInstanceCheckBox.setEnabled(value, getFieldEditorParent());
			}
			// assignReturnCheckBox.setEnabled(((Boolean)newValue).booleanValue(),
			// getFieldEditorParent());
		} else if (source == this.copyMethodCheckBox) {
			enableMethodFields((Boolean) newValue);
		} else if (source == this.copyFieldsCheckBox) {
			final Boolean value = (Boolean) newValue;
			this.includePattern.setEnabled(((Boolean) newValue).booleanValue(), getFieldEditorParent());
			this.excludePattern.setEnabled(((Boolean) newValue).booleanValue(), getFieldEditorParent());
			this.breakDateFieldsCheckBox.setEnabled(value, getFieldEditorParent());

		} /*else if (source == this.assignReturnCheckBox) {
			this.returnVariableName.setEnabled((Boolean) newValue, getFieldEditorParent());
			this.returnVariableName.setEmptyStringAllowed(!(Boolean) newValue);
			} */else if (source == this.projectComboList) {
			this.project = (String) newValue;
			if (this.sourceComboList != null) {
				showWarning("Please make sure source combo contains correct path for project " + this.project, "Warning");
			}
			if (isCreateNew()) {
				this.packageFieldEditor.setEnabled(!isEmpty(CreateSimilarPreferencePage.this.project), getFieldEditorParent());
			}
		} else if (source == this.sourceComboList) {
			this.sourcePath = (String) newValue;
			if (!isEmpty(this.project) && isEmpty(this.sourcePath)) {
				showError("Source path cannot be blank if project is not blank.", "Error");
				setErrorMessage("Source path cannot be blank if project is not blank.");
			}
		} else if (source == this.convertMethodParamCheckBox && !isCreateNew()) {
			this.convertMethodParamFromField.setEnabled(((Boolean) newValue).booleanValue(), getFieldEditorParent());
			this.convertMethodParamFromField.setEmptyStringAllowed(false);
			this.convertMethodParamToField.setEnabled(((Boolean) newValue).booleanValue(), getFieldEditorParent());
			this.convertMethodParamToField.setEmptyStringAllowed(false);
		} else if (source == this.addionalFieldsCheckBox) {
			this.addionalFieldsNameField.setEnabled((Boolean) newValue, getFieldEditorParent());
		} else {
			for (int i = 0; i < this.numMaxConfigs; i++) {
				if (source == this.createConfigCheckBox[i]) {
					getPreferenceStore().setValue(getPreferenceLabel(P_CREATE_CONFIG, this.preferenceId) + i,
							this.createConfigCheckBox[i].getBooleanValue());
					this.createSimilarConfigurationPart.enableConfigParts(i, (Boolean) newValue, getFieldEditorParent());
					break;
				}
			}
		}
	}

	/**
	 *
	 * @return
	 */
	protected boolean isCreateNew() {
		return false;
	}

	/**
	 * @param value
	 */
	private void enableMethodFields(final Boolean value) {
		/*this.assignReturnCheckBox.setEnabled(value, getFieldEditorParent());
		this.returnVariableName.setEnabled(value, getFieldEditorParent());*/
		this.methodAnnotations.setEnabled(value, getFieldEditorParent());
		this.createMethodBody.setEnabled(value, getFieldEditorParent());
		this.convertMethodParamCheckBox.setEnabled(value, getFieldEditorParent());
		this.convertMethodParamFromField.setEnabled(value, getFieldEditorParent());
		this.convertMethodParamToField.setEnabled(value, getFieldEditorParent());
		this.includePattern.setEnabled(value, getFieldEditorParent());
		this.excludePattern.setEnabled(value, getFieldEditorParent());
	}

	protected abstract boolean isForValueBeans();

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

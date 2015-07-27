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

import static org.eclipse.jdt.core.search.SearchEngine.createWorkspaceScope;
import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_ALL_TYPES;
import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_ANNOTATION_TYPES;
import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_CLASSES;
import static org.eclipse.jdt.ui.JavaUI.createTypeDialog;
import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.eclipse.jface.dialogs.MessageDialog.openInformation;
import static org.eclipse.jface.dialogs.MessageDialog.openQuestion;
import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.BUTTON_TEXT_BROWSE;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.DEFAULT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.common.FastCodeConstants.UNIT_TEST_FOLDER;
import static org.fastcode.preferences.JunitPreferences.getTestProfiles;
import static org.fastcode.preferences.JunitPreferences.reload;
import static org.fastcode.preferences.PreferenceConstants.P_BASE_TEST;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_ALL_TEST_PROFILES;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_ALWAYS_CREATE_INSTANCE;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_CLASS_ANNOTATIONS;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_CLASS_IMPORTS;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_CLASS_INSIDE_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_CREATE_METHOD_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_CREATE_NEW_PROF;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_DELETE_CURR_PROF;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_EXCEPTION_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_FIELD_ANNOTATIONS;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_METHOD_ANNOTATIONS;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_METHOD_COMMENT;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_NEGATIVE_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_PROFILE_NAME;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_PROFILE_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TEST_CLASS;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TEST_LOCATION;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TEST_METHOD;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TEST_PROFILE;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TEST_PROJECT;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TYPE;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TYPE_3;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TYPE_4;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TYPE_CUSTOM;
import static org.fastcode.preferences.PreferenceConstants.P_JUNIT_TYPE_TESTNG;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.JUnitUtil.isJunitTest;
import static org.fastcode.util.SourceUtil.getAllProjects;
import static org.fastcode.util.SourceUtil.getSourcePathsForProject;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.XMLUtil.exportXML;
import static org.fastcode.util.XMLUtil.importXML;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.util.MultiStringFieldEditor;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>,
 * we can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class JUnitPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final String		PROFILE_DEFAULT		= DEFAULT;
	private static final String		PROFILE_DELIMITER	= COLON;

	private StringFieldEditor		junitTestClass;
	private StringFieldEditor		junitTestMethod;
	protected StringFieldEditor		classInsideBody;
	private StringFieldEditor		exceptionBody;
	private StringFieldEditor		negativeBody;
	//	private StringFieldEditor junitTestFormat;
	//	private BooleanFieldEditor		junitAlwaysCreateTryCatch;
	private BooleanFieldEditor		junitCreateMethodBody;

	private StringButtonFieldEditor	junitSuperClass;
	private ComboFieldEditor		junitLocation;
	private ComboFieldEditor		junitProfiles;
	private StringFieldEditor		junitProfileName;
	private StringFieldEditor		junitProfilePattern;
	private ListEditor				classAnnotations;
	private FieldEditor				methodAnnotations;
	private ListEditor				fieldAnnotations;
	private FieldEditor				methodComment;
	private BooleanFieldEditor		createInstance;

	//private BooleanFieldEditor		showAllCheckBox;
	private BooleanFieldEditor		createNewProfCheckBox;
	private BooleanFieldEditor		deleteCurrentProfCheckBox;

	private RadioGroupFieldEditor	junitTypeRadio;
	private boolean					override			= false;
	private String					junitType;						// Temp place to store junit type.
	private String					junitTestProfile;				// Temp place to store Test Profile.
	private final String			junitTestProfilePattern;		// Temp place to store Test Profile Pattern.
	private final String[][]		allTestProfiles;				// Temp place to store all Test Profiles.

	private final IPreferenceStore	preferenceStore;
	private ListEditor				classImports;
	private ComboFieldEditor		projectComboList;
	private Button					importButton;
	private Button					exportButton;

	/**
	 *
	 */
	public JUnitPreferencePage() {
		super(GRID);
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		//		preferenceStore = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(this.preferenceStore);
		this.preferenceStore.setValue(P_JUNIT_CREATE_NEW_PROF, false);
		this.preferenceStore.setValue(P_JUNIT_DELETE_CURR_PROF, false);
		this.junitTestProfile = getPreferenceStore().contains(P_JUNIT_TEST_PROFILE) ? getPreferenceStore().getString(P_JUNIT_TEST_PROFILE)
				: PROFILE_DEFAULT;
		this.junitTestProfilePattern = getPreferenceStore().contains(getPreferenceName(P_JUNIT_PROFILE_PATTERN)) ? getPreferenceStore()
				.getString(getPreferenceName(P_JUNIT_PROFILE_PATTERN)) : ASTERISK;

		this.junitType = this.preferenceStore.contains(getPreferenceName(P_JUNIT_TYPE)) ? this.preferenceStore
				.getString(getPreferenceName(P_JUNIT_TYPE)) : P_JUNIT_TYPE_4;

		this.allTestProfiles = getTestProfiles();
		setDescription("Preference page for Junit test creator");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		// getFieldEditorParent().setFont(JFaceResources.getFont(PreferenceConstants.EDITOR_TEXT_FONT));
		// applyFont();

		this.junitProfiles = new ComboFieldEditor(P_JUNIT_TEST_PROFILE, "JUnit Test Profiles:", this.allTestProfiles,
				getFieldEditorParent());
		addField(this.junitProfiles);

		this.createNewProfCheckBox = new BooleanFieldEditor(P_JUNIT_CREATE_NEW_PROF, "Create A New Profile :", getFieldEditorParent());
		addField(this.createNewProfCheckBox);

		this.deleteCurrentProfCheckBox = new BooleanFieldEditor(P_JUNIT_DELETE_CURR_PROF, "Delete Current Profile :",
				getFieldEditorParent());
		addField(this.deleteCurrentProfCheckBox);
		if (this.allTestProfiles.length == 1) {
			this.deleteCurrentProfCheckBox.setEnabled(false, getFieldEditorParent());
		}

		this.junitProfileName = new StringFieldEditor(getPreferenceName(P_JUNIT_PROFILE_NAME), "Junit Test Profile Name :", 30,
				getFieldEditorParent());
		addField(this.junitProfileName);
		this.junitProfileName.setEnabled(false, getFieldEditorParent());
		//this.junitProfileName.setEmptyStringAllowed(false);

		this.junitProfilePattern = new StringFieldEditor(getPreferenceName(P_JUNIT_PROFILE_PATTERN), "Class Name Pattern :", 30,
				getFieldEditorParent());
		addField(this.junitProfilePattern);
		this.junitProfilePattern.setEnabled(false, getFieldEditorParent());
		//this.junitProfilePattern.setEmptyStringAllowed(false);

		final GlobalSettings globalSettings = getInstance();

		final String[][] junitTypes = { { "Junit 4", P_JUNIT_TYPE_4 }, { "Junit 3", P_JUNIT_TYPE_3 }, { "TestNG", P_JUNIT_TYPE_TESTNG },
				{ "Custom", P_JUNIT_TYPE_CUSTOM } };
		this.junitTypeRadio = new RadioGroupFieldEditor(getPreferenceName(P_JUNIT_TYPE), "Junit Type", 4, junitTypes,
				getFieldEditorParent(), true);
		addField(this.junitTypeRadio);

		this.junitTestClass = new StringFieldEditor(getPreferenceName(P_JUNIT_TEST_CLASS), "Junit Test Class Name :", 30,
				getFieldEditorParent());
		addField(this.junitTestClass);
		this.junitTestClass.setEmptyStringAllowed(false);

		this.junitTestMethod = new StringFieldEditor(getPreferenceName(P_JUNIT_TEST_METHOD), "Junit Test Method Name :", 30,
				getFieldEditorParent());
		addField(this.junitTestMethod);
		this.junitTestMethod.setEmptyStringAllowed(false);

		this.junitSuperClass = new StringButtonFieldEditor(getPreferenceName(P_BASE_TEST), "Base Test Class :", getFieldEditorParent()) {

			@Override
			protected String changePressed() {
				setTextLimit(200);
				try {
					final SelectionDialog selectionDialog = createTypeDialog(getShell(), null, createWorkspaceScope(), CONSIDER_CLASSES,
							false, "*TestCase");
					if (selectionDialog.open() == SWT.CANCEL) {
						return EMPTY_STR;
					}
					final IType type = (IType) selectionDialog.getResult()[0];
					JUnitPreferencePage.this.override = true;
					if (!isJunitTest(type)) {
						openError(getShell(), "Error", "This is not valid base class for Junit Tests. Please try again.");
						return EMPTY_STR;
					}
					return type.getFullyQualifiedName();
				} catch (final JavaModelException e) {
					e.printStackTrace();
				}
				return EMPTY_STR;
			}
		};
		//this.junitSuperClass.setEmptyStringAllowed(false);
		this.junitSuperClass.setChangeButtonText(BUTTON_TEXT_BROWSE);
		addField(this.junitSuperClass);
		if (this.junitType.equals(P_JUNIT_TYPE_4)) {
			this.junitSuperClass.setEnabled(false, getFieldEditorParent());
		}

		//		this.showAllCheckBox = new BooleanFieldEditor(getPreferenceName(P_JUNIT_SHOW_ALL_PATHS), "Show All Paths", getFieldEditorParent());
		//		addField(this.showAllCheckBox);

		//		boolean showAll = preferenceStore.getBoolean(P_JUNIT_SHOW_ALL_PATHS);
		//		if (showAll) {
		//			entryNamesAndValues = getAllSourcePaths(null);
		//		}

		final String[][] projects = getAllProjects();
		this.projectComboList = new ComboFieldEditor(getPreferenceName(P_JUNIT_TEST_PROJECT), "Project:", projects, getFieldEditorParent());
		addField(this.projectComboList);

		final String[][] entryNamesAndValues = getSourcePathsForProject(this.preferenceStore
				.getString(getPreferenceName(P_JUNIT_TEST_PROJECT)));
		this.junitLocation = new ComboFieldEditor(getPreferenceName(P_JUNIT_TEST_LOCATION), "Test Location:", entryNamesAndValues,
				getFieldEditorParent());
		addField(this.junitLocation);
		this.junitLocation.setEnabled(!globalSettings.isUseDefaultForPath(), getFieldEditorParent());

		this.createInstance = new BooleanFieldEditor(getPreferenceName(P_JUNIT_ALWAYS_CREATE_INSTANCE),
				"Always Create an Instance at Class Level", getFieldEditorParent());
		addField(this.createInstance);

		this.fieldAnnotations = new FastCodeListEditor(getPreferenceName(P_JUNIT_FIELD_ANNOTATIONS), "Field Annotations:",
				getFieldEditorParent(), CONSIDER_ANNOTATION_TYPES, null);
		addField(this.fieldAnnotations);

		//		this.junitTestFormat = new MultiStringFieldEditor(getPreferenceName(P_JUNIT_TEST_FORMAT), "Test Test Format:", getFieldEditorParent());
		//		addField(this.junitTestFormat);
		//((StringFieldEditor)junitTestFormat).setEmptyStringAllowed(false);

		this.classImports = new FastCodeListEditor(getPreferenceName(P_JUNIT_CLASS_IMPORTS), "Clases To Import:", getFieldEditorParent(),
				CONSIDER_ALL_TYPES, null);
		addField(this.classImports);

		this.classInsideBody = new MultiStringFieldEditor(getPreferenceName(P_JUNIT_CLASS_INSIDE_BODY), "Code Inside Class :",
				getFieldEditorParent());
		addField(this.classInsideBody);

		this.classAnnotations = new FastCodeListEditor(getPreferenceName(P_JUNIT_CLASS_ANNOTATIONS), "Class Annotations:",
				getFieldEditorParent(), CONSIDER_ANNOTATION_TYPES, null);
		addField(this.classAnnotations);

		this.methodAnnotations = new FastCodeListEditor(getPreferenceName(P_JUNIT_METHOD_ANNOTATIONS), "Test Method Annotations:",
				getFieldEditorParent(), CONSIDER_ANNOTATION_TYPES, null);
		addField(this.methodAnnotations);

		this.methodComment = new StringFieldEditor(getPreferenceName(P_JUNIT_METHOD_COMMENT), "Test Method Comment:", 60,
				getFieldEditorParent());
		addField(this.methodComment);

		this.junitCreateMethodBody = new BooleanFieldEditor(getPreferenceName(P_JUNIT_CREATE_METHOD_BODY), "Create Method Body",
				getFieldEditorParent());
		addField(this.junitCreateMethodBody);

		/*this.junitAlwaysCreateTryCatch = new BooleanFieldEditor(getPreferenceName(P_JUNIT_ALWAYS_CREATE_TRY_CATCH), "Always Create Try Catch Block",
				getFieldEditorParent());
		addField(this.junitAlwaysCreateTryCatch);*/

		this.exceptionBody = new MultiStringFieldEditor(getPreferenceName(P_JUNIT_EXCEPTION_BODY),
				"Body in the Catch Block (for happy tests)", getFieldEditorParent());
		addField(this.exceptionBody);

		this.negativeBody = new MultiStringFieldEditor(getPreferenceName(P_JUNIT_NEGATIVE_BODY), "Test body for negative tests :",
				getFieldEditorParent());
		addField(this.negativeBody);
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
					//processXML("UnitTestPreferences.xml", EMPTY_STR, UNIT_TEST_FOLDER);
					importXML("UnitTestPreferences.xml", EMPTY_STR, UNIT_TEST_FOLDER);
				} catch (final Exception ex) {
					try {
						throw new Exception("There was some error in Import templates : " + ex.getMessage());
					} catch (final Exception ex1) {
						MessageDialog.openError(null, "Import Failed", ex1.getMessage() + "....Please retry after making the changes");
						ex1.printStackTrace();
					}
					ex.printStackTrace();
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
					//processXML("UnitTestPreferences.xml", EMPTY_STR, UNIT_TEST_FOLDER);
					exportXML("UnitTestPreferences.xml", EMPTY_STR, UNIT_TEST_FOLDER);

				} catch (final Exception ex) {
					try {
						throw new Exception("There was some error in Export templates : " + ex.getMessage());
					} catch (final Exception ex1) {
						MessageDialog.openError(null, "Export Failed", ex1.getMessage() + "....Please retry after making the changes");
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

	/**
	 *
	 */
	@Override
	public boolean performOk() {

		if (!isEmpty(getErrorMessage())) {
			openError(getShell(), "Error", "There are errors present, cannot save.");
			return false;
		}

		if (this.deleteCurrentProfCheckBox.getBooleanValue()) {
			if (!openQuestion(getShell(), "Warning", "Do you want delete the current profile?")) {
				return false;
			}
			final String allTestProfiles = this.preferenceStore.getString(P_JUNIT_ALL_TEST_PROFILES);
			String[] profilesArr = null;
			profilesArr = allTestProfiles.split(PROFILE_DELIMITER);
			String nextProfile = null;
			StringBuilder newAllTestProfiles = new StringBuilder();
			int count = 0;
			for (final String profile : profilesArr) {
				if (profile.equals(this.junitTestProfile)) {
					nextProfile = count < profilesArr.length - 1 ? profilesArr[count + 1] : profilesArr[0];
				} else {
					newAllTestProfiles.append(profile + (count < profilesArr.length - 1 ? PROFILE_DELIMITER : EMPTY_STR));
				}
				count++;
			}
			if (newAllTestProfiles.toString().endsWith(PROFILE_DELIMITER)) {
				newAllTestProfiles = new StringBuilder(newAllTestProfiles.substring(0, newAllTestProfiles.length() - 1));
			}
			this.preferenceStore.setValue(P_JUNIT_TEST_PROFILE, nextProfile);
			this.preferenceStore.setValue(P_JUNIT_ALL_TEST_PROFILES, newAllTestProfiles.toString());
			return true;
		}

		if (this.createNewProfCheckBox.getBooleanValue()) {
			this.junitTestProfile = this.junitProfileName.getStringValue();
			final String profPattrn = this.junitProfilePattern.getStringValue();
			if (!isProfileValid(profPattrn)) {
				openError(getShell(), "Error", "Invalid profile pattern for non default profile, you need to be more specific.");
				return false;
			}
			switchProfile();
		}

		if (P_JUNIT_TYPE_3.equals(this.junitType)) {
			if (isEmpty(this.junitSuperClass.getStringValue())) {
				openError(getShell(), "Error", "Need to provide a super class for the Test.");
			}
			final String negativeBody = this.negativeBody.getStringValue();
			if (isEmpty(negativeBody)) {
				if (openQuestion(getShell(), "Warning", "Do you want populate Negative Test Body? It is nice to have for Junit 3 Tests.")) {
					this.negativeBody.setStringValue(this.preferenceStore.getDefaultString(P_JUNIT_NEGATIVE_BODY));
				}
			}
		}

		final boolean status = super.performOk();

		if (!status) {
			return status;
		}

		if (!this.preferenceStore.contains(P_JUNIT_ALL_TEST_PROFILES)) {
			this.preferenceStore.setValue(P_JUNIT_ALL_TEST_PROFILES, PROFILE_DEFAULT);
		}

		if (this.createNewProfCheckBox.getBooleanValue()) {
			if (!this.preferenceStore.contains(P_JUNIT_ALL_TEST_PROFILES)) {
				setErrorMessage("Unable to save new profile.");
				return false;
			}
			final String newProfile = this.junitProfileName.getStringValue();
			if (!isProfileValid(newProfile)) {
				return false;
			}

			final String profilePattern = this.junitProfilePattern.getStringValue();
			if (!isPatternValid(profilePattern)) {
				return false;
			}

			this.preferenceStore.setValue(P_JUNIT_TEST_PROFILE, this.junitProfileName.getStringValue());
			final String newTestProfile = this.junitProfileName.getStringValue();

			String allTestProfiles = this.preferenceStore.getString(P_JUNIT_ALL_TEST_PROFILES);
			if (profilePattern.equals(ASTERISK)) {
				allTestProfiles = allTestProfiles + PROFILE_DELIMITER + newTestProfile;
			} else {
				allTestProfiles = newTestProfile + PROFILE_DELIMITER + allTestProfiles;
			}
			this.preferenceStore.setValue(P_JUNIT_ALL_TEST_PROFILES, allTestProfiles);
		}

		reload(this.junitProfileName.getStringValue());
		return status;
	}

	/**
	 *
	 */
	private void switchProfile() {
		this.junitProfileName.setPreferenceName(getPreferenceName(P_JUNIT_PROFILE_NAME));
		this.junitProfilePattern.setPreferenceName(getPreferenceName(P_JUNIT_PROFILE_PATTERN));
		this.projectComboList.setPreferenceName(getPreferenceName(P_JUNIT_TEST_PROJECT));
		if (this.junitLocation != null) {
			this.junitLocation.setPreferenceName(getPreferenceName(P_JUNIT_TEST_LOCATION));
		}
		this.junitTestClass.setPreferenceName(getPreferenceName(P_JUNIT_TEST_CLASS));
		this.junitTestMethod.setPreferenceName(getPreferenceName(P_JUNIT_TEST_METHOD));
		this.junitSuperClass.setPreferenceName(getPreferenceName(P_BASE_TEST));
		this.createInstance.setPreferenceName(getPreferenceName(P_JUNIT_ALWAYS_CREATE_INSTANCE));
		this.fieldAnnotations.setPreferenceName(getPreferenceName(P_JUNIT_FIELD_ANNOTATIONS));
		//		this.junitTestFormat.setPreferenceName(getPreferenceName(P_JUNIT_TEST_FORMAT));
		this.classInsideBody.setPreferenceName(getPreferenceName(P_JUNIT_CLASS_INSIDE_BODY));
		//		this.showAllCheckBox.setPreferenceName(getPreferenceName(P_JUNIT_SHOW_ALL_PATHS));
		this.classAnnotations.setPreferenceName(getPreferenceName(P_JUNIT_CLASS_ANNOTATIONS));
		this.methodAnnotations.setPreferenceName(getPreferenceName(P_JUNIT_METHOD_ANNOTATIONS));
		this.methodComment.setPreferenceName(getPreferenceName(P_JUNIT_METHOD_COMMENT));
		this.junitCreateMethodBody.setPreferenceName(getPreferenceName(P_JUNIT_CREATE_METHOD_BODY));
		//this.junitAlwaysCreateTryCatch.setPreferenceName(getPreferenceName(P_JUNIT_ALWAYS_CREATE_TRY_CATCH));
		this.exceptionBody.setPreferenceName(getPreferenceName(P_JUNIT_EXCEPTION_BODY));
		this.negativeBody.setPreferenceName(getPreferenceName(P_JUNIT_NEGATIVE_BODY));
	}

	/**
	 * @param event
	 *
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		super.propertyChange(event);
		final Object source = event.getSource();

		if (source == this.junitProfiles) {
			if (event.getNewValue().equals(this.junitTestProfile)) {
				return;
			}
			if (!openQuestion(getShell(), "Warning", "This will close the dialog, you have to reopen it. "
					+ "If you have changed any values, it won't be saved. Do you want continue?")) {
				this.junitProfiles.load();
				return;
			}
			getPreferenceStore().setValue(P_JUNIT_TEST_PROFILE, (String) event.getNewValue());
			getShell().close();
		}/* else if (source == this.showAllCheckBox) {
			super.performOk();
			getShell().close();
			final String[] displayIds = new String[] { "FastCode.preferences.FastCodePreferencePage",
					"FastCode.preferences.CreateSimilarPreferenceDaoService", "FastCode.preferences.CreateSimilarPreferencePojoDaoService",
					"FastCode.preferences.CreateSimilarPreferencePojoDaoService",
					"FastCode.preferences.CreateSimilarPreferencePojoServiceUI", "FastCode.preferences.JUnitPreferencePage" };
			final PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(new Shell(),
					"FastCode.preferences.FastCodePreferencePage", displayIds, this.preferenceStore);
			dialog.create();
			dialog.setMessage("Fast Code JUnit Preference");
			dialog.open();
			}*/else if (source == this.junitSuperClass) {
			//			if (!override) {
			//openWarning(getShell(), "Warning", "You should not change this field manually.");
			//return;
			// Next line does not work. So leaving a warning to the user.
			//this.junitSuperClass.setStringValue((String)event.getOldValue());
			//			}
			this.override = false;
		} else if (source == this.junitTypeRadio) {
			final String negativeBody = this.negativeBody.getStringValue();
			if (!event.getNewValue().equals(event.getOldValue())) {
				if (event.getNewValue().equals(P_JUNIT_TYPE_4) || event.getNewValue().equals(P_JUNIT_TYPE_TESTNG)) {
					this.junitTestMethod.setStringValue("${method_name}");
					openInformation(getShell(), "Annotations", "This will automatically add the @Test annotations to test methods.");
					if (!isEmpty(negativeBody)) {
						if (openQuestion(getShell(), "Warning",
								"Do you want reset Negative Test Body? It is not required for Junit 4 Tests.")) {
							this.negativeBody.setStringValue(EMPTY_STR);
						}
					}
					this.junitSuperClass.setEnabled(false, getFieldEditorParent());
					this.junitSuperClass.setEmptyStringAllowed(true);
					this.junitSuperClass.setStringValue(EMPTY_STR);
					//					override = true;
				} else if (event.getNewValue().equals(P_JUNIT_TYPE_3) || event.getNewValue().equals(P_JUNIT_TYPE_CUSTOM)) {
					this.junitTestMethod.setStringValue("test${method_name}");
					//					if (isEmpty(negativeBody)) {
					//						if (openQuestion(getShell(), "Warning", "Do you want populate Negative Test Body? It is nice to have for Junit 3 Tests.")) {
					//							this.negativeBody.setStringValue(preferenceStore.getDefaultString(P_JUNIT_NEGATIVE_BODY));
					//						}
					//					}
					this.junitSuperClass.setEnabled(true, getFieldEditorParent());
					this.junitSuperClass.setEmptyStringAllowed(false);
				}
			}
			this.junitType = (String) event.getNewValue();
		} else if (source == this.negativeBody) {
			final String value = this.negativeBody.getStringValue();

			if (this.junitType == null || this.junitType.equals(EMPTY_STR)) {
				this.junitType = this.preferenceStore.getString(getPreferenceName(P_JUNIT_TYPE));
			}

			if (this.junitType != null && (this.junitType.equals(P_JUNIT_TYPE_4) || this.junitType.equals(P_JUNIT_TYPE_TESTNG))) {
				if (value != null && !value.equals(EMPTY_STR)) {
					final boolean answer = openQuestion(getShell(), "Question", "For Junit type 4 this field is not necessary. "
							+ "Do you like reset the value of this field?");
					if (answer) {
						this.negativeBody.setStringValue(EMPTY_STR);
					}
				}
			}
			/*} else if (source == this.junitAlwaysCreateTryCatch) {
				final Boolean value = (Boolean) event.getNewValue();
				if (this.junitType == null || this.junitType.equals(EMPTY_STR)) {
					this.junitType = this.preferenceStore.getString(getPreferenceName(P_JUNIT_TYPE));
				}
				if (value && this.junitType.equals(P_JUNIT_TYPE_4)) {
					openWarning(getShell(), "Warning", "It is not recommended to have this on for Junit Type 4.");
				}*/
		} else if (source == this.createNewProfCheckBox) {
			final String newProfile = this.junitProfileName.getStringValue();
			final Boolean checked = (Boolean) event.getNewValue();

			this.junitProfiles.setEnabled(!checked, getFieldEditorParent());
			this.junitProfileName.setEnabled(checked, getFieldEditorParent());
			this.junitProfilePattern.setEnabled(checked, getFieldEditorParent());
			this.deleteCurrentProfCheckBox.setEnabled(!checked && this.allTestProfiles.length > 1, getFieldEditorParent());
			this.junitProfileName.setStringValue(checked ? EMPTY_STR : this.junitTestProfile);
			this.junitProfilePattern.setStringValue(checked ? ASTERISK : this.junitTestProfilePattern);
			setErrorMessage(checked ? "Junit Test Profile Name cannot be empty." : null);
		} else if (source == this.deleteCurrentProfCheckBox) {
			final Boolean checked = (Boolean) event.getNewValue();
			this.createNewProfCheckBox.setEnabled(!checked, getFieldEditorParent());
			this.junitProfiles.setEnabled(!checked, getFieldEditorParent());
			this.junitLocation.setEnabled(!checked, getFieldEditorParent());
			this.classAnnotations.setEnabled(!checked, getFieldEditorParent());
			this.methodAnnotations.setEnabled(!checked, getFieldEditorParent());
			this.methodComment.setEnabled(!checked, getFieldEditorParent());
			this.junitSuperClass.setEnabled(!checked, getFieldEditorParent());
			this.projectComboList.setEnabled(!checked, getFieldEditorParent());
			if (this.junitLocation != null) {
				this.junitLocation.setEnabled(!checked, getFieldEditorParent());
			}
			this.junitTestClass.setEnabled(!checked, getFieldEditorParent());
			this.classInsideBody.setEnabled(!checked, getFieldEditorParent());
			//			this.junitTestFormat.setEnabled(!checked, getFieldEditorParent());
			this.junitTypeRadio.setEnabled(!checked, getFieldEditorParent());
			//this.junitAlwaysCreateTryCatch.setEnabled(!checked, getFieldEditorParent());
			this.junitTestMethod.setEnabled(!checked, getFieldEditorParent());
			this.negativeBody.setEnabled(!checked, getFieldEditorParent());
			this.exceptionBody.setEnabled(!checked, getFieldEditorParent());
			//this.showAllCheckBox.setEnabled(!checked, getFieldEditorParent());
			this.junitCreateMethodBody.setEnabled(!checked, getFieldEditorParent());
			this.createInstance.setEnabled(!checked, getFieldEditorParent());
			this.fieldAnnotations.setEnabled(!checked, getFieldEditorParent());
		} else if (source == this.junitProfileName) {
			final String newProfile = this.junitProfileName.getStringValue();
			final String profilePattern = this.junitProfilePattern.getStringValue();
			final String oldValue = (String) event.getOldValue();
			if (profilePattern.equals(EMPTY_STR) || profilePattern.substring(1).equalsIgnoreCase(oldValue)) {
				this.junitProfilePattern.setStringValue(ASTERISK + newProfile);
			}

			if (!isProfileValid(newProfile)) {
				// Do nothing here.
			}

		} else if (source == this.junitProfilePattern) {
			final String profilePattern = this.junitProfilePattern.getStringValue();
			if (isPatternValid(profilePattern)) {
				setErrorMessage(null);
			}
		}
	}

	/**
	 * @param profile
	 */
	private boolean isProfileValid(final String profile) {
		if (isEmpty(profile)) {
			setErrorMessage("Junit Test Profile Name cannot be empty.");
			return false;
		}

		for (final String[] tmpProfile : this.allTestProfiles) {
			if (profile.equals(tmpProfile[0])) {
				setErrorMessage("The Junit Test Profile already exists.");
				return false;
			}
		}
		return true;
	}

	/**
	 * @param profilePattern
	 */
	private boolean isPatternValid(final String profilePattern) {
		if (isEmpty(profilePattern)) {
			setErrorMessage("Junit Test Profile Pattern cannot be empty.");
			return false;
		}
		for (final String[] tmpProfile : this.allTestProfiles) {
			final String oldPattern = this.preferenceStore
					.getString(tmpProfile[0].equalsIgnoreCase(PROFILE_DEFAULT) ? P_JUNIT_PROFILE_PATTERN : P_JUNIT_PROFILE_PATTERN
							+ UNDERSCORE + tmpProfile[0]);
			if (profilePattern.equalsIgnoreCase(oldPattern)) {
				setErrorMessage("The Class Name Pattern already exists.");
				return false;
			}
		}
		return true;
	}

	/**
	 *
	 * @param preference
	 * @param profile
	 * @return
	 */
	private String getPreferenceName(final String preference) {
		return this.junitTestProfile.equals(PROFILE_DEFAULT) ? preference : preference + UNDERSCORE + this.junitTestProfile;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench workbench) {
	}

}

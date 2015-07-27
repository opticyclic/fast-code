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

import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.preferences.PreferenceConstants.P_DELIMITER_FOR_FILE_TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.P_EMBEDDED_FIELDS_EXCLUDE_PACKAGE;
import static org.fastcode.preferences.PreferenceConstants.P_EMBEDDED_FIELDS_INCLUDE_PACKAGE;
import static org.fastcode.preferences.PreferenceConstants.P_EMBEDDED_FIELDS_VIEW;
import static org.fastcode.preferences.PreferenceConstants.P_FILE_TEMPLATE_PLACHOLDER_NAME;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_STRICTLY_MAINTAIN_GETTER_SETTER;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeConstants.EMBEDDED_FIELDS_VIEW;
import org.fastcode.templates.util.VariablesUtil;

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

public class TemplatePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	protected ListEditor			includePackageList;

	protected ListEditor			excludePackageList;

	private final IPreferenceStore	preferenceStore;

	private RadioGroupFieldEditor	embeddedFieldsView;

	private static final String[][]	FLAT_HIERARCHY	= { { "Flat View", "flatView" }, { "Hierarchical View", "hierarchicalView" } };
	private BooleanFieldEditor		maintainGetterSetterCheckBox;

	private BooleanFieldEditor		enableTemplateBodyInDialogBoxesCheckBox;

	private StringFieldEditor		delimiterForFileTempalteField;

	private StringFieldEditor		placeholderNameForFileTemplate;

	/**
	 *
	 */
	public TemplatePreferencePage() {
		super(GRID);
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		setPreferenceStore(this.preferenceStore);
		setDescription("Create Template preference page");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		this.embeddedFieldsView = new RadioGroupFieldEditor(P_EMBEDDED_FIELDS_VIEW, "Show Child Fields in :", FLAT_HIERARCHY.length,
				FLAT_HIERARCHY, getFieldEditorParent(), true);
		addField(this.embeddedFieldsView);

		this.maintainGetterSetterCheckBox = new BooleanFieldEditor(P_GLOBAL_STRICTLY_MAINTAIN_GETTER_SETTER,
				"Strictly maintain getter/setter  ", getFieldEditorParent());
		addField(this.maintainGetterSetterCheckBox);

		this.enableTemplateBodyInDialogBoxesCheckBox = new BooleanFieldEditor(P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES,
				"Enable template body in dialog boxes", getFieldEditorParent());
		addField(this.enableTemplateBodyInDialogBoxesCheckBox);

		this.delimiterForFileTempalteField = new StringFieldEditor(P_DELIMITER_FOR_FILE_TEMPLATE, "Delimiter for file template : \n(leave it blank for white space)",
				getFieldEditorParent());
		addField(this.delimiterForFileTempalteField);

		this.placeholderNameForFileTemplate = new StringFieldEditor(P_FILE_TEMPLATE_PLACHOLDER_NAME,
				"Placeholder Name for file template :", getFieldEditorParent());
		addField(this.placeholderNameForFileTemplate);

		this.includePackageList = new FastCodeListEditor(P_EMBEDDED_FIELDS_INCLUDE_PACKAGE, "Include Package :", getFieldEditorParent(),
				-1, null);
		addField(this.includePackageList);

		this.excludePackageList = new FastCodeListEditor(P_EMBEDDED_FIELDS_EXCLUDE_PACKAGE, "Exclude Package :", getFieldEditorParent(),
				-1, null);
		addField(this.excludePackageList);

	}

	@Override
	public boolean isValid() {
		final boolean valid = super.isValid();
		return valid;
	}

	@Override
	public boolean performOk() {
		VariablesUtil.reload(true);
		final boolean status = super.performOk();
		return status;
	}

	@Override
	protected void performApply() {
		performOk();
	}

	/**
	 * @param event
	 *
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		final Object source = event.getSource();
		final Object newValue = event.getNewValue();
		if (newValue == EMBEDDED_FIELDS_VIEW.HIERARCHICAL_VIEW.getValue()){
			MessageDialog.openWarning(getShell(), "Warning", "Please include packages to view parent-child fields as hierarchical view.");
		}
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

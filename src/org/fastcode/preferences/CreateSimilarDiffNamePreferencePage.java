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

import static org.fastcode.preferences.PreferenceConstants.CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

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

public class CreateSimilarDiffNamePreferencePage extends CreateSimilarPreferencePage implements IWorkbenchPreferencePage {

	public CreateSimilarDiffNamePreferencePage() {
		super();
		this.preferenceId = CREATE_SIMILAR_PREFERENCE_DIFFERENT_NAME;
		setDescription("Create similar with Different Name Preference Page.");

	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {
		super.createFieldEditors();
		disableIrreleventFields();
	}

	@Override
	public boolean performOk() {
		return super.performOk();
	}

	/**
	 *
	 */
	private void disableIrreleventFields() {
		// toPattern.setEnabled(false, getFieldEditorParent());
		this.projectComboList.setEnabled(false, getFieldEditorParent());
		if (this.sourceComboList != null) {
			this.sourceComboList.setEnabled(false, getFieldEditorParent());
		}
		this.inclInstanceCheckBox.setEnabled(false, getFieldEditorParent());
		this.inclGetterSetterCheckBox.setEnabled(false, getFieldEditorParent());
		this.createMethodBody.setEnabled(false, getFieldEditorParent());
		this.convertMethodParamCheckBox.setEnabled(false, getFieldEditorParent());
		//this.assignReturnCheckBox.setEnabled(false, getFieldEditorParent());
		//this.returnVariableName.setEnabled(false, getFieldEditorParent());
		// createWorkingSetCheckBox.setEnabled(false, getFieldEditorParent());
		// workingSetName.setEnabled(false, getFieldEditorParent());
	}

	/*
	 * @Override public void propertyChange(PropertyChangeEvent event) {
	 * super.propertyChange(event); String frmPattern =
	 * fromPattern.getStringValue();
	 * toPattern.getTextControl(getFieldEditorParent
	 * ()).setText(frmPattern.replaceAll("ANY_CLASS", "input"));
	 * disableIrreleventFields(); }
	 */
	@Override
	protected boolean isForValueBeans() {
		return false;
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
/**
 *
 */
package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class CreateSimilarPreferencesPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public CreateSimilarPreferencesPage() {
		super(GRID);
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		setPreferenceStore(store);
		setDescription("Fast Code Create Similar Preference Page");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	@Override
	protected void createFieldEditors() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench arg0) {
		// TODO Auto-generated method stub

	}

}

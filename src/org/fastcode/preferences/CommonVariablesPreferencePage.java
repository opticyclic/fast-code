package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.preferences.PreferenceConstants.P_ALL_COMMON_VARIABLES;
import static org.fastcode.setting.GlobalSettings.getInstance;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.util.MultiStringFieldEditor;


public class CommonVariablesPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private StringFieldEditor		dateFormat;
	private MultiStringFieldEditor	commonvariables;

	private final boolean					errorShown	= false;
	private final String			defaultcommonvariables;
	TableViewer						variablesViewer;
	private Table					table;

	/**
	 *
	 */
	public CommonVariablesPreferencePage() {
		super(GRID);
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		setPreferenceStore(store);
		setDescription("Common Local Variables Used");
		this.defaultcommonvariables = getPreferenceStore().getDefaultString(P_ALL_COMMON_VARIABLES);
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	public void createFieldEditors() {

		this.commonvariables = new MultiStringFieldEditor(P_ALL_COMMON_VARIABLES, "", true,
				getFieldEditorParent());
		addField(this.commonvariables);
	}

	/**
	 * @param event
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		final Object newValue = event.getNewValue();
	}

	@Override
	public boolean performOk() {
		final boolean status = super.performOk();
		if (!status) {
			return status;
		}
		final GlobalSettings globalSettings = getInstance();
		return status;
	}

	@Override
	public void init(final IWorkbench workbench) {
	}

}

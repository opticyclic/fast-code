package org.fastcode.preferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Shell;

public class FieldsPreferencePage extends StatusDialog implements IPropertyChangeListener {

	public FieldsPreferencePage(final Shell parent) {
		super(parent);

	}

	private List				fields				= null;
	private boolean				isValid;
	private IPreferenceStore	store;
	private FieldEditor			invalidFieldEditor	= null;

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getProperty().equals(FieldEditor.IS_VALID)) {
			final boolean newValue = ((Boolean) event.getNewValue()).booleanValue();
			// If the new value is true then we must check all field editors.
			// If it is false, then the page is invalid in any case.
			if (newValue) {
				checkState();
			} else {
				this.invalidFieldEditor = (FieldEditor) event.getSource();
				setValid(newValue);
			}
		}
	}

	public void setValid(final boolean b) {
		final boolean oldValue = this.isValid;
		this.isValid = b;
	}

	@SuppressWarnings("unchecked")
	protected void addField(final FieldEditor editor) {
		if (this.fields == null) {
			this.fields = new ArrayList();
		}
		this.fields.add(editor);
	}

	protected void initialize() {
		if (this.fields != null) {
			final Iterator e = this.fields.iterator();
			while (e.hasNext()) {
				final FieldEditor pe = (FieldEditor) e.next();

				pe.setPropertyChangeListener(this);
				pe.setPreferenceStore(this.store);
				pe.load();
			}
		}
	}

	protected void checkState() {
		boolean valid = true;
		this.invalidFieldEditor = null;
		// The state can only be set to true if all
		// field editors contain a valid value. So we must check them all
		if (this.fields != null) {
			final int size = this.fields.size();
			for (int i = 0; i < size; i++) {
				final FieldEditor editor = (FieldEditor) this.fields.get(i);
				valid = valid && editor.isValid();
				if (!valid) {
					this.invalidFieldEditor = editor;
					break;
				}
			}
		}
		setValid(valid);
	}

	@Override
	public void okPressed() {

		if (this.fields != null) {
			final Iterator e = this.fields.iterator();
			while (e.hasNext()) {
				final FieldEditor pe = (FieldEditor) e.next();
				pe.store();
				// pe.setPresentsDefaultValue(false);
			}
		}
		super.okPressed();

	}

	protected void performDefaults() {
		if (this.fields != null) {
			final Iterator e = this.fields.iterator();
			while (e.hasNext()) {
				final FieldEditor pe = (FieldEditor) e.next();
				pe.loadDefault();
			}
		}
		// Force a recalculation of my error state.
		checkState();

	}

	public void setPreferenceStore(final IPreferenceStore store) {
		this.store = store;
	}

	public IPreferenceStore getPreferenceStore() {
		return this.store;
	}

}

package org.fastcode.preferences;

import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_ANNOTATION_TYPES;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.VARIABLE_FOLDER;
import static org.fastcode.preferences.PreferenceConstants.P_GETTER_CUSTOM_FORMAT;
import static org.fastcode.preferences.PreferenceConstants.P_GETTER_SETTER_FORMAT;
import static org.fastcode.preferences.PreferenceConstants.P_GETTER_SETTER_POSITION;
import static org.fastcode.preferences.PreferenceConstants.P_SETTER_CUSTOM_FORMAT;
import static org.fastcode.preferences.PreferenceConstants.P_SETTER_VAR_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.P_VARIABLE_ANNOTATION;
import static org.fastcode.util.XMLUtil.exportXML;
import static org.fastcode.util.XMLUtil.importXML;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
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
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.util.MultiStringFieldEditor;

public class CreateVariablePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private final IPreferenceStore	preferenceStore;
	private RadioGroupFieldEditor	getterSetterFormat;
	private StringFieldEditor		settetVarPrefix;
	private static final String[][]	GETTER_SETTER_FORMAT	= { { "Multiline With Comment", "multilineComment" },
			{ "Multiline Without Comment", "multilineNoComment" }, { "Single Line", "singleLine" }, { "Custom", "custom" } };
	private ListEditor				annotationList;
	private Button					importButton;
	private Button					exportButton;
	private StringFieldEditor		getterCustomFormat;
	private StringFieldEditor		setterCustomFormat;
	private boolean					enableCustomFormat		= false;
	private static final String[][]	GETTER_SETTER_POSITION	= { { "Getter Setter Pair", "getterSEtterPair" },
			{ "Getter First Then Setter", "getterfirst" }	};
	private RadioGroupFieldEditor	getterSetterPosition;

	public CreateVariablePreferencePage() {
		super(GRID);
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		setPreferenceStore(this.preferenceStore);
		setDescription("Variable Preference");
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		//this.enableCustomFormat=false;
		if (event.getSource() == this.getterSetterFormat) {
			final RadioGroupFieldEditor editor = (RadioGroupFieldEditor) event.getSource();
			if (editor.getPreferenceName().equals(P_GETTER_SETTER_FORMAT)) {
				if (event.getNewValue().toString().equals("custom")) {
					this.enableCustomFormat = true;
				} else {
					this.enableCustomFormat = false;
				}
			}

			this.getterCustomFormat.setEnabled(this.enableCustomFormat, getFieldEditorParent());
			this.setterCustomFormat.setEnabled(this.enableCustomFormat, getFieldEditorParent());

		}
	}

	@Override
	protected void createFieldEditors() {
		this.getterSetterFormat = new RadioGroupFieldEditor(P_GETTER_SETTER_FORMAT, "Getter Setter Format", GETTER_SETTER_FORMAT.length,
				GETTER_SETTER_FORMAT, getFieldEditorParent(), true);
		addField(this.getterSetterFormat);
		if (this.preferenceStore.getString(P_GETTER_SETTER_FORMAT).equals("custom")) {
			this.enableCustomFormat = true;
		}
		this.getterSetterPosition = new RadioGroupFieldEditor(P_GETTER_SETTER_POSITION, "Group Getter Setter",
				GETTER_SETTER_POSITION.length, GETTER_SETTER_POSITION, getFieldEditorParent(), true);
		addField(this.getterSetterPosition);
		this.settetVarPrefix = new StringFieldEditor(P_SETTER_VAR_PREFIX, "Setter Parameter Prefix", 30, getFieldEditorParent());
		addField(this.settetVarPrefix);
		this.getterCustomFormat = new MultiStringFieldEditor(P_GETTER_CUSTOM_FORMAT, "Custom getter format", getFieldEditorParent());
		this.getterCustomFormat.setEnabled(this.enableCustomFormat, getFieldEditorParent());
		addField(this.getterCustomFormat);
		this.setterCustomFormat = new MultiStringFieldEditor(P_SETTER_CUSTOM_FORMAT, "Custom setter format", getFieldEditorParent());
		addField(this.setterCustomFormat);
		this.setterCustomFormat.setEnabled(this.enableCustomFormat, getFieldEditorParent());
		this.annotationList = new FastCodeListEditor(P_VARIABLE_ANNOTATION, "Choose Annotation: ", getFieldEditorParent(),
				CONSIDER_ANNOTATION_TYPES, null);
		addField(this.annotationList);
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
					//processXML("CreateVariablePreferences.xml", EMPTY_STR, VARIABLE_FOLDER);
					importXML("CreateVariablePreferences.xml", EMPTY_STR, VARIABLE_FOLDER);
				} catch (final Exception ex) {
					try {
						throw new Exception("There was some error in Import templates : " + ex.getMessage());
					} catch (final Exception ex1) {
						MessageDialog.openError(null, "Import Failed", ex1.getMessage() + "....Please retry after making the changes");
						ex1.printStackTrace();
					}
					ex.printStackTrace();
				} finally {

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
					//processXML("CreateVariablePreferences.xml", EMPTY_STR, VARIABLE_FOLDER);
					exportXML("CreateVariablePreferences.xml", EMPTY_STR, VARIABLE_FOLDER);
				} catch (final Exception ex) {
					try {
						throw new Exception("There was some error in Export templates : " + ex.getMessage());
					} catch (final Exception ex1) {
						MessageDialog.openError(null, "Export Failed", ex.getMessage() + "....Please retry after making the changes");
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
	public boolean performOk() {
		final boolean status = super.performOk();

		return status;
	}

	@Override
	public void init(final IWorkbench arg0) {

	}

}

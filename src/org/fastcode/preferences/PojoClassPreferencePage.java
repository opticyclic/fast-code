package org.fastcode.preferences;

import static org.eclipse.jdt.core.search.SearchEngine.createWorkspaceScope;
import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_CLASSES;
import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_INTERFACES;
import static org.eclipse.jdt.ui.JavaUI.createTypeDialog;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.preferences.PreferenceConstants.P_DB_PACKAGE_FOR_POJO_CLASS;
import static org.fastcode.preferences.PreferenceConstants.P_POJO_BASE_CLASS;
import static org.fastcode.preferences.PreferenceConstants.P_POJO_IMPLEMENT_INTERFACES;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATES_TO_ENABLE_POJO;
import static org.fastcode.preferences.PreferenceConstants.P_WORKING_JAVA_PROJECT;
import static org.fastcode.util.SourceUtil.getAllProjects;
import static org.fastcode.util.SourceUtil.getJavaProject;
import static org.fastcode.util.StringUtil.isEmpty;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.util.MultiStringFieldEditor;

public class PojoClassPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	private final IPreferenceStore		preferenceStore;
	private ComboFieldEditor			projectComboList;
	//private ComboFieldEditor		pojoClassLocation;
	protected StringButtonFieldEditor	pojoClassLocation;
	protected StringButtonFieldEditor	baseClass;
	//protected StringButtonFieldEditor	interfaceToImplement;
	protected ListEditor				interfaces;
	String								workingJavaProject;
	private StringFieldEditor			templatesToEnablePojoClass;

	public PojoClassPreferencePage() {
		super(GRID);
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		setPreferenceStore(this.preferenceStore);
		setDescription("POJO Class Preferences");
	}

	@Override
	public void init(final IWorkbench arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createFieldEditors() {
		final String[][] projects = getAllProjects();
		this.projectComboList = new ComboFieldEditor(P_WORKING_JAVA_PROJECT, "Project:", projects, getFieldEditorParent());
		addField(this.projectComboList);

		if (isEmpty(this.workingJavaProject)) {
			this.workingJavaProject = this.preferenceStore.getString(P_WORKING_JAVA_PROJECT);
		}
		this.pojoClassLocation = new StringButtonFieldEditor(P_DB_PACKAGE_FOR_POJO_CLASS, "POJO Class Location :", getFieldEditorParent()) {
			@Override
			protected String changePressed() {
				try {
					final IJavaProject javaProject = getJavaProject(PojoClassPreferencePage.this.workingJavaProject);
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
		addField(this.pojoClassLocation);

		this.baseClass = new StringButtonFieldEditor(P_POJO_BASE_CLASS, "Base Class :", getFieldEditorParent()) {
			@Override
			protected String changePressed() {
				setTextLimit(200);
				try {
					final SelectionDialog selectionDialog = createTypeDialog(getShell(), null, createWorkspaceScope(), CONSIDER_CLASSES,
							false, EMPTY_STR);
					if (selectionDialog.open() == SWT.CANCEL) {
						return EMPTY_STR;
					}
					final IType type = (IType) selectionDialog.getResult()[0];

					return type.getFullyQualifiedName();
				} catch (final JavaModelException e) {
					e.printStackTrace();
				}
				return EMPTY_STR;
			}
		};
		addField(this.baseClass);

		this.interfaces = new FastCodeListEditor(P_POJO_IMPLEMENT_INTERFACES, "Interfaces To &Implement:", getFieldEditorParent(),
				CONSIDER_INTERFACES, null);
		addField(this.interfaces);
		this.templatesToEnablePojoClass = new MultiStringFieldEditor(P_TEMPLATES_TO_ENABLE_POJO,
				"Enable Pojo class for following templates", getFieldEditorParent());
		addField(this.templatesToEnablePojoClass);
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		super.propertyChange(event);
		final Object source = event.getSource();

		if (source == this.projectComboList) {
			this.workingJavaProject = (String) event.getNewValue();
		}
	}
}

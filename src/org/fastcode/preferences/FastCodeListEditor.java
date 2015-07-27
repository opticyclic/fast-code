/**
 *
 */
package org.fastcode.preferences;

import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_ANNOTATION_TYPES;
import static org.eclipse.jdt.ui.IJavaElementSearchConstants.CONSIDER_CLASSES_AND_INTERFACES;
import static org.eclipse.jdt.ui.JavaUI.createTypeDialog;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.ImportUtil.retriveStaticMembers;
import static org.fastcode.util.SourceUtil.getAnnotationDesc;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.isValidPackageName;

import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.fastcode.common.MemberSelectionDialog;
import org.fastcode.setting.GlobalSettings;

/**
 * @author Gautam
 *
 */
public class FastCodeListEditor extends ListEditor {

	private final int			type;
	private final IJavaProject	javaProject;

	/**
	 *
	 * @param preference
	 * @param labelText
	 * @param parent
	 * @param type
	 * @param javaProject
	 */
	public FastCodeListEditor(final String preference, final String labelText, final Composite parent, final int type,
			final IJavaProject javaProject) {
		super(preference, labelText, parent);
		this.type = type;
		this.javaProject = javaProject;
	}

	@Override
	protected void doFillIntoGrid(final Composite parent, final int numColumns) {
		final Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns + 1;
		control.setLayoutData(gd);

		final Composite composite = new Composite(parent, SWT.NONE);
		final GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.horizontalSpan = numColumns + 1;
		gridData.widthHint = SWT.DEFAULT;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(numColumns + 1, false));
		final List list = getListControl(composite);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = SWT.DEFAULT;
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns;
		gd.grabExcessHorizontalSpace = true;
		list.setLayoutData(gd);

		final Composite buttonBox = getButtonBoxControl(composite);
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		buttonBox.setLayoutData(gd);

	}

	/**
	 * @param items
	 */
	@Override
	protected String createList(final String[] items) {
		final StringBuilder retStr = new StringBuilder();
		for (final String item : items) {
			retStr.append(item + NEWLINE);
		}
		String retString = retStr.toString().trim();
		if (!retString.equals(EMPTY_STR)) {
			retString = retString.replaceAll(NEWLINE + "$", EMPTY_STR);
		}
		return retString;
	}

	/**
	 * @param stringList
	 *
	 */
	@Override
	protected String[] parseString(final String stringList) {
		return stringList.split(NEWLINE);
	}

	/**
	 *
	 */
	@Override
	protected String getNewInputObject() {
		SelectionDialog selectionDialog = null;
		int ret = Window.CANCEL;

		if (this.type == -1) {
			final InputDialog inputDialog = new InputDialog(new Shell(), "Package to include/exclude", "Enter package name", "",
					new IInputValidator() {

						@Override
						public String isValid(final String arg0) {
							if (!isValidPackageName(arg0)) {
								return "Please enter valid package name";
							}
							return null;
						}

					});

			final int retCode = inputDialog.open();
			final String packageName = inputDialog.getValue();
			if (retCode == Window.CANCEL || packageName == null || isEmpty(packageName)) {
				return null;
			} else {
				return packageName;
			}
		}

		try {
			//			this.type == IJavaElement.PACKAGE_FRAGMENT ? JavaUI.createPackageDialog(getShell(), this.javaProject, 0, EMPTY_STR) :
			selectionDialog = createTypeDialog(new Shell(), null, SearchEngine.createWorkspaceScope(), this.type, false, EMPTY_STR);
			ret = selectionDialog.open();
		} catch (final JavaModelException ex) {
			ex.printStackTrace();
			return null;
		}
		if (ret == Window.CANCEL) {
			return null;
		}

		final IType resultType;
		final IPackageFragment resultPackageFragment;
		/*if (this.type == IJavaElement.PACKAGE_FRAGMENT) {
			resultType = null;
			resultPackageFragment = (IPackageFragment)selectionDialog.getResult()[0];
		} else {*/
		resultType = (IType) selectionDialog.getResult()[0];
		resultPackageFragment = null;
		//}

		//String userInput = this.type == IJavaElement.PACKAGE_FRAGMENT ? resultPackageFragment.getElementName() : resultType.getFullyQualifiedName();

		String userInput = resultType.getFullyQualifiedName();

		if (this.type == CONSIDER_CLASSES_AND_INTERFACES) {

			try {
				if (resultType.getPackageFragment().getElementName().equals("java.lang")) {
					MessageDialog.openWarning(getShell(), "Warning", "Classes should be user defined");
					return null;
				} else {

					final IMember[] staticMembers = retriveStaticMembers(resultType);
					if (staticMembers != null && staticMembers.length > 0) {
						selectionDialog = new MemberSelectionDialog(getShell(), "Static Member Selection", "Select a Static Member",
								staticMembers, false);
						if (selectionDialog.open() == Window.CANCEL) {
							return userInput;
						}
						final IMember selectedStaticMember = (IMember) selectionDialog.getResult()[0];
						return userInput + DOT + selectedStaticMember.getElementName();

					} else {
						MessageDialog.openWarning(getShell(), "Warning", "Please select a class with static members");
						return null;
					}
				}

			} catch (final JavaModelException ex) {
				ex.printStackTrace();
			}
		}

		if (this.type != CONSIDER_ANNOTATION_TYPES) {
			return userInput;
		}

		// If it is Annotation open a text box where the user can enter parameters
		userInput = getAnnotationDesc(resultType);
		userInput = "@" + userInput;

		final GlobalSettings globalSettings = getInstance();
		final Map<String, String> typesMap = globalSettings.getAnnotationTypesMap();
		boolean doUpdateTypesMap = false;
		if (typesMap.containsKey(resultType.getElementName())) {
			final String exstClass = typesMap.get(resultType.getElementName());
			if (!exstClass.equals(resultType.getFullyQualifiedName())) {
				typesMap.put(resultType.getElementName(), resultType.getFullyQualifiedName());
				doUpdateTypesMap = true;
			}
		} else {
			doUpdateTypesMap = true;
			typesMap.put(resultType.getElementName(), resultType.getFullyQualifiedName());
		}
		if (doUpdateTypesMap) {
			globalSettings.updateAnnotationTypesMap();
		}

		final String title = this.type == CONSIDER_ANNOTATION_TYPES ? "Annotations" : "Class";
		String message = "Please type a new ";
		message += this.type == CONSIDER_ANNOTATION_TYPES ? "Annotations" : "Class";

		final InputDialog dialog = new InputDialog(new Shell(), title, message, userInput, new IInputValidator() {
			@Override
			public String isValid(final String newText) {
				if (FastCodeListEditor.this.type == CONSIDER_ANNOTATION_TYPES) {
					if (newText.trim().length() < 2 || !newText.startsWith("@")) {
						return "Annotation cannot be less than two characters or start without @";
					}
				}
				return null;
			}
		});
		ret = dialog.open();
		if (ret == Window.CANCEL) {
			return null;
		}
		return dialog.getValue();
	}

}

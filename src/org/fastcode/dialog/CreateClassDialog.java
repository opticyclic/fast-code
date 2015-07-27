package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.COMMON_CLASS_SUFFIX;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.util.MessageUtil.getChoiceFromMultipleValues;
import static org.fastcode.util.SourceUtil.getAlteredPackageName;
import static org.fastcode.util.SourceUtil.getDefaultPathFromProject;
import static org.fastcode.util.SourceUtil.getPackagesInProject;
import static org.fastcode.util.StringUtil.changeFirstLetterToUpperCase;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.isFirstLetterUpperCase;
import static org.fastcode.util.StringUtil.isJavaReservedWord;
import static org.fastcode.util.StringUtil.isValidVariableName;

import java.util.ArrayList;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.fastcode.common.CreateVariableData;
import org.fastcode.common.PackageSelectionDialog;
import org.fastcode.popup.actions.snippet.FastCodeCache;
import org.fastcode.setting.GlobalSettings;

public class CreateClassDialog extends CreateVariableDialog {
	private Combo	packageCombo;
	private Button	packageBrowseButton;
	private Text	classNameText;
	private Button	defaultConstructor;
	private Combo	baseClassCombo;
	private List	interfacesList;
	private Button	baseClassBrowseButton;
	private Button	interfaceBrowseButton;
	private String	className	= EMPTY_STR;

	public CreateClassDialog(final Shell shell) {
		super(shell);
	}

	/**
	 * @param parent
	 *
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);

		super.createErrorMessageText(parent);
		createAllPane(parent);
		this.packageCombo.setFocus();
		return super.createDialogArea(parent);
	}

	/**
	 * @param shell
	 * @param createVariableData
	 */
	public CreateClassDialog(final Shell shell, final CreateVariableData createVariableData) {
		super(shell);
		this.shell = shell;
		this.createVariableData = createVariableData;
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Create class and variable");

	}

	/**
	 * @param parent
	 */
	private void createAllPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout(6, false);
		composite.setLayout(layout);
		createClassNamePane(composite);
		createPackageSelectionPane(composite);

		if (this.createVariableData.isCreateClassDetailed()) {
			createBaseClassSelectionPane(composite);
		}
		if (this.createVariableData.isCreateClassDetailed()) {
			createDefaultConstructorPane(composite);
		}
	}

	private void createPackageSelectionPane(final Composite parent) {

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(parent, SWT.NONE);
		label.setText("Select Package:");
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData(GridData.FILL_HORIZONTAL);
		gridDataText.grabExcessHorizontalSpace = true;

		this.packageCombo = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);// new
		// Text(composite,
		// SWT.BORDER);
		this.packageCombo.setSize(200, 20);
		this.packageCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 200;
		this.packageCombo.setEnabled(true);
		String tmpPackagename = EMPTY_STR;
		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (this.createVariableData.getPackageFragment() != null) {
			tmpPackagename = getAlteredPackageName(this.createVariableData.getPackageFragment());
			this.packageCombo.add(tmpPackagename);
			this.packageCombo.select(0);
			fastCodeCache.getPackageSet().add(this.createVariableData.getPackageFragment());
		}

		if (!fastCodeCache.getPackageSet().isEmpty()) {
			for (final IPackageFragment pkgFrgmt : fastCodeCache.getPackageSet()) {
				final String tmpPackageName2 = getAlteredPackageName(pkgFrgmt);
				if (!tmpPackagename.equals(tmpPackageName2)) {
					this.packageCombo.add(tmpPackageName2);
				}
			}
		}
		this.packageCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String selectedPkgName = CreateClassDialog.this.packageCombo.getText();
				if (isEmpty(selectedPkgName)) {
					setErrorMessage("Please select a package", false, EMPTY_STR);
				} else {
					setErrorMessage("Please select a package", true, EMPTY_STR);
				}
				try {
					for (final IPackageFragment pkg : fastCodeCache.getPackageSet()) {
						if (getAlteredPackageName(pkg).equals(selectedPkgName)) {
							CreateClassDialog.super.createVariableData.setPackageFragment(pkg);
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
				if (!CreateClassDialog.this.className.equals(EMPTY_STR)) {
					CreateClassDialog.this.classNameText.setText(CreateClassDialog.this.className + getCommonSuffix());
				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		final GridData gridDataButton = new GridData();

		this.packageBrowseButton = new Button(parent, SWT.PUSH);
		this.packageBrowseButton.setText("Browse");
		this.packageBrowseButton.setLayoutData(gridDataButton);
		this.packageBrowseButton.setEnabled(true);
		final PackageSelectionDialog selectionDialog = null;

		this.packageBrowseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				try {
					final String srcPath = getDefaultPathFromProject(CreateClassDialog.this.createVariableData.getJavaProject(), "source",
							EMPTY_STR);
					final IPackageFragment allPackages[] = getPackagesInProject(CreateClassDialog.this.createVariableData.getJavaProject(),
							srcPath, "source");
					if (allPackages == null) {
						return;
					}
					final java.util.List<IPackageFragment> nonEmptyPackages = new ArrayList<IPackageFragment>();
					for (final IPackageFragment packageFragment : allPackages) {
						final ICompilationUnit[] compilationUnits = packageFragment.getCompilationUnits();
						if (compilationUnits != null && compilationUnits.length > 0) {
							nonEmptyPackages.add(packageFragment);
						}
					}

					final PackageSelectionDialog selectionDialog = new PackageSelectionDialog(new Shell(), "Package ",
							"Choose a package from below", allPackages);

					IPackageFragment packageFragment = null;
					if (selectionDialog.open() != CANCEL) {
						packageFragment = (IPackageFragment) selectionDialog.getFirstResult();
						// CreateClassDialog.this.packageCombo.setText(getAlteredPackageName(packageFragment));
						CreateClassDialog.this.packageCombo.add(getAlteredPackageName(packageFragment));
						CreateClassDialog.this.packageCombo.select(CreateClassDialog.this.packageCombo.getItemCount() - 1);
						if (!fastCodeCache.getPackageSet().contains(packageFragment)) {
							fastCodeCache.getPackageSet().add(packageFragment);
						}
						CreateClassDialog.this.createVariableData.setPackageFragment(packageFragment);
						if (!CreateClassDialog.this.className.equals(EMPTY_STR)) {
							CreateClassDialog.this.classNameText.setText(CreateClassDialog.this.className + getCommonSuffix());
						}
						setErrorMessage(CreateClassDialog.this.defaultMessage, false, EMPTY_STR);
					}

				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * @param parent
	 */
	private void createClassNamePane(final Composite parent) {
		final GridData gridDataLabel = new GridData();
		final Label classNameLabel = new Label(parent, SWT.NONE);

		classNameLabel.setText("Class Name:");
		classNameLabel.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;
		gridDataText.horizontalAlignment = SWT.FILL;
		gridDataText.horizontalSpan = 1;
		this.classNameText = new Text(parent, SWT.BORDER);
		this.classNameText.setSize(200, 20);
		this.classNameText.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 200;
		this.classNameText.setEnabled(true);

		this.classNameText.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent arg0) {
				validateClassname(true);
				if (!isFirstLetterUpperCase(CreateClassDialog.this.classNameText.getText().trim())) {
					CreateClassDialog.this.classNameText.setText(changeFirstLetterToUpperCase(CreateClassDialog.this.classNameText
							.getText().trim()));
				}
				if (CreateClassDialog.this.className.equals(EMPTY_STR)) {
					CreateClassDialog.this.className = CreateClassDialog.this.classNameText.getText();
					CreateClassDialog.this.classNameText.setText(CreateClassDialog.this.className + getCommonSuffix());
				}
			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.classNameText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent arg0) {
				validateClassname(false);

			}
		});
		//dummy label just for alignment
		final Label classNameLabel2 = new Label(parent, SWT.NONE);
	}

	private boolean doesClassExistInPackage(final String className) throws JavaModelException {
		if (CreateClassDialog.this.createVariableData.getPackageFragment() == null) {
			setErrorMessage("Please select a package", false, EMPTY_STR);
			return false;
		}
		final IJavaElement[] classesInPackage = CreateClassDialog.this.createVariableData.getPackageFragment().getChildren();

		for (int i = 0; i < classesInPackage.length; i++) {
			final String tmpClsName = classesInPackage[i].getElementName();
			if (className
					.equalsIgnoreCase(tmpClsName.substring(0, tmpClsName.indexOf(DOT/* + this.createVariableData.getCompUnitType()*/)))) {
				return true;
			}
		}

		return false;
	}

	private void validateClassname(final boolean focusLost) {
		final String className = CreateClassDialog.this.classNameText.getText().trim();
		try {
			if (focusLost && isEmpty(className)) {
				setErrorMessage("Class name cannot be empty", false, EMPTY_STR);
				CreateClassDialog.this.classNameText.setFocus();
				return;
			} else {

				setErrorMessage("Class name cannot be empty", true, EMPTY_STR);
			}
			if (doesClassExistInPackage(className)) {
				CreateClassDialog.this.setErrorMessage("Class with this name already exists in project", false, EMPTY_STR);
				CreateClassDialog.this.classNameText.setFocus();
				return;
			} else {
				CreateClassDialog.this.setErrorMessage("Class with this name already exists in project", true, EMPTY_STR);
			}
			if (isJavaReservedWord(className)) {
				CreateClassDialog.this.setErrorMessage("Class name cannot be Java reserved word", false, EMPTY_STR);
				CreateClassDialog.this.classNameText.setFocus();
				return;
			} else {
				CreateClassDialog.this.setErrorMessage("Class name cannot be Java reserved word", true, EMPTY_STR);
			}
			if (!isEmpty(className) && !isValidVariableName(className)) {
				CreateClassDialog.this.setErrorMessage("Special Characters are not allowed in class name", false, EMPTY_STR);
				CreateClassDialog.this.classNameText.setFocus();
				return;
			} else {
				CreateClassDialog.this.setErrorMessage("Special Characters are not allowed in class name", true, EMPTY_STR);
			}
			CreateClassDialog.this.createVariableData.setClassName(className);

		} catch (final JavaModelException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	@Override
	protected void okPressed() {
		if (isEmpty(this.packageCombo.getText())) {
			setErrorMessage("Please select a package.", false, EMPTY_STR);
			this.packageCombo.setFocus();
			return;
		} else {
			setErrorMessage("Please select a package.", true, EMPTY_STR);
		}

		if (isEmpty(this.classNameText.getText())) {
			setErrorMessage("Please enter a class name.", false, EMPTY_STR);
			this.classNameText.setFocus();
			return;
		} else {
			setErrorMessage("Please enter a class name.", true, EMPTY_STR);
		}
		super.okPressed();
	}

	/**
	 * @param parent
	 */
	private void createDefaultConstructorPane(final Composite parent) {
		/*final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);*/
		this.defaultConstructor = this.createButton(parent, "defaultConstrouctor", SWT.CHECK);
		this.defaultConstructor.setText("Create default constructor");
		this.defaultConstructor.setSelection(false);
		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;
		gridDataText.horizontalAlignment = SWT.FILL;
		gridDataText.horizontalSpan = 5;
		this.defaultConstructor.setLayoutData(gridDataText);
		this.defaultConstructor.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				if (CreateClassDialog.this.defaultConstructor.getSelection()) {
					CreateClassDialog.this.createVariableData.setDefaultConsReqd(true);
				}
			}

		});
	}

	/**
	 * @param parent
	 */
	private void createBaseClassSelectionPane(final Composite parent) {
		//final Composite composite=parent;
		/*final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 6;
		composite.setLayout(layout);*/

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(parent, SWT.NONE);
		label.setText("Select Base Class:");
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.baseClassCombo = new Combo(parent, SWT.READ_ONLY | SWT.DROP_DOWN);// new
		// Text(composite,
		// SWT.BORDER);
		this.baseClassCombo.setSize(200, 20);
		this.baseClassCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 200;
		this.baseClassCombo.setEnabled(true);

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (!fastCodeCache.getTypeSet().isEmpty()) {
			for (final IType type : fastCodeCache.getTypeSet()) {
				this.baseClassCombo.add(type.getFullyQualifiedName());
			}
		}
		final GridData gridDataButton = new GridData();

		this.baseClassBrowseButton = new Button(parent, SWT.PUSH);
		this.baseClassBrowseButton.setText("Browse");
		this.baseClassBrowseButton.setLayoutData(gridDataButton);
		this.baseClassBrowseButton.setEnabled(true);

		this.baseClassBrowseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final SelectionDialog selectionDialog;
				try {
					selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
							SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_CLASSES, false, EMPTY_STR);
					selectionDialog.setTitle("Select Base Class");
					selectionDialog.setMessage("Select the Base class");

					if (selectionDialog.open() != CANCEL) {
						final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
						CreateClassDialog.this.baseClassCombo.add(((IType) selectionDialog.getResult()[0]).getFullyQualifiedName());
						CreateClassDialog.this.baseClassCombo.select(CreateClassDialog.this.baseClassCombo.getItemCount() - 1);
						CreateClassDialog.this.createVariableData.setiSelectBaseClassType((IType) selectionDialog.getResult()[0]);
						if (!fastCodeCache.getTypeSet().contains(selectionDialog.getResult()[0])) {
							fastCodeCache.getTypeSet().add((IType) selectionDialog.getResult()[0]);
						}
					}

				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		createInteraceList(parent);
	}

	/**
	 * @param parent
	 */
	private void createInteraceList(final Composite parent) {
		/*final Composite composite = new Composite(parent, parent.getStyle());

		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);*/

		final GridData lableGrid = new GridData();
		final Label label = new Label(parent, SWT.NONE);
		label.setText("Select Interface:");
		label.setLayoutData(lableGrid);

		final GridData listGrid = new GridData(SWT.LEFT, SWT.NONE, false, true);

		this.interfacesList = new org.eclipse.swt.widgets.List(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		listGrid.verticalSpan = 1;
		final int listHeight = this.interfacesList.getItemHeight() * 4;
		final Rectangle trim = this.interfacesList.computeTrim(0, 0, 200, listHeight);
		listGrid.heightHint = trim.height;
		listGrid.widthHint = trim.width;
		this.interfacesList.setLayoutData(listGrid);

		final GridData gridDataButton = new GridData();

		this.interfaceBrowseButton = new Button(parent, SWT.PUSH);
		this.interfaceBrowseButton.setText("Browse");
		this.interfaceBrowseButton.setLayoutData(gridDataButton);
		this.interfaceBrowseButton.setEnabled(true);

		this.interfaceBrowseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final SelectionDialog selectionDialog;
				try {
					selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
							SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_INTERFACES, false, "");
					selectionDialog.setTitle("Select interfaces to implement");
					selectionDialog.setMessage("Select the interfaces to implement");

					if (selectionDialog.open() == CANCEL) {
						return;
					}
					for (int i = 0; i < selectionDialog.getResult().length; i++) {
						CreateClassDialog.this.interfacesList.add(((IType) selectionDialog.getResult()[i]).getFullyQualifiedName());
						CreateClassDialog.this.createVariableData.getiInterfaceType().add((IType) selectionDialog.getResult()[i]);
					}

				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	private String getCommonSuffix() {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String commonSuffixes = globalSettings.getPropertyValue(COMMON_CLASS_SUFFIX.toUpperCase(), EMPTY_STR);
		IJavaElement[] classesInPackage;
		String suffix = null;
		try {
			classesInPackage = CreateClassDialog.this.createVariableData.getPackageFragment().getChildren();
			final String[] commonSuffixArr = commonSuffixes.split(",");

			for (int k = 0; k < commonSuffixArr.length; k++) {
				for (int i = 0; i < classesInPackage.length; i++) {
					final String tmpClsName = classesInPackage[i].getElementName().substring(0,
							classesInPackage[i].getElementName().indexOf("."));
					if (!tmpClsName.toLowerCase().endsWith(commonSuffixArr[k].toLowerCase().trim())) {
						suffix = EMPTY_STR;
						break;
					}
					suffix = commonSuffixArr[k].trim();
				}
				if (suffix != null && !suffix.equals(EMPTY_STR)) {
					break;
				}
			}

		} catch (final JavaModelException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		if (suffix != null & suffix.trim().length() > 0) {
			final String[] choices = { "Yes", "No" };

			final String choice = getChoiceFromMultipleValues(this.shell, "Warning", "All the classes in this package end with " + suffix
					+ ". Do you want to add the same suffix to the class", choices);
			if (choice == null || choice.equals("No")) {
				return EMPTY_STR;
			}
		}
		return suffix == null ? EMPTY_STR : suffix;
	}

}

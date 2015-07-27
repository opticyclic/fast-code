package org.fastcode.dialog;

import static org.eclipse.jdt.core.Signature.getSignatureSimpleName;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.EXTENSION_OTHER;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.METHOD_NAME_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_PATTERN_DEFAULT;
import static org.fastcode.common.FastCodeConstants.METHOD_RETURN_TYPE_VOID;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.util.FastCodeUtil.getEmptyListForNull;
import static org.fastcode.util.SourceUtil.getFQNameFromFieldTypeName;
import static org.fastcode.util.StringUtil.createEmbeddedInstance;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.parseType;
import static org.fastcode.util.StringUtil.replacePlaceHolder;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.CreateUnitTestData;
import org.fastcode.common.FastCodeColor;
import org.fastcode.common.FastCodeConstants.HANDLE_EXCEPTION;
import org.fastcode.common.FastCodeConstants.JUNIT_TYPE;
import org.fastcode.common.FastCodeConstants.UNIT_TEST_CHOICE;
import org.fastcode.common.FastCodeConstants.UNIT_TEST_TYPE;
import org.fastcode.common.FastCodeMethod;
import org.fastcode.common.FastCodeMethodRegistry;
import org.fastcode.common.FastCodeType;
import org.fastcode.preferences.JunitPreferences;
import org.fastcode.util.JUnitCreator;
import org.fastcode.util.JUnitUtil;
import org.fastcode.util.JunitPreferencesAndType;
import org.fastcode.util.SourceUtil;
import org.fastcode.util.UnitTestReturnFormatOption;
import org.fastcode.util.UnitTestReturnFormatSettings;
import org.fastcode.util.UnitTestReturnFormatUtil;

public class CreateUnitTestDialog extends TrayDialog {

	Shell									shell;
	IPreferenceStore						preferenceStore;
	private CreateUnitTestData				createUnitTestData;
	private Combo							junitTestProfileCombo;
	private Text							testMethodName;
	private Button							regularTestButton;
	private Button							exceptionTestButton;
	private Button							throwExceptionButton;
	private Button							consumeExceptionButton;
	private org.eclipse.swt.widgets.List	unitTestResultFormatList;
	private org.eclipse.swt.widgets.List	methodSelectionList;
	UnitTestReturnFormatSettings			unitTestReturnFormatSettings	= UnitTestReturnFormatSettings.getInstance();
	private Button							additionalTestButton;
	private Button							jumpToTestButton;
	private boolean							methodThrowsException;
	private String							errorMessage;
	private Text							errorMessageText;
	private final String					defaultMessage					= NEWLINE;
	private final String					multipleSelectionMessage		= "Options,'Handle Exception','Return Format' and 'Test Method Name' can be used only when you choose single method.";
	private final String					testMethodExistMessage			= "Test Exists for the method selected.";
	private final String					createMethodBodyMessage			= "Create Method body is not enabled in preference.";
	private Button							stubMethod1, stubMethod2, stubMethod3, stubMethod4;
	private Text							additionalMethodName;
	private IMethod							methodSelected;
	StringBuilder							tstMethName;
	private Button							browse;
	private Text							excepClassText;
	private boolean							enableExcptClass				= false;
	protected String						notRunTimeExcpSubClass			= "The class chosen is not a Sub class of RuntimeException.";
	private String							addtnlTestMethodName			= "";
	private Button							junit3Button;
	private Button							junit4Button;
	private Button							testNGButton;
	private Combo							dataProviderCombo;
	private org.eclipse.swt.widgets.List	dependsOnMethodsList;
	private Text							warningMessageText;

	/**
	 * @param shell
	 */
	public CreateUnitTestDialog(final Shell shell) {
		super(shell);
		this.shell = shell;
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
	}

	/**
	 * @param shell
	 * @param createUnitTestData
	 */
	public CreateUnitTestDialog(final Shell shell, final CreateUnitTestData createUnitTestData) {
		super(shell);
		this.shell = shell;
		this.createUnitTestData = createUnitTestData;
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
	}

	@Override
	public void create() {
		super.create();
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Unit Test Details");
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);
		createErrorMessageText(parent);
		createWarningMessageText(parent);

		createUnitTestType(parent);

		createJunitTestProfileCombo(parent);
		createUnitTestMethodList(parent);

		createStubMethodsPane(parent);

		createTestTypes(parent);
		createExceptionBrowse(parent);
		createExceptionTestRslt(parent);

		createChoicePane(parent);

		//createDataProviderCombo(parent);
		//createDependsOnMethodsList(parent);

		createUnitTestResultFormatPane(parent);

		if (this.createUnitTestData.getUnitTestProfiles() != null && this.createUnitTestData.getUnitTestProfiles().size() == 1) {
			this.junitTestProfileCombo.select(0);
			this.createUnitTestData.setJunitTestProfileName(this.junitTestProfileCombo.getItem(this.junitTestProfileCombo
					.getSelectionIndex()));
			try {
				checkIfTestExists(parent);
			} catch (final Exception ex) {
				//setErrorMessage(ex.getMessage(), false);
				this.shell.close();
				MessageDialog.openError(new Shell(), "Some error occured, check the preference and try again.", ex.getMessage());
				ex.printStackTrace();
				/*try {
					throw ex;
				} catch (final Exception ex1) {
					ex1.printStackTrace();
				}*/
			}
		}
		if (this.createUnitTestData.isInvokedFromMethod()) {
			if (this.methodSelectionList.getItemCount() == 0) {
				this.methodSelectionList.add(this.createUnitTestData.getClassMethodsList().get(0).getElementName());
				this.methodSelectionList.select(0);
			}
		}

		this.junitTestProfileCombo.setFocus();
		return parent;
	}

	/**
	 * @param parent
	 */
	private void createDependsOnMethodsList(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());

		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);

		final GridData lableGrid = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Select Depends on Methods:    ");
		label.setLayoutData(lableGrid);

		final GridData listGrid = new GridData(SWT.LEFT, SWT.NONE, false, true);

		this.dependsOnMethodsList = new org.eclipse.swt.widgets.List(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		listGrid.verticalSpan = 4;
		final int listHeight = this.dependsOnMethodsList.getItemHeight() * 3;
		final Rectangle trim = this.dependsOnMethodsList.computeTrim(0, 0, 200, listHeight);
		listGrid.heightHint = trim.height;
		listGrid.widthHint = trim.width;
		this.dependsOnMethodsList.setLayoutData(listGrid);
		this.dependsOnMethodsList.setEnabled(false);

		this.dependsOnMethodsList.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				CreateUnitTestDialog.this.createUnitTestData.setSelectedDependsOnMethod(CreateUnitTestDialog.this.dependsOnMethodsList
						.getSelection());
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
	}

	/**
	 * @param parent
	 */
	private void createDataProviderCombo(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);

		final GridData LableGrid = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Select DataProvider:                   ");
		label.setLayoutData(LableGrid);

		final GridData ComboGrid = new GridData(200, 20);
		ComboGrid.grabExcessHorizontalSpace = true;
		this.dataProviderCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.dataProviderCombo.setLayoutData(ComboGrid);
		this.dataProviderCombo.setEnabled(false);
		this.dataProviderCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				CreateUnitTestDialog.this.createUnitTestData.setSelectedDataProvider(CreateUnitTestDialog.this.dataProviderCombo
						.getItem(CreateUnitTestDialog.this.dataProviderCombo.getSelectionIndex()));
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
	}

	/**
	 * @param parent
	 */
	private void createWarningMessageText(final Composite parent) {
		final GridData warnText = new GridData(650, 20);
		warnText.grabExcessHorizontalSpace = true;

		this.warningMessageText = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
		// this.errorMessageText.setBackground(new Color(null, 255, 255, 255));
		//this.warningMessageText.setForeground(new Color(null, 255, 165, 0));
		this.warningMessageText.setForeground(FastCodeColor.getWarningMsgColor());
		this.warningMessageText.setLayoutData(warnText);
		setWarningMessage(this.defaultMessage, false);
	}

	/**
	 * @param parent
	 */
	private void createUnitTestType(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Unit Test Type:                       ");
		label.setLayoutData(gridDataLabel);

		final GridData gridDataButton = new GridData();
		gridDataButton.grabExcessHorizontalSpace = true;

		this.junit3Button = new Button(composite, SWT.RADIO);
		this.junit3Button.setText("JUnit 3");
		this.junit3Button.setEnabled(false);

		this.junit4Button = new Button(composite, SWT.RADIO);
		this.junit4Button.setText("JUnit 4");
		this.junit4Button.setEnabled(false);

		this.testNGButton = new Button(composite, SWT.RADIO);
		this.testNGButton.setText("Test NG");
		this.testNGButton.setEnabled(false);
	}

	/**
	 * @param parent
	 */
	private void createExceptionBrowse(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Exception Class:                       ");
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.excepClassText = new Text(composite, SWT.BORDER | SWT.MULTI);
		this.excepClassText.setSize(200, 20);
		this.excepClassText.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 300;
		this.excepClassText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				if (CreateUnitTestDialog.this.excepClassText.getEnabled()) {
					if (isEmpty(CreateUnitTestDialog.this.excepClassText.getText())) {
						setErrorMessage("\nPlease choose Exception Class.", false);
					} else {
						setErrorMessage("\nPlease choose Exception Class.", true);
					}
				}
			}
		});

		final GridData gridDataButton = new GridData();

		this.browse = new Button(composite, SWT.PUSH);
		this.browse.setText("Browse");
		this.browse.setLayoutData(gridDataButton);
		this.browse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {

				final SelectionDialog selectionDialog;
				try {
					selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
							SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, EMPTY_STR);
					selectionDialog.setTitle("Select Exception Class");
					selectionDialog.setMessage("Select the exception class");

					if (selectionDialog.open() == CANCEL) {
						return;
					}

					final IType expClass = (IType) selectionDialog.getResult()[0];
					if (!expClass.getElementName().equals(RuntimeException.class.getSimpleName())) {
						final boolean isChildOfClass = isTypeChildOfClass(expClass, RuntimeException.class.getSimpleName());
						if (!isChildOfClass) {
							setErrorMessage(CreateUnitTestDialog.this.notRunTimeExcpSubClass, false);
						} else {
							setErrorMessage(CreateUnitTestDialog.this.notRunTimeExcpSubClass, true);
						}
					}

					CreateUnitTestDialog.this.excepClassText.setText(expClass.getFullyQualifiedName());
					CreateUnitTestDialog.this.createUnitTestData.setExcepClassName(expClass.getElementName());
					if (!expClass.getPackageFragment().getElementName().equals("java.lang")) {
						CreateUnitTestDialog.this.createUnitTestData.setExceptnIType(expClass);
					}

				} catch (final JavaModelException ex) {
					ex.printStackTrace();
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

		});

		this.excepClassText.setEnabled(false);
		this.browse.setEnabled(false);

	}

	/**
	 * @param expClass
	 * @param baseClassName
	 * @return
	 * @throws JavaModelException
	 */
	protected boolean isTypeChildOfClass(final IType expClass, final String baseClassName) throws JavaModelException {
		final ITypeHierarchy hierarchy = expClass.newSupertypeHierarchy(null);
		final String superClass = expClass.getSuperclassName();

		if (superClass == null) {
			return false;
		}

		for (final IType classType : hierarchy.getAllSuperclasses(expClass)) {
			if (classType.getElementName().equals(baseClassName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param parent
	 */
	private void createErrorMessageText(final Composite parent) {

		final GridData errText = new GridData(770, 40);
		errText.grabExcessHorizontalSpace = true;

		this.errorMessageText = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
		// this.errorMessageText.setBackground(new Color(null, 255, 255, 255));
		//this.errorMessageText.setForeground(new Color(null, 255, 0, 0));
		this.errorMessageText.setForeground(FastCodeColor.getErrorMsgColor());
		this.errorMessageText.setLayoutData(errText);
		setErrorMessage(this.defaultMessage, false);

	}

	@Override
	protected Control createButtonBar(final Composite parent) {
		return super.createButtonBar(parent);
	}

	@Override
	protected void okPressed() {
		if (this.createUnitTestData == null) {
			this.createUnitTestData = new CreateUnitTestData();
		}
		if (this.junitTestProfileCombo != null) {
			this.createUnitTestData.setJunitTestProfileName(this.junitTestProfileCombo.getItem(this.junitTestProfileCombo
					.getSelectionIndex()));
		}

		if (this.regularTestButton.getSelection()) {
			this.createUnitTestData.setUnitTestType(UNIT_TEST_TYPE.REGULAR_TEST);
		} else if (this.exceptionTestButton.getSelection()) {
			this.createUnitTestData.setUnitTestType(UNIT_TEST_TYPE.EXCEPTION_TEST);
		}

		if (this.methodThrowsException) {
			if (this.throwExceptionButton.getSelection()) {
				setErrorMessage(this.defaultMessage, false);
				this.createUnitTestData.setHandleException(HANDLE_EXCEPTION.THROW);
			} else if (this.consumeExceptionButton.getSelection()) {
				setErrorMessage(this.defaultMessage, false);
				this.createUnitTestData.setHandleException(HANDLE_EXCEPTION.CONSUME);
			} else {
				setErrorMessage("The method chosen throws Exception. Please select one way to handle the Exception.", false);
				return;
			}
		}
		if (this.unitTestResultFormatList != null) {
			this.createUnitTestData.setUnitTestRsltFormatSelected(this.unitTestResultFormatList.getSelection());
		}
		if (this.additionalTestButton != null && this.additionalTestButton.getSelection()) {
			this.createUnitTestData.setUnitTestChoice(UNIT_TEST_CHOICE.CREATEADDITIONALTEST);
			this.createUnitTestData.setAddnlTestMethodName(this.additionalMethodName.getText());
		} else if (this.jumpToTestButton != null && this.jumpToTestButton.getSelection()) {
			this.createUnitTestData.setUnitTestChoice(UNIT_TEST_CHOICE.JUMPTOTEST);
		}

		super.okPressed();
	}

	/**
	 * @param parent
	 */
	private void createJunitTestProfileCombo(final Composite parent) {
		final Composite composit = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 6;
		composit.setLayout(layout);
		final Label label = new Label(composit, SWT.NONE);
		label.setText("Junit Test Profile:                    ");

		this.junitTestProfileCombo = new Combo(composit, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		final GridData gridCombo = new GridData(216, 15);
		gridCombo.horizontalSpan = 2;
		gridCombo.horizontalAlignment = SWT.FILL;
		gridCombo.grabExcessHorizontalSpace = true;
		gridCombo.verticalAlignment = SWT.FILL;
		gridCombo.grabExcessVerticalSpace = true;
		this.junitTestProfileCombo.setLayoutData(gridCombo);

		if (this.createUnitTestData.getUnitTestProfiles() != null) {
			for (final String profileName : this.createUnitTestData.getUnitTestProfiles()) {
				if (!isEmpty(profileName)) {
					this.junitTestProfileCombo.add(profileName.trim());
				}
			}
		}

		this.junitTestProfileCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				CreateUnitTestDialog.this.createUnitTestData.setJunitTestProfileName(CreateUnitTestDialog.this.junitTestProfileCombo
						.getItem(CreateUnitTestDialog.this.junitTestProfileCombo.getSelectionIndex()));
				try {
					CreateUnitTestDialog.this.checkIfTestExists(parent);
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
	}

	/**
	 * @param parent
	 */
	private void createUnitTestMethodList(final Composite parent) {

		final Composite composite = new Composite(parent, parent.getStyle());

		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);

		final GridData lableGrid = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Select Unit Test Method:         ");
		label.setLayoutData(lableGrid);

		final GridData listGrid = new GridData(SWT.LEFT, SWT.NONE, false, true);

		this.methodSelectionList = new org.eclipse.swt.widgets.List(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		listGrid.verticalSpan = 4;
		final int listHeight = this.methodSelectionList.getItemHeight() * 4;
		final Rectangle trim = this.methodSelectionList.computeTrim(0, 0, 200, listHeight);
		listGrid.heightHint = trim.height;
		listGrid.widthHint = trim.width;
		this.methodSelectionList.setLayoutData(listGrid);

		/*for(final IMethod iMethod : this.createUnitTestData.getClassMethodsToShow().toArray(new IMethod[0])){
			this.methodSelectionList.add(iMethod.getElementName());
		}*/

		this.methodSelectionList.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				//final String[] selectedMethods = CreateUnitTestDialog.this.methodSelectionList.getSelection();
				final int[] selectedIndex = CreateUnitTestDialog.this.methodSelectionList.getSelectionIndices();
				CreateUnitTestDialog.this.processMethodSelected(selectedIndex);

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}
		});

		final GridData lableGrid2 = new GridData();
		final Label label2 = new Label(composite, SWT.NONE);
		label2.setText("   Test Method Name: ");
		label2.setLayoutData(lableGrid2);

		final GridData textGrid = new GridData(200, 15);
		this.testMethodName = new Text(composite, SWT.BORDER);
		this.testMethodName.setLayoutData(textGrid);

		this.testMethodName.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				CreateUnitTestDialog.this.createUnitTestData.setTestMethodName(CreateUnitTestDialog.this.testMethodName.getText());
			}

			@Override
			public void focusGained(final FocusEvent e) {

			}
		});

	}

	/**
	 * @param selectedIndex
	 */
	protected void processMethodSelected(final int[] selectedIndex) {
		if (CreateUnitTestDialog.this.createUnitTestData.getClassMethodsSelected() != null) {
			CreateUnitTestDialog.this.createUnitTestData.getClassMethodsSelected().clear();
		}
		System.out.println(selectedIndex.length);
		CreateUnitTestDialog.this.disableControls(selectedIndex.length == 1);
		if (CreateUnitTestDialog.this.unitTestResultFormatList != null) {
			CreateUnitTestDialog.this.unitTestResultFormatList.removeAll();
		}

		if (selectedIndex.length > 1) {
			CreateUnitTestDialog.this.setWarningMessage(CreateUnitTestDialog.this.multipleSelectionMessage, false);
			CreateUnitTestDialog.this.testMethodName.setText(EMPTY_STR);
		} else {
			try {
				this.methodSelected = CreateUnitTestDialog.this.createUnitTestData.getClassMethodsToShow().get(
						CreateUnitTestDialog.this.methodSelectionList.getSelectionIndex());

				CreateUnitTestDialog.this.createUnitTestData.setMethodReturnType(getSignatureSimpleName(this.methodSelected.getReturnType()
						.trim()));
				CreateUnitTestDialog.this.doesMethodThorowException(this.methodSelected);
				CreateUnitTestDialog.this.populateUnitTestReturnFormat();
				final String methodPattern = this.createUnitTestData.getJunitPreferencesAndType().getJunitPreferences()
						.getJunitTestMethod();

				final String testMethodName = replacePlaceHolder(methodPattern, METHOD_NAME_STR,
						methodPattern.startsWith(METHOD_PATTERN_DEFAULT) ? CreateUnitTestDialog.this.methodSelected.getElementName()
								: createEmbeddedInstance(CreateUnitTestDialog.this.methodSelected.getElementName()));

				this.testMethodName.setText(testMethodName);
				this.createUnitTestData.setTestMethodName(testMethodName);
			} catch (final JavaModelException ex) {
				ex.printStackTrace();
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			CreateUnitTestDialog.this.setWarningMessage(CreateUnitTestDialog.this.multipleSelectionMessage, true);
		}

		CreateUnitTestDialog.this.additionalTestButton.setEnabled(false);
		CreateUnitTestDialog.this.jumpToTestButton.setEnabled(false);
		for (final int index : selectedIndex) {
			CreateUnitTestDialog.this.createUnitTestData.getClassMethodsSelected().add(
					CreateUnitTestDialog.this.createUnitTestData.getClassMethodsToShow().get(index));

		}

		try {
			CreateUnitTestDialog.this.validateMethod(CreateUnitTestDialog.this.createUnitTestData.getClassMethodsSelected());
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		if (this.createUnitTestData.isTestClssExists() && selectedIndex.length == 1 && this.testNGButton.getSelection()) {
			populateDataProviderComboAndDependsOnMethodList(this.createUnitTestData.getExistingTestIMethods());
		}

	}

	/**
	 * @param iMethod
	 */
	protected void doesMethodThorowException(final IMethod iMethod) {
		try {
			this.methodThrowsException = iMethod.getExceptionTypes() != null && iMethod.getExceptionTypes().length > 0;
			this.throwExceptionButton.setEnabled(this.methodThrowsException);
			this.consumeExceptionButton.setEnabled(this.methodThrowsException);

		} catch (final JavaModelException ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * @param disable
	 */
	protected void disableControls(final boolean disable) {
		this.unitTestResultFormatList.setEnabled(disable);
		this.throwExceptionButton.setEnabled(disable);
		this.consumeExceptionButton.setEnabled(disable);
		this.throwExceptionButton.setSelection(false);
		this.consumeExceptionButton.setSelection(false);
		this.unitTestResultFormatList.deselectAll();
		this.testMethodName.setEnabled(disable);

	}

	/**
	 * @throws Exception
	 */
	private void checkIfTestExists(final Composite parent) throws Exception {
		final JunitPreferencesAndType junitPreferencesAndType = JUnitUtil.findJunitPreferences(this.createUnitTestData.getFromType(),
				this.createUnitTestData.getJunitTestProfileName());
		if (junitPreferencesAndType == null) {
			/*
			 * throw new
			 * Exception("This class you selected does not match any profile. "
			 * +
			 * "Please configure it first by going to Window -> Preference -> Fast Code Pereference."
			 * );
			 */
			setErrorMessage("This class you selected does not match any profile. "
					+ "Please configure it first by going to Window -> Preference -> Fast Code Pereference.", false);
		}
		this.createUnitTestData.setJunitPreferencesAndType(junitPreferencesAndType);
		JunitPreferences junitPreferences = junitPreferencesAndType.getJunitPreferences();
		IType typeToWorkOn = junitPreferencesAndType.getType();
		this.createUnitTestData.setTypeToWorkOn(typeToWorkOn);

		if (junitPreferences.getJunitType().equals(JUNIT_TYPE.JUNIT_TYPE_3)) {
			this.junit3Button.setSelection(true);
		} else if (junitPreferences.getJunitType().equals(JUNIT_TYPE.JUNIT_TYPE_4)) {
			this.junit4Button.setSelection(true);
		} else if (junitPreferences.getJunitType().equals(JUNIT_TYPE.JUNIT_TYPE_TESTNG)) {
			this.testNGButton.setSelection(true);
		}

		ICompilationUnit unitTestCU = JUnitUtil.findTestUnit(typeToWorkOn, this.createUnitTestData.getJunitPreferencesAndType()
				.getJunitPreferences());
		// find the interface if unit test is null
		if (unitTestCU == null && typeToWorkOn.isClass()) {
			final IType tmpType = SourceUtil.findSuperInterfaceType(typeToWorkOn);
			if (tmpType != null) {
				final JunitPreferences junitPref = JunitPreferences.getInstance(tmpType);
				if (junitPref != null) {
					unitTestCU = JUnitUtil.findTestUnit(tmpType, junitPref);
					if (unitTestCU != null) {
						junitPreferences = junitPref; //set it back to junitPreferencesAndType in UT data
						typeToWorkOn = tmpType;
						this.createUnitTestData.setTypeToWorkOn(typeToWorkOn);
						this.createUnitTestData.setJunitPreferencesAndType(new JunitPreferencesAndType(junitPreferences, typeToWorkOn));
					}
				}
			}
		}

		final boolean testClassExst = unitTestCU != null && unitTestCU.exists();
		this.createUnitTestData.setTestClssExists(testClassExst);
		this.createUnitTestData.setUnitTestCU(unitTestCU);
		IMethod[] existingTestMethods = null;

		if (testClassExst) {
			if (!unitTestCU.getResource().isSynchronized(0)) {
				//throw new Exception(unitTestCU.getElementName() + " is not Synchronized, please refresh and try again.");
				setErrorMessage(unitTestCU.getElementName() + " is not Synchronized, please refresh and try again.", false);
			}

			if (this.createUnitTestData.getClassMethodsToShow() != null) {
				this.createUnitTestData.getClassMethodsToShow().clear();
			}
			if (this.methodSelectionList != null) {
				this.methodSelectionList.removeAll();
			}

			existingTestMethods = unitTestCU.findPrimaryType().getMethods();
			this.createUnitTestData.setExistingTestIMethods(existingTestMethods);
			for (final IMethod classMth : this.createUnitTestData.getClassMethodsList().toArray(new IMethod[0])) {
				if (this.createUnitTestData.isInvokedFromMethod()) {
					this.createUnitTestData.getClassMethodsToShow().add(classMth);
					break;
				}
				boolean found = false;
				for (final IMethod testMth : existingTestMethods) {
					if (classMth.getElementName().equals(testMth.getElementName())) {
						found = true;
						break;
					}
				}
				if (!found) {
					this.createUnitTestData.getClassMethodsToShow().add(classMth);

				}
			}
			if (junitPreferences.getJunitType().equals(JUNIT_TYPE.JUNIT_TYPE_TESTNG)) {
				createDataProviderCombo(parent);
				createDependsOnMethodsList(parent);
			}
			for (final IMethod iMethod : this.createUnitTestData.getClassMethodsToShow().toArray(new IMethod[0])) {
				this.methodSelectionList.add(iMethod.getElementName());
				if (this.createUnitTestData.isInvokedFromMethod()) {
					this.methodSelectionList.selectAll();
					processMethodSelected(this.methodSelectionList.getSelectionIndices());
				}
			}

		} else {
			for (final IMethod iMethod : this.createUnitTestData.getClassMethodsList().toArray(new IMethod[0])) {
				this.createUnitTestData.getClassMethodsToShow().add(iMethod);
				this.methodSelectionList.add(iMethod.getElementName());
				if (this.createUnitTestData.isInvokedFromMethod()) {
					this.methodSelectionList.selectAll();
					processMethodSelected(this.methodSelectionList.getSelectionIndices());
				}
			}
		}

		final FastCodeMethod[] stubMethods = FastCodeMethodRegistry.getRegisteredUnitTestStubMethods(junitPreferences.getJunitType());
		if (stubMethods != null && !testClassExst) {
			for (final FastCodeMethod fastCodeMethod : stubMethods) {
				this.createUnitTestData.getStubMethodsMap().put(fastCodeMethod.getName(), fastCodeMethod);
			}
		} else {
			if (this.createUnitTestData.getStubMethodsMap() != null) {
				this.createUnitTestData.getStubMethodsMap().clear();
			}

			for (final FastCodeMethod fastCodeMethod : stubMethods) {
				boolean found = false;
				for (final IMethod method : existingTestMethods) {
					if (fastCodeMethod.getName().equals(method.getElementName())) {
						found = true;
						break;
					}
				}
				if (!found) {
					this.createUnitTestData.getStubMethodsMap().put(fastCodeMethod.getName(), fastCodeMethod);
				}
			}
		}
		if (this.createUnitTestData.getStubMethodsMap() != null && this.createUnitTestData.getStubMethodsMap().size() > 0) {
			for (final Entry<String, FastCodeMethod> entry : this.createUnitTestData.getStubMethodsMap().entrySet()) {
				if (entry.getKey().equalsIgnoreCase("setUpBeforeClass")) {
					this.stubMethod1.setEnabled(true);
				} else if (entry.getKey().equalsIgnoreCase("tearDownAfterClass")) {
					this.stubMethod2.setEnabled(true);
				} else if (entry.getKey().equalsIgnoreCase("setUp")) {
					this.stubMethod3.setEnabled(true);
				} else if (entry.getKey().equalsIgnoreCase("tearDown")) {
					this.stubMethod4.setEnabled(true);
				}
			}
		}

	}

	/**
	 * @param existingTestMethods
	 */
	private void populateDataProviderComboAndDependsOnMethodList(final IMethod[] existingTestMethods) {
		try {

			if (existingTestMethods != null && existingTestMethods.length > 0) {
				for (final IMethod method : existingTestMethods) {
					if (method.getAnnotations() != null && method.getAnnotations().length > 0) {
						for (final IAnnotation methodAnnotation : method.getAnnotations()) {
							if (methodAnnotation.getElementName().equals("Test")) {
								this.dependsOnMethodsList.add(method.getElementName());
							}
							if (methodAnnotation.getElementName().equals("DataProvider")) {
								for (final IMemberValuePair memValPair : methodAnnotation.getMemberValuePairs()) {
									if (memValPair.getMemberName().equals("name")) {
										this.dataProviderCombo.add((String) memValPair.getValue());
									}
								}
							}
						}
					}
				}

			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		if (this.dependsOnMethodsList.getItemCount() > 0) {
			this.dependsOnMethodsList.setEnabled(true);
		} else {
			this.dependsOnMethodsList.setEnabled(false);
		}
		if (this.dataProviderCombo.getItemCount() > 0) {
			this.dataProviderCombo.setEnabled(true);
		} else {
			this.dataProviderCombo.setEnabled(false);
		}
	}

	/**
	 * @param testMethodsSelected
	 * @throws Exception
	 */
	private void validateMethod(final List<IMethod> testMethodsSelected) throws Exception {

		final IType typeToWorkOn = this.createUnitTestData.getTypeToWorkOn();
		for (final IMethod method : this.createUnitTestData.getClassMethodsSelected().toArray(new IMethod[0])) {

			IMethod methodToWorkOn = method;
			if (!this.createUnitTestData.getFromType().equals(typeToWorkOn)) {
				methodToWorkOn = this.createUnitTestData.getTypeToWorkOn().getMethod(method.getElementName(), method.getParameterTypes());
				if (methodToWorkOn == null || !methodToWorkOn.exists()) {
					MessageDialog.openError(new Shell(), "Error", "Method " + method.getElementName() + " does not exist in "
							+ typeToWorkOn.getElementName());
					continue;
				}
			}

			final IMethod[] testMethods = this.createUnitTestData.isTestClssExists() ? JUnitCreator.findTestMethods(typeToWorkOn,
					methodToWorkOn, this.createUnitTestData.getJunitPreferencesAndType().getJunitPreferences()) : null;
			//				boolean testMethodExist = false, createAotherTestMethod = false;
			//				testMethodExist = createAotherTestMethod = false;

			if (this.createUnitTestData.getClassMethodsSelected().size() == 1) {
				final IMethod methodChosen = this.createUnitTestData.getClassMethodsSelected().get(0);

				if (testMethods != null && testMethods.length > 0) {

					//this.showChoicePane = true;
					this.exceptionTestButton.setSelection(true);
					/*this.consumeExceptionButton.setEnabled(false);
					this.consumeExceptionButton.setSelection(false);*/
					this.regularTestButton.setSelection(false);
					CreateUnitTestDialog.this.additionalTestButton.setEnabled(true);
					CreateUnitTestDialog.this.jumpToTestButton.setEnabled(true);
					this.additionalMethodName.setEnabled(true);
					this.tstMethName = new StringBuilder(method.getElementName() + EXTENSION_OTHER);
					int i = 1;
					while (true) {
						final IMethod additnlTestMethod = this.createUnitTestData.getUnitTestCU().findPrimaryType()
								.getMethod(this.tstMethName.toString(), null);
						if (additnlTestMethod == null || !additnlTestMethod.exists()) {
							this.additionalMethodName.setText(this.tstMethName.toString());
							break;
						}
						this.tstMethName.append(i++);
					}
					setWarningMessage(this.testMethodExistMessage, false);
					enableExcptClass(methodChosen, this.exceptionTestButton.getSelection());
				} else {
					this.exceptionTestButton.setSelection(false);
					/*this.consumeExceptionButton.setEnabled(true);
					this.consumeExceptionButton.setSelection(false);*/
					this.regularTestButton.setSelection(true);
					enableExcptClass(methodChosen, this.exceptionTestButton.getSelection());
				}
			}
		}
	}

	/**
	 * @param methodChosen
	 * @param excptTestSelected
	 */
	private void enableExcptClass(final IMethod methodChosen, final boolean excptTestSelected) {
		try {
			if (methodChosen.getExceptionTypes() == null || methodChosen.getExceptionTypes().length == 0) {
				if (excptTestSelected) {
					this.enableExcptClass = true;
					this.excepClassText.setEnabled(true);
					this.browse.setEnabled(true);
					this.excepClassText.setText("java.lang.RuntimeException");
				} else {
					this.enableExcptClass = false;
					this.excepClassText.setEnabled(false);
					this.excepClassText.setText(EMPTY_STR);
					this.browse.setEnabled(false);
				}
			} else {
				this.enableExcptClass = false;
				this.excepClassText.setEnabled(false);
				this.browse.setEnabled(false);
				this.excepClassText.setText(getSignatureSimpleName(methodChosen.getExceptionTypes()[0]));
				final IType expIType = methodChosen.getCompilationUnit().getJavaProject()
						.findType(getFQNameFromFieldTypeName(methodChosen.getExceptionTypes()[0], methodChosen.getCompilationUnit()));
				if (expIType != null && !expIType.getPackageFragment().getElementName().equals("java.lang")) {
					CreateUnitTestDialog.this.createUnitTestData.setExceptnIType(expIType);
				}
				if (excptTestSelected) {
					this.consumeExceptionButton.setEnabled(false);
					this.consumeExceptionButton.setSelection(false);
				} else {
					this.consumeExceptionButton.setEnabled(true);
					this.consumeExceptionButton.setSelection(false);
				}

			}
		} catch (final JavaModelException ex) {
			ex.printStackTrace();
		} catch (final Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * @param parent
	 */
	private void createTestTypes(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Test Types:                              ");
		this.regularTestButton = new Button(composite, SWT.RADIO);
		this.regularTestButton.setText("Regular Test");
		this.regularTestButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

		this.exceptionTestButton = new Button(composite, SWT.RADIO);
		this.exceptionTestButton.setText("Exception Test");
		this.exceptionTestButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (CreateUnitTestDialog.this.createUnitTestData.getClassMethodsSelected().size() == 1) {
					final IMethod methodChosen = CreateUnitTestDialog.this.createUnitTestData.getClassMethodsSelected().get(0);
					enableExcptClass(methodChosen, CreateUnitTestDialog.this.exceptionTestButton.getSelection());
				}

				/*CreateUnitTestDialog.this.consumeExceptionButton.setEnabled(!CreateUnitTestDialog.this.exceptionTestButton.getSelection());
				CreateUnitTestDialog.this.consumeExceptionButton.setSelection(false);*/
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

	}

	/**
	 * @param parent
	 */
	private void createExceptionTestRslt(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Handle Exception:                    ");
		this.throwExceptionButton = new Button(composite, SWT.RADIO);
		this.throwExceptionButton.setText("Throw Exception");
		//this.throwExceptionButton.setSelection(true);
		this.throwExceptionButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (CreateUnitTestDialog.this.throwExceptionButton.getSelection()) {
					//CreateUnitTestDialog.this.setErrorMessage(CreateUnitTestDialog.this.defaultMessage, false);
					setErrorMessage("The method chosen throws Exception. Please select one way to handle the Exception.", true);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

		this.consumeExceptionButton = new Button(composite, SWT.RADIO);
		this.consumeExceptionButton.setText("Consume Exception");
		this.consumeExceptionButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (CreateUnitTestDialog.this.consumeExceptionButton.getSelection()) {
					//CreateUnitTestDialog.this.setErrorMessage(CreateUnitTestDialog.this.defaultMessage,false);
					setErrorMessage("The method chosen throws Exception. Please select one way to handle the Exception.", true);
				} else {

				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
	}

	/**
	 * @param parent
	 */
	private void createChoicePane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		composite.setLayout(layout);
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Would You Like To:                   ");
		this.additionalTestButton = new Button(composite, SWT.RADIO);
		this.additionalTestButton.setText("Create an additional test");
		this.additionalTestButton.setSelection(true);
		this.additionalTestButton.setEnabled(false);
		this.additionalTestButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (CreateUnitTestDialog.this.additionalTestButton.getSelection()) {
					CreateUnitTestDialog.this.tstMethName = new StringBuilder(CreateUnitTestDialog.this.methodSelected.getElementName()
							+ EXTENSION_OTHER);
					int i = 1;
					while (true) {
						final IMethod testMethod = CreateUnitTestDialog.this.createUnitTestData.getUnitTestCU().findPrimaryType()
								.getMethod(CreateUnitTestDialog.this.tstMethName.toString(), null);
						if (testMethod == null || !testMethod.exists()) {
							CreateUnitTestDialog.this.additionalMethodName.setText(CreateUnitTestDialog.this.tstMethName.toString());
							break;
						}
						CreateUnitTestDialog.this.tstMethName.append(i++);
					}
					//CreateUnitTestDialog.this.additionalMethodName.setText(replacePlaceHolder(CreateUnitTestDialog.this.createUnitTestData.getJunitPreferencesAndType().getJunitPreferences().getJunitTestMethod(), "method_name", CreateUnitTestDialog.this.methodSelected.getElementName()));
					CreateUnitTestDialog.this.createUnitTestData.setAddnlTestMethodName(CreateUnitTestDialog.this.additionalMethodName
							.getText());
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
		this.jumpToTestButton = new Button(composite, SWT.RADIO);
		this.jumpToTestButton.setText("Jump To The Test");
		this.jumpToTestButton.setEnabled(false);
		this.jumpToTestButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

				/*if (methods.length == 1) {
					if (testMethods.length == 1) {
						return testMethods[0];
					}
					final MethodSelectionDialog methodSelectionDialog = new MethodSelectionDialog(new Shell(),
							"Select Test Method",
							"Multiple tests found for the method you selected, " +
									"please select one from the list below.",
									testMethods, false);
					methodSelectionDialog.open();
					return (methodSelectionDialog.getResult() == null) || (methodSelectionDialog.getResult().length == 0) ? null :
						(IMethod)methodSelectionDialog.getFirstResult();
				}*/
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

		final Label addTstMthNameLabel = new Label(composite, SWT.NONE);
		addTstMthNameLabel.setText("      Additional Test \n      Method Name:");

		this.additionalMethodName = new Text(composite, SWT.BORDER);
		this.additionalMethodName.setLayoutData(new GridData(200, 15));
		this.additionalMethodName.setEnabled(false);

		this.additionalMethodName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				CreateUnitTestDialog.this.tstMethName = new StringBuilder(CreateUnitTestDialog.this.additionalMethodName.getText());
				final IMethod testMethod = CreateUnitTestDialog.this.createUnitTestData.getUnitTestCU().findPrimaryType()
						.getMethod(CreateUnitTestDialog.this.tstMethName.toString(), null);
				if (testMethod == null || !testMethod.exists()) {
					CreateUnitTestDialog.this.setErrorMessage("A additional test method with name "
							+ CreateUnitTestDialog.this.addtnlTestMethodName + " alreay exists. Please use some other name.", true);
				} else {
					CreateUnitTestDialog.this.addtnlTestMethodName = CreateUnitTestDialog.this.additionalMethodName.getText();
					CreateUnitTestDialog.this.setErrorMessage("A additional test method with name "
							+ CreateUnitTestDialog.this.addtnlTestMethodName + " alreay exists. Please use some other name.", false);
				}
			}
		});
		this.additionalMethodName.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				/*				CreateUnitTestDialog.this.tstMethName = new StringBuilder(CreateUnitTestDialog.this.additionalMethodName.getText());
								final IMethod testMethod = CreateUnitTestDialog.this.createUnitTestData.getUnitTestCU().findPrimaryType().getMethod(CreateUnitTestDialog.this.tstMethName.toString(), null);
								if ((testMethod == null) || !testMethod.exists()) {
									CreateUnitTestDialog.this.setErrorMessage("A test method with name " + CreateUnitTestDialog.this.additionalMethodName.getText() + " alreay exists. Please use some other name.");
								} else {
									CreateUnitTestDialog.this.setErrorMessage(CreateUnitTestDialog.this.defaultMessage);
								}
				*/
			}

			@Override
			public void focusGained(final FocusEvent e) {

			}
		});

	}

	/**
	 * @param parent
	 */
	private void createStubMethodsPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());

		final GridLayout layout = new GridLayout();
		layout.numColumns = 5;
		composite.setLayout(layout);

		final GridData lableGrid = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Stub Methods:                          ");
		label.setLayoutData(lableGrid);

		final GridData checkBoxGrid = new GridData();
		checkBoxGrid.grabExcessHorizontalSpace = true;
		checkBoxGrid.widthHint = 130;
		checkBoxGrid.horizontalAlignment = SWT.RIGHT;

		/*final Label emptyLabel = new Label(composite, SWT.NONE);
		emptyLabel.setText("          ");
		emptyLabel.setLayoutData(checkBoxGrid);
		*/
		/*emptyLabel = new Label(composite, SWT.NONE);
		emptyLabel.setText("           ");
		emptyLabel.setLayoutData(checkBoxGrid);
		*/
		this.stubMethod1 = new Button(composite, SWT.CHECK);
		this.stubMethod1.setLayoutData(checkBoxGrid);
		this.stubMethod1.setEnabled(false);
		this.stubMethod1.setText("setUpBeforeClass");

		this.stubMethod1.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (CreateUnitTestDialog.this.stubMethod1.getSelection()) {
					CreateUnitTestDialog.this.createUnitTestData.getSelectedStubMethodsList().add(
							CreateUnitTestDialog.this.createUnitTestData.getStubMethodsMap().get(
									CreateUnitTestDialog.this.stubMethod1.getText()));
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
		});

		this.stubMethod2 = new Button(composite, SWT.CHECK);
		this.stubMethod2.setLayoutData(checkBoxGrid);
		this.stubMethod2.setEnabled(false);
		this.stubMethod2.setText("tearDownAfterClass");
		this.stubMethod2.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (CreateUnitTestDialog.this.stubMethod2.getSelection()) {
					CreateUnitTestDialog.this.createUnitTestData.getSelectedStubMethodsList().add(
							CreateUnitTestDialog.this.createUnitTestData.getStubMethodsMap().get(
									CreateUnitTestDialog.this.stubMethod2.getText()));
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}
		});

		this.stubMethod3 = new Button(composite, SWT.CHECK);
		this.stubMethod3.setLayoutData(checkBoxGrid);
		this.stubMethod3.setEnabled(false);
		this.stubMethod3.setText("setUp");
		this.stubMethod3.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (CreateUnitTestDialog.this.stubMethod3.getSelection()) {
					CreateUnitTestDialog.this.createUnitTestData.getSelectedStubMethodsList().add(
							CreateUnitTestDialog.this.createUnitTestData.getStubMethodsMap().get(
									CreateUnitTestDialog.this.stubMethod3.getText()));
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}
		});

		this.stubMethod4 = new Button(composite, SWT.CHECK);
		this.stubMethod4.setLayoutData(checkBoxGrid);
		this.stubMethod4.setEnabled(false);
		this.stubMethod4.setText("tearDown");
		this.stubMethod4.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (CreateUnitTestDialog.this.stubMethod4.getSelection()) {
					CreateUnitTestDialog.this.createUnitTestData.getSelectedStubMethodsList().add(
							CreateUnitTestDialog.this.createUnitTestData.getStubMethodsMap().get(
									CreateUnitTestDialog.this.stubMethod4.getText()));
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}
		});
		/*		final GridData listGrid = new GridData(SWT.FILL, SWT.FILL, true, true);
				this.stubMethodSelectionList = new org.eclipse.swt.widgets.List(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
				listGrid.verticalSpan = 4;
				final int listHeight = this.stubMethodSelectionList.getItemHeight() * 4;
				final Rectangle trim = this.stubMethodSelectionList.computeTrim(0, 0, 0, listHeight);
				listGrid.heightHint = trim.height;
				this.stubMethodSelectionList.setLayoutData(listGrid);
		*/
		/*for(final FastCodeMethod fcMethod : this.createUnitTestData.getStubMethodsList().toArray(new FastCodeMethod[0])){
			System.out.println(fcMethod.getName());
			this.stubMethod1.setEnabled(fcMethod.getName().equalsIgnoreCase("setUpBeforeClass"));
			this.stubMethod2.setEnabled(fcMethod.getName().equalsIgnoreCase("tearDownAfterClass"));
			this.stubMethod3.setEnabled(fcMethod.getName().equalsIgnoreCase("setUp"));
			this.stubMethod4.setEnabled(fcMethod.getName().equalsIgnoreCase("tearDown"));
		}*/

		/*this.stubMethodSelectionList.addSelectionListener(new SelectionListener() {

			public void widgetSelected(final SelectionEvent e) {
				final int[] selectedIndex = CreateUnitTestDialog.this.stubMethodSelectionList.getSelectionIndices();
				for (final int index : selectedIndex) {
					CreateUnitTestDialog.this.createUnitTestData.getSelectedStubMethodsList().add(CreateUnitTestDialog.this.createUnitTestData.getStubMethodsList().get(index));
				}
			}

			public void widgetDefaultSelected(final SelectionEvent e) {

			}
		});*/
	}

	/**
	 * @param parent
	 */
	private void createUnitTestResultFormatPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());

		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final GridData lableGrid = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Select Unit Test Result Format: ");
		label.setLayoutData(lableGrid);

		final GridData listGrid = new GridData(SWT.LEFT, SWT.FILL, true, true);
		this.unitTestResultFormatList = new org.eclipse.swt.widgets.List(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		listGrid.verticalSpan = 4;
		final int listHeight = this.unitTestResultFormatList.getItemHeight() * 4;
		final Rectangle trim = this.unitTestResultFormatList.computeTrim(0, 0, 200, listHeight);
		listGrid.heightHint = trim.height;
		listGrid.widthHint = trim.width;
		this.unitTestResultFormatList.setLayoutData(listGrid);
		/*		InputStream inputStream = null;
				final String unitTestResultFormatFile = "unit-test-result-format.xml";
				try {
					inputStream =FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + unitTestResultFormatFile), false); //ResourceUtil.class.getClassLoader().getResourceAsStream(unitTestResultFormatFile);

					if (inputStream == null) {
						throw new IllegalArgumentException("Invalid file " + unitTestResultFormatFile);
					}
					UnitTestReturnFormatUtil.getUnitTestReturnFormatSettings(inputStream);

				} catch (final Exception ex) {
					ex.printStackTrace();
				} finally {
					if (inputStream != null) {
							try {
								inputStream.close();
							} catch (final IOException ex) {
								ex.printStackTrace();
							}

					}
				}

				final Map<String, List<UnitTestReturnFormatOption>> returnTypeOptionMap = this.unitTestReturnFormatSettings.resultFormatMap.get("Junit4");// we
				// get 		// the		// junit		// type		// from		// preference		// store
				final List<UnitTestReturnFormatOption> unitTestReturnFormatOptionList = returnTypeOptionMap.get("String");// get(key)..key 	// will	// depends	// upon// the// return// type	// of// method

				for (final UnitTestReturnFormatOption unitTestReturnFormatOption : unitTestReturnFormatOptionList) {
					this.unitTestResultFormatList.add(unitTestReturnFormatOption.getName());
				}
		*/
		this.unitTestResultFormatList.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String[] selectedIndices = CreateUnitTestDialog.this.unitTestResultFormatList.getSelection();
				for (final String test : selectedIndices) {
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
	public void populateUnitTestReturnFormat() throws Exception {
		final String appliesTo = this.createUnitTestData.getJunitPreferencesAndType().getJunitPreferences().getJunitType().toString();
		final String methodReturnType = this.createUnitTestData.getMethodReturnType();

		if (this.createUnitTestData.getJunitPreferencesAndType().getJunitPreferences().isCreateMethodBody()) {
			setWarningMessage(this.createMethodBodyMessage, true);
			Map<String, List<UnitTestReturnFormatOption>> returnTypeOptionMap = null;
			String parent = "";

			if (!methodReturnType.equals(METHOD_RETURN_TYPE_VOID)) {
				/*
				 * String returnType = methodReturnType;
				 * if(methodReturnType.contains(ANGLE_BRACKET_LEFT)) {
				 * returnType = methodReturnType.substring(0,
				 * methodReturnType.indexOf(ANGLE_BRACKET_LEFT)).trim(); }
				 */
				final FastCodeType fastCodeType = parseType(methodReturnType, this.createUnitTestData.getFromType().getCompilationUnit());
				final UnitTestReturnFormatUtil returnFormatUtil = new UnitTestReturnFormatUtil();
				if (!this.unitTestReturnFormatSettings.getResultFormatMap().containsKey(appliesTo)
						|| !this.unitTestReturnFormatSettings.getResultFormatMap().get(appliesTo).containsKey(fastCodeType.getName())) {
					returnTypeOptionMap = returnFormatUtil.readFromResultFormatFile(appliesTo, fastCodeType.getName()).get(appliesTo);
				} else {
					returnTypeOptionMap = this.unitTestReturnFormatSettings.getResultFormatMap().get(appliesTo);
				}
				if (returnTypeOptionMap != null && !returnTypeOptionMap.containsKey(fastCodeType.getName())) {
					parent = getFQNameFromFieldTypeName(fastCodeType.getName(), this.createUnitTestData.getFromType().getCompilationUnit());
					if (!returnTypeOptionMap.containsKey(parent)) {
						if (!this.unitTestReturnFormatSettings.getParentMap().containsKey(fastCodeType.getName())) {
							final String tmpParent = returnFormatUtil.getParentFromXml(fastCodeType.getName()).get(fastCodeType.getName());
							if (tmpParent != null) {
								parent = tmpParent;
							}
						} else {
							parent = this.unitTestReturnFormatSettings.getParentMap().get(fastCodeType.getName());
						}
						returnTypeOptionMap = returnFormatUtil.readFromResultFormatFile(appliesTo, parent).get(appliesTo);
					}
				}

				// parent=parent.substring(parent.lastIndexOf(".")+1);
				if (!this.unitTestReturnFormatSettings.getParentMap().containsKey(fastCodeType.getName())) {
					this.unitTestReturnFormatSettings.getParentMap().put(fastCodeType.getName(), parent);
				}

				if (returnTypeOptionMap != null) {
					List<UnitTestReturnFormatOption> unitTestReturnFormatOptionList = returnTypeOptionMap.get(fastCodeType.getName());
					if (unitTestReturnFormatOptionList == null) {
						unitTestReturnFormatOptionList = returnTypeOptionMap.get(parent);
					}
					for (final UnitTestReturnFormatOption unitTestReturnFormatOption : getEmptyListForNull(unitTestReturnFormatOptionList)) {
						this.unitTestResultFormatList.add(unitTestReturnFormatOption.getName());
					}

				} else {
					setWarningMessage("Format for " + appliesTo + " not found.", false);
				}
			}
		} else {
			setWarningMessage(this.createMethodBodyMessage, false);
		}

	}

	public CreateUnitTestData getCreateUnitTestData() {
		return this.createUnitTestData;
	}

	/**
	 * @param errorMessage
	 * @param removerMessage
	 */
	public void setWarningMessage(String errorMessage, final boolean removerMessage) {
		this.errorMessage = errorMessage;
		if (this.warningMessageText != null && !this.warningMessageText.isDisposed()) {
			if (errorMessage == null) {
				this.warningMessageText.setText(" \n ");
			} else {
				if (isEmpty(this.warningMessageText.getText()) && !removerMessage) {
					this.warningMessageText.setText(errorMessage);
				} else {
					if (removerMessage) {
						this.warningMessageText.setText(this.warningMessageText.getText().replace(errorMessage, this.defaultMessage));
					} else if (!this.warningMessageText.getText().contains(errorMessage)) {
						this.warningMessageText.setText(this.warningMessageText.getText() + errorMessage);
					}
				}
			}
			//this.errorMessageText.setText(errorMessage == null ? " \n " : isEmpty(this.errorMessageText.getText()) ? errorMessage : !this.errorMessageText.getText().contains(errorMessage) ? this.errorMessageText.getText().trim() + errorMessage.trim() : this.errorMessageText.getText().trim()); //$NON-NLS-1$
			final boolean hasError = this.warningMessageText.getText() != this.defaultMessage; //errorMessage != this.defaultMessage;
			this.warningMessageText.setEnabled(hasError);
			this.warningMessageText.setVisible(hasError);
			this.warningMessageText.getParent().update();
			if (removerMessage) {
				errorMessage = this.defaultMessage;
			}
			/*final Control button = getButton(IDialogConstants.OK_ID);
			if (button != null) {
				System.out.println(errorMessage == this.defaultMessage || errorMessage == this.testMethodExistMessage || errorMessage == this.multipleSelectionMessage || errorMessage == this.createMethodBodyMessage);

				button.setEnabled(errorMessage == this.defaultMessage || errorMessage == this.testMethodExistMessage || errorMessage == this.multipleSelectionMessage || errorMessage == this.createMethodBodyMessage);
			}*/
		}
	}

	/**
	 * @param errorMessage
	 * @param removerMessage
	 */
	public void setErrorMessage(String errorMessage, final boolean removerMessage) {
		this.errorMessage = errorMessage;
		if (this.errorMessageText != null && !this.errorMessageText.isDisposed()) {
			if (errorMessage == null) {
				this.errorMessageText.setText(" \n ");
			} else {
				if (isEmpty(this.errorMessageText.getText()) && !removerMessage) {
					this.errorMessageText.setText(errorMessage);
				} else {
					if (removerMessage) {
						this.errorMessageText.setText(this.errorMessageText.getText().replace(errorMessage, this.defaultMessage));
					} else if (!this.errorMessageText.getText().contains(errorMessage)) {
						this.errorMessageText.setText(this.errorMessageText.getText() + errorMessage);
					}
				}
			}
			//this.errorMessageText.setText(errorMessage == null ? " \n " : isEmpty(this.errorMessageText.getText()) ? errorMessage : !this.errorMessageText.getText().contains(errorMessage) ? this.errorMessageText.getText().trim() + errorMessage.trim() : this.errorMessageText.getText().trim()); //$NON-NLS-1$
			final boolean hasError = this.errorMessageText.getText() != this.defaultMessage; //errorMessage != this.defaultMessage;
			this.errorMessageText.setEnabled(hasError);
			this.errorMessageText.setVisible(hasError);
			this.errorMessageText.getParent().update();
			if (removerMessage) {
				errorMessage = this.defaultMessage;
			}
			final Control button = getButton(IDialogConstants.OK_ID);
			if (button != null) {

				button.setEnabled(errorMessage == this.defaultMessage || errorMessage == this.testMethodExistMessage
						|| errorMessage == this.multipleSelectionMessage || errorMessage == this.createMethodBodyMessage);
			}
		}
	}

	/**
	 * @param returnType
	 * @return
	 */
	public String getParentForReturnType(final String returnType) {
		IImportDeclaration[] imports;
		String superClassName = "";
		try {
			imports = this.createUnitTestData.getFromType().getCompilationUnit().getImports();
			for (int i = 0; i < imports.length; i++) {
				if (imports[i].getElementName().endsWith(returnType)) {
					final IType type = SourceUtil.getTypeFromProject(this.createUnitTestData.getFromType().getCompilationUnit()
							.getJavaProject(), imports[i].getElementName());
					superClassName = type.getSuperclassName();
					break;
				}
			}
		} catch (final JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return superClassName;
	}

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Junit Dialog");

		shell.setFullScreen(true);

		//		createJunitDialog.open();
		//		while (!shell.isDisposed()) {
		//			if (!display.readAndDispatch()) {
		//				display.sleep();
		//			}
		//		}
		//		display.dispose();
	}
}

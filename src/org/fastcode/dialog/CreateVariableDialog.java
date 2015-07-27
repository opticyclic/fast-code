package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.GROOVY_EXTENSION;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN;
import static org.fastcode.common.FastCodeConstants.LIST;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.SET;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.common.FastCodeConstants.UNDER_SCORE;
import static org.fastcode.preferences.PreferenceConstants.P_RADIO_GROUP_EDITOR_VALUE;
import static org.fastcode.preferences.PreferenceConstants.P_SETTER_VAR_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.P_VARIABLE_ANNOTATION;
import static org.fastcode.util.FastCodeUtil.getEmptyArrayForNull;
import static org.fastcode.util.MessageUtil.getChoiceFromMultipleValues;
import static org.fastcode.util.SourceUtil.isNativeType;
import static org.fastcode.util.StringUtil.changeFirstLetterToLowerCase;
import static org.fastcode.util.StringUtil.changeToCamelCase;
import static org.fastcode.util.StringUtil.getClsNmeFromFQClsNme;
import static org.fastcode.util.StringUtil.isAllLettersUpperCase;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.isJavaReservedWord;
import static org.fastcode.util.StringUtil.isValidVariableName;
import static org.fastcode.util.StringUtil.reverseCamelCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.CreateVariableData;
import org.fastcode.common.FastCodeColor;
import org.fastcode.common.FastCodeConstants.ACCESS_MODIFIER;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER;
import org.fastcode.common.FastCodeFont;
import org.fastcode.setting.GlobalSettings;

/**
 * @author Gautam
 *
 */

public class CreateVariableDialog extends TrayDialog {

	private String								fieldName;
	private String								type;
	protected Text								fieldType;
	private Text								definedClassType;
	private Text								initialText;

	protected Button							primitiveButton;
	private Button								initializeButton;

	private Button								publicButton;
	private Button								privateButton;
	private Button								defaultButton;
	private Button								protectedButton;

	private Button								getterSetterButton;
	private Button								getterButton;
	private Button								noneButton;
	private Button								getterAdderButton;
	private Button								staticButton;
	private Button								finalButton;
	private Button								publicStaticFinalButton;

	private Button								listButton;
	private Button								arrayButton;
	private Button								setButton;

	private Combo								listCombo;
	private Combo								setCombo;
	private Button								notReqButton;

	RadioGroupFieldEditor						primitivesRadioButton;

	private Button								doNotChange;

	private Button								changeToWrapper;

	private String								setterVarPrefixStr;

	String										origFieldName			= EMPTY_STR;

	String										chngdFieldName			= EMPTY_STR;

	String[]									origFieldNameArray;

	String[]									chngdFieldNameArray;

	private static final String[][]				PRIMITIVES_TYPES		= { { "String", "String" }, { "Date", "Date" }, { "int", "int" },
			{ "boolean", "boolean" }, { "long", "long" }, { "float", "float" }, { "double", "double" }, { "short", "short" },
			{ "char", "char" }, { "byte", "byte" }						};
	private Button								browse;

	private int									countFinal				= 0;

	String										OK_LABEL				= "Create";

	private static final Map<String, String>	premitiveWrapperMap		= new HashMap<String, String>();

	protected CreateVariableData				createVariableData;
	Shell										shell;
	private boolean								isDefined				= false;

	final IPreferenceStore						preferenceStore;
	private String								errorMessage;
	private Text								errorMessageText;
	protected final String						defaultMessage			= NEWLINE;

	private static final Map<String, String>	initialValueMap			= new HashMap<String, String>();

	private Combo								insertionPointCombo;
	private List								annotationsList;
	private String								variableName;

	private Button								builderPattern;
	private Text								arrayDimVal;
	private Combo								arrayDim;
	private final String						builderErrorMsg			= "Cannot set specifier to Static when builder required is checked";

	String										fieldNameWithUnderScore;
	String										errorMsgVariableExist	= "Variable Name(s) already exists:-";
	static {
		premitiveWrapperMap.put("int", "java.lang.Integer");
		premitiveWrapperMap.put("boolean", "java.lang.Boolean");
		premitiveWrapperMap.put("byte", "java.lang.Byte");
		premitiveWrapperMap.put("char", "java.lang.Character");
		premitiveWrapperMap.put("double", "java.lang.Double");
		premitiveWrapperMap.put("float", "java.lang.Float");
		premitiveWrapperMap.put("long", "java.lang.Long");
		premitiveWrapperMap.put("short", "java.lang.Short");
		premitiveWrapperMap.put("String", "java.lang.String");
		premitiveWrapperMap.put("Date", "java.util.Date");
	}

	static {
		initialValueMap.put("String", "\"\"");
		initialValueMap.put("Date", "new Date()");
		initialValueMap.put("int", "0");
		initialValueMap.put("boolean", "true");
		initialValueMap.put("byte", "0");
		initialValueMap.put("char", "''");
		initialValueMap.put("double", "0.0");
		initialValueMap.put("float", "0.0f");
		initialValueMap.put("long", "0");
		initialValueMap.put("short", "0");
		initialValueMap.put("defined", "null");
		initialValueMap.put("array", "{}");
	}

	/**
	 * @param shell
	 */
	public CreateVariableDialog(final Shell shell) {
		super(shell);
		this.shell = shell;

		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID); // Activator.getDefault().getPreferenceStore();

	}

	/**
	 * @param shell
	 * @param createVariableData
	 */
	public CreateVariableDialog(final Shell shell, final CreateVariableData createVariableData) {
		super(shell);
		this.shell = shell;

		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID); // Activator.getDefault().getPreferenceStore();
		this.createVariableData = createVariableData;
	}

	@Override
	public void create() {
		super.create();
		final Control button = getButton(IDialogConstants.OK_ID);
		if (button != null) {
			if (isEmpty(this.definedClassType.getText()) && isEmpty(this.fieldType.getText())) {
				button.setEnabled(false);
			}

		}
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		if (this.createVariableData.isVariableModifyAction()) {
			shell.setText("Modify Variable Dialog");
		} else {
			shell.setText("Create Variable Dialog");
		}

	}

	/**
	 * @param parent
	 *
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {
		if (!this.createVariableData.isCreateClassSimple() && !this.createVariableData.isCreateClassDetailed()) {
			final GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			parent.setLayout(layout);
			createErrorMessageText(parent);
		}

		createPrimitiveButtons(parent);
		if (this.createVariableData.getFieldType() != null && this.createVariableData.getFieldType().equals("String")
				&& !this.createVariableData.isVariableModifyAction()) {
			this.primitivesRadioButton.setEnabled(false, parent);
		}
		this.definedClassType = createDefinedText(parent, "Type: ", 0);

		this.fieldType = createText(parent, "  Enter name(s) (SPACE SEPERATED) for the Choosen Type of Field: ", 0);
		createFieldDetailsPane(parent);
		setDefualtValues();
		if (this.createVariableData.isInterface()) {
			if (!this.createVariableData.isCreateFieldSimple()) {
				this.publicStaticFinalButton.setSelection(true);
				CreateVariableDialog.this.staticButton.setSelection(true);
				CreateVariableDialog.this.finalButton.setSelection(true);
				CreateVariableDialog.this.staticButton.setEnabled(false);
				CreateVariableDialog.this.finalButton.setEnabled(false);
				CreateVariableDialog.this.publicStaticFinalButton.setEnabled(false);
				CreateVariableDialog.this.initializeButton.setSelection(true);
			}
			CreateVariableDialog.this.publicButton.setSelection(false);
			CreateVariableDialog.this.privateButton.setSelection(false);
			CreateVariableDialog.this.protectedButton.setSelection(false);
			CreateVariableDialog.this.defaultButton.setSelection(true);
			CreateVariableDialog.this.getterButton.setEnabled(false);
			CreateVariableDialog.this.getterSetterButton.setEnabled(false);
			CreateVariableDialog.this.getterButton.setSelection(false);
			CreateVariableDialog.this.getterSetterButton.setSelection(false);
			CreateVariableDialog.this.publicButton.setEnabled(false);
			CreateVariableDialog.this.privateButton.setEnabled(false);
			CreateVariableDialog.this.protectedButton.setEnabled(false);
			CreateVariableDialog.this.defaultButton.setEnabled(false);
			CreateVariableDialog.this.noneButton.setEnabled(false);
			CreateVariableDialog.this.noneButton.setSelection(true);
			CreateVariableDialog.this.builderPattern.setEnabled(false);

		}
		if (!this.createVariableData.isVariableModifyAction()) {
			if (this.createVariableData.getGetterSetter() != null) {
				final GETTER_SETTER getterSetter = this.createVariableData.getGetterSetter();
				this.getterSetterButton.setSelection(getterSetter.equals(GETTER_SETTER.GETTER_SETTER_EXIST));
			}
			if (this.createVariableData.getFieldType() != null && this.createVariableData.getFieldType().equals("String")) {
				this.definedClassType.setText(premitiveWrapperMap.get(this.createVariableData.getFieldType()));
				this.primitivesRadioButton.setPreferenceName(P_RADIO_GROUP_EDITOR_VALUE);
				this.primitivesRadioButton.setPreferenceStore(this.preferenceStore);
				this.preferenceStore.setValue(P_RADIO_GROUP_EDITOR_VALUE, this.createVariableData.getFieldType());
				this.primitivesRadioButton.load();
			}
		}
		return parent;
	}

	private void createFieldDetailsPane(final Composite parent) {

		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout(6, false);
		composite.setLayout(layout);
		createDoNotChangeButton(composite);
		if (!this.createVariableData.isCreateFieldSimple()) {
			createInitializePane(composite);
		}
		createGetterSetterButtons(parent);
		createAccessButtons(parent);
		if (!this.createVariableData.isCreateFieldSimple()) {
			createModifierButtons(parent);
		}

		if (this.createVariableData.isCreateClassDetailed() || !this.createVariableData.isCreateClassSimple()
				&& !this.createVariableData.isCreateFieldSimple() || this.createVariableData.isVariableModifyAction()) {
			collectionSpec(parent);
		}
		if (!this.createVariableData.isCreateFieldSimple() && !this.createVariableData.isCreateClassSimple()
				&& !this.createVariableData.isCreateClassDetailed() || this.createVariableData.isVariableModifyAction()) {
			createInsertionPointCombo(parent);
		}
		if (this.createVariableData.isCreateClassDetailed() || !this.createVariableData.isCreateClassSimple()
				&& !this.createVariableData.isCreateFieldSimple() || this.createVariableData.isVariableModifyAction()) {
			createAnnotationsList(parent);
		}
		if (!this.createVariableData.isCreateClassSimple() && !this.createVariableData.isCreateClassDetailed()) {
			this.fieldType.setFocus();
		}
	}

	/**
	 * @param parent
	 */
	private void createAnnotationsList(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout(6, false);
		final GridData lableGrid = new GridData();
		composite.setLayout(layout);
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Select Annotation:");
		label.setLayoutData(lableGrid);

		final GridData listGrid = new GridData(SWT.NONE, SWT.NONE, false, true);

		this.annotationsList = new org.eclipse.swt.widgets.List(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		listGrid.verticalSpan = 2;
		final int listHeight = this.annotationsList.getItemHeight() * 4;
		final Rectangle trim = this.annotationsList.computeTrim(0, 0, 200, listHeight);
		listGrid.heightHint = trim.height;
		listGrid.widthHint = trim.width;
		listGrid.horizontalSpan = 2;

		this.annotationsList.setLayoutData(listGrid);

		if (isEmpty(this.preferenceStore.getString(P_VARIABLE_ANNOTATION))) {
			this.annotationsList.setEnabled(false);
		} else {
			final String annotations[] = this.preferenceStore.getString(P_VARIABLE_ANNOTATION).split(NEWLINE);

			for (final String annotation : annotations) {
				this.annotationsList.add(annotation);
			}
			this.annotationsList.setEnabled(true);
		}

		this.annotationsList.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

				if (CreateVariableDialog.this.annotationsList.getSelectionIndices().length >= 1) {
					CreateVariableDialog.this.createVariableData.setUseAnnotation(true);
					generateAnnotationImport(CreateVariableDialog.this.annotationsList.getSelection());
					CreateVariableDialog.this.createVariableData.setAnnotations(CreateVariableDialog.this.annotationsList.getSelection());

				} else {
					CreateVariableDialog.this.createVariableData.setUseAnnotation(false);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * @param selectedAnnotations
	 */
	protected void generateAnnotationImport(final String[] selectedAnnotations) {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final java.util.List<String> annotationsName = new ArrayList<String>();
		if (selectedAnnotations != null) {
			for (final String annotation : selectedAnnotations) {
				if (annotation.trim().equals(EMPTY_STR)) {
					continue;
				}
				final int off = annotation.indexOf(LEFT_PAREN);
				String annotName = annotation;
				if (off != -1) {
					annotName = annotation.substring(0, off);

				}

				if (annotName.startsWith("@")) {
					annotName = annotName.substring(1);
				}
				annotationsName.add(annotName);
			}
			final String[] annotNames = annotationsName.toArray(new String[0]);
			if (annotNames.length > 1) {
				for (int i = 0; i < annotNames.length; i++) {
					for (int j = 1; j < annotNames.length - 1; j++) {
						if (annotNames[j - 1].equals(annotNames[j])) {
							setErrorMessage("Annotations can not be same ", false, EMPTY_STR);
							return;
						} else {
							setErrorMessage("Annotations can not be same ", true, EMPTY_STR);
						}
					}
				}
			} else {
				setErrorMessage(this.defaultMessage, false, EMPTY_STR);
			}
			for (final String annot : annotationsName) {
				if (globalSettings.getAnnotationTypesMap().containsKey(annot)) {
					final String fullAnotTypeName = globalSettings.getAnnotationTypesMap().get(annot);
					this.createVariableData.addImportTypes(fullAnotTypeName);

				}
			}
		}
	}

	/**
	 * @param parent
	 */
	private void createInsertionPointCombo(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout(6, false);
		final GridData lableGrid = new GridData();
		composite.setLayout(layout);
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Insertion Point:      ");
		label.setLayoutData(lableGrid);

		final GridData comboGrid = new GridData(200, 20);
		comboGrid.grabExcessHorizontalSpace = true;
		this.insertionPointCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboGrid.horizontalAlignment = SWT.FILL;
		comboGrid.horizontalSpan = 2;
		this.insertionPointCombo.setLayoutData(comboGrid);
		this.insertionPointCombo.add("First Member");
		this.insertionPointCombo.add("Last Member");
		this.insertionPointCombo.select(1);

		for (final IField field : this.createVariableData.getiClassFields()) {
			this.insertionPointCombo.add("after '" + field.getElementName() + "'");
		}

		this.insertionPointCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent event) {

				final int selectionIndex = CreateVariableDialog.this.insertionPointCombo.getSelectionIndex();
				if (selectionIndex > 1) {
					CreateVariableDialog.this.createVariableData.setInsertionPoint(CreateVariableDialog.this.createVariableData
							.getiClassFields().get(selectionIndex - 1));
					CreateVariableDialog.this.createVariableData.setStringInsertionPoint(CreateVariableDialog.this.createVariableData
							.getInsertionPoint().getElementName());
				} else if (selectionIndex == 0) {
					CreateVariableDialog.this.createVariableData.setInsertionPoint(CreateVariableDialog.this.createVariableData
							.getiClassFields().get(selectionIndex));
					CreateVariableDialog.this.createVariableData.setStringInsertionPoint(CreateVariableDialog.this.insertionPointCombo
							.getItem(selectionIndex));
				} else if (selectionIndex == 1) {
					CreateVariableDialog.this.createVariableData.setInsertionPoint(null);
					CreateVariableDialog.this.createVariableData.setStringInsertionPoint(CreateVariableDialog.this.insertionPointCombo
							.getItem(selectionIndex));
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		final Label blank = new Label(composite, SWT.NONE);
		final GridData blankData = new GridData();
		blankData.horizontalAlignment = SWT.FILL;
		blankData.horizontalSpan = 3;
		blank.setLayoutData(blankData);
	}

	/**
	 * @param parent
	 */
	protected void createErrorMessageText(final Composite parent) {

		final GridData errText = new GridData(590, 40);
		errText.grabExcessHorizontalSpace = true;

		this.errorMessageText = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
		//this.errorMessageText.setForeground(new Color(null, 255, 0, 0));
		this.errorMessageText.setForeground(FastCodeColor.getErrorMsgColor());
		this.errorMessageText.setLayoutData(errText);
		setErrorMessage(this.defaultMessage, false, EMPTY_STR);

	}

	@Override
	protected Control createButtonBar(final Composite parent) {
		return super.createButtonBar(parent);
	}

	@Override
	protected void okPressed() {
		if (this.createVariableData == null) {
			this.createVariableData = new CreateVariableData();
		}

		if (isEmpty(this.definedClassType.getText())) {
			setErrorMessage("Please choose field type.", false, EMPTY_STR);
			this.definedClassType.setFocus();
			return;
		} else {
			setErrorMessage("Please choose field type.", true, EMPTY_STR);
		}

		if (isEmpty(this.fieldType.getText())) {
			setErrorMessage("Please enter field name(s).", false, EMPTY_STR);
			this.fieldType.setFocus();
			return;
		} else {
			setErrorMessage("Please enter field name(s).", true, EMPTY_STR);
		}

		if (isEmpty(this.fieldType.getText())) {
			setErrorMessage("Please enter valid variable name.", false, EMPTY_STR);
			this.fieldType.setFocus();
			return;
		} else {
			setErrorMessage("Please enter valid variable name.", true, EMPTY_STR);
		}

		if (this.initializeButton != null && this.initializeButton.getSelection() && isEmpty(this.initialText.getText())) {
			setErrorMessage("Please set a value to initialise.", false, EMPTY_STR);
			this.initialText.setFocus();
			return;
		} else {
			setErrorMessage("Please set a value to initialise.", true, EMPTY_STR);
		}

		GETTER_SETTER getterSetter = GETTER_SETTER.NONE;
		boolean isGetterSetterSelected = false;
		boolean isGetterSetterReqd = true;

		if (this.getterButton.getSelection()) {
			isGetterSetterSelected = true;
			getterSetter = GETTER_SETTER.GETTER_EXIST;
		} else if (this.getterSetterButton.getSelection()) {
			isGetterSetterSelected = true;
			getterSetter = GETTER_SETTER.GETTER_SETTER_EXIST;
		} else if (this.getterAdderButton.getSelection()) {
			isGetterSetterSelected = true;
			getterSetter = GETTER_SETTER.GETTER_ADDER_EXIST;
		} else if (this.noneButton.getSelection()) {
			isGetterSetterSelected = true;
			getterSetter = GETTER_SETTER.NONE;
		}
		if (this.publicButton.getSelection()) {
			isGetterSetterReqd = false;
		}
		if (isGetterSetterReqd) {
			if (!isGetterSetterSelected) {
				final String[] choices = { "Proceed Anyway", "Cancel" };

				final String choice = getChoiceFromMultipleValues(this.shell, "Warning",
						"You have not selected getter/setter.Would You Like To", choices);
				if (choice == null || choice.equals("Cancel")) {
					return;
				}
			}
		}

		this.createVariableData.setGetterSetter(getterSetter);

		ACCESS_MODIFIER access_modifier = ACCESS_MODIFIER.PRIVATE;
		if (this.privateButton.getSelection()) {
			access_modifier = ACCESS_MODIFIER.PRIVATE;
		} else if (this.publicButton.getSelection()) {
			access_modifier = ACCESS_MODIFIER.PUBLIC;
		} else if (this.protectedButton.getSelection()) {
			access_modifier = ACCESS_MODIFIER.PROTECTED;
		} else if (this.defaultButton.getSelection()) {
			access_modifier = ACCESS_MODIFIER.DEFAULT;
		}

		this.createVariableData.setAccessModifier(access_modifier);

		this.fieldName = this.fieldType.getText();
		final String[] names = this.fieldName.split("\\s+");
		this.createVariableData.setFieldNames(names);
		if (this.initializeButton != null) {
			final boolean isInitialized = this.initializeButton.getSelection();
			this.createVariableData.setInitialized(isInitialized);
			final String initialVal = this.initialText.getText();
			this.createVariableData.setInitialValue(initialVal);
		}
		if (!CreateVariableDialog.this.createVariableData.isCreateFieldSimple()) {
			final boolean finalSelect = this.finalButton.getSelection();
			this.createVariableData.setFinal(finalSelect);

			final boolean isStatic = this.staticButton.getSelection();
			this.createVariableData.setStatic(isStatic);
		}

		if (!isEmpty(this.definedClassType.getText())) {
			if (!this.createVariableData.isCreateClassSimple() && !CreateVariableDialog.this.createVariableData.isCreateFieldSimple()
					&& (this.listButton.getSelection() || this.setButton.getSelection() || this.arrayButton.getSelection())) {
				if (this.type.equals("String") || this.type.equals("Date")) {
					this.createVariableData.setFieldType(premitiveWrapperMap.get(this.type));
				} else {
					this.createVariableData.setFieldType(this.type);
				}

			} else {
				this.createVariableData.setFieldType(this.definedClassType.getText());
			}
		} else {
			this.createVariableData.setFieldType(this.type);
		}
		if (!this.createVariableData.isCreateClassSimple() && !CreateVariableDialog.this.createVariableData.isCreateFieldSimple()) {
			final boolean isArray = this.arrayButton.getSelection();
			this.createVariableData.setArray(isArray);

			final boolean isSet = this.setButton.getSelection();
			this.createVariableData.setSet(isSet);

			final boolean isList = this.listButton.getSelection();
			this.createVariableData.setList(isList);

			final String typeList = this.listCombo.getItem(this.listCombo.getSelectionIndex());
			this.createVariableData.setListType(typeList);

			final String typeSet = this.setCombo.getItem(this.setCombo.getSelectionIndex());
			this.createVariableData.setSetType(typeSet);

			if (isList) {
				this.createVariableData.setFieldParams(new String[] { getClsNmeFromFQClsNme(this.createVariableData.getFieldType()) });
				this.createVariableData.addImportTypes(this.createVariableData.classFQNameMap.get(LIST));
				if (!"List Types".equals(this.createVariableData.getListType())) {
					this.createVariableData
							.addImportTypes(this.createVariableData.classFQNameMap.get(this.createVariableData.getListType()));
				}
			} else if (isSet) {
				this.createVariableData.setFieldParams(new String[] { getClsNmeFromFQClsNme(this.createVariableData.getFieldType()) });
				this.createVariableData.addImportTypes(this.createVariableData.classFQNameMap.get(SET));
				if (!"Set Types".equals(this.createVariableData.getSetType())) {
					this.createVariableData
							.addImportTypes(this.createVariableData.classFQNameMap.get(this.createVariableData.getSetType()));
				}

			}
		}
		if (this.preferenceStore.contains(P_SETTER_VAR_PREFIX)) {
			this.createVariableData.setSetterVerPrefix(this.preferenceStore.getString(P_SETTER_VAR_PREFIX));
		}

		if (!isNativeType(this.createVariableData.getFieldType())) {
			this.createVariableData.addImportTypes(this.createVariableData.getFieldType());
		}
		if (this.createVariableData.isVariableModifyAction()) {
			final int selectionIndex = CreateVariableDialog.this.insertionPointCombo.getSelectionIndex();
			if (CreateVariableDialog.this.createVariableData.getiClassFields().size() > 0) {

				if (selectionIndex > 1) {
					CreateVariableDialog.this.createVariableData.setInsertionPoint(CreateVariableDialog.this.createVariableData
							.getiClassFields().get(selectionIndex - 1));
					CreateVariableDialog.this.createVariableData.setStringInsertionPoint(CreateVariableDialog.this.createVariableData
							.getInsertionPoint().getElementName());
				} else if (selectionIndex == 0) {
					CreateVariableDialog.this.createVariableData.setInsertionPoint(CreateVariableDialog.this.createVariableData
							.getiClassFields().get(selectionIndex));
					CreateVariableDialog.this.createVariableData.setStringInsertionPoint(CreateVariableDialog.this.insertionPointCombo
							.getItem(selectionIndex));
				} else if (selectionIndex == 1) {
					CreateVariableDialog.this.createVariableData.setInsertionPoint(null);
					CreateVariableDialog.this.createVariableData.setStringInsertionPoint(CreateVariableDialog.this.insertionPointCombo
							.getItem(selectionIndex));
				}
			}

		}

		final boolean isBuilderPattern = this.builderPattern.getSelection();
		this.createVariableData.setBuilderReqd(isBuilderPattern);
		if (this.arrayButton != null && this.arrayButton.getSelection()) {
			final String arrayDimVal = this.arrayDim.getItem(this.arrayDim.getSelectionIndex());
			this.createVariableData.setArrayDim(Integer.parseInt(arrayDimVal));
		}
		if (this.createVariableData.isInterface() && this.createVariableData.isCreateFieldSimple()) {
			this.createVariableData.setInitialized(true);
			if (premitiveWrapperMap.containsKey(this.type)) {
				this.createVariableData.setInitialValue(initialValueMap.get(this.type));
			} else {
				this.createVariableData.setInitialValue(null);
			}

		}
		super.okPressed();
	}

	/**
	 *
	 * @param parent
	 */

	protected void createAccessButtons(final Composite composite) {
		final Group group1 = new Group(composite, SWT.SHADOW_IN);
		group1.setText("Access Specifier :");
		final Device device = Display.getCurrent();
		final FontData[] fD = composite.getFont().getFontData();
		//group1.setFont((device, fD[0].getName(), fD[0].getHeight(), SWT.BOLD));
		group1.setFont(FastCodeFont.getBoldFont(fD[0].getName(), fD[0].getHeight()));

		final Composite radioBox = group1;
		final GridLayout layout = new GridLayout();
		layout.numColumns = 7;
		layout.makeColumnsEqualWidth = true;
		radioBox.setLayout(new GridLayout(7, true));
		//final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		radioBox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.privateButton = new Button(radioBox, SWT.RADIO);
		this.privateButton.setText("private");
		selectGetterSetter(this.privateButton);
		this.protectedButton = new Button(radioBox, SWT.RADIO);
		this.protectedButton.setText("protected");
		selectGetterSetter(this.protectedButton);
		this.publicButton = new Button(radioBox, SWT.RADIO);
		this.publicButton.setText("public");
		selectGetterSetter(this.publicButton);
		this.defaultButton = new Button(radioBox, SWT.RADIO);
		this.defaultButton.setText("default");
		selectGetterSetter(this.defaultButton);
		if (this.createVariableData.getCompUnitType().equals(JAVA_EXTENSION)) {
			this.privateButton.setSelection(true);
		} else if (this.createVariableData.getCompUnitType().equals(GROOVY_EXTENSION)) {
			this.defaultButton.setSelection(true);
		}
		this.privateButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				if (CreateVariableDialog.this.privateButton.getSelection()) {
					if (CreateVariableDialog.this.publicStaticFinalButton != null
							&& CreateVariableDialog.this.publicStaticFinalButton.getSelection()) {
						CreateVariableDialog.this.publicStaticFinalButton.setSelection(false);
					}
				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.publicButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				if (CreateVariableDialog.this.publicButton.getSelection()) {
					if (CreateVariableDialog.this.staticButton != null && CreateVariableDialog.this.staticButton.getSelection()
							&& CreateVariableDialog.this.finalButton.getSelection()) {
						CreateVariableDialog.this.publicStaticFinalButton.setSelection(true);
					}
				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		final Label blank1 = new Label(radioBox, SWT.NONE);
		final Label blank2 = new Label(radioBox, SWT.NONE);
		final Label blank3 = new Label(radioBox, SWT.NONE);
		blank1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		blank2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		blank3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	/**
	 *
	 * @param option
	 */
	protected void selectGetterSetter(final Button option) {
		boolean getter = false;
		boolean setter = false;
		boolean both = false;
		boolean none = false;
		boolean adder = false;
		boolean builder = false;
		if (option == this.privateButton) {
			setter = true;
			getter = true;
			both = true;
			none = true;
			adder = true;
			builder = true;
		} else if (option == this.publicButton) {
			none = true;
			adder = false;
		} else if (option == this.protectedButton) {
			getter = true;
			setter = true;
			both = true;
			none = true;
			adder = true;
			builder = true;
		} else if (option == this.defaultButton) {
			getter = true;
			setter = true;
			both = true;
			none = true;
			adder = true;
			builder = true;
		}
		addDisableListener(option, getter, setter, both, none, adder, builder);
	}

	/**
	 *
	 * @param option
	 * @param getter
	 * @param setter
	 * @param both
	 * @param none
	 * @param adder
	 */
	protected void addDisableListener(final Button option, final boolean getter, final boolean setter, final boolean both,
			final boolean none, final boolean adder, final boolean builder) {
		option.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				CreateVariableDialog.this.getterButton.setEnabled(getter);
				if (!getter) {
					CreateVariableDialog.this.getterButton.setSelection(getter);
				}
				if (!builder) {
					CreateVariableDialog.this.builderPattern.setSelection(builder);
				}
				CreateVariableDialog.this.noneButton.setEnabled(none);
				if (!none) {
					CreateVariableDialog.this.noneButton.setSelection(none);
				}
				if (CreateVariableDialog.this.countFinal == 1) {
					CreateVariableDialog.this.getterSetterButton.setEnabled(false);
					CreateVariableDialog.this.getterAdderButton.setEnabled(false);
					CreateVariableDialog.this.getterSetterButton.setSelection(false);
					CreateVariableDialog.this.getterAdderButton.setSelection(false);
				} else {
					CreateVariableDialog.this.getterSetterButton.setEnabled(both);
					if (!both) {
						CreateVariableDialog.this.getterSetterButton.setSelection(both);
					}
					if (!CreateVariableDialog.this.createVariableData.isCreateClassSimple()
							&& !CreateVariableDialog.this.createVariableData.isCreateFieldSimple()) {
						if (CreateVariableDialog.this.listButton.getSelection() || CreateVariableDialog.this.setButton.getSelection()) {
							CreateVariableDialog.this.getterAdderButton.setEnabled(adder);
							if (!adder) {
								CreateVariableDialog.this.getterAdderButton.setSelection(adder);
							}
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
			}
		});
	}

	/**
	 *
	 * @param parent
	 */
	protected void createPrimitiveButtons(final Composite parent) {
		this.primitivesRadioButton = new RadioGroupFieldEditor(P_RADIO_GROUP_EDITOR_VALUE, "Pre Defined Types", PRIMITIVES_TYPES.length,
				PRIMITIVES_TYPES, parent, true);
		this.primitivesRadioButton.setPreferenceStore(this.preferenceStore);
		this.primitivesRadioButton.setPropertyChangeListener(new IPropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				CreateVariableDialog.this.type = (String) event.getNewValue();

				if (!CreateVariableDialog.this.createVariableData.isCreateFieldSimple()
						&& CreateVariableDialog.this.initializeButton.getSelection()) {
					CreateVariableDialog.this.initialText.setText(CreateVariableDialog.initialValueMap.get(CreateVariableDialog.this.type));
				}
				if (!(CreateVariableDialog.this.type == "String" || CreateVariableDialog.this.type == "Date")) {
					if (!CreateVariableDialog.this.createVariableData.isCreateFieldSimple()) {
						CreateVariableDialog.this.initializeButton.setEnabled(true);
					}

					if (!CreateVariableDialog.this.createVariableData.isCreateFieldSimple()
							&& CreateVariableDialog.this.changeToWrapper != null
							&& !CreateVariableDialog.this.changeToWrapper.getSelection()) {
						CreateVariableDialog.this.changeToWrapper.setEnabled(true);
						if (!CreateVariableDialog.this.createVariableData.isCreateClassSimple()
								&& !CreateVariableDialog.this.createVariableData.isCreateFieldSimple()) {
							CreateVariableDialog.this.listButton.setEnabled(false);
							CreateVariableDialog.this.setButton.setEnabled(false);
							CreateVariableDialog.this.listCombo.setEnabled(false);
							CreateVariableDialog.this.setCombo.setEnabled(false);
							CreateVariableDialog.this.listButton.setSelection(false);
							CreateVariableDialog.this.setButton.setSelection(false);
						}
						CreateVariableDialog.this.getterAdderButton.setEnabled(false);
						CreateVariableDialog.this.getterAdderButton.setSelection(false);
					}
					if (CreateVariableDialog.this.changeToWrapper != null && CreateVariableDialog.this.changeToWrapper.getSelection()) {
						CreateVariableDialog.this.definedClassType.setText(CreateVariableDialog.premitiveWrapperMap
								.get(CreateVariableDialog.this.type));
						// CreateVariableDialog.this.addImport = true;
					} else {
						CreateVariableDialog.this.definedClassType.setText(CreateVariableDialog.this.type);

					}
				} else {
					CreateVariableDialog.this.definedClassType.setText(CreateVariableDialog.premitiveWrapperMap
							.get(CreateVariableDialog.this.type));
					if (!CreateVariableDialog.this.createVariableData.isCreateClassSimple()
							&& !CreateVariableDialog.this.createVariableData.isCreateFieldSimple()) {
						CreateVariableDialog.this.listButton.setEnabled(true);
						CreateVariableDialog.this.setButton.setEnabled(true);

						CreateVariableDialog.this.changeToWrapper.setSelection(false);
						CreateVariableDialog.this.changeToWrapper.setEnabled(false);

						final String tmpType = CreateVariableDialog.this.definedClassType.getText();
						if (CreateVariableDialog.this.listButton != null && CreateVariableDialog.this.listButton.getSelection()) {
							if (tmpType.indexOf(".") > -1) {
								CreateVariableDialog.this.definedClassType.setText("List<"
										+ tmpType.substring(tmpType.lastIndexOf(DOT) + 1, tmpType.length()) + ">");
							}
						}
						if (CreateVariableDialog.this.setButton != null && CreateVariableDialog.this.setButton.getSelection()) {
							if (CreateVariableDialog.this.type.indexOf(".") > -1) {
								CreateVariableDialog.this.definedClassType.setText("Set<"
										+ tmpType.substring(tmpType.lastIndexOf(DOT) + 1, tmpType.length()) + ">");
							}
						}

						// CreateVariableDialog.this.addImport = true;
					}
					if (CreateVariableDialog.this.arrayButton != null && CreateVariableDialog.this.arrayButton.getSelection()) {
						CreateVariableDialog.this.definedClassType.setText(CreateVariableDialog.this.type + "[]");

					}
				}

			}

		});

	}

	/**
	 *
	 * @param parent
	 */
	protected void createGetterSetterButtons(final Composite parent) {
		final Group group1 = new Group(parent, SWT.SHADOW_IN);
		group1.setText("Getter/Setter:");
		final Device device = Display.getCurrent();
		final FontData[] fD = parent.getFont().getFontData();
		System.out.println(fD[0].getName() + fD[0].getHeight());
		//group1.setFont(new Font(device, fD[0].getName(), fD[0].getHeight(), SWT.BOLD));
		group1.setFont(FastCodeFont.getBoldFont(fD[0].getName(), fD[0].getHeight()));

		final Composite radioBox = group1;
		//final GridLayout layout = new GridLayout(7, true);
		group1.setLayout(new GridLayout(7, true));
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		radioBox.setLayoutData(gd);
		this.getterAdderButton = new Button(radioBox, SWT.RADIO);
		this.getterAdderButton.setText("Getter/Adder");
		this.getterAdderButton.setEnabled(false);
		this.getterAdderButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {

			}
		});

		this.getterSetterButton = new Button(radioBox, SWT.RADIO);
		this.getterSetterButton.setText("Getter/Setter");
		this.getterSetterButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.getterSetterButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {

			}
		});

		this.getterButton = new Button(radioBox, SWT.RADIO);
		this.getterButton.setText("Getter Only");
		createBuilderButton(radioBox);
		this.noneButton = new Button(radioBox, SWT.RADIO);
		this.noneButton.setText("None");
		this.noneButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (this.createVariableData.getCompUnitType().equals(GROOVY_EXTENSION)) {
			this.noneButton.setSelection(true);
		}
		this.noneButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
		final Label blank1 = new Label(radioBox, SWT.NONE);
		final Label blank2 = new Label(radioBox, SWT.NONE);
		blank1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		blank2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	}

	/**
	 * @param parent
	 * @param labelText
	 * @param style

	 * @return
	 */
	private Text createText(final Composite parent, final String labelText, final int style) {
		final GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		final Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);

		final Text text = new Text(parent, SWT.BORDER | SWT.MULTI);
		text.setLayoutData(gridData);
		text.setSize(200, 20);
		text.addModifyListener(new ModifyListener() {
			String	exstFileds	= EMPTY_STR;

			@Override
			public void modifyText(final ModifyEvent e) {
				final String name = CreateVariableDialog.this.fieldType.getText();
				String errMsg = EMPTY_STR;
				if (!isEmpty(name)) {
					CreateVariableDialog.this.origFieldNameArray = name.split("\\s+");
					final StringBuilder inputNames = new StringBuilder();
					final StringBuilder fieldNamesWithUnderScore = new StringBuilder();

					for (String text : CreateVariableDialog.this.origFieldNameArray) {
						if (isJavaReservedWord(text)) {
							CreateVariableDialog.this.setErrorMessage("Variable name cannot be Java reserved word", false, EMPTY_STR);
							CreateVariableDialog.this.fieldType.setFocus();
							errMsg = CreateVariableDialog.this.errorMessage;
							return;
						} else {
							CreateVariableDialog.this.setErrorMessage("Variable name cannot be Java reserved word", true, EMPTY_STR);
						}
						if (!isValidVariableName(text)) {

							CreateVariableDialog.this.setErrorMessage("Special Characters are not allowed in variable names", false,
									EMPTY_STR);
							CreateVariableDialog.this.fieldType.setFocus();
							errMsg = CreateVariableDialog.this.errorMessage;
							return;
						} else {
							CreateVariableDialog.this.setErrorMessage("Special Characters are not allowed in variable names", true,
									EMPTY_STR);
						}
						if (text.contains(UNDERSCORE)) {
							text = reverseCamelCase(text, UNDER_SCORE);
							fieldNamesWithUnderScore.append(EMPTY_STR.equals(fieldNamesWithUnderScore.toString()) ? changeFirstLetterToLowerCase(text)
									: SPACE + changeFirstLetterToLowerCase(text));
						} else if (isAllLettersUpperCase(text)) {
							text = text.toLowerCase();
						}
						inputNames.append(EMPTY_STR.equals(inputNames.toString()) ? changeFirstLetterToLowerCase(text) : SPACE
								+ changeFirstLetterToLowerCase(text));
					}
					final String[] namesarr = name.split("\\s+");
					boolean duplicate = false;
					for (int j = 0; j < namesarr.length; j++) {
						for (int k = j + 1; k < namesarr.length; k++) {
							if (namesarr[k].equals(namesarr[j])) {
								duplicate = true;
								break;
							}
							if (duplicate) {
								break;
							}
						}
					}
					if (duplicate) {
						CreateVariableDialog.this.setErrorMessage("You have given the same variable name more than once", false, EMPTY_STR);
						CreateVariableDialog.this.fieldType.setFocus();
						errMsg = CreateVariableDialog.this.errorMessage;
						return;
					} else {
						CreateVariableDialog.this.setErrorMessage("You have given the same variable name more than once", true, EMPTY_STR);

					}

					if (!isEmpty(fieldNamesWithUnderScore.toString())) {
						CreateVariableDialog.this.fieldNameWithUnderScore = fieldNamesWithUnderScore.toString();
					}
					if (!CreateVariableDialog.this.origFieldName.equalsIgnoreCase(name)) {
						CreateVariableDialog.this.origFieldName = name;
					}

					CreateVariableDialog.this.chngdFieldName = inputNames.toString();
					// CreateVariableDialog.this.fieldType.setText(changeFirstLetterToLowerCase(CreateVariableDialog.this.chngdFieldName));..this
					CreateVariableDialog.this.chngdFieldNameArray = changeFirstLetterToLowerCase(CreateVariableDialog.this.chngdFieldName)
							.split("\\s+");
					int count = 0;
					final StringBuilder existingNames = new StringBuilder();

					for (final String strFromCompUnit : getEmptyArrayForNull(CreateVariableDialog.this.createVariableData
							.getExistingFields())) {
						for (final String strFromDialogBox : getEmptyArrayForNull(CreateVariableDialog.this.chngdFieldNameArray)) {

							if (strFromCompUnit.equals(strFromDialogBox)) {
								if (!existingNames.toString().contains(strFromDialogBox)) {
									existingNames.append(EMPTY_STR.equals(existingNames.toString()) ? strFromDialogBox : COMMA
											+ strFromDialogBox);
								}
								count++;
							}
						}

					}
					if (count > 0) {
						this.exstFileds = existingNames.toString();
						errMsg = CreateVariableDialog.this.errorMsgVariableExist;
						CreateVariableDialog.this.setErrorMessage(CreateVariableDialog.this.errorMsgVariableExist, false, this.exstFileds);
						CreateVariableDialog.this.fieldType.setFocus();
						return;

					} else {
						errMsg = CreateVariableDialog.this.errorMsgVariableExist;
						CreateVariableDialog.this.setErrorMessage(errMsg, true, this.exstFileds);
					}

				} else {
					CreateVariableDialog.this.setErrorMessage(CreateVariableDialog.this.errorMessage, true, this.exstFileds);
				}
			}
		});
		text.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(final FocusEvent e) {
			}

			@Override
			public void focusLost(final FocusEvent e) {
				final String name = CreateVariableDialog.this.fieldType.getText();
				if (!CreateVariableDialog.this.origFieldName.equals(name)) {
					return;
				}
				if (!isEmpty(name) && !isEmpty(CreateVariableDialog.this.chngdFieldName)) {

					CreateVariableDialog.this.fieldType.setText(CreateVariableDialog.this.chngdFieldName);

				}
				if (!CreateVariableDialog.this.createVariableData.isCreateFieldSimple()
						&& CreateVariableDialog.this.staticButton.getSelection() && CreateVariableDialog.this.finalButton.getSelection()) {
					CreateVariableDialog.this.fieldType.setText(name.toUpperCase());
				}
			}
		});

		return text;

	}

	/**
	 * @param parent
	 * @param labelText
	 * @param style
	 * @return
	 */
	private Text createDefinedText(final Composite parent, final String labelText, final int style) {

		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		final Text text = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		text.setSize(200, 20);
		text.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 500;
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				if (isEmpty(text.getText())) {
					CreateVariableDialog.this.setErrorMessage("Please choose field type.", false, EMPTY_STR);
				} else {
					CreateVariableDialog.this.setErrorMessage("Please choose field type.", true, EMPTY_STR);
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
					selectionDialog.setTitle("Select Defined Type");
					selectionDialog.setMessage("Select the defined type to create variable");

					if (selectionDialog.open() == CANCEL) {
						return;
					}
					//CreateVariableDialog.this.preferenceStore.setValue(P_RADIO_GROUP_EDITOR_VALUE,"Other");
					//CreateVariableDialog.this.primitivesRadioButton.load();
					final Composite temp = CreateVariableDialog.this.primitivesRadioButton.getRadioBoxControl(parent);
					final Control[] children = temp.getChildren();
					for (final Control child : children) {
						if (child instanceof Button) {
							((Button) child).setSelection(false);
						}
					}

					CreateVariableDialog.this.definedClassType.setText(((IType) selectionDialog.getResult()[0]).getFullyQualifiedName());
					if (isEmpty(CreateVariableDialog.this.fieldType.getText())) {
						CreateVariableDialog.this.fieldType.setText(changeFirstLetterToLowerCase(((IType) selectionDialog.getResult()[0])
								.getElementName()));
					}
					if (!CreateVariableDialog.this.createVariableData.isCreateClassSimple()
							&& !CreateVariableDialog.this.createVariableData.isCreateFieldSimple()) {
						CreateVariableDialog.this.listButton.setEnabled(true);
						CreateVariableDialog.this.setButton.setEnabled(true);
						CreateVariableDialog.this.changeToWrapper.setSelection(false);
						CreateVariableDialog.this.changeToWrapper.setEnabled(false);
					}
					final String tmpType = CreateVariableDialog.this.definedClassType.getText();
					CreateVariableDialog.this.type = tmpType;
					if (!CreateVariableDialog.this.createVariableData.isCreateClassSimple()
							&& !CreateVariableDialog.this.createVariableData.isCreateFieldSimple()) {
						if (CreateVariableDialog.this.listButton.getSelection()) {
							if (tmpType.indexOf(".") > -1) {
								CreateVariableDialog.this.definedClassType.setText("List<"
										+ tmpType.substring(tmpType.lastIndexOf(DOT) + 1, tmpType.length()) + ">");
							}
						}
						if (CreateVariableDialog.this.setButton.getSelection()) {
							if (tmpType.indexOf(".") > -1) {
								CreateVariableDialog.this.definedClassType.setText("Set<"
										+ tmpType.substring(tmpType.lastIndexOf(DOT) + 1, tmpType.length()) + ">");
							}
						}
						if (CreateVariableDialog.this.arrayButton.getSelection()) {
							if (tmpType.indexOf(DOT) > -1) {
								CreateVariableDialog.this.definedClassType.setText(tmpType.substring(tmpType.lastIndexOf(DOT) + 1,
										tmpType.length())
										+ "[]");
							}
						}
					}

				} catch (final JavaModelException ex) {
					ex.printStackTrace();
				}
				CreateVariableDialog.this.initialText.setText(CreateVariableDialog.this.initialValueMap.get("defined"));
				CreateVariableDialog.this.isDefined = true;

			}

		});
		return text;
	}

	/**
	 * @param parent
	 * @param labelText
	 * @param style
	 * @return
	 */

	protected Button createButton(final Composite parent, final String labelText, final int style) {
		final GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		final Button button = new Button(parent, style);
		button.setLayoutData(gridData);
		button.setText(labelText);
		return button;
	}

	/**
	 * @param parent
	 */
	protected void collectionSpec(final Composite composite) {
		final Group group1 = new Group(composite, SWT.SHADOW_IN);
		group1.setText("Collection:");
		final Device device = Display.getCurrent();
		final FontData[] fD = composite.getFont().getFontData();
		System.out.println(fD[0].getName() + fD[0].getHeight());
		//group1.setFont(new Font(device, fD[0].getName(), fD[0].getHeight(), SWT.BOLD));
		group1.setFont(FastCodeFont.getBoldFont(fD[0].getName(), fD[0].getHeight()));
		final Composite radioBox = group1;
		final GridLayout layout = new GridLayout();
		layout.numColumns = 7;
		layout.makeColumnsEqualWidth = true;
		radioBox.setLayout(layout);
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		radioBox.setLayoutData(gd);
		this.notReqButton = new Button(radioBox, SWT.RADIO);
		this.notReqButton.setText("None");
		this.arrayButton = new Button(radioBox, SWT.RADIO);
		this.arrayButton.setText("Array");
		this.listButton = new Button(radioBox, SWT.RADIO);
		this.listButton.setText("List");
		this.setButton = new Button(radioBox, SWT.RADIO);
		this.setButton.setText("Set");
		collectionDetail(radioBox);
	}

	/**
	 * @param parent
	 */
	protected void collectionDetail(final Composite radioBox) {

		this.listCombo = new Combo(radioBox, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		final GridData grid5 = new GridData();
		this.listCombo.setLayoutData(grid5);

		this.listCombo.add("List Types");
		this.listCombo.add("ArrayList");
		this.listCombo.add("LinkedList");
		this.listCombo.select(0);
		this.listCombo.setEnabled(false);

		this.setCombo = new Combo(radioBox, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		final GridData grid6 = new GridData();
		this.setCombo.setLayoutData(grid6);
		this.setCombo.add("Set Types");
		this.setCombo.add("HashSet");
		this.setCombo.add("LinkedHashSet");
		this.setCombo.add("TreeSet");
		this.setCombo.setEnabled(false);
		this.setCombo.select(0);

		this.listButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				if (CreateVariableDialog.this.listButton.getSelection()) {
					CreateVariableDialog.this.setCombo.setEnabled(false);
					CreateVariableDialog.this.initializeButton.setEnabled(true);
					CreateVariableDialog.this.initialText.setEnabled(false);
					if (CreateVariableDialog.this.initializeButton.getSelection()) {
						CreateVariableDialog.this.listCombo.setEnabled(true);
						CreateVariableDialog.this.initialText.setEnabled(false);
						CreateVariableDialog.this.listCombo.select(1);
					} else {
						CreateVariableDialog.this.listCombo.setEnabled(false);
						CreateVariableDialog.this.listCombo.select(0);
					}
					if (CreateVariableDialog.this.publicButton.getSelection()) {
						CreateVariableDialog.this.getterAdderButton.setEnabled(false);
						CreateVariableDialog.this.getterAdderButton.setSelection(false);
					} else {
						CreateVariableDialog.this.getterAdderButton.setEnabled(true);
					}

					final String tmpType = CreateVariableDialog.this.definedClassType.getText();
					if (tmpType.indexOf(DOT) > -1) {
						CreateVariableDialog.this.definedClassType.setText("List<"
								+ tmpType.substring(tmpType.lastIndexOf(DOT) + 1, tmpType.length()) + ">");

					} else if (tmpType.indexOf("<") > -1) {
						CreateVariableDialog.this.definedClassType.setText("List<"
								+ tmpType.substring(tmpType.lastIndexOf("<") + 1, tmpType.length() - 1) + ">");
					} else if (tmpType.indexOf("[") > -1) {
						CreateVariableDialog.this.definedClassType.setText("List<" + tmpType.substring(0, tmpType.lastIndexOf("[")) + ">");
					}
					CreateVariableDialog.this.arrayDim.setEnabled(false);
					CreateVariableDialog.this.arrayDim.select(0);

				} else {
					CreateVariableDialog.this.listCombo.setEnabled(false);
					CreateVariableDialog.this.setCombo.setEnabled(false);
					CreateVariableDialog.this.listCombo.select(0);
					CreateVariableDialog.this.setCombo.select(0);
					CreateVariableDialog.this.getterAdderButton.setEnabled(false);
					CreateVariableDialog.this.getterAdderButton.setSelection(false);
					// CreateVariableDialog.this.initializeButton.setSelection(true);
					CreateVariableDialog.this.initializeButton.setEnabled(true);
					CreateVariableDialog.this.initialText.setEnabled(true);
				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
			}
		});
		this.listCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (CreateVariableDialog.this.listCombo.getSelectionIndex() == 0) {
					CreateVariableDialog.this.listCombo.select(1);
				}
			}
		});
		this.setButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				if (CreateVariableDialog.this.setButton.getSelection()) {

					CreateVariableDialog.this.listCombo.setEnabled(false);
					CreateVariableDialog.this.initializeButton.setEnabled(true);

					CreateVariableDialog.this.initialText.setEnabled(false);
					if (CreateVariableDialog.this.initializeButton.getSelection()) {
						CreateVariableDialog.this.setCombo.setEnabled(true);
						CreateVariableDialog.this.initialText.setEnabled(false);
						CreateVariableDialog.this.setCombo.select(1);
					} else {
						CreateVariableDialog.this.setCombo.setEnabled(false);
						CreateVariableDialog.this.setCombo.select(0);
						// CreateVariableDialog.this.initialText.setEnabled(true);
					}
					if (CreateVariableDialog.this.publicButton.getSelection()) {
						CreateVariableDialog.this.getterAdderButton.setEnabled(false);
						CreateVariableDialog.this.getterAdderButton.setSelection(false);
					} else {
						CreateVariableDialog.this.getterAdderButton.setEnabled(true);
					}

					final String tmpType = CreateVariableDialog.this.definedClassType.getText();
					if (tmpType.indexOf(DOT) > -1) {
						CreateVariableDialog.this.definedClassType.setText("Set<"
								+ tmpType.substring(tmpType.lastIndexOf(DOT) + 1, tmpType.length()) + ">");

					} else if (tmpType.indexOf("<") > -1) {
						CreateVariableDialog.this.definedClassType.setText("Set<"
								+ tmpType.substring(tmpType.lastIndexOf("<") + 1, tmpType.length() - 1) + ">");
					} else if (tmpType.indexOf("[") > -1) {
						CreateVariableDialog.this.definedClassType.setText("Set<" + tmpType.substring(0, tmpType.lastIndexOf("[")) + ">");
					}
					CreateVariableDialog.this.arrayDim.setEnabled(false);
					CreateVariableDialog.this.arrayDim.select(0);

				} else {
					CreateVariableDialog.this.setCombo.setEnabled(false);
					CreateVariableDialog.this.listCombo.setEnabled(false);
					CreateVariableDialog.this.getterAdderButton.setEnabled(false);
					CreateVariableDialog.this.getterAdderButton.setSelection(false);
					CreateVariableDialog.this.initializeButton.setEnabled(true);
					CreateVariableDialog.this.initialText.setEnabled(true);
					CreateVariableDialog.this.listCombo.select(0);
					CreateVariableDialog.this.setCombo.select(0);
				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
			}
		});
		this.setCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (CreateVariableDialog.this.setCombo.getSelectionIndex() == 0) {
					CreateVariableDialog.this.setCombo.select(1);
				}
			}
		});

		this.notReqButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				if (CreateVariableDialog.this.notReqButton.getSelection()) {
					CreateVariableDialog.this.arrayDim.setEnabled(false);
					CreateVariableDialog.this.arrayDim.select(0);
					final String tmpType = CreateVariableDialog.this.type;
					if (tmpType != null && (tmpType.equals("String") || tmpType.equals("Date"))) {
						CreateVariableDialog.this.definedClassType.setText(premitiveWrapperMap.get(tmpType));
					} else {
						CreateVariableDialog.this.definedClassType.setText(tmpType);
					}

				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
			}
		});
		this.arrayButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				CreateVariableDialog.this.initialText.setText(CreateVariableDialog.initialValueMap.get("array"));
				if (CreateVariableDialog.this.arrayButton.getSelection()) {
					CreateVariableDialog.this.arrayDim.setEnabled(true);
					CreateVariableDialog.this.arrayDim.select(1);
					final String tmpType = CreateVariableDialog.this.definedClassType.getText();
					if (tmpType.indexOf(DOT) > -1) {
						CreateVariableDialog.this.definedClassType.setText(tmpType.substring(tmpType.lastIndexOf(DOT) + 1, tmpType.length())
								+ "[]");

					} else if (tmpType.indexOf("<") > -1) {
						CreateVariableDialog.this.definedClassType.setText(tmpType.substring(tmpType.lastIndexOf("<") + 1,
								tmpType.length() - 1) + "[]");
					} else if (tmpType.indexOf("[") > -1) {
						CreateVariableDialog.this.definedClassType.setText(tmpType.substring(0, tmpType.lastIndexOf("[")) + "[]");
					}

				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
			}
		});
		this.arrayDim = new Combo(radioBox, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		final GridData gridData = new GridData();
		this.arrayDim.setLayoutData(gridData);
		this.arrayDim.add("Array Dimension");
		this.arrayDim.add("1");
		this.arrayDim.add("2");
		this.arrayDim.add("3");
		this.arrayDim.add("4");
		this.arrayDim.add("5");
		this.arrayDim.setEnabled(false);
		this.arrayDim.select(0);
		this.arrayDim.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (CreateVariableDialog.this.arrayDim.getSelectionIndex() == 0) {
					CreateVariableDialog.this.arrayDim.select(1);
				}
			}
		});

		//dummy label just for alignment
		final Label lbl = new Label(radioBox, SWT.NONE);
		final GridData lblData = new GridData();
		lblData.horizontalAlignment = SWT.FILL;
		lblData.horizontalSpan = 2;
		lbl.setLayoutData(lblData);
	}

	/**
	 * @param parent
	 */
	protected void createModifierButtons(final Composite composite) {
		final Group group1 = new Group(composite, SWT.SHADOW_IN);
		group1.setText("Access Modifier :");
		final Device device = Display.getCurrent();
		final FontData[] fD = composite.getFont().getFontData();
		System.out.println(fD[0].getName() + fD[0].getHeight());
		//group1.setFont(new Font(device, fD[0].getName(), fD[0].getHeight(), SWT.BOLD));
		group1.setFont(FastCodeFont.getBoldFont(fD[0].getName(), fD[0].getHeight()));

		/*final RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		group1.setLayout(rowLayout);*/
		final Composite radioBox = group1;
		final GridLayout layout = new GridLayout();
		//layout.horizontalSpacing = 8;
		layout.numColumns = 7;
		layout.makeColumnsEqualWidth = true;
		radioBox.setLayout(layout);
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		radioBox.setLayoutData(gd);

		this.staticButton = new Button(radioBox, SWT.CHECK);
		this.staticButton.setText("STATIC");
		//final String variableName = null;
		this.staticButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}

			@Override
			public void widgetSelected(final SelectionEvent e) {

				if (CreateVariableDialog.this.staticButton.getSelection()) {

					if (!isEmpty(CreateVariableDialog.this.origFieldName) && CreateVariableDialog.this.finalButton.getSelection()) {
						CreateVariableDialog.this.variableName = CreateVariableDialog.this.origFieldName;
						final String changedfieldName = modifyVarName(CreateVariableDialog.this.origFieldName);
						CreateVariableDialog.this.fieldType.setText(changedfieldName);
						if (isEmpty(CreateVariableDialog.this.initialText.getText())
								&& !CreateVariableDialog.this.initializeButton.getSelection()) {
							CreateVariableDialog.this.setErrorMessage("Variable is not initialized", false, EMPTY_STR);
						} else {
							CreateVariableDialog.this.setErrorMessage("Variable is not initialized", true, EMPTY_STR);
						}
					}
					if (CreateVariableDialog.this.staticButton.getSelection() && CreateVariableDialog.this.builderPattern.getSelection()) {
						CreateVariableDialog.this.setErrorMessage(CreateVariableDialog.this.builderErrorMsg, false, EMPTY_STR);
					}
				} else {

					if (CreateVariableDialog.this.builderPattern.getSelection()
							&& !CreateVariableDialog.this.defaultMessage.equals(CreateVariableDialog.this.errorMessage)) {
						CreateVariableDialog.this.setErrorMessage(CreateVariableDialog.this.defaultMessage, false, EMPTY_STR);
					}
					if (!isEmpty(CreateVariableDialog.this.origFieldName)) {
						//CreateVariableDialog.this.fieldType.setText(changeFirstLetterToLowerCase(CreateVariableDialog.this.chngdFieldName));
						CreateVariableDialog.this.fieldType.setText(CreateVariableDialog.this.variableName);
					}
					if (CreateVariableDialog.this.publicButton.getSelection()
							|| CreateVariableDialog.this.publicStaticFinalButton.getSelection()) {
						CreateVariableDialog.this.publicStaticFinalButton.setSelection(false);
					}
				}
			}

		});
		this.finalButton = new Button(radioBox, SWT.CHECK);
		this.finalButton.setText("FINAL");

		this.finalButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				CreateVariableDialog.this.noneButton.setEnabled(true);
				if (CreateVariableDialog.this.finalButton.getSelection()) {
					CreateVariableDialog.this.countFinal = 1;
					// CreateVariableDialog.this.doNotChange.setEnabled(false);
					if (CreateVariableDialog.this.publicButton.getSelection()
							|| CreateVariableDialog.this.publicStaticFinalButton.getSelection()) {
						CreateVariableDialog.this.getterButton.setEnabled(false);
						CreateVariableDialog.this.getterButton.setSelection(false);
					} else {
						CreateVariableDialog.this.getterButton.setEnabled(true);
					}
					//CreateVariableDialog.this.setterButton.setEnabled(false);
					//CreateVariableDialog.this.setterButton.setSelection(false);
					CreateVariableDialog.this.getterSetterButton.setEnabled(false);
					CreateVariableDialog.this.getterSetterButton.setSelection(false);
					CreateVariableDialog.this.builderPattern.setSelection(false);
					if (!isEmpty(CreateVariableDialog.this.origFieldName) && CreateVariableDialog.this.staticButton.getSelection()) {
						CreateVariableDialog.this.variableName = CreateVariableDialog.this.origFieldName;
						final String changedfieldName = modifyVarName(CreateVariableDialog.this.origFieldName);
						CreateVariableDialog.this.fieldType.setText(changedfieldName);
						if (isEmpty(CreateVariableDialog.this.initialText.getText())
								&& !CreateVariableDialog.this.initializeButton.getSelection()) {
							CreateVariableDialog.this.setErrorMessage("Variable is not initialized", false, EMPTY_STR);
						} else {
							CreateVariableDialog.this.setErrorMessage("Variable is not initialized", true, EMPTY_STR);
						}
					}
				} else {
					CreateVariableDialog.this.countFinal = 0;
					// CreateVariableDialog.this.doNotChange.setEnabled(true);

					if (CreateVariableDialog.this.publicButton.getSelection()
							|| CreateVariableDialog.this.publicStaticFinalButton.getSelection()) {
						CreateVariableDialog.this.getterButton.setEnabled(false);
						//CreateVariableDialog.this.setterButton.setEnabled(false);
						CreateVariableDialog.this.getterSetterButton.setEnabled(false);
						CreateVariableDialog.this.getterButton.setSelection(false);
						//CreateVariableDialog.this.setterButton.setSelection(false);
						CreateVariableDialog.this.getterSetterButton.setSelection(false);
						CreateVariableDialog.this.publicStaticFinalButton.setSelection(false);
					} else {
						CreateVariableDialog.this.getterButton.setEnabled(true);
						//CreateVariableDialog.this.setterButton.setEnabled(true);
						CreateVariableDialog.this.getterSetterButton.setEnabled(true);

					}

					if (!isEmpty(CreateVariableDialog.this.origFieldName)) {
						// CreateVariableDialog.this.fieldType.setText(changeFirstLetterToLowerCase(CreateVariableDialog.this.chngdFieldName));
						CreateVariableDialog.this.fieldType.setText(CreateVariableDialog.this.variableName);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
			}
		});

		this.publicStaticFinalButton = new Button(radioBox, SWT.CHECK);
		this.publicStaticFinalButton.setText("public static final");

		this.publicStaticFinalButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				final boolean selected = CreateVariableDialog.this.publicStaticFinalButton.getSelection();
				if (selected) {
					CreateVariableDialog.this.countFinal = 1;
					CreateVariableDialog.this.staticButton.setSelection(true);
					CreateVariableDialog.this.finalButton.setSelection(true);
					CreateVariableDialog.this.publicButton.setSelection(true);
					CreateVariableDialog.this.privateButton.setSelection(false);
					CreateVariableDialog.this.protectedButton.setSelection(false);
					CreateVariableDialog.this.defaultButton.setSelection(false);
					CreateVariableDialog.this.getterButton.setEnabled(false);
					CreateVariableDialog.this.getterSetterButton.setEnabled(false);
					CreateVariableDialog.this.getterButton.setSelection(false);
					CreateVariableDialog.this.getterSetterButton.setSelection(false);
					CreateVariableDialog.this.noneButton.setEnabled(false);
					CreateVariableDialog.this.builderPattern.setEnabled(false);

					if (isEmpty(CreateVariableDialog.this.initialText.getText())
							&& !CreateVariableDialog.this.initializeButton.getSelection()) {
						CreateVariableDialog.this.setErrorMessage("Variable is not initialized", false, EMPTY_STR);
					} else {
						CreateVariableDialog.this.setErrorMessage("Variable is not initialized", true, EMPTY_STR);
					}
					if (!isEmpty(CreateVariableDialog.this.origFieldName)) {
						CreateVariableDialog.this.variableName = CreateVariableDialog.this.origFieldName;
						final String changedfieldName = modifyVarName(CreateVariableDialog.this.origFieldName);
						CreateVariableDialog.this.fieldType.setText(changedfieldName);
					}
					if (CreateVariableDialog.this.staticButton.getSelection() && CreateVariableDialog.this.builderPattern.getSelection()) {
						CreateVariableDialog.this.setErrorMessage(CreateVariableDialog.this.builderErrorMsg, false, EMPTY_STR);
					}

				} else {

					if (CreateVariableDialog.this.builderPattern.getSelection()
							&& !CreateVariableDialog.this.defaultMessage.equals(CreateVariableDialog.this.errorMessage)) {
						CreateVariableDialog.this.setErrorMessage(CreateVariableDialog.this.defaultMessage, false, EMPTY_STR);
					}
					CreateVariableDialog.this.countFinal = 0;
					CreateVariableDialog.this.staticButton.setSelection(false);
					CreateVariableDialog.this.finalButton.setSelection(false);
					CreateVariableDialog.this.publicButton.setSelection(false);

					CreateVariableDialog.this.getterButton.setEnabled(true);
					//CreateVariableDialog.this.setterButton.setEnabled(true);
					CreateVariableDialog.this.getterSetterButton.setEnabled(true);
					CreateVariableDialog.this.noneButton.setEnabled(true);
					CreateVariableDialog.this.builderPattern.setEnabled(true);

					if (!isEmpty(CreateVariableDialog.this.origFieldName)) {
						//CreateVariableDialog.this.fieldType.setText(changeFirstLetterToLowerCase(CreateVariableDialog.this.chngdFieldName));
						CreateVariableDialog.this.fieldType.setText(CreateVariableDialog.this.variableName);
					}
					CreateVariableDialog.this.privateButton.setSelection(true);
					CreateVariableDialog.this.setErrorMessage(CreateVariableDialog.this.errorMessage, true, EMPTY_STR);

				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
			}
		});

		final Label blank1 = new Label(radioBox, SWT.NONE);
		final Label blank2 = new Label(radioBox, SWT.NONE);
		final Label blank3 = new Label(radioBox, SWT.NONE);
		final Label blank4 = new Label(radioBox, SWT.NONE);
		blank1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		blank2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		blank3.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		blank4.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	/**
	 * @param parent
	 */
	private void createChangeToWrapperButton(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout(6, false);
		composite.setLayout(layout);
		this.changeToWrapper = new Button(composite, SWT.CHECK);
		this.changeToWrapper.setText("Use Wrapper Class");
		this.changeToWrapper.setEnabled(false);
		final GridData gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		this.changeToWrapper.setLayoutData(gridData);
		this.changeToWrapper.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (CreateVariableDialog.this.changeToWrapper.getSelection()) {
					CreateVariableDialog.this.definedClassType.setText(CreateVariableDialog.premitiveWrapperMap
							.get(CreateVariableDialog.this.type));
					CreateVariableDialog.this.listButton.setEnabled(true);
					CreateVariableDialog.this.setButton.setEnabled(true);
				} else {
					CreateVariableDialog.this.definedClassType.setText(CreateVariableDialog.this.type);
					CreateVariableDialog.this.listButton.setEnabled(false);
					CreateVariableDialog.this.setButton.setEnabled(false);
					CreateVariableDialog.this.listCombo.setEnabled(false);
					CreateVariableDialog.this.setCombo.setEnabled(false);
					CreateVariableDialog.this.getterAdderButton.setEnabled(false);
					CreateVariableDialog.this.getterAdderButton.setSelection(false);
					CreateVariableDialog.this.listButton.setSelection(false);
					CreateVariableDialog.this.setButton.setSelection(false);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
		});
		final Label blank = new Label(composite, SWT.NONE);
		final GridData blankData = new GridData();
		blankData.horizontalAlignment = SWT.FILL;
		blankData.horizontalSpan = 2;
		blank.setLayoutData(blankData);
	}

	/**
	 * @param parent
	 */
	private void createInitializePane(final Composite cmposite) {
		final Composite composite = new Composite(cmposite, cmposite.getStyle());
		final GridLayout layout = new GridLayout(6, false);
		composite.setLayout(layout);
		this.initializeButton = this.createButton(composite, "initialize", SWT.CHECK);
		this.initializeButton.setText("initialize");
		this.initializeButton.setSelection(false);

		final Label valueLabel = new Label(cmposite, SWT.NONE);
		valueLabel.setText("Value:");

		this.initialText = new Text(cmposite, SWT.BORDER | SWT.MULTI);
		final GridData griddata = new GridData();
		this.initialText.setLayoutData(griddata);
		this.initialText.setEnabled(false);
		this.initialText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				final String initialValue = CreateVariableDialog.this.initialText.getText();
				if (!isEmpty(initialValue) && !CreateVariableDialog.this.createVariableData.isCreateFieldSimple()) {
					if (CreateVariableDialog.this.staticButton.getSelection() && CreateVariableDialog.this.finalButton.getSelection()
							|| CreateVariableDialog.this.publicStaticFinalButton.getSelection()
							|| CreateVariableDialog.this.initializeButton.getSelection()) {
						CreateVariableDialog.this.setErrorMessage("Variable is not initialized", true, EMPTY_STR);
					}
				} else {
					CreateVariableDialog.this.setErrorMessage("Variable is not initialized", false, EMPTY_STR);
				}
			}
		});
		this.initializeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent selectionEvent) {
				if (CreateVariableDialog.this.initializeButton.getSelection()) {
					CreateVariableDialog.this.initialText.setEnabled(true);

					if (CreateVariableDialog.this.listButton != null && CreateVariableDialog.this.listButton.getSelection()) {
						CreateVariableDialog.this.listCombo.setEnabled(true);
						CreateVariableDialog.this.listCombo.select(1);
						CreateVariableDialog.this.initialText.setEnabled(false);
					} else if (CreateVariableDialog.this.setButton != null && CreateVariableDialog.this.setButton.getSelection()) {
						CreateVariableDialog.this.setCombo.setEnabled(true);
						CreateVariableDialog.this.initialText.setEnabled(false);
						CreateVariableDialog.this.listCombo.select(0);
						CreateVariableDialog.this.setCombo.select(1);
					}
					if (CreateVariableDialog.this.type != null) {
						if (CreateVariableDialog.this.isDefined) {
							CreateVariableDialog.this.initialText.setText(CreateVariableDialog.initialValueMap.get("defined"));
						} else if (CreateVariableDialog.this.arrayButton != null && CreateVariableDialog.this.arrayButton.getSelection()) {
							CreateVariableDialog.this.initialText.setText(CreateVariableDialog.initialValueMap.get("array"));
						} else {

							CreateVariableDialog.this.initialText.setText(CreateVariableDialog.initialValueMap
									.get(CreateVariableDialog.this.type));
						}
					}

				} else {
					if (CreateVariableDialog.this.staticButton.getSelection() && CreateVariableDialog.this.finalButton.getSelection()
							|| CreateVariableDialog.this.publicStaticFinalButton.getSelection()) {
						CreateVariableDialog.this.setErrorMessage("Variable is not initialized", false, EMPTY_STR);
					} else {
						CreateVariableDialog.this.setErrorMessage("Variable is not initialized", true, EMPTY_STR);
					}

					CreateVariableDialog.this.initialText.setEnabled(false);
					if (!CreateVariableDialog.this.createVariableData.isCreateClassDetailed()) {
						CreateVariableDialog.this.setCombo.setEnabled(false);
						CreateVariableDialog.this.listCombo.setEnabled(false);
						CreateVariableDialog.this.listCombo.select(0);
						CreateVariableDialog.this.setCombo.select(0);
					}
				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent selectionEvent) {
			}
		});
		final Label blank = new Label(composite, SWT.NONE);
		final GridData blankData = new GridData();
		blankData.horizontalAlignment = SWT.FILL;
		blankData.horizontalSpan = 3;
		blank.setLayoutData(blankData);
	}

	/**
	 * @param parent
	 */
	private void createDoNotChangeButton(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout(6, false);
		composite.setLayout(layout);

		this.doNotChange = new Button(composite, SWT.CHECK);
		this.doNotChange.setText("Do not change field name");
		final GridData gridData = new GridData();
		this.doNotChange.setLayoutData(gridData);
		this.doNotChange.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (CreateVariableDialog.this.doNotChange.getSelection()) {
					if (!isEmpty(CreateVariableDialog.this.origFieldName)) {
						CreateVariableDialog.this.fieldType.setText(CreateVariableDialog.this.origFieldName);
					}
				} else {
					CreateVariableDialog.this.fieldType.setText(changeFirstLetterToLowerCase(CreateVariableDialog.this.chngdFieldName));
				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
		});
		if (!this.createVariableData.isCreateClassSimple()) {
			createChangeToWrapperButton(parent);
		} else {
			final Label blank = new Label(composite, SWT.NONE);
			final GridData blankData = new GridData();
			blankData.horizontalAlignment = SWT.FILL;
			blankData.horizontalSpan = 4;
			blank.setLayoutData(blankData);
		}

	}

	/**
	 * * getter method for createVariableData
	 *
	 * @return
	 *
	 */
	public CreateVariableData getCreateVariableData() {
		return this.createVariableData;
	}

	/**
	 * @param parent
	 */
	private void createBuilderButton(final Composite parent) {
		this.builderPattern = new Button(parent, SWT.RADIO);
		this.builderPattern.setText("Builder Pattern");
		this.builderPattern.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (CreateVariableDialog.this.builderPattern.getSelection()
						&& (CreateVariableDialog.this.staticButton.getSelection() || CreateVariableDialog.this.publicStaticFinalButton
								.getSelection())) {
					CreateVariableDialog.this.setErrorMessage(CreateVariableDialog.this.builderErrorMsg, false, EMPTY_STR);
				} else {
					CreateVariableDialog.this.setErrorMessage(CreateVariableDialog.this.builderErrorMsg, true, EMPTY_STR);
				}
			}
		});
	}

	/**
	 *
	 */
	public void setDefualtValues() {
		if (this.createVariableData.isVariableModifyAction()) {
			this.fieldName = this.createVariableData.getFieldNames()[0];
			this.fieldType.setText(this.fieldName);
			final ACCESS_MODIFIER access_modifier = this.createVariableData.getAccessModifier();
			this.privateButton.setSelection(false);
			if (access_modifier.equals(ACCESS_MODIFIER.PRIVATE)) {
				this.privateButton.setSelection(true);
			} else if (access_modifier.equals(ACCESS_MODIFIER.PUBLIC)) {

				this.publicButton.setSelection(true);
			} else if (access_modifier.equals(ACCESS_MODIFIER.PROTECTED)) {
				this.protectedButton.setSelection(true);
			} else if (access_modifier.equals(ACCESS_MODIFIER.DEFAULT)) {
				this.defaultButton.setSelection(true);
			}

			if (this.createVariableData.isStatic()) {
				this.staticButton.setSelection(true);
			}
			if (this.createVariableData.isFinal()) {
				this.finalButton.setSelection(true);
			}
			if (this.createVariableData.isStatic() && this.createVariableData.isFinal() && access_modifier.equals(ACCESS_MODIFIER.PUBLIC)) {
				this.publicStaticFinalButton.setSelection(true);
			}
			this.type = this.createVariableData.getFieldType();
			if (this.createVariableData.isInitialized()) {
				this.initializeButton.setSelection(true);
				this.initializeButton.setEnabled(true);
				this.initialText.setEnabled(true);
				this.initialText.setText(isEmpty(this.createVariableData.getInitialValue()) ? initialValueMap.get(this.type)
						: this.createVariableData.getInitialValue());
			}
			if (this.createVariableData.getGetterSetter() != null) {
				final GETTER_SETTER getterSetter = this.createVariableData.getGetterSetter();
				this.getterButton.setSelection(getterSetter.equals(GETTER_SETTER.GETTER_EXIST));
				//this.setterButton.setSelection(getterSetter.equals(GETTER_SETTER.SETTER_EXIST));
				this.getterSetterButton.setSelection(getterSetter.equals(GETTER_SETTER.GETTER_SETTER_EXIST));
			}
			this.builderPattern.setSelection(this.createVariableData.isBuilderReqd());
			if (this.type.equals("String") || this.type.equals("Date")) {
				this.definedClassType.setText(premitiveWrapperMap.get(this.createVariableData.getFieldType()));
			} else {
				this.definedClassType.setText(this.createVariableData.getFieldType());
			}
			if (!this.createVariableData.isCreateClassSimple()) {
				this.arrayButton.setSelection(this.createVariableData.isArray());
				this.listButton.setSelection(this.createVariableData.isList());
				this.setButton.setSelection(this.createVariableData.isSet());
				if (this.createVariableData.isList() && this.createVariableData.isInitialized()) {
					this.listCombo.setEnabled(true);
					if (this.createVariableData.getListType().equals("LinkedList")) {
						this.listCombo.select(2);
					} else {
						this.listCombo.select(1);
					}
				}
				if (this.createVariableData.isSet() && this.createVariableData.isInitialized()) {
					this.setCombo.setEnabled(true);
					if (this.createVariableData.getSetType().equals("TreeSet")) {
						this.setCombo.select(3);
					} else if (this.createVariableData.getSetType().equals("LinkedHashSet")) {
						this.setCombo.select(2);
					} else {
						this.setCombo.select(1);
					}
				}
				if (this.arrayButton.getSelection()) {
					this.arrayDim.setEnabled(true);
					this.arrayDim.select(this.createVariableData.getArrayDim());
				}
				final String type = CreateVariableDialog.this.definedClassType.getText();
				if (this.listButton.getSelection()) {
					if (type.indexOf(DOT) > -1) {
						CreateVariableDialog.this.definedClassType.setText("List<"
								+ type.substring(type.lastIndexOf(DOT) + 1, type.length()) + ">");
					}
				}
				if (this.setButton.getSelection()) {
					if (type.indexOf(DOT) > -1) {
						CreateVariableDialog.this.definedClassType.setText("Set<"
								+ type.substring(type.lastIndexOf(DOT) + 1, type.length()) + ">");
					}
				}
				if (this.arrayButton.getSelection()) {
					if (type.indexOf(DOT) > -1) {
						CreateVariableDialog.this.definedClassType.setText(type.substring(type.lastIndexOf(DOT) + 1, type.length()) + "[]");
					}
				}
			}
			if (premitiveWrapperMap.containsKey(this.createVariableData.getFieldType())) {
				this.primitivesRadioButton.setPreferenceName(P_RADIO_GROUP_EDITOR_VALUE);
				this.primitivesRadioButton.setPreferenceStore(this.preferenceStore);
				this.preferenceStore.setValue(P_RADIO_GROUP_EDITOR_VALUE, this.createVariableData.getFieldType());
				this.primitivesRadioButton.load();
			}

			int pos = 1;
			if (this.createVariableData.getInsertionPoint() != null) {

				for (final IField field : this.createVariableData.getiClassFields()) {
					pos++;
					if (this.createVariableData.getInsertionPoint().getElementName().equals(field.getElementName())) {
						break;
					}

				}

			}
			if (this.createVariableData.getStringInsertionPoint() != null
					&& this.createVariableData.getStringInsertionPoint().equals("First Member")) {
				pos = 0;
			}
			this.insertionPointCombo.select(pos);
			if (this.createVariableData.getInsertionPoint() != null) {
				this.createVariableData.setStringInsertionPoint(this.createVariableData.getInsertionPoint().getElementName());
			}
			// this.insertionPointCombo.setEnabled(false);

		}
	}

	/**
	 * @param originalFieldName
	 * @return
	 */
	protected String modifyVarName(final String originalFieldName) {
		final StringBuilder modifiedFieldNames = new StringBuilder();
		for (final String fieldName : originalFieldName.split("\\s+")) {
			if (!isEmpty(this.fieldNameWithUnderScore)) {
				for (final String name : this.fieldNameWithUnderScore.split("\\s+")) {
					if (!isEmpty(name)) {
						if (fieldName.equalsIgnoreCase(name)) {
							modifiedFieldNames.append(EMPTY_STR.equals(modifiedFieldNames.toString()) ? changeToCamelCase(fieldName,
									UNDER_SCORE).toUpperCase() : SPACE + changeToCamelCase(fieldName, UNDER_SCORE).toUpperCase());
						} else {
							modifiedFieldNames.append(EMPTY_STR.equals(modifiedFieldNames.toString()) ? fieldName.toUpperCase() : SPACE
									+ fieldName.toUpperCase());
						}
					}
				}
			} else {
				modifiedFieldNames.append(EMPTY_STR.equals(modifiedFieldNames.toString()) ? fieldName.toUpperCase() : SPACE
						+ fieldName.toUpperCase());
			}
		}
		return modifiedFieldNames.toString();
	}

	/**
	 *
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Create Variable");

		shell.setFullScreen(true);
		final CreateVariableDialog createVariableDialog = new CreateVariableDialog(shell);

		createVariableDialog.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// System.out.println(createVariableDialog.fieldName);
		display.dispose();
	}

	/**
	 * @param errorMessage
	 * @param removerMessage
	 */
	public void setErrorMessage(String errorMessage, final boolean removerMessage, final String fieldnames) {
		this.errorMessage = errorMessage;
		final String tmpError = this.errorMessageText.getText();
		if (this.errorMessageText != null && !this.errorMessageText.isDisposed()) {
			if (errorMessage == null) {
				this.errorMessageText.setText(" \n ");
			} else {
				if (isEmpty(this.errorMessageText.getText()) && !removerMessage) {
					this.errorMessageText.setText(errorMessage);
				} else {
					if (removerMessage) {
						if (errorMessage.equals(this.errorMsgVariableExist)) {
							errorMessage = errorMessage + fieldnames;
						}
						this.errorMessageText.setText(this.errorMessageText.getText().replace(errorMessage, this.defaultMessage.trim()));
					} else if (!this.errorMessageText.getText().contains(errorMessage)) {
						this.errorMessageText.setText(this.errorMessageText.getText() + NEWLINE + errorMessage);
					}

				}
			}

			if (errorMessage.equals(this.errorMsgVariableExist) && tmpError.contains(this.errorMsgVariableExist)) {
				final String toReplace = tmpError.substring(0,
						tmpError.indexOf(this.errorMsgVariableExist) + this.errorMsgVariableExist.length());
				this.errorMessageText.setText(toReplace);

			}
			if (!fieldnames.equals(EMPTY_STR) && !removerMessage) {
				this.errorMessageText.setText(this.errorMessageText.getText() + fieldnames);
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

				button.setEnabled(errorMessage == this.defaultMessage);
			}
		}
	}

}

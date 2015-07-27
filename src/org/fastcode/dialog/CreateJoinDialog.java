package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.HYPHEN;
import static org.fastcode.common.FastCodeConstants.ORACLE;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_WITH_JOIN;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_WITH_JOIN_AND_MAPPER;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES;
import static org.fastcode.util.DatabaseUtil.getSchemaFromDb;
import static org.fastcode.util.DatabaseUtil.getTableColumnsFromDB;
import static org.fastcode.util.DatabaseUtil.getTableFromDb;
import static org.fastcode.util.FastCodeUtil.getEmptyArrayForNull;
import static org.fastcode.util.SourceUtil.getTypeFromWorkspace;
import static org.fastcode.util.StringUtil.isEmpty;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.fastcode.common.CreateJoinData;
import org.fastcode.common.DatabaseDetails;
import org.fastcode.common.FastCodeColor;
import org.fastcode.common.FastCodeConstants.FIELDS;
import org.fastcode.common.FastCodeConstants.JOIN_TYPES;
import org.fastcode.common.FastCodeConstants.NUMBER_OF_JOIN_TABLES;
import org.fastcode.common.FastCodeConstants.WHERE_CLAUSE_SEPARATOR;
import org.fastcode.popup.actions.snippet.FastCodeCache;
import org.fastcode.preferences.DatabaseConnectionSettings;
import org.fastcode.templates.viewer.TemplateFieldEditor;
import org.fastcode.util.ConnectToDatabase;
import org.fastcode.util.DatabaseCache;
import org.fastcode.util.FastCodeContentProposalProvider;

public class CreateJoinDialog extends TrayDialog {

	Shell							shell;
	private final IPreferenceStore	preferenceStore;
	private CreateJoinData			createJoinData;
	/*private Button					equalButton;
	private Button					notEqualButton;
	private Button					likeButton;
	private Button					notLikeButton;*/
	private Button					andButton;
	private Button					orButton;
	private String					errorMessage;
	private Text					errorMessageText;
	private final String			defaultMessage		= "\n";
	private Combo					firstTableCombo;
	private Text					firstTableInstanceName;
	private Text					secondTableInstanceName;
	private Combo					secondTableCombo;
	private Combo					thirdTableCombo;
	private Combo					copyOfThirdTableCombo;
	private Text					thirdTableInstanceName;
	private Text					copyOfThirdTableInstanceName;
	private Button					innerJoinButton;
	//private Button					outerJoinButton;
	private Button					leftJoinButton;
	private Button					rightJoinButton;
	//private Button					selfJoinButton;
	private Combo					columnsInFirstTableCombo;
	private Combo					columnsInSecondTableCombo;
	private Combo					columnsInThirdTableCombo;
	private Combo					columnsInCopyOfThirdTableCombo;
	private String					firstTableName;
	private String					secondTableName;
	private String					thirdTableName;
	private String					copyOfThirdTableName;
	private TemplateFieldEditor		templateBodyMultiText;
	private String					modifiedTemplateBody;
	private Button					twoTablesJoinButton;
	private Button					threeTablesJoinButton;
	private Text					messageText;
	private String					message;

	private Button					groupByButton;
	private Button					orderByButton;

	//private String					selectedTable1Instance;
	//private String					selectedTable2Instance;
	private Combo					pojoClassCombo;
	private Button					pojoClassBrowseButton;
	private Combo					firstSchemaCombo;
	private Combo					secondSchemaCombo;
	private Combo					thirdSchemaCombo;
	private Combo					fourthSchemaCombo;
	private Button					useAliasNameButton;
	Map<String, Object>				browsedPojoClassMap	= new HashMap<String, Object>();
	private Combo					databaseNameCombo;

	/**
	 * @param shell
	 */
	public CreateJoinDialog(final Shell shell) {
		super(shell);
		this.shell = shell;
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
	}

	/**
	 * @param shell
	 * @param createJoinData
	 */
	public CreateJoinDialog(final Shell shell, final CreateJoinData createJoinData) {
		super(shell);
		this.shell = shell;
		this.createJoinData = createJoinData;
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
	}

	@Override
	public void create() {
		super.create();
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Create Join Dialog");
		//shell.setSize(900, 500);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);
		createMessageText(parent);
		createErrorMessageText(parent);
		createNumberOfJoinTables(parent);
		createDatabaseNameSelectionPane(parent);
		createSchemaSelectionPane(parent);
		createTablesCombo(parent);
		createTableInstancesText(parent);

		createTablesColumnsCombo(parent);
		createJoinTypesRadio(parent);
		if (this.preferenceStore.getBoolean(P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES)) {
			createTemplateBodyMultiText(parent);
		}
		/*if (this.createJoinData.getTemplateSettings().getTemplateName().equals(DATABASE_TEMPLATE_SELECT_WITH_JOIN)) {
			createOrderByGroupByButtons(parent);
		}*/
		createWhereClauseSeparatorButtons(parent);
		if (this.createJoinData.getTemplateSettings().getTemplateName().equals(DATABASE_TEMPLATE_SELECT_WITH_JOIN)) {
			createAliasnameButton(parent);
		}
		if (this.createJoinData.getTemplateSettings().getTemplateName().equals(DATABASE_TEMPLATE_SELECT_WITH_JOIN_AND_MAPPER)) {
			createPojoClassSelectionPane(parent);
		}
		return parent;
	}

	@Override
	protected Control createButtonBar(final Composite parent) {
		return super.createButtonBar(parent);
	}

	@Override
	protected void okPressed() {

		if (this.createJoinData == null) {
			this.createJoinData = new CreateJoinData();
		}

		this.createJoinData.setFirstSchemaSelected(CreateJoinDialog.this.firstSchemaCombo.getText());
		this.createJoinData.setSecondSchemaSelected(CreateJoinDialog.this.secondSchemaCombo.getText());
		this.createJoinData.setThirdSchemaSelected(CreateJoinDialog.this.thirdSchemaCombo.getText());
		if (this.firstTableCombo.getSelectionIndex() == -1) {
			setErrorMessage("Please select join table1 ");
			return;
		} else if (this.secondTableCombo.getSelectionIndex() == -1) {
			setErrorMessage("Please select join  table2 ");
			return;
		} else {
			setErrorMessage(this.defaultMessage);
		}
		if (this.createJoinData.getNumberOfJoinTables().equals(NUMBER_OF_JOIN_TABLES.THREE)) {
			if (this.thirdTableCombo.getSelectionIndex() == -1) {
				setErrorMessage("Please select common join table");
				return;
			} else if (this.copyOfThirdTableCombo.getSelectionIndex() == -1) {
				setErrorMessage("Please select common join table");
				return;
			} else {
				setErrorMessage(this.defaultMessage);
			}
		}
		if (this.columnsInFirstTableCombo.getSelectionIndex() == -1) {
			setErrorMessage("Please Select one column for join Table1");
			return;
		} else if (this.columnsInSecondTableCombo.getSelectionIndex() == -1) {
			setErrorMessage("Please Select one column for join Table2");
			return;
		} else {
			setErrorMessage(this.defaultMessage);
		}
		if (this.createJoinData.getNumberOfJoinTables().equals(NUMBER_OF_JOIN_TABLES.THREE)) {
			if (this.columnsInThirdTableCombo.getSelectionIndex() == -1) {
				setErrorMessage("Please select one column for common join table");
				return;
			} else if (this.columnsInCopyOfThirdTableCombo.getSelectionIndex() == -1) {
				setErrorMessage("Please select one column for common join table");
				return;
			} else {
				setErrorMessage(this.defaultMessage);
			}
		}
		final String selectedTable1columnForJoin = this.columnsInFirstTableCombo.getItem(this.columnsInFirstTableCombo.getSelectionIndex());
		this.createJoinData.setSelectedTable1JoinColumn(selectedTable1columnForJoin.substring(0, selectedTable1columnForJoin.indexOf('-')));
		if (this.firstTableName.equals(this.secondTableName)) {
			final String selectedTable2ColumnForJoin = this.columnsInFirstTableCombo.getItem(this.columnsInFirstTableCombo
					.getSelectionIndex());
			this.createJoinData.setSelectedTable2JoinColumn(selectedTable2ColumnForJoin.substring(0,
					selectedTable2ColumnForJoin.indexOf('-')));
		} else {
			final String selectedTable2ColumnForJoin = this.columnsInSecondTableCombo.getItem(this.columnsInSecondTableCombo
					.getSelectionIndex());
			this.createJoinData.setSelectedTable2JoinColumn(selectedTable2ColumnForJoin.substring(0,
					selectedTable2ColumnForJoin.indexOf('-')));
		}

		if (this.innerJoinButton != null && this.innerJoinButton.getSelection()) {
			this.createJoinData.setJoinTypes(JOIN_TYPES.INNERJOIN);
		} else if (this.leftJoinButton != null && this.leftJoinButton.getSelection()) {
			this.createJoinData.setJoinTypes(JOIN_TYPES.LEFTJOIN);
		} else if (this.rightJoinButton != null && this.rightJoinButton.getSelection()) {
			this.createJoinData.setJoinTypes(JOIN_TYPES.RIGHTJOIN);
		}
		if (this.templateBodyMultiText != null) {
			this.modifiedTemplateBody = this.templateBodyMultiText.getStringValue();
			if (this.modifiedTemplateBody != null) {
				this.createJoinData.setModifiedTemplateBody(this.modifiedTemplateBody);
			} else {
				this.createJoinData.setModifiedTemplateBody(this.createJoinData.getTemplateSettings().getTemplateBody());
			}
		}
		if (this.createJoinData.getTemplateSettings().getTemplateName().equals(DATABASE_TEMPLATE_SELECT_WITH_JOIN_AND_MAPPER)) {
			if (this.pojoClassCombo != null && this.pojoClassCombo.isEnabled()) {
				if (isEmpty(this.pojoClassCombo.getText())) {
					setErrorMessage("Please choose a pojo class.");
					return;
				} else {
					if (this.createJoinData.getiSelectPojoClassType() != null) {
						final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
						final IType pojoClassType = this.createJoinData.getiSelectPojoClassType();
						if (!fastCodeCache.getTypeSet().contains(pojoClassType)) {
							fastCodeCache.getTypeSet().add(pojoClassType);
						}
					}
				}
			}
		}
		super.okPressed();
	}

	/**
	 * @param parent
	 */
	private void createNumberOfJoinTables(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Number of Join Tables:                                   ");
		this.twoTablesJoinButton = new Button(composite, SWT.RADIO);
		this.twoTablesJoinButton.setText("Two Tables");
		this.twoTablesJoinButton.setSelection(true);
		this.createJoinData.setNumberOfJoinTables(NUMBER_OF_JOIN_TABLES.TWO);
		this.twoTablesJoinButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				CreateJoinDialog.this.createJoinData.setNumberOfJoinTables(NUMBER_OF_JOIN_TABLES.TWO);
				CreateJoinDialog.this.thirdTableCombo.setEnabled(false);
				CreateJoinDialog.this.copyOfThirdTableCombo.setEnabled(false);
				CreateJoinDialog.this.thirdTableInstanceName.setEnabled(false);
				CreateJoinDialog.this.copyOfThirdTableInstanceName.setEnabled(false);
				CreateJoinDialog.this.columnsInThirdTableCombo.setEnabled(false);
				CreateJoinDialog.this.columnsInCopyOfThirdTableCombo.setEnabled(false);
				CreateJoinDialog.this.thirdSchemaCombo.setEnabled(false);
				CreateJoinDialog.this.fourthSchemaCombo.setEnabled(false);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

		this.threeTablesJoinButton = new Button(composite, SWT.RADIO);
		this.threeTablesJoinButton.setText("Three Tables");
		// this.threeTablesJoinButton.setSelection(false);
		this.threeTablesJoinButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				CreateJoinDialog.this.createJoinData.setNumberOfJoinTables(NUMBER_OF_JOIN_TABLES.THREE);
				//CreateJoinDialog.this.thirdTableCombo.setEnabled(true);
				//CreateJoinDialog.this.copyOfThirdTableCombo.setEnabled(true);
				CreateJoinDialog.this.thirdTableInstanceName.setEnabled(true);
				CreateJoinDialog.this.copyOfThirdTableInstanceName.setEnabled(true);
				CreateJoinDialog.this.columnsInThirdTableCombo.setEnabled(true);
				CreateJoinDialog.this.columnsInCopyOfThirdTableCombo.setEnabled(true);
				CreateJoinDialog.this.fourthSchemaCombo.setEnabled(true);
				CreateJoinDialog.this.thirdSchemaCombo.setEnabled(true);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
	}

	/**
	 * @param parent
	 */
	private void createTemplateBodyMultiText(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		/*final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);

		label.setText("Template Body:                                              ");
		label.setLayoutData(gridDataLabel);*/
		final GridData gridDataText = new GridData(550, 100);
		this.templateBodyMultiText = new TemplateFieldEditor("snippet", "Template Body:                                              ",
				composite, "snippet", FIELDS.TEMPLATE_BODY, SWT.MULTI);
		this.templateBodyMultiText.setLayout(gridDataText);
		/*final GridData gridDataText = new GridData(480, 150);
		gridDataText.grabExcessHorizontalSpace = true;
		this.templateBodyMultiText = new Text(composite, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		this.templateBodyMultiText.setLayoutData(gridDataText);*/

		if (this.createJoinData.getTemplateSettings().getTemplateBody() != null
				&& this.preferenceStore.getBoolean(P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES)) {
			this.templateBodyMultiText.setText(this.createJoinData.getTemplateSettings().getTemplateBody());
		} /*else {
			this.templateBodyMultiText.setEnabled(false);
			}*/
		/*this.templateBodyMultiText.addModifyListener(new ModifyListener() {

			public void modifyText(final ModifyEvent event) {
				CreateJoinDialog.this.modifiedTemplateBody = CreateJoinDialog.this.templateBodyMultiText.getText();
			}
		});*/

	}

	/**
	 * @param parent
	 */
	private void createTablesCombo(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);

		final GridData table1LableGrid = new GridData();
		final Label table1Label = new Label(composite, SWT.NONE);
		table1Label.setText("Select Join Table1:                           ");
		table1Label.setLayoutData(table1LableGrid);

		final GridData table1ComboGrid = new GridData(200, 20);
		table1ComboGrid.grabExcessHorizontalSpace = true;
		this.firstTableCombo = new Combo(composite, SWT.DROP_DOWN); // | SWT.READ_ONLY);
		this.firstTableCombo.setLayoutData(table1ComboGrid);

		/*for (final String table : this.createJoinData.getFirstTablesInDB().toArray(new String[0])) {
			this.firstTableCombo.add(table);

		}*/
		final ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();
		poulateTableCombo(connectToDatabase, this.firstTableCombo);
		this.firstTableCombo.setEnabled(true);
		this.firstTableCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (CreateJoinDialog.this.createJoinData.getNumberOfJoinTables().equals(NUMBER_OF_JOIN_TABLES.THREE)) {
					CreateJoinDialog.this.copyOfThirdTableCombo.setEnabled(true);
					CreateJoinDialog.this.thirdTableCombo.setEnabled(true);
				} else if (CreateJoinDialog.this.createJoinData.getNumberOfJoinTables().equals(NUMBER_OF_JOIN_TABLES.TWO)) {
					CreateJoinDialog.this.copyOfThirdTableCombo.setEnabled(false);
					CreateJoinDialog.this.thirdTableCombo.setEnabled(false);
				}
				String selectedTableInstance = null;
				CreateJoinDialog.this.firstTableName = CreateJoinDialog.this.firstTableCombo.getItem(CreateJoinDialog.this.firstTableCombo
						.getSelectionIndex());
				if (CreateJoinDialog.this.firstTableName != null) {
					CreateJoinDialog.this.createJoinData.setFirstTableName(CreateJoinDialog.this.firstTableName);
					selectedTableInstance = CreateJoinDialog.this.firstTableName.substring(0, 1).toLowerCase();
					CreateJoinDialog.this.firstTableInstanceName.setText(selectedTableInstance);
					CreateJoinDialog.this.createJoinData.setFirstTableInstanceName(selectedTableInstance);
					CreateJoinDialog.this.processColumnCombo(selectedTableInstance);
					CreateJoinDialog.this.setErrorMessage(CreateJoinDialog.this.defaultMessage);

				} else {
					CreateJoinDialog.this.setErrorMessage("Please Select join Table1");
				}

				if (CreateJoinDialog.this.copyOfThirdTableCombo.getSelectionIndex() > 0 && CreateJoinDialog.this.thirdTableName != null) {
					if (CreateJoinDialog.this.copyOfThirdTableCombo
							.getItem(CreateJoinDialog.this.copyOfThirdTableCombo.getSelectionIndex()).equals(
									CreateJoinDialog.this.thirdTableName)) {
						setErrorMessage(CreateJoinDialog.this.defaultMessage);
					} else {
						setErrorMessage("Please Select same Table for Common Join Table");
					}
				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.firstTableCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				CreateJoinDialog.this.createJoinData.setFirstTableName(CreateJoinDialog.this.firstTableCombo.getText());

				if (!isEmpty(CreateJoinDialog.this.createJoinData.getFirstTableName())) {
					setErrorMessage(CreateJoinDialog.this.defaultMessage);
					if (!CreateJoinDialog.this.createJoinData.getFirstTablesInDB().contains(
							CreateJoinDialog.this.createJoinData.getFirstTableName())) {
						setErrorMessage("This join table1 is not there in the DB.");
					}
				} else {
					setErrorMessage("Please select join Table1.");
				}
			}
		});
		this.firstTableCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent arg0) {
				setErrorMessage(CreateJoinDialog.this.defaultMessage);

			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				if (CreateJoinDialog.this.firstSchemaCombo.getText().equals(EMPTY_STR)) {
					setErrorMessage("Please select a schema first");
				}
			}
		});
		final GridData table2LableGrid = new GridData();
		final Label table2Label = new Label(composite, SWT.NONE);
		table2Label.setText("                   Select Join Table2:                           ");
		table2Label.setLayoutData(table2LableGrid);

		final GridData table2ComboGrid = new GridData(200, 20);
		table2ComboGrid.grabExcessHorizontalSpace = true;
		this.secondTableCombo = new Combo(composite, SWT.DROP_DOWN); // |
																		// SWT.READ_ONLY);
		this.secondTableCombo.setLayoutData(table2ComboGrid);
		this.secondTableCombo.setEnabled(true);

		/*for (final String table : this.createJoinData.getSecondTablesInDB()) {
			this.secondTableCombo.add(table);
		}*/
		poulateTableCombo(connectToDatabase, this.secondTableCombo);
		this.secondTableCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (CreateJoinDialog.this.createJoinData.getNumberOfJoinTables().equals(NUMBER_OF_JOIN_TABLES.THREE)) {
					CreateJoinDialog.this.copyOfThirdTableCombo.setEnabled(true);
					CreateJoinDialog.this.thirdTableCombo.setEnabled(true);
				} else if (CreateJoinDialog.this.createJoinData.getNumberOfJoinTables().equals(NUMBER_OF_JOIN_TABLES.TWO)) {
					CreateJoinDialog.this.copyOfThirdTableCombo.setEnabled(false);
					CreateJoinDialog.this.thirdTableCombo.setEnabled(false);
				}
				String selectedTableInstance = null;
				CreateJoinDialog.this.secondTableName = CreateJoinDialog.this.secondTableCombo
						.getItem(CreateJoinDialog.this.secondTableCombo.getSelectionIndex());
				if (CreateJoinDialog.this.secondTableName != null) {
					CreateJoinDialog.this.createJoinData.setSecondTableName(CreateJoinDialog.this.secondTableName);
					if (CreateJoinDialog.this.firstTableName.equals(CreateJoinDialog.this.secondTableName)
							|| CreateJoinDialog.this.firstTableName.substring(0, 1).equals(
									CreateJoinDialog.this.secondTableName.substring(0, 1))) {
						selectedTableInstance = CreateJoinDialog.this.secondTableName.substring(0, 1).toLowerCase() + "1";
					} else {
						selectedTableInstance = CreateJoinDialog.this.secondTableName.substring(0, 1).toLowerCase();
					}
					CreateJoinDialog.this.secondTableInstanceName.setText(selectedTableInstance);
					CreateJoinDialog.this.createJoinData.setSecondTableInstanceName(selectedTableInstance);
					CreateJoinDialog.this.processColumnCombo(selectedTableInstance);
					CreateJoinDialog.this.setErrorMessage(CreateJoinDialog.this.defaultMessage);
					if (CreateJoinDialog.this.createJoinData.getNumberOfJoinTables().equals(NUMBER_OF_JOIN_TABLES.TWO)) {
						populateTablesColumns(CreateJoinDialog.this.firstTableName, CreateJoinDialog.this.secondTableName);
					}
				} else {
					CreateJoinDialog.this.setErrorMessage("Please Select join  Table2");
				}

				if (CreateJoinDialog.this.columnsInFirstTableCombo.getSelectionIndex() > 0) {
					final String selectedTable1Column = CreateJoinDialog.this.columnsInFirstTableCombo
							.getItem(CreateJoinDialog.this.columnsInFirstTableCombo.getSelectionIndex());
					if (selectedTable1Column != null) {
						final String selectedTable1ColName = selectedTable1Column.substring(0, selectedTable1Column.indexOf('-'));
						populateTable2Column(selectedTable1ColName, CreateJoinDialog.this.secondTableName);
					}
				}
				if (CreateJoinDialog.this.copyOfThirdTableCombo.getSelectionIndex() > -1 && CreateJoinDialog.this.thirdTableName != null) {
					if (CreateJoinDialog.this.copyOfThirdTableCombo
							.getItem(CreateJoinDialog.this.copyOfThirdTableCombo.getSelectionIndex()).equals(
									CreateJoinDialog.this.thirdTableName)) {
						setErrorMessage(CreateJoinDialog.this.defaultMessage);
					} else {
						setErrorMessage("Please Select same Table for Common Join Table");
					}
				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.secondTableCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				CreateJoinDialog.this.createJoinData.setSecondTableName(CreateJoinDialog.this.secondTableCombo.getText());

				if (!isEmpty(CreateJoinDialog.this.createJoinData.getSecondTableName())) {
					setErrorMessage(CreateJoinDialog.this.defaultMessage);
					if (!CreateJoinDialog.this.createJoinData.getSecondTablesInDB().contains(
							CreateJoinDialog.this.createJoinData.getSecondTableName())) {
						setErrorMessage("This join table2 is not there in the DB.");
					}
				} else {
					setErrorMessage("Please select join Table2.");
				}
			}
		});
		this.secondTableCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent arg0) {
				setErrorMessage(CreateJoinDialog.this.defaultMessage);

			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				if (CreateJoinDialog.this.secondSchemaCombo.getText().equals(EMPTY_STR)) {
					setErrorMessage("Please select a schema first");
				}
			}
		});
		final GridData table3LableGrid = new GridData();
		final Label table3Label = new Label(composite, SWT.NONE);
		table3Label.setText("Select Common Join Table:                           ");
		table3Label.setLayoutData(table3LableGrid);

		final GridData table3ComboGrid = new GridData(200, 20);
		table3ComboGrid.grabExcessHorizontalSpace = true;
		this.thirdTableCombo = new Combo(composite, SWT.DROP_DOWN); // |
																	// SWT.READ_ONLY);
		this.thirdTableCombo.setLayoutData(table3ComboGrid);
		this.thirdTableCombo.setEnabled(false);

		/*for (final String table : this.createJoinData.getThirdTablesInDB()) {
			this.thirdTableCombo.add(table);
		}*/
		poulateTableCombo(connectToDatabase, this.thirdTableCombo);
		this.thirdTableCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(final FocusEvent e) {
				if (CreateJoinDialog.this.firstTableInstanceName.getText().equals(EMPTY_STR)
						|| CreateJoinDialog.this.secondTableInstanceName.getText().equals(EMPTY_STR)) {
					CreateJoinDialog.this.thirdTableCombo.setEnabled(false);
					setErrorMessage("Please select join Table1 & join Table 2");
					return;
				}
			}

			@Override
			public void focusLost(final FocusEvent e) {
			}
		});
		this.thirdTableCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				CreateJoinDialog.this.copyOfThirdTableCombo.setEnabled(true);
				String selectedTableInstance = null;
				CreateJoinDialog.this.thirdTableName = CreateJoinDialog.this.thirdTableCombo.getItem(CreateJoinDialog.this.thirdTableCombo
						.getSelectionIndex());
				if (CreateJoinDialog.this.thirdTableName != null) {
					CreateJoinDialog.this.createJoinData.setThirdTableName(CreateJoinDialog.this.thirdTableName);
					if (CreateJoinDialog.this.firstTableName != null && CreateJoinDialog.this.secondTableName != null) {

						if (CreateJoinDialog.this.firstTableName.equals(CreateJoinDialog.this.thirdTableName)
								&& CreateJoinDialog.this.secondTableName.equals(CreateJoinDialog.this.thirdTableName)
								|| CreateJoinDialog.this.firstTableName.substring(0, 1).equals(
										CreateJoinDialog.this.thirdTableName.substring(0, 1))
								&& CreateJoinDialog.this.secondTableName.substring(0, 1).equals(
										CreateJoinDialog.this.thirdTableName.substring(0, 1))) {
							selectedTableInstance = CreateJoinDialog.this.thirdTableName.substring(0, 1).toLowerCase() + "2";

						} else if (CreateJoinDialog.this.firstTableName.equals(CreateJoinDialog.this.thirdTableName)
								|| CreateJoinDialog.this.firstTableName.substring(0, 1).equals(
										CreateJoinDialog.this.thirdTableName.substring(0, 1))) {
							selectedTableInstance = CreateJoinDialog.this.thirdTableName.substring(0, 1).toLowerCase() + "1";

						} else if (CreateJoinDialog.this.secondTableName.equals(CreateJoinDialog.this.thirdTableName)
								|| CreateJoinDialog.this.secondTableName.substring(0, 1).equals(
										CreateJoinDialog.this.thirdTableName.substring(0, 1))) {
							selectedTableInstance = CreateJoinDialog.this.thirdTableName.substring(0, 1).toLowerCase() + "1";

						} else {
							selectedTableInstance = CreateJoinDialog.this.thirdTableName.substring(0, 1).toLowerCase();
						}
					} else {
						CreateJoinDialog.this.thirdTableCombo.setEnabled(false);
						setErrorMessage("Please select join Table1 & join Table 2");
						return;
					}
					CreateJoinDialog.this.thirdTableInstanceName.setText(selectedTableInstance);
					CreateJoinDialog.this.createJoinData.setThirdTableInstanceName(selectedTableInstance);
					CreateJoinDialog.this.processColumnCombo(selectedTableInstance);
					CreateJoinDialog.this.setErrorMessage(CreateJoinDialog.this.defaultMessage);
				} else {
					CreateJoinDialog.this.setErrorMessage("Please Select Common join  Table");
				}

				if (CreateJoinDialog.this.copyOfThirdTableCombo.getSelectionIndex() > -1 && CreateJoinDialog.this.thirdTableName != null) {
					if (CreateJoinDialog.this.copyOfThirdTableCombo
							.getItem(CreateJoinDialog.this.copyOfThirdTableCombo.getSelectionIndex()).equals(
									CreateJoinDialog.this.thirdTableName)) {
						setErrorMessage(CreateJoinDialog.this.defaultMessage);
					} else {
						setErrorMessage("Please Select same Table for Common Join Table");
					}
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.thirdTableCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				CreateJoinDialog.this.createJoinData.setThirdTableName(CreateJoinDialog.this.thirdTableCombo.getText());

				if (!isEmpty(CreateJoinDialog.this.createJoinData.getThirdTableName())) {
					setErrorMessage(CreateJoinDialog.this.defaultMessage);
					if (!CreateJoinDialog.this.createJoinData.getThirdTablesInDB().contains(
							CreateJoinDialog.this.createJoinData.getThirdTableName())) {
						setErrorMessage("This common join table is not there in the DB.");
					}
				} else {
					setErrorMessage("Please select Common join Table.");
					//CreateJoinDialog.this.thirdTableCombo.setItem(0, EMPTY_STR);
				}
			}
		});
		this.thirdTableCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent arg0) {
				setErrorMessage(CreateJoinDialog.this.defaultMessage);

			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				if (CreateJoinDialog.this.thirdSchemaCombo.getText().equals(EMPTY_STR)) {
					setErrorMessage("Please select a schema first");
				}
			}
		});
		final GridData copyOfTable3LableGrid = new GridData();
		final Label copyOfTable3Label = new Label(composite, SWT.NONE);
		copyOfTable3Label.setText("                   Select Common Join Table:                           ");
		copyOfTable3Label.setLayoutData(copyOfTable3LableGrid);

		final GridData copyOfTable3ComboGrid = new GridData(200, 20);
		copyOfTable3ComboGrid.grabExcessHorizontalSpace = true;
		this.copyOfThirdTableCombo = new Combo(composite, SWT.DROP_DOWN);
		this.copyOfThirdTableCombo.setLayoutData(copyOfTable3ComboGrid);
		this.copyOfThirdTableCombo.setEnabled(false);

		/*for (final String table : this.createJoinData.getThirdTablesInDB()) {
			this.copyOfThirdTableCombo.add(table);
		}*/
		poulateTableCombo(connectToDatabase, this.copyOfThirdTableCombo);
		this.copyOfThirdTableCombo.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(final FocusEvent e) {
				if (CreateJoinDialog.this.firstTableInstanceName.getText().equals(EMPTY_STR)
						|| CreateJoinDialog.this.secondTableInstanceName.getText().equals(EMPTY_STR)
						|| CreateJoinDialog.this.thirdTableInstanceName.getText().equals(EMPTY_STR)) {
					CreateJoinDialog.this.copyOfThirdTableCombo.setEnabled(false);
					setErrorMessage("Please select  join Table1 & Table2 & Common Table ");
					return;
				}
			}

			@Override
			public void focusLost(final FocusEvent e) {
			}
		});
		this.copyOfThirdTableCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				CreateJoinDialog.this.copyOfThirdTableName = CreateJoinDialog.this.copyOfThirdTableCombo
						.getItem(CreateJoinDialog.this.copyOfThirdTableCombo.getSelectionIndex());
				if (CreateJoinDialog.this.copyOfThirdTableName != null) {
					if (CreateJoinDialog.this.thirdTableName != null) {
						if (CreateJoinDialog.this.copyOfThirdTableName.equals(CreateJoinDialog.this.thirdTableName)) {
							CreateJoinDialog.this.copyOfThirdTableInstanceName.setText(CreateJoinDialog.this.createJoinData
									.getThirdTableInstanceName());
							CreateJoinDialog.this.columnsInCopyOfThirdTableCombo.removeAll();
							for (final String column : CreateJoinDialog.this.createJoinData.getColumnsOfThirdTableInDB()) {
								CreateJoinDialog.this.columnsInCopyOfThirdTableCombo.add(column);

							}
							CreateJoinDialog.this.setErrorMessage(CreateJoinDialog.this.defaultMessage);
						} else {
							CreateJoinDialog.this.setErrorMessage("Please Select same Table for Common Join Table");
						}
					} else {
						CreateJoinDialog.this.setErrorMessage("Please Select Common Join Table under Select Join Table1");
					}

				} else {
					CreateJoinDialog.this.setErrorMessage("Please Select Common join Table");
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

		this.copyOfThirdTableCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {

				if (!isEmpty(CreateJoinDialog.this.copyOfThirdTableCombo.getText())) {
					setErrorMessage(CreateJoinDialog.this.defaultMessage);
					if (!CreateJoinDialog.this.createJoinData.getThirdTablesInDB().contains(
							CreateJoinDialog.this.copyOfThirdTableCombo.getText())) {
						setErrorMessage("This common join  table is not there in the DB.");
					}
				} else {
					setErrorMessage("Please select Common join  Table.");
				}
			}
		});

	}

	/**
	 * @param firstTableName
	 * @param secondTableName
	 */
	protected void populateTablesColumns(final String firstTableName, final String secondTableName) {
		if (firstTableName != null && secondTableName != null) {
			for (final String table1Column : getEmptyArrayForNull(this.createJoinData.getColumnsOfFirstTableInDB())) {
				final String table1ColumnName = table1Column.substring(0, table1Column.indexOf('-'));
				for (final String table2Column : getEmptyArrayForNull(this.createJoinData.getColumnsOfSecondTableInDB())) {
					final String table2ColumnName = table2Column.substring(0, table2Column.indexOf('-'));
					if (table1ColumnName.equals(table2ColumnName)) {
						this.columnsInFirstTableCombo.setText(table1Column);
						this.columnsInSecondTableCombo.setText(table2Column);
						return;
					}
				}
			}
		}
	}

	/**
	 * @param tableInstanceName
	 */
	protected void processColumnCombo(final String tableInstanceName) {
		setMessage("Process is going on,Please wait");
		final ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();
		final DatabaseCache databaseCache = DatabaseCache.getInstance();
		Connection connection = null;
		try {
			connection = ConnectToDatabase.getCon() == null ? connectToDatabase.getNewConnection(this.databaseNameCombo.getText())
					: ConnectToDatabase.getCon();
		} catch (final Exception ex1) {
			ex1.printStackTrace();
		}
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		final String databaseType = databaseConnectionSettings.getConnMap().get(this.databaseNameCombo.getText()).getDatabaseType();
		if (tableInstanceName.equals(this.createJoinData.getFirstTableInstanceName())) {

			try {
				getTableColumnsFromDB(this.firstTableName, connection, this.firstSchemaCombo.getText(), databaseType);
				this.createJoinData.setColumnsOfFirstTableInDB(databaseCache.getTableNameColumnListMap()
						.get(this.firstSchemaCombo.getItem(this.firstSchemaCombo.getSelectionIndex()) + DOT + this.firstTableName)
						.toArray(new String[0]));
				this.columnsInFirstTableCombo.removeAll();
				for (final String column : getEmptyArrayForNull(this.createJoinData.getColumnsOfFirstTableInDB())) {
					this.columnsInFirstTableCombo.add(column);
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}

		} else if (tableInstanceName.equals(this.createJoinData.getSecondTableInstanceName())) {

			try {
				getTableColumnsFromDB(this.secondTableName, connection, this.secondSchemaCombo.getText(), databaseType);
				this.createJoinData.setColumnsOfSecondTableInDB(databaseCache.getTableNameColumnListMap()
						.get(this.secondSchemaCombo.getItem(this.secondSchemaCombo.getSelectionIndex()) + DOT + this.secondTableName)
						.toArray(new String[0]));
				this.columnsInSecondTableCombo.removeAll();
				for (final String column : getEmptyArrayForNull(this.createJoinData.getColumnsOfSecondTableInDB())) {
					this.columnsInSecondTableCombo.add(column);
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		} else if (tableInstanceName.equals(this.createJoinData.getThirdTableInstanceName())) {
			try {
				getTableColumnsFromDB(this.thirdTableName, connection, this.thirdSchemaCombo.getText(), databaseType);
				this.createJoinData.setColumnsOfThirdTableInDB(databaseCache.getTableNameColumnListMap()
						.get(this.thirdSchemaCombo.getItem(this.thirdSchemaCombo.getSelectionIndex()) + DOT + this.thirdTableName)
						.toArray(new String[0]));
				this.columnsInThirdTableCombo.removeAll();
				for (final String column : getEmptyArrayForNull(this.createJoinData.getColumnsOfThirdTableInDB())) {
					this.columnsInThirdTableCombo.add(column);
				}

			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}
		setMessage(this.defaultMessage);
	}

	/**
	 * @param parent
	 */
	private void createTableInstancesText(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);

		final GridData lableGrid1 = new GridData();
		final Label label1 = new Label(composite, SWT.NONE);
		label1.setText("Instance Of Join Table1:                ");
		label1.setLayoutData(lableGrid1);

		final GridData text1Grid = new GridData(50, 15);
		this.firstTableInstanceName = new Text(composite, SWT.BORDER);
		this.firstTableInstanceName.setLayoutData(text1Grid);
		this.firstTableInstanceName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent arg0) {
				if (CreateJoinDialog.this.firstTableInstanceName.getText().equals(CreateJoinDialog.this.secondTableInstanceName.getText())
						|| CreateJoinDialog.this.firstTableInstanceName.getText().equals(
								CreateJoinDialog.this.thirdTableInstanceName.getText())) {
					setErrorMessage("Instance of both the tables cannot be same. Please correct the same.");
					CreateJoinDialog.this.firstTableInstanceName.setFocus();
				} else {
					CreateJoinDialog.this.createJoinData.setFirstTableInstanceName(CreateJoinDialog.this.firstTableInstanceName.getText());
					setErrorMessage(CreateJoinDialog.this.defaultMessage);
				}
			}
		});

		final GridData lableGrid2 = new GridData();
		final Label label2 = new Label(composite, SWT.NONE);
		label2.setText("                                                                           Instance Of Join Table2:                 ");
		label2.setLayoutData(lableGrid2);

		final GridData text2Grid = new GridData(50, 15);
		this.secondTableInstanceName = new Text(composite, SWT.BORDER);
		this.secondTableInstanceName.setLayoutData(text2Grid);
		this.secondTableInstanceName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent event) {
				if (CreateJoinDialog.this.secondTableInstanceName.getText().equals(CreateJoinDialog.this.firstTableInstanceName.getText())
						|| CreateJoinDialog.this.secondTableInstanceName.getText().equals(
								CreateJoinDialog.this.thirdTableInstanceName.getText())) {
					setErrorMessage("Instance of both the tables cannot be same. Please correct the same.");
					CreateJoinDialog.this.secondTableInstanceName.setFocus();
				} else {
					CreateJoinDialog.this.createJoinData.setSecondTableInstanceName(CreateJoinDialog.this.secondTableInstanceName.getText());
					setErrorMessage(CreateJoinDialog.this.defaultMessage);
				}
			}
		});

		final GridData lableGrid3 = new GridData();
		final Label label3 = new Label(composite, SWT.NONE);
		label3.setText("Instance Of Common Join Table:                 ");
		label3.setLayoutData(lableGrid3);

		final GridData text3Grid = new GridData(50, 15);
		this.thirdTableInstanceName = new Text(composite, SWT.BORDER);
		this.thirdTableInstanceName.setLayoutData(text3Grid);
		this.thirdTableInstanceName.setEnabled(false);
		this.thirdTableInstanceName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent event) {
				if (CreateJoinDialog.this.thirdTableInstanceName.getText().equals(CreateJoinDialog.this.firstTableInstanceName.getText())
						|| CreateJoinDialog.this.thirdTableInstanceName.getText().equals(
								CreateJoinDialog.this.secondTableInstanceName.getText())) {
					setErrorMessage("Instance of both the tables cannot be same. Please correct the same.");
					CreateJoinDialog.this.thirdTableInstanceName.setFocus();
				} else {
					CreateJoinDialog.this.createJoinData.setThirdTableInstanceName(CreateJoinDialog.this.thirdTableInstanceName.getText());
					setErrorMessage(CreateJoinDialog.this.defaultMessage);
				}
			}
		});

		final GridData lableGrid4 = new GridData();
		final Label label4 = new Label(composite, SWT.NONE);
		label4.setText("                                                                           Instance Of Common Join Table:                 ");
		label4.setLayoutData(lableGrid4);
		final GridData text4Grid = new GridData(50, 15);
		this.copyOfThirdTableInstanceName = new Text(composite, SWT.BORDER);
		this.copyOfThirdTableInstanceName.setLayoutData(text4Grid);
		this.copyOfThirdTableInstanceName.setEnabled(false);
		this.copyOfThirdTableInstanceName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				if (CreateJoinDialog.this.copyOfThirdTableInstanceName.getText().equals(
						CreateJoinDialog.this.firstTableInstanceName.getText())
						|| CreateJoinDialog.this.copyOfThirdTableInstanceName.getText().equals(
								CreateJoinDialog.this.secondTableInstanceName.getText())) {
					setErrorMessage("Instance of both the tables cannot be same. Please correct the same.");
					CreateJoinDialog.this.copyOfThirdTableInstanceName.setFocus();
				} else {
					setErrorMessage(CreateJoinDialog.this.defaultMessage);
				}

			}
		});

	}

	/**
	 * @param parent
	 */
	private void createTablesColumnsCombo(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);

		final GridData columnLableGrid1 = new GridData();
		final Label columnLabel1 = new Label(composite, SWT.NONE);
		columnLabel1.setText("Select Column Of Join Table1:      ");
		columnLabel1.setLayoutData(columnLableGrid1);

		final GridData columnComboGrid1 = new GridData(250, 20);
		columnComboGrid1.grabExcessHorizontalSpace = true;
		this.columnsInFirstTableCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.columnsInFirstTableCombo.setLayoutData(columnComboGrid1);

		this.columnsInFirstTableCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (CreateJoinDialog.this.createJoinData.getNumberOfJoinTables().equals(NUMBER_OF_JOIN_TABLES.TWO)) {
					if (CreateJoinDialog.this.firstTableName.equals(CreateJoinDialog.this.secondTableName)) {
						CreateJoinDialog.this.columnsInSecondTableCombo.setText(CreateJoinDialog.this.columnsInFirstTableCombo
								.getItem(CreateJoinDialog.this.columnsInFirstTableCombo.getSelectionIndex()));
						CreateJoinDialog.this.columnsInSecondTableCombo.setEnabled(false);
					} else {
						CreateJoinDialog.this.columnsInSecondTableCombo.setEnabled(true);
					}
					CreateJoinDialog.this.setErrorMessage(CreateJoinDialog.this.defaultMessage);

					final String table1Col = CreateJoinDialog.this.columnsInFirstTableCombo
							.getItem(CreateJoinDialog.this.columnsInFirstTableCombo.getSelectionIndex());
					final String selectedTable1ColName = table1Col.substring(0, table1Col.indexOf('-'));
					if (CreateJoinDialog.this.columnsInSecondTableCombo.getSelectionIndex() == -1) {
						populateTable2Column(selectedTable1ColName, CreateJoinDialog.this.secondTableName);
					} else {
						final String table1Column = CreateJoinDialog.this.columnsInFirstTableCombo
								.getItem(CreateJoinDialog.this.columnsInFirstTableCombo.getSelectionIndex());
						final String table2Column = CreateJoinDialog.this.columnsInSecondTableCombo
								.getItem(CreateJoinDialog.this.columnsInSecondTableCombo.getSelectionIndex());

						//String table1ColumnType = table1Column.substring(table1Column.indexOf(' '), table1Column.indexOf(','));
						//final String table2ColumnType = table2Column.substring(table2Column.indexOf(' '), table2Column.indexOf(','));
						final String table1ColumnType = getColumnType(table1Column);
						final String table2ColumnType = getColumnType(table2Column);
						if (CreateJoinDialog.this.createJoinData.getNumberOfJoinTables().equals(NUMBER_OF_JOIN_TABLES.TWO)) {
							if (table1ColumnType.equals(table2ColumnType)) {
								CreateJoinDialog.this.setErrorMessage(CreateJoinDialog.this.defaultMessage);
							} else {
								CreateJoinDialog.this.setErrorMessage("Join Table1 Column Type is different from Join Table2 Column Type");
							}
						} else if (CreateJoinDialog.this.createJoinData.getNumberOfJoinTables().equals(NUMBER_OF_JOIN_TABLES.THREE)) {
							if (table1ColumnType.equals(table2ColumnType)) {
								CreateJoinDialog.this
										.setErrorMessage("Join Table1 Column Type should be different from Join Table2 Column Type");
							} else {
								CreateJoinDialog.this.setErrorMessage(CreateJoinDialog.this.defaultMessage);
							}
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		final GridData columnLableGrid2 = new GridData();
		final Label columnLabel2 = new Label(composite, SWT.NONE);
		columnLabel2.setText("   Select Column Of Join Table2:      ");
		columnLabel2.setLayoutData(columnLableGrid2);

		final GridData columnComboGrid2 = new GridData(250, 20);
		columnComboGrid2.grabExcessHorizontalSpace = true;
		this.columnsInSecondTableCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.columnsInSecondTableCombo.setLayoutData(columnComboGrid2);

		this.columnsInSecondTableCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

				final String table1Column = CreateJoinDialog.this.columnsInFirstTableCombo
						.getItem(CreateJoinDialog.this.columnsInFirstTableCombo.getSelectionIndex());
				final String table2Column = CreateJoinDialog.this.columnsInSecondTableCombo
						.getItem(CreateJoinDialog.this.columnsInSecondTableCombo.getSelectionIndex());

				final String table1ColumnType = getColumnType(table1Column); //table1Column.substring(table1Column.indexOf(' '), table1Column.indexOf(','));
				final String table2ColumnType = getColumnType(table2Column);//table2Column.substring(table2Column.indexOf(' '), table2Column.indexOf(','));
				if (CreateJoinDialog.this.createJoinData.getNumberOfJoinTables().equals(NUMBER_OF_JOIN_TABLES.TWO)) {
					if (table1ColumnType.equals(table2ColumnType)) {
						CreateJoinDialog.this.setErrorMessage(CreateJoinDialog.this.defaultMessage);
					} else {
						CreateJoinDialog.this.setErrorMessage("Join Table1 Column Type is different from Join Table2 Column Type");
					}
				} else if (CreateJoinDialog.this.createJoinData.getNumberOfJoinTables().equals(NUMBER_OF_JOIN_TABLES.THREE)) {
					if (table1ColumnType.equals(table2ColumnType)) {
						CreateJoinDialog.this.setErrorMessage("Join Table1 Column Type should be different from Join Table2 Column Type");
					} else {
						CreateJoinDialog.this.setErrorMessage(CreateJoinDialog.this.defaultMessage);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		final GridData columnLableGrid3 = new GridData();
		final Label columnLabel3 = new Label(composite, SWT.NONE);
		columnLabel3.setText("Select Column Of Common Join Table:      ");
		columnLabel3.setLayoutData(columnLableGrid3);

		final GridData columnComboGrid3 = new GridData(250, 20);
		columnComboGrid3.grabExcessHorizontalSpace = true;
		this.columnsInThirdTableCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.columnsInThirdTableCombo.setLayoutData(columnComboGrid3);
		this.columnsInThirdTableCombo.setEnabled(false);

		this.columnsInThirdTableCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String table1Column = CreateJoinDialog.this.columnsInFirstTableCombo
						.getItem(CreateJoinDialog.this.columnsInFirstTableCombo.getSelectionIndex());
				final String commonTableColumn = CreateJoinDialog.this.columnsInThirdTableCombo
						.getItem(CreateJoinDialog.this.columnsInThirdTableCombo.getSelectionIndex());
				final String table1ColumnType = getColumnType(table1Column);//table1Column.substring(table1Column.indexOf(' '), table1Column.indexOf(','));
				final String commonTableColumnType = getColumnType(commonTableColumn);//commonTableColumn.substring(commonTableColumn.indexOf(' '),commonTableColumn.indexOf(','));
				if (table1ColumnType.equals(commonTableColumnType)) {
					CreateJoinDialog.this.setErrorMessage(CreateJoinDialog.this.defaultMessage);
					CreateJoinDialog.this.createJoinData.setSelectedTable3JoinColumn(commonTableColumn.substring(0,
							commonTableColumn.indexOf('-')));
				} else {
					CreateJoinDialog.this.setErrorMessage("Join Table1 Column Type is different from Join Common Column Type");
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		final GridData columnLableGrid4 = new GridData();
		final Label columnLabel4 = new Label(composite, SWT.NONE);
		columnLabel4.setText("   Select Column Of Join Common Table:      ");
		columnLabel4.setLayoutData(columnLableGrid4);

		final GridData columnComboGrid4 = new GridData(250, 20);
		columnComboGrid4.grabExcessHorizontalSpace = true;
		this.columnsInCopyOfThirdTableCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.columnsInCopyOfThirdTableCombo.setLayoutData(columnComboGrid4);
		this.columnsInCopyOfThirdTableCombo.setEnabled(false);

		this.columnsInCopyOfThirdTableCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String table2Column = CreateJoinDialog.this.columnsInSecondTableCombo
						.getItem(CreateJoinDialog.this.columnsInSecondTableCombo.getSelectionIndex());
				final String copyOfCommonTableColumn = CreateJoinDialog.this.columnsInCopyOfThirdTableCombo
						.getItem(CreateJoinDialog.this.columnsInCopyOfThirdTableCombo.getSelectionIndex());
				final String table2ColumnType = getColumnType(table2Column);//table2Column.substring(table2Column.indexOf(' '), table2Column.indexOf(','));
				final String copyOfCommomTableColumnType = getColumnType(copyOfCommonTableColumn);//copyOfCommonTableColumn.substring(copyOfCommonTableColumn.indexOf(' '),copyOfCommonTableColumn.indexOf(','));
				if (table2ColumnType.equals(copyOfCommomTableColumnType)) {
					CreateJoinDialog.this.setErrorMessage(CreateJoinDialog.this.defaultMessage);
					CreateJoinDialog.this.createJoinData.setSelectedCopyOfTable3JoinColumn(copyOfCommonTableColumn.substring(0,
							copyOfCommonTableColumn.indexOf('-')));

				} else {
					CreateJoinDialog.this.setErrorMessage("Join Table2 Column Type is different from Join Common Column Type");
				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * @param tableColumn
	 * @return
	 */
	protected String getColumnType(final String tableColumn) {
		String columnType = null;
		if (tableColumn != null) {
			final String[] elementsOfTable1Col = tableColumn.split(COMMA);

			if (elementsOfTable1Col[0].trim().contains("Primary Key")) {
				columnType = elementsOfTable1Col[1].trim();
			} else {
				final String[] elements = elementsOfTable1Col[0].trim().split(HYPHEN);
				columnType = elements[1].trim();
			}
		}
		return columnType;
	}

	/**
	 * @param selectedTable1ColName
	 * @param secondTableName
	 */
	protected void populateTable2Column(final String selectedTable1ColName, final String secondTableName) {
		if (selectedTable1ColName != null && secondTableName != null) {
			for (final String column : getEmptyArrayForNull(this.createJoinData.getColumnsOfSecondTableInDB())) {
				if (column.substring(0, column.indexOf('-')).equals(selectedTable1ColName)) {
					this.columnsInSecondTableCombo.setText(column);
				}
			}
		}
	}

	/**
	 * @param parent
	 */
	private void createJoinTypesRadio(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 10;
		composite.setLayout(layout);
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Join Types:                                                       ");
		this.innerJoinButton = new Button(composite, SWT.RADIO);
		this.innerJoinButton.setText("Inner Join");
		this.innerJoinButton.setSelection(true);
		this.innerJoinButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		this.leftJoinButton = new Button(composite, SWT.RADIO);
		this.leftJoinButton.setText("Left Join");
		//this.leftJoinButton.setSelection(true);
		this.leftJoinButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.rightJoinButton = new Button(composite, SWT.RADIO);
		this.rightJoinButton.setText("Right Join");
		//this.rightJoinButton.setSelection(true);
		this.rightJoinButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	private void createOrderByGroupByButtons(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 8;
		composite.setLayout(layout);
		final Label label = new Label(composite, SWT.NONE);
		label.setText("GroupBy and OrderBy :                                  ");
		this.groupByButton = new Button(composite, SWT.CHECK);
		this.groupByButton.setText("Group By");
		this.groupByButton.setSelection(false);
		this.groupByButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

				if (((Button) event.widget).getSelection()) {
					if (CreateJoinDialog.this.firstTableCombo.getSelectionIndex() == -1) {
						setErrorMessage("Please choose the join table1.");
						CreateJoinDialog.this.groupByButton.setSelection(false);
						return;
					}
					if (CreateJoinDialog.this.secondTableCombo.getSelectionIndex() == -1) {
						setErrorMessage("Please choose the join table2.");
						CreateJoinDialog.this.groupByButton.setSelection(false);
						return;
					}
					if (CreateJoinDialog.this.threeTablesJoinButton.getSelection() == true
							&& CreateJoinDialog.this.thirdTableCombo.getSelectionIndex() == -1
							&& CreateJoinDialog.this.copyOfThirdTableCombo.getSelectionIndex() == -1) {
						setErrorMessage("Please choose the common join  table.");
						CreateJoinDialog.this.groupByButton.setSelection(false);
						return;
					}
					final ArrayList<String> tableNamesWithSchema = new ArrayList<String>();
					tableNamesWithSchema.add(CreateJoinDialog.this.firstSchemaCombo.getItem(CreateJoinDialog.this.firstSchemaCombo
							.getSelectionIndex())
							+ DOT
							+ CreateJoinDialog.this.firstTableCombo.getItem(CreateJoinDialog.this.firstTableCombo.getSelectionIndex()));
					tableNamesWithSchema.add(CreateJoinDialog.this.secondSchemaCombo.getItem(CreateJoinDialog.this.secondSchemaCombo
							.getSelectionIndex())
							+ DOT
							+ CreateJoinDialog.this.secondTableCombo.getItem(CreateJoinDialog.this.secondTableCombo.getSelectionIndex()));
					if (CreateJoinDialog.this.threeTablesJoinButton.getSelection()) {
						tableNamesWithSchema.add(CreateJoinDialog.this.thirdSchemaCombo.getItem(CreateJoinDialog.this.thirdSchemaCombo
								.getSelectionIndex())
								+ DOT
								+ CreateJoinDialog.this.thirdTableCombo.getItem(CreateJoinDialog.this.thirdTableCombo.getSelectionIndex()));
					}
					if (tableNamesWithSchema != null) {
						final OrderByGroupByJoinDialog dialog = new OrderByGroupByJoinDialog(new Shell(), "group_by_join",
								tableNamesWithSchema);
						if (dialog.open() == Window.CANCEL) {
							return;
						}
						CreateJoinDialog.this.createJoinData.setgroupByFieldSelectionMap(dialog.getSelectedFields());
					}

				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
		this.orderByButton = new Button(composite, SWT.CHECK);
		this.orderByButton.setText("Order By");
		this.orderByButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.widget).getSelection()) {
					if (CreateJoinDialog.this.firstTableCombo.getSelectionIndex() == -1) {
						setErrorMessage("Please choose the join table1.");
						CreateJoinDialog.this.groupByButton.setSelection(false);
						return;
					}
					if (CreateJoinDialog.this.secondTableCombo.getSelectionIndex() == -1) {
						setErrorMessage("Please choose the join table2.");
						CreateJoinDialog.this.groupByButton.setSelection(false);
						return;
					}
					if (CreateJoinDialog.this.threeTablesJoinButton.getSelection() == true
							&& CreateJoinDialog.this.thirdTableCombo.getSelectionIndex() == -1
							&& CreateJoinDialog.this.copyOfThirdTableCombo.getSelectionIndex() == -1) {
						setErrorMessage("Please choose the common join table.");
						CreateJoinDialog.this.groupByButton.setSelection(false);
						return;
					}
					final ArrayList<String> tableNamesWithSchema = new ArrayList<String>();
					tableNamesWithSchema.add(CreateJoinDialog.this.firstSchemaCombo.getItem(CreateJoinDialog.this.firstSchemaCombo
							.getSelectionIndex())
							+ DOT
							+ CreateJoinDialog.this.firstTableCombo.getItem(CreateJoinDialog.this.firstTableCombo.getSelectionIndex()));

					tableNamesWithSchema.add(CreateJoinDialog.this.secondSchemaCombo.getItem(CreateJoinDialog.this.secondSchemaCombo
							.getSelectionIndex())
							+ DOT
							+ CreateJoinDialog.this.secondTableCombo.getItem(CreateJoinDialog.this.secondTableCombo.getSelectionIndex()));

					if (CreateJoinDialog.this.threeTablesJoinButton.getSelection()) {
						tableNamesWithSchema.add(CreateJoinDialog.this.thirdSchemaCombo.getItem(CreateJoinDialog.this.thirdSchemaCombo
								.getSelectionIndex())
								+ DOT
								+ CreateJoinDialog.this.thirdTableCombo.getItem(CreateJoinDialog.this.thirdTableCombo.getSelectionIndex()));

					}
					if (tableNamesWithSchema != null) {
						final OrderByGroupByJoinDialog dialog = new OrderByGroupByJoinDialog(new Shell(), "order_by_join",
								tableNamesWithSchema);
						if (dialog.open() == Window.CANCEL) {
							return;
						}
						CreateJoinDialog.this.createJoinData.setorderByFieldSelectionMap(dialog.getSelectedFields());
					}

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
	private void createWhereClauseSeparatorButtons(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Where Separator:                                            ");
		this.andButton = new Button(composite, SWT.RADIO);
		this.andButton.setText("and");
		this.andButton.setSelection(true);
		this.createJoinData.setWhereClauseSeparator(WHERE_CLAUSE_SEPARATOR.AND);
		this.andButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.widget).getSelection()) {
					CreateJoinDialog.this.createJoinData.setWhereClauseSeparator(WHERE_CLAUSE_SEPARATOR.AND);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.orButton = new Button(composite, SWT.RADIO);
		this.orButton.setText("or");
		this.orButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.widget).getSelection()) {
					CreateJoinDialog.this.createJoinData.setWhereClauseSeparator(WHERE_CLAUSE_SEPARATOR.OR);
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
	private void createMessageText(final Composite parent) {
		final GridData msgText = new GridData(590, 40);
		msgText.grabExcessHorizontalSpace = true;
		this.messageText = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
		this.messageText.setLayoutData(msgText);
		setMessage(this.defaultMessage);
	}

	/**
	 * @param message
	 */
	private void setMessage(final String msg) {
		this.message = msg;
		if (this.messageText != null && !this.messageText.isDisposed()) {
			this.messageText.setText(this.message == null ? " \n " : this.message); //$NON-NLS-1$
			final boolean hasError = this.message != this.defaultMessage;
			this.messageText.setEnabled(hasError);
			this.messageText.setVisible(hasError);
			this.messageText.getParent().update();
			final Control buttonOk = getButton(IDialogConstants.OK_ID);
			final Control buttonCancel = getButton(IDialogConstants.CANCEL_ID);
			if (buttonOk != null) {
				buttonOk.setEnabled(this.message == this.defaultMessage);
			}
			if (buttonCancel != null) {
				buttonCancel.setEnabled(this.message == this.defaultMessage);
			}
		}
	}

	/**
	 * @param errorMessage
	 */
	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
		if (this.errorMessageText != null && !this.errorMessageText.isDisposed()) {
			this.errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
			boolean hasError = false;
			if (errorMessage != null && !errorMessage.equals(this.defaultMessage)) {
				hasError = true;
			}
			this.errorMessageText.setEnabled(hasError);
			this.errorMessageText.setVisible(hasError);
			this.errorMessageText.getParent().update();
			final Control button = getButton(IDialogConstants.OK_ID);
			if (button != null) {
				button.setEnabled(errorMessage.equals(this.defaultMessage));
			}
		}
	}

	private void createErrorMessageText(final Composite parent) {

		final GridData errText = new GridData(590, 40);
		errText.grabExcessHorizontalSpace = true;

		this.errorMessageText = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
		// this.errorMessageText.setBackground(new Color(null, 255, 255, 255));
		//this.errorMessageText.setForeground(new Color(null, 255, 0, 0));
		this.errorMessageText.setForeground(FastCodeColor.getErrorMsgColor());
		this.errorMessageText.setLayoutData(errText);
		setErrorMessage(this.defaultMessage);
	}

	public CreateJoinData getCreateJoinData() {
		return this.createJoinData;
	}

	private void createPojoClassSelectionPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Select Pojo Class:                                           ");
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.pojoClassCombo = new Combo(composite, SWT.NONE);// new
																// Text(composite,
																// SWT.BORDER);
		this.pojoClassCombo.setSize(200, 20);
		this.pojoClassCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 500;
		this.pojoClassCombo.setEnabled(true);

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (!fastCodeCache.getTypeSet().isEmpty()) {
			for (final IType type : fastCodeCache.getTypeSet()) {
				this.pojoClassCombo.add(type.getFullyQualifiedName());
			}
		}
		this.pojoClassCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String selectedPojoClassName = CreateJoinDialog.this.pojoClassCombo.getText();
				try {
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.getFullyQualifiedName().equals(selectedPojoClassName)) {
								CreateJoinDialog.this.createJoinData.setiSelectPojoClassType(type);
							}
						}
					}
					if (CreateJoinDialog.this.createJoinData.getiSelectPojoClassType() == null) {
						if (CreateJoinDialog.this.browsedPojoClassMap.containsKey(selectedPojoClassName)) {
							CreateJoinDialog.this.createJoinData.setiSelectPojoClassType((IType) CreateJoinDialog.this.browsedPojoClassMap
									.get(selectedPojoClassName));
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
		this.pojoClassCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				final String inputPojoClassName = CreateJoinDialog.this.pojoClassCombo.getText();
				if (!isEmpty(inputPojoClassName)) {
					for (final IType type : fastCodeCache.getTypeSet()) {
						if (type.getFullyQualifiedName().equals(inputPojoClassName)) {
							return;
						}
					}
					if (CreateJoinDialog.this.browsedPojoClassMap.containsKey(inputPojoClassName)) {
						return;
					}
					try {
						final IType inputClassType = getTypeFromWorkspace(inputPojoClassName);
						if (inputClassType != null) {
							CreateJoinDialog.this.createJoinData.setiSelectPojoClassType(inputClassType);
							if (!CreateJoinDialog.this.browsedPojoClassMap.containsKey(inputClassType.getFullyQualifiedName())) {
								CreateJoinDialog.this.browsedPojoClassMap.put(inputClassType.getFullyQualifiedName(), inputClassType);
							}
							/*if (!fastCodeCache.getTypeSet().contains(inputClassType)) {
								fastCodeCache.getTypeSet().add(inputClassType);
							}*/
							setErrorMessage(CreateJoinDialog.this.defaultMessage);
						} else {
							setErrorMessage("Class does not exist,Please enter an existing class name ");
						}
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		this.pojoClassCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {

				if (isEmpty(CreateJoinDialog.this.pojoClassCombo.getText())) {
					setErrorMessage("Please choose a Class");

				} else {
					setErrorMessage(CreateJoinDialog.this.defaultMessage);
				}
			}

		});

		final GridData gridDataButton = new GridData();

		this.pojoClassBrowseButton = new Button(composite, SWT.PUSH);
		this.pojoClassBrowseButton.setText("Browse");
		this.pojoClassBrowseButton.setLayoutData(gridDataButton);
		this.pojoClassBrowseButton.setEnabled(true);

		this.pojoClassBrowseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final SelectionDialog selectionDialog;
				try {
					selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
							SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, "");
					selectionDialog.setTitle("Select Pojo Class");
					selectionDialog.setMessage("Select the Pojo class");

					if (selectionDialog.open() == CANCEL) {
						return;
					}
					final IType browsedPojoClassType = (IType) selectionDialog.getResult()[0];
					CreateJoinDialog.this.createJoinData.setiSelectPojoClassType(browsedPojoClassType);

					boolean addItem = true;
					if (CreateJoinDialog.this.pojoClassCombo.getItems() != null) {
						for (final String existingPojoClass : CreateJoinDialog.this.pojoClassCombo.getItems()) {
							if (existingPojoClass.equals(browsedPojoClassType.getFullyQualifiedName())) {
								if (!CreateJoinDialog.this.pojoClassCombo.getText().equals(existingPojoClass)) {
									CreateJoinDialog.this.pojoClassCombo.select(CreateJoinDialog.this.pojoClassCombo
											.indexOf(existingPojoClass));
								}
								addItem = false;
								break;
							}
						}
					}
					if (addItem) {
						CreateJoinDialog.this.pojoClassCombo.add(browsedPojoClassType.getFullyQualifiedName());
						CreateJoinDialog.this.pojoClassCombo.select(CreateJoinDialog.this.pojoClassCombo.getItemCount() - 1);
					}
					//CreateJoinDialog.this.pojoClassCombo.setText(((IType) selectionDialog.getResult()[0]).getFullyQualifiedName());
					if (CreateJoinDialog.this.browsedPojoClassMap.isEmpty()) {
						CreateJoinDialog.this.browsedPojoClassMap.put(browsedPojoClassType.getFullyQualifiedName(), browsedPojoClassType);
					} else {
						if (!CreateJoinDialog.this.browsedPojoClassMap.containsKey(browsedPojoClassType.getFullyQualifiedName())) {
							CreateJoinDialog.this.browsedPojoClassMap.put(browsedPojoClassType.getFullyQualifiedName(),
									browsedPojoClassType);
						}
					}
					/*	if (!fastCodeCache.getTypeSet().contains(selectionDialog.getResult()[0])) {
							fastCodeCache.getTypeSet().add((IType) selectionDialog.getResult()[0]);
						}*/

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
	private void createSchemaSelectionPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);

		final GridData schema1LableGrid = new GridData();
		final Label schema1Label = new Label(composite, SWT.NONE);
		schema1Label.setText("Select Join Schema1:			");
		schema1Label.setLayoutData(schema1LableGrid);

		final GridData schema1ComboGrid = new GridData(200, 20);
		schema1ComboGrid.grabExcessHorizontalSpace = true;
		this.firstSchemaCombo = new Combo(composite, SWT.DROP_DOWN);
		this.firstSchemaCombo.setLayoutData(schema1ComboGrid);
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		final String databaseType = databaseConnectionSettings.getConnMap().get(this.databaseNameCombo.getText()).getDatabaseType();
		/*final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		int schemaIndex = 0;
		int k = 0;
		this.firstSchemaCombo.setLayoutData(schema1ComboGrid);
		String defaultSchema = EMPTY_STR;
		if (databaseConnectionSettings.getTypesofDabases().toLowerCase().equals(ORACLE)) {
			defaultSchema = databaseConnectionSettings.getUserName();
		} else {
			defaultSchema = databaseConnectionSettings.getNameofDabase();
		}
		for (final String schemaName : getEmptyArrayForNull(this.createJoinData.getSchemasInDB().toArray(new String[0]))) {
			if (schemaName.equalsIgnoreCase(defaultSchema)) {
				schemaIndex = k;
			}
			k++;
			this.firstSchemaCombo.add(schemaName);
		}
		this.firstSchemaCombo.select(schemaIndex);
		final FastCodeContentProposalProvider provider1 = new FastCodeContentProposalProvider(this.firstSchemaCombo.getItems());
		final ComboContentAdapter comboAdapter1 = new ComboContentAdapter();
		final ContentProposalAdapter adapter1 = new ContentProposalAdapter(this.firstSchemaCombo, comboAdapter1, provider1, null, null);
		adapter1.setPropagateKeys(true);
		adapter1.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);*/
		populateSchemaCombo(this.firstSchemaCombo);
		final ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();

		/*try {
			Connection connection = ConnectToDatabase.getCon() == null ? connectToDatabase.getConnection(databaseNameCombo.getText())
					: ConnectToDatabase.getCon();
		} catch (Exception ex1) {
			ex1.printStackTrace();
		}*/

		this.firstSchemaCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent arg0) {
				try {
					CreateJoinDialog.this.firstTableCombo.setEnabled(true);
					if (CreateJoinDialog.this.firstTableCombo.getItemCount() > 0) {
						CreateJoinDialog.this.firstTableCombo.removeAll();
					}
					final DatabaseCache databaseCache = DatabaseCache.getInstance();
					poulateTableCombo(connectToDatabase, firstTableCombo);
					/*getTableFromDb(ConnectToDatabase.getCon() == null ? connectToDatabase.getConnection(databaseNameCombo.getText())
							: ConnectToDatabase.getCon(), CreateJoinDialog.this.firstSchemaCombo.getText(), databaseType);
					
					for (final String tableName : getEmptyArrayForNull(databaseCache.getDbTableListMap()
							.get(CreateJoinDialog.this.firstSchemaCombo.getText()).toArray(new String[0]))) {
						CreateJoinDialog.this.firstTableCombo.add(tableName);
					}
					final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(
							CreateJoinDialog.this.firstTableCombo.getItems());
					final ComboContentAdapter comboAdapter = new ComboContentAdapter();
					final ContentProposalAdapter adapter = new ContentProposalAdapter(CreateJoinDialog.this.firstTableCombo, comboAdapter,
							provider, null, null);
					adapter.setPropagateKeys(true);
					adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);*/
					CreateJoinDialog.this.createJoinData.setFirstTablesInDB(databaseCache.getDbTableListMap().get(
							CreateJoinDialog.this.firstSchemaCombo.getText()));
					CreateJoinDialog.this.createJoinData.setFirstSchemaSelected(CreateJoinDialog.this.firstSchemaCombo.getText());
				} catch (final Exception ex) {
					ex.printStackTrace();
				}

			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		final GridData schema2LableGrid = new GridData();
		final Label schema2Label = new Label(composite, SWT.NONE);
		schema2Label.setText("	    Select Join Schema2:		");
		schema2Label.setLayoutData(schema2LableGrid);

		final GridData schema2ComboGrid = new GridData(200, 20);
		schema2ComboGrid.grabExcessHorizontalSpace = true;
		this.secondSchemaCombo = new Combo(composite, SWT.DROP_DOWN); // |
																		// SWT.READ_ONLY);
		this.secondSchemaCombo.setLayoutData(schema2ComboGrid);
		/*schemaIndex = 0;
		k = 0;
		for (final String schemaName : getEmptyArrayForNull(this.createJoinData.getSchemasInDB().toArray(new String[0]))) {
			if (schemaName.equalsIgnoreCase(defaultSchema)) {
				schemaIndex = k;
			}
			k++;
			this.secondSchemaCombo.add(schemaName);
		}
		this.secondSchemaCombo.select(schemaIndex);
		final FastCodeContentProposalProvider provider2 = new FastCodeContentProposalProvider(this.secondSchemaCombo.getItems());
		final ComboContentAdapter comboAdapter2 = new ComboContentAdapter();
		final ContentProposalAdapter adapter2 = new ContentProposalAdapter(this.secondSchemaCombo, comboAdapter2, provider2, null, null);
		adapter2.setPropagateKeys(true);
		adapter2.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);*/
		populateSchemaCombo(this.secondSchemaCombo);
		this.secondSchemaCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent arg0) {
				try {
					CreateJoinDialog.this.secondTableCombo.setEnabled(true);
					if (CreateJoinDialog.this.secondTableCombo.getItemCount() > 0) {
						CreateJoinDialog.this.secondTableCombo.removeAll();
					}

					final DatabaseCache databaseCache = DatabaseCache.getInstance();
					poulateTableCombo(connectToDatabase, secondTableCombo);
					/*getTableFromDb(ConnectToDatabase.getCon() == null ? connectToDatabase.getConnection(databaseNameCombo.getText())
							: ConnectToDatabase.getCon(), CreateJoinDialog.this.secondSchemaCombo.getText(), databaseType);
					

					final DatabaseCache databaseCache = DatabaseCache.getInstance();
					for (final String tableName : getEmptyArrayForNull(databaseCache.getDbTableListMap()
							.get(CreateJoinDialog.this.secondSchemaCombo.getText()).toArray(new String[0]))) {
						CreateJoinDialog.this.secondTableCombo.add(tableName);
					}
					final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(
							CreateJoinDialog.this.secondTableCombo.getItems());
					final ComboContentAdapter comboAdapter = new ComboContentAdapter();
					final ContentProposalAdapter adapter = new ContentProposalAdapter(CreateJoinDialog.this.secondTableCombo, comboAdapter,
							provider, null, null);
					adapter.setPropagateKeys(true);
					adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);*/
					CreateJoinDialog.this.createJoinData.setSecondTablesInDB(databaseCache.getDbTableListMap().get(
							CreateJoinDialog.this.secondSchemaCombo.getText()));
					CreateJoinDialog.this.createJoinData.setSecondSchemaSelected(CreateJoinDialog.this.secondSchemaCombo.getText());
				} catch (final Exception ex) {
					ex.printStackTrace();
				}

			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		final GridData schema3LableGrid = new GridData();
		final Label schema3Label = new Label(composite, SWT.NONE);
		schema3Label.setText("Select Join Schema3:");
		schema3Label.setLayoutData(schema3LableGrid);

		final GridData schema3ComboGrid = new GridData(200, 20);
		schema3ComboGrid.grabExcessHorizontalSpace = true;

		this.thirdSchemaCombo = new Combo(composite, SWT.DROP_DOWN); // |
																		// SWT.READ_ONLY);
		this.thirdSchemaCombo.setLayoutData(schema3ComboGrid);
		CreateJoinDialog.this.thirdSchemaCombo.setEnabled(false);
		/*schemaIndex = 0;
		k = 0;
		for (final String schemaName : getEmptyArrayForNull(this.createJoinData.getSchemasInDB().toArray(new String[0]))) {
			this.thirdSchemaCombo.add(schemaName);
			if (schemaName.equalsIgnoreCase(defaultSchema)) {
				schemaIndex = k;
			}
			k++;
		}
		this.thirdSchemaCombo.select(schemaIndex);
		CreateJoinDialog.this.thirdSchemaCombo.setEnabled(false);
		final FastCodeContentProposalProvider provider3 = new FastCodeContentProposalProvider(this.thirdSchemaCombo.getItems());
		final ComboContentAdapter comboAdapter3 = new ComboContentAdapter();
		final ContentProposalAdapter adapter3 = new ContentProposalAdapter(this.thirdSchemaCombo, comboAdapter3, provider3, null, null);
		adapter3.setPropagateKeys(true);
		adapter3.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);*/

		populateSchemaCombo(this.thirdSchemaCombo);

		this.thirdSchemaCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent arg0) {
				try {
					CreateJoinDialog.this.thirdTableCombo.setEnabled(true);
					if (CreateJoinDialog.this.thirdTableCombo.getItemCount() > 0) {
						CreateJoinDialog.this.thirdTableCombo.removeAll();
					}

					final DatabaseCache databaseCache = DatabaseCache.getInstance();
					poulateTableCombo(connectToDatabase, thirdTableCombo);
					/*getTableFromDb(ConnectToDatabase.getCon() == null ? connectToDatabase.getConnection(databaseNameCombo.getText())
							: ConnectToDatabase.getCon(), CreateJoinDialog.this.thirdSchemaCombo.getText(), databaseType);
					CreateJoinDialog.this.thirdTableCombo.setEnabled(true);
					
					for (final String tableName : getEmptyArrayForNull(databaseCache.getDbTableListMap()
							.get(CreateJoinDialog.this.thirdSchemaCombo.getText()).toArray(new String[0]))) {
						CreateJoinDialog.this.thirdTableCombo.add(tableName);
					}
					final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(
							CreateJoinDialog.this.thirdTableCombo.getItems());
					final ComboContentAdapter comboAdapter = new ComboContentAdapter();
					final ContentProposalAdapter adapter = new ContentProposalAdapter(CreateJoinDialog.this.thirdTableCombo, comboAdapter,
							provider, null, null);
					adapter.setPropagateKeys(true);
					adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);*/
					CreateJoinDialog.this.createJoinData.setThirdTablesInDB(databaseCache.getDbTableListMap().get(
							CreateJoinDialog.this.thirdSchemaCombo.getText()));
					CreateJoinDialog.this.createJoinData.setThirdSchemaSelected(CreateJoinDialog.this.thirdSchemaCombo.getText());
					CreateJoinDialog.this.fourthSchemaCombo.select(CreateJoinDialog.this.thirdSchemaCombo.getSelectionIndex());
				} catch (final Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}

			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		final GridData schema4LableGrid = new GridData();
		final Label schema4Label = new Label(composite, SWT.NONE);
		schema4Label.setText("	    Select Join  Schema4:");
		schema4Label.setLayoutData(schema4LableGrid);

		final GridData schema4ComboGrid = new GridData(200, 20);
		schema4ComboGrid.grabExcessHorizontalSpace = true;

		this.fourthSchemaCombo = new Combo(composite, SWT.DROP_DOWN); // |
																		// SWT.READ_ONLY);
		this.fourthSchemaCombo.setLayoutData(schema4ComboGrid);
		CreateJoinDialog.this.fourthSchemaCombo.setEnabled(false);
		/*schemaIndex = 0;
		k = 0;
		for (final String schemaName : getEmptyArrayForNull(this.createJoinData.getSchemasInDB().toArray(new String[0]))) {
			this.fourthSchemaCombo.add(schemaName);
			if (schemaName.equalsIgnoreCase(defaultSchema)) {
				schemaIndex = k;
			}
			k++;
		}
		this.fourthSchemaCombo.select(schemaIndex);
		final FastCodeContentProposalProvider provider4 = new FastCodeContentProposalProvider(this.fourthSchemaCombo.getItems());
		final ComboContentAdapter comboAdapter4 = new ComboContentAdapter();
		final ContentProposalAdapter adapter4 = new ContentProposalAdapter(this.fourthSchemaCombo, comboAdapter4, provider4, null, null);
		adapter4.setPropagateKeys(true);
		adapter4.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);*/
		populateSchemaCombo(this.fourthSchemaCombo);

		this.fourthSchemaCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent arg0) {
				try {
					CreateJoinDialog.this.copyOfThirdTableCombo.setEnabled(true);
					if (CreateJoinDialog.this.copyOfThirdTableCombo.getItemCount() > 0) {
						CreateJoinDialog.this.copyOfThirdTableCombo.removeAll();
					}
					poulateTableCombo(connectToDatabase, copyOfThirdTableCombo);
					//final DatabaseCache databaseCache = DatabaseCache.getInstance();
					/*getTableFromDb(ConnectToDatabase.getCon() == null ? connectToDatabase.getConnection(databaseNameCombo.getText())
							: ConnectToDatabase.getCon(), CreateJoinDialog.this.fourthSchemaCombo.getText(), databaseType);
					
					for (final String tableName : getEmptyArrayForNull(databaseCache.getDbTableListMap()
							.get(CreateJoinDialog.this.fourthSchemaCombo.getText()).toArray(new String[0]))) {
						CreateJoinDialog.this.copyOfThirdTableCombo.add(tableName);
					}
					final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(
							CreateJoinDialog.this.copyOfThirdTableCombo.getItems());
					final ComboContentAdapter comboAdapter = new ComboContentAdapter();
					final ContentProposalAdapter adapter = new ContentProposalAdapter(CreateJoinDialog.this.fourthSchemaCombo,
							comboAdapter, provider, null, null);
					adapter.setPropagateKeys(true);
					adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);*/
					// CreateJoinDialog.this.createJoinData.setThirdTablesInDB(databaseCache.getDbTableListMap().get(CreateJoinDialog.this.fourthSchemaCombo.getText()));
				} catch (final Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}

			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * @param connectToDatabase
	 * @param tableCombo
	 */
	private void poulateTableCombo(final ConnectToDatabase connectToDatabase, final Combo tableCombo) {
		Connection connection = null;
		try {
			final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
			connection = ConnectToDatabase.getCon() == null ? connectToDatabase.getNewConnection(this.databaseNameCombo.getText())
					: ConnectToDatabase.getCon();
			final String databaseType = databaseConnectionSettings.getConnMap().get(this.databaseNameCombo.getText()).getDatabaseType();
			final DatabaseDetails databaseConnection = databaseConnectionSettings.getConnMap().get(this.databaseNameCombo.getText());
			String schema = EMPTY_STR;
			if (tableCombo.equals(this.firstTableCombo) && this.firstSchemaCombo.isEnabled() && !isEmpty(this.firstSchemaCombo.getText())) {
				schema = this.firstSchemaCombo.getText();
			} else if (tableCombo.equals(this.secondTableCombo) && this.secondSchemaCombo.isEnabled() && !isEmpty(this.secondSchemaCombo.getText())) {
				schema = this.secondSchemaCombo.getText();
			} else if (tableCombo.equals(this.thirdTableCombo) && this.thirdSchemaCombo.isEnabled() && !isEmpty(this.thirdSchemaCombo.getText())) {
				schema = this.thirdSchemaCombo.getText();
			} else if (tableCombo.equals(this.copyOfThirdTableCombo) && this.fourthSchemaCombo.isEnabled() && !isEmpty(this.fourthSchemaCombo.getText())) {
				schema = this.fourthSchemaCombo.getText();
			} else {
				schema = databaseConnection.getDatabaseType().equalsIgnoreCase(ORACLE) ? databaseConnection.getUserName()
						: databaseConnection.getDatabaseName();
			}
			/*String defaultSchema = databaseConnection.getDatabaseType().equalsIgnoreCase(ORACLE) ? databaseConnection.getUserName()
					: databaseConnection.getDatabaseName();*/

			getTableFromDb(connection, schema, databaseType);
			tableCombo.removeAll();
			final DatabaseCache databaseCache = DatabaseCache.getInstance();
			for (final String tableName : getEmptyArrayForNull(databaseCache.getDbTableListMap().get(schema).toArray(new String[0]))) {
				tableCombo.add(tableName);
			}
			final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(tableCombo.getItems());
			final ComboContentAdapter comboAdapter = new ComboContentAdapter();
			final ContentProposalAdapter adapter = new ContentProposalAdapter(tableCombo, comboAdapter, provider, null, null);
			adapter.setPropagateKeys(true);
			adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @param parent
	 */
	private void createAliasnameButton(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final Label label = new Label(composite, SWT.NONE);
		label.setText("Use Alias name:                                              ");
		this.useAliasNameButton = new Button(composite, SWT.CHECK);
		this.useAliasNameButton.setSelection(false);

		this.useAliasNameButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				CreateJoinDialog.this.createJoinData.setUseAliasName(CreateJoinDialog.this.useAliasNameButton.getSelection());

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});

	}

	/**
	 * @param parent
	 */
	private void createDatabaseNameSelectionPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());

		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final Label schemaLabel = new Label(composite, SWT.NONE);
		schemaLabel.setText("Database Names:                                             ");
		schemaLabel.setLayoutData(new GridData());
		this.databaseNameCombo = new Combo(composite, SWT.DROP_DOWN);
		this.databaseNameCombo.setLayoutData(new GridData(150, 100));
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		int defaultDbNameIndex = 0;
		int k = 0;
		for (final String dbName : databaseConnectionSettings.getConnMap().keySet()) {
			if (databaseConnectionSettings.getNameofDabase().equals(dbName)) {
				defaultDbNameIndex = k;
			}
			this.databaseNameCombo.add(dbName);
			k++;
		}
		this.databaseNameCombo.select(defaultDbNameIndex);
		this.createJoinData.setSelectedDatabaseName(this.databaseNameCombo.getText());
		final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(this.databaseNameCombo.getItems());
		final ComboContentAdapter comboAdapter = new ComboContentAdapter();
		final ContentProposalAdapter adapter = new ContentProposalAdapter(this.databaseNameCombo, comboAdapter, provider, null, null);
		adapter.setPropagateKeys(true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		this.databaseNameCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent event) {
				final String selectedDbName = databaseNameCombo.getText();
				if (databaseConnectionSettings.getConnMap().keySet().contains(selectedDbName)) {
					if (!selectedDbName.equalsIgnoreCase(DatabaseConnectionSettings.getInstance().getNameofDabase())
							|| !createJoinData.getSelectedDatabaseName().equals(selectedDbName)) {
						setMessage("Process is going on,Please wait");
						firstSchemaCombo.setEnabled(false);
						secondSchemaCombo.setEnabled(false);
						thirdSchemaCombo.setEnabled(false);
						fourthSchemaCombo.setEnabled(false);
						//updatePreferenceStore(selectedDbName);
						//DatabaseConnectionSettings.setReload(true);
						final ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();
						connectToDatabase.closeConnection(ConnectToDatabase.getCon());
						final DatabaseCache databaseCache = DatabaseCache.getInstance();
						final DatabaseDetails databaseConnection = databaseConnectionSettings.getConnMap().get(selectedDbName);
						Connection connection = null;
						try {
							connection = connectToDatabase.getNewConnection(selectedDbName);
							getSchemaFromDb(connection, databaseConnection.getDatabaseType());
							createJoinData.setSchemasInDB(databaseCache.getDbSchemaListMap().get(databaseConnection.getDatabaseType()));
							populateSchemaCombo(firstSchemaCombo);
							populateSchemaCombo(secondSchemaCombo);
							populateSchemaCombo(thirdSchemaCombo);
							populateSchemaCombo(fourthSchemaCombo);
							if (createJoinData.getNumberOfJoinTables().equals((NUMBER_OF_JOIN_TABLES.TWO))) {
								firstSchemaCombo.setEnabled(true);
								secondSchemaCombo.setEnabled(true);
								poulateTableCombo(connectToDatabase, firstTableCombo);
								poulateTableCombo(connectToDatabase, secondTableCombo);
							} else if (createJoinData.getNumberOfJoinTables().equals((NUMBER_OF_JOIN_TABLES.THREE))) {
								firstSchemaCombo.setEnabled(true);
								secondSchemaCombo.setEnabled(true);
								thirdSchemaCombo.setEnabled(true);
								fourthSchemaCombo.setEnabled(true);
								poulateTableCombo(connectToDatabase, firstTableCombo);
								poulateTableCombo(connectToDatabase, secondTableCombo);
								poulateTableCombo(connectToDatabase, thirdTableCombo);
								poulateTableCombo(connectToDatabase, copyOfThirdTableCombo);
							}
							createJoinData.setSelectedDatabaseName(selectedDbName);
							setMessage(defaultMessage);
						} catch (final Exception ex) {
							ex.printStackTrace();
						}

					}
				}
			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		/*this.databaseNameCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent event) {
				String selectedDbName = databaseNameCombo.getText();

				if (!selectedDbName.equalsIgnoreCase(DatabaseConnectionSettings.getInstance().getNameofDabase())
						|| !createJoinData.getSelectedDatabaseName().equals(selectedDbName)) {
					setMessage("Process is going on,Please wait");
					firstSchemaCombo.setEnabled(false);
					secondSchemaCombo.setEnabled(false);
					thirdSchemaCombo.setEnabled(false);
					fourthSchemaCombo.setEnabled(false);
					//updatePreferenceStore(selectedDbName);
					//DatabaseConnectionSettings.setReload(true);
					ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();
					connectToDatabase.closeConnection(ConnectToDatabase.getCon());
					final DatabaseCache databaseCache = DatabaseCache.getInstance();
					final DatabaseDetails databaseConnection = databaseConnectionSettings.getConnMap().get(selectedDbName);
					Connection connection = null;
					try {
						connection = connectToDatabase.getConnection(selectedDbName);
						getSchemaFromDb(connection, databaseConnection.getDatabaseType());
						createJoinData.setSchemasInDB(databaseCache.getDbSchemaListMap().get(databaseConnection.getDatabaseType()));
						populateSchemaCombo(firstSchemaCombo);
						populateSchemaCombo(secondSchemaCombo);
						populateSchemaCombo(thirdSchemaCombo);
						populateSchemaCombo(fourthSchemaCombo);
						if (createJoinData.getNumberOfJoinTables().equals((NUMBER_OF_JOIN_TABLES.TWO))) {
							firstSchemaCombo.setEnabled(true);
							secondSchemaCombo.setEnabled(true);
							poulateTableCombo(connectToDatabase, firstTableCombo);
							poulateTableCombo(connectToDatabase, secondTableCombo);
						} else if (createJoinData.getNumberOfJoinTables().equals((NUMBER_OF_JOIN_TABLES.THREE))) {
							firstSchemaCombo.setEnabled(true);
							secondSchemaCombo.setEnabled(true);
							thirdSchemaCombo.setEnabled(true);
							fourthSchemaCombo.setEnabled(true);
							poulateTableCombo(connectToDatabase, firstTableCombo);
							poulateTableCombo(connectToDatabase, secondTableCombo);
							poulateTableCombo(connectToDatabase, thirdTableCombo);
							poulateTableCombo(connectToDatabase, copyOfThirdTableCombo);
						}

						setMessage(defaultMessage);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});*/
	}

	/**
	 * @param schemaCombo
	 */
	private void populateSchemaCombo(final Combo schemaCombo) {
		int schemaIndex = 0;
		int k = 0;
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		final DatabaseDetails databaseConnection = databaseConnectionSettings.getConnMap().get(this.databaseNameCombo.getText());
		final String defaultSchema = databaseConnection.getDatabaseType().equalsIgnoreCase(ORACLE) ? databaseConnection.getUserName()
				: databaseConnection.getDatabaseName();
		if (schemaCombo != null) {
			schemaCombo.removeAll();
		}
		for (final String schemaName : this.createJoinData.getSchemasInDB().toArray(new String[0])) {
			if (schemaName.equalsIgnoreCase(defaultSchema)) {
				schemaIndex = k;
			}
			schemaCombo.add(schemaName);
			k++;
		}
		schemaCombo.select(schemaIndex);
		final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(schemaCombo.getItems());
		final ComboContentAdapter comboAdapter = new ComboContentAdapter();
		final ContentProposalAdapter adapter = new ContentProposalAdapter(schemaCombo, comboAdapter, provider, null, null);
		adapter.setPropagateKeys(true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Create Join");

		shell.setFullScreen(true);
		final CreateJoinDialog createJoinDialog = new CreateJoinDialog(shell);

		createJoinDialog.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}

package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.DATETIME;
import static org.fastcode.common.FastCodeConstants.DEFAULT_SUFFIX;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.HYPHEN;
import static org.fastcode.common.FastCodeConstants.MYSQL;
import static org.fastcode.common.FastCodeConstants.MYSQL_DEFAULT_DATE;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.NUMBER;
import static org.fastcode.common.FastCodeConstants.ORACLE;
import static org.fastcode.common.FastCodeConstants.ORACLE_DEFAULT_DATE;
import static org.fastcode.common.FastCodeConstants.POSTGRESQL;
import static org.fastcode.common.FastCodeConstants.POSTGRE_DEFAULT_DATE;
import static org.fastcode.common.FastCodeConstants.STRING_CONSTANT;
import static org.fastcode.util.DatabaseUtil.getNumberOfRecordsInTable;
import static org.fastcode.util.DatabaseUtil.getSchemaFromDb;
import static org.fastcode.util.DatabaseUtil.getTableColumnsFromDB;
import static org.fastcode.util.DatabaseUtil.getTableFromDb;
import static org.fastcode.util.FastCodeUtil.getEmptyArrayForNull;
import static org.fastcode.util.FastCodeUtil.getEmptyListForNull;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.isValidTableOrColumnName;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.CreateTableData;
import org.fastcode.common.DatabaseDetails;
import org.fastcode.common.FastCodeColor;
import org.fastcode.common.FastCodeFont;
import org.fastcode.popup.actions.snippet.CreateNewDatabaseTableSnippetAction.CreateTableDialogCallback;
import org.fastcode.preferences.DatabaseConnectionSettings;
import org.fastcode.util.ConnectToDatabase;
import org.fastcode.util.DatabaseCache;
import org.fastcode.util.FastCodeContentProposalProvider;
import org.fastcode.util.SQLDatatypes;
import org.fastcode.util.SQLDatatypesMapping;

public class CreateTableDialog extends TrayDialog {
	Shell							shell;
	private CreateTableData			createTableData;
	private Combo					schemaCombo;
	private final IPreferenceStore	preferenceStore;
	private Text					errorMessageText;
	protected final String			defaultMessage	= NEWLINE;
	private String					errorMessage;
	private Text					tableNameText;
	private Text					columnNameText;
	private Button					stringColumnTypeButton;
	private Button					numberColumnTypeButton;
	private Button					dateTimeColumnTypeButton;
	private Button					OthersColumnTypeButton;
	private Button					notNullButton;
	private Combo					columnTypeCombo;
	private Combo					existingTableCombo;
	private Text					columnTypeSize;
	private Text					columnTypePrecisionAndScale;
	private Text					defaultValue;
	private Combo					databaseNameCombo;
	private String					tableName;
	private String					dbType;
	private Button					lenByte;
	private Button					lenChar;
	private Label					numberOfColumns;
	private final int						SUBMT_ID		= 2;
	private Button					submitButton;
	CreateTableDialogCallback		createTableDialogCallback;

	/**
	 * @param shell
	 */
	protected CreateTableDialog(final Shell shell) {
		super(shell);
		this.shell = shell;
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
	}

	/**
	 * @param shell
	 * @param createTableData
	 */
	public CreateTableDialog(final Shell shell, final CreateTableData createTableData,
			final CreateTableDialogCallback createTableDialogCallback) {
		super(shell);
		this.shell = shell;

		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID); // Activator.getDefault().getPreferenceStore();
		this.createTableData = createTableData;
		this.createTableDialogCallback = createTableDialogCallback;
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);

		if (this.createTableData.isCreateTableWithColumns()) {
			shell.setText("Create Table With Columns Dialog");
		} else if (this.createTableData.isAddColumnsToExistingTable()) {
			shell.setText("Add Columns To Existing Table Dialog");
		}
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);

		createErrorMessageText(parent);
		createDatabaseNameSelectionPane(parent);
		createSchemaSelectionPane(parent);
		if (this.createTableData.isAddColumnsToExistingTable()) {
			createExistingTableSelectionPane(parent);
		} else {
			this.tableNameText = createTableText(parent, "Enter New Table Name:  ", 0);
		}
		createColumnTypesRadioButtons(parent);
		createColumnDetailsButtons(parent);
		this.columnNameText = createColumnNameText(parent, "Enter name(s) (SPACE SEPERATED) for the Selected Column Type: ", 0);
		return parent;
	}

	/**
	 * @param parent
	 */
	private void createColumnDetailsButtons(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());

		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);

		final Label columnTypeSizeLabel = new Label(composite, SWT.NONE);
		columnTypeSizeLabel.setText("Length:                              ");
		this.columnTypeSize = new Text(composite, SWT.BORDER | SWT.MULTI);
		this.columnTypeSize.setSize(50, 20);
		this.columnTypeSize.setEnabled(false);

		this.lenByte = new Button(composite, SWT.RADIO);
		this.lenByte.setText("byte");
		this.lenByte.setEnabled(false);

		this.lenChar = new Button(composite, SWT.RADIO);
		this.lenChar.setText("char");
		this.lenChar.setEnabled(false);
		if (this.dbType.equalsIgnoreCase(ORACLE)) {
			this.lenByte.setVisible(true);
			this.lenChar.setVisible(true);
		} else {
			this.lenByte.setVisible(false);
			this.lenChar.setVisible(false);
		}

		final Composite comp1 = new Composite(parent, parent.getStyle());

		final GridLayout layout1 = new GridLayout();
		layout1.numColumns = 2;
		comp1.setLayout(layout1);

		final Label columnTypePrecisionAndScaleLabel = new Label(comp1, SWT.NONE);
		columnTypePrecisionAndScaleLabel.setText("Precision & Scale:               ");
		this.columnTypePrecisionAndScale = new Text(comp1, SWT.BORDER | SWT.MULTI);
		this.columnTypePrecisionAndScale.setSize(50, 20);
		this.columnTypePrecisionAndScale.setEnabled(false);

		final Composite comp2 = new Composite(parent, parent.getStyle());

		final GridLayout layout2 = new GridLayout();
		layout2.numColumns = 3;
		comp2.setLayout(layout2);

		this.notNullButton = new Button(comp2, SWT.CHECK);
		this.notNullButton.setText("Not Null                     ");

		final Label defaultValueLabel = new Label(comp2, SWT.NONE);
		defaultValueLabel.setText("Default Value");
		this.defaultValue = new Text(comp2, SWT.BORDER | SWT.MULTI);
		this.defaultValue.setSize(50, 20);

		this.columnTypeSize.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent modifyEvent) {
				if (CreateTableDialog.this.columnTypeSize.isEnabled()) {
					final String size = CreateTableDialog.this.columnTypeSize.getText();
					if (!isEmpty(size)) {

						try {
							CreateTableDialog.this.createTableData.setColumnTypeSize(Integer.parseInt(size));
						} catch (final NumberFormatException ex) {
							setErrorMessage("Please provide valid number for length.");
							return;
						}
						setErrorMessage(CreateTableDialog.this.defaultMessage);
					} else {
						/*setErrorMessage("Please provide Length for the selected column type");
						return;*/
					}
				}
			}
		});
		this.columnTypePrecisionAndScale.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent modifyEvent) {
				if (CreateTableDialog.this.columnTypePrecisionAndScale.isEnabled()) {
					final String precisionAndScale = CreateTableDialog.this.columnTypePrecisionAndScale.getText();
					if (!isEmpty(precisionAndScale)) {
						//if (precisionAndScale.contains(COMMA)) {
						CreateTableDialog.this.createTableData.setColumnTypePrecisionAndScale(precisionAndScale);
						setErrorMessage(CreateTableDialog.this.defaultMessage);
						//} else {
						//setErrorMessage("Please provide precision and scale with comma separator like 10,2 ");
						//}
					} else {
						//setErrorMessage("Please provide precision and scale for the selected column type");
					}
				}
			}
		});

		this.notNullButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				CreateTableDialog.this.createTableData.setNotNull(CreateTableDialog.this.notNullButton.getSelection() ? CreateTableDialog.this.notNullButton
						.getText().toUpperCase() : EMPTY_STR);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

		this.defaultValue.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent modifyEvent) {
				final String defaultValue = CreateTableDialog.this.defaultValue.getText();
				if (CreateTableDialog.this.createTableData.isAddColumnsToExistingTable()
						&& CreateTableDialog.this.notNullButton.getSelection() && defaultValue == null) {
					setErrorMessage("Please provide a default value as the column is \"not null\"");
				} else {
					CreateTableDialog.this.createTableData.setDefaultValue(defaultValue);
					setErrorMessage(CreateTableDialog.this.defaultMessage);
				}
			}
		});

		this.lenByte.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				if (CreateTableDialog.this.lenByte.getSelection()) {
					CreateTableDialog.this.createTableData.setLenType("byte");
					setErrorMessage(CreateTableDialog.this.defaultMessage);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

		this.lenChar.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				if (CreateTableDialog.this.lenChar.getSelection()) {
					CreateTableDialog.this.createTableData.setLenType("char");
					setErrorMessage(CreateTableDialog.this.defaultMessage);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
	}

	@Override
	protected void okPressed() {
		if (!validateData()) {
			return;
		}
		super.okPressed();
	}

	/**
	 *
	 */
	public boolean validateData() {
		if (this.createTableData == null) {
			this.createTableData = new CreateTableData();
		}
		if (isEmpty(this.schemaCombo.getText())) {
			setErrorMessage("Please select schema name");
			return false;
		} else {
			this.createTableData.setSchemaSelected(this.schemaCombo.getText());
		}

		if (this.tableNameText != null) {
			if (isEmpty(this.tableNameText.getText())) {
				setErrorMessage("Please enter table name");
				return false;
			} else {
				this.createTableData.setTableName(this.tableNameText.getText());
			}
		}

		if (isEmpty(this.tableName)) {
			setErrorMessage("Please enter table name");
			return false;
		} else {
			this.createTableData.setTableName(this.tableName);
		}

		if (this.columnTypeCombo.getSelectionIndex() == -1) {
			setErrorMessage("Please select column type");
			return false;
		} else {

		}
		/*if (columnTypeSize.isEnabled() && isEmpty(columnTypeSize.getText())) {
			setErrorMessage("Please enter column type length");
			return;
		}*/
		/*if (this.columnTypePrecisionAndScale.isEnabled() && isEmpty(this.columnTypePrecisionAndScale.getText())) {
			setErrorMessage("Please enter value for precision and scale");
			return;
		}*/
		if (isEmpty(this.columnNameText.getText())) {
			setErrorMessage("Please enter column name(s)");
			return false;
		} else {
			this.createTableData.setColumnNames(this.columnNameText.getText().split("\\s+"));
		}

		if (this.dbType.equalsIgnoreCase(ORACLE)) {
			if (this.stringColumnTypeButton.getSelection() && !(this.lenByte.getSelection() || this.lenChar.getSelection())) {
				setErrorMessage("Please select length type - char or byte");
				return false;
			}
		}

		this.createTableData.setNotNull(this.notNullButton.getSelection() ? this.notNullButton.getText().toUpperCase() : EMPTY_STR);
		this.createTableData.setDefaultValue(this.defaultValue.getText());
		return true;
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
		schemaLabel.setText("Database Names:             ");
		schemaLabel.setLayoutData(new GridData());
		this.databaseNameCombo = new Combo(composite, SWT.DROP_DOWN);
		this.databaseNameCombo.setLayoutData(new GridData(150, 100));
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		int defaultDbNameIndex = 0;
		int k = 0;
		String suffix = EMPTY_STR;
		for (final String dbName : databaseConnectionSettings.getConnMap().keySet()) {
			if (databaseConnectionSettings.getNameofDabase().equals(dbName)) {
				defaultDbNameIndex = k;
				suffix = DEFAULT_SUFFIX;
				this.dbType = databaseConnectionSettings.getTypesofDabases();
			}
			this.databaseNameCombo.add(dbName + suffix);
			suffix = EMPTY_STR;
			k++;
		}
		this.databaseNameCombo.select(defaultDbNameIndex);
		this.createTableData.setSelectedDatabaseName(removeSuffix(this.databaseNameCombo.getText()));
		final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(this.databaseNameCombo.getItems());
		final ComboContentAdapter comboAdapter = new ComboContentAdapter();
		final ContentProposalAdapter adapter = new ContentProposalAdapter(this.databaseNameCombo, comboAdapter, provider, null, null);
		adapter.setPropagateKeys(true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		this.databaseNameCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent event) {
				final String selectedDbName = removeSuffix(CreateTableDialog.this.databaseNameCombo.getText());
				if (databaseConnectionSettings.getConnMap().keySet().contains(selectedDbName)) {
					if (!selectedDbName.equalsIgnoreCase(DatabaseConnectionSettings.getInstance().getNameofDabase())
							|| !CreateTableDialog.this.createTableData.getSelectedDatabaseName().equals(selectedDbName)) {
						//updatePreferenceStore(selectedDbName);
						//DatabaseConnectionSettings.setReload(true);
						final ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();
						connectToDatabase.closeConnection(ConnectToDatabase.getCon());
						final DatabaseDetails databaseDetails = databaseConnectionSettings.getConnMap().get(selectedDbName);
						final DatabaseCache databaseCache = DatabaseCache.getInstance();
						CreateTableDialog.this.dbType = databaseDetails.getDatabaseType();
						Connection connection = null;
						try {
							connection = connectToDatabase.getNewConnection(selectedDbName);
							CreateTableDialog.this.createTableData.setGetNewConnection(true);
							getSchemaFromDb(connection, databaseDetails.getDatabaseType());
							CreateTableDialog.this.createTableData.setSchemasInDB(databaseCache.getDbSchemaListMap().get(
									databaseDetails.getDatabaseType()));
							populateSchemaCombo();
							if (CreateTableDialog.this.createTableData.isAddColumnsToExistingTable()) {
								poulateTableCombo(connectToDatabase);
							}
							CreateTableDialog.this.createTableData.setSelectedDatabaseName(selectedDbName);
							//getDatatypesList();

							if (stringColumnTypeButton.getSelection()) {
								populateColumnTypeCombo(stringColumnTypeButton.getText());
							} else if (numberColumnTypeButton.getSelection()) {
								populateColumnTypeCombo(numberColumnTypeButton.getText());
							} else if (dateTimeColumnTypeButton.getSelection()) {
								populateColumnTypeCombo("Datetime");
							} else if (OthersColumnTypeButton.getSelection()) {
								populateColumnTypeCombo(OthersColumnTypeButton.getText());
							}
							if (databaseDetails.getDatabaseType().equalsIgnoreCase(ORACLE)) {
								CreateTableDialog.this.lenByte.setVisible(true);
								CreateTableDialog.this.lenChar.setVisible(true);
							} else {
								CreateTableDialog.this.lenByte.setVisible(false);
								CreateTableDialog.this.lenChar.setVisible(false);
								CreateTableDialog.this.createTableData.setLenType(null);
							}
							if (CreateTableDialog.this.createTableData.isAddColumnsToExistingTable()) {
								CreateTableDialog.this.numberOfColumns.setText(EMPTY_STR);
							}
						} catch (final Exception ex) {
							ex.printStackTrace();
							//setErrorMessage(ex.getMessage());
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

			public void widgetSelected(final SelectionEvent event) {
				final String selectedDbName = removeSuffix(CreateTableDialog.this.databaseNameCombo.getText());

				if (!selectedDbName.equalsIgnoreCase(DatabaseConnectionSettings.getInstance().getNameofDabase())
						|| !CreateTableDialog.this.createTableData.getSelectedDatabaseName().equals(selectedDbName)) {
					//updatePreferenceStore(selectedDbName);
					//DatabaseConnectionSettings.setReload(true);
					final ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();
					connectToDatabase.closeConnection(ConnectToDatabase.getCon());
					final DatabaseDetails databaseConnection = databaseConnectionSettings.getConnMap().get(selectedDbName);
					final DatabaseCache databaseCache = DatabaseCache.getInstance();
					CreateTableDialog.this.dbType = databaseConnection.getDatabaseType();
					Connection connection = null;
					try {
						connection = connectToDatabase.getConnection(selectedDbName);
						getSchemaFromDb(connection, databaseConnection.getDatabaseType());
						CreateTableDialog.this.createTableData.setSchemasInDB(databaseCache.getDbSchemaListMap().get(
								databaseConnection.getDatabaseType()));
						populateSchemaCombo();
						poulateTableCombo(connectToDatabase);
						CreateTableDialog.this.createTableData.setSelectedDatabaseName(selectedDbName);
						if (databaseConnection.getDatabaseType().equalsIgnoreCase(ORACLE)) {
							CreateTableDialog.this.lenByte.setVisible(true);
							CreateTableDialog.this.lenChar.setVisible(true);
						} else {
							CreateTableDialog.this.lenByte.setVisible(false);
							CreateTableDialog.this.lenChar.setVisible(false);
						}
						CreateTableDialog.this.numberOfColumns.setText(EMPTY_STR);
					} catch (final Exception ex) {
						setErrorMessage(ex.getMessage());
					}

				}
			}

			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});*/
	}

	/**
	 * @param dbName
	 * @return
	 */
	private String removeSuffix(final String dbName) {
		return dbName.replace(DEFAULT_SUFFIX, EMPTY_STR);
	}

	/**
	 *
	 */
	private void populateSchemaCombo() {
		int schemaIndex = 0;
		int k = 0;
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		final DatabaseDetails databaseConnection = databaseConnectionSettings.getConnMap().get(
				removeSuffix(this.databaseNameCombo.getText()));
		final String defaultSchema = databaseConnection.getDatabaseType().equalsIgnoreCase(ORACLE) ? databaseConnection.getUserName()
				: databaseConnection.getDatabaseName();
		if (this.schemaCombo != null) {
			this.schemaCombo.removeAll();
		}
		for (final String schemaName : this.createTableData.getSchemasInDB().toArray(new String[0])) {
			if (schemaName.equalsIgnoreCase(defaultSchema)) {
				schemaIndex = k;
			}
			this.schemaCombo.add(schemaName);
			k++;
		}
		this.schemaCombo.select(schemaIndex);
		final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(this.schemaCombo.getItems());
		final ComboContentAdapter comboAdapter = new ComboContentAdapter();
		final ContentProposalAdapter adapter = new ContentProposalAdapter(this.schemaCombo, comboAdapter, provider, null, null);
		adapter.setPropagateKeys(true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
	}

	/**
	 * @param parent
	 */
	private void createColumnTypesRadioButtons(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout(6, false);
		composite.setLayout(layout);

		final Group group1 = new Group(composite, SWT.SHADOW_IN);
		group1.setText("Column Types:");
		final Device device = Display.getCurrent();
		final FontData[] fD = composite.getFont().getFontData();
		//columnTypesFont = new Font(device, fD[0].getName(), fD[0].getHeight(), SWT.SIMPLE);
		group1.setFont(FastCodeFont.getSimpleFont(fD[0].getName(), fD[0].getHeight()));
		final Composite radioBox = group1;
		final GridLayout layout1 = new GridLayout();
		layout1.numColumns = 7;
		layout1.makeColumnsEqualWidth = true;
		radioBox.setLayout(layout1);

		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		radioBox.setLayoutData(gd);

		this.stringColumnTypeButton = new Button(radioBox, SWT.RADIO);
		this.stringColumnTypeButton.setText("String");

		this.numberColumnTypeButton = new Button(radioBox, SWT.RADIO);
		this.numberColumnTypeButton.setText("Numeric");

		this.dateTimeColumnTypeButton = new Button(radioBox, SWT.RADIO);
		this.dateTimeColumnTypeButton.setText("Date Time");

		this.OthersColumnTypeButton = new Button(radioBox, SWT.RADIO);
		this.OthersColumnTypeButton.setText("Others");

		this.columnTypeCombo = new Combo(radioBox, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		this.columnTypeCombo.setLayoutData(new GridData(80, 20));
		this.columnTypeCombo.add("Column Types");
		this.columnTypeCombo.select(0);
		this.columnTypeCombo.setEnabled(false);

		this.stringColumnTypeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (CreateTableDialog.this.stringColumnTypeButton.getSelection()) {
					populateColumnTypeCombo(stringColumnTypeButton.getText());
					CreateTableDialog.this.columnTypeCombo.setEnabled(true);
					CreateTableDialog.this.columnTypeSize.setEnabled(true);
					CreateTableDialog.this.defaultValue.setText(EMPTY_STR);
					CreateTableDialog.this.createTableData.setDataType(STRING_CONSTANT);
					CreateTableDialog.this.lenByte.setEnabled(true);
					CreateTableDialog.this.lenChar.setEnabled(true);
				} else {
					CreateTableDialog.this.columnTypeCombo.setEnabled(false);
					CreateTableDialog.this.columnTypeCombo.select(0);
					//CreateTableDialog.this.defaultValue.setText(null);
					CreateTableDialog.this.createTableData.setColumnTypeSize(0);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		this.numberColumnTypeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (CreateTableDialog.this.numberColumnTypeButton.getSelection()) {
					//getDatatypesList(CreateTableDialog.this.dbType);
					populateColumnTypeCombo(numberColumnTypeButton.getText());
					CreateTableDialog.this.columnTypeCombo.setEnabled(true);
					CreateTableDialog.this.defaultValue.setText("0");
					CreateTableDialog.this.createTableData.setDataType(NUMBER);
					CreateTableDialog.this.columnTypePrecisionAndScale.setEnabled(true);
					CreateTableDialog.this.columnTypeSize.setEnabled(false);
					CreateTableDialog.this.columnTypeSize.setText(EMPTY_STR);
					CreateTableDialog.this.createTableData.setLenType(EMPTY_STR);
					CreateTableDialog.this.lenByte.setSelection(false);
					CreateTableDialog.this.lenChar.setSelection(false);
					CreateTableDialog.this.lenByte.setEnabled(false);
					CreateTableDialog.this.lenChar.setEnabled(false);
				} else {
					CreateTableDialog.this.columnTypeCombo.setEnabled(false);
					CreateTableDialog.this.columnTypeCombo.select(0);
					CreateTableDialog.this.defaultValue.setText(EMPTY_STR);
					CreateTableDialog.this.columnTypePrecisionAndScale.setEnabled(false);
					CreateTableDialog.this.columnTypePrecisionAndScale.setText(EMPTY_STR);
					CreateTableDialog.this.columnTypeSize.setEnabled(true);
					CreateTableDialog.this.createTableData
							.setColumnTypePrecisionAndScale(CreateTableDialog.this.columnTypePrecisionAndScale.getText());
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		this.dateTimeColumnTypeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (CreateTableDialog.this.dateTimeColumnTypeButton.getSelection()) {
					//getDatatypesList(CreateTableDialog.this.dbType);
					populateColumnTypeCombo("Datetime");
					CreateTableDialog.this.columnTypeCombo.setEnabled(true);
					CreateTableDialog.this.defaultValue.setText(getDefaultDate());
					CreateTableDialog.this.createTableData.setDataType(DATETIME);
					CreateTableDialog.this.createTableData.setLenType(EMPTY_STR);
					CreateTableDialog.this.columnTypeSize.setText(EMPTY_STR);
					CreateTableDialog.this.lenByte.setSelection(false);
					CreateTableDialog.this.lenChar.setSelection(false);
					CreateTableDialog.this.columnTypeSize.setEnabled(false);
					CreateTableDialog.this.lenByte.setEnabled(false);
					CreateTableDialog.this.lenChar.setEnabled(false);

				} else {
					CreateTableDialog.this.columnTypeCombo.setEnabled(false);
					CreateTableDialog.this.columnTypeCombo.select(0);
					CreateTableDialog.this.defaultValue.setText(EMPTY_STR);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

		this.OthersColumnTypeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				{
					if (CreateTableDialog.this.OthersColumnTypeButton.getSelection()) {
						//getDatatypesList(CreateTableDialog.this.dbType);
						populateColumnTypeCombo(OthersColumnTypeButton.getText());
						CreateTableDialog.this.columnTypeCombo.setEnabled(true);
						CreateTableDialog.this.defaultValue.setText(EMPTY_STR);
						CreateTableDialog.this.createTableData.setDataType("Others");
						CreateTableDialog.this.createTableData.setLenType(EMPTY_STR);
						CreateTableDialog.this.columnTypeSize.setText(EMPTY_STR);
						CreateTableDialog.this.lenByte.setSelection(false);
						CreateTableDialog.this.lenChar.setSelection(false);
					} else {
						CreateTableDialog.this.columnTypeCombo.setEnabled(false);
						CreateTableDialog.this.columnTypeCombo.select(0);
						CreateTableDialog.this.defaultValue.setText(null);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.columnTypeCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				/*if (columnTypeCombo.getSelectionIndex() == 0) {
					columnTypeCombo.select(1);
				}*/
				if (!isEmpty(CreateTableDialog.this.columnTypeCombo.getText())) {
					//CreateTableDialog.this.columnTypeSize.setEnabled(true);
					final String colType = CreateTableDialog.this.columnTypeCombo.getText().trim();
					/*if (colType.equalsIgnoreCase("decimal") || colType.equalsIgnoreCase("numeric")) {
						CreateTableDialog.this.columnTypePrecisionAndScale.setEnabled(true);
						CreateTableDialog.this.columnTypeSize.setEnabled(false);
					} else {
						CreateTableDialog.this.columnTypePrecisionAndScale.setEnabled(false);
						CreateTableDialog.this.columnTypeSize.setEnabled(true);
					}*/
					CreateTableDialog.this.createTableData.setColumnTypeSelected(colType);
					setErrorMessage(CreateTableDialog.this.defaultMessage);
				} else {
					CreateTableDialog.this.columnTypeSize.setEnabled(false);

				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * @return
	 */
	private String getDefaultDate() {
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		final DatabaseDetails databaseConnection = databaseConnectionSettings.getConnMap().get(
				removeSuffix(this.databaseNameCombo.getText()));
		if (databaseConnection.getDatabaseType().equalsIgnoreCase(ORACLE)) {
			return ORACLE_DEFAULT_DATE;
		} else if (databaseConnection.getDatabaseType().equalsIgnoreCase(MYSQL)) {
			return MYSQL_DEFAULT_DATE;
		} else if (databaseConnection.getDatabaseType().equalsIgnoreCase(POSTGRESQL)) {
			return POSTGRE_DEFAULT_DATE;
		}
		return null;
	}

	/**
	 * @param groupType
	 */
	protected void populateColumnTypeCombo(final String groupType) {
		final SQLDatatypesMapping sqldatatypesMapping = SQLDatatypesMapping.getInstance();
		final Map<String, ArrayList<SQLDatatypes>> groupBaseTypeMap = sqldatatypesMapping.getDatabaseDataTypeMap().get(this.dbType);
		if (this.columnTypeCombo != null) {
			this.columnTypeCombo.removeAll();
		}

		for (final SQLDatatypes columnType : getEmptyListForNull(groupBaseTypeMap.get(groupType))) {
			this.columnTypeCombo.add(columnType.getType().trim());
		}

	}

	/**
	 * @param parent
	 * @param labelText
	 * @param style
	 * @return
	 */
	private Text createColumnNameText(final Composite parent, final String labelText, final int style) {
		final GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		final Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);

		final Text text = new Text(parent, SWT.BORDER | SWT.MULTI);
		text.setLayoutData(gridData);
		text.setSize(200, 20);
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent modifyEvent) {
				String[] columnNames = null;
				final String names = CreateTableDialog.this.columnNameText.getText();

				if (!isEmpty(names)) {
					columnNames = names.split("\\s+");
					for (final String colName : columnNames) {
						if (!isValidTableOrColumnName(colName)) {
							setErrorMessage("Special Characters except Underscore are not allowed in column names");
							return;
						} else {
							setErrorMessage(CreateTableDialog.this.defaultMessage);
						}
						if (CreateTableDialog.this.createTableData.isAddColumnsToExistingTable()) {
							boolean isExist = false;
							//final ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();

							try {
								final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
								final String databaseType = databaseConnectionSettings.getConnMap()
										.get(removeSuffix(CreateTableDialog.this.databaseNameCombo.getText())).getDatabaseType();
								for (final String columnDetails : getTableColumnsFromDB(CreateTableDialog.this.tableName,
										ConnectToDatabase.getCon(), CreateTableDialog.this.schemaCombo.getText(), databaseType)) {
									final String columnName = columnDetails.substring(0, columnDetails.indexOf(HYPHEN));
									if (columnName.equalsIgnoreCase(colName)) {
										isExist = true;
										break;
									}
								}
								if (isExist) {
									setErrorMessage("Column Name- " + colName + " already exist,Please enter new name");
									return;
								} else {
									setErrorMessage(CreateTableDialog.this.defaultMessage);
								}
							} catch (final Exception ex) {
								ex.printStackTrace();
							}
						}
					}
					boolean duplicate = false;
					for (int j = 0; j < columnNames.length; j++) {
						for (int k = j + 1; k < columnNames.length; k++) {
							if (columnNames[k].equals(columnNames[j])) {
								duplicate = true;
								break;
							}
							if (duplicate) {
								break;
							}
						}
					}
					if (duplicate) {
						setErrorMessage("You have given the same column name more than once");
						return;
					}

				} else {
					setErrorMessage("Column Names can not be blank");
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
	private Text createTableText(final Composite parent, final String labelText, final int style) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final GridData gridData = new GridData(250, 20);
		gridData.grabExcessHorizontalSpace = true;
		//gridData.horizontalAlignment = GridData.FILL;
		final Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);

		final Text text = new Text(composite, SWT.BORDER | SWT.MULTI);
		text.setLayoutData(gridData);
		text.setSize(200, 20);
		text.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent focusEvent) {
				final String newTableName = CreateTableDialog.this.tableNameText.getText();

				if (isEmpty(newTableName)) {
					setErrorMessage("Table Name Can not be blank");
					return;
				} else {
					setErrorMessage(CreateTableDialog.this.defaultMessage);
				}
				//have to check special character
				if (!isValidTableOrColumnName(newTableName)) {
					setErrorMessage("Special Characters except underscore are not allowed in table names");
					return;
				} else {
					setErrorMessage(CreateTableDialog.this.defaultMessage);
				}
				if (!CreateTableDialog.this.createTableData.isAddColumnsToExistingTable()) {
					for (final String existingTableName : getEmptyListForNull(CreateTableDialog.this.createTableData.getTablesInDB())) {
						if (newTableName.equals(existingTableName)) {
							setErrorMessage("Table altrady exist in selected schema , Please provide another name");
							return;
						} else {
							setErrorMessage(CreateTableDialog.this.defaultMessage);
						}
					}
				}
				CreateTableDialog.this.tableName = newTableName;

			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent modifyEvent) {
				final String newTableName = CreateTableDialog.this.tableNameText.getText();

				if (isEmpty(newTableName)) {
					setErrorMessage("Table Name Can not be blank");
					return;
				} else {
					setErrorMessage(CreateTableDialog.this.defaultMessage);
				}
				if (!isValidTableOrColumnName(newTableName)) {
					setErrorMessage("Special Characters except underscore are not allowed in table names");
					return;
				} else {
					setErrorMessage(CreateTableDialog.this.defaultMessage);
				}
				if (!CreateTableDialog.this.createTableData.isAddColumnsToExistingTable()) {
					for (final String existingTableName : getEmptyListForNull(CreateTableDialog.this.createTableData.getTablesInDB())) {
						if (newTableName.equals(existingTableName)) {
							setErrorMessage("Table altrady exist in selected schema , Please provide another name");
							return;
						} else {
							setErrorMessage(CreateTableDialog.this.defaultMessage);
						}
					}
				}
				CreateTableDialog.this.tableName = newTableName;
			}
		});
		return text;
	}

	/**
	 * @param parent
	 */
	private void createExistingTableSelectionPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData tableLableGrid = new GridData();
		final Label tableLabel = new Label(composite, SWT.NONE);
		tableLabel.setText("Select Table:                     ");
		tableLabel.setLayoutData(tableLableGrid);

		final GridData tableComboGrid = new GridData(200, 20);
		tableComboGrid.grabExcessHorizontalSpace = true;
		this.existingTableCombo = new Combo(composite, SWT.DROP_DOWN);
		this.existingTableCombo.setLayoutData(tableComboGrid);

		final GridData nummOfColGrid = new GridData(300, 20);
		this.numberOfColumns = new Label(composite, SWT.NONE);
		this.numberOfColumns.setText(EMPTY_STR);
		this.numberOfColumns.setLayoutData(nummOfColGrid);

		final ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();
		poulateTableCombo(connectToDatabase);
		getNumberOfRec(CreateTableDialog.this.existingTableCombo.getText());
		this.existingTableCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (CreateTableDialog.this.createTableData.isAddColumnsToExistingTable()) {
					/*CreateTableDialog.this.tableNameText.setText(CreateTableDialog.this.existingTableCombo.getText());
					CreateTableDialog.this.tableNameText.setEnabled(false);*/
					CreateTableDialog.this.tableName = CreateTableDialog.this.existingTableCombo.getText();
					getNumberOfRec(CreateTableDialog.this.existingTableCombo.getText());
					setErrorMessage(CreateTableDialog.this.defaultMessage);

				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.existingTableCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent modifyEvent) {
				if (CreateTableDialog.this.createTableData.isAddColumnsToExistingTable()) {
					/*CreateTableDialog.this.tableNameText.setText(CreateTableDialog.this.existingTableCombo.getText());
					CreateTableDialog.this.tableNameText.setEnabled(false);*/
					CreateTableDialog.this.tableName = CreateTableDialog.this.existingTableCombo.getText();
					getNumberOfRec(CreateTableDialog.this.existingTableCombo.getText());
					setErrorMessage(CreateTableDialog.this.defaultMessage);
				}
			}
		});
	}

	/**
	 * @param text
	 */
	private void getNumberOfRec(final String text) {
		final ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();
		Connection connection = null;
		try {
			connection = ConnectToDatabase.getCon() == null ? connectToDatabase.getNewConnection(removeSuffix(this.databaseNameCombo
					.getText())) : ConnectToDatabase.getCon();
			if (this.existingTableCombo.getSelectionIndex() > -1) {
				final int numRec = getNumberOfRecordsInTable(connection, this.schemaCombo.getText(),
						this.existingTableCombo.getItem(this.existingTableCombo.getSelectionIndex()));
				if (numRec == 0) {
					this.numberOfColumns.setText("The table is empty.");
				} else if (numRec > 0) {
					this.numberOfColumns.setText("The table has data.");
				}

			}
		} catch (final Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * @param parent
	 */
	private void createSchemaSelectionPane(final Composite parent) {
		final Composite cmposite = new Composite(parent, parent.getStyle());

		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		cmposite.setLayout(layout);

		final Label schemaLabel = new Label(cmposite, SWT.NONE);
		schemaLabel.setText("Schema:                            ");
		schemaLabel.setLayoutData(new GridData());

		this.schemaCombo = new Combo(cmposite, SWT.DROP_DOWN); // |
																// SWT.READ_ONLY);

		this.schemaCombo.setLayoutData(new GridData(150, 100));
		/*int schemaIndex = 0;
		int k = 0;
		String defaultSchema = EMPTY_STR;
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		if (databaseConnectionSettings.getTypesofDabases().equalsIgnoreCase(ORACLE)) {
			defaultSchema = databaseConnectionSettings.getUserName();
		} else {
			defaultSchema = databaseConnectionSettings.getNameofDabase();
		}
		for (final String schemaName : this.createTableData.getSchemasInDB().toArray(new String[0])) {
			if (schemaName.equalsIgnoreCase(defaultSchema)) {
				schemaIndex = k;
			}
			this.schemaCombo.add(schemaName);
			k++;
		}
		this.schemaCombo.select(schemaIndex);
		final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(this.schemaCombo.getItems());
		final ComboContentAdapter comboAdapter = new ComboContentAdapter();
		final ContentProposalAdapter adapter = new ContentProposalAdapter(this.schemaCombo, comboAdapter, provider, null, null);
		adapter.setPropagateKeys(true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);*/

		populateSchemaCombo();
		final ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();

		this.schemaCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				if (CreateTableDialog.this.createTableData.isAddColumnsToExistingTable()) {
					poulateTableCombo(connectToDatabase);
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
	 */
	private void poulateTableCombo(final ConnectToDatabase connectToDatabase) {
		Connection connection = null;
		try {
			connection = ConnectToDatabase.getCon() == null ? connectToDatabase.getNewConnection(removeSuffix(this.databaseNameCombo
					.getText())) : ConnectToDatabase.getCon();
			final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
			final String databaseType = databaseConnectionSettings.getConnMap().get(removeSuffix(this.databaseNameCombo.getText()))
					.getDatabaseType();
			getTableFromDb(connection, CreateTableDialog.this.schemaCombo.getText(), databaseType);
			CreateTableDialog.this.existingTableCombo.removeAll();
			final DatabaseCache databaseCache = DatabaseCache.getInstance();
			for (final String tableName : getEmptyArrayForNull(databaseCache.getDbTableListMap()
					.get(CreateTableDialog.this.schemaCombo.getText()).toArray(new String[0]))) {
				CreateTableDialog.this.existingTableCombo.add(tableName);
			}
			CreateTableDialog.this.createTableData.setSchemaSelected(CreateTableDialog.this.schemaCombo.getText());
			final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(
					CreateTableDialog.this.existingTableCombo.getItems());
			final ComboContentAdapter comboAdapter = new ComboContentAdapter();
			final ContentProposalAdapter adapter = new ContentProposalAdapter(CreateTableDialog.this.existingTableCombo, comboAdapter,
					provider, null, null);
			adapter.setPropagateKeys(true);
			adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
			CreateTableDialog.this.createTableData.setTablesInDB(databaseCache.getDbTableListMap().get(
					CreateTableDialog.this.schemaCombo.getText()));
		} catch (final Exception ex) {
			ex.printStackTrace();
		}/*finally {
			connectToDatabase.closeConnection(connection);
			}*/
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
		setErrorMessage(this.defaultMessage);

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
			final Control submitButton = getButton(this.SUBMT_ID);
			if (button != null) {
				button.setEnabled(errorMessage.equals(this.defaultMessage));
			}
			if (submitButton != null) {
				submitButton.setEnabled(errorMessage.equals(this.defaultMessage));
			}
		}
	}

	@Override
	/*protected Control createButtonBar(Composite parent) {
		Composite buttonBar = new Composite(contents, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalAlignment = SWT.RIGHT;
		buttonBar.setLayoutData(data);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttonBar.setLayout(layout);

		Button okButton = new Button(buttonBar, SWT.PUSH);
		data = new GridData(BUTTON_WIDTH, SWT.DEFAULT);
		okButton.setLayoutData(data);
		okButton.setText(Messages.Dialog_InstallButton);
		okButton.setEnabled(false);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				buttonPressed(OK);
			}
		});

		Button cancelButton = new Button(buttonBar, SWT.PUSH);
		data = new GridData(BUTTON_WIDTH, SWT.DEFAULT);
		cancelButton.setLayoutData(data);
		cancelButton.setText(Messages.Dialog_CancelButton);
		cancelButton.setEnabled(false);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				buttonPressed(CANCEL);
			}
		return buttonBar;
	}*/
	protected void createButtonsForButtonBar(final Composite parent) {
		//createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
		createButton(parent, IDialogConstants.OK_ID, "Submit and Close", true);
		createSubmitButton(parent);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * @param parent
	 */
	public void createSubmitButton(final Composite parent) {
		this.submitButton = createButton(parent, this.SUBMT_ID, "Submit", true);
		this.submitButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				if (!validateData()) {
					return;
				} else {
					try {
						CreateTableDialog.this.createTableDialogCallback.submitPressed(CreateTableDialog.this.createTableData);
					} catch (final Exception ex) {
						setErrorMessage("Error - " + ex.getMessage());
						return;
					}
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
	}

}

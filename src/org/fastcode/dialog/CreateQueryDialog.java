package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.HQL_NAMED_QUERY_WITH_ANNOTATION_STR;
import static org.fastcode.common.FastCodeConstants.NAMED_QUERY;
import static org.fastcode.common.FastCodeConstants.NAMED_QUERY_ANNOTATION_STR;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.ORACLE;
import static org.fastcode.common.FastCodeConstants.SQL_QUERY;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_INSERT_NAMED_QUERY;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES;
import static org.fastcode.preferences.PreferenceConstants.P_HQL_NAMED_QUERY_FILE_LOCATION;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.util.DatabaseUtil.getSchemaFromDb;
import static org.fastcode.util.DatabaseUtil.getTableFromDb;
import static org.fastcode.util.FastCodeUtil.getEmptyArrayForNull;
import static org.fastcode.util.SourceUtil.findFileFromPath;
import static org.fastcode.util.SourceUtil.getFileContents;
import static org.fastcode.util.SourceUtil.getTypeFromWorkspace;
import static org.fastcode.util.StringUtil.isEmpty;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.internal.ide.dialogs.OpenResourceDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.CreateQueryData;
import org.fastcode.common.DatabaseDetails;
import org.fastcode.common.FastCodeColor;
import org.fastcode.common.FastCodeConstants.FIELDS;
import org.fastcode.common.FastCodeConstants.QUERY_CHOICES;
import org.fastcode.common.FastCodeConstants.WHERE_CLAUSE_QUALIFIER;
import org.fastcode.common.FastCodeConstants.WHERE_CLAUSE_SEPARATOR;
import org.fastcode.popup.actions.snippet.FastCodeCache;
import org.fastcode.preferences.DatabaseConnectionSettings;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.templates.viewer.TemplateFieldEditor;
import org.fastcode.util.ConnectToDatabase;
import org.fastcode.util.DatabaseCache;
import org.fastcode.util.FastCodeContentProposalProvider;
import org.fastcode.util.StringUtil;

public class CreateQueryDialog extends TrayDialog {

	Shell						shell;
	private CreateQueryData		createQueryData;
	private Button				newNamedQueryButton;
	private Button				existingNamedQueryButton;
	private Button				equalButton;
	private Button				notEqualButton;
	private Button				likeButton;
	private Button				notLikeButton;
	private Button				andButton;
	private Button				orButton;
	private Combo				existingNamedQueriesCombo;
	private Text				namedQueryFileNameText;
	private Button				browseNamedQueryFile;
	private Button				browseSelectClassName;
	IPreferenceStore			preferenceStore;
	IFile						namedQueryFile;
	// private Text selectClassNameText;
	// private String selectClassName;
	private Combo				selectClassCombo;
	IType						iSelectClassType;
	private Text				newNamedQueryText;
	private String				namedQueryFileContents;
	private String[]			namedQueries	= null;
	private Combo				tableCombo;
	private String				errorMessage;
	private Text				errorMessageText;
	// private Text selectPojoClassText;
	private Combo				pojoClassCombo;
	private Button				pojoClassBrowseButton;
	// IType iSelectPojoClassType;
	private final String		defaultMessage	= NEWLINE;
	private TemplateFieldEditor	templateBodyMultiText;
	private String				modifiedTemplateBody;
	private Combo				schemaCombo;
	Map<String, Object>			browsedClassMap	= new HashMap<String, Object>();
	private Combo				databaseNameCombo;

	/**
	 * @param shell
	 */
	public CreateQueryDialog(final Shell shell) {
		super(shell);
		this.shell = shell;
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
	}

	/**
	 * @param shell
	 * @param createQueryData
	 */
	public CreateQueryDialog(final Shell shell, final CreateQueryData createQueryData) {
		super(shell);
		this.shell = shell;
		this.createQueryData = createQueryData;
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
	}

	@Override
	public void create() {
		super.create();
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Create Named Query");
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);

		createErrorMessageText(parent);
		createQueryChoicesButtons(parent);

		// try {
		this.namedQueryFileNameText = createNamedQueryFileName(parent, "Named Query File:             ", 0);
		/*
		 * } catch (final Exception ex) { ex.printStackTrace();
		 * this.closeTray(); }
		 */

		existingNamedQueryCollection(parent);
		this.newNamedQueryText = createNewNamedQuery(parent, "New Query Name:             ", 0);
		if (this.createQueryData.getTemplatePrefix().equals(TEMPLATE)) {
			createSelectClassPane(parent);

		} else if (this.createQueryData.getTemplatePrefix().equals(P_DATABASE_TEMPLATE_PREFIX)) {
			createDatabaseNameSelectionPane(parent);
			createSchemaSelectionPane(parent);
			createTableSelectionPane(parent);
		}
		if (this.preferenceStore.getBoolean(P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES)) {
			createTemplateBodyMultiText(parent);
		}
		if (!this.createQueryData.getTemplatePrefix().equals(P_DATABASE_TEMPLATE_PREFIX)) {
			createWhereClauseQualifierButtons(parent);
		}
		if (!this.createQueryData.getTemplateType().equals(DATABASE_TEMPLATE_INSERT_NAMED_QUERY)) {

			createWhereClauseSeparatorButtons(parent);
		}
		if (this.createQueryData.getTemplatePrefix().equals(P_DATABASE_TEMPLATE_PREFIX)
				&& this.createQueryData.getTemplateType().endsWith(NAMED_QUERY_ANNOTATION_STR)) {
			createPojoClassSelectionPane(parent);
		}
		return parent;
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
		schemaLabel.setText("Database Names:               ");
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
		this.createQueryData.setSelectedDatabaseName(this.databaseNameCombo.getText());
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
							|| !createQueryData.getSelectedDatabaseName().equals(selectedDbName)) {
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
							createQueryData.setSchemasInDB(databaseCache.getDbSchemaListMap().get(databaseConnection.getDatabaseType()));
							populateSchemaCombo();
							poulateTableCombo(connectToDatabase);
							createQueryData.setSelectedDatabaseName(selectedDbName);

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
						|| !createQueryData.getSelectedDatabaseName().equals(selectedDbName)) {
					//updatePreferenceStore(selectedDbName);
					//DatabaseConnectionSettings.setReload(true);
					ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();
					connectToDatabase.closeConnection(ConnectToDatabase.getCon());
					final DatabaseDetails databaseConnection = databaseConnectionSettings.getConnMap().get(selectedDbName);
					final DatabaseCache databaseCache = DatabaseCache.getInstance();
					Connection connection = null;
					try {
						connection = connectToDatabase.getConnection(selectedDbName);
						getSchemaFromDb(connection, databaseConnection.getDatabaseType());
						createQueryData.setSchemasInDB(databaseCache.getDbSchemaListMap().get(databaseConnection.getDatabaseType()));
						populateSchemaCombo();
						poulateTableCombo(connectToDatabase);
						createQueryData.setSelectedDatabaseName(selectedDbName);
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {
						connectToDatabase.closeConnection(connection);
						}

				}
			}

			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});*/
	}

	/**
	 *
	 */
	private void populateSchemaCombo() {
		int schemaIndex = 0;
		int k = 0;
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		final DatabaseDetails databaseConnection = databaseConnectionSettings.getConnMap().get(this.databaseNameCombo.getText());
		final String defaultSchema = databaseConnection.getDatabaseType().equalsIgnoreCase(ORACLE) ? databaseConnection.getUserName()
				: databaseConnection.getDatabaseName();
		if (this.schemaCombo != null) {
			this.schemaCombo.removeAll();
		}
		for (final String schemaName : this.createQueryData.getSchemasInDB().toArray(new String[0])) {
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
	private void createTemplateBodyMultiText(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		final GridData gridDataText = new GridData(550, 100);
		this.templateBodyMultiText = new TemplateFieldEditor("snippet", "Template Body:                  ", composite, "snippet",
				FIELDS.TEMPLATE_BODY, SWT.MULTI);
		this.templateBodyMultiText.setLayout(gridDataText);

		if (this.createQueryData.getTemplateSettings().getTemplateBody() != null
				&& this.preferenceStore.getBoolean(P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES)) {
			this.templateBodyMultiText.setText(this.createQueryData.getTemplateSettings().getTemplateBody());
		}

	}

	/**
	 * @param parent
	 */
	private void createTableSelectionPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());

		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final GridData tableLableGrid = new GridData();
		final Label tableLabel = new Label(composite, SWT.NONE);
		tableLabel.setText("Select Table:                       ");
		tableLabel.setLayoutData(tableLableGrid);

		final GridData tableComboGrid = new GridData(200, 20);
		tableComboGrid.grabExcessHorizontalSpace = true;
		this.tableCombo = new Combo(composite, SWT.DROP_DOWN); // |
																// SWT.READ_ONLY);
		this.tableCombo.setLayoutData(tableComboGrid);

		/*
		 * for (final String table : this.createQueryData.getTablesInDB()) {
		 * this.tableCombo.add(table); }
		 */
		final ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();
		poulateTableCombo(connectToDatabase);
		this.tableCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				CreateQueryDialog.this.setErrorMessage(CreateQueryDialog.this.defaultMessage);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}
		});

		this.tableCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				CreateQueryDialog.this.createQueryData.setTableName(CreateQueryDialog.this.tableCombo.getText());

				if (!isEmpty(CreateQueryDialog.this.createQueryData.getTableName())) {
					setErrorMessage(CreateQueryDialog.this.defaultMessage);
					if (!CreateQueryDialog.this.createQueryData.getTablesInDB().contains(
							CreateQueryDialog.this.createQueryData.getTableName())) {
						setErrorMessage("This table is not there in the DB.");
					}
				} else {
					setErrorMessage("Please choose a table.");
				}
			}
		});
		this.tableCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent arg0) {
				setErrorMessage(CreateQueryDialog.this.defaultMessage);

			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				if (CreateQueryDialog.this.schemaCombo.getText().equals(EMPTY_STR)) {
					setErrorMessage("Please select a schema first");
				}
			}
		});

	}

	@Override
	protected Control createButtonBar(final Composite parent) {
		return super.createButtonBar(parent);
	}

	@Override
	protected void okPressed() {
		if (this.createQueryData == null) {
			this.createQueryData = new CreateQueryData();
		}

		// this.namedQueryFileName = this.namedQueryFileNameText.getText();
		this.createQueryData.setNamedQueryFileName(this.namedQueryFileNameText.getText());

		this.createQueryData.setiNamedqueryFile(this.namedQueryFile);
		this.createQueryData.setNamedQueryFileContent(this.namedQueryFileContents);

		if (this.namedQueryFile == null) {
			setErrorMessage("Unable to find name query file.");
			return;
		} else {
			setErrorMessage(this.defaultMessage);
		}
		/*
		 * if (this.newNamedQueryButton.getSelection()) {
		 * this.createQueryData.setQueryChoices
		 * (QUERY_CHOICES.CREATE_NEW_NAMED_QUERY);
		 * this.createQueryData.setChoice(0); } else if
		 * (this.existingNamedQueryButton.getSelection()) {
		 * this.createQueryData.
		 * setQueryChoices(QUERY_CHOICES.USE_EXISTING_NAMED_QUERY);
		 * this.createQueryData.setChoice(1); }
		 */

		if (this.createQueryData.getQueryChoices().equals(QUERY_CHOICES.USE_EXISTING_NAMED_QUERY)
				&& this.existingNamedQueriesCombo.getSelectionIndex() == -1) {
			setErrorMessage("Please select an existing named query.");
			return;
		} else {
			setErrorMessage(this.defaultMessage);
		}
		if (this.existingNamedQueriesCombo.getSelectionIndex() != -1) {
			final String exstTypeNamedQuery = this.existingNamedQueriesCombo.getItem(this.existingNamedQueriesCombo.getSelectionIndex());
			this.createQueryData.setExistingNamedQueryType(exstTypeNamedQuery);
		}
		this.createQueryData.setNewNamedQueryType(this.newNamedQueryText.getText());
		if (isEmpty(this.newNamedQueryText.getText())) {
			setErrorMessage("Query Name can not be blank");
			return;
		} else {
			setErrorMessage(this.defaultMessage);
		}
		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (this.createQueryData.getSelectClassName() == null && this.createQueryData.getiSelectClassType() == null
				&& this.createQueryData.getTemplatePrefix().equals(TEMPLATE)) {
			setErrorMessage("Please select a class");
			return;
		} else {
			setErrorMessage(this.defaultMessage);
		}
		if (this.selectClassCombo != null && this.selectClassCombo.isEnabled() && this.createQueryData.getSelectClassName() != null
				&& this.createQueryData.getiSelectClassType() != null && this.createQueryData.getTemplatePrefix().equals(TEMPLATE)) {
			final IType type = this.createQueryData.getiSelectClassType();
			if (!fastCodeCache.getTypeSet().contains(type)) {
				fastCodeCache.getTypeSet().add(type);
			}
		}
		if (this.tableCombo != null) {
			if (this.tableCombo.getSelectionIndex() == -1 && isEmpty(this.tableCombo.getText())) {
				setErrorMessage("Please select table.");
				return;
			} else {
				setErrorMessage(this.defaultMessage);
			}
			//have to modify the if conditions above and below -- nth minute change -- impact of making table selection editable
			if (this.tableCombo.getSelectionIndex() != -1) {
				this.createQueryData.setTableName(this.tableCombo.getItem(this.tableCombo.getSelectionIndex()));
			} else if (!isEmpty(this.tableCombo.getText())) {
				this.createQueryData.setTableName(this.tableCombo.getText());
			}

		}

		this.createQueryData.setExistingNamedQueries(this.namedQueries);

		if (this.createQueryData.getiSelectPojoClassType() == null
				&& this.createQueryData.getTemplateType().endsWith(NAMED_QUERY_ANNOTATION_STR)) {
			setErrorMessage("Please select a POJO class");
			return;
		} else {
			setErrorMessage(this.defaultMessage);
		}
		if (this.pojoClassCombo != null && this.pojoClassCombo.isEnabled() && this.createQueryData.getiSelectPojoClassType() != null) {
			final IType pojoClassType = this.createQueryData.getiSelectPojoClassType();
			if (!fastCodeCache.getTypeSet().contains(pojoClassType)) {
				fastCodeCache.getTypeSet().add(pojoClassType);
			}
		}
		if (this.templateBodyMultiText != null) {
			this.modifiedTemplateBody = this.templateBodyMultiText.getStringValue();
			if (this.modifiedTemplateBody != null) {
				this.createQueryData.setModifiedTemplateBody(this.modifiedTemplateBody);
			} else {
				this.createQueryData.setModifiedTemplateBody(this.createQueryData.getTemplateSettings().getTemplateBody());
			}
		}
		super.okPressed();
	}

	/**
	 * @param parent
	 */
	private void createQueryChoicesButtons(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 9;
		composite.setLayout(layout);

		final Label label = new Label(composite, SWT.NONE);
		label.setText("Query Choices:                   ");
		this.newNamedQueryButton = new Button(composite, SWT.RADIO);
		this.newNamedQueryButton.setText("Create new named query");
		this.newNamedQueryButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.widget).getSelection()) {
					CreateQueryDialog.this.createQueryData.setQueryChoices(QUERY_CHOICES.CREATE_NEW_NAMED_QUERY);
					CreateQueryDialog.this.createQueryData.setChoice(0);
					if (!isEmpty(CreateQueryDialog.this.newNamedQueryText.getText())
							&& CreateQueryDialog.this.existingNamedQueriesCombo.getItem(CreateQueryDialog.this.existingNamedQueriesCombo
									.getSelectionIndex()) != null) {
						if (CreateQueryDialog.this.newNamedQueryText.getText().equals(
								CreateQueryDialog.this.existingNamedQueriesCombo.getItem(CreateQueryDialog.this.existingNamedQueriesCombo
										.getSelectionIndex()))) {
							setErrorMessage("Named Query Exists, please select another name");
						} else {
							setErrorMessage(CreateQueryDialog.this.defaultMessage);
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		this.existingNamedQueryButton = new Button(composite, SWT.RADIO);
		this.existingNamedQueryButton.setText("Use an existing named query");
		if (this.createQueryData.getTemplateType().endsWith(HQL_NAMED_QUERY_WITH_ANNOTATION_STR)
				|| this.createQueryData.getTemplateType().endsWith(NAMED_QUERY_ANNOTATION_STR)) {
			this.existingNamedQueryButton.setEnabled(false);
		} else {
			this.existingNamedQueryButton.setEnabled(true);
		}
		this.existingNamedQueryButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.widget).getSelection()) {
					CreateQueryDialog.this.createQueryData.setQueryChoices(QUERY_CHOICES.USE_EXISTING_NAMED_QUERY);
					CreateQueryDialog.this.createQueryData.setChoice(1);
					if (!isEmpty(CreateQueryDialog.this.newNamedQueryText.getText())
							&& CreateQueryDialog.this.existingNamedQueriesCombo.getItem(CreateQueryDialog.this.existingNamedQueriesCombo
									.getSelectionIndex()) != null) {
						if (CreateQueryDialog.this.newNamedQueryText.getText().equals(
								CreateQueryDialog.this.existingNamedQueriesCombo.getItem(CreateQueryDialog.this.existingNamedQueriesCombo
										.getSelectionIndex()))) {
							setErrorMessage(CreateQueryDialog.this.defaultMessage);
						}
					}
					CreateQueryDialog.this.newNamedQueryText.setEnabled(false);
				} else {
					CreateQueryDialog.this.newNamedQueryText.setEnabled(true);
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
	private void createWhereClauseQualifierButtons(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 8;
		composite.setLayout(layout);
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Where Clause Qualifier:     ");
		this.equalButton = new Button(composite, SWT.RADIO);
		this.equalButton.setText("=");
		this.equalButton.setSelection(true);
		this.createQueryData.setWhereClauseQualifier(WHERE_CLAUSE_QUALIFIER.EQUAL);
		this.equalButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.widget).getSelection()) {
					CreateQueryDialog.this.createQueryData.setWhereClauseQualifier(WHERE_CLAUSE_QUALIFIER.EQUAL);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.notEqualButton = new Button(composite, SWT.RADIO);
		this.notEqualButton.setText("!=");
		this.notEqualButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.widget).getSelection()) {
					CreateQueryDialog.this.createQueryData.setWhereClauseQualifier(WHERE_CLAUSE_QUALIFIER.NOT_EQUAL);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.likeButton = new Button(composite, SWT.RADIO);
		this.likeButton.setText("like");
		this.likeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.widget).getSelection()) {
					CreateQueryDialog.this.createQueryData.setWhereClauseQualifier(WHERE_CLAUSE_QUALIFIER.LIKE);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.notLikeButton = new Button(composite, SWT.RADIO);
		this.notLikeButton.setText("not like");
		this.notLikeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.widget).getSelection()) {
					CreateQueryDialog.this.createQueryData.setWhereClauseQualifier(WHERE_CLAUSE_QUALIFIER.NOT_LIKE);
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
	private void createWhereClauseSeparatorButtons(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Where Clause Separator:   ");
		this.andButton = new Button(composite, SWT.RADIO);
		this.andButton.setText("and");
		this.andButton.setSelection(true);
		this.createQueryData.setWhereClauseSeparator(WHERE_CLAUSE_SEPARATOR.AND);
		this.andButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.widget).getSelection()) {
					CreateQueryDialog.this.createQueryData.setWhereClauseSeparator(WHERE_CLAUSE_SEPARATOR.AND);
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
					CreateQueryDialog.this.createQueryData.setWhereClauseSeparator(WHERE_CLAUSE_SEPARATOR.OR);
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
	 * @param labelText
	 * @param style
	 * @return
	 */
	protected Text createNamedQueryFileName(final Composite parent, final String labelText, final int style) {
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

		final Text text = new Text(composite, SWT.BORDER | SWT.MULTI);
		text.setSize(200, 20);
		text.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 500;

		final GridData gridDataButton = new GridData();

		this.browseNamedQueryFile = new Button(composite, SWT.PUSH);
		this.browseNamedQueryFile.setText("Browse");
		this.browseNamedQueryFile.setLayoutData(gridDataButton);
		this.browseNamedQueryFile.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				try {
					final OpenResourceDialog resourceDialog = new OpenResourceDialog(new Shell(), ResourcesPlugin.getWorkspace().getRoot(),
							IResource.FILE);
					resourceDialog.setTitle("Named Query File");
					resourceDialog.setMessage("Choose Named Query File");
					final int res = resourceDialog.open();
					if (res == CANCEL || resourceDialog.getResult() == null || resourceDialog.getResult().length == 0) {
						return;
					}
					CreateQueryDialog.this.namedQueryFile = (IFile) resourceDialog.getFirstResult();
					if (CreateQueryDialog.this.namedQueryFile == null) {
						CreateQueryDialog.this.setErrorMessage("Unable to find name query file.");
						return;
					} else {
						CreateQueryDialog.this.setErrorMessage(CreateQueryDialog.this.defaultMessage);
					}

					if (!CreateQueryDialog.this.namedQueryFile.isSynchronized(0)) {
						CreateQueryDialog.this.setErrorMessage(CreateQueryDialog.this.namedQueryFile.getName()
								+ " is not Synchronized, please refresh and try again.");
					} else {
						CreateQueryDialog.this.setErrorMessage(CreateQueryDialog.this.defaultMessage);
					}

					final IPath fullPath = CreateQueryDialog.this.namedQueryFile.getFullPath();
					CreateQueryDialog.this.namedQueryFileNameText.setText(CreateQueryDialog.this.namedQueryFile.getFullPath().toString());
					CreateQueryDialog.this.preferenceStore.setValue(P_HQL_NAMED_QUERY_FILE_LOCATION, fullPath.toString());
					CreateQueryDialog.this.namedQueries = CreateQueryDialog.this.getNamedQueries();
					CreateQueryDialog.this.existingNamedQueriesCombo.removeAll();
					for (final String query : getEmptyArrayForNull(CreateQueryDialog.this.namedQueries)) {
						CreateQueryDialog.this.existingNamedQueriesCombo.add(query);

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

		final IFile namedQueryFile = getFileFromPreferenceStore();
		if (namedQueryFile != null && !namedQueryFile.isSynchronized(0)) {
			MessageDialog.openError(this.shell, "Error", this.namedQueryFile.getName()
					+ " is not Synchronized, please refresh and try again.");
			return null;
		}
		text.setText(namedQueryFile != null ? this.namedQueryFile.getName() : EMPTY_STR);

		return text;

	}

	/**
	 * @param parent
	 * @param labelText
	 * @param style
	 * @return
	 */
	private Text createNewNamedQuery(final Composite parent, final String labelText, final int style) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		final Text text = new Text(composite, SWT.BORDER | SWT.MULTI);
		text.setLayoutData(new GridData(100, 15));
		text.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 500;
		text.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent event) {
				final String newNamedQuery = CreateQueryDialog.this.newNamedQueryText.getText();
				if (isEmpty(newNamedQuery)) {
					CreateQueryDialog.this.setErrorMessage("Query Name cannot be blank");
					return;
				} else {
					CreateQueryDialog.this.setErrorMessage(CreateQueryDialog.this.defaultMessage);
				}
				if (CreateQueryDialog.this.namedQueries != null) {
					for (final String namedQuery : getEmptyArrayForNull(CreateQueryDialog.this.namedQueries)) {
						if (newNamedQuery.equals(namedQuery) && CreateQueryDialog.this.newNamedQueryButton.getSelection()) {
							CreateQueryDialog.this.setErrorMessage("Named Query Exists, please select another name");
							return;
						} else {
							CreateQueryDialog.this.setErrorMessage(CreateQueryDialog.this.defaultMessage);
						}
					}
				}
			}

			@Override
			public void focusGained(final FocusEvent arg0) {

			}
		});
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent event) {
				final String newNamedQuery = CreateQueryDialog.this.newNamedQueryText.getText();
				if (isEmpty(newNamedQuery)) {
					CreateQueryDialog.this.setErrorMessage("Query Name cannot be blank");
					return;
				} else {
					CreateQueryDialog.this.setErrorMessage(CreateQueryDialog.this.defaultMessage);
				}
				if (CreateQueryDialog.this.namedQueries != null) {
					for (final String namedQuery : getEmptyArrayForNull(CreateQueryDialog.this.namedQueries)) {
						if (newNamedQuery.equals(namedQuery) && CreateQueryDialog.this.newNamedQueryButton.getSelection()) {
							CreateQueryDialog.this.setErrorMessage("Named Query Exists, please select another name");
							return;
						} else {
							CreateQueryDialog.this.setErrorMessage(CreateQueryDialog.this.defaultMessage);
						}
					}
				}
			}
		});
		return text;
	}

	/**
	 * @param parent
	 */
	private void createSelectClassPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Select Class:                        ");
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.selectClassCombo = new Combo(composite, SWT.NONE);
		this.selectClassCombo.setSize(200, 20);
		this.selectClassCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 500;

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (!fastCodeCache.getTypeSet().isEmpty()) {
			for (final IType type : fastCodeCache.getTypeSet()) {
				this.selectClassCombo.add(type.getFullyQualifiedName());
			}
		}
		this.selectClassCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String selectedClassName = CreateQueryDialog.this.selectClassCombo.getText();
				try {
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.getFullyQualifiedName().equals(selectedClassName)) {
								CreateQueryDialog.this.createQueryData.setSelectClassName(type.getFullyQualifiedName());
								CreateQueryDialog.this.createQueryData.setiSelectClassType(type);
							}
						}
					}
					if (CreateQueryDialog.this.createQueryData.getiSelectClassType() == null
							&& CreateQueryDialog.this.createQueryData.getSelectClassName() == null) {
						if (CreateQueryDialog.this.browsedClassMap.containsKey(selectedClassName)) {
							CreateQueryDialog.this.createQueryData.setiSelectClassType((IType) CreateQueryDialog.this.browsedClassMap
									.get(selectedClassName));
							CreateQueryDialog.this.createQueryData.setSelectClassName(selectedClassName);
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
		this.selectClassCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				final String inputClassName = CreateQueryDialog.this.selectClassCombo.getText();
				if (!isEmpty(inputClassName)) {
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.getFullyQualifiedName().equals(inputClassName)) {
								return;
							}
						}
					}
					if (CreateQueryDialog.this.browsedClassMap.containsKey(inputClassName)) {
						return;
					}
					try {
						final IType inputClassType = getTypeFromWorkspace(inputClassName);
						if (inputClassType != null) {
							CreateQueryDialog.this.createQueryData.setSelectClassName(inputClassType.getFullyQualifiedName());
							CreateQueryDialog.this.createQueryData.setiSelectClassType(inputClassType);
							if (!CreateQueryDialog.this.browsedClassMap.containsKey(inputClassType.getFullyQualifiedName())) {
								CreateQueryDialog.this.browsedClassMap.put(inputClassName, inputClassType);
							}
							/*if (!fastCodeCache.getTypeSet().contains(inputClassType)) {
								fastCodeCache.getTypeSet().add(inputClassType);
							}*/

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

		final GridData gridDataButton = new GridData();

		this.browseSelectClassName = new Button(composite, SWT.PUSH);
		this.browseSelectClassName.setText("Browse");
		this.browseSelectClassName.setLayoutData(gridDataButton);

		this.browseSelectClassName.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final SelectionDialog selectionDialog;
				try {
					selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
							SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, "");
					selectionDialog.setTitle("Select  Class");
					selectionDialog.setMessage("Select the  class");

					if (selectionDialog.open() == CANCEL) {
						return;
					}
					final IType browsedClassType = (IType) selectionDialog.getResult()[0];

					CreateQueryDialog.this.createQueryData.setSelectClassName(browsedClassType.getFullyQualifiedName());
					CreateQueryDialog.this.createQueryData.setiSelectClassType(browsedClassType);
					boolean addItem = true;
					if (CreateQueryDialog.this.selectClassCombo.getItems() != null) {
						for (final String existingClass : CreateQueryDialog.this.selectClassCombo.getItems()) {
							if (existingClass.equals(browsedClassType.getFullyQualifiedName())) {
								if (!CreateQueryDialog.this.selectClassCombo.getText().equals(existingClass)) {
									CreateQueryDialog.this.selectClassCombo.select(CreateQueryDialog.this.selectClassCombo
											.indexOf(existingClass));
								}
								addItem = false;
								break;
							}
						}
					}
					if (addItem) {
						CreateQueryDialog.this.selectClassCombo.add(browsedClassType.getFullyQualifiedName());
						CreateQueryDialog.this.selectClassCombo.select(CreateQueryDialog.this.selectClassCombo.getItemCount() - 1);
					}

					if (CreateQueryDialog.this.browsedClassMap.isEmpty()) {
						CreateQueryDialog.this.browsedClassMap.put(browsedClassType.getFullyQualifiedName(), browsedClassType);
					} else {
						if (!CreateQueryDialog.this.browsedClassMap.containsKey(browsedClassType.getFullyQualifiedName())) {
							CreateQueryDialog.this.browsedClassMap.put(browsedClassType.getFullyQualifiedName(), browsedClassType);
						}
					}
					/*CreateQueryDialog.this.selectClassCombo.setText(((IType) selectionDialog.getResult()[0]).getFullyQualifiedName());

					if (!fastCodeCache.getTypeSet().contains(selectionDialog.getResult()[0])) {
						fastCodeCache.getTypeSet().add((IType) selectionDialog.getResult()[0]);
					}*/

					if (CreateQueryDialog.this.createQueryData.getSelectClassName() == null
							&& CreateQueryDialog.this.createQueryData.getiSelectClassType() == null
							&& CreateQueryDialog.this.createQueryData.getTemplatePrefix().equals(TEMPLATE)) {
						setErrorMessage("Please select pojo class");
					} else {
						setErrorMessage(CreateQueryDialog.this.defaultMessage);
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
	 * @param labelText
	 * @param style
	 * @return
	 */
	/*
	 * private Text selectClassName(final Composite parent, final String
	 * labelText, final int style) { final Composite composite = new
	 * Composite(parent, parent.getStyle()); final GridLayout layout = new
	 * GridLayout(); layout.numColumns = 3; composite.setLayout(layout);
	 *
	 * final GridData gridDataLabel = new GridData(); final Label label = new
	 * Label(composite, SWT.NONE); label.setText(labelText);
	 * label.setLayoutData(gridDataLabel);
	 *
	 * final GridData gridDataText = new GridData();
	 * gridDataText.grabExcessHorizontalSpace = true;
	 *
	 * final Text text = new Text(composite, SWT.BORDER | SWT.MULTI);
	 * text.setSize(200, 20); text.setLayoutData(gridDataText);
	 * gridDataText.minimumWidth = 500;
	 *
	 * final GridData gridDataButton = new GridData();
	 *
	 * this.browseSelectClassName = new Button(composite, SWT.PUSH);
	 * this.browseSelectClassName.setText("Browse");
	 * this.browseSelectClassName.setLayoutData(gridDataButton);
	 * this.browseSelectClassName.addSelectionListener(new SelectionListener() {
	 *
	 * public void widgetSelected(final SelectionEvent event) { final
	 * SelectionDialog selectionDialog; try { selectionDialog =
	 * JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() :
	 * parent.getShell(), null, SearchEngine.createWorkspaceScope(),
	 * IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, "");
	 * selectionDialog.setTitle("Select Class");
	 * selectionDialog.setMessage("Select the class");
	 *
	 * if (selectionDialog.open() == CANCEL) { return; }
	 * CreateQueryDialog.this.iSelectClassType = (IType)
	 * selectionDialog.getResult()[0];
	 * CreateQueryDialog.this.selectClassNameText.setText(((IType)
	 * selectionDialog.getResult()[0]).getFullyQualifiedName());
	 *
	 * } catch (final Exception ex) { ex.printStackTrace(); } }
	 *
	 * public void widgetDefaultSelected(final SelectionEvent arg0) { // TODO
	 * Auto-generated method stub
	 *
	 * } });
	 *
	 * return text; }
	 */

	/**
	 * @param parent
	 */
	protected void existingNamedQueryCollection(final Composite parent) {
		final Composite composit = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 6;
		composit.setLayout(layout);
		final Label label = new Label(composit, SWT.NONE);
		label.setText("Existing Queries in the file:");

		this.existingNamedQueriesCombo = new Combo(composit, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		final GridData grid5 = new GridData();
		grid5.horizontalSpan = 1;
		grid5.horizontalAlignment = SWT.FILL;
		grid5.grabExcessHorizontalSpace = false;
		grid5.verticalAlignment = SWT.FILL;
		grid5.grabExcessVerticalSpace = true;
		this.existingNamedQueriesCombo.setLayoutData(grid5);

		if (getFileFromPreferenceStore() != null) {
			this.namedQueries = getNamedQueries();
			if (this.namedQueries != null) {
				for (final String namedQuery : getEmptyArrayForNull(this.namedQueries)) {
					this.existingNamedQueriesCombo.add(namedQuery);
				}
			}
		}

		this.existingNamedQueriesCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				CreateQueryDialog.this.newNamedQueryText.setText(CreateQueryDialog.this.existingNamedQueriesCombo
						.getItem(CreateQueryDialog.this.existingNamedQueriesCombo.getSelectionIndex()));
				if (CreateQueryDialog.this.newNamedQueryButton.getSelection()) {
					for (final String namedQuery : getEmptyArrayForNull(CreateQueryDialog.this.namedQueries)) {
						if (CreateQueryDialog.this.newNamedQueryText.getText().equals(namedQuery)) {
							CreateQueryDialog.this.setErrorMessage("Please enter a new Query Name.");
							CreateQueryDialog.this.newNamedQueryText.setFocus();
							return;

						} else {
							CreateQueryDialog.this.setErrorMessage(CreateQueryDialog.this.defaultMessage);
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
		});
		this.existingNamedQueriesCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent event) {
				if (CreateQueryDialog.this.newNamedQueryText.getText() != null) {
					for (final String namedQuery : getEmptyArrayForNull(CreateQueryDialog.this.namedQueries)) {
						if (CreateQueryDialog.this.newNamedQueryText.getText().equals(namedQuery)) {
							CreateQueryDialog.this.setErrorMessage("Please enter a new Query Name.");
							CreateQueryDialog.this.newNamedQueryText.setFocus();
							return;

						} else {
							CreateQueryDialog.this.setErrorMessage(CreateQueryDialog.this.defaultMessage);
						}
					}
				}
			}
		});
		/*this.existingNamedQueriesCombo.addFocusListener(new FocusListener() {

			public void focusLost(final FocusEvent event) {
				if (CreateQueryDialog.this.newNamedQueryText.getText() != null) {
					for (final String namedQuery : CreateQueryDialog.this.namedQueries) {
						if (CreateQueryDialog.this.newNamedQueryText.getText().equals(namedQuery)) {
							CreateQueryDialog.this.setErrorMessage("Please enter a new Query Name.");
							CreateQueryDialog.this.newNamedQueryText.setFocus();
							return;

						} else {
							CreateQueryDialog.this.setErrorMessage(CreateQueryDialog.this.defaultMessage);
						}
					}
				}
			}

			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});*/
	}

	/**
	 * @return
	 */
	protected String[] getNamedQueries() {
		try {
			if (this.namedQueryFile != null) {
				final GlobalSettings globalSettings = GlobalSettings.getInstance();
				String namedQueryNodeName = null;

				if (this.createQueryData.getTemplatePrefix().equals(TEMPLATE)) {
					namedQueryNodeName = globalSettings.getPropertyValue("named.query.node", NAMED_QUERY).trim();
				} else if (this.createQueryData.getTemplatePrefix().equals(P_DATABASE_TEMPLATE_PREFIX)) {
					namedQueryNodeName = globalSettings.getPropertyValue("named.query.node", SQL_QUERY).trim();
				}
				final String namedQueryFileContents = getFileContents(this.namedQueryFile);
				this.namedQueryFileContents = namedQueryFileContents;
				this.namedQueries = StringUtil.findAttributes(namedQueryNodeName, "name", namedQueryFileContents);

			}

		} catch (final Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return this.namedQueries;
	}

	/**
	 * @return
	 */
	protected IFile getFileFromPreferenceStore() {
		if (this.preferenceStore.contains(P_HQL_NAMED_QUERY_FILE_LOCATION)) {
			this.namedQueryFile = findFileFromPath(this.preferenceStore.getString(P_HQL_NAMED_QUERY_FILE_LOCATION));
			if (this.namedQueryFile != null) {
				return this.namedQueryFile;
			}
		}

		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String namedQueryFileLocation = globalSettings.getPropertyValue("named.query.file.location", EMPTY_STR).trim();
		if (!isEmpty(namedQueryFileLocation)) {
			this.namedQueryFile = findFileFromPath(namedQueryFileLocation);
			if (this.namedQueryFile != null) {
				this.preferenceStore.setValue(P_HQL_NAMED_QUERY_FILE_LOCATION, namedQueryFileLocation);
				return this.namedQueryFile;
			}
		}
		return null;
	}

	/*
	 * private Text createPojoClassText(final Composite parent, final String
	 * labelText, final int style) { final Composite composite = new
	 * Composite(parent, parent.getStyle()); final GridLayout layout = new
	 * GridLayout(); layout.numColumns = 3; composite.setLayout(layout);
	 *
	 * final GridData gridDataLabel = new GridData(); final Label label = new
	 * Label(composite, SWT.NONE); label.setText(labelText);
	 * label.setLayoutData(gridDataLabel);
	 *
	 * final GridData gridDataText = new GridData();
	 * gridDataText.grabExcessHorizontalSpace = true;
	 *
	 * final Text text = new Text(composite, SWT.BORDER | SWT.MULTI);
	 * text.setSize(200, 20); text.setLayoutData(gridDataText);
	 * gridDataText.minimumWidth = 500;
	 *
	 * final GridData gridDataButton = new GridData();
	 *
	 * this.pojoClassBrowseButton = new Button(composite, SWT.PUSH);
	 * this.pojoClassBrowseButton.setText("Browse");
	 * this.pojoClassBrowseButton.setLayoutData(gridDataButton);
	 * this.pojoClassBrowseButton.addSelectionListener(new SelectionListener() {
	 *
	 * public void widgetSelected(final SelectionEvent event) { final
	 * SelectionDialog selectionDialog; try { selectionDialog =
	 * JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() :
	 * parent.getShell(), null, SearchEngine.createWorkspaceScope(),
	 * IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, "");
	 * selectionDialog.setTitle("Select Pojo Class");
	 * selectionDialog.setMessage("Select the Pojo class");
	 *
	 * if (selectionDialog.open() == CANCEL) { return; }
	 * //CreateQueryDialog.this.iSelectPojoClassType = (IType)
	 * selectionDialog.getResult()[0];
	 * CreateQueryDialog.this.createQueryData.setiSelectPojoClassType((IType)
	 * selectionDialog.getResult()[0]);
	 * CreateQueryDialog.this.selectPojoClassText.setText(((IType)
	 * selectionDialog.getResult()[0]).getFullyQualifiedName());
	 *
	 * if (CreateQueryDialog.this.createQueryData.getiSelectPojoClassType() !=
	 * null) {
	 * CreateQueryDialog.this.setErrorMessage(CreateQueryDialog.this.defaultMessage
	 * ); } else if (CreateQueryDialog.this.selectPojoClassText == null &&
	 * CreateQueryDialog
	 * .this.createQueryData.getTemplateType().endsWith(NAMED_QUERY_ANNOTATION_STR
	 * )) {
	 * CreateQueryDialog.this.setErrorMessage("Please select a POJO class"); }
	 *
	 * } catch (final Exception ex) { ex.printStackTrace(); } }
	 *
	 * public void widgetDefaultSelected(final SelectionEvent arg0) { // TODO
	 * Auto-generated method stub
	 *
	 * } }); return text; }
	 */
	/**
	 * @param parent
	 */
	private void createPojoClassSelectionPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Select Pojo Class:               ");
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.pojoClassCombo = new Combo(composite, SWT.NONE);
		this.pojoClassCombo.setSize(200, 20);
		this.pojoClassCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 500;

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (!fastCodeCache.getTypeSet().isEmpty()) {
			for (final IType type : fastCodeCache.getTypeSet()) {
				this.pojoClassCombo.add(type.getFullyQualifiedName());
			}
		}
		this.pojoClassCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String selectedPojoClassName = CreateQueryDialog.this.pojoClassCombo.getText();
				try {
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.getFullyQualifiedName().equals(selectedPojoClassName)) {
								CreateQueryDialog.this.createQueryData.setiSelectPojoClassType(type);
							}
						}
					}
					if (CreateQueryDialog.this.createQueryData.getiSelectPojoClassType() == null) {
						if (CreateQueryDialog.this.browsedClassMap.containsKey(selectedPojoClassName)) {
							CreateQueryDialog.this.createQueryData.setiSelectPojoClassType((IType) CreateQueryDialog.this.browsedClassMap
									.get(selectedPojoClassName));
						}
					}
					if (CreateQueryDialog.this.createQueryData.getiSelectPojoClassType() == null
							&& CreateQueryDialog.this.createQueryData.getTemplateType().endsWith(NAMED_QUERY_ANNOTATION_STR)) {
						setErrorMessage("Please select pojo class");
					} else {
						setErrorMessage(CreateQueryDialog.this.defaultMessage);
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
				final String inputPojoClassName = CreateQueryDialog.this.pojoClassCombo.getText();
				if (!isEmpty(inputPojoClassName)) {
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.getFullyQualifiedName().equals(inputPojoClassName)) {
								return;
							}
						}
					}
					if (CreateQueryDialog.this.browsedClassMap.containsKey(inputPojoClassName)) {
						return;
					}

					try {
						final IType inputClassType = getTypeFromWorkspace(inputPojoClassName);
						if (inputClassType != null) {
							CreateQueryDialog.this.createQueryData.setiSelectPojoClassType(inputClassType);
							if (!CreateQueryDialog.this.browsedClassMap.containsKey(inputClassType.getFullyQualifiedName())) {
								CreateQueryDialog.this.browsedClassMap.put(inputClassType.getFullyQualifiedName(), inputClassType);
							}
							/*if (!fastCodeCache.getTypeSet().contains(inputClassType)) {
								fastCodeCache.getTypeSet().add(inputClassType);
							}*/
							setErrorMessage(CreateQueryDialog.this.defaultMessage);
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

				if (isEmpty(CreateQueryDialog.this.pojoClassCombo.getText())) {
					setErrorMessage("Please choose a Class");

				} else {
					setErrorMessage(CreateQueryDialog.this.defaultMessage);
				}
			}

		});

		final GridData gridDataButton = new GridData();

		this.pojoClassBrowseButton = new Button(composite, SWT.PUSH);
		this.pojoClassBrowseButton.setText("Browse");
		this.pojoClassBrowseButton.setLayoutData(gridDataButton);

		this.pojoClassBrowseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final SelectionDialog selectionDialog;
				try {
					selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
							SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, EMPTY_STR);
					selectionDialog.setTitle("Select Pojo Class");
					selectionDialog.setMessage("Select the Pojo class");

					if (selectionDialog.open() == CANCEL) {
						return;
					}
					final IType browsedPojoClassType = (IType) selectionDialog.getResult()[0];
					CreateQueryDialog.this.createQueryData.setiSelectPojoClassType(browsedPojoClassType);

					boolean addItem = true;
					if (CreateQueryDialog.this.pojoClassCombo.getItems() != null) {
						for (final String existingPojoClass : CreateQueryDialog.this.pojoClassCombo.getItems()) {
							if (existingPojoClass.equals(browsedPojoClassType.getFullyQualifiedName())) {
								if (!CreateQueryDialog.this.pojoClassCombo.getText().equals(existingPojoClass)) {
									CreateQueryDialog.this.pojoClassCombo.select(CreateQueryDialog.this.pojoClassCombo
											.indexOf(existingPojoClass));
								}
								addItem = false;
								break;
							}
						}
					}
					if (addItem) {
						CreateQueryDialog.this.pojoClassCombo.add(browsedPojoClassType.getFullyQualifiedName());
						CreateQueryDialog.this.pojoClassCombo.select(CreateQueryDialog.this.pojoClassCombo.getItemCount() - 1);
					}
					//CreateQueryDialog.this.pojoClassCombo.setText(((IType) selectionDialog.getResult()[0]).getFullyQualifiedName());

					if (CreateQueryDialog.this.browsedClassMap.isEmpty()) {
						CreateQueryDialog.this.browsedClassMap.put(browsedPojoClassType.getFullyQualifiedName(), browsedPojoClassType);
					} else {
						if (!CreateQueryDialog.this.browsedClassMap.containsKey(browsedPojoClassType.getFullyQualifiedName())) {
							CreateQueryDialog.this.browsedClassMap.put(browsedPojoClassType.getFullyQualifiedName(), browsedPojoClassType);
						}
					}
					/*if (!fastCodeCache.getTypeSet().contains(selectionDialog.getResult()[0])) {
						fastCodeCache.getTypeSet().add((IType) selectionDialog.getResult()[0]);
					}*/

					if (CreateQueryDialog.this.createQueryData.getiSelectPojoClassType() == null
							&& CreateQueryDialog.this.createQueryData.getTemplateType().endsWith(NAMED_QUERY_ANNOTATION_STR)) {
						setErrorMessage("Please select pojo class");
					} else {
						setErrorMessage(CreateQueryDialog.this.defaultMessage);
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

	public CreateQueryData getCreateQueryData() {
		return this.createQueryData;
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
		schemaLabel.setText("Schema:                              ");
		schemaLabel.setLayoutData(new GridData());

		this.schemaCombo = new Combo(cmposite, SWT.DROP_DOWN);
		this.schemaCombo.setLayoutData(new GridData(150, 100));
		/*int schemaIndex = 0;
		int k = 0;
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		String defaultSchema = EMPTY_STR;
		if (databaseConnectionSettings.getTypesofDabases().equalsIgnoreCase(ORACLE)) {
			defaultSchema = databaseConnectionSettings.getUserName();
		} else {
			defaultSchema = databaseConnectionSettings.getNameofDabase();
		}
		for (final String schemaName : getEmptyArrayForNull(this.createQueryData.getSchemasInDB().toArray(new String[0]))) {
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
			public void focusLost(final FocusEvent arg0) {
				poulateTableCombo(connectToDatabase);
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
			connection = ConnectToDatabase.getCon() == null ? connectToDatabase.getNewConnection(this.databaseNameCombo.getText())
					: ConnectToDatabase.getCon();
			final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
			final String databaseType = databaseConnectionSettings.getConnMap().get(this.databaseNameCombo.getText()).getDatabaseType();
			getTableFromDb(connection, CreateQueryDialog.this.schemaCombo.getText(), databaseType);
			CreateQueryDialog.this.tableCombo.removeAll();
			final DatabaseCache databaseCache = DatabaseCache.getInstance();
			for (final String tableName : getEmptyArrayForNull(databaseCache.getDbTableListMap()
					.get(CreateQueryDialog.this.schemaCombo.getText()).toArray(new String[0]))) {
				CreateQueryDialog.this.tableCombo.add(tableName);
			}
			CreateQueryDialog.this.createQueryData.setSchemaSelected(CreateQueryDialog.this.schemaCombo.getText());
			final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(
					CreateQueryDialog.this.tableCombo.getItems());
			final ComboContentAdapter comboAdapter = new ComboContentAdapter();
			final ContentProposalAdapter adapter = new ContentProposalAdapter(CreateQueryDialog.this.tableCombo, comboAdapter, provider,
					null, null);
			adapter.setPropagateKeys(true);
			adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
			CreateQueryDialog.this.createQueryData.setTablesInDB(databaseCache.getDbTableListMap().get(
					CreateQueryDialog.this.schemaCombo.getText()));
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Create Query");

		shell.setFullScreen(true);
		final CreateQueryDialog createQueryDialog = new CreateQueryDialog(shell);

		createQueryDialog.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}

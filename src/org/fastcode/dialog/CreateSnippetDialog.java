package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_CLASS_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_FILE_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_FOLDER_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_PACKAGE_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.FC_PLUGIN;
import static org.fastcode.common.FastCodeConstants.HYPHEN;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.ORACLE;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_ADD_FIELDS_TO_POJO_CLASS;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_ADD_FIELDS_TO_POJO_CLASS_WITHOUT_ANNOTATION;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_INSERT_SIMPLE;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_INSERT_WITH_NAMED_PARAMETER;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_POJO_CLASS;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_POJO_CLASS_WITHOUT_ANNOTATION;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_SIMPLE;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_SQL_QUERY;
import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SIMPLE_SNIPPET;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.P_DB_PACKAGE_FOR_POJO_CLASS;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATES_TO_ENABLE_POJO;
import static org.fastcode.preferences.PreferenceConstants.P_WORKING_JAVA_PROJECT;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_COPY_CLASSES;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_CREATE_IMPL;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_INSTANCE_OF_CLASSES;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_INSTANCE_OF_GENERIC_DAO;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_SPRING_BEAN_FILE;
import static org.fastcode.util.DatabaseUtil.getSchemaFromDb;
import static org.fastcode.util.DatabaseUtil.getTableFromDb;
import static org.fastcode.util.FastCodeUtil.getEmptyListForNull;
import static org.fastcode.util.SourceUtil.getAlteredPackageName;
import static org.fastcode.util.SourceUtil.getDefaultPathFromProject;
import static org.fastcode.util.SourceUtil.getJavaProject;
import static org.fastcode.util.SourceUtil.getPackageFromUser;
import static org.fastcode.util.SourceUtil.getPackagesInProject;
import static org.fastcode.util.SourceUtil.getTypeFromWorkspace;
import static org.fastcode.util.SourceUtil.getWorkingJavaProjectFromUser;
import static org.fastcode.util.SourceUtil.isFullNameOfFile;
import static org.fastcode.util.StringUtil.createDefaultInstance;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.isValidVariableName;
import static org.fastcode.util.StringUtil.makeWord;
import static org.fastcode.util.VersionControlUtil.isPrjConfigured;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.internal.ide.dialogs.OpenResourceDialog;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.CreateSnippetData;
import org.fastcode.common.DatabaseDetails;
import org.fastcode.common.FastCodeColor;
import org.fastcode.common.FastCodeConstants.FIELDS;
import org.fastcode.common.FastCodeConstants.FIRST_TEMPLATE;
import org.fastcode.common.FastCodeConstants.SECOND_TEMPLATE;
import org.fastcode.common.FastCodeConstants.WHERE_CLAUSE_SEPARATOR;
import org.fastcode.common.FastCodeFile;
import org.fastcode.common.FastCodeProject;
import org.fastcode.common.FastCodeReturn;
import org.fastcode.common.PackageSelectionDialog;
import org.fastcode.exception.FastCodeRepositoryException;
import org.fastcode.popup.actions.snippet.FastCodeCache;
import org.fastcode.preferences.DatabaseConnectionSettings;
import org.fastcode.preferences.VersionControlPreferences;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.setting.TemplateSettings;
import org.fastcode.templates.viewer.TemplateFieldEditor;
import org.fastcode.util.ConnectToDatabase;
import org.fastcode.util.DatabaseCache;
import org.fastcode.util.FastCodeContentProposalProvider;

/**
 * @author Gautam
 *
 */
public class CreateSnippetDialog extends TrayDialog {

	private CreateSnippetData	createSnippetData;
	private Text				errorMessageText;
	private final String		defaultMessage			= NEWLINE;
	Shell						shell;
	private String				errorMessage;
	private Combo				snippetCombo;
	private Combo				templateVariationCombo;
	private List				templateVariationList;
	// private Button browse;
	private Combo				tableCombo;
	/*private Combo				fileNameCombo;
	private Button				browseFile;*/
	private TemplateSettings	templateSettings;
	/*private Combo				fromClassNameCombo;
	private Button				browseFromClass;*/
	private Combo				toClassNameCombo;
	private Button				browseToClass;
	private Button				orButton;
	private Button				andButton;
	private Button				groupByButton;
	private Button				orderByButton;
	String						templateSettigsNull		= "Either there is no file open in the editor or template was not found.\n";
	// private Label fromClassLabel;
	private Combo				fromClassInstCombo;
	private Combo				toClassInstCombo;
	private Label				fromClassInstLabel;
	private Text				snippetDesc;
	private TemplateFieldEditor	templateBodyMultiText;
	private String				modifiedTemplateBody;
	private Combo				pojoClassCombo;
	private Button				pojoClassBrowseButton;
	IPreferenceStore			preferenceStore			= new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
	/*private Combo				packageCombo;
	private Button				packageBrowseButton;
	private Combo				folderCombo;
	private Button				folderBrowseButton;*/
	private Button				showLocalVariable;
	private Combo				selectTypeCombo;
	private Button				browseButton;
	private Label				selectTypeLabel;
	private Label				toClassLabel;
	private Label				toClassInstLabel;
	private Composite			multitextparent;
	private Combo				schemaCombo;
	private Button				useAliasNameButton;
	private String				currentType;
	private Label				projectLabel;
	private Combo				projectCombo;
	Map<String, IProject>		prjMap					= new HashMap<String, IProject>();
	Map<String, Object>			browsedFirstTemplateMap	= new HashMap<String, Object>();
	private Label				templateVariationLabel;
	private Combo				databaseNameCombo;
	private String				selectedDatabaseName;
	private Button				autoCheckin;

	/**
	 * @param shell
	 */
	public CreateSnippetDialog(final Shell shell) {
		super(shell);
		this.shell = shell;

	}

	/**
	 * @param shell
	 * @param createSnippetData
	 */
	public CreateSnippetDialog(final Shell shell, final CreateSnippetData createSnippetData) {
		super(shell);
		this.shell = shell;
		this.createSnippetData = createSnippetData;

	}

	@Override
	public void create() {
		super.create();

	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Create Snippet");
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

		createErrorMessageText(parent);

		createSnippetCombo(parent);
		createTemplateVariationList(parent);
		if (this.preferenceStore.getBoolean(P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES)) {
			createTemplateBodyMultiText(parent);
		}

		final String templatePrefix = this.createSnippetData.getTemplatePrefix();
		if (templatePrefix.equals(TEMPLATE)) {
			createProjectSelectionPane(parent);
			createSelectionPane(parent);
			createFromClassInstacnePane(parent);
			createToClassSelectionPane(parent);
			createToClassInstancePane(parent);
			if (this.createSnippetData.getLocalVariables() != null && this.createSnippetData.getLocalVariables().size() > 0) {
				createShowLocaVarButton(parent);
			}
			final String projectName = CreateSnippetDialog.this.projectCombo.getText();
			if (!isEmpty(projectName)) {
				isPrjInSync(CreateSnippetDialog.this.prjMap.get(projectName));
			}
		} else if (templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX)) {
			createDatabaseNameSelectionPane(parent);
			createSchemaSelectionPane(parent);
			createTableSelectionPane(parent);
			createPojoClassSelectionPane(parent);
			// createOrderByGroupByButtons(parent);
			createWhereClauseSeparatorButtons(parent);
			createAliasnameButton(parent);

		}

		createAutoCheckinPane(parent);
		if (this.createSnippetData.getSnippetTypes().length == 1) {
			this.snippetCombo.select(0);
			this.snippetCombo.setEnabled(false);
			processTemplateType(this.snippetCombo.getItem(this.snippetCombo.getSelectionIndex()));
		}

		return parent;
	}

	private void createAutoCheckinPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);
		this.autoCheckin = new Button(composite, SWT.CHECK);
		this.autoCheckin.setText("Auto Check in Changes?");
		final VersionControlPreferences versionControlPreferences = VersionControlPreferences.getInstance();
		if (versionControlPreferences.isEnable() && this.createSnippetData.getJavaProject() != null) {
			boolean prjShared = false;
			boolean prjConfigured = false;
			try {
				prjShared = !isEmpty(this.createSnippetData.getJavaProject().getProject().getPersistentProperties());
				prjConfigured = !isEmpty(isPrjConfigured(this.createSnippetData.getJavaProject().getProject().getName()));
			} catch (final CoreException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			} catch (final FastCodeRepositoryException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			if (prjShared && prjConfigured) {
				this.autoCheckin.setEnabled(true);
			} else {
				this.autoCheckin.setEnabled(false);
			}
		} else {
			this.autoCheckin.setEnabled(false);
		}
		this.autoCheckin.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				CreateSnippetDialog.this.createSnippetData.setDoAutoCheckin(CreateSnippetDialog.this.autoCheckin.getSelection());
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
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
		schemaLabel.setText("Database Names:        ");
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
		this.createSnippetData.setSelectedDatabaseName(this.databaseNameCombo.getText());
		final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(this.databaseNameCombo.getItems());
		final ComboContentAdapter comboAdapter = new ComboContentAdapter();
		final ContentProposalAdapter adapter = new ContentProposalAdapter(this.databaseNameCombo, comboAdapter, provider, null, null);
		adapter.setPropagateKeys(true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		this.databaseNameCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent event) {
				final String selectedDbName = CreateSnippetDialog.this.databaseNameCombo.getText();
				if (databaseConnectionSettings.getConnMap().keySet().contains(selectedDbName)) {
					if (!selectedDbName.equalsIgnoreCase(DatabaseConnectionSettings.getInstance().getNameofDabase())
							|| !CreateSnippetDialog.this.createSnippetData.getSelectedDatabaseName().equals(selectedDbName)) {
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
							CreateSnippetDialog.this.createSnippetData.setSchemasInDB(databaseCache.getDbSchemaListMap().get(
									databaseConnection.getDatabaseType()));
							populateSchemaCombo();
							poulateTableCombo(connectToDatabase);
							CreateSnippetDialog.this.createSnippetData.setSelectedDatabaseName(selectedDbName);

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
						|| !createSnippetData.getSelectedDatabaseName().equals(selectedDbName)) {
					//updatePreferenceStore(selectedDbName);
					//DatabaseConnectionSettings.setReload(true);
					ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();
					connectToDatabase.closeConnection(ConnectToDatabase.getCon());
					DatabaseCache databaseCache = DatabaseCache.getInstance();
					DatabaseDetails databaseConnection = databaseConnectionSettings.getConnMap().get(selectedDbName);
					Connection connection = null;
					try {
						connection = connectToDatabase.getConnection(selectedDbName);
						getSchemaFromDb(connection, databaseConnection.getDatabaseType());
						createSnippetData.setSchemasInDB(databaseCache.getDbSchemaListMap().get(databaseConnection.getDatabaseType()));
						populateSchemaCombo();
						poulateTableCombo(connectToDatabase);
						createSnippetData.setSelectedDatabaseName(selectedDbName);
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
		for (final String schemaName : this.createSnippetData.getSchemasInDB().toArray(new String[0])) {
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
	private void createProjectSelectionPane(final Composite parent) {

		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		this.projectLabel = new Label(composite, SWT.NONE);
		this.projectLabel.setText("Select Project:              ");
		this.projectLabel.setLayoutData(gridDataLabel);
		//this.projectLabel.setVisible(false);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.projectCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		this.projectCombo.setSize(200, 20);
		this.projectCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 500;
		//this.projectCombo.setEnabled(false);
		//this.projectCombo.setVisible(false);

		final IProject projects[] = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (final IProject prj : projects) {
			if (prj == null || !prj.exists() || !prj.isOpen()) {
				continue;
			}
			if (prj.getName().equals(FC_PLUGIN)) {
				continue;
			}
			this.projectCombo.add(prj.getName());
			this.prjMap.put(prj.getName(), prj);
		}
		if (this.createSnippetData.getJavaProject() != null) {
			this.projectCombo.select(this.projectCombo.indexOf(this.createSnippetData.getJavaProject().getElementName()));
		}

		this.projectCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String projectName = CreateSnippetDialog.this.projectCombo.getText();
				if (!isEmpty(projectName)) {
					CreateSnippetDialog.this.createSnippetData.setSelectedProject(new FastCodeProject(CreateSnippetDialog.this.prjMap
							.get(projectName)));
					setErrorMessage(CreateSnippetDialog.this.defaultMessage);
					isPrjInSync(CreateSnippetDialog.this.prjMap.get(projectName));
				} else {
					setErrorMessage("Please select a project");
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

		/*this.projectCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent arg0) {
				final String projectName = CreateSnippetDialog.this.projectCombo.getText();
				if (!isEmpty(projectName)) {
					isPrjInSync(CreateSnippetDialog.this.prjMap.get(projectName));
				}

			}
		});*/
	}

	/**
	 * @param project
	 *
	 */
	private boolean isPrjInSync(final IProject project) {
		if (project != null && !project.isSynchronized(IResource.DEPTH_INFINITE)) {
			this.snippetCombo.setEnabled(false);
			setErrorMessage("Project " + project.getName() + " is not synchronised. Please synchronise and try again.");
			return false;
		} else {
			this.snippetCombo.setEnabled(true);
			setErrorMessage(this.defaultMessage);
			return true;
		}
	}

	/**
	 * @param parent
	 */
	private void createSelectionPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		this.selectTypeLabel = new Label(composite, SWT.NONE);

		this.selectTypeLabel.setText("Select Type:                 ");
		this.selectTypeLabel.setLayoutData(gridDataLabel);
		this.selectTypeLabel.setVisible(false);
		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.selectTypeCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		this.selectTypeCombo.setSize(200, 20);
		this.selectTypeCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 500;
		this.selectTypeCombo.setEnabled(false);
		this.selectTypeCombo.setVisible(false);
		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();

		this.selectTypeCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

				switch (CreateSnippetDialog.this.templateSettings.getFirstTemplateItem()) {
				case Class:
					String selectedFromClassName = CreateSnippetDialog.this.selectTypeCombo.getText();
					if (selectedFromClassName.contains(ENCLOSING_CLASS_STR)) {
						selectedFromClassName = CreateSnippetDialog.this.currentType;
					}
					try {
						if (!fastCodeCache.getTypeSet().isEmpty()) {
							for (final IType type : fastCodeCache.getTypeSet()) {
								if (type.getFullyQualifiedName().equals(selectedFromClassName)) {
									CreateSnippetDialog.this.createSnippetData.setFromClass(type);
								}
							}
						}
						if (CreateSnippetDialog.this.createSnippetData.getFromClass() == null) {
							if (CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(selectedFromClassName)) {
								CreateSnippetDialog.this.createSnippetData
										.setFromClass((IType) CreateSnippetDialog.this.browsedFirstTemplateMap.get(selectedFromClassName));
							}
						}
					} catch (final Exception ex) {
						ex.printStackTrace();
					}

					setInstanceNameCombo(selectedFromClassName);
					/*if (CreateSnippetDialog.this.selectTypeCombo.getText().equals(CURRENT_CLASS)) {
						CreateSnippetDialog.this.fromClassInstCombo.setText(CURRENT_CLASS + " Instance");
					}*/
					break;
				case File:
					String selectedFileName = CreateSnippetDialog.this.selectTypeCombo.getText();
					if (selectedFileName.contains(ENCLOSING_FILE_STR)) {
						selectedFileName = CreateSnippetDialog.this.currentType;
					}
					try {
						if (!fastCodeCache.getFileSet().isEmpty()) {
							for (final IFile file : fastCodeCache.getFileSet()) {
								if (file.getFullPath().toString().equals(selectedFileName)) {//if (file.getName().equals(selectedFileName.substring(selectedFileName.lastIndexOf(FILE_SEPARATOR) + 1))) {
									if (!CreateSnippetDialog.this.createSnippetData.getFastCodeFiles().contains(new FastCodeFile(file))) {
										CreateSnippetDialog.this.createSnippetData.getFastCodeFiles().add(new FastCodeFile(file));//new FastCodeFile(file.getName(), file.getProjectRelativePath().toString()));
									}
									CreateSnippetDialog.this.createSnippetData.setResourceFile(file);
									CreateSnippetDialog.this.setErrorMessage(CreateSnippetDialog.this.defaultMessage);
								}
							}
						}
						if (CreateSnippetDialog.this.createSnippetData.getResourceFile() == null) {
							if (CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(selectedFileName)) {
								if (!CreateSnippetDialog.this.createSnippetData.getFastCodeFiles().contains(
										new FastCodeFile((IFile) CreateSnippetDialog.this.browsedFirstTemplateMap.get(selectedFileName)))) {
									CreateSnippetDialog.this.createSnippetData.getFastCodeFiles()
											.add(new FastCodeFile((IFile) CreateSnippetDialog.this.browsedFirstTemplateMap
													.get(selectedFileName)));

								}
								CreateSnippetDialog.this.createSnippetData
										.setResourceFile((IFile) CreateSnippetDialog.this.browsedFirstTemplateMap.get(selectedFileName));
								CreateSnippetDialog.this.setErrorMessage(CreateSnippetDialog.this.defaultMessage);
							}
						} else {
							if (!CreateSnippetDialog.this.createSnippetData.getFastCodeFiles().contains(
									new FastCodeFile(CreateSnippetDialog.this.createSnippetData.getResourceFile()))) {
								CreateSnippetDialog.this.createSnippetData.getFastCodeFiles().add(
										new FastCodeFile(CreateSnippetDialog.this.createSnippetData.getResourceFile()));
							}
						}
					} catch (final Exception ex) {
						ex.printStackTrace();
					}

					break;
				case Folder:
					String selectedFolderPath = CreateSnippetDialog.this.selectTypeCombo.getText();
					if (selectedFolderPath.contains(ENCLOSING_FOLDER_STR)) {
						selectedFolderPath = CreateSnippetDialog.this.currentType;
					}
					try {
						if (!fastCodeCache.getFolderSet().isEmpty()) {
							for (final IFolder folder : fastCodeCache.getFolderSet()) {
								if (folder.getFullPath().toString().equals(selectedFolderPath)) {
									CreateSnippetDialog.this.createSnippetData.setFolder(folder);
									CreateSnippetDialog.this.setErrorMessage(CreateSnippetDialog.this.defaultMessage);
								}
							}
						}
						if (CreateSnippetDialog.this.createSnippetData.getFolder() == null) {
							if (CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(selectedFolderPath)) {
								CreateSnippetDialog.this.createSnippetData
										.setFolder((IFolder) CreateSnippetDialog.this.browsedFirstTemplateMap.get(selectedFolderPath));
								CreateSnippetDialog.this.setErrorMessage(CreateSnippetDialog.this.defaultMessage);
							}
						}
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
					break;
				case Package:
					String selectedPkgName = CreateSnippetDialog.this.selectTypeCombo.getText();
					if (selectedPkgName.contains(ENCLOSING_PACKAGE_STR)) {
						selectedPkgName = CreateSnippetDialog.this.currentType;
					}
					try {
						if (!fastCodeCache.getPackageSet().isEmpty()) {
							for (final IPackageFragment pkg : fastCodeCache.getPackageSet()) {
								if (getAlteredPackageName(pkg).equals(selectedPkgName)) {
									CreateSnippetDialog.this.createSnippetData.setPackageFragment(pkg);
									CreateSnippetDialog.this.setErrorMessage(CreateSnippetDialog.this.defaultMessage);
								}
							}
						}
						if (CreateSnippetDialog.this.createSnippetData.getPackageFragment() == null) {
							if (CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(selectedPkgName)) {
								CreateSnippetDialog.this.createSnippetData
										.setPackageFragment((IPackageFragment) CreateSnippetDialog.this.browsedFirstTemplateMap
												.get(selectedPkgName));
								CreateSnippetDialog.this.setErrorMessage(CreateSnippetDialog.this.defaultMessage);
							}
						}
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
					break;
				case Enumeration:
					final String selectedEnumName = CreateSnippetDialog.this.selectTypeCombo.getText();
					try {
						if (!fastCodeCache.getTypeSet().isEmpty()) {
							for (final IType type : fastCodeCache.getTypeSet()) {
								if (type.getFullyQualifiedName().equals(selectedEnumName)) {
									CreateSnippetDialog.this.createSnippetData.setEnumType(type);
									CreateSnippetDialog.this.setErrorMessage(CreateSnippetDialog.this.defaultMessage);
								}
							}
						}

						if (CreateSnippetDialog.this.createSnippetData.getEnumType() == null) {
							if (CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(selectedEnumName)) {
								CreateSnippetDialog.this.createSnippetData
										.setEnumType((IType) CreateSnippetDialog.this.browsedFirstTemplateMap.get(selectedEnumName));
							}
						}
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
					setInstanceNameCombo(selectedEnumName);
					break;

				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		/*this.selectTypeCombo.addModifyListener(new ModifyListener() {

			public void modifyText(final ModifyEvent event) {

				if (CreateSnippetDialog.this.createSnippetData.isRequiresClass()) {

					if (isEmpty(CreateSnippetDialog.this.selectTypeCombo.getText())) {
						setErrorMessage("Please choose a Class");
					} else {
						if (!isValidVariableName(CreateSnippetDialog.this.selectTypeCombo.getText())) {
							setErrorMessage("Special charecters can not be used");
						} else {
							setErrorMessage(CreateSnippetDialog.this.defaultMessage);
						}
						// setErrorMessage(CreateSnippetDialog.this.defaultMessage);
					}

				} else if (CreateSnippetDialog.this.createSnippetData.isRequiresFile()) {

					if (isEmpty(CreateSnippetDialog.this.selectTypeCombo.getText())) {
						setErrorMessage("Please choose a file");
					} else {
						/*
						 * if (!isValidVariableName(CreateSnippetDialog.this.
						 * fileNameCombo .getText())) {
						 * setErrorMessage("Special charecters can not be used"
						 * ); } else {
						 * setErrorMessage(CreateSnippetDialog.this.defaultMessage
						 * ); }

						setErrorMessage(CreateSnippetDialog.this.defaultMessage);
					}
				}
			}
		});*/
		this.selectTypeCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent event) {

				switch (CreateSnippetDialog.this.templateSettings.getFirstTemplateItem()) {
				case Class:
					String inputFromClassName = CreateSnippetDialog.this.selectTypeCombo.getText();
					if (!isEmpty(inputFromClassName)) {
						if (inputFromClassName.contains(ENCLOSING_CLASS_STR)) {
							inputFromClassName = CreateSnippetDialog.this.currentType;
						}
						if (!fastCodeCache.getTypeSet().isEmpty()) {
							for (final IType type : fastCodeCache.getTypeSet()) {
								if (type.getFullyQualifiedName().equals(inputFromClassName)) {
									return;
								}
							}
						}
						if (CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(inputFromClassName)) {
							return;
						}
						try {
							final IType inputClassType = getTypeFromWorkspace(inputFromClassName);
							if (inputClassType != null) {
								CreateSnippetDialog.this.createSnippetData.setFromClass(inputClassType);
								setInstanceNameCombo(inputFromClassName);
								if (!CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(inputClassType.getFullyQualifiedName())) {
									CreateSnippetDialog.this.browsedFirstTemplateMap.put(inputClassType.getFullyQualifiedName(),
											inputClassType);
								}
								/*if (CreateSnippetDialog.this.selectTypeCombo.getText().equals(CURRENT_CLASS)) {
									CreateSnippetDialog.this.fromClassInstCombo.setText(CURRENT_CLASS + " Instance");
								}*/
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
					break;
				case File:
					String inputFileName = CreateSnippetDialog.this.selectTypeCombo.getText();
					if (!isEmpty(inputFileName)) {
						if (inputFileName.contains(ENCLOSING_FILE_STR)) {
							inputFileName = CreateSnippetDialog.this.currentType;
						}
						if (!fastCodeCache.getFileSet().isEmpty()) {
							for (final IFile file : fastCodeCache.getFileSet()) {
								if (file.getFullPath().toString().equals(inputFileName)) {
									return;
								}
							}
						}
						if (CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(inputFileName)) {
							return;
						}
						final boolean inputResult = isFullNameOfFile(inputFileName);
						if (inputResult) {
							try {
								final Path inputPath = new Path(inputFileName);
								final IPath iInputPath = inputPath.makeAbsolute();
								final IFile inputFile = ResourcesPlugin.getWorkspace().getRoot().getFile(iInputPath);
								if (inputFile != null && inputFile.exists()) {
									final FastCodeFile inputFastCodeFile = new FastCodeFile(inputFile);//new FastCodeFile(inputFile.getName(), inputFile.getProjectRelativePath().toString());
									if (!CreateSnippetDialog.this.createSnippetData.getFastCodeFiles().contains(inputFastCodeFile)) {
										CreateSnippetDialog.this.createSnippetData.getFastCodeFiles().add(inputFastCodeFile);
									}
									CreateSnippetDialog.this.createSnippetData.setResourceFile(inputFile);

									if (!CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(inputFile.getFullPath().toString())) {
										CreateSnippetDialog.this.browsedFirstTemplateMap.put(inputFile.getFullPath().toString(), inputFile);
									}
									/*if (!fastCodeCache.getFileSet().contains(inputFile)) {
										fastCodeCache.getFileSet().add(inputFile);

									}*/
									CreateSnippetDialog.this.setErrorMessage(CreateSnippetDialog.this.defaultMessage);
								} else {
									CreateSnippetDialog.this.setErrorMessage("File does not exist,Please enter an existing file name");
								}
							} catch (final Exception ex) {
								ex.printStackTrace();
							}
							CreateSnippetDialog.this.setErrorMessage(CreateSnippetDialog.this.defaultMessage);

						} else {
							CreateSnippetDialog.this
									.setErrorMessage("Please enter file name with full path like /Project Name/....../file name ");
						}
					}
					break;
				case Folder:
					String inputFolderPath = CreateSnippetDialog.this.selectTypeCombo.getText();
					if (!isEmpty(inputFolderPath)) {
						if (inputFolderPath.contains(ENCLOSING_FOLDER_STR)) {
							inputFolderPath = CreateSnippetDialog.this.currentType;
						}
						if (!fastCodeCache.getFolderSet().isEmpty()) {
							for (final IFolder folder : fastCodeCache.getFolderSet()) {
								if (folder.getFullPath().toString().equals(inputFolderPath)) {
									return;
								}
							}
						}
						if (CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(inputFolderPath)) {
							return;
						}

					}

					break;
				case Package:
					String inputPkgName = CreateSnippetDialog.this.selectTypeCombo.getText();
					if (!isEmpty(inputPkgName)) {
						if (inputPkgName.contains(ENCLOSING_PACKAGE_STR)) {
							inputPkgName = CreateSnippetDialog.this.currentType;
						}
						if (!fastCodeCache.getPackageSet().isEmpty()) {
							for (final IPackageFragment pkg : fastCodeCache.getPackageSet()) {
								if (pkg.getElementName().equals(inputPkgName)) {
									return;
								}
							}
						}
						if (CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(inputPkgName)) {
							return;
						}
					}
					break;
				case Enumeration:
					final String inputEnumName = CreateSnippetDialog.this.selectTypeCombo.getText();
					if (!isEmpty(inputEnumName)) {
						if (!fastCodeCache.getTypeSet().isEmpty()) {
							for (final IType type : fastCodeCache.getTypeSet()) {
								if (type.getFullyQualifiedName().equals(inputEnumName)) {
									return;
								}
							}
						}
						if (CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(inputEnumName)) {
							return;
						}
						try {
							if (!isEmpty(inputEnumName)) {
								final IType inputEnumType = getTypeFromWorkspace(inputEnumName);
								if (inputEnumType != null) {
									CreateSnippetDialog.this.createSnippetData.setEnumType(inputEnumType);
									setInstanceNameCombo(inputEnumName);
									if (!CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(inputEnumType.getFullyQualifiedName())) {
										CreateSnippetDialog.this.browsedFirstTemplateMap.put(inputEnumType.getFullyQualifiedName(),
												inputEnumType);
									}
									/*if (!fastCodeCache.getTypeSet().contains(inputEnumType)) {
										fastCodeCache.getTypeSet().add(inputEnumType);
									}*/
								} else {
									setErrorMessage("Enumeration does not exist,Please enter an existing enumeration name ");
								}
							}
						} catch (final Exception ex) {
							ex.printStackTrace();
						}
					}
					break;

				}
			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		final GridData gridDataButton = new GridData();
		this.browseButton = new Button(composite, SWT.PUSH);
		this.browseButton.setText("         Browse         ");
		this.browseButton.setLayoutData(gridDataButton);
		this.browseButton.setEnabled(false);
		this.browseButton.setVisible(false);
		this.browseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

				switch (CreateSnippetDialog.this.templateSettings.getFirstTemplateItem()) {
				case Class:
					SelectionDialog selectionDialog;
					try {

						/*if (CreateSnippetDialog.this.templateSettings.getSecondTemplateItem().equals(SECOND_TEMPLATE.field)) {
							selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
									SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_CLASSES, false, EMPTY_STR);
						} else {*/
						selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
								SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, EMPTY_STR);
						//}
						selectionDialog.setTitle("Select From Class ");
						selectionDialog.setMessage("Select the From Class to get fields ");

						if (selectionDialog.open() == CANCEL) {
							return;
						}
						final IType browseFromClassType = (IType) selectionDialog.getResult()[0];
						CreateSnippetDialog.this.createSnippetData.setFromClass(browseFromClassType);
						boolean addItem = true;
						if (CreateSnippetDialog.this.selectTypeCombo.getItems() != null) {
							for (final String existingClass : CreateSnippetDialog.this.selectTypeCombo.getItems()) {
								if (existingClass.equals(browseFromClassType.getFullyQualifiedName())) {
									if (!CreateSnippetDialog.this.selectTypeCombo.getText().equals(existingClass)) {
										CreateSnippetDialog.this.selectTypeCombo.select(CreateSnippetDialog.this.selectTypeCombo
												.indexOf(existingClass));
										setInstanceNameCombo(existingClass);
									}
									addItem = false;
									break;
								}
							}
						}
						if (addItem) {
							CreateSnippetDialog.this.selectTypeCombo.add(browseFromClassType.getFullyQualifiedName());
							CreateSnippetDialog.this.selectTypeCombo.select(CreateSnippetDialog.this.selectTypeCombo.getItemCount() - 1);
							setInstanceNameCombo(browseFromClassType.getFullyQualifiedName());
						}

						if (CreateSnippetDialog.this.browsedFirstTemplateMap.isEmpty()) {
							CreateSnippetDialog.this.browsedFirstTemplateMap.put(browseFromClassType.getFullyQualifiedName(),
									browseFromClassType);
						} else {
							if (!CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(browseFromClassType.getFullyQualifiedName())) {
								CreateSnippetDialog.this.browsedFirstTemplateMap.put(browseFromClassType.getFullyQualifiedName(),
										browseFromClassType);
							}
						}
						/*if (!fastCodeCache.getTypeSet().contains(browseFromClassType)) {
							fastCodeCache.getTypeSet().add(browseFromClassType);
						}*/

					} catch (final JavaModelException ex) {
						ex.printStackTrace();
					}
					break;
				case File:
					final OpenResourceDialog resourceDialog = new OpenResourceDialog(parent.getShell(), ResourcesPlugin.getWorkspace()
							.getRoot(), IResource.FILE);
					resourceDialog.setTitle("Select File ");
					resourceDialog.setMessage("Select the File to get fields from");

					if (resourceDialog.open() == CANCEL) {
						return;
					}
					final IFile browseFile = (IFile) resourceDialog.getResult()[0];
					final FastCodeFile browseFastCodeFile = new FastCodeFile(browseFile);
					if (CreateSnippetDialog.this.createSnippetData.getFastCodeFiles().isEmpty()) {
						CreateSnippetDialog.this.createSnippetData.getFastCodeFiles().add(browseFastCodeFile);
					} else if (!CreateSnippetDialog.this.createSnippetData.getFastCodeFiles().contains(browseFastCodeFile)) {
						CreateSnippetDialog.this.createSnippetData.getFastCodeFiles().add(browseFastCodeFile);
					}
					CreateSnippetDialog.this.createSnippetData.setResourceFile(browseFile);
					boolean addItem = true;
					if (CreateSnippetDialog.this.selectTypeCombo.getItems() != null) {
						for (final String existingFile : CreateSnippetDialog.this.selectTypeCombo.getItems()) {
							if (existingFile.equals(browseFile.getFullPath().toString())) {
								if (!CreateSnippetDialog.this.selectTypeCombo.getText().equals(existingFile)) {
									CreateSnippetDialog.this.selectTypeCombo.select(CreateSnippetDialog.this.selectTypeCombo
											.indexOf(existingFile));
								}
								addItem = false;
								break;

							}
						}
					}
					if (addItem) {
						CreateSnippetDialog.this.selectTypeCombo.add(browseFile.getFullPath().toString());
						CreateSnippetDialog.this.selectTypeCombo.select(CreateSnippetDialog.this.selectTypeCombo.getItemCount() - 1);
					}

					/*if (!fastCodeCache.getFileSet().contains(browseFile)) {
						fastCodeCache.getFileSet().add(browseFile);
					}*/
					if (CreateSnippetDialog.this.browsedFirstTemplateMap.isEmpty()) {
						CreateSnippetDialog.this.browsedFirstTemplateMap.put(browseFile.getFullPath().toString(), browseFile);
					} else {
						if (!CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(browseFile.getFullPath().toString())) {
							CreateSnippetDialog.this.browsedFirstTemplateMap.put(browseFile.getFullPath().toString(), browseFile);
						}
					}
					CreateSnippetDialog.this.setErrorMessage(CreateSnippetDialog.this.defaultMessage);
					break;
				case Folder:
					try {
						IPath path = null;
						final ContainerSelectionDialog dialog = new ContainerSelectionDialog(new Shell(),
								null/*ResourcesPlugin
									.getWorkspace().getRoot().getProject("TestFC")*/, true, "Select a folder:");
						dialog.setTitle("Select a Folder");

						if (dialog.open() != CANCEL) {
							path = (IPath) dialog.getResult()[0];
							System.out.println(path.isRoot());
							final int segmentCount = path.segmentCount();
							IFolder folder;
							if (segmentCount == 1) {
								final IProject prjct = ResourcesPlugin.getWorkspace().getRoot().getProject(path.toString());
								folder = prjct.getFolder(prjct.getFullPath());
							} else {
								folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
							}

							//final IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path); //new Path(path.toString() + FORWARD_SLASH));
							boolean addItem1 = true;
							if (CreateSnippetDialog.this.selectTypeCombo.getItems() != null) {
								for (final String existingFolder : CreateSnippetDialog.this.selectTypeCombo.getItems()) {
									if (existingFolder.equals(folder.getFullPath().toString())) {
										if (!CreateSnippetDialog.this.selectTypeCombo.getText().equals(existingFolder)) {
											CreateSnippetDialog.this.selectTypeCombo.select(CreateSnippetDialog.this.selectTypeCombo
													.indexOf(existingFolder));
										}
										addItem1 = false;
										break;
									}
								}
							}
							if (addItem1) {
								CreateSnippetDialog.this.selectTypeCombo.add(folder.getFullPath().toString());
								CreateSnippetDialog.this.selectTypeCombo.select(CreateSnippetDialog.this.selectTypeCombo.getItemCount() - 1);
							}
							/*if (!fastCodeCache.getFolderSet().contains(folder)) {
								fastCodeCache.getFolderSet().add(folder);
							}*/

							if (CreateSnippetDialog.this.browsedFirstTemplateMap.isEmpty()) {
								CreateSnippetDialog.this.browsedFirstTemplateMap.put(folder.getFullPath().toString(), folder);
							} else {
								if (!CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(folder.getFullPath().toString())) {
									CreateSnippetDialog.this.browsedFirstTemplateMap.put(folder.getFullPath().toString(), folder);
								}
							}
							CreateSnippetDialog.this.createSnippetData.setFolder(folder);
							if (!isEmpty(CreateSnippetDialog.this.selectTypeCombo.getText())) {
								CreateSnippetDialog.this.setErrorMessage(CreateSnippetDialog.this.defaultMessage);
							}
						}

					} catch (final Exception ex) {
						ex.printStackTrace();
					}
					break;
				case Package:
					try {
						final IJavaProject javaProject = CreateSnippetDialog.this.createSnippetData.getSelectedProject().getJavaProject() != null ? CreateSnippetDialog.this.createSnippetData
								.getSelectedProject().getJavaProject() : CreateSnippetDialog.this.createSnippetData.getJavaProject();
						final String srcPath = getDefaultPathFromProject(javaProject, "source", EMPTY_STR);
						final IPackageFragment allPackages[] = getPackagesInProject(javaProject, srcPath, "source");
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

						PackageSelectionDialog packageSelectionDialog = null;

						if (CreateSnippetDialog.this.createSnippetData.getTemplateType().equals(TEMPLATE_CREATE_IMPL)
								|| CreateSnippetDialog.this.createSnippetData.getTemplateType().equals(TEMPLATE_SPRING_BEAN_FILE)
								|| CreateSnippetDialog.this.createSnippetData.getTemplateType().equals(TEMPLATE_INSTANCE_OF_CLASSES)
								|| CreateSnippetDialog.this.createSnippetData.getTemplateType().equals(TEMPLATE_COPY_CLASSES)
								|| CreateSnippetDialog.this.createSnippetData.getTemplateType().equals(TEMPLATE_INSTANCE_OF_GENERIC_DAO)) {
							packageSelectionDialog = new PackageSelectionDialog(new Shell(), "Package ", "Choose a package from below",
									nonEmptyPackages.toArray(new IPackageFragment[0]));
						} else {
							packageSelectionDialog = new PackageSelectionDialog(new Shell(), "Package ", "Choose a package from below",
									allPackages);
						}
						IPackageFragment packageFragment = null;
						if (packageSelectionDialog.open() != CANCEL) {
							packageFragment = (IPackageFragment) packageSelectionDialog.getFirstResult();
							// CreateSnippetDialog.this.packageCombo.setText(getAlteredPackageName(packageFragment));
							boolean addItem1 = true;
							if (CreateSnippetDialog.this.selectTypeCombo.getItems() != null) {
								for (final String existingPkg : CreateSnippetDialog.this.selectTypeCombo.getItems()) {
									if (existingPkg.equals(getAlteredPackageName(packageFragment))) {
										if (!CreateSnippetDialog.this.selectTypeCombo.getText().equals(existingPkg)) {
											CreateSnippetDialog.this.selectTypeCombo.select(CreateSnippetDialog.this.selectTypeCombo
													.indexOf(existingPkg));
										}
										addItem1 = false;
										break;

									}
								}
							}
							if (addItem1) {
								CreateSnippetDialog.this.selectTypeCombo.add(getAlteredPackageName(packageFragment));
								CreateSnippetDialog.this.selectTypeCombo.select(CreateSnippetDialog.this.selectTypeCombo.getItemCount() - 1);
							}
							/*if (!fastCodeCache.getPackageSet().contains(packageFragment)) {
								fastCodeCache.getPackageSet().add(packageFragment);
							}*/

							if (CreateSnippetDialog.this.browsedFirstTemplateMap.isEmpty()) {
								CreateSnippetDialog.this.browsedFirstTemplateMap.put(getAlteredPackageName(packageFragment),
										packageFragment);
							} else {
								if (!CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(getAlteredPackageName(packageFragment))) {
									CreateSnippetDialog.this.browsedFirstTemplateMap.put(getAlteredPackageName(packageFragment),
											packageFragment);
								}
							}
							CreateSnippetDialog.this.createSnippetData.setPackageFragment(packageFragment);
						}

						if (!isEmpty(CreateSnippetDialog.this.selectTypeCombo.getText())) {
							CreateSnippetDialog.this.setErrorMessage(CreateSnippetDialog.this.defaultMessage);
						}
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
					break;
				case Enumeration:
					final SelectionDialog enumDialog;
					try {
						enumDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
								SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ENUMS, false, EMPTY_STR);
						enumDialog.setTitle("Select Enumeration ");
						enumDialog.setMessage("Select the enumeration to get fields from");

						if (enumDialog.open() == CANCEL) {
							return;
						}
						final IType browsedEnumType = (IType) enumDialog.getResult()[0];
						CreateSnippetDialog.this.createSnippetData.setEnumType(browsedEnumType);
						boolean addItem1 = true;
						if (CreateSnippetDialog.this.selectTypeCombo.getItems() != null) {
							for (final String existingEnum : CreateSnippetDialog.this.selectTypeCombo.getItems()) {
								if (existingEnum.equals(browsedEnumType.getFullyQualifiedName())) {
									if (!CreateSnippetDialog.this.selectTypeCombo.getText().equals(existingEnum)) {
										CreateSnippetDialog.this.selectTypeCombo.select(CreateSnippetDialog.this.selectTypeCombo
												.indexOf(existingEnum));
										setInstanceNameCombo(existingEnum);
									}
									addItem1 = false;
									break;
								}
							}
						}
						if (addItem1) {
							CreateSnippetDialog.this.selectTypeCombo.add(browsedEnumType.getFullyQualifiedName());
							CreateSnippetDialog.this.selectTypeCombo.select(CreateSnippetDialog.this.selectTypeCombo.getItemCount() - 1);
							setInstanceNameCombo(browsedEnumType.getFullyQualifiedName());
						}
						/*if (!fastCodeCache.getTypeSet().contains(browsedEnumType)) {
							fastCodeCache.getTypeSet().add(browsedEnumType);
						}*/

						if (CreateSnippetDialog.this.browsedFirstTemplateMap.isEmpty()) {
							CreateSnippetDialog.this.browsedFirstTemplateMap.put(browsedEnumType.getFullyQualifiedName(), browsedEnumType);
						} else {
							if (!CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(browsedEnumType.getFullyQualifiedName())) {
								CreateSnippetDialog.this.browsedFirstTemplateMap.put(browsedEnumType.getFullyQualifiedName(),
										browsedEnumType);
							}
						}
						CreateSnippetDialog.this.setErrorMessage(CreateSnippetDialog.this.defaultMessage);
					} catch (final JavaModelException ex) {
						ex.printStackTrace();
					}
					break;

				}

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
		label.setText("GroupBy and OrderBy : ");
		this.groupByButton = new Button(composite, SWT.CHECK);
		this.groupByButton.setText("Group By");
		this.groupByButton.setSelection(false);
		this.groupByButton.setEnabled(false);
		this.groupByButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.widget).getSelection()) {
					if (CreateSnippetDialog.this.tableCombo.getSelectionIndex() == -1) {
						setErrorMessage("Please choose a table.");
						CreateSnippetDialog.this.groupByButton.setSelection(false);
						return;
					} else {
						final String tableName = CreateSnippetDialog.this.tableCombo.getItem(CreateSnippetDialog.this.tableCombo
								.getSelectionIndex());
						final String schemaName = CreateSnippetDialog.this.schemaCombo.getItem(CreateSnippetDialog.this.schemaCombo
								.getSelectionIndex());
						if (tableName != null && schemaName != null) {
							final OrderByGroupByDialog dialog = new OrderByGroupByDialog(new Shell(), "group_by", tableName, schemaName);
							if (dialog.open() == Window.CANCEL) {
								return;
							}
							CreateSnippetDialog.this.createSnippetData.setgroupByFieldSelectionMap(dialog.getSelectedFields());

						} else {
							setErrorMessage("Please choose a table.");
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}

		});
		this.orderByButton = new Button(composite, SWT.CHECK);
		this.orderByButton.setText("Order By");
		this.orderByButton.setEnabled(false);
		this.orderByButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.widget).getSelection()) {
					if (CreateSnippetDialog.this.tableCombo.getSelectionIndex() == -1) {
						setErrorMessage("Please choose a table.");
						CreateSnippetDialog.this.orderByButton.setSelection(false);
						return;
					}
					final String tableName = CreateSnippetDialog.this.tableCombo.getItem(CreateSnippetDialog.this.tableCombo
							.getSelectionIndex());
					final String schemaName = CreateSnippetDialog.this.schemaCombo.getItem(CreateSnippetDialog.this.schemaCombo
							.getSelectionIndex());
					if (tableName != null && schemaName != null) {

						final OrderByGroupByDialog dialog = new OrderByGroupByDialog(new Shell(), "order_by", tableName, schemaName);
						if (dialog.open() == Window.CANCEL) {

							return;
						}
						CreateSnippetDialog.this.createSnippetData.setorderByFieldSelectionMap(dialog.getSelectedFields());

					} else {
						setErrorMessage("Please choose a table.");
					}
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

	}

	/*private void createFolderSelectionPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Select Folder:               ");
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.folderCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);// new
																				// Text(composite,
		// SWT.BORDER);
		this.folderCombo.setSize(200, 20);
		this.folderCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 500;
		this.folderCombo.setEnabled(false);

		final FastCodeCache fcCache = FastCodeCache.getInstance();
		if (!fcCache.getFolderSet().isEmpty()) {
			for (final IFolder folder : fcCache.getFolderSet()) {
				this.folderCombo.add(folder.getFullPath().toString());
			}
		}
		this.folderCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(final SelectionEvent event) {
				final String selectedFolderPath = CreateSnippetDialog.this.folderCombo.getText();
				try {
					for (final IFolder folder : fcCache.getFolderSet()) {
						if (folder.getFullPath().toString().equals(selectedFolderPath)) {
							CreateSnippetDialog.this.createSnippetData.setFolder(folder);
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}

		});
		this.folderCombo.addFocusListener(new FocusListener() {

			public void focusLost(final FocusEvent e) {
				final String inputFolderPath = CreateSnippetDialog.this.folderCombo.getText();
				for (final IFolder folder : fcCache.getFolderSet()) {
					if (folder.getFullPath().toString().equals(inputFolderPath)) {
						return;
					}
				}

				 * final boolean inputResult =
				 * isFullyQualifiedName(inputFromClassName); if (inputResult) {

				try { // to do

					 * final IType inputClassType =
					 * getTypeFromWorkspace(inputFromClassName); if
					 * (inputClassType != null) {
					 * CreateSnippetDialog.this.createSnippetData
					 * .setiSelectPojoClassType(inputClassType); if
					 * (!classCache.getTypeSet().contains(inputClassType)) {
					 * classCache.getTypeSet().add(inputClassType); }
					 *
					 * } else { setErrorMessage(
					 * "Class does not exist,Please enter an existing class name "
					 * ); }

				} catch (final Exception ex) {
					ex.printStackTrace();
				}


				 * } else { setErrorMessage(
				 * "Please write a fully qualified name like Package Name.Class name"
				 * ); }

			}

			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		final GridData gridDataButton = new GridData();

		this.folderBrowseButton = new Button(composite, SWT.PUSH);
		this.folderBrowseButton.setText("Browse");
		this.folderBrowseButton.setLayoutData(gridDataButton);
		this.folderBrowseButton.setEnabled(false);

		this.folderBrowseButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(final SelectionEvent event) {
				try {
					IPath path = null;
					final ContainerSelectionDialog dialog = new ContainerSelectionDialog(new Shell(), null, true, "Select a folder:");
					dialog.setTitle("Select a Folder");
					// dialog.//showClosedProjects(true);
					if (dialog.open() != CANCEL) {
						path = (IPath) dialog.getResult()[0];

						// final String project = path.segment(0);
						// final String srcPath =
						// path.toString().substring(project.length() + 1);
						// final IResource res =
						// getResourceFromWorkspace(path.toString());
						final IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
						// final IFolder folder = res.getProject().getFolder(new
						// Path(srcPath));

						 * final IFolder folder =
						 * CreateSnippetDialog.this.createSnippetData
						 * .getJavaProject().getProject() .getFolder(new
						 * Path(srcPath));

						// CreateSnippetDialog.this.folderCombo.setText(folder.getFullPath().toString());
						CreateSnippetDialog.this.folderCombo.add(folder.getFullPath().toString());
						CreateSnippetDialog.this.folderCombo.select(CreateSnippetDialog.this.folderCombo.getItemCount() - 1);
						if (!fcCache.getFolderSet().contains(folder)) {
							fcCache.getFolderSet().add(folder);
						}
						CreateSnippetDialog.this.createSnippetData.setFolder(folder);
					}

				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}*/

	/**
	 * @param parent
	 */
	/*private void createPackageSelectionPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Select Package:           ");
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.packageCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);// new
		// Text(composite,
		// SWT.BORDER);
		this.packageCombo.setSize(200, 20);
		this.packageCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 500;
		this.packageCombo.setEnabled(false);

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (!fastCodeCache.getPackageSet().isEmpty()) {
			for (final IPackageFragment pkgFrgmt : fastCodeCache.getPackageSet()) {
				this.packageCombo.add(getAlteredPackageName(pkgFrgmt));
			}
		}
		this.packageCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(final SelectionEvent event) {
				final String selectedPkgName = CreateSnippetDialog.this.packageCombo.getText();
				try {
					for (final IPackageFragment pkg : fastCodeCache.getPackageSet()) {
						if (getAlteredPackageName(pkg).equals(selectedPkgName)) {
							CreateSnippetDialog.this.createSnippetData.setPackageFragment(pkg);
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}

		});
		this.packageCombo.addFocusListener(new FocusListener() {

			public void focusLost(final FocusEvent e) {
				final String inputPkgName = CreateSnippetDialog.this.packageCombo.getText();
				for (final IPackageFragment pkg : fastCodeCache.getPackageSet()) {
					if (pkg.getElementName().equals(inputPkgName)) {
						return;
					}
				}

				 * final boolean inputResult =
				 * isFullyQualifiedName(inputFromClassName); if (inputResult) {

				try { // to do

					 * final IType inputClassType =
					 * getTypeFromWorkspace(inputFromClassName); if
					 * (inputClassType != null) {
					 * CreateSnippetDialog.this.createSnippetData
					 * .setiSelectPojoClassType(inputClassType); if
					 * (!classCache.getTypeSet().contains(inputClassType)) {
					 * classCache.getTypeSet().add(inputClassType); }
					 *
					 * } else { setErrorMessage(
					 * "Class does not exist,Please enter an existing class name "
					 * ); }

				} catch (final Exception ex) {
					ex.printStackTrace();
				}


				 * } else { setErrorMessage(
				 * "Please write a fully qualified name like Package Name.Class name"
				 * ); }

			}

			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		final GridData gridDataButton = new GridData();

		this.packageBrowseButton = new Button(composite, SWT.PUSH);
		this.packageBrowseButton.setText("Browse");
		this.packageBrowseButton.setLayoutData(gridDataButton);
		this.packageBrowseButton.setEnabled(false);

		this.packageBrowseButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(final SelectionEvent event) {
				try {
					final IPackageFragment allPackages[] = getPackagesInProject(CreateSnippetDialog.this.createSnippetData.getJavaProject());
					final java.util.List<IPackageFragment> nonEmptyPackages = new ArrayList<IPackageFragment>();
					for (final IPackageFragment packageFragment : allPackages) {
						final ICompilationUnit[] compilationUnits = packageFragment.getCompilationUnits();
						if (compilationUnits != null && compilationUnits.length > 0) {
							nonEmptyPackages.add(packageFragment);
						}
					}

					PackageSelectionDialog selectionDialog = null;

					if (CreateSnippetDialog.this.createSnippetData.getTemplateType().equals(TEMPLATE_CREATE_IMPL)
							|| CreateSnippetDialog.this.createSnippetData.getTemplateType().equals(TEMPLATE_SPRING_BEAN_FILE)
							|| CreateSnippetDialog.this.createSnippetData.getTemplateType().equals(TEMPLATE_INSTANCE_OF_CLASSES)
							|| CreateSnippetDialog.this.createSnippetData.getTemplateType().equals(TEMPLATE_COPY_CLASSES)
							|| CreateSnippetDialog.this.createSnippetData.getTemplateType().equals(TEMPLATE_INSTANCE_OF_GENERIC_DAO)) {
						selectionDialog = new PackageSelectionDialog(new Shell(), "Package ", "Choose a package from below",
								nonEmptyPackages.toArray(new IPackageFragment[0]));
					} else {
						selectionDialog = new PackageSelectionDialog(new Shell(), "Package ", "Choose a package from below", allPackages);
					}
					IPackageFragment packageFragment = null;
					if (selectionDialog.open() != CANCEL) {
						packageFragment = (IPackageFragment) selectionDialog.getFirstResult();
						// CreateSnippetDialog.this.packageCombo.setText(getAlteredPackageName(packageFragment));
						CreateSnippetDialog.this.packageCombo.add(getAlteredPackageName(packageFragment));
						CreateSnippetDialog.this.packageCombo.select(CreateSnippetDialog.this.packageCombo.getItemCount() - 1);
						if (!fastCodeCache.getPackageSet().contains(packageFragment)) {
							fastCodeCache.getPackageSet().add(packageFragment);
						}
						CreateSnippetDialog.this.createSnippetData.setPackageFragment(packageFragment);
					}

				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}*/

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
		label.setText("Select Pojo Class:        ");
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.pojoClassCombo = new Combo(composite, SWT.NONE);
		this.pojoClassCombo.setSize(200, 20);
		this.pojoClassCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 500;
		this.pojoClassCombo.setEnabled(false);

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (!fastCodeCache.getTypeSet().isEmpty()) {
			for (final IType type : fastCodeCache.getTypeSet()) {
				this.pojoClassCombo.add(type.getFullyQualifiedName());
			}
		}
		this.pojoClassCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String selectedPojoClassName = CreateSnippetDialog.this.pojoClassCombo.getText();
				try {
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.getFullyQualifiedName().equals(selectedPojoClassName)) {
								CreateSnippetDialog.this.createSnippetData.setiSelectPojoClassType(type);
							}
						}
					}
					if (CreateSnippetDialog.this.createSnippetData.getiSelectPojoClassType() == null) {
						if (CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(selectedPojoClassName)) {
							CreateSnippetDialog.this.createSnippetData
									.setiSelectPojoClassType((IType) CreateSnippetDialog.this.browsedFirstTemplateMap
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
				final String inputPojoClassName = CreateSnippetDialog.this.pojoClassCombo.getText();
				if (!isEmpty(inputPojoClassName)) {
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.getFullyQualifiedName().equals(inputPojoClassName)) {
								return;
							}
						}
					}
					if (CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(inputPojoClassName)) {
						return;
					}
					try {
						final IType inputClassType = getTypeFromWorkspace(inputPojoClassName);
						if (inputClassType != null) {
							CreateSnippetDialog.this.createSnippetData.setiSelectPojoClassType(inputClassType);
							if (!CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(inputClassType.getFullyQualifiedName())) {
								CreateSnippetDialog.this.browsedFirstTemplateMap.put(inputClassType.getFullyQualifiedName(), inputClassType);
							}
							/*if (!fastCodeCache.getTypeSet().contains(inputClassType)) {
								fastCodeCache.getTypeSet().add(inputClassType);
							}*/
							setErrorMessage(CreateSnippetDialog.this.defaultMessage);
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

				if (isEmpty(CreateSnippetDialog.this.pojoClassCombo.getText())) {
					setErrorMessage("Please choose a Class");

				} else {
					setErrorMessage(CreateSnippetDialog.this.defaultMessage);
				}
			}

		});

		final GridData gridDataButton = new GridData();

		this.pojoClassBrowseButton = new Button(composite, SWT.PUSH);
		this.pojoClassBrowseButton.setText("Browse");
		this.pojoClassBrowseButton.setLayoutData(gridDataButton);
		this.pojoClassBrowseButton.setEnabled(false);

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
					CreateSnippetDialog.this.createSnippetData.setiSelectPojoClassType(browsedPojoClassType);
					boolean addItem = true;
					if (CreateSnippetDialog.this.pojoClassCombo.getItems() != null) {
						for (final String existingPojoClass : CreateSnippetDialog.this.pojoClassCombo.getItems()) {
							if (existingPojoClass.equals(browsedPojoClassType.getFullyQualifiedName())) {
								if (!CreateSnippetDialog.this.pojoClassCombo.getText().equals(existingPojoClass)) {
									CreateSnippetDialog.this.pojoClassCombo.select(CreateSnippetDialog.this.pojoClassCombo
											.indexOf(existingPojoClass));
								}
								addItem = false;
								break;
							}
						}
					}
					if (addItem) {
						CreateSnippetDialog.this.pojoClassCombo.add(browsedPojoClassType.getFullyQualifiedName());
						CreateSnippetDialog.this.pojoClassCombo.select(CreateSnippetDialog.this.pojoClassCombo.getItemCount() - 1);
					}
					//CreateSnippetDialog.this.pojoClassCombo.setText(browsedPojoClassType.getFullyQualifiedName());

					if (CreateSnippetDialog.this.browsedFirstTemplateMap.isEmpty()) {
						CreateSnippetDialog.this.browsedFirstTemplateMap.put(browsedPojoClassType.getFullyQualifiedName(),
								browsedPojoClassType);
					} else {
						if (!CreateSnippetDialog.this.browsedFirstTemplateMap.containsKey(browsedPojoClassType.getFullyQualifiedName())) {
							CreateSnippetDialog.this.browsedFirstTemplateMap.put(browsedPojoClassType.getFullyQualifiedName(),
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
	private void createTemplateBodyMultiText(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		setMultiTextParent(composite);

		final GridData gridDataText = new GridData(550, 100);

		this.templateBodyMultiText = new TemplateFieldEditor("snippet", "Template Body:           ", composite, "snippet",
				FIELDS.TEMPLATE_BODY, SWT.MULTI);
		this.templateBodyMultiText.setLayout(gridDataText);

		if (this.createSnippetData.getSnippetTypes() != null && this.createSnippetData.getSnippetTypes().length == 1) {
			if (this.createSnippetData.getTemplateSettings() != null
					&& this.createSnippetData.getTemplateSettings().getTemplateBody() != null) {
				try {
					this.templateBodyMultiText.setText(this.createSnippetData.getTemplateSettings().getTemplateBody().trim());
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}
		} else {
			this.templateBodyMultiText.setEnabled(false, composite);
		}

	}

	/**
	 * @param parent
	 */
	private void createErrorMessageText(final Composite parent) {

		final GridData errText = new GridData(590, 40);
		errText.grabExcessHorizontalSpace = true;

		this.errorMessageText = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
		// this.errorMessageText.setBackground(new Color(null, 255, 255, 255));

		this.errorMessageText.setForeground(FastCodeColor.getErrorMsgColor());
		this.errorMessageText.setLayoutData(errText);
		setErrorMessage(this.defaultMessage);

	}

	/**
	 * @param parent
	 */
	private void createToClassInstancePane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		this.toClassInstLabel = new Label(composite, SWT.NONE);

		this.toClassInstLabel.setText("To Class Instance:      ");
		this.toClassInstLabel.setLayoutData(gridDataLabel);
		this.toClassInstLabel.setVisible(false);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.toClassInstCombo = new Combo(composite, SWT.NONE);
		this.toClassInstCombo.setSize(200, 20);
		this.toClassInstCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 400;
		this.toClassInstCombo.setEnabled(false);
		this.toClassInstCombo.setVisible(false);

		this.toClassInstCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				final String toInstance = CreateSnippetDialog.this.toClassInstCombo.getText();
				if (isEmpty(toInstance)) {
					setErrorMessage("Instance name cannot be empty");
				} else {
					setErrorMessage(CreateSnippetDialog.this.defaultMessage);
				}
				/*if (toInstance.equals(CURRENT_CLASS + " Instance")) {
					toInstance = createDefaultInstance(CreateSnippetDialog.this.currentType);
				}*/
				CreateSnippetDialog.this.createSnippetData.setToInstanceName(toInstance);

			}
		});

	}

	/**
	 * @param parent
	 */
	private void createFromClassInstacnePane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		this.fromClassInstLabel = new Label(composite, SWT.NONE);

		this.fromClassInstLabel.setText("Class Instance:             ");
		this.fromClassInstLabel.setLayoutData(gridDataLabel);
		this.fromClassInstLabel.setVisible(false);
		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.fromClassInstCombo = new Combo(composite, SWT.NONE);
		this.fromClassInstCombo.setSize(200, 20);
		this.fromClassInstCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 400;
		this.fromClassInstCombo.setEnabled(false);
		this.fromClassInstCombo.setVisible(false);

		this.fromClassInstCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				final String fromInstance = CreateSnippetDialog.this.fromClassInstCombo.getText();
				if (isEmpty(fromInstance)) {
					setErrorMessage("Instance name cannot be empty");
				} else {
					setErrorMessage(CreateSnippetDialog.this.defaultMessage);
				}
				/*if (fromInstance.equals(CURRENT_CLASS + " Instance")) {
					fromInstance = createDefaultInstance(CreateSnippetDialog.this.currentType);
				}*/
				CreateSnippetDialog.this.createSnippetData.setInstanceName(fromInstance);

			}
		});

	}

	/**
	 * @param parent
	 */
	private void createTableSelectionPane(final Composite parent) {
		final Composite cmposite = new Composite(parent, parent.getStyle());

		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		cmposite.setLayout(layout);

		final Label tableLabel = new Label(cmposite, SWT.NONE);
		tableLabel.setText("Table:                            ");
		tableLabel.setLayoutData(new GridData());

		this.tableCombo = new Combo(cmposite, SWT.DROP_DOWN);
		this.tableCombo.setLayoutData(new GridData(150, 100));
		final ConnectToDatabase connectToDatabase = ConnectToDatabase.getInstance();
		poulateTableCombo(connectToDatabase);
		this.tableCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				CreateSnippetDialog.this.createSnippetData.setTableSelected(CreateSnippetDialog.this.tableCombo
						.getItem(CreateSnippetDialog.this.tableCombo.getSelectionIndex()));
				try {
					if (CreateSnippetDialog.this.tableCombo.getSelectionIndex() >= 0) {
						setErrorMessage(CreateSnippetDialog.this.defaultMessage);
						if (CreateSnippetDialog.this.createSnippetData.getTemplateType().equals(DATABASE_TEMPLATE_POJO_CLASS)
								|| CreateSnippetDialog.this.createSnippetData.getTemplateType().equals(
										DATABASE_TEMPLATE_POJO_CLASS_WITHOUT_ANNOTATION)) {
							if (pojoClassForTableExist(CreateSnippetDialog.this.createSnippetData.getTableSelected())) {
								setErrorMessage("Pojo Class for the selected table already exist. Use \"Add Fields to POJO Class\" to add fields.");
							}
						}
					} else {
						setErrorMessage("Please choose a table.");
					}

				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}
		});

		this.tableCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				CreateSnippetDialog.this.createSnippetData.setTableSelected(CreateSnippetDialog.this.tableCombo.getText());

				if (!isEmpty(CreateSnippetDialog.this.createSnippetData.getTableSelected())) {
					setErrorMessage(CreateSnippetDialog.this.defaultMessage);
					if (!CreateSnippetDialog.this.createSnippetData.getTablesInDB().contains(
							CreateSnippetDialog.this.createSnippetData.getTableSelected())) {
						setErrorMessage("This table is not there in the DB.");
					}
					try {
						if (CreateSnippetDialog.this.createSnippetData.getTemplateType().equals(DATABASE_TEMPLATE_POJO_CLASS)
								|| CreateSnippetDialog.this.createSnippetData.getTemplateType().equals(
										DATABASE_TEMPLATE_POJO_CLASS_WITHOUT_ANNOTATION)) {
							if (pojoClassForTableExist(CreateSnippetDialog.this.createSnippetData.getTableSelected())) {
								setErrorMessage("Pojo Class for the selected table already exist. Use \"Add Fields to POJO Class\" to add fields.");
							}
						}
					} catch (final Exception ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				} else {
					setErrorMessage("Please choose a table.");
				}
			}
		});
		this.tableCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent arg0) {
				setErrorMessage(CreateSnippetDialog.this.defaultMessage);

			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				if (CreateSnippetDialog.this.schemaCombo.getText().equals(EMPTY_STR)) {
					setErrorMessage("Please select a schema first");
				}
			}
		});

	}

	/**
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	private boolean pojoClassForTableExist(final String tableName) throws Exception {
		final String pojoClassName = tableName.substring(0, 1).toUpperCase() + tableName.substring(1).toLowerCase() + "POJO";

		IJavaProject workingJavaProject;
		if (this.preferenceStore.contains(P_WORKING_JAVA_PROJECT)) {
			workingJavaProject = getJavaProject(this.preferenceStore.getString(P_WORKING_JAVA_PROJECT));
		} else {
			workingJavaProject = getWorkingJavaProjectFromUser();
			this.preferenceStore.setValue(P_WORKING_JAVA_PROJECT, workingJavaProject.getElementName());
		}
		IPackageFragment pojoClassPackage = null;
		if (this.preferenceStore.contains(P_DB_PACKAGE_FOR_POJO_CLASS)) {
			final String srcPath = getDefaultPathFromProject(workingJavaProject, "source", EMPTY_STR);
			for (final IPackageFragment pkgFragment : getPackagesInProject(workingJavaProject, srcPath, "source")) {
				if (pkgFragment.getElementName().equals(this.preferenceStore.getString(P_DB_PACKAGE_FOR_POJO_CLASS))) {
					pojoClassPackage = pkgFragment;
				}
			}
			if (pojoClassPackage == null) {
				MessageDialog.openInformation(
						new Shell(),
						"Information",
						"The POJO Class Location specified in Fast Code->Database->Pojo Class,does not exist in "
								+ workingJavaProject.getElementName() + " Project");
				pojoClassPackage = getPackageFromUser(workingJavaProject);
				this.preferenceStore.setValue(P_DB_PACKAGE_FOR_POJO_CLASS, pojoClassPackage.getElementName());
			}

			//pojoClassPackage = getPackageFragmentFromWorkspace(this.preferenceStore.getString(P_DB_PACKAGE_FOR_POJO_CLASS));
		} else {
			pojoClassPackage = getPackageFromUser(workingJavaProject);
			this.preferenceStore.setValue(P_DB_PACKAGE_FOR_POJO_CLASS, pojoClassPackage.getElementName());
		}
		final IType type = workingJavaProject.findType(pojoClassPackage.getElementName() + DOT + pojoClassName);
		if (type != null && type.exists()) {
			return true;
		}

		return false;
	}

	/**
	 * @param parent
	 */
	/*
	private void createFileSelectionPane(final Composite parent) {
	final Composite composite = new Composite(parent, parent.getStyle());
	final GridLayout layout = new GridLayout();
	layout.numColumns = 3;
	composite.setLayout(layout);

	final GridData gridDataLabel = new GridData();
	final Label label = new Label(composite, SWT.NONE);
	label.setText("Select File:                    ");
	label.setLayoutData(gridDataLabel);

	final GridData gridDataText = new GridData();
	gridDataText.grabExcessHorizontalSpace = true;

	this.fileNameCombo = new Combo(composite, SWT.NONE);// new
														// Text(composite,SWT.BORDER|SWT.MULTI);
	this.fileNameCombo.setSize(200, 20);
	this.fileNameCombo.setLayoutData(gridDataText);
	gridDataText.minimumWidth = 500;
	this.fileNameCombo.setEnabled(false);

	final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
	if (!fastCodeCache.getFileSet().isEmpty()) {
		for (final IFile file : fastCodeCache.getFileSet()) {
			// this.fileNameCombo.add(iFile.getName());
			this.fileNameCombo.add(file.getProjectRelativePath().toOSString()); // +
																				// "/"
																				// +
																				// iFile.getName());
			// System.out.println( iFile.getName());
		}
	}
	this.fileNameCombo.addSelectionListener(new SelectionListener() {

		public void widgetSelected(final SelectionEvent event) {
			final String selectedFileName = CreateSnippetDialog.this.fileNameCombo.getText();
			try {
				for (final IFile file : fastCodeCache.getFileSet()) {
					if (file.getName().equals(selectedFileName.substring(selectedFileName.lastIndexOf('\\') + 1))) {
						CreateSnippetDialog.this.createSnippetData.getFastCodeFiles().add(
								new FastCodeFile(file.getName(), file.getProjectRelativePath().toString()));
						CreateSnippetDialog.this.createSnippetData.setResourceFile(file);

					}
				}

			} catch (final Exception ex) {
				ex.printStackTrace();
			}

		}

		public void widgetDefaultSelected(final SelectionEvent arg0) {
			// TODO Auto-generated method stub

		}
	});
	this.fileNameCombo.addModifyListener(new ModifyListener() {

		public void modifyText(final ModifyEvent e) {

			if (isEmpty(CreateSnippetDialog.this.fileNameCombo.getText())) {
				setErrorMessage("Please choose a file");
			} else {

				 * if
				 * (!isValidVariableName(CreateSnippetDialog.this.fileNameCombo
				 * .getText())) {
				 * setErrorMessage("Special charecters can not be used"); }
				 * else {
				 * setErrorMessage(CreateSnippetDialog.this.defaultMessage);
				 * }

				setErrorMessage(CreateSnippetDialog.this.defaultMessage);
			}
		}
	});
	this.fileNameCombo.addFocusListener(new FocusListener() {

		public void focusLost(final FocusEvent e) {
			final String inputFileName = CreateSnippetDialog.this.fileNameCombo.getText();
			for (final IFile file : fastCodeCache.getFileSet()) {
				if (file.getName().equals(inputFileName.substring(inputFileName.lastIndexOf('\\') + 1))) {

					return;
				}
			}

			final boolean inputResult = isFullNameOfFile(inputFileName);
			if (inputResult) {
				try {
					final Path inputPath = new Path(inputFileName);
					final IPath iInputPath = inputPath.makeAbsolute();
					final IFile inputFile = ResourcesPlugin.getWorkspace().getRoot().getFile(iInputPath);
					if (inputFile != null && inputFile.exists()) {
						final FastCodeFile inputFastCodeFile = new FastCodeFile(inputFile.getName(), inputFile.getProjectRelativePath()
								.toString());
						CreateSnippetDialog.this.createSnippetData.getFastCodeFiles().add(inputFastCodeFile);
						CreateSnippetDialog.this.createSnippetData.setResourceFile(inputFile);
						if (!fastCodeCache.getFileSet().contains(inputFile)) {
							fastCodeCache.getFileSet().add(inputFile);
						}
					} else {
						CreateSnippetDialog.this.setErrorMessage("File does not exist,Please enter an existing file name");
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}

			} else {
				CreateSnippetDialog.this.setErrorMessage("Please enter file name with full path like /Project Name/....../file name ");
			}
		}

		public void focusGained(final FocusEvent arg0) {

		}
	});
	final GridData gridDataButton = new GridData();

	this.browseFile = new Button(composite, SWT.PUSH);
	this.browseFile.setText("Browse");
	this.browseFile.setLayoutData(gridDataButton);
	this.browseFile.setEnabled(false);
	this.browseFile.addSelectionListener(new SelectionListener() {

		public void widgetDefaultSelected(final SelectionEvent e) {
		}

		public void widgetSelected(final SelectionEvent e) {
			final OpenResourceDialog resourceDialog;
			resourceDialog = new OpenResourceDialog(parent.getShell(), ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);
			resourceDialog.setTitle("Select File ");
			resourceDialog.setMessage("Select the File to get fields from");

			if (resourceDialog.open() == CANCEL) {
				return;
			}
			final IFile browseFile = (IFile) resourceDialog.getResult()[0];
			final String browsePath = browseFile.getProjectRelativePath().toString();
			final FastCodeFile browseFastCodeFile = new FastCodeFile(browseFile.getName(), browsePath);
			CreateSnippetDialog.this.createSnippetData.getFastCodeFiles().add(browseFastCodeFile);
			CreateSnippetDialog.this.fileNameCombo.setText(browsePath); // browseFastCodeFile.getFullName());
			CreateSnippetDialog.this.createSnippetData.setResourceFile(browseFile);
			if (!fastCodeCache.getFileSet().contains(browseFile)) {
				fastCodeCache.getFileSet().add(browseFile);
			}
		}

	});

	}
	*/
	/**
	 * @param fileName
	 * @return
	 */

	/**
	 * @param parent
	 */
	/*
	private void createFromClassSelectionPane(final Composite parent) {
	final Composite composite = new Composite(parent, parent.getStyle());
	final GridLayout layout = new GridLayout();
	layout.numColumns = 3;
	composite.setLayout(layout);

	final GridData gridDataLabel = new GridData();
	this.fromClassLabel = new Label(composite, SWT.NONE);

	this.fromClassLabel.setText("Select Class:                 ");
	this.fromClassLabel.setLayoutData(gridDataLabel);

	final GridData gridDataText = new GridData();
	gridDataText.grabExcessHorizontalSpace = true;

	this.fromClassNameCombo = new Combo(composite, SWT.NONE);// new
																// Text(composite,
																// SWT.BORDER);
	this.fromClassNameCombo.setSize(200, 20);
	this.fromClassNameCombo.setLayoutData(gridDataText);
	gridDataText.minimumWidth = 500;
	this.fromClassNameCombo.setEnabled(false);

	final FastCodeCache fastCodeCache = FastCodeCache.getInstance();

	if (!fastCodeCache.getTypeSet().isEmpty()) {
		for (final IType type : fastCodeCache.getTypeSet()) {
			this.fromClassNameCombo.add(type.getFullyQualifiedName());
		}
	}

	this.fromClassNameCombo.addSelectionListener(new SelectionListener() {

		public void widgetSelected(final SelectionEvent event) {
			final String selectedFromClassName = CreateSnippetDialog.this.fromClassNameCombo.getText();
			try {
				for (final IType type : fastCodeCache.getTypeSet()) {
					if (type.getFullyQualifiedName().equals(selectedFromClassName)) {
						CreateSnippetDialog.this.createSnippetData.setFromClass(type);
					}
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}

			setInstanceNameCombo(selectedFromClassName);
		}

		public void widgetDefaultSelected(final SelectionEvent arg0) {
			// TODO Auto-generated method stub

		}
	});
	this.fromClassNameCombo.addFocusListener(new FocusListener() {

		public void focusLost(final FocusEvent e) {
			final String inputFromClassName = CreateSnippetDialog.this.fromClassNameCombo.getText();
			for (final IType type : fastCodeCache.getTypeSet()) {
				if (type.getFullyQualifiedName().equals(inputFromClassName)) {
					return;
				}
			}
			try {
				if (!isEmpty(inputFromClassName)) {
					final IType inputClassType = getTypeFromWorkspace(inputFromClassName);
					if (inputClassType != null) {
						CreateSnippetDialog.this.createSnippetData.setFromClass(inputClassType);
						setInstanceNameCombo(inputFromClassName);
						if (!fastCodeCache.getTypeSet().contains(inputClassType)) {
							fastCodeCache.getTypeSet().add(inputClassType);
						}
					} else {
						setErrorMessage("Class does not exist,Please enter an existing class name ");
					}
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}

		public void focusGained(final FocusEvent arg0) {
			// TODO Auto-generated method stub

		}
	});
	this.fromClassNameCombo.addModifyListener(new ModifyListener() {

		public void modifyText(final ModifyEvent e) {

			if (isEmpty(CreateSnippetDialog.this.fromClassNameCombo.getText())) {
				setErrorMessage("Please choose a Class");
			} else {
				if (!isValidVariableName(CreateSnippetDialog.this.fromClassNameCombo.getText())) {
					setErrorMessage("Special charecters can not be used");
				} else {
					setErrorMessage(CreateSnippetDialog.this.defaultMessage);
				}
				// setErrorMessage(CreateSnippetDialog.this.defaultMessage);
			}
		}

	});
	final GridData gridDataButton = new GridData();

	this.browseFromClass = new Button(composite, SWT.PUSH);
	this.browseFromClass.setText("Browse");
	this.browseFromClass.setLayoutData(gridDataButton);
	this.browseFromClass.setEnabled(false);

	this.browseFromClass.addSelectionListener(new SelectionListener() {

		public void widgetDefaultSelected(final SelectionEvent e) {
		}

		public void widgetSelected(final SelectionEvent e) {
			final SelectionDialog selectionDialog;
			try {
				selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
						SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, "");
				selectionDialog.setTitle("Select From Class ");
				selectionDialog.setMessage("Select the From Class to get fields from");

				if (selectionDialog.open() == CANCEL) {
					return;
				}
				CreateSnippetDialog.this.createSnippetData.setFromClass((IType) selectionDialog.getResult()[0]);
				CreateSnippetDialog.this.fromClassNameCombo.setText(((IType) selectionDialog.getResult()[0]).getFullyQualifiedName());
				setInstanceNameCombo(((IType) selectionDialog.getResult()[0]).getFullyQualifiedName());
				// FastCodeType browseFastCodeType = new
				// FastCodeType(((IType)
				// selectionDialog.getResult()[0]).getFullyQualifiedName());
				if (!fastCodeCache.getTypeSet().contains(selectionDialog.getResult()[0])) {
					fastCodeCache.getTypeSet().add((IType) selectionDialog.getResult()[0]);
				}

			} catch (final JavaModelException ex) {
				ex.printStackTrace();
			}

		}

	});

	}*/

	/**
	 * @param parent
	 */
	private void createToClassSelectionPane(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		this.toClassLabel = new Label(composite, SWT.NONE);
		this.toClassLabel.setText("Select To Class:           ");
		this.toClassLabel.setLayoutData(gridDataLabel);
		this.toClassLabel.setVisible(false);
		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.toClassNameCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		this.toClassNameCombo.setSize(200, 20);
		this.toClassNameCombo.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 500;
		this.toClassNameCombo.setEnabled(false);
		this.toClassNameCombo.setVisible(false);

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		final IEditorPart editorPart = CreateSnippetDialog.this.createSnippetData.getEditorpart();
		ICompilationUnit compUnit = null;
		if (editorPart != null) {
			compUnit = getCompilationUnitFromEditor(editorPart);
		}
		if (editorPart != null) {
			if (compUnit != null) {
				this.currentType = compUnit.getPrimary().findPrimaryType().getFullyQualifiedName();
				this.toClassNameCombo.add(ENCLOSING_CLASS_STR + HYPHEN + this.currentType);
				//this.toClassNameCombo.select(0);
			}
		}
		if (!fastCodeCache.getTypeSet().isEmpty()) {
			for (final IType type : fastCodeCache.getTypeSet()) {
				if (this.toClassNameCombo.getItems() != null) {
					for (final String existingClass : this.toClassNameCombo.getItems()) {
						if (!existingClass.contains(ENCLOSING_CLASS_STR)) {
							if (!existingClass.equals(type.getFullyQualifiedName())) {
								this.toClassNameCombo.add(type.getFullyQualifiedName());
							}
						}
					}
				}
			}
		}
		/*if (!fastCodeCache.getTypeSet().isEmpty()) {
			for (final IType type : fastCodeCache.getTypeSet()) {
				this.toClassNameCombo.add(type.getFullyQualifiedName());
			}
		}*/
		this.toClassNameCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				String selectedToClassName = CreateSnippetDialog.this.toClassNameCombo.getText();
				if (selectedToClassName.contains(ENCLOSING_CLASS_STR)) {
					selectedToClassName = CreateSnippetDialog.this.currentType;
				}
				try {
					for (final IType type : fastCodeCache.getTypeSet()) {
						if (type.getFullyQualifiedName().equals(selectedToClassName)) {
							CreateSnippetDialog.this.createSnippetData.setToClass(type);
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
				/*if (CreateSnippetDialog.this.toClassNameCombo.getText().equals(CURRENT_CLASS)) {
					CreateSnippetDialog.this.toClassInstCombo.setText(CURRENT_CLASS + " Instance");
				} else {*/
				CreateSnippetDialog.this.toClassInstCombo.setText(createDefaultInstance(selectedToClassName));
				//}
				/*	CreateSnippetDialog.this.toClassInstCombo.add(createDefaultInstance(selectedToClassName));
					CreateSnippetDialog.this.toClassInstCombo.select(CreateSnippetDialog.this.toClassInstCombo.getItemCount() - 1);*/
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.toClassNameCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				if (isEmpty(CreateSnippetDialog.this.toClassNameCombo.getText())) {
					setErrorMessage("Please select To Class");
				} else {
					if (!isValidVariableName(CreateSnippetDialog.this.toClassNameCombo.getText())) {
						setErrorMessage("Special charecters can not be used");
					} else {
						setErrorMessage(CreateSnippetDialog.this.defaultMessage);
					}
					// setErrorMessage(CreateSnippetDialog.this.defaultMessage);
				}
			}
		});
		this.toClassNameCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				String inputToClassName = CreateSnippetDialog.this.toClassNameCombo.getText();
				if (inputToClassName.contains(ENCLOSING_CLASS_STR)) {
					inputToClassName = CreateSnippetDialog.this.currentType;
				}
				for (final IType type : fastCodeCache.getTypeSet()) {
					if (type.getFullyQualifiedName().equals(inputToClassName)) {
						return;
					}
				}

				try {
					final IType inputClassType = getTypeFromWorkspace(inputToClassName);
					if (inputClassType != null) {
						CreateSnippetDialog.this.createSnippetData.setToClass(inputClassType);

						CreateSnippetDialog.this.toClassInstCombo.setText(createDefaultInstance(inputToClassName));
						//CreateSnippetDialog.this.toClassInstCombo.add(createDefaultInstance(inputToClassName));
						//CreateSnippetDialog.this.toClassInstCombo.select(CreateSnippetDialog.this.toClassInstCombo.getItemCount() - 1);
						if (!fastCodeCache.getTypeSet().contains(inputClassType)) {
							fastCodeCache.getTypeSet().add(inputClassType);
						}

					} else {
						setErrorMessage("Class does not exist");
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}

			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		final GridData gridDataButton = new GridData();

		this.browseToClass = new Button(composite, SWT.PUSH);
		this.browseToClass.setText("Browse To Class");
		this.browseToClass.setLayoutData(gridDataButton);
		this.browseToClass.setEnabled(false);
		this.browseToClass.setVisible(false);
		this.browseToClass.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final SelectionDialog selectionDialog;
				try {
					selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
							SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, EMPTY_STR);
					selectionDialog.setTitle("Select To Class ");
					selectionDialog.setMessage("Select the To Class to get fields from");

					if (selectionDialog.open() == CANCEL) {
						return;
					}
					CreateSnippetDialog.this.createSnippetData.setToClass((IType) selectionDialog.getResult()[0]);
					// CreateSnippetDialog.this.toClassNameCombo.setText(((IType)
					// selectionDialog.getResult()[0]).getFullyQualifiedName());
					CreateSnippetDialog.this.toClassNameCombo.add(((IType) selectionDialog.getResult()[0]).getFullyQualifiedName());
					CreateSnippetDialog.this.toClassNameCombo.select(CreateSnippetDialog.this.toClassNameCombo.getItemCount() - 1);
					CreateSnippetDialog.this.toClassInstCombo.setText(createDefaultInstance(((IType) selectionDialog.getResult()[0])
							.getElementName()));
					/*CreateSnippetDialog.this.toClassInstCombo.add(createDefaultInstance(((IType) selectionDialog.getResult()[0])
							.getElementName()));
					CreateSnippetDialog.this.toClassInstCombo.select(CreateSnippetDialog.this.toClassInstCombo.getItemCount() - 1);*/

					if (!fastCodeCache.getTypeSet().contains(selectionDialog.getResult()[0])) {
						fastCodeCache.getTypeSet().add((IType) selectionDialog.getResult()[0]);
					}

				} catch (final JavaModelException ex) {
					ex.printStackTrace();
				}

			}

		});

	}

	/**
	 * @param parent
	 */
	private void createTemplateVariationList(final Composite parent) {
		final Composite cmposite = new Composite(parent, parent.getStyle());

		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		cmposite.setLayout(layout);

		this.templateVariationLabel = new Label(cmposite, SWT.NONE);
		this.templateVariationLabel.setText("Template Variation:     ");
		this.templateVariationLabel.setLayoutData(new GridData());
		this.templateVariationLabel.setVisible(false);

		this.templateVariationCombo = new Combo(cmposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.templateVariationCombo.setLayoutData(new GridData(200, 100));
		this.templateVariationCombo.setEnabled(false);
		this.templateVariationCombo.setVisible(false);
		this.templateVariationCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				CreateSnippetDialog.this.createSnippetData
						.setVariationsSelected(new String[] { CreateSnippetDialog.this.templateVariationCombo
								.getItem(CreateSnippetDialog.this.templateVariationCombo.getSelectionIndex()) });
				if (CreateSnippetDialog.this.templateVariationCombo.getSelectionIndex() >= 0) {
					setErrorMessage(CreateSnippetDialog.this.defaultMessage);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}
		});

		final Label spaceLabel = new Label(cmposite, SWT.NONE);
		spaceLabel.setText("         ");

		this.templateVariationList = new List(cmposite, SWT.MULTI | SWT.BORDER);
		this.templateVariationList.setLayoutData(new GridData(200, 100));
		this.templateVariationList.setEnabled(false);
		this.templateVariationList.setVisible(false);
		this.templateVariationList.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				CreateSnippetDialog.this.createSnippetData.setVariationsSelected(CreateSnippetDialog.this.templateVariationList
						.getSelection());
				if (CreateSnippetDialog.this.templateVariationList.getSelectionIndex() >= 0) {
					setErrorMessage(CreateSnippetDialog.this.defaultMessage);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}
		});
	}

	/**
	 * @param parent
	 */
	private void createSnippetCombo(final Composite parent) {
		final Composite cmposite = new Composite(parent, parent.getStyle());

		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		cmposite.setLayout(layout);

		final Label snippetTypeLabel = new Label(cmposite, SWT.NONE);
		snippetTypeLabel.setText("Template:                      ");
		snippetTypeLabel.setLayoutData(new GridData());

		this.snippetCombo = new Combo(cmposite, SWT.NONE);
		this.snippetCombo.setLayoutData(new GridData(180, 15));

		if (this.createSnippetData.getSnippetTypes() != null) {
			for (final String snippetType : this.createSnippetData.getSnippetTypes()) {
				this.snippetCombo.add(snippetType);
			}
		}
		final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(
				CreateSnippetDialog.this.snippetCombo.getItems(), "snippet");
		final ComboContentAdapter comboAdapter = new ComboContentAdapter();
		final ContentProposalAdapter adapter = new ContentProposalAdapter(CreateSnippetDialog.this.snippetCombo, comboAdapter, provider,
				null, null);
		adapter.setPropagateKeys(true);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		this.snippetCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final String templateName = CreateSnippetDialog.this.snippetCombo.getItem(CreateSnippetDialog.this.snippetCombo
						.getSelectionIndex());
				CreateSnippetDialog.this.createSnippetData.setTemplateSettings(null);
				processTemplateType(templateName);
				if (CreateSnippetDialog.this.snippetCombo.getSelectionIndex() >= 0) {
					setErrorMessage(CreateSnippetDialog.this.defaultMessage);
				}
				/*if (CreateSnippetDialog.this.fromClassInstCombo != null && !isEmpty(CreateSnippetDialog.this.fromClassInstCombo.getText())) {
					CreateSnippetDialog.this.setInstanceNameCombo(CreateSnippetDialog.this.selectTypeCombo.getText());
				}*/
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {

			}
		});
		this.snippetCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent event) {
				final String templateName = CreateSnippetDialog.this.snippetCombo.getText();

				if (!isEmpty(templateName)) {
					setErrorMessage(CreateSnippetDialog.this.defaultMessage);
					boolean found = false;
					int selectionIndex = 0;
					int k = 0;
					boolean isUpper = false;
					for (final String snippetType : CreateSnippetDialog.this.createSnippetData.getSnippetTypes()) {
						if (snippetType.equals(templateName)) {
							found = true;
							selectionIndex = k;
							break;
						} else if (snippetType.equals(templateName.toUpperCase())) {
							found = true;
							selectionIndex = k;
							isUpper = true;
							break;
						}
						k++;
					}
					if (!found) {
						setErrorMessage("This template is not there in the config file/preference page.");
					} else {
						setErrorMessage(CreateSnippetDialog.this.defaultMessage);
						CreateSnippetDialog.this.createSnippetData.setTemplateSettings(null);
						CreateSnippetDialog.this.snippetCombo.select(selectionIndex);
						//CreateSnippetDialog.this.snippetCombo.setText(templateName);
						if (isUpper) {
							processTemplateType(templateName.toUpperCase());
						} else {
							processTemplateType(templateName);
						}
					}
				} else {
					setErrorMessage("Please choose a template.");
				}
			}
		});
		final Label spaceLabel = new Label(cmposite, SWT.NONE);
		spaceLabel.setText("             ");

		this.snippetDesc = new Text(cmposite, SWT.BORDER);
		this.snippetDesc.setLayoutData(new GridData(300, 15));
		this.snippetDesc.setEditable(false);
	}

	/**
	 * @param snippet
	 */
	protected void processTemplateType(final String templateName) {
		this.createSnippetData.setTemplateType(this.createSnippetData.getTemplatePrefix() + UNDERSCORE + templateName);
		this.createSnippetData.setDescription(makeWord(this.createSnippetData.getTemplateType()));

		if (this.createSnippetData.getTemplateSettings() == null) {
			this.templateSettings = getTemplateSettings(this.createSnippetData.getTemplateType());
			this.createSnippetData.setTemplateSettings(CreateSnippetDialog.this.templateSettings);
		} else {
			this.templateSettings = this.createSnippetData.getTemplateSettings();
		}
		final String templatePrefix = this.createSnippetData.getTemplatePrefix();
		if (this.createSnippetData.getTemplateSettings().getTemplateVariations() != null) {
			this.templateVariationLabel.setVisible(true);
			this.templateVariationList.removeAll();
			this.templateVariationCombo.removeAll();
			for (final String variation : this.createSnippetData.getTemplateSettings().getTemplateVariations()) {
				this.templateVariationLabel.setVisible(true);
				if (this.templateSettings.isAllowMultipleVariation()) {
					this.templateVariationList.add(variation);
					this.templateVariationList.setEnabled(true);
					this.templateVariationList.setVisible(true);
					this.templateVariationCombo.setEnabled(false);
					this.templateVariationCombo.setVisible(false);
				} else {
					this.templateVariationCombo.add(variation);
					this.templateVariationCombo.setEnabled(true);
					this.templateVariationCombo.setVisible(true);
					this.templateVariationList.setEnabled(false);
					this.templateVariationList.setVisible(false);
				}
			}
			if (this.createSnippetData.getTemplateSettings().getTemplateVariations().length == 1) {
				this.templateVariationCombo.select(0);
				this.templateVariationCombo.setEnabled(false);
				this.templateVariationCombo.setVisible(true);
				this.createSnippetData.setVariationsSelected(new String[] { this.templateVariationCombo.getItem(0) });
				this.templateVariationList.setEnabled(false);
				this.templateVariationList.setVisible(false);
			}
		} else {
			this.templateVariationLabel.setVisible(false);
			this.templateVariationCombo.setEnabled(false);
			this.templateVariationCombo.setVisible(false);
			this.templateVariationList.setEnabled(false);
			this.templateVariationList.setVisible(false);
			this.templateVariationCombo.removeAll();
			this.templateVariationList.removeAll();
		}

		/*this.createSnippetData.setRequiresClass(this.templateSettings.getFirstTemplateItem() == FIRST_TEMPLATE.Class);
		this.createSnippetData.setRequiresFile(this.templateSettings.getFirstTemplateItem() == FIRST_TEMPLATE.File);
		this.createSnippetData.setRequirePackage(this.templateSettings.getFirstTemplateItem() == FIRST_TEMPLATE.Package);
		this.createSnippetData.setRequireFolder(this.templateSettings.getFirstTemplateItem() == FIRST_TEMPLATE.Folder);*/

		populateSelectTypeCombo();
		if (templatePrefix.equals(TEMPLATE)) {
			this.projectLabel.setVisible(true);
			this.projectCombo.setEnabled(true);
			this.projectCombo.setVisible(true);
			switch (this.templateSettings.getFirstTemplateItem()) {

			case Class:
				final int numberofClasses = this.templateSettings.getNumberRequiredItems();
				if (numberofClasses > 0) {
					final java.util.List<String> clsNames = this.createSnippetData.getClassNames().get(numberofClasses);
					if (clsNames.size() == 1) {
						enableSelectType("Select Class:               ", "Browse Class", true, false);
						this.fromClassInstLabel.setVisible(true);
						this.fromClassInstCombo.setEnabled(true);
						this.fromClassInstCombo.setVisible(true);
						this.fromClassInstLabel.setText("Class Instance:           ");

					} else if (clsNames.size() == 2) {

						enableSelectType("Select From class:     ", "Browse From Class", true, true);
						this.fromClassInstLabel.setText("From Class Instance:   ");
					}
				} else {
					enableSelectType(EMPTY_STR, EMPTY_STR, false, false);
				}
				break;
			case File:
				enableSelectType("Select File:                   ", "Browse File", true, false);
				break;
			case Folder:
				enableSelectType("Select Folder:               ", "Browse Folder", true, false);
				break;
			case Package:
				enableSelectType("Select Package:             ", "Browse Package", true, false);
				break;
			case Enumeration:
				enableSelectType("Select Enumeration:         ", "Browse Enum", true, false);
				this.fromClassInstLabel.setVisible(true);
				this.fromClassInstCombo.setEnabled(true);
				this.fromClassInstCombo.setVisible(true);
				this.fromClassInstLabel.setText("Enumeration Instance:     ");
				break;
			case None:
				enableSelectType(EMPTY_STR, EMPTY_STR, false, false);
				break;
			}
		}
		//final String templatePrefix = this.createSnippetData.getTemplatePrefix();
		if (templatePrefix.equals(P_DATABASE_TEMPLATE_PREFIX)) {
			enableWhrSepQulfr(!(this.snippetCombo.getItem(this.snippetCombo.getSelectionIndex()).equals(
					DATABASE_TEMPLATE_INSERT_SIMPLE.replaceFirst("^" + templatePrefix + UNDERSCORE, EMPTY_STR))
					|| this.snippetCombo.getItem(this.snippetCombo.getSelectionIndex()).equals(
							DATABASE_TEMPLATE_INSERT_WITH_NAMED_PARAMETER.replaceFirst("^" + templatePrefix + UNDERSCORE, EMPTY_STR))
					|| this.snippetCombo.getItem(this.snippetCombo.getSelectionIndex()).equals(
							DATABASE_TEMPLATE_POJO_CLASS.replaceFirst("^" + templatePrefix + UNDERSCORE, EMPTY_STR))
					|| this.snippetCombo.getItem(this.snippetCombo.getSelectionIndex()).equals(
							DATABASE_TEMPLATE_ADD_FIELDS_TO_POJO_CLASS.replaceFirst("^" + templatePrefix + UNDERSCORE, EMPTY_STR))
					|| this.snippetCombo.getItem(this.snippetCombo.getSelectionIndex()).equals(
							DATABASE_TEMPLATE_SIMPLE_SNIPPET.replaceFirst("^" + templatePrefix + UNDERSCORE, EMPTY_STR))
					|| this.snippetCombo.getItem(this.snippetCombo.getSelectionIndex()).equals(
							DATABASE_TEMPLATE_POJO_CLASS_WITHOUT_ANNOTATION.replaceFirst("^" + templatePrefix + UNDERSCORE, EMPTY_STR)) || this.snippetCombo
					.getItem(this.snippetCombo.getSelectionIndex()).equals(
							DATABASE_TEMPLATE_ADD_FIELDS_TO_POJO_CLASS_WITHOUT_ANNOTATION.replaceFirst("^" + templatePrefix + UNDERSCORE,
									EMPTY_STR))));

			if (this.preferenceStore.getString(P_TEMPLATES_TO_ENABLE_POJO).contains(templateName)) {
				this.pojoClassBrowseButton.setEnabled(true);
				this.pojoClassCombo.setEnabled(true);
			} else {
				this.pojoClassBrowseButton.setEnabled(false);
				this.pojoClassCombo.setEnabled(false);
			}
			if (this.createSnippetData.getTemplateType().equals(DATABASE_TEMPLATE_SELECT_SIMPLE)
					|| this.createSnippetData.getTemplateType().equals(DATABASE_TEMPLATE_SELECT_SQL_QUERY)) {
				// this.orderByButton.setEnabled(true);
				// this.groupByButton.setEnabled(true);
				this.useAliasNameButton.setEnabled(true);
			} else {
				// this.orderByButton.setEnabled(false);
				// this.groupByButton.setEnabled(false);
				this.useAliasNameButton.setEnabled(false);
			}
		}

		this.snippetDesc.setText(this.createSnippetData.getTemplateSettings().getTemplateDescription());
		// final IPreferenceStore preferenceStore = new
		// ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		if (this.createSnippetData.getTemplateSettings() != null
				&& this.preferenceStore.getBoolean(P_GLOBAL_ENABLE_TEMPLATE_BODY_IN_DIALOG_BOXES)) {
			this.templateBodyMultiText.setEnabled(true, getMultiTextParent());
			this.templateBodyMultiText.setText(this.createSnippetData.getTemplateSettings().getTemplateBody());
		} /*
			* else { this.templateBodyMultiText.setEnabled(false); }
			*/
	}

	/**
	 * @param labelText
	 * @param browseText
	 * @param enableSelectType
	 * @param disableToClass
	 */
	private void enableSelectType(final String labelText, final String browseText, final boolean enableSelectType,
			final boolean disableToClass) {
		this.selectTypeLabel.setVisible(enableSelectType);
		this.selectTypeLabel.setText(labelText);
		this.selectTypeCombo.setEnabled(enableSelectType);
		this.selectTypeCombo.setVisible(enableSelectType);
		this.browseButton.setText(browseText);
		this.browseButton.setEnabled(enableSelectType);
		this.browseButton.setVisible(enableSelectType);
		this.fromClassInstLabel.setVisible(disableToClass);
		this.fromClassInstCombo.setVisible(disableToClass);
		this.fromClassInstCombo.setEnabled(disableToClass);
		this.toClassLabel.setVisible(disableToClass);
		this.toClassNameCombo.setVisible(disableToClass);
		this.toClassNameCombo.setEnabled(disableToClass);
		this.browseToClass.setVisible(disableToClass);
		this.browseToClass.setEnabled(disableToClass);
		this.toClassInstLabel.setVisible(disableToClass);
		this.toClassInstCombo.setVisible(disableToClass);
		this.toClassInstCombo.setEnabled(disableToClass);
	}

	/**
	 * @param enable
	 */
	private void enableWhrSepQulfr(final boolean enable) {
		/*
		 * this.equalButton.setEnabled(enable);
		 * this.notEqualButton.setEnabled(enable);
		 * this.likeButton.setEnabled(enable);
		 * this.notLikeButton.setEnabled(enable);
		 */
		this.andButton.setEnabled(enable);
		this.orButton.setEnabled(enable);
	}

	private void enableGroupByOrderBy(final boolean enable) {
		this.groupByButton.setEnabled(enable);
		this.orderByButton.setEnabled(enable);
	}

	/**
	 * @param templateType
	 * @return
	 */
	private TemplateSettings getTemplateSettings(final String templateType) {
		if (isEmpty(templateType)) {
			setErrorMessage(this.templateSettigsNull);
			return null;
		}
		return TemplateSettings.getTemplateSettings(templateType, this.createSnippetData.getTemplatePrefix());
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
		label.setText("Where Separator:        ");
		this.andButton = new Button(composite, SWT.RADIO);
		this.andButton.setText("and");
		this.andButton.setSelection(true);
		this.createSnippetData.setWhereClauseSeparator(WHERE_CLAUSE_SEPARATOR.AND);
		this.andButton.setEnabled(false);
		this.andButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.widget).getSelection()) {
					CreateSnippetDialog.this.createSnippetData.setWhereClauseSeparator(WHERE_CLAUSE_SEPARATOR.AND);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
		this.orButton = new Button(composite, SWT.RADIO);
		this.orButton.setText("or");
		this.orButton.setEnabled(false);
		this.orButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				if (((Button) event.widget).getSelection()) {
					CreateSnippetDialog.this.createSnippetData.setWhereClauseSeparator(WHERE_CLAUSE_SEPARATOR.OR);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
	}

	@Override
	protected void okPressed() {
		if (this.createSnippetData == null) {
			this.createSnippetData = new CreateSnippetData();
		}

		if (this.projectCombo != null && this.projectCombo.getText() != null) {
			if (!isPrjInSync(this.prjMap.get(this.projectCombo.getText()))) {
				return;
			}
		}

		if (this.snippetCombo != null && this.snippetCombo.isEnabled() && this.snippetCombo.getSelectionIndex() == -1) {
			setErrorMessage("Please choose a template.");
			return;
		}

		if (this.templateVariationCombo != null && this.templateVariationCombo.isEnabled()
				&& this.templateVariationCombo.getSelectionIndex() == -1) {
			setErrorMessage("Please choose a template variation.");
			return;
		}

		if (this.templateVariationList != null && this.templateVariationList.isEnabled()
				&& this.templateVariationList.getSelectionIndex() == -1) {
			setErrorMessage("Please choose a template variation.");
			return;
		}

		if (this.tableCombo != null && this.tableCombo.isEnabled() && this.tableCombo.getSelectionIndex() == -1) {
			if (isEmpty(this.tableCombo.getText())) {
				setErrorMessage("Please choose a table.");
				return;
			}
		}

		if (this.selectTypeCombo != null && this.selectTypeCombo.isEnabled() && isEmpty(this.selectTypeCombo.getText())) {
			if (this.templateSettings.getFirstTemplateItem().equals(FIRST_TEMPLATE.Class)) {
				setErrorMessage("Please choose a class.");
			} else if (this.templateSettings.getFirstTemplateItem().equals(FIRST_TEMPLATE.File)) {
				setErrorMessage("Please choose a file.");
			} else if (this.templateSettings.getFirstTemplateItem().equals(FIRST_TEMPLATE.Folder)) {
				setErrorMessage("Please choose a folder.");
			} else if (this.templateSettings.getFirstTemplateItem().equals(FIRST_TEMPLATE.Package)) {
				setErrorMessage("Please choose a package.");
			} else if (this.templateSettings.getFirstTemplateItem().equals(FIRST_TEMPLATE.Enumeration)) {
				setErrorMessage("Please choose an enumeration.");
			}
			return;
		} else if (this.selectTypeCombo != null && this.selectTypeCombo.isEnabled() && !isEmpty(this.selectTypeCombo.getText())) {
			setErrorMessage(this.defaultMessage);
			setFastCodeCacheWithSelectedType();
		}
		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (this.toClassNameCombo != null && this.toClassNameCombo.isEnabled() && !isEmpty(this.toClassNameCombo.getText())) {
			if (this.createSnippetData.getToClass() != null) {
				try {
					final IType selectedToClassType = this.createSnippetData.getToClass();
					if (!fastCodeCache.getTypeSet().contains(selectedToClassType)) {
						fastCodeCache.getTypeSet().add(selectedToClassType);
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		if (this.showLocalVariable != null && !this.showLocalVariable.getSelection() && this.selectTypeCombo != null
				&& this.selectTypeCombo.isEnabled() && isEmpty(this.selectTypeCombo.getText())) {
			setErrorMessage("Please choose a class.");
			return;
		}

		if (this.toClassNameCombo != null && this.toClassNameCombo.isEnabled() && isEmpty(this.toClassNameCombo.getText())) {
			setErrorMessage("Please choose a To class.");
			return;
		}

		if (this.templateBodyMultiText != null) {
			this.modifiedTemplateBody = this.templateBodyMultiText.getStringValue();
			if (this.modifiedTemplateBody != null) {
				this.createSnippetData.setTemplateBodyFromSnippetDialog(this.modifiedTemplateBody);
			} else {
				this.createSnippetData.setTemplateBodyFromSnippetDialog(this.createSnippetData.getTemplateSettings().getTemplateBody());
			}
		}
		if (this.preferenceStore.getString(P_TEMPLATES_TO_ENABLE_POJO).contains(
				this.createSnippetData.getTemplateType().substring(this.createSnippetData.getTemplatePrefix().length() + 1))
				&& !this.createSnippetData.getTemplateType().equals(DATABASE_TEMPLATE_SELECT_SIMPLE)) {
			if (this.pojoClassCombo != null && this.pojoClassCombo.isEnabled() && isEmpty(this.pojoClassCombo.getText())) {
				setErrorMessage("Please choose a pojo class.");
				return;
			}
		}
		if (this.pojoClassCombo != null && this.pojoClassCombo.isEnabled()) {
			if (!isEmpty(this.pojoClassCombo.getText())) {
				if (this.createSnippetData.getiSelectPojoClassType() != null) {
					try {
						final IType selectedPojoClassType = this.createSnippetData.getiSelectPojoClassType();
						if (!fastCodeCache.getTypeSet().contains(selectedPojoClassType)) {
							fastCodeCache.getTypeSet().add(selectedPojoClassType);
						}
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		/*if (this.createSnippetData.getTemplateType().equals(TEMPLATE_CREATE_FILE_WITH_SELECTED_CONTENT)) {
			if (isEmpty(this.createSnippetData.getSelectedText())) {
				setErrorMessage("Please select some text in editor and try again");
				return;
			}
		}*/

		if (this.createSnippetData.getSelectedProject() == null) {
			if (this.projectCombo != null && this.projectCombo.getText() != null) {
				this.createSnippetData.setSelectedProject(new FastCodeProject(this.prjMap.get(this.projectCombo.getText())));
			}
		}
		/*if (connection != null) {
			try {
				connection.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}*/
		super.okPressed();
	}

	public CreateSnippetData getCreateSnippetData() {
		return this.createSnippetData;
	}

	/**
	 *
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

	/*	*//**
			* @param templateSetting
			* @return
			*/
	/*
	private boolean requireClasses(final TemplateSettings templateSetting) {
	if (templateSetting.getFirstTemplateItem() == FIRST_TEMPLATE.Class) {
		return true;
	} else if (templateSetting.getFirstTemplateItem() == FIRST_TEMPLATE.File) {
		return false;
	}
	return false;

	}

	*//**
		* @param templateSetting
		* @return
		*/
	/*
	private boolean requireFiles(final TemplateSettings templateSetting) {
	if (templateSetting.getFirstTemplateItem() == FIRST_TEMPLATE.Class) {
		return false;
	} else if (templateSetting.getFirstTemplateItem() == FIRST_TEMPLATE.File) {
		return true;
	}
	return false;

	}

	*//**
		* @param property
		* @param defaultValue
		* @return
		*/
	/*
	private boolean getBooleanPropertyValue(final String property, final String defaultValue) {
	final GlobalSettings globalSettings = GlobalSettings.getInstance();
	final String value = globalSettings.getPropertyValue(property, defaultValue);
	return value.equals("true");
	}*/

	/**
	 * @param parent
	 */
	private void createShowLocaVarButton(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);
		this.showLocalVariable = new Button(composite, SWT.CHECK);
		this.showLocalVariable.setText("Include local variables");
		this.showLocalVariable.setEnabled(true);
		this.showLocalVariable.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (CreateSnippetDialog.this.showLocalVariable.getSelection()) {
					CreateSnippetDialog.this.createSnippetData.setShowLocalVriable(true);
				} else {
					CreateSnippetDialog.this.createSnippetData.setShowLocalVriable(false);
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
		});
	}

	/**
	 * @param selectedClassname
	 */
	private void setInstanceNameCombo(String selectedClassname) {
		final int offset = selectedClassname.lastIndexOf(DOT);
		CreateSnippetDialog.this.fromClassInstCombo.removeAll();
		final String defaultInstance = createDefaultInstance(selectedClassname);
		CreateSnippetDialog.this.fromClassInstCombo.add(defaultInstance);
		final String template = CreateSnippetDialog.this.snippetCombo.getText();
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String templatesApplForlocalVar = globalSettings.getPropertyValue("TEMPLATES_APPL_FOR_LOCALVAR", EMPTY_STR).trim();
		if (templatesApplForlocalVar.contains(template)) {
			if (offset != -1) {
				selectedClassname = selectedClassname.substring(offset + 1);
				final java.util.List<FastCodeReturn> list = this.createSnippetData.getLocalVariables();
				for (final FastCodeReturn fastCodeReturn : getEmptyListForNull(list)) {
					if (fastCodeReturn.getType().getName().equals(selectedClassname)) {
						if (!defaultInstance.equals(fastCodeReturn.getName())) {

							CreateSnippetDialog.this.fromClassInstCombo.add(fastCodeReturn.getName());
						}
					}
				}
			}
		}
		CreateSnippetDialog.this.fromClassInstCombo.select(0);

	}

	/**
	 *
	 */
	private void populateSelectTypeCombo() {
		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		final IEditorPart editorPart = CreateSnippetDialog.this.createSnippetData.getEditorpart();
		ICompilationUnit compUnit = null;
		if (editorPart != null) {
			compUnit = getCompilationUnitFromEditor(editorPart);
		}

		if (this.selectTypeCombo != null) {
			this.selectTypeCombo.removeAll();
		}
		switch (this.templateSettings.getFirstTemplateItem()) {
		case Class:
			if (editorPart != null) {
				if (compUnit != null) {
					this.currentType = compUnit.getPrimary().findPrimaryType().getFullyQualifiedName();
					this.selectTypeCombo.add(ENCLOSING_CLASS_STR + HYPHEN + this.currentType);
					this.createSnippetData.setFromClass(compUnit.getPrimary().findPrimaryType());
					if (!isEmpty(this.fromClassInstCombo.getText())) {
						this.fromClassInstCombo.removeAll();
					}
					//this.selectTypeCombo.select(0);
					//setInstanceNameCombo(this.currentType);
				}
			}
			if (!fastCodeCache.getTypeSet().isEmpty()) {
				for (final IType type : fastCodeCache.getTypeSet()) {
					try {
						if (this.templateSettings.getSecondTemplateItem().equals(SECOND_TEMPLATE.field) && type.isEnum()) {
							continue;
						}
					} catch (final Exception ex) {
						CreateSnippetDialog.this.setErrorMessage(ex.getMessage());
					}
					if (!isEmpty(this.currentType) && this.currentType.equals(type.getFullyQualifiedName())) {
						continue;
					}

					boolean addItem = true;
					if (this.selectTypeCombo.getItems() != null) {
						for (final String existingClass : this.selectTypeCombo.getItems()) {
							if (existingClass.contains(ENCLOSING_CLASS_STR)) {
								continue;
							}
							if (existingClass.equals(type.getFullyQualifiedName())) {
								addItem = false;
								break;
							}

						}
						if (addItem) {
							this.selectTypeCombo.add(type.getFullyQualifiedName());
						}
					}
				}
			}

			break;
		case File:
			if (editorPart != null) {
				if (compUnit == null) {
					final IFile file = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);
					this.currentType = file.getFullPath().toString();
					this.selectTypeCombo.add(ENCLOSING_FILE_STR + HYPHEN + this.currentType);
					this.createSnippetData.setResourceFile(file);
					//this.createSnippetData.getFastCodeFiles().add(new FastCodeFile(file));
					//this.selectTypeCombo.select(0);
				}
			}
			if (!fastCodeCache.getFileSet().isEmpty()) {
				for (final IFile file : fastCodeCache.getFileSet()) {

					if (!isEmpty(this.currentType) && this.currentType.equals(file.getFullPath().toString())) {
						continue;
					}
					boolean addItem = true;
					if (this.selectTypeCombo.getItems() != null) {
						for (final String existingFile : this.selectTypeCombo.getItems()) {
							if (existingFile.contains(ENCLOSING_FILE_STR)) {
								continue;
							}
							if (existingFile.equals(file.getFullPath().toString())) {
								addItem = false;
								break;

							}
						}
						if (addItem) {
							this.selectTypeCombo.add(file.getFullPath().toString());
						}
					}
				}
			}
			break;
		case Folder:
			if (editorPart != null) {
				if (compUnit == null) {
					final IFile file = (IFile) editorPart.getEditorInput().getAdapter(IFile.class);

					try {
						if (file != null) {
							final String srcPath = file
									.getProjectRelativePath()
									.toString()
									.substring(
											0,
											file.getProjectRelativePath().toString().indexOf(file.getProjectRelativePath().lastSegment()) - 1);

							if (this.createSnippetData.getJavaProject() == null) {
								this.createSnippetData.setJavaProject(JavaCore.create(file.getProject()));
							}
							final IFolder folder = this.createSnippetData.getJavaProject().getProject().getFolder(srcPath);
							if (folder != null) {
								this.currentType = folder.getFullPath().toString();
								this.selectTypeCombo.add(ENCLOSING_FOLDER_STR + HYPHEN + this.currentType);
								this.createSnippetData.setFolder(folder);
								//this.selectTypeCombo.select(0);
							}
						}

					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
			}
			if (!fastCodeCache.getFolderSet().isEmpty()) {
				for (final IFolder folder : fastCodeCache.getFolderSet()) {
					if (!isEmpty(this.currentType) && this.currentType.equals(folder.getFullPath().toString())) {
						continue;
					}
					boolean addItem = true;
					if (this.selectTypeCombo.getItems() != null) {
						for (final String existingFolder : this.selectTypeCombo.getItems()) {
							if (existingFolder.contains(ENCLOSING_FOLDER_STR)) {
								continue;
							}
							if (existingFolder.equals(folder.getFullPath().toString())) {
								addItem = false;
								break;
							}
						}
						if (addItem) {
							this.selectTypeCombo.add(folder.getFullPath().toString());
						}
					}
				}
			}
			break;
		case Package:
			if (editorPart != null) {
				if (compUnit != null) {
					final IPackageFragment packageFragment = compUnit.getPrimary().findPrimaryType().getPackageFragment();
					this.currentType = getAlteredPackageName(packageFragment);
					this.selectTypeCombo.add(ENCLOSING_PACKAGE_STR + HYPHEN + this.currentType);
					this.createSnippetData.setPackageFragment(packageFragment);
					//this.selectTypeCombo.select(0);
				}
			}
			if (!fastCodeCache.getPackageSet().isEmpty()) {
				for (final IPackageFragment pkgFrgmt : fastCodeCache.getPackageSet()) {
					if (!isEmpty(this.currentType) && this.currentType.equals(getAlteredPackageName(pkgFrgmt))) {
						continue;
					}
					boolean addItem = true;
					if (this.selectTypeCombo.getItems() != null) {
						for (final String existingPkg : this.selectTypeCombo.getItems()) {
							if (existingPkg.contains(ENCLOSING_PACKAGE_STR)) {
								continue;
							}
							if (existingPkg.equals(getAlteredPackageName(pkgFrgmt))) {
								addItem = false;
								break;

							}
						}
						if (addItem) {
							this.selectTypeCombo.add(getAlteredPackageName(pkgFrgmt));
						}
					}
				}
			}
			break;
		case Enumeration:
			if (!fastCodeCache.getTypeSet().isEmpty()) {
				for (final IType type : fastCodeCache.getTypeSet()) {

					try {
						if (type.isEnum()) {
							this.selectTypeCombo.add(type.getFullyQualifiedName());
						}
					} catch (final Exception ex) {
						CreateSnippetDialog.this.setErrorMessage(ex.getMessage());
					}
				}
			}
			break;
		case None:
			break;
		default:
			break;
		}
	}

	/**
	 * @param composite
	 */
	private void setMultiTextParent(final Composite composite) {
		this.multitextparent = composite;

	}

	/**
	 * @return
	 */
	private Composite getMultiTextParent() {
		return this.multitextparent;

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
		schemaLabel.setText("Schema:                        ");
		schemaLabel.setLayoutData(new GridData());

		this.schemaCombo = new Combo(cmposite, SWT.DROP_DOWN);

		this.schemaCombo.setLayoutData(new GridData(150, 100));
		/*int schemaIndex = 0;
		int k = 0;
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		String defaultSchema = databaseConnectionSettings.getTypesofDabases().equalsIgnoreCase(ORACLE) ? databaseConnectionSettings
				.getUserName() : databaseConnectionSettings.getNameofDabase();

		for (final String schemaName : this.createSnippetData.getSchemasInDB().toArray(new String[0])) {
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
			getTableFromDb(connection, CreateSnippetDialog.this.schemaCombo.getText(), databaseType);
			CreateSnippetDialog.this.tableCombo.removeAll();
			final DatabaseCache databaseCache = DatabaseCache.getInstance();
			for (final String tableName : databaseCache.getDbTableListMap().get(CreateSnippetDialog.this.schemaCombo.getText())
					.toArray(new String[0])) {
				CreateSnippetDialog.this.tableCombo.add(tableName);
			}
			CreateSnippetDialog.this.createSnippetData.setSchemaSelected(CreateSnippetDialog.this.schemaCombo.getText());
			final FastCodeContentProposalProvider provider = new FastCodeContentProposalProvider(
					CreateSnippetDialog.this.tableCombo.getItems());
			final ComboContentAdapter comboAdapter = new ComboContentAdapter();
			final ContentProposalAdapter adapter = new ContentProposalAdapter(CreateSnippetDialog.this.tableCombo, comboAdapter, provider,
					null, null);
			adapter.setPropagateKeys(true);
			adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
			CreateSnippetDialog.this.createSnippetData.setTablesInDB(databaseCache.getDbTableListMap().get(
					CreateSnippetDialog.this.schemaCombo.getText()));
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
		label.setText("Use Alias name:           ");
		this.useAliasNameButton = new Button(composite, SWT.CHECK);
		this.useAliasNameButton.setSelection(false);

		this.useAliasNameButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				CreateSnippetDialog.this.createSnippetData.setUseAliasName(CreateSnippetDialog.this.useAliasNameButton.getSelection());

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});

	}

	/**
	 * @param editorPart
	 * @return
	 */
	protected ICompilationUnit getCompilationUnitFromEditor(final IEditorPart editorPart) {
		final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();
		final ICompilationUnit compilationUnit = manager.getWorkingCopy(editorPart.getEditorInput());
		return compilationUnit != null && compilationUnit.exists() ? compilationUnit : null;
	}

	private void setFastCodeCacheWithSelectedType() {
		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		switch (this.templateSettings.getFirstTemplateItem()) {
		case Class:
			if (this.createSnippetData.getFromClass() != null) {
				final IType fromClassType = this.createSnippetData.getFromClass();
				if (fromClassType != null) {
					if (!fastCodeCache.getTypeSet().contains(fromClassType)) {
						fastCodeCache.getTypeSet().add(fromClassType);
					}
				}
			}
			break;
		case File:
			if (this.createSnippetData.getResourceFile() != null) {

				final IFile selectedFile = this.createSnippetData.getResourceFile();
				if (selectedFile != null) {
					if (!fastCodeCache.getFileSet().contains(selectedFile)) {
						fastCodeCache.getFileSet().add(selectedFile);
					}
				}
			}
			break;
		case Folder:
			if (this.createSnippetData.getFolder() != null) {
				final IFolder folder = this.createSnippetData.getFolder();
				if (folder != null && folder.exists()) {
					if (!fastCodeCache.getFolderSet().contains(folder)) {
						fastCodeCache.getFolderSet().add(folder);
					}
				}
			}
			break;
		case Package:
			if (this.createSnippetData.getPackageFragment() != null) {
				final IPackageFragment packageFragment = this.createSnippetData.getPackageFragment();
				if (packageFragment != null) {
					if (!fastCodeCache.getPackageSet().contains(packageFragment)) {
						fastCodeCache.getPackageSet().add(packageFragment);

					}
				}
			}
			break;

		}

	}

	/**
	 *
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Create snippet");

		shell.setFullScreen(true);
		final CreateSnippetDialog createSnippetDialog = new CreateSnippetDialog(shell);

		createSnippetDialog.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}

package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.ASTERISK_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.PROJECT_URL_DELIMITER;
import static org.fastcode.common.FastCodeConstants.PROJECT_URL_PAIR_DELIMITER;
import static org.fastcode.preferences.PreferenceConstants.P_CHECK_IN;
import static org.fastcode.preferences.PreferenceConstants.P_COMMENTS_FOOTER;
import static org.fastcode.preferences.PreferenceConstants.P_COMMENTS_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.P_ENABLE_AUTO_CHECKIN;
import static org.fastcode.preferences.PreferenceConstants.P_ENABLE_TRACK_ECLIPSE_CHANGE;
import static org.fastcode.preferences.PreferenceConstants.P_EXCLUDE_DIR;
import static org.fastcode.preferences.PreferenceConstants.P_REPOSIROTY_PROJECT_URL_PAIR;
import static org.fastcode.preferences.PreferenceConstants.P_REPOSITORY_NAME;
import static org.fastcode.preferences.PreferenceConstants.P_REPOSITORY_PASSWORD;
import static org.fastcode.preferences.PreferenceConstants.P_REPOSITORY_PROJECT;
import static org.fastcode.preferences.PreferenceConstants.P_REPOSITORY_URL;
import static org.fastcode.preferences.PreferenceConstants.P_REPOSITORY_USERNAME;
import static org.fastcode.preferences.PreferenceConstants.P_TIME_GAP_BEFORE_CHECK_IN;
import static org.fastcode.util.FastCodeUtil.getEmptyArrayForNull;
import static org.fastcode.util.FastCodeUtil.getEmptyListForNull;
import static org.fastcode.util.StringUtil.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.services.IEvaluationService;
import org.fastcode.common.RepositoryData;
import org.fastcode.dialog.RepositoryDialog;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.util.MultiStringFieldEditor;

public class VersionControlPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private ComboFieldEditor			repositoryName;
	//private StringFieldEditor			repositoryBaseLocation;
	private static final String[][]		REPOSITORY_NAMES	= { { "SVN", "SVN" } };													//, { "CVS", "CVS" } };
	private RadioGroupFieldEditor		checkIn;
	private static final String[][]		CHECK_IN_TYPES		= { { "Check In Automatically", "checkIn" },
			{ "Ask Before Check In", "askBeforeCheckIn" }, { "Do Not Check In", "donotCheckIn" } };
	private StringFieldEditor			prefixComments;
	private MultiStringFieldEditor		footerComments;
	private final IPreferenceStore		preferenceStore;
	private StringFieldEditor			userName;
	private StringFieldEditor			passWord;
	private IntegerFieldEditor			timeGapBeforeAutoCheckIn;
	BooleanFieldEditor					enableAutoCheckin;

	private String						currentValueOfRepoName;
	private String						currentValueOfRepoBaseLocation;
	private String						currentValueOfUserName;
	private String						currentValueOfPassWord;
	TableViewer							prjUrlViewer;
	private Table						table;
	private Button						addButton;
	private Button						editButton;
	private Button						removeButton;
	private BooleanFieldEditor			enableResChangeListener;
	private StringFieldEditor			excludeDir;
	protected String	currentValueOfProject;
	protected String	currentValueOfRepoUrl;

	public VersionControlPreferencePage() {
		super(GRID);
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		setPreferenceStore(this.preferenceStore);
		setDescription("Version Control");
	}

	@Override
	protected void createFieldEditors() {
		this.enableAutoCheckin = new BooleanFieldEditor(P_ENABLE_AUTO_CHECKIN, "Enable Auto Checkin", getFieldEditorParent());
		addField(this.enableAutoCheckin);
		this.enableResChangeListener = new BooleanFieldEditor(P_ENABLE_TRACK_ECLIPSE_CHANGE, "Enable Track and Check in Eclipse Changes",
				getFieldEditorParent());
		addField(this.enableResChangeListener);
		this.repositoryName = new ComboFieldEditor(P_REPOSITORY_NAME, "Repository name:  ", REPOSITORY_NAMES, getFieldEditorParent());
		addField(this.repositoryName);
		//uncomment this after enabling browse button in Repository Dialog
		/*this.repositoryBaseLocation = new StringFieldEditor(P_REPOSITORY_BASE_LOCATION, "Repository Base URL:  ", getFieldEditorParent());
		this.repositoryBaseLocation.setEmptyStringAllowed(false);
		addField(this.repositoryBaseLocation);*/

		//this.repositoryUrl = new StringFieldEditor(P_REPOSITORY_URL, "Repository Url", getFieldEditorParent());
		/*this.repositoryUrl = new StringButtonFieldEditor(P_REPOSITORY_URL, "Repository Url:  ", getFieldEditorParent()) {
			@Override
			protected String changePressed() {
				try {
					final String url = VersionControlPreferencePage.this.currentValueOfRepoLoc;
					final RepositoryService repositoryService = getRepositoryServiceClass();
					final List<String> projects = repositoryService.getSharedProjects(url);
					final StringSelectionDialog selectionDialog = new StringSelectionDialog(new Shell(), "Select Project", "Select Project", projects.toArray(new String[0]), false);
					if (selectionDialog.open() == Window.CANCEL) {
						return null;

					} else {
						final String repository = (String) selectionDialog.getResult()[0];
						return url + FORWARD_SLASH + repository;
					}
				} catch (final FastCodeRepositoryException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				return null;
			}
		};
		addField(this.repositoryUrl);*/
		this.userName = new StringFieldEditor(P_REPOSITORY_USERNAME, "UserName:", getFieldEditorParent());
		addField(this.userName);
		this.passWord = new StringFieldEditor(P_REPOSITORY_PASSWORD, "Password:", getFieldEditorParent()) {
			@Override
			protected void doFillIntoGrid(final Composite parent, final int numColumns) {
				super.doFillIntoGrid(parent, numColumns);

				getTextControl().setEchoChar(ASTERISK_CHAR);
			}
		};
		addField(this.passWord);

		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String prj_url_delim = globalSettings.getPropertyValue(PROJECT_URL_DELIMITER.toUpperCase(), EMPTY_STR);
		final String prj_url_pair_delim = globalSettings.getPropertyValue(PROJECT_URL_PAIR_DELIMITER.toUpperCase(), EMPTY_STR);

		final Composite parent = new Composite(getFieldEditorParent(), SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		final GridData gd1 = new GridData(GridData.FILL_BOTH);
		gd1.horizontalSpan = 2;
		parent.setLayoutData(gd1);
		parent.setLayout(layout);

		final Composite innerParent = new Composite(parent, SWT.NONE);
		final GridLayout innerLayout = new GridLayout();
		innerLayout.numColumns = 2;
		innerLayout.marginHeight = 0;
		innerLayout.marginWidth = 0;
		innerParent.setLayout(innerLayout);
		final GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		innerParent.setLayoutData(gd);

		final Composite tableComposite = new Composite(innerParent, SWT.NONE);
		final GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 450;
		data.heightHint = convertHeightInCharsToPixels(10);
		tableComposite.setLayoutData(data);

		final TableColumnLayout columnLayout = new TableColumnLayout();
		tableComposite.setLayout(columnLayout);

		this.prjUrlViewer = new TableViewer(tableComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		this.table = this.prjUrlViewer.getTable();

		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);
		//this.table.setSize(500, 200);

		final GC gc = new GC(getShell());
		gc.setFont(JFaceResources.getDialogFont());


		final TableViewerColumn viewerColumn1 = new TableViewerColumn(this.prjUrlViewer, SWT.NONE);
		final TableColumn column1 = viewerColumn1.getColumn();
		column1.setText("Project Name");
		final int minWidth = computeMinimumColumnWidth(gc, "Project Name");
		columnLayout.setColumnData(column1, new ColumnWeightData(12, minWidth, true));


		final TableViewerColumn viewerColumn2 = new TableViewerColumn(this.prjUrlViewer, SWT.NONE);
		final TableColumn column2 = viewerColumn2.getColumn();
		column2.setText("URL");
		final int minWidth1 = computeMinimumColumnWidth(gc, "URL");
		columnLayout.setColumnData(column2, new ColumnWeightData(24, minWidth1, true));


		this.prjUrlViewer.setLabelProvider(new ProjectUrlLabelProvider());
		this.prjUrlViewer.setContentProvider(new ProjectUrlContentProvider());

		this.prjUrlViewer.setInput(getEmptyListForNull(loadData()));

		final Composite buttons = new Composite(innerParent, SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		final GridLayout buttonLayout = new GridLayout();
		buttonLayout.marginHeight = 0;
		buttonLayout.marginWidth = 0;
		buttons.setLayout(buttonLayout);

		this.addButton = new Button(buttons, SWT.PUSH);
		this.addButton.setText("Add");
		if (isEmpty(this.preferenceStore.getString(P_REPOSITORY_PASSWORD)) || isEmpty(this.preferenceStore.getString(P_REPOSITORY_USERNAME))) {
			this.addButton.setEnabled(false);
		}
		this.addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.addButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				final String prjUrlPairs = VersionControlPreferencePage.this.preferenceStore.getString(P_REPOSIROTY_PROJECT_URL_PAIR);
				final RepositoryData repositoryData = new RepositoryData();
				/*String repBaseLoc = VersionControlPreferencePage.this.currentValueOfRepoBaseLocation;
				if (isEmpty(repBaseLoc)) {
					repBaseLoc = VersionControlPreferencePage.this.preferenceStore.getString(P_REPOSITORY_BASE_LOCATION);
					if (isEmpty(repBaseLoc)) {
						//openWarning(getShell(), "Please enter value for URL", "Please enter value for Repository URL");
						//VersionControlPreferencePage.this.editButton.setEnabled(false);
					}
				}

				repositoryData.setRepBaseLocation(repBaseLoc);*/ //VersionControlPreferencePage.this.currentValueOfRepoName.toLowerCase() + COLON + FORWARD_SLASH + FORWARD_SLASH + LOCAL_HOST); //
				repositoryData.setPrjUrlMap(getPrjUrlPairMap(prjUrlPairs));
				String userName = VersionControlPreferencePage.this.currentValueOfUserName;
				if (isEmpty(userName)) {
					userName = VersionControlPreferencePage.this.preferenceStore.getString(P_REPOSITORY_USERNAME);
					if (isEmpty(userName)) {
						/*openWarning(getShell(), "Please enter value for URL", "Please enter value for Repository URL");
						VersionControlPreferencePage.this.editButton.setEnabled(false);*/
					}
				}
				repositoryData.setUserName(userName);
				String password = VersionControlPreferencePage.this.currentValueOfPassWord;
				if (isEmpty(password)) {
					password = VersionControlPreferencePage.this.preferenceStore.getString(P_REPOSITORY_PASSWORD);
					if (isEmpty(password)) {
						/*openWarning(getShell(), "Please enter value for URL", "Please enter value for Repository URL");
						VersionControlPreferencePage.this.editButton.setEnabled(false);*/
					}
				}
				repositoryData.setPassword(password);
				repositoryData.setSource("Add");
				final RepositoryDialog repositoryDialog = new RepositoryDialog(getShell(), repositoryData);
				if (repositoryDialog.open() == Window.CANCEL) {
					return;
				}
				VersionControlPreferencePage.this.currentValueOfProject = repositoryData.getAssociatedProject();
				VersionControlPreferencePage.this.currentValueOfRepoUrl = repositoryData.getRepUrl();

				final String currPrjUrl = repositoryData.getAssociatedProject().trim() + prj_url_delim + repositoryData.getRepUrl().trim();
				VersionControlPreferencePage.this.preferenceStore.setValue(P_REPOSIROTY_PROJECT_URL_PAIR, isEmpty(prjUrlPairs) ? currPrjUrl
						: prjUrlPairs + prj_url_pair_delim + currPrjUrl);
				VersionControlPreferencePage.this.prjUrlViewer.setInput(getEmptyListForNull(loadData()));
				VersionControlPreferencePage.this.prjUrlViewer.refresh();

			}

		});

		this.editButton = new Button(buttons, SWT.PUSH);
		this.editButton.setText("Edit");
		this.editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		if (isEmpty(this.preferenceStore.getString(P_REPOSITORY_PASSWORD)) || isEmpty(this.preferenceStore.getString(P_REPOSITORY_USERNAME))) {
			this.editButton.setEnabled(false);
		}
		this.editButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event e) {

				final String prjUrlPairs = VersionControlPreferencePage.this.preferenceStore.getString(P_REPOSIROTY_PROJECT_URL_PAIR);
				//final RepositoryData repositoryData = new RepositoryData();

				final int index = VersionControlPreferencePage.this.table.getSelectionIndex();
				if (index > -1) {
					final RepositoryData repositoryData = (RepositoryData) VersionControlPreferencePage.this.prjUrlViewer
							.getElementAt(index);
					final String origPrjUrl = repositoryData.getAssociatedProject().trim() + prj_url_delim + repositoryData.getRepUrl().trim();
					/*String repLoc = VersionControlPreferencePage.this.currentValueOfRepoLoc;
					if (isEmpty(repLoc)) {
						repLoc = VersionControlPreferencePage.this.preferenceStore.getString(P_REPOSITORY_LOCATION);
						if (isEmpty(repLoc)) {
							openWarning(getShell(), "Please enter value for URL", "Please enter value for Repository URL");
							VersionControlPreferencePage.this.editButton.setEnabled(false);
						}
					}*/
					repositoryData.setRepBaseLocation(VersionControlPreferencePage.this.currentValueOfRepoBaseLocation); //VersionControlPreferencePage.this.currentValueOfRepoName.toLowerCase() + COLON + FORWARD_SLASH + FORWARD_SLASH + LOCAL_HOST); //
					repositoryData.setPrjUrlMap(getPrjUrlPairMap(prjUrlPairs));
					String userName = VersionControlPreferencePage.this.currentValueOfUserName;
					if (isEmpty(userName)) {
						userName = VersionControlPreferencePage.this.preferenceStore.getString(P_REPOSITORY_USERNAME);
						if (isEmpty(userName)) {
							/*openWarning(getShell(), "Please enter value for URL", "Please enter value for Repository URL");
							VersionControlPreferencePage.this.editButton.setEnabled(false);*/
						}
					}
					repositoryData.setUserName(userName);
					String password = VersionControlPreferencePage.this.currentValueOfPassWord;
					if (isEmpty(password)) {
						password = VersionControlPreferencePage.this.preferenceStore.getString(P_REPOSITORY_PASSWORD);
						if (isEmpty(password)) {
							/*openWarning(getShell(), "Please enter value for URL", "Please enter value for Repository URL");
							VersionControlPreferencePage.this.editButton.setEnabled(false);*/
						}
					}
					repositoryData.setPassword(password);
					repositoryData.setSource("Edit");
					final RepositoryDialog repositoryDialog = new RepositoryDialog(getShell(), repositoryData);
					if (repositoryDialog.open() == Window.CANCEL) {
						return;
					}
					VersionControlPreferencePage.this.currentValueOfProject = repositoryData.getAssociatedProject();
					VersionControlPreferencePage.this.currentValueOfRepoUrl = repositoryData.getRepUrl();
					final String currPrjUrl = repositoryData.getAssociatedProject().trim() + prj_url_delim + repositoryData.getRepUrl().trim();
					final String finalPrjUrlPairs = prjUrlPairs.replace(origPrjUrl, currPrjUrl);
					VersionControlPreferencePage.this.preferenceStore.setValue(P_REPOSIROTY_PROJECT_URL_PAIR, finalPrjUrlPairs);
					VersionControlPreferencePage.this.prjUrlViewer.setInput(getEmptyListForNull(loadData()));
					VersionControlPreferencePage.this.prjUrlViewer.refresh();
				}
			}

		});

		this.removeButton = new Button(buttons, SWT.PUSH);
		this.removeButton.setText("Remove");
		this.removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.removeButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				final int index = VersionControlPreferencePage.this.table.getSelectionIndex();
				System.out.println("index--" + index);
				if (index > -1) {
					final RepositoryData repositoryData = (RepositoryData) VersionControlPreferencePage.this.prjUrlViewer
							.getElementAt(index);
					System.out.println("repositoryData--" + repositoryData);
					System.out.println(repositoryData.getAssociatedProject());
					System.out.println(repositoryData.getRepUrl());
					final String prjUrlPairs = VersionControlPreferencePage.this.preferenceStore.getString(P_REPOSIROTY_PROJECT_URL_PAIR);
					System.out.println(prjUrlPairs);
					final String finalPrjUrlPairs = prjUrlPairs.replace(repositoryData.getAssociatedProject().trim() + prj_url_delim
							+ repositoryData.getRepUrl().trim(), EMPTY_STR);
					VersionControlPreferencePage.this.preferenceStore.setValue(P_REPOSIROTY_PROJECT_URL_PAIR, finalPrjUrlPairs);
					System.out.println("set to pref....");
					VersionControlPreferencePage.this.prjUrlViewer.setInput(getEmptyListForNull(loadData()));
					System.out.println("refresh done...");
					VersionControlPreferencePage.this.prjUrlViewer.refresh();
					System.out.println("remove -- done");

				}

			}

		});

		this.checkIn = new RadioGroupFieldEditor(P_CHECK_IN, "Check In:", CHECK_IN_TYPES.length, CHECK_IN_TYPES, getFieldEditorParent(),
				true);
		addField(this.checkIn);
		this.timeGapBeforeAutoCheckIn = new IntegerFieldEditor(P_TIME_GAP_BEFORE_CHECK_IN, "Time Gap Before Check In(in Min):",
				getFieldEditorParent());
		addField(this.timeGapBeforeAutoCheckIn);
		this.excludeDir = new StringFieldEditor(P_EXCLUDE_DIR, "Directories to be excluded:  ", getFieldEditorParent());
		addField(this.excludeDir);
		this.prefixComments = new StringFieldEditor(P_COMMENTS_PREFIX, "Prefix Comments:  ", getFieldEditorParent());
		addField(this.prefixComments);
		this.footerComments = new MultiStringFieldEditor(P_COMMENTS_FOOTER, "Footer Comments:   ", getFieldEditorParent(), false);
		addField(this.footerComments);

		this.currentValueOfRepoName = this.preferenceStore.getString(P_REPOSITORY_NAME);
		this.currentValueOfRepoUrl = this.preferenceStore.getString(P_REPOSITORY_URL);
		this.currentValueOfUserName = this.preferenceStore.getString(P_REPOSITORY_USERNAME);
		this.currentValueOfPassWord = this.preferenceStore.getString(P_REPOSITORY_PASSWORD);
		//this.currentValueOfRepoBaseLocation = this.preferenceStore.getString(P_REPOSITORY_BASE_LOCATION);

		/*if (isEmpty(this.currentValueOfRepoName) || isEmpty(this.currentValueOfRepoUrl) || isEmpty(this.currentValueOfUserName)
				|| isEmpty(this.currentValueOfPassWord)) {
			MessageDialog.openWarning(getShell(), "Warning", "Input value to Repository Name,Repository Url,UserName and Password fields.");
		}*/
		enableFields(getPreferenceStore().getBoolean(P_ENABLE_AUTO_CHECKIN));
	}

	/*protected Map<String, String> getPrjNameSharedPrjNameMap(final String prjUrlPairs) {
		final Map<String, String> prjShrdPrjMap = new HashMap<String, String>();
		final String projectUrlPair = this.preferenceStore.getString(P_REPOSIROTY_PROJECT_URL_PAIR);
		if (!isEmpty(projectUrlPair)) {
			final String[] prjUrlArray = projectUrlPair.split(SEMICOLON);
			for (final String prjURL : prjUrlArray) {
				if (!isEmpty(prjURL)) {
					final String[] prjUrlArr = prjURL.split(HYPHEN);
					final String sharedPrjName = prjUrlArr[1].substring(prjUrlArr[1].lastIndexOf(FORWARD_SLASH) + 1);
					prjShrdPrjMap.put(prjUrlArr[0], sharedPrjName);
				}
			}
		}
		return prjShrdPrjMap;
	}*/

	public static Map<String, String> getPrjUrlPairMap(final String prjUrlPairs) {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String prj_url_delim = globalSettings.getPropertyValue(PROJECT_URL_DELIMITER.toUpperCase(), EMPTY_STR);
		final String prj_url_pair_delim = globalSettings.getPropertyValue(PROJECT_URL_PAIR_DELIMITER.toUpperCase(), EMPTY_STR);
		final Map<String, String> prjUrlPairMap = new HashMap<String, String>();
		if (!isEmpty(prjUrlPairs)) {
			final String[] prjUrlArray = prjUrlPairs.split(prj_url_pair_delim);
			for (final String prjURL : getEmptyArrayForNull(prjUrlArray)) {
				if (!isEmpty(prjURL)) {
					final String[] prjUrlArr = prjURL.split(prj_url_delim);
					//final String sharedPrjName = prjUrlArr[1].substring(prjUrlArr[1].lastIndexOf(FORWARD_SLASH) + 1);
					if (prjUrlArr.length < 2) {
						continue;
					}
					prjUrlPairMap.put(prjUrlArr[0].trim(), prjUrlArr[1].trim());
				}
			}
		}
		return prjUrlPairMap;
	}

	private List<RepositoryData> loadData() {
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String prj_url_delim = globalSettings.getPropertyValue(PROJECT_URL_DELIMITER.toUpperCase(), EMPTY_STR);
		final String prj_url_pair_delim = globalSettings.getPropertyValue(PROJECT_URL_PAIR_DELIMITER.toUpperCase(), EMPTY_STR);
		final List<RepositoryData> repDataList = new ArrayList<RepositoryData>();
		final String projectUrlPair = this.preferenceStore.getString(P_REPOSIROTY_PROJECT_URL_PAIR);
		if (!isEmpty(projectUrlPair)) {
			final String[] prjUrlArray = projectUrlPair.split(prj_url_pair_delim);
			for (final String prjURL : prjUrlArray) {
				if (!isEmpty(prjURL)) {
					final String[] prjUrlArr = prjURL.split(prj_url_delim);
					if (prjUrlArr.length < 2) {
						continue;
					}
					final RepositoryData dataFromPref = new RepositoryData();
					dataFromPref.setAssociatedProject(prjUrlArr[0].trim());
					dataFromPref.setRepUrl(prjUrlArr[1].trim());
					repDataList.add(dataFromPref);
				}
			}
		}
		return repDataList;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		super.propertyChange(event);
		final Object source = event.getSource();
		if (source == this.repositoryName) {
			final String newValue = (String) event.getNewValue();
			this.currentValueOfRepoName = newValue;
			/*this.repositoryBaseLocation.setStringValue(this.currentValueOfRepoName.toLowerCase() + COLON + FORWARD_SLASH + FORWARD_SLASH
					+ LOCAL_HOST);*/
		}

		//this.currentValueOfRepoLoc = this.repositoryLocation.getStringValue(); -- uncomment this after enabling browse in Rep Dialog
		//this.currentValueOfRepoUrl = this.repositoryUrl.getStringValue();
		this.currentValueOfUserName = this.userName.getStringValue();
		this.currentValueOfPassWord = this.passWord.getStringValue();
		if (isEmpty(this.currentValueOfUserName) || isEmpty(this.currentValueOfPassWord)) {
			this.addButton.setEnabled(false);
			this.editButton.setEnabled(false);
		} else {
			this.addButton.setEnabled(true);
			this.editButton.setEnabled(true);
		}
		if (source == this.enableAutoCheckin) {
			enableFields((Boolean) event.getNewValue());
			/*final IEvaluationService service = (IEvaluationService) PlatformUI.getWorkbench().getService(IEvaluationService.class);
			service.requestEvaluation("org.fastcode.menus.versioncontrol");*/
		}

		/*if (source == this.repositoryUrl) {
			final String prj = this.preferenceStore.getString(P_REPOSITORY_PROJECT);
			if (isEmpty(prj)) {
				resetProjectName();
				if (!resetProjectName()) {
					//return false; - to check this line
				}
			}

		}*/
	}

	private void enableFields(final Boolean newValue) {
		this.repositoryName.setEnabled(newValue, getFieldEditorParent());
		//this.repositoryBaseLocation.setEnabled(newValue, getFieldEditorParent());
		this.userName.setEnabled(newValue, getFieldEditorParent());
		this.passWord.setEnabled(newValue, getFieldEditorParent());
		this.table.setEnabled(newValue); //, getFieldEditorParent());
		this.addButton.setEnabled(newValue);
		this.removeButton.setEnabled(newValue);
		this.editButton.setEnabled(newValue);
		this.checkIn.setEnabled(newValue, getFieldEditorParent());
		this.timeGapBeforeAutoCheckIn.setEnabled(newValue, getFieldEditorParent());
		this.prefixComments.setEnabled(newValue, getFieldEditorParent());
		this.footerComments.setEnabled(newValue, getFieldEditorParent());
		this.excludeDir.setEnabled(newValue, getFieldEditorParent());
		this.enableResChangeListener.setEnabled(newValue, getFieldEditorParent());
		if (!newValue) {
			//this.enableResChangeListener.
			this.preferenceStore.setValue(P_ENABLE_TRACK_ECLIPSE_CHANGE, newValue);
		}
	}

	@Override
	public boolean performOk() {
		if (this.enableAutoCheckin.getBooleanValue()) {
			if (isEmpty(this.currentValueOfRepoName) /*|| isEmpty(this.currentValueOfRepoBaseLocation)*/ || isEmpty(this.currentValueOfUserName)
					|| isEmpty(this.currentValueOfPassWord)) {
				MessageDialog.openWarning(getShell(), "Warning",
						"Please enter value to Repository Name,UserName and Password fields.");
				return false;
			}
			/*final String prj = this.preferenceStore.getString(P_REPOSITORY_PROJECT);
			if (isEmpty(prj)) {
				if (!resetProjectName()) {
					return false;
				}
			} else {
				if (!this.preferenceStore.getString(P_REPOSITORY_URL).equals(this.currentValueOfRepoUrl)) {
					if (!resetProjectName()) {
						return false;
					}
				}
			}*/
		}

		final boolean status = super.performOk();
		final IEvaluationService service = (IEvaluationService) PlatformUI.getWorkbench().getService(IEvaluationService.class);
		service.requestEvaluation("org.fastcode.menus.versioncontrol");
		if (status) {
			/*MessageDialog.openInformation(new Shell(), "Information",
					"The SVN url is specific to the project. While working with other project(s), please change the URL.");*/
			VersionControlPreferences.setReload(status);
		} else {
			return status;
		}
		return status;
	}

	private int computeMinimumColumnWidth(final GC gc, final String string) {

		return gc.stringExtent(string).x + 10; // pad 10 to accommodate table
												// header trimmings

	}

	/**
	 * @return
	 *
	 */
	public boolean resetProjectName() {
		String prj;
		final InputDialog inputDialog = new InputDialog(getShell(), "Project to work with", "Enter the project name", "", null);

		if (inputDialog.open() == Window.CANCEL || inputDialog.getValue().trim().equals(EMPTY_STR)) {
			return false;
		}

		prj = inputDialog.getValue();
		this.preferenceStore.setValue(P_REPOSITORY_PROJECT, prj);
		return true;
	}

	@Override
	public void init(final IWorkbench arg0) {
		// TODO Auto-generated method stub

	}

	public class ProjectUrlContentProvider implements IStructuredContentProvider {

		/*
		 * @see IStructuredContentProvider#getElements(Object)
		 */
		@Override
		public Object[] getElements(final Object input) {
			return ((List<RepositoryData>) input).toArray();
		}

		/*
		 * @see IContentProvider#inputChanged(Viewer, Object, Object)
		 */
		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {

		}

		/*
		 * @see IContentProvider#dispose()
		 */
		@Override
		public void dispose() {

		}

	}

	public class ProjectUrlLabelProvider extends LabelProvider implements ITableLabelProvider {

		/*
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
		 * .Object, int)
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		/*
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang
		 * .Object, int)
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final RepositoryData data = (RepositoryData) element;

			switch (columnIndex) {
			case 0:
				return data.getAssociatedProject();
			case 1:
				return data.getRepUrl();
			default:
				return EMPTY_STR; //$NON-NLS-1$
			}
		}

	}
}

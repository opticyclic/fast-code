package org.fastcode.preferences;

import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.DB_TEMPLATES_FOLDER;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.TEMPLATES_FOLDER;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.common.TemplateStore.addTemplate;
import static org.fastcode.preferences.PreferenceConstants.P_ALL_COMMON_TEMPLATES;
import static org.fastcode.preferences.PreferenceConstants.P_ALL_TEMPLATES;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_ALL_TEMPLATES;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ADDITIONAL_PARAMETERS;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ALLOWED_FILE_NAMES;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ALLOW_MULTIPLE_VARIATION;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_DESCRIPTION;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ENABLE_TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_FIRST_TEMPLATE_ITEM;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_GETTER_SETTER;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_NUMBER_REQUIRED_ITEMS;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_SECOND_TEMPLATE_ITEM;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_VARIATION;
import static org.fastcode.preferences.PreferenceUtil.getTemplatePreferenceKey;
import static org.fastcode.util.FastCodeUtil.getEmptyListForNull;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.TemplateUtil.loadTemplates;
import static org.fastcode.util.XMLUtil.exportXML;
import static org.fastcode.util.XMLUtil.importXML;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeConstants.FIELDS;
import org.fastcode.common.FastCodeConstants.TEMPLATE_PREFERENCE_NAME;
import org.fastcode.common.Template;
import org.fastcode.common.TemplateStore;
import org.fastcode.setting.TemplateSettings;
import org.fastcode.templates.util.VariablesUtil;
import org.fastcode.templates.viewer.TemplateFieldEditor;
import org.fastcode.util.DefaultTemplatesManager;
import org.fastcode.util.TemplateUtil;

public class AbstractTableTemplatePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private TableViewer					fTableViewer;
	private Table						table;
	private Button						fAddButton;
	private Button						fEditButton;
	private Button						fRemoveButton;
	private Button						fRestoreButton;
	private Button						fRevertButton;
	private Button						fImportButton;
	private Button						fExportButton;
	private TemplateFieldEditor			templateBodyMultiText;
	private Button						fFormatButton;

	String								allTemplates;
	String[]							TEMPLATES_LIST;
	//final boolean						additionalTemplate;

	final ArrayList<Template>			templatesList			= new ArrayList<Template>();

	protected String					templatePrefix;
	IPreferenceStore					store;

	private final String				allTemplatesPreferenceKey;

	private static Map<String, String>	filesMap				= new HashMap<String, String>();

	static {
		filesMap.put(P_DATABASE_ALL_TEMPLATES, "database-templates-config.xml");
		filesMap.put(P_ALL_TEMPLATES, "templates-config.xml");
		filesMap.put(P_ALL_COMMON_TEMPLATES, "common-templates-config.xml");

	}

	private static Map<String, String>	foldersMap				= new HashMap<String, String>();

	static {

		foldersMap.put(P_DATABASE_ALL_TEMPLATES, DB_TEMPLATES_FOLDER);
		foldersMap.put(P_ALL_TEMPLATES, TEMPLATES_FOLDER);
		foldersMap.put(P_ALL_COMMON_TEMPLATES, TEMPLATES_FOLDER);
	}

	Map<String, Integer>				newTemplatesCount		= new HashMap<String, Integer>();
	Map<String, Integer>				deleteTemplatesCount	= new HashMap<String, Integer>();
	private String						templateSelected;
	private boolean						exported				= true;

	/**
	 *
	 */
	public AbstractTableTemplatePreferencePage() {

		this.allTemplatesPreferenceKey = getAllTemplatesPreferenceKey();
		//this.additionalTemplate = P_ALL_ADDITIONAL_TEMPLATES.equals(this.allTemplatesPreferenceKey);
		this.store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		setPreferenceStore(this.store);
		this.allTemplates = this.store.getString(this.allTemplatesPreferenceKey);
		this.TEMPLATES_LIST = this.allTemplates.split(COLON);
		/*final TemplateStore templateStore = TemplateStore.getInstance();

		if (templateStore.isEmpty() || !templateStore.contains(this.allTemplatesPreferenceKey)) {
			loadTemplates();

		}*/
		setDescription("Template preference");

	}

	@Override
	public void init(final IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Control createContents(final Composite ancestor) {
		final Composite parent = new Composite(ancestor, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
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
		data.widthHint = 360;
		data.heightHint = convertHeightInCharsToPixels(10);
		tableComposite.setLayoutData(data);

		final TableColumnLayout columnLayout = new TableColumnLayout();
		tableComposite.setLayout(columnLayout);

		this.fTableViewer = new TableViewer(tableComposite, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION | SWT.H_SCROLL
				| SWT.V_SCROLL);

		this.table = this.fTableViewer.getTable();

		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		final GC gc = new GC(getShell());
		gc.setFont(JFaceResources.getDialogFont());

		final TableViewerColumn viewerColumn1 = new TableViewerColumn(this.fTableViewer, SWT.NONE);
		final TableColumn column1 = viewerColumn1.getColumn();
		column1.setText("Name");
		int minWidth = computeMinimumColumnWidth(gc, "Name");
		columnLayout.setColumnData(column1, new ColumnWeightData(4, minWidth, true));

		final TableViewerColumn viewerColumn3 = new TableViewerColumn(this.fTableViewer, SWT.NONE);
		final TableColumn column3 = viewerColumn3.getColumn();
		column3.setText("Description");
		minWidth = computeMinimumColumnWidth(gc, "Description");
		columnLayout.setColumnData(column3, new ColumnWeightData(3, minWidth, true));

		final TableViewerColumn viewerColumn4 = new TableViewerColumn(this.fTableViewer, SWT.NONE);
		final TableColumn column4 = viewerColumn4.getColumn();
		column4.setText("Allowed File Names");
		minWidth = computeMinimumColumnWidth(gc, "Allowed ");
		columnLayout.setColumnData(column4, new ColumnWeightData(2, minWidth, true));

		gc.dispose();

		this.fTableViewer.setLabelProvider(new TemplateLabelProvider());
		this.fTableViewer.setContentProvider(new TemplateContentProvider());

		// this.fTableViewer.setComparator(new ViewerComparator() {
		// @Override
		// public int compare(final Viewer viewer, final Object object1,
		// final Object object2) {
		// if (object1 instanceof TemplateTableData
		// && object2 instanceof TemplateTableData) {
		// final Template left = ((TemplateTableData) object1)
		// .getTemplate();
		// final Template right = ((TemplateTableData) object2)
		// .getTemplate();
		// final int result = Collator.getInstance().compare(
		// left.getName(), right.getName());
		// if (result != 0) {
		// return result;
		// }
		// return Collator.getInstance().compare(
		// left.getDescription(), right.getDescription());
		// }
		// return super.compare(viewer, object1, object2);
		// }
		//
		// @Override
		// public boolean isSorterProperty(final Object element,
		// final String property) {
		// return true;
		// }
		// });

		this.fTableViewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(final Viewer viewer, final Object object1, final Object object2) {

				if (object1 instanceof Template && object2 instanceof Template) {
					final String left = ((Template) object1).getTemplateName();
					final String right = ((Template) object2).getTemplateName();
					final int result = left.compareToIgnoreCase(right);
					if (result != 0) {
						return result;
					}

				}
				return super.compare(viewer, object1, object2);
			}

			@Override
			public final boolean isSorterProperty(final Object element, final String property) {
				return true;
			}

		});

		this.table.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				if (event.detail == SWT.CHECK) {
					final Template template = (Template) event.item.getData();
					final String prefkey = getTemplatePreferenceKey(AbstractTableTemplatePreferencePage.this.templatePrefix + UNDERSCORE
							+ template.getTemplateName(), P_TEMPLATE_ENABLE_TEMPLATE);
					final boolean value = getPreferenceStore().getBoolean(prefkey);
					setEnabled(template, value);
				} else {
					VariablesUtil.reload(true);
					final Template template = (Template) AbstractTableTemplatePreferencePage.this.fTableViewer
							.getElementAt(AbstractTableTemplatePreferencePage.this.table.getSelectionIndex());
					AbstractTableTemplatePreferencePage.this.templateBodyMultiText.setText(template.getTemplateBody());
					AbstractTableTemplatePreferencePage.this.templateSelected = template.getTemplateName();
					enableOperations();

				}
			}
		});

		this.table.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				editTemplate();
			}

			@Override
			public void mouseDown(final MouseEvent e) {
			}

			@Override
			public void mouseUp(final MouseEvent e) {
			}
		});
		final Composite buttons = new Composite(innerParent, SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);

		this.fAddButton = new Button(buttons, SWT.PUSH);
		this.fAddButton.setText("New");
		this.fAddButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fAddButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				add();
				refreshInput();
				AbstractTableTemplatePreferencePage.this.fTableViewer.refresh();
				enableTemplates();
				// TemplateSettings.setReload(true);
			}
		});

		this.fEditButton = new Button(buttons, SWT.PUSH);
		this.fEditButton.setText("Edit");
		this.fEditButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fEditButton.setEnabled(false);
		this.fEditButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event e) {

				editTemplate();

			}
		});

		this.fRemoveButton = new Button(buttons, SWT.PUSH);
		this.fRemoveButton.setText("Remove");
		this.fRemoveButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.fRemoveButton.setEnabled(false);

		this.fRemoveButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				final int index = AbstractTableTemplatePreferencePage.this.table.getSelectionIndex();
				if (index > -1) {
					remove(index);
					enableTemplates();
					// TemplateSettings.setReload(true);

				}

			}
		});

		createSeparator(buttons);

		this.fRestoreButton = new Button(buttons, SWT.PUSH);
		this.fRestoreButton.setText("Restore");
		this.fRestoreButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// this.fRestoreButton.setEnabled(false);
		this.fRestoreButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				restoreDeleted();
				enableTemplates();
				// TemplateSettings.setReload(true);
			}
		});

		this.fRevertButton = new Button(buttons, SWT.PUSH);
		this.fRevertButton.setText("Revert");
		this.fRevertButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fRevertButton.setEnabled(false);
		this.fRevertButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				// revert();
			}
		});

		createSeparator(buttons);

		this.fImportButton = new Button(buttons, SWT.PUSH);
		this.fImportButton.setText("Import");
		this.fImportButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.fImportButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				import_();
				loadTemplates(AbstractTableTemplatePreferencePage.this.store, AbstractTableTemplatePreferencePage.this.templatePrefix,
						AbstractTableTemplatePreferencePage.this.allTemplatesPreferenceKey);
				refreshInput();
				AbstractTableTemplatePreferencePage.this.fTableViewer.refresh();
				enableTemplates();
				loadPreview();
			}
		});

		this.fExportButton = new Button(buttons, SWT.PUSH);
		this.fExportButton.setText("Export");
		this.fExportButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// this.fExportButton.setEnabled(false);
		this.fExportButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event e) {
				export();
				enableTemplates();
			}
		});

		doCreateViewer(parent);

		/*if (isShowFormatterSetting()) {
			this.fFormatButton = new Button(parent, SWT.CHECK);
			this.fFormatButton.setText("Use Code Formatter");
			final GridData gd1 = new GridData();
			gd1.horizontalSpan = 2;
			this.fFormatButton.setLayoutData(gd1);
			// this.fFormatButton.setSelection(getPreferenceStore().getBoolean(
			// getFormatterPreferenceKey()));
		}*/

		this.fTableViewer.setInput(refreshInput());

		// Dialog.applyDialogFont(parent);
		innerParent.layout();
		enableTemplates();
		getControl().addListener(SWT.Traverse, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				if (event.detail == SWT.TRAVERSE_ESCAPE) {
					if (!AbstractTableTemplatePreferencePage.this.exported) {
						if (MessageDialog.openQuestion(null, "Escape Pressed", "New template has been added.Save Changes?")) {
							export();

						}

					}

				}
			}
		});
		return parent;
	}

	protected void editTemplate() {
		final Template templateSelected = (Template) AbstractTableTemplatePreferencePage.this.fTableViewer
				.getElementAt(AbstractTableTemplatePreferencePage.this.table.getSelectionIndex());

		edit(templateSelected, AbstractTableTemplatePreferencePage.this.table.getSelectionIndex());
		refreshInput();
		AbstractTableTemplatePreferencePage.this.fTableViewer.refresh();
		enableTemplates();
		AbstractTableTemplatePreferencePage.this.templateBodyMultiText.setText(getPreferenceStore()
				.getString(
						getTemplatePreferenceKey(
								AbstractTableTemplatePreferencePage.this.templatePrefix + "_" + templateSelected.getTemplateName(),
								P_TEMPLATE_BODY)));

	}

	private void loadPreview() {
		if (this.templateBodyMultiText.getStringValue().equals(EMPTY_STR)) {
			this.templateBodyMultiText.setText(getPreferenceStore().getString(
					getTemplatePreferenceKey(this.templateSelected, P_TEMPLATE_BODY)));
		}

	}

	/**
	 * @param template
	 * @param value
	 */
	private void setEnabled(final Template template, final boolean value) {
		getPreferenceStore()
				.setValue(
						getTemplatePreferenceKey(this.templatePrefix + UNDERSCORE + template.getTemplateName(), P_TEMPLATE_ENABLE_TEMPLATE),
						!value);

		enableTemplates();

	}

	protected void referesh() {
		this.store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		setPreferenceStore(this.store);
		this.allTemplates = this.store.getString(this.allTemplatesPreferenceKey);
		this.TEMPLATES_LIST = this.allTemplates.split(COLON);
		final TemplateStore templateStore = TemplateStore.getInstance();
		templateStore.getTemplatesList(getAllTemplatesPreferenceKey()).clear();
		TemplateUtil.loadTemplates(this.store, this.templatePrefix, this.allTemplatesPreferenceKey);
		refreshInput();
		this.fTableViewer.refresh();

	}

	/**
	 *
	 */
	private void enableTemplates() {

		for (final TableItem item : this.table.getItems()) {

			item.setChecked(getPreferenceStore().getBoolean(
					getTemplatePreferenceKey(this.templatePrefix + UNDERSCORE + item.getText(), P_TEMPLATE_ENABLE_TEMPLATE)));
		}
		this.fTableViewer.refresh();

	}

	/**
	 * @return
	 */
	private ArrayList<Template> refreshInput() {
		this.templatesList.clear();
		final TemplateStore templateStore = TemplateStore.getInstance();
		for (final Template t : getEmptyListForNull(templateStore.getTemplatesList(this.allTemplatesPreferenceKey))) {
			if (!t.isTemplateDeleted()) {
				this.templatesList.add(t);

			}

		}
		Collections.sort(this.templatesList);
		return this.templatesList;
	}

	/**
	 *
	 */
	protected void enableOperations() {
		this.fEditButton.setEnabled(true);
		this.fExportButton.setEnabled(true);
		this.fRemoveButton.setEnabled(true);

	}

	/**
	 *
	 */
	protected void restoreDeleted() {

		final TemplateStore templateStore = TemplateStore.getInstance();
		for (final Template t : templateStore.getTemplatesList(this.allTemplatesPreferenceKey)) {
			if (t.isTemplateDeleted()) {
				t.setTemplateDeleted(false);
				this.allTemplates = this.allTemplates + COLON + this.templatePrefix + UNDERSCORE + t.getTemplateName();
			}
		}
		this.deleteTemplatesCount.clear();
		this.store.setValue(this.allTemplatesPreferenceKey, this.allTemplates);
		refreshInput();
		this.fTableViewer.refresh();
		this.exported = false;
	}

	/**
	 * @param index
	 */
	protected void remove(final int index) {

		final TemplateStore templateStore = TemplateStore.getInstance();
		for (final Template t : templateStore.getTemplatesList(this.allTemplatesPreferenceKey)) {

			if (this.templatesList.get(index).getTemplateName().equals(t.getTemplateName())) {
				final String tname = this.templatePrefix + UNDERSCORE + t.getTemplateName();
				if (this.allTemplates.contains(tname)) {
					final ArrayList<String> defaultTemplates = DefaultTemplatesManager.getInstance().getDefaultTemplates(
							this.templatePrefix);
					if (defaultTemplates.contains(tname)) {
						MessageDialog.openWarning(null, "Deleting Default templates...", tname
								+ "is a default template.Default templates should not be deleted");
						return;
					}
					if (!MessageDialog.openConfirm(null, "Deleting template...", "Are you sure you want to remove template - " + tname
							+ " - from preference.")) {
						return;
					}
					if (this.allTemplates.indexOf(tname) + tname.length() < this.allTemplates.length()) {
						this.allTemplates = this.allTemplates.replace(tname + COLON, EMPTY_STR);
					} else {
						this.allTemplates = this.allTemplates.replace(COLON + tname, EMPTY_STR);
					}
					this.store.setValue(this.allTemplatesPreferenceKey, this.allTemplates);
					t.setTemplateDeleted(true);
					this.deleteTemplatesCount.put(
							this.allTemplatesPreferenceKey,
							this.deleteTemplatesCount.get(this.allTemplatesPreferenceKey) == null ? 1 : this.deleteTemplatesCount
									.get(this.allTemplatesPreferenceKey) + 1);
					break;
				}

			}
		}
		refreshInput();
		this.fTableViewer.refresh();
		this.templateBodyMultiText.setText(EMPTY_STR);
		AbstractTableTemplatePreferencePage.this.fRestoreButton.setEnabled(true);
		this.exported = false;
	}

	/**
	 * @param template
	 * @param index
	 */
	protected void edit(final Template template, final int index) {

		final OpenEditTemplateDialog dialog = new OpenEditTemplateDialog(new Shell(), getPreferenceStore(), template, this.templatePrefix,
				this.allTemplatesPreferenceKey, isDetailedTemplate(), isShowAllowedFileExtension(), false);
		if (dialog.open() == Window.CANCEL) {
			return;
		}
		if (dialog.getTemplateName() == null) {

			updateTemplateStore(template);

		} else if (dialog.isExistingTemplateRenamed()) {

			replaceNewTemplate(template, createNewTemplate(dialog.getTemplateName()));

			this.allTemplates = this.allTemplates.contains(this.templatePrefix + UNDERSCORE + template.getTemplateName()) ? this.allTemplates
					.replace(this.templatePrefix + UNDERSCORE + template.getTemplateName(),
							this.templatePrefix + UNDERSCORE + dialog.getTemplateName()) : EMPTY_STR;

			this.store.setValue(this.allTemplatesPreferenceKey, this.allTemplates);

			this.newTemplatesCount.put(
					this.allTemplatesPreferenceKey,
					this.newTemplatesCount.get(this.allTemplatesPreferenceKey) == null ? 1 : this.newTemplatesCount
							.get(this.allTemplatesPreferenceKey) + 1);

		} else {
			addTemplate(this.allTemplatesPreferenceKey, createNewTemplate(dialog.getTemplateName()));
			this.allTemplates = this.allTemplates + COLON + dialog.getTemplateName();
			this.store.setValue(this.allTemplatesPreferenceKey, this.allTemplates);

			this.newTemplatesCount.put(
					this.allTemplatesPreferenceKey,
					this.newTemplatesCount.get(this.allTemplatesPreferenceKey) == null ? 1 : this.newTemplatesCount
							.get(this.allTemplatesPreferenceKey) + 1);
		}

	}

	/**
	 * @param template
	 */
	private void updateTemplateStore(final Template template) {
		final TemplateStore templateStore = TemplateStore.getInstance();

		for (final Template t : templateStore.getTemplatesList(this.allTemplatesPreferenceKey)) {
			if (template.getTemplateName().equals(t.getTemplateName())) {
				final String templateName = this.templatePrefix + UNDERSCORE + t.getTemplateName();
				t.setDescription(getPreferenceStore().getString(getTemplatePreferenceKey(templateName, P_TEMPLATE_DESCRIPTION)));
				t.setTemplateVariation(getPreferenceStore().getString(getTemplatePreferenceKey(templateName, P_TEMPLATE_VARIATION)));
				t.setAllowedFileNames(getPreferenceStore().getString(getTemplatePreferenceKey(templateName, P_TEMPLATE_ALLOWED_FILE_NAMES)));
				t.setTemplateBody(getPreferenceStore().getString(getTemplatePreferenceKey(templateName, P_TEMPLATE_BODY)));
				t.setAdditionalParameters(getPreferenceStore().getString(
						getTemplatePreferenceKey(templateName, P_TEMPLATE_ADDITIONAL_PARAMETERS)));
				if (isDetailedTemplate()) {
					t.setFirstTemplateItem(getPreferenceStore().getString(
							getTemplatePreferenceKey(templateName, P_TEMPLATE_FIRST_TEMPLATE_ITEM)));
					t.setSecondTemplateItem(getPreferenceStore().getString(
							getTemplatePreferenceKey(templateName, P_TEMPLATE_SECOND_TEMPLATE_ITEM)));
					t.setRequiredClassItem(getPreferenceStore().getString(
							getTemplatePreferenceKey(templateName, P_TEMPLATE_NUMBER_REQUIRED_ITEMS)));
					t.setGetterSetterItem(getPreferenceStore().getString(getTemplatePreferenceKey(templateName, P_TEMPLATE_GETTER_SETTER)));
					t.setMultipleVariationCheckbox(getPreferenceStore().getBoolean(
							getTemplatePreferenceKey(templateName, P_TEMPLATE_ALLOW_MULTIPLE_VARIATION)));
				}
			}
		}

	}

	protected void add() {

		final OpenEditTemplateDialog dialog = new OpenEditTemplateDialog(new Shell(), getPreferenceStore(), null, this.templatePrefix,
				this.allTemplatesPreferenceKey, isDetailedTemplate(), isShowAllowedFileExtension(), true);
		if (dialog.open() == Window.CANCEL) {
			return;
		}

		if (dialog.getTemplateName() != null) {

			final String template = this.templatePrefix + UNDERSCORE + dialog.getTemplateName();
			refreshInput();
			this.fTableViewer.refresh();
			this.allTemplates = this.allTemplates + COLON + template;
			this.store.setValue(this.allTemplatesPreferenceKey, this.allTemplates);
			this.newTemplatesCount.put(
					this.allTemplatesPreferenceKey,
					this.newTemplatesCount.get(this.allTemplatesPreferenceKey) == null ? 1 : this.newTemplatesCount
							.get(this.allTemplatesPreferenceKey) + 1);
			addTemplate(this.allTemplatesPreferenceKey, createNewTemplate(dialog.getTemplateName()));
			this.exported = false;
		}

	}

	/**
	 * @param oldTemplate
	 * @param newTemplate
	 */
	private void replaceNewTemplate(final Template oldTemplate, final Template newTemplate) {

		final TemplateStore templateStore = TemplateStore.getInstance();

		templateStore.getTemplatesList(this.allTemplatesPreferenceKey).set(
				templateStore.getTemplatesList(this.allTemplatesPreferenceKey).indexOf(oldTemplate), newTemplate);

	}

	/**
	 * @param templateName
	 * @return
	 */
	private Template createNewTemplate(final String templateName) {
		Template newTemplate;
		final String template = this.templatePrefix + UNDERSCORE + templateName;
		if (isDetailedTemplate()) {

			newTemplate = new Template(templateName, getPreferenceStore().getString(
					getTemplatePreferenceKey(template, P_TEMPLATE_DESCRIPTION)), getPreferenceStore().getString(
					getTemplatePreferenceKey(template, P_TEMPLATE_VARIATION)), getPreferenceStore().getString(
					getTemplatePreferenceKey(template, P_TEMPLATE_ALLOWED_FILE_NAMES)), getPreferenceStore().getString(
					getTemplatePreferenceKey(template, P_TEMPLATE_BODY)), getPreferenceStore().getBoolean(
					getTemplatePreferenceKey(template, P_TEMPLATE_ENABLE_TEMPLATE)), getPreferenceStore().getString(
					getTemplatePreferenceKey(template, P_TEMPLATE_FIRST_TEMPLATE_ITEM)), getPreferenceStore().getString(
					getTemplatePreferenceKey(template, P_TEMPLATE_SECOND_TEMPLATE_ITEM)), getPreferenceStore().getString(
					getTemplatePreferenceKey(template, P_TEMPLATE_NUMBER_REQUIRED_ITEMS)), getPreferenceStore().getString(
					getTemplatePreferenceKey(template, P_TEMPLATE_GETTER_SETTER)), getPreferenceStore().getString(
					getTemplatePreferenceKey(template, P_TEMPLATE_ADDITIONAL_PARAMETERS)), getPreferenceStore().getBoolean(
					getTemplatePreferenceKey(template, P_TEMPLATE_ALLOW_MULTIPLE_VARIATION)), null, false);

		} else {
			newTemplate = new Template(templateName, getPreferenceStore().getString(
					getTemplatePreferenceKey(template, P_TEMPLATE_DESCRIPTION)), getPreferenceStore().getString(
					getTemplatePreferenceKey(template, P_TEMPLATE_VARIATION)), getPreferenceStore().getString(
					getTemplatePreferenceKey(template, P_TEMPLATE_ALLOWED_FILE_NAMES)), getPreferenceStore().getString(
					getTemplatePreferenceKey(template, P_TEMPLATE_BODY)), getPreferenceStore().getBoolean(
					getTemplatePreferenceKey(template, P_TEMPLATE_ENABLE_TEMPLATE)), false);
		}
		return newTemplate;
	}

	protected void import_() {

		try {
			if (getAllTemplatesPreferenceKey().equals(P_ALL_TEMPLATES)) {
				//processXML("templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_TEMPLATES.toString(), TEMPLATES_FOLDER);
				importXML("templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_TEMPLATES.toString(), TEMPLATES_FOLDER);
			} else if (getAllTemplatesPreferenceKey().equals(P_ALL_COMMON_TEMPLATES)) {
				//processXML("additional-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_ADDITIONAL_TEMPLATES.toString(),	TEMPLATES_FOLDER);
				importXML("common-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_COMMON_TEMPLATES.toString(), TEMPLATES_FOLDER);
			} else if (getAllTemplatesPreferenceKey().equals(P_DATABASE_ALL_TEMPLATES)) {
				//processXML("database-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_DATABASE_TEMPLATES.name(), DB_TEMPLATES_FOLDER);
				importXML("database-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_DATABASE_TEMPLATES.name(), DB_TEMPLATES_FOLDER);
			} /*else if (getAllTemplatesPreferenceKey().equals(P_ALL_ADDITIONAL_DATABASE_TEMPLATES)) {
				//processXML("additional-database-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_ADDITIONAL_DATABASE_TEMPLATES.name(),DB_TEMPLATES_FOLDER);
				importXML("additional-database-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_ADDITIONAL_DATABASE_TEMPLATES.name(),
						DB_TEMPLATES_FOLDER);
			} /*else if (getAllTemplatesPreferenceKey().equals(P_FILE_ALL_TEMPLAT)) {
				//processXML("file-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_FILE_TEMPLATES.name(), FILE_TEMPLATES_FOLDER);
				importXML("file-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_FILE_TEMPLATES.name(), FILE_TEMPLATES_FOLDER);
				}*/
		} catch (final Exception ex) {
			try {
				throw new Exception("There was some error in Import templates : " + ex.getMessage());
			} catch (final Exception ex1) {
				MessageDialog.openError(null, "Import Failed", ex1.getMessage() + "....Please retry after making the changes");
				ex1.printStackTrace();
			}
			ex.printStackTrace();
		}

	}

	protected void export() {
		try {
			if (getAllTemplatesPreferenceKey().equals(P_ALL_TEMPLATES)) {
				//processXML("templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_TEMPLATES.toString(), TEMPLATES_FOLDER);
				exportXML("templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_TEMPLATES.toString(), TEMPLATES_FOLDER);
			} else if (getAllTemplatesPreferenceKey().equals(P_ALL_COMMON_TEMPLATES)) {
				//processXML("additional-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_ADDITIONAL_TEMPLATES.toString(),TEMPLATES_FOLDER);
				exportXML("common-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_COMMON_TEMPLATES.toString(), TEMPLATES_FOLDER);
			} else if (getAllTemplatesPreferenceKey().equals(P_DATABASE_ALL_TEMPLATES)) {
				//processXML("database-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_DATABASE_TEMPLATES.name(), DB_TEMPLATES_FOLDER);
				exportXML("database-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_DATABASE_TEMPLATES.name(), DB_TEMPLATES_FOLDER);
			} /*else if (getAllTemplatesPreferenceKey().equals(P_ALL_ADDITIONAL_DATABASE_TEMPLATES)) {
				//processXML("additional-database-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_ADDITIONAL_DATABASE_TEMPLATES.name(),	DB_TEMPLATES_FOLDER);
				exportXML("additional-database-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_ADDITIONAL_DATABASE_TEMPLATES.name(),
						DB_TEMPLATES_FOLDER);
			} /*else if (getAllTemplatesPreferenceKey().equals(P_FILE_ALL_TEMPLATES)) {
				//processXML("file-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_FILE_TEMPLATES.name(), FILE_TEMPLATES_FOLDER);
				exportXML("file-templates-config.xml", TEMPLATE_PREFERENCE_NAME.ALL_FILE_TEMPLATES.name(), FILE_TEMPLATES_FOLDER);
				}*/
			this.exported = true;
		} catch (final Exception ex) {
			try {
				throw new Exception("There was some error in Export templates : " + ex.getMessage());
			} catch (final Exception ex1) {
				MessageDialog.openError(null, "Export Failed", ex.getMessage() + "....Please retry after making the changes");
				ex1.printStackTrace();
			}
			ex.printStackTrace();
		}
	}

	private boolean isShowFormatterSetting() {

		return true;
	}

	/**
	 * @param parent
	 */
	private void doCreateViewer(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText("Preview");
		final GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		final GridData gridDataText = new GridData(SWT.FILL, SWT.FILL, true, false);
		gridDataText.heightHint = 250;

		this.templateBodyMultiText = new TemplateFieldEditor("preview", EMPTY_STR, parent, "TEMPLATE", FIELDS.TEMPLATE_BODY, SWT.MULTI);
		this.templateBodyMultiText.setLayout(gridDataText);
		this.templateBodyMultiText.setText(EMPTY_STR);
		this.templateBodyMultiText.setEditable(false);
		//this.templateBodyMultiText.setEnabled(false, parent);

	}

	/**
	 * Creates a separator between buttons.
	 *
	 * @param parent
	 *            the parent composite
	 * @return a separator
	 */
	private Label createSeparator(final Composite parent) {
		final Label separator = new Label(parent, SWT.NONE);
		separator.setVisible(false);
		final GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.BEGINNING;
		gd.heightHint = 4;
		separator.setLayoutData(gd);
		return separator;
	}

	/**
	 * @param gc
	 * @param string
	 * @return
	 */
	private int computeMinimumColumnWidth(final GC gc, final String string) {

		return gc.stringExtent(string).x + 10; // pad 10 to accommodate table
												// header trimmings

	}

	/**
	 *
	 * @return
	 */
	protected String getAllTemplatesPreferenceKey() {
		return P_ALL_TEMPLATES;
	}

	protected boolean isDetailedTemplate() {
		return true;
	}

	/**
	 *
	 */

	protected boolean isShowAllowedFileExtension() {
		return true;
	}

	@Override
	public boolean performOk() {
		if (!this.exported) {
			if (this.newTemplatesCount.get(this.allTemplatesPreferenceKey) != null
					&& this.newTemplatesCount.get(this.allTemplatesPreferenceKey) > 0
					|| this.deleteTemplatesCount.get(this.allTemplatesPreferenceKey) != null
					&& this.deleteTemplatesCount.get(this.allTemplatesPreferenceKey) > 0) {

				final MessageDialog dialog = new MessageDialog(
						null,
						"Warning",
						null,
						"Templates have been added/renamed/deleted,\n Would you like to save the changes, by exporting the same, before exiting the preference page?  ",
						MessageDialog.WARNING, new String[] { "Yes", "No" }, 0) {

					@Override
					protected void buttonPressed(final int buttonId) {
						setReturnCode(buttonId);
						close();

					}
				};

				dialog.open();

				if (dialog.getReturnCode() == 0) {
					this.fExportButton.setEnabled(true);
					export();
				}

			}
		}
		if (!isEmpty(getErrorMessage())) {
			openError(getShell(), "Error", "There are errors present, cannot save.");
			return false;
		}
		final boolean status = super.performOk();
		TemplateSettings.setReload(status);
		return status;
	}

}

/**
 * Label provider for templates.
 */
class TemplateLabelProvider extends LabelProvider implements ITableLabelProvider {

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
		final Template data = (Template) element;

		switch (columnIndex) {
		case 0:
			return data.getTemplateName();
		case 1:

			return data.getDescription();
		case 2:
			return data.getAllowedFileNames();

		default:
			return EMPTY_STR; //$NON-NLS-1$
		}
	}
}

class TemplateContentProvider implements IStructuredContentProvider {

	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	@Override
	public Object[] getElements(final Object input) {
		return ((List<Template>) input).toArray();
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

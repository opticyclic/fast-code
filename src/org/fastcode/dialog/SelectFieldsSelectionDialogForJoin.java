package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.HASH;
import static org.fastcode.common.FastCodeConstants.HASH_CHAR;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.common.FastCodeConstants.UNDER_SCORE;
import static org.fastcode.util.FastCodeUtil.getEmptyListForNull;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeDataBaseField;
import org.fastcode.common.FastCodeDataBaseFieldDecorator;
import org.fastcode.util.DataBaseTypeInfo;
import org.fastcode.util.DatabaseCache;
import org.fastcode.util.SQLFunctions;
import org.fastcode.util.SQLFunctionsMapping;

public class SelectFieldsSelectionDialogForJoin extends TitleAreaDialog {

	private class TableLabelProviderForSelectFieldJoin extends LabelProvider implements ITableLabelProvider {

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java
		 * .lang.Object, int)
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {

			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final int TABLE_NAME_COLUMN = 0;
			final int FIELD_NAME_COLUMN = 1;
			final int DATA_TYPE_COLUMN = 2;
			final int IS_NULLABLE_COLUMN = 3;
			final int ALAIS_COLUMN = 5;
			final int SQLFUNCTIONS_COLUMN = 4;
			final DataBaseFieldInfoForJoin dataBaseFieldInfo = (DataBaseFieldInfoForJoin) element;
			String result = EMPTY_STR;
			switch (columnIndex) {
			case TABLE_NAME_COLUMN:
				result = dataBaseFieldInfo.getTableName();
				break;
			case FIELD_NAME_COLUMN:
				result = dataBaseFieldInfo.getFieldName();
				break;
			case DATA_TYPE_COLUMN:
				result = dataBaseFieldInfo.getDataType();
				break;
			case IS_NULLABLE_COLUMN:
				result = String.valueOf(dataBaseFieldInfo.isNullable());
				break;
			case ALAIS_COLUMN:
				result = dataBaseFieldInfo.getValue() == null ? EMPTY_STR : dataBaseFieldInfo.getAlaisName();
				break;
			case SQLFUNCTIONS_COLUMN:
				result = String.valueOf(dataBaseFieldInfo.getValue());
				break;
			default:

				result = EMPTY_STR;
			}
			return result;
		}

	}

	private static class ContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(final Object inputElement) {

			return ((List<DataBaseFieldInfoForJoin>) inputElement).toArray();
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		}
	}

	private final String[]								tableNames				= new String[3];

	DatabaseCache										databaseCache			= DatabaseCache.getInstance();
	List<FastCodeDataBaseFieldDecorator>				fieldInfoForFirstTable	= new ArrayList<FastCodeDataBaseFieldDecorator>();
	List<FastCodeDataBaseFieldDecorator>				fieldInfoForSecondTable	= new ArrayList<FastCodeDataBaseFieldDecorator>();
	List<FastCodeDataBaseFieldDecorator>				fieldInfoForThirdTable	= new ArrayList<FastCodeDataBaseFieldDecorator>();
	private final List<DataBaseFieldInfoForJoin>		fieldListForJoin		= new ArrayList<DataBaseFieldInfoForJoin>();

	private final List<FastCodeDataBaseFieldDecorator>	selectedFields			= new ArrayList<FastCodeDataBaseFieldDecorator>();

	Shell												shell;

	final IPreferenceStore								preferenceStore;

	TableViewer											tableViewer;
	Table												table;

	private boolean										selfJoin				= false;

	private boolean										isAliasNameRequired;

	private boolean										isSQLFunctionsRequired;

	private final List<DataBaseFieldInfoForJoin>		joinrows				= new ArrayList<DataBaseFieldInfoForJoin>();

	static ArrayList<String>							selectFieldsJoin		= new ArrayList<String>();
	static {

		selectFieldsJoin.add("Table Name");
		selectFieldsJoin.add("Field Name");
		selectFieldsJoin.add("Data Type");
		selectFieldsJoin.add("Nullable");
		//selectFieldsJoin.add("Alais Name");

	}

	/**
	 * @param shell
	 */
	protected SelectFieldsSelectionDialogForJoin(final Shell shell) {
		super(shell);
		this.shell = shell;

		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID); // Activator.getDefault().getPreferenceStore();
	}

	/**
	 * @param shell
	 * @param firstTableName
	 * @param secondTableName
	 * @param schema2
	 * @param schema1
	 */
	public SelectFieldsSelectionDialogForJoin(final Shell shell, final String firstTableName, final String secondTableName,
			final boolean isSQLFunctionsRequired, final boolean isAliasNameRequired, final String schema1, final String schema2) {
		super(shell);
		this.shell = shell;
		this.shell.setSize(700, 200);
		// System.out.println("inside create variable dialog");
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		// this.createClassData = createClassData;
		this.tableNames[0] = firstTableName;
		this.tableNames[1] = secondTableName;
		this.isSQLFunctionsRequired = isSQLFunctionsRequired;
		this.isAliasNameRequired = isAliasNameRequired;

		//TableUtil.updateSQLFunctionsMapping();

		this.fieldInfoForFirstTable.addAll(getEmptyListForNull(this.databaseCache.getTableNameFieldDetailsMap().get(
				schema1 + DOT + firstTableName)));
		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.fieldInfoForFirstTable) {
			createInputForTable(firstTableName, schema1, fastCodeDataBaseFieldDecorator);

		}

		if (this.tableNames[0].equals(this.tableNames[1])) {
			this.selfJoin = true;
			for (final FastCodeDataBaseFieldDecorator field : this.fieldInfoForFirstTable) {

				this.fieldInfoForSecondTable.add(new FastCodeDataBaseFieldDecorator(new FastCodeDataBaseField(field.getName(), field
						.getType(), field.getValue(), field.getSize(), field.isNullable(), field.getJavaName(), field.getJavaTypeName(),
						field.getTableName(), null), null));
			}

		} else {

			this.fieldInfoForSecondTable.addAll(getEmptyListForNull(this.databaseCache.getTableNameFieldDetailsMap().get(
					schema2 + DOT + secondTableName)));
		}
		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.fieldInfoForSecondTable) {
			createInputForTable(secondTableName, schema2, fastCodeDataBaseFieldDecorator);

		}

	}

	/**
	 * @param shell
	 * @param firstTableName
	 * @param secondTableName
	 * @param thirdTableName
	 * @param schema3
	 * @param schema2
	 * @param schema1
	 */
	public SelectFieldsSelectionDialogForJoin(final Shell shell, final String firstTableName, final String secondTableName,
			final String thirdTableName, final boolean isSQLFunctionsRequired, final boolean isAliasNameRequired, final String schema1,
			final String schema2, final String schema3) {
		super(shell);
		this.shell = shell;
		this.shell.setSize(700, 200);
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		this.tableNames[0] = firstTableName;
		this.tableNames[1] = secondTableName;
		this.tableNames[2] = thirdTableName;
		this.isSQLFunctionsRequired = isSQLFunctionsRequired;
		this.isAliasNameRequired = isAliasNameRequired;

		this.fieldInfoForFirstTable.addAll(getEmptyListForNull(this.databaseCache.getTableNameFieldDetailsMap().get(
				schema1 + DOT + firstTableName)));
		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.fieldInfoForFirstTable) {
			createInputForTable(firstTableName, schema1, fastCodeDataBaseFieldDecorator);
		}

		if (this.tableNames[0].equals(this.tableNames[1])) {
			this.selfJoin = true;
			for (final FastCodeDataBaseFieldDecorator field : this.fieldInfoForFirstTable) {
				this.fieldInfoForSecondTable.add(new FastCodeDataBaseFieldDecorator(new FastCodeDataBaseField(field.getName(), field
						.getType(), field.getValue(), field.getSize(), field.isNullable(), field.getJavaName(), field.getJavaTypeName(),
						field.getTableName(), null), null));
			}
		} else {
			this.fieldInfoForSecondTable.addAll(getEmptyListForNull(this.databaseCache.getTableNameFieldDetailsMap().get(
					schema2 + DOT + secondTableName)));

		}

		for (final FastCodeDataBaseFieldDecorator field : this.fieldInfoForSecondTable) {
			createInputForTable(secondTableName, schema2, field);
		}

		if (this.tableNames[1].equals(this.tableNames[2])) {
			this.selfJoin = true;
			for (final FastCodeDataBaseFieldDecorator field : this.fieldInfoForSecondTable) {
				this.fieldInfoForThirdTable.add(new FastCodeDataBaseFieldDecorator(new FastCodeDataBaseField(field.getName(), field
						.getType(), field.getValue(), field.getSize(), field.isNullable(), field.getJavaName(), field.getJavaTypeName(),
						field.getTableName(), null), null));
			}

		} else if (this.tableNames[0].equals(this.tableNames[2])) {
			this.selfJoin = true;
			for (final FastCodeDataBaseFieldDecorator field : this.fieldInfoForFirstTable) {
				this.fieldInfoForThirdTable.add(new FastCodeDataBaseFieldDecorator(new FastCodeDataBaseField(field.getName(), field
						.getType(), field.getValue(), field.getSize(), field.isNullable(), field.getJavaName(), field.getJavaTypeName(),
						field.getTableName(), null), null));
			}

		} else {

			this.fieldInfoForThirdTable.addAll(getEmptyListForNull(this.databaseCache.getTableNameFieldDetailsMap().get(
					schema3 + DOT + thirdTableName)));

		}
		for (final FastCodeDataBaseFieldDecorator fields : this.fieldInfoForThirdTable) {
			createInputForTable(thirdTableName, schema3, fields);
		}
	}

	@Override
	public void create() {
		super.create();

	}

	/**
	 * @param tableName
	 * @param schemaName
	 * @param fastCodeDataBaseFieldDecorator
	 */
	private void createInputForTable(final String tableName, final String schemaName,
			final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator) {

		String fieldName;
		final DataBaseTypeInfo dataBaseTypeInfo = DataBaseTypeInfo.getInstance();
		if (!dataBaseTypeInfo.getPrimaryKeyColumns(schemaName + DOT + tableName).isEmpty()
				&& TableUtil.isPrimaryKey(fastCodeDataBaseFieldDecorator.getName(), schemaName + DOT + tableName)) {
			fieldName = fastCodeDataBaseFieldDecorator.getName() + " *";
		} else {
			fieldName = fastCodeDataBaseFieldDecorator.getName();
		}

		this.fieldListForJoin.add(new DataBaseFieldInfoForJoin(tableName, fieldName, fastCodeDataBaseFieldDecorator.getType(),
				fastCodeDataBaseFieldDecorator.getSize(), fastCodeDataBaseFieldDecorator.isNullable(), TableUtil
						.getFunctionsList(fastCodeDataBaseFieldDecorator.getType())));

	}

	@Override
	protected Control createContents(final Composite parent) {
		final Control contents = super.createContents(parent);
		if (this.tableNames[2] == null) {
			setMessage("Fields selection table for join for  \" " + this.tableNames[0] + " \" and \" " + this.tableNames[1]
					+ " \" relations ", IMessageProvider.INFORMATION);
		} else if (this.tableNames[2] != null) {
			setMessage("Fields selection table for join for  \" " + this.tableNames[0] + " \" , \" " + this.tableNames[1] + " \" and \" "
					+ this.tableNames[2] + " \" relations ", IMessageProvider.INFORMATION);
		}
		return contents;

	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Select Fields");
		final int columnCount = this.fieldListForJoin.size();
		final int height = columnCount > 10 ? columnCount * 35 : columnCount < 5 ? 350 : columnCount * 50;
		shell.setSize(800, height);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		// Composite container = new Composite(parent, SWT.NONE);
		// container.setLayout(new GridLayout(1, false));

		// Create the composite
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		// Add TableColumnLayout
		final TableColumnLayout layout = new TableColumnLayout();
		composite.setLayout(layout);

		this.tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.CHECK);

		this.table = this.tableViewer.getTable();
		this.table.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.table.forceFocus();
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);
		this.tableViewer.setUseHashlookup(true);

		// Creates a column for each attribute of a database field
		for (final String f : selectFieldsJoin) {
			final TableViewerColumn FieldNameColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
			final TableColumn fieldNameFirst = FieldNameColumn.getColumn();
			layout.setColumnData(fieldNameFirst, new ColumnWeightData(8, ColumnWeightData.MINIMUM_WIDTH, true));
			fieldNameFirst.setText(f);
			/*if (f.equals("Alais Name")) {
				FieldNameColumn.setEditingSupport(new AlaisNameEditingSupport(FieldNameColumn.getViewer()));
			}*/
		}

		// column for SQLFunctions
		if (this.isSQLFunctionsRequired) {
			final TableViewerColumn functionsColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
			final TableColumn functionsTitle = functionsColumn.getColumn();

			layout.setColumnData(functionsTitle, new ColumnWeightData(4, ColumnWeightData.MINIMUM_WIDTH, true));
			functionsTitle.setText("Functions");

			// Sets the editing support for combobox
			final EditingSupport exampleEditingSupport = new ComboBoxEditingSupport(functionsColumn.getViewer());
			functionsColumn.setEditingSupport(exampleEditingSupport);
		}

		if (this.isAliasNameRequired) {
			final TableViewerColumn aliasNameColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
			final TableColumn aliasNameTitle = aliasNameColumn.getColumn();

			layout.setColumnData(aliasNameTitle, new ColumnWeightData(8, ColumnWeightData.MINIMUM_WIDTH, true));
			aliasNameTitle.setText("Alias Name");
			aliasNameColumn.setEditingSupport(new AlaisNameEditingSupport(aliasNameColumn.getViewer()));
		}

		// populates the table with data
		final TableLabelProviderForSelectFieldJoin tableLabelProvider = new TableLabelProviderForSelectFieldJoin();

		this.tableViewer.setLabelProvider(tableLabelProvider);
		this.tableViewer.setContentProvider(new ContentProvider());
		this.tableViewer.setInput(this.fieldListForJoin);
		this.tableViewer.refresh();

		this.table.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				if (event.detail == SWT.CHECK) {

					setSelectedFields((DataBaseFieldInfoForJoin) event.item.getData());
				} else {

				}
			}

		});
		configureShell(this.shell);
		return parent;

	}

	/**
	 * @param fieldName
	 * @return
	 */
	public FastCodeDataBaseFieldDecorator getFastCodeDataBaseFieldDecorator(String fieldName) {

		if (fieldName.contains(" *")) {
			fieldName = fieldName.substring(0, fieldName.length() - 2);
		}

		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.fieldInfoForFirstTable) {
			if (fastCodeDataBaseFieldDecorator.getName().equals(fieldName)) {
				return fastCodeDataBaseFieldDecorator;
			}
		}

		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.fieldInfoForSecondTable) {
			if (fastCodeDataBaseFieldDecorator.getName().equals(fieldName)) {
				return fastCodeDataBaseFieldDecorator;
			}
		}

		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.fieldInfoForThirdTable) {
			if (fastCodeDataBaseFieldDecorator.getName().equals(fieldName)) {
				return fastCodeDataBaseFieldDecorator;
			}

		}
		return null;
	}

	/**
	 * @param element
	 */
	protected void setSelectedFields(final DataBaseFieldInfoForJoin element) {

		FastCodeDataBaseFieldDecorator field = TableUtil.getFastCodeDataBaseFieldDecorator(element.getFieldName(),
				getList(element.getTableName()));
		if (this.selectedFields.contains(field)) {
			if (this.selfJoin == true) {
				if (this.tableNames[0].equals(this.tableNames[1])) {
					field = TableUtil.getFastCodeDataBaseFieldDecorator(element.getFieldName(), this.fieldInfoForSecondTable);
				} else {
					field = TableUtil.getFastCodeDataBaseFieldDecorator(element.getFieldName(), this.fieldInfoForThirdTable);
				}
			}

			if (this.selectedFields.contains(field)) {
				this.selectedFields.remove(field);
				this.joinrows.remove(element);
			} else {

				this.selectedFields.add(field);
				this.joinrows.add(element);
			}
		}

		else {

			this.selectedFields.add(field);
			this.joinrows.add(element);
		}

	}

	/**
	 * @param tableName
	 * @return
	 */
	private List<FastCodeDataBaseFieldDecorator> getList(final String tableName) {

		if (this.tableNames[0].equals(tableName)) {
			return this.fieldInfoForFirstTable;
		} else if (this.tableNames[1].equals(tableName)) {
			return this.fieldInfoForSecondTable;
		} else {
			return this.fieldInfoForThirdTable;
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		super.createButtonsForButtonBar(parent);
		final Button selectAllButton = createButton(parent, IDialogConstants.SELECT_ALL_ID, "Select All", false);

		selectAllButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				for (final TableItem tableItem : SelectFieldsSelectionDialogForJoin.this.tableViewer.getTable().getItems()) {
					tableItem.setChecked(true);
				}
				selectAllRows();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 *
	 */

	/**
	 *
	 */
	protected void selectAllRows() {
		for (int i = 0; i < this.fieldListForJoin.size(); i++) {
			this.table.select(i);
			setSelectedFields((DataBaseFieldInfoForJoin) this.tableViewer.getElementAt(i));
		}

	}

	/*
	 * update the field with the function selected
	 */
	protected void updateFunctionsFields() {

		String fieldName = null;
		final SQLFunctionsMapping sqlFuncMapping = SQLFunctionsMapping.getInstance();

		final ArrayList<String> fnames = new ArrayList<String>();
		List<FastCodeDataBaseFieldDecorator> list = new ArrayList<FastCodeDataBaseFieldDecorator>();
		for (final DataBaseFieldInfoForJoin info : this.joinrows) {

			if (info.getFieldName().contains(ASTERISK)) {
				fieldName = info.getFieldName().substring(0, info.getFieldName().length() - 2);
			} else {
				fieldName = info.getFieldName();
			}
			if (fnames == null || fnames != null && !fnames.contains(fieldName)) {
				fnames.add(fieldName);
				list = getList(info.getTableName());
			} else if (fnames != null && fnames.contains(fieldName)) {
				if (this.selfJoin == true) {

					list = this.tableNames[0].equals(this.tableNames[1]) ? this.fieldInfoForSecondTable
							: this.tableNames[2] != null ? this.fieldInfoForThirdTable : null;
				} else {
					list = getList(info.getTableName());
				}
			}

			for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.selectedFields) {

				if (fastCodeDataBaseFieldDecorator.equals(TableUtil.getFastCodeDataBaseFieldDecorator(fieldName, list))) {
					final SQLFunctions func = info.getValue().equals("select") ? null : sqlFuncMapping.getSQLFunction(info.getValue());
					fastCodeDataBaseFieldDecorator.setSQLFunction(func);
					fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().setAlaisName(info.getAlaisName());
					break;
				}

			}

		}
	}

	@Override
	protected void okPressed() {
		if (this.selectedFields.isEmpty()) {
			MessageDialog.openError(null, "Selection Error", "Select one or more fields");
			return;
		}
		updateFunctionsFields();
		super.okPressed();
	}

	/**
	 * @return
	 */
	public List<FastCodeDataBaseFieldDecorator> getSelectedFields() {

		return this.selectedFields;

	}

	private class AlaisNameEditingSupport extends EditingSupport {

		private final TextCellEditor	cellEditor;
		private final ColumnViewer		viewer;

		public AlaisNameEditingSupport(final ColumnViewer viewer) {
			super(viewer);
			this.cellEditor = new TextCellEditor((Composite) getViewer().getControl());
			this.viewer = viewer;
		}

		@Override
		protected CellEditor getCellEditor(final Object element) {
			return this.cellEditor;
		}

		@Override
		protected boolean canEdit(final Object element) {
			return true;
		}

		@Override
		protected Object getValue(final Object element) {
			return ((DataBaseFieldInfoForJoin) element).getAlaisName();
		}

		@Override
		protected void setValue(final Object element, final Object value) {
			if (value != null) {
				((DataBaseFieldInfoForJoin) element).setAlaisName(String.valueOf(value));
				this.viewer.update(element, null);
			}
		}
	}

	/*
	 * editing support class for combo box
	 */
	public class ComboBoxEditingSupport extends EditingSupport {

		private ComboBoxViewerCellEditor	cellEditor	= null;
		ColumnViewer						viewer		= null;

		/*
		 * Creates a new combobox with editing support
		 */
		@SuppressWarnings("deprecation")
		public ComboBoxEditingSupport(final ColumnViewer viewer) {
			super(viewer);
			this.cellEditor = new ComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
			this.cellEditor.setLabelProvider(new LabelProvider());
			this.cellEditor.setContenProvider(new ArrayContentProvider());
			// cellEditor.setInput(Functions.values());
			this.viewer = viewer;
		}

		@Override
		protected CellEditor getCellEditor(final Object element) {
			return this.cellEditor;
		}

		@Override
		protected boolean canEdit(final Object element) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */

		@Override
		protected Object getValue(final Object element) {
			if (element instanceof DataBaseFieldInfoForJoin) {
				final DataBaseFieldInfoForJoin data = (DataBaseFieldInfoForJoin) element;
				this.cellEditor.setInput(data.getFunctions());
				return data.getValue();
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object,
		 * java.lang.Object)
		 */
		@Override
		protected void setValue(final Object element, final Object value) {
			if (value != null && element instanceof DataBaseFieldInfoForJoin && value instanceof String) {
				final DataBaseFieldInfoForJoin data = (DataBaseFieldInfoForJoin) element;
				final String newValue = (String) value;
				// only set new value if it differs from old one

				if (data.getValue() != null && !data.getValue().equals(newValue.trim())) {
					data.setValue(newValue);
					data.setAlaisName(getAlaisName(data.getValue().trim(), data.getFieldName().toLowerCase()));
				}
			}

			else {
				final DataBaseFieldInfoForJoin data = (DataBaseFieldInfoForJoin) element;
				if (data.getFunctions() != null) {
					data.setValue("select");
				}
			}

			this.viewer.refresh();

		}

		/**
		 * @param sqlFunctionName
		 * @param fieldName
		 * @return
		 */
		private String getAlaisName(final String sqlFunctionName, String fieldName) {
			if (!sqlFunctionName.equals("select")) {
				fieldName = removeSpecialChars(fieldName);

				final SQLFunctionsMapping sqlFuncMapping = SQLFunctionsMapping.getInstance();
				final String aliasName = sqlFuncMapping.getSQLFunction(sqlFunctionName).getInsertAt().equals("prefix") ? sqlFunctionName
						+ fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1) : fieldName
						+ sqlFunctionName.substring(0, 1).toUpperCase() + sqlFunctionName.substring(1);
				return aliasName;
			} else {
				return removeSpecialChars(fieldName);
			}

		}

		/**
		 * @param fieldName
		 * @return
		 */
		private String removeSpecialChars(String fieldName) {
			if (fieldName.contains(HASH)) {
				fieldName = fieldName.indexOf(HASH_CHAR) == fieldName.length() - 1 ? fieldName.substring(0, fieldName.length() - 1)
						.toLowerCase() : fieldName.toLowerCase();
			}

			while (fieldName.contains(UNDERSCORE)) {
				fieldName = fieldName.substring(0, fieldName.lastIndexOf(UNDER_SCORE))
						+ fieldName.substring(fieldName.lastIndexOf(UNDER_SCORE) + 1, fieldName.lastIndexOf(UNDER_SCORE) + 2).toUpperCase()
						+ fieldName.substring(fieldName.lastIndexOf(UNDER_SCORE) + 2);
			}
			return fieldName;
		}

	}

}

class DataBaseFieldInfoForJoin {

	private String					tableName;
	private String					fieldName;
	private String					dataType;
	private int						size;
	private boolean					nullable;
	private final ArrayList<String>	functions;
	private String					func;
	private String					alaisName;

	/**
	 * @param tableName
	 * @param fieldName
	 * @param dataType
	 * @param size
	 * @param nullable
	 * @param alaisName
	 * @param functions
	 */
	public DataBaseFieldInfoForJoin(final String tableName, final String fieldName, final String dataType, final int size,
			final boolean nullable, final ArrayList<String> functions) {

		this.tableName = tableName;
		this.fieldName = fieldName;
		this.dataType = dataType;
		this.size = size;
		this.nullable = nullable;
		this.alaisName = updateAlaisName(fieldName);
		this.functions = functions;
		this.func = this.functions != null ? this.functions.get(0) : EMPTY_STR;

	}

	private String updateAlaisName(final String name) {
		String alaisname = name.indexOf(HASH_CHAR) == name.length() - 1 ? name.substring(0, name.length() - 1).toLowerCase() : name
				.toLowerCase();

		while (alaisname.contains(UNDERSCORE)) {
			alaisname = alaisname.substring(0, alaisname.lastIndexOf(UNDER_SCORE))
					+ alaisname.substring(alaisname.lastIndexOf(UNDER_SCORE) + 1, alaisname.lastIndexOf(UNDER_SCORE) + 2).toUpperCase()
					+ alaisname.substring(alaisname.lastIndexOf(UNDER_SCORE) + 2);
		}
		return alaisname.contains(ASTERISK) ? alaisname.replace(ASTERISK, EMPTY_STR) : alaisname;
	}

	public String getAlaisName() {
		return this.alaisName;
	}

	public void setAlaisName(final String alaisName) {
		this.alaisName = alaisName;
	}

	public ArrayList<String> getFunctions() {
		return this.functions;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public void setFieldName(final String fieldName) {
		this.fieldName = fieldName;
	}

	public String getDataType() {
		return this.dataType;
	}

	public void setDataType(final String dataType) {
		this.dataType = dataType;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(final int size) {
		this.size = size;
	}

	public boolean isNullable() {
		return this.nullable;
	}

	public void setNullable(final boolean nullable) {
		this.nullable = nullable;
	}

	public String getValue() {
		if (this.func != null) {
			return this.func.trim();
		} else {
			return EMPTY_STR;
		}
	}

	public void setValue(final String value) {
		this.func = value;
	}

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Fields Selection Table For Join");

		shell.setFullScreen(true);
		final SelectFieldsSelectionDialogForJoin createClassDialog = new SelectFieldsSelectionDialogForJoin(shell);

		createClassDialog.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}

}

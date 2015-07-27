package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.HASH;
import static org.fastcode.common.FastCodeConstants.HASH_CHAR;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.common.FastCodeConstants.UNDER_SCORE;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;
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
import org.eclipse.swt.layout.GridLayout;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeDataBaseFieldDecorator;
import org.fastcode.util.DataBaseTypeInfo;
import org.fastcode.util.DatabaseCache;
import org.fastcode.util.SQLFunctions;
import org.fastcode.util.SQLFunctionsMapping;

public class SelectFieldsSelectionDialog extends TitleAreaDialog {

	private class TableLabelProviderForSelectFields extends LabelProvider implements ITableLabelProvider {

		final int	FIELD_NAME_COLUMN	= 0;
		final int	DATA_TYPE_COLUMN	= 1;
		final int	IS_NULLABLE_COLUMN	= 2;
		final int	ALAIS_NAME			= 4;
		final int	SQLFUNCTIONS_COLUMN	= 3;

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

			final DataBaseFieldInfo dataBaseFieldInfo = (DataBaseFieldInfo) element;
			String result = EMPTY_STR;
			switch (columnIndex) {
			case FIELD_NAME_COLUMN:
				result = dataBaseFieldInfo.getFieldName();
				break;
			case DATA_TYPE_COLUMN:
				result = dataBaseFieldInfo.getDataType();
				break;
			case IS_NULLABLE_COLUMN:
				result = String.valueOf(dataBaseFieldInfo.isNullable());
				break;

			case ALAIS_NAME:
				result = dataBaseFieldInfo.getAlaisName() == null ? EMPTY_STR : dataBaseFieldInfo.getAlaisName();
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

			return ((List<DataBaseFieldInfo>) inputElement).toArray();
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		}
	}

	private String										tableName;

	DatabaseCache										databaseCache			= DatabaseCache.getInstance();
	List<FastCodeDataBaseFieldDecorator>				fieldInfo				= new ArrayList<FastCodeDataBaseFieldDecorator>();

	private final List<DataBaseFieldInfo>				fieldList				= new ArrayList<DataBaseFieldInfo>();

	private final List<FastCodeDataBaseFieldDecorator>	selectedFields			= new ArrayList<FastCodeDataBaseFieldDecorator>();

	Shell												shell;

	final IPreferenceStore								preferenceStore;

	TableViewer											tableViewer;
	Table												table;

	boolean												sqlFunctionsRequired	= false;

	private boolean										isAliasNameRequired;

	private Text										errorMessageText;

	private String										errorMessage;

	private final String								defaultMessage			= EMPTY_STR;

	private String										templateType			= null;

	private Text										messageText;

	private String										schemaName;

	static ArrayList<String>							selectFields			= new ArrayList<String>();

	static {
		selectFields.add("Field Name");
		selectFields.add("Data Type");
		selectFields.add("Nullable");
		//selectFields.add("Alais Name");
	}

	/**
	 * @param shell
	 */
	protected SelectFieldsSelectionDialog(final Shell shell) {
		super(shell);
		this.shell = shell;
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
	}

	/**
	 * @param shell
	 * @param tableName
	 * @param sqlFunctionsRequired
	 * @param templateType
	 * @param isAliasNameRequired
	 * @param schemaName
	 */
	public SelectFieldsSelectionDialog(final Shell shell, final String tableName, final boolean sqlFunctionsRequired,
			final String templateType, final boolean isAliasNameRequired, final String schemaName) {
		super(shell);
		this.shell = shell;
		this.shell.setSize(700, 200);
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		// this.createClassData = createClassData;
		this.sqlFunctionsRequired = sqlFunctionsRequired;
		this.isAliasNameRequired = isAliasNameRequired;
		this.templateType = templateType;
		this.tableName = tableName;
		this.schemaName = schemaName;
		this.fieldInfo.addAll(getEmptyListForNull(this.databaseCache.getTableNameFieldDetailsMap().get(schemaName + DOT + this.tableName)));
		String fieldName;
		final DataBaseTypeInfo dataBaseTypeInfo = DataBaseTypeInfo.getInstance();

		TableUtil.updateSQLFunctionsMapping();

		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.fieldInfo) {
			if (!dataBaseTypeInfo.getPrimaryKeyColumns(this.schemaName + DOT + this.tableName).isEmpty()
					&& TableUtil.isPrimaryKey(fastCodeDataBaseFieldDecorator.getName(), this.schemaName + DOT + this.tableName)) {
				fieldName = fastCodeDataBaseFieldDecorator.getName() + SPACE + ASTERISK;
			} else {
				fieldName = fastCodeDataBaseFieldDecorator.getName();
			}

			this.fieldList.add(new DataBaseFieldInfo(fieldName, fastCodeDataBaseFieldDecorator.getType(), fastCodeDataBaseFieldDecorator
					.getSize(), fastCodeDataBaseFieldDecorator.isNullable(), TableUtil.getFunctionsList(fastCodeDataBaseFieldDecorator
					.getType())));
		}
		// plist.addAll(this.fieldInfo);
	}

	@Override
	public void create() {
		super.create();

	}

	@Override
	protected Control createContents(final Composite parent) {
		final Control contents = super.createContents(parent);
		setMessage("Field selection table for \" " + this.tableName + " \"  relation", IMessageProvider.INFORMATION);

		return contents;
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Select Fields");
		final int columnCount = this.fieldList.size();
		final int height = columnCount > 10 ? columnCount * 35 : columnCount < 5 ? 350 : columnCount * 55;
		shell.setSize(800, height);
		//shell.setSize(800, 700);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);
		createTable(parent);
		return parent;
	}

	/**
	 * @param parent
	 * @return
	 */
	protected Control createTable(final Composite parent) {
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

		// creates a column for each property of a field
		for (final String f : selectFields) {

			final TableViewerColumn viewerColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
			final TableColumn tableColumn = viewerColumn.getColumn();
			layout.setColumnData(tableColumn, new ColumnWeightData(8, ColumnWeightData.MINIMUM_WIDTH, true));
			tableColumn.setText(f);
			/*if (f.equals("Alais Name")) {
				viewerColumn.setEditingSupport(new AlaisNameEditingSupport(viewerColumn.getViewer()));
			}*/

		}

		if (this.sqlFunctionsRequired) {
			// creates a column to select SQLFunctions
			final TableViewerColumn functionsColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
			final TableColumn functionsTitle = functionsColumn.getColumn();

			layout.setColumnData(functionsTitle, new ColumnWeightData(4, ColumnWeightData.MINIMUM_WIDTH, true));
			functionsTitle.setText("Functions");

			// adds editing support to the functions column
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
		// set the label for all the columns and populates the table with the
		// data

		final TableLabelProviderForSelectFields tableLabelProvider = new TableLabelProviderForSelectFields();
		this.tableViewer.setLabelProvider(tableLabelProvider);
		this.tableViewer.setContentProvider(new ContentProvider());
		this.tableViewer.setInput(this.fieldList);
		this.tableViewer.refresh();

		this.table.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				if (event.detail == SWT.CHECK) {

					setSelectedFields((DataBaseFieldInfo) event.item.getData());
					setErrorMessage(SelectFieldsSelectionDialog.this.defaultMessage);

				}
			}

		});

		configureShell(this.shell);
		return parent;

	}

	/**
	 *
	 * @param errorMessage
	 */

	@Override
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

	/*
	 * update the field with the function selected
	 */
	/**
	 *
	 */
	protected void updateFunctionsFields() {

		final SQLFunctionsMapping sqlFuncMapping = SQLFunctionsMapping.getInstance();
		String fieldName = null;
		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.selectedFields) {

			for (final DataBaseFieldInfo info : this.fieldList) {
				if (info.getFieldName().contains(ASTERISK)) {
					fieldName = info.getFieldName().substring(0, info.getFieldName().length() - 2);
				} else {
					fieldName = info.getFieldName();
				}
				if (fastCodeDataBaseFieldDecorator.getName().equals(fieldName)) {
					final SQLFunctions func = info.getValue().trim().equals("select") ? null : sqlFuncMapping.getSQLFunction(info
							.getValue().trim());
					fastCodeDataBaseFieldDecorator.setSQLFunction(func);
					fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().setAlaisName(info.getAlaisName());

				}

			}

		}
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		super.createButtonsForButtonBar(parent);
		final Button selectAllButton = createButton(parent, IDialogConstants.SELECT_ALL_ID, "Select All", false);

		selectAllButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				for (final TableItem tableItem : SelectFieldsSelectionDialog.this.tableViewer.getTable().getItems()) {
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

	protected void selectAllRows() {
		for (int i = 0; i < this.fieldList.size(); i++) {
			this.table.select(i);
			setSelectedFields((DataBaseFieldInfo) this.tableViewer.getElementAt(i));
		}

	}

	/**
	 * @param fieldName
	 * @return
	 */
	public FastCodeDataBaseFieldDecorator getFastCodeDataBaseFieldDecorator(String fieldName) {
		if (fieldName.contains(SPACE + ASTERISK)) {
			fieldName = fieldName.substring(0, fieldName.length() - 2);
		}
		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.fieldInfo) {
			if (fastCodeDataBaseFieldDecorator.getName().equals(fieldName)) {
				return fastCodeDataBaseFieldDecorator;
			}

		}

		return null;
	}

	/**
	 * @param element
	 */
	protected void setSelectedFields(final DataBaseFieldInfo element) {

		final FastCodeDataBaseFieldDecorator field = getFastCodeDataBaseFieldDecorator(element.getFieldName());
		if (this.selectedFields.contains(field)) {
			this.selectedFields.remove(field);
		} else {
			this.selectedFields.add(field);
		}

	}

	@Override
	protected void okPressed() {
		if (this.selectedFields.isEmpty()) {
			MessageDialog.openError(null, "Selection Error", "Select one or more fields");
			return;
		}
		updateFunctionsFields();
		if (this.templateType.contains(P_DATABASE_TEMPLATE_PREFIX + UNDERSCORE + "INSERT")
				|| this.templateType.contains(P_DATABASE_TEMPLATE_PREFIX + UNDERSCORE + "UPDATE")) {
			if (checkNotNullableColumns() != 0) {
				return;
			}
		}
		super.okPressed();
	}

	private int checkNotNullableColumns() {
		final int returnCode = 0;
		final String[] notNullColNames = this.databaseCache.getNotNullColumnListMap().get(this.schemaName + DOT + this.tableName)
				.toArray(new String[0]);
		if (notNullColNames != null) {
			int count = 0;
			for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.selectedFields) {

				final String columnName = fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().getName();
				for (final String notNullColName : notNullColNames) {
					if (notNullColName.equals(columnName)) {
						count++;
					}
				}
			}
			if (count < notNullColNames.length) {

				final MessageDialog dialog = new MessageDialog(null, "Warning", null,
						"All not null Columns are not selected,\n Click Yes to proceed and No to go back ", MessageDialog.WARNING,
						new String[] { "Yes", "No" }, 0) {

					@Override
					protected void buttonPressed(final int buttonId) {
						setReturnCode(buttonId);
						close();

					}
				};

				dialog.open();

				return dialog.getReturnCode();

			}
		}
		return returnCode;
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
			return ((DataBaseFieldInfo) element).getAlaisName();
		}

		@Override
		protected void setValue(final Object element, final Object value) {
			if (value != null) {
				((DataBaseFieldInfo) element).setAlaisName(String.valueOf(value));
				this.viewer.update(element, null);
			}
		}
	}

	/*
	 * editing support class for combobox
	 */
	private class ComboBoxEditingSupport extends EditingSupport {

		private ComboBoxViewerCellEditor	cellEditor	= null;
		ColumnViewer						viewer		= null;

		/*
		 * Creates a new combobox with editing support
		 */
		public ComboBoxEditingSupport(final ColumnViewer viewer) {
			super(viewer);
			this.cellEditor = new ComboBoxViewerCellEditor((Composite) getViewer().getControl(), SWT.READ_ONLY);
			this.cellEditor.setLabelProvider(new LabelProvider());
			this.cellEditor.setContenProvider(new ArrayContentProvider());
			// this.cellEditor.setInput(Functions.values());
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
		 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
		 */

		@Override
		protected Object getValue(final Object element) {
			if (element instanceof DataBaseFieldInfo) {
				final DataBaseFieldInfo data = (DataBaseFieldInfo) element;

				this.cellEditor.setInput(data.getFunctions());
				return data.getValue();
			}

			return null;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
		 */
		@Override
		protected void setValue(final Object element, final Object value) {
			if (value != null && element instanceof DataBaseFieldInfo) {
				final DataBaseFieldInfo data = (DataBaseFieldInfo) element;
				final String newValue = (String) value;
				/* only set new value if it differs from old one */

				if (data.getValue() != null && !data.getValue().equals(newValue.trim())) {
					data.setValues(newValue);
					data.setAlaisName(getAlaisName(data.getValue().trim(), data.getFieldName().toLowerCase()));
				}

			} else {
				final DataBaseFieldInfo data = (DataBaseFieldInfo) element;
				if (data.getFunctions() != null) {
					data.setValues("select");
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

class DataBaseFieldInfo {

	private String				fieldName;
	private String				dataType;
	private int					size;
	private boolean				nullable;
	private String				alaisName;

	private ArrayList<String>	functions;
	private String				func;

	/**
	 * @param name
	 * @param dataType
	 * @param size
	 * @param nullable
	 * @param alaisName
	 * @param funcs
	 */
	public DataBaseFieldInfo(final String name, final String dataType, final int size, final boolean nullable, final ArrayList<String> funcs) {

		this.fieldName = name;
		this.dataType = dataType;
		this.size = size;
		this.nullable = nullable;
		this.alaisName = updateAlaisName(name);
		this.functions = funcs;
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

	public ArrayList<String> getFunctions() {
		return this.functions;
	}

	public void setFunctions(final ArrayList<String> functions) {
		this.functions = functions;
	}

	public String getValue() {
		if (this.func != null) {
			return this.func.trim();
		} else {
			return EMPTY_STR;
		}
	}

	public void setValues(final String value) {
		this.func = value;
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

	public String getAlaisName() {
		return this.alaisName;
	}

	public void setAlaisName(final String alaisName) {
		this.alaisName = alaisName;
	}

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Fields Selection Table");

		shell.setFullScreen(true);
		final SelectFieldsSelectionDialog selectFieldSelectionDialog = new SelectFieldsSelectionDialog(shell);

		selectFieldSelectionDialog.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}
}

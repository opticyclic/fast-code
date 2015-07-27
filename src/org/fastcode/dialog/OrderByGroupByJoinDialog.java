package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeDataBaseFieldDecorator;
import org.fastcode.util.DataBaseTypeInfo;
import org.fastcode.util.DatabaseCache;

public class OrderByGroupByJoinDialog extends TitleAreaDialog {

	private final ArrayList<String>						tableNames		= new ArrayList<String>();

	DatabaseCache										databaseCache	= DatabaseCache.getInstance();

	List<FastCodeDataBaseFieldDecorator>				fieldInfo		= new ArrayList<FastCodeDataBaseFieldDecorator>();

	private final List<FastCodeDataBaseFieldDecorator>	selectedFields	= new ArrayList<FastCodeDataBaseFieldDecorator>();

	private final List<DataBaseFieldJoin>				fieldList		= new ArrayList<DataBaseFieldJoin>();

	Shell												shell;

	final IPreferenceStore								preferenceStore;

	TableViewer											tableViewer;

	Table												table;

	private String										dialogName;

	static ArrayList<String>							selectFieldJoin	= new ArrayList<String>();

	static {

		selectFieldJoin.add("Table Name");
		selectFieldJoin.add("Field Name");

	}

	@SuppressWarnings("deprecation")
	protected OrderByGroupByJoinDialog(final Shell shell) {
		super(shell);
		this.shell = shell;
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
	}

	@SuppressWarnings("deprecation")
	public OrderByGroupByJoinDialog(final Shell shell, final String dialogName, final ArrayList<String> tableNamesWithSchema) {
		super(shell);
		this.shell = shell;
		this.shell.setSize(700, 00);
		this.dialogName = dialogName;
		//this.tableNames = this.tableNames;
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		for (final String tableNameWithSchema : tableNamesWithSchema) {
			this.fieldInfo.addAll(this.databaseCache.getTableNameFieldDetailsMap().get(tableNameWithSchema));
			final String tableName = tableNameWithSchema.substring(tableNameWithSchema.indexOf(DOT) + 1, tableNameWithSchema.length());
			final String schemaName = tableNameWithSchema.substring(0, tableNameWithSchema.indexOf(DOT));
			System.out.println(tableName);
			if (this.databaseCache.getDbTableListMap().containsKey(schemaName)) {
				for (final String tableName1 : this.databaseCache.getDbTableListMap().get(schemaName)) {
					if (tableName1.equals(tableName)) {
						this.tableNames.add(tableName);
					}
				}

			}
			for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.databaseCache.getTableNameFieldDetailsMap()
					.get(tableNameWithSchema)) {

				createInput(tableNameWithSchema, fastCodeDataBaseFieldDecorator);
			}

		}
	}

	@Override
	public void create() {
		super.create();

	}

	/*
	 *
	 * Creates the input for the table
	 *
	 * @param table name
	 *
	 * @param field decorator
	 */
	private void createInput(final String tableNameWithSchema, final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator) {

		String fieldName;
		final DataBaseTypeInfo dataBaseTypeInfo = DataBaseTypeInfo.getInstance();
		if (!dataBaseTypeInfo.getPrimaryKeyColumns(tableNameWithSchema).isEmpty()
				&& TableUtil.isPrimaryKey(fastCodeDataBaseFieldDecorator.getName(), tableNameWithSchema)) {
			fieldName = fastCodeDataBaseFieldDecorator.getName() + " *";
		} else {
			fieldName = fastCodeDataBaseFieldDecorator.getName();
		}
		final String tableName = tableNameWithSchema.substring(tableNameWithSchema.indexOf(DOT) + 1, tableNameWithSchema.length());
		this.fieldList.add(new DataBaseFieldJoin(tableName, fieldName));

	}

	@Override
	protected Control createContents(final Composite parent) {
		final Control contents = super.createContents(parent);

		setMessage(
				"Fields selection table for" + this.dialogName + " for  \" " + this.tableNames.get(0) + " \" and \" "
						+ this.tableNames.get(1) + " \" relations ", IMessageProvider.INFORMATION);

		return contents;
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Create \"Select Field\" Dialog For " + this.dialogName);

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

		// creates a column for each property of a field
		for (final String f : selectFieldJoin) {
			final TableViewerColumn FieldNameColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
			final TableColumn fieldNameFirst = FieldNameColumn.getColumn();
			layout.setColumnData(fieldNameFirst, new ColumnWeightData(8, ColumnWeightData.MINIMUM_WIDTH, true));
			fieldNameFirst.setText(f);
		}

		// populate the table with the data
		final TableLabelProviderForSelectField tableLabelProvider = new TableLabelProviderForSelectField();
		this.tableViewer.setLabelProvider(tableLabelProvider);
		this.tableViewer.setContentProvider(new ContentProvider());
		this.tableViewer.setInput(this.fieldList);
		this.tableViewer.refresh();

		/*this.tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(final SelectionChangedEvent event) {

				setSelectedFields((DataBaseFieldJoin) OrderByGroupByJoinDialog.this.tableViewer.getElementAt(OrderByGroupByJoinDialog.this.table.getSelectionIndex()));

			}

		});*/

		this.table.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				if (event.detail == SWT.CHECK) {
					setSelectedFields((DataBaseFieldJoin) event.item.getData());

				}
			}
		});

		return parent;

	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		super.createButtonsForButtonBar(parent);
		final Button selectAllButton = createButton(parent, IDialogConstants.SELECT_ALL_ID, "Select All", true);

		selectAllButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

				selectAllRows();

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	/*
	 * set the fields selected
	 */
	protected void setSelectedFields(final DataBaseFieldJoin element) {
		// TODO Auto-generated method stub
		boolean elementExists = false;
		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.selectedFields) {
			if (fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().getName().equals(element.getFieldName())) {
				elementExists = true;
				break;
			}

		}
		if (elementExists == false) {
			this.selectedFields.add(TableUtil.getFastCodeDataBaseFieldDecorator(element.getFieldName(), this.fieldInfo));
		}

	}

	public List<FastCodeDataBaseFieldDecorator> getSelectedFields() {

		return this.selectedFields;
	}

	/*
	 * select all the rows in the table
	 */
	protected void selectAllRows() {
		for (int i = 0; i < this.fieldList.size(); i++) {
			this.table.select(i);
			setSelectedFields((DataBaseFieldJoin) this.tableViewer.getElementAt(i));
		}

	}

	@Override
	protected void okPressed() {

		super.okPressed();
	}

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Select Fields Table for Order By and Group By");

		shell.setFullScreen(true);
		final OrderByGroupByDialog dialog = new OrderByGroupByDialog(shell);

		dialog.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}

	/*
	 * label provide class for the table
	 */
	private class TableLabelProviderForSelectField extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {

			return null;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.
		 * lang.Object, int)
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {

			final int TABLE_NAME_COLUMN = 0;
			final int FIELD_NAME_COLUMN = 1;

			final DataBaseFieldJoin dataBaseFieldInfo = (DataBaseFieldJoin) element;
			String result = EMPTY_STR;
			switch (columnIndex) {

			case TABLE_NAME_COLUMN:
				result = dataBaseFieldInfo.getTableName();
				break;

			case FIELD_NAME_COLUMN:
				result = dataBaseFieldInfo.getFieldName();
				break;

			default:

				result = EMPTY_STR;
			}
			return result;
		}

	}

	/*
	 * renders the object as a row in table
	 */
	private static class ContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(final Object inputElement) {

			return ((List<DataBaseFieldJoin>) inputElement).toArray();
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		}
	}

}

class DataBaseFieldJoin {

	private String	tableName;
	private String	fieldName;

	public DataBaseFieldJoin(final String tableName, final String fieldName) {

		this.fieldName = fieldName;
		this.tableName = tableName;

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

}

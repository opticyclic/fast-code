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

public class OrderByGroupByDialog extends TitleAreaDialog {

	private final String[]								tableNames		= new String[2];

	DatabaseCache										databaseCache	= DatabaseCache.getInstance();

	List<FastCodeDataBaseFieldDecorator>				fieldInfo		= new ArrayList<FastCodeDataBaseFieldDecorator>();

	private final List<DataBaseField>					fieldList		= new ArrayList<DataBaseField>();

	private final List<FastCodeDataBaseFieldDecorator>	selectedFields	= new ArrayList<FastCodeDataBaseFieldDecorator>();

	Shell												shell;

	final IPreferenceStore								preferenceStore;

	TableViewer											tableViewer;

	Table												table;

	private String										dialogName;

	private String										tableName;

	static ArrayList<String>							selectField		= new ArrayList<String>();

	private String										schemaName;

	static {

		selectField.add("Field Name");

	}

	@SuppressWarnings("deprecation")
	protected OrderByGroupByDialog(final Shell shell) {
		super(shell);
		this.shell = shell;

		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
	}

	@SuppressWarnings("deprecation")
	public OrderByGroupByDialog(final Shell shell, final String dialogName, final String tableName, final String schemaName) {
		super(shell);
		this.shell = shell;
		this.shell.setSize(700, 00);
		this.dialogName = dialogName;
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		this.tableName = tableName;
		this.schemaName = schemaName;
		this.fieldInfo.addAll(this.databaseCache.getTableNameFieldDetailsMap().get(this.schemaName + DOT + this.tableName));
		createInput();

	}

	@Override
	public void create() {
		super.create();

	}

	/*
	 * creates the input for the table
	 */
	private void createInput() {

		String fieldName;
		final DataBaseTypeInfo dataBaseTypeInfo = DataBaseTypeInfo.getInstance();
		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.fieldInfo) {

			if (!dataBaseTypeInfo.getPrimaryKeyColumns(this.schemaName + DOT + this.tableName).isEmpty()
					&& TableUtil.isPrimaryKey(fastCodeDataBaseFieldDecorator.getName(), this.schemaName + DOT + this.tableName)) {
				fieldName = fastCodeDataBaseFieldDecorator.getName() + " *";
			} else {
				fieldName = fastCodeDataBaseFieldDecorator.getName();
			}

			this.fieldList.add(new DataBaseField(fieldName));
		}

	}

	@Override
	protected Control createContents(final Composite parent) {
		final Control contents = super.createContents(parent);

		setMessage("Fields selection table for" + this.dialogName + " for  \" " + this.tableNames[0] + " \" and \" " + this.tableNames[1]
				+ " \" relations ", IMessageProvider.INFORMATION);

		return contents;
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Create \"Select Field\" Dialog For " + this.dialogName);

	}

	@Override
	protected Control createDialogArea(final Composite parent) {

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

		// create a column for each property of the field
		for (final String f : selectField) {
			final TableViewerColumn FieldNameColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
			final TableColumn fieldNameFirst = FieldNameColumn.getColumn();
			layout.setColumnData(fieldNameFirst, new ColumnWeightData(8, ColumnWeightData.MINIMUM_WIDTH, true));
			fieldNameFirst.setText(f);
		}

		final TableLabelProviderForSelectField tableLabelProvider = new TableLabelProviderForSelectField();
		this.tableViewer.setLabelProvider(tableLabelProvider);
		this.tableViewer.setContentProvider(new ContentProvider());
		this.tableViewer.setInput(this.fieldList);
		this.tableViewer.refresh();

		/*this.tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(final SelectionChangedEvent event) {

				setSelectedFields((DataBaseField) OrderByGroupByDialog.this.tableViewer.getElementAt(OrderByGroupByDialog.this.table
						.getSelectionIndex()));

			}

		});*/

		this.table.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				if (event.detail == SWT.CHECK) {
					setSelectedFields((DataBaseField) event.item.getData());

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
	 * set the fields selected by the user
	 */
	protected void setSelectedFields(final DataBaseField element) {
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

	/*
	 * returns the list of selected fields
	 */
	public List<FastCodeDataBaseFieldDecorator> getSelectedFields() {

		return this.selectedFields;
	}

	/*
	 * selects all the rows in the table
	 */
	protected void selectAllRows() {
		for (int i = 0; i < this.fieldList.size(); i++) {
			this.table.select(i);
			setSelectedFields((DataBaseField) this.tableViewer.getElementAt(i));
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
	 * label provider class for the table
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

			final int FIELD_NAME_COLUMN = 0;

			final DataBaseField dataBaseFieldInfo = (DataBaseField) element;
			String result = EMPTY_STR;
			switch (columnIndex) {

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

			return ((List<DataBaseField>) inputElement).toArray();
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		}
	}

}

class DataBaseField {

	private String	fieldName;

	public DataBaseField(final String fieldName) {

		this.fieldName = fieldName;

	}

	public String getFieldName() {
		return this.fieldName;
	}

	public void setFieldName(final String fieldName) {
		this.fieldName = fieldName;
	}
}

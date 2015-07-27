package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.BETWEEN_COLUMN;
import static org.fastcode.common.FastCodeConstants.CHECKED;
import static org.fastcode.common.FastCodeConstants.DISABLED_CHECKBOX;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EQUAL_TO_COLUMN;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.GREATER_THAN_COLUMN;
import static org.fastcode.common.FastCodeConstants.IN_COLUMN;
import static org.fastcode.common.FastCodeConstants.LESS_THAN_COLUMN;
import static org.fastcode.common.FastCodeConstants.LIKE_COLUMN;
import static org.fastcode.common.FastCodeConstants.NOT_EQUAL_TO_COLUMN;
import static org.fastcode.common.FastCodeConstants.NOT_LIKE_COLUMN;
import static org.fastcode.common.FastCodeConstants.NOT_NULLABLE_COLUMN;
import static org.fastcode.common.FastCodeConstants.NULLABLE_COLUMN;
import static org.fastcode.common.FastCodeConstants.ORACLE;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.UNCHECKED;
import static org.fastcode.util.SourceUtil.getImagefromFCCacheMap;
import static org.fastcode.util.SourceUtil.populateFCCacheEntityImageMap;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.Activator;
import org.fastcode.common.FastCodeConstants.Qualifier;
import org.fastcode.common.FastCodeDataBaseField;
import org.fastcode.common.FastCodeDataBaseFieldDecorator;
import org.fastcode.popup.actions.snippet.FastCodeCache;
import org.fastcode.preferences.DatabaseConnectionSettings;
import org.fastcode.util.DataBaseTypeInfo;
import org.fastcode.util.DatabaseCache;

public class WhereFieldTableDialog extends TitleAreaDialog {

	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {

			final int EQUAL_TO_COLUMN = 1;
			final int NOT_EQUAL_TO_COLUMN = 2;
			final int LESS_THAN_COLUMN = 3;
			final int GREATER_THAN_COLUMN = 4;
			final int NULLABLE_COLUMN = 5;
			final int NOT_NULLABLE_COLUMN = 6;
			final int IN_COLUMN = 7;
			final int BETWEEN_COLUMN = 8;
			final int LIKE_COLUMN = 9;
			final int NOT_LIKE_COLUMN = 10;

			FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator = new FastCodeDataBaseFieldDecorator();

			fastCodeDataBaseFieldDecorator = getFastCodeDataBaseFieldDecorator(((WhereQualifier) element).getFieldName());
			final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
			Image checked = null;
			Image unchecked = null;
			Image disabled = null;
			if (fastCodeCache.getEntityImageMap().containsKey(CHECKED)) {
				checked = getImagefromFCCacheMap(CHECKED);
			} else {
				checked = Activator.getDefault().getImageRegistry().get(CHECKED);
				populateFCCacheEntityImageMap(CHECKED, checked);
			}
			if (fastCodeCache.getEntityImageMap().containsKey(UNCHECKED)) {
				checked = getImagefromFCCacheMap(UNCHECKED);
			} else {
				unchecked = Activator.getDefault().getImageRegistry().get(UNCHECKED);
				populateFCCacheEntityImageMap(UNCHECKED, unchecked);
			}
			if (fastCodeCache.getEntityImageMap().containsKey(DISABLED_CHECKBOX)) {
				checked = getImagefromFCCacheMap(DISABLED_CHECKBOX);
			} else {
				disabled = Activator.getDefault().getImageRegistry().get(DISABLED_CHECKBOX);
				populateFCCacheEntityImageMap(DISABLED_CHECKBOX, disabled);
			}
			Image returnImage;

			final WhereQualifier whereQualifier = (WhereQualifier) element;

			switch (columnIndex) {

			case EQUAL_TO_COLUMN:
				if (!whereQualifier.isNotEqualTo() && !whereQualifier.isNotLike() && !whereQualifier.isLike() && !whereQualifier.isIn()
						&& !whereQualifier.isBetween() && !whereQualifier.isNullable() && !whereQualifier.isNotNullable()
						&& !whereQualifier.isGreaterThan() && !whereQualifier.isLessThan()) {
					returnImage = whereQualifier.isEqualTo() ? checked : unchecked;
				} else {
					returnImage = disabled;
				}
				break;
			case NOT_EQUAL_TO_COLUMN:
				if (!whereQualifier.isEqualTo() && !whereQualifier.isNotLike() && !whereQualifier.isLike() && !whereQualifier.isIn()
						&& !whereQualifier.isBetween() && !whereQualifier.isNullable() && !whereQualifier.isNotNullable()
						&& !whereQualifier.isGreaterThan() && !whereQualifier.isLessThan()) {
					returnImage = whereQualifier.isNotEqualTo() ? checked : unchecked;
				} else {
					returnImage = disabled;
				}
				break;
			case LESS_THAN_COLUMN:
				if (!whereQualifier.isEqualTo() && !whereQualifier.isNotEqualTo() && !whereQualifier.isNotLike()
						&& !whereQualifier.isLike() && !whereQualifier.isIn() && !whereQualifier.isBetween()
						&& !whereQualifier.isNullable() && !whereQualifier.isNotNullable() && !whereQualifier.isGreaterThan()) {
					returnImage = whereQualifier.isLessThan() ? checked : unchecked;
				} else {
					returnImage = disabled;
				}
				if (CheckForStringDataTypes(fastCodeDataBaseFieldDecorator)) {
					returnImage = disabled;
				}

				break;
			case GREATER_THAN_COLUMN:
				if (!whereQualifier.isEqualTo() && !whereQualifier.isNotEqualTo() && !whereQualifier.isNotLike()
						&& !whereQualifier.isLike() && !whereQualifier.isIn() && !whereQualifier.isBetween()
						&& !whereQualifier.isNullable() && !whereQualifier.isNotNullable() && !whereQualifier.isLessThan()) {
					returnImage = whereQualifier.isGreaterThan() ? checked : unchecked;
				} else {
					returnImage = disabled;
				}
				if (CheckForStringDataTypes(fastCodeDataBaseFieldDecorator)) {
					returnImage = disabled;
				}
				break;
			case NULLABLE_COLUMN:
				if (!getFastCodeDataBaseFieldDecorator(whereQualifier.getFieldName()).getFastCodeDataBaseField().isNullable()
						|| whereQualifier.isEqualTo() || whereQualifier.isNotEqualTo() || whereQualifier.isLike()
						|| whereQualifier.isNotLike() || whereQualifier.isIn() || whereQualifier.isBetween()
						|| whereQualifier.isNotNullable() || whereQualifier.isGreaterThan() || whereQualifier.isLessThan()) {
					returnImage = disabled;
				} else {
					returnImage = whereQualifier.isNullable() ? checked : unchecked;
				}
				break;
			case NOT_NULLABLE_COLUMN:
				if (!getFastCodeDataBaseFieldDecorator(whereQualifier.getFieldName()).getFastCodeDataBaseField().isNullable()
						|| whereQualifier.isEqualTo() || whereQualifier.isNotEqualTo() || whereQualifier.isLike()
						|| whereQualifier.isNotLike() || whereQualifier.isIn() || whereQualifier.isBetween() || whereQualifier.isNullable()
						|| whereQualifier.isGreaterThan() || whereQualifier.isLessThan()) {
					returnImage = disabled;
				}

				else {
					returnImage = whereQualifier.isNotNullable() ? checked : unchecked;
				}
				break;
			case IN_COLUMN:
				if (!whereQualifier.isEqualTo() && !whereQualifier.isNotEqualTo() && !whereQualifier.isNotLike()
						&& !whereQualifier.isLike() && !whereQualifier.isBetween() && !whereQualifier.isNullable()
						&& !whereQualifier.isNotNullable() && !whereQualifier.isGreaterThan() && !whereQualifier.isLessThan()) {
					returnImage = whereQualifier.isIn() ? checked : unchecked;
				} else {
					returnImage = disabled;
				}
				break;
			case BETWEEN_COLUMN:
				if (!whereQualifier.isEqualTo() && !whereQualifier.isNotEqualTo() && !whereQualifier.isNotLike() && !whereQualifier.isIn()
						&& !whereQualifier.isLike() && !whereQualifier.isNullable() && !whereQualifier.isNotNullable()
						&& !whereQualifier.isGreaterThan() && !whereQualifier.isLessThan()) {
					returnImage = whereQualifier.isBetween() ? checked : unchecked;
				} else {
					returnImage = disabled;
				}
				if (CheckForStringDataTypes(fastCodeDataBaseFieldDecorator)) {
					returnImage = disabled;
				}
				break;
			case LIKE_COLUMN:
				if (!whereQualifier.isEqualTo() && !whereQualifier.isNotEqualTo() && !whereQualifier.isNotLike() && !whereQualifier.isIn()
						&& !whereQualifier.isBetween() && !whereQualifier.isNullable() && !whereQualifier.isNotNullable()
						&& !whereQualifier.isGreaterThan() && !whereQualifier.isLessThan()) {
					returnImage = whereQualifier.isLike() ? checked : unchecked;
				} else {
					returnImage = disabled;
				}
				if (CheckForIntegerDataTypes(fastCodeDataBaseFieldDecorator)) {
					returnImage = disabled;
				}

				break;
			case NOT_LIKE_COLUMN:
				if (!whereQualifier.isEqualTo() && !whereQualifier.isNotEqualTo() && !whereQualifier.isLike() && !whereQualifier.isIn()
						&& !whereQualifier.isBetween() && !whereQualifier.isNullable() && !whereQualifier.isNotNullable()
						&& !whereQualifier.isGreaterThan() && !whereQualifier.isLessThan()) {
					returnImage = whereQualifier.isNotLike() ? checked : unchecked;
				} else {
					returnImage = disabled;
				}
				if (CheckForIntegerDataTypes(fastCodeDataBaseFieldDecorator)) {
					returnImage = disabled;

				}
				break;

			default:
				// should not reach here
				returnImage = null;

			}

			return returnImage;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final int FIELD_NAME = 0;
			final WhereQualifier whereQualifier = (WhereQualifier) element;
			String result = "";
			switch (columnIndex) {
			case FIELD_NAME:
				result = whereQualifier.getFieldName();
				break;
			default:

				result = "";
			}
			return result;
		}

	}

	private static class ContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(final Object inputElement) {

			return ((List<WhereQualifier>) inputElement).toArray();
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		}
	}

	private String										tableName;

	DatabaseCache										databaseCache	= DatabaseCache.getInstance();
	List<FastCodeDataBaseFieldDecorator>				fieldInfo		= new ArrayList<FastCodeDataBaseFieldDecorator>();

	private final List<WhereQualifier>					fieldList		= new ArrayList<WhereQualifier>();

	private final List<FastCodeDataBaseFieldDecorator>	selectedFields	= new ArrayList<FastCodeDataBaseFieldDecorator>();

	Shell												shell;

	final IPreferenceStore								preferenceStore;

	TableViewer											tableViewer;
	Table												table;

	static ArrayList<String>							whereFields		= new ArrayList<String>();

	static {
		whereFields.add("field Name");
		whereFields.add("Equal To");
		whereFields.add("Not Equal To");
		whereFields.add("Less Than");
		whereFields.add("Greater Than");
		whereFields.add("Is Null");
		whereFields.add("Is Not NUll");
		whereFields.add("In");
		whereFields.add("Between");
		whereFields.add("Like");
		whereFields.add("Not Like");
	}

	/**
	 * @param shell
	 */
	protected WhereFieldTableDialog(final Shell shell) {
		super(shell);
		this.shell = shell;

		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
	}

	/**
	 * @param shell
	 * @param tableName
	 * @param schemaName
	 */
	public WhereFieldTableDialog(final Shell shell, final String tableName, final String schemaName) {
		super(shell);
		this.shell = shell;
		//this.shell.setSize(700, 200);
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		this.tableName = tableName;
		this.fieldInfo.addAll(this.databaseCache.getTableNameFieldDetailsMap().get(schemaName + DOT + this.tableName));
		String fieldName;
		final DataBaseTypeInfo dataBaseTypeInfo = DataBaseTypeInfo.getInstance();
		final DatabaseConnectionSettings dbconn = DatabaseConnectionSettings.getInstance();
		if (dbconn.getTypesofDabases().toLowerCase().equals(ORACLE)) {
			this.fieldInfo.add(new FastCodeDataBaseFieldDecorator(new FastCodeDataBaseField("rownum", "NUMBER", "Integer"), null));
		}
		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.fieldInfo) {
			if (!dataBaseTypeInfo.getPrimaryKeyColumns(schemaName + DOT + this.tableName).isEmpty()
					&& TableUtil.isPrimaryKey(fastCodeDataBaseFieldDecorator.getName(), schemaName + DOT + this.tableName)) {
				fieldName = fastCodeDataBaseFieldDecorator.getName() + SPACE + ASTERISK;
			} else {
				fieldName = fastCodeDataBaseFieldDecorator.getName();
			}
			this.fieldList.add(new WhereQualifier(fieldName, false, false, false, false, false, false, false, false, false, false));
		}

	}

	@Override
	public void create() {
		super.create();

	}

	/**
	 * @param fastCodeDataBaseFieldDecorator
	 * @return
	 */
	public boolean CheckForIntegerDataTypes(final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator) {
		final String dataType = fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().getType().toLowerCase();
		if (dataType.equals("int") || dataType.equals("integer") || dataType.equals("decimal") || dataType.equals("double")
				|| dataType.equals("float") || dataType.equals("real") || dataType.equals("mediumint") || dataType.equals("time")
				|| dataType.equals("date") || dataType.equals("datatime") || dataType.equals("timestamp") || dataType.equals("year")
				|| dataType.equals("number")) {
			return true;

		}
		return false;

	}

	/**
	 * @param fastCodeDataBaseFieldDecorator
	 * @return
	 */
	public boolean CheckForStringDataTypes(final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator) {
		final String dataType = fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().getType().toLowerCase();
		if (dataType.equals("varchar") || dataType.equals("text") || dataType.equals("character") || dataType.equals("character varying")
				|| dataType.equals("varchar2") || dataType.equals("char") || dataType.equals("character")) {
			return true;
		}
		return false;
	}

	@Override
	protected Control createContents(final Composite parent) {
		final Control contents = super.createContents(parent);

		setMessage("Where field qualfier selection table", IMessageProvider.INFORMATION);

		return contents;
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Where Fields");
		shell.setSize(900, 500);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		final TableColumnLayout layout = new TableColumnLayout();
		composite.setLayout(layout);

		this.tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);

		this.table = this.tableViewer.getTable();
		this.table.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.table.forceFocus();
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);
		this.tableViewer.setUseHashlookup(true);

		// Creates the input for the table
		int index = -1;
		for (final String f : whereFields) {
			final TableViewerColumn fieldNameColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
			final TableColumn fieldNameFirst = fieldNameColumn.getColumn();
			layout.setColumnData(fieldNameFirst, new ColumnWeightData(8, ColumnWeightData.MINIMUM_WIDTH, true));
			fieldNameFirst.setText(f);
			final EditingSupport likeEditingSupport = new CheckboxEditingSupport(fieldNameColumn.getViewer(), index);
			fieldNameColumn.setEditingSupport(likeEditingSupport);
			index++;

		}

		// populate the table with the data
		final TableLabelProvider tableLabelProvider = new TableLabelProvider();

		this.tableViewer.setLabelProvider(tableLabelProvider);
		this.tableViewer.setContentProvider(new ContentProvider());
		this.tableViewer.setInput(this.fieldList);
		this.tableViewer.refresh();

		this.table.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				updateSelectedFields((WhereQualifier) WhereFieldTableDialog.this.tableViewer.getElementAt(WhereFieldTableDialog.this.table
						.getSelectionIndex()));

				setSelectedFields((WhereQualifier) WhereFieldTableDialog.this.tableViewer.getElementAt(WhereFieldTableDialog.this.table
						.getSelectionIndex()));
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				// TODO Auto-generated method stub

			}

		});
		return parent;

	}

	/**
	 * @param element
	 */
	protected void updateSelectedFields(final WhereQualifier element) {
		FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator = new FastCodeDataBaseFieldDecorator();

		fastCodeDataBaseFieldDecorator = getFastCodeDataBaseFieldDecorator(element.getFieldName());

		Qualifier qual = null;
		if (element.isEqualTo()) {
			qual = Qualifier.EQUALTO;
		} else if (element.isNotEqualTo()) {
			qual = Qualifier.NOTEQUALTO;
		} else if (element.isLessThan()) {
			qual = Qualifier.LESSTHAN;
		} else if (element.isGreaterThan()) {
			qual = Qualifier.GREATERTHAN;
		} else if (element.isNullable()) {
			qual = Qualifier.NULLABLE;
		} else if (element.isNotNullable()) {
			qual = Qualifier.NOTNULLABLE;
		} else if (element.isIn()) {
			qual = Qualifier.IN;
		} else if (element.isBetween()) {
			qual = Qualifier.BETWEEN;
		} else if (element.isLike()) {
			qual = Qualifier.LIKE;
		} else if (element.isNotLike()) {
			qual = Qualifier.NOTLIKE;
		}

		fastCodeDataBaseFieldDecorator.setWhereQualifier(qual);

	}

	/**
	 * @param fieldName
	 * @return
	 */
	public FastCodeDataBaseFieldDecorator getFastCodeDataBaseFieldDecorator(String fieldName) {
		if (fieldName.contains(" *")) {
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
	protected void setSelectedFields(final WhereQualifier element) {
		boolean elementExists = false;
		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.selectedFields) {
			if (fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().getName().equals(element.getFieldName())) {
				elementExists = true;
				if (!element.isEqualTo() || !element.isNotEqualTo() || !element.isLike() || !element.isNotLike() || !element.isIn()
						|| !element.isBetween() || !element.isNotNullable() || !element.isNullable()

						|| !element.isGreaterThan() || !element.isLessThan()) {

					this.selectedFields.remove(getFastCodeDataBaseFieldDecorator(element.getFieldName()));

				}

				break;
			}

		}
		if (elementExists == false) {
			if (element.isEqualTo() || element.isNotEqualTo() || element.isLike() || element.isNotLike() || element.isIn()
					|| element.isBetween() || element.isNotNullable() || element.isNullable() || element.isGreaterThan()
					|| element.isLessThan()) {
				this.selectedFields.add(getFastCodeDataBaseFieldDecorator(element.getFieldName()));
			}
		}
		//

	}

	@Override
	protected void okPressed() {
		if (this.selectedFields.isEmpty()) {
			MessageDialog.openError(null, "Selection Error", "Select one or more fields ");
			return;
		}
		super.okPressed();
	}

	/**
	 * @return
	 */
	public List<FastCodeDataBaseFieldDecorator> getSelectedFields() {

		return this.selectedFields;

	}

	private class CheckboxEditingSupport extends EditingSupport {
		private CheckboxCellEditor	cellEditor1	= null;
		private final ColumnViewer	viewer;
		private final int			columnIndex;

		/**
		 * @param viewer
		 * @param columnIndex
		 */
		CheckboxEditingSupport(final ColumnViewer viewer, final int columnIndex) {
			super(viewer);
			this.cellEditor1 = new CheckboxCellEditor((Composite) getViewer().getControl(), SWT.CHECK | SWT.READ_ONLY);
			this.viewer = viewer;
			this.columnIndex = columnIndex;

		}

		@Override
		protected CellEditor getCellEditor(final Object element) {
			return this.cellEditor1;
		}

		@Override
		protected boolean canEdit(final Object element) {

			return true;
		}

		@Override
		protected Object getValue(final Object element) {

			final WhereQualifier whereQualifier = (WhereQualifier) element;

			if (element instanceof WhereQualifier) {

				switch (this.columnIndex) {
				case EQUAL_TO_COLUMN:
					return whereQualifier.isEqualTo();

				case NOT_EQUAL_TO_COLUMN:
					return whereQualifier.isNotEqualTo();
				case LESS_THAN_COLUMN:
					return whereQualifier.isLessThan();
				case GREATER_THAN_COLUMN:
					return whereQualifier.isGreaterThan();

				case NULLABLE_COLUMN:
					return whereQualifier.isNullable();

				case NOT_NULLABLE_COLUMN:
					return whereQualifier.isNotNullable();

				case IN_COLUMN:
					return whereQualifier.isIn();

				case BETWEEN_COLUMN:
					return whereQualifier.isBetween();

				case LIKE_COLUMN:
					return whereQualifier.isLike();

				case NOT_LIKE_COLUMN:
					return whereQualifier.isNotLike();

				default:

					break;

				}
				;
			}
			return null;
		}

		@Override
		protected void setValue(final Object element, final Object value) {

			FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator = new FastCodeDataBaseFieldDecorator();

			fastCodeDataBaseFieldDecorator = getFastCodeDataBaseFieldDecorator(((WhereQualifier) element).getFieldName());

			if (element instanceof WhereQualifier) {
				final WhereQualifier data = (WhereQualifier) element;

				switch (this.columnIndex) {
				case EQUAL_TO_COLUMN:

					if (data.isNotEqualTo() || data.isLike() || data.isNotLike() || data.isNullable() || data.isNotNullable()
							|| data.isIn() || data.isBetween() || data.isGreaterThan() || data.isLessThan()) {
						data.setEqualTo(false);
					} else {
						data.setEqualTo((Boolean) value);
					}
					break;
				case NOT_EQUAL_TO_COLUMN:
					if (data.isEqualTo() || data.isLike() || data.isNotLike() || data.isNullable() || data.isNotNullable() || data.isIn()
							|| data.isBetween() || data.isGreaterThan() || data.isLessThan()) {
						data.setNotEqualTo(false);
					} else {
						data.setNotEqualTo((Boolean) value);
					}
					break;

				case LESS_THAN_COLUMN:
					if (CheckForStringDataTypes(fastCodeDataBaseFieldDecorator) || data.isEqualTo() || data.isNotEqualTo() || data.isLike()
							|| data.isNotLike() || data.isNullable() || data.isNotNullable() || data.isIn() || data.isBetween()
							|| data.isGreaterThan()) {
						data.setLessThan(false);
					} else {
						data.setLessThan((Boolean) value);
					}
					break;
				case GREATER_THAN_COLUMN:
					if (CheckForStringDataTypes(fastCodeDataBaseFieldDecorator) || data.isEqualTo() || data.isNotEqualTo() || data.isLike()
							|| data.isNotLike() || data.isNullable() || data.isNotNullable() || data.isIn() || data.isBetween()
							|| data.isLessThan()) {
						data.setGreaterThan(false);
					} else {
						data.setGreaterThan((Boolean) value);
					}
					break;

				case NULLABLE_COLUMN:
					if (!fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().isNullable() || data.isEqualTo() || data.isLike()
							|| data.isNotLike() || data.isNotEqualTo() || data.isNotNullable() || data.isIn() || data.isBetween()
							|| data.isGreaterThan() || data.isLessThan()) {
						data.setNullable(false);
					} else {
						data.setNullable((Boolean) value);
					}
					break;
				case NOT_NULLABLE_COLUMN:
					if (!fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().isNullable() || data.isEqualTo() || data.isLike()
							|| data.isNotLike() || data.isNullable() || data.isIn() || data.isBetween() || data.isGreaterThan()
							|| data.isLessThan()) {
						data.setNotNullable(false);
					} else {
						data.setNotNullable((Boolean) value);
					}
					break;
				case IN_COLUMN:
					if (data.isEqualTo() || data.isNotEqualTo() || data.isLike() || data.isNotLike() || data.isNullable()
							|| data.isNotNullable() || data.isBetween() || data.isGreaterThan() || data.isLessThan()) {
						data.setIn(false);
					} else {
						data.setIn((Boolean) value);
					}
					break;
				case BETWEEN_COLUMN:
					if (CheckForStringDataTypes(fastCodeDataBaseFieldDecorator) || data.isEqualTo() || data.isNotEqualTo() || data.isLike()
							|| data.isNotLike() || data.isNullable() || data.isNotNullable() || data.isIn() || data.isGreaterThan()
							|| data.isLessThan()) {
						data.setBetween(false);
					} else {
						data.setBetween((Boolean) value);
					}
					break;
				case LIKE_COLUMN:
					if (CheckForIntegerDataTypes(fastCodeDataBaseFieldDecorator) || data.isNotLike() || data.isEqualTo()
							|| data.isNotEqualTo() || data.isNullable() || data.isIn() || data.isBetween() || data.isNotNullable()
							|| data.isGreaterThan() || data.isLessThan()) {
						data.setLike(false);
					} else {
						data.setLike((Boolean) value);
					}
					break;
				case NOT_LIKE_COLUMN:
					if (CheckForIntegerDataTypes(fastCodeDataBaseFieldDecorator) || data.isLike() || data.isEqualTo()
							|| data.isNotEqualTo() || data.isNotNullable() || data.isNullable() || data.isIn() || data.isBetween()
							|| data.isGreaterThan() || data.isLessThan()) {
						data.setNotLike(false);
					} else {
						data.setNotLike((Boolean) value);
					}
					break;
				default:
					break;

				}

			}

			this.viewer.refresh();
		}

	}
}

class WhereQualifier {

	private boolean	like;
	private boolean	notLike;
	private boolean	equalTo;
	private boolean	notEqualTo;
	private boolean	nullable;
	private boolean	notNullable;
	private boolean	between;
	private boolean	in;
	private boolean	lessThan;

	private boolean	greaterThan;

	private String	fieldName;

	/**
	 * @param name
	 * @param like
	 * @param notLike
	 * @param equalTo
	 * @param notEqualTo
	 * @param nullable
	 * @param notNullable
	 * @param between
	 * @param in
	 * @param lessThan
	 * @param greaterThan
	 */
	public WhereQualifier(final String name, final boolean like, final boolean notLike, final boolean equalTo, final boolean notEqualTo,
			final boolean nullable, final boolean notNullable, final boolean between, final boolean in, final boolean lessThan,
			final boolean greaterThan) {

		this.fieldName = name;
		this.like = like;
		this.notLike = notLike;
		this.equalTo = equalTo;
		this.notEqualTo = notEqualTo;
		this.nullable = nullable;
		this.notNullable = notNullable;
		this.between = between;
		this.in = in;
		this.lessThan = lessThan;
		this.greaterThan = greaterThan;
	}

	public boolean isLessThan() {
		return this.lessThan;
	}

	public void setLessThan(final boolean lessThan) {
		this.lessThan = lessThan;
	}

	public boolean isGreaterThan() {
		return this.greaterThan;
	}

	public void setGreaterThan(final boolean greaterThan) {
		this.greaterThan = greaterThan;
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public void setFieldName(final String fieldName) {
		this.fieldName = fieldName;
	}

	public boolean isNotLike() {
		return this.notLike;
	}

	public void setNotLike(final boolean notLike) {
		this.notLike = notLike;
	}

	public boolean isEqualTo() {
		return this.equalTo;
	}

	public void setEqualTo(final boolean equalTo) {
		this.equalTo = equalTo;
	}

	public boolean isNotEqualTo() {
		return this.notEqualTo;
	}

	public void setNotEqualTo(final boolean notEqualTo) {
		this.notEqualTo = notEqualTo;
	}

	public boolean isNullable() {
		return this.nullable;
	}

	public void setNullable(final boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isNotNullable() {
		return this.notNullable;
	}

	public void setNotNullable(final boolean notNullable) {
		this.notNullable = notNullable;
	}

	public boolean isBetween() {
		return this.between;
	}

	public void setBetween(final boolean between) {
		this.between = between;
	}

	public boolean isIn() {
		return this.in;
	}

	public void setIn(final boolean in) {
		this.in = in;
	}

	public boolean isLike() {
		return this.like;
	}

	public void setLike(final boolean like) {
		this.like = like;
	}

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Where Fields Table");

		shell.setFullScreen(true);
		final WhereFieldTableDialog createClassDialog = new WhereFieldTableDialog(shell);

		createClassDialog.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}
}

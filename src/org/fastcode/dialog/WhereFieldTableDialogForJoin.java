package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.BETWEEN_COLUMN;
import static org.fastcode.common.FastCodeConstants.CHECKED;
import static org.fastcode.common.FastCodeConstants.DISABLED_CHECKBOX;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
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
import static org.fastcode.common.FastCodeConstants.UNCHECKED;
import static org.fastcode.util.FastCodeUtil.getEmptyListForNull;
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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
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
import org.fastcode.util.DataBaseTypeInfo;
import org.fastcode.util.DatabaseCache;

public class WhereFieldTableDialogForJoin extends TitleAreaDialog {

	private class TableLabelProviderForJoin extends LabelProvider implements ITableLabelProvider {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {

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
			final int EQUAL_TO_COLUMN = 2;
			final int NOT_EQUAL_TO_COLUMN = 3;
			final int LESS_THAN_COLUMN = 4;
			final int GREATER_THAN_COLUMN = 5;
			final int NULLABLE_COLUMN = 6;
			final int NOT_NULLABLE_COLUMN = 7;
			final int IN_COLUMN = 8;
			final int BETWEEN_COLUMN = 9;
			final int LIKE_COLUMN = 10;
			final int NOT_LIKE_COLUMN = 11;

			final WhereQualifier whereQualifier = ((WhereQualifierForJoin) element).getQualifier();
			FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator = new FastCodeDataBaseFieldDecorator();

			fastCodeDataBaseFieldDecorator = getFastCodeDataBaseFieldDecorator(whereQualifier.getFieldName());

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
				if (CheckForStringDataTypes(fastCodeDataBaseFieldDecorator)) {
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
				} else {
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
				;
			}

			return returnImage;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final int TABLE_NAME = 0;
			final int FIELD_NAME = 1;
			final WhereQualifierForJoin qualifierForJoin = (WhereQualifierForJoin) element;
			String result = EMPTY_STR;
			switch (columnIndex) {
			case TABLE_NAME:
				result = qualifierForJoin.getTableName();
				break;
			case FIELD_NAME:
				result = qualifierForJoin.getQualifier().getFieldName();
				break;
			default:

				result = EMPTY_STR;
			}
			return result;
		}

		public void setComposite(final Composite composite) {
		}
	}

	private static class ContentProvider implements IStructuredContentProvider {
		@Override
		public Object[] getElements(final Object inputElement) {

			return ((List<WhereQualifierForJoin>) inputElement).toArray();
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		}
	}

	DatabaseCache										databaseCache			= DatabaseCache.getInstance();
	List<FastCodeDataBaseFieldDecorator>				fieldInfoForFirstTable	= new ArrayList<FastCodeDataBaseFieldDecorator>();
	List<FastCodeDataBaseFieldDecorator>				fieldInfoForSecondTable	= new ArrayList<FastCodeDataBaseFieldDecorator>();
	List<FastCodeDataBaseFieldDecorator>				fieldInfoForThirdTable	= new ArrayList<FastCodeDataBaseFieldDecorator>();
	private final List<WhereQualifierForJoin>			fieldListForJoin		= new ArrayList<WhereQualifierForJoin>();

	private final List<FastCodeDataBaseFieldDecorator>	selectedFields			= new ArrayList<FastCodeDataBaseFieldDecorator>();

	Shell												shell;

	final IPreferenceStore								preferenceStore;

	TableViewer											tableViewer;
	Table												table;
	static ArrayList<String>							whereFieldsJoin			= new ArrayList<String>();

	static {
		whereFieldsJoin.add("Table Name");
		whereFieldsJoin.add("Field Name");
		whereFieldsJoin.add("Equal To");
		whereFieldsJoin.add("Not Equal To");
		whereFieldsJoin.add("Less Than");
		whereFieldsJoin.add("Greater Than");
		whereFieldsJoin.add("Is Null");
		whereFieldsJoin.add("Is Not NUll");
		whereFieldsJoin.add("In");
		whereFieldsJoin.add("Between");
		whereFieldsJoin.add("Like");
		whereFieldsJoin.add("Not Like");
	}

	private final String[]								tableNames				= new String[3];
	private boolean										selfJoin;

	/**
	 * @param shell
	 */
	protected WhereFieldTableDialogForJoin(final Shell shell) {
		super(shell);
		this.shell = shell;

		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
	}

	/**
	 * @param shell
	 * @param firstTableName
	 * @param secondTableName
	 * @param schema2
	 * @param schema1
	 */
	public WhereFieldTableDialogForJoin(final Shell shell, final String firstTableName, final String secondTableName, final String schema1,
			final String schema2) {
		super(shell);
		this.shell = shell;
		this.shell.setSize(700, 200);
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);

		this.tableNames[0] = firstTableName;
		this.tableNames[1] = secondTableName;
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
	public WhereFieldTableDialogForJoin(final Shell shell, final String firstTableName, final String secondTableName,
			final String thirdTableName, final String schema1, final String schema2, final String schema3) {
		super(shell);
		this.shell = shell;
		//this.shell.setSize(700, 300);
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		this.tableNames[0] = firstTableName;
		this.tableNames[1] = secondTableName;
		this.tableNames[2] = thirdTableName;
		this.fieldInfoForFirstTable.addAll(getEmptyListForNull(this.databaseCache.getTableNameFieldDetailsMap().get(
				schema1 + DOT + firstTableName)));
		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.fieldInfoForFirstTable) {
			createInputForTable(firstTableName, schema1, fastCodeDataBaseFieldDecorator);
			/*this.fieldListForJoin.add(new WhereQualifierForJoin(firstTableName, new WhereQualifier(fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField()
					.getName(), false, false, false, false, false, false, false, false, false, false)));*/
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
			/*this.fieldListForJoin.add(new WhereQualifierForJoin(secondTableName, new WhereQualifier(fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField()
					.getName(), false, false, false, false, false, false, false, false, false, false)));*/
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

		for (final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator : this.fieldInfoForThirdTable) {
			createInputForTable(thirdTableName, schema3, fastCodeDataBaseFieldDecorator);
			/*this.fieldListForJoin.add(new WhereQualifierForJoin(thirdTableName, new WhereQualifier(fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField()
					.getName(), false, false, false, false, false, false, false, false, false, false)));*/
		}
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

		this.fieldListForJoin.add(new WhereQualifierForJoin(tableName, new WhereQualifier(fieldName, false, false, false, false, false,
				false, false, false, false, false)));

	}

	@Override
	public void create() {
		super.create();

	}

	public boolean CheckForIntegerDataTypes(final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator) {
		final String dataType = fastCodeDataBaseFieldDecorator.getType().toLowerCase();
		if (dataType.equals("int") || dataType.equals("integer") || dataType.equals("decimal") || dataType.equals("double")
				|| dataType.equals("float") || dataType.equals("real") || dataType.equals("mediumint") || dataType.equals("time")
				|| dataType.equals("date") || dataType.equals("datatime") || dataType.equals("timestamp") || dataType.equals("year")
				|| dataType.equals("number")) {
			return true;

		}
		return false;

	}

	public boolean CheckForStringDataTypes(final FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator) {
		final String dataType = fastCodeDataBaseFieldDecorator.getType().toLowerCase();
		if (dataType.equals("varchar") || dataType.equals("text") || dataType.equals("character varying") || dataType.equals("varchar2")
				|| dataType.equals("char") || dataType.equals("character")) {
			return true;
		}
		return false;
	}

	@Override
	protected Control createContents(final Composite parent) {
		final Control contents = super.createContents(parent);
		if (this.tableNames[2] == null) {
			setMessage("Where field qualfier selection table for Join for \" " + this.tableNames[0] + " \" and \" " + this.tableNames[1]
					+ " \" relations ", IMessageProvider.INFORMATION);
		} else if (this.tableNames[2] != null) {
			setMessage("Where field qualfier selection table for Join for \" " + this.tableNames[0] + " \" , \" " + this.tableNames[1]
					+ " \" and \" " + this.tableNames[2] + " \" relations ", IMessageProvider.INFORMATION);
		}

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

		// creates a column for each attribute
		int index = -2;
		for (final String f : whereFieldsJoin) {
			final TableViewerColumn fieldNameColumn = new TableViewerColumn(this.tableViewer, SWT.NONE);
			final TableColumn fieldNameFirst = fieldNameColumn.getColumn();
			layout.setColumnData(fieldNameFirst, new ColumnWeightData(8, ColumnWeightData.MINIMUM_WIDTH, true));
			fieldNameFirst.setText(f);
			final EditingSupport likeEditingSupport = new CheckBoxEditingSupport(fieldNameColumn.getViewer(), index,
					this.fieldInfoForFirstTable, this.fieldInfoForSecondTable, this.fieldInfoForThirdTable);
			fieldNameColumn.setEditingSupport(likeEditingSupport);
			index++;

		}

		// populates the table with the data

		final TableLabelProviderForJoin tableLabelProviderForJoin = new TableLabelProviderForJoin();
		tableLabelProviderForJoin.setComposite(composite);
		this.tableViewer.setLabelProvider(tableLabelProviderForJoin);

		this.tableViewer.setContentProvider(new ContentProvider());

		this.tableViewer.setInput(this.fieldListForJoin);

		this.tableViewer.refresh();

		this.tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {

				setSelectedFields((WhereQualifierForJoin) WhereFieldTableDialogForJoin.this.tableViewer
						.getElementAt(WhereFieldTableDialogForJoin.this.table.getSelectionIndex()));

			}

		});

		return parent;

	}

	/**
	 * @param element
	 * @param field
	 */
	protected void updateSelectedFields(final WhereQualifierForJoin element, final FastCodeDataBaseFieldDecorator field) {
		// FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator = new
		// FastCodeDataBaseFieldDecorator();
		//
		// fastCodeDataBaseFieldDecorator = TableUtil
		// .getFastCodeDataBaseFieldDecorator(element.getQualifier()
		// .getFieldName(), this.fieldInfoForFirstTable,
		// this.fieldInfoForSecondTable,this.fieldInfoForThirdTable);
		Qualifier qual = null;

		if (element.getQualifier().isEqualTo()) {
			qual = Qualifier.EQUALTO;
		} else if (element.getQualifier().isNotEqualTo()) {
			qual = Qualifier.NOTEQUALTO;
		} else if (element.getQualifier().isLessThan()) {
			qual = Qualifier.LESSTHAN;
		} else if (element.getQualifier().isGreaterThan()) {
			qual = Qualifier.GREATERTHAN;
		} else if (element.getQualifier().isNullable()) {
			qual = Qualifier.NULLABLE;
		} else if (element.getQualifier().isNotNullable()) {
			qual = Qualifier.NOTNULLABLE;
		} else if (element.getQualifier().isIn()) {
			qual = Qualifier.IN;
		} else if (element.getQualifier().isBetween()) {
			qual = Qualifier.BETWEEN;
		} else if (element.getQualifier().isLike()) {
			qual = Qualifier.LIKE;
		} else if (element.getQualifier().isNotLike()) {
			qual = Qualifier.NOTLIKE;
		}

		field.setWhereQualifier(qual);

	}

	/**
	 * @param fieldName
	 * @return
	 */
	private FastCodeDataBaseFieldDecorator getFastCodeDataBaseFieldDecorator(String fieldName) {
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
	protected void setSelectedFields(final WhereQualifierForJoin element) {
		FastCodeDataBaseFieldDecorator field = TableUtil.getFastCodeDataBaseFieldDecorator(element.getQualifier().getFieldName(),
				getList(element.getTableName()));
		if (this.selectedFields.contains(field)) {
			if (this.selfJoin == true) {
				if (this.tableNames[0].equals(this.tableNames[1])) {
					field = TableUtil
							.getFastCodeDataBaseFieldDecorator(element.getQualifier().getFieldName(), this.fieldInfoForSecondTable);
				} else {
					field = TableUtil.getFastCodeDataBaseFieldDecorator(element.getQualifier().getFieldName(), this.fieldInfoForThirdTable);
				}
			}
			if (this.selectedFields.contains(field)) {
				this.selectedFields.remove(field);

			} else {

				updateSelectedFields(element, field);
				this.selectedFields.add(field);

			}

		}

		else {
			updateSelectedFields(element, field);
			this.selectedFields.add(field);

		}
	}

	private List<FastCodeDataBaseFieldDecorator> getList(final String tableName) {

		if (this.tableNames[0].equals(tableName)) {
			return this.fieldInfoForFirstTable;
		} else if (this.tableNames[1].equals(tableName)) {
			return this.fieldInfoForSecondTable;
		} else {
			return this.fieldInfoForThirdTable;
		}

	}

	/**
	 * @return
	 */
	public List<FastCodeDataBaseFieldDecorator> getSelectedFields() {

		return this.selectedFields;

	}

	@Override
	protected void okPressed() {
		if (this.selectedFields.isEmpty()) {
			MessageDialog.openError(null, "Selection Error", "Select one or more fields ");
			return;
		}
		super.okPressed();
	}

	/*
	 * editing support class for checkbox
	 */

	public class CheckBoxEditingSupport extends EditingSupport {
		private CheckboxCellEditor				cellEditor	= null;
		private final ColumnViewer				viewer;
		private final int						columnIndex;
		List<FastCodeDataBaseFieldDecorator>	fieldInfoForFirstTable;
		List<FastCodeDataBaseFieldDecorator>	fieldInfoForSecondTable;
		List<FastCodeDataBaseFieldDecorator>	fieldInfoForThirdTable;

		/*
		 * creates a new checkbox with editing support
		 */
		public CheckBoxEditingSupport(final ColumnViewer viewer, final int columnIndex,
				final List<FastCodeDataBaseFieldDecorator> fieldInfoForFirstTable,
				final List<FastCodeDataBaseFieldDecorator> fieldInfoForSecondTable,
				final List<FastCodeDataBaseFieldDecorator> fieldInfoForThirdTable) {
			super(viewer);
			this.cellEditor = new CheckboxCellEditor((Composite) getViewer().getControl(), SWT.CHECK | SWT.READ_ONLY);
			this.viewer = viewer;
			this.columnIndex = columnIndex;
			this.fieldInfoForFirstTable = fieldInfoForFirstTable;
			this.fieldInfoForSecondTable = fieldInfoForSecondTable;
			this.fieldInfoForThirdTable = fieldInfoForThirdTable;

		}

		public CheckBoxEditingSupport(final ColumnViewer viewer, final int columnIndex, final List<FastCodeDataBaseFieldDecorator> fieldInfo) {
			super(viewer);
			this.cellEditor = new CheckboxCellEditor((Composite) getViewer().getControl(), SWT.CHECK | SWT.READ_ONLY);
			this.viewer = viewer;
			this.columnIndex = columnIndex;

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

			final WhereQualifier whereQualifier = ((WhereQualifierForJoin) element).getQualifier();

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

			FastCodeDataBaseFieldDecorator fastCodeDataBaseFieldDecorator = new FastCodeDataBaseFieldDecorator();
			WhereQualifier whereQualifier = null;

			if (element instanceof WhereQualifierForJoin) {
				final WhereQualifierForJoin whereQualifierForJoin = (WhereQualifierForJoin) element;
				whereQualifier = whereQualifierForJoin

				.getQualifier();
				fastCodeDataBaseFieldDecorator = TableUtil.getFastCodeDataBaseFieldDecorator(((WhereQualifierForJoin) element)
						.getQualifier().getFieldName(), this.fieldInfoForFirstTable, this.fieldInfoForSecondTable,
						this.fieldInfoForThirdTable);

			}
			switch (this.columnIndex) {
			case EQUAL_TO_COLUMN:

				if (whereQualifier.isNotEqualTo() || whereQualifier.isLike() || whereQualifier.isNotLike() || whereQualifier.isNullable()
						|| whereQualifier.isNotNullable() || whereQualifier.isIn() || whereQualifier.isBetween()
						|| whereQualifier.isGreaterThan() || whereQualifier.isLessThan()) {
					whereQualifier.setEqualTo(false);
				} else {
					whereQualifier.setEqualTo((Boolean) value);
				}
				break;
			case NOT_EQUAL_TO_COLUMN:
				if (whereQualifier.isEqualTo() || whereQualifier.isLike() || whereQualifier.isNotLike() || whereQualifier.isNullable()
						|| whereQualifier.isNotNullable() || whereQualifier.isIn() || whereQualifier.isBetween()

						|| whereQualifier.isGreaterThan() || whereQualifier.isLessThan()) {
					whereQualifier.setNotEqualTo(false);
				} else {
					whereQualifier.setNotEqualTo((Boolean) value);
				}
				break;
			case LESS_THAN_COLUMN:
				if (TableUtil.isStringDataType(fastCodeDataBaseFieldDecorator)

				|| whereQualifier.isEqualTo() || whereQualifier.isNotEqualTo() || whereQualifier.isLike() || whereQualifier.isNotLike()
						|| whereQualifier.isNullable() || whereQualifier.isNotNullable() || whereQualifier.isIn()
						|| whereQualifier.isBetween() || whereQualifier.isGreaterThan()) {
					whereQualifier.setLessThan(false);
				} else {
					whereQualifier.setLessThan((Boolean) value);
				}
				break;
			case GREATER_THAN_COLUMN:
				if (TableUtil.isStringDataType(fastCodeDataBaseFieldDecorator) || whereQualifier.isEqualTo()
						|| whereQualifier.isNotEqualTo() || whereQualifier.isLike() || whereQualifier.isNotLike()
						|| whereQualifier.isNullable() || whereQualifier.isNotNullable() || whereQualifier.isIn()
						|| whereQualifier.isBetween() || whereQualifier.isLessThan()) {
					whereQualifier.setGreaterThan(false);
				} else {
					whereQualifier.setGreaterThan((Boolean) value);
				}
				break;

			case NULLABLE_COLUMN:
				if (!fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().isNullable() || whereQualifier.isEqualTo()
						|| whereQualifier.isLike() || whereQualifier.isNotLike() || whereQualifier.isNotEqualTo()
						|| whereQualifier.isNotNullable() || whereQualifier.isIn() || whereQualifier.isBetween()
						|| whereQualifier.isGreaterThan() || whereQualifier.isLessThan()) {
					whereQualifier.setNullable(false);
				} else {
					whereQualifier.setNullable((Boolean) value);
				}
				break;
			case NOT_NULLABLE_COLUMN:
				if (!fastCodeDataBaseFieldDecorator.getFastCodeDataBaseField().isNullable() || whereQualifier.isEqualTo()
						|| whereQualifier.isLike() || whereQualifier.isNotLike() || whereQualifier.isNullable() || whereQualifier.isIn()
						|| whereQualifier.isBetween() || whereQualifier.isGreaterThan() || whereQualifier.isLessThan()) {
					whereQualifier.setNotNullable(false);
				} else {
					whereQualifier.setNotNullable((Boolean) value);
				}
				break;
			case IN_COLUMN:
				if (whereQualifier.isEqualTo() || whereQualifier.isNotEqualTo() || whereQualifier.isLike() || whereQualifier.isNotLike()
						|| whereQualifier.isNullable() || whereQualifier.isNotNullable() || whereQualifier.isBetween()
						|| whereQualifier.isGreaterThan() || whereQualifier.isLessThan()) {
					whereQualifier.setIn(false);
				} else {
					whereQualifier.setIn((Boolean) value);
				}
				break;
			case BETWEEN_COLUMN:
				if (TableUtil.isStringDataType(fastCodeDataBaseFieldDecorator) || whereQualifier.isEqualTo()
						|| whereQualifier.isNotEqualTo() || whereQualifier.isLike() || whereQualifier.isNotLike()
						|| whereQualifier.isNullable() || whereQualifier.isNotNullable() || whereQualifier.isIn()
						|| whereQualifier.isGreaterThan() || whereQualifier.isLessThan()) {
					whereQualifier.setBetween(false);
				} else {
					whereQualifier.setBetween((Boolean) value);
				}
				break;
			case LIKE_COLUMN:
				if (TableUtil.isIntegerDataType(fastCodeDataBaseFieldDecorator) || whereQualifier.isNotLike() || whereQualifier.isNotLike()
						|| whereQualifier.isEqualTo() || whereQualifier.isNotEqualTo() || whereQualifier.isNullable()
						|| whereQualifier.isIn() || whereQualifier.isBetween() || whereQualifier.isNotNullable()
						|| whereQualifier.isGreaterThan() || whereQualifier.isLessThan()) {
					whereQualifier.setLike(false);
				} else {
					whereQualifier.setLike((Boolean) value);
				}
				break;
			case NOT_LIKE_COLUMN:
				if (TableUtil.isIntegerDataType(fastCodeDataBaseFieldDecorator) || whereQualifier.isLike() || whereQualifier.isEqualTo()
						|| whereQualifier.isNotEqualTo() || whereQualifier.isNotNullable() || whereQualifier.isNullable()
						|| whereQualifier.isIn() || whereQualifier.isBetween() || whereQualifier.isGreaterThan()
						|| whereQualifier.isLessThan()) {
					whereQualifier.setNotLike(false);
				} else {
					whereQualifier.setNotLike((Boolean) value);
				}
				break;
			default:
				break;

			}

			this.viewer.refresh();

		}

	}

}

class WhereQualifierForJoin {

	private String			tableName;
	private WhereQualifier	qualifier;

	/**
	 * @param tableName
	 * @param whereQualifier
	 */
	public WhereQualifierForJoin(final String tableName, final WhereQualifier whereQualifier) {
		this.tableName = tableName;
		this.qualifier = whereQualifier;
	}

	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

	public WhereQualifier getQualifier() {
		return this.qualifier;
	}

	public void setQualifier(final WhereQualifier qualifier) {
		this.qualifier = qualifier;
	}

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Where Fields Table For Join");

		shell.setFullScreen(true);
		final WhereFieldTableDialogForJoin whereFieldTableDialog = new WhereFieldTableDialogForJoin(shell);

		whereFieldTableDialog.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		display.dispose();
	}
}

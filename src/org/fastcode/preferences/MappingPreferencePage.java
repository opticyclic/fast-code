/*
 * Fast Code Plugin for Eclipse
 *
 * Copyright (C) 2008  Gautam Dev
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA.
 *
 * Plugin Home Page: http://fast-code.sourceforge.net/
 */

package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.mapping.MappingDefinitionList.columnNames;
import static org.fastcode.util.StringUtil.createEmbeddedInstance;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.mapping.IMappingListViewer;
import org.fastcode.mapping.MappingDefinition;
import org.fastcode.mapping.MappingDefinitionList;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class MappingPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	TableViewer	tableViewer;

	/**
	 *
	 */
	public MappingPreferencePage() {
		super();
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		setPreferenceStore(store);
		setDescription("Preference Page For Jumping Between Similar Classes or Configuration.");

	}

	@Override
	public Control createContents(final Composite parent) {
		final Composite entryTable = new Composite(parent, SWT.NULL);
		final GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		entryTable.setLayoutData(data);

		final GridLayout layout = new GridLayout();
		entryTable.setLayout(layout);

		final Label label = new Label(entryTable, SWT.NONE);
		label.setText("Please Define The Mapping For Jumping Between Similar Classes Or Configurations");

		final Table table = new Table(entryTable, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION);

		// ILabelDecorator decorator =
		// PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();

		this.tableViewer = new TableViewer(table);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		final MappingDefinitionList mappingDefinitionList = MappingDefinitionList.getInstance();

		this.tableViewer.setColumnProperties(columnNames);

		// 1st column
		TableColumn column = new TableColumn(table, SWT.CENTER, 0);
		column.setText(createEmbeddedInstance(columnNames[0]));
		column.setWidth(120);

		// 2nd column
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(createEmbeddedInstance(columnNames[1]) + " Pattern");
		column.setWidth(280);
		// Add listener to column so tasks are sorted by description when
		// clicked

		/*
		 * column.addSelectionListener(new SelectionAdapter() {
		 *
		 * public void widgetSelected(SelectionEvent e) {
		 * tableViewer.setSorter(new
		 * ExampleTaskSorter(ExampleTaskSorter.DESCRIPTION)); } });
		 */

		// 3rd column
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(createEmbeddedInstance(columnNames[2]) + " Pattern");
		column.setWidth(280);
		// Add listener to column so tasks are sorted by owner when clicked
		/*
		 * column.addSelectionListener(new SelectionAdapter() {
		 *
		 * public void widgetSelected(SelectionEvent e) {
		 * tableViewer.setSorter(new
		 * ExampleTaskSorter(ExampleTaskSorter.OWNER)); } });
		 */

		this.tableViewer.setContentProvider(new MappingDefinitionContentProvider(mappingDefinitionList));
		this.tableViewer.setLabelProvider(new MappingBuilderLabelProvider());
		// tableViewer.setLabelProvider(new TableDecoratingLabelProvider(new
		// MappingBuilderLabelProvider(), decorator));
		// tableViewer.setInput(this);
		this.tableViewer.setInput(mappingDefinitionList);

		final CellEditor[] editors = new CellEditor[columnNames.length];
		editors[0] = new TextCellEditor(table);
		editors[1] = new TextCellEditor(table);
		editors[2] = new TextCellEditor(table);

		this.tableViewer.setCellEditors(editors);
		this.tableViewer.setCellModifier(new MappingCellModifier(mappingDefinitionList));

		// Create and configure the "Add" button
		final Button addButton = new Button(entryTable, SWT.PUSH | SWT.CENTER);
		addButton.setText("Add New Mapping");

		final GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 125;
		addButton.setLayoutData(gridData);
		addButton.setEnabled(false);
		addButton.addSelectionListener(new SelectionAdapter() {

			// Add a task to the ExampleTaskList and refresh the view
			@Override
			public void widgetSelected(final SelectionEvent e) {
				mappingDefinitionList.addMappingDefinition(new MappingDefinition("New Category", "New Source", "New Destination"));
			}
		});

		return entryTable;
	}

	@Override
	public boolean performOk() {
		return super.performOk();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(final IWorkbench workbench) {
	}

	private class TableDecoratingLabelProvider extends DecoratingLabelProvider implements ITableLabelProvider {

		ITableLabelProvider	provider;
		ILabelDecorator		decorator;

		/**
		 * @param provider
		 * @param decorator
		 */
		public TableDecoratingLabelProvider(final ILabelProvider provider, final ILabelDecorator decorator) {
			super(provider, decorator);
			this.provider = (ITableLabelProvider) provider;
			this.decorator = decorator;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java
		 * .lang.Object, int)
		 */
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			final Image image = this.provider.getColumnImage(element, columnIndex);
			if (this.decorator != null) {
				final Image decorated = this.decorator.decorateImage(image, element);
				if (decorated != null) {
					return decorated;
				}
			}
			return image;
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
			final String text = this.provider.getColumnText(element, columnIndex);
			if (this.decorator != null) {
				final String decorated = this.decorator.decorateText(text, element);
				if (decorated != null) {
					return decorated;
				}
			}
			return text;
		}
	}

	class MappingDefinitionContentProvider implements IStructuredContentProvider, IMappingListViewer {

		private final List<MappingDefinition>	mappingDefinitions;

		public MappingDefinitionContentProvider(final MappingDefinitionList mappingDefinitionList) {
			this.mappingDefinitions = mappingDefinitionList.getMappingDefinitions();
		}

		@Override
		public void dispose() {
		}

		/**
		 * @param viewer
		 * @param oldInput
		 * @param newInput
		 *
		 */
		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			if (newInput != null) {
				((MappingDefinitionList) newInput).addChangeListener(this);
			}
			if (oldInput != null) {
				((MappingDefinitionList) oldInput).removeChangeListener(this);
			}
		}

		/**
		 *
		 * @param mappingDefinition
		 */
		@Override
		public void addMappingDefinition(final MappingDefinition mappingDefinition) {
			//
			MappingPreferencePage.this.tableViewer.add(mappingDefinition);
		}

		/**
		 * @param mappingDefinition
		 */
		@Override
		public void removeMappingDefinition(final MappingDefinition mappingDefinition) {
			MappingPreferencePage.this.tableViewer.remove(mappingDefinition);
		}

		/**
		 * @param mappingDefinition
		 */
		@Override
		public void updateMappingDefinition(final MappingDefinition mappingDefinition) {
			MappingPreferencePage.this.tableViewer.update(mappingDefinition, null);
		}

		/**
		 * @param inputElement
		 */
		@Override
		public Object[] getElements(final Object inputElement) {
			return this.mappingDefinitions.toArray();
		}
	}

	/**
	 *
	 * @author Gautam
	 *
	 */
	private class MappingBuilderLabelProvider extends LabelProvider implements ITableLabelProvider {
		// private static class MappingBuilderLabelProvider extends
		// LabelProvider {

		/**
		 * @param element
		 * @param columnIndex
		 *
		 */
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			if (element instanceof MappingDefinition) {
				switch (columnIndex) {
				case 0:
					return ((MappingDefinition) element).getCategory();
				case 1:
					return ((MappingDefinition) element).getSource();
				case 2:
					return ((MappingDefinition) element).getDestination();
				default:
					break;
				}

			}
			return null;
		}

		/**
		 * @param element
		 *
		 */
		@Override
		public Image getImage(final Object element) {
			return null;
		}

		/**
		 * @param element
		 *
		 */
		@Override
		public String getText(final Object element) {
			if (element instanceof MappingDefinition) {
				return ((MappingDefinition) element).getDestination();
			}
			return null;
		}

		/**
		 * @param element
		 * @param property
		 *
		 */
		@Override
		public boolean isLabelProperty(final Object element, final String property) {
			if (element instanceof MappingDefinition) {
				if (property.equals("category") || property.equals("source") || property.equals("destination")) {
					return true;
				}
			}
			return false;
		}

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}
	}

	private class MappingCellModifier implements ICellModifier {
		private final MappingDefinitionList	mappingDefinitionList;

		public MappingCellModifier(final MappingDefinitionList mappingDefinitionList) {
			this.mappingDefinitionList = mappingDefinitionList;
		}

		@Override
		public boolean canModify(final Object element, final String property) {
			final int columnIndex = Arrays.asList(MappingDefinitionList.columnNames).indexOf(property);

			return columnIndex > 0;
		}

		@Override
		public Object getValue(final Object element, final String property) {
			Object result = null;
			final int columnIndex = Arrays.asList(MappingDefinitionList.columnNames).indexOf(property);

			final MappingDefinition mappingDefinition = (MappingDefinition) element;

			switch (columnIndex) {
			case 0: //
				result = mappingDefinition.getCategory();
				break;
			case 1: //
				result = mappingDefinition.getSource();
				break;
			case 2: //
				result = mappingDefinition.getDestination();
				break;
			default:
				result = EMPTY_STR;
			}
			return result;
		}

		/**
		 * @param element
		 * @param property
		 * @param value
		 *
		 */
		@Override
		public void modify(final Object element, final String property, final Object value) {
			final int columnIndex = Arrays.asList(MappingDefinitionList.columnNames).indexOf(property);

			final TableItem tableItem = (TableItem) element;
			final MappingDefinition mappingDefinition = (MappingDefinition) tableItem.getData();

			switch (columnIndex) {
			case 0:
				mappingDefinition.setCategory((String) value);
				break;
			case 1:
				mappingDefinition.setSource((String) value);
				break;
			case 2:
				mappingDefinition.setDestination((String) value);
				break;
			default:
				break;
			}
			this.mappingDefinitionList.updateMappingDefinition(mappingDefinition);
		}

	}

}

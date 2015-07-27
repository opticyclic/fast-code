/**
 *
 */
package org.fastcode.common;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author gdev
 *
 */
public class DatabaseFieldSelectionDialog extends FastCodeSelectionDialog {
	List<Object>	columns	= new ArrayList<Object>();
	//private Button	upButton;
	//private Button	downButton;

	/**
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param elements
	 * @param multipleSelection
	 */
	public DatabaseFieldSelectionDialog(final Shell parent, final String title, final String message, final Object[] elements,
			final boolean multipleSelection) {
		super(parent, title, message, elements, 0, multipleSelection);
		for (final Object column : elements) {
			this.columns.add(column);
		}
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		super.createButtonsForButtonBar(parent);
		final Button selectAllButton = createButton(parent, IDialogConstants.SELECT_ALL_ID, "Select All", false);

		selectAllButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

				DatabaseFieldSelectionDialog.this.setSelection(DatabaseFieldSelectionDialog.this.columns.toArray(new String[0]));

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		/*this.upButton = createButton(parent, UPBUTTONID, "Up", true);
		if (getSelectionIndex() == 0) {
			this.upButton.setEnabled(false);
		} else {
			this.upButton.setEnabled(true);
		}
		this.upButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(final SelectionEvent event) {
				final int index = DatabaseFieldSelectionDialog.this.getSelectionIndex();
				if (index != 0) {
					DatabaseFieldSelectionDialog.this.swap(true);
					DatabaseFieldSelectionDialog.this.setListElements(DatabaseFieldSelectionDialog.this.columns.toArray());
		//System.out.println(DatabaseFieldSelectionDialog.this.setListElements(DatabaseFieldSelectionDialog.this.columns.toArray()));
					//System.out.println(DatabaseFieldSelectionDialog.this.setListElements(DatabaseFieldSelectionDialog.this.columns.toArray()));
					//System.out.println(DatabaseFieldSelectionDialog.this.getResult());
				}//DatabaseFieldSelectionDialog.this.setReturnCode(UPBUTTONID);
			}

			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.downButton = createButton(parent, DOWNBUTTONID, "Down", true);
		if (getSelectionIndex() == this.columns.size() - 1) {
			this.downButton.setEnabled(false);
		} else {
			this.downButton.setEnabled(true);
		}
		this.downButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(final SelectionEvent event) {
				final int index = DatabaseFieldSelectionDialog.this.getSelectionIndex();
				if (index != DatabaseFieldSelectionDialog.this.columns.size() - 1) {
					DatabaseFieldSelectionDialog.this.swap(false);
					//DatabaseFieldSelectionDialog.this.setReturnCode(DOWNBUTTONID);
				}
			}

			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});*/
	}

	/*protected void swap(final boolean up) {
		//this.setPresentDefaultValue(false);
		final int index = getSelectionIndex();
		final int target = up ? index - 1 : index + 1;
		final Object obj = this.columns.get(index);
		final Object obj1 = this.columns.get(target);
		this.columns.remove(index);
		this.columns.remove(target);
		this.columns.add(index, obj1);
		this.columns.add(target, obj);
		System.out.println(this.columns);
		if (index > 0) {
			String[] selection = Arrays.asList(this.getSelectedElements()).toArray(new String[0]);
			Assert.isTrue(selection.length == 1);
			this.columns.remove(index);
			this.columns.add(target, selection[0]);
			this.columns.set(target, selection[0]);

		}
		selectionChanged();
	}*/

	/*@Override
	protected void buttonPressed(final int buttonId) {

		super.buttonPressed(buttonId);
		setReturnCode(buttonId);
		setElements(this.columns.toArray());
	}*/
	/*private void selectionChanged() {
		final int index = getSelectionIndex();
		final int size = this.columns.size();
		this.upButton.setEnabled(size > 1 && index > 0);
		this.downButton.setEnabled(size > 1 && index >= 0 && index < size - 1);
	}
*/
	/*private void setPresentDefaultValue(final boolean b) {
		// TODO Auto-generated method stub

	}*/
}

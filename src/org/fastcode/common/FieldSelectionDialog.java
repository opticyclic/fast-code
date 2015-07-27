/**
 *
 */
package org.fastcode.common;

import static org.eclipse.jdt.core.IJavaElement.FIELD;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IField;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Gautam
 *
 */
public class FieldSelectionDialog extends FastCodeSelectionDialog {
	List<Object>	fields	= new ArrayList<Object>();

	/**
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param elements
	 * @param multipleSelection
	 */
	public FieldSelectionDialog(final Shell parent, final String title, final String message, final IField[] elements,
			final boolean multipleSelection) {
		super(parent, title, message, elements, FIELD, multipleSelection);
		for (final Object column : elements) {
			this.fields.add(column);
		}
	}

	/**
	 *
	 * @param parent
	 * @param title
	 * @param message
	 * @param fastcodefield
	 * @param multipleSelection
	 */
	public FieldSelectionDialog(final Shell parent, final String title, final String message, final FastCodeField[] elements,
			final boolean multipleSelection) {
		super(parent, title, message, elements, -1, multipleSelection);
		for (final Object column : elements) {
			this.fields.add(column);
		}
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		super.createButtonsForButtonBar(parent);

		final Button selectAllButton = createButton(parent, IDialogConstants.SELECT_ALL_ID, "Select All", false);

		selectAllButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				FieldSelectionDialog.this.setSelection(FieldSelectionDialog.this.fields.toArray(new FastCodeField[0]));
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

}

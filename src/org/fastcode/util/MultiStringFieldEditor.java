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

package org.fastcode.util;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class MultiStringFieldEditor extends StringFieldEditor {
	/**
	 * Text limit of text field in characters; initially unlimited.
	 */
	private final int		textLimit			= UNLIMITED;
	private boolean	readOnly			= false;

	/**
	 * The validation strategy;
	 * <code>VALIDATE_ON_KEY_STROKE</code> by default.
	 */
	private final int		validateStrategy	= VALIDATE_ON_KEY_STROKE;

	/**
	 * The text field, or <code>null</code> if none.
	 */
	Text			textField;
	private boolean grabSpace = true;

	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public MultiStringFieldEditor(final String name, final String labelText, final Composite parent) {
		super(name, labelText, parent);
	}

	/**
	 * @param name
	 * @param labelText
	 * @param parent
	 */
	public MultiStringFieldEditor(final String name, final String labelText, final Composite parent, final boolean grabSpace) {
		super(name, labelText, parent);
		this.grabSpace = grabSpace;
	}

	/**
	 * @param name
	 * @param labelText
	 * @param readOnly
	 * @param parent
	 */
	public MultiStringFieldEditor(final String name, final String labelText, final boolean readOnly, final Composite parent) {
		super(name, labelText, parent);
		this.readOnly = readOnly;
	}

	/**
	 * Returns this field editor's text control.
	 * <p>
	 * The control is created if it does not yet exist
	 * </p>
	 *
	 * @param parent the parent
	 * @return the text control
	 */
	@Override
	public Text getTextControl(final Composite parent) {
		if (this.textField == null) {
			this.textField = this.readOnly ? new Text(parent, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL)
					: new Text(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

			this.textField.setFont(parent.getFont());
			switch (this.validateStrategy) {
			case VALIDATE_ON_KEY_STROKE:
				this.textField.addKeyListener(new KeyAdapter() {

					/* (non-Javadoc)
					 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
					 */
					@Override
					public void keyReleased(final KeyEvent e) {
						valueChanged();
					}
				});

				break;
			case VALIDATE_ON_FOCUS_LOST:
				this.textField.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(final KeyEvent e) {
						clearErrorMessage();
					}
				});
				this.textField.addFocusListener(new FocusAdapter() {
					@Override
					public void focusGained(final FocusEvent e) {
						refreshValidState();
					}

					@Override
					public void focusLost(final FocusEvent e) {
						valueChanged();
						clearErrorMessage();
					}
				});
				break;
			default:
				Assert.isTrue(false, "Unknown validate strategy");//$NON-NLS-1$
			}
			this.textField.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(final DisposeEvent event) {
					MultiStringFieldEditor.this.textField = null;
				}
			});
			if (this.textLimit > 0) {//Only set limits above 0 - see SWT spec
				this.textField.setTextLimit(this.textLimit);
			}
		} else {
			checkParent(this.textField, parent);
		}
		return this.textField;
	}

	/* (non-Javadoc)
	 * Method declared on FieldEditor.
	 */
	@Override
	protected void adjustForNumColumns(final int numColumns) {
		final GridData gd = (GridData) this.textField.getLayoutData();
		gd.horizontalSpan = numColumns - 1;
		// We only grab excess space if we have to
		// If another field editor has more columns then
		// we assume it is setting the width.
		gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
		gd.grabExcessVerticalSpace = this.grabSpace;
		gd.heightHint = 100;
	}

	/**
	 * Fills this field editor's basic controls into the given parent.
	 * <p>
	 * The string field implementation of this <code>FieldEditor</code>
	 * framework method contributes the text field. Subclasses may override
	 * but must call <code>super.doFillIntoGrid</code>.
	 * </p>
	 */
	@Override
	protected void doFillIntoGrid(final Composite parent, final int numColumns) {
		super.doFillIntoGrid(parent, numColumns);
		adjustForNumColumns(numColumns);
	}

}

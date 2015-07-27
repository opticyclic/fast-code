package org.fastcode.templates.viewer;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TemplateField extends FieldEditor {

	/**
	 * Validation strategy constant (value <code>0</code>) indicating that
	 * the editor should perform validation after every key stroke.
	 *
	 * @see #setValidateStrategy
	 */
	public static final int	VALIDATE_ON_KEY_STROKE	= 0;

	/**
	 * Validation strategy constant (value <code>1</code>) indicating that
	 * the editor should perform validation only when the text widget
	 * loses focus.
	 *
	 * @see #setValidateStrategy
	 */
	public static final int	VALIDATE_ON_FOCUS_LOST	= 1;

	/**
	 * Text limit constant (value <code>-1</code>) indicating unlimited
	 * text limit and width.
	 */
	public static int		UNLIMITED				= -1;

	/**
	 * Cached valid state.
	 */
	private boolean			isValid;

	/**
	 * Old text value.
	 * @since 3.4 this field is protected.
	 */
	protected String		oldValue;

	/**
	 * The text field, or <code>null</code> if none.
	 */
	FastCodeTemplateViewer	templateField;

	/**
	 * Width of text field in characters; initially unlimited.
	 */
	private int				widthInChars			= UNLIMITED;

	/**
	 * Text limit of text field in characters; initially unlimited.
	 */
	private int				textLimit				= UNLIMITED;

	/**
	 * The error message, or <code>null</code> if none.
	 */
	private String			errorMessage;

	/**
	 * Indicates whether the empty string is legal;
	 * <code>true</code> by default.
	 */
	private boolean			emptyStringAllowed		= true;

	/**
	 * The validation strategy;
	 * <code>VALIDATE_ON_KEY_STROKE</code> by default.
	 */
	private int				validateStrategy		= VALIDATE_ON_KEY_STROKE;

	/**
	 * Creates a new string field editor
	 */
	protected TemplateField() {
	}

	/**
	 * Creates a string field editor.
	 * Use the method <code>setTextLimit</code> to limit the text.
	 *
	 * @param name the name of the preference this field editor works on
	 * @param labelText the label text of the field editor
	 * @param width the width of the text input field in characters,
	 *  or <code>UNLIMITED</code> for no limit
	 * @param strategy either <code>VALIDATE_ON_KEY_STROKE</code> to perform
	 *  on the fly checking (the default), or <code>VALIDATE_ON_FOCUS_LOST</code> to
	 *  perform validation only after the text has been typed in
	 * @param parent the parent of the field editor's control
	 * @since 2.0
	 */
	public TemplateField(final String name, final String labelText, final int width, final int strategy, final Composite parent) {
		init(name, labelText);
		this.widthInChars = width;
		setValidateStrategy(strategy);
		this.isValid = false;
		this.errorMessage = JFaceResources.getString(".errorMessage");//$NON-NLS-1$
		createControl(parent);
	}

	/**
	 * Creates a string field editor.
	 * Use the method <code>setTextLimit</code> to limit the text.
	 *
	 * @param name the name of the preference this field editor works on
	 * @param labelText the label text of the field editor
	 * @param width the width of the text input field in characters,
	 *  or <code>UNLIMITED</code> for no limit
	 * @param parent the parent of the field editor's control
	 */
	public TemplateField(final String name, final String labelText, final int width, final Composite parent) {
		this(name, labelText, width, VALIDATE_ON_KEY_STROKE, parent);
	}

	/**
	 * Creates a string field editor of unlimited width.
	 * Use the method <code>setTextLimit</code> to limit the text.
	 *
	 * @param name the name of the preference this field editor works on
	 * @param labelText the label text of the field editor
	 * @param parent the parent of the field editor's control
	 */
	public TemplateField(final String name, final String labelText, final Composite parent) {
		this(name, labelText, UNLIMITED, parent);
	}

	/*
	 * Method declared on FieldEditor.
	 */
	@Override
	protected void adjustForNumColumns(final int numColumns) {
		final GridData gd = (GridData) this.templateField.getControl().getLayoutData();
		gd.horizontalSpan = numColumns - 1;
		// We only grab excess space if we have to
		// If another field editor has more columns then
		// we assume it is setting the width.
		gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
	}

	/**
	 * Checks whether the text input field contains a valid value or not.
	 *
	 * @return <code>true</code> if the field value is valid,
	 *   and <code>false</code> if invalid
	 */
	protected boolean checkState() {
		boolean result = false;
		if (this.emptyStringAllowed) {
			result = true;
		}

		if (this.templateField == null) {
			result = false;
		}

		final String txt = this.templateField.getDocument().get();

		result = txt.trim().length() > 0 || this.emptyStringAllowed;

		// call hook for subclasses
		result = result && doCheckState();

		if (result) {
			clearErrorMessage();
		} else {
			showErrorMessage(this.errorMessage);
		}

		return result;
	}

	/**
	 * Hook for subclasses to do specific state checks.
	 * <p>
	 * The default implementation of this framework method does
	 * nothing and returns <code>true</code>.  Subclasses should
	 * override this method to specific state checks.
	 * </p>
	 *
	 * @return <code>true</code> if the field value is valid,
	 *   and <code>false</code> if invalid
	 */
	protected boolean doCheckState() {
		return true;
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
		getLabelControl(parent);

		this.templateField = getTextControl(parent);
		final GridData gd = new GridData();
		gd.horizontalSpan = numColumns - 1;
		if (this.widthInChars != UNLIMITED) {
			final GC gc = new GC((Drawable) this.templateField);
			try {
				final Point extent = gc.textExtent("X");//$NON-NLS-1$
				gd.widthHint = this.widthInChars * extent.x;
			} finally {
				gc.dispose();
			}
		} else {
			gd.horizontalAlignment = GridData.FILL;
			gd.grabExcessHorizontalSpace = true;
		}
		this.templateField.getControl().setLayoutData(gd);
	}

	/*
	 * Method declared on FieldEditor.
	 */
	@Override
	protected void doLoad() {
		if (this.templateField != null) {
			final String value = getPreferenceStore().getString(getPreferenceName());
			this.templateField.setDocument(new Document(value));
			this.oldValue = value;
		}
	}

	/*
	 * Method declared on FieldEditor.
	 */
	@Override
	protected void doLoadDefault() {
		if (this.templateField != null) {
			final String value = getPreferenceStore().getDefaultString(getPreferenceName());
			this.templateField.setDocument(new Document(value));
		}
		valueChanged();
	}

	/*
	 * Method declared on FieldEditor.
	 */
	@Override
	protected void doStore() {
		getPreferenceStore().setValue(getPreferenceName(), this.templateField.getDocument().get());
	}

	/**
	 * Returns the error message that will be displayed when and if
	 * an error occurs.
	 *
	 * @return the error message, or <code>null</code> if none
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/*
	 * Method declared on FieldEditor.
	 */
	@Override
	public int getNumberOfControls() {
		return 2;
	}

	/**
	 * Returns the field editor's value.
	 *
	 * @return the current value
	 */
	public String getStringValue() {
		if (this.templateField != null) {
			return this.templateField.getDocument().get();
		}

		return getPreferenceStore().getString(getPreferenceName());
	}

	/**
	 * Returns this field editor's text control.
	 *
	 * @return the text control, or <code>null</code> if no
	 * text field is created yet
	 */
	protected FastCodeTemplateViewer getTextControl() {
		return this.templateField;
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
	public FastCodeTemplateViewer getTextControl(final Composite parent) {
		if (this.templateField == null) {
			this.templateField = new FastCodeTemplateViewer(parent, null, null, SWT.SINGLE | SWT.BORDER);
			this.templateField.getControl().setFont(parent.getFont());
			switch (this.validateStrategy) {
			case VALIDATE_ON_KEY_STROKE:
				this.templateField.getControl().addKeyListener(new KeyAdapter() {

					/*
					 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
					 */
					@Override
					public void keyReleased(final KeyEvent e) {
						valueChanged();
					}
				});
				this.templateField.getControl().addFocusListener(new FocusAdapter() {
					// Ensure that the value is checked on focus loss in case we
					// missed a keyRelease or user hasn't released key.
					// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=214716
					@Override
					public void focusLost(final FocusEvent e) {
						valueChanged();
					}
				});

				break;
			case VALIDATE_ON_FOCUS_LOST:
				this.templateField.getControl().addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(final KeyEvent e) {
						clearErrorMessage();
					}
				});
				this.templateField.getControl().addFocusListener(new FocusAdapter() {
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
			this.templateField.getControl().addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(final DisposeEvent event) {
					TemplateField.this.templateField = null;
				}
			});
			if (this.textLimit > 0) {//Only set limits above 0 - see SWT spec
				((Text) this.templateField.getControl()).setTextLimit(this.textLimit);
			}
		} else {
			checkParent(this.templateField.getControl(), parent);
		}
		return this.templateField;
	}

	/**
	 * Returns whether an empty string is a valid value.
	 *
	 * @return <code>true</code> if an empty string is a valid value, and
	 *  <code>false</code> if an empty string is invalid
	 * @see #setEmptyStringAllowed
	 */
	public boolean isEmptyStringAllowed() {
		return this.emptyStringAllowed;
	}

	/*
	 * Method declared on FieldEditor.
	 */
	@Override
	public boolean isValid() {
		return this.isValid;
	}

	/*
	 * Method declared on FieldEditor.
	 */
	@Override
	protected void refreshValidState() {
		this.isValid = checkState();
	}

	/**
	 * Sets whether the empty string is a valid value or not.
	 *
	 * @param b <code>true</code> if the empty string is allowed,
	 *  and <code>false</code> if it is considered invalid
	 */
	public void setEmptyStringAllowed(final boolean b) {
		this.emptyStringAllowed = b;
	}

	/**
	 * Sets the error message that will be displayed when and if
	 * an error occurs.
	 *
	 * @param message the error message
	 */
	public void setErrorMessage(final String message) {
		this.errorMessage = message;
	}

	/*
	 * Method declared on FieldEditor.
	 */
	@Override
	public void setFocus() {
		if (this.templateField != null) {
			this.templateField.getControl().setFocus();
		}
	}

	/**
	 * Sets this field editor's value.
	 *
	 * @param value the new value, or <code>null</code> meaning the empty string
	 */
	public void setStringValue(String value) {
		if (this.templateField != null) {
			if (value == null) {
				value = "";//$NON-NLS-1$
			}
			this.oldValue = this.templateField.getDocument().get();
			if (!this.oldValue.equals(value)) {
				this.templateField.setDocument(new Document(value));
				valueChanged();
			}
		}
	}

	/**
	 * Sets this text field's text limit.
	 *
	 * @param limit the limit on the number of character in the text
	 *  input field, or <code>UNLIMITED</code> for no limit

	 */
	public void setTextLimit(final int limit) {
		this.textLimit = limit;
		if (this.templateField != null) {
			((Text) this.templateField.getControl()).setTextLimit(limit);
		}
	}

	/**
	 * Sets the strategy for validating the text.
	 * <p>
	 * Calling this method has no effect after <code>createPartControl</code>
	 * is called. Thus this method is really only useful for subclasses to call
	 * in their constructor. However, it has public visibility for backward
	 * compatibility.
	 * </p>
	 *
	 * @param value either <code>VALIDATE_ON_KEY_STROKE</code> to perform
	 *  on the fly checking (the default), or <code>VALIDATE_ON_FOCUS_LOST</code> to
	 *  perform validation only after the text has been typed in
	 */
	public void setValidateStrategy(final int value) {
		Assert.isTrue(value == VALIDATE_ON_FOCUS_LOST || value == VALIDATE_ON_KEY_STROKE);
		this.validateStrategy = value;
	}

	/**
	 * Shows the error message set via <code>setErrorMessage</code>.
	 */
	public void showErrorMessage() {
		showErrorMessage(this.errorMessage);
	}

	/**
	 * Informs this field editor's listener, if it has one, about a change
	 * to the value (<code>VALUE</code> property) provided that the old and
	 * new values are different.
	 * <p>
	 * This hook is <em>not</em> called when the text is initialized
	 * (or reset to the default value) from the preference store.
	 * </p>
	 */
	protected void valueChanged() {
		setPresentsDefaultValue(false);
		final boolean oldState = this.isValid;
		refreshValidState();

		if (this.isValid != oldState) {
			fireStateChanged(IS_VALID, oldState, this.isValid);
		}

		final String newValue = this.templateField.getDocument().get();
		if (!newValue.equals(this.oldValue)) {
			fireValueChanged(VALUE, this.oldValue, newValue);
			this.oldValue = newValue;
		}
	}

	/*
	 * @see FieldEditor.setEnabled(boolean,Composite).
	 */
	@Override
	public void setEnabled(final boolean enabled, final Composite parent) {
		super.setEnabled(enabled, parent);
		getTextControl(parent).getControl().setEnabled(enabled);
	}

}

package org.fastcode.common;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CreateDatabaseSnippetDialog extends TrayDialog {

	protected Button statementButton;

	private Button selectButton;
	private Button insertButton;
	private Button updateButton;
	private Button deleteButton;

	private Button simpleButton;
	private Button namedParameterButton;
	private Button namedQueryButton;
	private Button namedQueryAnnotationButton;

	protected CreateDatabaseSnippetDialog(final Shell shell) {
		super(shell);
		// TODO Auto-generated constructor stub
	}

	public CreateDatabaseSnippetDialog(final Shell shell, final CreateVariableData createVariableData) {
		super(shell);
		//this.createVariableData= createVariableData;
	}


	@Override
	public void create() {
		super.create();
	}

	/**
	 * @param parent
	 *
	 */

	@Override
	protected Control createDialogArea(final Composite parent) {
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);

		createStatementButtons(parent);
		createStatementTypeButtons(parent);
		//this.createPrimitiveButtons(parent);
//		this.definedClassType = this.createText(parent, "Defined: ", 0);
//
//		this.fieldType = this.createText(parent, "Enter name(s) (SPACE SEPERATED) for the Choosen Type of Field: ", 0);
//		///////  ----  >>>> this.fieldType.addlistener.
//
//		this.createInitializePane(parent);
//
//		this.createAccessButtons(parent);
//
//		this.createSpecifierButtons(parent);
//
//		//		final Label collectionLabel = new Label(parent, SWT.NONE);
//		//		collectionLabel.setText("Collection : ");
//
//		this.collectionSpec(parent);
//		this.collectionDetail(parent);
//
//		this.createGetterSetterButtons(parent);

		return parent;
	}


	protected void createStatementButtons(final Composite parent)	{
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 8;
		composite.setLayout(layout);

		final Label ACCESS_SPEC = new Label(composite, SWT.NONE);
		ACCESS_SPEC.setText("SQL Statement :               ");

		this.selectButton = new Button(composite, SWT.RADIO);
		this.selectButton.setText("select");
		this.selectButton.setSelection(true);
		//this.selectGetterSetter(this.privateButton);
		this.insertButton = new Button(composite, SWT.RADIO);
		this.insertButton.setText( "insert");
		//this.selectGetterSetter(this.protectedButton);
		this.updateButton = new Button(composite, SWT.RADIO);
		this.updateButton.setText( "update");
		//this.selectGetterSetter(this.publicButton);
		this.deleteButton = new Button(composite, SWT.RADIO);
		this.deleteButton.setText("delete");
		//this.selectGetterSetter(this.defaultButton);
	}

	protected void createStatementTypeButtons(final Composite parent)	{
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 8;
		composite.setLayout(layout);

		final Label ACCESS_SPEC = new Label(composite, SWT.NONE);
		ACCESS_SPEC.setText("SQL Statement :               ");

		this.simpleButton = new Button(composite, SWT.RADIO);
		this.simpleButton.setText("simple");
		this.simpleButton.setSelection(true);
		//this.selectGetterSetter(this.privateButton);
		this.namedParameterButton = new Button(composite, SWT.RADIO);
		this.namedParameterButton.setText( "Named Parameter");
		//this.selectGetterSetter(this.protectedButton);
		this.namedQueryButton = new Button(composite, SWT.RADIO);
		this.namedQueryButton.setText( "Named Query");
		//this.selectGetterSetter(this.publicButton);
		this.namedQueryAnnotationButton = new Button(composite, SWT.RADIO);
		this.namedQueryAnnotationButton.setText("Named Query with Annotation");
		//this.selectGetterSetter(this.defaultButton);
	}

	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Create Database Snippet");
		shell.setFullScreen(true);
		final CreateDatabaseSnippetDialog createDatabaseSnippetDialog = new CreateDatabaseSnippetDialog(shell);
		createDatabaseSnippetDialog.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		//	System.out.println(createVariableDialog.fieldName);
		display.dispose();
	}

}

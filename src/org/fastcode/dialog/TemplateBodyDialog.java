package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.fastcode.common.FastCodeConstants.FIELDS;
import org.fastcode.templates.viewer.TemplateFieldEditor;

@Deprecated
public class TemplateBodyDialog extends StatusDialog {

	private Composite	parent;
	private TemplateFieldEditor	templateBodyMultiText;
	private final String	templateBodyText;

	public TemplateBodyDialog(final Shell parent, final String templateBodyText) {
		super(parent);
		this.templateBodyText=templateBodyText;

	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Template Body");
		shell.setSize(900,600);

	}

	@Override
	protected Control createDialogArea(final Composite ancestor) {

		this.parent = new Composite(ancestor, SWT.NONE);
		final GridLayout layout1 = new GridLayout();
		layout1.numColumns = 1;
		layout1.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout1.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout1.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout1.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		this.parent.setLayout(layout1);
		this.parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		createDocViewer(this.parent);
		return ancestor;
	}

	private void createDocViewer(final Composite parent2) {
		final Label label = new Label(this.parent, SWT.NONE);
		label.setText("Template Body");
		final GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		final GridData gridDataText = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridDataText.heightHint = 600;
		gridDataText.widthHint=700;
		this.templateBodyMultiText = new TemplateFieldEditor("snippet", EMPTY_STR, this.parent, "TEMPLATE",  FIELDS.TEMPLATE_BODY,  SWT.MULTI);
		this.templateBodyMultiText.setLayout(gridDataText);
		this.templateBodyMultiText.setText(this.templateBodyText);
		this.templateBodyMultiText.setEditable(true);


	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}

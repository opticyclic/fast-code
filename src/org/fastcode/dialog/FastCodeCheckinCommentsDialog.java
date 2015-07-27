package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.NEW;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.util.FastCodeUtil.getEmptyListForNull;
import static org.fastcode.util.StringUtil.isEmpty;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.fastcode.common.FastCodeCheckinCommentsData;

public class FastCodeCheckinCommentsDialog extends TrayDialog {

	private Combo	commentsFromCacheCombo;
	private Combo	commentsFromRepositoryCombo;
	Shell			shell;
	private FastCodeCheckinCommentsData	fastCodeCheckinCommentsData;
	private Text finalComment;
	private Button addPrefixFooter;

	/**
	 * @param shell
	 */
	public FastCodeCheckinCommentsDialog(final Shell shell) {
		super(shell);
		this.shell = shell;
	}

	/**
	 * @param shell
	 * @param createSnippetData
	 */
	public FastCodeCheckinCommentsDialog(final Shell shell, final FastCodeCheckinCommentsData fastCodeComboData) {
		super(shell);
		this.shell = shell;
		this.fastCodeCheckinCommentsData = fastCodeComboData;

	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText(this.fastCodeCheckinCommentsData.getTitle());
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		createFinalComments(parent);
		createPrefixFooter(parent);
		createPreviousComments(parent);
		return parent;
	}

	private void createPrefixFooter(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 8;
		composite.setLayout(layout);
		final Label label = new Label(composite, SWT.NONE);
		label.setText("Add Prefix and Footer : ");
		this.addPrefixFooter = new Button(composite, SWT.CHECK);
		this.addPrefixFooter.setSelection(true);
		this.addPrefixFooter.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				FastCodeCheckinCommentsDialog.this.fastCodeCheckinCommentsData.setAddPrefixFooter(((Button) event.widget).getSelection());
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}

		});

		final Composite composite1 = new Composite(parent, parent.getStyle());
		final GridLayout layout1 = new GridLayout();
		composite1.setLayout(layout1);
		final Label preComntLabel = new Label(composite1, SWT.NONE);
		preComntLabel.setText("Previous comments:");
	}

	private void createPreviousComments(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		final GridData comboGrid = new GridData();
		this.commentsFromCacheCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.commentsFromCacheCombo.setLayoutData(comboGrid);
		this.commentsFromCacheCombo.add("<previous comments from cache>");
		this.commentsFromCacheCombo.select(0);

		for (final String prevComnt : getEmptyListForNull(this.fastCodeCheckinCommentsData.getComntsFromCache())) {
			this.commentsFromCacheCombo.add(prevComnt);
		}

		this.commentsFromCacheCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String prevComnt = FastCodeCheckinCommentsDialog.this.commentsFromCacheCombo.getItem(FastCodeCheckinCommentsDialog.this.commentsFromCacheCombo
						.getSelectionIndex());
				final String finalComnt = FastCodeCheckinCommentsDialog.this.finalComment.getText();
				FastCodeCheckinCommentsDialog.this.finalComment.setText(isEmpty(finalComnt) ? prevComnt : finalComnt.trim() + NEWLINE + prevComnt);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

		final GridData comboGrid2 = new GridData(200,20);
		this.commentsFromRepositoryCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.commentsFromRepositoryCombo.setLayoutData(comboGrid2);
		this.commentsFromRepositoryCombo.add("<previous comments from repository>");
		this.commentsFromRepositoryCombo.select(0);

		for (final String prevComnt : getEmptyListForNull(this.fastCodeCheckinCommentsData.getComntsFromRepo())) {
			this.commentsFromRepositoryCombo.add(prevComnt);
		}

		this.commentsFromRepositoryCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String prevComnt = FastCodeCheckinCommentsDialog.this.commentsFromRepositoryCombo.getItem(FastCodeCheckinCommentsDialog.this.commentsFromRepositoryCombo
						.getSelectionIndex());
				final String finalComnt = FastCodeCheckinCommentsDialog.this.finalComment.getText();
				FastCodeCheckinCommentsDialog.this.finalComment.setText(isEmpty(finalComnt) ? prevComnt : finalComnt.trim() + NEWLINE + prevComnt);
				FastCodeCheckinCommentsDialog.this.addPrefixFooter.setSelection(false);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
	}

	private void createFinalComments(final Composite parent) {
		final GridData gridData = new GridData(500,200);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		final Label label = new Label(parent, SWT.NONE);
		label.setText("Comments:");

		this.finalComment = new Text(parent, SWT.BORDER | SWT.MULTI);
		this.finalComment.setLayoutData(gridData);
		this.finalComment.setSize(500,200);
		if (!isEmpty(this.fastCodeCheckinCommentsData.getFinalComment())) {
			this.finalComment.setText(this.fastCodeCheckinCommentsData.getFinalComment());
		}
		this.finalComment.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent arg0) {
				if (isEmpty(FastCodeCheckinCommentsDialog.this.finalComment.getText())) {
					FastCodeCheckinCommentsDialog.this.addPrefixFooter.setSelection(true);
				}
			}

		});

	}


	@Override
	protected void okPressed() {

		this.fastCodeCheckinCommentsData.setFinalComment(this.finalComment.getText());
		this.fastCodeCheckinCommentsData.setAddPrefixFooter(this.addPrefixFooter.getSelection());
		super.okPressed();
	}
}

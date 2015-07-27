package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.util.FastCodeUtil.closeInputStream;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.Activator;

public class AboutFastCodeDialog extends TrayDialog {
	Shell					shell;
	final IPreferenceStore	preferenceStore;

	public AboutFastCodeDialog(final Shell parentShell) {
		super(parentShell);
		this.shell = parentShell;
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		parentShell.setSize(600, 700);
	}

	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("About Fast-Code");
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);
		createText(parent);

		return super.createDialogArea(parent);
	}

	@Override
	protected void setButtonLayoutData(final Button button) {
		super.setButtonLayoutData(button);
		final Control button1 = getButton(IDialogConstants.CANCEL_ID);
		if (button1 != null) {
			button1.setVisible(false);
		}
	}

	private void createText(final Composite parent) {
		final GridData text = new GridData(480, 300);
		text.grabExcessHorizontalSpace = true;

		/*Text messageText = new Text(parent, SWT.READ_ONLY | SWT.MULTI);
		messageText.setLayoutData(text);*/

		final Link link = new Link(parent, SWT.NONE);
		link.setLayoutData(text);
		InputStream inputStream = null;
		try {
			inputStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/about.properties"), false);
			final byte[] bytes = new byte[inputStream.available()];
			inputStream.read(bytes);
			final String fileContent = new String(bytes);
			link.setText(fileContent.trim());
			link.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					try {
						PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(e.text));
					} catch (final PartInitException ex) {
						ex.printStackTrace();
					} catch (final MalformedURLException ex) {
						ex.printStackTrace();
					}
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent arg0) {
					// TODO Auto-generated method stub

				}
			});
		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {
			closeInputStream(inputStream);
		}
	}
}

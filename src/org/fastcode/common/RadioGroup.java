package org.fastcode.common;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class RadioGroup {

	List<Button>		radioButtons;
	String				seletedItem;
	String				label;
	private final Group	control;

	public RadioGroup(final Composite parent, final String label, final String[][] labels) {
		final Group group = new Group(parent, SWT.NONE);
		// group.setFont(parent.getFont());
		final String text = label;
		if (text != null) {
			group.setText(text);
		}

		final GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 8;
		layout.numColumns = 5;
		group.setLayout(layout);

		this.radioButtons = new ArrayList<Button>();
		for (int i = 0; i < labels.length; i++) {
			final Button radio = new Button(group, SWT.RADIO | SWT.LEFT);
			this.radioButtons.add(radio);

			radio.setText(labels[i][0]);
			radio.setData(labels[i][1]);
			radio.setFont(parent.getFont());

		}
		this.control = group;
		this.label = label;
		createLabel(parent, EMPTY_STR);

	}

	public String getLabel() {
		return this.label;
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public List<Button> getRadioButtons() {
		return this.radioButtons;
	}

	public void setEnabled(final boolean enable) {
		this.control.setEnabled(enable);
		for (final Button b : this.radioButtons) {
			b.setEnabled(enable);
			b.setSelection(false);
		}

	}

	public boolean isEnabled() {
		return this.control.isEnabled();
	}

	public String getSelectedItem() {
		return this.seletedItem;
	}

	public void setSelectedItem(final String value) {
		for (final Button b : this.radioButtons) {
			if (b.getData().equals(value)) {
				b.setSelection(true);
			}
			// b.setSelection(false);
		}
		this.seletedItem = value;
	}

	private static Label createLabel(final Composite parent, final String name) {
		final Label label = new Label(parent, SWT.NULL);
		label.setText(name);
		label.setLayoutData(new GridData());
		return label;
	}

}

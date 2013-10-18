package org.eclipse.gef4.swtfx.controls;

import org.eclipse.gef4.swtfx.SwtControlAdapterNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;

public class SwtLabel extends SwtControlAdapterNode<Label> {
	private String text;

	public SwtLabel(String text) {
		super(null);
		this.text = text;
	}

	private Label createLabel() {
		Label label = new Label(getScene(), SWT.NONE);
		label.setText(text);
		return label;
	}

	@Override
	protected void hookControl() {
		setControl(createLabel());
		super.hookControl();
	}

	@Override
	protected void unhookControl() {
		super.unhookControl();
		getControl().dispose();
	}
}

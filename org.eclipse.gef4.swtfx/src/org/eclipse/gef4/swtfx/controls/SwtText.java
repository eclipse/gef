/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swtfx.controls;

import org.eclipse.gef4.swtfx.SwtControlAdapterNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

public class SwtText extends SwtControlAdapterNode<Text> {

	public static enum Type {
		SINGLE, MULTI
	}

	private Type type;

	private String text;

	private int selStart = 0;
	private int selEnd = 0;

	public SwtText(Type type) {
		super(null);
		this.type = type;
	}

	private Text createText() {
		Text control = new Text(getScene(), type == Type.SINGLE ? SWT.SINGLE
				: SWT.MULTI);
		control.setText(text);
		control.setSelection(selStart, selEnd);
		return control;
	}

	public String getSelectionText() {
		Text control = getControl();
		if (control != null) {
			return control.getSelectionText();
		}
		return "";
	}

	public String getText() {
		Text control = getControl();
		if (control != null) {
			text = control.getText();
		}
		return text;
	}

	@Override
	protected void hookControl() {
		setControl(createText());
		super.hookControl();
	}

	public void setSelection(int start, int end) {
		selStart = start;
		selEnd = end;
		Text control = getControl();
		if (control != null) {
			control.setSelection(start, end);
		}
	}

	public void setText(String text) {
		this.text = text;
		if (getControl() != null) {
			getControl().setText(text);
		}
	}

	@Override
	protected void unhookControl() {
		super.unhookControl();
		getControl().dispose();
	}

}

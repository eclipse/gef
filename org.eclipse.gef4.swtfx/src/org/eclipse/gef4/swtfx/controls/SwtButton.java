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
import org.eclipse.swt.widgets.Button;

public class SwtButton extends SwtControlAdapterNode<Button> {

	public static enum Type {
		PUSH, TOGGLE;
		public int getSwtFlags() {
			switch (this) {
			case PUSH:
				return SWT.PUSH;
			case TOGGLE:
				return SWT.TOGGLE;
			default:
				throw new IllegalStateException("Unsupported SwtButton.Type: "
						+ this);
			}
		}
	}

	private String text;
	private Type type;

	public SwtButton(String text) {
		this(text, Type.PUSH);
	}

	public SwtButton(String text, Type type) {
		super(null);
		this.text = text;
		this.type = type;
	}

	private Button createButton() {
		Button button = new Button(getScene(), type.getSwtFlags());
		button.setText(text);
		return button;
	}

	@Override
	protected void hookControl() {
		setControl(createButton());
		super.hookControl();
	}

	@Override
	protected void unhookControl() {
		super.unhookControl();
		getControl().dispose();
	}

}

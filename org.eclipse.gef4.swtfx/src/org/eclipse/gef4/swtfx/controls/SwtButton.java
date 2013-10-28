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
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class SwtButton extends SwtControlAdapterNode<Button> {

	public static enum Type {
		PUSH, TOGGLE, RADIO, CHECK;

		public int getSwtFlags() {
			switch (this) {
			case PUSH:
				return SWT.PUSH;
			case TOGGLE:
				return SWT.TOGGLE;
			case RADIO:
				return SWT.RADIO;
			case CHECK:
				return SWT.CHECK;
			default:
				throw new IllegalStateException("Unsupported SwtButton.Type: "
						+ this);
			}
		}
	}

	public static void groupRadios(final SwtButton... radios) {
		// validate arguments
		for (SwtButton btn : radios) {
			if (btn.type != Type.RADIO) {
				throw new IllegalArgumentException(
						"You can only group RADIO buttons.");
			}
		}

		// register event handlers
		for (final SwtButton btn : radios) {
			btn.addEventHandler(ActionEvent.ACTION,
					new IEventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							for (SwtButton btn : radios) {
								btn.setSelection(false);
							}
							btn.setSelection(true);
						}
					});
		}
	}

	private String text;
	private Type type;
	private boolean sel;

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
		button.setSelection(sel);
		return button;
	}

	public boolean getSelection() {
		if (getControl() != null) {
			return getControl().getSelection();
		}
		return false;
	}

	@Override
	protected void hookControl() {
		setControl(createButton());
		super.hookControl();
	}

	public void setSelection(boolean sel) {
		this.sel = sel;
		if (getControl() != null) {
			getControl().setSelection(sel);
		}
	}

	@Override
	protected void unhookControl() {
		super.unhookControl();
		getControl().dispose();
	}

}

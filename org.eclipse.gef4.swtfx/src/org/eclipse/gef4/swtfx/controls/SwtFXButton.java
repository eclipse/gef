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

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;

import org.eclipse.gef4.swtfx.SwtFXCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

public class SwtFXButton extends AbstractSwtFXControl<Button> {

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

	public static void groupRadios(final SwtFXButton... radios) {
		// validate arguments
		for (SwtFXButton btn : radios) {
			if (btn.type != Type.RADIO) {
				throw new IllegalArgumentException(
						"You can only group RADIO buttons.");
			}
		}

		// register event handlers
		for (final SwtFXButton btn : radios) {
			btn.addEventHandler(ActionEvent.ACTION,
					new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							for (SwtFXButton btn : radios) {
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

	public SwtFXButton(String text) {
		this(text, Type.PUSH);
	}

	public SwtFXButton(String text, Type type) {
		super();
		this.text = text;
		this.type = type;
	}

	@Override
	protected Button createControl(final SwtFXCanvas fxCanvas) {
		if (fxCanvas == null) {
			throw new IllegalStateException(
					"Cannot create SWT Control: Missing FXCanvas!");
		}
		Button button = new Button(fxCanvas, type.getSwtFlags());
		button.setText(text);
		button.setSelection(sel);

		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				SwtFXButton target = SwtFXButton.this;
				Event.fireEvent(target, new ActionEvent(target, target));
			}
		});

		return button;
	}

	public boolean getSelection() {
		if (getControl() != null) {
			sel = getControl().getSelection();
		}
		return sel;
	}

	public String getText() {
		if (getControl() != null) {
			text = getControl().getText();
		}
		return text;
	}

	@Override
	public void resize(double width, double height) {
		super.resize(width, height);
	}

	public void setSelection(boolean sel) {
		this.sel = sel;
		if (getControl() != null) {
			getControl().setSelection(sel);
		}
	}

	public void setText(String text) {
		this.text = text;
		if (getControl() != null) {
			getControl().setText(text);
		}
	}

}

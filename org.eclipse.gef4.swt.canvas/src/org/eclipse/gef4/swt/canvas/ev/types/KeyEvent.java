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
package org.eclipse.gef4.swt.canvas.ev.types;

import org.eclipse.gef4.swt.canvas.ev.EventType;
import org.eclipse.gef4.swt.canvas.ev.IEventTarget;

public class KeyEvent extends InputEvent {

	public static final EventType<KeyEvent> ANY = new EventType<KeyEvent>(
			EventType.ROOT, "KeyEvent");

	public static final EventType<KeyEvent> KEY_PRESSED = new EventType<KeyEvent>(
			ANY, "KeyPressedEvent");

	public static final EventType<KeyEvent> KEY_RELEASED = new EventType<KeyEvent>(
			ANY, "KeyReleasedEvent");

	private static final long serialVersionUID = 1L;

	private int code;
	private char sym;

	public KeyEvent(Object source, IEventTarget target,
			EventType<? extends KeyEvent> type, int code, char sym) {
		super(source, target, type);
		this.code = code;
		this.sym = sym;
	}

	public char getChar() {
		return sym;
	}

	public int getCode() {
		return code;
	}

}

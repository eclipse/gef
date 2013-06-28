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

public class TraverseEvent extends InputEvent {

	private static final long serialVersionUID = 1L;

	public static final EventType<TraverseEvent> ANY = new EventType<TraverseEvent>(
			InputEvent.ANY, "TraverseEvent");

	// TODO: If we want to provide specialized event types for
	// traverseNext/Prev/Up/Down it should look like this:
	//
	// public static final EventType<TraverseEvent> TRAVERSE_NEXT = new
	// EventType<TraverseEvent>(
	// ANY, "TraverseNextEvent");
	//
	// public static final EventType<TraverseEvent> TRAVERSE_PREVIOUS = new
	// EventType<TraverseEvent>(
	// ANY, "TraversePreviousEvent");

	private int detail;

	private int code;

	private int modMask;

	public TraverseEvent(Object source, IEventTarget target,
			EventType<? extends InputEvent> type, int detail, int keyCode,
			int modifierMask) {
		super(source, target, type);
		this.detail = detail;
		code = keyCode;
		modMask = modifierMask;
	}

	/**
	 * Returns the event's detail field. You can compare the value to the
	 * SWT.TRAVERSE_x constants.
	 * 
	 * @return the event's detail field
	 */
	public int getDetail() {
		return detail;
	}

	public int getKeyCode() {
		return code;
	}

	public int getModifierMask() {
		return modMask;
	}

	@Override
	public String toString() {
		return getEventType().getName() + "(detail=" + getDetail()
				+ ", keyCode=" + getKeyCode() + ", modifierMask="
				+ getModifierMask() + ")";
	}

}

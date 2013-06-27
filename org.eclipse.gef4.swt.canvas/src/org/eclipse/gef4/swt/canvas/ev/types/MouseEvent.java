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

import org.eclipse.gef4.swt.canvas.ev.Event;
import org.eclipse.gef4.swt.canvas.ev.EventType;
import org.eclipse.gef4.swt.canvas.ev.IEventTarget;

public class MouseEvent extends Event {

	public static final EventType<MouseEvent> ANY = new EventType<MouseEvent>(
			EventType.ROOT, "MouseEvent");

	public static final EventType<MouseEvent> MOUSE_PRESSED = new EventType<MouseEvent>(
			ANY, "MousePressEvent");

	public static final EventType<MouseEvent> MOUSE_RELEASED = new EventType<MouseEvent>(
			ANY, "MouseReleaseEvent");

	public static final EventType<MouseEvent> MOUSE_SCROLLED = new EventType<MouseEvent>(
			ANY, "MouseScrollEvent");

	public static final EventType<MouseEvent> MOUSE_MOVED = new EventType<MouseEvent>(
			ANY, "MouseMoveEvent");

	public static final EventType<MouseEvent> MOUSE_ENTERED_TARGET = new EventType<MouseEvent>(
			ANY, "MouseEnterTargetEvent");

	public static final EventType<MouseEvent> MOUSE_EXITED_TARGET = new EventType<MouseEvent>(
			ANY, "MouseExitTargetEvent");

	public static final EventType<MouseEvent> MOUSE_ENTERED = new EventType<MouseEvent>(
			MOUSE_ENTERED_TARGET, "MouseEnterEvent");

	public static final EventType<MouseEvent> MOUSE_EXITED = new EventType<MouseEvent>(
			MOUSE_EXITED_TARGET, "MouseExitEvent");

	private static final long serialVersionUID = 1L;

	private int button;
	private int count;
	private double x;
	private double y;

	public MouseEvent(Object source, IEventTarget target,
			EventType<? extends MouseEvent> type, int button, int count,
			double x, double y) {
		super(source, target, type);

		this.button = button;
		this.count = count;
		this.x = x;
		this.y = y;
	}

	public int getButton() {
		return button;
	}

	public int getClickCount() {
		return count;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

}

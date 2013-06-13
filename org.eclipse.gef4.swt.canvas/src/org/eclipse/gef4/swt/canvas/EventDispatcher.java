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
package org.eclipse.gef4.swt.canvas;

import java.util.List;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class EventDispatcher implements Listener {

	/*
	 * TODO: use an event table based on the event type so that we do not walk
	 * over all the listeners for all the events but rather walk over the
	 * listeners for the current event type only.
	 */

	/*
	 * NOTE: The SWT constants for the event types are sorted by their name in
	 * the EVENT_TYPES array.
	 * 
	 * NOTE: SWT.MouseHover is issued after the tooltip delay.
	 * 
	 * FIXME: we do not listen to SWT.DragDetect (because we want to get
	 * notified on MouseDown events immediately)
	 */
	final public static int[] EVENT_TYPES = new int[] { SWT.Activate, SWT.Arm,
			SWT.Close, SWT.Collapse, SWT.Deactivate, SWT.DefaultSelection,
			SWT.Deiconify, SWT.Dispose, SWT.EraseItem, SWT.Expand, SWT.FocusIn,
			SWT.FocusOut, SWT.HardKeyDown, SWT.HardKeyUp, SWT.Help, SWT.Hide,
			SWT.Iconify, SWT.ImeComposition, SWT.KeyDown, SWT.KeyUp,
			SWT.MeasureItem, SWT.MenuDetect, SWT.Modify, SWT.MouseDoubleClick,
			SWT.MouseDown, SWT.MouseEnter, SWT.MouseExit,
			SWT.MouseHorizontalWheel, SWT.MouseHover, SWT.MouseMove,
			SWT.MouseUp, SWT.MouseVerticalWheel, SWT.Move, SWT.None,
			SWT.OpenDocument, SWT.OrientationChange, SWT.Paint, SWT.PaintItem,
			SWT.Resize, SWT.Selection, SWT.SetData, SWT.Settings, SWT.Show,
			SWT.Skin, SWT.Traverse, SWT.Verify };

	final public static int[] MOUSE_EVENT_TYPES = new int[] {
			SWT.MouseDoubleClick, SWT.MouseDown, SWT.MouseEnter, SWT.MouseExit,
			SWT.MouseHorizontalWheel, SWT.MouseHover, SWT.MouseMove,
			SWT.MouseUp, SWT.MouseVerticalWheel };

	final public static int[] KEYBOARD_EVENT_TYPES = new int[] { SWT.KeyDown,
			SWT.KeyUp };

	private static boolean anyOf(int element, int[] set) {
		for (int e : set) {
			if (element == e) {
				return true;
			}
		}
		return false;
	}

	private Group group;
	private IFigure mouseTarget;
	private IFigure focusTarget;

	public EventDispatcher() {
	}

	public void addListeners() {
		for (int type : EVENT_TYPES) {
			group.addListener(type, this);
		}
	}

	private IFigure getFigureUnderCursor() {
		org.eclipse.swt.graphics.Point cursorLocation = Display.getCurrent()
				.getCursorLocation();
		cursorLocation = group.toControl(cursorLocation);
		Point cursor = new Point(cursorLocation.x, cursorLocation.y);
		return group.getFigureAt(cursor);
	}

	@Override
	public void handleEvent(Event event) {
		List<IFigure> figures = group.getFigures();
		if (figures.size() < 1) {
			return;
		}

		if (isKeyboardEvent(event)) {
			// SWT would not send us the event if we are not supposed to handle
			// it (no other control is focused)
			if (focusTarget != null) {
				// a figure is focus target
				focusTarget.handleEvent(event);
				return;
			}

			// cursor figure is the target
			IFigure cursorFigure = getFigureUnderCursor();
			if (cursorFigure != null) {
				cursorFigure.handleEvent(event);
				return;
			}
		} else if (isMouseEvent(event)) {
			// SWT would not send us the event, if we were not to handle it (no
			// control grabbed the mouse)
			if (mouseTarget != null) {
				mouseTarget.handleEvent(event);
				if (event.type == SWT.MouseUp) {
					mouseTarget = null;
				}
				return;
			} else {
				IFigure cursorTarget = getFigureUnderCursor();
				if (cursorTarget != null) {
					if (event.type == SWT.MouseDown) {
						mouseTarget = cursorTarget;
						mouseTarget.handleEvent(event);
						return;
					}
					cursorTarget.handleEvent(event);
				}
			}
		} else {
			// System.out.println("fallback: getFigureUnderCursor()");
			IFigure cursorTarget = getFigureUnderCursor();
			if (cursorTarget != null) {
				cursorTarget.handleEvent(event);
				return;
			}
		}

		// no one else handles the event, so the group can handle it:
		group.handleEvent(event);
	}

	private boolean isKeyboardEvent(Event event) {
		return anyOf(event.type, KEYBOARD_EVENT_TYPES);
	}

	private boolean isMouseEvent(Event event) {
		return anyOf(event.type, MOUSE_EVENT_TYPES);
	}

	public void removeListeners() {
		for (int type : EVENT_TYPES) {
			group.removeListener(type, this);
		}
	}

	void setFocusTarget(IFigure focusTarget) {
		this.focusTarget = focusTarget;
	}

	public void setGroup(Object group) {
		if (group instanceof Group) {
			this.group = (Group) group;
		} else {
			throw new IllegalArgumentException(
					"We need a defined gef4.swt.canvas.Group here!");
		}
	}

}

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
package org.eclipse.gef4.swtfx;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.Event;
import org.eclipse.gef4.swtfx.event.EventType;
import org.eclipse.gef4.swtfx.event.KeyEvent;
import org.eclipse.gef4.swtfx.event.MouseEvent;
import org.eclipse.gef4.swtfx.event.SwtEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

class SwtEventForwarder implements Listener {

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

	public static final int[] MOUSE_EVENT_TYPES = new int[] {
			SWT.MouseDoubleClick, SWT.MouseDown, SWT.MouseEnter, SWT.MouseExit,
			SWT.MouseHorizontalWheel, SWT.MouseHover, SWT.MouseMove,
			SWT.MouseUp, SWT.MouseVerticalWheel };

	public static final int[] KEYBOARD_EVENT_TYPES = new int[] { SWT.KeyDown,
			SWT.KeyUp };

	private static final int[] UNSUPPORTED_EVENT_TYPES = new int[] {
			SWT.Activate, SWT.Arm, SWT.Close, SWT.Collapse, SWT.Deactivate,
			SWT.Deiconify, SWT.DefaultSelection, SWT.Dispose, SWT.DragDetect,
			SWT.Expand, SWT.FocusIn, SWT.FocusOut, SWT.Gesture, SWT.Help,
			SWT.Hide, SWT.Iconify, SWT.Modify, SWT.MenuDetect, SWT.Move,
			SWT.Paint, SWT.Show, SWT.Touch, SWT.Traverse, SWT.Verify };

	private static boolean anyOf(int element, int[] set) {
		for (int e : set) {
			if (element == e) {
				return true;
			}
		}
		return false;
	}

	/**
	 * SWT {@link Control} on which a general event listener is registered which
	 * wraps SWT events in GEF4 {@link Event} objects.
	 */
	private Control sender;

	/**
	 * GEF4 {@link Scene} which is responsible for dispatching the events.
	 */
	private Scene receiver;

	/**
	 * Constructs a new SwtEventForwarder which dispatches (nearly) all SWT
	 * events that occur on the given {@link Control} to the passed-in
	 * {@link Scene}.
	 * 
	 * @param sender
	 * @param receiver
	 */
	public SwtEventForwarder(Control sender, Scene receiver) {
		this.sender = sender;
		this.receiver = receiver;
		registerListeners();
		sender.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				unregisterListeners();
			}
		});
	}

	/**
	 * @param e
	 * @param absPos
	 *            absolute mouse position
	 * @param entered
	 */
	private void fireEntered(org.eclipse.swt.widgets.Event e, INode entered,
			Point absPos) {
		INode parent = entered.getParentNode();
		if (parent != null) {
			fireEntered(e, parent, absPos);
		}

		org.eclipse.swt.graphics.Point scene = receiver.toControl(
				(int) absPos.x, (int) absPos.y);

		Point localPos = entered.displayToLocal(absPos);
		Event.fireEvent(entered, new MouseEvent(e.widget, entered,
				MouseEvent.MOUSE_ENTERED_TARGET, e.button, e.count, localPos.x,
				localPos.y, scene.x, scene.y, absPos.x, absPos.y));
	}

	/**
	 * @param e
	 * @param exited
	 * @param absPos
	 *            absolute mouse position
	 */
	private void fireExited(org.eclipse.swt.widgets.Event e, INode exited,
			Point absPos) {
		INode parent = exited.getParentNode();
		if (parent != null) {
			fireExited(e, parent, absPos);
		}

		org.eclipse.swt.graphics.Point scene = receiver.toControl(
				(int) absPos.x, (int) absPos.y);

		Point localPos = exited.displayToLocal(absPos);
		Event.fireEvent(exited, new MouseEvent(e.widget, exited,
				MouseEvent.MOUSE_EXITED_TARGET, e.button, e.count, localPos.x,
				localPos.y, scene.x, scene.y, absPos.x, absPos.y));
	}

	/**
	 * @param event
	 * @return
	 */
	private Point getAbsMousePos(org.eclipse.swt.widgets.Event event) {
		org.eclipse.swt.graphics.Point abs = sender.toDisplay(event.x, event.y);
		Point mousePosition = new Point(abs.x, abs.y);
		return mousePosition;
	}

	private Point getMousePosition() {
		org.eclipse.swt.graphics.Point cursor = receiver.getDisplay()
				.getCursorLocation();
		return new Point(cursor.x, cursor.y);
	}

	/**
	 * @return the {@link Scene} which is responsible for dispatching the events
	 */
	public Scene getReceiver() {
		return receiver;
	}

	/**
	 * @return the {@link Control} for which SWT events are collected
	 */
	public Control getSender() {
		return sender;
	}

	/**
	 * All SWT events are processed here. The IEventTarget for the events is
	 * selected according to some rules:
	 */
	@Override
	public void handleEvent(org.eclipse.swt.widgets.Event event) {
		if (anyOf(event.type, MOUSE_EVENT_TYPES)) {
			handleMouseEvent(event);
		} else if (anyOf(event.type, KEYBOARD_EVENT_TYPES)) {
			handleKeyboardEvent(event);
		} else if (anyOf(event.type, UNSUPPORTED_EVENT_TYPES)) {
			return;
		} else {
			handleOtherEvent(event);
		}
	}

	private void handleKeyboardEvent(org.eclipse.swt.widgets.Event event) {
		INode focusTarget = receiver.getFocusTarget();
		if (focusTarget != null) {
			Event.fireEvent(focusTarget, wrap(event, focusTarget));
		} else {
			handleOtherEvent(event);
		}
	}

	private void handleMouseEvent(org.eclipse.swt.widgets.Event event) {
		if (event.type == SWT.MouseMove || event.type == SWT.MouseEnter
				|| event.type == SWT.MouseExit) {
			// check for enter/exit
			INode oldPointerTarget = receiver.getMousePointerTarget();

			Point mousePosition = getAbsMousePos(event);
			Point rootLocalMousePosition = receiver.getRoot().displayToLocal(
					mousePosition);

			INode newPointerTarget = receiver.getRoot().getNodeAt(
					rootLocalMousePosition);

			if (oldPointerTarget != null) {
				if (newPointerTarget != oldPointerTarget) {
					fireExited(event, oldPointerTarget, mousePosition);
					if (newPointerTarget != null) {
						fireEntered(event, newPointerTarget, mousePosition);
					}
				}
			} else {
				if (newPointerTarget != null) {
					fireEntered(event, newPointerTarget, mousePosition);
				}
			}

			receiver.setMousePointerTarget(newPointerTarget);

			if (event.type != SWT.MouseMove) {
				return;
			}
		}

		INode mouseTarget = receiver.getMouseTarget();
		if (mouseTarget != null) {
			if (event.type == SWT.MouseUp) {
				receiver.setMouseTarget(null);
			}
			Event.fireEvent(mouseTarget, wrap(event, mouseTarget));
		} else {
			Point mousePosition = getAbsMousePos(event);
			receiver.getRoot().displayToLocal(mousePosition, mousePosition);

			INode nodeAtMouse = receiver.getRoot().getNodeAt(mousePosition);

			if (nodeAtMouse != null) {
				if (event.type == SWT.MouseDown) {
					receiver.setMouseTarget(nodeAtMouse);
				}
				Event.fireEvent(nodeAtMouse, wrap(event, nodeAtMouse));
			} else {
				if (receiver.getRoot().contains(mousePosition)) {
					Event.fireEvent(receiver.getRoot(),
							wrap(event, receiver.getRoot()));
				}
			}
		}
	}

	private void handleOtherEvent(org.eclipse.swt.widgets.Event event) {
		if (receiver.isDisposed()) {
			return;
		} else if (event.type == SWT.Selection) {
			// send to focus target if possible
			INode focusTarget = receiver.getFocusTarget();
			if (focusTarget != null) {
				Event.fireEvent(focusTarget, wrap(event, focusTarget));
				return;
			}
		}
		handleOtherEvent(event, getMousePosition());
	}

	private void handleOtherEvent(org.eclipse.swt.widgets.Event event,
			Point mousePosition) {
		receiver.getRoot().displayToLocal(mousePosition, mousePosition);

		INode nodeAtMouse = receiver.getRoot().getNodeAt(mousePosition);

		if (nodeAtMouse == null) {
			if (receiver.getRoot().contains(mousePosition)) {
				Event.fireEvent(receiver.getRoot(),
						wrap(event, receiver.getRoot()));
			}
		} else {
			Event.fireEvent(nodeAtMouse, wrap(event, nodeAtMouse));
		}
	}

	private void registerListeners() {
		for (int type : EVENT_TYPES) {
			sender.addListener(type, this);
		}
	}

	public void setReceiver(Scene receiver) {
		this.receiver = receiver;
	}

	public void transformMouseCoords(INode target, double x, double y,
			double[] displayOut, double[] sceneOut, double[] targetOut) {
		org.eclipse.swt.graphics.Point display = sender.toDisplay((int) x,
				(int) y);
		displayOut[0] = display.x;
		displayOut[1] = display.y;

		org.eclipse.swt.graphics.Point scene = receiver.toControl(display.x,
				display.y);
		sceneOut[0] = scene.x;
		sceneOut[1] = scene.y;

		Point p = new Point(display.x, display.y);
		target.displayToLocal(p, p);
		targetOut[0] = p.x;
		targetOut[1] = p.y;
	}

	protected void unregisterListeners() {
		for (int type : EVENT_TYPES) {
			sender.removeListener(type, this);
		}
	}

	private Event wrap(org.eclipse.swt.widgets.Event e, INode target) {
		switch (e.type) {
		case SWT.MouseHover:
		case SWT.MouseHorizontalWheel:
			// TODO: the previous event types are ignored => we have to
			// implement them
			return new SwtEvent(e.widget, target, SwtEvent.ANY);
		case SWT.MouseDoubleClick: {
			double[] displayXY = new double[2];
			double[] sceneXY = new double[2];
			double[] targetXY = new double[2];
			transformMouseCoords(target, e.x, e.y, displayXY, sceneXY, targetXY);
			return new MouseEvent(e.widget, target,
					MouseEvent.MOUSE_DOUBLE_CLICKED, e.button, e.count,
					targetXY[0], targetXY[1], sceneXY[0], sceneXY[1],
					displayXY[0], displayXY[1]);
		}
		case SWT.Resize:
			return new ActionEvent(e.widget, target, ActionEvent.RESIZE);
		case SWT.Selection:
			return new ActionEvent(e.widget, target, ActionEvent.ACTION);
		case SWT.KeyDown:
			return new KeyEvent(e.widget, target, KeyEvent.KEY_PRESSED,
					e.keyCode, e.character, e.stateMask);
		case SWT.KeyUp:
			return new KeyEvent(e.widget, target, KeyEvent.KEY_RELEASED,
					e.keyCode, e.character, e.stateMask);
		case SWT.MouseDown: {
			double[] displayXY = new double[2];
			double[] sceneXY = new double[2];
			double[] targetXY = new double[2];
			transformMouseCoords(target, e.x, e.y, displayXY, sceneXY, targetXY);
			return new MouseEvent(e.widget, target, MouseEvent.MOUSE_PRESSED,
					e.button, e.count, targetXY[0], targetXY[1], sceneXY[0],
					sceneXY[1], displayXY[0], displayXY[1]);
		}
		case SWT.MouseEnter: {
			double[] displayXY = new double[2];
			double[] sceneXY = new double[2];
			double[] targetXY = new double[2];
			transformMouseCoords(target, e.x, e.y, displayXY, sceneXY, targetXY);
			return new MouseEvent(e.widget, target,
					MouseEvent.MOUSE_ENTERED_TARGET, e.button, e.count,
					targetXY[0], targetXY[1], sceneXY[0], sceneXY[1],
					displayXY[0], displayXY[1]);
		}
		case SWT.MouseExit: {
			double[] displayXY = new double[2];
			double[] sceneXY = new double[2];
			double[] targetXY = new double[2];
			transformMouseCoords(target, e.x, e.y, displayXY, sceneXY, targetXY);
			return new MouseEvent(e.widget, target,
					MouseEvent.MOUSE_EXITED_TARGET, e.button, e.count,
					targetXY[0], targetXY[1], sceneXY[0], sceneXY[1],
					displayXY[0], displayXY[1]);
		}
		case SWT.MouseMove: {
			double[] displayXY = new double[2];
			double[] sceneXY = new double[2];
			double[] targetXY = new double[2];
			transformMouseCoords(target, e.x, e.y, displayXY, sceneXY, targetXY);
			EventType<MouseEvent> type = MouseEvent.MOUSE_MOVED;
			if (((e.stateMask & SWT.BUTTON1) != 0)
					|| ((e.stateMask & SWT.BUTTON2) != 0)
					|| ((e.stateMask & SWT.BUTTON3) != 0)
					|| ((e.stateMask & SWT.BUTTON4) != 0)
					|| ((e.stateMask & SWT.BUTTON5) != 0)) {
				type = MouseEvent.MOUSE_DRAGGED;
			}
			return new MouseEvent(e.widget, target, type, e.button, e.count,
					targetXY[0], targetXY[1], sceneXY[0], sceneXY[1],
					displayXY[0], displayXY[1]);
		}
		case SWT.MouseWheel: {
			double[] displayXY = new double[2];
			double[] sceneXY = new double[2];
			double[] targetXY = new double[2];
			transformMouseCoords(target, e.x, e.y, displayXY, sceneXY, targetXY);
			return new MouseEvent(e.widget, target, MouseEvent.MOUSE_SCROLLED,
					e.button, e.count, targetXY[0], targetXY[1], sceneXY[0],
					sceneXY[1], displayXY[0], displayXY[1]);
		}
		case SWT.MouseUp: {
			double[] displayXY = new double[2];
			double[] sceneXY = new double[2];
			double[] targetXY = new double[2];
			transformMouseCoords(target, e.x, e.y, displayXY, sceneXY, targetXY);
			return new MouseEvent(e.widget, target, MouseEvent.MOUSE_RELEASED,
					e.button, e.count, targetXY[0], targetXY[1], sceneXY[0],
					sceneXY[1], displayXY[0], displayXY[1]);
		}
		default:
			throw new IllegalArgumentException(
					"This SWT event type is not supported: " + e);
		}
	}
}

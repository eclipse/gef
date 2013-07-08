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
package org.eclipse.gef4.swt.fx.event;

import java.util.List;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.swt.fx.AbstractParent;
import org.eclipse.gef4.swt.fx.Group;
import org.eclipse.gef4.swt.fx.IFigure;
import org.eclipse.gef4.swt.fx.INode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;

public class SwtEventTargetSelector implements Listener {

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

	private AbstractParent region;
	private IFigure mouseTarget;
	private IFigure focusTarget;
	private IFigure mouseEnteredFigure;

	public SwtEventTargetSelector(AbstractParent region) {
		this.region = region;
		addListeners();
	}

	public void addListeners() {
		for (int type : EVENT_TYPES) {
			region.addListener(type, this);
		}
	}

	private IFigure getFigureUnderCursor() {
		org.eclipse.swt.graphics.Point cursorLocation = Display.getCurrent()
				.getCursorLocation();
		cursorLocation = region.toControl(cursorLocation);
		Point cursor = new Point(cursorLocation.x, cursorLocation.y);
		double[] xy = new double[2];
		transformToTarget(region, cursorLocation.x, cursorLocation.y, xy);
		cursor.x = xy[0];
		cursor.y = xy[1];
		return region.getFigureAt(cursor);
	}

	public IFigure getFocusTarget() {
		return focusTarget;
	}

	public IFigure getMouseTarget() {
		return mouseTarget;
	}

	@Override
	public void handleEvent(org.eclipse.swt.widgets.Event event) {
		// ignore Traverse events (per Group FocusManger/FocusDispatcher will
		// handle key presses appropriately)
		if (event.type == SWT.Traverse) {
			event.doit = false;
			return;
		}

		List<IFigure> figures = region.getFigures();
		if (figures.size() < 1) {
			// no figures => group is the target
			Event.fireEvent(region, wrap(event, region));
			return;
		}

		if (isKeyboardEvent(event)) {
			// SWT would not send us the event if we are not supposed to handle
			// it (no other control is focused)
			if (focusTarget != null) {
				// a figure is focus target
				Event.fireEvent(focusTarget, wrap(event, focusTarget));
				return;
			}

			// cursor figure is the target
			IFigure cursorFigure = getFigureUnderCursor();
			if (cursorFigure != null) {
				Event.fireEvent(cursorFigure, wrap(event, cursorFigure));
				return;
			}
		} else if (isMouseEvent(event)) {
			// SWT would not send us the event, if we were not to handle it (no
			// control grabbed the mouse)
			if (mouseTarget != null) {
				Event.fireEvent(mouseTarget, wrap(event, mouseTarget));
				if (event.type == SWT.MouseUp) {
					mouseTarget = null;
				}
				return;
			} else {
				IFigure cursorTarget = getFigureUnderCursor();

				if (event.type == SWT.MouseEnter || event.type == SWT.MouseExit) {
					Event.fireEvent(region, wrap(event, region));
				}

				// insert mouse entered/exited events
				if (event.type == SWT.MouseMove || event.type == SWT.MouseEnter
						|| event.type == SWT.MouseExit) {
					// special case SWT MouseExit
					if (mouseEnteredFigure != null
							&& event.type == SWT.MouseExit) {
						Event.fireEvent(mouseEnteredFigure, new MouseEvent(
								event.widget, mouseEnteredFigure,
								MouseEvent.MOUSE_EXITED_TARGET, event.button,
								event.count, event.x, event.y));
						mouseEnteredFigure = null;
						return;
					}

					// fire mouse exit
					if (mouseEnteredFigure != null
							&& mouseEnteredFigure != cursorTarget) {
						Event.fireEvent(mouseEnteredFigure, new MouseEvent(
								event.widget, mouseEnteredFigure,
								MouseEvent.MOUSE_EXITED_TARGET, event.button,
								event.count, event.x, event.y));
						mouseEnteredFigure = null;
					}

					// fire mouse enter
					if (mouseEnteredFigure == null && cursorTarget != null) {
						Event.fireEvent(cursorTarget, new MouseEvent(
								event.widget, cursorTarget,
								MouseEvent.MOUSE_ENTERED_TARGET, event.button,
								event.count, event.x, event.y));
						mouseEnteredFigure = cursorTarget;
					}

					// SWT MouseEnter/MouseExit need no further processing
					if (event.type == SWT.MouseEnter
							|| event.type == SWT.MouseExit) {
						return;
					}
				}

				if (cursorTarget != null) {
					if (event.type == SWT.MouseDown) {
						mouseTarget = cursorTarget;
						Event.fireEvent(mouseTarget, wrap(event, mouseTarget));
						return;
					}
					Event.fireEvent(cursorTarget, wrap(event, cursorTarget));
				}
			}
		} else {
			IFigure cursorTarget = getFigureUnderCursor();
			if (cursorTarget != null) {
				Event.fireEvent(cursorTarget, wrap(event, cursorTarget));
				return;
			}
		}

		// our group is the target
		Event.fireEvent(region, wrap(event, region));
	}

	private boolean isKeyboardEvent(org.eclipse.swt.widgets.Event event) {
		return anyOf(event.type, KEYBOARD_EVENT_TYPES);
	}

	private boolean isMouseEvent(org.eclipse.swt.widgets.Event event) {
		return anyOf(event.type, MOUSE_EVENT_TYPES);
	}

	public void removeListeners() {
		for (int type : EVENT_TYPES) {
			region.removeListener(type, this);
		}
	}

	public void setFocusTarget(IFigure focusTarget) {
		this.focusTarget = focusTarget;
	}

	public void setGroup(Object group) {
		if (group instanceof Group) {
			this.region = (Group) group;
		} else {
			throw new IllegalArgumentException(
					"We need a defined gef4.swt.canvas.Group here!");
		}
	}

	private void transformToTarget(INode target, double x, double y,
			double[] xyOut) {
		Point control = new Point(x, y);
		Point local = new Point();
		region.controlToLocal(control, local);
		xyOut[0] = local.x;
		xyOut[1] = local.y;
	}

	private Event wrap(org.eclipse.swt.widgets.Event e, INode target) {
		switch (e.type) {
		case SWT.Activate:
		case SWT.Arm:
		case SWT.Close:
		case SWT.Collapse:
		case SWT.Deactivate:
		case SWT.Deiconify:
		case SWT.DefaultSelection:
		case SWT.Dispose:
		case SWT.DragDetect:
		case SWT.Expand:
		case SWT.FocusIn:
		case SWT.FocusOut:
		case SWT.Gesture:
		case SWT.Help:
		case SWT.Hide:
		case SWT.Iconify:
		case SWT.Modify:
		case SWT.MenuDetect:
		case SWT.MouseDoubleClick:
		case SWT.MouseHorizontalWheel:
			// TODO
			// return new MouseEvent(e.widget, target,
			// MouseEvent.MOUSE_SCROLLED,
			// e.button, e.count, e.x, e.y);
		case SWT.MouseHover:
		case SWT.Move:
		case SWT.Paint:
		case SWT.Resize:
		case SWT.Selection:
		case SWT.Show:
		case SWT.Touch:
		case SWT.Verify:
			// TODO: Those are all ignored, implement'em!
			return new SwtEvent(e, target, SwtEvent.ANY);
		case SWT.KeyDown:
			return new KeyEvent(e.widget, target, KeyEvent.KEY_PRESSED,
					e.keyCode, e.character);
		case SWT.KeyUp:
			return new KeyEvent(e.widget, target, KeyEvent.KEY_RELEASED,
					e.keyCode, e.character);
		case SWT.MouseDown: {
			double[] xy = new double[2];
			transformToTarget(target, e.x, e.y, xy);
			return new MouseEvent(e.widget, target, MouseEvent.MOUSE_PRESSED,
					e.button, e.count, xy[0], xy[1]);
		}
		case SWT.MouseEnter: {
			// TODO: re-think this one
			double[] xy = new double[2];
			transformToTarget(target, e.x, e.y, xy);
			return new MouseEvent(e.widget, target,
					MouseEvent.MOUSE_ENTERED_TARGET, e.button, e.count, xy[0],
					xy[1]);
		}
		case SWT.MouseExit: {
			// TODO: re-think this one
			double[] xy = new double[2];
			transformToTarget(target, e.x, e.y, xy);
			return new MouseEvent(e.widget, target,
					MouseEvent.MOUSE_EXITED_TARGET, e.button, e.count, xy[0],
					xy[1]);
		}
		case SWT.MouseMove: {
			double[] xy = new double[2];
			transformToTarget(target, e.x, e.y, xy);
			return new MouseEvent(e.widget, target, MouseEvent.MOUSE_MOVED,
					e.button, e.count, xy[0], xy[1]);
		}
		case SWT.MouseWheel: {
			double[] xy = new double[2];
			transformToTarget(target, e.x, e.y, xy);
			return new MouseEvent(e.widget, target, MouseEvent.MOUSE_SCROLLED,
					e.button, e.count, xy[0], xy[1]);
		}
		case SWT.MouseUp: {
			double[] xy = new double[2];
			transformToTarget(target, e.x, e.y, xy);
			return new MouseEvent(e.widget, target, MouseEvent.MOUSE_RELEASED,
					e.button, e.count, xy[0], xy[1]);
		}
		case SWT.Traverse:
			// XXX: This is unreachable code, because handleEvent() ignores
			// Traverse events, completely.
			// TODO: re-think this one
			return new TraverseEvent(region, target, TraverseEvent.ANY,
					e.detail, e.keyCode, e.stateMask);
		default:
			throw new IllegalArgumentException(
					"This SWT event type is not supported: " + e);
		}
	}

}

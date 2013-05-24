package org.eclipse.gef4.swt.canvas;

import java.util.List;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class RootGroup extends Group {

	private static boolean anyOf(int element, int[] set) {
		for (int e : set) {
			if (element == e) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param group
	 * @param cursor
	 */
	private static IFigure getFigureAt(Group group, Point cursor) {
		List<IFigure> figures = group.getFigures();
		for (IFigure f : figures) {
			if (f.getBounds().getTransformedShape().contains(cursor)) {
				return f;
			}
		}
		return null;
	}

	private static boolean isKeyboardEvent(Event event) {
		return anyOf(event.type, EventDispatcher.KEYBOARD_EVENT_TYPES);
	}

	private static boolean isMouseEvent(Event event) {
		return anyOf(event.type, EventDispatcher.MOUSE_EVENT_TYPES);
	}

	private Object focusTarget; // TODO: private EventTarget focusTarget;

	private Object mouseTarget; // TODO: private EventTarget mouseTarget;

	public RootGroup(Composite parent) {
		super(parent);
		final RootGroup me = this;

		for (int type : EventDispatcher.EVENT_TYPES) {
			parent.getDisplay().addFilter(type, new Listener() {
				@Override
				public void handleEvent(Event event) {
					if (event.widget instanceof Control) {
						for (Control c = (Control) event.widget; c != null; c = c
								.getParent()) {
							if (me == c) {
								me.processEvent(event);
								break;
							}
						}
					}
				}
			});
		}
	}

	public void processEvent(Event event) {
		assert event.widget instanceof Control;
		Control swtTarget = (Control) event.widget;

		Object eventTarget = TargetSelection(event, swtTarget);

		System.out.println("SWT Control ("
				+ swtTarget.getClass().getCanonicalName() + ") issued Event ("
				+ event.type + ") -> target (" + eventTarget + ")");
	}

	/**
	 * <p>
	 * There are a few special rules to select the event target:
	 * </p>
	 * 
	 * <p>
	 * <ol>
	 * <li>Key events are directed to the focus target.</li>
	 * 
	 * <li>Mouse events are directed to the mouse target. The mouse target is
	 * set when a mouse button is pressed. It is the widget under the cursor at
	 * the time of the button press. The mouse target is fixed until the button
	 * is released, even if the mouse leaves the client area of the mouse
	 * target.</li>
	 * 
	 * <li>Mouse events are directed to the cursor target if no mouse target is
	 * set. The cursor target is the widget under the cursor.</li>
	 * </ol>
	 * </p>
	 * 
	 * <p>
	 * Although these rules are respected by SWT, we still need to manage our
	 * own focus-target, cursor-target, and mouse-target in order to be able to
	 * direct events not only to SWT widgets but also GEF4 figures.
	 * </p>
	 * 
	 * @param event
	 * @param swtTarget
	 */
	private Object TargetSelection(Event event, Control swtTarget) {
		/*
		 * TODO: special mouse events
		 * 
		 * 1. If we have no mouseTarget yet and this is a MouseDown event, set
		 * the mouseTarget.
		 * 
		 * 2. If we have a mouseTarget and this is a MouseUp event, unset the
		 * mouseTarget.
		 */

		// 1.
		if (isKeyboardEvent(event) && focusTarget != null) {
			return focusTarget;
		}

		// 2.
		if (isMouseEvent(event)) {
			if (mouseTarget != null) {
				return mouseTarget;
			}

			// 3.
			if (swtTarget instanceof Group) {
				// find figure under cursor
				Point cursor = new Point(event.x, event.y);
				IFigure f = getFigureAt((Group) swtTarget, cursor);
				if (f != null) {
					return f;
				}
			}
			// no figure under cursor => widget under cursor
			return swtTarget;
		}

		if (swtTarget instanceof Group) {
			/*
			 * A Group issued the event. Therefore, we know that either one of
			 * our figures will be the event target or the Group itself.
			 */
			Group group = (Group) swtTarget;
			// FIXME: wrong cursor for events that use .x and .y specially.
			Point cursor = new Point(event.x, event.y);
			IFigure f = getFigureAt(group, cursor);
			if (f != null) {
				return f;
			}
			// no figure under cursor => widget under cursor
			return swtTarget;
		}

		return swtTarget;
	}
}

package org.eclipse.gef4.swt.canvas;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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
			// System.out.println("...testing " + f);
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
							// System.out.println("...widget " + c);
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

	private void EventCapturing(Event event, Object eventTarget,
			List<Object> route) {
		// TODO Auto-generated method stub

	}

	/**
	 * @param group
	 * @param cursor
	 */
	private Object getGroupTarget(Group group, Point cursor) {
		IFigure f = getFigureAt(group, cursor);
		if (f != null) {
			return f;
		}
		return group;
	}

	public void processEvent(Event event) {
		assert event.widget instanceof Control;
		Control swtTarget = (Control) event.widget;

		// System.out.println("SWT Control (" + swtTarget + ")");
		// System.out.println("Event (" + event + ")");

		Object eventTarget = TargetSelection(event, swtTarget);

		// System.out.println("EventTarget (" + eventTarget + ")");

		List<Object> route = RouteConstruction(eventTarget);

		EventCapturing(event, eventTarget, route);

		// System.out.println("Route:");
		// for (Object node : route) {
		// System.out.println("  " + node);
		// }
	}

	private void RouteConstruction(List<Object> route, Object eventTarget) {
		if (eventTarget instanceof IFigure) {
			IFigure f = (IFigure) eventTarget;
			RouteConstruction(route, f.getContainer());
		} else if (eventTarget instanceof Control) {
			Control c = (Control) eventTarget;
			Composite parent = c.getParent();
			if (parent != null) {
				RouteConstruction(route, parent);
			}
		} else {
			throw new IllegalStateException(
					"Given eventTarget is neither (GEF4) IFigure nor (SWT) Control!");
		}
		route.add(eventTarget);
	}

	/**
	 * Constructs the route which the event travels along, i.e. returns all
	 * widgets/figures in the hierarchy from this RootGroup to the eventTarget.
	 * 
	 * @param eventTarget
	 * @return
	 */
	private List<Object> RouteConstruction(Object eventTarget) {
		List<Object> route = new LinkedList<Object>();
		RouteConstruction(route, eventTarget);
		return route;
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
		// the focusTarget receives all keyboard events
		if (isKeyboardEvent(event) && focusTarget != null) {
			return focusTarget;
		}

		if (isMouseEvent(event)) {
			Object target = swtTarget;

			// the mouse target receives all mouse events
			if (mouseTarget != null) {
				target = mouseTarget;
				// unset the mouseTarget on a MouseUp event
				if (event.type == SWT.MouseUp) {
					mouseTarget = null;
				}
				return target;
			}

			if (swtTarget instanceof Group) {
				Point cursor = new Point(event.x, event.y);
				// System.out.println("..search figure at " + cursor);
				target = getGroupTarget((Group) swtTarget, cursor);
			}

			// set the mouseTarget on a MouseDown event
			if (mouseTarget == null && event.type == SWT.MouseDown) {
				mouseTarget = target;
			}

			return target;
		}

		if (swtTarget instanceof Group) {
			org.eclipse.swt.graphics.Point cursorLocation = Display
					.getCurrent().getCursorLocation();
			Point cursor = new Point(cursorLocation.x, cursorLocation.y);
			return getGroupTarget((Group) swtTarget, cursor);
		}

		// in any other case, return the swt control that emitted the event
		return swtTarget;
	}

}

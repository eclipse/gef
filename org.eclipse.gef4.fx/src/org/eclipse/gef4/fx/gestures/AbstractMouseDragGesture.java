/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.gestures;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

/**
 * An FXMouseDragGesture can be used to listen to mouse press, drag, and release
 * events. In order to use it, you have to subclass it and implement the
 * {@link #press(Node, MouseEvent)},
 * {@link #drag(Node, MouseEvent, double, double)}, and
 * {@link #release(Node, MouseEvent, double, double)} methods.
 *
 * @author mwienand
 *
 */
public abstract class AbstractMouseDragGesture extends AbstractGesture {

	private Node pressed;
	private Point2D startMousePosition;

	/**
	 * This {@link EventHandler} is registered as a event handler on the target
	 * node to initiate a press-drag-release gesture.
	 */
	private EventHandler<? super MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			onMousePress(event);
		}
	};

	/**
	 * This {@link EventHandler} is registered as an event filter on the
	 * {@link Scene} to handle drag and release events.
	 */
	private EventHandler<? super MouseEvent> mouseFilter = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			onMouseEvent(event);
		}
	};

	/**
	 * This method is called upon {@link MouseEvent#MOUSE_DRAGGED} events.
	 *
	 * @param target
	 *            The event target.
	 * @param event
	 *            The corresponding {@link MouseEvent}.
	 * @param dx
	 *            The horizontal displacement from the mouse press location.
	 * @param dy
	 *            The vertical displacement from the mouse press location.
	 */
	abstract protected void drag(Node target, MouseEvent event, double dx,
			double dy);

	/**
	 * Returns the currently pressed {@link Node}.
	 *
	 * @return The currently pressed {@link Node}.
	 */
	public Node getPressed() {
		return pressed;
	}

	/**
	 * This method is called for *any* {@link MouseEvent} that occurs in the
	 * {@link Scene} where this gesture is currently registered. It processes
	 * {@link MouseEvent#MOUSE_DRAGGED} and {@link MouseEvent#MOUSE_RELEASED}
	 * events if the gesture was previously initiated (pressed node is known).
	 *
	 * @param event
	 *            The {@link MouseEvent} to process.
	 * @see #onMousePress(MouseEvent)
	 */
	protected void onMouseEvent(MouseEvent event) {
		if (pressed == null) {
			// gesture not initiated
			return;
		}

		// determine dragged/released state
		EventType<? extends Event> type = event.getEventType();
		if (type.equals(MouseEvent.MOUSE_EXITED_TARGET)) {
			// ignore mouse exited target events here (they may result from
			// visual changes that are caused by a preceding press)
			return;
		}

		boolean dragged = type.equals(MouseEvent.MOUSE_DRAGGED);
		boolean released = false;

		if (!dragged) {
			released = type.equals(MouseEvent.MOUSE_RELEASED);

			// FIXME: account for losing events
			if (!released) {
				if (!event.isPrimaryButtonDown()
						&& !event.isSecondaryButtonDown()
						&& !event.isMiddleButtonDown()) {
					// no button down?
					released = true;
				}
			}
		}

		if (dragged || released) {
			double x = event.getSceneX();
			double dx = x - startMousePosition.getX();
			double y = event.getSceneY();
			double dy = y - startMousePosition.getY();
			if (dragged) {
				drag(pressed, event, dx, dy);
			} else {
				release(pressed, event, dx, dy);
				pressed.removeEventHandler(MouseEvent.ANY, mouseFilter);
				pressed = null;
			}
		}
	}

	/**
	 * This method is called when a {@link MouseEvent#MOUSE_PRESSED} event
	 * occurs in the {@link Scene} where this gesture is currently registered.
	 * This initiates the gesture and activates processing of drag and release
	 * events.
	 *
	 * @param event
	 *            The {@link MouseEvent} to process.
	 * @see #onMouseEvent(MouseEvent)
	 */
	protected void onMousePress(MouseEvent event) {
		EventTarget target = event.getTarget();
		if (target instanceof Node) {
			pressed = (Node) target;
			pressed.addEventHandler(MouseEvent.ANY, mouseFilter);
			startMousePosition = new Point2D(event.getSceneX(),
					event.getSceneY());
			press(pressed, event);
		}
	}

	/**
	 * This method is called upon {@link MouseEvent#MOUSE_PRESSED} events.
	 *
	 * @param target
	 *            The event target.
	 * @param event
	 *            The corresponding {@link MouseEvent}.
	 */
	abstract protected void press(Node target, MouseEvent event);

	@Override
	protected void register() {
		getScene().addEventFilter(MouseEvent.ANY, mouseFilter);
		getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
	}

	/**
	 * This method is called upon {@link MouseEvent#MOUSE_RELEASED} events. This
	 * method is also called for other mouse events, when a mouse release event
	 * was not fired, but was detected otherwise (probably only possible when
	 * using the JavaFX/SWT integration).
	 *
	 * @param target
	 *            The event target.
	 * @param event
	 *            The corresponding {@link MouseEvent}.
	 * @param dx
	 *            The horizontal displacement from the mouse press location.
	 * @param dy
	 *            The vertical displacement from the mouse press location.
	 */
	abstract protected void release(Node target, MouseEvent event, double dx,
			double dy);

	@Override
	protected void unregister() {
		getScene().removeEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
		getScene().removeEventFilter(MouseEvent.ANY, mouseFilter);
	}

}

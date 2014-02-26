/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public abstract class FXMouseDragGesture {

	/**
	 * Represents the state of expectation regarding mouse events. Keeping track
	 * of the gesture state is important if the mouse is already pressed when we
	 * register the event handlers.
	 */
	private static enum State {
		/**
		 * In INIT state we expect a press event.
		 */
		INIT,

		/**
		 * In PERFORM state we expect drag or release events.
		 */
		PERFORM
	}

	private State state = State.INIT;

	private double ox, oy;
	private Scene scene;
	private Node targetNode;

	public FXMouseDragGesture() {
	}

	private EventHandler<? super MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			if (state != State.INIT) {
				/*
				 * XXX: We got trapped in PERFORM state, which should not be
				 * possible, but happens at times... As a workaround, we call
				 * release with dx = 0 and dy = 0.
				 */
				if (targetNode != e.getTarget()) {
					throw new IllegalStateException("wrong target node!");
					// TODO: JavaFX mouse event target selection bug => platform specific fix
				}
				state = State.INIT;
				removeTargetHandlers();
				release(targetNode, e, 0, 0);
			}

			ox = e.getSceneX();
			oy = e.getSceneY();

			if (e.getTarget() instanceof Node) {
				targetNode = (Node) e.getTarget();
				addTargetHandlers();
				press(targetNode, e);
				state = State.PERFORM;
			}
		}
	};

	private void addTargetHandlers() {
		targetNode.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
		targetNode.addEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
	}

	abstract protected void press(Node target, MouseEvent event);

	private EventHandler<? super MouseEvent> draggedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			if (targetNode != e.getTarget()) {
				throw new IllegalStateException("wrong target node!");
			}
			
			if (state != State.PERFORM)
				return;

			double x = e.getSceneX();
			double y = e.getSceneY();
			double dx = x - ox;
			double dy = y - oy;
			drag(targetNode, e, dx, dy);
		}
	};

	abstract protected void drag(Node target, MouseEvent event, double dx,
			double dy);

	private EventHandler<? super MouseEvent> releasedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			if (targetNode != e.getTarget()) {
				throw new IllegalStateException("wrong target node!");
			}
			
			if (state != State.PERFORM)
				return;

			double x = e.getSceneX();
			double y = e.getSceneY();
			double dx = x - ox;
			double dy = y - oy;
			release(targetNode, e, dx, dy);
			removeTargetHandlers();
			targetNode = null;
			state = State.INIT;
		}
	};

	abstract protected void release(Node target, MouseEvent event, double dx,
			double dy);

	public void setScene(Scene scene) {
		if (this.scene == scene) {
			return;
		}

		if (this.scene != null) {
			if (targetNode != null) {
				removeTargetHandlers();
				targetNode = null;
			}
			this.scene.removeEventHandler(MouseEvent.MOUSE_PRESSED,
					pressedHandler);
		}

		this.scene = scene;

		if (scene != null) {
			scene.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
			state = State.INIT;
		}
	}

	private void removeTargetHandlers() {
		targetNode.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
		targetNode.removeEventHandler(MouseEvent.MOUSE_RELEASED,
				releasedHandler);
	}

}

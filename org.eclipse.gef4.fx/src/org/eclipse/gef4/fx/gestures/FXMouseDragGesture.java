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

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseDragEvent;
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

	private EventHandler<? super MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			if (state != State.INIT) {
				/*
				 * XXX: We got trapped in PERFORM state, which should not be
				 * possible, but happens at times... As a workaround, we call
				 * releasedHandler#handle(MouseEvent).
				 * 
				 * We give it the pressed event, although it might be a good
				 * idea to pass-in dx = 0 and dy = 0.
				 */
				if (targetNode != e.getTarget()) {
					// TODO: JavaFX mouse event target selection bug => platform
					// specific fix
					System.err.println("wrong target node!");
				}
				// TODO: IPolicy#cancel() - cancel policy to notify the gesture
				// ended unexpectedly
				releasedHandler.handle(e);
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

	private EventHandler<? super MouseEvent> dragDetectedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (targetNode.getScene() != null) {
				targetNode.startFullDrag();
			}
		}
	};

	private List<Node> nodesUnderMouse = new ArrayList<Node>();

	private EventHandler<? super MouseDragEvent> dragEnteredHandler = new EventHandler<MouseDragEvent>() {
		@Override
		public void handle(MouseDragEvent event) {
			EventTarget target = event.getTarget();
			if (target instanceof Node) {
				Node node = (Node) target;
				if (targetNode != node) {
					if (!nodesUnderMouse.contains(node)) {
						nodesUnderMouse.add(node);
					}
				}
			}
		}
	};

	private EventHandler<? super MouseDragEvent> dragExitedHandler = new EventHandler<MouseDragEvent>() {
		@Override
		public void handle(MouseDragEvent event) {
			EventTarget target = event.getTarget();
			if (target instanceof Node) {
				Node node = (Node) target;
				if (targetNode != node) {
					if (nodesUnderMouse.contains(node)) {
						nodesUnderMouse.remove(node);
					}
				}
			}
		}
	};

	private EventHandler<? super MouseEvent> draggedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			if (targetNode != e.getTarget()) {
				System.err.println("wrong target node!");
			}

			if (state != State.PERFORM) {
				return;
			}

			double x = e.getSceneX();
			double y = e.getSceneY();
			double dx = x - ox;
			double dy = y - oy;
			drag(targetNode, e, dx, dy, nodesUnderMouse);
		}
	};

	private EventHandler<? super MouseEvent> releasedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			if (targetNode != e.getTarget()) {
				// FIXME: System.err.println("wrong target node!");
			}

			if (state != State.PERFORM) {
				return;
			}

			double x = e.getSceneX();
			double y = e.getSceneY();
			double dx = x - ox;
			double dy = y - oy;
			release(targetNode, e, dx, dy, nodesUnderMouse);
			removeTargetHandlers();
			targetNode = null;
			state = State.INIT;
		}
	};

	public FXMouseDragGesture() {
	}

	protected void addTargetHandlers() {
		nodesUnderMouse.clear();
		targetNode.setMouseTransparent(true);
		targetNode.addEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
		targetNode.addEventHandler(MouseEvent.DRAG_DETECTED,
				dragDetectedHandler);
		scene.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED_TARGET,
				dragEnteredHandler);
		scene.addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED_TARGET,
				dragExitedHandler);
		targetNode.addEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
	}

	abstract protected void drag(Node target, MouseEvent event, double dx,
			double dy, List<Node> nodesUnderMouse);

	public Scene getScene() {
		return scene;
	}

	public Node getTargetNode() {
		return targetNode;
	}

	abstract protected void press(Node target, MouseEvent event);

	abstract protected void release(Node target, MouseEvent event, double dx,
			double dy, List<Node> nodesUnderMouse);

	protected void removeTargetHandlers() {
		targetNode.setMouseTransparent(false);
		targetNode.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
		scene.removeEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED_TARGET,
				dragExitedHandler);
		scene.removeEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED_TARGET,
				dragEnteredHandler);
		targetNode.removeEventHandler(MouseEvent.DRAG_DETECTED,
				dragDetectedHandler);
		targetNode.removeEventHandler(MouseEvent.MOUSE_RELEASED,
				releasedHandler);
	}

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

}

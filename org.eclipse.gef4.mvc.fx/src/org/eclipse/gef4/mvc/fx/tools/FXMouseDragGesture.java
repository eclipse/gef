package org.eclipse.gef4.mvc.fx.tools;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

abstract public class FXMouseDragGesture {

	private double ox, oy;
	private Scene scene;
	private Node targetNode;

	public FXMouseDragGesture() {
	}

	private EventHandler<? super MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent e) {
			ox = e.getSceneX();
			oy = e.getSceneY();

			if (e.getTarget() instanceof Node) {
				targetNode = (Node) e.getTarget();
				addTargetHandlers();
				press(targetNode, e);
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
			double x = e.getSceneX();
			double y = e.getSceneY();
			double dx = x - ox;
			double dy = y - oy;
			release(targetNode, e, dx, dy);
			removeTargetHandlers();
			targetNode = null;
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
			}
			this.scene.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
		}
		
		this.scene = scene;
		
		if (scene != null) {
			scene.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
		}
	}

	private void removeTargetHandlers() {
		targetNode.removeEventHandler(MouseEvent.MOUSE_DRAGGED, draggedHandler);
		targetNode.removeEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
	}

}

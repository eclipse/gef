package org.eclipse.gef4.mvc.javafx;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.aspects.resizerelocate.AbstractRelocateTool;

public class FXRelocateTool extends AbstractRelocateTool<Node> {
	
	private boolean performing = false;
	
	private EventHandler<MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			initRelocate(new Point(event.getSceneX(), event.getSceneY()));
			performing = true;
		}
	};

	private EventHandler<MouseEvent> draggedFilter = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (performing) {
				performRelocate(new Point(event.getSceneX(), event.getSceneY()));
			}
		}
	};

	private EventHandler<MouseEvent> releasedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (performing) {
				performing = false;
				commitRelocate(new Point(event.getSceneX(), event.getSceneY()));
			}
		}
	};

	@Override
	public void activate() {
		super.activate();
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventFilter(MouseEvent.MOUSE_DRAGGED, draggedFilter);
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
	}

	@Override
	public void deactivate() {
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.removeEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.removeEventFilter(MouseEvent.MOUSE_DRAGGED, draggedFilter);
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
		super.deactivate();
	}

}

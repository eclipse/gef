package org.eclipse.gef4.mvc.javafx;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.aspects.resize.AbstractResizeTool;
import org.eclipse.gef4.mvc.domain.IEditDomain;

public class FXResizeTool extends AbstractResizeTool<Node> {

	IEditDomain<Node> domain = null;

	private EventHandler<MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			initResize(new Point(event.getSceneX(), event.getSceneY()));
		}
	};

	private EventHandler<MouseEvent> draggedFilter = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			performResize(new Point(event.getSceneX(), event.getSceneY()));
		}
	};

	private EventHandler<MouseEvent> releasedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			// TODO: the resize tool should not now something about this
			// this responsibility should either be placed in the domain, the
			// viewer, the selection tool or the handle tool (the most
			// appropriate location probably)
			// the handle tool should in this case also push the handle tool to
			// the stack
			domain = getDomain(); // we need this to properly unregister
			commitResize(new Point(event.getSceneX(), event.getSceneY()));
			domain.popTool(); // remove ourselves from the tool stack
		}

	};

	@Override
	public void activate() {
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventFilter(MouseEvent.MOUSE_DRAGGED, draggedFilter);
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
	}

	@Override
	public void deactivate() {
		// TODO: proper handling of domain registration
		((FXViewer) domain.getViewer()).getCanvas().getScene()
				.removeEventHandler(MouseEvent.MOUSE_PRESSED, pressedHandler);
		((FXViewer) domain.getViewer()).getCanvas().getScene()
				.removeEventFilter(MouseEvent.MOUSE_DRAGGED, draggedFilter);
		((FXViewer) domain.getViewer()).getCanvas().getScene()
				.removeEventHandler(MouseEvent.MOUSE_RELEASED, releasedHandler);
		domain = null;
	}
}

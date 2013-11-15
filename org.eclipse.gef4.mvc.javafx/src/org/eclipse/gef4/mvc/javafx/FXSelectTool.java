package org.eclipse.gef4.mvc.javafx;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.aspects.selection.AbstractSelectTool;
import org.eclipse.gef4.mvc.parts.IEditPart;

public class FXSelectTool extends AbstractSelectTool<Node> {

	private EventHandler<MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (select(getTargetPart(event), event.isControlDown())) {
				event.consume();
			}
		}
	};

	private IEditPart<Node> getTargetPart(MouseEvent event) {
		IEditPart<Node> newSelection = getDomain().getViewer().getVisualPartMap().get(
				((Node) event.getTarget()));
		return newSelection;
	}

	@Override
	public void activate() {
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventFilter(MouseEvent.MOUSE_PRESSED, pressedHandler);
	}

	@Override
	public void deactivate() {
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.removeEventFilter(MouseEvent.MOUSE_PRESSED, pressedHandler);
	}

}

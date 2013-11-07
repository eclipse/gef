package org.eclipse.gef4.mvc.javafx;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.mvc.parts.IEditPart;
import org.eclipse.gef4.mvc.tools.AbstractSelectTool;

public class FXSelectTool extends AbstractSelectTool<Node> {

	private EventHandler<MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			// compute new selection target
			IEditPart<Node> targetPart = getTargetPart(event);
			// perform selection
			if (targetPart != null) {
				select(targetPart, event.isControlDown());
			} else {
				deselectAll();
			}
		}

	};

	private IEditPart<Node> getTargetPart(MouseEvent event) {
		IEditPart<Node> newSelection = getViewer().getVisualPartMap().get(
				((Node) event.getTarget()));
		return newSelection;
	}

	@Override
	public void activate() {
		((FXViewer) getViewer()).getCanvas().getScene()
				.addEventFilter(MouseEvent.MOUSE_PRESSED, pressedHandler);
	}

	@Override
	public void deactivate() {
		((FXViewer) getViewer()).getCanvas().getScene()
				.removeEventFilter(MouseEvent.MOUSE_PRESSED, pressedHandler);
	}

}

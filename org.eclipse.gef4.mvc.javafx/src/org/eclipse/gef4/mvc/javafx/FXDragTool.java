package org.eclipse.gef4.mvc.javafx;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.IEditPart;
import org.eclipse.gef4.mvc.tools.AbstractDragTool;

public class FXDragTool extends AbstractDragTool<Node> {

	private EventHandler<MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			for(IEditPart<Node> selected : getEditDomain().getSelectionAndFocusModel().getSelectedParts()){
				initDrag(selected, new Point(event.getScreenX(),
						event.getScreenY()));
			}
		}

	};
	
	private EventHandler<MouseEvent> draggedHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			for(IEditPart<Node> selected : getEditDomain().getSelectionAndFocusModel().getSelectedParts()){
				commitDrag(selected,
						new Point(event.getScreenX(), event.getScreenY()));
			}
		}
	};

	@Override
	public void activate() {
		((FXViewer) getViewer()).getCanvas().getScene()
				.addEventFilter(MouseEvent.MOUSE_PRESSED, pressedHandler);
		((FXViewer) getViewer()).getCanvas().getScene()
				.addEventFilter(MouseEvent.MOUSE_DRAGGED, draggedHandler);
	}

	@Override
	public void deactivate() {
		((FXViewer) getViewer()).getCanvas().getScene()
				.removeEventFilter(MouseEvent.MOUSE_DRAGGED, draggedHandler);
		((FXViewer) getViewer()).getCanvas().getScene()
				.removeEventFilter(MouseEvent.MOUSE_PRESSED, pressedHandler);
	}

}

package org.eclipse.gef4.mvc.javafx;

import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.aspects.dragging.AbstractDragTool;
import org.eclipse.gef4.mvc.aspects.selection.SelectionModel;
import org.eclipse.gef4.mvc.parts.IEditPart;

public class FXDragTool extends AbstractDragTool<Node> {

	private EventHandler<MouseEvent> pressedHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			for(IEditPart<Node> selected : (List<IEditPart<Node>>)getDomain().getProperty(SelectionModel.class).getSelection()){
				initDrag(selected, new Point(event.getScreenX(),
						event.getScreenY()));
			}
		}

	};
	
	private EventHandler<MouseEvent> draggedHandler = new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent event) {
			for(IEditPart<Node> selected : (List<IEditPart<Node>>)getDomain().getProperty(SelectionModel.class).getSelection()){
				commitDrag(selected,
						new Point(event.getScreenX(), event.getScreenY()));
			}
		}
	};

	@Override
	public void activate() {
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventFilter(MouseEvent.MOUSE_PRESSED, pressedHandler);
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.addEventFilter(MouseEvent.MOUSE_DRAGGED, draggedHandler);
	}

	@Override
	public void deactivate() {
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.removeEventFilter(MouseEvent.MOUSE_DRAGGED, draggedHandler);
		((FXViewer) getDomain().getViewer()).getCanvas().getScene()
				.removeEventFilter(MouseEvent.MOUSE_PRESSED, pressedHandler);
	}

}

package org.eclipse.gef4.mvc.fx;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.aspects.resizerelocate.AbstractRelocateTool;

public class FXRelocateTool extends AbstractRelocateTool<Node> {
	
	private FXMouseDragGesture gesture = new FXMouseDragGesture() {
		@Override
		protected void release(Node target, MouseEvent event, double dx, double dy) {
			commitRelocate(new Point(event.getSceneX(), event.getSceneY()));
		}
		
		@Override
		protected void press(Node target, MouseEvent event) {
			initRelocate(new Point(event.getSceneX(), event.getSceneY()));
		}
		
		@Override
		protected void drag(Node target, MouseEvent event, double dx, double dy) {
			performRelocate(new Point(event.getSceneX(), event.getSceneY()));
		}
	};

	@Override
	public void activate() {
		super.activate();
		gesture.setScene(((FXViewer) getDomain().getViewer()).getCanvas().getScene());
	}

	@Override
	public void deactivate() {
		gesture.setScene(null);
		super.deactivate();
	}

}

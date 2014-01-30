package org.eclipse.gef4.mvc.fx.tools;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.tools.AbstractRelocateSelectedTool;

public class FXRelocateSelectedTool extends AbstractRelocateSelectedTool<Node> {

	private FXMouseDragGesture gesture = new FXMouseDragGesture() {
		@Override
		protected void release(Node target, MouseEvent event, double dx,
				double dy) {
			// only drag selection, if the mouse target is selected
			IVisualPart<Node> mouseTargetPart = FXPartUtils.getMouseTargetPart(
					getDomain().getViewer(), event);
			if (getTargetParts().contains(mouseTargetPart)) {
				commitRelocate(new Point(event.getSceneX(), event.getSceneY()));
			}
		}

		@Override
		protected void press(Node target, MouseEvent event) {
			// only drag selection, if the mouse target is selected
			IVisualPart<Node> mouseTargetPart = FXPartUtils.getMouseTargetPart(
					getDomain().getViewer(), event);
			if (getTargetParts().contains(mouseTargetPart)) {
				initRelocate(new Point(event.getSceneX(), event.getSceneY()));
			}
		}

		@Override
		protected void drag(Node target, MouseEvent event, double dx, double dy) {
			// only drag selection, if the mouse target is selected
			IVisualPart<Node> mouseTargetPart = FXPartUtils.getMouseTargetPart(
					getDomain().getViewer(), event);
			if (getTargetParts().contains(mouseTargetPart)) {
				performRelocate(new Point(event.getSceneX(), event.getSceneY()));
			}
		}
	};

	@Override
	public void activate() {
		super.activate();
		gesture.setScene(((FXViewer) getDomain().getViewer()).getCanvas()
				.getScene());
	}

	@Override
	public void deactivate() {
		gesture.setScene(null);
		super.deactivate();
	}

}

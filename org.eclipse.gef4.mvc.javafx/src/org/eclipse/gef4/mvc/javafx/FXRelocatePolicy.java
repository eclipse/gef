package org.eclipse.gef4.mvc.javafx;

import javafx.geometry.Point2D;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.aspects.relocate.AbstractRelocatePolicy;
import org.eclipse.gef4.mvc.parts.INodeContentPart;

public class FXRelocatePolicy extends AbstractRelocatePolicy<Node> {

	Point absoluteStartPositionInScene;
	Point absoluteInitialMousePressedLocation;

	@Override
	public void initRelocate(Point absoluteMousePressLocation) {
		absoluteInitialMousePressedLocation = absoluteMousePressLocation;

		Node hostVisual = getHost().getVisual();
		Node parentVisual = ((INodeContentPart<Node>) getHost()).getParent()
				.getVisual();
		Point2D localToScene = parentVisual.localToScene(
				hostVisual.getLayoutX(), hostVisual.getLayoutY());
		// TODO: use conversion facility
		absoluteStartPositionInScene = new Point(localToScene.getX(),
				localToScene.getY());
	}

	@Override
	public void performRelocate(Point absoluteMouseDragLocation) {
		if (absoluteInitialMousePressedLocation != null) {
			Point delta = absoluteMouseDragLocation
					.getTranslated(absoluteInitialMousePressedLocation
							.getNegated());
			if (!(delta.equals(new Point()))) {
				Point absoluteEndPositionInScene = absoluteStartPositionInScene
						.getTranslated(delta);
				Node hostVisual = getHost().getVisual();
				Node parentVisual = ((INodeContentPart<Node>) getHost())
						.getParent().getVisual();
				Point2D sceneToLocal = parentVisual.sceneToLocal(new Point2D(
						absoluteEndPositionInScene.x,
						absoluteEndPositionInScene.y));
				if (sceneToLocal != null) {
					hostVisual.setLayoutX(sceneToLocal.getX());
					hostVisual.setLayoutY(sceneToLocal.getY());
				}
			}
		}
	}

	@Override
	public void commitRelocate(Point newAbsolutePosition) {
		// TODO: create IUndoableOperation and execute it on the
		// IOperationHistory
	}
}

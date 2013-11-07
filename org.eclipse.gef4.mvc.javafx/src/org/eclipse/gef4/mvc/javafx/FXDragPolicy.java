package org.eclipse.gef4.mvc.javafx;

import javafx.geometry.Point2D;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.parts.INodeEditPart;
import org.eclipse.gef4.mvc.policies.AbstractEditPolicy;
import org.eclipse.gef4.mvc.policies.IDragPolicy;

public class FXDragPolicy extends AbstractEditPolicy<Node> implements
		IDragPolicy<Node> {

	Point absoluteStartPositionInScene;
	Point absoluteInitialMousePressedLocation;

	@Override
	public void initDrag(Point absoluteMousePressLocation) {
		absoluteInitialMousePressedLocation = absoluteMousePressLocation;

		Node hostVisual = getHost().getVisual();
		Node parentVisual = ((INodeEditPart<Node>) getHost()).getParent()
				.getVisual();
		Point2D localToScene = parentVisual.localToScene(
				hostVisual.getLayoutX(), hostVisual.getLayoutY());
		// TODO: use conversion facility
		absoluteStartPositionInScene = new Point(localToScene.getX(),
				localToScene.getY());
	}

	@Override
	public void commitDrag(Point absoluteMouseDragLocation) {
		if (absoluteInitialMousePressedLocation != null) {
			Point delta = absoluteMouseDragLocation
					.getTranslated(absoluteInitialMousePressedLocation
							.getNegated());
			if (!(delta.equals(new Point()))) {
				Point absoluteEndPositionInScene = absoluteStartPositionInScene
						.getTranslated(delta);
				Node hostVisual = getHost().getVisual();
				Node parentVisual = ((INodeEditPart<Node>) getHost())
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
}

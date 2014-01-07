package org.eclipse.gef4.mvc.fx.anchors;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;

public class FXChopBoxAnchor extends AbstractFXAnchor {

	@Override
	protected Point calculatePosition(Node anchored) {
		// compute intersection point between outline of anchorage reference
		// shape and line through anchorage and anchor reference points.
		Line referenceLine = new Line(getAnchorageReferencePoint(), getAnchorReferencePoint(anchored));
		Point[] intersectionPoints = getAnchorageReferenceShape().getOutline().getIntersections(referenceLine);
		if(intersectionPoints.length > 0){
			return intersectionPoints[0];
		}
		return getAnchorageReferenceShape().getBounds().getCenter();
	}

	protected IShape getAnchorageReferenceShape() {
		return JavaFX2Geometry.toRectangle(getAnchorage().localToScene(
				getAnchorage().getBoundsInLocal()));
	}

	protected Point getAnchorageReferencePoint() {
		return getAnchorageReferenceShape().getBounds().getCenter();
	}

	protected Point getAnchorReferencePoint(Node anchored) {
		return JavaFX2Geometry.toRectangle(
				anchored.localToScene(anchored.getBoundsInLocal())).getCenter();
	}

}

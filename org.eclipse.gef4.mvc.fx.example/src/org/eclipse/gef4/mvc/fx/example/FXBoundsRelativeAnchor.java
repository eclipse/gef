package org.eclipse.gef4.mvc.fx.example;

import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.AbstractFXAnchor;

public class FXBoundsRelativeAnchor extends AbstractFXAnchor {

	private Point anchorageBoundsRelativeOffset;

	public FXBoundsRelativeAnchor(Point anchorageBoundsRelativeOffset) {
		this.anchorageBoundsRelativeOffset = anchorageBoundsRelativeOffset;
	}

	@Override
	protected Point calculatePositionInScene() {
		// calculate position based on scene position
		// TODO: Use GEF4 Geometry
		Bounds boundsInScene = getAnchorage().localToScene(
				getAnchorage().getBoundsInLocal());
		return new Point(boundsInScene.getMinX(), boundsInScene.getMinY())
				.getTranslated(anchorageBoundsRelativeOffset);
	}

}

package org.eclipse.gef4.mvc.fx.anchors;

import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Point;

public class FXFixPointAnchor extends AbstractFXAnchor {

	private Point anchorageBoundsRelativeOffset;

	public FXFixPointAnchor(Point anchorageBoundsRelativeOffset) {
		this.anchorageBoundsRelativeOffset = anchorageBoundsRelativeOffset;
	}

	@Override
	protected Point calculatePosition(Node anchored) {
		Bounds boundsInScene = getAnchorage().localToScene(
				getAnchorage().getBoundsInLocal());
		return new Point(boundsInScene.getMinX(), boundsInScene.getMinY())
				.getTranslated(anchorageBoundsRelativeOffset);
	}

}

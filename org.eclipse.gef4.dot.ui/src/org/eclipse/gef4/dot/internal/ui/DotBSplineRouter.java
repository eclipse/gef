package org.eclipse.gef4.dot.internal.ui;

import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.DynamicAnchor.AnchoredReferencePoint;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.anchors.StaticAnchor;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.StraightRouter;
import org.eclipse.gef4.geometry.planar.Point;

public class DotBSplineRouter extends StraightRouter {

	private Point startReferencePoint;
	private Point endReferencePoint;

	public DotBSplineRouter(Point startReferencePoint,
			Point endReferencePoint) {
		this.startReferencePoint = startReferencePoint;
		this.endReferencePoint = endReferencePoint;
	}

	@Override
	protected void updateComputationParameters(Connection connection,
			int anchorIndex) {
		if (anchorIndex == 0
				|| anchorIndex == connection.getAnchorsUnmodifiable().size() - 1) {
			IAnchor anchor = connection.getAnchorsUnmodifiable().get(anchorIndex);
			AnchorKey anchorKey = connection.getAnchorKey(anchorIndex);
			if (anchor instanceof DynamicAnchor) {
				((DynamicAnchor) anchor)
						.getComputationParameter(anchorKey,
								AnchoredReferencePoint.class)
						.set(anchorIndex == 0 ? startReferencePoint
								: endReferencePoint);
			} else if (anchor instanceof StaticAnchor) {
				((StaticAnchor) anchor).setReferencePosition(anchorIndex == 0
						? startReferencePoint : endReferencePoint);
			}
		} else {
			super.updateComputationParameters(connection, anchorIndex);
		}
	}
}
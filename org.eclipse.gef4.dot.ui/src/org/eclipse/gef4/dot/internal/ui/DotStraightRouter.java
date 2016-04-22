/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal.ui;

import org.eclipse.gef4.fx.anchors.AbstractComputationStrategy.AnchoredReferencePoint;
import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.StraightRouter;
import org.eclipse.gef4.geometry.planar.Point;

public class DotStraightRouter extends StraightRouter {

	private Point startReferencePoint;
	private Point endReferencePoint;

	public DotStraightRouter(Point startReferencePoint,
			Point endReferencePoint) {
		this.startReferencePoint = startReferencePoint;
		this.endReferencePoint = endReferencePoint;
	}

	public Point getStartReferencePoint() {
		return startReferencePoint;
	}

	@Override
	protected void updateReferencePoint(Connection connection, int index) {
		if (index == 0) {
			IAnchor startAnchor = connection.getAnchor(index);
			if (startAnchor instanceof DynamicAnchor) {
				((DynamicAnchor) startAnchor)
						.getComputationParameter(
								connection.getAnchorKey(index),
								AnchoredReferencePoint.class)
						.set(startReferencePoint);
			}
		} else if (index == connection.getAnchors().size() - 1) {
			IAnchor endAnchor = connection.getAnchor(index);
			if (endAnchor instanceof DynamicAnchor) {
				((DynamicAnchor) endAnchor)
						.getComputationParameter(
								connection.getAnchorKey(index),
								AnchoredReferencePoint.class)
						.set(endReferencePoint);
			}
		}
		super.updateReferencePoint(connection, index);
	}
}

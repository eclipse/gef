/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal.ui;

import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.OrthogonalRouter;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.zest.fx.ZestProperties;

public class DotOrthogonalRouter extends OrthogonalRouter {

	private org.eclipse.gef4.graph.Edge edge;
	private Point startReferencePoint;
	private Point endReferencePoint;

	public DotOrthogonalRouter(org.eclipse.gef4.graph.Edge edge,
			Point startReferencePoint, Point endReferencePoint) {
		this.edge = edge;
		this.startReferencePoint = startReferencePoint;
		this.endReferencePoint = endReferencePoint;
	}

	@Override
	protected Point getAnchorReferencePoint(Connection connection, int index) {
		if (Boolean.TRUE.equals(ZestProperties._getTouched(edge))) {
			return super.getAnchorReferencePoint(connection, index);
		}
		if (index == 0) {
			return startReferencePoint;
		} else if (index == connection.getAnchors().size() - 1) {
			return endReferencePoint;
		} else {
			return super.getAnchorReferencePoint(connection, index);
		}
	}

}

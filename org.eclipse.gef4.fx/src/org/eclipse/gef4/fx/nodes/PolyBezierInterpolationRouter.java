/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import java.util.List;

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;

/**
 * The {@link PolyBezierInterpolationRouter} interpolates a cubic Bezier spline
 * through the supplied {@link Point way points}.
 *
 * @author mwienand
 *
 */
public class PolyBezierInterpolationRouter implements IConnectionRouter {

	@Override
	public ICurve route(Connection connection) {
		List<Point> points = connection.getPoints();
		if (points.size() < 2) {
			return new Line(0, 0, 0, 0);
		}
		return PolyBezier.interpolateCubic(points.toArray(new Point[] {}));
	}

}

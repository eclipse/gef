/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.nodes;

import java.util.List;

import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.PolyBezier;

/**
 * The {@link PolyBezierInterpolator} interpolates a cubic Bezier spline through
 * the supplied {@link Point way points}.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class PolyBezierInterpolator extends AbstractInterpolator {

	@Override
	protected ICurve computeCurve(Connection connection) {
		List<Point> points = connection.getPointsUnmodifiable();
		if (points.size() < 2) {
			return new Line(0, 0, 0, 0);
		}
		return PolyBezier.interpolateCubic(points.toArray(new Point[] {}));
	}

}

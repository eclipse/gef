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

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;

/**
 * An {@link IFXConnectionRouter} {@link #routeConnection(Point[]) computes} an
 * {@link ICurve} based on a number of supplied {@link Point}s.
 *
 * @author mwienand
 *
 */
public interface IFXConnectionRouter {

	/**
	 * Computes the curve geometry for a connection based on the given points.
	 *
	 * @param points
	 *            start point, way points, and end point of the routed
	 *            connection.
	 * @return new curve geometry for the connection.
	 */
	public ICurve routeConnection(Point[] points);

}

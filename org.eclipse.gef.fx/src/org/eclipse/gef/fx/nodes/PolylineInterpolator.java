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

import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polyline;

/**
 * The {@link PolylineInterpolator} constructs a
 * {@link javafx.scene.shape.Polyline} through the given {@link Connection}'s
 * points.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class PolylineInterpolator extends AbstractInterpolator {

	@Override
	protected ICurve computeCurve(Connection connection) {
		return new Polyline(
				connection.getPointsUnmodifiable().toArray(new Point[] {}));
	}

}

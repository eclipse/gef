/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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

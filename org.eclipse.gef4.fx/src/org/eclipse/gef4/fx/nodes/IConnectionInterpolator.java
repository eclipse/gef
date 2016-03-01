/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen  (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import org.eclipse.gef4.geometry.planar.ICurve;

/**
 * An {@link IConnectionInterpolator} is responsible for
 * {@link #interpolate(Connection) computing} an {@link ICurve} that represents
 * the geometry of the {@link Connection}.
 */
public interface IConnectionInterpolator {

	/**
	 * An {@link ICurve} that is interpolated through the {@link Connection}'s
	 * points.
	 *
	 * @param connection
	 *            The {@link Connection} for which to interpolate an
	 *            {@link ICurve}.
	 * @return The {@link ICurve} that was interpolated through the
	 *         {@link Connection}'s points.
	 */
	ICurve interpolate(Connection connection);

}

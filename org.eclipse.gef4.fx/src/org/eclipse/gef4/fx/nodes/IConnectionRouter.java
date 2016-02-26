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
 *     Alexander Nyßen  (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import org.eclipse.gef4.geometry.planar.ICurve;

/**
 * An {@link IConnectionRouter} is responsible for {@link #route(Connection)
 * computing} an {@link ICurve} that represents the geometry of the
 * {@link Connection}.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public interface IConnectionRouter {

	/**
	 * Computes the {@link ICurve} geometry for the given connection. If
	 * necessary, the {@link IConnectionRouter router} may also adjust the
	 * {@link Connection connection's} control points.
	 *
	 * @param connection
	 *            The {@link Connection} to route.
	 *
	 * @return A new {@link ICurve} geometry representing the geometry that is
	 *         to be rendered for the given connection.
	 */
	public ICurve route(Connection connection);

}

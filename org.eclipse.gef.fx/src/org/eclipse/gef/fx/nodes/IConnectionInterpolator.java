/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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

/**
 * An {@link IConnectionInterpolator} is responsible for updating the
 * {@link Connection}'s {@link Connection#getCurve() curve node} (which includes
 * to properly clip it at the start and end decorations), as well as for
 * arranging the decorations.
 *
 * @author anyssen
 * @author mwienand
 */
public interface IConnectionInterpolator {

	/**
	 * Interpolates the given {@link Connection}, i.e updates its
	 * {@link Connection#getCurve() curve node} to reflect a respective
	 * geometry. The {@link IConnectionInterpolator} is also responsible of
	 * arranging the connection's {@link Connection#getStartDecoration() start}
	 * and {@link Connection#getEndDecoration() end} decorations (and has to
	 * ensure the curve node is properly clipped to not render through the
	 * decorations).
	 *
	 * @param connection
	 *            The {@link Connection} to interpolate.
	 */
	void interpolate(Connection connection);

}

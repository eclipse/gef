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

import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.IAnchor;

/**
 * An {@link IConnectionRouter} is responsible for {@link #route(Connection)
 * adjustment} of a {@link Connection}'s points. This may also include providing
 * reference points for {@link DynamicAnchor}s the connection is attached to.
 *
 * @author anyssen
 * @author mwienand
 */
public interface IConnectionRouter {

	/**
	 * Adjusts the {@link Connection connection's} anchors (if necessary) to
	 * satisfy certain routing constraints. This includes insertion of
	 * 'implicit' anchors, as well as updating the positions of existing anchors
	 * (which includes manipulating the
	 * {@link org.eclipse.gef.fx.anchors.IComputationStrategy.Parameter
	 * computation parameters} of {@link DynamicAnchor dynamic anchors}.
	 *
	 * @param connection
	 *            The {@link Connection} to route.
	 */
	public void route(Connection connection);

	/**
	 * Returns <code>true</code> if the given {@link IAnchor} was added by this
	 * {@link IConnectionRouter} during {@link #route(Connection)}. Otherwise
	 * returns <code>false</code>.
	 *
	 * @param anchor
	 *            The {@link IAnchor} that is tested for implicitness.
	 * @return <code>true</code> if the given {@link IAnchor} is implicit,
	 *         otherwise <code>false</code>.
	 */
	// TODO: Query explicit anchors using IBendableContentPart so that implicit
	// anchors can be identified without needing this method.
	public boolean wasInserted(IAnchor anchor);

}

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

import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.IAnchor;

/**
 * An {@link IConnectionRouter} is responsible for {@link #route(Connection)
 * adjustment} of a {@link Connection}'s points. This may also include providing
 * reference points for {@link DynamicAnchor}s the connection is attached to.
 *
 * @author mwienand
 * @author anyssen
 *
 */
public interface IConnectionRouter {

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
	public boolean isImplicitAnchor(IAnchor anchor);

	/**
	 * Adjusts the {@link Connection connection's} points (if necessary), which
	 * includes computing reference points for {@link DynamicAnchor}s (if any).
	 *
	 * @param connection
	 *            The {@link Connection} to route.
	 */
	public void route(Connection connection);

}

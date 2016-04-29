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

import java.util.Map;

import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.geometry.planar.Point;

import javafx.beans.property.ReadOnlyMapProperty;

/**
 * An {@link IConnectionRouter} is responsible for {@link #route(Connection)
 * adjustment} of a {@link Connection}'s points. This may also include providing
 * reference points for {@link DynamicAnchor}s the connection is attached to.
 *
 * @author mwienand
 * @author anyssen
 */
public interface IConnectionRouter {

	/**
	 * A {@link Map} that stores reference points for {@link AnchorKey}s.
	 *
	 * @return A {@link Map} that stores reference points for {@link AnchorKey}
	 *         s.
	 */
	public ReadOnlyMapProperty<AnchorKey, Point> positionHintsProperty();

	/**
	 * Adjusts the {@link Connection connection's} anchors (if necessary) to
	 * satisfy certain routing constraints. This includes insertion of
	 * 'implicit' anchors, as well as updating the positions of existing anchors
	 * (which includes manipulating the
	 * {@link org.eclipse.gef4.fx.anchors.IComputationStrategy.Parameter
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
	public boolean wasInserted(IAnchor anchor);

}

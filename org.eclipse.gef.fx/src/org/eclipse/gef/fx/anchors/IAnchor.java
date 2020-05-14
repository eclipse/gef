/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
package org.eclipse.gef.fx.anchors;

import org.eclipse.gef.geometry.planar.Point;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableMap;
import javafx.scene.Node;

/**
 * An {@link IAnchor} is a visual anchor that will provide positions for
 * attached {@link AnchorKey}s (i.e. anchored {@link Node}s) in local
 * coordinates of the {@link AnchorKey}'s anchored {@link Node}.
 * <p>
 * The position for each attached {@link Node} will be recomputed in case the
 * attached {@link Node} or any of its ancestors are changed in a way that has
 * an effect on the position being provided for the attached {@link Node}. The
 * {@link #positionsUnmodifiableProperty()} will be updated accordingly, it may
 * be monitored for changes.
 * <p>
 * An {@link IAnchor} may be bound to an anchorage {@link Node}. If this is the
 * case, positions for all attached {@link Node}s will also be recomputed in
 * case the anchorage {@link Node} or any of its ancestors are changed in a way
 * that will have an effect on the position of the attached {@link Node}.
 *
 * @author anyssen
 * @author mwienand
 */
public interface IAnchor {

	/**
	 * Provides a read-only property with the anchorage {@link Node} this
	 * {@link IAnchor} is bound to. The property value may be <code>null</code>
	 * in case this {@link IAnchor} is not bound to an anchorage {@link Node}.
	 *
	 * @return A read-only property storing the anchorage {@link Node}.
	 */
	ReadOnlyObjectProperty<Node> anchorageProperty();

	/**
	 * Attaches the given {@link AnchorKey} to this {@link IAnchor}.
	 *
	 * @param key
	 *            The {@link AnchorKey} to be attached.
	 */
	void attach(AnchorKey key);

	/**
	 * Detaches the given {@link AnchorKey} from this {@link IAnchor}.
	 *
	 * @param key
	 *            The {@link AnchorKey} to be detached.
	 */
	void detach(AnchorKey key);

	/**
	 * Provides the anchorage {@link Node} this {@link IAnchor} is bound to.
	 * Will return the value of the {@link #anchorageProperty()}.
	 *
	 * @return The value of the {@link #anchorageProperty()}.
	 */
	Node getAnchorage();

	/**
	 * Provides a position for the given {@link AnchorKey}. The provided
	 * {@link AnchorKey} has to be attached to this {@link IAnchor} (see
	 * {@link #attach(AnchorKey)}).
	 *
	 * @param key
	 *            The {@link AnchorKey} to retrieve a position for. It has to be
	 *            attached ({@link #attach(AnchorKey)}) to this {@link IAnchor}
	 *            before.
	 * @return The position for the given {@link AnchorKey} within local
	 *         coordinates of the {@link AnchorKey}'s anchored {@link Node}.
	 */
	Point getPosition(AnchorKey key);

	/**
	 * Provides an observable read-only (map) with positions (in local
	 * coordinates of the anchored {@link Node}s) for all attached
	 * {@link AnchorKey}s. The positions will be updated for all attached
	 * {@link AnchorKey}s if the attached {@link Node}s or the anchorage
	 * {@link Node}, the {@link IAnchor} is bound to, or any of their ancestors
	 * changes in a way that will effect the positions (within the local
	 * coordinate space of the attached {@link Node}s).
	 *
	 * @return A read-only observable map storing positions for all attached
	 *         {@link AnchorKey}s.
	 */
	public ObservableMap<AnchorKey, Point> getPositionsUnmodifiable();

	/**
	 * Determines if the given {@link AnchorKey} is currently attached to this
	 * {@link IAnchor}.
	 *
	 * @param key
	 *            The {@link AnchorKey} to test.
	 * @return <code>true</code> if the given key is attached, otherwise
	 *         <code>false</code>.
	 */
	boolean isAttached(AnchorKey key);

	/**
	 * Provides a read-only (map) property with positions (in local coordinates
	 * of the anchored {@link Node}s) for all attached {@link AnchorKey}s. The
	 * positions will be updated for all attached {@link AnchorKey}s if the
	 * attached {@link Node}s or the anchorage {@link Node}, the {@link IAnchor}
	 * is bound to, or any of their ancestors changes in a way that will effect
	 * the positions (within the local coordinate space of the attached
	 * {@link Node}s).
	 *
	 * @return A read-only (map) property storing positions for all attached
	 *         {@link AnchorKey}s.
	 */
	public ReadOnlyMapProperty<AnchorKey, Point> positionsUnmodifiableProperty();

}

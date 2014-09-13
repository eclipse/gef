/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.anchors;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.geometry.planar.Point;

/**
 * A visual anchor that can provide positions for attached {@link AnchorKey}s
 * (i.e. anchored {@link Node}s) in local coordinates of the {@link AnchorKey}'s
 * anchored {@link Node}. An {@link IFXAnchor} may be bound to an anchorage
 * {@link Node}. If this is the case, it will recompute positions for all
 * attached anchored {@link Node}s in case the anchorage {@link Node} or any of
 * its ancestors are changed in a way that will have an effect on the position
 * (in local coordinates of the anchored {@link Node}s).
 *
 * @author anyssen
 */
public interface IFXAnchor {

	/**
	 * Provides a read-only property with the anchorage {@link Node} this
	 * {@link IFXAnchor} is attached to. The property value may be
	 * <code>null</code> in case this {@link IFXAnchor} is not bound to an
	 * anchorage {@link Node}.
	 *
	 * @return A read-only property storing the anchorage {@link Node}.
	 */
	ReadOnlyObjectProperty<Node> anchorageProperty();

	/**
	 * Attaches the given {@link AnchorKey} to this {@link IFXAnchor}.
	 *
	 * @param key
	 *            The {@link AnchorKey} to be attached.
	 * @param info
	 *            An {@link IAdaptable}, which may be adapted by the
	 *            {@link IFXAnchor} to obtain additional information. May be
	 *            <code>null</code> if the respective {@link IFXAnchor} does not
	 *            require additional information.
	 */
	void attach(AnchorKey key, IAdaptable info);

	/**
	 * Detaches the given {@link AnchorKey} from this {@link IFXAnchor}.
	 *
	 * @param key
	 *            The {@link AnchorKey} to be detached.
	 * @param info
	 *            An {@link IAdaptable}, which may be adapted by the
	 *            {@link IFXAnchor} to obtain additional information. May be
	 *            <code>null</code> if the respective {@link IFXAnchor} does not
	 *            require additional information.
	 */
	void detach(AnchorKey key, IAdaptable info);

	/**
	 * Provides the anchorage {@link Node} this {@link IFXAnchor} is bound to.
	 * Will return the value of the {@link #anchorageProperty()}.
	 *
	 * @return The value of the {@link #anchorageProperty()}.
	 */
	Node getAnchorage();

	/**
	 * Provides a position for the given {@link AnchorKey}. The provided
	 * {@link AnchorKey} has to be attached to this {@link IFXAnchor} (see
	 * {@link #attach(AnchorKey, IAdaptable)}).
	 *
	 * @param key
	 *            The {@link AnchorKey} to retrieve a position for. It has to be
	 *            attached ({@link #attach(AnchorKey, IAdaptable)}) to this
	 *            {@link IFXAnchor} before.
	 * @return The position for the given {@link AnchorKey} within local
	 *         coordinates of the {@link AnchorKey}'s anchored {@link Node}.
	 */
	Point getPosition(AnchorKey key);

	/**
	 * Determines if the given {@link AnchorKey} is currently attached to this
	 * {@link IFXAnchor}.
	 *
	 * @param key
	 *            The {@link AnchorKey} to test.
	 * @return <code>true</code> if the given key is attached, otherwise
	 *         <code>false</code>.
	 */
	boolean isAttached(AnchorKey key);

	/**
	 * Provides a read-only (map) property with positions (in local coordinates
	 * of the anchored) for all attached {@link AnchorKey}s. The positions will
	 * be updated for all attached {@link AnchorKey}s if the anchorage
	 * {@link Node} or any of its ancestors changes in a way that will effect
	 * them.
	 *
	 * @return A read-only (map) property storing positions for all attached
	 *         {@link AnchorKey}s.
	 */
	ReadOnlyMapProperty<AnchorKey, Point> positionProperty();

}

/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Ny??en (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.fx.anchors;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Point;

/**
 * 
 * @author anyssen
 * 
 */
public interface IFXAnchor {

	/**
	 * @return property storing the anchorage {@link Node}
	 */
	ReadOnlyObjectProperty<Node> anchorageNodeProperty();

	/**
	 * Attaches the given {@link AnchorKey} to this {@link IFXAnchor}.
	 */
	void attach(AnchorKey key);

	/**
	 * Detaches the given {@link AnchorKey} from this {@link IFXAnchor}.
	 */
	void detach(AnchorKey key);

	/**
	 * @return value of {@link #anchorageNodeProperty()}
	 */
	Node getAnchorageNode();

	/**
	 * @param key
	 *            the {@link AnchorKey} to retrieve a position for
	 * @return position for the given anchored
	 */
	Point getPosition(AnchorKey key);

	/**
	 * Determines if the given {@link AnchorKey} is currently attached to this
	 * {@link IFXAnchor}.
	 * 
	 * @param key
	 * @return <code>true</code> if the given key is attached, otherwise
	 *         <code>false</code>.
	 */
	boolean isAttached(AnchorKey key);

	/**
	 * @return property storing positions for keys (map)
	 */
	ReadOnlyMapProperty<AnchorKey, Point> positionProperty();

}

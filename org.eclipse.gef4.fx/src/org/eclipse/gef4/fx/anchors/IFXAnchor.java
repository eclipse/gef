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
 * @param <V>
 */
public interface IFXAnchor {

	/**
	 * @return property storing the anchorage {@link Node}
	 */
	ReadOnlyObjectProperty<Node> anchorageNodeProperty();

	/**
	 * @return value of {@link #anchorageNodeProperty()}
	 */
	Node getAnchorageNode();

	/**
	 * @param anchored
	 * @return position for the given anchored
	 */
	Point getPosition(AnchorKey key);

	/**
	 * @return property storing positions for anchoreds (map)
	 */
	ReadOnlyMapProperty<AnchorKey, Point> positionProperty();

}

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

import javafx.beans.property.MapProperty;
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
public interface IFXNodeAnchor {

	ReadOnlyObjectProperty<Node> anchorageNodeProperty();
	
	Node getAnchorageNode();

	MapProperty<Node, Point> referencePointProperty();

	ReadOnlyMapProperty<Node, Point> positionProperty();

	void setReferencePoint(Node anchored, Point referencePoint);

	Point getReferencePoint(Node anchored);

	Point getPosition(Node anchored);

	Point computePosition(Node anchored, Point referencePoint);

	void recomputePositions();

}

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

import java.beans.PropertyChangeListener;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Point;

/**
 * 
 * @author anyssen
 *
 * @param <V>
 */
public interface IFXNodeAnchor {

	// TODO: use FX observable property to notify about position changes 
	public final static String REPRESH = "anchorageReferenceShape";
	
	public void addPropertyChangeListener(PropertyChangeListener listener);

	public void removePropertyChangeListener(PropertyChangeListener listener);

	Node getAnchorage();
	
	Point getPosition(Node anchored, Point referencePosition);
	
}

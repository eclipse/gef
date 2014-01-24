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
package org.eclipse.gef4.mvc.anchors;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.IPropertyChangeSupport;

/**
 * 
 * @author anyssen
 *
 * @param <V>
 */
public interface IAnchor<V> extends IPropertyChangeSupport {

	// TODO: create dedicated interface to notify that position is invalidated and has to be re-retrieved via the getPosition callback. 
	public final static String REPRESH = "anchorageReferenceShape";

	V getAnchorage();

	void setAnchorage(V anchorage);

	Point getPosition(V anchored, Point referencePosition);
	
}

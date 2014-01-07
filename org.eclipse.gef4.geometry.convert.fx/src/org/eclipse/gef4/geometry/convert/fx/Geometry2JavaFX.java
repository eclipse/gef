/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *    
 *******************************************************************************/
package org.eclipse.gef4.geometry.convert.fx;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;

public class Geometry2JavaFX {

	private Geometry2JavaFX() {
		// this class should not be instantiated by clients
	}

	public static final Bounds toFXBounds(Rectangle r) {
		return new BoundingBox(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}
	
	public static final Point2D toFXPoint(Point p){
		return new Point2D(p.x, p.y);
	}
}

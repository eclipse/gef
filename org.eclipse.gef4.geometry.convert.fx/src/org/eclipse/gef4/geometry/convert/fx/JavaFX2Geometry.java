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

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.transform.Transform;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.Path.Segment;

public class JavaFX2Geometry {

	private JavaFX2Geometry() {
		// this class should not be instantiated by clients
	}
	
	public static final AffineTransform toAffineTransform(
			Transform t) {
		return new AffineTransform(t.getMxx(), t.getMxy(), t.getMyx(), t.getMyy(), t.getTx(), t.getTy());
	}

	public static final Rectangle toRectangle(Bounds b) {
		return new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(),
				b.getHeight());
	}
	
	public static final Point toPoint(Point2D point){
		return new Point(point.getX(), point.getY());
	}
	
}

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
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;

import org.eclipse.gef4.geometry.planar.Path;
import org.eclipse.gef4.geometry.planar.Path.Segment;
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
	
	public static PathElement[] toPathElements(Path path) {
		Segment[] segments = path.getSegments();
		PathElement[] elements = new PathElement[segments.length];
		for (int i = 0; i < segments.length; i++) {
			Point[] points = segments[i].getPoints();
//			if (points.length > 0) {
//				System.out.println(i + ": " + points[points.length - 1]);
//			}
			switch (segments[i].getType()) {
			case Segment.MOVE_TO:
				elements[i] = new MoveTo(points[0].x, points[0].y);
				break;
			case Segment.LINE_TO:
				elements[i] = new LineTo(points[0].x, points[0].y);
				break;
			case Segment.QUAD_TO:
				elements[i] = new QuadCurveTo(points[0].x, points[0].y,
						points[1].x, points[1].y);
				break;
			case Segment.CUBIC_TO:
				elements[i] = new CubicCurveTo(points[0].x, points[0].y,
						points[1].x, points[1].y, points[2].x, points[2].y);
				break;
			case Segment.CLOSE:
				elements[i] = new ClosePath();
				break;
			default:
				throw new IllegalStateException("Unknown Path.Segment: <"
						+ segments[i] + ">");
			}
		}
		return elements;
	}
	
	public static javafx.scene.shape.Path toPath(Path path) {
		javafx.scene.shape.Path fxPath = new javafx.scene.shape.Path(toPathElements(path));
		fxPath.setFillRule(path.getWindingRule() == Path.WIND_EVEN_ODD ? FillRule.EVEN_ODD : FillRule.NON_ZERO);
		return fxPath;
	}
	
}

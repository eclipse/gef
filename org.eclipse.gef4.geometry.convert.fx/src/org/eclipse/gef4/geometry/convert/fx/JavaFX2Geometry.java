/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.transform.Transform;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.Path.Segment;

public class JavaFX2Geometry {

	private JavaFX2Geometry() {
		// this class should not be instantiated by clients
	}

	public static final AffineTransform toAffineTransform(Transform t) {
		return new AffineTransform(t.getMxx(), t.getMyx(), t.getMxy(),
				t.getMyy(), t.getTx(), t.getTy());
	}

	public static final Rectangle toRectangle(Bounds b) {
		return new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(),
				b.getHeight());
	}

	public static final Point toPoint(Point2D point) {
		return new Point(point.getX(), point.getY());
	}

	public static final org.eclipse.gef4.geometry.planar.Path toPath(Path path) {
		ObservableList<PathElement> elements = path.getElements();
		org.eclipse.gef4.geometry.planar.Path.Segment[] segments = new org.eclipse.gef4.geometry.planar.Path.Segment[elements
				.size()];

		for (int i = 0; i < segments.length; i++) {
			PathElement element = elements.get(i);
			if (element instanceof MoveTo) {
				MoveTo moveTo = (MoveTo) element;
				segments[i] = new Segment(Segment.MOVE_TO, new Point(
						moveTo.getX(), moveTo.getY()));
			} else if (element instanceof LineTo) {
				LineTo lineTo = (LineTo) element;
				segments[i] = new Segment(Segment.LINE_TO, new Point(
						lineTo.getX(), lineTo.getY()));
			} else if (element instanceof QuadCurveTo) {
				QuadCurveTo quadTo = (QuadCurveTo) element;
				segments[i] = new Segment(Segment.QUAD_TO, new Point(
						quadTo.getControlX(), quadTo.getControlY()), new Point(
						quadTo.getX(), quadTo.getY()));
			} else if (element instanceof CubicCurveTo) {
				CubicCurveTo cubicTo = (CubicCurveTo) element;
				segments[i] = new Segment(Segment.CUBIC_TO, new Point(
						cubicTo.getControlX1(), cubicTo.getControlY1()),
						new Point(cubicTo.getControlX2(), cubicTo
								.getControlY2()), new Point(cubicTo.getX(),
								cubicTo.getY()));
			} else if (element instanceof ClosePath) {
				segments[i] = new Segment(Segment.CLOSE);
			}
		}

		int windingRule = path.getFillRule() == FillRule.EVEN_ODD ? org.eclipse.gef4.geometry.planar.Path.WIND_EVEN_ODD
				: org.eclipse.gef4.geometry.planar.Path.WIND_NON_ZERO;

		return new org.eclipse.gef4.geometry.planar.Path(windingRule, segments);
	}

}

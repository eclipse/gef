/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.fx.nodes;

import javafx.geometry.Bounds;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Arc;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IScalable;
import org.eclipse.gef4.geometry.planar.Path.Segment;
import org.eclipse.gef4.geometry.planar.Pie;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;

import com.sun.javafx.geom.Shape;

/**
 * A {@link Shape} that can be constructed using an underlying {@link IGeometry}
 * . In contrast to {@link Shape} {@link FXGeometryNode} is resizable,
 * performing a scale in case the underlying {@link IGeometry} is not directly
 * resizable.
 * 
 * @author nyssen
 * 
 * @param <T> An {@link IGeometry} used to define this {@link FXGeometryNode}
 */
public class FXGeometryNode<T extends IGeometry> extends Path {

	public static PathElement[] toPathElements(IGeometry geom) {
		Segment[] segments = geom.toPath().getSegments();
		PathElement[] elements = new PathElement[segments.length];
		for (int i = 0; i < segments.length; i++) {
			Point[] points = segments[i].getPoints();
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

	private T geometry;

	public FXGeometryNode() {
		super();
	}

	public FXGeometryNode(T geom) {
		setGeometry(geom);
	}

	public T getGeometry() {
		return geometry;
	}

	@Override
	public boolean isResizable() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void resize(double width, double height) {
		if (geometry instanceof Rectangle) {
			((Rectangle) geometry).setSize(width, height);
		} else if (geometry instanceof RoundedRectangle) {
			((RoundedRectangle) geometry).setSize(width, height);
		} else if (geometry instanceof Ellipse) {
			((Ellipse) geometry).setSize(width, height);
		} else if (geometry instanceof Pie) {
			((Pie) geometry).setSize(width, height);
		} else if (geometry instanceof Arc) {
			((Arc) geometry).setSize(width, height);
		} else {
			Bounds bounds = getLayoutBounds();
			double sx = width / bounds.getWidth();
			double sy = height / bounds.getHeight();
			if (geometry instanceof IScalable) {
				// Line, Polyline, PolyBezier, BezierCurve, CubicCurve,
				// QuadraticCurve, Polygon, CurvedPolygon, Region, and Ring are
				// not
				// directly resizable but scalable
				((IScalable<T>) geometry).scale(sx, sy, geometry.getBounds()
						.getX(), geometry.getBounds().getY());
			} else {
				// apply transform to path
				Point boundsOrigin = new Point(bounds.getMinX(),
						bounds.getMinY());
				geometry = (T) geometry
						.getTransformed(
								new AffineTransform(1, 0, 0, 1,
										-boundsOrigin.x, -boundsOrigin.y))
						.getTransformed(new AffineTransform(sx, 0, 0, sy, 0, 0))
						.getTransformed(
								new AffineTransform(1, 0, 0, 1, boundsOrigin.x,
										boundsOrigin.y));
			}

		}
		updatePathElements();
	}

	public void setGeometry(T geometry) {
		this.geometry = geometry;
		updatePathElements();
	}

	/**
	 * Updates the visual representation (Path) of this GeometryNode. This is
	 * done automatically when setting the geometry. But in case you change
	 * properties of a geometry, you have to call this method in order to update
	 * its visual counter part.
	 */
	public void updatePathElements() {
		getElements().clear();
		getElements().addAll(toPathElements(geometry));
	}
}

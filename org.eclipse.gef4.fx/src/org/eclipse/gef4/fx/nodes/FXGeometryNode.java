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

import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;

import org.eclipse.gef4.geometry.planar.Arc;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Path.Segment;
import org.eclipse.gef4.geometry.planar.Pie;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.geometry.planar.RoundedRectangle;

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
		// up to now, support resize for all AbstractRectangleBasedGeometries
		// TODO: we could enable this in general in case we know how to resize
		// other geometries
		return geometry instanceof Rectangle
				|| geometry instanceof RoundedRectangle
				|| geometry instanceof Ellipse || geometry instanceof Arc
				|| geometry instanceof Pie;
	}

	@Override
	public void resize(double width, double height) {
		if (geometry instanceof Rectangle) {
			((Rectangle) geometry).setSize(width, height);
		} else if (geometry instanceof RoundedRectangle) {
			((RoundedRectangle) geometry).setSize(width, height);
		} else if (geometry instanceof Ellipse) {
			((Ellipse) geometry).setSize(width, height);
		} else if (geometry instanceof Arc) {
			((Arc) geometry).setSize(width, height);
		} else if (geometry instanceof Pie) {
			((Pie) geometry).setSize(width, height);
		} else {
			super.resize(width, height);
			// TODO: we could resize by scaling the shape according to its
			// bounds
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

/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.fx.utils;

import org.eclipse.gef.geometry.planar.Arc;
import org.eclipse.gef.geometry.planar.CubicCurve;
import org.eclipse.gef.geometry.planar.Ellipse;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Path;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polygon;
import org.eclipse.gef.geometry.planar.Polyline;
import org.eclipse.gef.geometry.planar.QuadraticCurve;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.geometry.planar.RoundedRectangle;
import org.eclipse.gef.geometry.planar.Path.Segment;

import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Shape;

/**
 * The utility class {@link Geometry2Shape} provides methods for the conversion
 * of {@link IGeometry} implementations to JavaFX {@link Shape} implementations.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class Geometry2Shape {

	/**
	 * Returns a {@link javafx.scene.shape.Arc} that visualizes the given
	 * {@link Arc}.
	 *
	 * @param arc
	 *            The {@link Arc} from which a {@link javafx.scene.shape.Arc} is
	 *            constructed.
	 * @return A newly created {@link javafx.scene.shape.Arc} that visualizes
	 *         the given {@link Arc}.
	 */
	public static javafx.scene.shape.Arc toArc(Arc arc) {
		return new javafx.scene.shape.Arc(arc.getX() + arc.getWidth() / 2,
				arc.getY() + arc.getHeight() / 2, arc.getWidth() / 2,
				arc.getHeight() / 2, arc.getStartAngle().deg(),
				arc.getAngularExtent().deg());
	}

	/**
	 * Returns a {@link javafx.scene.shape.CubicCurve} that visualizes the given
	 * {@link CubicCurve}.
	 *
	 * @param cubic
	 *            The {@link CubicCurve} from which a
	 *            {@link javafx.scene.shape.CubicCurve} is constructed.
	 * @return A newly created {@link javafx.scene.shape.CubicCurve} that
	 *         visualizes the given {@link CubicCurve}.
	 */
	public static javafx.scene.shape.CubicCurve toCubicCurve(CubicCurve cubic) {
		return new javafx.scene.shape.CubicCurve(cubic.getX1(), cubic.getY1(),
				cubic.getCtrlX1(), cubic.getCtrlY1(), cubic.getCtrlX2(),
				cubic.getCtrlY2(), cubic.getX2(), cubic.getY2());
	}

	/**
	 * Returns a {@link javafx.scene.shape.Ellipse} that visualizes the given
	 * {@link Ellipse}.
	 *
	 * @param ellipse
	 *            The {@link Ellipse} from which a
	 *            {@link javafx.scene.shape.Ellipse} is constructed.
	 * @return A newly created {@link javafx.scene.shape.Ellipse} that
	 *         visualizes the given {@link Ellipse}.
	 */
	public static javafx.scene.shape.Ellipse toEllipse(Ellipse ellipse) {
		return new javafx.scene.shape.Ellipse(
				ellipse.getX() + ellipse.getWidth() / 2,
				ellipse.getY() + ellipse.getHeight() / 2,
				ellipse.getWidth() / 2, ellipse.getHeight() / 2);
	}

	/**
	 * Returns a {@link javafx.scene.shape.Line} that visualizes the given
	 * {@link Line}.
	 *
	 * @param line
	 *            The {@link Line} from which a {@link javafx.scene.shape.Line}
	 *            is constructed.
	 * @return A newly created {@link javafx.scene.shape.Line} that visualizes
	 *         the given {@link Line}.
	 */
	public static javafx.scene.shape.Line toLine(Line line) {
		return new javafx.scene.shape.Line(line.getX1(), line.getY1(),
				line.getX2(), line.getY2());
	}

	/**
	 * Converts the given {@link Path} to a JavaFX
	 * {@link javafx.scene.shape.Path}.
	 *
	 * @param path
	 *            The {@link Path} to convert.
	 * @return The new JavaFX {@link javafx.scene.shape.Path}.
	 */
	public static javafx.scene.shape.Path toPath(Path path) {
		javafx.scene.shape.Path fxPath = new javafx.scene.shape.Path(
				toPathElements(path));
		fxPath.setFillRule(path.getWindingRule() == Path.WIND_EVEN_ODD
				? FillRule.EVEN_ODD : FillRule.NON_ZERO);
		return fxPath;
	}

	/**
	 * Converts the given {@link Path} to an array of JavaFX {@link PathElement}
	 * s.
	 *
	 * @param path
	 *            The {@link Path} to convert.
	 * @return The new array of {@link PathElement}s.
	 */
	public static PathElement[] toPathElements(Path path) {
		Segment[] segments = path.getSegments();
		PathElement[] elements = new PathElement[segments.length];
		for (int i = 0; i < segments.length; i++) {
			Point[] points = segments[i].getPoints();
			// if (points.length > 0) {
			// System.out.println(i + ": " + points[points.length - 1]);
			// }
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
				throw new IllegalStateException(
						"Unknown Path.Segment: <" + segments[i] + ">");
			}
		}
		return elements;
	}

	/**
	 * Returns a {@link javafx.scene.shape.Polygon} that visualizes the given
	 * {@link Polygon}.
	 *
	 * @param polygon
	 *            The {@link Polygon} from which a
	 *            {@link javafx.scene.shape.Polygon} is constructed.
	 * @return A newly created {@link javafx.scene.shape.Polygon} that
	 *         visualizes the given {@link Polygon}.
	 */
	public static javafx.scene.shape.Polygon toPolygon(Polygon polygon) {
		return new javafx.scene.shape.Polygon(polygon.getCoordinates());
	}

	/**
	 * Returns a {@link javafx.scene.shape.Polyline} that visualizes the given
	 * {@link Polyline}.
	 *
	 * @param polyline
	 *            The {@link Polyline} from which a
	 *            {@link javafx.scene.shape.Polyline} is constructed.
	 * @return A newly created {@link javafx.scene.shape.Polyline} that
	 *         visualizes the given {@link Polyline}.
	 */
	public static javafx.scene.shape.Polyline toPolyline(Polyline polyline) {
		return new javafx.scene.shape.Polyline(polyline.getCoordinates());
	}

	/**
	 * Returns a {@link javafx.scene.shape.QuadCurve} that visualizes the given
	 * {@link QuadraticCurve}.
	 *
	 * @param quadCurve
	 *            The {@link QuadraticCurve} from which a
	 *            {@link javafx.scene.shape.QuadCurve} is constructed.
	 * @return A newly created {@link javafx.scene.shape.QuadCurve} that
	 *         visualizes the given {@link QuadraticCurve}.
	 */
	public static javafx.scene.shape.QuadCurve toQuadCurve(
			QuadraticCurve quadCurve) {
		return new javafx.scene.shape.QuadCurve(quadCurve.getX1(),
				quadCurve.getY1(), quadCurve.getCtrlX(), quadCurve.getCtrlY(),
				quadCurve.getX2(), quadCurve.getY2());
	}

	/**
	 * Returns a {@link javafx.scene.shape.Rectangle} that visualizes the given
	 * {@link Rectangle}.
	 *
	 * @param rect
	 *            The {@link Rectangle} from which a
	 *            {@link javafx.scene.shape.Rectangle} is constructed.
	 * @return A newly created {@link javafx.scene.shape.Rectangle} that
	 *         visualizes the given {@link Rectangle}.
	 */
	public static javafx.scene.shape.Rectangle toRectangle(Rectangle rect) {
		return new javafx.scene.shape.Rectangle(rect.getX(), rect.getY(),
				rect.getWidth(), rect.getHeight());
	}

	/**
	 * Returns a {@link javafx.scene.shape.Rectangle} that visualizes the given
	 * {@link RoundedRectangle}.
	 *
	 * @param roundedRect
	 *            The {@link RoundedRectangle} from which a
	 *            {@link javafx.scene.shape.Rectangle} is constructed.
	 * @return A newly created {@link javafx.scene.shape.Rectangle} that
	 *         visualizes the given {@link RoundedRectangle}.
	 */
	public static javafx.scene.shape.Rectangle toRectangle(
			RoundedRectangle roundedRect) {
		javafx.scene.shape.Rectangle rectangle = new javafx.scene.shape.Rectangle(
				roundedRect.getX(), roundedRect.getY(), roundedRect.getWidth(),
				roundedRect.getHeight());
		rectangle.setArcWidth(roundedRect.getArcWidth());
		rectangle.setArcHeight(roundedRect.getArcHeight());
		return rectangle;
	}

	/**
	 * Creates a {@link Shape} that visualizes the passed-in {@link IGeometry} .
	 *
	 * @param geometry
	 *            The {@link IGeometry} for which a {@link Shape} is created.
	 * @return A newly created {@link Shape} that visualizes the given
	 *         {@link IGeometry}.
	 */
	public static Shape toShape(IGeometry geometry) {
		if (geometry instanceof Arc) {
			return toArc((Arc) geometry);
		} else if (geometry instanceof CubicCurve) {
			return toCubicCurve((CubicCurve) geometry);
		} else if (geometry instanceof Ellipse) {
			return toEllipse((Ellipse) geometry);
		} else if (geometry instanceof Line) {
			return toLine((Line) geometry);
		} else if (geometry instanceof Path) {
			return toPath((Path) geometry);
		} else if (geometry instanceof Polygon) {
			return toPolygon((Polygon) geometry);
		} else if (geometry instanceof Polyline) {
			return toPolyline((Polyline) geometry);
		} else if (geometry instanceof QuadraticCurve) {
			QuadraticCurve quad = (QuadraticCurve) geometry;
			return toQuadCurve(quad);
		} else if (geometry instanceof Rectangle) {
			return toRectangle((Rectangle) geometry);
		} else if (geometry instanceof RoundedRectangle) {
			return toRectangle((RoundedRectangle) geometry);
		} else {
			return toPath(geometry.toPath());
		}
	}

}

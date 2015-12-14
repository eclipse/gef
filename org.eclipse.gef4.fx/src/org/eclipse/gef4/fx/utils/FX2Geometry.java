/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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
package org.eclipse.gef4.fx.utils;

import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Path.Segment;
import org.eclipse.gef4.geometry.planar.Point;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;

/**
 * The utility class {@link FX2Geometry} provides methods for the conversion of
 * JavaFX {@link Node} implementations to {@link IGeometry} implementations.
 *
 * @author mwienand
 *
 */
public class FX2Geometry {

	/**
	 * Converts the given JavaFX {@link Arc} to a
	 * {@link org.eclipse.gef4.geometry.planar.Arc}.
	 *
	 * @param arc
	 *            The JavaFX {@link Arc} to convert.
	 * @return The corresponding {@link org.eclipse.gef4.geometry.planar.Arc}.
	 */
	public static org.eclipse.gef4.geometry.planar.Arc toArc(Arc arc) {
		return new org.eclipse.gef4.geometry.planar.Arc(
				arc.getCenterX() - arc.getRadiusX(),
				arc.getCenterY() - arc.getRadiusY(),
				arc.getRadiusX() + arc.getRadiusX(),
				arc.getRadiusY() + arc.getRadiusY(),
				Angle.fromDeg(arc.getStartAngle()),
				Angle.fromDeg(arc.getLength()));
	}

	/**
	 * Converts the given JavaFX {@link CubicCurve} to a
	 * {@link org.eclipse.gef4.geometry.planar.CubicCurve}.
	 *
	 * @param cubic
	 *            The JavaFX {@link CubicCurve} to convert.
	 * @return The corresponding
	 *         {@link org.eclipse.gef4.geometry.planar.CubicCurve}.
	 */
	public static org.eclipse.gef4.geometry.planar.CubicCurve toCubicCurve(
			CubicCurve cubic) {
		return new org.eclipse.gef4.geometry.planar.CubicCurve(
				cubic.getStartX(), cubic.getStartY(), cubic.getControlX1(),
				cubic.getControlY1(), cubic.getControlX2(),
				cubic.getControlY2(), cubic.getEndX(), cubic.getEndY());
	}

	/**
	 * Converts the given GEF4 FX {@link Connection} to an {@link ICurve}.
	 *
	 * @param connection
	 *            The {@link Connection} to convert.
	 * @return The corresponding {@link ICurve}.
	 */
	public static ICurve toCurve(Connection connection) {
		GeometryNode<ICurve> curveNode = connection.getCurveNode();
		return (ICurve) NodeUtils.localToParent(curveNode,
				curveNode.getGeometry());
	}

	/**
	 * Converts the given JavaFX {@link Circle} to a
	 * {@link org.eclipse.gef4.geometry.planar.Ellipse}.
	 *
	 * @param circle
	 *            The JavaFX {@link Circle} to convert.
	 * @return The corresponding
	 *         {@link org.eclipse.gef4.geometry.planar.Ellipse}.
	 */
	public static org.eclipse.gef4.geometry.planar.Ellipse toEllipse(
			Circle circle) {
		return new org.eclipse.gef4.geometry.planar.Ellipse(
				circle.getCenterX() - circle.getRadius(),
				circle.getCenterY() - circle.getRadius(),
				circle.getRadius() + circle.getRadius(),
				circle.getRadius() + circle.getRadius());
	}

	/**
	 * Converts the given JavaFX {@link Ellipse} to a
	 * {@link org.eclipse.gef4.geometry.planar.Ellipse}.
	 *
	 * @param ellipse
	 *            The JavaFX {@link Ellipse} to convert.
	 * @return The corresponding
	 *         {@link org.eclipse.gef4.geometry.planar.Ellipse}.
	 */
	public static org.eclipse.gef4.geometry.planar.Ellipse toEllipse(
			Ellipse ellipse) {
		return new org.eclipse.gef4.geometry.planar.Ellipse(
				ellipse.getCenterX() - ellipse.getRadiusX(),
				ellipse.getCenterY() - ellipse.getRadiusY(),
				ellipse.getRadiusX() + ellipse.getRadiusX(),
				ellipse.getRadiusY() + ellipse.getRadiusY());
	}

	/**
	 * Returns an {@link IGeometry} that describes the geometric outline of the
	 * passed-in {@link Node}. The conversion is supported for
	 * {@link Connection} and {@link GeometryNode}, as well as the following
	 * JavaFX {@link Node}s:
	 * <ul>
	 * <li>{@link Arc}
	 * <li>{@link Circle}
	 * <li>{@link CubicCurve}
	 * <li>{@link Ellipse}
	 * <li>{@link Line}
	 * <li>{@link Path}
	 * <li>{@link Polygon}
	 * <li>{@link Polyline}
	 * <li>{@link QuadCurve}
	 * <li>{@link Rectangle}
	 * </ul>
	 *
	 * @param visual
	 *            The {@link Node} for which an {@link IGeometry} is determined.
	 * @return The {@link IGeometry} that best describes the geometric outline
	 *         of the given {@link Node}.
	 * @throws IllegalStateException
	 *             if the given {@link Node} is not supported.
	 */
	public static IGeometry toGeometry(Node visual) {
		if (visual instanceof Connection) {
			return toCurve((Connection) visual);
		} else if (visual instanceof GeometryNode) {
			return ((GeometryNode<?>) visual).getGeometry();
		} else if (visual instanceof Arc) {
			return toArc((Arc) visual);
		} else if (visual instanceof Circle) {
			return toEllipse((Circle) visual);
		} else if (visual instanceof CubicCurve) {
			return toCubicCurve((CubicCurve) visual);
		} else if (visual instanceof Ellipse) {
			return toEllipse((Ellipse) visual);
		} else if (visual instanceof Line) {
			return toLine((Line) visual);
		} else if (visual instanceof Path) {
			return toPath((Path) visual);
		} else if (visual instanceof Polygon) {
			return toPolygon((Polygon) visual);
		} else if (visual instanceof Polyline) {
			return toPolyline((Polyline) visual);
		} else if (visual instanceof QuadCurve) {
			QuadCurve quad = (QuadCurve) visual;
			return toQuadraticCurve(quad);
		} else if (visual instanceof Rectangle) {
			return toRoundedRectangle((Rectangle) visual);
		} else {
			// Text and SVGPath shapes are currently not supported
			throw new IllegalStateException(
					"Cannot compute geometric outline for visual of type <"
							+ visual.getClass() + ">.");
		}
	}

	/**
	 * Converts the given JavaFX {@link Line} to a
	 * {@link org.eclipse.gef4.geometry.planar.Line}.
	 *
	 * @param line
	 *            The JavaFX {@link Line} to convert.
	 * @return The corresponding {@link org.eclipse.gef4.geometry.planar.Line}.
	 */
	public static org.eclipse.gef4.geometry.planar.Line toLine(Line line) {
		return new org.eclipse.gef4.geometry.planar.Line(line.getStartX(),
				line.getStartY(), line.getEndX(), line.getEndY());
	}

	/**
	 * Converts the given JavaFX {@link Path} to a
	 * {@link org.eclipse.gef4.geometry.planar.Path}.
	 *
	 * @param path
	 *            The JavaFX {@link Path} to convert.
	 * @return The corresponding {@link org.eclipse.gef4.geometry.planar.Path}.
	 */
	public static final org.eclipse.gef4.geometry.planar.Path toPath(
			Path path) {
		ObservableList<PathElement> elements = path.getElements();
		org.eclipse.gef4.geometry.planar.Path.Segment[] segments = new org.eclipse.gef4.geometry.planar.Path.Segment[elements
				.size()];

		for (int i = 0; i < segments.length; i++) {
			PathElement element = elements.get(i);
			if (element instanceof MoveTo) {
				MoveTo moveTo = (MoveTo) element;
				segments[i] = new Segment(Segment.MOVE_TO,
						new Point(moveTo.getX(), moveTo.getY()));
			} else if (element instanceof LineTo) {
				LineTo lineTo = (LineTo) element;
				segments[i] = new Segment(Segment.LINE_TO,
						new Point(lineTo.getX(), lineTo.getY()));
			} else if (element instanceof QuadCurveTo) {
				QuadCurveTo quadTo = (QuadCurveTo) element;
				segments[i] = new Segment(Segment.QUAD_TO,
						new Point(quadTo.getControlX(), quadTo.getControlY()),
						new Point(quadTo.getX(), quadTo.getY()));
			} else if (element instanceof CubicCurveTo) {
				CubicCurveTo cubicTo = (CubicCurveTo) element;
				segments[i] = new Segment(Segment.CUBIC_TO,
						new Point(cubicTo.getControlX1(),
								cubicTo.getControlY1()),
						new Point(cubicTo.getControlX2(),
								cubicTo.getControlY2()),
						new Point(cubicTo.getX(), cubicTo.getY()));
			} else if (element instanceof ClosePath) {
				segments[i] = new Segment(Segment.CLOSE);
			}
		}

		int windingRule = path.getFillRule() == FillRule.EVEN_ODD
				? org.eclipse.gef4.geometry.planar.Path.WIND_EVEN_ODD
				: org.eclipse.gef4.geometry.planar.Path.WIND_NON_ZERO;

		return new org.eclipse.gef4.geometry.planar.Path(windingRule, segments);
	}

	/**
	 * Converts the given JavaFX {@link Polygon} to a
	 * {@link org.eclipse.gef4.geometry.planar.Polygon}.
	 *
	 * @param polygon
	 *            The JavaFX {@link Polygon} to convert.
	 * @return The corresponding
	 *         {@link org.eclipse.gef4.geometry.planar.Polygon}.
	 */
	public static IGeometry toPolygon(Polygon polygon) {
		double[] coords = new double[polygon.getPoints().size()];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = polygon.getPoints().get(i).doubleValue();
		}
		return new org.eclipse.gef4.geometry.planar.Polygon(coords);
	}

	/**
	 * Converts the given JavaFX {@link Polyline} to a
	 * {@link org.eclipse.gef4.geometry.planar.Polyline}.
	 *
	 * @param polyline
	 *            The JavaFX {@link Polyline} to convert.
	 * @return The corresponding
	 *         {@link org.eclipse.gef4.geometry.planar.Polyline}.
	 */
	public static IGeometry toPolyline(Polyline polyline) {
		double[] coords = new double[polyline.getPoints().size()];
		for (int i = 0; i < coords.length; i++) {
			coords[i] = polyline.getPoints().get(i).doubleValue();
		}
		return new org.eclipse.gef4.geometry.planar.Polyline(coords);
	}

	/**
	 * Converts the given JavaFX {@link QuadCurve} to a
	 * {@link org.eclipse.gef4.geometry.planar.QuadraticCurve}.
	 *
	 * @param quad
	 *            The JavaFX {@link QuadCurve} to convert.
	 * @return The corresponding
	 *         {@link org.eclipse.gef4.geometry.planar.QuadraticCurve}.
	 */
	public static IGeometry toQuadraticCurve(QuadCurve quad) {
		return new org.eclipse.gef4.geometry.planar.QuadraticCurve(
				quad.getStartX(), quad.getStartY(), quad.getControlX(),
				quad.getControlY(), quad.getEndX(), quad.getEndY());
	}

	/**
	 * Converts the given JavaFX {@link Rectangle} to a
	 * {@link org.eclipse.gef4.geometry.planar.RoundedRectangle}.
	 *
	 * @param rect
	 *            The JavaFX {@link Rectangle} to convert.
	 * @return The corresponding
	 *         {@link org.eclipse.gef4.geometry.planar.RoundedRectangle}.
	 */
	public static IGeometry toRoundedRectangle(Rectangle rect) {
		return new org.eclipse.gef4.geometry.planar.RoundedRectangle(
				rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(),
				rect.getArcWidth(), rect.getArcHeight());
	}

}

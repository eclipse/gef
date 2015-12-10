/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for bug #483710
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.providers;

import org.eclipse.gef4.common.adapt.IAdaptable;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;

/**
 * The {@link GeometricOutlineProvider} is a {@link Provider Provider
 * <IGeometry>} that returns an {@link IGeometry} that corresponds to the
 * geometric outline of its host visual, i.e. it does not include the stroke of
 * the visual or other visual properties (e.g. clip or effect). The
 * {@link IGeometry} is specified within the local coordinate system of the host
 * visual.
 *
 * @author anyssen
 *
 */
public class GeometricOutlineProvider
		implements IAdaptable.Bound<IVisualPart<Node, ? extends Node>>,
		Provider<IGeometry> {

	private IVisualPart<Node, ? extends Node> host;

	@Override
	public IGeometry get() {
		// return host visual's geometry in local coordinates
		return getGeometry(host.getVisual());
	}

	@Override
	public IVisualPart<Node, ? extends Node> getAdaptable() {
		return host;
	}

	/**
	 * Returns an {@link IGeometry} representing the outline of the passed in
	 * visual {@link Node}, within the local coordinate space of that
	 * {@link Node}.
	 *
	 * @param visual
	 *            The {@link Node} for which to retrieve the outline geometry.
	 * @return An {@link IGeometry} representing the outline geometry.
	 */
	protected IGeometry getGeometry(Node visual) {
		if (visual instanceof Connection) {
			Node curveNode = ((Connection) visual).getCurveNode();
			if (curveNode instanceof GeometryNode) {
				return NodeUtils.localToParent(curveNode,
						((GeometryNode<?>) curveNode).getGeometry());
			} else {
				throw new IllegalStateException(
						"The curve-node of a Connection is expected to be a GeometryNode.");
			}
		} else if (visual instanceof GeometryNode) {
			return ((GeometryNode<?>) visual).getGeometry();
		} else if (visual instanceof Arc) {
			Arc arc = (Arc) visual;
			return new org.eclipse.gef4.geometry.planar.Arc(
					arc.getCenterX() - arc.getRadiusX(),
					arc.getCenterY() - arc.getRadiusY(),
					arc.getRadiusX() + arc.getRadiusX(),
					arc.getRadiusY() + arc.getRadiusY(),
					Angle.fromDeg(arc.getStartAngle()),
					Angle.fromDeg(arc.getLength()));
		} else if (visual instanceof Circle) {
			Circle circle = (Circle) visual;
			return new org.eclipse.gef4.geometry.planar.Ellipse(
					circle.getCenterX() - circle.getRadius(),
					circle.getCenterY() - circle.getRadius(),
					circle.getRadius() + circle.getRadius(),
					circle.getRadius() + circle.getRadius());
		} else if (visual instanceof CubicCurve) {
			CubicCurve cubic = (CubicCurve) visual;
			return new org.eclipse.gef4.geometry.planar.CubicCurve(
					cubic.getStartX(), cubic.getStartY(), cubic.getControlX1(),
					cubic.getControlY1(), cubic.getControlX2(),
					cubic.getControlY2(), cubic.getEndX(), cubic.getEndY());
		} else if (visual instanceof Ellipse) {
			Ellipse ellipse = (Ellipse) visual;
			return new org.eclipse.gef4.geometry.planar.Ellipse(
					ellipse.getCenterX() - ellipse.getRadiusX(),
					ellipse.getCenterY() - ellipse.getRadiusY(),
					ellipse.getRadiusX() + ellipse.getRadiusX(),
					ellipse.getRadiusY() + ellipse.getRadiusY());
		} else if (visual instanceof Line) {
			Line line = (Line) visual;
			return new org.eclipse.gef4.geometry.planar.Line(line.getStartX(),
					line.getStartY(), line.getEndX(), line.getEndY());
		} else if (visual instanceof Path) {
			Path path = (Path) visual;
			return JavaFX2Geometry.toPath(path);
		} else if (visual instanceof Polygon) {
			Polygon polygon = (Polygon) visual;
			double[] coords = new double[polygon.getPoints().size()];
			for (int i = 0; i < coords.length; i++) {
				coords[i] = polygon.getPoints().get(i).doubleValue();
			}
			return new org.eclipse.gef4.geometry.planar.Polygon(coords);
		} else if (visual instanceof Polyline) {
			Polyline polyline = (Polyline) visual;
			double[] coords = new double[polyline.getPoints().size()];
			for (int i = 0; i < coords.length; i++) {
				coords[i] = polyline.getPoints().get(i).doubleValue();
			}
			return new org.eclipse.gef4.geometry.planar.Polyline(coords);
		} else if (visual instanceof QuadCurve) {
			QuadCurve quad = (QuadCurve) visual;
			return new org.eclipse.gef4.geometry.planar.QuadraticCurve(
					quad.getStartX(), quad.getStartY(), quad.getControlX(),
					quad.getControlY(), quad.getEndX(), quad.getEndY());
		} else if (visual instanceof Rectangle) {
			Rectangle rect = (Rectangle) visual;
			if (rect.getArcWidth() > 0 && rect.getArcHeight() > 0) {
				return new org.eclipse.gef4.geometry.planar.RoundedRectangle(
						rect.getX(), rect.getY(), rect.getWidth(),
						rect.getHeight(), rect.getArcWidth(),
						rect.getArcHeight());
			} else {
				return new org.eclipse.gef4.geometry.planar.Rectangle(
						rect.getX(), rect.getY(), rect.getWidth(),
						rect.getHeight());
			}
		} else {
			throw new IllegalStateException(
					"Cannot compute geometric outline for visual of type <"
							+ visual.getClass() + ">.");
		}
	}

	@Override
	public void setAdaptable(IVisualPart<Node, ? extends Node> adaptable) {
		this.host = adaptable;
	}

}

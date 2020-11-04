/*******************************************************************************
 * Copyright (c) 2014, 2020 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.common.beans.property.ReadOnlyListWrapperEx;
import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.PolyBezier;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Paint;

// TODO: parameterize with concrete ICurve and encapsulate construction of geometry; limit the number of waypoints if needed
public class GeometricCurve extends AbstractGeometricElement<ICurve> {

	public enum Decoration {
		NONE, ARROW, CIRCLE
	}

	public enum InterpolationStyle {
		POLYGON, POLYBEZIER
	}

	public enum RoutingStyle {
		STRAIGHT, ORTHOGONAL
	}

	public static final String SOURCE_DECORATION_PROPERTY = "sourceDecoration";
	public static final String TARGET_DECORATION_PROPERTY = "targetDecoration";
	public static final String ROUTING_STYLE_PROPERTY = "routingStyle";
	public static final String INTERPOLATION_STYLE_PROPERTY = "interpolationStyle";
	public static final String WAY_POINTS_PROPERTY = "wayPoints";
	public static final String DASHES_PROPERTY = "dashes";

	public static ICurve constructCurveFromWayPoints(Point... waypoints) {
		if (waypoints == null || waypoints.length < 2) {
			throw new IllegalArgumentException("Need at least two waypoints.");
		}
		return PolyBezier.interpolateCubic(waypoints);
	}

	private final ReadOnlyListWrapperEx<Point> wayPointsProperty = new ReadOnlyListWrapperEx<>(this,
			WAY_POINTS_PROPERTY, CollectionUtils.<Point> observableArrayList());
	private final ObjectProperty<Decoration> sourceDecorationProperty = new SimpleObjectProperty<>(this,
			SOURCE_DECORATION_PROPERTY, Decoration.NONE);
	private final ObjectProperty<Decoration> targetDecorationProperty = new SimpleObjectProperty<>(this,
			TARGET_DECORATION_PROPERTY, Decoration.NONE);
	private final ObjectProperty<RoutingStyle> routingStyleProperty = new SimpleObjectProperty<>(this,
			ROUTING_STYLE_PROPERTY, RoutingStyle.STRAIGHT);
	private final ObjectProperty<InterpolationStyle> interpolationStyleProperty = new SimpleObjectProperty<>(this,
			INTERPOLATION_STYLE_PROPERTY, InterpolationStyle.POLYBEZIER);
	private final ReadOnlyListWrapperEx<Double> dashesProperty = new ReadOnlyListWrapperEx<>(this, DASHES_PROPERTY,
			CollectionUtils.<Double> observableArrayList());
	private final Set<AbstractGeometricElement<? extends IGeometry>> sourceAnchorages = new HashSet<>();
	private final Set<AbstractGeometricElement<? extends IGeometry>> targetAnchorages = new HashSet<>();

	public GeometricCurve(Point[] waypoints, Paint stroke, double strokeWidth, Double[] dashes, Effect effect,
			RoutingStyle routingStyle, InterpolationStyle interpolationStyle) {
		super(constructCurveFromWayPoints(waypoints), stroke, strokeWidth, effect);
		if (waypoints.length < 2) {
			throw new IllegalArgumentException("At least start and end point need to be specified,");
		}
		wayPointsProperty.addAll(Arrays.asList(waypoints));
		dashesProperty.addAll(dashes);
		setRoutingStyle(routingStyle);
		setInterpolationStyle(interpolationStyle);
	}

	public void addSourceAnchorage(AbstractGeometricElement<? extends IGeometry> anchored) {
		sourceAnchorages.add(anchored);
	}

	public void addTargetAnchorage(AbstractGeometricElement<? extends IGeometry> anchored) {
		targetAnchorages.add(anchored);
	}

	public void addWayPoint(int i, Point p) {
		List<Point> points = getWayPointsCopy();
		points.add(i, p);
		setWayPoints(points.toArray(new Point[] {}));
	}

	public ReadOnlyListProperty<Double> dashesProperty() {
		return dashesProperty.getReadOnlyProperty();
	}

	public GeometricCurve getCopy() {
		GeometricCurve copy = new GeometricCurve(getWayPointsCopy().toArray(new Point[] {}), getStroke(),
				getStrokeWidth(), getDashes(), getEffect(), getRoutingStyle(), getInterpolationStyle());
		copy.setTransform(getTransform());
		copy.setRoutingStyle(getRoutingStyle());
		copy.setSourceDecoration(getSourceDecoration());
		copy.setTargetDecoration(getTargetDecoration());
		return copy;
	}

	public Double[] getDashes() {
		return dashesProperty.get().toArray(new Double[] {});
	}

	public InterpolationStyle getInterpolationStyle() {
		return interpolationStyleProperty.get();
	}

	public RoutingStyle getRoutingStyle() {
		return routingStyleProperty.get();
	}

	public Set<AbstractGeometricElement<? extends IGeometry>> getSourceAnchorages() {
		return sourceAnchorages;
	}

	public Decoration getSourceDecoration() {
		return sourceDecorationProperty.get();
	}

	public Set<AbstractGeometricElement<? extends IGeometry>> getTargetAnchorages() {
		return targetAnchorages;
	}

	public Decoration getTargetDecoration() {
		return targetDecorationProperty.get();
	}

	public List<Point> getWayPoints() {
		return Collections.unmodifiableList(wayPointsProperty.get());
	}

	public List<Point> getWayPointsCopy() {
		return new ArrayList<>(getWayPoints());
	}

	/**
	 * Returns the {@link ObjectProperty} for the {@link InterpolationStyle} of
	 * this {@link GeometricCurve}.
	 *
	 * @return The {@link ObjectProperty} for the {@link InterpolationStyle} of
	 *         this {@link GeometricCurve}.
	 */
	public ObjectProperty<InterpolationStyle> interpolationStyleProperty() {
		return interpolationStyleProperty;
	}

	public void removeWayPoint(int i) {
		// TODO: check index
		List<Point> points = getWayPointsCopy();
		points.remove(i);
		setWayPoints(points.toArray(new Point[] {}));
	}

	/**
	 * Returns the {@link ObjectProperty} for the {@link RoutingStyle} of this
	 * {@link GeometricCurve}.
	 *
	 * @return The {@link ObjectProperty} for the {@link RoutingStyle} of this
	 *         {@link GeometricCurve}.
	 */
	public ObjectProperty<RoutingStyle> routingStyleProperty() {
		return routingStyleProperty;
	}

	public void setInterpolationStyle(InterpolationStyle interpolationStyle) {
		interpolationStyleProperty.set(interpolationStyle);
	}

	public void setRoutingStyle(RoutingStyle routingStyle) {
		routingStyleProperty.set(routingStyle);
	}

	public void setSourceDecoration(Decoration sourceDecoration) {
		sourceDecorationProperty.set(sourceDecoration);
	}

	public void setTargetDecoration(Decoration targetDecoration) {
		targetDecorationProperty.set(targetDecoration);
	}

	public void setWayPoint(int i, Point p) {
		List<Point> points = getWayPointsCopy();
		points.set(i, p);
		setWayPoints(points.toArray(new Point[] {}));
	}

	public void setWayPoints(Point... waypoints) {
		if (waypoints.length < 2) {
			throw new IllegalArgumentException("Need at least two waypoints.");
		}
		this.wayPointsProperty.setAll(Arrays.asList(waypoints));
		setGeometry(constructCurveFromWayPoints(waypoints));
	}

	/**
	 * Returns the {@link ObjectProperty} for the source {@link Decoration} of
	 * this {@link GeometricCurve}.
	 *
	 * @return The {@link ObjectProperty} for the source {@link Decoration} of
	 *         this {@link GeometricCurve}.
	 */
	public ObjectProperty<Decoration> sourceDecorationProperty() {
		return sourceDecorationProperty;
	}

	/**
	 * Returns the {@link ObjectProperty} for the target {@link Decoration} of
	 * this {@link GeometricCurve}.
	 *
	 * @return The {@link ObjectProperty} for the target {@link Decoration} of
	 *         this {@link GeometricCurve}.
	 */
	public ObjectProperty<Decoration> targetDecorationProperty() {
		return targetDecorationProperty;
	}

	public ReadOnlyListProperty<Point> wayPointsProperty() {
		return wayPointsProperty.getReadOnlyProperty();
	}

}

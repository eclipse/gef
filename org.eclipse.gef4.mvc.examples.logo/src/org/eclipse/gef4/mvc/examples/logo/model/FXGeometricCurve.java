/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;

import javafx.scene.effect.Effect;
import javafx.scene.paint.Paint;

// TODO: parameterize with concrete ICurve and encapsulate construction of geometry; limit the number of waypoints if needed
public class FXGeometricCurve extends AbstractFXGeometricElement<ICurve> {

	public enum Decoration {
		NONE, ARROW, CIRCLE
	}

	public static final String SOURCE_DECORATION_PROPERTY = "sourceDecoration";
	public static final String TARGET_DECORATION_PROPERTY = "targetDecoration";

	public static ICurve constructCurveFromWayPoints(Point... waypoints) {
		if (waypoints == null || waypoints.length == 0) {
			waypoints = new Point[] { new Point(), new Point() };
		} else if (waypoints.length == 1) {
			waypoints = new Point[] { new Point(), waypoints[0] };
		}
		return PolyBezier.interpolateCubic(waypoints);
	}

	private final List<Point> waypoints = new ArrayList<>();
	private Decoration sourceDecoration = Decoration.NONE;
	private Decoration targetDecoration = Decoration.NONE;
	public double[] dashes = new double[0];
	private final Set<AbstractFXGeometricElement<? extends IGeometry>> sourceAnchorages = new HashSet<AbstractFXGeometricElement<? extends IGeometry>>();
	private final Set<AbstractFXGeometricElement<? extends IGeometry>> targetAnchorages = new HashSet<AbstractFXGeometricElement<? extends IGeometry>>();

	public FXGeometricCurve(Point[] waypoints, Paint stroke, double strokeWidth,
			double[] dashes, Effect effect) {
		super(constructCurveFromWayPoints(waypoints), stroke, strokeWidth,
				effect);
		this.waypoints.addAll(Arrays.asList(waypoints));
		this.dashes = dashes;
	}

	public void addSourceAnchorage(
			AbstractFXGeometricElement<? extends IGeometry> anchored) {
		sourceAnchorages.add(anchored);
	}

	public void addTargetAnchorage(
			AbstractFXGeometricElement<? extends IGeometry> anchored) {
		targetAnchorages.add(anchored);
	}

	public void addWayPoint(int i, Point p) {
		// TODO: check index != 0 && index != end
		List<Point> points = getWayPointsCopy();
		points.add(i, p);
		setWayPoints(points.toArray(new Point[] {}));
	}

	public double[] getDashes() {
		return Arrays.copyOf(dashes, dashes.length);
	}

	public Set<AbstractFXGeometricElement<? extends IGeometry>> getSourceAnchorages() {
		return sourceAnchorages;
	}

	public Decoration getSourceDecoration() {
		return sourceDecoration;
	}

	public Set<AbstractFXGeometricElement<? extends IGeometry>> getTargetAnchorages() {
		return targetAnchorages;
	}

	public Decoration getTargetDecoration() {
		return targetDecoration;
	}

	public List<Point> getWayPoints() {
		return Collections.unmodifiableList(waypoints);
	}

	public List<Point> getWayPointsCopy() {
		return new ArrayList<Point>(waypoints);
	}

	public void removeWayPoint(int i) {
		// TODO: check index
		List<Point> points = getWayPointsCopy();
		points.remove(i);
		setWayPoints(points.toArray(new Point[] {}));
	}

	public void setSourceDecoration(Decoration sourceDecoration) {
		Decoration oldSourceDecoration = this.sourceDecoration;
		this.sourceDecoration = sourceDecoration;
		pcs.firePropertyChange(SOURCE_DECORATION_PROPERTY, oldSourceDecoration,
				sourceDecoration);
	}

	public void setTargetDecoration(Decoration targetDecoration) {
		Decoration oldTargetDecoration = this.targetDecoration;
		this.targetDecoration = targetDecoration;
		pcs.firePropertyChange(TARGET_DECORATION_PROPERTY, oldTargetDecoration,
				targetDecoration);
	}

	public void setWayPoint(int i, Point p) {
		List<Point> points = getWayPointsCopy();
		points.set(i, p);
		setWayPoints(points.toArray(new Point[] {}));
	}

	public void setWayPoints(Point... waypoints) {
		// cache waypoints and polybezier
		this.waypoints.clear();
		this.waypoints.addAll(Arrays.asList(waypoints));
		setGeometry(constructCurveFromWayPoints(waypoints));
	}

}

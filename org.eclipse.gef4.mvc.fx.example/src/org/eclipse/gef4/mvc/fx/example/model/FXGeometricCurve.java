/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.example.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;

// TODO: parameterize with concrete ICurve and encapsulate construction of geometry; limit the number of waypoints if needed
public class FXGeometricCurve extends AbstractFXGeometricElement<ICurve> {

	private List<Point> waypoints = new ArrayList<>();
	public double[] dashes = new double[0];

	public FXGeometricCurve(Point[] waypoints, Color stroke,
			double strokeWidth, double[] dashes, Effect effect) {
		super(constructCurveFromWayPoints(waypoints), stroke, strokeWidth,
				effect);
		this.waypoints.addAll(Arrays.asList(waypoints));
		this.dashes = dashes;
	}

	protected void setWayPoints(Point... waypoints) {
		// cache waypoints and polybezier
		this.waypoints.clear();
		this.waypoints.addAll(Arrays.asList(waypoints));
		setGeometry(constructCurveFromWayPoints(waypoints));
	}

	public static ICurve constructCurveFromWayPoints(Point... waypoints) {
		// return new Polyline(waypoints);
		return PolyBezier.interpolateCubic(waypoints);
	}

	public List<Point> getWayPoints() {
		return Collections.unmodifiableList(waypoints);
	}

	private List<Point> getWayPointsCopy() {
		return new ArrayList<Point>(waypoints);
	}

	public void addWayPoint(int i, Point p) {
		// TODO: check index != 0 && index != end
		List<Point> points = getWayPointsCopy();
		points.add(i, p);
		setWayPoints(points.toArray(new Point[] {}));
	}

	public void removeWayPoint(int i) {
		// TODO: check index
		List<Point> points = getWayPointsCopy();
		points.remove(i);
		setWayPoints(points.toArray(new Point[] {}));
	}

	public void setWayPoint(int i, Point p) {
		List<Point> points = getWayPointsCopy();
		points.set(i, p);
		setWayPoints(points.toArray(new Point[] {}));
	}
}

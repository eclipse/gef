package org.eclipse.gef4.mvc.fx.example.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polyline;

public class FXGeometricCurve extends AbstractFXGeometricElement<ICurve> {

	private List<Point> waypoints = new ArrayList<>();

	public FXGeometricCurve(Point... waypoints) {
		super(constructCurveFromWayPoints(waypoints));
		this.waypoints.addAll(Arrays.asList(waypoints));
	}

	protected void setWayPoints(Point... waypoints) {
		// cache waypoints and polybezier
		this.waypoints.clear();
		this.waypoints.addAll(Arrays.asList(waypoints));
		setGeometry(constructCurveFromWayPoints(waypoints));
	}

	public static ICurve constructCurveFromWayPoints(Point... waypoints) {
		return new Polyline(waypoints);
//		return PolyBezier.interpolateCubic(waypoints);
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

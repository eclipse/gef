package org.eclipse.gef4.mvc.fx.example.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polyline;

public class FXGeometricCurve extends AbstractFXGeometricElement<Polyline> {

	public FXGeometricCurve(Polyline curve) {
		super(curve);
	}
	
	public List<Point> getWayPoints() {
		ArrayList<Point> points = new ArrayList<Point>();
		points.addAll(Arrays.asList(getGeometry().getPoints()));
		return points;
	}
	
	public void addWayPoint(int i, Point p) {
		// TODO: check index != 0 && index != end
		List<Point> points = getWayPoints();
		points.add(i, p);
		setGeometry(new Polyline(points.toArray(new Point[0])));
	}
	
	public void removeWayPoint(int i) {
		// TODO: check index
		List<Point> points = getWayPoints();
		points.remove(i);
		setGeometry(new Polyline(points.toArray(new Point[0])));
	}
	
	public void setWayPoint(int i, Point p) {
		// TODO: check index != 0 && index != end
		List<Point> points = getWayPoints();
		points.set(i, p);
		setGeometry(new Polyline(points.toArray(new Point[0])));
	}

}

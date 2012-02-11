/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.eclipse.gef4.geometry.Angle;
import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.transform.AffineTransform;

/**
 * Represents the geometric shape of an arc, which is defined by its enclosing
 * framing rectangle, a start angle (relative to the x-axis), and an angular
 * extend (in CCW direction).
 * 
 * @author anyssen
 * 
 */
public class Arc implements IGeometry {

	private static final long serialVersionUID = 1L;

	// TODO: move to utilities
	private static final Path toPath(CubicCurve... curves) {
		Path p = new Path();
		for (int i = 0; i < curves.length; i++) {
			if (i == 0) {
				p.moveTo(curves[i].getX1(), curves[i].getY1());
			}
			p.curveTo(curves[i].getCtrl1X(), curves[i].getCtrl1Y(),
					curves[i].getCtrl2X(), curves[i].getCtrl2Y(),
					curves[i].getX2(), curves[i].getY2());
		}
		return p;
	}

	private double x;
	private double y;
	private double width;
	private double height;
	private Angle startAngle;

	private Angle angularExtent;

	/**
	 * Constructs a new {@link Arc} so that it is fully contained within the
	 * framing rectangle defined by (x, y, width, height), spanning the given
	 * extend (in CCW direction) from the given start angle (relative to the
	 * x-axis).
	 * 
	 * @param x
	 *            the x-coordinate of the framing rectangle
	 * @param y
	 *            the y-coordinate of the framing rectangle
	 * @param width
	 * @param height
	 * @param startAngle
	 * @param angularExtent
	 */
	public Arc(double x, double y, double width, double height,
			Angle startAngle, Angle angularExtent) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.startAngle = startAngle;
		this.angularExtent = angularExtent;
	}

	private CubicCurve computeApproximation(double start, double end) {
		// compute major and minor axis length
		double a = width / 2;
		double b = height / 2;

		// // calculate start and end points of the arc from start to end
		Point startPoint = new Point(x + a + a * Math.cos(start), y + b - b
				* Math.sin(start));
		Point endPoint = new Point(x + a + a * Math.cos(end), y + b - b
				* Math.sin(end));

		// approximation by cubic Bezier according to approximation provided in:
		// http://www.spaceroots.org/documents/ellipse/elliptical-arc.pdf
		double t = Math.tan((end - start) / 2);
		double alpha = Math.sin(end - start)
				* (Math.sqrt(4.0d + 3.0d * t * t) - 1) / 3;
		Point controlPoint1 = new Point(startPoint.x + alpha * -a
				* Math.sin(start), startPoint.y - alpha * b * Math.cos(start));
		Point controlPoint2 = new Point(
				endPoint.x - alpha * -a * Math.sin(end), endPoint.y + alpha * b
						* Math.cos(end));

		Point[] points = new Point[] { startPoint, controlPoint1,
				controlPoint2, endPoint };
		return new CubicCurve(points);
	}

	/**
	 * @see IGeometry#contains(Point)
	 */
	public boolean contains(Point p) {
		return false;
	}

	/**
	 * @see IGeometry#contains(Rectangle)
	 */
	public boolean contains(Rectangle r) {
		return false;
	}

	public Angle getAngularExtent() {
		return angularExtent;
	}

	/**
	 * @see IGeometry#getBounds()
	 */
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}

	public double getHeight() {
		return height;
	}

	/**
	 * Returns the points of intersection between this {@link Arc} and the given
	 * other {@link Arc}.
	 * 
	 * @param other
	 *            The {@link Arc} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(Arc other) {
		if (equals(other)) {
			return new Point[] {};
		}

		HashSet<Point> intersections = new HashSet<Point>();

		for (CubicCurve seg : getSegments()) {
			intersections.addAll(Arrays.asList(getIntersections(seg)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the points of intersection between this {@link Arc} and the given
	 * {@link CubicCurve}.
	 * 
	 * @param c
	 *            The {@link CubicCurve} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(CubicCurve c) {
		return c.getIntersections(this);
	}

	/**
	 * Returns the points of intersection between this {@link Arc} and the given
	 * {@link Ellipse}.
	 * 
	 * @param e
	 *            The {@link Ellipse} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(Ellipse e) {
		return e.getIntersections(this);
	}

	/**
	 * Returns the points of intersection between this {@link Arc} and the given
	 * {@link Line}.
	 * 
	 * @param l
	 *            The {@link Line} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(Line l) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (CubicCurve seg : getSegments()) {
			intersections.addAll(Arrays.asList(seg.getIntersections(l)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the points of intersection between this {@link Arc} and the given
	 * {@link Polygon}.
	 * 
	 * @param p
	 *            The {@link Polygon} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(Polygon p) {
		return p.getIntersections(this);
	}

	/**
	 * Returns the points of intersection between this {@link Arc} and the given
	 * {@link Polyline}.
	 * 
	 * @param p
	 *            The {@link Polyline} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(Polyline p) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (CubicCurve seg : getSegments()) {
			intersections.addAll(Arrays.asList(seg.getIntersections(p)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the points of intersection between this {@link Arc} and the given
	 * {@link QuadraticCurve}.
	 * 
	 * @param c
	 *            The {@link QuadraticCurve} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(QuadraticCurve c) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (CubicCurve seg : getSegments()) {
			intersections.addAll(Arrays.asList(seg.getIntersections(c)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the points of intersection between this {@link Arc} and the given
	 * {@link Rectangle}.
	 * 
	 * @param r
	 *            The {@link Rectangle} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(Rectangle r) {
		HashSet<Point> intersections = new HashSet<Point>();

		for (CubicCurve seg : getSegments()) {
			intersections.addAll(Arrays.asList(seg.getIntersections(r)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns the points of intersection between this {@link Arc} and the given
	 * {@link RoundedRectangle}.
	 * 
	 * @param r
	 *            The {@link RoundedRectangle} to test for intersections
	 * @return the points of intersection.
	 */
	public Point[] getIntersections(RoundedRectangle r) {
		HashSet<Point> intersections = new HashSet<Point>();

		// line segments
		intersections.addAll(Arrays.asList(getIntersections(r.getTop())));
		intersections.addAll(Arrays.asList(getIntersections(r.getLeft())));
		intersections.addAll(Arrays.asList(getIntersections(r.getBottom())));
		intersections.addAll(Arrays.asList(getIntersections(r.getRight())));

		// arc segments
		intersections.addAll(Arrays.asList(getIntersections(r.getTopRight())));
		intersections.addAll(Arrays.asList(getIntersections(r.getTopLeft())));
		intersections
				.addAll(Arrays.asList(getIntersections(r.getBottomLeft())));
		intersections
				.addAll(Arrays.asList(getIntersections(r.getBottomRight())));

		return intersections.toArray(new Point[] {});
	}

	public CubicCurve[] getSegments() {
		double start = getStartAngle().rad();
		double end = getStartAngle().rad() + getAngularExtent().rad();

		// approximation is for arcs with angle < 90 degrees, so we may have to
		// split the arc into up to 4 cubic curves
		List<CubicCurve> segments = new ArrayList<CubicCurve>();
		if (angularExtent.deg() <= 90.0) {
			segments.add(computeApproximation(start, end));
		} else {
			// two or more segments, the first will be an ellipse segment
			// approximation
			segments.add(computeApproximation(start, start + Math.PI / 2));
			if (angularExtent.deg() <= 180.0) {
				// two segments, calculate the second (which is below 90
				// degrees)
				segments.add(computeApproximation(start + Math.PI / 2, end));
			} else {
				// three or more segments, so calculate the second one
				segments.add(computeApproximation(start + Math.PI / 2, start
						+ Math.PI));
				if (angularExtent.deg() <= 270.0) {
					// three segments, calculate the third (which is below 90
					// degrees)
					segments.add(computeApproximation(start + Math.PI, end));
				} else {
					// four segments (fourth below 90 degrees), so calculate the
					// third and fourth
					segments.add(computeApproximation(start + Math.PI, start
							+ 3 * Math.PI / 2));
					segments.add(computeApproximation(start + 3 * Math.PI / 2,
							end));
				}
			}
		}
		return segments.toArray(new CubicCurve[] {});
	}

	public Angle getStartAngle() {
		return startAngle;
	}

	/**
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	public IGeometry getTransformed(AffineTransform t) {
		return toPath().getTransformed(t);
	}

	public double getWidth() {
		return width;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	/**
	 * @see IGeometry#intersects(Rectangle)
	 */
	public boolean intersects(Rectangle r) {
		throw new UnsupportedOperationException();
	}

	public void setAngularExtent(Angle angularExtent) {
		this.angularExtent = angularExtent;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setStartAngle(Angle startAngle) {
		this.startAngle = startAngle;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @see IGeometry#toPath()
	 */
	public Path toPath() {
		CubicCurve[] segments = getSegments();
		return toPath(segments);
	}

	/**
	 * @see org.eclipse.gef4.geometry.planar.IGeometry#getCopy()
	 */
	public Arc getCopy() {
		return new Arc(x, y, width, height, startAngle, angularExtent);
	}
}

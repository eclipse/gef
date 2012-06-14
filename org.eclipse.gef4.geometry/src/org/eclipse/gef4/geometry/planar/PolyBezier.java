/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.gef4.geometry.Angle;
import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.utils.CurveUtils;
import org.eclipse.gef4.geometry.utils.PointListUtils;

/**
 * A {@link PolyBezier} is an {@link IPolyCurve} which consists of one or more
 * connected {@link BezierCurve}s.
 */
public class PolyBezier extends AbstractGeometry implements IPolyCurve,
		ITranslatable<PolyBezier>, IScalable<PolyBezier>,
		IRotatable<PolyBezier> {

	private static final long serialVersionUID = 1L;

	private static BezierCurve[] copy(BezierCurve... beziers) {
		BezierCurve[] copy = new BezierCurve[beziers.length];

		for (int i = 0; i < beziers.length; i++) {
			copy[i] = beziers[i].getCopy();
		}

		return copy;
	}

	private BezierCurve[] beziers;

	/**
	 * Constructs a new {@link PolyBezier} of the given {@link BezierCurve}s.
	 * The {@link BezierCurve}s are expected to be connected with each other.
	 * 
	 * @param beziers
	 *            the {@link BezierCurve}s which will constitute this
	 *            {@link PolyBezier}
	 */
	public PolyBezier(BezierCurve... beziers) {
		this.beziers = copy(beziers);
	}

	public boolean contains(Point p) {
		for (BezierCurve c : beziers) {
			if (c.contains(p)) {
				return true;
			}
		}
		return false;
	}

	public Rectangle getBounds() {
		Rectangle bounds = new Rectangle();

		for (BezierCurve c : beziers) {
			bounds.union(c.getBounds());
		}

		return bounds;
	}

	public PolyBezier getCopy() {
		return new PolyBezier(beziers);
	}

	public BezierCurve[] getCurves() {
		return copy(beziers);
	}

	public Point[] getIntersections(ICurve g) {
		return CurveUtils.getIntersections(g, this);
	}

	public Point getP1() {
		return beziers[0].getP1();
	}

	public Point getP2() {
		return beziers[beziers.length - 1].getP2();
	}

	public PolyBezier getRotatedCCW(Angle angle) {
		return getCopy().rotateCCW(angle);
	}

	public PolyBezier getRotatedCCW(Angle angle, double cx, double cy) {
		return getCopy().getRotatedCCW(angle, cx, cy);
	}

	public PolyBezier getRotatedCCW(Angle angle, Point center) {
		return getCopy().getRotatedCCW(angle, center);
	}

	public PolyBezier getRotatedCW(Angle angle) {
		return getCopy().getRotatedCW(angle);
	}

	public PolyBezier getRotatedCW(Angle angle, double cx, double cy) {
		return getCopy().getRotatedCW(angle, cx, cy);
	}

	public PolyBezier getRotatedCW(Angle angle, Point center) {
		return getCopy().getRotatedCW(angle, center);
	}

	public PolyBezier getScaled(double factor) {
		return getCopy().scale(factor);
	}

	public PolyBezier getScaled(double fx, double fy) {
		return getCopy().scale(fx, fy);
	}

	public PolyBezier getScaled(double factor, double cx, double cy) {
		return getCopy().scale(factor, cx, cy);
	}

	public PolyBezier getScaled(double fx, double fy, double cx, double cy) {
		return getCopy().scale(fx, fy, cx, cy);
	}

	public PolyBezier getScaled(double fx, double fy, Point center) {
		return getCopy().scale(fx, fy, center);
	}

	public PolyBezier getScaled(double factor, Point center) {
		return getCopy().scale(factor, center);
	}

	public PolyBezier getTranslated(double dx, double dy) {
		return getCopy().translate(dx, dy);
	}

	public PolyBezier getTranslated(Point d) {
		return getCopy().translate(d.x, d.y);
	}

	public double getX1() {
		return getP1().x;
	}

	public double getX2() {
		return getP2().x;
	}

	public double getY1() {
		return getP1().y;
	}

	public double getY2() {
		return getP2().y;
	}

	public boolean intersects(ICurve c) {
		return CurveUtils.intersects(c, this);
	}

	public boolean overlaps(ICurve c) {
		return CurveUtils.overlaps(c, this);
	}

	public PolyBezier rotateCCW(Angle angle) {
		ArrayList<Point> points = new ArrayList<Point>();
		for (BezierCurve c : beziers) {
			points.addAll(Arrays.asList(c.getPoints()));
		}
		Point centroid = PointListUtils.computeCentroid(points
				.toArray(new Point[] {}));
		return rotateCCW(angle, centroid.x, centroid.y);
	}

	public PolyBezier rotateCCW(Angle angle, double cx, double cy) {
		for (BezierCurve c : beziers) {
			c.rotateCCW(angle, cx, cy);
		}
		return this;
	}

	public PolyBezier rotateCCW(Angle angle, Point center) {
		return rotateCCW(angle, center.x, center.y);
	}

	public PolyBezier rotateCW(Angle angle) {
		ArrayList<Point> points = new ArrayList<Point>();
		for (BezierCurve c : beziers) {
			points.addAll(Arrays.asList(c.getPoints()));
		}
		Point centroid = PointListUtils.computeCentroid(points
				.toArray(new Point[] {}));
		return rotateCW(angle, centroid.x, centroid.y);
	}

	public PolyBezier rotateCW(Angle angle, double cx, double cy) {
		for (BezierCurve c : beziers) {
			c.rotateCW(angle, cx, cy);
		}
		return this;
	}

	public PolyBezier rotateCW(Angle angle, Point center) {
		return rotateCW(angle, center.x, center.y);
	}

	public PolyBezier scale(double factor) {
		return scale(factor, factor);
	}

	public PolyBezier scale(double fx, double fy) {
		ArrayList<Point> points = new ArrayList<Point>();
		for (BezierCurve c : beziers) {
			points.addAll(Arrays.asList(c.getPoints()));
		}
		Point centroid = PointListUtils.computeCentroid(points
				.toArray(new Point[] {}));
		return scale(fx, fy, centroid.x, centroid.y);
	}

	public PolyBezier scale(double factor, double cx, double cy) {
		return scale(factor, factor, cx, cy);
	}

	public PolyBezier scale(double fx, double fy, double cx, double cy) {
		for (BezierCurve c : beziers) {
			c.scale(fx, fy, cx, cy);
		}
		return this;
	}

	public PolyBezier scale(double fx, double fy, Point center) {
		return scale(fx, fx, center.x, center.y);
	}

	public PolyBezier scale(double factor, Point center) {
		return scale(factor, factor, center.x, center.y);
	}

	public BezierCurve[] toBezier() {
		return copy(beziers);
	}

	public Path toPath() {
		return CurveUtils.toPath(beziers);
	}

	public PolyBezier translate(double dx, double dy) {
		for (BezierCurve c : beziers) {
			c.translate(dx, dy);
		}
		return this;
	}

	public PolyBezier translate(Point d) {
		return translate(d.x, d.y);
	}

}

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
package org.eclipse.gef4.geometry.utils;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.euclidean.Straight;
import org.eclipse.gef4.geometry.planar.CubicCurve;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.QuadraticCurve;
import org.eclipse.gef4.geometry.planar.Rectangle;

/**
 * The {@link CurveUtils} class provides functionality that can be used for
 * linear curves ({@link Line}, {@link Straight}), as well as quadratic and
 * cubic Bezier curves.
 * 
 * @author wienand
 * 
 */
public class CurveUtils {

	/**
	 * The Vector3D class implements a three dimensional vector (components x,
	 * y, z) with its standard operations: addition and multiplication (scalar,
	 * dot-product, cross-product).
	 * 
	 * It is used to represent planar lines and planar points which are
	 * represented by three dimensional planes and three dimensional lines
	 * through the origin, respectively.
	 * 
	 * @author wienand
	 */
	private static final class Vector3D {
		public double x, y, z;

		/**
		 * Constructs a new {@link Vector3D} from the given {@link Point},
		 * setting z to 1.
		 * 
		 * @param p
		 */
		public Vector3D(Point p) {
			this(p.x, p.y, 1);
		}

		/**
		 * Constructs a new {@link Vector3D} object with the given component
		 * values.
		 * 
		 * @param px
		 * @param py
		 * @param pz
		 */
		public Vector3D(double px, double py, double pz) {
			x = px;
			y = py;
			z = pz;
		}

		/**
		 * Returns a copy of this {@link Vector3D}.
		 * 
		 * @return a copy of this {@link Vector3D}
		 */
		public Vector3D getCopy() {
			return new Vector3D(x, y, z);
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof Vector3D) {
				Vector3D o = (Vector3D) other;
				Point tmp = this.toPoint();
				if (tmp == null) {
					return o.toPoint() == null;
				}
				return tmp.equals(o.toPoint());
			}
			return false;
		}

		/**
		 * Returns a new {@link Vector3D} object with its components set to the
		 * sum of the individual x, y and z components of this {@link Vector3D}
		 * and the given other {@link Vector3D}.
		 * 
		 * @param other
		 * @return a new {@link Vector3D} object representing the sum of this
		 *         {@link Vector3D} and the given other {@link Vector3D}
		 */
		public Vector3D getAdded(Vector3D other) {
			return new Vector3D(this.x + other.x, this.y + other.y, this.z
					+ other.z);
		}

		/**
		 * Returns a new {@link Vector3D} object with its components set to the
		 * difference of the individual x, y and z components of this
		 * {@link Vector3D} and the given other {@link Vector3D}.
		 * 
		 * @param other
		 * @return a new {@link Vector3D} object representing the difference of
		 *         this {@link Vector3D} and the given other {@link Vector3D}
		 */
		public Vector3D getSubtracted(Vector3D other) {
			return new Vector3D(this.x - other.x, this.y - other.y, this.z
					- other.z);
		}

		/**
		 * Returns a new {@link Vector3D} object with its components set to the
		 * x, y and z components of this {@link Vector3D} scaled by the given
		 * factor.
		 * 
		 * @param f
		 *            The scaling factor.
		 * @return a new {@link Vector3D} object with its components set to the
		 *         x, y and z components of this {@link Vector3D} scaled by the
		 *         given factor
		 */
		public Vector3D getScaled(double f) {
			return new Vector3D(x * f, y * f, z * f);
		}

		/**
		 * Returns a new {@link Vector3D} object with its components set to the
		 * given ratio between this {@link Vector3D} and the given other
		 * {@link Vector3D}.
		 * 
		 * @param other
		 *            The other {@link Vector3D}.
		 * @param t
		 *            The ratio.
		 * @return a new {@link Vector3D} object with its components set to the
		 *         given ratio between this {@link Vector3D} and the given other
		 *         {@link Vector3D}
		 */
		public Vector3D getRatio(Vector3D other, double t) {
			return getAdded(other.getSubtracted(this).getScaled(t));
		}

		/**
		 * Returns a new {@link Vector3D} object that has the same direction as
		 * this {@link Vector3D} but the x- and y-components are normalized so
		 * that <code>x*x + y*y = 1</code>.
		 * 
		 * @return a new {@link Vector3D} object with x- and y-components
		 *         normalized to fulfill x*x + y*y = 1.
		 */
		public Vector3D getLineNormalized() {
			double f = Math.sqrt(x * x + y * y);
			if (f == 0) {
				return null;
			}
			return new Vector3D(x / f, y / f, z / f);
		}

		/**
		 * Returns a new {@link Vector3D} object that is the cross product of
		 * this and the given other {@link Vector3D}.
		 * 
		 * @param other
		 * @return a new {@link Vector3D} object that is the cross product of
		 *         this and the given other {@link Vector3D}
		 */
		public Vector3D getCrossed(Vector3D other) {
			return new Vector3D(this.y * other.z - this.z * other.y, this.z
					* other.x - this.x * other.z, this.x * other.y - this.y
					* other.x);
		}

		/**
		 * Returns the dot-product of this and the given other {@link Vector3D}.
		 * 
		 * @param other
		 * @return the dot-product of this and the given other {@link Vector3D}
		 */
		public double getDot(Vector3D other) {
			return this.x * other.x + this.y * other.y + this.z * other.z;
		}

		/**
		 * Returns a new {@link Point} object that is represented by this
		 * {@link Vector3D}.
		 * 
		 * @return a new {@link Point} object that is represented by this
		 *         {@link Vector3D}
		 */
		public Point toPoint() {
			if (this.z == 0) {
				return null;
			}
			return new Point(this.x / this.z, this.y / this.z);
		}

		public String toString() {
			return "Vector3D (" + x + ", " + y + ", " + z + ")";
		}

		@Override
		public int hashCode() {
			// cannot generate a good hash-code because of the imprecise
			// comparisons
			return 0;
		}
	}

	/**
	 * The {@link BezierCurve} provides a common representation for arbitrary
	 * Bezier curves.
	 * 
	 * It can evaluate points on the curve, check points for containment and
	 * compute intersection points for one curve with another.
	 * 
	 * It uses homogeneous coordinates (represented by {@link Vector3D} objects)
	 * to represent planar lines and points and to compute line/line
	 * intersections and point/line distances.
	 * 
	 * @author wienand
	 */
	private static final class BezierCurve {

		private static final double UNRECOGNIZABLE_PRECISION_FRACTION = PrecisionUtils
				.calculateFraction(0) / 10;

		private Vector3D[] points;

		/**
		 * Constructs a new {@link BezierCurve} object from the given control
		 * points.
		 * 
		 * @param controlPoints
		 */
		public BezierCurve(Point... controlPoints) {
			points = new Vector3D[controlPoints.length];
			for (int i = 0; i < points.length; i++) {
				points[i] = new Vector3D(controlPoints[i].x,
						controlPoints[i].y, 1);
			}
		}

		/**
		 * Constructs a new {@link BezierCurve} object from the given control
		 * points.
		 * 
		 * Note that a Point(2, 3) is represented by a Vector3D(2, 3, 1). So for
		 * a Point(x, y) the corresponding vector is Vector(x, y, 1).
		 * 
		 * @param controlPoints
		 */
		public BezierCurve(Vector3D... controlPoints) {
			points = new Vector3D[controlPoints.length];
			for (int i = 0; i < points.length; i++) {
				points[i] = controlPoints[i].getCopy();
			}
		}

		/**
		 * Constructs a new {@link BezierCurve} from the given
		 * {@link QuadraticCurve}.
		 * 
		 * @param c
		 */
		public BezierCurve(QuadraticCurve c) {
			this(c.getP1(), c.getCtrl(), c.getP2());
		}

		/**
		 * Constructs a new {@link BezierCurve} from the given
		 * {@link CubicCurve}.
		 * 
		 * @param c
		 */
		public BezierCurve(CubicCurve c) {
			this(c.getP1(), c.getCtrl1(), c.getCtrl2(), c.getP2());
		}

		/**
		 * Returns a copy of this {@link BezierCurve}'s points.
		 * 
		 * @return a copy of this {@link BezierCurve}'s points
		 */
		private Vector3D[] getPointsCopy() {
			Vector3D[] copy = new Vector3D[points.length];
			for (int i = 0; i < points.length; i++) {
				copy[i] = points[i].getCopy();
			}
			return copy;
		}

		/**
		 * Constructs the explicit BÃ©zier curve for this curve's x-components.
		 * 
		 * @return the explicit BÃ©zier curve for this curve's x-components
		 */
		public BezierCurve getExplicitX() {
			Vector3D[] explicit = new Vector3D[points.length];

			for (int i = 0; i < points.length; i++) {
				explicit[i] = new Vector3D((double) i
						/ ((double) points.length - 1d), points[i].toPoint().x,
						1);
			}

			return new BezierCurve(explicit);
		}

		/**
		 * Constructs the explicit BÃ©zier curve for this curve's y-components.
		 * 
		 * @return the explicit BÃ©zier curve for this curve's y-components
		 */
		public BezierCurve getExplicitY() {
			Vector3D[] explicit = new Vector3D[points.length];

			for (int i = 0; i < points.length; i++) {
				explicit[i] = new Vector3D((double) i
						/ ((double) points.length - 1d), points[i].toPoint().y,
						1);
			}

			return new BezierCurve(explicit);
		}

		/**
		 * Checks if all y-components of this {@link BezierCurve}'s points have
		 * the same sign.
		 * 
		 * Returns true if either all y-components are positive or all
		 * y-components are negative.
		 * 
		 * Returns false, otherwise.
		 * 
		 * @param c
		 * @return true if all y-components are either positive or negative,
		 *         otherwise false
		 */
		private static boolean sameSign(BezierCurve c) {
			double sign = c.points[0].toPoint().y;
			if (sign == 0) {
				return false;
			}
			for (int i = 1; i < c.points.length; i++) {
				if (sign < 0) {
					if (c.points[i].toPoint().y >= 0) {
						return false;
					}
				} else if (sign > 0) {
					if (c.points[i].toPoint().y <= 0) {
						return false;
					}
				}
			}
			return true;
		}

		/**
		 * Used to store parameter values in a HashSet with an imprecise
		 * equals() operation.
		 * 
		 * @author wienand
		 */
		private static final class ImpreciseDouble {
			private double value;
			private int shift;

			public ImpreciseDouble(double a) {
				value = a;
				shift = 1;
			}

			/**
			 * Returns the double value represented by this
			 * {@link ImpreciseDouble}.
			 * 
			 * @return the double value represented by this
			 *         {@link ImpreciseDouble}
			 */
			public double getValue() {
				return value;
			}

			@Override
			public boolean equals(Object obj) {
				if (obj instanceof ImpreciseDouble) {
					ImpreciseDouble o = (ImpreciseDouble) obj;
					return PrecisionUtils.equal(value, o.value, shift);
				}
				return false;
			}
		}

		/**
		 * Calculates the roots of the given explicit {@link BezierCurve} on the
		 * interval [a;b].
		 * 
		 * You can get an explicit {@link BezierCurve} from an arbitrary
		 * {@link BezierCurve} for either its x- or y-components using the
		 * {@link BezierCurve#getExplicitX()} or
		 * {@link BezierCurve#getExplicitY()} methods, respectively.
		 * 
		 * @param c
		 * @param a
		 *            start of the parameter interval
		 * @param b
		 *            end of the parameter interval
		 * @return the roots of the given explicit {@link BezierCurve} on the
		 *         interval [a;b]
		 */
		private static HashSet<ImpreciseDouble> getRoots(BezierCurve c,
				double a, double b) {
			BezierCurve clipped = c.getClipped(a, b);

			if (sameSign(clipped)) {
				return new HashSet<ImpreciseDouble>();
			}

			if (PrecisionUtils.equal(a, b, +2)) {
				HashSet<ImpreciseDouble> root = new HashSet<ImpreciseDouble>();
				root.add(new ImpreciseDouble((a + b) / 2));
				return root;
			}

			HashSet<ImpreciseDouble> left = getRoots(c, a, (a + b) / 2);
			HashSet<ImpreciseDouble> right = getRoots(c, (a + b) / 2, b);

			left.addAll(right);

			return left;
		}

		/**
		 * Calculates the roots of the given {@link BezierCurve} which is
		 * expected to be explicit.
		 * 
		 * You can get an explicit {@link BezierCurve} from an arbitrary
		 * {@link BezierCurve} for either its x- or y-components using the
		 * {@link BezierCurve#getExplicitX()} or
		 * {@link BezierCurve#getExplicitY()} methods, respectively.
		 * 
		 * @param c
		 * @return the roots of the given explicit {@link BezierCurve}
		 */
		private static double[] getRoots(BezierCurve c) {
			// TODO: check that the given BezierCurve is explicit
			HashSet<ImpreciseDouble> roots = getRoots(c, 0, 1);
			ImpreciseDouble[] rootsFuzzyDouble = roots
					.toArray(new ImpreciseDouble[] {});
			double[] rootsDouble = new double[rootsFuzzyDouble.length];
			for (int i = 0; i < rootsDouble.length; i++) {
				rootsDouble[i] = rootsFuzzyDouble[i].getValue();
			}
			return rootsDouble;
		}

		/**
		 * Computes all real roots of this {@link BezierCurve}'s x(t) function.
		 * 
		 * @return all real roots of this {@link BezierCurve}'s x(t) function
		 */
		public double[] getRootsX() {
			return getRoots(getExplicitX());
		}

		/**
		 * Computes all real roots of this {@link BezierCurve}'s y(t) function.
		 * 
		 * @return all real roots of this {@link BezierCurve}'s y(t) function
		 */
		public double[] getRootsY() {
			return getRoots(getExplicitY());
		}

		/**
		 * Computes the real planar {@link Point}s for this {@link BezierCurve}.
		 * 
		 * @return the real planar {@link Point}s for this {@link BezierCurve}
		 */
		public Point[] getRealPoints() {
			Point[] realPoints = new Point[points.length];
			for (int i = 0; i < points.length; i++) {
				realPoints[i] = points[i].toPoint();
			}
			return realPoints;
		}

		/**
		 * Returns the {@link Point} at the given parameter value t.
		 * 
		 * @param t
		 *            Parameter value
		 * @return {@link Point} at parameter value t
		 */
		public Vector3D get(double t) {
			if (t < 0 || t > 1) {
				throw new IllegalArgumentException("t out of range");
			}

			// using horner's scheme:
			int n = points.length;
			if (n < 1) {
				return null;
			}

			double bn = 1, tn = 1, d = 1d - t;
			Vector3D pn = points[0].getScaled(bn * tn);
			for (int i = 1; i < n; i++) {
				bn = bn * (n - i) / i;
				tn = tn * t;
				pn = pn.getScaled(d).getAdded(points[i].getScaled(bn * tn));
			}

			return pn;
		}

		/**
		 * Creates a new {@link BezierCurve} with all points translated by the
		 * given {@link Point}.
		 * 
		 * @param p
		 * @return a new {@link BezierCurve} with all points translated by the
		 *         given {@link Point}
		 */
		public BezierCurve getTranslated(Point p) {
			Point[] translated = new Point[points.length];

			for (int i = 0; i < translated.length; i++) {
				translated[i] = points[i].toPoint().getTranslated(p);
			}

			return new BezierCurve(translated);
		}

		/**
		 * Returns true if the given {@link Point} lies on this
		 * {@link BezierCurve}. Returns false, otherwise.
		 * 
		 * @param p
		 *            the {@link Point} to test for containment
		 * @return true if the {@link Point} is contained, false otherwise
		 */
		public boolean contains(Point p) {
			if (p == null) {
				return false;
			}

			BezierCurve test = this.getTranslated(p.getNegated());
			double[] xts = test.getRootsX();
			double[] yts = test.getRootsY();

			for (double xt : xts) {
				for (double yt : yts) {
					if (PrecisionUtils.equal(xt, yt)) {
						return true;
					} else {
						Point qx = get(xt).toPoint();
						Point qy = get(yt).toPoint();
						// qx != null && qy != null &&
						if (qx.equals(qy)) {
							return true;
						}
					}
				}
			}
			return false;
		}

		/**
		 * Subdivides this {@link BezierCurve} at the given parameter value t
		 * into two new {@link BezierCurve}. The left-of t and the right-of t
		 * {@link BezierCurve} objects.
		 * 
		 * @param t
		 *            Parameter value
		 * @return The left-of t and right-of t {@link BezierCurve} objects
		 */
		public BezierCurve[] split(double t) {
			Vector3D[] leftPoints = new Vector3D[points.length];
			Vector3D[] rightPoints = new Vector3D[points.length];

			Vector3D[] ratioPoints = getPointsCopy();

			for (int i = 0; i < points.length; i++) {
				leftPoints[i] = ratioPoints[0];
				rightPoints[points.length - 1 - i] = ratioPoints[points.length
						- 1 - i];

				for (int j = 0; j < points.length - i - 1; j++) {
					ratioPoints[j] = ratioPoints[j].getRatio(
							ratioPoints[j + 1], t);
				}
			}

			return new BezierCurve[] { new BezierCurve(leftPoints),
					new BezierCurve(rightPoints) };
		}

		/**
		 * Returns a new {@link BezierCurve} object representing this bezier
		 * curve on the interval [s;e].
		 * 
		 * @param s
		 * @param e
		 * @return a new {@link BezierCurve} object representing this bezier
		 *         curve on the interval [s;e]
		 */
		public BezierCurve getClipped(double s, double e) {
			BezierCurve right = split(s)[1];
			double rightT2 = (e - s) / (1 - s);
			return right.split(rightT2)[0];
		}

		/**
		 * Checks if the parameters are considered equal on both curves and adds
		 * the point of intersection of the mid lines of the curves.
		 * 
		 * @param p
		 * @param q
		 * @param intersections
		 * @return true if the parameters are considered equal and false
		 *         otherwise
		 */
		private static Vector3D parameterConvergence(BezierCurve p, double ps,
				double pe, BezierCurve q, double qs, double qe) {
			// localEndPointsCheck();
			if (PrecisionUtils.equal(ps, pe, +2)) {
				Vector3D poi = p.get((ps + pe) / 2);
				return poi;
			}
			if (PrecisionUtils.equal(qs, qe, +2)) {
				Vector3D poi = q.get((qs + qe) / 2);
				return poi;
			}
			return null;
		}

		/**
		 * Returns the bounds of the control polygon of this {@link BezierCurve}
		 * .
		 * 
		 * @return a {@link Rectangle} representing the bounds of the control
		 *         polygon of this {@link BezierCurve}
		 */
		public Rectangle getControlBounds() {
			Point[] realPoints = getRealPoints();

			double xmin = realPoints[0].x, xmax = realPoints[0].x, ymin = realPoints[0].y, ymax = realPoints[0].y;

			for (int i = 1; i < realPoints.length; i++) {
				if (realPoints[i].x < xmin) {
					xmin = realPoints[i].x;
				} else if (realPoints[i].x > xmax) {
					xmax = realPoints[i].x;
				}

				if (realPoints[i].y < ymin) {
					ymin = realPoints[i].y;
				} else if (realPoints[i].y > ymax) {
					ymax = realPoints[i].y;
				}
			}

			return new Rectangle(xmin, ymin, xmax - xmin, ymax - ymin);
		}

		/**
		 * Generates the difference points of this {@link BezierCurve} to the
		 * given line.
		 * 
		 * The difference points are the control points of a Bezier curve that
		 * yields the signed difference of the point on this curve at a
		 * determinate parameter value to the given line.
		 * 
		 * @param line
		 * @return the difference curve's control points
		 */
		private Vector3D[] genDifferencePoints(Vector3D line) {
			Vector3D[] D = new Vector3D[points.length];
			for (int i = 0; i < points.length; i++) {
				double y = line.getDot(points[i]);
				D[i] = new Vector3D(
						(double) (i) / (double) (points.length - 1), y, 1);
			}
			return D;
		}

		public Set<Vector3D> getIntersections(BezierCurve other) {
			// end point intersections
			Set<Vector3D> endPointIntersections = getEndPointIntersections(
					this, other);

			// TODO: tangential intersections

			// simple intersections
			// TODO: recursion => iteration
			Set<Vector3D> intersections = getIntersections(
					endPointIntersections, this, 0, 1, other, 0, 1);

			intersections.addAll(endPointIntersections);
			return intersections;
		}

		private Set<Vector3D> getEndPointIntersections(BezierCurve p,
				BezierCurve q) {
			Set<Vector3D> intersections = new HashSet<Vector3D>();
			for (int i : new int[] { 0, p.points.length - 1 }) {
				if (q.contains(p.points[i].toPoint())) {
					intersections.add(p.points[i]);
				}
			}
			for (int i : new int[] { 0, q.points.length - 1 }) {
				if (p.contains(q.points[i].toPoint())) {
					intersections.add(q.points[i]);
				}
			}
			return intersections;
		}

		private static class FatLine {
			public Vector3D line;
			public double dmin, dmax;

			private FatLine() {
				line = new Vector3D(0, 0, 0);
				dmin = dmax = 0;
			}

			public static FatLine from(BezierCurve c, boolean ortho) {
				FatLine L = new FatLine();
				L.dmin = L.dmax = 0;

				L.line = c.points[0].getCrossed(c.points[c.points.length - 1]);

				if (ortho) {
					L.line = c.points[0].getCrossed(c.points[0]
							.getAdded(new Vector3D(L.line.x, L.line.y, 0)));
				}

				L.line = L.line.getLineNormalized();

				if (L.line == null) {
					return null;
				}

				for (int i = 0; i < c.points.length; i++) {
					double d = L.line.getDot(c.points[i]);
					if (d < L.dmin)
						L.dmin = d;
					else if (d > L.dmax)
						L.dmax = d;
				}

				return L;
			}
		}

		private static double intersectXAxisParallel(Point p, Point q, double y) {
			// p.y != q.y because this routine is only called when either the
			// lower or the higher fat line bound is crossed.
			return new Vector3D(p).getCrossed(new Vector3D(q))
					.getCrossed(new Vector3D(0, 1, -y)).toPoint().x;
			// double dy = q.y - p.y;
			// double s = (y - p.y) / dy;
			// return (q.x - p.x) * s + p.x;
		}

		private static Point[] getConvexHull(Vector3D[] points) {
			Point[] chPoints = new Point[points.length];
			for (int i = 0; i < points.length; i++) {
				chPoints[i] = points[i].toPoint();
			}
			return PointListUtils.getConvexHull(chPoints);
		}

		/**
		 * @param L
		 * @return
		 */
		private double[] clipTo(FatLine L) {
			double[] interval = new double[] { 1, 0 };

			Point[] D = getConvexHull(genDifferencePoints(L.line));

			// we do not know which point is returned first by the
			// getConvexHull() method. That's why we have to check the "first"
			// point, too.
			boolean isBelow = D[0].y < L.dmin;
			boolean isAbove = D[0].y > L.dmax;
			insideFatLineCheck(interval, D, 0, isBelow, isAbove);

			boolean wasBelow = isBelow, wasAbove = isAbove;

			for (int i = 1; i < D.length; i++) {
				isBelow = D[i].y < L.dmin;
				isAbove = D[i].y > L.dmax;

				insideFatLineCheck(interval, D, i, isBelow, isAbove);
				wasBelow = belowFatLineCheck(interval, L, D, i - 1, i, isBelow,
						wasBelow);
				wasAbove = aboveFatLineCheck(interval, L, D, i - 1, i, isAbove,
						wasAbove);
			}

			// closing segment
			isBelow = D[0].y < L.dmin;
			isAbove = D[0].y > L.dmax;
			belowFatLineCheck(interval, L, D, D.length - 1, 0, isBelow,
					wasBelow);
			aboveFatLineCheck(interval, L, D, D.length - 1, 0, isAbove,
					wasAbove);

			return interval;
		}

		private boolean aboveFatLineCheck(double[] interval, FatLine L,
				Point[] D, int i, int j, boolean isAbove, boolean wasAbove) {
			if (isAbove != wasAbove) {
				// crosses higher
				double x = intersectXAxisParallel(D[i], D[j], L.dmax);
				moveInterval(interval, x);
				wasAbove = isAbove;
			}
			return wasAbove;
		}

		private boolean belowFatLineCheck(double[] interval, FatLine L,
				Point[] D, int i, int j, boolean isBelow, boolean wasBelow) {
			if (isBelow != wasBelow) {
				// crosses lower
				double x = intersectXAxisParallel(D[i], D[j], L.dmin);
				moveInterval(interval, x);
				wasBelow = isBelow;
			}
			return wasBelow;
		}

		private void insideFatLineCheck(double[] interval, Point[] D, int i,
				boolean isBelow, boolean isAbove) {
			if (!(isBelow || isAbove)) {
				// inside
				moveInterval(interval, D[i].x);
			}
		}

		private void moveInterval(double[] interval, double x) {
			if (interval[0] > x)
				interval[0] = x;
			if (interval[1] < x)
				interval[1] = x;
		}

		/**
		 * Computes and returns the points of intersection between this
		 * {@link BezierCurve} and the given other {@link BezierCurve}.
		 * 
		 * @param endPointIntersections
		 *            all points of intersections that are end-points of one of
		 *            the curves
		 * @param p
		 *            first {@link BezierCurve}
		 * @param ps
		 *            start value of the first {@link BezierCurve}'s parameter
		 *            interval
		 * @param pe
		 *            end value of the first {@link BezierCurve}'s parameter
		 *            interval
		 * @param q
		 *            second {@link BezierCurve}
		 * @param qs
		 *            start value of the second {@link BezierCurve}'s parameter
		 *            interval
		 * @param qe
		 *            end value of the second {@link BezierCurve}'s parameter
		 *            interval
		 * @return the intersections between this {@link BezierCurve} and the
		 *         given other {@link BezierCurve}
		 */
		public static Set<Vector3D> getIntersections(
				Set<Vector3D> endPointIntersections, BezierCurve p, double ps,
				double pe, BezierCurve q, double qs, double qe) {
			BezierCurve pClipped = p.getClipped(ps, pe);
			BezierCurve qClipped = q.getClipped(qs, qe);

			// end point intersection check
			if (endPointIntersectionConvergence(endPointIntersections,
					pClipped, ps, pe, qClipped, qs, qe)) {
				Set<Vector3D> no_intersections = new HashSet<Vector3D>(0);
				return no_intersections;
			}

			// TODO: tangential intersection check

			// parameter convergence check
			Vector3D poi = parameterConvergence(p, ps, pe, q, qs, qe);
			if (poi != null) {
				// "exactly" one intersection
				if (p.contains(poi.toPoint()) && q.contains(poi.toPoint())) {
					Set<Vector3D> intersection = new HashSet<Vector3D>(1);
					intersection.add(poi);
					return intersection;
				}
				Set<Vector3D> no_intersections = new HashSet<Vector3D>(0);
				return no_intersections;
			}

			Set<Vector3D> intersections = new HashSet<Vector3D>();

			// construct "parallel" and "orthogonal" fat lines
			FatLine L1 = FatLine.from(qClipped, false);
			FatLine L2 = FatLine.from(qClipped, true);

			// curve implosion check
			if (L1 == null || L2 == null) {
				// qClipped is too small to construct a fat line from it
				// therefore, return its mid-point if it is contained by the
				// other curve
				Vector3D mid = q.get((qs + qe) / 2);
				if (p.contains(mid.toPoint())) {
					Set<Vector3D> intersection = new HashSet<Vector3D>(1);
					intersection.add(mid);
					return intersection;
				}
				Set<Vector3D> no_intersections = new HashSet<Vector3D>(0);
				return no_intersections;
			}

			// clip to the fat lines
			double[] interval = pClipped.clipTo(L1);
			double[] interval_ortho = pClipped.clipTo(L2);

			// pick smaller interval range
			if ((interval[1] - interval[0]) > (interval_ortho[1] - interval_ortho[0])) {
				interval[0] = interval_ortho[0];
				interval[1] = interval_ortho[1];
			}

			// re-calculate s and e from the clipped interval
			double news = ps + interval[0] * (pe - ps);
			double newe = ps + interval[1] * (pe - ps);
			double ratio = (newe - news) / (pe - ps);
			ps = news;
			pe = newe;

			if (ratio < 0) {
				// no more intersections
				return intersections;
			} else if (ratio > 0.8) {
				// split longer curve and find intersections for both halves
				if ((pe - ps) > (qe - qs)) {
					double pm = (ps + pe) / 2;
					intersections.addAll(getIntersections(
							endPointIntersections, p, ps, pm, q, qs, qe));
					intersections.addAll(getIntersections(
							endPointIntersections, p, pm
									+ UNRECOGNIZABLE_PRECISION_FRACTION, pe, q,
							qs, qe));
				} else {
					double qm = (qs + qe) / 2;
					intersections.addAll(getIntersections(
							endPointIntersections, q, qs, qm, p, ps, pe));
					intersections.addAll(getIntersections(
							endPointIntersections, q, qm
									+ UNRECOGNIZABLE_PRECISION_FRACTION, qe, p,
							ps, pe));
				}

				return intersections;
			} else {
				// clipped more than 20%
				return getIntersections(endPointIntersections, q, qs, qe, p,
						ps, pe);
			}
		}

		private static boolean endPointIntersectionConvergence(
				Set<Vector3D> endPointIntersections, BezierCurve pClipped,
				double ps, double pe, BezierCurve qClipped, double qs, double qe) {
			if (PrecisionUtils.equal(ps, pe, -3)) {
				if (endPointIntersections.contains(pClipped.points[0])
						|| endPointIntersections
								.contains(pClipped.points[pClipped.points.length - 1])) {
					return true;
				}
			}
			if (PrecisionUtils.equal(qs, qe, -3)) {
				if (endPointIntersections.contains(qClipped.points[0])
						|| endPointIntersections
								.contains(qClipped.points[qClipped.points.length - 1])) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Computes and returns the points of intersection between two
	 * {@link CubicCurve}s.
	 * 
	 * @param p
	 *            the first {@link CubicCurve} to intersect
	 * @param q
	 *            the second {@link CubicCurve} to intersect
	 * @return the intersections between two {@link CubicCurve}s
	 */
	public static Point[] getIntersections(CubicCurve p, CubicCurve q) {
		Set<CurveUtils.Vector3D> intersections = new BezierCurve(p)
				.getIntersections(new BezierCurve(q));

		Set<Point> pois = new HashSet<Point>();
		for (CurveUtils.Vector3D poi : intersections) {
			if (poi.z != 0) {
				pois.add(poi.toPoint());
			}
		}

		return pois.toArray(new Point[] {});
	}

	/**
	 * Computes the {@link Point} of intersection of two {@link Straight}s.
	 * 
	 * @param s1
	 *            the first {@link Straight} to test for intersection
	 * @param s2
	 *            the second {@link Straight} to test for intersection
	 * @return the {@link Point} of intersection if it exists, <code>null</code>
	 *         otherwise
	 */
	public static Point getIntersection(Straight s1, Straight s2) {
		Vector3D l1 = new Vector3D(s1.position.toPoint())
				.getCrossed(new Vector3D(s1.position.getAdded(s1.direction)
						.toPoint()));
		Vector3D l2 = new Vector3D(s2.position.toPoint())
				.getCrossed(new Vector3D(s2.position.getAdded(s2.direction)
						.toPoint()));

		return l1.getCrossed(l2).toPoint();
	}

	/**
	 * Computes the signed distance of the third {@link Point} to the line
	 * through the first two {@link Point}s.
	 * 
	 * The signed distance is positive if the three {@link Point}s are in
	 * counter-clockwise order and negative if the {@link Point}s are in
	 * clockwise order. It is zero if the third {@link Point} lies on the line.
	 * 
	 * If the first two {@link Point}s do not form a line (i.e. they are equal)
	 * this function returns the distance of the first and the last
	 * {@link Point}.
	 * 
	 * @param p
	 *            the start-{@link Point} of the line
	 * @param q
	 *            the end-{@link Point} of the line
	 * @param r
	 *            the relative {@link Point} to test for
	 * @return the signed distance of {@link Point} r to the line through
	 *         {@link Point}s p and q
	 */
	public static double getSignedDistance(Point p, Point q, Point r) {
		Vector3D normalizedLine = new Vector3D(p).getCrossed(new Vector3D(q))
				.getLineNormalized();

		if (normalizedLine == null) {
			return p.getDistance(r);
		}

		double dot = normalizedLine.getDot(new Vector3D(r));
		return -dot;
	}

	/**
	 * Returns the signed distance of the {@link Point} to the {@link Straight}.
	 * 
	 * {@link Point}s that are to the left of the {@link Straight} in the
	 * direction of the {@link Straight}'s direction vector have a positive
	 * distance whereas {@link Point}s to the right of the {@link Straight} in
	 * the direction of the {@link Straight}'s direction vector have a negative
	 * distance.
	 * 
	 * The absolute value of the signed distance is the actual distance of the
	 * {@link Point} to the {@link Straight}.
	 * 
	 * @param s
	 * @param p
	 * @return the signed distance of the {@link Point} to the {@link Straight}
	 * 
	 * @see CurveUtils#getSignedDistance(Point, Point, Point)
	 */
	public static double getSignedDistance(Straight s, Point p) {
		return getSignedDistance(s.position.toPoint(),
				s.position.getAdded(s.direction).toPoint(), p);
	}

	/**
	 * Tests if the given {@link Point} lies on the given {@link CubicCurve}.
	 * 
	 * @param c
	 *            the {@link CubicCurve} to test
	 * @param p
	 *            the {@link Point} to test for containment
	 * @return true if the {@link Point} lies on the {@link CubicCurve}, false
	 *         otherwise
	 */
	public static boolean contains(CubicCurve c, Point p) {
		return new BezierCurve(c).contains(p);
	}

	/**
	 * Tests if the given {@link Point} lies on the given {@link QuadraticCurve}
	 * .
	 * 
	 * @param c
	 *            the {@link QuadraticCurve} to test
	 * @param p
	 *            the {@link Point} to test for containment
	 * @return true if the {@link Point} lies on the {@link QuadraticCurve},
	 *         false otherwise
	 */
	public static boolean contains(QuadraticCurve c, Point p) {
		return new BezierCurve(c).contains(p);
	}

	/**
	 * Subdivides the given {@link CubicCurve} at parameter value t in the left
	 * and right sub-curves.
	 * 
	 * @param c
	 *            the {@link CubicCurve} to subdivide
	 * @param t
	 *            the parameter value to subdivide at
	 * @return the left and right sub-curves as an array of {@link CubicCurve}
	 */
	public static CubicCurve[] split(CubicCurve c, double t) {
		BezierCurve[] split = new BezierCurve(c).split(t);
		return new CubicCurve[] { new CubicCurve(split[0].getRealPoints()),
				new CubicCurve(split[1].getRealPoints()) };
	}

	/**
	 * Subdivides the given {@link QuadraticCurve} at parameter value t in the
	 * left and right sub-curves.
	 * 
	 * @param c
	 *            the {@link QuadraticCurve} to subdivide
	 * @param t
	 *            the parameter value to subdivide at
	 * @return the left and right sub-curves as an array of
	 *         {@link QuadraticCurve}
	 */
	public static QuadraticCurve[] split(QuadraticCurve c, double t) {
		BezierCurve[] split = new BezierCurve(c).split(t);
		return new QuadraticCurve[] {
				new QuadraticCurve(split[0].getRealPoints()),
				new QuadraticCurve(split[1].getRealPoints()) };
	}

	/**
	 * Returns a new {@link QuadraticCurve} that represents the given
	 * {@link QuadraticCurve} on the parameter interval [t1;t2].
	 * 
	 * @param c
	 *            the {@link QuadraticCurve} to clip
	 * @param t1
	 *            lower parameter bound
	 * @param t2
	 *            upper parameter bound
	 * @return a new {@link QuadraticCurve} that represents the given
	 *         {@link QuadraticCurve} on the interval [t1;t2]
	 */
	public static QuadraticCurve clip(QuadraticCurve c, double t1, double t2) {
		BezierCurve bc = new BezierCurve(c);
		return new QuadraticCurve(bc.getClipped(t1, t2).getRealPoints());
	}

	/**
	 * Returns a new {@link CubicCurve} that represents the given
	 * {@link CubicCurve} on the parameter interval [t1;t2].
	 * 
	 * @param c
	 *            the {@link CubicCurve} to clip
	 * @param t1
	 *            lower parameter bound
	 * @param t2
	 *            upper parameter bound
	 * @return a new {@link CubicCurve} that represents the given
	 *         {@link CubicCurve} on the interval [t1;t2]
	 */
	public static CubicCurve clip(CubicCurve c, double t1, double t2) {
		BezierCurve bc = new BezierCurve(c);
		return new CubicCurve(bc.getClipped(t1, t2).getRealPoints());
	}

	/**
	 * Evaluates the {@link Point} on the given {@link CubicCurve} at parameter
	 * value t.
	 * 
	 * @param c
	 *            the {@link CubicCurve}
	 * @param t
	 *            the parameter value
	 * @return the {@link Point} on the given {@link CubicCurve} at parameter
	 *         value t
	 */
	public static Point get(CubicCurve c, double t) {
		return new BezierCurve(c).get(t).toPoint();
	}

	/**
	 * Evaluates the {@link Point} on the given {@link QuadraticCurve} at
	 * parameter value t.
	 * 
	 * @param c
	 *            the {@link QuadraticCurve}
	 * @param t
	 *            the parameter value
	 * @return the {@link Point} on the given {@link QuadraticCurve} at
	 *         parameter value t
	 */
	public static Point get(QuadraticCurve c, double t) {
		return new BezierCurve(c).get(t).toPoint();
	}

	/**
	 * Computes and returns the bounds of the control polygon of the given
	 * {@link CubicCurve}. The control polygon is the convex hull of the start-,
	 * end-, and control points of the {@link CubicCurve}.
	 * 
	 * @param c
	 *            the {@link CubicCurve} to compute the control bounds for
	 * @return the bounds of the control polygon of the given {@link CubicCurve}
	 */
	public static Rectangle getControlBounds(CubicCurve c) {
		return new BezierCurve(c).getControlBounds();
	}
}

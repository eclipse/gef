/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.projective.Straight3D;
import org.eclipse.gef4.geometry.projective.Vector3D;
import org.eclipse.gef4.geometry.utils.PointListUtils;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Abstract base class of Bezier Curves.
 * 
 * @author anyssen
 */
public class BezierCurve extends AbstractGeometry implements ICurve,
		ITranslatable<BezierCurve>, IScalable<BezierCurve>,
		IRotatable<BezierCurve> {

	private static class FatLine {
		public static FatLine from(BezierCurve c, boolean ortho) {
			FatLine L = new FatLine();
			L.dmin = L.dmax = 0;

			L.line = Straight3D.through(c.points[0],
					c.points[c.points.length - 1]);
			if (L.line == null) {
				return null;
			}

			if (ortho) {
				L.line = L.line.getOrtho();
			}
			if (L.line == null) {
				return null;
			}

			for (int i = 0; i < c.points.length; i++) {
				double d = L.line.getSignedDistanceCW(c.points[i]);
				if (d < L.dmin)
					L.dmin = d;
				else if (d > L.dmax)
					L.dmax = d;
			}

			return L;
		}

		public Straight3D line;

		public double dmin, dmax;

		private FatLine() {
			line = null;
			dmin = dmax = 0;
		}
	}

	/**
	 * Representation of an interval [a;b]. It is used to represent sub-curves
	 * of a {@link BezierCurve}.
	 * 
	 * @author wienand
	 */
	public static final class Interval {
		/**
		 * Constructs a new {@link Interval} object holding an invalid parameter
		 * interval.
		 * 
		 * @return a new {@link Interval} object holding an invalid parameter
		 *         interval
		 */
		public static Interval getEmpty() {
			return new Interval(1, 0);
		}

		/**
		 * Constructs a new {@link Interval} object holding the interval [0;1]
		 * which is the parameter interval representing a full
		 * {@link BezierCurve}.
		 * 
		 * @return a new {@link Interval} object holding the interval [0;1]
		 */
		public static Interval getFull() {
			return new Interval(0, 1);
		}

		/**
		 * Returns the smaller {@link Interval} object, i.e. the one with the
		 * smallest parameter range.
		 * 
		 * @param i
		 * @param j
		 * @return the {@link Interval} with the smallest parameter range
		 */
		public static Interval min(Interval i, Interval j) {
			return (i.b - i.a) > (j.b - j.a) ? j : i;
		}

		/**
		 * An {@link Interval} holds the parameter range [a;b]. Valid parameter
		 * ranges require 0 <= a <= b <= 1.
		 */
		public double a;

		/**
		 * An {@link Interval} holds the parameter range [a;b]. Valid parameter
		 * ranges require 0 <= a <= b <= 1.
		 */
		public double b;

		/**
		 * Constructs a new {@link Interval} object from the given double
		 * values. Only the first two double values are of importance as the
		 * rest of them are ignored.
		 * 
		 * The new {@link Interval} holds the parameter range [a;b] if a is the
		 * first double value and b is the second double value.
		 * 
		 * @param ds
		 */
		public Interval(double... ds) {
			if (ds.length > 1) {
				a = ds[0];
				b = ds[1];
			} else {
				throw new IllegalArgumentException(
						"not enough values to create interval");
			}
		}

		/**
		 * Tests if this {@link Interval}'s parameter range does converge with
		 * default imprecision. Returns <code>true</code> for a ~= b,
		 * <code>false</code> otherwise.
		 * 
		 * @see Interval#converges(int)
		 * 
		 * @return <code>true</code> if a ~= b (default imprecision),
		 *         <code>false</code> otherwise
		 */
		public boolean converges() {
			return converges(0);
		}

		/**
		 * Tests if this {@link Interval}'s parameter range does converge with
		 * specified imprecision. Returns <code>true</code> for a ~= b,
		 * <code>false</code> otherwise.
		 * 
		 * The imprecision is specified by providing a shift value which shifts
		 * the epsilon used for the number comparison. A positive shift demands
		 * for a smaller epsilon (higher precision) whereas a negative shift
		 * demands for a greater epsilon (lower precision).
		 * 
		 * @param shift
		 *            precision shift
		 * @return <code>true</code> if a ~= b (specified imprecision),
		 *         <code>false</code> otherwise
		 */
		public boolean converges(int shift) {
			return PrecisionUtils.equal(a, b, shift);
		}

		/**
		 * Expands this {@link Interval} to include the given other
		 * {@link Interval}.
		 * 
		 * @param i
		 */
		public void expand(Interval i) {
			if (i.a < a)
				a = i.a;
			if (i.b > b)
				b = i.b;
		}

		/**
		 * Returns a copy of this {@link Interval}.
		 * 
		 * @return a copy of this {@link Interval}
		 */
		public Interval getCopy() {
			return new Interval(a, b);
		}

		/**
		 * Returns the middle parameter value m = (a+b)/2 of this
		 * {@link Interval}.
		 * 
		 * @return the middle parameter value of this {@link Interval}
		 */
		public double getMid() {
			return (a + b) / 2;
		}

		/**
		 * Scales this {@link Interval} to the given {@link Interval}. The given
		 * {@link Interval} specifies the new upper and lower bounds of this
		 * {@link Interval} in percent.
		 * 
		 * Returns the ratio of this {@link Interval}'s new parameter range to
		 * its old parameter range.
		 * 
		 * @param interval
		 *            the new upper and lower bounds in percent
		 * @return the ratio of this {@link Interval}'s new parameter range to
		 *         its old parameter range
		 */
		public double scaleTo(Interval interval) {
			double na = a + interval.a * (b - a);
			double nb = a + interval.b * (b - a);
			double ratio = (nb - na) / (b - a);
			a = na;
			b = nb;
			return ratio;
		}
	}

	/**
	 * An {@link IntervalPair} combines two {@link BezierCurve}s and their
	 * corresponding parameter ranges.
	 * 
	 * @author wienand
	 */
	public static final class IntervalPair {
		/**
		 * The first {@link BezierCurve}.
		 */
		public BezierCurve p;
		/**
		 * The second {@link BezierCurve}.
		 */
		public BezierCurve q;
		/**
		 * The parameter {@link Interval} for the first {@link BezierCurve}.
		 */
		public Interval pi;
		/**
		 * The parameter {@link Interval} for the second {@link BezierCurve}.
		 */
		public Interval qi;

		/**
		 * Constructs a new {@link IntervalPair} with the given
		 * {@link BezierCurve}s and their corresponding parameter ranges.
		 * 
		 * @param pp
		 *            the first {@link BezierCurve}
		 * @param pt
		 *            the parameter {@link Interval} for the first
		 *            {@link BezierCurve}
		 * @param pq
		 *            the second {@link BezierCurve}
		 * @param pu
		 *            the parameter {@link Interval} for the second
		 *            {@link BezierCurve}
		 */
		public IntervalPair(BezierCurve pp, Interval pt, BezierCurve pq,
				Interval pu) {
			p = pp;
			pi = pt;
			q = pq;
			qi = pu;
		}

		/**
		 * Tests if both parameter {@link Interval}s do converge (@see
		 * Interval#converges()) or both {@link BezierCurve}s are degenerated,
		 * i.e. they are collapsed to a single {@link Point}.
		 * 
		 * @return <code>true</true> if both parameter {@link Interval}s do converge, <code>false</code>
		 *         otherwise
		 */
		public boolean converges() {
			return converges(0);
		}

		/**
		 * Tests if both parameter {@link Interval}s do converge (@see
		 * Interval#converges(int)) or both {@link BezierCurve}s are
		 * degenerated, i.e. they are collapsed to a single {@link Point}.
		 * 
		 * @param shift
		 *            the precision shift
		 * @return <code>true</true> if both parameter {@link Interval}s do converge, <code>false</code>
		 *         otherwise
		 */
		public boolean converges(int shift) {
			return (pi.converges(shift) || pointsEquals(
					p.getHC(pi.a).toPoint(), p.getHC(pi.b).toPoint(), shift))
					&& (qi.converges(shift) || pointsEquals(q.getHC(qi.a)
							.toPoint(), q.getHC(qi.b).toPoint(), shift));
			// return pi.converges(shift) && qi.converges(shift);
		}

		/**
		 * Expands this {@link IntervalPair} to include the given other
		 * {@link IntervalPair}.
		 * 
		 * @param ip
		 */
		public void expand(IntervalPair ip) {
			if (p == ip.p) {
				pi.expand(ip.pi);
				qi.expand(ip.qi);
			} else {
				pi.expand(ip.qi);
				qi.expand(ip.pi);
			}
		}

		/**
		 * Returns a copy of this {@link IntervalPair}. The underlying
		 * {@link BezierCurve}s are only shallow copied. The corresponding
		 * parameter {@link Interval}s are truly copied.
		 * 
		 * @return a copy of this {@link IntervalPair}
		 */
		public IntervalPair getCopy() {
			return new IntervalPair(p, pi.getCopy(), q, qi.getCopy());
		}

		/**
		 * Returns the first sub-curve of this {@link IntervalPair}. This curve
		 * is the first {@link BezierCurve} p over its corresponding parameter
		 * {@link Interval} pi.
		 * 
		 * @return the first sub-curve of this {@link IntervalPair}
		 */
		public BezierCurve getPClipped() {
			return p.getClipped(Math.max(pi.a, 0), Math.min(pi.b, 1));
		}

		/**
		 * Splits the first parameter {@link Interval} pi at half and returns
		 * the resulting {@link IntervalPair}s.
		 * 
		 * @return two {@link IntervalPair}s representing a split of the first
		 *         paramter {@link Interval} in half
		 */
		public IntervalPair[] getPSplit() {
			double pm = (pi.a + pi.b) / 2;
			return new IntervalPair[] {
					new IntervalPair(p, new Interval(pi.a, pm), q, qi.getCopy()),
					new IntervalPair(p, new Interval(pm + 10
							* UNRECOGNIZABLE_PRECISION_FRACTION, pi.b), q,
							qi.getCopy()) };
		}

		/**
		 * Returns the second sub-curve of this {@link IntervalPair}. This curve
		 * is the second {@link BezierCurve} q over its corresponding parameter
		 * {@link Interval} qi.
		 * 
		 * @return the second sub-curve of this {@link IntervalPair}
		 */
		public BezierCurve getQClipped() {
			return q.getClipped(Math.max(qi.a, 0), Math.min(qi.b, 1));
		}

		/**
		 * Splits the second parameter {@link Interval} qi at half and returns
		 * the resulting {@link IntervalPair}s.
		 * 
		 * @return two {@link IntervalPair}s representing a split of the second
		 *         paramter {@link Interval} in half
		 */
		public IntervalPair[] getQSplit() {
			double qm = (qi.a + qi.b) / 2;
			return new IntervalPair[] {
					new IntervalPair(q, new Interval(qi.a, qm), p, pi.getCopy()),
					new IntervalPair(q, new Interval(qm + 10
							* UNRECOGNIZABLE_PRECISION_FRACTION, qi.b), p,
							pi.getCopy()) };
		}

		/**
		 * Creates a new {@link IntervalPair} with swapped {@link BezierCurve}s
		 * and their parameter {@link Interval}s.
		 * 
		 * @return a new {@link IntervalPair} with swapped {@link BezierCurve}s
		 *         and their parameter {@link Interval}s
		 */
		public IntervalPair getSwapped() {
			return new IntervalPair(q, qi.getCopy(), p, pi.getCopy());
		}

		/**
		 * Calculates which {@link BezierCurve}'s parameter {@link Interval} is
		 * longer. Returns <code>true</code> if the distance from start
		 * parameter to end parameter of the frist parameter {@link Interval} pi
		 * is greater than the distance from start parameter to end parameter of
		 * the second parameter {@link Interval} qi. Otherwise, returns
		 * <code>false</code>.
		 * 
		 * @return <code>true</code> if the distance from start to end parameter
		 *         value of the first parameter {@link Interval} pi is greater
		 *         than the distance from start to end parameter value of the
		 *         second parameter {@link Interval} qi. Othwise, returns
		 *         <code>false</code>.
		 */
		public boolean isPLonger() {
			return (pi.b - pi.a) > (qi.b - qi.a);
		}
	}

	private interface IPointCmp {
		public boolean pIsBetterThanQ(Point p, Point q);
	}

	private static final long serialVersionUID = 1L;

	// TODO: use constants that limit the number of iterations for the
	// different iterative/recursive algorithms:
	// INTERSECTIONS_MAX_ITERATIONS, APPROXIMATION_MAX_ITERATIONS

	private static final int CHUNK_SHIFT = -3;

	private static final boolean ORTHOGONAL = true;

	private static final boolean PARALLEL = false;

	private static final double UNRECOGNIZABLE_PRECISION_FRACTION = PrecisionUtils
			.calculateFraction(0) / 10;

	private static IntervalPair[] clusterChunks(IntervalPair[] intervalPairs,
			int shift) {
		ArrayList<IntervalPair> ips = new ArrayList<IntervalPair>();

		ips.addAll(Arrays.asList(intervalPairs));

		Collections.sort(ips, new Comparator<IntervalPair>() {
			public int compare(IntervalPair i, IntervalPair j) {
				return i.pi.a <= j.pi.a ? -1 : 1;
			}
		});

		// for (IntervalPair ip : ips) {
		// System.out.println("P [" + ip.pi.a + ";" + ip.pi.b + "]  Q ["
		// + ip.qi.a + ";" + ip.qi.b + "]");
		// }

		ArrayList<IntervalPair> clusters = new ArrayList<IntervalPair>();
		IntervalPair current = null;
		boolean couldMerge;

		do {
			clusters.clear();
			couldMerge = false;
			for (IntervalPair i : ips) {
				if (current == null) {
					current = i.getCopy();
				} else if (isNextTo(current, i, shift)) {
					couldMerge = true;
					current.expand(i);
				} else {
					isNextTo(current, i, shift);
					clusters.add(current);
					current = i.getCopy();
				}
			}
			if (current != null) {
				clusters.add(current);
				current = null;
			}
			ips.clear();
			ips.addAll(clusters);
		} while (couldMerge);

		return clusters.toArray(new IntervalPair[] {});
	}

	private static void copyIntervalPair(IntervalPair a, IntervalPair b) {
		a.p = b.p;
		a.q = b.q;
		a.pi = b.pi;
		a.qi = b.qi;
	}

	private static IntervalPair extractOverlap(
			IntervalPair[] intersectionCandidates, IntervalPair[] endPoints) {
		// merge intersection candidates and end points
		IntervalPair[] fineChunks = new IntervalPair[intersectionCandidates.length
				+ endPoints.length];
		for (int i = 0; i < intersectionCandidates.length; i++) {
			fineChunks[i] = intersectionCandidates[i];
		}
		for (int i = 0; i < endPoints.length; i++) {
			fineChunks[intersectionCandidates.length + i] = endPoints[i];
		}

		if (fineChunks.length == 0) {
			return null;
		}

		// recluster chunks
		normalizeIntervalPairs(fineChunks);
		IntervalPair[] chunks = clusterChunks(fineChunks, CHUNK_SHIFT - 1);

		/*
		 * if they overlap, the chunk has to start/end in a start-/endpoint of
		 * the curves.
		 */

		for (IntervalPair overlap : chunks) {
			if (PrecisionUtils.smallerEqual(overlap.pi.a, 0)
					&& PrecisionUtils.greaterEqual(overlap.pi.b, 1)
					|| PrecisionUtils.smallerEqual(overlap.qi.a, 0)
					&& PrecisionUtils.greaterEqual(overlap.qi.b, 1)
					|| (PrecisionUtils.smallerEqual(overlap.pi.a, 0) || PrecisionUtils
							.greaterEqual(overlap.pi.b, 1))
					&& (PrecisionUtils.smallerEqual(overlap.qi.a, 0) || PrecisionUtils
							.greaterEqual(overlap.qi.b, 1))) {
				// it overlaps
				if (PrecisionUtils.smallerEqual(overlap.pi.a, 0,
						CHUNK_SHIFT - 1)
						&& PrecisionUtils.smallerEqual(overlap.pi.b, 0,
								CHUNK_SHIFT - 1)
						|| PrecisionUtils.greaterEqual(overlap.pi.a, 1,
								CHUNK_SHIFT - 1)
						&& PrecisionUtils.greaterEqual(overlap.pi.b, 1,
								CHUNK_SHIFT - 1)
						|| PrecisionUtils.smallerEqual(overlap.qi.a, 0,
								CHUNK_SHIFT - 1)
						&& PrecisionUtils.smallerEqual(overlap.qi.b, 0,
								CHUNK_SHIFT - 1)
						|| PrecisionUtils.greaterEqual(overlap.qi.a, 1,
								CHUNK_SHIFT - 1)
						&& PrecisionUtils.greaterEqual(overlap.qi.b, 1,
								CHUNK_SHIFT - 1)) {
					// only end-point-intersection
					return null;
				}
				return refineOverlap(overlap);
			}
		}

		return null;
	}

	/**
	 * Returns the convex hull of the given {@link Vector3D}s.
	 * 
	 * The {@link PointListUtils#getConvexHull(Point[])} method is used to
	 * calculate the convex hull. This method does only accept {@link Point} s
	 * for input.
	 * 
	 * @param vectors
	 * @return
	 */
	private static Point[] getConvexHull(Vector3D[] vectors) {
		Point[] points = new Point[vectors.length];
		for (int i = 0; i < vectors.length; i++) {
			points[i] = vectors[i].toPoint();
		}
		return PointListUtils.getConvexHull(points);
	}

	/**
	 * Computes the intersection of the line from {@link Point} p to
	 * {@link Point} q with the x-axis-parallel line f(x) = y.
	 * 
	 * There is always an intersection, because this routine is only called when
	 * either the lower or the higher fat line bound is crossed.
	 * 
	 * The following conditions are fulfilled: (p.x!=q.x) and (p.y!=q.y) and
	 * (p.y<y<q.y) or (p.y>y>q.y).
	 * 
	 * From these values, one can build a function g(x) = m*x + b where
	 * m=(q.y-p.y)/(q.x-p.x) and b=p.y-m*p.x.
	 * 
	 * The point of intersection is given by f(x) = g(x). The x-coordinate of
	 * this point is x = (y - b) / m.
	 * 
	 * @param p
	 *            The start point of the {@link Line}
	 * @param q
	 *            The end point of the {@link Line}
	 * @param y
	 *            The x-axis-parallel line f(x) = y
	 * @return the x coordinate of the intersection point.
	 */
	private static double intersectXAxisParallel(Point p, Point q, double y) {
		double m = (q.y - p.y) / (q.x - p.x);
		return (y - p.y + m * p.x) / m;
	}

	private static boolean isNextTo(Interval i, Interval j, int shift) {
		return PrecisionUtils.smallerEqual(j.a, i.b, shift)
				&& PrecisionUtils.greaterEqual(j.b, i.a, shift);
	}

	private static boolean isNextTo(IntervalPair a, IntervalPair b, int shift) {
		return isNextTo(a.pi, b.pi, shift) && isNextTo(a.qi, b.qi, shift);
	}

	private static void normalizeIntervalPairs(IntervalPair[] intervalPairs) {
		// in every interval, p and q have to be the same curves
		if (intervalPairs.length == 0) {
			return;
		}

		BezierCurve pId = intervalPairs[0].p;
		BezierCurve qId = intervalPairs[0].q;

		for (IntervalPair ip : intervalPairs) {
			if (ip.p != pId) {
				Interval qi = ip.pi;
				Interval pi = ip.qi;
				ip.p = pId;
				ip.q = qId;
				ip.pi = pi;
				ip.qi = qi;
			}
		}
	}

	private static boolean pointsEquals(Point p1, Point p2, int shift) {
		return PrecisionUtils.equal(p1.x, p2.x, shift)
				&& PrecisionUtils.equal(p1.y, p2.y, shift);
	}

	/**
	 * Binary search from the intervals' limits to the intervals' inner values.
	 * 
	 * @param overlap
	 *            {@link IntervalPair} representing the overlap of two
	 *            {@link BezierCurve}s
	 * @return refined overlap
	 */
	private static IntervalPair refineOverlap(IntervalPair overlap) {
		Interval piLo = refineOverlapLo(overlap.p, overlap.pi.a,
				overlap.pi.getMid(), overlap.q);
		Interval piHi = refineOverlapHi(overlap.p, overlap.pi.getMid(),
				overlap.pi.b, overlap.q);
		Interval qiLo = refineOverlapLo(overlap.q, overlap.qi.a,
				overlap.qi.getMid(), overlap.p);
		Interval qiHi = refineOverlapHi(overlap.q, overlap.qi.getMid(),
				overlap.qi.b, overlap.p);
		overlap.pi.a = piLo.b;
		overlap.pi.b = piHi.a;
		overlap.qi.a = qiLo.b;
		overlap.qi.b = qiHi.a;
		return overlap;
	}

	private static Interval refineOverlapHi(BezierCurve p, double mid,
			double b, BezierCurve q) {
		Interval i = new Interval(Math.max(mid, 0), Math.min(b, 1));
		double prevLo;
		Point pLo;
		int c = 0;

		while (c++ < 30 && !i.converges()) {
			prevLo = i.a;
			i.a = i.getMid();
			pLo = p.get(i.a);

			if (!q.contains(pLo)) {
				i.b = i.a;
				i.a = prevLo;
			}
		}

		return i;
	}

	/**
	 * @param p
	 * @param a
	 * @param mid
	 * @param q
	 * @return
	 */
	private static Interval refineOverlapLo(BezierCurve p, double a,
			double mid, BezierCurve q) {
		Interval i = new Interval(Math.max(a, 0), Math.min(mid, 1));
		double prevHi;
		Point pHi;
		int c = 0;

		while (c++ < 30 && !i.converges()) {
			prevHi = i.b;
			i.b = i.getMid();
			pHi = p.get(i.b);

			if (!q.contains(pHi)) {
				i.a = i.b;
				i.b = prevHi;
			}
		}

		return i;
	}

	private Vector3D[] points;

	private static final IPointCmp xminCmp = new IPointCmp() {
		public boolean pIsBetterThanQ(Point p, Point q) {
			return PrecisionUtils.smallerEqual(p.x, q.x);
		}
	};

	private static final IPointCmp xmaxCmp = new IPointCmp() {
		public boolean pIsBetterThanQ(Point p, Point q) {
			return PrecisionUtils.greaterEqual(p.x, q.x);
		}
	};

	private static final IPointCmp yminCmp = new IPointCmp() {
		public boolean pIsBetterThanQ(Point p, Point q) {
			return PrecisionUtils.smallerEqual(p.y, q.y);
		}
	};

	private static final IPointCmp ymaxCmp = new IPointCmp() {
		public boolean pIsBetterThanQ(Point p, Point q) {
			return PrecisionUtils.greaterEqual(p.y, q.y);
		}
	};

	private static boolean containmentParameter(BezierCurve c,
			double[] interval, Point p) {
		Stack<Interval> parts = new Stack<Interval>();
		parts.push(new Interval(interval));
		while (!parts.empty()) {
			Interval i = parts.pop();

			if (i.converges(1)) {
				interval[0] = i.a;
				interval[1] = i.b;
				break;
			}

			double iMid = i.getMid();
			Interval left = new Interval(i.a, iMid);
			Interval right = new Interval(iMid, i.b);

			BezierCurve clipped = c.getClipped(left.a, left.b);
			Rectangle bounds = clipped.getControlBounds();

			if (bounds.contains(p)) {
				parts.push(left);
			}

			clipped = c.getClipped(right.a, right.b);
			bounds = clipped.getControlBounds();

			if (bounds.contains(p)) {
				parts.push(right);
			}
		}
		return PrecisionUtils.equal(interval[0], interval[1], 1);
	}

	/**
	 * Returns the similarity of the given {@link BezierCurve} to a {@link Line}
	 * , which is defined as the absolute distance of its control points to the
	 * base line connecting its end points.
	 * 
	 * A similarity of 0 means that the given {@link BezierCurve}'s control
	 * points are on a straight line.
	 * 
	 * @param c
	 * @return
	 */
	private static double distanceToBaseLine(BezierCurve c) {
		Straight3D baseLine = Straight3D.through(c.points[0],
				c.points[c.points.length - 1]);

		if (baseLine == null) {
			return 0d;
		}

		double maxDistance = 0d;
		for (int i = 1; i < c.points.length - 1; i++) {
			maxDistance = Math.max(maxDistance,
					Math.abs(baseLine.getSignedDistanceCW(c.points[i])));
		}

		return maxDistance;
	}

	/**
	 * Constructs a new {@link BezierCurve} from the given {@link CubicCurve}.
	 * 
	 * @param c
	 */
	public BezierCurve(CubicCurve c) {
		this(c.getP1(), c.getCtrl1(), c.getCtrl2(), c.getP2());
	}

	/**
	 * Constructs a new {@link BezierCurve} from the given control point
	 * coordinates. The coordinates are expected to be in x, y order, i.e. x1,
	 * y1, x2, y2, x3, y3, ...
	 * 
	 * @param controlPoints
	 */
	public BezierCurve(double... controlPoints) {
		this(PointListUtils.toPointsArray(controlPoints));
	}

	/**
	 * Constructs a new {@link BezierCurve} object from the given control
	 * points.
	 * 
	 * @param controlPoints
	 */
	public BezierCurve(Point... controlPoints) {
		points = new Vector3D[controlPoints.length];
		for (int i = 0; i < points.length; i++) {
			points[i] = new Vector3D(controlPoints[i].x, controlPoints[i].y, 1);
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
	 * Constructs a new {@link BezierCurve} object from the given control
	 * points.
	 * 
	 * Note that a Point(2, 3) is represented by a Vector3D(2, 3, 1). So for a
	 * Point(x, y) the corresponding vector is Vector(x, y, 1).
	 * 
	 * @param controlPoints
	 */
	private BezierCurve(Vector3D... controlPoints) {
		points = new Vector3D[controlPoints.length];
		for (int i = 0; i < points.length; i++) {
			points[i] = controlPoints[i].getCopy();
		}
	}

	/**
	 * There are three tests, that have to be done.
	 * 
	 * The inside-fat-line-check has to be done for every point. It adjusts the
	 * interval, so that inseparable portions of the curve are detected.
	 * 
	 * The other two checks have to be done for every segment. They find
	 * intersections of the curve's convex hull with the fat-line boundaries.
	 * The points of intersection identify points of breakage.
	 * 
	 * The starting interval is chosen to be invalid. The individual checks move
	 * the start and end parameter values past to one another. If everything can
	 * be clipped, the resulting interval remains invalid. If the resulting
	 * interval I = [a;b] is valid (a <= b), then the portions [0;a] and [b;1]
	 * of the curve can be clipped away.
	 * 
	 * @param L
	 *            The {@link FatLine} to clip to
	 * @return the new parameter interval for this {@link BezierCurve}.
	 */
	private double[] clipTo(FatLine L) {
		double[] interval = new double[] { 1, 0 };

		Point[] D = getConvexHull(genDifferencePoints(L.line));

		for (Point p : D) {
			if (Double.isNaN(p.y) || L.dmin <= p.y && p.y <= L.dmax) {
				moveInterval(interval, p.x);
			}
		}

		for (Line seg : PointListUtils.toSegmentsArray(D, true)) {
			if (seg.getP1().y < L.dmin != seg.getP2().y < L.dmin) {
				double x = intersectXAxisParallel(seg.getP1(), seg.getP2(),
						L.dmin);
				moveInterval(interval, x);
			}
			if (seg.getP1().y < L.dmax != seg.getP2().y < L.dmax) {
				double x = intersectXAxisParallel(seg.getP1(), seg.getP2(),
						L.dmax);
				moveInterval(interval, x);
			}
		}

		return interval;
	}

	/**
	 * <p>
	 * Tests if this {@link BezierCurve} contains the given other
	 * {@link BezierCurve}.
	 * </p>
	 * 
	 * <p>
	 * The other {@link BezierCurve} is regarded to be contained if its start
	 * and end {@link Point} lie on this {@link BezierCurve} and an overlapping
	 * segment of the two curves can be detected.
	 * </p>
	 * 
	 * @param o
	 * @return <code>true</code> if the given {@link BezierCurve} is contained
	 *         by this {@link BezierCurve}, otherwise <code>false</code>
	 */
	public boolean contains(BezierCurve o) {
		return contains(o.getP1()) && contains(o.getP2())
				&& getOverlap(o) != null;
	}

	/**
	 * Returns true if the given {@link Point} lies on this {@link BezierCurve}.
	 * Returns false, otherwise.
	 * 
	 * @param p
	 *            the {@link Point} to test for containment
	 * @return true if the {@link Point} is contained, false otherwise
	 */
	public boolean contains(final Point p) {
		if (p == null) {
			return false;
		}

		return containmentParameter(this, new double[] { 0, 1 }, p);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BezierCurve) {
			BezierCurve o = (BezierCurve) obj;
			BezierCurve t = this;
			while (o.points.length < t.points.length)
				o = o.getElevated();
			while (t.points.length < o.points.length)
				t = t.getElevated();
			Point[] oPoints = o.getPoints();
			Point[] tPoints = t.getPoints();
			return PointListUtils.equals(oPoints, tPoints)
					|| PointListUtils.equalsReverse(oPoints, tPoints);
		}
		return false;
	}

	private void findEndPointIntersections(IntervalPair ip,
			Set<IntervalPair> endPointIntervalPairs, Set<Point> intersections) {
		final double CHUNK_SHIFT_EPSILON = PrecisionUtils
				.calculateFraction(CHUNK_SHIFT);

		Point poi = ip.p.points[0].toPoint();
		double[] interval = new double[] { 0, 1 };
		if (containmentParameter(ip.q, interval, poi)) {
			ip.pi.a = CHUNK_SHIFT_EPSILON;
			interval[0] = (interval[0] + interval[1]) / 2;
			interval[1] = interval[0] + CHUNK_SHIFT_EPSILON / 2;
			interval[0] = interval[0] - CHUNK_SHIFT_EPSILON / 2;
			endPointIntervalPairs.add(new IntervalPair(ip.p, new Interval(0,
					ip.pi.a), ip.q, new Interval(interval)));
			intersections.add(poi);
		}

		poi = ip.p.points[ip.p.points.length - 1].toPoint();
		interval[0] = 0;
		interval[1] = 1;
		if (containmentParameter(ip.q, interval, poi)) {
			ip.pi.b = 1 - CHUNK_SHIFT_EPSILON;
			interval[0] = (interval[0] + interval[1]) / 2;
			interval[1] = interval[0] + CHUNK_SHIFT_EPSILON / 2;
			interval[0] = interval[0] - CHUNK_SHIFT_EPSILON / 2;
			endPointIntervalPairs.add(new IntervalPair(ip.p, new Interval(
					ip.pi.b, 1), ip.q, new Interval(interval)));
			intersections.add(poi);
		}

		poi = ip.q.points[0].toPoint();
		interval[0] = 0;
		interval[1] = 1;
		if (containmentParameter(ip.p, interval, poi)) {
			ip.qi.a = CHUNK_SHIFT_EPSILON;
			interval[0] = (interval[0] + interval[1]) / 2;
			interval[1] = interval[0] + CHUNK_SHIFT_EPSILON / 2;
			interval[0] = interval[0] - CHUNK_SHIFT_EPSILON / 2;
			endPointIntervalPairs.add(new IntervalPair(ip.p, new Interval(
					interval), ip.q, new Interval(0, ip.qi.a)));
			intersections.add(poi);
		}

		poi = ip.q.points[ip.q.points.length - 1].toPoint();
		interval[0] = 0;
		interval[1] = 1;
		if (containmentParameter(ip.p, interval, poi)) {
			ip.qi.b = 1 - CHUNK_SHIFT_EPSILON;
			interval[0] = (interval[0] + interval[1]) / 2;
			interval[1] = interval[0] + CHUNK_SHIFT_EPSILON / 2;
			interval[0] = interval[0] - CHUNK_SHIFT_EPSILON / 2;
			endPointIntervalPairs.add(new IntervalPair(ip.p, new Interval(
					interval), ip.q, new Interval(ip.qi.b, 1)));
			intersections.add(poi);
		}
	}

	private Point findExtreme(IPointCmp cmp) {
		return findExtreme(cmp, Interval.getFull());
	}

	private Point findExtreme(IPointCmp cmp, Interval iStart) {
		Stack<Interval> parts = new Stack<Interval>();
		parts.push(iStart);

		Point xtreme = getHC(iStart.a).toPoint();

		while (!parts.isEmpty()) {
			Interval i = parts.pop();
			BezierCurve clipped = getClipped(i.a, i.b);

			Point sp = clipped.points[0].toPoint();
			xtreme = cmp.pIsBetterThanQ(sp, xtreme) ? sp : xtreme;
			Point ep = clipped.points[clipped.points.length - 1].toPoint();
			xtreme = cmp.pIsBetterThanQ(ep, xtreme) ? ep : xtreme;

			boolean everythingWorse = true;
			for (int j = 1; j < clipped.points.length - 1; j++) {
				if (!cmp.pIsBetterThanQ(xtreme, clipped.points[j].toPoint())) {
					everythingWorse = false;
					break;
				}
			}

			if (everythingWorse) {
				continue;
			}

			// split interval
			double im = i.getMid();
			parts.push(new Interval(im, i.b));
			parts.push(new Interval(i.a, im));
		}

		return xtreme;
	}

	/**
	 * Find intersection interval chunks. The chunks are not very precise. We
	 * will refine them later.
	 * 
	 * @param ip
	 * @param intervalPairs
	 * @param intersections
	 */
	private void findIntersectionChunks(IntervalPair ip,
			Set<IntervalPair> intervalPairs, Set<Point> intersections) {
		if (ip.converges(CHUNK_SHIFT)) {
			intervalPairs.add(ip.getCopy());
			return;
		}

		BezierCurve pClipped = ip.getPClipped();
		BezierCurve qClipped = ip.getQClipped();

		// construct "parallel" and "orthogonal" fat lines
		FatLine L1 = FatLine.from(qClipped, PARALLEL);
		FatLine L2 = FatLine.from(qClipped, ORTHOGONAL);

		// curve implosion check
		if (L1 == null || L2 == null) {
			// q is degenerated
			Point poi = ip.q.getHC(ip.qi.getMid()).toPoint();
			double[] interval = new double[] { 0, 1 };
			if (poi != null && containmentParameter(ip.p, interval, poi)) {
				intersections.add(poi);
				// intervalPairs.add(new IntervalPair(ip.p,
				// new Interval(interval), ip.q, new Interval(ip.qi
				// .getMid(), ip.qi.getMid())));
			}
			return;
		}

		// clip to the fat lines
		Interval interval = new Interval(pClipped.clipTo(L1));
		Interval intervalOrtho = new Interval(pClipped.clipTo(L2));

		// pick smaller interval range
		interval = Interval.min(interval, intervalOrtho);

		// re-calculate s and e from the clipped interval
		double ratio = ip.pi.scaleTo(interval);

		if (ratio < 0) {
			// no more intersections
			return;
		} else if (ratio > 0.8) {
			/*
			 * Split longer curve and find intersections for both halves. Add an
			 * unrecognizable fraction to the beginning of the second parameter
			 * interval, so that only one of the getIntersection() calls can
			 * converge in the middle.
			 */
			if (ip.isPLonger()) {
				IntervalPair[] nip = ip.getPSplit();
				findIntersectionChunks(nip[0], intervalPairs, intersections);
				findIntersectionChunks(nip[1], intervalPairs, intersections);
			} else {
				IntervalPair[] nip = ip.getQSplit();
				findIntersectionChunks(nip[0], intervalPairs, intersections);
				findIntersectionChunks(nip[1], intervalPairs, intersections);
			}

			return;
		} else {
			findIntersectionChunks(ip.getSwapped(), intervalPairs,
					intersections);
		}
	}

	/**
	 * This routine is only called for an interval that has been detected to
	 * contain a single tangential point of intersection. We do now try to find
	 * it.
	 * 
	 * @param ipIO
	 * @param intervalPairs
	 * @param intersections
	 */
	private Point findSinglePreciseIntersection(IntervalPair ipIO) {
		Stack<IntervalPair> partStack = new Stack<IntervalPair>();
		partStack.push(ipIO);

		while (!partStack.isEmpty()) {
			IntervalPair ip = partStack.pop();

			if (ip.converges()) {
				// TODO: do another clipping algorithm here. the one that
				// uses control bounds.
				for (Point pp : ip.p.toPoints(ip.pi)) {
					for (Point qp : ip.q.toPoints(ip.qi)) {
						if (pp.equals(qp)) {
							copyIntervalPair(ipIO, ip);
							return pp;
						}
					}
				}
				continue;
			}

			BezierCurve pClipped = ip.getPClipped();
			BezierCurve qClipped = ip.getQClipped();

			// construct "parallel" and "orthogonal" fat lines
			FatLine L1 = FatLine.from(qClipped, PARALLEL);
			FatLine L2 = FatLine.from(qClipped, ORTHOGONAL);

			// curve implosion check
			if (L1 == null || L2 == null) {
				// q is degenerated
				Point poi = ip.q.getHC(ip.qi.getMid()).toPoint();
				if (ip.p.contains(poi)) {
					copyIntervalPair(ipIO, ip);
					return poi;
				}
				continue;
			}

			// clip to the fat lines
			Interval interval = new Interval(pClipped.clipTo(L1));
			Interval intervalOrtho = new Interval(pClipped.clipTo(L2));

			// pick smaller interval range
			interval = Interval.min(interval, intervalOrtho);

			// re-calculate s and e from the clipped interval
			double ratio = ip.pi.scaleTo(interval);

			if (ratio < 0) {
				// no more intersections
				continue;
			} else if (ratio > 0.8) {
				/*
				 * Split longer curve and find intersections for both halves.
				 * Add an unrecognizable fraction to the beginning of the second
				 * parameter interval, so that only one of the getIntersection()
				 * calls can converge in the middle.
				 */
				IntervalPair[] nip = ip.isPLonger() ? ip.getPSplit() : ip
						.getQSplit();
				partStack.push(nip[1]);
				partStack.push(nip[0]);
			} else {
				partStack.push(ip.getSwapped());
			}
		}

		return null;
	}

	/**
	 * Generates the difference points of this {@link BezierCurve} to the given
	 * line.
	 * 
	 * The difference points are the control points of a Bezier curve that
	 * yields the signed difference of the point on this curve at a determinate
	 * parameter value to the given line.
	 * 
	 * @param line
	 * @return the difference curve's control points
	 */
	private Vector3D[] genDifferencePoints(Straight3D line) {
		Vector3D[] D = new Vector3D[points.length];
		for (int i = 0; i < points.length; i++) {
			double y = line.getSignedDistanceCW(points[i]);
			D[i] = new Vector3D((double) (i) / (double) (points.length - 1), y,
					1);
		}
		return D;
	}

	/**
	 * Computes the {@link Point} on this {@link BezierCurve} at parameter value
	 * t, which is expected to lie in the range [0;1].
	 * 
	 * @param t
	 *            parameter value
	 * @return the {@link Point} on this {@link BezierCurve} at the given
	 *         parameter value
	 */
	public Point get(double t) {
		return getHC(t).toPoint();
	}

	/**
	 * @see IGeometry#getBounds()
	 * @return the bounds of this {@link BezierCurve}.
	 */
	public Rectangle getBounds() {
		double xmin = findExtreme(xminCmp).x;
		double xmax = findExtreme(xmaxCmp).x;
		double ymin = findExtreme(yminCmp).y;
		double ymax = findExtreme(ymaxCmp).y;

		return new Rectangle(new Point(xmin, ymin), new Point(xmax, ymax));
	}

	/**
	 * Returns a new {@link BezierCurve} object representing this bezier curve
	 * on the interval [s;e].
	 * 
	 * @param s
	 * @param e
	 * @return a new {@link BezierCurve} object representing this bezier curve
	 *         on the interval [s;e]
	 */
	public BezierCurve getClipped(double s, double e) {
		if (s == 1) {
			return new BezierCurve(points[points.length - 1]);
		}
		BezierCurve right = split(s)[1];
		double rightT2 = (e - s) / (1 - s);
		return right.split(rightT2)[0];
	}

	/**
	 * Returns the bounds of the control polygon of this {@link BezierCurve} .
	 * 
	 * @return a {@link Rectangle} representing the bounds of the control
	 *         polygon of this {@link BezierCurve}
	 */
	public Rectangle getControlBounds() {
		Point[] realPoints = getPoints();

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

	public BezierCurve getCopy() {
		return new BezierCurve(points);
	}

	/**
	 * Computes the hodograph (first parametric derivative) of this
	 * {@link BezierCurve}.
	 * 
	 * @return the hodograph (first parametric derivative) of this
	 *         {@link BezierCurve}
	 */
	public BezierCurve getDerivative() {
		Vector3D[] controlPoints = new Vector3D[points.length - 1];

		for (int i = 0; i < controlPoints.length; i++) {
			controlPoints[i] = points[i + 1].getSubtracted(points[i])
					.getScaled(points.length - 1);
			// ignore z coordinate:
			controlPoints[i].z = 1;
		}

		return new BezierCurve(controlPoints);
	}

	/**
	 * Computes a {@link BezierCurve} with a degree of one higher than this
	 * {@link BezierCurve}'s degree but of the same shape.
	 * 
	 * @return a {@link BezierCurve} of the same shape as this
	 *         {@link BezierCurve} but with one more control {@link Point}
	 */
	public BezierCurve getElevated() {
		Point[] p = getPoints();
		Point[] q = new Point[p.length + 1];
		q[0] = p[0];
		q[p.length] = p[p.length - 1];
		for (int i = 1; i < p.length; i++) {
			double c = (double) i / (double) (p.length);
			q[i] = p[i - 1].getScaled(c).getTranslated(p[i].getScaled(1 - c));
		}
		return new BezierCurve(q);
	}

	/**
	 * Returns the {@link Point} at the given parameter value t.
	 * 
	 * @param t
	 *            Parameter value
	 * @return {@link Point} at parameter value t
	 */
	private Vector3D getHC(double t) {
		if (t < 0 || t > 1) {
			throw new IllegalArgumentException("t out of range: " + t);
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
	 * Computes {@link IntervalPair}s which do reflect points of intersection
	 * between this and the given other {@link BezierCurve}. Each
	 * {@link IntervalPair} reflects a single point of intersection.
	 * 
	 * For every {@link IntervalPair} a point of intersection is inserted into
	 * the given {@link Set} of {@link Point}s.
	 * 
	 * If there are infinite {@link Point}s of intersection, i.e. the curves do
	 * overlap, an empty set is returned. (see
	 * {@link BezierCurve#overlaps(BezierCurve)})
	 * 
	 * @param other
	 * @param intersections
	 *            the {@link Point}-{@link Set} where points of intersection are
	 *            inserted
	 * @return for a finite number of intersection {@link Point}s, a {@link Set}
	 *         of {@link IntervalPair}s is returned where every
	 *         {@link IntervalPair} represents a single {@link Point} of
	 *         intersection. For an infinite number of intersection
	 *         {@link Point}s, an empty {@link Set} is returned.
	 */
	public Set<IntervalPair> getIntersectionIntervalPairs(BezierCurve other,
			Set<Point> intersections) {
		Set<IntervalPair> intervalPairs = new HashSet<IntervalPair>();
		Set<IntervalPair> endPointIntervalPairs = new HashSet<IntervalPair>();

		IntervalPair ip = new IntervalPair(this, Interval.getFull(), other,
				Interval.getFull());

		findEndPointIntersections(ip, endPointIntervalPairs, intersections);
		findIntersectionChunks(ip, intervalPairs, intersections);
		normalizeIntervalPairs(intervalPairs.toArray(new IntervalPair[] {}));
		IntervalPair[] clusters = clusterChunks(
				intervalPairs.toArray(new IntervalPair[] {}), 0);

		IntervalPair overlapIntervalPair = extractOverlap(clusters,
				endPointIntervalPairs.toArray(new IntervalPair[] {}));
		BezierCurve overlap = overlapIntervalPair == null ? null
				: overlapIntervalPair.getPClipped();

		Set<IntervalPair> results = new HashSet<IntervalPair>();

		for (IntervalPair epip : endPointIntervalPairs) {
			if (overlapIntervalPair == null
					|| !isNextTo(overlapIntervalPair, epip, CHUNK_SHIFT)) {
				results.add(epip);
			} else {
				for (Iterator<Point> iterator = intersections.iterator(); iterator
						.hasNext();) {
					if (overlap.contains(iterator.next())) {
						iterator.remove();
					}
				}
			}
		}

		outer: for (IntervalPair cluster : clusters) {
			if (overlapIntervalPair != null)
				if (isNextTo(overlapIntervalPair, cluster, CHUNK_SHIFT))
					continue outer;

			for (IntervalPair epip : endPointIntervalPairs)
				if (isNextTo(cluster, epip, CHUNK_SHIFT))
					continue outer;

			// a.t.m. assume for every cluster just a single point of
			// intersection:
			Point poi = findSinglePreciseIntersection(cluster);
			if (poi != null) {
				intersections.add(poi);
				if (cluster.converges()) {
					results.add(cluster.getCopy());
				}
			}
		}

		return results;
	}

	/**
	 * @see BezierCurve#findEndPointIntersections(IntervalPair, Set, Set)
	 * @see BezierCurve#findIntersectionChunks(IntervalPair, Set, Set)
	 * 
	 * @param other
	 * @return the points of intersection of this {@link BezierCurve} and the
	 *         given other {@link BezierCurve}.
	 */
	public Point[] getIntersections(BezierCurve other) {
		Set<Point> intersections = new HashSet<Point>();
		getIntersectionIntervalPairs(other, intersections);
		return intersections.toArray(new Point[] {});
	}

	public final Point[] getIntersections(ICurve curve) {
		Set<Point> intersections = new HashSet<Point>();

		for (BezierCurve c : curve.toBezier()) {
			intersections.addAll(Arrays.asList(getIntersections(c)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Computes the overlap of this {@link BezierCurve} and the given other
	 * {@link BezierCurve}. If no overlap exists, <code>null</code> is returned.
	 * Otherwise, a {@link BezierCurve}, representing the overlap, is returned.
	 * 
	 * An overlap is identified by an infinite number of intersection points.
	 * 
	 * @param other
	 * @return a {@link BezierCurve} representing the overlap of this and the
	 *         given other {@link BezierCurve} if an overlap exists, otherwise
	 *         <code>null</code>.
	 */
	public BezierCurve getOverlap(BezierCurve other) {
		Set<Point> intersections = new HashSet<Point>();
		Set<IntervalPair> intervalPairs = new HashSet<IntervalPair>();
		Set<IntervalPair> endPointIntervalPairs = new HashSet<IntervalPair>();

		IntervalPair ip = new IntervalPair(this, Interval.getFull(), other,
				Interval.getFull());

		findEndPointIntersections(ip, endPointIntervalPairs, intersections);
		findIntersectionChunks(ip, intervalPairs, intersections);
		IntervalPair[] intervalPairs2 = intervalPairs
				.toArray(new IntervalPair[] {});
		normalizeIntervalPairs(intervalPairs2);
		IntervalPair[] clusters = clusterChunks(intervalPairs2, 0);

		IntervalPair overlap = extractOverlap(clusters,
				endPointIntervalPairs.toArray(new IntervalPair[] {}));
		return overlap == null ? null : overlap.getPClipped();
	}

	public Point getP1() {
		return points[0].toPoint();
	}

	public Point getP2() {
		return points[points.length - 1].toPoint();
	}

	/**
	 * @param p
	 * @return -1 if p not on curve, otherwise the corresponding parameter
	 *         value.
	 */
	public double getParameterAt(Point p) {
		if (p == null) {
			// return -1;
			throw new NullPointerException("Point may not be null.");
		}

		double[] interval = new double[] { 0, 1 };
		if (containmentParameter(this, interval, p)) {
			return (interval[0] + interval[1]) / 2;
		} else {
			// return -1;
			throw new IllegalArgumentException(
					"The given point does not lie on the curve.");
		}
	}

	/**
	 * Returns the {@link Point} at index i in the points array of this
	 * {@link BezierCurve}. The start {@link Point} is at index 0, the first
	 * handle-{@link Point} is at index 1, etc.
	 * 
	 * @param i
	 *            the index of a {@link Point} in the points array of this
	 *            {@link BezierCurve}
	 * @return the {@link Point} at the given index in the points array of this
	 *         {@link BezierCurve}
	 */
	public Point getPoint(int i) {
		if (i < 0 || i >= points.length) {
			throw new IllegalArgumentException(
					"getPoint("
							+ i
							+ "): You can only index this BezierCurve's points from 0 to "
							+ (points.length - 1) + ".");
		}
		return points[i].toPoint();
	}

	/**
	 * Computes the real planar {@link Point}s for this {@link BezierCurve}.
	 * 
	 * @return the real planar {@link Point}s for this {@link BezierCurve}
	 */
	public Point[] getPoints() {
		Point[] realPoints = new Point[points.length];
		for (int i = 0; i < points.length; i++) {
			realPoints[i] = points[i].toPoint();
		}
		return realPoints;
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

	public BezierCurve getRotatedCCW(Angle angle) {
		return getCopy().rotateCCW(angle);
	}

	public BezierCurve getRotatedCCW(Angle angle, double cx, double cy) {
		return getCopy().rotateCCW(angle, cx, cy);
	}

	public BezierCurve getRotatedCCW(Angle angle, Point center) {
		return getCopy().rotateCCW(angle, center);
	}

	public BezierCurve getRotatedCW(Angle angle) {
		return getCopy().rotateCW(angle);
	}

	public BezierCurve getRotatedCW(Angle angle, double cx, double cy) {
		return getCopy().rotateCW(angle, cx, cy);
	}

	public BezierCurve getRotatedCW(Angle angle, Point center) {
		return getCopy().rotateCW(angle, center);
	}

	public BezierCurve getScaled(double factor) {
		return getCopy().getScaled(factor);
	}

	public BezierCurve getScaled(double fx, double fy) {
		return getCopy().getScaled(fx, fy);
	}

	public BezierCurve getScaled(double factor, double cx, double cy) {
		return getCopy().getScaled(factor, cx, cy);
	}

	public BezierCurve getScaled(double fx, double fy, double cx, double cy) {
		return getCopy().getScaled(fx, fy, cx, cy);
	}

	public BezierCurve getScaled(double fx, double fy, Point center) {
		return getCopy().getScaled(fx, fy, center);
	}

	public BezierCurve getScaled(double factor, Point center) {
		return getCopy().getScaled(factor, center);
	}

	public BezierCurve getTranslated(double dx, double dy) {
		return getCopy().translate(dx, dy);
	}

	public BezierCurve getTranslated(Point d) {
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
		return getIntersections(c).length > 0;
	}

	/**
	 * Moves the interval's start and end values. The start value is set to x if
	 * x is smaller than the start value. The end value is set to x if x is
	 * greater than the end value.
	 * 
	 * @param interval
	 *            The current interval
	 * @param x
	 */
	private void moveInterval(double[] interval, double x) {
		if (interval[0] > x) {
			interval[0] = x;
		}
		if (interval[1] < x) {
			interval[1] = x;
		}
	}

	/**
	 * Returns <code>true</code> if this and the given other {@link BezierCurve}
	 * do overlap. Otherwise, returns <code>false</cdoe>.
	 * 
	 * @param other
	 * @return <code>true</code> if this and the given other {@link BezierCurve}
	 *         overlap, otherwise <code>false</code>
	 */
	public boolean overlaps(BezierCurve other) {
		return getOverlap(other) != null;
	}

	public final boolean overlaps(ICurve c) {
		for (BezierCurve seg : c.toBezier()) {
			if (overlaps(seg)) {
				return true;
			}
		}
		return false;
	}

	public BezierCurve rotateCCW(Angle angle) {
		Point centroid = PointListUtils.computeCentroid(getPoints());
		return rotateCCW(angle, centroid.x, centroid.y);
	}

	public BezierCurve rotateCCW(Angle angle, double cx, double cy) {
		Point[] realPoints = getPoints();
		PointListUtils.rotateCCW(realPoints, angle, cx, cy);
		for (int i = 0; i < realPoints.length; i++) {
			setPoint(i, realPoints[i]);
		}
		return this;
	}

	public BezierCurve rotateCCW(Angle alpha, Point center) {
		for (int i = 0; i < points.length; i++) {
			points[i] = new Vector3D(new Vector(points[i].toPoint()
					.getTranslated(center.getNegated())).getRotatedCCW(alpha)
					.toPoint().getTranslated(center));
		}
		return this;
	}

	public BezierCurve rotateCW(Angle angle) {
		Point centroid = PointListUtils.computeCentroid(getPoints());
		return rotateCW(angle, centroid.x, centroid.y);
	}

	public BezierCurve rotateCW(Angle angle, double cx, double cy) {
		Point[] realPoints = getPoints();
		PointListUtils.rotateCW(realPoints, angle, cx, cy);
		for (int i = 0; i < realPoints.length; i++) {
			setPoint(i, realPoints[i]);
		}
		return this;
	}

	public BezierCurve rotateCW(Angle angle, Point center) {
		return rotateCW(angle, center.x, center.y);
	}

	public BezierCurve scale(double factor) {
		return scale(factor, factor);
	}

	public BezierCurve scale(double fx, double fy) {
		Point centroid = PointListUtils.computeCentroid(getPoints());
		return scale(fx, fy, centroid.x, centroid.y);
	}

	public BezierCurve scale(double factor, double cx, double cy) {
		return scale(factor, factor, cx, cy);
	}

	public BezierCurve scale(double fx, double fy, double cx, double cy) {
		Point[] realPoints = getPoints();
		PointListUtils.scale(realPoints, fx, fy, cx, cy);
		for (int i = 0; i < realPoints.length; i++) {
			setPoint(i, realPoints[i]);
		}
		return this;
	}

	public BezierCurve scale(double fx, double fy, Point center) {
		return scale(fx, fy, center.x, center.y);
	}

	public BezierCurve scale(double factor, Point center) {
		return scale(factor, factor, center.x, center.y);
	}

	/**
	 * Sets the start {@link Point} of this {@link BezierCurve} to the given
	 * {@link Point}.
	 * 
	 * @param p1
	 *            the new start {@link Point} of this {@link BezierCurve}
	 */
	public void setP1(Point p1) {
		setPoint(0, p1);
	}

	/**
	 * Sets the end {@link Point} of this {@link BezierCurve} to the given
	 * {@link Point}.
	 * 
	 * @param p2
	 *            the new end {@link Point} of this {@link BezierCurve}
	 */
	public void setP2(Point p2) {
		setPoint(points.length - 1, p2);
	}

	/**
	 * Sets the {@link Point} at index i in this {@link BezierCurve}'s
	 * {@link Point}s array to the given {@link Point}. The start {@link Point}
	 * 's index is 0. The index of the first handle {@link Point} is 1, etc.
	 * 
	 * @param i
	 *            the index in this {@link BezierCurve}'s {@link Point}s array
	 * @param p
	 *            the new {@link Point} for the given index
	 */
	public void setPoint(int i, Point p) {
		if (i < 0 || i >= points.length) {
			throw new IllegalArgumentException(
					"setPoint("
							+ i
							+ ", "
							+ p
							+ "): You can only index this BezierCurve's points from 0 to "
							+ (points.length - 1) + ".");
		}
		points[i] = new Vector3D(p);
	}

	/**
	 * Subdivides this {@link BezierCurve} at the given parameter value t into
	 * two new {@link BezierCurve}s. The first one is the curve over [0;t] and
	 * the second one is the curve over [t;1].
	 * 
	 * NOTE: One could provide two methods splitLeft() and splitRight() in case
	 * just the left or right part of the curve is needed.
	 * 
	 * @param t
	 *            Parameter value
	 * @return Two curves; to the left and to the right of t.
	 */
	public BezierCurve[] split(double t) {
		Vector3D[] leftPoints = new Vector3D[points.length];
		Vector3D[] rightPoints = new Vector3D[points.length];

		Vector3D[] ratioPoints = getPointsCopy();

		for (int i = 0; i < points.length; i++) {
			leftPoints[i] = ratioPoints[0];
			rightPoints[points.length - 1 - i] = ratioPoints[points.length - 1
					- i];

			for (int j = 0; j < points.length - i - 1; j++) {
				ratioPoints[j] = ratioPoints[j].getRatio(ratioPoints[j + 1], t);
			}
		}

		return new BezierCurve[] { new BezierCurve(leftPoints),
				new BezierCurve(rightPoints) };
	}

	public BezierCurve[] toBezier() {
		return new BezierCurve[] { this };
	}

	/**
	 * Returns a hard approximation of this {@link BezierCurve} as a
	 * {@link CubicCurve}. The new {@link CubicCurve} is constructed from the
	 * first three {@link Point}s in this {@link BezierCurve}'s {@link Point}s
	 * array and the end {@link Point} of this {@link BezierCurve}. If this
	 * {@link BezierCurve} is not of degree four or higher, i.e. it does not
	 * have four or more control {@link Point}s (including start and end
	 * {@link Point}), <code>null</code> is returned.
	 * 
	 * @return a new {@link CubicCurve} that is constructed by the first three
	 *         {@link Point}s and the end {@link Point} of this
	 *         {@link BezierCurve} or <code>null</code> if this
	 *         {@link BezierCurve} does not have at least four control
	 *         {@link Point}s
	 */
	public CubicCurve toCubic() {
		if (points.length > 3) {
			return new CubicCurve(points[0].toPoint(), points[1].toPoint(),
					points[2].toPoint(), points[points.length - 1].toPoint());
		}
		return null;
	}

	/**
	 * Returns a hard approximation of this {@link BezierCurve} as a
	 * {@link Line}. The {@link Line} is constructed from the start and end
	 * {@link Point} of this {@link BezierCurve}.
	 * 
	 * Sometimes, this {@link Line} is referred to as the base-line of the
	 * {@link BezierCurve}.
	 * 
	 * @return a {@link Line} from start to end {@link Point} of this
	 *         {@link BezierCurve}
	 */
	public Line toLine() {
		if (points.length > 1) {
			return new Line(points[0].toPoint(),
					points[points.length - 1].toPoint());
		}
		return null;
	}

	/**
	 * Returns an approximation of this {@link BezierCurve} by a strip of
	 * {@link Line}s. For detailed information on how the approximation is
	 * calculated, see {@link BezierCurve#toLineStrip(double, Interval)}.
	 * 
	 * @see BezierCurve#toLineStrip(double, Interval)
	 * @param lineSimilarity
	 * @return an approximation of this {@link BezierCurve} by a strip of
	 *         {@link Line}s
	 */
	public Line[] toLineStrip(double lineSimilarity) {
		return toLineStrip(lineSimilarity, Interval.getFull());
	}

	/**
	 * Returns {@link Line} segments approximating this {@link BezierCurve}.
	 * 
	 * The {@link BezierCurve} is recursively subdivided until it is "similar"
	 * to a straight line. The similarity check computes the sum of the
	 * distances of the control points to the baseline of the
	 * {@link BezierCurve}. If this sum is smaller than the given
	 * lineSimilarity, the {@link BezierCurve} is assumed to be "similar" to a
	 * straight line.
	 * 
	 * @param lineSimilarity
	 *            The threshold for the sum of the distances of the control
	 *            points to the baseline of this {@link BezierCurve}
	 * @param startInterval
	 *            The interval of the curve that has to be transformed into a
	 *            strip of {@link Line}s.
	 * @return {@link Line} segments approximating this {@link BezierCurve}.
	 */
	public Line[] toLineStrip(double lineSimilarity, Interval startInterval) {
		ArrayList<Line> lines = new ArrayList<Line>();

		Point startPoint = getHC(startInterval.a).toPoint();

		// System.out.println("BEZIER CURVE - LINE APPROXIMATION");
		// System.out.println("---------------------------------");
		// System.out.println("moveTo(" + startPoint.x + ", " + startPoint.y
		// + ")");

		Stack<Interval> parts = new Stack<Interval>();
		parts.push(startInterval);

		while (!parts.isEmpty()) {
			// System.out.println("pop");
			Interval i = parts.pop();
			BezierCurve part = getClipped(i.a, i.b);

			if (distanceToBaseLine(part) < lineSimilarity) {
				Point endPoint = getHC(i.b).toPoint();
				lines.add(new Line(startPoint, endPoint));
				startPoint = endPoint;
				// System.out.println("lineTo(" + endPoint.x + ", "
				// + endPoint.y + ")");
			} else {
				// System.out.println("push'em");
				double im = i.getMid();
				parts.push(new Interval(im, i.b));
				parts.push(new Interval(i.a, im));
			}
		}

		return lines.toArray(new Line[] {});
	}

	/**
	 * Returns a {@link Path} approximating this {@link BezierCurve} using
	 * {@link Line} segments.
	 * 
	 * @return a {@link Path} approximating this {@link BezierCurve} using
	 *         {@link Line} segments.
	 */
	public Path toPath() {
		Path path = new Path();

		Point startPoint = points[0].toPoint();
		path.moveTo(startPoint.x, startPoint.y);

		for (Line seg : toLineStrip(0.25d)) {
			path.lineTo(seg.getX2(), seg.getY2());
		}

		return path;
	}

	/**
	 * Computes {@link Point}s on this {@link BezierCurve} over the given
	 * {@link Interval}. Consecutive returned {@link Point}s are required to be
	 * {@link Point#equals(Object)} to each other.
	 * 
	 * @param startInterval
	 *            the {@link Interval} of this {@link BezierCurve} to calculate
	 *            {@link Point}s for
	 * @return {@link Point}s on this {@link BezierCurve} over the given
	 *         parameter {@link Interval} where consecutive {@link Point}s are
	 *         {@link Point#equals(Object)} to each other
	 */
	public Point[] toPoints(Interval startInterval) {
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(getHC(startInterval.a).toPoint());

		// System.out.println("BEZIER CURVE - LINE APPROXIMATION");
		// System.out.println("---------------------------------");
		// System.out.println("moveTo(" + startPoint.x + ", " + startPoint.y
		// + ")");

		Stack<Interval> parts = new Stack<Interval>();
		parts.push(startInterval);

		while (!parts.isEmpty()) {
			// System.out.println("pop");
			Interval i = parts.pop();
			BezierCurve part = getClipped(i.a, i.b);

			Point[] partPoints = part.getPoints();

			boolean allTogether = true;
			for (int j = 1; j < partPoints.length; j++) {
				if (!partPoints[0].equals(partPoints[j])) {
					allTogether = false;
					break;
				}
			}

			if (allTogether) {
				points.add(partPoints[partPoints.length - 1]);
			} else {
				double im = i.getMid();
				parts.push(new Interval(im, i.b));
				parts.push(new Interval(i.a, im));
			}
		}

		return points.toArray(new Point[] {});
	}

	/**
	 * Returns a hard approximation of this {@link BezierCurve} as a
	 * {@link QuadraticCurve}. The new {@link QuadraticCurve} is constructed
	 * from the first two {@link Point}s in this {@link BezierCurve}'s
	 * {@link Point}s array and the end {@link Point} of this
	 * {@link BezierCurve}. If this {@link BezierCurve} is not of degree three
	 * or higher, i.e. it does not have three or more control {@link Point}s
	 * (including start and end {@link Point}), <code>null</code> is returned.
	 * 
	 * @return a new {@link QuadraticCurve} that is constructed by the first two
	 *         {@link Point}s and the end {@link Point} of this
	 *         {@link BezierCurve} or <code>null</code> if this
	 *         {@link BezierCurve} does not have at least three control
	 *         {@link Point}s
	 */
	public QuadraticCurve toQuadratic() {
		if (points.length > 2) {
			return new QuadraticCurve(points[0].toPoint(), points[1].toPoint(),
					points[points.length - 1].toPoint());
		}
		return null;
	}

	public BezierCurve translate(double dx, double dy) {
		Point[] realPoints = getPoints();
		PointListUtils.translate(realPoints, dx, dy);
		for (int i = 0; i < realPoints.length; i++) {
			setPoint(i, realPoints[i]);
		}
		return this;
	}

	public BezierCurve translate(Point d) {
		return translate(d.x, d.y);
	}

}

/*******************************************************************************
 * Copyright (c) 2011, 2017 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.planar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.euclidean.Straight;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.internal.utils.PointListUtils;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef.geometry.projective.Straight3D;
import org.eclipse.gef.geometry.projective.Vector3D;

/**
 * <p>
 * Instances of the {@link BezierCurve} class individually represent an
 * arbitrary Bezier curve. This is the base class of the special quadratic and
 * cubic Bezier curve classes ({@link QuadraticCurve} and {@link CubicCurve}).
 * </p>
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class BezierCurve extends AbstractGeometry
		implements ICurve, ITranslatable<BezierCurve>, IScalable<BezierCurve>,
		IRotatable<BezierCurve> {

	private static class CuspAwareOffsetApproximator {

		private static class Cusp extends PartialCurve {
			public Cusp(BezierCurve c, double t0, double t1) {
				super(c, t0, t1);
			}
		}

		private static interface ICurveSimplifier {
			public List<PartialCurve> simplify(BezierCurve curve);
		}

		private static interface ICuspSplitter {
			public List<PartialCurve> splitAtCusps(BezierCurve curve);
		}

		private static interface IOffsetAlgorithm {
			public static class PartialOffset {
				public BezierCurve offset;
				public double curveStart;
				public double curveEnd;

				public PartialOffset(BezierCurve offset, double t0, double t1) {
					curveStart = t0;
					curveEnd = t1;
					this.offset = offset;
				}

				public PartialOffset(PartialCurve pc, BezierCurve offset) {
					this(offset, pc.start, pc.end);
				}
			}

			public List<PartialOffset> computeOffset(BezierCurve curve,
					double distance);
		}

		private static class LasserCurveSimplifier implements ICurveSimplifier {
			private static final int DEFAULT_MAX_DEPTH = 16;

			private int maxDepth;

			public LasserCurveSimplifier() {
				this(DEFAULT_MAX_DEPTH);
			}

			public LasserCurveSimplifier(int maxDepth) {
				this.maxDepth = maxDepth;
			}

			private double computeAngleSum(BezierCurve curve) {
				double angleSum = 0d;
				Point[] points = curve.getPoints();
				for (int i = 0; i < points.length - 2; i++) {
					Vector first = new Vector(points[i], points[i + 1]);
					Vector second = new Vector(points[i + 1], points[i + 2]);
					if (first.getLength() * second.getLength() > 0) {
						Angle angle = first.getAngle(second);
						angleSum += angle.rad();
					}
				}
				return angleSum;
			}

			private List<PartialCurve> computeLasserWithParams(
					PartialCurve partialCurve, int currentDepth) {
				BezierCurve curve = partialCurve.curve
						.getClipped(partialCurve.start, partialCurve.end);
				double angleSum = computeAngleSum(curve);
				List<PartialCurve> spline = new ArrayList<>();
				if (currentDepth < maxDepth && angleSum > Math.PI) {
					PartialCurve[] split = partialCurve.split();
					List<PartialCurve> left = computeLasserWithParams(split[0],
							currentDepth + 1);
					List<PartialCurve> right = computeLasserWithParams(split[1],
							currentDepth + 1);
					spline.addAll(left);
					spline.addAll(right);
				} else {
					spline.add(partialCurve);
				}
				return spline;
			}

			@Override
			public List<PartialCurve> simplify(BezierCurve curve) {
				return computeLasserWithParams(new PartialCurve(curve, 0, 1),
						0);
			}
		}

		private static class PartialCurve {
			public BezierCurve curve;
			public double start;
			public double end;

			public PartialCurve(BezierCurve c, double t0, double t1) {
				curve = c;
				start = t0;
				end = t1;
			}

			public PartialCurve[] split() {
				double mid = start + 0.5 * (end - start);
				return new PartialCurve[] { new PartialCurve(curve, start, mid),
						new PartialCurve(curve, mid, end) };
			}
		}

		private static class SamplingCuspSplitter implements ICuspSplitter {
			private static final int DEFAULT_MAX_DEPTH = 4;
			private static final int DEFAULT_SAMPLE_COUNT = 128;
			private static final double DEFAULT_MIN_ANGLE_RAD = Angle
					.fromDeg(10).rad();

			private int sampleCount;
			private double minAngleRad;
			private int maxDepth;
			private BezierCurve curve;

			public SamplingCuspSplitter() {
				this(DEFAULT_SAMPLE_COUNT, DEFAULT_MIN_ANGLE_RAD,
						DEFAULT_MAX_DEPTH);
			}

			public SamplingCuspSplitter(int sampleCount, double minAngle,
					int maxDepth) {
				if (sampleCount < 2) {
					throw new IllegalArgumentException("sampleCount < 2");
				}
				this.sampleCount = sampleCount;
				this.minAngleRad = minAngle;
				this.maxDepth = maxDepth;
			}

			private List<Cusp> getCusps() {
				List<Cusp> cusps = new ArrayList<>();
				BezierCurve hodograph = curve.getDerivative();
				Point lastDirection = null;
				double lastT = 0;
				for (int i = 0; i < sampleCount; i++) {
					double t = i / (double) (sampleCount - 1);
					Point direction = hodograph.get(t);
					if (lastDirection != null && !direction.equals(0, 0)) {
						Angle angle = new Vector(direction)
								.getAngle(new Vector(lastDirection));
						if (angle.rad() > minAngleRad) {
							cusps.add(refineCusp(lastT, t, 0));
						}
					}
					if (!direction.equals(0, 0)) {
						lastDirection = direction;
						lastT = t;
					}
				}
				if (cusps.size() > 1) {
					// filter out same cusps
					Cusp lastCusp = cusps.get(cusps.size() - 1);
					for (int i = cusps.size() - 2; i >= 0; i--) {
						Cusp c = cusps.get(i);
						if (curve.get(c.start)
								.getDistance(curve.get(lastCusp.start)) < 1) {
							// advance cusp parameters
							if (lastCusp.start > c.start) {
								lastCusp.start = c.start;
							}
							if (lastCusp.end < c.end) {
								lastCusp.end = c.end;
							}
							// remove same cusp
							cusps.remove(i);
						} else {
							lastCusp = c;
						}
					}
				}
				return cusps;
			}

			private Cusp refineCusp(double t0, double t1, int depth) {
				// do not refine further if the cusp is already precise
				Point pa = curve.get(t0);
				Point pb = curve.get(t1);
				if (pa.getDistance(pb) < 0.2) {
					return new Cusp(curve, t0, t1);
				}
				BezierCurve hodograph = curve.getDerivative();
				Double maxRad = null;
				Point lastDirection = null;
				double lastT = 0;
				double maxA = t0, maxB = t1;
				for (int i = 0; i < sampleCount; i++) {
					double t = t0 + (t1 - t0) * i / (sampleCount - 1);
					Point direction = hodograph.get(t);
					if (lastDirection != null && !direction.equals(0, 0)) {
						Angle angle = new Vector(direction)
								.getAngle(new Vector(lastDirection));
						if (maxRad == null || angle.rad() > maxRad) {
							maxRad = angle.rad();
							maxA = lastT;
							maxB = t;
						}
					}
					if (!direction.equals(0, 0)) {
						lastDirection = direction;
						lastT = t;
					}
				}
				if (depth < maxDepth) {
					return refineCusp(maxA, maxB, depth + 1);
				} else {
					return new Cusp(curve, maxA, maxB);
				}
			}

			@Override
			public List<PartialCurve> splitAtCusps(BezierCurve curve) {
				this.curve = curve;
				List<Cusp> cusps = getCusps();
				List<PartialCurve> cc = new ArrayList<>();
				if (cusps.isEmpty()) {
					cc.add(new PartialCurve(curve, 0, 1));
					return cc;
				}
				// add initial curve
				cc.add(new PartialCurve(curve.split(cusps.get(0).start)[0], 0,
						1));
				// and initial cusp
				cc.add(cusps.get(0));
				for (int i = 1; i < cusps.size(); i++) {
					// add curve from previous end to current start
					cc.add(new PartialCurve(curve.getClipped(
							cusps.get(i - 1).end, cusps.get(i).start), 0, 1));
					// add cusp
					cc.add(cusps.get(i));
				}
				// add final curve
				cc.add(new PartialCurve(
						curve.split(cusps.get(cusps.size() - 1).end)[1], 0, 1));
				return cc;
			}
		}

		public static class TillerHansonOffsetAlgorithm
				implements IOffsetAlgorithm {
			private static class ControlLeg {
				public ControlVertex start;
				public ControlVertex end;

				public ControlLeg(ControlVertex start, ControlVertex end) {
					this.start = new ControlVertex(start.position,
							start.multiplicity);
					this.end = new ControlVertex(end.position,
							end.multiplicity);
				}
			}

			private static class ControlVertex {
				public Point position;
				public int multiplicity;

				public ControlVertex(Point pos) {
					this.position = pos.getCopy();
					this.multiplicity = 1;
				}

				public ControlVertex(Point pos, int mult) {
					this.position = pos.getCopy();
					this.multiplicity = mult;
				}
			}

			private static final double DEFAULT_ACCEPTABLE_ERROR = 0.1;
			private static final int DEFAULT_MAX_DEPTH = 32;

			private double acceptableError;
			private int maxDepth;
			private double distance;

			public TillerHansonOffsetAlgorithm() {
				this(DEFAULT_ACCEPTABLE_ERROR, DEFAULT_MAX_DEPTH);
			}

			public TillerHansonOffsetAlgorithm(double acceptableError,
					int maxDepth) {
				this.acceptableError = acceptableError;
				this.maxDepth = maxDepth;
			}

			private BezierCurve approximateOffset(BezierCurve curve) {
				// collect ControlVertex objects for all unique subsequent
				// points
				// of the curve
				Point[] curvePoints = curve.getPoints();
				List<ControlVertex> curveVertices = new ArrayList<>();
				curveVertices.add(new ControlVertex(curvePoints[0]));
				for (int i = 1; i < curvePoints.length; i++) {
					Point p = curvePoints[i];
					ControlVertex lastVertex = curveVertices
							.get(curveVertices.size() - 1);
					if (lastVertex.position.equals(p)) {
						lastVertex.multiplicity++;
					} else {
						curveVertices.add(new ControlVertex(p));
					}
				}

				// we need at least two vertices to be able to approximate an
				// offset
				if (curveVertices.size() < 2) {
					return curve.getCopy();
				}

				// build ControlLeg objects for the ControlVertex objects
				List<ControlLeg> legs = new ArrayList<>();
				for (int i = 0; i < curveVertices.size() - 1; i++) {
					ControlVertex start = curveVertices.get(i);
					ControlVertex end = curveVertices.get(i + 1);
					legs.add(new ControlLeg(start, end));
				}

				// compute offset control legs
				List<ControlLeg> offsetLegs = new ArrayList<>();
				for (ControlLeg leg : legs) {
					Vector direction = new Vector(leg.start.position,
							leg.end.position);
					if (direction.isNull()) {
						// should not be possible since we eliminated all
						// subsequent
						// equal points using ControlVertex
						throw new IllegalStateException(
								"[ERROR] Leg direction cannot be computed because start and end position are the same.");
					} else {
						Point translation = direction.getOrthogonalComplement()
								.getNormalized().getMultiplied(distance)
								.toPoint();
						ControlVertex offsetStart = new ControlVertex(
								leg.start.position.getTranslated(translation));
						ControlVertex offsetEnd = new ControlVertex(
								leg.end.position.getTranslated(translation));
						offsetLegs.add(new ControlLeg(offsetStart, offsetEnd));
					}
				}

				// compute intersections between offset ControlLeg objects
				List<ControlVertex> offsetVertices = new ArrayList<>();
				offsetVertices.add(offsetLegs.get(0).start);
				for (int i = 1; i < offsetLegs.size(); i++) {
					// find intersection with previous leg
					ControlLeg previousLeg = offsetLegs.get(i - 1);
					ControlLeg currentLeg = offsetLegs.get(i);
					Straight s1 = new Straight(previousLeg.start.position,
							previousLeg.end.position);
					Straight s2 = new Straight(currentLeg.start.position,
							currentLeg.end.position);
					Vector intersection = s1.getIntersection(s2);
					if (intersection == null) {
						// use mid of legs' endpoints as the intersection
						Point p1 = previousLeg.end.position;
						Point p2 = currentLeg.start.position;
						Point mid = new Point(p1.x + p2.x, p1.y + p2.y)
								.getScaled(0.5);
						intersection = new Vector(mid);
					}
					offsetVertices.add(new ControlVertex(intersection.toPoint(),
							currentLeg.start.multiplicity));
				}
				offsetVertices.add(offsetLegs.get(offsetLegs.size() - 1).end);

				// collect offset points, respecting multiplicity of vertices
				List<Point> offsetPoints = new ArrayList<>();
				for (ControlVertex vertex : offsetVertices) {
					for (int i = 0; i < vertex.multiplicity; i++) {
						offsetPoints.add(vertex.position.getCopy());
					}
				}

				// construct bezier curve from approx offset points
				return new BezierCurve(offsetPoints.toArray(new Point[0]));
			}

			@Override
			public List<PartialOffset> computeOffset(BezierCurve curve,
					double distance) {
				this.distance = distance;
				return computeTillerHansonWithParams(
						new PartialCurve(curve, 0, 1), 0);
			}

			private double computeOffsetError(BezierCurve curve,
					BezierCurve hodograph, BezierCurve approx) {
				Double error = null;
				int N = curve.getPoints().length * 4;
				for (int i = 0; i < N; i++) {
					double t = i / (double) (N - 1);
					// evaluate offset
					Point position = curve.get(t);
					Point derivative = hodograph.get(t);
					Vector tangent = new Vector(derivative);
					if (tangent.getLength() > 0) {
						Point direction = tangent.getNormalized()
								.getOrthogonalComplement()
								.getMultiplied(distance).toPoint();
						Point offset = position.getTranslated(direction);
						double delta = approx.get(t).getDistance(offset);
						if (error == null || delta > error) {
							error = delta;
						}
					}
				}
				return error == null ? -1 : error.doubleValue();
			}

			private List<PartialOffset> computeTillerHansonWithParams(
					PartialCurve partialCurve, int currentDepth) {
				BezierCurve curve = partialCurve.curve
						.getClipped(partialCurve.start, partialCurve.end);
				BezierCurve approx = approximateOffset(curve);
				double error = computeOffsetError(curve, curve.getDerivative(),
						approx);
				List<PartialOffset> sapprox = new ArrayList<>();
				if (currentDepth < maxDepth && error >= acceptableError) {
					PartialCurve[] s = partialCurve.split();
					List<PartialOffset> l = computeTillerHansonWithParams(s[0],
							currentDepth + 1);
					List<PartialOffset> r = computeTillerHansonWithParams(s[1],
							currentDepth + 1);
					sapprox.addAll(l);
					sapprox.addAll(r);
				} else if (error >= 0) {
					sapprox.add(new PartialOffset(partialCurve, approx));
				}
				return sapprox;
			}
		}

		private ICurveSimplifier curveSimplifier;
		private IOffsetAlgorithm offsetAlgorithm;
		private ICuspSplitter cuspSplitter;

		public CuspAwareOffsetApproximator() {
			this(new LasserCurveSimplifier(), new TillerHansonOffsetAlgorithm(),
					new SamplingCuspSplitter());
		}

		public CuspAwareOffsetApproximator(ICurveSimplifier curveSimplifier,
				IOffsetAlgorithm offsetAlgorithm, ICuspSplitter cuspSplitter) {
			this.curveSimplifier = curveSimplifier;
			this.offsetAlgorithm = offsetAlgorithm;
			this.cuspSplitter = cuspSplitter;
		}

		public OffsetApproximation approximateOffset(BezierCurve curve,
				double distance) {
			List<BezierCurve> simpleCurve = new ArrayList<>();
			List<BezierCurve> approxOffsetCurve = new ArrayList<>();
			Map<Integer, Integer> approx2simple = new HashMap<>();
			Map<Integer, Double> a2sParamStart = new HashMap<>();
			Map<Integer, Double> a2sParamEnd = new HashMap<>();

			List<PartialCurve> cuspsExtracted = cuspSplitter
					.splitAtCusps(curve);
			for (PartialCurve cc : cuspsExtracted) {
				if (!(cc instanceof Cusp)) {
					// remove self intersections
					List<PartialCurve> simplified = curveSimplifier
							.simplify(cc.curve);
					List<BezierCurve> simplifiedCurves = new ArrayList<>(
							simplified.size());
					for (PartialCurve pc : simplified) {
						simplifiedCurves
								.add(pc.curve.getClipped(pc.start, pc.end));
					}
					int simpleSize = simpleCurve.size();
					simpleCurve.addAll(simplifiedCurves);
					for (int j = 0; j < simplifiedCurves.size(); j++) {
						BezierCurve simple = simplifiedCurves.get(j);
						List<IOffsetAlgorithm.PartialOffset> parts = offsetAlgorithm
								.computeOffset(simple, distance);
						for (IOffsetAlgorithm.PartialOffset part : parts) {
							List<PartialCurve> splitApprox = curveSimplifier
									.simplify(part.offset);
							int approxSize = approxOffsetCurve.size();
							for (PartialCurve pc : splitApprox) {
								approxOffsetCurve.add(
										pc.curve.getClipped(pc.start, pc.end));
							}
							for (int i = 0; i < splitApprox.size(); i++) {
								approx2simple.put(approxSize + i,
										simpleSize + j);
								PartialCurve pca = splitApprox.get(i);
								double width = part.curveEnd - part.curveStart;
								double c0 = part.curveStart + pca.start * width;
								double c1 = part.curveStart + pca.end * width;
								a2sParamStart.put(approxSize + i, c0);
								a2sParamEnd.put(approxSize + i, c1);
							}
						}
					}
				} else {
					// the point of the arc serves as the center of the arc
					Point center = curve.get(cc.start / 2 + cc.end / 2);

					// compute start and end normals
					Point startDirection = curve.getDerivative().get(cc.start);
					while (startDirection.equals(0, 0) && cc.start > 0) {
						cc.start -= 0.0001;
						if (cc.start < 0) {
							cc.start = 0;
						}
						startDirection = curve.getDerivative().get(cc.start);
					}
					Point endDirection = curve.getDerivative().get(cc.end);
					while (endDirection.equals(0, 0) && cc.end < 1) {
						cc.end += 0.0001;
						if (cc.end > 1) {
							cc.end = 1;
						}
						endDirection = curve.getDerivative().get(cc.end);
					}
					if (startDirection.equals(0, 0)) {
						startDirection.setLocation(endDirection);
					}
					if (endDirection.equals(0, 0)) {
						endDirection.setLocation(startDirection);
					}
					if (startDirection.equals(0, 0)
							|| endDirection.equals(0, 0)) {
						System.out.println("ERROR");
						Point baselineDirection = new Vector(cc.curve.get(0),
								cc.curve.get(1)).toPoint();
						startDirection.setLocation(baselineDirection);
						endDirection.setLocation(baselineDirection);
					}

					Vector startNormal = new Vector(startDirection)
							.getNormalized().getOrthogonalComplement();
					Vector endNormal = new Vector(endDirection).getNormalized()
							.getOrthogonalComplement();

					// compute angle between start and end normals
					Angle angleCCW = startNormal.getAngleCCW(endNormal);
					Angle angleCW = startNormal.getAngleCW(endNormal);

					// compute start start angle and length angle for the
					// arc
					Angle arcStartAngle;
					Angle arcLengthAngle;
					if (angleCCW.rad() < angleCW.rad()) {
						arcStartAngle = new Vector(1, 0)
								.getAngleCCW(startNormal);
						arcLengthAngle = angleCCW;
					} else {
						arcStartAngle = new Vector(1, 0).getAngleCCW(endNormal);
						arcLengthAngle = angleCW;
					}

					// compute arc approximation
					double absDistance = Math.abs(distance);
					PolyBezier arc = new Arc(center.x - absDistance,
							center.y - absDistance, 2 * absDistance,
							2 * absDistance, arcStartAngle,
							arcLengthAngle).getRotatedCCW(
									Angle.fromDeg(distance < 0 ? 180 : 0));
					List<BezierCurve> arcBezier = Arrays.asList(arc.toBezier());

					// ensure arc beziers are in the correct order
					Point lastOffsetPoint = curve.get(cc.start).getTranslated(
							startNormal.getMultiplied(distance).toPoint());
					if (lastOffsetPoint.getDistance(
							arcBezier.get(0).getP1()) > lastOffsetPoint
									.getDistance(
											arcBezier.get(arcBezier.size() - 1)
													.getP2())) {
						// reverse curves
						Collections.reverse(arcBezier);
						for (int i = 0; i < arcBezier.size(); i++) {
							BezierCurve c = arcBezier.get(i);
							List<Point> pts = Arrays.asList(c.getPoints());
							Collections.reverse(pts);
							arcBezier.set(i, new BezierCurve(
									pts.toArray(new Point[] {})));
						}
					}

					// add arc and map to simple curve
					int approxSize = approxOffsetCurve.size();
					int simpleSize = simpleCurve.size();
					int i = 0;
					for (BezierCurve c : arcBezier) {
						approxOffsetCurve.add(c);
						approx2simple.put(approxSize + i, simpleSize - 1);
						a2sParamStart.put(approxSize + i, 1d);
						a2sParamEnd.put(approxSize + i, 1d);
						i++;
					}
				}
			}

			return new OffsetApproximation(curve, distance, simpleCurve,
					approxOffsetCurve, approx2simple, a2sParamStart,
					a2sParamEnd);
		}
	}

	/**
	 * <p>
	 * A {@link FatLine} combines a {@link Straight3D} with a positive and
	 * negative distance called dmax and dmin, respectively.
	 * </p>
	 * <p>
	 * It is used to apply a geometric clipping algorithm for finding
	 * {@link Point}s of intersection on two {@link BezierCurve}s. One of the
	 * {@link BezierCurve}s is bounded by a {@link FatLine} so that the other
	 * {@link BezierCurve} can be clipped against that {@link FatLine}.
	 * </p>
	 */
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
				if (d < L.dmin) {
					L.dmin = d;
				} else if (d > L.dmax) {
					L.dmax = d;
				}
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
	 * An {@link Interval} records a lower and an upper limit that define the
	 * mathematical interval [a;b] (inclusively). It is used to represent
	 * sub-curves of a {@link BezierCurve} by bounding the {@link BezierCurve}'s
	 * parameter value to the respective interval.
	 */
	static final class Interval {

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
		 * which is the parameter {@link Interval} representing a full
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
		 *            The first operand.
		 * @param j
		 *            The second operand.
		 * @return The {@link Interval} with the smallest parameter range.
		 */
		public static Interval min(Interval i, Interval j) {
			return (i.b - i.a) > (j.b - j.a) ? j : i;
		}

		/**
		 * An {@link Interval} records the parameter range [a;b]. Valid
		 * parameter ranges require 0 &lt;= a &lt;= b &lt;= 1.
		 */
		public double a;

		/**
		 * An {@link Interval} records the parameter range [a;b]. Valid
		 * parameter ranges require 0 &lt;= a &lt;= b &lt;= 1.
		 */
		public double b;

		/**
		 * <p>
		 * Constructs a new {@link Interval} object from the given double
		 * values. Only the first two double values are of importance as the
		 * rest of them are ignored.
		 * </p>
		 * <p>
		 * The new {@link Interval} holds the parameter range [a;b] if a is the
		 * first double value and b is the second double value.
		 * </p>
		 *
		 * @param ds
		 *            the lower and upper limit for the {@link Interval} object
		 *            to be created
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
		 * Checks if this {@link Interval}'s parameter range does converge with
		 * default imprecision.
		 *
		 * @return <code>true</code> if a ~= b (within default imprecision),
		 *         otherwise <code>false</code>
		 * @see Interval#converges(int)
		 */
		public boolean converges() {
			return converges(0);
		}

		/**
		 * <p>
		 * Checks if this {@link Interval}'s parameter range does converge with
		 * specified imprecision.
		 * </p>
		 * <p>
		 * The imprecision is specified by providing a shift value which shifts
		 * the epsilon used for the number comparison. A positive shift demands
		 * for a smaller epsilon (higher precision) whereas a negative shift
		 * demands for a greater epsilon (lower precision).
		 * </p>
		 *
		 * @param shift
		 *            precision shift
		 * @return <code>true</code> if a ~= b (within specified imprecision),
		 *         otherwise <code>false</code>
		 */
		public boolean converges(int shift) {
			return PrecisionUtils.equal(a, b, shift);
		}

		/**
		 * Expands this {@link Interval} to include the given other
		 * {@link Interval}.
		 *
		 * @param i
		 *            The other {@link Interval} to which <code>this</code> is
		 *            expanded.
		 */
		public void expand(Interval i) {
			if (i.a < a) {
				a = i.a;
			}
			if (i.b > b) {
				b = i.b;
			}
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
		 * Returns the middle parameter value <code>m = (a+b)/2</code> of this
		 * {@link Interval}.
		 *
		 * @return the middle parameter value of this {@link Interval}
		 */
		public double getMid() {
			return (a + b) / 2;
		}

		/**
		 * <p>
		 * Scales this {@link Interval} to the given {@link Interval}. The given
		 * {@link Interval} specifies the new upper and lower bounds of this
		 * {@link Interval} in percent.
		 * </p>
		 * <p>
		 * Returns the ratio of this {@link Interval}'s new parameter range to
		 * its old parameter range.
		 * </p>
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
			// ensure interval stays valid
			if (a < 0) {
				a = 0;
			}
			if (a > 1) {
				a = 1;
				b = 1;
			}
			if (b < 0) {
				a = 0;
				b = 0;
			}
			if (b > 1) {
				b = 1;
			}
			return ratio;
		}

	}

	// TODO: use constants that limit the number of iterations for the
	// different iterative/recursive algorithms:
	// INTERSECTIONS_MAX_ITERATIONS, APPROXIMATION_MAX_ITERATIONS

	/**
	 * An {@link IntervalPair} combines two {@link BezierCurve}s and their
	 * corresponding parameter ranges.
	 */
	static final class IntervalPair {

		/**
		 * Overwrites the attribute values of {@link IntervalPair} <i>dst</i>
		 * with the respective attribute values of {@link IntervalPair}
		 * <i>src</i>.
		 *
		 * @param dst
		 *            the destination {@link IntervalPair}
		 * @param src
		 *            the source {@link IntervalPair}
		 */
		public static void copy(IntervalPair dst, IntervalPair src) {
			dst.p = src.p;
			dst.q = src.q;
			dst.pi = src.pi;
			dst.qi = src.qi;
		}

		private static boolean equals(Vector3D v1, Vector3D v2,
				int precisionShift) {
			return PrecisionUtils.equal(v1.x / v1.z, v2.x / v2.z,
					precisionShift)
					&& PrecisionUtils.equal(v1.y / v1.z, v2.y / v2.z,
							precisionShift);
		}

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
		 * Checks if both parameter {@link Interval}s do converge (@see
		 * Interval#converges()) or both {@link BezierCurve}s are degenerated,
		 * i.e. they are collapsed to a single {@link Point}.
		 *
		 * @return <code>true</code> if both parameter {@link Interval}s do
		 *         converge, otherwise <code>false</code>
		 */
		public boolean converges() {
			return converges(0);
		}

		/**
		 * Checks if both parameter {@link Interval}s do converge (@see
		 * Interval#converges(int)) or both {@link BezierCurve}s are
		 * degenerated, i.e. they are collapsed to a single {@link Point}.
		 *
		 * @param shift
		 *            the precision shift
		 * @return <code>true</code> if both parameter {@link Interval}s do
		 *         converge, otherwise <code>false</code>
		 */
		public boolean converges(int shift) {
			return (pi.converges(shift)
					|| equals(p.getHC(pi.a), p.getHC(pi.b), shift))
					&& (qi.converges(shift)
							|| equals(q.getHC(qi.a), q.getHC(qi.b), shift));
		}

		/**
		 * Returns <code>true</code> if the first interval converges to a single
		 * point. Otherwise returns <code>false</code>.
		 *
		 * @return <code>true</code> if the first interval converges to a single
		 *         point, otherwise <code>false</code>.
		 */
		public boolean convergesP() {
			return equals(p.getHC(pi.a), p.getHC(pi.b), 0);
		}

		/**
		 * Returns <code>true</code> if the second interval converges to a
		 * single point. Otherwise returns <code>false</code>.
		 *
		 * @return <code>true</code> if the second interval converges to a
		 *         single point, otherwise <code>false</code>.
		 */
		public boolean convergesQ() {
			return equals(q.getHC(qi.a), q.getHC(qi.b), 0);
		}

		/**
		 * Expands this {@link IntervalPair} to include the given other
		 * {@link IntervalPair}.
		 *
		 * @param ip
		 *            The other {@link IntervalPair} to which <code>this</code>
		 *            is expanded.
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
		 * parameter {@link Interval}s, contrairwise, are truly copied.
		 *
		 * @return a copy of this {@link IntervalPair}
		 */
		public IntervalPair getCopy() {
			return new IntervalPair(p, pi.getCopy(), q, qi.getCopy());
		}

		/**
		 * Returns the first sub-curve of this {@link IntervalPair}. This curve
		 * is the first {@link BezierCurve} <i>p</i> over its corresponding
		 * parameter {@link Interval} <i>pi</i>.
		 *
		 * @return the first sub-curve of this {@link IntervalPair}
		 */
		public BezierCurve getPClipped() {
			return p.getClipped(Math.max(pi.a, 0), Math.min(pi.b, 1));
		}

		/**
		 * Splits the first parameter {@link Interval} <i>pi</i> at half and
		 * returns the resulting {@link IntervalPair}s.
		 *
		 * @return two {@link IntervalPair}s representing a split of the first
		 *         parameter {@link Interval} at half
		 */
		public IntervalPair[] getPSplit() {
			double pm = (pi.a + pi.b) / 2;
			return new IntervalPair[] {
					new IntervalPair(p, new Interval(pi.a, pm), q,
							qi.getCopy()),
					new IntervalPair(p,
							new Interval(
									Math.min(pi.b,
											pm + 10 * UNRECOGNIZABLE_PRECISION_FRACTION),
									pi.b),
							q, qi.getCopy()) };
		}

		/**
		 * Returns the second sub-curve of this {@link IntervalPair}. This curve
		 * is the second {@link BezierCurve} <i>q</i> over its corresponding
		 * parameter {@link Interval} <i>qi</i>.
		 *
		 * @return the second sub-curve of this {@link IntervalPair}
		 */
		public BezierCurve getQClipped() {
			return q.getClipped(Math.max(qi.a, 0), Math.min(qi.b, 1));
		}

		/**
		 * Splits the second parameter {@link Interval} <i>qi</i> at half and
		 * returns the resulting {@link IntervalPair}s.
		 *
		 * @return two {@link IntervalPair}s representing a split of the second
		 *         parameter {@link Interval} at half
		 */
		public IntervalPair[] getQSplit() {
			double qm = (qi.a + qi.b) / 2;
			return new IntervalPair[] {
					new IntervalPair(q, new Interval(qi.a, qm), p,
							pi.getCopy()),
					new IntervalPair(q,
							new Interval(
									Math.min(qi.b,
											qm + 10 * UNRECOGNIZABLE_PRECISION_FRACTION),
									qi.b),
							p, pi.getCopy()) };
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
		 * longer.
		 *
		 * @return <code>true</code> if the distance from start to end parameter
		 *         value of the first parameter {@link Interval} <i>pi</i> is
		 *         greater than the distance from start to end parameter value
		 *         of the second parameter {@link Interval} <i>qi</i>. Othwise,
		 *         returns <code>false</code>.
		 */
		public boolean isPLonger() {
			return (pi.b - pi.a) > (qi.b - qi.a);
		}
	}

	private static class LocalIntersectionOffsetRefiner {

		private static interface ICurveIntersector {
			public List<Point> getIntersections(BezierCurve cp, BezierCurve cq);
		}

		private static interface IGlobalIntersectionDetector {
			public boolean isGlobalIntersection(OffsetApproximation oa,
					int fstApproxIndex, int sndApproxIndex);
		}

		private static class Intersection {
			public int ai;
			public double ap;
			public int bi;
			public double bp;

			public Intersection(int ai, double ap, int bi, double bp) {
				this.ai = ai;
				this.ap = ap;
				this.bi = bi;
				this.bp = bp;
			}

			@Override
			public String toString() {
				return ai + "," + ap + " : " + bi + "," + bp;
			}
		}

		private static class LineSimilarityCurveIntersector
				implements ICurveIntersector {
			private static final double DEFAULT_LINE_SIMILARITY_THRESHOLD = 0.2d;
			private static final int DEFAULT_MAX_DEPTH = 32;

			// TODO: find a proper name
			private static double getLineSimilarity(BezierCurve cp) {
				double max = 0d;
				Line baseline = cp.toLine();
				int N = cp.getPoints().length;
				for (int i = 0; i < N; i++) {
					Point p = cp.get(i / (double) (N - 1));
					double distance = p.getDistance(baseline.getProjection(p));
					if (distance > max) {
						max = distance;
					}
				}
				return max;
			}

			private double lineSimilarityThreshold;

			private int maxDepth;

			public LineSimilarityCurveIntersector() {
				this(DEFAULT_LINE_SIMILARITY_THRESHOLD, DEFAULT_MAX_DEPTH);
			}

			public LineSimilarityCurveIntersector(
					double lineSimilarityThreshold, int maxDepth) {
				this.lineSimilarityThreshold = lineSimilarityThreshold;
				this.maxDepth = maxDepth;
			}

			@Override
			public List<Point> getIntersections(BezierCurve cp,
					BezierCurve cq) {
				return getIntersections(cp, cq, 0);
			}

			private List<Point> getIntersections(BezierCurve cp, BezierCurve cq,
					int currentDepth) {
				// throw away curves where the control bounds are separate
				if (!cp.getControlBounds().touches(cq.getControlBounds())) {
					return Collections.emptyList();
				}
				// line intersection approximation
				double lineSimilarityP = getLineSimilarity(cp);
				if (lineSimilarityP < lineSimilarityThreshold) {
					double lineSimilarityQ = getLineSimilarity(cq);
					if (lineSimilarityQ < lineSimilarityThreshold) {
						// compute line intersection
						Point baselineIntersection = cp.toLine()
								.getIntersection(cq.toLine());
						if (baselineIntersection == null) {
							return Collections.emptyList();
						}
						if (baselineIntersection != null) {
							// project onto both curves
							Point p = cp.getProjection(baselineIntersection);
							Point q = cq.getProjection(baselineIntersection);
							// return middle of projections as intersection
							Point approxIntersection = new Rectangle(p, q)
									.getCenter();
							ArrayList<Point> intersections = new ArrayList<>();
							intersections.add(approxIntersection);
							return intersections;
						}
					}
				}
				// subdivision
				ArrayList<Point> intersections = new ArrayList<>();
				if (currentDepth < maxDepth) {
					BezierCurve[] pSplit = cp.split(0.5);
					BezierCurve[] qSplit = cq.split(0.5);
					intersections.addAll(getIntersections(pSplit[0], qSplit[0],
							currentDepth + 1));
					intersections.addAll(getIntersections(pSplit[0], qSplit[1],
							currentDepth + 1));
					intersections.addAll(getIntersections(pSplit[1], qSplit[0],
							currentDepth + 1));
					intersections.addAll(getIntersections(pSplit[1], qSplit[1],
							currentDepth + 1));
				}
				return intersections;
			}
		}

		private static class WindingGlobalIntersectionDetector
				implements IGlobalIntersectionDetector {
			private static final double DEFAULT_SAMPLE_DISTANCE = 2d;
			private static final int DEFAULT_SAMPLE_COUNT = 36;

			private int sampleCount;
			private double sampleDistance;

			public WindingGlobalIntersectionDetector() {
				this(DEFAULT_SAMPLE_COUNT, DEFAULT_SAMPLE_DISTANCE);
			}

			public WindingGlobalIntersectionDetector(int sampleCount,
					double sampleDistance) {
				this.sampleCount = sampleCount;
				this.sampleDistance = sampleDistance;
			}

			private double determineAngle(List<BezierCurve> inputCurves) {
				// sample input segments with minimum distance
				List<Point> samples = new ArrayList<>();
				for (BezierCurve c : inputCurves) {
					samples.addAll(sample(c));
				}
				// compute signed angle from the samples
				double signedAngleSum = 0d;
				for (int s = 0; s < samples.size() - 3; s++) {
					Point p = samples.get(s);
					Point q = samples.get(s + 1);
					Point r = samples.get(s + 2);
					Vector u = new Vector(p, q);
					Vector v = new Vector(q, r);
					if (u.getLength() * v.getLength() > 0) {
						double ccw = u.getAngleCCW(v).rad();
						double cw = u.getAngleCW(v).rad();
						if (ccw < cw) {
							signedAngleSum += ccw;
						} else {
							signedAngleSum -= cw;
						}
					}
				}
				return signedAngleSum;
			}

			@Override
			public boolean isGlobalIntersection(OffsetApproximation oa,
					int fstApproxIndex, int sndApproxIndex) {
				// extract indices of the simplified input curve
				Integer simpleI = oa.getInputIndex(fstApproxIndex);
				Integer simpleJ = oa.getInputIndex(sndApproxIndex);
				if (simpleI == null || simpleJ == null) {
					throw new IllegalStateException(
							"OffsetApproximator does not map all offset approximation segments to the simplified input curve segments.");
				}

				// query the simplified input curve
				List<BezierCurve> simpleCurve = oa.getSimplifiedInputCurve();

				// query parameters for the simplified input curve
				Double ips = oa.getInputStartParam(fstApproxIndex);
				Double jpe = oa.getInputEndParam(sndApproxIndex);

				// construct input curve segments corresponding to
				// this part of the offset
				List<BezierCurve> inputCurves = new ArrayList<>();
				if (simpleJ > simpleI) {
					BezierCurve sl = simpleCurve.get(simpleI).split(ips)[1];
					inputCurves.add(sl);
					for (int n = simpleI + 1; n < simpleJ; n++) {
						inputCurves.add(simpleCurve.get(n));
					}
					BezierCurve sr = simpleCurve.get(simpleJ).split(jpe)[0];
					inputCurves.add(sr);
				} else {
					inputCurves
							.add(simpleCurve.get(simpleI).getClipped(ips, jpe));
				}

				return Math.abs(determineAngle(inputCurves)) >= Math.PI;
			}

			private List<Point> sample(BezierCurve curve) {
				List<Point> pts = new ArrayList<>();
				for (int i = -1; i < sampleCount; i++) {
					double t = (i + 1) / (double) sampleCount;
					if (pts.isEmpty()) {
						pts.add(curve.get(t));
					} else {
						Point pt = curve.get(t);
						if (pts.get(pts.size() - 1)
								.getDistance(pt) >= sampleDistance) {
							pts.add(pt);
						}
					}
				}
				return pts;
			}
		}

		private static final double DEFAULT_END_PARAM_PERCENTAGE = 0.02;
		// XXX: the containment epsilon has to be greater than the acceptable
		// offset error (see
		// TillerHansonOffsetAlgorithm#DEFAULT_ACCEPTABLE_ERROR)
		private static final double DEFAULT_CONTAINMENT_EPSILON = 0.02;

		private double endParamPercentage;
		private double containmentEpsilon;
		private ICurveIntersector curveIntersector;
		private IGlobalIntersectionDetector globalIntersectionDetector;

		public LocalIntersectionOffsetRefiner() {
			this(new LineSimilarityCurveIntersector(),
					new WindingGlobalIntersectionDetector(),
					DEFAULT_END_PARAM_PERCENTAGE, DEFAULT_CONTAINMENT_EPSILON);
		}

		public LocalIntersectionOffsetRefiner(
				ICurveIntersector curveIntersector,
				IGlobalIntersectionDetector globalIntersectionDetector,
				double endParamPercentage, double containmentEpsilon) {
			this.curveIntersector = curveIntersector;
			this.globalIntersectionDetector = globalIntersectionDetector;
			this.endParamPercentage = endParamPercentage;
			this.containmentEpsilon = containmentEpsilon;
		}

		public PolyBezier refine(OffsetApproximation oa) {
			// record intersections in the offset that need to be removed
			List<BezierCurve> approxOffset = oa.getApproximatedOffsetCurve();
			List<Intersection> offsetIntersections = new ArrayList<>();
			for (int i = 0; i < approxOffset.size() - 1; i++) {
				BezierCurve a = approxOffset.get(i);
				for (int j = i + 1; j < approxOffset.size(); j++) {
					BezierCurve b = approxOffset.get(j);
					Point[] intersections = curveIntersector
							.getIntersections(a, b).toArray(new Point[0]);
					if (intersections.length > 0) {
						// compute intersection clip parameters
						double minA = 1, maxB = 0;
						for (int k = 0; k < intersections.length; k++) {
							double ta = a.getParameterAt(
									a.getProjection(intersections[k]));
							double tb = b.getParameterAt(
									b.getProjection(intersections[k]));
							if (ta < minA) {
								minA = ta;
							}
							if (tb > maxB) {
								maxB = tb;
							}
						}

						// disregard start/end intersections
						if (j == i + 1 && intersections.length == 1
								&& minA > (1 - endParamPercentage)
								&& maxB < endParamPercentage) {
							continue;
						}
						// disregard global intersections
						if (globalIntersectionDetector.isGlobalIntersection(oa,
								i, j)) {
							continue;
						}

						offsetIntersections
								.add(new Intersection(i, minA, j, maxB));
					}
				}
			}

			// sort intersections by nesting
			List<Intersection> toRemove = new ArrayList<>();
			if (offsetIntersections.size() > 1) {
				for (int i = 0; i < offsetIntersections.size(); i++) {
					Intersection fst = offsetIntersections.get(i);
					boolean nesting = false;
					List<Integer> nested = new ArrayList<>();
					for (int j = i + 1; j < offsetIntersections.size(); j++) {
						Intersection snd = offsetIntersections.get(j);
						boolean lo = fst.ai < snd.ai
								|| fst.ai == snd.ai && fst.ap <= snd.ap;
						boolean hi = fst.bi > snd.bi
								|| fst.bi == snd.bi && fst.bp <= snd.bp;
						if (lo && hi) {
							nesting = true;
							nested.add(j);
						}
					}
					if (nesting) {
						Collections.reverse(nested);
						for (int j : nested) {
							offsetIntersections.remove(j);
						}
					}
					toRemove.add(fst);
				}
			} else if (offsetIntersections.size() == 1) {
				toRemove.add(offsetIntersections.get(0));
			}

			// clip offset at intersections and record indices of offset
			// segments that need to be removed completely
			List<Integer> indicesToRemove = new ArrayList<>();
			for (int i = toRemove.size() - 1; i >= 0; i--) {
				Intersection inter = toRemove.get(i);
				BezierCurve a = approxOffset.get(inter.ai);
				BezierCurve b = approxOffset.get(inter.bi);
				BezierCurve[] asplit = a.split(inter.ap);
				BezierCurve[] bananaSplit = b.split(inter.bp);
				// replace a and b with clipped versions
				approxOffset.set(inter.ai, asplit[0]);
				approxOffset.set(inter.bi, bananaSplit[1]);
				// remove all curves between a and b (if any)
				for (int k = inter.bi - 1; k > inter.ai; k--) {
					if (!indicesToRemove.contains(k)) {
						indicesToRemove.add(k);
					}
				}
			}

			// sort indices to remove descendingly so that we can iterate over
			// them and remove them without having to adjust the index
			Collections.sort(indicesToRemove, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					return o1 == o2 ? 0 : o1 < o1 ? -1 : +1;
				}
			});
			for (int k : indicesToRemove) {
				approxOffset.remove(k);
			}

			// find indices of fully contained offset curves
			List<Integer> fullyContainedOffsetIndices = new ArrayList<>();
			BezierCurve input = oa.getInputCurve();
			double dMin = Math.abs(oa.getOffsetDistance()) - containmentEpsilon;
			for (int i = approxOffset.size() - 1; i >= 0; i--) {
				BezierCurve oi = approxOffset.get(i);
				boolean fullyContained = true;
				for (Point p : oi.getBounds().getPoints()) {
					double dp = input.getProjection(p).getDistance(p);
					if (dp > dMin) {
						fullyContained = false;
					}
				}
				if (fullyContained) {
					fullyContainedOffsetIndices.add(i);
				}
			}

			if (fullyContainedOffsetIndices.size() > 0) {
				// search for fully contained start segments
				List<Integer> startRemove = new ArrayList<>();
				for (int i = 0; i < approxOffset.size()
						&& fullyContainedOffsetIndices.contains(i); i++) {
					startRemove.add(i);
				}
				Collections.reverse(startRemove);

				// search for fully contained end segments
				List<Integer> endRemove = new ArrayList<>();
				for (int i = approxOffset.size() - 1; i >= 0
						&& fullyContainedOffsetIndices.contains(i); i--) {
					endRemove.add(i);
				}

				// check if startRemove and endRemove overlap
				if (startRemove.size() > 0 && endRemove.size() > 0
						&& startRemove.get(0) >= endRemove
								.get(endRemove.size() - 1)) {
					// remove whole curve
					approxOffset.clear();
				} else {
					// remove the fully contained start/end segments
					// XXX: to prevent index adjustment, end is removed first
					for (int i : endRemove) {
						approxOffset.remove(i);
					}
					for (int i : startRemove) {
						approxOffset.remove(i);
					}
				}
			}

			// merge the curves to yield a valid PolyBezier
			return mergeCurves(approxOffset);
		}
	}

	private static class OffsetApproximation {
		private List<BezierCurve> simpleCurve = new ArrayList<>();
		private List<BezierCurve> approxOffsetCurve = new ArrayList<>();
		private Map<Integer, Integer> approx2simple = new HashMap<>();
		private Map<Integer, Double> a2sParamStart = new HashMap<>();
		private Map<Integer, Double> a2sParamEnd = new HashMap<>();
		private BezierCurve inputCurve;
		private double offsetDistance;

		public OffsetApproximation(BezierCurve inputCurve,
				double offsetDistance, List<BezierCurve> simpleCurve,
				List<BezierCurve> approxOffsetCurve,
				Map<Integer, Integer> approx2simple,
				Map<Integer, Double> a2sParamStart,
				Map<Integer, Double> a2sParamEnd) {
			this.inputCurve = inputCurve;
			this.offsetDistance = offsetDistance;
			this.simpleCurve = simpleCurve;
			this.approxOffsetCurve = approxOffsetCurve;
			this.approx2simple = approx2simple;
			this.a2sParamStart = a2sParamStart;
			this.a2sParamEnd = a2sParamEnd;
		}

		public List<BezierCurve> getApproximatedOffsetCurve() {
			return approxOffsetCurve;
		}

		public BezierCurve getInputCurve() {
			return inputCurve;
		}

		public double getInputEndParam(int i) {
			return a2sParamEnd.get(i);
		}

		public int getInputIndex(int i) {
			return approx2simple.get(i);
		}

		public double getInputStartParam(int i) {
			return a2sParamStart.get(i);
		}

		public double getOffsetDistance() {
			return offsetDistance;
		}

		public List<BezierCurve> getSimplifiedInputCurve() {
			return simpleCurve;
		}
	}

	private static final long serialVersionUID = 1L;

	private static final int CHUNK_SHIFT = -3;

	private static final boolean ORTHOGONAL = true;

	private static final boolean PARALLEL = false;

	private static final double UNRECOGNIZABLE_PRECISION_FRACTION = PrecisionUtils
			.calculateFraction(0) / 10;

	/**
	 * A criteria {@link BiFunction} to find the {@link Point} with the minimal
	 * x coordinate in a list of {@link Point}s.
	 *
	 * @see #findExtreme(BiFunction)
	 * @see #findExtreme(BiFunction, Interval)
	 */
	private static final BiFunction<Point, Point, Boolean> xminCriteria = (p,
			q) -> {
		return PrecisionUtils.smallerEqual(p.x, q.x);
	};

	/**
	 * A criteria {@link BiFunction} to find the {@link Point} with the maximal
	 * x coordinate in a list of {@link Point}s.
	 *
	 * @see #findExtreme(BiFunction)
	 * @see #findExtreme(BiFunction, Interval)
	 */
	private static final BiFunction<Point, Point, Boolean> xmaxCriteria = (p,
			q) -> {
		return PrecisionUtils.greaterEqual(p.x, q.x);
	};

	/**
	 * A criteria {@link BiFunction} to find the {@link Point} with the minimal
	 * y coordinate in a list of {@link Point}s.
	 *
	 * @see #findExtreme(BiFunction)
	 * @see #findExtreme(BiFunction, Interval)
	 */
	private static final BiFunction<Point, Point, Boolean> yminCriteria = (p,
			q) -> {
		return PrecisionUtils.smallerEqual(p.y, q.y);
	};

	/**
	 * A criteria {@link BiFunction} to find the {@link Point} with the maximal
	 * y coordinate in a list of {@link Point}s.
	 *
	 * @see #findExtreme(BiFunction)
	 * @see #findExtreme(BiFunction, Interval)
	 */
	private static final BiFunction<Point, Point, Boolean> ymaxCriteria = (p,
			q) -> {
		return PrecisionUtils.greaterEqual(p.y, q.y);
	};

	/**
	 * <p>
	 * Clusters consecutive {@link IntervalPair}s into a new array of
	 * {@link IntervalPair}s. Two {@link IntervalPair}s are regarded to be
	 * consecutive if they are {@link #isNextTo(IntervalPair, IntervalPair, int)
	 * next to} each other within the imprecision specified by the given
	 * <i>shift</i>.
	 * </p>
	 *
	 * @param intervalPairs
	 *            the array of {@link IntervalPair}s to cluster
	 * @param shift
	 *            the precision shift (see
	 *            {@link PrecisionUtils#calculateFraction(int)})
	 * @return a new array of {@link IntervalPair}s, each of which is the
	 *         composition of an {@link IntervalPair} cluster
	 */
	private static IntervalPair[] clusterChunks(IntervalPair[] intervalPairs,
			int shift) {
		ArrayList<IntervalPair> ips = new ArrayList<>();

		ips.addAll(Arrays.asList(intervalPairs));

		Collections.sort(ips, new Comparator<IntervalPair>() {
			@Override
			public int compare(IntervalPair i, IntervalPair j) {
				if (i.pi.a < j.pi.a) {
					return -1;
				} else if (i.pi.a > j.pi.a) {
					return 1;
				}
				return 0;
			}
		});

		ArrayList<IntervalPair> clusters = new ArrayList<>();
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

	/**
	 * Searches the parameter value of the given {@link Point} on the given
	 * {@link BezierCurve} using de Casteljau subdivision. The resulting
	 * parameter range for the {@link Point} is recorded in the given
	 * {@link Interval}. If the {@link Point} could be found on the
	 * {@link BezierCurve} within the given parameter {@link Interval}. The
	 * {@link Interval} is set to a convergin (see {@link Interval#converges()})
	 * parameter range that contains the {@link Point} on the
	 * {@link BezierCurve}.
	 *
	 * @param c
	 *            The {@link BezierCurve} on which the {@link Point} is searched
	 *            for.
	 * @param interval
	 *            The parameter {@link Interval} on the given
	 *            {@link BezierCurve} which is searched for the {@link Point}.
	 *            The resulting parameter range is recorded in this
	 *            {@link Interval}.
	 * @param p
	 *            the {@link Point} to find
	 * @return <code>true</code> if the a converging parameter {@link Interval}
	 *         that contains the {@link Point} can be identified, otherwise
	 *         <code>false</code>
	 */
	private static boolean containmentParameter(BezierCurve c,
			double[] interval, Point p) {
		Stack<Interval> parts = new Stack<>();
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
	 * <p>
	 * Returns the similarity of the given {@link BezierCurve} to a {@link Line}
	 * , which is defined as the absolute distance of its control {@link Point}s
	 * to the base {@link Line} connecting its end {@link Point}s.
	 * </p>
	 * <p>
	 * A similarity of <code>0</code> means that the given {@link BezierCurve}'s
	 * control {@link Point}s are on a straight {@link Line}.
	 * </p>
	 *
	 * @param c
	 *            the {@link BezierCurve} of which the distance to its base
	 *            {@link Line} is computed
	 * @return the distance of the given {@link BezierCurve} to its base
	 *         {@link Line}
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
	 * Searches for an overlapping segment within the given {@link IntervalPair}
	 * s.
	 *
	 * @param intersectionCandidates
	 *            the {@link IntervalPair}s representing non-end-{@link Point}
	 *            intersection candidates
	 * @param endPoints
	 *            the {@link IntervalPair}s representing end-{@link Point}
	 *            intersections
	 * @return <code>null</code> if no overlapping segment can be identified,
	 *         otherwise an {@link IntervalPair} representing the overlapping
	 *         segment
	 */
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
					|| (PrecisionUtils.smallerEqual(overlap.pi.a, 0)
							|| PrecisionUtils.greaterEqual(overlap.pi.b, 1))
							&& (PrecisionUtils.smallerEqual(overlap.qi.a, 0)
									|| PrecisionUtils.greaterEqual(overlap.qi.b,
											1))) {
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
	 * Checks all end {@link Point}s of the two passed-in {@link BezierCurve}s
	 * if they are {@link Point}s of intersection.
	 *
	 * @param ip
	 *            the {@link IntervalPair} describing both curves
	 * @param endPointIntervalPairs
	 *            the set of {@link IntervalPair}s to store the results
	 * @param intersections
	 *            the set of {@link Point}s to additionally store the associated
	 *            intersection {@link Point}s
	 */
	private static void findEndPointIntersections(IntervalPair ip,
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
			endPointIntervalPairs.add(new IntervalPair(ip.p,
					new Interval(0, ip.pi.a), ip.q, new Interval(interval)));
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
			endPointIntervalPairs.add(new IntervalPair(ip.p,
					new Interval(ip.pi.b, 1), ip.q, new Interval(interval)));
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
			endPointIntervalPairs.add(new IntervalPair(ip.p,
					new Interval(interval), ip.q, new Interval(0, ip.qi.a)));
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
			endPointIntervalPairs.add(new IntervalPair(ip.p,
					new Interval(interval), ip.q, new Interval(ip.qi.b, 1)));
			intersections.add(poi);
		}
	}

	/**
	 * <p>
	 * Find intersection {@link IntervalPair} chunks. The chunks are not very
	 * precise. We will refine them later.
	 * </p>
	 * <p>
	 * Searches for (imprecise) intersection {@link IntervalPair}s using the
	 * Bezier clipping algorithm. Every recorded {@link IntervalPair} limits the
	 * parameter {@link Interval} for a possible intersection on both
	 * {@link BezierCurve}s.
	 * </p>
	 *
	 * @param ip
	 *            the {@link IntervalPair} that is currently processed
	 * @param intervalPairs
	 *            the set of {@link IntervalPair}s to store the results
	 * @param intersections
	 *            the set of intersection {@link Point}s to store those in case
	 *            of a degenerated {@link BezierCurve} (or a degenerated
	 *            sub-curve)
	 */
	private static void findIntersectionChunks(IntervalPair ip,
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
	 * contain a single {@link Point} of intersection. We do now try to find it.
	 *
	 * @param ipIO
	 *            the {@link IntervalPair} that specifies a single {@link Point}
	 *            of intersection on two {@link BezierCurve}s
	 */
	private static Point findSinglePreciseIntersection(IntervalPair ipIO) {
		Stack<IntervalPair> partStack = new Stack<>();
		partStack.push(ipIO);

		while (!partStack.isEmpty()) {
			IntervalPair ip = partStack.pop();

			// quick check if intersections can be found
			BezierCurve pClipped = ip.getPClipped();
			BezierCurve qClipped = ip.getQClipped();
			if (!pClipped.getControlBounds()
					.touches(qClipped.getControlBounds())) {
				continue;
			}

			if (ip.convergesP()) {
				Point p = ip.p.getHC(ip.pi.a).toPoint();
				if (ip.q.contains(p)) {
					return p;
				}
			}

			if (ip.convergesQ()) {
				Point q = ip.q.getHC(ip.qi.a).toPoint();
				if (ip.p.contains(q)) {
					return q;
				}
			}

			if (ip.converges()) {
				// TODO: do another clipping algorithm here. the one that
				// uses control bounds.
				for (Point pp : ip.p.toPoints(ip.pi)) {
					for (Point qp : ip.q.toPoints(ip.qi)) {
						if (pp.equals(qp)) {
							IntervalPair.copy(ipIO, ip);
							return pp;
						}
					}
				}
				continue;
			}

			// construct "parallel" and "orthogonal" fat lines
			FatLine L1 = FatLine.from(qClipped, PARALLEL);
			FatLine L2 = FatLine.from(qClipped, ORTHOGONAL);

			// curve implosion check
			if (L1 == null || L2 == null) {
				// q is degenerated
				Point poi = ip.q.getHC(ip.qi.getMid()).toPoint();
				if (ip.p.contains(poi)) {
					IntervalPair.copy(ipIO, ip);
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
				IntervalPair[] nip = ip.isPLonger() ? ip.getPSplit()
						: ip.getQSplit();
				partStack.push(nip[1]);
				partStack.push(nip[0]);
			} else {
				partStack.push(ip.getSwapped());
			}
		}

		return null;
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

	/**
	 * Checks if the given {@link Interval}s are considered to be next to each
	 * other within the specified imprecision. Two {@link Interval}s are
	 * considered next to each other, if their limits are overlapping within the
	 * specified imprecision.
	 *
	 * @param i
	 * @param j
	 * @param shift
	 *            the precision shift (see
	 *            {@link PrecisionUtils#calculateFraction(int)})
	 * @return <code>true</code> if the two {@link Interval}s are considered to
	 *         be next to each other within the specified imprecision, otherwise
	 *         <code>false</code>
	 */
	private static boolean isNextTo(Interval i, Interval j, int shift) {
		return PrecisionUtils.smallerEqual(j.a, i.b, shift)
				&& PrecisionUtils.greaterEqual(j.b, i.a, shift);
	}

	/**
	 * Checks if the two {@link IntervalPair}s are considered next to each
	 * other. The {@link IntervalPair}s are regarded to be normalized (see
	 * {@link #normalizeIntervalPairs(IntervalPair[])}). Two
	 * {@link IntervalPair}s are considered next to each other, if the
	 * {@link Interval}s of their assigned {@link BezierCurve}s are considered
	 * next to each other (see {@link #isNextTo(Interval, Interval, int)}).
	 *
	 * @param a
	 * @param b
	 * @param shift
	 *            the precision shift (see
	 *            {@link PrecisionUtils#calculateFraction(int)})
	 * @return <code>true</code> if the {@link IntervalPair}s are considered
	 *         next to each other within the specified imprecision, otherwise
	 *         <code>false</code>
	 */
	private static boolean isNextTo(IntervalPair a, IntervalPair b, int shift) {
		return isNextTo(a.pi, b.pi, shift) && isNextTo(a.qi, b.qi, shift);
	}

	/**
	 * Merges the given List of {@link BezierCurve}s by setting the end/start
	 * point of two consecutive segments to the middle point between the two.
	 * Returns a {@link PolyBezier} that is constructed from the adjusted
	 * curves.
	 *
	 * @param curves
	 * @return A {@link PolyBezier} constructed from the merged curves.
	 */
	private static PolyBezier mergeCurves(List<BezierCurve> curves) {
		if (curves.size() > 1) {
			// adjust start/end points within the curves so that they are
			// continuous
			for (int i = 1; i < curves.size(); i++) {
				Point last = curves.get(i - 1).getP2();
				Point next = curves.get(i).getP1();
				if (!next.equals(last)) {
					Point mid = new Rectangle(last, next).getCenter();
					curves.get(i - 1).setP2(mid);
					curves.get(i).setP1(mid);
				}
			}
		}

		// save the refined offset as a PolyBezier
		return new PolyBezier(curves.toArray(new BezierCurve[] {}));
	}

	/**
	 * Moves the {@link Interval}'s start and end values. The start value is set
	 * to <i>x</i> if <i>x</i> is smaller than the start value. The end value is
	 * set to <i>x</i> if <i>x</i> is greater than the end value.
	 *
	 * @param interval
	 *            the {@link Interval} to modify
	 * @param x
	 *            the modification value
	 */
	// TODO: Migrate into a member function of Internal (and use it instead of
	// double[])
	private static void moveInterval(double[] interval, double x) {
		// assure that 0 <= x <= 1 to prevent invalid parameter values
		if (x < 0) {
			x = 0;
		} else if (x > 1) {
			x = 1;
		}

		if (interval[0] > x) {
			interval[0] = x;
		}
		if (interval[1] < x) {
			interval[1] = x;
		}
	}

	/**
	 * Normalizes the given {@link IntervalPair}s so that all
	 * {@link IntervalPair}s have the same {@link BezierCurve} assigned to their
	 * <code>p</code> attribute and that all {@link IntervalPair}s have the same
	 * {@link BezierCurve} assigned to their <code>q</code> attribute.
	 *
	 * @param intervalPairs
	 *            the {@link IntervalPair}s to normalize
	 */
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

	/**
	 * Binary search from the {@link IntervalPair}'s {@link Interval}s' limits
	 * to the {@link Interval} s' inner values to refine the overlap represented
	 * by the given {@link IntervalPair}.
	 *
	 * @param overlap
	 *            the {@link IntervalPair} representing the overlap of two
	 *            {@link BezierCurve}s
	 * @return the given {@link IntervalPair} for convenience
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

	/**
	 * Binary search from the {@link Interval}'s limits to the {@link Interval}
	 * 's inner values of the firstly given {@link BezierCurve} <i>p</i> for the
	 * outer-most intersection {@link Point} with the secondly given
	 * {@link BezierCurve} <i>q</i>.
	 *
	 * @param p
	 * @param mid
	 *            the {@link Interval}'s start value (
	 *            <code>mid > 0 ? mid : 0</code> )
	 * @param b
	 *            the {@link Interval}'s end value (<code>b < 1 ? b : 1</code>)
	 * @param q
	 * @return
	 */
	private static Interval refineOverlapHi(BezierCurve p, double mid, double b,
			BezierCurve q) {
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
	 * Binary search from the {@link Interval}'s limits to the {@link Interval}
	 * 's inner values of the firstly given {@link BezierCurve} <i>p</i> for the
	 * outer-most intersection {@link Point} with the secondly given
	 * {@link BezierCurve} <i>q</i>.
	 *
	 * @param p
	 * @param a
	 *            the {@link Interval}'s start value (<code>a > 0 ? a : 0</code>
	 *            )
	 * @param mid
	 *            the {@link Interval}'s end value (
	 *            <code>mid < 1 ? mid : 1</code>)
	 * @param q
	 * @return
	 */
	private static Interval refineOverlapLo(BezierCurve p, double a, double mid,
			BezierCurve q) {
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

	/**
	 * An array of {@link Vector3D}s which represent the control points of this
	 * {@link BezierCurve} in homogenous coordinates.
	 */
	private final Vector3D[] points;

	/**
	 * Constructs a new {@link BezierCurve} from the given {@link CubicCurve}.
	 *
	 * @param c
	 *            the {@link CubicCurve} of which the new {@link BezierCurve} is
	 *            constructed from
	 */
	public BezierCurve(CubicCurve c) {
		this(c.getP1(), c.getCtrl1(), c.getCtrl2(), c.getP2());
	}

	/**
	 * Constructs a new {@link BezierCurve} from the given control {@link Point}
	 * coordinates. The coordinates are expected to be in x, y order, i.e. x1,
	 * y1, x2, y2, x3, y3, ...
	 *
	 * @param controlPoints
	 *            the control {@link Point} coordinates of the new
	 *            {@link BezierCurve} in x, y order
	 */
	public BezierCurve(double... controlPoints) {
		this(PointListUtils.toPointsArray(controlPoints));
	}

	/**
	 * Constructs a new {@link BezierCurve} from the given control {@link Point}
	 * s.
	 *
	 * @param controlPoints
	 *            the control {@link Point}s of the new {@link BezierCurve}
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
	 *            the {@link QuadraticCurve} of which the new
	 *            {@link BezierCurve} is constructed from
	 */
	public BezierCurve(QuadraticCurve c) {
		this(c.getP1(), c.getCtrl(), c.getP2());
	}

	/**
	 * <p>
	 * Constructs a new {@link BezierCurve} object from the control points
	 * represented by the given {@link Vector3D}s.
	 * </p>
	 * <p>
	 * Note that a Point(x, y) is represented by a Vector3D(x, y, 1).
	 * </p>
	 *
	 * @param controlPoints
	 *            the {@link Vector3D}s representing the control points of the
	 *            new {@link BezierCurve}
	 */
	private BezierCurve(Vector3D... controlPoints) {
		points = new Vector3D[controlPoints.length];
		for (int i = 0; i < points.length; i++) {
			points[i] = controlPoints[i].getCopy();
		}
	}

	/**
	 * <p>
	 * Firstly, the difference of this {@link BezierCurve} to the given
	 * {@link FatLine} is computed. This is another {@link BezierCurve} of which
	 * the control {@link Point}s are further examined.
	 * </p>
	 * <p>
	 * Every difference control {@link Point} is checked if it is inside the
	 * given {@link FatLine}. Difference control {@link Point}s within the
	 * {@link FatLine} represent portions of this {@link BezierCurve} which
	 * cannot be clipped. Therefore, the {@link Interval} recording the
	 * parameter range of this {@link BezierCurve} is appropriately modified for
	 * these difference control {@link Point}s.
	 * </p>
	 * <p>
	 * Subsequently, the {@link Line}s connecting the start/end {@link Point} of
	 * the difference {@link BezierCurve} and the other control {@link Point}s
	 * of the difference {@link BezierCurve} are intersected with the
	 * {@link FatLine}'s border {@link Line}s. The outermost intersections
	 * identify parameter ranges that can be clipped away from this
	 * {@link BezierCurve}. Therefore, the {@link Interval} recording the
	 * parameter range of this {@link BezierCurve} is appropriately modified for
	 * these intersections.
	 * </p>
	 * <p>
	 * The starting {@link Interval} is chosen to be invalid. The individual
	 * checks move the lower and upper limits past to one another. If everything
	 * can be clipped, the resulting {@link Interval} remains invalid. If the
	 * resulting {@link Interval} <code>I = [a;b]</code> is valid (
	 * <code>a <= b</code>), then the portions <code>[0;a]</code> and
	 * <code>[b;1]</code> of this {@link BezierCurve} can be clipped away.
	 * </p>
	 *
	 * @param L
	 *            the {@link FatLine} to clip this {@link BezierCurve} to
	 * @return the new parameter {@link Interval} for this {@link BezierCurve}
	 */
	// TODO: return Interval
	private double[] clipTo(FatLine L) {
		double[] interval = new double[] { 1, 0 };

		Vector3D[] differenceVectors = genDifferencePoints(L.line);

		Point[] differencePoints = new Point[differenceVectors.length];
		for (int i = 0; i < differenceVectors.length; i++) {
			differencePoints[i] = differenceVectors[i].toPoint();
		}

		// inside fat line check
		for (Point p : differencePoints) {
			if (Double.isNaN(p.y) || L.dmin <= p.y && p.y <= L.dmax) {
				moveInterval(interval, p.x);
			}
		}

		// intersections from start
		for (int i = 1; i < differencePoints.length; i++) {
			Line seg = new Line(differencePoints[0], differencePoints[i]);
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

		// intersections from end
		for (int i = 0; i < differencePoints.length - 1; i++) {
			Line seg = new Line(differencePoints[i],
					differencePoints[differencePoints.length - 1]);
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
	 * <p>
	 * The other {@link BezierCurve} is regarded to be contained by this
	 * {@link BezierCurve} if its start and end {@link Point} lie on this
	 * {@link BezierCurve} and an overlapping segment of the two curves can be
	 * detected.
	 * </p>
	 *
	 * @param o
	 *            the {@link BezierCurve} that is checked to be contained by
	 *            this {@link BezierCurve}
	 * @return <code>true</code> if the given {@link BezierCurve} is contained
	 *         by this {@link BezierCurve}, otherwise <code>false</code>
	 */
	public boolean contains(BezierCurve o) {
		return contains(o.getP1()) && contains(o.getP2())
				&& getOverlap(o) != null;
	}

	@Override
	public boolean contains(final Point p) {
		if (p == null) {
			return false;
		}

		return containmentParameter(this, new double[] { 0, 1 }, p);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BezierCurve)) {
			return false;
		}
		BezierCurve o = (BezierCurve) other;
		BezierCurve t = this;
		while (o.points.length < t.points.length) {
			o = o.getElevated();
		}
		while (t.points.length < o.points.length) {
			t = t.getElevated();
		}
		Point[] oPoints = o.getPoints();
		Point[] tPoints = t.getPoints();
		return Arrays.equals(oPoints, tPoints)
				|| Arrays.equals(oPoints, Point.getReverseCopy(tPoints));
	}

	/**
	 * Searches for the specified extreme on this {@link BezierCurve}.
	 *
	 * @param criteria
	 *            Specifies the criteria to use for finding the extreme. As long
	 *            as it returns <code>true</code> for a given pair of points,
	 *            search is continued.
	 * @return The extreme {@link Point} that can be identified
	 */
	private Point findExtreme(BiFunction<Point, Point, Boolean> criteria) {
		return findExtreme(criteria, Interval.getFull());
	}

	/**
	 * <p>
	 * Searches for an extreme {@link Point} on this {@link BezierCurve}.
	 * </p>
	 *
	 * @param criteria
	 *            Specifies the criteria to use for finding the extreme. As long
	 *            as it returns <code>true</code> for a given pair of points,
	 *            search is continued.
	 * @param iStart
	 *            the start {@link Interval} on this {@link BezierCurve} in
	 *            which the extreme {@link Point} is searched for
	 * @return The extreme {@link Point} that could be found
	 */
	private Point findExtreme(BiFunction<Point, Point, Boolean> criteria,
			Interval iStart) {
		Stack<Interval> parts = new Stack<>();
		parts.push(iStart);

		Point xtreme = getHC(iStart.a).toPoint();

		while (!parts.isEmpty()) {
			Interval i = parts.pop();
			BezierCurve clipped = getClipped(i.a, i.b);

			Point sp = clipped.points[0].toPoint();
			xtreme = criteria.apply(sp, xtreme) ? sp : xtreme;
			Point ep = clipped.points[clipped.points.length - 1].toPoint();
			xtreme = criteria.apply(ep, xtreme) ? ep : xtreme;

			boolean everythingWorse = true;
			for (int j = 1; j < clipped.points.length - 1; j++) {
				if (!criteria.apply(xtreme, clipped.points[j].toPoint())) {
					everythingWorse = false;
					break;
				}
			}

			if (everythingWorse) {
				continue;
			}

			// split interval
			if (!i.converges()) {
				double im = i.getMid();
				parts.push(new Interval(im, i.b));
				parts.push(new Interval(i.a, im));
			}
		}

		return xtreme;
	}

	/**
	 * <p>
	 * Generates the difference control {@link Point}s of this
	 * {@link BezierCurve} to the given {@link Straight3D}.
	 * </p>
	 * <p>
	 * The difference control {@link Point}s are the control {@link Point}s of a
	 * {@link BezierCurve} that yields the signed distance of each {@link Point}
	 * on this {@link BezierCurve} to the given {@link Straight3D}.
	 * </p>
	 *
	 * @param line
	 *            the {@link Straight3D} to which the difference
	 *            {@link BezierCurve}'s control {@link Point}s are to be
	 *            computed
	 * @return the difference {@link BezierCurve}'s control {@link Point}s
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
	 * <i>t</i>, which is expected to lie in the parameter {@link Interval}
	 * <code>[0;1]</code>.
	 *
	 * @param t
	 *            the parameter value for which this {@link BezierCurve} is
	 *            evaluated
	 * @return the {@link Point} on this {@link BezierCurve} at the given
	 *         parameter value
	 */
	public Point get(double t) {
		return getHC(t).toPoint();
	}

	@Override
	public Rectangle getBounds() {
		// TODO: check costs of 'inlining' lambdas here
		double xmin = findExtreme(xminCriteria).x;
		double xmax = findExtreme(xmaxCriteria).x;
		double ymin = findExtreme(yminCriteria).y;
		double ymax = findExtreme(ymaxCriteria).y;
		return new Rectangle(new Point(xmin, ymin), new Point(xmax, ymax));
	}

	/**
	 * Returns a new {@link BezierCurve} object representing this
	 * {@link BezierCurve} on the {@link Interval} <code>[s;e]</code>.
	 *
	 * @param s
	 *            the lower limit of the parameter {@link Interval} which is
	 *            clipped out of this {@link BezierCurve}
	 * @param e
	 *            the upper limit of the parameter {@link Interval} which is
	 *            clipped out of this {@link BezierCurve}
	 * @return a new {@link BezierCurve} representing this {@link BezierCurve}
	 *         on the {@link Interval} <code>[s;e]</code>
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
	 * Returns a bounding {@link Rectangle} of the control {@link Polygon} of
	 * this {@link BezierCurve}.
	 *
	 * @return a {@link Rectangle} representing the bounds of the control
	 *         {@link Polygon} of this {@link BezierCurve}
	 */
	public Rectangle getControlBounds() {
		Point[] realPoints = getPoints();

		double xmin = realPoints[0].x, xmax = realPoints[0].x,
				ymin = realPoints[0].y, ymax = realPoints[0].y;

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

	@Override
	public BezierCurve getCopy() {
		return new BezierCurve(points);
	}

	/**
	 * Computes the hodograph, the first parametric derivative, of this
	 * {@link BezierCurve}.
	 *
	 * @return the hodograph of this {@link BezierCurve}
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
	 * Returns a {@link Vector3D} representing the {@link Point} at the given
	 * parameter value.
	 *
	 * @param t
	 *            the parameter value for which this {@link BezierCurve} is
	 *            evaluated
	 * @return The point at the given parameter value, represented in
	 *         homogeneous coordinates as a {@link Vector3D}.
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
	 * <p>
	 * Computes {@link IntervalPair}s which do reflect {@link Point}s of
	 * intersection between this and the given other {@link BezierCurve}. Each
	 * {@link IntervalPair} reflects a single {@link Point} of intersection.
	 * </p>
	 * <p>
	 * For every {@link IntervalPair} a {@link Point} of intersection is
	 * inserted into the given {@link Set} of {@link Point}s.
	 * </p>
	 * <p>
	 * If there are infinite {@link Point}s of intersection, i.e. the curves do
	 * overlap, an empty set is returned. (see
	 * {@link BezierCurve#overlaps(BezierCurve)})
	 * </p>
	 *
	 * @param other
	 *            The {@link BezierCurve} which is searched for {@link Point}s
	 *            of intersection with this {@link BezierCurve}.
	 * @param intersections
	 *            The {@link Point}-{@link Set} where {@link Point}s of
	 *            intersection are inserted.
	 * @return For a finite number of intersection {@link Point}s, a {@link Set}
	 *         of {@link IntervalPair}s is returned where every
	 *         {@link IntervalPair} represents a single {@link Point} of
	 *         intersection. For an infinite number of intersection
	 *         {@link Point}s, an empty {@link Set} is returned.
	 */
	Set<IntervalPair> getIntersectionIntervalPairs(BezierCurve other,
			Set<Point> intersections) {
		Set<IntervalPair> intervalPairs = new HashSet<>();
		Set<IntervalPair> endPointIntervalPairs = new HashSet<>();

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

		Set<IntervalPair> results = new HashSet<>();

		for (IntervalPair epip : endPointIntervalPairs) {
			if (overlapIntervalPair == null
					|| !isNextTo(overlapIntervalPair, epip, CHUNK_SHIFT)) {
				results.add(epip);
			} else {
				for (Iterator<Point> iterator = intersections
						.iterator(); iterator.hasNext();) {
					if (overlap.contains(iterator.next())) {
						iterator.remove();
					}
				}
			}
		}

		outer: for (IntervalPair cluster : clusters) {
			if (overlapIntervalPair != null) {
				if (isNextTo(overlapIntervalPair, cluster, CHUNK_SHIFT)) {
					continue outer;
				}
			}

			for (IntervalPair epip : endPointIntervalPairs) {
				if (isNextTo(cluster, epip, CHUNK_SHIFT)) {
					continue outer;
				}
			}

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
	 * Returns the {@link Point}s of intersection of this and the given other
	 * {@link BezierCurve}.
	 *
	 * @param other
	 *            the {@link BezierCurve} which is searched for {@link Point}s
	 *            of intersection with this {@link BezierCurve}
	 * @return the {@link Point}s of intersection of this {@link BezierCurve}
	 *         and the given other {@link BezierCurve}
	 */
	public Point[] getIntersections(BezierCurve other) {
		Set<Point> intersections = new HashSet<>();
		getIntersectionIntervalPairs(other, intersections);
		return intersections.toArray(new Point[] {});
	}

	@Override
	public final Point[] getIntersections(ICurve curve) {
		Set<Point> intersections = new HashSet<>();

		for (BezierCurve c : curve.toBezier()) {
			intersections.addAll(Arrays.asList(getIntersections(c)));
		}

		return intersections.toArray(new Point[] {});
	}

	/**
	 * Returns a {@link PolyBezier} that represents an approximation of the
	 * refined offset of this {@link BezierCurve} where cusps in the input curve
	 * are approximated by arc segments in the offset and local
	 * self-intersections in the offset are removed while global
	 * self-intersections and other singularities in the offset remain
	 * unprocessed.
	 *
	 * @param distance
	 *            The signed distance for which to compute a refined offset
	 *            approximation.
	 * @return A {@link PolyBezier} representing the refined offset of this
	 *         {@link BezierCurve} for the given distance.
	 */
	public PolyBezier getOffset(double distance) {
		return new LocalIntersectionOffsetRefiner()
				.refine(new CuspAwareOffsetApproximator()
						.approximateOffset(this, distance));
	}

	/**
	 * Returns a {@link PolyBezier} that represents an approximation of the
	 * offset of this {@link BezierCurve} where cusps in the input curve are
	 * approximated by arc segments in the offset but any singularities remain
	 * unprocessed.
	 *
	 * @param distance
	 *            The signed distance for which to compute an offset
	 *            approximation.
	 * @return A {@link PolyBezier} representing the (unprocessed) offset of
	 *         this {@link BezierCurve} for the given distance.
	 */
	PolyBezier getOffsetRaw(double distance) {
		// merge the curves to yield a valid PolyBezier
		return mergeCurves(new CuspAwareOffsetApproximator()
				.approximateOffset(this, distance)
				.getApproximatedOffsetCurve());
	}

	/**
	 * <p>
	 * Returns a {@link BezierCurve} that represents the overlap of this
	 * {@link BezierCurve} and the given other {@link BezierCurve}. If no
	 * overlap exists, <code>null</code> is returned. An overlap is identified
	 * by an infinite number of intersection points.
	 * </p>
	 *
	 * @param other
	 *            The {@link BezierCurve} to which an overlap is computed.
	 * @return a {@link BezierCurve} representing the overlap of this and the
	 *         given other {@link BezierCurve} if an overlap exists, otherwise
	 *         <code>null</code>
	 */
	public BezierCurve getOverlap(BezierCurve other) {
		if (equals(other)) {
			return getCopy();
		}

		Set<Point> intersections = new HashSet<>();
		Set<IntervalPair> intervalPairs = new HashSet<>();
		Set<IntervalPair> endPointIntervalPairs = new HashSet<>();

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

	@Override
	public final ICurve[] getOverlaps(ICurve c) {
		return CurveUtils.getOverlaps(this, c);
	}

	@Override
	public Point getP1() {
		return points[0].toPoint();
	}

	@Override
	public Point getP2() {
		return points[points.length - 1].toPoint();
	}

	/**
	 * Returns the parameter value of this {@link BezierCurve} for the given
	 * {@link Point}. If the given {@link Point} is not on this
	 * {@link BezierCurve} an {@link IllegalArgumentException} is thrown.
	 *
	 * @param p
	 *            the {@link Point} for which the parameter value on this
	 *            {@link BezierCurve} is to be found
	 * @return the corresponding parameter value of the given {@link Point} on
	 *         this {@link BezierCurve}
	 */
	public double getParameterAt(Point p) {
		if (p == null) {
			throw new IllegalArgumentException(
					"The passed-in Point may not be null: getParameterAt(" + p
							+ "), this = " + this);
		}

		double[] interval = new double[] { 0, 1 };
		if (containmentParameter(this, interval, p)) {
			return (interval[0] + interval[1]) / 2;
		} else {
			throw new IllegalArgumentException(
					"The given Point does not lie on this BezierCurve: getParameterAt("
							+ p + "), this = " + this);
		}
	}

	/**
	 * Returns the <i>i</i>th control {@link Point} of this {@link BezierCurve}.
	 * The start {@link Point} is at index <code>0</code>, the first handle-
	 * {@link Point} is at index <code>1</code>, etc.
	 *
	 * @param i
	 *            the index of the control {@link Point} of this
	 *            {@link BezierCurve} to return
	 * @return the <i>i</i>th control {@link Point} of this {@link BezierCurve}
	 */
	public Point getPoint(int i) {
		if (i < 0 || i >= points.length) {
			throw new IllegalArgumentException(
					"You can only index this BezierCurve's points from 0 to "
							+ (points.length - 1) + ": getPoint(" + i
							+ "), this = " + this);
		}
		return points[i].toPoint();
	}

	/**
	 * Returns the control {@link Point}s of this {@link BezierCurve}.
	 *
	 * @return the control {@link Point}s of this {@link BezierCurve}
	 */
	public Point[] getPoints() {
		Point[] realPoints = new Point[points.length];
		for (int i = 0; i < points.length; i++) {
			realPoints[i] = points[i].toPoint();
		}
		return realPoints;
	}

	/**
	 * Returns a copy of the {@link Vector3D} representations of the control
	 * points of this {@link BezierCurve}.
	 *
	 * @return a copy of the {@link Vector3D} representations of the control
	 *         points of this {@link BezierCurve}
	 */
	private Vector3D[] getPointsCopy() {
		Vector3D[] copy = new Vector3D[points.length];
		for (int i = 0; i < points.length; i++) {
			copy[i] = points[i].getCopy();
		}
		return copy;
	}

	@Override
	public Point getProjection(final Point reference) {
		// find nearest to reference within 100 samples
		int numSamples = 100;

		double nearestParam = 0;
		Point nearest = get(nearestParam);
		double distance = reference.getDistance(nearest);
		for (int i = 1; i < numSamples; i++) {
			double t = i / (numSamples - 1.0);
			Point candidate = get(t);
			double d = reference.getDistance(candidate);
			if (d < distance) {
				nearestParam = t;
				nearest = candidate;
				distance = d;
			}
		}

		// compute interval
		Interval interval = new Interval(
				nearestParam / (numSamples - 1) - 1 / (double) (numSamples - 1),
				nearestParam / (numSamples - 1)
						+ 1 / (double) (numSamples - 1));
		// ensure interval is valid
		interval.a = Math.min(1, Math.max(0, interval.a));
		interval.b = Math.min(1, Math.max(0, interval.b));

		// refine interval
		while (!interval.converges()) {
			// compute start point and end point for the current interval
			Point sp = get(interval.a);
			Point ep = get(interval.b);

			// compute distance to reference point
			double sDist = reference.getDistance(sp);
			double eDist = reference.getDistance(ep);

			if (sDist >= distance && eDist >= distance) {
				// start point and end point have greater distance
				// => reduce interval on both sides
				double range = interval.b - interval.a;
				interval.b = interval.a + 0.75 * range;
				interval.a = interval.a + 0.25 * range;
			} else if (sDist < distance && sDist < eDist) {
				// start has smaller distance
				distance = sDist;
				nearest = sp;
				// reduce interval to its left side
				interval.b = (interval.a + interval.b) / 2;
			} else if (eDist < distance) {
				// end has smaller distance
				distance = eDist;
				nearest = ep;
				// reduce interval to its right side
				interval.a = (interval.a + interval.b) / 2;
			} else {
				// impossible
				throw new IllegalStateException(
						"condition should not be reachable");
			}
		}

		return nearest;
	}

	@Override
	public BezierCurve getRotatedCCW(Angle angle) {
		return getCopy().rotateCCW(angle);
	}

	@Override
	public BezierCurve getRotatedCCW(Angle angle, double cx, double cy) {
		return getCopy().rotateCCW(angle, cx, cy);
	}

	@Override
	public BezierCurve getRotatedCCW(Angle angle, Point center) {
		return getCopy().rotateCCW(angle, center);
	}

	@Override
	public BezierCurve getRotatedCW(Angle angle) {
		return getCopy().rotateCW(angle);
	}

	@Override
	public BezierCurve getRotatedCW(Angle angle, double cx, double cy) {
		return getCopy().rotateCW(angle, cx, cy);
	}

	@Override
	public BezierCurve getRotatedCW(Angle angle, Point center) {
		return getCopy().rotateCW(angle, center);
	}

	@Override
	public BezierCurve getScaled(double factor) {
		return getCopy().scale(factor);
	}

	@Override
	public BezierCurve getScaled(double fx, double fy) {
		return getCopy().scale(fx, fy);
	}

	@Override
	public BezierCurve getScaled(double factor, double cx, double cy) {
		return getCopy().scale(factor, cx, cy);
	}

	@Override
	public BezierCurve getScaled(double fx, double fy, double cx, double cy) {
		return getCopy().scale(fx, fy, cx, cy);
	}

	@Override
	public BezierCurve getScaled(double fx, double fy, Point center) {
		return getCopy().scale(fx, fy, center);
	}

	@Override
	public BezierCurve getScaled(double factor, Point center) {
		return getCopy().scale(factor, center);
	}

	/**
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	@Override
	public BezierCurve getTransformed(AffineTransform t) {
		return new BezierCurve(t.getTransformed(getPoints()));
	}

	@Override
	public BezierCurve getTranslated(double dx, double dy) {
		return getCopy().translate(dx, dy);
	}

	@Override
	public BezierCurve getTranslated(Point d) {
		return getCopy().translate(d.x, d.y);
	}

	@Override
	public double getX1() {
		return getP1().x;
	}

	@Override
	public double getX2() {
		return getP2().x;
	}

	@Override
	public double getY1() {
		return getP1().y;
	}

	@Override
	public double getY2() {
		return getP2().y;
	}

	@Override
	public boolean intersects(ICurve c) {
		return getIntersections(c).length > 0;
	}

	/**
	 * Checks if this {@link BezierCurve} and the given other
	 * {@link BezierCurve} overlap, i.e. an infinite set of intersection
	 * {@link Point}s exists.
	 *
	 * @param other
	 *            the {@link BezierCurve} to check for an overlapping segment
	 *            with this {@link BezierCurve}
	 * @return <code>true</code> if this and the given other {@link BezierCurve}
	 *         overlap, otherwise <code>false</code>
	 */
	public boolean overlaps(BezierCurve other) {
		return getOverlap(other) != null;
	}

	@Override
	public final boolean overlaps(ICurve c) {
		for (BezierCurve seg : c.toBezier()) {
			if (overlaps(seg)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Directly rotates this {@link BezierCurve} counter-clockwise (CCW) around
	 * its center {@link Point} by the given {@link Angle}. Direct adaptation
	 * means, that <code>this</code> {@link BezierCurve} is modified in-place.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 */
	public BezierCurve rotateCCW(Angle angle) {
		Point centroid = Point.getCentroid(getPoints());
		return rotateCCW(angle, centroid.x, centroid.y);
	}

	/**
	 * Directly rotates this {@link BezierCurve} counter-clockwise (CCW) around
	 * the {@link Point} specified by the given x and y coordinate values by the
	 * given {@link Angle}. Direct adaptation means, that <code>this</code>
	 * {@link BezierCurve} is modified in-place.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @param cx
	 *            the x coordinate of the {@link Point} to rotate around
	 * @param cy
	 *            the y coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public BezierCurve rotateCCW(Angle angle, double cx, double cy) {
		Point[] realPoints = getPoints();
		Point.rotateCCW(realPoints, angle, cx, cy);
		for (int i = 0; i < realPoints.length; i++) {
			setPoint(i, realPoints[i]);
		}
		return this;
	}

	/**
	 * Directly rotates this {@link BezierCurve} counter-clockwise (CCW) around
	 * the given {@link Point} by the given {@link Angle}. Direct adaptation
	 * means, that <code>this</code> {@link BezierCurve} is modified in-place.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @param center
	 *            the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public BezierCurve rotateCCW(Angle angle, Point center) {
		for (int i = 0; i < points.length; i++) {
			points[i] = new Vector3D(new Vector(
					points[i].toPoint().getTranslated(center.getNegated()))
							.getRotatedCCW(angle).toPoint()
							.getTranslated(center));
		}
		return this;
	}

	/**
	 * Directly rotates this {@link BezierCurve} clockwise (CW) around its
	 * center {@link Point} by the given {@link Angle}. Direct adaptation means,
	 * that <code>this</code> {@link BezierCurve} is modified in-place.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 */
	public BezierCurve rotateCW(Angle angle) {
		Point centroid = Point.getCentroid(getPoints());
		return rotateCW(angle, centroid.x, centroid.y);
	}

	/**
	 * Directly rotates this {@link BezierCurve} clockwise (CW) around the
	 * {@link Point} specified by the given x and y coordinate values by the
	 * given {@link Angle}. Direct adaptation means, that <code>this</code>
	 * {@link BezierCurve} is modified in-place.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @param cx
	 *            the x coordinate of the {@link Point} to rotate around
	 * @param cy
	 *            the y coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public BezierCurve rotateCW(Angle angle, double cx, double cy) {
		Point[] realPoints = getPoints();
		Point.rotateCW(realPoints, angle, cx, cy);
		for (int i = 0; i < realPoints.length; i++) {
			setPoint(i, realPoints[i]);
		}
		return this;
	}

	/**
	 * Directly rotates this {@link BezierCurve} clockwise (CW) around the given
	 * {@link Point} by the given {@link Angle}. Direct adaptation means, that
	 * <code>this</code> {@link BezierCurve} is modified in-place.
	 *
	 * @param angle
	 *            the rotation {@link Angle}
	 * @param center
	 *            the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public BezierCurve rotateCW(Angle angle, Point center) {
		return rotateCW(angle, center.x, center.y);
	}

	@Override
	public BezierCurve scale(double factor) {
		return scale(factor, factor);
	}

	@Override
	public BezierCurve scale(double fx, double fy) {
		Point centroid = Point.getCentroid(getPoints());
		return scale(fx, fy, centroid.x, centroid.y);
	}

	@Override
	public BezierCurve scale(double factor, double cx, double cy) {
		return scale(factor, factor, cx, cy);
	}

	@Override
	public BezierCurve scale(double fx, double fy, double cx, double cy) {
		Point[] realPoints = getPoints();
		Point.scale(realPoints, fx, fy, cx, cy);
		for (int i = 0; i < realPoints.length; i++) {
			setPoint(i, realPoints[i]);
		}
		return this;
	}

	@Override
	public BezierCurve scale(double fx, double fy, Point center) {
		return scale(fx, fy, center.x, center.y);
	}

	@Override
	public BezierCurve scale(double factor, Point center) {
		return scale(factor, factor, center.x, center.y);
	}

	/**
	 * Sets the start {@link Point} of this {@link BezierCurve} to the given
	 * {@link Point}.
	 *
	 * @param p1
	 *            the new start {@link Point} of this {@link BezierCurve}
	 * @return <code>this</code> for convenience
	 */
	public BezierCurve setP1(Point p1) {
		setPoint(0, p1);
		return this;
	}

	/**
	 * Sets the end {@link Point} of this {@link BezierCurve} to the given
	 * {@link Point}.
	 *
	 * @param p2
	 *            the new end {@link Point} of this {@link BezierCurve}
	 * @return <code>this</code> for convenience
	 */
	public BezierCurve setP2(Point p2) {
		setPoint(points.length - 1, p2);
		return this;
	}

	/**
	 * Sets the <i>i</i>th control {@link Point} of this {@link BezierCurve}.
	 * The start {@link Point} is at index <code>0</code>, the first handle-
	 * {@link Point} is at index <code>1</code>, etc.
	 *
	 * @param i
	 *            the index of the control {@link Point} of this
	 *            {@link BezierCurve} to set
	 * @param p
	 *            the new control {@link Point} at the given index
	 * @return <code>this</code> for convenience
	 */
	public BezierCurve setPoint(int i, Point p) {
		if (i < 0 || i >= points.length) {
			throw new IllegalArgumentException("setPoint(" + i + ", " + p
					+ "): You can only index this BezierCurve's points from 0 to "
					+ (points.length - 1) + ".");
		}
		points[i] = new Vector3D(p);
		return this;
	}

	/**
	 * Subdivides this {@link BezierCurve} at the given parameter value <i>t</i>
	 * into two new {@link BezierCurve}s. The first one is the
	 * {@link BezierCurve} over the parameter {@link Interval}
	 * <code>[0;t]</code> and the second one is the {@link BezierCurve} over the
	 * parameter {@link Interval} <code>[t;1]</code>.
	 *
	 * @param t
	 *            the parameter value at which this {@link BezierCurve} is
	 *            subdivided
	 * @return an array of two {@link BezierCurve}s, the left (
	 *         <code>[0;t]</code>) and the right (<code>[t;1]</code>)
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

	@Override
	public BezierCurve[] toBezier() {
		return new BezierCurve[] { this };
	}

	/**
	 * Returns a hard approximation of this {@link BezierCurve} as a
	 * {@link CubicCurve}. The new {@link CubicCurve} is constructed from the
	 * start {@link Point}, the first two handle {@link Point}s and the end
	 * {@link Point} of this {@link BezierCurve}. If this {@link BezierCurve} is
	 * not of degree four or higher, i.e. it does not have four or more control
	 * {@link Point}s (including start and end {@link Point}), <code>null</code>
	 * is returned.
	 *
	 * @return a new {@link CubicCurve} that is constructed from the start
	 *         {@link Point}, the first two handle {@link Point}s and the end
	 *         {@link Point} of this {@link BezierCurve} or <code>null</code> if
	 *         this {@link BezierCurve} does not have at least four control
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
	 * @return a {@link Line} from the start {@link Point} to the end
	 *         {@link Point} of this {@link BezierCurve} or <code>null</code> if
	 *         this {@link BezierCurve} does only have one control {@link Point}
	 */
	public Line toLine() {
		if (points.length > 1) {
			return new Line(points[0].toPoint(),
					points[points.length - 1].toPoint());
		}
		return null;
	}

	/**
	 * Computes an approximation of this {@link BezierCurve} by a strip of
	 * {@link Line}s. For detailed information on how the approximation is
	 * computed, see {@link BezierCurve#toLineStrip(double, Interval)}.
	 *
	 * @param lineSimilarity
	 *            the threshold for the sum of the distances of the control
	 *            {@link Point}s to the baseline ({@link #toLine()}) of this
	 *            {@link BezierCurve}
	 * @return an approximation of this {@link BezierCurve} by a strip of
	 *         {@link Line}s
	 * @see BezierCurve#toLineStrip(double, Interval)
	 */
	public Line[] toLineStrip(double lineSimilarity) {
		return toLineStrip(lineSimilarity, Interval.getFull());
	}

	/**
	 * <p>
	 * Computes an approximation of this {@link BezierCurve} by a strip of
	 * {@link Line}s.
	 * </p>
	 * <p>
	 * The {@link BezierCurve} is recursively subdivided until it is "similar"
	 * to a straight {@link Line}. The similarity check computes the sum of the
	 * distances of the control {@link Point}s to the baseline (
	 * {@link #toLine()}) of this {@link BezierCurve}. If this sum is smaller
	 * than the given <i>lineSimilarity</i>, the {@link BezierCurve} is assumed
	 * to be "similar" to a straight line.
	 * </p>
	 *
	 * @param lineSimilarity
	 *            the threshold for the sum of the distances of the control
	 *            points to the baseline of this {@link BezierCurve}
	 * @param startInterval
	 *            the {@link Interval} of this {@link BezierCurve} that has to
	 *            be approximated by a strip of {@link Line}s
	 * @return {@link Line} segments approximating this {@link BezierCurve}
	 */
	public Line[] toLineStrip(double lineSimilarity, Interval startInterval) {
		ArrayList<Line> lines = new ArrayList<>();

		Point startPoint = getHC(startInterval.a).toPoint();

		Stack<Interval> parts = new Stack<>();
		parts.push(startInterval);

		while (!parts.isEmpty()) {
			Interval i = parts.pop();
			BezierCurve part = getClipped(i.a, i.b);

			if (distanceToBaseLine(part) < lineSimilarity) {
				Point endPoint = getHC(i.b).toPoint();
				lines.add(new Line(startPoint, endPoint));
				startPoint = endPoint;
			} else {
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
	 *         {@link Line} segments
	 */
	@Override
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
	 * {@link Point#equals(Object) equal} to each other.
	 *
	 * @param startInterval
	 *            the {@link Interval} of this {@link BezierCurve} to calculate
	 *            {@link Point}s for
	 * @return {@link Point}s on this {@link BezierCurve} over the given
	 *         parameter {@link Interval} where consecutive {@link Point}s are
	 *         {@link Point#equals(Object) equal} to each other
	 */
	public Point[] toPoints(Interval startInterval) {
		ArrayList<Point> points = new ArrayList<>();
		points.add(getHC(startInterval.a).toPoint());

		Stack<Interval> parts = new Stack<>();
		parts.push(startInterval);

		while (!parts.isEmpty()) {
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
	 * from the start {@link Point}, the first handle {@link Point} and the end
	 * {@link Point} of this {@link BezierCurve}. If this {@link BezierCurve} is
	 * not of degree three or higher, i.e. it does not have three or more
	 * control {@link Point}s (including start and end {@link Point}),
	 * <code>null</code> is returned.
	 *
	 * @return a new {@link QuadraticCurve} that is constructed from the start
	 *         {@link Point}, the first handle {@link Point} and the end
	 *         {@link Point} of this {@link BezierCurve} or <code>null</code> if
	 *         this {@link BezierCurve} does not have at least three control
	 *         {@link Point}s
	 */
	public QuadraticCurve toQuadratic() {
		if (points.length > 2) {
			return new QuadraticCurve(points[0].toPoint(), points[1].toPoint(),
					points[points.length - 1].toPoint());
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("BezierCurve(");
		for (int i = 0; i < points.length; i++) {
			Vector3D v = points[i];
			str.append(v);
			if (i < points.length - 1) {
				str.append(", ");
			}
		}
		str.append(")");
		return str.toString();
	}

	@Override
	public BezierCurve translate(double dx, double dy) {
		Point[] realPoints = getPoints();
		Point.translate(realPoints, dx, dy);
		for (int i = 0; i < realPoints.length; i++) {
			setPoint(i, realPoints[i]);
		}
		return this;
	}

	@Override
	public BezierCurve translate(Point d) {
		return translate(d.x, d.y);
	}

}
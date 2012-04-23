/*******************************************************************************
 * Copyright (c) 2010 Research Group Software Construction, 
 *                    RWTH Aachen University and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alexander Nyßen (Research Group Software Contruction, RWTH Aachen University) - initial API and implementation
 *    Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.euclidean;

import java.io.Serializable;

import org.eclipse.gef4.geometry.Angle;
import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.projective.Straight3D;
import org.eclipse.gef4.geometry.projective.Vector3D;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * Represents a straight line within 2-dimensional Euclidean space.
 * 
 * @author anyssen
 */
public class Straight implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	/** position vector of this straight */
	public Vector position;

	/** direction vector of this straight */
	public Vector direction;

	/**
	 * Constructs a new Straight with the given position and direction.
	 * 
	 * @param position
	 * @param direction
	 */
	public Straight(Vector position, Vector direction) {
		this.position = position.clone();
		this.direction = direction.clone();
	}

	/**
	 * Constructs a new Straight between the two given Points.
	 * 
	 * @param point1
	 *            a first waypoint of the Straight to be constructed
	 * @param point2
	 *            a second waypoint of the Straight to be constructed
	 */
	public Straight(Point point1, Point point2) {
		this(new Vector(point1), new Vector(point1, point2));
	}

	/**
	 * Constructs a new {@link Straight} through the start and end {@link Point}
	 * of the given {@link Line}.
	 * 
	 * @param line
	 */
	public Straight(Line line) {
		this(line.getP1(), line.getP2());
	}

	@Override
	public Straight clone() {
		return getCopy();
	}

	/**
	 * Checks whether this Straight and the provided one have a single point of
	 * intersection.
	 * 
	 * @param other
	 *            The Straight to use for the calculation.
	 * @return true if the two Straights intersect, false otherwise, even if the
	 *         straights are equal.
	 */
	public boolean intersects(Straight other) {
		return !PrecisionUtils.equal(direction.getDotProduct(other.direction
				.getOrthogonalComplement()), 0, +6);
	}

	/**
	 * Checks whether this Straight and the provided one have an intersection
	 * point, which is inside the specified segment between segmentStart and
	 * segmentEnd.
	 * 
	 * @param segmentStart
	 *            a Vector indicating the start point of the segment. Has to be
	 *            a point on the straight.
	 * @param segmentEnd
	 *            a Vector indicating the end point of the segment. Has to be a
	 *            point on the straight.
	 * @param other
	 *            the Straight to test
	 * @return true if the true straights intersect and the intersection point
	 *         is contained within the specified segment, false otherwise.
	 * @since 3.2
	 */
	public boolean intersectsWithinSegment(Vector segmentStart,
			Vector segmentEnd, Straight other) {
		// precondition: segment start and end have to be points on this
		// straight.
		if (!contains(segmentStart) || !contains(segmentEnd)) {
			throw new IllegalArgumentException(
					"segment points have to be contained"); //$NON-NLS-1$
		}

		// check if segmentStart->segmentEnd is a legal segment or a single
		// point
		Vector segmentDirection = segmentEnd.getSubtracted(segmentStart);
		if (segmentDirection.isNull()) {
			return other.contains(segmentStart);
		}

		// legal segment, check if there is an intersection within the segment
		if (intersects(other)) {
			Vector intersection = getIntersection(other);
			return containsWithinSegment(segmentStart, segmentEnd, intersection);
		}
		return false;
	}

	/**
	 * Computes the intersection point of this Straight and the provided one, if
	 * it exists.
	 * 
	 * @param other
	 *            The Straight to use for calculations.
	 * @return A Vector pointing to the intersection point, if it exists, null
	 *         if no intersection point exists (or the Straights are equal).
	 */
	public Vector getIntersection(Straight other) {
		Vector3D l1 = new Vector3D(this.position.toPoint())
				.getCrossed(new Vector3D(this.position.getAdded(this.direction)
						.toPoint()));
		Vector3D l2 = new Vector3D(other.position.toPoint())
				.getCrossed(new Vector3D(other.position.getAdded(
						other.direction).toPoint()));

		Point poi = l1.getCrossed(l2).toPoint();

		return poi == null ? null : new Vector(poi);
	}

	/**
	 * Returns the (smallest) angle between this Straight and the provided one.
	 * 
	 * @param other
	 *            The Straight to be used for the calculation.
	 * @return The angle spanned between the two {@link Straight}s.
	 */
	public Angle getAngle(Straight other) {
		return direction.getAngle(other.direction);
	}

	/**
	 * Returns the clock-wise (CW) or negative angle spanned between the two
	 * {@link Straight}s.
	 * 
	 * The returned angle is the opposite of the angle returned by the
	 * getAngleCCW(Straight other) but for an angle of 0\u00b0. For an angle of
	 * 0\u00b0 both methods return an angle of 0 degrees.
	 * 
	 * @param other
	 * @return The clock-wise (CW) or negative angle spanned between the two
	 *         {@link Straight}s.
	 */
	public Angle getAngleCW(Straight other) {
		Angle angle = getAngleCCW(other);
		Angle angle0 = Angle.fromRad(0);
		if (angle.equals(angle0)) {
			// special-case 0Â°: CW/CCW does not matter
			return angle0;
		}
		return angle.getOppositeSemi();
	}

	/**
	 * Returns the counter-clock-wise (CCW) or positive {@link Angle} spanned
	 * between the two {@link Straight}s.
	 * 
	 * The returned {@link Angle} is the opposite of the {@link Angle} returned
	 * by the getAngleCCW(Straight other) method.
	 * 
	 * @param other
	 * @return The counter-clock-wise (CCW) or positive angle spanned between
	 *         the two {@link Straight}s.
	 */
	public Angle getAngleCCW(Straight other) {
		Angle angle = getAngle(other);
		if (direction.getCrossProduct(other.direction) > 0) {
			angle = angle.getOppositeSemi();
		}
		return angle;
	}

	/**
	 * Returns the projection of the given {@link Vector} onto this
	 * {@link Straight}, which is the point on this {@link Straight} with the
	 * minimal distance to the point, denoted by the provided {@link Vector}.
	 * 
	 * @param vector
	 *            The {@link Vector} whose projection should be determined.
	 * @return A new {@link Vector} representing the projection of the provided
	 *         {@link Vector} onto this {@link Straight}.
	 */
	public Vector getProjection(Vector vector) {
		// calculate with a normalized direction vector to prevent rounding
		// effects
		Vector normalized = direction.getNormalized();
		return new Straight(position, normalized).getIntersection(new Straight(
				vector, normalized.getOrthogonalComplement()));
	}

	/**
	 * Returns the distance of the provided Vector to this Straight, which is
	 * the distance between the provided Vector and its projection onto this
	 * Straight.
	 * 
	 * @param vector
	 *            The Vector whose distance is to be calculated.
	 * @return the distance between this Straight and the provided Vector.
	 */
	public double getDistance(Vector vector) {
		return getProjection(vector).getSubtracted(vector).getLength();
	}

	/**
	 * Returns the signed distance of the given {@link Vector} to this
	 * {@link Straight}.
	 * 
	 * The signed distance indicates on which side of the {@link Straight} the
	 * {@link Vector} lies. If it lies on the right side of this
	 * {@link Straight}'s direction {@link Vector}, the signed distance is
	 * negative. If it is on the left side of this {@link Straight}'s direction
	 * Vector, the signed distance is positive.
	 * 
	 * @param vector
	 * @return the signed distance of the given {@link Vector} to this Straight
	 */
	public double getSignedDistanceCCW(Vector vector) {
		// TODO: check which implementation is better

		return Straight.getSignedDistanceCCW(this.position.toPoint(),
				this.position.getAdded(this.direction).toPoint(),
				vector.toPoint());

		// Vector projected = getProjection(vector);
		// Vector d = vector.getSubtracted(projected);
		//
		// double len = d.getLength();
		//
		// if (!d.isNull()) {
		// Angle angleCW = direction.getAngleCW(d);
		//
		// if (angleCW.equals(Angle.fromDeg(90))) {
		// len = -len;
		// }
		// }
		//
		// return len;
	}

	/**
	 * Returns the signed distance of the given {@link Vector} to this Straight.
	 * 
	 * The signed distance indicates on which side of the Straight the Vector
	 * lies. If it is on the right side of this Straight's direction Vector, the
	 * signed distance is positive. If it is on the left side of this Straight's
	 * direction Vector, the signed distance is negative.
	 * 
	 * @param vector
	 * @return the signed distance of the given {@link Vector} to this Straight
	 */
	public double getSignedDistanceCW(Vector vector) {
		return -getSignedDistanceCCW(vector);
	}

	/**
	 * Returns this {@link Straight}'s parameter value for the given
	 * {@link Point} p.
	 * 
	 * This method is the reverse of the getPointAt(double parameter) method.
	 * 
	 * @param p
	 * @return this {@link Straight}'s parameter value for the given
	 *         {@link Point} p
	 */
	public double getParameterAt(Point p) {
		if (direction.x != 0) {
			return (p.x - position.x) / direction.x;
		}
		if (direction.y != 0) {
			return (p.y - position.y) / direction.y;
		}
		return 0; // never get here
	}

	/**
	 * Returns the {@link Point} on this {@link Straight} at parameter p. The
	 * {@link Point} that you get is calculated by multiplying this
	 * {@link Straight}'s direction {@link Vector} by the parameter value and
	 * translating that {@link Vector} by this {@link Straight}'s position
	 * {@link Vector}.
	 * 
	 * This method is the reverse of the getPointAt(double parameter) method.
	 * 
	 * @param parameter
	 * @return the {@link Point} on this {@link Straight} at parameter p
	 */
	public Point getPointAt(double parameter) {
		return new Point(position.x + direction.x * parameter, position.y
				+ direction.y * parameter);
	}

	/**
	 * Calculates whether the point indicated by the provided Vector is a point
	 * on this Straight.
	 * 
	 * @param vector
	 *            the Vector who has to be checked.
	 * @return true if the point indicated by the given Vector is a point of
	 *         this Straight, false otherwise.
	 */
	public boolean contains(Vector vector) {
		// deal with rounding effects here
		return PrecisionUtils.equal(getDistance(vector), 0);
	}

	/**
	 * Calculates whether the point indicated by the provided Vector is a point
	 * on the straight segment between the given start and end points.
	 * 
	 * @param segmentStart
	 *            a Vector indicating the start point of the segment. Has to be
	 *            a point on the straight.
	 * @param segmentEnd
	 *            a Vector indicating the end point of the segment. Has to be a
	 *            point on the straight.
	 * @param vector
	 *            the Vector who has to be checked.
	 * @return true if point indicated by the given Vector is a point on this
	 *         straight, within the specified segment, false otherwise.
	 */
	public boolean containsWithinSegment(Vector segmentStart,
			Vector segmentEnd, Vector vector) {
		// precondition: segment start and end have to be points on this
		// straight.
		if (!contains(segmentStart) || !contains(segmentEnd)) {
			throw new IllegalArgumentException(
					"segment points have to be contained"); //$NON-NLS-1$
		}

		// check if segmentStart->segmentEnd is a legal segment or a single
		// point
		Vector segmentDirection = segmentEnd.getSubtracted(segmentStart);
		if (segmentDirection.isNull()) {
			return segmentStart.equals(vector);
		}

		// legal segment
		if (new Straight(segmentStart, segmentDirection).contains(vector)) {
			// compute parameter s, so that vector = segmentStart + s *
			// (segmentEnd - segmentStart).
			double s = segmentDirection.isVertical() ? (vector.y - segmentStart.y)
					/ segmentDirection.y
					: (vector.x - segmentStart.x) / segmentDirection.x;
			// if s is between 0 and 1, intersection point lies within
			// segment
			if (PrecisionUtils.smallerEqual(0, s)
					&& PrecisionUtils.smallerEqual(s, 1)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether this {@link Straight} and the provided one are parallel to
	 * each other.
	 * 
	 * @param other
	 *            The {@link Straight} to test for parallelism.
	 * @return true if the direction {@link Vector}s of this {@link Straight}
	 *         and the provided one are parallel, false otherwise.
	 */
	public boolean isParallelTo(Straight other) {
		return direction.isParallelTo(other.direction);
	}

	/**
	 * Checks whether this {@link Straight} is equal to the provided
	 * {@link Straight}. Two {@link Straight}s s1 and s2 are equal, if the
	 * position {@link Vector} of s2 is a point on s1 and the direction
	 * {@link Vector}s of s1 and s2 are parallel.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Straight)) {
			return false;
		} else {
			Straight otherStraight = (Straight) other;
			return contains(otherStraight.position)
					&& isParallelTo(otherStraight);
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// calculating a better hashCode is not possible, because due to the
		// imprecision, equals() is no longer transitive
		return 0;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Straight: " + position.toString() + " + s * " + direction.toString(); //$NON-NLS-1$
	}

	/**
	 * Returns a copy of this {@link Straight} object.
	 * 
	 * @return a copy of this {@link Straight} object.
	 */
	public Straight getCopy() {
		return new Straight(position, direction);
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
	public static double getSignedDistanceCCW(Point p, Point q, Point r) {
		Straight3D line = Straight3D.through(new Vector3D(p), new Vector3D(q));
		if (line == null) {
			return 0d;
		}
		return -line.getSignedDistanceCW(new Vector3D(r));
	}

}
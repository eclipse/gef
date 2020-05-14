/*******************************************************************************
 * Copyright (c) 2010, 2016 Research Group Software Construction,
 *                          RWTH Aachen University and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (Research Group Software Contruction, RWTH Aachen University) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.euclidean;

import java.io.Serializable;

import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.projective.Straight3D;
import org.eclipse.gef.geometry.projective.Vector3D;

/**
 * Represents a straight line within 2-dimensional Euclidean space.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class Straight implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * <p>
	 * Computes the counter-clockwise (CCW) signed distance of the third
	 * {@link Point} to the {@link Straight} through the first two {@link Point}
	 * s.
	 * </p>
	 * <p>
	 * The CCW signed distance is positive if the three {@link Point}s are in
	 * counter-clockwise order and negative if the {@link Point}s are in
	 * clockwise order. It is zero if the third {@link Point} lies on the line.
	 * </p>
	 * <p>
	 * If the first two {@link Point}s are equal to each other, this method
	 * returns the distance of the first {@link Point} to the last {@link Point}
	 * .
	 * </p>
	 *
	 * @param p
	 *            the start {@link Point} of the {@link Straight}
	 * @param q
	 *            the end {@link Point} of the {@link Straight}
	 * @param r
	 *            the relative {@link Point}
	 * @return the CCW signed distance of the {@link Point} r to the
	 *         {@link Straight} through {@link Point}s p and q
	 */
	public static double getSignedDistanceCCW(Point p, Point q, Point r) {
		Straight3D line = Straight3D.through(new Vector3D(p), new Vector3D(q));
		if (line == null) {
			return 0d;
		}
		return -line.getSignedDistanceCW(new Vector3D(r));
	}

	/** The position {@link Vector} of this {@link Straight}. */
	public Vector position;

	/** The direction {@link Vector} of this {@link Straight}. */
	public Vector direction;

	/**
	 * Constructs a new {@link Straight} through the start and end {@link Point}
	 * of the given {@link Line}.
	 *
	 * @param line
	 *            the {@link Line} which the new {@link Straight} shall pass
	 *            through
	 */
	public Straight(Line line) {
		this(line.getP1(), line.getP2());
	}

	/**
	 * Constructs a new {@link Straight} that passes through the two given
	 * {@link Point}s.
	 *
	 * @param point1
	 *            a first waypoint of the {@link Straight} to be constructed
	 * @param point2
	 *            a second waypoint of the {@link Straight} to be constructed
	 */
	public Straight(Point point1, Point point2) {
		this(new Vector(point1), new Vector(point1, point2));
	}

	/**
	 * Constructs a new {@link Straight} with the given position {@link Vector}
	 * and direction {@link Vector}.
	 *
	 * @param position
	 *            a support {@link Vector} of the new {@link Straight}
	 * @param direction
	 *            a direction {@link Vector} of the new {@link Straight}
	 */
	public Straight(Vector position, Vector direction) {
		this.position = position.clone();
		this.direction = direction.clone();
	}

	@Override
	public Straight clone() {
		return getCopy();
	}

	/**
	 * Checks if the {@link Point} indicated by the provided {@link Vector} is a
	 * {@link Point} on this {@link Straight}.
	 *
	 * @param vector
	 *            the {@link Vector} that is checked to lie on this
	 *            {@link Straight}
	 * @return <code>true</code> if the {@link Point} indicated by the given
	 *         {@link Vector} is a {@link Point} of this {@link Straight},
	 *         otherwise <code>false</code>
	 */
	public boolean contains(Vector vector) {
		// deal with rounding effects here
		return PrecisionUtils.equal(getDistance(vector), 0);
	}

	/**
	 * Checks if the {@link Point} indicated by the provided {@link Vector} is a
	 * {@link Point} on the {@link Straight} segment between the given start and
	 * end {@link Point}s indicated by their corresponding position
	 * {@link Vector}s.
	 *
	 * @param segmentStart
	 *            A {@link Vector} indicating the start {@link Point} of the
	 *            segment. It has to lie on this {@link Straight}.
	 * @param segmentEnd
	 *            A {@link Vector} indicating the end {@link Point} of the
	 *            segment. It has to lie on this {@link Straight}.
	 * @param vector
	 *            The {@link Vector} that is checked for containment.
	 * @return <code>true</code> if the {@link Point} indicated by the given
	 *         {@link Vector} lies on this {@link Straight}, within the
	 *         specified segment, otherwise <code>false</code>
	 */
	public boolean containsWithinSegment(Vector segmentStart, Vector segmentEnd,
			Vector vector) {
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
			double s = segmentDirection.y != 0
					? (vector.y - segmentStart.y) / segmentDirection.y
					: (vector.x - segmentStart.x) / segmentDirection.x;
			// if s is between 0 and 1, the intersection point lies within
			// segment
			if (PrecisionUtils.smallerEqual(0, s)
					&& PrecisionUtils.smallerEqual(s, 1)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if this {@link Straight} is equal to the provided {@link Straight}
	 * . Two {@link Straight}s s1 and s2 are equal, if the position
	 * {@link Vector} of s2 is a {@link Point} on s1 and the direction
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
	 * Returns the (smallest) {@link Angle} between this {@link Straight} and
	 * the provided one.
	 *
	 * @param other
	 *            the {@link Straight} to compute the {@link Angle} with
	 * @return the {@link Angle} spanned between the two {@link Straight}s
	 */
	public Angle getAngle(Straight other) {
		return direction.getAngle(other.direction);
	}

	/**
	 * <p>
	 * Returns the counter-clockwise (CCW) or positive {@link Angle} spanned
	 * between the two {@link Straight}s.
	 * </p>
	 * <p>
	 * The returned {@link Angle} is the semi-opposite (see
	 * {@link Angle#getOppositeSemi()}) of the {@link Angle} returned by the
	 * {@link Straight#getAngleCW(Straight)} method except that for 180deg/0deg,
	 * both methods return an {@link Angle} of 0deg.
	 * </p>
	 *
	 * @param other
	 *            The {@link Straight} to which the {@link Angle} is computed.
	 * @return the counter-clockwise (CCW) or positive {@link Angle} spanned
	 *         between the two {@link Straight}s
	 */
	public Angle getAngleCCW(Straight other) {
		Angle angle = getAngle(other);
		if (direction.getCrossProduct(other.direction) > 0) {
			angle = angle.getOppositeSemi();
		}
		return angle;
	}

	/**
	 * <p>
	 * Returns the clockwise (CW) or negative {@link Angle} spanned between the
	 * two {@link Straight}s.
	 * </p>
	 * <p>
	 * The returned {@link Angle} is the semi-opposite (see
	 * {@link Angle#getOppositeSemi()}) of the {@link Angle} returned by the
	 * {@link Straight#getAngleCCW(Straight)} method except that for
	 * 180deg/0deg, both methods return an {@link Angle} of 0deg.
	 * </p>
	 *
	 * @param other
	 *            The {@link Straight} to which the {@link Angle} is computed.
	 * @return the clockwise (CW) or negative {@link Angle} spanned between the
	 *         two {@link Straight}s
	 */
	public Angle getAngleCW(Straight other) {
		Angle angle = getAngleCCW(other);
		Angle angle0 = Angle.fromRad(0);
		if (angle.equals(angle0)) {
			// special-case 0deg: CW/CCW does not matter
			return angle0;
		}
		return angle.getOppositeSemi();
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
	 * Returns the distance of the provided {@link Vector} to this
	 * {@link Straight}, which is the distance between the provided
	 * {@link Vector} and its projection onto this {@link Straight} (see
	 * {@link Straight#getProjection(Vector)}).
	 *
	 * @param vector
	 *            the {@link Vector} whose distance to this {@link Straight} is
	 *            to be calculated
	 * @return the distance between this {@link Straight} and the provided
	 *         {@link Vector}
	 */
	public double getDistance(Vector vector) {
		return getProjection(vector).getSubtracted(vector).getLength();
	}

	/**
	 * Computes and returns the position {@link Vector} of the intersection of
	 * this {@link Straight} and the provided one. If the two {@link Straight}s
	 * are parallel or identical, <code>null</code> is returned.
	 *
	 * @param other
	 *            the {@link Straight} to compute the position {@link Vector} of
	 *            the intersection with
	 * @return a {@link Vector} pointing to the intersection point,
	 *         <code>null</code> if no intersection {@link Point} exists
	 */
	public Vector getIntersection(Straight other) {
		Vector p1 = this.position.getAdded(this.direction);
		Vector p2 = other.position.getAdded(other.direction);
		Vector3D l1 = new Vector3D(this.position.x, this.position.y, 1)
				.getCrossProduct(new Vector3D(p1.toPoint()));
		Vector3D l2 = new Vector3D(other.position.x, other.position.y, 1)
				.getCrossProduct(new Vector3D(p2.toPoint()));
		Point poi = l1.getCrossProduct(l2).toPoint();

		return poi == null ? null : new Vector(poi);
	}

	/**
	 * <p>
	 * Returns this {@link Straight}'s parameter value for the given
	 * {@link Vector}. If the given {@link Vector} is not on this
	 * {@link Straight} an {@link IllegalArgumentException} is thrown.
	 * </p>
	 * <p>
	 * This method is the reverse of the
	 * {@link Straight#getPositionVectorAt(double)} method.
	 * </p>
	 *
	 * @param vp
	 *            a {@link Vector} on this {@link Straight} for which the
	 *            parameter value is to be calculated
	 * @return this {@link Straight}'s parameter value for the given
	 *         {@link Vector}
	 */
	public double getParameterAt(Vector vp) {
		if (!contains(vp)) {
			throw new IllegalArgumentException(
					"The given position Vector has to be on this Straight: getParameterAt("
							+ vp + "), this = " + this);
		}

		if (Math.abs(direction.x) > Math.abs(direction.y)) {
			return (vp.x - position.x) / direction.x;
		}
		if (direction.y != 0) {
			return (vp.y - position.y) / direction.y;
		}

		throw new IllegalStateException(
				"The direction Vector of this Straight may not be (0, 0) for this computation: getParameterAt("
						+ vp + "), this = " + this);
	}

	/**
	 * <p>
	 * Returns the {@link Vector} on this {@link Straight} at the given
	 * parameter value. The {@link Vector} that you get is calculated by
	 * multiplying this {@link Straight}'s direction {@link Vector} by the
	 * parameter value and translating that {@link Vector} by this
	 * {@link Straight}'s position {@link Vector}.
	 * </p>
	 * <p>
	 * This method is the reverse of the {@link Straight#getParameterAt(Vector)}
	 * method.
	 * </p>
	 *
	 * @param parameter
	 *            the parameter value for which the corresponding {@link Vector}
	 *            on this {@link Straight} is to be calculated
	 * @return the {@link Vector} on this {@link Straight} at the passed-in
	 *         parameter value
	 */
	public Vector getPositionVectorAt(double parameter) {
		return new Vector(position.x + direction.x * parameter,
				position.y + direction.y * parameter);
	}

	/**
	 * Returns the projection of the given {@link Vector} onto this
	 * {@link Straight}, which is the {@link Point} on this {@link Straight}
	 * with the minimal distance to the {@link Point}, denoted by the provided
	 * {@link Vector}.
	 *
	 * @param vector
	 *            the {@link Vector} whose projection should be determined
	 * @return a new {@link Vector} representing the projection of the provided
	 *         {@link Vector} onto this {@link Straight}
	 */
	public Vector getProjection(Vector vector) {
		// calculate with a normalized direction vector to prevent rounding
		// effects
		Vector normalized = direction.getNormalized();

		// to compensate rounding problems with large vectors, we shift
		// straight and given vector by the straight's position vector before
		// the computation and back before returning the computed projection.
		Straight s1 = new Straight(Vector.NULL, normalized);
		Straight s2 = new Straight(vector.getSubtracted(position),
				normalized.getOrthogonalComplement());
		return s1.getIntersection(s2).getAdded(position);
	}

	/**
	 * <p>
	 * Returns the counter-clockwise (CCW) signed distance of the given
	 * {@link Vector} to this {@link Straight}.
	 * </p>
	 * <p>
	 * The CCW signed distance indicates on which side of the {@link Straight}
	 * the {@link Vector} lies. If it lies on the right side of this
	 * {@link Straight}'s direction {@link Vector}, the CCW signed distance is
	 * negative. If it is on the left side of this {@link Straight}'s direction
	 * {@link Vector}, it is positive.
	 * </p>
	 *
	 * @param vector
	 *            the {@link Vector} for which the CCW signed distance to this
	 *            {@link Straight} is to be calculated
	 * @return the CCW signed distance of the given {@link Vector} to this
	 *         {@link Straight}
	 */
	public double getSignedDistanceCCW(Vector vector) {
		return Straight.getSignedDistanceCCW(this.position.toPoint(),
				this.position.getAdded(this.direction).toPoint(),
				vector.toPoint());
	}

	/**
	 * <p>
	 * Returns the clockwise (CW) signed distance of the given {@link Vector} to
	 * this {@link Straight}.
	 * </p>
	 * <p>
	 * The CW signed distance indicates on which side of the {@link Straight}
	 * the {@link Vector} lies. If it is on the right side of this
	 * {@link Straight}'s direction {@link Vector}, the CW signed distance is
	 * positive. If it is on the left side of this {@link Straight}'s direction
	 * {@link Vector}, it is negative.
	 * </p>
	 *
	 * @param vector
	 *            the {@link Vector} for which the CW signed distance to this
	 *            {@link Straight} is to be calculated
	 * @return the CW signed distance of the given {@link Vector} to this
	 *         {@link Straight}
	 */
	public double getSignedDistanceCW(Vector vector) {
		return -getSignedDistanceCCW(vector);
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
	 * Checks whether this {@link Straight} and the provided one have a single
	 * {@link Point} of intersection.
	 *
	 * @param other
	 *            the {@link Straight} to test for an intersection {@link Point}
	 *            with this {@link Straight}
	 * @return <code>true</code> if the two {@link Straight}s intersect in one
	 *         single {@link Point}, otherwise <code>false</code>
	 */
	public boolean intersects(Straight other) {
		return !PrecisionUtils.equal(direction.getDotProduct(
				other.direction.getOrthogonalComplement()), 0, +6);
	}

	/**
	 * Checks whether this {@link Straight} and the provided one have an
	 * intersection {@link Point} which is inside the specified segment between
	 * the segmentStart and segmentEnd {@link Vector}s.
	 *
	 * @param segmentStart
	 *            A {@link Vector} indicating the start {@link Point} of the
	 *            segment. It has to be a {@link Point} on the {@link Straight}.
	 * @param segmentEnd
	 *            A {@link Vector} indicating the end {@link Point} of the
	 *            segment. It has to be a {@link Point} on the {@link Straight}.
	 * @param other
	 *            The {@link Straight} to test.
	 * @return <code>true</code> if the two {@link Straight}s intersect and the
	 *         intersection {@link Point} is contained within the specified
	 *         segment, otherwise <code>false</code>
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
			return containsWithinSegment(segmentStart, segmentEnd,
					intersection);
		}
		return false;
	}

	/**
	 * Checks if this {@link Straight} and the provided one are parallel to each
	 * other. Identical {@link Straight}s are regarded to be parallel to each
	 * other.
	 *
	 * @param other
	 *            the {@link Straight} that is checked to be parallel to this
	 *            {@link Straight}
	 * @return <code>true</code> if the direction {@link Vector}s of this
	 *         {@link Straight} and the provided one are parallel, otherwise
	 *         <code>false</code>
	 */
	public boolean isParallelTo(Straight other) {
		return direction.isParallelTo(other.direction);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Straight: " + position.toString() + " + s * " //$NON-NLS-1$
				+ direction.toString();
	}

}

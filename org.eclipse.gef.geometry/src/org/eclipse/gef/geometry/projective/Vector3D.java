/*******************************************************************************
 * Copyright (c) 2011, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.projective;

import org.eclipse.gef.geometry.planar.Point;

/**
 * The Vector3D class implements a three dimensional vector (components x, y, z)
 * with its standard operations: addition and multiplication (scalar,
 * dot-product, cross-product).
 *
 * It is used to represent planar lines and planar points which are represented
 * by three dimensional planes and three dimensional lines through the origin,
 * respectively.
 *
 * @author mwienand
 *
 */
public final class Vector3D {
	/**
	 * the x-coordinate of this {@link Vector3D}.
	 */
	public double x;

	/**
	 * the y-coordinate of this {@link Vector3D}.
	 */
	public double y;

	/**
	 * the homogeneous coordinate of this {@link Vector3D}.
	 */
	public double z;

	/**
	 * Constructs a new {@link Vector3D} object with the given component values.
	 *
	 * @param px
	 *            The x-coordinate of the new {@link Vector3D}.
	 * @param py
	 *            The y-coordinate of the new {@link Vector3D}.
	 * @param pz
	 *            The z-coordinate of the new {@link Vector3D}.
	 */
	public Vector3D(double px, double py, double pz) {
		x = px;
		y = py;
		z = pz;
	}

	/**
	 * Constructs a new {@link Vector3D} from the given {@link Point}, setting z
	 * to 1.
	 *
	 * @param p
	 *            The {@link Point} which determines the new {@link Vector3D}'s
	 *            x- and y-coordinate.
	 */
	public Vector3D(Point p) {
		this(p.x, p.y, 1);
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
	 * Returns a new {@link Vector3D} object with its components set to the sum
	 * of the individual x, y and z components of this {@link Vector3D} and the
	 * given other {@link Vector3D}.
	 *
	 * @param other
	 *            The {@link Vector3D} which is added to this {@link Vector3D}.
	 * @return a new {@link Vector3D} object representing the sum of this
	 *         {@link Vector3D} and the given other {@link Vector3D}
	 */
	public Vector3D getAdded(Vector3D other) {
		return new Vector3D(this.x + other.x, this.y + other.y,
				this.z + other.z);
	}

	/**
	 * Returns a copy of this {@link Vector3D}.
	 *
	 * @return a copy of this {@link Vector3D}
	 */
	public Vector3D getCopy() {
		return new Vector3D(x, y, z);
	}

	/**
	 * Returns a new {@link Vector3D} object that is the cross product of this
	 * and the given other {@link Vector3D}.
	 *
	 * @param other
	 *            The {@link Vector3D} to which the cross product is computed.
	 * @return a new {@link Vector3D} object that is the cross product of this
	 *         and the given other {@link Vector3D}
	 */
	public Vector3D getCrossProduct(Vector3D other) {
		return new Vector3D(this.y * other.z - this.z * other.y,
				this.z * other.x - this.x * other.z,
				this.x * other.y - this.y * other.x);
	}

	/**
	 * Returns the dot-product of this and the given other {@link Vector3D}.
	 *
	 * @param other
	 *            The {@link Vector3D} to which the dot product is computed.
	 * @return the dot-product of this and the given other {@link Vector3D}
	 */
	public double getDotProduct(Vector3D other) {
		return this.x * other.x + this.y * other.y + this.z * other.z;
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
	 * Returns a new {@link Vector3D} object with its components set to the x, y
	 * and z components of this {@link Vector3D} scaled by the given factor.
	 *
	 * @param f
	 *            The scaling factor.
	 * @return a new {@link Vector3D} object with its components set to the x, y
	 *         and z components of this {@link Vector3D} scaled by the given
	 *         factor
	 */
	public Vector3D getScaled(double f) {
		return new Vector3D(x * f, y * f, z * f);
	}

	/**
	 * Returns a new {@link Vector3D} object with its components set to the
	 * difference of the individual x, y and z components of this
	 * {@link Vector3D} and the given other {@link Vector3D}.
	 *
	 * @param other
	 *            The {@link Vector3D} which is subtracted from this
	 *            {@link Vector3D}.
	 * @return a new {@link Vector3D} object representing the difference of this
	 *         {@link Vector3D} and the given other {@link Vector3D}
	 */
	public Vector3D getSubtracted(Vector3D other) {
		return new Vector3D(this.x - other.x, this.y - other.y,
				this.z - other.z);
	}

	@Override
	public int hashCode() {
		// cannot generate a good hash-code because of the imprecise
		// comparisons
		return 0;
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

	@Override
	public String toString() {
		return "Vector3D(" + x + ", " + y + ", " + z + ")";
	}
}

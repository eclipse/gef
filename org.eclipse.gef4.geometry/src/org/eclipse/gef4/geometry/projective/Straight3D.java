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
package org.eclipse.gef4.geometry.projective;

import org.eclipse.gef4.geometry.euclidean.Straight;
import org.eclipse.gef4.geometry.planar.Point;

/**
 * <p>
 * A two-dimensional infinite line that is defined by three coordinates of which
 * the third is a so called homogeneous coordinate. Calculations are easier to
 * do on such lines:
 * <ul>
 * <li>the point of intersection between two lines is the cross product of their
 * respective three dimensional vectors</li>
 * <li>the distance from a point to the line is the scalar product of both three
 * dimensional vectors</li>
 * </ul>
 * </p>
 * <p>
 * This is the complement to the {@link Vector3D} which represents a
 * {@link Point} with a third, homogeneous coordinate.
 * </p>
 * 
 * @author wienand
 */
public final class Straight3D {
	private Vector3D sp, line;
	private double f;

	private Straight3D() {
	}

	/**
	 * Constructs a new {@link Straight3D} through the given start and end
	 * {@link Vector3D}s.
	 * 
	 * @param start
	 * @param end
	 * @return a new {@link Straight3D} through start and end {@link Vector3D}s
	 */
	public static Straight3D through(Vector3D start, Vector3D end) {
		Straight3D self = new Straight3D();
		self.sp = start;
		self.line = self.sp.getCrossProduct(end);

		self.f = Math.sqrt(self.line.x * self.line.x + self.line.y
				* self.line.y);
		if (self.f == 0d) {
			return null;
		}

		return self;
	}

	/**
	 * Returns the orthogonal {@link Straight3D} through this {@link Straight3D}
	 * 's start {@link Vector3D}.
	 * 
	 * @return the orthogonal {@link Straight3D} through this {@link Straight3D}
	 *         's start {@link Vector3D}
	 */
	public Straight3D getOrtho() {
		return getOrtho(sp);
	}

	/**
	 * Returns the orthogonal {@link Straight3D} through the given
	 * {@link Vector3D}.
	 * 
	 * @param vp
	 * @return the orthogonal {@link Straight3D} through the given
	 *         {@link Vector3D}
	 */
	public Straight3D getOrtho(Vector3D vp) {
		return Straight3D.through(vp, new Vector3D(vp.x + line.x,
				vp.y + line.y, vp.z));
	}

	/**
	 * Returns the clock-wise signed distance of the given {@link Vector3D} to
	 * this {@link Straight3D}. The clock-wise signed distance is the dot
	 * product of the both {@link Vector3D}s divided by the length of the line's
	 * (x,y) vector: <code>|(x,y)|</code>.
	 * 
	 * @param vp
	 * @return the clock-wise signed distance of the {@link Vector3D} to this
	 *         {@link Straight3D}
	 */
	public double getSignedDistanceCW(Vector3D vp) {
		Point p = vp.toPoint();
		return (line.x * p.x + line.y * p.y + line.z) / f;
	}

	/**
	 * Returns the intersection between this and the given other
	 * {@link Straight3D}. The intersection is the cross product of both
	 * {@link Vector3D}s.
	 * 
	 * @param other
	 * @return the intersection between this and the given other
	 *         {@link Straight3D}
	 */
	public Vector3D getIntersection(Straight3D other) {
		return line.getCrossProduct(other.line);
	}

	/**
	 * Transfer this {@link Straight3D} into a representative {@link Straight}.
	 * 
	 * @return a representative {@link Straight}
	 */
	public Straight toStraight() {
		return new Straight(sp.toPoint(), sp.toPoint().getTranslated(
				new Point(line.y, -line.x)));
	}

}
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
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - javadoc comment enhancements
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.planar;

import java.awt.geom.NoninvertibleTransformException;

import org.eclipse.gef.geometry.convert.awt.AWT2Geometry;
import org.eclipse.gef.geometry.convert.awt.Geometry2AWT;
import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.euclidean.Vector;

/**
 * <p>
 * The {@link AffineTransform} class provides methods to create and modify
 * 2-dimensional affine transformations.
 * </p>
 * <p>
 * It delegates to the {@link java.awt.geom.AffineTransform} functionality.
 * </p>
 *
 * @author anyssen
 * @author mwienand
 *
 */
public class AffineTransform implements Cloneable {

	// TODO: implement affine transform locally to get rid of dependency on
	// awt.geom.

	private java.awt.geom.AffineTransform delegate = new java.awt.geom.AffineTransform();

	/**
	 * Creates a new {@link AffineTransform} with its transformation matrix set
	 * to the identity matrix.
	 */
	public AffineTransform() {
	}

	/**
	 * Creates a new {@link AffineTransform} with its transformation matrix set
	 * to the specified values. Note that rotation is a combination of shearing
	 * and scaling.
	 *
	 * @param m00
	 *            the value of the transformation matrix in row 0 and column 0
	 *            (x coordinate scaling)
	 * @param m10
	 *            the value of the transformation matrix in row 1 and column 0
	 *            (y coordinate shearing)
	 * @param m01
	 *            the value of the transformation matrix in row 0 and column 1
	 *            (x coordinate shearing)
	 * @param m11
	 *            the value of the transformation matrix in row 1 and column 1
	 *            (y coordinate scaling)
	 * @param m02
	 *            the value of the transformation matrix in row 0 and column 2
	 *            (x coordinate translation)
	 * @param m12
	 *            the value of the transformation matrix in row 1 and column 2
	 *            (y coordinate translation)
	 */
	public AffineTransform(double m00, double m10, double m01, double m11,
			double m02, double m12) {
		delegate = new java.awt.geom.AffineTransform(m00, m10, m01, m11, m02,
				m12);
	}

	/**
	 * Creates a new {@link AffineTransform} with its transformation matrix set
	 * to the values of the passed-in array. See the
	 * {@link AffineTransform#AffineTransform(double, double, double, double, double, double)}
	 * or the {@link java.awt.geom.AffineTransform#AffineTransform(double[])}
	 * method for a specification of the values in the array.
	 *
	 * @param flatmatrix
	 *            the values for the transformation matrix
	 * @see AffineTransform#AffineTransform(double, double, double, double,
	 *      double, double)
	 */
	public AffineTransform(double[] flatmatrix) {
		delegate = new java.awt.geom.AffineTransform(flatmatrix);
	}

	private AffineTransform(java.awt.geom.AffineTransform delegate) {
		this.delegate = delegate;
	}

	@Override
	public Object clone() {
		return delegate.clone();
	}

	/**
	 * Concatenates this {@link AffineTransform} and the given
	 * {@link AffineTransform}, multiplying the transformation matrix of this
	 * {@link AffineTransform} from the left with the transformation matrix of
	 * the other {@link AffineTransform}.
	 *
	 * @param Tx
	 *            the {@link AffineTransform} that is concatenated with this
	 *            {@link AffineTransform}
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform concatenate(AffineTransform Tx) {
		delegate.concatenate(Tx.delegate);
		return this;
	}

	/**
	 * Transforms an array of {@link Point}s specified by their coordinate
	 * values with this {@link AffineTransform} without applying the translation
	 * components of the transformation matrix of this {@link AffineTransform}.
	 *
	 * @param srcPts
	 *            the array of x and y coordinates specifying the {@link Point}s
	 *            that are transformed
	 * @param srcOff
	 *            the index of the <i>srcPts</i> array where the x coordinate of
	 *            the first {@link Point} to transform is found
	 * @param dstPts
	 *            the destination array of x and y coordinates for the result of
	 *            the transformation
	 * @param dstOff
	 *            the index of the <i>dstPts</i> array where the x coordinate of
	 *            the first transformed {@link Point} is stored
	 * @param numPts
	 *            the number of {@link Point}s to transform
	 */
	public void deltaTransform(double[] srcPts, int srcOff, double[] dstPts,
			int dstOff, int numPts) {
		delegate.deltaTransform(srcPts, srcOff, dstPts, dstOff, numPts);
	}

	/**
	 * Transforms the given {@link Point} with this {@link AffineTransform}
	 * without applying the translation components of the transformation matrix
	 * of this {@link AffineTransform}.
	 *
	 * @param pt
	 *            the {@link Point} to transform
	 * @return a new, transformed {@link Point}
	 */
	public Point deltaTransform(Point pt) {
		return AWT2Geometry.toPoint(
				delegate.deltaTransform(Geometry2AWT.toAWTPoint(pt), null));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AffineTransform) {
			return delegate.equals(((AffineTransform) obj).delegate);
		}
		return false;
	}

	/**
	 * Returns a copy of this {@link AffineTransform}.
	 *
	 * @return a copy of this {@link AffineTransform}
	 */
	public AffineTransform getCopy() {
		return new AffineTransform(getMatrix());
	}

	/**
	 * Computes the determinant of the transformation matrix of this
	 * {@link AffineTransform}.
	 *
	 * @return the determinant of the transformation matrix of this
	 *         {@link AffineTransform}
	 */
	public double getDeterminant() {
		return delegate.getDeterminant();
	}

	/**
	 * Creates a new {@link AffineTransform} that represents the inverse
	 * transformation of this {@link AffineTransform}.
	 *
	 * @return a new {@link AffineTransform} that represents the inverse
	 *         transformation of this {@link AffineTransform}
	 */
	public AffineTransform getInverse() {
		try {
			return new AffineTransform(delegate.createInverse());
		} catch (NoninvertibleTransformException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Returns the matrix component in the first row and first column.
	 *
	 * @return The matrix component in the first row and first column.
	 */
	public double getM00() {
		return delegate.getScaleX();
	}

	/**
	 * Returns the matrix component in the first row and second column.
	 *
	 * @return The matrix component in the first row and second column.
	 */
	public double getM01() {
		return delegate.getShearX();
	}

	/**
	 * Returns the matrix component in the second row and first column.
	 *
	 * @return The matrix component in the second row and first column.
	 */
	public double getM10() {
		return delegate.getShearY();
	}

	/**
	 * Returns the matrix component in the second row and second column.
	 *
	 * @return The matrix component in the second row and second column.
	 */
	public double getM11() {
		return delegate.getScaleY();
	}

	/**
	 * Returns the 6 specifiable elements of the transformation matrix of this
	 * {@link AffineTransform}.
	 *
	 * @return the 6 specifiable elements of the transformation matrix of this
	 *         {@link AffineTransform}
	 */
	public double[] getMatrix() {
		double[] flatmatrix = new double[6];
		delegate.getMatrix(flatmatrix);
		return flatmatrix;
	}

	/**
	 * Returns the rotation component of this {@link AffineTransform}.
	 *
	 * @return The rotation component of this {@link AffineTransform}.
	 */
	public Angle getRotation() {
		double rad = Math.atan2(getM01(), getM00());
		return Angle.fromRad(rad);
	}

	/**
	 * Returns the x coordinate scaling of this {@link AffineTransform}'s
	 * transformation matrix.
	 *
	 * @return the x coordinate scaling of this {@link AffineTransform}'s
	 *         transformation matrix
	 */
	public double getScaleX() {
		return Math.sqrt(getM00() * getM00() + getM10() * getM10());
	}

	/**
	 * Returns the y coordinate scaling of this {@link AffineTransform}'s
	 * transformation matrix.
	 *
	 * @return the y coordinate scaling of this {@link AffineTransform}'s
	 *         transformation matrix
	 */
	public double getScaleY() {
		return Math.sqrt(getM01() * getM01() + getM11() * getM11());
	}

	/**
	 * Transforms the given {@link Point} with this {@link AffineTransform} by
	 * multiplying the transformation matrix of this {@link AffineTransform}
	 * with the given {@link Point}.
	 *
	 * @param ptSrc
	 *            the {@link Point} to transform
	 * @return a new, transformed {@link Point}
	 */
	public Point getTransformed(Point ptSrc) {
		return AWT2Geometry.toPoint(
				delegate.transform(Geometry2AWT.toAWTPoint(ptSrc), null));
	}

	/**
	 * Transforms the given array of {@link Point}s with this
	 * {@link AffineTransform} by multiplying the transformation matrix of this
	 * {@link AffineTransform} individually with each of the given {@link Point}
	 * s.
	 *
	 * @param points
	 *            array of {@link Point}s to transform
	 * @return an array of new, transformed {@link Point}s
	 */
	public Point[] getTransformed(Point[] points) {
		Point[] result = new Point[points.length];

		for (int i = 0; i < points.length; i++) {
			result[i] = getTransformed(points[i]);
		}

		return result;
	}

	/**
	 * Returns the x coordinate translation of this {@link AffineTransform}'s
	 * transformation matrix.
	 *
	 * @return the x coordinate translation of this {@link AffineTransform}'s
	 *         transformation matrix
	 */
	public double getTranslateX() {
		return delegate.getTranslateX();
	}

	/**
	 * Returns the y coordinate translation of this {@link AffineTransform}'s
	 * transformation matrix.
	 *
	 * @return the y coordinate translation of this {@link AffineTransform}'s
	 *         transformation matrix
	 */
	public double getTranslateY() {
		return delegate.getTranslateY();
	}

	/**
	 * Returns the type of transformation represented by this
	 * {@link AffineTransform}. See the
	 * {@link java.awt.geom.AffineTransform#getType()} method for a
	 * specification of the return type of this method.
	 *
	 * @return the type of transformation represented by this
	 *         {@link AffineTransform}
	 */
	public int getType() {
		return delegate.getType();
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	/**
	 * Inverse transforms an array of {@link Point}s specified by their
	 * coordinate values with this {@link AffineTransform}.
	 *
	 * @param srcPts
	 *            the array of x and y coordinates specifying the {@link Point}s
	 *            that are inverse transformed
	 * @param srcOff
	 *            the index of the <i>srcPts</i> array where the x coordinate of
	 *            the first {@link Point} to inverse transform is found
	 * @param dstPts
	 *            the destination array of x and y coordinates for the result of
	 *            the inverse transformation
	 * @param dstOff
	 *            the index of the <i>dstPts</i> array where the x coordinate of
	 *            the first inverse transformed {@link Point} is stored
	 * @param numPts
	 *            the number of {@link Point}s to inverse transform
	 * @throws NoninvertibleTransformException
	 *             when this {@link AffineTransform} is not invertible.
	 */
	public void inverseTransform(double[] srcPts, int srcOff, double[] dstPts,
			int dstOff, int numPts) throws NoninvertibleTransformException {
		delegate.inverseTransform(srcPts, srcOff, dstPts, dstOff, numPts);
	}

	/**
	 * Inverse transforms the given {@link Point} with this
	 * {@link AffineTransform}.
	 *
	 * @param pt
	 *            the {@link Point} to inverse transform
	 * @return a new, inverse transformed {@link Point}
	 * @throws NoninvertibleTransformException
	 *             when this {@link AffineTransform} is not invertible.
	 */
	public Point inverseTransform(Point pt)
			throws NoninvertibleTransformException {
		return AWT2Geometry.toPoint(
				delegate.inverseTransform(Geometry2AWT.toAWTPoint(pt), null));
	}

	/**
	 * Inverts this {@link AffineTransform}.
	 *
	 * @return <code>this</code> for convenience
	 * @throws NoninvertibleTransformException
	 *             when this {@link AffineTransform} is not invertible.
	 */
	public AffineTransform invert() throws NoninvertibleTransformException {
		delegate.invert();
		return this;
	}

	/**
	 * Checks if the transformation matrix of this {@link AffineTransform}
	 * equals the identity matrix.
	 *
	 * @return <code>true</code> if the transformation matrix of this
	 *         {@link AffineTransform} equals the identity matrix, otherwise
	 *         <code>false</code>
	 */
	public boolean isIdentity() {
		return delegate.isIdentity();
	}

	/**
	 * Concatenates this {@link AffineTransform} and the given
	 * {@link AffineTransform} in reverse order, multiplying the transformation
	 * matrix of this {@link AffineTransform} from the right with the
	 * transformation matrix of the other {@link AffineTransform}.
	 *
	 * @param Tx
	 *            the {@link AffineTransform} that is concatenated with this
	 *            {@link AffineTransform} in reverse order
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform preConcatenate(AffineTransform Tx) {
		delegate.preConcatenate(Tx.delegate);
		return this;
	}

	/**
	 * Adds a rotation by an integer multiple of 90deg to the transformation
	 * matrix of this {@link AffineTransform}. The integer multiple of 90deg is
	 * specified by the given number of quadrants.
	 *
	 * @param numquadrants
	 *            the integer that defines the number of quadrants to rotate by
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform quadrantRotate(int numquadrants) {
		delegate.quadrantRotate(numquadrants);
		return this;
	}

	/**
	 * Adds a rotation by an integer multiple of 90deg around the {@link Point}
	 * specified by the given x and y coordinates to the transformation matrix
	 * of this {@link AffineTransform}.
	 *
	 * @param numquadrants
	 *            the integer that defines the number of quadrants to rotate by
	 * @param anchorx
	 *            the x coordinate of the {@link Point} to rotate around
	 * @param anchory
	 *            the y coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform quadrantRotate(int numquadrants, double anchorx,
			double anchory) {
		delegate.quadrantRotate(numquadrants, anchorx, anchory);
		return this;
	}

	/**
	 * Adds a rotation with the given angle (in radians) to the transformation
	 * matrix of this {@link AffineTransform}.
	 *
	 * @param theta
	 *            the rotation angle in radians
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform rotate(double theta) {
		delegate.rotate(theta);
		return this;
	}

	/**
	 * Adds a rotation to the transformation matrix of this
	 * {@link AffineTransform}. The given coordinates specify a {@link Vector}
	 * whose {@link Angle} to the x-axis is the applied rotation {@link Angle}.
	 *
	 * @param vecx
	 *            the x coordinate of the {@link Vector} specifying the rotation
	 *            {@link Angle}
	 * @param vecy
	 *            the y coordinate of the {@link Vector} specifying the rotation
	 *            {@link Angle}
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform rotate(double vecx, double vecy) {
		delegate.rotate(vecx, vecy);
		return this;
	}

	/**
	 * Adds a rotation with the given angle (in radians) around the
	 * {@link Point} specified by the given x and y coordinates to the
	 * transformation matrix of this {@link AffineTransform}.
	 *
	 * @param theta
	 *            the rotation angle in radians
	 * @param anchorx
	 *            the x coordinate of the {@link Point} to rotate around
	 * @param anchory
	 *            the y coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform rotate(double theta, double anchorx,
			double anchory) {
		delegate.rotate(theta, anchorx, anchory);
		return this;
	}

	// TODO: Add the possibility to pass Angle objects instead of simple double
	// values.

	/**
	 * Adds a rotation around a {@link Point} to the transformation matrix of
	 * this {@link AffineTransform}. The given coordinates specify a
	 * {@link Vector} whose {@link Angle} to the x-axis is the applied rotation
	 * {@link Angle} and the anchor {@link Point} for the rotation.
	 *
	 * @param vecx
	 *            the x coordinate of the {@link Vector} specifying the rotation
	 *            {@link Angle}
	 * @param vecy
	 *            the y coordinate of the {@link Vector} specifying the rotation
	 *            {@link Angle}
	 * @param anchorx
	 *            the x coordinate of the {@link Point} to rotate around
	 * @param anchory
	 *            the y coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform rotate(double vecx, double vecy, double anchorx,
			double anchory) {
		delegate.rotate(vecx, vecy, anchorx, anchory);
		return this;
	}

	/**
	 * Adds an x and y scaling to the transformation matrix of this
	 * {@link AffineTransform}.
	 *
	 * @param sx
	 *            the x scaling factor added to the transformation matrix of
	 *            this {@link AffineTransform}
	 * @param sy
	 *            the y scaling factor added to the transformation matrix of
	 *            this {@link AffineTransform}
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform scale(double sx, double sy) {
		delegate.scale(sx, sy);
		return this;
	}

	/**
	 * Sets the transformation matrix of this {@link AffineTransform} to the
	 * identity matrix.
	 *
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform setToIdentity() {
		delegate.setToIdentity();
		return this;
	}

	/**
	 * Sets the transformation matrix of this {@link AffineTransform} to a pure
	 * rotation matrix where the rotation angle is an integer multiple of 90deg.
	 *
	 * @param numquadrants
	 *            the integer that defines the number of quadrants to rotate by
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform setToQuadrantRotation(int numquadrants) {
		delegate.setToQuadrantRotation(numquadrants);
		return this;
	}

	/**
	 * Sets the transformation matrix of this {@link AffineTransform} to a pure
	 * rotation and translation matrix where the rotation angle is an integer
	 * multiple of 90deg and the rotation is around the {@link Point} specified
	 * by the given x and y coordinates.
	 *
	 * @param numquadrants
	 *            the integer that defines the number of quadrants to rotate by
	 * @param anchorx
	 *            the x coordinate of the {@link Point} to rotate around
	 * @param anchory
	 *            the y coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform setToQuadrantRotation(int numquadrants,
			double anchorx, double anchory) {
		delegate.setToQuadrantRotation(numquadrants, anchorx, anchory);
		return this;
	}

	/**
	 * Sets the transformation matrix of this {@link AffineTransform} to a pure
	 * rotation matrix by the given angle specified in radians.
	 *
	 * @param theta
	 *            the rotation angle (in radians)
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform setToRotation(double theta) {
		delegate.setToRotation(theta);
		return this;
	}

	/**
	 * Sets the transformation matrix of this {@link AffineTransform} to a pure
	 * rotation matrix. The given x and y coordinates specify a {@link Vector}
	 * whose {@link Angle} to the x-axis defines the rotation {@link Angle}.
	 *
	 * @param vecx
	 *            the x coordinate of the {@link Vector} whose {@link Angle} to
	 *            the x-axis defines the rotation {@link Angle}
	 * @param vecy
	 *            the y coordinate of the {@link Vector} whose {@link Angle} to
	 *            the x-axis defines the rotation {@link Angle}
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform setToRotation(double vecx, double vecy) {
		delegate.setToRotation(vecx, vecy);
		return this;
	}

	/**
	 * Sets the transformation matrix of this {@link AffineTransform} to a pure
	 * rotation and translation matrix. Thus, the resulting transformation
	 * matrix rotates {@link Point}s by the given angle (in radians) around the
	 * {@link Point} specified by the given x and y coordinates.
	 *
	 * @param theta
	 *            the rotation angle (in radians)
	 * @param anchorx
	 *            the x coordinate of the {@link Point} to rotate around
	 * @param anchory
	 *            the y coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform setToRotation(double theta, double anchorx,
			double anchory) {
		delegate.setToRotation(theta, anchorx, anchory);
		return this;
	}

	/**
	 * Sets the transformation matrix of this {@link AffineTransform} to a pure
	 * rotation and translation matrix. The firstly given x and y coordinates
	 * specify a {@link Vector} whose {@link Angle} to the x-axis defines the
	 * rotation {@link Angle}. The secondly given x and y coordinates specify
	 * the {@link Point} to rotate around.
	 *
	 * @param vecx
	 *            the x coordinate of the {@link Vector} whose {@link Angle} to
	 *            the x-axis defines the rotation {@link Angle}
	 * @param vecy
	 *            the y coordinate of the {@link Vector} whose {@link Angle} to
	 *            the x-axis defines the rotation {@link Angle}
	 * @param anchorx
	 *            the x coordinate of the {@link Point} to rotate around
	 * @param anchory
	 *            the y coordinate of the {@link Point} to rotate around
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform setToRotation(double vecx, double vecy,
			double anchorx, double anchory) {
		delegate.setToRotation(vecx, vecy, anchorx, anchory);
		return this;
	}

	/**
	 * Sets the transformation matrix of this {@link AffineTransform} to a pure
	 * scaling matrix.
	 *
	 * @param sx
	 *            the x scaling factor
	 * @param sy
	 *            the y scaling factor
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform setToScale(double sx, double sy) {
		delegate.setToScale(sx, sy);
		return this;
	}

	/**
	 * Sets the transformation matrix of this {@link AffineTransform} to a pure
	 * shearing matrix.
	 *
	 * @param shx
	 *            the x shearing factor
	 * @param shy
	 *            the y shearing factor
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform setToShear(double shx, double shy) {
		delegate.setToShear(shx, shy);
		return this;
	}

	/**
	 * Sets the transformation matrix of this {@link AffineTransform} to a pure
	 * translation matrix that translates {@link Point}s by the given x and y
	 * values.
	 *
	 * @param tx
	 *            the x translation value
	 * @param ty
	 *            the y translation value
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform setToTranslation(double tx, double ty) {
		delegate.setToTranslation(tx, ty);
		return this;
	}

	/**
	 * Sets the transformation matrix of this {@link AffineTransform} to the
	 * transformation matrix of the given {@link AffineTransform}.
	 *
	 * @param Tx
	 *            the {@link AffineTransform} specifying the new transformation
	 *            matrix of this {@link AffineTransform}
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform setTransform(AffineTransform Tx) {
		delegate.setTransform(Tx.delegate);
		return this;
	}

	/**
	 * Sets the respective values of the transformation matrix of this
	 * {@link AffineTransform} to the supplied ones. Note that rotation is a
	 * combination of shearing and scaling.
	 *
	 * @param m00
	 *            the value of the transformation matrix in row 0 and column 0
	 *            (x coordinate scaling)
	 * @param m10
	 *            the value of the transformation matrix in row 1 and column 0
	 *            (y coordinate shearing)
	 * @param m01
	 *            the value of the transformation matrix in row 0 and column 1
	 *            (x coordinate shearing)
	 * @param m11
	 *            the value of the transformation matrix in row 1 and column 1
	 *            (y coordinate scaling)
	 * @param m02
	 *            the value of the transformation matrix in row 0 and column 2
	 *            (x coordinate translation)
	 * @param m12
	 *            the value of the transformation matrix in row 1 and column 2
	 *            (y coordinate translation)
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform setTransform(double m00, double m10, double m01,
			double m11, double m02, double m12) {
		delegate.setTransform(m00, m10, m01, m11, m02, m12);
		return this;
	}

	/**
	 * Adds an x and y shearing to the transformation matrix of this
	 * {@link AffineTransform}.
	 *
	 * @param shx
	 *            the x shearing factor added to the transformation matrix of
	 *            this {@link AffineTransform}
	 * @param shy
	 *            the y shearing factor added to the transformation matrix of
	 *            this {@link AffineTransform}
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform shear(double shx, double shy) {
		delegate.shear(shx, shy);
		return this;
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	/**
	 * Sets the translation values of the x and y coordinates of the
	 * transformation matrix of this {@link AffineTransform}.
	 *
	 * @param tx
	 *            the x coordinate translation
	 * @param ty
	 *            the y coordinate translation
	 * @return <code>this</code> for convenience
	 */
	public AffineTransform translate(double tx, double ty) {
		delegate.translate(tx, ty);
		return this;
	}

}

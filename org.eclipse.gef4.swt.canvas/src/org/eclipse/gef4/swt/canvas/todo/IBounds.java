/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swt.canvas.todo;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IShape;

/**
 * An {@link IBounds} object consists of an {@link IShape} that encloses some
 * graphical object and an {@link AffineTransform} that represents the
 * transformation matrix used to transform the graphical object before drawing
 * it. The bounds {@link IShape} is transformed by the {@link AffineTransform}
 * to yield the transformed bounds shape ({@link #getTransformedShape()}).
 * 
 * @author mwienand
 * 
 */
public interface IBounds extends Cloneable {

	/**
	 * Returns a copy of this {@link IBounds} object.
	 * 
	 * @return a copy of this {@link IBounds}
	 */
	IBounds getCopy();

	/**
	 * Returns (a copy of) the current bounds {@link IShape}. The returned
	 * object may not be <code>null</code>.
	 * 
	 * @return (a copy of) the current bounds {@link IShape}
	 */
	IShape getShape();

	/**
	 * Returns the current bounds {@link IShape} by reference. The returned
	 * object may not be <code>null</code>.
	 * 
	 * @return the current bounds {@link IShape} by reference
	 */
	IShape getShapeByReference();

	/**
	 * Returns (a copy of) the current bounds {@link AffineTransform}. The
	 * returned object may not be <code>null</code>.
	 * 
	 * @return (a copy of) the current bounds {@link AffineTransform}
	 */
	AffineTransform getTransform();

	/**
	 * Returns the current bounds {@link AffineTransform} by reference. The
	 * returned object may not be <code>null</code>.
	 * 
	 * @return the current bounds {@link AffineTransform} by reference
	 */
	AffineTransform getTransformByReference();

	/**
	 * Returns the transformed bounds shape. The current bounds shape (
	 * {@link #getShape()}) is transformed by the current bounds transformation
	 * ({@link #getTransform()}). The returned object may not be
	 * <code>null</code>.
	 * 
	 * @return the transformed bounds shape
	 */
	IShape getTransformedShape();

	/**
	 * Returns the transformed bounds shape by reference. Note that the returned
	 * object may be <code>null</code>, however, it is guaranteed that the
	 * returned object is not <code>null</code> after a call to
	 * {@link #getTransformedShape()} when no setters are called in-between. In
	 * addition, the returned transformed {@link IShape} is only valid as long
	 * as no setter is called.
	 * 
	 * @return the transformed bounds shape by reference
	 */
	IShape getTransformedShapeByReference();

	/**
	 * Sets the current bounds {@link IShape} to (a copy of) the given object.
	 * 
	 * @param shape
	 *            the new bounding {@link IShape}, may not be <code>null</code>
	 */
	void setShape(IShape shape);

	/**
	 * Sets the current bounds {@link IShape} to the given object by reference.
	 * 
	 * @param shape
	 *            the new bounding {@link IShape}, may not be <code>null</code>
	 */
	void setShapeByReference(IShape shape);

	/**
	 * Sets the current bounds {@link AffineTransform} to (a copy of) the given
	 * object.
	 * 
	 * @param at
	 *            the new bounds {@link AffineTransform}, may not be
	 *            <code>null</code>
	 */
	void setTransform(AffineTransform at);

	/**
	 * Sets the current bounds {@link AffineTransform} to the given object by
	 * reference.
	 * 
	 * @param at
	 *            the new bounds {@link AffineTransform}, may not be
	 *            <code>null</code>
	 */
	void setTransformByReference(AffineTransform at);

}

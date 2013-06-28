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
package org.eclipse.gef4.swt.fx;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IShape;

public abstract class AbstractBounds<S extends AbstractBounds<?, ?, ?>, T extends IShape, U extends IShape>
		implements IBounds {

	private AffineTransform transform;
	private T shape;
	private U transformed;

	@Override
	protected S clone() {
		return getCopy();
	}

	/**
	 * Returns a new empty instance of the child class. Only child class
	 * specific attributes have to be copied to the new instance by the
	 * implementation. The {@link #getCopy()} and {@link #clone()} methods use
	 * this method to get the instance and set the current {@link IShape} and
	 * {@link AffineTransform} by calling {@link #setShape(IShape)} and
	 * {@link #setTransform(AffineTransform)} respectively.
	 * 
	 * @return a new instance of the child class with specific attributes copied
	 *         to it
	 */
	protected abstract S copy();

	@Override
	public S getCopy() {
		S copy = copy();
		copy.setShape(shape);
		copy.setTransform(transform);
		return copy;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getShape() {
		return (T) shape.getCopy();
	}

	@Override
	public T getShapeByReference() {
		return shape;
	}

	@Override
	public AffineTransform getTransform() {
		return transform.getCopy();
	}

	@Override
	public AffineTransform getTransformByReference() {
		return transform;
	}

	@SuppressWarnings("unchecked")
	@Override
	public U getTransformedShape() {
		if (transformed == null) {
			transformed = transform(shape);
		}
		return (U) transformed.getCopy();
	}

	@Override
	public U getTransformedShapeByReference() {
		return transformed;
	}

	/**
	 * Determines whether the passed-in {@link IShape} can be used to describe
	 * the enclosing area represented by this {@link IBounds}. The passed-in
	 * {@link IShape} is guaranteed not to be <code>null</code>.
	 * 
	 * @param shape
	 *            the {@link IShape} in question
	 * @return <code>true</code> for a correct {@link IShape}, otherwise
	 *         <code>false</code>
	 */
	protected abstract boolean isShapeOk(IShape shape);

	@SuppressWarnings("unchecked")
	@Override
	public void setShape(IShape shape) {
		if (shape != null && isShapeOk(shape)) {
			this.shape = (T) ((T) shape).getCopy();
			transformed = null;
		} else {
			throw new IllegalArgumentException("The given IShape <" + shape
					+ "> is of wrong type!");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setShapeByReference(IShape shape) {
		if (shape == null || !isShapeOk(shape)) {
			throw new IllegalArgumentException("The given IShape <" + shape
					+ "> is of wrong type!");
		}
		this.shape = (T) shape;
	}

	@Override
	public void setTransform(AffineTransform at) {
		if (at == null) {
			throw new IllegalArgumentException(
					"setTransform((AffineTransform) null) is not allowed. To reset the transformation matrix: setTransform(new AffineTransform())");
		}
		transform = at.getCopy();
		transformed = null;
	}

	@Override
	public void setTransformByReference(AffineTransform at) {
		transform = at;
	}

	/**
	 * Returns the result of transforming the given {@link IShape} using the
	 * current {@link AffineTransform} which is accessible via
	 * {@link #getTransform()} and {@link #getTransformByReference()}.
	 * 
	 * @param shape
	 *            the {@link IShape} to transform
	 * @return the result of transforming the current {@link IShape} using the
	 *         current {@link AffineTransform}
	 */
	protected abstract U transform(T shape);

}

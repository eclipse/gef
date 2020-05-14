/*******************************************************************************
 * Copyright (c) 2012, 2016 itemis AG and others.
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
package org.eclipse.gef.geometry.planar;

/**
 * <p>
 * The {@link IScalable} interface collects all scaling short-cut methods.
 * </p>
 * 
 * <p>
 * The {@link #scale(double)}, {@link #scale(double, double)},
 * {@link #scale(double, Point)}, {@link #scale(double, double, double)},
 * {@link #scale(double, double, Point)} and
 * {@link #scale(double, double, double, double)} methods are directly applied
 * to the calling object. They scale it by the given factor(s) around the given
 * {@link Point} or an appropriate default.
 * </p>
 * 
 * <p>
 * On the other hand, the {@link #getScaled(double)},
 * {@link #getScaled(double, double)}, {@link #getScaled(double, Point)},
 * {@link #getScaled(double, double, double)},
 * {@link #getScaled(double, double, Point)} and
 * {@link #getScaled(double, double, double, double)} methods are applied to a
 * copy of the calling object.
 * </p>
 * 
 * <p>
 * If you do not specify the relative {@link Point} for the scaling, the
 * implementation will appropriately choose one. In most cases, this will be the
 * center of the scaled object.
 * </p>
 * 
 * @param <T>
 *            the implementing type
 * 
 * @author mwienand
 * 
 */
public interface IScalable<T extends IGeometry> {

	/**
	 * Scales a copy of the calling object by the given factor relative to its
	 * center {@link Point}.
	 * 
	 * @param factor
	 *            scale-factor
	 * @return the new, scaled object
	 */
	public T getScaled(double factor);

	/**
	 * Scales a copy of the calling object by the given factors relative to its
	 * center {@link Point}.
	 * 
	 * @param fx
	 *            x-scale-factor
	 * @param fy
	 *            y-scale-factor
	 * @return the new, scaled object
	 */
	public T getScaled(double fx, double fy);

	/**
	 * Scales a copy of the calling object by the given factor relative to the
	 * given center {@link Point} (cx, cy).
	 * 
	 * @param factor
	 *            scale-factor
	 * @param cx
	 *            x-coordinate of the relative {@link Point} for the scaling
	 * @param cy
	 *            y-coordinate of the relative {@link Point} for the scaling
	 * @return the new, scaled object
	 */
	public T getScaled(double factor, double cx, double cy);

	/**
	 * Scales a copy of the calling object by the given factors relative to the
	 * given center {@link Point} (cx, cy).
	 * 
	 * @param fx
	 *            x-scale-factor
	 * @param fy
	 *            y-scale-factor
	 * @param cx
	 *            x-coordinate of the relative {@link Point} for the scaling
	 * @param cy
	 *            y-coordinate of the relative {@link Point} for the scaling
	 * @return the new, scaled object
	 */
	public T getScaled(double fx, double fy, double cx, double cy);

	/**
	 * Scales a copy of the calling object by the given factors relative to the
	 * given center {@link Point}.
	 * 
	 * @param fx
	 *            x-scale-factor
	 * @param fy
	 *            y-scale-factor
	 * @param center
	 *            relative {@link Point} for the scaling
	 * @return the new, scaled object
	 */
	public T getScaled(double fx, double fy, Point center);

	/**
	 * Scales a copy of the calling object by the given factor relative to the
	 * given center {@link Point}.
	 * 
	 * @param factor
	 *            scale-factor
	 * @param center
	 *            relative {@link Point} for the scaling
	 * @return the new, scaled object
	 */
	public T getScaled(double factor, Point center);

	/**
	 * Scales the calling object by the given factor relative to its center
	 * {@link Point}.
	 * 
	 * @param factor
	 *            scale-factor
	 * @return <code>this</code> for convenience
	 */
	public T scale(double factor);

	/**
	 * Scales the calling object by the given factors relative to the given
	 * center {@link Point}.
	 * 
	 * @param fx
	 *            x-scale-factor
	 * @param fy
	 *            y-scale-factor
	 * @return <code>this</code> for convenience
	 */
	public T scale(double fx, double fy);

	/**
	 * Scales the calling object by the given factor relative to the given
	 * center {@link Point} (cx, cy).
	 * 
	 * @param factor
	 *            scale-factor
	 * @param cx
	 *            x-coordinate of the relative {@link Point} for the scaling
	 * @param cy
	 *            y-coordinate of the relative {@link Point} for the scaling
	 * @return <code>this</code> for convenience
	 */
	public T scale(double factor, double cx, double cy);

	/**
	 * Scales the calling object by the given factors relative to the given
	 * center {@link Point} (cx, cy).
	 * 
	 * @param fx
	 *            x-scale-factor
	 * @param fy
	 *            y-scale-factor
	 * @param cx
	 *            x-coordinate of the relative {@link Point} for the scaling
	 * @param cy
	 *            y-coordinate of the relative {@link Point} for the scaling
	 * @return <code>this</code> for convenience
	 */
	public T scale(double fx, double fy, double cx, double cy);

	/**
	 * Scales the calling object by the given factors relative to the given
	 * center {@link Point}.
	 * 
	 * @param fx
	 *            x-scale-factor
	 * @param fy
	 *            y-scale-factor
	 * @param center
	 *            relative {@link Point} for the scaling
	 * @return <code>this</code> for convenience
	 */
	public T scale(double fx, double fy, Point center);

	/**
	 * Scales the calling object by the given factor relative to the given
	 * center {@link Point}.
	 * 
	 * @param factor
	 *            scale-factor
	 * @param center
	 *            relative {@link Point} for the scaling
	 * @return <code>this</code> for convenience
	 */
	public T scale(double factor, Point center);

}

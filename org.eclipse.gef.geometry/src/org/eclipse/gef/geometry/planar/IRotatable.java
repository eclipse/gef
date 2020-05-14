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

import org.eclipse.gef.geometry.euclidean.Angle;

/**
 * <p>
 * The {@link IRotatable} interface collects the out-of-place rotation short-cut
 * methods.
 * </p>
 * 
 * <p>
 * Rotation cannot be applied directly to all {@link IGeometry}s. For example,
 * {@link Rectangle}, {@link Ellipse}, {@link Region} and
 * {@link RoundedRectangle} cannot be slanted. Therefore, you have to specify
 * the result type for the rotation methods via a type parameter.
 * </p>
 * 
 * <p>
 * There are two directions of rotation: clock-wise (CW) and counter-clock-wise
 * (CCW). The individual method names reflect the direction of rotation that is
 * used. These are the rotation methods: {@link #getRotatedCCW(Angle)},
 * {@link #getRotatedCCW(Angle, Point)},
 * {@link #getRotatedCCW(Angle, double, double)}, {@link #getRotatedCW(Angle)},
 * {@link #getRotatedCW(Angle, Point)},
 * {@link #getRotatedCW(Angle, double, double)}.
 * </p>
 * 
 * <p>
 * If you do not specify a {@link Point} to rotate around, the implementation
 * can appropriately choose one. In most cases, this will be the center
 * {@link Point} of the rotated object.
 * </p>
 * 
 * @param <T>
 *            type of the rotation results
 * 
 * @author mwienand
 * 
 */
public interface IRotatable<T extends IGeometry> {

	/**
	 * Rotates the calling object by specified {@link Angle} counter-clock-wise
	 * (CCW) around its center {@link Point}. Does not necessarily return an
	 * object of the same type.
	 * 
	 * @param angle
	 *            rotation {@link Angle}
	 * @return an {@link IGeometry} representing the result of the rotation
	 */
	public T getRotatedCCW(Angle angle);

	/**
	 * Rotates the calling object by the specified {@link Angle}
	 * counter-clock-wise (CCW) around the specified center {@link Point} (cx,
	 * cy). Does not necessarily return an object of the same type.
	 * 
	 * @param angle
	 *            rotation {@link Angle}
	 * @param cx
	 *            x-coordinate of the relative {@link Point} for the rotation
	 * @param cy
	 *            y-coordinate of the relative {@link Point} for the rotation
	 * @return an {@link IGeometry} representing the result of the rotation
	 */
	public T getRotatedCCW(Angle angle, double cx, double cy);

	/**
	 * Rotates the calling object by the specified {@link Angle}
	 * counter-clock-wise (CCW) around the specified center {@link Point}. Does
	 * not necessarily return an object of the same type.
	 * 
	 * @param angle
	 *            rotation {@link Angle}
	 * @param center
	 *            relative {@link Point} for the rotation
	 * @return an {@link IGeometry} representing the result of the rotation
	 */
	public T getRotatedCCW(Angle angle, Point center);

	/**
	 * Rotates the calling object by specified {@link Angle} clock-wise (CW)
	 * around its center {@link Point}. Does not necessarily return an object of
	 * the same type.
	 * 
	 * @param angle
	 *            rotation {@link Angle}
	 * @return an {@link IGeometry} representing the result of the rotation
	 */
	public T getRotatedCW(Angle angle);

	/**
	 * Rotates the calling object by the specified {@link Angle} clock-wise (CW)
	 * around the specified center {@link Point} (cx, cy). Does not necessarily
	 * return an object of the same type.
	 * 
	 * @param angle
	 *            rotation {@link Angle}
	 * @param cx
	 *            x-coordinate of the relative {@link Point} for the rotation
	 * @param cy
	 *            y-coordinate of the relative {@link Point} for the rotation
	 * @return an {@link IGeometry} representing the result of the rotation
	 */
	public T getRotatedCW(Angle angle, double cx, double cy);

	/**
	 * Rotates the calling object by the specified {@link Angle} clock-wise (CW)
	 * around the specified center {@link Point}. Does not necessarily return an
	 * object of the same type.
	 * 
	 * @param angle
	 *            rotation {@link Angle}
	 * @param center
	 *            relative {@link Point} for the rotation
	 * @return an {@link IGeometry} representing the result of the rotation
	 */
	public T getRotatedCW(Angle angle, Point center);

}

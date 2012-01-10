/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.shapes;

import java.io.Serializable;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.transform.AffineTransform;

/**
 * Common abstraction over all geometric shapes.
 * 
 * @author anyssen
 * 
 */
public interface Geometry extends Cloneable, Serializable {

	/**
	 * Returns whether the given {@link Point} is contained within this
	 * {@link Geometry}. This includes the case that the {@link Point} lies on
	 * the border of this {@link Geometry}.
	 * 
	 * @param p
	 *            The {@link Point} being tested for containment
	 * @return <code>true</code> if the {@link Point} is contained within this
	 *         {@link Geometry}, <code>false</code> otherwise.
	 */
	boolean contains(final Point p);

	/**
	 * Returns <code>true</code> if the given {@link Rectangle} is contained
	 * within {@link Geometry}, <code>false</code> otherwise.
	 * 
	 * @param r
	 *            The {@link Rectangle} to test
	 * @return <code>true</code> if the {@link Rectangle} is fully contained
	 *         within this {@link Geometry}
	 */
	boolean contains(final Rectangle r);

	/**
	 * Returns the smallest {@link Rectangle} fully enclosing this
	 * {@link Geometry}.
	 * 
	 * @return A new {@link Rectangle} object that fully encloses this
	 *         {@link Geometry}
	 */
	Rectangle getBounds();

	/**
	 * Returns a new {@link Geometry}, which represents the given
	 * {@link Geometry} after the application of the given
	 * {@link AffineTransform}. In case the {@link AffineTransform} may be
	 * performed type intrinsic (e.g. scaling on a {@link Rectangle}), an object
	 * of the same type is returned.
	 * 
	 * @param t
	 *            The {@link AffineTransform} to be applied
	 * @return A new {@link Geometry} object representing this {@link Geometry}
	 *         after the application of the given {@link AffineTransform}.
	 */
	Geometry getTransformed(final AffineTransform t);

	/**
	 * Returns <code>true</code> if the input Rectangle intersects this
	 * Geometry, i.e. there is at least one common point. This includes the case
	 * that the given rectangle is fully contained.
	 * 
	 * @param r
	 *            The {@link Rectangle} for the intersection test
	 * @return <code>true</code> if the input {@link Rectangle} and this
	 *         {@link Geometry} have at least one common point.
	 */
	boolean intersects(final Rectangle r);

	/**
	 * Converts this {@link Geometry} into a {@link Path} representation.
	 * 
	 * @return A new {@link Path} representation for this {@link Geometry}.
	 */
	Path toPath();

}

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
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef.geometry.planar;

import java.io.Serializable;

/**
 * <p>
 * A glance at the list of implementing classes reveals that the
 * {@link IGeometry} interface bundles all the basic common methods for planar
 * geometric objects. These methods enable you to test if a {@link Point}
 * belongs to an {@link IGeometry} using the {@link #contains(Point)} method.
 * The {@link #getBounds()} method returns a bounding box of the
 * {@link IGeometry} that you call that method on. Moreover, you can bake a copy
 * of an {@link IGeometry} using its {@link #getCopy()} method. To apply an
 * {@link AffineTransform} to an {@link IGeometry}, use the
 * {@link #getTransformed(AffineTransform)} method. Additionally, every
 * {@link IGeometry} can be transfered into a {@link Path} by using the
 * {@link #toPath()} method. And you can check if two {@link IGeometry}s are
 * touching each other, i.e. they have at least one {@link Point} in common, via
 * the {@link #touches(IGeometry)} method.
 * </p>
 * 
 * @author anyssen
 * @author mwienand
 * 
 */
public interface IGeometry extends Cloneable, Serializable {

	/**
	 * Returns whether the given {@link Point} is contained within this
	 * {@link IGeometry}. This includes the case that the {@link Point} lies on
	 * the border of this {@link IGeometry}.
	 * 
	 * @param p
	 *            The {@link Point} being tested for containment
	 * @return <code>true</code> if the {@link Point} is contained within this
	 *         {@link IGeometry}, <code>false</code> otherwise.
	 */
	boolean contains(final Point p);

	/**
	 * Returns the smallest {@link Rectangle} fully enclosing this
	 * {@link IGeometry}.
	 * 
	 * @return A new {@link Rectangle} object that fully encloses this
	 *         {@link IGeometry}
	 */
	Rectangle getBounds();

	// TODO: getTightBounds() : Polygon

	/**
	 * Returns a new identical copy of this {@link IGeometry}.
	 * 
	 * @return a copy identical to this {@link IGeometry}
	 */
	IGeometry getCopy();

	/**
	 * Returns a new {@link IGeometry}, which represents the given
	 * {@link IGeometry} after the application of the given
	 * {@link AffineTransform}. In case the {@link AffineTransform} may be
	 * performed type intrinsic (e.g. scaling on a {@link Rectangle}), an object
	 * of the same type is returned.
	 * 
	 * @param t
	 *            The {@link AffineTransform} to be applied
	 * @return A new {@link IGeometry} object representing this
	 *         {@link IGeometry} after the application of the given
	 *         {@link AffineTransform}.
	 */
	IGeometry getTransformed(final AffineTransform t);

	/**
	 * Converts this {@link IGeometry} into a {@link Path} representation.
	 * 
	 * @return A new {@link Path} representation for this {@link IGeometry}.
	 */
	Path toPath();

	/**
	 * Returns <code>true</code> if the input {@link IGeometry} touches this
	 * {@link IGeometry}, i.e. there is at least one common point.
	 * 
	 * @param g
	 *            The {@link IGeometry} for the intersection test
	 * @return <code>true</code> if the input {@link IGeometry} and this
	 *         {@link IGeometry} have at least one common point.
	 */
	boolean touches(IGeometry g);

}

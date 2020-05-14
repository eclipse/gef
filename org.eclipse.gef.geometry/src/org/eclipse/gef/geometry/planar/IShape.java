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

/**
 * An {@link IShape} is the representation of an areal geometric object. It is
 * thus the counterpart of an {@link ICurve}.
 * <p>
 * An {@link IShape} allows to test if another {@link IGeometry} is fully
 * contained by it ({@link #contains(IGeometry)}). Moreover its (closed) outline
 * can be retrieved as an whole ({@link #getOutline()}) or in the form of
 * individual segments ({@link #getOutlineSegments()}. As all other
 * {@link IGeometry}s, an {@link IShape} may be transformed (
 * {@link #getTransformed(AffineTransform)}) into another {@link IShape}.
 *
 * @author anyssen
 * @author mwienand
 *
 */
public interface IShape extends IGeometry {

	/**
	 * Tests whether the given {@link IGeometry} is fully contained by this
	 * {@link IShape}.
	 *
	 * @param g
	 *            The {@link IGeometry} to test for containment
	 * @return <code>true</code> if the given {@link IGeometry} is fully
	 *         contained by this {@link IShape}, <code>false</code> otherwise.
	 */
	public boolean contains(final IGeometry g);

	/**
	 * Returns an {@link ICurve} representing the outline of this {@link IShape}
	 * .
	 *
	 * @return An {@link ICurve} representing this {@link IShape}'s outline.
	 */
	public ICurve getOutline();

	/**
	 * Returns the individual {@link ICurve} segments, which constitute the
	 * outline of this {@link IShape}.
	 *
	 * @return The {@link ICurve} segments of this {@link IShape}'s outline.
	 */
	public ICurve[] getOutlineSegments();

	/**
	 * Returns a new {@link IShape}, which represents the given {@link IShape}
	 * after the application of the given {@link AffineTransform}.
	 *
	 * @see IGeometry#getTransformed(AffineTransform)
	 */
	@Override
	public IShape getTransformed(AffineTransform t);

}

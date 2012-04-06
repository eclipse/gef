/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

/**
 * An {@link IShape} is an {@link IGeometry} with a closed outline consisting of
 * multiple {@link ICurve} segments.
 */
public interface IShape extends IGeometry {

	/**
	 * Returns an {@link IPolyCurve} representing the outline of this
	 * {@link IShape}.
	 * 
	 * @return an {@link IPolyCurve} representing the outline
	 */
	public IPolyCurve getOutline();

	/**
	 * Returns the individual {@link ICurve} segments, which constitute the
	 * outline of this {@link IShape}.
	 * 
	 * @return the {@link ICurve} segments of this {@link IShape}'s outline
	 */
	public ICurve[] getOutlineSegments();

	/**
	 * Tests whether the given {@link IGeometry} is fully contained by this
	 * {@link IShape}.
	 * 
	 * @param g
	 *            the {@link IGeometry} to test for containment
	 * @return <code>true</code> if the given {@link IGeometry} is fully
	 *         contained by this {@link IShape}, <code>false</code> otherwise
	 */
	public boolean contains(final IGeometry g);

}

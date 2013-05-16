/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
 * 
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
 * <p>
 * The IShape interface is the counterpart of the ICurve interface, in that it
 * brings together operations to be used on areal geometric objects, such as
 * Polygons. An IShape allows to test if an IGeometry is fully contained by the
 * IShape using the contains(IGeometry) method. An IGeometry is considered to be
 * fully contained by an IShape if the IShape contains all the Points that
 * constitute the IGeometry in question. Moreover, the closed outline of an
 * IShape can be retrieved via the getOutline() method. It is defined to be an
 * IPolyCurve. If you want to process the outline segments individually, you can
 * use the getOutlineSegments() method, instead.
 * </p>
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
	 *            the {@link IGeometry} to test for containment
	 * @return <code>true</code> if the given {@link IGeometry} is fully
	 *         contained by this {@link IShape}, <code>false</code> otherwise
	 */
	public boolean contains(final IGeometry g);

	/**
	 * Returns an {@link ICurve} representing the outline of this {@link IShape}
	 * .
	 * 
	 * @return an {@link ICurve} representing the outline
	 */
	public ICurve getOutline();

	/**
	 * Returns the individual {@link ICurve} segments, which constitute the
	 * outline of this {@link IShape}.
	 * 
	 * @return the {@link ICurve} segments of this {@link IShape}'s outline
	 */
	public ICurve[] getOutlineSegments();

	@Override
	public IShape getTransformed(AffineTransform t);

}

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
package org.eclipse.gef4.geometry.planar;

/**
 * <p>
 * In contradiction to the relation of the IPolyCurve to the ICurve interfaces,
 * the IPolyShape interface does not extend the IShape interface. Anyhow, they
 * share their interface methods. You can test an IGeometry for containment
 * using the contains(IGeometry) method and the outline segments can be
 * retrieved via the getOutlineSegments() method.
 * </p>
 */
public interface IPolyShape extends IGeometry {

	/**
	 * Returns the {@link IShape}s that constitute this {@link IPolyShape}.
	 * 
	 * @return an array of {@link IShape}s, representing the parts that make up
	 *         this {@link IPolyShape}.
	 */
	IShape[] getShapes();

	/**
	 * <p>
	 * Computes the outline segments of this {@link IPolyShape}.
	 * </p>
	 * 
	 * <p>
	 * Each {@link ICurve} segment of the outline of the internal {@link IShape}
	 * s can be either an inner segment or an outer segment. This method
	 * extracts only the outer segments. The segments bordering voids are
	 * considered to be outer segments, too.
	 * </p>
	 * 
	 * @return the outline segments of this {@link IPolyShape}
	 */
	public ICurve[] getOutlineSegments();

	// /**
	// * <p>
	// * Computes the outline of this {@link IPolyShape}.
	// * </p>
	// *
	// * <p>
	// * The outline is returned as an array of {@link IPolyCurve}s. For every
	// * closed outline of this {@link IPolyShape}, one {@link IPolyCurve} is
	// * returned.
	// * </p>
	// *
	// * @return array of {@link IPolyCurve}s, one for each closed outline
	// */
	// public IPolyCurve[] getOutline();

	/**
	 * Checks if the given {@link IGeometry} is fully contained by this
	 * {@link IPolyShape}.
	 * 
	 * @param g
	 * @return <code>true</code> if the {@link IGeometry} is contained by this
	 *         {@link IPolyShape}, otherwise <code>false</code>
	 */
	public boolean contains(final IGeometry g);

}

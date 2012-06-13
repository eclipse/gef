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
 * An {@link IPolyShape} is the common abstraction over all {@link IGeometry}s
 * that are constituted by several {@link IShape} parts.
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

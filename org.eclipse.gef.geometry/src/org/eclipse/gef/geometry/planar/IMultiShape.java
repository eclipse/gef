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
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.planar;

/**
 * <p>
 * In contradiction to the relation of the IPolyCurve to the ICurve interfaces,
 * the IMultiShape interface does not extend the IShape interface. Anyhow, they
 * share their interface methods. You can test an IGeometry for containment
 * using the contains(IGeometry) method and the outline segments can be
 * retrieved via the getOutlineSegments() method.
 * </p>
 *
 * @author anyssen
 *
 */
public interface IMultiShape extends IGeometry {

	/**
	 * Checks if the given {@link IGeometry} is fully contained by this
	 * {@link IMultiShape}.
	 *
	 * @param g
	 *            The {@link IGeometry} which is tested for containment.
	 * @return <code>true</code> if the {@link IGeometry} is contained by this
	 *         {@link IMultiShape}, otherwise <code>false</code>
	 */
	public boolean contains(final IGeometry g);

	/**
	 * <p>
	 * Computes the outlines of this {@link IMultiShape}.
	 * </p>
	 *
	 * <p>
	 * The outlines are returned as an array of {@link ICurve}s. For every
	 * closed outline of this {@link IMultiShape} one {@link ICurve} is
	 * returned.
	 * </p>
	 *
	 * @return an array of {@link ICurve}s, one for each closed outline
	 */
	public ICurve[] getOutlines();

	/**
	 * <p>
	 * Computes the outline segments of this {@link IMultiShape}.
	 * </p>
	 *
	 * <p>
	 * Each {@link ICurve} segment of the outline of the internal {@link IShape}
	 * s can be either an inner segment or an outer segment. This method
	 * extracts only the outer segments. The segments bordering voids are
	 * considered to be outer segments, too.
	 * </p>
	 *
	 * @return the outline segments of this {@link IMultiShape}
	 */
	public ICurve[] getOutlineSegments();

	/**
	 * Returns the {@link IShape}s that constitute this {@link IMultiShape}.
	 *
	 * @return an array of {@link IShape}s, representing the parts that make up
	 *         this {@link IMultiShape}.
	 */
	IShape[] getShapes();

}

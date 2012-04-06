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
 * An {@link IPolyCurve} is an {@link ICurve} that is constituted by multiple
 * connected {@link ICurve} segments.
 */
public interface IPolyCurve extends ICurve {

	/**
	 * Returns the {@link ICurve} segments of this {@link IPolyCurve}.
	 * 
	 * @return an array of {@link ICurve}s, representing the segments that make
	 *         up this {@link IPolyCurve}
	 */
	public ICurve[] getCurves();

	// TODO: intersects(), getIntersections(), overlaps()

}

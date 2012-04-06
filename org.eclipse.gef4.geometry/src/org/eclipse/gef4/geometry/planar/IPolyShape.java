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

	// contains()

}

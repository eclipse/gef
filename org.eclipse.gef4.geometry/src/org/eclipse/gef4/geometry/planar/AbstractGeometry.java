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

import org.eclipse.gef4.geometry.transform.AffineTransform;

abstract class AbstractGeometry implements IGeometry {

	private static final long serialVersionUID = 1L;

	/**
	 * Overwritten with public visibility as recommended within
	 * {@link Cloneable}.
	 */
	@Override
	public final Object clone() {
		return getCopy();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		// calculating a better hashCode is not possible, because due to the
		// imprecision, equals() is no longer transitive
		return 0;
	}

	/**
	 * Default implementation returning a transformed {@link Path}
	 * representation of this Geometry. Subclasses may overwrite to return a
	 * more appropriate representation.
	 * 
	 * @return a transformed {@link Path} representation of this
	 *         {@link IGeometry}
	 */
	public IGeometry getTransformed(AffineTransform t) {
		return toPath().getTransformed(t);
	}
}

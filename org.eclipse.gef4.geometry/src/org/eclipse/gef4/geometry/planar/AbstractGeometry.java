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
	public Object clone() {
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
	public IGeometry getTransformed(final AffineTransform t) {
		return toPath().getTransformed(t);
	}

	// TODO: implement IPolyShape semantic
	public boolean touches(final IGeometry g) {
		if (this instanceof ICurve) {
			if (g instanceof ICurve) {
				return ((ICurve) this).intersects((ICurve) g)
						|| ((ICurve) this).overlaps((ICurve) g);
			} else if (g instanceof IShape) {
				return ((IShape) g).contains(this)
						|| this.touches(((IShape) g).getOutline());
			} else {
				throw new UnsupportedOperationException("Not yet implemented.");
			}
		} else if (this instanceof IShape) {
			if (g instanceof ICurve) {
				return ((IShape) this).contains(g)
						|| ((IShape) this).getOutline().touches(g);
			} else if (g instanceof IShape) {
				return ((IShape) this).contains(g)
						|| ((IShape) g).contains(this)
						|| ((IShape) this).getOutline().touches(
								((IShape) g).getOutline());
			} else {
				throw new UnsupportedOperationException("Not yet implemented.");
			}
		} else {
			throw new UnsupportedOperationException("Not yet implemented.");
		}
	}

}

/*******************************************************************************
 * Copyright (c) 2011, 2012 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - IPolyShape semantics for touches()
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

abstract class AbstractGeometry implements IGeometry {

	private static final long serialVersionUID = 1L;

	/**
	 * Overridden with public visibility as recommended within {@link Cloneable}
	 * .
	 */
	@Override
	public Object clone() {
		return getCopy();
	}

	/**
	 * Default implementation returning a transformed {@link Path}
	 * representation of this {@link IGeometry}. Subclasses may override this
	 * method to return a more specific representation.
	 * 
	 * @return a transformed {@link Path} representation of this
	 *         {@link IGeometry}
	 */
	public IGeometry getTransformed(final AffineTransform t) {
		return toPath().getTransformed(t);
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

	public boolean touches(final IGeometry g) {
		if (this instanceof ICurve) {
			if (g instanceof ICurve) {
				return ((ICurve) this).intersects((ICurve) g)
						|| ((ICurve) this).overlaps((ICurve) g);
			} else if (g instanceof IShape) {
				return ((IShape) g).contains(this)
						|| this.touches(((IShape) g).getOutline());
			} else if (g instanceof IPolyShape) {
				if (((IPolyShape) g).contains(this)) {
					return true;
				}
				for (ICurve c : ((IPolyShape) g).getOutlineSegments()) {
					if (this.touches(c)) {
						return true;
					}
				}
				return false;
			} else {
				throw new UnsupportedOperationException(
						"Not yet implemented: touches(" + this + ", " + g + ")");
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
			} else if (g instanceof IPolyShape) {
				if (((IShape) this).contains(g)) {
					return true;
				}
				if (((IPolyShape) g).contains(this)) {
					return true;
				}
				IPolyCurve thisOutline = ((IShape) this).getOutline();
				for (ICurve c : ((IPolyShape) g).getOutlineSegments()) {
					if (thisOutline.touches(c)) {
						return true;
					}
				}
				return false;
			} else {
				throw new UnsupportedOperationException(
						"Not yet implemented: touches(" + this + ", " + g + ")");
			}
		} else if (this instanceof IPolyShape) {
			IPolyShape thisPolyShape = (IPolyShape) this;
			if (g instanceof ICurve) {
				ICurve gCurve = (ICurve) g;
				if (thisPolyShape.contains(gCurve)) {
					return true;
				}
				for (ICurve c : thisPolyShape.getOutlineSegments()) {
					if (gCurve.touches(c)) {
						return true;
					}
				}
				return false;
			} else if (g instanceof IShape) {
				IShape gShape = (IShape) g;
				if (thisPolyShape.contains(gShape)
						|| gShape.contains(thisPolyShape)) {
					return true;
				}
				IPolyCurve gOutline = gShape.getOutline();
				for (ICurve c : thisPolyShape.getOutlineSegments()) {
					if (gOutline.touches(c)) {
						return true;
					}
				}
				return false;
			} else if (g instanceof IPolyShape) {
				IPolyShape gPolyShape = (IPolyShape) g;
				if (thisPolyShape.contains(gPolyShape)
						|| gPolyShape.contains(thisPolyShape)) {
					return true;
				}
				for (ICurve thisOutlineSeg : thisPolyShape.getOutlineSegments()) {
					for (ICurve gOutlineSeg : gPolyShape.getOutlineSegments()) {
						if (thisOutlineSeg.touches(gOutlineSeg)) {
							return true;
						}
					}
				}
				return false;
			} else {
				throw new UnsupportedOperationException(
						"Not yet implemented: touches(" + this + ", " + g + ")");
			}
		} else {
			throw new UnsupportedOperationException(
					"Not yet implemented: touches(" + this + ", " + g + ")");
		}
	}

}

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
 *     Matthias Wienand (itemis AG) - IMultiShape semantics for touches()
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.planar;

/**
 * The abstract base class for all {@link IGeometry}s. It provides generic
 * support for cloning ({@link Cloneable}) by delegating to {@link #getCopy()},
 * which needs to be implemented by subclasses. Further, provides generic
 * support for affine transformations by applying the respective
 * {@link AffineTransform} to the path representation of this
 * {@link AbstractGeometry} (see {@link #toPath()}). Implements overlap
 * detection ({@link #touches(IGeometry)}) for all known {@link IGeometry}s.
 *
 * @author anyssen
 * @author mwienand
 *
 */
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
	@Override
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

	@Override
	public boolean touches(final IGeometry g) {
		if (this instanceof ICurve) {
			if (g instanceof ICurve) {
				return ((ICurve) this).intersects((ICurve) g)
						|| ((ICurve) this).overlaps((ICurve) g);
			} else if (g instanceof IShape) {
				return ((IShape) g).contains(this)
						|| this.touches(((IShape) g).getOutline());
			} else if (g instanceof IMultiShape) {
				if (((IMultiShape) g).contains(this)) {
					return true;
				}
				for (ICurve c : ((IMultiShape) g).getOutlineSegments()) {
					if (this.touches(c)) {
						return true;
					}
				}
				return false;
			} else {
				throw new UnsupportedOperationException(
						"Not yet implemented: touches(" + this + ", " + g
								+ ")");
			}
		} else if (this instanceof IShape) {
			if (g instanceof ICurve) {
				return ((IShape) this).contains(g)
						|| ((IShape) this).getOutline().touches(g);
			} else if (g instanceof IShape) {
				return ((IShape) this).contains(g)
						|| ((IShape) g).contains(this)
						|| ((IShape) this).getOutline()
								.touches(((IShape) g).getOutline());
			} else if (g instanceof IMultiShape) {
				if (((IShape) this).contains(g)) {
					return true;
				}
				if (((IMultiShape) g).contains(this)) {
					return true;
				}
				ICurve thisOutline = ((IShape) this).getOutline();
				for (ICurve c : ((IMultiShape) g).getOutlineSegments()) {
					if (thisOutline.touches(c)) {
						return true;
					}
				}
				return false;
			} else {
				throw new UnsupportedOperationException(
						"Not yet implemented: touches(" + this + ", " + g
								+ ")");
			}
		} else if (this instanceof IMultiShape) {
			IMultiShape thisPolyShape = (IMultiShape) this;
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
				ICurve gOutline = gShape.getOutline();
				for (ICurve c : thisPolyShape.getOutlineSegments()) {
					if (gOutline.touches(c)) {
						return true;
					}
				}
				return false;
			} else if (g instanceof IMultiShape) {
				IMultiShape gMultiShape = (IMultiShape) g;
				if (thisPolyShape.contains(gMultiShape)
						|| gMultiShape.contains(thisPolyShape)) {
					return true;
				}
				for (ICurve thisOutlineSeg : thisPolyShape
						.getOutlineSegments()) {
					for (ICurve gOutlineSeg : gMultiShape
							.getOutlineSegments()) {
						if (thisOutlineSeg.touches(gOutlineSeg)) {
							return true;
						}
					}
				}
				return false;
			} else {
				throw new UnsupportedOperationException(
						"Not yet implemented: touches(" + this + ", " + g
								+ ")");
			}
		} else {
			throw new UnsupportedOperationException(
					"Not yet implemented: touches(" + this + ", " + g + ")");
		}
	}

}

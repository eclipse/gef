package org.eclipse.gef4.geometry.planar;

import org.eclipse.gef4.geometry.transform.AffineTransform;

public abstract class AbstractGeometry implements IGeometry {

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

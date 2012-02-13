package org.eclipse.gef4.geometry.planar;

public interface IPolyCurve extends IGeometry {

	/**
	 * Returns a sequence of {@link ICurve}s, representing the curve segments of
	 * this {@link IPolyCurve}.
	 * 
	 * @return an array of {@link ICurve}s, representing the segments that make
	 *         up this {@link IPolyCurve}
	 */
	public ICurve[] getSegments();
}

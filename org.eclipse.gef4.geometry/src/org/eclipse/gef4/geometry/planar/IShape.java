package org.eclipse.gef4.geometry.planar;

public interface IShape extends IGeometry {

	// IPolyCurve getOutline();

	ICurve[] getOutlineSegments();
}

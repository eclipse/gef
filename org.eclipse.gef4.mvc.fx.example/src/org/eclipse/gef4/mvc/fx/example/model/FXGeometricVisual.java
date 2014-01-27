package org.eclipse.gef4.mvc.fx.example.model;

import org.eclipse.gef4.geometry.planar.IGeometry;

public class FXGeometricVisual<G extends IGeometry> {

	public G geometry;

	public FXGeometricVisual(G geometry) {
		this.geometry = geometry;
	}

}
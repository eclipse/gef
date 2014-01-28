package org.eclipse.gef4.mvc.fx.example.model;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;

abstract public class AbstractFXGeometricVisualElement<G extends IGeometry> {

	public G geometry;
	public AffineTransform transform;

	public AbstractFXGeometricVisualElement(G geometry) {
		this.geometry = geometry;
	}
	
	public AbstractFXGeometricVisualElement(G geometry, AffineTransform transform) {
		this.geometry = geometry;
		this.transform = transform;
	}

}
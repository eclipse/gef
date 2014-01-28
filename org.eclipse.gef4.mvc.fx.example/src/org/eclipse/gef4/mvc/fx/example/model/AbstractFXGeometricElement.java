package org.eclipse.gef4.mvc.fx.example.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.IPropertyChangeSupport;

abstract public class AbstractFXGeometricElement<G extends IGeometry> implements IPropertyChangeSupport {

	public static final String GEOMETRY_PROPERTY = "Geometry";
	public static final String TRANSFORM_PROPERTY = "Transform";
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private G geometry;
	private AffineTransform transform;
	
	public G getGeometry() {
		return geometry;
	}
	
	public AffineTransform getTransform() {
		return transform;
	}
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public AbstractFXGeometricElement(G geometry) {
		setGeometry(geometry);
	}
	
	public AbstractFXGeometricElement(G geometry, AffineTransform transform) {
		this(geometry);
		setTransform(transform);
	}
	
	public void setGeometry(G geometry) {
		G old = this.geometry;
		this.geometry = geometry;
		pcs.firePropertyChange(GEOMETRY_PROPERTY, old, geometry);
	}
	
	public void setTransform(AffineTransform transform) {
		AffineTransform old = this.transform;
		this.transform = transform;
		pcs.firePropertyChange(TRANSFORM_PROPERTY, old, transform);
	}

}
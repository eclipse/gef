package org.eclipse.gef4.mvc.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class DefaultZoomingModel implements IZoomingModel {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private double zoom = IZoomingModel.DEFAULT_ZOOM_FACTOR;

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public double getZoomFactor() {
		return zoom;
	}

	@Override
	public void setZoomFactor(double zoomFactor) {
		if (zoomFactor <= 0) {
			throw new IllegalArgumentException(
					"Expected: Positive double value. Given: <" + zoomFactor
							+ ">.");
		}
		double oldZoom = zoom;
		zoom = zoomFactor;
		pcs.firePropertyChange(ZOOM_FACTOR_PROPERTY, oldZoom, zoom);
	}

}

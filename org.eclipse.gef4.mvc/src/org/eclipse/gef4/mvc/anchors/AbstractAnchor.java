package org.eclipse.gef4.mvc.anchors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class AbstractAnchor<V> implements IAnchor<V> {

	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);
	
	private V anchorage;

	@Override
	public V getAnchorage() {
		return anchorage;
	}

	@Override
	public void setAnchorage(V anchorage) {
		this.anchorage = anchorage;
	}
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
}

package org.eclipse.gef4.mvc.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.gef4.mvc.parts.IVisualPart;

public class DefaultHoverModel<V> implements IHoverModel<V> {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private IVisualPart<V> hovered = null;
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public IVisualPart<V> getHover() {
		return hovered;
	}

	@Override
	public void setHover(IVisualPart<V> cp) {
		IVisualPart<V> oldHover = hovered;
		hovered = cp;
		pcs.firePropertyChange(HOVER_PROPERTY, oldHover, hovered);
	}

	@Override
	public void clearHover() {
		setHover(null);
	}

}

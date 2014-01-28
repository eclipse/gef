package org.eclipse.gef4.mvc.fx.example.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef4.mvc.fx.example.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;

public abstract class AbstractFXExampleElementPart extends AbstractFXContentPart implements
		PropertyChangeListener {
	
	@Override
	public AbstractFXGeometricElement<?> getModel() {
		return (AbstractFXGeometricElement<?>) super.getModel();
	}
	
	@Override
	public void activate() {
		super.activate();
		getModel().addPropertyChangeListener(this);
	}
	
	@Override
	public void deactivate() {
		getModel().removePropertyChangeListener(this);
		super.deactivate();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == getModel()) {
			refreshVisual();
		}
	}
	
}

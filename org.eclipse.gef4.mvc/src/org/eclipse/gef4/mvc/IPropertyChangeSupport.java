package org.eclipse.gef4.mvc;

import java.beans.PropertyChangeListener;

public interface IPropertyChangeSupport {

	public void addPropertyChangeListener(PropertyChangeListener listener);
	
	public void removePropertyChangeListener(PropertyChangeListener listener);
	
}

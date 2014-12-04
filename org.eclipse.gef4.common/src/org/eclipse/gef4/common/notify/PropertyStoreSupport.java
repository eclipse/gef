/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.common.notify;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

public class PropertyStoreSupport implements IPropertyStore {

	private Map<String, Object> properties = new HashMap<String, Object>();
	private PropertyChangeSupport pcs;
	
	public PropertyStoreSupport(Object source) {
		pcs = new PropertyChangeSupport(source);
	}

	public Object getProperty(String name) {
		return properties.get(name);
	}

	public void setProperty(String propertyName, Object newValue) {
		Object oldValue = properties.get(propertyName);
		properties.put(propertyName, newValue);
		// prevent unnecessary property change events by checking if the value
		// actually changes
		if (oldValue != newValue
				&& (oldValue == null || !oldValue.equals(newValue))) {
			pcs.firePropertyChange(propertyName, oldValue, newValue);
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

}

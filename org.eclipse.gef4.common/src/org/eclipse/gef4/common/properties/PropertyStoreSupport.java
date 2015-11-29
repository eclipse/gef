/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.common.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

/**
 * A support class to manage properties for a source {@link IPropertyStore}. It
 * offers all methods defined by {@link IPropertyStore}, while not formally
 * implementing the interface, and can thus be used by a source
 * {@link IPropertyStore} as a delegate.
 * <p>
 * In addition to the source {@link IPropertyStore} a
 * {@link PropertyChangeSupport} is expected during construction. It will be
 * used to fire {@link PropertyChangeEvent}s when property value changes, i.e.
 * whenever a call to {@link #setProperty(String, Object)} results in a property
 * value being set, unset, or changed. The name of the property will be used as
 * the property name within all those events.
 */
public class PropertyStoreSupport {

	private Map<String, Object> properties = new HashMap<>();
	private PropertyChangeSupport pcs;

	/**
	 * Creates a new {@link PropertyStoreSupport} for the given source
	 * {@link IPropertyStore} and a related {@link PropertyChangeSupport}.
	 * 
	 * @param source
	 *            The {@link IPropertyStore} that encloses the to be created
	 *            {@link PropertyStoreSupport}, delegating calls to it. May not
	 *            be <code>null</code>
	 * @param pcs
	 *            An {@link PropertyChangeSupport}, which will be used to fire
	 *            {@link PropertyChangeEvent}'s whenever properties are set or
	 *            unset. May not be <code>null</code>.
	 */
	public PropertyStoreSupport(IPropertyStore source,
			PropertyChangeSupport pcs) {
		if (source == null) {
			throw new IllegalArgumentException("source may not be null.");
		}
		if (pcs == null) {
			throw new IllegalArgumentException("pcs may not be null.");
		}
		this.pcs = pcs;
	}

	/**
	 * Retrieves the value of the property with the given name.
	 * 
	 * @param name
	 *            The name of the property whose value is to be retrieved.
	 * @return The value of the property specified by its name.
	 * 
	 * @see IPropertyStore#getProperty(String)
	 */
	public Object getProperty(String name) {
		return properties.get(name);
	}

	/**
	 * Sets or updates the value of the property with the given name. Uses the
	 * delegate {@link PropertyStoreSupport} to notify registered
	 * {@link PropertyChangeListener}s in case a property was set, unset, or
	 * actually changed, suppressing change events in case the passed in value
	 * is equal to the old value.
	 * 
	 * @param name
	 *            The name of the property whose value it to set/change.
	 * @param value
	 *            The new or initial value to set.
	 * 
	 * @see IPropertyStore#setProperty(String, Object)
	 */
	public void setProperty(String name, Object value) {
		Object oldValue = properties.get(name);
		properties.put(name, value);
		// prevent unnecessary property change events by checking if the value
		// actually changes
		if (oldValue != value
				&& (oldValue == null || !oldValue.equals(value))) {
			pcs.firePropertyChange(name, oldValue, value);
		}
	}
}

/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.common.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

/**
 * An extension to {@link java.beans.PropertyChangeSupport} that is dedicated to
 * provide support implementation of {@link IPropertyChangeNotifier}.
 * 
 * @author anyssen
 *
 */
public class PropertyChangeNotifierSupport extends PropertyChangeSupport {

	private static final long serialVersionUID = -2361885204235272457L;
	private Object source;

	/**
	 * Constructs a new {@link PropertyChangeNotifierSupport}.
	 * 
	 * @param sourceBean
	 *            The bean that is used as source in any fired
	 *            {@link PropertyChangeEvent}.
	 */
	public PropertyChangeNotifierSupport(Object sourceBean) {
		super(sourceBean);
		// XXX: We need to keep track of source bean as well because our super
		// class does not expose the field and does also not provide a getter
		// for it.
		this.source = sourceBean;
	}

	/**
	 * Reports a bound keyed property update to listeners that have been
	 * registered to track updates of all properties or a property with the
	 * specified name.
	 * <p>
	 * No event is fired if old and new values are equal and non-null.
	 * <p>
	 * This is merely a convenience wrapper around the more general
	 * {@link #firePropertyChange(PropertyChangeEvent)} method.
	 *
	 * @param propertyName
	 *            the programmatic name of the property that was changed
	 * @param key
	 *            the key of the property element that was changed
	 * @param oldValue
	 *            the old value of the property
	 * @param newValue
	 *            the new value of the property
	 */
	public void fireKeyedPropertyChange(String propertyName, Object key,
			Object oldValue, Object newValue) {
		if (oldValue == null || newValue == null
				|| !oldValue.equals(newValue)) {
			firePropertyChange(new KeyedPropertyChangeEvent(source,
					propertyName, oldValue, newValue, key));
		}
	}
}

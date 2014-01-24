/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.anchors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * 
 * @author anyssen
 *
 * @param <V>
 */
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

/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.gef4.mvc.parts.IContentPart;

/**
 * Default implementation of the focus model.
 * 
 * @param <V>
 */
public class DefaultFocusModel<V> implements IFocusModel<V> {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private IContentPart<V> focused = null;
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}
	
	@Override
	public IContentPart<V> getFocused() {
		return focused;
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
	
	@Override
	public void setFocused(IContentPart<V> focusPart) {
		IContentPart<V> old = focused;
		focused = focusPart;
		pcs.firePropertyChange(IFocusModel.FOCUS_PROPERTY, old, focused);
	}
	
}

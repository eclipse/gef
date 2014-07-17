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

import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * 
 * @author mwienand
 * 
 * @param <VR> The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public class DefaultHoverModel<VR> implements IHoverModel<VR> {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private IVisualPart<VR> hovered = null;

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public IVisualPart<VR> getHover() {
		return hovered;
	}

	@Override
	public void setHover(IVisualPart<VR> cp) {
		IVisualPart<VR> oldHover = hovered;
		hovered = cp;
		pcs.firePropertyChange(HOVER_PROPERTY, oldHover, hovered);
	}

	@Override
	public void clearHover() {
		setHover(null);
	}

}

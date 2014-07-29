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
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * Default implementation of the focus model.
 * 
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public class DefaultFocusModel<VR> implements IFocusModel<VR> {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private IContentPart<VR> focused = null;
	private boolean isViewerFocused = false;

	public DefaultFocusModel() {
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public IContentPart<VR> getFocused() {
		return focused;
	}

	@Override
	public boolean isViewerFocused() {
		return isViewerFocused;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	@Override
	public void setFocused(IContentPart<VR> focusPart) {
		IContentPart<VR> old = focused;
		focused = focusPart;
		pcs.firePropertyChange(IFocusModel.FOCUS_PROPERTY, old, focused);
	}

	@Override
	public void setViewerFocused(boolean viewerFocused) {
		boolean old = isViewerFocused;
		isViewerFocused = viewerFocused;
		pcs.firePropertyChange(VIEWER_FOCUS_PROPERTY, old, viewerFocused);
	}

}

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
package org.eclipse.gef4.mvc.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef4.mvc.models.FocusModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 *
 * @author anyssen
 *
 * @param <VR>
 *            The visual root node of the UI toolkit this {@link IVisualPart} is
 *            used in, e.g. javafx.scene.Node in case of JavaFX.
 */
public abstract class AbstractFocusBehavior<VR> extends AbstractBehavior<VR>
		implements PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		getHost().getRoot().getViewer().getAdapter(FocusModel.class)
				.addPropertyChangeListener(this);
	}

	protected abstract void applyFocus();

	@Override
	public void deactivate() {
		getHost().getRoot().getViewer().getAdapter(FocusModel.class)
				.removePropertyChangeListener(this);
		super.deactivate();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (FocusModel.VIEWER_FOCUS_PROPERTY.equals(evt.getPropertyName())) {
			// viewer focus changed
		} else if (FocusModel.FOCUS_PROPERTY.equals(evt.getPropertyName())) {
			if (evt.getNewValue() == getHost()) {
				applyFocus();
			}
		}
	}

}
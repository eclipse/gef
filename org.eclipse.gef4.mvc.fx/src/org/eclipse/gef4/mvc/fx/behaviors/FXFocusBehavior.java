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
package org.eclipse.gef4.mvc.fx.behaviors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.scene.Node;

import org.eclipse.gef4.mvc.behaviors.AbstractBehavior;
import org.eclipse.gef4.mvc.models.FocusModel;

/**
 *
 * @author mwienand
 * @author anyssen
 *
 */
public class FXFocusBehavior extends AbstractBehavior<Node> implements
		PropertyChangeListener {

	@Override
	public void activate() {
		super.activate();
		getHost().getRoot().getViewer().getAdapter(FocusModel.class)
				.addPropertyChangeListener(this);
	}

	protected void applyFocus() {
		getHost().getVisual().requestFocus();
	}

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

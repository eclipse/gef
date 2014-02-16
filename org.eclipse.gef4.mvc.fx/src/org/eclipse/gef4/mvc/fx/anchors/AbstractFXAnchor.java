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
package org.eclipse.gef4.mvc.fx.anchors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import org.eclipse.gef4.mvc.anchors.AbstractAnchor;
import org.eclipse.gef4.mvc.anchors.IAnchor;

public abstract class AbstractFXAnchor extends AbstractAnchor<Node> {

	private ChangeListener<Bounds> boundsListener;

	public AbstractFXAnchor(Node anchorage) {
		super(anchorage);
	}

	@Override
	protected void setAnchorage(Node anchorage) {
		Node oldAnchorage = getAnchorage();
		if (oldAnchorage != null) {
			// unregister listeners
			unregisterLayoutListener(oldAnchorage);
		}
		super.setAnchorage(anchorage);
		if (anchorage != null) {
			// register listeners
			registerLayoutListeners(anchorage);
		}
	}

	private void registerLayoutListeners(Node anchorageOrAnchored) {
		// add bounds-in-parent listeners to the whole hierarchy to witness node
		// resizing and container movement
		boundsListener = new ChangeListener<Bounds>() {
			@Override
			public void changed(
					ObservableValue<? extends Bounds> observable,
					Bounds oldValue, Bounds newValue) {
				propertyChangeSupport.firePropertyChange(IAnchor.REPRESH,
						null, null);
			}
		};
		
		Node current = anchorageOrAnchored;
		current.boundsInParentProperty().addListener(boundsListener);
	}

	private void unregisterLayoutListener(Node anchorageOrAnchored) {
		// remove the previously added listeners
		Node current = anchorageOrAnchored;
		current.boundsInParentProperty().removeListener(boundsListener);
		
		// dispose listener
		boundsListener = null;
	}
}

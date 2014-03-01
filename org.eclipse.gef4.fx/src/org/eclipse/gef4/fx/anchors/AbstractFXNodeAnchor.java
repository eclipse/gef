/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Ny??en (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.fx.anchors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

import org.eclipse.gef4.fx.listener.VisualChangeListener;

public abstract class AbstractFXNodeAnchor implements IFXNodeAnchor {
	
	private Node anchorage;
	
	private VisualChangeListener visualListener = new VisualChangeListener() {
		@Override
		protected void transformChanged(Transform oldTransform,
				Transform newTransform) {
			propertyChangeSupport.firePropertyChange(IFXNodeAnchor.REPRESH, null,
					null);
		}

		@Override
		protected void boundsChanged(Bounds oldBounds, Bounds newBounds) {
			propertyChangeSupport.firePropertyChange(IFXNodeAnchor.REPRESH, null,
					null);
		}
	};

	public AbstractFXNodeAnchor(Node anchorage) {
		setAnchorage(anchorage);
	}

	protected void setAnchorage(Node anchorage) {
		Node oldAnchorage = getAnchorage();
		if (oldAnchorage != null) {
			unregisterLayoutListener(oldAnchorage);
		}
		this.anchorage = anchorage;
		if (anchorage != null) {
			registerLayoutListeners(anchorage);
		}
	}

	private void registerLayoutListeners(Node anchorageOrAnchored) {
		visualListener = new VisualChangeListener() {
			@Override
			protected void transformChanged(Transform oldTransform,
					Transform newTransform) {
				propertyChangeSupport.firePropertyChange(IFXNodeAnchor.REPRESH, null,
						null);
			}

			@Override
			protected void boundsChanged(Bounds oldBounds, Bounds newBounds) {
				propertyChangeSupport.firePropertyChange(IFXNodeAnchor.REPRESH, null,
						null);
			}
		};
		visualListener.register(anchorageOrAnchored, anchorageOrAnchored
				.getScene().getRoot());
	}

	private void unregisterLayoutListener(Node anchorageOrAnchored) {
		visualListener.unregister();
	}
	
	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);
		
	public Node getAnchorage() {
		return anchorage;
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

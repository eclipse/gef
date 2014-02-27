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

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

import org.eclipse.gef4.fx.listener.VisualChangeListener;
import org.eclipse.gef4.mvc.anchors.AbstractAnchor;
import org.eclipse.gef4.mvc.anchors.IAnchor;

public abstract class AbstractFXAnchor extends AbstractAnchor<Node> {

	private VisualChangeListener visualListener = new VisualChangeListener() {
		@Override
		protected void transformChanged(Transform oldTransform,
				Transform newTransform) {
			propertyChangeSupport.firePropertyChange(IAnchor.REPRESH, null,
					null);
		}

		@Override
		protected void boundsChanged(Bounds oldBounds, Bounds newBounds) {
			propertyChangeSupport.firePropertyChange(IAnchor.REPRESH, null,
					null);
		}
	};

	public AbstractFXAnchor(Node anchorage) {
		super(anchorage);
	}

	@Override
	protected void setAnchorage(Node anchorage) {
		Node oldAnchorage = getAnchorage();
		if (oldAnchorage != null) {
			unregisterLayoutListener(oldAnchorage);
		}
		super.setAnchorage(anchorage);
		if (anchorage != null) {
			registerLayoutListeners(anchorage);
		}
	}

	private void registerLayoutListeners(Node anchorageOrAnchored) {
		visualListener = new VisualChangeListener() {
			@Override
			protected void transformChanged(Transform oldTransform,
					Transform newTransform) {
				propertyChangeSupport.firePropertyChange(IAnchor.REPRESH, null,
						null);
			}

			@Override
			protected void boundsChanged(Bounds oldBounds, Bounds newBounds) {
				propertyChangeSupport.firePropertyChange(IAnchor.REPRESH, null,
						null);
			}
		};
		visualListener.register(anchorageOrAnchored, anchorageOrAnchored
				.getScene().getRoot());
	}

	private void unregisterLayoutListener(Node anchorageOrAnchored) {
		visualListener.unregister();
	}
}

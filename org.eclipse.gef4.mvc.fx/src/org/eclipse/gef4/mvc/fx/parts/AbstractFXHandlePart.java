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
package org.eclipse.gef4.mvc.fx.parts;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

import org.eclipse.gef4.fx.listener.VisualChangeListener;
import org.eclipse.gef4.mvc.parts.AbstractHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

abstract public class AbstractFXHandlePart extends AbstractHandlePart<Node> {

	private VisualChangeListener visualListener = new VisualChangeListener() {
		@Override
		protected void boundsChanged(Bounds oldBounds, Bounds newBounds) {
			refreshVisual();
		}

		@Override
		protected void transformChanged(Transform oldTransform,
				Transform newTransform) {
			refreshVisual();
		}
	};

	@Override
	public void attachVisualToAnchorageVisual(IVisualPart<Node> anchorage, Node anchorageVisual) {
		visualListener.register(anchorageVisual,
				((FXRootPart) getRoot()).getLayerStackPane());
	};

	@Override
	public void detachVisualFromAnchorageVisual(IVisualPart<Node> anchorage, Node anchorageVisual) {
		visualListener.unregister();
	}

}

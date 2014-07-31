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

import java.util.HashMap;
import java.util.Map;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.mvc.parts.AbstractHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

abstract public class AbstractFXHandlePart extends AbstractHandlePart<Node> {

	private final Map<IVisualPart<Node>, VisualChangeListener> visualChangeListeners = new HashMap<IVisualPart<Node>, VisualChangeListener>();

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node> anchorage,
			String role) {
		// we only add one VCL per anchorage
		if (!visualChangeListeners.containsKey(anchorage)) {
			VisualChangeListener listener = new VisualChangeListener() {
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
			visualChangeListeners.put(anchorage, listener);
			listener.register(anchorage.getVisual(),
					((FXRootPart) getRoot()).getLayerStackPane());
		}
	};

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node> anchorage,
			String role) {
		// the anchorage might be registered under a different role
		if (!getAnchorages().containsKey(anchorage)) {
			// now we are sure that we do not need to listen to visual changes
			// of this anchorage any more
			visualChangeListeners.remove(anchorage).unregister();
		}
	}

}

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

import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.mvc.parts.AbstractHandlePart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

/**
 * Abstract base implementation for a JavaFX-specific {@link IHandlePart}.
 *
 * @author anyssen
 *
 * @param <V>
 *            The visual {@link Node} used by this {@link AbstractFXHandlePart}.
 */
abstract public class AbstractFXHandlePart<V extends Node>
		extends AbstractHandlePart<Node, V> {

	private final Map<IVisualPart<Node, ? extends Node>, VisualChangeListener> visualChangeListeners = new HashMap<>();
	private final Map<IVisualPart<Node, ? extends Node>, Integer> anchorageLinkCount = new HashMap<>();

	@Override
	protected void attachToAnchorageVisual(
			IVisualPart<Node, ? extends Node> anchorage, String role) {
		// we only add one visual change listener per anchorage, so we need to
		// keep track of the number of links to an anchorage (roles)
		int count = anchorageLinkCount.get(anchorage) == null ? 0
				: anchorageLinkCount.get(anchorage);

		if (count == 0) {
			VisualChangeListener listener = new VisualChangeListener() {
				@Override
				protected void boundsInLocalChanged(Bounds oldBounds,
						Bounds newBounds) {
					refreshVisual();
				}

				@Override
				protected void localToParentTransformChanged(Node observed,
						Transform oldTransform, Transform newTransform) {
					refreshVisual();
				}
			};
			visualChangeListeners.put(anchorage, listener);
			listener.register(anchorage.getVisual(), getVisual());
		}

		anchorageLinkCount.put(anchorage, count + 1);
	}

	@Override
	protected void detachFromAnchorageVisual(
			IVisualPart<Node, ? extends Node> anchorage, String role) {

		// infer current number of links
		int count = anchorageLinkCount.get(anchorage);

		// the anchorage might be registered under a different roles, only
		// remove the listener if there is no link left
		if (count == 1) {
			// now we are sure that we do not need to listen to visual changes
			// of this anchorage any more
			visualChangeListeners.remove(anchorage).unregister();
		}

		if (count > 0) {
			anchorageLinkCount.put(anchorage, count - 1);
		} else {
			anchorageLinkCount.remove(anchorage);
		}
	}

	@Override
	protected void registerAtVisualPartMap(IViewer<Node> viewer, V visual) {
		// register "main" visual for this part
		super.registerAtVisualPartMap(viewer, visual);
		// register nested visuals that are not controlled by other parts
		FXPartUtils.registerNestedVisuals(this, viewer.getVisualPartMap(),
				visual);
	}

	@Override
	protected void unregisterFromVisualPartMap(IViewer<Node> viewer, V visual) {
		// unregister "main" visual for this part
		super.unregisterFromVisualPartMap(viewer, visual);
		// unregister nested visuals that are not controlled by other parts
		FXPartUtils.unregisterNestedVisuals(this, viewer.getVisualPartMap(),
				visual);
	}
}

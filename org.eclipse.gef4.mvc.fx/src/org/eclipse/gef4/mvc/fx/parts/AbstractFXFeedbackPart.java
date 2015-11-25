/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.mvc.parts.AbstractFeedbackPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

/**
 * Abstract base implementation for a JavaFX-specific {@link IFeedbackPart}.
 *
 * @author anyssen
 *
 * @param <V>
 *            The visual {@link Node} used by this
 *            {@link AbstractFXFeedbackPart}.
 */
abstract public class AbstractFXFeedbackPart<V extends Node>
		extends AbstractFeedbackPart<Node, V> {

	private final VisualChangeListener visualListener = new VisualChangeListener() {
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

	@Override
	protected void attachToAnchorageVisual(
			IVisualPart<Node, ? extends Node> anchorage, String role) {
		visualListener.register(anchorage.getVisual(), getVisual());
	}

	@Override
	protected void detachFromAnchorageVisual(
			IVisualPart<Node, ? extends Node> anchorage, String role) {
		visualListener.unregister();
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

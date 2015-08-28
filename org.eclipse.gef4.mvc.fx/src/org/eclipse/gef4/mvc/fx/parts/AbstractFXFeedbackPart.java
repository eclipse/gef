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

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.mvc.parts.AbstractFeedbackPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * Abstract base implementation for a JavaFX-specific {@link IFeedbackPart}.
 *
 * @author anyssen
 *
 * @param <N>
 *            The visual {@link Node} used by this
 *            {@link AbstractFXFeedbackPart}.
 */
abstract public class AbstractFXFeedbackPart<N extends Node>
		extends AbstractFeedbackPart<Node, N> {

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

}

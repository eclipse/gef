/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.parts;

import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.mvc.fx.parts.FXSelectionLinkFeedbackPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

/**
 * The {@link ZestFxEdgeLinkFeedbackPart} displays link feedback (a dashes line)
 * between an {@link EdgeContentPart} and its anchored {@link EdgeLabelPart}.
 *
 * @author mwienand
 *
 */
public class ZestFxEdgeLinkFeedbackPart extends FXSelectionLinkFeedbackPart {

	private final VisualChangeListener visualListener = new VisualChangeListener() {
		@Override
		protected void boundsInLocalChanged(Bounds oldBounds, Bounds newBounds) {
			refreshVisual();
		}

		@Override
		protected void localToParentTransformChanged(Node observed, Transform oldTransform, Transform newTransform) {
			refreshVisual();
		}
	};

	/**
	 * Default constructor.
	 */
	public ZestFxEdgeLinkFeedbackPart() {
	}

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		if (anchorage instanceof EdgeContentPart) {
			EdgeContentPart edgeContentPart = (EdgeContentPart) anchorage;
			// find EdgeLabelPart in the anchorages of the EdgeContentPart
			for (IVisualPart<Node, ? extends Node> anchored : edgeContentPart.getAnchoreds()) {
				if (anchored instanceof EdgeLabelPart) {
					visualListener.register(anchored.getVisual(), getVisual());
					break;
				}
			}
		}
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		visualListener.unregister();
	}

}

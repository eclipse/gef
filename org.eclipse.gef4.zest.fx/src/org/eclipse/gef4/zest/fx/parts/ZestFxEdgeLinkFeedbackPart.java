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

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.parts.FXSelectionLinkFeedbackPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Provider;

public class ZestFxEdgeLinkFeedbackPart extends FXSelectionLinkFeedbackPart {

	private final VisualChangeListener visualListener = new VisualChangeListener() {
		@Override
		protected void boundsInLocalChanged(Bounds oldBounds, Bounds newBounds) {
			refreshVisual();
		}

		@Override
		protected void localToParentTransformChanged(Node observed,
				Transform oldTransform, Transform newTransform) {
			refreshVisual();
		}
	};

	public ZestFxEdgeLinkFeedbackPart(
			Provider<IGeometry> feedbackGeometryProvider) {
		super(feedbackGeometryProvider);
	}

	@Override
	protected void attachToAnchorageVisual(
			IVisualPart<Node, ? extends Node> anchorage, String role) {
		if (anchorage instanceof EdgeContentPart) {
			EdgeContentPart edgeContentPart = (EdgeContentPart) anchorage;
			// find EdgeLabelPart in the anchorages of the EdgeContentPart
			for (IVisualPart<Node, ? extends Node> anchored : edgeContentPart
					.getAnchoreds()) {
				if (anchored instanceof EdgeLabelPart) {
					visualListener.register(anchored.getVisual(), getVisual());
					break;
				}
			}
		}
	}

	@Override
	protected void detachFromAnchorageVisual(
			IVisualPart<Node, ? extends Node> anchorage, String role) {
		visualListener.unregister();
	}

}

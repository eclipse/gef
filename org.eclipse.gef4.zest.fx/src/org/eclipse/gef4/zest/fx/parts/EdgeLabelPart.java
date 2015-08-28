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
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.parts.AbstractVisualPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class EdgeLabelPart extends AbstractVisualPart<Node, Text> {

	private VisualChangeListener vcl = new VisualChangeListener() {
		@Override
		protected void boundsInLocalChanged(Bounds oldBounds, Bounds newBounds) {
			refreshVisual();
		}

		@Override
		protected void localToParentTransformChanged(Node observed, Transform oldTransform, Transform newTransform) {
			refreshVisual();
		}
	};
	private Translate translate;

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		vcl.register(anchorage.getVisual(), getVisual());
	}

	@Override
	protected Text createVisual() {
		Text text = new Text();
		text.setTextOrigin(VPos.TOP);
		text.setManaged(false);
		text.setPickOnBounds(true);
		// add translation transform to the Text
		translate = new Translate();
		text.getTransforms().add(translate);
		return text;
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		vcl.unregister();
	}

	@Override
	protected void doRefreshVisual(Text visual) {
		EdgeContentPart edgeContentPart = getHost();
		if (edgeContentPart == null) {
			return;
		}
		// determine bounds of anchorage visual
		Rectangle bounds = edgeContentPart.getVisual().getCurveNode().getGeometry().getBounds();
		// determine text bounds
		Bounds textBounds = getVisual().getLayoutBounds();
		// compute label position
		visual.setTranslateX(bounds.getX() + bounds.getWidth() / 2 - textBounds.getWidth() / 2);
		visual.setTranslateY(bounds.getY() + bounds.getHeight() / 2 - textBounds.getHeight());
	}

	public EdgeContentPart getHost() {
		return getAnchorages().isEmpty() ? null : (EdgeContentPart) getAnchorages().keys().iterator().next();
	}

	public Translate getOffset() {
		return translate;
	}

}
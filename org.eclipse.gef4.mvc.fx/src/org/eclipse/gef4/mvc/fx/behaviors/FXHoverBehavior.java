/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.behaviors;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.behaviors.AbstractHoverBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXBoundsFeedbackPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;

// TODO: this class is a hack; do not use effect for feedback
public class FXHoverBehavior extends AbstractHoverBehavior<Node> {

	private IHandlePart<Node> feedbackPart;
	
	private void showFeedback(Effect effect) {
		feedbackPart = new FXBoundsFeedbackPart((IContentPart<Node>) getHost(), getFeedbackGeometryProvider(), Color.web("#5a61af"), effect);
		getHost().getRoot().addChild(feedbackPart);
		getHost().addAnchored(feedbackPart);
	}

	@Override
	protected IGeometry getFeedbackGeometry() {
		return JavaFX2Geometry.toRectangle(getHost().getVisual().getLayoutBounds());
	}

	@Override
	protected void hideFeedback() {
		if (feedbackPart != null) {
			getHost().removeAnchored(feedbackPart);
			getHost().getRoot().removeChild(feedbackPart);
			feedbackPart = null;
		}
	}

	@Override
	protected void showFeedback() {
		showFeedback(getFeedbackEffect());
	}

	protected Effect getFeedbackEffect() {
		DropShadow effect = new DropShadow();
		effect.setRadius(5);
		return effect;
	}

}

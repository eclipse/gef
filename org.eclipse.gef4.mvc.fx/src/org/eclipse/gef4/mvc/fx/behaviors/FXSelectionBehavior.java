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
package org.eclipse.gef4.mvc.fx.behaviors;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.behaviors.AbstractSelectionBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXBoundsFeedbackPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;

public class FXSelectionBehavior extends AbstractSelectionBehavior<Node> {

	private IHandlePart<Node> feedbackPart;

	@Override
	protected IGeometry getFeedbackGeometry() {
		return JavaFX2Geometry.toRectangle(getHost().getVisual().getLayoutBounds());
	}

	@Override
	protected IGeometry getHandleGeometry() {
		return getFeedbackGeometry();
	}

	private void showFeedback(Paint stroke, Effect effect) {
		feedbackPart = new FXBoundsFeedbackPart((IContentPart<Node>) getHost(),
				getFeedbackGeometryProvider(), stroke, effect);
		// TODO: use BehaviorUtils when migrating to explicit IFeedbackPart
		getHost().getRoot().addChild(feedbackPart);
		getHost().addAnchored(feedbackPart);
	}

	@Override
	protected void hideFeedback() {
		if (feedbackPart != null) {
			// TODO: use BehaviorUtils when migrating to explicit IFeedbackPart
			getHost().removeAnchored(feedbackPart);
			getHost().getRoot().removeChild(feedbackPart);
			feedbackPart = null;
		}
	}

	@Override
	protected void showPrimaryFeedback() {
		showFeedback(Color.web("#5a61af"), getPrimarySelectionFeedbackEffect());
	}

	@Override
	protected void showSecondaryFeedback() {
		showFeedback(Color.web("#5a61af"),
				getSecondarySelectionFeedbackEffect());
	}

	protected Effect getPrimarySelectionFeedbackEffect() {
		DropShadow effect = new DropShadow();
		effect.setColor(Color.web("#d5faff"));
		effect.setRadius(5);
		effect.setSpread(0.6);
		return effect;
	}

	protected Effect getSecondarySelectionFeedbackEffect() {
		return null;
	}

}

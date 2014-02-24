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

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.behaviors.AbstractSelectionBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXBoundsFeedbackPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;

public class FXSelectionBehavior extends
		AbstractSelectionBehavior<Node> {

	private IHandlePart<Node> feedbackPart;

	private void showFeedback(Effect effect) {
		feedbackPart = new FXBoundsFeedbackPart(
				((IContentPart<Node>) getHost()).getVisual(), new FXGeometryNode<Rectangle>(new Rectangle()), effect);
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
		showFeedback(getPrimarySelectionFeedbackEffect());
	}

	@Override
	protected void showSecondaryFeedback() {
		showFeedback(getSecondarySelectionFeedbackEffect());
	}

	protected Effect getPrimarySelectionFeedbackEffect() {
		DropShadow effect = new DropShadow();
		effect.setRadius(5);
		return effect;
	}

	protected Effect getSecondarySelectionFeedbackEffect() {
		DropShadow effect = new DropShadow();
		effect.setRadius(5);
		return effect;
	}
	
}

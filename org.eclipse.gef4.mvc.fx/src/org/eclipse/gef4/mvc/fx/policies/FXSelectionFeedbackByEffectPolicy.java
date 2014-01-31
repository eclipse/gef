package org.eclipse.gef4.mvc.fx.policies;

import java.util.Collections;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;

import org.eclipse.gef4.mvc.fx.parts.FXBoundsFeedbackPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractSelectionFeedbackPolicy;

public class FXSelectionFeedbackByEffectPolicy extends
		AbstractSelectionFeedbackPolicy<Node> {

	@Override
	public void setHost(IVisualPart<Node> host) {
		if (!(host instanceof IContentPart)) {
			throw new IllegalArgumentException(
					"May only apply this policy to IContentParts.");
		}
		super.setHost(host);
	}

	private IHandlePart<Node> feedbackPart;

	private void showFeedback(Effect effect) {
		feedbackPart = new FXBoundsFeedbackPart(
				((IContentPart<Node>) getHost()).getVisual(), effect);
		getHost().getRoot().addHandleParts(
				Collections.<IHandlePart<Node>> singletonList(feedbackPart));
		getHost().addAnchored(feedbackPart);
	}

	@Override
	protected void hideFeedback() {
		if (feedbackPart != null) {
			getHost().removeAnchored(feedbackPart);
			getHost()
					.getRoot()
					.removeHandleParts(
							Collections
									.<IHandlePart<Node>> singletonList(feedbackPart));
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

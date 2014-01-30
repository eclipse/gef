package org.eclipse.gef4.mvc.fx.policies;

import java.util.ArrayList;
import java.util.List;

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
		if(!(host instanceof IContentPart)){
			throw new IllegalArgumentException("May only apply this policy to IContentParts.");
		}
		super.setHost(host);
	}
	
	private List<IHandlePart<Node>> feedbackParts = new ArrayList<IHandlePart<Node>>();

	private void showFeedback(Effect effect) {
		// traverse target parts and create a feedback part for each
		feedbackParts.add(new FXBoundsFeedbackPart((IContentPart<Node>)getHost(), effect));
		getHost().getRoot().addHandleParts(feedbackParts);
	}

	@Override
	protected void hideFeedback() {
		getHost().getRoot().removeHandleParts(feedbackParts);
		feedbackParts.clear();
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

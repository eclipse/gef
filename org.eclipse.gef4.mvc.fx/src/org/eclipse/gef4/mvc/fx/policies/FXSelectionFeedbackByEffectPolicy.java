package org.eclipse.gef4.mvc.fx.policies;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import org.eclipse.gef4.mvc.policies.AbstractSelectionFeedbackPolicy;

public class FXSelectionFeedbackByEffectPolicy extends AbstractSelectionFeedbackPolicy<Node> {

	@Override
	protected void hideFeedback() {
		getHost().getVisual().setEffect(null);
	}
	
	@Override
	protected void showPrimaryFeedback() {
		DropShadow effect = new DropShadow();
		effect.setColor(new Color(0, 0, 0, 1));
		effect.setOffsetX(5);
		effect.setOffsetY(5);
		effect.setRadius(5);
		getHost().getVisual().setEffect(effect);
	}
	
	@Override
	protected void showSecondaryFeedback() {
		DropShadow effect = new DropShadow();
		effect.setColor(new Color(0.5, 0.5, 0.5, 1));
		effect.setOffsetX(5);
		effect.setOffsetY(5);
		effect.setRadius(5);
		getHost().getVisual().setEffect(effect);
	}

}

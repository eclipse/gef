package org.eclipse.gef4.mvc.fx.policies;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import org.eclipse.gef4.mvc.policies.AbstractHoverFeedbackPolicy;

// TODO: this class is a hack; do not use effect for feedback
public class FXHoverFeedbackByEffectPolicy extends AbstractHoverFeedbackPolicy<Node> {

	@Override
	protected void hideFeedback() {
		if(!getHost().getRoot().getViewer().getSelectionModel().getSelected().contains(getHost())){
			getHost().getVisual().setEffect(null);
		}
	}
	
	@Override
	protected void showFeedback() {
		DropShadow effect = new DropShadow();
		effect.setColor(new Color(0.5, 0.5, 0.5, 1));
		effect.setOffsetX(5);
		effect.setOffsetY(5);
		effect.setRadius(5);
		getHost().getVisual().setEffect(effect);
	}

}

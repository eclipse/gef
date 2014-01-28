package org.eclipse.gef4.mvc.fx.policies;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import org.eclipse.gef4.mvc.fx.parts.FXRootVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractHoverFeedbackPolicy;

public class FXHoverFeedbackByEffectPolicy extends AbstractHoverFeedbackPolicy<Node> {

	@Override
	protected void hideFeedback() {
	}
	
	@Override
	protected void showFeedback() {
	}
	
	public FXHoverFeedbackByEffectPolicy() {
	}

}

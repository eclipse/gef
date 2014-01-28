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
import org.eclipse.gef4.mvc.policies.AbstractSelectionFeedbackPolicy;

public class FXSelectionFeedbackPolicy extends AbstractSelectionFeedbackPolicy<Node> {

	private static final Paint INVISIBLE = new Color(0, 0, 0, 0);
	private Group feedback;
	
	@Override
	public void applyFeedbackEffect() {
		feedback.setEffect(createSelectionEffect());
	}

	public Effect createSelectionEffect() {
		DropShadow effect = new DropShadow();
		effect.setOffsetX(0);
		effect.setOffsetY(0);
		effect.setRadius(10);
		effect.setColor(new Color(0, 0, 1, 1));
		effect.setSpread(0.5);
		return effect;
	}

	@Override
	public void showFeedback() {
		FXRootVisualPart root = (FXRootVisualPart) getHost().getRoot();
		// guard against editor contents changes
		if (root == null)
			return;
		Pane feedbackLayer = root.getFeedbackLayer();
		feedbackLayer.getChildren().add(feedback);
	}

	@Override
	public void hideFeedback() {
		if (feedback != null) {
			Pane pane = (Pane) feedback.getParent();
			if (pane != null)
				pane.getChildren().remove(feedback);
			feedback = null;
		}
	}

	@Override
	public void applyProperties(Node selectionVisual, Node feedbackVisual) {
		applyPosition(selectionVisual, feedbackVisual);
		if (selectionVisual instanceof Shape && feedbackVisual instanceof Shape) {
			applyPaint((Shape) selectionVisual, (Shape) feedbackVisual);
		}
	}

	@Override
	public void addFeedbackVisual(Node feedbackVisual) {
		feedback.getChildren().add(feedbackVisual);
	}

	@Override
	public void initFeedback() {
		feedback = new Group();
	}
	
	/**
	 * Applies visualization properties of the selection visual to the feedback
	 * visual. Per default, stroke and fill are copied.
	 * 
	 * @param selectionVisual
	 * @param feedbackVisual
	 */
	protected void applyPaint(Shape selectionVisual, Shape feedbackVisual) {
		feedbackVisual.setStroke(selectionVisual.getStroke());
		feedbackVisual.setFill(INVISIBLE);
	}

	/**
	 * Applies layout properties of the selection visual to the feedback visual.
	 * Per default, layout-x, layout-y, translate-x, translate-y, and transforms
	 * are copied.
	 * 
	 * @param selectionVisual
	 * @param feedbackVisual
	 */
	protected void applyPosition(Node selectionVisual, Node feedbackVisual) {
		feedbackVisual.setLayoutX(selectionVisual.getLayoutX());
		feedbackVisual.setLayoutY(selectionVisual.getLayoutY());

		feedbackVisual.setTranslateX(selectionVisual.getTranslateX());
		feedbackVisual.setTranslateY(selectionVisual.getTranslateY());

		feedbackVisual.getTransforms().addAll(selectionVisual.getTransforms());
	}

	@Override
	public Node createFeedback(Node selectionVisual) {
		Bounds layoutBounds = selectionVisual.getLayoutBounds();
		Rectangle rectangle = new Rectangle(layoutBounds.getMinX(),
				layoutBounds.getMinY(), layoutBounds.getWidth(),
				layoutBounds.getHeight());
		rectangle.setFill(new Color(0, 0, 0, 0));
		rectangle.setStroke(new Color(0, 0, 1, 1));
		return rectangle;
	}

}

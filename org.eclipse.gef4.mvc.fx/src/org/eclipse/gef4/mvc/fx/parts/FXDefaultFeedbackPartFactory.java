package org.eclipse.gef4.mvc.fx.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;

import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXHoverBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class FXDefaultFeedbackPartFactory implements IFeedbackPartFactory<Node> {

	@Inject
	private Injector injector;

	@Override
	public List<IFeedbackPart<Node>> createFeedbackParts(
			List<IContentPart<Node>> targets, IBehavior<Node> contextBehavior,
			Map<Object, Object> contextMap) {
		// no targets
		if (targets == null || targets.isEmpty()) {
			return Collections.emptyList();
		}

		// differentiate creation context
		if (contextBehavior instanceof FXSelectionBehavior) {
			return createSelectionFeedbackParts(targets,
					(FXSelectionBehavior) contextBehavior, contextMap);
		} else if (contextBehavior instanceof FXHoverBehavior) {
			return createHoverFeedbackParts(targets,
					(FXHoverBehavior) contextBehavior, contextMap);
		}

		// unknown creation context, do not create handles
		return Collections.emptyList();
	}

	protected List<IFeedbackPart<Node>> createHoverFeedbackParts(
			List<IContentPart<Node>> targets, FXHoverBehavior hoverBehavior,
			Map<Object, Object> contextMap) {
		// no feedback for multiple selection
		if (targets.size() > 1) {
			return Collections.emptyList();
		}

		final IContentPart<Node> targetPart = targets.get(0);
		List<IFeedbackPart<Node>> feedbackParts = new ArrayList<IFeedbackPart<Node>>();
		FXGeometricFeedbackPart part = new FXGeometricFeedbackPart(targetPart,
				hoverBehavior.getFeedbackGeometryProvider(contextMap),
				Color.web("#5a61af"),
				hoverBehavior.getHoverFeedbackEffect(contextMap));
		injector.injectMembers(part);
		feedbackParts.add(part);
		return feedbackParts;
	}

	protected List<IFeedbackPart<Node>> createSelectionFeedbackParts(
			List<IContentPart<Node>> targets,
			FXSelectionBehavior selectionBehavior,
			Map<Object, Object> contextMap) {
		// no feedback for multiple selection
		if (targets.size() > 1) {
			return Collections.emptyList();
		}

		// single selection, create feedback based on geometry
		final IContentPart<Node> targetPart = targets.get(0);
		List<IFeedbackPart<Node>> feedbackParts = new ArrayList<IFeedbackPart<Node>>();
		boolean isPrimaryFeedback = selectionBehavior.getHost().getRoot()
				.getViewer().getSelectionModel().getSelected().get(0) == targetPart;
		FXGeometricFeedbackPart part = new FXGeometricFeedbackPart(targetPart,
				selectionBehavior.getFeedbackGeometryProvider(contextMap),
				isPrimaryFeedback ? Color.BLACK : Color.GREY,
				isPrimaryFeedback ? getPrimarySelectionFeedbackEffect()
						: getSecondarySelectionFeedbackEffect());
		injector.injectMembers(part);
		feedbackParts.add(part);
		return feedbackParts;
	}

	// TODO: move to selection behavior
	protected Effect getPrimarySelectionFeedbackEffect() {
		DropShadow effect = new DropShadow();
		effect.setColor(Color.BLACK);
		effect.setRadius(5);
		effect.setSpread(0.6);
		return effect;
	}

	// TODO: move to selection behavior
	protected Effect getSecondarySelectionFeedbackEffect() {
		DropShadow effect = new DropShadow();
		effect.setColor(Color.GREY);
		effect.setRadius(5);
		effect.setSpread(0.6);
		return effect;
	}

}

package org.eclipse.gef4.mvc.fx.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javafx.scene.Node;

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

		List<IFeedbackPart<Node>> feedbackParts = new ArrayList<IFeedbackPart<Node>>();
		FXGeometricFeedbackPart part = new FXHoverFeedbackPart(
				hoverBehavior.getFeedbackGeometryProvider(contextMap));
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
		List<IFeedbackPart<Node>> feedbackParts = new ArrayList<IFeedbackPart<Node>>();
		FXGeometricFeedbackPart part = new FXSelectionFeedbackPart(
				selectionBehavior.getFeedbackGeometryProvider(contextMap));
		injector.injectMembers(part);
		feedbackParts.add(part);
		return feedbackParts;
	}

}

package org.eclipse.gef4.mvc.fx.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.scene.Node;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class FXDefaultFeedbackPartFactory implements IFeedbackPartFactory<Node> {

	public static final String SELECTION_FEEDBACK_GEOMETRY_PROVIDER = "SELECTION_OUTLINE_FEEDBACK_GEOMETRY_PROVIDER";
	public static final String HOVER_FEEDBACK_GEOMETRY_PROVIDER = "HOVER_FEEDBACK_GEOMETRY_PROVIDER";

	@Inject
	private Injector injector;

	/**
	 * Creates a dotted feedback-line between an anchored part and its
	 * anchorage.
	 *
	 * @param anchored
	 * @param anchorage
	 * @param anchorageRole
	 * @return The {@link IFeedbackPart} for this anchor link, or
	 *         <code>null</code> if no feedback should be rendered.
	 */
	protected IFeedbackPart<Node> createAnchorLinkFeedbackPart(
			final IContentPart<Node> anchored,
			final IContentPart<Node> anchorage, String anchorageRole) {

		// only show anchor link feedback if anchorage and anchored provider is
		// not null (and anchored is no connection)
		if (!(anchored.getVisual() instanceof IFXConnection)) {
			Provider<IGeometry> anchorageGeometryProvider = anchorage
					.getAdapter(AdapterKey.get(Provider.class,
							SELECTION_FEEDBACK_GEOMETRY_PROVIDER));
			Provider<IGeometry> anchoredGeometryProvider = anchored
					.getAdapter(AdapterKey.get(Provider.class,
							SELECTION_FEEDBACK_GEOMETRY_PROVIDER));
			if (anchoredGeometryProvider != null
					&& anchorageGeometryProvider != null) {
				return new FXSelectionLinkFeedbackPart(anchorage, anchored,
						anchoredGeometryProvider, anchorageGeometryProvider);
			}
		}
		return null;
	}

	@Override
	public List<IFeedbackPart<Node>> createFeedbackParts(
			List<IContentPart<Node>> targets, IBehavior<Node> contextBehavior,
			Map<Object, Object> contextMap) {
		// no targets
		if (targets == null || targets.isEmpty()) {
			return Collections.emptyList();
		}

		// differentiate creation context
		if (contextBehavior instanceof SelectionBehavior) {
			return createSelectionFeedbackParts(targets,
					(SelectionBehavior<Node>) contextBehavior, contextMap);
		} else if (contextBehavior instanceof HoverBehavior) {
			return createHoverFeedbackParts(targets,
					(HoverBehavior<Node>) contextBehavior, contextMap);
		}

		// unknown creation context, do not create handles
		return Collections.emptyList();
	}

	protected List<IFeedbackPart<Node>> createHoverFeedbackParts(
			List<IContentPart<Node>> targets,
			HoverBehavior<Node> hoverBehavior, Map<Object, Object> contextMap) {
		// no feedback for empty or multiple selection
		if (targets.size() == 0 || targets.size() > 1) {
			return Collections.emptyList();
		}

		List<IFeedbackPart<Node>> feedbackParts = new ArrayList<IFeedbackPart<Node>>();

		IContentPart<Node> target = targets.iterator().next();

		Provider<IGeometry> hoverFeedbackGeometryProvider = target
				.getAdapter(AdapterKey.get(Provider.class,
						HOVER_FEEDBACK_GEOMETRY_PROVIDER));
		if (hoverFeedbackGeometryProvider != null) {
			AbstractFXGeometricFeedbackPart part = new FXHoverFeedbackPart(
					hoverFeedbackGeometryProvider);
			injector.injectMembers(part);
			feedbackParts.add(part);
		}

		return feedbackParts;
	}

	protected List<IFeedbackPart<Node>> createSelectionFeedbackParts(
			List<IContentPart<Node>> targets,
			SelectionBehavior<Node> selectionBehavior,
			Map<Object, Object> contextMap) {
		// no feedback for empty or multiple selection
		if (targets.size() == 0 || targets.size() > 1) {
			return Collections.emptyList();
		}

		// single selection, create selection feedback based on geometry
		List<IFeedbackPart<Node>> feedbackParts = new ArrayList<IFeedbackPart<Node>>();

		// selection outline feedback
		IContentPart<Node> target = targets.iterator().next();
		Provider<IGeometry> selectionFeedbackGeometryProvider = target
				.getAdapter(AdapterKey.get(Provider.class,
						SELECTION_FEEDBACK_GEOMETRY_PROVIDER));
		if (selectionFeedbackGeometryProvider != null) {
			AbstractFXGeometricFeedbackPart selectionFeedbackPart = new FXSelectionFeedbackPart(
					selectionFeedbackGeometryProvider);
			injector.injectMembers(selectionFeedbackPart);
			feedbackParts.add(selectionFeedbackPart);
		}

		// selection anchor link feedback parts
		for (IContentPart<Node> t : targets) {
			if (!t.getAnchorages().isEmpty()) {
				for (Entry<IVisualPart<Node>, String> entry : t.getAnchorages()
						.entries()) {
					if (entry.getKey() instanceof IContentPart) {
						IFeedbackPart<Node> anchorLinkFeedbackPart = createAnchorLinkFeedbackPart(
								t, (IContentPart<Node>) entry.getKey(),
								entry.getValue());
						if (anchorLinkFeedbackPart != null) {
							injector.injectMembers(anchorLinkFeedbackPart);
							feedbackParts.add(anchorLinkFeedbackPart);
						}
					}
				}
			}
		}

		return feedbackParts;
	}
}

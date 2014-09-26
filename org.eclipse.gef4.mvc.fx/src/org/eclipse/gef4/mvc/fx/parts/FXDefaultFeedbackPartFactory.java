package org.eclipse.gef4.mvc.fx.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.scene.Node;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.anchors.FXChopBoxComputationStrategy;
import org.eclipse.gef4.fx.nodes.FXConnection;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
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

	public static final String SELECTION_FEEDBACK_GEOMETRY_PROVIDER = "SELECTION_FEEDBACK_GEOMETRY_PROVIDER";

	public static final String SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER = "SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER";

	public static final String HOVER_FEEDBACK_GEOMETRY_PROVIDER = "HOVER_FEEDBACK_GEOMETRY_PROVIDER";

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

		final IContentPart<Node> target = targets.iterator().next();

		final Provider<IGeometry> hoverFeedbackGeometryProvider = target
				.<Provider<IGeometry>> getAdapter(AdapterKey.get(
						Provider.class, HOVER_FEEDBACK_GEOMETRY_PROVIDER));
		if (hoverFeedbackGeometryProvider != null) {
			Provider<IGeometry> geometryInSceneProvider = new Provider<IGeometry>() {

				@Override
				public IGeometry get() {
					return FXUtils.localToScene(target.getVisual(),
							hoverFeedbackGeometryProvider.get());
				}
			};

			FXHoverFeedbackPart part = new FXHoverFeedbackPart(
					geometryInSceneProvider);
			injector.injectMembers(part);
			feedbackParts.add(part);
		}

		return feedbackParts;
	}

	/**
	 * Creates a dotted feedback-line between an anchored part and its
	 * anchorage.
	 *
	 * @param anchored
	 *            The anchored {@link IContentPart}.
	 * @param anchorage
	 *            The anchorage {@link IContentPart}.
	 * @param anchorageRole
	 *            The role under which the anchorage is stored at the anchored.
	 * @return The {@link IFeedbackPart} for this anchor link, or
	 *         <code>null</code> if no feedback should be rendered.
	 */
	protected IFeedbackPart<Node> createLinkFeedbackPart(
			final IContentPart<Node> anchored,
			final IContentPart<Node> anchorage, String anchorageRole) {

		// only show anchor link feedback if anchorage and anchored provider is
		// not null (and anchored is no connection)
		if (!(anchored.getVisual() instanceof FXConnection)) {
			// TODO: need own providers for link feedback (computation might be
			// different)
			final Provider<IGeometry> anchorageGeometryProvider = anchorage
					.<Provider<IGeometry>> getAdapter(AdapterKey.get(
							Provider.class,
							SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER));
			final Provider<IGeometry> anchoredGeometryProvider = anchored
					.<Provider<IGeometry>> getAdapter(AdapterKey.get(
							Provider.class,
							SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER));
			if (anchoredGeometryProvider != null
					&& anchorageGeometryProvider != null) {
				if (anchoredGeometryProvider == null
						|| anchorageGeometryProvider == null) {
					return null;
				}
				Provider<IGeometry> linkFeedbackGeometryProvider = new Provider<IGeometry>() {

					// TODO: inject
					private final FXChopBoxComputationStrategy computationStrategy = new FXChopBoxComputationStrategy();

					private Point computePosition(Node anchoredVisual,
							IGeometry anchoredGeometryInLocal,
							Node anchorageVisual,
							IGeometry anchorageGeometryInLocal) {
						return computationStrategy.computePositionInScene(
								anchorageVisual, anchorageGeometryInLocal,
								anchoredVisual, computationStrategy
										.computeReferencePointInLocal(
												anchoredVisual,
												anchoredGeometryInLocal));
					}

					@Override
					public IGeometry get() {
						// get anchored visual and geometry
						Node anchoredVisual = anchored.getVisual();
						IGeometry anchoredGeometryInLocal = anchoredGeometryProvider
								.get();

						// get anchorage visual and geometry
						Node anchorageVisual = anchorage.getVisual();
						IGeometry anchorageGeometryInLocal = anchorageGeometryProvider
								.get();

						// determine link source point
						Point sourcePointInScene = computePosition(
								anchoredVisual, anchoredGeometryInLocal,
								anchorageVisual, anchorageGeometryInLocal);

						// determine link target point
						Point targetPointInScene = computePosition(
								anchorageVisual, anchorageGeometryInLocal,
								anchoredVisual, anchoredGeometryInLocal);

						// construct link line
						return new Line(sourcePointInScene, targetPointInScene);
					}

				};
				return new FXSelectionLinkFeedbackPart(
						linkFeedbackGeometryProvider);
			}
		}
		return null;
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
		final IContentPart<Node> target = targets.iterator().next();
		final Provider<IGeometry> selectionFeedbackGeometryProvider = target
				.<Provider<IGeometry>> getAdapter(AdapterKey.get(
						Provider.class, SELECTION_FEEDBACK_GEOMETRY_PROVIDER));
		if (selectionFeedbackGeometryProvider != null) {
			Provider<IGeometry> geometryInSceneProvider = new Provider<IGeometry>() {

				@Override
				public IGeometry get() {
					return FXUtils.localToScene(target.getVisual(),
							selectionFeedbackGeometryProvider.get());
				}
			};

			FXSelectionFeedbackPart selectionFeedbackPart = new FXSelectionFeedbackPart(
					geometryInSceneProvider);
			injector.injectMembers(selectionFeedbackPart);
			feedbackParts.add(selectionFeedbackPart);
		}

		// selection link feedback parts
		for (IContentPart<Node> t : targets) {
			if (!t.getAnchorages().isEmpty()) {
				for (Entry<IVisualPart<Node>, String> entry : t.getAnchorages()
						.entries()) {
					if (entry.getKey() instanceof IContentPart) {
						IFeedbackPart<Node> anchorLinkFeedbackPart = createLinkFeedbackPart(
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

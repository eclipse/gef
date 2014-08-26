package org.eclipse.gef4.mvc.fx.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.scene.Node;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
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

	public static final String SELECTION_FEEDBACK_GEOMETRY_PROVIDER = "SELECTION_OUTLINE_FEEDBACK_GEOMETRY_PROVIDER";

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
				.getAdapter(AdapterKey.get(Provider.class,
						HOVER_FEEDBACK_GEOMETRY_PROVIDER));
		if (hoverFeedbackGeometryProvider != null) {
			Provider<IGeometry> geometryInSceneProvider = new Provider<IGeometry>() {

				@Override
				public IGeometry get() {
					return FXUtils.localToScene(target.getVisual(),
							hoverFeedbackGeometryProvider.get());
				}
			};

			AbstractFXBoundsFeedbackPart part = new FXHoverFeedbackPart(
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
	 * @param anchorage
	 * @param anchorageRole
	 * @return The {@link IFeedbackPart} for this anchor link, or
	 *         <code>null</code> if no feedback should be rendered.
	 */
	protected IFeedbackPart<Node> createLinkFeedbackPart(
			final IContentPart<Node> anchored,
			final IContentPart<Node> anchorage, String anchorageRole) {

		// only show anchor link feedback if anchorage and anchored provider is
		// not null (and anchored is no connection)
		if (!(anchored.getVisual() instanceof IFXConnection)) {
			// TODO: need own providers for link feedback (computation might be
			// different)
			final Provider<IGeometry> anchorageGeometryProvider = anchorage
					.getAdapter(AdapterKey.get(Provider.class,
							SELECTION_FEEDBACK_GEOMETRY_PROVIDER));
			final Provider<IGeometry> anchoredGeometryProvider = anchored
					.getAdapter(AdapterKey.get(Provider.class,
							SELECTION_FEEDBACK_GEOMETRY_PROVIDER));
			if (anchoredGeometryProvider != null
					&& anchorageGeometryProvider != null) {
				if (anchoredGeometryProvider == null
						|| anchorageGeometryProvider == null) {
					return null;
				}
				Provider<IGeometry> linkFeedbackGeometryProvider = new Provider<IGeometry>() {

					@Override
					public IGeometry get() {
						IGeometry linkSourceGeometryInScene = FXUtils
								.localToScene(anchored.getVisual(),
										anchoredGeometryProvider.get());
						ICurve linkSourceOutlineInScene = linkSourceGeometryInScene instanceof ICurve ? (ICurve) linkSourceGeometryInScene
								: ((IShape) linkSourceGeometryInScene)
										.getOutline();

						IGeometry linkTargetGeometryInScene = FXUtils
								.localToScene(anchorage.getVisual(),
										anchorageGeometryProvider.get());
						ICurve linkTargetOutlineInScene = linkTargetGeometryInScene instanceof ICurve ? (ICurve) linkTargetGeometryInScene
								: ((IShape) linkTargetGeometryInScene)
										.getOutline();

						final Line centerLineInScene = new Line(
								linkSourceGeometryInScene.getBounds()
										.getCenter(), linkTargetGeometryInScene
										.getBounds().getCenter());

						Point sourcePointInScene = linkSourceOutlineInScene
								.getNearestIntersection(centerLineInScene,
										linkTargetGeometryInScene.getBounds()
												.getCenter());
						if (sourcePointInScene == null) {
							sourcePointInScene = linkSourceGeometryInScene
									.getBounds().getCenter();
						}
						Point targetPointInScene = linkTargetOutlineInScene
								.getNearestIntersection(centerLineInScene,
										linkSourceGeometryInScene.getBounds()
												.getCenter());
						if (targetPointInScene == null) {
							targetPointInScene = linkTargetGeometryInScene
									.getBounds().getCenter();
						}

						final Line linkLineInScene = new Line(
								sourcePointInScene, targetPointInScene);
						return linkLineInScene;
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
				.getAdapter(AdapterKey.get(Provider.class,
						SELECTION_FEEDBACK_GEOMETRY_PROVIDER));
		if (selectionFeedbackGeometryProvider != null) {
			Provider<IGeometry> geometryInSceneProvider = new Provider<IGeometry>() {

				@Override
				public IGeometry get() {
					return FXUtils.localToScene(target.getVisual(),
							selectionFeedbackGeometryProvider.get());
				}
			};

			AbstractFXBoundsFeedbackPart selectionFeedbackPart = new FXSelectionFeedbackPart(
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

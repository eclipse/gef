/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.anchors.ChopBoxAnchor;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import javafx.scene.Node;

/**
 * The {@link FXDefaultFeedbackPartFactory} is an {@link IFeedbackPartFactory}
 * implementation that is parameterized by {@link Node}.
 *
 * @author mwienand
 *
 */
public class FXDefaultFeedbackPartFactory
		implements IFeedbackPartFactory<Node> {

	/**
	 * The role name for the <code>Provider&lt;IGeometry&gt;</code> that will be
	 * used to generate selection feedback.
	 */
	public static final String SELECTION_FEEDBACK_GEOMETRY_PROVIDER = "SELECTION_FEEDBACK_GEOMETRY_PROVIDER";

	/**
	 * The role name for the <code>Provider&lt;IGeometry&gt;</code> that will be
	 * used to generate selection link feedback.
	 */
	public static final String SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER = "SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER";

	/**
	 * The role name for the <code>Provider&lt;IGeometry&gt;</code> that will be
	 * used to generate hover feedback.
	 */
	public static final String HOVER_FEEDBACK_GEOMETRY_PROVIDER = "HOVER_FEEDBACK_GEOMETRY_PROVIDER";

	@Inject
	private Injector injector;

	@Override
	public List<IFeedbackPart<Node, ? extends Node>> createFeedbackParts(
			List<? extends IVisualPart<Node, ? extends Node>> targets,
			IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {
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

	/**
	 * Creates {@link FXHoverFeedbackPart}s for the given <i>targets</i>.
	 *
	 * @param targets
	 *            The list of {@link IVisualPart}s for which hover feedback is
	 *            generated.
	 * @param hoverBehavior
	 *            The {@link HoverBehavior} that initiated the feedback
	 *            creation.
	 * @param contextMap
	 *            A map in which the state-less {@link HoverBehavior} may place
	 *            additional context information for the creation process. It
	 *            may either directly contain additional information needed by
	 *            this factory, or may be passed back by the factory to the
	 *            calling {@link HoverBehavior} to query such kind of
	 *            information (in which case it will allow the
	 *            {@link HoverBehavior} to identify the creation context).
	 * @return A list containing the created feedback parts.
	 */
	@SuppressWarnings("serial")
	protected List<IFeedbackPart<Node, ? extends Node>> createHoverFeedbackParts(
			List<? extends IVisualPart<Node, ? extends Node>> targets,
			HoverBehavior<Node> hoverBehavior, Map<Object, Object> contextMap) {
		// no feedback for empty or multiple selection
		if (targets.size() == 0 || targets.size() > 1) {
			return Collections.emptyList();
		}
		List<IFeedbackPart<Node, ? extends Node>> feedbackParts = new ArrayList<IFeedbackPart<Node, ? extends Node>>();

		final IVisualPart<Node, ? extends Node> target = targets.iterator()
				.next();
		final Provider<? extends IGeometry> hoverFeedbackGeometryProvider = target
				.getAdapter(AdapterKey
						.get(new TypeToken<Provider<? extends IGeometry>>() {
						}, HOVER_FEEDBACK_GEOMETRY_PROVIDER));
		if (hoverFeedbackGeometryProvider != null) {
			Provider<IGeometry> geometryInSceneProvider = new Provider<IGeometry>() {
				@Override
				public IGeometry get() {
					return NodeUtils.localToScene(target.getVisual(),
							hoverFeedbackGeometryProvider.get());
				}
			};
			FXHoverFeedbackPart part = injector
					.getInstance(FXHoverFeedbackPart.class);
			part.setGeometryProvider(geometryInSceneProvider);
			feedbackParts.add(part);
		}

		return feedbackParts;
	}

	/**
	 * Creates a dotted feedback-line between an anchored part and its
	 * anchorage.
	 *
	 * @param anchored
	 *            The anchored {@link IVisualPart}.
	 * @param anchorage
	 *            The anchorage {@link IVisualPart}.
	 * @param anchorageRole
	 *            The role under which the anchorage is stored at the anchored.
	 * @return The {@link IFeedbackPart} for this anchor link, or
	 *         <code>null</code> if no feedback should be rendered.
	 */
	@SuppressWarnings("serial")
	protected IFeedbackPart<Node, ? extends Node> createLinkFeedbackPart(
			final IVisualPart<Node, ? extends Node> anchored,
			final IVisualPart<Node, ? extends Node> anchorage,
			String anchorageRole) {
		// only show link feedback when anchored is no connection
		if (!(anchored.getVisual() instanceof Connection)) {
			final Provider<IGeometry> anchorageGeometryProvider = anchorage
					.<Provider<IGeometry>> getAdapter(AdapterKey.get(
							new TypeToken<Provider<? extends IGeometry>>() {
							}, SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER));
			final Provider<IGeometry> anchoredGeometryProvider = anchored
					.<Provider<IGeometry>> getAdapter(AdapterKey.get(
							new TypeToken<Provider<? extends IGeometry>>() {
							}, SELECTION_LINK_FEEDBACK_GEOMETRY_PROVIDER));
			// only show anchor link feedback if anchorage and anchored provider
			// is not null
			if (anchoredGeometryProvider != null
					&& anchorageGeometryProvider != null) {
				if (anchoredGeometryProvider == null
						|| anchorageGeometryProvider == null) {
					return null;
				}
				Provider<IGeometry> linkFeedbackGeometryProvider = new Provider<IGeometry>() {
					// TODO (#471628): inject; maybe extend IComputationStrategy
					// interface
					private final ChopBoxAnchor.IComputationStrategy.Impl computationStrategy = new ChopBoxAnchor.IComputationStrategy.Impl();

					private Point computePosition(Node anchoredVisual,
							IGeometry anchoredGeometryInLocal,
							Node anchorageVisual,
							IGeometry anchorageGeometryInLocal) {
						return computationStrategy.computePositionInScene(
								anchorageVisual, anchoredVisual,
								computationStrategy
										.computeAnchorageReferencePointInLocal(
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
				FXSelectionLinkFeedbackPart part = injector
						.getInstance(FXSelectionLinkFeedbackPart.class);
				part.setGeometryProvider(linkFeedbackGeometryProvider);
				return part;
			}
		}
		return null;
	}

	/**
	 * Creates {@link FXSelectionFeedbackPart}s and
	 * {@link FXSelectionLinkFeedbackPart}s for the <i>targets</i>.
	 *
	 * @param targets
	 *            The list of {@link IVisualPart}s for which selection feedback
	 *            is created.
	 * @param selectionBehavior
	 *            The {@link SelectionBehavior} that initiated the feedback
	 *            creation.
	 * @param contextMap
	 *            A map in which the state-less {@link SelectionBehavior} may
	 *            place additional context information for the creation process.
	 *            It may either directly contain additional information needed
	 *            by this factory, or may be passed back by the factory to the
	 *            calling {@link SelectionBehavior} to query such kind of
	 *            information (in which case it will allow the
	 *            {@link SelectionBehavior} to identify the creation context).
	 * @return A list containing the created feedback parts.
	 */
	@SuppressWarnings("serial")
	protected List<IFeedbackPart<Node, ? extends Node>> createSelectionFeedbackParts(
			List<? extends IVisualPart<Node, ? extends Node>> targets,
			SelectionBehavior<Node> selectionBehavior,
			Map<Object, Object> contextMap) {
		// no feedback for empty or multiple selection
		if (targets.size() == 0 || targets.size() > 1) {
			return Collections.emptyList();
		}

		// single selection, create selection feedback based on geometry
		List<IFeedbackPart<Node, ? extends Node>> feedbackParts = new ArrayList<IFeedbackPart<Node, ? extends Node>>();

		// selection outline feedback
		final IVisualPart<Node, ? extends Node> target = targets.iterator()
				.next();
		final Provider<IGeometry> selectionFeedbackGeometryProvider = target
				.<Provider<IGeometry>> getAdapter(AdapterKey
						.get(new TypeToken<Provider<? extends IGeometry>>() {
						}, SELECTION_FEEDBACK_GEOMETRY_PROVIDER));
		if (selectionFeedbackGeometryProvider != null) {
			Provider<IGeometry> geometryInSceneProvider = new Provider<IGeometry>() {
				@Override
				public IGeometry get() {
					return NodeUtils.localToScene(target.getVisual(),
							selectionFeedbackGeometryProvider.get());
				}
			};
			FXSelectionFeedbackPart selectionFeedbackPart = injector
					.getInstance(FXSelectionFeedbackPart.class);
			selectionFeedbackPart.setGeometryProvider(geometryInSceneProvider);
			feedbackParts.add(selectionFeedbackPart);
		}

		// selection link feedback parts
		for (IVisualPart<Node, ? extends Node> t : targets) {
			if (!t.getAnchorages().isEmpty()) {
				for (Entry<IVisualPart<Node, ? extends Node>, String> entry : t
						.getAnchorages().entries()) {
					if (entry.getKey() instanceof IVisualPart) {
						IFeedbackPart<Node, ? extends Node> anchorLinkFeedbackPart = createLinkFeedbackPart(
								t, entry.getKey(), entry.getValue());
						if (anchorLinkFeedbackPart != null) {
							feedbackParts.add(anchorLinkFeedbackPart);
						}
					}
				}
			}
		}

		return feedbackParts;
	}

}

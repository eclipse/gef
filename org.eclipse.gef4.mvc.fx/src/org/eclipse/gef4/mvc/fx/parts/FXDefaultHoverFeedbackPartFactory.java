/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.parts.IFeedbackPart;
import org.eclipse.gef4.mvc.parts.IFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import javafx.scene.Node;

/**
 *
 * @author mwienand
 *
 */
public class FXDefaultHoverFeedbackPartFactory
		implements IFeedbackPartFactory<Node> {

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
		// check creation context
		if (!(contextBehavior instanceof HoverBehavior)) {
			throw new IllegalStateException(
					"The FXDefaultHoverFeedbackPartFactory can only generate feedback parts in the context of a HoverBehavior, but the context behavior is a <"
							+ contextBehavior + ">.");
		}

		if (targets == null || targets.isEmpty()) {
			// nothing to do if we do not have targets
			return Collections.emptyList();
		}

		return createHoverFeedbackParts(targets,
				(HoverBehavior<Node>) contextBehavior, contextMap);
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
		List<IFeedbackPart<Node, ? extends Node>> feedbackParts = new ArrayList<>();

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

}

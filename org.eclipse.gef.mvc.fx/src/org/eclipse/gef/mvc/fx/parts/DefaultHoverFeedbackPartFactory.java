/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.IGeometry;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.paint.Color;

/**
 *
 * @author mwienand
 *
 */
public class DefaultHoverFeedbackPartFactory implements IFeedbackPartFactory {

	/**
	 * The binding name for the primary selection color.
	 */
	public static final String HOVER_FEEDBACK_COLOR_PROVIDER = "HOVER_FEEDBACK_COLOR_PROVIDER";

	/**
	 * The role name for the <code>Provider&lt;IGeometry&gt;</code> that will be
	 * used to generate hover feedback.
	 */
	public static final String HOVER_FEEDBACK_GEOMETRY_PROVIDER = "HOVER_FEEDBACK_GEOMETRY_PROVIDER";

	/**
	 * Defines the default {@link Color} for hover feedback.
	 */
	public static final Color DEFAULT_HOVER_FEEDBACK_COLOR = Color
			.web("#5a61af");

	@Inject
	private Injector injector;

	@Override
	public List<IFeedbackPart<? extends Node>> createFeedbackParts(
			List<? extends IVisualPart<? extends Node>> targets,
			Map<Object, Object> contextMap) {
		// check that we have targets
		if (targets == null || targets.isEmpty()) {
			throw new IllegalArgumentException(
					"Part factory is called without targets.");
		}
		if (targets.size() > 1) {
			throw new IllegalArgumentException(
					"Cannot create feedback for multiple targets.");
		}

		final IVisualPart<? extends Node> target = targets.iterator().next();
		List<IFeedbackPart<? extends Node>> feedbackParts = new ArrayList<>();

		// determine feedback geometry
		@SuppressWarnings("serial")
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
			HoverFeedbackPart part = injector
					.getInstance(HoverFeedbackPart.class);
			part.setGeometryProvider(geometryInSceneProvider);
			feedbackParts.add(part);
		}

		return feedbackParts;
	}

}

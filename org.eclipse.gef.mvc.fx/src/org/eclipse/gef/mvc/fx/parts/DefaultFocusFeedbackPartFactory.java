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
public class DefaultFocusFeedbackPartFactory implements IFeedbackPartFactory {

	/**
	 * Defines the binding name for the focus feedback color.
	 */
	public static final String FOCUS_FEEDBACK_COLOR_PROVIDER = "FOCUS_FEEDBACK_COLOR_PROVIDER";

	/**
	 * Defines the default {@link Color} for focus feedback.
	 */
	public static final Color DEFAULT_FOCUS_FEEDBACK_COLOR = Color
			.web("#8ec0fc");

	/**
	 * The role name for the <code>Provider&lt;IGeometry&gt;</code> that will be
	 * used to generate focus feedback.
	 */
	public static final String FOCUS_FEEDBACK_GEOMETRY_PROVIDER = "FOCUS_FEEDBACK_GEOMETRY_PROVIDER";

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
		// there can only be one focused part
		if (targets.size() > 1) {
			throw new IllegalArgumentException(
					"Cannot create feedback for multiple targets.");
		}

		final IVisualPart<? extends Node> target = targets.iterator().next();
		List<IFeedbackPart<? extends Node>> feedbackParts = new ArrayList<>();

		// determine feedback geometry
		@SuppressWarnings("serial")
		final Provider<? extends IGeometry> focusFeedbackGeometryProvider = target
				.getAdapter(AdapterKey
						.get(new TypeToken<Provider<? extends IGeometry>>() {
						}, FOCUS_FEEDBACK_GEOMETRY_PROVIDER));
		if (focusFeedbackGeometryProvider != null) {
			Provider<IGeometry> geometryInSceneProvider = new Provider<IGeometry>() {
				@Override
				public IGeometry get() {
					return NodeUtils.localToScene(target.getVisual(),
							focusFeedbackGeometryProvider.get());
				}
			};
			FocusFeedbackPart part = injector
					.getInstance(FocusFeedbackPart.class);
			part.setGeometryProvider(geometryInSceneProvider);
			feedbackParts.add(part);
		}

		return feedbackParts;
	}

}

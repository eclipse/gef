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
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXFocusBehavior;
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
public class FXDefaultFocusFeedbackPartFactory
		implements IFeedbackPartFactory<Node> {

	/**
	 * The role name for the <code>Provider&lt;IGeometry&gt;</code> that will be
	 * used to generate focus feedback.
	 */
	public static final String FOCUS_FEEDBACK_GEOMETRY_PROVIDER = "FOCUS_FEEDBACK_GEOMETRY_PROVIDER";

	@Inject
	private Injector injector;

	@Override
	public List<IFeedbackPart<Node, ? extends Node>> createFeedbackParts(
			List<? extends IVisualPart<Node, ? extends Node>> targets,
			IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {
		// check creation context
		if (!(contextBehavior instanceof FXFocusBehavior)) {
			throw new IllegalArgumentException(
					"The FXDefaultFocusFeedbackPartFactory can only generate feedback parts in the context of an FXFocusBehavior, but the context behavior is a <"
							+ contextBehavior + ">.");
		}
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

		final IVisualPart<Node, ? extends Node> target = targets.iterator()
				.next();
		List<IFeedbackPart<Node, ? extends Node>> feedbackParts = new ArrayList<>();

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
			FXFocusFeedbackPart part = injector
					.getInstance(FXFocusFeedbackPart.class);
			part.setGeometryProvider(geometryInSceneProvider);
			feedbackParts.add(part);
		}

		return feedbackParts;
	}

}

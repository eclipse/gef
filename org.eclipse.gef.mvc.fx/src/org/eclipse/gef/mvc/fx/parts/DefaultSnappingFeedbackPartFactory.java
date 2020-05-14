/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
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

import org.eclipse.gef.mvc.fx.behaviors.SnappingBehavior;
import org.eclipse.gef.mvc.fx.models.SnappingModel;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.scene.Node;

/**
 * The {@link DefaultSnappingFeedbackPartFactory} is an {@link IFeedbackPartFactory}
 * that is used by the {@link SnappingBehavior} to generate
 * {@link SnappingFeedbackPart}s for the {@link SnappingLocation}s that
 * are stored in the {@link SnappingModel}.
 */
public class DefaultSnappingFeedbackPartFactory implements IFeedbackPartFactory {

	@Inject
	private Injector injector;

	@Override
	public List<IFeedbackPart<? extends Node>> createFeedbackParts(
			List<? extends IVisualPart<? extends Node>> targets,
			Map<Object, Object> contextMap) {
		if (targets == null || targets.isEmpty()) {
			throw new IllegalStateException(
					"Part factory is called without target parts.");
		}
		if (targets.size() > 1) {
			throw new IllegalStateException(
					"Expected a single target part, but got " + targets.size()
							+ ".");
		}

		// generate feedback depending on target
		IContentPart<? extends Node> target = (IContentPart<? extends Node>) targets
				.get(0);
		List<IFeedbackPart<? extends Node>> feedback = new ArrayList<>();

		// determine the corresponding snapping info
		SnappingModel snappingModel = target.getRoot().getViewer()
				.getAdapter(SnappingModel.class);
		List<SnappingLocation> snappingLocations = snappingModel
				.getSnappingLocationsFor(target);
		// for each snapping location
		for (SnappingLocation snappingLocation : snappingLocations) {
			// create a feedback part
			SnappingFeedbackPart fb = injector
					.getInstance(SnappingFeedbackPart.class);
			fb.setSnappingLocation(snappingLocation);
			feedback.add(fb);
		}
		return feedback;
	}
}

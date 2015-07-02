/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.parts;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.models.HidingModel;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class ZestFxHandlePartFactory extends FXDefaultHandlePartFactory {

	@Inject
	private Injector injector;

	@Override
	protected IHandlePart<Node, ? extends Node> createHoverSegmentHandlePart(
			final IVisualPart<Node, ? extends Node> target,
			Provider<BezierCurve[]> hoverHandlesSegmentsInSceneProvider,
			int segmentCount, int segmentIndex, Map<Object, Object> contextMap) {
		if (target instanceof NodeContentPart) {
			if (segmentIndex == 0) {
				// create prune handle at first vertex
				ZestFxHidingHandlePart part = injector
						.getInstance(ZestFxHidingHandlePart.class);
				part.setSegmentsProvider(hoverHandlesSegmentsInSceneProvider);
				part.setSegmentIndex(segmentIndex);
				part.setSegmentParameter(0);
				return part;
			} else if (segmentIndex == 1) {
				// create expand handle at second vertex
				// but check if we have pruned neighbors, first
				HidingModel hidingModel = target.getRoot().getViewer()
						.getAdapter(HidingModel.class);
				if (!hidingModel.getHiddenNeighbors(
						((NodeContentPart) target).getContent()).isEmpty()) {
					ZestFxExpandingHandlePart part = injector
							.getInstance(ZestFxExpandingHandlePart.class);
					part.setSegmentsProvider(hoverHandlesSegmentsInSceneProvider);
					part.setSegmentIndex(segmentIndex);
					part.setSegmentParameter(0);
					return part;
				}
			}
		}
		return null;
	}

	@Override
	protected List<IHandlePart<Node, ? extends Node>> createMultiSelectionHandleParts(
			List<? extends IVisualPart<Node, ? extends Node>> targets,
			Map<Object, Object> contextMap) {
		return Collections.emptyList();
	}

}

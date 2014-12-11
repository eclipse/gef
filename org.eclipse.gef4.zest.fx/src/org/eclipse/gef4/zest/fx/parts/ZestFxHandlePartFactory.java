/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.models.PruningModel;

import com.google.inject.Provider;

public class ZestFxHandlePartFactory extends FXDefaultHandlePartFactory {

	@Override
	protected IHandlePart<Node, ? extends Node> createHoverSegmentHandlePart(
			final IVisualPart<Node, ? extends Node> target,
			Provider<BezierCurve[]> hoverHandlesSegmentsInSceneProvider,
			int segmentCount, int segmentIndex, Map<Object, Object> contextMap) {
		if (target instanceof NodeContentPart) {
			if (segmentIndex == 0) {
				// create prune handle at first vertex
				return new ZestFxPruningHandlePart(
						hoverHandlesSegmentsInSceneProvider, segmentIndex, 0);
			} else if (segmentIndex == 1) {
				// create expand handle at second vertex
				// but check if we have pruned neighbors, first
				PruningModel pruningModel = target.getRoot().getViewer()
						.getDomain().getAdapter(PruningModel.class);
				if (!pruningModel.getPrunedNeighbors(
						((NodeContentPart) target).getContent()).isEmpty()) {
					return new ZestFxExpandingHandlePart(
							hoverHandlesSegmentsInSceneProvider, segmentIndex,
							0);
				}
			}
		}
		return null;
	}

	@Override
	protected List<IHandlePart<Node, ? extends Node>> createSelectionHandleParts(
			List<? extends IVisualPart<Node, ? extends Node>> targets,
			SelectionBehavior<Node> selectionBehavior,
			Map<Object, Object> contextMap) {
		return Collections.emptyList();
	}

}

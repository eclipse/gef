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
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class ZestFxHandlePartFactory extends FXDefaultHandlePartFactory {

	@Inject
	private Injector injector;

	@Override
	protected IHandlePart<Node> createHoverSegmentHandlePart(
			final IContentPart<Node> target,
			Provider<BezierCurve[]> hoverHandlesSegmentsInSceneProvider,
			int segmentCount, int segmentIndex, Map<Object, Object> contextMap) {
		if (target instanceof NodeContentPart && segmentIndex == 0) {
			// create prune/expand handle
			IHandlePart<Node> pruningHandlePart = new ZestFxPruningHandlePart(
					hoverHandlesSegmentsInSceneProvider, segmentIndex, 0);
			injector.injectMembers(pruningHandlePart);
			return pruningHandlePart;
		} else {
			// do not create handles for the other segments
			return null;
		}
	}

	@Override
	protected List<IHandlePart<Node>> createSelectionHandleParts(
			List<IContentPart<Node>> targets,
			SelectionBehavior<Node> selectionBehavior,
			Map<Object, Object> contextMap) {
		return Collections.emptyList();
	}

}

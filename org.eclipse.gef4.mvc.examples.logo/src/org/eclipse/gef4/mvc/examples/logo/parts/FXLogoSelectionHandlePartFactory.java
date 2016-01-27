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
package org.eclipse.gef4.mvc.examples.logo.parts;

import java.util.List;
import java.util.Map;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.mvc.behaviors.IBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultSelectionHandlePartFactory;
import org.eclipse.gef4.mvc.fx.policies.FXBendOnSegmentHandleDragPolicy;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Provider;

import javafx.scene.Node;

public class FXLogoSelectionHandlePartFactory
		extends FXDefaultSelectionHandlePartFactory {

	@Override
	protected List<IHandlePart<Node, ? extends Node>> createSingleSelectionHandlePartsForCurve(
			IVisualPart<Node, ? extends Node> target,
			IBehavior<Node> contextBehavior, Map<Object, Object> contextMap,
			Provider<BezierCurve[]> segmentsProvider) {
		List<IHandlePart<Node, ? extends Node>> parts = super.createSingleSelectionHandlePartsForCurve(
				target, contextBehavior, contextMap, segmentsProvider);
		for (IHandlePart<Node, ? extends Node> p : parts) {
			// make way points draggable and end points reconnectable
			// TODO: binding the following policy requires dynamic binding
			p.setAdapter(new FXBendOnSegmentHandleDragPolicy());
		}
		return parts;
	}

}

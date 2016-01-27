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

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultSelectionHandlePartFactory;
import org.eclipse.gef4.mvc.fx.policies.FXBendOnSegmentHandleDragPolicy;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Provider;

import javafx.scene.Node;

public class FXLogoSelectionHandlePartFactory
		extends FXDefaultSelectionHandlePartFactory {

	@Override
	public IHandlePart<Node, ? extends Node> createCurveSelectionHandlePart(
			final IVisualPart<Node, ? extends Node> targetPart,
			final Provider<BezierCurve[]> segmentsProvider, int segmentCount,
			int segmentIndex, double segmentParameter) {
		final FXCircleSegmentHandlePart part = (FXCircleSegmentHandlePart) super.createCurveSelectionHandlePart(
				targetPart, segmentsProvider, segmentCount, segmentIndex,
				segmentParameter);

		if (segmentIndex + segmentParameter > 0
				&& segmentIndex + segmentParameter < segmentCount) {
			// make way points (middle segment vertices) draggable
			// TODO: binding the following policy requires dynamic binding
			part.setAdapter(new FXBendOnSegmentHandleDragPolicy());
		} else {
			// make end points reconnectable
			// TODO: binding the following policy requires dynamic binding
			part.setAdapter(new FXBendOnSegmentHandleDragPolicy());
		}

		return part;
	}

}

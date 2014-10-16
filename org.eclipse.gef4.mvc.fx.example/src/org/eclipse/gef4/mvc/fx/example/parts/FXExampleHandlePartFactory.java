/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.example.parts;

import java.util.List;
import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.Node;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXBendOnSegmentHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateOnCornerHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateOnCornerHandleDragPolicy.ReferencePoint;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;

import com.google.inject.Provider;

public class FXExampleHandlePartFactory extends FXDefaultHandlePartFactory {

	@Override
	public IHandlePart<Node> createCornerHandlePart(
			final List<IContentPart<Node>> targets,
			Provider<IGeometry> handleGeometryProvider, Pos position,
			Map<Object, Object> contextMap) {
		IHandlePart<Node> part = super.createCornerHandlePart(targets,
				handleGeometryProvider, position, contextMap);
		// TODO: bind the policy in module
		part.setAdapter(AdapterKey.get(AbstractFXDragPolicy.class),
				new FXResizeRelocateOnCornerHandleDragPolicy(
						toReferencePoint(position)));
		return part;
	}

	@Override
	public IHandlePart<Node> createCurveSelectionHandlePart(
			final IContentPart<Node> targetPart,
			final Provider<BezierCurve[]> segmentsProvider, int segmentCount,
			int segmentIndex, double segmentParameter) {
		final FXSegmentHandlePart part = (FXSegmentHandlePart) super
				.createCurveSelectionHandlePart(targetPart, segmentsProvider,
						segmentCount, segmentIndex, segmentParameter);

		if (segmentIndex + segmentParameter > 0
				&& segmentIndex + segmentParameter < segmentCount) {
			// make way points (middle segment vertices) draggable
			// TODO: binding the following policy requires dynamic binding
			part.setAdapter(AdapterKey.get(AbstractFXDragPolicy.class),
					new FXBendOnSegmentHandleDragPolicy());
		} else {
			// make end points reconnectable
			// TODO: binding the following policy requires dynamic binding
			part.setAdapter(AdapterKey.get(AbstractFXDragPolicy.class),
					new FXBendOnSegmentHandleDragPolicy());
		}

		return part;
	}

	private ReferencePoint toReferencePoint(Pos position) {
		switch (position) {
		case TOP_LEFT:
			return ReferencePoint.TOP_LEFT;
		case TOP_RIGHT:
			return ReferencePoint.TOP_RIGHT;
		case BOTTOM_LEFT:
			return ReferencePoint.BOTTOM_LEFT;
		case BOTTOM_RIGHT:
			return ReferencePoint.BOTTOM_RIGHT;
		default:
			throw new IllegalStateException(
					"Unknown Pos: <"
							+ position
							+ ">. Expected any of: TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT.");
		}
	}

}

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
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXBendOnSegmentHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateOnCornerHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateOnCornerHandleDragPolicy.ReferencePoint;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class FXExampleHandlePartFactory extends FXDefaultHandlePartFactory {

	@Inject
	private Injector injector;

	private List<IHandlePart<Node>> parts;

	@Override
	public IHandlePart<Node> createCornerHandlePart(
			Provider<IGeometry> handleGeometryProvider, Pos position,
			Map<Object, Object> contextMap) {
		IHandlePart<Node> part = super.createCornerHandlePart(
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
			final Provider<IGeometry> handleGeometryProvider, int segmentIndex,
			final boolean isEndPoint) {
		final FXSegmentHandlePart part = (FXSegmentHandlePart) super
				.createCurveSelectionHandlePart(targetPart,
						handleGeometryProvider, segmentIndex, isEndPoint);

		if (segmentIndex > 0 && !isEndPoint) {
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

	@Override
	protected List<IHandlePart<Node>> createCurveSelectionHandleParts(
			final IContentPart<Node> targetPart,
			Provider<IGeometry> handleGeometryProvider,
			Map<Object, Object> contextMap) {
		parts = super.createCurveSelectionHandleParts(targetPart,
				handleGeometryProvider, contextMap);

		// create mid point (insertion) handles
		BezierCurve[] beziers = ((ICurve) handleGeometryProvider.get())
				.toBezier();
		for (int i = 0; i < beziers.length; i++) {
			int segmentIndex = i;
			final FXSegmentHandlePart hp = new FXSegmentHandlePart(
					handleGeometryProvider, segmentIndex, 0.5);
			injector.injectMembers(hp);
			// TODO: binding the following policy requires dynamic binding
			hp.setAdapter(AdapterKey.get(AbstractFXDragPolicy.class),
					new FXBendOnSegmentHandleDragPolicy());
			parts.add(hp);
		}

		return parts;
	}

	// @Override
	// public IHandlePart<Node> createSegmentHandlePart(
	// Provider<IGeometry> handleGeometryProvider, int vertexIndex,
	// Map<Object, Object> contextMap) {
	// IHandlePart<Node> part = super.createSegmentHandlePart(
	// handleGeometryProvider, vertexIndex, contextMap);
	// // TODO: binding the following policy requires dynamic binding
	// part.setAdapter(AdapterKey.get(AbstractFXDragPolicy.class),
	// new FXResizeRelocateOnCornerHandleDragPolicy(
	// toReferencePoint(vertexIndex)));
	// return part;
	// }

	// // TODO -> this has to be done somewhere else, or we need to use box
	// handle
	// // parts -> better compute this from position of vertex??
	// private ReferencePoint toReferencePoint(int vertexIndex) {
	// switch (vertexIndex) {
	// case 0:
	// return ReferencePoint.TOP_LEFT;
	// case 1:
	// return ReferencePoint.TOP_RIGHT;
	// case 2:
	// return ReferencePoint.BOTTOM_RIGHT;
	// case 3:
	// return ReferencePoint.BOTTOM_LEFT;
	// default:
	// throw new IllegalStateException("Unsupported vertex index ("
	// + vertexIndex + "), expected 0 to 3.");
	// }
	// }

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

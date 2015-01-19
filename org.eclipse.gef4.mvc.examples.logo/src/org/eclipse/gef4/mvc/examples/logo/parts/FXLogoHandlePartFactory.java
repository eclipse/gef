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
package org.eclipse.gef4.mvc.examples.logo.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.geometry.Pos;
import javafx.scene.Node;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.mvc.behaviors.HoverBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXBendOnSegmentHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateOnCornerHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateOnCornerHandleDragPolicy.ReferencePoint;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class FXLogoHandlePartFactory extends FXDefaultHandlePartFactory {

	@Inject
	private Injector injector;

	@Override
	public IHandlePart<Node, ? extends Node> createBoundsSelectionCornerHandlePart(
			final List<? extends IVisualPart<Node, ? extends Node>> targets,
			Provider<? extends IGeometry> handleGeometryProvider, Pos position,
			Map<Object, Object> contextMap) {
		IHandlePart<Node, ? extends Node> part = super
				.createBoundsSelectionCornerHandlePart(targets,
						handleGeometryProvider, position, contextMap);
		// TODO: bind the policy in module
		part.setAdapter(AdapterKey.get(AbstractFXDragPolicy.class),
				new FXResizeRelocateOnCornerHandleDragPolicy(
						toReferencePoint(position)));
		return part;
	}

	@Override
	public IHandlePart<Node, ? extends Node> createCurveSelectionHandlePart(
			final IVisualPart<Node, ? extends Node> targetPart,
			final Provider<BezierCurve[]> segmentsProvider, int segmentCount,
			int segmentIndex, double segmentParameter) {
		final FXCircleSegmentHandlePart part = (FXCircleSegmentHandlePart) super
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

	@Override
	protected List<IHandlePart<Node, ? extends Node>> createHoverHandleParts(
			IVisualPart<Node, ? extends Node> target,
			HoverBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {
		List<IHandlePart<Node, ? extends Node>> handles = new ArrayList<IHandlePart<Node, ? extends Node>>();
		if (target instanceof FXGeometricShapePart) {
			// create parent handle part
			FXHoverHandleRootPart parentHp = new FXHoverHandleRootPart();
			injector.injectMembers(parentHp);
			handles.add(parentHp);

			// XXX: addChild() should be called automatically?
			FXDeleteHandlePart deleteHp = new FXDeleteHandlePart();
			injector.injectMembers(deleteHp);
			parentHp.addChild(deleteHp);

			// XXX: addChild() should be called automatically?
			FXRotateHandlePart rotateHp = new FXRotateHandlePart();
			injector.injectMembers(rotateHp);
			parentHp.addChild(rotateHp);

			return handles;
		}
		return super
				.createHoverHandleParts(target, contextBehavior, contextMap);
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

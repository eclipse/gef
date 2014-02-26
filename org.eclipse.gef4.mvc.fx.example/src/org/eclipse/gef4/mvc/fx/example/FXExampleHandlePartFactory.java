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
package org.eclipse.gef4.mvc.fx.example;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.IProvider;
import org.eclipse.gef4.mvc.fx.example.policies.AbstractWayPointPolicy;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.policies.IDragPolicy;
import org.eclipse.gef4.mvc.policies.AbstractResizeRelocateSelectedOnHandleDragPolicy.ReferencePoint;

public class FXExampleHandlePartFactory extends FXDefaultHandlePartFactory {

	@Override
	public IHandlePart<Node> createMultiSelectionCornerHandlePart(
			List<IContentPart<Node>> targets, Pos position) {
		IHandlePart<Node> part = super.createMultiSelectionCornerHandlePart(
				targets, position);
		part.installBound(IDragPolicy.class,
				new FXResizeRelocateSelectedOnHandleDragPolicy(
						toReferencePoint(position)));
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

	@Override
	public IHandlePart<Node> createCurveSelectionHandlePart(
			final IContentPart<Node> targetPart,
			final IProvider<IGeometry> handleGeometryProvider,
			final int segmentIndex, final boolean isEndPoint) {
		final IHandlePart<Node> part = super.createCurveSelectionHandlePart(
				targetPart, handleGeometryProvider, segmentIndex, isEndPoint);

		// make way points (middle segment vertices) draggable
		if (segmentIndex > 0 && !isEndPoint) {
			part.installBound(IDragPolicy.class, new IDragPolicy.Impl<Node>() {
				private Point startPoint;

				@Override
				public void press(Point mouseLocation) {
					getWayPointHandlePolicy(targetPart).selectWayPoint(
							segmentIndex - 1);
					startPoint = new Point(part.getVisual().getLayoutX(), part
							.getVisual().getLayoutY());
				}

				@Override
				public void drag(Point mouseLocation, Dimension delta) {
					Point newPosition = startPoint.getTranslated(delta.width,
							delta.height);
					getWayPointHandlePolicy(targetPart).updateWayPoint(
							segmentIndex - 1, newPosition);
				}

				@Override
				public void release(Point mouseLocation, Dimension delta) {
					Point newPosition = startPoint.getTranslated(delta.width,
							delta.height);
					getWayPointHandlePolicy(targetPart).commitWayPoint(
							segmentIndex - 1, newPosition);
				}
			});
		}

		return part;
	}

	private AbstractWayPointPolicy getWayPointHandlePolicy(
			IContentPart<Node> targetPart) {
		return targetPart.getBound(AbstractWayPointPolicy.class);
	}

	@Override
	public IHandlePart<Node> createShapeSelectionHandlePart(
			IContentPart<Node> targetPart,
			IProvider<IGeometry> handleGeometryProvider, int vertexIndex) {
		IHandlePart<Node> part = super.createShapeSelectionHandlePart(
				targetPart, handleGeometryProvider, vertexIndex);
		part.installBound(IDragPolicy.class,
				new FXResizeRelocateSelectedOnHandleDragPolicy(
						toReferencePoint(vertexIndex)));
		return part;
	}

	private ReferencePoint toReferencePoint(int vertexIndex) {
		switch (vertexIndex) {
		case 0:
			return ReferencePoint.TOP_LEFT;
		case 1:
			return ReferencePoint.TOP_RIGHT;
		case 2:
			return ReferencePoint.BOTTOM_RIGHT;
		case 3:
			return ReferencePoint.BOTTOM_LEFT;
		default:
			throw new IllegalStateException("Unsupported vertex index ("
					+ vertexIndex + "), expected 0 to 3.");
		}
	}

}

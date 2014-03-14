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
package org.eclipse.gef4.mvc.fx.ui.example;

import java.util.List;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.IProvider;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateSelectedOnHandleDragPolicy.ReferencePoint;
import org.eclipse.gef4.mvc.fx.ui.example.parts.FXMidPointHandlePart;
import org.eclipse.gef4.mvc.fx.ui.example.policies.AbstractReconnectionPolicy;
import org.eclipse.gef4.mvc.fx.ui.example.policies.AbstractWayPointPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;

public class FXExampleHandlePartFactory extends FXDefaultHandlePartFactory {

	@Override
	public IHandlePart<Node> createMultiSelectionCornerHandlePart(
			List<IContentPart<Node>> targets, Pos position) {
		IHandlePart<Node> part = super.createMultiSelectionCornerHandlePart(
				targets, position);
		part.installBound(AbstractFXDragPolicy.class,
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
	protected List<IHandlePart<Node>> createCurveSelectionHandleParts(
			final IContentPart<Node> targetPart,
			IProvider<IGeometry> handleGeometryProvider, IGeometry geom) {
		// create vertex handles
		List<IHandlePart<Node>> parts = super.createCurveSelectionHandleParts(
				targetPart, handleGeometryProvider, geom);

		// create mid point (insertion) handles
		BezierCurve[] beziers = ((ICurve) geom).toBezier();
		for (int i = 0; i < beziers.length; i++) {
			final int segmentIndex = i;
			final IHandlePart<Node> hp = new FXMidPointHandlePart(targetPart,
					handleGeometryProvider, segmentIndex);
			hp.installBound(AbstractFXDragPolicy.class,
					new AbstractFXDragPolicy() {
						@Override
						public void press(MouseEvent e) {
							getWayPointHandlePolicy(targetPart).createWayPoint(
									segmentIndex,
									new Point(e.getSceneX(), e.getSceneY()));
						}

						@Override
						public void drag(MouseEvent e, Dimension delta,
								List<Node> nodesUnderMouse,
								List<IContentPart<Node>> partsUnderMouse) {
							getWayPointHandlePolicy(targetPart).updateWayPoint(
									segmentIndex,
									new Point(e.getSceneX(), e.getSceneY()));
						}

						@Override
						public void release(MouseEvent e, Dimension delta,
								List<Node> nodesUnderMouse,
								List<IContentPart<Node>> partsUnderMouse) {
							getWayPointHandlePolicy(targetPart).commitWayPoint(
									segmentIndex,
									new Point(e.getSceneX(), e.getSceneY()));
						}
					});
			parts.add(hp);
		}

		return parts;
	}

	@Override
	public IHandlePart<Node> createCurveSelectionHandlePart(
			final IContentPart<Node> targetPart,
			final IProvider<IGeometry> handleGeometryProvider,
			final int segmentIndex, final boolean isEndPoint) {
		final IHandlePart<Node> part = super.createCurveSelectionHandlePart(
				targetPart, handleGeometryProvider, segmentIndex, isEndPoint);

		if (segmentIndex > 0 && !isEndPoint) {
			// make way points (middle segment vertices) draggable
			part.installBound(AbstractFXDragPolicy.class,
					new AbstractFXDragPolicy() {
						@Override
						public void press(MouseEvent e) {
							getWayPointHandlePolicy(targetPart).selectWayPoint(
									segmentIndex - 1,
									new Point(e.getSceneX(), e.getSceneY()));
						}

						@Override
						public void drag(MouseEvent e, Dimension delta,
								List<Node> nodesUnderMouse,
								List<IContentPart<Node>> partsUnderMouse) {
							getWayPointHandlePolicy(targetPart).updateWayPoint(
									segmentIndex - 1,
									new Point(e.getSceneX(), e.getSceneY()));
						}

						@Override
						public void release(MouseEvent e, Dimension delta,
								List<Node> nodesUnderMouse,
								List<IContentPart<Node>> partsUnderMouse) {
							getWayPointHandlePolicy(targetPart).commitWayPoint(
									segmentIndex - 1,
									new Point(e.getSceneX(), e.getSceneY()));
						}
					});
		} else {
			// make end points reconnectable
			part.installBound(AbstractFXDragPolicy.class,
					new AbstractFXDragPolicy() {
						@Override
						public void press(MouseEvent e) {
							AbstractReconnectionPolicy p = getReconnectionPolicy(targetPart);
							if (p != null) {
								p.loosen(isEndPoint ? 1 : 0);
							}
						}

						@Override
						public void drag(MouseEvent e, Dimension delta,
								List<Node> nodesUnderMouse,
								List<IContentPart<Node>> partsUnderMouse) {
							getReconnectionPolicy(targetPart).dragTo(delta,
									partsUnderMouse);
						}

						@Override
						public void release(MouseEvent e, Dimension delta,
								List<Node> nodesUnderMouse,
								List<IContentPart<Node>> partsUnderMouse) {
							getReconnectionPolicy(targetPart).releaseAt(delta,
									partsUnderMouse);
						}

						private AbstractReconnectionPolicy getReconnectionPolicy(
								IContentPart<Node> targetPart) {
							return targetPart
									.getBound(AbstractReconnectionPolicy.class);
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
		part.installBound(AbstractFXDragPolicy.class,
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

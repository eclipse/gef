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
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.IProvider;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultHandlePartFactory;
import org.eclipse.gef4.mvc.fx.parts.FXSelectionHandlePart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXReconnectPolicy;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXWayPointPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateSelectedOnHandleDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocateSelectedOnHandleDragPolicy.ReferencePoint;
import org.eclipse.gef4.mvc.fx.ui.example.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.fx.ui.example.parts.FXMidPointHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;

public class FXExampleHandlePartFactory extends FXDefaultHandlePartFactory {

	public final static Color FILL_RED = Color.web("#ff0000");

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
		final List<IHandlePart<Node>> parts = super
				.createCurveSelectionHandleParts(targetPart,
						handleGeometryProvider, geom);

		// create mid point (insertion) handles
		BezierCurve[] beziers = ((ICurve) geom).toBezier();
		for (int i = 0; i < beziers.length; i++) {
			int segmentIndex = i;
			final FXMidPointHandlePart hp = new FXMidPointHandlePart(
					targetPart, handleGeometryProvider, segmentIndex);
			hp.installBound(AbstractFXDragPolicy.class,
					new AbstractFXDragPolicy() {
						@Override
						public void press(MouseEvent e) {
							// TODO: merge mid point and vertex handle parts
							if (hp.isVertex()) {
								getWayPointHandlePolicy(targetPart)
										.selectWayPoint(
												hp.getVertexIndex() - 1,
												new Point(e.getSceneX(), e
														.getSceneY()));
							} else {
								getWayPointHandlePolicy(targetPart)
										.createWayPoint(
												hp.getVertexIndex(),
												new Point(e.getSceneX(), e
														.getSceneY()));
								for (IHandlePart<Node> vertexHp : parts) {
									FXSelectionHandlePart part = (FXSelectionHandlePart) vertexHp;
									if (part.getVertexIndex() > hp
											.getVertexIndex()
											|| (part.getVertexIndex() == hp
													.getVertexIndex() && part
													.isEndPoint())) {
										part.incVertexIndex();
									}
								}
								// become vertex handle part
								hp.toVertex();
								((Shape) hp.getVisual())
										.setFill(FXSelectionHandlePart.FILL_BLUE);
							}
						}

						@Override
						public void drag(MouseEvent e, Dimension delta,
								List<Node> nodesUnderMouse,
								List<IContentPart<Node>> partsUnderMouse) {
							getWayPointHandlePolicy(targetPart).updateWayPoint(
									hp.getVertexIndex() - 1,
									new Point(e.getSceneX(), e.getSceneY()));
						}

						@Override
						public void release(MouseEvent e, Dimension delta,
								List<Node> nodesUnderMouse,
								List<IContentPart<Node>> partsUnderMouse) {
							getWayPointHandlePolicy(targetPart).commitWayPoint(
									hp.getVertexIndex() - 1,
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
			int segmentIndex, final boolean isEndPoint) {
		final FXSelectionHandlePart part = (FXSelectionHandlePart) super
				.createCurveSelectionHandlePart(targetPart,
						handleGeometryProvider, segmentIndex, isEndPoint);

		if (segmentIndex > 0 && !isEndPoint) {
			// make way points (middle segment vertices) draggable
			part.installBound(AbstractFXDragPolicy.class,
					new AbstractFXDragPolicy() {
						@Override
						public void press(MouseEvent e) {
							getWayPointHandlePolicy(targetPart).selectWayPoint(
									part.getVertexIndex() - 1,
									new Point(e.getSceneX(), e.getSceneY()));
						}

						@Override
						public void drag(MouseEvent e, Dimension delta,
								List<Node> nodesUnderMouse,
								List<IContentPart<Node>> partsUnderMouse) {
							getWayPointHandlePolicy(targetPart).updateWayPoint(
									part.getVertexIndex() - 1,
									new Point(e.getSceneX(), e.getSceneY()));
						}

						@Override
						public void release(MouseEvent e, Dimension delta,
								List<Node> nodesUnderMouse,
								List<IContentPart<Node>> partsUnderMouse) {
							IUndoableOperation operation = getWayPointHandlePolicy(targetPart).commitWayPoint(
									part.getVertexIndex() - 1,
									new Point(e.getSceneX(), e.getSceneY()));
							// FIXME: change way point operation bug: NPE
//							executeOperation(operation);
						}
					});
		} else {
			// make end points reconnectable
			part.installBound(AbstractFXDragPolicy.class,
					new AbstractFXDragPolicy() {
						@Override
						public void press(MouseEvent e) {
							AbstractFXReconnectPolicy p = getReconnectionPolicy(targetPart);
							if (p != null) {
								p.press(!isEndPoint,
										new Point(e.getSceneX(), e.getSceneY()));
							}
						}

						@Override
						public void drag(MouseEvent e, Dimension delta,
								List<Node> nodesUnderMouse,
								List<IContentPart<Node>> partsUnderMouse) {
							AbstractFXReconnectPolicy policy = getReconnectionPolicy(targetPart);
							policy.dragTo(
									new Point(e.getSceneX(), e.getSceneY()),
									partsUnderMouse);
							// TODO: move color change to some other place?
							if (policy.isConnected()) {
								((Shape) part.getVisual()).setFill(FILL_RED);
							} else {
								((Shape) part.getVisual()).setFill(FXSelectionHandlePart.FILL_BLUE);
							}
						}

						@Override
						public void release(MouseEvent e, Dimension delta,
								List<Node> nodesUnderMouse,
								List<IContentPart<Node>> partsUnderMouse) {
							IUndoableOperation operation = getReconnectionPolicy(
									targetPart).commit();
							executeOperation(operation);
						}

						private AbstractFXReconnectPolicy getReconnectionPolicy(
								IContentPart<Node> targetPart) {
							return targetPart
									.getBound(AbstractFXReconnectPolicy.class);
						}
					});

			// change color to red if they are connected
			// TODO: move to somewhere else
			if (targetPart instanceof FXGeometricCurvePart) {
				FXGeometricCurvePart cp = (FXGeometricCurvePart) targetPart;
				IFXConnection connection = (IFXConnection) cp.getVisual();
				IFXAnchor anchor = isEndPoint ? connection.getEndAnchor()
						: connection.getStartAnchor();
				if (!(anchor instanceof FXStaticAnchor)) {
					((Shape) part.getVisual()).setFill(FILL_RED);
				}
			}
		}

		return part;
	}

	private AbstractFXWayPointPolicy getWayPointHandlePolicy(
			IContentPart<Node> targetPart) {
		return targetPart.getBound(AbstractFXWayPointPolicy.class);
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

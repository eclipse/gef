/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.ui.example.policies;

import java.util.HashMap;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Shape;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.FXSelectionHandlePart;
import org.eclipse.gef4.mvc.fx.ui.example.FXExampleHandlePartFactory;
import org.eclipse.gef4.mvc.fx.ui.example.operations.FXExampleReconnectOperation;
import org.eclipse.gef4.mvc.fx.ui.example.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.fx.ui.example.parts.FXGeometricShapePart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;

public class FXExampleReconnectionPolicy extends AbstractPolicy<Node> {

	private final FXGeometricCurvePart curvePart;

	public FXExampleReconnectionPolicy(FXGeometricCurvePart fxGeometricCurvePart) {
		curvePart = fxGeometricCurvePart;
	}

	private boolean isStartAnchor;
	private FXSelectionHandlePart part;
	private Point2D startPointScene;
	private Point startPointLocal;
	private boolean connected;

	private FXGeometricShapePart oldShapePart;
	private FXGeometricShapePart newShapePart;

	public void loosen(int anchorIndex, Point startPointInScene,
			FXSelectionHandlePart part) {
		curvePart.setRefreshFromModel(false);
		this.part = part;
		this.startPointScene = new Point2D(startPointInScene.x,
				startPointInScene.y);

		// determine anchor index and offset
		isStartAnchor = anchorIndex == 0;
		// replaceAnchorIndex = anchorIndex;
		Point2D pLocal = curvePart.getVisual().sceneToLocal(startPointScene);
		startPointLocal = new Point(pLocal.getX(), pLocal.getY());

		removeCurrentAnchor();
	}

	public void dragTo(Point pointInScene,
			List<IContentPart<Node>> partsUnderMouse) {
		FXGeometricShapePart anchorPart = getAnchorPart(partsUnderMouse);
		if (connected) {
			if (anchorPart != null) {
				// nothing to do/position still fixed by anchor
				return;
			} else {
				removeCurrentAnchor();
			}
		} else {
			if (anchorPart != null) {
				addAnchorPart(anchorPart);
			} else {
				// update reference position (static anchor)
				Point position = transformToLocal(pointInScene);
				IFXConnection visual = (IFXConnection) curvePart.getVisual();
				IFXAnchor anchor = isStartAnchor ? visual.getStartAnchor()
						: visual.getEndAnchor();
				anchor.setReferencePoint(curvePart.getVisual(), position);
				anchor.recomputePositions();
			}
		}
	}

	public IUndoableOperation commit() {
		curvePart.setRefreshFromModel(true);
		FXExampleReconnectOperation operation = new FXExampleReconnectOperation(
				"Reconnect", curvePart, isStartAnchor, oldShapePart,
				newShapePart);
		curvePart.refreshVisual();
		return operation;
	}

	private void addAnchorPart(FXGeometricShapePart cp) {
		newShapePart = cp;
		cp.addAnchored(curvePart, new HashMap<Object, Object>() {
			{
				put("vertex", isStartAnchor ? 0 : 1);
			}
		});
		((Shape) part.getVisual()).setFill(FXExampleHandlePartFactory.FILL_RED);
		IFXConnection visual = (IFXConnection) curvePart.getVisual();
		if (isStartAnchor) {
			visual.getStartAnchor().recomputePositions();
		} else {
			visual.getEndAnchor().recomputePositions();
		}
		connected = true;
	}

	private Point transformToLocal(Point p) {
		Point2D pLocal = curvePart.getVisual().sceneToLocal(p.x, p.y);
		Point2D initialPosLocal = curvePart.getVisual().sceneToLocal(
				startPointScene);

		Point delta = new Point(pLocal.getX() - initialPosLocal.getX(),
				pLocal.getY() - initialPosLocal.getY());

		return new Point(startPointLocal.x + delta.x, startPointLocal.y
				+ delta.y);
	}

	private void removeCurrentAnchor() {
		IFXConnection visual = (IFXConnection) curvePart.getVisual();
		IFXAnchor currentAnchor = isStartAnchor ? visual.getStartAnchor()
				: visual.getEndAnchor();
		Node anchorageNode = currentAnchor.getAnchorageNode();
		if (anchorageNode != null) {
			IVisualPart<Node> shapePart = getHost().getRoot().getViewer()
					.getVisualPartMap().get(anchorageNode);
			if (oldShapePart == null) {
				oldShapePart = (FXGeometricShapePart) shapePart;
			}
			shapePart.removeAnchored(curvePart);
			((Shape) part.getVisual()).setFill(FXSelectionHandlePart.FILL_BLUE);
		}
		connected = false;
	}

	private FXGeometricShapePart getAnchorPart(
			List<IContentPart<Node>> partsUnderMouse) {
		for (IContentPart<Node> cp : partsUnderMouse) {
			if (cp instanceof FXGeometricShapePart) {
				return (FXGeometricShapePart) cp;
			}
		}
		return null;
	}

}
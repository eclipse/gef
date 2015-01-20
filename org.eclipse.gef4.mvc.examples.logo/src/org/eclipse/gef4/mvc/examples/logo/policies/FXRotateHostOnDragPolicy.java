/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.policies;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXRotateNodeOperation;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.SetMultimap;

public class FXRotateHostOnDragPolicy extends AbstractFXDragPolicy {

	private Point initialHostMidInScene;
	private Point initialPointerLocationInScene;
	private FXRotateNodeOperation rotateNodeOperation;
	private boolean invalidGesture = false;
	private double initialRotate;

	protected Angle computeRotationAngleCW(MouseEvent e) {
		Vector vStart = new Vector(initialHostMidInScene,
				initialPointerLocationInScene);
		Vector vEnd = new Vector(initialHostMidInScene, new Point(
				e.getSceneX(), e.getSceneY()));
		Angle angle = vStart.getAngleCW(vEnd);
		return angle;
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		// do nothing when the user does not press control
		if (invalidGesture) {
			return;
		}

		// locally execute operation
		updateOperation(e);
		try {
			rotateNodeOperation.execute(null, null);
		} catch (ExecutionException x) {
			throw new IllegalStateException(x);
		}
	}

	@Override
	public IVisualPart<Node, ? extends Node> getHost() {
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = super
				.getHost().getAnchorages();
		if (anchorages.isEmpty()) {
			return null;
		}
		// first anchorage = FXGeometricShapePart
		return anchorages.keySet().iterator().next();
	}

	@Override
	public void press(MouseEvent e) {
		// do nothing when the user does not press control
		if (!e.isControlDown()) {
			invalidGesture = true;
			return;
		}

		initialPointerLocationInScene = new Point(e.getSceneX(), e.getSceneY());
		Node hostVisual = getHost().getVisual();
		Bounds boundsInScene = hostVisual.localToScene(hostVisual
				.getBoundsInLocal());
		initialHostMidInScene = new Point(boundsInScene.getMinX()
				+ boundsInScene.getWidth() / 2, boundsInScene.getMinY()
				+ boundsInScene.getHeight() / 2);
		rotateNodeOperation = new FXRotateNodeOperation(getHost().getVisual());
		initialRotate = rotateNodeOperation.getOldDeg();

		// pivot
		Point2D pivot = hostVisual.sceneToLocal(initialHostMidInScene.x,
				initialHostMidInScene.y);
		rotateNodeOperation.setNewPivotX(pivot.getX());
		rotateNodeOperation.setNewPivotY(pivot.getY());
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		// do nothing when the user does not press control
		if (invalidGesture) {
			invalidGesture = false;
			return;
		}

		updateOperation(e);
		getHost().getRoot().getViewer().getDomain()
				.execute(rotateNodeOperation);
	}

	private void updateOperation(MouseEvent e) {
		rotateNodeOperation.setNewDeg(initialRotate
				+ computeRotationAngleCW(e).deg());
	}

}

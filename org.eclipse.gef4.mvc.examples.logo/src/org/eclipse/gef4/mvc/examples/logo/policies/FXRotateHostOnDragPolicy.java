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
import javafx.scene.transform.Affine;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXTransformOperation;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.SetMultimap;

public class FXRotateHostOnDragPolicy extends AbstractFXDragPolicy {

	private Point pivotInScene;
	private Point initialPointerLocationInScene;
	private FXTransformOperation transformOperation;
	private boolean invalidGesture = false;
	private Point2D pivotInHost;

	protected Angle computeRotationAngleCW(MouseEvent e) {
		Vector vStart = new Vector(pivotInScene, initialPointerLocationInScene);
		Vector vEnd = new Vector(pivotInScene, new Point(e.getSceneX(),
				e.getSceneY()));
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
			transformOperation.execute(null, null);
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
				.getLayoutBounds());
		pivotInScene = new Point(boundsInScene.getMinX()
				+ boundsInScene.getWidth() / 2, boundsInScene.getMinY()
				+ boundsInScene.getHeight() / 2);
		transformOperation = new FXTransformOperation(getHost());

		pivotInHost = hostVisual.sceneToLocal(pivotInScene.x, pivotInScene.y);
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		// do nothing when the user does not press control
		if (invalidGesture) {
			invalidGesture = false;
			return;
		}

		updateOperation(e);
		getHost().getRoot().getViewer().getDomain().execute(transformOperation);
	}

	private void updateOperation(MouseEvent e) {
		Affine oldTransform = transformOperation.getOldTransform();
		AffineTransform rotate = new AffineTransform().rotate(
				computeRotationAngleCW(e).rad(), pivotInHost.getX(),
				pivotInHost.getY());
		AffineTransform newTransform = JavaFX2Geometry.toAffineTransform(
				oldTransform).concatenate(rotate);
		transformOperation.setNewTransform(Geometry2JavaFX
				.toFXAffine(newTransform));
	}

}

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
package org.eclipse.gef4.mvc.fx.policies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.collect.SetMultimap;

public class FXRotateSelectedOnHandleDragPolicy extends AbstractFXDragPolicy {

	private boolean invalidGesture = false;
	private Point initialPointerLocationInScene;
	private Point pivotInScene;
	private final Map<IVisualPart<Node, ? extends Node>, Point2D> pivotInHost = new HashMap<IVisualPart<Node, ? extends Node>, Point2D>();

	protected Point computePivotInScene() {
		Rectangle bounds = null;
		for (IVisualPart<Node, ? extends Node> part : getTargetParts()) {
			Rectangle boundsInScene = JavaFX2Geometry.toRectangle(part
					.getVisual().localToScene(
							part.getVisual().getLayoutBounds()));
			if (bounds == null) {
				bounds = boundsInScene;
			} else {
				bounds.union(boundsInScene);
			}
		}
		return bounds == null ? null : bounds.getCenter();
	}

	protected Angle computeRotationAngleCW(MouseEvent e,
			IVisualPart<Node, ? extends Node> part) {
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
		for (IVisualPart<Node, ? extends Node> part : getTargetParts()) {
			updateOperation(e, part);
		}
	}

	@Override
	public IVisualPart<Node, ? extends Node> getHost() {
		SetMultimap<IVisualPart<Node, ? extends Node>, String> anchorages = super
				.getHost().getAnchorages();
		if (anchorages.isEmpty()) {
			return null;
		}
		return anchorages.keySet().iterator().next();
	}

	protected List<IContentPart<Node, ? extends Node>> getTargetParts() {
		return getHost().getRoot().getViewer()
				.<SelectionModel<Node>> getAdapter(SelectionModel.class)
				.getSelected();
	}

	protected FXTransformPolicy getTransformPolicy(
			IVisualPart<Node, ? extends Node> part) {
		return part.getAdapter(FXTransformPolicy.class);
	}

	@Override
	public void press(MouseEvent e) {
		// do nothing when the user does not press control
		if (!e.isControlDown()) {
			invalidGesture = true;
			return;
		}

		// save pointer location for later angle calculation
		initialPointerLocationInScene = new Point(e.getSceneX(), e.getSceneY());

		// determine pivot point
		pivotInScene = computePivotInScene();

		// initialize for all target parts
		for (IVisualPart<Node, ? extends Node> part : getTargetParts()) {
			// transform pivot point to local coordinates
			Node visual = part.getVisual();
			Point2D pivotLocal = visual.sceneToLocal(pivotInScene.x,
					pivotInScene.y);
			// take scaling into account
			FXTransformPolicy transformPolicy = getTransformPolicy(part);
			AffineTransform oldTransform = JavaFX2Geometry
					.toAffineTransform(transformPolicy.getNodeTransform());
			double scaleX = oldTransform.getScaleX();
			double scaleY = oldTransform.getScaleY();
			pivotLocal = new Point2D(pivotLocal.getX() * scaleX,
					pivotLocal.getY() * scaleY);
			pivotInHost.put(part, pivotLocal);
			// initialize transaction policy
			transformPolicy.init();
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		// do nothing when the user does not press control
		if (invalidGesture) {
			invalidGesture = false;
			return;
		}
		for (IVisualPart<Node, ? extends Node> part : getTargetParts()) {
			updateOperation(e, part);
			getHost().getRoot().getViewer().getDomain()
					.execute(getTransformPolicy(part).commit());
		}
		pivotInHost.clear();
	}

	private void updateOperation(MouseEvent e,
			IVisualPart<Node, ? extends Node> part) {
		// determine scaling
		FXTransformPolicy transformPolicy = getTransformPolicy(part);
		Affine nodeTransform = transformPolicy.getNodeTransform();
		AffineTransform oldTransform = JavaFX2Geometry
				.toAffineTransform(nodeTransform);
		double scaleX = oldTransform.getScaleX();
		double scaleY = oldTransform.getScaleY();
		// compute rotation; ensure rotation is done before scaling
		Point2D pivot = pivotInHost.get(part);
		AffineTransform rotate = new AffineTransform()
				.scale(1 / scaleX, 1 / scaleY)
				.rotate(computeRotationAngleCW(e, part).rad(), pivot.getX(),
						pivot.getY()).scale(scaleX, scaleY);
		// apply rotation to the current transformations
		transformPolicy.setConcatenation(rotate);
	}

}

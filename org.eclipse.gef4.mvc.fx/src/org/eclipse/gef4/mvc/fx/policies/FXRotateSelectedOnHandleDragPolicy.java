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

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link FXRotateSelectedOnHandleDragPolicy} is an
 * {@link AbstractFXOnDragPolicy} that rotates the whole {@link SelectionModel
 * selection} when a selection handle is dragged.
 *
 * @author mwienand
 *
 */
public class FXRotateSelectedOnHandleDragPolicy extends AbstractFXOnDragPolicy {

	private boolean invalidGesture = false;
	private Point initialPointerLocationInScene;
	private Point pivotInScene;
	private Map<IContentPart<Node, ? extends Node>, Integer> rotationIndices = new HashMap<IContentPart<Node, ? extends Node>, Integer>();

	/**
	 * Computes the clock-wise rotation angle based on the initial mouse
	 * position and the actual mouse position.
	 *
	 * @param e
	 *            The latest {@link MouseEvent}.
	 * @param part
	 *            The {@link IVisualPart} that is rotated.
	 * @return The clock-wise rotation angle.
	 */
	protected Angle computeRotationAngleCW(MouseEvent e,
			IVisualPart<Node, ? extends Node> part) {
		Vector vStart = new Vector(pivotInScene, initialPointerLocationInScene);
		Vector vEnd = new Vector(pivotInScene,
				new Point(e.getSceneX(), e.getSceneY()));
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

	/**
	 * Returns a {@link List} containing the whole {@link SelectionModel
	 * selection}.
	 *
	 * @return A {@link List} containing the whole {@link SelectionModel
	 *         selection}.
	 */
	protected List<IContentPart<Node, ? extends Node>> getTargetParts() {
		return getHost().getRoot().getViewer()
				.<SelectionModel<Node>> getAdapter(SelectionModel.class)
				.getSelection();
	}

	/**
	 * Returns the {@link FXTransformPolicy} that is installed on the given
	 * {@link IVisualPart}.
	 *
	 * @param part
	 *            The {@link IVisualPart} of which the {@link FXTransformPolicy}
	 *            is returned.
	 * @return The {@link FXTransformPolicy} that is installed on the given
	 *         {@link IVisualPart}.
	 */
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
		Rectangle bounds = FXPartUtils
				.getUnionedVisualBoundsInScene(getTargetParts());
		if (bounds == null) {
			throw new IllegalStateException(
					"Cannot determine visual bounds (null).");
		}
		pivotInScene = bounds.getCenter();

		// initialize for all target parts
		rotationIndices.clear();
		for (IContentPart<Node, ? extends Node> part : getTargetParts()) {
			// transform pivot point to local coordinates
			FXTransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				storeAndDisableRefreshVisuals(part);
				init(transformPolicy);
				// transform pivot to parent coordinates
				Point pivotInLocal = JavaFX2Geometry
						.toPoint(getHost().getVisual().getParent()
								.sceneToLocal(pivotInScene.x, pivotInScene.y));
				// create transformations
				int translateIndex = transformPolicy.createPostTransform();
				int rotateIndex = transformPolicy.createPostTransform();
				int translateBackIndex = transformPolicy.createPostTransform();
				// set translation transforms
				transformPolicy.setPostTranslate(translateIndex,
						-pivotInLocal.x, -pivotInLocal.y);
				transformPolicy.setPostTranslate(translateBackIndex,
						pivotInLocal.x, pivotInLocal.y);
				// save rotation index for later adjustment
				rotationIndices.put(part, rotateIndex);
			}
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
			FXTransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				restoreRefreshVisuals(part);
				commit(transformPolicy);
			}
		}
	}

	private void updateOperation(MouseEvent e,
			IVisualPart<Node, ? extends Node> part) {
		// determine scaling
		FXTransformPolicy transformPolicy = getTransformPolicy(part);
		if (transformPolicy != null) {
			transformPolicy.setPostRotate(rotationIndices.get(part),
					computeRotationAngleCW(e, part));
		}
	}

}

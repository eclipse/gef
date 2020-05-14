/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.policies.TransformPolicy;

import javafx.scene.Node;
import javafx.scene.input.RotateEvent;

/**
 * The {@link RotateSelectedOnRotateHandler} is an {@link IOnRotateHandler} that
 * rotates the whole {@link SelectionModel selection} when its {@link #getHost()
 * host} experiences a touch rotate gesture.
 *
 * @author anyssen
 *
 */
public class RotateSelectedOnRotateHandler extends AbstractHandler
		implements IOnRotateHandler {

	private Point pivotInScene;
	private Map<IContentPart<? extends Node>, Integer> rotationIndices = new HashMap<>();
	private List<IContentPart<? extends Node>> targetParts;

	// gesture validity
	private boolean invalidGesture = false;

	@Override
	public void abortRotate() {
		if (invalidGesture) {
			return;
		}

		// roll back transform operations
		for (IVisualPart<? extends Node> part : getTargetParts()) {
			TransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				restoreRefreshVisuals(part);
				rollback(transformPolicy);
			}
		}
	}

	/**
	 * Returns a {@link List} containing all {@link IContentPart}s that should
	 * be rotated by this policy. Per default, the whole {@link SelectionModel
	 * selection} is returned.
	 *
	 * @return A {@link List} containing all {@link IContentPart}s that should
	 *         be rotated by this policy.
	 */
	protected List<IContentPart<? extends Node>> determineTargetParts() {
		return getHost().getRoot().getViewer().getAdapter(SelectionModel.class)
				.getSelectionUnmodifiable();
	}

	@Override
	public void endRotate(RotateEvent e) {
		if (invalidGesture) {
			return;
		}

		// commit transform operations
		for (IVisualPart<? extends Node> part : getTargetParts()) {
			updateOperation(e, part);
			TransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				restoreRefreshVisuals(part);
				commit(transformPolicy);
			}
		}
	}

	/**
	 * Returns the target parts of this policy.
	 *
	 * @return The target parts of this policy.
	 */
	protected List<IContentPart<? extends Node>> getTargetParts() {
		return targetParts;
	}

	/**
	 * Returns the {@link TransformPolicy} that is installed on the given
	 * {@link IVisualPart}.
	 *
	 * @param part
	 *            The {@link IVisualPart} of which the {@link TransformPolicy}
	 *            is returned.
	 * @return The {@link TransformPolicy} that is installed on the given
	 *         {@link IVisualPart}.
	 */
	protected TransformPolicy getTransformPolicy(
			IVisualPart<? extends Node> part) {
		return part.getAdapter(TransformPolicy.class);
	}

	/**
	 * Returns <code>true</code> if the given {@link RotateEvent} should trigger
	 * rotation. Otherwise returns <code>false</code>. Per default always
	 * returns <code>true</code>.
	 *
	 * @param event
	 *            The {@link RotateEvent} in question.
	 * @return <code>true</code> to indicate that the given {@link RotateEvent}
	 *         should trigger rotation, otherwise <code>false</code>.
	 */
	protected boolean isRotate(RotateEvent event) {
		return true;
	}

	@Override
	public void rotate(RotateEvent e) {
		if (invalidGesture) {
			return;
		}

		for (IVisualPart<? extends Node> part : getTargetParts()) {
			updateOperation(e, part);
		}
	}

	@Override
	public void startRotate(RotateEvent e) {
		targetParts = determineTargetParts();

		invalidGesture = !isRotate(e);
		if (invalidGesture) {
			return;
		}

		Rectangle bounds = PartUtils.getUnionedVisualBoundsInScene(targetParts);
		if (bounds == null) {
			throw new IllegalStateException(
					"Cannot determine visual bounds (null).");
		}
		pivotInScene = bounds.getCenter();

		// initialize for all target parts
		rotationIndices.clear();
		for (IContentPart<? extends Node> part : getTargetParts()) {
			// transform pivot point to local coordinates
			TransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				storeAndDisableRefreshVisuals(part);
				init(transformPolicy);
				// transform pivot to parent coordinates
				Point pivotInLocal = FX2Geometry
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

	private void updateOperation(RotateEvent e,
			IVisualPart<? extends Node> part) {
		// Point2D pivot = pivotInTargetPartVisuals.get(part);
		Angle rotationAngle = Angle.fromDeg(e.getTotalAngle());
		TransformPolicy transformPolicy = getTransformPolicy(part);
		if (transformPolicy != null) {
			transformPolicy.setPostRotate(rotationIndices.get(part),
					rotationAngle);
		}
	}

}

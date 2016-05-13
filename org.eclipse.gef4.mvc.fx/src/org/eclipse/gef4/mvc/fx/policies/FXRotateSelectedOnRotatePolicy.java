/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractInteractionPolicy;

import com.google.common.reflect.TypeToken;

import javafx.scene.Node;
import javafx.scene.input.RotateEvent;

/**
 * The {@link FXRotateSelectedOnRotatePolicy} is an {@link IFXOnRotatePolicy}
 * that rotates the whole {@link SelectionModel selection} when its
 * {@link #getHost() host} experiences a touch rotate gesture.
 *
 * @author anyssen
 *
 */
public class FXRotateSelectedOnRotatePolicy
		extends AbstractInteractionPolicy<Node> implements IFXOnRotatePolicy {

	private Point pivotInScene;
	private Map<IContentPart<Node, ? extends Node>, Integer> rotationIndices = new HashMap<>();
	private List<IContentPart<Node, ? extends Node>> targetParts;

	// gesture validity
	private boolean invalidGesture = false;

	/**
	 * Returns a {@link List} containing all {@link IContentPart}s that should
	 * be rotated by this policy. Per default, the whole {@link SelectionModel
	 * selection} is returned.
	 *
	 * @return A {@link List} containing all {@link IContentPart}s that should
	 *         be rotated by this policy.
	 */
	@SuppressWarnings("serial")
	protected List<IContentPart<Node, ? extends Node>> determineTargetParts() {
		return getHost().getRoot().getViewer()
				.getAdapter(new TypeToken<SelectionModel<Node>>() {
				}).getSelectionUnmodifiable();
	}

	/**
	 * Returns the target parts of this policy.
	 *
	 * @return The target parts of this policy.
	 */
	protected List<IContentPart<Node, ? extends Node>> getTargetParts() {
		return targetParts;
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

		for (IVisualPart<Node, ? extends Node> part : getTargetParts()) {
			updateOperation(e, part);
		}
	}

	@Override
	public void rotationAborted() {
		if (invalidGesture) {
			return;
		}

		// roll back transform operations
		for (IVisualPart<Node, ? extends Node> part : getTargetParts()) {
			FXTransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				restoreRefreshVisuals(part);
				rollback(transformPolicy);
			}
		}
	}

	@Override
	public void rotationFinished(RotateEvent e) {
		if (invalidGesture) {
			return;
		}

		// commit transform operations
		for (IVisualPart<Node, ? extends Node> part : getTargetParts()) {
			updateOperation(e, part);
			FXTransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				restoreRefreshVisuals(part);
				commit(transformPolicy);
			}
		}
	}

	@Override
	public void rotationStarted(RotateEvent e) {
		targetParts = determineTargetParts();

		invalidGesture = !isRotate(e);
		if (invalidGesture) {
			return;
		}

		Rectangle bounds = FXPartUtils
				.getUnionedVisualBoundsInScene(targetParts);
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
			IVisualPart<Node, ? extends Node> part) {
		// Point2D pivot = pivotInTargetPartVisuals.get(part);
		Angle rotationAngle = Angle.fromDeg(e.getTotalAngle());
		FXTransformPolicy transformPolicy = getTransformPolicy(part);
		if (transformPolicy != null) {
			transformPolicy.setPostRotate(rotationIndices.get(part),
					rotationAngle);
		}
	}

}

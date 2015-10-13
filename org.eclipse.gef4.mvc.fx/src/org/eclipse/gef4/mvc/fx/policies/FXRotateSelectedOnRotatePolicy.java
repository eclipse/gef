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

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.scene.Node;
import javafx.scene.input.RotateEvent;

/**
 * The {@link FXRotateSelectedOnRotatePolicy} is an
 * {@link AbstractFXOnRotatePolicy} that rotates the whole {@link SelectionModel
 * selection} when its {@link #getHost() host} experiences a touch rotate
 * gesture.
 *
 * @author anyssen
 *
 */
public class FXRotateSelectedOnRotatePolicy extends AbstractFXOnRotatePolicy {

	private Point pivotInScene;
	private Map<IContentPart<Node, ? extends Node>, Integer> rotationIndices = new HashMap<IContentPart<Node, ? extends Node>, Integer>();

	/**
	 * Returns a {@link List} containing all {@link IContentPart}s that should
	 * be rotated by this policy. Per default, the whole {@link SelectionModel
	 * selection} is returned.
	 *
	 * @return A {@link List} containing all {@link IContentPart}s that should
	 *         be rotated by this policy.
	 */
	protected List<IContentPart<Node, ? extends Node>> getTargetParts() {
		return getHost().getRoot().getViewer()
				.<SelectionModel<Node>> getAdapter(SelectionModel.class)
				.getSelected();
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
	public void rotate(RotateEvent e) {
		for (IVisualPart<Node, ? extends Node> part : getTargetParts()) {
			updateOperation(e, part);
		}
	}

	@Override
	public void rotationFinished(RotateEvent e) {
		for (IVisualPart<Node, ? extends Node> part : getTargetParts()) {
			updateOperation(e, part);
			FXTransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				restoreRefreshVisuals(part);
				getHost().getRoot().getViewer().getDomain()
						.execute(transformPolicy.commit());
			}
		}
	}

	@Override
	public void rotationStarted(RotateEvent e) {
		// determine pivot point
		Rectangle bounds = FXPartUtils
				.getUnionedVisualBoundsInScene(getTargetParts());
		pivotInScene = bounds == null ? null : bounds.getCenter();

		// initialize for all target parts
		rotationIndices.clear();
		for (IContentPart<Node, ? extends Node> part : getTargetParts()) {
			// transform pivot point to local coordinates
			FXTransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				storeAndDisableRefreshVisuals(part);
				transformPolicy.init();
				rotationIndices.put(part,
						transformPolicy.createPostRotate(pivotInScene));
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

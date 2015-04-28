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

import java.util.List;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.scene.Node;
import javafx.scene.input.RotateEvent;

public class FXRotateSelectedOnRotatePolicy extends AbstractFXOnRotatePolicy {

	private Point pivotInScene;

	protected FXRotatePolicy getRotatePolicy(
			IVisualPart<Node, ? extends Node> part) {
		return part.getAdapter(FXRotatePolicy.class);
	}

	protected List<IContentPart<Node, ? extends Node>> getTargetParts() {
		return getHost().getRoot().getViewer()
				.<SelectionModel<Node>> getAdapter(SelectionModel.class)
				.getSelected();
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
			FXRotatePolicy rotatePolicy = getRotatePolicy(part);
			if (rotatePolicy != null) {
				getHost().getRoot().getViewer().getDomain()
						.execute(rotatePolicy.commit());
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
		for (IVisualPart<Node, ? extends Node> part : getTargetParts()) {
			// transform pivot point to local coordinates
			FXRotatePolicy rotatePolicy = getRotatePolicy(part);
			if (rotatePolicy != null) {
				rotatePolicy.init();
			}
		}
	}

	private void updateOperation(RotateEvent e,
			IVisualPart<Node, ? extends Node> part) {
		// Point2D pivot = pivotInTargetPartVisuals.get(part);
		Angle rotationAngle = Angle.fromDeg(e.getTotalAngle());
		FXRotatePolicy rotatePolicy = getRotatePolicy(part);
		if (rotatePolicy != null) {
			rotatePolicy.performRotation(rotationAngle, pivotInScene);
		}
	}
}

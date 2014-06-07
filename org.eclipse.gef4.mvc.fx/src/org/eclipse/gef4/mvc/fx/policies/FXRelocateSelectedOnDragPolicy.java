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
package org.eclipse.gef4.mvc.fx.policies;

import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;

public class FXRelocateSelectedOnDragPolicy extends AbstractFXDragPolicy {

	private Point initialMouseLocation = null;

	protected FXResizeRelocatePolicy getResizeRelocatePolicy(
			IContentPart<Node> part) {
		return part.getAdapter(FXResizeRelocatePolicy.class);
	}

	public List<IContentPart<Node>> getTargetParts() {
		return getHost().getRoot().getViewer().getSelectionModel()
				.getSelected();
	}

	@Override
	public void press(MouseEvent e) {
		initialMouseLocation = new Point(e.getSceneX(), e.getSceneY());
		for (IContentPart<Node> part : getTargetParts()) {
			ITransactional policy = getResizeRelocatePolicy(part);
			if (policy != null) {
				policy.init();
			}
		}
	}

	@Override
	public void drag(MouseEvent e, Dimension delta, List<Node> nodesUnderMouse,
			List<IContentPart<Node>> partsUnderMouse) {
		for (IContentPart<Node> part : getTargetParts()) {
			FXResizeRelocatePolicy policy = getResizeRelocatePolicy(part);
			if (policy != null) {
				Point2D initialPos = part.getVisual().sceneToLocal(
						initialMouseLocation.x, initialMouseLocation.y);
				Point2D currentPos = part.getVisual().sceneToLocal(
						e.getSceneX(), e.getSceneY());
				Point2D deltaPoint = new Point2D(currentPos.getX()
						- initialPos.getX(), currentPos.getY()
						- initialPos.getY());
				policy.performResizeRelocate(deltaPoint.getX(), deltaPoint.getY(), 0, 0);
			}
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta,
			List<Node> nodesUnderMouse, List<IContentPart<Node>> partsUnderMouse) {
		// perform operation
		boolean performCommit = false;
		ReverseUndoCompositeOperation operation = new ReverseUndoCompositeOperation(
				"Resize/Relocate");
		for (IContentPart<Node> part : getTargetParts()) {
			ITransactional policy = getResizeRelocatePolicy(part);
			if (policy != null) {
				IUndoableOperation commit = policy.commit();
				if (commit != null) {
					operation.add(commit);
					performCommit = true;
				}
			}
		}
		if (performCommit) {
			executeOperation(operation);
		}
		initialMouseLocation = null;
	}

}

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
package org.eclipse.gef4.mvc.fx.example.policies;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.FXSelectionHandlePart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXWayPointPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class MoveWayPointOnHandleDragPolicy extends AbstractFXDragPolicy {

	public MoveWayPointOnHandleDragPolicy() {
	}

	@Override
	public void press(MouseEvent e) {
		getWayPointHandlePolicy(getHost().getAnchorages().get(0)).selectWayPoint(
				getVertexIndex(),
				new Point(e.getSceneX(), e.getSceneY()));
	}

	private int getVertexIndex() {
		return ((FXSelectionHandlePart) getHost()).getVertexIndex() - 1;
	}

	@Override
	public void drag(MouseEvent e, Dimension delta, List<Node> nodesUnderMouse,
			List<IContentPart<Node>> partsUnderMouse) {
		getWayPointHandlePolicy(getHost().getAnchorages().get(0)).moveWayPoint(
				new Point(e.getSceneX(), e.getSceneY()));
	}

	@Override
	public void release(MouseEvent e, Dimension delta,
			List<Node> nodesUnderMouse, List<IContentPart<Node>> partsUnderMouse) {
		IUndoableOperation operation = getWayPointHandlePolicy(getHost().getAnchorages().get(0)).commit();
		executeOperation(operation);
	}

	private AbstractFXWayPointPolicy getWayPointHandlePolicy(
			IVisualPart<Node> targetPart) {
		return targetPart.getAdapter(AbstractFXWayPointPolicy.class);
	}
	
}
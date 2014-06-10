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
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.FXSelectionHandlePart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXWayPointPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class MoveWayPointOnHandleDragPolicy extends AbstractFXDragPolicy {
	
	private List<IHandlePart<Node>> parts;

	public MoveWayPointOnHandleDragPolicy(List<IHandlePart<Node>> parts) {
		this.parts = parts;
	}
	
	@Override
	public void press(MouseEvent e) {
		getWayPointHandlePolicy(getHost().getAnchorages().get(0)).selectWayPoint(
				getSegmentIndex(),
				new Point(e.getSceneX(), e.getSceneY()));
	}

	private int getSegmentIndex() {
		return ((FXSelectionHandlePart) getHost()).getSegmentIndex() - 1;
	}

	@Override
	public void drag(MouseEvent e, Dimension delta, List<Node> nodesUnderMouse,
			List<IContentPart<Node>> partsUnderMouse) {
		IFXConnection connection = (IFXConnection) getHost().getAnchorages().get(0).getVisual();
		List<Point> before = connection.getWayPoints();
		
		getWayPointHandlePolicy(getHost().getAnchorages().get(0)).moveWayPoint(
				new Point(e.getSceneX(), e.getSceneY()));
		
		List<Point> after = connection.getWayPoints();
		
		if (before.size() != after.size()) {
			// re-assign segment index
			for (int i = 0; i < parts.size(); i++) {
				FXSelectionHandlePart part = (FXSelectionHandlePart) parts.get(i);
				part.setSegmentIndex(i);
			}
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta,
			List<Node> nodesUnderMouse, List<IContentPart<Node>> partsUnderMouse) {
		IUndoableOperation operation = getWayPointHandlePolicy(getHost().getAnchorages().get(0)).commit();
		executeOperation(operation);
	}

	private FXWayPointPolicy getWayPointHandlePolicy(
			IVisualPart<Node> targetPart) {
		return targetPart.getAdapter(FXWayPointPolicy.class);
	}
	
}
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
package org.eclipse.gef4.mvc.fx.ui.example.policies;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.FXSelectionHandlePart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXWayPointPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;

public class MoveWayPointOnHandleDragPolicy extends AbstractFXDragPolicy {

	private final IContentPart<Node> targetPart;
	private final FXSelectionHandlePart part;

	public MoveWayPointOnHandleDragPolicy(IContentPart<Node> targetPart,
			FXSelectionHandlePart part) {
		this.targetPart = targetPart;
		this.part = part;
	}

	@Override
	public void press(MouseEvent e) {
		getWayPointHandlePolicy(targetPart).selectWayPoint(
				part.getVertexIndex() - 1,
				new Point(e.getSceneX(), e.getSceneY()));
	}

	@Override
	public void drag(MouseEvent e, Dimension delta, List<Node> nodesUnderMouse,
			List<IContentPart<Node>> partsUnderMouse) {
		getWayPointHandlePolicy(targetPart).updateWayPoint(
				part.getVertexIndex() - 1,
				new Point(e.getSceneX(), e.getSceneY()));
	}

	@Override
	public void release(MouseEvent e, Dimension delta,
			List<Node> nodesUnderMouse, List<IContentPart<Node>> partsUnderMouse) {
		// operation =
		getWayPointHandlePolicy(targetPart).commitWayPoint(
				part.getVertexIndex() - 1,
				new Point(e.getSceneX(), e.getSceneY()));
		// FIXME: change way point operation bug: NPE
		// executeOperation(operation);
	}

	private AbstractFXWayPointPolicy getWayPointHandlePolicy(
			IContentPart<Node> targetPart) {
		return targetPart.getBound(AbstractFXWayPointPolicy.class);
	}
}
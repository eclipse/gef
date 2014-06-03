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
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.FXSelectionHandlePart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXReconnectPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;

public class ReconnectWayPointOnHandleDragPolicy extends AbstractFXDragPolicy {

	private final static Color FILL_CONNECTED = Color.web("#ff0000");
	
	private final IContentPart<Node> targetPart;
	private final FXSelectionHandlePart part;
	private final boolean isEndPoint;

	public ReconnectWayPointOnHandleDragPolicy(IContentPart<Node> targetPart,
			FXSelectionHandlePart part, boolean isEndPoint) {
		this.targetPart = targetPart;
		this.part = part;
		this.isEndPoint = isEndPoint;
	}

	@Override
	public void press(MouseEvent e) {
		AbstractFXReconnectPolicy p = getReconnectionPolicy(targetPart);
		if (p != null) {
			p.press(!isEndPoint, new Point(e.getSceneX(), e.getSceneY()));
		}
	}

	@Override
	public void drag(MouseEvent e, Dimension delta, List<Node> nodesUnderMouse,
			List<IContentPart<Node>> partsUnderMouse) {
		AbstractFXReconnectPolicy policy = getReconnectionPolicy(targetPart);
		policy.dragTo(new Point(e.getSceneX(), e.getSceneY()), partsUnderMouse);
		// TODO: move color change to some other place?
		if (policy.isConnected()) {
			((Shape) part.getVisual())
					.setFill(FILL_CONNECTED);
		} else {
			((Shape) part.getVisual()).setFill(FXSelectionHandlePart.FILL_BLUE);
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta,
			List<Node> nodesUnderMouse, List<IContentPart<Node>> partsUnderMouse) {
		IUndoableOperation operation = getReconnectionPolicy(targetPart)
				.commit();
		executeOperation(operation);
	}

	private AbstractFXReconnectPolicy getReconnectionPolicy(
			IContentPart<Node> targetPart) {
		return targetPart.getAdapter(AbstractFXReconnectPolicy.class);
	}
	
}
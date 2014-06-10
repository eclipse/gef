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
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class ReconnectWayPointOnHandleDragPolicy extends AbstractFXDragPolicy {

	private final static Color FILL_CONNECTED = Color.web("#ff0000");
	private final boolean isEndPoint;

	public ReconnectWayPointOnHandleDragPolicy(boolean isEndPoint) {
		this.isEndPoint = isEndPoint;
	}

	@Override
	public void press(MouseEvent e) {
		AbstractFXReconnectPolicy p = getReconnectionPolicy(getHost()
				.getAnchorages().get(0));
		if (p != null) {
			p.init();
			p.press(!isEndPoint, new Point(e.getSceneX(), e.getSceneY()));
		}
	}

	@Override
	public void drag(MouseEvent e, Dimension delta, List<Node> nodesUnderMouse,
			List<IContentPart<Node>> partsUnderMouse) {
		AbstractFXReconnectPolicy policy = getReconnectionPolicy(getHost()
				.getAnchorages().get(0));
		policy.dragTo(new Point(e.getSceneX(), e.getSceneY()), partsUnderMouse);
		if (policy.isConnected()) {
			((Shape) getHost().getVisual()).setFill(FILL_CONNECTED);
		} else {
			((Shape) getHost().getVisual())
					.setFill(FXSelectionHandlePart.FILL_BLUE);
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta,
			List<Node> nodesUnderMouse, List<IContentPart<Node>> partsUnderMouse) {
		IUndoableOperation operation = getReconnectionPolicy(
				getHost().getAnchorages().get(0)).commit();
		executeOperation(operation);
	}

	private AbstractFXReconnectPolicy getReconnectionPolicy(
			IVisualPart<Node> part) {
		return part.getAdapter(AbstractFXReconnectPolicy.class);
	}

}
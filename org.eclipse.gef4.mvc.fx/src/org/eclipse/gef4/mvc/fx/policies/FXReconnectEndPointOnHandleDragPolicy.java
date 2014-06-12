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
package org.eclipse.gef4.mvc.fx.policies;

import java.util.List;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.FXSegmentHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

// TODO: this is applicable only to parts with IFXConnection visual
public class FXReconnectEndPointOnHandleDragPolicy extends AbstractFXDragPolicy {

	private final boolean isEndPoint;

	public FXReconnectEndPointOnHandleDragPolicy(boolean isEndPoint) {
		this.isEndPoint = isEndPoint;
	}

	@Override
	public void drag(MouseEvent e, Dimension delta, List<Node> nodesUnderMouse,
			List<IContentPart<Node>> partsUnderMouse) {
		FXReconnectEndPointPolicy policy = getReconnectionPolicy(getHost()
				.getAnchorages().get(0));
		policy.dragTo(new Point(e.getSceneX(), e.getSceneY()), partsUnderMouse);
	}

	@Override
	public FXSegmentHandlePart getHost() {
		return (FXSegmentHandlePart) super.getHost();
	}

	private FXReconnectEndPointPolicy getReconnectionPolicy(IVisualPart<Node> part) {
		return part.getAdapter(FXReconnectEndPointPolicy.class);
	}

	@Override
	public void press(MouseEvent e) {
		FXReconnectEndPointPolicy p = getReconnectionPolicy(getHost().getAnchorages()
				.get(0));
		if (p != null) {
			p.init();
			p.press(!isEndPoint, new Point(e.getSceneX(), e.getSceneY()));
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta,
			List<Node> nodesUnderMouse, List<IContentPart<Node>> partsUnderMouse) {
		IUndoableOperation operation = getReconnectionPolicy(
				getHost().getAnchorages().get(0)).commit();
		executeOperation(operation);
	}

}
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
import javafx.scene.shape.Shape;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.example.parts.FXMidPointHandlePart;
import org.eclipse.gef4.mvc.fx.parts.FXSelectionHandlePart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXWayPointPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;

public class InsertWayPointOnHandleDragPolicy extends AbstractFXDragPolicy {

	// TODO: generalize (user should be able to bind this policy to any handle
	// port, not just FXMidPointHandleParts..)
	private final FXMidPointHandlePart hp;
	private final List<IHandlePart<Node>> parts;
	private final IContentPart<Node> targetPart;

	private AbstractFXWayPointPolicy getWayPointHandlePolicy(
			IContentPart<Node> targetPart) {
		return targetPart.getBound(AbstractFXWayPointPolicy.class);
	}

	public InsertWayPointOnHandleDragPolicy(FXMidPointHandlePart hp,
			List<IHandlePart<Node>> parts, IContentPart<Node> targetPart) {
		this.hp = hp;
		this.parts = parts;
		this.targetPart = targetPart;
	}

	@Override
	public void press(MouseEvent e) {
		// TODO: merge mid point and vertex handle parts
		if (hp.isVertex()) {
			getWayPointHandlePolicy(targetPart).selectWayPoint(
					hp.getVertexIndex() - 1,
					new Point(e.getSceneX(), e.getSceneY()));
		} else {
			getWayPointHandlePolicy(targetPart).createWayPoint(
					hp.getVertexIndex(),
					new Point(e.getSceneX(), e.getSceneY()));
			for (IHandlePart<Node> vertexHp : parts) {
				FXSelectionHandlePart part = (FXSelectionHandlePart) vertexHp;
				if (part.getVertexIndex() > hp.getVertexIndex()
						|| (part.getVertexIndex() == hp.getVertexIndex() && part
								.isEndPoint())) {
					part.incVertexIndex();
				}
			}
			// become vertex handle part
			hp.toVertex();
			((Shape) hp.getVisual()).setFill(FXSelectionHandlePart.FILL_BLUE);
		}
	}

	@Override
	public void drag(MouseEvent e, Dimension delta, List<Node> nodesUnderMouse,
			List<IContentPart<Node>> partsUnderMouse) {
		getWayPointHandlePolicy(targetPart).updateWayPoint(
				hp.getVertexIndex() - 1,
				new Point(e.getSceneX(), e.getSceneY()));
	}

	@Override
	public void release(MouseEvent e, Dimension delta,
			List<Node> nodesUnderMouse, List<IContentPart<Node>> partsUnderMouse) {
		getWayPointHandlePolicy(targetPart).commitWayPoint(
				hp.getVertexIndex() - 1,
				new Point(e.getSceneX(), e.getSceneY()));
	}

}
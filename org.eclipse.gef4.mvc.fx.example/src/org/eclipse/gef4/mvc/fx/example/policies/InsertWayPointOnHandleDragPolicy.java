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
import org.eclipse.gef4.mvc.fx.policies.FXWayPointPolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class InsertWayPointOnHandleDragPolicy extends AbstractFXDragPolicy {

	private List<IHandlePart<Node>> parts;

	private FXWayPointPolicy getWayPointHandlePolicy(IVisualPart<Node> part) {
		return part.getAdapter(FXWayPointPolicy.class);
	}

	public InsertWayPointOnHandleDragPolicy(List<IHandlePart<Node>> parts) {
		this.parts = parts;
	}

	@Override
	public void press(MouseEvent e) {
		FXSelectionHandlePart hp = (FXSelectionHandlePart) getHost();
		if (hp.getSegmentParameter() == 0.5) {
			getWayPointHandlePolicy(getHost().getAnchorages().get(0))
					.createWayPoint(hp.getSegmentIndex(),
							new Point(e.getSceneX(), e.getSceneY()));
			for (IHandlePart<Node> part : parts) {
				FXSelectionHandlePart p = (FXSelectionHandlePart) part;
				if (p.getSegmentIndex() > hp.getSegmentIndex()
						|| (p.getSegmentIndex() == hp.getSegmentIndex() && p
								.getSegmentParameter() == 1)) {
					p.setSegmentIndex(p.getSegmentIndex() + 1);
				}
			}
			hp.setSegmentParameter(1);
		} else {
			getWayPointHandlePolicy(getHost().getAnchorages().get(0))
					.selectWayPoint(hp.getSegmentIndex() - 1,
							new Point(e.getSceneX(), e.getSceneY()));
		}
	}

	@Override
	public void drag(MouseEvent e, Dimension delta, List<Node> nodesUnderMouse,
			List<IContentPart<Node>> partsUnderMouse) {
		// TODO: if moved onto an existing waypoint, we should probably delete that (as when moving bendpoints)
		getWayPointHandlePolicy(getHost().getAnchorages().get(0)).moveWayPoint(
				new Point(e.getSceneX(), e.getSceneY()));
	}

	@Override
	public void release(MouseEvent e, Dimension delta,
			List<Node> nodesUnderMouse, List<IContentPart<Node>> partsUnderMouse) {
		// defensively fire at least one drag() before a release()
		drag(e, delta, nodesUnderMouse, partsUnderMouse);
		IUndoableOperation operation = getWayPointHandlePolicy(
				getHost().getAnchorages().get(0)).commit();
		executeOperation(operation);
	}

}
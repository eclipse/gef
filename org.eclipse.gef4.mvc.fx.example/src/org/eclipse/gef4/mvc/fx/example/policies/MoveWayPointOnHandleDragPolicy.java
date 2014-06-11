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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.parts.PartUtils;

public class MoveWayPointOnHandleDragPolicy extends AbstractFXDragPolicy {

	public MoveWayPointOnHandleDragPolicy() {
	}

	@Override
	public void press(MouseEvent e) {
		getWayPointHandlePolicy(getHost().getAnchorages().get(0))
				.selectWayPoint(getSegmentIndex(),
						new Point(e.getSceneX(), e.getSceneY()));
	}

	private int getSegmentIndex() {
		return ((FXSelectionHandlePart) getHost()).getSegmentIndex() - 1;
	}

	@Override
	public void drag(MouseEvent e, Dimension delta, List<Node> nodesUnderMouse,
			List<IContentPart<Node>> partsUnderMouse) {
		IFXConnection connection = (IFXConnection) getHost().getAnchorages()
				.get(0).getVisual();

		List<Point> before = connection.getWayPoints();

		getWayPointHandlePolicy(getHost().getAnchorages().get(0)).moveWayPoint(
				new Point(e.getSceneX(), e.getSceneY()));

		List<Point> after = connection.getWayPoints();

		if (before.size() != after.size()) {
			// re-assign segment index and segment parameter
//			System.out.println("Before: " + before.size() + " waypoints");
//			System.out.println("After: " + after.size() + " waypoints");
			List<FXSelectionHandlePart> parts = PartUtils.filterParts(
					PartUtils.getAnchoreds(getHost().getAnchorages()),
					FXSelectionHandlePart.class);
			Collections.sort(parts, new Comparator<FXSelectionHandlePart>() {
				@Override
				public int compare(FXSelectionHandlePart o1,
						FXSelectionHandlePart o2) {
					if (o1.getSegmentIndex() < o2.getSegmentIndex()) {
						return -1;
					} else if (o1.getSegmentIndex() == o2.getSegmentIndex()) {
						return (int) (o1.getSegmentParameter() * 2 - o2
								.getSegmentParameter() * 2);
					} else {
						return 1;
					}
				}
			});
//			System.out.println("Found " + parts.size()
//					+ " FXSelectionHandleParts");
			Iterator<FXSelectionHandlePart> it = parts.iterator();
			FXSelectionHandlePart part = null;
			for (int i = 0; i <= after.size(); i++) {
				// param 0
				part = it.next();
//				System.out.println("Reassigned index " + part.getSegmentIndex()
//						+ " - " + part.getSegmentParameter() + " to " + i
//						+ " - " + 0.0);
				part.setSegmentIndex(i);
				part.setSegmentParameter(0.0);
				// param 0.5
				part = it.next();
//				System.out.println("Reassigned index " + part.getSegmentIndex()
//						+ " - " + part.getSegmentParameter() + " to " + i
//						+ " - " + 0.5);
				part.setSegmentIndex(i);
			}
			// param 1
			part = it.next();
//			System.out.println("Reassigned index " + part.getSegmentIndex()
//					+ " - " + part.getSegmentParameter() + " to " + (after.size())
//					+ " - " + 1.0);
			part.setSegmentIndex(after.size());
			part.setSegmentParameter(1.0);
			
			// not used -> could be removed (and re-added)
			while(it.hasNext()){
				part = it.next();
//				System.out.println("Superfluous " + part.getSegmentIndex()
//						+ " - " + part.getSegmentParameter());
			}
//			System.out.println("");
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta,
			List<Node> nodesUnderMouse, List<IContentPart<Node>> partsUnderMouse) {
		IUndoableOperation operation = getWayPointHandlePolicy(
				getHost().getAnchorages().get(0)).commit();
		executeOperation(operation);
	}

	private FXWayPointPolicy getWayPointHandlePolicy(
			IVisualPart<Node> targetPart) {
		return targetPart.getAdapter(FXWayPointPolicy.class);
	}

}
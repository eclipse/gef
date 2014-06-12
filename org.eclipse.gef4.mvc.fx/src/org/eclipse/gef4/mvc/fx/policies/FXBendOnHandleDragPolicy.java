/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG) - Fixes related to bug #437076
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.FXSegmentHandlePart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.parts.PartUtils;

/**
 * 
 * @author mwienand
 * @author anyssen
 * 
 */
// TODO: this is only applicable to FXSelectionHandlePart hosts and should
// enforce it (best by template parameter)
public class FXBendOnHandleDragPolicy extends AbstractFXDragPolicy {

	private int createdSegmentIndex;

	private void adjustHandles(List<Point> oldWaypoints,
			List<Point> newWaypoints) {
		if (oldWaypoints.size() != newWaypoints.size()) {
			// re-assign segment index and segment parameter
			// System.out.println("Before: " + oldWaypoints.size() +
			// " waypoints");
			// System.out.println("After: " + newWaypoints.size() +
			// " waypoints");
			List<FXSegmentHandlePart> parts = PartUtils.filterParts(
					PartUtils.getAnchoreds(getHost().getAnchorages()),
					FXSegmentHandlePart.class);
			Collections.<FXSegmentHandlePart> sort(parts);
			// System.out.println("Found " + parts.size()
			// + " FXSelectionHandleParts");
			Iterator<FXSegmentHandlePart> it = parts.iterator();
			FXSegmentHandlePart part = null;
			for (int i = 0; i <= newWaypoints.size(); i++) {
				// param 0
				part = it.next();
				// System.out.println("Reassigned index " +
				// part.getSegmentIndex()
				// + " - " + part.getSegmentParameter() + " to " + i
				// + " - " + 0.0);
				setSegmentIndex(part, i);
				setSegmentParameter(part, 0.0);

				// skip mid point handles around newly created waypoints
				if (createdSegmentIndex < 0
						|| part.getSegmentIndex() != createdSegmentIndex - 1
						&& part.getSegmentIndex() != createdSegmentIndex) {
					// param 0.5
					part = it.next();
					// System.out.println("Reassigned index "
					// + part.getSegmentIndex() + " - "
					// + part.getSegmentParameter() + " to " + i + " - "
					// + 0.5);
					setSegmentIndex(part, i);
					setSegmentParameter(part, 0.5);
				}
			}
			// param 1
			part = it.next();
			// System.out.println("Reassigned index " + part.getSegmentIndex()
			// + " - " + part.getSegmentParameter() + " to "
			// + (newWaypoints.size()) + " - " + 1.0);
			setSegmentIndex(part, newWaypoints.size());
			setSegmentParameter(part, 1.0);

			// not used -> could be removed (and re-added)
			while (it.hasNext()) {
				part = it.next();
				// System.out.println("Reassigned index " +
				// part.getSegmentIndex()
				// + " - " + part.getSegmentParameter() + " to " + -1
				// + " - " + 1.0);
				// hide (but do not remove from root part and anchorage yet
				// (this will be initiated upon commit)
				setSegmentIndex(part, -1);
			}
		}
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
		adjustHandles(before, after);
	}

	@Override
	public FXSegmentHandlePart getHost() {
		return (FXSegmentHandlePart) super.getHost();
	}

	private int getSegmentIndex() {
		return getHost().getSegmentIndex() - 1;
	}

	private FXBendPolicy getWayPointHandlePolicy(IVisualPart<Node> targetPart) {
		return targetPart.getAdapter(FXBendPolicy.class);
	}

	@Override
	public void press(MouseEvent e) {
		createdSegmentIndex = -1;
		FXSegmentHandlePart hp = getHost();
		if (hp.getSegmentParameter() == 0.5) {
			// select create new way point
			getWayPointHandlePolicy(getHost().getAnchorages().get(0))
					.createWayPoint(hp.getSegmentIndex(),
							new Point(e.getSceneX(), e.getSceneY()));
			List<FXSegmentHandlePart> parts = PartUtils.filterParts(
					PartUtils.getAnchoreds(getHost().getAnchorages()),
					FXSegmentHandlePart.class);
			Collections.<FXSegmentHandlePart> sort(parts);
			for (FXSegmentHandlePart p : parts) {
				if (p.getSegmentIndex() > hp.getSegmentIndex()
						|| (p.getSegmentIndex() == hp.getSegmentIndex() && p
								.getSegmentParameter() == 1)) {
					p.setSegmentIndex(p.getSegmentIndex() + 1);
				}
			}
			hp.setSegmentIndex(hp.getSegmentIndex() + 1);
			hp.setSegmentParameter(0);
			createdSegmentIndex = hp.getSegmentIndex();
		} else {
			// select existing way point
			getWayPointHandlePolicy(getHost().getAnchorages().get(0))
					.selectWayPoint(getSegmentIndex(),
							new Point(e.getSceneX(), e.getSceneY()));
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta,
			List<Node> nodesUnderMouse, List<IContentPart<Node>> partsUnderMouse) {
		IUndoableOperation operation = getWayPointHandlePolicy(
				getHost().getAnchorages().get(0)).commit();
		executeOperation(operation);
	}

	private void setSegmentIndex(FXSegmentHandlePart part, int value) {
		if (part.getSegmentIndex() != value) {
			part.setSegmentIndex(value);
		}
	}

	private void setSegmentParameter(FXSegmentHandlePart part, double value) {
		if (part.getSegmentParameter() != value) {
			part.setSegmentParameter(value);
		}
	}

}
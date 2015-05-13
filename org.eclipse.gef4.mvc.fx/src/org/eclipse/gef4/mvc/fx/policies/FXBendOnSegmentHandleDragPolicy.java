/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.fx.nodes.FXConnection;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.parts.PartUtils;

/**
 *
 * @author mwienand
 * @author anyssen
 *
 */
// TODO: this is only applicable to FXSegmentHandlePart hosts
public class FXBendOnSegmentHandleDragPolicy extends AbstractFXOnDragPolicy {

	private int createdSegmentIndex;
	private boolean initialRefreshVisual = true;

	private void adjustHandles(List<Point> oldWaypoints,
			List<Point> newWaypoints) {
		if (oldWaypoints.size() != newWaypoints.size()) {
			// re-assign segment index and segment parameter
			// System.out.println("Before: " + oldWaypoints.size() +
			// " waypoints");
			// System.out.println("After: " + newWaypoints.size() +
			// " waypoints");
			List<FXCircleSegmentHandlePart> parts = PartUtils.filterParts(
					PartUtils.getAnchoreds(getHost().getAnchorages().keySet()),
					FXCircleSegmentHandlePart.class);
			Collections.<FXCircleSegmentHandlePart> sort(parts);
			// System.out.println("Found " + parts.size() +
			// " FXSelectionHandleParts");
			Iterator<FXCircleSegmentHandlePart> it = parts.iterator();
			FXCircleSegmentHandlePart part = null;
			for (int i = 0; i <= newWaypoints.size(); i++) {
				// param 0
				part = it.next();
				// System.out.println("Reassigned index " +
				// part.getSegmentIndex() + " - " + part.getSegmentParameter() +
				// " to " + i + " - " + 0.0);
				setSegmentIndex(part, i);
				setSegmentParameter(part, 0.0);

				// skip mid point handles around newly created waypoints
				if (createdSegmentIndex < 0
						|| part.getSegmentIndex() != createdSegmentIndex - 1
						&& part.getSegmentIndex() != createdSegmentIndex) {
					// param 0.5
					part = it.next();
					// System.out.println("Reassigned index " +
					// part.getSegmentIndex() + " - " +
					// part.getSegmentParameter() + " to " + i + " - " + 0.5);
					setSegmentIndex(part, i);
					setSegmentParameter(part, 0.5);
				}
			}
			// param 1
			part = it.next();
			// System.out.println("Reassigned index " + part.getSegmentIndex() +
			// " - " + part.getSegmentParameter() + " to " +
			// (newWaypoints.size()) + " - " + 1.0);
			setSegmentIndex(part, newWaypoints.size());
			setSegmentParameter(part, 1.0);

			// not used -> could be removed (and re-added)
			while (it.hasNext()) {
				part = it.next();
				// System.out.println("Reassigned index " +
				// part.getSegmentIndex() + " - " + part.getSegmentParameter() +
				// " to " + -1 + " - " + 1.0);
				// hide (but do not remove from root part and anchorage yet
				// (this will be initiated upon commit)
				setSegmentIndex(part, -1);
			}
		}
	}

	protected void disableRefreshVisuals() {
		IVisualPart<Node, ? extends Node> anchorage = getHost().getAnchorages()
				.keySet().iterator().next();
		anchorage.setRefreshVisual(false);
		initialRefreshVisual = anchorage.isRefreshVisual();
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		IVisualPart<Node, ? extends Node> anchorage = getHost().getAnchorages()
				.keySet().iterator().next();
		FXConnection connection = (FXConnection) anchorage.getVisual();

		List<Point> before = new ArrayList<Point>(connection.getWayPoints());

		getBendPolicy(anchorage).moveSelectedSegmentPoint(
				new Point(e.getSceneX(), e.getSceneY()));

		List<Point> after = new ArrayList<Point>(connection.getWayPoints());

		adjustHandles(before, after);
	}

	protected void enableRefreshVisuals() {
		IVisualPart<Node, ? extends Node> anchorage = getHost().getAnchorages()
				.keySet().iterator().next();
		anchorage.setRefreshVisual(initialRefreshVisual);
	}

	protected FXBendPolicy getBendPolicy(
			IVisualPart<Node, ? extends Node> targetPart) {
		// retrieve the default bend policy
		return targetPart.getAdapter(FXBendPolicy.class);
	}

	@Override
	public FXCircleSegmentHandlePart getHost() {
		return (FXCircleSegmentHandlePart) super.getHost();
	}

	@Override
	public void press(MouseEvent e) {
		createdSegmentIndex = -1;
		FXCircleSegmentHandlePart hp = getHost();
		IVisualPart<Node, ? extends Node> anchorage = getHost().getAnchorages()
				.keySet().iterator().next();

		disableRefreshVisuals(anchorage);
		init(getBendPolicy(anchorage));

		if (hp.getSegmentParameter() == 0.5) {
			// create new way point
			getBendPolicy(anchorage).createAndSelectSegmentPoint(
					hp.getSegmentIndex(),
					new Point(e.getSceneX(), e.getSceneY()));

			// find other segment handle parts
			List<FXCircleSegmentHandlePart> parts = PartUtils.filterParts(
					PartUtils.getAnchoreds(getHost().getAnchorages().keySet()),
					FXCircleSegmentHandlePart.class);

			// sort parts by segment index and parameter
			Collections.<FXCircleSegmentHandlePart> sort(parts);

			// increment segment index of succeeding parts
			for (FXCircleSegmentHandlePart p : parts) {
				if (p.getSegmentIndex() > hp.getSegmentIndex()
						|| (p.getSegmentIndex() == hp.getSegmentIndex() && p
								.getSegmentParameter() == 1)) {
					p.setSegmentIndex(p.getSegmentIndex() + 1);
				}
			}

			// adjust index and parameter of this segment handle part
			hp.setSegmentIndex(hp.getSegmentIndex() + 1);
			hp.setSegmentParameter(0);
			createdSegmentIndex = hp.getSegmentIndex();
		} else {
			// select existing way point
			getBendPolicy(anchorage).selectSegmentPoint(hp.getSegmentIndex(),
					hp.getSegmentParameter(),
					new Point(e.getSceneX(), e.getSceneY()));
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		IVisualPart<Node, ? extends Node> anchorage = getHost().getAnchorages()
				.keySet().iterator().next();
		enableRefreshVisuals();
		// TODO: we need to ensure this can be done before
		// enableRefreshVisuals(), because visuals should already be up to date
		// (and we thus save a potential refresh)
		commit(getBendPolicy(anchorage));
	}

	private void setSegmentIndex(FXCircleSegmentHandlePart part, int value) {
		if (part.getSegmentIndex() != value) {
			part.setSegmentIndex(value);
		}
	}

	private void setSegmentParameter(FXCircleSegmentHandlePart part, double value) {
		if (part.getSegmentParameter() != value) {
			part.setSegmentParameter(value);
		}
	}

}
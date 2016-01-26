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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.parts.PartUtils;

import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * The {@link FXBendOnSegmentHandleDragPolicy} is an {@link IFXOnDragPolicy}
 * that can be installed on the handle parts of an {@link Connection}, so that
 * the user is able to manipulate that connection by dragging its handles. This
 * policy expects that a handle is created for each anchor point of the
 * connection (start, way, end), as well as for each middle point of a segment.
 * Moreover, this policy expects that the respective handles are of type
 * {@link FXCircleSegmentHandlePart}.
 *
 * @author mwienand
 * @author anyssen
 *
 */
// TODO: this is only applicable to FXSegmentHandlePart hosts
public class FXBendOnSegmentHandleDragPolicy extends AbstractFXInteractionPolicy
		implements IFXOnDragPolicy {

	private int createdSegmentIndex;

	private CursorSupport cursorSupport = new CursorSupport(this);

	private void adjustHandles(List<Point> points,
			boolean skipMidPointsAroundCreated) {
		// re-assign segment index and segment parameter
		List<FXCircleSegmentHandlePart> parts = PartUtils.filterParts(
				PartUtils.getAnchoreds(
						getHost().getAnchoragesUnmodifiable().keySet()),
				FXCircleSegmentHandlePart.class);

		Collections.<FXCircleSegmentHandlePart> sort(parts);
		// System.out.println("Found " + points.size() + " points.");
		// System.out.println(
		// "Found " + parts.size() + " FXSelectionHandleParts");
		Iterator<FXCircleSegmentHandlePart> it = parts.iterator();
		FXCircleSegmentHandlePart part = null;
		for (int i = 0; i < points.size() - 1; i++) {
			// param 0
			if (it.hasNext()) {
				part = it.next();
				// System.out.println("Reassigned index " +
				// part.getSegmentIndex()
				// + " - " + part.getSegmentParameter() + " to " + i
				// + " - " + 0.0);
				setSegmentIndex(part, i);
				setSegmentParameter(part, 0.0);
			}

			// skip mid point handles around newly created waypoints
			if (createdSegmentIndex < 0 || !skipMidPointsAroundCreated
					|| part.getSegmentIndex() != createdSegmentIndex - 1
							&& part.getSegmentIndex() != createdSegmentIndex) {
				// param 0.5
				if (it.hasNext()) {
					part = it.next();
					// System.out.println(
					// "Reassigned index " + part.getSegmentIndex() + " - "
					// + part.getSegmentParameter() + " to " + i
					// + " - " + 0.5);
					setSegmentIndex(part, i);
					setSegmentParameter(part, 0.5);
				}
			}

			// param 1
			if (i == points.size() - 2) {
				if (it.hasNext()) {
					part = it.next();
					// System.out.println(
					// "Reassigned index " + part.getSegmentIndex() + " - "
					// + part.getSegmentParameter() + " to " + i
					// + " - " + 1.0);
					setSegmentIndex(part, i);
					setSegmentParameter(part, 1.0);
				}
			}
		}
		// not used -> could be removed (and re-added)
		while (it.hasNext()) {
			part = it.next();
			// System.out.println("Reassigned index " + part.getSegmentIndex()
			// + " - " + part.getSegmentParameter() + " to " + -1 + " - "
			// + 1.0);
			// hide (but do not remove from root part and anchorage yet
			// (this will be initiated upon commit)
			setSegmentIndex(part, -1);
		}
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		IVisualPart<Node, ? extends Connection> targetPart = getTargetPart();
		Connection connection = targetPart.getVisual();

		List<Point> before = Arrays.asList(connection.getPoints());

		getBendPolicy(targetPart)
				.moveSelectedPoint(new Point(e.getSceneX(), e.getSceneY()));

		List<Point> after = Arrays.asList(connection.getPoints());

		if (before.size() != after.size()) {
			adjustHandles(after, true);
		}
	}

	/**
	 * Returns the {@link FXBendPolicy} that is installed on the given
	 * {@link IVisualPart}.
	 *
	 * @param targetPart
	 *            The {@link IVisualPart} of which the installed
	 *            {@link FXBendPolicy} is returned.
	 * @return The {@link FXBendPolicy} that is installed on the given
	 *         {@link IVisualPart}.
	 */
	protected FXBendPolicy getBendPolicy(
			IVisualPart<Node, ? extends Node> targetPart) {
		// retrieve the default bend policy
		return targetPart.getAdapter(FXBendPolicy.class);
	}

	/**
	 * Returns the {@link CursorSupport} of this policy.
	 *
	 * @return The {@link CursorSupport} of this policy.
	 */
	protected CursorSupport getCursorSupport() {
		return cursorSupport;
	}

	@Override
	public FXCircleSegmentHandlePart getHost() {
		return (FXCircleSegmentHandlePart) super.getHost();
	}

	/**
	 * Returns the target {@link IVisualPart} for this policy. Per default the
	 * first anchorage is returned, which has to be an {@link IVisualPart} with
	 * an {@link Connection} visual.
	 *
	 * @return The target {@link IVisualPart} for this policy.
	 */
	@SuppressWarnings("unchecked")
	protected IVisualPart<Node, ? extends Connection> getTargetPart() {
		return (IVisualPart<Node, ? extends Connection>) getHost()
				.getAnchoragesUnmodifiable().keySet().iterator().next();
	}

	@Override
	public void hideIndicationCursor() {
		getCursorSupport().restoreCursor();
	}

	@Override
	public void press(MouseEvent e) {
		createdSegmentIndex = -1;
		FXCircleSegmentHandlePart hostPart = getHost();
		IVisualPart<Node, ? extends Connection> targetPart = getTargetPart();

		storeAndDisableRefreshVisuals(targetPart);
		init(getBendPolicy(targetPart));

		if (hostPart.getSegmentParameter() == 0.5) {
			// create new way point
			getBendPolicy(targetPart).createAndSelectPoint(
					hostPart.getSegmentIndex(),
					new Point(e.getSceneX(), e.getSceneY()));

			// find other segment handle parts
			List<FXCircleSegmentHandlePart> parts = PartUtils.filterParts(
					PartUtils.getAnchoreds(
							getHost().getAnchoragesUnmodifiable().keySet()),
					FXCircleSegmentHandlePart.class);

			// sort parts by segment index and parameter
			Collections.<FXCircleSegmentHandlePart> sort(parts);

			// increment segment index of succeeding parts
			for (FXCircleSegmentHandlePart p : parts) {
				if (p.getSegmentIndex() > hostPart.getSegmentIndex()
						|| (p.getSegmentIndex() == hostPart.getSegmentIndex()
								&& p.getSegmentParameter() == 1)) {
					p.setSegmentIndex(p.getSegmentIndex() + 1);
				}
			}

			// adjust index and parameter of this segment handle part
			hostPart.setSegmentIndex(hostPart.getSegmentIndex() + 1);
			hostPart.setSegmentParameter(0);
			createdSegmentIndex = hostPart.getSegmentIndex();
		} else {
			// select existing way point
			getBendPolicy(targetPart).selectPoint(hostPart.getSegmentIndex(),
					hostPart.getSegmentParameter(),
					new Point(e.getSceneX(), e.getSceneY()));
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		IVisualPart<Node, ? extends Connection> targetPart = getTargetPart();
		restoreRefreshVisuals(targetPart);
		// TODO: we need to ensure this can be done before
		// enableRefreshVisuals(), because visuals should already be up to date
		// (and we thus save a potential refresh)
		commit(getBendPolicy(targetPart));

		// it may be that the bend policy returns null (no-op) because a newly
		// created segment point was direcly removed through overlay. In this
		// case, we need to adjust the handles as well
		adjustHandles(
				Arrays.asList(
						((Connection) targetPart.getVisual()).getPoints()),
				false);
	}

	private void setSegmentIndex(FXCircleSegmentHandlePart part, int value) {
		if (part.getSegmentIndex() != value) {
			part.setSegmentIndex(value);
		}
	}

	private void setSegmentParameter(FXCircleSegmentHandlePart part,
			double value) {
		if (part.getSegmentParameter() != value) {
			part.setSegmentParameter(value);
		}
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		return false;
	}

}
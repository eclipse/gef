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

import java.util.List;

import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.OrthogonalRouter;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.policies.FXBendConnectionPolicy.AnchorHandle;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * The {@link FXBendFirstAnchorageOnSegmentHandleDragPolicy} is an
 * {@link IFXOnDragPolicy} that can be installed on the handle parts of an
 * {@link Connection}, so that the user is able to manipulate that connection by
 * dragging its handles. This policy expects that a handle is created for each
 * anchor point of the connection (start, way, end), as well as for each middle
 * point of a segment. Moreover, this policy expects that the respective handles
 * are of type {@link FXCircleSegmentHandlePart}.
 *
 * @author mwienand
 * @author anyssen
 *
 */
// TODO: this is only applicable to FXSegmentHandlePart hosts
public class FXBendFirstAnchorageOnSegmentHandleDragPolicy
		extends AbstractFXInteractionPolicy implements IFXOnDragPolicy {

	private CursorSupport cursorSupport = new CursorSupport(this);

	private IVisualPart<Node, ? extends Connection> targetPart;

	private Point initialMouseInScene;

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		Connection connection = targetPart.getVisual();
		List<Point> before = connection.getPoints();

		getBendPolicy(targetPart).move(initialMouseInScene,
				new Point(e.getSceneX(), e.getSceneY()));

		List<Point> after = connection.getPoints();
		if (before.size() != after.size()) {
			targetPart.getAdapter(SelectionBehavior.class).updateHandles();
		}
	}

	@Override
	public void dragAborted() {
		restoreRefreshVisuals(targetPart);
		rollback(getBendPolicy(targetPart));
		targetPart.getAdapter(SelectionBehavior.class).updateHandles();
	}

	/**
	 * Returns the {@link FXBendConnectionPolicy} that is installed on the given
	 * {@link IVisualPart}.
	 *
	 * @param targetPart
	 *            The {@link IVisualPart} of which the installed
	 *            {@link FXBendConnectionPolicy} is returned.
	 * @return The {@link FXBendConnectionPolicy} that is installed on the given
	 *         {@link IVisualPart}.
	 */
	protected FXBendConnectionPolicy getBendPolicy(
			IVisualPart<Node, ? extends Node> targetPart) {
		// retrieve the default bend policy
		return targetPart.getAdapter(FXBendConnectionPolicy.class);
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
		initialMouseInScene = new Point(e.getSceneX(), e.getSceneY());
		FXCircleSegmentHandlePart hostPart = getHost();
		targetPart = getTargetPart();

		storeAndDisableRefreshVisuals(targetPart);
		FXBendConnectionPolicy bendPolicy = getBendPolicy(targetPart);
		init(bendPolicy);

		if (hostPart.getSegmentParameter() == 0.5) {
			if (e.isShiftDown() || targetPart.getVisual()
					.getRouter() instanceof OrthogonalRouter) {
				// move segment, copy ends when connected

				// determine indices of neighbor anchors
				int firstSegmentIndex = hostPart.getSegmentIndex();
				int secondSegmentIndex = hostPart.getSegmentIndex() + 1;

				// determine connectedness for neighbor anchors
				Node firstAnchorage = targetPart.getVisual()
						.getAnchor(firstSegmentIndex).getAnchorage();
				boolean isFirstConnected = firstAnchorage != null
						&& firstAnchorage != targetPart.getVisual();
				Node secondAnchorage = targetPart.getVisual()
						.getAnchor(secondSegmentIndex).getAnchorage();
				boolean isSecondConnected = secondAnchorage != null
						&& secondAnchorage != targetPart.getVisual();

				// XXX: Make second explicit first so that the first segment
				// index is still valid.
				AnchorHandle secondAnchorHandle = bendPolicy
						.makeExplicitBefore(secondSegmentIndex);
				AnchorHandle firstAnchorHandle = bendPolicy
						.makeExplicitAfter(firstSegmentIndex);

				// copy first if connected
				if (isFirstConnected) {
					System.out.println("copy first :: initial "
							+ firstAnchorHandle.getInitialPosition()
							+ " :: current " + firstAnchorHandle.getPosition());
					// TODO: transform to scene
					firstAnchorHandle = bendPolicy.createAfter(
							firstAnchorHandle,
							firstAnchorHandle.getInitialPosition());
				}

				// copy second if connected
				if (isSecondConnected) {
					System.out.println("copy second :: initial "
							+ secondAnchorHandle.getInitialPosition()
							+ " :: current "
							+ secondAnchorHandle.getPosition());
					// TODO: transform to scene
					secondAnchorHandle = bendPolicy.createBefore(
							secondAnchorHandle,
							secondAnchorHandle.getInitialPosition());
				}

				// select the end anchors for manipulation
				bendPolicy.select(firstAnchorHandle);
				bendPolicy.select(secondAnchorHandle);
			} else {
				// create new way point in middle and move it (disabled for
				// orthogonal connections)

				AnchorHandle previousAnchorHandle = bendPolicy
						.findExplicitAnchorBackwards(
								hostPart.getSegmentIndex());
				AnchorHandle newAnchorHandle = bendPolicy
						.createAfter(previousAnchorHandle, initialMouseInScene);

				// select for manipulation
				bendPolicy.select(newAnchorHandle);
			}
		} else if (hostPart.getSegmentParameter() == 0.25) {
			// split segment, move its first halve

			// determine segment indices for neighbor anchors
			int firstSegmentIndex = hostPart.getSegmentIndex();
			int secondSegmentIndex = hostPart.getSegmentIndex() + 1;

			// determine middle of segment
			Point firstPoint = targetPart.getVisual()
					.getPoint(firstSegmentIndex);
			Point secondPoint = targetPart.getVisual()
					.getPoint(secondSegmentIndex);
			Vector direction = new Vector(firstPoint, secondPoint);
			Point midPoint = firstPoint.getTranslated(direction.x / 2,
					direction.y / 2);
			Point2D midInScene = targetPart.getVisual().localToScene(midPoint.x,
					midPoint.y);

			System.out.println("[split segment 0.25]");
			System.out.println("first point = " + firstPoint);
			System.out.println("second point = " + secondPoint);
			System.out.println("mid point = " + midPoint);

			// determine connectedness of first anchor handle
			Node firstAnchorage = targetPart.getVisual()
					.getAnchor(firstSegmentIndex).getAnchorage();
			boolean isFirstConnected = firstAnchorage != null
					&& firstAnchorage != targetPart.getVisual();

			// make the anchor handles explicit
			// XXX: Make second explicit first so that the first segment
			// index is still valid.
			AnchorHandle secondAnchorHandle = bendPolicy
					.makeExplicitBefore(secondSegmentIndex);
			AnchorHandle firstAnchorHandle = bendPolicy
					.makeExplicitAfter(firstSegmentIndex);

			// copy first point if connected
			if (isFirstConnected) {
				System.out.println("copy first :: initial "
						+ firstAnchorHandle.getInitialPosition()
						+ " :: current " + firstAnchorHandle.getPosition());
				// use the copy as the new first anchor handle
				// TODO: transform to scene
				firstAnchorHandle = bendPolicy.createAfter(firstAnchorHandle,
						firstAnchorHandle.getInitialPosition());
			}

			// create new anchor at the segment's middle
			secondAnchorHandle = bendPolicy.createAfter(firstAnchorHandle,
					FX2Geometry.toPoint(midInScene));
			// copy that new anchor
			secondAnchorHandle = bendPolicy.createAfter(firstAnchorHandle,
					FX2Geometry.toPoint(midInScene));

			// select the first anchor and the copy of the new mid anchor for
			// movement
			bendPolicy.select(firstAnchorHandle);
			bendPolicy.select(secondAnchorHandle);
		} else if (hostPart.getSegmentParameter() == 0.75) {
			// split segment, move its second halve

			// determine segment indices for neighbor anchors
			int firstSegmentIndex = hostPart.getSegmentIndex();
			int secondSegmentIndex = hostPart.getSegmentIndex() + 1;

			// determine middle of segment
			Point firstPoint = targetPart.getVisual()
					.getPoint(firstSegmentIndex);
			Point secondPoint = targetPart.getVisual()
					.getPoint(secondSegmentIndex);
			Vector direction = new Vector(firstPoint, secondPoint);
			Point midPoint = firstPoint.getTranslated(direction.x / 2,
					direction.y / 2);
			Point2D midInScene = targetPart.getVisual().localToScene(midPoint.x,
					midPoint.y);

			// determine connectedness of second anchor handle
			Node secondAnchorage = targetPart.getVisual()
					.getAnchor(secondSegmentIndex).getAnchorage();
			boolean isSecondConnected = secondAnchorage != null
					&& secondAnchorage != targetPart.getVisual();

			// make the anchor handles explicit
			// XXX: Make second explicit first so that the first segment
			// index is still valid.
			AnchorHandle secondAnchorHandle = bendPolicy
					.makeExplicitBefore(secondSegmentIndex);
			AnchorHandle firstAnchorHandle = bendPolicy
					.makeExplicitAfter(firstSegmentIndex);

			// copy second point if connected
			if (isSecondConnected) {
				System.out.println("copy second :: initial "
						+ secondAnchorHandle.getInitialPosition()
						+ " :: current " + secondAnchorHandle.getPosition());
				// use the copy as the new first anchor handle
				// TODO: transform to scene
				secondAnchorHandle = bendPolicy.createBefore(secondAnchorHandle,
						secondAnchorHandle.getInitialPosition());
			}

			// create new anchor at the segment's middle
			firstAnchorHandle = bendPolicy.createAfter(firstAnchorHandle,
					FX2Geometry.toPoint(midInScene));
			// copy that new anchor
			firstAnchorHandle = bendPolicy.createAfter(firstAnchorHandle,
					FX2Geometry.toPoint(midInScene));

			// select the second anchor and the copy of the new mid anchor for
			// movement
			bendPolicy.select(firstAnchorHandle);
			bendPolicy.select(secondAnchorHandle);
		} else {
			// compute connection index from handle part data
			int connectionIndex = hostPart.getSegmentIndex()
					+ (hostPart.getSegmentParameter() == 1 ? 1 : 0);

			// make anchor explicit if it is implicit
			bendPolicy.select(bendPolicy.makeExplicitAfter(connectionIndex));
		}

		// update handles
		targetPart.getAdapter(SelectionBehavior.class).updateHandles();
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		commit(getBendPolicy(targetPart));
		restoreRefreshVisuals(targetPart);
		// it may be that the bend policy returns null (no-op) because a newly
		// created segment point was direcly removed through overlay. In this
		// case, we need to update the handles as well
		targetPart.getAdapter(SelectionBehavior.class).updateHandles();
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
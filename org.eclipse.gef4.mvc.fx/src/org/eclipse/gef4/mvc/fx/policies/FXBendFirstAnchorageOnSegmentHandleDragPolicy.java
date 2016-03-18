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

import java.util.Comparator;
import java.util.List;

import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.OrthogonalRouter;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.policies.FXBendConnectionPolicy.AnchorHandle;
import org.eclipse.gef4.mvc.parts.IHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.reflect.TypeToken;

import javafx.geometry.Bounds;
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
	private Point currentMouseInScene;

	private Comparator<IHandlePart<Node, ? extends Node>> handleDistanceComparator = new Comparator<IHandlePart<Node, ? extends Node>>() {
		@Override
		public int compare(IHandlePart<Node, ? extends Node> interactedWith,
				IHandlePart<Node, ? extends Node> other) {
			// Bounds bounds = getHost().getVisual().getLayoutBounds();
			// Point2D position = getHost().getVisual().localToScene(
			// bounds.getMinX() + bounds.getWidth() / 2,
			// bounds.getMinY() + bounds.getHeight() / 2);
			Bounds otherBounds = other.getVisual().getLayoutBounds();
			Point2D otherPosition = other.getVisual()
					.localToScene(otherBounds.getMinX()
							+ otherBounds.getWidth() / 2,
					otherBounds.getMinY() + otherBounds.getHeight() / 2);
			// only useful to find the most similar part
			return (int) (currentMouseInScene
					.getDistance(FX2Geometry.toPoint(otherPosition)) * 10);
		}
	};

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		currentMouseInScene.setX(e.getSceneX());
		currentMouseInScene.setY(e.getSceneY());

		Connection connection = targetPart.getVisual();
		List<Point> before = connection.getPoints();

		getBendPolicy(targetPart).move(initialMouseInScene,
				currentMouseInScene);

		List<Point> after = connection.getPoints();

		if (before.size() != after.size()) {
			updateHandles();
		}
	}

	@Override
	public void dragAborted() {
		restoreRefreshVisuals(targetPart);
		rollback(getBendPolicy(targetPart));
		updateHandles();
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
	public AbstractFXSegmentHandlePart<? extends Node> getHost() {
		return (AbstractFXSegmentHandlePart<? extends Node>) super.getHost();
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
		currentMouseInScene = initialMouseInScene.getCopy();
		AbstractFXSegmentHandlePart<? extends Node> hostPart = getHost();
		targetPart = getTargetPart();

		storeAndDisableRefreshVisuals(targetPart);
		FXBendConnectionPolicy bendPolicy = getBendPolicy(targetPart);
		init(bendPolicy);

		if (hostPart.getSegmentParameter() == 0.5) {
			if (e.isShiftDown() || targetPart.getVisual()
					.getRouter() instanceof OrthogonalRouter) {
				// move segment, copy ends when connected
				bendPolicy.selectSegment(hostPart.getSegmentIndex());
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

			// determine connectedness of first anchor handle
			Node firstAnchorage = targetPart.getVisual()
					.getAnchor(firstSegmentIndex).getAnchorage();
			boolean isFirstConnected = firstAnchorage != null
					&& firstAnchorage != targetPart.getVisual();

			// make the anchor handles explicit
			boolean isStart = firstSegmentIndex == 0;
			List<AnchorHandle> explicit = bendPolicy.makeExplicit(
					isStart ? firstSegmentIndex : firstSegmentIndex - 1,
					secondSegmentIndex);
			AnchorHandle firstAnchorHandle = explicit.get(isStart ? 0 : 1);
			AnchorHandle secondAnchorHandle = explicit.get(isStart ? 1 : 2);

			// copy first point if connected
			if (isFirstConnected) {
				// use the copy as the new first anchor handle
				firstAnchorHandle = bendPolicy.createAfter(firstAnchorHandle,
						FX2Geometry.toPoint(targetPart.getVisual().localToScene(
								Geometry2FX.toFXPoint(firstAnchorHandle
										.getInitialPosition()))));
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
			boolean isEnd = secondSegmentIndex == targetPart.getVisual()
					.getAnchors().size() - 1;
			List<AnchorHandle> explicit = bendPolicy.makeExplicit(
					firstSegmentIndex,
					isEnd ? secondSegmentIndex : secondSegmentIndex + 1);
			AnchorHandle firstAnchorHandle = explicit.get(0);
			AnchorHandle secondAnchorHandle = explicit.get(1);

			// copy second point if connected
			if (isSecondConnected) {
				// use the copy as the new first anchor handle
				secondAnchorHandle = bendPolicy.createBefore(secondAnchorHandle,
						FX2Geometry.toPoint(targetPart.getVisual().localToScene(
								Geometry2FX.toFXPoint(secondAnchorHandle
										.getInitialPosition()))));
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
			bendPolicy.select(bendPolicy
					.makeExplicit(connectionIndex, connectionIndex).get(0));
		}

		// move initially to remove a possible overlay
		bendPolicy.move(new Point(), new Point());
		updateHandles();
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		currentMouseInScene.setX(e.getSceneX());
		currentMouseInScene.setY(e.getSceneY());
		commit(getBendPolicy(targetPart));
		restoreRefreshVisuals(targetPart);
		// updateHandles();
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		return false;
	}

	/**
	 * Re-computes the handle parts. Adjusts the host to reflect its new
	 * position.
	 */
	@SuppressWarnings({ "serial", "unchecked" })
	protected void updateHandles() {
		IHandlePart<Node, ? extends Node> replacementHandle = targetPart
				.getAdapter(new TypeToken<SelectionBehavior<Node>>() {
				}).updateHandles(handleDistanceComparator, getHost());
		if (replacementHandle instanceof AbstractFXSegmentHandlePart) {
			AbstractFXSegmentHandlePart<Node> segmentData = (AbstractFXSegmentHandlePart<Node>) replacementHandle;
			getHost().setSegmentIndex(segmentData.getSegmentIndex());
			getHost().setSegmentParameter(segmentData.getSegmentParameter());
		}
	}

}
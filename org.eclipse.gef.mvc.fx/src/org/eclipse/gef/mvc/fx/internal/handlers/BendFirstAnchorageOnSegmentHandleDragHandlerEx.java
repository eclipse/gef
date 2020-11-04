/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG) - Fixes related to bug #437076
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.internal.handlers;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.eclipse.gef.fx.internal.nodes.IBendableCurve;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnDragHandler;
import org.eclipse.gef.mvc.fx.handlers.SnapToSupport;
import org.eclipse.gef.mvc.fx.internal.behaviors.BendConnectionPolicyEx;
import org.eclipse.gef.mvc.fx.internal.behaviors.BendCurvePolicy;
import org.eclipse.gef.mvc.fx.models.HoverModel;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;
import org.eclipse.gef.mvc.fx.parts.AbstractSegmentHandlePart;
import org.eclipse.gef.mvc.fx.parts.CircleSegmentHandlePart;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IHandlePart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.BendConnectionPolicy;

import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * The {@link BendFirstAnchorageOnSegmentHandleDragHandlerEx} is an
 * {@link IOnDragHandler} that can be installed on the handle parts of an
 * {@link Connection}, so that the user is able to manipulate that connection by
 * dragging its handles. This policy expects that a handle is created for each
 * anchor point of the connection (start, way, end), as well as for each middle
 * point of a segment. Moreover, this policy expects that the respective handles
 * are of type {@link CircleSegmentHandlePart}.
 *
 * @author mwienand
 * @author anyssen
 *
 */
// TODO: this is only applicable to FXSegmentHandlePart hosts
@SuppressWarnings("restriction")
public class BendFirstAnchorageOnSegmentHandleDragHandlerEx
		extends AbstractHandler implements IOnDragHandler {

	private SnapToSupport snapToSupport = null;
	private IVisualPart<? extends Node> targetPart;
	private Point initialMouseInScene;
	private Point handlePositionInScene;
	private int initialSegmentIndex;
	private double initialSegmentParameter;
	private boolean isInvalid = false;
	private BendCurvePolicy bendPolicy;
	private Point startPositionInScene;

	private Comparator<IHandlePart<? extends Node>> handleDistanceComparator = new Comparator<IHandlePart<? extends Node>>() {
		@Override
		public int compare(IHandlePart<? extends Node> interactedWith,
				IHandlePart<? extends Node> other) {
			Bounds otherBounds = other.getVisual().getLayoutBounds();
			Point2D otherPosition = other.getVisual().localToScene(
					otherBounds.getMinX() + otherBounds.getWidth() / 2,
					otherBounds.getMinY() + otherBounds.getHeight() / 2);
			// only useful to find the most similar part
			return (int) (handlePositionInScene
					.getDistance(FX2Geometry.toPoint(otherPosition)) * 10);
		}
	};

	@Override
	public void abortDrag() {
		if (isInvalid) {
			return;
		}
		restoreRefreshVisuals(targetPart);
		rollback(bendPolicy);
		updateHandles();
		bendPolicy = null;
		targetPart = null;
		if (snapToSupport != null) {
			snapToSupport.stopSnapping();
			snapToSupport = null;
		}
	}

	/**
	 * Returns the {@link BendCurvePolicy} that is installed on the
	 * {@link #getTargetPart()}.
	 *
	 * @return The {@link BendCurvePolicy} that is installed on the
	 *         {@link #getTargetPart()}.
	 */
	protected BendCurvePolicy determineBendPolicy() {
		// retrieve the default bend policy from the target part
		return targetPart.getAdapter(BendCurvePolicy.class);
	}

	/**
	 * Determines the target {@link IVisualPart} for this interaction handler.
	 * Per default, the first anchorage of the {@link #getHost()} is returned.
	 *
	 * @return The target {@link IVisualPart} for this interaction handler.
	 */
	@SuppressWarnings("unchecked")
	protected IVisualPart<? extends Connection> determineTargetPart() {
		return (IVisualPart<? extends Connection>) getHost()
				.getAnchoragesUnmodifiable().keySet().iterator().next();
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		if (isInvalid) {
			return;
		}

		// apply mouse-delta to selected-position-in-scene
		Point currentPositionInScene = startPositionInScene
				.getTranslated(delta);

		// snap to grid
		if (snapToSupport != null) {
			if (!isPrecise(e)) {
				currentPositionInScene.translate(snapToSupport.snap(delta));
			} else {
				snapToSupport.clearSnappingFeedback();
			}
		}

		// perform changes
		Point finalPositionInScene = bendPolicy.move(startPositionInScene,
				currentPositionInScene);

		handlePositionInScene.setX(finalPositionInScene.x);
		handlePositionInScene.setY(finalPositionInScene.y);

		updateHandles();
	}

	@Override
	public void endDrag(MouseEvent e, Dimension delta) {
		if (isInvalid) {
			return;
		}
		commit(bendPolicy);
		restoreRefreshVisuals(targetPart);
		updateHandles();
		bendPolicy = null;
		targetPart = null;
		if (snapToSupport != null) {
			snapToSupport.stopSnapping();
			snapToSupport = null;
		}
	}

	/**
	 * Returns the {@link BendCurvePolicy} to use for manipulating the
	 * {@link #getTargetPart()}.
	 *
	 * @return The {@link BendCurvePolicy} to use for manipulating the
	 *         {@link #getTargetPart()}.
	 */
	protected BendCurvePolicy getBendPolicy() {
		return bendPolicy;
	}

	@Override
	public AbstractSegmentHandlePart<? extends Node> getHost() {
		return (AbstractSegmentHandlePart<? extends Node>) super.getHost();
	}

	/**
	 * Returns the target {@link IVisualPart} for this policy that is determined
	 * using {@link #determineTargetPart()} if it is not set, yet.
	 *
	 * @return The target {@link IVisualPart} for this policy.
	 */
	protected IVisualPart<? extends Node> getTargetPart() {
		return targetPart;
	}

	@Override
	public void hideIndicationCursor() {
	}

	/**
	 * Returns <code>true</code> if the given {@link MouseEvent} should trigger
	 * bend, <code>false</code> otherwise. Otherwise returns <code>false</code>
	 * . By default will always return <code>true</code>.
	 *
	 * @param event
	 *            The {@link MouseEvent} in question.
	 * @return <code>true</code> if the given {@link MouseEvent} should trigger
	 *         bend, otherwise <code>false</code>.
	 */
	protected boolean isBend(MouseEvent event) {
		return true;
	}

	/**
	 * Returns <code>true</code> if precise manipulations should be performed
	 * for the given {@link MouseEvent}. Otherwise returns <code>false</code>.
	 *
	 * @param e
	 *            The {@link MouseEvent} that is used to determine if precise
	 *            manipulations should be performed (i.e. if the corresponding
	 *            modifier key is pressed).
	 * @return <code>true</code> if precise manipulations should be performed,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isPrecise(MouseEvent e) {
		return e.isShortcutDown();
	}

	/**
	 * Prepares the given {@link BendConnectionPolicy} for the manipulation of
	 * its host part.
	 *
	 * @param isShiftDown
	 *            <code>true</code> if shift is pressed, otherwise
	 *            <code>false</code>.
	 * @param bendPolicy
	 *            {@link BendConnectionPolicy} of the target part.
	 */
	@SuppressWarnings("unchecked")
	private void prepareBend(boolean isShiftDown, BendCurvePolicy bendPolicy) {
		AbstractSegmentHandlePart<? extends Node> host = getHost();
		IBendableCurve<? extends Node, ? extends Node> targetVisual = (IBendableCurve<? extends Node, ? extends Node>) targetPart
				.getVisual();
		if (host.getSegmentParameter() == 0.5) {
			if (isShiftDown || (targetPart.getVisual() instanceof Connection
					&& ((Connection) targetVisual)
							.getRouter() instanceof OrthogonalRouter)) {
				// move segment, copy ends when connected
				bendPolicy.selectSegment(host.getSegmentIndex(),
						host.getSegmentIndex() + 1);
			} else {
				// create new way point in middle and move it (disabled for
				// orthogonal connections)
				Integer previousAnchorHandle = host.getSegmentIndex();
				if (bendPolicy instanceof BendConnectionPolicyEx) {
					previousAnchorHandle = ((BendConnectionPolicyEx) bendPolicy)
							.getExplicitIndexAtOrBefore(host.getSegmentIndex());
				}
				Integer newAnchorHandle = bendPolicy
						.createAfter(previousAnchorHandle, initialMouseInScene);

				// select for manipulation
				bendPolicy.select(newAnchorHandle);
			}
		} else if (host.getSegmentParameter() == 0.25
				|| host.getSegmentParameter() == 0.75) {
			// split segment
			boolean selectFirstHalve = host.getSegmentParameter() == 0.25;

			// determine segment indices for neighbor anchors
			int firstSegmentIndex = host.getSegmentIndex();
			int secondSegmentIndex = host.getSegmentIndex() + 1;

			// determine middle of segment
			Point firstPoint = ((IBendableCurve<? extends Node, ? extends Node>) targetPart
					.getVisual()).getPoint(firstSegmentIndex);
			Point secondPoint = ((IBendableCurve<? extends Node, ? extends Node>) targetPart
					.getVisual()).getPoint(secondSegmentIndex);
			Vector direction = new Vector(firstPoint, secondPoint);
			Point midPoint = firstPoint.getTranslated(direction.x / 2,
					direction.y / 2);
			Point2D midInScene = targetPart.getVisual().localToScene(midPoint.x,
					midPoint.y);

			// determine connected status of start or end point (depending on
			// which side of the segment is moved after splitting)
			boolean isConnected = ((IBendableCurve<? extends Node, ? extends Node>) targetPart
					.getVisual())
							.isConnected(selectFirstHalve ? firstSegmentIndex
									: secondSegmentIndex);

			// make the anchors at the segment indices explicit
			Integer firstAnchorHandle = firstSegmentIndex;
			Integer secondAnchorHandle = secondSegmentIndex;
			if (bendPolicy instanceof BendConnectionPolicyEx) {
				List<Integer> explicit = ((BendConnectionPolicyEx) bendPolicy)
						.makeExplicit(firstSegmentIndex, secondSegmentIndex);
				firstAnchorHandle = explicit.get(0);
				secondAnchorHandle = explicit.get(1);
			}

			// selected for movement
			if (isConnected) {
				// compute connection index for point to copy
				// TODO: Remove duplicate code (see
				// BendConnectionOperation#getConnectionIndex(int)).
				int explicitCount = -1;
				int connectionIndex = 0;
				for (; connectionIndex < targetVisual.getPointsUnmodifiable()
						.size(); connectionIndex++) {
					if (!(targetVisual instanceof Connection)
							|| !(((Connection) targetVisual).getRouter())
									.wasInserted(((Connection) targetVisual)
											.getAnchor(connectionIndex))) {
						explicitCount++;
					}
					if (explicitCount == (selectFirstHalve ? firstAnchorHandle
							: secondAnchorHandle)) {
						// found all operation indices
						break;
					}
				}
				// determine position in scene for point to copy
				Point positionInScene = FX2Geometry.toPoint(targetPart
						.getVisual().localToScene(Geometry2FX.toFXPoint(
								targetVisual.getPoint(connectionIndex))));
				// copy the anchor
				if (selectFirstHalve) {
					firstAnchorHandle = bendPolicy
							.createAfter(firstAnchorHandle, positionInScene);
				} else {
					secondAnchorHandle = bendPolicy
							.createBefore(secondAnchorHandle, positionInScene);
				}
			}

			// create new anchor at segment's middle and copy that new anchor so
			// that the copy can be selected for movement
			if (selectFirstHalve) {
				secondAnchorHandle = bendPolicy.createAfter(firstAnchorHandle,
						FX2Geometry.toPoint(midInScene));
				secondAnchorHandle = bendPolicy.createAfter(firstAnchorHandle,
						FX2Geometry.toPoint(midInScene));
			} else {
				firstAnchorHandle = bendPolicy.createAfter(firstAnchorHandle,
						FX2Geometry.toPoint(midInScene));
				firstAnchorHandle = bendPolicy.createAfter(firstAnchorHandle,
						FX2Geometry.toPoint(midInScene));
				// increment second anchor handle because we added 2 points
				// before that
				secondAnchorHandle += 2;
			}

			// select the anchors for movement
			bendPolicy.select(firstAnchorHandle);
			bendPolicy.select(secondAnchorHandle);
		} else {
			// compute connection index from handle part data
			int connectionIndex = host.getSegmentIndex()
					+ (host.getSegmentParameter() == 1 ? 1 : 0);

			// make anchor explicit if it is implicit
			int index = connectionIndex;
			if (bendPolicy instanceof BendConnectionPolicyEx) {
				index = ((BendConnectionPolicyEx) bendPolicy)
						.makeExplicit(connectionIndex, connectionIndex).get(0);
			}
			bendPolicy.select(index);
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

	@Override
	public void startDrag(MouseEvent e) {
		isInvalid = !isBend(e);
		if (isInvalid) {
			return;
		}

		initialMouseInScene = new Point(e.getSceneX(), e.getSceneY());
		handlePositionInScene = initialMouseInScene.getCopy();

		AbstractSegmentHandlePart<? extends Node> hostPart = getHost();
		initialSegmentIndex = hostPart.getSegmentIndex();
		initialSegmentParameter = hostPart.getSegmentParameter();

		targetPart = determineTargetPart();
		storeAndDisableRefreshVisuals(targetPart);

		bendPolicy = determineBendPolicy();
		init(bendPolicy);

		prepareBend(e.isShiftDown(), bendPolicy);
		// move initially so that the initial positions for the selected
		// points are computed
		bendPolicy.move(initialMouseInScene, initialMouseInScene);
		// query selected position
		List<Point> initialPositions = bendPolicy.getSelectedInitialPositions();
		Point startPositionInConnectionLocal = initialPositions.get(0);
		startPositionInScene = FX2Geometry.toPoint(targetPart.getVisual()
				.localToScene(startPositionInConnectionLocal.x,
						startPositionInConnectionLocal.y));

		snapToSupport = targetPart instanceof IContentPart
				? targetPart.getViewer().getAdapter(SnapToSupport.class)
				: null;
		if (snapToSupport != null) {
			// Only report HSL or VSL depending on segment orientation
			SnappingLocation vssl = new SnappingLocation(
					(IContentPart<? extends Node>) targetPart,
					Orientation.VERTICAL, startPositionInScene.y);
			SnappingLocation hssl = new SnappingLocation(
					(IContentPart<? extends Node>) targetPart,
					Orientation.HORIZONTAL, startPositionInScene.x);
			Orientation orientation = bendPolicy.getOrientation();
			snapToSupport.startSnapping(
					(IContentPart<? extends Node>) targetPart,
					orientation == Orientation.HORIZONTAL
							? Arrays.asList(vssl, hssl)
							: Arrays.asList(hssl, vssl));
		}
		updateHandles();
	}

	/**
	 * Re-computes the handle parts. Adjusts the host to reflect its new
	 * position.
	 */
	@SuppressWarnings("unchecked")
	protected void updateHandles() {
		if (!(targetPart instanceof IContentPart)) {
			return;
		}
		IContentPart<? extends Node> targetContentPart = (IContentPart<? extends Node>) targetPart;
		IHandlePart<? extends Node> replacementHandle = targetPart.getRoot()
				.getAdapter(SelectionBehavior.class).updateHandles(
						targetContentPart, handleDistanceComparator, getHost());
		if (replacementHandle instanceof AbstractSegmentHandlePart) {
			AbstractSegmentHandlePart<Node> segmentData = (AbstractSegmentHandlePart<Node>) replacementHandle;
			getHost().setSegmentIndex(segmentData.getSegmentIndex());
			getHost().setSegmentParameter(segmentData.getSegmentParameter());
			if (segmentData.getSegmentParameter() == initialSegmentParameter) {
				// Restore hover if the replacement handle fulfills the same
				// role as the host (same parameter == same role).
				getHost().getRoot().getViewer().getAdapter(HoverModel.class)
						.setHover(getHost());
			} else if (!((initialSegmentParameter == 0.25
					|| initialSegmentParameter == 0.75)
					&& segmentData.getSegmentParameter() == 0.5
					&& Math.abs(segmentData.getSegmentIndex()
							- initialSegmentIndex) < 2)) {
				// XXX: If a quarter handle was dragged and replaced by a mid
				// handle, we do not clear hover.
				getHost().getRoot().getViewer().getAdapter(HoverModel.class)
						.clearHover();
			}
		}
	}
}
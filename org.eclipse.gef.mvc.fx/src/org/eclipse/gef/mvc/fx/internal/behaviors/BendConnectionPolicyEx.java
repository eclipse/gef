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
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.internal.behaviors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.internal.nodes.IBendableCurve;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.IConnectionRouter;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart.BendPoint;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;

import com.google.inject.Provider;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * The {@link BendConnectionPolicyEx} can be used to manipulate the points
 * constituting an {@link Connection}, i.e. its start, way, and end points. Each
 * point is realized though an {@link BendPoint}, which may either be local to
 * the {@link Connection} (i.e. the anchor refers to the {@link Connection} as
 * anchorage), or it may be provided by another {@link IVisualPart} (i.e. the
 * anchor is provided by a {@link Provider} adapted to the part), to which the
 * connection is being connected.
 *
 * When moving a point the policy takes care of:
 * <ul>
 * <li>Removing overlaid neighbor points.</li>
 * <li>Re-adding temporarily removed neighbor points.</li>
 * <li>Reconnecting points to the {@link IVisualPart} under mouse when
 * applicable.</li>
 * </ul>
 *
 * @author mwienand
 * @author anyssen
 */
@SuppressWarnings("restriction")
public class BendConnectionPolicyEx extends BendCurvePolicy {

	/**
	 * An {@link ImplicitGroup} stores an {@link AnchorHandle} and a number of
	 * subsequent implicit {@link Point}s.
	 *
	 * @author mwienand
	 *
	 */
	private static class ImplicitGroup {
		int precedingExplicitIndex;
		List<Point> points = new ArrayList<>();

		public ImplicitGroup(int precedingExplicitIndex) {
			this.precedingExplicitIndex = precedingExplicitIndex;
		}
	}

	private boolean isNormalizationNeeded = false;

	private Point preMoveStartHint = null;
	private Point preMoveEndHint = null;

	/**
	 * Determines if the anchor at the given explicit index can be replaced with
	 * an anchor that is obtained from an underlying visual part. Per default,
	 * only the start and the end index can be connected.
	 *
	 * @param explicitAnchorIndex
	 *            The explicit anchor index for which to determine if it can be
	 *            connected.
	 * @return <code>true</code> if the anchor at the given index can be
	 *         connected, otherwise <code>false</code>.
	 */
	@Override
	protected boolean canConnect(int explicitAnchorIndex) {
		return explicitAnchorIndex == 0
				|| explicitAnchorIndex == getBendOperation()
						.getFinalBendPoints().size() - 1;
	}

	@Override
	public ITransactionalOperation commit() {
		if (isNormalizationNeeded) {
			// showAnchors("pre-norm:");
			normalize();
			// showAnchors("commit:");
		}

		return super.commit();
	}

	private Point computeEndHint() {
		if (getCurve().getEndAnchor() instanceof DynamicAnchor
				&& getCurve().getPointsUnmodifiable().size() > 1) {
			Point endPoint = getCurve().getEndPoint();
			Point neighbor = getCurve()
					.getPoint(getCurve().getPointsUnmodifiable().size() - 2);
			Point translated = endPoint.getTranslated(
					endPoint.getDifference(neighbor).getScaled(0.5));
			return translated;
		}
		return null;
	}

	private Point computeStartHint() {
		if (getCurve().getStartAnchor() instanceof DynamicAnchor
				&& getCurve().getPointsUnmodifiable().size() > 1) {
			Point startPoint = getCurve().getStartPoint();
			Point neighbor = getCurve().getPoint(1);
			Point translated = startPoint.getTranslated(
					startPoint.getDifference(neighbor).getScaled(0.5));
			return translated;
		}
		return null;
	}

	/**
	 * Creates a new anchor after the anchor specified by the given explicit
	 * anchor index. Returns the new anchor's explicit index.
	 *
	 * @param explicitAnchorIndex
	 *            An explicit anchor index that references the explicit anchor
	 *            after which the new anchor is inserted.
	 * @param mouseInScene
	 *            The position for the new anchor in scene coordinates.
	 *
	 * @return The index for the new anchor.
	 */
	@Override
	public int createAfter(int explicitAnchorIndex, Point mouseInScene) {
		checkInitialized();
		// create point => normalization needed after commit
		isNormalizationNeeded = true;
		// determine insertion index
		return super.createAfter(explicitAnchorIndex, mouseInScene);
	}

	/**
	 * Creates a new anchor before the anchor specified by the given explicit
	 * anchor index. Returns the new anchor's explicit index.
	 *
	 * @param explicitAnchorIndex
	 *            An explicit anchor index that references the explicit anchor
	 *            before which the new anchor is inserted.
	 * @param mouseInScene
	 *            The position for the new anchor in scene coordinates.
	 *
	 * @return The index for the new anchor.
	 */
	@Override
	public int createBefore(int explicitAnchorIndex, Point mouseInScene) {
		checkInitialized();
		// create point => normalization needed after commit
		isNormalizationNeeded = true;
		// determine insertion index
		return super.createBefore(explicitAnchorIndex, mouseInScene);
	}

	/**
	 * Returns the {@link Connection} that is manipulated by this policy.
	 *
	 * @return The {@link Connection} that is manipulated by this policy.
	 */
	@SuppressWarnings("unchecked")
	protected IBendableCurve<? extends Node, ? extends Node> getCurve() {
		return (IBendableCurve<? extends Node, ? extends Node>) getHost()
				.getVisual();
	}

	/**
	 * Returns the explicit anchor index for the first explicit anchor that is
	 * found within the connection's anchors when starting to search at the
	 * given connection index, and incrementing the index by the given step per
	 * iteration.
	 *
	 * @param startConnectionIndex
	 *            The index at which the search starts.
	 * @param step
	 *            The increment step (e.g. <code>1</code> or <code>-1</code>).
	 * @return The explicit anchor index for the first explicit anchor that is
	 *         found within the connection's anchors when starting to search at
	 *         the given index.
	 */
	protected int getExplicitIndex(int startConnectionIndex, int step) {
		if (getCurve() instanceof Connection) {
			Connection connection = (Connection) getCurve();
			List<BendPoint> bpoints = getHost().getVisualBendPoints();
			List<IAnchor> anchors = connection.getAnchorsUnmodifiable();
			IConnectionRouter router = connection.getRouter();

			// first find explicit index at or after
			// then decrement if needed
			int atOrBeforeBi = -1;
			for (int ci = 0, bi = -1; ci < anchors.size()
					&& bi < bpoints.size(); ci++) {
				if (!router.wasInserted(anchors.get(ci))) {
					bi++;
				}
				if (ci >= startConnectionIndex) {
					atOrBeforeBi = bi;
					break;
				}
			}

			if (atOrBeforeBi == -1) {
				throw new IllegalStateException(
						"Start of connection is implicit, i.e. inserted by the router.");
			}

			if (step < 0) {
				return atOrBeforeBi;
			} else {
				if (router.wasInserted(anchors.get(startConnectionIndex))) {
					return atOrBeforeBi + 1;
				} else {
					return atOrBeforeBi;
				}
			}
		}
		return startConnectionIndex;

	}

	/**
	 * Returns an explicit anchor index for the first explicit anchor that can
	 * be found when iterating the connection anchors forwards, starting at the
	 * given connection index. If the anchor at the given index is an explicit
	 * anchor, an explicit anchor index for that anchor will be returned. If no
	 * explicit anchor is found, an exception is thrown, because the start and
	 * end anchor of a connection need to be explicit.
	 *
	 * @param connectionIndex
	 *            The index that specifies the anchor of the connection at which
	 *            the search starts.
	 * @return An explicit anchor index for the next explicit anchor.
	 */
	public int getExplicitIndexAtOrAfter(int connectionIndex) {
		return getExplicitIndex(connectionIndex, 1);
	}

	/**
	 * Returns an explicit anchor index for the first explicit anchor that can
	 * be found when iterating the connection anchors backwards, starting at the
	 * given connection index. If the anchor at the given index is an explicit
	 * anchor, an explicit anchor index for that anchor will be returned. If no
	 * explicit anchor is found, an exception is thrown, because the start and
	 * end anchor of a connection need to be explicit.
	 *
	 * @param connectionIndex
	 *            The index that specifies the anchor of the connection at which
	 *            the search starts.
	 * @return An explicit anchor index for the previous explicit anchor.
	 */
	public int getExplicitIndexAtOrBefore(int connectionIndex) {
		return getExplicitIndex(connectionIndex, -1);
	}

	/**
	 * Returns the visual point index for the given bend point index.
	 *
	 * @param bendPointIndex
	 *            The index of the bend point
	 * @return the visual index.
	 */
	private int getVisualIndex(int bendPointIndex) {
		if (getCurve() instanceof Connection) {
			Connection connection = (Connection) getHost().getVisual();
			IConnectionRouter router = connection.getRouter();
			ObservableList<IAnchor> anchors = connection
					.getAnchorsUnmodifiable();
			for (int ci = 0, bi = 0; ci < anchors.size(); ci++) {
				if (!router.wasInserted(anchors.get(ci))) {
					if (bi == bendPointIndex) {
						return ci;
					}
					bi++;
				}
			}
			throw new IllegalStateException(
					"Cannot find connection index for BendPoint index.");
		} else {
			return bendPointIndex;
		}

	}

	/**
	 * Returns the current position for the given explicit anchor index, within
	 * the local coordinate system of the {@link Connection}.
	 *
	 * @param bendPointIndex
	 *            The index
	 * @return The {@link Point} in local {@link Connection} coordinates.
	 */
	@Override
	protected Point getVisualPoint(int bendPointIndex) {
		int ci = getVisualIndex(bendPointIndex);
		return super.getVisualPoint(ci);

	}

	@Override
	public void init() {
		isNormalizationNeeded = false;

		super.init();
	}

	/**
	 * Returns <code>true</code> if the anchor at the given connection index is
	 * explicit. Otherwise returns <code>false</code>.
	 *
	 * @param connectionIndex
	 *            The connection index that specifies the anchor to test.
	 * @return <code>true</code> if the specified anchor is explicit, otherwise
	 *         <code>false</code>.
	 */
	public boolean isExplicit(int connectionIndex) {
		if (getCurve() instanceof Connection) {
			Connection connection = (Connection) getCurve();
			IAnchor anchor = connection.getAnchor(connectionIndex);
			return !connection.getRouter().wasInserted(anchor);
		} else {
			return true;
		}
	}

	/**
	 * Returns true if the first specified anchor overlays the second specified
	 * anchor.
	 *
	 * @param overlayingExplicitAnchorIndex
	 * @param overlainExplicitAnchorIndex
	 * @return
	 */
	private boolean isExplicitOverlay(int overlayingExplicitAnchorIndex,
			int overlainExplicitAnchorIndex) {
		return getVisualPoint(overlayingExplicitAnchorIndex)
				.getDistance(getVisualPoint(
						overlainExplicitAnchorIndex)) <= getOverlayThreshold();
	}

	/**
	 * Whether orthogonal routing is to be used.
	 *
	 * @return <true> if movement is to be constraint in orthogonal directions.
	 */
	public boolean isOrthogonal() {
		boolean isOrtho = getCurve() instanceof Connection
				&& ((Connection) getCurve())
						.getRouter() instanceof OrthogonalRouter;
		return isOrtho;
	}

	private boolean isUnpreciseEquals(double y0, double y1) {
		return Math.abs(y0 - y1) < 1;
	}

	@Override
	protected void locallyExecuteOperation() {
		try {
			getBendOperation().execute(null, null);
		} catch (Exception x) {
			throw new IllegalStateException(x);
		}
	}

	/**
	 * Makes the connection anchor at the given connection index explicit and
	 * returns its explicit index.
	 *
	 * @param connectionIndex
	 *            The connection index to make explicit.
	 * @return The (new) explicit index for the given connection index.
	 */
	public int makeExplicit(int connectionIndex) {
		return makeExplicit(connectionIndex, connectionIndex).get(0);
	}

	/**
	 * Makes the connection anchors within the given range of connection indices
	 * explicit and returns their explicit indices.
	 *
	 * @param startConnectionIndex
	 *            The first connection index to make explicit.
	 * @param endConnectionIndex
	 *            The last connection index to make explicit.
	 * @return A list of explicit anchor indices for the given range of
	 *         connection indices.
	 */
	public List<Integer> makeExplicit(int startConnectionIndex,
			int endConnectionIndex) {
		// new explicit point => normalization needed
		isNormalizationNeeded = true;
		// find the anchor handle before the start index
		List<ImplicitGroup> implicitGroups = new ArrayList<>();
		boolean isStartExplicit = isExplicit(startConnectionIndex);
		implicitGroups.add(new ImplicitGroup(
				getExplicitIndexAtOrBefore(startConnectionIndex)));
		// find implicit groups within the given index range
		for (int i = startConnectionIndex; i <= endConnectionIndex; i++) {
			if (isExplicit(i)) {
				// start a new group
				int explicitAnchorHandle = getExplicitIndexAtOrBefore(i);
				implicitGroups.add(new ImplicitGroup(explicitAnchorHandle));
			} else {
				// add point to current group
				Point pointInLocal = getCurve().getPoint(i);
				Point pointInScene = NodeUtils
						.localToScene(getHost().getVisual(), pointInLocal);
				implicitGroups.get(implicitGroups.size() - 1).points
						.add(pointInScene);
			}
		}
		// remove first group if empty
		if (implicitGroups.get(0).points.isEmpty()) {
			implicitGroups.remove(0);
		}
		// create explicit anchors one by one in reverse order so that the
		// indices are not messed up
		int addedCount = 0;
		List<Integer> handles = new ArrayList<>();
		for (int i = 0; i < implicitGroups.size(); i++) {
			ImplicitGroup ig = implicitGroups.get(i);
			int prec = ig.precedingExplicitIndex + addedCount;
			if (!handles.isEmpty() || isStartExplicit) {
				handles.add(prec);
			}
			for (Point p : ig.points) {
				prec = createAfter(prec, p);
				addedCount++;
				handles.add(prec);
			}
		}
		return handles;
	}

	/**
	 * Moves the currently selected point to the given mouse position in scene
	 * coordinates.
	 *
	 * @param initialMouseInScene
	 *            The initial mouse position in scene coordinates.
	 * @param currentMouseInScene
	 *            The current mouse position in scene coordinates.
	 */
	@Override
	public Point move(Point initialMouseInScene, Point currentMouseInScene) {
		checkInitialized();
		isNormalizationNeeded = true;

		// constrain movement in one direction for segment based connections
		if (isOrthogonal()) {
			Point initialMouseInLocal = NodeUtils
					.sceneToLocal(getHost().getVisual(), initialMouseInScene);
			Point mouseDeltaInLocal = NodeUtils
					.sceneToLocal(getHost().getVisual(), currentMouseInScene)
					.getTranslated(initialMouseInLocal.getNegated());
			if (getOrientation() == Orientation.HORIZONTAL) {
				mouseDeltaInLocal.x = 0;
			} else {
				mouseDeltaInLocal.y = 0;
			}
			Point translatedMouseInScene = NodeUtils.localToScene(
					getHost().getVisual(),
					initialMouseInLocal.getTranslated(mouseDeltaInLocal));
			return super.move(initialMouseInScene, translatedMouseInScene);
		} else {
			return super.move(initialMouseInScene, currentMouseInScene);
		}
	}

	/**
	 * For segment based connections, the control points need to be normalized,
	 * i.e. all control points that lie on the orthogonal connection between two
	 * other control points have to be removed.
	 */
	public void normalize() {
		if (!(getCurve() instanceof Connection)) {
			return;
		}

		Connection connection = (Connection) getCurve();
		if (!(connection.getRouter() instanceof OrthogonalRouter)) {
			return;
		}

		// execute operation so that changes are applied
		locallyExecuteOperation();
		route();

		// determine all connection anchors
		List<IAnchor> anchors = connection.getAnchorsUnmodifiable();

		// determine corresponding positions
		List<Point> positions = connection.getPointsUnmodifiable();

		// test each explicit static anchor for removal potential
		int explicitIndex = 0; // start is explicit
		boolean removed = false;
		for (int i = 1; i < anchors.size() - 1; i++) {
			IAnchor anchor = anchors.get(i);
			if (!connection.getRouter().wasInserted(anchor)) {
				// found an explicit anchor
				explicitIndex++;

				// determine surrounding positions
				Point prev = positions.get(i - 1);
				Point next = positions.get(i + 1);
				Point current = positions.get(i);

				// determine in-direction and out-direction for current
				// point
				Vector inDirection = new Vector(prev, current);
				Vector outDirection = new Vector(current, next);

				if (inDirection.isNull() || outDirection.isNull()
						|| inDirection.isParallelTo(outDirection)) {
					// XXX: Compute previous position in scene coordinates
					// before manipulating the connection.
					Point prevInScene = FX2Geometry
							.toPoint(connection.localToScene(prev.x, prev.y));
					// make previous and next explicit
					if (connection.getRouter()
							.wasInserted(anchors.get(i + 1))) {
						// make next explicit
						makeExplicit(i + 1);
					}
					if (connection.getRouter()
							.wasInserted(anchors.get(i - 1))) {
						// make previous explicit
						// XXX: We need to insert a point manually here and
						// cannot rely on makeExplicit() because the indices
						// could have changed.
						createBefore(explicitIndex, prevInScene);
						explicitIndex++;
					}
					// remove current point as it is unnecessary
					getBendOperation().getFinalBendPoints()
							.remove(explicitIndex);
					// start a new normalization
					removed = true;
					break;
				}
			}
		}

		if (removed) {
			normalize();
		}
	}

	@Override
	protected void removeOverlain() {
		if (getCurve() instanceof Connection
				&& ((Connection) getCurve())
						.getRouter() instanceof OrthogonalRouter
				&& getSelectedIndices().size() == 2) {
			// segment overlay removal for orthogonal connection
			removeOverlainSegments();
		} else {
			// point overlay removal otherwise
			removeOverlainPoints();
		}
		route();
	}

	private void removeOverlainPoints() {
		int explicitAnchorsSize = getBendOperation().getFinalBendPoints()
				.size();
		for (int i = getSelectedIndices().size() - 1; i >= 0
				&& explicitAnchorsSize > 2; i--) {
			int index = getSelectedIndices().get(i);
			// XXX: If an overlay is recognized, the overlaying anchor is
			// removed and practically replaced by the overlain anchor. This
			// might seem unintuitive, however, it enables the user to
			// cleanly remove control points by dragging them onto a neighboring
			// point, without augmenting any other control points.
			boolean isLeftOverlain = index > 0
					&& isExplicitOverlay(index, index - 1);
			boolean isRightOverlain = index < explicitAnchorsSize - 1
					&& isExplicitOverlay(index, index + 1);

			if (isLeftOverlain || isRightOverlain) {
				int overlainIndex = isLeftOverlain ? index - 1 : index + 1;
				if (getSelectedIndices().contains(overlainIndex)) {
					// selected overlays other selected
					// => skip this overlay
					continue;
				}
				// remove from connection
				getBendOperation().getFinalBendPoints().remove(index);
				// apply changes by executing the operation
				locallyExecuteOperation();

			}
		}
	}

	private void removeOverlainSegments() {
		// define indices for segment overlays
		int[][] possibleSegmentOverlays = new int[][] {
				new int[] { -2, -1, 2, 3 }, new int[] { -2, -1, 2 },
				new int[] { -1, 2, 3 }, new int[] { -1, 2 },
				new int[] { -2, -1 }, new int[] { 2, 3 }, new int[] { 2 },
				new int[] { -1 } };

		// test for segment overlays and remove the first segment overlays that
		// can be found
		boolean removed = false;
		for (int i = 0; i < possibleSegmentOverlays.length && !removed; i++) {
			removed = testAndRemoveSegmentOverlay(possibleSegmentOverlays[i]);
		}

		// apply changes (if any)
		if (removed) {
			locallyExecuteOperation();
		}
	}

	@Override
	protected void restorePreMoveBendpoints() {
		super.restorePreMoveBendpoints();
		setNewHints(preMoveStartHint, preMoveEndHint);
		locallyExecuteOperation();
	}

	/**
	 * Provides position hints to the connection's {@link IConnectionRouter} and
	 * let's the router route the connection, so these position hints can be
	 * forwarded to the anchors.
	 */
	protected void route() {
		Point newStartHint = computeStartHint();
		Point newEndHint = computeEndHint();
		setNewHints(newStartHint, newEndHint);
		locallyExecuteOperation();
	}

	/**
	 * Selects the point specified by the given segment index and parameter for
	 * manipulation. Captures the initial position of the selected point and the
	 * related initial mouse location.
	 *
	 * @param explicitAnchorIndex
	 *            Index of the explicit anchor to select for manipulation.
	 */
	@Override
	public void select(int explicitAnchorIndex) {
		checkInitialized();
		super.select(explicitAnchorIndex);

		// after last call to select, the bend points are final
		preMoveStartHint = computeStartHint();
		preMoveEndHint = computeEndHint();
	}

	@Override
	public void selectSegment(int from, int to) {
		// make explicit
		List<Integer> explicit = makeExplicit(from, to);
		super.selectSegment(explicit.get(0), explicit.get(1));
	}

	/**
	 * Updates the positions (hints) for attached bend points.
	 *
	 * @param startHint
	 *            The new start point hint.
	 * @param endHint
	 *            The new end point hint.
	 */
	protected void setNewHints(Point startHint, Point endHint) {
		List<BendPoint> finalBendPoints = getBendOperation()
				.getFinalBendPoints();
		BendPoint bendPoint = finalBendPoints.get(0);
		if (bendPoint.isAttached() && startHint != null) {
			finalBendPoints.set(0,
					new BendPoint(bendPoint.getContentAnchorage(), startHint));
		}
		bendPoint = finalBendPoints.get(finalBendPoints.size() - 1);
		if (bendPoint.isAttached() && endHint != null) {
			finalBendPoints.set(finalBendPoints.size() - 1,
					new BendPoint(bendPoint.getContentAnchorage(), endHint));
		}
	}

	/**
	 * Tests if the current selection complies to the overlay specified by the
	 * given parameters. The <i>overlainPointIndicesRelativeToSelection</i> is
	 * an integer array that specifies the indices (relative to the selected
	 * indices) of all points that are tested to be overlain by the current
	 * selection.
	 * <p>
	 * The points specified by the given indices need to be aligned with the
	 * selection, i.e. they need to be on a vertical or horizontal line. The
	 * first and last indices specify the resulting segment which the selection
	 * snaps to in case of an overlay. If the distance between the resulting
	 * segment and the selected segment is smaller than the
	 * {@link #getOverlayThreshold()}, then all specified points and the
	 * selection are replaced by the result segment.
	 *
	 * @param overlainPointIndicesRelativeToSelection
	 *            An integer array that specifies the indices (relative to the
	 *            selected indices) of all points that are part of this overlay
	 *            in ascending order, excluding the selected indices.
	 * @return <code>true</code> if the overlay was removed, otherwise
	 *         <code>false</code>.
	 */
	private boolean testAndRemoveSegmentOverlay(
			int[] overlainPointIndicesRelativeToSelection) {

		int numPoints = getSelectedIndices().size();
		boolean isSelectionHorizontal = numPoints == 2 && isOrthogonal()
				&& getOrientation() == Orientation.HORIZONTAL;

		// check that positions are present for the given indices within the
		// connection. if not all are present, return without applying any
		// modifications.
		List<Point> points = Arrays.asList(Point.getCopy(
				getCurve().getPointsUnmodifiable().toArray(new Point[] {})));
		int firstIndex = overlainPointIndicesRelativeToSelection[0];
		int lastIndex = overlainPointIndicesRelativeToSelection[overlainPointIndicesRelativeToSelection.length
				- 1];
		int selectionStartIndexInConnection = getVisualIndex(
				getSelectedIndices().get(0));
		if (selectionStartIndexInConnection + firstIndex < 0
				|| selectionStartIndexInConnection + firstIndex >= points
						.size()) {
			return false;
		}
		if (selectionStartIndexInConnection + lastIndex < 0
				|| selectionStartIndexInConnection + lastIndex >= points
						.size()) {
			return false;
		}

		// evaluate positions for the given indices
		List<Point> overlainPoints = new ArrayList<>();
		for (int i = 0; i < overlainPointIndicesRelativeToSelection.length; i++) {
			overlainPoints.add(points.get(selectionStartIndexInConnection
					+ overlainPointIndicesRelativeToSelection[i]));
		}

		// determine segment positions (relative to their orientations). if not
		// all segments have the same position, return without applying any
		// modifications.
		double p = isSelectionHorizontal ? overlainPoints.get(0).y
				: overlainPoints.get(0).x;

		for (int i = 1; i < overlainPoints.size(); i++) {
			Point q = overlainPoints.get(i);
			if (isSelectionHorizontal && !isUnpreciseEquals(p, q.y)
					|| !isSelectionHorizontal && !isUnpreciseEquals(p, q.x)) {
				// wrong orientation
				return false;
			}
			if (isSelectionHorizontal && !isUnpreciseEquals(p, q.y)
					|| !isSelectionHorizontal && !isUnpreciseEquals(p, q.x)) {
				// wrong position
				return false;
			}
		}

		// compute the (provisional) resulting segment from the given overlain
		// indices. the first index is the start index for the result, the last
		// index is the end index for the result.
		Point resultStart = overlainPoints.get(0);
		Point resultEnd = overlainPoints.get(overlainPoints.size() - 1);

		// compute the distance between the selected segment and the overlain
		// result segment. if the distance is above the removal threshold,
		// return without applying any modifications.
		Point selectionStart = points.get(selectionStartIndexInConnection);
		Point selectionEnd = points.get(selectionStartIndexInConnection + 1);
		double distance = Math
				.abs(isSelectionHorizontal ? resultStart.y - selectionStart.y
						: resultStart.x - selectionStart.x);
		if (distance > getOverlayThreshold()) {
			return false;
		}

		// at this point, the overlay is confirmed and needs to be removed.
		// therefore, the overlap of selection and result needs to be removed
		// and their difference needs to be saved as the final result
		if (overlainPointIndicesRelativeToSelection.length <= 2) {
			if (isSelectionHorizontal) {
				// same y values => adjust x
				if (firstIndex < 0) {
					resultEnd.x = selectionEnd.x;
				} else {
					resultStart.x = selectionStart.x;
				}
			} else {
				// same x values => adjust y
				if (firstIndex < 0) {
					resultEnd.y = selectionEnd.y;
				} else {
					resultStart.y = selectionStart.y;
				}
			}
		}

		// make the result segment explicit
		int overlayStartIndex = Math.min(selectionStartIndexInConnection,
				selectionStartIndexInConnection + firstIndex);
		int overlayEndIndex = Math.max(selectionStartIndexInConnection + 1,
				selectionStartIndexInConnection + lastIndex);

		List<Integer> explicit = makeExplicit(overlayStartIndex,
				overlayEndIndex);

		// remove the selection and the other overlain anchors
		int removedCount = 0;
		for (int i = explicit.size() - 2; i >= 1; i--) {
			getBendOperation().getFinalBendPoints()
					.remove((int) explicit.get(i));
			removedCount++;
		}

		// overwrite the first and last explicit anchor with a new unconnected
		// anchor for the adjusted result position if the respective anchor is
		// currently unconnected and neither the start nor the end point
		Integer resultStartIndex = explicit.get(0);
		BendPoint resultStartAnchor = getBendOperation().getFinalBendPoints()
				.get(resultStartIndex);
		if (resultStartIndex > 0 && !resultStartAnchor.isAttached()) {
			getBendOperation().getFinalBendPoints().set(resultStartIndex,
					new BendPoint(resultStart));
		}

		Integer resultEndIndex = explicit.get(explicit.size() - 1)
				- removedCount;
		BendPoint resultEndAnchor = getBendOperation().getFinalBendPoints()
				.get(resultEndIndex);
		if (resultEndIndex < getBendOperation().getFinalBendPoints().size() - 1
				&& !resultEndAnchor.isAttached()) {
			getBendOperation().getFinalBendPoints().set(resultEndIndex,
					new BendPoint(resultEnd));
		}

		return true;
	}

	@Override
	public String toString() {
		return "BendConnectionPolicy[host=" + getHost() + "]";
	}

}
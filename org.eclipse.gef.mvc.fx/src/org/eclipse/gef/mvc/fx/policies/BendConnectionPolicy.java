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
package org.eclipse.gef.mvc.fx.policies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.IConnectionRouter;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.models.GridModel;
import org.eclipse.gef.mvc.fx.operations.AbstractCompositeOperation;
import org.eclipse.gef.mvc.fx.operations.BendContentOperation;
import org.eclipse.gef.mvc.fx.operations.BendVisualOperation;
import org.eclipse.gef.mvc.fx.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart.BendPoint;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.providers.IAnchorProvider;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.inject.Provider;

import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * The {@link BendConnectionPolicy} can be used to manipulate the points
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
public class BendConnectionPolicy extends AbstractPolicy {

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

	/**
	 * The overlay threshold, i.e. the distance between two points so that they
	 * are regarded as overlying.
	 */
	protected static final double DEFAULT_OVERLAY_THRESHOLD = 10;

	/**
	 * The overlay threshold, i.e. the distance between two points so that they
	 * are regarded as overlying.
	 */
	protected static final double DEFAULT_SEGMENT_OVERLAY_THRESHOLD = 6;

	private List<Point> selectedInitialPositions = new ArrayList<>();
	private Point preMoveStartHint = null;
	private Point preMoveEndHint = null;
	private boolean isSelectionHorizontal = false;
	// TODO: remove usePreMoveHints
	private boolean usePreMoveHints = false;
	private boolean isNormalizationNeeded = false;

	private List<BendPoint> initialBendPoints = new ArrayList<>();
	private List<BendPoint> preMoveBendPoints = new ArrayList<>();
	private List<Integer> selectedIndices = new ArrayList<>();

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

		ITransactionalOperation commitOperation = super.commit();
		if (commitOperation != null && !commitOperation.isNoOp()
				&& getHost() instanceof IBendableContentPart) {
			// chain content changes
			// unconnected control points
			ForwardUndoCompositeOperation composite = new ForwardUndoCompositeOperation(
					"Bend Content");
			composite.add(commitOperation);
			BendContentOperation resizeOperation = new BendContentOperation(
					getHost(), getInitialBendPoints(), getCurrentBendPoints());
			composite.add(resizeOperation);

			commitOperation = composite;
		}

		// clear state
		initialBendPoints = null;

		return commitOperation;
	}

	private Point computeEndHint() {
		if (getConnection().getEndAnchor() instanceof DynamicAnchor
				&& getConnection().getPointsUnmodifiable().size() > 1) {
			Point endPoint = getConnection().getEndPoint();
			Point neighbor = getConnection().getPoint(
					getConnection().getPointsUnmodifiable().size() - 2);
			Point translated = endPoint.getTranslated(
					endPoint.getDifference(neighbor).getScaled(0.5));
			return translated;
		}
		return null;
	}

	private Point computeStartHint() {
		if (getConnection().getStartAnchor() instanceof DynamicAnchor
				&& getConnection().getPointsUnmodifiable().size() > 1) {
			Point startPoint = getConnection().getStartPoint();
			Point neighbor = getConnection().getPoint(1);
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
	public int createAfter(int explicitAnchorIndex, Point mouseInScene) {
		checkInitialized();
		// create point => normalization needed after commit
		isNormalizationNeeded = true;
		// determine insertion index
		int insertionIndex = explicitAnchorIndex + 1;
		// insert new anchor
		insertExplicitAnchor(insertionIndex, mouseInScene);
		// return handle to newly created anchor
		return insertionIndex;
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
	public int createBefore(int explicitAnchorIndex, Point mouseInScene) {
		checkInitialized();
		// create point => normalization needed after commit
		isNormalizationNeeded = true;
		// determine insertion index
		int insertionIndex = explicitAnchorIndex;
		// insert new anchor
		insertExplicitAnchor(insertionIndex, mouseInScene);
		// return handle to newly created anchor
		return insertionIndex;
	}

	@Override
	protected ITransactionalOperation createOperation() {
		ForwardUndoCompositeOperation fwdOp = new ForwardUndoCompositeOperation(
				"BendPlusHints");
		fwdOp.add(new BendVisualOperation(getHost()));
		return fwdOp;
	}

	/**
	 * Determines the {@link BendPoint} that should replace the anchor of the
	 * currently selected point. If the point can connect, the
	 * {@link IVisualPart} at the mouse position is queried for an
	 * {@link BendPoint} via a {@link Provider}&lt;{@link BendPoint}&gt;
	 * adapter. Otherwise an (unconnected) anchor is create using
	 * {@link #createUnconnectedAnchor(Point)} .
	 *
	 * @param explicitAnchorIndex
	 *            The explicit anchor index for which to determine the anchor.
	 * @param positionInLocal
	 *            A position in local coordinates of the connection.
	 * @param canConnect
	 *            <code>true</code> if the point can be attached to an
	 *            underlying {@link IVisualPart}, otherwise <code>false</code>.
	 * @return The {@link BendPoint} that replaces the anchor of the currently
	 *         modified point.
	 */
	private BendPoint findOrCreateAnchor(int explicitAnchorIndex,
			Point positionInLocal, boolean canConnect) {
		BendPoint anchor = null;
		// try to find an anchor that is provided from an underlying node
		if (canConnect) {
			Point selectedPointCurrentPositionInScene = FX2Geometry
					.toPoint(getConnection().localToScene(
							Geometry2FX.toFXPoint(positionInLocal)));
			List<Node> pickedNodes = NodeUtils.getNodesAt(
					getHost().getRoot().getVisual(),
					selectedPointCurrentPositionInScene.x,
					selectedPointCurrentPositionInScene.y);
			anchor = getCompatibleAnchor(explicitAnchorIndex,
					getParts(pickedNodes), selectedPointCurrentPositionInScene);
		}
		if (anchor == null) {
			anchor = new BendPoint(positionInLocal);
		}
		return anchor;
	}

	/**
	 * Returns an {@link BendVisualOperation} that is extracted from the
	 * operation created by {@link #createOperation()}.
	 *
	 * @return an {@link BendVisualOperation} that is extracted from the
	 *         operation created by {@link #createOperation()}.
	 */
	protected BendVisualOperation getBendOperation() {
		return (BendVisualOperation) ((AbstractCompositeOperation) super.getOperation())
				.getOperations().get(0);
	}

	private BendPoint getCompatibleAnchor(int explicitAnchorIndex,
			List<IContentPart<? extends Node>> partsUnderMouse,
			Point positionInScene) {
		for (IContentPart<? extends Node> part : partsUnderMouse) {
			if (part == getHost()) {
				continue;
			}
			IAnchorProvider anchorProvider = part
					.getAdapter(IAnchorProvider.class);
			if (anchorProvider != null) {
				return new BendPoint(part.getContent(), positionInScene);
			}
		}
		return null;
	}

	/**
	 * Returns the {@link Connection} that is manipulated by this policy.
	 *
	 * @return The {@link Connection} that is manipulated by this policy.
	 */
	protected Connection getConnection() {
		return getHost().getVisual();
	}

	private int getConnectionIndex(Integer bendPointIndex) {
		Connection connection = getHost().getVisual();
		IConnectionRouter router = connection.getRouter();
		ObservableList<IAnchor> anchors = connection.getAnchorsUnmodifiable();
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
	}

	/**
	 * Returns the current control points of the content.
	 *
	 * @return The current control points.
	 */
	protected List<BendPoint> getCurrentBendPoints() {
		return getHost().getVisualBendPoints();
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
		List<BendPoint> bpoints = getHost().getVisualBendPoints();
		List<IAnchor> anchors = getConnection().getAnchorsUnmodifiable();
		IConnectionRouter router = getConnection().getRouter();

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

	@SuppressWarnings("unchecked")
	@Override
	public IBendableContentPart<Connection> getHost() {
		return (IBendableContentPart<Connection>) super.getHost();
	}

	/**
	 * Returns the initial bend points before bending the content.
	 *
	 * @return The initial bend points.
	 */
	protected List<BendPoint> getInitialBendPoints() {
		return initialBendPoints;
	}

	/**
	 * Removes the overlay threshold, i.e. the distance between two points, so
	 * that they are regarded as overlaying. When the background grid is enables
	 * ( {@link GridModel#isShowGrid()}, then the grid cell size is used to
	 * determine the overlay threshold. Otherwise, the
	 * {@link #DEFAULT_OVERLAY_THRESHOLD} is used.
	 *
	 * @return The overlay threshold.
	 */
	protected double getOverlayThreshold() {
		// TODO: respect snapping (grid cell size, snapping distances, etc.)
		if (getConnection().getRouter() instanceof OrthogonalRouter
				&& selectedIndices.size() == 2) {
			return DEFAULT_SEGMENT_OVERLAY_THRESHOLD;
		}
		// fallback to default
		return DEFAULT_OVERLAY_THRESHOLD;
	}

	private List<IContentPart<? extends Node>> getParts(
			List<Node> nodesUnderMouse) {
		List<IContentPart<? extends Node>> parts = new ArrayList<>();

		IViewer viewer = getHost().getRoot().getViewer();
		for (Node node : nodesUnderMouse) {
			IVisualPart<? extends Node> part = PartUtils
					.retrieveVisualPart(viewer, node);
			if (part instanceof IContentPart) {
				parts.add((IContentPart<? extends Node>) part);
			}
		}
		return parts;
	}

	/**
	 * Returns the current position for the given explicit anchor index, within
	 * the local coordinate system of the {@link Connection}.
	 *
	 * @param explicitAnchorIndex
	 *            The index
	 * @return The {@link Point} in local {@link Connection} coordinates.
	 */
	private Point getPoint(int explicitAnchorIndex) {
		Connection connection = getHost().getVisual();
		int ci = getConnectionIndex(explicitAnchorIndex);
		return connection.getPoint(ci);
	}

	/**
	 * Returns the initial positions of the selected points in the local
	 * coordinate system of the {@link #getConnection()}. May be
	 * <code>null</code> prior to the first {@link #move(Point, Point)} call.
	 *
	 * @return The initial positions of the selected points in the local
	 *         coordinate system of the {@link #getConnection()}.
	 */
	public List<Point> getSelectedInitialPositions() {
		return selectedInitialPositions;
	}

	@Override
	public void init() {
		selectedIndices.clear();
		selectedInitialPositions.clear();
		preMoveBendPoints.clear();
		usePreMoveHints = true;
		isNormalizationNeeded = false;
		super.init();
		// showAnchors("init:");
		initialBendPoints = getCurrentBendPoints();
		preMoveStartHint = getHost().getVisual().getStartPointHint();
		preMoveEndHint = getHost().getVisual().getEndPointHint();
	}

	/**
	 * Creates a new static anchor for the given position and inserts it at the
	 * given index.
	 *
	 * @param insertionIndex
	 *            The explicit anchor index at which the new anchor is inserted.
	 * @param mouseInScene
	 *            The position for the new anchor in scene coordinates.
	 */
	protected void insertExplicitAnchor(int insertionIndex,
			Point mouseInScene) {
		// convert position to local coordinates
		Point mouseInLocal = FX2Geometry.toPoint(getConnection()
				.sceneToLocal(Geometry2FX.toFXPoint(mouseInScene)));
		getBendOperation().getFinalBendPoints().add(insertionIndex,
				new BendPoint(mouseInLocal));
		locallyExecuteOperation();
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
		IAnchor anchor = getConnection().getAnchor(connectionIndex);
		return !getConnection().getRouter().wasInserted(anchor);
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
		return getPoint(overlayingExplicitAnchorIndex).getDistance(
				getPoint(overlainExplicitAnchorIndex)) <= getOverlayThreshold();
	}

	/**
	 * Returns <code>true</code> if the selected points are on a horizontal
	 * line. Otherwise returns <code>false</code>.
	 *
	 * @return <code>true</code> if the selected points are on a horizontal
	 *         line, otherwise <code>false</code>.
	 */
	public boolean isSelectionHorizontal() {
		return isSelectionHorizontal;
	}

	private boolean isUnpreciseEquals(double y0, double y1) {
		return Math.abs(y0 - y1) < 1;
	}

	@Override
	protected void locallyExecuteOperation() {
		// locally execute bend operation
		try {
			getBendOperation().execute(null, null);
		} catch (Exception x) {
			throw new IllegalStateException(x);
		}
		// apply hints
		if (usePreMoveHints) {
			setNewHints(preMoveStartHint, preMoveEndHint);
		} else {
			Point newStartHint = computeStartHint();
			Point newEndHint = computeEndHint();
			setNewHints(newStartHint, newEndHint);
		}
		// locally execute hints operation
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
				Point pointInLocal = getConnection().getPoint(i);
				Point pointInScene = FX2Geometry.toPoint(getConnection()
						.localToScene(Geometry2FX.toFXPoint(pointInLocal)));
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
	public void move(Point initialMouseInScene, Point currentMouseInScene) {
		checkInitialized();
		// showAnchors("Before Restore:");

		// determine selection status
		int numPoints = selectedIndices.size();
		boolean isOrtho = numPoints == 2
				&& getConnection().getRouter() instanceof OrthogonalRouter;

		// save/restore explicit anchors
		if (preMoveBendPoints.isEmpty()) {
			// first move => we need to normalize upon commit now
			isNormalizationNeeded = true;
			usePreMoveHints = false;
			// save initial selected positions
			for (int i = 0; i < selectedIndices.size(); i++) {
				selectedInitialPositions.add(i,
						getPoint(selectedIndices.get(i)));
			}
			// save initial pre-move explicit anchors
			preMoveBendPoints.addAll(getBendOperation().getFinalBendPoints());
			// determine selection segment orientation
			if (isOrtho) {
				double y0 = selectedInitialPositions.get(0).y;
				double y1 = selectedInitialPositions.get(1).y;
				isSelectionHorizontal = isUnpreciseEquals(y0, y1);
			}
			// save initial pre-move hints
			preMoveStartHint = computeStartHint();
			preMoveEndHint = computeEndHint();
		} else {
			// restore initial pre-move explicit anchors
			getBendOperation().setFinalBendPoints(preMoveBendPoints);
			// restore initial pre-move hints
			setNewHints(preMoveStartHint, preMoveEndHint);
			usePreMoveHints = true;
			locallyExecuteOperation();
			usePreMoveHints = false;
		}
		// showAnchors("After Restore:");

		// compensate the movement of the local coordinate system w.r.t. the
		// scene coordinate system (the scene coordinate system stays consistent
		// w.r.t. mouse movement)
		Point mouseDeltaInLocal = FX2Geometry
				.toPoint(getConnection().sceneToLocal(
						Geometry2FX.toFXPoint(currentMouseInScene)))
				.getTranslated(FX2Geometry
						.toPoint(getConnection().sceneToLocal(
								Geometry2FX.toFXPoint(initialMouseInScene)))
						.getNegated());

		// constrain movement in one direction for segment based connections
		if (isOrtho) {
			if (isSelectionHorizontal) {
				mouseDeltaInLocal.x = 0;
			} else {
				mouseDeltaInLocal.y = 0;
			}
		}

		// update positions
		for (int i = 0; i < selectedIndices.size(); i++) {
			Point selectedPointCurrentPositionInLocal = selectedInitialPositions
					.get(i).getTranslated(mouseDeltaInLocal);

			int explicitAnchorIndex = selectedIndices.get(i);
			boolean canConnect = canConnect(explicitAnchorIndex);

			// update anchor
			getBendOperation().getFinalBendPoints().set(explicitAnchorIndex,
					findOrCreateAnchor(explicitAnchorIndex,
							selectedPointCurrentPositionInLocal, canConnect));
		}
		locallyExecuteOperation();
		// showAnchors("After Move:");

		// remove overlain
		removeOverlain();
		// showAnchors("After RemoveOverlain:");
	}

	/**
	 * For segment based connections, the control points need to be normalized,
	 * i.e. all control points that lie on the orthogonal connection between two
	 * other control points have to be removed.
	 */
	public void normalize() {
		if (!(getConnection().getRouter() instanceof OrthogonalRouter)) {
			return;
		}

		// enable hint computation
		usePreMoveHints = false;

		// execute operation so that changes are applied
		locallyExecuteOperation();

		// determine all connection anchors
		List<IAnchor> anchors = getConnection().getAnchorsUnmodifiable();

		// determine corresponding positions
		List<Point> positions = getConnection().getPointsUnmodifiable();

		// test each explicit static anchor for removal potential
		int explicitIndex = 0; // start is explicit
		boolean removed = false;
		for (int i = 1; i < anchors.size() - 1; i++) {
			IAnchor anchor = anchors.get(i);
			if (!getConnection().getRouter().wasInserted(anchor)) {
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
					Point prevInScene = FX2Geometry.toPoint(
							getConnection().localToScene(prev.x, prev.y));
					// make previous and next explicit
					if (getConnection().getRouter()
							.wasInserted(anchors.get(i + 1))) {
						// make next explicit
						makeExplicit(i + 1);
					}
					if (getConnection().getRouter()
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

	private void removeOverlain() {
		if (getConnection().getRouter() instanceof OrthogonalRouter
				&& selectedIndices.size() == 2) {
			// segment overlay removal for orthogonal connection
			removeOverlainSegments();
		} else {
			// point overlay removal otherwise
			removeOverlainPoints();
		}
	}

	private void removeOverlainPoints() {
		int explicitAnchorsSize = getBendOperation().getFinalBendPoints()
				.size();
		for (int i = selectedIndices.size() - 1; i >= 0
				&& explicitAnchorsSize > 2; i--) {
			int index = selectedIndices.get(i);
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
				if (selectedIndices.contains(overlainIndex)) {
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

	/**
	 * Provides position hints to the connection's {@link IConnectionRouter} and
	 * let's the router route the connection, so these position hints can be
	 * forwarded to the anchors.
	 */
	protected void route() {
	}

	/**
	 * Selects the point specified by the given segment index and parameter for
	 * manipulation. Captures the initial position of the selected point and the
	 * related initial mouse location.
	 *
	 * @param explicitAnchorIndex
	 *            Index of the explicit anchor to select for manipulation.
	 */
	public void select(int explicitAnchorIndex) {
		checkInitialized();
		// save selected anchor handles
		selectedIndices.add(explicitAnchorIndex);
	}

	/**
	 * Selects the end points of the connection segment specified by the given
	 * index. Makes the corresponding anchors explicit first and copies them if
	 * they are connected.
	 *
	 * @param segmentIndex
	 *            The index of a connection segment.
	 */
	public void selectSegment(int segmentIndex) {
		// determine indices of neighbor anchors
		int firstSegmentIndex = segmentIndex;
		int secondSegmentIndex = segmentIndex + 1;

		// determine connectedness for neighbor anchors
		Node firstAnchorage = getConnection().getAnchor(firstSegmentIndex)
				.getAnchorage();
		boolean isFirstConnected = firstAnchorage != null
				&& firstAnchorage != getConnection();
		Node secondAnchorage = getConnection().getAnchor(secondSegmentIndex)
				.getAnchorage();
		boolean isSecondConnected = secondAnchorage != null
				&& secondAnchorage != getConnection();

		// make explicit
		List<Integer> explicit = makeExplicit(firstSegmentIndex,
				secondSegmentIndex);
		int firstAnchorHandle = explicit.get(0);
		int secondAnchorHandle = explicit.get(1);

		// create unconnected copies of the segment anchors if they are
		// connected
		if (isFirstConnected) {
			firstAnchorHandle = createAfter(firstAnchorHandle,
					FX2Geometry.toPoint(getConnection().localToScene(Geometry2FX
							.toFXPoint(getPoint(firstAnchorHandle)))));
			// XXX: increase index of second anchor because one anchor was
			// inserted before it
			secondAnchorHandle++;
		}
		if (isSecondConnected) {
			secondAnchorHandle = createBefore(secondAnchorHandle,
					FX2Geometry.toPoint(getConnection().localToScene(Geometry2FX
							.toFXPoint(getPoint(secondAnchorHandle)))));
		}

		// select the end anchors for manipulation
		select(firstAnchorHandle);
		select(secondAnchorHandle);
	}

	// private void showAnchors(String message) {
	// List<BendPoint> newAnchors = getBendOperation().getFinalBendPoints();
	// String anchorsString = "";
	// for (int i = 0, j = 0; i < getConnection().getAnchorsUnmodifiable()
	// .size(); i++) {
	// BendPoint anchor = getConnection().getAnchor(i);
	// if (getConnection().getRouter().wasInserted(anchor)) {
	// anchorsString = anchorsString + " - "
	// + anchor.getClass().toString() + "["
	// + getConnection().getPoint(i) + "],\n";
	// } else {
	// anchorsString = anchorsString
	// + (selectedIndices.contains(j) ? "(*)"
	// : " * ")
	// + anchor.getClass().toString() + "["
	// + getConnection().getPoint(i) + " :: "
	// + NodeUtils.localToScene(getConnection(),
	// getConnection().getPoint(i))
	// + "]" + " (" + newAnchors.get(j) + "),\n";
	// j++;
	// }
	// }
	// System.out.println(message + "\n" + anchorsString);
	// }

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
		// check that positions are present for the given indices within the
		// connection. if not all are present, return without applying any
		// modifications.
		List<Point> points = Arrays.asList(Point.getCopy(getConnection()
				.getPointsUnmodifiable().toArray(new Point[] {})));
		int firstIndex = overlainPointIndicesRelativeToSelection[0];
		int lastIndex = overlainPointIndicesRelativeToSelection[overlainPointIndicesRelativeToSelection.length
				- 1];
		int selectionStartIndexInConnection = getConnectionIndex(
				selectedIndices.get(0));
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

		// System.out.println("same coordinate = " + p);

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

		// System.out.println("=== Segment Overlay ===");
		// System.out.println("selection: " + selectedIndices);
		// System.out.println(
		// "overlain: " + toList(overlainPointIndicesRelativeToSelection));
		// System.out.println("overlain points: " + overlainPoints);
		// System.out.println(
		// "selection line: " + selectionStart + " -> " + selectionEnd);
		// System.out.println("result line: " + resultStart + " -> " +
		// resultEnd);
		// System.out.println("distance: " + distance);

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

		// System.out.println("result: " + resultStart + " -> " + resultEnd);

		// make the result segment explicit
		int overlayStartIndex = Math.min(selectionStartIndexInConnection,
				selectionStartIndexInConnection + firstIndex);
		int overlayEndIndex = Math.max(selectionStartIndexInConnection + 1,
				selectionStartIndexInConnection + lastIndex);

		List<Integer> explicit = makeExplicit(overlayStartIndex,
				overlayEndIndex);
		// showAnchors("After makeExplicit:");

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
			// System.out.println(
			// "Insert unconnected result start at " + resultStartIndex);
			getBendOperation().getFinalBendPoints().set(resultStartIndex,
					new BendPoint(resultStart));
		}

		Integer resultEndIndex = explicit.get(explicit.size() - 1)
				- removedCount;
		BendPoint resultEndAnchor = getBendOperation().getFinalBendPoints()
				.get(resultEndIndex);
		if (resultEndIndex < getBendOperation().getFinalBendPoints().size() - 1
				&& !resultEndAnchor.isAttached()) {
			// System.out.println(
			// "Insert unconnected result end at " + resultEndIndex);
			getBendOperation().getFinalBendPoints().set(resultEndIndex,
					new BendPoint(resultEnd));
		}

		return true;
	}

	// private List<Integer> toList(int[] array) {
	// List<Integer> list = new ArrayList<>();
	// for (int item : array) {
	// list.add(item);
	// }
	// return list;
	// }

	@Override
	public String toString() {
		return "BendConnectionPolicy[host=" + getHost() + "]";
	}

}
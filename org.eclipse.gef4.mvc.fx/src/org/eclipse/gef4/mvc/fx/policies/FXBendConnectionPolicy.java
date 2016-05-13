/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.fx.anchors.DynamicAnchor;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.anchors.StaticAnchor;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.IConnectionRouter;
import org.eclipse.gef4.fx.nodes.OrthogonalRouter;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXBendConnectionOperation;
import org.eclipse.gef4.mvc.fx.operations.FXUpdateAnchorHintsOperation;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.fx.providers.IAnchorProvider;
import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.operations.DeselectOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.SelectOperation;
import org.eclipse.gef4.mvc.parts.IBendableContentPart.BendPoint;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractBendPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.Node;

/**
 * The {@link FXBendConnectionPolicy} can be used to manipulate the points
 * constituting an {@link Connection}, i.e. its start, way, and end points. Each
 * point is realized though an {@link IAnchor}, which may either be local to the
 * {@link Connection} (i.e. the anchor refers to the {@link Connection} as
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
public class FXBendConnectionPolicy extends AbstractBendPolicy<Node> {

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

	/**
	 * Retrieves the content element represented by the anchor's anchorage.
	 *
	 * @param viewer
	 *            The viewer to find the content part in.
	 * @param anchor
	 *            The anchor whose anchorage is to be evaluated.
	 * @return The content element, or <code>null</code> if none could be
	 *         retrieved.
	 */
	static Object getAnchorageContent(IViewer<Node> viewer, IAnchor anchor) {
		Node anchorageNode = anchor.getAnchorage();
		IVisualPart<Node, ? extends Node> part = FXPartUtils
				.retrieveVisualPart(viewer, anchorageNode);
		if (part instanceof IContentPart) {
			return ((IContentPart<Node, ? extends Node>) part).getContent();
		}
		return null;
	}

	/**
	 * Retrieves the current bend points of the connection, which include the
	 * start and end points, as well as the control points.
	 *
	 * @param connectionPart
	 *            The connection part whose bend points to infer.
	 * @return The list of bend points.
	 */
	// TODO (bug #493515): Migrate into an operation of IBendableContentPart
	static List<BendPoint> getCurrentBendPoints(
			IVisualPart<Node, ? extends Connection> connectionPart) {
		List<BendPoint> bendPoints = new ArrayList<>();
		Connection connection = connectionPart.getVisual();
		List<IAnchor> anchors = connection.getAnchorsUnmodifiable();
		for (int i = 0; i < anchors.size(); i++) {
			IAnchor a = anchors.get(i);
			if (!connection.getRouter().wasInserted(a)) {
				if (connection.isConnected(i)) {
					// provide a position hint for a connected bend point
					Point positionHint = connection.getPoint(i);
					if (i == 0 && connection.getStartPointHint() != null) {
						positionHint = connection.getStartPointHint();
					}
					if (i == anchors.size() - 1
							&& connection.getEndPointHint() != null) {
						positionHint = connection.getEndPointHint();
					}
					bendPoints.add(new BendPoint(
							FXBendConnectionPolicy.getAnchorageContent(
									connectionPart.getRoot().getViewer(), a),
							positionHint));
				} else {
					bendPoints.add(new BendPoint(connection.getPoint(i)));
				}
			}
		}
		return bendPoints;
	}

	private List<Integer> selectedExplicitAnchorIndices = new ArrayList<>();
	private List<Point> selectedInitialPositions = new ArrayList<>();
	private List<IAnchor> preMoveExplicitAnchors = new ArrayList<>();
	private Point preMoveStartHint = null;
	private Point preMoveEndHint = null;
	private boolean isSelectionHorizontal = false;
	// TODO: remove usePreMoveHints
	private boolean usePreMoveHints = false;

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
				|| explicitAnchorIndex == getBendOperation().getNewAnchors()
						.size() - 1;
	}

	@Override
	public ITransactionalOperation commit() {
		// showAnchors("pre-norm:");
		normalize();
		// showAnchors("commit:");

		ITransactionalOperation commit = super.commit();
		if (commit == null || commit.isNoOp()) {
			return null;
		}

		return commit;
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
		fwdOp.add(new FXBendConnectionOperation(getConnection()));
		fwdOp.add(new FXUpdateAnchorHintsOperation(getConnection()));
		return fwdOp;
	}

	/**
	 * Create an {@link IUndoableOperation} to re-select the host part.
	 *
	 * @return An {@link IUndoableOperation} that deselects and selects the root
	 *         part.
	 */
	@SuppressWarnings("serial")
	protected ReverseUndoCompositeOperation createReselectOperation() {
		if (!(getHost() instanceof IContentPart) || !(getHost().getRoot()
				.getViewer().getAdapter(new TypeToken<SelectionModel<Node>>() {
				})
				.isSelected((IContentPart<Node, ? extends Node>) getHost()))) {
			return null;
		}

		// assemble deselect and select operations to form a reselect
		ReverseUndoCompositeOperation reselectOperation = new ReverseUndoCompositeOperation(
				"re-select");

		// build "deselect host" operation
		IViewer<Node> viewer = getHost().getRoot().getViewer();
		DeselectOperation<Node> deselectOperation = new DeselectOperation<>(
				viewer, Collections.singletonList(
						(IContentPart<Node, Connection>) getHost()));

		// build "select host" operation
		SelectOperation<Node> selectOperation = new SelectOperation<>(viewer,
				Collections.singletonList(
						(IContentPart<Node, Connection>) getHost()));

		reselectOperation.add(deselectOperation);
		reselectOperation.add(selectOperation);
		return reselectOperation;
	}

	/**
	 * Creates an (unconnected) anchor (i.e. one anchored on the
	 * {@link Connection}) for the given position (in scene coordinates).
	 *
	 * @param selectedPointCurrentPositionInLocal
	 *            The location in local coordinates of the connection
	 * @return An {@link IAnchor} that yields the given position.
	 */
	protected IAnchor createUnconnectedAnchor(
			Point selectedPointCurrentPositionInLocal) {
		return new StaticAnchor(getConnection(),
				selectedPointCurrentPositionInLocal);
	}

	/**
	 * Determines the {@link IAnchor} that should replace the anchor of the
	 * currently selected point. If the point can connect, the
	 * {@link IVisualPart} at the mouse position is queried for an
	 * {@link IAnchor} via a {@link Provider}&lt;{@link IAnchor}&gt; adapter.
	 * Otherwise an (unconnected) anchor is create using
	 * {@link #createUnconnectedAnchor(Point)} .
	 *
	 * @param positionInLocal
	 *            A position in local coordinates of the connection.
	 * @param canConnect
	 *            <code>true</code> if the point can be attached to an
	 *            underlying {@link IVisualPart}, otherwise <code>false</code>.
	 * @return The {@link IAnchor} that replaces the anchor of the currently
	 *         modified point.
	 */
	protected IAnchor findOrCreateAnchor(Point positionInLocal,
			boolean canConnect) {
		IAnchor anchor = null;
		// try to find an anchor that is provided from an underlying node
		if (canConnect) {
			Point selectedPointCurrentPositionInScene = FX2Geometry
					.toPoint(getConnection().localToScene(
							Geometry2FX.toFXPoint(positionInLocal)));
			List<Node> pickedNodes = NodeUtils.getNodesAt(
					getHost().getRoot().getVisual(),
					selectedPointCurrentPositionInScene.x,
					selectedPointCurrentPositionInScene.y);
			anchor = getCompatibleAnchor(getParts(pickedNodes));
		}
		if (anchor == null) {
			anchor = createUnconnectedAnchor(positionInLocal);
		}
		return anchor;
	}

	/**
	 * Returns an {@link FXBendConnectionOperation} that is extracted from the
	 * operation created by {@link #createOperation()}.
	 *
	 * @return an {@link FXBendConnectionOperation} that is extracted from the
	 *         operation created by {@link #createOperation()}.
	 */
	protected FXBendConnectionOperation getBendOperation() {
		return (FXBendConnectionOperation) ((AbstractCompositeOperation) super.getOperation())
				.getOperations().get(0);
	}

	private IAnchor getCompatibleAnchor(
			List<IContentPart<Node, ? extends Node>> partsUnderMouse) {
		for (IContentPart<Node, ? extends Node> part : partsUnderMouse) {
			if (part == getHost()) {
				continue;
			}
			// TODO: this is not correct; we should not hard-code the compatible
			// anchor via the computation strategy.
			IAnchorProvider anchorProvider = part
					.getAdapter(IAnchorProvider.class);
			if (anchorProvider != null) {
				IAnchor anchor = anchorProvider.get(getHost());
				if (anchor != null) {
					return anchor;
				}
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

	@Override
	protected List<BendPoint> getCurrentBendPoints() {
		return getCurrentBendPoints(getHost());
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
		List<IAnchor> anchors = getConnection().getAnchorsUnmodifiable();
		IConnectionRouter router = getConnection().getRouter();
		for (int i = startConnectionIndex; i >= 0
				&& i < anchors.size(); i += step) {
			IAnchor anchor = anchors.get(i);
			if (!router.wasInserted(anchor)) {
				// found an explicit anchor => iterate explicit anchors to find
				// the one with matching connection index
				List<IAnchor> newAnchors = getBendOperation().getNewAnchors();
				for (int j = 0; j < newAnchors.size(); j++) {
					if (getBendOperation().getConnectionIndex(j) == i) {
						return j;
					}
				}
				throw new IllegalStateException(
						"The explicit anchors of the connection are out of sync with the explicit anchors of the policy.");
			}
		}

		// start and end need to be explicit, therefore, we should always be
		// able to find an explicit anchor, regardless of the passed-in
		// connection index
		throw new IllegalStateException(
				"The start and end anchor of a Connection need to be explicit.");
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
	public IVisualPart<Node, Connection> getHost() {
		return (IVisualPart<Node, Connection>) super.getHost();
	}

	/**
	 * Computes the mouse movement delta (w.r.t. to the initial mouse position)
	 * in local coordinates .
	 *
	 * @param initialMousePositionInScene
	 *            The initial mouse position in scene coordinates.
	 *
	 * @param currentMousePositionInScene
	 *            The current mouse position in scene coordinates.
	 * @return The movement delta, translated into local coordinates of the
	 *         connection
	 *
	 */
	// TODO: extract to somewhere else (this is used in several places)
	protected Point getMouseDeltaInLocal(Point initialMousePositionInScene,
			Point currentMousePositionInScene) {
		Point mouseInLocal = FX2Geometry.toPoint(getConnection().sceneToLocal(
				Geometry2FX.toFXPoint(currentMousePositionInScene)));
		// compensate the movement of the local coordinate system w.r.t. the
		// scene coordinate system (the scene coordinate system stays consistent
		// w.r.t. mouse movement)
		Point deltaInLocal = mouseInLocal
				.getTranslated(FX2Geometry
						.toPoint(getConnection().sceneToLocal(Geometry2FX
								.toFXPoint(initialMousePositionInScene)))
						.getNegated());
		return deltaInLocal;
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
		if (getConnection().getRouter() instanceof OrthogonalRouter
				&& selectedExplicitAnchorIndices.size() == 2) {
			// TODO: grid cell size
			return DEFAULT_SEGMENT_OVERLAY_THRESHOLD;
		}
		// depending grid cell size
		GridModel model = getHost().getRoot().getViewer()
				.getAdapter(GridModel.class);
		if (model != null && model.isSnapToGrid()) {
			return Math.min(model.getGridCellWidth(), model.getGridCellHeight())
					/ 4;
		}
		// fallback to default
		return DEFAULT_OVERLAY_THRESHOLD;
	}

	private List<IContentPart<Node, ? extends Node>> getParts(
			List<Node> nodesUnderMouse) {
		List<IContentPart<Node, ? extends Node>> parts = new ArrayList<>();

		IViewer<Node> viewer = getHost().getRoot().getViewer();
		for (Node node : nodesUnderMouse) {
			IVisualPart<Node, ? extends Node> part = FXPartUtils
					.retrieveVisualPart(viewer, node);
			if (part instanceof IContentPart) {
				parts.add((IContentPart<Node, ? extends Node>) part);
			}
		}
		return parts;
	}

	/**
	 * Returns the current position for the given explicit anchor index.
	 *
	 * @param explicitAnchorIndex
	 * @return
	 */
	private Point getPoint(int explicitAnchorIndex) {
		return getConnection().getPoint(
				getBendOperation().getConnectionIndex(explicitAnchorIndex));
	}

	private FXUpdateAnchorHintsOperation getUpdateHintsOperation() {
		return (FXUpdateAnchorHintsOperation) ((AbstractCompositeOperation) super.getOperation())
				.getOperations().get(1);
	}

	@Override
	public void init() {
		selectedExplicitAnchorIndices.clear();
		selectedInitialPositions.clear();
		preMoveExplicitAnchors.clear();
		preMoveStartHint = null;
		preMoveEndHint = null;
		usePreMoveHints = false;
		super.init();
		// showAnchors("init:");
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
		getBendOperation().getNewAnchors().add(insertionIndex,
				createUnconnectedAnchor(mouseInLocal));
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
			getUpdateHintsOperation().setNewHints(preMoveStartHint,
					preMoveEndHint);
		} else {
			Point newStartHint = computeStartHint();
			Point newEndHint = computeEndHint();
			getUpdateHintsOperation().setNewHints(newStartHint, newEndHint);
		}
		// locally execute hints operation
		try {
			getUpdateHintsOperation().execute(null, null);
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
		int numPoints = selectedExplicitAnchorIndices.size();
		boolean isOrtho = numPoints == 2
				&& getConnection().getRouter() instanceof OrthogonalRouter;

		// save/restore explicit anchors
		if (preMoveExplicitAnchors.isEmpty()) {
			// save initial selected positions
			for (int i = 0; i < selectedExplicitAnchorIndices.size(); i++) {
				selectedInitialPositions.add(i,
						getPoint(selectedExplicitAnchorIndices.get(i)));
			}
			// save initial pre-move explicit anchors
			preMoveExplicitAnchors.addAll(getBendOperation().getNewAnchors());
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
			getBendOperation().setNewAnchors(preMoveExplicitAnchors);
			// restore initial pre-move hints
			getUpdateHintsOperation().setNewHints(preMoveStartHint,
					preMoveEndHint);
			usePreMoveHints = true;
			locallyExecuteOperation();
			usePreMoveHints = false;
		}
		// showAnchors("After Restore:");

		// constrain movement in one direction for segment based connections
		Point mouseDeltaInLocal = getMouseDeltaInLocal(initialMouseInScene,
				currentMouseInScene);
		if (isOrtho) {
			if (isSelectionHorizontal) {
				mouseDeltaInLocal.x = 0;
			} else {
				mouseDeltaInLocal.y = 0;
			}
		}

		// update positions
		for (int i = 0; i < selectedExplicitAnchorIndices.size(); i++) {
			Point selectedPointCurrentPositionInLocal = selectedInitialPositions
					.get(i).getTranslated(mouseDeltaInLocal);

			int explicitAnchorIndex = selectedExplicitAnchorIndices.get(i);
			boolean canConnect = canConnect(explicitAnchorIndex);

			// update anchor
			getBendOperation().getNewAnchors().set(explicitAnchorIndex,
					findOrCreateAnchor(selectedPointCurrentPositionInLocal,
							canConnect));
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
					getBendOperation().getNewAnchors().remove(explicitIndex);
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
				&& selectedExplicitAnchorIndices.size() == 2) {
			// segment overlay removal for orthogonal connection
			removeOverlainSegments();
		} else {
			// point overlay removal otherwise
			removeOverlainPoints();
		}
	}

	private void removeOverlainPoints() {
		int explicitAnchorsSize = getBendOperation().getNewAnchors().size();
		for (int i = selectedExplicitAnchorIndices.size() - 1; i >= 0
				&& explicitAnchorsSize > 2; i--) {
			int index = selectedExplicitAnchorIndices.get(i);
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
				if (selectedExplicitAnchorIndices.contains(overlainIndex)) {
					// selected overlays other selected
					// => skip this overlay
					continue;
				}
				// remove from connection
				getBendOperation().getNewAnchors().remove(index);
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
		selectedExplicitAnchorIndices.add(explicitAnchorIndex);
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
	// List<IAnchor> newAnchors = getBendOperation().getNewAnchors();
	// String anchorsString = "";
	// for (int i = 0, j = 0; i < getConnection().getAnchorsUnmodifiable()
	// .size(); i++) {
	// IAnchor anchor = getConnection().getAnchor(i);
	// if (getConnection().getRouter().wasInserted(anchor)) {
	// anchorsString = anchorsString + " - "
	// + anchor.getClass().toString() + "["
	// + getConnection().getPoint(i) + "],\n";
	// } else {
	// anchorsString = anchorsString
	// + (selectedExplicitAnchorIndices.contains(j) ? "(*)"
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
		int selectionStartIndexInConnection = getBendOperation()
				.getConnectionIndex(selectedExplicitAnchorIndices.get(0));
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
		// System.out.println("selection: " + selectedExplicitAnchorIndices);
		// System.out.println(
		// "overlain: " + toList(overlainPointIndicesRelativeToSelection));
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
			getBendOperation().getNewAnchors().remove((int) explicit.get(i));
			removedCount++;
		}

		// overwrite the first and last explicit anchor with a new unconnected
		// anchor for the adjusted result position if the respective anchor is
		// currently unconnected
		Integer resultStartIndex = explicit.get(0);
		if (!(getBendOperation().getNewAnchors()
				.get(resultStartIndex) instanceof DynamicAnchor)) {
			getBendOperation().getNewAnchors().set(resultStartIndex,
					createUnconnectedAnchor(resultStart));
		}
		Integer resultEndIndex = explicit.get(explicit.size() - 1)
				- removedCount;
		if (!(getBendOperation().getNewAnchors()
				.get(resultEndIndex) instanceof DynamicAnchor)) {
			getBendOperation().getNewAnchors().set(resultEndIndex,
					createUnconnectedAnchor(resultEnd));
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
		return "FXBendConnectionPolicy[host=" + getHost() + "]";
	}

}
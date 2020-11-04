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
import java.util.List;

import org.eclipse.gef.fx.internal.nodes.IBendableCurve;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
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
import org.eclipse.gef.mvc.fx.policies.AbstractPolicy;
import org.eclipse.gef.mvc.fx.providers.IAnchorProvider;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import com.google.inject.Provider;

import javafx.geometry.Orientation;
import javafx.scene.Node;

/**
 * The {@link BendCurvePolicy} can be used to manipulate the points constituting
 * an {@link IBendableCurve}, i.e. its start, way, and end points. Each point is
 * realized though an {@link BendPoint}, which may either be local to the
 * {@link IBendableCurve} (as one of its points), or it may be provided by
 * another {@link IVisualPart} (i.e. the anchor is provided by a
 * {@link Provider} adapted to the part), to which the {@link IBendableCurve} is
 * connected.
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
public class BendCurvePolicy extends AbstractPolicy {

	/**
	 * The overlay threshold, i.e. the distance between two points so that they
	 * are regarded as overlying.
	 */
	protected static final double DEFAULT_SEGMENT_OVERLAY_THRESHOLD = 6;
	/**
	 * The overlay threshold, i.e. the distance between two points so that they
	 * are regarded as overlying.
	 */
	protected static final double DEFAULT_OVERLAY_THRESHOLD = 10;

	// required for the content operation to be chained upon commit
	private List<BendPoint> initialBendPoints = new ArrayList<>();

	private List<BendPoint> preMoveBendPoints = new ArrayList<>();
	private List<Integer> selectedIndices = new ArrayList<>();
	private List<Point> selectedIndicesInitialPositions = new ArrayList<>();

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
		int insertionIndex = explicitAnchorIndex;
		// insert new anchor
		insertExplicitAnchor(insertionIndex, mouseInScene);
		// return handle to newly created anchor
		return insertionIndex;
	}

	@Override
	protected ITransactionalOperation createOperation() {
		ForwardUndoCompositeOperation fwdOp = new ForwardUndoCompositeOperation(
				"BendAndRoute");
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
					.toPoint(getHost().getVisual().localToScene(
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
	 * Returns the current control points of the content.
	 *
	 * @return The current control points.
	 */
	protected List<BendPoint> getCurrentBendPoints() {
		return getHost().getVisualBendPoints();
	}

	@Override
	public IBendableContentPart<? extends Node> getHost() {
		return (IBendableContentPart<? extends Node>) super.getHost();
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
	 * If a segment, i.e. two bend points is selected, returns the orientation
	 * of the selection.
	 *
	 * @return <code>true</code> if the selected points are on a horizontal
	 *         line, otherwise <code>false</code>.
	 */
	public Orientation getOrientation() {
		// determine selection status
		if (selectedIndices.size() > 1) {

			Point p1 = getSelectedInitialPositions().get(0);
			Point p2 = getSelectedInitialPositions().get(1);
			Point delta = p1.getDifference(p2);
			if (Math.abs(delta.x) > Math.abs(delta.y)) {
				return Orientation.HORIZONTAL;
			} else {
				return Orientation.VERTICAL;
			}
		}
		return null;
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
		if (getSelectedIndices().size() > 1) {
			return DEFAULT_SEGMENT_OVERLAY_THRESHOLD;
		}
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
	 * Returns the list of indices selected via {@link #select(int)}.
	 *
	 * @return The list of selected indices in the order of their selection.
	 */
	protected List<Integer> getSelectedIndices() {
		return selectedIndices;
	}

	/**
	 * Returns the initial positions of the selected points in the local
	 * coordinate system of the host visual. May be <code>null</code> prior to
	 * the first {@link #move(Point, Point)} call.
	 *
	 * @return The initial positions of the selected points in the local
	 *         coordinate system of the host visual.
	 */
	public List<Point> getSelectedInitialPositions() {
		return selectedIndicesInitialPositions;
	}

	/**
	 * Returns the current position for the given explicit anchor index, within
	 * the local coordinate system of the {@link Connection}.
	 *
	 * @param bendPointIndex
	 *            The index
	 * @return The {@link Point} in local {@link Connection} coordinates.
	 */
	@SuppressWarnings("unchecked")
	protected Point getVisualPoint(int bendPointIndex) {
		Node visual = getHost().getVisual();
		if (visual instanceof IBendableCurve) {
			return ((IBendableCurve<? extends Node, ? extends Node>) visual)
					.getPoint(bendPointIndex);
		} else if (visual instanceof Connection) {
			return ((Connection) visual).getPoint(bendPointIndex);
		}
		throw new IllegalStateException("Unsupported visual.");
	}

	@Override
	public void init() {
		selectedIndices.clear();
		selectedIndicesInitialPositions.clear();

		super.init();

		initialBendPoints = getCurrentBendPoints();
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
		Point mouseInLocal = FX2Geometry.toPoint(getHost().getVisual()
				.sceneToLocal(Geometry2FX.toFXPoint(mouseInScene)));
		getBendOperation().getFinalBendPoints().add(insertionIndex,
				new BendPoint(mouseInLocal));
		locallyExecuteOperation();
	}

	/**
	 * Returns true if the first specified anchor overlays the second specified
	 * anchor.
	 *
	 * @param overlayingIndex
	 * @param overlainIndex
	 * @return
	 */
	private boolean isOverlay(int overlayingIndex, int overlainIndex) {
		return getVisualPoint(overlayingIndex).getDistance(
				getVisualPoint(overlainIndex)) <= getOverlayThreshold();
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
	 * Moves the currently selected point to the given mouse position in scene
	 * coordinates.
	 *
	 * @param initialMouseInScene
	 *            The initial mouse position in scene coordinates.
	 * @param currentMouseInScene
	 *            The current mouse position in scene coordinates.
	 * @return The point to which the move was performed, in scene coordinates.
	 */
	public Point move(Point initialMouseInScene, Point currentMouseInScene) {
		checkInitialized();

		// save/restore explicit anchors
		restorePreMoveBendpoints();

		// compensate the movement of the local coordinate system w.r.t. the
		// scene coordinate system (the scene coordinate system stays consistent
		// w.r.t. mouse movement)
		Point mouseDeltaInLocal = FX2Geometry
				.toPoint(getHost().getVisual().sceneToLocal(
						Geometry2FX.toFXPoint(currentMouseInScene)))
				.getTranslated(
						FX2Geometry
								.toPoint(getHost().getVisual()
										.sceneToLocal(Geometry2FX.toFXPoint(
												initialMouseInScene)))
								.getNegated());

		// update positions
		for (int i = 0; i < selectedIndices.size(); i++) {
			int bendPointIndex = selectedIndices.get(i);
			boolean canConnect = canConnect(bendPointIndex);

			Point selectedPointCurrentPositionInLocal = selectedIndicesInitialPositions
					.get(i).getTranslated(mouseDeltaInLocal);

			// update anchor
			getBendOperation().getFinalBendPoints().set(bendPointIndex,
					findOrCreateAnchor(bendPointIndex,
							selectedPointCurrentPositionInLocal, canConnect));
		}
		locallyExecuteOperation();
		// showAnchors("After Move:");

		// remove overlain
		removeOverlain();
		// showAnchors("After RemoveOverlain:");

		return currentMouseInScene;
	}

	/**
	 * Removes any bend points overlain during the movement of selected ones.
	 */
	protected void removeOverlain() {
		// point overlay removal otherwise
		removeOverlainPoints();
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
			boolean isLeftOverlain = index > 0 && isOverlay(index, index - 1);
			boolean isRightOverlain = index < explicitAnchorsSize - 1
					&& isOverlay(index, index + 1);

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

	/**
	 * Restores the initial bend points prior to moving.
	 */
	protected void restorePreMoveBendpoints() {
		// showAnchors("Before Restore:");
		getBendOperation().setFinalBendPoints(preMoveBendPoints);
		locallyExecuteOperation();
		// showAnchors("After Restore:");
	}

	/**
	 * Selects the point specified by the given segment index and parameter for
	 * manipulation. Captures the initial position of the selected point and the
	 * related initial mouse location.
	 *
	 * @param index
	 *            Index of the bend point to select for manipulation.
	 */
	public void select(int index) {
		checkInitialized();
		// save selected anchor handles
		selectedIndices.add(index);
		selectedIndicesInitialPositions.add(getVisualPoint(index));

		// after last call to select, the bend points are final
		preMoveBendPoints.clear();
		preMoveBendPoints.addAll(getBendOperation().getFinalBendPoints());
	}

	/**
	 * Selects the end points of the segment specified by the given indices.
	 * Will introduce new bend points in case the start or the end bend point
	 * are connected.
	 *
	 * @param from
	 *            The start index of a segment.
	 * @param to
	 *            The end index of the segment.
	 *
	 */
	@SuppressWarnings("unchecked")
	public void selectSegment(int from, int to) {
		int firstAnchorHandle = from;
		int secondAnchorHandle = to;
		// create unconnected copies of the segment anchors if they are
		// connected
		boolean isFirstConnected = getHost().getVisual() instanceof Connection
				? ((Connection) getHost().getVisual()).isConnected(from)
				: ((IBendableCurve<? extends Node, ? extends Node>) getHost()
						.getVisual()).isConnected(from);
		boolean isSecondConnected = getHost().getVisual() instanceof Connection
				? ((Connection) getHost().getVisual()).isConnected(to)
				: ((IBendableCurve<? extends Node, ? extends Node>) getHost()
						.getVisual()).isConnected(to);
		if (isFirstConnected) {
			firstAnchorHandle = createAfter(firstAnchorHandle,
					NodeUtils.localToScene(getHost().getVisual(),
							getVisualPoint(firstAnchorHandle)));
			// XXX: increase index of second anchor because one anchor was
			// inserted before it
			secondAnchorHandle++;
		}
		if (isSecondConnected) {
			secondAnchorHandle = createBefore(secondAnchorHandle,
					NodeUtils.localToScene(getHost().getVisual(),
							getVisualPoint(secondAnchorHandle)));
		}

		// select the end anchors for manipulation
		select(firstAnchorHandle);
		select(secondAnchorHandle);
	}

}
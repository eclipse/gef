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
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.anchors.StaticAnchor;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.OrthogonalRouter;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXBendConnectionOperation;
import org.eclipse.gef4.mvc.fx.parts.FXPartUtils;
import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.operations.DeselectOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.SelectOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractTransactionPolicy;
import org.eclipse.gef4.mvc.policies.AbstractTransformPolicy;
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
public class FXBendConnectionPolicy extends AbstractTransactionPolicy<Node> {

	/**
	 * An {@link AnchorHandle} represents an explicit {@link IAnchor} within the
	 * manipulated {@link Connection}. The associated connection index is kept
	 * up-to-date by this policy, so that {@link AnchorHandle}s can safely be
	 * used and even passed to the user.
	 *
	 * @author mwienand
	 *
	 */
	public class AnchorHandle {
		private Point initialPosition = null;

		/**
		 * The explicit anchor index for this handle. This value is volatile, it
		 * will be updated when anchors are manipulated.
		 */
		private int explicitAnchorIndex = 0;

		/**
		 * Creates a new {@link AnchorHandle} for the explicit anchor at the
		 * given index.
		 *
		 * @param explicitAnchorIndex
		 *            The explicit anchor index for which to create a handle.
		 */
		public AnchorHandle(int explicitAnchorIndex) {
			this.explicitAnchorIndex = explicitAnchorIndex;
			initialPosition = getPosition();
		}

		/**
		 * Returns the {@link IAnchor} that corresponds to this
		 * {@link AnchorHandle}.
		 *
		 * @return The {@link IAnchor} that corresponds to this
		 *         {@link AnchorHandle}.
		 */
		public IAnchor getAnchor() {
			return getConnection().getAnchor(getConnectionIndex());
		}

		/**
		 * Returns the index within the Connection's anchors for this
		 * {@link AnchorHandle}.
		 *
		 * @return The connection's anchor index for this {@link AnchorHandle}.
		 */
		public int getConnectionIndex() {
			int explicitCount = -1;

			for (int i = 0; i < getConnection().getPoints().size(); i++) {
				IAnchor a = getConnection().getAnchor(i);
				if (!getConnection().getRouter().isImplicitAnchor(a)) {
					explicitCount++;
				}
				if (explicitCount == explicitAnchorIndex) {
					// found all operation indices
					return i;
				}
			}

			throw new IllegalArgumentException(
					"Cannot determine connection index for operation index "
							+ explicitAnchorIndex + ".");
		}

		/**
		 * Returns the initial position of the referenced anchor. The initial
		 * position is evaluated upon handle construction.
		 *
		 * @return The initial position of the referenced anchor.
		 */
		public Point getInitialPosition() {
			return initialPosition;
		}

		/**
		 * Returns the operation index for this {@link AnchorHandle}.
		 *
		 * @return The operation index for this {@link AnchorHandle}.
		 */
		public int getOperationIndex() {
			return explicitAnchorIndex;
		}

		/**
		 * Returns the current position of the referenced anchor.
		 *
		 * @return The current position of the referenced anchor.
		 */
		public Point getPosition() {
			return getConnection().getPoint(getConnectionIndex());
		}
	}

	/**
	 * A {@link PointOverlay} represents a 2-point-overlay. A 2-point-overlay
	 * occurs when a selected point is dragged into the removal radius of one of
	 * its neighbors.
	 */
	public class PointOverlay {
		/**
		 * The replacement {@link AnchorHandle}.
		 */
		private AnchorHandle replacement;

		/**
		 * The removed {@link IAnchor}.
		 */
		private IAnchor removed;

		/**
		 * Flag that indicates if the removed handle was before its replacement.
		 */
		private boolean wasBefore;

		/**
		 * The selected {@link AnchorHandle} that was removed.
		 */
		private AnchorHandle selected;

		/**
		 *
		 * @param removed
		 *            The removed {@link IAnchor}.
		 * @param replacement
		 *            The replacement {@link AnchorHandle}.
		 * @param wasBefore
		 *            Flag that indicates if the removed handle was before its
		 *            replacement.
		 * @param selectedHandle
		 *            The selected {@link AnchorHandle} that was removed.
		 */
		public PointOverlay(IAnchor removed, AnchorHandle replacement,
				boolean wasBefore, AnchorHandle selectedHandle) {
			this.removed = removed;
			this.replacement = replacement;
			this.wasBefore = wasBefore;
			this.selected = selectedHandle;
		}
	}

	/**
	 * A {@link SegmentOverlay} represents a 3-segment-overlay. A
	 * 3-segment-overlay occurs when a selected segment is dragged close to
	 * another, parallel segment, so that the orthogonal gap segment's length is
	 * below the removal threshold.
	 *
	 * <pre>
	 * A-----P   Q           A = Anchorage
	 *       |               M = retained neighbor
	 *       |   M-----A     N = removed neighbor
	 *       |   |           O = removed selected
	 *       O---N           P = constrained selected
	 *                       Q = constrained position
	 * </pre>
	 *
	 * @author mwienand
	 *
	 */
	public class SegmentOverlay {
		private boolean isNeighborsFirst;

		// retained neighbor
		private AnchorHandle retainedNeighborHandle;
		private IAnchor retainedNeighborOldAnchor;
		private IAnchor retainedNeighborNewAnchor;

		// removed neighbor
		private AnchorHandle removedNeighborHandle;
		private IAnchor removedNeighborOldAnchor;

		// removed selected
		private AnchorHandle removedSelectedHandle;
		private IAnchor removedSelectedOldAnchor;

		// constrained selected
		private AnchorHandle constrainedSelectedHandle;
		private IAnchor constrainedSelectedOldAnchor;
		private IAnchor constrainedSelectedNewAnchor;

		/**
		 * Constructs a new {@link SegmentOverlay} from the given data.
		 *
		 * @param isNeighborsFirst
		 *            a
		 * @param retainedNeighborHandle
		 *            a
		 * @param retainedNeighborOldAnchor
		 *            a
		 * @param retainedNeighborNewAnchor
		 *            a
		 * @param removedNeighborHandle
		 *            a
		 * @param removedNeighborOldAnchor
		 *            a
		 * @param removedSelectedHandle
		 *            a
		 * @param removedSelectedOldAnchor
		 *            a
		 * @param constrainedSelectedHandle
		 *            a
		 * @param constrainedSelectedOldAnchor
		 *            a
		 * @param constrainedSelectedNewAnchor
		 *            a
		 *
		 */
		public SegmentOverlay(boolean isNeighborsFirst,
				AnchorHandle retainedNeighborHandle,
				IAnchor retainedNeighborOldAnchor,
				IAnchor retainedNeighborNewAnchor,
				AnchorHandle removedNeighborHandle,
				IAnchor removedNeighborOldAnchor,
				AnchorHandle removedSelectedHandle,
				IAnchor removedSelectedOldAnchor,
				AnchorHandle constrainedSelectedHandle,
				IAnchor constrainedSelectedOldAnchor,
				IAnchor constrainedSelectedNewAnchor) {
			this.isNeighborsFirst = isNeighborsFirst;
			this.retainedNeighborHandle = retainedNeighborHandle;
			this.retainedNeighborOldAnchor = retainedNeighborOldAnchor;
			this.retainedNeighborNewAnchor = retainedNeighborNewAnchor;
			this.removedNeighborHandle = removedNeighborHandle;
			this.removedNeighborOldAnchor = removedNeighborOldAnchor;
			this.removedSelectedHandle = removedSelectedHandle;
			this.removedSelectedOldAnchor = removedSelectedOldAnchor;
			this.constrainedSelectedHandle = constrainedSelectedHandle;
			this.constrainedSelectedOldAnchor = constrainedSelectedOldAnchor;
			this.constrainedSelectedNewAnchor = constrainedSelectedNewAnchor;
		}
	}

	/**
	 * The overlay threshold, i.e. the distance between two points so that they
	 * are regarded as overlying.
	 */
	protected static final double DEFAULT_OVERLAY_THRESHOLD = 10;

	private List<AnchorHandle> explicitAnchors = new ArrayList<>();
	private List<AnchorHandle> selectedAnchors = new ArrayList<>();
	private List<PointOverlay> pointOverlays = new ArrayList<>();
	private List<SegmentOverlay> segmentOverlays = new ArrayList<>();

	private boolean canConnect(int explicitAnchorIndex) {
		return explicitAnchorIndex == 0
				|| explicitAnchorIndex == explicitAnchors.size() - 1;
	}

	@Override
	public ITransactionalOperation commit() {
		normalize();
		ITransactionalOperation bendOperation = super.commit();

		if (bendOperation == null || bendOperation.isNoOp()) {
			return null;
		}

		// chain a reselect operation here, so handles are properly updated
		ForwardUndoCompositeOperation updateOperation = new ForwardUndoCompositeOperation(
				bendOperation.getLabel());
		updateOperation.add(bendOperation);
		ReverseUndoCompositeOperation reselectOperation = createReselectOperation();
		if (reselectOperation != null) {
			updateOperation.add(reselectOperation);
		}

		return updateOperation;
	}

	/**
	 * Creates a new anchor after the anchor specified by the given explicit
	 * anchor index. Returns the new anchor's explicit index.
	 *
	 * @param explicitAnchorHandle
	 *            An {@link AnchorHandle} that references the explicit anchor
	 *            after which the new anchor is inserted.
	 * @param mouseInScene
	 *            The position for the new anchor in scene coordinates.
	 *
	 * @return The {@link AnchorHandle} for the new anchor.
	 */
	public AnchorHandle createAfter(AnchorHandle explicitAnchorHandle,
			Point mouseInScene) {
		checkInitialized();
		// determine insertion index
		int insertionIndex = explicitAnchorHandle.explicitAnchorIndex + 1;
		// insert new anchor
		insertExplicitAnchor(insertionIndex, mouseInScene);
		// return handle to newly created anchor
		return explicitAnchors.get(insertionIndex);
	}

	/**
	 * Creates a new anchor before the anchor specified by the given explicit
	 * anchor index. Returns the new anchor's explicit index.
	 *
	 * @param explicitAnchorHandle
	 *            An {@link AnchorHandle} that references the explicit anchor
	 *            after which the new anchor is inserted.
	 * @param mouseInScene
	 *            The position for the new anchor in scene coordinates.
	 *
	 * @return The {@link AnchorHandle} for the new anchor.
	 */
	public AnchorHandle createBefore(AnchorHandle explicitAnchorHandle,
			Point mouseInScene) {
		checkInitialized();
		// determine insertion index
		int insertionIndex = explicitAnchorHandle.explicitAnchorIndex;
		// insert new anchor
		insertExplicitAnchor(insertionIndex, mouseInScene);
		// return index of newly created anchor
		return explicitAnchors.get(insertionIndex);
	}

	@Override
	protected FXBendConnectionOperation createOperation() {
		return new FXBendConnectionOperation(getConnection());
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
	 * Returns an {@link AnchorHandle} for the first explicit anchor that can be
	 * found when iterating the connection anchors backwards, starting at the
	 * given connection index. If the anchor at the given index is an explicit
	 * anchor, an {@link AnchorHandle} for that anchor will be returned. If no
	 * explicit anchor is found, an exception is thrown, because the start and
	 * end anchor of a connection need to be explicit.
	 *
	 * @param connectionIndex
	 *            The index that specifies the anchor of the connection at which
	 *            the search starts.
	 * @return An {@link AnchorHandle} for the previous explicit anchor.
	 */
	public AnchorHandle findExplicitAnchorBackwards(int connectionIndex) {
		for (int i = connectionIndex; i >= 0; i--) {
			IAnchor anchor = getConnection().getAnchor(i);
			if (!getConnection().getRouter().isImplicitAnchor(anchor)) {
				// found an explicit anchor
				// iterate explicit anchors to find the one with matching
				// connection index
				for (int j = 0; j < explicitAnchors.size(); j++) {
					if (explicitAnchors.get(j).getConnectionIndex() == i) {
						return explicitAnchors.get(j);
					}
				}
			}
		}

		// start and end need to be explicit, therefore, we should always be
		// able to find an explicit anchor, regardless of the passed-in
		// connection index
		throw new IllegalStateException(
				"The start and end anchor of a Connection need to be explicit.");
	}

	/**
	 * Returns an {@link AnchorHandle} for the first explicit anchor that can be
	 * found when iterating the connection anchors forwards, starting at the
	 * given connection index. If the anchor at the given index is an explicit
	 * anchor, an {@link AnchorHandle} for that anchor will be returned. If no
	 * explicit anchor is found, an exception is thrown, because the start and
	 * end anchor of a connection need to be explicit.
	 *
	 * @param connectionIndex
	 *            The index that specifies the anchor of the connection at which
	 *            the search starts.
	 * @return An {@link AnchorHandle} for the next explicit anchor.
	 */
	public AnchorHandle findExplicitAnchorForwards(int connectionIndex) {
		for (int i = connectionIndex; i < getConnection().getAnchors()
				.size(); i++) {
			IAnchor anchor = getConnection().getAnchor(i);
			if (!getConnection().getRouter().isImplicitAnchor(anchor)) {
				// found an explicit anchor
				// iterate explicit anchors to find the one with matching
				// connection index
				for (int j = 0; j < explicitAnchors.size(); j++) {
					if (explicitAnchors.get(j).getConnectionIndex() == i) {
						return explicitAnchors.get(j);
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
	@SuppressWarnings("serial")
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
			IVisualPart<Node, ? extends Node> anchorPart = getAnchorPart(
					getParts(pickedNodes));
			if (anchorPart != null) {
				// use anchor returned by part
				anchor = anchorPart.getAdapter(
						new TypeToken<Provider<? extends IAnchor>>() {
						}).get();
			}
		}
		if (anchor == null) {
			anchor = createUnconnectedAnchor(positionInLocal);
		}
		return anchor;
	}

	@SuppressWarnings("serial")
	private IContentPart<Node, ? extends Node> getAnchorPart(
			List<IContentPart<Node, ? extends Node>> partsUnderMouse) {
		for (IContentPart<Node, ? extends Node> cp : partsUnderMouse) {
			IContentPart<Node, ? extends Node> part = cp;
			Provider<? extends IAnchor> anchorProvider = part
					.getAdapter(new TypeToken<Provider<? extends IAnchor>>() {
					});
			if (anchorProvider != null && anchorProvider.get() != null) {
				return part;
			}
		}
		return null;
	}

	/**
	 * Returns an {@link FXBendConnectionOperation} that is extracted from the
	 * operation created by {@link #createOperation()}.
	 *
	 * @return an {@link FXBendConnectionOperation} that is extracted from the
	 *         operation created by {@link #createOperation()}.
	 */
	protected FXBendConnectionOperation getBendOperation() {
		return (FXBendConnectionOperation) super.getOperation();
	}

	/**
	 * Returns the {@link Connection} that is manipulated by this policy.
	 *
	 * @return The {@link Connection} that is manipulated by this policy.
	 */
	protected Connection getConnection() {
		return getHost().getVisual();
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
		// scene coordinate system (the scene coordinate system stays
		// consistent
		// w.r.t. to mouse movement)
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
		GridModel model = getHost().getRoot().getViewer()
				.getAdapter(GridModel.class);
		if (model != null && model.isSnapToGrid()) {
			return Math.min(model.getGridCellWidth(), model.getGridCellHeight())
					/ 4;
		}
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

	private SegmentOverlay getSegmentOverlayLeft() {
		int connectionIndex = selectedAnchors.get(0).getConnectionIndex();
		if (connectionIndex < 2) {
			return null;
		}

		// determine segment direction constraint before manipulating the
		// selection
		boolean isSameY = isSelectionOnHorizontalLine();

		// constrained selected
		AnchorHandle constrainedSelectedHandle = selectedAnchors.get(1);
		IAnchor constrainedSelectedOldAnchor = constrainedSelectedHandle
				.getAnchor();

		// removed selected
		AnchorHandle removedSelectedHandle = selectedAnchors.get(0);
		IAnchor removedSelectedOldAnchor = removedSelectedHandle.getAnchor();

		// removed neighbor
		int removedNeighborIndex = removedSelectedHandle.getConnectionIndex()
				- 1;
		IAnchor removedNeighborOldAnchor = getConnection()
				.getAnchor(removedNeighborIndex);
		// determine if removed neighbor is explicit
		boolean removedNeighborWasExplicit = isExplicit(removedNeighborIndex);
		// make it explicit
		AnchorHandle removedNeighborHandle = makeExplicitBefore(
				removedNeighborIndex);

		// retained neighbor
		int retainedNeighborIndex = removedNeighborIndex - 1;
		IAnchor retainedNeighborOldAnchor = getConnection()
				.getAnchor(retainedNeighborIndex);
		// determine if retained neighbor is explicit
		boolean retainedNeighborWasExplicit = isExplicit(retainedNeighborIndex);
		// make it explicit
		AnchorHandle retainedNeighborHandle = makeExplicitBefore(
				retainedNeighborIndex);
		IAnchor retainedNeighborNewAnchor = retainedNeighborHandle.getAnchor();

		// compute new constrained anchor
		// TODO: special case: constrained anchor overlays existing point
		Point removedNeighborPosition = removedNeighborHandle.getPosition();
		Point constrainedSelectedPosition = constrainedSelectedHandle
				.getPosition();
		IAnchor constrainedSelectedNewAnchor = createUnconnectedAnchor(
				new Point(
						isSameY ? constrainedSelectedPosition.x
								: removedNeighborPosition.x,
						isSameY ? removedNeighborPosition.y
								: constrainedSelectedPosition.y));

		// construct segment overlay
		SegmentOverlay segmentOverlay = new SegmentOverlay(true,
				retainedNeighborHandle, retainedNeighborOldAnchor,
				retainedNeighborNewAnchor, removedNeighborHandle,
				removedNeighborOldAnchor, removedSelectedHandle,
				removedSelectedOldAnchor, constrainedSelectedHandle,
				constrainedSelectedOldAnchor, constrainedSelectedNewAnchor);

		// compute segment distance
		double distance = isSameY
				? Math.abs(removedNeighborHandle.getPosition().y
						- removedSelectedHandle.getPosition().y)
				: Math.abs(removedNeighborHandle.getPosition().x
						- removedSelectedHandle.getPosition().x);

		// if the distance is below the overlay threshold then a segment overlay
		// is found
		if (distance < DEFAULT_OVERLAY_THRESHOLD) {
			return segmentOverlay;
		}

		// XXX: No left segment overlay found. However, we eventually made some
		// anchors explicit in order to obtain a handle for them. These anchors
		// need to be made implicit again.

		// restore removed neighbor anchor
		if (!removedNeighborWasExplicit) {
			getBendOperation().getNewAnchors().set(
					removedNeighborHandle.explicitAnchorIndex,
					removedNeighborOldAnchor);
			explicitAnchors.remove(removedNeighborHandle.explicitAnchorIndex);
		}
		// restore retained neighbor anchor
		if (!retainedNeighborWasExplicit) {
			getBendOperation().getNewAnchors().set(
					retainedNeighborHandle.explicitAnchorIndex,
					retainedNeighborOldAnchor);
			explicitAnchors.remove(retainedNeighborHandle.explicitAnchorIndex);
		}
		// fix explicit anchor indices
		for (int i = 0; i < explicitAnchors.size(); i++) {
			explicitAnchors.get(i).explicitAnchorIndex = i;
		}

		return null;
	}

	private SegmentOverlay getSegmentOverlayRight() {
		int connectionIndex = selectedAnchors.get(1).getConnectionIndex();
		if (connectionIndex + 2 >= getConnection().getPoints().size()) {
			return null;
		}

		// debug output
		// List<IAnchor> newAnchors = getBendOperation().getNewAnchors();
		// String anchorsBeforeRightOverlay = "";
		// for (int i = 0, j = 0; i < getConnection().getAnchors().size(); i++)
		// {
		// IAnchor anchor = getConnection().getAnchor(i);
		// if (!getConnection().getRouter().isImplicitAnchor(anchor)) {
		// anchorsBeforeRightOverlay = anchorsBeforeRightOverlay
		// + (selectedAnchors.contains(explicitAnchors.get(j))
		// ? "(*)" : " * ")
		// + anchor.getClass().toString() + "["
		// + getConnection().getPoint(i) + "]" + " ("
		// + newAnchors.get(j) + ") {" + explicitAnchors.get(j)
		// + "},\n";
		// j++;
		// }
		// }

		// determine segment direction constraint before manipulating the
		// selection
		boolean isSameY = isSelectionOnHorizontalLine();

		// retained neighbor
		int retainedNeighborIndex = connectionIndex + 2;
		IAnchor retainedNeighborOldAnchor = getConnection()
				.getAnchor(retainedNeighborIndex);
		// determine if retained neighbor is explicit
		boolean retainedNeighborWasExplicit = isExplicit(retainedNeighborIndex);
		// make it explicit
		AnchorHandle retainedNeighborHandle = makeExplicitBefore(
				retainedNeighborIndex);
		IAnchor retainedNeighborNewAnchor = retainedNeighborHandle.getAnchor();

		// removed neighbor
		int removedNeighborIndex = retainedNeighborIndex - 1;
		IAnchor removedNeighborOldAnchor = getConnection()
				.getAnchor(removedNeighborIndex);
		// determine if removed neighbor is explicit
		boolean removedNeighborWasExplicit = isExplicit(removedNeighborIndex);
		// make it explicit
		AnchorHandle removedNeighborHandle = makeExplicitBefore(
				removedNeighborIndex);

		// removed selected
		AnchorHandle removedSelectedHandle = selectedAnchors.get(1);
		IAnchor removedSelectedOldAnchor = removedSelectedHandle.getAnchor();

		// constrained selected
		AnchorHandle constrainedSelectedHandle = selectedAnchors.get(0);
		IAnchor constrainedSelectedOldAnchor = constrainedSelectedHandle
				.getAnchor();

		// compute new constrained anchor
		// TODO: special case: constrained anchor overlays existing point
		Point removedNeighborPosition = removedNeighborHandle.getPosition();
		Point constrainedSelectedPosition = constrainedSelectedHandle
				.getPosition();
		IAnchor constrainedSelectedNewAnchor = createUnconnectedAnchor(
				new Point(
						isSameY ? constrainedSelectedPosition.x
								: removedNeighborPosition.x,
						isSameY ? removedNeighborPosition.y
								: constrainedSelectedPosition.y));

		// construct segment overlay
		SegmentOverlay segmentOverlay = new SegmentOverlay(false,
				retainedNeighborHandle, retainedNeighborOldAnchor,
				retainedNeighborNewAnchor, removedNeighborHandle,
				removedNeighborOldAnchor, removedSelectedHandle,
				removedSelectedOldAnchor, constrainedSelectedHandle,
				constrainedSelectedOldAnchor, constrainedSelectedNewAnchor);

		// compute segment distance
		double distance = isSameY
				? Math.abs(removedNeighborHandle.getPosition().y
						- removedSelectedHandle.getPosition().y)
				: Math.abs(removedNeighborHandle.getPosition().x
						- removedSelectedHandle.getPosition().x);

		// if the distance is below the overlay threshold then a segment overlay
		// is found
		if (distance < DEFAULT_OVERLAY_THRESHOLD) {
			// debug output
			// System.out.println("anchors before right overlay:\n"
			// + anchorsBeforeRightOverlay);
			return segmentOverlay;
		}

		// XXX: No left segment overlay found. However, we eventually made some
		// anchors explicit in order to obtain a handle for them. These anchors
		// need to be made implicit again.

		// restore retained neighbor anchor
		if (!retainedNeighborWasExplicit) {
			getBendOperation().getNewAnchors().set(
					retainedNeighborHandle.explicitAnchorIndex,
					retainedNeighborOldAnchor);
			explicitAnchors.remove(retainedNeighborHandle.explicitAnchorIndex);
		}
		// restore removed neighbor anchor
		if (!removedNeighborWasExplicit) {
			getBendOperation().getNewAnchors().set(
					removedNeighborHandle.explicitAnchorIndex,
					removedNeighborOldAnchor);
			explicitAnchors.remove(removedNeighborHandle.explicitAnchorIndex);
		}
		// fix explicit anchor indices
		for (int i = 0; i < explicitAnchors.size(); i++) {
			explicitAnchors.get(i).explicitAnchorIndex = i;
		}

		return null;
	}

	@Override
	public void init() {
		selectedAnchors.clear();
		explicitAnchors.clear();
		pointOverlays.clear();
		segmentOverlays.clear();
		super.init();
		// create handles for all explicit anchors
		int explicitAnchorIndex = 0;
		for (int i = 0; i < getConnection().getAnchors().size(); i++) {
			IAnchor anchor = getConnection().getAnchor(i);
			if (!getConnection().getRouter().isImplicitAnchor(anchor)) {
				explicitAnchors.add(new AnchorHandle(explicitAnchorIndex++));
			}
		}
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
		AnchorHandle newAnchorHandle = new AnchorHandle(insertionIndex);
		explicitAnchors.add(insertionIndex, newAnchorHandle);
		// update explicit anchor indices
		// => increment successing anchors' indices
		for (int i = insertionIndex + 1; i < explicitAnchors.size(); i++) {
			explicitAnchors.get(i).explicitAnchorIndex++;
		}
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
		return !getConnection().getRouter().isImplicitAnchor(anchor);
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
		AnchorHandle overlaying = explicitAnchors
				.get(overlayingExplicitAnchorIndex);
		AnchorHandle overlain = explicitAnchors
				.get(overlainExplicitAnchorIndex);
		return overlaying.getPosition().getDistance(
				overlain.getPosition()) <= DEFAULT_OVERLAY_THRESHOLD;
	}

	/**
	 * Returns <code>true</code> if the selected anchors are on a horizontal
	 * line, i.e. they share an equal Y coordinate. Otherwise returns
	 * <code>false</code>.
	 *
	 * @return <code>true</code> if the Y coordinates of the selected anchors
	 *         are the same, otherwise <code>false</code>.
	 */
	protected boolean isSelectionOnHorizontalLine() {
		double y0 = selectedAnchors.get(0).getInitialPosition().y;
		double y1 = selectedAnchors.get(1).getInitialPosition().y;
		boolean isHorizontallyConstrained = isUnpreciseEquals(y0, y1);
		return isHorizontallyConstrained;
	}

	private boolean isUnpreciseEquals(double y0, double y1) {
		return Math.abs(y0 - y1) < 1;
	}

	/**
	 * Returns an {@link AnchorHandle} for the explicit anchor at the given
	 * connection index. If the anchor at the given connection index is
	 * implicit, it will be made explicit first.
	 *
	 * @param connectionIndex
	 *            The connection index of the anchor to make explicit.
	 * @return The {@link AnchorHandle} for the explicit anchor that references
	 *         the specified connection anchor.
	 */
	public AnchorHandle makeExplicitAfter(int connectionIndex) {
		AnchorHandle precedingAnchorHandle = findExplicitAnchorBackwards(
				connectionIndex);
		if (isExplicit(connectionIndex)) {
			// the "preceding" anchor handle is actually the anchor handle
			// for
			// the passed-in connection index
			return precedingAnchorHandle;
		}
		// create a new anchor handle after the preceding anchor handle. use
		// the
		// connection to determine the position for the new anchor.
		Point position = getConnection().getPoint(connectionIndex);
		return createAfter(precedingAnchorHandle, FX2Geometry
				.toPoint(getConnection().localToScene(position.x, position.y)));
	}

	/**
	 * Returns an {@link AnchorHandle} for the explicit anchor at the given
	 * connection index. If the anchor at the given connection index is
	 * implicit, it will be made explicit first.
	 *
	 * @param connectionIndex
	 *            The connection index of the anchor to make explicit.
	 * @return The {@link AnchorHandle} for the explicit anchor that references
	 *         the specified connection anchor.
	 */
	public AnchorHandle makeExplicitBefore(int connectionIndex) {
		AnchorHandle nextAnchorHandle = findExplicitAnchorForwards(
				connectionIndex);
		if (isExplicit(connectionIndex)) {
			// the "next" anchor handle is actually the anchor handle for
			// the passed-in connection index
			return nextAnchorHandle;
		}
		// create a new anchor handle before the next anchor handle. use the
		// connection to determine the position for the new anchor.
		Point position = getConnection().getPoint(connectionIndex);
		return createBefore(nextAnchorHandle, FX2Geometry
				.toPoint(getConnection().localToScene(position.x, position.y)));
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

		// restore removed so that selection is present
		restoreRemoved();

		// constrain movement in one direction for segment based connections
		int numPoints = selectedAnchors.size();
		boolean isSegmentBased = numPoints > 1
				&& getConnection().getRouter() instanceof OrthogonalRouter;
		Point mouseDeltaInLocal = getMouseDeltaInLocal(initialMouseInScene,
				currentMouseInScene);
		if (isSegmentBased) {
			if (isSelectionOnHorizontalLine()) {
				mouseDeltaInLocal.x = 0;
			} else {
				mouseDeltaInLocal.y = 0;
			}
		}

		// update positions
		for (int i = 0; i < selectedAnchors.size(); i++) {
			Point selectedPointCurrentPositionInLocal = selectedAnchors.get(i)
					.getInitialPosition().getTranslated(mouseDeltaInLocal);

			// snap-to-grid
			// TODO: make snapping (0.5) configurable
			Dimension snapToGridOffset = AbstractTransformPolicy
					.getSnapToGridOffset(
							getHost().getRoot().getViewer()
									.<GridModel> getAdapter(GridModel.class),
							selectedPointCurrentPositionInLocal.x,
							selectedPointCurrentPositionInLocal.y, 0.5, 0.5);
			selectedPointCurrentPositionInLocal
					.translate(snapToGridOffset.getNegated());

			int explicitAnchorIndex = selectedAnchors
					.get(i).explicitAnchorIndex;
			boolean canConnect = canConnect(explicitAnchorIndex);

			// update anchor
			getBendOperation().getNewAnchors().set(explicitAnchorIndex,
					findOrCreateAnchor(selectedPointCurrentPositionInLocal,
							canConnect));
		}
		locallyExecuteOperation();

		// remove overlain
		removeOverlain();
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
		List<IAnchor> anchors = getConnection().getAnchors();

		// determine corresponding positions
		List<Point> positions = getConnection().getPoints();

		// test each explicit static anchor for removal potential
		int explicitIndex = 0; // start is explicit
		boolean removed = false;
		for (int i = 1; i < anchors.size() - 1; i++) {
			IAnchor anchor = anchors.get(i);
			if (!getConnection().getRouter().isImplicitAnchor(anchor)) {
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
					// make previous and next explicit
					if (getConnection().getRouter()
							.isImplicitAnchor(anchors.get(i + 1))) {
						// make next explicit
						makeExplicitBefore(i + 1);
					}
					if (getConnection().getRouter()
							.isImplicitAnchor(anchors.get(i - 1))) {
						// make previous explicit
						makeExplicitAfter(i - 1);
						explicitIndex++;
					}
					// remove current point as it is unnecessary
					explicitAnchors.remove(explicitIndex);
					getBendOperation().getNewAnchors().remove(explicitIndex);
					// fix anchor indices
					for (int j = 0; j < explicitAnchors.size(); j++) {
						explicitAnchors.get(j).explicitAnchorIndex = j;
					}
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
				&& selectedAnchors.size() == 2) {
			// segment overlay removal for orthogonal connection
			removeOverlainSegments();
			// do not remove point overlays if a segment overlay was found
			if (!segmentOverlays.isEmpty()) {
				return;
			}
		}
		// point overlay removal
		removeOverlainPoints();
	}

	private void removeOverlainPoints() {
		for (int i = 0; i < selectedAnchors.size(); i++) {
			AnchorHandle handle = selectedAnchors.get(i);
			int index = handle.explicitAnchorIndex;
			// XXX: If an overlay is recognized, the overlaying anchor is
			// removed and practically replaced by the overlain anchor. This
			// might seem unintuitive, however, it enables the user to
			// cleanly
			// remove control points by dragging them onto a neighboring
			// point,
			// without augmenting any other control points.
			boolean isLeftOverlain = index > 0
					&& isExplicitOverlay(index, index - 1);
			boolean isRightOverlain = index < explicitAnchors.size() - 1
					&& isExplicitOverlay(index, index + 1);

			if (isLeftOverlain || isRightOverlain) {
				int overlainIndex = isLeftOverlain ? index - 1 : index + 1;
				AnchorHandle explicitNeighborHandle = explicitAnchors
						.get(overlainIndex);
				if (selectedAnchors.contains(explicitNeighborHandle)) {
					// selected overlays other selected
					// => skip this overlay
					continue;
				}
				// remove overlaying anchor handle (the selected handle)
				explicitAnchors.remove(index);
				int connectionIndex = handle.getConnectionIndex();
				IAnchor toBeRemovedAnchor = getConnection()
						.getAnchor(connectionIndex);
				// remove from connection
				getBendOperation().getNewAnchors().remove(index);
				// decrement indices of successing anchor handles
				for (int j = index; j < explicitAnchors.size(); j++) {
					explicitAnchors.get(j).explicitAnchorIndex--;
				}
				// store removed
				PointOverlay pointOverlay = new PointOverlay(toBeRemovedAnchor,
						explicitNeighborHandle, !isLeftOverlain, handle);
				pointOverlays.add(pointOverlay);
				locallyExecuteOperation();
			}
		}
	}

	private void removeOverlainSegments() {
		SegmentOverlay segmentOverlay = getSegmentOverlayLeft();
		if (segmentOverlay != null) {
			segmentOverlays.add(segmentOverlay);
			// previous segment overlay
			// => isFirst = true (retained-n, removed-n, removed-s,
			// constrained-s)
			// constrain selected
			getBendOperation().getNewAnchors().set(
					segmentOverlay.constrainedSelectedHandle.explicitAnchorIndex,
					segmentOverlay.constrainedSelectedNewAnchor);
			// remove selected
			int index = segmentOverlay.removedSelectedHandle.explicitAnchorIndex;
			getBendOperation().getNewAnchors().remove(index);
			explicitAnchors.remove(index);
			// remove neighbor
			index = segmentOverlay.removedNeighborHandle.explicitAnchorIndex;
			getBendOperation().getNewAnchors().remove(index);
			explicitAnchors.remove(index);
			// retain neighbor
			index = segmentOverlay.retainedNeighborHandle.explicitAnchorIndex;
			getBendOperation().getNewAnchors().set(index,
					segmentOverlay.retainedNeighborNewAnchor);
			// adjust indices of anchor handles
			for (int j = 0; j < explicitAnchors.size(); j++) {
				explicitAnchors.get(j).explicitAnchorIndex = j;
			}
		} else {
			segmentOverlay = getSegmentOverlayRight();
			if (segmentOverlay != null) {
				segmentOverlays.add(segmentOverlay);
				// next segment overlay
				// => isFirst = false (constrained-s, removed-s, removed-n,
				// retained-n)
				// retain neighbor
				int index = segmentOverlay.retainedNeighborHandle.explicitAnchorIndex;
				getBendOperation().getNewAnchors().set(index,
						segmentOverlay.retainedNeighborNewAnchor);
				// remove neighbor
				index = segmentOverlay.removedNeighborHandle.explicitAnchorIndex;
				getBendOperation().getNewAnchors().remove(index);
				explicitAnchors.remove(index);
				// remove selected
				index = segmentOverlay.removedSelectedHandle.explicitAnchorIndex;
				getBendOperation().getNewAnchors().remove(index);
				explicitAnchors.remove(index);
				// constrain selected
				getBendOperation().getNewAnchors().set(
						segmentOverlay.constrainedSelectedHandle.explicitAnchorIndex,
						segmentOverlay.constrainedSelectedNewAnchor);
				// adjust indices of anchor handles
				for (int j = 0; j < explicitAnchors.size(); j++) {
					explicitAnchors.get(j).explicitAnchorIndex = j;
				}
			}
		}
		if (!segmentOverlays.isEmpty()) {
			locallyExecuteOperation();
		}
	}

	private void restoreRemoved() {
		// restore segment overlays
		while (!segmentOverlays.isEmpty()) {
			SegmentOverlay segmentOverlay = segmentOverlays.remove(0);

			if (segmentOverlay.isNeighborsFirst) {
				// left overlay (retained-n, removed-n, removed-s,
				// constrained-s)
				// restore constrained selected
				getBendOperation().getNewAnchors().set(
						segmentOverlay.constrainedSelectedHandle.explicitAnchorIndex,
						segmentOverlay.constrainedSelectedOldAnchor);
				// re-add removed selected
				getBendOperation().getNewAnchors().add(
						segmentOverlay.constrainedSelectedHandle.explicitAnchorIndex,
						segmentOverlay.removedSelectedOldAnchor);
				explicitAnchors.add(
						segmentOverlay.constrainedSelectedHandle.explicitAnchorIndex,
						segmentOverlay.removedSelectedHandle);
				if (!getConnection().getRouter().isImplicitAnchor(
						segmentOverlay.removedNeighborOldAnchor)) {
					// re-add removed neighbor
					getBendOperation().getNewAnchors()
							.add(segmentOverlay.retainedNeighborHandle.explicitAnchorIndex
									+ 1,
							segmentOverlay.removedNeighborOldAnchor);
					explicitAnchors
							.add(segmentOverlay.retainedNeighborHandle.explicitAnchorIndex
									+ 1, segmentOverlay.removedNeighborHandle);
				}
				// restore retained neighbor
				getBendOperation().getNewAnchors().set(
						segmentOverlay.retainedNeighborHandle.explicitAnchorIndex,
						segmentOverlay.retainedNeighborOldAnchor);
				if (getConnection().getRouter().isImplicitAnchor(
						segmentOverlay.removedNeighborOldAnchor)) {
					getBendOperation().getNewAnchors().remove(
							segmentOverlay.retainedNeighborHandle.explicitAnchorIndex);
					explicitAnchors.remove(
							segmentOverlay.retainedNeighborHandle.explicitAnchorIndex);
				}
				// fix indices
				for (int i = 0; i < explicitAnchors.size(); i++) {
					explicitAnchors.get(i).explicitAnchorIndex = i;
				}
				locallyExecuteOperation();
			} else {
				// right overlay (constrained-s, removed-s, removed-n,
				// retained-n)
				// restore retained neighbor
				getBendOperation().getNewAnchors().set(
						segmentOverlay.retainedNeighborHandle.explicitAnchorIndex,
						segmentOverlay.retainedNeighborOldAnchor);
				if (getConnection().getRouter().isImplicitAnchor(
						segmentOverlay.removedNeighborOldAnchor)) {
					getBendOperation().getNewAnchors().remove(
							segmentOverlay.retainedNeighborHandle.explicitAnchorIndex);
					explicitAnchors.remove(
							segmentOverlay.retainedNeighborHandle.explicitAnchorIndex);
				}
				if (!getConnection().getRouter().isImplicitAnchor(
						segmentOverlay.removedNeighborOldAnchor)) {
					// re-add removed neighbor
					getBendOperation().getNewAnchors().add(
							segmentOverlay.retainedNeighborHandle.explicitAnchorIndex,
							segmentOverlay.removedNeighborOldAnchor);
					explicitAnchors.add(
							segmentOverlay.retainedNeighborHandle.explicitAnchorIndex,
							segmentOverlay.removedNeighborHandle);
				}
				// re-add removed selected
				getBendOperation()
						.getNewAnchors().add(
								segmentOverlay.constrainedSelectedHandle.explicitAnchorIndex
										+ 1,
								segmentOverlay.removedSelectedOldAnchor);
				explicitAnchors
						.add(segmentOverlay.constrainedSelectedHandle.explicitAnchorIndex
								+ 1, segmentOverlay.removedSelectedHandle);
				// restore constrained selected
				getBendOperation().getNewAnchors().set(
						segmentOverlay.constrainedSelectedHandle.explicitAnchorIndex,
						segmentOverlay.constrainedSelectedOldAnchor);
				// fix indices
				for (int i = 0; i < explicitAnchors.size(); i++) {
					explicitAnchors.get(i).explicitAnchorIndex = i;
				}
				locallyExecuteOperation();

				// debug output
				// List<IAnchor> newAnchors =
				// getBendOperation().getNewAnchors();
				// String anchorsAfterRightOverlayRestore = "";
				// for (int i = 0, j = 0; i < getConnection().getAnchors()
				// .size(); i++) {
				// IAnchor anchor = getConnection().getAnchor(i);
				// if (!getConnection().getRouter().isImplicitAnchor(anchor)) {
				// anchorsAfterRightOverlayRestore =
				// anchorsAfterRightOverlayRestore
				// + (selectedAnchors.contains(
				// explicitAnchors.get(j)) ? "(*)" : " * ")
				// + anchor.getClass().toString() + "["
				// + getConnection().getPoint(i) + "]" + " ("
				// + newAnchors.get(j) + ") {"
				// + explicitAnchors.get(j) + "},\n";
				// j++;
				// }
				// }
				// System.out.println("anchors after right overlay restore:\n"
				// + anchorsAfterRightOverlayRestore);
			}
		}

		// restore point overlays
		while (!pointOverlays.isEmpty()) {
			PointOverlay pointOverlay = pointOverlays.remove(0);
			AnchorHandle replacementHandle = pointOverlay.replacement;
			// add the removed anchor
			int insertionIndex = pointOverlay.wasBefore
					? replacementHandle.explicitAnchorIndex
					: replacementHandle.explicitAnchorIndex + 1;
			getBendOperation().getNewAnchors().add(insertionIndex,
					pointOverlay.removed);
			locallyExecuteOperation();
			explicitAnchors.add(insertionIndex, pointOverlay.selected);
			// update anchor indices
			for (int i = insertionIndex; i < explicitAnchors.size(); i++) {
				explicitAnchors.get(i).explicitAnchorIndex = i;
			}
		}
	}

	/**
	 * Selects the point specified by the given segment index and parameter for
	 * manipulation. Captures the initial position of the selected point and the
	 * related initial mouse location.
	 *
	 * @param explicitAnchorHandle
	 *            Index of the explicit anchor to select for manipulation.
	 */
	public void select(AnchorHandle explicitAnchorHandle) {
		checkInitialized();
		// save selected anchor handles
		selectedAnchors.add(explicitAnchorHandle);
	}

	@Override
	public String toString() {
		return "FXBendConnectionPolicy[host=" + getHost() + "]";
	}

}
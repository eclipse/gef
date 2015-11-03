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
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXConnection;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXBendOperation;
import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.operations.ChangeSelectionOperation;
import org.eclipse.gef4.mvc.operations.ForwardUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.SetRefreshVisualOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.Node;

/**
 * The {@link FXBendPolicy} can be used to manipulate the points constituting an
 * {@link FXConnection}, i.e. its start, way, and end points. Each point is
 * realized though an {@link IFXAnchor}, which may either be local to the
 * {@link FXConnection} (i.e. the anchor refers to the {@link FXConnection} as
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
public class FXBendPolicy extends AbstractPolicy<Node>
		implements ITransactional {

	/**
	 * The overlay threshold, i.e. the distance between two points so that they
	 * are regarded as overlying.
	 */
	protected static final double DEFAULT_OVERLAY_THRESHOLD = 10;

	/**
	 * Stores the <i>initialized</i> flag for this policy, i.e.
	 * <code>true</code> after {@link #init()} was called, and
	 * <code>false</code> after {@link #commit()} was called, respectively.
	 */
	protected boolean initialized;

	private IFXAnchor removedOverlainAnchor;
	private int removedOverlainAnchorIndex;

	private int selectedPointIndex;
	private Point selectedPointInitialPositionInLocal;
	private int selectedPointIndexBeforeOverlaidRemoval;

	private Point initialMousePositionInScene;

	/**
	 * The bend operation that is currently used.
	 */
	protected FXBendOperation bendOperation;

	/**
	 * Returns <code>true</code> if the currently modified start, end, or way
	 * point can be connected, i.e. realized by an anchor that is not anchored
	 * to the {@link FXConnection} itself (see {@link IFXAnchor#getAnchorage()}
	 * ), but provided through a {@link IVisualPart}'s anchor provider (i.e. a
	 * {@link Provider}&lt;{@link IFXAnchor}&gt; adapted to the
	 * {@link IVisualPart}). Otherwise returns <code>false</code>. Per default,
	 * only the start and the end point can be attached.
	 *
	 * @param pointIndex
	 *            The index of the currently modified connection point.
	 *
	 * @return <code>true</code> if the currently modified point can be realized
	 *         through an {@link IFXAnchor} not anchored on the
	 *         {@link FXConnection}. Otherwise returns <code>false</code>.
	 *
	 * @see FXConnection#isStartConnected()
	 * @see FXConnection#isWayConnected(int)
	 * @see FXConnection#isEndConnected()
	 *
	 */
	protected boolean canConnect(int pointIndex) {
		// up to now, only allow attaching start and end point.
		return pointIndex == 0
				|| pointIndex == bendOperation.getNewAnchors().size() - 1;
	}

	@Override
	public ITransactionalOperation commit() {
		if (!initialized) {
			return null;
		}
		// after commit, we need to be re-initialized
		initialized = false;

		if (bendOperation != null && !bendOperation.isNoOp()) {
			// get current selection
			IViewer<Node> viewer = getHost().getRoot().getViewer();
			SelectionModel<Node> selectionModel = viewer
					.<SelectionModel<Node>> getAdapter(SelectionModel.class);
			List<IContentPart<Node, ? extends Node>> selection = selectionModel
					.getSelection();

			// get selection without host
			List<IContentPart<Node, ? extends Node>> selectionWithoutHost = new ArrayList<IContentPart<Node, ? extends Node>>(
					selectionModel.getSelection());
			selectionWithoutHost.remove(getHost());

			// build "deselect host" operation
			ChangeSelectionOperation<Node> deselectOperation = new ChangeSelectionOperation<Node>(
					viewer, selection, selectionWithoutHost);

			// build "select host" operation
			ChangeSelectionOperation<Node> selectOperation = new ChangeSelectionOperation<Node>(
					viewer, selectionWithoutHost, selection);

			// assemble deselect and select operations to form a reselect
			ReverseUndoCompositeOperation reselectOperation = new ReverseUndoCompositeOperation(
					"re-select");
			reselectOperation.add(deselectOperation);
			reselectOperation.add(selectOperation);

			// assemble visual and reselect operations to form an update
			ForwardUndoCompositeOperation updateOperation = new ForwardUndoCompositeOperation(
					bendOperation.getLabel());
			updateOperation.add(bendOperation);
			updateOperation.add(reselectOperation);

			// guard the update operation from model refreshes
			ReverseUndoCompositeOperation guardedUpdateOperation = new ReverseUndoCompositeOperation(
					bendOperation.getLabel());
			guardedUpdateOperation.add(new SetRefreshVisualOperation<Node>(
					getHost(), getHost().isRefreshVisual(), false));
			guardedUpdateOperation.add(updateOperation);
			guardedUpdateOperation.add(new SetRefreshVisualOperation<Node>(
					getHost(), false, getHost().isRefreshVisual()));

			return guardedUpdateOperation;
		}
		return null;
	}

	/**
	 * Creates a new point at the given segment. The new way point is then
	 * selected for further manipulation.
	 *
	 * @param segmentIndex
	 *            The index of the segment for which a new way point is created.
	 * @param mouseInScene
	 *            The mouse position in scene coordinates.
	 */
	public void createAndSelectPoint(int segmentIndex, Point mouseInScene) {
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}

		// create new way point
		Point mouseInLocal = JavaFX2Geometry.toPoint(getConnection()
				.sceneToLocal(Geometry2JavaFX.toFXPoint(mouseInScene)));
		bendOperation.getNewAnchors().add(segmentIndex + 1,
				createUnconnectedAnchor(mouseInLocal));

		locallyExecuteOperation();

		// select newly created way point
		selectPoint(segmentIndex + 1, 0, mouseInScene);
	}

	/**
	 * Creates an (unconnected) anchor (i.e. one anchored on the
	 * {@link FXConnection}) for the given position (in scene coordinates).
	 *
	 * @param selectedPointCurrentPositionInLocal
	 *            The location in local coordinates of the connection
	 * @return An {@link IFXAnchor} that yields the given position.
	 */
	protected IFXAnchor createUnconnectedAnchor(
			Point selectedPointCurrentPositionInLocal) {
		return new FXStaticAnchor(getConnection(),
				selectedPointCurrentPositionInLocal);
	}

	/**
	 * Determines the {@link IFXAnchor} that should replace the anchor of the
	 * currently selected point. If the point can connect, the
	 * {@link IVisualPart} at the mouse position is queried for an
	 * {@link IFXAnchor} via a {@link Provider}&lt;{@link IFXAnchor}&gt;
	 * adapter. Otherwise an (unconnected) anchor is create using
	 * {@link #createUnconnectedAnchor(Point)} .
	 *
	 * @param positionInLocal
	 *            A position in local coordinates of the connection.
	 * @param canConnect
	 *            <code>true</code> if the point can be attached to an
	 *            underlying {@link IVisualPart}, otherwise <code>false</code>.
	 * @return The {@link IFXAnchor} that replaces the anchor of the currently
	 *         modified point.
	 */
	@SuppressWarnings("serial")
	protected IFXAnchor findOrCreateAnchor(Point positionInLocal,
			boolean canConnect) {
		IFXAnchor anchor = null;
		// try to find an anchor that is provided from an underlying node
		if (canConnect) {
			Point selectedPointCurrentPositionInScene = JavaFX2Geometry
					.toPoint(getConnection().localToScene(
							Geometry2JavaFX.toFXPoint(positionInLocal)));
			List<Node> pickedNodes = FXUtils.getNodesAt(
					getHost().getRoot().getVisual(),
					selectedPointCurrentPositionInScene.x,
					selectedPointCurrentPositionInScene.y);
			IVisualPart<Node, ? extends Node> anchorPart = getAnchorPart(
					getParts(pickedNodes));
			if (anchorPart != null) {
				// use anchor returned by part
				anchor = anchorPart.getAdapter(
						new TypeToken<Provider<? extends IFXAnchor>>() {
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
			Provider<? extends IFXAnchor> anchorProvider = part
					.getAdapter(new TypeToken<Provider<? extends IFXAnchor>>() {
					});
			if (anchorProvider != null && anchorProvider.get() != null) {
				return part;
			}
		}
		return null;
	}

	/**
	 * Returns the {@link FXConnection} that is manipulated by this policy.
	 *
	 * @return The {@link FXConnection} that is manipulated by this policy.
	 */
	protected FXConnection getConnection() {
		return (FXConnection) getHost().getVisual();
	}

	/**
	 * Computes the mouse movement delta (w.r.t. to the initial mouse position)
	 * in local coordinates .
	 *
	 * @param currentMousePositionInScene
	 *            The current mouse position in scene coordinates.
	 * @return The movement delta, translated into local coordinates of the
	 *         connection
	 *
	 */
	// TODO: extract to somewhere else (this is used in several places)
	protected Point getMouseDeltaInLocal(Point currentMousePositionInScene) {
		Point mouseInLocal = JavaFX2Geometry
				.toPoint(getConnection().sceneToLocal(Geometry2JavaFX
						.toFXPoint(currentMousePositionInScene)));
		// compensate the movement of the local coordinate system w.r.t. the
		// scene coordinate system (the scene coordinate system stays consistent
		// w.r.t. to mouse movement)
		Point deltaInLocal = mouseInLocal
				.getTranslated(JavaFX2Geometry
						.toPoint(getConnection().sceneToLocal(Geometry2JavaFX
								.toFXPoint(initialMousePositionInScene)))
				.getNegated());
		return deltaInLocal;
	}

	/**
	 * If the point at the given index is overlain by the currently selected
	 * point, i.e. their distance is smaller than the
	 * {@link #getOverlayThreshold() overlay threshold} and they are above the
	 * same anchorage, returns the anchor that can be found at the candidate
	 * location.
	 *
	 * @param candidateIndex
	 *            The candidate index.
	 * @param mouseInScene
	 *            The current mouse position in scene coordinates.
	 *
	 * @return The overlaid {@link IFXAnchor} to be used for the currently
	 *         selected point
	 */
	protected IFXAnchor getOverlayAnchor(int candidateIndex,
			Point mouseInScene) {
		Point candidateLocation = null;

		// TODO: provide getPoint(int index) in FXConnection
		FXConnection connection = getConnection();
		if (candidateIndex == 0) {
			candidateLocation = connection.getStartPoint();
		} else if (candidateIndex == connection.getWayAnchorsSize() + 1) {
			candidateLocation = connection.getEndPoint();
		} else {
			candidateLocation = connection.getWayPoint(candidateIndex - 1);
		}

		// overlay if distance is small enough and we do not change the
		// anchorage
		Point selectedPointCurrentPositionInLocal = this.selectedPointInitialPositionInLocal
				.getTranslated(getMouseDeltaInLocal(mouseInScene));
		if (candidateLocation.getDistance(
				selectedPointCurrentPositionInLocal) >= getOverlayThreshold()) {
			return null;
		}

		IFXAnchor candidateAnchor = findOrCreateAnchor(
				selectedPointCurrentPositionInLocal, true);
		if (connection.getAnchors().get(candidateIndex)
				.getAnchorage() == candidateAnchor.getAnchorage()) {
			return connection.getAnchors().get(candidateIndex);
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
		List<IContentPart<Node, ? extends Node>> parts = new ArrayList<IContentPart<Node, ? extends Node>>();

		Map<Node, IVisualPart<Node, ? extends Node>> partMap = getHost()
				.getRoot().getViewer().getVisualPartMap();
		for (Node node : nodesUnderMouse) {
			if (partMap.containsKey(node)) {
				IVisualPart<Node, ? extends Node> part = partMap.get(node);
				if (part instanceof IContentPart) {
					parts.add((IContentPart<Node, ? extends Node>) part);
				}
			}
		}
		return parts;
	}

	/**
	 * Returns the initial position of the currently selected point in the local
	 * coordinate system of the {@link FXConnection}.
	 *
	 * @return The initial position in the local coordinate system of the
	 *         {@link FXConnection}.
	 *
	 * @see #selectPoint(int, double, Point)
	 */
	protected Point getSelectedPointInitialPositionInLocal() {
		return selectedPointInitialPositionInLocal;
	}

	/**
	 * Handles the hiding of an overlain point as well as the expose of a
	 * previously overlain point.
	 * <ol>
	 * <li>Restores a point that was previously removed because it was overlaid.
	 * </li>
	 * <li>Checks if the currently modified point overlays another point of the
	 * {@link FXConnection}. The overlaid point is removed and saved so that it
	 * can be restored later.</li>
	 * </ol>
	 *
	 * @param mouseInScene
	 *            The current mouse position in scene coordinates.
	 */
	protected void handleOverlay(Point mouseInScene) {
		// put removed back in (may be removed againg before returning)
		if (removedOverlainAnchor != null) {
			selectedPointIndex = selectedPointIndexBeforeOverlaidRemoval;
			bendOperation.getNewAnchors().add(removedOverlainAnchorIndex,
					removedOverlainAnchor);
			locallyExecuteOperation();
			removedOverlainAnchor = null;
		}

		// do not remove overlaid if there are no way points
		if (bendOperation.getNewAnchors().size() <= 2) {
			return;
		}

		removedOverlainAnchorIndex = -1;
		selectedPointIndexBeforeOverlaidRemoval = selectedPointIndex;
		IFXAnchor overlayAnchor = null;

		// determine if left neighbor is overlain (and can be removed)
		if (selectedPointIndex > 0) {
			int candidateIndex = selectedPointIndex - 1;
			overlayAnchor = getOverlayAnchor(candidateIndex, mouseInScene);
			if (overlayAnchor != null) {
				// remove previous (in case of start point, ensure we stay
				// anchored to the same anchorage)
				removedOverlainAnchorIndex = candidateIndex;
				selectedPointIndex--;
			}
		}

		// if left neighbor is not overlain (and not removed), determine if
		// right neighbor is overlain (and can be removed)
		if (removedOverlainAnchorIndex == -1
				&& selectedPointIndex < bendOperation.getNewAnchors().size()
						- 1) {
			int candidateIndex = selectedPointIndex + 1;
			overlayAnchor = getOverlayAnchor(candidateIndex, mouseInScene);
			if (overlayAnchor != null) {
				// remove next (in case of end point, ensure we stay
				// anchored to the same anchorage)
				removedOverlainAnchorIndex = candidateIndex;
			}
		}

		// remove neighbor if overlaid
		if (removedOverlainAnchorIndex != -1) {
			bendOperation.getNewAnchors().set(
					selectedPointIndexBeforeOverlaidRemoval, overlayAnchor);
			removedOverlainAnchor = bendOperation.getNewAnchors()
					.remove(removedOverlainAnchorIndex);
			locallyExecuteOperation();
		}
	}

	@Override
	public void init() {
		bendOperation = new FXBendOperation(getConnection());
		removedOverlainAnchor = null;
		removedOverlainAnchorIndex = -1;
		selectedPointIndex = -1;
		selectedPointIndexBeforeOverlaidRemoval = -1;
		selectedPointInitialPositionInLocal = null;
		initialized = true;
	}

	/**
	 * Locally executes the {@link FXBendOperation} that is updated by this
	 * policy, i.e. not on the operation history.
	 */
	protected void locallyExecuteOperation() {
		try {
			bendOperation.execute(null, null);
		} catch (ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Moves the currently selected point to the given mouse position in scene
	 * coordinates. Checks if the selected point overlays another point using
	 * {@link #handleOverlay(Point)}.
	 *
	 * @param mouseInScene
	 *            The current mouse position in scene coordinates.
	 */
	public void moveSelectedPoint(Point mouseInScene) {
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}
		if (selectedPointIndex < 0) {
			throw new IllegalStateException("No point was selected.");
		}

		// update position
		Point selectedPointCurrentPositionInLocal = this.selectedPointInitialPositionInLocal
				.getTranslated(getMouseDeltaInLocal(mouseInScene));

		// snap-to-grid
		// TODO: make snapping (0.5) configurable
		Dimension snapToGridOffset = FXTransformPolicy.getSnapToGridOffset(
				getHost().getRoot().getViewer().<GridModel> getAdapter(
						GridModel.class),
				selectedPointCurrentPositionInLocal.x,
				selectedPointCurrentPositionInLocal.y, 0.5, 0.5);
		selectedPointCurrentPositionInLocal
				.translate(snapToGridOffset.getNegated());

		bendOperation.getNewAnchors().set(selectedPointIndex,
				findOrCreateAnchor(selectedPointCurrentPositionInLocal,
						canConnect(selectedPointIndex)));

		locallyExecuteOperation();
		handleOverlay(mouseInScene);
	}

	/**
	 * Selects the point specified by the given segment index and parameter for
	 * manipulation. Captures the initial position of the selected point (see
	 * {@link #getSelectedPointInitialPositionInLocal()}) and the related
	 * initial mouse location.
	 *
	 * @param segmentIndex
	 *            The index of the segment of which a point is to be
	 *            manipulated.
	 * @param segmentParameter
	 *            The parameter on the segment to identify if its the end point.
	 * @param mouseInScene
	 *            The current mouse position in scene coordinates.
	 */
	public void selectPoint(int segmentIndex, double segmentParameter,
			Point mouseInScene) {
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}

		// store handle part information
		if (segmentParameter == 1) {
			selectedPointIndex = segmentIndex + 1;
		} else {
			selectedPointIndex = segmentIndex;
		}

		initialMousePositionInScene = mouseInScene.getCopy();
		selectedPointInitialPositionInLocal = bendOperation.getConnection()
				.getPoints()[selectedPointIndex];
	}

	@Override
	public String toString() {
		return "FXBendPolicy[host=" + getHost() + "]";
	}

}
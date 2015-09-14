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
 * {@link FXConnection}, i.e. its start point, way points, and end point. When
 * moving a point the policy takes care of:
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

	private int currentAnchorIndex;
	private int currentAnchorIndexBeforeOverlaidRemoval;

	// operation
	private FXBendOperation op;
	private Point initialMousePositionInScene;
	private Point initialReferencePositionInLocal;

	/**
	 * Returns <code>true</code> if the currently modified point can be attached
	 * to an {@link IVisualPart}. Otherwise returns <code>false</code>. Per
	 * default, only the start and the end point can be attached.
	 *
	 * @return <code>true</code> if the currently modified point can be attached
	 *         to an {@link IVisualPart}. Otherwise returns <code>false</code>.
	 */
	protected boolean canAttach() {
		// up to now, only allow attaching start and end point.
		return currentAnchorIndex == 0
				|| currentAnchorIndex == op.getNewAnchors().size() - 1;
	}

	@Override
	public ITransactionalOperation commit() {
		if (!initialized) {
			return null;
		}
		// after commit, we need to be re-initialized
		initialized = false;

		if (op != null && !op.isNoOp()) {
			// get current selection
			IViewer<Node> viewer = getHost().getRoot().getViewer();
			SelectionModel<Node> selectionModel = viewer
					.<SelectionModel<Node>> getAdapter(SelectionModel.class);
			List<IContentPart<Node, ? extends Node>> selection = selectionModel
					.getSelected();

			// get selection without host
			List<IContentPart<Node, ? extends Node>> selectionWithoutHost = new ArrayList<IContentPart<Node, ? extends Node>>(
					selectionModel.getSelected());
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
					op.getLabel());
			updateOperation.add(op);
			updateOperation.add(reselectOperation);

			// guard the update operation from model refreshes
			ReverseUndoCompositeOperation guardedUpdateOperation = new ReverseUndoCompositeOperation(
					op.getLabel());
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
	 * Creates a new way point at the given segment. The new way point is then
	 * selected for further manipulation.
	 *
	 * @param segmentIndex
	 *            The index of the segment for which a new way point is created.
	 * @param mouseInScene
	 *            The mouse position in scene coordinates.
	 */
	public void createAndSelectSegmentPoint(int segmentIndex,
			Point mouseInScene) {
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}

		// create new way point
		op.getNewAnchors().add(segmentIndex + 1,
				generateStaticAnchor(mouseInScene));

		locallyExecuteOperation();

		// select newly created way point
		selectSegmentPoint(segmentIndex + 1, 0, mouseInScene);
	}

	/**
	 * Determines the {@link IFXAnchor} that replaces the anchor of the
	 * currently modified point. If the point can be attached to an underlying
	 * anchor, then the {@link IVisualPart} at the mouse position is queried for
	 * an {@link IFXAnchor}. Otherwise a static anchor is generated using
	 * {@link #generateStaticAnchor(Point)}.
	 *
	 * @param currentReferencePositionInScene
	 *            The mouse position in scene coordinates.
	 * @param canAttach
	 *            <code>true</code> if the point can be attached to an
	 *            underlying {@link IVisualPart}, otherwise <code>false</code>.
	 * @return The {@link IFXAnchor} that replaces the anchor of the currently
	 *         modified point.
	 */
	@SuppressWarnings("serial")
	protected IFXAnchor findAnchor(Point currentReferencePositionInScene,
			boolean canAttach) {
		IFXAnchor anchor = null;
		// try to find an anchor that is provided from an underlying node
		if (canAttach) {
			List<Node> pickedNodes = FXUtils.getNodesAt(
					getHost().getRoot().getVisual(),
					currentReferencePositionInScene.x,
					currentReferencePositionInScene.y);
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
			anchor = generateStaticAnchor(currentReferencePositionInScene);
		}
		return anchor;
	}

	/**
	 * Generates an {@link FXStaticAnchor} that yields the given position (in
	 * scene coordinates).
	 *
	 * @param scene
	 *            The static position in scene coordinates.
	 * @return An {@link FXStaticAnchor} that yields the given position.
	 */
	protected IFXAnchor generateStaticAnchor(Point scene) {
		return new FXStaticAnchor(getConnection(), JavaFX2Geometry
				.toPoint(getConnection().sceneToLocal(scene.x, scene.y)));
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
	 * Returns the initial mouse position in scene coordinates.
	 *
	 * @return The initial mouse position in scene coordinates.
	 */
	protected Point getInitialMousePositionInScene() {
		return initialMousePositionInScene;
	}

	/**
	 * Returns the initial reference position in the local coordinate system of
	 * the {@link FXConnection} that is manipulated by this policy.
	 *
	 * @return The initial reference position in the local coordinate system of
	 *         the {@link FXConnection}.
	 */
	protected Point getInitialReferencePositionInLocal() {
		return initialReferencePositionInLocal;
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
	 * <ol>
	 * <li>Restores a point that was previously removed because it was overlaid.
	 * <li>Checks if the currently modified point overlays another point of the
	 * {@link FXConnection}. The overlaid point is removed and saved so that it
	 * can be restored later.
	 * </ol>
	 *
	 * @param currentPositionInScene
	 *            The current mouse position in scene coordinates.
	 */
	protected void hideShowOverlain(Point currentPositionInScene) {
		// put removed back in (may be removed againg before returning)
		if (removedOverlainAnchor != null) {
			currentAnchorIndex = currentAnchorIndexBeforeOverlaidRemoval;
			op.getNewAnchors().add(removedOverlainAnchorIndex,
					removedOverlainAnchor);
			locallyExecuteOperation();
			removedOverlainAnchor = null;
		}

		// do not remove overlaid if there are no way points
		if (op.getNewAnchors().size() <= 2) {
			return;
		}

		removedOverlainAnchorIndex = -1;
		currentAnchorIndexBeforeOverlaidRemoval = currentAnchorIndex;

		// determine if right neighbor is overlain (and can be removed)
		if (currentAnchorIndex > 0) {
			int candidateIndex = currentAnchorIndex - 1;
			if (isOverlain(candidateIndex, currentAnchorIndex,
					currentPositionInScene)) {
				// remove previous (in case of start point, ensure we stay
				// anchored to the same anchorage)
				removedOverlainAnchorIndex = candidateIndex;
				currentAnchorIndex--;
			}
		}
		// if left neighbor is not overlain (and not removed), determine if
		// right neighbor is overlain (and can be removed)
		if (removedOverlainAnchorIndex == -1
				&& currentAnchorIndex < op.getNewAnchors().size() - 1) {
			int candidateIndex = currentAnchorIndex + 1;
			if (isOverlain(candidateIndex, currentAnchorIndex,
					currentPositionInScene)) {
				// remove next (in case of end point, ensure we stay
				// anchored to the same anchorage)
				removedOverlainAnchorIndex = candidateIndex;
			}
		}

		// remove neighbor if overlaid
		if (removedOverlainAnchorIndex != -1) {
			removedOverlainAnchor = op.getNewAnchors()
					.remove(removedOverlainAnchorIndex);
			locallyExecuteOperation();
		}
	}

	@Override
	public void init() {
		op = new FXBendOperation(getConnection());
		removedOverlainAnchor = null;
		initialized = true;
	}

	/**
	 * Returns <code>true</code> if the points given by the specified indices
	 * are overlaying, i.e. their distance is smaller than the
	 * {@link #getOverlayThreshold() overlay threshold} and they are above the
	 * same anchorage. Otherwise returns <code>false</code>. If the points are
	 * overlaying, then the anchor at the current index is replaced by the
	 * anchor that can be found at the candidate location. The point at the
	 * candidate index is later on removed within
	 * {@link #hideShowOverlain(Point)}.
	 *
	 * @param candidateIndex
	 *            The candidate index.
	 * @param currentIndex
	 *            The currently modified index.
	 * @param currentReferencePositionInScene
	 *            The current reference position in scene coordinates.
	 * @return <code>true</code> if the specified points are overlying,
	 *         otherwise <code>false</code>.
	 */
	protected boolean isOverlain(int candidateIndex, int currentIndex,
			Point currentReferencePositionInScene) {
		Point candidateLocation = null;
		if (candidateIndex == 0) {
			candidateLocation = op.getConnection().getStartPoint();
		} else
			if (candidateIndex == op.getConnection().getWayAnchorsSize() + 1) {
			candidateLocation = op.getConnection().getEndPoint();
		} else {
			candidateLocation = op.getConnection()
					.getWayPoint(candidateIndex - 1);
		}
		// overlay if distance is small enough and we do not change the
		// anchorage
		IFXAnchor candidateAnchor = findAnchor(currentReferencePositionInScene,
				true);
		Point currentPoint = JavaFX2Geometry.toPoint(getConnection()
				.getCurveNode().sceneToLocal(currentReferencePositionInScene.x,
						currentReferencePositionInScene.y));

		boolean overlay = candidateLocation
				.getDistance(currentPoint) < getOverlayThreshold()
				&& op.getConnection().getAnchors().get(candidateIndex)
						.getAnchorage() == candidateAnchor.getAnchorage();
		if (overlay) {
			// exchange current anchor
			op.getNewAnchors().set(currentIndex, candidateAnchor);
			locallyExecuteOperation();
		}
		return overlay;
	}

	/**
	 * Locally executes the {@link FXBendOperation} that is updated by this
	 * policy, i.e. not on the operation history.
	 */
	protected void locallyExecuteOperation() {
		try {
			op.execute(null, null);
		} catch (ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Moves the currently selected point to the given mouse position in scene
	 * coordinates. Checks if the selected point overlays another point using
	 * {@link #hideShowOverlain(Point)}.
	 *
	 * @param mouseInScene
	 *            The current mouse position in scene coordinates.
	 */
	public void moveSelectedSegmentPoint(Point mouseInScene) {
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}

		Point mouseInLocal = JavaFX2Geometry.toPoint(getConnection()
				.sceneToLocal(Geometry2JavaFX.toFXPoint(mouseInScene)));

		// compensate the movement of the local coordinate system w.r.t. the
		// scene coordinate system (the scene coordinate system stays consistent
		// w.r.t. to mouse movement)
		Point deltaInLocal = mouseInLocal
				.getTranslated(JavaFX2Geometry
						.toPoint(getConnection().sceneToLocal(Geometry2JavaFX
								.toFXPoint(initialMousePositionInScene)))
				.getNegated());

		Point currentReferencePositionInLocal = this.initialReferencePositionInLocal
				.getTranslated(deltaInLocal);

		// TODO: make snapping (0.5) configurable
		Dimension snapToGridOffset = FXTransformPolicy.getSnapToGridOffset(
				getHost().getRoot().getViewer().<GridModel> getAdapter(
						GridModel.class),
				currentReferencePositionInLocal.x,
				currentReferencePositionInLocal.y, 0.5, 0.5);

		Point currentReferencePositionInScene = JavaFX2Geometry
				.toPoint(getConnection().localToScene(Geometry2JavaFX.toFXPoint(
						currentReferencePositionInLocal.getTranslated(
								snapToGridOffset.getNegated()))));

		op.getNewAnchors().set(currentAnchorIndex,
				findAnchor(currentReferencePositionInScene, canAttach()));

		locallyExecuteOperation();
		hideShowOverlain(currentReferencePositionInScene);
	}

	/**
	 * Selects the point specified by the given segment index and parameter for
	 * manipulation.
	 *
	 * @param segmentIndex
	 *            The index of the segment of which a point is to be
	 *            manipulated.
	 * @param segmentParameter
	 *            The parameter on the segment to identify if its the end point.
	 * @param mouseInScene
	 *            The current mouse position in scene coordinates.
	 */
	public void selectSegmentPoint(int segmentIndex, double segmentParameter,
			Point mouseInScene) {
		if (!initialized) {
			throw new IllegalStateException("Not yet initialized!");
		}

		// store handle part information
		if (segmentParameter == 1) {
			currentAnchorIndex = segmentIndex + 1;
		} else {
			currentAnchorIndex = segmentIndex;
		}

		initialMousePositionInScene = mouseInScene.getCopy();
		initialReferencePositionInLocal = op.getConnection()
				.getPoints()[currentAnchorIndex];
	}

	@Override
	public String toString() {
		return "FXBendPolicy[host=" + getHost() + "]";
	}

}
/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
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
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.operations.SetRefreshVisualOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.AbstractPolicy;
import org.eclipse.gef4.mvc.viewer.IViewer;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

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
public class FXBendPolicy extends AbstractPolicy<Node> implements
		ITransactional {

	protected static final double DEFAULT_OVERLAY_THRESHOLD = 10;

	private IFXAnchor removedOverlainAnchor;
	private int removedOverlainAnchorIndex;

	private int currentAnchorIndex;
	private int currentAnchorIndexBeforeOverlaidRemoval;

	// operation
	private FXBendOperation op;

	private Point initialMousePositionInScene;

	private Point initialReferencePositionInLocal;

	protected boolean canAttach() {
		// up to now, only allow attaching start and end point.
		return currentAnchorIndex == 0
				|| currentAnchorIndex == op.getNewAnchors().size() - 1;
	}

	@Override
	public IUndoableOperation commit() {
		if (op != null) {
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

			// build "deselect anchorages" operation
			ChangeSelectionOperation<Node> deselectOperation = new ChangeSelectionOperation<Node>(
					viewer, selection, selectionWithoutHost);

			// build "select anchorages" operation
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
		return op;
	}

	public void createAndSelectSegmentPoint(int segmentIndex, Point mouseInScene) {
		// create new way point
		op.getNewAnchors().add(segmentIndex + 1,
				new FXStaticAnchor(mouseInScene));

		locallyExecuteOperation();

		// select newly created way point
		selectSegmentPoint(segmentIndex + 1, 0, mouseInScene);
	}

	@SuppressWarnings("serial")
	protected IFXAnchor findAnchor(Point currentReferencePositionInScene,
			boolean canAttach) {
		IFXAnchor anchor = null;
		// try to find an anchor that is provided from an underlying node
		if (canAttach) {
			List<Node> pickedNodes = FXUtils.getNodesAt(getHost().getRoot()
					.getVisual(), currentReferencePositionInScene.x,
					currentReferencePositionInScene.y);
			IVisualPart<Node, ? extends Node> anchorPart = getAnchorPart(getParts(pickedNodes));
			if (anchorPart != null) {
				// use anchor returned by part
				anchor = anchorPart.getAdapter(
						new TypeToken<Provider<? extends IFXAnchor>>() {
						}).get();
			}
		}
		if (anchor == null) {
			anchor = new FXStaticAnchor(currentReferencePositionInScene);
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

	protected FXConnection getConnection() {
		return (FXConnection) getHost().getVisual();
	}

	protected Point getInitialMousePositionInScene() {
		return initialMousePositionInScene;
	}

	protected Point getInitialReferencePositionInLocal() {
		return initialReferencePositionInLocal;
	}

	protected double getOverlayThreshold() {
		GridModel model = getHost().getRoot().getViewer()
				.getAdapter(GridModel.class);
		if (model != null && model.isSnapToGrid()) {
			return Math
					.min(model.getGridCellWidth(), model.getGridCellHeight()) / 4;
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
			removedOverlainAnchor = op.getNewAnchors().remove(
					removedOverlainAnchorIndex);
			locallyExecuteOperation();
		}
	}

	@Override
	public void init() {
		op = new FXBendOperation(getConnection());
		removedOverlainAnchor = null;
	}

	protected boolean isOverlain(int candidateIndex, int currentIndex,
			Point currentReferencePositionInScene) {
		Point candidateLocation = null;
		if (candidateIndex == 0) {
			candidateLocation = op.getConnection().getStartPoint();
		} else if (candidateIndex == op.getConnection().getWayAnchorsSize() + 1) {
			candidateLocation = op.getConnection().getEndPoint();
		} else {
			candidateLocation = op.getConnection().getWayPoint(
					candidateIndex - 1);
		}
		// overlay if distance is small enough and we do not change the
		// anchorage
		IFXAnchor candidateAnchor = findAnchor(currentReferencePositionInScene,
				true);
		// TODO: compensate that getStartPoint(), etc. of connection is
		// actually in the coordinate space of the curve node
		Point currentPoint = JavaFX2Geometry.toPoint(getConnection()
				.getCurveNode().sceneToLocal(currentReferencePositionInScene.x,
						currentReferencePositionInScene.y));

		boolean overlay = candidateLocation.getDistance(currentPoint) < getOverlayThreshold()
				&& op.getConnection().getAnchors().get(candidateIndex)
						.getAnchorage() == candidateAnchor.getAnchorage();
		if (overlay) {
			// exchange current anchor
			op.getNewAnchors().set(currentIndex, candidateAnchor);
			locallyExecuteOperation();
		}
		return overlay;
	}

	protected void locallyExecuteOperation() {
		try {
			op.execute(null, null);
		} catch (ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	public void moveSelectedSegmentPoint(Point mouseInScene) {
		Point mouseInLocal = JavaFX2Geometry.toPoint(getConnection()
				.sceneToLocal(Geometry2JavaFX.toFXPoint(mouseInScene)));

		// compensate the movement of the local coordinate system w.r.t. the
		// scene coordinate system (the scene coordinate system stays consistent
		// w.r.t. to mouse movement)
		Point deltaInLocal = mouseInLocal
				.getTranslated(JavaFX2Geometry
						.toPoint(
								getConnection()
										.sceneToLocal(
												Geometry2JavaFX
														.toFXPoint(initialMousePositionInScene)))
						.getNegated());

		Point currentReferencePositionInLocal = this.initialReferencePositionInLocal
				.getTranslated(deltaInLocal);

		// TODO: make snapping (0.5) configurable
		Dimension snapToGridOffset = FXResizeRelocatePolicy
				.getSnapToGridOffset(getHost().getRoot().getViewer()
						.<GridModel> getAdapter(GridModel.class),
						currentReferencePositionInLocal.x,
						currentReferencePositionInLocal.y, 0.5, 0.5);

		Point currentReferencePositionInScene = JavaFX2Geometry
				.toPoint(getConnection().localToScene(
						Geometry2JavaFX
								.toFXPoint(currentReferencePositionInLocal
										.getTranslated(snapToGridOffset
												.getNegated()))));

		op.getNewAnchors().set(currentAnchorIndex,
				findAnchor(currentReferencePositionInScene, canAttach()));

		locallyExecuteOperation();
		hideShowOverlain(currentReferencePositionInScene);
	}

	public void selectSegmentPoint(int segmentIndex, double segmentParameter,
			Point mouseInScene) {
		// store handle part information
		if (segmentParameter == 1) {
			currentAnchorIndex = segmentIndex + 1;
		} else {
			currentAnchorIndex = segmentIndex;
		}

		initialMousePositionInScene = mouseInScene.getCopy();
		initialReferencePositionInLocal = op.getConnection().getPoints()[currentAnchorIndex];
	}

	@Override
	public String toString() {
		return "FXBendPolicy[host=" + getHost() + "]";
	}

}
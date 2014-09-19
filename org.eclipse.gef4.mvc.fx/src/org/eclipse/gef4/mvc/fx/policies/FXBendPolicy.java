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
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.operations.FXBendOperation;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
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

	private Point initialPositionInScene;

	private Point initialPositionInLocal;

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
			List<IContentPart<Node>> selection = selectionModel.getSelected();

			// get selection without host
			List<IContentPart<Node>> selectionWithoutHost = new ArrayList<IContentPart<Node>>(
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

	protected IFXAnchor findAnchor(Point currentPositionInScene,
			boolean canAttach) {
		IFXAnchor anchor = null;
		// try to find an anchor that is provided from an underlying node
		if (canAttach) {
			List<Node> pickedNodes = FXUtils.getNodesAt(getHost().getRoot()
					.getVisual(), currentPositionInScene.x,
					currentPositionInScene.y);
			AbstractFXContentPart anchorPart = getAnchorPart(getParts(pickedNodes));
			if (anchorPart != null) {
				// use anchor returned by part
				anchor = anchorPart.getAnchor(getHost());
			}
		}
		if (anchor == null) {
			anchor = new FXStaticAnchor(currentPositionInScene);
		}
		return anchor;
	}

	private AbstractFXContentPart getAnchorPart(
			List<IContentPart<Node>> partsUnderMouse) {
		for (IContentPart<Node> cp : partsUnderMouse) {
			AbstractFXContentPart part = (AbstractFXContentPart) cp;
			IFXAnchor anchor = part.getAnchor(getHost());
			if (anchor != null) {
				return part;
			}
		}
		return null;
	}

	protected FXConnection getConnection() {
		return (FXConnection) getHost().getVisual();
	}

	protected double getOverlayThreshold() {
		return DEFAULT_OVERLAY_THRESHOLD;
	}

	private List<IContentPart<Node>> getParts(List<Node> nodesUnderMouse) {
		List<IContentPart<Node>> parts = new ArrayList<IContentPart<Node>>();

		Map<Node, IVisualPart<Node>> partMap = getHost().getRoot().getViewer()
				.getVisualPartMap();
		for (Node node : nodesUnderMouse) {
			if (partMap.containsKey(node)) {
				IVisualPart<Node> part = partMap.get(node);
				if (part instanceof IContentPart) {
					parts.add((IContentPart<Node>) part);
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
			Point currentPositionInScene) {
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
		IFXAnchor candidateAnchor = findAnchor(currentPositionInScene, true);
		// TODO: compensate that getStartPoint(), etc. of connection is
		// actually in the coordinate space of the curve node
		Point currentPoint = JavaFX2Geometry.toPoint(getConnection()
				.getCurveNode().sceneToLocal(currentPositionInScene.x,
						currentPositionInScene.y));

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
		Point deltaInLocal = mouseInLocal.getTranslated(JavaFX2Geometry
				.toPoint(
						getConnection().sceneToLocal(
								Geometry2JavaFX
										.toFXPoint(initialPositionInScene)))
				.getNegated());

		Point currentPositionInLocal = this.initialPositionInLocal
				.getTranslated(deltaInLocal);

		Point currentPositionInScene = JavaFX2Geometry
				.toPoint(getConnection().localToScene(
						Geometry2JavaFX.toFXPoint(currentPositionInLocal)));

		op.getNewAnchors().set(currentAnchorIndex,
				findAnchor(currentPositionInScene, canAttach()));

		locallyExecuteOperation();
		hideShowOverlain(currentPositionInScene);
	}

	public void selectSegmentPoint(int segmentIndex, double segmentParameter,
			Point mouseInScene) {
		// store handle part information
		if (segmentParameter == 1) {
			currentAnchorIndex = segmentIndex + 1;
		} else {
			currentAnchorIndex = segmentIndex;
		}

		initialPositionInScene = mouseInScene.getCopy();
		initialPositionInLocal = JavaFX2Geometry
				.toPoint(op.getConnection().sceneToLocal(
						Geometry2JavaFX.toFXPoint(initialPositionInScene)));
	}

	@Override
	public String toString() {
		return "FXBendPolicy[host=" + getHost() + "]";
	}

}
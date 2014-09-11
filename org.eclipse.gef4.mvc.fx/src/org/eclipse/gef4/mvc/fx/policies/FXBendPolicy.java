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

import javafx.geometry.Point2D;
import javafx.scene.Node;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.fx.anchors.FXStaticAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXConnection;
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
 */
public class FXBendPolicy extends AbstractPolicy<Node> implements
		ITransactional {

	// constants (TODO: make configurable)
	protected static final double REMOVE_THRESHOLD = 10;

	// interaction
	private Point2D startPointInScene;
	private Point startPoint;
	private Point currentPoint;
	private IFXAnchor removedAnchor;
	private int removedAnchorIndex;
	private int anchorIndex;
	private int oldAnchorIndex;

	// operation
	private FXBendOperation op;

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

	public void createWayPoint(int segmentIndex, Point mouseInScene) {
		FXConnection hostVisual = getConnection();
		Point2D mouseInLocal = hostVisual.sceneToLocal(mouseInScene.x,
				mouseInScene.y);

		// create new way point
		segmentIndex++;
		op.getNewAnchors().add(
				segmentIndex,
				new FXStaticAnchor(
						JavaFX2Geometry.toPoint(hostVisual.localToScene(
								mouseInLocal.getX(), mouseInLocal.getY()))));
		locallyExecuteOperation();

		// select newly created way point
		selectPoint(segmentIndex, 0, mouseInScene);
	}

	protected AbstractFXContentPart getAnchorPart(
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

	protected void hideShowOverlaid() {
		// put removed back in
		if (removedAnchor != null) {
			anchorIndex = oldAnchorIndex;
			op.getNewAnchors().add(removedAnchorIndex, removedAnchor);
			locallyExecuteOperation();
			removedAnchor = null;
		}

		// do not remove overlaid if there are no way points
		if (op.getNewAnchors().size() <= 2) {
			return;
		}

		removedAnchorIndex = -1;
		oldAnchorIndex = anchorIndex;

		// determine overlaid neighbor
		if (anchorIndex > 0) {
			int prevIndex = anchorIndex - 1;
			Point prevLocation = prevIndex == 0 ? op.getConnection()
					.getStartPoint() : op.getConnection().getWayPoint(
					prevIndex - 1);
			if (prevLocation.getDistance(currentPoint) < REMOVE_THRESHOLD) {
				// remove previous
				removedAnchorIndex = prevIndex;
				anchorIndex--;
			}
		}
		if (removedAnchorIndex == -1
				&& anchorIndex < op.getNewAnchors().size() - 1) {
			int nextIndex = anchorIndex + 1;
			Point nextLocation = nextIndex == op.getConnection()
					.getWayAnchorsSize() + 1 ? op.getConnection().getEndPoint()
					: op.getConnection().getWayPoint(nextIndex - 1);
			if (nextLocation.getDistance(currentPoint) < REMOVE_THRESHOLD) {
				// remove next
				removedAnchorIndex = nextIndex;
			}
		}

		// remove neighbor if overlaid
		if (removedAnchorIndex != -1) {
			removedAnchor = op.getNewAnchors().remove(removedAnchorIndex);
			locallyExecuteOperation();
		}
	}

	@Override
	public void init() {
		op = new FXBendOperation(getConnection());
		removedAnchor = null;
	}

	protected void locallyExecuteOperation() {
		try {
			op.execute(null, null);
		} catch (ExecutionException e) {
			throw new IllegalStateException(e);
		}
	}

	public void movePoint(Point mouseInScene,
			List<IContentPart<Node>> partsUnderMouse) {
		// update position
		Point2D mouseInLocal = getConnection().sceneToLocal(mouseInScene.x,
				mouseInScene.y);
		Point2D startPointInLocal = getConnection().sceneToLocal(
				startPointInScene);
		Point delta = new Point(mouseInLocal.getX() - startPointInLocal.getX(),
				mouseInLocal.getY() - startPointInLocal.getY());

		currentPoint.x = startPoint.x + delta.x;
		currentPoint.y = startPoint.y + delta.y;

		// update
		hideShowOverlaid();
		updateCurrentAnchorLink(mouseInScene, partsUnderMouse);
	}

	public void selectPoint(int segmentIndex, double segmentParameter,
			Point mouseInScene) {
		// store handle part information
		if (segmentParameter == 1) {
			anchorIndex = segmentIndex + 1;
		} else {
			anchorIndex = segmentIndex;
		}

		// initialize position
		startPointInScene = new Point2D(mouseInScene.x, mouseInScene.y);
		startPoint = anchorIndex == 0 ? op.getConnection().getStartPoint()
				: anchorIndex <= op.getConnection().getWayAnchorsSize() ? op
						.getConnection().getWayPoint(anchorIndex - 1) : op
						.getConnection().getEndPoint();
		currentPoint = startPoint.getCopy();
	}

	@Override
	public String toString() {
		return "FXMoveBendPointPolicy[host=" + getHost() + "]";
	}

	protected void updateCurrentAnchorLink(Point mouseInScene,
			List<IContentPart<Node>> partsUnderMouse) {
		// snaps for start and end (TODO: configurable)
		boolean snaps = anchorIndex == 0
				|| anchorIndex == op.getNewAnchors().size() - 1;

		IFXAnchor anchor = null;
		if (snaps) {
			AbstractFXContentPart anchorPart = getAnchorPart(partsUnderMouse);
			if (anchorPart != null) {
				// use anchor returned by part
				anchor = anchorPart.getAnchor(getHost());
			}
		}

		if (anchor == null) {
			// use static anchor, re-use key
			anchor = new FXStaticAnchor(JavaFX2Geometry.toPoint(getConnection()
					.localToScene(currentPoint.x, currentPoint.y)));
		}

		op.getNewAnchors().set(anchorIndex, anchor);
		locallyExecuteOperation();
	}

}
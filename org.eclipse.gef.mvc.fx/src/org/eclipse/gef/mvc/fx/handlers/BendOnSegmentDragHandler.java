/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
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
package org.eclipse.gef.mvc.fx.handlers;

import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polyline;
import org.eclipse.gef.mvc.fx.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.BendConnectionPolicy;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Uses the {@link BendConnectionPolicy} of its host to move the dragged
 * connection segment.
 */
public class BendOnSegmentDragHandler extends AbstractHandler
		implements IOnDragHandler {

	private SnapToSupport snapToSupport = null;
	private Point initialMouseInScene;
	private boolean isInvalid = false;
	private BendConnectionPolicy bendPolicy;

	@Override
	public void abortDrag() {
		if (isInvalid) {
			return;
		}
		rollback(bendPolicy);
		restoreRefreshVisuals(getHost());
		updateHandles();
		bendPolicy = null;
		if (snapToSupport != null) {
			snapToSupport.stopSnapping();
			snapToSupport = null;
		}
	}

	/**
	 * Returns the {@link BendConnectionPolicy} of the host.
	 *
	 * @return The {@link BendConnectionPolicy} of the host.
	 */
	protected BendConnectionPolicy determineBendPolicy() {
		return getHost().getAdapter(BendConnectionPolicy.class);
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		if (isInvalid) {
			return;
		}

		Point newEndPointInScene = new Point(e.getSceneX(), e.getSceneY());
		if (snapToSupport != null) {
			if (!isPrecise(e)) {
				newEndPointInScene.translate(snapToSupport.snap(delta));
			} else {
				snapToSupport.clearSnappingFeedback();
			}
		}

		// perform changes
		bendPolicy.move(initialMouseInScene, newEndPointInScene);
		updateHandles();
	}

	@Override
	public void endDrag(MouseEvent e, Dimension delta) {
		if (isInvalid) {
			return;
		}
		commit(bendPolicy);
		restoreRefreshVisuals(getHost());
		updateHandles();
		bendPolicy = null;
		if (snapToSupport != null) {
			snapToSupport.stopSnapping();
			snapToSupport = null;
		}
	}

	/**
	 * Returns the {@link BendConnectionPolicy} of the host.
	 *
	 * @return The {@link BendConnectionPolicy} of the host.
	 */
	protected BendConnectionPolicy getBendPolicy() {
		return bendPolicy;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IVisualPart<Connection> getHost() {
		return (IVisualPart<Connection>) super.getHost();
	}

	/**
	 * Returns the {@link SnapToSupport} that is used by this
	 * {@link BendOnSegmentDragHandler} to snap the dragged segment.
	 *
	 * @return The {@link SnapToSupport} that is used by this
	 *         {@link BendOnSegmentDragHandler}.
	 */
	protected SnapToSupport getSnapToSupport() {
		return snapToSupport;
	}

	@Override
	public void hideIndicationCursor() {
	}

	/**
	 * Returns <code>true</code> if the given {@link MouseEvent} should trigger
	 * bending. Otherwise returns <code>false</code>. Per default returns
	 * <code>true</code> if a single mouse click is performed.
	 *
	 * @param event
	 *            The {@link MouseEvent} in question.
	 * @return <code>true</code> if the given {@link MouseEvent} should trigger
	 *         focus and select, otherwise <code>false</code>.
	 */
	protected boolean isBend(MouseEvent event) {
		boolean isInvalid = false;
		if (!(getHost().getVisual().getRouter() instanceof OrthogonalRouter)) {
			// abort if non-orthogonal
			isInvalid = true;
		} else {
			IVisualPart<? extends Node> host = getHost();
			ObservableList<IContentPart<? extends Node>> selection = host
					.getRoot().getViewer().getAdapter(SelectionModel.class)
					.getSelectionUnmodifiable();
			if (selection.size() > 1 && selection.contains(host)) {
				// abort if part of multiple selection
				isInvalid = true;
			} else if (!getHost().getVisual().isStartConnected()
					&& !getHost().getVisual().isEndConnected()) {
				// abort if unconnected
				isInvalid = true;
			}
		}
		return !isInvalid;
	}

	/**
	 * Returns <code>true</code> if precise manipulations should be performed
	 * for the given {@link MouseEvent}. Otherwise returns <code>false</code>.
	 *
	 * @param e
	 *            The {@link MouseEvent} that is used to determine if precise
	 *            manipulations should be performed (i.e. if the corresponding
	 *            modifier key is pressed).
	 * @return <code>true</code> if precise manipulations should be performed,
	 *         <code>false</code> otherwise.
	 */
	protected boolean isPrecise(MouseEvent e) {
		return e.isShortcutDown();
	}

	/**
	 * Prepares the given {@link BendConnectionPolicy} for the manipulation of
	 * its host.
	 *
	 * @param bendPolicy
	 *            The {@link BendConnectionPolicy} that is prepared.
	 */
	private void prepareBend(BendConnectionPolicy bendPolicy) {
		// determine curve in scene coordinates
		Connection connection = bendPolicy.getHost().getVisual();

		// construct polyline for connection points
		Polyline polyline = new Polyline(
				connection.getPointsUnmodifiable().toArray(new Point[] {}));
		Polyline polylineInScene = (Polyline) NodeUtils.localToScene(connection,
				polyline);

		// determine pressed segment (nearest to mouse)
		Line[] segmentsInScene = polylineInScene.getCurves();
		double minDistance = -1;
		int segmentIndex = -1;
		for (int i = 0; i < segmentsInScene.length; i++) {
			Line segment = segmentsInScene[i];
			Point projection = segment.getProjection(initialMouseInScene);
			double distance = projection.getDistance(initialMouseInScene);
			if (minDistance < 0 || distance < minDistance) {
				minDistance = distance;
				segmentIndex = i;
			}
		}

		if (segmentIndex < 0) {
			// it is better to die than to return in failure
			throw new IllegalStateException("Cannot identify pressed segment.");
		}

		// select segment
		bendPolicy.selectSegment(segmentIndex);
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		// TODO: Show <|> or ^-v indication cursor for segment movement.
		// cursorSupport.storeAndReplaceCursor(verticalSegment ?
		// LEFT_RIGHT_CURSOR : TOP_DOWN_CURSOR);
		return false;
	}

	@Override
	public void startDrag(MouseEvent e) {
		isInvalid = !isBend(e);
		if (isInvalid) {
			return;
		}

		// save initial mouse position in scene coordinates
		initialMouseInScene = new Point(e.getSceneX(), e.getSceneY());

		// disable refresh visuals for the host
		storeAndDisableRefreshVisuals(getHost());

		bendPolicy = determineBendPolicy();
		init(bendPolicy);
		updateHandles();

		prepareBend(bendPolicy);
		// move initially so that the initial positions for the selected
		// points are computed
		bendPolicy.move(initialMouseInScene, initialMouseInScene);

		// query selected position
		List<Point> initialPositions = bendPolicy.getSelectedInitialPositions();
		Point startPositionInConnectionLocal = initialPositions.get(0);
		Point startPositionInScene = FX2Geometry.toPoint(getHost().getVisual()
				.localToScene(startPositionInConnectionLocal.x,
						startPositionInConnectionLocal.y));

		snapToSupport = getHost() instanceof IContentPart
				? getHost().getViewer().getAdapter(SnapToSupport.class) : null;
		if (snapToSupport != null) {
			// Only report HSL or VSL depending on segment orientation
			SnappingLocation sl = bendPolicy.isSelectionHorizontal()
					? new SnappingLocation(
							(IContentPart<? extends Connection>) getHost(),
							Orientation.VERTICAL, startPositionInScene.y)
					: new SnappingLocation(
							(IContentPart<? extends Connection>) getHost(),
							Orientation.HORIZONTAL, startPositionInScene.x);
			snapToSupport.startSnapping(
					(IContentPart<? extends Connection>) getHost(),
					Arrays.asList(sl));
		}
	}

	/**
	 * Updates the selection handles.
	 */
	protected void updateHandles() {
		getHost().getRoot().getAdapter(SelectionBehavior.class)
				.updateHandles(getHost(), null, null);
	}
}

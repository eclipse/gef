/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Polyline;
import org.eclipse.gef.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef.mvc.models.SelectionModel;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IVisualPart;

import com.google.common.reflect.TypeToken;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Uses the {@link FXBendConnectionPolicy} of its host to move the dragged
 * connection segment.
 */
public class FXBendOnSegmentDragPolicy extends AbstractFXInteractionPolicy
		implements IFXOnDragPolicy {

	private CursorSupport cursorSupport = new CursorSupport(this);
	private SnapSupport snapSupport = new SnapSupport(this);
	private Point initialMouseInScene;
	private boolean isInvalid = false;
	private boolean isPrepared;
	private FXBendConnectionPolicy bendPolicy;

	@Override
	public void abortDrag() {
		if (isInvalid) {
			return;
		}
		rollback(bendPolicy);
		restoreRefreshVisuals(getHost());
		updateHandles();
		bendPolicy = null;
	}

	/**
	 * Returns the {@link FXBendConnectionPolicy} of the host.
	 *
	 * @return The {@link FXBendConnectionPolicy} of the host.
	 */
	protected FXBendConnectionPolicy determineBendPolicy() {
		return getHost().getAdapter(FXBendConnectionPolicy.class);
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		if (isInvalid) {
			return;
		}

		// prepare for manipulation upon first drag
		if (!isPrepared) {
			isPrepared = true;
			prepareBend(bendPolicy);
			// move initially so that the initial positions for the selected
			// points are computed
			bendPolicy.move(initialMouseInScene, initialMouseInScene);
			// TODO: investigate why the following seems unnecessary:
			// 1. query selected position
			// 2. transform selected position to scene
		}
		// TODO: investigate why the following seems unnecessary:
		// 3. apply mouse-delta to selected-position-in-scene
		// 4. snap selected-position-in-scene unless precise
		// 5. call move(initial-position-in-scene, snapped-position-in-scene)

		// snap to grid
		Point newEndPointInScene = isPrecise(e)
				? new Point(e.getSceneX(), e.getSceneY())
				: snapSupport.snapToGrid(e.getSceneX(), e.getSceneY());

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
	}

	/**
	 * Returns the {@link FXBendConnectionPolicy} of the host.
	 *
	 * @return The {@link FXBendConnectionPolicy} of the host.
	 */
	protected FXBendConnectionPolicy getBendPolicy() {
		return bendPolicy;
	}

	/**
	 * Returns the {@link CursorSupport} of this policy.
	 *
	 * @return The {@link CursorSupport} of this policy.
	 */
	protected CursorSupport getCursorSupport() {
		return cursorSupport;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IVisualPart<Node, Connection> getHost() {
		return (IVisualPart<Node, Connection>) super.getHost();
	}

	@Override
	public void hideIndicationCursor() {
		cursorSupport.restoreCursor();
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
			IVisualPart<Node, ? extends Node> host = getHost();
			@SuppressWarnings("serial")
			ObservableList<IContentPart<Node, ? extends Node>> selection = host
					.getRoot().getViewer()
					.getAdapter(new TypeToken<SelectionModel<Node>>() {
					}).getSelectionUnmodifiable();
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
	 * Prepares the given {@link FXBendConnectionPolicy} for the manipulation of
	 * its host.
	 *
	 * @param bendPolicy
	 *            The {@link FXBendConnectionPolicy} that is prepared.
	 */
	private void prepareBend(FXBendConnectionPolicy bendPolicy) {
		// determine curve in scene coordinates
		Connection connection = bendPolicy.getConnection();

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

		isPrepared = false;

		// save initial mouse position in scene coordinates
		initialMouseInScene = new Point(e.getSceneX(), e.getSceneY());

		// disable refresh visuals for the host
		storeAndDisableRefreshVisuals(getHost());

		bendPolicy = determineBendPolicy();
		init(bendPolicy);
		updateHandles();
	}

	/**
	 * Updates the selection handles.
	 */
	@SuppressWarnings("unchecked")
	protected void updateHandles() {
		getHost().getRoot().getAdapter(SelectionBehavior.class)
				.updateHandles(getHost(), null, null);
	}

}

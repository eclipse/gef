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
package org.eclipse.gef4.mvc.fx.policies;

import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.OrthogonalRouter;
import org.eclipse.gef4.fx.utils.NodeUtils;
import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.behaviors.SelectionBehavior;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

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
	private Point initialMouseInScene;
	private boolean isInvalid = false;

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		if (isInvalid) {
			return;
		}

		Point currentMouseInScene = new Point(e.getSceneX(), e.getSceneY());
		getBendPolicy().move(initialMouseInScene, currentMouseInScene);
		updateHandles();
	}

	@Override
	public void dragAborted() {
		if (isInvalid) {
			return;
		}

		rollback(getBendPolicy());
		restoreRefreshVisuals(getHost());

		updateHandles();
	}

	/**
	 * Returns the {@link FXBendConnectionPolicy} of the host.
	 *
	 * @return The {@link FXBendConnectionPolicy} of the host.
	 */
	protected FXBendConnectionPolicy getBendPolicy() {
		return getHost().getAdapter(FXBendConnectionPolicy.class);
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

	@Override
	public void press(MouseEvent e) {
		isInvalid = false;
		if (!(getHost().getVisual().getRouter() instanceof OrthogonalRouter)) {
			// abort if non-orthogonal
			isInvalid = true;
		} else {
			// abort if part of multiple selection
			IVisualPart<Node, ? extends Node> host = getHost();
			@SuppressWarnings("serial")
			ObservableList<IContentPart<Node, ? extends Node>> selection = host
					.getRoot().getViewer()
					.getAdapter(new TypeToken<SelectionModel<Node>>() {
					}).getSelectionUnmodifiable();
			isInvalid = selection.size() > 1 && selection.contains(host);
		}
		if (isInvalid) {
			return;
		}

		// save initial mouse position in scene coordinates
		initialMouseInScene = new Point(e.getSceneX(), e.getSceneY());

		// disable refresh visuals for the host
		storeAndDisableRefreshVisuals(getHost());

		// initialize bend policy
		FXBendConnectionPolicy bendPolicy = getBendPolicy();
		init(bendPolicy);

		// determine curve in scene coordinates
		Connection connection = bendPolicy.getConnection();

		ICurve curve = connection.getCurveNode().getGeometry();
		ICurve curveInScene = (ICurve) NodeUtils
				.localToScene(connection.getCurveNode(), curve);

		// determine pressed segment (nearest to mouse)
		BezierCurve[] beziersInScene = curveInScene.toBezier();
		double minDistance = -1;
		int segmentIndex = -1;
		for (int i = 0; i < beziersInScene.length; i++) {
			BezierCurve bc = beziersInScene[i];
			Line line = bc.toLine();
			Point projection = line.getProjection(initialMouseInScene);
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

		// move initially to remove a potentially overlain anchor
		bendPolicy.move(initialMouseInScene, initialMouseInScene);
		updateHandles();
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		if (isInvalid) {
			return;
		}

		commit(getBendPolicy());
		restoreRefreshVisuals(getHost());

		updateHandles();
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		// TODO: Show <|> or ^-v indication cursor for segment movement.
		// cursorSupport.storeAndReplaceCursor(verticalSegment ?
		// LEFT_RIGHT_CURSRO : TOP_DOWN_CURSOR);
		return false;
	}

	/**
	 * Updates the selection handles.
	 */
	@SuppressWarnings("unchecked")
	protected void updateHandles() {
		getHost().getAdapter(SelectionBehavior.class).updateHandles(null, null);
	}

}

/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.utils.NodeUtils;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.models.GridModel;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.policies.TransformPolicy;
import org.eclipse.gef.mvc.fx.viewer.IViewer;

import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;

/**
 * The {@link TranslateSelectedOnDragHandler} is an {@link IOnDragHandler} that
 * relocates its {@link #getHost() host} when it is dragged with the mouse.
 *
 * @author anyssen
 *
 */
public class TranslateSelectedOnDragHandler extends AbstractHandler
		implements IOnDragHandler {

	private CursorSupport cursorSupport = new CursorSupport(this);
	private SnapSupport snapSupport = new SnapSupport(this);
	private Point initialMouseLocationInScene = null;
	private Map<IContentPart<? extends Node>, Integer> translationIndices = new HashMap<>();
	private List<Pair<IContentPart<? extends Node>, TransformPolicy>> targets;

	// gesture validity
	private boolean invalidGesture = false;

	private Map<IContentPart<? extends Node>, Rectangle> boundsInScene = new IdentityHashMap<>();

	@Override
	public void abortDrag() {
		if (invalidGesture) {
			return;
		}

		// roll back changes for all target parts
		for (Pair<IContentPart<? extends Node>, TransformPolicy> pair : targets) {
			rollback(pair.getValue());
			restoreRefreshVisuals(pair.getKey());
		}

		// reset targets
		targets = null;
		// reset initial pointer location
		setInitialMouseLocationInScene(null);
		// reset translation indices
		translationIndices.clear();
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		// abort this policy if no target parts could be found
		if (invalidGesture) {
			return;
		}
		// determine viewer
		IViewer viewer = getHost().getRoot().getViewer();
		// prepare data for snap-to-grid
		Node gridLocalVisual = null;
		GridModel gridModel = null;
		double granularityX = 0d;
		double granularityY = 0d;
		if (!isPrecise(e)) {
			granularityX = snapSupport.getSnapToGridGranularityX();
			granularityY = snapSupport.getSnapToGridGranularityY();
			gridModel = viewer.getAdapter(GridModel.class);
			gridLocalVisual = snapSupport.getGridLocalVisual(viewer);
		}
		// apply changes to the target parts
		for (Pair<IContentPart<? extends Node>, TransformPolicy> pair : targets) {
			// determine start and end position in scene coordinates
			Point startInScene = boundsInScene.get(pair.getKey()).getTopLeft();
			Point endInScene = startInScene.getTranslated(delta);
			// snap to grid
			Point newEndInScene = endInScene.getCopy();
			if (gridLocalVisual != null) {
				newEndInScene = snapSupport.snapToGrid(endInScene.x,
						endInScene.y, gridModel, granularityX, granularityY,
						gridLocalVisual);
			}
			// compute delta in parent coordinates
			Point newEndInParent = NodeUtils.sceneToLocal(
					pair.getKey().getVisual().getParent(), newEndInScene);
			Point startInParent = NodeUtils.sceneToLocal(
					pair.getKey().getVisual().getParent(), startInScene);
			Point deltaInParent = newEndInParent
					.getTranslated(startInParent.getNegated());
			// update transformation
			pair.getValue().setPostTranslate(
					translationIndices.get(pair.getKey()), deltaInParent.x,
					deltaInParent.y);
		}
	}

	@Override
	public void endDrag(MouseEvent e, Dimension delta) {
		// abort this policy if no target parts could be found
		if (invalidGesture) {
			return;
		}

		// commit changes for all target parts
		for (Pair<IContentPart<? extends Node>, TransformPolicy> pair : targets) {
			commit(pair.getValue());
			restoreRefreshVisuals(pair.getKey());
		}

		// reset target parts
		targets = null;
		// reset initial pointer location
		setInitialMouseLocationInScene(null);
		// reset translation indices
		translationIndices.clear();
	}

	/**
	 * Returns the {@link CursorSupport} of this policy.
	 *
	 * @return The {@link CursorSupport} of this policy.
	 */
	protected CursorSupport getCursorSupport() {
		return cursorSupport;
	}

	/**
	 * Returns the initial mouse location in scene coordinates.
	 *
	 * @return The initial mouse location in scene coordinates.
	 */
	protected Point getInitialMouseLocationInScene() {
		return initialMouseLocationInScene;
	}

	/**
	 * Returns a {@link List} containing all {@link IContentPart}s that should
	 * be relocated by this policy.
	 *
	 * @return A {@link List} containing all {@link IContentPart}s that should
	 *         be relocated by this policy.
	 */
	protected List<IContentPart<? extends Node>> getTargetParts() {
		return getHost().getRoot().getViewer().getAdapter(SelectionModel.class)
				.getSelectionUnmodifiable();
	}

	/**
	 * Returns the {@link TransformPolicy} that is installed on the given
	 * {@link IContentPart}.
	 *
	 * @param part
	 *            The {@link IContentPart} for which to return the installed
	 *            {@link TransformPolicy}.
	 * @return The {@link TransformPolicy} that is installed on the given
	 *         {@link IContentPart}.
	 */
	protected TransformPolicy getTransformPolicy(
			IContentPart<? extends Node> part) {
		return part.getAdapter(TransformPolicy.class);
	}

	@Override
	public void hideIndicationCursor() {
		getCursorSupport().restoreCursor();
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
	 * Returns whether the given {@link MouseEvent} should trigger translation.
	 * Per default, will return <code>true</code> if we have more than one
	 * target part or the single target part does not have a connection with an
	 * orthogonal router.
	 *
	 * @param event
	 *            The {@link MouseEvent} in question.
	 * @param targetParts
	 *            The list of (provisional) target {@link IContentPart}s.
	 * @return <code>true</code> if the given {@link MouseEvent} should trigger
	 *         translation, otherwise <code>false</code>.
	 */
	// TODO (bug #493418): This condition needs improvement
	protected boolean isTranslate(MouseEvent event,
			List<IContentPart<? extends Node>> targetParts) {
		// do not translate the only selected part if a
		// BendOnSegmentDragPolicy is registered for that part and the part is
		// an orthogonal connection that is connected at source and/or target
		if (targetParts.size() == 1 && targetParts.get(0)
				.getAdapter(BendOnSegmentDragHandler.class) != null) {
			IContentPart<? extends Node> part = targetParts.get(0);
			Node visual = part.getVisual();
			if (visual instanceof Connection
					&& ((Connection) visual)
							.getRouter() instanceof OrthogonalRouter
					&& (((Connection) visual).isStartConnected()
							|| ((Connection) visual).isEndConnected())) {
				targetParts = null;
			}
		}
		if (targetParts == null || targetParts.isEmpty()) {
			// abort this policy if no target parts could be found
			return false;
		}
		return true;
	}

	/**
	 * Sets the initial mouse location to the given value.
	 *
	 * @param point
	 *            The initial mouse location.
	 */
	protected void setInitialMouseLocationInScene(Point point) {
		initialMouseLocationInScene = point;
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		return false;
	}

	@Override
	public void startDrag(MouseEvent e) {
		// determine target parts
		List<IContentPart<? extends Node>> targetParts = getTargetParts();
		targets = new ArrayList<>();

		// decide whether to perform translation
		invalidGesture = !isTranslate(e, targetParts);
		if (invalidGesture) {
			return;
		}

		// save initial pointer location
		setInitialMouseLocationInScene(new Point(e.getSceneX(), e.getSceneY()));

		// initialize this policy for all determined target parts
		for (IContentPart<? extends Node> part : targetParts) {
			TransformPolicy policy = part.getAdapter(TransformPolicy.class);
			if (policy == null) {
				continue;
			}
			targets.add(new Pair<>(part, policy));

			// init transaction policy
			storeAndDisableRefreshVisuals(part);
			init(policy);
			translationIndices.put(part, policy.createPostTransform());
			// determine shape bounds
			Rectangle shapeBounds = NodeUtils
					.getShapeBounds(getHost().getVisual());
			Rectangle shapeBoundsInScene = NodeUtils
					.localToScene(getHost().getVisual(), shapeBounds)
					.getBounds();
			boundsInScene.put(part, shapeBoundsInScene);
		}
	}
}

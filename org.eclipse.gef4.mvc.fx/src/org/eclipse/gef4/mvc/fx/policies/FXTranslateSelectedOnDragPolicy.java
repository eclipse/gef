/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.policies;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.OrthogonalRouter;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.AbstractTransformPolicy;

import com.google.common.reflect.TypeToken;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * The {@link FXTranslateSelectedOnDragPolicy} is an {@link IFXOnDragPolicy}
 * that relocates its {@link #getHost() host} when it is dragged with the mouse.
 *
 * @author anyssen
 *
 */
public class FXTranslateSelectedOnDragPolicy extends AbstractFXInteractionPolicy
		implements IFXOnDragPolicy {

	private Point initialMouseLocationInScene = null;
	private Map<IContentPart<Node, ? extends Node>, Integer> translationIndices = new HashMap<>();
	private List<IContentPart<Node, ? extends Node>> targetParts;
	private CursorSupport cursorSupport = new CursorSupport(this);

	// gesture validity
	private boolean invalidGesture = false;

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		// abort this policy if no target parts could be found
		if (invalidGesture) {
			return;
		}

		// apply changes to the target parts
		for (IContentPart<Node, ? extends Node> part : targetParts) {
			FXTransformPolicy policy = getTransformPolicy(part);
			if (policy != null) {
				Point2D startInParent = getHost().getVisual().getParent()
						.sceneToLocal(0, 0);
				Point2D endInParent = getHost().getVisual().getParent()
						.sceneToLocal(delta.width, delta.height);
				Dimension snapToGridOffset = AbstractTransformPolicy
						.getSnapToGridOffset(getHost().getRoot().getViewer()
								.<GridModel> getAdapter(GridModel.class),
								endInParent.getX(), endInParent.getY(),
								getSnapToGridGranularityX(),
								getSnapToGridGranularityY());
				Point2D deltaInParent = new Point2D(
						endInParent.getX() - snapToGridOffset.width
								- startInParent.getX(),
						endInParent.getY() - snapToGridOffset.height
								- startInParent.getY());
				policy.setPostTranslate(translationIndices.get(part),
						deltaInParent.getX(), deltaInParent.getY());
			}
		}
	}

	@Override
	public void dragAborted() {
		if (invalidGesture) {
			return;
		}

		// roll back changes for all target parts
		for (IContentPart<Node, ? extends Node> part : targetParts) {
			FXTransformPolicy policy = getTransformPolicy(part);
			if (policy != null) {
				rollback(policy);
				restoreRefreshVisuals(part);
			}
		}

		// reset target parts
		targetParts = null;
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
	 * Returns the horizontal granularity for "snap-to-grid" where
	 * <code>1</code> means it will snap to integer grid positions.
	 *
	 * @return The horizontal granularity for "snap-to-grid".
	 */
	protected double getSnapToGridGranularityX() {
		return 1;
	}

	/**
	 * Returns the vertical granularity for "snap-to-grid" where <code>1</code>
	 * means it will snap to integer grid positions.
	 *
	 * @return The vertical granularity for "snap-to-grid".
	 */
	protected double getSnapToGridGranularityY() {
		return 1;
	}

	/**
	 * Returns a {@link List} containing all {@link IContentPart}s that should
	 * be relocated by this policy.
	 *
	 * @return A {@link List} containing all {@link IContentPart}s that should
	 *         be relocated by this policy.
	 */
	@SuppressWarnings("serial")
	protected List<IContentPart<Node, ? extends Node>> getTargetParts() {
		return getHost().getRoot().getViewer()
				.getAdapter(new TypeToken<SelectionModel<Node>>() {
				}).getSelectionUnmodifiable();
	}

	/**
	 * Returns the {@link FXTransformPolicy} that is installed on the given
	 * {@link IContentPart}.
	 *
	 * @param part
	 *            The {@link IContentPart} for which to return the installed
	 *            {@link FXTransformPolicy}.
	 * @return The {@link FXTransformPolicy} that is installed on the given
	 *         {@link IContentPart}.
	 */
	protected FXTransformPolicy getTransformPolicy(
			IContentPart<Node, ? extends Node> part) {
		return part.getAdapter(FXTransformPolicy.class);
	}

	@Override
	public void hideIndicationCursor() {
		getCursorSupport().restoreCursor();
	}

	/**
	 * Returns whether the given {@link MouseEvent} should trigger translation.
	 * Per default, will return <code>true</code> if we have more than one
	 * target part or the single target part does not have a connection with an
	 * orthogonal router.
	 *
	 * @param event
	 *            The {@link MouseEvent} in question.
	 * @return <code>true</code> if the given {@link MouseEvent} should trigger
	 *         translation, otherwise <code>false</code>.
	 */
	// TODO (bug #493418): This condition needs improvement
	protected boolean isTranslate(MouseEvent event) {
		// do not translate the only selected part if an
		// FXBendOnSegmentDragPolicy is registered for that part and the part is
		// an orthogonal connection
		if (targetParts.size() == 1 && targetParts.get(0)
				.getAdapter(FXBendOnSegmentDragPolicy.class) != null) {
			IContentPart<Node, ? extends Node> part = targetParts.get(0);
			Node visual = part.getVisual();
			if (visual instanceof Connection && ((Connection) visual)
					.getRouter() instanceof OrthogonalRouter) {
				targetParts = null;
			}
		}
		if (targetParts == null || targetParts.isEmpty()) {
			// abort this policy if no target parts could be found
			return false;
		}
		return true;
	}

	@Override
	public void press(MouseEvent e) {
		// determine target parts
		targetParts = getTargetParts();

		// decide whether to perform translation
		invalidGesture = !isTranslate(e);
		if (invalidGesture) {
			return;
		}

		// save initial pointer location
		setInitialMouseLocationInScene(new Point(e.getSceneX(), e.getSceneY()));

		// initialize this policy for all determined target parts
		for (IContentPart<Node, ? extends Node> part : targetParts) {
			// init transaction policy
			FXTransformPolicy policy = getTransformPolicy(part);
			if (policy != null) {
				storeAndDisableRefreshVisuals(part);
				init(policy);
				translationIndices.put(part, policy.createPostTransform());
			}
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		// abort this policy if no target parts could be found
		if (invalidGesture) {
			return;
		}

		// commit changes for all target parts
		for (IContentPart<Node, ? extends Node> part : targetParts) {
			FXTransformPolicy policy = getTransformPolicy(part);
			if (policy != null) {
				commit(policy);
				restoreRefreshVisuals(part);
			}
		}

		// reset target parts
		targetParts = null;
		// reset initial pointer location
		setInitialMouseLocationInScene(null);
		// reset translation indices
		translationIndices.clear();
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

}

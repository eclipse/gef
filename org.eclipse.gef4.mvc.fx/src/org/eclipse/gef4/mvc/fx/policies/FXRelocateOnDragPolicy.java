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

import java.util.List;

import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * The {@link FXRelocateOnDragPolicy} is an {@link AbstractFXOnDragPolicy} that
 * relocates its {@link #getHost() host} when it is dragged with the mouse.
 *
 * @author anyssen
 *
 */
public class FXRelocateOnDragPolicy extends AbstractFXOnDragPolicy {

	private Point initialMouseLocationInScene = null;

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		for (IContentPart<Node, ? extends Node> part : getTargetParts()) {
			FXResizeRelocatePolicy policy = getResizeRelocatePolicy(part);
			if (policy != null) {
				Node visual = part.getVisual();
				Point2D initialPosInParent = visual
						.localToParent(visual.sceneToLocal(Geometry2JavaFX
								.toFXPoint(getInitialMouseLocationInScene())));
				Point2D currentPosInParent = visual.localToParent(
						visual.sceneToLocal(e.getSceneX(), e.getSceneY()));
				Point2D deltaPoint = new Point2D(
						currentPosInParent.getX() - initialPosInParent.getX(),
						currentPosInParent.getY() - initialPosInParent.getY());
				policy.performResizeRelocate(deltaPoint.getX(),
						deltaPoint.getY(), 0, 0);
			}
		}
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
	 * Returns the {@link FXResizeRelocatePolicy} that is installed on the given
	 * {@link IContentPart}.
	 *
	 * @param part
	 *            The {@link IContentPart} for which to return the installed
	 *            {@link FXResizeRelocatePolicy}.
	 * @return The {@link FXResizeRelocatePolicy} that is installed on the given
	 *         {@link IContentPart}.
	 */
	protected FXResizeRelocatePolicy getResizeRelocatePolicy(
			IContentPart<Node, ? extends Node> part) {
		return part.getAdapter(FXResizeRelocatePolicy.class);
	}

	/**
	 * Returns a {@link List} containing all {@link IContentPart}s that should
	 * be relocated by this policy.
	 *
	 * @return A {@link List} containing all {@link IContentPart}s that should
	 *         be relocated by this policy.
	 */
	public List<IContentPart<Node, ? extends Node>> getTargetParts() {
		return getHost().getRoot().getViewer()
				.<SelectionModel<Node>> getAdapter(SelectionModel.class)
				.getSelected();
	}

	@Override
	public void press(MouseEvent e) {
		setInitialMouseLocationInScene(new Point(e.getSceneX(), e.getSceneY()));
		for (IContentPart<Node, ? extends Node> part : getTargetParts()) {
			disableRefreshVisuals(part);
			// init transaction policy
			init(getResizeRelocatePolicy(part));
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		for (IContentPart<Node, ? extends Node> part : getTargetParts()) {
			FXResizeRelocatePolicy policy = getResizeRelocatePolicy(part);
			if (policy != null) {
				enableRefreshVisuals(part);
				// TODO: we need to ensure this can be done before
				// enableRefreshVisuals(), because visuals should already be up
				// to date
				// (and we thus save a potential refresh)
				commit(policy);
			}
		}
		setInitialMouseLocationInScene(null);
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

}

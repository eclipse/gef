/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;

public class FXRelocateOnDragPolicy extends AbstractFXDragPolicy {

	private Point initialMouseLocationInScene = null;

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		for (IContentPart<Node, ? extends Node> part : getTargetParts()) {
			FXResizeRelocatePolicy policy = getResizeRelocatePolicy(part);
			if (policy != null) {
				Point2D initialPosInLocal = part.getVisual().sceneToLocal(
						Geometry2JavaFX
								.toFXPoint(getInitialMouseLocationInScene()));
				Point2D currentPosInLocal = part.getVisual().sceneToLocal(
						e.getSceneX(), e.getSceneY());
				Point2D deltaPoint = new Point2D(currentPosInLocal.getX()
						- initialPosInLocal.getX(), currentPosInLocal.getY()
						- initialPosInLocal.getY());
				policy.performResizeRelocate(deltaPoint.getX(),
						deltaPoint.getY(), 0, 0);
			}
		}
	}

	protected Point getInitialMouseLocationInScene() {
		return initialMouseLocationInScene;
	}

	protected FXResizeRelocatePolicy getResizeRelocatePolicy(
			IContentPart<Node, ? extends Node> part) {
		return part.getAdapter(FXResizeRelocatePolicy.class);
	}

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
			part.setRefreshVisual(false);
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

	protected void setInitialMouseLocationInScene(Point point) {
		initialMouseLocationInScene = point;
	}

}

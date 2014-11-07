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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.convert.fx.Geometry2JavaFX;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.operations.AbstractCompositeOperation;
import org.eclipse.gef4.mvc.operations.ITransactional;
import org.eclipse.gef4.mvc.operations.ReverseUndoCompositeOperation;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

public class FXRelocateOnDragPolicy extends AbstractFXDragPolicy implements
		ITransactional {

	private AbstractCompositeOperation commitOperation = null;

	private Point initialMouseLocationInScene = null;
	private final Map<IVisualPart<Node>, Boolean> initialRefreshVisual = new HashMap<IVisualPart<Node>, Boolean>();

	@Override
	public IUndoableOperation commit() {
		return commitOperation.unwrap();
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		for (IContentPart<Node> part : getTargetParts()) {
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
			IContentPart<Node> part) {
		return part.getAdapter(FXResizeRelocatePolicy.class);
	}

	public List<IContentPart<Node>> getTargetParts() {
		return getHost().getRoot().getViewer()
				.<SelectionModel<Node>> getAdapter(SelectionModel.class)
				.getSelected();
	}

	@Override
	public void init() {
		// FIXME: if we change this to forward undo, the example does not
		// properly work (connections are not properly undone after moving).
		commitOperation = new ReverseUndoCompositeOperation("Relocate");
	}

	@Override
	public void press(MouseEvent e) {
		setInitialMouseLocationInScene(new Point(e.getSceneX(), e.getSceneY()));
		for (IContentPart<Node> part : getTargetParts()) {
			ITransactional policy = getResizeRelocatePolicy(part);
			if (policy != null) {
				initialRefreshVisual.put(part, part.isRefreshVisual());
				part.setRefreshVisual(false);
				policy.init();
			}
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		for (IContentPart<Node> part : getTargetParts()) {
			FXResizeRelocatePolicy policy = getResizeRelocatePolicy(part);
			if (policy != null) {
				part.setRefreshVisual(initialRefreshVisual.remove(part));
				IUndoableOperation commit = policy.commit();
				if (commit != null) {
					commitOperation.add(commit);
				}
			}
		}
		setInitialMouseLocationInScene(null);
		if (!initialRefreshVisual.isEmpty()) {
			throw new IllegalStateException(
					"The refresh-visual flag was not properly reset for all (initial) target parts.");
		}
	}

	protected void setInitialMouseLocationInScene(Point point) {
		initialMouseLocationInScene = point;
	}

}

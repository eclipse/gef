/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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

import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXCornerHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

// Only applicable for AbstractFXCornerHandlePart, see #getHost().
public class FXResizeRelocateOnCornerHandleDragPolicy extends
		AbstractFXDragPolicy {

	private Point initialPointerLocation;
	private double dx;
	private double dy;
	private double dw;
	private double dh;
	private boolean invalidGesture = false;

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		if (invalidGesture) {
			return;
		}

		updateDeltas(e);
		getResizeRelocatePolicy().performResizeRelocate(dx, dy, dw, dh);
	}

	@Override
	public AbstractFXCornerHandlePart<? extends Node> getHost() {
		return (AbstractFXCornerHandlePart<? extends Node>) super.getHost();
	}

	protected FXResizeRelocatePolicy getResizeRelocatePolicy() {
		return getTargetPart().getAdapter(FXResizeRelocatePolicy.class);
	}

	protected IVisualPart<Node, ? extends Node> getTargetPart() {
		return getHost().getAnchorages().keySet().iterator().next();
	}

	@Override
	public void press(MouseEvent e) {
		if (e.isControlDown()) {
			invalidGesture = true;
			return;
		}

		initialPointerLocation = new Point(e.getSceneX(), e.getSceneY());
		init(getResizeRelocatePolicy());
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		if (invalidGesture) {
			invalidGesture = false;
			return;
		}

		updateDeltas(e);
		commit(getResizeRelocatePolicy());
	}

	protected void updateDeltas(MouseEvent e) {
		dx = dy = dw = dh = 0;
		Node visual = getTargetPart().getVisual();
		Point2D startLocal = visual.sceneToLocal(initialPointerLocation.x,
				initialPointerLocation.y);
		Point2D endLocal = visual.sceneToLocal(e.getSceneX(), e.getSceneY());
		double deltaX = endLocal.getX() - startLocal.getX();
		double deltaY = endLocal.getY() - startLocal.getY();

		Point2D startParent = visual.localToParent(startLocal);
		Point2D endParent = visual.localToParent(endLocal);
		double deltaXParent = endParent.getX() - startParent.getX();
		double deltaYParent = endParent.getY() - startParent.getY();

		Pos pos = getHost().getPos();
		if (pos.getHpos().equals(HPos.RIGHT)) {
			dw = deltaX;
		} else if (pos.getHpos().equals(HPos.LEFT)) {
			dx = deltaXParent;
			dw = -deltaX;
		}
		if (pos.getVpos().equals(VPos.BOTTOM)) {
			dh = deltaY;
		} else if (pos.getVpos().equals(VPos.TOP)) {
			dy = deltaYParent;
			dh = -deltaY;
		}
	}

}

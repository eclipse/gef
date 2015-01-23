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

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXSegmentHandlePart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

// Only applicable for AbstractFXCornerHandlePart, see #getHost().
public class FXResizeRelocateOnHandleDragPolicy extends AbstractFXDragPolicy {

	private Point initialPointerLocation;
	private double initialTx;
	private double initialTy;
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
	public AbstractFXSegmentHandlePart<? extends Node> getHost() {
		return (AbstractFXSegmentHandlePart<? extends Node>) super.getHost();
	}

	protected FXResizeRelocatePolicy getResizeRelocatePolicy() {
		return getTargetPart().getAdapter(FXResizeRelocatePolicy.class);
	}

	protected IVisualPart<Node, ? extends Node> getTargetPart() {
		return getHost().getAnchorages().keySet().iterator().next();
	}

	@SuppressWarnings("serial")
	private Affine getTargetTransform() {
		return getTargetPart().getAdapter(
				AdapterKey.get(new TypeToken<Provider<Affine>>() {
				}, FXTransformPolicy.TRANSFORMATION_PROVIDER_ROLE)).get();
	}

	@Override
	public void press(MouseEvent e) {
		if (e.isControlDown()) {
			invalidGesture = true;
			return;
		}

		initialPointerLocation = new Point(e.getSceneX(), e.getSceneY());
		initialTx = getTargetTransform().getTx();
		initialTy = getTargetTransform().getTy();
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

		// segment index determines logical position (0 = top left, 1 = top
		// right, 2 = bottom right, 3 = bottom left)
		int segment = getHost().getSegmentIndex();

		Point2D layout = visual.parentToLocal(initialTx, initialTy);
		double lx = layout.getX();
		double ly = layout.getY();
		if (segment == 0 || segment == 3) {
			// left side => change x
			lx += deltaX;
		}
		if (segment == 0 || segment == 1) {
			// top side => change y
			ly += deltaY;
		}

		Point2D layoutParent = visual.localToParent(lx, ly);
		dx = layoutParent.getX() - initialTx;
		dy = layoutParent.getY() - initialTy;

		if (segment == 1 || segment == 2) {
			// right side
			dw = deltaX;
		} else {
			// left side
			dw = -deltaX;
		}
		if (segment == 2 || segment == 3) {
			// bottom side
			dh = deltaY;
		} else {
			// top side
			dh = -deltaY;
		}
	}

}

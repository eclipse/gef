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

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXSegmentHandlePart;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

/**
 * The {@link FXResizeTranslateOnHandleDragPolicy} is an
 * {@link AbstractFXOnDragPolicy} that handles the resize and relocation of its
 * (selected) first anchorage when an {@link AbstractFXSegmentHandlePart} of the
 * box selection of the first anchorage is dragged with the mouse.
 *
 * @author mwienand
 *
 */
// Only applicable for AbstractFXSegmentHandlePart, see #getHost().
public class FXResizeTranslateOnHandleDragPolicy
		extends AbstractFXOnDragPolicy {

	private boolean invalidGesture = false;
	private Point initialPointerLocation;
	private double initialTx;
	private double initialTy;
	private int translationIndex;

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		// guard against uninitialized access
		if (invalidGesture) {
			return;
		}
		// compute delta in local coordinates
		Node visual = getTargetPart().getVisual();
		Point2D startLocal = visual.sceneToLocal(initialPointerLocation.x,
				initialPointerLocation.y);
		Point2D endLocal = visual.sceneToLocal(e.getSceneX(), e.getSceneY());
		double deltaX = endLocal.getX() - startLocal.getX();
		double deltaY = endLocal.getY() - startLocal.getY();
		// determine current layout position in local coordinates
		Point2D layout = visual.parentToLocal(initialTx, initialTy);
		double lx = layout.getX();
		double ly = layout.getY();
		// segment index determines logical position (0 = top left, 1 = top
		// right, 2 = bottom right, 3 = bottom left)
		int segment = getHost().getSegmentIndex();
		if (segment == 0 || segment == 3) {
			// left side => change layout x by local delta x
			lx += deltaX;
		}
		if (segment == 0 || segment == 1) {
			// top side => change layout y by local delta y
			ly += deltaY;
		}
		// transform new layout position to parent coordinates
		Point2D layoutParent = visual.localToParent(lx, ly);
		// compute layout delta in parent coordinates
		double pdx = layoutParent.getX() - initialTx;
		double pdy = layoutParent.getY() - initialTy;
		// determine resize in local coordinates
		double ldw, ldh;
		if (segment == 1 || segment == 2) {
			// right side
			ldw = deltaX;
		} else {
			// left side
			ldw = -deltaX;
		}
		if (segment == 2 || segment == 3) {
			// bottom side
			ldh = deltaY;
		} else {
			// top side
			ldh = -deltaY;
		}
		// apply translation and resize using underlying policies
		getResizePolicy().performResize(ldw, ldh);
		getTransformPolicy().setPostTranslate(translationIndex, pdx, pdy);
	}

	@Override
	public AbstractFXSegmentHandlePart<? extends Node> getHost() {
		return (AbstractFXSegmentHandlePart<? extends Node>) super.getHost();
	}

	/**
	 * Returns the {@link FXResizePolicy} that is installed on the
	 * {@link #getTargetPart()}.
	 *
	 * @return The {@link FXResizePolicy} that is installed on the
	 *         {@link #getTargetPart()}.
	 */
	protected FXResizePolicy getResizePolicy() {
		return getTargetPart().getAdapter(FXResizePolicy.class);
	}

	/**
	 * Returns the target {@link IVisualPart} for this policy. Per default the
	 * first anchorage is returned.
	 *
	 * @return The target {@link IVisualPart} for this policy.
	 */
	protected IVisualPart<Node, ? extends Node> getTargetPart() {
		return getHost().getAnchorages().keySet().iterator().next();
	}

	private Affine getTargetTransform() {
		return getTargetPart()
				.getAdapter(FXTransformPolicy.TRANSFORM_PROVIDER_KEY).get();
	}

	/**
	 * Returns the {@link FXTransformPolicy} that is installed on the
	 * {@link #getTargetPart()}.
	 *
	 * @return The {@link FXTransformPolicy} that is installed on the
	 *         {@link #getTargetPart()}.
	 */
	protected FXTransformPolicy getTransformPolicy() {
		return getTargetPart().getAdapter(FXTransformPolicy.class);
	}

	private boolean isMultiSelection() {
		return getTargetPart().getRoot().getViewer()
				.getAdapter(SelectionModel.class).getSelection().size() > 1;
	}

	@Override
	public void press(MouseEvent e) {
		if (e.isControlDown() || isMultiSelection()) {
			invalidGesture = true;
			return;
		}
		storeAndDisableRefreshVisuals(getTargetPart());
		initialPointerLocation = new Point(e.getSceneX(), e.getSceneY());
		Affine targetTransform = getTargetTransform();
		initialTx = targetTransform.getTx();
		initialTy = targetTransform.getTy();
		init(getResizePolicy());
		init(getTransformPolicy());
		translationIndex = getTransformPolicy().createPostTransform();
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		if (invalidGesture) {
			invalidGesture = false;
			return;
		}
		restoreRefreshVisuals(getTargetPart());
		commit(getResizePolicy());
		commit(getTransformPolicy());
	}

}

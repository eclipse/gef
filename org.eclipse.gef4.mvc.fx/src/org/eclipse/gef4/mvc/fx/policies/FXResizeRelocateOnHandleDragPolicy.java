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

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXSegmentHandlePart;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IVisualPart;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

/**
 * The {@link FXResizeRelocateOnHandleDragPolicy} is an
 * {@link AbstractFXOnDragPolicy} that handles the resize and relocation of its
 * (selected) first anchorage when an {@link AbstractFXSegmentHandlePart} of the
 * box selection of the first anchorage is dragged with the mouse.
 *
 * @author mwienand
 *
 */
// Only applicable for AbstractFXSegmentHandlePart, see #getHost().
public class FXResizeRelocateOnHandleDragPolicy extends AbstractFXOnDragPolicy {

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

	/**
	 * Returns the {@link FXResizeRelocatePolicy} that is installed on the
	 * {@link #getTargetPart()}.
	 *
	 * @return The {@link FXResizeRelocatePolicy} that is installed on the
	 *         {@link #getTargetPart()}.
	 */
	protected FXResizeRelocatePolicy getResizeRelocatePolicy() {
		return getTargetPart().getAdapter(FXResizeRelocatePolicy.class);
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

	@SuppressWarnings("serial")
	private Affine getTargetTransform() {
		return getTargetPart()
				.getAdapter(AdapterKey.get(new TypeToken<Provider<Affine>>() {
				}, FXTransformPolicy.TRANSFORMATION_PROVIDER_ROLE)).get();
	}

	private boolean isMultiSelection() {
		return getTargetPart().getRoot().getViewer()
				.getAdapter(SelectionModel.class).getSelected().size() > 1;
	}

	@Override
	public void press(MouseEvent e) {
		if (e.isControlDown() || isMultiSelection()) {
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

	/**
	 * Computes the resize and relocation deltas from the given
	 * {@link MouseEvent}. The
	 * {@link AbstractFXSegmentHandlePart#getSegmentIndex()} of the host
	 * determines the logical position of the handle:
	 * <ul>
	 * <li>0: top left
	 * <li>1: top right
	 * <li>2: bottom right
	 * <li>3: bottom left
	 * </ul>
	 *
	 * @param e
	 *            The drag {@link MouseEvent}.
	 */
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

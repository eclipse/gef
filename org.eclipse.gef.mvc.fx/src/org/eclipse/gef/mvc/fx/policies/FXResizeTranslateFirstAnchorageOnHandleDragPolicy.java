/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.policies;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.parts.AbstractFXSegmentHandlePart;
import org.eclipse.gef.mvc.models.SelectionModel;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.viewer.IViewer;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

/**
 * The {@link FXResizeTranslateFirstAnchorageOnHandleDragPolicy} is an
 * {@link IFXOnDragPolicy} that handles the resize and relocation of its
 * (selected) first anchorage when an {@link AbstractFXSegmentHandlePart} of the
 * box selection of the first anchorage is dragged with the mouse.
 *
 * @author mwienand
 *
 */
// Only applicable for AbstractFXSegmentHandlePart, see #getHost().
public class FXResizeTranslateFirstAnchorageOnHandleDragPolicy
		extends AbstractFXInteractionPolicy implements IFXOnDragPolicy {

	private boolean invalidGesture = false;
	private Point initialPointerLocation;
	private double initialTx;
	private double initialTy;
	private int translationIndex;
	private CursorSupport cursorSupport = new CursorSupport(this);
	private IVisualPart<Node, ? extends Node> targetPart;

	@Override
	public void abortDrag() {
		if (invalidGesture) {
			return;
		}
		restoreRefreshVisuals(getTargetPart());
		commit(getResizePolicy());
		commit(getTransformPolicy());
	}

	/**
	 * Returns the target {@link IVisualPart} for this policy. Per default the
	 * first anchorage is returned.
	 *
	 * @return The target {@link IVisualPart} for this policy.
	 */
	protected IVisualPart<Node, ? extends Node> determineTargetPart() {
		return getHost().getAnchoragesUnmodifiable().keySet().iterator().next();
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		// guard against uninitialized access
		if (invalidGesture) {
			return;
		}
		// compute end point
		Node visual = getTargetPart().getVisual();
		Point2D startLocal = visual.sceneToLocal(initialPointerLocation.x,
				initialPointerLocation.y);
		// snap to grid
		IViewer<Node> viewer = getHost().getRoot().getViewer();
		Point newEndScene = isPrecise(e)
				? new Point(e.getSceneX(), e.getSceneY())
				: snapToGrid(viewer, e.getSceneX(), e.getSceneY());
		Point2D endLocal = visual.sceneToLocal(newEndScene.x, newEndScene.y);
		// compute delta in local coordinates
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
		getResizePolicy().resize(ldw, ldh);
		getTransformPolicy().setPostTranslate(translationIndex, pdx, pdy);
	}

	@Override
	public void endDrag(MouseEvent e, Dimension delta) {
		if (invalidGesture) {
			invalidGesture = false;
			return;
		}
		restoreRefreshVisuals(getTargetPart());
		commit(getResizePolicy());
		commit(getTransformPolicy());
	}

	/**
	 * Returns the {@link CursorSupport} of this policy.
	 *
	 * @return The {@link CursorSupport} of this policy.
	 */
	protected CursorSupport getCursorSupport() {
		return cursorSupport;
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
	 * Returns the target part of this policy.
	 *
	 * @return The target part of this policy.
	 */
	protected IVisualPart<Node, ? extends Node> getTargetPart() {
		return targetPart;
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

	@Override
	public void hideIndicationCursor() {
		getCursorSupport().restoreCursor();
	}

	private boolean isMultiSelection() {
		return getTargetPart().getRoot().getViewer()
				.getAdapter(SelectionModel.class).getSelectionUnmodifiable()
				.size() > 1;
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
	 * Returns <code>true</code> if the given {@link MouseEvent} should trigger
	 * resize and translate. Otherwise returns <code>false</code>. Per default
	 * returns <code>true</code> if <code>&lt;Control&gt;</code> is not pressed
	 * and there is not more than single anchorage.
	 *
	 * @param event
	 *            The {@link MouseEvent} in question.
	 * @return <code>true</code> if the given {@link MouseEvent} should trigger
	 *         resize and translate, otherwise <code>false</code>.
	 */
	protected boolean isResizeTranslate(MouseEvent event) {
		return !event.isControlDown() && !isMultiSelection();
	}

	/**
	 * Sets the target part (i.e. the part that is resized and translated) of
	 * this policy to the given value.
	 *
	 * @param determinedTargetPart
	 *            The target part of this policy.
	 */
	protected void setTargetPart(
			IVisualPart<Node, ? extends Node> determinedTargetPart) {
		this.targetPart = determinedTargetPart;
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
		setTargetPart(determineTargetPart());
		invalidGesture = !isResizeTranslate(e);
		if (invalidGesture) {
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

}

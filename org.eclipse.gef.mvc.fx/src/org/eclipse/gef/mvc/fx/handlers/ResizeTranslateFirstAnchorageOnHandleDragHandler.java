/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.handlers;

import java.util.Arrays;

import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.models.SnappingModel.SnappingLocation;
import org.eclipse.gef.mvc.fx.parts.AbstractSegmentHandlePart;
import org.eclipse.gef.mvc.fx.parts.ITransformableContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.ResizePolicy;
import org.eclipse.gef.mvc.fx.policies.TransformPolicy;

import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * The {@link ResizeTranslateFirstAnchorageOnHandleDragHandler} is an
 * {@link IOnDragHandler} that handles the resize and relocation of its
 * (selected) first anchorage when an {@link AbstractSegmentHandlePart} of the
 * box selection of the first anchorage is dragged with the mouse.
 *
 * @author mwienand
 *
 */
// Only applicable for AbstractSegmentHandlePart, see #getHost().
public class ResizeTranslateFirstAnchorageOnHandleDragHandler
		extends AbstractHandler implements IOnDragHandler {

	private SnapToSupport snapToSupport;
	private boolean invalidGesture = false;
	private Point initialPointerLocation;
	private int translationIndex;
	private IVisualPart<? extends Node> targetPart;
	private Point initialVertex;

	@Override
	public void abortDrag() {
		if (invalidGesture) {
			return;
		}
		if (snapToSupport != null) {
			snapToSupport.stopSnapping();
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
	protected IVisualPart<? extends Node> determineTargetPart() {
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
		Point newEndScene = new Point(e.getSceneX(), e.getSceneY());

		// compute delta in scene
		Point deltaInScene = new Point(newEndScene.x - initialPointerLocation.x,
				newEndScene.y - initialPointerLocation.y);

		// apply delta to the moved vertex
		Point newVertex = initialVertex.getTranslated(deltaInScene);

		// snap the moved vertex (unless isPrecise(e))
		Point snappedVertex = newVertex;
		if (snapToSupport != null) {
			if (!isPrecise(e)) {
				snappedVertex.translate(snapToSupport
						.snap(new Dimension(deltaInScene.x, deltaInScene.y)));
			} else {
				snapToSupport.clearSnappingFeedback();
			}
		}

		// compute delta between initial and snapped vertex
		Point2D startLocal = visual.sceneToLocal(initialVertex.x,
				initialVertex.y);
		Point2D endLocal = visual.sceneToLocal(snappedVertex.x,
				snappedVertex.y);

		// compute delta in local coordinates
		double deltaX = endLocal.getX() - startLocal.getX();
		double deltaY = endLocal.getY() - startLocal.getY();

		// segment index determines logical position (0 = top left, 1 = top
		// right, 2 = bottom right, 3 = bottom left)
		int segment = getHost().getSegmentIndex();

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

		// XXX: Resize before querying the applicable delta, so that the minimum
		// size is respected.
		getResizePolicy().resize(ldw, ldh);
		Dimension applicableDelta = new Dimension(
				getResizePolicy().getDeltaWidth(),
				getResizePolicy().getDeltaHeight());

		// Only apply translation if possible, i.e. if the resize cannot be
		// applied in total, the translation can probably not be applied in
		// total as well.
		if (applicableDelta.width != ldw) {
			deltaX = applicableDelta.width;
			if (segment == 0 || segment == 3) {
				deltaX = -deltaX;
			}
		}
		if (applicableDelta.height != ldh) {
			deltaY = applicableDelta.height;
			if (segment == 0 || segment == 1) {
				deltaY = -deltaY;
			}
		}

		// compute (local) translation
		double ldx = segment == 0 || segment == 3 ? deltaX : 0;
		double ldy = segment == 0 || segment == 1 ? deltaY : 0;

		// apply translation
		getTransformPolicy().setPreTranslate(translationIndex, ldx, ldy);
	}

	@Override
	public void endDrag(MouseEvent e, Dimension delta) {
		if (invalidGesture) {
			invalidGesture = false;
			return;
		}
		if (snapToSupport != null) {
			snapToSupport.stopSnapping();
		}
		restoreRefreshVisuals(getTargetPart());
		commit(getResizePolicy());
		commit(getTransformPolicy());
	}

	@Override
	public AbstractSegmentHandlePart<? extends Node> getHost() {
		return (AbstractSegmentHandlePart<? extends Node>) super.getHost();
	}

	/**
	 * Returns the {@link ResizePolicy} that is installed on the
	 * {@link #getTargetPart()}.
	 *
	 * @return The {@link ResizePolicy} that is installed on the
	 *         {@link #getTargetPart()}.
	 */
	protected ResizePolicy getResizePolicy() {
		return getTargetPart().getAdapter(ResizePolicy.class);
	}

	/**
	 * Returns the target part of this policy.
	 *
	 * @return The target part of this policy.
	 */
	protected ITransformableContentPart<? extends Node> getTargetPart() {
		return (ITransformableContentPart<? extends Node>) targetPart;
	}

	/**
	 * Returns the {@link TransformPolicy} that is installed on the
	 * {@link #getTargetPart()}.
	 *
	 * @return The {@link TransformPolicy} that is installed on the
	 *         {@link #getTargetPart()}.
	 */
	protected TransformPolicy getTransformPolicy() {
		return getTargetPart().getAdapter(TransformPolicy.class);
	}

	@Override
	public void hideIndicationCursor() {
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
			IVisualPart<? extends Node> determinedTargetPart) {
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
		ITransformableContentPart<? extends Node> targetPart = getTargetPart();
		storeAndDisableRefreshVisuals(targetPart);
		initialPointerLocation = new Point(e.getSceneX(), e.getSceneY());
		init(getResizePolicy());
		init(getTransformPolicy());
		translationIndex = getTransformPolicy().createPreTransform();
		// determine initial bounds in scene
		Bounds layoutBounds = targetPart.getVisual().getLayoutBounds();
		Bounds initialBoundsInScene = targetPart.getVisual()
				.localToScene(layoutBounds);
		// save moved vertex
		int segment = getHost().getSegmentIndex();
		if (segment == 0) {
			initialVertex = new Point(initialBoundsInScene.getMinX(),
					initialBoundsInScene.getMinY());
		} else if (segment == 1) {
			initialVertex = new Point(initialBoundsInScene.getMaxX(),
					initialBoundsInScene.getMinY());
		} else if (segment == 2) {
			initialVertex = new Point(initialBoundsInScene.getMaxX(),
					initialBoundsInScene.getMaxY());
		} else if (segment == 3) {
			initialVertex = new Point(initialBoundsInScene.getMinX(),
					initialBoundsInScene.getMaxY());
		}

		snapToSupport = targetPart.getViewer().getAdapter(SnapToSupport.class);
		if (snapToSupport != null) {
			SnappingLocation hssl = new SnappingLocation(targetPart,
					Orientation.HORIZONTAL, initialVertex.x);
			SnappingLocation vssl = new SnappingLocation(targetPart,
					Orientation.VERTICAL, initialVertex.y);
			snapToSupport.startSnapping(targetPart, Arrays.asList(hssl, vssl));
		}
	}
}

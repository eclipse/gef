/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.PartUtils;
import org.eclipse.gef.mvc.fx.policies.TransformPolicy;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * The {@link RotateSelectedOnHandleDragHandler} is an {@link IOnDragHandler}
 * that rotates the whole {@link SelectionModel selection} when a selection
 * handle is dragged.
 *
 * @author mwienand
 *
 */
public class RotateSelectedOnHandleDragHandler extends AbstractHandler
		implements IOnDragHandler {

	// indication cursor
	private CursorSupport cursorSupport;
	private ImageCursor rotateCursor;

	// gesture validity
	private boolean invalidGesture = false;

	// initial state
	private Point initialPointerLocationInScene;
	private Point pivotInScene;
	private Map<IContentPart<? extends Node>, Integer> rotationIndices = new HashMap<>();
	private List<IContentPart<? extends Node>> targetParts;

	@Override
	public void abortDrag() {
		if (invalidGesture) {
			return;
		}
		// rollback transform operations
		for (IVisualPart<? extends Node> part : getTargetParts()) {
			TransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				restoreRefreshVisuals(part);
				rollback(transformPolicy);
			}
		}
	}

	/**
	 * Computes the clock-wise rotation angle based on the initial mouse
	 * position and the actual mouse position.
	 *
	 * @param e
	 *            The latest {@link MouseEvent}.
	 * @param part
	 *            The {@link IVisualPart} that is rotated.
	 * @return The clock-wise rotation angle.
	 */
	protected Angle computeRotationAngleCW(MouseEvent e,
			IVisualPart<? extends Node> part) {
		Vector vStart = new Vector(pivotInScene, initialPointerLocationInScene);
		Vector vEnd = new Vector(pivotInScene,
				new Point(e.getSceneX(), e.getSceneY()));
		Angle angle = vStart.getAngleCW(vEnd);
		return angle;
	}

	/**
	 * Returns the {@link Cursor} that is shown to indicate that this policy
	 * will perform a rotation.
	 *
	 * @return The {@link Cursor} that is shown to indicate that this policy
	 *         will perform a rotation.
	 */
	protected ImageCursor createRotateCursor() {
		return new ImageCursor(new Image(RotateSelectedOnHandleDragHandler.class
				.getResource("/rotate_obj.gif").toExternalForm()));
	}

	/**
	 * Returns a {@link List} containing the whole {@link SelectionModel
	 * selection}.
	 *
	 * @return A {@link List} containing the whole {@link SelectionModel
	 *         selection}.
	 */
	protected List<IContentPart<? extends Node>> determineTargetParts() {
		return getHost().getRoot().getViewer().getAdapter(SelectionModel.class)
				.getSelectionUnmodifiable();
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		// do nothing when the user does not press control
		if (invalidGesture) {
			return;
		}
		for (IVisualPart<? extends Node> part : getTargetParts()) {
			updateOperation(e, part);
		}
	}

	@Override
	public void endDrag(MouseEvent e, Dimension delta) {
		// do nothing when the user does not press control
		if (invalidGesture) {
			invalidGesture = false;
			return;
		}

		// commit transform operations
		for (IVisualPart<? extends Node> part : getTargetParts()) {
			updateOperation(e, part);
			TransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				restoreRefreshVisuals(part);
				commit(transformPolicy);
			}
		}
	}

	/**
	 * Returns the {@link CursorSupport} of this policy.
	 *
	 * @return The {@link CursorSupport} of this policy.
	 */
	protected CursorSupport getCursorSupport() {
		if (cursorSupport == null) {
			cursorSupport = getHost().getViewer()
					.getAdapter(CursorSupport.class);
		}
		return cursorSupport;
	}

	/**
	 * Returns the {@link Cursor} that indicates rotation. Delegates to
	 * {@link #createRotateCursor()} to create that cursor if it was not created
	 * yet.
	 *
	 * @return The {@link Cursor} that indicates rotation.
	 */
	protected Cursor getRotateCursor() {
		if (rotateCursor == null) {
			rotateCursor = createRotateCursor();
		}
		return rotateCursor;
	}

	/**
	 * Returns the target parts of this policy.
	 *
	 * @return The target parts of this policy.
	 */
	protected List<IContentPart<? extends Node>> getTargetParts() {
		return targetParts;
	}

	/**
	 * Returns the {@link TransformPolicy} that is installed on the given
	 * {@link IVisualPart}.
	 *
	 * @param part
	 *            The {@link IVisualPart} of which the {@link TransformPolicy}
	 *            is returned.
	 * @return The {@link TransformPolicy} that is installed on the given
	 *         {@link IVisualPart}.
	 */
	protected TransformPolicy getTransformPolicy(
			IVisualPart<? extends Node> part) {
		return part.getAdapter(TransformPolicy.class);
	}

	@Override
	public void hideIndicationCursor() {
		getCursorSupport().restoreCursor();
	}

	/**
	 * Returns <code>true</code> if the given {@link MouseEvent} should trigger
	 * rotation. Otherwise returns <code>false</code>. Per default returns
	 * <code>true</code> if <code>&lt;Control&gt;</code> is pressed.
	 *
	 * @param event
	 *            The {@link MouseEvent} in question.
	 * @return <code>true</code> if the given {@link MouseEvent} should trigger
	 *         rotation, otherwise <code>false</code>.
	 */
	protected boolean isRotate(MouseEvent event) {
		return event.isControlDown();
	}

	/**
	 * If the given flag <i>isControlDown</i> is <code>true</code>, then the
	 * mouse cursor is changed to a rotate cursor. Otherwise, the mouse cursor
	 * is not changed. Returns <code>true</code> if the mouse cursor was
	 * changed. Otherwise returns <code>false</code>.
	 *
	 * @param isControlDown
	 *            Flag to indicate if the control modifier key is pressed.
	 * @return <code>true</code> if the mouse cursor was changed, otherwise
	 *         <code>false</code>.
	 */
	protected boolean showIndicationCursor(boolean isControlDown) {
		if (isControlDown) {
			getCursorSupport().storeAndReplaceCursor(getRotateCursor());
			return true;
		}
		return false;
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return showIndicationCursor(event.isControlDown());
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		return showIndicationCursor(event.isControlDown());
	}

	@Override
	public void startDrag(MouseEvent e) {
		// do nothing when the user does not press control
		invalidGesture = !isRotate(e);
		if (invalidGesture) {
			return;
		}

		// save pointer location for later angle calculation
		initialPointerLocationInScene = new Point(e.getSceneX(), e.getSceneY());
		targetParts = determineTargetParts();
		if (targetParts == null) {
			targetParts = Collections.emptyList();
		}
		Rectangle bounds = PartUtils.getUnionedVisualBoundsInScene(targetParts);
		if (bounds == null) {
			throw new IllegalStateException(
					"Cannot determine visual bounds (null).");
		}
		pivotInScene = bounds.getCenter();

		// initialize for all target parts
		rotationIndices.clear();
		for (IContentPart<? extends Node> part : getTargetParts()) {
			// transform pivot point to local coordinates
			TransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				storeAndDisableRefreshVisuals(part);
				init(transformPolicy);
				// transform pivot to parent coordinates
				Point pivotInLocal = FX2Geometry
						.toPoint(getHost().getVisual().getParent()
								.sceneToLocal(pivotInScene.x, pivotInScene.y));
				// create transformations
				int translateIndex = transformPolicy.createPostTransform();
				int rotateIndex = transformPolicy.createPostTransform();
				int translateBackIndex = transformPolicy.createPostTransform();
				// set translation transforms
				transformPolicy.setPostTranslate(translateIndex,
						-pivotInLocal.x, -pivotInLocal.y);
				transformPolicy.setPostTranslate(translateBackIndex,
						pivotInLocal.x, pivotInLocal.y);
				// save rotation index for later adjustment
				rotationIndices.put(part, rotateIndex);
			}
		}
	}

	private void updateOperation(MouseEvent e,
			IVisualPart<? extends Node> part) {
		// determine scaling
		TransformPolicy transformPolicy = getTransformPolicy(part);
		if (transformPolicy != null) {
			transformPolicy.setPostRotate(rotationIndices.get(part),
					computeRotationAngleCW(e, part));
		}
	}

}

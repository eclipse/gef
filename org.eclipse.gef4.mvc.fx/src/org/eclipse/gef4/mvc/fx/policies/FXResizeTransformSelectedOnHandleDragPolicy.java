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
package org.eclipse.gef4.mvc.fx.policies;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.gef4.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXSegmentHandlePart;
import org.eclipse.gef4.mvc.models.GridModel;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.AbstractTransformPolicy;

import com.google.common.reflect.TypeToken;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * The {@link FXResizeTransformSelectedOnHandleDragPolicy} is an
 * {@link IFXOnDragPolicy} that relocates and scales the whole
 * {@link SelectionModel selection} when its host (a box selection handle,
 * {@link AbstractFXSegmentHandlePart}) is dragged.
 *
 * @author mwienand
 *
 */
public class FXResizeTransformSelectedOnHandleDragPolicy
		extends AbstractFXInteractionPolicy implements IFXOnDragPolicy {

	private Point initialMouseLocation = null;
	private Rectangle selectionBounds;
	private Map<IContentPart<Node, ? extends Node>, Double> relX1 = null;
	private Map<IContentPart<Node, ? extends Node>, Double> relY1 = null;
	private Map<IContentPart<Node, ? extends Node>, Double> relX2 = null;
	private Map<IContentPart<Node, ? extends Node>, Double> relY2 = null;
	private boolean invalidGesture = false;
	private Map<IContentPart<Node, ? extends Node>, Integer> scaleIndices = new HashMap<>();
	private Map<IContentPart<Node, ? extends Node>, Integer> translateIndices = new HashMap<>();
	private CursorSupport cursorSupport = new CursorSupport(this);
	private List<IContentPart<Node, ? extends Node>> targetParts;

	/**
	 * Default constructor.
	 */
	public FXResizeTransformSelectedOnHandleDragPolicy() {
	}

	/**
	 * Computes the relative x and y coordinates for the given target part and
	 * stores them in the {@link #relX1}, {@link #relY1}, {@link #relX2}, and
	 * {@link #relY2} maps.
	 *
	 * @param targetPart
	 */
	private void computeRelatives(
			IContentPart<Node, ? extends Node> targetPart) {
		Rectangle bounds = getVisualBounds(targetPart);

		double left = bounds.getX() - selectionBounds.getX();
		relX1.put(targetPart, left / selectionBounds.getWidth());

		double right = left + bounds.getWidth();
		relX2.put(targetPart, right / selectionBounds.getWidth());

		double top = bounds.getY() - selectionBounds.getY();
		relY1.put(targetPart, top / selectionBounds.getHeight());

		double bottom = top + bounds.getHeight();
		relY2.put(targetPart, bottom / selectionBounds.getHeight());
	}

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		if (invalidGesture) {
			return;
		}

		if (selectionBounds == null) {
			return;
		}
		if (targetParts.isEmpty()) {
			return;
		}

		// snap to grid
		IContentPart<Node, ? extends Node> firstTargetPart = targetParts.get(0);
		Node firstVisual = firstTargetPart.getVisual();
		Point2D endPointInParent = firstVisual.localToParent(
				firstVisual.sceneToLocal(e.getSceneX(), e.getSceneY()));
		Dimension snapToGridOffset = AbstractTransformPolicy
				.getSnapToGridOffset(
						getHost().getRoot().getViewer().<GridModel> getAdapter(
								GridModel.class),
						endPointInParent.getX(), endPointInParent.getY(),
						getSnapToGridGranularityX(),
						getSnapToGridGranularityY());
		endPointInParent = new Point2D(
				endPointInParent.getX() - snapToGridOffset.width,
				endPointInParent.getY() - snapToGridOffset.height);
		Point2D endPointInScene = firstVisual.getParent()
				.localToScene(endPointInParent);
		// update selection bounds
		Rectangle sel = updateSelectionBounds(
				FX2Geometry.toPoint(endPointInScene));

		for (IContentPart<Node, ? extends Node> targetPart : targetParts) {
			// compute initial and new bounds for this target
			Bounds initialBounds = getBounds(selectionBounds, targetPart);
			Bounds newBounds = getBounds(sel, targetPart);

			// compute translation in scene coordinates
			double dx = newBounds.getMinX() - initialBounds.getMinX();
			double dy = newBounds.getMinY() - initialBounds.getMinY();

			// transform translation to parent coordinates
			Node visual = targetPart.getVisual();
			Point2D originInParent = visual.getParent().sceneToLocal(0, 0);
			Point2D deltaInParent = visual.getParent().sceneToLocal(dx, dy);
			dx = deltaInParent.getX() - originInParent.getX();
			dy = deltaInParent.getY() - originInParent.getY();

			// apply translation
			getTransformPolicy(targetPart)
					.setPostTranslate(translateIndices.get(targetPart), dx, dy);

			// check if we can resize the part
			AffineTransform affineTransform = getTransformPolicy(targetPart)
					.getCurrentTransform();
			if (affineTransform.getRotation().equals(Angle.fromDeg(0))) {
				// no rotation => resize possible
				// TODO: special case 90 degree rotations
				double dw = newBounds.getWidth() - initialBounds.getWidth();
				double dh = newBounds.getHeight() - initialBounds.getHeight();
				Point2D originInLocal = visual.sceneToLocal(newBounds.getMinX(),
						newBounds.getMinY());
				Point2D dstInLocal = visual.sceneToLocal(
						newBounds.getMinX() + dw, newBounds.getMinY() + dh);
				dw = dstInLocal.getX() - originInLocal.getX();
				dh = dstInLocal.getY() - originInLocal.getY();
				getResizePolicy(targetPart).resize(dw, dh);
			} else {
				// compute scaling based on bounds change
				double sx = newBounds.getWidth() / initialBounds.getWidth();
				double sy = newBounds.getHeight() / initialBounds.getHeight();
				// apply scaling
				getTransformPolicy(targetPart)
						.setPostScale(scaleIndices.get(targetPart), sx, sy);
			}
		}
	}

	@Override
	public void dragAborted() {
		if (invalidGesture) {
			return;
		}

		// rollback transactional policies
		for (IContentPart<Node, ? extends Node> part : targetParts) {
			FXTransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				restoreRefreshVisuals(part);
				rollback(transformPolicy);
				FXResizePolicy resizePolicy = getResizePolicy(part);
				if (resizePolicy != null) {
					rollback(resizePolicy);
				}
			}
		}

		// clear transformation indices lists
		scaleIndices.clear();
		translateIndices.clear();
		// null resize context vars
		selectionBounds = null;
		initialMouseLocation = null;
		relX1 = relY1 = relX2 = relY2 = null;
	}

	private Bounds getBounds(Rectangle sel,
			IContentPart<Node, ? extends Node> targetPart) {
		double x1 = sel.getX() + sel.getWidth() * relX1.get(targetPart);
		double x2 = sel.getX() + sel.getWidth() * relX2.get(targetPart);
		double y1 = sel.getY() + sel.getHeight() * relY1.get(targetPart);
		double y2 = sel.getY() + sel.getHeight() * relY2.get(targetPart);
		return new BoundingBox(x1, y1, x2 - x1, y2 - y1);
	}

	/**
	 * Returns the {@link CursorSupport} of this policy.
	 *
	 * @return The {@link CursorSupport} of this policy.
	 */
	protected CursorSupport getCursorSupport() {
		return cursorSupport;
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractFXSegmentHandlePart<Node> getHost() {
		return (AbstractFXSegmentHandlePart<Node>) super.getHost();
	}

	/**
	 * Returns the {@link FXResizePolicy} that is installed on the given
	 * {@link IContentPart}.
	 *
	 * @param part
	 *            The {@link IContentPart} of which the {@link FXResizePolicy}
	 *            is returned.
	 * @return The {@link FXResizePolicy} that is installed on the given
	 *         {@link IContentPart}.
	 */
	protected FXResizePolicy getResizePolicy(
			IContentPart<Node, ? extends Node> part) {
		return part.getAdapter(FXResizePolicy.class);
	}

	/**
	 * Returns the unioned {@link #getVisualBounds(IContentPart) bounds} of all
	 * target parts.
	 *
	 * @param targetParts
	 * @return the unioned visual bounds of all target parts
	 */
	private Rectangle getSelectionBounds(
			List<IContentPart<Node, ? extends Node>> targetParts) {
		if (targetParts.isEmpty()) {
			throw new IllegalArgumentException("No target parts given.");
		}

		Rectangle bounds = getVisualBounds(targetParts.get(0));
		if (targetParts.size() == 1) {
			return bounds;
		}

		ListIterator<IContentPart<Node, ? extends Node>> iterator = targetParts
				.listIterator(1);
		while (iterator.hasNext()) {
			IContentPart<Node, ? extends Node> cp = iterator.next();
			bounds.union(getVisualBounds(cp));
		}
		return bounds;
	}

	/**
	 * Returns the horizontal granularity for "snap-to-grid" where
	 * <code>1</code> means it will snap to integer grid positions.
	 *
	 * @return The horizontal granularity for "snap-to-grid".
	 */
	protected double getSnapToGridGranularityX() {
		return 1;
	}

	/**
	 * Returns the vertical granularity for "snap-to-grid" where <code>1</code>
	 * means it will snap to integer grid positions.
	 *
	 * @return The vertical granularity for "snap-to-grid".
	 */
	protected double getSnapToGridGranularityY() {
		return 1;
	}

	/**
	 * Returns a {@link List} containing all {@link IContentPart}s that should
	 * be scaled/relocated by this policy. Per default, the whole
	 * {@link SelectionModel selection} is returned.
	 *
	 * @return A {@link List} containing all {@link IContentPart}s that should
	 *         be scaled/relocated by this policy.
	 */
	@SuppressWarnings("serial")
	protected List<IContentPart<Node, ? extends Node>> getTargetParts() {
		return getHost().getRoot().getViewer()
				.getAdapter(new TypeToken<SelectionModel<Node>>() {
				}).getSelectionUnmodifiable();
	}

	/**
	 * Returns the {@link FXTransformPolicy} that is installed on the given
	 * {@link IContentPart}.
	 *
	 * @param part
	 *            The {@link IContentPart} of which the
	 *            {@link FXTransformPolicy} is returned.
	 * @return The {@link FXTransformPolicy} that is installed on the given
	 *         {@link IContentPart}.
	 */
	protected FXTransformPolicy getTransformPolicy(
			IContentPart<Node, ? extends Node> part) {
		return part.getAdapter(FXTransformPolicy.class);
	}

	/**
	 * Returns a {@link Rectangle} representing the visual bounds of the given
	 * {@link IContentPart} within the coordinate system of the {@link Scene}.
	 *
	 * @param contentPart
	 *            The {@link IContentPart} of which the visual bounds are
	 *            computed.
	 * @return A {@link Rectangle} representing the visual bounds of the given
	 *         {@link IContentPart} within the coordinate system of the
	 *         {@link Scene}.
	 */
	protected Rectangle getVisualBounds(
			IContentPart<Node, ? extends Node> contentPart) {
		if (contentPart == null) {
			throw new IllegalArgumentException("contentPart may not be null!");
		}
		return FX2Geometry.toRectangle(contentPart.getVisual()
				.localToScene(contentPart.getVisual().getLayoutBounds()));
	}

	@Override
	public void hideIndicationCursor() {
		getCursorSupport().restoreCursor();
	}

	/**
	 * Returns <code>true</code> if the given {@link MouseEvent} should trigger
	 * resize and transform of the selected parts. Otherwise returns
	 * <code>false</code>. Per default, returns <code>true</code> if
	 * <code>&lt;Control&gt;</code> is not pressed and at least two target parts
	 * are present.
	 *
	 * @param event
	 *            The {@link ScrollEvent} in question.
	 * @return <code>true</code> to indicate that the given {@link ScrollEvent}
	 *         should trigger panning, otherwise <code>false</code>.
	 */
	protected boolean isResizeTransform(MouseEvent event) {
		return targetParts.size() > 1 && !event.isControlDown();
	}

	@Override
	public void press(MouseEvent e) {
		targetParts = getTargetParts();
		invalidGesture = !isResizeTransform(e);
		if (invalidGesture) {
			return;
		}
		// init resize context vars
		initialMouseLocation = new Point(e.getSceneX(), e.getSceneY());
		selectionBounds = getSelectionBounds(targetParts);
		relX1 = new HashMap<>();
		relY1 = new HashMap<>();
		relX2 = new HashMap<>();
		relY2 = new HashMap<>();
		// init scale relocate policies
		for (IContentPart<Node, ? extends Node> targetPart : targetParts) {
			FXTransformPolicy transformPolicy = getTransformPolicy(targetPart);
			if (transformPolicy != null) {
				storeAndDisableRefreshVisuals(targetPart);
				computeRelatives(targetPart);
				init(transformPolicy);
				// transform scale pivot to parent coordinates
				Point pivotInScene = getVisualBounds(targetPart).getTopLeft();
				Point pivotInParent = FX2Geometry
						.toPoint(getHost().getVisual().getParent()
								.sceneToLocal(pivotInScene.x, pivotInScene.y));
				// create transformations for scaling around pivot
				int translateToOriginIndex = transformPolicy
						.createPostTransform();
				int scaleIndex = transformPolicy.createPostTransform();
				int translateBackIndex = transformPolicy.createPostTransform();
				// set translation transforms for scaling
				transformPolicy.setPostTranslate(translateToOriginIndex,
						-pivotInParent.x, -pivotInParent.y);
				transformPolicy.setPostTranslate(translateBackIndex,
						pivotInParent.x, pivotInParent.y);
				// save rotation index for later adjustments
				scaleIndices.put(targetPart, scaleIndex);
				// create transform for translation of the target part
				translateIndices.put(targetPart,
						transformPolicy.createPostTransform());
				// initialize resize policy if available
				FXResizePolicy resizePolicy = getResizePolicy(targetPart);
				if (resizePolicy != null) {
					init(resizePolicy);
				}
			}
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		if (invalidGesture) {
			invalidGesture = false;
			return;
		}

		for (IContentPart<Node, ? extends Node> part : targetParts) {
			FXTransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				restoreRefreshVisuals(part);
				commit(transformPolicy);
				FXResizePolicy resizePolicy = getResizePolicy(part);
				if (resizePolicy != null) {
					commit(resizePolicy);
				}
			}
		}
		// clear transformation indices lists
		scaleIndices.clear();
		translateIndices.clear();
		// null resize context vars
		selectionBounds = null;
		initialMouseLocation = null;
		relX1 = relY1 = relX2 = relY2 = null;
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		return false;
	}

	/**
	 * Returns updated selection bounds. The initial selection bounds are copied
	 * and the copy is shrinked or expanded depending on the mouse location
	 * change and the handle edge (top, bottom, left, or right).
	 *
	 * @param mouseLocation
	 * @return
	 */
	private Rectangle updateSelectionBounds(Point endPointInScene) {
		Rectangle sel = selectionBounds.getCopy();

		double dx = endPointInScene.x - initialMouseLocation.x;
		double dy = endPointInScene.y - initialMouseLocation.y;

		int segment = getHost().getSegmentIndex();
		if (segment == 0 || segment == 3) {
			sel.shrink(dx, 0, 0, 0);
		} else if (segment == 1 || segment == 2) {
			sel.expand(0, 0, dx, 0);
		}

		if (segment == 0 || segment == 1) {
			sel.shrink(0, dy, 0, 0);
		} else if (segment == 2 || segment == 3) {
			sel.expand(0, 0, 0, dy);
		}

		return sel;
	}

}

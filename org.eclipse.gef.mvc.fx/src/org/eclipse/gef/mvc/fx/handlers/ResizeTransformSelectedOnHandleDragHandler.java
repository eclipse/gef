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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.gef.geometry.convert.fx.FX2Geometry;
import org.eclipse.gef.geometry.convert.fx.Geometry2FX;
import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.parts.AbstractSegmentHandlePart;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.policies.ResizePolicy;
import org.eclipse.gef.mvc.fx.policies.TransformPolicy;
import org.eclipse.gef.mvc.fx.providers.ResizableTransformableBoundsProvider;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * The {@link ResizeTransformSelectedOnHandleDragHandler} is an
 * {@link IOnDragHandler} that relocates and scales the whole
 * {@link SelectionModel selection} when its host (a box selection handle,
 * {@link AbstractSegmentHandlePart}) is dragged.
 *
 * @author mwienand
 *
 */
public class ResizeTransformSelectedOnHandleDragHandler extends AbstractHandler
		implements IOnDragHandler {

	private Point initialMouseLocation = null;
	private Rectangle selectionBounds;
	private Map<IContentPart<? extends Node>, Double> relX1 = null;
	private Map<IContentPart<? extends Node>, Double> relY1 = null;
	private Map<IContentPart<? extends Node>, Double> relX2 = null;
	private Map<IContentPart<? extends Node>, Double> relY2 = null;
	private boolean invalidGesture = false;
	private Map<IContentPart<? extends Node>, Integer> scaleIndices = new HashMap<>();
	private Map<IContentPart<? extends Node>, Integer> translateIndices = new HashMap<>();
	private List<IContentPart<? extends Node>> targetParts;

	/**
	 * Default constructor.
	 */
	public ResizeTransformSelectedOnHandleDragHandler() {
	}

	@Override
	public void abortDrag() {
		if (invalidGesture) {
			return;
		}

		// rollback transactional policies
		for (IContentPart<? extends Node> part : targetParts) {
			TransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				restoreRefreshVisuals(part);
				rollback(transformPolicy);
				ResizePolicy resizePolicy = getResizePolicy(part);
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

	/**
	 * Computes the relative x and y coordinates for the given target part and
	 * stores them in the {@link #relX1}, {@link #relY1}, {@link #relX2}, and
	 * {@link #relY2} maps.
	 *
	 * @param targetPart
	 */
	private void computeRelatives(IContentPart<? extends Node> targetPart) {
		Rectangle bounds = getVisualBounds(targetPart);
		if (bounds != null) {
			double left = bounds.getX() - selectionBounds.getX();
			double right = left + bounds.getWidth();
			double top = bounds.getY() - selectionBounds.getY();
			double bottom = top + bounds.getHeight();
			double selWidth = selectionBounds.getWidth();
			double selHeight = selectionBounds.getHeight();
			relX1.put(targetPart, left / selWidth);
			relX2.put(targetPart, right / selWidth);
			relY1.put(targetPart, top / selHeight);
			relY2.put(targetPart, bottom / selHeight);
		}
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
		// FIXME: apply resize-transform first, then snap the moved vertex to
		// the next grid position and update the values

		// TODO
		Point newEndPointInScene = isPrecise(e)
				? new Point(e.getSceneX(), e.getSceneY())
				: new Point(e.getSceneX(), e.getSceneY());

		// update selection bounds
		Rectangle sel = updateSelectionBounds(newEndPointInScene);

		// update target parts
		for (IContentPart<? extends Node> targetPart : targetParts) {
			// compute initial and new bounds for this target
			Bounds initialBounds = getBounds(selectionBounds, targetPart);
			Bounds newBounds = getBounds(sel, targetPart);

			// System.out.println(targetPart.getClass().getSimpleName()
			// + " bounds change from " + initialBounds.getMinX() + ", "
			// + initialBounds.getMinY() + " : " + initialBounds.getWidth()
			// + " x " + initialBounds.getHeight() + " to "
			// + newBounds.getMinX() + ", " + newBounds.getMinY() + " : "
			// + newBounds.getWidth() + " x " + newBounds.getHeight()
			// + ".");

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

				// System.out.println(
				// "delta size in scene: " + dw + ", " + dh + ".");

				Point2D originInLocal = visual.sceneToLocal(newBounds.getMinX(),
						newBounds.getMinY());
				Point2D dstInLocal = visual.sceneToLocal(
						newBounds.getMinX() + dw, newBounds.getMinY() + dh);
				dw = dstInLocal.getX() - originInLocal.getX();
				dh = dstInLocal.getY() - originInLocal.getY();

				// System.out.println(
				// "delta size in local: " + dw + ", " + dh + ".");

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
	public void endDrag(MouseEvent e, Dimension delta) {
		if (invalidGesture) {
			invalidGesture = false;
			return;
		}

		for (IContentPart<? extends Node> part : targetParts) {
			TransformPolicy transformPolicy = getTransformPolicy(part);
			if (transformPolicy != null) {
				restoreRefreshVisuals(part);
				commit(transformPolicy);
				ResizePolicy resizePolicy = getResizePolicy(part);
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

	private Bounds getBounds(Rectangle sel,
			IContentPart<? extends Node> targetPart) {
		double x1 = sel.getX() + sel.getWidth() * relX1.get(targetPart);
		double x2 = sel.getX() + sel.getWidth() * relX2.get(targetPart);
		double y1 = sel.getY() + sel.getHeight() * relY1.get(targetPart);
		double y2 = sel.getY() + sel.getHeight() * relY2.get(targetPart);
		return new BoundingBox(x1, y1, x2 - x1, y2 - y1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractSegmentHandlePart<Node> getHost() {
		return (AbstractSegmentHandlePart<Node>) super.getHost();
	}

	/**
	 * Returns the {@link ResizePolicy} that is installed on the given
	 * {@link IContentPart}.
	 *
	 * @param part
	 *            The {@link IContentPart} of which the {@link ResizePolicy} is
	 *            returned.
	 * @return The {@link ResizePolicy} that is installed on the given
	 *         {@link IContentPart}.
	 */
	protected ResizePolicy getResizePolicy(IContentPart<? extends Node> part) {
		return part.getAdapter(ResizePolicy.class);
	}

	/**
	 * Returns the unioned {@link #getVisualBounds(IContentPart) bounds} of all
	 * target parts.
	 *
	 * @param targetParts
	 * @return the unioned visual bounds of all target parts
	 */
	private Rectangle getSelectionBounds(
			List<IContentPart<? extends Node>> targetParts) {
		if (targetParts.isEmpty()) {
			throw new IllegalArgumentException("No target parts given.");
		}

		Rectangle bounds = getVisualBounds(targetParts.get(0));
		if (targetParts.size() == 1) {
			return bounds;
		}

		ListIterator<IContentPart<? extends Node>> iterator = targetParts
				.listIterator(1);
		while (iterator.hasNext()) {
			IContentPart<? extends Node> cp = iterator.next();
			if (bounds == null) {
				bounds = getVisualBounds(cp);
			} else {
				bounds.union(getVisualBounds(cp));
			}
		}
		return bounds;
	}

	/**
	 * Returns a {@link List} containing all {@link IContentPart}s that should
	 * be scaled/relocated by this policy. Per default, the whole
	 * {@link SelectionModel selection} is returned.
	 *
	 * @return A {@link List} containing all {@link IContentPart}s that should
	 *         be scaled/relocated by this policy.
	 */
	protected List<IContentPart<? extends Node>> getTargetParts() {
		List<IContentPart<? extends Node>> selection = new ArrayList<>(
				getHost().getRoot().getViewer().getAdapter(SelectionModel.class)
						.getSelectionUnmodifiable());
		// filter out parts without transformable-resizable bounds
		Iterator<IContentPart<? extends Node>> it = selection.iterator();
		while (it.hasNext()) {
			if (getVisualBounds(it.next()) == null) {
				it.remove();
			}
		}
		return selection;
	}

	/**
	 * Returns the {@link TransformPolicy} that is installed on the given
	 * {@link IContentPart}.
	 *
	 * @param part
	 *            The {@link IContentPart} of which the {@link TransformPolicy}
	 *            is returned.
	 * @return The {@link TransformPolicy} that is installed on the given
	 *         {@link IContentPart}.
	 */
	protected TransformPolicy getTransformPolicy(
			IContentPart<? extends Node> part) {
		return part.getAdapter(TransformPolicy.class);
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
			IContentPart<? extends Node> contentPart) {
		if (contentPart == null) {
			throw new IllegalArgumentException("contentPart may not be null!");
		}

		// use provider to compute bounds
		ResizableTransformableBoundsProvider boundsProvider = new ResizableTransformableBoundsProvider();
		boundsProvider.setAdaptable(contentPart);
		IGeometry boundsInLocal = boundsProvider.get();

		// transform to scene
		return boundsInLocal == null ? null
				: FX2Geometry.toRectangle(contentPart.getVisual().localToScene(
						Geometry2FX.toFXBounds(boundsInLocal.getBounds())));
	}

	@Override
	public void hideIndicationCursor() {
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
		return targetParts.size() > 0 && !event.isControlDown()
				&& getHost().getRoot().getViewer()
						.getAdapter(SelectionModel.class)
						.getSelectionUnmodifiable().size() > 1;
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
		for (IContentPart<? extends Node> targetPart : targetParts) {
			TransformPolicy transformPolicy = getTransformPolicy(targetPart);
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
				ResizePolicy resizePolicy = getResizePolicy(targetPart);
				if (resizePolicy != null) {
					init(resizePolicy);
				}
			}
		}
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

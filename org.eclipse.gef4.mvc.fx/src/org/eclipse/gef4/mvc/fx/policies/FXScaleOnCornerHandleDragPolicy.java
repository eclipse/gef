/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXSegmentHandlePart;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.IContentPart;

// Only applicable for AbstractFXCornerHandlepart
public class FXScaleOnCornerHandleDragPolicy extends AbstractFXDragPolicy {

	/*
	 * TODO: allow negative scaling
	 */

	private Point initialMouseLocation = null;
	private Rectangle selectionBounds;
	private Map<IContentPart<Node, ? extends Node>, Double> relX1 = null;
	private Map<IContentPart<Node, ? extends Node>, Double> relY1 = null;
	private Map<IContentPart<Node, ? extends Node>, Double> relX2 = null;
	private Map<IContentPart<Node, ? extends Node>, Double> relY2 = null;

	public FXScaleOnCornerHandleDragPolicy() {
	}

	/**
	 * Computes the relative x and y coordinates for the given target part and
	 * stores them in the {@link #relX1}, {@link #relY1}, {@link #relX2}, and
	 * {@link #relY2} maps.
	 *
	 * @param targetPart
	 */
	private void computeRelatives(IContentPart<Node, ? extends Node> targetPart) {
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
		if (selectionBounds == null) {
			return;
		}
		Rectangle sel = updateSelectionBounds(e);
		for (IContentPart<Node, ? extends Node> targetPart : getTargetParts()) {
			double[] initialBounds = getBounds(selectionBounds, targetPart);
			double[] newBounds = getBounds(sel, targetPart);

			// transform initialBounds to target space
			Node visual = targetPart.getVisual();
			Point2D initialTopLeft = visual.sceneToLocal(initialBounds[0],
					initialBounds[1]);
			Point2D initialBotRight = visual.sceneToLocal(initialBounds[2],
					initialBounds[3]);

			// transform newBounds to target space
			Point2D newTopLeft = visual
					.sceneToLocal(newBounds[0], newBounds[1]);
			Point2D newBotRight = visual.sceneToLocal(newBounds[2],
					newBounds[3]);

			double dx = newTopLeft.getX() - initialTopLeft.getX();
			double dy = newTopLeft.getY() - initialTopLeft.getY();
			double dw = (newBotRight.getX() - newTopLeft.getX())
					- (initialBotRight.getX() - initialTopLeft.getX());
			double dh = (newBotRight.getY() - newTopLeft.getY())
					- (initialBotRight.getY() - initialTopLeft.getY());

			if (getResizeRelocatePolicy(targetPart) != null) {
				getResizeRelocatePolicy(targetPart).performResizeRelocate(dx,
						dy, dw, dh);
			}
		}
	}

	private double[] getBounds(Rectangle sel,
			IContentPart<Node, ? extends Node> targetPart) {
		double x1 = sel.getX() + sel.getWidth() * relX1.get(targetPart);
		double x2 = sel.getX() + sel.getWidth() * relX2.get(targetPart);
		double y1 = sel.getY() + sel.getHeight() * relY1.get(targetPart);
		double y2 = sel.getY() + sel.getHeight() * relY2.get(targetPart);
		return new double[] { x1, y1, x2, y2 };
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractFXSegmentHandlePart<Node> getHost() {
		return (AbstractFXSegmentHandlePart<Node>) super.getHost();
	}

	protected FXResizeRelocatePolicy getResizeRelocatePolicy(
			IContentPart<Node, ? extends Node> part) {
		return part.getAdapter(FXResizeRelocatePolicy.class);
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

	public List<IContentPart<Node, ? extends Node>> getTargetParts() {
		return getHost().getRoot().getViewer()
				.<SelectionModel<Node>> getAdapter(SelectionModel.class)
				.getSelected();
	}

	protected Rectangle getVisualBounds(
			IContentPart<Node, ? extends Node> contentPart) {
		if (contentPart == null) {
			throw new IllegalArgumentException("contentPart may not be null!");
		}
		return JavaFX2Geometry.toRectangle(contentPart.getVisual()
				.localToScene(contentPart.getVisual().getBoundsInLocal()));
	}

	@Override
	public void press(MouseEvent e) {
		// init resize context vars
		initialMouseLocation = new Point(e.getSceneX(), e.getSceneY());
		selectionBounds = getSelectionBounds(getTargetParts());
		relX1 = new HashMap<IContentPart<Node, ? extends Node>, Double>();
		relY1 = new HashMap<IContentPart<Node, ? extends Node>, Double>();
		relX2 = new HashMap<IContentPart<Node, ? extends Node>, Double>();
		relY2 = new HashMap<IContentPart<Node, ? extends Node>, Double>();
		for (IContentPart<Node, ? extends Node> targetPart : getTargetParts()) {
			computeRelatives(targetPart);
			init(getResizeRelocatePolicy(targetPart));
		}
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		for (IContentPart<Node, ? extends Node> part : getTargetParts()) {
			FXResizeRelocatePolicy policy = getResizeRelocatePolicy(part);
			if (policy != null) {
				commit(policy);
			}
		}
		// null resize context vars
		selectionBounds = null;
		initialMouseLocation = null;
		relX1 = relY1 = relX2 = relY2 = null;
	}

	/**
	 * Returns updated selection bounds. The initial selection bounds are copied
	 * and the copy is shrinked or expanded depending on the mouse location
	 * change and the {@link #getReferencePoint() handle-edge}.
	 *
	 * @param mouseLocation
	 * @return
	 */
	private Rectangle updateSelectionBounds(MouseEvent e) {
		Rectangle sel = selectionBounds.getCopy();

		double dx = e.getSceneX() - initialMouseLocation.x;
		double dy = e.getSceneY() - initialMouseLocation.y;

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

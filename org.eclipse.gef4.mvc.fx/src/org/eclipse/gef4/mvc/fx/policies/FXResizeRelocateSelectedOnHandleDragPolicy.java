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

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.parts.IContentPart;

// TODO: refactor
public class FXResizeRelocateSelectedOnHandleDragPolicy extends
		AbstractFXDragPolicy {

	public FXResizeRelocateSelectedOnHandleDragPolicy(ReferencePoint refPoint) {
		this.referencePoint = refPoint;
	}

	protected Rectangle getVisualBounds(IContentPart<Node> contentPart) {
		if (contentPart == null) {
			throw new IllegalArgumentException("contentPart may not be null!");
		}
		return JavaFX2Geometry.toRectangle(contentPart.getVisual()
				.localToScene(contentPart.getVisual().getBoundsInLocal()));
	}

	/*
	 * TODO: allow negative scaling
	 */

	/**
	 * <p>
	 * Specifies the position of the "resize handle" that is used to resize the
	 * target parts. This resize tool needs to know which edge(s) of the
	 * selection bounds are being dragged in order to compute correct new
	 * selection bounds.
	 * </p>
	 * <p>
	 * Therefore, the individual HandleEdge constants provide methods
	 * {@link #isTop()}, {@link #isLeft()}, {@link #isRight()}, and
	 * {@link #isBottom()} to evaluate if the top, left, right, or bottom edges
	 * are affected, respectively.
	 * </p>
	 */
	public static enum ReferencePoint {
		TOP(true, false, false, false), LEFT(false, true, false, false), RIGHT(
				false, false, true, false), BOTTOM(false, false, false, true), TOP_LEFT(
				true, true, false, false), TOP_RIGHT(true, false, true, false), BOTTOM_LEFT(
				false, true, false, true), BOTTOM_RIGHT(false, false, true,
				true);

		private boolean t, l, r, b;

		private ReferencePoint(boolean top, boolean left, boolean right,
				boolean bottom) {
			t = top;
			l = left;
			r = right;
			b = bottom;
		}

		public boolean isTop() {
			return t;
		}

		public boolean isLeft() {
			return l;
		}

		public boolean isRight() {
			return r;
		}

		public boolean isBottom() {
			return b;
		}
	}

	private Point initialMouseLocation = null;
	private Rectangle selectionBounds;
	private ReferencePoint referencePoint = null;
	private Map<IContentPart<Node>, Double> relX1 = null;
	private Map<IContentPart<Node>, Double> relY1 = null;
	private Map<IContentPart<Node>, Double> relX2 = null;
	private Map<IContentPart<Node>, Double> relY2 = null;

	protected FXResizeRelocatePolicy getResizeRelocatePolicy(
			IContentPart<Node> editPart) {
		return editPart.getBound(FXResizeRelocatePolicy.class);
	}

	public List<IContentPart<Node>> getTargetParts() {
		return getHost().getRoot().getViewer().getSelectionModel()
				.getSelected();
	}

	@Override
	public void press(MouseEvent e) {
		// init resize context vars
		initialMouseLocation = new Point(e.getSceneX(), e.getSceneY());
		selectionBounds = getSelectionBounds(getTargetParts());
		relX1 = new HashMap<IContentPart<Node>, Double>();
		relY1 = new HashMap<IContentPart<Node>, Double>();
		relX2 = new HashMap<IContentPart<Node>, Double>();
		relY2 = new HashMap<IContentPart<Node>, Double>();
		for (IContentPart<Node> targetPart : getTargetParts()) {
			computeRelatives(targetPart);
			if (getResizeRelocatePolicy(targetPart) != null) {
				getResizeRelocatePolicy(targetPart).initResizeRelocate();
			}
		}
	}

	/**
	 * Computes the relative x and y coordinates for the given target part and
	 * stores them in the {@link #relX1}, {@link #relY1}, {@link #relX2}, and
	 * {@link #relY2} maps.
	 * 
	 * @param targetPart
	 */
	private void computeRelatives(IContentPart<Node> targetPart) {
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

	/**
	 * Returns the unioned {@link #getVisualBounds(IContentPart) bounds} of all
	 * target parts.
	 * 
	 * @param targetParts
	 * @return
	 */
	private Rectangle getSelectionBounds(List<IContentPart<Node>> targetParts) {
		if (targetParts.isEmpty()) {
			throw new IllegalArgumentException("No target parts given.");
		}

		Rectangle bounds = getVisualBounds(targetParts.get(0));
		if (targetParts.size() == 1) {
			return bounds;
		}

		ListIterator<IContentPart<Node>> iterator = targetParts.listIterator(1);
		while (iterator.hasNext()) {
			IContentPart<Node> cp = iterator.next();
			bounds.union(getVisualBounds(cp));
		}
		return bounds;
	}

	@Override
	public void drag(MouseEvent e, Dimension delta, List<Node> nodesUnderMouse,
			List<IContentPart<Node>> partsUnderMouse) {
		if (selectionBounds == null) {
			return;
		}
		Rectangle sel = updateSelectionBounds(e);
		for (IContentPart<Node> targetPart : getTargetParts()) {
			double[] initialBounds = getBounds(selectionBounds, targetPart);
			double[] newBounds = getBounds(sel, targetPart);
			double dx = newBounds[0] - initialBounds[0];
			double dy = newBounds[1] - initialBounds[1];
			double dw = (newBounds[2] - newBounds[0])
					- (initialBounds[2] - initialBounds[0]);
			double dh = (newBounds[3] - newBounds[1])
					- (initialBounds[3] - initialBounds[1]);
			if (getResizeRelocatePolicy(targetPart) != null) {
				getResizeRelocatePolicy(targetPart).performResizeRelocate(dx,
						dy, dw, dh);
			}
		}
	}

	private double[] getBounds(Rectangle sel, IContentPart<Node> targetPart) {
		double x1 = sel.getX() + sel.getWidth() * relX1.get(targetPart);
		double x2 = sel.getX() + sel.getWidth() * relX2.get(targetPart);
		double y1 = sel.getY() + sel.getHeight() * relY1.get(targetPart);
		double y2 = sel.getY() + sel.getHeight() * relY2.get(targetPart);
		return new double[] { x1, y1, x2, y2 };
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

		if (referencePoint.isLeft()) {
			sel.shrink(dx, 0, 0, 0);
		} else if (referencePoint.isRight()) {
			sel.expand(0, 0, dx, 0);
		}

		if (referencePoint.isTop()) {
			sel.shrink(0, dy, 0, 0);
		} else if (referencePoint.isBottom()) {
			sel.expand(0, 0, 0, dy);
		}
		return sel;
	}

	@Override
	public void release(MouseEvent e, Dimension delta,
			List<Node> nodesUnderMouse, List<IContentPart<Node>> partsUnderMouse) {
		if (selectionBounds == null) {
			return;
		}
		Rectangle sel = updateSelectionBounds(e);
		for (IContentPart<Node> targetPart : getTargetParts()) {
			// use previously computed relative coordinates to get the visuals
			// bounds in the new selection area
			double x1 = sel.getX() + sel.getWidth() * relX1.get(targetPart);
			double x2 = sel.getX() + sel.getWidth() * relX2.get(targetPart);
			double y1 = sel.getY() + sel.getHeight() * relY1.get(targetPart);
			double y2 = sel.getY() + sel.getHeight() * relY2.get(targetPart);
			FXResizeRelocatePolicy resizeRelocatePolicy = getResizeRelocatePolicy(targetPart);
			if (resizeRelocatePolicy != null) {
				resizeRelocatePolicy.performResizeRelocate(x1, y1, x2 - x1, y2
						- y1);
				executeOperation(resizeRelocatePolicy.commit());
			}
		}

		// null resize context vars
		this.selectionBounds = null;
		initialMouseLocation = null;
		relX1 = relY1 = relX2 = relY2 = null;
	}
}

/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.tools;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.policies.AbstractResizeRelocatePolicy;

/**
 * <p>
 * Multi selection resize and relocate tool that preserves the relative visual
 * bounds of all selected target content parts within the collective selection
 * bounds.
 * </p>
 * <p>
 * The user controls a selection rectangle which encloses all selected content
 * parts. When the user alters the selection bounds by dragging its edges, this
 * tool computes new selection bounds for the change using the provided
 * {@link ReferencePoint}. The visuals are relocated and resized as if they
 * would be scaled to fit the new selection bounds.
 * </p>
 * 
 * @author anyssen
 * @author mwienand
 * 
 * @param <V>
 */
public abstract class AbstractResizeRelocateTool<V> extends AbstractTool<V> {

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
	private Map<IContentPart<V>, Double> relX1 = null;
	private Map<IContentPart<V>, Double> relY1 = null;
	private Map<IContentPart<V>, Double> relX2 = null;
	private Map<IContentPart<V>, Double> relY2 = null;

	@SuppressWarnings("unchecked")
	protected AbstractResizeRelocatePolicy<V> getResizeRelocatePolicy(
			IContentPart<V> editPart) {
		return editPart.getEditPolicy(AbstractResizeRelocatePolicy.class);
	}

	public List<IContentPart<V>> getTargetParts() {
		return getDomain().getViewer().getSelectionModel().getSelected();
	}

	/**
	 * Returns the to scene transformed bounds-in-parent of the given content
	 * part's visual.
	 * 
	 * @return
	 */
	protected abstract Rectangle getVisualBounds(IContentPart<V> contentPart);

	/**
	 * Returns the {@link ReferencePoint} for this resize operation.
	 * 
	 * @return
	 */
	protected abstract ReferencePoint getReferencePoint();

	public void initResize(Point mouseLocation) {
		// init resize context vars
		initialMouseLocation = mouseLocation;
		selectionBounds = getSelectionBounds(getTargetParts());
		referencePoint = getReferencePoint();
		relX1 = new HashMap<IContentPart<V>, Double>();
		relY1 = new HashMap<IContentPart<V>, Double>();
		relX2 = new HashMap<IContentPart<V>, Double>();
		relY2 = new HashMap<IContentPart<V>, Double>();

		for (IContentPart<V> targetPart : getTargetParts()) {
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
	private void computeRelatives(IContentPart<V> targetPart) {
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
	private Rectangle getSelectionBounds(List<IContentPart<V>> targetParts) {
		if (targetParts.isEmpty()) {
			throw new IllegalArgumentException("No target parts given.");
		}

		Rectangle bounds = getVisualBounds(targetParts.get(0));
		if (targetParts.size() == 1) {
			return bounds;
		}

		ListIterator<IContentPart<V>> iterator = targetParts.listIterator(1);
		while (iterator.hasNext()) {
			IContentPart<V> cp = iterator.next();
			bounds.union(getVisualBounds(cp));
		}
		return bounds;
	}

	public void performResize(Point mouseLocation) {
		Rectangle sel = updateSelectionBounds(mouseLocation);
		for (IContentPart<V> targetPart : getTargetParts()) {
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

	private double[] getBounds(Rectangle sel, IContentPart<V> targetPart) {
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
	private Rectangle updateSelectionBounds(Point mouseLocation) {
		Rectangle sel = selectionBounds.getCopy();

		double dx = mouseLocation.x - initialMouseLocation.x;
		double dy = mouseLocation.y - initialMouseLocation.y;

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

		referencePoint = getReferencePoint();
		return sel;
	}

	public void commitResize(Point mouseLocation) {
		Rectangle sel = updateSelectionBounds(mouseLocation);
		for (IContentPart<V> targetPart : getTargetParts()) {
			// use previously computed relative coordinates to get the visuals
			// bounds in the new selection area
			double x1 = sel.getX() + sel.getWidth() * relX1.get(targetPart);
			double x2 = sel.getX() + sel.getWidth() * relX2.get(targetPart);
			double y1 = sel.getY() + sel.getHeight() * relY1.get(targetPart);
			double y2 = sel.getY() + sel.getHeight() * relY2.get(targetPart);
			if (getResizeRelocatePolicy(targetPart) != null) {
				getResizeRelocatePolicy(targetPart).commitResizeRelocate(x1,
						y1, x2 - x1, y2 - y1);
			}
		}

		// null resize context vars
		this.selectionBounds = null;
		initialMouseLocation = null;
		referencePoint = null;
		relX1 = relY1 = relX2 = relY2 = null;
	}

}

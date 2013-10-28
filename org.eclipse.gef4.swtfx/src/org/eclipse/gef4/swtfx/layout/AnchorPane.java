/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swtfx.layout;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.INode;

public class AnchorPane extends Pane {

	private Map<INode, AnchorPaneConstraints> constraints = new HashMap<INode, AnchorPaneConstraints>();

	public AnchorPane() {
	}

	public void add(INode node, AnchorPaneConstraints constraints) {
		addChildNodes(node);
		this.constraints.put(node, constraints);
	}

	@Override
	public double computePrefHeight(double width) {
		// return super.computePrefHeight(width);
		return computePrefRect().getHeight();
	}

	private Rectangle computePrefRect() {
		double xMin = Double.MAX_VALUE;
		double xMax = Double.MIN_VALUE;
		double yMin = xMin;
		double yMax = xMax;

		for (INode child : getManagedChildren()) {
			Rectangle rect = computePrefRect(child);

			xMin = Math.min(xMin, rect.getX());
			xMax = Math.max(xMax, rect.getX() + rect.getWidth());
			yMin = Math.min(yMin, rect.getY());
			yMax = Math.max(yMax, rect.getY() + rect.getHeight());
		}

		return new Rectangle(xMin, yMin, xMax - xMin, yMax - yMin);
	}

	private Rectangle computePrefRect(INode child) {
		AnchorPaneConstraints c = constraints(child);
		Double left = c.getLeft();
		Double bottom = c.getBottom();
		Double right = c.getRight();
		Double top = c.getTop();

		double w = child.computePrefWidth(-1);
		double h = child.computePrefHeight(-1);

		double horizontalConstraints = (left == null ? 0 : left)
				+ (right == null ? 0 : right);
		double verticalConstraints = (top == null ? 0 : top)
				+ (bottom == null ? 0 : bottom);

		return new Rectangle(0, 0, w + horizontalConstraints, h
				+ verticalConstraints);
	}

	@Override
	public double computePrefWidth(double height) {
		// return super.computePrefWidth(height);
		return computePrefRect().getWidth();
	}

	private AnchorPaneConstraints constraints(INode child) {
		if (!constraints.containsKey(child)) {
			constraints.put(child, new AnchorPaneConstraints());
		}
		return constraints.get(child);
	}

	private Rectangle getPrefRect(INode child) {
		AnchorPaneConstraints c = constraints(child);
		Double left = c.getLeft();
		Double bottom = c.getBottom();
		Double right = c.getRight();
		Double top = c.getTop();

		child.autosize();
		double w = child.getLayoutBounds().getWidth();
		double h = child.getLayoutBounds().getHeight();

		double x = left == null ? right == null ? 0 : getWidth() - right - w
				: left;
		double y = top == null ? bottom == null ? 0 : getHeight() - bottom - h
				: top;
		double x2 = right == null ? x + w : getWidth() - right;
		double y2 = bottom == null ? y + h : getHeight() - bottom;

		w = x2 - x;
		h = y2 - y;

		if (w < 0) {
			w = 0;
		}
		if (h < 0) {
			h = 0;
		}

		return new Rectangle(x, y, w, h);
	}

	@Override
	public void layoutChildren() {
		// double availableWidth = getWidth();
		// double availableHeight = getHeight();

		for (INode child : getManagedChildren()) {
			Rectangle rect = getPrefRect(child);

			child.relocate(rect.getX(), rect.getY());
			if (child.isResizable()) {
				child.resize(rect.getWidth(), rect.getHeight());
			}
		}
	}

	@Override
	public String toString() {
		return "AnchorPane " + System.identityHashCode(this)
				+ " { children-count: " + getChildNodes().size() + " }";
	}

}

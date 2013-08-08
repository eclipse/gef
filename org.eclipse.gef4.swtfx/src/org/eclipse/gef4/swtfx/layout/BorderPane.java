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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.swtfx.INode;

public class BorderPane extends Pane {

	private INode top;
	private INode left;
	private INode bottom;
	private INode right;
	private INode center;
	private Map<INode, BorderPaneConstraints> constraints = new HashMap<INode, BorderPaneConstraints>();

	public BorderPane() {
	}

	@Override
	public void addChildNodes(INode... nodes) {
		throw new UnsupportedOperationException(
				"You cannot add arbitrary children to a BorderPane.");
	}

	public INode getBottom() {
		return bottom;
	}

	public INode getCenter() {
		return center;
	}

	@Override
	public List<INode> getChildNodes() {
		return Collections.unmodifiableList(super.getChildNodes());
	}

	public INode getLeft() {
		return left;
	}

	public INode getRight() {
		return right;
	}

	public INode getTop() {
		return top;
	}

	@Override
	public void layoutChildren() {
		double availableWidth = getWidth();
		double availableHeight = getHeight();

		// TODO: constraints

		double y = 0;

		if (top != null) {
			BorderPaneConstraints c = constraints.get(top);

			top.relocate(0, 0);

			y = top.computePrefHeight(availableWidth);
			if (top.isResizable()) {
				top.resize(availableWidth, y);
			}
		}

		double hBottom = bottom == null ? 0 : bottom
				.computePrefHeight(availableWidth);

		double x = 0;
		double h = 0;

		if (left != null) {
			BorderPaneConstraints c = constraints.get(left);

			left.relocate(0, y);

			h = availableHeight - hBottom - y;
			x = left.computePrefWidth(h);
			if (left.isResizable()) {
				left.resize(x, h);
			}
		}

		if (bottom != null) {
			BorderPaneConstraints c = constraints.get(bottom);

			bottom.relocate(0, y + h);

			if (bottom.isResizable()) {
				bottom.resize(availableWidth, hBottom);
			}
		}

		double w = 0;

		if (right != null) {
			BorderPaneConstraints c = constraints.get(right);

			w = right.computePrefWidth(h);

			right.relocate(availableWidth - w, y);

			if (right.isResizable()) {
				right.resize(w, h);
			}
		}

		if (center != null) {
			BorderPaneConstraints c = constraints.get(center);

			center.relocate(x, y);

			if (center.isResizable()) {
				center.resize(availableWidth - x - w, h);
			}
		}
	}

	public void setBottom(INode bottomNode, BorderPaneConstraints constraints) {
		if (bottom != null) {
			super.getChildNodes().remove(bottom);
		}
		super.addChildNodes(bottomNode);
		bottom = bottomNode;
		this.constraints.put(bottom, constraints);
	}

	public void setCenter(INode centerNode, BorderPaneConstraints constraints) {
		if (center != null) {
			super.getChildNodes().remove(center);
		}
		super.addChildNodes(centerNode);
		center = centerNode;
		this.constraints.put(center, constraints);
	}

	public void setLeft(INode leftNode, BorderPaneConstraints constraints) {
		if (left != null) {
			super.getChildNodes().remove(left);
		}
		super.addChildNodes(leftNode);
		left = leftNode;
		this.constraints.put(left, constraints);
	}

	public void setRight(INode rightNode, BorderPaneConstraints constraints) {
		if (right != null) {
			super.getChildNodes().remove(right);
		}
		super.addChildNodes(rightNode);
		right = rightNode;
		this.constraints.put(right, constraints);
	}

	public void setTop(INode topNode, BorderPaneConstraints constraints) {
		if (top != null) {
			super.getChildNodes().remove(top);
		}
		super.addChildNodes(topNode);
		top = topNode;
		this.constraints.put(top, constraints);
	}

}

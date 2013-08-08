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

import org.eclipse.gef4.swtfx.INode;

public class AnchorPane extends Pane {

	private Map<INode, AnchorPaneConstraints> constraints = new HashMap<INode, AnchorPaneConstraints>();

	public AnchorPane() {
	}

	public void add(INode node, AnchorPaneConstraints constraints) {
		addChildNodes(node);
		this.constraints.put(node, constraints);
	}

	private AnchorPaneConstraints constraints(INode child) {
		if (!constraints.containsKey(child)) {
			constraints.put(child, new AnchorPaneConstraints());
		}
		return constraints.get(child);
	}

	@Override
	public void layoutChildren() {
		// double availableWidth = getWidth();
		// double availableHeight = getHeight();

		// TODO: logger
		// System.out.println("area: " + availableWidth + " x " +
		// availableHeight);

		for (INode child : getManagedChildren()) {
			AnchorPaneConstraints c = constraints(child);
			Double left = c.getLeft();
			Double bottom = c.getBottom();
			Double right = c.getRight();
			Double top = c.getTop();

			double w = child.computePrefWidth(-1);
			double h = child.computePrefHeight(-1);

			double x = left == null ? right == null ? 0 : getWidth() - right
					- w : left;
			double y = top == null ? bottom == null ? 0 : getHeight() - bottom
					- h : top;
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

			// System.out.println("anchor " + child + " at " + x + ", " + y
			// + " sized " + w + " x " + h);

			child.relocate(x, y);
			if (child.isResizable()) {
				child.resize(x2 - x, y2 - y);
			}
		}

		// TODO: minimum/maximum resizing
	}
}

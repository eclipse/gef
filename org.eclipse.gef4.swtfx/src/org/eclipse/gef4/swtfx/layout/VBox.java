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

public class VBox extends Pane {

	private Map<INode, Boolean> fill = new HashMap<INode, Boolean>();
	private INode grower = null;

	public VBox() {
	}

	public void add(INode node, boolean fill) {
		addChildren(node);
		this.fill.put(node, fill);
	}

	private double getDeltaHeight(INode[] managed, double height) {
		double totalHeight = 0;
		for (INode n : managed) {
			totalHeight += n.getLayoutBounds().getHeight();
		}
		return height - totalHeight;
	}

	public Boolean getFill(INode node) {
		if (fill.containsKey(node)) {
			return fill.get(node);
		}
		return false;
	}

	public INode getGrower() {
		return grower;
	}

	@Override
	public void layoutChildren() {
		INode[] managed = getManagedChildren();
		if (managed == null || managed.length == 0) {
			return;
		}

		// auto-size to determine pref-bounds
		super.layoutChildren();

		// get available space
		double width = getWidth();
		double height = getHeight();

		// compute delta space
		double deltaHeight = getDeltaHeight(managed, height);

		// compute delta space per child
		double y = 0;
		double perChild = deltaHeight / managed.length;

		for (INode n : managed) {
			double w = n.getLayoutBounds().getWidth();
			double h = n.getLayoutBounds().getHeight();

			n.relocate(0, y);

			if (n.isResizable()) {
				double newWidth = w > width ? width : w;
				double newHeight = h + perChild;

				if (grower != null) {
					newHeight = h;
					if (grower == n) {
						newHeight += deltaHeight;
					}
				}

				if (fill.containsKey(n)) {
					if (fill.get(n)) {
						newWidth = width;
					}
				}

				n.resize(newWidth, newHeight);
				y += newHeight;
			} else {
				y += h;
			}
		}
	}

	public void setFill(INode node, Boolean fill) {
		this.fill.put(node, fill);
	}

	public void setGrower(INode node) {
		grower = node;
	}

	@Override
	public String toString() {
		return "VBox " + System.identityHashCode(this) + " { children-count: "
				+ getChildrenUnmodifiable().size() + " }";
	}

}

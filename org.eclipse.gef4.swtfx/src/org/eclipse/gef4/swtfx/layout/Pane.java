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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.AbstractParent;
import org.eclipse.gef4.swtfx.INode;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.RgbaColor;

public class Pane extends AbstractParent {

	private boolean debugging = false;

	@Override
	public double computePrefHeight(double width) {
		double prefHeight = getPrefHeight();
		if (prefHeight == INode.USE_COMPUTED_SIZE) {
			return super.computePrefHeight(width);
		}
		return prefHeight;
	}

	@Override
	public double computePrefWidth(double height) {
		double prefWidth = getPrefWidth();
		if (prefWidth == INode.USE_COMPUTED_SIZE) {
			return super.computePrefWidth(height);
		}
		return prefWidth;
	}

	@Override
	public Rectangle getLayoutBounds() {
		return new Rectangle(0, 0, getWidth(), getHeight());
	}

	public INode[] getManagedChildren() {
		List<INode> managed = new ArrayList<INode>();
		for (INode child : getChildrenUnmodifiable()) {
			if (child.isManaged()) {
				managed.add(child);
			}
		}
		return managed.toArray(new INode[0]);
	}

	@Override
	public void renderFigures(GraphicsContext g) {
		super.renderFigures(g);
		if (debugging) {
			renderLayoutDebug(g);
		}
	}

	protected void renderLayoutDebug(GraphicsContext g) {
		g.save();

		AffineTransform tx = getLocalToAbsoluteTransform();
		org.eclipse.swt.graphics.Point location = getScene().toDisplay(0, 0);
		tx.preConcatenate(new AffineTransform().translate(-location.x,
				-location.y));
		g.setTransform(tx);

		g.setLineWidth(1);

		for (INode node : getChildrenUnmodifiable()) {
			if (node instanceof IParent) {
				g.setStroke(new RgbaColor(255, 0, 0));
				g.strokePath(node.getBoundsInParent().toPath());
			} else {
				g.setStroke(new RgbaColor(0, 255, 0));
				g.strokePath(node.getBoundsInParent().toPath());
			}
		}

		g.setStroke(new RgbaColor(0, 0, 255));
		g.strokePath(getLayoutBounds().toPath());

		g.restore();
	}

	@Override
	public String toString() {
		return "Pane @ " + System.identityHashCode(this)
				+ " (children-count => " + getChildrenUnmodifiable().size() + ")";
	}
}

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
package org.eclipse.gef4.mvc.fx.parts;

import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

/**
 * @author mwienand
 * @author anyssen
 * 
 */
public class FXBoxHandlePart extends AbstractFXHandlePart implements
		Comparable<FXBoxHandlePart> {

	private Rectangle visual = null;
	private final Pos pos;

	public FXBoxHandlePart(Pos pos) {
		this.pos = pos;
		visual = new Rectangle();
		visual.setFill(Color.web("#d5faff"));
		visual.setStroke(Color.web("#5a61af"));
		visual.setWidth(5);
		visual.setHeight(5);
		visual.setStrokeWidth(1);
		visual.setStrokeType(StrokeType.OUTSIDE);
	}

	@Override
	public int compareTo(FXBoxHandlePart o) {
		// if we are bound to the same anchorages, we may compare positions,
		// otherwise we are not comparable
		if (!getAnchorages().equals(o.getAnchorages())) {
			throw new IllegalArgumentException(
					"Can only compare FXBoxHandles that are bound to the same anchorages.");
		}
		return pos.compareTo(o.pos);
	}

	@Override
	public void doRefreshVisual() {
		Bounds unionedBoundsInScene = FXPartUtils
				.getUnionedVisualBoundsInScene(getAnchorages().keySet());
		if (unionedBoundsInScene != null) {
			Bounds layoutBounds = visual.getParent().sceneToLocal(
					unionedBoundsInScene);
			double xInset = getXInset();
			double yInset = getYInset();
			if (Pos.TOP_LEFT == getPos()) {
				visual.setLayoutX(layoutBounds.getMinX() - xInset);
				visual.setLayoutY(layoutBounds.getMinY() - yInset);
			} else if (Pos.TOP_RIGHT == getPos()) {
				visual.setLayoutX(layoutBounds.getMaxX() - xInset);
				visual.setLayoutY(layoutBounds.getMinY() - yInset);
			} else if (Pos.BOTTOM_RIGHT == getPos()) {
				visual.setLayoutX(layoutBounds.getMaxX() - xInset);
				visual.setLayoutY(layoutBounds.getMaxY() - yInset);
			} else if (Pos.BOTTOM_LEFT == getPos()) {
				visual.setLayoutX(layoutBounds.getMinX() - xInset);
				visual.setLayoutY(layoutBounds.getMaxY() - yInset);
			} else {
				throw new IllegalArgumentException(
						"Unsupported position constant.");
			}
		}
	}

	public Pos getPos() {
		return pos;
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	protected double getXInset() {
		double xInset = visual.getWidth() / 2.0;
		return xInset;
	}

	protected double getYInset() {
		double yInset = visual.getHeight() / 2.0;
		return yInset;
	}

}

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
package org.eclipse.gef4.mvc.fx.parts;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.geometry.planar.IGeometry;

/**
 * A handle part used for showing feedback based on layout bounds of an
 * underlying target part
 * 
 * @author nyssen
 * 
 */
public class FXBoundsFeedbackPart extends AbstractFXHandlePart {

	private static final Color INVISIBLE = new Color(0, 0, 0, 0);

	private FXGeometryNode<? extends IGeometry> feedbackVisual;
//	private Rectangle feedbackVisual;
	private Node targetVisual;

	public FXBoundsFeedbackPart(Node targetVisual, FXGeometryNode<? extends IGeometry> feedbackVisual, Effect effect) {
		this.targetVisual = targetVisual;
		this.feedbackVisual = feedbackVisual;
//		this.feedbackVisual = new Rectangle();
		this.feedbackVisual.setStroke(Color.web("#5a61af"));
		this.feedbackVisual.setStrokeWidth(1);
		this.feedbackVisual.setFill(INVISIBLE);
		this.feedbackVisual.setEffect(effect);
		this.feedbackVisual.setMouseTransparent(true);
		this.feedbackVisual.setManaged(false);
	}

	@Override
	public Node getVisual() {
		return feedbackVisual;
	}

	@Override
	public void refreshVisual() {
		Bounds feedbackVisualBounds = feedbackVisual.getParent().sceneToLocal(
				targetVisual.localToScene(targetVisual.getLayoutBounds()));

		double x = feedbackVisualBounds.getMinX();
		double y = feedbackVisualBounds.getMinY();
		double width = feedbackVisualBounds.getWidth();
		double height = feedbackVisualBounds.getHeight();

		feedbackVisual.setLayoutX(x);
		feedbackVisual.setLayoutY(y);
		feedbackVisual.resize(width, height);
//		feedbackVisual.setWidth(width);
//		feedbackVisual.setHeight(height);
	}
}
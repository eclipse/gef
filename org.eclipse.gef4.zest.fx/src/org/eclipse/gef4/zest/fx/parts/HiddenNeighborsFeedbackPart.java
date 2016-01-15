/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.parts;

import java.util.Set;

import org.eclipse.gef4.mvc.fx.parts.AbstractFXFeedbackPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.models.HidingModel;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * The {@link HiddenNeighborsFeedbackPart} is an {@link AbstractFXFeedbackPart}
 * that displays the number of hidden neighbors (see
 * {@link HidingModel#getHiddenNeighbors(org.eclipse.gef4.graph.Node)}) of its
 * first anchorage.
 *
 * @author mwienand
 *
 */
// TODO: only applicable for NodeContentPart (anchorage)
public class HiddenNeighborsFeedbackPart extends AbstractFXFeedbackPart<Group> {

	private Circle circle;
	private Text text;

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		super.attachToAnchorageVisual(anchorage, role);
		getVisual().visibleProperty().bind(anchorage.getVisual().visibleProperty());
	}

	// TODO: extract visual to its own type
	@Override
	protected Group createVisual() {
		Group visual = new Group();
		visual.setAutoSizeChildren(false);

		circle = new Circle(10);
		// TODO: move to CSS
		circle.setFill(Color.RED);
		circle.setStroke(Color.BLACK);

		text = new Text("0");

		visual.getChildren().addAll(circle, text);
		return visual;
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		super.detachFromAnchorageVisual(anchorage, role);
		getVisual().visibleProperty().unbind();
	}

	@Override
	protected void doRefreshVisual(Group visual) {
		Set<IVisualPart<Node, ? extends Node>> keySet = getAnchoragesUnmodifiable().keySet();
		if (keySet.isEmpty()) {
			return;
		}
		IVisualPart<Node, ? extends Node> anchorage = keySet.iterator().next();
		if (((NodeContentPart) anchorage).getContent() == null) {
			return;
		}

		// update position
		Bounds anchorageLayoutBoundsInLocal = getVisual()
				.sceneToLocal(anchorage.getVisual().localToScene(anchorage.getVisual().getLayoutBounds()));
		double x = anchorageLayoutBoundsInLocal.getMaxX();
		double y = anchorageLayoutBoundsInLocal.getMaxY();
		circle.setCenterX(x);
		circle.setCenterY(y);

		// update text
		HidingModel hidingModel = getViewer().getAdapter(HidingModel.class);
		int count = hidingModel.getHiddenNeighbors(((NodeContentPart) anchorage).getContent()).size();
		text.setText(Integer.toString(count));

		Bounds textLayoutBounds = text.getLayoutBounds();

		// update circle size
		double size = textLayoutBounds.getWidth();
		if (textLayoutBounds.getHeight() > size) {
			size = textLayoutBounds.getHeight();
		}
		circle.setRadius(size / 2);

		// update text position
		text.relocate(x - textLayoutBounds.getWidth() / 2, y - textLayoutBounds.getHeight() / 2);
	}

}

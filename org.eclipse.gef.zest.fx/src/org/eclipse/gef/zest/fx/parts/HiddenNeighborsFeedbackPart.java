/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx.parts;

import java.util.Set;

import org.eclipse.gef.mvc.fx.parts.AbstractFeedbackPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.zest.fx.models.HidingModel;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

/**
 * The {@link HiddenNeighborsFeedbackPart} is an {@link AbstractFeedbackPart}
 * that displays the number of hidden neighbors (see
 * {@link HidingModel#getHiddenNeighbors(org.eclipse.gef.graph.Node)}) of its
 * first anchorage.
 *
 * @author mwienand
 *
 */
// TODO: only applicable for NodePart (anchorage)
public class HiddenNeighborsFeedbackPart extends AbstractFeedbackPart<Group> {

	private Circle circle;
	private Text text;

	@Override
	protected void doAttachToAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
		super.doAttachToAnchorageVisual(anchorage, role);
		getVisual().visibleProperty().bind(anchorage.getVisual().visibleProperty());
	}

	// TODO: extract visual to its own type
	@Override
	protected Group doCreateVisual() {
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
	protected void doDetachFromAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
		super.doDetachFromAnchorageVisual(anchorage, role);
		getVisual().visibleProperty().unbind();
	}

	@Override
	protected void doRefreshVisual(Group visual) {
		Set<IVisualPart<? extends Node>> keySet = getAnchoragesUnmodifiable().keySet();
		if (keySet.isEmpty()) {
			return;
		}
		IVisualPart<? extends Node> anchorage = keySet.iterator().next();
		if (((NodePart) anchorage).getContent() == null) {
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
		int count = hidingModel.getHiddenNeighbors(((NodePart) anchorage).getContent()).size();
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

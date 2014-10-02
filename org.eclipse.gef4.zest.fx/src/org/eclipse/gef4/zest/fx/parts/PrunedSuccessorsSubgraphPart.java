/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import org.eclipse.gef4.mvc.fx.parts.AbstractFXFeedbackPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.models.SubgraphModel;

// TODO: only applicable for NodeContentPart (anchorage)
public class PrunedSuccessorsSubgraphPart extends AbstractFXFeedbackPart {

	// TODO: extract visual to its own type
	private Group visuals = new Group();
	private Circle circle = new Circle(10);
	private Text text = new Text("0");

	{
		visuals.setAutoSizeChildren(false);
		visuals.getChildren().addAll(circle, text);
		circle.setFill(Color.RED);
		circle.setStroke(Color.BLACK);
	}

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node> anchorage,
			String role) {
		super.attachToAnchorageVisual(anchorage, role);
		visuals.visibleProperty().bind(anchorage.getVisual().visibleProperty());
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node> anchorage,
			String role) {
		super.detachFromAnchorageVisual(anchorage, role);
		visuals.visibleProperty().unbind();
	}

	@Override
	protected void doRefreshVisual() {
		Set<IVisualPart<Node>> keySet = getAnchorages().keySet();
		if (keySet.isEmpty()) {
			return;
		}
		IVisualPart<Node> anchorage = keySet.iterator().next();
		Bounds anchorageLayoutBoundsInLocal = visuals.sceneToLocal(anchorage
				.getVisual().localToScene(
						anchorage.getVisual().getLayoutBounds()));

		double x = anchorageLayoutBoundsInLocal.getMaxX();
		double y = anchorageLayoutBoundsInLocal.getMaxY();

		circle.setCenterX(x);
		circle.setCenterY(y);

		SubgraphModel subgraphModel = getViewer().getDomain().getAdapter(
				SubgraphModel.class);
		Set<NodeContentPart> containedNodes = subgraphModel
				.getContainedNodes((NodeContentPart) anchorage);
		int count = containedNodes == null ? 0 : containedNodes.size();
		text.setText(Integer.toString(count));

		Bounds textLayoutBounds = text.getLayoutBounds();

		double size = textLayoutBounds.getWidth();
		if (textLayoutBounds.getHeight() > size) {
			size = textLayoutBounds.getHeight();
		}
		circle.setRadius(size / 2);

		text.relocate(x - textLayoutBounds.getWidth() / 2,
				y - textLayoutBounds.getHeight() / 2);
	}

	@Override
	public Node getVisual() {
		return visuals;
	}

}

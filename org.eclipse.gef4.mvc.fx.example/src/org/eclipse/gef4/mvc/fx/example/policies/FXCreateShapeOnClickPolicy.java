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
package org.eclipse.gef4.mvc.fx.example.policies;

import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricModel;
import org.eclipse.gef4.mvc.fx.example.model.FXGeometricShape;
import org.eclipse.gef4.mvc.fx.example.parts.FXGeometricModelPart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXClickPolicy;
import org.eclipse.gef4.mvc.fx.policies.FXResizeRelocatePolicy;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.CreationPolicy;

public class FXCreateShapeOnClickPolicy extends AbstractFXClickPolicy {

	@Override
	public void click(MouseEvent e) {
		// create shape on right click
		if (MouseButton.SECONDARY.equals(e.getButton())) {
			EventTarget target = e.getTarget();
			if (target instanceof Node) {
				Node targetNode = (Node) target;
				// check if the event is relevant for us
				if (getHost().getVisual().getScene() == targetNode.getScene()) {
					createShape(e.getSceneX(), e.getSceneY());
				}
			}
		}
	}

	private void createShape(double sceneX, double sceneY) {
		// take creation policy from root part
		CreationPolicy<Node> creationPolicy = getHost().getRoot()
				.<CreationPolicy<Node>> getAdapter(CreationPolicy.class);
		creationPolicy.init();

		// find model part
		IVisualPart<Node> modelPart = getHost().getRoot().getChildren().get(0);
		if (!(modelPart instanceof FXGeometricModelPart)) {
			throw new IllegalStateException("Cannot find FXGeometricModelPart.");
		}

		// create new shape
		FXGeometricShape content = new FXGeometricShape(
				FXGeometricModel.createHandleShapeGeometry(),
				new AffineTransform(1, 0, 0, 1, 12, 15), Color.WHITE,
				FXGeometricModel.GEF_SHADOW_EFFECT);
		creationPolicy.create(content, (FXGeometricModelPart) modelPart);
		IUndoableOperation createOperation = creationPolicy.commit();

		// execute on stack
		getHost().getRoot().getViewer().getDomain().execute(createOperation);

		// find content part
		IContentPart<Node> contentPart = getHost().getRoot().getViewer()
				.getContentPartMap().get(content);

		// get the visual location
		Node visual = contentPart.getVisual();
		double x = visual.getLayoutBounds().getMinX() + visual.getLayoutX();
		double y = visual.getLayoutBounds().getMinY() + visual.getLayoutY();
		Point2D locationInLocal = visual.sceneToLocal(sceneX, sceneY);

		FXResizeRelocatePolicy resizeRelocatePolicy = contentPart
				.getAdapter(FXResizeRelocatePolicy.class);
		resizeRelocatePolicy.init();
		resizeRelocatePolicy.performResizeRelocate(locationInLocal.getX() - x,
				locationInLocal.getY() - y, 0, 0);
		IUndoableOperation relocateOperation = resizeRelocatePolicy.commit();

		// execute on stack
		getHost().getRoot().getViewer().getDomain().execute(relocateOperation);
	}

}

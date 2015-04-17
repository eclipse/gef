/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.examples.logo.policies;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricModel;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricModelPart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricShapePart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXDragPolicy;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.CreationPolicy;

public class FXCreateCurveOnDragPolicy extends AbstractFXDragPolicy {

	private FXGeometricCurvePart curvePart;

	@Override
	public void drag(MouseEvent e, Dimension delta) {
		// move curve to pointer location
		curvePart.getVisual().setEndPoint(getLocation(e));
	}

	protected Point getLocation(MouseEvent e) {
		Point2D location = ((FXViewer) getHost().getRoot().getViewer())
				.getScrollPane().getContentGroup()
				.sceneToLocal(e.getSceneX(), e.getSceneY());
		return new Point(location.getX(), location.getY());
	}

	protected FXGeometricShapePart getShapePart() {
		return (FXGeometricShapePart) getHost().getParent().getAnchorages()
				.keySet().iterator().next();
	}

	@Override
	public void press(MouseEvent e) {
		if (curvePart != null) {
			throw new IllegalStateException(
					"Cannot create a curve while creating a curve!");
		}

		// create new curve
		FXGeometricCurve curve = new FXGeometricCurve(new Point[] {},
				FXGeometricModel.GEF_COLOR_GREEN,
				FXGeometricModel.GEF_STROKE_WIDTH,
				FXGeometricModel.GEF_DASH_PATTERN, null);
		curve.addSourceAnchorage(getShapePart().getContent());

		// create using CreationPolicy from root part
		CreationPolicy<Node> creationPolicy = getHost().getRoot()
				.<CreationPolicy<Node>> getAdapter(CreationPolicy.class);
		creationPolicy.init();

		// find model part
		IVisualPart<Node, ? extends Node> modelPart = getHost().getRoot()
				.getChildren().get(0);
		if (!(modelPart instanceof FXGeometricModelPart)) {
			throw new IllegalStateException("Cannot find FXGeometricModelPart.");
		}

		creationPolicy.create((FXGeometricModelPart) modelPart, curve);
		IUndoableOperation createOperation = creationPolicy.commit();

		// execute on stack
		getHost().getRoot().getViewer().getDomain().execute(createOperation);

		curvePart = (FXGeometricCurvePart) getHost().getRoot().getViewer()
				.getContentPartMap().get(curve);

		// move curve to pointer location
		curvePart.getVisual().setEndPoint(getLocation(e));
	}

	@Override
	public void release(MouseEvent e, Dimension delta) {
		// move curve to pointer location
		curvePart.getVisual().setEndPoint(getLocation(e));
		curvePart = null;
	}

}

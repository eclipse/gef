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

import java.util.Collections;

import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.examples.logo.model.FXGeometricModel;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricCurvePart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricModelPart;
import org.eclipse.gef4.mvc.examples.logo.parts.FXGeometricShapePart;
import org.eclipse.gef4.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXOnClickPolicy;
import org.eclipse.gef4.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.operations.ITransactionalOperation;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.CreationPolicy;

import com.google.common.collect.Multiset;

import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class FXCreateCurveOnClickPolicy extends AbstractFXOnClickPolicy {

	@Override
	public void click(MouseEvent e) {
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
			throw new IllegalStateException(
					"Cannot find FXGeometricModelPart.");
		}

		creationPolicy.create((FXGeometricModelPart) modelPart, curve);
		ITransactionalOperation createOperation = creationPolicy.commit();

		// execute on stack
		getHost().getRoot().getViewer().getDomain().execute(createOperation);

		FXGeometricCurvePart curvePart = (FXGeometricCurvePart) getHost()
				.getRoot().getViewer().getContentPartMap().get(curve);

		// move curve to pointer location
		curvePart.getVisual().setEndPoint(getLocation(e));

		// close current execution transaction and open it again for the drag
		// tool, so that a new transaction is started (the bend should be done
		// after the create, so that they are undone in reverse, i.e. first undo
		// bend, then undo create).
		FXClickDragTool dragTool = getHost().getRoot().getViewer().getDomain()
				.getAdapter(FXClickDragTool.class);
		getHost().getRoot().getViewer().getDomain()
				.closeExecutionTransaction(dragTool);
		getHost().getRoot().getViewer().getDomain()
				.openExecutionTransaction(dragTool);

		updateDragTargetToLastSegmentHandlePart(curvePart, e.getTarget());
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

	protected void updateDragTargetToLastSegmentHandlePart(
			FXGeometricCurvePart curvePart, EventTarget eventTarget) {
		// select curve part to generate segment handles
		getHost().getRoot().getViewer().getAdapter(SelectionModel.class)
				.deselectAll();
		getHost().getRoot().getViewer()
				.<SelectionModel<Node>> getAdapter(SelectionModel.class)
				.select(Collections.singletonList(curvePart));

		// find last segment handle part
		Multiset<IVisualPart<Node, ? extends Node>> anchoreds = curvePart
				.getAnchoreds();
		FXCircleSegmentHandlePart lastSegmentHandlePart = null;
		for (IVisualPart<Node, ? extends Node> anchored : anchoreds) {
			if (anchored instanceof FXCircleSegmentHandlePart) {
				FXCircleSegmentHandlePart circleSegmentHandlePart = (FXCircleSegmentHandlePart) anchored;
				if (circleSegmentHandlePart.getSegmentParameter() == 1.0) {
					lastSegmentHandlePart = circleSegmentHandlePart;
					break;
				}
			}
		}

		// override drag target with segment handle part
		getHost().getRoot().getViewer().getDomain()
				.getAdapter(FXClickDragTool.class)
				.overrideTargetForThisInteraction(eventTarget,
						lastSegmentHandlePart);
	}

}

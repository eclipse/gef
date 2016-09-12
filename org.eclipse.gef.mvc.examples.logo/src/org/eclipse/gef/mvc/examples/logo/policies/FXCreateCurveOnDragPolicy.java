/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.policies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.examples.logo.model.FXGeometricCurve;
import org.eclipse.gef.mvc.examples.logo.model.FXGeometricModel;
import org.eclipse.gef.mvc.examples.logo.parts.FXGeometricCurvePart;
import org.eclipse.gef.mvc.examples.logo.parts.FXGeometricModelPart;
import org.eclipse.gef.mvc.examples.logo.parts.FXGeometricShapePart;
import org.eclipse.gef.mvc.fx.parts.FXCircleSegmentHandlePart;
import org.eclipse.gef.mvc.fx.policies.AbstractFXInteractionPolicy;
import org.eclipse.gef.mvc.fx.policies.IFXOnDragPolicy;
import org.eclipse.gef.mvc.fx.tools.FXClickDragTool;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.models.SelectionModel;
import org.eclipse.gef.mvc.operations.DeselectOperation;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IVisualPart;
import org.eclipse.gef.mvc.policies.CreationPolicy;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multiset;
import com.google.common.reflect.TypeToken;

import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class FXCreateCurveOnDragPolicy extends AbstractFXInteractionPolicy implements IFXOnDragPolicy {

	private FXCircleSegmentHandlePart bendTargetPart;
	private Map<AdapterKey<? extends IFXOnDragPolicy>, IFXOnDragPolicy> dragPolicies;
	private FXGeometricCurvePart curvePart;

	@Override
	public void drag(MouseEvent event, Dimension delta) {
		if (bendTargetPart == null) {
			return;
		}

		// forward drag events to bend target part
		if (dragPolicies != null) {
			for (IFXOnDragPolicy dragPolicy : dragPolicies.values()) {
				dragPolicy.drag(event, delta);
			}
		}
	}

	@Override
	public void abortDrag() {
		if (bendTargetPart == null) {
			return;
		}

		// forward event to bend target part
		if (dragPolicies != null) {
			for (IFXOnDragPolicy dragPolicy : dragPolicies.values()) {
				dragPolicy.abortDrag();
			}
		}

		restoreRefreshVisuals(curvePart);
		curvePart = null;
		bendTargetPart = null;
		dragPolicies = null;
	}

	protected FXCircleSegmentHandlePart findBendTargetPart(FXGeometricCurvePart curvePart, EventTarget eventTarget) {
		// find last segment handle part
		Multiset<IVisualPart<Node, ? extends Node>> anchoreds = curvePart.getAnchoredsUnmodifiable();
		for (IVisualPart<Node, ? extends Node> anchored : anchoreds) {
			if (anchored instanceof FXCircleSegmentHandlePart) {
				FXCircleSegmentHandlePart circleSegmentHandlePart = (FXCircleSegmentHandlePart) anchored;
				if (circleSegmentHandlePart.getSegmentParameter() == 1.0) {
					return circleSegmentHandlePart;
				}
			}
		}

		throw new IllegalStateException("Cannot find bend target part.");
	}

	protected Point getLocation(MouseEvent e) {
		Point2D location = ((FXViewer) getHost().getRoot().getViewer()).getCanvas().getContentGroup()
				.sceneToLocal(e.getSceneX(), e.getSceneY());
		return new Point(location.getX(), location.getY());
	}

	protected FXGeometricShapePart getShapePart() {
		return (FXGeometricShapePart) getHost().getAnchoragesUnmodifiable().keySet().iterator().next();
	}

	@Override
	public void hideIndicationCursor() {
	}

	@SuppressWarnings("serial")
	@Override
	public void startDrag(MouseEvent event) {
		// find model part
		IVisualPart<Node, ? extends Node> modelPart = getHost().getRoot().getChildrenUnmodifiable().get(0);
		if (!(modelPart instanceof FXGeometricModelPart)) {
			throw new IllegalStateException("Cannot find FXGeometricModelPart.");
		}

		// create new curve
		FXGeometricCurve curve = new FXGeometricCurve(new Point[] { new Point(), new Point() },
				FXGeometricModel.GEF_COLOR_GREEN, FXGeometricModel.GEF_STROKE_WIDTH, FXGeometricModel.GEF_DASH_PATTERN,
				null);
		curve.addSourceAnchorage(getShapePart().getContent());

		// create using CreationPolicy from root part
		CreationPolicy<Node> creationPolicy = getHost().getRoot().getAdapter(new TypeToken<CreationPolicy<Node>>() {
		});
		init(creationPolicy);
		curvePart = (FXGeometricCurvePart) creationPolicy.create(curve, (FXGeometricModelPart) modelPart,
				HashMultimap.<IContentPart<Node, ? extends Node>, String> create());
		commit(creationPolicy);

		// disable refresh visuals for the curvePart
		storeAndDisableRefreshVisuals(curvePart);

		// move curve to pointer location
		curvePart.getVisual().setEndPoint(getLocation(event));

		// build operation to deselect all but the new curve part
		List<IContentPart<Node, ? extends Node>> toBeDeselected = new ArrayList<>(
				getHost().getRoot().getViewer().getAdapter(new TypeToken<SelectionModel<Node>>() {
				}).getSelectionUnmodifiable());
		toBeDeselected.remove(curvePart);
		DeselectOperation<Node> deselectOperation = new DeselectOperation<>(getHost().getRoot().getViewer(),
				toBeDeselected);

		// execute on stack
		try {
			getHost().getRoot().getViewer().getDomain().execute(deselectOperation, new NullProgressMonitor());
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}

		// find bend target part
		bendTargetPart = findBendTargetPart(curvePart, event.getTarget());
		if (bendTargetPart != null) {
			dragPolicies = bendTargetPart.getAdapters(FXClickDragTool.ON_DRAG_POLICY_KEY);
		}
		if (dragPolicies != null) {
			for (IFXOnDragPolicy dragPolicy : dragPolicies.values()) {
				dragPolicy.startDrag(event);
			}
		}
	}

	@Override
	public void endDrag(MouseEvent e, Dimension delta) {
		if (bendTargetPart == null) {
			return;
		}

		// forward event to bend target part
		if (dragPolicies != null) {
			for (IFXOnDragPolicy dragPolicy : dragPolicies.values()) {
				dragPolicy.endDrag(e, delta);
			}
		}

		restoreRefreshVisuals(curvePart);
		curvePart = null;
		bendTargetPart = null;
		dragPolicies = null;
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		return false;
	}

}

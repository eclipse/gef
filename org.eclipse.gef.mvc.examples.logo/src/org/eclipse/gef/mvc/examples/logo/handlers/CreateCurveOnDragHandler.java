/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.examples.logo.MvcLogoExample;
import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve;
import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve.InterpolationStyle;
import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve.RoutingStyle;
import org.eclipse.gef.mvc.examples.logo.parts.GeometricCurvePart;
import org.eclipse.gef.mvc.examples.logo.parts.GeometricShapePart;
import org.eclipse.gef.mvc.fx.gestures.ClickDragGesture;
import org.eclipse.gef.mvc.fx.handlers.AbstractHandler;
import org.eclipse.gef.mvc.fx.handlers.IOnDragHandler;
import org.eclipse.gef.mvc.fx.models.SelectionModel;
import org.eclipse.gef.mvc.fx.operations.DeselectOperation;
import org.eclipse.gef.mvc.fx.parts.CircleSegmentHandlePart;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.policies.CreationPolicy;
import org.eclipse.gef.mvc.fx.viewer.InfiniteCanvasViewer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multiset;

import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class CreateCurveOnDragHandler extends AbstractHandler implements IOnDragHandler {

	private CircleSegmentHandlePart bendTargetPart;
	private Map<AdapterKey<? extends IOnDragHandler>, IOnDragHandler> dragPolicies;
	private GeometricCurvePart<? extends Node, ? extends Node, ? extends Node> curvePart;

	@Override
	public void abortDrag() {
		if (bendTargetPart == null) {
			return;
		}

		// forward event to bend target part
		if (dragPolicies != null) {
			for (IOnDragHandler dragPolicy : dragPolicies.values()) {
				dragPolicy.abortDrag();
			}
		}

		restoreRefreshVisuals(curvePart);
		curvePart = null;
		bendTargetPart = null;
		dragPolicies = null;
	}

	@Override
	public void drag(MouseEvent event, Dimension delta) {
		if (bendTargetPart == null) {
			return;
		}

		// forward drag events to bend target part
		if (dragPolicies != null) {
			for (IOnDragHandler dragPolicy : dragPolicies.values()) {
				dragPolicy.drag(event, delta);
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
			for (IOnDragHandler dragPolicy : dragPolicies.values()) {
				dragPolicy.endDrag(e, delta);
			}
		}

		restoreRefreshVisuals(curvePart);
		curvePart = null;
		bendTargetPart = null;
		dragPolicies = null;
	}

	protected CircleSegmentHandlePart findBendTargetPart(GeometricCurvePart<?, ?, ?> curvePart,
			EventTarget eventTarget) {
		// find last segment handle part
		Multiset<IVisualPart<? extends Node>> anchoreds = curvePart.getAnchoredsUnmodifiable();
		for (IVisualPart<? extends Node> anchored : anchoreds) {
			if (anchored instanceof CircleSegmentHandlePart) {
				CircleSegmentHandlePart circleSegmentHandlePart = (CircleSegmentHandlePart) anchored;
				if (circleSegmentHandlePart.getSegmentParameter() == 1.0) {
					return circleSegmentHandlePart;
				}
			}
		}
		throw new IllegalStateException("Cannot find bend target part.");
	}

	protected Point getLocation(MouseEvent e) {
		// XXX: Viewer may be null if the host is removed in the same pass in
		// which the event is forwarded.
		if (getHost().getViewer() == null) {
			return new Point(e.getSceneX(), e.getSceneY());
		}
		// FIXME: Prevent invocation of interaction policies when their host
		// does not have a link to the viewer.
		Point2D location = ((InfiniteCanvasViewer) getHost().getRoot().getViewer()).getCanvas().getContentGroup()
				.sceneToLocal(e.getSceneX(), e.getSceneY());
		return new Point(location.getX(), location.getY());
	}

	protected GeometricShapePart getShapePart() {
		return (GeometricShapePart) getHost().getAnchoragesUnmodifiable().keySet().iterator().next();
	}

	@Override
	public void hideIndicationCursor() {
	}

	@Override
	public boolean showIndicationCursor(KeyEvent event) {
		return false;
	}

	@Override
	public boolean showIndicationCursor(MouseEvent event) {
		return false;
	}

	@SuppressWarnings("restriction")
	@Override
	public void startDrag(MouseEvent event) {
		// create new curve
		GeometricCurve curve = new GeometricCurve(new Point[] { new Point(), new Point() },
				MvcLogoExample.GEF_COLOR_GREEN, MvcLogoExample.GEF_STROKE_WIDTH, MvcLogoExample.GEF_DASH_PATTERN, null,
				RoutingStyle.STRAIGHT, InterpolationStyle.POLYBEZIER);
		curve.addSourceAnchorage(getShapePart().getContent());

		// create using CreationPolicy from root part
		CreationPolicy creationPolicy = getHost().getRoot().getAdapter(CreationPolicy.class);
		init(creationPolicy);
		curvePart = (GeometricCurvePart<?, ?, ?>) creationPolicy.create(curve, getHost().getRoot(),
				HashMultimap.<IContentPart<? extends Node>, String> create());
		commit(creationPolicy);

		// disable refresh visuals for the curvePart
		storeAndDisableRefreshVisuals(curvePart);

		// move curve to pointer location
		curvePart.getVisual().setEndPoint(getLocation(event));

		// build operation to deselect all but the new curve part
		List<IContentPart<? extends Node>> toBeDeselected = new ArrayList<>(
				getHost().getRoot().getViewer().getAdapter(SelectionModel.class).getSelectionUnmodifiable());
		toBeDeselected.remove(curvePart);
		DeselectOperation deselectOperation = new DeselectOperation(getHost().getRoot().getViewer(), toBeDeselected);
		// execute on stack
		try {
			getHost().getRoot().getViewer().getDomain().execute(deselectOperation, new NullProgressMonitor());
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}

		// find bend target part
		bendTargetPart = findBendTargetPart(curvePart, event.getTarget());
		if (bendTargetPart != null) {
			dragPolicies = bendTargetPart.getAdapters(ClickDragGesture.ON_DRAG_POLICY_KEY);
		}
		if (dragPolicies != null) {
			MouseEvent dragEvent = new MouseEvent(event.getSource(), event.getTarget(), MouseEvent.MOUSE_DRAGGED,
					event.getX(), event.getY(), event.getScreenX(), event.getScreenY(), event.getButton(),
					event.getClickCount(), event.isShiftDown(), event.isControlDown(), event.isAltDown(),
					event.isMetaDown(), event.isPrimaryButtonDown(), event.isMiddleButtonDown(),
					event.isSecondaryButtonDown(), event.isSynthesized(), event.isPopupTrigger(),
					event.isStillSincePress(), event.getPickResult());
			for (IOnDragHandler dragPolicy : dragPolicies.values()) {
				dragPolicy.startDrag(event);
				// XXX: send initial drag event so that the end position is set
				dragPolicy.drag(dragEvent, new Dimension());
			}
		}
	}
}

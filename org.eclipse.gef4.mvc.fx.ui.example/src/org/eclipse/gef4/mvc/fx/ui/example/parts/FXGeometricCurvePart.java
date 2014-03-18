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
package org.eclipse.gef4.mvc.fx.ui.example.parts;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;

import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXCurveConnection;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.ui.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.ui.example.policies.FXExampleReconnectionPolicy;
import org.eclipse.gef4.mvc.fx.ui.example.policies.FXExampleWayPointPolicy;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IHoverPolicy;
import org.eclipse.gef4.mvc.policies.ISelectionPolicy;

public class FXGeometricCurvePart extends AbstractFXGeometricElementPart {

	private final FXSelectionBehavior selectionBehavior = new FXSelectionBehavior() {
		@Override
		public IGeometry getFeedbackGeometry() {
			return visual.getGeometry();
		}
	};

	private FXCurveConnection visual;

	private boolean doRefreshVisual = true;

	private boolean setStartAnchor = true;

	public void setReplaceStartAnchor(boolean isReplaceStartAnchor) {
		setStartAnchor = isReplaceStartAnchor;
	}

	public boolean isReplaceStartAnchor() {
		return setStartAnchor;
	}

	public void setModelRefresh(boolean isModelRefresh) {
		doRefreshVisual = isModelRefresh;
	}

	public boolean isModelRefresh() {
		return doRefreshVisual;
	}

	public FXGeometricCurvePart() {
		visual = new FXCurveConnection() {
			@Override
			public ICurve computeCurveGeometry() {
				return FXGeometricCurve
						.constructCurveFromWayPoints(getPoints());
			}
		};

		installBound(selectionBehavior);
		installBound(ISelectionPolicy.class, new ISelectionPolicy.Impl<Node>());
		installBound(IHoverPolicy.class, new IHoverPolicy.Impl<Node>() {
			@Override
			public boolean isHoverable() {
				return !getHost().getRoot().getViewer().getSelectionModel()
						.getSelected().contains(getHost());
			}
		});
		installBound(new FXExampleWayPointPolicy(this));
		installBound(new FXExampleReconnectionPolicy(
				this));
	}

	@Override
	public FXGeometricCurve getContent() {
		return (FXGeometricCurve) super.getContent();
	}

	@Override
	public void setContent(Object model) {
		if (!(model instanceof FXGeometricCurve)) {
			throw new IllegalArgumentException(
					"Only ICurve models are supported.");
		}
		super.setContent(model);
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	@Override
	public void refreshVisual() {
		if (!doRefreshVisual) {
			return;
		}

		// TODO: compare way points to identify if we need to refresh
		FXGeometricCurve curve = getContent();
		List<Point> wayPoints = curve.getWayPoints();
		if (curve.getTransform() != null) {
			Point[] transformedWayPoints = curve.getTransform().getTransformed(
					wayPoints.toArray(new Point[] {}));
			visual.setWayPoints(transformedWayPoints);
		} else {
			visual.setWayPoints(wayPoints);
		}

		// apply stroke paint
		if (visual.getStroke() != curve.getStroke()) {
			visual.setStroke(curve.getStroke());
		}

		// stroke width
		if (visual.getStrokeWidth() != curve.getStrokeWidth()) {
			visual.setStrokeWidth(curve.getStrokeWidth());
		}

		// dashes
		List<Double> dashList = new ArrayList<Double>(curve.dashes.length);
		for (double d : curve.dashes) {
			dashList.add(d);
		}
		if (!visual.getStrokeDashArray().equals(dashList)) {
			visual.getStrokeDashArray().setAll(dashList);
		}

		// apply effect
		super.refreshVisual();
	}

	@Override
	public void attachVisualToAnchorageVisual(
			final IVisualPart<Node> anchorage, Node anchorageVisual) {
		final IFXAnchor anchor = ((AbstractFXContentPart) anchorage)
				.getAnchor(this);
		if (setStartAnchor) {
			visual.setStartAnchor(anchor);
			setStartAnchor = false;
		} else {
			visual.setEndAnchor(anchor);
		}
	}

	@Override
	public void detachVisualFromAnchorageVisual(IVisualPart<Node> anchorage,
			Node anchorageVisual) {
		final IFXAnchor anchor = ((AbstractFXContentPart) anchorage)
				.getAnchor(this);
		if (anchor == visual.getStartAnchor()) {
			visual.loosenStartAnchor();
		} else if (anchor == visual.getEndAnchor()) {
			visual.loosenEndAnchor();
		} else {
			throw new IllegalStateException("Detach unknown anchor: " + anchor);
		}
	}

	public FXSelectionBehavior getSelectionBehavior() {
		return selectionBehavior;
	}

}

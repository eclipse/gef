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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javafx.scene.Node;

import org.eclipse.gef4.fx.nodes.FXCurveConnection;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXReconnectPolicy;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXWayPointPolicy;
import org.eclipse.gef4.mvc.fx.ui.example.model.FXGeometricCurve;
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

	public FXGeometricCurvePart() {
		visual = new FXCurveConnection() {
			@Override
			public ICurve computeGeometry() {
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
		installBound(AbstractFXWayPointPolicy.class, new AbstractFXWayPointPolicy() {
			@Override
			public IFXConnection getConnection() {
				return visual;
			}
			
			@Override
			public void commitWayPoint(int wayPointIndex, Point p) {
				super.commitWayPoint(wayPointIndex, p);
				selectionBehavior.refreshFeedback();
				selectionBehavior.refreshHandles();
				refreshVisual();
			}
		});
		installBound(AbstractFXReconnectPolicy.class, new AbstractFXReconnectPolicy() {
			@Override
			public IFXConnection getConnection() {
				return visual;
			}
		});
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
		if (!isRefreshVisual()) {
			return;
		}

		// TODO: compare way points to identify if we need to refresh
		FXGeometricCurve curve = getContent();
		List<Point> wayPoints = curve.getWayPoints();
		if (curve.getTransform() != null) {
			Point[] transformedWayPoints = curve.getTransform().getTransformed(
					wayPoints.toArray(new Point[] {}));
			visual.setWayPoints(Arrays.asList(transformedWayPoints));
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
			final IVisualPart<Node> anchorage, Node anchorageVisual,
			Map<Object, Object> contextMap) {
		visual.attachTo(((AbstractFXContentPart) anchorage).getAnchor(this),
				contextMap);
	}

	@Override
	public void detachVisualFromAnchorageVisual(IVisualPart<Node> anchorage,
			Node anchorageVisual) {
		visual.detachFrom(((AbstractFXContentPart) anchorage).getAnchor(this));
	}

	public FXSelectionBehavior getSelectionBehavior() {
		return selectionBehavior;
	}

}

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

import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXCurveConnection;
import org.eclipse.gef4.fx.nodes.IFXConnection;
import org.eclipse.gef4.fx.nodes.IFXDecoration;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXReconnectPolicy;
import org.eclipse.gef4.mvc.fx.policies.AbstractFXWayPointPolicy;
import org.eclipse.gef4.mvc.fx.ui.example.model.AbstractFXGeometricElement;
import org.eclipse.gef4.mvc.fx.ui.example.model.FXGeometricCurve;
import org.eclipse.gef4.mvc.fx.ui.example.model.FXGeometricModel.AnchorType;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.policies.IHoverPolicy;
import org.eclipse.gef4.mvc.policies.ISelectionPolicy;

public class FXGeometricCurvePart extends AbstractFXGeometricElementPart {

	public static class ArrowHead extends Polyline implements IFXDecoration {
		public ArrowHead() {
			super(15.0, 0.0, 10.0, 0.0, 10.0, 3.0, 0.0, 0.0, 10.0, -3.0,
					10.0, 0.0);
		}

		@Override
		public Point getLocalStartPoint() {
			return new Point(0, 0);
		}

		@Override
		public Point getLocalEndPoint() {
			return new Point(15, 0);
		}

		@Override
		public Node getVisual() {
			return this;
		}
	}
	
	public static class CircleHead extends Circle implements IFXDecoration {
		public CircleHead() {
			super(5);
		}

		@Override
		public Point getLocalStartPoint() {
			return new Point(0, 0);
		}

		@Override
		public Point getLocalEndPoint() {
			return new Point(0, 0);
		}

		@Override
		public Node getVisual() {
			return this;
		}
	}

	private final FXSelectionBehavior selectionBehavior = new FXSelectionBehavior() {
		@Override
		public IGeometry getFeedbackGeometry() {
			return visual.getCurveNode().getGeometry();
		}
	};

	private FXCurveConnection visual;

	public FXGeometricCurvePart() {
		visual = new FXCurveConnection() {
			{
				setStartDecoration(new ArrowHead());
				setEndDecoration(new CircleHead());
			}

			@Override
			public ICurve computeGeometry(Point[] points) {
				return FXGeometricCurve.constructCurveFromWayPoints(points);
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
		installBound(AbstractFXWayPointPolicy.class,
				new AbstractFXWayPointPolicy() {
					@Override
					public IFXConnection getConnection() {
						return visual;
					}

					@Override
					public IUndoableOperation commitWayPoint(int wayPointIndex,
							Point p) {
						IUndoableOperation op = super.commitWayPoint(
								wayPointIndex, p);
						setRefreshVisual(false);
						FXGeometricCurve curve = getContent();
						List<Point> curveWPs = curve.getWayPoints();
						for (int i = curveWPs.size() - 1; i >= 0; i--) {
							curve.removeWayPoint(i);
						}
						List<Point> wayPoints = visual.getWayPoints();
						for (int i = 0; i < wayPoints.size(); i++) {
							curve.addWayPoint(i, wayPoints.get(i));
						}
						selectionBehavior.refreshFeedback();
						selectionBehavior.refreshHandles();
						setRefreshVisual(true);
						refreshVisual();

						// TODO: remove way point juggling here, just execute
						// the operation

						return op;
					}
				});
		installBound(AbstractFXReconnectPolicy.class,
				new AbstractFXReconnectPolicy() {
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
		if (visual.getCurveNode().getStroke() != curve.getStroke()) {
			visual.getCurveNode().setStroke(curve.getStroke());
			IFXDecoration startDecoration = visual.getStartDecoration();
			if (startDecoration != null) {
				((Shape) startDecoration.getVisual()).setStroke(curve.getStroke());
			}
			IFXDecoration endDecoration = visual.getEndDecoration();
			if (endDecoration != null) {
				((Shape) endDecoration.getVisual()).setStroke(curve.getStroke());
			}
		}

		// stroke width
		if (visual.getCurveNode().getStrokeWidth() != curve.getStrokeWidth()) {
			visual.getCurveNode().setStrokeWidth(curve.getStrokeWidth());
			IFXDecoration startDecoration = visual.getStartDecoration();
			if (startDecoration != null) {
				((Shape) startDecoration.getVisual()).setStrokeWidth(curve.getStrokeWidth());
			}
			IFXDecoration endDecoration = visual.getEndDecoration();
			if (endDecoration != null) {
				((Shape) endDecoration.getVisual()).setStrokeWidth(curve.getStrokeWidth());
			}
		}

		// dashes
		List<Double> dashList = new ArrayList<Double>(curve.dashes.length);
		for (double d : curve.dashes) {
			dashList.add(d);
		}
		if (!visual.getCurveNode().getStrokeDashArray().equals(dashList)) {
			visual.getCurveNode().getStrokeDashArray().setAll(dashList);
		}

		// apply effect
		super.refreshVisual();
	}

	@Override
	public void attachVisualToAnchorageVisual(
			final IVisualPart<Node> anchorage, Node anchorageVisual) {
		// TODO: the context should not be stored in the model, but created here
		// based on the model
		AbstractFXGeometricElement<?> anchorageContent = ((AbstractFXGeometricElementPart) anchorage)
				.getContent();
		AnchorType type = anchorageContent.getAnchorType(getContent());
		attachTo(visual, ((AbstractFXContentPart) anchorage).getAnchor(this),
				type);
	}

	@Override
	public void detachVisualFromAnchorageVisual(IVisualPart<Node> anchorage,
			Node anchorageVisual) {
		detachFrom(visual, ((AbstractFXContentPart) anchorage).getAnchor(this));
	}

	private void attachTo(IFXConnection visual, IFXAnchor anchor,
			AnchorType anchorType) {
		if (anchorType != null) {
			switch (anchorType) {
			case START:
				visual.setStartAnchor(anchor);
				break;
			case END:
				visual.setEndAnchor(anchor);
				break;
			default:
				throw new IllegalArgumentException("Unsupported anchor type.");
			}
		} else {
			throw new IllegalStateException("no <type> specified");
		}
	}

	private void detachFrom(IFXConnection visual, IFXAnchor anchor) {
		if (anchor == visual.getStartAnchor()) {
			visual.setStartPoint(visual.getStartPoint());
		} else if (anchor == visual.getEndAnchor()) {
			visual.setEndPoint(visual.getEndPoint());
		} else {
			for (int i = 0; i < visual.getWayPointAnchors().size(); i++) {
				if (anchor == visual.getWayPointAnchors().get(i)) {
					visual.setWayPoint(i, visual.getWayPoint(i));
					return;
				}
			}
			throw new IllegalStateException(
					"Cannot detach from unknown anchor: " + anchor);
		}
		// TODO: what if multiple points are bound to the same anchor?
	}

	public FXSelectionBehavior getSelectionBehavior() {
		return selectionBehavior;
	}

}

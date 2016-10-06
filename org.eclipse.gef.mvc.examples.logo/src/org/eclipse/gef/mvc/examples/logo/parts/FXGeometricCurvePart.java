/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.common.collections.SetMultimapChangeListener;
import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.anchors.OrthogonalProjectionStrategy;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.nodes.PolyBezierInterpolator;
import org.eclipse.gef.fx.nodes.PolylineInterpolator;
import org.eclipse.gef.fx.nodes.StraightRouter;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.examples.logo.model.AbstractFXGeometricElement;
import org.eclipse.gef.mvc.examples.logo.model.FXGeometricCurve;
import org.eclipse.gef.mvc.examples.logo.model.FXGeometricCurve.Decoration;
import org.eclipse.gef.mvc.examples.logo.model.FXGeometricCurve.RoutingStyle;
import org.eclipse.gef.mvc.fx.parts.IFXBendableContentPart;
import org.eclipse.gef.mvc.fx.parts.IFXResizableVisualPart;
import org.eclipse.gef.mvc.fx.parts.IFXTransformableContentPart;
import org.eclipse.gef.mvc.fx.providers.IAnchorProvider;
import org.eclipse.gef.mvc.parts.IVisualPart;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;

public class FXGeometricCurvePart extends AbstractFXGeometricElementPart<Connection>
		implements IFXTransformableContentPart<Connection>, IFXBendableContentPart, IFXResizableVisualPart<Connection> {

	public static class ArrowHead extends Polygon {
		public ArrowHead() {
			super(0, 0, 10, 3, 10, -3);
			setFill(Color.TRANSPARENT);
		}
	}

	public static class CircleHead extends Circle {
		public CircleHead() {
			super(5);
			setFill(Color.TRANSPARENT);
		}
	}

	private static final String END_ROLE = "END";

	private static final String START_ROLE = "START";

	private final CircleHead START_CIRCLE_HEAD = new CircleHead();

	private final CircleHead END_CIRCLE_HEAD = new CircleHead();

	private final ArrowHead START_ARROW_HEAD = new ArrowHead();
	private final ArrowHead END_ARROW_HEAD = new ArrowHead();
	private FXGeometricCurve previousContent;

	// refresh visual upon model property changes
	private final ListChangeListener<Point> wayPointsChangeListener = new ListChangeListener<Point>() {
		@Override
		public void onChanged(javafx.collections.ListChangeListener.Change<? extends Point> c) {
			refreshVisual();
		}
	};
	private final ListChangeListener<Double> dashesChangeListener = new ListChangeListener<Double>() {
		@Override
		public void onChanged(javafx.collections.ListChangeListener.Change<? extends Double> c) {
			refreshVisual();
		}
	};
	private final ChangeListener<RoutingStyle> routingStyleChangeListener = new ChangeListener<RoutingStyle>() {
		@Override
		public void changed(ObservableValue<? extends RoutingStyle> observable, RoutingStyle oldValue,
				RoutingStyle newValue) {
			refreshVisual();
		}
	};
	private final ChangeListener<Decoration> decorationChangeListener = new ChangeListener<Decoration>() {
		@Override
		public void changed(ObservableValue<? extends Decoration> observable, Decoration oldValue,
				Decoration newValue) {
			refreshVisual();
		}
	};

	public FXGeometricCurvePart() {
		anchoragesUnmodifiableProperty()
				.addListener(new SetMultimapChangeListener<IVisualPart<Node, ? extends Node>, String>() {
					@Override
					public void onChanged(
							Change<? extends IVisualPart<Node, ? extends Node>, ? extends String> change) {
						// XXX: As the selection handles show the connected
						// state, their visuals need to be refreshed.
						// TODO: Rather register an anchorages-observer within
						// FXCircleSegmentHandlePart when attaching to an
						// anchorage.
						for (IVisualPart<Node, ? extends Node> anchored : getAnchoredsUnmodifiable()) {
							anchored.refreshVisual();
						}
					}
				});
	}

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		IAnchor anchor = anchorage.getAdapter(IAnchorProvider.class).get(this, role);
		if (role.equals(START_ROLE)) {
			getVisual().setStartAnchor(anchor);
			getContent().setWayPoint(0, getVisual().getStartPoint());
		} else if (role.equals(END_ROLE)) {
			getVisual().setEndAnchor(anchor);
			getContent().setWayPoint(getContent().getWayPoints().size() - 1, getVisual().getEndPoint());
		} else {
			throw new IllegalStateException("Cannot attach to anchor with role <" + role + ">.");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void bendContent(List<BendPoint> bendPoints) {
		getContent().getSourceAnchorages().clear();
		getContent().getTargetAnchorages().clear();
		List<Point> waypoints = new ArrayList<>();
		for (int i = 0; i < bendPoints.size(); i++) {
			BendPoint bp = bendPoints.get(i);
			if (bp.isAttached()) {
				if (i == 0) {
					// update start anchorage
					// TODO: introduce setter so this is more concise
					getContent().addSourceAnchorage(
							(AbstractFXGeometricElement<? extends IGeometry>) bp.getContentAnchorage());
					// update start hint
					waypoints.add(bp.getPosition());
				}
				if (i == bendPoints.size() - 1) {
					// update end anchorage
					// TODO: introduce setter so this is more concise
					getContent().addTargetAnchorage(
							(AbstractFXGeometricElement<? extends IGeometry>) bp.getContentAnchorage());
					// update end point hint
					waypoints.add(bp.getPosition());
				}
			} else {
				waypoints.add(bp.getPosition());
			}
		}
		refreshContentAnchorages();
		getContent().setWayPoints(waypoints.toArray(new Point[] {}));
	}

	@Override
	protected Connection createVisual() {
		Connection visual = new Connection();
		visual.setInterpolator(new PolyBezierInterpolator());
		((GeometryNode<?>) visual.getCurve()).setStrokeLineCap(StrokeLineCap.BUTT);
		return visual;
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		if (role.equals(START_ROLE)) {
			getVisual().setStartPoint(getVisual().getStartPoint());
			getContent().setWayPoint(0, getVisual().getStartPoint());
		} else if (role.equals(END_ROLE)) {
			getVisual().setEndPoint(getVisual().getEndPoint());
			getContent().setWayPoint(getContent().getWayPoints().size() - 1, getVisual().getEndPoint());
		} else {
			throw new IllegalStateException("Cannot detach from anchor with role <" + role + ">.");
		}
	}

	@Override
	protected void doAttachToContentAnchorage(Object contentAnchorage, String role) {
		if (!(contentAnchorage instanceof AbstractFXGeometricElement)) {
			throw new IllegalArgumentException("Inappropriate content anchorage: wrong type.");
		}
		AbstractFXGeometricElement<?> geom = (AbstractFXGeometricElement<?>) contentAnchorage;
		if (START_ROLE.equals(role)) {
			getContent().getSourceAnchorages().add(geom);
		} else if (END_ROLE.equals(role)) {
			getContent().getTargetAnchorages().add(geom);
		}
	}

	@Override
	protected void doDetachFromContentAnchorage(Object contentAnchorage, String role) {
		if (START_ROLE.equals(role)) {
			getContent().getSourceAnchorages().remove(contentAnchorage);
		} else if (END_ROLE.equals(role)) {
			getContent().getTargetAnchorages().remove(contentAnchorage);
		}
	}

	@Override
	protected SetMultimap<Object, String> doGetContentAnchorages() {
		SetMultimap<Object, String> anchorages = HashMultimap.create();

		Set<AbstractFXGeometricElement<? extends IGeometry>> sourceAnchorages = getContent().getSourceAnchorages();
		for (Object src : sourceAnchorages) {
			anchorages.put(src, START_ROLE);
		}
		Set<AbstractFXGeometricElement<? extends IGeometry>> targetAnchorages = getContent().getTargetAnchorages();
		for (Object dst : targetAnchorages) {
			anchorages.put(dst, END_ROLE);
		}
		return anchorages;
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		return Collections.emptyList();
	}

	@Override
	protected void doRefreshVisual(Connection visual) {
		FXGeometricCurve content = getContent();

		List<Point> wayPoints = content.getWayPointsCopy();

		// TODO: why is this needed??
		AffineTransform transform = content.getTransform();
		if (previousContent == null || (transform != null && !transform.equals(previousContent.getTransform())
				|| transform == null && previousContent.getTransform() != null)) {
			if (transform != null) {
				Point[] transformedWayPoints = transform.getTransformed(wayPoints.toArray(new Point[] {}));
				wayPoints = Arrays.asList(transformedWayPoints);
			}
		}

		if (!getContentAnchoragesUnmodifiable().containsValue(START_ROLE)) {
			visual.setStartPoint(wayPoints.remove(0));
		} else {
			visual.setStartPointHint(wayPoints.remove(0));
		}

		if (!getContentAnchoragesUnmodifiable().containsValue(END_ROLE)) {
			visual.setEndPoint(wayPoints.remove(wayPoints.size() - 1));
		} else {
			visual.setEndPointHint(wayPoints.remove(wayPoints.size() - 1));
		}

		if (!visual.getControlPoints().equals(wayPoints)) {
			visual.setControlPoints(wayPoints);
		}

		// decorations
		switch (content.getSourceDecoration()) {
		case NONE:
			if (visual.getStartDecoration() != null) {
				visual.setStartDecoration(null);
			}
			break;
		case CIRCLE:
			if (visual.getStartDecoration() == null || !(visual.getStartDecoration() instanceof CircleHead)) {
				visual.setStartDecoration(START_CIRCLE_HEAD);
			}
			break;
		case ARROW:
			if (visual.getStartDecoration() == null || !(visual.getStartDecoration() instanceof ArrowHead)) {
				visual.setStartDecoration(START_ARROW_HEAD);
			}
			break;
		}
		switch (content.getTargetDecoration()) {
		case NONE:
			if (visual.getEndDecoration() != null) {
				visual.setEndDecoration(null);
			}
			break;
		case CIRCLE:
			if (visual.getEndDecoration() == null || !(visual.getEndDecoration() instanceof CircleHead)) {
				visual.setEndDecoration(END_CIRCLE_HEAD);
			}
			break;
		case ARROW:
			if (visual.getEndDecoration() == null || !(visual.getEndDecoration() instanceof ArrowHead)) {
				visual.setEndDecoration(END_ARROW_HEAD);
			}
			break;
		}

		Shape startDecorationVisual = (Shape) visual.getStartDecoration();
		Shape endDecorationVisual = (Shape) visual.getEndDecoration();

		// stroke paint
		if (((GeometryNode<?>) visual.getCurve()).getStroke() != content.getStroke()) {
			((GeometryNode<?>) visual.getCurve()).setStroke(content.getStroke());
		}
		if (startDecorationVisual != null && startDecorationVisual.getStroke() != content.getStroke()) {
			startDecorationVisual.setStroke(content.getStroke());
		}
		if (endDecorationVisual != null && endDecorationVisual.getStroke() != content.getStroke()) {
			endDecorationVisual.setStroke(content.getStroke());
		}

		// stroke width
		if (((GeometryNode<?>) visual.getCurve()).getStrokeWidth() != content.getStrokeWidth()) {
			((GeometryNode<?>) visual.getCurve()).setStrokeWidth(content.getStrokeWidth());
		}
		if (startDecorationVisual != null && startDecorationVisual.getStrokeWidth() != content.getStrokeWidth()) {
			startDecorationVisual.setStrokeWidth(content.getStrokeWidth());
		}
		if (endDecorationVisual != null && endDecorationVisual.getStrokeWidth() != content.getStrokeWidth()) {
			endDecorationVisual.setStrokeWidth(content.getStrokeWidth());
		}

		// dashes
		List<Double> dashList = new ArrayList<>(content.getDashes().length);
		for (double d : content.getDashes()) {
			dashList.add(d);
		}
		if (!((GeometryNode<?>) visual.getCurve()).getStrokeDashArray().equals(dashList)) {
			((GeometryNode<?>) visual.getCurve()).getStrokeDashArray().setAll(dashList);
		}

		// connection router
		if (content.getRoutingStyle().equals(RoutingStyle.ORTHOGONAL)) {
			// re-attach visual in case we are connected to an anchor with
			// non orthogonal computation strategy
			if (getVisual().getStartAnchor() != null && getVisual().getStartAnchor() instanceof DynamicAnchor
					&& !(((DynamicAnchor) getVisual().getStartAnchor())
							.getComputationStrategy() instanceof OrthogonalProjectionStrategy)) {
				IVisualPart<Node, ? extends Node> anchorage = getViewer().getVisualPartMap()
						.get(getVisual().getStartAnchor().getAnchorage());
				detachFromAnchorageVisual(anchorage, START_ROLE);
				if (anchorage != this) {
					// connected to anchorage
					attachToAnchorageVisual(anchorage, START_ROLE);
				}
			}
			if (getVisual().getEndAnchor() != null && getVisual().getEndAnchor() instanceof DynamicAnchor
					&& !(((DynamicAnchor) getVisual().getEndAnchor())
							.getComputationStrategy() instanceof OrthogonalProjectionStrategy)) {
				IVisualPart<Node, ? extends Node> anchorage = getViewer().getVisualPartMap()
						.get(getVisual().getEndAnchor().getAnchorage());
				detachFromAnchorageVisual(anchorage, END_ROLE);
				if (anchorage != this) {
					// connected to anchorage
					attachToAnchorageVisual(anchorage, END_ROLE);
				}
			}
			if (!(visual.getInterpolator() instanceof PolylineInterpolator)) {
				visual.setInterpolator(new PolylineInterpolator());
			}
			if (!(visual.getRouter() instanceof OrthogonalRouter)) {
				visual.setRouter(new OrthogonalRouter());
			}
		} else {
			// re-attach visual in case we are connected to an anchor with
			// orthogonal computation strategy
			if (getVisual().getStartAnchor() != null && getVisual().getStartAnchor() instanceof DynamicAnchor
					&& ((DynamicAnchor) getVisual().getStartAnchor())
							.getComputationStrategy() instanceof OrthogonalProjectionStrategy) {
				IVisualPart<Node, ? extends Node> anchorage = getViewer().getVisualPartMap()
						.get(getVisual().getStartAnchor().getAnchorage());
				detachFromAnchorageVisual(anchorage, START_ROLE);
				attachToAnchorageVisual(anchorage, START_ROLE);
			}
			if (getVisual().getEndAnchor() != null && getVisual().getEndAnchor() instanceof DynamicAnchor
					&& ((DynamicAnchor) getVisual().getEndAnchor())
							.getComputationStrategy() instanceof OrthogonalProjectionStrategy) {
				IVisualPart<Node, ? extends Node> anchorage = getViewer().getVisualPartMap()
						.get(getVisual().getEndAnchor().getAnchorage());
				detachFromAnchorageVisual(anchorage, END_ROLE);
				attachToAnchorageVisual(anchorage, END_ROLE);
			}
			if (!(visual.getInterpolator() instanceof PolyBezierInterpolator)) {
				visual.setInterpolator(new PolyBezierInterpolator());
			}
			if (!(visual.getRouter() instanceof StraightRouter)) {
				visual.setRouter(new StraightRouter());
			}
		}

		previousContent = content;

		// apply effect
		super.doRefreshVisual(visual);

	}

	@Override
	public FXGeometricCurve getContent() {
		return (FXGeometricCurve) super.getContent();
	}

	@Override
	public void setContent(Object model) {
		if (model != null && !(model instanceof FXGeometricCurve)) {
			throw new IllegalArgumentException("Only ICurve models are supported.");
		}
		if (getContent() != null) {
			// remove property change listeners from model
			getContent().wayPointsProperty().removeListener(wayPointsChangeListener);
			getContent().dashesProperty().removeListener(dashesChangeListener);
			getContent().routingStyleProperty().removeListener(routingStyleChangeListener);
			getContent().sourceDecorationProperty().removeListener(decorationChangeListener);
			getContent().targetDecorationProperty().removeListener(decorationChangeListener);
		}
		super.setContent(model);
		if (getContent() != null) {
			// add property change listeners to model
			getContent().wayPointsProperty().addListener(wayPointsChangeListener);
			getContent().dashesProperty().addListener(dashesChangeListener);
			getContent().routingStyleProperty().addListener(routingStyleChangeListener);
			getContent().sourceDecorationProperty().addListener(decorationChangeListener);
			getContent().targetDecorationProperty().addListener(decorationChangeListener);
		}
	}

	@Override
	public void transformContent(AffineTransform transform) {
		// applying transform to content is done by transforming waypoints
		getContent().setWayPoints(transform.getTransformed(getContent().getWayPoints().toArray(new Point[] {})));
	}

}
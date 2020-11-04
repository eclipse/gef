/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.examples.logo.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.gef.common.collections.SetMultimapChangeListener;
import org.eclipse.gef.fx.anchors.DynamicAnchor;
import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.anchors.OrthogonalProjectionStrategy;
import org.eclipse.gef.fx.internal.nodes.ConnectionEx;
import org.eclipse.gef.fx.internal.nodes.IBendableCurve;
import org.eclipse.gef.fx.internal.nodes.Traverse;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.nodes.PolyBezierInterpolator;
import org.eclipse.gef.fx.nodes.PolylineInterpolator;
import org.eclipse.gef.fx.nodes.StraightRouter;
import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.IGeometry;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.mvc.examples.logo.model.AbstractGeometricElement;
import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve;
import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve.Decoration;
import org.eclipse.gef.mvc.examples.logo.model.GeometricCurve.RoutingStyle;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.providers.IAnchorProvider;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;

@SuppressWarnings("restriction")
public abstract class GeometricCurvePart<C extends Node, D extends Node, T extends Node & IBendableCurve<C, D>>
		extends AbstractGeometricElementPart<T> implements IBendableContentPart<T> {

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

	public static final class CONNECTION
			extends GeometricCurvePart<GeometryNode<? extends ICurve>, Node, ConnectionEx> {
		@Override
		protected ConnectionEx doCreateVisual() {
			return new ConnectionEx();
		}
	}

	public static final class TRAVERSE extends GeometricCurvePart<Polyline, Shape, Traverse> {
		@Override
		protected Traverse doCreateVisual() {
			return new Traverse();
		}
	}

	private final CircleHead START_CIRCLE_HEAD = new CircleHead();
	private final CircleHead END_CIRCLE_HEAD = new CircleHead();
	private final ArrowHead START_ARROW_HEAD = new ArrowHead();
	private final ArrowHead END_ARROW_HEAD = new ArrowHead();

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

	public GeometricCurvePart() {
		anchoragesUnmodifiableProperty()
				.addListener(new SetMultimapChangeListener<IVisualPart<? extends Node>, String>() {
					@Override
					public void onChanged(Change<? extends IVisualPart<? extends Node>, ? extends String> change) {
						// XXX: As the selection handles show the connected
						// state, their visuals need to be refreshed.
						// TODO: Rather register an anchorages-observer within
						// CircleSegmentHandlePart when attaching to an
						// anchorage.
						for (IVisualPart<? extends Node> anchored : getAnchoredsUnmodifiable()) {
							anchored.refreshVisual();
						}
					}
				});
	}

	@Override
	protected void doAttachToAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
		IAnchor anchor = anchorage.getAdapter(IAnchorProvider.class).get(this, role);
		if (role.equals(SOURCE_ROLE)) {
			getVisual().setStartAnchor(anchor);
			getContent().setWayPoint(0, getVisual().getStartPoint());
		} else if (role.equals(TARGET_ROLE)) {
			getVisual().setEndAnchor(anchor);
			getContent().setWayPoint(getContent().getWayPoints().size() - 1, getVisual().getEndPoint());
		} else {
			throw new IllegalStateException("Cannot attach to anchor with role <" + role + ">.");
		}
	}

	@Override
	protected void doAttachToContentAnchorage(Object contentAnchorage, String role) {
		if (!(contentAnchorage instanceof AbstractGeometricElement)) {
			throw new IllegalArgumentException("Inappropriate content anchorage: wrong type.");
		}
		AbstractGeometricElement<?> geom = (AbstractGeometricElement<?>) contentAnchorage;
		if (SOURCE_ROLE.equals(role)) {
			getContent().getSourceAnchorages().add(geom);
		} else if (TARGET_ROLE.equals(role)) {
			getContent().getTargetAnchorages().add(geom);
		}
	}

	@Override
	protected void doDetachFromAnchorageVisual(IVisualPart<? extends Node> anchorage, String role) {
		if (role.equals(SOURCE_ROLE)) {
			getVisual().setStartPoint(getVisual().getStartPoint());
			getContent().setWayPoint(0, getVisual().getStartPoint());
		} else if (role.equals(TARGET_ROLE)) {
			getVisual().setEndPoint(getVisual().getEndPoint());
			getContent().setWayPoint(getContent().getWayPoints().size() - 1, getVisual().getEndPoint());
		} else {
			throw new IllegalStateException("Cannot detach from anchor with role <" + role + ">.");
		}
	}

	@Override
	protected void doDetachFromContentAnchorage(Object contentAnchorage, String role) {
		if (SOURCE_ROLE.equals(role)) {
			getContent().getSourceAnchorages().remove(contentAnchorage);
		} else if (TARGET_ROLE.equals(role)) {
			getContent().getTargetAnchorages().remove(contentAnchorage);
		}
	}

	@Override
	protected SetMultimap<Object, String> doGetContentAnchorages() {
		SetMultimap<Object, String> anchorages = HashMultimap.create();

		Set<AbstractGeometricElement<? extends IGeometry>> sourceAnchorages = getContent().getSourceAnchorages();
		for (Object src : sourceAnchorages) {
			anchorages.put(src, SOURCE_ROLE);
		}
		Set<AbstractGeometricElement<? extends IGeometry>> targetAnchorages = getContent().getTargetAnchorages();
		for (Object dst : targetAnchorages) {
			anchorages.put(dst, TARGET_ROLE);
		}
		return anchorages;
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		return Collections.emptyList();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doRefreshVisual(T visual) {
		setVisualBendPoints(getContentBendPoints());

		GeometricCurve content = getContent();

		List<Double> dashList = new ArrayList<>(content.getDashes().length);
		for (double d : content.getDashes()) {
			dashList.add(d);
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
				visual.setStartDecoration((D) START_CIRCLE_HEAD);
			}
			break;
		case ARROW:
			if (visual.getStartDecoration() == null || !(visual.getStartDecoration() instanceof ArrowHead)) {
				visual.setStartDecoration((D) START_ARROW_HEAD);
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
				visual.setEndDecoration((D) END_CIRCLE_HEAD);
			}
			break;
		case ARROW:
			if (visual.getEndDecoration() == null || !(visual.getEndDecoration() instanceof ArrowHead)) {
				visual.setEndDecoration((D) END_ARROW_HEAD);
			}
			break;
		}

		Shape startDecorationVisual = (Shape) visual.getStartDecoration();
		Shape endDecorationVisual = (Shape) visual.getEndDecoration();

		if (visual.getCurve() instanceof GeometryNode) {
			if (((GeometryNode<?>) visual.getCurve()).getStrokeLineCap() != StrokeLineCap.BUTT) {
				((GeometryNode<?>) visual.getCurve()).setStrokeLineCap(StrokeLineCap.BUTT);
			}
			((GeometryNode<?>) visual.getCurve()).setStrokeLineCap(StrokeLineCap.BUTT);
			if (((GeometryNode<?>) visual.getCurve()).getStroke() != content.getStroke()) {
				((GeometryNode<?>) visual.getCurve()).setStroke(content.getStroke());
			}
			if (((GeometryNode<?>) visual.getCurve()).getStrokeWidth() != content.getStrokeWidth()) {
				((GeometryNode<?>) visual.getCurve()).setStrokeWidth(content.getStrokeWidth());
			}
			if (!((GeometryNode<?>) visual.getCurve()).getStrokeDashArray().equals(dashList)) {
				((GeometryNode<?>) visual.getCurve()).getStrokeDashArray().setAll(dashList);
			}
		} else if (visual.getCurve() instanceof Shape) {
			if (((Shape) visual.getCurve()).getStrokeLineCap() != StrokeLineCap.BUTT) {
				((Shape) visual.getCurve()).setStrokeLineCap(StrokeLineCap.BUTT);
			}
			if (((Shape) visual.getCurve()).getStroke() != content.getStroke()) {
				((Shape) visual.getCurve()).setStroke(content.getStroke());
			}
			if (((Shape) visual.getCurve()).getStrokeWidth() != content.getStrokeWidth()) {
				((Shape) visual.getCurve()).setStrokeWidth(content.getStrokeWidth());
			}
			if (!((Shape) visual.getCurve()).getStrokeDashArray().equals(dashList)) {
				((Shape) visual.getCurve()).getStrokeDashArray().setAll(dashList);
			}
		}

		if (startDecorationVisual != null && startDecorationVisual.getStroke() != content.getStroke()) {
			startDecorationVisual.setStroke(content.getStroke());
		}
		if (endDecorationVisual != null && endDecorationVisual.getStroke() != content.getStroke()) {
			endDecorationVisual.setStroke(content.getStroke());
		}
		if (startDecorationVisual != null && startDecorationVisual.getStrokeWidth() != content.getStrokeWidth()) {
			startDecorationVisual.setStrokeWidth(content.getStrokeWidth());
		}
		if (endDecorationVisual != null && endDecorationVisual.getStrokeWidth() != content.getStrokeWidth()) {
			endDecorationVisual.setStrokeWidth(content.getStrokeWidth());
		}

		// connection router
		if (content.getRoutingStyle().equals(RoutingStyle.ORTHOGONAL)) {
			// re-attach visual in case we are connected to an anchor with
			// non orthogonal computation strategy
			if (visual.getStartAnchor() != null && visual.getStartAnchor() instanceof DynamicAnchor
					&& !(((DynamicAnchor) visual.getStartAnchor())
							.getComputationStrategy() instanceof OrthogonalProjectionStrategy)) {
				IVisualPart<? extends Node> anchorage = getViewer().getVisualPartMap()
						.get(getVisual().getStartAnchor().getAnchorage());
				doDetachFromAnchorageVisual(anchorage, SOURCE_ROLE);
				if (anchorage != this) {
					// connected to anchorage
					doAttachToAnchorageVisual(anchorage, SOURCE_ROLE);
				}
			}
			if (visual.getEndAnchor() != null && visual.getEndAnchor() instanceof DynamicAnchor
					&& !(((DynamicAnchor) getVisual().getEndAnchor())
							.getComputationStrategy() instanceof OrthogonalProjectionStrategy)) {
				IVisualPart<? extends Node> anchorage = getViewer().getVisualPartMap()
						.get(visual.getEndAnchor().getAnchorage());
				doDetachFromAnchorageVisual(anchorage, TARGET_ROLE);
				if (anchorage != this) {
					// connected to anchorage
					doAttachToAnchorageVisual(anchorage, TARGET_ROLE);
				}
			}
			if (visual instanceof Connection) {
				if (!(((Connection) visual).getInterpolator() instanceof PolylineInterpolator)) {
					((Connection) visual).setInterpolator(new PolylineInterpolator());
				}
				if (!(((Connection) visual).getRouter() instanceof OrthogonalRouter)) {
					((Connection) visual).setRouter(new OrthogonalRouter());
				}
			}
		} else {
			// re-attach visual in case we are connected to an anchor with
			// orthogonal computation strategy
			if (getVisual().getStartAnchor() != null && getVisual().getStartAnchor() instanceof DynamicAnchor
					&& ((DynamicAnchor) getVisual().getStartAnchor())
							.getComputationStrategy() instanceof OrthogonalProjectionStrategy) {
				IVisualPart<? extends Node> anchorage = getViewer().getVisualPartMap()
						.get(getVisual().getStartAnchor().getAnchorage());
				doDetachFromAnchorageVisual(anchorage, SOURCE_ROLE);
				doAttachToAnchorageVisual(anchorage, SOURCE_ROLE);
			}
			if (visual.getEndAnchor() != null && visual.getEndAnchor() instanceof DynamicAnchor
					&& ((DynamicAnchor) visual.getEndAnchor())
							.getComputationStrategy() instanceof OrthogonalProjectionStrategy) {
				IVisualPart<? extends Node> anchorage = getViewer().getVisualPartMap()
						.get(visual.getEndAnchor().getAnchorage());
				doDetachFromAnchorageVisual(anchorage, TARGET_ROLE);
				doAttachToAnchorageVisual(anchorage, TARGET_ROLE);
			}
			if (visual instanceof Connection) {
				if (!(((Connection) visual).getInterpolator() instanceof PolyBezierInterpolator)) {
					((Connection) visual).setInterpolator(new PolyBezierInterpolator());
				}
				if (!(((Connection) visual).getRouter() instanceof StraightRouter)) {
					((Connection) visual).setRouter(new StraightRouter());
				}
			}
		}

		// apply effect
		super.doRefreshVisual(visual);
	}

	@Override
	public GeometricCurve getContent() {
		return (GeometricCurve) super.getContent();
	}

	@Override
	public List<BendPoint> getContentBendPoints() {
		List<BendPoint> bendPoints = new ArrayList<>();
		// use content way points for the positions
		List<Point> wayPoints = getContent().getWayPointsCopy();
		// if we have a source/target anchorage, create an attached bend point
		// for it
		Set<AbstractGeometricElement<? extends IGeometry>> sourceAnchorages = getContent().getSourceAnchorages();
		int startIndex = 0;
		if (sourceAnchorages != null && !sourceAnchorages.isEmpty()) {
			bendPoints.add(new BendPoint(sourceAnchorages.iterator().next(), wayPoints.get(startIndex++)));
		}
		Set<AbstractGeometricElement<? extends IGeometry>> targetAnchorages = getContent().getTargetAnchorages();
		int lastIndex = wayPoints.size() - 1;
		if (targetAnchorages != null && !targetAnchorages.isEmpty()) {
			bendPoints.add(new BendPoint(targetAnchorages.iterator().next(), wayPoints.get(lastIndex--)));
		}
		// add unattached bend-points for the rest of the way points
		for (int i = startIndex; i <= lastIndex; i++) {
			bendPoints.add(i, new BendPoint(wayPoints.get(i)));
		}
		return bendPoints;
	}

	@Override
	public void setContent(Object model) {
		if (model != null && !(model instanceof GeometricCurve)) {
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

	@SuppressWarnings("unchecked")
	@Override
	public void setContentBendPoints(List<BendPoint> bendPoints) {
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
							(AbstractGeometricElement<? extends IGeometry>) bp.getContentAnchorage());
					// update start hint
					waypoints.add(bp.getPosition());
				}
				if (i == bendPoints.size() - 1) {
					// update end anchorage
					// TODO: introduce setter so this is more concise
					getContent().addTargetAnchorage(
							(AbstractGeometricElement<? extends IGeometry>) bp.getContentAnchorage());
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

}
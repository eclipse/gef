/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
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
package org.eclipse.gef4.zest.fx.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.nodes.IConnectionInterpolator;
import org.eclipse.gef4.fx.nodes.IConnectionRouter;
import org.eclipse.gef4.fx.nodes.OrthogonalRouter;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IBendableContentPart;
import org.eclipse.gef4.mvc.parts.ITransformableContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.ZestFxModule;
import org.eclipse.gef4.zest.fx.ZestProperties;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.collections.MapChangeListener;
import javafx.scene.Node;

/**
 * The {@link EdgePart} is the controller for an {@link Edge} content object. It
 * uses {@link Connection} for the visualization.
 *
 * @author mwienand
 *
 */
public class EdgePart extends AbstractFXContentPart<Connection>
		implements ITransformableContentPart<Node, Connection>, IBendableContentPart<Node, Connection> {

	/**
	 * The role used for attaching to the source node.
	 */
	protected static final String SOURCE_ROLE = "SOURCE";
	/**
	 * The role used for attaching to the target node.
	 */
	protected static final String TARGET_ROLE = "TARGET";

	/**
	 * The CSS class that is assigned to the visual of this {@link EdgePart}.
	 */
	public static final String CSS_CLASS = "edge";

	/**
	 * The CSS class that is assigned to the {@link Connection#getCurveNode()
	 * curve node} of the {@link Connection} of this {@link EdgePart}.
	 */
	public static final String CSS_CLASS_CURVE = "curve";

	private MapChangeListener<String, Object> edgeAttributesObserver = new MapChangeListener<String, Object>() {

		@Override
		public void onChanged(MapChangeListener.Change<? extends String, ? extends Object> change) {
			if (ZestProperties.ROUTER__E.equals(change.getKey())) {
				// if the router changed, re-attach the visual (so we attach to
				// a different anchor)
				for (Entry<IVisualPart<Node, ? extends Node>, String> anchoragesByRole : getAnchoragesUnmodifiable()
						.entries()) {
					detachFromAnchorageVisual(anchoragesByRole.getKey(), anchoragesByRole.getValue());
					attachToAnchorageVisual(anchoragesByRole.getKey(), anchoragesByRole.getValue());
				}
			}
			refreshVisual();
		}

	};

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		if (!getVisual().getChildren().contains(child.getVisual())) {
			getVisual().getChildren().add(child.getVisual());
		}
	}

	@SuppressWarnings("serial")
	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		Provider<? extends IAnchor> anchorProvider = null;
		if (ZestProperties.getRouter(getContent()) != null
				&& ZestProperties.getRouter(getContent()) instanceof OrthogonalRouter) {
			anchorProvider = anchorage.getAdapter(AdapterKey.get(new TypeToken<Provider<? extends IAnchor>>() {
			}, ZestFxModule.ORTHOGONAL_ROUTING_ANCHOR_PROVIDER_ROLE));
		} else {
			anchorProvider = anchorage.getAdapter(AdapterKey.get(new TypeToken<Provider<? extends IAnchor>>() {
			}, ZestFxModule.STRAIGHT_ROUTING_ANCHOR_PROVIDER_ROLE));
		}
		if (anchorProvider == null) {
			throw new IllegalStateException("Require <Provider<IAnchor>> adapter at <" + anchorage.getClass() + ">.");
		}
		IAnchor anchor = anchorProvider == null ? null : anchorProvider.get();
		if (role.equals(SOURCE_ROLE)) {
			getVisual().setStartAnchor(anchor);
		} else if (role.equals(TARGET_ROLE)) {
			getVisual().setEndAnchor(anchor);
		} else {
			throw new IllegalArgumentException("Cannot attach to anchor with role <" + role + ">.");
		}
	}

	@Override
	public void bendContent(List<org.eclipse.gef4.mvc.parts.IBendableContentPart.BendPoint> bendPoints) {
		List<Point> waypoints = new ArrayList<>();
		for (int i = 0; i < bendPoints.size(); i++) {
			BendPoint bp = bendPoints.get(i);
			if (i == 0) {
				// update source
				org.eclipse.gef4.graph.Node newSource = bp.isAttached()
						? (org.eclipse.gef4.graph.Node) bp.getContentAnchorage() : null;
				org.eclipse.gef4.graph.Node oldSource = getContent().getSource();
				if (oldSource != newSource) {
					if (oldSource != null) {
						detachFromContentAnchorage(oldSource, SOURCE_ROLE);
					}
					if (newSource != null) {
						attachToContentAnchorage(newSource, SOURCE_ROLE);
					}
				}
			}
			if (i == bendPoints.size() - 1) {
				// update target
				org.eclipse.gef4.graph.Node newTarget = bp.isAttached()
						? (org.eclipse.gef4.graph.Node) bp.getContentAnchorage() : null;
				org.eclipse.gef4.graph.Node oldTarget = getContent().getTarget();
				if (oldTarget != newTarget) {
					if (oldTarget != null) {
						detachFromContentAnchorage(oldTarget, TARGET_ROLE);
					}
					if (newTarget != null) {
						attachToContentAnchorage(newTarget, TARGET_ROLE);
					}
				}
			}
			if (!bp.isAttached()) {
				waypoints.add(bp.getPosition());
			}
		}
		ZestProperties.setControlPoints(getContent(), waypoints);
	}

	@Override
	protected Connection createVisual() {
		Connection visual = new Connection();
		visual.getStyleClass().add(CSS_CLASS);
		visual.getCurveNode().getStyleClass().add(CSS_CLASS_CURVE);
		return visual;
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		Connection connection = getVisual();
		if (role.equals(SOURCE_ROLE)) {
			Point startPoint = connection.getStartPoint();
			connection.setStartPoint(startPoint == null ? new Point() : startPoint);
		} else if (role.equals(TARGET_ROLE)) {
			Point endPoint = connection.getEndPoint();
			connection.setEndPoint(endPoint == null ? new Point() : endPoint);
		} else {
			throw new IllegalArgumentException("Cannot detach from anchor with role <" + role + ">.");
		}
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		getContent().attributesProperty().addListener(edgeAttributesObserver);
	}

	@Override
	protected void doAttachToContentAnchorage(Object contentAnchorage, String role) {
		if (SOURCE_ROLE.equals(role)) {
			getContent().setSource((org.eclipse.gef4.graph.Node) contentAnchorage);
		} else if (TARGET_ROLE.equals(role)) {
			getContent().setTarget((org.eclipse.gef4.graph.Node) contentAnchorage);
		} else {
			throw new IllegalArgumentException("Cannot attach to content anchorage with role <" + role + ">.");
		}
	}

	@Override
	protected void doDeactivate() {
		getContent().attributesProperty().removeListener(edgeAttributesObserver);
		super.doDeactivate();
	}

	@Override
	protected void doDetachFromContentAnchorage(Object contentAnchorage, String role) {
		if (SOURCE_ROLE.equals(role)) {
			getContent().setSource(null);
		} else if (TARGET_ROLE.equals(role)) {
			getContent().setTarget(null);
		} else {
			throw new IllegalArgumentException("Cannot detach from content anchorage with role <" + role + ">.");
		}
	}

	@Override
	protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
		SetMultimap<Object, String> anchorages = HashMultimap.create();
		org.eclipse.gef4.graph.Node source = getContent().getSource();
		if (source != null) {
			anchorages.put(source, SOURCE_ROLE);
		}
		org.eclipse.gef4.graph.Node target = getContent().getTarget();
		if (target != null) {
			anchorages.put(target, TARGET_ROLE);
		}
		return anchorages;
	}

	@Override
	protected List<? extends Object> doGetContentChildren() {
		return Collections.emptyList();
	}

	@Override
	protected void doRefreshVisual(Connection visual) {
		Edge edge = getContent();
		Map<String, Object> attrs = edge.attributesProperty();
		GeometryNode<ICurve> curveNode = visual.getCurveNode();

		// css class
		if (attrs.containsKey(ZestProperties.CSS_CLASS__NE)) {
			String cssClass = ZestProperties.getCssClass(edge);
			if (!visual.getStyleClass().contains(cssClass)) {
				visual.getStyleClass().add(cssClass);
			}
		}

		// css id
		if (attrs.containsKey(ZestProperties.CSS_ID__NE)) {
			String cssId = ZestProperties.getCssId(edge);
			visual.setId(cssId);
		}

		// css style
		String connCssStyle = ZestProperties.getCurveCssStyle(edge);
		if (attrs.containsKey(ZestProperties.CURVE_CSS_STYLE__E)) {
			curveNode.setStyle(connCssStyle);
		}

		// custom decoration
		Node sourceDecoration = ZestProperties.getSourceDecoration(edge);
		if (sourceDecoration != null) {
			visual.setStartDecoration(sourceDecoration);
			// apply curve CSS style
			if (connCssStyle != null) {
				sourceDecoration.setStyle(connCssStyle);
			}
		}
		Node targetDecoration = ZestProperties.getTargetDecoration(edge);
		if (targetDecoration != null) {
			visual.setEndDecoration(targetDecoration);
			// apply curve CSS style
			if (connCssStyle != null) {
				targetDecoration.setStyle(connCssStyle);
			}
		}

		// connection router
		IConnectionRouter router = ZestProperties.getRouter(edge);
		if (router != null) {
			visual.setRouter(router);
		}

		IConnectionInterpolator interpolator = ZestProperties.getInterpolator(edge);
		if (interpolator != null) {
			visual.setInterpolator(interpolator);
		}

		// control points
		List<Point> controlPoints = new ArrayList<>(ZestProperties.getControlPoints(edge));
		if (!getContentAnchoragesUnmodifiable().containsValue(SOURCE_ROLE)) {
			if (!controlPoints.isEmpty()) {
				visual.setStartPoint(controlPoints.remove(0));
			} else {
				visual.setStartPoint(new Point());
			}
		}
		if (!getContentAnchoragesUnmodifiable().containsValue(TARGET_ROLE)) {
			if (!controlPoints.isEmpty()) {
				visual.setEndPoint(controlPoints.remove(controlPoints.size() - 1));
			} else {
				visual.setEndPoint(new Point());
			}
		}
		if (!visual.getControlPoints().equals(controlPoints)) {
			visual.setControlPoints(controlPoints);
		}
	}

	@Override
	public Edge getContent() {
		return (Edge) super.getContent();
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().remove(child.getVisual());
	}

	@Override
	public void transformContent(AffineTransform transform) {
		ZestProperties.setControlPoints(getContent(), Arrays.asList(
				transform.getTransformed(ZestProperties.getControlPoints(getContent()).toArray(new Point[] {}))));
	}

}

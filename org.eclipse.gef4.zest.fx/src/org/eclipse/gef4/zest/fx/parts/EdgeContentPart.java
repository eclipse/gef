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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.nodes.Connection;
import org.eclipse.gef4.fx.nodes.GeometryNode;
import org.eclipse.gef4.fx.nodes.IConnectionRouter;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/**
 * The {@link EdgeContentPart} is the controller for an {@link Edge} content
 * object. It uses {@link Connection} for the visualization.
 *
 * @author mwienand
 *
 */
public class EdgeContentPart extends AbstractFXContentPart<Connection> {

	/**
	 * The {@link ArrowHead} is used as a decoration for a connection
	 * {@link Connection}.
	 */
	public static class ArrowHead extends Polygon {
		/**
		 * Default constructor.
		 */
		public ArrowHead() {
			super(0, 0, 10, 3, 10, -3);
		}
	}

	/**
	 * The CSS class that is assigned to the {@link Connection} of this
	 * {@link EdgeContentPart}.
	 */
	public static final String CSS_CLASS = "edge";

	/**
	 * The CSS class that is assigned to the {@link Connection#getCurveNode()
	 * curve node} of the {@link Connection} of this {@link EdgeContentPart}.
	 */
	public static final String CSS_CLASS_CURVE = "curve";

	private PropertyChangeListener edgeAttributesPropertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (Edge.ATTRIBUTES_PROPERTY.equals(evt.getPropertyName())) {
				refreshVisual();
			}
		}
	};

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		if (!getVisual().getChildren().contains(child.getVisual())) {
			getVisual().getChildren().add(child.getVisual());
		}
	}

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		@SuppressWarnings("serial")
		Provider<? extends IAnchor> anchorProvider = anchorage
				.getAdapter(AdapterKey.get(new TypeToken<Provider<? extends IAnchor>>() {
				}));
		IAnchor anchor = anchorProvider == null ? null : anchorProvider.get();
		if (role.equals("START")) {
			getVisual().setStartAnchor(anchor);
		} else if (role.equals("END")) {
			getVisual().setEndAnchor(anchor);
		} else {
			throw new IllegalStateException("Cannot attach to anchor with role <" + role + ">.");
		}
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
		if (role.equals("START")) {
			Point startPoint = connection.getStartPoint();
			connection.setStartPoint(startPoint == null ? new Point() : startPoint);
		} else {
			Point endPoint = connection.getEndPoint();
			connection.setEndPoint(endPoint == null ? new Point() : endPoint);
		}
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		getContent().addPropertyChangeListener(edgeAttributesPropertyChangeListener);
	}

	@Override
	protected void doDeactivate() {
		getContent().removePropertyChangeListener(edgeAttributesPropertyChangeListener);
		super.doDeactivate();
	}

	@Override
	protected void doRefreshVisual(Connection visual) {
		GraphLayoutContext glc = getGraphLayoutContext();
		if (glc == null) {
			return;
		}

		Edge edge = getContent();
		Map<String, Object> attrs = edge.getAttrs();
		GeometryNode<ICurve> curveNode = visual.getCurveNode();

		// css class
		curveNode.getStyleClass().clear();
		if (!visual.getStyleClass().contains(CSS_CLASS)) {
			visual.getStyleClass().add(CSS_CLASS);
		}
		if (attrs.containsKey(ZestProperties.ELEMENT_CSS_CLASS)) {
			String cssClass = ZestProperties.getCssClass(edge);
			if (!visual.getStyleClass().contains(cssClass)) {
				visual.getStyleClass().add(cssClass);
			}
		}

		// css id
		if (attrs.containsKey(ZestProperties.ELEMENT_CSS_ID)) {
			String cssId = ZestProperties.getCssId(edge);
			visual.setId(cssId);
		}

		// css style
		if (attrs.containsKey(ZestProperties.EDGE_CURVE_CSS_STYLE)) {
			String connCssStyle = ZestProperties.getEdgeCurveCssStyle(edge);
			curveNode.setStyle(connCssStyle);
		}

		// default decoration for directed graphs
		if (ZestProperties.GRAPH_TYPE_DIRECTED.equals(ZestProperties.getType(glc.getGraph(), true))) {
			visual.setEndDecoration(new ArrowHead());
		} else {
			visual.setEndDecoration(new ArrowHead());
		}

		// custom decoration
		Shape sourceDecoration = ZestProperties.getSourceDecoration(edge);
		if (sourceDecoration != null) {
			visual.setStartDecoration(sourceDecoration);
		}
		Shape targetDecoration = ZestProperties.getTargetDecoration(edge);
		if (targetDecoration != null) {
			visual.setEndDecoration(targetDecoration);
		}

		// connection router
		IConnectionRouter router = ZestProperties.getRouter(edge);
		if (router != null) {
			visual.setRouter(router);
		}
	}

	@Override
	public Edge getContent() {
		return (Edge) super.getContent();
	}

	@Override
	public SetMultimap<Object, String> getContentAnchorages() {
		SetMultimap<Object, String> anchorages = HashMultimap.create();
		anchorages.put(getContent().getSource(), "START");
		anchorages.put(getContent().getTarget(), "END");
		return anchorages;
	}

	@Override
	public List<? extends Object> getContentChildren() {
		return Collections.emptyList();
	}

	/**
	 * Returns the {@link GraphLayoutContext} that corresponds to the
	 * {@link Graph} to which the content of this {@link EdgeContentPart}
	 * belongs.
	 *
	 * @return The {@link GraphLayoutContext} that corresponds to the
	 *         {@link Graph} to which the content of this
	 *         {@link EdgeContentPart} belongs.
	 */
	protected GraphLayoutContext getGraphLayoutContext() {
		return getViewer().getContentPartMap().get(getContent().getGraph()).getAdapter(GraphLayoutContext.class);
	}

	@Override
	protected void register(IViewer<Node> viewer) {
		super.register(viewer);
	}

	@Override
	protected void removeChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		getVisual().getChildren().remove(child.getVisual());
	}

}

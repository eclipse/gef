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
import java.util.Map;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXConnection;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.fx.nodes.IFXConnectionRouter;
import org.eclipse.gef4.fx.nodes.IFXDecoration;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.mvc.viewer.IViewer;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

import javafx.scene.Node;
import javafx.scene.shape.Polyline;

/**
 * The {@link EdgeContentPart} is the controller for an {@link Edge} content
 * object. It uses {@link FXConnection} for the visualization.
 *
 * @author mwienand
 *
 */
public class EdgeContentPart extends AbstractFXContentPart<FXConnection> {

	/**
	 * The {@link ArrowHead} is an {@link IFXDecoration} implementation that can
	 * be used to displays an arrow at either side of an {@link FXConnection}.
	 */
	public static class ArrowHead extends Polyline implements IFXDecoration {
		/**
		 * Default constructor.
		 */
		public ArrowHead() {
			super(15.0, 0.0, 10.0, 0.0, 10.0, 3.0, 0.0, 0.0, 10.0, -3.0, 10.0, 0.0);
		}

		@Override
		public Point getLocalEndPoint() {
			return new Point(15, 0);
		}

		@Override
		public Point getLocalStartPoint() {
			return new Point(0, 0);
		}

		@Override
		public Node getVisual() {
			return this;
		}
	}

	/**
	 * The CSS class that is assigned to the {@link FXConnection} of this
	 * {@link EdgeContentPart}.
	 */
	public static final String CSS_CLASS = "edge";

	/**
	 * The CSS class that is assigned to the {@link FXConnection#getCurveNode()
	 * curve node} of the {@link FXConnection} of this {@link EdgeContentPart}.
	 */
	public static final String CSS_CLASS_CURVE = "curve";

	/**
	 * The CSS class that is assigned to the visualization of the
	 * {@link EdgeLabelPart} of this {@link EdgeContentPart}.
	 */
	public static final String CSS_CLASS_LABEL = "label";

	@Inject
	private Injector injector;
	private PropertyChangeListener edgeAttributesPropertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (Edge.ATTRIBUTES_PROPERTY.equals(evt.getPropertyName())) {
				refreshVisual();
			}
		}
	};

	private EdgeLabelPart edgeLabelPart;

	@Override
	protected void addChildVisual(IVisualPart<Node, ? extends Node> child, int index) {
		if (!getVisual().getChildren().contains(child.getVisual())) {
			getVisual().getChildren().add(child.getVisual());
		}
	}

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		@SuppressWarnings("serial")
		Provider<? extends IFXAnchor> anchorProvider = anchorage
				.getAdapter(AdapterKey.get(new TypeToken<Provider<? extends IFXAnchor>>() {
				}));
		IFXAnchor anchor = anchorProvider == null ? null : anchorProvider.get();
		if (role.equals("START")) {
			getVisual().setStartAnchor(anchor);
		} else if (role.equals("END")) {
			getVisual().setEndAnchor(anchor);
		} else {
			throw new IllegalStateException("Cannot attach to anchor with role <" + role + ">.");
		}
	}

	@Override
	protected FXConnection createVisual() {
		FXConnection visual = new FXConnection();
		visual.getStyleClass().add(CSS_CLASS);
		visual.getCurveNode().getStyleClass().add(CSS_CLASS_CURVE);
		return visual;
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node, ? extends Node> anchorage, String role) {
		FXConnection connection = getVisual();
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
		// add label part
		if (edgeLabelPart == null) {
			edgeLabelPart = injector.getInstance(EdgeLabelPart.class);
			edgeLabelPart.getVisual().getStyleClass().add(CSS_CLASS_LABEL);
			getParent().addChild(edgeLabelPart);
			edgeLabelPart.addAnchorage(this);
		}
	}

	@Override
	protected void doDeactivate() {
		getContent().removePropertyChangeListener(edgeAttributesPropertyChangeListener);
		super.doDeactivate();
	}

	@Override
	public void doRefreshVisual(FXConnection visual) {
		GraphLayoutContext glc = getGraphLayoutContext();
		if (glc == null || edgeLabelPart == null) {
			return;
		}

		Edge edge = getContent();
		Map<String, Object> attrs = edge.getAttrs();
		FXGeometryNode<ICurve> curveNode = visual.getCurveNode();

		// css class
		visual.getStyleClass().clear();
		visual.getStyleClass().add(CSS_CLASS);
		if (attrs.containsKey(ZestProperties.ELEMENT_CSS_CLASS)) {
			String cssClass = ZestProperties.getCssClass(edge);
			visual.getStyleClass().add(cssClass);
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
		if (attrs.containsKey(ZestProperties.EDGE_LABEL_CSS_STYLE)) {
			String textCssStyle = ZestProperties.getEdgeLabelCssStyle(edge);
			edgeLabelPart.getVisual().setStyle(textCssStyle);
		}

		// label
		Object label = attrs.get(ZestProperties.ELEMENT_LABEL);
		if (label instanceof String) {
			edgeLabelPart.getVisual().setText((String) label);
		}

		// default decoration for directed graphs
		if (ZestProperties.GRAPH_TYPE_DIRECTED.equals(ZestProperties.getType(glc.getGraph(), true))) {
			visual.setEndDecoration(new ArrowHead());
		} else {
			visual.setEndDecoration(null);
		}

		// custom decoration
		IFXDecoration sourceDecoration = ZestProperties.getSourceDecoration(edge);
		if (sourceDecoration != null) {
			visual.setStartDecoration(sourceDecoration);
		}
		IFXDecoration targetDecoration = ZestProperties.getTargetDecoration(edge);
		if (targetDecoration != null) {
			visual.setEndDecoration(targetDecoration);
		}

		// connection router
		IFXConnectionRouter router = ZestProperties.getRouter(edge);
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

	@Override
	public void setContent(Object content) {
		super.setContent(content);
		if (content == null) {
			return;
		}
		if (!(content instanceof Edge)) {
			throw new IllegalArgumentException("Content of wrong type!");
		}
		final FXConnection visual = getVisual();
		setAdapter(AdapterKey.get(Provider.class, FXDefaultFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER),
				new Provider<IGeometry>() {
					@Override
					public IGeometry get() {
						return FXUtils.localToParent(visual, FXUtils.localToParent(visual.getCurveNode(),
								((FXGeometryNode<?>) visual.getCurveNode()).getGeometry()));
					}
				});
		setAdapter(AdapterKey.get(Provider.class, FXDefaultFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER),
				new Provider<IGeometry>() {
					@Override
					public IGeometry get() {
						return FXUtils.localToParent(visual, FXUtils.localToParent(visual.getCurveNode(),
								((FXGeometryNode<?>) visual.getCurveNode()).getGeometry()));
					}
				});
	}

	@Override
	protected void unregister(IViewer<Node> viewer) {
		if (edgeLabelPart != null) {
			removeAnchored(edgeLabelPart);
			getParent().removeChild(edgeLabelPart);
			edgeLabelPart = null;
		}
		super.unregister(viewer);
	}

}

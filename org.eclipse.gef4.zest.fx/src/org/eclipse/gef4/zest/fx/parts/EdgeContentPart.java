/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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

import javafx.scene.Node;
import javafx.scene.shape.Polyline;

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
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.LayoutModel;
import org.eclipse.gef4.zest.fx.parts.EdgeContentPart.ArrowHead;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class EdgeContentPart extends AbstractFXContentPart<FXConnection> {

	public static class ArrowHead extends Polyline implements IFXDecoration {
		public ArrowHead() {
			super(15.0, 0.0, 10.0, 0.0, 10.0, 3.0, 0.0, 0.0, 10.0, -3.0, 10.0,
					0.0);
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

	public static final String CSS_CLASS = "edge";

	public static final String CSS_CLASS_CURVE = "curve";

	public static final String CSS_CLASS_LABEL = "label";
	private static final double GAP_LENGTH = 7d;
	private static final double DASH_LENGTH = 7d;

	private static final Double DOT_LENGTH = 1d;
	private EdgeLabelPart edgeLabelPart;

	@Override
	protected void attachToAnchorageVisual(
			IVisualPart<Node, ? extends Node> anchorage, String role) {
		@SuppressWarnings("serial")
		Provider<? extends IFXAnchor> anchorProvider = anchorage
				.getAdapter(AdapterKey
						.get(new TypeToken<Provider<? extends IFXAnchor>>() {
						}));
		IFXAnchor anchor = anchorProvider == null ? null : anchorProvider.get();
		if (role.equals("START")) {
			getVisual().setStartAnchor(anchor);
		} else if (role.equals("END")) {
			getVisual().setEndAnchor(anchor);
		} else if (role.equals("LABEL")) {
			if (!getVisual().getChildren().contains(anchorage.getVisual())) {
				getVisual().getChildren().add(anchorage.getVisual());
			}
		} else {
			throw new IllegalStateException(
					"Cannot attach to anchor with role <" + role + ">.");
		}
	}

	@Override
	protected FXConnection createVisual() {
		// create connection
		FXConnection visual = new FXConnection() {
			@Override
			protected void refreshGeometry() {
				super.refreshGeometry();
				// IMPORTANT: The FXConnection clears its children, therefore,
				// we have to re-insert the edgeLabelPart here.
				// TODO: Therefore, the labels should be handled as children of
				// the root part instead.
				if (edgeLabelPart != null) {
					if (!getVisual().getChildren().contains(
							edgeLabelPart.getVisual())) {
						getVisual().getChildren()
								.add(edgeLabelPart.getVisual());
					}
					edgeLabelPart.refreshVisual();
				}
			}
		};
		visual.getStyleClass().add(CSS_CLASS);
		visual.getCurveNode().getStyleClass().add(CSS_CLASS_CURVE);
		return visual;
	}

	@Override
	protected void detachFromAnchorageVisual(
			IVisualPart<Node, ? extends Node> anchorage, String role) {
		FXConnection connection = getVisual();
		if (role.equals("START")) {
			Point startPoint = connection.getStartPoint();
			connection.setStartPoint(startPoint == null ? new Point()
					: startPoint);
		} else {
			Point endPoint = connection.getEndPoint();
			connection.setEndPoint(endPoint == null ? new Point() : endPoint);
		}
	}

	@Override
	protected void doActivate() {
		super.doActivate();
		getContent().addPropertyChangeListener(
				edgeAttributesPropertyChangeListener);
	}

	@Override
	protected void doDeactivate() {

		getContent().removePropertyChangeListener(
				edgeAttributesPropertyChangeListener);
		super.doDeactivate();
	}

	@Override
	public void doRefreshVisual(FXConnection visual) {
		GraphLayoutContext glc = getLayoutModel();
		if (glc == null || edgeLabelPart == null) {
			return;
		}

		// add label on first refresh
		if (edgeLabelPart == null) {
			edgeLabelPart = new EdgeLabelPart();
			injector.injectMembers(edgeLabelPart);
			edgeLabelPart.getVisual().getStyleClass().add(CSS_CLASS_LABEL);
			addAnchorage(edgeLabelPart, "LABEL");
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
		if (ZestProperties.GRAPH_TYPE_DIRECTED.equals(ZestProperties.getType(
				glc.getGraph(), true))) {
			visual.setEndDecoration(new ArrowHead());
		} else {
			visual.setEndDecoration(null);
		}

		// custom decoration
		IFXDecoration sourceDecoration = ZestProperties
				.getSourceDecoration(edge);
		if (sourceDecoration != null) {
			visual.setStartDecoration(sourceDecoration);
		}
		IFXDecoration targetDecoration = ZestProperties
				.getTargetDecoration(edge);
		if (targetDecoration != null) {
			visual.setEndDecoration(targetDecoration);
		}

		// connection router
		IFXConnectionRouter router = ZestProperties.getRouter(edge);
		if (router != null) {
			visual.setRouter(router);
		}

		// dashes
		Object style = attrs.get(ZestProperties.EDGE_STYLE);
		if (style == ZestProperties.EDGE_STYLE_DASHED) {
			curveNode.getStrokeDashArray().setAll(DASH_LENGTH, GAP_LENGTH);
		} else if (style == ZestProperties.EDGE_STYLE_DASHDOT) {
			curveNode.getStrokeDashArray().setAll(DASH_LENGTH, GAP_LENGTH,
					DOT_LENGTH, GAP_LENGTH);
		} else if (style == ZestProperties.EDGE_STYLE_DASHDOTDOT) {
			curveNode.getStrokeDashArray().setAll(DASH_LENGTH, GAP_LENGTH,
					DOT_LENGTH, GAP_LENGTH, DOT_LENGTH, GAP_LENGTH);
		} else if (style == ZestProperties.EDGE_STYLE_DOTTED) {
			curveNode.getStrokeDashArray().setAll(DOT_LENGTH, GAP_LENGTH);
		} else {
			curveNode.getStrokeDashArray().clear();
		}

		// visibility
		IContentPart<Node, ? extends Node> sourcePart = getViewer()
				.getContentPartMap().get(edge.getSource());
		IContentPart<Node, ? extends Node> targetPart = getViewer()
				.getContentPartMap().get(edge.getTarget());

		if (sourcePart != null && targetPart != null
				&& sourcePart.getVisual().isVisible()
				&& targetPart.getVisual().isVisible()) {
			visual.setVisible(true);
			visual.setMouseTransparent(false);
		} else {
			visual.setVisible(false);
			visual.setMouseTransparent(true);
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

	protected LayoutModel getLayoutModel() {
		if (getContent() == null) {
			return null;
		}
		IContentPart<Node, ? extends Node> contentPart = getViewer()
				.getContentPartMap().get(getContent().getGraph());
		return contentPart == null ? null : contentPart
				.getAdapter(LayoutModel.class);
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
		setAdapter(
				AdapterKey
						.get(Provider.class,
								FXDefaultFeedbackPartFactory.SELECTION_FEEDBACK_GEOMETRY_PROVIDER),
				new Provider<IGeometry>() {
					@Override
					public IGeometry get() {
						return FXUtils.localToParent(visual, FXUtils
								.localToParent(visual.getCurveNode(),
										((FXGeometryNode<?>) visual
												.getCurveNode()).getGeometry()));
					}
				});
		setAdapter(AdapterKey.get(Provider.class,
				FXDefaultFeedbackPartFactory.HOVER_FEEDBACK_GEOMETRY_PROVIDER),
				new Provider<IGeometry>() {
					@Override
					public IGeometry get() {
						return FXUtils.localToParent(visual, FXUtils
								.localToParent(visual.getCurveNode(),
										((FXGeometryNode<?>) visual
												.getCurveNode()).getGeometry()));
					}
				});
	}

}

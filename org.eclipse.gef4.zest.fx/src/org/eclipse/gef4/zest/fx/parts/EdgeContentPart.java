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

import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.shape.Polyline;
import javafx.scene.text.Text;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXConnection;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.fx.nodes.FXUtils;
import org.eclipse.gef4.fx.nodes.IFXDecoration;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.parts.FXDefaultFeedbackPartFactory;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.LayoutModel;
import org.eclipse.gef4.zest.fx.parts.EdgeContentPart.FXLabeledConnection;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;

public class EdgeContentPart extends AbstractFXContentPart<FXLabeledConnection> {

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

	public class FXLabeledConnection extends FXConnection {

		protected Text text = new Text();
		// TODO: protected HBox hbox = new HBox();
		// TODO: protected ImageView imageView = new ImageView();

		{
			text.setTextOrigin(VPos.TOP);
			text.setManaged(false);
		}

		public Text getLabelText() {
			return text;
		}

		@Override
		protected void refreshGeometry() {
			super.refreshGeometry();

			// TODO: image

			Bounds textBounds = text.getLayoutBounds();
			Rectangle bounds = getCurveNode().getGeometry().getBounds();
			text.setTranslateX(bounds.getX() + bounds.getWidth() / 2
					- textBounds.getWidth() / 2);
			text.setTranslateY(bounds.getY() + bounds.getHeight() / 2
					- textBounds.getHeight());
			// FIXME: add to children list at beginning
			if (!getChildren().contains(text)) {
				getChildren().add(text);
			}
		}

	}

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

	@Override
	protected void attachToAnchorageVisual(
			IVisualPart<Node, ? extends Node> anchorage, String role) {
		@SuppressWarnings("serial")
		IFXAnchor anchor = anchorage.getAdapter(
				AdapterKey.get(new TypeToken<Provider<? extends IFXAnchor>>() {
				})).get();
		if (role.equals("START")) {
			getVisual().setStartAnchor(anchor);
		} else if (role.equals("END")) {
			getVisual().setEndAnchor(anchor);
		} else {
			throw new IllegalStateException(
					"Cannot attach to anchor with role <" + role + ">.");
		}
	}

	@Override
	protected FXLabeledConnection createVisual() {
		FXLabeledConnection visual = new FXLabeledConnection();
		visual.getStyleClass().add(CSS_CLASS);
		visual.getCurveNode().getStyleClass().add(CSS_CLASS_CURVE);
		visual.getLabelText().getStyleClass().add(CSS_CLASS_LABEL);
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
	public void doRefreshVisual(FXLabeledConnection visual) {
		GraphLayoutContext glc = getLayoutModel();
		if (glc == null) {
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
			visual.getLabelText().setStyle(textCssStyle);
		}

		// label
		Object label = attrs.get(ZestProperties.ELEMENT_LABEL);
		if (label instanceof String) {
			visual.getLabelText().setText((String) label);
		}

		// decoration
		if (ZestProperties.GRAPH_TYPE_DIRECTED.equals(ZestProperties.getType(
				glc.getGraph(), true))) {
			visual.setEndDecoration(new ArrowHead());
		} else {
			visual.setEndDecoration(null);
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
		return getViewer().getContentPartMap().get(getContent().getGraph())
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
		final FXLabeledConnection visual = getVisual();
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

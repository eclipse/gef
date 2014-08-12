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

import java.util.Map;

import javafx.scene.Node;
import javafx.scene.shape.Polyline;

import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.fx.anchors.AnchorLink;
import org.eclipse.gef4.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXChopBoxHelper;
import org.eclipse.gef4.fx.nodes.FXCurveConnection;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.fx.nodes.FXLabeledConnection;
import org.eclipse.gef4.fx.nodes.IFXDecoration;
import org.eclipse.gef4.geometry.convert.fx.JavaFX2Geometry;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr;
import org.eclipse.gef4.mvc.behaviors.AbstractHoverBehavior;
import org.eclipse.gef4.mvc.behaviors.AbstractSelectionBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXHoverBehavior;
import org.eclipse.gef4.mvc.fx.behaviors.FXSelectionBehavior;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.fx.models.ILayoutModel;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class EdgeContentPart extends AbstractFXContentPart {

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

	public static final String CSS_CLASS = "edge";
	public static final Object ATTR_CLASS = "class";
	public static final Object ATTR_ID = "id";

	private static final double GAP_LENGTH = 7d;
	private static final double DASH_LENGTH = 7d;
	private static final Double DOT_LENGTH = 1d;

	private FXLabeledConnection visual;

	{
		visual = new FXLabeledConnection();
		new FXChopBoxHelper(visual.getConnection());
		visual.getStyleClass().add(CSS_CLASS);
		visual.getConnection().getCurveNode().getStyleClass().add("curve");
	}

	@Override
	protected void attachToAnchorageVisual(IVisualPart<Node> anchorage,
			String role) {

		IFXAnchor anchor = ((AbstractFXContentPart) anchorage).getAnchor(this);
		FXCurveConnection connection = visual.getConnection();
		if (role.equals("START")) {
			connection.setStartAnchor(anchor);
		} else {
			connection.setEndAnchor(anchor);
		}

		if (connection.isStartConnected() && connection.isEndConnected()) {
			AnchorLink startAl = connection.startAnchorLinkProperty().get();
			AnchorLink endAl = connection.endAnchorLinkProperty().get();
			((FXChopBoxAnchor) startAl.getAnchor()).setReferencePoint(
					startAl.getKey(), getAnchorageCenter(endAl));
			((FXChopBoxAnchor) endAl.getAnchor()).setReferencePoint(
					endAl.getKey(), startAl.getPosition());
		}
	}

	@Override
	protected void detachFromAnchorageVisual(IVisualPart<Node> anchorage,
			String role) {
		FXCurveConnection connection = visual.getConnection();
		if (role.equals("START")) {
			connection.setStartPoint(connection.getStartPoint());
		} else {
			connection.setEndPoint(connection.getEndPoint());
		}
	}

	@Override
	public void doRefreshVisual() {
		GraphLayoutContext glc = (GraphLayoutContext) getViewer().getDomain()
				.getAdapter(ILayoutModel.class).getLayoutContext();
		if (glc == null) {
			return;
		}

		// decoration
		FXCurveConnection connection = visual.getConnection();
		if (Attr.Value.GRAPH_DIRECTED.equals(glc.getGraph().getAttrs()
				.get(Attr.Key.GRAPH_TYPE.toString()))) {
			connection.setEndDecoration(new ArrowHead());
		} else {
			connection.setEndDecoration(null);
		}

		// TODO: visibility
		FXGeometryNode<ICurve> curveNode = connection.getCurveNode();

		// dashes
		Object style = getContent().getAttrs().get(
				Graph.Attr.Key.EDGE_STYLE.toString());
		if (style == Graph.Attr.Value.LINE_DASH) {
			curveNode.getStrokeDashArray().setAll(DASH_LENGTH, GAP_LENGTH);
		} else if (style == Graph.Attr.Value.LINE_DASHDOT) {
			curveNode.getStrokeDashArray().setAll(DASH_LENGTH, GAP_LENGTH,
					DOT_LENGTH, GAP_LENGTH);
		} else if (style == Graph.Attr.Value.LINE_DASHDOTDOT) {
			curveNode.getStrokeDashArray().setAll(DASH_LENGTH, GAP_LENGTH,
					DOT_LENGTH, GAP_LENGTH, DOT_LENGTH, GAP_LENGTH);
		} else if (style == Graph.Attr.Value.LINE_DOT) {
			curveNode.getStrokeDashArray().setAll(DOT_LENGTH, GAP_LENGTH);
		} else {
			curveNode.getStrokeDashArray().clear();
		}
	}

	private Point getAnchorageCenter(AnchorLink al) {
		Node anchorage = al.getAnchor().getAnchorageNode();
		Rectangle bounds = JavaFX2Geometry.toRectangle(anchorage
				.localToScene(anchorage.getLayoutBounds()));
		return bounds.getCenter();
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
	public Node getVisual() {
		return visual;
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
		Edge edge = (Edge) content;

		Map<String, Object> attrs = edge.getAttrs();
		Object label = attrs.get(Attr.Key.LABEL.toString());
		if (label instanceof String) {
			visual.setLabel((String) label);
		}
		if (attrs.containsKey(ATTR_CLASS)) {
			visual.getStyleClass().add((String) attrs.get(ATTR_CLASS));
		}
		if (attrs.containsKey(ATTR_ID)) {
			visual.setId((String) attrs.get(ATTR_ID));
		}

		setAdapter(AdapterKey.get(AbstractSelectionBehavior.class),
				new FXSelectionBehavior() {
			@Override
			protected IGeometry getHostGeometry() {
				return visual.getConnection().getCurveNode()
						.getGeometry();
			}
		});
		setAdapter(AdapterKey.get(AbstractHoverBehavior.class),
				new FXHoverBehavior() {
			@Override
			protected IGeometry getFeedbackGeometry(
					Map<Object, Object> contextMap) {
				return visual.getConnection().getCurveNode()
						.getGeometry();
			}
		});
	}

}

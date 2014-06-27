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
package org.eclipse.gef4.zest.fx;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.fx.nodes.FXCurveConnection;
import org.eclipse.gef4.fx.nodes.FXGeometryNode;
import org.eclipse.gef4.fx.nodes.IFXDecoration;
import org.eclipse.gef4.fx.widgets.FXLabeledConnection;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.eclipse.gef4.zest.layout.GraphEdgeLayout;
import org.eclipse.gef4.zest.layout.GraphLayoutContext;

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

	private static final double GAP_LENGTH = 7d;
	private static final double DASH_LENGTH = 7d;
	private static final Double DOT_LENGTH = 1d;

	private Edge edge;
	private GraphEdgeLayout edgeLayout;
	private FXLabeledConnection visual = new FXLabeledConnection();

	private PropertyChangeListener layoutContextListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (ILayoutModel.LAYOUT_CONTEXT_PROPERTY.equals(evt
					.getPropertyName())) {
				GraphLayoutContext layoutContext = (GraphLayoutContext) getViewer()
						.getDomain().getAdapter(ILayoutModel.class)
						.getLayoutContext();
				if (layoutContext != null) {
					initEdgeLayout();
				}
			}
		}
	};

	public EdgeContentPart(Edge content) {
		edge = content;
		Object label = edge.getAttrs().get(Attr.Key.LABEL.toString());
		if (label instanceof String) {
			visual.setLabel((String) label);
		}
	}

	@Override
	public void activate() {
		super.activate();
		getViewer().getDomain().getAdapter(ILayoutModel.class)
				.addPropertyChangeListener(layoutContextListener);
	}

	@Override
	public void attachVisualToAnchorageVisual(IVisualPart<Node> anchorage,
			Node anchorageVisual) {
		IContentPart<Node> sourcePart = anchorage.getRoot().getViewer()
				.getContentPartMap().get(edge.getSource());
		IContentPart<Node> targetPart = anchorage.getRoot().getViewer()
				.getContentPartMap().get(edge.getTarget());

		IFXAnchor anchor = ((AbstractFXContentPart) anchorage).getAnchor(this);
		if (anchorage == sourcePart) {
			visual.getConnection().setStartAnchor(anchor);
		} else if (anchorage == targetPart) {
			visual.getConnection().setEndAnchor(anchor);
		}

		super.attachVisualToAnchorageVisual(anchorage, anchorageVisual);
	}

	@Override
	public void doRefreshVisual() {
		if (edgeLayout == null) {
			return;
		}
	}

	@Override
	public Node getVisual() {
		return visual;
	}

	protected void initEdgeLayout() {
		edgeLayout = ((GraphLayoutContext) getViewer().getDomain()
				.getAdapter(ILayoutModel.class).getLayoutContext())
				.getEdgeLayout(edge);

		// decoration
		FXCurveConnection connection = visual.getConnection();
		if (edgeLayout.isDirected()) {
			connection.setEndDecoration(new ArrowHead());
		} else {
			connection.setEndDecoration(null);
		}

		// color
		FXGeometryNode<ICurve> curveNode = connection.getCurveNode();
		if (!edgeLayout.isVisible()) {
			curveNode.setStroke(Color.TRANSPARENT);
		} else {
			curveNode.setStroke(Color.BLACK);
		}

		// dashes
		Object style = edge.getAttrs()
				.get(Graph.Attr.Key.EDGE_STYLE.toString());
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

}

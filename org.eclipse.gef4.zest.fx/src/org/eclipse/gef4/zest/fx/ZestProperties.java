/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.scene.image.Image;

import org.eclipse.gef4.fx.nodes.IFXConnectionRouter;
import org.eclipse.gef4.fx.nodes.IFXDecoration;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.ILayoutAlgorithm;

public class ZestProperties {

	public static String getCssClass(Edge edge) {
		return (String) edge.getAttrs().get(ELEMENT_CSS_CLASS);
	}

	public static String getCssClass(Node node) {
		return (String) node.getAttrs().get(ELEMENT_CSS_CLASS);
	}

	public static String getCssId(Edge edge) {
		return (String) edge.getAttrs().get(ELEMENT_CSS_ID);
	}

	public static String getCssId(Node node) {
		return (String) node.getAttrs().get(ELEMENT_CSS_ID);
	}

	public static String getEdgeCurveCssStyle(Edge edge) {
		return (String) edge.getAttrs().get(EDGE_CURVE_CSS_STYLE);
	}

	public static String getEdgeLabelCssStyle(Edge edge) {
		return (String) edge.getAttrs().get(EDGE_LABEL_CSS_STYLE);
	}

	public static Boolean getFisheye(Node node, boolean returnDefaultIfMissing) {
		Object fisheye = node.getAttrs().get(NODE_FISHEYE);
		if (fisheye instanceof Boolean) {
			return (Boolean) fisheye;
		}
		return returnDefaultIfMissing ? false : null;
	}

	public static Image getIcon(Node node) {
		return (Image) node.getAttrs().get(NODE_ICON);
	}

	public static String getLabel(Edge edge) {
		return (String) edge.getAttrs().get(ELEMENT_LABEL);
	}

	public static String getLabel(Node node) {
		return (String) node.getAttrs().get(ELEMENT_LABEL);
	}

	public static ILayoutAlgorithm getLayout(Graph graph) {
		Object layout = graph.getAttrs().get(GRAPH_LAYOUT);
		if (layout instanceof ILayoutAlgorithm) {
			return (ILayoutAlgorithm) layout;
		}
		return null;
	}

	public static Boolean getLayoutIrrelevant(Edge edge,
			boolean returnDefaultIfMissing) {
		Map<String, Object> attrs = edge.getAttrs();
		if (attrs.containsKey(ELEMENT_LAYOUT_IRRELEVANT)) {
			return (Boolean) attrs.get(ELEMENT_LAYOUT_IRRELEVANT);
		}
		return returnDefaultIfMissing ? ELEMENT_LAYOUT_IRRELEVANT_DEFAULT
				: null;
	}

	public static Boolean getLayoutIrrelevant(Node node,
			boolean returnDefaultIfMissing) {
		Map<String, Object> attrs = node.getAttrs();
		if (attrs.containsKey(ELEMENT_LAYOUT_IRRELEVANT)) {
			return (Boolean) attrs.get(ELEMENT_LAYOUT_IRRELEVANT);
		}
		return returnDefaultIfMissing ? ELEMENT_LAYOUT_IRRELEVANT_DEFAULT
				: null;
	}

	public static String getNodeLabelCssStyle(Node node) {
		return (String) node.getAttrs().get(NODE_LABEL_CSS_STYLE);
	}

	public static String getNodeRectCssStyle(Node node) {
		return (String) node.getAttrs().get(NODE_RECT_CSS_STYLE);
	}

	public static IFXConnectionRouter getRouter(Edge edge) {
		return (IFXConnectionRouter) edge.getAttrs().get(EDGE_ROUTER);
	}

	public static IFXDecoration getSourceDecoration(Edge edge) {
		return (IFXDecoration) edge.getAttrs().get(EDGE_SOURCE_DECORATION);
	}

	public static String getStyle(Edge edge) {
		return (String) edge.getAttrs().get(EDGE_STYLE);
	}

	public static IFXDecoration getTargetDecoration(Edge edge) {
		return (IFXDecoration) edge.getAttrs().get(EDGE_TARGET_DECORATION);
	}

	public static String getTooltip(Node node) {
		return (String) node.getAttrs().get(NODE_TOOLTIP);
	}

	public static String getType(Graph graph, boolean returnDefaultIfMissing) {
		Object type = graph.getAttrs().get(GRAPH_TYPE);
		if (type instanceof String) {
			String stype = (String) type;
			if (GRAPH_TYPE_VALUES.contains(stype)) {
				return stype;
			}
		}
		return returnDefaultIfMissing ? GRAPH_TYPE_DEFAULT : null;
	}

	public static void setCssClass(Edge edge, String cssClass) {
		edge.getAttrs().put(ELEMENT_CSS_CLASS, cssClass);
	}

	public static void setCssClass(Node node, String cssClass) {
		node.getAttrs().put(ELEMENT_CSS_CLASS, cssClass);
	}

	public static void setCssId(Edge edge, String cssId) {
		edge.getAttrs().put(ELEMENT_CSS_ID, cssId);
	}

	public static void setCssId(Node node, String cssId) {
		node.getAttrs().put(ELEMENT_CSS_ID, cssId);
	}

	public static void setEdgeConnCssStyle(Edge edge, String connCssStyle) {
		edge.getAttrs().put(EDGE_CURVE_CSS_STYLE, connCssStyle);
	}

	public static void setEdgeTextCssStyle(Edge edge, String textCssStyle) {
		edge.getAttrs().put(EDGE_LABEL_CSS_STYLE, textCssStyle);
	}

	public static void setFisheye(Node node, Boolean fisheye) {
		node.getAttrs().put(NODE_FISHEYE, fisheye);
	}

	public static void setIcon(Node node, Image icon) {
		node.getAttrs().put(NODE_ICON, icon);
	}

	public static void setLabel(Edge edge, String label) {
		edge.getAttrs().put(ELEMENT_LABEL, label);
	}

	public static void setLabel(Node node, String label) {
		node.getAttrs().put(ELEMENT_LABEL, label);
	}

	public static void setLayout(Graph graph, ILayoutAlgorithm algorithm) {
		graph.getAttrs().put(GRAPH_LAYOUT, algorithm);
	}

	public static void setLayoutIrrelevant(Edge edge, Boolean layoutIrrelevant) {
		edge.getAttrs().put(ELEMENT_LAYOUT_IRRELEVANT, layoutIrrelevant);
	}

	public static void setLayoutIrrelevant(Node node, Boolean layoutIrrelevant) {
		node.getAttrs().put(ELEMENT_LAYOUT_IRRELEVANT, layoutIrrelevant);
	}

	public static void setNodeRectCssStyle(Node node, String rectCssStyle) {
		node.getAttrs().put(NODE_RECT_CSS_STYLE, rectCssStyle);
	}

	public static void setNodeTextCssStyle(Node node, String textCssStyle) {
		node.getAttrs().put(NODE_LABEL_CSS_STYLE, textCssStyle);
	}

	public static void setRouter(Edge edge, IFXConnectionRouter router) {
		edge.getAttrs().put(EDGE_ROUTER, router);
	}

	public static void setSourceDecoration(Edge edge,
			IFXDecoration sourceDecoration) {
		edge.getAttrs().put(EDGE_SOURCE_DECORATION, sourceDecoration);
	}

	public static void setStyle(Edge edge, String style) {
		if (!EDGE_STYLE_VALUES.contains(style)) {
			throw new IllegalArgumentException("Cannot set edge attribute \""
					+ EDGE_STYLE + "\" to \"" + style
					+ "\"; supported values: " + EDGE_STYLE_VALUES);
		}
		edge.getAttrs().put(EDGE_STYLE, style);
	}

	public static void setTargetDecoration(Edge edge,
			IFXDecoration targetDecoration) {
		edge.getAttrs().put(EDGE_TARGET_DECORATION, targetDecoration);
	}

	public static void setTooltip(Node node, String tooltip) {
		node.getAttrs().put(NODE_TOOLTIP, tooltip);
	}

	public static void setType(Graph graph, String type) {
		if (!GRAPH_TYPE_VALUES.contains(type)) {
			throw new IllegalArgumentException("Cannot set graph attribute \""
					+ GRAPH_TYPE + "\" to \"" + type + "\"; supported values: "
					+ GRAPH_TYPE_VALUES);
		}
		graph.getAttrs().put(GRAPH_TYPE, type);
	}

	/**
	 * This layout attribute determines if an element (node/edge) is irrelevant
	 * for laying out, i.e. it should be filtered before laying out.
	 */
	public static final String ELEMENT_LAYOUT_IRRELEVANT = "layoutIrrelevant";
	public static final Boolean ELEMENT_LAYOUT_IRRELEVANT_DEFAULT = false;

	public static final String ELEMENT_CSS_CLASS = "css-class";
	public static final String ELEMENT_CSS_ID = "css-id";
	public static final String ELEMENT_LABEL = "label";

	public static final String NODE_RECT_CSS_STYLE = "node-rect-css-style";
	public static final String NODE_LABEL_CSS_STYLE = "node-label-css-style";

	public static final String EDGE_CURVE_CSS_STYLE = "edge-curve-css-style";
	public static final String EDGE_LABEL_CSS_STYLE = "edge-label-css-style";

	public static final String NODE_ICON = "icon";
	public static final String NODE_TOOLTIP = "tooltip";
	public static final String NODE_FISHEYE = "fisheye";

	public static final String EDGE_TARGET_DECORATION = "target-decoration";
	public static final String EDGE_SOURCE_DECORATION = "source-decoration";

	public static final String EDGE_ROUTER = "edge-router";

	public static final String EDGE_STYLE = "style";
	public static final String EDGE_STYLE_DASHED = "dashed";
	public static final String EDGE_STYLE_DOTTED = "dotted";
	public static final String EDGE_STYLE_SOLID = "solid";
	public static final String EDGE_STYLE_DASHDOT = "dashdot";
	public static final String EDGE_STYLE_DASHDOTDOT = "dashdotdot";
	public static final Set<String> EDGE_STYLE_VALUES = new HashSet<String>(
			Arrays.asList(EDGE_STYLE_DASHED, EDGE_STYLE_DOTTED,
					EDGE_STYLE_SOLID, EDGE_STYLE_DASHDOT, EDGE_STYLE_DASHDOTDOT));
	public static final String EDGE_STYLE_DEFAULT = EDGE_STYLE_SOLID;

	public static final String GRAPH_TYPE = "type";
	public static final String GRAPH_TYPE_DIRECTED = "directed";
	public static final String GRAPH_TYPE_UNDIRECTED = "undirected";
	public static final Set<String> GRAPH_TYPE_VALUES = new HashSet<String>(
			Arrays.asList(GRAPH_TYPE_DIRECTED, GRAPH_TYPE_UNDIRECTED));
	public static final String GRAPH_TYPE_DEFAULT = GRAPH_TYPE_UNDIRECTED;

	public static final String GRAPH_LAYOUT = "layout";

	public static final String GRAPH_IS_LAYED_OUT = "is-layed-out";

}

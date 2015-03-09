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
import java.util.Set;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;

public class ZestProperties {

	public static String getCssClass(Edge edge) {
		return (String) edge.getAttrs().get(NODE_CSS_CLASS);
	}

	public static String getCssClass(Node node) {
		return (String) node.getAttrs().get(NODE_CSS_CLASS);
	}

	public static String getCssId(Edge edge) {
		return (String) edge.getAttrs().get(EDGE_CSS_ID);
	}

	public static String getCssId(Node node) {
		return (String) node.getAttrs().get(NODE_CSS_ID);
	}

	public static String getCssStyle(Edge edge) {
		return (String) edge.getAttrs().get(EDGE_CSS_STYLE);
	}

	public static String getCssStyle(Node node) {
		return (String) node.getAttrs().get(NODE_CSS_STYLE);
	}

	public static Boolean getFisheye(Node node) {
		return (Boolean) node.getAttrs().get(NODE_FISHEYE);
	}

	public static String getIconUrl(Node node) {
		return (String) node.getAttrs().get(NODE_ICON_URL);
	}

	public static String getLabel(Edge edge) {
		return (String) edge.getAttrs().get(EDGE_LABEL);
	}

	public static String getLabel(Node node) {
		return (String) node.getAttrs().get(NODE_LABEL);
	}

	public static LayoutAlgorithm getLayout(Graph graph) {
		return (LayoutAlgorithm) graph.getAttrs().get(GRAPH_LAYOUT);
	}

	public static String getStyle(Edge edge) {
		return (String) edge.getAttrs().get(EDGE_STYLE);
	}

	public static String getTooltip(Node node) {
		return (String) node.getAttrs().get(NODE_TOOLTIP);
	}

	public static String getType(Graph graph) {
		return (String) graph.getAttrs().get(GRAPH_TYPE);
	}

	public static void setCssClass(Edge edge, String cssClass) {
		edge.getAttrs().put(EDGE_CSS_CLASS, cssClass);
	}

	public static void setCssClass(Node node, String cssClass) {
		node.getAttrs().put(NODE_CSS_CLASS, cssClass);
	}

	public static void setCssId(Edge edge, String cssId) {
		edge.getAttrs().put(EDGE_CSS_ID, cssId);
	}

	public static void setCssId(Node node, String cssId) {
		node.getAttrs().put(NODE_CSS_ID, cssId);
	}

	public static void setCssStyle(Edge edge, String cssStyle) {
		edge.getAttrs().put(EDGE_CSS_STYLE, cssStyle);
	}

	public static void setCssStyle(Node node, String cssStyle) {
		node.getAttrs().put(NODE_CSS_STYLE, cssStyle);
	}

	public static void setFisheye(Node node, Boolean fisheye) {
		node.getAttrs().put(NODE_FISHEYE, fisheye);
	}

	public static void setIconUrl(Node node, String iconUrl) {
		node.getAttrs().put(NODE_ICON_URL, iconUrl);
	}

	public static void setLabel(Edge edge, String label) {
		edge.getAttrs().put(EDGE_LABEL, label);
	}

	public static void setLabel(Node node, String label) {
		node.getAttrs().put(NODE_LABEL, label);
	}

	public static void setLayout(Graph graph, LayoutAlgorithm algorithm) {
		graph.getAttrs().put(GRAPH_LAYOUT, algorithm);
	}

	public static void setStyle(Edge edge, String style) {
		if (!EDGE_STYLE_VALUES.contains(style)) {
			throw new IllegalArgumentException("Cannot set edge attribute \""
					+ EDGE_STYLE + "\" to \"" + style
					+ "\"; supported values: " + EDGE_STYLE_VALUES);
		}
		edge.getAttrs().put(EDGE_STYLE, style);
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

	public static final String NODE_CSS_CLASS = "css-class";
	public static final String NODE_CSS_ID = "css-id";
	public static final String NODE_CSS_STYLE = "css-style";

	public static final String NODE_ICON_URL = "iconUrl";
	// TODO: public static final String NODE_ICON = "icon";

	public static final String NODE_TOOLTIP = "tooltip";

	public static final String NODE_FISHEYE = "fisheye";

	public static final String NODE_LABEL = "label";
	public static final String NODE_LABEL_DEFAULT = "-";

	public static final String EDGE_LABEL = "label";

	public static final String EDGE_CSS_CLASS = "css-class";
	public static final String EDGE_CSS_ID = "css-id";
	public static final String EDGE_CSS_STYLE = "css-style";

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
	public static final LayoutAlgorithm GRAPH_LAYOUT_DEFAULT = new TreeLayoutAlgorithm();

}

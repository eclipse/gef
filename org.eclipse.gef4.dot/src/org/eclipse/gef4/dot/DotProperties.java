/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.dot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;

public class DotProperties {

	// node id
	public static final String NODE_ID = "id";

	// node label
	public static final String NODE_LABEL = "label";

	// layout algorithm
	public static final String GRAPH_LAYOUT = "layout";
	public static final String GRAPH_LAYOUT_DOT = "dot";
	public static final String GRAPH_LAYOUT_OSAGE = "osage";
	public static final String GRAPH_LAYOUT_GRID = "grid";
	public static final String GRAPH_LAYOUT_TWOPI = "twopi";
	public static final String GRAPH_LAYOUT_CIRCO = "circo";
	public static final String GRAPH_LAYOUT_NEATO = "neato";
	public static final String GRAPH_LAYOUT_FDP = "fdp";
	public static final String GRAPH_LAYOUT_SFDP = "sfdp";
	public static final Set<String> GRAPH_LAYOUT_VALUES = new HashSet<String>(
			Arrays.asList(GRAPH_LAYOUT_DOT, GRAPH_LAYOUT_OSAGE,
					GRAPH_LAYOUT_GRID, GRAPH_LAYOUT_TWOPI, GRAPH_LAYOUT_CIRCO,
					GRAPH_LAYOUT_NEATO, GRAPH_LAYOUT_FDP, GRAPH_LAYOUT_SFDP));
	public static final String GRAPH_LAYOUT_DEFAULT = GRAPH_LAYOUT_DOT;

	// rankdir layout attribute
	public static final String GRAPH_RANKDIR = "rankdir";
	public static final String GRAPH_RANKDIR_LR = "lr";
	public static final String GRAPH_RANKDIR_TD = "td";
	public static final Set<String> GRAPH_RANKDIR_VALUES = new HashSet<String>(
			Arrays.asList(GRAPH_RANKDIR_LR, GRAPH_RANKDIR_TD));
	public static final String GRAPH_RANKDIR_DEFAULT = GRAPH_RANKDIR_TD;

	// graph type
	public static final String GRAPH_TYPE = "type";
	public static final String GRAPH_TYPE_DIRECTED = "directed";
	public static final String GRAPH_TYPE_UNDIRECTED = "undirected";
	public static final Set<String> GRAPH_TYPE_VALUES = new HashSet<String>(
			Arrays.asList(GRAPH_TYPE_DIRECTED, GRAPH_TYPE_UNDIRECTED));
	public static final String GRAPH_TYPE_DEFAULT = GRAPH_TYPE_UNDIRECTED;

	// edge style
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

	// edge label
	public static final String EDGE_LABEL = "label";

	// edge id
	public static final String EDGE_ID = "id";

	public static String getLayout(Graph graph) {
		return (String) graph.getAttrs().get(GRAPH_LAYOUT);
	}

	public static String getType(Graph graph) {
		return (String) graph.getAttrs().get(GRAPH_TYPE);
	}

	public static String getRankdir(Graph graph) {
		return (String) graph.getAttrs().get(GRAPH_RANKDIR);
	}

	public static String getLabel(Node node) {
		return (String) node.getAttrs().get(NODE_LABEL);
	}

	public static String getId(Node node) {
		return (String) node.getAttrs().get(NODE_ID);
	}

	public static String getLabel(Edge edge) {
		return (String) edge.getAttrs().get(EDGE_LABEL);
	}

	public static String getStyle(Edge edge) {
		return (String) edge.getAttrs().get(EDGE_STYLE);
	}

	public static String getId(Edge edge) {
		return (String) edge.getAttrs().get(EDGE_ID);
	}

}

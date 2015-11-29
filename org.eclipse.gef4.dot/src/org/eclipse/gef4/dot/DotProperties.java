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

/**
 * The {@link DotProperties} class contains all properties which are supported
 * by the {@link DotImport}, i.e. they are set on the resulting {@link Graph}.
 * 
 * @author mwienand
 * @author anyssen
 *
 */
public class DotProperties {

	/**
	 * Specifies the identifier of a node.
	 */
	public static final String NODE_ID = "id";

	/**
	 * Specifies the label of a node.
	 */
	public static final String NODE_LABEL = "label";

	/**
	 * Specifies the identifier of an edge.
	 */
	public static final String EDGE_ID = "id";

	/**
	 * Specifies the label of an edge.
	 */
	public static final String EDGE_LABEL = "label";

	/**
	 * Specifies the rendering style of an edge, i.e. if it is solid, dashed,
	 * dotted, etc. Possible values are defined by {@link #EDGE_STYLE_VALUES}.
	 * The default value is defined by {@link #EDGE_STYLE_DEFAULT}.
	 */
	public static final String EDGE_STYLE = "style";

	/**
	 * This {@link #EDGE_STYLE} value specifies that the edge is rendered with
	 * the void, which means the the original Dot default value is used.
	 */
	public static final String EDGE_STYLE_VOID = "";

	/**
	 * This {@link #EDGE_STYLE} value specifies that the edge is rendered
	 * dashed.
	 */
	public static final String EDGE_STYLE_DASHED = "dashed";

	/**
	 * This {@link #EDGE_STYLE} value specifies that the edge is rendered
	 * dotted.
	 */
	public static final String EDGE_STYLE_DOTTED = "dotted";

	/**
	 * This {@link #EDGE_STYLE} value specifies that the edge is rendered solid.
	 */
	public static final String EDGE_STYLE_SOLID = "solid";

	/**
	 * This {@link #EDGE_STYLE} value specifies that the edge is rendered
	 * invisible.
	 */
	public static final String EDGE_STYLE_INVIS = "invis";

	/**
	 * This {@link #EDGE_STYLE} value specifies that the edge is rendered bold.
	 */
	public static final String EDGE_STYLE_BOLD = "bold";

	/**
	 * This {@link #EDGE_STYLE} value specifies that the edge is rendered
	 * tapered.
	 */
	public static final String EDGE_STYLE_TAPERED = "tapered";

	/**
	 * Defines all possible values for the {@link #EDGE_STYLE} property.
	 */
	public static final Set<String> EDGE_STYLE_VALUES = new HashSet<>(
			Arrays.asList(EDGE_STYLE_DASHED, EDGE_STYLE_DOTTED,
					EDGE_STYLE_SOLID, EDGE_STYLE_INVIS, EDGE_STYLE_BOLD,
					EDGE_STYLE_TAPERED, EDGE_STYLE_VOID));

	/**
	 * Defines the default value for the {@link #EDGE_STYLE} property, which is
	 * {@link #EDGE_STYLE_SOLID}.
	 */
	public static final String EDGE_STYLE_DEFAULT = EDGE_STYLE_SOLID;

	/**
	 * Specifies the graph type. Possible values are defined by
	 * {@link #GRAPH_TYPE_VALUES}. The default value is defined by
	 * {@link #GRAPH_TYPE_DEFAULT}.
	 */
	public static final String GRAPH_TYPE = "type";

	/**
	 * This {@link #GRAPH_TYPE} value specifies that the edges within the graph
	 * are directed.
	 */
	public static final String GRAPH_TYPE_DIRECTED = "directed";

	/**
	 * This {@link #GRAPH_TYPE} value specifies that the edges within the graph
	 * are undirected.
	 */
	public static final String GRAPH_TYPE_UNDIRECTED = "undirected";

	/**
	 * Defines all possible values for the {@link #GRAPH_TYPE} property.
	 */
	public static final Set<String> GRAPH_TYPE_VALUES = new HashSet<>(
			Arrays.asList(GRAPH_TYPE_DIRECTED, GRAPH_TYPE_UNDIRECTED));

	/**
	 * Defines the default value for {@link #GRAPH_TYPE}, which is
	 * {@link #GRAPH_TYPE_UNDIRECTED}.
	 */
	public static final String GRAPH_TYPE_DEFAULT = GRAPH_TYPE_UNDIRECTED;

	/**
	 * Specifies the layout algorithm which shall be used to layout the graph.
	 * Possible values are defined by {@link #GRAPH_LAYOUT_VALUES}. The default
	 * value is defined by {@link #GRAPH_LAYOUT_DEFAULT}.
	 */
	public static final String GRAPH_LAYOUT = "layout";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "dot" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_DOT = "dot";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "osage" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_OSAGE = "osage";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "grid" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_GRID = "grid";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "twopi" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_TWOPI = "twopi";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "circo" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_CIRCO = "circo";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "neato" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_NEATO = "neato";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "fdp" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_FDP = "fdp";

	/**
	 * This {@link #GRAPH_LAYOUT} value specifies that the "sfdp" layout
	 * algorithm is to be used for laying out the graph.
	 */
	public static final String GRAPH_LAYOUT_SFDP = "sfdp";

	/**
	 * Defines all possible values for the {@link #GRAPH_LAYOUT} property.
	 */
	public static final Set<String> GRAPH_LAYOUT_VALUES = new HashSet<>(
			Arrays.asList(GRAPH_LAYOUT_DOT, GRAPH_LAYOUT_OSAGE,
					GRAPH_LAYOUT_GRID, GRAPH_LAYOUT_TWOPI, GRAPH_LAYOUT_CIRCO,
					GRAPH_LAYOUT_NEATO, GRAPH_LAYOUT_FDP, GRAPH_LAYOUT_SFDP));

	/**
	 * Defines the default value for the {@link #GRAPH_LAYOUT} property, which
	 * is {@link #GRAPH_LAYOUT_DOT}.
	 */
	public static final String GRAPH_LAYOUT_DEFAULT = GRAPH_LAYOUT_DOT;

	/**
	 * Specifies the rankdir property which is passed to the layout algorithm
	 * which is used for laying out the graph. Possible values are defined by
	 * {@link #GRAPH_RANKDIR_VALUES}. The default value is defined by
	 * {@link #GRAPH_RANKDIR_DEFAULT}.
	 */
	public static final String GRAPH_RANKDIR = "rankdir";

	/**
	 * This {@link #GRAPH_RANKDIR} value specifies that the graph is to be laid
	 * out horizontally from left to right.
	 */
	public static final String GRAPH_RANKDIR_LR = "lr";

	/**
	 * This {@link #GRAPH_RANKDIR} value specifies that the graph is to be laid
	 * out vertically from top to bottom.
	 */
	public static final String GRAPH_RANKDIR_TD = "td";

	/**
	 * Defines all possible values for the {@link #GRAPH_RANKDIR} property.
	 */
	public static final Set<String> GRAPH_RANKDIR_VALUES = new HashSet<>(
			Arrays.asList(GRAPH_RANKDIR_LR, GRAPH_RANKDIR_TD));

	/**
	 * Defines the default value for the {@link #GRAPH_RANKDIR} property.
	 */
	public static final String GRAPH_RANKDIR_DEFAULT = GRAPH_RANKDIR_TD;

	/**
	 * Returns the value of the {@link #GRAPH_LAYOUT} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #GRAPH_LAYOUT} property.
	 * @return The value of the {@link #GRAPH_LAYOUT} property of the given
	 *         {@link Graph}.
	 */
	public static String getLayout(Graph graph) {
		return (String) graph.getAttrs().get(GRAPH_LAYOUT);
	}

	/**
	 * Sets the {@link #GRAPH_LAYOUT} property of the given {@link Graph} to the
	 * given <i>layout</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #GRAPH_LAYOUT} property.
	 * @param layout
	 *            The new value for the {@link #GRAPH_LAYOUT} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>layout</i> value is not supported, i.e. not
	 *             contained within {@link #GRAPH_LAYOUT_VALUES}.
	 */
	public static void setLayout(Graph graph, String layout) {
		if (!GRAPH_LAYOUT_VALUES.contains(layout)) {
			throw new IllegalArgumentException(
					"Cannot set graph attribute \"layout\" to \"" + layout
							+ "\"; supported values: " + GRAPH_LAYOUT_VALUES);
		}
		graph.getAttrs().put(GRAPH_LAYOUT, layout);
	}

	/**
	 * Returns the value of the {@link #GRAPH_TYPE} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #GRAPH_TYPE} property.
	 * @return The value of the {@link #GRAPH_TYPE} property of the given
	 *         {@link Graph}.
	 */
	public static String getType(Graph graph) {
		return (String) graph.getAttrs().get(GRAPH_TYPE);
	}

	/**
	 * Sets the {@link #GRAPH_TYPE} property of the given {@link Graph} to the
	 * given <i>type</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #GRAPH_TYPE} property.
	 * @param type
	 *            The new value for the {@link #GRAPH_TYPE} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>type</i> value is not supported, i.e. not
	 *             contained within {@link #GRAPH_TYPE_VALUES}.
	 */
	public static void setType(Graph graph, String type) {
		if (!GRAPH_TYPE_VALUES.contains(type)) {
			throw new IllegalArgumentException(
					"Cannot set graph attribute \"type\" to \"" + type
							+ "\"; supported values: " + GRAPH_TYPE_VALUES);
		}
		graph.getAttrs().put(GRAPH_TYPE, type);
	}

	/**
	 * Returns the value of the {@link #GRAPH_RANKDIR} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #GRAPH_RANKDIR} property.
	 * @return The value of the {@link #GRAPH_RANKDIR} property of the given
	 *         {@link Graph}.
	 */
	public static String getRankdir(Graph graph) {
		return (String) graph.getAttrs().get(GRAPH_RANKDIR);
	}

	/**
	 * Sets the {@link #GRAPH_RANKDIR} property of the given {@link Graph} to
	 * the given <i>rankdir</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #GRAPH_RANKDIR} property.
	 * @param rankdir
	 *            The new value for the {@link #GRAPH_RANKDIR} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>rankdir</i> value is not supported, i.e.
	 *             not contained within {@link #GRAPH_RANKDIR_VALUES}.
	 */
	public static void setRankdir(Graph graph, String rankdir) {
		if (!GRAPH_RANKDIR_VALUES.contains(rankdir)) {
			throw new IllegalArgumentException(
					"Cannot set graph attribute \"rankdir\" to \"" + rankdir
							+ "\"; supported values: " + GRAPH_RANKDIR_VALUES);
		}
		graph.getAttrs().put(GRAPH_RANKDIR, rankdir);
	}

	/**
	 * Returns the value of the {@link #NODE_LABEL} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #NODE_LABEL} property.
	 * @return The value of the {@link #NODE_LABEL} property of the given
	 *         {@link Node}.
	 */
	public static String getLabel(Node node) {
		return (String) node.getAttrs().get(NODE_LABEL);
	}

	/**
	 * Sets the {@link #NODE_LABEL} property of the given {@link Node} to the
	 * given <i>label</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #NODE_LABEL} property.
	 * @param label
	 *            The new value for the {@link #NODE_LABEL} property.
	 */
	public static void setLabel(Node node, String label) {
		node.getAttrs().put(NODE_LABEL, label);
	}

	/**
	 * Returns the value of the {@link #NODE_ID} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #NODE_ID} property.
	 * @return The value of the {@link #NODE_ID} property of the given
	 *         {@link Node}.
	 */
	public static String getId(Node node) {
		return (String) node.getAttrs().get(NODE_ID);
	}

	/**
	 * Sets the {@link #NODE_ID} property of the given {@link Node} to the given
	 * <i>id</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #NODE_ID} property.
	 * @param id
	 *            The new value for the {@link #NODE_ID} property.
	 */
	public static void setId(Node node, String id) {
		node.getAttrs().put(NODE_ID, id);
	}

	/**
	 * Returns the value of the {@link #EDGE_LABEL} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_LABEL} property.
	 * @return The value of the {@link #EDGE_LABEL} property of the given
	 *         {@link Edge}.
	 */
	public static String getLabel(Edge edge) {
		return (String) edge.getAttrs().get(EDGE_LABEL);
	}

	/**
	 * Sets the {@link #EDGE_LABEL} property of the given {@link Edge} to the
	 * given <i>label</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #EDGE_LABEL} property.
	 * @param label
	 *            The new value for the {@link #EDGE_LABEL} property.
	 */
	public static void setLabel(Edge edge, String label) {
		edge.getAttrs().put(EDGE_LABEL, label);
	}

	/**
	 * Returns the value of the {@link #EDGE_STYLE} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_STYLE} property.
	 * @return The value of the {@link #EDGE_STYLE} property of the given
	 *         {@link Edge}.
	 */
	public static String getStyle(Edge edge) {
		return (String) edge.getAttrs().get(EDGE_STYLE);
	}

	/**
	 * Sets the {@link #EDGE_STYLE} property of the given {@link Edge} to the
	 * given <i>style</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #EDGE_STYLE} property.
	 * @param style
	 *            The new value for the {@link #EDGE_STYLE} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>style</i> value is not supported, i.e. not
	 *             contained within {@link #EDGE_STYLE_VALUES}.
	 */
	public static void setStyle(Edge edge, String style) {
		if (!EDGE_STYLE_VALUES.contains(style)) {
			throw new IllegalArgumentException(
					"Cannot set edge attribute \"style\" to \"" + style
							+ "\"; supported values: " + EDGE_STYLE_VALUES);
		}
		edge.getAttrs().put(EDGE_STYLE, style);
	}

	/**
	 * Returns the value of the {@link #EDGE_ID} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #EDGE_ID} property.
	 * @return The value of the {@link #EDGE_ID} property of the given
	 *         {@link Edge}.
	 */
	public static String getId(Edge edge) {
		return (String) edge.getAttrs().get(EDGE_ID);
	}

	/**
	 * Sets the {@link #EDGE_ID} property of the given {@link Edge} to the given
	 * <i>id</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #EDGE_ID} property.
	 * @param id
	 *            The new value for the {@link #EDGE_ID} property.
	 */
	public static void setId(Edge edge, String id) {
		edge.getAttrs().put(EDGE_ID, id);
	}

}

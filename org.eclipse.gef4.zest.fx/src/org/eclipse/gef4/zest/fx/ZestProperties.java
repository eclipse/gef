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

import java.awt.Point;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.fx.nodes.IConnectionRouter;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.ILayoutAlgorithm;

import javafx.scene.image.Image;

/**
 * The {@link ZestProperties} class contains a collection of attributes that are
 * evaluated by Zest.FX and their default values. It does also provide utility
 * methods to read and write these attributes.
 *
 * @author mwienand
 *
 */
public class ZestProperties {

	/**
	 * This attribute determines if an element (node/edge) is irrelevant for
	 * laying out, i.e. it should be filtered before laying out.
	 *
	 * @see #ELEMENT_LAYOUT_IRRELEVANT_DEFAULT
	 * @see #getLayoutIrrelevant(Edge, boolean)
	 * @see #getLayoutIrrelevant(Node, boolean)
	 * @see #setLayoutIrrelevant(Edge, Boolean)
	 * @see #setLayoutIrrelevant(Node, Boolean)
	 */
	public static final String ELEMENT_LAYOUT_IRRELEVANT = "layoutIrrelevant";

	/**
	 * The default value for the {@link #ELEMENT_LAYOUT_IRRELEVANT} attribute.
	 */
	public static final Boolean ELEMENT_LAYOUT_IRRELEVANT_DEFAULT = false;

	/**
	 * This attribute determines the CSS class for an element (node/edge). This
	 * attribute does not have a default value.
	 *
	 * @see #getCssClass(Edge)
	 * @see #getCssClass(Node)
	 * @see #setCssClass(Edge, String)
	 * @see #setCssClass(Node, String)
	 */
	public static final String ELEMENT_CSS_CLASS = "css-class";

	/**
	 * This attribute determines the CSS id for an element (node/edge). This
	 * attribute does not have a default value.
	 *
	 * @see #getCssId(Edge)
	 * @see #getCssId(Node)
	 * @see #setCssId(Edge, String)
	 * @see #setCssId(Node, String)
	 */
	public static final String ELEMENT_CSS_ID = "css-id";

	/**
	 * This attribute determines the label for an element (node/edge). This
	 * attribute does not have a default value.
	 *
	 * @see #getLabel(Edge)
	 * @see #getLabel(Node)
	 * @see #setLabel(Edge, String)
	 * @see #setLabel(Node, String)
	 */
	public static final String ELEMENT_LABEL = "label";

	/**
	 * This attribute determines the CSS style for an edge. This attribute does
	 * not have a default value.
	 *
	 * @see #getEdgeCurveCssStyle(Edge)
	 * @see #setEdgeConnCssStyle(Edge, String)
	 */
	public static final String EDGE_CURVE_CSS_STYLE = "edge-curve-css-style";

	/**
	 * This attribute determines the CSS style for an edge label. This attribute
	 * does not have a default value.
	 *
	 * @see #getEdgeLabelCssStyle(Edge)
	 * @see #setEdgeTextCssStyle(Edge, String)
	 */
	public static final String EDGE_LABEL_CSS_STYLE = "edge-label-css-style";

	/**
	 * This attribute determines the way points for the {@link #EDGE_ROUTER}.
	 *
	 * @see #getRouterPoints(Edge)
	 * @see #setRouterPoints(Edge, List)
	 */
	public static final String EDGE_ROUTER_POINTS = "edge-router-points";

	/**
	 * This attribute determines the CSS style for a node rectangle. This
	 * attribute does not have a default value.
	 *
	 * @see #getNodeRectCssStyle(Node)
	 * @see #setNodeRectCssStyle(Node, String)
	 */
	public static final String NODE_RECT_CSS_STYLE = "node-rect-css-style";

	/**
	 * This attribute determines the CSS style for a node label. This attribute
	 * does not have a default value.
	 *
	 * @see #getNodeLabelCssStyle(Node)
	 * @see #setNodeTextCssStyle(Node, String)
	 */
	public static final String NODE_LABEL_CSS_STYLE = "node-label-css-style";

	/**
	 * This attribute determines the icon for a node. This attribute does not
	 * have a default value.
	 *
	 * @see #getIcon(Node)
	 * @see #setIcon(Node, Image)
	 */
	public static final String NODE_ICON = "icon";

	/**
	 * This attribute determines the bounds for a {@link Node}, i.e. its
	 * position and size.
	 *
	 * @see #getBounds(Node)
	 * @see #setBounds(Node, Rectangle)
	 */
	public static final String NODE_BOUNDS = "bounds";

	/**
	 * This attribute determines the tooltip for a node. This attribute does not
	 * have a default value.
	 *
	 * @see #getTooltip(Node)
	 * @see #setTooltip(Node, String)
	 */
	public static final String NODE_TOOLTIP = "tooltip";

	/**
	 * This attribute determines the fisheye state for a node.
	 *
	 * @see #NODE_FISHEYE_DEFAULT
	 * @see #getFisheye(Node, boolean)
	 * @see #setFisheye(Node, Boolean)
	 */
	public static final String NODE_FISHEYE = "fisheye";

	/**
	 * This attribute determines the target decoration for an edge. This
	 * attribute does not have a default value.
	 *
	 * @see #getTargetDecoration(Edge)
	 * @see #setTargetDecoration(Edge, javafx.scene.shape.Shape)
	 */
	public static final String EDGE_TARGET_DECORATION = "target-decoration";

	/**
	 * This attribute determines the source decoration for an edge. This
	 * attribute does not have a default value.
	 *
	 * @see #getSourceDecoration(Edge)
	 * @see #setSourceDecoration(Edge, javafx.scene.shape.Shape)
	 */
	public static final String EDGE_SOURCE_DECORATION = "source-decoration";

	/**
	 * This attribute determines the {@link IConnectionRouter} used to route an
	 * edge. This attribute does not have a default value.
	 *
	 * @see #getRouter(Edge)
	 * @see #setRouter(Edge, IConnectionRouter)
	 */
	public static final String EDGE_ROUTER = "edge-router";

	/**
	 * This attribute determines if a graph is directed or undirected.
	 *
	 * @see #GRAPH_TYPE_VALUES
	 * @see #GRAPH_TYPE_DEFAULT
	 * @see #getType(Graph, boolean)
	 * @see #setType(Graph, String)
	 */
	public static final String GRAPH_TYPE = "type";

	/**
	 * This {@link #GRAPH_TYPE} specifies that the edges of the graph are
	 * directed.
	 */
	public static final String GRAPH_TYPE_DIRECTED = "directed";

	/**
	 * This {@link #GRAPH_TYPE} specifies that the edges of the graph are
	 * undirected.
	 */
	public static final String GRAPH_TYPE_UNDIRECTED = "undirected";

	/**
	 * These are the possible {@link #GRAPH_TYPE} values:
	 * <ul>
	 * <li>{@link #GRAPH_TYPE_DIRECTED}
	 * <li>{@link #GRAPH_TYPE_UNDIRECTED} (default)
	 * </ul>
	 */
	public static final Set<String> GRAPH_TYPE_VALUES = new HashSet<>(
			Arrays.asList(GRAPH_TYPE_DIRECTED, GRAPH_TYPE_UNDIRECTED));

	/**
	 * The default value for the {@link #GRAPH_TYPE} attribute.
	 */
	public static final String GRAPH_TYPE_DEFAULT = GRAPH_TYPE_UNDIRECTED;

	/**
	 * This attribute determines the {@link ILayoutAlgorithm} used to layout the
	 * graph.
	 *
	 * @see #getLayout(Graph)
	 * @see #setLayout(Graph, ILayoutAlgorithm)
	 */
	public static final String GRAPH_LAYOUT_ALGORITHM = "layout";

	/**
	 * The default value of the {@link #NODE_FISHEYE} attribute.
	 */
	public static Boolean NODE_FISHEYE_DEFAULT = false;

	/**
	 * Returns the value of the {@link #NODE_BOUNDS} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} for which to return the {@link #NODE_BOUNDS}.
	 * @return The value of the {@link #NODE_BOUNDS} attribute of the given
	 *         {@link Node}.
	 */
	public static Rectangle getBounds(Node node) {
		Object bounds = node.getAttributes().get(NODE_BOUNDS);
		if (bounds instanceof Rectangle) {
			return (Rectangle) bounds;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #ELEMENT_CSS_CLASS} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the CSS class is determined.
	 * @return The CSS class of the given {@link Edge}.
	 */
	// TODO: Return null if not present.
	public static String getCssClass(Edge edge) {
		return (String) edge.attributesProperty().get(ELEMENT_CSS_CLASS);
	}

	/**
	 * Returns the value of the {@link #ELEMENT_CSS_CLASS} attribute of the
	 * given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the CSS class is determined.
	 * @return The CSS class of the given {@link Node}.
	 */
	// TODO: Return null if not present.
	public static String getCssClass(Node node) {
		return (String) node.attributesProperty().get(ELEMENT_CSS_CLASS);
	}

	/**
	 * Returns the value of the {@link #ELEMENT_CSS_ID} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the CSS id is determined.
	 * @return The CSS id of the given {@link Edge}.
	 */
	// TODO: Return null if not present.
	public static String getCssId(Edge edge) {
		return (String) edge.attributesProperty().get(ELEMENT_CSS_ID);
	}

	/**
	 * Returns the value of the {@link #ELEMENT_CSS_ID} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the CSS id is determined.
	 * @return The CSS id of the given {@link Node}.
	 */
	// TODO: Return null if not present.
	public static String getCssId(Node node) {
		return (String) node.attributesProperty().get(ELEMENT_CSS_ID);
	}

	/**
	 * Returns the value of the {@link #EDGE_CURVE_CSS_STYLE} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the curve CSS style is determined.
	 * @return The curve CSS style of the given {@link Edge}.
	 */
	// TODO: Return null if not present.
	public static String getEdgeCurveCssStyle(Edge edge) {
		return (String) edge.attributesProperty().get(EDGE_CURVE_CSS_STYLE);
	}

	/**
	 * Returns the value of the {@link #EDGE_LABEL_CSS_STYLE} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is determined.
	 * @return The label CSS style of the given {@link Edge}.
	 */
	// TODO: Return null if not present.
	public static String getEdgeLabelCssStyle(Edge edge) {
		return (String) edge.attributesProperty().get(EDGE_LABEL_CSS_STYLE);
	}

	/**
	 * Returns the value of the {@link #NODE_FISHEYE} attribute of the given
	 * {@link Node}. If the attribute is not set for the given {@link Node},
	 * either the default attribute value is returned, or <code>null</code>,
	 * depending on the <i>returnDefaultIfMissing</i> flag.
	 *
	 * @param node
	 *            The {@link Node} of which the fisheye state is determined.
	 * @param returnDefaultIfMissing
	 *            <code>true</code> to indicate that the default attribute value
	 *            should be returned if the attribute is not set for the given
	 *            {@link Node}, otherwise <code>false</code>.
	 * @return The fisheye state of the given {@link Node}.
	 */
	public static Boolean getFisheye(Node node, boolean returnDefaultIfMissing) {
		Object fisheye = node.attributesProperty().get(NODE_FISHEYE);
		if (fisheye instanceof Boolean) {
			return (Boolean) fisheye;
		}
		return returnDefaultIfMissing ? NODE_FISHEYE_DEFAULT : null;
	}

	/**
	 * Returns the value of the {@link #NODE_ICON} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the icon is determined.
	 * @return The icon of the given {@link Node}.
	 */
	// TODO: Return null if not present.
	public static Image getIcon(Node node) {
		return (Image) node.attributesProperty().get(NODE_ICON);
	}

	/**
	 * Returns the value of the {@link #ELEMENT_LABEL} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label is determined.
	 * @return The label of the given {@link Edge}.
	 */
	// TODO: Return null if not present.
	public static String getLabel(Edge edge) {
		return (String) edge.attributesProperty().get(ELEMENT_LABEL);
	}

	/**
	 * Returns the value of the {@link #ELEMENT_LABEL} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the label is determined.
	 * @return The label of the given {@link Node}.
	 */
	// TODO: Return null if not present.
	public static String getLabel(Node node) {
		return (String) node.attributesProperty().get(ELEMENT_LABEL);
	}

	/**
	 * Returns the value of the {@link #GRAPH_LAYOUT_ALGORITHM} attribute of the
	 * given {@link Graph}.
	 *
	 * @param graph
	 *            The {@link Graph} of which the layout algorithm is determined.
	 * @return The layout algorithm of the given {@link Graph}.
	 */
	public static ILayoutAlgorithm getLayout(Graph graph) {
		Object layout = graph.attributesProperty().get(GRAPH_LAYOUT_ALGORITHM);
		if (layout instanceof ILayoutAlgorithm) {
			return (ILayoutAlgorithm) layout;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #ELEMENT_LAYOUT_IRRELEVANT} attribute of
	 * the given {@link Edge}. If the attribute is not set for the given
	 * {@link Edge}, either the default attribute value is returned, or
	 * <code>null</code>, depending on the <i>returnDefaultIfMissing</i> flag.
	 *
	 * @param edge
	 *            The {@link Edge} of which the layout irrelevant flag is
	 *            determined.
	 * @param returnDefaultIfMissing
	 *            <code>true</code> to indicate that the default attribute value
	 *            should be returned if the attribute is not set for the given
	 *            {@link Edge}, otherwise <code>false</code>.
	 * @return The layout irrelevant flag of the given {@link Edge}.
	 */
	public static Boolean getLayoutIrrelevant(Edge edge, boolean returnDefaultIfMissing) {
		Map<String, Object> attrs = edge.attributesProperty();
		if (attrs.containsKey(ELEMENT_LAYOUT_IRRELEVANT)) {
			return (Boolean) attrs.get(ELEMENT_LAYOUT_IRRELEVANT);
		}
		return returnDefaultIfMissing ? ELEMENT_LAYOUT_IRRELEVANT_DEFAULT : null;
	}

	/**
	 * Returns the value of the {@link #ELEMENT_LAYOUT_IRRELEVANT} attribute of
	 * the given {@link Node}. If the attribute is not set for the given
	 * {@link Node}, either the default attribute value is returned, or
	 * <code>null</code>, depending on the <i>returnDefaultIfMissing</i> flag.
	 *
	 * @param node
	 *            The {@link Edge} of which the layout irrelevant flag is
	 *            determined.
	 * @param returnDefaultIfMissing
	 *            <code>true</code> to indicate that the default attribute value
	 *            should be returned if the attribute is not set for the given
	 *            {@link Node}, otherwise <code>false</code>.
	 * @return The layout irrelevant flag of the given {@link Node}.
	 */
	public static Boolean getLayoutIrrelevant(Node node, boolean returnDefaultIfMissing) {
		Map<String, Object> attrs = node.attributesProperty();
		if (attrs.containsKey(ELEMENT_LAYOUT_IRRELEVANT)) {
			return (Boolean) attrs.get(ELEMENT_LAYOUT_IRRELEVANT);
		}
		return returnDefaultIfMissing ? ELEMENT_LAYOUT_IRRELEVANT_DEFAULT : null;
	}

	/**
	 * Returns the value of the {@link #NODE_LABEL_CSS_STYLE} attribute of the
	 * given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the label CSS style is determined.
	 * @return The label CSS style of the given {@link Node}.
	 */
	// TODO: Return null if not present.
	public static String getNodeLabelCssStyle(Node node) {
		return (String) node.attributesProperty().get(NODE_LABEL_CSS_STYLE);
	}

	/**
	 * Returns the value of the {@link #NODE_RECT_CSS_STYLE} attribute of the
	 * given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the node rectangle CSS style is
	 *            determined.
	 * @return The node rectangle CSS style of the given {@link Node}.
	 */
	// TODO: Return null if not present.
	public static String getNodeRectCssStyle(Node node) {
		return (String) node.attributesProperty().get(NODE_RECT_CSS_STYLE);
	}

	/**
	 * Returns the value of the {@link #EDGE_ROUTER} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the router is determined.
	 * @return The router of the given {@link Edge}.
	 */
	// TODO: Return null if not present.
	public static IConnectionRouter getRouter(Edge edge) {
		return (IConnectionRouter) edge.attributesProperty().get(EDGE_ROUTER);
	}

	/**
	 * Returns the value of the {@link #EDGE_ROUTER_POINTS} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} for which to determine the router points.
	 * @return The value of the {@link #EDGE_ROUTER_POINTS} attribute of the
	 *         given {@link Edge}.
	 */
	@SuppressWarnings("unchecked")
	public static List<Point> getRouterPoints(Edge edge) {
		Object routerPoints = edge.getAttributes().get(EDGE_ROUTER_POINTS);
		if (routerPoints instanceof List) {
			return (List<Point>) routerPoints;
		}
		return Collections.emptyList();
	}

	/**
	 * Returns the value of the {@link #EDGE_SOURCE_DECORATION} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source decoration is determined.
	 * @return The source decoration of the given {@link Edge}.
	 */
	public static javafx.scene.shape.Shape getSourceDecoration(Edge edge) {
		return (javafx.scene.shape.Shape) edge.attributesProperty().get(EDGE_SOURCE_DECORATION);
	}

	/**
	 * Returns the value of the {@link #EDGE_TARGET_DECORATION} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is determined.
	 * @return The target decoration of the given {@link Edge}.
	 */
	// TODO: Return null if not present.
	public static javafx.scene.shape.Shape getTargetDecoration(Edge edge) {
		return (javafx.scene.shape.Shape) edge.attributesProperty().get(EDGE_TARGET_DECORATION);
	}

	/**
	 * Returns the value of the {@link #NODE_TOOLTIP} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the tooltip is determined.
	 * @return The tooltip of the given {@link Node}.
	 */
	// TODO: Return null if not present.
	public static String getTooltip(Node node) {
		return (String) node.attributesProperty().get(NODE_TOOLTIP);
	}

	/**
	 * Returns the value of the {@link #GRAPH_TYPE} attribute of the given
	 * {@link Graph}. If the attribute is not set for the given {@link Graph},
	 * either the default attribute value is returned, or <code>null</code>,
	 * depending on the <i>returnDefaultIfMissing</i> flag.
	 *
	 * @param graph
	 *            The {@link Graph} of which the graph type is determined.
	 * @param returnDefaultIfMissing
	 *            <code>true</code> to indicate that the default attribute value
	 *            should be returned if the attribute is not set for the given
	 *            {@link Graph}, otherwise <code>false</code>.
	 * @return The graph type of the given {@link Graph}.
	 */
	public static String getType(Graph graph, boolean returnDefaultIfMissing) {
		Object type = graph.attributesProperty().get(GRAPH_TYPE);
		if (type instanceof String) {
			String stype = (String) type;
			if (GRAPH_TYPE_VALUES.contains(stype)) {
				return stype;
			}
		}
		return returnDefaultIfMissing ? GRAPH_TYPE_DEFAULT : null;
	}

	/**
	 * Sets the value of the {@link #NODE_BOUNDS} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} for which to return the {@link #NODE_BOUNDS}.
	 * @param bounds
	 *            The {@link Rectangle} describing the new bounds for the given
	 *            {@link Node}.
	 */
	public static void setBounds(Node node, Rectangle bounds) {
		node.getAttributes().put(NODE_BOUNDS, bounds);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_CSS_CLASS} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the CSS class is changed.
	 * @param cssClass
	 *            The new CSS class for the given {@link Edge}.
	 */
	public static void setCssClass(Edge edge, String cssClass) {
		edge.attributesProperty().put(ELEMENT_CSS_CLASS, cssClass);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_CSS_CLASS} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the CSS class is changed.
	 * @param cssClass
	 *            The new CSS class for the given {@link Node}.
	 */
	public static void setCssClass(Node node, String cssClass) {
		node.attributesProperty().put(ELEMENT_CSS_CLASS, cssClass);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_CSS_ID} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the CSS id is changed.
	 * @param cssId
	 *            The new CSS id for the given {@link Edge}.
	 */
	public static void setCssId(Edge edge, String cssId) {
		edge.attributesProperty().put(ELEMENT_CSS_ID, cssId);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_CSS_ID} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the CSS id is changed.
	 * @param cssId
	 *            The new CSS id for the given {@link Node}.
	 */
	public static void setCssId(Node node, String cssId) {
		node.attributesProperty().put(ELEMENT_CSS_ID, cssId);
	}

	/**
	 * Sets the value of the {@link #EDGE_CURVE_CSS_STYLE} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the curve CSS style is changed.
	 * @param connCssStyle
	 *            The new curve CSS style for the given {@link Edge}.
	 */
	// TODO: Rename to setEdgeCurveCssStyle
	public static void setEdgeConnCssStyle(Edge edge, String connCssStyle) {
		edge.attributesProperty().put(EDGE_CURVE_CSS_STYLE, connCssStyle);
	}

	/**
	 * Sets the value of the {@link #EDGE_LABEL_CSS_STYLE} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is changed.
	 * @param textCssStyle
	 *            The new label CSS style for the given {@link Edge}.
	 */
	// TODO: Rename to setEdgeLabelCssStyle
	public static void setEdgeTextCssStyle(Edge edge, String textCssStyle) {
		edge.attributesProperty().put(EDGE_LABEL_CSS_STYLE, textCssStyle);
	}

	/**
	 * Sets the value of the {@link #NODE_FISHEYE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the fisheye state is changed.
	 * @param fisheye
	 *            The new fisheye state for the given {@link Node}.
	 */
	public static void setFisheye(Node node, Boolean fisheye) {
		node.attributesProperty().put(NODE_FISHEYE, fisheye);
	}

	/**
	 * Sets the value of the {@link #NODE_ICON} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the icon is changed.
	 * @param icon
	 *            The new {@link Image} for the given {@link Node}.
	 */
	public static void setIcon(Node node, Image icon) {
		node.attributesProperty().put(NODE_ICON, icon);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_LABEL} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label is changed.
	 * @param label
	 *            The new label for the given {@link Edge}.
	 */
	public static void setLabel(Edge edge, String label) {
		edge.attributesProperty().put(ELEMENT_LABEL, label);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_LABEL} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the label is changed.
	 * @param label
	 *            The new label for the given {@link Node}.
	 */
	public static void setLabel(Node node, String label) {
		node.attributesProperty().put(ELEMENT_LABEL, label);
	}

	/**
	 * Sets the value of the {@link #GRAPH_TYPE} attribute of the given
	 * {@link Graph} to the given value.
	 *
	 * @param graph
	 *            The {@link Graph} of which the layout algorithm is changed.
	 * @param algorithm
	 *            The new {@link ILayoutAlgorithm} for the given {@link Graph}.
	 */
	public static void setLayout(Graph graph, ILayoutAlgorithm algorithm) {
		graph.attributesProperty().put(GRAPH_LAYOUT_ALGORITHM, algorithm);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_LAYOUT_IRRELEVANT} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the layout irrelevant flag is
	 *            changed.
	 * @param layoutIrrelevant
	 *            The new layout irrelevant flag for the given {@link Edge}.
	 */
	public static void setLayoutIrrelevant(Edge edge, Boolean layoutIrrelevant) {
		edge.attributesProperty().put(ELEMENT_LAYOUT_IRRELEVANT, layoutIrrelevant);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_LAYOUT_IRRELEVANT} attribute of the
	 * given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the layout irrelevant flag is
	 *            changed.
	 * @param layoutIrrelevant
	 *            The new layout irrelevant flag for the given {@link Node}.
	 */
	public static void setLayoutIrrelevant(Node node, Boolean layoutIrrelevant) {
		node.attributesProperty().put(ELEMENT_LAYOUT_IRRELEVANT, layoutIrrelevant);
	}

	/**
	 * Sets the value of the {@link #NODE_RECT_CSS_STYLE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the node rectangle CSS style is
	 *            changed.
	 * @param rectCssStyle
	 *            The new node rectangle CSS style for the given {@link Node}.
	 */
	public static void setNodeRectCssStyle(Node node, String rectCssStyle) {
		node.attributesProperty().put(NODE_RECT_CSS_STYLE, rectCssStyle);
	}

	/**
	 * Sets the value of the {@link #NODE_LABEL_CSS_STYLE} attribute of the
	 * given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the node label CSS style is changed.
	 * @param textCssStyle
	 *            The new node label CSS style for the given {@link Node}.
	 */
	// TODO: Rename to setNodeLabelCssStyle
	public static void setNodeTextCssStyle(Node node, String textCssStyle) {
		node.attributesProperty().put(NODE_LABEL_CSS_STYLE, textCssStyle);
	}

	/**
	 * Sets the value of the {@link #EDGE_ROUTER} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the router is changed.
	 * @param router
	 *            The new {@link IConnectionRouter} for the given {@link Edge} .
	 */
	public static void setRouter(Edge edge, IConnectionRouter router) {
		edge.attributesProperty().put(EDGE_ROUTER, router);
	}

	/**
	 * Sets the value of the {@link #EDGE_ROUTER_POINTS} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the {@link #EDGE_ROUTER_POINTS}
	 *            attribute is changed.
	 * @param routerPoints
	 *            The new {@link List} of router {@link Point}s for the given
	 *            {@link Edge}.
	 */
	public static void setRouterPoints(Edge edge, List<Point> routerPoints) {
		edge.getAttributes().put(EDGE_ROUTER_POINTS, routerPoints);
	}

	/**
	 * Sets the value of the {@link #EDGE_SOURCE_DECORATION} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source decoration is changed.
	 * @param sourceDecoration
	 *            The new source decoration {@link javafx.scene.shape.Shape} for
	 *            the given {@link Edge}.
	 */
	public static void setSourceDecoration(Edge edge, javafx.scene.shape.Shape sourceDecoration) {
		edge.attributesProperty().put(EDGE_SOURCE_DECORATION, sourceDecoration);
	}

	/**
	 * Sets the value of the {@link #EDGE_TARGET_DECORATION} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is changed.
	 * @param targetDecoration
	 *            The new target decoration {@link javafx.scene.shape.Shape} for
	 *            the given {@link Edge}.
	 */
	public static void setTargetDecoration(Edge edge, javafx.scene.shape.Shape targetDecoration) {
		edge.attributesProperty().put(EDGE_TARGET_DECORATION, targetDecoration);
	}

	/**
	 * Sets the value of the {@link #NODE_TOOLTIP} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the tooltip is changed.
	 * @param tooltip
	 *            The new tooltip for the given {@link Node}.
	 */
	public static void setTooltip(Node node, String tooltip) {
		node.attributesProperty().put(NODE_TOOLTIP, tooltip);
	}

	/**
	 * Sets the value of the {@link #GRAPH_TYPE} attribute of the given
	 * {@link Graph} to the given value.
	 *
	 * @param graph
	 *            The {@link Graph} of which the type is changed.
	 * @param type
	 *            The new type for the given {@link Graph}.
	 * @throws IllegalArgumentException
	 *             when the given <i>type</i> value is not contained within
	 *             {@link #GRAPH_TYPE_VALUES}.
	 */
	public static void setType(Graph graph, String type) {
		if (!GRAPH_TYPE_VALUES.contains(type)) {
			throw new IllegalArgumentException("Cannot set graph attribute \"" + GRAPH_TYPE + "\" to \"" + type
					+ "\"; supported values: " + GRAPH_TYPE_VALUES);
		}
		graph.attributesProperty().put(GRAPH_TYPE, type);
	}

}

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

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.fx.anchors.IAnchor;
import org.eclipse.gef4.fx.nodes.IConnectionInterpolator;
import org.eclipse.gef4.fx.nodes.IConnectionRouter;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.ILayoutAlgorithm;

import com.google.inject.Provider;

import javafx.scene.image.Image;

/**
 * The {@link ZestProperties} class contains a definition of attributes
 * (including their default values) that are evaluated by Zest.FX. It also
 * provides type-safe utility methods to set and get attribute values.
 *
 * @author mwienand
 *
 */
public class ZestProperties {

	/**
	 * This attribute determines if an element (node/edge) should be ignored by
	 * automatic layout.
	 *
	 * @see #ELEMENT_LAYOUT_IRRELEVANT_DEFAULT
	 * @see #getLayoutIrrelevant(Edge, boolean)
	 * @see #getLayoutIrrelevant(Node, boolean)
	 * @see #setLayoutIrrelevant(Edge, Boolean)
	 * @see #setLayoutIrrelevant(Node, Boolean)
	 */
	public static final String ELEMENT_LAYOUT_IRRELEVANT = "element-layout-irrelevant";

	/**
	 * The default value for the {@link #ELEMENT_LAYOUT_IRRELEVANT} attribute.
	 */
	public static final Boolean ELEMENT_LAYOUT_IRRELEVANT_DEFAULT = false;

	/**
	 * This attribute determines if the corresponding element is invisible.
	 *
	 * @see #getInvisible(Edge, Boolean)
	 * @see #getInvisible(Node, Boolean)
	 * @see #setInvisible(Edge, Boolean)
	 * @see #setInvisible(Node, Boolean)
	 */
	public static final String ELEMENT_INVISIBLE = "invisible";

	/**
	 * The default value for the {@link #ELEMENT_INVISIBLE} attribute.
	 */
	public static final Boolean ELEMENT_INVISIBLE_DEFAULT = false;

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
	 * @see #getCurveCssStyle(Edge)
	 * @see #setCurveCssStyle(Edge, String)
	 */
	public static final String EDGE_CURVE_CSS_STYLE = "edge-curve-css-style";

	/**
	 * This attribute determines the way points that are passed along to the
	 * {@link #EDGE_ROUTER} in addition to the start and end point, which are
	 * provided by the {@link Connection} and computed by {@link IAnchor}s at
	 * the source and target node of the {@link Edge} (and not included in the
	 * list of way points).
	 *
	 * @see #getControlPoints(Edge)
	 * @see #setControlPoints(Edge, List)
	 */
	public static final String EDGE_CONTROL_POINTS = "edge-control-points";

	/**
	 * This attribute determines the shape being used for background and outline
	 * visualization of the node.
	 */
	public static final String NODE_SHAPE = "node-shape";

	/**
	 * This attribute determines the CSS style for a node rectangle. This
	 * attribute does not have a default value.
	 *
	 * @see #getShapeCssStyle(Node)
	 * @see #setShapeCssStyle(Node, String)
	 */
	public static final String NODE_SHAPE_CSS_STYLE = "node-rect-css-style";

	/**
	 * This attribute determines the CSS style for a node or edge label. This
	 * attribute does not have a default value.
	 *
	 * @see #getLabelCssStyle(Node)
	 * @see #getLabelCssStyle(Edge)
	 * @see #setLabelCssStyle(Node, String)
	 * @see #setLabelCssStyle(Edge, String)
	 */
	public static final String ELEMENT_LABEL_CSS_STYLE = "element-label-css-style";

	/**
	 * This attribute determines the CSS style for an external node or edge
	 * label. This attribute does not have a default value.
	 *
	 * @see #getExternalLabelCssStyle(Node)
	 * @see #getExternalLabelCssStyle(Edge)
	 * @see #setExternalLabelCssStyle(Node, String)
	 * @see #setExternalLabelCssStyle(Edge, String)
	 */
	public static final String ELEMENT_EXTERNAL_LABEL_CSS_STYLE = "element-external-label-css-style";

	/**
	 * This attribute determines the (optional) external label of a node.
	 */
	public static final String ELEMENT_EXTERNAL_LABEL = "element-external-label";

	/**
	 * This attribute determines the position of a node's external label (in
	 * case it exists).
	 */
	public static final String ELEMENT_EXTERNAL_LABEL_POSITION = "element-external-label-position";

	/**
	 * This attribute determines the position of an edge's label (in case it
	 * exists).
	 */
	public static final String EDGE_LABEL_POSITION = "edge-label-position";

	/**
	 * This attribute determines the position of an edge's source label (in case
	 * it exists).
	 */
	public static final String EDGE_SOURCE_LABEL_POSITION = "edge-source-label-position";

	/**
	 * This attribute determines the position of an edge's target label (in case
	 * it exists).
	 */
	public static final String EDGE_TARGET_LABEL_POSITION = "edge-target-label-position";

	/**
	 * This attribute determines the icon for a node. This attribute does not
	 * have a default value.
	 *
	 * @see #getIcon(Node)
	 * @see #setIcon(Node, Image)
	 */
	public static final String NODE_ICON = "icon";

	/**
	 * This attribute determines the size for a {@link Node}.
	 *
	 * @see #getSize(Node)
	 * @see #setSize(Node, Dimension)
	 */
	public static final String NODE_SIZE = "size";

	/**
	 * This attribute determines the position for a {@link Node}.
	 *
	 * @see #getPosition(Node)
	 * @see #setPosition(Node, Point)
	 */
	public static final String NODE_POSITION = "position";

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
	 * This attribute determines the target label for an edge. This attribute
	 * does not have a default value.
	 *
	 * @see #getTargetLabel(Edge)
	 * @see #setTargetLabel(Edge, String)
	 */
	public static final String EDGE_TARGET_LABEL = "target-label";

	/**
	 * This attribute determines the source label for an edge. This attribute
	 * does not have a default value.
	 *
	 * @see #getSourceLabel(Edge)
	 * @see #setSourceLabel(Edge, String)
	 */
	public static final String EDGE_SOURCE_LABEL = "source-label";

	/**
	 * This attribute determines the {@link IConnectionRouter} used to route an
	 * edge. This attribute does not have a default value.
	 *
	 * @see #getRouter(Edge)
	 * @see #setRouter(Edge, IConnectionRouter)
	 */
	public static final String EDGE_ROUTER = "edge-router";

	/**
	 * This attribute determines the {@link IConnectionInterpolator} used to
	 * infer a geometry for an edge. This attribute does not have a default
	 * value.
	 *
	 * @see #getInterpolator(Edge)
	 * @see #setInterpolator(Edge, IConnectionInterpolator)
	 */
	public static final String EDGE_INTERPOLATOR = "edge-interpolator";

	/**
	 * This attribute determines the {@link ILayoutAlgorithm} used to layout the
	 * graph.
	 *
	 * @see #getLayoutAlgorithm(Graph)
	 * @see #setLayoutAlgorithm(Graph, ILayoutAlgorithm)
	 */
	public static final String GRAPH_LAYOUT_ALGORITHM = "layout";

	/**
	 * The default value of the {@link #NODE_FISHEYE} attribute.
	 */
	public static Boolean NODE_FISHEYE_DEFAULT = false;

	/**
	 * Returns the value of the {@link #EDGE_CONTROL_POINTS} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} for which to determine the router points.
	 * @return The value of the {@link #EDGE_CONTROL_POINTS} attribute of the
	 *         given {@link Edge}, or an empty list, if the attribute is unset.
	 */
	@SuppressWarnings("unchecked")
	public static List<Point> getControlPoints(Edge edge) {
		Object controlPoints = edge.getAttributes().get(EDGE_CONTROL_POINTS);
		if (controlPoints instanceof List) {
			return (List<Point>) controlPoints;
		}
		return Collections.emptyList();
	}

	/**
	 * Returns the value of the {@link #ELEMENT_CSS_CLASS} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the CSS class is determined.
	 * @return The CSS class of the given {@link Edge}.
	 */
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
	public static String getCssId(Edge edge) {
		Object value = edge.attributesProperty().get(ELEMENT_CSS_ID);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #ELEMENT_CSS_ID} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the CSS id is determined.
	 * @return The CSS id of the given {@link Node}.
	 */
	public static String getCssId(Node node) {
		Object value = node.attributesProperty().get(ELEMENT_CSS_ID);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #EDGE_CURVE_CSS_STYLE} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the curve CSS style is determined.
	 * @return The curve CSS style of the given {@link Edge}.
	 */
	public static String getCurveCssStyle(Edge edge) {
		return (String) edge.attributesProperty().get(EDGE_CURVE_CSS_STYLE);
	}

	/**
	 * Returns the value of the {@link #ELEMENT_EXTERNAL_LABEL} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label is determined.
	 * @return The label of the given {@link Edge}.
	 */
	public static String getExternalLabel(Edge edge) {
		Object value = edge.attributesProperty().get(ELEMENT_EXTERNAL_LABEL);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #ELEMENT_EXTERNAL_LABEL} attribute of the
	 * given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the label is determined.
	 * @return The label of the given {@link Node}.
	 */
	public static String getExternalLabel(Node node) {
		Object value = node.attributesProperty().get(ELEMENT_EXTERNAL_LABEL);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #ELEMENT_LABEL_CSS_STYLE} attribute of
	 * the given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is determined.
	 * @return The label CSS style of the given {@link Edge}.
	 */
	public static String getExternalLabelCssStyle(Edge edge) {
		return (String) edge.attributesProperty().get(ELEMENT_EXTERNAL_LABEL_CSS_STYLE);
	}

	/**
	 * Returns the value of the {@link #ELEMENT_LABEL_CSS_STYLE} attribute of
	 * the given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the label CSS style is determined.
	 * @return The label CSS style of the given {@link Edge}.
	 */
	public static String getExternalLabelCssStyle(Node node) {
		return (String) node.attributesProperty().get(ELEMENT_EXTERNAL_LABEL_CSS_STYLE);
	}

	/**
	 * Returns the value of the {@link #ELEMENT_EXTERNAL_LABEL_POSITION}
	 * attribute of the given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the position is determined.
	 * @return The value of the {@link #ELEMENT_EXTERNAL_LABEL_POSITION}
	 *         attribute of the given {@link Edge}.
	 */
	public static Point getExternalLabelPosition(Edge edge) {
		Object object = edge.getAttributes().get(ELEMENT_EXTERNAL_LABEL_POSITION);
		if (object instanceof Point) {
			return (Point) object;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #ELEMENT_EXTERNAL_LABEL_POSITION}
	 * attribute of the given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the position is determined.
	 * @return The value of the {@link #ELEMENT_EXTERNAL_LABEL_POSITION}
	 *         attribute of the given {@link Node}.
	 */
	public static Point getExternalLabelPosition(Node node) {
		Object object = node.getAttributes().get(ELEMENT_EXTERNAL_LABEL_POSITION);
		if (object instanceof Point) {
			return (Point) object;
		}
		return null;
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
	public static Image getIcon(Node node) {
		return (Image) node.attributesProperty().get(NODE_ICON);
	}

	/**
	 * Returns the value of the {@link #EDGE_INTERPOLATOR} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the interpolator is determined.
	 * @return The router of the given {@link Edge}.
	 */
	public static IConnectionInterpolator getInterpolator(Edge edge) {
		return (IConnectionInterpolator) edge.attributesProperty().get(EDGE_INTERPOLATOR);
	}

	/**
	 * Returns the value of the {@link #ELEMENT_INVISIBLE} attribute of the
	 * given {@link Edge}. If the attribute is not set for the given
	 * {@link Edge}, either the default attribute value is returned, or
	 * <code>null</code>, depending on the <i>returnDefaultIfMissing</i> flag.
	 *
	 * @param edge
	 *            The {@link Edge} of which the {@link #ELEMENT_INVISIBLE}
	 *            attribute value is determined.
	 * @param returnDefaultIfMissing
	 *            <code>true</code> to indicate that the default attribute value
	 *            should be returned if the attribute is not set for the given
	 *            {@link Edge}, otherwise <code>false</code>.
	 * @return The value of the {@link #ELEMENT_INVISIBLE} attribute of the
	 *         given {@link Edge}.
	 */
	public static Boolean getInvisible(Edge edge, Boolean returnDefaultIfMissing) {
		if (edge.getAttributes().containsKey(ELEMENT_INVISIBLE)) {
			return (Boolean) edge.getAttributes().get(ELEMENT_INVISIBLE);
		}
		return returnDefaultIfMissing ? ELEMENT_INVISIBLE_DEFAULT : null;
	}

	/**
	 * Returns the value of the {@link #ELEMENT_INVISIBLE} attribute of the
	 * given {@link Node}. If the attribute is not set for the given
	 * {@link Node}, either the default attribute value is returned, or
	 * <code>null</code>, depending on the <i>returnDefaultIfMissing</i> flag.
	 *
	 * @param node
	 *            The {@link Node} of which the {@link #ELEMENT_INVISIBLE}
	 *            attribute value is determined.
	 * @param returnDefaultIfMissing
	 *            <code>true</code> to indicate that the default attribute value
	 *            should be returned if the attribute is not set for the given
	 *            {@link Node}, otherwise <code>false</code>.
	 * @return The value of the {@link #ELEMENT_INVISIBLE} attribute of the
	 *         given {@link Node}.
	 */
	public static Boolean getInvisible(Node node, Boolean returnDefaultIfMissing) {
		if (node.getAttributes().containsKey(ELEMENT_INVISIBLE)) {
			return (Boolean) node.getAttributes().get(ELEMENT_INVISIBLE);
		}
		return returnDefaultIfMissing ? ELEMENT_INVISIBLE_DEFAULT : null;
	}

	/**
	 * Returns the value of the {@link #ELEMENT_LABEL} attribute for the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} whose attribute value is to be retrieved.
	 * @return The value of the {@link #ELEMENT_LABEL} attribute {@link Edge}.
	 *         If a {@link Provider} was set for the attribute, the value is
	 *         retrieved from the provider using {@link Provider#get()}.
	 */
	public static String getLabel(Edge edge) {
		Object value = edge.attributesProperty().get(ELEMENT_LABEL);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #ELEMENT_LABEL} attribute for the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} whose attribute value is to be retrieved.
	 * @return The value of the {@link #ELEMENT_LABEL} attribute {@link Node}.
	 *         If a {@link Provider} was set for the attribute, the value is
	 *         retrieved from the provider using {@link Provider#get()}.
	 */
	public static String getLabel(Node node) {
		Object value = node.attributesProperty().get(ELEMENT_LABEL);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #ELEMENT_LABEL_CSS_STYLE} attribute of
	 * the given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is determined.
	 * @return The label CSS style of the given {@link Edge}.
	 */
	public static String getLabelCssStyle(Edge edge) {
		return (String) edge.attributesProperty().get(ELEMENT_LABEL_CSS_STYLE);
	}

	/**
	 * Returns the value of the {@link #ELEMENT_LABEL_CSS_STYLE} attribute of
	 * the given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the label CSS style is determined.
	 * @return The label CSS style of the given {@link Node}.
	 */
	public static String getLabelCssStyle(Node node) {
		return (String) node.attributesProperty().get(ELEMENT_LABEL_CSS_STYLE);
	}

	/**
	 * Returns the value of the {@link #EDGE_LABEL_POSITION} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the position is determined.
	 * @return The value of the {@link #EDGE_LABEL_POSITION} attribute of the
	 *         given {@link Edge}.
	 */
	public static Point getLabelPosition(Edge edge) {
		Object object = edge.getAttributes().get(EDGE_LABEL_POSITION);
		if (object instanceof Point) {
			return (Point) object;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #GRAPH_LAYOUT_ALGORITHM} attribute of the
	 * given {@link Graph}.
	 *
	 * @param graph
	 *            The {@link Graph} of which the layout algorithm is determined.
	 * @return The layout algorithm of the given {@link Graph}.
	 */
	public static ILayoutAlgorithm getLayoutAlgorithm(Graph graph) {
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
	 * Returns the value of the {@link #NODE_POSITION} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the position is determined.
	 * @return The value of the {@link #NODE_POSITION} attribute of the given
	 *         {@link Node}.
	 */
	public static Point getPosition(Node node) {
		Object object = node.getAttributes().get(NODE_POSITION);
		if (object instanceof Point) {
			return (Point) object;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #EDGE_ROUTER} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the router is determined.
	 * @return The router of the given {@link Edge}.
	 */
	public static IConnectionRouter getRouter(Edge edge) {
		return (IConnectionRouter) edge.attributesProperty().get(EDGE_ROUTER);
	}

	/**
	 * Returns the value of the {@link #NODE_SHAPE} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} for which to return the value of its
	 *            {@link #NODE_SHAPE} attribute.
	 * @return A {@link javafx.scene.Node} that represents the shape, which is
	 *         used for rendering background and outline of the node.
	 */
	public static javafx.scene.Node getShape(Node node) {
		return (javafx.scene.Node) node.attributesProperty().get(NODE_SHAPE);
	}

	/**
	 * Returns the value of the {@link #NODE_SHAPE_CSS_STYLE} attribute of the
	 * given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the node rectangle CSS style is
	 *            determined.
	 * @return The node rectangle CSS style of the given {@link Node}.
	 */
	public static String getShapeCssStyle(Node node) {
		return (String) node.attributesProperty().get(NODE_SHAPE_CSS_STYLE);
	}

	/**
	 * Returns the value of the {@link #NODE_SIZE} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} for which to return the {@link #NODE_SIZE}.
	 * @return The value of the {@link #NODE_SIZE} attribute of the given
	 *         {@link Node}.
	 */
	public static Dimension getSize(Node node) {
		Object bounds = node.getAttributes().get(NODE_SIZE);
		if (bounds instanceof Dimension) {
			return (Dimension) bounds;
		}
		return null;
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
	 * Returns the value of the {@link #EDGE_SOURCE_LABEL} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source decoration is determined.
	 * @return The value of the {@link #EDGE_SOURCE_LABEL} attribute. In case a
	 *         provider is set for the attribute, the value will be retrieved
	 *         from the provider using {@link Provider#get()}.
	 */
	public static String getSourceLabel(Edge edge) {
		Object value = edge.attributesProperty().get(EDGE_SOURCE_LABEL);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #EDGE_SOURCE_LABEL_POSITION} attribute of
	 * the given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source label position is
	 *            determined.
	 * @return The value of the {@link #EDGE_SOURCE_LABEL_POSITION} attribute of
	 *         the given {@link Edge}.
	 */
	public static Point getSourceLabelPosition(Edge edge) {
		Object object = edge.getAttributes().get(EDGE_SOURCE_LABEL_POSITION);
		if (object instanceof Point) {
			return (Point) object;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #EDGE_TARGET_DECORATION} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is determined.
	 * @return The target decoration of the given {@link Edge}.
	 */
	public static javafx.scene.shape.Shape getTargetDecoration(Edge edge) {
		return (javafx.scene.shape.Shape) edge.attributesProperty().get(EDGE_TARGET_DECORATION);
	}

	/**
	 * Returns the value of the {@link #EDGE_TARGET_LABEL} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is determined.
	 * @return The value of the {@link #EDGE_TARGET_LABEL} attribute. In case a
	 *         provider is set for the attribute, the value will be retrieved
	 *         from the provider using {@link Provider#get()}.
	 */
	public static String getTargetLabel(Edge edge) {
		Object value = edge.attributesProperty().get(EDGE_TARGET_LABEL);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #EDGE_TARGET_LABEL_POSITION} attribute of
	 * the given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target label position is
	 *            determined.
	 * @return The value of the {@link #EDGE_TARGET_LABEL_POSITION} attribute of
	 *         the given {@link Edge}.
	 */
	public static Point getTargetLabelPosition(Edge edge) {
		Object object = edge.getAttributes().get(EDGE_TARGET_LABEL_POSITION);
		if (object instanceof Point) {
			return (Point) object;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #NODE_TOOLTIP} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the tooltip is determined.
	 * @return The tooltip of the given {@link Node}. If a {@link Provider} is
	 *         set for {@link #NODE_TOOLTIP}, the value will be retrieved from
	 *         it using {@link Provider#get()}.
	 */
	public static String getTooltip(Node node) {
		Object value = node.attributesProperty().get(NODE_TOOLTIP);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Sets the value of the {@link #EDGE_CONTROL_POINTS} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the {@link #EDGE_CONTROL_POINTS}
	 *            attribute is changed.
	 * @param controlPoints
	 *            The new {@link List} of control {@link Point}s for the given
	 *            {@link Edge}.
	 */
	public static void setControlPoints(Edge edge, List<Point> controlPoints) {
		edge.getAttributes().put(EDGE_CONTROL_POINTS, controlPoints);
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
	 * @param cssIdProvider
	 *            The new CSS id for the given {@link Edge}.
	 */
	public static void setCssId(Edge edge, Provider<String> cssIdProvider) {
		edge.attributesProperty().put(ELEMENT_CSS_ID, cssIdProvider);
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
	 * @param cssIdProvider
	 *            The new CSS id for the given {@link Node}.
	 */
	public static void setCssId(Node node, Provider<String> cssIdProvider) {
		node.attributesProperty().put(ELEMENT_CSS_ID, cssIdProvider);
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
	public static void setCurveCssStyle(Edge edge, String connCssStyle) {
		edge.attributesProperty().put(EDGE_CURVE_CSS_STYLE, connCssStyle);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_EXTERNAL_LABEL} attribute of the
	 * given {@link Edge} to the given provider.
	 *
	 * @param edge
	 *            The {@link Edge} of which the external label is changed.
	 * @param labelProvider
	 *            A {@link Provider} that is used to retrieve the value of the
	 *            {@link #ELEMENT_EXTERNAL_LABEL} attribute.
	 */
	public static void setExternalLabel(Edge edge, Provider<String> labelProvider) {
		edge.attributesProperty().put(ELEMENT_EXTERNAL_LABEL, labelProvider);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_EXTERNAL_LABEL} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the external label is changed.
	 * @param label
	 *            The new label for the given {@link Edge}.
	 */
	public static void setExternalLabel(Edge edge, String label) {
		edge.attributesProperty().put(ELEMENT_EXTERNAL_LABEL, label);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_EXTERNAL_LABEL} attribute of the
	 * given {@link Node} to the given provider.
	 *
	 * @param node
	 *            The {@link Node} of which the external label is changed.
	 * @param labelProvider
	 *            A {@link Provider} that is used to retrieve the value of the
	 *            {@link #ELEMENT_EXTERNAL_LABEL} attribute.
	 */
	public static void setExternalLabel(Node node, Provider<String> labelProvider) {
		node.attributesProperty().put(ELEMENT_EXTERNAL_LABEL, labelProvider);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_EXTERNAL_LABEL} attribute of the
	 * given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the external label is changed.
	 * @param label
	 *            The new label for the given {@link Node}.
	 */
	public static void setExternalLabel(Node node, String label) {
		node.attributesProperty().put(ELEMENT_EXTERNAL_LABEL, label);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_EXTERNAL_LABEL_CSS_STYLE} attribute
	 * of the given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is changed.
	 * @param textCssStyle
	 *            The new label CSS style for the given {@link Edge}.
	 */
	public static void setExternalLabelCssStyle(Edge edge, String textCssStyle) {
		edge.attributesProperty().put(ELEMENT_EXTERNAL_LABEL_CSS_STYLE, textCssStyle);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_EXTERNAL_LABEL_CSS_STYLE} attribute
	 * of the given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the node label CSS style is changed.
	 * @param textCssStyle
	 *            The new node label CSS style for the given {@link Node}.
	 */
	public static void setExternalLabelCssStyle(Node node, String textCssStyle) {
		node.attributesProperty().put(ELEMENT_EXTERNAL_LABEL_CSS_STYLE, textCssStyle);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_EXTERNAL_LABEL_POSITION} attribute
	 * of the given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the
	 *            {@link #ELEMENT_EXTERNAL_LABEL_POSITION} attribute is changed.
	 * @param externalLabelPosition
	 *            The new external label position.
	 */
	public static void setExternalLabelPosition(Edge edge, Point externalLabelPosition) {
		edge.getAttributes().put(ELEMENT_EXTERNAL_LABEL_POSITION, externalLabelPosition);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_EXTERNAL_LABEL_POSITION} attribute
	 * of the given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the
	 *            {@link #ELEMENT_EXTERNAL_LABEL_POSITION} attribute is changed.
	 * @param externalLabelPosition
	 *            The new external label position.
	 */
	public static void setExternalLabelPosition(Node node, Point externalLabelPosition) {
		node.getAttributes().put(ELEMENT_EXTERNAL_LABEL_POSITION, externalLabelPosition);
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
	 * Sets the value of the {@link #EDGE_INTERPOLATOR} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the interpolator is changed.
	 * @param interpolator
	 *            The new {@link IConnectionInterpolator} for the given
	 *            {@link Edge} .
	 */
	public static void setInterpolator(Edge edge, IConnectionInterpolator interpolator) {
		edge.attributesProperty().put(EDGE_INTERPOLATOR, interpolator);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_INVISIBLE} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} for which to set the
	 *            {@link #ELEMENT_INVISIBLE} attribute.
	 * @param invisible
	 *            The new value for the {@link #ELEMENT_INVISIBLE} attribute of
	 *            the given {@link Edge}.
	 */
	public static void setInvisible(Edge edge, Boolean invisible) {
		edge.getAttributes().put(ELEMENT_INVISIBLE, invisible);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_INVISIBLE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} for which to set the
	 *            {@link #ELEMENT_INVISIBLE} attribute.
	 * @param invisible
	 *            The new value for the {@link #ELEMENT_INVISIBLE} attribute of
	 *            the given {@link Node}.
	 */
	public static void setInvisible(Node node, Boolean invisible) {
		node.getAttributes().put(ELEMENT_INVISIBLE, invisible);
	}

	/**
	 * Sets the {@link #ELEMENT_LABEL} attribute of the given {@link Edge} to
	 * the given provider.
	 *
	 * @param edge
	 *            The {@link Edge} whose attribute is to be changed.
	 * @param labelProvider
	 *            A {@link Provider} which provides the value for the
	 *            {@link #ELEMENT_LABEL} attribute.
	 */
	public static void setLabel(Edge edge, Provider<String> labelProvider) {
		edge.attributesProperty().put(ELEMENT_LABEL, labelProvider);
	}

	/**
	 * Sets the {@link #ELEMENT_LABEL} attribute of the given {@link Edge} to
	 * the given value.
	 *
	 * @param edge
	 *            The {@link Edge} whose attribute is to be changed.
	 * @param label
	 *            The new value for the {@link #ELEMENT_LABEL} attribute.
	 */
	public static void setLabel(Edge edge, String label) {
		edge.attributesProperty().put(ELEMENT_LABEL, label);
	}

	/**
	 * Sets the {@link #ELEMENT_LABEL} attribute of the given {@link Node} to
	 * the given provider.
	 *
	 * @param node
	 *            The {@link Node} whose attribute is to be changed.
	 * @param labelProvider
	 *            A {@link Provider} which provides the value for the
	 *            {@link #ELEMENT_LABEL} attribute.
	 */
	public static void setLabel(Node node, Provider<String> labelProvider) {
		node.attributesProperty().put(ELEMENT_LABEL, labelProvider);
	}

	/**
	 * Sets the {@link #ELEMENT_LABEL} attribute of the given {@link Node} to
	 * the given value.
	 *
	 * @param node
	 *            The {@link Node} whose attribute is to be changed.
	 * @param label
	 *            The new value for the {@link #ELEMENT_LABEL} attribute.
	 */
	public static void setLabel(Node node, String label) {
		node.attributesProperty().put(ELEMENT_LABEL, label);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_LABEL_CSS_STYLE} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is changed.
	 * @param textCssStyle
	 *            The new label CSS style for the given {@link Edge}.
	 */
	public static void setLabelCssStyle(Edge edge, String textCssStyle) {
		edge.attributesProperty().put(ELEMENT_LABEL_CSS_STYLE, textCssStyle);
	}

	/**
	 * Sets the value of the {@link #ELEMENT_LABEL_CSS_STYLE} attribute of the
	 * given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the node label CSS style is changed.
	 * @param textCssStyle
	 *            The new node label CSS style for the given {@link Node}.
	 */
	public static void setLabelCssStyle(Node node, String textCssStyle) {
		node.attributesProperty().put(ELEMENT_LABEL_CSS_STYLE, textCssStyle);
	}

	/**
	 * Sets the value of the {@link #EDGE_LABEL_POSITION} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label is changed.
	 * @param labelPosition
	 *            The new position for the label of the given {@link Edge}.
	 */
	public static void setLabelPosition(Edge edge, Point labelPosition) {
		edge.attributesProperty().put(EDGE_LABEL_POSITION, labelPosition);
	}

	/**
	 * Sets the value of the {@link #GRAPH_LAYOUT_ALGORITHM} attribute of the
	 * given {@link Graph} to the given value.
	 *
	 * @param graph
	 *            The {@link Graph} of which the layout algorithm is changed.
	 * @param algorithm
	 *            The new {@link ILayoutAlgorithm} for the given {@link Graph}.
	 */
	public static void setLayoutAlgorithm(Graph graph, ILayoutAlgorithm algorithm) {
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
	 * Sets the value of the {@link #NODE_POSITION} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the {@link #NODE_POSITION} attribute
	 *            is changed.
	 * @param position
	 *            The new node position.
	 */
	public static void setPosition(Node node, Point position) {
		if (position == null) {
			node.getAttributes().remove(NODE_POSITION);
		}
		node.getAttributes().put(NODE_POSITION, position);
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
	 * Sets the value of the {@link #NODE_SHAPE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node}, for which to set the value of the
	 *            {@link #NODE_SHAPE} attribute.
	 * @param shape
	 *            The shape that is be used for rendering the node outline and
	 *            background.
	 */
	public static void setShape(Node node, javafx.scene.Node shape) {
		node.attributesProperty().put(NODE_SHAPE, shape);
	}

	/**
	 * Sets the value of the {@link #NODE_SHAPE_CSS_STYLE} attribute of the
	 * given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the node rectangle CSS style is
	 *            changed.
	 * @param rectCssStyle
	 *            The new node rectangle CSS style for the given {@link Node}.
	 */
	public static void setShapeCssStyle(Node node, String rectCssStyle) {
		node.attributesProperty().put(NODE_SHAPE_CSS_STYLE, rectCssStyle);
	}

	/**
	 * Sets the value of the {@link #NODE_SIZE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} for which to return the {@link #NODE_SIZE}.
	 * @param size
	 *            The {@link Dimension} describing the new size for the given
	 *            {@link Node}.
	 */
	public static void setSize(Node node, Dimension size) {
		if (size == null) {
			node.getAttributes().remove(NODE_SIZE);
		}
		node.getAttributes().put(NODE_SIZE, size);
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
	 * Sets the value of the {@link #EDGE_SOURCE_LABEL} attribute of the given
	 * {@link Edge} to the given provider.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is changed.
	 * @param sourceLabelProvider
	 *            A {@link Provider} which provides the value for
	 *            {@link #EDGE_SOURCE_LABEL} attribute.
	 */
	public static void setSourceLabel(Edge edge, Provider<String> sourceLabelProvider) {
		edge.attributesProperty().put(EDGE_SOURCE_LABEL, sourceLabelProvider);
	}

	/**
	 * Sets the value of the {@link #EDGE_SOURCE_LABEL} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is changed.
	 * @param sourceLabel
	 *            The new source label for the given {@link Edge}.
	 */
	public static void setSourceLabel(Edge edge, String sourceLabel) {
		edge.attributesProperty().put(EDGE_SOURCE_LABEL, sourceLabel);
	}

	/**
	 * Sets the value of the {@link #EDGE_SOURCE_LABEL_POSITION} attribute of
	 * the given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source label is changed.
	 * @param labelPosition
	 *            The new position for the source label of the given
	 *            {@link Edge}.
	 */
	public static void setSourceLabelPosition(Edge edge, Point labelPosition) {
		edge.attributesProperty().put(EDGE_SOURCE_LABEL_POSITION, labelPosition);
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
	 * Sets the value of the {@link #EDGE_TARGET_LABEL} attribute of the given
	 * {@link Edge} to the given provider.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is changed.
	 * @param targetLabelProvider
	 *            A {@link Provider} which provides the value for
	 *            {@link #EDGE_TARGET_LABEL} attribute.
	 */
	public static void setTargetLabel(Edge edge, Provider<String> targetLabelProvider) {
		edge.attributesProperty().put(EDGE_TARGET_LABEL, targetLabelProvider);
	}

	/**
	 * Sets the value of the {@link #EDGE_TARGET_LABEL} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is changed.
	 * @param targetLabel
	 *            The new target label for the given {@link Edge}.
	 */
	public static void setTargetLabel(Edge edge, String targetLabel) {
		edge.attributesProperty().put(EDGE_TARGET_LABEL, targetLabel);
	}

	/**
	 * Sets the value of the {@link #EDGE_TARGET_LABEL_POSITION} attribute of
	 * the given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target label is changed.
	 * @param labelPosition
	 *            The new position for the target label of the given
	 *            {@link Edge}.
	 */
	public static void setTargetLabelPosition(Edge edge, Point labelPosition) {
		edge.attributesProperty().put(EDGE_TARGET_LABEL_POSITION, labelPosition);
	}

	/**
	 * Sets the value of the {@link #NODE_TOOLTIP} attribute of the given
	 * {@link Node} to the given provider.
	 *
	 * @param node
	 *            The {@link Node} whose attribute is change.
	 * @param tooltipProvider
	 *            A {@link Provider} which is used to retrieve the
	 *            {@link #NODE_TOOLTIP} value.
	 */
	public static void setTooltip(Node node, Provider<String> tooltipProvider) {
		node.attributesProperty().put(NODE_TOOLTIP, tooltipProvider);
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
}

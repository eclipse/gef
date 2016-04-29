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
	 * @see #getLayoutIrrelevant(Edge)
	 * @see #getLayoutIrrelevant(Node)
	 * @see #setLayoutIrrelevant(Edge, Boolean)
	 * @see #setLayoutIrrelevant(Node, Boolean)
	 */
	public static final String LAYOUT_IRRELEVANT__NE = "element-layout-irrelevant";

	/**
	 * This attribute determines if the corresponding element is invisible.
	 *
	 * @see #getInvisible(Edge)
	 * @see #getInvisible(Node)
	 * @see #setInvisible(Edge, Boolean)
	 * @see #setInvisible(Node, Boolean)
	 */
	public static final String INVISIBLE__NE = "invisible";

	/**
	 * This attribute determines the CSS class for an element (node/edge). This
	 * attribute does not have a default value.
	 *
	 * @see #getCssClass(Edge)
	 * @see #getCssClass(Node)
	 * @see #setCssClass(Edge, String)
	 * @see #setCssClass(Node, String)
	 */
	public static final String CSS_CLASS__NE = "css-class";

	/**
	 * This attribute determines the CSS id for an element (node/edge). This
	 * attribute does not have a default value.
	 *
	 * @see #getCssId(Edge)
	 * @see #getCssId(Node)
	 * @see #setCssId(Edge, String)
	 * @see #setCssId(Node, String)
	 */
	public static final String CSS_ID__NE = "css-id";

	/**
	 * This attribute determines the label for an element (node/edge). This
	 * attribute does not have a default value.
	 *
	 * @see #getLabel(Edge)
	 * @see #getLabel(Node)
	 * @see #setLabel(Edge, String)
	 * @see #setLabel(Node, String)
	 */
	public static final String LABEL__NE = "label";

	/**
	 * This attribute determines the curve being used for visualization of the
	 * edge.
	 */
	public static final String CURVE__E = "edge-curve";

	/**
	 * This attribute determines the start point being used for the
	 * visualization of the edge.
	 */
	public static final String START_POINT__E = "edge-start-point";

	/**
	 * This attribute determines the end point being used for the visualization
	 * of the edge.
	 */
	public static final String END_POINT__E = "edge-end-point";

	/**
	 * This attribute determines the CSS style for an edge. This attribute does
	 * not have a default value.
	 *
	 * @see #getCurveCssStyle(Edge)
	 * @see #setCurveCssStyle(Edge, String)
	 */
	public static final String CURVE_CSS_STYLE__E = "edge-curve-css-style";

	/**
	 * This attribute determines the way points that are passed along to the
	 * {@link #ROUTER__E} in addition to the start and end point, which are
	 * provided by the {@link Connection} and computed by {@link IAnchor}s at
	 * the source and target node of the {@link Edge} (and not included in the
	 * list of way points).
	 *
	 * @see #getControlPoints(Edge)
	 * @see #setControlPoints(Edge, List)
	 */
	public static final String CONTROL_POINTS__E = "edge-control-points";

	/**
	 * This attribute determines the shape being used for background and outline
	 * visualization of the node.
	 */
	public static final String SHAPE__N = "node-shape";

	/**
	 * This attribute determines the CSS style for a node rectangle. This
	 * attribute does not have a default value.
	 *
	 * @see #getShapeCssStyle(Node)
	 * @see #setShapeCssStyle(Node, String)
	 */
	public static final String SHAPE_CSS_STYLE__N = "node-rect-css-style";

	/**
	 * This attribute determines the CSS style for a node or edge label. This
	 * attribute does not have a default value.
	 *
	 * @see #getLabelCssStyle(Node)
	 * @see #getLabelCssStyle(Edge)
	 * @see #setLabelCssStyle(Node, String)
	 * @see #setLabelCssStyle(Edge, String)
	 */
	public static final String LABEL_CSS_STYLE__E = "element-label-css-style";

	/**
	 * This attribute determines the CSS style for an external node or edge
	 * label. This attribute does not have a default value.
	 *
	 * @see #getExternalLabelCssStyle(Node)
	 * @see #getExternalLabelCssStyle(Edge)
	 * @see #setExternalLabelCssStyle(Node, String)
	 * @see #setExternalLabelCssStyle(Edge, String)
	 */
	public static final String EXTERNAL_LABEL_CSS_STYLE__NE = "element-external-label-css-style";

	/**
	 * This attribute determines the (optional) external label of a node.
	 */
	public static final String EXTERNAL_LABEL__NE = "element-external-label";

	/**
	 * This attribute determines the position of a node's external label (in
	 * case it exists).
	 */
	public static final String EXTERNAL_LABEL_POSITION__NE = "element-external-label-position";

	/**
	 * This attribute determines the position of an edge's label (in case it
	 * exists).
	 */
	public static final String LABEL_POSITION__E = "edge-label-position";

	/**
	 * This attribute determines the position of an edge's source label (in case
	 * it exists).
	 */
	public static final String SOURCE_LABEL_POSITION__E = "edge-source-label-position";

	/**
	 * This attribute determines the position of an edge's target label (in case
	 * it exists).
	 */
	public static final String TARGET_LABEL_POSITION__E = "edge-target-label-position";

	/**
	 * This attribute determines the icon for a node. This attribute does not
	 * have a default value.
	 *
	 * @see #getIcon(Node)
	 * @see #setIcon(Node, Image)
	 */
	public static final String ICON__N = "icon";

	/**
	 * This attribute determines the size for a {@link Node}.
	 *
	 * @see #getSize(Node)
	 * @see #setSize(Node, Dimension)
	 */
	public static final String SIZE__N = "size";

	/**
	 * This attribute determines the position for a {@link Node}.
	 *
	 * @see #getPosition(Node)
	 * @see #setPosition(Node, Point)
	 */
	public static final String POSITION__N = "position";

	/**
	 * This attribute determines the tooltip for a node. This attribute does not
	 * have a default value.
	 *
	 * @see #getTooltip(Node)
	 * @see #setTooltip(Node, String)
	 */
	public static final String TOOLTIP__N = "tooltip";

	/**
	 * This attribute determines the target decoration for an edge. This
	 * attribute does not have a default value.
	 *
	 * @see #getTargetDecoration(Edge)
	 * @see #setTargetDecoration(Edge, javafx.scene.Node)
	 */
	public static final String TARGET_DECORATION__E = "target-decoration";

	/**
	 * This attribute determines the source decoration for an edge. This
	 * attribute does not have a default value.
	 *
	 * @see #getSourceDecoration(Edge)
	 * @see #setSourceDecoration(Edge, javafx.scene.Node)
	 */
	public static final String SOURCE_DECORATION__E = "source-decoration";

	/**
	 * This attribute determines the target label for an edge. This attribute
	 * does not have a default value.
	 *
	 * @see #getTargetLabel(Edge)
	 * @see #setTargetLabel(Edge, String)
	 */
	public static final String TARGET_LABEL__E = "edge-target-label";

	/**
	 * This attribute determines the source label for an edge. This attribute
	 * does not have a default value.
	 *
	 * @see #getSourceLabel(Edge)
	 * @see #setSourceLabel(Edge, String)
	 */
	public static final String SOURCE_LABEL__E = "edge-source-label";

	/**
	 * This attribute determines the {@link IConnectionRouter} used to route an
	 * edge. This attribute does not have a default value.
	 *
	 * @see #getRouter(Edge)
	 * @see #setRouter(Edge, IConnectionRouter)
	 */
	public static final String ROUTER__E = "edge-router";

	/**
	 * This attribute determines the {@link IConnectionInterpolator} used to
	 * infer a geometry for an edge. This attribute does not have a default
	 * value.
	 *
	 * @see #getInterpolator(Edge)
	 * @see #setInterpolator(Edge, IConnectionInterpolator)
	 */
	public static final String INTERPOLATOR__E = "edge-interpolator";

	/**
	 * This attribute determines the {@link ILayoutAlgorithm} used to layout the
	 * graph.
	 *
	 * @see #getLayoutAlgorithm(Graph)
	 * @see #setLayoutAlgorithm(Graph, ILayoutAlgorithm)
	 */
	public static final String LAYOUT_ALGORITHM__G = "layout";

	/**
	 * Returns the value of the {@link #CONTROL_POINTS__E} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} for which to determine the router points.
	 * @return The value of the {@link #CONTROL_POINTS__E} attribute of the
	 *         given {@link Edge}, or an empty list, if the attribute is unset.
	 */
	@SuppressWarnings("unchecked")
	public static List<Point> getControlPoints(Edge edge) {
		Object controlPoints = edge.getAttributes().get(CONTROL_POINTS__E);
		if (controlPoints instanceof List) {
			return (List<Point>) controlPoints;
		}
		return Collections.emptyList();
	}

	/**
	 * Returns the value of the {@link #CSS_CLASS__NE} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the CSS class is determined.
	 * @return The CSS class of the given {@link Edge}.
	 */
	public static String getCssClass(Edge edge) {
		return (String) edge.attributesProperty().get(CSS_CLASS__NE);
	}

	/**
	 * Returns the value of the {@link #CSS_CLASS__NE} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the CSS class is determined.
	 * @return The CSS class of the given {@link Node}.
	 */
	public static String getCssClass(Node node) {
		return (String) node.attributesProperty().get(CSS_CLASS__NE);
	}

	/**
	 * Returns the value of the {@link #CSS_ID__NE} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the CSS id is determined.
	 * @return The CSS id of the given {@link Edge}.
	 */
	public static String getCssId(Edge edge) {
		Object value = edge.attributesProperty().get(CSS_ID__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #CSS_ID__NE} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the CSS id is determined.
	 * @return The CSS id of the given {@link Node}.
	 */
	public static String getCssId(Node node) {
		Object value = node.attributesProperty().get(CSS_ID__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #CURVE__E} attribute of the given
	 * {@link Node}.
	 *
	 * @param edge
	 *            The {@link Node} for which to return the value of its
	 *            {@link #CURVE__E} attribute.
	 * @return A {@link javafx.scene.Node} that represents the visualization of
	 *         the edge.
	 */
	public static javafx.scene.Node getCurve(Edge edge) {
		return (javafx.scene.Node) edge.attributesProperty().get(CURVE__E);
	}

	/**
	 * Returns the value of the {@link #CURVE_CSS_STYLE__E} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the curve CSS style is determined.
	 * @return The curve CSS style of the given {@link Edge}.
	 */
	public static String getCurveCssStyle(Edge edge) {
		return (String) edge.attributesProperty().get(CURVE_CSS_STYLE__E);
	}

	/**
	 * Returns the value of the {@link #END_POINT__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the end {@link Point} is determined.
	 * @return The end {@link Point} of the given {@link Edge}.
	 */
	public static Point getEndPoint(Edge edge) {
		return (Point) edge.attributesProperty().get(END_POINT__E);
	}

	/**
	 * Returns the value of the {@link #EXTERNAL_LABEL__NE} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label is determined.
	 * @return The label of the given {@link Edge}.
	 */
	public static String getExternalLabel(Edge edge) {
		Object value = edge.attributesProperty().get(EXTERNAL_LABEL__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #EXTERNAL_LABEL__NE} attribute of the
	 * given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the label is determined.
	 * @return The label of the given {@link Node}.
	 */
	public static String getExternalLabel(Node node) {
		Object value = node.attributesProperty().get(EXTERNAL_LABEL__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #LABEL_CSS_STYLE__E} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is determined.
	 * @return The label CSS style of the given {@link Edge}.
	 */
	public static String getExternalLabelCssStyle(Edge edge) {
		return (String) edge.attributesProperty().get(EXTERNAL_LABEL_CSS_STYLE__NE);
	}

	/**
	 * Returns the value of the {@link #LABEL_CSS_STYLE__E} attribute of the
	 * given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the label CSS style is determined.
	 * @return The label CSS style of the given {@link Edge}.
	 */
	public static String getExternalLabelCssStyle(Node node) {
		return (String) node.attributesProperty().get(EXTERNAL_LABEL_CSS_STYLE__NE);
	}

	/**
	 * Returns the value of the {@link #EXTERNAL_LABEL_POSITION__NE} attribute
	 * of the given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the position is determined.
	 * @return The value of the {@link #EXTERNAL_LABEL_POSITION__NE} attribute
	 *         of the given {@link Edge}.
	 */
	public static Point getExternalLabelPosition(Edge edge) {
		Object object = edge.getAttributes().get(EXTERNAL_LABEL_POSITION__NE);
		if (object instanceof Point) {
			return (Point) object;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #EXTERNAL_LABEL_POSITION__NE} attribute
	 * of the given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the position is determined.
	 * @return The value of the {@link #EXTERNAL_LABEL_POSITION__NE} attribute
	 *         of the given {@link Node}.
	 */
	public static Point getExternalLabelPosition(Node node) {
		Object object = node.getAttributes().get(EXTERNAL_LABEL_POSITION__NE);
		if (object instanceof Point) {
			return (Point) object;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #ICON__N} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the icon is determined.
	 * @return The icon of the given {@link Node}.
	 */
	public static Image getIcon(Node node) {
		return (Image) node.attributesProperty().get(ICON__N);
	}

	/**
	 * Returns the value of the {@link #INTERPOLATOR__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the interpolator is determined.
	 * @return The router of the given {@link Edge}.
	 */
	public static IConnectionInterpolator getInterpolator(Edge edge) {
		return (IConnectionInterpolator) edge.attributesProperty().get(INTERPOLATOR__E);
	}

	/**
	 * Returns the value of the {@link #INVISIBLE__NE} attribute of the given
	 * {@link Edge}. If the attribute is not set for the given {@link Edge},
	 * either the default attribute value is returned, or <code>null</code>,
	 * depending on the <i>returnDefaultIfMissing</i> flag.
	 *
	 * @param edge
	 *            The {@link Edge} of which the {@link #INVISIBLE__NE} attribute
	 *            value is determined.
	 * @return The value of the {@link #INVISIBLE__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static Boolean getInvisible(Edge edge) {
		return (Boolean) edge.getAttributes().get(INVISIBLE__NE);
	}

	/**
	 * Returns the value of the {@link #INVISIBLE__NE} attribute of the given
	 * {@link Node}. If the attribute is not set for the given {@link Node},
	 * either the default attribute value is returned, or <code>null</code>,
	 * depending on the <i>returnDefaultIfMissing</i> flag.
	 *
	 * @param node
	 *            The {@link Node} of which the {@link #INVISIBLE__NE} attribute
	 *            value is determined.
	 * @return The value of the {@link #INVISIBLE__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static Boolean getInvisible(Node node) {
		return (Boolean) node.getAttributes().get(INVISIBLE__NE);
	}

	/**
	 * Returns the value of the {@link #LABEL__NE} attribute for the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} whose attribute value is to be retrieved.
	 * @return The value of the {@link #LABEL__NE} attribute {@link Edge}. If a
	 *         {@link Provider} was set for the attribute, the value is
	 *         retrieved from the provider using {@link Provider#get()}.
	 */
	public static String getLabel(Edge edge) {
		Object value = edge.attributesProperty().get(LABEL__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #LABEL__NE} attribute for the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} whose attribute value is to be retrieved.
	 * @return The value of the {@link #LABEL__NE} attribute {@link Node}. If a
	 *         {@link Provider} was set for the attribute, the value is
	 *         retrieved from the provider using {@link Provider#get()}.
	 */
	public static String getLabel(Node node) {
		Object value = node.attributesProperty().get(LABEL__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #LABEL_CSS_STYLE__E} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is determined.
	 * @return The label CSS style of the given {@link Edge}.
	 */
	public static String getLabelCssStyle(Edge edge) {
		return (String) edge.attributesProperty().get(LABEL_CSS_STYLE__E);
	}

	/**
	 * Returns the value of the {@link #LABEL_CSS_STYLE__E} attribute of the
	 * given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the label CSS style is determined.
	 * @return The label CSS style of the given {@link Node}.
	 */
	public static String getLabelCssStyle(Node node) {
		return (String) node.attributesProperty().get(LABEL_CSS_STYLE__E);
	}

	/**
	 * Returns the value of the {@link #LABEL_POSITION__E} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the position is determined.
	 * @return The value of the {@link #LABEL_POSITION__E} attribute of the
	 *         given {@link Edge}.
	 */
	public static Point getLabelPosition(Edge edge) {
		Object object = edge.getAttributes().get(LABEL_POSITION__E);
		if (object instanceof Point) {
			return (Point) object;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #LAYOUT_ALGORITHM__G} attribute of the
	 * given {@link Graph}.
	 *
	 * @param graph
	 *            The {@link Graph} of which the layout algorithm is determined.
	 * @return The layout algorithm of the given {@link Graph}.
	 */
	public static ILayoutAlgorithm getLayoutAlgorithm(Graph graph) {
		Object layout = graph.attributesProperty().get(LAYOUT_ALGORITHM__G);
		if (layout instanceof ILayoutAlgorithm) {
			return (ILayoutAlgorithm) layout;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #LAYOUT_IRRELEVANT__NE} attribute of the
	 * given {@link Edge}. If the attribute is not set for the given
	 * {@link Edge}, either the default attribute value is returned, or
	 * <code>null</code>, depending on the <i>returnDefaultIfMissing</i> flag.
	 *
	 * @param edge
	 *            The {@link Edge} of which the layout irrelevant flag is
	 *            determined.
	 * @return The layout irrelevant flag of the given {@link Edge}.
	 */
	public static Boolean getLayoutIrrelevant(Edge edge) {
		return (Boolean) edge.attributesProperty().get(LAYOUT_IRRELEVANT__NE);
	}

	/**
	 * Returns the value of the {@link #LAYOUT_IRRELEVANT__NE} attribute of the
	 * given {@link Node}. If the attribute is not set for the given
	 * {@link Node}, either the default attribute value is returned, or
	 * <code>null</code>, depending on the <i>returnDefaultIfMissing</i> flag.
	 *
	 * @param node
	 *            The {@link Edge} of which the layout irrelevant flag is
	 *            determined.
	 * @return The layout irrelevant flag of the given {@link Node}.
	 */
	public static Boolean getLayoutIrrelevant(Node node) {
		return (Boolean) node.attributesProperty().get(LAYOUT_IRRELEVANT__NE);
	}

	/**
	 * Returns the value of the {@link #POSITION__N} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the position is determined.
	 * @return The value of the {@link #POSITION__N} attribute of the given
	 *         {@link Node}.
	 */
	public static Point getPosition(Node node) {
		Object object = node.getAttributes().get(POSITION__N);
		if (object instanceof Point) {
			return (Point) object;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #ROUTER__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the router is determined.
	 * @return The router of the given {@link Edge}.
	 */
	public static IConnectionRouter getRouter(Edge edge) {
		return (IConnectionRouter) edge.attributesProperty().get(ROUTER__E);
	}

	/**
	 * Returns the value of the {@link #SHAPE__N} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} for which to return the value of its
	 *            {@link #SHAPE__N} attribute.
	 * @return A {@link javafx.scene.Node} that represents the shape, which is
	 *         used for rendering background and outline of the node.
	 */
	public static javafx.scene.Node getShape(Node node) {
		return (javafx.scene.Node) node.attributesProperty().get(SHAPE__N);
	}

	/**
	 * Returns the value of the {@link #SHAPE_CSS_STYLE__N} attribute of the
	 * given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the node rectangle CSS style is
	 *            determined.
	 * @return The node rectangle CSS style of the given {@link Node}.
	 */
	public static String getShapeCssStyle(Node node) {
		return (String) node.attributesProperty().get(SHAPE_CSS_STYLE__N);
	}

	/**
	 * Returns the value of the {@link #SIZE__N} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} for which to return the {@link #SIZE__N}.
	 * @return The value of the {@link #SIZE__N} attribute of the given
	 *         {@link Node}.
	 */
	public static Dimension getSize(Node node) {
		Object bounds = node.getAttributes().get(SIZE__N);
		if (bounds instanceof Dimension) {
			return (Dimension) bounds;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #SOURCE_DECORATION__E} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source decoration is determined.
	 * @return The source decoration of the given {@link Edge}.
	 */
	public static javafx.scene.Node getSourceDecoration(Edge edge) {
		return (javafx.scene.Node) edge.attributesProperty().get(SOURCE_DECORATION__E);
	}

	/**
	 * Returns the value of the {@link #SOURCE_LABEL__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source decoration is determined.
	 * @return The value of the {@link #SOURCE_LABEL__E} attribute. In case a
	 *         provider is set for the attribute, the value will be retrieved
	 *         from the provider using {@link Provider#get()}.
	 */
	public static String getSourceLabel(Edge edge) {
		Object value = edge.attributesProperty().get(SOURCE_LABEL__E);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #SOURCE_LABEL_POSITION__E} attribute of
	 * the given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source label position is
	 *            determined.
	 * @return The value of the {@link #SOURCE_LABEL_POSITION__E} attribute of
	 *         the given {@link Edge}.
	 */
	public static Point getSourceLabelPosition(Edge edge) {
		Object object = edge.getAttributes().get(SOURCE_LABEL_POSITION__E);
		if (object instanceof Point) {
			return (Point) object;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #START_POINT__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the start {@link Point} is
	 *            determined.
	 * @return The start {@link Point} of the given {@link Edge}.
	 */
	public static Point getStartPoint(Edge edge) {
		return (Point) edge.attributesProperty().get(START_POINT__E);
	}

	/**
	 * Returns the value of the {@link #TARGET_DECORATION__E} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is determined.
	 * @return The target decoration of the given {@link Edge}.
	 */
	public static javafx.scene.Node getTargetDecoration(Edge edge) {
		return (javafx.scene.Node) edge.attributesProperty().get(TARGET_DECORATION__E);
	}

	/**
	 * Returns the value of the {@link #TARGET_LABEL__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is determined.
	 * @return The value of the {@link #TARGET_LABEL__E} attribute. In case a
	 *         provider is set for the attribute, the value will be retrieved
	 *         from the provider using {@link Provider#get()}.
	 */
	public static String getTargetLabel(Edge edge) {
		Object value = edge.attributesProperty().get(TARGET_LABEL__E);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #TARGET_LABEL_POSITION__E} attribute of
	 * the given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target label position is
	 *            determined.
	 * @return The value of the {@link #TARGET_LABEL_POSITION__E} attribute of
	 *         the given {@link Edge}.
	 */
	public static Point getTargetLabelPosition(Edge edge) {
		Object object = edge.getAttributes().get(TARGET_LABEL_POSITION__E);
		if (object instanceof Point) {
			return (Point) object;
		}
		return null;
	}

	/**
	 * Returns the value of the {@link #TOOLTIP__N} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the tooltip is determined.
	 * @return The tooltip of the given {@link Node}. If a {@link Provider} is
	 *         set for {@link #TOOLTIP__N}, the value will be retrieved from it
	 *         using {@link Provider#get()}.
	 */
	public static String getTooltip(Node node) {
		Object value = node.attributesProperty().get(TOOLTIP__N);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Sets the value of the {@link #CONTROL_POINTS__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the {@link #CONTROL_POINTS__E}
	 *            attribute is changed.
	 * @param controlPoints
	 *            The new {@link List} of control {@link Point}s for the given
	 *            {@link Edge}.
	 */
	public static void setControlPoints(Edge edge, List<Point> controlPoints) {
		edge.getAttributes().put(CONTROL_POINTS__E, controlPoints);
	}

	/**
	 * Sets the value of the {@link #CSS_CLASS__NE} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the CSS class is changed.
	 * @param cssClass
	 *            The new CSS class for the given {@link Edge}.
	 */
	public static void setCssClass(Edge edge, String cssClass) {
		edge.attributesProperty().put(CSS_CLASS__NE, cssClass);
	}

	/**
	 * Sets the value of the {@link #CSS_CLASS__NE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the CSS class is changed.
	 * @param cssClass
	 *            The new CSS class for the given {@link Node}.
	 */
	public static void setCssClass(Node node, String cssClass) {
		node.attributesProperty().put(CSS_CLASS__NE, cssClass);
	}

	/**
	 * Sets the value of the {@link #CSS_ID__NE} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the CSS id is changed.
	 * @param cssIdProvider
	 *            The new CSS id for the given {@link Edge}.
	 */
	public static void setCssId(Edge edge, Provider<String> cssIdProvider) {
		edge.attributesProperty().put(CSS_ID__NE, cssIdProvider);
	}

	/**
	 * Sets the value of the {@link #CSS_ID__NE} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the CSS id is changed.
	 * @param cssId
	 *            The new CSS id for the given {@link Edge}.
	 */
	public static void setCssId(Edge edge, String cssId) {
		edge.attributesProperty().put(CSS_ID__NE, cssId);
	}

	/**
	 * Sets the value of the {@link #CSS_ID__NE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the CSS id is changed.
	 * @param cssIdProvider
	 *            The new CSS id for the given {@link Node}.
	 */
	public static void setCssId(Node node, Provider<String> cssIdProvider) {
		node.attributesProperty().put(CSS_ID__NE, cssIdProvider);
	}

	/**
	 * Sets the value of the {@link #CSS_ID__NE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the CSS id is changed.
	 * @param cssId
	 *            The new CSS id for the given {@link Node}.
	 */
	public static void setCssId(Node node, String cssId) {
		node.attributesProperty().put(CSS_ID__NE, cssId);
	}

	/**
	 * Sets the value of the {@link #SHAPE__N} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge}, for which to set the value of the
	 *            {@link #CURVE__E} attribute.
	 * @param curve
	 *            The {@link javafx.scene.Node} that is used to visualize the
	 *            connection.
	 */
	public static void setCurve(Edge edge, javafx.scene.Node curve) {
		edge.attributesProperty().put(CURVE__E, curve);
	}

	/**
	 * Sets the value of the {@link #CURVE_CSS_STYLE__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the curve CSS style is changed.
	 * @param connCssStyle
	 *            The new curve CSS style for the given {@link Edge}.
	 */
	public static void setCurveCssStyle(Edge edge, String connCssStyle) {
		edge.attributesProperty().put(CURVE_CSS_STYLE__E, connCssStyle);
	}

	/**
	 * Sets the value of the {@link #END_POINT__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge}, for which to set the value of the
	 *            {@link #END_POINT__E} attribute.
	 * @param endPoint
	 *            The end {@link Point} for the given {@link Edge}.
	 */
	public static void setEndPoint(Edge edge, Point endPoint) {
		edge.attributesProperty().put(END_POINT__E, endPoint);
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL__NE} attribute of the given
	 * {@link Edge} to the given provider.
	 *
	 * @param edge
	 *            The {@link Edge} of which the external label is changed.
	 * @param labelProvider
	 *            A {@link Provider} that is used to retrieve the value of the
	 *            {@link #EXTERNAL_LABEL__NE} attribute.
	 */
	public static void setExternalLabel(Edge edge, Provider<String> labelProvider) {
		edge.attributesProperty().put(EXTERNAL_LABEL__NE, labelProvider);
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL__NE} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the external label is changed.
	 * @param label
	 *            The new label for the given {@link Edge}.
	 */
	public static void setExternalLabel(Edge edge, String label) {
		edge.attributesProperty().put(EXTERNAL_LABEL__NE, label);
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL__NE} attribute of the given
	 * {@link Node} to the given provider.
	 *
	 * @param node
	 *            The {@link Node} of which the external label is changed.
	 * @param labelProvider
	 *            A {@link Provider} that is used to retrieve the value of the
	 *            {@link #EXTERNAL_LABEL__NE} attribute.
	 */
	public static void setExternalLabel(Node node, Provider<String> labelProvider) {
		node.attributesProperty().put(EXTERNAL_LABEL__NE, labelProvider);
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL__NE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the external label is changed.
	 * @param label
	 *            The new label for the given {@link Node}.
	 */
	public static void setExternalLabel(Node node, String label) {
		node.attributesProperty().put(EXTERNAL_LABEL__NE, label);
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_CSS_STYLE__NE} attribute of
	 * the given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is changed.
	 * @param textCssStyle
	 *            The new label CSS style for the given {@link Edge}.
	 */
	public static void setExternalLabelCssStyle(Edge edge, String textCssStyle) {
		edge.attributesProperty().put(EXTERNAL_LABEL_CSS_STYLE__NE, textCssStyle);
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_CSS_STYLE__NE} attribute of
	 * the given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the node label CSS style is changed.
	 * @param textCssStyle
	 *            The new node label CSS style for the given {@link Node}.
	 */
	public static void setExternalLabelCssStyle(Node node, String textCssStyle) {
		node.attributesProperty().put(EXTERNAL_LABEL_CSS_STYLE__NE, textCssStyle);
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_POSITION__NE} attribute of
	 * the given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the
	 *            {@link #EXTERNAL_LABEL_POSITION__NE} attribute is changed.
	 * @param externalLabelPosition
	 *            The new external label position.
	 */
	public static void setExternalLabelPosition(Edge edge, Point externalLabelPosition) {
		edge.getAttributes().put(EXTERNAL_LABEL_POSITION__NE, externalLabelPosition);
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_POSITION__NE} attribute of
	 * the given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the
	 *            {@link #EXTERNAL_LABEL_POSITION__NE} attribute is changed.
	 * @param externalLabelPosition
	 *            The new external label position.
	 */
	public static void setExternalLabelPosition(Node node, Point externalLabelPosition) {
		node.getAttributes().put(EXTERNAL_LABEL_POSITION__NE, externalLabelPosition);
	}

	/**
	 * Sets the value of the {@link #ICON__N} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the icon is changed.
	 * @param icon
	 *            The new {@link Image} for the given {@link Node}.
	 */
	public static void setIcon(Node node, Image icon) {
		node.attributesProperty().put(ICON__N, icon);
	}

	/**
	 * Sets the value of the {@link #INTERPOLATOR__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the interpolator is changed.
	 * @param interpolator
	 *            The new {@link IConnectionInterpolator} for the given
	 *            {@link Edge} .
	 */
	public static void setInterpolator(Edge edge, IConnectionInterpolator interpolator) {
		edge.attributesProperty().put(INTERPOLATOR__E, interpolator);
	}

	/**
	 * Sets the value of the {@link #INVISIBLE__NE} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} for which to set the {@link #INVISIBLE__NE}
	 *            attribute.
	 * @param invisible
	 *            The new value for the {@link #INVISIBLE__NE} attribute of the
	 *            given {@link Edge}.
	 */
	public static void setInvisible(Edge edge, Boolean invisible) {
		edge.getAttributes().put(INVISIBLE__NE, invisible);
	}

	/**
	 * Sets the value of the {@link #INVISIBLE__NE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} for which to set the {@link #INVISIBLE__NE}
	 *            attribute.
	 * @param invisible
	 *            The new value for the {@link #INVISIBLE__NE} attribute of the
	 *            given {@link Node}.
	 */
	public static void setInvisible(Node node, Boolean invisible) {
		node.getAttributes().put(INVISIBLE__NE, invisible);
	}

	/**
	 * Sets the {@link #LABEL__NE} attribute of the given {@link Edge} to the
	 * given provider.
	 *
	 * @param edge
	 *            The {@link Edge} whose attribute is to be changed.
	 * @param labelProvider
	 *            A {@link Provider} which provides the value for the
	 *            {@link #LABEL__NE} attribute.
	 */
	public static void setLabel(Edge edge, Provider<String> labelProvider) {
		edge.attributesProperty().put(LABEL__NE, labelProvider);
	}

	/**
	 * Sets the {@link #LABEL__NE} attribute of the given {@link Edge} to the
	 * given value.
	 *
	 * @param edge
	 *            The {@link Edge} whose attribute is to be changed.
	 * @param label
	 *            The new value for the {@link #LABEL__NE} attribute.
	 */
	public static void setLabel(Edge edge, String label) {
		edge.attributesProperty().put(LABEL__NE, label);
	}

	/**
	 * Sets the {@link #LABEL__NE} attribute of the given {@link Node} to the
	 * given provider.
	 *
	 * @param node
	 *            The {@link Node} whose attribute is to be changed.
	 * @param labelProvider
	 *            A {@link Provider} which provides the value for the
	 *            {@link #LABEL__NE} attribute.
	 */
	public static void setLabel(Node node, Provider<String> labelProvider) {
		node.attributesProperty().put(LABEL__NE, labelProvider);
	}

	/**
	 * Sets the {@link #LABEL__NE} attribute of the given {@link Node} to the
	 * given value.
	 *
	 * @param node
	 *            The {@link Node} whose attribute is to be changed.
	 * @param label
	 *            The new value for the {@link #LABEL__NE} attribute.
	 */
	public static void setLabel(Node node, String label) {
		node.attributesProperty().put(LABEL__NE, label);
	}

	/**
	 * Sets the value of the {@link #LABEL_CSS_STYLE__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is changed.
	 * @param textCssStyle
	 *            The new label CSS style for the given {@link Edge}.
	 */
	public static void setLabelCssStyle(Edge edge, String textCssStyle) {
		edge.attributesProperty().put(LABEL_CSS_STYLE__E, textCssStyle);
	}

	/**
	 * Sets the value of the {@link #LABEL_CSS_STYLE__E} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the node label CSS style is changed.
	 * @param textCssStyle
	 *            The new node label CSS style for the given {@link Node}.
	 */
	public static void setLabelCssStyle(Node node, String textCssStyle) {
		node.attributesProperty().put(LABEL_CSS_STYLE__E, textCssStyle);
	}

	/**
	 * Sets the value of the {@link #LABEL_POSITION__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label is changed.
	 * @param labelPosition
	 *            The new position for the label of the given {@link Edge}.
	 */
	public static void setLabelPosition(Edge edge, Point labelPosition) {
		edge.attributesProperty().put(LABEL_POSITION__E, labelPosition);
	}

	/**
	 * Sets the value of the {@link #LAYOUT_ALGORITHM__G} attribute of the given
	 * {@link Graph} to the given value.
	 *
	 * @param graph
	 *            The {@link Graph} of which the layout algorithm is changed.
	 * @param algorithm
	 *            The new {@link ILayoutAlgorithm} for the given {@link Graph}.
	 */
	public static void setLayoutAlgorithm(Graph graph, ILayoutAlgorithm algorithm) {
		graph.attributesProperty().put(LAYOUT_ALGORITHM__G, algorithm);
	}

	/**
	 * Sets the value of the {@link #LAYOUT_IRRELEVANT__NE} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the layout irrelevant flag is
	 *            changed.
	 * @param layoutIrrelevant
	 *            The new layout irrelevant flag for the given {@link Edge}.
	 */
	public static void setLayoutIrrelevant(Edge edge, Boolean layoutIrrelevant) {
		edge.attributesProperty().put(LAYOUT_IRRELEVANT__NE, layoutIrrelevant);
	}

	/**
	 * Sets the value of the {@link #LAYOUT_IRRELEVANT__NE} attribute of the
	 * given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the layout irrelevant flag is
	 *            changed.
	 * @param layoutIrrelevant
	 *            The new layout irrelevant flag for the given {@link Node}.
	 */
	public static void setLayoutIrrelevant(Node node, Boolean layoutIrrelevant) {
		node.attributesProperty().put(LAYOUT_IRRELEVANT__NE, layoutIrrelevant);
	}

	/**
	 * Sets the value of the {@link #POSITION__N} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the {@link #POSITION__N} attribute
	 *            is changed.
	 * @param position
	 *            The new node position.
	 */
	public static void setPosition(Node node, Point position) {
		if (position == null) {
			node.getAttributes().remove(POSITION__N);
		}
		node.getAttributes().put(POSITION__N, position);
	}

	/**
	 * Sets the value of the {@link #ROUTER__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the router is changed.
	 * @param router
	 *            The new {@link IConnectionRouter} for the given {@link Edge} .
	 */
	public static void setRouter(Edge edge, IConnectionRouter router) {
		edge.attributesProperty().put(ROUTER__E, router);
	}

	/**
	 * Sets the value of the {@link #SHAPE__N} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node}, for which to set the value of the
	 *            {@link #SHAPE__N} attribute.
	 * @param shape
	 *            The shape that is be used for rendering the node outline and
	 *            background.
	 */
	public static void setShape(Node node, javafx.scene.Node shape) {
		node.attributesProperty().put(SHAPE__N, shape);
	}

	/**
	 * Sets the value of the {@link #SHAPE_CSS_STYLE__N} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the node rectangle CSS style is
	 *            changed.
	 * @param rectCssStyle
	 *            The new node rectangle CSS style for the given {@link Node}.
	 */
	public static void setShapeCssStyle(Node node, String rectCssStyle) {
		node.attributesProperty().put(SHAPE_CSS_STYLE__N, rectCssStyle);
	}

	/**
	 * Sets the value of the {@link #SIZE__N} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} for which to return the {@link #SIZE__N}.
	 * @param size
	 *            The {@link Dimension} describing the new size for the given
	 *            {@link Node}.
	 */
	public static void setSize(Node node, Dimension size) {
		if (size == null) {
			node.getAttributes().remove(SIZE__N);
		}
		node.getAttributes().put(SIZE__N, size);
	}

	/**
	 * Sets the value of the {@link #SOURCE_DECORATION__E} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source decoration is changed.
	 * @param sourceDecoration
	 *            The new source decoration for the given {@link Edge}.
	 */
	public static void setSourceDecoration(Edge edge, javafx.scene.Node sourceDecoration) {
		edge.attributesProperty().put(SOURCE_DECORATION__E, sourceDecoration);
	}

	/**
	 * Sets the value of the {@link #SOURCE_LABEL__E} attribute of the given
	 * {@link Edge} to the given provider.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is changed.
	 * @param sourceLabelProvider
	 *            A {@link Provider} which provides the value for
	 *            {@link #SOURCE_LABEL__E} attribute.
	 */
	public static void setSourceLabel(Edge edge, Provider<String> sourceLabelProvider) {
		edge.attributesProperty().put(SOURCE_LABEL__E, sourceLabelProvider);
	}

	/**
	 * Sets the value of the {@link #SOURCE_LABEL__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is changed.
	 * @param sourceLabel
	 *            The new source label for the given {@link Edge}.
	 */
	public static void setSourceLabel(Edge edge, String sourceLabel) {
		edge.attributesProperty().put(SOURCE_LABEL__E, sourceLabel);
	}

	/**
	 * Sets the value of the {@link #SOURCE_LABEL_POSITION__E} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source label is changed.
	 * @param labelPosition
	 *            The new position for the source label of the given
	 *            {@link Edge}.
	 */
	public static void setSourceLabelPosition(Edge edge, Point labelPosition) {
		edge.attributesProperty().put(SOURCE_LABEL_POSITION__E, labelPosition);
	}

	/**
	 * Sets the value of the {@link #START_POINT__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge}, for which to set the value of the
	 *            {@link #START_POINT__E} attribute.
	 * @param startPoint
	 *            The start Point for the given {@link Edge}.
	 */
	public static void setStartPoint(Edge edge, Point startPoint) {
		edge.attributesProperty().put(START_POINT__E, startPoint);
	}

	/**
	 * Sets the value of the {@link #TARGET_DECORATION__E} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is changed.
	 * @param targetDecoration
	 *            The new target decoration for the given {@link Edge}.
	 */
	public static void setTargetDecoration(Edge edge, javafx.scene.Node targetDecoration) {
		edge.attributesProperty().put(TARGET_DECORATION__E, targetDecoration);
	}

	/**
	 * Sets the value of the {@link #TARGET_LABEL__E} attribute of the given
	 * {@link Edge} to the given provider.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is changed.
	 * @param targetLabelProvider
	 *            A {@link Provider} which provides the value for
	 *            {@link #TARGET_LABEL__E} attribute.
	 */
	public static void setTargetLabel(Edge edge, Provider<String> targetLabelProvider) {
		edge.attributesProperty().put(TARGET_LABEL__E, targetLabelProvider);
	}

	/**
	 * Sets the value of the {@link #TARGET_LABEL__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is changed.
	 * @param targetLabel
	 *            The new target label for the given {@link Edge}.
	 */
	public static void setTargetLabel(Edge edge, String targetLabel) {
		edge.attributesProperty().put(TARGET_LABEL__E, targetLabel);
	}

	/**
	 * Sets the value of the {@link #TARGET_LABEL_POSITION__E} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target label is changed.
	 * @param labelPosition
	 *            The new position for the target label of the given
	 *            {@link Edge}.
	 */
	public static void setTargetLabelPosition(Edge edge, Point labelPosition) {
		edge.attributesProperty().put(TARGET_LABEL_POSITION__E, labelPosition);
	}

	/**
	 * Sets the value of the {@link #TOOLTIP__N} attribute of the given
	 * {@link Node} to the given provider.
	 *
	 * @param node
	 *            The {@link Node} whose attribute is change.
	 * @param tooltipProvider
	 *            A {@link Provider} which is used to retrieve the
	 *            {@link #TOOLTIP__N} value.
	 */
	public static void setTooltip(Node node, Provider<String> tooltipProvider) {
		node.attributesProperty().put(TOOLTIP__N, tooltipProvider);
	}

	/**
	 * Sets the value of the {@link #TOOLTIP__N} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the tooltip is changed.
	 * @param tooltip
	 *            The new tooltip for the given {@link Node}.
	 */
	public static void setTooltip(Node node, String tooltip) {
		node.attributesProperty().put(TOOLTIP__N, tooltip);
	}
}

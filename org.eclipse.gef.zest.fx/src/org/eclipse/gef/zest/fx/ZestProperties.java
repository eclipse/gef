/*******************************************************************************
 * Copyright (c) 2015, 2019 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *     Tamas Miklossy   (itemis AG) - documentation improvements
 *                                  - edge tooltip support (bug #530658)
 *
 *******************************************************************************/
package org.eclipse.gef.zest.fx;

import java.util.Collections;
import java.util.List;

import org.eclipse.gef.fx.anchors.IAnchor;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.fx.nodes.IConnectionInterpolator;
import org.eclipse.gef.fx.nodes.IConnectionRouter;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;

import com.google.inject.Provider;

import javafx.scene.image.Image;

/**
 * The {@link ZestProperties} class contains the definition of the attributes
 * (including their default values) that are evaluated by Zest.FX. It also
 * provides type-safe utility methods to get and set the attribute values.
 *
 * @author mwienand
 *
 */
public class ZestProperties {

	/**
	 * This attribute determines if an element (node/edge) should be ignored by the
	 * automatic layout.
	 *
	 * @see #getLayoutIrrelevant(Edge)
	 * @see #getLayoutIrrelevant(Node)
	 * @see #setLayoutIrrelevant(Edge, Boolean)
	 * @see #setLayoutIrrelevant(Node, Boolean)
	 */
	public static final String LAYOUT_IRRELEVANT__NE = "element-layout-irrelevant";

	/**
	 * This attribute determines if an element (node/edge) is invisible.
	 *
	 * @see #getInvisible(Edge)
	 * @see #getInvisible(Node)
	 * @see #setInvisible(Edge, Boolean)
	 * @see #setInvisible(Node, Boolean)
	 */
	public static final String INVISIBLE__NE = "element-invisible";

	/**
	 * This attribute determines the CSS class for an element (node/edge). This
	 * attribute does not have a default value.
	 *
	 * @see #getCssClass(Edge)
	 * @see #getCssClass(Node)
	 * @see #setCssClass(Edge, String)
	 * @see #setCssClass(Node, String)
	 */
	public static final String CSS_CLASS__NE = "element-css-class";

	/**
	 * This attribute determines the CSS id for an element (node/edge). This
	 * attribute does not have a default value.
	 *
	 * @see #getCssId(Edge)
	 * @see #getCssId(Node)
	 * @see #setCssId(Edge, String)
	 * @see #setCssId(Node, String)
	 */
	public static final String CSS_ID__NE = "element-css-id";

	/**
	 * This attribute determines the label for an element (node/edge). This
	 * attribute does not have a default value.
	 *
	 * @see #getLabel(Edge)
	 * @see #getLabel(Node)
	 * @see #setLabel(Edge, String)
	 * @see #setLabel(Node, String)
	 */
	public static final String LABEL__NE = "element-label";

	/**
	 * This attribute stores a back-reference to the original model element from
	 * which this {@link Graph}, {@link Node}, or {@link Edge} was constructed.
	 *
	 * @since 5.1
	 */
	public static final String MODEL__GNE = "element-model";

	/**
	 * This attribute determines the curve being used for visualization of the edge.
	 */
	public static final String CURVE__E = "edge-curve";

	/**
	 * This attribute determines the start point being used for the visualization of
	 * the edge.
	 */
	public static final String START_POINT__E = "edge-start-point";

	/**
	 * This attribute determines the end point being used for the visualization of
	 * the edge.
	 */
	public static final String END_POINT__E = "edge-end-point";

	/**
	 * This attribute determines the CSS style for an edge. This attribute does not
	 * have a default value.
	 *
	 * @see #getCurveCssStyle(Edge)
	 * @see #setCurveCssStyle(Edge, String)
	 */
	public static final String CURVE_CSS_STYLE__E = "edge-curve-css-style";

	/**
	 * This attribute determines the CSS style for the source decoration of an edge.
	 * This attribute does not have a default value.
	 *
	 * @see #getSourceDecorationCssStyle(Edge)
	 * @see #setSourceDecorationCssStyle(Edge, String)
	 */
	public static final String SOURCE_DECORATION_CSS_STYLE__E = "edge-source-decoration-css-style";

	/**
	 * This attribute determines the CSS style for the target decoration of an edge.
	 * This attribute does not have a default value.
	 *
	 * @see #getTargetDecorationCssStyle(Edge)
	 * @see #setTargetDecorationCssStyle(Edge, String)
	 */
	public static final String TARGET_DECORATION_CSS_STYLE__E = "edge-target-decoration-css-style";

	/**
	 * This attribute determines the way points that are passed along to the
	 * {@link #ROUTER__E} in addition to the start and end point, which are provided
	 * by the {@link Connection} and computed by {@link IAnchor}s at the source and
	 * target node of the {@link Edge} (and not included in the list of way points).
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
	 * This attribute determines the CSS style for a node shape. This attribute does
	 * not have a default value.
	 *
	 * @see #getShapeCssStyle(Node)
	 * @see #setShapeCssStyle(Node, String)
	 */
	public static final String SHAPE_CSS_STYLE__N = "node-rect-css-style";

	/**
	 * This attribute determines the CSS style for an element (node/edge) label.
	 * This attribute does not have a default value.
	 *
	 * @see #getLabelCssStyle(Node)
	 * @see #getLabelCssStyle(Edge)
	 * @see #setLabelCssStyle(Node, String)
	 * @see #setLabelCssStyle(Edge, String)
	 */
	public static final String LABEL_CSS_STYLE__NE = "element-label-css-style";

	/**
	 * This attribute determines the CSS style for the source edge label. This
	 * attribute does not have a default value.
	 *
	 * @see #getSourceLabelCssStyle(Edge)
	 * @see #setSourceLabelCssStyle(Edge, String)
	 */
	public static final String SOURCE_LABEL_CSS_STYLE__E = "edge-source-label-css-style";

	/**
	 * This attribute determines the CSS style for the target edge label. This
	 * attribute does not have a default value.
	 *
	 * @see #getTargetLabelCssStyle(Edge)
	 * @see #setTargetLabelCssStyle(Edge, String)
	 */
	public static final String TARGET_LABEL_CSS_STYLE__E = "edge-target-label-css-style";

	/**
	 * This attribute determines the CSS style for an external label of an element
	 * (node/edge). This attribute does not have a default value.
	 *
	 * @see #getExternalLabelCssStyle(Node)
	 * @see #getExternalLabelCssStyle(Edge)
	 * @see #setExternalLabelCssStyle(Node, String)
	 * @see #setExternalLabelCssStyle(Edge, String)
	 */
	public static final String EXTERNAL_LABEL_CSS_STYLE__NE = "element-external-label-css-style";

	/**
	 * This attribute determines the (optional) external label of an element
	 * (node/edge).
	 */
	public static final String EXTERNAL_LABEL__NE = "element-external-label";

	/**
	 * This attribute determines the position of an element (node/edge)'s external
	 * label (in case it exists).
	 */
	public static final String EXTERNAL_LABEL_POSITION__NE = "element-external-label-position";

	/**
	 * This attribute determines the position of an edge's label (in case it
	 * exists).
	 */
	public static final String LABEL_POSITION__E = "edge-label-position";

	/**
	 * This attribute determines the position of an edge's source label (in case it
	 * exists).
	 */
	public static final String SOURCE_LABEL_POSITION__E = "edge-source-label-position";

	/**
	 * This attribute determines the position of an edge's target label (in case it
	 * exists).
	 */
	public static final String TARGET_LABEL_POSITION__E = "edge-target-label-position";

	/**
	 * This attribute determines the icon for a node. This attribute does not have a
	 * default value.
	 *
	 * @see #getIcon(Node)
	 * @see #setIcon(Node, Image)
	 */
	public static final String ICON__N = "node-icon";

	/**
	 * This attribute determines the size for a node.
	 *
	 * @see #getSize(Node)
	 * @see #setSize(Node, Dimension)
	 */
	public static final String SIZE__N = "node-size";

	/**
	 * This attribute determines the position for a node.
	 *
	 * @see #getPosition(Node)
	 * @see #setPosition(Node, Point)
	 */
	public static final String POSITION__N = "node-position";

	/**
	 * This attribute determines the tooltip for a node. This attribute does not
	 * have a default value.
	 *
	 * @see #getTooltip(Node)
	 * @see #setTooltip(Node, String)
	 */
	public static final String TOOLTIP__N = "node-tooltip";

	/**
	 * This attribute determines the tooltip for an edge. This attribute does not
	 * have a default value.
	 *
	 * @see #getTooltip(Edge)
	 * @see #setTooltip(Edge, String)
	 * @since 5.1
	 */
	public static final String TOOLTIP__E = "edge-tooltip";

	/**
	 * This attribute determines the tooltip for an edge label. This attribute does
	 * not have a default value.
	 *
	 * @see #getLabelTooltip(Edge)
	 * @see #setLabelTooltip(Edge, String)
	 * @since 5.1
	 */
	public static final String LABEL_TOOLTIP__E = "edge-label-tooltip";

	/**
	 * This attribute determines the tooltip for an edge source label. This
	 * attribute does not have a default value.
	 *
	 * @see #getSourceLabelTooltip(Edge)
	 * @see #setSourceLabelTooltip(Edge, String)
	 * @since 5.1
	 */
	public static final String SOURCE_LABEL_TOOLTIP__E = "edge-source-label-tooltip";

	/**
	 * This attribute determines the tooltip for an edge target label. This
	 * attribute does not have a default value.
	 *
	 * @see #getTargetLabelTooltip(Edge)
	 * @see #setTargetLabelTooltip(Edge, String)
	 * @since 5.1
	 */
	public static final String TARGET_LABEL_TOOLTIP__E = "edge-target-label-tooltip";

	/**
	 * This attribute determines the tooltip for an element (node/edge) external
	 * label. This attribute does not have a default value.
	 *
	 * @see #getExternalLabelTooltip(Edge)
	 * @see #getExternalLabelTooltip(Node)
	 * @see #setExternalLabelTooltip(Edge, String)
	 * @see #setExternalLabelTooltip(Node, String)
	 * @since 5.1
	 */
	public static final String EXTERNAL_LABEL_TOOLTIP__NE = "element-external-label-tooltip";

	/**
	 * This attribute determines the target decoration for an edge. This attribute
	 * does not have a default value.
	 *
	 * @see #getTargetDecoration(Edge)
	 * @see #setTargetDecoration(Edge, javafx.scene.Node)
	 */
	public static final String TARGET_DECORATION__E = "edge-target-decoration";

	/**
	 * This attribute determines the source decoration for an edge. This attribute
	 * does not have a default value.
	 *
	 * @see #getSourceDecoration(Edge)
	 * @see #setSourceDecoration(Edge, javafx.scene.Node)
	 */
	public static final String SOURCE_DECORATION__E = "edge-source-decoration";

	/**
	 * This attribute determines the target label for an edge. This attribute does
	 * not have a default value.
	 *
	 * @see #getTargetLabel(Edge)
	 * @see #setTargetLabel(Edge, String)
	 */
	public static final String TARGET_LABEL__E = "edge-target-label";

	/**
	 * This attribute determines the source label for an edge. This attribute does
	 * not have a default value.
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
	 * This attribute determines the {@link IConnectionInterpolator} used to infer a
	 * geometry for an edge. This attribute does not have a default value.
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
	public static final String LAYOUT_ALGORITHM__G = "graph-layout-algorithm";

	/**
	 * Returns the value of the {@link #CONTROL_POINTS__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} for which to determine the router points.
	 * @return The value of the {@link #CONTROL_POINTS__E} attribute of the given
	 *         {@link Edge}, or an empty list, if the attribute is unset.
	 */
	@SuppressWarnings("unchecked")
	public static List<Point> getControlPoints(Edge edge) {
		Object value = edge.getAttributes().get(CONTROL_POINTS__E);
		if (value instanceof Provider) {
			return (List<Point>) ((Provider<?>) value).get();
		}
		return value == null ? Collections.<Point>emptyList() : (List<Point>) value;
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
		Object value = edge.attributesProperty().get(CSS_CLASS__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
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
		Object value = node.attributesProperty().get(CSS_CLASS__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
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
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} for which to return the value of its
	 *            {@link #CURVE__E} attribute.
	 * @return A {@link javafx.scene.Node} that represents the visualization of the
	 *         edge.
	 */
	public static javafx.scene.Node getCurve(Edge edge) {
		Object value = edge.attributesProperty().get(CURVE__E);
		if (value instanceof Provider) {
			return (javafx.scene.Node) ((Provider<?>) value).get();
		}
		return (javafx.scene.Node) value;
	}

	/**
	 * Returns the value of the {@link #CURVE_CSS_STYLE__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the curve CSS style is determined.
	 * @return The curve CSS style of the given {@link Edge}.
	 */
	public static String getCurveCssStyle(Edge edge) {
		Object value = edge.attributesProperty().get(CURVE_CSS_STYLE__E);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
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
		Object value = edge.attributesProperty().get(END_POINT__E);
		if (value instanceof Provider) {
			return (Point) ((Provider<?>) value).get();
		}
		return (Point) value;
	}

	/**
	 * Returns the value of the {@link #EXTERNAL_LABEL__NE} attribute of the given
	 * {@link Edge}.
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
	 * Returns the value of the {@link #EXTERNAL_LABEL__NE} attribute of the given
	 * {@link Node}.
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
	 * Returns the value of the {@link #EXTERNAL_LABEL_CSS_STYLE__NE} attribute of
	 * the given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is determined.
	 * @return The label CSS style of the given {@link Edge}.
	 */
	public static String getExternalLabelCssStyle(Edge edge) {
		Object value = edge.attributesProperty().get(EXTERNAL_LABEL_CSS_STYLE__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #EXTERNAL_LABEL_CSS_STYLE__NE} attribute of
	 * the given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the label CSS style is determined.
	 * @return The label CSS style of the given {@link Node}.
	 */
	public static String getExternalLabelCssStyle(Node node) {
		Object value = node.attributesProperty().get(EXTERNAL_LABEL_CSS_STYLE__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #EXTERNAL_LABEL_POSITION__NE} attribute of
	 * the given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the position is determined.
	 * @return The value of the {@link #EXTERNAL_LABEL_POSITION__NE} attribute of
	 *         the given {@link Edge}.
	 */
	public static Point getExternalLabelPosition(Edge edge) {
		Object value = edge.getAttributes().get(EXTERNAL_LABEL_POSITION__NE);
		if (value instanceof Provider) {
			return (Point) ((Provider<?>) value).get();
		}
		return (Point) value;
	}

	/**
	 * Returns the value of the {@link #EXTERNAL_LABEL_POSITION__NE} attribute of
	 * the given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the position is determined.
	 * @return The value of the {@link #EXTERNAL_LABEL_POSITION__NE} attribute of
	 *         the given {@link Node}.
	 */
	public static Point getExternalLabelPosition(Node node) {
		Object value = node.getAttributes().get(EXTERNAL_LABEL_POSITION__NE);
		if (value instanceof Provider) {
			return (Point) ((Provider<?>) value).get();
		}
		return (Point) value;
	}

	/**
	 * Returns the value of the {@link #EXTERNAL_LABEL_TOOLTIP__NE} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the external label tooltip is
	 *            determined.
	 * @return The tooltip of the given {@link Edge} external label. If a
	 *         {@link Provider} is set for {@link #EXTERNAL_LABEL_TOOLTIP__NE}, the
	 *         value will be retrieved from it using {@link Provider#get()}.
	 * @since 5.1
	 */
	public static String getExternalLabelTooltip(Edge edge) {
		Object value = edge.attributesProperty().get(EXTERNAL_LABEL_TOOLTIP__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #EXTERNAL_LABEL_TOOLTIP__NE} attribute of the
	 * given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the external label tooltip is
	 *            determined.
	 * @return The tooltip of the given {@link Node} extrnal label. If a
	 *         {@link Provider} is set for {@link #EXTERNAL_LABEL_TOOLTIP__NE}, the
	 *         value will be retrieved from it using {@link Provider#get()}.
	 * @since 5.1
	 */
	public static String getExternalLabelTooltip(Node node) {
		Object value = node.attributesProperty().get(EXTERNAL_LABEL_TOOLTIP__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
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
		Object value = node.getAttributes().get(ICON__N);
		if (value instanceof Provider) {
			return (Image) ((Provider<?>) value).get();
		}
		return (Image) value;
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
		Object value = edge.getAttributes().get(INTERPOLATOR__E);
		if (value instanceof Provider) {
			return (IConnectionInterpolator) ((Provider<?>) value).get();
		}
		return (IConnectionInterpolator) value;
	}

	/**
	 * Returns the value of the {@link #INVISIBLE__NE} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the {@link #INVISIBLE__NE} attribute
	 *            value is determined.
	 * @return The value of the {@link #INVISIBLE__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static Boolean getInvisible(Edge edge) {
		Object value = edge.getAttributes().get(INVISIBLE__NE);
		if (value instanceof Provider) {
			return (Boolean) ((Provider<?>) value).get();
		}
		return (Boolean) value;
	}

	/**
	 * Returns the value of the {@link #INVISIBLE__NE} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the {@link #INVISIBLE__NE} attribute
	 *            value is determined.
	 * @return The value of the {@link #INVISIBLE__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static Boolean getInvisible(Node node) {
		Object value = node.getAttributes().get(INVISIBLE__NE);
		if (value instanceof Provider) {
			return (Boolean) ((Provider<?>) value).get();
		}
		return (Boolean) value;
	}

	/**
	 * Returns the value of the {@link #LABEL__NE} attribute for the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} whose attribute value is to be retrieved.
	 * @return The value of the {@link #LABEL__NE} attribute {@link Edge}. If a
	 *         {@link Provider} was set for the attribute, the value is retrieved
	 *         from the provider using {@link Provider#get()}.
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
	 *         {@link Provider} was set for the attribute, the value is retrieved
	 *         from the provider using {@link Provider#get()}.
	 */
	public static String getLabel(Node node) {
		Object value = node.attributesProperty().get(LABEL__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #LABEL_CSS_STYLE__NE} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is determined.
	 * @return The label CSS style of the given {@link Edge}.
	 */
	public static String getLabelCssStyle(Edge edge) {
		Object value = edge.attributesProperty().get(LABEL_CSS_STYLE__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #LABEL_CSS_STYLE__NE} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the label CSS style is determined.
	 * @return The label CSS style of the given {@link Node}.
	 */
	public static String getLabelCssStyle(Node node) {
		Object value = node.attributesProperty().get(LABEL_CSS_STYLE__NE);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #LABEL_POSITION__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the position is determined.
	 * @return The value of the {@link #LABEL_POSITION__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static Point getLabelPosition(Edge edge) {
		Object value = edge.attributesProperty().get(LABEL_POSITION__E);
		if (value instanceof Provider) {
			return (Point) ((Provider<?>) value).get();
		}
		return (Point) value;
	}

	/**
	 * Returns the value of the {@link #LABEL_TOOLTIP__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label tooltip is determined.
	 * @return The tooltip of the given {@link Edge} label. If a {@link Provider} is
	 *         set for {@link #LABEL_TOOLTIP__E}, the value will be retrieved from
	 *         it using {@link Provider#get()}.
	 * @since 5.1
	 */
	public static String getLabelTooltip(Edge edge) {
		Object value = edge.attributesProperty().get(LABEL_TOOLTIP__E);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #LAYOUT_ALGORITHM__G} attribute of the given
	 * {@link Graph}.
	 *
	 * @param graph
	 *            The {@link Graph} of which the layout algorithm is determined.
	 * @return The layout algorithm of the given {@link Graph}.
	 */
	public static ILayoutAlgorithm getLayoutAlgorithm(Graph graph) {
		Object value = graph.attributesProperty().get(LAYOUT_ALGORITHM__G);
		if (value instanceof Provider) {
			return (ILayoutAlgorithm) ((Provider<?>) value).get();
		}
		return (ILayoutAlgorithm) value;
	}

	/**
	 * Returns the value of the {@link #LAYOUT_IRRELEVANT__NE} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the layout irrelevant flag is
	 *            determined.
	 * @return The layout irrelevant flag of the given {@link Edge}.
	 */
	public static Boolean getLayoutIrrelevant(Edge edge) {
		Object value = edge.attributesProperty().get(LAYOUT_IRRELEVANT__NE);
		if (value instanceof Provider) {
			return (Boolean) ((Provider<?>) value).get();
		}
		return (Boolean) value;
	}

	/**
	 * Returns the value of the {@link #LAYOUT_IRRELEVANT__NE} attribute of the
	 * given {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the layout irrelevant flag is
	 *            determined.
	 * @return The layout irrelevant flag of the given {@link Node}.
	 */
	public static Boolean getLayoutIrrelevant(Node node) {
		Object value = node.attributesProperty().get(LAYOUT_IRRELEVANT__NE);
		if (value instanceof Provider) {
			return (Boolean) ((Provider<?>) value).get();
		}
		return (Boolean) value;
	}

	/**
	 * Returns the value of the {@link #MODEL__GNE} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge The {@link Edge} of which the model is determined.
	 * @return The model of the given {@link Edge}.
	 * @since 5.1
	 */
	public static Object getModel(Edge edge) {
		return edge.attributesProperty().get(MODEL__GNE);
	}

	/**
	 * Returns the value of the {@link #MODEL__GNE} attribute of the given
	 * {@link Graph}.
	 *
	 * @param graph The {@link Graph} of which the model is determined.
	 * @return The model of the given {@link Graph}.
	 * @since 5.1
	 */
	public static Object getModel(Graph graph) {
		return graph.attributesProperty().get(MODEL__GNE);
	}

	/**
	 * Returns the value of the {@link #MODEL__GNE} attribute of the given
	 * {@link Node}.
	 *
	 * @param node The {@link Node} of which the model is determined.
	 * @return The model of the given {@link Node}.
	 * @since 5.1
	 */
	public static Object getModel(Node node) {
		return node.attributesProperty().get(MODEL__GNE);
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
		Object value = node.attributesProperty().get(POSITION__N);
		if (value instanceof Provider) {
			return (Point) ((Provider<?>) value).get();
		}
		return (Point) value;
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
		Object value = edge.attributesProperty().get(ROUTER__E);
		if (value instanceof Provider) {
			return (IConnectionRouter) ((Provider<?>) value).get();
		}
		return (IConnectionRouter) value;
	}

	/**
	 * Returns the value of the {@link #SHAPE__N} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} for which to return the value of its
	 *            {@link #SHAPE__N} attribute.
	 * @return A {@link javafx.scene.Node} that represents the shape, which is used
	 *         for rendering background and outline of the node.
	 */
	public static javafx.scene.Node getShape(Node node) {
		Object value = node.attributesProperty().get(SHAPE__N);
		if (value instanceof Provider) {
			return (javafx.scene.Node) ((Provider<?>) value).get();
		}
		return (javafx.scene.Node) value;
	}

	/**
	 * Returns the value of the {@link #SHAPE_CSS_STYLE__N} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the node rectangle CSS style is
	 *            determined.
	 * @return The node rectangle CSS style of the given {@link Node}.
	 */
	public static String getShapeCssStyle(Node node) {
		Object value = node.attributesProperty().get(SHAPE_CSS_STYLE__N);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
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
		Object value = node.attributesProperty().get(SIZE__N);
		if (value instanceof Provider) {
			return (Dimension) ((Provider<?>) value).get();
		}
		return (Dimension) value;
	}

	/**
	 * Returns the value of the {@link #SOURCE_DECORATION__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source decoration is determined.
	 * @return The source decoration of the given {@link Edge}.
	 */
	public static javafx.scene.Node getSourceDecoration(Edge edge) {
		Object value = edge.attributesProperty().get(SOURCE_DECORATION__E);
		if (value instanceof Provider) {
			return (javafx.scene.Node) ((Provider<?>) value).get();
		}
		return (javafx.scene.Node) value;
	}

	/**
	 * Returns the value of the {@link #SOURCE_DECORATION_CSS_STYLE__E} attribute of
	 * the given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source decoration CSS style is
	 *            determined.
	 * @return The source decoration CSS style of the given {@link Edge}.
	 */
	public static String getSourceDecorationCssStyle(Edge edge) {
		Object value = edge.attributesProperty().get(SOURCE_DECORATION_CSS_STYLE__E);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #SOURCE_LABEL__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source decoration is determined.
	 * @return The value of the {@link #SOURCE_LABEL__E} attribute. In case a
	 *         provider is set for the attribute, the value will be retrieved from
	 *         the provider using {@link Provider#get()}.
	 */
	public static String getSourceLabel(Edge edge) {
		Object value = edge.attributesProperty().get(SOURCE_LABEL__E);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #SOURCE_LABEL_CSS_STYLE__E} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source label CSS style is
	 *            determined.
	 * @return The source label CSS style of the given {@link Edge}.
	 */
	public static String getSourceLabelCssStyle(Edge edge) {
		Object value = edge.attributesProperty().get(SOURCE_LABEL_CSS_STYLE__E);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #SOURCE_LABEL_POSITION__E} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source label position is determined.
	 * @return The value of the {@link #SOURCE_LABEL_POSITION__E} attribute of the
	 *         given {@link Edge}.
	 */
	public static Point getSourceLabelPosition(Edge edge) {
		Object value = edge.attributesProperty().get(SOURCE_LABEL_POSITION__E);
		if (value instanceof Provider) {
			return (Point) ((Provider<?>) value).get();
		}
		return (Point) value;
	}

	/**
	 * Returns the value of the {@link #SOURCE_LABEL_TOOLTIP__E} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source label tooltip is determined.
	 * @return The tooltip of the given {@link Edge} source label. If a
	 *         {@link Provider} is set for {@link #SOURCE_LABEL_TOOLTIP__E}, the
	 *         value will be retrieved from it using {@link Provider#get()}.
	 * @since 5.1
	 */
	public static String getSourceLabelTooltip(Edge edge) {
		Object value = edge.attributesProperty().get(SOURCE_LABEL_TOOLTIP__E);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #START_POINT__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the start {@link Point} is determined.
	 * @return The start {@link Point} of the given {@link Edge}.
	 */
	public static Point getStartPoint(Edge edge) {
		Object value = edge.attributesProperty().get(START_POINT__E);
		if (value instanceof Provider) {
			return (Point) ((Provider<?>) value).get();
		}
		return (Point) value;
	}

	/**
	 * Returns the value of the {@link #TARGET_DECORATION__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is determined.
	 * @return The target decoration of the given {@link Edge}.
	 */
	public static javafx.scene.Node getTargetDecoration(Edge edge) {
		Object value = edge.attributesProperty().get(TARGET_DECORATION__E);
		if (value instanceof Provider) {
			return (javafx.scene.Node) ((Provider<?>) value).get();
		}
		return (javafx.scene.Node) value;
	}

	/**
	 * Returns the value of the {@link #TARGET_DECORATION_CSS_STYLE__E} attribute of
	 * the given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration CSS style is
	 *            determined.
	 * @return The target decoration CSS style of the given {@link Edge}.
	 */
	public static String getTargetDecorationCssStyle(Edge edge) {
		Object value = edge.attributesProperty().get(TARGET_DECORATION_CSS_STYLE__E);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #TARGET_LABEL__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is determined.
	 * @return The value of the {@link #TARGET_LABEL__E} attribute. In case a
	 *         provider is set for the attribute, the value will be retrieved from
	 *         the provider using {@link Provider#get()}.
	 */
	public static String getTargetLabel(Edge edge) {
		Object value = edge.attributesProperty().get(TARGET_LABEL__E);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #TARGET_LABEL_CSS_STYLE__E} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target label CSS style is
	 *            determined.
	 * @return The target label CSS style of the given {@link Edge}.
	 */
	public static String getTargetLabelCssStyle(Edge edge) {
		Object value = edge.attributesProperty().get(TARGET_LABEL_CSS_STYLE__E);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #TARGET_LABEL_POSITION__E} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target label position is determined.
	 * @return The value of the {@link #TARGET_LABEL_POSITION__E} attribute of the
	 *         given {@link Edge}.
	 */
	public static Point getTargetLabelPosition(Edge edge) {
		Object value = edge.attributesProperty().get(TARGET_LABEL_POSITION__E);
		if (value instanceof Provider) {
			return (Point) ((Provider<?>) value).get();
		}
		return (Point) value;
	}

	/**
	 * Returns the value of the {@link #TARGET_LABEL_TOOLTIP__E} attribute of the
	 * given {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target label tooltip is determined.
	 * @return The tooltip of the given {@link Edge} target label. If a
	 *         {@link Provider} is set for {@link #TARGET_LABEL_TOOLTIP__E}, the
	 *         value will be retrieved from it using {@link Provider#get()}.
	 * @since 5.1
	 */
	public static String getTargetLabelTooltip(Edge edge) {
		Object value = edge.attributesProperty().get(TARGET_LABEL_TOOLTIP__E);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #TOOLTIP__E} attribute of the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} of which the tooltip is determined.
	 * @return The tooltip of the given {@link Edge}. If a {@link Provider} is set
	 *         for {@link #TOOLTIP__E}, the value will be retrieved from it using
	 *         {@link Provider#get()}.
	 * @since 5.1
	 */
	public static String getTooltip(Edge edge) {
		Object value = edge.attributesProperty().get(TOOLTIP__E);
		if (value instanceof Provider) {
			return (String) ((Provider<?>) value).get();
		}
		return (String) value;
	}

	/**
	 * Returns the value of the {@link #TOOLTIP__N} attribute of the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} of which the tooltip is determined.
	 * @return The tooltip of the given {@link Node}. If a {@link Provider} is set
	 *         for {@link #TOOLTIP__N}, the value will be retrieved from it using
	 *         {@link Provider#get()}.
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
	 *            The {@link Edge} of which the {@link #CONTROL_POINTS__E} attribute
	 *            is changed.
	 * @param controlPoints
	 *            The new {@link List} of control {@link Point}s for the given
	 *            {@link Edge}.
	 */
	public static void setControlPoints(Edge edge, List<Point> controlPoints) {
		if (controlPoints == null) {
			edge.getAttributes().remove(CONTROL_POINTS__E);
		} else {
			edge.getAttributes().put(CONTROL_POINTS__E, controlPoints);
		}
	}

	/**
	 * Sets the value of the {@link #CONTROL_POINTS__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the {@link #CONTROL_POINTS__E} attribute
	 *            is changed.
	 * @param controlPointsProvider
	 *            The new {@link List} of control {@link Point}s for the given
	 *            {@link Edge}.
	 */
	public static void setControlPoints(Edge edge, Provider<List<Point>> controlPointsProvider) {
		if (controlPointsProvider == null) {
			edge.getAttributes().remove(CONTROL_POINTS__E);
		} else {
			edge.getAttributes().put(CONTROL_POINTS__E, controlPointsProvider);
		}
	}

	/**
	 * Sets the value of the {@link #CSS_CLASS__NE} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the CSS class is changed.
	 * @param cssClassProvider
	 *            The new CSS class for the given {@link Edge}.
	 */
	public static void setCssClass(Edge edge, Provider<String> cssClassProvider) {
		if (cssClassProvider == null) {
			edge.getAttributes().remove(CSS_CLASS__NE);
		} else {
			edge.attributesProperty().put(CSS_CLASS__NE, cssClassProvider);
		}
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
		if (cssClass == null) {
			edge.getAttributes().remove(CSS_CLASS__NE);
		} else {
			edge.attributesProperty().put(CSS_CLASS__NE, cssClass);
		}
	}

	/**
	 * Sets the value of the {@link #CSS_CLASS__NE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the CSS class is changed.
	 * @param cssClassProvider
	 *            The new CSS class for the given {@link Node}.
	 */
	public static void setCssClass(Node node, Provider<String> cssClassProvider) {
		if (cssClassProvider == null) {
			node.getAttributes().remove(CSS_CLASS__NE);
		} else {
			node.attributesProperty().put(CSS_CLASS__NE, cssClassProvider);
		}
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
		if (cssClass == null) {
			node.getAttributes().remove(CSS_CLASS__NE);
		} else {
			node.attributesProperty().put(CSS_CLASS__NE, cssClass);
		}
	}

	/**
	 * Sets the value of the {@link #CSS_ID__NE} attribute of the given {@link Edge}
	 * to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the CSS id is changed.
	 * @param cssIdProvider
	 *            The new CSS id for the given {@link Edge}.
	 */
	public static void setCssId(Edge edge, Provider<String> cssIdProvider) {
		if (cssIdProvider == null) {
			edge.getAttributes().remove(CSS_ID__NE);
		} else {
			edge.attributesProperty().put(CSS_ID__NE, cssIdProvider);
		}
	}

	/**
	 * Sets the value of the {@link #CSS_ID__NE} attribute of the given {@link Edge}
	 * to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the CSS id is changed.
	 * @param cssId
	 *            The new CSS id for the given {@link Edge}.
	 */
	public static void setCssId(Edge edge, String cssId) {
		if (cssId == null) {
			edge.getAttributes().remove(CSS_ID__NE);
		} else {
			edge.attributesProperty().put(CSS_ID__NE, cssId);
		}
	}

	/**
	 * Sets the value of the {@link #CSS_ID__NE} attribute of the given {@link Node}
	 * to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the CSS id is changed.
	 * @param cssIdProvider
	 *            The new CSS id for the given {@link Node}.
	 */
	public static void setCssId(Node node, Provider<String> cssIdProvider) {
		if (cssIdProvider == null) {
			node.getAttributes().remove(CSS_ID__NE);
		} else {
			node.attributesProperty().put(CSS_ID__NE, cssIdProvider);
		}
	}

	/**
	 * Sets the value of the {@link #CSS_ID__NE} attribute of the given {@link Node}
	 * to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the CSS id is changed.
	 * @param cssId
	 *            The new CSS id for the given {@link Node}.
	 */
	public static void setCssId(Node node, String cssId) {
		if (cssId == null) {
			node.getAttributes().remove(CSS_ID__NE);
		} else {
			node.attributesProperty().put(CSS_ID__NE, cssId);
		}
	}

	/**
	 * Sets the value of the {@link #SHAPE__N} attribute of the given {@link Edge}
	 * to the given value.
	 *
	 * @param edge
	 *            The {@link Edge}, for which to set the value of the
	 *            {@link #CURVE__E} attribute.
	 * @param curve
	 *            The {@link javafx.scene.Node} that is used to visualize the
	 *            connection.
	 */
	public static void setCurve(Edge edge, javafx.scene.Node curve) {
		if (curve == null) {
			edge.getAttributes().remove(CURVE__E);
		} else {
			edge.attributesProperty().put(CURVE__E, curve);
		}
	}

	/**
	 * Sets the value of the {@link #CURVE__E} attribute of the given {@link Edge}
	 * to the given value.
	 *
	 * @param edge
	 *            The {@link Edge}, for which to set the value of the
	 *            {@link #CURVE__E} attribute.
	 * @param curveProvider
	 *            The {@link javafx.scene.Node} that is used to visualize the
	 *            connection.
	 */
	public static void setCurve(Edge edge, Provider<javafx.scene.Node> curveProvider) {
		if (curveProvider == null) {
			edge.getAttributes().remove(CURVE__E);
		} else {
			edge.attributesProperty().put(CURVE__E, curveProvider);
		}
	}

	/**
	 * Sets the value of the {@link #CURVE_CSS_STYLE__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the curve CSS style is changed.
	 * @param curveCssStyleProvider
	 *            The new curve CSS style for the given {@link Edge}.
	 */
	public static void setCurveCssStyle(Edge edge, Provider<String> curveCssStyleProvider) {
		if (curveCssStyleProvider == null) {
			edge.getAttributes().remove(CURVE_CSS_STYLE__E);
		} else {
			edge.attributesProperty().put(CURVE_CSS_STYLE__E, curveCssStyleProvider);
		}
	}

	/**
	 * Sets the value of the {@link #CURVE_CSS_STYLE__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the curve CSS style is changed.
	 * @param curveCssStyle
	 *            The new curve CSS style for the given {@link Edge}.
	 */
	public static void setCurveCssStyle(Edge edge, String curveCssStyle) {
		if (curveCssStyle == null) {
			edge.getAttributes().remove(CURVE_CSS_STYLE__E);
		} else {
			edge.attributesProperty().put(CURVE_CSS_STYLE__E, curveCssStyle);
		}
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
		if (endPoint == null) {
			edge.getAttributes().remove(END_POINT__E);
		} else {
			edge.attributesProperty().put(END_POINT__E, endPoint);
		}
	}

	/**
	 * Sets the value of the {@link #END_POINT__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge}, for which to set the value of the
	 *            {@link #END_POINT__E} attribute.
	 * @param endPointProvider
	 *            The end {@link Point} for the given {@link Edge}.
	 */
	public static void setEndPoint(Edge edge, Provider<Point> endPointProvider) {
		if (endPointProvider == null) {
			edge.getAttributes().remove(END_POINT__E);
		} else {
			edge.attributesProperty().put(END_POINT__E, endPointProvider);
		}
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
		if (labelProvider == null) {
			edge.getAttributes().remove(EXTERNAL_LABEL__NE);
		} else {
			edge.attributesProperty().put(EXTERNAL_LABEL__NE, labelProvider);
		}
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
		if (label == null) {
			edge.getAttributes().remove(EXTERNAL_LABEL__NE);
		} else {
			edge.attributesProperty().put(EXTERNAL_LABEL__NE, label);
		}
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
		if (labelProvider == null) {
			node.getAttributes().remove(EXTERNAL_LABEL__NE);
		} else {
			node.attributesProperty().put(EXTERNAL_LABEL__NE, labelProvider);
		}
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
		if (label == null) {
			node.getAttributes().remove(EXTERNAL_LABEL__NE);
		} else {
			node.attributesProperty().put(EXTERNAL_LABEL__NE, label);
		}
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_CSS_STYLE__NE} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is changed.
	 * @param textCssStyleProvider
	 *            The new label CSS style for the given {@link Edge}.
	 */
	public static void setExternalLabelCssStyle(Edge edge, Provider<String> textCssStyleProvider) {
		if (textCssStyleProvider == null) {
			edge.getAttributes().remove(EXTERNAL_LABEL_CSS_STYLE__NE);
		} else {
			edge.attributesProperty().put(EXTERNAL_LABEL_CSS_STYLE__NE, textCssStyleProvider);
		}
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_CSS_STYLE__NE} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is changed.
	 * @param textCssStyle
	 *            The new label CSS style for the given {@link Edge}.
	 */
	public static void setExternalLabelCssStyle(Edge edge, String textCssStyle) {
		if (textCssStyle == null) {
			edge.getAttributes().remove(EXTERNAL_LABEL_CSS_STYLE__NE);
		} else {
			edge.attributesProperty().put(EXTERNAL_LABEL_CSS_STYLE__NE, textCssStyle);
		}
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_CSS_STYLE__NE} attribute of the
	 * given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the node label CSS style is changed.
	 * @param textCssStyleProvider
	 *            The new node label CSS style for the given {@link Node}.
	 */
	public static void setExternalLabelCssStyle(Node node, Provider<String> textCssStyleProvider) {
		if (textCssStyleProvider == null) {
			node.getAttributes().remove(EXTERNAL_LABEL_CSS_STYLE__NE);
		} else {
			node.attributesProperty().put(EXTERNAL_LABEL_CSS_STYLE__NE, textCssStyleProvider);
		}
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_CSS_STYLE__NE} attribute of the
	 * given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the node label CSS style is changed.
	 * @param textCssStyle
	 *            The new node label CSS style for the given {@link Node}.
	 */
	public static void setExternalLabelCssStyle(Node node, String textCssStyle) {
		if (textCssStyle == null) {
			node.getAttributes().remove(EXTERNAL_LABEL_CSS_STYLE__NE);
		} else {
			node.attributesProperty().put(EXTERNAL_LABEL_CSS_STYLE__NE, textCssStyle);
		}
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_POSITION__NE} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the {@link #EXTERNAL_LABEL_POSITION__NE}
	 *            attribute is changed.
	 * @param externalLabelPosition
	 *            The new external label position.
	 */
	public static void setExternalLabelPosition(Edge edge, Point externalLabelPosition) {
		if (externalLabelPosition == null) {
			edge.getAttributes().remove(EXTERNAL_LABEL_POSITION__NE);
		} else {
			edge.getAttributes().put(EXTERNAL_LABEL_POSITION__NE, externalLabelPosition);
		}
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_POSITION__NE} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the {@link #EXTERNAL_LABEL_POSITION__NE}
	 *            attribute is changed.
	 * @param externalLabelPositionProvider
	 *            The new external label position.
	 */
	public static void setExternalLabelPosition(Edge edge, Provider<Point> externalLabelPositionProvider) {
		if (externalLabelPositionProvider == null) {
			edge.getAttributes().remove(EXTERNAL_LABEL_POSITION__NE);
		} else {
			edge.getAttributes().put(EXTERNAL_LABEL_POSITION__NE, externalLabelPositionProvider);
		}
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_POSITION__NE} attribute of the
	 * given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the {@link #EXTERNAL_LABEL_POSITION__NE}
	 *            attribute is changed.
	 * @param externalLabelPosition
	 *            The new external label position.
	 */
	public static void setExternalLabelPosition(Node node, Point externalLabelPosition) {
		if (externalLabelPosition == null) {
			node.getAttributes().remove(EXTERNAL_LABEL_POSITION__NE);
		} else {
			node.getAttributes().put(EXTERNAL_LABEL_POSITION__NE, externalLabelPosition);
		}
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_POSITION__NE} attribute of the
	 * given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the {@link #EXTERNAL_LABEL_POSITION__NE}
	 *            attribute is changed.
	 * @param externalLabelPositionProvider
	 *            The new external label position.
	 */
	public static void setExternalLabelPosition(Node node, Provider<Point> externalLabelPositionProvider) {
		if (externalLabelPositionProvider == null) {
			node.getAttributes().remove(EXTERNAL_LABEL_POSITION__NE);
		} else {
			node.getAttributes().put(EXTERNAL_LABEL_POSITION__NE, externalLabelPositionProvider);
		}
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_TOOLTIP__NE} attribute of the
	 * given {@link Edge} to the given provider.
	 *
	 * @param edge
	 *            The {@link Edge} whose attribute is change.
	 * @param tooltipProvider
	 *            A {@link Provider} which is used to retrieve the
	 *            {@link #EXTERNAL_LABEL_TOOLTIP__NE} value.
	 * @since 5.1
	 */
	public static void setExternalLabelTooltip(Edge edge, Provider<String> tooltipProvider) {
		if (tooltipProvider == null) {
			edge.attributesProperty().remove(EXTERNAL_LABEL_TOOLTIP__NE);
		} else {
			edge.attributesProperty().put(EXTERNAL_LABEL_TOOLTIP__NE, tooltipProvider);
		}
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_TOOLTIP__NE} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the external label tooltip is changed.
	 * @param tooltip
	 *            The new external label tooltip for the given {@link Edge}.
	 * @since 5.1
	 */
	public static void setExternalLabelTooltip(Edge edge, String tooltip) {
		if (tooltip == null) {
			edge.attributesProperty().remove(EXTERNAL_LABEL_TOOLTIP__NE);
		} else {
			edge.attributesProperty().put(EXTERNAL_LABEL_TOOLTIP__NE, tooltip);
		}
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_TOOLTIP__NE} attribute of the
	 * given {@link Node} to the given provider.
	 *
	 * @param node
	 *            The {@link Node} whose attribute is change.
	 * @param tooltipProvider
	 *            A {@link Provider} which is used to retrieve the
	 *            {@link #EXTERNAL_LABEL_TOOLTIP__NE} value.
	 * @since 5.1
	 */
	public static void setExternalLabelTooltip(Node node, Provider<String> tooltipProvider) {
		if (tooltipProvider == null) {
			node.attributesProperty().remove(EXTERNAL_LABEL_TOOLTIP__NE);
		} else {
			node.attributesProperty().put(EXTERNAL_LABEL_TOOLTIP__NE, tooltipProvider);
		}
	}

	/**
	 * Sets the value of the {@link #EXTERNAL_LABEL_TOOLTIP__NE} attribute of the
	 * given {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the external label tooltip is changed.
	 * @param tooltip
	 *            The new tooltip for the given {@link Node}'s external label.
	 * @since 5.1
	 */
	public static void setExternalLabelTooltip(Node node, String tooltip) {
		if (tooltip == null) {
			node.attributesProperty().remove(EXTERNAL_LABEL_TOOLTIP__NE);
		} else {
			node.attributesProperty().put(EXTERNAL_LABEL_TOOLTIP__NE, tooltip);
		}
	}

	/**
	 * Sets the value of the {@link #ICON__N} attribute of the given {@link Node} to
	 * the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the icon is changed.
	 * @param icon
	 *            The new {@link Image} for the given {@link Node}.
	 */
	public static void setIcon(Node node, Image icon) {
		if (icon == null) {
			node.getAttributes().remove(ICON__N);
		} else {
			node.attributesProperty().put(ICON__N, icon);
		}
	}

	/**
	 * Sets the value of the {@link #ICON__N} attribute of the given {@link Node} to
	 * the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the icon is changed.
	 * @param iconProvider
	 *            The new {@link Image} for the given {@link Node}.
	 */
	public static void setIcon(Node node, Provider<Image> iconProvider) {
		if (iconProvider == null) {
			node.getAttributes().remove(ICON__N);
		} else {
			node.attributesProperty().put(ICON__N, iconProvider);
		}
	}

	/**
	 * Sets the value of the {@link #INTERPOLATOR__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the interpolator is changed.
	 * @param interpolator
	 *            The new {@link IConnectionInterpolator} for the given {@link Edge}
	 *            .
	 */
	public static void setInterpolator(Edge edge, IConnectionInterpolator interpolator) {
		if (interpolator == null) {
			edge.getAttributes().remove(INTERPOLATOR__E);
		} else {
			edge.attributesProperty().put(INTERPOLATOR__E, interpolator);
		}
	}

	/**
	 * Sets the value of the {@link #INTERPOLATOR__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the interpolator is changed.
	 * @param interpolatorProvider
	 *            The new {@link IConnectionInterpolator} for the given {@link Edge}
	 *            .
	 */
	public static void setInterpolator(Edge edge, Provider<IConnectionInterpolator> interpolatorProvider) {
		if (interpolatorProvider == null) {
			edge.getAttributes().remove(INTERPOLATOR__E);
		} else {
			edge.attributesProperty().put(INTERPOLATOR__E, interpolatorProvider);
		}
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
		if (invisible == null) {
			edge.getAttributes().remove(INVISIBLE__NE);
		} else {
			edge.getAttributes().put(INVISIBLE__NE, invisible);
		}
	}

	/**
	 * Sets the value of the {@link #INVISIBLE__NE} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} for which to set the {@link #INVISIBLE__NE}
	 *            attribute.
	 * @param invisibleProvider
	 *            The new value for the {@link #INVISIBLE__NE} attribute of the
	 *            given {@link Edge}.
	 */
	public static void setInvisible(Edge edge, Provider<Boolean> invisibleProvider) {
		if (invisibleProvider == null) {
			edge.getAttributes().remove(INVISIBLE__NE);
		} else {
			edge.getAttributes().put(INVISIBLE__NE, invisibleProvider);
		}
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
		if (invisible == null) {
			node.getAttributes().remove(INVISIBLE__NE);
		} else {
			node.getAttributes().put(INVISIBLE__NE, invisible);
		}
	}

	/**
	 * Sets the value of the {@link #INVISIBLE__NE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} for which to set the {@link #INVISIBLE__NE}
	 *            attribute.
	 * @param invisibleProvider
	 *            The new value for the {@link #INVISIBLE__NE} attribute of the
	 *            given {@link Node}.
	 */
	public static void setInvisible(Node node, Provider<Boolean> invisibleProvider) {
		if (invisibleProvider == null) {
			node.getAttributes().remove(INVISIBLE__NE);
		} else {
			node.getAttributes().put(INVISIBLE__NE, invisibleProvider);
		}
	}

	/**
	 * Sets the {@link #LABEL__NE} attribute of the given {@link Edge} to the given
	 * provider.
	 *
	 * @param edge
	 *            The {@link Edge} whose attribute is to be changed.
	 * @param labelProvider
	 *            A {@link Provider} which provides the value for the
	 *            {@link #LABEL__NE} attribute.
	 */
	public static void setLabel(Edge edge, Provider<String> labelProvider) {
		if (labelProvider == null) {
			edge.getAttributes().remove(LABEL__NE);
		} else {
			edge.attributesProperty().put(LABEL__NE, labelProvider);
		}
	}

	/**
	 * Sets the {@link #LABEL__NE} attribute of the given {@link Edge} to the given
	 * value.
	 *
	 * @param edge
	 *            The {@link Edge} whose attribute is to be changed.
	 * @param label
	 *            The new value for the {@link #LABEL__NE} attribute.
	 */
	public static void setLabel(Edge edge, String label) {
		if (label == null) {
			edge.getAttributes().remove(LABEL__NE);
		} else {
			edge.attributesProperty().put(LABEL__NE, label);
		}
	}

	/**
	 * Sets the {@link #LABEL__NE} attribute of the given {@link Node} to the given
	 * provider.
	 *
	 * @param node
	 *            The {@link Node} whose attribute is to be changed.
	 * @param labelProvider
	 *            A {@link Provider} which provides the value for the
	 *            {@link #LABEL__NE} attribute.
	 */
	public static void setLabel(Node node, Provider<String> labelProvider) {
		if (labelProvider == null) {
			node.getAttributes().remove(LABEL__NE);
		} else {
			node.attributesProperty().put(LABEL__NE, labelProvider);
		}
	}

	/**
	 * Sets the {@link #LABEL__NE} attribute of the given {@link Node} to the given
	 * value.
	 *
	 * @param node
	 *            The {@link Node} whose attribute is to be changed.
	 * @param label
	 *            The new value for the {@link #LABEL__NE} attribute.
	 */
	public static void setLabel(Node node, String label) {
		if (label == null) {
			node.getAttributes().remove(LABEL__NE);
		} else {
			node.attributesProperty().put(LABEL__NE, label);
		}
	}

	/**
	 * Sets the value of the {@link #LABEL_CSS_STYLE__NE} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is changed.
	 * @param textCssStyleProvider
	 *            The new label CSS style for the given {@link Edge}.
	 */
	public static void setLabelCssStyle(Edge edge, Provider<String> textCssStyleProvider) {
		if (textCssStyleProvider == null) {
			edge.getAttributes().remove(LABEL_CSS_STYLE__NE);
		} else {
			edge.attributesProperty().put(LABEL_CSS_STYLE__NE, textCssStyleProvider);
		}
	}

	/**
	 * Sets the value of the {@link #LABEL_CSS_STYLE__NE} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label CSS style is changed.
	 * @param textCssStyle
	 *            The new label CSS style for the given {@link Edge}.
	 */
	public static void setLabelCssStyle(Edge edge, String textCssStyle) {
		if (textCssStyle == null) {
			edge.getAttributes().remove(LABEL_CSS_STYLE__NE);
		} else {
			edge.attributesProperty().put(LABEL_CSS_STYLE__NE, textCssStyle);
		}
	}

	/**
	 * Sets the value of the {@link #LABEL_CSS_STYLE__NE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the node label CSS style is changed.
	 * @param textCssStyleProvider
	 *            The new node label CSS style for the given {@link Node}.
	 */
	public static void setLabelCssStyle(Node node, Provider<String> textCssStyleProvider) {
		if (textCssStyleProvider == null) {
			node.getAttributes().remove(LABEL_CSS_STYLE__NE);
		} else {
			node.attributesProperty().put(LABEL_CSS_STYLE__NE, textCssStyleProvider);
		}
	}

	/**
	 * Sets the value of the {@link #LABEL_CSS_STYLE__NE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the node label CSS style is changed.
	 * @param textCssStyle
	 *            The new node label CSS style for the given {@link Node}.
	 */
	public static void setLabelCssStyle(Node node, String textCssStyle) {
		if (textCssStyle == null) {
			node.getAttributes().remove(LABEL_CSS_STYLE__NE);
		} else {
			node.attributesProperty().put(LABEL_CSS_STYLE__NE, textCssStyle);
		}
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
		if (labelPosition == null) {
			edge.getAttributes().remove(LABEL_POSITION__E);
		} else {
			edge.attributesProperty().put(LABEL_POSITION__E, labelPosition);
		}
	}

	/**
	 * Sets the value of the {@link #LABEL_POSITION__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label is changed.
	 * @param labelPositionProvider
	 *            The new position for the label of the given {@link Edge}.
	 */
	public static void setLabelPosition(Edge edge, Provider<Point> labelPositionProvider) {
		if (labelPositionProvider == null) {
			edge.getAttributes().remove(LABEL_POSITION__E);
		} else {
			edge.attributesProperty().put(LABEL_POSITION__E, labelPositionProvider);
		}
	}

	/**
	 * Sets the value of the {@link #LABEL_TOOLTIP__E} attribute of the given
	 * {@link Edge} to the given provider.
	 *
	 * @param edge
	 *            The {@link Edge} whose attribute is change.
	 * @param tooltipProvider
	 *            A {@link Provider} which is used to retrieve the
	 *            {@link #LABEL_TOOLTIP__E} value.
	 * @since 5.1
	 */
	public static void setLabelTooltip(Edge edge, Provider<String> tooltipProvider) {
		if (tooltipProvider == null) {
			edge.attributesProperty().remove(LABEL_TOOLTIP__E);
		} else {
			edge.attributesProperty().put(LABEL_TOOLTIP__E, tooltipProvider);
		}
	}

	/**
	 * Sets the value of the {@link #LABEL_TOOLTIP__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the label tooltip is changed.
	 * @param tooltip
	 *            The new tooltip for the given {@link Edge} label.
	 * @since 5.1
	 */
	public static void setLabelTooltip(Edge edge, String tooltip) {
		if (tooltip == null) {
			edge.attributesProperty().remove(LABEL_TOOLTIP__E);
		} else {
			edge.attributesProperty().put(LABEL_TOOLTIP__E, tooltip);
		}
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
		if (algorithm == null) {
			graph.getAttributes().remove(LAYOUT_ALGORITHM__G);
		} else {
			graph.attributesProperty().put(LAYOUT_ALGORITHM__G, algorithm);
		}
	}

	/**
	 * Sets the value of the {@link #LAYOUT_ALGORITHM__G} attribute of the given
	 * {@link Graph} to the given value.
	 *
	 * @param graph
	 *            The {@link Graph} of which the layout algorithm is changed.
	 * @param algorithmProvider
	 *            The new {@link ILayoutAlgorithm} for the given {@link Graph}.
	 */
	public static void setLayoutAlgorithm(Graph graph, Provider<ILayoutAlgorithm> algorithmProvider) {
		if (algorithmProvider == null) {
			graph.getAttributes().remove(LAYOUT_ALGORITHM__G);
		} else {
			graph.attributesProperty().put(LAYOUT_ALGORITHM__G, algorithmProvider);
		}
	}

	/**
	 * Sets the value of the {@link #LAYOUT_IRRELEVANT__NE} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the layout irrelevant flag is changed.
	 * @param layoutIrrelevant
	 *            The new layout irrelevant flag for the given {@link Edge}.
	 */
	public static void setLayoutIrrelevant(Edge edge, Boolean layoutIrrelevant) {
		if (layoutIrrelevant == null) {
			edge.getAttributes().remove(LAYOUT_IRRELEVANT__NE);
		} else {
			edge.attributesProperty().put(LAYOUT_IRRELEVANT__NE, layoutIrrelevant);
		}
	}

	/**
	 * Sets the value of the {@link #LAYOUT_IRRELEVANT__NE} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the layout irrelevant flag is changed.
	 * @param layoutIrrelevantProvider
	 *            The new layout irrelevant flag for the given {@link Edge}.
	 */
	public static void setLayoutIrrelevant(Edge edge, Provider<Boolean> layoutIrrelevantProvider) {
		if (layoutIrrelevantProvider == null) {
			edge.getAttributes().remove(LAYOUT_IRRELEVANT__NE);
		} else {
			edge.attributesProperty().put(LAYOUT_IRRELEVANT__NE, layoutIrrelevantProvider);
		}
	}

	/**
	 * Sets the value of the {@link #LAYOUT_IRRELEVANT__NE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the layout irrelevant flag is changed.
	 * @param layoutIrrelevant
	 *            The new layout irrelevant flag for the given {@link Node}.
	 */
	public static void setLayoutIrrelevant(Node node, Boolean layoutIrrelevant) {
		if (layoutIrrelevant == null) {
			node.getAttributes().remove(LAYOUT_IRRELEVANT__NE);
		} else {
			node.attributesProperty().put(LAYOUT_IRRELEVANT__NE, layoutIrrelevant);
		}
	}

	/**
	 * Sets the value of the {@link #LAYOUT_IRRELEVANT__NE} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the layout irrelevant flag is changed.
	 * @param layoutIrrelevantProvider
	 *            The new layout irrelevant flag for the given {@link Node}.
	 */
	public static void setLayoutIrrelevant(Node node, Provider<Boolean> layoutIrrelevantProvider) {
		if (layoutIrrelevantProvider == null) {
			node.getAttributes().remove(LAYOUT_IRRELEVANT__NE);
		} else {
			node.attributesProperty().put(LAYOUT_IRRELEVANT__NE, layoutIrrelevantProvider);
		}
	}

	/**
	 * Sets the value of the {@link #MODEL__GNE} attribute of the given {@link Edge}
	 * to the given value.
	 *
	 * @param edge  The {@link Edge} of which the model is changed.
	 * @param model The model for the given {@link Edge}.
	 * @since 5.1
	 */
	public static void setModel(Edge edge, Object model) {
		if (model == null) {
			edge.getAttributes().remove(MODEL__GNE);
		} else {
			edge.attributesProperty().put(MODEL__GNE, model);
		}
	}

	/**
	 * Sets the value of the {@link #MODEL__GNE} attribute of the given
	 * {@link Graph} to the given value.
	 *
	 * @param graph The {@link Graph} of which the model is changed.
	 * @param model The model for the given {@link Graph}.
	 * @since 5.1
	 */
	public static void setModel(Graph graph, Object model) {
		if (model == null) {
			graph.getAttributes().remove(MODEL__GNE);
		} else {
			graph.attributesProperty().put(MODEL__GNE, model);
		}
	}

	/**
	 * Sets the value of the {@link #MODEL__GNE} attribute of the given {@link Node}
	 * to the given value.
	 *
	 * @param node  The {@link Node} of which the model is changed.
	 * @param model The model for the given {@link Node}.
	 * @since 5.1
	 */
	public static void setModel(Node node, Object model) {
		if (model == null) {
			node.getAttributes().remove(MODEL__GNE);
		} else {
			node.attributesProperty().put(MODEL__GNE, model);
		}
	}

	/**
	 * Sets the value of the {@link #POSITION__N} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the {@link #POSITION__N} attribute is
	 *            changed.
	 * @param position
	 *            The new node position.
	 */
	public static void setPosition(Node node, Point position) {
		if (position == null) {
			node.getAttributes().remove(POSITION__N);
		} else {
			node.getAttributes().put(POSITION__N, position);
		}
	}

	/**
	 * Sets the value of the {@link #POSITION__N} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the {@link #POSITION__N} attribute is
	 *            changed.
	 * @param positionProvider
	 *            The new node position.
	 */
	public static void setPosition(Node node, Provider<Point> positionProvider) {
		if (positionProvider == null) {
			node.getAttributes().remove(POSITION__N);
		} else {
			node.getAttributes().put(POSITION__N, positionProvider);
		}
	}

	/**
	 * Sets the value of the {@link #ROUTER__E} attribute of the given {@link Edge}
	 * to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the router is changed.
	 * @param router
	 *            The new {@link IConnectionRouter} for the given {@link Edge} .
	 */
	public static void setRouter(Edge edge, IConnectionRouter router) {
		if (router == null) {
			edge.getAttributes().remove(ROUTER__E);
		} else {
			edge.attributesProperty().put(ROUTER__E, router);
		}
	}

	/**
	 * Sets the value of the {@link #ROUTER__E} attribute of the given {@link Edge}
	 * to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the router is changed.
	 * @param routerProvider
	 *            The new {@link IConnectionRouter} for the given {@link Edge} .
	 */
	public static void setRouter(Edge edge, Provider<IConnectionRouter> routerProvider) {
		if (routerProvider == null) {
			edge.getAttributes().remove(ROUTER__E);
		} else {
			edge.attributesProperty().put(ROUTER__E, routerProvider);
		}
	}

	/**
	 * Sets the value of the {@link #SHAPE__N} attribute of the given {@link Node}
	 * to the given value.
	 *
	 * @param node
	 *            The {@link Node}, for which to set the value of the
	 *            {@link #SHAPE__N} attribute.
	 * @param shape
	 *            The shape that is be used for rendering the node outline and
	 *            background.
	 */
	public static void setShape(Node node, javafx.scene.Node shape) {
		if (shape == null) {
			node.getAttributes().remove(SHAPE__N);
		} else {
			node.attributesProperty().put(SHAPE__N, shape);
		}
	}

	/**
	 * Sets the value of the {@link #SHAPE__N} attribute of the given {@link Node}
	 * to the given value.
	 *
	 * @param node
	 *            The {@link Node}, for which to set the value of the
	 *            {@link #SHAPE__N} attribute.
	 * @param shapeProvider
	 *            The shape that is be used for rendering the node outline and
	 *            background.
	 */
	public static void setShape(Node node, Provider<javafx.scene.Node> shapeProvider) {
		if (shapeProvider == null) {
			node.getAttributes().remove(SHAPE__N);
		} else {
			node.attributesProperty().put(SHAPE__N, shapeProvider);
		}
	}

	/**
	 * Sets the value of the {@link #SHAPE_CSS_STYLE__N} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the node rectangle CSS style is changed.
	 * @param rectCssStyleProvider
	 *            The new node rectangle CSS style for the given {@link Node}.
	 */
	public static void setShapeCssStyle(Node node, Provider<String> rectCssStyleProvider) {
		if (rectCssStyleProvider == null) {
			node.getAttributes().remove(SHAPE_CSS_STYLE__N);
		} else {
			node.attributesProperty().put(SHAPE_CSS_STYLE__N, rectCssStyleProvider);
		}
	}

	/**
	 * Sets the value of the {@link #SHAPE_CSS_STYLE__N} attribute of the given
	 * {@link Node} to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the node rectangle CSS style is changed.
	 * @param rectCssStyle
	 *            The new node rectangle CSS style for the given {@link Node}.
	 */
	public static void setShapeCssStyle(Node node, String rectCssStyle) {
		if (rectCssStyle == null) {
			node.getAttributes().remove(SHAPE_CSS_STYLE__N);
		} else {
			node.attributesProperty().put(SHAPE_CSS_STYLE__N, rectCssStyle);
		}
	}

	/**
	 * Sets the value of the {@link #SIZE__N} attribute of the given {@link Node} to
	 * the given value.
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
		} else {
			node.getAttributes().put(SIZE__N, size);
		}
	}

	/**
	 * Sets the value of the {@link #SIZE__N} attribute of the given {@link Node} to
	 * the given value.
	 *
	 * @param node
	 *            The {@link Node} for which to return the {@link #SIZE__N}.
	 * @param sizeProvider
	 *            The {@link Dimension} describing the new size for the given
	 *            {@link Node}.
	 */
	public static void setSize(Node node, Provider<Dimension> sizeProvider) {
		if (sizeProvider == null) {
			node.getAttributes().remove(SIZE__N);
		} else {
			node.getAttributes().put(SIZE__N, sizeProvider);
		}
	}

	/**
	 * Sets the value of the {@link #SOURCE_DECORATION__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source decoration is changed.
	 * @param sourceDecoration
	 *            The new source decoration for the given {@link Edge}.
	 */
	public static void setSourceDecoration(Edge edge, javafx.scene.Node sourceDecoration) {
		if (sourceDecoration == null) {
			edge.attributesProperty().remove(SOURCE_DECORATION__E);
		} else {
			edge.attributesProperty().put(SOURCE_DECORATION__E, sourceDecoration);
		}
	}

	/**
	 * Sets the value of the {@link #SOURCE_DECORATION__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source decoration is changed.
	 * @param sourceDecorationProvider
	 *            The new source decoration for the given {@link Edge}.
	 */
	public static void setSourceDecoration(Edge edge, Provider<javafx.scene.Node> sourceDecorationProvider) {
		if (sourceDecorationProvider == null) {
			edge.attributesProperty().remove(SOURCE_DECORATION__E);
		} else {
			edge.attributesProperty().put(SOURCE_DECORATION__E, sourceDecorationProvider);
		}
	}

	/**
	 * Sets the value of the {@link #SOURCE_DECORATION_CSS_STYLE__E} attribute of
	 * the given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source decoration CSS style is
	 *            changed.
	 * @param sourceDecorationCssStyleProvider
	 *            The new source decoration CSS style for the given {@link Edge} .
	 */
	public static void setSourceDecorationCssStyle(Edge edge, Provider<String> sourceDecorationCssStyleProvider) {
		edge.attributesProperty().put(SOURCE_DECORATION_CSS_STYLE__E, sourceDecorationCssStyleProvider);
	}

	/**
	 * Sets the value of the {@link #SOURCE_DECORATION_CSS_STYLE__E} attribute of
	 * the given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source decoration CSS style is
	 *            changed.
	 * @param sourceDecorationCssStyle
	 *            The new source decoration CSS style for the given {@link Edge} .
	 */
	public static void setSourceDecorationCssStyle(Edge edge, String sourceDecorationCssStyle) {
		if (sourceDecorationCssStyle == null) {
			edge.attributesProperty().remove(SOURCE_DECORATION_CSS_STYLE__E);
		} else {
			edge.attributesProperty().put(SOURCE_DECORATION_CSS_STYLE__E, sourceDecorationCssStyle);
		}
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
		if (sourceLabelProvider == null) {
			edge.attributesProperty().remove(SOURCE_LABEL__E);
		} else {
			edge.attributesProperty().put(SOURCE_LABEL__E, sourceLabelProvider);
		}
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
		if (sourceLabel == null) {
			edge.attributesProperty().remove(SOURCE_LABEL__E);
		} else {
			edge.attributesProperty().put(SOURCE_LABEL__E, sourceLabel);
		}
	}

	/**
	 * Sets the value of the {@link #SOURCE_LABEL_CSS_STYLE__E} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source label CSS style is changed.
	 * @param sourceLabelCssStyleProvider
	 *            The new source label CSS style for the given {@link Edge} .
	 */
	public static void setSourceLabelCssStyle(Edge edge, Provider<String> sourceLabelCssStyleProvider) {
		if (sourceLabelCssStyleProvider == null) {
			edge.attributesProperty().remove(SOURCE_LABEL_CSS_STYLE__E);
		} else {
			edge.attributesProperty().put(SOURCE_LABEL_CSS_STYLE__E, sourceLabelCssStyleProvider);
		}
	}

	/**
	 * Sets the value of the {@link #SOURCE_LABEL_CSS_STYLE__E} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source label CSS style is changed.
	 * @param sourceLabelCssStyle
	 *            The new source label CSS style for the given {@link Edge} .
	 */
	public static void setSourceLabelCssStyle(Edge edge, String sourceLabelCssStyle) {
		if (sourceLabelCssStyle == null) {
			edge.attributesProperty().remove(SOURCE_LABEL_CSS_STYLE__E);
		} else {
			edge.attributesProperty().put(SOURCE_LABEL_CSS_STYLE__E, sourceLabelCssStyle);
		}
	}

	/**
	 * Sets the value of the {@link #SOURCE_LABEL_POSITION__E} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source label is changed.
	 * @param labelPosition
	 *            The new position for the source label of the given {@link Edge}.
	 */
	public static void setSourceLabelPosition(Edge edge, Point labelPosition) {
		if (labelPosition == null) {
			edge.attributesProperty().remove(SOURCE_LABEL_POSITION__E);
		} else {
			edge.attributesProperty().put(SOURCE_LABEL_POSITION__E, labelPosition);
		}
	}

	/**
	 * Sets the value of the {@link #SOURCE_LABEL_POSITION__E} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source label is changed.
	 * @param labelPositionProvider
	 *            The new position for the source label of the given {@link Edge}.
	 */
	public static void setSourceLabelPosition(Edge edge, Provider<Point> labelPositionProvider) {
		if (labelPositionProvider == null) {
			edge.attributesProperty().remove(SOURCE_LABEL_POSITION__E);
		} else {
			edge.attributesProperty().put(SOURCE_LABEL_POSITION__E, labelPositionProvider);
		}
	}

	/**
	 * Sets the value of the {@link #SOURCE_LABEL_TOOLTIP__E} attribute of the given
	 * {@link Edge} to the given provider.
	 *
	 * @param edge
	 *            The {@link Edge} whose attribute is change.
	 * @param tooltipProvider
	 *            A {@link Provider} which is used to retrieve the
	 *            {@link #SOURCE_LABEL_TOOLTIP__E} value.
	 * @since 5.1
	 */
	public static void setSourceLabelTooltip(Edge edge, Provider<String> tooltipProvider) {
		if (tooltipProvider == null) {
			edge.attributesProperty().remove(SOURCE_LABEL_TOOLTIP__E);
		} else {
			edge.attributesProperty().put(SOURCE_LABEL_TOOLTIP__E, tooltipProvider);
		}
	}

	/**
	 * Sets the value of the {@link #SOURCE_LABEL_TOOLTIP__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the source label tooltip is changed.
	 * @param tooltip
	 *            The new tooltip for the given {@link Edge} source label.
	 * @since 5.1
	 */
	public static void setSourceLabelTooltip(Edge edge, String tooltip) {
		if (tooltip == null) {
			edge.attributesProperty().remove(SOURCE_LABEL_TOOLTIP__E);
		} else {
			edge.attributesProperty().put(SOURCE_LABEL_TOOLTIP__E, tooltip);
		}
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
		if (startPoint == null) {
			edge.attributesProperty().remove(START_POINT__E);
		} else {
			edge.attributesProperty().put(START_POINT__E, startPoint);
		}
	}

	/**
	 * Sets the value of the {@link #START_POINT__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge}, for which to set the value of the
	 *            {@link #START_POINT__E} attribute.
	 * @param startPointProvider
	 *            The start Point for the given {@link Edge}.
	 */
	public static void setStartPoint(Edge edge, Provider<Point> startPointProvider) {
		if (startPointProvider == null) {
			edge.attributesProperty().remove(START_POINT__E);
		} else {
			edge.attributesProperty().put(START_POINT__E, startPointProvider);
		}
	}

	/**
	 * Sets the value of the {@link #TARGET_DECORATION__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is changed.
	 * @param targetDecoration
	 *            The new target decoration for the given {@link Edge}.
	 */
	public static void setTargetDecoration(Edge edge, javafx.scene.Node targetDecoration) {
		if (targetDecoration == null) {
			edge.attributesProperty().remove(TARGET_DECORATION__E);
		} else {
			edge.attributesProperty().put(TARGET_DECORATION__E, targetDecoration);
		}
	}

	/**
	 * Sets the value of the {@link #TARGET_DECORATION__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration is changed.
	 * @param targetDecorationProvider
	 *            The new target decoration for the given {@link Edge}.
	 */
	public static void setTargetDecoration(Edge edge, Provider<javafx.scene.Node> targetDecorationProvider) {
		if (targetDecorationProvider == null) {
			edge.attributesProperty().remove(TARGET_DECORATION__E);
		} else {
			edge.attributesProperty().put(TARGET_DECORATION__E, targetDecorationProvider);
		}
	}

	/**
	 * Sets the value of the {@link #TARGET_DECORATION_CSS_STYLE__E} attribute of
	 * the given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration CSS style is
	 *            changed.
	 * @param targetDecorationCssStyleProvider
	 *            The new target decoration CSS style for the given {@link Edge} .
	 */
	public static void setTargetDecorationCssStyle(Edge edge, Provider<String> targetDecorationCssStyleProvider) {
		if (targetDecorationCssStyleProvider == null) {
			edge.attributesProperty().remove(TARGET_DECORATION_CSS_STYLE__E);
		} else {
			edge.attributesProperty().put(TARGET_DECORATION_CSS_STYLE__E, targetDecorationCssStyleProvider);
		}
	}

	/**
	 * Sets the value of the {@link #TARGET_DECORATION_CSS_STYLE__E} attribute of
	 * the given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target decoration CSS style is
	 *            changed.
	 * @param targetDecorationCssStyle
	 *            The new target decoration CSS style for the given {@link Edge} .
	 */
	public static void setTargetDecorationCssStyle(Edge edge, String targetDecorationCssStyle) {
		if (targetDecorationCssStyle == null) {
			edge.attributesProperty().remove(TARGET_DECORATION_CSS_STYLE__E);
		} else {
			edge.attributesProperty().put(TARGET_DECORATION_CSS_STYLE__E, targetDecorationCssStyle);
		}
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
		if (targetLabelProvider == null) {
			edge.attributesProperty().remove(TARGET_LABEL__E);
		} else {
			edge.attributesProperty().put(TARGET_LABEL__E, targetLabelProvider);
		}
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
		if (targetLabel == null) {
			edge.attributesProperty().remove(TARGET_LABEL__E);
		} else {
			edge.attributesProperty().put(TARGET_LABEL__E, targetLabel);
		}
	}

	/**
	 * Sets the value of the {@link #TARGET_LABEL_CSS_STYLE__E} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target label CSS style is changed.
	 * @param targetLabelCssStyleProvider
	 *            The new target label CSS style for the given {@link Edge} .
	 */
	public static void setTargetLabelCssStyle(Edge edge, Provider<String> targetLabelCssStyleProvider) {
		if (targetLabelCssStyleProvider == null) {
			edge.attributesProperty().remove(TARGET_LABEL_CSS_STYLE__E);
		} else {
			edge.attributesProperty().put(TARGET_LABEL_CSS_STYLE__E, targetLabelCssStyleProvider);
		}
	}

	/**
	 * Sets the value of the {@link #TARGET_LABEL_CSS_STYLE__E} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target label CSS style is changed.
	 * @param targetLabelCssStyle
	 *            The new target label CSS style for the given {@link Edge} .
	 */
	public static void setTargetLabelCssStyle(Edge edge, String targetLabelCssStyle) {
		if (targetLabelCssStyle == null) {
			edge.attributesProperty().remove(TARGET_LABEL_CSS_STYLE__E);
		} else {
			edge.attributesProperty().put(TARGET_LABEL_CSS_STYLE__E, targetLabelCssStyle);
		}
	}

	/**
	 * Sets the value of the {@link #TARGET_LABEL_POSITION__E} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target label is changed.
	 * @param labelPosition
	 *            The new position for the target label of the given {@link Edge}.
	 */
	public static void setTargetLabelPosition(Edge edge, Point labelPosition) {
		if (labelPosition == null) {
			edge.attributesProperty().remove(TARGET_LABEL_POSITION__E);
		} else {
			edge.attributesProperty().put(TARGET_LABEL_POSITION__E, labelPosition);
		}
	}

	/**
	 * Sets the value of the {@link #TARGET_LABEL_POSITION__E} attribute of the
	 * given {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target label is changed.
	 * @param labelPositionProvider
	 *            The new position for the target label of the given {@link Edge}.
	 */
	public static void setTargetLabelPosition(Edge edge, Provider<Point> labelPositionProvider) {
		if (labelPositionProvider == null) {
			edge.attributesProperty().remove(TARGET_LABEL_POSITION__E);
		} else {
			edge.attributesProperty().put(TARGET_LABEL_POSITION__E, labelPositionProvider);
		}
	}

	/**
	 * Sets the value of the {@link #TARGET_LABEL_TOOLTIP__E} attribute of the given
	 * {@link Edge} to the given provider.
	 *
	 * @param edge
	 *            The {@link Edge} whose attribute is change.
	 * @param tooltipProvider
	 *            A {@link Provider} which is used to retrieve the
	 *            {@link #TARGET_LABEL_TOOLTIP__E} value.
	 * @since 5.1
	 */
	public static void setTargetLabelTooltip(Edge edge, Provider<String> tooltipProvider) {
		if (tooltipProvider == null) {
			edge.attributesProperty().remove(TARGET_LABEL_TOOLTIP__E);
		} else {
			edge.attributesProperty().put(TARGET_LABEL_TOOLTIP__E, tooltipProvider);
		}
	}

	/**
	 * Sets the value of the {@link #TARGET_LABEL_TOOLTIP__E} attribute of the given
	 * {@link Edge} to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the target label tooltip is changed.
	 * @param tooltip
	 *            The new tooltip for the given {@link Edge} target label.
	 * @since 5.1
	 */
	public static void setTargetLabelTooltip(Edge edge, String tooltip) {
		if (tooltip == null) {
			edge.attributesProperty().remove(TARGET_LABEL_TOOLTIP__E);
		} else {
			edge.attributesProperty().put(TARGET_LABEL_TOOLTIP__E, tooltip);
		}
	}

	/**
	 * Sets the value of the {@link #TOOLTIP__E} attribute of the given {@link Edge}
	 * to the given provider.
	 *
	 * @param edge
	 *            The {@link Edge} whose attribute is change.
	 * @param tooltipProvider
	 *            A {@link Provider} which is used to retrieve the
	 *            {@link #TOOLTIP__E} value.
	 * @since 5.1
	 */
	public static void setTooltip(Edge edge, Provider<String> tooltipProvider) {
		if (tooltipProvider == null) {
			edge.attributesProperty().remove(TOOLTIP__E);
		} else {
			edge.attributesProperty().put(TOOLTIP__E, tooltipProvider);
		}
	}

	/**
	 * Sets the value of the {@link #TOOLTIP__E} attribute of the given {@link Edge}
	 * to the given value.
	 *
	 * @param edge
	 *            The {@link Edge} of which the tooltip is changed.
	 * @param tooltip
	 *            The new tooltip for the given {@link Edge}.
	 * @since 5.1
	 */
	public static void setTooltip(Edge edge, String tooltip) {
		if (tooltip == null) {
			edge.attributesProperty().remove(TOOLTIP__E);
		} else {
			edge.attributesProperty().put(TOOLTIP__E, tooltip);
		}
	}

	/**
	 * Sets the value of the {@link #TOOLTIP__N} attribute of the given {@link Node}
	 * to the given provider.
	 *
	 * @param node
	 *            The {@link Node} whose attribute is change.
	 * @param tooltipProvider
	 *            A {@link Provider} which is used to retrieve the
	 *            {@link #TOOLTIP__N} value.
	 */
	public static void setTooltip(Node node, Provider<String> tooltipProvider) {
		if (tooltipProvider == null) {
			node.attributesProperty().remove(TOOLTIP__N);
		} else {
			node.attributesProperty().put(TOOLTIP__N, tooltipProvider);
		}
	}

	/**
	 * Sets the value of the {@link #TOOLTIP__N} attribute of the given {@link Node}
	 * to the given value.
	 *
	 * @param node
	 *            The {@link Node} of which the tooltip is changed.
	 * @param tooltip
	 *            The new tooltip for the given {@link Node}.
	 */
	public static void setTooltip(Node node, String tooltip) {
		if (tooltip == null) {
			node.attributesProperty().remove(TOOLTIP__N);
		} else {
			node.attributesProperty().put(TOOLTIP__N, tooltip);
		}
	}
}

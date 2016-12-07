/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *     Tamas Miklossy (itemis AG)   - Add support for arrowType edge decorations (bug #477980)
 *                                  - Add support for polygon-based node shapes (bug #441352)
 *                                  - Add support for all dot attributes (bug #461506)
 *******************************************************************************/
package org.eclipse.gef.dot.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.dot.internal.language.DotStandaloneSetup;
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowType;
import org.eclipse.gef.dot.internal.language.clustermode.ClusterMode;
import org.eclipse.gef.dot.internal.language.color.Color;
import org.eclipse.gef.dot.internal.language.dir.DirType;
import org.eclipse.gef.dot.internal.language.dot.AttrStmt;
import org.eclipse.gef.dot.internal.language.dot.Attribute;
import org.eclipse.gef.dot.internal.language.dot.AttributeType;
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode;
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtSubgraph;
import org.eclipse.gef.dot.internal.language.dot.GraphType;
import org.eclipse.gef.dot.internal.language.dot.NodeStmt;
import org.eclipse.gef.dot.internal.language.dot.Subgraph;
import org.eclipse.gef.dot.internal.language.layout.Layout;
import org.eclipse.gef.dot.internal.language.outputmode.OutputMode;
import org.eclipse.gef.dot.internal.language.pagedir.Pagedir;
import org.eclipse.gef.dot.internal.language.point.Point;
import org.eclipse.gef.dot.internal.language.rankdir.Rankdir;
import org.eclipse.gef.dot.internal.language.shape.Shape;
import org.eclipse.gef.dot.internal.language.splines.Splines;
import org.eclipse.gef.dot.internal.language.splinetype.SplineType;
import org.eclipse.gef.dot.internal.language.style.Style;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.language.terminals.ID.Type;
import org.eclipse.gef.dot.internal.language.validation.DotJavaValidator;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.xtext.EcoreUtil2;

import com.google.inject.Inject;

/**
 * The {@link DotAttributes} class contains all attributes which are supported
 * by {@link DotImport} and {@link DotExport}.
 * 
 * @author mwienand
 * @author anyssen
 *
 */
public class DotAttributes {

	@Inject
	private static DotJavaValidator dotValidator;

	/**
	 * Specifies the name of a graph, node, or edge (not an attribute), as
	 * retrieved through the graph, node_id, as well as edge_stmt and edgeRHS
	 * grammar rules.
	 */
	public static final String _NAME__GNE = "_name";

	/**
	 * Specifies the graph type.
	 */
	public static final String _TYPE__G = "_type";

	/**
	 * Specifies the 'arrowhead' attribute of an edge.
	 */
	public static final String ARROWHEAD__E = "arrowhead";

	/**
	 * Specifies the 'arrowsize' attribute of an edge.
	 */
	public static final String ARROWSIZE__E = "arrowsize";

	/**
	 * Specifies the 'arrowtail' attribute of an edge.
	 */
	public static final String ARROWTAIL__E = "arrowtail";

	/**
	 * Specifies the 'bgcolor' attribute of a graph.
	 */
	public static final String BGCOLOR__G = "bgcolor";

	/**
	 * Specifies the 'clusterrank' attribute of a graph.
	 */
	public static final String CLUSTERRANK__G = "clusterrank";

	/**
	 * Specifies the 'color' attribute of a node or edge.
	 */
	public static final String COLOR__NE = "color";

	/**
	 * Specifies the 'colorscheme' attribute of a graph, node or edge.
	 */
	public static final String COLORSCHEME__GNE = "colorscheme";

	/**
	 * Specifies the 'dir' attribute of an edge.
	 */
	public static final String DIR__E = "dir";

	/**
	 * Specifies the 'distortion' attribute of a node.
	 */
	public static final String DISTORTION__N = "distortion";

	/**
	 * Specifies the 'fillcolor' attribute of a node or edge.
	 */
	public static final String FILLCOLOR__NE = "fillcolor";

	/**
	 * Specifies the 'fixedsize' attribute of a node.
	 */
	public static final String FIXEDSIZE__N = "fixedsize";

	/**
	 * Specifies the 'fontcolor' attribute of a graph, node or edge.
	 */
	public static final String FONTCOLOR__GNE = "fontcolor";

	/**
	 * Specifies the 'forcelabels' attribute of a graph.
	 */
	public static final String FORCELABELS__G = "forcelabels";

	/**
	 * Specifies the 'head_lp' attribute (head label position) of an edge.
	 */
	public static final String HEAD_LP__E = "head_lp";

	/**
	 * Specifies the 'headlabel' attribute of an edge.
	 */
	public static final String HEADLABEL__E = "headlabel";

	/**
	 * Specifies the 'height' attribute of a node.
	 */
	public static final String HEIGHT__N = "height";

	/**
	 * Specifies the 'id' attribute of a graph, node or edge.
	 */
	public static final String ID__GNE = "id";

	/**
	 * Specifies the 'label' attribute of a graph, node or edge.
	 */
	public static final String LABEL__GNE = "label";

	/**
	 * Specifies the 'labelfontcolor' attribute of an edge.
	 */
	public static final String LABELFONTCOLOR__E = "labelfontcolor";

	/**
	 * Specifies the 'layout' attribute of a graph.
	 */
	public static final String LAYOUT__G = "layout";

	/**
	 * Specifies the 'lp' attribute (label position) of a graph or edge.
	 */
	public static final String LP__GE = "lp";

	/**
	 * Specifies the 'outputorder' attribute of a graph.
	 */
	public static final String OUTPUTORDER__G = "outputorder";

	/**
	 * Specifies the 'pagedir' attribute of a graph.
	 */
	public static final String PAGEDIR__G = "pagedir";

	/**
	 * Specifies the 'pos' attribute of a node or edge.
	 */
	public static final String POS__NE = "pos";

	/**
	 * Specifies the 'rankdir' attribute which is passed to the layout algorithm
	 * which is used for laying out the graph.
	 */
	public static final String RANKDIR__G = "rankdir";

	/**
	 * Specifies the 'shape' attribute of a node.
	 */
	public static final String SHAPE__N = "shape";

	/**
	 * Specifies the 'sides' attribute of a node.
	 */
	public static final String SIDES__N = "sides";

	/**
	 * Specifies the 'skew' attribute of a node.
	 */
	public static final String SKEW__N = "skew";

	/**
	 * Specifies the 'splines' attribute of a graph. It is used to control how
	 * the edges are to be rendered.
	 */
	public static final String SPLINES__G = "splines";

	/**
	 * Specifies the 'style' attribute of a graph, node or edge.
	 */
	public static final String STYLE__GNE = "style";

	/**
	 * Specifies the 'tail_lp' attribute (tail label position) of an edge.
	 */
	public static final String TAIL_LP__E = "tail_lp";

	/**
	 * Specifies the 'taillabel' attribute of an edge.
	 */
	public static final String TAILLABEL__E = "taillabel";

	/**
	 * Specifies the 'width' attribute of a node.
	 */
	public static final String WIDTH__N = "width";

	/**
	 * Specifies the 'xlabel' attribute (external label) of a node or edge.
	 */
	public static final String XLABEL__NE = "xlabel";

	/**
	 * Specifies the 'xlp' attribute (external label position) of a node or
	 * edge.
	 */
	public static final String XLP__NE = "xlp";

	/**
	 * Returns the value of the {@link #_NAME__GNE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @return The value of the {@link #_NAME__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static ID _getNameRaw(Graph graph) {
		return (ID) graph.attributesProperty().get(_NAME__GNE);
	}

	/**
	 * Returns the value of the {@link #_NAME__GNE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @return The value of the {@link #_NAME__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static String _getName(Graph graph) {
		ID _nameRaw = _getNameRaw(graph);
		return _nameRaw != null ? _nameRaw.toValue() : null;
	}

	/**
	 * Returns the value of the {@link #_NAME__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @return The value of the {@link #_NAME__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static ID _getNameRaw(Node node) {
		return (ID) node.attributesProperty().get(_NAME__GNE);
	}

	/**
	 * Returns the value of the {@link #_NAME__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @return The value of the {@link #_NAME__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static String _getName(Node node) {
		ID _nameRaw = _getNameRaw(node);
		return _nameRaw != null ? _nameRaw.toValue() : null;
	}

	/**
	 * Returns the value of the {@link #_TYPE__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #_TYPE__G} attribute.
	 * @return The value of the {@link #_TYPE__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static GraphType _getType(Graph graph) {
		return (GraphType) graph.attributesProperty().get(_TYPE__G);
	}

	/**
	 * Sets the {@link #_NAME__GNE} attribute of the given {@link Graph} to the
	 * given <i>name</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @param name
	 *            The new value for the {@link #_NAME__GNE} attribute.
	 */
	public static void _setNameRaw(Graph graph, ID name) {
		graph.attributesProperty().put(_NAME__GNE, name);
	}

	/**
	 * Sets the {@link #_NAME__GNE} attribute of the given {@link Graph} to the
	 * given <i>name</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @param name
	 *            The new value for the {@link #_NAME__GNE} attribute.
	 */
	public static void _setName(Graph graph, String name) {
		_setNameRaw(graph, ID.fromValue(name));
	}

	/**
	 * Sets the {@link #_NAME__GNE} attribute of the given {@link Node} to the
	 * given <i>name</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @param name
	 *            The new value for the {@link #_NAME__GNE} attribute.
	 */
	public static void _setNameRaw(Node node, ID name) {
		node.attributesProperty().put(_NAME__GNE, name);
	}

	/**
	 * Sets the {@link #_NAME__GNE} attribute of the given {@link Node} to the
	 * given <i>name</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @param name
	 *            The new value for the {@link #_NAME__GNE} attribute.
	 */
	public static void _setName(Node node, String name) {
		_setNameRaw(node, ID.fromValue(name));
	}

	/**
	 * Sets the {@link #_TYPE__G} attribute of the given {@link Graph} to the
	 * given <i>type</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #_TYPE__G} attribute.
	 * @param type
	 *            The new value for the {@link #_TYPE__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>type</i> value is not supported.
	 */
	public static void _setType(Graph graph, GraphType type) {
		graph.attributesProperty().put(_TYPE__G, type);
	}

	private static List<Diagnostic> filter(List<Diagnostic> diagnostics,
			int severity) {
		List<Diagnostic> filtered = new ArrayList<>();
		for (Diagnostic d : diagnostics) {
			if (d.getSeverity() >= severity) {
				filtered.add(d);
			}
		}
		return filtered;
	}

	/**
	 * Returns the value of the {@link #ARROWHEAD__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWHEAD__E} attribute.
	 * @return The value of the {@link #ARROWHEAD__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getArrowHeadRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(ARROWHEAD__E);
	}

	/**
	 * Returns the value of the {@link #ARROWHEAD__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWHEAD__E} attribute.
	 * @return The value of the {@link #ARROWHEAD__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getArrowHead(Edge edge) {
		ID arrowHeadRaw = getArrowHeadRaw(edge);
		return arrowHeadRaw != null ? arrowHeadRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #ARROWHEAD__E} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWHEAD__E} attribute, parsed as an
	 *            {@link ArrowType} .
	 * @return The value of the {@link #ARROWHEAD__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static ArrowType getArrowHeadParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.ARROWTYPE_PARSER, getArrowHead(edge));
	}

	/**
	 * Returns the value of the {@link #ARROWSIZE__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWSIZE__E} attribute.
	 * @return The value of the {@link #ARROWSIZE__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getArrowSizeRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(ARROWSIZE__E);
	}

	/**
	 * Returns the (parsed) value of the {@link #ARROWSIZE__E} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWSIZE__E} attribute, parsed as a {@link Double}.
	 * @return The value of the {@link #ARROWSIZE__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getArrowSize(Edge edge) {
		ID arrowSize = getArrowSizeRaw(edge);
		return arrowSize != null ? arrowSize.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #ARROWSIZE__E} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWSIZE__E} attribute, parsed as a {@link Double}.
	 * @return The value of the {@link #ARROWSIZE__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static Double getArrowSizeParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.DOUBLE_PARSER, getArrowSize(edge));
	}

	/**
	 * Returns the value of the {@link #ARROWTAIL__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWTAIL__E} attribute.
	 * @return The value of the {@link #ARROWTAIL__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getArrowTailRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(ARROWTAIL__E);
	}

	/**
	 * Returns the value of the {@link #ARROWTAIL__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWTAIL__E} attribute.
	 * @return The value of the {@link #ARROWTAIL__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getArrowTail(Edge edge) {
		ID arrowTail = getArrowTailRaw(edge);
		return arrowTail != null ? arrowTail.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #ARROWTAIL__E} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWTAIL__E} attribute, parsed as an
	 *            {@link ArrowType} .
	 * @return The value of the {@link #ARROWTAIL__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static ArrowType getArrowTailParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.ARROWTYPE_PARSER, getArrowTail(edge));
	}

	/**
	 * Returns the value of the {@link #BGCOLOR__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #BGCOLOR__G} attribute.
	 * @return The value of the {@link #BGCOLOR__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static ID getBgColorRaw(Graph graph) {
		return (ID) graph.attributesProperty().get(BGCOLOR__G);
	}

	/**
	 * Returns the value of the {@link #BGCOLOR__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #BGCOLOR__G} attribute.
	 * @return The value of the {@link #BGCOLOR__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getBgColor(Graph graph) {
		ID bgColorRaw = getBgColorRaw(graph);
		return bgColorRaw != null ? bgColorRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #BGCOLOR__G} attribute of the
	 * given {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #BGCOLOR__G} attribute, parsed as a {@link Color}.
	 * @return The value of the {@link #BGCOLOR__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static Color getBgColorParsed(Graph graph) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.COLOR_PARSER, getBgColor(graph));
	}

	/**
	 * Returns the value of the {@link #CLUSTERRANK__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #CLUSTERRANK__G} attribute.
	 * @return The value of the {@link #CLUSTERRANK__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static ID getClusterRankRaw(Graph graph) {
		return (ID) graph.attributesProperty().get(CLUSTERRANK__G);
	}

	/**
	 * Returns the value of the {@link #CLUSTERRANK__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #CLUSTERRANK__G} attribute.
	 * @return The value of the {@link #CLUSTERRANK__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getClusterRank(Graph graph) {
		ID clusterRankRaw = getClusterRankRaw(graph);
		return clusterRankRaw != null ? clusterRankRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #CLUSTERRANK__G} attribute of
	 * the given {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #CLUSTERRANK__G} attribute, parsed as a
	 *            {@link ClusterMode}.
	 * @return The value of the {@link #CLUSTERRANK__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static ClusterMode getClusterRankParsed(Graph graph) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.CLUSTERMODE_PARSER, getClusterRank(graph));
	}

	/**
	 * Returns the value of the {@link #COLOR__NE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #COLOR__NE} attribute.
	 * @return The value of the {@link #COLOR__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getColorRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(COLOR__NE);
	}

	/**
	 * Returns the value of the {@link #COLOR__NE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #COLOR__NE} attribute.
	 * @return The value of the {@link #COLOR__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getColor(Edge edge) {
		ID colorRaw = getColorRaw(edge);
		return colorRaw != null ? colorRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #COLOR__NE} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #COLOR__NE} attribute, parsed as a {@link Color}.
	 * @return The value of the {@link #COLOR__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static Color getColorParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.COLOR_PARSER, getColor(edge));
	}

	/**
	 * Returns the value of the {@link #COLOR__NE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #COLOR__NE} attribute.
	 * @return The value of the {@link #COLOR__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getColorRaw(Node node) {
		return (ID) node.attributesProperty().get(COLOR__NE);
	}

	/**
	 * Returns the value of the {@link #COLOR__NE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #COLOR__NE} attribute.
	 * @return The value of the {@link #COLOR__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static String getColor(Node node) {
		ID colorRaw = getColorRaw(node);
		return colorRaw != null ? colorRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #COLOR__NE} attribute of the
	 * given {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #COLOR__NE} attribute, parsed as a {@link Color}.
	 * @return The value of the {@link #COLOR__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static Color getColorParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.COLOR_PARSER, getColor(node));
	}

	/**
	 * Returns the value of the {@link #COLORSCHEME__GNE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @return The value of the {@link #COLORSCHEME__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getColorSchemeRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(COLORSCHEME__GNE);
	}

	/**
	 * Returns the value of the {@link #COLORSCHEME__GNE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @return The value of the {@link #COLORSCHEME__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getColorScheme(Edge edge) {
		ID colorSchemeRaw = getColorSchemeRaw(edge);
		return colorSchemeRaw != null ? colorSchemeRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #COLORSCHEME__GNE} attribute of
	 * the given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @return The value of the {@link #COLORSCHEME__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getColorSchemeParsed(Edge edge) {
		// TODO: use LblString parser
		return getColorScheme(edge);
	}

	/**
	 * Returns the value of the {@link #COLORSCHEME__GNE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @return The value of the {@link #COLORSCHEME__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static ID getColorSchemeRaw(Graph graph) {
		return (ID) graph.attributesProperty().get(COLORSCHEME__GNE);
	}

	/**
	 * Returns the value of the {@link #COLORSCHEME__GNE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @return The value of the {@link #COLORSCHEME__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getColorScheme(Graph graph) {
		ID colorSchemeRaw = getColorSchemeRaw(graph);
		return colorSchemeRaw != null ? colorSchemeRaw.toValue() : null;
	}

	/**
	 * Returns the value of the {@link #COLORSCHEME__GNE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @return The value of the {@link #COLORSCHEME__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getColorSchemeParsed(Graph graph) {
		// TODO: use LblString parser
		return getColorScheme(graph);
	}

	/**
	 * Returns the value of the {@link #COLORSCHEME__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @return The value of the {@link #COLORSCHEME__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getColorSchemeRaw(Node node) {
		return (ID) node.attributesProperty().get(COLORSCHEME__GNE);
	}

	/**
	 * Returns the value of the {@link #COLORSCHEME__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @return The value of the {@link #COLORSCHEME__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static String getColorScheme(Node node) {
		ID colorSchemeRaw = getColorSchemeRaw(node);
		return colorSchemeRaw != null ? colorSchemeRaw.toValue() : null;
	}

	/**
	 * Returns the value of the {@link #COLORSCHEME__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @return The value of the {@link #COLORSCHEME__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static String getColorSchemeParsed(Node node) {
		// TODO: use LblString parser
		return getColorScheme(node);
	}

	/**
	 * Returns the value of the {@link #DIR__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #DIR__E} attribute.
	 * @return The value of the {@link #DIR__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getDirRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(DIR__E);
	}

	/**
	 * Returns the value of the {@link #DIR__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #DIR__E} attribute.
	 * @return The value of the {@link #DIR__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getDir(Edge edge) {
		ID dirRaw = getDirRaw(edge);
		return dirRaw != null ? dirRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #DIR__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #DIR__E} attribute, parsed as a {@link DirType}.
	 * @return The value of the {@link #DIR__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static DirType getDirParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.DIRTYPE_PARSER, getDir(edge));
	}

	/**
	 * Returns the value of the {@link #DISTORTION__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #DISTORTION__N} attribute.
	 * @return The value of the {@link #DISTORTION__N} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getDistortionRaw(Node node) {
		return (ID) node.attributesProperty().get(DISTORTION__N);
	}

	/**
	 * Returns the value of the {@link #DISTORTION__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #DISTORTION__N} attribute.
	 * @return The value of the {@link #DISTORTION__N} attribute of the given
	 *         {@link Node}.
	 */
	public static String getDistortion(Node node) {
		ID distortionRaw = getDistortionRaw(node);
		return distortionRaw != null ? distortionRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #DISTORTION__N} attribute of the
	 * given {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #DISTORTION__N} attribute, parsed as a {@link Double}.
	 * @return The value of the {@link #DISTORTION__N} attribute of the given
	 *         {@link Node}.
	 */
	public static Double getDistortionParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.DOUBLE_PARSER, getDistortion(node));
	}

	/**
	 * Returns the value of the {@link #FILLCOLOR__NE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #FILLCOLOR__NE} attribute.
	 * @return The value of the {@link #FILLCOLOR__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getFillColorRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(FILLCOLOR__NE);
	}

	/**
	 * Returns the value of the {@link #FILLCOLOR__NE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #FILLCOLOR__NE} attribute.
	 * @return The value of the {@link #FILLCOLOR__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getFillColor(Edge edge) {
		ID fillColorRaw = getFillColorRaw(edge);
		return fillColorRaw != null ? fillColorRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #FILLCOLOR__NE} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #FILLCOLOR__NE} attribute, parsed as a {@link Color}.
	 * @return The value of the {@link #FILLCOLOR__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static Color getFillColorParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.COLOR_PARSER, getFillColor(edge));
	}

	/**
	 * Returns the value of the {@link #FILLCOLOR__NE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #FILLCOLOR__NE} attribute.
	 * @return The value of the {@link #FILLCOLOR__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getFillColorRaw(Node node) {
		return (ID) node.attributesProperty().get(FILLCOLOR__NE);
	}

	/**
	 * Returns the value of the {@link #FILLCOLOR__NE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #FILLCOLOR__NE} attribute.
	 * @return The value of the {@link #FILLCOLOR__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static String getFillColor(Node node) {
		ID fillColorRaw = getFillColorRaw(node);
		return fillColorRaw != null ? fillColorRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #FILLCOLOR__NE} attribute of the
	 * given {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #FILLCOLOR__NE} attribute, parsed as a {@link Color}.
	 * @return The value of the {@link #FILLCOLOR__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static Color getFillColorParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.COLOR_PARSER, getFillColor(node));
	}

	/**
	 * Returns the value of the {@link #FIXEDSIZE__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #FIXEDSIZE__N} attribute.
	 * @return The value of the {@link #FIXEDSIZE__N} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getFixedSizeRaw(Node node) {
		return (ID) node.attributesProperty().get(FIXEDSIZE__N);
	}

	/**
	 * Returns the value of the {@link #FIXEDSIZE__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #FIXEDSIZE__N} attribute.
	 * @return The value of the {@link #FIXEDSIZE__N} attribute of the given
	 *         {@link Node}.
	 */
	public static String getFixedSize(Node node) {
		ID fixedSizeRaw = getFixedSizeRaw(node);
		return fixedSizeRaw != null ? fixedSizeRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #FIXEDSIZE__N} attribute of the
	 * given {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #FIXEDSIZE__N} attribute, parsed as a {@link Boolean}.
	 * @return The value of the {@link #FIXEDSIZE__N} attribute of the given
	 *         {@link Node}.
	 */
	public static Boolean getFixedSizeParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.BOOL_PARSER, getFixedSize(node));
	}

	/**
	 * Returns the value of the {@link #FONTCOLOR__GNE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @return The value of the {@link #FONTCOLOR__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getFontColorRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(FONTCOLOR__GNE);
	}

	/**
	 * Returns the value of the {@link #FONTCOLOR__GNE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @return The value of the {@link #FONTCOLOR__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getFontColor(Edge edge) {
		ID fontColorRaw = getFontColorRaw(edge);
		return fontColorRaw != null ? fontColorRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #FONTCOLOR__GNE} attribute of
	 * the given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #FONTCOLOR__GNE} attribute, parsed as a {@link Color}.
	 * @return The value of the {@link #FONTCOLOR__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static Color getFontColorParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.COLOR_PARSER, getFontColor(edge));
	}

	/**
	 * Returns the value of the {@link #FONTCOLOR__GNE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @return The value of the {@link #FONTCOLOR__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static ID getFontColorRaw(Graph graph) {
		return (ID) graph.attributesProperty().get(FONTCOLOR__GNE);
	}

	/**
	 * Returns the value of the {@link #FONTCOLOR__GNE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @return The value of the {@link #FONTCOLOR__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getFontColor(Graph graph) {
		ID fontColorRaw = getFontColorRaw(graph);
		return fontColorRaw != null ? fontColorRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #FONTCOLOR__GNE} attribute of
	 * the given {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #FONTCOLOR__GNE} attribute, parsed as a {@link Color}.
	 * @return The value of the {@link #FONTCOLOR__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static Color getFontColorParsed(Graph graph) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.COLOR_PARSER, getFontColor(graph));
	}

	/**
	 * Returns the value of the {@link #FONTCOLOR__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @return The value of the {@link #FONTCOLOR__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getFontColorRaw(Node node) {
		return (ID) node.attributesProperty().get(FONTCOLOR__GNE);
	}

	/**
	 * Returns the value of the {@link #FONTCOLOR__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @return The value of the {@link #FONTCOLOR__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static String getFontColor(Node node) {
		ID fontColorRaw = getFontColorRaw(node);
		return fontColorRaw != null ? fontColorRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #FONTCOLOR__GNE} attribute of
	 * the given {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #FONTCOLOR__GNE} attribute, parsed as a {@link Color}.
	 * @return The value of the {@link #FONTCOLOR__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static Color getFontColorParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.COLOR_PARSER, getFontColor(node));
	}

	/**
	 * Returns the value of the {@link #FORCELABELS__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #FORCELABELS__G} attribute.
	 * @return The value of the {@link #FORCELABELS__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static ID getForceLabelsRaw(Graph graph) {
		return (ID) graph.attributesProperty().get(FORCELABELS__G);
	}

	/**
	 * Returns the value of the {@link #FORCELABELS__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #FORCELABELS__G} attribute.
	 * @return The value of the {@link #FORCELABELS__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getForceLabels(Graph graph) {
		ID forceLabelsRaw = getForceLabelsRaw(graph);
		return forceLabelsRaw != null ? forceLabelsRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #FORCELABELS__G} attribute of
	 * the given {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #FORCELABELS__G} attribute, parsed as a {@link Boolean}
	 *            .
	 * @return The value of the {@link #FORCELABELS__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static Boolean getForceLabelsParsed(Graph graph) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.BOOL_PARSER, getForceLabels(graph));
	}

	private static String getFormattedDiagnosticMessage(
			List<Diagnostic> diagnostics) {
		StringBuilder sb = new StringBuilder();
		for (Diagnostic n : diagnostics) {
			String message = n.getMessage();
			if (!message.isEmpty()) {
				if (sb.length() != 0) {
					sb.append(" ");
				}
				sb.append(message);
			}
		}
		return sb.toString();
	}

	/**
	 * Returns the value of the {@link #HEADLABEL__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #HEADLABEL__E} attribute.
	 * @return The value of the {@link #HEADLABEL__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getHeadLabelRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(HEADLABEL__E);
	}

	/**
	 * Returns the value of the {@link #HEADLABEL__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #HEADLABEL__E} attribute.
	 * @return The value of the {@link #HEADLABEL__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getHeadLabel(Edge edge) {
		ID headLabelRaw = getHeadLabelRaw(edge);
		return headLabelRaw != null ? headLabelRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #HEADLABEL__E} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #HEADLABEL__E} attribute.
	 * @return The value of the {@link #HEADLABEL__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getHeadLabelParsed(Edge edge) {
		return getHeadLabel(edge);
	}

	/**
	 * Returns the value of the {@link #HEAD_LP__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #HEAD_LP__E} attribute.
	 * @return The value of the {@link #HEAD_LP__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getHeadLpRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(HEAD_LP__E);
	}

	/**
	 * Returns the value of the {@link #HEAD_LP__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #HEAD_LP__E} attribute.
	 * @return The value of the {@link #HEAD_LP__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getHeadLp(Edge edge) {
		ID headLpRaw = getHeadLpRaw(edge);
		return headLpRaw != null ? headLpRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #HEAD_LP__E} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #HEAD_LP__E} attribute, parsed as a {@link Point}.
	 * @return The value of the {@link #HEAD_LP__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static Point getHeadLpParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.POINT_PARSER, getHeadLp(edge));
	}

	/**
	 * Returns the value of the {@link #HEIGHT__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #HEIGHT__N} attribute.
	 * @return The value of the {@link #HEIGHT__N} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getHeightRaw(Node node) {
		return (ID) node.attributesProperty().get(HEIGHT__N);
	}

	/**
	 * Returns the value of the {@link #HEIGHT__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #HEIGHT__N} attribute.
	 * @return The value of the {@link #HEIGHT__N} attribute of the given
	 *         {@link Node}.
	 */
	public static String getHeight(Node node) {
		ID heightRaw = getHeightRaw(node);
		return heightRaw != null ? heightRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #HEIGHT__N} attribute of the
	 * given {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #HEIGHT__N} attribute, parsed as a {@link Double}.
	 * @return The value of the {@link #HEIGHT__N} attribute of the given
	 *         {@link Node}.
	 */
	public static Double getHeightParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.DOUBLE_PARSER, getHeight(node));
	}

	/**
	 * Returns the value of the {@link #ID__GNE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ID__GNE} attribute.
	 * @return The value of the {@link #ID__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getIdRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(ID__GNE);
	}

	/**
	 * Returns the value of the {@link #ID__GNE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ID__GNE} attribute.
	 * @return The value of the {@link #ID__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getId(Edge edge) {
		ID idRaw = getIdRaw(edge);
		return idRaw != null ? idRaw.toValue() : null;
	}

	/**
	 * Returns the (pared) value of the {@link #ID__GNE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ID__GNE} attribute.
	 * @return The value of the {@link #ID__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getIdParsed(Edge edge) {
		return getId(edge);
	}

	/**
	 * Returns the value of the {@link #ID__GNE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #ID__GNE} attribute.
	 * @return The value of the {@link #ID__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static ID getIdRaw(Graph graph) {
		return (ID) graph.attributesProperty().get(ID__GNE);
	}

	/**
	 * Returns the value of the {@link #ID__GNE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #ID__GNE} attribute.
	 * @return The value of the {@link #ID__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getId(Graph graph) {
		ID idRaw = getIdRaw(graph);
		return idRaw != null ? idRaw.toValue() : null;
	}

	/**
	 * Returns the value of the {@link #ID__GNE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #ID__GNE} attribute.
	 * @return The value of the {@link #ID__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getIdParsed(Graph graph) {
		return getId(graph);
	}

	/**
	 * Returns the value of the {@link #ID__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #ID__GNE} attribute.
	 * @return The value of the {@link #ID__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getIdRaw(Node node) {
		return (ID) node.attributesProperty().get(ID__GNE);
	}

	/**
	 * Returns the value of the {@link #ID__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #ID__GNE} attribute.
	 * @return The value of the {@link #ID__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static String getId(Node node) {
		ID idRaw = getIdRaw(node);
		return idRaw != null ? idRaw.toValue() : null;
	}

	/**
	 * Returns the value of the {@link #ID__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #ID__GNE} attribute.
	 * @return The value of the {@link #ID__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static String getIdParsed(Node node) {
		return getId(node);
	}

	/**
	 * Returns the value of the {@link #LABEL__GNE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @return The value of the {@link #LABEL__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getLabelRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(LABEL__GNE);
	}

	/**
	 * Returns the value of the {@link #LABEL__GNE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @return The value of the {@link #LABEL__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getLabel(Edge edge) {
		ID labelRaw = getLabelRaw(edge);
		return labelRaw != null ? labelRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #LABEL__GNE} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @return The value of the {@link #LABEL__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getLabelParsed(Edge edge) {
		return getLabel(edge);
	}

	/**
	 * Returns the value of the {@link #LABEL__GNE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @return The value of the {@link #LABEL__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static ID getLabelRaw(Graph graph) {
		return (ID) graph.attributesProperty().get(LABEL__GNE);
	}

	/**
	 * Returns the value of the {@link #LABEL__GNE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @return The value of the {@link #LABEL__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getLabel(Graph graph) {
		ID labelRaw = getLabelRaw(graph);
		return labelRaw != null ? labelRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #LABEL__GNE} attribute of the
	 * given {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @return The value of the {@link #LABEL__GNE} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getLabelParsed(Graph graph) {
		return getLabel(graph);
	}

	/**
	 * Returns the value of the {@link #LABEL__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @return The value of the {@link #LABEL__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getLabelRaw(Node node) {
		return (ID) node.attributesProperty().get(LABEL__GNE);
	}

	/**
	 * Returns the value of the {@link #LABEL__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @return The value of the {@link #LABEL__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static String getLabel(Node node) {
		ID labelRaw = getLabelRaw(node);
		return labelRaw != null ? labelRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #LABEL__GNE} attribute of the
	 * given {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @return The value of the {@link #LABEL__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static String getLabelParsed(Node node) {
		return getLabel(node);
	}

	/**
	 * Returns the value of the {@link #LABELFONTCOLOR__E} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #LABELFONTCOLOR__E} attribute.
	 * @return The value of the {@link #LABELFONTCOLOR__E} attribute of the
	 *         given {@link Edge}.
	 */
	public static ID getLabelFontColorRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(LABELFONTCOLOR__E);
	}

	/**
	 * Returns the value of the {@link #LABELFONTCOLOR__E} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #LABELFONTCOLOR__E} attribute.
	 * @return The value of the {@link #LABELFONTCOLOR__E} attribute of the
	 *         given {@link Edge}.
	 */
	public static String getLabelFontColor(Edge edge) {
		ID labelFontColor = getLabelFontColorRaw(edge);
		return labelFontColor != null ? labelFontColor.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #LABELFONTCOLOR__E} attribute of
	 * the given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #LABELFONTCOLOR__E} attribute, parsed as a
	 *            {@link Color}.
	 * @return The value of the {@link #LABELFONTCOLOR__E} attribute of the
	 *         given {@link Edge}.
	 */
	public static Color getLabelFontColorParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.COLOR_PARSER, getLabelFontColor(edge));
	}

	/**
	 * Returns the value of the {@link #LAYOUT__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #LAYOUT__G} attribute.
	 * @return The value of the {@link #LAYOUT__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static ID getLayoutRaw(Graph graph) {
		return (ID) graph.attributesProperty().get(LAYOUT__G);
	}

	/**
	 * Returns the value of the {@link #LAYOUT__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #LAYOUT__G} attribute.
	 * @return The value of the {@link #LAYOUT__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getLayout(Graph graph) {
		ID layoutRaw = getLayoutRaw(graph);
		return layoutRaw != null ? layoutRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #LAYOUT__G} attribute of the
	 * given {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #LAYOUT__G} attribute, parsed as a {@link Layout}.
	 * @return The value of the {@link #LAYOUT__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static Layout getLayoutParsed(Graph graph) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.LAYOUT_PARSER, getLayout(graph));
	}

	/**
	 * Returns the value of the {@link #LP__GE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #LP__GE} attribute.
	 * @return The value of the {@link #LP__GE} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getLpRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(LP__GE);
	}

	/**
	 * Returns the value of the {@link #LP__GE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #LP__GE} attribute.
	 * @return The value of the {@link #LP__GE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getLp(Edge edge) {
		ID lpRaw = getLpRaw(edge);
		return lpRaw != null ? lpRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #LP__GE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #LP__GE} attribute, parsed as a {@link Point}.
	 * @return The value of the {@link #LP__GE} attribute of the given
	 *         {@link Edge}.
	 */
	public static Point getLpParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.POINT_PARSER, getLp(edge));
	}

	/**
	 * Returns the value of the {@link #LP__GE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #LP__GE} attribute.
	 * @return The value of the {@link #LP__GE} attribute of the given
	 *         {@link Graph}.
	 */
	public static ID getLpRaw(Graph graph) {
		return (ID) graph.attributesProperty().get(LP__GE);
	}

	/**
	 * Returns the value of the {@link #LP__GE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #LP__GE} attribute.
	 * @return The value of the {@link #LP__GE} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getLp(Graph graph) {
		ID lpRaw = getLpRaw(graph);
		return lpRaw != null ? lpRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #LP__GE} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #LP__GE} attribute, parsed as a {@link Point}.
	 * @return The value of the {@link #LP__GE} attribute of the given
	 *         {@link Graph}.
	 */
	public static Point getLpParsed(Graph graph) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.POINT_PARSER, getLp(graph));
	}

	/**
	 * Returns the value of the {@link #OUTPUTORDER__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #OUTPUTORDER__G} attribute.
	 * @return The value of the {@link #OUTPUTORDER__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static ID getOutputOrderRaw(Graph graph) {
		return (ID) graph.attributesProperty().get(OUTPUTORDER__G);
	}

	/**
	 * Returns the value of the {@link #OUTPUTORDER__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #OUTPUTORDER__G} attribute.
	 * @return The value of the {@link #OUTPUTORDER__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getOutputOrder(Graph graph) {
		ID outputOrderRaw = getOutputOrderRaw(graph);
		return outputOrderRaw != null ? outputOrderRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #OUTPUTORDER__G} attribute of
	 * the given {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #OUTPUTORDER__G} attribute, parsed as an
	 *            {@link OutputMode}.
	 * @return The value of the {@link #OUTPUTORDER__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static OutputMode getOutputOrderParsed(Graph graph) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.OUTPUTMODE_PARSER, getOutputOrder(graph));
	}

	/**
	 * Returns the value of the {@link #PAGEDIR__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #PAGEDIR__G} attribute.
	 * @return The value of the {@link #PAGEDIR__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static ID getPagedirRaw(Graph graph) {
		return (ID) graph.attributesProperty().get(PAGEDIR__G);
	}

	/**
	 * Returns the value of the {@link #PAGEDIR__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #PAGEDIR__G} attribute.
	 * @return The value of the {@link #PAGEDIR__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getPagedir(Graph graph) {
		ID pagedirRaw = getPagedirRaw(graph);
		return pagedirRaw != null ? pagedirRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #PAGEDIR__G} attribute of the
	 * given {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #PAGEDIR__G} attribute, parsed as a {@link Pagedir}.
	 * @return The value of the {@link #PAGEDIR__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static Pagedir getPagedirParsed(Graph graph) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.PAGEDIR_PARSER, getPagedir(graph));
	}

	/**
	 * Returns the value of the {@link #POS__NE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #POS__NE} attribute.
	 * @return The value of the {@link #POS__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getPosRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(POS__NE);
	}

	/**
	 * Returns the value of the {@link #POS__NE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #POS__NE} attribute.
	 * @return The value of the {@link #POS__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getPos(Edge edge) {
		ID posRaw = getPosRaw(edge);
		return posRaw != null ? posRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #POS__NE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #POS__NE} attribute, parsed as a {@link SplineType}.
	 * @return The value of the {@link #POS__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static SplineType getPosParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.SPLINETYPE_PARSER, getPos(edge));
	}

	/**
	 * Returns the value of the {@link #POS__NE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #POS__NE} attribute.
	 * @return The value of the {@link #POS__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getPosRaw(Node node) {
		return (ID) node.attributesProperty().get(POS__NE);
	}

	/**
	 * Returns the value of the {@link #POS__NE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #POS__NE} attribute.
	 * @return The value of the {@link #POS__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static String getPos(Node node) {
		ID posRaw = getPosRaw(node);
		return posRaw != null ? posRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #POS__NE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #POS__NE} attribute, parsed as a {@link Point}.
	 * @return The value of the {@link #POS__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static Point getPosParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.POINT_PARSER, getPos(node));
	}

	/**
	 * Returns the value of the {@link #RANKDIR__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #RANKDIR__G} attribute.
	 * @return The value of the {@link #RANKDIR__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static ID getRankdirRaw(Graph graph) {
		return (ID) graph.attributesProperty().get(RANKDIR__G);
	}

	/**
	 * Returns the value of the {@link #RANKDIR__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #RANKDIR__G} attribute.
	 * @return The value of the {@link #RANKDIR__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getRankdir(Graph graph) {
		ID rankdirRaw = getRankdirRaw(graph);
		return rankdirRaw != null ? rankdirRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #RANKDIR__G} attribute of the
	 * given {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #RANKDIR__G} attribute, parsed as a {@link Rankdir}.
	 * @return The value of the {@link #RANKDIR__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static Rankdir getRankdirParsed(Graph graph) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.RANKDIR_PARSER, getRankdir(graph));
	}

	/**
	 * Returns the value of the {@link #SHAPE__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #SHAPE__N} attribute.
	 * @return The value of the {@link #SHAPE__N} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getShapeRaw(Node node) {
		return (ID) node.attributesProperty().get(SHAPE__N);
	}

	/**
	 * Returns the value of the {@link #SHAPE__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #SHAPE__N} attribute.
	 * @return The value of the {@link #SHAPE__N} attribute of the given
	 *         {@link Node}.
	 */
	public static String getShape(Node node) {
		ID shapeRaw = getShapeRaw(node);
		return shapeRaw != null ? shapeRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #SHAPE__N} attribute of the
	 * given {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #SHAPE__N} attribute, parsed as a {@link Shape}.
	 * @return The value of the {@link #SHAPE__N} attribute of the given
	 *         {@link Node}.
	 */
	public static Shape getShapeParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.SHAPE_PARSER, getShape(node));
	}

	/**
	 * Returns the value of the {@link #SIDES__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #SIDES__N} attribute.
	 * @return The value of the {@link #SIDES__N} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getSidesRaw(Node node) {
		return (ID) node.attributesProperty().get(SIDES__N);
	}

	/**
	 * Returns the value of the {@link #SIDES__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #SIDES__N} attribute.
	 * @return The value of the {@link #SIDES__N} attribute of the given
	 *         {@link Node}.
	 */
	public static String getSides(Node node) {
		ID sidesRaw = getSidesRaw(node);
		return sidesRaw != null ? sidesRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #SIDES__N} attribute of the
	 * given {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #SIDES__N} attribute, parsed as an {@link Integer}.
	 * @return The value of the {@link #SIDES__N} attribute of the given
	 *         {@link Node}.
	 */
	public static Integer getSidesParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.INT_PARSER, getSides(node));
	}

	/**
	 * Returns the value of the {@link #SKEW__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #SKEW__N} attribute.
	 * @return The value of the {@link #SKEW__N} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getSkewRaw(Node node) {
		return (ID) node.attributesProperty().get(SKEW__N);
	}

	/**
	 * Returns the value of the {@link #SKEW__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #SKEW__N} attribute.
	 * @return The value of the {@link #SKEW__N} attribute of the given
	 *         {@link Node}.
	 */
	public static String getSkew(Node node) {
		ID skewRaw = getSkewRaw(node);
		return skewRaw != null ? skewRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #SKEW__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #SKEW__N} attribute, parsed as a {@link Double}.
	 * @return The value of the {@link #SKEW__N} attribute of the given
	 *         {@link Node}.
	 */
	public static Double getSkewParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.DOUBLE_PARSER, getSkew(node));
	}

	/**
	 * Returns the value of the {@link #SPLINES__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #SPLINES__G} attribute.
	 * @return The value of the {@link #SPLINES__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static ID getSplinesRaw(Graph graph) {
		return (ID) graph.attributesProperty().get(SPLINES__G);
	}

	/**
	 * Returns the value of the {@link #SPLINES__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #SPLINES__G} attribute.
	 * @return The value of the {@link #SPLINES__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static String getSplines(Graph graph) {
		ID splinesRaw = getSplinesRaw(graph);
		return splinesRaw != null ? splinesRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #SPLINES__G} attribute of the
	 * given {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #SPLINES__G} attribute, parsed as a {@link Boolean}.
	 * @return The value of the {@link #SPLINES__G} attribute of the given
	 *         {@link Graph}.
	 */
	public static Splines getSplinesParsed(Graph graph) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.SPLINES_PARSER, getSplines(graph));
	}

	/**
	 * Returns the value of the {@link #STYLE__GNE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #STYLE__GNE} attribute.
	 * @return The value of the {@link #STYLE__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getStyleRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(STYLE__GNE);
	}

	/**
	 * Returns the value of the {@link #STYLE__GNE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #STYLE__GNE} attribute.
	 * @return The value of the {@link #STYLE__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getStyle(Edge edge) {
		ID styleRaw = getStyleRaw(edge);
		return styleRaw != null ? styleRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #STYLE__GNE} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #STYLE__GNE} attribute, parsed as a {@link Style}.
	 * @return The value of the {@link #STYLE__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static Style getStyleParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.STYLE_PARSER, getStyle(edge));
	}

	/**
	 * Returns the value of the {@link #STYLE__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #STYLE__GNE} attribute.
	 * @return The value of the {@link #STYLE__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getStyleRaw(Node node) {
		return (ID) node.attributesProperty().get(STYLE__GNE);
	}

	/**
	 * Returns the value of the {@link #STYLE__GNE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #STYLE__GNE} attribute.
	 * @return The value of the {@link #STYLE__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static String getStyle(Node node) {
		ID styleRaw = getStyleRaw(node);
		return styleRaw != null ? styleRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #STYLE__GNE} attribute of the
	 * given {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #STYLE__GNE} attribute, parsed as a {@link Style}.
	 * @return The value of the {@link #STYLE__GNE} attribute of the given
	 *         {@link Node}.
	 */
	public static Style getStyleParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.STYLE_PARSER, getStyle(node));
	}

	/**
	 * Returns the value of the {@link #TAILLABEL__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #TAILLABEL__E} attribute.
	 * @return The value of the {@link #TAILLABEL__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getTailLabelRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(TAILLABEL__E);
	}

	/**
	 * Returns the value of the {@link #TAILLABEL__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #TAILLABEL__E} attribute.
	 * @return The value of the {@link #TAILLABEL__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getTailLabel(Edge edge) {
		ID tailLabelParsed = getTailLabelRaw(edge);
		return tailLabelParsed != null ? tailLabelParsed.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #TAILLABEL__E} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #TAILLABEL__E} attribute.
	 * @return The value of the {@link #TAILLABEL__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getTailLabelParsed(Edge edge) {
		return getTailLabel(edge);
	}

	/**
	 * Returns the value of the {@link #TAIL_LP__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #TAIL_LP__E} attribute.
	 * @return The value of the {@link #TAIL_LP__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getTailLpRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(TAIL_LP__E);
	}

	/**
	 * Returns the value of the {@link #TAIL_LP__E} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #TAIL_LP__E} attribute.
	 * @return The value of the {@link #TAIL_LP__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getTailLp(Edge edge) {
		ID tailLpRaw = getTailLpRaw(edge);
		return tailLpRaw != null ? tailLpRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #TAIL_LP__E} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #TAIL_LP__E} attribute, parsed as a {@link Point}.
	 * @return The value of the {@link #TAIL_LP__E} attribute of the given
	 *         {@link Edge}.
	 */
	public static Point getTailLpParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.POINT_PARSER, getTailLp(edge));
	}

	/**
	 * Returns the value of the {@link #WIDTH__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #WIDTH__N} attribute.
	 * @return The value of the {@link #WIDTH__N} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getWidthRaw(Node node) {
		return (ID) node.attributesProperty().get(WIDTH__N);
	}

	/**
	 * Returns the value of the {@link #WIDTH__N} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #WIDTH__N} attribute.
	 * @return The value of the {@link #WIDTH__N} attribute of the given
	 *         {@link Node}.
	 */
	public static String getWidth(Node node) {
		ID widthRaw = getWidthRaw(node);
		return widthRaw != null ? widthRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #WIDTH__N} attribute of the
	 * given {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #WIDTH__N} attribute, parsed as a {@link Double}.
	 * @return The value of the {@link #WIDTH__N} attribute of the given
	 *         {@link Node}.
	 */
	public static Double getWidthParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.DOUBLE_PARSER, getWidth(node));
	}

	/**
	 * Returns the value of the {@link #XLABEL__NE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #XLABEL__NE} attribute.
	 * @return The value of the {@link #XLABEL__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getXLabelRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(XLABEL__NE);
	}

	/**
	 * Returns the value of the {@link #XLABEL__NE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #XLABEL__NE} attribute.
	 * @return The value of the {@link #XLABEL__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getXLabel(Edge edge) {
		ID xLabelRaw = getXLabelRaw(edge);
		return xLabelRaw != null ? xLabelRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #XLABEL__NE} attribute of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #XLABEL__NE} attribute.
	 * @return The value of the {@link #XLABEL__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getXLabelParsed(Edge edge) {
		return getXLabel(edge);
	}

	/**
	 * Returns the value of the {@link #XLABEL__NE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #XLABEL__NE} attribute.
	 * @return The value of the {@link #XLABEL__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getXLabelRaw(Node node) {
		return (ID) node.attributesProperty().get(XLABEL__NE);
	}

	/**
	 * Returns the value of the {@link #XLABEL__NE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #XLABEL__NE} attribute.
	 * @return The value of the {@link #XLABEL__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static String getXLabel(Node node) {
		ID xLabelRaw = getXLabelRaw(node);
		return xLabelRaw != null ? xLabelRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #XLABEL__NE} attribute of the
	 * given {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #XLABEL__NE} attribute.
	 * @return The value of the {@link #XLABEL__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static String getXLabelParsed(Node node) {
		return getXLabel(node);
	}

	/**
	 * Returns the value of the {@link #XLP__NE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #XLP__NE} attribute.
	 * @return The value of the {@link #XLP__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static ID getXlpRaw(Edge edge) {
		return (ID) edge.attributesProperty().get(XLP__NE);
	}

	/**
	 * Returns the value of the {@link #XLP__NE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #XLP__NE} attribute.
	 * @return The value of the {@link #XLP__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String getXlp(Edge edge) {
		ID xlpRaw = getXlpRaw(edge);
		return xlpRaw != null ? xlpRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #XLP__NE} attribute of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #XLP__NE} attribute, parsed as a {@link Point}.
	 * @return The value of the {@link #XLP__NE} attribute of the given
	 *         {@link Edge}.
	 */
	public static Point getXlpParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.POINT_PARSER, getXlp(edge));
	}

	/**
	 * Returns the value of the {@link #XLP__NE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #XLP__NE} attribute.
	 * @return The value of the {@link #XLP__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static ID getXlpRaw(Node node) {
		return (ID) node.attributesProperty().get(XLP__NE);
	}

	/**
	 * Returns the value of the {@link #XLP__NE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #XLP__NE} attribute.
	 * @return The value of the {@link #XLP__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static String getXlp(Node node) {
		ID xlpRaw = getXlpRaw(node);
		return xlpRaw != null ? xlpRaw.toValue() : null;
	}

	/**
	 * Returns the (parsed) value of the {@link #XLP__NE} attribute of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #XLP__NE} attribute, parsed as a {@link Point}.
	 * @return The value of the {@link #XLP__NE} attribute of the given
	 *         {@link Node}.
	 */
	public static Point getXlpParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.POINT_PARSER, getXlp(node));
	}

	/**
	 * Sets the {@link #ARROWHEAD__E} attribute of the given {@link Edge} to the
	 * given <i>arrowHead</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWHEAD__E} attribute.
	 * @param arrowHead
	 *            The new value for the {@link #ARROWHEAD__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>arrowHead</i> value is not supported.
	 */
	public static void setArrowHeadRaw(Edge edge, ID arrowHead) {
		validate(AttributeContext.EDGE, ARROWHEAD__E, arrowHead.toValue());
		edge.attributesProperty().put(ARROWHEAD__E, arrowHead);
	}

	/**
	 * Sets the {@link #ARROWHEAD__E} attribute of the given {@link Edge} to the
	 * given <i>arrowHead</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWHEAD__E} attribute.
	 * @param arrowHead
	 *            The new value for the {@link #ARROWHEAD__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>arrowHead</i> value is not supported.
	 */
	public static void setArrowHead(Edge edge, String arrowHead) {
		setArrowHeadRaw(edge, ID.fromValue(arrowHead, Type.STRING));
	}

	/**
	 * Sets the {@link #ARROWHEAD__E} attribute of the given {@link Edge} to the
	 * given <i>arrowHeadParsed</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWHEAD__E} attribute.
	 * @param arrowHeadParsed
	 *            The new value for the {@link #ARROWHEAD__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>arrowHeadParsed</i> value is not supported.
	 */
	public static void setArrowHeadParsed(Edge edge,
			ArrowType arrowHeadParsed) {
		setArrowHead(edge, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.ARROWTYPE_SERIALIZER, arrowHeadParsed));
	}

	/**
	 * Sets the {@link #ARROWSIZE__E} attribute of the given {@link Edge} to the
	 * given <i>arrowSize</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWSIZE__E} attribute.
	 * @param arrowSize
	 *            The new value for the {@link #ARROWSIZE__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>arrowSize</i> value is not supported.
	 */
	public static void setArrowSizeRaw(Edge edge, ID arrowSize) {
		validate(AttributeContext.EDGE, ARROWSIZE__E, arrowSize.toValue());
		edge.attributesProperty().put(ARROWSIZE__E, arrowSize);
	}

	/**
	 * Sets the {@link #ARROWSIZE__E} attribute of the given {@link Edge} to the
	 * given <i>arrowSize</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWSIZE__E} attribute.
	 * @param arrowSize
	 *            The new value for the {@link #ARROWSIZE__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>arrowSize</i> value is not supported.
	 */
	public static void setArrowSize(Edge edge, String arrowSize) {
		setArrowSizeRaw(edge, ID.fromValue(arrowSize, Type.NUMERAL));
	}

	/**
	 * Sets the {@link #ARROWSIZE__E} attribute of the given {@link Edge} to the
	 * given <i>arrowSizeParsed</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWSIZE__E} attribute.
	 * @param arrowSizeParsed
	 *            The new value for the {@link #ARROWSIZE__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>arrowSizeParsed</i> value is not supported.
	 */
	public static void setArrowSizeParsed(Edge edge, Double arrowSizeParsed) {
		setArrowSize(edge, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.DOUBLE_SERIALIZER, arrowSizeParsed));
	}

	/**
	 * Sets the {@link #ARROWTAIL__E} attribute of the given {@link Edge} to the
	 * given <i>arrowTail</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWTAIL__E} attribute.
	 * @param arrowTail
	 *            The new value for the {@link #ARROWTAIL__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>arrowTail</i> value is not supported.
	 */
	public static void setArrowTailRaw(Edge edge, ID arrowTail) {
		validate(AttributeContext.EDGE, ARROWTAIL__E, arrowTail.toValue());
		edge.attributesProperty().put(ARROWTAIL__E, arrowTail);
	}

	/**
	 * Sets the {@link #ARROWTAIL__E} attribute of the given {@link Edge} to the
	 * given <i>arrowTail</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWTAIL__E} attribute.
	 * @param arrowTail
	 *            The new value for the {@link #ARROWTAIL__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>arrowTail</i> value is not supported.
	 */
	public static void setArrowTail(Edge edge, String arrowTail) {
		setArrowTailRaw(edge, ID.fromValue(arrowTail, Type.STRING));
	}

	/**
	 * Sets the {@link #ARROWTAIL__E} attribute of the given {@link Edge} to the
	 * given <i>arrowTailParsed</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWTAIL__E} attribute.
	 * @param arrowTailParsed
	 *            The new value for the {@link #ARROWTAIL__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>arrowTailParsed</i> value is not supported.
	 */
	public static void setArrowTailParsed(Edge edge,
			ArrowType arrowTailParsed) {
		setArrowTail(edge, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.ARROWTYPE_SERIALIZER, arrowTailParsed));
	}

	/**
	 * Sets the {@link #BGCOLOR__G} attribute of the given {@link Graph} to the
	 * given <i>bgColor</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #BGCOLOR__G} attribute.
	 * @param bgColor
	 *            The new value for the {@link #BGCOLOR__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>bgColor</i> value is not supported.
	 */
	public static void setBgColorRaw(Graph graph, ID bgColor) {
		validate(AttributeContext.GRAPH, BGCOLOR__G, bgColor.toValue());
		graph.attributesProperty().put(BGCOLOR__G, bgColor);
	}

	/**
	 * Sets the {@link #BGCOLOR__G} attribute of the given {@link Graph} to the
	 * given <i>bgColor</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #BGCOLOR__G} attribute.
	 * @param bgColor
	 *            The new value for the {@link #BGCOLOR__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>bgColor</i> value is not supported.
	 */
	public static void setBgColor(Graph graph, String bgColor) {
		setBgColorRaw(graph, ID.fromValue(bgColor));
	}

	/**
	 * Sets the {@link #BGCOLOR__G} attribute of the given {@link Graph} to the
	 * given <i>bgColorParsed</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #BGCOLOR__G} attribute.
	 * @param bgColorParsed
	 *            The new value for the {@link #BGCOLOR__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>bgColorParsed</i> value is not supported.
	 */
	public static void setBgColorParsed(Graph graph, Color bgColorParsed) {
		setBgColor(graph, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.COLOR_SERIALIZER, bgColorParsed));
	}

	/**
	 * Sets the {@link #CLUSTERRANK__G} attribute of the given {@link Graph} to
	 * the given <i>clusterRank</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #CLUSTERRANK__G} attribute.
	 * @param clusterRank
	 *            The new value for the {@link #CLUSTERRANK__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>clusterRank</i> value is not supported.
	 */
	public static void setClusterRankRaw(Graph graph, ID clusterRank) {
		validate(AttributeContext.GRAPH, CLUSTERRANK__G, clusterRank.toValue());
		graph.attributesProperty().put(CLUSTERRANK__G, clusterRank);
	}

	/**
	 * Sets the {@link #CLUSTERRANK__G} attribute of the given {@link Graph} to
	 * the given <i>clusterRank</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #CLUSTERRANK__G} attribute.
	 * @param clusterRank
	 *            The new value for the {@link #CLUSTERRANK__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>clusterRank</i> value is not supported.
	 */
	public static void setClusterRank(Graph graph, String clusterRank) {
		setClusterRankRaw(graph, ID.fromValue(clusterRank, Type.STRING));
	}

	/**
	 * Sets the {@link #CLUSTERRANK__G} attribute of the given {@link Graph} to
	 * the given <i>clusterRankParsed</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #CLUSTERRANK__G} attribute.
	 * @param clusterRankParsed
	 *            The new value for the {@link #CLUSTERRANK__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>clusterRankParsed</i> value is not
	 *             supported.
	 */
	public static void setClusterRankParsed(Graph graph,
			ClusterMode clusterRankParsed) {
		setClusterRank(graph, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.CLUSTERMODE_SERIALIZER, clusterRankParsed));
	}

	/**
	 * Sets the {@link #COLOR__NE} attribute of the given {@link Edge} to the
	 * given <i>color</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #COLOR__NE} attribute.
	 * @param color
	 *            The new value for the {@link #COLOR__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>color</i> value is not supported.
	 */
	public static void setColorRaw(Edge edge, ID color) {
		validate(AttributeContext.EDGE, COLOR__NE, color.toValue());
		edge.attributesProperty().put(COLOR__NE, color);
	}

	/**
	 * Sets the {@link #COLOR__NE} attribute of the given {@link Edge} to the
	 * given <i>color</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #COLOR__NE} attribute.
	 * @param color
	 *            The new value for the {@link #COLOR__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>color</i> value is not supported.
	 */
	public static void setColor(Edge edge, String color) {
		setColorRaw(edge, ID.fromValue(color));
	}

	/**
	 * Sets the {@link #COLOR__NE} attribute of the given {@link Edge} to the
	 * given <i>colorParsed</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #COLOR__NE} attribute.
	 * @param colorParsed
	 *            The new value for the {@link #COLOR__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>colorParsed</i> value is not supported.
	 */
	public static void setColorParsed(Edge edge, Color colorParsed) {
		setColor(edge, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.COLOR_SERIALIZER, colorParsed));
	}

	/**
	 * Sets the {@link #COLOR__NE} attribute of the given {@link Node} to the
	 * given <i>color</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #COLOR__NE} attribute.
	 * @param color
	 *            The new value for the {@link #COLOR__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>color</i> value is not supported.
	 */
	public static void setColorRaw(Node node, ID color) {
		validate(AttributeContext.NODE, COLOR__NE, color.toValue());
		node.attributesProperty().put(COLOR__NE, color);
	}

	/**
	 * Sets the {@link #COLOR__NE} attribute of the given {@link Node} to the
	 * given <i>color</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #COLOR__NE} attribute.
	 * @param color
	 *            The new value for the {@link #COLOR__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>color</i> value is not supported.
	 */
	public static void setColor(Node node, String color) {
		setColorRaw(node, ID.fromValue(color));
	}

	/**
	 * Sets the {@link #COLOR__NE} attribute of the given {@link Node} to the
	 * given <i>colorParsed</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #COLOR__NE} attribute.
	 * @param colorParsed
	 *            The new value for the {@link #COLOR__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>colorParsed</i> value is not supported.
	 */
	public static void setColorParsed(Node node, Color colorParsed) {
		setColor(node, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.COLOR_SERIALIZER, colorParsed));
	}

	/**
	 * Sets the {@link #COLORSCHEME__GNE} attribute of the given {@link Edge} to
	 * the given <i>colorScheme</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @param colorScheme
	 *            The new value for the {@link #COLORSCHEME__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>colorScheme</i> value is not supported.
	 */
	public static void setColorSchemeRaw(Edge edge, ID colorScheme) {
		validate(AttributeContext.EDGE, COLORSCHEME__GNE,
				colorScheme.toValue());
		edge.attributesProperty().put(COLORSCHEME__GNE, colorScheme);
	}

	/**
	 * Sets the {@link #COLORSCHEME__GNE} attribute of the given {@link Edge} to
	 * the given <i>colorScheme</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @param colorScheme
	 *            The new value for the {@link #COLORSCHEME__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>colorScheme</i> value is not supported.
	 */
	public static void setColorScheme(Edge edge, String colorScheme) {
		setColorSchemeRaw(edge, ID.fromValue(colorScheme));
	}

	/**
	 * Sets the {@link #COLORSCHEME__GNE} attribute of the given {@link Edge} to
	 * the given <i>colorScheme</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @param colorSchemeParsed
	 *            The new value for the {@link #COLORSCHEME__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>colorScheme</i> value is not supported.
	 */
	public static void setColorSchemeParsed(Edge edge,
			String colorSchemeParsed) {
		setColorScheme(edge, colorSchemeParsed);
	}

	/**
	 * Sets the {@link #COLORSCHEME__GNE} attribute of the given {@link Graph}
	 * to the given <i>colorScheme</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @param colorScheme
	 *            The new value for the {@link #COLORSCHEME__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>colorScheme</i> value is not supported.
	 */
	public static void setColorSchemeRaw(Graph graph, ID colorScheme) {
		validate(AttributeContext.GRAPH, COLORSCHEME__GNE,
				colorScheme.toValue());
		graph.attributesProperty().put(COLORSCHEME__GNE, colorScheme);
	}

	/**
	 * Sets the {@link #COLORSCHEME__GNE} attribute of the given {@link Graph}
	 * to the given <i>colorScheme</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @param colorScheme
	 *            The new value for the {@link #COLORSCHEME__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>colorScheme</i> value is not supported.
	 */
	public static void setColorScheme(Graph graph, String colorScheme) {
		setColorSchemeRaw(graph, ID.fromValue(colorScheme));
	}

	/**
	 * Sets the {@link #COLORSCHEME__GNE} attribute of the given {@link Graph}
	 * to the given <i>colorScheme</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @param colorSchemeParsed
	 *            The new value for the {@link #COLORSCHEME__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>colorScheme</i> value is not supported.
	 */
	public static void setColorSchemeParsed(Graph graph,
			String colorSchemeParsed) {
		setColorScheme(graph, colorSchemeParsed);
	}

	/**
	 * Sets the {@link #COLORSCHEME__GNE} attribute of the given {@link Node} to
	 * the given <i>colorScheme</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @param colorScheme
	 *            The new value for the {@link #COLORSCHEME__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>colorScheme</i> value is not supported.
	 */
	public static void setColorSchemeRaw(Node node, ID colorScheme) {
		validate(AttributeContext.NODE, COLORSCHEME__GNE,
				colorScheme.toValue());
		node.attributesProperty().put(COLORSCHEME__GNE, colorScheme);
	}

	/**
	 * Sets the {@link #COLORSCHEME__GNE} attribute of the given {@link Node} to
	 * the given <i>colorScheme</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @param colorScheme
	 *            The new value for the {@link #COLORSCHEME__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>colorScheme</i> value is not supported.
	 */
	public static void setColorScheme(Node node, String colorScheme) {
		setColorSchemeRaw(node, ID.fromValue(colorScheme));
	}

	/**
	 * Sets the {@link #COLORSCHEME__GNE} attribute of the given {@link Node} to
	 * the given <i>colorScheme</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #COLORSCHEME__GNE} attribute.
	 * @param colorSchemeParsed
	 *            The new value for the {@link #COLORSCHEME__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>colorScheme</i> value is not supported.
	 */
	public static void setColorSchemeParsed(Node node,
			String colorSchemeParsed) {
		setColorScheme(node, colorSchemeParsed);
	}

	/**
	 * Sets the {@link #DIR__E} attribute of the given {@link Edge} to the given
	 * <i>dir</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #DIR__E} attribute.
	 * @param dir
	 *            The new value for the {@link #DIR__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>dir</i> value is not supported.
	 */
	public static void setDirRaw(Edge edge, ID dir) {
		validate(AttributeContext.EDGE, DIR__E, dir.toValue());
		edge.attributesProperty().put(DIR__E, dir);
	}

	/**
	 * Sets the {@link #DIR__E} attribute of the given {@link Edge} to the given
	 * <i>dir</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #DIR__E} attribute.
	 * @param dir
	 *            The new value for the {@link #DIR__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>dir</i> value is not supported.
	 */
	public static void setDir(Edge edge, String dir) {
		setDirRaw(edge, ID.fromValue(dir, Type.STRING));
	}

	/**
	 * Sets the {@link #DIR__E} attribute of the given {@link Edge} to the given
	 * <i>dirParsed</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #DIR__E} attribute.
	 * @param dirParsed
	 *            The new value for the {@link #DIR__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>dirParsed</i> value is not supported.
	 */
	public static void setDirParsed(Edge edge, DirType dirParsed) {
		setDir(edge, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.DIRTYPE_SERIALIZER, dirParsed));
	}

	/**
	 * Sets the {@link #DISTORTION__N} attribute of the given {@link Node} to
	 * the given <i>distortion</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #DISTORTION__N} attribute.
	 * @param distortion
	 *            The new value for the {@link #DISTORTION__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>distortion</i> value is not supported.
	 */
	public static void setDistortionRaw(Node node, ID distortion) {
		validate(AttributeContext.NODE, DISTORTION__N, distortion.toValue());
		node.attributesProperty().put(DISTORTION__N, distortion);
	}

	/**
	 * Sets the {@link #DISTORTION__N} attribute of the given {@link Node} to
	 * the given <i>distortion</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #DISTORTION__N} attribute.
	 * @param distortion
	 *            The new value for the {@link #DISTORTION__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>distortion</i> value is not supported.
	 */
	public static void setDistortion(Node node, String distortion) {
		setDistortionRaw(node, ID.fromValue(distortion, Type.NUMERAL));
	}

	/**
	 * Sets the {@link #DISTORTION__N} attribute of the given {@link Node} to
	 * the given <i>distortionParsed</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #DISTORTION__N} attribute.
	 * @param distortionParsed
	 *            The new value for the {@link #DISTORTION__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>distortionParsed</i> value is not
	 *             supported.
	 */
	public static void setDistortionParsed(Node node, Double distortionParsed) {
		setDistortion(node, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.DOUBLE_SERIALIZER, distortionParsed));
	}

	/**
	 * Sets the {@link #FILLCOLOR__NE} attribute of the given {@link Edge} to
	 * the given <i>fillColor</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #FILLCOLOR__NE} attribute.
	 * @param fillColor
	 *            The new value for the {@link #FILLCOLOR__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fillColor</i> value is not supported.
	 */
	public static void setFillColorRaw(Edge edge, ID fillColor) {
		validate(AttributeContext.EDGE, FILLCOLOR__NE, fillColor.toValue());
		edge.attributesProperty().put(FILLCOLOR__NE, fillColor);
	}

	/**
	 * Sets the {@link #FILLCOLOR__NE} attribute of the given {@link Edge} to
	 * the given <i>fillColor</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #FILLCOLOR__NE} attribute.
	 * @param fillColor
	 *            The new value for the {@link #FILLCOLOR__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fillColor</i> value is not supported.
	 */
	public static void setFillColor(Edge edge, String fillColor) {
		setFillColorRaw(edge, ID.fromValue(fillColor));
	}

	/**
	 * Sets the {@link #FILLCOLOR__NE} attribute of the given {@link Edge} to
	 * the given <i>fillColorParsed</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #FILLCOLOR__NE} attribute.
	 * @param fillColorParsed
	 *            The new value for the {@link #FILLCOLOR__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fillColorParsed</i> value is not supported.
	 */
	public static void setFillColorParsed(Edge edge, Color fillColorParsed) {
		setFillColor(edge, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.COLOR_SERIALIZER, fillColorParsed));
	}

	/**
	 * Sets the {@link #FILLCOLOR__NE} attribute of the given {@link Node} to
	 * the given <i>fillColor</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #FILLCOLOR__NE} attribute.
	 * @param fillColor
	 *            The new value for the {@link #FILLCOLOR__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fillColor</i> value is not supported.
	 */
	public static void setFillColorRaw(Node node, ID fillColor) {
		validate(AttributeContext.NODE, FILLCOLOR__NE, fillColor.toValue());
		node.attributesProperty().put(FILLCOLOR__NE, fillColor);
	}

	/**
	 * Sets the {@link #FILLCOLOR__NE} attribute of the given {@link Node} to
	 * the given <i>fillColor</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #FILLCOLOR__NE} attribute.
	 * @param fillColor
	 *            The new value for the {@link #FILLCOLOR__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fillColor</i> value is not supported.
	 */
	public static void setFillColor(Node node, String fillColor) {
		setFillColorRaw(node, ID.fromValue(fillColor));
	}

	/**
	 * Sets the {@link #FILLCOLOR__NE} attribute of the given {@link Node} to
	 * the given <i>fillColorParsed</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #FILLCOLOR__NE} attribute.
	 * @param fillColorParsed
	 *            The new value for the {@link #FILLCOLOR__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fillColorParsed</i> value is not supported.
	 */
	public static void setFillColorParsed(Node node, Color fillColorParsed) {
		setFillColor(node, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.COLOR_SERIALIZER, fillColorParsed));
	}

	/**
	 * Sets the {@link #FIXEDSIZE__N} attribute of the given {@link Node} to the
	 * given <i>fixedSize</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #FIXEDSIZE__N} attribute.
	 * @param fixedSize
	 *            The new value for the {@link #FIXEDSIZE__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fixedSize</i> value is not supported.
	 */
	public static void setFixedSizeRaw(Node node, ID fixedSize) {
		validate(AttributeContext.NODE, FIXEDSIZE__N, fixedSize.toValue());
		node.attributesProperty().put(FIXEDSIZE__N, fixedSize);
	}

	/**
	 * Sets the {@link #FIXEDSIZE__N} attribute of the given {@link Node} to the
	 * given <i>fixedSize</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #FIXEDSIZE__N} attribute.
	 * @param fixedSize
	 *            The new value for the {@link #FIXEDSIZE__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fixedSize</i> value is not supported.
	 */
	public static void setFixedSize(Node node, String fixedSize) {
		setFixedSizeRaw(node, ID.fromValue(fixedSize, Type.STRING));
	}

	/**
	 * Sets the {@link #FIXEDSIZE__N} attribute of the given {@link Node} to the
	 * given <i>fixedSizeParsed</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #FIXEDSIZE__N} attribute.
	 * @param fixedSizeParsed
	 *            The new value for the {@link #FIXEDSIZE__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fixedSizeParsed</i> value is not supported.
	 */
	public static void setFixedSizeParsed(Node node, Boolean fixedSizeParsed) {
		setFixedSize(node, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.BOOL_SERIALIZER, fixedSizeParsed));
	}

	/**
	 * Sets the {@link #FONTCOLOR__GNE} attribute of the given {@link Edge} to
	 * the given <i>fontColor</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @param fontColor
	 *            The new value for the {@link #FONTCOLOR__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fontColor</i> value is not supported.
	 */
	public static void setFontColorRaw(Edge edge, ID fontColor) {
		validate(AttributeContext.EDGE, FONTCOLOR__GNE, fontColor.toValue());
		edge.attributesProperty().put(FONTCOLOR__GNE, fontColor);
	}

	/**
	 * Sets the {@link #FONTCOLOR__GNE} attribute of the given {@link Edge} to
	 * the given <i>fontColor</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @param fontColor
	 *            The new value for the {@link #FONTCOLOR__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fontColor</i> value is not supported.
	 */
	public static void setFontColor(Edge edge, String fontColor) {
		setFontColorRaw(edge, ID.fromValue(fontColor));
	}

	/**
	 * Sets the {@link #FONTCOLOR__GNE} attribute of the given {@link Edge} to
	 * the given <i>fontColorParsed</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @param fontColorParsed
	 *            The new value for the {@link #FONTCOLOR__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fontColorParsed</i> value is not supported.
	 */
	public static void setFontColorParsed(Edge edge, Color fontColorParsed) {
		setFontColor(edge, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.COLOR_SERIALIZER, fontColorParsed));
	}

	/**
	 * Sets the {@link #FONTCOLOR__GNE} attribute of the given {@link Graph} to
	 * the given <i>fontColor</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @param fontColor
	 *            The new value for the {@link #FONTCOLOR__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fontColor</i> value is not supported.
	 */
	public static void setFontColorRaw(Graph graph, ID fontColor) {
		validate(AttributeContext.GRAPH, FONTCOLOR__GNE, fontColor.toValue());
		graph.attributesProperty().put(FONTCOLOR__GNE, fontColor);
	}

	/**
	 * Sets the {@link #FONTCOLOR__GNE} attribute of the given {@link Graph} to
	 * the given <i>fontColor</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @param fontColor
	 *            The new value for the {@link #FONTCOLOR__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fontColor</i> value is not supported.
	 */
	public static void setFontColor(Graph graph, String fontColor) {
		setFontColorRaw(graph, ID.fromValue(fontColor));
	}

	/**
	 * Sets the {@link #FONTCOLOR__GNE} attribute of the given {@link Graph} to
	 * the given <i>fontColorParsed</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @param fontColorParsed
	 *            The new value for the {@link #FONTCOLOR__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fontColorParsed</i> value is not supported.
	 */
	public static void setFontColorParsed(Graph graph, Color fontColorParsed) {
		setFontColor(graph, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.COLOR_SERIALIZER, fontColorParsed));
	}

	/**
	 * Sets the {@link #FONTCOLOR__GNE} attribute of the given {@link Node} to
	 * the given <i>fontColor</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @param fontColor
	 *            The new value for the {@link #FONTCOLOR__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fontColor</i> value is not supported.
	 */
	public static void setFontColorRaw(Node node, ID fontColor) {
		validate(AttributeContext.NODE, FONTCOLOR__GNE, fontColor.toValue());
		node.attributesProperty().put(FONTCOLOR__GNE, fontColor);
	}

	/**
	 * Sets the {@link #FONTCOLOR__GNE} attribute of the given {@link Node} to
	 * the given <i>fontColor</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @param fontColor
	 *            The new value for the {@link #FONTCOLOR__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fontColor</i> value is not supported.
	 */
	public static void setFontColor(Node node, String fontColor) {
		setFontColorRaw(node, ID.fromValue(fontColor));
	}

	/**
	 * Sets the {@link #FONTCOLOR__GNE} attribute of the given {@link Node} to
	 * the given <i>fontColorParsed</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #FONTCOLOR__GNE} attribute.
	 * @param fontColorParsed
	 *            The new value for the {@link #FONTCOLOR__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>fontColorParsed</i> value is not supported.
	 */
	public static void setFontColorParsed(Node node, Color fontColorParsed) {
		setFontColor(node, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.COLOR_SERIALIZER, fontColorParsed));
	}

	/**
	 * Sets the {@link #FORCELABELS__G} attribute of the given {@link Graph} to
	 * the given <i>forceLabels</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #FORCELABELS__G} attribute.
	 * @param forceLabels
	 *            The new value for the {@link #FORCELABELS__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>forceLabels</i> value is not supported.
	 */
	public static void setForceLabelsRaw(Graph graph, ID forceLabels) {
		validate(AttributeContext.GRAPH, FORCELABELS__G, forceLabels.toValue());
		graph.attributesProperty().put(FORCELABELS__G, forceLabels);
	}

	/**
	 * Sets the {@link #FORCELABELS__G} attribute of the given {@link Graph} to
	 * the given <i>forceLabels</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #FORCELABELS__G} attribute.
	 * @param forceLabels
	 *            The new value for the {@link #FORCELABELS__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>forceLabels</i> value is not supported.
	 */
	public static void setForceLabels(Graph graph, String forceLabels) {
		setForceLabelsRaw(graph, ID.fromValue(forceLabels, Type.STRING));
	}

	/**
	 * Sets the {@link #FORCELABELS__G} attribute of the given {@link Graph} to
	 * the given <i>forceLabelsParsed</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #FORCELABELS__G} attribute.
	 * @param forceLabelsParsed
	 *            The new value for the {@link #FORCELABELS__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>forceLabelsParsed</i> value is not
	 *             supported.
	 */
	public static void setForceLabelsParsed(Graph graph,
			Boolean forceLabelsParsed) {
		setForceLabels(graph, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.BOOL_SERIALIZER, forceLabelsParsed));
	}

	/**
	 * Sets the {@link #HEADLABEL__E} attribute of the given {@link Edge} to the
	 * given <i>headLabel</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #HEADLABEL__E} attribute.
	 * @param headLabel
	 *            The new value for the {@link #HEADLABEL__E} attribute.
	 */
	public static void setHeadLabelRaw(Edge edge, ID headLabel) {
		edge.attributesProperty().put(HEADLABEL__E, headLabel);
	}

	/**
	 * Sets the {@link #HEADLABEL__E} attribute of the given {@link Edge} to the
	 * given <i>headLabel</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #HEADLABEL__E} attribute.
	 * @param headLabel
	 *            The new value for the {@link #HEADLABEL__E} attribute.
	 */
	public static void setHeadLabel(Edge edge, String headLabel) {
		setHeadLabelRaw(edge, ID.fromValue(headLabel));
	}

	/**
	 * Sets the {@link #HEADLABEL__E} attribute of the given {@link Edge} to the
	 * given <i>headLabel</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #HEADLABEL__E} attribute.
	 * @param headLabel
	 *            The new value for the {@link #HEADLABEL__E} attribute.
	 */
	// TODO: use LblString
	public static void setHeadLabelParsed(Edge edge, String headLabel) {
		// TODO: use LBL_STRING_SERIALIZER and infer type from LblString
		// sub-type
		setHeadLabel(edge, headLabel);
	}

	/**
	 * Sets the {@link #HEAD_LP__E} attribute of the given {@link Edge} to the
	 * given <i>headLp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #HEAD_LP__E} attribute.
	 * @param headLp
	 *            The new value for the {@link #HEAD_LP__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>headLp</i> value is not supported.
	 */
	public static void setHeadLpRaw(Edge edge, ID headLp) {
		validate(AttributeContext.EDGE, HEAD_LP__E, headLp.toValue());
		edge.attributesProperty().put(HEAD_LP__E, headLp);
	}

	/**
	 * Sets the {@link #HEAD_LP__E} attribute of the given {@link Edge} to the
	 * given <i>headLp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #HEAD_LP__E} attribute.
	 * @param headLp
	 *            The new value for the {@link #HEAD_LP__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>headLp</i> value is not supported.
	 */
	public static void setHeadLp(Edge edge, String headLp) {
		setHeadLpRaw(edge, ID.fromValue(headLp, Type.QUOTED_STRING));
	}

	/**
	 * Sets the {@link #HEAD_LP__E} attribute of the given {@link Edge} to the
	 * given <i>headLpParsed</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #HEAD_LP__E} attribute.
	 * @param headLpParsed
	 *            The new value for the {@link #HEAD_LP__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>headLpParsed</i> value is not supported.
	 */
	public static void setHeadLpParsed(Edge edge, Point headLpParsed) {
		setHeadLp(edge, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.POINT_SERIALIZER, headLpParsed));
	}

	/**
	 * Sets the {@link #HEIGHT__N} attribute of the given {@link Node} to the
	 * given <i>height</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #HEIGHT__N} attribute.
	 * @param height
	 *            The new value for the {@link #HEIGHT__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>height</i> value is not supported.
	 */
	public static void setHeightRaw(Node node, ID height) {
		validate(AttributeContext.NODE, HEIGHT__N, height.toValue());
		node.attributesProperty().put(HEIGHT__N, height);
	}

	/**
	 * Sets the {@link #HEIGHT__N} attribute of the given {@link Node} to the
	 * given <i>height</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #HEIGHT__N} attribute.
	 * @param height
	 *            The new value for the {@link #HEIGHT__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>height</i> value is not supported.
	 */
	public static void setHeight(Node node, String height) {
		setHeightRaw(node, ID.fromValue(height, Type.NUMERAL));
	}

	/**
	 * Sets the {@link #HEIGHT__N} attribute of the given {@link Node} to the
	 * given <i>heightParsed</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #HEIGHT__N} attribute.
	 * @param heightParsed
	 *            The new value for the {@link #HEIGHT__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>heightParsed</i> value is not supported.
	 */
	public static void setHeightParsed(Node node, Double heightParsed) {
		setHeight(node, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.DOUBLE_SERIALIZER, heightParsed));
	}

	/**
	 * Sets the {@link #ID__GNE} attribute of the given {@link Edge} to the
	 * given <i>id</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ID__GNE} attribute.
	 * @param id
	 *            The new value for the {@link #ID__GNE} attribute.
	 */
	public static void setIdRaw(Edge edge, ID id) {
		edge.attributesProperty().put(ID__GNE, id);
	}

	/**
	 * Sets the {@link #ID__GNE} attribute of the given {@link Edge} to the
	 * given <i>id</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ID__GNE} attribute.
	 * @param id
	 *            The new value for the {@link #ID__GNE} attribute.
	 */
	public static void setId(Edge edge, String id) {
		setIdRaw(edge, ID.fromValue(id));
	}

	/**
	 * Sets the {@link #ID__GNE} attribute of the given {@link Edge} to the
	 * given <i>id</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ID__GNE} attribute.
	 * @param id
	 *            The new value for the {@link #ID__GNE} attribute.
	 */
	// TODO: use EscString
	public static void setIdParsed(Edge edge, String id) {
		setId(edge, id);
	}

	/**
	 * Sets the {@link #ID__GNE} attribute of the given {@link Graph} to the
	 * given <i>id</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #ID__GNE} attribute.
	 * @param id
	 *            The new value for the {@link #ID__GNE} attribute.
	 */
	public static void setIdRaw(Graph graph, ID id) {
		graph.attributesProperty().put(ID__GNE, id);
	}

	/**
	 * Sets the {@link #ID__GNE} attribute of the given {@link Graph} to the
	 * given <i>id</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #ID__GNE} attribute.
	 * @param id
	 *            The new value for the {@link #ID__GNE} attribute.
	 */
	public static void setId(Graph graph, String id) {
		setIdRaw(graph, ID.fromValue(id));
	}

	/**
	 * Sets the {@link #ID__GNE} attribute of the given {@link Graph} to the
	 * given <i>id</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #ID__GNE} attribute.
	 * @param id
	 *            The new value for the {@link #ID__GNE} attribute.
	 */
	// TODO: use EscString
	public static void setIdParsed(Graph graph, String id) {
		setId(graph, id);
	}

	/**
	 * Sets the {@link #ID__GNE} attribute of the given {@link Node} to the
	 * given <i>id</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #ID__GNE} attribute.
	 * @param id
	 *            The new value for the {@link #ID__GNE} attribute.
	 */
	public static void setIdRaw(Node node, ID id) {
		node.attributesProperty().put(ID__GNE, id);
	}

	/**
	 * Sets the {@link #ID__GNE} attribute of the given {@link Node} to the
	 * given <i>id</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #ID__GNE} attribute.
	 * @param id
	 *            The new value for the {@link #ID__GNE} attribute.
	 */
	public static void setId(Node node, String id) {
		setIdRaw(node, ID.fromValue(id));
	}

	/**
	 * Sets the {@link #ID__GNE} attribute of the given {@link Node} to the
	 * given <i>id</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #ID__GNE} attribute.
	 * @param id
	 *            The new value for the {@link #ID__GNE} attribute.
	 */
	// TODO: use EscString
	public static void setIdParsed(Node node, String id) {
		setId(node, id);
	}

	/**
	 * Sets the {@link #LABEL__GNE} attribute of the given {@link Edge} to the
	 * given <i>label</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @param label
	 *            The new value for the {@link #LABEL__GNE} attribute.
	 */
	public static void setLabelRaw(Edge edge, ID label) {
		edge.attributesProperty().put(LABEL__GNE, label);
	}

	/**
	 * Sets the {@link #LABEL__GNE} attribute of the given {@link Edge} to the
	 * given <i>label</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @param label
	 *            The new value for the {@link #LABEL__GNE} attribute.
	 */
	public static void setLabel(Edge edge, String label) {
		setLabelRaw(edge, ID.fromValue(label));
	}

	/**
	 * Sets the {@link #LABEL__GNE} attribute of the given {@link Edge} to the
	 * given <i>label</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @param label
	 *            The new value for the {@link #LABEL__GNE} attribute.
	 */
	// TODO: use LblString
	public static void setLabelParsed(Edge edge, String label) {
		// TODO: use LBL_STRING_SERIALIZER and infer type from LblString
		// sub-type
		setLabel(edge, label);
	}

	/**
	 * Sets the {@link #LABEL__GNE} attribute of the given {@link Graph} to the
	 * given <i>label</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @param label
	 *            The new value for the {@link #LABEL__GNE} attribute.
	 */
	public static void setLabelRaw(Graph graph, ID label) {
		graph.attributesProperty().put(LABEL__GNE, label);
	}

	/**
	 * Sets the {@link #LABEL__GNE} attribute of the given {@link Graph} to the
	 * given <i>label</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @param label
	 *            The new value for the {@link #LABEL__GNE} attribute.
	 */
	public static void setLabel(Graph graph, String label) {
		setLabelRaw(graph, ID.fromValue(label));
	}

	/**
	 * Sets the {@link #LABEL__GNE} attribute of the given {@link Graph} to the
	 * given <i>label</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @param label
	 *            The new value for the {@link #LABEL__GNE} attribute.
	 */
	// TODO: use LblString
	public static void setLabelParsed(Graph graph, String label) {
		// TODO: use LBL_STRING_SERIALIZER and infer type from LblString
		// sub-type
		setLabel(graph, label);
	}

	/**
	 * Sets the {@link #LABEL__GNE} attribute of the given {@link Node} to the
	 * given <i>label</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @param label
	 *            The new value for the {@link #LABEL__GNE} attribute.
	 */
	public static void setLabelRaw(Node node, ID label) {
		node.attributesProperty().put(LABEL__GNE, label);
	}

	/**
	 * Sets the {@link #LABEL__GNE} attribute of the given {@link Node} to the
	 * given <i>label</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @param label
	 *            The new value for the {@link #LABEL__GNE} attribute.
	 */
	public static void setLabel(Node node, String label) {
		setLabelRaw(node, ID.fromValue(label));
	}

	/**
	 * Sets the {@link #LABEL__GNE} attribute of the given {@link Node} to the
	 * given <i>label</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #LABEL__GNE} attribute.
	 * @param label
	 *            The new value for the {@link #LABEL__GNE} attribute.
	 */
	// TODO: use LblString
	public static void setLabelParsed(Node node, String label) {
		// TODO: use LBL_STRING_SERIALIZER and infer type from LblString
		// sub-type
		setLabel(node, label);
	}

	/**
	 * Sets the {@link #LABELFONTCOLOR__E} attribute of the given {@link Edge}
	 * to the given <i>labelFontColor</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #LABELFONTCOLOR__E} attribute.
	 * @param labelFontColor
	 *            The new value for the {@link #LABELFONTCOLOR__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>labelFontColor</i> value is not supported.
	 */
	public static void setLabelFontColorRaw(Edge edge, ID labelFontColor) {
		validate(AttributeContext.EDGE, LABELFONTCOLOR__E,
				labelFontColor.toValue());
		edge.attributesProperty().put(LABELFONTCOLOR__E, labelFontColor);
	}

	/**
	 * Sets the {@link #LABELFONTCOLOR__E} attribute of the given {@link Edge}
	 * to the given <i>labelFontColor</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #LABELFONTCOLOR__E} attribute.
	 * @param labelFontColor
	 *            The new value for the {@link #LABELFONTCOLOR__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>labelFontColor</i> value is not supported.
	 */
	public static void setLabelFontColor(Edge edge, String labelFontColor) {
		setLabelFontColorRaw(edge, ID.fromValue(labelFontColor));
	}

	/**
	 * Sets the {@link #LABELFONTCOLOR__E} attribute of the given {@link Edge}
	 * to the given <i>labelFontColorParsed</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #LABELFONTCOLOR__E} attribute.
	 * @param labelFontColorParsed
	 *            The new value for the {@link #LABELFONTCOLOR__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>labelFontColorParsed</i> value is not
	 *             supported.
	 */
	public static void setLabelFontColorParsed(Edge edge,
			Color labelFontColorParsed) {
		setLabelFontColor(edge, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.COLOR_SERIALIZER, labelFontColorParsed));
	}

	/**
	 * Sets the {@link #LAYOUT__G} attribute of the given {@link Graph} to the
	 * given <i>layout</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #LAYOUT__G} attribute.
	 * @param layout
	 *            The new value for the {@link #LAYOUT__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>layout</i> value is not supported.
	 */
	public static void setLayoutRaw(Graph graph, ID layout) {
		validate(AttributeContext.GRAPH, LAYOUT__G, layout.toValue());
		graph.attributesProperty().put(LAYOUT__G, layout);
	}

	/**
	 * Sets the {@link #LAYOUT__G} attribute of the given {@link Graph} to the
	 * given <i>layout</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #LAYOUT__G} attribute.
	 * @param layout
	 *            The new value for the {@link #LAYOUT__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>layout</i> value is not supported.
	 */
	public static void setLayout(Graph graph, String layout) {
		setLayoutRaw(graph, ID.fromValue(layout, Type.STRING));
	}

	/**
	 * Sets the {@link #LAYOUT__G} attribute of the given {@link Graph} to the
	 * given <i>layoutParsed</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #LAYOUT__G} attribute.
	 * @param layoutParsed
	 *            The new value for the {@link #LAYOUT__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>layoutParsed</i> value is not supported.
	 */
	public static void setLayoutParsed(Graph graph, Layout layoutParsed) {
		setLayout(graph, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.LAYOUT_SERIALIZER, layoutParsed));
	}

	/**
	 * Sets the {@link #LP__GE} attribute of the given {@link Edge} to the given
	 * <i>lp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #LP__GE} attribute.
	 * @param lp
	 *            The new value for the {@link #LP__GE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>lp</i> value is not supported.
	 */
	public static void setLpRaw(Edge edge, ID lp) {
		validate(AttributeContext.EDGE, LP__GE, lp.toValue());
		edge.attributesProperty().put(LP__GE, lp);
	}

	/**
	 * Sets the {@link #LP__GE} attribute of the given {@link Edge} to the given
	 * <i>lp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #LP__GE} attribute.
	 * @param lp
	 *            The new value for the {@link #LP__GE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>lp</i> value is not supported.
	 */
	public static void setLp(Edge edge, String lp) {
		setLpRaw(edge, ID.fromValue(lp, Type.QUOTED_STRING));
	}

	/**
	 * Sets the {@link #LP__GE} attribute of the given {@link Edge} to the given
	 * <i>lpParsed</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #LP__GE} attribute.
	 * @param lpParsed
	 *            The new value for the {@link #LP__GE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>lpParsed</i> value is not supported.
	 */
	public static void setLpParsed(Edge edge, Point lpParsed) {
		setLp(edge, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.POINT_SERIALIZER, lpParsed));
	}

	/**
	 * Sets the {@link #LP__GE} attribute of the given {@link Graph} to the
	 * given <i>lp</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #LP__GE} attribute.
	 * @param lp
	 *            The new value for the {@link #LP__GE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>lp</i> value is not supported.
	 */
	public static void setLpRaw(Graph graph, ID lp) {
		validate(AttributeContext.GRAPH, LP__GE, lp.toValue());
		graph.attributesProperty().put(LP__GE, lp);
	}

	/**
	 * Sets the {@link #LP__GE} attribute of the given {@link Graph} to the
	 * given <i>lp</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #LP__GE} attribute.
	 * @param lp
	 *            The new value for the {@link #LP__GE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>lp</i> value is not supported.
	 */
	public static void setLp(Graph graph, String lp) {
		setLpRaw(graph, ID.fromValue(lp, Type.QUOTED_STRING));
	}

	/**
	 * Sets the {@link #LP__GE} attribute of the given {@link Graph} to the
	 * given <i>lpParsed</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #LP__GE} attribute.
	 * @param lpParsed
	 *            The new value for the {@link #LP__GE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>lpParsed</i> value is not supported.
	 */
	public static void setLpParsed(Graph graph, Point lpParsed) {
		setLp(graph, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.POINT_SERIALIZER, lpParsed));
	}

	/**
	 * Sets the {@link #OUTPUTORDER__G} attribute of the given {@link Graph} to
	 * the given <i>outputOrder</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #OUTPUTORDER__G} attribute.
	 * @param outputOrder
	 *            The new value for the {@link #OUTPUTORDER__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>outputOrder</i> value is not supported.
	 */
	public static void setOutputOrderRaw(Graph graph, ID outputOrder) {
		validate(AttributeContext.GRAPH, OUTPUTORDER__G, outputOrder.toValue());
		graph.attributesProperty().put(OUTPUTORDER__G, outputOrder);
	}

	/**
	 * Sets the {@link #OUTPUTORDER__G} attribute of the given {@link Graph} to
	 * the given <i>outputOrder</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #OUTPUTORDER__G} attribute.
	 * @param outputOrder
	 *            The new value for the {@link #OUTPUTORDER__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>outputOrder</i> value is not supported.
	 */
	public static void setOutputOrder(Graph graph, String outputOrder) {
		setOutputOrderRaw(graph, ID.fromValue(outputOrder, Type.STRING));
	}

	/**
	 * Sets the {@link #OUTPUTORDER__G} attribute of the given {@link Graph} to
	 * the given <i>outputOrderParsed</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #OUTPUTORDER__G} attribute.
	 * @param outputOrderParsed
	 *            The new value for the {@link #OUTPUTORDER__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>outputOrderParsed</i> value is not
	 *             supported.
	 */
	public static void setOutputOrderParsed(Graph graph,
			OutputMode outputOrderParsed) {
		setOutputOrder(graph, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.OUTPUTMODE_SERIALIZER, outputOrderParsed));
	}

	/**
	 * Sets the {@link #PAGEDIR__G} attribute of the given {@link Graph} to the
	 * given <i>pageDir</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #PAGEDIR__G} attribute.
	 * @param pagedir
	 *            The new value for the {@link #PAGEDIR__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>pageDir</i> value is not supported.
	 */
	public static void setPagedirRaw(Graph graph, ID pagedir) {
		validate(AttributeContext.GRAPH, PAGEDIR__G, pagedir.toValue());
		graph.attributesProperty().put(PAGEDIR__G, pagedir);
	}

	/**
	 * Sets the {@link #PAGEDIR__G} attribute of the given {@link Graph} to the
	 * given <i>pageDir</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #PAGEDIR__G} attribute.
	 * @param pagedir
	 *            The new value for the {@link #PAGEDIR__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>pageDir</i> value is not supported.
	 */
	public static void setPagedir(Graph graph, String pagedir) {
		setPagedirRaw(graph, ID.fromValue(pagedir, Type.STRING));
	}

	/**
	 * Sets the {@link #PAGEDIR__G} attribute of the given {@link Graph} to the
	 * given <i>pageDirParsed</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #PAGEDIR__G} attribute.
	 * @param pagedirParsed
	 *            The new value for the {@link #PAGEDIR__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>pageDirParsed</i> value is not supported.
	 */
	public static void setPagedirParsed(Graph graph, Pagedir pagedirParsed) {
		setPagedir(graph, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.PAGEDIR_SERIALIZER, pagedirParsed));
	}

	/**
	 * Sets the {@link #POS__NE} attribute of the given {@link Edge} to the
	 * given <i>pos</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #POS__NE} attribute.
	 * @param pos
	 *            The new value for the {@link #POS__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>pos</i> value is not supported.
	 */
	public static void setPosRaw(Edge edge, ID pos) {
		validate(AttributeContext.EDGE, POS__NE, pos.toValue());
		edge.attributesProperty().put(POS__NE, pos);
	}

	/**
	 * Sets the {@link #POS__NE} attribute of the given {@link Edge} to the
	 * given <i>pos</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #POS__NE} attribute.
	 * @param pos
	 *            The new value for the {@link #POS__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>pos</i> value is not supported.
	 */
	public static void setPos(Edge edge, String pos) {
		setPosRaw(edge, ID.fromValue(pos, Type.QUOTED_STRING));
	}

	/**
	 * Sets the {@link #POS__NE} attribute of the given {@link Edge} to the
	 * given <i>posParsed</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #POS__NE} attribute.
	 * @param posParsed
	 *            The new value for the {@link #POS__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>posParsed</i> value is not supported.
	 */
	public static void setPosParsed(Edge edge, SplineType posParsed) {
		setPos(edge, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.SPLINETYPE_SERIALIZER, posParsed));
	}

	/**
	 * Sets the {@link #POS__NE} attribute of the given {@link Node} to the
	 * given <i>pos</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #POS__NE} attribute.
	 * @param pos
	 *            The new value for the {@link #POS__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>pos</i> value is not supported.
	 */
	public static void setPosRaw(Node node, ID pos) {
		validate(AttributeContext.NODE, POS__NE, pos.toValue());
		node.attributesProperty().put(POS__NE, pos);
	}

	/**
	 * Sets the {@link #POS__NE} attribute of the given {@link Node} to the
	 * given <i>pos</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #POS__NE} attribute.
	 * @param pos
	 *            The new value for the {@link #POS__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>pos</i> value is not supported.
	 */
	public static void setPos(Node node, String pos) {
		setPosRaw(node, ID.fromValue(pos, Type.QUOTED_STRING));
	}

	/**
	 * Sets the {@link #POS__NE} attribute of the given {@link Node} to the
	 * given <i>posParsed</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #POS__NE} attribute.
	 * @param posParsed
	 *            The new value for the {@link #POS__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>posParsed</i> value is not supported.
	 */
	public static void setPosParsed(Node node, Point posParsed) {
		setPos(node, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.POINT_SERIALIZER, posParsed));
	}

	/**
	 * Sets the {@link #RANKDIR__G} attribute of the given {@link Graph} to the
	 * given <i>rankdir</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #RANKDIR__G} attribute.
	 * @param rankdir
	 *            The new value for the {@link #RANKDIR__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>rankdir</i> value is not supported.
	 */
	public static void setRankdirRaw(Graph graph, ID rankdir) {
		validate(AttributeContext.GRAPH, RANKDIR__G, rankdir.toValue());
		graph.attributesProperty().put(RANKDIR__G, rankdir);
	}

	/**
	 * Sets the {@link #RANKDIR__G} attribute of the given {@link Graph} to the
	 * given <i>rankdir</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #RANKDIR__G} attribute.
	 * @param rankdir
	 *            The new value for the {@link #RANKDIR__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>rankdir</i> value is not supported.
	 */
	public static void setRankdir(Graph graph, String rankdir) {
		setRankdirRaw(graph, ID.fromValue(rankdir, Type.STRING));
	}

	/**
	 * Sets the {@link #RANKDIR__G} attribute of the given {@link Graph} to the
	 * given <i>rankdirParsed</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #RANKDIR__G} attribute.
	 * @param rankdirParsed
	 *            The new value for the {@link #RANKDIR__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>rankdirParsed</i> value is not supported.
	 */
	public static void setRankdirParsed(Graph graph, Rankdir rankdirParsed) {
		setRankdir(graph, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.RANKDIR_SERIALIZER, rankdirParsed));
	}

	/**
	 * Sets the {@link #SHAPE__N} attribute of the given {@link Node} to the
	 * given <i>shape</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #SHAPE__N} attribute.
	 * @param shape
	 *            The new value for the {@link #SHAPE__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>shape</i> value is not supported.
	 */
	public static void setShapeRaw(Node node, ID shape) {
		validate(AttributeContext.NODE, SHAPE__N, shape.toValue());
		node.attributesProperty().put(SHAPE__N, shape);
	}

	/**
	 * Sets the {@link #SHAPE__N} attribute of the given {@link Node} to the
	 * given <i>shape</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #SHAPE__N} attribute.
	 * @param shape
	 *            The new value for the {@link #SHAPE__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>shape</i> value is not supported.
	 */
	public static void setShape(Node node, String shape) {
		setShapeRaw(node, ID.fromValue(shape));
	}

	/**
	 * Sets the {@link #SHAPE__N} attribute of the given {@link Node} to the
	 * given <i>shapeParsed</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #SHAPE__N} attribute.
	 * @param shapeParsed
	 *            The new value for the {@link #SHAPE__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>shapeParsed</i> value is not supported.
	 */
	public static void setShapeParsed(Node node, Shape shapeParsed) {
		setShape(node, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.SHAPE_SERIALIZER, shapeParsed));
	}

	/**
	 * Sets the {@link #SIDES__N} attribute of the given {@link Node} to the
	 * given <i>sides</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #SIDES__N} attribute.
	 * @param sides
	 *            The new value for the {@link #SIDES__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>sides</i> value is not supported.
	 */
	public static void setSidesRaw(Node node, ID sides) {
		validate(AttributeContext.NODE, SIDES__N, sides.toValue());
		node.attributesProperty().put(SIDES__N, sides);
	}

	/**
	 * Sets the {@link #SIDES__N} attribute of the given {@link Node} to the
	 * given <i>sides</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #SIDES__N} attribute.
	 * @param sides
	 *            The new value for the {@link #SIDES__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>sides</i> value is not supported.
	 */
	public static void setSides(Node node, String sides) {
		setSidesRaw(node, ID.fromValue(sides, Type.NUMERAL));
	}

	/**
	 * Sets the {@link #SIDES__N} attribute of the given {@link Node} to the
	 * given <i>sidesParsed</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #SIDES__N} attribute.
	 * @param sidesParsed
	 *            The new value for the {@link #SIDES__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>sidesParsed</i> value is not supported.
	 */
	public static void setSidesParsed(Node node, Integer sidesParsed) {
		setSides(node, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.INT_SERIALIZER, sidesParsed));
	}

	/**
	 * Sets the {@link #SKEW__N} attribute of the given {@link Node} to the
	 * given <i>skew</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #SKEW__N} attribute.
	 * @param skew
	 *            The new value for the {@link #SKEW__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>skew</i> value is not supported.
	 */
	public static void setSkewRaw(Node node, ID skew) {
		validate(AttributeContext.NODE, SKEW__N, skew.toValue());
		node.attributesProperty().put(SKEW__N, skew);
	}

	/**
	 * Sets the {@link #SKEW__N} attribute of the given {@link Node} to the
	 * given <i>skew</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #SKEW__N} attribute.
	 * @param skew
	 *            The new value for the {@link #SKEW__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>skew</i> value is not supported.
	 */
	public static void setSkew(Node node, String skew) {
		setSkewRaw(node, ID.fromValue(skew, Type.NUMERAL));
	}

	/**
	 * Sets the {@link #SKEW__N} attribute of the given {@link Node} to the
	 * given <i>skewParsed</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #SKEW__N} attribute.
	 * @param skewParsed
	 *            The new value for the {@link #SKEW__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>skewParsed</i> value is not supported.
	 */
	public static void setSkewParsed(Node node, Double skewParsed) {
		setSkew(node, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.DOUBLE_SERIALIZER, skewParsed));
	}

	/**
	 * Sets the {@link #SPLINES__G} attribute of the given {@link Graph} to the
	 * given <i>splines</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #SPLINES__G} attribute.
	 * @param splines
	 *            The new value for the {@link #SPLINES__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>splines</i> value is not supported.
	 */
	public static void setSplinesRaw(Graph graph, ID splines) {
		validate(AttributeContext.GRAPH, SPLINES__G, splines.toValue());
		graph.attributesProperty().put(SPLINES__G, splines);
	}

	/**
	 * Sets the {@link #SPLINES__G} attribute of the given {@link Graph} to the
	 * given <i>splines</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #SPLINES__G} attribute.
	 * @param splines
	 *            The new value for the {@link #SPLINES__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>splines</i> value is not supported.
	 */
	public static void setSplines(Graph graph, String splines) {
		setSplinesRaw(graph, ID.fromValue(splines, Type.QUOTED_STRING));
	}

	/**
	 * Sets the {@link #SPLINES__G} attribute of the given {@link Graph} to the
	 * given <i>splinesParsed</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #SPLINES__G} attribute.
	 * @param splinesParsed
	 *            The new value for the {@link #SPLINES__G} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>splinesParsed</i> value is not supported.
	 */
	public static void setSplinesParsed(Graph graph, Splines splinesParsed) {
		setSplines(graph, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.SPLINES_SERIALIZER, splinesParsed));
	}

	/**
	 * Sets the {@link #STYLE__GNE} attribute of the given {@link Edge} to the
	 * given <i>style</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #STYLE__GNE} attribute.
	 * @param style
	 *            The new value for the {@link #STYLE__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>style</i> value is not supported.
	 */
	public static void setStyleRaw(Edge edge, ID style) {
		validate(AttributeContext.EDGE, STYLE__GNE, style.toValue());
		edge.attributesProperty().put(STYLE__GNE, style);
	}

	/**
	 * Sets the {@link #STYLE__GNE} attribute of the given {@link Edge} to the
	 * given <i>style</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #STYLE__GNE} attribute.
	 * @param style
	 *            The new value for the {@link #STYLE__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>style</i> value is not supported.
	 */
	public static void setStyle(Edge edge, String style) {
		setStyleRaw(edge, ID.fromValue(style));
	}

	/**
	 * Sets the {@link #STYLE__GNE} attribute of the given {@link Edge} to the
	 * given <i>styleParsed</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #STYLE__GNE} attribute.
	 * @param styleParsed
	 *            The new value for the {@link #STYLE__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>styleParsed</i> value is not supported.
	 */
	public static void setStyleParsed(Edge edge, Style styleParsed) {
		setStyle(edge, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.STYLE_SERIALIZER, styleParsed));
	}

	/**
	 * Sets the {@link #STYLE__GNE} attribute of the given {@link Node} to the
	 * given <i>style</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #STYLE__GNE} attribute.
	 * @param style
	 *            The new value for the {@link #STYLE__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>style</i> value is not supported.
	 */
	public static void setStyleRaw(Node node, ID style) {
		validate(AttributeContext.NODE, STYLE__GNE, style.toValue());
		node.attributesProperty().put(STYLE__GNE, style);
	}

	/**
	 * Sets the {@link #STYLE__GNE} attribute of the given {@link Node} to the
	 * given <i>style</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #STYLE__GNE} attribute.
	 * @param style
	 *            The new value for the {@link #STYLE__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>style</i> value is not supported.
	 */
	public static void setStyle(Node node, String style) {
		setStyleRaw(node, ID.fromValue(style));
	}

	/**
	 * Sets the {@link #STYLE__GNE} attribute of the given {@link Node} to the
	 * given <i>styleParsed</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #STYLE__GNE} attribute.
	 * @param styleParsed
	 *            The new value for the {@link #STYLE__GNE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>styleParsed</i> value is not supported.
	 */
	public static void setStyleParsed(Node node, Style styleParsed) {
		setStyle(node, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.STYLE_SERIALIZER, styleParsed));
	}

	/**
	 * Sets the {@link #TAILLABEL__E} attribute of the given {@link Edge} to the
	 * given <i>tailLabel</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #TAILLABEL__E} attribute.
	 * @param tailLabel
	 *            The new value for the {@link #TAILLABEL__E} attribute.
	 */
	public static void setTailLabelRaw(Edge edge, ID tailLabel) {
		edge.attributesProperty().put(TAILLABEL__E, tailLabel);
	}

	/**
	 * Sets the {@link #TAILLABEL__E} attribute of the given {@link Edge} to the
	 * given <i>tailLabel</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #TAILLABEL__E} attribute.
	 * @param tailLabel
	 *            The new value for the {@link #TAILLABEL__E} attribute.
	 */
	public static void setTailLabel(Edge edge, String tailLabel) {
		setTailLabelRaw(edge, ID.fromValue(tailLabel));
	}

	/**
	 * Sets the {@link #TAILLABEL__E} attribute of the given {@link Edge} to the
	 * given <i>tailLabel</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #TAILLABEL__E} attribute.
	 * @param tailLabel
	 *            The new value for the {@link #TAILLABEL__E} attribute.
	 */
	public static void setTailLabelParsed(Edge edge, String tailLabel) {
		setTailLabel(edge, tailLabel);
	}

	/**
	 * Sets the {@link #TAIL_LP__E} attribute of the given {@link Edge} to the
	 * given <i>tailLp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #TAIL_LP__E} attribute.
	 * @param tailLp
	 *            The new value for the {@link #TAIL_LP__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>tailLp</i> value is not supported.
	 */
	public static void setTailLpRaw(Edge edge, ID tailLp) {
		validate(AttributeContext.EDGE, TAIL_LP__E, tailLp.toValue());
		edge.attributesProperty().put(TAIL_LP__E, tailLp);
	}

	/**
	 * Sets the {@link #TAIL_LP__E} attribute of the given {@link Edge} to the
	 * given <i>tailLp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #TAIL_LP__E} attribute.
	 * @param tailLp
	 *            The new value for the {@link #TAIL_LP__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>tailLp</i> value is not supported.
	 */
	public static void setTailLp(Edge edge, String tailLp) {
		setTailLpRaw(edge, ID.fromValue(tailLp, Type.QUOTED_STRING));
	}

	/**
	 * Sets the {@link #TAIL_LP__E} attribute of the given {@link Edge} to the
	 * given <i>tailLpParsed</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #TAIL_LP__E} attribute.
	 * @param tailLpParsed
	 *            The new value for the {@link #TAIL_LP__E} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>tailLpParsed</i> value is not supported.
	 */
	public static void setTailLpParsed(Edge edge, Point tailLpParsed) {
		setTailLp(edge, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.POINT_SERIALIZER, tailLpParsed));
	}

	/**
	 * Sets the {@link #WIDTH__N} attribute of the given {@link Node} to the
	 * given <i>width</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #WIDTH__N} attribute.
	 * @param width
	 *            The new value for the {@link #WIDTH__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>width</i> value is not supported.
	 */
	public static void setWidthRaw(Node node, ID width) {
		validate(AttributeContext.NODE, WIDTH__N, width.toValue());
		node.attributesProperty().put(WIDTH__N, width);
	}

	/**
	 * Sets the {@link #WIDTH__N} attribute of the given {@link Node} to the
	 * given <i>width</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #WIDTH__N} attribute.
	 * @param width
	 *            The new value for the {@link #WIDTH__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>width</i> value is not supported.
	 */
	public static void setWidth(Node node, String width) {
		setWidthRaw(node, ID.fromValue(width, Type.NUMERAL));
	}

	/**
	 * Sets the {@link #WIDTH__N} attribute of the given {@link Node} to the
	 * given <i>widthParsed</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #WIDTH__N} attribute.
	 * @param widthParsed
	 *            The new value for the {@link #WIDTH__N} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>widthParsed</i> value is not supported.
	 */
	public static void setWidthParsed(Node node, Double widthParsed) {
		setWidth(node, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.DOUBLE_SERIALIZER, widthParsed));
	}

	/**
	 * Sets the {@link #XLABEL__NE} attribute of the given {@link Edge} to the
	 * given <i>xLabel</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #XLABEL__NE} attribute.
	 * @param xLabel
	 *            The new value for the {@link #XLABEL__NE} attribute.
	 */
	public static void setXLabelRaw(Edge edge, ID xLabel) {
		edge.attributesProperty().put(XLABEL__NE, xLabel);
	}

	/**
	 * Sets the {@link #XLABEL__NE} attribute of the given {@link Edge} to the
	 * given <i>xLabel</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #XLABEL__NE} attribute.
	 * @param xLabel
	 *            The new value for the {@link #XLABEL__NE} attribute.
	 */
	public static void setXLabel(Edge edge, String xLabel) {
		setXLabelRaw(edge, ID.fromValue(xLabel));
	}

	/**
	 * Sets the {@link #XLABEL__NE} attribute of the given {@link Edge} to the
	 * given <i>xLabel</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #XLABEL__NE} attribute.
	 * @param xLabel
	 *            The new value for the {@link #XLABEL__NE} attribute.
	 */
	// TODO: introduce LblString
	public static void setXLabelParsed(Edge edge, String xLabel) {
		// TODO: use LBL_STRING_SERIALIZER and infer type from LblString
		// sub-type
		setXLabel(edge, xLabel);
	}

	/**
	 * Sets the {@link #XLABEL__NE} attribute of the given {@link Node} to the
	 * given <i>xLabel</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #XLABEL__NE} attribute.
	 * @param xLabel
	 *            The new value for the {@link #XLABEL__NE} attribute.
	 */
	public static void setXLabelRaw(Node node, ID xLabel) {
		node.attributesProperty().put(XLABEL__NE, xLabel);
	}

	/**
	 * Sets the {@link #XLABEL__NE} attribute of the given {@link Node} to the
	 * given <i>xLabel</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #XLABEL__NE} attribute.
	 * @param xLabel
	 *            The new value for the {@link #XLABEL__NE} attribute.
	 */
	public static void setXLabel(Node node, String xLabel) {
		setXLabelRaw(node, ID.fromValue(xLabel));
	}

	/**
	 * Sets the {@link #XLABEL__NE} attribute of the given {@link Node} to the
	 * given <i>xLabel</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #XLABEL__NE} attribute.
	 * @param xLabel
	 *            The new value for the {@link #XLABEL__NE} attribute.
	 */
	// TODO: introduce LblString
	public static void setXLabelParsed(Node node, String xLabel) {
		// TODO: use LBL_STRING_SERIALIZER and infer type from LblString
		// sub-type
		setXLabel(node, xLabel);
	}

	/**
	 * Sets the {@link #XLP__NE} attribute of the given {@link Edge} to the
	 * given <i>xlp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #XLP__NE} attribute.
	 * @param xlp
	 *            The new value for the {@link #XLP__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>xlp</i> value is not supported.
	 */
	public static void setXlpRaw(Edge edge, ID xlp) {
		validate(AttributeContext.EDGE, XLP__NE, xlp.toValue());
		edge.attributesProperty().put(XLP__NE, xlp);
	}

	/**
	 * Sets the {@link #XLP__NE} attribute of the given {@link Edge} to the
	 * given <i>xlp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #XLP__NE} attribute.
	 * @param xlp
	 *            The new value for the {@link #XLP__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>xlp</i> value is not supported.
	 */
	public static void setXlp(Edge edge, String xlp) {
		setXlpRaw(edge, ID.fromValue(xlp, Type.QUOTED_STRING));
	}

	/**
	 * Sets the {@link #XLP__NE} attribute of the given {@link Edge} to the
	 * given <i>xlpParsed</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #XLP__NE} attribute.
	 * @param xlpParsed
	 *            The new value for the {@link #XLP__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>xlpParsed</i> value is not supported.
	 */
	public static void setXlpParsed(Edge edge, Point xlpParsed) {
		setXlp(edge, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.POINT_SERIALIZER, xlpParsed));
	}

	/**
	 * Sets the {@link #XLP__NE} attribute of the given {@link Node} to the
	 * given <i>xlp</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #XLP__NE} attribute.
	 * @param xlp
	 *            The new value for the {@link #XLP__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>xlp</i> value is not supported.
	 */
	public static void setXlpRaw(Node node, ID xlp) {
		validate(AttributeContext.NODE, XLP__NE, xlp.toValue());
		node.attributesProperty().put(XLP__NE, xlp);
	}

	/**
	 * Sets the {@link #XLP__NE} attribute of the given {@link Node} to the
	 * given <i>xlp</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #XLP__NE} attribute.
	 * @param xlp
	 *            The new value for the {@link #XLP__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>xlp</i> value is not supported.
	 */
	public static void setXlp(Node node, String xlp) {
		setXlpRaw(node, ID.fromValue(xlp, Type.QUOTED_STRING));
	}

	/**
	 * Sets the {@link #XLP__NE} attribute of the given {@link Node} to the
	 * given <i>xlpParsed</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #XLP__NE} attribute.
	 * @param xlpParsed
	 *            The new value for the {@link #XLP__NE} attribute.
	 * @throws IllegalArgumentException
	 *             when the given <i>xlpParsed</i> value is not supported.
	 */
	public static void setXlpParsed(Node node, Point xlpParsed) {
		setXlp(node, DotLanguageSupport.serializeAttributeValue(
				DotLanguageSupport.POINT_SERIALIZER, xlpParsed));
	}

	private static void validate(AttributeContext context, String attributeName,
			String attributeValue) {
		if (dotValidator == null) {
			// if we are not injected (standalone), create validator instance
			dotValidator = new DotStandaloneSetup()
					.createInjectorAndDoEMFRegistration()
					.getInstance(DotJavaValidator.class);
		}

		List<Diagnostic> diagnostics = filter(dotValidator
				.validateAttributeValue(context, attributeName, attributeValue),
				Diagnostic.ERROR);
		if (!diagnostics.isEmpty()) {
			throw new IllegalArgumentException("Cannot set "
					+ context.name().toLowerCase() + " attribute '"
					+ attributeName + "' to '" + attributeValue + "'. "
					+ getFormattedDiagnosticMessage(diagnostics));
		}
	}

	/**
	 * Indication of the context in which an attribute is used.
	 */
	public static enum AttributeContext {
		/**
		 * Edge
		 */
		EDGE,

		/**
		 * Graph
		 */
		GRAPH,

		/**
		 * Node
		 */
		NODE,

		/**
		 * Subgraph/Cluster
		 */
		SUBGRAPH
	}

	/**
	 * Checks whether the given {@link Attribute} is used in the context of an
	 * edge. That is, it is either nested below an {@link EdgeStmtNode} or an
	 * {@link EdgeStmtSubgraph}, or used within an {@link AttrStmt} of type
	 * {@link AttributeType#EDGE}.
	 * 
	 * @param attribute
	 *            The {@link Attribute} to test.
	 * @return <code>true</code> if the {@link Attribute} is used in the context
	 *         of an edge, <code>false</code> otherwise.
	 */
	public static boolean isEdgeAttribute(Attribute attribute) {
		return getContext(attribute) == AttributeContext.EDGE;
	}

	/**
	 * Checks whether the given {@link Attribute} is used in the context of a
	 * top-level graph.
	 * 
	 * @param attribute
	 *            The {@link Attribute} to test.
	 * @return <code>true</code> if the {@link Attribute} is used in the context
	 *         of a top-level graph, <code>false</code> otherwise.
	 */
	public static boolean isGraphAttribute(Attribute attribute) {
		return getContext(attribute) == AttributeContext.GRAPH;
	}

	/**
	 * Checks whether the given {@link Attribute} is used in the context of a
	 * node. That is, it is either nested below a {@link NodeStmt} or used
	 * within an {@link AttrStmt} of type {@link AttributeType#NODE}.
	 * 
	 * @param attribute
	 *            The {@link Attribute} to test.
	 * @return <code>true</code> if the {@link Attribute} is used in the context
	 *         of a node, <code>false</code> otherwise.
	 */
	public static boolean isNodeAttribute(Attribute attribute) {
		return getContext(attribute) == AttributeContext.NODE;
	}

	/**
	 * Checks whether the given {@link Attribute} is used in the context of a
	 * subgraph.
	 * 
	 * @param attribute
	 *            The {@link Attribute} to test.
	 * @return <code>true</code> if the {@link Attribute} is used in the context
	 *         of a subgraph, <code>false</code> otherwise.
	 */
	public static boolean isSubgraphAttribute(Attribute attribute) {
		return getContext(attribute) == AttributeContext.SUBGRAPH;
	}

	/**
	 * Determine the context in which the given {@link EObject} is used.
	 * 
	 * @param eObject
	 *            The {@link EObject} for which the context is to be determined.
	 * @return the context in which the given {@link EObject} is used.
	 */
	public static AttributeContext getContext(EObject eObject) {
		// attribute nested below EdgeStmtNode or EdgeStmtSubgraph
		if (EcoreUtil2.getContainerOfType(eObject, EdgeStmtNode.class) != null
				|| EcoreUtil2.getContainerOfType(eObject,
						EdgeStmtSubgraph.class) != null) {
			return AttributeContext.EDGE;
		}
		// global AttrStmt with AttributeType 'edge'
		AttrStmt attrStmt = EcoreUtil2.getContainerOfType(eObject,
				AttrStmt.class);
		if (attrStmt != null && AttributeType.EDGE.equals(attrStmt.getType())) {
			return AttributeContext.EDGE;
		}

		// attribute nested below NodeStmt
		if (EcoreUtil2.getContainerOfType(eObject, NodeStmt.class) != null) {
			return AttributeContext.NODE;
		}
		// global AttrStmt with AttributeType 'node'
		if (attrStmt != null && AttributeType.NODE.equals(attrStmt.getType())) {
			return AttributeContext.NODE;
		}

		// attribute nested below Subgraph
		if (EcoreUtil2.getContainerOfType(eObject, Subgraph.class) != null) {
			return AttributeContext.SUBGRAPH;
		}

		// attribute is neither edge nor node nor subgraph attribute
		return AttributeContext.GRAPH;
	}
}

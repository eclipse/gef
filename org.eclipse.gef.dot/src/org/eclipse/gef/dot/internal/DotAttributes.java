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
import org.eclipse.gef.dot.internal.parser.DotStandaloneSetup;
import org.eclipse.gef.dot.internal.parser.arrowtype.ArrowType;
import org.eclipse.gef.dot.internal.parser.dir.DirType;
import org.eclipse.gef.dot.internal.parser.dot.GraphType;
import org.eclipse.gef.dot.internal.parser.layout.Layout;
import org.eclipse.gef.dot.internal.parser.point.Point;
import org.eclipse.gef.dot.internal.parser.rankdir.Rankdir;
import org.eclipse.gef.dot.internal.parser.shape.Shape;
import org.eclipse.gef.dot.internal.parser.splines.Splines;
import org.eclipse.gef.dot.internal.parser.splinetype.SplineType;
import org.eclipse.gef.dot.internal.parser.style.Style;
import org.eclipse.gef.dot.internal.parser.validation.DotJavaValidator;
import org.eclipse.gef.dot.internal.parser.validation.DotJavaValidator.AttributeContext;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.xtext.serializer.ISerializer;

import com.google.inject.Injector;

/**
 * The {@link DotAttributes} class contains all attributes which are supported
 * by {@link DotImport} and {@link DotExport}.
 * 
 * @author mwienand
 * @author anyssen
 *
 */
public class DotAttributes {

	private static final Injector dotInjector = new DotStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	private static final DotJavaValidator DOT_VALIDATOR = dotInjector
			.getInstance(DotJavaValidator.class);

	/**
	 * Specifies the name of a graph, node, or edge (not an attribute), as
	 * retrieved through the graph, node_id, as well as edge_stmt and edgeRHS
	 * grammar rules.
	 */
	public static final String _NAME__GNE = "_name";

	/**
	 * Specifies the graph type. Possible values are defined by
	 * {@link #_TYPE__G__GRAPH} and {@link #_TYPE__G__DIGRAPH}.
	 */
	public static final String _TYPE__G = "_type";

	/**
	 * This {@link #_TYPE__G} value specifies that the edges within the graph
	 * are undirected.
	 */
	public static final String _TYPE__G__DIGRAPH = GraphType.DIGRAPH
			.getLiteral();

	/**
	 * This {@link #_TYPE__G} value specifies that the edges within the graph
	 * are directed.
	 */
	public static final String _TYPE__G__GRAPH = GraphType.GRAPH.getLiteral();

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
	 * Specifies the 'dir' attribute of an edge.
	 */
	public static final String DIR__E = "dir";

	/**
	 * Specifies the 'distortion' attribute of a node.
	 */
	public static final String DISTORTION__N = "distortion";

	/**
	 * Specifies the 'fixedsize' attribute of a node.
	 */
	public static final String FIXEDSIZE__N = "fixedsize";

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
	 * Specifies the 'layout' attribute of a graph.
	 */
	public static final String LAYOUT__G = "layout";

	/**
	 * Specifies the 'lp' attribute (label position) of a graph or edge.
	 */
	public static final String LP__GE = "lp";

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
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @return The value of the {@link #_NAME__GNE} attribute of the given
	 *         {@link Edge}.
	 */
	public static String _getName(Edge edge) {
		return (String) edge.attributesProperty().get(_NAME__GNE);
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
		return (String) graph.attributesProperty().get(_NAME__GNE);
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
		return (String) node.attributesProperty().get(_NAME__GNE);
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
	public static String _getType(Graph graph) {
		return (String) graph.attributesProperty().get(_TYPE__G);
	}

	/**
	 * Sets the {@link #_NAME__GNE} attribute of the given {@link Edge} to the
	 * given <i>name</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #_NAME__GNE} attribute.
	 * @param name
	 *            The new value for the {@link #_NAME__GNE} attribute.
	 */
	public static void _setName(Edge edge, String name) {
		edge.attributesProperty().put(_NAME__GNE, name);
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
		graph.attributesProperty().put(_NAME__GNE, name);
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
		node.attributesProperty().put(_NAME__GNE, name);
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
	public static void _setType(Graph graph, String type) {
		if (!_TYPE__G__GRAPH.equals(type) && !_TYPE__G__DIGRAPH.equals(type)) {
			throw new IllegalArgumentException(
					"Cannot set graph attribute \"type\" to \"" + type
							+ "\"; supported values: " + _TYPE__G__GRAPH + ", "
							+ _TYPE__G__DIGRAPH);
		}
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
	public static String getArrowHead(Edge edge) {
		return (String) edge.attributesProperty().get(ARROWHEAD__E);
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
	public static String getArrowSize(Edge edge) {
		return (String) edge.attributesProperty().get(ARROWSIZE__E);
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
	public static String getArrowTail(Edge edge) {
		return (String) edge.attributesProperty().get(ARROWTAIL__E);
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
		return (String) edge.attributesProperty().get(DIR__E);
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
	public static String getDistortion(Node node) {
		return (String) node.attributesProperty().get(DISTORTION__N);
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
		return (String) node.attributesProperty().get(FIXEDSIZE__N);
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
		return (String) graph.attributesProperty().get(FORCELABELS__G);
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
	public static String getHeadLabel(Edge edge) {
		return (String) edge.attributesProperty().get(HEADLABEL__E);
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
		return (String) edge.attributesProperty().get(HEAD_LP__E);
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
	public static String getHeight(Node node) {
		return (String) node.attributesProperty().get(HEIGHT__N);
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
	public static String getId(Edge edge) {
		return (String) edge.attributesProperty().get(ID__GNE);
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
		return (String) graph.attributesProperty().get(ID__GNE);
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
		return (String) node.attributesProperty().get(ID__GNE);
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
		return (String) edge.attributesProperty().get(LABEL__GNE);
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
		return (String) graph.attributesProperty().get(LABEL__GNE);
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
		return (String) node.attributesProperty().get(LABEL__GNE);
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
		return (String) graph.attributesProperty().get(LAYOUT__G);
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
		return Layout.get(getLayout(graph));
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
		return (String) edge.attributesProperty().get(LP__GE);
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
		return (String) graph.attributesProperty().get(LP__GE);
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
		return (String) edge.attributesProperty().get(POS__NE);
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
		return (String) node.attributesProperty().get(POS__NE);
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
	public static String getRankdir(Graph graph) {
		return (String) graph.attributesProperty().get(RANKDIR__G);
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
	public static String getShape(Node node) {
		return (String) node.attributesProperty().get(SHAPE__N);
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
	public static String getSides(Node node) {
		return (String) node.attributesProperty().get(SIDES__N);
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
	public static String getSkew(Node node) {
		return (String) node.attributesProperty().get(SKEW__N);
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
	public static String getSplines(Graph graph) {
		return (String) graph.attributesProperty().get(SPLINES__G);
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
		return Splines.get(getSplines(graph));
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
		return (String) edge.attributesProperty().get(STYLE__GNE);
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
		return (String) node.attributesProperty().get(STYLE__GNE);
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
	public static String getTailLabel(Edge edge) {
		return (String) edge.attributesProperty().get(TAILLABEL__E);
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
		return (String) edge.attributesProperty().get(TAIL_LP__E);
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
	public static String getWidth(Node node) {
		return (String) node.attributesProperty().get(WIDTH__N);
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
	public static String getXLabel(Edge edge) {
		return (String) edge.attributesProperty().get(XLABEL__NE);
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
		return (String) node.attributesProperty().get(XLABEL__NE);
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
		return (String) edge.attributesProperty().get(XLP__NE);
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
		return (String) node.attributesProperty().get(XLP__NE);
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

	private static <T extends EObject> String serialize(ISerializer serializer,
			T parsedValue) {
		if (parsedValue == null) {
			return null;
		}
		return serializer.serialize(parsedValue);
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
		validate(AttributeContext.EDGE, ARROWHEAD__E, arrowHead);
		edge.attributesProperty().put(ARROWHEAD__E, arrowHead);
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
		setArrowHead(edge, serialize(DotLanguageSupport.ARROWTYPE_SERIALIZER,
				arrowHeadParsed));
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
		validate(AttributeContext.EDGE, ARROWSIZE__E, arrowSize);
		edge.attributesProperty().put(ARROWSIZE__E, arrowSize);
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
		setArrowSize(edge, arrowSizeParsed.toString());
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
		validate(AttributeContext.EDGE, ARROWTAIL__E, arrowTail);
		edge.attributesProperty().put(ARROWTAIL__E, arrowTail);
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
		setArrowTail(edge, serialize(DotLanguageSupport.ARROWTYPE_SERIALIZER,
				arrowTailParsed));
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
		validate(AttributeContext.EDGE, DIR__E, dir);
		edge.attributesProperty().put(DIR__E, dir);
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
		setDir(edge, dirParsed.toString());
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
		validate(AttributeContext.NODE, DISTORTION__N, distortion);
		node.attributesProperty().put(DISTORTION__N, distortion);
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
		setDistortion(node, distortionParsed.toString());
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
		validate(AttributeContext.NODE, FIXEDSIZE__N, fixedSize);
		node.attributesProperty().put(FIXEDSIZE__N, fixedSize);
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
		setFixedSize(node, fixedSizeParsed.toString());
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
		validate(AttributeContext.GRAPH, FORCELABELS__G, forceLabels);
		graph.attributesProperty().put(FORCELABELS__G, forceLabels);
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
		setForceLabels(graph, forceLabelsParsed.toString());
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
		edge.attributesProperty().put(HEADLABEL__E, headLabel);
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
		validate(AttributeContext.EDGE, HEAD_LP__E, headLp);
		edge.attributesProperty().put(HEAD_LP__E, headLp);
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
		setHeadLp(edge,
				serialize(DotLanguageSupport.POINT_SERIALIZER, headLpParsed));
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
		validate(AttributeContext.NODE, HEIGHT__N, height);
		node.attributesProperty().put(HEIGHT__N, height);
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
		setHeight(node, heightParsed.toString());
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
		edge.attributesProperty().put(ID__GNE, id);
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
		graph.attributesProperty().put(ID__GNE, id);
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
		node.attributesProperty().put(ID__GNE, id);
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
		edge.attributesProperty().put(LABEL__GNE, label);
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
		graph.attributesProperty().put(LABEL__GNE, label);
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
		node.attributesProperty().put(LABEL__GNE, label);
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
		validate(AttributeContext.GRAPH, LAYOUT__G, layout);
		graph.attributesProperty().put(LAYOUT__G, layout);
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
		setLayout(graph, layoutParsed.toString());
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
		validate(AttributeContext.EDGE, LP__GE, lp);
		edge.attributesProperty().put(LP__GE, lp);
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
		validate(AttributeContext.GRAPH, LP__GE, lp);
		graph.attributesProperty().put(LP__GE, lp);
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
		setLp(edge, serialize(DotLanguageSupport.POINT_SERIALIZER, lpParsed));
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
		setLp(graph, serialize(DotLanguageSupport.POINT_SERIALIZER, lpParsed));
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
		validate(AttributeContext.EDGE, POS__NE, pos);
		edge.attributesProperty().put(POS__NE, pos);
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
		validate(AttributeContext.NODE, POS__NE, pos);
		node.attributesProperty().put(POS__NE, pos);
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
		setPos(edge,
				serialize(DotLanguageSupport.SPLINETYPE_SERIALIZER, posParsed));
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
		setPos(node, serialize(DotLanguageSupport.POINT_SERIALIZER, posParsed));
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
		validate(AttributeContext.GRAPH, RANKDIR__G, rankdir);
		graph.attributesProperty().put(RANKDIR__G, rankdir);
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
		setRankdir(graph, rankdirParsed.toString());
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
		validate(AttributeContext.NODE, SHAPE__N, shape);
		node.attributesProperty().put(SHAPE__N, shape);
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
		setShape(node,
				serialize(DotLanguageSupport.SHAPE_SERIALIZER, shapeParsed));
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
		validate(AttributeContext.NODE, SIDES__N, sides);
		node.attributesProperty().put(SIDES__N, sides);
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
		setSides(node, sidesParsed.toString());
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
		validate(AttributeContext.NODE, SKEW__N, skew);
		node.attributesProperty().put(SKEW__N, skew);
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
		setSkew(node, skewParsed.toString());
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
		validate(AttributeContext.GRAPH, SPLINES__G, splines);
		graph.attributesProperty().put(SPLINES__G, splines);
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
		setSplines(graph, splinesParsed.toString());
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
		validate(AttributeContext.EDGE, STYLE__GNE, style);
		edge.attributesProperty().put(STYLE__GNE, style);
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
		validate(AttributeContext.NODE, STYLE__GNE, style);
		node.attributesProperty().put(STYLE__GNE, style);
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
		setStyle(edge,
				serialize(DotLanguageSupport.STYLE_SERIALIZER, styleParsed));
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
		setStyle(node,
				serialize(DotLanguageSupport.STYLE_SERIALIZER, styleParsed));
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
		edge.attributesProperty().put(TAILLABEL__E, tailLabel);
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
		validate(AttributeContext.EDGE, TAIL_LP__E, tailLp);
		edge.attributesProperty().put(TAIL_LP__E, tailLp);
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
		setTailLp(edge,
				serialize(DotLanguageSupport.POINT_SERIALIZER, tailLpParsed));
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
		validate(AttributeContext.NODE, WIDTH__N, width);
		node.attributesProperty().put(WIDTH__N, width);
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
		setWidth(node, widthParsed.toString());
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
		edge.attributesProperty().put(XLABEL__NE, xLabel);
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
		node.attributesProperty().put(XLABEL__NE, xLabel);
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
		validate(AttributeContext.EDGE, XLP__NE, xlp);
		edge.attributesProperty().put(XLP__NE, xlp);
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
		validate(AttributeContext.NODE, XLP__NE, xlp);
		node.attributesProperty().put(XLP__NE, xlp);
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
		setXlp(edge, serialize(DotLanguageSupport.POINT_SERIALIZER, xlpParsed));
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
		setXlp(node, serialize(DotLanguageSupport.POINT_SERIALIZER, xlpParsed));
	}

	private static void validate(AttributeContext context, String attributeName,
			String attributeValue) {
		List<Diagnostic> diagnostics = filter(DOT_VALIDATOR
				.validateAttributeValue(context, attributeName, attributeValue),
				Diagnostic.ERROR);
		if (!diagnostics.isEmpty()) {
			throw new IllegalArgumentException("Cannot set "
					+ context.name().toLowerCase() + " attribute '"
					+ attributeName + "' to '" + attributeValue + "'. "
					+ getFormattedDiagnosticMessage(diagnostics));
		}
	}
}

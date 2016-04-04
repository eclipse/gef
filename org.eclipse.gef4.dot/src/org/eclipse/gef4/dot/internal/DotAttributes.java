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
 *     Alexander Nyßen (itemis AG)  - initial API and implementation
 *     Tamas Miklossy (itemis AG)   - Add support for arrowType edge decorations (bug #477980)
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef4.dot.internal.parser.DotStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.arrowtype.ArrowType;
import org.eclipse.gef4.dot.internal.parser.dir.DirType;
import org.eclipse.gef4.dot.internal.parser.dot.GraphType;
import org.eclipse.gef4.dot.internal.parser.point.Point;
import org.eclipse.gef4.dot.internal.parser.rankdir.Rankdir;
import org.eclipse.gef4.dot.internal.parser.splinetype.SplineType;
import org.eclipse.gef4.dot.internal.parser.validation.DotJavaValidator;
import org.eclipse.gef4.dot.internal.parser.validation.DotJavaValidator.AttributeContext;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.xtext.serializer.ISerializer;

import com.google.inject.Injector;

/**
 * The {@link DotAttributes} class contains all properties which are supported
 * by {@link DotImport} and {@link DotExport}, i.e. they are set on the
 * resulting {@link Graph}.
 * 
 * @author mwienand
 * @author anyssen
 *
 */
// TODO: Define explicit enum types for enumerated string values. Provided them
// as parsed values as well.
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
	 * Specifies the 'forceLabels' attribute of a graph.
	 */
	public static final String FORCELABELS__G = "forcelabels";

	/**
	 * Specifies the 'head_lp' attribute (head label position) of an edge.
	 */
	public static final String HEAD_LP__E = "head_lp";

	/**
	 * Specifies the tail label of an edge (headlabel).
	 */
	public static final String HEADLABEL__E = "headlabel";

	/**
	 * Specified the 'height' attribute of a node.
	 */
	public static final String HEIGHT__N = "height";

	/**
	 * Specifies the 'id' attribute of a graph, node, or edge.
	 */
	public static final String ID__GNE = "id";

	/**
	 * Specifies the 'label' attribute of a graph, node, or edge.
	 */
	public static final String LABEL__GNE = "label";

	/**
	 * This {@link #LAYOUT__G} value specifies that the "circo" layout algorithm
	 * is to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__CIRCO = "circo";

	/**
	 * This {@link #LAYOUT__G} value specifies that the "dot" layout algorithm
	 * is to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__DOT = "dot";

	/**
	 * This {@link #LAYOUT__G} value specifies that the "fdp" layout algorithm
	 * is to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__FDP = "fdp";

	/**
	 * This {@link #LAYOUT__G} value specifies that the "grid" layout algorithm
	 * is to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__GRID = "grid";

	/**
	 * This {@link #LAYOUT__G} value specifies that the "neato" layout algorithm
	 * is to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__NEATO = "neato";

	/**
	 * This {@link #LAYOUT__G} value specifies that the "osage" layout algorithm
	 * is to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__OSAGE = "osage";

	/**
	 * This {@link #LAYOUT__G} value specifies that the "sfdp" layout algorithm
	 * is to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__SFDP = "sfdp";

	/**
	 * This {@link #LAYOUT__G} value specifies that the "twopi" layout algorithm
	 * is to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__TWOPI = "twopi";

	/**
	 * Defines the default value for the {@link #LAYOUT__G} property, which is
	 * {@link #LAYOUT__G__DOT}.
	 */
	public static final String LAYOUT__G__DEFAULT = LAYOUT__G__DOT;

	/**
	 * Defines all possible values for the {@link #LAYOUT__G} property.
	 */
	public static final Set<String> LAYOUT__G__VALUES = new HashSet<>(
			Arrays.asList(LAYOUT__G__DOT, LAYOUT__G__OSAGE, LAYOUT__G__GRID,
					LAYOUT__G__TWOPI, LAYOUT__G__CIRCO, LAYOUT__G__NEATO,
					LAYOUT__G__FDP, LAYOUT__G__SFDP));

	/**
	 * Specifies the layout algorithm which shall be used to layout the graph.
	 * Possible values are defined by {@link #LAYOUT__G__VALUES}. The default
	 * value is defined by {@link #LAYOUT__G__DEFAULT}.
	 */
	public static final String LAYOUT__G = "layout";

	/**
	 * Specifies 'lp' attribute (label position) of an edge.
	 */
	public static final String LP__E = "lp";

	/**
	 * Specified the 'pos' attribute of a node or edge.
	 */
	public static final String POS__NE = "pos";

	/**
	 * Specifies the rankdir property which is passed to the layout algorithm
	 * which is used for laying out the graph.
	 */
	public static final String RANKDIR__G = "rankdir";

	/**
	 * Specifies the name of the 'splines' attribute. It is used to control how
	 * edges are to be rendered.
	 */
	public static final String SPLINES__G = "splines";

	/**
	 * This {@link #SPLINES__G} value indicates that no edges are to be drawn.
	 * This is a synonym of {@link #SPLINES__G__EMPTY}
	 */
	public static final String SPLINES__G__NONE = "none";

	/**
	 * This {@link #SPLINES__G} value indicates that no edges are to be drawn.
	 * This is a synonym of {@link #SPLINES__G__NONE}
	 */
	public static final String SPLINES__G__EMPTY = "";

	/**
	 * This {@link #SPLINES__G} value indicates that lines are to be used.
	 */
	public static final String SPLINES__G__LINE = "line";

	/**
	 * This {@link #SPLINES__G} value indicates that lines are to be used. This
	 * is a synonym of {@link #SPLINES__G__LINE}
	 */
	public static final String SPLINES__G__FALSE = "false";

	/**
	 * This {@link #SPLINES__G} value indicates that splines are to be used.
	 */
	public static final String SPLINES__G__SPLINE = "spline";

	/**
	 * This {@link #SPLINES__G} value indicates that straight polylines are to
	 * be used.
	 */
	public static final String SPLINES__G__POLYLINE = "polyline";

	/**
	 * This {@link #SPLINES__G} value indicates that orthogonal polylines are to
	 * be used.
	 */
	public static final String SPLINES__G__ORTHO = "ortho";

	/**
	 * This {@link #SPLINES__G} value indicates that splines are to be used.
	 * This is a synonym of {@link #SPLINES__G__SPLINE}
	 */
	public static final String SPLINES__G__TRUE = "true";

	/**
	 * This {@link #SPLINES__G} value indicates that 'compound' are to be used.
	 */
	public static final String SPLINES__G__COMPOUND = "compound";

	/**
	 * The possible values of the {@link #SPLINES__G} attribute.
	 */
	public static final Set<String> SPLINES__G__VALUES = new HashSet<>(
			Arrays.asList(SPLINES__G__EMPTY, SPLINES__G__NONE,
					SPLINES__G__FALSE, SPLINES__G__LINE, SPLINES__G__POLYLINE,
					SPLINES__G__ORTHO, SPLINES__G__SPLINE, SPLINES__G__TRUE,
					SPLINES__G__COMPOUND));

	/**
	 * Specifies the rendering style of an edge, i.e. if it is solid, dashed,
	 * dotted, etc. Possible values are defined by {@link #STYLE__E__VALUES}.
	 * The default value is defined by {@link #STYLE__E__DEFAULT}.
	 */
	public static final String STYLE__E = "style";

	/**
	 * This {@link #STYLE__E} value specifies that the edge is rendered bold.
	 */
	public static final String STYLE__E__BOLD = "bold";

	/**
	 * This {@link #STYLE__E} value specifies that the edge is rendered dashed.
	 */
	public static final String STYLE__E__DASHED = "dashed";

	/**
	 * This {@link #STYLE__E} value specifies that the edge is rendered dotted.
	 */
	public static final String STYLE__E__DOTTED = "dotted";

	/**
	 * This {@link #STYLE__E} value specifies that the edge is rendered
	 * invisible.
	 */
	public static final String STYLE__E__INVIS = "invis";

	/**
	 * This {@link #STYLE__E} value specifies that the edge is rendered solid.
	 */
	public static final String STYLE__E__SOLID = "solid";

	/**
	 * This {@link #STYLE__E} value specifies that the edge is rendered tapered.
	 */
	public static final String STYLE__E__TAPERED = "tapered";

	/**
	 * This {@link #STYLE__E} value specifies that the edge is rendered with the
	 * void, which means the the original Dot default value is used.
	 */
	public static final String STYLE__E__VOID = "";

	/**
	 * Defines the default value for the {@link #STYLE__E} property, which is
	 * {@link #STYLE__E__SOLID}.
	 */
	public static final String STYLE__E__DEFAULT = STYLE__E__SOLID;

	/**
	 * Defines all possible values for the {@link #STYLE__E} property.
	 */
	// TODO: convert into enum
	public static final Set<String> STYLE__E__VALUES = new HashSet<>(
			Arrays.asList(STYLE__E__DASHED, STYLE__E__DOTTED, STYLE__E__SOLID,
					STYLE__E__INVIS, STYLE__E__BOLD, STYLE__E__TAPERED,
					STYLE__E__VOID));

	/**
	 * Specifies the 'tail_lp' attribute (tail label position) of an edge.
	 */
	public static final String TAIL_LP__E = "tail_lp";

	/**
	 * Specifies the tail label of an edge (taillabel).
	 */
	public static final String TAILLABEL__E = "taillabel";

	/**
	 * Specified the 'width' attribute of a node.
	 */
	public static final String WIDTH__N = "width";

	/**
	 * Specifies the external label of an node.
	 */
	public static final String XLABEL__NE = "xlabel";

	/**
	 * Specifies the 'xlp' attribute (external label position) of a node or
	 * edge.
	 */
	public static final String XLP__NE = "xlp";

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
	 * Returns the value of the {@link #ARROWHEAD__E} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWHEAD__E} property.
	 * @return The value of the {@link #ARROWHEAD__E} property of the given
	 *         {@link Edge}.
	 */
	public static String getArrowHead(Edge edge) {
		return (String) edge.attributesProperty().get(ARROWHEAD__E);
	}

	/**
	 * Returns the (parsed) value of the {@link #ARROWHEAD__E} property of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWHEAD__E} property, parsed as an {@link ArrowType}
	 *            .
	 * @return The value of the {@link #ARROWHEAD__E} property of the given
	 *         {@link Edge}.
	 */
	public static ArrowType getArrowHeadParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.ARROWTYPE_PARSER, getArrowHead(edge));
	}

	/**
	 * Returns the value of the {@link #ARROWSIZE__E} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWSIZE__E} property.
	 * @return The value of the {@link #ARROWSIZE__E} property of the given
	 *         {@link Edge}.
	 */
	public static String getArrowSize(Edge edge) {
		return (String) edge.attributesProperty().get(ARROWSIZE__E);
	}

	/**
	 * Returns the (parsed) value of the {@link #ARROWSIZE__E} property of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWSIZE__E} property, parsed as an {@link ArrowType}
	 *            .
	 * 
	 * @return The value of the {@link #ARROWSIZE__E} property of the given
	 *         {@link Edge}.
	 */
	public static Double getArrowSizeParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.DOUBLE_PARSER, getArrowSize(edge));
	}

	/**
	 * Returns the value of the {@link #ARROWTAIL__E} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWTAIL__E} property.
	 * @return The value of the {@link #ARROWTAIL__E} property of the given
	 *         {@link Edge}.
	 */
	public static String getArrowTail(Edge edge) {
		return (String) edge.attributesProperty().get(ARROWTAIL__E);
	}

	/**
	 * Returns the (parsed) value of the {@link #ARROWTAIL__E} property of the
	 * given {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ARROWTAIL__E} property, parsed as an {@link ArrowType}
	 *            .
	 * 
	 * @return The value of the {@link #ARROWTAIL__E} property of the given
	 *         {@link Edge}.
	 */
	public static ArrowType getArrowTailParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.ARROWTYPE_PARSER, getArrowTail(edge));
	}

	/**
	 * Returns the value of the {@link #DIR__E} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #DIR__E} property.
	 * @return The value of the {@link #DIR__E} property of the given
	 *         {@link Edge}.
	 */
	public static String getDir(Edge edge) {
		return (String) edge.attributesProperty().get(DIR__E);
	}

	/**
	 * Returns the value of the {@link #DIR__E} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #DIR__E} property.
	 * @return The value of the {@link #DIR__E} property of the given
	 *         {@link Edge}.
	 */
	public static DirType getDirParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.DIRTYPE_PARSER, getDir(edge));
	}

	/**
	 * Returns the value of the {@link #FORCELABELS__G} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #FORCELABELS__G} property.
	 * @return The value of the {@link #FORCELABELS__G} property of the given
	 *         {@link Graph}.
	 */
	public static String getForceLabels(Graph graph) {
		return (String) graph.getAttributes().get(FORCELABELS__G);
	}

	/**
	 * Returns the value of the {@link #FORCELABELS__G} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #FORCELABELS__G} property.
	 * @return The value of the {@link #FORCELABELS__G} property of the given
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
	 * Returns the value of the {@link #HEADLABEL__E} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #HEADLABEL__E} property.
	 * @return The value of the {@link #HEADLABEL__E} property of the given
	 *         {@link Edge}.
	 */
	public static String getHeadLabel(Edge edge) {
		return (String) edge.attributesProperty().get(HEADLABEL__E);
	}

	/**
	 * Returns the value of the {@link #HEAD_LP__E} property of the given
	 * {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #HEAD_LP__E} property.
	 * @return The value of the {@link #HEAD_LP__E} property of the given
	 *         {@link Edge}.
	 */
	public static String getHeadLp(Edge edge) {
		return (String) edge.attributesProperty().get(HEAD_LP__E);
	}

	/**
	 * Returns the (parsed) value of the {@link #HEAD_LP__E} property of the
	 * given {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #HEAD_LP__E} property, parsed as a {@link Point}.
	 * @return The value of the {@link #HEAD_LP__E} property of the given
	 *         {@link Edge}.
	 */
	public static Point getHeadLpParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.POINT_PARSER, getHeadLp(edge));
	}

	/**
	 * Returns the value of the {@link #HEIGHT__N} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #HEIGHT__N} property.
	 * @return The value of the {@link #HEIGHT__N} property of the given
	 *         {@link Node}.
	 */
	public static String getHeight(Node node) {
		return (String) node.attributesProperty().get(HEIGHT__N);
	}

	/**
	 * Returns the value of the {@link #HEIGHT__N} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #HEIGHT__N} property.
	 * @return The value of the {@link #HEIGHT__N} property of the given
	 *         {@link Node}.
	 */
	public static Double getHeightParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.DOUBLE_PARSER, getHeight(node));
	}

	/**
	 * Returns the value of the {@link #ID__GNE} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #ID__GNE} property.
	 * @return The value of the {@link #ID__GNE} property of the given
	 *         {@link Edge}.
	 */
	public static String getId(Edge edge) {
		return (String) edge.attributesProperty().get(ID__GNE);
	}

	/**
	 * Returns the value of the {@link #ID__GNE} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #ID__GNE} property.
	 * @return The value of the {@link #ID__GNE} property of the given
	 *         {@link Graph}.
	 */
	public static String getId(Graph graph) {
		return (String) graph.attributesProperty().get(ID__GNE);
	}

	/**
	 * Returns the value of the {@link #ID__GNE} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #ID__GNE} property.
	 * @return The value of the {@link #ID__GNE} property of the given
	 *         {@link Node}.
	 */
	public static String getId(Node node) {
		return (String) node.attributesProperty().get(ID__GNE);
	}

	/**
	 * Returns the value of the {@link #LABEL__GNE} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #LABEL__GNE} property.
	 * @return The value of the {@link #LABEL__GNE} property of the given
	 *         {@link Edge}.
	 */
	public static String getLabel(Edge edge) {
		return (String) edge.attributesProperty().get(LABEL__GNE);
	}

	/**
	 * Returns the value of the {@link #LABEL__GNE} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #LABEL__GNE} property.
	 * @return The value of the {@link #LABEL__GNE} property of the given
	 *         {@link Node}.
	 */
	public static String getLabel(Node node) {
		return (String) node.attributesProperty().get(LABEL__GNE);
	}

	/**
	 * Returns the value of the {@link #LAYOUT__G} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #LAYOUT__G} property.
	 * @return The value of the {@link #LAYOUT__G} property of the given
	 *         {@link Graph}.
	 */
	public static String getLayout(Graph graph) {
		return (String) graph.attributesProperty().get(LAYOUT__G);
	}

	/**
	 * Returns the value of the {@link #LP__E} property of the given
	 * {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #LP__E} property.
	 * @return The value of the {@link #LP__E} property of the given
	 *         {@link Edge}.
	 */
	public static String getLp(Edge edge) {
		return (String) edge.attributesProperty().get(LP__E);
	}

	/**
	 * Returns the (parsed) value of the {@link #LP__E} property of the given
	 * {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #LP__E} property, parsed as a {@link Point}.
	 * @return The value of the {@link #LP__E} property of the given
	 *         {@link Edge}.
	 */
	public static Point getLpParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.POINT_PARSER, getLp(edge));
	}

	/**
	 * Returns the value of the {@link #_NAME__GNE} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #_NAME__GNE} property.
	 * @return The value of the {@link #_NAME__GNE} property of the given
	 *         {@link Edge}.
	 */
	public static String getName(Edge edge) {
		return (String) edge.attributesProperty().get(_NAME__GNE);
	}

	/**
	 * Returns the value of the {@link #_NAME__GNE} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #_NAME__GNE} property.
	 * @return The value of the {@link #_NAME__GNE} property of the given
	 *         {@link Graph}.
	 */
	public static String getName(Graph graph) {
		return (String) graph.attributesProperty().get(_NAME__GNE);
	}

	/**
	 * Returns the value of the {@link #_NAME__GNE} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #_NAME__GNE} property.
	 * @return The value of the {@link #_NAME__GNE} property of the given
	 *         {@link Node}.
	 */
	public static String getName(Node node) {
		return (String) node.attributesProperty().get(_NAME__GNE);
	}

	/**
	 * Returns the value of the {@link #POS__NE} property of the given
	 * {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #POS__NE} property.
	 * @return The value of the {@link #POS__NE} property of the given
	 *         {@link Edge}.
	 */
	public static String getPos(Edge edge) {
		return (String) edge.attributesProperty().get(POS__NE);
	}

	/**
	 * Returns the value of the {@link #POS__NE} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #POS__NE} property.
	 * @return The value of the {@link #POS__NE} property of the given
	 *         {@link Node}.
	 */
	public static String getPos(Node node) {
		return (String) node.attributesProperty().get(POS__NE);
	}

	/**
	 * Returns the (parsed) value of the {@link #POS__NE} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #POS__NE} property, parsed as a {@link SplineType}.
	 * @return The value of the {@link #POS__NE} property of the given
	 *         {@link Edge}.
	 */
	public static SplineType getPosParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.SPLINETYPE_PARSER, getPos(edge));
	}

	/**
	 * Returns the (parsed) value of the {@link #POS__NE} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #POS__NE} property, parsed as a {@link Point}.
	 * @return The value of the {@link #POS__NE} property of the given
	 *         {@link Node}.
	 */
	public static Point getPosParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.POINT_PARSER, getPos(node));
	}

	/**
	 * Returns the value of the {@link #RANKDIR__G} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #RANKDIR__G} property.
	 * @return The value of the {@link #RANKDIR__G} property of the given
	 *         {@link Graph}.
	 */
	public static String getRankdir(Graph graph) {
		return (String) graph.attributesProperty().get(RANKDIR__G);
	}

	/**
	 * Returns the value of the {@link #RANKDIR__G} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #RANKDIR__G} property.
	 * @return The value of the {@link #RANKDIR__G} property of the given
	 *         {@link Graph}.
	 */
	public static Rankdir getRankdirParsed(Graph graph) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.RANKDIR_PARSER, getRankdir(graph));
	}

	/**
	 * Returns the value of the {@link #SPLINES__G} attribute of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #SPLINES__G} property.
	 * @return The value of the {@link #SPLINES__G} property of the given
	 *         {@link Graph}.
	 */
	public static String getSplines(Graph graph) {
		return (String) graph.attributesProperty().get(SPLINES__G);
	}

	/**
	 * Returns the value of the {@link #STYLE__E} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #STYLE__E} property.
	 * @return The value of the {@link #STYLE__E} property of the given
	 *         {@link Edge}.
	 */
	public static String getStyle(Edge edge) {
		return (String) edge.attributesProperty().get(STYLE__E);
	}

	/**
	 * Returns the value of the {@link #TAILLABEL__E} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #TAILLABEL__E} property.
	 * @return The value of the {@link #TAILLABEL__E} property of the given
	 *         {@link Edge}.
	 */
	public static String getTailLabel(Edge edge) {
		return (String) edge.attributesProperty().get(TAILLABEL__E);
	}

	/**
	 * Returns the value of the {@link #TAIL_LP__E} property of the given
	 * {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #TAIL_LP__E} property.
	 * @return The value of the {@link #TAIL_LP__E} property of the given
	 *         {@link Edge}.
	 */
	public static String getTailLp(Edge edge) {
		return (String) edge.attributesProperty().get(TAIL_LP__E);
	}

	/**
	 * Returns the (parsed) value of the {@link #TAIL_LP__E} property of the
	 * given {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #TAIL_LP__E} property, parsed as a {@link Point}.
	 * @return The value of the {@link #TAIL_LP__E} property of the given
	 *         {@link Edge}.
	 */
	public static Point getTailLpParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.POINT_PARSER, getTailLp(edge));
	}

	/**
	 * Returns the value of the {@link #_TYPE__G} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #_TYPE__G} property.
	 * @return The value of the {@link #_TYPE__G} property of the given
	 *         {@link Graph}.
	 */
	public static String getType(Graph graph) {
		return (String) graph.attributesProperty().get(_TYPE__G);
	}

	/**
	 * Returns the value of the {@link #WIDTH__N} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #WIDTH__N} property.
	 * @return The value of the {@link #WIDTH__N} property of the given
	 *         {@link Node}.
	 */
	public static String getWidth(Node node) {
		return (String) node.attributesProperty().get(WIDTH__N);
	}

	/**
	 * Returns the value of the {@link #WIDTH__N} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #WIDTH__N} property.
	 * @return The value of the {@link #WIDTH__N} property of the given
	 *         {@link Node}.
	 */
	public static Double getWidthParsed(Node node) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.DOUBLE_PARSER, getWidth(node));
	}

	/**
	 * Returns the value of the {@link #XLABEL__NE} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #XLABEL__NE} property.
	 * @return The value of the {@link #XLABEL__NE} property of the given
	 *         {@link Edge}.
	 */
	public static String getXLabel(Edge edge) {
		return (String) edge.attributesProperty().get(XLABEL__NE);
	}

	/**
	 * Returns the value of the {@link #XLABEL__NE} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #XLABEL__NE} property.
	 * @return The value of the {@link #XLABEL__NE} property of the given
	 *         {@link Node}.
	 */
	public static String getXLabel(Node node) {
		return (String) node.attributesProperty().get(XLABEL__NE);
	}

	/**
	 * Returns the value of the {@link #XLP__NE} property of the given
	 * {@link Edge}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #XLP__NE} property.
	 * @return The value of the {@link #XLP__NE} property of the given
	 *         {@link Edge}.
	 */
	public static String getXlp(Edge edge) {
		return (String) edge.attributesProperty().get(XLP__NE);
	}

	/**
	 * Returns the value of the {@link #XLP__NE} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #XLP__NE} property.
	 * @return The value of the {@link #XLP__NE} property of the given
	 *         {@link Node}.
	 */
	public static String getXlp(Node node) {
		return (String) node.attributesProperty().get(XLP__NE);
	}

	/**
	 * Returns the (parsed) value of the {@link #XLP__NE} property of the given
	 * {@link Node}.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to return the value of the
	 *            {@link #XLP__NE} property, parsed as a {@link Point}.
	 * @return The value of the {@link #XLP__NE} property of the given
	 *         {@link Edge}.
	 */
	public static Point getXlpParsed(Edge edge) {
		return DotLanguageSupport.parseAttributeValue(
				DotLanguageSupport.POINT_PARSER, getXlp(edge));
	}

	/**
	 * Returns the (parsed) value of the {@link #XLP__NE} property of the given
	 * {@link Node}.
	 * 
	 * @param node
	 *            The {@link Node} for which to return the value of the
	 *            {@link #XLP__NE} property, parsed as a {@link Point}.
	 * @return The value of the {@link #XLP__NE} property of the given
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
	 * Sets the {@link #ARROWHEAD__E} property of the given {@link Edge} to the
	 * given <i>arrowHead</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWHEAD__E} property.
	 * @param arrowHead
	 *            The new value for the {@link #ARROWHEAD__E} property.
	 */
	public static void setArrowHead(Edge edge, String arrowHead) {
		validate(AttributeContext.EDGE, ARROWHEAD__E, arrowHead);
		edge.attributesProperty().put(ARROWHEAD__E, arrowHead);
	}

	/**
	 * Sets the {@link #ARROWHEAD__E} property of the given {@link Edge} to the
	 * given <i>arrowHead</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWHEAD__E} property.
	 * @param arrowHeadParsed
	 *            The new value for the {@link #ARROWHEAD__E} property.
	 */
	public static void setArrowHeadParsed(Edge edge,
			ArrowType arrowHeadParsed) {
		setArrowHead(edge, serialize(DotLanguageSupport.ARROWTYPE_SERIALIZER,
				arrowHeadParsed));
	}

	/**
	 * Sets the {@link #ARROWSIZE__E} property of the given {@link Edge} to the
	 * given <i>arrowSize</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWSIZE__E} property.
	 * @param arrowSize
	 *            The new value for the {@link #ARROWSIZE__E} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>arrowSize</i> value is not supported.
	 */
	public static void setArrowSize(Edge edge, String arrowSize) {
		validate(AttributeContext.EDGE, ARROWSIZE__E, arrowSize);
		edge.attributesProperty().put(ARROWSIZE__E, arrowSize);
	}

	/**
	 * Sets the {@link #ARROWSIZE__E} property of the given {@link Edge} to the
	 * given <i>arrowSize</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWSIZE__E} property.
	 * @param arrowSizeParsed
	 *            The new value for the {@link #ARROWSIZE__E} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>arrowSize</i> value is not supported.
	 */
	public static void setArrowSizeParsed(Edge edge, Double arrowSizeParsed) {
		setArrowSize(edge, arrowSizeParsed.toString());
	}

	/**
	 * Sets the {@link #ARROWTAIL__E} property of the given {@link Edge} to the
	 * given <i>arrowTail</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWTAIL__E} property.
	 * @param arrowTail
	 *            The new value for the {@link #ARROWTAIL__E} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>arrowTail</i> value is not supported.
	 */
	public static void setArrowTail(Edge edge, String arrowTail) {
		validate(AttributeContext.EDGE, ARROWTAIL__E, arrowTail);
		edge.attributesProperty().put(ARROWTAIL__E, arrowTail);
	}

	/**
	 * Sets the {@link #ARROWTAIL__E} property of the given {@link Edge} to the
	 * given <i>arrowTail</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ARROWTAIL__E} property.
	 * @param arrowTailParsed
	 *            The new value for the {@link #ARROWTAIL__E} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>arrowTail</i> value is not supported.
	 */
	public static void setArrowTailParsed(Edge edge,
			ArrowType arrowTailParsed) {
		setArrowTail(edge, serialize(DotLanguageSupport.ARROWTYPE_SERIALIZER,
				arrowTailParsed));
	}

	/**
	 * Sets the {@link #DIR__E} property of the given {@link Edge} to the given
	 * <i>direction</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #DIR__E} property.
	 * @param dir
	 *            The new value for the {@link #DIR__E} property.
	 */
	public static void setDir(Edge edge, String dir) {
		validate(AttributeContext.EDGE, DIR__E, dir);
		edge.attributesProperty().put(DIR__E, dir);
	}

	/**
	 * Sets the {@link #DIR__E} property of the given {@link Edge} to the given
	 * <i>dir</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #DIR__E} property.
	 * @param dirParsed
	 *            The new value for the {@link #DIR__E} property.
	 */
	public static void setDirParsed(Edge edge, DirType dirParsed) {
		setDir(edge, dirParsed.toString());
	}

	/**
	 * Sets the {@link #FORCELABELS__G} property of the given {@link Graph} to
	 * the given value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #FORCELABELS__G} property.
	 * @param forceLabels
	 *            The new value for the {@link #FORCELABELS__G} property.
	 */
	public static void setForceLabels(Graph graph, String forceLabels) {
		graph.getAttributes().put(FORCELABELS__G, forceLabels);
	}

	/**
	 * Sets the {@link #FORCELABELS__G} property of the given {@link Graph} to
	 * the given value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #FORCELABELS__G} property.
	 * @param forceLabelsParsed
	 *            The new value for the {@link #FORCELABELS__G} property.
	 */
	public static void setForceLabelsParsed(Graph graph,
			Boolean forceLabelsParsed) {
		setForceLabels(graph, forceLabelsParsed.toString());
	}

	/**
	 * Sets the {@link #HEADLABEL__E} property of the given {@link Edge} to the
	 * given <i>label</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #HEADLABEL__E} property.
	 * @param label
	 *            The new value for the {@link #HEADLABEL__E} property.
	 */
	public static void setHeadLabel(Edge edge, String label) {
		edge.attributesProperty().put(HEADLABEL__E, label);
	}

	/**
	 * Sets the {@link #HEAD_LP__E} property of the given {@link Edge} to the
	 * given <i>headLp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #HEAD_LP__E} property.
	 * @param headLp
	 *            The new value for the {@link #HEAD_LP__E} property.
	 */
	public static void setHeadLp(Edge edge, String headLp) {
		edge.attributesProperty().put(HEAD_LP__E, headLp);
	}

	/**
	 * Sets the {@link #HEAD_LP__E} property of the given {@link Edge} to the
	 * given <i>headLp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #HEAD_LP__E} property.
	 * @param headLpParsed
	 *            The new value for the {@link #HEAD_LP__E} property.
	 */
	public static void setHeadLpParsed(Edge edge, Point headLpParsed) {
		setHeadLp(edge,
				serialize(DotLanguageSupport.POINT_SERIALIZER, headLpParsed));
	}

	/**
	 * Sets the {@link #HEIGHT__N} property of the given {@link Node} to the
	 * given value.
	 * 
	 * @param node
	 *            The {@link Node} whose property value to set.
	 * @param height
	 *            The new value of the {@link #HEIGHT__N} property.
	 */
	public static void setHeight(Node node, String height) {
		validate(AttributeContext.NODE, HEIGHT__N, height);
		node.getAttributes().put(HEIGHT__N, height);
	}

	/**
	 * Sets the {@link #HEIGHT__N} property of the given {@link Node} to the
	 * given value.
	 * 
	 * @param node
	 *            The {@link Node} whose property value to set.
	 * @param heightParsed
	 *            The new value of the {@link #HEIGHT__N} property.
	 */
	public static void setHeightParsed(Node node, Double heightParsed) {
		setHeight(node, heightParsed.toString());
	}

	/**
	 * Sets the {@link #ID__GNE} property of the given {@link Edge} to the given
	 * <i>id</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #ID__GNE} property.
	 * @param id
	 *            The new value for the {@link #ID__GNE} property.
	 */
	public static void setId(Edge edge, String id) {
		edge.attributesProperty().put(ID__GNE, id);
	}

	/**
	 * Sets the {@link #ID__GNE} property of the given {@link Graph} to the
	 * given <i>id</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #ID__GNE} property.
	 * @param id
	 *            The new value for the {@link #ID__GNE} property.
	 */
	public static void setId(Graph graph, String id) {
		graph.attributesProperty().put(ID__GNE, id);
	}

	/**
	 * Sets the {@link #ID__GNE} property of the given {@link Node} to the given
	 * <i>id</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #ID__GNE} property.
	 * @param id
	 *            The new value for the {@link #ID__GNE} property.
	 */
	public static void setId(Node node, String id) {
		node.attributesProperty().put(ID__GNE, id);
	}

	/**
	 * Sets the {@link #LABEL__GNE} property of the given {@link Edge} to the
	 * given <i>label</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #LABEL__GNE} property.
	 * @param label
	 *            The new value for the {@link #LABEL__GNE} property.
	 */
	public static void setLabel(Edge edge, String label) {
		edge.attributesProperty().put(LABEL__GNE, label);
	}

	/**
	 * Sets the {@link #LABEL__GNE} property of the given {@link Node} to the
	 * given <i>label</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #LABEL__GNE} property.
	 * @param label
	 *            The new value for the {@link #LABEL__GNE} property.
	 */
	public static void setLabel(Node node, String label) {
		node.attributesProperty().put(LABEL__GNE, label);
	}

	/**
	 * Sets the {@link #LAYOUT__G} property of the given {@link Graph} to the
	 * given <i>layout</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #LAYOUT__G} property.
	 * @param layout
	 *            The new value for the {@link #LAYOUT__G} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>layout</i> value is not supported, i.e. not
	 *             contained within {@link #LAYOUT__G__VALUES}.
	 */
	public static void setLayout(Graph graph, String layout) {
		validate(AttributeContext.GRAPH, LAYOUT__G, layout);
		graph.attributesProperty().put(LAYOUT__G, layout);
	}

	/**
	 * Sets the {@link #LP__E} property of the given {@link Edge} to the given
	 * <i>lp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #LP__E} property.
	 * @param lp
	 *            The new value for the {@link #LP__E} property.
	 */
	public static void setLp(Edge edge, String lp) {
		edge.attributesProperty().put(LP__E, lp);
	}

	/**
	 * Sets the {@link #LP__E} property of the given {@link Edge} to the given
	 * <i>lp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #LP__E} property.
	 * @param lpParsed
	 *            The new value for the {@link #LP__E} property.
	 */
	public static void setLpParsed(Edge edge, Point lpParsed) {
		setLp(edge, serialize(DotLanguageSupport.POINT_SERIALIZER, lpParsed));
	}

	/**
	 * Sets the {@link #_NAME__GNE} property of the given {@link Graph} to the
	 * given <i>name</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #_NAME__GNE} property.
	 * @param name
	 *            The new value for the {@link #_NAME__GNE} property.
	 */
	public static void setName(Edge edge, String name) {
		edge.attributesProperty().put(_NAME__GNE, name);
	}

	/**
	 * Sets the {@link #_NAME__GNE} property of the given {@link Graph} to the
	 * given <i>name</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #_NAME__GNE} property.
	 * @param name
	 *            The new value for the {@link #_NAME__GNE} property.
	 */
	public static void setName(Graph graph, String name) {
		graph.attributesProperty().put(_NAME__GNE, name);
	}

	/**
	 * Sets the {@link #_NAME__GNE} property of the given {@link Node} to the
	 * given <i>id</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #_NAME__GNE} property.
	 * @param id
	 *            The new value for the {@link #_NAME__GNE} property.
	 */
	public static void setName(Node node, String id) {
		node.attributesProperty().put(_NAME__GNE, id);
	}

	/**
	 * Sets the {@link #POS__NE} property of the given {@link Edge} to the given
	 * value.
	 * 
	 * @param edge
	 *            The {@link Edge} whose property value to set.
	 * @param pos
	 *            The new value of the {@link #POS__NE} property.
	 */
	public static void setPos(Edge edge, String pos) {
		validate(AttributeContext.EDGE, POS__NE, pos);
		edge.getAttributes().put(POS__NE, pos);
	}

	/**
	 * Sets the {@link #POS__NE} property of the given {@link Node} to the given
	 * value.
	 * 
	 * @param node
	 *            The {@link Node} whose property value to set.
	 * @param pos
	 *            The new value of the {@link #POS__NE} property.
	 */
	public static void setPos(Node node, String pos) {
		validate(AttributeContext.NODE, POS__NE, pos);
		node.getAttributes().put(POS__NE, pos);
	}

	/**
	 * Sets the {@link #POS__NE} property of the given {@link Edge} to the given
	 * value.
	 * 
	 * @param edge
	 *            The {@link Edge} whose property value to set.
	 * @param posParsed
	 *            The new value of the {@link #POS__NE} property.
	 */
	public static void setPosParsed(Edge edge, SplineType posParsed) {
		setPos(edge,
				serialize(DotLanguageSupport.SPLINETYPE_SERIALIZER, posParsed));
	}

	/**
	 * Sets the {@link #POS__NE} property of the given {@link Node} to the
	 * string representation of the given value.
	 * 
	 * @param node
	 *            The {@link Node} whose property value to set.
	 * @param posParsed
	 *            The new value of the {@link #POS__NE} property.
	 */
	public static void setPosParsed(Node node, Point posParsed) {
		setPos(node, serialize(DotLanguageSupport.POINT_SERIALIZER, posParsed));
	}

	/**
	 * Sets the {@link #RANKDIR__G} property of the given {@link Graph} to the
	 * given <i>rankdir</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #RANKDIR__G} property.
	 * @param rankdir
	 *            The new value for the {@link #RANKDIR__G} property.
	 */
	public static void setRankdir(Graph graph, String rankdir) {
		validate(AttributeContext.GRAPH, RANKDIR__G, rankdir);
		graph.attributesProperty().put(RANKDIR__G, rankdir);
	}

	/**
	 * Sets the {@link #RANKDIR__G} property of the given {@link Graph} to the
	 * given <i>rankdir</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #RANKDIR__G} property.
	 * @param rankdirParsed
	 *            The new value for the {@link #RANKDIR__G} property.
	 */
	public static void setRankdirParsed(Graph graph, Rankdir rankdirParsed) {
		setRankdir(graph, rankdirParsed.toString());
	}

	/**
	 * Sets the {@link #SPLINES__G} attribute of the given {@link Graph} to the
	 * given value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #SPLINES__G} property.
	 * @param splines
	 *            The new value for the {@link #SPLINES__G} property.
	 * @throws IllegalArgumentException
	 *             When the given value is not supported.
	 */
	public static void setSplines(Graph graph, String splines) {
		validate(AttributeContext.GRAPH, SPLINES__G, splines);
		graph.attributesProperty().put(SPLINES__G, splines);
	}

	/**
	 * Sets the {@link #STYLE__E} property of the given {@link Edge} to the
	 * given <i>style</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #STYLE__E} property.
	 * @param style
	 *            The new value for the {@link #STYLE__E} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>style</i> value is not supported, i.e. not
	 *             contained within {@link #STYLE__E__VALUES}.
	 */
	public static void setStyle(Edge edge, String style) {
		validate(AttributeContext.EDGE, STYLE__E, style);
		edge.attributesProperty().put(STYLE__E, style);
	}

	/**
	 * Sets the {@link #TAILLABEL__E} property of the given {@link Edge} to the
	 * given <i>label</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #TAILLABEL__E} property.
	 * @param label
	 *            The new value for the {@link #TAILLABEL__E} property.
	 */
	public static void setTailLabel(Edge edge, String label) {
		edge.attributesProperty().put(TAILLABEL__E, label);
	}

	/**
	 * Sets the {@link #TAIL_LP__E} property of the given {@link Edge} to the
	 * given <i>tailLp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #TAIL_LP__E} property.
	 * @param tailLp
	 *            The new value for the {@link #TAIL_LP__E} property.
	 */
	public static void setTailLp(Edge edge, String tailLp) {
		edge.attributesProperty().put(TAIL_LP__E, tailLp);
	}

	/**
	 * Sets the {@link #TAIL_LP__E} property of the given {@link Edge} to the
	 * given <i>tailLp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #TAIL_LP__E} property.
	 * @param tailLpParsed
	 *            The new value for the {@link #TAIL_LP__E} property.
	 */
	public static void setTailLpParsed(Edge edge, Point tailLpParsed) {
		setTailLp(edge,
				serialize(DotLanguageSupport.POINT_SERIALIZER, tailLpParsed));
	}

	/**
	 * Sets the {@link #_TYPE__G} property of the given {@link Graph} to the
	 * given <i>type</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #_TYPE__G} property.
	 * @param type
	 *            The new value for the {@link #_TYPE__G} property.
	 */
	public static void setType(Graph graph, String type) {
		if (!_TYPE__G__GRAPH.equals(type) && !_TYPE__G__DIGRAPH.equals(type)) {
			throw new IllegalArgumentException(
					"Cannot set graph attribute \"type\" to \"" + type
							+ "\"; supported values: " + _TYPE__G__GRAPH + ", "
							+ _TYPE__G__DIGRAPH);
		}
		graph.attributesProperty().put(_TYPE__G, type);
	}

	/**
	 * Sets the {@link #WIDTH__N} property of the given {@link Node} to the
	 * given value.
	 * 
	 * @param node
	 *            The {@link Node} whose property value to set.
	 * @param width
	 *            The new value of the {@link #WIDTH__N} property.
	 */
	public static void setWidth(Node node, String width) {
		validate(AttributeContext.NODE, WIDTH__N, width);
		node.getAttributes().put(WIDTH__N, width);
	}

	/**
	 * Sets the {@link #WIDTH__N} property of the given {@link Node} to the
	 * given value.
	 * 
	 * @param node
	 *            The {@link Node} whose property value to set.
	 * @param widthParsed
	 *            The new value of the {@link #WIDTH__N} property.
	 */
	public static void setWidthParsed(Node node, Double widthParsed) {
		setWidth(node, widthParsed.toString());
	}

	/**
	 * Sets the {@link #XLABEL__NE} property of the given {@link Edge} to the
	 * given <i>xLabel</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #XLABEL__NE} property.
	 * @param xLabel
	 *            The new value for the {@link #XLABEL__NE} property.
	 */
	public static void setXLabel(Edge edge, String xLabel) {
		edge.attributesProperty().put(XLABEL__NE, xLabel);
	}

	/**
	 * Sets the {@link #XLABEL__NE} property of the given {@link Node} to the
	 * given <i>xLabel</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #XLABEL__NE} property.
	 * @param xLabel
	 *            The new value for the {@link #XLABEL__NE} property.
	 */
	public static void setXLabel(Node node, String xLabel) {
		node.attributesProperty().put(XLABEL__NE, xLabel);
	}

	/**
	 * Sets the {@link #XLP__NE} property of the given {@link Edge} to the given
	 * <i>xlp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #XLP__NE} property.
	 * @param xlp
	 *            The new value for the {@link #XLP__NE} property.
	 */
	public static void setXlp(Edge edge, String xlp) {
		edge.attributesProperty().put(XLP__NE, xlp);
	}

	/**
	 * Sets the {@link #XLP__NE} property of the given {@link Node} to the given
	 * <i>xlp</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #XLP__NE} property.
	 * @param xlp
	 *            The new value for the {@link #XLP__NE} property.
	 */
	public static void setXlp(Node node, String xlp) {
		node.attributesProperty().put(XLP__NE, xlp);
	}

	/**
	 * Sets the {@link #XLP__NE} property of the given {@link Edge} to the given
	 * <i>xlp</i> value.
	 * 
	 * @param edge
	 *            The {@link Edge} for which to change the value of the
	 *            {@link #XLP__NE} property.
	 * @param xlpParsed
	 *            The new value for the {@link #XLP__NE} property.
	 */
	public static void setXlpParsed(Edge edge, Point xlpParsed) {
		setXlp(edge, serialize(DotLanguageSupport.POINT_SERIALIZER, xlpParsed));
	}

	/**
	 * Sets the {@link #XLP__NE} property of the given {@link Node} to the
	 * string value of the given <i>xlpParsed</i> value.
	 * 
	 * @param node
	 *            The {@link Node} for which to change the value of the
	 *            {@link #XLP__NE} property.
	 * @param xlpParsed
	 *            The new value for the {@link #XLP__NE} property.
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

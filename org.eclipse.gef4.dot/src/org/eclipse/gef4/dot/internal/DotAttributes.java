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

import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.gef4.dot.internal.parser.DotAttributesStandaloneSetup;
import org.eclipse.gef4.dot.internal.parser.dot.GraphType;
import org.eclipse.gef4.dot.internal.parser.dotAttributes.ArrowType;
import org.eclipse.gef4.dot.internal.parser.dotAttributes.ArrowType_ArrowShape;
import org.eclipse.gef4.dot.internal.parser.dotAttributes.Point;
import org.eclipse.gef4.dot.internal.parser.dotAttributes.SplineType;
import org.eclipse.gef4.dot.internal.parser.parser.antlr.DotAttributesParser;
import org.eclipse.gef4.dot.internal.parser.services.DotAttributesGrammarAccess;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.parser.IParseResult;

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
public class DotAttributes {

	// TODO: if platform is running, don't use standalone setup here
	private static final Injector injector = new DotAttributesStandaloneSetup()
			.createInjectorAndDoEMFRegistration();

	private static final DotAttributesParser dotAttributesParser = injector
			.getInstance(DotAttributesParser.class);

	private static final DotAttributesGrammarAccess dotAttributesGrammarAccess = injector
			.getInstance(DotAttributesGrammarAccess.class);

	/**
	 * Specifies the name of a graph, node, or edge (not an attribute), as
	 * retrieved through the graph, node_id, as well as edge_stmt and edgeRHS
	 * grammar rules.
	 */
	public static final String _NAME__GNE = "_name";

	/**
	 * Specifies the graph type. Possible values are defined by
	 * {@link #_TYPE__G__VALUES}.
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
	 * Defines all possible values for the {@link #_TYPE__G} property.
	 */
	public static final Set<String> _TYPE__G__VALUES = new HashSet<>(
			Arrays.asList(_TYPE__G__GRAPH, _TYPE__G__DIGRAPH));

	/**
	 * Specifies the 'arrowhead' attribute of an edge.
	 */
	public static final String ARROWHEAD__E = "arrowhead";

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
	 * This {@link #LAYOUT_G} value specifies that the "circo" layout algorithm
	 * is to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__CIRCO = "circo";

	/**
	 * This {@link #LAYOUT_G} value specifies that the "dot" layout algorithm is
	 * to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__DOT = "dot";

	/**
	 * This {@link #LAYOUT_G} value specifies that the "fdp" layout algorithm is
	 * to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__FDP = "fdp";

	/**
	 * This {@link #LAYOUT_G} value specifies that the "grid" layout algorithm
	 * is to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__GRID = "grid";

	/**
	 * This {@link #LAYOUT_G} value specifies that the "neato" layout algorithm
	 * is to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__NEATO = "neato";

	/**
	 * This {@link #LAYOUT_G} value specifies that the "osage" layout algorithm
	 * is to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__OSAGE = "osage";

	/**
	 * This {@link #LAYOUT_G} value specifies that the "sfdp" layout algorithm
	 * is to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__SFDP = "sfdp";

	/**
	 * This {@link #LAYOUT_G} value specifies that the "twopi" layout algorithm
	 * is to be used for laying out the graph.
	 */
	public static final String LAYOUT__G__TWOPI = "twopi";

	/**
	 * Defines the default value for the {@link #LAYOUT_G} property, which is
	 * {@link #LAYOUT__G__DOT}.
	 */
	public static final String LAYOUT__G__DEFAULT = LAYOUT__G__DOT;

	/**
	 * Defines all possible values for the {@link #LAYOUT_G} property.
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
	public static final String LAYOUT_G = "layout";

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
	 * which is used for laying out the graph. Possible values are defined by
	 * {@link #RANKDIR__G__VALUES}. The default value is defined by
	 * {@link #RANKDIR__G__DEFAULT}.
	 */
	public static final String RANKDIR__G = "rankdir";

	/**
	 * This {@link #RANKDIR__G} value specifies that the graph is to be laid out
	 * horizontally from left to right.
	 */
	public static final String RANKDIR__G__LR = "lr";

	/**
	 * This {@link #RANKDIR__G} value specifies that the graph is to be laid out
	 * vertically from top to bottom.
	 */
	public static final String RANKDIR__G__TD = "td";

	/**
	 * Defines the default value for the {@link #RANKDIR__G} property.
	 */
	public static final String RANKDIR__G__DEFAULT = RANKDIR__G__TD;

	/**
	 * Defines all possible values for the {@link #RANKDIR__G} property.
	 */
	public static final Set<String> RANKDIR__G__VALUES = new HashSet<>(
			Arrays.asList(RANKDIR__G__LR, RANKDIR__G__TD));

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
	 *            {@link #ARROWHEAD__E} property, parsed as an
	 *            {@link ArrowType_ArrowShape}.
	 * @return The value of the {@link #ARROWHEAD__E} property of the given
	 *         {@link Edge}.
	 */
	public static ArrowType getArrowHeadParsed(Edge edge) {
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getArrowTypeRule(),
				getArrowHead(edge));

		ArrowType arrowType = (ArrowType) parsedPropertyValue
				.getRootASTElement();
		return arrowType;
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
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getPointRule(), getHeadLp(edge));
		Point point = (Point) parsedPropertyValue.getRootASTElement();
		return point;
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
	 * Returns the value of the {@link #LAYOUT_G} property of the given
	 * {@link Graph}.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to return the value of the
	 *            {@link #LAYOUT_G} property.
	 * @return The value of the {@link #LAYOUT_G} property of the given
	 *         {@link Graph}.
	 */
	public static String getLayout(Graph graph) {
		return (String) graph.attributesProperty().get(LAYOUT_G);
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
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getPointRule(), getLp(edge));
		Point point = (Point) parsedPropertyValue.getRootASTElement();
		return point;
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
	 * {@link Edge}.
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
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getSplineTypeRule(), getPos(edge));
		SplineType splineType = (SplineType) parsedPropertyValue
				.getRootASTElement();
		return splineType;
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
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getPointRule(), getPos(node));
		Point point = (Point) parsedPropertyValue.getRootASTElement();
		return point;
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

	private static String getSyntaxErrorMessages(IParseResult parseResult) {
		StringBuilder sb = new StringBuilder();
		for (INode error : parseResult.getSyntaxErrors()) {
			SyntaxErrorMessage syntaxErrorMessage = error
					.getSyntaxErrorMessage();
			sb.append(syntaxErrorMessage.getMessage());
		}
		return sb.toString();
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
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getPointRule(), getTailLp(edge));
		Point point = (Point) parsedPropertyValue.getRootASTElement();
		return point;
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
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getPointRule(), getXlp(edge));
		Point point = (Point) parsedPropertyValue.getRootASTElement();
		return point;
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
		IParseResult parsedPropertyValue = parsePropertyValue(
				dotAttributesGrammarAccess.getPointRule(), getXlp(node));
		Point point = (Point) parsedPropertyValue.getRootASTElement();
		return point;
	}

	private static IParseResult parsePropertyValue(ParserRule rule,
			String propertyValue) {
		IParseResult parseResult = dotAttributesParser.parse(rule,
				new StringReader(propertyValue));
		return parseResult;
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
		edge.attributesProperty().put(ARROWHEAD__E, arrowHead);
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
		edge.attributesProperty().put(LP__E, headLp);
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
		try {
			Double.parseDouble(height);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Cannot set node attribute '" + HEIGHT__N + "' to '"
							+ height + "': parsing as double failed.");
		}
		node.getAttributes().put(HEIGHT__N, height);
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
	 * Sets the {@link #LAYOUT_G} property of the given {@link Graph} to the
	 * given <i>layout</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #LAYOUT_G} property.
	 * @param layout
	 *            The new value for the {@link #LAYOUT_G} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>layout</i> value is not supported, i.e. not
	 *             contained within {@link #LAYOUT__G__VALUES}.
	 */
	public static void setLayout(Graph graph, String layout) {
		if (!LAYOUT__G__VALUES.contains(layout)) {
			throw new IllegalArgumentException(
					"Cannot set graph attribute \"layout\" to \"" + layout
							+ "\"; supported values: " + LAYOUT__G__VALUES);
		}
		graph.attributesProperty().put(LAYOUT_G, layout);
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
		IParseResult parseResult = parsePropertyValue(
				dotAttributesGrammarAccess.getSplineTypeRule(), pos);
		if (parseResult.hasSyntaxErrors()) {
			throw new IllegalArgumentException(
					"Cannot set edge attribute '" + POS__NE + "' to '" + pos
							+ "': " + getSyntaxErrorMessages(parseResult));
		}
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
		IParseResult parseResult = parsePropertyValue(
				dotAttributesGrammarAccess.getPointRule(), pos);
		if (parseResult.hasSyntaxErrors()) {
			throw new IllegalArgumentException(
					"Cannot set node attribute '" + POS__NE + "' to '" + pos
							+ "': " + getSyntaxErrorMessages(parseResult));
		}
		node.getAttributes().put(POS__NE, pos);
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
	 * @throws IllegalArgumentException
	 *             when the given <i>rankdir</i> value is not supported, i.e.
	 *             not contained within {@link #RANKDIR__G__VALUES}.
	 */
	public static void setRankdir(Graph graph, String rankdir) {
		if (!RANKDIR__G__VALUES.contains(rankdir)) {
			throw new IllegalArgumentException(
					"Cannot set graph attribute \"rankdir\" to \"" + rankdir
							+ "\"; supported values: " + RANKDIR__G__VALUES);
		}
		graph.attributesProperty().put(RANKDIR__G, rankdir);
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
		if (!STYLE__E__VALUES.contains(style)) {
			throw new IllegalArgumentException(
					"Cannot set edge attribute \"style\" to \"" + style
							+ "\"; supported values: " + STYLE__E__VALUES);
		}
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
	 * Sets the {@link #_TYPE__G} property of the given {@link Graph} to the
	 * given <i>type</i> value.
	 * 
	 * @param graph
	 *            The {@link Graph} for which to change the value of the
	 *            {@link #_TYPE__G} property.
	 * @param type
	 *            The new value for the {@link #_TYPE__G} property.
	 * @throws IllegalArgumentException
	 *             when the given <i>type</i> value is not supported, i.e. not
	 *             contained within {@link #_TYPE__G__VALUES}.
	 */
	public static void setType(Graph graph, String type) {
		if (!_TYPE__G__VALUES.contains(type)) {
			throw new IllegalArgumentException(
					"Cannot set graph attribute \"type\" to \"" + type
							+ "\"; supported values: " + _TYPE__G__VALUES);
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
		try {
			Double.parseDouble(width);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Cannot set node attribute '" + WIDTH__N + "' to '" + width
							+ "': parsing as double failed.");
		}
		node.getAttributes().put(WIDTH__N, width);
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
	 * @param arrowType
	 *            the arrow type to check for validity
	 * 
	 * @return true if the arrowType is valid, false otherwise
	 */
	public static boolean isValidArrowType(String arrowType) {
		IParseResult parseResult = parsePropertyValue(
				dotAttributesGrammarAccess.getArrowTypeRule(), arrowType);
		return !parseResult.hasSyntaxErrors();
	}
}

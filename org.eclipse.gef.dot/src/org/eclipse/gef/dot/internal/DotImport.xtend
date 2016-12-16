/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - Merge DotInterpreter into DotImport (bug #491261)
 *                                 - Add support for all dot attributes (bug #461506)
 * 
 *******************************************************************************/
package org.eclipse.gef.dot.internal

import com.google.inject.Inject
import java.io.File
import java.io.StringReader
import java.util.List
import java.util.Map
import org.eclipse.gef.dot.internal.language.DotStandaloneSetup
import org.eclipse.gef.dot.internal.language.dot.AttrList
import org.eclipse.gef.dot.internal.language.dot.AttrStmt
import org.eclipse.gef.dot.internal.language.dot.Attribute
import org.eclipse.gef.dot.internal.language.dot.DotAst
import org.eclipse.gef.dot.internal.language.dot.DotFactory
import org.eclipse.gef.dot.internal.language.dot.DotGraph
import org.eclipse.gef.dot.internal.language.dot.EdgeRhs
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsNode
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode
import org.eclipse.gef.dot.internal.language.dot.NodeId
import org.eclipse.gef.dot.internal.language.dot.NodeStmt
import org.eclipse.gef.dot.internal.language.dot.Stmt
import org.eclipse.gef.dot.internal.language.dot.Subgraph
import org.eclipse.gef.dot.internal.language.parser.antlr.DotParser
import org.eclipse.gef.dot.internal.language.splines.Splines
import org.eclipse.gef.dot.internal.language.terminals.ID
import org.eclipse.gef.graph.Edge
import org.eclipse.gef.graph.Graph
import org.eclipse.gef.graph.Graph.Builder
import org.eclipse.gef.graph.Node

import static extension org.eclipse.gef.dot.internal.DotAttributes.*

/**
 * A parser that creates a {@link Graph} with {@link DotAttributes} from a Graphviz DOT string or file.
 * 
 * @author anyssen
 * 
 */
class DotImport {

	// fields are private by default 
	@Inject
	var static DotParser dotParser

	Builder graphBuilder
	Map<String, ID> globalGraphAttributes = newHashMap
	Map<String, ID> globalNodeAttributes = newHashMap
	Map<String, ID> globalEdgeAttributes = newHashMap

	// TODO: support a list of graphs
	def Graph importDot(String dotString) {
		if (dotParser == null) {

			// if we are not injected (standalone), create parser instance
			dotParser = new DotStandaloneSetup().createInjectorAndDoEMFRegistration().getInstance(DotParser)
		}

		var parseResult = dotParser.parse(new StringReader(dotString))

		if (parseResult.hasSyntaxErrors) {
			throw new IllegalArgumentException(
				"Given DOT string is not valid: " +
					parseResult.syntaxErrors.map[syntaxErrorMessage.message].join(","))
		}

		// TODO: use validator to semantically validate as well
		// TODO: return list of graphs rather than only the first one
		(parseResult.rootASTElement as DotAst).transformDotAst
	}

	// TODO: support a list of graphs
	def Graph importDot(File dotFile) {
		importDot(DotFileUtils.read(dotFile))
	}

	private def Graph transformDotAst(DotAst it) {

		// TODO: return list of graphs rather than only the first one
		graphs.map[transformDotGraph].head
	}

	private def Graph transformDotGraph(DotGraph it) {

		// clear global attributes, which only hold for each respective graph
		globalGraphAttributes.clear
		globalNodeAttributes.clear
		globalEdgeAttributes.clear

		// create a new graph builder and clear the nodes map
		graphBuilder = new Graph.Builder
		_createCache_createNode.clear

		// name (meta-attribute)
		if (name != null) {
			graphBuilder.attr(_NAME__GNE, name)
		}

		// type (meta-attribute)
		graphBuilder.attr(_TYPE__G, type)

		// process all statements except for graph attributes, they will be processed later
		stmts.filter[!(it instanceof Attribute)].forEach[transformStmt]

		// process the graph last, so we can initialize attributes of the
		// created graph object rather than using the builder we can thus
		// ensure attribute values get properly validated.
		val graph = graphBuilder.build

		val setter = [ String attributeName, (Graph, ID)=>void f |
			val attributeValue = getAttributeValue(attributeName)
			if (attributeValue != null) {
				f.apply(graph, attributeValue)
			} else if (globalGraphAttributes.containsKey(attributeName)) {
				f.apply(graph, globalGraphAttributes.get(attributeName))
			}
		]

		setter.apply(BGCOLOR__G, [g, value|g.setBgcolorRaw(value)])
		setter.apply(CLUSTERRANK__G, [g, value|g.setClusterrankRaw(value)])
		setter.apply(FONTCOLOR__GNE, [g, value|g.setFontcolorRaw(value)])
		setter.apply(LAYOUT__G, [g, value|g.setLayoutRaw(value)])
		setter.apply(OUTPUTORDER__G, [g, value|g.setOutputorderRaw(value)])
		setter.apply(PAGEDIR__G, [g, value|g.setPagedirRaw(value)])
		setter.apply(RANKDIR__G, [g, value|g.setRankdirRaw(value)])

		// splines
		var splines = getAttributeValue(SPLINES__G)
		if (splines == null && globalGraphAttributes.containsKey(SPLINES__G)) {
			splines = globalGraphAttributes.get(SPLINES__G)
		}
		if (splines != null) {
			// XXX: splines can either be a defined enum value or a bool value
			// (which is mapped to respective enum values) we use the enum
			// values alone and thus map the bool value here
			val Boolean booleanValue = DotLanguageSupport.parseAttributeValue(DotLanguageSupport.BOOL_PARSER,
				splines.toValue)
			if (booleanValue != null) {
				graph.setSplinesParsed(
					if (Boolean.TRUE.equals(booleanValue))
						Splines.TRUE
					else
						Splines.FALSE
				)
			} else {
				graph.setSplinesRaw(splines)
			}
		}

		graph
	}

	private def Node transformNodeId(NodeId it) {

		// create an empty attribute lists indicating no local node attribute definitions
		transformNodeId(#[DotFactory.eINSTANCE.createAttrList])
	}

	private def Node transformNodeId(NodeId it, List<AttrList> attrLists) {
		val isExistingNode = _createCache_createNode.containsKey(CollectionLiterals.newArrayList(name))

		val node = name.createNode

		val setter = [ String attributeName, (Node, ID)=>void f |
			val attributeValue = attrLists.getAttributeValue(attributeName)
			if (attributeValue != null) {
				f.apply(node, attributeValue)
			} else if (!isExistingNode && globalNodeAttributes.containsKey(attributeName)) {

				// consider the global nodes attributes only if the node has just been created
				f.apply(node, globalNodeAttributes.get(attributeName))
			}
		]

		setter.apply(COLOR__NE, [n, value|n.setColorRaw(value)])
		setter.apply(COLORSCHEME__GNE, [n, value|n.setColorschemeRaw(value)])
		setter.apply(DISTORTION__N, [n, value|n.setDistortionRaw(value)])
		setter.apply(FILLCOLOR__NE, [n, value|n.setFillcolorRaw(value)])
		setter.apply(FIXEDSIZE__N, [n, value|n.setFixedsizeRaw(value)])
		setter.apply(FONTCOLOR__GNE, [n, value|n.setFontcolorRaw(value)])
		setter.apply(HEIGHT__N, [n, value|n.setHeightRaw(value)])
		setter.apply(ID__GNE, [n, value|n.setIdRaw(value)])
		setter.apply(LABEL__GNE, [n, value|n.setLabelRaw(value)])
		setter.apply(POS__NE, [n, value|n.setPosRaw(value)])
		setter.apply(SHAPE__N, [n, value|n.setShapeRaw(value)])
		setter.apply(SIDES__N, [n, value|n.setSidesRaw(value)])
		setter.apply(SKEW__N, [n, value|n.setSkewRaw(value)])
		setter.apply(STYLE__GNE, [n, value|n.setStyleRaw(value)])
		setter.apply(WIDTH__N, [n, value|n.setWidthRaw(value)])
		setter.apply(XLABEL__NE, [n, value|n.setXlabelRaw(value)])
		setter.apply(XLP__NE, [n, value|n.setXlpRaw(value)])

		node
	}

	/*
	********************************************************************************************************************************
	*  dynamic dispatch methods 
	********************************************************************************************************************************
	*/
	private def dispatch void transformStmt(Stmt it) {
		System.err.println("DotImport cannot transform Stmt: " + it);
	}

	private def dispatch void transformStmt(AttrStmt it) {
		switch type {
			case GRAPH: {

				// global graph attributes
				attrLists.forEach [
					attributes.forEach [
						globalGraphAttributes.put(name.toValue, value)
					]
				]
			}
			case NODE: {

				// global node attributes
				attrLists.forEach [
					attributes.forEach [
						globalNodeAttributes.put(name.toValue, value)
					]
				]
			}
			case EDGE: {

				// global edge attributes
				attrLists.forEach [
					attributes.forEach [
						globalEdgeAttributes.put(name.toValue, value)
					]
				]
			}
		}
	}

	private def dispatch void transformStmt(NodeStmt it) {
		node.transformNodeId(attrLists)
	}

	private def dispatch void transformStmt(EdgeStmtNode it) {
		var sourceNode = node.transformNodeId
		for (EdgeRhs edgeRhs : edgeRHS) {
			switch edgeRhs {
				EdgeRhsNode: {
					val targetNode = edgeRhs.node.transformNodeId
					createEdge(sourceNode, edgeRhs.op.literal, targetNode, attrLists)

					// current target node may be source for next EdgeRHS
					sourceNode = targetNode
				}
				default: {
					System.err.println("DotImport cannot transform EdgeStmtNode: " + it);
				}
			}
		}
	}

	private def dispatch void transformStmt(Subgraph it) {

		//FIXME: we ignore subgraphs for now, but transform nested statements
		it.stmts.forEach[transformStmt]
	}

	private def create new Node.Builder().buildNode() createNode(ID nodeName) {
		_setNameRaw(nodeName)
		graphBuilder.nodes(it)
	}

	def private void createEdge(Node sourceNode, String edgeOp, Node targetNode, List<AttrList> attrLists) {
		val edge = new Edge.Builder(sourceNode, targetNode).buildEdge()

		val setter = [ String attributeName, (Edge, ID)=>void f |
			val attributeValue = attrLists.getAttributeValue(attributeName)
			if (attributeValue != null) {
				f.apply(edge, attributeValue)
			} else if (globalEdgeAttributes.containsKey(attributeName)) {
				f.apply(edge, globalEdgeAttributes.get(attributeName))
			}
		]

		setter.apply(ARROWHEAD__E, [e, value|e.setArrowheadRaw(value)])
		setter.apply(ARROWSIZE__E, [e, value|e.setArrowsizeRaw(value)])
		setter.apply(ARROWTAIL__E, [e, value|e.setArrowtailRaw(value)])
		setter.apply(COLOR__NE, [e, value|e.setColorRaw(value)])
		setter.apply(COLORSCHEME__GNE, [e, value|e.setColorschemeRaw(value)])
		setter.apply(DIR__E, [e, value|e.setDirRaw(value)])
		setter.apply(FILLCOLOR__NE, [e, value|e.setFillcolorRaw(value)])
		setter.apply(FONTCOLOR__GNE, [e, value|e.setFontcolorRaw(value)])
		setter.apply(HEAD_LP__E, [e, value|e.setHeadLpRaw(value)])
		setter.apply(HEADLABEL__E, [e, value|e.setHeadlabelRaw(value)])
		setter.apply(ID__GNE, [e, value|e.setIdRaw(value)])
		setter.apply(LABEL__GNE, [e, value|e.setLabelRaw(value)])
		setter.apply(LABELFONTCOLOR__E, [e, value|e.setLabelfontcolorRaw(value)])
		setter.apply(LP__GE, [e, value|e.setLpRaw(value)])
		setter.apply(POS__NE, [e, value|e.setPosRaw(value)])
		setter.apply(STYLE__GNE, [e, value|e.setStyleRaw(value)])
		setter.apply(TAILLABEL__E, [e, value|e.setTaillabelRaw(value)])
		setter.apply(TAIL_LP__E, [e, value|e.setTailLpRaw(value)])
		setter.apply(XLABEL__NE, [e, value|e.setXlabelRaw(value)])
		setter.apply(XLP__NE, [e, value|e.setXlpRaw(value)])

		graphBuilder.edges(edge)
	}

	def static ID getAttributeValue(DotGraph graph, String name) {
		for (stmt : graph.stmts) {
			var ID value = switch stmt {
				//no need to consider AttrStmt here, because the global graph attributes are evaluated somewhere else
				Attribute:
					stmt.getAttributeValue(name)
			}
			if (value != null) {
				return value
			}
		}
		null
	}

	/**
	 * Returns the value of the first attribute with the give name or
	 * <code>null</code> if no attribute could be found.
	 * 
	 * @param attrLists
	 *            The {@link AttrList}s to search.
	 * @param name
	 *            The name of the attribute whose value is to be retrieved.
	 * @return The attribute value or <code>null</code> in case the attribute
	 *         could not be found.
	 */
	def static ID getAttributeValue(List<AttrList> attrLists, String name) {
		for (AttrList attrList : attrLists) {
			val value = attrList.getAttributeValue(name)
			if (value != null) {
				return value
			}
		}
		null
	}

	def private static ID getAttributeValue(AttrList attrList, String name) {
		attrList.attributes.findFirst[it.name.toValue == name]?.value
	}

	def private static ID getAttributeValue(Attribute attribute, String name) {
		if (attribute.name.toValue.equals(name)) {
			return attribute.value
		}
		null
	}
}

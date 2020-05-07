/*******************************************************************************
 * Copyright (c) 2016, 2019 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen    (itemis AG) - initial API and implementation
 *     Tamas Miklossy     (itemis AG) - Merge DotInterpreter into DotImport (bug #491261)
 *                                    - Add support for all dot attributes (bug #461506)
 *     Zoey Gerrit Prigge (itemis AG) - Add support for all dot attributes (bug #461506)
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
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsNode
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode
import org.eclipse.gef.dot.internal.language.dot.NodeId
import org.eclipse.gef.dot.internal.language.dot.NodeStmt
import org.eclipse.gef.dot.internal.language.dot.Stmt
import org.eclipse.gef.dot.internal.language.dot.Subgraph
import org.eclipse.gef.dot.internal.language.parser.antlr.DotParser
import org.eclipse.gef.dot.internal.language.terminals.ID
import org.eclipse.gef.graph.Edge
import org.eclipse.gef.graph.Graph
import org.eclipse.gef.graph.Node
import org.eclipse.xtext.parser.IParser

import static extension org.eclipse.gef.dot.internal.DotAttributes.*
import static extension org.eclipse.gef.dot.internal.language.DotAstHelper.*

/**
 * A parser that creates a {@link Graph} with {@link DotAttributes} from a Graphviz DOT string or file.
 * The created {@link Graph} follows the structure of the DOT input very closely.
 * Subgraphs (including clusters) are represented by a {@link Node} with a nested {@link Graph},
 * where the graph holds all attributes (like the name). If a node is used in multiple (sub-)graphs,
 * it will be contained in the graph where it is defined (first occurrence).
 *
 * @author anyssen
 *
 */
class DotImport {

	@Inject
	var static IParser dotParser

	private static def IParser getDotParser() {
		if (dotParser === null) {

			// if we are not injected (standalone), create parser instance
			dotParser = new DotStandaloneSetup().createInjectorAndDoEMFRegistration().getInstance(DotParser)
		}
		return dotParser
	}

	def List<Graph> importDot(File dotFile) {
		DotFileUtils.read(dotFile).importDot
	}

	def List<Graph> importDot(String dotString) {
		var parseResult = getDotParser.parse(new StringReader(dotString))

		if (parseResult.hasSyntaxErrors) {
			throw new IllegalArgumentException(
				"Given DOT string is not valid: " +
					parseResult.syntaxErrors.map[syntaxErrorMessage.message].join(","))
		}

		(parseResult.rootASTElement as DotAst).importDot
	}

	def List<Graph> importDot(DotAst dotAst) {
		// TODO: use validator to semantically validate
		dotAst.graphs.map[transformDotGraph].filterNull.toList
	}

	private def Map<String, ID> create newHashMap globalGraphAttributes(Graph.Builder context) {
	}

	private def Map<String, ID> create newHashMap globalNodeAttributes(Graph.Builder context) {
	}

	private def Map<String, ID> create newHashMap globalEdgeAttributes(Graph.Builder context) {
	}

	private def Graph transformDotGraph(DotGraph it) {

		// clear global attributes, which only hold for each respective graph
		_createCache_globalGraphAttributes.clear
		_createCache_globalNodeAttributes.clear
		_createCache_globalEdgeAttributes.clear

		// clear the nodes and subgraphs create caches
		_createCache_createNode.clear
		_createCache_createSubgraph.clear

		// create a new graph builder
		val graphBuilder = new Graph.Builder

		// name (meta-attribute)
		if (name !== null) {
			graphBuilder.attr(_NAME__GNE, name)
		}

		// type (meta-attribute)
		graphBuilder.attr(_TYPE__G, type)

		// process all statements except for graph attributes, they will be processed later
		stmts.filter[!(it instanceof Attribute)].forEach[transformStmt(graphBuilder)]

		// process the graph last, so we can initialize attributes of the
		// created graph object rather than using the builder we can thus
		// ensure attribute values get properly validated.
		val graph = graphBuilder.build

		// apply all global graph attributes we have processed before
		val setter = [ String attributeName, (Graph, ID)=>void f |
			val attributeValue = getAttributeValue(attributeName)
			if (attributeValue !== null) {
				f.apply(graph, attributeValue)
			} else if (globalGraphAttributes(graphBuilder).containsKey(attributeName)) {
				f.apply(graph, globalGraphAttributes(graphBuilder).get(attributeName))
			}
		]

		// graph attributes
		setter.apply(BB__GC, [g, value|g.setBbRaw(value)])
		setter.apply(BGCOLOR__GC, [g, value|g.setBgcolorRaw(value)])
		setter.apply(CLUSTERRANK__G, [g, value|g.setClusterrankRaw(value)])
		setter.apply(COLORSCHEME__GCNE, [g, value|g.setColorschemeRaw(value)])
		setter.apply(FONTCOLOR__GCNE, [g, value|g.setFontcolorRaw(value)])
		setter.apply(FONTNAME__GCNE, [g, value|g.setFontnameRaw(value)])
		setter.apply(FONTSIZE__GCNE, [g, value|g.setFontsizeRaw(value)])
		setter.apply(LABEL__GCNE, [g, value|g.setLabelRaw(value)])
		setter.apply(LAYOUT__G, [g, value|g.setLayoutRaw(value)])
		setter.apply(OUTPUTORDER__G, [g, value|g.setOutputorderRaw(value)])
		setter.apply(PAGEDIR__G, [g, value|g.setPagedirRaw(value)])
		setter.apply(RANKDIR__G, [g, value|g.setRankdirRaw(value)])
		setter.apply(SPLINES__G, [g, value|g.setSplinesRaw(value)])

		graph
	}

	private def Node transformNodeId(NodeId it, Graph.Builder graphBuilder) {

		// create an empty attribute list indicating no local node attribute definitions
		transformNodeId(#[DotFactory.eINSTANCE.createAttrList], graphBuilder)
	}

	private def Node transformNodeId(NodeId it, List<AttrList> attrLists, Graph.Builder graphBuilder) {
		val isExistingNode = _createCache_createNode.containsKey(CollectionLiterals.newArrayList(name.toValue))

		val node = name.toValue.createNode
		if (!isExistingNode) {
			node._setNameRaw(name)
			graphBuilder.nodes(node)
		}

		val setter = [ String attributeName, (Node, ID)=>void f |
			val attributeValue = attrLists.getAttributeValue(attributeName)
			if (attributeValue !== null) {
				f.apply(node, attributeValue)
			} else if (!isExistingNode && globalNodeAttributes(graphBuilder).containsKey(attributeName)) {
				//XXX:  consider the global nodes attributes only if the node has just been created
				f.apply(node, globalNodeAttributes(graphBuilder).get(attributeName))
			}
		]

		setter.apply(COLOR__CNE, [n, value|n.setColorRaw(value)])
		setter.apply(COLORSCHEME__GCNE, [n, value|n.setColorschemeRaw(value)])
		setter.apply(DISTORTION__N, [n, value|n.setDistortionRaw(value)])
		setter.apply(FILLCOLOR__CNE, [n, value|n.setFillcolorRaw(value)])
		setter.apply(FIXEDSIZE__N, [n, value|n.setFixedsizeRaw(value)])
		setter.apply(FONTCOLOR__GCNE, [n, value|n.setFontcolorRaw(value)])
		setter.apply(FONTNAME__GCNE, [n, value|n.setFontnameRaw(value)])
		setter.apply(FONTSIZE__GCNE, [n, value|n.setFontsizeRaw(value)])
		setter.apply(HEIGHT__N, [n, value|n.setHeightRaw(value)])
		setter.apply(ID__GCNE, [n, value|n.setIdRaw(value)])
		setter.apply(LABEL__GCNE, [n, value|n.setLabelRaw(value)])
		setter.apply(PENWIDTH__CNE, [g, value|g.setPenwidthRaw(value)])
		setter.apply(POS__NE, [n, value|n.setPosRaw(value)])
		setter.apply(SHAPE__N, [n, value|n.setShapeRaw(value)])
		setter.apply(SIDES__N, [n, value|n.setSidesRaw(value)])
		setter.apply(SKEW__N, [n, value|n.setSkewRaw(value)])
		setter.apply(STYLE__GCNE, [n, value|n.setStyleRaw(value)])
		setter.apply(TOOLTIP__CNE, [n, value|n.setTooltipRaw(value)])
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
	private def dispatch void transformStmt(Stmt it, Graph.Builder graphBuilder) {
		System.err.println("DotImport cannot transform Stmt: " + it)
	}

	private def dispatch void transformStmt(AttrStmt it, Graph.Builder graphBuilder) {
		switch type {
			case GRAPH: {

				// global graph attributes
				attrLists.forEach [
					attributes.forEach [
						globalGraphAttributes(graphBuilder).put(name.toValue, value)
					]
				]
			}
			case NODE: {

				// global node attributes
				attrLists.forEach [
					attributes.forEach [
						globalNodeAttributes(graphBuilder).put(name.toValue, value)
					]
				]
			}
			case EDGE: {

				// global edge attributes
				attrLists.forEach [
					attributes.forEach [
						globalEdgeAttributes(graphBuilder).put(name.toValue, value)
					]
				]
			}
		}
	}

	private def dispatch void transformStmt(NodeStmt it, Graph.Builder graphBuilder) {
		node.transformNodeId(attrLists, graphBuilder)
	}

	private def dispatch void transformStmt(EdgeStmtNode it, Graph.Builder graphBuilder) {
		var sourceNode = node.transformNodeId(graphBuilder)
		for (edgeRhs : edgeRHS) {
			switch edgeRhs {
				EdgeRhsNode: {
					val targetNode = edgeRhs.node.transformNodeId(graphBuilder)
					graphBuilder.edges(createEdge(sourceNode, edgeRhs.op.literal, targetNode, attrLists, graphBuilder))

					// current target node may be source for next EdgeRHS
					sourceNode = targetNode
				}
				default: {
					System.err.println("DotImport cannot transform EdgeStmtNode: " + it)
				}
			}
		}
	}

	private def dispatch void transformStmt(Subgraph it, Graph.Builder graphBuilder) {
		// anonymous subgraphs cannot be 'merged', which is why we have to create a new subgraph for each
		val isExistingSubgraph = name !== null && _createCache_createSubgraph.containsKey(CollectionLiterals.newArrayList(name.toValue))
		val subgraphBuilder = new Graph.Builder
		val subgraphNode = if(name === null) System::identityHashCode(subgraphBuilder).toString.createSubgraph else name.toValue.createSubgraph

		if (name !== null) {
			subgraphBuilder.attr(_NAME__GNE, name)
		}

		// We evaluate global attributes from 'outer' scopes, by transferring global graph (applicable to subgraph, cluster),
		// node, and edge attributes as initial global attributes of the nested graph process all statements.
		globalGraphAttributes(subgraphBuilder).putAll(globalGraphAttributes(graphBuilder))
		globalNodeAttributes(subgraphBuilder).putAll(globalNodeAttributes(graphBuilder))
		globalEdgeAttributes(subgraphBuilder).putAll(globalEdgeAttributes(graphBuilder))

		// process all statements except for subgraph/cluster attributes, they will be processed later
		stmts.filter[!(it instanceof Attribute)].forEach[transformStmt(subgraphBuilder)]

		val subgraph = subgraphBuilder.build

		if (!isExistingSubgraph) {
			subgraphNode.nestedGraph = subgraph
			subgraph.nestingNode = subgraphNode
			graphBuilder.nodes(subgraphNode)
		} else {

			// merge into existing subgraph
			subgraphNode.nestedGraph.attributes.putAll(subgraph.attributes)
			subgraphNode.nestedGraph.nodes.addAll(subgraph.nodes.filter[!subgraphNode.nestedGraph.nodes.contains(it)])
			subgraphNode.nestedGraph.edges.addAll(subgraph.edges.filter[!subgraphNode.nestedGraph.nodes.contains(it)])
		}

		// apply all global cluster and subgraph attributes to subgraph
		val setter = [ String attributeName, (Graph, ID)=>void f |
			val attributeValue = getAttributeValue(attributeName)
			if (attributeValue !== null) {
				f.apply(subgraph, attributeValue)
			} else if (globalGraphAttributes(subgraphBuilder).containsKey(attributeName)) {
				f.apply(subgraph, globalGraphAttributes(subgraphBuilder).get(attributeName))
			}
		]

		// subgraph/cluster attributes
		setter.apply(BB__GC, [g, value|g.setBbRaw(value)])
		setter.apply(BGCOLOR__GC, [g, value|g.setBgcolorRaw(value)])
		setter.apply(COLOR__CNE, [g, value|g.setColorRaw(value)])
		setter.apply(COLORSCHEME__GCNE, [g, value|g.setColorschemeRaw(value)])
		setter.apply(FILLCOLOR__CNE, [g, value|g.setFillcolorRaw(value)])
		setter.apply(FONTCOLOR__GCNE, [g, value|g.setFontcolorRaw(value)])
		setter.apply(FONTNAME__GCNE, [g, value|g.setFontnameRaw(value)])
		setter.apply(FONTSIZE__GCNE, [g, value|g.setFontsizeRaw(value)])
		setter.apply(ID__GCNE, [g, value|g.setIdRaw(value)])
		setter.apply(LABEL__GCNE, [g, value|g.setLabelRaw(value)])
		setter.apply(LP__GCE, [g, value|g.setLpRaw(value)])
		setter.apply(RANK__S, [g, value|g.setRankRaw(value)])
		setter.apply(PENWIDTH__CNE, [g, value|g.setPenwidthRaw(value)])
		setter.apply(STYLE__GCNE, [g, value|g.setStyleRaw(value)])
		setter.apply(TOOLTIP__CNE, [g, value|g.setTooltipRaw(value)])
	}

	private def create new Node.Builder().buildNode() createSubgraph(String subgraphName) {
	}

	private def create new Node.Builder().buildNode() createNode(String nodeName) {
	}

	private def Edge createEdge(Node sourceNode, String edgeOp, Node targetNode, List<AttrList> attrLists,
		Graph.Builder graphBuilder) {
		val edge = new Edge.Builder(sourceNode, targetNode).buildEdge()

		val setter = [ String attributeName, (Edge, ID)=>void f |
			val attributeValue = attrLists.getAttributeValue(attributeName)
			if (attributeValue !== null) {
				f.apply(edge, attributeValue)
			} else if (globalEdgeAttributes(graphBuilder).containsKey(attributeName)) {
				f.apply(edge, globalEdgeAttributes(graphBuilder).get(attributeName))
			}
		]

		setter.apply(ARROWHEAD__E, [e, value|e.setArrowheadRaw(value)])
		setter.apply(ARROWSIZE__E, [e, value|e.setArrowsizeRaw(value)])
		setter.apply(ARROWTAIL__E, [e, value|e.setArrowtailRaw(value)])
		setter.apply(COLOR__CNE, [e, value|e.setColorRaw(value)])
		setter.apply(COLORSCHEME__GCNE, [e, value|e.setColorschemeRaw(value)])
		setter.apply(DIR__E, [e, value|e.setDirRaw(value)])
		setter.apply(EDGETOOLTIP__E, [e, value|e.setEdgetooltipRaw(value)])
		setter.apply(FILLCOLOR__CNE, [e, value|e.setFillcolorRaw(value)])
		setter.apply(FONTCOLOR__GCNE, [e, value|e.setFontcolorRaw(value)])
		setter.apply(FONTNAME__GCNE, [e, value|e.setFontnameRaw(value)])
		setter.apply(FONTSIZE__GCNE, [e, value|e.setFontsizeRaw(value)])
		setter.apply(HEAD_LP__E, [e, value|e.setHeadLpRaw(value)])
		setter.apply(HEADLABEL__E, [e, value|e.setHeadlabelRaw(value)])
		setter.apply(HEADPORT__E, [e, value|e.setHeadportRaw(value)])
		setter.apply(HEADTOOLTIP__E, [e, value|e.setHeadtooltipRaw(value)])
		setter.apply(ID__GCNE, [e, value|e.setIdRaw(value)])
		setter.apply(LABEL__GCNE, [e, value|e.setLabelRaw(value)])
		setter.apply(LABELFONTCOLOR__E, [e, value|e.setLabelfontcolorRaw(value)])
		setter.apply(LABELFONTNAME__E, [e, value|e.setLabelfontnameRaw(value)])
		setter.apply(LABELFONTSIZE__E, [e, value|e.setLabelfontsizeRaw(value)])
		setter.apply(LABELTOOLTIP__E, [e, value|e.setLabeltooltipRaw(value)])
		setter.apply(LP__GCE, [e, value|e.setLpRaw(value)])
		setter.apply(PENWIDTH__CNE, [g, value|g.setPenwidthRaw(value)])
		setter.apply(POS__NE, [e, value|e.setPosRaw(value)])
		setter.apply(STYLE__GCNE, [e, value|e.setStyleRaw(value)])
		setter.apply(TAILLABEL__E, [e, value|e.setTaillabelRaw(value)])
		setter.apply(TAILPORT__E, [e, value|e.setTailportRaw(value)])
		setter.apply(TAILTOOLTIP__E, [e, value|e.setTailtooltipRaw(value)])
		setter.apply(TAIL_LP__E, [e, value|e.setTailLpRaw(value)])
		setter.apply(TOOLTIP__CNE, [e, value|e.setTooltipRaw(value)])
		setter.apply(XLABEL__NE, [e, value|e.setXlabelRaw(value)])
		setter.apply(XLP__NE, [e, value|e.setXlpRaw(value)])

		edge
	}

}

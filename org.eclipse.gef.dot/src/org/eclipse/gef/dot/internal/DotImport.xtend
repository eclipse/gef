/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - merge DotInterpreter into DotImport (bug #491261)
 * 
 *******************************************************************************/
package org.eclipse.gef.dot.internal

import com.google.inject.Injector
import java.io.File
import java.io.StringReader
import java.util.List
import java.util.Map
import org.eclipse.gef.dot.internal.parser.DotStandaloneSetup
import org.eclipse.gef.dot.internal.parser.conversion.DotTerminalConverters
import org.eclipse.gef.dot.internal.parser.dot.AttrList
import org.eclipse.gef.dot.internal.parser.dot.AttrStmt
import org.eclipse.gef.dot.internal.parser.dot.Attribute
import org.eclipse.gef.dot.internal.parser.dot.DotAst
import org.eclipse.gef.dot.internal.parser.dot.DotFactory
import org.eclipse.gef.dot.internal.parser.dot.DotGraph
import org.eclipse.gef.dot.internal.parser.dot.EdgeRhs
import org.eclipse.gef.dot.internal.parser.dot.EdgeRhsNode
import org.eclipse.gef.dot.internal.parser.dot.EdgeRhsSubgraph
import org.eclipse.gef.dot.internal.parser.dot.EdgeStmtNode
import org.eclipse.gef.dot.internal.parser.dot.GraphType
import org.eclipse.gef.dot.internal.parser.dot.NodeId
import org.eclipse.gef.dot.internal.parser.dot.NodeStmt
import org.eclipse.gef.dot.internal.parser.dot.Stmt
import org.eclipse.gef.dot.internal.parser.parser.antlr.DotParser
import org.eclipse.gef.dot.internal.parser.splines.Splines
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
	val static Injector dotInjector = new DotStandaloneSetup().createInjectorAndDoEMFRegistration
	val static DotParser dotParser = dotInjector.getInstance(DotParser)
	
	Builder graphBuilder
	Map<String, String> globalGraphAttributes = newHashMap
	Map<String, String> globalNodeAttributes = newHashMap
	Map<String, String> globalEdgeAttributes = newHashMap

	// TODO: support a list of graphs
	def Graph importDot(String dotString) {
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
	
	
    private def Graph transformDotAst(DotAst it){
		// TODO: return list of graphs rather than only the first one
		graphs.map[transformDotGraph].head
	}
	
	private def Graph transformDotGraph(DotGraph it){
		// clear global attributes, which only hold for each respective graph
		globalGraphAttributes.clear
		globalNodeAttributes.clear
		globalEdgeAttributes.clear
		
		// create a new graph builder and clear the nodes map
		graphBuilder = new Graph.Builder
		_createCache_createNode.clear
		
		// name (meta-attribute)
		val escapedName = name.escaped
		if (escapedName != null) {
			graphBuilder.attr(_NAME__GNE, escapedName)
		}
		
		// type (meta-attribute)
		graphBuilder.attr(_TYPE__G,
				if (type == GraphType.GRAPH) 
					_TYPE__G__GRAPH 
				else 
					_TYPE__G__DIGRAPH
		)
		
		// process all statements except for graph attributes, they will be processed later
		stmts.filter[!(it instanceof Attribute)].forEach[transformStmt]
		
		// process the graph last, so we can initialize attributes of the
		// created graph object rather than using the builder we can thus
		// ensure attribute values get properly validated.
		val graph = graphBuilder.build
		
		val setter = [
			String attributeName, (Graph, String)=>void f | 
			val attributeValue = getAttributeValue(attributeName)
			if (attributeValue != null) {
				f.apply(graph, attributeValue)
			} else if (globalGraphAttributes.containsKey(attributeName)) {
				f.apply(graph, globalGraphAttributes.get(attributeName))
			}
		]
		
		setter.apply(LAYOUT__G,  [g, value | g.setLayout(value) ])
		setter.apply(RANKDIR__G, [g, value | g.setRankdir(value)])
		
		// splines
		var splines = getAttributeValue(SPLINES__G)
		if (splines == null && globalGraphAttributes.containsKey(SPLINES__G)) {
			splines = globalGraphAttributes.get(SPLINES__G)
		}
		if (splines != null) {
			// XXX: splines can either be a defined enum value or a bool value
			// (which is mapped to respective enum values) we use the enum
			// values alone and thus map the bool value here
			val Boolean booleanValue = DotLanguageSupport.parseAttributeValue(
					DotLanguageSupport.BOOL_PARSER, splines)
			if (booleanValue != null) {
				graph.setSplinesParsed(
						if (Boolean.TRUE.equals(booleanValue))
							Splines.TRUE
						else 
							Splines.FALSE
					)
			} else {
				graph.setSplines(splines)
			}
		}		
		
		graph		
	}
	
	private def Node transformNodeId(NodeId it) {
		// create an empty attribute lists indicating no local node attribute definitions
		transformNodeId(#[DotFactory.eINSTANCE.createAttrList])
	}
	
	private def Node transformNodeId(NodeId it, List<AttrList> attrLists) {
		val isExistingNode = _createCache_createNode.containsKey(CollectionLiterals.newArrayList(name.escaped))
		
		val node = name.escaped.createNode
	
		val setter = [
			String attributeName, (Node, String)=>void f | 
			val attributeValue = attrLists.getAttributeValue(attributeName)
			if (attributeValue != null) {
				f.apply(node, attributeValue)
			} else if (!isExistingNode && globalNodeAttributes.containsKey(attributeName)) {
				// consider the global nodes attributes only if the node has just been created
				f.apply(node, globalNodeAttributes.get(attributeName))
			}
		]
		
		setter.apply(DISTORTION__N, [n, value | n.setDistortion(value)])
		setter.apply(FIXEDSIZE__N,  [n, value | n.setFixedSize(value) ])
		setter.apply(HEIGHT__N,     [n, value | n.setHeight(value)    ])
		setter.apply(ID__GNE,       [n, value | n.setId(value)        ])
		setter.apply(LABEL__GNE,    [n, value | n.setLabel(value)     ])
		setter.apply(POS__NE,       [n, value | n.setPos(value)       ])
		setter.apply(SHAPE__N,      [n, value | n.setShape(value)     ])
		setter.apply(SIDES__N,      [n, value | n.setSides(value)     ])
		setter.apply(SKEW__N,       [n, value | n.setSkew(value)      ])
		setter.apply(STYLE__GNE,    [n, value | n.setStyle(value)     ])
		setter.apply(WIDTH__N,      [n, value | n.setWidth(value)     ])
		setter.apply(XLABEL__NE,    [n, value | n.setXLabel(value)    ])
		setter.apply(XLP__NE,       [n, value | n.setXlp(value)       ])
		
		node
	}
	
	/*
	********************************************************************************************************************************
	*  dynamic dispatch methods 
	********************************************************************************************************************************
	*/		
	private def dispatch void transformStmt(Stmt it){
		throw new IllegalArgumentException("DotImport cannot transform statement: " + class)
	}
	
	private def dispatch void transformStmt(AttrStmt it) {
		switch type {
		case GRAPH: {
			// global graph attributes
			attrLists.forEach[
				attributes.forEach[
					globalGraphAttributes.put(name, value.escaped)
				]
			]
		}
		case NODE: {
			// global node attributes
			attrLists.forEach[
				attributes.forEach[
					globalNodeAttributes.put(name, value.escaped)
				]
			]
		}
		case EDGE: {
			// global edge attributes
			attrLists.forEach[
				attributes.forEach[
					globalEdgeAttributes.put(name, value.escaped)
				]
			]
		}
		}
	}
	
	private def dispatch void transformStmt(NodeStmt it){		
		node.transformNodeId(attrLists)
	}	
	
	private def dispatch void transformStmt(EdgeStmtNode it) {
		var sourceNode = node.transformNodeId
		for(EdgeRhs edgeRhs : edgeRHS){
			switch edgeRhs {
				EdgeRhsNode: {  
					val targetNode = edgeRhs.node.transformNodeId
					createEdge(sourceNode, edgeRhs.op.literal, targetNode, attrLists)
					// current target node may be source for next EdgeRHS
					sourceNode = targetNode
				}
				EdgeRhsSubgraph: {
					// TODO: add support for transforming edges with
					// subgraphs on their right hand side
				}
				default:{
					throw new IllegalArgumentException("DotImport cannot transform EdgeRhs: " + class)
				}
			}			
		}
	}
	
	private def create new Node.Builder().buildNode() createNode(String nodeName) {
		_setName(nodeName)
		graphBuilder.nodes(it)
	}
	
	def private void createEdge(Node sourceNode, String edgeOp, Node targetNode, List<AttrList> attrLists) {
		val edge = new Edge.Builder(sourceNode, targetNode)
				.attr(_NAME__GNE, sourceNode._getName + edgeOp + targetNode._getName)
				.buildEdge()
		
		val setter = [
			String attributeName, (Edge, String)=>void f | 
			val attributeValue = attrLists.getAttributeValue(attributeName)
			if (attributeValue != null) {
				f.apply(edge, attributeValue)
			} else if (globalEdgeAttributes.containsKey(attributeName)) {
				f.apply(edge, globalEdgeAttributes.get(attributeName))
			}
		]
		
		setter.apply(ARROWHEAD__E, [e, value | e.setArrowHead(value)])
		setter.apply(ARROWSIZE__E, [e, value | e.setArrowSize(value)])
		setter.apply(ARROWTAIL__E, [e, value | e.setArrowTail(value)])
		setter.apply(DIR__E,       [e, value | e.setDir(value)      ])
		setter.apply(HEAD_LP__E,   [e, value | e.setHeadLp(value)   ])
		setter.apply(HEADLABEL__E, [e, value | e.setHeadLabel(value)])
		setter.apply(ID__GNE,      [e, value | e.setId(value)       ])
		setter.apply(LABEL__GNE,   [e, value | e.setLabel(value)    ])
		setter.apply(LP__GE,       [e, value | e.setLp(value)       ])
		setter.apply(POS__NE,      [e, value | e.setPos(value)      ])
		setter.apply(STYLE__GNE,   [e, value | e.setStyle(value)    ])
		setter.apply(TAILLABEL__E, [e, value | e.setTailLabel(value)])
		setter.apply(TAIL_LP__E,   [e, value | e.setTailLp(value)   ])
		setter.apply(XLABEL__NE,   [e, value | e.setXLabel(value)   ])
		setter.apply(XLP__NE,      [e, value | e.setXlp(value)      ])

		graphBuilder.edges(edge)
	}
	
	def private String getAttributeValue(DotGraph graph, String name) {
		for (stmt : graph.stmts) {
			var String value =
				switch stmt {
					AttrStmt:  stmt.getAttributeValue(name)
					Attribute: stmt.getAttributeValue(name)
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
	def private String getAttributeValue(List<AttrList> attrLists, String name) {
		for (AttrList attrList : attrLists) {
			val value = attrList.getAttributeValue(name)
			if (value != null) {
				return value
			}
		}
		null
	}
	
	def private String getAttributeValue(AttrStmt attrStmt, String name) {
		attrStmt.attrLists.getAttributeValue(name)
	}

	def private String getAttributeValue(AttrList attrList, String name) {
		attrList.attributes.findFirst[it.name==name]?.value.escaped
	}
	
	def private String getAttributeValue(Attribute attribute, String name) {
		if (attribute.name.equals(name)) {
			return attribute.value.escaped
		}
		null
	}
	
	private def escaped(String it) {
		DotTerminalConverters.unquote(it)
	}
}

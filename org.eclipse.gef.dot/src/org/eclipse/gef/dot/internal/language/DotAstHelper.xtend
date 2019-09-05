/*******************************************************************************
 * Copyright (c) 2017, 2019 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *     Zoey Gerrit Prigge         - Generalized dependent attribute method 
 *                                  to use with recordBased Node shapes (bug #454629)
 *                                - include getAllAttributesSameName/Value (bug #548911)
 * 
 *******************************************************************************/
package org.eclipse.gef.dot.internal.language

import java.util.List
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EObject
import org.eclipse.gef.dot.internal.DotAttributes
import org.eclipse.gef.dot.internal.language.dot.AttrList
import org.eclipse.gef.dot.internal.language.dot.AttrStmt
import org.eclipse.gef.dot.internal.language.dot.Attribute
import org.eclipse.gef.dot.internal.language.dot.AttributeType
import org.eclipse.gef.dot.internal.language.dot.DotGraph
import org.eclipse.gef.dot.internal.language.dot.EdgeRhsNode
import org.eclipse.gef.dot.internal.language.dot.EdgeStmtNode
import org.eclipse.gef.dot.internal.language.dot.NodeId
import org.eclipse.gef.dot.internal.language.dot.NodeStmt
import org.eclipse.gef.dot.internal.language.dot.Stmt
import org.eclipse.gef.dot.internal.language.dot.Subgraph
import org.eclipse.gef.dot.internal.language.terminals.ID

import static extension org.eclipse.xtext.EcoreUtil2.*
import org.eclipse.xtext.EcoreUtil2

/**
 * This class provides helper methods for walking the DOT abstract syntax tree.
 */
class DotAstHelper {

	def static NodeId getNodeId(NodeId nodeId) {
		val dotGraph = nodeId.getContainerOfType(DotGraph)

		for (nodeStmt : dotGraph.allNodeStatements) {
			if (nodeStmt.node !== null && nodeStmt.node.name == nodeId.name) {
				return nodeStmt.node
			}
		}

		null
	}

	/*
	 * Collects all nodeId EObjects having the same name as the baseNodeId
	 */
	def static List<NodeId> getAllNodeIds(NodeId baseNodeId) {
		val result = newLinkedList
		val dotGraph = baseNodeId.getContainerOfType(DotGraph)

		// consider nodes
		for (nodeStmt : dotGraph.stmts.filter(NodeStmt)) {
			val nodeId = nodeStmt.node
			if (nodeId !== null && nodeId.name == baseNodeId.name && nodeId != baseNodeId) {
				result += nodeId
			}
		}

		// consider edges
		for (edgeStmtNode : dotGraph.stmts.filter(EdgeStmtNode)) {

			// consider the left side of the edges
			var nodeId = edgeStmtNode.node
			if (nodeId !== null && nodeId.name == baseNodeId.name && nodeId !== baseNodeId) {
				result += nodeId
			}

			// consider the right side of the edges
			val edgeRHS = edgeStmtNode.edgeRHS.head
			if (edgeRHS instanceof EdgeRhsNode) {
				nodeId = edgeRHS.node
				if (nodeId !== null && nodeId.name == baseNodeId.name && nodeId !== baseNodeId) {
					result += nodeId
				}
			}
		}

		result
	}

	def static getAllAttributesSameValue(Attribute comparator) {
		val result = newLinkedList
		val dotGraph = comparator.getContainerOfType(DotGraph)
		val comparatorParsed = DotAttributes.parsed(comparator)

		for (candidate : dotGraph.getAllContentsOfType(Attribute).filter [ e |
			val parsed = DotAttributes.parsed(e)
			if (parsed instanceof EObject && comparatorParsed instanceof EObject) {
				// Explicit cast necessary (presumably && not xtend supported for implicit cast)
				EcoreUtil2.equals(parsed as EObject, comparatorParsed as EObject)
			} else {
				parsed == comparatorParsed
			}
		]) {
			result += candidate
		}
		result
	}

	def static getAllAttributesSameName(Attribute comparator) {
		val result = newLinkedList
		val dotGraph = comparator.getContainerOfType(DotGraph)
		val comparatorName = comparator?.name?.toValue

		for (candidate : dotGraph.getAllContentsOfType(Attribute).filter[e|e?.name?.toValue == comparatorName]) {
			result += candidate
		}
		result
	}

	/**
	 * Returns the color scheme attribute value that is set for the given
	 * attribute.
	 * 
	 * @param attribute
	 *            The attribute to determine the color scheme attribute value
	 *            for.
	 * @return The color scheme value that is set for the given attribute, or
	 *         null if it cannot be determined.
	 */
	def static String getColorSchemeAttributeValue(Attribute attribute) {
		getDependedOnAttributeValue(attribute, DotAttributes.COLORSCHEME__GCNE)
	}

	/**
	 * 
	 * Returns an attribute value specified by attributeName that is set for given attribute
	 * 
	 * @param dependentAttribute
	 * 			The attribute to determine a depending value for.
	 * @param attributeName
	 * 			The name of the attribute that the dependentAttribute depends on.
	 * @return The attribute value set for the attribute specified by attributeName
	 */
	def static String getDependedOnAttributeValue(Attribute dependentAttribute, String attributeName) {
		// attribute nested below EdgeStmtNode
		val edgeStmtNode = dependentAttribute.getContainerOfType(EdgeStmtNode)
		if (edgeStmtNode !== null) {
			// look for a locally defined 'dependedOnValue' attribute
			var dependedOnValue = edgeStmtNode.attrLists.getAttributeValue(attributeName)
			if (dependedOnValue !== null) {
				return dependedOnValue.toValue
			}
			// look for a globally defined 'dependedOnValue' attribute
			dependedOnValue = edgeStmtNode.getGlobalDependedOnValue(AttributeType.EDGE, attributeName)
			if (dependedOnValue !== null) {
				return dependedOnValue.toValue
			}
		}

		// attribute nested below NodeStmt
		val nodeStmt = dependentAttribute.getContainerOfType(NodeStmt)
		if (nodeStmt !== null) {
			// look for a locally defined 'dependedOnValue' attribute
			var dependedOnValue = nodeStmt.attrLists.getAttributeValue(attributeName)
			if (dependedOnValue !== null) {
				return dependedOnValue.toValue
			}
			// look for a globally defined 'dependedOnValue' attribute
			dependedOnValue = nodeStmt.getGlobalDependedOnValue(AttributeType.NODE, attributeName)
			if (dependedOnValue !== null) {
				return dependedOnValue.toValue
			}
		}

		// attribute nested below AttrStmt
		val attrStmt = dependentAttribute.getContainerOfType(AttrStmt)
		if (attrStmt !== null) {
			val dependedOnValue = attrStmt.attrLists.getAttributeValue(attributeName)
			if (dependedOnValue !== null) {
				return dependedOnValue.toValue
			}
		}

		// attribute nested below Subgraph
		val subgraph = dependentAttribute.getContainerOfType(Subgraph)
		if (subgraph !== null) {
			val dependedOnValue = subgraph.getAttributeValue(attributeName)
			if (dependedOnValue !== null) {
				return dependedOnValue.toValue
			}
		}

		// attribute nested below Graph
		val dotGraph = dependentAttribute.getContainerOfType(DotGraph)
		if (dotGraph !== null) {
			var dependedOnValue = dotGraph.getAttributeValueAll(attributeName)
			if (dependedOnValue !== null) {
				return dependedOnValue.toValue
			}
			// look for a globally defined 'dependedOnValue' attribute
			dependedOnValue = dotGraph.getGlobalDependedOnValue(AttributeType.GRAPH, attributeName)
			if (dependedOnValue !== null) {
				return dependedOnValue.toValue
			}
		}

		null
	}

	private def static ID getGlobalDependedOnValue(EObject eObject, AttributeType attributeType, String attributeName) {
		// consider subgraph first
		val subgraph = eObject.getContainerOfType(Subgraph)
		if (subgraph !== null) {
			val value = subgraph.stmts.getAttributeValue(attributeType, attributeName)
			if (value !== null) {
				return value
			}
		}

		// consider graph second
		val dotGraph = eObject.getContainerOfType(DotGraph)
		if (dotGraph !== null) {
			val value = dotGraph.stmts.getAttributeValue(attributeType, attributeName)
			if (value !== null) {
				return value
			}
		}

		null
	}

	private def static ID getAttributeValue(EList<Stmt> stmts, AttributeType attributeType, String attributeName) {
		for (stmt : stmts) {
			if (stmt instanceof AttrStmt) {
				if (stmt.type == attributeType) {
					return stmt.attrLists.getAttributeValue(attributeName)
				}
			}
		}
		null
	}

	def static ID getAttributeValue(DotGraph graph, String name) {
		for (stmt : graph.stmts) {
			val value = switch stmt {
				// no need to consider AttrStmt here, because the global graph attributes are evaluated somewhere else
				Attribute:
					stmt.getAttributeValue(name)
			}
			if (value !== null) {
				return value
			}
		}
		null
	}

	def static ID getAttributeValueAll(DotGraph graph, String name) {
		for (stmt : graph.stmts) {
			val value = switch stmt {
				AttrStmt:
					stmt.attrLists.getAttributeValue(name)
				Attribute:
					stmt.getAttributeValue(name)
			}
			if (value !== null) {
				return value
			}
		}
		null
	}

	def static ID getAttributeValue(Subgraph subgraph, String name) {
		for (stmt : subgraph.stmts) {
			val value = switch stmt {
				// no need to consider AttrStmt here, because the global graph attributes are evaluated somewhere else
				Attribute:
					stmt.getAttributeValue(name)
			}
			if (value !== null) {
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
		for (attrList : attrLists) {
			val value = attrList.getAttributeValue(name)
			if (value !== null) {
				return value
			}
		}
		null
	}

	private def static ID getAttributeValue(AttrList attrList, String name) {
		attrList.attributes.findFirst[it.name.toValue == name]?.value
	}

	private def static ID getAttributeValue(Attribute attribute, String name) {
		if (attribute.name.toValue.equals(name)) {
			return attribute.value
		}
		null
	}

	/**
	 * Collects all node statements residing in the dot graph or in its subgraphs.
	 */
	private static def getAllNodeStatements(DotGraph dotGraph) {
		val nodeStamentsInDotGraph = dotGraph.stmts.filter(NodeStmt)

		val nodeStatementsInSubgraphs = newLinkedList
		dotGraph.stmts.filter(Subgraph).forEach [
			nodeStatementsInSubgraphs += allNodeStatementsInSubgraph
		]

		nodeStamentsInDotGraph + nodeStatementsInSubgraphs
	}

	private static def List<NodeStmt> getAllNodeStatementsInSubgraph(Subgraph subgraph) {
		val nodeStatementInSubgraph = subgraph.stmts.filter(NodeStmt).toList
		subgraph.stmts.filter(Subgraph).forEach [
			nodeStatementInSubgraph += allNodeStatementsInSubgraph
		]
		nodeStatementInSubgraph
	}
}

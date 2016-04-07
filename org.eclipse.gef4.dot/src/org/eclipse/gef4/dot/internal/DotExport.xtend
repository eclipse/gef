/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.dot.internal

import java.io.File
import org.eclipse.gef4.common.attributes.IAttributeStore
import org.eclipse.gef4.dot.internal.parser.dot.EdgeOp
import org.eclipse.gef4.graph.Edge
import org.eclipse.gef4.graph.Graph
import org.eclipse.gef4.graph.Node

import static org.eclipse.gef4.dot.internal.DotAttributes.*

/**
 * A serializer that creates a Graphviz DOT string or file from a {@link Graph} with {@link DotAttributes}.
 * 
 * @author anyssen
 * 
 */
class DotExport {

	// TODO: support a list of graphs
	def String exportDot(Graph graph) {
		// graph type is mandatory meta-attribute
		if (!graph.attributes.containsKey(_TYPE__G)) {
			throw new IllegalArgumentException(
				"The " + _TYPE__G + " attribute has to be set on the input graph " + graph + ".");
		}
		// node name is mandatory meta-attribute
		if (graph.nodes.exists[it.attributes.get(_NAME__GNE) == null]) {
			throw new IllegalArgumentException(
				"The " + _NAME__GNE + " attribute has to be set for all nodes of the input graph " + graph + ".");
		}
		print(graph)
	}

	// TODO: support a list of graphs
	def File exportDot(Graph graph, String pathname) {
		DotFileUtils.write(exportDot(graph), new File(pathname));
	}

	private def String print(Graph graph) '''
		«graph.attributes.get(_TYPE__G)» «IF graph.hasName»«graph.name» «ENDIF»{
			«IF graph.hasNonMetaAttributes»
				«graph.printNonMetaAttributes(";")»
			«ENDIF»
			«graph.nodes.sortBy[it.attributes.get(_NAME__GNE) as String].map[it.print].join("; ")»
			«FOR edge : graph.edges.sortBy[it.attributes.get(_NAME__GNE) as String]»
				«edge.name»«IF edge.hasNonMetaAttributes» [«edge.printNonMetaAttributes(",")»]«ENDIF»
			«ENDFOR»
		}
	'''

	private def isMetaAttribute(String key) {
		key.startsWith("_")
	}

	private def isDirected(Graph graph) {
		_TYPE__G__DIGRAPH.equals(graph.attributes.get(_TYPE__G))
	}

	private def print(Node node) {
		node.name + if(node.hasNonMetaAttributes) " [" + node.printNonMetaAttributes(",") + "]" else ""
	}

	private def hasName(IAttributeStore it) {
		it.attributes.get(_NAME__GNE) != null
	}

	private def dispatch String name(IAttributeStore store) {
		store.attributes.get(_NAME__GNE) as String
	}

	private def dispatch String name(Edge edge) {
		edge.source.name + (if(edge.graph.directed) EdgeOp.DIRECTED.literal else EdgeOp.UNDIRECTED.literal) +
			edge.target.name
	}

	private def hasNonMetaAttributes(IAttributeStore store) {
		// filter out properties that are prefixed with "_" as these do not match attributes
		store.attributes.keySet.exists[!isMetaAttribute]
	}

	private def printNonMetaAttributes(IAttributeStore store, String separator) {
		store.attributes.entrySet.filter[!key.isMetaAttribute].map[key + '="' + value + '"'].sort.join(separator + " ")
	}
}
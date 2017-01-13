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
package org.eclipse.gef.dot.internal

import java.io.File
import org.eclipse.gef.common.attributes.IAttributeStore
import org.eclipse.gef.dot.internal.language.dot.EdgeOp
import org.eclipse.gef.dot.internal.language.dot.GraphType
import org.eclipse.gef.dot.internal.language.terminals.ID
import org.eclipse.gef.graph.Edge
import org.eclipse.gef.graph.Graph
import org.eclipse.gef.graph.Node

import static extension org.eclipse.gef.dot.internal.DotAttributes.*

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
		if (graph.type == null ) {
			throw new IllegalArgumentException(
				"The " + _TYPE__G + " attribute has to be set on the input graph " + graph + ".");
		}
		// node name is mandatory meta-attribute
		if (graph.nodes.exists[!hasName]) {
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
		«graph.type» «IF graph.hasName»«graph.name» «ENDIF»{
			«IF graph.hasNonMetaAttributes»
				«graph.printNonMetaAttributes(";")»
			«ENDIF»
			«graph.nodes.sortBy[name].map[print].join("; ")»
			«FOR edge : graph.edges.sortBy[name]»
				«edge.name»«IF edge.hasNonMetaAttributes» [«edge.printNonMetaAttributes(",")»]«ENDIF»
			«ENDFOR»
		}
	'''

	private def isMetaAttribute(String key) {
		key.startsWith("_")
	}

	private def isDirected(Graph it) {
		GraphType.DIGRAPH.equals(type)
	}

	private def print(Node node) {
		node.name + if(node.hasNonMetaAttributes) " [" + node.printNonMetaAttributes(",") + "]" else ""
	}

	private def hasName(IAttributeStore it) {
		it.attributes.get(_NAME__GNE) != null
	}
	
	private def GraphType type(Graph it) {
		_getType
	}

	private def dispatch String name(IAttributeStore store) {
		(store.attributes.get(_NAME__GNE) as ID).toValue
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
		store.attributes.entrySet.filter[!key.isMetaAttribute].map[key + '=' + value.toString].sort.join(separator + " ")
	}
}
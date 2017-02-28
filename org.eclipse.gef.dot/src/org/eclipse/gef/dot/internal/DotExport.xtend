/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Tamas Miklossy  (itemis AG) - minor refactorings
 * 
 *******************************************************************************/
package org.eclipse.gef.dot.internal

import java.io.File
import java.util.List
import org.eclipse.gef.common.attributes.IAttributeStore
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

	def String exportDot(Graph graph) {
		newArrayList(graph).exportDot
	}

	def String exportDot(List<Graph> graphs) {
		val builder = new StringBuilder
		for (graph : graphs) {

			// graph type is mandatory meta-attribute
			if (graph.type === null) {
				throw new IllegalArgumentException(
					"The " + _TYPE__G + " attribute has to be set on the input graph " + graph + ".")
			}

			// node name is mandatory meta-attribute
			if (graph.nodes.filter[nestedGraph === null].exists[!hasName]) {
				throw new IllegalArgumentException(
					"The " + _NAME__GNE + " attribute has to be set for all nodes of the input graph " + graph + ".")
			}
			builder.append(graph.print)
		}
		builder.toString
	}

	def File exportDot(Graph graph, String pathname) {
		DotFileUtils.write(graph.exportDot, new File(pathname))
	}

	def File exportDot(List<Graph> graphs, String pathname) {
		DotFileUtils.write(graphs.exportDot, new File(pathname))
	}

	private def String print(Graph graph) '''
		«graph.type» «IF graph.hasName»«graph.name» «ENDIF»{
			«IF graph.hasNonMetaAttributes»
				«graph.printNonMetaAttributes(";")»
			«ENDIF»
			«graph.nodes.map[print].join("; ")»
			«FOR edge : graph.edges»
				«edge.name»«IF edge.hasNonMetaAttributes» [«edge.printNonMetaAttributes(",")»]«ENDIF»
			«ENDFOR»
		}
	'''

	private def isMetaAttribute(String key) {
		key.startsWith("_")
	}

	private def String print(Node node) {
		if (node.nestedGraph !== null) {
			'''
			subgraph «IF node.nestedGraph.hasName»«node.nestedGraph.name» «ENDIF»{
				«IF node.nestedGraph.hasNonMetaAttributes»
					«node.nestedGraph.printNonMetaAttributes(";")»
				«ENDIF»
				«node.nestedGraph.nodes.map[print].join("; ")»
				«FOR edge : node.nestedGraph.edges»
					«edge.name»«IF edge.hasNonMetaAttributes» [«edge.printNonMetaAttributes(",")»]«ENDIF»
				«ENDFOR»
			}'''
		} else {
			node.name + if(node.hasNonMetaAttributes) " [" + node.printNonMetaAttributes(",") + "]" else ""
		}
	}

	private def hasName(IAttributeStore it) {
		attributes.get(_NAME__GNE) !== null
	}

	private def GraphType type(Graph it) {
		_getType
	}

	private def dispatch String name(IAttributeStore store) {
		(store.attributes.get(_NAME__GNE) as ID).toValue
	}

	private def dispatch String name(Edge edge) {
		edge._getName
	}

	private def hasNonMetaAttributes(IAttributeStore store) {

		// filter out properties that are prefixed with "_" as these do not match attributes
		store.attributes.keySet.exists[!isMetaAttribute]
	}

	private def printNonMetaAttributes(IAttributeStore store, String separator) {
		store.attributes.entrySet.filter[!key.isMetaAttribute].map[key + '=' + value.toString].sort.join(separator + " ")
	}
}

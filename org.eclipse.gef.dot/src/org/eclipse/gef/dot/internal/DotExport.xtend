/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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

	private def String print(Graph it) '''
		«type» «IF hasName»«name» «ENDIF»{
			«IF hasNonMetaAttributes»
				«printNonMetaAttributes(";")»
			«ENDIF»
			«nodes.map[print].join("; ")»
			«FOR edge : edges»
				«edge.name»«IF edge.hasNonMetaAttributes» [«edge.printNonMetaAttributes(",")»]«ENDIF»
			«ENDFOR»
		}
	'''

	private def isMetaAttribute(String it) {
		startsWith("_")
	}

	private def String print(Node it) {
		if (nestedGraph !== null) {
			'''
			subgraph «IF nestedGraph.hasName»«nestedGraph.name» «ENDIF»{
				«IF nestedGraph.hasNonMetaAttributes»
					«nestedGraph.printNonMetaAttributes(";")»
				«ENDIF»
				«nestedGraph.nodes.map[print].join("; ")»
				«FOR edge : nestedGraph.edges»
					«edge.name»«IF edge.hasNonMetaAttributes» [«edge.printNonMetaAttributes(",")»]«ENDIF»
				«ENDFOR»
			}'''
		} else {
			name + if(hasNonMetaAttributes) " [" + printNonMetaAttributes(",") + "]" else ""
		}
	}

	private def hasName(IAttributeStore it) {
		attributes.get(_NAME__GNE) !== null
	}

	private def GraphType type(Graph it) {
		_getType
	}

	private def dispatch String name(IAttributeStore it) {
		(attributes.get(_NAME__GNE) as ID).toValue
	}

	private def dispatch String name(Edge it) {
		_getName
	}

	private def hasNonMetaAttributes(IAttributeStore it) {

		// filter out properties that are prefixed with "_" as these do not match attributes
		attributes.keySet.exists[!isMetaAttribute]
	}

	private def printNonMetaAttributes(IAttributeStore it, String separator) {
		attributes.entrySet.filter[!key.isMetaAttribute].map[key + '=' + value.toString].sort.join(separator + " ")
	}
}

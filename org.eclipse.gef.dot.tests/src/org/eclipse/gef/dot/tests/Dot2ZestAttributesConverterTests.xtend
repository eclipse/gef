/*******************************************************************************
 * Copyright (c) 2019 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef.dot.tests

import org.eclipse.gef.common.attributes.IAttributeStore
import org.eclipse.gef.dot.internal.ui.Dot2ZestAttributesConverter
import org.eclipse.gef.graph.Edge
import org.eclipse.gef.graph.Graph
import org.eclipse.gef.graph.Node
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

/*
 * Test cases for the {@link Dot2ZestAttributesConverter#copy(IAttributeStore, IAttributeStore)} method.
 */
class Dot2ZestAttributesConverterTests {

	extension Dot2ZestAttributesConverter converter
	@Rule public ExpectedException thrown = ExpectedException.none

	@Before def void setup() {
		converter = new Dot2ZestAttributesConverter => [
			options.emulateLayout = false // TODO remove once FX tests work
		]
	}

	@Test def copying_from_edge_to_edge() {
		edge.copy(edge)
	}

	@Test def copying_from_edge_to_graph() {
		thrown.expect(IllegalArgumentException)
		edge.copy(graph)
	}

	@Test def copying_from_edge_to_node() {
		thrown.expect(IllegalArgumentException)
		edge.copy(node)
	}

	@Test def copying_from_edge_to_invalid() {
		thrown.expect(IllegalArgumentException)
		edge.copy(invalid)
	}

	@Test def copying_from_graph_to_edge() {
		thrown.expect(IllegalArgumentException)
		graph.copy(edge)
	}

	@Test def copying_from_graph_to_graph() {
		graph.copy(graph)
	}

	@Test def copying_from_graph_to_node() {
		thrown.expect(IllegalArgumentException)
		graph.copy(node)
	}

	@Test def copying_from_graph_to_invalid() {
		thrown.expect(IllegalArgumentException)
		graph.copy(invalid)
	}

	@Test def copying_from_node_to_edge() {
		thrown.expect(IllegalArgumentException)
		node.copy(edge)
	}

	@Test def copying_from_node_to_graph() {
		thrown.expect(IllegalArgumentException)
		node.copy(graph)
	}

	@Test def copying_from_node_to_node() {
		node.copy(node)
	}

	@Test def copying_from_node_to_invalid() {
		thrown.expect(IllegalArgumentException)
		node.copy(invalid)
	}

	@Test def copying_from_invalid_to_edge() {
		thrown.expect(IllegalArgumentException)
		invalid.copy(edge)
	}

	@Test def copying_from_invalid_to_graph() {
		thrown.expect(IllegalArgumentException)
		invalid.copy(graph)
	}

	@Test def copying_from_invalid_to_node() {
		thrown.expect(IllegalArgumentException)
		invalid.copy(node)
	}

	@Test def copying_from_invalid_to_invalid() {
		thrown.expect(IllegalArgumentException)
		invalid.copy(invalid)
	}

	private def edge() {
		val n1 = node
		val n2 = node
		val e = new Edge(n1, n2)
		val g = new Graph.Builder
		g.nodes(n1, n2).edges(e).build
		e
	}

	private def graph() {
		new Graph
	}

	private def node() {
		new Node
	}

	private def invalid() {
		new IAttributeStore() {
			override attributesProperty() {}
			override getAttributes() {}
		}
	}
}

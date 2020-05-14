/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.graph.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.eclipse.gef.common.attributes.IAttributeCopier;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.GraphCopier;
import org.eclipse.gef.graph.Node;
import org.junit.Test;

public class GraphCopierTests {

	private static final String ID = "id";

	@Test
	public void buildNestedWithKeys() {
		// build source graph containing a nested graph in the second node
		Node n = new Node();
		Node m = new Node();
		Edge nm = new Edge(n, m);
		Node ma = new Node();
		Node mb = new Node();
		Edge mab = new Edge(ma, mb);
		Graph mg = new Graph(Arrays.asList(ma, mb), Arrays.asList(mab));
		m.setNestedGraph(mg);
		Graph sourceGraph = new Graph(Arrays.asList(n, m), Arrays.asList(nm));

		// copy source graph
		GraphCopier copier = new GraphCopier(IAttributeCopier.NULL_COPY);
		Graph copy = copier.copy(sourceGraph);

		// check number of nodes and edges
		assertEquals(2, copy.getNodes().size());
		assertEquals(1, copy.getEdges().size());

		// check source and target of first edge
		assertEquals(copy.getNodes().get(0),
				copy.getEdges().get(0).getSource());
		assertEquals(copy.getNodes().get(1),
				copy.getEdges().get(0).getTarget());
	}

	private Graph genGraph(int size) {
		Graph.Builder gb = new Graph.Builder();
		Node prev = null;
		for (int id = 0; id < size; id++) {
			Node next = gb.node().attr(ID, Integer.toString(id)).buildNode();
			if (prev != null) {
				gb.edge(prev, next).attr(ID, Integer.toString(id - 1) + " -> "
						+ Integer.toString(id));
			}
			prev = next;
		}
		return gb.build();
	}

	@Test
	public void test_edge_map_shallow_copy() {
		Graph g = genGraph(10);
		GraphCopier copier = new GraphCopier(IAttributeCopier.SHALLOW_COPY);
		Graph copy = copier.copy(g);
		for (Edge inputEdge : g.getEdges()) {
			assertTrue(copier.getInputToOutputEdgeMap().containsKey(inputEdge));
			Edge outputEdge = copier.getInputToOutputEdgeMap().get(inputEdge);
			assertNotSame(outputEdge, inputEdge);
			assertTrue(copy.getEdges().contains(outputEdge));
			assertEquals(outputEdge.getAttributes().get(ID),
					inputEdge.getAttributes().get(ID));
			assertSame(outputEdge.getAttributes().get(ID),
					inputEdge.getAttributes().get(ID));
		}
	}

	@Test
	public void test_node_map_shallow_copy() {
		Graph g = genGraph(10);
		GraphCopier copier = new GraphCopier(IAttributeCopier.SHALLOW_COPY);
		Graph copy = copier.copy(g);
		for (Node inputNode : g.getNodes()) {
			assertTrue(copier.getInputToOutputNodeMap().containsKey(inputNode));
			Node outputNode = copier.getInputToOutputNodeMap().get(inputNode);
			assertNotSame(outputNode, inputNode);
			assertTrue(copy.getNodes().contains(outputNode));
			assertEquals(outputNode.getAttributes().get(ID),
					inputNode.getAttributes().get(ID));
			assertSame(outputNode.getAttributes().get(ID),
					inputNode.getAttributes().get(ID));
		}
	}

}

/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

import org.eclipse.gef.common.attributes.IAttributeCopier;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.GraphCopier;
import org.eclipse.gef.graph.Node;
import org.junit.Test;

public class GraphCopierTests {

	private static final String ID = "id";

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

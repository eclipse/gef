/*******************************************************************************
 * Copyright (c) 2014, 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.IConnectionLayout;
import org.eclipse.gef4.layout.IEntityLayout;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.zest.fx.ZestProperties;
import org.eclipse.gef4.zest.fx.layout.GraphLayoutContext;
import org.junit.Test;

public class GraphLayoutContextTests {

	private static int ID;

	/*
	 * The following constants and methods are for easily building graph
	 * structures.
	 */

	public static Map<String, Object> ATTR_EMPTY = new HashMap<String, Object>();

	public static Edge e(Node n, Node m) {
		return new Edge.Builder(n, m).buildEdge();
	}

	public static List<Edge> edges(List<Node> nodes, int... indices) {
		List<Edge> edges = new ArrayList<Edge>();
		for (int i = 0; i < indices.length; i += 2) {
			edges.add(e(nodes.get(indices[i]), nodes.get(indices[i + 1])));
		}
		return edges;
	}

	public static Node n(String label) {
		return new Node.Builder().attr(ZestProperties.ELEMENT_LABEL, label).attr(ZestProperties.ELEMENT_CSS_ID, ID++)
				.buildNode();
	}

	public static List<Node> nodes(String... labels) {
		Node[] nodes = new Node[labels.length];
		for (int i = 0; i < labels.length; i++) {
			nodes[i] = n(labels[i]);
		}
		return Arrays.asList(nodes);
	}

	/*
	 * The test_n_m() functions test graphs with n nodes and m edges
	 */

	/**
	 * <p>
	 * Checks if the source and target nodes of the <i>x</i>th edge of the given
	 * <i>edges</i> list are correctly returned as the items of the
	 * corresponding layout nodes.
	 * </p>
	 * <p>
	 * Does also check if the connection layout of that edge is correctly
	 * returned as the only connection layout from its source to its target.
	 * </p>
	 *
	 * @param edges
	 *            graph edges
	 * @param glc
	 *            layout context
	 * @param x
	 *            edge index
	 * @see GraphLayoutContext#getConnections()
	 * @see GraphLayoutContext#getConnections(IEntityLayout, IEntityLayout)
	 * @see IConnectionLayout#getSource()
	 * @see IConnectionLayout#getTarget()
	 */
	private void checkEdgeIdentity(List<Edge> edges, GraphLayoutContext glc, int x) {
		IConnectionLayout layout = glc.getConnections()[x];
		INodeLayout source = layout.getSource();
		INodeLayout target = layout.getTarget();
		assertSame(edges.get(x).getSource(), source.getItems()[0]);
		assertSame(edges.get(x).getTarget(), target.getItems()[0]);

		IConnectionLayout[] connections = glc.getConnections(source, target);
		assertEquals(1, connections.length);
		assertSame(layout, connections[0]);
	}

	/**
	 * Checks if the layout context constructed from a graph corresponds to that
	 * graph in its structure.
	 *
	 * <ol>
	 * <li>Constructs a graph from the given nodes and edges.</li>
	 * <li>Constructs a layout context for that graph.</li>
	 * <li>{@link #checkSizes(List, List, GraphLayoutContext) Checks} the number
	 * of transfered objects.</li>
	 * <li>{@link #checkNodesAndEdges(List, List, GraphLayoutContext) Checks}
	 * identities and order of transfered nodes and edges.</li>
	 * </ol>
	 *
	 * @param nodes
	 *            graph nodes
	 * @param edges
	 *            graph edges
	 */
	private void checkIntegrity(List<Node> nodes, List<Edge> edges) {
		Graph graph = new Graph(ATTR_EMPTY, nodes, edges);
		GraphLayoutContext glc = new GraphLayoutContext(graph);
		checkSizes(nodes, edges, glc);
		checkNodesAndEdges(nodes, edges, glc);
	}

	/**
	 * Checks if the <i>x</i>th node of the given <i>nodes</i> list is correctly
	 * returned as the only item from the corresponding layout node.
	 *
	 * @param nodes
	 *            graph nodes
	 * @param glc
	 *            layout context
	 * @param x
	 *            node index
	 */
	private void checkNodeIdentity(List<Node> nodes, GraphLayoutContext glc, int x) {
		Object[] items = glc.getNodes()[x].getItems();
		assertEquals(1, items.length);
		assertSame(nodes.get(x), items[0]);
	}

	/**
	 * Checks the identity and order of transfered layout nodes and edges.
	 *
	 * @param nodes
	 *            graph nodes
	 * @param edges
	 *            graph edges
	 * @param glc
	 *            layout context to check
	 */
	private void checkNodesAndEdges(List<Node> nodes, List<Edge> edges, GraphLayoutContext glc) {
		for (int i = 0; i < nodes.size(); i++) {
			checkNodeIdentity(nodes, glc, i);
		}
		for (int i = 0; i < edges.size(); i++) {
			checkEdgeIdentity(edges, glc, i);
		}
	}

	/**
	 * Checks if the number of returned layout objects (nodes, edges, entities)
	 * corresponds with the sizes of the given graph nodes and edges.
	 *
	 * @param nodes
	 *            graph nodes
	 * @param edges
	 *            graph edges
	 * @param glc
	 *            layout context
	 */
	private void checkSizes(List<Node> nodes, List<Edge> edges, GraphLayoutContext glc) {
		assertEquals(nodes.size(), glc.getNodes().length);
		assertEquals(edges.size(), glc.getConnections().length);
		assertEquals(nodes.size(), glc.getEntities().length);
	}

	@Test
	public void test_1_0() {
		List<Node> nodes = nodes("1");
		List<Edge> edges = edges(nodes);
		checkIntegrity(nodes, edges);
	}

	@Test
	public void test_2_1() {
		List<Node> nodes = nodes("1", "2");
		List<Edge> edges = edges(nodes, 0, 1);
		checkIntegrity(nodes, edges);
	}

	@Test
	public void test_4_4() {
		List<Node> nodes = nodes("1", "2", "3", "4");
		List<Edge> edges = edges(nodes, 0, 1, 1, 2, 2, 3, 0, 3);
		checkIntegrity(nodes, edges);
	}

	@Test
	public void test_empty() {
		List<Node> nodes = nodes();
		List<Edge> edges = edges(nodes);
		checkIntegrity(nodes, edges);
	}

}

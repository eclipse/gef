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
package org.eclipse.gef4.zest.fx.layout;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.AbstractLayoutContext;
import org.eclipse.gef4.layout.IConnectionLayout;
import org.eclipse.gef4.layout.IEntityLayout;
import org.eclipse.gef4.layout.ILayoutContext;
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.ISubgraphLayout;

/**
 * The {@link GraphLayoutContext} is a {@link Graph}-specific
 * {@link ILayoutContext} implementation. It adapts GEF4 Graph to GEF4 Layout.
 *
 * @author mwienand
 *
 */
public class GraphLayoutContext extends AbstractLayoutContext {

	private Graph g;
	private final Map<Node, GraphNodeLayout> nodeMap = new IdentityHashMap<Node, GraphNodeLayout>();
	private final Map<Edge, GraphEdgeLayout> edgeMap = new IdentityHashMap<Edge, GraphEdgeLayout>();

	// TODO: subgraphs

	/**
	 * Constructs a new {@link GraphLayoutContext} without nodes and edges.
	 */
	public GraphLayoutContext() {
		setGraph(null);
	}

	/**
	 * Constructs a new {@link GraphLayoutContext} from the given {@link Graph}.
	 * The {@link Node}s and {@link Edge}s of the {@link Graph} are transfered
	 * into {@link GraphNodeLayout}s and {@link GraphEdgeLayout}s.
	 *
	 * @param graph
	 *            The {@link Graph} that is transfered.
	 */
	public GraphLayoutContext(Graph graph) {
		setGraph(graph);
	}

	@Override
	public ISubgraphLayout createSubgraph(INodeLayout[] nodes) {
		// TODO: subgraphs
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	public IConnectionLayout[] getConnections() {
		List<IConnectionLayout> connections = new ArrayList<IConnectionLayout>();
		IConnectionLayout[] all = super.getConnections();
		// filter out any hidden nodes
		for (IConnectionLayout c : all) {
			if (isLayoutIrrelevant(c) || isLayoutIrrelevant(c.getSource()) || isLayoutIrrelevant(c.getTarget())) {
				continue;
			}
			connections.add(c);
		}
		return connections.toArray(new IConnectionLayout[] {});
	}

	/**
	 * Returns the {@link GraphEdgeLayout} corresponding to the given
	 * {@link Edge}.
	 *
	 * @param edge
	 *            The {@link Edge} for which to return the corresponding
	 *            {@link GraphEdgeLayout}.
	 * @return The {@link GraphEdgeLayout} corresponding to the given
	 *         {@link Edge}.
	 */
	public GraphEdgeLayout getEdgeLayout(Edge edge) {
		return edgeMap.get(edge);
	}

	@Override
	public IEntityLayout[] getEntities() {
		return getNodes();
	}

	/**
	 * Returns the transfered {@link Graph}.
	 *
	 * @return The transfered {@link Graph}.
	 */
	public Graph getGraph() {
		return g;
	}

	/**
	 * Returns the {@link GraphNodeLayout} corresponding to the given
	 * {@link Node}.
	 *
	 * @param node
	 *            The {@link Node} for which to return the corrsponding
	 *            {@link GraphNodeLayout}.
	 * @return The {@link GraphNodeLayout} corresponding to the given
	 *         {@link Node}.
	 */
	public GraphNodeLayout getNodeLayout(Node node) {
		return nodeMap.get(node);
	}

	@Override
	public INodeLayout[] getNodes() {
		List<INodeLayout> nodes = new ArrayList<INodeLayout>();
		INodeLayout[] allNodes = super.getNodes();
		// filter out any hidden nodes
		for (INodeLayout n : allNodes) {
			if (isLayoutIrrelevant(n)) {
				continue;
			}
			nodes.add(n);
		}
		return nodes.toArray(new INodeLayout[] {});
	}

	/**
	 * Transfers the given {@link Graph} into this {@link GraphLayoutContext},
	 * i.e. creates {@link GraphNodeLayout}s and {@link GraphEdgeLayout}s for
	 * the {@link Node}s and {@link Edge}s of the given {@link Graph}.
	 *
	 * @param graph
	 *            The {@link Graph} that is transfered into this
	 *            {@link GraphLayoutContext}.
	 */
	public void setGraph(Graph graph) {
		if (graph == null) {
			graph = new Graph();
		}
		this.g = graph;
		transferNodes();
		transferEdges();
	}

	private void transferEdges() {
		clearEdges();
		edgeMap.clear();
		for (Edge edge : g.getEdges()) {
			GraphEdgeLayout graphConnection = new GraphEdgeLayout(this, edge);
			addEdge(graphConnection);
			edgeMap.put(edge, graphConnection);
		}
	}

	private void transferNodes() {
		clearNodes();
		nodeMap.clear();
		for (Node node : g.getNodes()) {
			GraphNodeLayout graphNode = new GraphNodeLayout(this, node);
			addNode(graphNode);
			nodeMap.put(node, graphNode);
		}
	}

}

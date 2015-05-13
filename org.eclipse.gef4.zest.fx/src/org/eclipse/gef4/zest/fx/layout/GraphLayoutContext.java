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
import org.eclipse.gef4.layout.INodeLayout;
import org.eclipse.gef4.layout.ISubgraphLayout;

/**
 * Transformation from GEF4 Graph with ZestProperties to GEF4 Layout.
 *
 * @author mwienand
 *
 */
public class GraphLayoutContext extends AbstractLayoutContext {

	private Graph g;
	private final Map<Node, GraphNodeLayout> nodeMap = new IdentityHashMap<Node, GraphNodeLayout>();
	private final Map<Edge, GraphEdgeLayout> edgeMap = new IdentityHashMap<Edge, GraphEdgeLayout>();

	// TODO: subgraphs

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
			if (isLayoutIrrelevant(c) || isLayoutIrrelevant(c.getSource())
					|| isLayoutIrrelevant(c.getTarget())) {
				continue;
			}
			connections.add(c);
		}
		return connections.toArray(new IConnectionLayout[] {});
	}

	public GraphEdgeLayout getEdgeLayout(Edge edge) {
		return edgeMap.get(edge);
	}

	@Override
	public IEntityLayout[] getEntities() {
		return getNodes();
	}

	public Graph getGraph() {
		return g;
	}

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

	public void setGraph(Graph graph) {
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

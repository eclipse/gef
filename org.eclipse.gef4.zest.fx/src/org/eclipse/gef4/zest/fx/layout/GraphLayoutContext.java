/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.interfaces.EntityLayout;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.layout.interfaces.SubgraphLayout;

public class GraphLayoutContext extends AbstractLayoutContext {

	private Graph g;
	private final Map<Node, GraphNodeLayout> nodeMap = new HashMap<Node, GraphNodeLayout>();
	private final Map<Edge, GraphEdgeLayout> edgeMap = new HashMap<Edge, GraphEdgeLayout>();
	// TODO: subgraphs
	// TODO: We have to expose a hook for flushChanges() to be able to do
	// something when layouting finishes
	private final List<Runnable> onFlushChanges = new ArrayList<Runnable>();

	public GraphLayoutContext(Graph graph) {
		setGraph(graph);
	}

	public void addOnFlushChanges(Runnable runnable) {
		if (runnable == null) {
			throw new IllegalArgumentException("Runnable may not be null.");
		}
		onFlushChanges.add(runnable);
	}

	@Override
	public SubgraphLayout createSubgraph(NodeLayout[] nodes) {
		// TODO: subgraphs
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	protected void doFlushChanges(boolean animationHint) {
		// TODO: use specific flush-changes-listener to pass animationHint along
		for (Runnable r : onFlushChanges) {
			r.run();
		}
	}

	public GraphEdgeLayout getEdgeLayout(Edge edge) {
		return edgeMap.get(edge);
	}

	@Override
	public EntityLayout[] getEntities() {
		// TODO: subgraphs
		return getNodes();
	}

	public Graph getGraph() {
		return g;
	}

	public GraphNodeLayout getNodeLayout(Node node) {
		return nodeMap.get(node);
	}

	public void removeOnFlushChanges(Runnable runnable) {
		if (!onFlushChanges.contains(runnable)) {
			throw new IllegalArgumentException(
					"Given Runnable is not contained in the list.");
		}
		onFlushChanges.remove(runnable);
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

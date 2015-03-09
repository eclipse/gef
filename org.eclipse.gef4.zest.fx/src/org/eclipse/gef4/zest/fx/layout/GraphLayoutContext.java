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
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.EntityLayout;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.layout.interfaces.SubgraphLayout;

/**
 * Transformation from GEF4 Graph with ZestProperties to GEF4 Layout.
 *
 * @author wienand
 *
 */
public class GraphLayoutContext extends AbstractLayoutContext {

	private Graph g;
	private final Map<Node, GraphNodeLayout> nodeMap = new IdentityHashMap<Node, GraphNodeLayout>();
	private final Map<Edge, GraphEdgeLayout> edgeMap = new IdentityHashMap<Edge, GraphEdgeLayout>();
	// TODO: subgraphs
	private final List<Runnable> onFlushChanges = new ArrayList<Runnable>();
	private final List<ILayoutFilter> layoutFilters = new ArrayList<ILayoutFilter>();

	public GraphLayoutContext(Graph graph) {
		setGraph(graph);
	}

	public void addLayoutFilter(ILayoutFilter layoutFilter) {
		layoutFilters.add(layoutFilter);
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

	public void firePruningChanged(GraphNodeLayout node) {
		pcs.firePropertyChange("hidden", 0, 1);
	}

	@Override
	public ConnectionLayout[] getConnections() {
		List<ConnectionLayout> connections = new ArrayList<ConnectionLayout>();
		ConnectionLayout[] all = super.getConnections();
		// filter out any hidden nodes
		for (ConnectionLayout c : all) {
			if (isLayoutIrrelevant(c) || isLayoutIrrelevant(c.getSource())
					|| isLayoutIrrelevant(c.getTarget())) {
				continue;
			}
			connections.add(c);
		}
		return connections.toArray(new ConnectionLayout[] {});
	}

	public GraphEdgeLayout getEdgeLayout(Edge edge) {
		return edgeMap.get(edge);
	}

	@Override
	public EntityLayout[] getEntities() {
		return getNodes();
	}

	public Graph getGraph() {
		return g;
	}

	public List<ILayoutFilter> getLayoutFilters() {
		return Collections.unmodifiableList(layoutFilters);
	}

	public GraphNodeLayout getNodeLayout(Node node) {
		return nodeMap.get(node);
	}

	@Override
	public NodeLayout[] getNodes() {
		List<NodeLayout> nodes = new ArrayList<NodeLayout>();
		NodeLayout[] allNodes = super.getNodes();
		// filter out any hidden nodes
		for (NodeLayout n : allNodes) {
			if (isLayoutIrrelevant(n)) {
				continue;
			}
			nodes.add(n);
		}
		return nodes.toArray(new NodeLayout[] {});
	}

	private boolean isLayoutIrrelevant(ConnectionLayout c) {
		return false;
	}

	public boolean isLayoutIrrelevant(NodeLayout nodeLayout) {
		for (ILayoutFilter filter : layoutFilters) {
			if (filter.isLayoutIrrelevant(nodeLayout)) {
				return true;
			}
		}
		return false;
	}

	public void removeLayoutFilter(ILayoutFilter layoutFilter) {
		layoutFilters.remove(layoutFilter);
	}

	public void removeOnFlushChanges(Runnable runnable) {
		if (!onFlushChanges.contains(runnable)) {
			new IllegalArgumentException(
					"Given Runnable is not contained in the list.")
					.printStackTrace();
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

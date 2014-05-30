package org.eclipse.gef4.zest.layout;

import java.util.HashMap;
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
	private Runnable flushChanges;

	public GraphLayoutContext(Graph graph) {
		setGraph(graph);
	}

	@Override
	public SubgraphLayout createSubgraph(NodeLayout[] nodes) {
		// TODO: subgraphs
		throw new UnsupportedOperationException("not yet implemented");
	}

	@Override
	protected void doFlushChanges(boolean animationHint) {
		if (getFlushChanges() != null) {
			getFlushChanges().run();
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

	public Runnable getFlushChanges() {
		return flushChanges;
	}

	public Graph getGraph() {
		return g;
	}

	public GraphNodeLayout getNodeLayout(Node node) {
		return nodeMap.get(node);
	}

	public void setFlushChanges(Runnable flushChanges) {
		this.flushChanges = flushChanges;
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

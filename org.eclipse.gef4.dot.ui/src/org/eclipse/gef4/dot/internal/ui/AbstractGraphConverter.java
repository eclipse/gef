package org.eclipse.gef4.dot.internal.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;

public abstract class AbstractGraphConverter {

	private Map<Node, Node> dotToZestNodes = new HashMap<Node, Node>();

	public Graph convert(Graph dot) {
		Graph zest = new Graph();
		convertAttributes(dot, zest);
		// convert nodes and store dot to zest mapping, so that source and
		// destination of edges can be found easily later
		for (Node dotNode : dot.getNodes()) {
			Node zestNode = convertNode(dotNode);
			zestNode.setGraph(zest);
			dotToZestNodes.put(dotNode, zestNode);
			zest.getNodes().add(zestNode);
		}
		// convert edges
		for (Edge dotEdge : dot.getEdges()) {
			Edge edge = convertEdge(dotEdge);
			edge.setGraph(zest);
			zest.getEdges().add(edge);
		}
		dotToZestNodes.clear();
		return zest;
	}

	protected Edge convertEdge(Edge dotEdge) {
		// find nodes
		Node zestSource = dotToZestNodes.get(dotEdge.getSource());
		Node zestTarget = dotToZestNodes.get(dotEdge.getTarget());
		// create edge
		Edge zestEdge = new Edge(zestSource, zestTarget);
		convertAttributes(dotEdge, zestEdge);
		return zestEdge;
	}

	protected Node convertNode(Node dotNode) {
		Node node = new Node();
		convertAttributes(dotNode, node);
		// convert nested graph
		if (dotNode.getNestedGraph() != null) {
			Graph nested = convert(dotNode.getNestedGraph());
			node.setNestedGraph(nested);
		}
		return node;
	}

	protected abstract void convertAttributes(Graph dot, Graph zest);

	protected abstract void convertAttributes(Edge dot, Edge zest);

	protected abstract void convertAttributes(Node dot, Node zest);

}
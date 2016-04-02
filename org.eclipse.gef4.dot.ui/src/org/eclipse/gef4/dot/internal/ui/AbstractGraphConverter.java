package org.eclipse.gef4.dot.internal.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.zest.fx.ZestProperties;

public abstract class AbstractGraphConverter {

	public final static class Options {

		/**
		 * Indicates whether layout should be emulated or not. If set to
		 * <code>true</code>, an {@link ILayoutAlgorithm} is to be inferred for
		 * the given dot, and set as value of the
		 * {@link ZestProperties#GRAPH_LAYOUT_ALGORITHM} attribute. If set to
		 * <code>false</code> (i.e. native layout is performed via Graphviz and
		 * position information is already provided in the dot input), the
		 * {@link ZestProperties#GRAPH_LAYOUT_ALGORITHM} should remain unset.
		 */
		public boolean emulateLayout = true;

		// TOOD: control whether position information is to be transformed or
		// not; should not depend on emulateLayout
		// public boolean ignoreLayoutInfo = false;

		/**
		 * Specifies whether the y-coordinate values of all position information
		 * is to be inverted. If set to <code>true</code> the y-values of all
		 * position information is to be inverted. If set to <code>false</code>,
		 * it is to be transformed without inversion.
		 */
		public boolean invertYAxis = true;
	}

	private Map<Node, Node> inputToOutputNodes = new HashMap<Node, Node>();
	private Options options = new Options();

	public Options options() {
		return options;
	}

	public Graph convert(Graph inputGraph) {
		Graph outputGraph = new Graph();
		convertAttributes(inputGraph, outputGraph);
		// convert nodes and store dot to zest mapping, so that source and
		// destination of edges can be found easily later
		for (Node inputNode : inputGraph.getNodes()) {
			Node outputNode = convertNode(inputNode);
			if (outputNode != null) {
				inputToOutputNodes.put(inputNode, outputNode);
				outputNode.setGraph(outputGraph);
				outputGraph.getNodes().add(outputNode);
			}
		}
		// convert edges
		for (Edge inputEdge : inputGraph.getEdges()) {
			Edge outputEdge = convertEdge(inputEdge);
			if (outputEdge != null) {
				outputEdge.setGraph(outputGraph);
				outputGraph.getEdges().add(outputEdge);
			}
		}
		inputToOutputNodes.clear();
		return outputGraph;
	}

	protected Edge convertEdge(Edge inputEdge) {
		// find nodes
		Node outputSource = inputToOutputNodes.get(inputEdge.getSource());
		Node outputTarget = inputToOutputNodes.get(inputEdge.getTarget());
		// create edge
		Edge outputEdge = new Edge(outputSource, outputTarget);
		convertAttributes(inputEdge, outputEdge);
		return outputEdge;
	}

	protected Node convertNode(Node inputNode) {
		Node outputNode = new Node();
		convertAttributes(inputNode, outputNode);
		// convert nested graph
		if (inputNode.getNestedGraph() != null) {
			Graph nested = convert(inputNode.getNestedGraph());
			outputNode.setNestedGraph(nested);
		}
		return outputNode;
	}

	protected abstract void convertAttributes(Graph inputGraph,
			Graph outputGraph);

	protected abstract void convertAttributes(Edge inputEdge, Edge outputEdge);

	protected abstract void convertAttributes(Node inputNode, Node outputNode);

}
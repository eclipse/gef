/*******************************************************************************
 * Copyright (c) 2016, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - provide input to output maps (bug #497662)
 *
 *******************************************************************************/
package org.eclipse.gef.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.common.attributes.IAttributeCopier;
import org.eclipse.gef.common.attributes.IAttributeStore;

/**
 * A copier for {@link Graph graphs}.
 *
 * After a graph was copied, the copier can be queried for the mappings of input
 * nodes to output nodes and input edges to output edges.
 *
 * A copier is a stateful utility.
 *
 * @author anyssen
 *
 */
public class GraphCopier {

	private Map<Node, Node> inputToOutputNodes = new IdentityHashMap<>();
	private Map<Edge, Edge> inputToOutputEdges = new IdentityHashMap<>();
	private IAttributeCopier attributeCopier;

	/**
	 * Creates a new {@link GraphCopier} instance with the given
	 * {@link IAttributeCopier}.
	 *
	 * @param attributeCopier The {@link IAttributeCopier} used to copy the
	 *                        attributes of {@link Graph}, {@link Node}s, and
	 *                        {@link Edge}s.
	 */
	public GraphCopier(IAttributeCopier attributeCopier) {
		this.attributeCopier = attributeCopier;
	}

	/**
	 * Discard any data that was tracked in previous copy operations.
	 *
	 * @since 5.1
	 */
	protected void clearInputToOutputMaps() {
		inputToOutputNodes.clear();
		inputToOutputEdges.clear();
	}

	/**
	 * Creates a copy of the given {@link Graph}.
	 *
	 * @param graph The Graph to copy.
	 * @return A new graph that is the result of the copy operation.
	 */
	public Graph copy(Graph graph) {
		clearInputToOutputMaps();
		return copyGraph(graph);
	}

	/**
	 * Transfers attributes from the given input {@link Graph}, {@link Node}, or
	 * {@link Edge} to the given output {@link Graph}, {@link Node}, or
	 * {@link Edge}. The attributes may be copied or simply transferred. This lies
	 * within the responsibility of the {@link IAttributeCopier} that was passed in
	 * on construction of the {@link GraphCopier}.
	 *
	 * @param inputStore  The {@link Graph}, {@link Node}, or {@link Edge} from
	 *                    which to copy attributes.
	 * @param outputStore The {@link Graph}, {@link Node}, or {@link Edge} to copy
	 *                    attributes to.
	 */
	// TODO: This callback should be removed (inlined); we will need to refactor
	// the Dot2ZestGraphConverter first into a pure IAttributeCopier.
	protected void copyAttributes(IAttributeStore inputStore, IAttributeStore outputStore) {
		getAttributeCopier().copy(inputStore, outputStore);
	}

	/**
	 * Creates a copy of the given edge.
	 *
	 * @param edge The Edge to copy.
	 * @return A new {@link Edge} with transferred relations and (copied)
	 *         attributes.
	 */
	protected Edge copyEdge(Edge edge) {
		// find nodes
		Node outputSource = inputToOutputNodes.get(edge.getSource());
		Node outputTarget = inputToOutputNodes.get(edge.getTarget());
		// create edge
		Edge outputEdge = new Edge(outputSource, outputTarget);
		copyAttributes(edge, outputEdge);
		return outputEdge;
	}

	/**
	 * Copies all the edges of the given graph into the output graph.
	 *
	 * @param graph       The input {@link Graph} to copy.
	 * @param outputGraph The output Graph.
	 *
	 * @since 5.1
	 */
	protected void copyEdges(Graph graph, Graph outputGraph) {
		List<Edge> allEdges = new ArrayList<>(graph.getEdges().size());
		for (Edge inputEdge : graph.getEdges()) {
			Edge outputEdge = copyEdge(inputEdge);
			if (outputEdge != null) {
				trackCopiedEdge(inputEdge, outputEdge);
				allEdges.add(outputEdge);
			}
		}
		outputGraph.getEdges().addAll(allEdges);
	}

	/**
	 * Copies the given {@link Graph} using the current {@link IAttributeCopier}.
	 * Records the copied nodes in the {@link #getInputToOutputNodeMap()} and the
	 * copied edges in the {@link #getInputToOutputEdgeMap()}.
	 *
	 * @param graph The input {@link Graph} to copy.
	 * @return The copied result {@link Graph}.
	 */
	protected Graph copyGraph(Graph graph) {
		Graph outputGraph = new Graph();
		copyAttributes(graph, outputGraph);
		copyNodes(graph, outputGraph);
		copyEdges(graph, outputGraph);
		return outputGraph;
	}

	/**
	 * Creates a copy of the given node.
	 *
	 * @param node The {@link Node} to copy.
	 * @return A new Node with transferred relations and (copied) attributes.
	 */
	protected Node copyNode(Node node) {
		Node outputNode = new Node();
		copyAttributes(node, outputNode);
		// convert nested graph
		if (node.getNestedGraph() != null) {
			Graph nested = copyGraph(node.getNestedGraph());
			outputNode.setNestedGraph(nested);
		}
		return outputNode;
	}

	/**
	 * Copies all the nodes of the given graph into the output graph.
	 *
	 * @param graph       The input {@link Graph} to copy.
	 * @param outputGraph The output Graph.
	 * @since 5.1
	 */
	protected void copyNodes(Graph graph, Graph outputGraph) {
		// keeping track of copied nodes (so we can relocate them to
		// link edges)
		List<Node> allNodes = new ArrayList<>(graph.getNodes().size());
		for (Node inputNode : graph.getNodes()) {
			Node outputNode = copyNode(inputNode);
			if (outputNode != null) {
				trackCopiedNode(inputNode, outputNode);
				allNodes.add(outputNode);
			}
		}
		outputGraph.getNodes().addAll(allNodes);
	}

	/**
	 * Returns the {@link IAttributeCopier} used by this {@link GraphCopier}.
	 *
	 * @return The {@link IAttributeCopier} used by this {@link GraphCopier}.
	 */
	public IAttributeCopier getAttributeCopier() {
		return attributeCopier;
	}

	/**
	 * Returns an (unmodifiable) {@link Map} from input {@link Edge}s to output
	 * {@link Edge}s.
	 *
	 * @return An (unmodifiable) {@link Map} from input {@link Edge}s to output
	 *         {@link Edge}s.
	 */
	public Map<Edge, Edge> getInputToOutputEdgeMap() {
		return Collections.unmodifiableMap(inputToOutputEdges);
	}

	/**
	 * Returns an (unmodifiable) {@link Map} from input {@link Node}s to output
	 * {@link Node}s.
	 *
	 * @return An (unmodifiable) {@link Map} from input {@link Node}s to output
	 *         {@link Node}s.
	 */
	public Map<Node, Node> getInputToOutputNodeMap() {
		return Collections.unmodifiableMap(inputToOutputNodes);
	}

	/**
	 * Maintains a mapping from the input edge to the output edge.
	 *
	 * @param inputEdge  the input edge.
	 * @param outputEdge the output edge.
	 *
	 * @since 5.1
	 */
	protected void trackCopiedEdge(Edge inputEdge, Edge outputEdge) {
		inputToOutputEdges.put(inputEdge, outputEdge);
	}

	/**
	 * Maintains a mapping from the input node to the output node.
	 *
	 * @param inputNode  the input node.
	 * @param outputNode the output node.
	 *
	 * @since 5.1
	 */
	protected void trackCopiedNode(Node inputNode, Node outputNode) {
		inputToOutputNodes.put(inputNode, outputNode);
	}

}
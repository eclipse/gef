/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - provide input to output maps (bug #497662)
 *
 *******************************************************************************/
package org.eclipse.gef.graph;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.gef.common.attributes.IAttributeCopier;
import org.eclipse.gef.common.attributes.IAttributeStore;

/**
 * A copier for {@link Graph}s.
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
	 * @param attributeCopier
	 *            The {@link IAttributeCopier} used to copy the attributes of
	 *            {@link Graph}, {@link Node}s, and {@link Edge}s.
	 */
	public GraphCopier(IAttributeCopier attributeCopier) {
		this.attributeCopier = attributeCopier;
	}

	/**
	 * Creates a copy of the given {@link Graph}.
	 *
	 * @param graph
	 *            The Graph to copy.
	 * @return A new graph that is the result of the copy operation.
	 */
	public Graph copy(Graph graph) {
		// clear input to output maps
		inputToOutputNodes.clear();
		inputToOutputEdges.clear();
		// create new graph to hold the copy
		Graph outputGraph = new Graph();
		copyAttributes(graph, outputGraph);
		// copy nodes, keeping track of copied nodes (so we can relocate them to
		// link edges)
		for (Node inputNode : graph.getNodes()) {
			Node outputNode = copyNode(inputNode);
			if (outputNode != null) {
				inputToOutputNodes.put(inputNode, outputNode);
				outputNode.setGraph(outputGraph);
				outputGraph.getNodes().add(outputNode);
			}
		}
		// copy edges
		for (Edge inputEdge : graph.getEdges()) {
			Edge outputEdge = copyEdge(inputEdge);
			if (outputEdge != null) {
				inputToOutputEdges.put(inputEdge, outputEdge);
				outputEdge.setGraph(outputGraph);
				outputGraph.getEdges().add(outputEdge);
			}
		}
		return outputGraph;
	}

	/**
	 * Transfers attributes from the given input {@link Graph}, {@link Node}, or
	 * {@link Edge} to the given output {@link Graph}, {@link Node}, or
	 * {@link Edge}. The attributes may be copied or simply transferred. This
	 * lies within the responsibility of the {@link IAttributeCopier} that was
	 * passed in on construction of the {@link GraphCopier}.
	 *
	 * @param inputStore
	 *            The {@link Graph}, {@link Node}, or {@link Edge} from which to
	 *            copy attributes.
	 * @param outputStore
	 *            The {@link Graph}, {@link Node}, or {@link Edge} to copy
	 *            attributes to.
	 */
	// TODO: This callback should be removed (inlined); we will need to refactor
	// the Dot2ZestGraphConverter first into a pure IAttributeCopier.
	protected void copyAttributes(IAttributeStore inputStore, IAttributeStore outputStore) {
		attributeCopier.copy(inputStore, outputStore);
	}

	/**
	 * Creates a copy of the given edge.
	 *
	 * @param edge
	 *            The Edge to copy.
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
	 * Creates a copy of the given node.
	 *
	 * @param node
	 *            The {@link Node} to copy.
	 * @return A new Node with transferred relations and (copied) attributes.
	 */
	protected Node copyNode(Node node) {
		Node outputNode = new Node();
		copyAttributes(node, outputNode);
		// convert nested graph
		if (node.getNestedGraph() != null) {
			Graph nested = copy(node.getNestedGraph());
			outputNode.setNestedGraph(nested);
		}
		return outputNode;
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

}
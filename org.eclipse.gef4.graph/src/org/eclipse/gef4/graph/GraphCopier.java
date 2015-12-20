/*******************************************************************************
 * Copyright (c) 2013, 2015 Fabian Steeg and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg                - initial API and implementation (see #372365)
 *     Alexander Nyßen (itemis AG) - major refactorings
 *
 *******************************************************************************/
package org.eclipse.gef4.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Imports the content of a GEF4 graph generated from DOT into an existing GEF4
 * graph.
 *
 * @author Fabian Steeg (fsteeg)
 * @author anyssen
 */
final public class GraphCopier {

	private Graph sourceGraph;
	private String attributeNameForId = "ID";

	/**
	 * @param sourceGraph
	 *            The Zest source graph to import into another graph. Note that
	 *            this will only support a subset of the graph attributes, as it
	 *            is used for import of Zest graphs created from DOT input.
	 * @param attributeNameForId
	 *            The name of the attribute that stores an identification value
	 *            for nodes.
	 */
	public GraphCopier(Graph sourceGraph, String attributeNameForId) {
		this.sourceGraph = sourceGraph;
		this.attributeNameForId = attributeNameForId;
	}

	private Edge copy(Edge edge, Graph.Builder targetGraph, Map<Node, Node> copiedNodes, Map<Object, Node> ids) {
		// determine source and target
		Node srcSource = edge.getSource();
		Node source = find(ids, srcSource);
		if (source == null) {
			source = copiedNodes.get(srcSource);
		}

		Node srcTarget = edge.getTarget();
		Node target = find(ids, srcTarget);
		if (target == null) {
			target = copiedNodes.get(srcTarget);
		}

		// copy edge
		Edge.Builder copy = new Edge.Builder(source, target);

		// copy attributes
		for (Entry<String, Object> attr : edge.getAttributes().entrySet()) {
			copy.attr(attr.getKey(), attr.getValue());
		}

		// put into graph
		Edge build = copy.buildEdge();
		targetGraph.edges(build);
		return build;
	}

	private Node copy(Node node, Graph.Builder targetGraph) {
		Node.Builder copy = new Node.Builder();
		// copy attributes
		for (Entry<String, Object> attr : node.getAttributes().entrySet()) {
			copy.attr(attr.getKey(), attr.getValue());
		}
		Node copiedNode = copy.buildNode();
		targetGraph.nodes(copiedNode);
		return copiedNode;
	}

	private Node find(Map<Object, Node> ids, Node n) {
		Object id = n.getAttributes().get(attributeNameForId);
		if (id != null && !ids.containsKey(id)) {
			ids.put(id, n);
			return null;
		}
		return ids.get(id);
	}

	/**
	 * @param targetGraph
	 *            The graph to add content to
	 */
	public void into(Graph.Builder targetGraph) {
		// copy attributes
		for (Entry<String, Object> attr : sourceGraph.getAttributes().entrySet()) {
			targetGraph.attr(attr.getKey(), attr.getValue());
		}
		// find all existing node IDs in the target graph
		Graph targetGraphBuilt = targetGraph.build();
		List<Node> nodes = targetGraphBuilt.getNodes();
		Map<Object, Node> ids = new HashMap<>();
		for (Node n : nodes) {
			find(ids, n);
		}
		// copy non-existing nodes over
		Map<Node, Node> copiedNodes = new HashMap<>();
		for (Node node : sourceGraph.getNodes()) {
			if (find(ids, node) == null) {
				copiedNodes.put(node, copy(node, targetGraph));
			}
		}
		// copy edges over
		for (Edge edge : sourceGraph.getEdges()) {
			copy(edge, targetGraph, copiedNodes, ids);
		}
	}

}

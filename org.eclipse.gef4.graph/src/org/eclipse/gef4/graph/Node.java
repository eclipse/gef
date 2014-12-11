/*******************************************************************************
 * Copyright (c) 2013-2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 372365
 *******************************************************************************/
package org.eclipse.gef4.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef4.graph.Graph.Attr;

public final class Node {

	public static class Builder {

		private Map<String, Object> attrs = new HashMap<String, Object>();

		public Builder attr(Attr.Key attr, Object value) {
			return attr(attr.toString(), value);
		}

		public Node.Builder attr(String key, Object value) {
			attrs.put(key, value);
			return this;
		}

		public Node build() {
			return new Node(attrs);
		}

	}

	private final Map<String, Object> attrs;
	private Graph graph; // associated graph
	private Graph nestedGraph;

	public Node() {
		this(new HashMap<String, Object>());
	}

	public Node(Map<String, Object> attrs) {
		this.attrs = attrs;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (!(that instanceof Node)) {
			return false;
		}
		boolean attrsEqual = this.getAttrs().equals(((Node) that).getAttrs());
		return attrsEqual;
	}

	public Set<? extends Edge> getAllIncomingEdges() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Edge> incoming = new HashSet<Edge>();
		incoming.addAll(getIncomingLocalEdges());
		if (graph.getNestingNode() != null) {
			incoming.addAll(graph.getNestingNode().getAllIncomingEdges());
		}
		return incoming;
	}

	public Set<? extends Edge> getAllOutgoingEdges() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Edge> outgoing = new HashSet<Edge>();
		outgoing.addAll(getOutgoingLocalEdges());
		if (graph.getNestingNode() != null) {
			outgoing.addAll(graph.getNestingNode().getAllOutgoingEdges());
		}
		return outgoing;
	}

	public Set<? extends Node> getAllPredecessingNodes() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Node> predecessors = new HashSet<Node>();
		predecessors.addAll(getPredecessingLocalNodes());
		if (graph.getNestingNode() != null) {
			predecessors.addAll(graph.getNestingNode()
					.getAllPredecessingNodes());
		}
		return predecessors;
	}

	public Set<? extends Node> getAllSuccessingNodes() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Node> successors = new HashSet<Node>();
		successors.addAll(getSuccessingLocalNodes());
		if (graph.getNestingNode() != null) {
			successors.addAll(graph.getNestingNode().getAllSuccessingNodes());
		}
		return successors;
	}

	public Map<String, Object> getAttrs() {
		return attrs;
	}

	public Graph getGraph() {
		return graph;
	}

	public Set<? extends Edge> getIncomingLocalEdges() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Edge> incoming = new HashSet<Edge>();
		for (Edge e : graph.getEdges()) {
			if (e.getTarget() == this) {
				incoming.add(e);
			}
		}
		return incoming;
	}

	public Graph getNestedGraph() {
		return nestedGraph;
	}

	public Set<? extends Edge> getOutgoingLocalEdges() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Edge> outgoing = new HashSet<Edge>();
		for (Edge e : graph.getEdges()) {
			if (e.getSource() == this) {
				outgoing.add(e);
			}
		}
		return outgoing;
	}

	public Set<? extends Node> getPredecessingLocalNodes() {
		Set<Node> predecessors = new HashSet<Node>();
		for (Edge incoming : getIncomingLocalEdges()) {
			predecessors.add(incoming.getSource());
		}
		return predecessors;
	}

	public Set<? extends Node> getSuccessingLocalNodes() {
		Set<Node> successors = new HashSet<Node>();
		for (Edge outgoing : getOutgoingLocalEdges()) {
			successors.add(outgoing.getTarget());
		}
		return successors;
	}

	@Override
	public int hashCode() {
		return getAttrs().hashCode();
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public void setNestedGraph(Graph nestedGraph) {
		this.nestedGraph = nestedGraph;
	}

	@Override
	public String toString() {
		return String.format("Node {%s attrs}", attrs.size()); //$NON-NLS-1$
	}

}

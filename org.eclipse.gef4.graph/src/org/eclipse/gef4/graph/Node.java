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
	/**
	 * The {@link Graph} which this {@link Node} belongs to.
	 */
	private Graph graph; // associated graph
	/**
	 * The {@link Graph} that is nested inside of this {@link Node}.
	 */
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

	/**
	 * Returns all incoming {@link Edge}s of this {@link Node}. The full graph
	 * hierarchy is scanned for incoming edges, and not just the
	 * {@link #getGraph() associated graph}.
	 *
	 * @return All incoming {@link Edge}s.
	 */
	public Set<? extends Edge> getAllIncomingEdges() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Edge> incoming = new HashSet<Edge>();
		incoming.addAll(getLocalIncomingEdges());
		if (graph.getNestingNode() != null) {
			incoming.addAll(graph.getNestingNode().getAllIncomingEdges());
		}
		return incoming;
	}

	/**
	 * Returns all neighbors of this {@link Node}. The full graph hierarchy is
	 * scanned for neighbors, and not just the {@link #getGraph() associated
	 * graph}.
	 *
	 * @return All neighbors.
	 */
	public Set<Node> getAllNeighbors() {
		Set<Node> neighbors = new HashSet<Node>();
		neighbors.addAll(getAllPredecessorNodes());
		neighbors.addAll(getAllSuccessorNodes());
		return neighbors;
	}

	/**
	 * Returns all outgoing {@link Edge}s of this {@link Node}. The full graph
	 * hierarchy is scanned for outgoing edges, and not just the
	 * {@link #getGraph() associated graph}.
	 *
	 * @return All outgoing {@link Edge}s.
	 */
	public Set<? extends Edge> getAllOutgoingEdges() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Edge> outgoing = new HashSet<Edge>();
		outgoing.addAll(getLocalOutgoingEdges());
		if (graph.getNestingNode() != null) {
			outgoing.addAll(graph.getNestingNode().getAllOutgoingEdges());
		}
		return outgoing;
	}

	/**
	 * Returns all predecessor {@link Node}s of this {@link Node}. The full
	 * graph hierarchy is scanned for predecessor nodes, and not just the
	 * {@link #getGraph() associated graph}.
	 *
	 * @return All predecessor {@link Node}s.
	 */
	public Set<? extends Node> getAllPredecessorNodes() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Node> predecessors = new HashSet<Node>();
		predecessors.addAll(getLocalPredecessorNodes());
		if (graph.getNestingNode() != null) {
			predecessors
					.addAll(graph.getNestingNode().getAllPredecessorNodes());
		}
		return predecessors;
	}

	/**
	 * Returns all successor {@link Node}s of this {@link Node}. The full graph
	 * hierarchy is scanned for successor nodes, and not just the
	 * {@link #getGraph() associated graph}.
	 *
	 * @return All successor {@link Node}s.
	 */
	public Set<? extends Node> getAllSuccessorNodes() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Node> successors = new HashSet<Node>();
		successors.addAll(getLocalSuccessorNodes());
		if (graph.getNestingNode() != null) {
			successors.addAll(graph.getNestingNode().getAllSuccessorNodes());
		}
		return successors;
	}

	public Map<String, Object> getAttrs() {
		return attrs;
	}

	public Graph getGraph() {
		return graph;
	}

	/**
	 * Returns the local incoming {@link Edge}s of this {@link Node}. Only the
	 * {@link #getGraph() associated graph} is scanned for incoming edges, and
	 * not the whole graph hierarchy.
	 *
	 * @return The local incoming {@link Edge}s.
	 */
	public Set<Edge> getLocalIncomingEdges() {
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

	public Set<Node> getLocalNeighbors() {
		Set<Node> neighbors = new HashSet<Node>();
		neighbors.addAll(getLocalPredecessorNodes());
		neighbors.addAll(getLocalSuccessorNodes());
		return neighbors;
	}

	/**
	 * Returns the local outgoing {@link Edge}s of this {@link Node}. Only the
	 * {@link #getGraph() associated graph} is scanned for outgoing edges, and
	 * not the whole graph hierarchy.
	 *
	 * @return The local outgoing {@link Edge}s.
	 */
	public Set<Edge> getLocalOutgoingEdges() {
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

	/**
	 * Returns the local predecessor {@link Node}s of this {@link Node}. Only
	 * the {@link #getGraph() associated graph} is scanned for predecessor
	 * nodes, and not the whole graph hierarchy.
	 *
	 * @return The local predecessor {@link Node}s.
	 */
	public Set<Node> getLocalPredecessorNodes() {
		Set<Node> predecessors = new HashSet<Node>();
		for (Edge incoming : getLocalIncomingEdges()) {
			predecessors.add(incoming.getSource());
		}
		return predecessors;
	}

	/**
	 * Returns the local successor {@link Node}s of this {@link Node}. Only the
	 * {@link #getGraph() associated graph} is scanned for successor nodes, and
	 * not the whole graph hierarchy.
	 *
	 * @return The local successor {@link Node}s.
	 */
	public Set<Node> getLocalSuccessorNodes() {
		Set<Node> successors = new HashSet<Node>();
		for (Edge outgoing : getLocalOutgoingEdges()) {
			successors.add(outgoing.getTarget());
		}
		return successors;
	}

	/**
	 * Returns the {@link Graph} that is nested inside of this {@link Node}. May
	 * be <code>null</code>.
	 *
	 * @return The {@link Graph} that is nested inside of this {@link Node}, or
	 *         <code>null</code>.
	 */
	public Graph getNestedGraph() {
		return nestedGraph;
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
		if (nestedGraph.getNestingNode() != this) {
			nestedGraph.setNestingNode(this);
		}
	}

	@Override
	public String toString() {
		return String.format("Node {%s attrs}", attrs.size()); //$NON-NLS-1$
	}

}

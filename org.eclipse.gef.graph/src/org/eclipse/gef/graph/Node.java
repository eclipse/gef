/*******************************************************************************
 * Copyright (c) 2013, 2017 Fabian Steeg and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Fabian Steeg                 - initial API and implementation (bug #372365)
 *     Matthias Wienand (itemis AG) - contribution for bugs #438734, #461296
 *     Alexander Nyßen (itemis AG)  - refactoring of builder API (bug #480293)
 *******************************************************************************/
package org.eclipse.gef.graph;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.eclipse.gef.common.attributes.IAttributeStore;
import org.eclipse.gef.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef.graph.Graph.Builder.Context;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * A {@link Node} represents a vertex within a {@link Graph}.
 *
 * @author Fabian Steeg
 * @author Alexander Nyßen
 *
 */
public class Node implements IAttributeStore {

	/**
	 * The {@link Builder} can be used to construct a {@link Node} little by
	 * little.
	 */
	public static class Builder {

		private Graph.Builder.Context context;
		private Object key;
		private List<Entry<Object, Object>> attr = new ArrayList<>();

		/**
		 * Constructs a new (anonymous) context-free {@link Node.Builder}, which
		 * can only be used to construct a single node via {@link #buildNode()},
		 * i.e. which cannot be chained.
		 */
		public Builder() {
			this(null);
		}

		/**
		 * Constructs a new (anonymous) {@link Node.Builder} for the given
		 * {@link Context}.
		 *
		 * @param context
		 *            The context in which the {@link Node.Builder} is used.
		 */
		public Builder(Graph.Builder.Context context) {
			this(context, null);
		}

		/**
		 * Constructs a new identifiable {@link Node.Builder} for the given
		 * {@link Context}.
		 *
		 * @param context
		 *            The context in which the {@link Node.Builder} is used.
		 * @param key
		 *            The key to identify the builder.
		 */
		public Builder(Graph.Builder.Context context, Object key) {
			this.context = context;
			if (context != null) {
				this.key = (key == null) ? UUID.randomUUID() : key;
				this.context.nodeBuilders.put(this.key, this);
			}
		}

		/**
		 * Uses the given setter to set the attribute value.
		 *
		 * @param <T>
		 *            The type of the attribute.
		 *
		 * @param setter
		 *            The setter to apply.
		 * @param value
		 *            The value to apply.
		 * @return <code>this</code> for convenience.
		 */
		public <T> Node.Builder attr(BiConsumer<Node, T> setter, T value) {
			attr.add(new AbstractMap.SimpleImmutableEntry<>(setter, value));
			return this;
		}

		/**
		 * Puts the given <i>key</i>-<i>value</i>-pair into the
		 * {@link Node#attributesProperty() attributesProperty map} of the
		 * {@link Node} which is constructed by this {@link Builder}.
		 *
		 * @param key
		 *            The attribute name which is inserted.
		 * @param value
		 *            The attribute value which is inserted.
		 * @return <code>this</code> for convenience.
		 */
		public Node.Builder attr(String key, Object value) {
			attr.add(new AbstractMap.SimpleImmutableEntry<>(key, value));
			return this;
		}

		/**
		 * Constructs a new {@link Graph} from the values which have been
		 * supplied to the builder chain.
		 *
		 * @return A new {@link Graph}.
		 */
		public Graph build() {
			return context.builder.build();
		}

		/**
		 * Creates a new {@link Node}, setting the values specified via this
		 * {@link Node.Builder}.
		 *
		 * @return A newly created {@link Node}.
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Node buildNode() {
			Node n = new Node();
			for (Entry<Object, Object> s : attr) {
				if (s.getKey() instanceof String) {
					n.attributesProperty().put((String) s.getKey(), s.getValue());
				} else {
					((BiConsumer) s.getKey()).accept(n, s.getValue());
				}
			}
			return n;
		}

		/**
		 * Constructs a new {@link Edge.Builder}.
		 *
		 * @param sourceNodeOrKey
		 *            The source {@link Node} or a key to identify the source
		 *            {@link Node} (or its {@link Node.Builder}).
		 *
		 * @param targetNodeOrKey
		 *            The target {@link Node} or a key to identify the target
		 *            {@link Node} (or its {@link Node.Builder}).
		 *
		 * @return A new {@link Edge.Builder}.
		 */
		public Edge.Builder edge(Object sourceNodeOrKey, Object targetNodeOrKey) {
			Edge.Builder eb = new Edge.Builder(context, sourceNodeOrKey, targetNodeOrKey);
			return eb;
		}

		/**
		 * Returns the key that can be used to identify this
		 * {@link Node.Builder}
		 *
		 * @return The key that can be used for identification.
		 */
		protected Object getKey() {
			return key;
		}

		/**
		 * Constructs a new (anonymous) {@link Node.Builder}.
		 *
		 * @return A new {@link Node.Builder}.
		 */
		public Node.Builder node() {
			Node.Builder nb = new Node.Builder(context);
			context.nodeKeys.add(nb.getKey());
			return nb;
		}

		/**
		 * Constructs a new (identifiable) {@link Node.Builder}.
		 *
		 * @param key
		 *            The key that can be used to identify the
		 *            {@link Node.Builder}
		 *
		 * @return A new {@link Node.Builder}.
		 */
		public Node.Builder node(Object key) {
			Node.Builder nb = new Node.Builder(context, key);
			context.nodeKeys.add(key);
			return nb;
		}

	}

	private final ReadOnlyMapWrapper<String, Object> attributesProperty = new ReadOnlyMapWrapperEx<>(this,
			ATTRIBUTES_PROPERTY, FXCollections.<String, Object>observableHashMap());

	/**
	 * The {@link Graph} which this {@link Node} belongs to.
	 */
	private Graph graph; // associated graph
	/**
	 * The {@link Graph} that is nested inside of this {@link Node}.
	 */
	private Graph nestedGraph;

	/**
	 * Constructs a new {@link Node}.
	 */
	public Node() {
		this(new HashMap<String, Object>());
	}

	/**
	 * Constructs a new {@link Node} and copies the given
	 * <i>attributesProperty</i> into the {@link #attributesProperty()
	 * attributesProperty map} of this {@link Node}.
	 *
	 * @param attributes
	 *            A {@link Map} containing the attributesProperty which are
	 *            copied into the {@link #attributesProperty()
	 *            attributesProperty map} of this {@link Node}.
	 */
	public Node(Map<String, Object> attributes) {
		this.attributesProperty.putAll(attributes);
	}

	@Override
	public ReadOnlyMapProperty<String, Object> attributesProperty() {
		return attributesProperty.getReadOnlyProperty();
	}

	/**
	 * Returns all incoming {@link Edge}s of this {@link Node}. The full graph
	 * hierarchy is scanned for incoming edges, and not just the
	 * {@link #getGraph() associated graph}.
	 *
	 * @return All incoming {@link Edge}s.
	 */
	public Set<Edge> getAllIncomingEdges() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Edge> incoming = Collections.newSetFromMap(new IdentityHashMap<Edge, Boolean>());
		incoming.addAll(getIncomingEdges());
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
		Set<Node> neighbors = Collections.newSetFromMap(new IdentityHashMap<Node, Boolean>());
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
	public Set<Edge> getAllOutgoingEdges() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Edge> outgoing = Collections.newSetFromMap(new IdentityHashMap<Edge, Boolean>());
		outgoing.addAll(getOutgoingEdges());
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
	public Set<Node> getAllPredecessorNodes() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Node> predecessors = Collections.newSetFromMap(new IdentityHashMap<Node, Boolean>());
		predecessors.addAll(getPredecessorNodes());
		if (graph.getNestingNode() != null) {
			predecessors.addAll(graph.getNestingNode().getAllPredecessorNodes());
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
	public Set<Node> getAllSuccessorNodes() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Node> successors = Collections.newSetFromMap(new IdentityHashMap<Node, Boolean>());
		successors.addAll(getSuccessorNodes());
		if (graph.getNestingNode() != null) {
			successors.addAll(graph.getNestingNode().getAllSuccessorNodes());
		}
		return successors;
	}

	@Override
	public ObservableMap<String, Object> getAttributes() {
		return attributesProperty.get();
	}

	/**
	 * Returns the {@link Graph} to which this {@link Node} belongs.
	 *
	 * @return The {@link Graph} to which this {@link Node} belongs.
	 */
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
	public Set<Edge> getIncomingEdges() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Edge> incoming = Collections.newSetFromMap(new IdentityHashMap<Edge, Boolean>());
		for (Edge e : graph.getEdges()) {
			if (e.getTarget() == this) {
				incoming.add(e);
			}
		}
		return incoming;
	}

	/**
	 * Returns all (local) neighbors of this {@link Node}, i.e. the union of the
	 * {@link #getPredecessorNodes()} and {@link #getSuccessorNodes()} .
	 *
	 * @return All (local) neighbors of this {@link Node}.
	 */
	public Set<Node> getNeighbors() {
		Set<Node> neighbors = Collections.newSetFromMap(new IdentityHashMap<Node, Boolean>());
		neighbors.addAll(getPredecessorNodes());
		neighbors.addAll(getSuccessorNodes());
		return neighbors;
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

	/**
	 * Returns the local outgoing {@link Edge}s of this {@link Node}. Only the
	 * {@link #getGraph() associated graph} is scanned for outgoing edges, and
	 * not the whole graph hierarchy.
	 *
	 * @return The local outgoing {@link Edge}s.
	 */
	public Set<Edge> getOutgoingEdges() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Edge> outgoing = Collections.newSetFromMap(new IdentityHashMap<Edge, Boolean>());
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
	public Set<Node> getPredecessorNodes() {
		Set<Node> predecessors = Collections.newSetFromMap(new IdentityHashMap<Node, Boolean>());
		for (Edge incoming : getIncomingEdges()) {
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
	public Set<Node> getSuccessorNodes() {
		Set<Node> successors = Collections.newSetFromMap(new IdentityHashMap<Node, Boolean>());
		for (Edge outgoing : getOutgoingEdges()) {
			successors.add(outgoing.getTarget());
		}
		return successors;
	}

	/**
	 * Sets the {@link Graph} to which this {@link Node} belongs to the given
	 * value.
	 *
	 * @param graph
	 *            The new {@link Graph} for this {@link Node}.
	 */
	void setGraph(Graph graph) {
		if (graph != null && !graph.getNodes().contains(this)) {
			throw new IllegalArgumentException("Node is not contained in graph " + graph);
		}
		this.graph = graph;
	}

	/**
	 * Sets the {@link Graph} which is nested inside this {@link Node} to the
	 * given value.
	 *
	 * @param nestedGraph
	 *            The new nested {@link Graph} for this {@link Node}.
	 */
	public void setNestedGraph(Graph nestedGraph) {
		Graph oldNestedGraph = this.nestedGraph;
		this.nestedGraph = nestedGraph;
		if (oldNestedGraph != null && oldNestedGraph != nestedGraph) {
			oldNestedGraph.setNestingNode(null);
		}
		if (nestedGraph != null && oldNestedGraph != nestedGraph) {
			nestedGraph.setNestingNode(this);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Node {");
		boolean separator = false;
		TreeMap<String, Object> sortedAttrs = new TreeMap<>();
		sortedAttrs.putAll(attributesProperty);
		for (Object attrKey : sortedAttrs.keySet()) {
			if (separator) {
				sb.append(", ");
			} else {
				separator = true;
			}
			sb.append(attrKey.toString() + " : " + attributesProperty.get(attrKey));
		}
		sb.append("}");
		return sb.toString();
	}

}

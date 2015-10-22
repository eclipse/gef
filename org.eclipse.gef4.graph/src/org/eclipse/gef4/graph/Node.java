/*******************************************************************************
 * Copyright (c) 2013, 2015 Fabian Steeg and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg                 - initial API and implementation (bug #372365)
 *     Matthias Wienand (itemis AG) - contribution for bugs #438734, #461296
 *     Alexander Nyßen              - refactoring of builder API (bug #480293)
 *******************************************************************************/
package org.eclipse.gef4.graph;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.gef4.common.notify.IMapObserver;
import org.eclipse.gef4.common.notify.ObservableMap;
import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.gef4.graph.Graph.Builder.Context;

/**
 * A {@link Node} represents a vertex within a {@link Graph}.
 *
 * @author Fabian Steeg
 *
 */
public class Node implements IPropertyChangeNotifier {

	/**
	 * The {@link Builder} can be used to construct a {@link Node} little by
	 * little.
	 */
	public static class Builder {
		private Map<String, Object> attrs = new HashMap<String, Object>();

		private Graph.Builder.Context context;
		private Object key;

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
				if (key == null) {
					this.key = new Integer(System.identityHashCode(this)).toString();
				} else {
					this.key = key;
				}
				this.context.nodeBuilders.put(key, this);
			}
		}

		/**
		 * Puts the given <i>key</i>-<i>value</i>-pair into the
		 * {@link Node#getAttrs() attributes map} of the {@link Node} which is
		 * constructed by this {@link Builder}.
		 *
		 * @param key
		 *            The attribute name which is inserted.
		 * @param value
		 *            The attribute value which is inserted.
		 * @return <code>this</code> for convenience.
		 */
		public Node.Builder attr(String key, Object value) {
			attrs.put(key, value);
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
		public Node buildNode() {
			return new Node(attrs);
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
			return nb;
		}

	}

	/**
	 * The property name that is used to notify change listeners about changes
	 * made to the attributes of this Node. A property change event for this
	 * property will have its old value set to a
	 * <code>Map&lt;String, Object&gt;</code> holding the old attributes and its
	 * new value set to a <code>Map&lt;String, Object&gt;</code> holding the new
	 * attributes.
	 */
	public static final String ATTRIBUTES_PROPERTY = "attributes";

	private IMapObserver<String, Object> attributesObserver = new IMapObserver<String, Object>() {
		@Override
		public void afterChange(ObservableMap<String, Object> observableMap, Map<String, Object> previousMap) {
			pcs.firePropertyChange(ATTRIBUTES_PROPERTY, previousMap, observableMap);
		}
	};

	/**
	 * The {@link PropertyChangeSupport} which handles (un-)registration of
	 * {@link PropertyChangeListener}s and firing of {@link PropertyChangeEvent}
	 * s.
	 */
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private final ObservableMap<String, Object> attrs = new ObservableMap<String, Object>();
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
	 * Constructs a new {@link Node} and copies the given <i>attributes</i> into
	 * the {@link #getAttrs() attributes map} of this {@link Node}.
	 *
	 * @param attrs
	 *            A {@link Map} containing the attributes which are copied into
	 *            the {@link #getAttrs() attributes map} of this {@link Node}.
	 */
	public Node(Map<String, Object> attrs) {
		this.attrs.putAll(attrs);
		this.attrs.addMapObserver(attributesObserver);
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
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
	public Set<Edge> getAllIncomingEdges() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Edge> incoming = Collections.newSetFromMap(new IdentityHashMap<Edge, Boolean>());
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
	public Set<Node> getAllPredecessorNodes() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Node> predecessors = Collections.newSetFromMap(new IdentityHashMap<Node, Boolean>());
		predecessors.addAll(getLocalPredecessorNodes());
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
		successors.addAll(getLocalSuccessorNodes());
		if (graph.getNestingNode() != null) {
			successors.addAll(graph.getNestingNode().getAllSuccessorNodes());
		}
		return successors;
	}

	/**
	 * Returns the attributes map of this {@link Node} by reference. When this
	 * map is changed, a {@link PropertyChangeEvent} is fired for the
	 * {@link #ATTRIBUTES_PROPERTY}.
	 *
	 * @return The attributes map of this {@link Node} by reference.
	 */
	public Map<String, Object> getAttrs() {
		return attrs;
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
	public Set<Edge> getLocalIncomingEdges() {
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
	 * {@link #getLocalPredecessorNodes()} and {@link #getLocalSuccessorNodes()}
	 * .
	 *
	 * @return All (local) neighbors of this {@link Node}.
	 */
	public Set<Node> getLocalNeighbors() {
		Set<Node> neighbors = Collections.newSetFromMap(new IdentityHashMap<Node, Boolean>());
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
	public Set<Node> getLocalPredecessorNodes() {
		Set<Node> predecessors = Collections.newSetFromMap(new IdentityHashMap<Node, Boolean>());
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
		Set<Node> successors = Collections.newSetFromMap(new IdentityHashMap<Node, Boolean>());
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

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Sets the {@link Graph} to which this {@link Node} belongs to the given
	 * value.
	 *
	 * @param graph
	 *            The new {@link Graph} for this {@link Node}.
	 */
	public void setGraph(Graph graph) {
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
		this.nestedGraph = nestedGraph;
		if (nestedGraph.getNestingNode() != this) {
			nestedGraph.setNestingNode(this);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Node {");
		boolean separator = false;
		TreeMap<String, Object> sortedAttrs = new TreeMap<String, Object>();
		sortedAttrs.putAll(attrs);
		for (Object attrKey : sortedAttrs.keySet()) {
			if (separator) {
				sb.append(", ");
			} else {
				separator = true;
			}
			sb.append(attrKey.toString() + " : " + attrs.get(attrKey));
		}
		sb.append("}");
		return sb.toString();
	}

}

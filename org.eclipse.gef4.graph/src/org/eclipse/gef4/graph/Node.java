/*******************************************************************************
 * Copyright (c) 2013-2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 372365
 *               Matthias Wienand (itemis AG) - contribution for bugs 438734, 461296
 *******************************************************************************/
package org.eclipse.gef4.graph;

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

public final class Node implements IPropertyChangeNotifier {

	public static class Builder {
		private Map<String, Object> attrs = new HashMap<String, Object>();

		public Node.Builder attr(String key, Object value) {
			attrs.put(key, value);
			return this;
		}

		public Node build() {
			return new Node(attrs);
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
		public void afterChange(ObservableMap<String, Object> observableMap,
				Map<String, Object> previousMap) {
			pcs.firePropertyChange(ATTRIBUTES_PROPERTY, previousMap,
					observableMap);
		}
	};

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

	public Node() {
		this(new HashMap<String, Object>());
	}

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
	public Set<? extends Edge> getAllIncomingEdges() {
		if (graph == null) {
			return Collections.emptySet();
		}
		Set<Edge> incoming = Collections
				.newSetFromMap(new IdentityHashMap<Edge, Boolean>());
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
		Set<Node> neighbors = Collections
				.newSetFromMap(new IdentityHashMap<Node, Boolean>());
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
		Set<Edge> outgoing = Collections
				.newSetFromMap(new IdentityHashMap<Edge, Boolean>());
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
		Set<Node> predecessors = Collections
				.newSetFromMap(new IdentityHashMap<Node, Boolean>());
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
		Set<Node> successors = Collections
				.newSetFromMap(new IdentityHashMap<Node, Boolean>());
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
		Set<Edge> incoming = Collections
				.newSetFromMap(new IdentityHashMap<Edge, Boolean>());
		for (Edge e : graph.getEdges()) {
			if (e.getTarget() == this) {
				incoming.add(e);
			}
		}
		return incoming;
	}

	public Set<Node> getLocalNeighbors() {
		Set<Node> neighbors = Collections
				.newSetFromMap(new IdentityHashMap<Node, Boolean>());
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
		Set<Edge> outgoing = Collections
				.newSetFromMap(new IdentityHashMap<Edge, Boolean>());
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
		Set<Node> predecessors = Collections
				.newSetFromMap(new IdentityHashMap<Node, Boolean>());
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
		Set<Node> successors = Collections
				.newSetFromMap(new IdentityHashMap<Node, Boolean>());
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

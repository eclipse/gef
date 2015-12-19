/*******************************************************************************
 * Copyright (c) 2013, 2015 Fabian Steeg and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg    - initial API and implementation (bug #372365)
 *     Alexander Nyßen - refactoring of builder API (bug #480293)
 *
 *******************************************************************************/
package org.eclipse.gef4.graph;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;
import org.eclipse.gef4.common.properties.ListProperty;
import org.eclipse.gef4.common.properties.MapProperty;

/**
 * A {@link Graph} is a container for {@link Node}s and {@link Edge}s between
 * those {@link Node}s.
 *
 * @author Fabian Steeg
 * @author anyssen
 *
 */
public final class Graph implements IPropertyChangeNotifier {

	/**
	 * The {@link Builder} can be used to construct a new {@link Graph} little
	 * by little.
	 */
	public static class Builder {

		/**
		 * A context object passed to nested builders when creating a builder
		 * chain.
		 *
		 */
		protected class Context {
			/**
			 * The {@link Graph.Builder} used to construct the {@link Graph},
			 * i.e. the root of the builder chain.
			 */
			protected Graph.Builder builder;
			/**
			 * {@link Node.Builder}s, which are part of the builder chain,
			 * mapped to their keys.
			 */
			protected Map<Object, Node.Builder> nodeBuilders = new HashMap<>();
			/**
			 * {@link Edge.Builder}s, which are part of the builder chain.
			 */
			protected List<Edge.Builder> edgeBuilders = new ArrayList<>();
		}

		// use linked hash map to preserve ordering
		private LinkedHashMap<Object, Node> nodes = new LinkedHashMap<>();
		private List<Edge> edges = new ArrayList<>();
		private Context context;

		private Map<String, Object> attrs = new HashMap<>();

		/**
		 * Constructs a new {@link Builder} without {@link Node}s and
		 * {@link Edge}s.
		 */
		public Builder() {
			context = new Context();
			context.builder = this;
		}

		/**
		 * Puts the given <i>key</i>-<i>value</i>-pair into the
		 * {@link Graph#getAttrs() attributes map} of the {@link Graph} which is
		 * constructed by this {@link Builder}.
		 *
		 * @param key
		 *            The attribute name which is inserted.
		 * @param value
		 *            The attribute value which is inserted.
		 * @return <code>this</code> for convenience.
		 */
		public Graph.Builder attr(String key, Object value) {
			attrs.put(key, value);
			return this;
		}

		/**
		 * Constructs a new {@link Graph} from the values which have been
		 * supplied to this {@link Builder}.
		 *
		 * @return A new {@link Graph} from the values which have been supplied
		 *         to this {@link Builder}.
		 */
		public Graph build() {
			for (Node.Builder nb : context.nodeBuilders.values()) {
				nodes.put(nb.getKey(), nb.buildNode());
			}
			for (Edge.Builder eb : context.edgeBuilders) {
				edges.add(eb.buildEdge());
			}

			return new Graph(attrs, nodes.values(), edges);
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
		 * Adds the given {@link Edge}s to the {@link Graph} which is
		 * constructed by this {@link Builder}.
		 *
		 * @param edges
		 *            The {@link Edge}s which are added to the {@link Graph}
		 *            which is constructed by this {@link Builder}.
		 * @return <code>this</code> for convenience.
		 */
		public Graph.Builder edges(Edge... edges) {
			this.edges.addAll(Arrays.asList(edges));
			return this;
		}

		/**
		 * Retrieves the node already created by a builder for the given key, or
		 * creates a new one via the respective {@link Node.Builder}.
		 *
		 * @param key
		 *            The key to identify the {@link Node} or
		 *            {@link Node.Builder}.
		 * @return An existing or newly created {@link Node}.
		 */
		protected Node findOrCreateNode(Object key) {
			// if we have already created a new with the given key, return the
			// created node
			if (nodes.containsKey(key)) {
				return nodes.get(key);
			} else {
				// create a new node
				org.eclipse.gef4.graph.Node.Builder nodeBuilder = context.nodeBuilders.get(key);
				if (nodeBuilder == null) {
					return null;
				}
				nodes.put(key, nodeBuilder.buildNode());
			}
			return nodes.get(key);
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

		/**
		 * Adds the given {@link Node}s to the {@link Graph} which is
		 * constructed by this {@link Builder}.
		 *
		 * @param nodes
		 *            The {@link Node}s which are added to the {@link Graph}
		 *            which is constructed by this {@link Builder}.
		 * @return <code>this</code> for convenience.
		 */
		public Graph.Builder nodes(Node... nodes) {
			for (Node n : nodes) {
				this.nodes.put(System.identityHashCode(n), n);
			}
			return this;
		}
	}

	/**
	 * The property name that is used to notify change listeners about changes
	 * made to the attributes of this Graph. A property change event for this
	 * property will have its old value set to a
	 * <code>Map&lt;String, Object&gt;</code> holding the old attributes and its
	 * new value set to a <code>Map&lt;String, Object&gt;</code> holding the new
	 * attributes.
	 */
	public static final String ATTRIBUTES_PROPERTY = "attributes";

	/**
	 * The property name that is used to notify change listeners about
	 * added/removed nodes. A property change event for this property will have
	 * its old value set to a <code>List&lt;Node&gt;</code> holding the old
	 * nodes and its new value set to a <code>List&lt;Node&gt;</code> holding
	 * the new nodes.
	 */
	public static final String NODES_PROPERTY = "nodes";

	/**
	 * The property name that is used to notify change listeners about
	 * added/removed edges. A property change event for this property will have
	 * its old value set to a <code>List&lt;Edge&gt;</code> holding the old
	 * edges and its new value set to a <code>List&lt;Edge&gt;</code> holding
	 * the new edges.
	 */
	public static final String EDGES_PROPERTY = "edges";

	/**
	 * {@link Node}s directly contained by this {@link Graph}.
	 */
	private final ListProperty<Node> nodes = new ListProperty<>(this, NODES_PROPERTY);

	/**
	 * {@link Edge}s for which this {@link Graph} is a common ancestor for
	 * {@link Edge#getSource() source} and {@link Edge#getTarget() target}.
	 */
	private final ListProperty<Edge> edges = new ListProperty<>(this, EDGES_PROPERTY);

	/**
	 * Attributes of this {@link Graph}.
	 */
	private final MapProperty<String, Object> attrs = new MapProperty<>(this, ATTRIBUTES_PROPERTY);

	/**
	 * {@link Node} which contains this {@link Graph}. May be <code>null</code>
	 * .
	 */
	private Node nestingNode; // when contained as a nested graph within a node

	/**
	 * Default constructor, using empty collections for attributes, nodes, and
	 * edges.
	 */
	public Graph() {
		this(new HashMap<String, Object>(), new ArrayList<Node>(), new ArrayList<Edge>());
	}

	/**
	 * Constructs a new {@link Graph} from the given attributes, nodes, and
	 * edges. Associates all nodes and edges with this {@link Graph}.
	 *
	 * @param attrs
	 *            Map of graph attributes.
	 * @param nodes
	 *            List of {@link Node}s.
	 * @param edges
	 *            List of {@link Edge}s.
	 */
	public Graph(Map<String, Object> attrs, Collection<? extends Node> nodes, Collection<? extends Edge> edges) {
		this.attrs.putAll(attrs);
		this.nodes.addAll(nodes);
		this.edges.addAll(edges);
		// set graph on nodes and edges
		for (Node n : nodes) {
			n.setGraph(this);
		}
		for (Edge e : edges) {
			e.setGraph(this);
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		nodes.addPropertyChangeListener(listener);
		edges.addPropertyChangeListener(listener);
		attrs.addPropertyChangeListener(listener);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Graph)) {
			return false;
		}
		Graph thatGraph = (Graph) other;
		boolean attrsEqual = this.getAttrs().equals(thatGraph.getAttrs());
		boolean nodesEqual = this.getNodes().equals(thatGraph.getNodes());
		boolean edgesEqual = this.getEdges().equals(thatGraph.getEdges());
		return attrsEqual && nodesEqual && edgesEqual;
	}

	/**
	 * Returns the map of attributes of this {@link Graph} by reference.
	 *
	 * @return The map of attributes of this {@link Graph} by reference.
	 */
	public Map<String, Object> getAttrs() {
		return attrs;
	}

	/**
	 * Returns the list of {@link Edge}s of this {@link Graph} by reference.
	 *
	 * @return The list of {@link Edge}s of this {@link Graph} by reference.
	 */
	public List<Edge> getEdges() {
		return edges;
	}

	/**
	 * Returns the {@link Node} in which this {@link Graph} is nested. Returns
	 * <code>null</code> when this {@link Graph} is not nested.
	 *
	 * @return The {@link Node} in which this {@link Graph} is nested, or
	 *         <code>null</code>.
	 */
	public Node getNestingNode() {
		return nestingNode;
	}

	/**
	 * Returns the list of {@link Node}s of this {@link Graph} by reference.
	 *
	 * @return The list of {@link Node}s of this {@link Graph} by reference.
	 */
	// TOOD: expose list property??
	public List<Node> getNodes() {
		return nodes;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + getAttrs().hashCode();
		result = 31 * result + getNodes().hashCode();
		result = 31 * result + getEdges().hashCode();
		return result;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		nodes.removePropertyChangeListener(listener);
		edges.removePropertyChangeListener(listener);
		attrs.removePropertyChangeListener(listener);
	}

	/**
	 * Sets the nesting {@link Node} of this {@link Graph}.
	 *
	 * @param nestingNode
	 *            The new {@link Node} in which this {@link Graph} is nested.
	 */
	public void setNestingNode(Node nestingNode) {
		this.nestingNode = nestingNode;
		if (nestingNode.getNestedGraph() != this) {
			nestingNode.setNestedGraph(this);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Graph");
		sb.append(" attr {");
		boolean separator = false;

		TreeMap<String, Object> sortedAttrs = new TreeMap<>();
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
		sb.append(".nodes {");
		separator = false;
		for (Node n : getNodes()) {
			if (separator) {
				sb.append(", ");
			} else {
				separator = true;
			}
			sb.append(n.toString());
		}
		sb.append("}");
		sb.append(".edges {");
		separator = false;
		for (Edge e : getEdges()) {
			if (separator) {
				sb.append(", ");
			} else {
				separator = true;
			}
			sb.append(e.toString());
		}
		sb.append("}");
		return sb.toString();
	}

}

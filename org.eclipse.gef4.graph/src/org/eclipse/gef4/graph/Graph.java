/*******************************************************************************
 * Copyright (c) 2013, 2015 Fabian Steeg and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see #372365)
 *
 *******************************************************************************/
package org.eclipse.gef4.graph;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.gef4.common.notify.IListObserver;
import org.eclipse.gef4.common.notify.IMapObserver;
import org.eclipse.gef4.common.notify.ObservableList;
import org.eclipse.gef4.common.notify.ObservableMap;
import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;

public final class Graph implements IPropertyChangeNotifier {

	public static class Builder {

		private List<Node> nodes = new ArrayList<Node>();
		private List<Edge> edges = new ArrayList<Edge>();
		private Map<String, Object> attrs = new HashMap<String, Object>();

		public Builder() {
		}

		public Graph.Builder attr(String key, Object value) {
			attrs.put(key, value);
			return this;
		}

		public Graph build() {
			return new Graph(attrs, nodes, edges);
		}

		public Graph.Builder edges(Edge... edges) {
			this.edges.addAll(Arrays.asList(edges));
			return this;
		}

		public Graph.Builder nodes(Node... nodes) {
			this.nodes.addAll(Arrays.asList(nodes));
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

	private IMapObserver<String, Object> attributesObserver = new IMapObserver<String, Object>() {
		@Override
		public void afterChange(ObservableMap<String, Object> observableMap,
				Map<String, Object> previousMap) {
			pcs.firePropertyChange(ATTRIBUTES_PROPERTY, previousMap,
					observableMap);
		}
	};

	private IListObserver<Node> nodesObserver = new IListObserver<Node>() {
		@Override
		public void afterChange(ObservableList<Node> observableList,
				List<Node> previousList) {
			pcs.firePropertyChange(NODES_PROPERTY, previousList, observableList);
		}
	};

	private IListObserver<Edge> edgesObserver = new IListObserver<Edge>() {
		@Override
		public void afterChange(ObservableList<Edge> observableList,
				List<Edge> previousList) {
			pcs.firePropertyChange(EDGES_PROPERTY, previousList, observableList);
		}
	};

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * {@link Node}s directly contained by this {@link Graph}.
	 */
	private final ObservableList<Node> nodes = new ObservableList<Node>();

	/**
	 * {@link Edge}s for which this {@link Graph} is a common ancestor for
	 * {@link Edge#getSource() source} and {@link Edge#getTarget() target}.
	 */
	private final ObservableList<Edge> edges = new ObservableList<Edge>();

	/**
	 * Attributes of this {@link Graph}.
	 */
	private final ObservableMap<String, Object> attrs = new ObservableMap<String, Object>();

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
		this(new HashMap<String, Object>(), new ArrayList<Node>(),
				new ArrayList<Edge>());
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
	public Graph(Map<String, Object> attrs, List<Node> nodes, List<Edge> edges) {
		this.attrs.putAll(attrs);
		this.attrs.addMapObserver(attributesObserver);
		this.nodes.addAll(nodes);
		this.nodes.addListObserver(nodesObserver);
		this.edges.addAll(edges);
		this.edges.addListObserver(edgesObserver);
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
		pcs.addPropertyChangeListener(listener);
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
		pcs.removePropertyChangeListener(listener);
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

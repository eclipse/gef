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
 *     Fabian Steeg                - initial API and implementation (bug #372365)
 *     Alexander Nyßen (itemis AG) - refactoring of builder API (bug #480293)
 *
 *******************************************************************************/
package org.eclipse.gef.graph;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import org.eclipse.gef.common.attributes.IAttributeStore;
import org.eclipse.gef.common.beans.property.ReadOnlyMapWrapperEx;
import org.eclipse.gef.graph.Graph.Builder.Context;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * An {@link Edge} represents a (directed) connection between two {@link Node}s
 * in a {@link Graph}.
 *
 * @author Fabian Steeg
 * @author anyssen
 *
 */
public class Edge implements IAttributeStore {

	/**
	 * The {@link Builder} can be used to construct an {@link Edge} little by
	 * little.
	 */
	public static class Builder {

		private List<Entry<Object, Object>> attr = new ArrayList<>();
		private Graph.Builder.Context context;
		private Object sourceNodeOrKey;
		private Object targetNodeOrKey;

		/**
		 * Constructs a new (anonymous) {@link Edge.Builder} for the given
		 * {@link Context}.
		 *
		 * @param context
		 *            The context in which the {@link Edge.Builder} is used.
		 * @param sourceNodeOrKey
		 *            The source {@link Node} or a key to identify the source
		 *            {@link Node} (or its {@link Node.Builder}).
		 *
		 * @param targetNodeOrKey
		 *            The target {@link Node} or a key to identify the target
		 *            {@link Node} (or its {@link Node.Builder}).
		 */
		public Builder(Graph.Builder.Context context, Object sourceNodeOrKey, Object targetNodeOrKey) {
			this.context = context;
			if (context != null) {
				context.edgeBuilders.add(this);
			}
			this.sourceNodeOrKey = sourceNodeOrKey;
			this.targetNodeOrKey = targetNodeOrKey;
		}

		/**
		 * Constructs a new (anonymous) context-free {@link Edge.Builder}, which
		 * can only be used to construct a single edge via {@link #buildEdge()},
		 * i.e. which cannot be chained.
		 *
		 * @param sourceNode
		 *            The source {@link Node}.
		 *
		 * @param targetNode
		 *            The target {@link Node}.
		 */
		public Builder(Node sourceNode, Node targetNode) {
			this(null, sourceNode, targetNode);
		}

		/**
		 * Uses the given setter to set the attribute value.
		 *
		 * @param <T>
		 *            The attribute type.
		 *
		 * @param setter
		 *            The setter to apply.
		 * @param value
		 *            The value to apply.
		 * @return <code>this</code> for convenience.
		 */
		public <T> Edge.Builder attr(BiConsumer<Edge, T> setter, T value) {
			attr.add(new AbstractMap.SimpleImmutableEntry<>(setter, value));
			return this;
		}

		/**
		 * Puts the given <i>key</i>-<i>value</i>-pair into the
		 * {@link Edge#attributesProperty() attributesProperty map} of the
		 * {@link Edge} which is constructed by this {@link Builder}.
		 *
		 * @param key
		 *            The attribute name which is inserted.
		 * @param value
		 *            The attribute value which is inserted.
		 * @return <code>this</code> for convenience.
		 */
		public Edge.Builder attr(String key, Object value) {
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
			if (context == null) {
				throw new IllegalStateException("The builder is not chained.");
			}
			return context.builder.build();
		}

		/**
		 * Creates a new {@link Edge}, setting the values specified via this
		 * {@link Edge.Builder}.
		 *
		 * @return A newly created {@link Edge}.
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Edge buildEdge() {
			if (context == null && !(sourceNodeOrKey instanceof Node)) {
				throw new IllegalStateException("May only use builder keys in case of chained builders.");
			}
			if (context == null && !(targetNodeOrKey instanceof Node)) {
				throw new IllegalStateException("May only use builder keys in case of chained builders.");
			}
			Node sourceNode = sourceNodeOrKey instanceof Node ? (Node) sourceNodeOrKey
					: context.builder.findOrCreateNode(sourceNodeOrKey);
			Node targetNode = targetNodeOrKey instanceof Node ? (Node) targetNodeOrKey
					: context.builder.findOrCreateNode(targetNodeOrKey);

			Edge e = new Edge(sourceNode, targetNode);
			for (Entry<Object, Object> s : attr) {
				if (s.getKey() instanceof String) {
					e.attributesProperty().put((String) s.getKey(), s.getValue());
				} else {
					((BiConsumer) s.getKey()).accept(e, s.getValue());
				}
			}
			return e;
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
		 * Constructs a new (anonymous) {@link Node.Builder} for a node.
		 *
		 * @return A {@link Node.Builder}.
		 */
		public Node.Builder node() {
			Node.Builder nb = new Node.Builder(context);
			context.nodeKeys.add(nb.getKey());
			return nb;
		}

		/**
		 * Constructs a new (identifiable) {@link Node.Builder} for a node.
		 *
		 * @param key
		 *            The key that can be used to identify the
		 *            {@link Node.Builder}
		 *
		 * @return A {@link Node.Builder}.
		 */
		public Node.Builder node(Object key) {
			Node.Builder nb = new Node.Builder(context, key);
			context.nodeKeys.add(key);
			return nb;
		}
	}

	private final ReadOnlyMapWrapper<String, Object> attributesProperty = new ReadOnlyMapWrapperEx<>(this,
			ATTRIBUTES_PROPERTY, FXCollections.<String, Object>observableHashMap());
	private Node source;
	private Node target;
	private Graph graph; // associated graph

	/**
	 * Constructs a new {@link Edge} which connects the given <i>source</i>
	 * {@link Node} with the given <i>target</i> {@link Node}. The given
	 * <i>attributesProperty</i> are copied into the
	 * {@link #attributesProperty() attributesProperty map} of this {@link Edge}
	 * .
	 *
	 * @param attributes
	 *            A {@link Map} containing the attributesProperty which are
	 *            copied into the {@link #attributesProperty()
	 *            attributesProperty map} of this {@link Edge}.
	 * @param source
	 *            The source {@link Node} for this {@link Edge}.
	 * @param target
	 *            The target {@link Node} for this {@link Edge}.
	 */
	public Edge(Map<String, Object> attributes, Node source, Node target) {
		this.attributesProperty.putAll(attributes);
		this.source = source;
		this.target = target;
	}

	/**
	 * Constructs a new {@link Edge} which connects the given <i>source</i>
	 * {@link Node} with the given <i>target</i> {@link Node}.
	 *
	 * @param source
	 *            The source {@link Node} for this {@link Edge}.
	 * @param target
	 *            The target {@link Node} for this {@link Edge}.
	 */
	public Edge(Node source, Node target) {
		this(new HashMap<String, Object>(), source, target);
	}

	@Override
	public ReadOnlyMapProperty<String, Object> attributesProperty() {
		return attributesProperty.getReadOnlyProperty();
	}

	@Override
	public ObservableMap<String, Object> getAttributes() {
		return attributesProperty.get();
	}

	/**
	 * Returns the {@link Graph} to which this {@link Edge} belongs.
	 *
	 * @return The {@link Graph} to which this {@link Edge} belongs.
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Returns the source {@link Node} of this {@link Edge}.
	 *
	 * @return The source {@link Node} of this {@link Edge}.
	 */
	public Node getSource() {
		return source;
	}

	/**
	 * Returns the target {@link Node} of this {@link Edge}.
	 *
	 * @return The target {@link Node} of this {@link Edge}.
	 */
	public Node getTarget() {
		return target;
	}

	/**
	 * Sets the {@link Graph} to which this {@link Edge} belongs to the given
	 * value.
	 *
	 * @param graph
	 *            The new {@link Graph} for this {@link Edge}.
	 */
	void setGraph(Graph graph) {
		if (graph != null && !graph.getEdges().contains(this)) {
			throw new IllegalArgumentException("Edge is not contained in graph " + graph);
		}
		this.graph = graph;
	}

	/**
	 * Sets the source {@link Node} of this {@link Edge} to the given value.
	 *
	 * @param source
	 *            The new source {@link Node} for this {@link Edge}.
	 */
	public void setSource(Node source) {
		this.source = source;
	}

	/**
	 * Sets the target {@link Node} of this {@link Edge} to the given value.
	 *
	 * @param target
	 *            The new target {@link Node} for this {@link Edge}.
	 */
	public void setTarget(Node target) {
		this.target = target;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Edge {");
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
		sb.append("} from " + getSource() + " to " + getTarget());
		return sb.toString();
	}

}

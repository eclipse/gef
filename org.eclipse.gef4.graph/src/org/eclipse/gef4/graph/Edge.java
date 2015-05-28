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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.gef4.common.notify.IMapObserver;
import org.eclipse.gef4.common.notify.ObservableMap;
import org.eclipse.gef4.common.properties.IPropertyChangeNotifier;

import com.sun.javafx.fxml.PropertyChangeEvent;

/**
 * An {@link Edge} represents a (directed) connection between two {@link Node}s
 * in a {@link Graph}.
 *
 * @author Fabian Steeg
 *
 */
public final class Edge implements IPropertyChangeNotifier {

	/*
	 * TODO: How to check consistency? The associated graph has to be an
	 * ancestor of both source and target nodes.
	 */

	/**
	 * The {@link Builder} can be used to construct an {@link Edge} little by
	 * little.
	 */
	public static class Builder {

		private Map<String, Object> attrs = new HashMap<String, Object>();
		private Node source;
		private Node target;

		/**
		 * Constructs a new {@link Builder} for an {@link Edge} which connects
		 * the given <i>source</i> {@link Node} with the given <i>target</i>
		 * {@link Node}.
		 *
		 * @param source
		 *            The source {@link Node} for the {@link Edge} which is
		 *            constructed by this {@link Builder}.
		 * @param target
		 *            The target {@link Node} for the {@link Edge} which is
		 *            constructed by this {@link Builder}.
		 */
		public Builder(Node source, Node target) {
			this.source = source;
			this.target = target;
		}

		/**
		 * Puts the given <i>key</i>-<i>value</i>-pair into the
		 * {@link Edge#getAttrs() attributes map} of the {@link Edge} which is
		 * constructed by this {@link Builder}.
		 *
		 * @param key
		 *            The attribute name which is inserted.
		 * @param value
		 *            The attribute value which is inserted.
		 * @return <code>this</code> for convenience.
		 */
		public Edge.Builder attr(String key, Object value) {
			attrs.put(key, value);
			return this;
		}

		/**
		 * Constructs a new {@link Edge} from the values which have been
		 * supplied to this {@link Builder}.
		 *
		 * @return A new {@link Edge} from the values which have been supplied
		 *         to this {@link Builder}.
		 */
		public Edge build() {
			return new Edge(attrs, source, target);
		}

	}

	/**
	 * The property name that is used to notify change listeners about changes
	 * made to the attributes of this Edge. A property change event for this
	 * property will have its old value set to a
	 * <code>Map&lt;String, Object&gt;</code> holding the old attributes and its
	 * new value set to a <code>Map&lt;String, Object&gt;</code> holding the new
	 * attributes.
	 */
	public static final String ATTRIBUTES_PROPERTY = "attributes";

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private IMapObserver<String, Object> attributesObserver = new IMapObserver<String, Object>() {
		@Override
		public void afterChange(ObservableMap<String, Object> observableMap,
				Map<String, Object> previousMap) {
			pcs.firePropertyChange(ATTRIBUTES_PROPERTY, previousMap,
					observableMap);
		}
	};

	private final ObservableMap<String, Object> attrs = new ObservableMap<String, Object>();
	private Node source;
	private Node target;
	private Graph graph; // associated graph

	/**
	 * Constructs a new {@link Edge} which connects the given <i>source</i>
	 * {@link Node} with the given <i>target</i> {@link Node}. The given
	 * <i>attributes</i> are copied into the {@link #getAttrs() attributes map}
	 * of this {@link Edge}.
	 *
	 * @param attrs
	 *            A {@link Map} containing the attributes which are copied into
	 *            the {@link #getAttrs() attributes map} of this {@link Edge}.
	 * @param source
	 *            The source {@link Node} for this {@link Edge}.
	 * @param target
	 *            The target {@link Node} for this {@link Edge}.
	 */
	public Edge(Map<String, Object> attrs, Node source, Node target) {
		this.attrs.putAll(attrs);
		this.attrs.addMapObserver(attributesObserver);
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
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (!(that instanceof Edge)) {
			return false;
		}
		Edge thatEdge = (Edge) that;
		boolean attrsEqual = this.getAttrs().equals(thatEdge.getAttrs());
		boolean sourceEqual = this.getSource().equals(thatEdge.getSource());
		boolean targetEqual = this.getTarget().equals(thatEdge.getTarget());
		return attrsEqual && sourceEqual && targetEqual;
	}

	/**
	 * Returns the attributes map of this {@link Edge} by reference. When this
	 * map is changed, a {@link PropertyChangeEvent} is fired for the
	 * {@link #ATTRIBUTES_PROPERTY}.
	 *
	 * @return The attributes map of this {@link Edge} by reference.
	 */
	public Map<String, Object> getAttrs() {
		return attrs;
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

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + getAttrs().hashCode();
		result = 31 * result + getSource().hashCode();
		result = 31 * result + getTarget().hashCode();
		return result;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Sets the {@link Graph} to which this {@link Edge} belongs to the given
	 * value.
	 *
	 * @param graph
	 *            The new {@link Graph} for this {@link Edge}.
	 */
	public void setGraph(Graph graph) {
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
		sb.append("} from " + getSource() + " to " + getTarget());
		return sb.toString();
	}

}

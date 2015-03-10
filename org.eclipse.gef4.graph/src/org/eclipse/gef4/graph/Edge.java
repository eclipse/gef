/*******************************************************************************
 * Copyright (c) 2013-2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 372365
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

public final class Edge implements IPropertyChangeNotifier {

	/*
	 * TODO: How to check consistency? The associated graph has to be an
	 * ancestor of both source and target nodes.
	 */

	public static class Builder {

		private Map<String, Object> attrs = new HashMap<String, Object>();
		private Node source;
		private Node target;

		public Builder(Node source, Node target) {
			this.source = source;
			this.target = target;
		}

		public Edge.Builder attr(String key, Object value) {
			attrs.put(key, value);
			return this;
		}

		public Edge build() {
			return new Edge(attrs, source, target);
		}

	}

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * The property name that is used to notify change listeners about changes
	 * made to the attributes of this Edge. A property change event for this
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

	private final ObservableMap<String, Object> attrs = new ObservableMap<String, Object>();
	private Node source;
	private Node target;
	private Graph graph; // associated graph

	public Edge(Map<String, Object> attrs, Node source, Node target) {
		this.attrs.putAll(attrs);
		this.attrs.addMapObserver(attributesObserver);
		this.source = source;
		this.target = target;
	}

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

	public Map<String, Object> getAttrs() {
		return attrs;
	}

	public Graph getGraph() {
		return graph;
	}

	public Node getSource() {
		return source;
	}

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

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public void setSource(Node source) {
		this.source = source;
	}

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

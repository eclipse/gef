/*******************************************************************************
 * Copyright (c) 2013-2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 372365
 *******************************************************************************/
package org.eclipse.gef4.graph;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.graph.Graph.Attr;

public final class Edge {

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

		public Builder attr(Attr.Key attr, Object value) {
			return attr(attr.toString(), value);
		}

		public Edge.Builder attr(String key, Object value) {
			attrs.put(key, value);
			return this;
		}

		public Edge build() {
			return new Edge(attrs, source, target);
		}

	}

	private final Map<String, Object> attrs;
	private Node source;
	private Node target;
	private Graph graph; // associated graph

	public Edge(Map<String, Object> attrs, Node source, Node target) {
		this.attrs = attrs;
		this.source = source;
		this.target = target;
	}

	public Edge(Node source, Node target) {
		this(new HashMap<String, Object>(), source, target);
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
		return String.format("Edge {%s attrs} from %s to %s",//$NON-NLS-1$
				getAttrs().size(), getSource(), getTarget());
	}

}

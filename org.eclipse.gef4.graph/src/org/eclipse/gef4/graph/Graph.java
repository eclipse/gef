/*******************************************************************************
 * Copyright (c) 2013-2014 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 372365
 *******************************************************************************/
package org.eclipse.gef4.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Graph {

	public static class Builder {
		
		private List<Node> nodes = new ArrayList<Node>();
		private List<Edge> edges = new ArrayList<Edge>();
		private Map<String, Object> attrs = new HashMap<String, Object>();

		public Builder() {
		}

		public Graph.Builder nodes(Node... nodes) {
			this.nodes.addAll(Arrays.asList(nodes));
			return this;
		}

		public Graph.Builder edges(Edge... edges) {
			this.edges.addAll(Arrays.asList(edges));
			return this;
		}

		public Graph.Builder attr(String key, Object value) {
			attrs.put(key, value);
			return this;
		}

		public Builder attr(Attr.Key attr, Object value) {
			return attr(attr.toString(), value);
		}

		public Graph build() {
			return new Graph(attrs, nodes, edges);
		}

	}

	private final List<Node> nodes;
	private final List<Edge> edges;
	private final Map<String, Object> attrs;

	public static class Attr {
		public static enum Key {
			NODE_STYLE, EDGE_STYLE, LABEL, STYLE, ID, IMAGE, LAYOUT, GRAPH_TYPE
		}

		public static enum Value {
			LINE_DASH, LINE_DOT, LINE_SOLID, LINE_DASHDOT, LINE_DASHDOTDOT, //
			GRAPH_DIRECTED, GRAPH_UNDIRECTED, CONNECTIONS_DIRECTED, NONE
		}
	}

	public Graph(Map<String, Object> attrs, List<Node> nodes, List<Edge> edges) {
		this.attrs = attrs;
		this.nodes = nodes;
		this.edges = edges;
	}
	
	/**
	 * Default constructor, using empty collections for attributes, nodes, and edges.
	 */
	public Graph() {
		this(new HashMap<String, Object>(), new ArrayList<Node>(), new ArrayList<Edge>());
	}

	public Map<String, Object> getAttrs() {
		return attrs;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public List<Node> getNodes() {
		return nodes;
	}
	
	@Override
	public String toString() {
		return String.format("Graph {%s nodes, %s edges}", nodes.size(), //$NON-NLS-1$
				edges.size());
	}

	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;
		if (!(that instanceof Graph))
			return false;
		Graph thatGraph = (Graph) that;
		boolean attrsEqual = this.getAttrs().equals(thatGraph.getAttrs());
		boolean nodesEqual = this.getNodes().equals(thatGraph.getNodes());
		boolean edgesEqual = this.getEdges().equals(thatGraph.getEdges());
		return attrsEqual && nodesEqual && edgesEqual;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + getAttrs().hashCode();
		result = 31 * result + getNodes().hashCode();
		result = 31 * result + getEdges().hashCode();
		return result;
	}
	
}

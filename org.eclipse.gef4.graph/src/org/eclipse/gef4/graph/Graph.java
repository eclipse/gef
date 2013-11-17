/*******************************************************************************
 * Copyright (c) 2013 Fabian Steeg. All rights reserved. This program and
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

import org.eclipse.gef4.graph.internal.dot.DotImport;
import org.eclipse.gef4.graph.internal.dot.GraphCreatorInterpreter;
import org.eclipse.gef4.graph.internal.dot.export.DotExport;

public class Graph {

	private List<Node> nodes = new ArrayList<Node>();
	private List<Edge> edges = new ArrayList<Edge>();
	private Map<String, Object> attr = new HashMap<String, Object>();

	public static enum Attr {
		NODE_STYLE, EDGE_STYLE, LABEL, STYLE, DATA, IMAGE, LAYOUT
	}

	public Graph() {
	}

	public Graph(String dot) {
		new GraphCreatorInterpreter().create(new DotImport(dot).getDotAst(),
				this);
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public Graph withNodes(Node... nodes) {
		this.nodes.addAll(Arrays.asList(nodes));
		return this;
	}

	public Graph withEdges(Edge... edges) {
		this.edges.addAll(Arrays.asList(edges));
		return this;
	}

	public Object getAttribute(String key) {
		return attr.get(key);
	}

	public Graph withAttribute(String key, Object value) {
		attr.put(key, value);
		return this;
	}

	public Graph withDot(String dot) {
		new DotImport(dot).into(this);
		return this;
	}

	public String toDot() {
		return new DotExport(this).toDotString();
	}
}

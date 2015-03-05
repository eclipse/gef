/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.dot.examples;

import org.eclipse.gef4.dot.DotExport;
import org.eclipse.gef4.dot.DotProperties;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;

public final class DotExportExample {

	public static void main(final String[] args) {
		/* Set up a directed graph with a single connection: */
		Graph.Builder graph = new Graph.Builder();
		Node node1 = new Node.Builder().attr(DotProperties.NODE_ID, "1")
				.attr(DotProperties.NODE_LABEL, "Node 1").build();
		Node node2 = new Node.Builder().attr(DotProperties.NODE_ID, "2")
				.attr(DotProperties.NODE_LABEL, "Node 2").build();
		Edge edge = new Edge.Builder(node1, node2)
		.attr(DotProperties.EDGE_LABEL, "A dotted edge")
		.attr(DotProperties.EDGE_STYLE, DotProperties.EDGE_STYLE_DOTTED)
		.build();
		graph.attr(DotProperties.GRAPH_TYPE, DotProperties.GRAPH_TYPE_DIRECTED)
				.nodes(node1, node2).edges(edge);

		/* Export the graph to a DOT string or a DOT file: */
		DotExport dotExport = new DotExport(graph.build());
		System.out.println(dotExport.toDotString());
	}

}

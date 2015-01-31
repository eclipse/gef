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
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr;
import org.eclipse.gef4.graph.Node;

public final class DotExportExample {

	public static void main(final String[] args) {
		/* Set up a directed graph with a single connection: */
		Graph.Builder graph = new Graph.Builder();
		Node node1 = new Node.Builder().attr(Attr.Key.ID.toString(), "1")
				.attr(Attr.Key.LABEL.toString(), "Node 1").build();
		Node node2 = new Node.Builder().attr(Attr.Key.ID.toString(), "2")
				.attr(Attr.Key.LABEL.toString(), "Node 2").build();
		Edge edge = new Edge.Builder(node1, node2)
		.attr(Attr.Key.LABEL.toString(), "A dotted edge")
		.attr(Attr.Key.EDGE_STYLE.toString(), Graph.Attr.Value.LINE_DOT)
		.build();
		graph.attr(Graph.Attr.Key.GRAPH_TYPE.toString(),
				Graph.Attr.Value.GRAPH_DIRECTED).nodes(node1, node2)
				.edges(edge);

		/* Export the graph to a DOT string or a DOT file: */
		DotExport dotExport = new DotExport(graph.build());
		System.out.println(dotExport.toDotString());
	}

}

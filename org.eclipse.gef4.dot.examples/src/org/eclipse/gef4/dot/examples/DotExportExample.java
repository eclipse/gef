/*******************************************************************************
 * Copyright (c) 2009, 2015 Fabian Steeg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *******************************************************************************/
package org.eclipse.gef4.dot.examples;

import org.eclipse.gef4.dot.DotExport;
import org.eclipse.gef4.dot.DotProperties;
import org.eclipse.gef4.graph.Graph;

public final class DotExportExample {

	public static void main(final String[] args) {
		/* Set up a directed graph with a single connection: */
		Graph graph = new Graph.Builder()
				.attr(DotProperties.GRAPH_TYPE,
						DotProperties.GRAPH_TYPE_DIRECTED)
				.node("n1").attr(DotProperties.NODE_ID, "1")
				.attr(DotProperties.NODE_LABEL, "Node 1")//
				.node("n2").attr(DotProperties.NODE_ID, "2")
				.attr(DotProperties.NODE_LABEL, "Node 2")//
				.edge("n1", "n2")
				.attr(DotProperties.EDGE_LABEL, "A dotted edge")
				.attr(DotProperties.EDGE_STYLE, DotProperties.EDGE_STYLE_DOTTED)
				.build();

		/* Export the graph to a DOT string or a DOT file: */
		DotExport dotExport = new DotExport(graph);
		System.out.println(dotExport.toDotString());
	}

}

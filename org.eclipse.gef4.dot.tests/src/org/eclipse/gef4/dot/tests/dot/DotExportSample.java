/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.dot.tests.dot;

import java.io.File;

import org.eclipse.gef4.dot.Edge;
import org.eclipse.gef4.dot.Graph;
import org.eclipse.gef4.dot.Node;
import org.eclipse.gef4.dot.Graph.Attr;
import org.eclipse.gef4.dot.internal.dot.ZestStyle;
import org.eclipse.gef4.dot.internal.dot.export.DotExport;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;

/**
 * Zest graph sample input for the Zest-To-Dot transformation. Contains
 * everything that is currently supported by the transformation: node and edge
 * labels, edge styles.
 * <p/>
 * Uses the actual Zest Graph class and populates an instance of that, instead
 * of subclassing the Zest Graph and exporting the subclass (as in the samples
 * used for testing, which are based on Graphs generated using the
 * org.eclipse.gef4.zest.dot.import bundle).
 */
public final class DotExportSample {
	public static void main(final String[] args) {
		/* Set up a directed Zest graph with a single connection: */
		Graph.Builder graph = new Graph.Builder();
		Node node1 = new Node.Builder().attr(Attr.LABEL, "Node 1").build();
		Node node2 = new Node.Builder().attr(Attr.LABEL, "Node 2").build();
		Edge edge = new Edge.Builder(node1, node2)
				.attr(Attr.LABEL, "A dotted edge")
				.attr(Attr.EDGE_STYLE, ZestStyle.LINE_DOT).build();
		graph.attr(Graph.Attr.EDGE_STYLE, ZestStyle.CONNECTIONS_DIRECTED)
				.edges(edge);
		/* Export the Zest graph to a DOT string or a DOT file: */
		DotExport dotExport = new DotExport(graph.build());
		System.out.println(dotExport.toDotString());
		dotExport.toDotFile(new File("src-gen/DirectSample.dot")); //$NON-NLS-1$
		/* Show the Zest graph: */
		graph.attr(Graph.Attr.LAYOUT, new TreeLayoutAlgorithm());
	}

	private DotExportSample() { /* enforce non-instantiability */
	}
}

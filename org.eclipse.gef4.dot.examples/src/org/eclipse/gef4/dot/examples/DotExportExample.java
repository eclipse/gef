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

import org.eclipse.gef4.dot.internal.DotAttributes;
import org.eclipse.gef4.dot.internal.DotExport;
import org.eclipse.gef4.graph.Graph;

public final class DotExportExample {

	public static void main(final String[] args) {
		/* Set up a directed graph with a single connection: */
		Graph graph = new Graph.Builder()
				.attr(DotAttributes._TYPE__G, DotAttributes._TYPE__G__GRAPH)
				.node("n1").attr(DotAttributes._NAME__GNE, "1")
				.attr(DotAttributes.LABEL__GNE, "Node 1")//
				.node("n2").attr(DotAttributes._NAME__GNE, "2")
				.attr(DotAttributes.LABEL__GNE, "Node 2")//
				.edge("n1", "n2")
				.attr(DotAttributes.LABEL__GNE, "A dotted edge")
				.attr(DotAttributes.STYLE__E, DotAttributes.STYLE__E__DOTTED)
				.build();

		/* Export the graph to a DOT string or a DOT file: */
		System.out.println(new DotExport().exportDot(graph));
	}

}

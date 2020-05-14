/*******************************************************************************
 * Copyright (c) 2009, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *     Tamas Miklossy (itemis AG) - Add support for all dot attributes (bug #461506)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.examples;

import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.DotExport;
import org.eclipse.gef.dot.internal.language.dot.GraphType;
import org.eclipse.gef.dot.internal.language.style.EdgeStyle;
import org.eclipse.gef.graph.Graph;

public final class DotExportExample {

	public static void main(final String[] args) {
		/* Set up a directed graph with a single connection */
		Graph graph = new Graph.Builder()
				.attr(DotAttributes::_setType, GraphType.DIGRAPH)//
				.node("n1")//
				.attr(DotAttributes::_setName, "1")
				.attr(DotAttributes::setLabel, "Node 1")//
				.node("n2")//
				.attr(DotAttributes::_setName, "2")
				.attr(DotAttributes::setLabel, "Node 2")//
				.edge("n1", "n2")//
				.attr(DotAttributes::setLabel, "A dotted edge")//
				.attr(DotAttributes::setStyle, EdgeStyle.DOTTED.toString())//
				.build();

		/* Export the graph to a DOT string */
		System.out.println(new DotExport().exportDot(graph));
	}

}

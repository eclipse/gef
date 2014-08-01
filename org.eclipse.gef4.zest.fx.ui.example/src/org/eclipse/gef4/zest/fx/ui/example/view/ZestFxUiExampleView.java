/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.zest.fx.ui.example.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr.Key;
import org.eclipse.gef4.zest.fx.example.ZestFxExampleModule;
import org.eclipse.gef4.zest.fx.parts.NodeContentPart;
import org.eclipse.gef4.zest.fx.ui.example.ZestFxUiExampleModule;
import org.eclipse.gef4.zest.fx.ui.view.ZestFxUiView;

import com.google.inject.Guice;
import com.google.inject.util.Modules;

public class ZestFxUiExampleView extends ZestFxUiView {

	public static Graph DEFAULT_GRAPH = build09();

	private static Graph build09() {
		// create nodes "0" to "9"
		List<org.eclipse.gef4.graph.Node> nodes = new ArrayList<org.eclipse.gef4.graph.Node>();
		nodes.addAll(Arrays.asList(n("0"), n("1"), n("2"), n("3"), n("4"),
				n("5"), n("6"), n("7", "custom"), n("8", "custom"),
				n("9", "custom")));

		// create some edges between those nodes
		List<Edge> edges = new ArrayList<Edge>();
		edges.addAll(Arrays.asList(e(nodes.get(0), nodes.get(9)),
				e(nodes.get(1), nodes.get(8)), e(nodes.get(2), nodes.get(7)),
				e(nodes.get(3), nodes.get(6)), e(nodes.get(4), nodes.get(5)),
				e(nodes.get(0), nodes.get(4)), e(nodes.get(1), nodes.get(6)),
				e(nodes.get(2), nodes.get(8)), e(nodes.get(3), nodes.get(5)),
				e(nodes.get(4), nodes.get(7)), e(nodes.get(5), nodes.get(1))));

		// default: directed connections
		HashMap<String, Object> attrs = new HashMap<String, Object>();
		attrs.put(Graph.Attr.Key.GRAPH_TYPE.toString(),
				Graph.Attr.Value.GRAPH_DIRECTED);
		return new Graph(attrs, nodes, edges);
	}

	private static Edge e(org.eclipse.gef4.graph.Node n,
			org.eclipse.gef4.graph.Node m) {
		String label = (String) n.getAttrs().get(Key.LABEL.toString())
				+ (String) m.getAttrs().get(Key.LABEL.toString());
		return new Edge.Builder(n, m).attr(Key.LABEL, label).build();
	}

	private static org.eclipse.gef4.graph.Node n(String label) {
		return new org.eclipse.gef4.graph.Node.Builder().attr(Key.LABEL, label)
				.build();
	}

	private static org.eclipse.gef4.graph.Node n(String label, String cssClass) {
		return new org.eclipse.gef4.graph.Node.Builder().attr(Key.LABEL, label)
				.attr(NodeContentPart.ATTR_CLASS, cssClass).build();
	}

	public ZestFxUiExampleView() {
		super(Guice.createInjector(Modules.override(new ZestFxExampleModule())
				.with(new ZestFxUiExampleModule())));
		setGraph(DEFAULT_GRAPH);
	}

}

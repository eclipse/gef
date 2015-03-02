/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef4.zest.examples.layout.FilterGraphSnippet
 *
 *******************************************************************************/
package org.eclipse.gef4.layout.examples;

import javafx.application.Application;

import org.eclipse.gef4.fx.nodes.ScrollPaneEx;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.examples.AbstractZestExample;
import org.eclipse.gef4.zest.fx.parts.GraphContentPart;

public class FilterGraphExample extends AbstractZestExample {

	public static void main(String[] args) {
		Application.launch(args);
	}

	public FilterGraphExample() {
		super("GEF4 Layouts - Filter Graph Example");
	}

	@Override
	protected Graph createGraph() {
		Graph graph = new Graph();
		graph.getAttrs().put(Graph.Attr.Key.GRAPH_TYPE.toString(),
				Graph.Attr.Value.GRAPH_DIRECTED);

		Node a = n(graph, LABEL, "Root");
		Node b = n(graph, LABEL, "B");
		Node c = n(graph, LABEL, "C");
		Node d = n(graph, LABEL, "D");
		Node e = n(graph, LABEL, "E");
		Node f = n(graph, LABEL, "F");
		Node g = n(graph, LABEL, "G");
		Node h = n(graph, LABEL, "H");

		e(graph, a, b, LABEL, "", GraphContentPart.ATTR_LAYOUT_IRRELEVANT,
				Boolean.TRUE, "class", "red");
		e(graph, a, c, LABEL, "", GraphContentPart.ATTR_LAYOUT_IRRELEVANT,
				Boolean.TRUE, "class", "red");
		e(graph, a, d, LABEL, "", GraphContentPart.ATTR_LAYOUT_IRRELEVANT,
				Boolean.TRUE, "class", "red");

		e(graph, b, e, LABEL, "");
		e(graph, b, f, LABEL, "");
		e(graph, c, g, LABEL, "");
		e(graph, d, h, LABEL, "");

		e(graph, b, c, LABEL, "", GraphContentPart.ATTR_LAYOUT_IRRELEVANT,
				Boolean.TRUE, "class", "red");
		e(graph, c, d, LABEL, "", GraphContentPart.ATTR_LAYOUT_IRRELEVANT,
				Boolean.TRUE, "class", "red");
		e(graph, e, f, LABEL, "", GraphContentPart.ATTR_LAYOUT_IRRELEVANT,
				Boolean.TRUE, "class", "red");
		e(graph, f, g, LABEL, "", GraphContentPart.ATTR_LAYOUT_IRRELEVANT,
				Boolean.TRUE, "class", "red");
		e(graph, h, e, LABEL, "", GraphContentPart.ATTR_LAYOUT_IRRELEVANT,
				Boolean.TRUE, "class", "red");

		return graph;
	}

	@Override
	protected LayoutAlgorithm createLayoutAlgorithm() {
		return new TreeLayoutAlgorithm(TreeLayoutAlgorithm.TOP_DOWN);
	}

	@Override
	protected void customizeUi(ScrollPaneEx scrollPane) {
		super.customizeUi(scrollPane);
		// TODO: Find it via bundle in OSGI context.
		scrollPane.getStylesheets().add(
				getClass().getResource("FilterGraphExample.css")
						.toExternalForm());
	}

	@Override
	protected int getStageHeight() {
		return 400;
	}

	@Override
	protected int getStageWidth() {
		return 400;
	}

}

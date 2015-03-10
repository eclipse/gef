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
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.examples.AbstractZestExample;
import org.eclipse.gef4.zest.fx.ZestProperties;

public class FilterLayoutExample extends AbstractZestExample {

	public static void main(String[] args) {
		Application.launch(args);
	}

	public FilterLayoutExample() {
		super("GEF4 Layouts - Filter Layout Example");
	}

	@Override
	protected Graph createGraph() {
		Graph graph = new Graph();
		ZestProperties.setType(graph, ZestProperties.GRAPH_TYPE_DIRECTED);

		Node a = n(graph, LABEL, "Root");
		Node b = n(graph, LABEL, "B");
		Node c = n(graph, LABEL, "C");
		Node d = n(graph, LABEL, "D");
		Node e = n(graph, LABEL, "E");
		Node f = n(graph, LABEL, "F");
		Node g = n(graph, LABEL, "G");
		Node h = n(graph, LABEL, "H");

		e(graph, a, b, LABEL, "", LAYOUT_IRRELEVANT, Boolean.TRUE, CSS_CLASS,
				"red");
		e(graph, a, c, LABEL, "", LAYOUT_IRRELEVANT, Boolean.TRUE, CSS_CLASS,
				"red");
		e(graph, a, d, LABEL, "", LAYOUT_IRRELEVANT, Boolean.TRUE, CSS_CLASS,
				"red");

		e(graph, b, e, LABEL, "");
		e(graph, b, f, LABEL, "");
		e(graph, c, g, LABEL, "");
		e(graph, d, h, LABEL, "");

		e(graph, b, c, LABEL, "", LAYOUT_IRRELEVANT, Boolean.TRUE, CSS_CLASS,
				"red");
		e(graph, c, d, LABEL, "", LAYOUT_IRRELEVANT, Boolean.TRUE, CSS_CLASS,
				"red");
		e(graph, e, f, LABEL, "", LAYOUT_IRRELEVANT, Boolean.TRUE, CSS_CLASS,
				"red");
		e(graph, f, g, LABEL, "", LAYOUT_IRRELEVANT, Boolean.TRUE, CSS_CLASS,
				"red");
		e(graph, h, e, LABEL, "", LAYOUT_IRRELEVANT, Boolean.TRUE, CSS_CLASS,
				"red");

		graph.getAttrs().put(ZestProperties.GRAPH_LAYOUT,
				new TreeLayoutAlgorithm(TreeLayoutAlgorithm.TOP_DOWN));

		return graph;
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

/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef.zest.examples.layout.FilterGraphSnippet
 *
 *******************************************************************************/
package org.eclipse.gef.layout.examples;

import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.gef.zest.examples.AbstractZestExample;
import org.eclipse.gef.zest.fx.ZestProperties;

import javafx.application.Application;
import javafx.scene.Scene;

public class FilterLayoutExample extends AbstractZestExample {

	public static void main(String[] args) {
		Application.launch(args);
	}

	public FilterLayoutExample() {
		super("GEF Layouts - Filter Layout Example");
	}

	@Override
	protected Graph createGraph() {
		Graph graph = new Graph();

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

		graph.attributesProperty().put(ZestProperties.LAYOUT_ALGORITHM__G,
				new TreeLayoutAlgorithm(TreeLayoutAlgorithm.TOP_DOWN));

		return graph;
	}

	@Override
	protected Scene createScene(IViewer viewer) {
		Scene scene = super.createScene(viewer);
		scene.getStylesheets().add(getClass()
				.getResource("FilterGraphExample.css").toExternalForm());
		return scene;
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

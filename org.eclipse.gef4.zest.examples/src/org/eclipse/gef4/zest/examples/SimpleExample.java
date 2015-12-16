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
 * Note: Parts of this class have been transferred from org.eclipse.gef4.zest.examples.layout.SimpleGraphSnippet
 *
 *******************************************************************************/
package org.eclipse.gef4.zest.examples;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.fx.ZestProperties;

import javafx.application.Application;

public class SimpleExample extends AbstractZestExample {

	public static void main(String[] args) {
		Application.launch(args);
	}

	public SimpleExample() {
		super("GEF4 Zest - Simple Example");
	}

	@Override
	protected Graph createGraph() {
		Node n = n(LABEL, "Paper");
		Node n2 = n(LABEL, "Rock");
		Node n3 = n(LABEL, "Scissors");
		Edge e12 = new Edge(n, n2);
		Edge e23 = new Edge(n2, n3);
		Edge e31 = new Edge(n3, n);
		return new Graph.Builder().nodes(n, n2, n3).edges(e12, e23, e31)
				.attr(ZestProperties.GRAPH_LAYOUT_ALGORITHM, new SpringLayoutAlgorithm())
				.build();
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

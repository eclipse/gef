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
package org.eclipse.gef4.layout.examples;

import javafx.application.Application;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.zest.examples.AbstractZestExample;

public class SimpleLayoutExample extends AbstractZestExample {

	public static void main(String[] args) {
		Application.launch(args);
	}

	public SimpleLayoutExample() {
		super("GEF4 Layouts - Simple Layout Example");
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
				.build();
	}

	@Override
	protected LayoutAlgorithm createLayoutAlgorithm() {
		return new SpringLayoutAlgorithm();
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

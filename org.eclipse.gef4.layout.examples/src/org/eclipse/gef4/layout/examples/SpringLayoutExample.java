/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 * Note: Parts of this class have been transferred from org.eclipse.gef4.zest.examples.layout.RadialLayoutExample
 *
 *******************************************************************************/
package org.eclipse.gef4.layout.examples;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr.Key;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;

public class SpringLayoutExample extends AbstractLayoutExample {

	public static void main(String[] args) {
		Application.launch(args);
	}

	public SpringLayoutExample() {
		super("GEF4 Layouts - Spring Layout Example");
	}

	@Override
	protected Graph createGraph() {
		// create nodes
		List<Node> nodes = new ArrayList<>();
		List<Edge> edges = new ArrayList<>();

		Node root = n(Key.LABEL.toString(), "Root");
		nodes.add(root);
		for (int i = 0; i < 3; i++) {
			Node n = n(Key.LABEL.toString(), "1 - " + i);
			nodes.add(n);
			for (int j = 0; j < 3; j++) {
				// make these nodes differ via their ids (as the labels are
				// identical)
				Node n2 = n(Key.ID.toString(), i + "-" + j,
						Key.LABEL.toString(), "2 - " + j);
				nodes.add(n2);
				Edge e = e(n, n2);
				edges.add(e);
			}
			edges.add(e(root, n));
		}
		return new Graph.Builder().nodes(nodes.toArray(new Node[] {}))
				.edges(edges.toArray(new Edge[] {})).build();

	}

	@Override
	protected LayoutAlgorithm createLayoutAlgorithm() {
		return new SpringLayoutAlgorithm();
	}

}

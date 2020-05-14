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
 *     Alexander Nyßen (itemis AG) - initial API & implementation
 *
 *******************************************************************************/
package org.eclipse.gef.layout.examples;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.algorithms.SpaceTreeLayoutAlgorithm;
import org.eclipse.gef.zest.examples.AbstractZestExample;
import org.eclipse.gef.zest.fx.ZestProperties;

import javafx.application.Application;

public class SpaceTreeLayoutExample extends AbstractZestExample {

	public static void main(String[] args) {
		Application.launch(args);
	}

	public SpaceTreeLayoutExample() {
		super("GEF Layouts - Space Tree Layout Example");
	}

	@Override
	protected Graph createGraph() {
		// create nodes
		List<Node> nodes = new ArrayList<>();
		List<Edge> edges = new ArrayList<>();

		Node root = n(LABEL, "Root");
		nodes.add(root);
		for (int i = 0; i < 3; i++) {
			Node n = n(LABEL, "1 - " + i);
			nodes.add(n);
			for (int j = 0; j < 3; j++) {
				// make these nodes differ via their ids (as the labels are
				// identical)
				Node n2 = n(ID, i + "-" + j, LABEL, "2 - " + j);
				nodes.add(n2);
				Edge e = e(n, n2);
				edges.add(e);
			}
			edges.add(e(root, n));
		}
		return new Graph.Builder().nodes(nodes.toArray(new Node[] {}))
				.edges(edges.toArray(new Edge[] {}))
				.attr(ZestProperties.LAYOUT_ALGORITHM__G,
						new SpaceTreeLayoutAlgorithm(
								SpaceTreeLayoutAlgorithm.LEFT_RIGHT))
				.build();

	}

}

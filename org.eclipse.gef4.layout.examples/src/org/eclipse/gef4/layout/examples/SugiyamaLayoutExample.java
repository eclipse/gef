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
 *  Note: Parts of this class have been transferred from org.eclipse.gef4.zest.examples.layout.SugiyamaLayoutExample
 *
 *******************************************************************************/
package org.eclipse.gef4.layout.examples;

import javafx.application.Application;

import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Graph.Attr.Key;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.SugiyamaLayoutAlgorithm;
import org.eclipse.gef4.zest.examples.AbstractZestExample;

public class SugiyamaLayoutExample extends AbstractZestExample {

	public static void main(String[] args) {
		Application.launch(args);
	}

	public SugiyamaLayoutExample() {
		super("GEF4 Layouts - Radial Layout Example");
	}

	@Override
	protected Graph createGraph() {
		// create nodes
		org.eclipse.gef4.graph.Node[] nodes = new org.eclipse.gef4.graph.Node[] {
				n(Key.LABEL.toString(), "Coal"),
				n(Key.LABEL.toString(), "Ore"),
				n(Key.LABEL.toString(), "Stone"),
				n(Key.LABEL.toString(), "Metal"),
				n(Key.LABEL.toString(), "Concrete"),
				n(Key.LABEL.toString(), "Machine"),
				n(Key.LABEL.toString(), "Building") };

		// create edges
		org.eclipse.gef4.graph.Edge[] edges = new org.eclipse.gef4.graph.Edge[] {
				e(nodes[0], nodes[3]) /* coal -> metal */,
				e(nodes[0], nodes[4]) /* coal -> concrete */,
				e(nodes[3], nodes[5]) /* metal -> machine */,
				e(nodes[3], nodes[6]) /* metal -> building */,
				e(nodes[4], nodes[6]) /* concrete -> building */,
				e(nodes[1], nodes[3]) /* ore -> metal */, e(nodes[2], nodes[4]) /*
																			 * stone
																			 * ->
																			 * concrete
																			 */};

		return new Graph.Builder()
				.attr(Graph.Attr.Key.GRAPH_TYPE.toString(),
						Graph.Attr.Value.GRAPH_DIRECTED).nodes(nodes)
				.edges(edges).build();
	}

	@Override
	protected LayoutAlgorithm createLayoutAlgorithm() {
		return new SugiyamaLayoutAlgorithm();
	}

}

/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
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
 *  Note: Parts of this class have been transferred from org.eclipse.gef.zest.examples.layout.SugiyamaLayoutExample
 *
 *******************************************************************************/
package org.eclipse.gef.layout.examples;

import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.layout.algorithms.SugiyamaLayoutAlgorithm;
import org.eclipse.gef.zest.examples.AbstractZestExample;
import org.eclipse.gef.zest.fx.ZestProperties;

import javafx.application.Application;

public class SugiyamaLayoutExample extends AbstractZestExample {

	public static void main(String[] args) {
		Application.launch(args);
	}

	public SugiyamaLayoutExample() {
		super("GEF Layouts - Sugiyama Layout Example");
	}

	@Override
	protected Graph createGraph() {
		// create nodes
		org.eclipse.gef.graph.Node[] nodes = new org.eclipse.gef.graph.Node[] {
				n(LABEL, "Coal"), n(LABEL, "Ore"), n(LABEL, "Stone"),
				n(LABEL, "Metal"), n(LABEL, "Concrete"), n(LABEL, "Machine"),
				n(LABEL, "Building") };

		// create edges
		org.eclipse.gef.graph.Edge[] edges = new org.eclipse.gef.graph.Edge[] {
				e(nodes[0], nodes[3]) /* coal -> metal */,
				e(nodes[0], nodes[4]) /* coal -> concrete */,
				e(nodes[3], nodes[5]) /* metal -> machine */,
				e(nodes[3], nodes[6]) /* metal -> building */,
				e(nodes[4], nodes[6]) /* concrete -> building */,
				e(nodes[1], nodes[3]) /* ore -> metal */,
				e(nodes[2], nodes[4]) /* stone -> concrete */ };

		return new Graph.Builder().nodes(nodes).edges(edges)
				.attr(ZestProperties.LAYOUT_ALGORITHM__G,
						new SugiyamaLayoutAlgorithm())
				.build();
	}

}

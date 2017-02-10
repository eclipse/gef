/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.GraphCopier;
import org.eclipse.gef.graph.Node;

public class Dot2ZestGraphCopier extends GraphCopier {

	public Dot2ZestGraphCopier() {
		super(new Dot2ZestAttributesConverter());
	}

	@Override
	public Dot2ZestAttributesConverter getAttributeCopier() {
		return (Dot2ZestAttributesConverter) super.getAttributeCopier();
	}

	public Graph copy(Graph graph) {
		Graph copiedGraph = super.copy(graph);
		// post-process graph to 'flatten' non-cluster subgraphs; insert
		// nested nodes at index of subgraph node
		// FIXME: no longer flatten cluster subgraphs as soon as they can be
		// properly rendered
		List<org.eclipse.gef.graph.Node> replacementNodes = new ArrayList<>();
		for (Node topLevelNode : copiedGraph.getNodes()) {
			if (topLevelNode.getNestedGraph() == null) {
				replacementNodes.add(topLevelNode);
			} else {
				Graph subgraph = topLevelNode.getNestedGraph();
				for (Node n : subgraph.getNodes()) {
					// 'unfold' all incoming and outgoing edges (they have
					// to refer to the nested nodes)
					for (Edge e : topLevelNode.getIncomingEdges()) {
						Edge edgeCopy = copyEdge(e);
						edgeCopy.setTarget(n);
						copiedGraph.getEdges().add(edgeCopy);
					}
					for (Edge e : topLevelNode.getOutgoingEdges()) {
						Edge edgeCopy = copyEdge(e);
						edgeCopy.setSource(n);
						copiedGraph.getEdges().add(edgeCopy);
					}
				}
				replacementNodes.addAll(subgraph.getNodes());
				copiedGraph.getEdges().addAll(subgraph.getEdges());
			}
		}
		copiedGraph.getNodes().setAll(replacementNodes);
		return copiedGraph;
	}
}
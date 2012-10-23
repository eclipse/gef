/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.internal.dot;

import org.eclipse.gef4.zest.core.widgets.Graph;
import org.eclipse.gef4.zest.core.widgets.GraphConnection;
import org.eclipse.gef4.zest.core.widgets.GraphNode;

/**
 * Imports the content of a Zest graph generated from DOT into an existing Zest
 * graph.
 * 
 * @author Fabian Steeg (fsteeg)
 */
final class ZestGraphImport {
	private Graph graphFromDot;

	/**
	 * @param sourceGraph
	 *            The Zest source graph to import into another graph. Note that
	 *            this will only support a subset of the graph attributes, as it
	 *            is used for import of Zest graphs created from DOT input.
	 */
	ZestGraphImport(Graph sourceGraph) {
		this.graphFromDot = sourceGraph;
	}

	/**
	 * @param targetGraph
	 *            The graph to add content to
	 */
	void into(Graph targetGraph) {
		Graph sourceGraph = graphFromDot;
		targetGraph.setNodeStyle(sourceGraph.getNodeStyle());
		targetGraph.setConnectionStyle(sourceGraph.getConnectionStyle());
		targetGraph.setLayoutAlgorithm(sourceGraph.getLayoutAlgorithm(), true);
		for (Object edge : sourceGraph.getConnections()) {
			copy((GraphConnection) edge, targetGraph);
		}
		for (Object node : sourceGraph.getNodes()) {
			copy((GraphNode) node, targetGraph);
		}
		targetGraph.update();
	}

	private GraphConnection copy(GraphConnection edge, Graph targetGraph) {
		GraphNode source = copy(edge.getSource(), targetGraph);
		GraphNode target = copy(edge.getDestination(), targetGraph);
		GraphConnection copy = new GraphConnection(targetGraph,
				edge.getStyle(), source, target);
		copy.setText(edge.getText());
		copy.setData(edge.getData());
		copy.setLineStyle(edge.getLineStyle());
		return copy;
	}

	private GraphNode copy(GraphNode node, Graph targetGraph) {
		GraphNode find = find(node, targetGraph);
		if (find == null) {
			GraphNode copy = new GraphNode(targetGraph, node.getStyle(),
					node.getText());
			copy.setImage(node.getImage());
			copy.setData(node.getData());
			return copy;
		}
		return find; // target already contains the node to copy over
	}

	private GraphNode find(GraphNode node, Graph graph) {
		for (Object o : graph.getNodes()) {
			GraphNode n = (GraphNode) o;
			if (node.getData() != null && node.getData().equals(n.getData())) {
				return n;
			}
		}
		return null;
	}
}

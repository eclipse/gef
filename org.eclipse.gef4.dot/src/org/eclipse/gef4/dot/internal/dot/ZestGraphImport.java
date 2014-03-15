/*******************************************************************************
 * Copyright (c) 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.dot.internal.dot;

import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;

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
	void into(Graph.Builder targetGraph) {
		Graph sourceGraph = graphFromDot;
		targetGraph.attr(Graph.Attr.NODE_STYLE.toString(), sourceGraph
				.getAttrs().get(Graph.Attr.NODE_STYLE.toString()));
		targetGraph.attr(Graph.Attr.EDGE_STYLE.toString(), sourceGraph
				.getAttrs().get(Graph.Attr.EDGE_STYLE.toString()));
		targetGraph.attr(Graph.Attr.LAYOUT.toString(), sourceGraph.getAttrs()
				.get(Graph.Attr.LAYOUT.toString()));
		for (Object edge : sourceGraph.getEdges()) {
			copy((Edge) edge, targetGraph);
		}
		for (Object node : sourceGraph.getNodes()) {
			copy((Node) node, targetGraph);
		}
	}

	private Edge copy(Edge edge, Graph.Builder targetGraph) {
		Node source = copy(edge.getSource(), targetGraph);
		Node target = copy(edge.getTarget(), targetGraph);
		Edge copy = new Edge.Builder(source, target)
				.attr(Graph.Attr.STYLE.toString(),
						edge.getAttrs().get(Graph.Attr.STYLE.toString()))
				.attr(Graph.Attr.LABEL.toString(),
						edge.getAttrs().get(Graph.Attr.LABEL.toString()))
				.attr(Graph.Attr.ID.toString(),
						edge.getAttrs().get(Graph.Attr.ID.toString()))
				.attr(Graph.Attr.EDGE_STYLE.toString(),
						edge.getAttrs().get(Graph.Attr.EDGE_STYLE.toString()))
				.build();
		targetGraph.edges(copy);
		return copy;
	}

	private Node copy(Node node, Graph.Builder targetGraph) {
		Node find = find(node, targetGraph.build());
		if (find == null) {
			Node copy = new Node.Builder()
					.attr(Graph.Attr.LABEL.toString(),
							node.getAttrs().get(Graph.Attr.LABEL.toString()))
					.attr(Graph.Attr.STYLE.toString(),
							node.getAttrs().get(Graph.Attr.STYLE.toString()))
					.attr(Graph.Attr.IMAGE.toString(),
							node.getAttrs().get(Graph.Attr.IMAGE.toString()))
					.attr(Graph.Attr.ID.toString(),
							node.getAttrs().get(Graph.Attr.ID.toString()))
					.build();
			targetGraph.nodes(copy);
			return copy;
		}
		return find; // target already contains the node to copy over
	}

	private Node find(Node node, Graph graph) {
		for (Object o : graph.getNodes()) {
			Node n = (Node) o;
			Object nodeData = node.getAttrs().get(Graph.Attr.ID.toString());
			if (nodeData != null
					&& nodeData.equals(n.getAttrs().get(
							Graph.Attr.ID.toString()))) {
				return n;
			}
		}
		return null;
	}
}

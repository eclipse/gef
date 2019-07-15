/*******************************************************************************
 * Copyright (c) 2017, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alexander Nyßen (itemis AG) - initial API and implementation
 *    Tamas Miklossy  (itemis AG) - minor improvements, refactoring
 *    Zoey Prigge     (itemis AG) - DotGraphView: FontName support (bug #541056)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.conversion;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.rect.Rect;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.GraphCopier;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.zest.fx.ZestProperties;

public class Dot2ZestGraphCopier extends GraphCopier {

	private List<Node> clusterNodes;

	public Dot2ZestGraphCopier() {
		this(new Dot2ZestAttributesConverter());
		clusterNodes = new LinkedList<Node>();
	}

	public Dot2ZestGraphCopier(
			Dot2ZestAttributesConverter dot2ZestAttributesConverter) {
		super(dot2ZestAttributesConverter);
	}

	@Override
	public Dot2ZestAttributesConverter getAttributeCopier() {
		return (Dot2ZestAttributesConverter) super.getAttributeCopier();
	}

	@Override
	public Graph copy(Graph dotGraph) {
		Graph zestGraph = super.copy(dotGraph);

		// post-process graph to handle nested graphs
		clusterNodes.clear();
		List<Node> zestNodeReplacements = new ArrayList<>();
		for (Node zestNode : zestGraph.getNodes()) {
			processZestNode(zestNode, zestGraph, zestNodeReplacements);
		}

		// add cluster nodes as lowest in z-order
		zestNodeReplacements.addAll(0, clusterNodes);

		return new Graph(zestGraph.attributesProperty(), zestNodeReplacements,
				zestGraph.edgesProperty());
	}

	protected void processZestNode(Node zestNode, Graph zestGraph,
			List<Node> zestNodeReplacements) {
		if (zestNode.getNestedGraph() == null) {
			zestNodeReplacements.add(zestNode);
		} else {
			processNestedGraph(zestNode, zestGraph, zestNodeReplacements);
		}
	}

	protected void processNestedGraph(Node zestNode, Graph zestGraph,
			List<Node> zestNodeReplacements) {
		// retrieve copied dot node to decide whether we have a cluster
		// (as the name of the nested graph will not be copied)
		Node dotNode = getDotNode(zestNode);

		// only support clusters in native mode
		if (DotAttributes.isCluster(dotNode)
				&& !getAttributeCopier().options().emulateLayout) {
			processCluster(dotNode, zestNode);
		}
		processSubgraph(zestNode, zestGraph, zestNodeReplacements);

		for (Node nestedNode : zestNode.getNestedGraph().getNodes()) {
			processZestNode(nestedNode, zestGraph, zestNodeReplacements);
		}

		zestNode.setNestedGraph(null);
	}

	protected void processCluster(Node dotNode, Node zestNode) {
		clusterNodes.add(zestNode);

		// initialize a rectangle shape
		ZestProperties.setShape(zestNode, new GeometryNode<>(new Rectangle()));

		// for cluster nodes position and size are determined using
		// the bounding box (specified as attribute of nested graph)
		Rect bb = DotAttributes.getBbParsed(dotNode.getNestedGraph());

		Point zestPosition = null;
		Dimension zestSize = null;
		if (bb != null) {
			zestPosition = new Point(bb.getLlx(), bb.getUry());
			zestSize = new Dimension(bb.getUrx() - bb.getLlx(),
					bb.getLly() - bb.getUry());
			ZestProperties.setPosition(zestNode, zestPosition);
			ZestProperties.setSize(zestNode, zestSize);
		}

		Dot2ZestAttributesConverter attributesCopier = getAttributeCopier();

		// cluster node style
		ZestProperties.setShapeCssStyle(zestNode,
				new DotClusterStyleUtil(attributesCopier.colorUtil, dotNode)
						.computeZestStyle().toString());

		// label fontcolor, fontsize, fontname
		String zestNodeLabelCssStyle = attributesCopier
				.computeZestGraphLabelCssStyle(dotNode.getNestedGraph());
		if (zestNodeLabelCssStyle != null) {
			ZestProperties.setExternalLabelCssStyle(zestNode,
					zestNodeLabelCssStyle);
		}

		// determine label for cluster
		// TODO: respect style settings, escape sequences and HTML labels
		String dotLabel = DotAttributes.getLabel(dotNode.getNestedGraph());
		if ("\\G".equals(dotLabel)) { //$NON-NLS-1$
			dotLabel = DotAttributes._getName(dotNode.getNestedGraph());
		}
		if (dotLabel != null) {
			ZestProperties.setExternalLabel(zestNode, dotLabel);
			// XXX: We use an external label and position it inside
			// the node, as Zest will always center the label.
			// TODO: change this as soon as different label
			// positions are supported by Zest
			if (zestPosition != null && zestSize != null) {
				double xOffset = 0.5 * (zestSize.width
						- attributesCopier.computeZestLabelSize(attributesCopier
								.dummyTextNodeWithStyle(dotLabel, null)).width);
				double yOffset = 4;
				ZestProperties.setExternalLabelPosition(zestNode,
						zestPosition.getTranslated(xOffset, yOffset));
			}
		}
	}

	protected void processSubgraph(Node zestNode, Graph zestGraph,
			List<Node> zestNodeReplacements) {
		Graph subgraph = zestNode.getNestedGraph();
		for (Node n : subgraph.getNodes()) {
			// 'unfold' all incoming and outgoing edges (they have
			// to refer to the nested nodes)
			for (Edge e : zestNode.getIncomingEdges()) {
				Edge edgeCopy = copyEdge(e);
				edgeCopy.setTarget(n);
				zestGraph.getEdges().add(edgeCopy);
			}
			for (Edge e : zestNode.getOutgoingEdges()) {
				Edge edgeCopy = copyEdge(e);
				edgeCopy.setSource(n);
				zestGraph.getEdges().add(edgeCopy);
			}
		}
		zestGraph.getEdges().addAll(subgraph.getEdges());
	}

	protected Node getDotNode(Node zestNode) {
		Map<Node, Node> inputToOutputMap = getInputToOutputNodeMap();
		for (Node input : inputToOutputMap.keySet()) {
			Node output = inputToOutputMap.get(input);
			if (output == zestNode) {
				return input;
			}
		}
		return null;
	}
}
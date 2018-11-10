/*******************************************************************************
 * Copyright (c) 2017, 2018 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alexander Nyßen (itemis AG) - initial API and implementation
 *    Tamas Miklossy  (itemis AG) - minor improvements
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui;

import java.util.ArrayList;
import java.util.List;

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

	public Dot2ZestGraphCopier() {
		this(new Dot2ZestAttributesConverter());
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
	public Graph copy(Graph graph) {
		Graph copiedGraph = super.copy(graph);
		// post-process graph to 'flatten' non-cluster subgraphs
		List<org.eclipse.gef.graph.Node> replacementNodes = new ArrayList<>();
		for (Node node : copiedGraph.getNodes()) {
			if (node.getNestedGraph() == null) {
				replacementNodes.add(node);
			} else {
				// retrieve copied dot node to decide whether we have a cluster
				// (as the name of the nested graph will not be copied)
				Node dotNode = null;
				for (Node n : getInputToOutputNodeMap().keySet()) {
					if (getInputToOutputNodeMap().get(n) == node) {
						dotNode = n;
						break;
					}
				}
				// only support clusters in native mode
				if (DotAttributes.isCluster(dotNode)
						&& !getAttributeCopier().options().emulateLayout) {
					// ensure cluster nodes get lowest z-order
					replacementNodes.add(0, node);

					// initialize a rectangle shape
					ZestProperties.setShape(node,
							new GeometryNode<>(new Rectangle()));

					// for cluster nodes position and size are determined using
					// the bounding box (specified as attribute of nested graph)
					Rect bb = DotAttributes
							.getBbParsed(dotNode.getNestedGraph());

					Point zestPosition = null;
					Dimension zestSize = null;
					if (bb != null) {
						zestPosition = new Point(bb.getLlx(), bb.getUry());
						zestSize = new Dimension(bb.getUrx() - bb.getLlx(),
								bb.getLly() - bb.getUry());
						ZestProperties.setPosition(node, zestPosition);
						ZestProperties.setSize(node, zestSize);
					}

					// determine label for cluster
					String dotLabel = DotAttributes
							.getLabel(dotNode.getNestedGraph());
					if ("\\G".equals(dotLabel)) { //$NON-NLS-1$
						dotLabel = DotAttributes
								._getName(dotNode.getNestedGraph());
					}
					if (dotLabel != null) {
						ZestProperties.setExternalLabel(node, dotLabel);
						// XXX: We use an external label and position it inside
						// the node, as Zest will always center the label.
						// TODO: change this as soon as different label
						// positions are supported by Zest
						if (zestPosition != null && zestSize != null) {
							double xOffset = 0.5 * (zestSize.width
									- Dot2ZestAttributesConverter
											.computeZestLabelSize(
													dotLabel).width);
							double yOffset = 4;
							ZestProperties.setExternalLabelPosition(node,
									zestPosition.getTranslated(xOffset,
											yOffset));
						}
					}
				}
				Graph subgraph = node.getNestedGraph();
				for (Node n : subgraph.getNodes()) {
					// 'unfold' all incoming and outgoing edges (they have
					// to refer to the nested nodes)
					for (Edge e : node.getIncomingEdges()) {
						Edge edgeCopy = copyEdge(e);
						edgeCopy.setTarget(n);
						copiedGraph.getEdges().add(edgeCopy);
					}
					for (Edge e : node.getOutgoingEdges()) {
						Edge edgeCopy = copyEdge(e);
						edgeCopy.setSource(n);
						copiedGraph.getEdges().add(edgeCopy);
					}
				}
				replacementNodes.addAll(subgraph.getNodes());
				copiedGraph.getEdges().addAll(subgraph.getEdges());
				node.setNestedGraph(null);
			}
		}
		// add cluster nodes as lowest in z-order
		return new Graph(copiedGraph.attributesProperty(), replacementNodes,
				copiedGraph.edgesProperty());
	}
}
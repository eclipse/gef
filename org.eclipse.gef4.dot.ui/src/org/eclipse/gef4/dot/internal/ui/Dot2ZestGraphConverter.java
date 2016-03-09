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
 *******************************************************************************/
package org.eclipse.gef4.dot.internal.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.gef4.dot.internal.DotAttributes;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.fx.ZestProperties;

public class Dot2ZestGraphConverter {

	private Graph dotGraph;
	private Map<Node, Node> dotToZestNodes = new HashMap<Node, Node>();
	private boolean emulateLayout;
	private boolean invertYAxis;

	// TODO: we may need an option class here if multiple options are needed
	public Dot2ZestGraphConverter(Graph dotGraph, boolean emulateLayout,
			boolean invertYAxis) {
		this.dotGraph = dotGraph;
		this.emulateLayout = emulateLayout;
		this.invertYAxis = invertYAxis;
	}

	public Graph convert() {
		return convertGraph(dotGraph);
	}

	private Graph convertGraph(Graph dot) {
		Graph zest = new Graph();
		convertGraphAttributes(dot.getAttributes(), zest.getAttributes());
		// convert nodes and store dot to zest mapping, so that source and
		// destination of edges can be found easily later
		for (Node dotNode : dot.getNodes()) {
			Node zestNode = convertNode(dotNode);
			zestNode.setGraph(zest);
			dotToZestNodes.put(dotNode, zestNode);
			zest.getNodes().add(zestNode);
		}
		// convert edges
		for (Edge dotEdge : dot.getEdges()) {
			Edge edge = convertEdge(dotEdge);
			edge.setGraph(zest);
			zest.getEdges().add(edge);
		}
		return zest;
	}

	private Edge convertEdge(Edge dotEdge) {
		// find nodes
		Node zestSource = dotToZestNodes.get(dotEdge.getSource());
		Node zestTarget = dotToZestNodes.get(dotEdge.getTarget());
		// create edge
		Edge zestEdge = new Edge(zestSource, zestTarget);
		convertEdgeAttributes(dotEdge.getAttributes(),
				zestEdge.getAttributes());
		return zestEdge;
	}

	private void convertEdgeAttributes(Map<String, Object> dot,
			Map<String, Object> zest) {
		// convert id and label
		Object dotId = dot.get(DotAttributes.EDGE_ID);
		Object dotLabel = dot.get(DotAttributes.EDGE_LABEL);
		zest.put(ZestProperties.ELEMENT_CSS_ID, dotId);
		zest.put(ZestProperties.ELEMENT_LABEL, dotLabel);

		// convert edge style
		Object dotStyle = dot.get(DotAttributes.EDGE_STYLE);
		String curveCssStyle = null;
		if (DotAttributes.EDGE_STYLE_DASHED.equals(dotStyle)) {
			curveCssStyle = "-fx-stroke-dash-array: 7 7;"; //$NON-NLS-1$
		} else if (DotAttributes.EDGE_STYLE_DOTTED.equals(dotStyle)) {
			curveCssStyle = "-fx-stroke-dash-array: 1 7;"; //$NON-NLS-1$
		} else if (DotAttributes.EDGE_STYLE_BOLD.equals(dotStyle)) {
			curveCssStyle = "-fx-stroke-width: 2;"; //$NON-NLS-1$
		}
		// TODO: handle tapered edges

		if (curveCssStyle != null) {
			zest.put(ZestProperties.EDGE_CURVE_CSS_STYLE, curveCssStyle);
		}
	}

	private Node convertNode(Node dotNode) {
		Node node = new Node();
		convertNodeAttributes(dotNode, node);
		// convert nested graph
		if (dotNode.getNestedGraph() != null) {
			Graph nested = convertGraph(dotNode.getNestedGraph());
			node.setNestedGraph(nested);
		}
		return node;
	}

	// TODO: change into applyNodeAttributes, pass in node, and use set methods
	// of ZestProperties
	private void convertNodeAttributes(Node dotNode, Node zestNode) {
		// convert id and label
		String dotId = DotAttributes.getId(dotNode);
		ZestProperties.setCssId(zestNode, dotId);

		String dotLabel = DotAttributes.getLabel(dotNode);
		if (dotLabel != null && dotLabel.equals("\\N")) { //$NON-NLS-1$
			// The node default label '\N' is used to indicate that a node's
			// name or ID becomes its label.
			dotLabel = dotId;
		}
		ZestProperties.setLabel(zestNode, dotLabel);

		// position
		String dotPos = DotAttributes.getPos(dotNode);
		if (dotPos != null) {
			boolean inputOnly = ((String) dotPos).contains("!");//$NON-NLS-1$
			String posString = inputOnly ? ((String) dotPos).substring(0,
					((String) dotPos).indexOf("!")) : (String) dotPos;//$NON-NLS-1$
			double x = Double.parseDouble(
					posString.substring(0, posString.indexOf(","))); //$NON-NLS-1$
			double y = (invertYAxis ? -1 : 1) * Double.parseDouble(
					posString.substring(posString.indexOf(",") + 1)); //$NON-NLS-1$
			ZestProperties.setPosition(zestNode, new Point(x, y));
			// if a position is marked as input-only in Dot, have Zest ignore it
			ZestProperties.setLayoutIrrelevant(zestNode, inputOnly);
		}

		// size
		Dimension zestSize = new Dimension(-1, -1);
		String dotHeight = DotAttributes.getHeight(dotNode);
		if (dotHeight != null) {
			// TODO: determine DPI (maybe pass in as option)
			double zestHeight = Double.parseDouble(dotHeight) * 72; // inches
			zestSize.setHeight(zestHeight);
		}
		String dotWidth = DotAttributes.getWidth(dotNode);
		if (dotWidth != null) {
			double zestWidth = Double.parseDouble(dotWidth) * 72; // inches
			zestSize.setWidth(zestWidth);
		}
		if (!new Dimension(-1, -1).equals(zestSize)) {
			ZestProperties.setSize(zestNode, zestSize);
		}
	}

	private void convertGraphAttributes(Map<String, Object> dot,
			Map<String, Object> zest) {
		// convert layout and rankdir to LayoutAlgorithm
		if (emulateLayout) {
			Object dotLayout = dot.get(DotAttributes.GRAPH_LAYOUT);
			Object dotRankdir = dot.get(DotAttributes.GRAPH_RANKDIR);
			ILayoutAlgorithm algo = null;
			if (DotAttributes.GRAPH_LAYOUT_CIRCO.equals(dotLayout)
					|| DotAttributes.GRAPH_LAYOUT_NEATO.equals(dotLayout)
					|| DotAttributes.GRAPH_LAYOUT_TWOPI.equals(dotLayout)) {
				algo = new RadialLayoutAlgorithm();
			} else if (DotAttributes.GRAPH_LAYOUT_FDP.equals(dotLayout)
					|| DotAttributes.GRAPH_LAYOUT_SFDP.equals(dotLayout)) {
				algo = new SpringLayoutAlgorithm();
			} else if (DotAttributes.GRAPH_LAYOUT_GRID.equals(dotLayout)
					|| DotAttributes.GRAPH_LAYOUT_OSAGE.equals(dotLayout)) {
				algo = new GridLayoutAlgorithm();
			} else {
				boolean lr = DotAttributes.GRAPH_RANKDIR_LR.equals(dotRankdir);
				algo = new TreeLayoutAlgorithm(
						lr ? TreeLayoutAlgorithm.LEFT_RIGHT
								: TreeLayoutAlgorithm.TOP_DOWN);
			}
			zest.put(ZestProperties.GRAPH_LAYOUT_ALGORITHM, algo);
		}

		// convert graph type
		Object dotType = dot.get(DotAttributes.GRAPH_TYPE);
		if (DotAttributes.GRAPH_TYPE_DIRECTED.equals(dotType)) {
			zest.put(ZestProperties.GRAPH_TYPE,
					ZestProperties.GRAPH_TYPE_DIRECTED);
		} else if (DotAttributes.GRAPH_TYPE_UNDIRECTED.equals(dotType)) {
			zest.put(ZestProperties.GRAPH_TYPE,
					ZestProperties.GRAPH_TYPE_UNDIRECTED);
		}
	}
}

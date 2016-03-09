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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.dot.internal.DotAttributes;
import org.eclipse.gef4.dot.internal.parser.dotAttributes.SplineType;
import org.eclipse.gef4.dot.internal.parser.dotAttributes.SplineType_Spline;
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
		convertEdgeAttributes(dotEdge, zestEdge);
		return zestEdge;
	}

	private void convertEdgeAttributes(Edge dot, Edge zest) {
		// convert id and label
		String dotId = DotAttributes.getId(dot);
		String dotLabel = DotAttributes.getLabel(dot);
		ZestProperties.setCssId(zest, dotId);
		ZestProperties.setLabel(zest, dotLabel);

		// convert edge style
		String dotStyle = DotAttributes.getStyle(dot);
		String connectionCssStyle = null;
		if (DotAttributes.EDGE_STYLE_DASHED.equals(dotStyle)) {
			connectionCssStyle = "-fx-stroke-dash-array: 7 7;"; //$NON-NLS-1$
		} else if (DotAttributes.EDGE_STYLE_DOTTED.equals(dotStyle)) {
			connectionCssStyle = "-fx-stroke-dash-array: 1 7;"; //$NON-NLS-1$
		} else if (DotAttributes.EDGE_STYLE_BOLD.equals(dotStyle)) {
			connectionCssStyle = "-fx-stroke-width: 2;"; //$NON-NLS-1$
		}
		// TODO: handle tapered edges
		if (connectionCssStyle != null) {
			ZestProperties.setEdgeConnCssStyle(zest, connectionCssStyle);
		}

		// position (only convert in native mode, as the results will otherwise
		// not match)
		if (!emulateLayout) {
			String dotPos = DotAttributes.getPos(dot);
			if (dotPos != null) {
				SplineType splineType = DotAttributes.getPosParsed(dot);
				List<Point> controlPoints = new ArrayList<>();
				for (SplineType_Spline spline : splineType.getSplines()) {
					// start
					org.eclipse.gef4.dot.internal.parser.dotAttributes.Point startp = spline
							.getStartp();
					if (startp == null) {
						// if we have no start point, add the first control
						// point
						// twice
						startp = spline.getControlPoints().get(0);
					}
					controlPoints.add(new Point(startp.getX(),
							(invertYAxis ? -1 : 1) * startp.getY()));

					// control points
					for (org.eclipse.gef4.dot.internal.parser.dotAttributes.Point cp : spline
							.getControlPoints()) {
						controlPoints.add(new Point(cp.getX(),
								(invertYAxis ? -1 : 1) * cp.getY()));
					}

					// end
					org.eclipse.gef4.dot.internal.parser.dotAttributes.Point endp = spline
							.getEndp();
					if (endp == null) {
						// if we have no end point, add the last control point
						// twice
						endp = spline.getControlPoints()
								.get(spline.getControlPoints().size() - 1);
					}
					controlPoints.add(new Point(endp.getX(),
							(invertYAxis ? -1 : 1) * endp.getY()));
				}
				ZestProperties.setControlPoints(zest, controlPoints);
				ZestProperties.setInterpolator(zest,
						new DotBSplineInterpolator());
			}
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
	private void convertNodeAttributes(Node dot, Node zest) {
		// convert id and label
		String dotId = DotAttributes.getId(dot);
		ZestProperties.setCssId(zest, dotId);

		String dotLabel = DotAttributes.getLabel(dot);
		if (dotLabel != null && dotLabel.equals("\\N")) { //$NON-NLS-1$
			// The node default label '\N' is used to indicate that a node's
			// name or ID becomes its label.
			dotLabel = dotId;
		}
		ZestProperties.setLabel(zest, dotLabel);

		// Convert position and size; as node position is interpreted as center,
		// we need to know the size in order to infer correct zest positions
		String dotPos = DotAttributes.getPos(dot);
		String dotHeight = DotAttributes.getHeight(dot);
		String dotWidth = DotAttributes.getWidth(dot);
		if (dotPos != null && dotWidth != null && dotHeight != null) {
			// dot default scaling is 72 DPI
			double zestHeight = Double.parseDouble(dotHeight) * 72; // inches
			double zestWidth = Double.parseDouble(dotWidth) * 72; // inches
			ZestProperties.setSize(zest, new Dimension(zestWidth, zestHeight));

			// node position is interpreted as center of node in Dot, and
			// top-left in Zest
			org.eclipse.gef4.dot.internal.parser.dotAttributes.Point posParsed = DotAttributes
					.getPosParsed(dot);
			ZestProperties.setPosition(zest,
					new Point(posParsed.getX() - zestWidth / 2,
							(invertYAxis ? -1 : 1) * (posParsed.getY())
									- zestHeight / 2));
			// if a position is marked as input-only in Dot, have Zest ignore it
			ZestProperties.setLayoutIrrelevant(zest, posParsed.isInputOnly());
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

/*******************************************************************************
 * Copyright (c) 2014 Rene Kuhlemann.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rene Kuhlemann - provided first version of code based on the initial paper
 *    		of Sugiyama et al. (http://dx.doi.org/10.1109/TSMC.1981.4308636),
 *          associated to bugzilla entry #384730  
 *    Adam Kovacs - implements the new LayerProvider and 
 *    		CrossingReducer interfaces
 *******************************************************************************/
package org.eclipse.gef4.layout.algorithms;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.layout.LayoutAlgorithm;
import org.eclipse.gef4.layout.interfaces.CrossingReducer;
import org.eclipse.gef4.layout.interfaces.LayerProvider;
import org.eclipse.gef4.layout.interfaces.LayoutContext;
import org.eclipse.gef4.layout.interfaces.NodeLayout;

/**
 * The SugiyamaLayoutAlgorithm class implements an algorithm to arrange a
 * directed graph in a layered tree-like layout. The final presentation follows
 * five design principles for enhanced readability:
 * 
 * - Hierarchical layout of vertices - Least crossings of lines (edges) -
 * Straightness of lines when ever possible - Close layout of vertices connected
 * to each other, i.e. short paths - Balanced layout of lines coming into or
 * going from a vertex
 * 
 * For further information see http://dx.doi.org/10.1109/TSMC.1981.4308636
 * 
 * This layout algorithm works only with - directed graphs (
 * {@link ZestStyles.CONNECTIONS_DIRECTED}) - graphs without cycles (otherwise
 * an appropriate RuntimeException is thrown)
 * 
 * @version 1.3
 * @author Rene Kuhlemann
 * @author Adam Kovacs
 */
public class SugiyamaLayoutAlgorithm implements LayoutAlgorithm, LayerProvider,
		CrossingReducer {

	public enum Direction {
		HORIZONTAL, VERTICAL
	};

	private List<List<NodeWrapper>> layers = new ArrayList<List<NodeWrapper>>();
	private Map<NodeLayout, NodeWrapper> map = new IdentityHashMap<NodeLayout, NodeWrapper>();
	private final Direction direction;
	private final Dimension dimension;

	private LayoutContext context;
	private int last; // index of the last element in a layer after padding
						// process

	private LayerProvider layerProvider;
	private CrossingReducer crossingReducer;

	/**
	 * Constructs a tree-like, layered layout of a directed graph.
	 * 
	 * @param dir
	 *            - {@link SugiyamaLayoutAlgorithm#HORIZONTAL}: left to right -
	 *            {@link SugiyamaLayoutAlgorithm#VERTCAL}: top to bottom
	 * 
	 * @param dim
	 *            - desired size of the layout area. Uses
	 *            {@link LayoutContext#getBounds()} if not set
	 * 
	 * @param layering
	 *            - implementation of LayerProvider interface
	 * 
	 * @param crossing
	 *            - implementation of CrossingReducer interface
	 */
	public SugiyamaLayoutAlgorithm(Direction dir, Dimension dim,
			LayerProvider layering, CrossingReducer crossing) {
		direction = dir;
		dimension = dim;

		layerProvider = layering;
		crossingReducer = crossing;
	}

	public SugiyamaLayoutAlgorithm(Direction dir, LayerProvider layerProvider,
			CrossingReducer crossing) {
		this(dir, null, layerProvider, crossing);
	}

	public SugiyamaLayoutAlgorithm(Direction dir, LayerProvider layerProvider) {
		this(dir, null, layerProvider, new BarycentricCrossingReducer());
	}

	public SugiyamaLayoutAlgorithm(Direction dir, CrossingReducer crossing) {
		this(dir, null, new SimpleLayerProvider(), crossing);
	}

	public SugiyamaLayoutAlgorithm(Direction dir, Dimension dim) {
		this(dir, dim, new SimpleLayerProvider(),
				new BarycentricCrossingReducer());
	}

	public SugiyamaLayoutAlgorithm(Direction dir) {
		this(dir, null, new SimpleLayerProvider(),
				new BarycentricCrossingReducer());
	}

	public SugiyamaLayoutAlgorithm() {
		this(Direction.VERTICAL, null, new DFSLayerProvider(),
				new BarycentricCrossingReducer());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef4.layout.LayoutAlgorithm#setLayoutContext(org.eclipse
	 * .zest.layouts.interfaces.LayoutContext)
	 */
	public void setLayoutContext(LayoutContext context) {
		this.context = context;
	}

	public LayoutContext getLayoutContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef4.layout.LayoutAlgorithm#applyLayout(boolean)
	 */
	public void applyLayout(boolean clean) {
		if (!clean)
			return;
		layers.clear();
		map.clear();

		ArrayList<NodeLayout> nodes = new ArrayList<NodeLayout>();
		ArrayList<NodeLayout> nodes2 = new ArrayList<NodeLayout>();
		for (NodeLayout node : context.getNodes()) {
			nodes.add(node);
			nodes2.add(node);
		}
		if (layerProvider != null) {
			clearAssignedNodes();
			addAssignedNode(nodes.get(0), 0);
			addAssignedNode(nodes.get(nodes.size() - 1), 0);
			layers = calculateLayers(nodes, getAssignedNodes());
		} else {
			List<NodeWrapper> layer = new ArrayList<NodeWrapper>();
			for (int i = 0; i < nodes.size(); i++) {
				NodeWrapper nw = new NodeWrapper(nodes.get(i), 0);
				nw.index = i;
				layer.add(nw);
			}
			layers.add(layer);
		}
		if (crossingReducer != null) {
			map = crossReduction(layers);
		}

		for (List<NodeWrapper> layer : layers) {
			if (layer.size() > last)
				last = layer.size();
			for (NodeWrapper nw : layer) {
				map.put(nw.node, nw);
			}
		}
		calculatePositions();
	}

	private void calculatePositions() {
		Rectangle boundary = context.getBounds();
		if (dimension != null)
			boundary = new Rectangle(0, 0, dimension.getWidth(),
					dimension.getHeight());
		double dx = boundary.getWidth() / layers.size();
		double dy = boundary.getHeight() / (last + 1);
		if (direction == Direction.HORIZONTAL)
			for (NodeLayout node : context.getNodes()) {
				NodeWrapper nw = map.get(node);
				node.setLocation((nw.layer + 0.5d) * dx, (nw.index + 0.5d) * dy);
			}
		else
			for (NodeLayout node : context.getNodes()) {
				NodeWrapper nw = map.get(node);
				node.setLocation((nw.index + 0.5d) * dx, (nw.layer + 0.5d) * dy);
			}
	}

	public Map<NodeLayout, NodeWrapper> crossReduction(
			List<List<NodeWrapper>> nodes) {
		// TODO Auto-generated method stub
		if (crossingReducer != null) {
			return crossingReducer.crossReduction(nodes);
		} else
			return null;
	}

	public Map<NodeLayout, Integer> getAssignedNodes() {
		// TODO Auto-generated method stub
		if (layerProvider != null) {
			return layerProvider.getAssignedNodes();
		} else
			return null;
	}

	public void addAssignedNode(NodeLayout node, int layer) {
		// TODO Auto-generated method stub
		if (layerProvider != null) {
			layerProvider.addAssignedNode(node, layer);
		}
	}

	public void clearAssignedNodes() {
		// TODO Auto-generated method stub
		if (layerProvider != null) {
			layerProvider.clearAssignedNodes();
		}
	}

	public List<List<NodeWrapper>> calculateLayers(List<NodeLayout> nodes,
			Map<NodeLayout, Integer> assignedNodes) {
		// TODO Auto-generated method stub
		if (layerProvider != null) {
			return layerProvider.calculateLayers(nodes, assignedNodes);
		} else
			return null;
	}
}
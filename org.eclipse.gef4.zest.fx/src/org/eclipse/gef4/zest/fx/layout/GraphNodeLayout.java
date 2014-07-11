/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
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
package org.eclipse.gef4.zest.fx.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.PropertyStoreSupport;
import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.EntityLayout;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.layout.interfaces.SubgraphLayout;

public class GraphNodeLayout implements NodeLayout {

	// TODO: move these to interface
	public static final String PRUNABLE_PROPERTY = "prunable";
	public static final String RESIZABLE_PROPERTY = "resizable";
	public static final String ASPECT_RATIO_PROPERTY = "aspectRatio";
	// public static final String PRUNED_PROPERTY = "pruned";

	// defaults for properties
	// TODO: move these to interface, too?
	private static final Boolean DEFAULT_MINIMIZED = false;
	private static final Boolean DEFAULT_MOVABLE = true;
	private static final Dimension DEFAULT_SIZE = new Dimension(0, 0);
	private static final Point DEFAULT_LOCATION = new Point(0, 0);
	private static final Boolean DEFAULT_PRUNABLE = true;
	private static final Boolean DEFAULT_RESIZABLE = true;
	private static final Double DEFAULT_ASPECT_RATIO = 0d;
	// private static final Boolean DEFAULT_PRUNED = false;

	// initialization context
	private GraphLayoutContext context;
	private PropertyStoreSupport ps = new PropertyStoreSupport();
	private Node node;
	private SubgraphLayout subgraph;

	public GraphNodeLayout(GraphLayoutContext context, Node node) {
		this.context = context;
		this.node = node;
		// copy properties
		for (Entry<String, Object> e : node.getAttrs().entrySet()) {
			setProperty(e.getKey(), e.getValue());
		}
	}

	@Override
	public ConnectionLayout[] getIncomingConnections() {
		List<ConnectionLayout> incoming = new ArrayList<ConnectionLayout>();

		ConnectionLayout[] connections = context.getConnections();
		for (ConnectionLayout c : connections) {
			if (c.getTarget() == this) {
				incoming.add(c);
			}
		}

		return incoming.toArray(new ConnectionLayout[0]);
	}

	@Override
	public Object[] getItems() {
		return new Object[] { node };
	}

	@Override
	public Point getLocation() {
		Object location = getProperty(EntityLayout.LOCATION_PROPERTY);
		if (!(location instanceof Point)) {
			location = DEFAULT_LOCATION;
			setProperty(LOCATION_PROPERTY, location);
		}
		return ((Point) location).getCopy();
	}

	// public Node getNodeModel() {
	// return node;
	// }

	// FIXME: duplicate code! getIncomingConnections ~ getOutgoingConnections
	@Override
	public ConnectionLayout[] getOutgoingConnections() {
		List<ConnectionLayout> incoming = new ArrayList<ConnectionLayout>();

		ConnectionLayout[] connections = context.getConnections();
		for (ConnectionLayout c : connections) {
			if (c.getSource() == this) {
				incoming.add(c);
			}
		}

		return incoming.toArray(new ConnectionLayout[0]);
	}

	@Override
	public EntityLayout[] getPredecessingEntities() {
		return getPredecessingNodes();
	}

	@Override
	public NodeLayout[] getPredecessingNodes() {
		NodeLayout[] nodes = context.getNodes();

		int index = 0;
		while (nodes[index] != this) {
			index++;
		}

		int length = index;
		if (length == 0) {
			return new NodeLayout[0];
		}

		NodeLayout[] predecessors = new NodeLayout[length];
		for (int i = 0; i < predecessors.length; i++) {
			predecessors[i] = nodes[i];
		}
		return predecessors;
	}

	@Override
	public double getPreferredAspectRatio() {
		Object ar = getProperty(ASPECT_RATIO_PROPERTY);
		if (!(ar instanceof Double)) {
			ar = DEFAULT_ASPECT_RATIO;
			setProperty(ASPECT_RATIO_PROPERTY, ar);
		}
		return ((Double) ar).doubleValue();
	}

	@Override
	public Object getProperty(String name) {
		return ps.getProperty(name);
	}

	@Override
	public Dimension getSize() {
		Object size = getProperty(SIZE_PROPERTY);
		if (!(size instanceof Dimension)) {
			size = DEFAULT_SIZE;
			setProperty(SIZE_PROPERTY, size);
		}
		return ((Dimension) size).getCopy();
	}

	@Override
	public SubgraphLayout getSubgraph() {
		return subgraph;
	}

	@Override
	public EntityLayout[] getSuccessingEntities() {
		return getSuccessingNodes();
	}

	@Override
	public NodeLayout[] getSuccessingNodes() {
		NodeLayout[] nodes = context.getNodes();

		int index = 0;
		while (nodes[index] != this) {
			index++;
		}

		int offset = index + 1;
		if (offset >= nodes.length) {
			return new NodeLayout[0];
		}

		NodeLayout[] successors = new NodeLayout[nodes.length - offset];
		for (int i = 0; i < successors.length; i++) {
			successors[i] = nodes[offset + i];
		}
		return successors;
	}

	@Override
	public boolean isMinimized() {
		Object minimized = getProperty(MINIMIZED_PROPERTY);
		if (!(minimized instanceof Boolean)) {
			minimized = DEFAULT_MINIMIZED;
			setProperty(MINIMIZED_PROPERTY, minimized);
		}
		return ((Boolean) minimized).booleanValue();
	}

	@Override
	public boolean isMovable() {
		Object movable = getProperty(MOVABLE_PROPERTY);
		if (!(movable instanceof Boolean)) {
			movable = DEFAULT_MOVABLE;
			setProperty(MOVABLE_PROPERTY, movable);
		}
		return ((Boolean) movable).booleanValue();
	}

	@Override
	public boolean isPrunable() {
		Object prunable = getProperty(PRUNABLE_PROPERTY);
		if (!(prunable instanceof Boolean)) {
			prunable = DEFAULT_PRUNABLE;
			setProperty(PRUNABLE_PROPERTY, prunable);
		}
		return ((Boolean) prunable).booleanValue();
	}

	@Override
	public boolean isPruned() {
		return subgraph != null;
	}

	@Override
	public boolean isResizable() {
		Object resizable = getProperty(RESIZABLE_PROPERTY);
		if (!(resizable instanceof Boolean)) {
			resizable = DEFAULT_RESIZABLE;
			setProperty(RESIZABLE_PROPERTY, resizable);
		}
		return ((Boolean) resizable).booleanValue();
	}

	@Override
	public void prune(SubgraphLayout subgraph) {
		// TODO: fire events
		if (this.subgraph != null) {
			this.subgraph.removeNodes(new NodeLayout[] { this });
		}
		this.subgraph = subgraph;
		subgraph.addNodes(new NodeLayout[] { this });
	}

	@Override
	public void setLocation(double x, double y) {
		if (Double.isNaN(x)) {
			x = 0;
		}
		if (Double.isNaN(y)) {
			y = 0;
		}

		// TODO: use Point#setLocation() when we already store a location
		setProperty(LOCATION_PROPERTY, new Point(x, y));
		context.fireNodeMovedEvent(this);
	}

	@Override
	public void setMinimized(boolean minimized) {
		setProperty(MINIMIZED_PROPERTY, minimized);
	}

	@Override
	public void setProperty(String name, Object value) {
		ps.setProperty(name, value);
	}

	@Override
	public void setSize(double width, double height) {
		// TODO: use Dimension#setSize() when we already store a size
		setProperty(SIZE_PROPERTY, new Dimension(width, height));
		context.fireNodeResizedEvent(this);
	}

}

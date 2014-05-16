/*******************************************************************************
 * Copyright (c) 2009-2010 Mateusz Matela and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.gef4.zest.core.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef4.layout.PropertyStoreSupport;
import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.EntityLayout;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.layout.interfaces.SubgraphLayout;

class InternalNodeLayout implements NodeLayout {

	/**
	 * This listener is added to nodes' figures as a workaround for the problem
	 * of minimized nodes leaving single on the graph pixels when zoomed out
	 */
	private final static FigureListener figureListener = new FigureListener() {
		public void figureMoved(IFigure source) {
			// hide figures of minimized nodes
			GraphNode node = figureToNode.get(source);
			if (node.getLayout().isMinimized() && source.getSize().equals(0, 0)) {
				source.setVisible(false);
			} else {
				source.setVisible(node.isVisible());
			}
		}
	};

	// FIXME: *static* figure-to-node map?!
	private final static HashMap<IFigure, GraphNode> figureToNode = new HashMap<IFigure, GraphNode>();

	private PropertyStoreSupport ps = new PropertyStoreSupport();

	// internal/layout things
	private final GraphNode node;
	private final InternalLayoutContext layoutContext;
	private DefaultSubgraph subgraph;
	private boolean isDisposed = false;

	{
		// fill default properties
		setProperty(NodeLayout.MINIMIZED_PROPERTY, false);
		setProperty(EntityLayout.MOVABLE_PROPERTY, true);
	}

	public InternalNodeLayout(GraphNode graphNode,
			InternalLayoutContext layoutContext) {
		this.node = graphNode;
		this.layoutContext = layoutContext;
		graphNode.nodeFigure.addFigureListener(figureListener);
		figureToNode.put(graphNode.nodeFigure, graphNode);
	}

	public org.eclipse.gef4.geometry.planar.Point getLocation() {
		Object location = getProperty(EntityLayout.LOCATION_PROPERTY);
		if (location == null) {
			refreshLocation();
		}
		return ((org.eclipse.gef4.geometry.planar.Point) getProperty(EntityLayout.LOCATION_PROPERTY))
				.getCopy();
	}

	public org.eclipse.gef4.geometry.planar.Dimension getSize() {
		Object size = getProperty(EntityLayout.SIZE_PROPERTY);
		if (size == null) {
			refreshSize();
		}
		return ((org.eclipse.gef4.geometry.planar.Dimension) getProperty(EntityLayout.SIZE_PROPERTY))
				.getCopy();
	}

	public SubgraphLayout getSubgraph() {
		return subgraph;
	}

	public boolean isMovable() {
		return (Boolean) getProperty(EntityLayout.MOVABLE_PROPERTY);
	}

	public boolean isPrunable() {
		return layoutContext.isPruningEnabled();
	}

	public boolean isPruned() {
		return subgraph != null;
	}

	public boolean isResizable() {
		return (node.parent.getItem().getStyle() & ZestStyles.NODES_NO_LAYOUT_RESIZE) == 0;
	}

	public void prune(SubgraphLayout subgraph) {
		if (subgraph != null && !(subgraph instanceof DefaultSubgraph)) {
			throw new RuntimeException(
					"InternalNodeLayout can be pruned only to instance of DefaultSubgraph.");
		}
		layoutContext.checkChangesAllowed();
		if (subgraph == this.subgraph) {
			return;
		}
		if (this.subgraph != null) {
			SubgraphLayout subgraph2 = this.subgraph;
			this.subgraph = null;
			subgraph2.removeNodes(new NodeLayout[] { this });
		}
		if (subgraph != null) {
			this.subgraph = (DefaultSubgraph) subgraph;
			subgraph.addNodes(new NodeLayout[] { this });
		}
	}

	public void setLocation(double x, double y) {
		if (!layoutContext.isLayoutItemFiltered(this.getNode())) {
			layoutContext.checkChangesAllowed();
			internalSetLocation(x, y);
		}
	}

	private void internalSetLocation(double x, double y) {
		Object location = getProperty(EntityLayout.LOCATION_PROPERTY);
		if (location != null) {
			((org.eclipse.gef4.geometry.planar.Point) location).setLocation(x,
					y);
		} else {
			setProperty(EntityLayout.LOCATION_PROPERTY,
					new org.eclipse.gef4.geometry.planar.Point(x, y));
		}
	}

	public void setSize(double width, double height) {
		layoutContext.checkChangesAllowed();
		internalSetSize(width, height);
	}

	private void internalSetSize(double width, double height) {
		Object size = getProperty(EntityLayout.SIZE_PROPERTY);
		if (size != null) {
			((org.eclipse.gef4.geometry.planar.Dimension) size).setSize(width,
					height);
		} else {
			setProperty(EntityLayout.SIZE_PROPERTY,
					new org.eclipse.gef4.geometry.planar.Dimension(width,
							height));
		}
	}

	public void setMinimized(boolean minimized) {
		layoutContext.checkChangesAllowed();
		getSize(); // FIXME: strange action at a distance!
		setProperty(NodeLayout.MINIMIZED_PROPERTY, minimized);
	}

	public boolean isMinimized() {
		return (Boolean) getProperty(NodeLayout.MINIMIZED_PROPERTY);
	}

	public NodeLayout[] getPredecessingNodes() {
		ConnectionLayout[] connections = getIncomingConnections();
		NodeLayout[] result = new NodeLayout[connections.length];
		for (int i = 0; i < connections.length; i++) {
			result[i] = connections[i].getSource();
			if (result[i] == this) {
				result[i] = connections[i].getTarget();
			}
		}
		return result;
	}

	public NodeLayout[] getSuccessingNodes() {
		ConnectionLayout[] connections = getOutgoingConnections();
		NodeLayout[] result = new NodeLayout[connections.length];
		for (int i = 0; i < connections.length; i++) {
			result[i] = connections[i].getTarget();
			if (result[i] == this) {
				result[i] = connections[i].getSource();
			}
		}
		return result;
	}

	public EntityLayout[] getSuccessingEntities() {
		if (isPruned()) {
			return new NodeLayout[0];
		}
		ArrayList<EntityLayout> result = new ArrayList<EntityLayout>();
		HashSet<SubgraphLayout> addedSubgraphs = new HashSet<SubgraphLayout>();
		NodeLayout[] successingNodes = getSuccessingNodes();
		for (int i = 0; i < successingNodes.length; i++) {
			if (!successingNodes[i].isPruned()) {
				result.add(successingNodes[i]);
			} else {
				SubgraphLayout successingSubgraph = successingNodes[i]
						.getSubgraph();
				if (successingSubgraph.isGraphEntity()
						&& !addedSubgraphs.contains(successingSubgraph)) {
					result.add(successingSubgraph);
					addedSubgraphs.add(successingSubgraph);
				}
			}
		}
		return result.toArray(new EntityLayout[result.size()]);
	}

	public EntityLayout[] getPredecessingEntities() {
		if (isPruned()) {
			return new NodeLayout[0];
		}
		ArrayList<EntityLayout> result = new ArrayList<EntityLayout>();
		HashSet<SubgraphLayout> addedSubgraphs = new HashSet<SubgraphLayout>();
		NodeLayout[] predecessingNodes = getPredecessingNodes();
		for (int i = 0; i < predecessingNodes.length; i++) {
			if (!predecessingNodes[i].isPruned()) {
				result.add(predecessingNodes[i]);
			} else {
				SubgraphLayout predecessingSubgraph = predecessingNodes[i]
						.getSubgraph();
				if (predecessingSubgraph.isGraphEntity()
						&& !addedSubgraphs.contains(predecessingSubgraph)) {
					result.add(predecessingSubgraph);
					addedSubgraphs.add(predecessingSubgraph);
				}
			}
		}
		return result.toArray(new EntityLayout[result.size()]);
	}

	public ConnectionLayout[] getIncomingConnections() {
		ArrayList<InternalConnectionLayout> result = new ArrayList<InternalConnectionLayout>();
		for (Iterator<GraphConnection> iterator = node.getTargetConnections()
				.iterator(); iterator.hasNext();) {
			GraphConnection connection = iterator.next();
			if (!layoutContext.isLayoutItemFiltered(connection)) {
				result.add(connection.getLayout());
			}
		}
		for (Iterator<GraphConnection> iterator = node.getSourceConnections()
				.iterator(); iterator.hasNext();) {
			GraphConnection connection = iterator.next();
			if (!connection.isDirected()
					&& !layoutContext.isLayoutItemFiltered(connection)) {
				result.add(connection.getLayout());
			}
		}
		return result.toArray(new ConnectionLayout[result.size()]);
	}

	public ConnectionLayout[] getOutgoingConnections() {
		ArrayList<InternalConnectionLayout> result = new ArrayList<InternalConnectionLayout>();
		for (Iterator<GraphConnection> iterator = node.getSourceConnections()
				.iterator(); iterator.hasNext();) {
			GraphConnection connection = iterator.next();
			if (!layoutContext.isLayoutItemFiltered(connection)) {
				result.add(connection.getLayout());
			}
		}
		for (Iterator<GraphConnection> iterator = node.getTargetConnections()
				.iterator(); iterator.hasNext();) {
			GraphConnection connection = iterator.next();
			if (!connection.isDirected()
					&& !layoutContext.isLayoutItemFiltered(connection)) {
				result.add(connection.getLayout());
			}
		}
		return result.toArray(new ConnectionLayout[result.size()]);
	}

	public double getPreferredAspectRatio() {
		return 0;
	}

	GraphNode getNode() {
		return node;
	}

	public Object[] getItems() {
		return new GraphNode[] { node };
	}

	void applyLayout() {
		if (isMinimized()) {
			node.setSize(0, 0);
			Object location = getProperty(EntityLayout.LOCATION_PROPERTY);
			if (location != null) {
				org.eclipse.gef4.geometry.planar.Point p = (org.eclipse.gef4.geometry.planar.Point) location;
				node.setLocation(p.x, p.y);
			}
		} else {
			node.setSize(-1, -1);
			Object location = getProperty(EntityLayout.LOCATION_PROPERTY);
			Object size = getProperty(EntityLayout.SIZE_PROPERTY);
			if (location != null) {
				org.eclipse.gef4.geometry.planar.Point p = (org.eclipse.gef4.geometry.planar.Point) location;
				org.eclipse.gef4.geometry.planar.Dimension d = (org.eclipse.gef4.geometry.planar.Dimension) size;
				// FIXME: what if size == null?
				node.setLocation(p.x - getSize().width / 2, p.y - d.height / 2);
			}
			if (size != null) {
				org.eclipse.gef4.geometry.planar.Dimension d = (org.eclipse.gef4.geometry.planar.Dimension) size;
				Dimension currentSize = node.getSize();
				if (d.width != currentSize.width
						|| d.height != currentSize.height) {
					node.setSize(d.width, d.height);
				}
			}
		}
	}

	InternalLayoutContext getOwnerLayoutContext() {
		return layoutContext;
	}

	void refreshSize() {
		Dimension size = node.getSize();
		internalSetSize(size.width, size.height);
	}

	void refreshLocation() {
		Point location = node.getLocation();
		Object sizeObj = getProperty(EntityLayout.SIZE_PROPERTY);
		org.eclipse.gef4.geometry.planar.Dimension size = (org.eclipse.gef4.geometry.planar.Dimension) sizeObj;
		internalSetLocation(location.x + getSize().width / 2, location.y
				+ size.height / 2);
	}

	public String toString() {
		return node.toString() + "(layout)";
	}

	void dispose() {
		isDisposed = true;
		if (subgraph != null) {
			subgraph.removeNodes(new NodeLayout[] { this });
		}
		layoutContext.fireNodeRemovedEvent(node.getLayout());
		figureToNode.remove(node.nodeFigure);
	}

	boolean isDisposed() {
		return isDisposed;
	}

	public void setProperty(String name, Object value) {
		ps.setProperty(name, value);
	}

	public Object getProperty(String name) {
		return ps.getProperty(name);
	}

}
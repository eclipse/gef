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
import org.eclipse.gef4.layout.PropertiesHelper;
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

	public InternalNodeLayout(GraphNode graphNode,
			InternalLayoutContext layoutContext) {
		this.node = graphNode;
		this.layoutContext = layoutContext;
		graphNode.nodeFigure.addFigureListener(figureListener);
		figureToNode.put(graphNode.nodeFigure, graphNode);
	}

	// TODO: replace with PropertiesHelper.setX calls
	private void setp(String name, Object value) {
		ps.setProperty(name, value);
	}

	// TODO: replace with PropertiesHelper.getX calls
	private Object getp(String name) {
		return ps.getProperty(name);
	}

	public org.eclipse.gef4.geometry.planar.Point getLocation() {
		Object location = getp(PropertiesHelper.LOCATION_PROPERTY);
		if (location == null) {
			refreshLocation();
		}
		return ((org.eclipse.gef4.geometry.planar.Point) getp(PropertiesHelper.LOCATION_PROPERTY))
				.getCopy();
	}

	public org.eclipse.gef4.geometry.planar.Dimension getSize() {
		Object size = getp(PropertiesHelper.SIZE_PROPERTY);
		if (size == null) {
			refreshSize();
		}
		return ((org.eclipse.gef4.geometry.planar.Dimension) getp(PropertiesHelper.SIZE_PROPERTY))
				.getCopy();
	}

	public SubgraphLayout getSubgraph() {
		return subgraph;
	}

	public boolean isMovable() {
		Object movable = getp(PropertiesHelper.MOVABLE_PROPERTY);
		if (movable instanceof Boolean) {
			return (Boolean) movable;
		}
		return PropertiesHelper.DEFAULT_MOVABLE;
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
		Object location = getp(PropertiesHelper.LOCATION_PROPERTY);
		if (location != null) {
			((org.eclipse.gef4.geometry.planar.Point) location).setLocation(x,
					y);
		} else {
			setp(PropertiesHelper.LOCATION_PROPERTY,
					new org.eclipse.gef4.geometry.planar.Point(x, y));
		}
	}

	public void setSize(double width, double height) {
		layoutContext.checkChangesAllowed();
		internalSetSize(width, height);
	}

	private void internalSetSize(double width, double height) {
		Object size = getp(PropertiesHelper.SIZE_PROPERTY);
		if (size != null) {
			((org.eclipse.gef4.geometry.planar.Dimension) size).setSize(width,
					height);
		} else {
			setp(PropertiesHelper.SIZE_PROPERTY,
					new org.eclipse.gef4.geometry.planar.Dimension(width,
							height));
		}
	}

	public void setMinimized(boolean minimized) {
		layoutContext.checkChangesAllowed();
		setp(PropertiesHelper.MINIMIZED_PROPERTY, minimized);
	}

	public boolean isMinimized() {
		Object minimized = getp(PropertiesHelper.MINIMIZED_PROPERTY);
		if (minimized instanceof Boolean) {
			return (Boolean) minimized;
		}
		return PropertiesHelper.DEFAULT_MINIMIZED;
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
			if (!PropertiesHelper.isPruned(successingNodes[i])) {
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
			if (!PropertiesHelper.isPruned(predecessingNodes[i])) {
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
			Object location = getp(PropertiesHelper.LOCATION_PROPERTY);
			if (location != null) {
				org.eclipse.gef4.geometry.planar.Point p = (org.eclipse.gef4.geometry.planar.Point) location;
				node.setLocation(p.x, p.y);
			}
		} else {
			node.setSize(-1, -1);
			Object location = getp(PropertiesHelper.LOCATION_PROPERTY);
			Object size = getp(PropertiesHelper.SIZE_PROPERTY);
			if (location != null) {
				org.eclipse.gef4.geometry.planar.Point p = (org.eclipse.gef4.geometry.planar.Point) location;
				org.eclipse.gef4.geometry.planar.Dimension d = getSize();
				node.setLocation(p.x - d.width / 2, p.y - d.height / 2);
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
		org.eclipse.gef4.geometry.planar.Dimension size = getSize();
		internalSetLocation(location.x + size.width / 2, location.y
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
		if (PropertiesHelper.ASPECT_RATIO_PROPERTY.equals(name)) {
			// not supported
		} else if (PropertiesHelper.LOCATION_PROPERTY.equals(name)) {
			org.eclipse.gef4.geometry.planar.Point p = (org.eclipse.gef4.geometry.planar.Point) value;
			setLocation(p.x, p.y);
		} else if (PropertiesHelper.MINIMIZED_PROPERTY.equals(name)) {
			setMinimized((Boolean) value);
		} else if (PropertiesHelper.MOVABLE_PROPERTY.equals(name)) {
			// not supported
		} else if (PropertiesHelper.PRUNABLE_PROPERTY.equals(name)) {
			// not supported
		} else if (PropertiesHelper.RESIZABLE_PROPERTY.equals(name)) {
			// not supported
		} else if (PropertiesHelper.SIZE_PROPERTY.equals(name)) {
			org.eclipse.gef4.geometry.planar.Dimension size = (org.eclipse.gef4.geometry.planar.Dimension) value;
			setSize(size.width, size.height);
		} else {
			setp(name, value);
		}
	}

	public Object getProperty(String name) {
		if (PropertiesHelper.ASPECT_RATIO_PROPERTY.equals(name)) {
			return getPreferredAspectRatio();
		} else if (PropertiesHelper.LOCATION_PROPERTY.equals(name)) {
			return getLocation();
		} else if (PropertiesHelper.MINIMIZED_PROPERTY.equals(name)) {
			return isMinimized();
		} else if (PropertiesHelper.MOVABLE_PROPERTY.equals(name)) {
			return isMovable();
		} else if (PropertiesHelper.PRUNABLE_PROPERTY.equals(name)) {
			return isPrunable();
		} else if (PropertiesHelper.RESIZABLE_PROPERTY.equals(name)) {
			return isResizable();
		} else if (PropertiesHelper.SIZE_PROPERTY.equals(name)) {
			return getSize();
		} else {
			return getp(name);
		}
	}

}
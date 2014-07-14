/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.layout;

import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.layout.interfaces.ConnectionLayout;
import org.eclipse.gef4.layout.interfaces.EntityLayout;
import org.eclipse.gef4.layout.interfaces.LayoutContext;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.layout.interfaces.SubgraphLayout;

public class PropertiesHelper implements IProperties {

	// graphs

	public static Rectangle getBounds(LayoutContext graph) {
		Object bounds = graph.getProperty(BOUNDS_PROPERTY);
		if (bounds instanceof Rectangle) {
			return (Rectangle) bounds;
		}
		return DEFAULT_BOUNDS;
	}

	public static void setBounds(LayoutContext graph, Rectangle bounds) {
		graph.setProperty(BOUNDS_PROPERTY, bounds);
	}

	public static Boolean isBoundsExpandable(LayoutContext graph) {
		Object boundsExpandable = graph.getProperty(BOUNDS_EXPANDABLE_PROPERTY);
		if (boundsExpandable instanceof Boolean) {
			return (Boolean) boundsExpandable;
		}
		return DEFAULT_BOUNDS_EXPANDABLE;
	}

	public static void setBoundsExpandable(LayoutContext graph,
			boolean boundsExpandable) {
		graph.setProperty(BOUNDS_EXPANDABLE_PROPERTY, boundsExpandable);
	}

	public static Boolean isDynamicLayoutEnables(LayoutContext graph) {
		Object dynamicLayoutEnabled = graph
				.getProperty(DYNAMIC_LAYOUT_ENABLED_PROPERTY);
		if (dynamicLayoutEnabled instanceof Boolean) {
			return (Boolean) dynamicLayoutEnabled;
		}
		return DEFAULT_DYNAMIC_LAYOUT_ENABLED;
	}

	public static void setDynamicLayoutEnabled(LayoutContext graph,
			boolean dynamicLayoutEnabled) {
		graph.setProperty(DYNAMIC_LAYOUT_ENABLED_PROPERTY, dynamicLayoutEnabled);
	}

	// entities

	public static Point getLocation(EntityLayout entity) {
		Object location = entity.getProperty(LOCATION_PROPERTY);
		if (location instanceof Point) {
			return (Point) location;
		}
		return DEFAULT_LOCATION;
	}

	public static Dimension getSize(EntityLayout entity) {
		Object size = entity.getProperty(SIZE_PROPERTY);
		if (size instanceof Dimension) {
			return (Dimension) size;
		}
		return DEFAULT_SIZE;
	}

	public static Double getPreferredAspectRatio(EntityLayout entity) {
		Object ar = entity.getProperty(ASPECT_RATIO_PROPERTY);
		if (ar instanceof Double) {
			return (Double) ar;
		}
		return DEFAULT_ASPECT_RATIO;
	}

	public static Boolean isResizable(EntityLayout entity) {
		Object resizable = entity.getProperty(RESIZABLE_PROPERTY);
		if (resizable instanceof Boolean) {
			return (Boolean) resizable;
		}
		return DEFAULT_RESIZABLE;
	}

	public static Boolean isMovable(EntityLayout entity) {
		Object movable = entity.getProperty(MOVABLE_PROPERTY);
		if (movable instanceof Boolean) {
			return (Boolean) movable;
		}
		return DEFAULT_MOVABLE;
	}

	public static void setLocation(EntityLayout entity, double x, double y) {
		entity.setProperty(LOCATION_PROPERTY, new Point(x, y));
	}

	public static void setSize(EntityLayout entity, double w, double h) {
		entity.setProperty(SIZE_PROPERTY, new Dimension(w, h));
	}

	// nodes

	public static Boolean isMinimized(NodeLayout node) {
		Object minimized = node.getProperty(MINIMIZED_PROPERTY);
		if (minimized instanceof Boolean) {
			return (Boolean) minimized;
		}
		return DEFAULT_MINIMIZED;
	}

	public static void setMinimized(NodeLayout node, boolean minimized) {
		node.setProperty(MINIMIZED_PROPERTY, minimized);
	}

	public static Boolean isPrunable(NodeLayout node) {
		Object prunable = node.getProperty(PRUNABLE_PROPERTY);
		if (prunable instanceof Boolean) {
			return (Boolean) prunable;
		}
		return DEFAULT_PRUNABLE;
	}

	public static Boolean isPruned(NodeLayout node) {
		return node.getSubgraph() != null;
	}

	public static void setPrunable(NodeLayout node, boolean prunable) {
		node.setProperty(PRUNABLE_PROPERTY, prunable);
	}

	// connections

	public static Boolean isDirected(ConnectionLayout edge) {
		Object directed = edge.getProperty(DIRECTED_PROPERTY);
		if (directed instanceof Boolean) {
			return (Boolean) directed;
		}
		return DEFAULT_DIRECTED;
	}

	public static Boolean isVisible(ConnectionLayout edge) {
		Object visible = edge.getProperty(VISIBLE_PROPERTY);
		if (visible instanceof Boolean) {
			return (Boolean) visible;
		}
		return DEFAULT_VISIBLE;
	}

	public static Double getWeight(ConnectionLayout edge) {
		Object weight = edge.getProperty(WEIGHT_PROPERTY);
		if (weight instanceof Double) {
			return (Double) weight;
		}
		return DEFAULT_WEIGHT;
	}

	public static void setDirected(ConnectionLayout edge, boolean directed) {
		edge.setProperty(DIRECTED_PROPERTY, directed);
	}

	public static void setVisible(ConnectionLayout edge, boolean visible) {
		edge.setProperty(VISIBLE_PROPERTY, visible);
	}

	public static void setWeight(ConnectionLayout edge, double weight) {
		edge.setProperty(WEIGHT_PROPERTY, weight);
	}

	// subgraphs

	public static void setDirectionDependant(SubgraphLayout subgraph,
			boolean isDirectionDependant) {
		subgraph.setProperty(DIRECTION_DEPENDANT_PROPERTY, isDirectionDependant);
	}

	// TODO: ensure valid direction by using an enum
	public static void setDirection(SubgraphLayout subgraph, int direction) {
		subgraph.setProperty(DIRECTION_PROPERTY, direction);
	}

	public static void setIsGraphEntity(SubgraphLayout subgraph,
			boolean isGraphEntity) {
		subgraph.setProperty(IS_GRAPH_ENTITY_PROPERTY, isGraphEntity);
	}

	public static Boolean isDirectionDependant(SubgraphLayout subgraph) {
		Object directionDependant = subgraph
				.getProperty(DIRECTION_DEPENDANT_PROPERTY);
		if (directionDependant instanceof Boolean) {
			return (Boolean) directionDependant;
		}
		return DEFAULT_DIRECTION_DEPENDANT;
	}

	public static Boolean isGraphEntity(SubgraphLayout subgraph) {
		Object isGraphEntity = subgraph.getProperty(IS_GRAPH_ENTITY_PROPERTY);
		if (isGraphEntity instanceof Boolean) {
			return (Boolean) isGraphEntity;
		}
		return DEFAULT_IS_GRAPH_ENTITY;
	}

	public static Integer getDirection(SubgraphLayout subgraph) {
		Object direction = subgraph.getProperty(DIRECTION_PROPERTY);
		if (direction instanceof Integer) {
			return (Integer) direction;
		}
		return DEFAULT_DIRECTION;
	}

}
